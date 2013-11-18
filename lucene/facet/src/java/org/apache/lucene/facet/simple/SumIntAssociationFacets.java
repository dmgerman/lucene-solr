begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|simple
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
name|simple
operator|.
name|SimpleFacetsCollector
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
comment|// nocommit jdoc that this assumes/requires the default encoding
end_comment

begin_class
DECL|class|SumIntAssociationFacets
specifier|public
class|class
name|SumIntAssociationFacets
extends|extends
name|Facets
block|{
DECL|field|facetsConfig
specifier|private
specifier|final
name|FacetsConfig
name|facetsConfig
decl_stmt|;
DECL|field|taxoReader
specifier|private
specifier|final
name|TaxonomyReader
name|taxoReader
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|field|facetsFieldName
specifier|private
specifier|final
name|String
name|facetsFieldName
decl_stmt|;
DECL|field|children
specifier|private
specifier|final
name|int
index|[]
name|children
decl_stmt|;
DECL|field|parents
specifier|private
specifier|final
name|int
index|[]
name|parents
decl_stmt|;
DECL|field|siblings
specifier|private
specifier|final
name|int
index|[]
name|siblings
decl_stmt|;
DECL|method|SumIntAssociationFacets
specifier|public
name|SumIntAssociationFacets
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|facetsConfig
parameter_list|,
name|SimpleFacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|FacetsConfig
operator|.
name|DEFAULT_INDEXED_FIELD_NAME
argument_list|,
name|taxoReader
argument_list|,
name|facetsConfig
argument_list|,
name|fc
argument_list|)
expr_stmt|;
block|}
DECL|method|SumIntAssociationFacets
specifier|public
name|SumIntAssociationFacets
parameter_list|(
name|String
name|facetsFieldName
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|facetsConfig
parameter_list|,
name|SimpleFacetsCollector
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|facetsFieldName
operator|=
name|facetsFieldName
expr_stmt|;
name|this
operator|.
name|taxoReader
operator|=
name|taxoReader
expr_stmt|;
name|this
operator|.
name|facetsConfig
operator|=
name|facetsConfig
expr_stmt|;
name|ParallelTaxonomyArrays
name|pta
init|=
name|taxoReader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
decl_stmt|;
name|children
operator|=
name|pta
operator|.
name|children
argument_list|()
expr_stmt|;
name|parents
operator|=
name|pta
operator|.
name|parents
argument_list|()
expr_stmt|;
name|siblings
operator|=
name|pta
operator|.
name|siblings
argument_list|()
expr_stmt|;
name|values
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
name|sumValues
argument_list|(
name|fc
operator|.
name|getMatchingDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|sumValues
specifier|private
specifier|final
name|void
name|sumValues
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
comment|//System.out.println("count matchingDocs=" + matchingDocs + " facetsField=" + facetsFieldName);
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
name|facetsFieldName
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
comment|// nocommit use OrdinalsReader?  but, add a
comment|// BytesRef getAssociation()?
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
name|offset
init|=
name|scratch
operator|.
name|offset
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|end
condition|)
block|{
name|int
name|ord
init|=
operator|(
operator|(
name|bytes
index|[
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|int
name|value
init|=
operator|(
operator|(
name|bytes
index|[
name|offset
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xFF
operator|)
decl_stmt|;
name|offset
operator|+=
literal|4
expr_stmt|;
name|values
index|[
name|ord
index|]
operator|+=
name|value
expr_stmt|;
block|}
operator|++
name|doc
expr_stmt|;
block|}
block|}
comment|// nocommit we could do this lazily instead:
comment|// Rollup any necessary dims:
comment|// nocommit should we rollup?
comment|/*     for(Map.Entry<String,FacetsConfig.DimConfig> ent : facetsConfig.getDimConfigs().entrySet()) {       String dim = ent.getKey();       FacetsConfig.DimConfig ft = ent.getValue();       if (ft.hierarchical&& ft.multiValued == false) {         int dimRootOrd = taxoReader.getOrdinal(new FacetLabel(dim));         // It can be -1 if this field was declared in the         // facetsConfig but never indexed:         if (dimRootOrd> 0) {           counts[dimRootOrd] += rollup(children[dimRootOrd]);         }       }     }     */
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
name|values
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
name|values
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
comment|/** Return the count for a specific path.  Returns -1 if    *  this path doesn't exist, else the count. */
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
name|values
index|[
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getTopChildren
specifier|public
name|SimpleFacetResult
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
name|ord
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
name|ord
operator|==
operator|-
literal|1
condition|)
block|{
comment|//System.out.println("no ord for path=" + path);
return|return
literal|null
return|;
block|}
return|return
name|getTopChildren
argument_list|(
name|cp
argument_list|,
name|ord
argument_list|,
name|topN
argument_list|)
return|;
block|}
DECL|method|getTopChildren
specifier|private
name|SimpleFacetResult
name|getTopChildren
parameter_list|(
name|FacetLabel
name|path
parameter_list|,
name|int
name|dimOrd
parameter_list|,
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
name|TopOrdCountQueue
name|q
init|=
operator|new
name|TopOrdCountQueue
argument_list|(
name|topN
argument_list|)
decl_stmt|;
name|int
name|bottomValue
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
name|long
name|sumValue
init|=
literal|0
decl_stmt|;
name|TopOrdCountQueue
operator|.
name|OrdAndCount
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
if|if
condition|(
name|values
index|[
name|ord
index|]
operator|>
literal|0
condition|)
block|{
name|sumValue
operator|+=
name|values
index|[
name|ord
index|]
expr_stmt|;
if|if
condition|(
name|values
index|[
name|ord
index|]
operator|>
name|bottomValue
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
name|TopOrdCountQueue
operator|.
name|OrdAndCount
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
name|count
operator|=
name|values
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
name|bottomValue
operator|=
name|q
operator|.
name|top
argument_list|()
operator|.
name|count
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
name|sumValue
operator|==
literal|0
condition|)
block|{
comment|//System.out.println("totCount=0 for path=" + path);
return|return
literal|null
return|;
block|}
comment|/*     FacetsConfig.DimConfig ft = facetsConfig.getDimConfig(path.components[0]);     // nocommit shouldn't we verify the indexedFieldName     // matches what was passed to our ctor?     if (ft.hierarchical&& ft.multiValued) {       totCount = counts[dimOrd];     }     */
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
name|TopOrdCountQueue
operator|.
name|OrdAndCount
name|ordAndCount
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
name|ordAndCount
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
name|path
operator|.
name|length
index|]
argument_list|,
name|ordAndCount
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SimpleFacetResult
argument_list|(
name|path
argument_list|,
name|sumValue
argument_list|,
name|labelValues
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAllDims
specifier|public
name|List
argument_list|<
name|SimpleFacetResult
argument_list|>
name|getAllDims
parameter_list|(
name|int
name|topN
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|children
index|[
name|TaxonomyReader
operator|.
name|ROOT_ORDINAL
index|]
decl_stmt|;
name|List
argument_list|<
name|SimpleFacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleFacetResult
argument_list|>
argument_list|()
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
name|SimpleFacetResult
name|result
init|=
name|getTopChildren
argument_list|(
name|taxoReader
operator|.
name|getPath
argument_list|(
name|ord
argument_list|)
argument_list|,
name|ord
argument_list|,
name|topN
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
name|ord
operator|=
name|siblings
index|[
name|ord
index|]
expr_stmt|;
block|}
comment|// Sort by highest count:
name|Collections
operator|.
name|sort
argument_list|(
name|results
argument_list|,
operator|new
name|Comparator
argument_list|<
name|SimpleFacetResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|SimpleFacetResult
name|a
parameter_list|,
name|SimpleFacetResult
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|>
name|b
operator|.
name|value
operator|.
name|intValue
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|>
name|a
operator|.
name|value
operator|.
name|intValue
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
comment|// Tie break by dimension
return|return
name|a
operator|.
name|path
operator|.
name|components
index|[
literal|0
index|]
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|path
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
block|}
end_class

end_unit

