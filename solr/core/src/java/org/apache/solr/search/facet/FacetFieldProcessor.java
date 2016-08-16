begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|HashMap
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocSet
import|;
end_import

begin_comment
comment|/**  * Facet processing based on field values. (not range nor by query)  * @see FacetField  */
end_comment

begin_class
DECL|class|FacetFieldProcessor
specifier|abstract
class|class
name|FacetFieldProcessor
extends|extends
name|FacetProcessor
argument_list|<
name|FacetField
argument_list|>
block|{
DECL|field|sf
name|SchemaField
name|sf
decl_stmt|;
DECL|field|indexOrderAcc
name|SlotAcc
name|indexOrderAcc
decl_stmt|;
DECL|field|effectiveMincount
name|int
name|effectiveMincount
decl_stmt|;
DECL|field|deferredAggs
name|Map
argument_list|<
name|String
argument_list|,
name|AggValueSource
argument_list|>
name|deferredAggs
decl_stmt|;
comment|// null if none
comment|// TODO: push any of this down to base class?
comment|//
comment|// For sort="x desc", collectAcc would point to "x", and sortAcc would also point to "x".
comment|// collectAcc would be used to accumulate all buckets, and sortAcc would be used to sort those buckets.
comment|//
DECL|field|collectAcc
name|SlotAcc
name|collectAcc
decl_stmt|;
comment|// Accumulator to collect across entire domain (in addition to the countAcc).  May be null.
DECL|field|sortAcc
name|SlotAcc
name|sortAcc
decl_stmt|;
comment|// Accumulator to use for sorting *only* (i.e. not used for collection). May be an alias of countAcc, collectAcc, or indexOrderAcc
DECL|field|otherAccs
name|SlotAcc
index|[]
name|otherAccs
decl_stmt|;
comment|// Accumulators that do not need to be calculated across all buckets.
DECL|field|allBucketsAcc
name|SpecialSlotAcc
name|allBucketsAcc
decl_stmt|;
comment|// this can internally refer to otherAccs and/or collectAcc. setNextReader should be called on otherAccs directly if they exist.
DECL|method|FacetFieldProcessor
name|FacetFieldProcessor
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|FacetField
name|freq
parameter_list|,
name|SchemaField
name|sf
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|this
operator|.
name|sf
operator|=
name|sf
expr_stmt|;
name|this
operator|.
name|effectiveMincount
operator|=
call|(
name|int
call|)
argument_list|(
name|fcontext
operator|.
name|isShard
argument_list|()
condition|?
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|freq
operator|.
name|mincount
argument_list|)
else|:
name|freq
operator|.
name|mincount
argument_list|)
expr_stmt|;
block|}
comment|// This is used to create accs for second phase (or to create accs for all aggs)
annotation|@
name|Override
DECL|method|createAccs
specifier|protected
name|void
name|createAccs
parameter_list|(
name|int
name|docCount
parameter_list|,
name|int
name|slotCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|accMap
operator|==
literal|null
condition|)
block|{
name|accMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|// allow a custom count acc to be used
if|if
condition|(
name|countAcc
operator|==
literal|null
condition|)
block|{
name|countAcc
operator|=
operator|new
name|CountSlotArrAcc
argument_list|(
name|fcontext
argument_list|,
name|slotCount
argument_list|)
expr_stmt|;
name|countAcc
operator|.
name|key
operator|=
literal|"count"
expr_stmt|;
block|}
if|if
condition|(
name|accs
operator|!=
literal|null
condition|)
block|{
comment|// reuse these accs, but reset them first
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accs
control|)
block|{
name|acc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
else|else
block|{
name|accs
operator|=
operator|new
name|SlotAcc
index|[
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
block|}
name|int
name|accIdx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AggValueSource
argument_list|>
name|entry
range|:
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SlotAcc
name|acc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|slotCount
operator|==
literal|1
condition|)
block|{
name|acc
operator|=
name|accMap
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|acc
operator|!=
literal|null
condition|)
block|{
name|acc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|acc
operator|==
literal|null
condition|)
block|{
name|acc
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|createSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|docCount
argument_list|,
name|slotCount
argument_list|)
expr_stmt|;
name|acc
operator|.
name|key
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|accMap
operator|.
name|put
argument_list|(
name|acc
operator|.
name|key
argument_list|,
name|acc
argument_list|)
expr_stmt|;
block|}
name|accs
index|[
name|accIdx
operator|++
index|]
operator|=
name|acc
expr_stmt|;
block|}
block|}
DECL|method|createCollectAcc
name|void
name|createCollectAcc
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
name|accMap
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|// we always count...
comment|// allow a subclass to set a custom counter.
if|if
condition|(
name|countAcc
operator|==
literal|null
condition|)
block|{
name|countAcc
operator|=
operator|new
name|CountSlotArrAcc
argument_list|(
name|fcontext
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|freq
operator|.
name|sortVariable
argument_list|)
condition|)
block|{
name|sortAcc
operator|=
name|countAcc
expr_stmt|;
name|deferredAggs
operator|=
name|freq
operator|.
name|getFacetStats
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|freq
operator|.
name|sortVariable
argument_list|)
condition|)
block|{
comment|// allow subclass to set indexOrderAcc first
if|if
condition|(
name|indexOrderAcc
operator|==
literal|null
condition|)
block|{
comment|// This sorting accumulator just goes by the slot number, so does not need to be collected
comment|// and hence does not need to find it's way into the accMap or accs array.
name|indexOrderAcc
operator|=
operator|new
name|SortSlotAcc
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
block|}
name|sortAcc
operator|=
name|indexOrderAcc
expr_stmt|;
name|deferredAggs
operator|=
name|freq
operator|.
name|getFacetStats
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|AggValueSource
name|sortAgg
init|=
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|get
argument_list|(
name|freq
operator|.
name|sortVariable
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortAgg
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|=
name|sortAgg
operator|.
name|createSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|numDocs
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
name|collectAcc
operator|.
name|key
operator|=
name|freq
operator|.
name|sortVariable
expr_stmt|;
comment|// TODO: improve this
block|}
name|sortAcc
operator|=
name|collectAcc
expr_stmt|;
name|deferredAggs
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|freq
operator|.
name|getFacetStats
argument_list|()
argument_list|)
expr_stmt|;
name|deferredAggs
operator|.
name|remove
argument_list|(
name|freq
operator|.
name|sortVariable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deferredAggs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|deferredAggs
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|needOtherAccs
init|=
name|freq
operator|.
name|allBuckets
decl_stmt|;
comment|// TODO: use for missing too...
if|if
condition|(
operator|!
name|needOtherAccs
condition|)
block|{
comment|// we may need them later, but we don't want to create them now
comment|// otherwise we won't know if we need to call setNextReader on them.
return|return;
block|}
comment|// create the deferred aggs up front for use by allBuckets
name|createOtherAccs
argument_list|(
name|numDocs
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|createOtherAccs
specifier|private
name|void
name|createOtherAccs
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|int
name|numSlots
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|otherAccs
operator|!=
literal|null
condition|)
block|{
comment|// reuse existing accumulators
for|for
control|(
name|SlotAcc
name|acc
range|:
name|otherAccs
control|)
block|{
name|acc
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// todo - make reset take numDocs and numSlots?
block|}
return|return;
block|}
name|int
name|numDeferred
init|=
name|deferredAggs
operator|==
literal|null
condition|?
literal|0
else|:
name|deferredAggs
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|numDeferred
operator|<=
literal|0
condition|)
return|return;
name|otherAccs
operator|=
operator|new
name|SlotAcc
index|[
name|numDeferred
index|]
expr_stmt|;
name|int
name|otherAccIdx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AggValueSource
argument_list|>
name|entry
range|:
name|deferredAggs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AggValueSource
name|agg
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|SlotAcc
name|acc
init|=
name|agg
operator|.
name|createSlotAcc
argument_list|(
name|fcontext
argument_list|,
name|numDocs
argument_list|,
name|numSlots
argument_list|)
decl_stmt|;
name|acc
operator|.
name|key
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|accMap
operator|.
name|put
argument_list|(
name|acc
operator|.
name|key
argument_list|,
name|acc
argument_list|)
expr_stmt|;
name|otherAccs
index|[
name|otherAccIdx
operator|++
index|]
operator|=
name|acc
expr_stmt|;
block|}
if|if
condition|(
name|numDeferred
operator|==
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// accs and otherAccs are the same...
name|accs
operator|=
name|otherAccs
expr_stmt|;
block|}
block|}
DECL|method|collectFirstPhase
name|int
name|collectFirstPhase
parameter_list|(
name|DocSet
name|docs
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|num
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|num
operator|=
name|collectAcc
operator|.
name|collect
argument_list|(
name|docs
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allBucketsAcc
operator|!=
literal|null
condition|)
block|{
name|num
operator|=
name|allBucketsAcc
operator|.
name|collect
argument_list|(
name|docs
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
return|return
name|num
operator|>=
literal|0
condition|?
name|num
else|:
name|docs
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|collectFirstPhase
name|void
name|collectFirstPhase
parameter_list|(
name|int
name|segDoc
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|.
name|collect
argument_list|(
name|segDoc
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allBucketsAcc
operator|!=
literal|null
condition|)
block|{
name|allBucketsAcc
operator|.
name|collect
argument_list|(
name|segDoc
argument_list|,
name|slot
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|fillBucket
name|void
name|fillBucket
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|target
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|slotNum
parameter_list|,
name|DocSet
name|subDomain
parameter_list|,
name|Query
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|target
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|<=
literal|0
operator|&&
operator|!
name|freq
operator|.
name|processEmpty
condition|)
return|return;
if|if
condition|(
name|collectAcc
operator|!=
literal|null
operator|&&
name|slotNum
operator|>=
literal|0
condition|)
block|{
name|collectAcc
operator|.
name|setValues
argument_list|(
name|target
argument_list|,
name|slotNum
argument_list|)
expr_stmt|;
block|}
name|createOtherAccs
argument_list|(
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|otherAccs
operator|==
literal|null
operator|&&
name|freq
operator|.
name|subFacets
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
if|if
condition|(
name|subDomain
operator|==
literal|null
condition|)
block|{
name|subDomain
operator|=
name|fcontext
operator|.
name|searcher
operator|.
name|getDocSet
argument_list|(
name|filter
argument_list|,
name|fcontext
operator|.
name|base
argument_list|)
expr_stmt|;
block|}
comment|// if no subFacets, we only need a DocSet
comment|// otherwise we need more?
comment|// TODO: save something generic like "slotNum" in the context and use that to implement things like filter exclusion if necessary?
comment|// Hmmm, but we need to look up some stuff anyway (for the label?)
comment|// have a method like "DocSet applyConstraint(facet context, DocSet parent)"
comment|// that's needed for domain changing things like joins anyway???
if|if
condition|(
name|otherAccs
operator|!=
literal|null
condition|)
block|{
comment|// do acc at a time (traversing domain each time) or do all accs for each doc?
for|for
control|(
name|SlotAcc
name|acc
range|:
name|otherAccs
control|)
block|{
name|acc
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// TODO: only needed if we previously used for allBuckets or missing
name|acc
operator|.
name|collect
argument_list|(
name|subDomain
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|acc
operator|.
name|setValues
argument_list|(
name|target
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
name|processSubs
argument_list|(
name|target
argument_list|,
name|filter
argument_list|,
name|subDomain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processStats
specifier|protected
name|void
name|processStats
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
parameter_list|,
name|DocSet
name|docs
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docCount
operator|==
literal|0
operator|&&
operator|!
name|freq
operator|.
name|processEmpty
operator|||
name|freq
operator|.
name|getFacetStats
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|bucket
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
return|return;
block|}
name|createAccs
argument_list|(
name|docCount
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|int
name|collected
init|=
name|collect
argument_list|(
name|docs
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// countAcc.incrementCount(0, collected);  // should we set the counton the acc instead of just passing it?
assert|assert
name|collected
operator|==
name|docCount
assert|;
name|addStats
argument_list|(
name|bucket
argument_list|,
name|collected
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// overrides but with different signature!
DECL|method|addStats
specifier|private
name|void
name|addStats
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|target
parameter_list|,
name|int
name|count
parameter_list|,
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
name|target
operator|.
name|add
argument_list|(
literal|"count"
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
operator|||
name|freq
operator|.
name|processEmpty
condition|)
block|{
for|for
control|(
name|SlotAcc
name|acc
range|:
name|accs
control|)
block|{
name|acc
operator|.
name|setValues
argument_list|(
name|target
argument_list|,
name|slotNum
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|setNextReader
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
comment|// base class calls this (for missing bucket...) ...  go over accs[] in that case
name|super
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|setNextReaderFirstPhase
name|void
name|setNextReaderFirstPhase
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherAccs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SlotAcc
name|acc
range|:
name|otherAccs
control|)
block|{
name|acc
operator|.
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Slot
specifier|static
class|class
name|Slot
block|{
DECL|field|slot
name|int
name|slot
decl_stmt|;
DECL|method|tiebreakCompare
specifier|public
name|int
name|tiebreakCompare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|slotB
operator|-
name|slotA
return|;
block|}
block|}
DECL|class|SpecialSlotAcc
specifier|static
class|class
name|SpecialSlotAcc
extends|extends
name|SlotAcc
block|{
DECL|field|collectAcc
name|SlotAcc
name|collectAcc
decl_stmt|;
DECL|field|otherAccs
name|SlotAcc
index|[]
name|otherAccs
decl_stmt|;
DECL|field|collectAccSlot
name|int
name|collectAccSlot
decl_stmt|;
DECL|field|otherAccsSlot
name|int
name|otherAccsSlot
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|method|SpecialSlotAcc
name|SpecialSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|SlotAcc
name|collectAcc
parameter_list|,
name|int
name|collectAccSlot
parameter_list|,
name|SlotAcc
index|[]
name|otherAccs
parameter_list|,
name|int
name|otherAccsSlot
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectAcc
operator|=
name|collectAcc
expr_stmt|;
name|this
operator|.
name|collectAccSlot
operator|=
name|collectAccSlot
expr_stmt|;
name|this
operator|.
name|otherAccs
operator|=
name|otherAccs
expr_stmt|;
name|this
operator|.
name|otherAccsSlot
operator|=
name|otherAccsSlot
expr_stmt|;
block|}
DECL|method|getCollectAccSlot
specifier|public
name|int
name|getCollectAccSlot
parameter_list|()
block|{
return|return
name|collectAccSlot
return|;
block|}
DECL|method|getOtherAccSlot
specifier|public
name|int
name|getOtherAccSlot
parameter_list|()
block|{
return|return
name|otherAccsSlot
return|;
block|}
DECL|method|getSpecialCount
name|long
name|getSpecialCount
parameter_list|()
block|{
return|return
name|count
return|;
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
assert|assert
name|slot
operator|!=
name|collectAccSlot
operator|||
name|slot
operator|<
literal|0
assert|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|collectAccSlot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherAccs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SlotAcc
name|otherAcc
range|:
name|otherAccs
control|)
block|{
name|otherAcc
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|otherAccsSlot
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// collectAcc and otherAccs will normally have setNextReader called directly on them.
comment|// This, however, will be used when collect(DocSet,slot) variant is used on this Acc.
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherAccs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SlotAcc
name|otherAcc
range|:
name|otherAccs
control|)
block|{
name|otherAcc
operator|.
name|setNextReader
argument_list|(
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|setValues
specifier|public
name|void
name|setValues
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
parameter_list|,
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|collectAcc
operator|!=
literal|null
condition|)
block|{
name|collectAcc
operator|.
name|setValues
argument_list|(
name|bucket
argument_list|,
name|collectAccSlot
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|otherAccs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SlotAcc
name|otherAcc
range|:
name|otherAccs
control|)
block|{
name|otherAcc
operator|.
name|setValues
argument_list|(
name|bucket
argument_list|,
name|otherAccsSlot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
comment|// reset should be called on underlying accs
comment|// TODO: but in case something does need to be done here, should we require this method to be called but do nothing for now?
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
comment|// someone else will call resize on collectAcc directly
if|if
condition|(
name|collectAccSlot
operator|>=
literal|0
condition|)
block|{
name|collectAccSlot
operator|=
name|resizer
operator|.
name|getNewSlot
argument_list|(
name|collectAccSlot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

