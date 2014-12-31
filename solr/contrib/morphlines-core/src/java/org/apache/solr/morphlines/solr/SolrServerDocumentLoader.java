begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
import|;
end_import

begin_comment
comment|/**  * @deprecated Use {@link org.apache.solr.morphlines.solr.SolrClientDocumentLoader}  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SolrServerDocumentLoader
specifier|public
class|class
name|SolrServerDocumentLoader
extends|extends
name|SolrClientDocumentLoader
block|{
DECL|method|SolrServerDocumentLoader
specifier|public
name|SolrServerDocumentLoader
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|batchSize
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

