begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|KeeperException
operator|.
name|NodeExistsException
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

begin_class
DECL|class|ZkCmdExecutor
specifier|public
class|class
name|ZkCmdExecutor
block|{
DECL|field|retryDelay
specifier|private
name|long
name|retryDelay
init|=
literal|1300L
decl_stmt|;
comment|// 300 ms over for padding
DECL|field|retryCount
specifier|private
name|int
name|retryCount
decl_stmt|;
DECL|field|acl
specifier|private
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
init|=
name|ZooDefs
operator|.
name|Ids
operator|.
name|OPEN_ACL_UNSAFE
decl_stmt|;
DECL|method|ZkCmdExecutor
specifier|public
name|ZkCmdExecutor
parameter_list|(
name|int
name|timeoutms
parameter_list|)
block|{
name|double
name|timeouts
init|=
name|timeoutms
operator|/
literal|1000.0
decl_stmt|;
name|this
operator|.
name|retryCount
operator|=
name|Math
operator|.
name|round
argument_list|(
literal|0.5f
operator|*
operator|(
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
literal|8.0f
operator|*
name|timeouts
operator|+
literal|1.0f
argument_list|)
operator|-
literal|1.0f
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getAcl
specifier|public
name|List
argument_list|<
name|ACL
argument_list|>
name|getAcl
parameter_list|()
block|{
return|return
name|acl
return|;
block|}
DECL|method|setAcl
specifier|public
name|void
name|setAcl
parameter_list|(
name|List
argument_list|<
name|ACL
argument_list|>
name|acl
parameter_list|)
block|{
name|this
operator|.
name|acl
operator|=
name|acl
expr_stmt|;
block|}
DECL|method|getRetryDelay
specifier|public
name|long
name|getRetryDelay
parameter_list|()
block|{
return|return
name|retryDelay
return|;
block|}
DECL|method|setRetryDelay
specifier|public
name|void
name|setRetryDelay
parameter_list|(
name|long
name|retryDelay
parameter_list|)
block|{
name|this
operator|.
name|retryDelay
operator|=
name|retryDelay
expr_stmt|;
block|}
comment|/**    * Perform the given operation, retrying if the connection fails    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|retryOperation
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|retryOperation
parameter_list|(
name|ZkOperation
name|operation
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|KeeperException
name|exception
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|retryCount
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
return|return
operator|(
name|T
operator|)
name|operation
operator|.
name|execute
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|ConnectionLossException
name|e
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
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
name|InterruptedException
argument_list|()
throw|;
block|}
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|instanceof
name|ClosableThread
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|ClosableThread
operator|)
name|Thread
operator|.
name|currentThread
argument_list|()
operator|)
operator|.
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
name|retryDelay
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
name|exception
throw|;
block|}
DECL|method|ensureExists
specifier|public
name|void
name|ensureExists
parameter_list|(
name|String
name|path
parameter_list|,
specifier|final
name|SolrZkClient
name|zkClient
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ensureExists
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|zkClient
argument_list|)
expr_stmt|;
block|}
DECL|method|ensureExists
specifier|public
name|void
name|ensureExists
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
name|CreateMode
name|createMode
parameter_list|,
specifier|final
name|SolrZkClient
name|zkClient
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|path
argument_list|,
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{
comment|// its okay if another beats us creating the node
block|}
block|}
comment|/**    * Performs a retry delay if this is not the first attempt    *     * @param attemptCount    *          the number of the attempts performed so far    */
DECL|method|retryDelay
specifier|protected
name|void
name|retryDelay
parameter_list|(
name|int
name|attemptCount
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|attemptCount
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|attemptCount
operator|*
name|retryDelay
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

