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
name|facet
operator|.
name|encoding
operator|.
name|DGapIntEncoder
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
name|encoding
operator|.
name|DGapVInt8IntEncoder
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
name|encoding
operator|.
name|EightFlagsIntEncoder
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
name|encoding
operator|.
name|FourFlagsIntEncoder
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
name|encoding
operator|.
name|IntEncoder
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
name|encoding
operator|.
name|NOnesIntEncoder
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
name|encoding
operator|.
name|SortingIntEncoder
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
name|encoding
operator|.
name|UniqueValuesIntEncoder
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
name|encoding
operator|.
name|VInt8IntEncoder
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
name|simple
operator|.
name|CachedOrdinalsReader
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
name|DocValuesOrdinalsReader
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
name|simple
operator|.
name|FacetsConfig
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
name|FastTaxonomyFacetCounts
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
name|OrdinalsReader
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
name|TaxonomyFacetCounts
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|FacetTestCase
specifier|public
specifier|abstract
class|class
name|FacetTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|ENCODERS
specifier|private
specifier|static
specifier|final
name|IntEncoder
index|[]
name|ENCODERS
init|=
operator|new
name|IntEncoder
index|[]
block|{
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
block|,
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapVInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
block|,
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|EightFlagsIntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|FourFlagsIntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|NOnesIntEncoder
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
argument_list|)
block|,
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|NOnesIntEncoder
argument_list|(
literal|4
argument_list|)
argument_list|)
argument_list|)
argument_list|)
block|,    }
decl_stmt|;
comment|/** Returns a {@link CategoryListParams} with random {@link IntEncoder} and field. */
DECL|method|randomCategoryListParams
specifier|public
specifier|static
name|CategoryListParams
name|randomCategoryListParams
parameter_list|()
block|{
specifier|final
name|String
name|field
init|=
name|CategoryListParams
operator|.
name|DEFAULT_FIELD
operator|+
literal|"$"
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
return|return
name|randomCategoryListParams
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns a {@link CategoryListParams} with random {@link IntEncoder}. */
DECL|method|randomCategoryListParams
specifier|public
specifier|static
name|CategoryListParams
name|randomCategoryListParams
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|IntEncoder
name|encoder
init|=
name|ENCODERS
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|ENCODERS
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
return|return
operator|new
name|CategoryListParams
argument_list|(
name|field
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|IntEncoder
name|createEncoder
parameter_list|()
block|{
return|return
name|encoder
return|;
block|}
block|}
return|;
block|}
DECL|method|getTaxonomyFacetCounts
specifier|public
name|Facets
name|getTaxonomyFacetCounts
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|SimpleFacetsCollector
name|c
parameter_list|)
throws|throws
name|IOException
block|{
name|Facets
name|facets
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|facets
operator|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|OrdinalsReader
name|ordsReader
init|=
operator|new
name|DocValuesOrdinalsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|ordsReader
operator|=
operator|new
name|CachedOrdinalsReader
argument_list|(
name|ordsReader
argument_list|)
expr_stmt|;
block|}
name|facets
operator|=
operator|new
name|TaxonomyFacetCounts
argument_list|(
name|ordsReader
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|facets
return|;
block|}
block|}
end_class

end_unit

