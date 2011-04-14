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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|atomic
operator|.
name|AtomicBoolean
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
comment|/**  * This class controls {@link DocumentsWriterPerThread} flushing during  * indexing. It tracks the memory consumption per  * {@link DocumentsWriterPerThread} and uses a configured {@link FlushPolicy} to  * decide if a {@link DocumentsWriterPerThread} must flush.  *<p>  * In addition to the {@link FlushPolicy} the flush control might set certain  * {@link DocumentsWriterPerThread} as flush pending iff a  * {@link DocumentsWriterPerThread} exceeds the  * {@link IndexWriterConfig#getRAMPerThreadHardLimitMB()} to prevent address  * space exhaustion.  */
end_comment

begin_class
DECL|class|DocumentsWriterFlushControl
specifier|public
specifier|final
class|class
name|DocumentsWriterFlushControl
block|{
DECL|field|maxBytesPerDWPT
specifier|private
specifier|final
name|long
name|maxBytesPerDWPT
decl_stmt|;
DECL|field|activeBytes
specifier|private
name|long
name|activeBytes
init|=
literal|0
decl_stmt|;
DECL|field|flushBytes
specifier|private
name|long
name|flushBytes
init|=
literal|0
decl_stmt|;
DECL|field|numPending
specifier|private
specifier|volatile
name|int
name|numPending
init|=
literal|0
decl_stmt|;
DECL|field|numFlushing
specifier|private
specifier|volatile
name|int
name|numFlushing
init|=
literal|0
decl_stmt|;
DECL|field|flushDeletes
specifier|final
name|AtomicBoolean
name|flushDeletes
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|fullFlush
specifier|private
name|boolean
name|fullFlush
init|=
literal|false
decl_stmt|;
DECL|field|flushQueue
specifier|private
name|Queue
argument_list|<
name|DocumentsWriterPerThread
argument_list|>
name|flushQueue
init|=
operator|new
name|LinkedList
argument_list|<
name|DocumentsWriterPerThread
argument_list|>
argument_list|()
decl_stmt|;
comment|// only for safety reasons if a DWPT is close to the RAM limit
DECL|field|blockedFlushes
specifier|private
name|Queue
argument_list|<
name|DocumentsWriterPerThread
argument_list|>
name|blockedFlushes
init|=
operator|new
name|LinkedList
argument_list|<
name|DocumentsWriterPerThread
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|peakActiveBytes
name|long
name|peakActiveBytes
init|=
literal|0
decl_stmt|;
comment|// only with assert
DECL|field|peakFlushBytes
name|long
name|peakFlushBytes
init|=
literal|0
decl_stmt|;
comment|// only with assert
DECL|field|peakNetBytes
name|long
name|peakNetBytes
init|=
literal|0
decl_stmt|;
comment|// only with assert
DECL|field|healthiness
specifier|private
specifier|final
name|Healthiness
name|healthiness
decl_stmt|;
DECL|field|perThreadPool
specifier|private
specifier|final
name|DocumentsWriterPerThreadPool
name|perThreadPool
decl_stmt|;
DECL|field|flushPolicy
specifier|private
specifier|final
name|FlushPolicy
name|flushPolicy
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|flushingWriters
specifier|private
specifier|final
name|HashMap
argument_list|<
name|DocumentsWriterPerThread
argument_list|,
name|Long
argument_list|>
name|flushingWriters
init|=
operator|new
name|HashMap
argument_list|<
name|DocumentsWriterPerThread
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|documentsWriter
specifier|private
specifier|final
name|DocumentsWriter
name|documentsWriter
decl_stmt|;
DECL|method|DocumentsWriterFlushControl
name|DocumentsWriterFlushControl
parameter_list|(
name|DocumentsWriter
name|documentsWriter
parameter_list|,
name|Healthiness
name|healthiness
parameter_list|,
name|long
name|maxBytesPerDWPT
parameter_list|)
block|{
name|this
operator|.
name|healthiness
operator|=
name|healthiness
expr_stmt|;
name|this
operator|.
name|perThreadPool
operator|=
name|documentsWriter
operator|.
name|perThreadPool
expr_stmt|;
name|this
operator|.
name|flushPolicy
operator|=
name|documentsWriter
operator|.
name|flushPolicy
expr_stmt|;
name|this
operator|.
name|maxBytesPerDWPT
operator|=
name|maxBytesPerDWPT
expr_stmt|;
name|this
operator|.
name|documentsWriter
operator|=
name|documentsWriter
expr_stmt|;
block|}
DECL|method|activeBytes
specifier|public
specifier|synchronized
name|long
name|activeBytes
parameter_list|()
block|{
return|return
name|activeBytes
return|;
block|}
DECL|method|flushBytes
specifier|public
specifier|synchronized
name|long
name|flushBytes
parameter_list|()
block|{
return|return
name|flushBytes
return|;
block|}
DECL|method|netBytes
specifier|public
specifier|synchronized
name|long
name|netBytes
parameter_list|()
block|{
return|return
name|flushBytes
operator|+
name|activeBytes
return|;
block|}
DECL|method|commitPerThreadBytes
specifier|private
name|void
name|commitPerThreadBytes
parameter_list|(
name|ThreadState
name|perThread
parameter_list|)
block|{
specifier|final
name|long
name|delta
init|=
name|perThread
operator|.
name|perThread
operator|.
name|bytesUsed
argument_list|()
operator|-
name|perThread
operator|.
name|perThreadBytes
decl_stmt|;
name|perThread
operator|.
name|perThreadBytes
operator|+=
name|delta
expr_stmt|;
comment|/*      * We need to differentiate here if we are pending since setFlushPending      * moves the perThread memory to the flushBytes and we could be set to      * pending during a delete      */
if|if
condition|(
name|perThread
operator|.
name|flushPending
condition|)
block|{
name|flushBytes
operator|+=
name|delta
expr_stmt|;
block|}
else|else
block|{
name|activeBytes
operator|+=
name|delta
expr_stmt|;
block|}
assert|assert
name|updatePeaks
argument_list|(
name|delta
argument_list|)
assert|;
block|}
DECL|method|updatePeaks
specifier|private
name|boolean
name|updatePeaks
parameter_list|(
name|long
name|delta
parameter_list|)
block|{
name|peakActiveBytes
operator|=
name|Math
operator|.
name|max
argument_list|(
name|peakActiveBytes
argument_list|,
name|activeBytes
argument_list|)
expr_stmt|;
name|peakFlushBytes
operator|=
name|Math
operator|.
name|max
argument_list|(
name|peakFlushBytes
argument_list|,
name|flushBytes
argument_list|)
expr_stmt|;
name|peakNetBytes
operator|=
name|Math
operator|.
name|max
argument_list|(
name|peakNetBytes
argument_list|,
name|netBytes
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|doAfterDocument
specifier|synchronized
name|DocumentsWriterPerThread
name|doAfterDocument
parameter_list|(
name|ThreadState
name|perThread
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
block|{
name|commitPerThreadBytes
argument_list|(
name|perThread
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|perThread
operator|.
name|flushPending
condition|)
block|{
if|if
condition|(
name|isUpdate
condition|)
block|{
name|flushPolicy
operator|.
name|onUpdate
argument_list|(
name|this
argument_list|,
name|perThread
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flushPolicy
operator|.
name|onInsert
argument_list|(
name|this
argument_list|,
name|perThread
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|perThread
operator|.
name|flushPending
operator|&&
name|perThread
operator|.
name|perThreadBytes
operator|>
name|maxBytesPerDWPT
condition|)
block|{
comment|// safety check to prevent a single DWPT exceeding its RAM limit. This
comment|// is super
comment|// important since we can not address more than 2048 MB per DWPT
name|setFlushPending
argument_list|(
name|perThread
argument_list|)
expr_stmt|;
if|if
condition|(
name|fullFlush
condition|)
block|{
name|DocumentsWriterPerThread
name|toBlock
init|=
name|internalTryCheckOutForFlush
argument_list|(
name|perThread
argument_list|,
literal|false
argument_list|)
decl_stmt|;
assert|assert
name|toBlock
operator|!=
literal|null
assert|;
name|blockedFlushes
operator|.
name|add
argument_list|(
name|toBlock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|DocumentsWriterPerThread
name|flushingDWPT
init|=
name|getFlushIfPending
argument_list|(
name|perThread
argument_list|)
decl_stmt|;
name|healthiness
operator|.
name|updateStalled
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|flushingDWPT
return|;
block|}
DECL|method|doAfterFlush
specifier|synchronized
name|void
name|doAfterFlush
parameter_list|(
name|DocumentsWriterPerThread
name|dwpt
parameter_list|)
block|{
assert|assert
name|flushingWriters
operator|.
name|containsKey
argument_list|(
name|dwpt
argument_list|)
assert|;
try|try
block|{
name|numFlushing
operator|--
expr_stmt|;
name|Long
name|bytes
init|=
name|flushingWriters
operator|.
name|remove
argument_list|(
name|dwpt
argument_list|)
decl_stmt|;
name|flushBytes
operator|-=
name|bytes
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|perThreadPool
operator|.
name|recycle
argument_list|(
name|dwpt
argument_list|)
expr_stmt|;
name|healthiness
operator|.
name|updateStalled
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|allFlushesDue
specifier|public
specifier|synchronized
name|boolean
name|allFlushesDue
parameter_list|()
block|{
return|return
name|numFlushing
operator|==
literal|0
return|;
block|}
DECL|method|waitForFlush
specifier|public
specifier|synchronized
name|void
name|waitForFlush
parameter_list|()
block|{
if|if
condition|(
name|numFlushing
operator|!=
literal|0
condition|)
block|{
try|try
block|{
name|this
operator|.
name|wait
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
comment|/**    * Sets flush pending state on the given {@link ThreadState}. The    * {@link ThreadState} must have indexed at least on Document and must not be    * already pending.    */
DECL|method|setFlushPending
specifier|public
specifier|synchronized
name|void
name|setFlushPending
parameter_list|(
name|ThreadState
name|perThread
parameter_list|)
block|{
assert|assert
operator|!
name|perThread
operator|.
name|flushPending
assert|;
assert|assert
name|perThread
operator|.
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
operator|>
literal|0
assert|;
name|perThread
operator|.
name|flushPending
operator|=
literal|true
expr_stmt|;
comment|// write access synced
specifier|final
name|long
name|bytes
init|=
name|perThread
operator|.
name|perThreadBytes
decl_stmt|;
name|flushBytes
operator|+=
name|bytes
expr_stmt|;
name|activeBytes
operator|-=
name|bytes
expr_stmt|;
name|numPending
operator|++
expr_stmt|;
comment|// write access synced
block|}
DECL|method|doOnAbort
specifier|synchronized
name|void
name|doOnAbort
parameter_list|(
name|ThreadState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|flushPending
condition|)
block|{
name|flushBytes
operator|-=
name|state
operator|.
name|perThreadBytes
expr_stmt|;
block|}
else|else
block|{
name|activeBytes
operator|-=
name|state
operator|.
name|perThreadBytes
expr_stmt|;
block|}
comment|// take it out of the loop this DWPT is stale
name|perThreadPool
operator|.
name|replaceForFlush
argument_list|(
name|state
argument_list|,
name|closed
argument_list|)
expr_stmt|;
name|healthiness
operator|.
name|updateStalled
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|tryCheckoutForFlush
specifier|synchronized
name|DocumentsWriterPerThread
name|tryCheckoutForFlush
parameter_list|(
name|ThreadState
name|perThread
parameter_list|,
name|boolean
name|setPending
parameter_list|)
block|{
if|if
condition|(
name|fullFlush
condition|)
return|return
literal|null
return|;
return|return
name|internalTryCheckOutForFlush
argument_list|(
name|perThread
argument_list|,
name|setPending
argument_list|)
return|;
block|}
DECL|method|internalTryCheckOutForFlush
specifier|private
name|DocumentsWriterPerThread
name|internalTryCheckOutForFlush
parameter_list|(
name|ThreadState
name|perThread
parameter_list|,
name|boolean
name|setPending
parameter_list|)
block|{
if|if
condition|(
name|setPending
operator|&&
operator|!
name|perThread
operator|.
name|flushPending
condition|)
block|{
name|setFlushPending
argument_list|(
name|perThread
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|perThread
operator|.
name|flushPending
condition|)
block|{
comment|// we are pending so all memory is already moved to flushBytes
if|if
condition|(
name|perThread
operator|.
name|tryLock
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
name|perThread
operator|.
name|isActive
argument_list|()
condition|)
block|{
assert|assert
name|perThread
operator|.
name|isHeldByCurrentThread
argument_list|()
assert|;
specifier|final
name|DocumentsWriterPerThread
name|dwpt
decl_stmt|;
specifier|final
name|long
name|bytes
init|=
name|perThread
operator|.
name|perThreadBytes
decl_stmt|;
comment|// do that before
comment|// replace!
name|dwpt
operator|=
name|perThreadPool
operator|.
name|replaceForFlush
argument_list|(
name|perThread
argument_list|,
name|closed
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|flushingWriters
operator|.
name|containsKey
argument_list|(
name|dwpt
argument_list|)
operator|:
literal|"DWPT is already flushing"
assert|;
comment|// record the flushing DWPT to reduce flushBytes in doAfterFlush
name|flushingWriters
operator|.
name|put
argument_list|(
name|dwpt
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|numPending
operator|--
expr_stmt|;
comment|// write access synced
name|numFlushing
operator|++
expr_stmt|;
return|return
name|dwpt
return|;
block|}
block|}
finally|finally
block|{
name|perThread
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
DECL|method|getFlushIfPending
specifier|private
name|DocumentsWriterPerThread
name|getFlushIfPending
parameter_list|(
name|ThreadState
name|perThread
parameter_list|)
block|{
if|if
condition|(
name|numPending
operator|>
literal|0
condition|)
block|{
specifier|final
name|DocumentsWriterPerThread
name|dwpt
init|=
name|perThread
operator|==
literal|null
condition|?
literal|null
else|:
name|tryCheckoutForFlush
argument_list|(
name|perThread
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|dwpt
operator|==
literal|null
condition|)
block|{
return|return
name|nextPendingFlush
argument_list|()
return|;
block|}
return|return
name|dwpt
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DocumentsWriterFlushControl [activeBytes="
operator|+
name|activeBytes
operator|+
literal|", flushBytes="
operator|+
name|flushBytes
operator|+
literal|"]"
return|;
block|}
DECL|method|nextPendingFlush
name|DocumentsWriterPerThread
name|nextPendingFlush
parameter_list|()
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|DocumentsWriterPerThread
name|poll
init|=
name|flushQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|poll
operator|!=
literal|null
condition|)
block|{
return|return
name|poll
return|;
block|}
block|}
if|if
condition|(
name|numPending
operator|>
literal|0
condition|)
block|{
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|allActiveThreads
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|allActiveThreads
operator|.
name|hasNext
argument_list|()
operator|&&
name|numPending
operator|>
literal|0
condition|)
block|{
name|ThreadState
name|next
init|=
name|allActiveThreads
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|flushPending
condition|)
block|{
name|DocumentsWriterPerThread
name|dwpt
init|=
name|tryCheckoutForFlush
argument_list|(
name|next
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|dwpt
operator|!=
literal|null
condition|)
block|{
return|return
name|dwpt
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|setClosed
specifier|synchronized
name|void
name|setClosed
parameter_list|()
block|{
comment|// set by DW to signal that we should not release new DWPT after close
name|this
operator|.
name|closed
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * Returns an iterator that provides access to all currently active {@link ThreadState}s     */
DECL|method|allActiveThreads
specifier|public
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|allActiveThreads
parameter_list|()
block|{
return|return
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
return|;
block|}
DECL|method|maxNetBytes
name|long
name|maxNetBytes
parameter_list|()
block|{
return|return
name|flushPolicy
operator|.
name|getMaxNetBytes
argument_list|()
return|;
block|}
DECL|method|doOnDelete
specifier|synchronized
name|void
name|doOnDelete
parameter_list|()
block|{
comment|// pass null this is a global delete no update
name|flushPolicy
operator|.
name|onDelete
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of delete terms in the global pool    */
DECL|method|getNumGlobalTermDeletes
specifier|public
name|int
name|getNumGlobalTermDeletes
parameter_list|()
block|{
return|return
name|documentsWriter
operator|.
name|deleteQueue
operator|.
name|numGlobalTermDeletes
argument_list|()
return|;
block|}
DECL|method|numFlushingDWPT
name|int
name|numFlushingDWPT
parameter_list|()
block|{
return|return
name|numFlushing
return|;
block|}
DECL|method|setFlushDeletes
specifier|public
name|void
name|setFlushDeletes
parameter_list|()
block|{
name|flushDeletes
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|numActiveDWPT
name|int
name|numActiveDWPT
parameter_list|()
block|{
return|return
name|this
operator|.
name|perThreadPool
operator|.
name|getMaxThreadStates
argument_list|()
return|;
block|}
DECL|method|markForFullFlush
name|void
name|markForFullFlush
parameter_list|()
block|{
specifier|final
name|DocumentsWriterDeleteQueue
name|flushingQueue
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
assert|assert
operator|!
name|fullFlush
assert|;
name|fullFlush
operator|=
literal|true
expr_stmt|;
name|flushingQueue
operator|=
name|documentsWriter
operator|.
name|deleteQueue
expr_stmt|;
comment|// set a new delete queue - all subsequent DWPT will use this queue until
comment|// we do another full flush
name|documentsWriter
operator|.
name|deleteQueue
operator|=
operator|new
name|DocumentsWriterDeleteQueue
argument_list|(
operator|new
name|BufferedDeletes
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|allActiveThreads
init|=
name|perThreadPool
operator|.
name|getActivePerThreadsIterator
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|DocumentsWriterPerThread
argument_list|>
name|toFlush
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentsWriterPerThread
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|allActiveThreads
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|ThreadState
name|next
init|=
name|allActiveThreads
operator|.
name|next
argument_list|()
decl_stmt|;
name|next
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|next
operator|.
name|isActive
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|next
operator|.
name|perThread
operator|.
name|deleteQueue
operator|!=
name|flushingQueue
condition|)
block|{
comment|// this one is already a new DWPT
continue|continue;
block|}
if|if
condition|(
name|next
operator|.
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|DocumentsWriterPerThread
name|dwpt
init|=
name|next
operator|.
name|perThread
decl_stmt|;
comment|// just for assert
specifier|final
name|DocumentsWriterPerThread
name|flushingDWPT
init|=
name|internalTryCheckOutForFlush
argument_list|(
name|next
argument_list|,
literal|true
argument_list|)
decl_stmt|;
assert|assert
name|flushingDWPT
operator|!=
literal|null
operator|:
literal|"DWPT must never be null here since we hold the lock and it holds documents"
assert|;
assert|assert
name|dwpt
operator|==
name|flushingDWPT
operator|:
literal|"flushControl returned different DWPT"
assert|;
name|toFlush
operator|.
name|add
argument_list|(
name|flushingDWPT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// get the new delete queue from DW
name|next
operator|.
name|perThread
operator|.
name|initialize
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|next
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|flushQueue
operator|.
name|addAll
argument_list|(
name|blockedFlushes
argument_list|)
expr_stmt|;
name|blockedFlushes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|flushQueue
operator|.
name|addAll
argument_list|(
name|toFlush
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|finishFullFlush
specifier|synchronized
name|void
name|finishFullFlush
parameter_list|()
block|{
assert|assert
name|fullFlush
assert|;
assert|assert
name|flushQueue
operator|.
name|isEmpty
argument_list|()
assert|;
try|try
block|{
if|if
condition|(
operator|!
name|blockedFlushes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|flushQueue
operator|.
name|addAll
argument_list|(
name|blockedFlushes
argument_list|)
expr_stmt|;
name|blockedFlushes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fullFlush
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|abortFullFlushes
specifier|synchronized
name|void
name|abortFullFlushes
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|DocumentsWriterPerThread
name|dwpt
range|:
name|flushQueue
control|)
block|{
name|doAfterFlush
argument_list|(
name|dwpt
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DocumentsWriterPerThread
name|dwpt
range|:
name|blockedFlushes
control|)
block|{
name|doAfterFlush
argument_list|(
name|dwpt
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|flushQueue
operator|.
name|clear
argument_list|()
expr_stmt|;
name|blockedFlushes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fullFlush
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|isFullFlush
specifier|synchronized
name|boolean
name|isFullFlush
parameter_list|()
block|{
return|return
name|fullFlush
return|;
block|}
block|}
end_class

end_unit

