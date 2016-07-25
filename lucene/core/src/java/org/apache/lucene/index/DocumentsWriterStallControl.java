begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ThreadInterruptedException
import|;
end_import

begin_comment
comment|/**  * Controls the health status of a {@link DocumentsWriter} sessions. This class  * used to block incoming indexing threads if flushing significantly slower than  * indexing to ensure the {@link DocumentsWriter}s healthiness. If flushing is  * significantly slower than indexing the net memory used within an  * {@link IndexWriter} session can increase very quickly and easily exceed the  * JVM's available memory.  *<p>  * To prevent OOM Errors and ensure IndexWriter's stability this class blocks  * incoming threads from indexing once 2 x number of available  * {@link ThreadState}s in {@link DocumentsWriterPerThreadPool} is exceeded.  * Once flushing catches up and the number of flushing DWPT is equal or lower  * than the number of active {@link ThreadState}s threads are released and can  * continue indexing.  */
end_comment

begin_class
DECL|class|DocumentsWriterStallControl
specifier|final
class|class
name|DocumentsWriterStallControl
block|{
DECL|field|stalled
specifier|private
specifier|volatile
name|boolean
name|stalled
decl_stmt|;
DECL|field|numWaiting
specifier|private
name|int
name|numWaiting
decl_stmt|;
comment|// only with assert
DECL|field|wasStalled
specifier|private
name|boolean
name|wasStalled
decl_stmt|;
comment|// only with assert
DECL|field|waiting
specifier|private
specifier|final
name|Map
argument_list|<
name|Thread
argument_list|,
name|Boolean
argument_list|>
name|waiting
init|=
operator|new
name|IdentityHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// only with assert
comment|/**    * Update the stalled flag status. This method will set the stalled flag to    *<code>true</code> iff the number of flushing    * {@link DocumentsWriterPerThread} is greater than the number of active    * {@link DocumentsWriterPerThread}. Otherwise it will reset the    * {@link DocumentsWriterStallControl} to healthy and release all threads    * waiting on {@link #waitIfStalled()}    */
DECL|method|updateStalled
specifier|synchronized
name|void
name|updateStalled
parameter_list|(
name|boolean
name|stalled
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|stalled
operator|!=
name|stalled
condition|)
block|{
name|this
operator|.
name|stalled
operator|=
name|stalled
expr_stmt|;
if|if
condition|(
name|stalled
condition|)
block|{
name|wasStalled
operator|=
literal|true
expr_stmt|;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Blocks if documents writing is currently in a stalled state.     *     */
DECL|method|waitIfStalled
name|void
name|waitIfStalled
parameter_list|()
block|{
if|if
condition|(
name|stalled
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|stalled
condition|)
block|{
comment|// react on the first wakeup call!
comment|// don't loop here, higher level logic will re-stall!
try|try
block|{
name|incWaiters
argument_list|()
expr_stmt|;
comment|// Defensive, in case we have a concurrency bug that fails to .notify/All our thread:
comment|// just wait for up to 1 second here, and let caller re-stall if it's still needed:
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|decrWaiters
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
DECL|method|anyStalledThreads
name|boolean
name|anyStalledThreads
parameter_list|()
block|{
return|return
name|stalled
return|;
block|}
DECL|method|incWaiters
specifier|private
name|void
name|incWaiters
parameter_list|()
block|{
name|numWaiting
operator|++
expr_stmt|;
assert|assert
name|waiting
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|==
literal|null
assert|;
assert|assert
name|numWaiting
operator|>
literal|0
assert|;
block|}
DECL|method|decrWaiters
specifier|private
name|void
name|decrWaiters
parameter_list|()
block|{
name|numWaiting
operator|--
expr_stmt|;
assert|assert
name|waiting
operator|.
name|remove
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
operator|!=
literal|null
assert|;
assert|assert
name|numWaiting
operator|>=
literal|0
assert|;
block|}
DECL|method|hasBlocked
specifier|synchronized
name|boolean
name|hasBlocked
parameter_list|()
block|{
comment|// for tests
return|return
name|numWaiting
operator|>
literal|0
return|;
block|}
DECL|method|isHealthy
name|boolean
name|isHealthy
parameter_list|()
block|{
comment|// for tests
return|return
operator|!
name|stalled
return|;
comment|// volatile read!
block|}
DECL|method|isThreadQueued
specifier|synchronized
name|boolean
name|isThreadQueued
parameter_list|(
name|Thread
name|t
parameter_list|)
block|{
comment|// for tests
return|return
name|waiting
operator|.
name|containsKey
argument_list|(
name|t
argument_list|)
return|;
block|}
DECL|method|wasStalled
specifier|synchronized
name|boolean
name|wasStalled
parameter_list|()
block|{
comment|// for tests
return|return
name|wasStalled
return|;
block|}
block|}
end_class

end_unit

