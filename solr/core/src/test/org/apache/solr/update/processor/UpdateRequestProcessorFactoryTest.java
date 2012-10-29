begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  *   */
end_comment

begin_class
DECL|class|UpdateRequestProcessorFactoryTest
specifier|public
class|class
name|UpdateRequestProcessorFactoryTest
extends|extends
name|AbstractSolrTestCase
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
literal|"solrconfig-transformers.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testConfiguration
specifier|public
name|void
name|testConfiguration
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
comment|// make sure it loaded the factories
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"standard"
argument_list|)
decl_stmt|;
comment|// Make sure it got 3 items (4 configured, 1 is enable=false)
name|assertEquals
argument_list|(
literal|"wrong number of (enabled) factories in chain"
argument_list|,
literal|3
argument_list|,
name|chained
operator|.
name|getFactories
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// first one should be log, and it should be configured properly
name|UpdateRequestProcessorFactory
name|first
init|=
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong factory at front of chain"
argument_list|,
name|LogUpdateProcessorFactory
operator|.
name|class
argument_list|,
name|first
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|LogUpdateProcessorFactory
name|log
init|=
operator|(
name|LogUpdateProcessorFactory
operator|)
name|first
decl_stmt|;
name|assertEquals
argument_list|(
literal|"wrong config for LogUpdateProcessorFactory"
argument_list|,
literal|100
argument_list|,
name|log
operator|.
name|maxNumToLog
argument_list|)
expr_stmt|;
name|UpdateRequestProcessorChain
name|custom
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|CustomUpdateRequestProcessorFactory
name|link
init|=
operator|(
name|CustomUpdateRequestProcessorFactory
operator|)
name|custom
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|custom
argument_list|,
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|custom
argument_list|,
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"custom"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure the NamedListArgs got through ok
name|assertEquals
argument_list|(
literal|"{name={n8=88,n9=99}}"
argument_list|,
name|link
operator|.
name|args
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpdateDistribChainSkipping
specifier|public
name|void
name|testUpdateDistribChainSkipping
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
for|for
control|(
specifier|final
name|String
name|name
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"distrib-chain-explicit"
argument_list|,
literal|"distrib-chain-implicit"
argument_list|,
literal|"distrib-chain-noop"
argument_list|)
control|)
block|{
name|UpdateRequestProcessor
name|proc
decl_stmt|;
name|UpdateRequestProcessorChain
name|chain
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|name
argument_list|,
name|chain
argument_list|)
expr_stmt|;
comment|// either explicitly, or because of injection
name|assertEquals
argument_list|(
name|name
operator|+
literal|" chain length"
argument_list|,
literal|4
argument_list|,
name|chain
operator|.
name|getFactories
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Custom comes first in all three of our chains
name|proc
operator|=
name|chain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|()
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|name
operator|+
literal|" first processor isn't a CustomUpdateRequestProcessor: "
operator|+
name|proc
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|proc
operator|instanceof
name|CustomUpdateRequestProcessor
argument_list|)
expr_stmt|;
comment|// varies depending on chain, but definitely shouldn't be Custom
name|proc
operator|=
name|chain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|,
literal|"non_blank_value"
argument_list|)
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|name
operator|+
literal|" post distrib proc should not be a CustomUpdateRequestProcessor: "
operator|+
name|proc
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|proc
operator|instanceof
name|CustomUpdateRequestProcessor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

