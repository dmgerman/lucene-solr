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
comment|/**  * Merge policy that tries to balance not doing large  * segment merges with not accumulating too many segments in  * the index, to provide for better performance in near  * real-time setting.  *  *<p>This is based on code from zoie, described in more detail  * at http://code.google.com/p/zoie/wiki/ZoieMergePolicy.</p>  */
end_comment

begin_class
DECL|class|BalancedSegmentMergePolicy
specifier|public
class|class
name|BalancedSegmentMergePolicy
extends|extends
name|LogByteSizeMergePolicy
block|{
DECL|field|DEFAULT_NUM_LARGE_SEGMENTS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NUM_LARGE_SEGMENTS
init|=
literal|10
decl_stmt|;
DECL|field|_partialExpunge
specifier|private
name|boolean
name|_partialExpunge
init|=
literal|false
decl_stmt|;
DECL|field|_numLargeSegments
specifier|private
name|int
name|_numLargeSegments
init|=
name|DEFAULT_NUM_LARGE_SEGMENTS
decl_stmt|;
DECL|field|_maxSmallSegments
specifier|private
name|int
name|_maxSmallSegments
init|=
literal|2
operator|*
name|LogMergePolicy
operator|.
name|DEFAULT_MERGE_FACTOR
decl_stmt|;
DECL|field|_maxSegments
specifier|private
name|int
name|_maxSegments
init|=
name|_numLargeSegments
operator|+
name|_maxSmallSegments
decl_stmt|;
DECL|method|BalancedSegmentMergePolicy
specifier|public
name|BalancedSegmentMergePolicy
parameter_list|()
block|{   }
DECL|method|setMergePolicyParams
specifier|public
name|void
name|setMergePolicyParams
parameter_list|(
name|MergePolicyParams
name|params
parameter_list|)
block|{
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|setPartialExpunge
argument_list|(
name|params
operator|.
name|_doPartialExpunge
argument_list|)
expr_stmt|;
name|setNumLargeSegments
argument_list|(
name|params
operator|.
name|_numLargeSegments
argument_list|)
expr_stmt|;
name|setMaxSmallSegments
argument_list|(
name|params
operator|.
name|_maxSmallSegments
argument_list|)
expr_stmt|;
name|setPartialExpunge
argument_list|(
name|params
operator|.
name|_doPartialExpunge
argument_list|)
expr_stmt|;
name|setMergeFactor
argument_list|(
name|params
operator|.
name|_mergeFactor
argument_list|)
expr_stmt|;
name|setUseCompoundFile
argument_list|(
name|params
operator|.
name|_useCompoundFile
argument_list|)
expr_stmt|;
name|setMaxMergeDocs
argument_list|(
name|params
operator|.
name|_maxMergeDocs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|size
specifier|protected
name|long
name|size
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
literal|1.0f
operator|-
name|delRatio
operator|)
operator|*
name|byteSize
argument_list|)
operator|)
return|;
block|}
DECL|method|setPartialExpunge
specifier|public
name|void
name|setPartialExpunge
parameter_list|(
name|boolean
name|doPartialExpunge
parameter_list|)
block|{
name|_partialExpunge
operator|=
name|doPartialExpunge
expr_stmt|;
block|}
DECL|method|getPartialExpunge
specifier|public
name|boolean
name|getPartialExpunge
parameter_list|()
block|{
return|return
name|_partialExpunge
return|;
block|}
DECL|method|setNumLargeSegments
specifier|public
name|void
name|setNumLargeSegments
parameter_list|(
name|int
name|numLargeSegments
parameter_list|)
block|{
if|if
condition|(
name|numLargeSegments
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numLargeSegments cannot be less than 2"
argument_list|)
throw|;
block|}
name|_numLargeSegments
operator|=
name|numLargeSegments
expr_stmt|;
name|_maxSegments
operator|=
name|_numLargeSegments
operator|+
literal|2
operator|*
name|getMergeFactor
argument_list|()
expr_stmt|;
block|}
DECL|method|getNumLargeSegments
specifier|public
name|int
name|getNumLargeSegments
parameter_list|()
block|{
return|return
name|_numLargeSegments
return|;
block|}
DECL|method|setMaxSmallSegments
specifier|public
name|void
name|setMaxSmallSegments
parameter_list|(
name|int
name|maxSmallSegments
parameter_list|)
block|{
if|if
condition|(
name|maxSmallSegments
operator|<
name|getMergeFactor
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxSmallSegments cannot be less than mergeFactor"
argument_list|)
throw|;
block|}
name|_maxSmallSegments
operator|=
name|maxSmallSegments
expr_stmt|;
name|_maxSegments
operator|=
name|_numLargeSegments
operator|+
name|_maxSmallSegments
expr_stmt|;
block|}
DECL|method|getMaxSmallSegments
specifier|public
name|int
name|getMaxSmallSegments
parameter_list|()
block|{
return|return
name|_maxSmallSegments
return|;
block|}
annotation|@
name|Override
DECL|method|setMergeFactor
specifier|public
name|void
name|setMergeFactor
parameter_list|(
name|int
name|mergeFactor
parameter_list|)
block|{
name|super
operator|.
name|setMergeFactor
argument_list|(
name|mergeFactor
argument_list|)
expr_stmt|;
if|if
condition|(
name|_maxSmallSegments
operator|<
name|getMergeFactor
argument_list|()
condition|)
block|{
name|_maxSmallSegments
operator|=
name|getMergeFactor
argument_list|()
expr_stmt|;
name|_maxSegments
operator|=
name|_numLargeSegments
operator|+
name|_maxSmallSegments
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|findMergesForOptimize
specifier|public
name|MergeSpecification
name|findMergesForOptimize
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|int
name|maxNumSegments
parameter_list|,
name|Set
argument_list|<
name|SegmentInfo
argument_list|>
name|segmentsToOptimize
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|maxNumSegments
operator|>
literal|0
assert|;
name|MergeSpecification
name|spec
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|isOptimized
argument_list|(
name|infos
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
if|if
condition|(
name|maxNumSegments
operator|==
literal|1
condition|)
block|{
comment|// Since we must optimize down to 1 segment, the
comment|// choice is simple:
name|boolean
name|useCompoundFile
init|=
name|getUseCompoundFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|>
literal|1
operator|||
operator|!
name|isOptimized
argument_list|(
name|infos
operator|.
name|info
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|spec
operator|=
operator|new
name|MergeSpecification
argument_list|()
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
block|}
elseif|else
if|if
condition|(
name|last
operator|>
name|maxNumSegments
condition|)
block|{
comment|// find most balanced merges
name|spec
operator|=
name|findBalancedMerges
argument_list|(
name|infos
argument_list|,
name|last
argument_list|,
name|maxNumSegments
argument_list|,
name|_partialExpunge
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|spec
return|;
block|}
DECL|method|findBalancedMerges
specifier|private
name|MergeSpecification
name|findBalancedMerges
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|int
name|infoLen
parameter_list|,
name|int
name|maxNumSegments
parameter_list|,
name|boolean
name|partialExpunge
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|infoLen
operator|<=
name|maxNumSegments
condition|)
return|return
literal|null
return|;
name|MergeSpecification
name|spec
init|=
operator|new
name|MergeSpecification
argument_list|()
decl_stmt|;
name|boolean
name|useCompoundFile
init|=
name|getUseCompoundFile
argument_list|()
decl_stmt|;
comment|// use Viterbi algorithm to find the best segmentation.
comment|// we will try to minimize the size variance of resulting segments.
name|double
index|[]
index|[]
name|variance
init|=
name|createVarianceTable
argument_list|(
name|infos
argument_list|,
name|infoLen
argument_list|,
name|maxNumSegments
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxMergeSegments
init|=
name|infoLen
operator|-
name|maxNumSegments
operator|+
literal|1
decl_stmt|;
name|double
index|[]
name|sumVariance
init|=
operator|new
name|double
index|[
name|maxMergeSegments
index|]
decl_stmt|;
name|int
index|[]
index|[]
name|backLink
init|=
operator|new
name|int
index|[
name|maxNumSegments
index|]
index|[
name|maxMergeSegments
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|(
name|maxMergeSegments
operator|-
literal|1
operator|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|sumVariance
index|[
name|i
index|]
operator|=
name|variance
index|[
literal|0
index|]
index|[
name|i
index|]
expr_stmt|;
name|backLink
index|[
literal|0
index|]
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|maxNumSegments
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
operator|(
name|maxMergeSegments
operator|-
literal|1
operator|)
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|--
control|)
block|{
name|double
name|minV
init|=
name|Double
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|minK
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
name|j
init|;
name|k
operator|>=
literal|0
condition|;
name|k
operator|--
control|)
block|{
name|double
name|v
init|=
name|sumVariance
index|[
name|k
index|]
operator|+
name|variance
index|[
name|i
operator|+
name|k
index|]
index|[
name|j
operator|-
name|k
index|]
decl_stmt|;
if|if
condition|(
name|v
operator|<
name|minV
condition|)
block|{
name|minV
operator|=
name|v
expr_stmt|;
name|minK
operator|=
name|k
expr_stmt|;
block|}
block|}
name|sumVariance
index|[
name|j
index|]
operator|=
name|minV
expr_stmt|;
name|backLink
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|minK
expr_stmt|;
block|}
block|}
comment|// now, trace back the back links to find all merges,
comment|// also find a candidate for partial expunge if requested
name|int
name|mergeEnd
init|=
name|infoLen
decl_stmt|;
name|int
name|prev
init|=
name|maxMergeSegments
operator|-
literal|1
decl_stmt|;
name|int
name|expungeCandidate
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|maxDelCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|maxNumSegments
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|prev
operator|=
name|backLink
index|[
name|i
index|]
index|[
name|prev
index|]
expr_stmt|;
name|int
name|mergeStart
init|=
name|i
operator|+
name|prev
decl_stmt|;
if|if
condition|(
operator|(
name|mergeEnd
operator|-
name|mergeStart
operator|)
operator|>
literal|1
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
name|mergeStart
argument_list|,
name|mergeEnd
argument_list|)
argument_list|,
name|useCompoundFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|partialExpunge
condition|)
block|{
name|SegmentInfo
name|info
init|=
name|infos
operator|.
name|info
argument_list|(
name|mergeStart
argument_list|)
decl_stmt|;
name|int
name|delCount
init|=
name|info
operator|.
name|getDelCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|delCount
operator|>
name|maxDelCount
condition|)
block|{
name|expungeCandidate
operator|=
name|mergeStart
expr_stmt|;
name|maxDelCount
operator|=
name|delCount
expr_stmt|;
block|}
block|}
block|}
name|mergeEnd
operator|=
name|mergeStart
expr_stmt|;
block|}
if|if
condition|(
name|partialExpunge
operator|&&
name|maxDelCount
operator|>
literal|0
condition|)
block|{
comment|// expunge deletes
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
name|expungeCandidate
argument_list|,
name|expungeCandidate
operator|+
literal|1
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
DECL|method|createVarianceTable
specifier|private
name|double
index|[]
index|[]
name|createVarianceTable
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|int
name|last
parameter_list|,
name|int
name|maxNumSegments
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|maxMergeSegments
init|=
name|last
operator|-
name|maxNumSegments
operator|+
literal|1
decl_stmt|;
name|double
index|[]
index|[]
name|variance
init|=
operator|new
name|double
index|[
name|last
index|]
index|[
name|maxMergeSegments
index|]
decl_stmt|;
comment|// compute the optimal segment size
name|long
name|optSize
init|=
literal|0
decl_stmt|;
name|long
index|[]
name|sizeArr
init|=
operator|new
name|long
index|[
name|last
index|]
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
name|sizeArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sizeArr
index|[
name|i
index|]
operator|=
name|size
argument_list|(
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|optSize
operator|+=
name|sizeArr
index|[
name|i
index|]
expr_stmt|;
block|}
name|optSize
operator|=
operator|(
name|optSize
operator|/
name|maxNumSegments
operator|)
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
name|last
condition|;
name|i
operator|++
control|)
block|{
name|long
name|size
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
name|maxMergeSegments
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|i
operator|+
name|j
operator|)
operator|<
name|last
condition|)
block|{
name|size
operator|+=
name|sizeArr
index|[
name|i
operator|+
name|j
index|]
expr_stmt|;
name|double
name|residual
init|=
operator|(
operator|(
name|double
operator|)
name|size
operator|/
operator|(
name|double
operator|)
name|optSize
operator|)
operator|-
literal|1.0d
decl_stmt|;
name|variance
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|residual
operator|*
name|residual
expr_stmt|;
block|}
else|else
block|{
name|variance
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
block|}
block|}
block|}
return|return
name|variance
return|;
block|}
annotation|@
name|Override
DECL|method|findMergesToExpungeDeletes
specifier|public
name|MergeSpecification
name|findMergesToExpungeDeletes
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|int
name|numSegs
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numLargeSegs
init|=
operator|(
name|numSegs
operator|<
name|_numLargeSegments
condition|?
name|numSegs
else|:
name|_numLargeSegments
operator|)
decl_stmt|;
name|MergeSpecification
name|spec
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|numLargeSegs
operator|<
name|numSegs
condition|)
block|{
name|SegmentInfos
name|smallSegments
init|=
name|infos
operator|.
name|range
argument_list|(
name|numLargeSegs
argument_list|,
name|numSegs
argument_list|)
decl_stmt|;
name|spec
operator|=
name|super
operator|.
name|findMergesToExpungeDeletes
argument_list|(
name|smallSegments
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numLargeSegs
condition|;
name|i
operator|++
control|)
block|{
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
name|info
operator|.
name|hasDeletions
argument_list|()
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
name|i
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|getUseCompoundFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|spec
return|;
block|}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
name|MergeSpecification
name|findMerges
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numSegs
init|=
name|infos
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numLargeSegs
init|=
name|_numLargeSegments
decl_stmt|;
if|if
condition|(
name|numSegs
operator|<=
name|numLargeSegs
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|totalLargeSegSize
init|=
literal|0
decl_stmt|;
name|long
name|totalSmallSegSize
init|=
literal|0
decl_stmt|;
name|SegmentInfo
name|info
decl_stmt|;
comment|// compute the total size of large segments
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numLargeSegs
condition|;
name|i
operator|++
control|)
block|{
name|info
operator|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|totalLargeSegSize
operator|+=
name|size
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|// compute the total size of small segments
for|for
control|(
name|int
name|i
init|=
name|numLargeSegs
init|;
name|i
operator|<
name|numSegs
condition|;
name|i
operator|++
control|)
block|{
name|info
operator|=
name|infos
operator|.
name|info
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|totalSmallSegSize
operator|+=
name|size
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|long
name|targetSegSize
init|=
operator|(
name|totalLargeSegSize
operator|/
operator|(
name|numLargeSegs
operator|-
literal|1
operator|)
operator|)
decl_stmt|;
if|if
condition|(
name|targetSegSize
operator|<=
name|totalSmallSegSize
condition|)
block|{
comment|// the total size of small segments is big enough,
comment|// promote the small segments to a large segment and do balanced merge,
if|if
condition|(
name|totalSmallSegSize
operator|<
name|targetSegSize
operator|*
literal|2
condition|)
block|{
name|MergeSpecification
name|spec
init|=
name|findBalancedMerges
argument_list|(
name|infos
argument_list|,
name|numLargeSegs
argument_list|,
operator|(
name|numLargeSegs
operator|-
literal|1
operator|)
argument_list|,
name|_partialExpunge
argument_list|)
decl_stmt|;
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
comment|// should not happen
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
name|numLargeSegs
argument_list|,
name|numSegs
argument_list|)
argument_list|,
name|getUseCompoundFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|spec
return|;
block|}
else|else
block|{
return|return
name|findBalancedMerges
argument_list|(
name|infos
argument_list|,
name|numSegs
argument_list|,
name|numLargeSegs
argument_list|,
name|_partialExpunge
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|_maxSegments
operator|<
name|numSegs
condition|)
block|{
comment|// we have more than _maxSegments, merge small segments smaller than targetSegSize/4
name|MergeSpecification
name|spec
init|=
operator|new
name|MergeSpecification
argument_list|()
decl_stmt|;
name|int
name|startSeg
init|=
name|numLargeSegs
decl_stmt|;
name|long
name|sizeThreshold
init|=
operator|(
name|targetSegSize
operator|/
literal|4
operator|)
decl_stmt|;
while|while
condition|(
name|startSeg
operator|<
name|numSegs
condition|)
block|{
name|info
operator|=
name|infos
operator|.
name|info
argument_list|(
name|startSeg
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
argument_list|(
name|info
argument_list|)
operator|<
name|sizeThreshold
condition|)
break|break;
name|startSeg
operator|++
expr_stmt|;
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
name|startSeg
argument_list|,
name|numSegs
argument_list|)
argument_list|,
name|getUseCompoundFile
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|spec
return|;
block|}
else|else
block|{
comment|// apply the log merge policy to small segments.
name|SegmentInfos
name|smallSegments
init|=
name|infos
operator|.
name|range
argument_list|(
name|numLargeSegs
argument_list|,
name|numSegs
argument_list|)
decl_stmt|;
name|MergeSpecification
name|spec
init|=
name|super
operator|.
name|findMerges
argument_list|(
name|smallSegments
argument_list|)
decl_stmt|;
if|if
condition|(
name|_partialExpunge
condition|)
block|{
name|OneMerge
name|expunge
init|=
name|findOneSegmentToExpunge
argument_list|(
name|infos
argument_list|,
name|numLargeSegs
argument_list|)
decl_stmt|;
if|if
condition|(
name|expunge
operator|!=
literal|null
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
name|spec
operator|.
name|add
argument_list|(
name|expunge
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|spec
return|;
block|}
block|}
DECL|method|findOneSegmentToExpunge
specifier|private
name|OneMerge
name|findOneSegmentToExpunge
parameter_list|(
name|SegmentInfos
name|infos
parameter_list|,
name|int
name|maxNumSegments
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|expungeCandidate
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|maxDelCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|maxNumSegments
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
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
name|int
name|delCount
init|=
name|info
operator|.
name|getDelCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|delCount
operator|>
name|maxDelCount
condition|)
block|{
name|expungeCandidate
operator|=
name|i
expr_stmt|;
name|maxDelCount
operator|=
name|delCount
expr_stmt|;
block|}
block|}
if|if
condition|(
name|maxDelCount
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|OneMerge
argument_list|(
name|infos
operator|.
name|range
argument_list|(
name|expungeCandidate
argument_list|,
name|expungeCandidate
operator|+
literal|1
argument_list|)
argument_list|,
name|getUseCompoundFile
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|class|MergePolicyParams
specifier|public
specifier|static
class|class
name|MergePolicyParams
block|{
DECL|field|_numLargeSegments
specifier|private
name|int
name|_numLargeSegments
decl_stmt|;
DECL|field|_maxSmallSegments
specifier|private
name|int
name|_maxSmallSegments
decl_stmt|;
DECL|field|_doPartialExpunge
specifier|private
name|boolean
name|_doPartialExpunge
decl_stmt|;
DECL|field|_mergeFactor
specifier|private
name|int
name|_mergeFactor
decl_stmt|;
DECL|field|_useCompoundFile
specifier|private
name|boolean
name|_useCompoundFile
decl_stmt|;
DECL|field|_maxMergeDocs
specifier|private
name|int
name|_maxMergeDocs
decl_stmt|;
DECL|method|MergePolicyParams
specifier|public
name|MergePolicyParams
parameter_list|()
block|{
name|_useCompoundFile
operator|=
literal|true
expr_stmt|;
name|_doPartialExpunge
operator|=
literal|false
expr_stmt|;
name|_numLargeSegments
operator|=
name|DEFAULT_NUM_LARGE_SEGMENTS
expr_stmt|;
name|_maxSmallSegments
operator|=
literal|2
operator|*
name|LogMergePolicy
operator|.
name|DEFAULT_MERGE_FACTOR
expr_stmt|;
name|_maxSmallSegments
operator|=
name|_numLargeSegments
operator|+
name|_maxSmallSegments
expr_stmt|;
name|_mergeFactor
operator|=
name|LogMergePolicy
operator|.
name|DEFAULT_MERGE_FACTOR
expr_stmt|;
name|_maxMergeDocs
operator|=
name|LogMergePolicy
operator|.
name|DEFAULT_MAX_MERGE_DOCS
expr_stmt|;
block|}
DECL|method|setNumLargeSegments
specifier|public
name|void
name|setNumLargeSegments
parameter_list|(
name|int
name|numLargeSegments
parameter_list|)
block|{
name|_numLargeSegments
operator|=
name|numLargeSegments
expr_stmt|;
block|}
DECL|method|getNumLargeSegments
specifier|public
name|int
name|getNumLargeSegments
parameter_list|()
block|{
return|return
name|_numLargeSegments
return|;
block|}
DECL|method|setMaxSmallSegments
specifier|public
name|void
name|setMaxSmallSegments
parameter_list|(
name|int
name|maxSmallSegments
parameter_list|)
block|{
name|_maxSmallSegments
operator|=
name|maxSmallSegments
expr_stmt|;
block|}
DECL|method|getMaxSmallSegments
specifier|public
name|int
name|getMaxSmallSegments
parameter_list|()
block|{
return|return
name|_maxSmallSegments
return|;
block|}
DECL|method|setPartialExpunge
specifier|public
name|void
name|setPartialExpunge
parameter_list|(
name|boolean
name|doPartialExpunge
parameter_list|)
block|{
name|_doPartialExpunge
operator|=
name|doPartialExpunge
expr_stmt|;
block|}
DECL|method|getPartialExpunge
specifier|public
name|boolean
name|getPartialExpunge
parameter_list|()
block|{
return|return
name|_doPartialExpunge
return|;
block|}
DECL|method|setMergeFactor
specifier|public
name|void
name|setMergeFactor
parameter_list|(
name|int
name|mergeFactor
parameter_list|)
block|{
name|_mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
block|}
DECL|method|getMergeFactor
specifier|public
name|int
name|getMergeFactor
parameter_list|()
block|{
return|return
name|_mergeFactor
return|;
block|}
DECL|method|setMaxMergeDocs
specifier|public
name|void
name|setMaxMergeDocs
parameter_list|(
name|int
name|maxMergeDocs
parameter_list|)
block|{
name|_maxMergeDocs
operator|=
name|maxMergeDocs
expr_stmt|;
block|}
DECL|method|getMaxMergeDocs
specifier|public
name|int
name|getMaxMergeDocs
parameter_list|()
block|{
return|return
name|_maxMergeDocs
return|;
block|}
DECL|method|setUseCompoundFile
specifier|public
name|void
name|setUseCompoundFile
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
name|_useCompoundFile
operator|=
name|useCompoundFile
expr_stmt|;
block|}
DECL|method|isUseCompoundFile
specifier|public
name|boolean
name|isUseCompoundFile
parameter_list|()
block|{
return|return
name|_useCompoundFile
return|;
block|}
block|}
block|}
end_class

end_unit

