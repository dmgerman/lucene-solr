begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.grouping.endresulttransformer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|endresulttransformer
package|;
end_package

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
name|ScoreDoc
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
name|SolrDocument
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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

begin_comment
comment|/**  * Responsible for transforming the grouped result into the final format for displaying purposes.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|EndResultTransformer
specifier|public
interface|interface
name|EndResultTransformer
block|{
comment|/**    * Transforms the specified result into its final form and puts it into the specified response.    *    * @param result The map containing the grouping result (for grouping by field and query)    * @param rb The response builder containing the response used to render the result and the grouping specification    * @param solrDocumentSource The source of {@link SolrDocument} instances    */
DECL|method|transform
name|void
name|transform
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|result
parameter_list|,
name|ResponseBuilder
name|rb
parameter_list|,
name|SolrDocumentSource
name|solrDocumentSource
parameter_list|)
function_decl|;
comment|/**    * Abstracts the source for {@link SolrDocument} instances.    * The source of documents is different for a distributed search than local search    */
DECL|interface|SolrDocumentSource
specifier|public
interface|interface
name|SolrDocumentSource
block|{
DECL|method|retrieve
name|SolrDocument
name|retrieve
parameter_list|(
name|ScoreDoc
name|doc
parameter_list|)
function_decl|;
block|}
block|}
end_interface

end_unit

