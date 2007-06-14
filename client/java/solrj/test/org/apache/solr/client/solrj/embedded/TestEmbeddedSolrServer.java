begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
operator|.
name|embedded
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
name|SolrExampleTestBase
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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

begin_comment
comment|/**  * This runs SolrServer test using   *   * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestEmbeddedSolrServer
specifier|public
class|class
name|TestEmbeddedSolrServer
extends|extends
name|SolrExampleTestBase
block|{
DECL|field|server
name|SolrServer
name|server
decl_stmt|;
DECL|method|setUp
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// setup the server...
name|server
operator|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSolrServer
specifier|protected
name|SolrServer
name|getSolrServer
parameter_list|()
block|{
return|return
name|server
return|;
block|}
block|}
end_class

end_unit

