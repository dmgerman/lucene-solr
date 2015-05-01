begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|DocValues
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
name|LeafReaderContext
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
name|NumericDocValues
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
name|DocIdSetIterator
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
name|Bits
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
name|BytesRef
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
name|FixedBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_class
DECL|class|UniqueAgg
specifier|public
class|class
name|UniqueAgg
extends|extends
name|StrAggValueSource
block|{
DECL|field|UNIQUE
specifier|public
specifier|static
name|String
name|UNIQUE
init|=
literal|"unique"
decl_stmt|;
comment|// internal constants used for aggregating values from multiple shards
DECL|field|VALS
specifier|static
name|String
name|VALS
init|=
literal|"vals"
decl_stmt|;
DECL|method|UniqueAgg
specifier|public
name|UniqueAgg
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|UNIQUE
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSlotAcc
specifier|public
name|SlotAcc
name|createSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
name|SchemaField
name|sf
init|=
name|fcontext
operator|.
name|qcontext
operator|.
name|searcher
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|getArg
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sf
operator|.
name|multiValued
argument_list|()
operator|||
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|multiValuedFieldCache
argument_list|()
condition|)
block|{
if|if
condition|(
name|sf
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
return|return
operator|new
name|UniqueMultiDvSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|getArg
argument_list|()
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|UniqueMultivaluedSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|getArg
argument_list|()
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|NumericAcc
argument_list|(
name|fcontext
argument_list|,
name|getArg
argument_list|()
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|UniqueSinglevaluedSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|getArg
argument_list|()
argument_list|,
name|numSlots
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createFacetMerger
specifier|public
name|FacetMerger
name|createFacetMerger
parameter_list|(
name|Object
name|prototype
parameter_list|)
block|{
return|return
operator|new
name|Merger
argument_list|()
return|;
block|}
DECL|class|Merger
specifier|private
specifier|static
class|class
name|Merger
extends|extends
name|FacetSortableMerger
block|{
DECL|field|answer
name|long
name|answer
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|sumUnique
name|long
name|sumUnique
decl_stmt|;
DECL|field|values
name|Set
argument_list|<
name|Object
argument_list|>
name|values
decl_stmt|;
DECL|field|sumAdded
name|long
name|sumAdded
decl_stmt|;
DECL|field|shardsMissingSum
name|long
name|shardsMissingSum
decl_stmt|;
DECL|field|shardsMissingMax
name|long
name|shardsMissingMax
decl_stmt|;
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Object
name|facetResult
parameter_list|)
block|{
name|SimpleOrderedMap
name|map
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|facetResult
decl_stmt|;
name|long
name|unique
init|=
operator|(
operator|(
name|Number
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"unique"
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|sumUnique
operator|+=
name|unique
expr_stmt|;
name|int
name|valsListed
init|=
literal|0
decl_stmt|;
name|List
name|vals
init|=
operator|(
name|List
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"vals"
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|vals
operator|.
name|size
argument_list|()
operator|*
literal|4
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|addAll
argument_list|(
name|vals
argument_list|)
expr_stmt|;
name|valsListed
operator|=
name|vals
operator|.
name|size
argument_list|()
expr_stmt|;
name|sumAdded
operator|+=
name|valsListed
expr_stmt|;
block|}
name|shardsMissingSum
operator|+=
name|unique
operator|-
name|valsListed
expr_stmt|;
name|shardsMissingMax
operator|=
name|Math
operator|.
name|max
argument_list|(
name|shardsMissingMax
argument_list|,
name|unique
operator|-
name|valsListed
argument_list|)
expr_stmt|;
comment|// TODO: somehow get& use the count in the bucket?
block|}
DECL|method|getLong
specifier|private
name|long
name|getLong
parameter_list|()
block|{
if|if
condition|(
name|answer
operator|>=
literal|0
condition|)
return|return
name|answer
return|;
name|answer
operator|=
name|values
operator|==
literal|null
condition|?
literal|0
else|:
name|values
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|answer
operator|==
literal|0
condition|)
block|{
comment|// either a real "0", or no values returned from shards
name|answer
operator|=
name|shardsMissingSum
expr_stmt|;
return|return
name|answer
return|;
block|}
name|double
name|factor
init|=
operator|(
operator|(
name|double
operator|)
name|values
operator|.
name|size
argument_list|()
operator|)
operator|/
name|sumAdded
decl_stmt|;
comment|// what fraction of listed values were unique
name|long
name|estimate
init|=
call|(
name|long
call|)
argument_list|(
name|shardsMissingSum
operator|*
name|factor
argument_list|)
decl_stmt|;
name|answer
operator|=
name|values
operator|.
name|size
argument_list|()
operator|+
name|estimate
expr_stmt|;
return|return
name|answer
return|;
block|}
annotation|@
name|Override
DECL|method|getMergedResult
specifier|public
name|Object
name|getMergedResult
parameter_list|()
block|{
return|return
name|getLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FacetSortableMerger
name|other
parameter_list|,
name|FacetField
operator|.
name|SortDirection
name|direction
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|getLong
argument_list|()
argument_list|,
operator|(
operator|(
name|Merger
operator|)
name|other
operator|)
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|LongSet
specifier|static
class|class
name|LongSet
block|{
DECL|field|LOAD_FACTOR
specifier|static
specifier|final
name|float
name|LOAD_FACTOR
init|=
literal|0.7f
decl_stmt|;
DECL|field|vals
name|long
index|[]
name|vals
decl_stmt|;
DECL|field|cardinality
name|int
name|cardinality
decl_stmt|;
DECL|field|mask
name|int
name|mask
decl_stmt|;
DECL|field|threshold
name|int
name|threshold
decl_stmt|;
DECL|field|zeroCount
name|int
name|zeroCount
decl_stmt|;
comment|// 1 if a 0 was collected
comment|/** sz must be a power of two */
DECL|method|LongSet
name|LongSet
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|vals
operator|=
operator|new
name|long
index|[
name|sz
index|]
expr_stmt|;
name|mask
operator|=
name|sz
operator|-
literal|1
expr_stmt|;
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|sz
operator|*
name|LOAD_FACTOR
argument_list|)
expr_stmt|;
block|}
DECL|method|add
name|void
name|add
parameter_list|(
name|long
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|0
condition|)
block|{
name|zeroCount
operator|=
literal|1
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|cardinality
operator|>=
name|threshold
condition|)
block|{
name|rehash
argument_list|()
expr_stmt|;
block|}
comment|// For floats: exponent bits start at bit 23 for single precision,
comment|// and bit 52 for double precision.
comment|// Many values will only have significant bits just to the right of that,
comment|// and the leftmost bits will all be zero.
comment|// For now, lets just settle to get first 8 significant mantissa bits of double or float in the lowest bits of our hash
comment|// The upper bits of our hash will be irrelevant.
name|int
name|h
init|=
call|(
name|int
call|)
argument_list|(
name|val
operator|+
operator|(
name|val
operator|>>>
literal|44
operator|)
operator|+
operator|(
name|val
operator|>>>
literal|15
operator|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|slot
init|=
name|h
operator|&
name|mask
init|;
condition|;
name|slot
operator|=
operator|(
name|slot
operator|+
literal|1
operator|)
operator|&
name|mask
control|)
block|{
name|long
name|v
init|=
name|vals
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
name|vals
index|[
name|slot
index|]
operator|=
name|val
expr_stmt|;
name|cardinality
operator|++
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|v
operator|==
name|val
condition|)
block|{
comment|// val is already in the set
break|break;
block|}
block|}
block|}
DECL|method|rehash
specifier|private
name|void
name|rehash
parameter_list|()
block|{
name|long
index|[]
name|oldVals
init|=
name|vals
decl_stmt|;
name|int
name|newCapacity
init|=
name|vals
operator|.
name|length
operator|<<
literal|1
decl_stmt|;
name|vals
operator|=
operator|new
name|long
index|[
name|newCapacity
index|]
expr_stmt|;
name|mask
operator|=
name|newCapacity
operator|-
literal|1
expr_stmt|;
name|threshold
operator|=
call|(
name|int
call|)
argument_list|(
name|newCapacity
operator|*
name|LOAD_FACTOR
argument_list|)
expr_stmt|;
name|cardinality
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|long
name|val
range|:
name|oldVals
control|)
block|{
if|if
condition|(
name|val
operator|!=
literal|0
condition|)
block|{
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|cardinality
name|int
name|cardinality
parameter_list|()
block|{
return|return
name|cardinality
operator|+
name|zeroCount
return|;
block|}
block|}
DECL|class|NumericAcc
class|class
name|NumericAcc
extends|extends
name|SlotAcc
block|{
DECL|field|sf
name|SchemaField
name|sf
decl_stmt|;
DECL|field|sets
name|LongSet
index|[]
name|sets
decl_stmt|;
DECL|field|values
name|NumericDocValues
name|values
decl_stmt|;
DECL|field|exists
name|Bits
name|exists
decl_stmt|;
DECL|method|NumericAcc
specifier|public
name|NumericAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|String
name|field
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|sf
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|sets
operator|=
operator|new
name|LongSet
index|[
name|numSlots
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|sets
operator|=
operator|new
name|LongSet
index|[
name|sets
operator|.
name|length
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|exists
operator|=
name|DocValues
operator|.
name|getDocsWithField
argument_list|(
name|readerContext
operator|.
name|reader
argument_list|()
argument_list|,
name|sf
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|val
init|=
name|values
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|0
operator|&&
operator|!
name|exists
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return;
block|}
name|LongSet
name|set
init|=
name|sets
index|[
name|slot
index|]
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|set
operator|=
name|sets
index|[
name|slot
index|]
operator|=
operator|new
name|LongSet
argument_list|(
literal|16
argument_list|)
expr_stmt|;
block|}
comment|// TODO: could handle 0s at this level too
name|set
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fcontext
operator|.
name|isShard
argument_list|()
condition|)
block|{
return|return
name|getShardValue
argument_list|(
name|slot
argument_list|)
return|;
block|}
return|return
name|getCardinality
argument_list|(
name|slot
argument_list|)
return|;
block|}
DECL|method|getCardinality
specifier|private
name|int
name|getCardinality
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|LongSet
name|set
init|=
name|sets
index|[
name|slot
index|]
decl_stmt|;
return|return
name|set
operator|==
literal|null
condition|?
literal|0
else|:
name|set
operator|.
name|cardinality
argument_list|()
return|;
block|}
DECL|method|getShardValue
specifier|public
name|Object
name|getShardValue
parameter_list|(
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|LongSet
name|set
init|=
name|sets
index|[
name|slot
index|]
decl_stmt|;
name|int
name|unique
init|=
name|getCardinality
argument_list|(
name|slot
argument_list|)
decl_stmt|;
name|SimpleOrderedMap
name|map
init|=
operator|new
name|SimpleOrderedMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"unique"
argument_list|,
name|unique
argument_list|)
expr_stmt|;
name|int
name|maxExplicit
init|=
literal|100
decl_stmt|;
comment|// TODO: make configurable
comment|// TODO: share values across buckets
if|if
condition|(
name|unique
operator|<=
name|maxExplicit
condition|)
block|{
name|List
name|lst
init|=
operator|new
name|ArrayList
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|unique
argument_list|,
name|maxExplicit
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|set
operator|.
name|zeroCount
operator|>
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|long
name|val
range|:
name|set
operator|.
name|vals
control|)
block|{
if|if
condition|(
name|val
operator|!=
literal|0
condition|)
block|{
name|lst
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|map
operator|.
name|add
argument_list|(
literal|"vals"
argument_list|,
name|lst
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|getCardinality
argument_list|(
name|slotA
argument_list|)
operator|-
name|getCardinality
argument_list|(
name|slotB
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

