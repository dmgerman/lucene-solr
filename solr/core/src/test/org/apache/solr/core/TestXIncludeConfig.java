begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
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
name|schema
operator|.
name|IndexSchema
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
name|update
operator|.
name|processor
operator|.
name|RegexReplaceProcessorFactory
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|Assume
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
comment|/**   * Test both XInclude as well as more old school "entity includes"  */
end_comment

begin_class
DECL|class|TestXIncludeConfig
specifier|public
class|class
name|TestXIncludeConfig
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
literal|"solrconfig-xinclude.xml"
argument_list|,
literal|"schema-xinclude.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
comment|//see whether it even makes sense to run this test
name|dbf
operator|.
name|setXIncludeAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|testXInclude
specifier|public
name|void
name|testXInclude
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
name|assertNotNull
argument_list|(
literal|"includedHandler is null"
argument_list|,
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"includedHandler"
argument_list|)
argument_list|)
expr_stmt|;
name|UpdateRequestProcessorChain
name|chain
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"special-include"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"chain is missing included processor"
argument_list|,
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"chain with inclued processor is wrong size"
argument_list|,
literal|1
argument_list|,
name|chain
operator|.
name|getProcessors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"chain has wrong included processor"
argument_list|,
name|RegexReplaceProcessorFactory
operator|.
name|class
argument_list|,
name|chain
operator|.
name|getProcessors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSchema
name|schema
init|=
name|core
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
comment|// xinclude
name|assertNotNull
argument_list|(
literal|"ft-included is null"
argument_list|,
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"ft-included"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field-included is null"
argument_list|,
name|schema
operator|.
name|getFieldOrNull
argument_list|(
literal|"field-included"
argument_list|)
argument_list|)
expr_stmt|;
comment|// entity include
name|assertNotNull
argument_list|(
literal|"ft-entity-include1 is null"
argument_list|,
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"ft-entity-include1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"ft-entity-include2 is null"
argument_list|,
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"ft-entity-include2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// sanity check
name|assertNull
argument_list|(
literal|"ft-entity-include3 is not null"
argument_list|,
comment|// Does Not Exist Anywhere
name|schema
operator|.
name|getFieldTypeByName
argument_list|(
literal|"ft-entity-include3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

