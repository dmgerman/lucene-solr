begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|similarities
operator|.
name|LMJelinekMercerSimilarity
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
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Tests {@link LMJelinekMercerSimilarityFactory}  */
end_comment

begin_class
DECL|class|TestLMJelinekMercerSimilarityFactory
specifier|public
class|class
name|TestLMJelinekMercerSimilarityFactory
extends|extends
name|BaseSimilarityTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-lmjelinekmercer.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** jelinek-mercer with default parameters */
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|LMJelinekMercerSimilarity
operator|.
name|class
argument_list|,
name|getSimilarity
argument_list|(
literal|"text"
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** jelinek-mercer with parameters */
DECL|method|testParameters
specifier|public
name|void
name|testParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|Similarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_params"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|LMJelinekMercerSimilarity
operator|.
name|class
argument_list|,
name|sim
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|LMJelinekMercerSimilarity
name|lm
init|=
operator|(
name|LMJelinekMercerSimilarity
operator|)
name|sim
decl_stmt|;
name|assertEquals
argument_list|(
literal|0.4f
argument_list|,
name|lm
operator|.
name|getLambda
argument_list|()
argument_list|,
literal|0.01f
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

