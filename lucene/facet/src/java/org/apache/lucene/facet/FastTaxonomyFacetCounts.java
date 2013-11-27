begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|facet
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|FacetLabel
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
name|index
operator|.
name|BinaryDocValues
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

begin_comment
comment|/** Computes facets counts, assuming the default encoding  *  into DocValues was used.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|FastTaxonomyFacetCounts
specifier|public
class|class
name|FastTaxonomyFacetCounts
extends|extends
name|TaxonomyFacets
block|{
DECL|field|counts
specifier|private
specifier|final
name|int
index|[]
name|counts
decl_stmt|;
comment|/** Create {@code FastTaxonomyFacetCounts}, which also    *  counts all facet labels. */
DECL|method|FastTaxonomyFacetCounts
specifier|public
name|FastTaxonomyFacetCounts
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
expr_stmt|;
block|}
comment|/** Create {@code FastTaxonomyFacetCounts}, using the    *  specified {@code indexFieldName} for ordinals.  Use    *  this if you had set {@link    *  FacetsConfig#setIndexFieldName} to change the index    *  field name for certain dimensions. */
DECL|method|FastTaxonomyFacetCounts
specifier|public
name|FastTaxonomyFacetCounts
parameter_list|(
name|String
name|indexFieldName
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|FacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|indexFieldName
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|taxoReader
operator|.
name|getSize
argument_list|()
index|]
expr_stmt|;
name|count
argument_list|(
name|fc
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|count
specifier|private
specifier|final
name|void
name|count
parameter_list|(
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|MatchingDocs
name|hits
range|:
name|matchingDocs
control|)
block|{
name|BinaryDocValues
name|dv
init|=
name|hits
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|indexFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
comment|// this reader does not have DocValues for the requested category list
continue|continue;
block|}
name|FixedBitSet
name|bits
init|=
name|hits
operator|.
name|bits
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|hits
operator|.
name|bits
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|doc
init|=
literal|0
decl_stmt|;
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|//System.out.println("count seg=" + hits.context.reader());
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
comment|//System.out.println("  doc=" + doc);
name|dv
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|scratch
operator|.
name|bytes
decl_stmt|;
name|int
name|end
init|=
name|scratch
operator|.
name|offset
operator|+
name|scratch
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
name|scratch
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
name|end
condition|)
block|{
name|byte
name|b
init|=
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
assert|assert
name|ord
operator|<
name|counts
operator|.
name|length
operator|:
literal|"ord="
operator|+
name|ord
operator|+
literal|" vs maxOrd="
operator|+
name|counts
operator|.
name|length
assert|;
comment|//System.out.println("    ord=" + ord);
operator|++
name|counts
index|[
name|ord
index|]
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
operator|++
name|doc
expr_stmt|;
block|}
block|}
comment|// nocommit we could do this lazily instead:
comment|// Rollup any necessary dims:
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FacetsConfig
operator|.
name|DimConfig
argument_list|>
name|ent
range|:
name|config
operator|.
name|getDimConfigs
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|dim
init|=
name|ent
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|FacetsConfig
operator|.
name|DimConfig
name|ft
init|=
name|ent
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|ft
operator|.
name|hierarchical
operator|&&
name|ft
operator|.
name|multiValued
operator|==
literal|false
condition|)
block|{
name|int
name|dimRootOrd
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
operator|new
name|FacetLabel
argument_list|(
name|dim
argument_list|)
argument_list|)
decl_stmt|;
comment|// It can be -1 if this field was declared in the
comment|// config but never indexed:
if|if
condition|(
name|dimRootOrd
operator|>
literal|0
condition|)
block|{
name|counts
index|[
name|dimRootOrd
index|]
operator|+=
name|rollup
argument_list|(
name|children
index|[
name|dimRootOrd
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|rollup
specifier|private
name|int
name|rollup
parameter_list|(
name|int
name|ord
parameter_list|)
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ord
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|int
name|childValue
init|=
name|counts
index|[
name|ord
index|]
operator|+
name|rollup
argument_list|(
name|children
index|[
name|ord
index|]
argument_list|)
decl_stmt|;
name|counts
index|[
name|ord
index|]
operator|=
name|childValue
expr_stmt|;
name|sum
operator|+=
name|childValue
expr_stmt|;
name|ord
operator|=
name|siblings
index|[
name|ord
index|]
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Override
DECL|method|getSpecificValue
specifier|public
name|Number
name|getSpecificValue
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyDim
argument_list|(
name|dim
argument_list|)
expr_stmt|;
name|int
name|ord
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
name|FacetLabel
operator|.
name|create
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|counts
index|[
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getTopChildren
specifier|public
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: can we factor this out?
if|if
condition|(
name|topN
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"topN must be> 0 (got: "
operator|+
name|topN
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|FacetsConfig
operator|.
name|DimConfig
name|dimConfig
init|=
name|verifyDim
argument_list|(
name|dim
argument_list|)
decl_stmt|;
comment|//System.out.println("ftfc.getTopChildren topN=" + topN);
name|FacetLabel
name|cp
init|=
name|FacetLabel
operator|.
name|create
argument_list|(
name|dim
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|int
name|dimOrd
init|=
name|taxoReader
operator|.
name|getOrdinal
argument_list|(
name|cp
argument_list|)
decl_stmt|;
if|if
condition|(
name|dimOrd
operator|==
operator|-
literal|1
condition|)
block|{
comment|//System.out.println("no ord for dim=" + dim + " path=" + path);
return|return
literal|null
return|;
block|}
name|TopOrdAndIntQueue
name|q
init|=
operator|new
name|TopOrdAndIntQueue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|taxoReader
operator|.
name|getSize
argument_list|()
argument_list|,
name|topN
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bottomCount
init|=
literal|0
decl_stmt|;
name|int
name|ord
init|=
name|children
index|[
name|dimOrd
index|]
decl_stmt|;
name|int
name|totCount
init|=
literal|0
decl_stmt|;
name|int
name|childCount
init|=
literal|0
decl_stmt|;
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
name|reuse
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|ord
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
comment|//System.out.println("  check ord=" + ord + " label=" + taxoReader.getPath(ord) + " topN=" + topN);
if|if
condition|(
name|counts
index|[
name|ord
index|]
operator|>
literal|0
condition|)
block|{
name|totCount
operator|+=
name|counts
index|[
name|ord
index|]
expr_stmt|;
name|childCount
operator|++
expr_stmt|;
if|if
condition|(
name|counts
index|[
name|ord
index|]
operator|>
name|bottomCount
condition|)
block|{
if|if
condition|(
name|reuse
operator|==
literal|null
condition|)
block|{
name|reuse
operator|=
operator|new
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
argument_list|()
expr_stmt|;
block|}
name|reuse
operator|.
name|ord
operator|=
name|ord
expr_stmt|;
name|reuse
operator|.
name|value
operator|=
name|counts
index|[
name|ord
index|]
expr_stmt|;
name|reuse
operator|=
name|q
operator|.
name|insertWithOverflow
argument_list|(
name|reuse
argument_list|)
expr_stmt|;
if|if
condition|(
name|q
operator|.
name|size
argument_list|()
operator|==
name|topN
condition|)
block|{
name|bottomCount
operator|=
name|q
operator|.
name|top
argument_list|()
operator|.
name|value
expr_stmt|;
block|}
block|}
block|}
name|ord
operator|=
name|siblings
index|[
name|ord
index|]
expr_stmt|;
block|}
if|if
condition|(
name|totCount
operator|==
literal|0
condition|)
block|{
comment|//System.out.println("  no matches");
return|return
literal|null
return|;
block|}
if|if
condition|(
name|dimConfig
operator|.
name|multiValued
condition|)
block|{
if|if
condition|(
name|dimConfig
operator|.
name|requireDimCount
condition|)
block|{
name|totCount
operator|=
name|counts
index|[
name|dimOrd
index|]
expr_stmt|;
block|}
else|else
block|{
comment|// Our sum'd count is not correct, in general:
name|totCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Our sum'd dim count is accurate, so we keep it
block|}
name|LabelAndValue
index|[]
name|labelValues
init|=
operator|new
name|LabelAndValue
index|[
name|q
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|labelValues
operator|.
name|length
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
name|TopOrdAndIntQueue
operator|.
name|OrdAndValue
name|ordAndValue
init|=
name|q
operator|.
name|pop
argument_list|()
decl_stmt|;
name|FacetLabel
name|child
init|=
name|taxoReader
operator|.
name|getPath
argument_list|(
name|ordAndValue
operator|.
name|ord
argument_list|)
decl_stmt|;
name|labelValues
index|[
name|i
index|]
operator|=
operator|new
name|LabelAndValue
argument_list|(
name|child
operator|.
name|components
index|[
name|cp
operator|.
name|length
index|]
argument_list|,
name|ordAndValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FacetResult
argument_list|(
name|totCount
argument_list|,
name|labelValues
argument_list|,
name|childCount
argument_list|)
return|;
block|}
block|}
end_class

end_unit

