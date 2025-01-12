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
name|io
operator|.
name|IOException
import|;
end_import

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
name|EnumMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|Condition
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BooleanSupplier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|MergeInfo
import|;
end_import

begin_comment
comment|/**  *<p>Expert: a MergePolicy determines the sequence of  * primitive merge operations.</p>  *   *<p>Whenever the segments in an index have been altered by  * {@link IndexWriter}, either the addition of a newly  * flushed segment, addition of many segments from  * addIndexes* calls, or a previous merge that may now need  * to cascade, {@link IndexWriter} invokes {@link  * #findMerges} to give the MergePolicy a chance to pick  * merges that are now required.  This method returns a  * {@link MergeSpecification} instance describing the set of  * merges that should be done, or null if no merges are  * necessary.  When IndexWriter.forceMerge is called, it calls  * {@link #findForcedMerges(SegmentInfos,int,Map, IndexWriter)} and the MergePolicy should  * then return the necessary merges.</p>  *  *<p>Note that the policy can return more than one merge at  * a time.  In this case, if the writer is using {@link  * SerialMergeScheduler}, the merges will be run  * sequentially but if it is using {@link  * ConcurrentMergeScheduler} they will be run concurrently.</p>  *   *<p>The default MergePolicy is {@link  * TieredMergePolicy}.</p>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|MergePolicy
specifier|public
specifier|abstract
class|class
name|MergePolicy
block|{
comment|/**    * Progress and state for an executing merge. This class    * encapsulates the logic to pause and resume the merge thread    * or to abort the merge entirely.    *     * @lucene.experimental */
DECL|class|OneMergeProgress
specifier|public
specifier|static
class|class
name|OneMergeProgress
block|{
comment|/** Reason for pausing the merge thread. */
DECL|enum|PauseReason
specifier|public
specifier|static
enum|enum
name|PauseReason
block|{
comment|/** Stopped (because of throughput rate set to 0, typically). */
DECL|enum constant|STOPPED
name|STOPPED
block|,
comment|/** Temporarily paused because of exceeded throughput rate. */
DECL|enum constant|PAUSED
name|PAUSED
block|,
comment|/** Other reason. */
DECL|enum constant|OTHER
name|OTHER
block|}
empty_stmt|;
DECL|field|pauseLock
specifier|private
specifier|final
name|ReentrantLock
name|pauseLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|pausing
specifier|private
specifier|final
name|Condition
name|pausing
init|=
name|pauseLock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**      * Pause times (in nanoseconds) for each {@link PauseReason}.      */
DECL|field|pauseTimesNS
specifier|private
specifier|final
name|EnumMap
argument_list|<
name|PauseReason
argument_list|,
name|AtomicLong
argument_list|>
name|pauseTimesNS
decl_stmt|;
DECL|field|aborted
specifier|private
specifier|volatile
name|boolean
name|aborted
decl_stmt|;
comment|/**      * This field is for sanity-check purposes only. Only the same thread that invoked      * {@link OneMerge#mergeInit()} is permitted to be calling       * {@link #pauseNanos}. This is always verified at runtime.       */
DECL|field|owner
specifier|private
name|Thread
name|owner
decl_stmt|;
comment|/** Creates a new merge progress info. */
DECL|method|OneMergeProgress
specifier|public
name|OneMergeProgress
parameter_list|()
block|{
comment|// Place all the pause reasons in there immediately so that we can simply update values.
name|pauseTimesNS
operator|=
operator|new
name|EnumMap
argument_list|<
name|PauseReason
argument_list|,
name|AtomicLong
argument_list|>
argument_list|(
name|PauseReason
operator|.
name|class
argument_list|)
expr_stmt|;
for|for
control|(
name|PauseReason
name|p
range|:
name|PauseReason
operator|.
name|values
argument_list|()
control|)
block|{
name|pauseTimesNS
operator|.
name|put
argument_list|(
name|p
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Abort the merge this progress tracks at the next       * possible moment.      */
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|aborted
operator|=
literal|true
expr_stmt|;
name|wakeup
argument_list|()
expr_stmt|;
comment|// wakeup any paused merge thread.
block|}
comment|/**      * Return the aborted state of this merge.      */
DECL|method|isAborted
specifier|public
name|boolean
name|isAborted
parameter_list|()
block|{
return|return
name|aborted
return|;
block|}
comment|/**      * Pauses the calling thread for at least<code>pauseNanos</code> nanoseconds      * unless the merge is aborted or the external condition returns<code>false</code>,      * in which case control returns immediately.      *       * The external condition is required so that other threads can terminate the pausing immediately,      * before<code>pauseNanos</code> expires. We can't rely on just {@link Condition#awaitNanos(long)} alone      * because it can return due to spurious wakeups too.        *       * @param condition The pause condition that should return false if immediate return from this      *      method is needed. Other threads can wake up any sleeping thread by calling       *      {@link #wakeup}, but it'd fall to sleep for the remainder of the requested time if this      *      condition       */
DECL|method|pauseNanos
specifier|public
name|void
name|pauseNanos
parameter_list|(
name|long
name|pauseNanos
parameter_list|,
name|PauseReason
name|reason
parameter_list|,
name|BooleanSupplier
name|condition
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|!=
name|owner
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Only the merge owner thread can call pauseNanos(). This thread: "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|", owner thread: "
operator|+
name|owner
argument_list|)
throw|;
block|}
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|AtomicLong
name|timeUpdate
init|=
name|pauseTimesNS
operator|.
name|get
argument_list|(
name|reason
argument_list|)
decl_stmt|;
name|pauseLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
name|pauseNanos
operator|>
literal|0
operator|&&
operator|!
name|aborted
operator|&&
name|condition
operator|.
name|getAsBoolean
argument_list|()
condition|)
block|{
name|pauseNanos
operator|=
name|pausing
operator|.
name|awaitNanos
argument_list|(
name|pauseNanos
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|pauseLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|timeUpdate
operator|.
name|addAndGet
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Request a wakeup for any threads stalled in {@link #pauseNanos}.      */
DECL|method|wakeup
specifier|public
name|void
name|wakeup
parameter_list|()
block|{
name|pauseLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|pausing
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|pauseLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns pause reasons and associated times in nanoseconds. */
DECL|method|getPauseTimes
specifier|public
name|Map
argument_list|<
name|PauseReason
argument_list|,
name|Long
argument_list|>
name|getPauseTimes
parameter_list|()
block|{
name|Set
argument_list|<
name|Entry
argument_list|<
name|PauseReason
argument_list|,
name|AtomicLong
argument_list|>
argument_list|>
name|entries
init|=
name|pauseTimesNS
operator|.
name|entrySet
argument_list|()
decl_stmt|;
return|return
name|entries
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toMap
argument_list|(
parameter_list|(
name|e
parameter_list|)
lambda|->
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
parameter_list|(
name|e
parameter_list|)
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|setMergeThread
specifier|final
name|void
name|setMergeThread
parameter_list|(
name|Thread
name|owner
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|owner
operator|==
literal|null
assert|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
block|}
comment|/** OneMerge provides the information necessary to perform    *  an individual primitive merge operation, resulting in    *  a single new segment.  The merge spec includes the    *  subset of segments to be merged as well as whether the    *  new segment should use the compound file format.    *    * @lucene.experimental */
DECL|class|OneMerge
specifier|public
specifier|static
class|class
name|OneMerge
block|{
DECL|field|info
name|SegmentCommitInfo
name|info
decl_stmt|;
comment|// used by IndexWriter
DECL|field|registerDone
name|boolean
name|registerDone
decl_stmt|;
comment|// used by IndexWriter
DECL|field|mergeGen
name|long
name|mergeGen
decl_stmt|;
comment|// used by IndexWriter
DECL|field|isExternal
name|boolean
name|isExternal
decl_stmt|;
comment|// used by IndexWriter
DECL|field|maxNumSegments
name|int
name|maxNumSegments
init|=
operator|-
literal|1
decl_stmt|;
comment|// used by IndexWriter
comment|/** Estimated size in bytes of the merged segment. */
DECL|field|estimatedMergeBytes
specifier|public
specifier|volatile
name|long
name|estimatedMergeBytes
decl_stmt|;
comment|// used by IndexWriter
comment|// Sum of sizeInBytes of all SegmentInfos; set by IW.mergeInit
DECL|field|totalMergeBytes
specifier|volatile
name|long
name|totalMergeBytes
decl_stmt|;
DECL|field|readers
name|List
argument_list|<
name|SegmentReader
argument_list|>
name|readers
decl_stmt|;
comment|// used by IndexWriter
comment|/** Segments to be merged. */
DECL|field|segments
specifier|public
specifier|final
name|List
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|segments
decl_stmt|;
comment|/**      * Control used to pause/stop/resume the merge thread.       */
DECL|field|mergeProgress
specifier|private
specifier|final
name|OneMergeProgress
name|mergeProgress
decl_stmt|;
DECL|field|mergeStartNS
specifier|volatile
name|long
name|mergeStartNS
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Total number of documents in segments to be merged, not accounting for deletions. */
DECL|field|totalMaxDoc
specifier|public
specifier|final
name|int
name|totalMaxDoc
decl_stmt|;
DECL|field|error
name|Throwable
name|error
decl_stmt|;
comment|/** Sole constructor.      * @param segments List of {@link SegmentCommitInfo}s      *        to be merged. */
DECL|method|OneMerge
specifier|public
name|OneMerge
parameter_list|(
name|List
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|segments
parameter_list|)
block|{
if|if
condition|(
literal|0
operator|==
name|segments
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"segments must include at least one segment"
argument_list|)
throw|;
block|}
comment|// clone the list, as the in list may be based off original SegmentInfos and may be modified
name|this
operator|.
name|segments
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|segments
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|segments
control|)
block|{
name|count
operator|+=
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
name|totalMaxDoc
operator|=
name|count
expr_stmt|;
name|mergeProgress
operator|=
operator|new
name|OneMergeProgress
argument_list|()
expr_stmt|;
block|}
comment|/**       * Called by {@link IndexWriter} after the merge started and from the      * thread that will be executing the merge.      */
DECL|method|mergeInit
specifier|public
name|void
name|mergeInit
parameter_list|()
throws|throws
name|IOException
block|{
name|mergeProgress
operator|.
name|setMergeThread
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Called by {@link IndexWriter} after the merge is done and all readers have been closed. */
DECL|method|mergeFinished
specifier|public
name|void
name|mergeFinished
parameter_list|()
throws|throws
name|IOException
block|{     }
comment|/** Wrap the reader in order to add/remove information to the merged segment. */
DECL|method|wrapForMerge
specifier|public
name|CodecReader
name|wrapForMerge
parameter_list|(
name|CodecReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
return|;
block|}
comment|/**      * Expert: Sets the {@link SegmentCommitInfo} of the merged segment.      * Allows sub-classes to e.g. set diagnostics properties.      */
DECL|method|setMergeInfo
specifier|public
name|void
name|setMergeInfo
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
comment|/**      * Returns the {@link SegmentCommitInfo} for the merged segment,      * or null if it hasn't been set yet.      */
DECL|method|getMergeInfo
specifier|public
name|SegmentCommitInfo
name|getMergeInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
comment|/** Record that an exception occurred while executing      *  this merge */
DECL|method|setException
specifier|synchronized
name|void
name|setException
parameter_list|(
name|Throwable
name|error
parameter_list|)
block|{
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
comment|/** Retrieve previous exception set by {@link      *  #setException}. */
DECL|method|getException
specifier|synchronized
name|Throwable
name|getException
parameter_list|()
block|{
return|return
name|error
return|;
block|}
comment|/** Returns a readable description of the current merge      *  state. */
DECL|method|segString
specifier|public
name|String
name|segString
parameter_list|()
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numSegments
init|=
name|segments
operator|.
name|size
argument_list|()
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
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|segments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" into "
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|info
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|maxNumSegments
operator|!=
operator|-
literal|1
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" [maxNumSegments="
operator|+
name|maxNumSegments
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isAborted
argument_list|()
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" [ABORTED]"
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the total size in bytes of this merge. Note that this does not      * indicate the size of the merged segment, but the      * input total size. This is only set once the merge is      * initialized by IndexWriter.      */
DECL|method|totalBytesSize
specifier|public
name|long
name|totalBytesSize
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|totalMergeBytes
return|;
block|}
comment|/**      * Returns the total number of documents that are included with this merge.      * Note that this does not indicate the number of documents after the merge.      * */
DECL|method|totalNumDocs
specifier|public
name|int
name|totalNumDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|segments
control|)
block|{
name|total
operator|+=
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
comment|/** Return {@link MergeInfo} describing this merge. */
DECL|method|getStoreMergeInfo
specifier|public
name|MergeInfo
name|getStoreMergeInfo
parameter_list|()
block|{
return|return
operator|new
name|MergeInfo
argument_list|(
name|totalMaxDoc
argument_list|,
name|estimatedMergeBytes
argument_list|,
name|isExternal
argument_list|,
name|maxNumSegments
argument_list|)
return|;
block|}
comment|/** Returns true if this merge was or should be aborted. */
DECL|method|isAborted
specifier|public
name|boolean
name|isAborted
parameter_list|()
block|{
return|return
name|mergeProgress
operator|.
name|isAborted
argument_list|()
return|;
block|}
comment|/** Marks this merge as aborted. The merge thread should terminate at the soonest possible moment. */
DECL|method|setAborted
specifier|public
name|void
name|setAborted
parameter_list|()
block|{
name|this
operator|.
name|mergeProgress
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
comment|/** Checks if merge has been aborted and throws a merge exception if so. */
DECL|method|checkAborted
specifier|public
name|void
name|checkAborted
parameter_list|()
throws|throws
name|MergeAbortedException
block|{
if|if
condition|(
name|isAborted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MergePolicy
operator|.
name|MergeAbortedException
argument_list|(
literal|"merge is aborted: "
operator|+
name|segString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a {@link OneMergeProgress} instance for this merge, which provides      * statistics of the merge threads (run time vs. sleep time) if merging is throttled.      */
DECL|method|getMergeProgress
specifier|public
name|OneMergeProgress
name|getMergeProgress
parameter_list|()
block|{
return|return
name|mergeProgress
return|;
block|}
block|}
comment|/**    * A MergeSpecification instance provides the information    * necessary to perform multiple merges.  It simply    * contains a list of {@link OneMerge} instances.    */
DECL|class|MergeSpecification
specifier|public
specifier|static
class|class
name|MergeSpecification
block|{
comment|/**      * The subset of segments to be included in the primitive merge.      */
DECL|field|merges
specifier|public
specifier|final
name|List
argument_list|<
name|OneMerge
argument_list|>
name|merges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Sole constructor.  Use {@link      *  #add(MergePolicy.OneMerge)} to add merges. */
DECL|method|MergeSpecification
specifier|public
name|MergeSpecification
parameter_list|()
block|{     }
comment|/** Adds the provided {@link OneMerge} to this      *  specification. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|OneMerge
name|merge
parameter_list|)
block|{
name|merges
operator|.
name|add
argument_list|(
name|merge
argument_list|)
expr_stmt|;
block|}
comment|/** Returns a description of the merges in this specification. */
DECL|method|segString
specifier|public
name|String
name|segString
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"MergeSpec:\n"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|merges
operator|.
name|size
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
operator|.
name|append
argument_list|(
literal|1
operator|+
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|merges
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|segString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** Exception thrown if there are any problems while executing a merge. */
DECL|class|MergeException
specifier|public
specifier|static
class|class
name|MergeException
extends|extends
name|RuntimeException
block|{
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
comment|/** Create a {@code MergeException}. */
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|String
name|message
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** Create a {@code MergeException}. */
DECL|method|MergeException
specifier|public
name|MergeException
parameter_list|(
name|Throwable
name|exc
parameter_list|,
name|Directory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|exc
argument_list|)
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
comment|/** Returns the {@link Directory} of the index that hit      *  the exception. */
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
block|}
comment|/** Thrown when a merge was explicitly aborted because    *  {@link IndexWriter#abortMerges} was called.  Normally    *  this exception is privately caught and suppressed by    *  {@link IndexWriter}. */
DECL|class|MergeAbortedException
specifier|public
specifier|static
class|class
name|MergeAbortedException
extends|extends
name|IOException
block|{
comment|/** Create a {@link MergeAbortedException}. */
DECL|method|MergeAbortedException
specifier|public
name|MergeAbortedException
parameter_list|()
block|{
name|super
argument_list|(
literal|"merge is aborted"
argument_list|)
expr_stmt|;
block|}
comment|/** Create a {@link MergeAbortedException} with a      *  specified message. */
DECL|method|MergeAbortedException
specifier|public
name|MergeAbortedException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Default ratio for compound file system usage. Set to<tt>1.0</tt>, always use     * compound file system.    */
DECL|field|DEFAULT_NO_CFS_RATIO
specifier|protected
specifier|static
specifier|final
name|double
name|DEFAULT_NO_CFS_RATIO
init|=
literal|1.0
decl_stmt|;
comment|/**    * Default max segment size in order to use compound file system. Set to {@link Long#MAX_VALUE}.    */
DECL|field|DEFAULT_MAX_CFS_SEGMENT_SIZE
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_MAX_CFS_SEGMENT_SIZE
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** If the size of the merge segment exceeds this ratio of    *  the total index size then it will remain in    *  non-compound format */
DECL|field|noCFSRatio
specifier|protected
name|double
name|noCFSRatio
init|=
name|DEFAULT_NO_CFS_RATIO
decl_stmt|;
comment|/** If the size of the merged segment exceeds    *  this value then it will not use compound file format. */
DECL|field|maxCFSSegmentSize
specifier|protected
name|long
name|maxCFSSegmentSize
init|=
name|DEFAULT_MAX_CFS_SEGMENT_SIZE
decl_stmt|;
comment|/**    * Creates a new merge policy instance.    */
DECL|method|MergePolicy
specifier|public
name|MergePolicy
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_NO_CFS_RATIO
argument_list|,
name|DEFAULT_MAX_CFS_SEGMENT_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new merge policy instance with default settings for noCFSRatio    * and maxCFSSegmentSize. This ctor should be used by subclasses using different    * defaults than the {@link MergePolicy}    */
DECL|method|MergePolicy
specifier|protected
name|MergePolicy
parameter_list|(
name|double
name|defaultNoCFSRatio
parameter_list|,
name|long
name|defaultMaxCFSSegmentSize
parameter_list|)
block|{
name|this
operator|.
name|noCFSRatio
operator|=
name|defaultNoCFSRatio
expr_stmt|;
name|this
operator|.
name|maxCFSSegmentSize
operator|=
name|defaultMaxCFSSegmentSize
expr_stmt|;
block|}
comment|/**    * Determine what set of merge operations are now necessary on the index.    * {@link IndexWriter} calls this whenever there is a change to the segments.    * This call is always synchronized on the {@link IndexWriter} instance so    * only one thread at a time will call this method.    * @param mergeTrigger the event that triggered the merge    * @param segmentInfos    *          the total set of segments in the index    * @param writer the IndexWriter to find the merges on    */
DECL|method|findMerges
specifier|public
specifier|abstract
name|MergeSpecification
name|findMerges
parameter_list|(
name|MergeTrigger
name|mergeTrigger
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Determine what set of merge operations is necessary in    * order to merge to {@code<=} the specified segment count. {@link IndexWriter} calls this when its    * {@link IndexWriter#forceMerge} method is called. This call is always    * synchronized on the {@link IndexWriter} instance so only one thread at a    * time will call this method.    *     * @param segmentInfos    *          the total set of segments in the index    * @param maxSegmentCount    *          requested maximum number of segments in the index (currently this    *          is always 1)    * @param segmentsToMerge    *          contains the specific SegmentInfo instances that must be merged    *          away. This may be a subset of all    *          SegmentInfos.  If the value is True for a    *          given SegmentInfo, that means this segment was    *          an original segment present in the    *          to-be-merged index; else, it was a segment    *          produced by a cascaded merge.    * @param writer the IndexWriter to find the merges on    */
DECL|method|findForcedMerges
specifier|public
specifier|abstract
name|MergeSpecification
name|findForcedMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|int
name|maxSegmentCount
parameter_list|,
name|Map
argument_list|<
name|SegmentCommitInfo
argument_list|,
name|Boolean
argument_list|>
name|segmentsToMerge
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Determine what set of merge operations is necessary in order to expunge all    * deletes from the index.    *     * @param segmentInfos    *          the total set of segments in the index    * @param writer the IndexWriter to find the merges on    */
DECL|method|findForcedDeletesMerges
specifier|public
specifier|abstract
name|MergeSpecification
name|findForcedDeletesMerges
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns true if a new segment (regardless of its origin) should use the    * compound file format. The default implementation returns<code>true</code>    * iff the size of the given mergedInfo is less or equal to    * {@link #getMaxCFSSegmentSizeMB()} and the size is less or equal to the    * TotalIndexSize * {@link #getNoCFSRatio()} otherwise<code>false</code>.    */
DECL|method|useCompoundFile
specifier|public
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|SegmentCommitInfo
name|mergedInfo
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getNoCFSRatio
argument_list|()
operator|==
literal|0.0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|long
name|mergedInfoSize
init|=
name|size
argument_list|(
name|mergedInfo
argument_list|,
name|writer
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergedInfoSize
operator|>
name|maxCFSSegmentSize
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getNoCFSRatio
argument_list|()
operator|>=
literal|1.0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|infos
control|)
block|{
name|totalSize
operator|+=
name|size
argument_list|(
name|info
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
return|return
name|mergedInfoSize
operator|<=
name|getNoCFSRatio
argument_list|()
operator|*
name|totalSize
return|;
block|}
comment|/** Return the byte size of the provided {@link    *  SegmentCommitInfo}, pro-rated by percentage of    *  non-deleted documents is set. */
DECL|method|size
specifier|protected
name|long
name|size
parameter_list|(
name|SegmentCommitInfo
name|info
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|byteSize
init|=
name|info
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|int
name|delCount
init|=
name|writer
operator|.
name|numDeletedDocs
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|double
name|delRatio
init|=
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
operator|<=
literal|0
condition|?
literal|0.0f
else|:
operator|(
name|float
operator|)
name|delCount
operator|/
operator|(
name|float
operator|)
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
assert|assert
name|delRatio
operator|<=
literal|1.0
assert|;
return|return
operator|(
name|info
operator|.
name|info
operator|.
name|maxDoc
argument_list|()
operator|<=
literal|0
condition|?
name|byteSize
else|:
call|(
name|long
call|)
argument_list|(
name|byteSize
operator|*
operator|(
literal|1.0
operator|-
name|delRatio
operator|)
argument_list|)
operator|)
return|;
block|}
comment|/** Returns true if this single info is already fully merged (has no    *  pending deletes, is in the same dir as the    *  writer, and matches the current compound file setting */
DECL|method|isMerged
specifier|protected
specifier|final
name|boolean
name|isMerged
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|SegmentCommitInfo
name|info
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|writer
operator|!=
literal|null
assert|;
name|boolean
name|hasDeletions
init|=
name|writer
operator|.
name|numDeletedDocs
argument_list|(
name|info
argument_list|)
operator|>
literal|0
decl_stmt|;
return|return
operator|!
name|hasDeletions
operator|&&
name|info
operator|.
name|info
operator|.
name|dir
operator|==
name|writer
operator|.
name|getDirectory
argument_list|()
operator|&&
name|useCompoundFile
argument_list|(
name|infos
argument_list|,
name|info
argument_list|,
name|writer
argument_list|)
operator|==
name|info
operator|.
name|info
operator|.
name|getUseCompoundFile
argument_list|()
return|;
block|}
comment|/** Returns current {@code noCFSRatio}.    *    *  @see #setNoCFSRatio */
DECL|method|getNoCFSRatio
specifier|public
name|double
name|getNoCFSRatio
parameter_list|()
block|{
return|return
name|noCFSRatio
return|;
block|}
comment|/** If a merged segment will be more than this percentage    *  of the total size of the index, leave the segment as    *  non-compound file even if compound file is enabled.    *  Set to 1.0 to always use CFS regardless of merge    *  size. */
DECL|method|setNoCFSRatio
specifier|public
name|void
name|setNoCFSRatio
parameter_list|(
name|double
name|noCFSRatio
parameter_list|)
block|{
if|if
condition|(
name|noCFSRatio
argument_list|<
literal|0.0
operator|||
name|noCFSRatio
argument_list|>
literal|1.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"noCFSRatio must be 0.0 to 1.0 inclusive; got "
operator|+
name|noCFSRatio
argument_list|)
throw|;
block|}
name|this
operator|.
name|noCFSRatio
operator|=
name|noCFSRatio
expr_stmt|;
block|}
comment|/** Returns the largest size allowed for a compound file segment */
DECL|method|getMaxCFSSegmentSizeMB
specifier|public
specifier|final
name|double
name|getMaxCFSSegmentSizeMB
parameter_list|()
block|{
return|return
name|maxCFSSegmentSize
operator|/
literal|1024
operator|/
literal|1024.
return|;
block|}
comment|/** If a merged segment will be more than this value,    *  leave the segment as    *  non-compound file even if compound file is enabled.    *  Set this to Double.POSITIVE_INFINITY (default) and noCFSRatio to 1.0    *  to always use CFS regardless of merge size. */
DECL|method|setMaxCFSSegmentSizeMB
specifier|public
name|void
name|setMaxCFSSegmentSizeMB
parameter_list|(
name|double
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|<
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxCFSSegmentSizeMB must be>=0 (got "
operator|+
name|v
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|v
operator|*=
literal|1024
operator|*
literal|1024
expr_stmt|;
name|this
operator|.
name|maxCFSSegmentSize
operator|=
name|v
operator|>
name|Long
operator|.
name|MAX_VALUE
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
operator|(
name|long
operator|)
name|v
expr_stmt|;
block|}
block|}
end_class

end_unit

