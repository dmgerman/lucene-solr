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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Set
import|;
end_import

begin_comment
comment|/**<p>This class implements a {@link MergePolicy} that tries  *  to merge segments into levels of exponentially  *  increasing size, where each level has< mergeFactor  *  segments in it.  Whenever a given levle has mergeFactor  *  segments or more in it, they will be merged.</p>  *  *<p>This class is abstract and requires a subclass to  * define the {@link #size} method which specifies how a  * segment's size is determined.  {@link LogDocMergePolicy}  * is one subclass that measures size by document count in  * the segment.  {@link LogByteSizeMergePolicy} is another  * subclass that measures size as the total byte size of the  * file(s) for the segment.</p>  */
end_comment

begin_class
DECL|class|LogMergePolicy
specifier|public
specifier|abstract
class|class
name|LogMergePolicy
extends|extends
name|MergePolicy
block|{
comment|/** Defines the allowed range of log(size) for each    *  level.  A level is computed by taking the max segment    *  log size, minus LEVEL_LOG_SPAN, and finding all    *  segments falling within that range. */
DECL|field|LEVEL_LOG_SPAN
specifier|public
specifier|static
specifier|final
name|double
name|LEVEL_LOG_SPAN
init|=
literal|0.75
decl_stmt|;
comment|/** Default merge factor, which is how many segments are    *  merged at a time */
DECL|field|DEFAULT_MERGE_FACTOR
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MERGE_FACTOR
init|=
literal|10
decl_stmt|;
comment|/** Default maximum segment size.  A segment of this size    *  or larger will never be merged.  @see setMaxMergeDocs */
DECL|field|DEFAULT_MAX_MERGE_DOCS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_MERGE_DOCS
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|mergeFactor
specifier|private
name|int
name|mergeFactor
init|=
name|DEFAULT_MERGE_FACTOR
decl_stmt|;
DECL|field|minMergeSize
name|long
name|minMergeSize
decl_stmt|;
DECL|field|maxMergeSize
name|long
name|maxMergeSize
decl_stmt|;
DECL|field|maxMergeDocs
name|int
name|maxMergeDocs
init|=
name|DEFAULT_MAX_MERGE_DOCS
decl_stmt|;
comment|/* TODO 3.0: change this default to true */
DECL|field|calibrateSizeByDeletes
specifier|protected
name|boolean
name|calibrateSizeByDeletes
init|=
literal|false
decl_stmt|;
DECL|field|useCompoundFile
specifier|private
name|boolean
name|useCompoundFile
init|=
literal|true
decl_stmt|;
DECL|field|useCompoundDocStore
specifier|private
name|boolean
name|useCompoundDocStore
init|=
literal|true
decl_stmt|;
DECL|field|writer
specifier|private
name|IndexWriter
name|writer
decl_stmt|;
DECL|method|verbose
specifier|protected
name|boolean
name|verbose
parameter_list|()
block|{
return|return
name|writer
operator|!=
literal|null
operator|&&
name|writer
operator|.
name|verbose
argument_list|()
return|;
block|}
DECL|method|message
specifier|private
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|writer
operator|.
name|message
argument_list|(
literal|"LMP: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**<p>Returns the number of segments that are merged at    * once and also controls the total number of segments    * allowed to accumulate in the index.</p> */
DECL|method|getMergeFactor
specifier|public
name|int
name|getMergeFactor
parameter_list|()
block|{
return|return
name|mergeFactor
return|;
block|}
comment|/** Determines how often segment indices are merged by    * addDocument().  With smaller values, less RAM is used    * while indexing, and searches on unoptimized indices are    * faster, but indexing speed is slower.  With larger    * values, more RAM is used during indexing, and while    * searches on unoptimized indices are slower, indexing is    * faster.  Thus larger values (> 10) are best for batch    * index creation, and smaller values (< 10) for indices    * that are interactively maintained. */
DECL|method|setMergeFactor
specifier|public
name|void
name|setMergeFactor
parameter_list|(
name|int
name|mergeFactor
parameter_list|)
block|{
if|if
condition|(
name|mergeFactor
operator|<
literal|2
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"mergeFactor cannot be less than 2"
argument_list|)
throw|;
name|this
operator|.
name|mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
block|}
comment|// Javadoc inherited
DECL|method|useCompoundFile
specifier|public
name|boolean
name|useCompoundFile
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|SegmentInfo
name|info
parameter_list|)
block|{
return|return
name|useCompoundFile
return|;
block|}
comment|/** Sets whether compound file format should be used for    *  newly flushed and newly merged segments. */
DECL|method|setUseCompoundFile
specifier|public
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
name|this
operator|.
name|useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
comment|/** Returns true if newly flushed and newly merge segments    *  are written in compound file format. @see    *  #setUseCompoundFile */
DECL|method|getUseCompoundFile
specifier|public
name|boolean
name|getUseCompoundFile
parameter_list|()
block|{
return|return
name|useCompoundFile
return|;
block|}
comment|// Javadoc inherited
DECL|method|useCompoundDocStore
specifier|public
name|boolean
name|useCompoundDocStore
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|)
block|{
return|return
name|useCompoundDocStore
return|;
block|}
comment|/** Sets whether compound file format should be used for    *  newly flushed and newly merged doc store    *  segment files (term vectors and stored fields). */
DECL|method|setUseCompoundDocStore
specifier|public
name|void
name|setUseCompoundDocStore
parameter_list|(
name|boolean
name|useCompoundDocStore
parameter_list|)
block|{
name|this
operator|.
name|useCompoundDocStore
operator|=
name|useCompoundDocStore
expr_stmt|;
block|}
comment|/** Returns true if newly flushed and newly merge doc    *  store segment files (term vectors and stored fields)    *  are written in compound file format. @see    *  #setUseCompoundDocStore */
DECL|method|getUseCompoundDocStore
specifier|public
name|boolean
name|getUseCompoundDocStore
parameter_list|()
block|{
return|return
name|useCompoundDocStore
return|;
block|}
comment|/** Sets whether the segment size should be calibrated by    *  the number of deletes when choosing segments for merge. */
DECL|method|setCalibrateSizeByDeletes
specifier|public
name|void
name|setCalibrateSizeByDeletes
parameter_list|(
name|boolean
name|calibrateSizeByDeletes
parameter_list|)
block|{
name|this
operator|.
name|calibrateSizeByDeletes
operator|=
name|calibrateSizeByDeletes
expr_stmt|;
block|}
comment|/** Returns true if the segment size should be calibrated     *  by the number of deletes when choosing segments for merge. */
DECL|method|getCalibrateSizeByDeletes
specifier|public
name|boolean
name|getCalibrateSizeByDeletes
parameter_list|()
block|{
return|return
name|calibrateSizeByDeletes
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|method|size
specifier|abstract
specifier|protected
name|long
name|size
parameter_list|(
name|SegmentInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|sizeDocs
specifier|protected
name|long
name|sizeDocs
parameter_list|(
name|SegmentInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|calibrateSizeByDeletes
condition|)
block|{
return|return
operator|(
name|info
operator|.
name|docCount
operator|-
operator|(
name|long
operator|)
name|info
operator|.
name|getDelCount
argument_list|()
operator|)
return|;
block|}
else|else
block|{
return|return
name|info
operator|.
name|docCount
return|;
block|}
block|}
DECL|method|sizeBytes
specifier|protected
name|long
name|sizeBytes
parameter_list|(
name|SegmentInfo
name|info
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
if|if
condition|(
name|calibrateSizeByDeletes
condition|)
block|{
name|float
name|delRatio
init|=
operator|(
name|info
operator|.
name|docCount
operator|<=
literal|0
condition|?
literal|0.0f
else|:
operator|(
operator|(
name|float
operator|)
name|info
operator|.
name|getDelCount
argument_list|()
operator|/
operator|(
name|float
operator|)
name|info
operator|.
name|docCount
operator|)
operator|)
decl_stmt|;
return|return
operator|(
name|info
operator|.
name|docCount
operator|<=
literal|0
condition|?
name|byteSize
else|:
call|(
name|long
call|)
argument_list|(
operator|(
name|float
operator|)
name|byteSize
operator|*
operator|(
literal|1.0f
operator|-
name|delRatio
operator|)
argument_list|)
operator|)
return|;
block|}
else|else
block|{
return|return
name|byteSize
return|;
block|}
block|}
DECL|method|isOptimized
specifier|private
name|boolean
name|isOptimized
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|int
name|maxNumSegments
parameter_list|,
name|Set
name|segmentsToOptimize
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numSegments
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|numToOptimize
init|=
literal|0
decl_stmt|;
name|SegmentInfo
name|optimizeInfo
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
name|numSegments
operator|&&
name|numToOptimize
operator|<=
name|maxNumSegments
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentsToOptimize
operator|.
name|contains
argument_list|(
name|info
argument_list|)
condition|)
block|{
name|numToOptimize
operator|++
expr_stmt|;
name|optimizeInfo
operator|=
name|info
expr_stmt|;
block|}
block|}
return|return
name|numToOptimize
operator|<=
name|maxNumSegments
operator|&&
operator|(
name|numToOptimize
operator|!=
literal|1
operator|||
name|isOptimized
argument_list|(
name|writer
argument_list|,
name|optimizeInfo
argument_list|)
operator|)
return|;
block|}
comment|/** Returns true if this single nfo is optimized (has no    *  pending norms or deletes, is in the same dir as the    *  writer, and matches the current compound file setting */
DECL|method|isOptimized
specifier|private
name|boolean
name|isOptimized
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|SegmentInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|!
name|info
operator|.
name|hasDeletions
argument_list|()
operator|&&
operator|!
name|info
operator|.
name|hasSeparateNorms
argument_list|()
operator|&&
name|info
operator|.
name|dir
operator|==
name|writer
operator|.
name|getDirectory
argument_list|()
operator|&&
name|info
operator|.
name|getUseCompoundFile
argument_list|()
operator|==
name|useCompoundFile
return|;
block|}
comment|/** Returns the merges necessary to optimize the index.    *  This merge policy defines "optimized" to mean only one    *  segment in the index, where that segment has no    *  deletions pending nor separate norms, and it is in    *  compound file format if the current useCompoundFile    *  setting is true.  This method returns multiple merges    *  (mergeFactor at a time) so the {@link MergeScheduler}    *  in use may make use of concurrency. */
DECL|method|findMergesForOptimize
specifier|public
name|MergeSpecification
name|findMergesForOptimize
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|int
name|maxNumSegments
parameter_list|,
name|Set
name|segmentsToOptimize
parameter_list|)
throws|throws
name|IOException
block|{
name|MergeSpecification
name|spec
decl_stmt|;
assert|assert
name|maxNumSegments
operator|>
literal|0
assert|;
if|if
condition|(
operator|!
name|isOptimized
argument_list|(
name|infos
argument_list|,
name|writer
argument_list|,
name|maxNumSegments
argument_list|,
name|segmentsToOptimize
argument_list|)
condition|)
block|{
comment|// Find the newest (rightmost) segment that needs to
comment|// be optimized (other segments may have been flushed
comment|// since optimize started):
name|int
name|last
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
while|while
condition|(
name|last
operator|>
literal|0
condition|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
operator|--
name|last
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentsToOptimize
operator|.
name|contains
argument_list|(
name|info
argument_list|)
condition|)
block|{
name|last
operator|++
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|last
operator|>
literal|0
condition|)
block|{
name|spec
operator|=
operator|new
name|MergeSpecification
argument_list|()
expr_stmt|;
comment|// First, enroll all "full" merges (size
comment|// mergeFactor) to potentially be run concurrently:
while|while
condition|(
name|last
operator|-
name|maxNumSegments
operator|+
literal|1
operator|>=
name|mergeFactor
condition|)
block|{
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|infos
operator|.
name|range
argument_list|(
name|last
operator|-
name|mergeFactor
argument_list|,
name|last
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
name|last
operator|-=
name|mergeFactor
expr_stmt|;
block|}
comment|// Only if there are no full merges pending do we
comment|// add a final partial (< mergeFactor segments) merge:
if|if
condition|(
literal|0
operator|==
name|spec
operator|.
name|merges
operator|.
name|size
argument_list|()
condition|)
block|{
if|if
condition|(
name|maxNumSegments
operator|==
literal|1
condition|)
block|{
comment|// Since we must optimize down to 1 segment, the
comment|// choice is simple:
if|if
condition|(
name|last
operator|>
literal|1
operator|||
operator|!
name|isOptimized
argument_list|(
name|writer
argument_list|,
name|infos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|infos
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|last
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|last
operator|>
name|maxNumSegments
condition|)
block|{
comment|// Take care to pick a partial merge that is
comment|// least cost, but does not make the index too
comment|// lopsided.  If we always just picked the
comment|// partial tail then we could produce a highly
comment|// lopsided index over time:
comment|// We must merge this many segments to leave
comment|// maxNumSegments in the index (from when
comment|// optimize was first kicked off):
specifier|final
name|int
name|finalMergeSize
init|=
name|last
operator|-
name|maxNumSegments
operator|+
literal|1
decl_stmt|;
comment|// Consider all possible starting points:
name|long
name|bestSize
init|=
literal|0
decl_stmt|;
name|int
name|bestStart
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
name|last
operator|-
name|finalMergeSize
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|long
name|sumSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|finalMergeSize
condition|;
name|j
operator|++
control|)
name|sumSize
operator|+=
name|size
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|j
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
operator|||
operator|(
name|sumSize
operator|<
literal|2
operator|*
name|size
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
operator|&&
name|sumSize
operator|<
name|bestSize
operator|)
condition|)
block|{
name|bestStart
operator|=
name|i
expr_stmt|;
name|bestSize
operator|=
name|sumSize
expr_stmt|;
block|}
block|}
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|infos
operator|.
name|range
argument_list|(
name|bestStart
argument_list|,
name|bestStart
operator|+
name|finalMergeSize
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
name|spec
operator|=
literal|null
expr_stmt|;
block|}
else|else
name|spec
operator|=
literal|null
expr_stmt|;
return|return
name|spec
return|;
block|}
comment|/**    * Finds merges necessary to expunge all deletes from the    * index.  We simply merge adjacent segments that have    * deletes, up to mergeFactor at a time.    */
DECL|method|findMergesToExpungeDeletes
specifier|public
name|MergeSpecification
name|findMergesToExpungeDeletes
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
specifier|final
name|int
name|numSegments
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"findMergesToExpungeDeletes: "
operator|+
name|numSegments
operator|+
literal|" segments"
argument_list|)
expr_stmt|;
name|MergeSpecification
name|spec
init|=
operator|new
name|MergeSpecification
argument_list|()
decl_stmt|;
name|int
name|firstSegmentWithDeletions
init|=
operator|-
literal|1
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
specifier|final
name|SegmentInfo
name|info
init|=
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  segment "
operator|+
name|info
operator|.
name|name
operator|+
literal|" has deletions"
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstSegmentWithDeletions
operator|==
operator|-
literal|1
condition|)
name|firstSegmentWithDeletions
operator|=
name|i
expr_stmt|;
elseif|else
if|if
condition|(
name|i
operator|-
name|firstSegmentWithDeletions
operator|==
name|mergeFactor
condition|)
block|{
comment|// We've seen mergeFactor segments in a row with
comment|// deletions, so force a merge now:
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  add merge "
operator|+
name|firstSegmentWithDeletions
operator|+
literal|" to "
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
literal|" inclusive"
argument_list|)
expr_stmt|;
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|segmentInfos
operator|.
name|range
argument_list|(
name|firstSegmentWithDeletions
argument_list|,
name|i
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
name|firstSegmentWithDeletions
operator|=
name|i
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|firstSegmentWithDeletions
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// End of a sequence of segments with deletions, so,
comment|// merge those past segments even if it's fewer than
comment|// mergeFactor segments
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  add merge "
operator|+
name|firstSegmentWithDeletions
operator|+
literal|" to "
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
literal|" inclusive"
argument_list|)
expr_stmt|;
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|segmentInfos
operator|.
name|range
argument_list|(
name|firstSegmentWithDeletions
argument_list|,
name|i
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
name|firstSegmentWithDeletions
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|firstSegmentWithDeletions
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  add merge "
operator|+
name|firstSegmentWithDeletions
operator|+
literal|" to "
operator|+
operator|(
name|numSegments
operator|-
literal|1
operator|)
operator|+
literal|" inclusive"
argument_list|)
expr_stmt|;
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|segmentInfos
operator|.
name|range
argument_list|(
name|firstSegmentWithDeletions
argument_list|,
name|numSegments
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|spec
return|;
block|}
comment|/** Checks if any merges are now necessary and returns a    *  {@link MergePolicy.MergeSpecification} if so.  A merge    *  is necessary when there are more than {@link    *  #setMergeFactor} segments at a given level.  When    *  multiple levels have too many segments, this method    *  will return multiple merges, allowing the {@link    *  MergeScheduler} to use concurrency. */
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numSegments
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"findMerges: "
operator|+
name|numSegments
operator|+
literal|" segments"
argument_list|)
expr_stmt|;
comment|// Compute levels, which is just log (base mergeFactor)
comment|// of the size of each segment
name|float
index|[]
name|levels
init|=
operator|new
name|float
index|[
name|numSegments
index|]
decl_stmt|;
specifier|final
name|float
name|norm
init|=
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
name|mergeFactor
argument_list|)
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
specifier|final
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|long
name|size
init|=
name|size
argument_list|(
name|info
argument_list|)
decl_stmt|;
comment|// Floor tiny segments
if|if
condition|(
name|size
operator|<
literal|1
condition|)
name|size
operator|=
literal|1
expr_stmt|;
name|levels
index|[
name|i
index|]
operator|=
operator|(
name|float
operator|)
name|Math
operator|.
name|log
argument_list|(
name|size
argument_list|)
operator|/
name|norm
expr_stmt|;
block|}
specifier|final
name|float
name|levelFloor
decl_stmt|;
if|if
condition|(
name|minMergeSize
operator|<=
literal|0
condition|)
name|levelFloor
operator|=
operator|(
name|float
operator|)
literal|0.0
expr_stmt|;
else|else
name|levelFloor
operator|=
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|log
argument_list|(
name|minMergeSize
argument_list|)
operator|/
name|norm
argument_list|)
expr_stmt|;
comment|// Now, we quantize the log values into levels.  The
comment|// first level is any segment whose log size is within
comment|// LEVEL_LOG_SPAN of the max size, or, who has such as
comment|// segment "to the right".  Then, we find the max of all
comment|// other segments and use that to define the next level
comment|// segment, etc.
name|MergeSpecification
name|spec
init|=
literal|null
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|numSegments
condition|)
block|{
comment|// Find max level of all segments not already
comment|// quantized.
name|float
name|maxLevel
init|=
name|levels
index|[
name|start
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
operator|+
name|start
init|;
name|i
operator|<
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|float
name|level
init|=
name|levels
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|level
operator|>
name|maxLevel
condition|)
name|maxLevel
operator|=
name|level
expr_stmt|;
block|}
comment|// Now search backwards for the rightmost segment that
comment|// falls into this level:
name|float
name|levelBottom
decl_stmt|;
if|if
condition|(
name|maxLevel
operator|<
name|levelFloor
condition|)
comment|// All remaining segments fall into the min level
name|levelBottom
operator|=
operator|-
literal|1.0F
expr_stmt|;
else|else
block|{
name|levelBottom
operator|=
call|(
name|float
call|)
argument_list|(
name|maxLevel
operator|-
name|LEVEL_LOG_SPAN
argument_list|)
expr_stmt|;
comment|// Force a boundary at the level floor
if|if
condition|(
name|levelBottom
operator|<
name|levelFloor
operator|&&
name|maxLevel
operator|>=
name|levelFloor
condition|)
name|levelBottom
operator|=
name|levelFloor
expr_stmt|;
block|}
name|int
name|upto
init|=
name|numSegments
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|upto
operator|>=
name|start
condition|)
block|{
if|if
condition|(
name|levels
index|[
name|upto
index|]
operator|>=
name|levelBottom
condition|)
block|{
break|break;
block|}
name|upto
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"  level "
operator|+
name|levelBottom
operator|+
literal|" to "
operator|+
name|maxLevel
operator|+
literal|": "
operator|+
operator|(
literal|1
operator|+
name|upto
operator|-
name|start
operator|)
operator|+
literal|" segments"
argument_list|)
expr_stmt|;
comment|// Finally, record all merges that are viable at this level:
name|int
name|end
init|=
name|start
operator|+
name|mergeFactor
decl_stmt|;
while|while
condition|(
name|end
operator|<=
literal|1
operator|+
name|upto
condition|)
block|{
name|boolean
name|anyTooLarge
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|anyTooLarge
operator||=
operator|(
name|size
argument_list|(
name|info
argument_list|)
operator|>=
name|maxMergeSize
operator|||
name|sizeDocs
argument_list|(
name|info
argument_list|)
operator|>=
name|maxMergeDocs
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|anyTooLarge
condition|)
block|{
if|if
condition|(
name|spec
operator|==
literal|null
condition|)
name|spec
operator|=
operator|new
name|MergeSpecification
argument_list|()
expr_stmt|;
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"    "
operator|+
name|start
operator|+
literal|" to "
operator|+
name|end
operator|+
literal|": add this merge"
argument_list|)
expr_stmt|;
name|spec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|infos
operator|.
name|range
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|verbose
argument_list|()
condition|)
name|message
argument_list|(
literal|"    "
operator|+
name|start
operator|+
literal|" to "
operator|+
name|end
operator|+
literal|": contains segment over maxMergeSize or maxMergeDocs; skipping"
argument_list|)
expr_stmt|;
name|start
operator|=
name|end
expr_stmt|;
name|end
operator|=
name|start
operator|+
name|mergeFactor
expr_stmt|;
block|}
name|start
operator|=
literal|1
operator|+
name|upto
expr_stmt|;
block|}
return|return
name|spec
return|;
block|}
comment|/**<p>Determines the largest segment (measured by    * document count) that may be merged with other segments.    * Small values (e.g., less than 10,000) are best for    * interactive indexing, as this limits the length of    * pauses while indexing to a few seconds.  Larger values    * are best for batched indexing and speedier    * searches.</p>    *    *<p>The default value is {@link Integer#MAX_VALUE}.</p>    *    *<p>The default merge policy ({@link    * LogByteSizeMergePolicy}) also allows you to set this    * limit by net size (in MB) of the segment, using {@link    * LogByteSizeMergePolicy#setMaxMergeMB}.</p>    */
DECL|method|setMaxMergeDocs
specifier|public
name|void
name|setMaxMergeDocs
parameter_list|(
name|int
name|maxMergeDocs
parameter_list|)
block|{
name|this
operator|.
name|maxMergeDocs
operator|=
name|maxMergeDocs
expr_stmt|;
block|}
comment|/** Returns the largest segment (measured by document    *  count) that may be merged with other segments.    *  @see #setMaxMergeDocs */
DECL|method|getMaxMergeDocs
specifier|public
name|int
name|getMaxMergeDocs
parameter_list|()
block|{
return|return
name|maxMergeDocs
return|;
block|}
block|}
end_class

end_unit

