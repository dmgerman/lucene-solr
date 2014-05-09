begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
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
comment|/**  * {@link DocumentsWriterPerThreadPool} controls {@link ThreadState} instances  * and their thread assignments during indexing. Each {@link ThreadState} holds  * a reference to a {@link DocumentsWriterPerThread} that is once a  * {@link ThreadState} is obtained from the pool exclusively used for indexing a  * single document by the obtaining thread. Each indexing thread must obtain  * such a {@link ThreadState} to make progress. Depending on the  * {@link DocumentsWriterPerThreadPool} implementation {@link ThreadState}  * assignments might differ from document to document.  *<p>  * Once a {@link DocumentsWriterPerThread} is selected for flush the thread pool  * is reusing the flushing {@link DocumentsWriterPerThread}s ThreadState with a  * new {@link DocumentsWriterPerThread} instance.  *</p>  */
end_comment

begin_class
DECL|class|DocumentsWriterPerThreadPool
specifier|final
class|class
name|DocumentsWriterPerThreadPool
implements|implements
name|Cloneable
block|{
comment|/**    * {@link ThreadState} references and guards a    * {@link DocumentsWriterPerThread} instance that is used during indexing to    * build a in-memory index segment. {@link ThreadState} also holds all flush    * related per-thread data controlled by {@link DocumentsWriterFlushControl}.    *<p>    * A {@link ThreadState}, its methods and members should only accessed by one    * thread a time. Users must acquire the lock via {@link ThreadState#lock()}    * and release the lock in a finally block via {@link ThreadState#unlock()}    * before accessing the state.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|ThreadState
specifier|final
specifier|static
class|class
name|ThreadState
extends|extends
name|ReentrantLock
block|{
DECL|field|dwpt
name|DocumentsWriterPerThread
name|dwpt
decl_stmt|;
comment|// TODO this should really be part of DocumentsWriterFlushControl
comment|// write access guarded by DocumentsWriterFlushControl
DECL|field|flushPending
specifier|volatile
name|boolean
name|flushPending
init|=
literal|false
decl_stmt|;
comment|// TODO this should really be part of DocumentsWriterFlushControl
comment|// write access guarded by DocumentsWriterFlushControl
DECL|field|bytesUsed
name|long
name|bytesUsed
init|=
literal|0
decl_stmt|;
comment|// guarded by Reentrant lock
DECL|field|isActive
specifier|private
name|boolean
name|isActive
init|=
literal|true
decl_stmt|;
DECL|method|ThreadState
name|ThreadState
parameter_list|(
name|DocumentsWriterPerThread
name|dpwt
parameter_list|)
block|{
name|this
operator|.
name|dwpt
operator|=
name|dpwt
expr_stmt|;
block|}
comment|/**      * Resets the internal {@link DocumentsWriterPerThread} with the given one.       * if the given DWPT is<code>null</code> this ThreadState is marked as inactive and should not be used      * for indexing anymore.      * @see #isActive()        */
DECL|method|deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
name|isActive
operator|=
literal|false
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
specifier|private
name|void
name|reset
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
name|this
operator|.
name|dwpt
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|flushPending
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Returns<code>true</code> if this ThreadState is still open. This will      * only return<code>false</code> iff the DW has been closed and this      * ThreadState is already checked out for flush.      */
DECL|method|isActive
name|boolean
name|isActive
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
return|return
name|isActive
return|;
block|}
DECL|method|isInitialized
name|boolean
name|isInitialized
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
return|return
name|isActive
argument_list|()
operator|&&
name|dwpt
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the number of currently active bytes in this ThreadState's      * {@link DocumentsWriterPerThread}      */
DECL|method|getBytesUsedPerThread
specifier|public
name|long
name|getBytesUsedPerThread
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
comment|// public for FlushPolicy
return|return
name|bytesUsed
return|;
block|}
comment|/**      * Returns this {@link ThreadState}s {@link DocumentsWriterPerThread}      */
DECL|method|getDocumentsWriterPerThread
specifier|public
name|DocumentsWriterPerThread
name|getDocumentsWriterPerThread
parameter_list|()
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
comment|// public for FlushPolicy
return|return
name|dwpt
return|;
block|}
comment|/**      * Returns<code>true</code> iff this {@link ThreadState} is marked as flush      * pending otherwise<code>false</code>      */
DECL|method|isFlushPending
specifier|public
name|boolean
name|isFlushPending
parameter_list|()
block|{
return|return
name|flushPending
return|;
block|}
block|}
DECL|field|threadStates
specifier|private
specifier|final
name|ThreadState
index|[]
name|threadStates
decl_stmt|;
DECL|field|numThreadStatesActive
specifier|private
specifier|volatile
name|int
name|numThreadStatesActive
decl_stmt|;
DECL|field|freeList
specifier|private
specifier|final
name|ThreadState
index|[]
name|freeList
decl_stmt|;
DECL|field|freeCount
specifier|private
name|int
name|freeCount
decl_stmt|;
comment|/**    * Creates a new {@link DocumentsWriterPerThreadPool} with a given maximum of {@link ThreadState}s.    */
DECL|method|DocumentsWriterPerThreadPool
name|DocumentsWriterPerThreadPool
parameter_list|(
name|int
name|maxNumThreadStates
parameter_list|)
block|{
if|if
condition|(
name|maxNumThreadStates
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxNumThreadStates must be>= 1 but was: "
operator|+
name|maxNumThreadStates
argument_list|)
throw|;
block|}
name|threadStates
operator|=
operator|new
name|ThreadState
index|[
name|maxNumThreadStates
index|]
expr_stmt|;
name|numThreadStatesActive
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threadStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threadStates
index|[
name|i
index|]
operator|=
operator|new
name|ThreadState
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|freeList
operator|=
operator|new
name|ThreadState
index|[
name|maxNumThreadStates
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|DocumentsWriterPerThreadPool
name|clone
parameter_list|()
block|{
comment|// We should only be cloned before being used:
if|if
condition|(
name|numThreadStatesActive
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"clone this object before it is used!"
argument_list|)
throw|;
block|}
return|return
operator|new
name|DocumentsWriterPerThreadPool
argument_list|(
name|threadStates
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * Returns the max number of {@link ThreadState} instances available in this    * {@link DocumentsWriterPerThreadPool}    */
DECL|method|getMaxThreadStates
name|int
name|getMaxThreadStates
parameter_list|()
block|{
return|return
name|threadStates
operator|.
name|length
return|;
block|}
comment|/**    * Returns the active number of {@link ThreadState} instances.    */
DECL|method|getActiveThreadState
name|int
name|getActiveThreadState
parameter_list|()
block|{
return|return
name|numThreadStatesActive
return|;
block|}
comment|/**    * Returns a new {@link ThreadState} iff any new state is available otherwise    *<code>null</code>.    *<p>    * NOTE: the returned {@link ThreadState} is already locked iff non-    *<code>null</code>.    *     * @return a new {@link ThreadState} iff any new state is available otherwise    *<code>null</code>    */
DECL|method|newThreadState
specifier|private
name|ThreadState
name|newThreadState
parameter_list|()
block|{
assert|assert
name|numThreadStatesActive
operator|<
name|threadStates
operator|.
name|length
assert|;
specifier|final
name|ThreadState
name|threadState
init|=
name|threadStates
index|[
name|numThreadStatesActive
index|]
decl_stmt|;
name|threadState
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// lock so nobody else will get this ThreadState
name|boolean
name|unlock
init|=
literal|true
decl_stmt|;
try|try
block|{
if|if
condition|(
name|threadState
operator|.
name|isActive
argument_list|()
condition|)
block|{
comment|// unreleased thread states are deactivated during DW#close()
name|numThreadStatesActive
operator|++
expr_stmt|;
comment|// increment will publish the ThreadState
comment|//System.out.println("activeCount=" + numThreadStatesActive);
assert|assert
name|threadState
operator|.
name|dwpt
operator|==
literal|null
assert|;
name|unlock
operator|=
literal|false
expr_stmt|;
return|return
name|threadState
return|;
block|}
comment|// we are closed: unlock since the threadstate is not active anymore
assert|assert
name|assertUnreleasedThreadStatesInactive
argument_list|()
assert|;
return|return
literal|null
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|unlock
condition|)
block|{
comment|// in any case make sure we unlock if we fail
name|threadState
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|assertUnreleasedThreadStatesInactive
specifier|private
specifier|synchronized
name|boolean
name|assertUnreleasedThreadStatesInactive
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|numThreadStatesActive
init|;
name|i
operator|<
name|threadStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|threadStates
index|[
name|i
index|]
operator|.
name|tryLock
argument_list|()
operator|:
literal|"unreleased threadstate should not be locked"
assert|;
try|try
block|{
assert|assert
operator|!
name|threadStates
index|[
name|i
index|]
operator|.
name|isInitialized
argument_list|()
operator|:
literal|"expected unreleased thread state to be inactive"
assert|;
block|}
finally|finally
block|{
name|threadStates
index|[
name|i
index|]
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Deactivate all unreleased threadstates     */
DECL|method|deactivateUnreleasedStates
specifier|synchronized
name|void
name|deactivateUnreleasedStates
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|numThreadStatesActive
init|;
name|i
operator|<
name|threadStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ThreadState
name|threadState
init|=
name|threadStates
index|[
name|i
index|]
decl_stmt|;
name|threadState
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|threadState
operator|.
name|deactivate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|threadState
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|// In case any threads are waiting for indexing:
name|notifyAll
argument_list|()
expr_stmt|;
block|}
DECL|method|reset
name|DocumentsWriterPerThread
name|reset
parameter_list|(
name|ThreadState
name|threadState
parameter_list|,
name|boolean
name|closed
parameter_list|)
block|{
assert|assert
name|threadState
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
specifier|final
name|DocumentsWriterPerThread
name|dwpt
init|=
name|threadState
operator|.
name|dwpt
decl_stmt|;
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|threadState
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|threadState
operator|.
name|deactivate
argument_list|()
expr_stmt|;
block|}
return|return
name|dwpt
return|;
block|}
DECL|method|recycle
name|void
name|recycle
parameter_list|(
name|DocumentsWriterPerThread
name|dwpt
parameter_list|)
block|{
comment|// don't recycle DWPT by default
block|}
comment|/** This method is used by DocumentsWriter/FlushControl to obtain a ThreadState to do an indexing operation (add/updateDocument). */
DECL|method|getAndLock
name|ThreadState
name|getAndLock
parameter_list|(
name|Thread
name|requestingThread
parameter_list|,
name|DocumentsWriter
name|documentsWriter
parameter_list|)
block|{
name|ThreadState
name|threadState
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|freeCount
operator|>
literal|0
condition|)
block|{
comment|// Important that we are LIFO here! This way if number of concurrent indexing threads was once high, but has now reduced, we only use a
comment|// limited number of thread states:
name|threadState
operator|=
name|freeList
index|[
name|freeCount
operator|-
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|threadState
operator|.
name|dwpt
operator|==
literal|null
condition|)
block|{
comment|// This thread-state is not initialized, e.g. it
comment|// was just flushed. See if we can instead find
comment|// another free thread state that already has docs
comment|// indexed. This way if incoming thread concurrency
comment|// has decreased, we don't leave docs
comment|// indefinitely buffered, tying up RAM.  This
comment|// will instead get those thread states flushed,
comment|// freeing up RAM for larger segment flushes:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|freeCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|freeList
index|[
name|i
index|]
operator|.
name|dwpt
operator|!=
literal|null
condition|)
block|{
comment|// Use this one instead, and swap it with
comment|// the un-initialized one:
name|ThreadState
name|ts
init|=
name|freeList
index|[
name|i
index|]
decl_stmt|;
name|freeList
index|[
name|i
index|]
operator|=
name|threadState
expr_stmt|;
name|threadState
operator|=
name|ts
expr_stmt|;
break|break;
block|}
block|}
block|}
name|freeCount
operator|--
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|numThreadStatesActive
operator|<
name|threadStates
operator|.
name|length
condition|)
block|{
comment|// ThreadState is already locked before return by this method:
return|return
name|newThreadState
argument_list|()
return|;
block|}
else|else
block|{
comment|// Wait until a thread state frees up:
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|ThreadInterruptedException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// This could take time, e.g. if the threadState is [briefly] checked for flushing:
name|threadState
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|threadState
return|;
block|}
DECL|method|release
name|void
name|release
parameter_list|(
name|ThreadState
name|state
parameter_list|)
block|{
name|state
operator|.
name|unlock
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
assert|assert
name|freeCount
operator|<
name|freeList
operator|.
name|length
assert|;
name|freeList
index|[
name|freeCount
operator|++
index|]
operator|=
name|state
expr_stmt|;
comment|// In case any thread is waiting, wake one of them up since we just released a thread state; notify() should be sufficient but we do
comment|// notifyAll defensively:
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the<i>i</i>th active {@link ThreadState} where<i>i</i> is the    * given ord.    *     * @param ord    *          the ordinal of the {@link ThreadState}    * @return the<i>i</i>th active {@link ThreadState} where<i>i</i> is the    *         given ord.    */
DECL|method|getThreadState
name|ThreadState
name|getThreadState
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
return|return
name|threadStates
index|[
name|ord
index|]
return|;
block|}
comment|/**    * Returns the ThreadState with the minimum estimated number of threads    * waiting to acquire its lock or<code>null</code> if no {@link ThreadState}    * is yet visible to the calling thread.    */
DECL|method|minContendedThreadState
name|ThreadState
name|minContendedThreadState
parameter_list|()
block|{
name|ThreadState
name|minThreadState
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|limit
init|=
name|numThreadStatesActive
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ThreadState
name|state
init|=
name|threadStates
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|minThreadState
operator|==
literal|null
operator|||
name|state
operator|.
name|getQueueLength
argument_list|()
operator|<
name|minThreadState
operator|.
name|getQueueLength
argument_list|()
condition|)
block|{
name|minThreadState
operator|=
name|state
expr_stmt|;
block|}
block|}
return|return
name|minThreadState
return|;
block|}
comment|/**    * Returns the number of currently deactivated {@link ThreadState} instances.    * A deactivated {@link ThreadState} should not be used for indexing anymore.    *     * @return the number of currently deactivated {@link ThreadState} instances.    */
DECL|method|numDeactivatedThreadStates
name|int
name|numDeactivatedThreadStates
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
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
name|threadStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ThreadState
name|threadState
init|=
name|threadStates
index|[
name|i
index|]
decl_stmt|;
name|threadState
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|threadState
operator|.
name|isActive
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|threadState
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**    * Deactivates an active {@link ThreadState}. Inactive {@link ThreadState} can    * not be used for indexing anymore once they are deactivated. This method should only be used    * if the parent {@link DocumentsWriter} is closed or aborted.    *     * @param threadState the state to deactivate    */
DECL|method|deactivateThreadState
name|void
name|deactivateThreadState
parameter_list|(
name|ThreadState
name|threadState
parameter_list|)
block|{
assert|assert
name|threadState
operator|.
name|isActive
argument_list|()
assert|;
name|threadState
operator|.
name|deactivate
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

