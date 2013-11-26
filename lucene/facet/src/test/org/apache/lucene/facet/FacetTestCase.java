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

begin_class
DECL|class|FacetTestCase
specifier|public
specifier|abstract
class|class
name|FacetTestCase
extends|extends
name|LuceneTestCase
block|{
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
name|FacetsCollector
name|c
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|config
argument_list|,
name|c
argument_list|,
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
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
name|FacetsCollector
name|c
parameter_list|,
name|String
name|indexFieldName
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
name|indexFieldName
argument_list|,
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
argument_list|(
name|indexFieldName
argument_list|)
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

