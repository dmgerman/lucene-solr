begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

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

begin_comment
comment|/**  * A callback interface for streaming response  *   * @since solr 4.0  */
end_comment

begin_class
DECL|class|StreamingResponseCallback
specifier|public
specifier|abstract
class|class
name|StreamingResponseCallback
block|{
comment|/*    * Called for each SolrDocument in the response    */
DECL|method|streamSolrDocument
specifier|public
specifier|abstract
name|void
name|streamSolrDocument
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
function_decl|;
comment|/*    * Called at the beginning of each DocList (and SolrDocumentList)    */
DECL|method|streamDocListInfo
specifier|public
specifier|abstract
name|void
name|streamDocListInfo
parameter_list|(
name|long
name|numFound
parameter_list|,
name|long
name|start
parameter_list|,
name|Float
name|maxScore
parameter_list|)
function_decl|;
block|}
end_class

end_unit

