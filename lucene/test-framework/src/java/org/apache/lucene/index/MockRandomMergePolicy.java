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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Random
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  * MergePolicy that makes random decisions for testing.  */
end_comment

begin_class
DECL|class|MockRandomMergePolicy
specifier|public
class|class
name|MockRandomMergePolicy
extends|extends
name|MergePolicy
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|doNonBulkMerges
name|boolean
name|doNonBulkMerges
init|=
literal|true
decl_stmt|;
DECL|method|MockRandomMergePolicy
specifier|public
name|MockRandomMergePolicy
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
comment|// fork a private random, since we are called
comment|// unpredictably from threads:
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**     * Set to true if sometimes readers to be merged should be wrapped in a FilterReader    * to mixup bulk merging.    */
DECL|method|setDoNonBulkMerges
specifier|public
name|void
name|setDoNonBulkMerges
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|doNonBulkMerges
operator|=
name|v
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|findMerges
specifier|public
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
block|{
name|MergeSpecification
name|mergeSpec
init|=
literal|null
decl_stmt|;
comment|//System.out.println("MRMP: findMerges sis=" + segmentInfos);
name|int
name|numSegments
init|=
name|segmentInfos
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|segments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|merging
init|=
name|writer
operator|.
name|getMergingSegments
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|sipc
range|:
name|segmentInfos
control|)
block|{
if|if
condition|(
operator|!
name|merging
operator|.
name|contains
argument_list|(
name|sipc
argument_list|)
condition|)
block|{
name|segments
operator|.
name|add
argument_list|(
name|sipc
argument_list|)
expr_stmt|;
block|}
block|}
name|numSegments
operator|=
name|segments
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|numSegments
operator|>
literal|1
operator|&&
operator|(
name|numSegments
operator|>
literal|30
operator|||
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
operator|)
condition|)
block|{
name|Collections
operator|.
name|shuffle
argument_list|(
name|segments
argument_list|,
name|random
argument_list|)
expr_stmt|;
comment|// TODO: sometimes make more than 1 merge?
name|mergeSpec
operator|=
operator|new
name|MergeSpecification
argument_list|()
expr_stmt|;
specifier|final
name|int
name|segsToMerge
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
name|numSegments
argument_list|)
decl_stmt|;
if|if
condition|(
name|doNonBulkMerges
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|mergeSpec
operator|.
name|add
argument_list|(
operator|new
name|MockRandomOneMerge
argument_list|(
name|segments
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|segsToMerge
argument_list|)
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mergeSpec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|segments
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|segsToMerge
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mergeSpec
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedMerges
specifier|public
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
block|{
specifier|final
name|List
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|eligibleSegments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|segmentInfos
control|)
block|{
if|if
condition|(
name|segmentsToMerge
operator|.
name|containsKey
argument_list|(
name|info
argument_list|)
condition|)
block|{
name|eligibleSegments
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|//System.out.println("MRMP: findMerges sis=" + segmentInfos + " eligible=" + eligibleSegments);
name|MergeSpecification
name|mergeSpec
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|eligibleSegments
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
operator|(
name|eligibleSegments
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|isMerged
argument_list|(
name|segmentInfos
argument_list|,
name|eligibleSegments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|writer
argument_list|)
operator|==
literal|false
operator|)
condition|)
block|{
name|mergeSpec
operator|=
operator|new
name|MergeSpecification
argument_list|()
expr_stmt|;
comment|// Already shuffled having come out of a set but
comment|// shuffle again for good measure:
name|Collections
operator|.
name|shuffle
argument_list|(
name|eligibleSegments
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|eligibleSegments
operator|.
name|size
argument_list|()
condition|)
block|{
name|int
name|max
init|=
name|Math
operator|.
name|min
argument_list|(
literal|10
argument_list|,
name|eligibleSegments
operator|.
name|size
argument_list|()
operator|-
name|upto
argument_list|)
decl_stmt|;
name|int
name|inc
init|=
name|max
operator|<=
literal|2
condition|?
name|max
else|:
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
name|max
argument_list|)
decl_stmt|;
if|if
condition|(
name|doNonBulkMerges
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|mergeSpec
operator|.
name|add
argument_list|(
operator|new
name|MockRandomOneMerge
argument_list|(
name|eligibleSegments
operator|.
name|subList
argument_list|(
name|upto
argument_list|,
name|upto
operator|+
name|inc
argument_list|)
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mergeSpec
operator|.
name|add
argument_list|(
operator|new
name|OneMerge
argument_list|(
name|eligibleSegments
operator|.
name|subList
argument_list|(
name|upto
argument_list|,
name|upto
operator|+
name|inc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|upto
operator|+=
name|inc
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mergeSpec
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|OneMerge
name|merge
range|:
name|mergeSpec
operator|.
name|merges
control|)
block|{
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|merge
operator|.
name|segments
control|)
block|{
assert|assert
name|segmentsToMerge
operator|.
name|containsKey
argument_list|(
name|info
argument_list|)
assert|;
block|}
block|}
block|}
return|return
name|mergeSpec
return|;
block|}
annotation|@
name|Override
DECL|method|findForcedDeletesMerges
specifier|public
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
block|{
return|return
name|findMerges
argument_list|(
literal|null
argument_list|,
name|segmentInfos
argument_list|,
name|writer
argument_list|)
return|;
block|}
annotation|@
name|Override
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
comment|// 80% of the time we create CFS:
return|return
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|!=
literal|1
return|;
block|}
DECL|class|MockRandomOneMerge
specifier|static
class|class
name|MockRandomOneMerge
extends|extends
name|OneMerge
block|{
DECL|field|r
specifier|final
name|Random
name|r
decl_stmt|;
DECL|method|MockRandomOneMerge
name|MockRandomOneMerge
parameter_list|(
name|List
argument_list|<
name|SegmentCommitInfo
argument_list|>
name|segments
parameter_list|,
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|(
name|segments
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
comment|// wrap it (e.g. prevent bulk merge etc)
comment|// TODO: cut this over to FilterCodecReader api, we can explicitly
comment|// enable/disable bulk merge for portions of the index we want.
name|int
name|thingToDo
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
if|if
condition|(
name|thingToDo
operator|==
literal|0
condition|)
block|{
comment|// simple no-op FilterReader
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: MockRandomMergePolicy now swaps in a SlowCodecReaderWrapper for merging reader="
operator|+
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
operator|new
name|FilterLeafReader
argument_list|(
name|reader
argument_list|)
block|{}
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|thingToDo
operator|==
literal|1
condition|)
block|{
comment|// renumber fields
comment|// NOTE: currently this only "blocks" bulk merges just by
comment|// being a FilterReader. But it might find bugs elsewhere,
comment|// and maybe the situation can be improved in the future.
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: MockRandomMergePolicy now swaps in a MismatchedLeafReader for merging reader="
operator|+
name|reader
argument_list|)
expr_stmt|;
block|}
return|return
name|SlowCodecReaderWrapper
operator|.
name|wrap
argument_list|(
operator|new
name|MismatchedLeafReader
argument_list|(
name|reader
argument_list|,
name|r
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// otherwise, reader is unchanged
return|return
name|reader
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

