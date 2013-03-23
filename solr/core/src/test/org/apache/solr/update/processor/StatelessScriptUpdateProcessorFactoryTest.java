begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|params
operator|.
name|SolrParams
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
name|ModifiableSolrParams
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
name|common
operator|.
name|SolrException
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
name|SolrInputDocument
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

begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngineManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngine
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Tests {@link StatelessScriptUpdateProcessorFactory}.  *  * TODO: This test, to run from an IDE, requires a working directory of<path-to>/solr/core/src/test-files.  Fix!  */
end_comment

begin_class
DECL|class|StatelessScriptUpdateProcessorFactoryTest
specifier|public
class|class
name|StatelessScriptUpdateProcessorFactoryTest
extends|extends
name|UpdateProcessorTestBase
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
name|Assume
operator|.
name|assumeNotNull
argument_list|(
operator|(
operator|new
name|ScriptEngineManager
argument_list|()
operator|)
operator|.
name|getEngineByExtension
argument_list|(
literal|"js"
argument_list|)
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-script-updateprocessor.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**    * simple test of a basic script processor chain using the full     * RequestHandler + UpdateProcessorChain flow    */
DECL|method|testFullRequestHandlerFlow
specifier|public
name|void
name|testFullRequestHandlerFlow
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
literal|"Simple assertion that adding a document works"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4055"
argument_list|,
literal|"subject"
argument_list|,
literal|"Hoss"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"couldn't find hoss using script added field"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"script_added_i:[40 TO 45]"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:4055"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|,
literal|"//str[@name='id'][.='4055']"
argument_list|)
expr_stmt|;
comment|// clean up
name|processDeleteById
argument_list|(
literal|"run-no-scripts"
argument_list|,
literal|"4055"
argument_list|)
expr_stmt|;
name|processCommit
argument_list|(
literal|"run-no-scripts"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleScript
specifier|public
name|void
name|testSingleScript
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
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
literal|"single-script"
argument_list|)
decl_stmt|;
specifier|final
name|StatelessScriptUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|StatelessScriptUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
operator|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|functionMessages
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setScriptEngineCustomizer
argument_list|(
operator|new
name|ScriptEngineCustomizer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|customize
parameter_list|(
name|ScriptEngine
name|engine
parameter_list|)
block|{
name|engine
operator|.
name|put
argument_list|(
literal|"functionMessages"
argument_list|,
name|functionMessages
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
literal|"single-script"
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|processCommit
argument_list|(
literal|"run-no-scripts"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"couldn't find doc by id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|processDeleteById
argument_list|(
literal|"single-script"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|processCommit
argument_list|(
literal|"single-script"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"found deleted doc"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|functionMessages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processAdd0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processDelete0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processCommit0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleScripts
specifier|public
name|void
name|testMultipleScripts
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
name|chain
range|:
operator|new
name|String
index|[]
block|{
literal|"dual-scripts-arr"
block|,
literal|"dual-scripts-strs"
block|}
control|)
block|{
name|UpdateRequestProcessorChain
name|chained
init|=
name|core
operator|.
name|getUpdateProcessingChain
argument_list|(
name|chain
argument_list|)
decl_stmt|;
specifier|final
name|StatelessScriptUpdateProcessorFactory
name|factory
init|=
operator|(
operator|(
name|StatelessScriptUpdateProcessorFactory
operator|)
name|chained
operator|.
name|getFactories
argument_list|()
index|[
literal|0
index|]
operator|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|functionMessages
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ScriptEngineCustomizer
name|customizer
init|=
operator|new
name|ScriptEngineCustomizer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|customize
parameter_list|(
name|ScriptEngine
name|engine
parameter_list|)
block|{
name|engine
operator|.
name|put
argument_list|(
literal|"functionMessages"
argument_list|,
name|functionMessages
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|factory
operator|.
name|setScriptEngineCustomizer
argument_list|(
name|customizer
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|chained
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add Double field"
argument_list|,
literal|42.3d
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add integer field"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|42
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_i"
argument_list|)
argument_list|)
expr_stmt|;
name|processCommit
argument_list|(
literal|"run-no-scripts"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|chain
operator|+
literal|": couldn't find doc by id"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|processDeleteById
argument_list|(
name|chain
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|processCommit
argument_list|(
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
literal|6
argument_list|,
name|functionMessages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processAdd0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processAdd1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
operator|+
literal|": script order doesn't match conf order"
argument_list|,
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"processAdd0"
argument_list|)
operator|<
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"processAdd1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processDelete0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processDelete1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
operator|+
literal|": script order doesn't match conf order"
argument_list|,
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"processDelete0"
argument_list|)
operator|<
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"processDelete1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processCommit0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"processCommit1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
operator|+
literal|": script order doesn't match conf order"
argument_list|,
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"processCommit0"
argument_list|)
operator|<
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"processCommit1"
argument_list|)
argument_list|)
expr_stmt|;
name|finish
argument_list|(
name|chain
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
argument_list|,
literal|8
argument_list|,
name|functionMessages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"finish0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
argument_list|,
name|functionMessages
operator|.
name|contains
argument_list|(
literal|"finish1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|chain
operator|+
literal|": script order doesn't match conf order"
argument_list|,
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"finish0"
argument_list|)
operator|<
name|functionMessages
operator|.
name|indexOf
argument_list|(
literal|"finish1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|chain
operator|+
literal|": found deleted doc"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testConditionalExecution
specifier|public
name|void
name|testConditionalExecution
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|chain
range|:
operator|new
name|String
index|[]
block|{
literal|"conditional-script"
block|,
literal|"conditional-scripts"
block|}
control|)
block|{
name|ModifiableSolrParams
name|reqParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|reqParams
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|chain
operator|+
literal|" added String field despite condition"
argument_list|,
name|d
operator|.
name|containsKey
argument_list|(
literal|"script_added_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|chain
operator|+
literal|" added Double field despite condition"
argument_list|,
name|d
operator|.
name|containsKey
argument_list|(
literal|"script_added_d"
argument_list|)
argument_list|)
expr_stmt|;
name|reqParams
operator|.
name|add
argument_list|(
literal|"go-for-it"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|d
operator|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|reqParams
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add String field"
argument_list|,
literal|"i went for it"
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add Double field"
argument_list|,
literal|42.3d
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add integer field"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|42
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_i"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testForceEngine
specifier|public
name|void
name|testForceEngine
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeNotNull
argument_list|(
operator|(
operator|new
name|ScriptEngineManager
argument_list|()
operator|)
operator|.
name|getEngineByName
argument_list|(
literal|"javascript"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|chain
init|=
literal|"force-script-engine"
decl_stmt|;
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add Double field"
argument_list|,
literal|42.3d
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chain
operator|+
literal|" didn't add integer field"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|42
argument_list|)
argument_list|,
name|d
operator|.
name|getFieldValue
argument_list|(
literal|"script_added_i"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPropogatedException
specifier|public
name|void
name|testPropogatedException
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|chain
init|=
literal|"error-on-add"
decl_stmt|;
try|try
block|{
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Exception doesn't contain script error string: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"no-soup-fo-you"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|fail
argument_list|(
literal|"Did not get exception from script"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingFunctions
specifier|public
name|void
name|testMissingFunctions
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|chain
init|=
literal|"missing-functions"
decl_stmt|;
try|try
block|{
name|SolrInputDocument
name|d
init|=
name|processAdd
argument_list|(
name|chain
argument_list|,
name|doc
argument_list|(
name|f
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"name"
argument_list|,
literal|" foo "
argument_list|)
argument_list|,
name|f
argument_list|(
literal|"subject"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Exception doesn't contain expected error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"processAdd"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|fail
argument_list|(
literal|"Did not get exception from script"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

