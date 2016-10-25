begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|Facets
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
name|FacetsConfig
operator|.
name|DimConfig
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|FacetsConfig
import|;
end_import

begin_comment
comment|/** Base class for all taxonomy-based facets impls. */
end_comment

begin_class
DECL|class|TaxonomyFacets
specifier|public
specifier|abstract
class|class
name|TaxonomyFacets
extends|extends
name|Facets
block|{
DECL|field|BY_VALUE_THEN_DIM
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|FacetResult
argument_list|>
name|BY_VALUE_THEN_DIM
init|=
operator|new
name|Comparator
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResult
name|a
parameter_list|,
name|FacetResult
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
operator|>
name|b
operator|.
name|value
operator|.
name|doubleValue
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
name|doubleValue
argument_list|()
operator|>
name|a
operator|.
name|value
operator|.
name|doubleValue
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|a
operator|.
name|dim
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|dim
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
comment|/** Index field name provided to the constructor. */
DECL|field|indexFieldName
specifier|protected
specifier|final
name|String
name|indexFieldName
decl_stmt|;
comment|/** {@code TaxonomyReader} provided to the constructor. */
DECL|field|taxoReader
specifier|protected
specifier|final
name|TaxonomyReader
name|taxoReader
decl_stmt|;
comment|/** {@code FacetsConfig} provided to the constructor. */
DECL|field|config
specifier|protected
specifier|final
name|FacetsConfig
name|config
decl_stmt|;
comment|/** Maps parent ordinal to its child, or -1 if the parent    *  is childless. */
DECL|field|children
specifier|protected
specifier|final
name|int
index|[]
name|children
decl_stmt|;
comment|/** Maps an ordinal to its sibling, or -1 if there is no    *  sibling. */
DECL|field|siblings
specifier|protected
specifier|final
name|int
index|[]
name|siblings
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|TaxonomyFacets
specifier|protected
name|TaxonomyFacets
parameter_list|(
name|String
name|indexFieldName
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|indexFieldName
operator|=
name|indexFieldName
expr_stmt|;
name|this
operator|.
name|taxoReader
operator|=
name|taxoReader
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
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
name|siblings
operator|=
name|pta
operator|.
name|siblings
argument_list|()
expr_stmt|;
block|}
comment|/** Throws {@code IllegalArgumentException} if the    *  dimension is not recognized.  Otherwise, returns the    *  {@link DimConfig} for this dimension. */
DECL|method|verifyDim
specifier|protected
name|FacetsConfig
operator|.
name|DimConfig
name|verifyDim
parameter_list|(
name|String
name|dim
parameter_list|)
block|{
name|FacetsConfig
operator|.
name|DimConfig
name|dimConfig
init|=
name|config
operator|.
name|getDimConfig
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dimConfig
operator|.
name|indexFieldName
operator|.
name|equals
argument_list|(
name|indexFieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dimension \""
operator|+
name|dim
operator|+
literal|"\" was not indexed into field \""
operator|+
name|indexFieldName
operator|+
literal|"\""
argument_list|)
throw|;
block|}
return|return
name|dimConfig
return|;
block|}
annotation|@
name|Override
DECL|method|getAllDims
specifier|public
name|List
argument_list|<
name|FacetResult
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
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|String
name|dim
init|=
name|taxoReader
operator|.
name|getPath
argument_list|(
name|ord
argument_list|)
operator|.
name|components
index|[
literal|0
index|]
decl_stmt|;
name|FacetsConfig
operator|.
name|DimConfig
name|dimConfig
init|=
name|config
operator|.
name|getDimConfig
argument_list|(
name|dim
argument_list|)
decl_stmt|;
if|if
condition|(
name|dimConfig
operator|.
name|indexFieldName
operator|.
name|equals
argument_list|(
name|indexFieldName
argument_list|)
condition|)
block|{
name|FacetResult
name|result
init|=
name|getTopChildren
argument_list|(
name|topN
argument_list|,
name|dim
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
block|}
name|ord
operator|=
name|siblings
index|[
name|ord
index|]
expr_stmt|;
block|}
comment|// Sort by highest value, tie break by dim:
name|Collections
operator|.
name|sort
argument_list|(
name|results
argument_list|,
name|BY_VALUE_THEN_DIM
argument_list|)
expr_stmt|;
return|return
name|results
return|;
block|}
block|}
end_class

end_unit

