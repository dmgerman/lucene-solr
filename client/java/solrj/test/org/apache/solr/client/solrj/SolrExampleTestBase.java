begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/**  * This should include tests against the example solr config  *   * This lets us try various SolrServer implementations with the same tests.  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrExampleTestBase
specifier|abstract
specifier|public
class|class
name|SolrExampleTestBase
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"../../../example/solr/conf/schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"../../../example/solr/conf/solrconfig.xml"
return|;
block|}
comment|/**    * Subclasses need to initialize the server impl    */
DECL|method|getSolrServer
specifier|protected
specifier|abstract
name|SolrServer
name|getSolrServer
parameter_list|()
function_decl|;
comment|/**    * Create a new solr server    */
DECL|method|createNewSolrServer
specifier|protected
specifier|abstract
name|SolrServer
name|createNewSolrServer
parameter_list|()
function_decl|;
block|}
end_class

end_unit

