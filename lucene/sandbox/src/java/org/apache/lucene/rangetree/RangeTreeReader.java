begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.rangetree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|rangetree
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|SortedNumericDocValues
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
name|search
operator|.
name|DocIdSet
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
name|IndexInput
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
name|Accountable
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
name|DocIdSetBuilder
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
name|RamUsageEstimator
import|;
end_import

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
name|Arrays
import|;
end_import

begin_comment
comment|/** Handles intersection of a range with a numeric tree previously written with {@link RangeTreeWriter}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|RangeTreeReader
specifier|final
class|class
name|RangeTreeReader
implements|implements
name|Accountable
block|{
DECL|field|blockFPs
specifier|final
specifier|private
name|long
index|[]
name|blockFPs
decl_stmt|;
DECL|field|blockMinValues
specifier|final
specifier|private
name|long
index|[]
name|blockMinValues
decl_stmt|;
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|globalMaxValue
specifier|final
name|long
name|globalMaxValue
decl_stmt|;
DECL|field|approxDocsPerBlock
specifier|final
name|int
name|approxDocsPerBlock
decl_stmt|;
DECL|method|RangeTreeReader
specifier|public
name|RangeTreeReader
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read index:
name|int
name|numLeaves
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|approxDocsPerBlock
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|blockMinValues
operator|=
operator|new
name|long
index|[
name|numLeaves
index|]
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
name|numLeaves
condition|;
name|i
operator|++
control|)
block|{
name|blockMinValues
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|blockFPs
operator|=
operator|new
name|long
index|[
name|numLeaves
index|]
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
name|numLeaves
condition|;
name|i
operator|++
control|)
block|{
name|blockFPs
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
name|globalMaxValue
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
DECL|method|getMinValue
specifier|public
name|long
name|getMinValue
parameter_list|()
block|{
return|return
name|blockMinValues
index|[
literal|0
index|]
return|;
block|}
DECL|method|getMaxValue
specifier|public
name|long
name|getMaxValue
parameter_list|()
block|{
return|return
name|globalMaxValue
return|;
block|}
DECL|class|QueryState
specifier|private
specifier|static
specifier|final
class|class
name|QueryState
block|{
DECL|field|in
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|docs
specifier|final
name|DocIdSetBuilder
name|docs
decl_stmt|;
DECL|field|minValueIncl
specifier|final
name|long
name|minValueIncl
decl_stmt|;
DECL|field|maxValueIncl
specifier|final
name|long
name|maxValueIncl
decl_stmt|;
DECL|field|sndv
specifier|final
name|SortedNumericDocValues
name|sndv
decl_stmt|;
DECL|method|QueryState
specifier|public
name|QueryState
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|long
name|minValueIncl
parameter_list|,
name|long
name|maxValueIncl
parameter_list|,
name|SortedNumericDocValues
name|sndv
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|docs
operator|=
operator|new
name|DocIdSetBuilder
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|this
operator|.
name|minValueIncl
operator|=
name|minValueIncl
expr_stmt|;
name|this
operator|.
name|maxValueIncl
operator|=
name|maxValueIncl
expr_stmt|;
name|this
operator|.
name|sndv
operator|=
name|sndv
expr_stmt|;
block|}
block|}
DECL|method|intersect
specifier|public
name|DocIdSet
name|intersect
parameter_list|(
name|long
name|minIncl
parameter_list|,
name|long
name|maxIncl
parameter_list|,
name|SortedNumericDocValues
name|sndv
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|minIncl
operator|>
name|maxIncl
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY
return|;
block|}
if|if
condition|(
name|minIncl
operator|>
name|globalMaxValue
operator|||
name|maxIncl
operator|<
name|blockMinValues
index|[
literal|0
index|]
condition|)
block|{
return|return
name|DocIdSet
operator|.
name|EMPTY
return|;
block|}
name|QueryState
name|state
init|=
operator|new
name|QueryState
argument_list|(
name|in
operator|.
name|clone
argument_list|()
argument_list|,
name|maxDoc
argument_list|,
name|minIncl
argument_list|,
name|maxIncl
argument_list|,
name|sndv
argument_list|)
decl_stmt|;
name|int
name|startBlockIncl
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|blockMinValues
argument_list|,
name|minIncl
argument_list|)
decl_stmt|;
if|if
condition|(
name|startBlockIncl
operator|>=
literal|0
condition|)
block|{
comment|// There can be dups here, when the same value is added many
comment|// times.  Also, we need the first block whose min is< minIncl:
while|while
condition|(
name|startBlockIncl
operator|>
literal|0
operator|&&
name|blockMinValues
index|[
name|startBlockIncl
index|]
operator|==
name|minIncl
condition|)
block|{
name|startBlockIncl
operator|--
expr_stmt|;
block|}
block|}
else|else
block|{
name|startBlockIncl
operator|=
name|Math
operator|.
name|max
argument_list|(
operator|-
name|startBlockIncl
operator|-
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|endBlockIncl
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|blockMinValues
argument_list|,
name|maxIncl
argument_list|)
decl_stmt|;
if|if
condition|(
name|endBlockIncl
operator|>=
literal|0
condition|)
block|{
comment|// There can be dups here, when the same value is added many
comment|// times.  Also, we need the first block whose max is> minIncl:
while|while
condition|(
name|endBlockIncl
operator|<
name|blockMinValues
operator|.
name|length
operator|-
literal|1
operator|&&
name|blockMinValues
index|[
name|endBlockIncl
index|]
operator|==
name|maxIncl
condition|)
block|{
name|endBlockIncl
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|endBlockIncl
operator|=
name|Math
operator|.
name|max
argument_list|(
operator|-
name|endBlockIncl
operator|-
literal|2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
assert|assert
name|startBlockIncl
operator|<=
name|endBlockIncl
assert|;
name|state
operator|.
name|in
operator|.
name|seek
argument_list|(
name|blockFPs
index|[
name|startBlockIncl
index|]
argument_list|)
expr_stmt|;
comment|//System.out.println("startBlockIncl=" + startBlockIncl + " endBlockIncl=" + endBlockIncl);
comment|// Rough estimate of how many hits we'll see.  Note that in the degenerate case
comment|// (index same value many times) this could be a big over-estimate, but in the typical
comment|// case it's good:
name|state
operator|.
name|docs
operator|.
name|grow
argument_list|(
name|approxDocsPerBlock
operator|*
operator|(
name|endBlockIncl
operator|-
name|startBlockIncl
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|int
name|hitCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|block
init|=
name|startBlockIncl
init|;
name|block
operator|<=
name|endBlockIncl
condition|;
name|block
operator|++
control|)
block|{
name|boolean
name|doFilter
init|=
name|blockMinValues
index|[
name|block
index|]
operator|<=
name|minIncl
operator|||
name|block
operator|==
name|blockMinValues
operator|.
name|length
operator|-
literal|1
operator|||
name|blockMinValues
index|[
name|block
operator|+
literal|1
index|]
operator|>=
name|maxIncl
decl_stmt|;
comment|//System.out.println("  block=" + block + " min=" + blockMinValues[block] + " doFilter=" + doFilter);
name|int
name|newCount
decl_stmt|;
if|if
condition|(
name|doFilter
condition|)
block|{
comment|// We must filter each hit:
name|newCount
operator|=
name|addSome
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newCount
operator|=
name|addAll
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
name|hitCount
operator|+=
name|newCount
expr_stmt|;
block|}
comment|// NOTE: hitCount is an over-estimate in the multi-valued case:
return|return
name|state
operator|.
name|docs
operator|.
name|build
argument_list|(
name|hitCount
argument_list|)
return|;
block|}
comment|/** Adds all docs from the current block. */
DECL|method|addAll
specifier|private
name|int
name|addAll
parameter_list|(
name|QueryState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// How many values are stored in this leaf cell:
name|int
name|count
init|=
name|state
operator|.
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|state
operator|.
name|docs
operator|.
name|grow
argument_list|(
name|count
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docID
init|=
name|state
operator|.
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|state
operator|.
name|docs
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/** Adds docs from the current block, filtering each hit against the query min/max.  This    *  is only needed on the boundary blocks. */
DECL|method|addSome
specifier|private
name|int
name|addSome
parameter_list|(
name|QueryState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|hitCount
init|=
literal|0
decl_stmt|;
comment|// How many points are stored in this leaf cell:
name|int
name|count
init|=
name|state
operator|.
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|state
operator|.
name|docs
operator|.
name|grow
argument_list|(
name|count
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docID
init|=
name|state
operator|.
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|state
operator|.
name|sndv
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
comment|// How many values this doc has:
name|int
name|docValueCount
init|=
name|state
operator|.
name|sndv
operator|.
name|count
argument_list|()
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
name|docValueCount
condition|;
name|j
operator|++
control|)
block|{
name|long
name|value
init|=
name|state
operator|.
name|sndv
operator|.
name|valueAt
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|>=
name|state
operator|.
name|minValueIncl
operator|&&
name|value
operator|<=
name|state
operator|.
name|maxValueIncl
condition|)
block|{
name|state
operator|.
name|docs
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|hitCount
operator|++
expr_stmt|;
comment|// Stop processing values for this doc:
break|break;
block|}
block|}
block|}
return|return
name|hitCount
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|blockMinValues
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
operator|+
name|blockFPs
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
return|;
block|}
block|}
end_class

end_unit

