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
name|Similarity
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
name|SimilarityProvider
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
name|SolrTestCaseJ4
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
name|core
operator|.
name|SolrCore
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
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
import|;
end_import

begin_class
DECL|class|BaseSimilarityTestCase
specifier|public
specifier|abstract
class|class
name|BaseSimilarityTestCase
extends|extends
name|SolrTestCaseJ4
block|{
comment|/** returns the similarity in use for the field */
DECL|method|getSimilarity
specifier|protected
name|Similarity
name|getSimilarity
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Similarity
name|sim
init|=
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getSimilarityProvider
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
return|return
name|sim
return|;
block|}
comment|/** returns the (Solr)SimilarityProvider */
DECL|method|getSimilarityProvider
specifier|protected
name|SimilarityProvider
name|getSimilarityProvider
parameter_list|()
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SimilarityProvider
name|prov
init|=
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getSimilarityProvider
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
return|return
name|prov
return|;
block|}
block|}
end_class

end_unit

