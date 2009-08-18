begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|solr
operator|.
name|common
operator|.
name|SolrDocument
import|;
end_import

begin_comment
comment|/**  * Callback capability for modifying a SolrDocument in the {@link SolrPluginUtils#docListToSolrDocumentList(org.apache.solr.search.DocList, org.apache.solr.search.SolrIndexSearcher, java.util.Set, java.util.Map)}  *  *<p/>  * NOTE: This API is subject to change.  * Due to https://issues.apache.org/jira/browse/SOLR-1298 and https://issues.apache.org/jira/browse/SOLR-705, this interface may change in the future.  *  **/
end_comment

begin_interface
DECL|interface|SolrDocumentModifier
specifier|public
interface|interface
name|SolrDocumentModifier
block|{
comment|/**    * Implement this method to allow for changes to be made to the {@link org.apache.solr.common.SolrDocument} in the {@link SolrPluginUtils#docListToSolrDocumentList(org.apache.solr.search.DocList, org.apache.solr.search.SolrIndexSearcher, java.util.Set, SolrDocumentModifier, java.util.Map)}    * call.    * @param doc The {@link org.apache.solr.common.SolrDocument} that can be modified.    */
DECL|method|process
name|void
name|process
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

