begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index.streaming
package|package
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
name|streaming
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
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
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|FacetException
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
name|CategoryContainerTestBase
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
name|DummyProperty
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
name|categorypolicy
operator|.
name|NonTopLevelOrdinalPolicy
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
name|categorypolicy
operator|.
name|NonTopLevelPathPolicy
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
name|categorypolicy
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
name|categorypolicy
operator|.
name|PathPolicy
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
name|DefaultFacetIndexingParams
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
name|index
operator|.
name|streaming
operator|.
name|CategoryAttributesStream
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
name|streaming
operator|.
name|CategoryListTokenizer
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
name|streaming
operator|.
name|CategoryParentsStream
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
name|TaxonomyWriter
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
name|lucene
operator|.
name|LuceneTaxonomyWriter
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|CategoryParentsStreamTest
specifier|public
class|class
name|CategoryParentsStreamTest
extends|extends
name|CategoryContainerTestBase
block|{
comment|/**    * Verifies that a {@link CategoryParentsStream} can be constructed from    * {@link CategoryAttributesStream} and produces the correct number of    * tokens with default facet indexing params.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testStreamDefaultParams
specifier|public
name|void
name|testStreamDefaultParams
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|CategoryParentsStream
name|stream
init|=
operator|new
name|CategoryParentsStream
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|taxonomyWriter
argument_list|,
operator|new
name|DefaultFacetIndexingParams
argument_list|()
argument_list|)
decl_stmt|;
comment|// count the number of tokens
name|int
name|nTokens
decl_stmt|;
for|for
control|(
name|nTokens
operator|=
literal|0
init|;
name|stream
operator|.
name|incrementToken
argument_list|()
condition|;
name|nTokens
operator|++
control|)
block|{     }
comment|// should be 6 - all categories and parents
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|6
argument_list|,
name|nTokens
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies that a {@link CategoryParentsStream} can be constructed from    * {@link CategoryAttributesStream} and produces the correct number of    * tokens with non top level facet indexing params.    *     * @throws IOException    */
annotation|@
name|Test
DECL|method|testStreamNonTopLevelParams
specifier|public
name|void
name|testStreamNonTopLevelParams
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|indexingParams
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|OrdinalPolicy
name|fixedOrdinalPolicy
parameter_list|()
block|{
return|return
operator|new
name|NonTopLevelOrdinalPolicy
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PathPolicy
name|fixedPathPolicy
parameter_list|()
block|{
return|return
operator|new
name|NonTopLevelPathPolicy
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|CategoryParentsStream
name|stream
init|=
operator|new
name|CategoryParentsStream
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|taxonomyWriter
argument_list|,
name|indexingParams
argument_list|)
decl_stmt|;
comment|// count the number of tokens
name|int
name|nTokens
decl_stmt|;
for|for
control|(
name|nTokens
operator|=
literal|0
init|;
name|stream
operator|.
name|incrementToken
argument_list|()
condition|;
name|nTokens
operator|++
control|)
block|{     }
comment|/*      * should be 4: 3 non top level ("two", "three" and "six"), and one      * explicit top level ("four")      */
name|assertEquals
argument_list|(
literal|"Wrong number of tokens"
argument_list|,
literal|4
argument_list|,
name|nTokens
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies the correctness when no attributes in parents are retained in    * {@link CategoryParentsStream}.    *     * @throws IOException    * @throws FacetException     */
annotation|@
name|Test
DECL|method|testNoRetainableAttributes
specifier|public
name|void
name|testNoRetainableAttributes
parameter_list|()
throws|throws
name|IOException
throws|,
name|FacetException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|directory
argument_list|)
decl_stmt|;
operator|new
name|CategoryParentsStream
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|taxonomyWriter
argument_list|,
operator|new
name|DefaultFacetIndexingParams
argument_list|()
argument_list|)
expr_stmt|;
comment|// add DummyAttribute, but do not retain, only one expected
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|initialCatgeories
index|[
literal|0
index|]
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|CategoryParentsStream
name|stream
init|=
operator|new
name|CategoryParentsStream
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|taxonomyWriter
argument_list|,
operator|new
name|DefaultFacetIndexingParams
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|nAttributes
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|stream
operator|.
name|categoryAttribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nAttributes
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with attributes"
argument_list|,
literal|1
argument_list|,
name|nAttributes
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Verifies the correctness when attributes in parents are retained in    * {@link CategoryParentsStream}.    *     * @throws IOException    * @throws FacetException     */
annotation|@
name|Test
DECL|method|testRetainableAttributes
specifier|public
name|void
name|testRetainableAttributes
parameter_list|()
throws|throws
name|IOException
throws|,
name|FacetException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|indexingParams
init|=
operator|new
name|DefaultFacetIndexingParams
argument_list|()
decl_stmt|;
operator|new
name|CategoryParentsStream
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|taxonomyWriter
argument_list|,
name|indexingParams
argument_list|)
expr_stmt|;
comment|// add DummyAttribute and retain it, three expected
name|categoryContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|categoryContainer
operator|.
name|addCategory
argument_list|(
name|initialCatgeories
index|[
literal|0
index|]
argument_list|,
operator|new
name|DummyProperty
argument_list|()
argument_list|)
expr_stmt|;
name|CategoryParentsStream
name|stream
init|=
operator|new
name|CategoryParentsStream
argument_list|(
operator|new
name|CategoryAttributesStream
argument_list|(
name|categoryContainer
argument_list|)
argument_list|,
name|taxonomyWriter
argument_list|,
operator|new
name|DefaultFacetIndexingParams
argument_list|()
argument_list|)
decl_stmt|;
name|stream
operator|.
name|addRetainableProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
expr_stmt|;
name|MyCategoryListTokenizer
name|tokenizer
init|=
operator|new
name|MyCategoryListTokenizer
argument_list|(
name|stream
argument_list|,
name|indexingParams
argument_list|)
decl_stmt|;
name|int
name|nAttributes
init|=
literal|0
decl_stmt|;
try|try
block|{
while|while
condition|(
name|tokenizer
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|stream
operator|.
name|categoryAttribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nAttributes
operator|++
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Properties retained after stream closed"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of tokens with attributes"
argument_list|,
literal|3
argument_list|,
name|nAttributes
argument_list|)
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|MyCategoryListTokenizer
specifier|private
specifier|final
class|class
name|MyCategoryListTokenizer
extends|extends
name|CategoryListTokenizer
block|{
DECL|method|MyCategoryListTokenizer
specifier|public
name|MyCategoryListTokenizer
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|FacetIndexingParams
name|indexingParams
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|indexingParams
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|categoryAttribute
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|categoryAttribute
operator|.
name|getCategoryPath
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|categoryAttribute
operator|.
name|getProperty
argument_list|(
name|DummyProperty
operator|.
name|class
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Properties not cleared properly from parents stream"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

