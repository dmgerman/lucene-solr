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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|index
operator|.
name|FieldInfos
operator|.
name|FieldNumberBiMap
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
name|SetOnce
import|;
end_import

begin_comment
comment|/**  * {@link DocumentsWriterPerThreadPool} controls {@link ThreadState} instances  * and their thread assignments during indexing. Each {@link ThreadState} holds  * a reference to a {@link DocumentsWriterPerThread} that is once a  * {@link ThreadState} is obtained from the pool exclusively used for indexing a  * single document by the obtaining thread. Each indexing thread must obtain  * such a {@link ThreadState} to make progress. Depending on the  * {@link DocumentsWriterPerThreadPool} implementation {@link ThreadState}  * assignments might differ from document to document.  *<p>  * Once a {@link DocumentsWriterPerThread} is selected for flush the thread pool  * is reusing the flushing {@link DocumentsWriterPerThread}s ThreadState with a  * new {@link DocumentsWriterPerThread} instance.  *</p>  */
end_comment

begin_class
DECL|class|DocumentsWriterPerThreadPool
specifier|public
specifier|abstract
class|class
name|DocumentsWriterPerThreadPool
block|{
comment|/** The maximum number of simultaneous threads that may be    *  indexing documents at once in IndexWriter; if more    *  than this many threads arrive they will wait for    *  others to finish. */
DECL|field|DEFAULT_MAX_THREAD_STATES
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_MAX_THREAD_STATES
init|=
literal|8
decl_stmt|;
comment|/**    * {@link ThreadState} references and guards a    * {@link DocumentsWriterPerThread} instance that is used during indexing to    * build a in-memory index segment. {@link ThreadState} also holds all flush    * related per-thread data controlled by {@link DocumentsWriterFlushControl}.    *<p>    * A {@link ThreadState}, its methods and members should only accessed by one    * thread a time. Users must acquire the lock via {@link ThreadState#lock()}    * and release the lock in a finally block via {@link ThreadState#unlock()}    * before accessing the state.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|ThreadState
specifier|public
specifier|final
specifier|static
class|class
name|ThreadState
extends|extends
name|ReentrantLock
block|{
comment|// package private for FlushPolicy
DECL|field|perThread
name|DocumentsWriterPerThread
name|perThread
decl_stmt|;
comment|// write access guarded by DocumentsWriterFlushControl
DECL|field|flushPending
specifier|volatile
name|boolean
name|flushPending
init|=
literal|false
decl_stmt|;
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
name|perThread
parameter_list|)
block|{
name|this
operator|.
name|perThread
operator|=
name|perThread
expr_stmt|;
block|}
comment|/**      * Resets the internal {@link DocumentsWriterPerThread} with the given one.       * if the given DWPT is<code>null</code> this ThreadState is marked as inactive and should not be used      * for indexing anymore.      * @see #isActive()        */
DECL|method|resetWriter
name|void
name|resetWriter
parameter_list|(
name|DocumentsWriterPerThread
name|perThread
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
if|if
condition|(
name|perThread
operator|==
literal|null
condition|)
block|{
name|isActive
operator|=
literal|false
expr_stmt|;
block|}
name|this
operator|.
name|perThread
operator|=
name|perThread
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
name|perThread
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
DECL|field|perThreads
specifier|private
specifier|final
name|ThreadState
index|[]
name|perThreads
decl_stmt|;
DECL|field|numThreadStatesActive
specifier|private
specifier|volatile
name|int
name|numThreadStatesActive
decl_stmt|;
DECL|field|globalFieldMap
specifier|private
name|FieldNumberBiMap
name|globalFieldMap
decl_stmt|;
DECL|field|documentsWriter
specifier|private
specifier|final
name|SetOnce
argument_list|<
name|DocumentsWriter
argument_list|>
name|documentsWriter
init|=
operator|new
name|SetOnce
argument_list|<
name|DocumentsWriter
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@link DocumentsWriterPerThreadPool} with max.    * {@link #DEFAULT_MAX_THREAD_STATES} thread states.    */
DECL|method|DocumentsWriterPerThreadPool
specifier|public
name|DocumentsWriterPerThreadPool
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_MAX_THREAD_STATES
argument_list|)
expr_stmt|;
block|}
DECL|method|DocumentsWriterPerThreadPool
specifier|public
name|DocumentsWriterPerThreadPool
parameter_list|(
name|int
name|maxNumPerThreads
parameter_list|)
block|{
name|maxNumPerThreads
operator|=
operator|(
name|maxNumPerThreads
operator|<
literal|1
operator|)
condition|?
name|DEFAULT_MAX_THREAD_STATES
else|:
name|maxNumPerThreads
expr_stmt|;
name|perThreads
operator|=
operator|new
name|ThreadState
index|[
name|maxNumPerThreads
index|]
expr_stmt|;
name|numThreadStatesActive
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|(
name|DocumentsWriter
name|documentsWriter
parameter_list|,
name|FieldNumberBiMap
name|globalFieldMap
parameter_list|,
name|IndexWriterConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|documentsWriter
operator|.
name|set
argument_list|(
name|documentsWriter
argument_list|)
expr_stmt|;
comment|// thread pool is bound to DW
name|this
operator|.
name|globalFieldMap
operator|=
name|globalFieldMap
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
name|perThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FieldInfos
name|infos
init|=
operator|new
name|FieldInfos
argument_list|(
name|globalFieldMap
argument_list|)
decl_stmt|;
name|perThreads
index|[
name|i
index|]
operator|=
operator|new
name|ThreadState
argument_list|(
operator|new
name|DocumentsWriterPerThread
argument_list|(
name|documentsWriter
operator|.
name|directory
argument_list|,
name|documentsWriter
argument_list|,
name|infos
argument_list|,
name|documentsWriter
operator|.
name|chain
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns the max number of {@link ThreadState} instances available in this    * {@link DocumentsWriterPerThreadPool}    */
DECL|method|getMaxThreadStates
specifier|public
name|int
name|getMaxThreadStates
parameter_list|()
block|{
return|return
name|perThreads
operator|.
name|length
return|;
block|}
comment|/**    * Returns the active number of {@link ThreadState} instances.    */
DECL|method|getActiveThreadState
specifier|public
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
specifier|public
specifier|synchronized
name|ThreadState
name|newThreadState
parameter_list|()
block|{
if|if
condition|(
name|numThreadStatesActive
operator|<
name|perThreads
operator|.
name|length
condition|)
block|{
specifier|final
name|ThreadState
name|threadState
init|=
name|perThreads
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
assert|assert
name|threadState
operator|.
name|perThread
operator|!=
literal|null
assert|;
name|threadState
operator|.
name|perThread
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|unlock
operator|=
literal|false
expr_stmt|;
return|return
name|threadState
return|;
block|}
comment|// unlock since the threadstate is not active anymore - we are closed!
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
return|return
literal|null
return|;
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
name|perThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
assert|assert
name|perThreads
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
name|perThreads
index|[
name|i
index|]
operator|.
name|isActive
argument_list|()
operator|:
literal|"expected unreleased thread state to be inactive"
assert|;
block|}
finally|finally
block|{
name|perThreads
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
specifier|protected
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
name|perThreads
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
name|perThreads
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
name|resetWriter
argument_list|(
literal|null
argument_list|)
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
block|}
DECL|method|replaceForFlush
specifier|protected
name|DocumentsWriterPerThread
name|replaceForFlush
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
name|perThread
decl_stmt|;
if|if
condition|(
operator|!
name|closed
condition|)
block|{
specifier|final
name|FieldInfos
name|infos
init|=
operator|new
name|FieldInfos
argument_list|(
name|globalFieldMap
argument_list|)
decl_stmt|;
specifier|final
name|DocumentsWriterPerThread
name|newDwpt
init|=
operator|new
name|DocumentsWriterPerThread
argument_list|(
name|dwpt
argument_list|,
name|infos
argument_list|)
decl_stmt|;
name|newDwpt
operator|.
name|initialize
argument_list|()
expr_stmt|;
name|threadState
operator|.
name|resetWriter
argument_list|(
name|newDwpt
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|threadState
operator|.
name|resetWriter
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|dwpt
return|;
block|}
DECL|method|recycle
specifier|public
name|void
name|recycle
parameter_list|(
name|DocumentsWriterPerThread
name|dwpt
parameter_list|)
block|{
comment|// don't recycle DWPT by default
block|}
DECL|method|getAndLock
specifier|public
specifier|abstract
name|ThreadState
name|getAndLock
parameter_list|(
name|Thread
name|requestingThread
parameter_list|,
name|DocumentsWriter
name|documentsWriter
parameter_list|)
function_decl|;
comment|/**    * Returns an iterator providing access to all {@link ThreadState}    * instances.     */
comment|// TODO: new Iterator per indexed doc is overkill...?
DECL|method|getAllPerThreadsIterator
specifier|public
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|getAllPerThreadsIterator
parameter_list|()
block|{
return|return
name|getPerThreadsIterator
argument_list|(
name|this
operator|.
name|perThreads
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * Returns an iterator providing access to all active {@link ThreadState}    * instances.     *<p>    * Note: The returned iterator will only iterator    * {@link ThreadState}s that are active at the point in time when this method    * has been called.    *     */
comment|// TODO: new Iterator per indexed doc is overkill...?
DECL|method|getActivePerThreadsIterator
specifier|public
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|getActivePerThreadsIterator
parameter_list|()
block|{
return|return
name|getPerThreadsIterator
argument_list|(
name|numThreadStatesActive
argument_list|)
return|;
block|}
DECL|method|getPerThreadsIterator
specifier|private
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|getPerThreadsIterator
parameter_list|(
specifier|final
name|int
name|upto
parameter_list|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|<
name|upto
return|;
block|}
specifier|public
name|ThreadState
name|next
parameter_list|()
block|{
return|return
name|perThreads
index|[
name|i
operator|++
index|]
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove() not supported."
argument_list|)
throw|;
block|}
block|}
return|;
block|}
comment|/**    * Returns the ThreadState with the minimum estimated number of threads    * waiting to acquire its lock or<code>null</code> if no {@link ThreadState}    * is yet visible to the calling thread.    */
DECL|method|minContendedThreadState
specifier|protected
name|ThreadState
name|minContendedThreadState
parameter_list|()
block|{
name|ThreadState
name|minThreadState
init|=
literal|null
decl_stmt|;
comment|// TODO: new Iterator per indexed doc is overkill...?
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|it
init|=
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|ThreadState
name|state
init|=
name|it
operator|.
name|next
argument_list|()
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
block|}
end_class

end_unit

