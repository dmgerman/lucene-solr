begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|SolrTestCaseJ4
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
name|embedded
operator|.
name|JettySolrRunner
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
name|impl
operator|.
name|HttpSolrServer
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
name|request
operator|.
name|CoreAdminRequest
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
name|common
operator|.
name|params
operator|.
name|CoreAdminParams
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
name|util
operator|.
name|NamedList
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
name|CoreContainer
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
name|CoreDescriptor
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
name|core
operator|.
name|SolrXMLCoresLocator
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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_class
DECL|class|CoreAdminHandlerTest
specifier|public
class|class
name|CoreAdminHandlerTest
extends|extends
name|SolrTestCaseJ4
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"_sys_vars"
return|;
block|}
annotation|@
name|Test
DECL|method|testCreateWithSysVars
specifier|public
name|void
name|testCreateWithSysVars
parameter_list|()
throws|throws
name|Exception
block|{
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// I require FS-based indexes for this test.
specifier|final
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|getCoreName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|workDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|workDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|coreName
init|=
literal|"with_sys_vars"
decl_stmt|;
name|File
name|instDir
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
name|coreName
argument_list|)
decl_stmt|;
name|File
name|subHome
init|=
operator|new
name|File
argument_list|(
name|instDir
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Failed to make subdirectory "
argument_list|,
name|subHome
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
comment|// Be sure we pick up sysvars when we create this
name|String
name|srcDir
init|=
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf"
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|srcDir
argument_list|,
literal|"schema-tiny.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"schema_ren.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|srcDir
argument_list|,
literal|"solrconfig-minimal.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|srcDir
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|subHome
argument_list|,
literal|"solrconfig.snippet.randomindexconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|SolrXMLCoresLocator
operator|.
name|NonPersistingLocator
name|locator
init|=
operator|(
name|SolrXMLCoresLocator
operator|.
name|NonPersistingLocator
operator|)
name|cores
operator|.
name|getCoresLocator
argument_list|()
decl_stmt|;
specifier|final
name|CoreAdminHandler
name|admin
init|=
operator|new
name|CoreAdminHandler
argument_list|(
name|cores
argument_list|)
decl_stmt|;
comment|// create a new core (using CoreAdminHandler) w/ properties
name|System
operator|.
name|setProperty
argument_list|(
literal|"INSTDIR_TEST"
argument_list|,
name|instDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"CONFIG_TEST"
argument_list|,
literal|"solrconfig_ren.xml"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"SCHEMA_TEST"
argument_list|,
literal|"schema_ren.xml"
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"data_diff"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"DATA_TEST"
argument_list|,
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
name|getCoreName
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
literal|"${INSTDIR_TEST}"
argument_list|,
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
literal|"${CONFIG_TEST}"
argument_list|,
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
literal|"${SCHEMA_TEST}"
argument_list|,
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
literal|"${DATA_TEST}"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception on create"
argument_list|,
name|resp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
comment|// First assert that these values are persisted.
name|h
operator|.
name|validateXPath
argument_list|(
name|locator
operator|.
name|xml
argument_list|,
literal|"/solr/cores/core[@name='"
operator|+
name|getCoreName
argument_list|()
operator|+
literal|"' and @instanceDir='${INSTDIR_TEST}']"
argument_list|,
literal|"/solr/cores/core[@name='"
operator|+
name|getCoreName
argument_list|()
operator|+
literal|"' and @dataDir='${DATA_TEST}']"
argument_list|,
literal|"/solr/cores/core[@name='"
operator|+
name|getCoreName
argument_list|()
operator|+
literal|"' and @schema='${SCHEMA_TEST}']"
argument_list|,
literal|"/solr/cores/core[@name='"
operator|+
name|getCoreName
argument_list|()
operator|+
literal|"' and @config='${CONFIG_TEST}']"
argument_list|)
expr_stmt|;
comment|// Now assert that certain values are properly dereferenced in the process of creating the core, see
comment|// SOLR-4982.
comment|// Should NOT be a datadir named ${DATA_TEST} (literal). This is the bug after all
name|File
name|badDir
init|=
operator|new
name|File
argument_list|(
name|instDir
argument_list|,
literal|"${DATA_TEST}"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Should have substituted the sys var, found file "
operator|+
name|badDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|badDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// For the other 3 vars, we couldn't get past creating the core fi dereferencing didn't work correctly.
comment|// Should have segments in the directory pointed to by the ${DATA_TEST}.
name|File
name|test
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have found index dir at "
operator|+
name|test
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|test
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|test
operator|=
operator|new
name|File
argument_list|(
name|test
argument_list|,
literal|"segments.gen"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have found segments.gen at "
operator|+
name|test
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|test
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// Cleanup
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCoreAdminHandler
specifier|public
name|void
name|testCoreAdminHandler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|workDir
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|workDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Failed to mkdirs workDir"
argument_list|,
name|workDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
specifier|final
name|CoreAdminHandler
name|admin
init|=
operator|new
name|CoreAdminHandler
argument_list|(
name|cores
argument_list|)
decl_stmt|;
name|String
name|instDir
decl_stmt|;
try|try
init|(
name|SolrCore
name|template
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|"collection1"
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|template
argument_list|)
expr_stmt|;
name|instDir
operator|=
name|template
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
expr_stmt|;
block|}
specifier|final
name|File
name|instDirFile
init|=
operator|new
name|File
argument_list|(
name|instDir
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"instDir doesn't exist: "
operator|+
name|instDir
argument_list|,
name|instDirFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|File
name|instPropFile
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"instProp"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|instDirFile
argument_list|,
name|instPropFile
argument_list|)
expr_stmt|;
comment|// create a new core (using CoreAdminHandler) w/ properties
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
name|instPropFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
literal|"props"
argument_list|,
name|CoreAdminParams
operator|.
name|PROPERTY_PREFIX
operator|+
literal|"hoss"
argument_list|,
literal|"man"
argument_list|,
name|CoreAdminParams
operator|.
name|PROPERTY_PREFIX
operator|+
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Exception on create"
argument_list|,
name|resp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
name|CoreDescriptor
name|cd
init|=
name|cores
operator|.
name|getCoreDescriptor
argument_list|(
literal|"props"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Core not added!"
argument_list|,
name|cd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cd
operator|.
name|getCoreProperty
argument_list|(
literal|"hoss"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"man"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cd
operator|.
name|getCoreProperty
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
comment|// attempt to create a bogus core and confirm failure
name|ignoreException
argument_list|(
literal|"Could not load config"
argument_list|)
expr_stmt|;
try|try
block|{
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
literal|"bogus_dir_core"
argument_list|,
name|CoreAdminParams
operator|.
name|INSTANCE_DIR
argument_list|,
literal|"dir_does_not_exist_127896"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"bogus collection created ok"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|// :NOOP:
comment|// :TODO: CoreAdminHandler's exception messages are terrible, otherwise we could assert something useful here
block|}
name|unIgnoreException
argument_list|(
literal|"Could not load config"
argument_list|)
expr_stmt|;
comment|// check specifically for status of the failed core name
name|resp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|STATUS
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
literal|"bogus_dir_core"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
name|failures
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Exception
argument_list|>
operator|)
name|resp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"initFailures"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is null"
argument_list|,
name|failures
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|status
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|resp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"core status is null"
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of core failures"
argument_list|,
literal|1
argument_list|,
name|failures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Exception
name|fail
init|=
name|failures
operator|.
name|get
argument_list|(
literal|"bogus_dir_core"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null failure for test core"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"init failure doesn't mention problem: "
operator|+
name|fail
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|fail
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"dir_does_not_exist"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bogus_dir_core status isn't empty"
argument_list|,
literal|0
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|status
operator|.
name|get
argument_list|(
literal|"bogus_dir_core"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// :TODO: because of SOLR-3665 we can't ask for status from all cores
comment|// cleanup
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteInstanceDir
specifier|public
name|void
name|testDeleteInstanceDir
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|solrHomeDirectory
init|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-corex-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|solrHomeDirectory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|copySolrHomeToTemp
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"corex"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|File
name|corex
init|=
operator|new
name|File
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"corex"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
operator|new
name|File
argument_list|(
name|corex
argument_list|,
literal|"core.properties"
argument_list|)
argument_list|,
literal|""
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|JettySolrRunner
name|runner
init|=
operator|new
name|JettySolrRunner
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"/solr"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|runner
operator|.
name|start
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://localhost:"
operator|+
name|runner
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|"/solr/corex"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
name|SolrTestCaseJ4
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
name|SolrTestCaseJ4
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|()
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|server
operator|=
operator|new
name|HttpSolrServer
argument_list|(
literal|"http://localhost:"
operator|+
name|runner
operator|.
name|getLocalPort
argument_list|()
operator|+
literal|"/solr"
argument_list|)
expr_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
name|SolrTestCaseJ4
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
name|SolrTestCaseJ4
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|CoreAdminRequest
operator|.
name|Unload
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Unload
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|req
operator|.
name|setDeleteInstanceDir
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
literal|"corex"
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|runner
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Instance directory exists after core unload with deleteInstanceDir=true : "
operator|+
name|corex
argument_list|,
name|corex
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception testing core unload with deleteInstanceDir=true"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|runner
operator|.
name|isStopped
argument_list|()
condition|)
block|{
name|runner
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testNonexistentCoreReload
specifier|public
name|void
name|testNonexistentCoreReload
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CoreAdminHandler
name|admin
init|=
operator|new
name|CoreAdminHandler
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
try|try
block|{
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|RELOAD
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
literal|"non-existent-core"
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Was able to successfully reload non-existent-core"
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
literal|"Expected error message for non-existent core."
argument_list|,
literal|"Core with core name [non-existent-core] does not exist."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

