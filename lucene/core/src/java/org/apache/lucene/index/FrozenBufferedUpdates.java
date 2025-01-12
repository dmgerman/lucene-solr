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
name|ArrayList
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
name|LinkedHashMap
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|BufferedUpdatesStream
operator|.
name|QueryAndLimit
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
name|DocValuesUpdate
operator|.
name|BinaryDocValuesUpdate
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
name|DocValuesUpdate
operator|.
name|NumericDocValuesUpdate
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
name|PrefixCodedTerms
operator|.
name|TermIterator
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
name|Query
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
name|ArrayUtil
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

begin_comment
comment|/**  * Holds buffered deletes and updates by term or query, once pushed. Pushed  * deletes/updates are write-once, so we shift to more memory efficient data  * structure to hold them. We don't hold docIDs because these are applied on  * flush.  */
end_comment

begin_class
DECL|class|FrozenBufferedUpdates
class|class
name|FrozenBufferedUpdates
block|{
comment|/* Query we often undercount (say 24 bytes), plus int. */
DECL|field|BYTES_PER_DEL_QUERY
specifier|final
specifier|static
name|int
name|BYTES_PER_DEL_QUERY
init|=
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|Integer
operator|.
name|BYTES
operator|+
literal|24
decl_stmt|;
comment|// Terms, in sorted order:
DECL|field|terms
specifier|final
name|PrefixCodedTerms
name|terms
decl_stmt|;
comment|// Parallel array of deleted query, and the docIDUpto for each
DECL|field|queries
specifier|final
name|Query
index|[]
name|queries
decl_stmt|;
DECL|field|queryLimits
specifier|final
name|int
index|[]
name|queryLimits
decl_stmt|;
comment|// numeric DV update term and their updates
DECL|field|numericDVUpdates
specifier|final
name|NumericDocValuesUpdate
index|[]
name|numericDVUpdates
decl_stmt|;
comment|// binary DV update term and their updates
DECL|field|binaryDVUpdates
specifier|final
name|BinaryDocValuesUpdate
index|[]
name|binaryDVUpdates
decl_stmt|;
DECL|field|bytesUsed
specifier|final
name|int
name|bytesUsed
decl_stmt|;
DECL|field|numTermDeletes
specifier|final
name|int
name|numTermDeletes
decl_stmt|;
DECL|field|gen
specifier|private
name|long
name|gen
init|=
operator|-
literal|1
decl_stmt|;
comment|// assigned by BufferedUpdatesStream once pushed
DECL|field|isSegmentPrivate
specifier|final
name|boolean
name|isSegmentPrivate
decl_stmt|;
comment|// set to true iff this frozen packet represents
comment|// a segment private deletes. in that case is should
comment|// only have Queries
DECL|method|FrozenBufferedUpdates
specifier|public
name|FrozenBufferedUpdates
parameter_list|(
name|BufferedUpdates
name|deletes
parameter_list|,
name|boolean
name|isSegmentPrivate
parameter_list|)
block|{
name|this
operator|.
name|isSegmentPrivate
operator|=
name|isSegmentPrivate
expr_stmt|;
assert|assert
operator|!
name|isSegmentPrivate
operator|||
name|deletes
operator|.
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|:
literal|"segment private package should only have del queries"
assert|;
name|Term
name|termsArray
index|[]
init|=
name|deletes
operator|.
name|terms
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|deletes
operator|.
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ArrayUtil
operator|.
name|timSort
argument_list|(
name|termsArray
argument_list|)
expr_stmt|;
name|PrefixCodedTerms
operator|.
name|Builder
name|builder
init|=
operator|new
name|PrefixCodedTerms
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|termsArray
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
name|terms
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|queries
operator|=
operator|new
name|Query
index|[
name|deletes
operator|.
name|queries
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|queryLimits
operator|=
operator|new
name|int
index|[
name|deletes
operator|.
name|queries
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Query
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|deletes
operator|.
name|queries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|queries
index|[
name|upto
index|]
operator|=
name|ent
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|queryLimits
index|[
name|upto
index|]
operator|=
name|ent
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
comment|// TODO if a Term affects multiple fields, we could keep the updates key'd by Term
comment|// so that it maps to all fields it affects, sorted by their docUpto, and traverse
comment|// that Term only once, applying the update to all fields that still need to be
comment|// updated.
name|List
argument_list|<
name|NumericDocValuesUpdate
argument_list|>
name|allNumericUpdates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numericUpdatesSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LinkedHashMap
argument_list|<
name|Term
argument_list|,
name|NumericDocValuesUpdate
argument_list|>
name|numericUpdates
range|:
name|deletes
operator|.
name|numericUpdates
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|NumericDocValuesUpdate
name|update
range|:
name|numericUpdates
operator|.
name|values
argument_list|()
control|)
block|{
name|allNumericUpdates
operator|.
name|add
argument_list|(
name|update
argument_list|)
expr_stmt|;
name|numericUpdatesSize
operator|+=
name|update
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
block|}
name|numericDVUpdates
operator|=
name|allNumericUpdates
operator|.
name|toArray
argument_list|(
operator|new
name|NumericDocValuesUpdate
index|[
name|allNumericUpdates
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
comment|// TODO if a Term affects multiple fields, we could keep the updates key'd by Term
comment|// so that it maps to all fields it affects, sorted by their docUpto, and traverse
comment|// that Term only once, applying the update to all fields that still need to be
comment|// updated.
name|List
argument_list|<
name|BinaryDocValuesUpdate
argument_list|>
name|allBinaryUpdates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|binaryUpdatesSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LinkedHashMap
argument_list|<
name|Term
argument_list|,
name|BinaryDocValuesUpdate
argument_list|>
name|binaryUpdates
range|:
name|deletes
operator|.
name|binaryUpdates
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|BinaryDocValuesUpdate
name|update
range|:
name|binaryUpdates
operator|.
name|values
argument_list|()
control|)
block|{
name|allBinaryUpdates
operator|.
name|add
argument_list|(
name|update
argument_list|)
expr_stmt|;
name|binaryUpdatesSize
operator|+=
name|update
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
block|}
name|binaryDVUpdates
operator|=
name|allBinaryUpdates
operator|.
name|toArray
argument_list|(
operator|new
name|BinaryDocValuesUpdate
index|[
name|allBinaryUpdates
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
call|(
name|int
call|)
argument_list|(
name|terms
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|queries
operator|.
name|length
operator|*
name|BYTES_PER_DEL_QUERY
operator|+
name|numericUpdatesSize
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|numericDVUpdates
argument_list|)
operator|+
name|binaryUpdatesSize
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|binaryDVUpdates
argument_list|)
argument_list|)
expr_stmt|;
name|numTermDeletes
operator|=
name|deletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|setDelGen
specifier|public
name|void
name|setDelGen
parameter_list|(
name|long
name|gen
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|gen
operator|==
operator|-
literal|1
assert|;
name|this
operator|.
name|gen
operator|=
name|gen
expr_stmt|;
name|terms
operator|.
name|setDelGen
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
DECL|method|delGen
specifier|public
name|long
name|delGen
parameter_list|()
block|{
assert|assert
name|gen
operator|!=
operator|-
literal|1
assert|;
return|return
name|gen
return|;
block|}
DECL|method|termIterator
specifier|public
name|TermIterator
name|termIterator
parameter_list|()
block|{
return|return
name|terms
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|queriesIterable
specifier|public
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
name|queriesIterable
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|QueryAndLimit
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QueryAndLimit
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|QueryAndLimit
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|upto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|upto
operator|<
name|queries
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryAndLimit
name|next
parameter_list|()
block|{
name|QueryAndLimit
name|ret
init|=
operator|new
name|QueryAndLimit
argument_list|(
name|queries
index|[
name|upto
index|]
argument_list|,
name|queryLimits
index|[
name|upto
index|]
argument_list|)
decl_stmt|;
name|upto
operator|++
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
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
name|String
name|s
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|numTermDeletes
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" "
operator|+
name|numTermDeletes
operator|+
literal|" deleted terms (unique count="
operator|+
name|terms
operator|.
name|size
argument_list|()
operator|+
literal|")"
expr_stmt|;
block|}
if|if
condition|(
name|queries
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" "
operator|+
name|queries
operator|.
name|length
operator|+
literal|" deleted queries"
expr_stmt|;
block|}
if|if
condition|(
name|bytesUsed
operator|!=
literal|0
condition|)
block|{
name|s
operator|+=
literal|" bytesUsed="
operator|+
name|bytesUsed
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|method|any
name|boolean
name|any
parameter_list|()
block|{
return|return
name|terms
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
name|queries
operator|.
name|length
operator|>
literal|0
operator|||
name|numericDVUpdates
operator|.
name|length
operator|>
literal|0
operator|||
name|binaryDVUpdates
operator|.
name|length
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

