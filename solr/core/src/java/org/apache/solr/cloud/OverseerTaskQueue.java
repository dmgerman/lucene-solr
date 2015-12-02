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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|ZkNodeProps
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
name|util
operator|.
name|stats
operator|.
name|TimerContext
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

begin_comment
comment|/**  * A {@link DistributedQueue} augmented with helper methods specific to the overseer task queues.  * Methods specific to this subclass ignore superclass internal state and hit ZK directly.  * This is inefficient!  But the API on this class is kind of muddy..  */
end_comment

begin_class
DECL|class|OverseerTaskQueue
specifier|public
class|class
name|OverseerTaskQueue
extends|extends
name|DistributedQueue
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
DECL|field|response_prefix
specifier|private
specifier|final
name|String
name|response_prefix
init|=
literal|"qnr-"
decl_stmt|;
DECL|method|OverseerTaskQueue
specifier|public
name|OverseerTaskQueue
parameter_list|(
name|SolrZkClient
name|zookeeper
parameter_list|,
name|String
name|dir
parameter_list|)
block|{
name|this
argument_list|(
name|zookeeper
argument_list|,
name|dir
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|OverseerTaskQueue
specifier|public
name|OverseerTaskQueue
parameter_list|(
name|SolrZkClient
name|zookeeper
parameter_list|,
name|String
name|dir
parameter_list|,
name|Overseer
operator|.
name|Stats
name|stats
parameter_list|)
block|{
name|super
argument_list|(
name|zookeeper
argument_list|,
name|dir
argument_list|,
name|stats
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true if the queue contains a task with the specified async id.    */
DECL|method|containsTaskWithRequestId
specifier|public
name|boolean
name|containsTaskWithRequestId
parameter_list|(
name|String
name|requestIdKey
parameter_list|,
name|String
name|requestId
parameter_list|)
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
name|stats
operator|.
name|setQueueLength
argument_list|(
name|childNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|childNames
control|)
block|{
if|if
condition|(
name|childName
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|byte
index|[]
name|data
init|=
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|childName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|ZkNodeProps
name|message
init|=
name|ZkNodeProps
operator|.
name|load
argument_list|(
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|.
name|containsKey
argument_list|(
name|requestIdKey
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> {}"
argument_list|,
name|message
operator|.
name|get
argument_list|(
name|requestIdKey
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|get
argument_list|(
name|requestIdKey
argument_list|)
operator|.
name|equals
argument_list|(
name|requestId
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// Another client removed the node first, try next
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Remove the event and save the response into the other path.    *     */
DECL|method|remove
specifier|public
name|byte
index|[]
name|remove
parameter_list|(
name|QueueEvent
name|event
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|TimerContext
name|time
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_remove_event"
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|path
init|=
name|event
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|responsePath
init|=
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
decl_stmt|;
if|if
condition|(
name|zookeeper
operator|.
name|exists
argument_list|(
name|responsePath
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|zookeeper
operator|.
name|setData
argument_list|(
name|responsePath
argument_list|,
name|event
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Response ZK path: "
operator|+
name|responsePath
operator|+
literal|" doesn't exist."
operator|+
literal|"  Requestor may have disconnected from ZooKeeper"
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|data
init|=
name|zookeeper
operator|.
name|getData
argument_list|(
name|path
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
name|path
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Watcher that blocks until a WatchedEvent occurs for a znode.    */
DECL|class|LatchWatcher
specifier|private
specifier|final
class|class
name|LatchWatcher
implements|implements
name|Watcher
block|{
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
decl_stmt|;
DECL|field|event
specifier|private
name|WatchedEvent
name|event
decl_stmt|;
DECL|field|latchEventType
specifier|private
name|Event
operator|.
name|EventType
name|latchEventType
decl_stmt|;
DECL|method|LatchWatcher
name|LatchWatcher
parameter_list|(
name|Object
name|lock
parameter_list|)
block|{
name|this
argument_list|(
name|lock
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|LatchWatcher
name|LatchWatcher
parameter_list|(
name|Event
operator|.
name|EventType
name|eventType
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|,
name|eventType
argument_list|)
expr_stmt|;
block|}
DECL|method|LatchWatcher
name|LatchWatcher
parameter_list|(
name|Object
name|lock
parameter_list|,
name|Event
operator|.
name|EventType
name|eventType
parameter_list|)
block|{
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
name|this
operator|.
name|latchEventType
operator|=
name|eventType
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
name|Event
operator|.
name|EventType
name|eventType
init|=
name|event
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// None events are ignored
comment|// If latchEventType is not null, only fire if the type matches
name|LOG
operator|.
name|info
argument_list|(
literal|"{} fired on path {} state {} latchEventType {}"
argument_list|,
name|eventType
argument_list|,
name|event
operator|.
name|getPath
argument_list|()
argument_list|,
name|event
operator|.
name|getState
argument_list|()
argument_list|,
name|latchEventType
argument_list|)
expr_stmt|;
if|if
condition|(
name|eventType
operator|!=
name|Event
operator|.
name|EventType
operator|.
name|None
operator|&&
operator|(
name|latchEventType
operator|==
literal|null
operator|||
name|eventType
operator|==
name|latchEventType
operator|)
condition|)
block|{
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
if|if
condition|(
name|this
operator|.
name|event
operator|!=
literal|null
condition|)
return|return;
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
comment|/**    * Inserts data into zookeeper.    *     * @return true if data was successfully added    */
DECL|method|createData
specifier|private
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
comment|/**    * Offer the data and wait for the response    *     */
DECL|method|offer
specifier|public
name|QueueEvent
name|offer
parameter_list|(
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
name|TimerContext
name|time
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_offer"
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Create and watch the response node before creating the request node;
comment|// otherwise we may miss the response.
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
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL_SEQUENTIAL
argument_list|)
decl_stmt|;
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|LatchWatcher
name|watcher
init|=
operator|new
name|LatchWatcher
argument_list|(
name|lock
argument_list|)
decl_stmt|;
name|Stat
name|stat
init|=
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
decl_stmt|;
comment|// create the request node
name|createData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|PREFIX
operator|+
name|watchID
operator|.
name|substring
argument_list|(
name|watchID
operator|.
name|lastIndexOf
argument_list|(
literal|"-"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
if|if
condition|(
name|stat
operator|!=
literal|null
operator|&&
name|watcher
operator|.
name|getWatchedEvent
argument_list|()
operator|==
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
comment|// create the event before deleting the node, otherwise we can get the deleted
comment|// event from the watcher.
name|QueueEvent
name|event
init|=
operator|new
name|QueueEvent
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
name|event
return|;
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|peekTopN
specifier|public
name|List
argument_list|<
name|QueueEvent
argument_list|>
name|peekTopN
parameter_list|(
name|int
name|n
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|excludeSet
parameter_list|,
name|long
name|waitMillis
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ArrayList
argument_list|<
name|QueueEvent
argument_list|>
name|topN
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Peeking for top {} elements. ExcludeSet: {}"
argument_list|,
name|n
argument_list|,
name|excludeSet
argument_list|)
expr_stmt|;
name|TimerContext
name|time
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|waitMillis
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
name|time
operator|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_peekTopN_wait_forever"
argument_list|)
expr_stmt|;
else|else
name|time
operator|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_peekTopN_wait"
operator|+
name|waitMillis
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|String
name|headNode
range|:
name|getChildren
argument_list|(
name|waitMillis
argument_list|)
control|)
block|{
if|if
condition|(
name|topN
operator|.
name|size
argument_list|()
operator|<
name|n
condition|)
block|{
try|try
block|{
name|String
name|id
init|=
name|dir
operator|+
literal|"/"
operator|+
name|headNode
decl_stmt|;
if|if
condition|(
name|excludeSet
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
continue|continue;
name|QueueEvent
name|queueEvent
init|=
operator|new
name|QueueEvent
argument_list|(
name|id
argument_list|,
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|headNode
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
decl_stmt|;
name|topN
operator|.
name|add
argument_list|(
name|queueEvent
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// Another client removed the node first, try next
block|}
block|}
else|else
block|{
if|if
condition|(
name|topN
operator|.
name|size
argument_list|()
operator|>=
literal|1
condition|)
block|{
name|printQueueEventsListElementIds
argument_list|(
name|topN
argument_list|)
expr_stmt|;
return|return
name|topN
return|;
block|}
block|}
block|}
if|if
condition|(
name|topN
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|printQueueEventsListElementIds
argument_list|(
name|topN
argument_list|)
expr_stmt|;
return|return
name|topN
return|;
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|printQueueEventsListElementIds
specifier|private
specifier|static
name|void
name|printQueueEventsListElementIds
parameter_list|(
name|ArrayList
argument_list|<
name|QueueEvent
argument_list|>
name|topN
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"["
argument_list|)
decl_stmt|;
for|for
control|(
name|QueueEvent
name|queueEvent
range|:
name|topN
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|queueEvent
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning topN elements: {}"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *    * Gets last element of the Queue without removing it.    */
DECL|method|getTailId
specifier|public
name|String
name|getTailId
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|// TODO: could we use getChildren here?  Unsure what freshness guarantee the caller needs.
name|TreeSet
argument_list|<
name|String
argument_list|>
name|orderedChildren
init|=
name|fetchZkChildren
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|headNode
range|:
name|orderedChildren
operator|.
name|descendingSet
argument_list|()
control|)
if|if
condition|(
name|headNode
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|QueueEvent
name|queueEvent
init|=
operator|new
name|QueueEvent
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|headNode
argument_list|,
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|headNode
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
decl_stmt|;
return|return
name|queueEvent
operator|.
name|getId
argument_list|()
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
comment|// Another client removed the node first, try next
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|class|QueueEvent
specifier|public
specifier|static
class|class
name|QueueEvent
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
name|QueueEvent
name|other
init|=
operator|(
name|QueueEvent
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
DECL|method|QueueEvent
name|QueueEvent
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

