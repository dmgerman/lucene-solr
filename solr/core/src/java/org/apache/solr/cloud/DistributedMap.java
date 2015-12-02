begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
operator|.
name|ErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|SolrZkClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkCmdExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|WatchedEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|Watcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A distributed map.  * This supports basic map functions e.g. get, put, contains for interaction with zk which  * don't have to be ordered i.e. DistributedQueue.  */
end_comment

begin_class
DECL|class|DistributedMap
specifier|public
class|class
name|DistributedMap
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_TIMEOUT
specifier|protected
specifier|static
name|long
name|DEFAULT_TIMEOUT
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|dir
specifier|protected
specifier|final
name|String
name|dir
decl_stmt|;
DECL|field|zookeeper
specifier|protected
name|SolrZkClient
name|zookeeper
decl_stmt|;
DECL|field|prefix
specifier|protected
specifier|final
name|String
name|prefix
init|=
literal|"mn-"
decl_stmt|;
DECL|field|response_prefix
specifier|protected
specifier|final
name|String
name|response_prefix
init|=
literal|"mnr-"
decl_stmt|;
DECL|method|DistributedMap
specifier|public
name|DistributedMap
parameter_list|(
name|SolrZkClient
name|zookeeper
parameter_list|,
name|String
name|dir
parameter_list|,
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|ZkCmdExecutor
name|cmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|(
name|zookeeper
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|cmdExecutor
operator|.
name|ensureExists
argument_list|(
name|dir
argument_list|,
name|zookeeper
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|zookeeper
operator|=
name|zookeeper
expr_stmt|;
block|}
DECL|class|LatchChildWatcher
specifier|protected
class|class
name|LatchChildWatcher
implements|implements
name|Watcher
block|{
DECL|field|lock
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|event
specifier|private
name|WatchedEvent
name|event
init|=
literal|null
decl_stmt|;
DECL|method|LatchChildWatcher
specifier|public
name|LatchChildWatcher
parameter_list|()
block|{}
DECL|method|LatchChildWatcher
specifier|public
name|LatchChildWatcher
parameter_list|(
name|Object
name|lock
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"LatchChildWatcher fired on path: "
operator|+
name|event
operator|.
name|getPath
argument_list|()
operator|+
literal|" state: "
operator|+
name|event
operator|.
name|getState
argument_list|()
operator|+
literal|" type "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|await
specifier|public
name|void
name|await
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|lock
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getWatchedEvent
specifier|public
name|WatchedEvent
name|getWatchedEvent
parameter_list|()
block|{
return|return
name|event
return|;
block|}
block|}
comment|/**    * Inserts data into zookeeper.    *    * @return true if data was successfully added    */
DECL|method|createData
specifier|protected
name|String
name|createData
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|CreateMode
name|mode
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
return|return
name|zookeeper
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|data
argument_list|,
name|mode
argument_list|,
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
try|try
block|{
name|zookeeper
operator|.
name|create
argument_list|(
name|dir
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|ne
parameter_list|)
block|{
comment|// someone created it
block|}
block|}
block|}
block|}
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|String
name|trackingId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|createData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|prefix
operator|+
name|trackingId
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Offer the data and wait for the response    *    */
DECL|method|put
specifier|public
name|MapEvent
name|put
parameter_list|(
name|String
name|trackingId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|String
name|path
init|=
name|createData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|prefix
operator|+
name|trackingId
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
decl_stmt|;
name|String
name|watchID
init|=
name|createData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|response_prefix
operator|+
name|path
operator|.
name|substring
argument_list|(
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"-"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|)
decl_stmt|;
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|LatchChildWatcher
name|watcher
init|=
operator|new
name|LatchChildWatcher
argument_list|(
name|lock
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|zookeeper
operator|.
name|exists
argument_list|(
name|watchID
argument_list|,
name|watcher
argument_list|,
literal|true
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|watcher
operator|.
name|await
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|bytes
init|=
name|zookeeper
operator|.
name|getData
argument_list|(
name|watchID
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|zookeeper
operator|.
name|delete
argument_list|(
name|watchID
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|MapEvent
argument_list|(
name|watchID
argument_list|,
name|bytes
argument_list|,
name|watcher
operator|.
name|getWatchedEvent
argument_list|()
argument_list|)
return|;
block|}
DECL|method|get
specifier|public
name|MapEvent
name|get
parameter_list|(
name|String
name|trackingId
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
operator|new
name|MapEvent
argument_list|(
name|trackingId
argument_list|,
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|prefix
operator|+
name|trackingId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|String
name|trackingId
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|zookeeper
operator|.
name|exists
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|prefix
operator|+
name|trackingId
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|stat
operator|.
name|getNumChildren
argument_list|()
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|trackingId
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|zookeeper
operator|.
name|delete
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|prefix
operator|+
name|trackingId
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper method to clear all child nodes for a parent node.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|zookeeper
operator|.
name|getChildren
argument_list|(
name|dir
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|childNames
control|)
block|{
name|zookeeper
operator|.
name|delete
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|childName
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MapEvent
specifier|public
specifier|static
class|class
name|MapEvent
block|{
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|id
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|id
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MapEvent
name|other
init|=
operator|(
name|MapEvent
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|id
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|id
operator|.
name|equals
argument_list|(
name|other
operator|.
name|id
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|field|event
specifier|private
name|WatchedEvent
name|event
init|=
literal|null
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|method|MapEvent
name|MapEvent
parameter_list|(
name|String
name|id
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|WatchedEvent
name|event
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
block|}
DECL|method|setId
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setBytes
specifier|public
name|void
name|setBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
DECL|method|getWatchedEvent
specifier|public
name|WatchedEvent
name|getWatchedEvent
parameter_list|()
block|{
return|return
name|event
return|;
block|}
block|}
block|}
end_class

end_unit

