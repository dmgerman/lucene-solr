begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|index
operator|.
name|TieredMergePolicy
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
name|index
operator|.
name|DirectoryReader
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
name|store
operator|.
name|Directory
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
name|params
operator|.
name|CommonParams
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
name|SolrEventListener
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
name|index
operator|.
name|TieredMergePolicyFactory
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
name|LocalSolrQueryRequest
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
name|SolrQueryRequest
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *   *  */
end_comment

begin_class
DECL|class|DirectUpdateHandlerTest
specifier|public
class|class
name|DirectUpdateHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|savedFactory
specifier|static
name|String
name|savedFactory
decl_stmt|;
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
name|savedFactory
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.DirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"org.apache.solr.core.MockFSDirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|systemSetPropertySolrTestsMergePolicy
argument_list|(
name|TieredMergePolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|systemSetPropertySolrTestsMergePolicyFactory
argument_list|(
name|TieredMergePolicyFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|systemClearPropertySolrTestsMergePolicy
argument_list|()
expr_stmt|;
name|systemClearPropertySolrTestsMergePolicyFactory
argument_list|()
expr_stmt|;
if|if
condition|(
name|savedFactory
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|savedFactory
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
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
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRequireUniqueKey
specifier|public
name|void
name|testRequireUniqueKey
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Add a valid document
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// More than one id should fail
name|assertFailedU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"id"
argument_list|,
literal|"ignore_exception"
argument_list|,
literal|"text"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No id should fail
name|ignoreException
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|assertFailedU
argument_list|(
name|adoc
argument_list|(
literal|"text"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
literal|"This test requires a schema that has no version field, "
operator|+
literal|"it appears the schema file in use has been edited to violate "
operator|+
literal|"this requirement"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getFieldOrNull
argument_list|(
name|VersionInfo
operator|.
name|VERSION_FIELD
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|)
argument_list|)
expr_stmt|;
comment|// search - not committed - docs should not be found.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// now they should be there
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// now delete one
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
comment|// not committed yet
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// 5 should be gone
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:5"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// now delete all
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// not committed yet
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// 6 should be gone
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:6"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddRollback
specifier|public
name|void
name|testAddRollback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// re-init the core
name|deleteCore
argument_list|()
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
comment|// commit "A"
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|updater
operator|instanceof
name|DirectUpdateHandler2
argument_list|)
expr_stmt|;
name|DirectUpdateHandler2
name|duh2
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|updater
decl_stmt|;
name|SolrQueryRequest
name|ureq
init|=
name|req
argument_list|()
decl_stmt|;
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|ureq
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cmtCmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
comment|// rollback "B"
name|ureq
operator|=
name|req
argument_list|()
expr_stmt|;
name|RollbackUpdateCommand
name|rbkCmd
init|=
operator|new
name|RollbackUpdateCommand
argument_list|(
name|ureq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|rollback
argument_list|(
name|rbkCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// search - "B" should not be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A OR id:B"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"B\" should not be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|)
expr_stmt|;
comment|// Add a doc after the rollback to make sure we can continue to add/delete documents
comment|// after a rollback as normal
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"ZZZ"
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
literal|"\"ZZZ\" must be found."
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:ZZZ"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='ZZZ']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeleteRollback
specifier|public
name|void
name|testDeleteRollback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// re-init the core
name|deleteCore
argument_list|()
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
comment|// commit "A", "B"
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|UpdateHandler
name|updater
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|updater
operator|instanceof
name|DirectUpdateHandler2
argument_list|)
expr_stmt|;
name|DirectUpdateHandler2
name|duh2
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|updater
decl_stmt|;
name|SolrQueryRequest
name|ureq
init|=
name|req
argument_list|()
decl_stmt|;
name|CommitUpdateCommand
name|cmtCmd
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|ureq
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cmtCmd
operator|.
name|waitSearcher
operator|=
literal|true
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|commit
argument_list|(
name|cmtCmd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|duh2
operator|.
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// search - "A","B" should be found.
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:A OR id:B"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"\"A\" and \"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// delete "B"
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"B"
argument_list|)
argument_list|)
expr_stmt|;
comment|// search - "A","B" should be found.
name|assertQ
argument_list|(
literal|"\"A\" and \"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// rollback "B"
name|ureq
operator|=
name|req
argument_list|()
expr_stmt|;
name|RollbackUpdateCommand
name|rbkCmd
init|=
operator|new
name|RollbackUpdateCommand
argument_list|(
name|ureq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|deleteByIdCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|updater
operator|.
name|rollback
argument_list|(
name|rbkCmd
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|deleteByIdCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|duh2
operator|.
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|duh2
operator|.
name|rollbackCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// search - "B" should be found.
name|assertQ
argument_list|(
literal|"\"B\" should be found."
argument_list|,
name|req
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='A']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='B']"
argument_list|)
expr_stmt|;
comment|// Add a doc after the rollback to make sure we can continue to add/delete documents
comment|// after a rollback as normal
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"ZZZ"
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
literal|"\"ZZZ\" must be found."
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:ZZZ"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='ZZZ']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExpungeDeletes
specifier|public
name|void
name|testExpungeDeletes
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// dup, triggers delete
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|sr
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|DirectoryReader
name|r
init|=
name|sr
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"maxDoc !> numDocs ... expected some deletions"
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
operator|>
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|sr
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"expungeDeletes"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|sr
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|r
operator|=
name|r
operator|=
name|sr
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// no deletions
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
comment|// no dups
name|sr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrepareCommit
specifier|public
name|void
name|testPrepareCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"999"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure there's just one segment
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// commit a second time to make sure index files aren't still referenced by the old searcher
name|SolrQueryRequest
name|sr
init|=
name|req
argument_list|()
decl_stmt|;
name|DirectoryReader
name|r
init|=
name|sr
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|Directory
name|d
init|=
name|r
operator|.
name|directory
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"FILES before addDoc="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|d
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|nFiles
init|=
name|d
operator|.
name|listAll
argument_list|()
operator|.
name|length
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"FILES before prepareCommit="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|d
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|""
argument_list|,
name|params
argument_list|(
literal|"prepareCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"FILES after prepareCommit="
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|d
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|d
operator|.
name|listAll
argument_list|()
operator|.
name|length
operator|>
name|nFiles
argument_list|)
expr_stmt|;
comment|// make sure new index files were actually written
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|""
argument_list|,
name|params
argument_list|(
literal|"rollback"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|updateJ
argument_list|(
literal|""
argument_list|,
name|params
argument_list|(
literal|"prepareCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/numFound==0"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|)
argument_list|,
literal|"/response/numFound==1"
argument_list|)
expr_stmt|;
name|sr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPostSoftCommitEvents
specifier|public
name|void
name|testPostSoftCommitEvents
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
assert|assert
name|core
operator|!=
literal|null
assert|;
name|DirectUpdateHandler2
name|updater
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|MySolrEventListener
name|listener
init|=
operator|new
name|MySolrEventListener
argument_list|()
decl_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|updater
operator|.
name|registerSoftCommitCallback
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"999"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"newSearcher was called more than once"
argument_list|,
literal|1
argument_list|,
name|listener
operator|.
name|newSearcherCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"postSoftCommit was not called"
argument_list|,
name|listener
operator|.
name|postSoftCommitAt
operator|.
name|get
argument_list|()
operator|==
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"newSearcher was called after postSoftCommitCallback"
argument_list|,
name|listener
operator|.
name|postSoftCommitAt
operator|.
name|get
argument_list|()
operator|>=
name|listener
operator|.
name|newSearcherOpenedAt
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|MySolrEventListener
specifier|static
class|class
name|MySolrEventListener
implements|implements
name|SolrEventListener
block|{
DECL|field|newSearcherCount
name|AtomicInteger
name|newSearcherCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|newSearcherOpenedAt
name|AtomicLong
name|newSearcherOpenedAt
init|=
operator|new
name|AtomicLong
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
DECL|field|postSoftCommitAt
name|AtomicLong
name|postSoftCommitAt
init|=
operator|new
name|AtomicLong
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|postSoftCommit
specifier|public
name|void
name|postSoftCommit
parameter_list|()
block|{
name|postSoftCommitAt
operator|.
name|set
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{
name|newSearcherCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|newSearcherOpenedAt
operator|.
name|set
argument_list|(
name|newSearcher
operator|.
name|getOpenNanoTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{      }
block|}
block|}
end_class

end_unit

