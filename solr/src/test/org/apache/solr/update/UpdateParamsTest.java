begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
DECL|package|org.apache.solr.update
DECL|class|UpdateParamsTest
DECL|method|getSchemaFile
DECL|method|getSolrConfigFile
DECL|method|testUpdateProcessorParamDeprecation
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|params
operator|.
name|MapSolrParams
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
name|params
operator|.
name|UpdateParams
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
name|*
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
name|XmlUpdateRequestHandler
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
name|request
operator|.
name|SolrQueryRequestBase
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
name|response
operator|.
name|SolrQueryResponse
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
name|AbstractSolrTestCase
import|;
end_import

begin_class
specifier|public
class|class
name|UpdateParamsTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
comment|/**    * Tests that both update.chain and update.processor works    * NOTE: This test will fail when support for update.processor is removed and should then be removed    */
specifier|public
name|void
name|testUpdateProcessorParamDeprecation
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|XmlUpdateRequestHandler
name|handler
init|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|MapSolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|getMap
argument_list|()
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN_DEPRECATED
argument_list|,
literal|"nonexistant"
argument_list|)
expr_stmt|;
comment|// Add a single document
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
comment|// First check that the old param behaves as it should
try|try
block|{
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Faulty update.processor parameter (deprecated but should work) not causing an error - i.e. it is not detected"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Got wrong exception while testing update.chain"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"unknown UpdateRequestProcessorChain: nonexistant"
argument_list|)
expr_stmt|;
block|}
comment|// Then check that the new param behaves correctly
name|params
operator|.
name|getMap
argument_list|()
operator|.
name|remove
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN_DEPRECATED
argument_list|)
expr_stmt|;
name|params
operator|.
name|getMap
argument_list|()
operator|.
name|put
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|,
literal|"nonexistant"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Faulty update.chain parameter not causing an error - i.e. it is not detected"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Got wrong exception while testing update.chain"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"unknown UpdateRequestProcessorChain: nonexistant"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

