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
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXParseException
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
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|CoreContainerCoreInitFailuresTest
specifier|public
class|class
name|CoreContainerCoreInitFailuresTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|solrHome
name|File
name|solrHome
init|=
literal|null
decl_stmt|;
DECL|field|cc
name|CoreContainer
name|cc
init|=
literal|null
decl_stmt|;
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
specifier|final
name|String
name|dirSuffix
parameter_list|)
block|{
name|solrHome
operator|=
name|createTempDir
argument_list|(
name|dirSuffix
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|cc
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cc
operator|=
literal|null
expr_stmt|;
block|}
name|solrHome
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testFlowWithEmpty
specifier|public
name|void
name|testFlowWithEmpty
parameter_list|()
throws|throws
name|Exception
block|{
comment|// reused state
name|Map
argument_list|<
name|String
argument_list|,
name|CoreContainer
operator|.
name|CoreLoadFailure
argument_list|>
name|failures
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|cores
init|=
literal|null
decl_stmt|;
name|Exception
name|fail
init|=
literal|null
decl_stmt|;
name|init
argument_list|(
literal|"empty_flow"
argument_list|)
expr_stmt|;
comment|// solr.xml
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|solrXml
argument_list|,
name|EMPTY_SOLR_XML
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
comment|// ----
comment|// init the CoreContainer
name|cc
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|load
argument_list|()
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|0
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of core failures"
argument_list|,
literal|0
argument_list|,
name|failures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// -----
comment|// try to add a collection with a path that doesn't exist
specifier|final
name|CoreDescriptor
name|bogus
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
literal|"bogus"
argument_list|,
literal|"bogus_path"
argument_list|)
decl_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
name|cc
operator|.
name|create
argument_list|(
name|bogus
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"bogus inst dir failed to trigger exception from create"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"init exception doesn't mention bogus dir: "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|0
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
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
name|fail
operator|=
name|failures
operator|.
name|get
argument_list|(
literal|"bogus"
argument_list|)
operator|.
name|exception
expr_stmt|;
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
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we get null accessing a non-existent core
name|assertNull
argument_list|(
name|cc
operator|.
name|getCore
argument_list|(
literal|"does_not_exist"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we get a 500 accessing the core with an init failure
try|try
block|{
name|SolrCore
name|c
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"bogus"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Failed to get Exception on accessing core with init failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
comment|// double wrapped
name|String
name|cause
init|=
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"getCore() ex cause doesn't mention init fail: "
operator|+
name|cause
argument_list|,
literal|0
operator|<
name|cause
operator|.
name|indexOf
argument_list|(
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// let the test end here, with some recorded failures, and let cleanUp()
comment|// verify that there is no problem shuting down CoreContainer with known
comment|// SolrCore failures
block|}
DECL|method|testFlowBadFromStart
specifier|public
name|void
name|testFlowBadFromStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// reused state
name|Map
argument_list|<
name|String
argument_list|,
name|CoreContainer
operator|.
name|CoreLoadFailure
argument_list|>
name|failures
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|cores
init|=
literal|null
decl_stmt|;
name|Exception
name|fail
init|=
literal|null
decl_stmt|;
name|init
argument_list|(
literal|"bad_flow"
argument_list|)
expr_stmt|;
comment|// start with two collections: one valid, and one broken
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|solrXml
argument_list|,
name|BAD_SOLR_XML
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
comment|// our "ok" collection
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-defaults.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_ok"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/schema-minimal.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_ok"
argument_list|,
literal|"conf"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// our "bad" collection
name|ignoreException
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"DummyMergePolicy"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/bad-mp-solrconfig.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/schema-minimal.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// -----
comment|// init the  CoreContainer with the mix of ok/bad cores
name|cc
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|load
argument_list|()
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|1
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
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
name|fail
operator|=
name|failures
operator|.
name|get
argument_list|(
literal|"col_bad"
argument_list|)
operator|.
name|exception
expr_stmt|;
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
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|fail
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"DummyMergePolicy"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we get null accessing a non-existent core
name|assertNull
argument_list|(
name|cc
operator|.
name|getCore
argument_list|(
literal|"does_not_exist"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we get a 500 accessing the core with an init failure
try|try
block|{
name|SolrCore
name|c
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"col_bad"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Failed to get Exception on accessing core with init failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
comment|// double wrapped
name|String
name|cause
init|=
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"getCore() ex cause doesn't mention init fail: "
operator|+
name|cause
argument_list|,
literal|0
operator|<
name|cause
operator|.
name|indexOf
argument_list|(
literal|"DummyMergePolicy"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// -----
comment|// "fix" the bad collection
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-defaults.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|CoreDescriptor
name|fixed
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
literal|"col_bad"
argument_list|,
literal|"col_bad"
argument_list|)
decl_stmt|;
name|cc
operator|.
name|create
argument_list|(
name|fixed
argument_list|)
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|2
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_bad not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_bad"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of core failures"
argument_list|,
literal|0
argument_list|,
name|failures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// -----
comment|// try to add a collection with a path that doesn't exist
specifier|final
name|CoreDescriptor
name|bogus
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|cc
argument_list|,
literal|"bogus"
argument_list|,
literal|"bogus_path"
argument_list|)
decl_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
name|cc
operator|.
name|create
argument_list|(
name|bogus
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"bogus inst dir failed to trigger exception from create"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"init exception doesn't mention bogus dir: "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|2
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_bad not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_bad"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
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
name|fail
operator|=
name|failures
operator|.
name|get
argument_list|(
literal|"bogus"
argument_list|)
operator|.
name|exception
expr_stmt|;
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
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we get null accessing a non-existent core
name|assertNull
argument_list|(
name|cc
operator|.
name|getCore
argument_list|(
literal|"does_not_exist"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we get a 500 accessing the core with an init failure
try|try
block|{
name|SolrCore
name|c
init|=
name|cc
operator|.
name|getCore
argument_list|(
literal|"bogus"
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Failed to get Exception on accessing core with init failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
comment|// double wrapped
name|String
name|cause
init|=
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"getCore() ex cause doesn't mention init fail: "
operator|+
name|cause
argument_list|,
literal|0
operator|<
name|cause
operator|.
name|indexOf
argument_list|(
literal|"bogus_path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// -----
comment|// break col_bad's config and try to RELOAD to add failure
specifier|final
name|long
name|col_bad_old_start
init|=
name|getCoreStartTime
argument_list|(
name|cc
argument_list|,
literal|"col_bad"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|,
literal|"This is giberish, not valid XML<"
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"SAX"
argument_list|)
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"col_bad"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"corrupt solrconfig.xml failed to trigger exception from reload"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|Throwable
name|rootException
init|=
name|getWrappedException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"We're supposed to have a wrapped SAXParserException here, but we don't"
argument_list|,
name|rootException
operator|instanceof
name|SAXParseException
argument_list|)
expr_stmt|;
name|SAXParseException
name|se
init|=
operator|(
name|SAXParseException
operator|)
name|rootException
decl_stmt|;
name|assertTrue
argument_list|(
literal|"reload exception doesn't refer to slrconfig.xml "
operator|+
name|se
operator|.
name|getSystemId
argument_list|()
argument_list|,
literal|0
operator|<
name|se
operator|.
name|getSystemId
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Failed core reload should not have changed start time"
argument_list|,
name|col_bad_old_start
argument_list|,
name|getCoreStartTime
argument_list|(
name|cc
argument_list|,
literal|"col_bad"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|2
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_bad not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_bad"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of core failures"
argument_list|,
literal|2
argument_list|,
name|failures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Throwable
name|ex
init|=
name|getWrappedException
argument_list|(
name|failures
operator|.
name|get
argument_list|(
literal|"col_bad"
argument_list|)
operator|.
name|exception
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"null failure for test core"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"init failure isn't SAXParseException"
argument_list|,
name|ex
operator|instanceof
name|SAXParseException
argument_list|)
expr_stmt|;
name|SAXParseException
name|saxEx
init|=
operator|(
name|SAXParseException
operator|)
name|ex
decl_stmt|;
name|assertTrue
argument_list|(
literal|"init failure doesn't mention problem: "
operator|+
name|saxEx
operator|.
name|toString
argument_list|()
argument_list|,
name|saxEx
operator|.
name|getSystemId
argument_list|()
operator|.
name|contains
argument_list|(
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// ----
comment|// fix col_bad's config (again) and RELOAD to fix failure
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-defaults.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|cc
operator|.
name|reload
argument_list|(
literal|"col_bad"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Core reload should have changed start time"
argument_list|,
name|col_bad_old_start
operator|<
name|getCoreStartTime
argument_list|(
name|cc
argument_list|,
literal|"col_bad"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|2
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_bad not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_bad"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
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
block|}
DECL|method|testJavaLangErrorFromHandlerOnStartup
specifier|public
name|void
name|testJavaLangErrorFromHandlerOnStartup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// reused state
name|Map
argument_list|<
name|String
argument_list|,
name|CoreContainer
operator|.
name|CoreLoadFailure
argument_list|>
name|failures
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|cores
init|=
literal|null
decl_stmt|;
name|Exception
name|fail
init|=
literal|null
decl_stmt|;
name|init
argument_list|(
literal|"java_lang_error_handler"
argument_list|)
expr_stmt|;
comment|// start with two collections: 1 ok, and 1 that throws java.lang.Error on startup
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|solrXml
argument_list|,
name|BAD_SOLR_XML
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
comment|// our "ok" collection
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-defaults.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_ok"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/schema-minimal.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_ok"
argument_list|,
literal|"conf"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// our "bad" collection
name|ignoreException
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"my_error_handler"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/bad-error-solrconfig.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/schema-minimal.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// -----
comment|// init the  CoreContainer with the mix of ok/bad cores
name|cc
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|load
argument_list|()
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|1
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
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
name|fail
operator|=
name|failures
operator|.
name|get
argument_list|(
literal|"col_bad"
argument_list|)
operator|.
name|exception
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"null failure for test core"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"init failure doesn't mention root problem: "
operator|+
name|fail
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|fail
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"throwing a java.lang.Error"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJavaLangErrorFromSchemaOnStartup
specifier|public
name|void
name|testJavaLangErrorFromSchemaOnStartup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// reused state
name|Map
argument_list|<
name|String
argument_list|,
name|CoreContainer
operator|.
name|CoreLoadFailure
argument_list|>
name|failures
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|cores
init|=
literal|null
decl_stmt|;
name|Exception
name|fail
init|=
literal|null
decl_stmt|;
name|init
argument_list|(
literal|"java_lang_error_schema"
argument_list|)
expr_stmt|;
comment|// start with two collections: 1 ok, and 1 that throws java.lang.Error on startup
name|File
name|solrXml
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|write
argument_list|(
name|solrXml
argument_list|,
name|BAD_SOLR_XML
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
comment|// our "ok" collection
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-defaults.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_ok"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/schema-minimal.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_ok"
argument_list|,
literal|"conf"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// our "bad" collection
name|ignoreException
argument_list|(
name|Pattern
operator|.
name|quote
argument_list|(
literal|"error_ft"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/solrconfig-defaults.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"solr/collection1/conf/bad-schema-init-error.xml"
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|getFile
argument_list|(
name|solrHome
argument_list|,
literal|"col_bad"
argument_list|,
literal|"conf"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// -----
comment|// init the  CoreContainer with the mix of ok/bad cores
name|cc
operator|=
operator|new
name|CoreContainer
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|cc
operator|.
name|load
argument_list|()
expr_stmt|;
comment|// check that we have the cores we expect
name|cores
operator|=
name|cc
operator|.
name|getCoreNames
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core names is null"
argument_list|,
name|cores
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong number of cores"
argument_list|,
literal|1
argument_list|,
name|cores
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"col_ok not found"
argument_list|,
name|cores
operator|.
name|contains
argument_list|(
literal|"col_ok"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that we have the failures we expect
name|failures
operator|=
name|cc
operator|.
name|getCoreInitFailures
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"core failures is a null map"
argument_list|,
name|failures
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
name|fail
operator|=
name|failures
operator|.
name|get
argument_list|(
literal|"col_bad"
argument_list|)
operator|.
name|exception
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"null failure for test core"
argument_list|,
name|fail
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"init failure doesn't mention root problem: "
operator|+
name|fail
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|0
operator|<
name|fail
operator|.
name|getMessage
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|"throwing java.lang.Error"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getCoreStartTime
specifier|private
name|long
name|getCoreStartTime
parameter_list|(
specifier|final
name|CoreContainer
name|cc
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
try|try
init|(
name|SolrCore
name|tmp
init|=
name|cc
operator|.
name|getCore
argument_list|(
name|name
argument_list|)
init|)
block|{
return|return
name|tmp
operator|.
name|getStartTime
argument_list|()
return|;
block|}
block|}
DECL|field|EMPTY_SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_SOLR_XML
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr persistent=\"false\">\n"
operator|+
literal|"<cores adminPath=\"/admin/cores\">\n"
operator|+
literal|"</cores>\n"
operator|+
literal|"</solr>"
decl_stmt|;
DECL|field|BAD_SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|BAD_SOLR_XML
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
operator|+
literal|"<solr persistent=\"false\">\n"
operator|+
literal|"<cores adminPath=\"/admin/cores\">\n"
operator|+
literal|"<core name=\"col_ok\" instanceDir=\"col_ok\" />\n"
operator|+
literal|"<core name=\"col_bad\" instanceDir=\"col_bad\" />\n"
operator|+
literal|"</cores>\n"
operator|+
literal|"</solr>"
decl_stmt|;
block|}
end_class

end_unit

