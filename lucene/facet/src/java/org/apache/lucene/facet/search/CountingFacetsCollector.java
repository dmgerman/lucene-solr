begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|Arrays
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
name|Comparator
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
operator|.
name|Entry
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|CategoryListParams
operator|.
name|OrdinalPolicy
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|CountFacetRequest
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
operator|.
name|SortBy
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetRequest
operator|.
name|SortOrder
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
name|facet
operator|.
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResult
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
name|facet
operator|.
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|ParallelTaxonomyArrays
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
name|AtomicReaderContext
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
name|DocValues
operator|.
name|Source
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
name|Collector
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
name|Scorer
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
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
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
name|encoding
operator|.
name|DGapVInt8IntDecoder
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A {@link Collector} which counts facets associated with matching documents.  * This {@link Collector} can be used only in the following conditions:  *<ul>  *<li>All {@link FacetRequest requests} must be {@link CountFacetRequest}, with  * their {@link FacetRequest#getDepth() depth} equals to 1, and  * {@link FacetRequest#getNumLabel()} must be&ge; than  * {@link FacetRequest#getNumResults()}. Also, their sorting options must be  * {@link SortOrder#DESCENDING} and {@link SortBy#VALUE} (although ties are  * broken by ordinals).  *<li>Partitions should be disabled (  * {@link FacetIndexingParams#getPartitionSize()} should return  * Integer.MAX_VALUE).  *<li>There can be only one {@link CategoryListParams} in the  * {@link FacetIndexingParams}, with {@link DGapVInt8IntDecoder}.  *</ul>  *   *<p>  *<b>NOTE:</b> this colletro uses {@link DocValues#getSource()} by default,  * which pre-loads the values into memory. If your application cannot afford the  * RAM, you should use  * {@link #CountingFacetsCollector(FacetSearchParams, TaxonomyReader, FacetArrays, boolean)}  * and specify to use a direct source (corresponds to  * {@link DocValues#getDirectSource()}).  *   *<p>  *<b>NOTE:</b> this collector supports category lists that were indexed with  * {@link OrdinalPolicy#NO_PARENTS}, by counting up the parents too, after  * resolving the leafs counts. Note though that it is your responsibility to  * guarantee that indeed a document wasn't indexed with two categories that  * share a common parent, or otherwise the parent's count will be wrong.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|CountingFacetsCollector
specifier|public
class|class
name|CountingFacetsCollector
extends|extends
name|FacetsCollector
block|{
DECL|field|fsp
specifier|private
specifier|final
name|FacetSearchParams
name|fsp
decl_stmt|;
DECL|field|ordinalPolicy
specifier|private
specifier|final
name|OrdinalPolicy
name|ordinalPolicy
decl_stmt|;
DECL|field|taxoReader
specifier|private
specifier|final
name|TaxonomyReader
name|taxoReader
decl_stmt|;
DECL|field|buf
specifier|private
specifier|final
name|BytesRef
name|buf
init|=
operator|new
name|BytesRef
argument_list|(
literal|32
argument_list|)
decl_stmt|;
DECL|field|facetArrays
specifier|private
specifier|final
name|FacetArrays
name|facetArrays
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
DECL|field|facetsField
specifier|private
specifier|final
name|String
name|facetsField
decl_stmt|;
DECL|field|useDirectSource
specifier|private
specifier|final
name|boolean
name|useDirectSource
decl_stmt|;
DECL|field|matchingDocs
specifier|private
specifier|final
name|HashMap
argument_list|<
name|Source
argument_list|,
name|FixedBitSet
argument_list|>
name|matchingDocs
init|=
operator|new
name|HashMap
argument_list|<
name|Source
argument_list|,
name|FixedBitSet
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|facetsValues
specifier|private
name|DocValues
name|facetsValues
decl_stmt|;
DECL|field|bits
specifier|private
name|FixedBitSet
name|bits
decl_stmt|;
DECL|method|CountingFacetsCollector
specifier|public
name|CountingFacetsCollector
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|)
block|{
name|this
argument_list|(
name|fsp
argument_list|,
name|taxoReader
argument_list|,
operator|new
name|FacetArrays
argument_list|(
name|taxoReader
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|CountingFacetsCollector
specifier|public
name|CountingFacetsCollector
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetArrays
name|facetArrays
parameter_list|,
name|boolean
name|useDirectSource
parameter_list|)
block|{
assert|assert
name|facetArrays
operator|.
name|arrayLength
operator|>=
name|taxoReader
operator|.
name|getSize
argument_list|()
operator|:
literal|"too small facet array"
assert|;
assert|assert
name|assertParams
argument_list|(
name|fsp
argument_list|)
operator|==
literal|null
operator|:
name|assertParams
argument_list|(
name|fsp
argument_list|)
assert|;
name|this
operator|.
name|fsp
operator|=
name|fsp
expr_stmt|;
name|CategoryListParams
name|clp
init|=
name|fsp
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fsp
operator|.
name|facetRequests
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
name|this
operator|.
name|ordinalPolicy
operator|=
name|clp
operator|.
name|getOrdinalPolicy
argument_list|()
expr_stmt|;
name|this
operator|.
name|facetsField
operator|=
name|clp
operator|.
name|field
expr_stmt|;
name|this
operator|.
name|taxoReader
operator|=
name|taxoReader
expr_stmt|;
name|this
operator|.
name|facetArrays
operator|=
name|facetArrays
expr_stmt|;
name|this
operator|.
name|counts
operator|=
name|facetArrays
operator|.
name|getIntArray
argument_list|()
expr_stmt|;
name|this
operator|.
name|useDirectSource
operator|=
name|useDirectSource
expr_stmt|;
block|}
comment|/**    * Asserts that this {@link FacetsCollector} can handle the given    * {@link FacetSearchParams}. Returns {@code null} if true, otherwise an error    * message.    */
DECL|method|assertParams
specifier|static
name|String
name|assertParams
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|)
block|{
comment|// verify that all facet requests are CountFacetRequest
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|fr
operator|instanceof
name|CountFacetRequest
operator|)
condition|)
block|{
return|return
literal|"all FacetRequests must be CountFacetRequest"
return|;
block|}
if|if
condition|(
name|fr
operator|.
name|getDepth
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
literal|"all requests must be of depth 1"
return|;
block|}
if|if
condition|(
name|fr
operator|.
name|getNumLabel
argument_list|()
operator|<
name|fr
operator|.
name|getNumResults
argument_list|()
condition|)
block|{
return|return
literal|"this Collector always labels all requested results"
return|;
block|}
if|if
condition|(
name|fr
operator|.
name|getSortOrder
argument_list|()
operator|!=
name|SortOrder
operator|.
name|DESCENDING
condition|)
block|{
return|return
literal|"this Collector always sorts results in descending order"
return|;
block|}
if|if
condition|(
name|fr
operator|.
name|getSortBy
argument_list|()
operator|!=
name|SortBy
operator|.
name|VALUE
condition|)
block|{
return|return
literal|"this Collector always sorts by results' values"
return|;
block|}
block|}
comment|// verify that there's only one CategoryListParams for all FacetRequests
name|CategoryListParams
name|clp
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
name|CategoryListParams
name|cpclp
init|=
name|fsp
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|clp
operator|==
literal|null
condition|)
block|{
name|clp
operator|=
name|cpclp
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clp
operator|!=
name|cpclp
condition|)
block|{
return|return
literal|"all FacetRequests must belong to the same CategoryListParams"
return|;
block|}
block|}
if|if
condition|(
name|clp
operator|==
literal|null
condition|)
block|{
return|return
literal|"at least one FacetRequest must be defined"
return|;
block|}
comment|// verify DGapVInt decoder
if|if
condition|(
name|clp
operator|.
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
operator|.
name|getClass
argument_list|()
operator|!=
name|DGapVInt8IntDecoder
operator|.
name|class
condition|)
block|{
return|return
literal|"this Collector supports only DGap + VInt encoding"
return|;
block|}
comment|// verify that partitions are disabled
if|if
condition|(
name|fsp
operator|.
name|indexingParams
operator|.
name|getPartitionSize
argument_list|()
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
literal|"this Collector does not support partitions"
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|facetsValues
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|docValues
argument_list|(
name|facetsField
argument_list|)
expr_stmt|;
if|if
condition|(
name|facetsValues
operator|!=
literal|null
condition|)
block|{
name|Source
name|facetSource
init|=
name|useDirectSource
condition|?
name|facetsValues
operator|.
name|getDirectSource
argument_list|()
else|:
name|facetsValues
operator|.
name|getSource
argument_list|()
decl_stmt|;
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|matchingDocs
operator|.
name|put
argument_list|(
name|facetSource
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|facetsValues
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|countFacets
specifier|private
name|void
name|countFacets
parameter_list|()
block|{
for|for
control|(
name|Entry
argument_list|<
name|Source
argument_list|,
name|FixedBitSet
argument_list|>
name|entry
range|:
name|matchingDocs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Source
name|facetsSource
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|FixedBitSet
name|bits
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
name|int
name|length
init|=
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|length
operator|&&
operator|(
name|doc
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|doc
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|facetsSource
operator|.
name|getBytes
argument_list|(
name|doc
argument_list|,
name|buf
argument_list|)
expr_stmt|;
if|if
condition|(
name|buf
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// this document has facets
name|int
name|upto
init|=
name|buf
operator|.
name|offset
operator|+
name|buf
operator|.
name|length
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
name|int
name|offset
init|=
name|buf
operator|.
name|offset
decl_stmt|;
name|int
name|prev
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|upto
condition|)
block|{
name|byte
name|b
init|=
name|buf
operator|.
name|bytes
index|[
name|offset
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
block|{
name|prev
operator|=
name|ord
operator|=
operator|(
operator|(
name|ord
operator|<<
literal|7
operator|)
operator||
name|b
operator|)
operator|+
name|prev
expr_stmt|;
name|counts
index|[
name|ord
index|]
operator|++
expr_stmt|;
name|ord
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|ord
operator|=
operator|(
name|ord
operator|<<
literal|7
operator|)
operator||
operator|(
name|b
operator|&
literal|0x7F
operator|)
expr_stmt|;
block|}
block|}
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
block|}
DECL|method|countParents
specifier|private
name|void
name|countParents
parameter_list|(
name|int
index|[]
name|parents
parameter_list|)
block|{
comment|// counts[0] is the count of ROOT, which we don't care about and counts[1]
comment|// can only update counts[0], so we don't bother to visit it too. also,
comment|// since parents always have lower ordinals than their children, we traverse
comment|// the array backwards. this also allows us to update just the immediate
comment|// parent's count (actually, otherwise it would be a mistake).
for|for
control|(
name|int
name|i
init|=
name|counts
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>
literal|1
condition|;
name|i
operator|--
control|)
block|{
name|int
name|count
init|=
name|counts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|int
name|parent
init|=
name|parents
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|0
condition|)
block|{
name|counts
index|[
name|parent
index|]
operator|+=
name|count
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getFacetResults
specifier|public
specifier|synchronized
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getFacetResults
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
comment|// first, count matching documents' facets
name|countFacets
argument_list|()
expr_stmt|;
name|ParallelTaxonomyArrays
name|arrays
init|=
name|taxoReader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinalPolicy
operator|==
name|OrdinalPolicy
operator|.
name|NO_PARENTS
condition|)
block|{
comment|// need to count parents
name|countParents
argument_list|(
name|arrays
operator|.
name|parents
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// compute top-K
specifier|final
name|int
index|[]
name|children
init|=
name|arrays
operator|.
name|children
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|siblings
init|=
name|arrays
operator|.
name|siblings
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
name|int
name|rootOrd
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootOrd
operator|==
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
comment|// category does not exist
continue|continue;
block|}
name|FacetResultNode
name|root
init|=
operator|new
name|FacetResultNode
argument_list|()
decl_stmt|;
name|root
operator|.
name|ordinal
operator|=
name|rootOrd
expr_stmt|;
name|root
operator|.
name|label
operator|=
name|fr
operator|.
name|categoryPath
expr_stmt|;
name|root
operator|.
name|value
operator|=
name|counts
index|[
name|rootOrd
index|]
expr_stmt|;
if|if
condition|(
name|fr
operator|.
name|getNumResults
argument_list|()
operator|>
name|taxoReader
operator|.
name|getSize
argument_list|()
condition|)
block|{
comment|// specialize this case, user is interested in all available results
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|child
init|=
name|children
index|[
name|rootOrd
index|]
decl_stmt|;
while|while
condition|(
name|child
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|count
init|=
name|counts
index|[
name|child
index|]
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|FacetResultNode
name|node
init|=
operator|new
name|FacetResultNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|label
operator|=
name|taxoReader
operator|.
name|getPath
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|node
operator|.
name|value
operator|=
name|count
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|child
operator|=
name|siblings
index|[
name|child
index|]
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResultNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResultNode
name|o1
parameter_list|,
name|FacetResultNode
name|o2
parameter_list|)
block|{
name|int
name|value
init|=
call|(
name|int
call|)
argument_list|(
name|o2
operator|.
name|value
operator|-
name|o1
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
name|value
operator|=
name|o2
operator|.
name|ordinal
operator|-
name|o1
operator|.
name|ordinal
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|root
operator|.
name|subResults
operator|=
name|nodes
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
operator|new
name|FacetResult
argument_list|(
name|fr
argument_list|,
name|root
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// since we use sentinel objects, we cannot reuse PQ. but that's ok because it's not big
name|FacetResultNodeQueue
name|pq
init|=
operator|new
name|FacetResultNodeQueue
argument_list|(
name|fr
operator|.
name|getNumResults
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FacetResultNode
name|top
init|=
name|pq
operator|.
name|top
argument_list|()
decl_stmt|;
name|int
name|child
init|=
name|children
index|[
name|rootOrd
index|]
decl_stmt|;
name|int
name|numResults
init|=
literal|0
decl_stmt|;
comment|// count the number of results
while|while
condition|(
name|child
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|count
init|=
name|counts
index|[
name|child
index|]
decl_stmt|;
if|if
condition|(
name|count
operator|>
name|top
operator|.
name|value
condition|)
block|{
name|top
operator|.
name|value
operator|=
name|count
expr_stmt|;
name|top
operator|.
name|ordinal
operator|=
name|child
expr_stmt|;
name|top
operator|=
name|pq
operator|.
name|updateTop
argument_list|()
expr_stmt|;
operator|++
name|numResults
expr_stmt|;
block|}
name|child
operator|=
name|siblings
index|[
name|child
index|]
expr_stmt|;
block|}
comment|// pop() the least (sentinel) elements
name|int
name|pqsize
init|=
name|pq
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|numResults
operator|<
name|pqsize
condition|?
name|numResults
else|:
name|pqsize
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pqsize
operator|-
name|size
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|// create the FacetResultNodes.
name|FacetResultNode
index|[]
name|subResults
init|=
operator|new
name|FacetResultNode
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
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
name|FacetResultNode
name|node
init|=
name|pq
operator|.
name|pop
argument_list|()
decl_stmt|;
name|node
operator|.
name|label
operator|=
name|taxoReader
operator|.
name|getPath
argument_list|(
name|node
operator|.
name|ordinal
argument_list|)
expr_stmt|;
name|subResults
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
name|root
operator|.
name|subResults
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|subResults
argument_list|)
expr_stmt|;
name|res
operator|.
name|add
argument_list|(
operator|new
name|FacetResult
argument_list|(
name|fr
argument_list|,
name|root
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
finally|finally
block|{
name|facetArrays
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
comment|// the actual work is done post-collection, so we always support out-of-order.
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|// TODO: review ResultSortUtils queues and check if we can reuse any of them here
comment|// and then alleviate the SortOrder/SortBy constraint
DECL|class|FacetResultNodeQueue
specifier|private
specifier|static
class|class
name|FacetResultNodeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|FacetResultNode
argument_list|>
block|{
DECL|method|FacetResultNodeQueue
specifier|public
name|FacetResultNodeQueue
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|boolean
name|prepopulate
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
name|prepopulate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSentinelObject
specifier|protected
name|FacetResultNode
name|getSentinelObject
parameter_list|()
block|{
return|return
operator|new
name|FacetResultNode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|FacetResultNode
name|a
parameter_list|,
name|FacetResultNode
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|<
name|b
operator|.
name|value
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|a
operator|.
name|value
operator|>
name|b
operator|.
name|value
condition|)
return|return
literal|false
return|;
comment|// both have the same value, break tie by ordinal
return|return
name|a
operator|.
name|ordinal
operator|<
name|b
operator|.
name|ordinal
return|;
block|}
block|}
block|}
end_class

end_unit

