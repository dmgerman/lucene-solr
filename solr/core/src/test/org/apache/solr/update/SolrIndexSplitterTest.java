begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|index
operator|.
name|Term
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|EmbeddedSolrServer
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
name|cloud
operator|.
name|CompositeIdRouter
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
name|cloud
operator|.
name|DocRouter
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
name|cloud
operator|.
name|PlainIdRouter
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
name|Hash
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
name|DirectoryFactory
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
name|request
operator|.
name|LocalSolrQueryRequest
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

begin_class
DECL|class|SolrIndexSplitterTest
specifier|public
class|class
name|SolrIndexSplitterTest
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
DECL|field|indexDir1
DECL|field|indexDir2
DECL|field|indexDir3
name|File
name|indexDir1
init|=
literal|null
decl_stmt|,
name|indexDir2
init|=
literal|null
decl_stmt|,
name|indexDir3
init|=
literal|null
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
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
name|indexDir1
operator|=
name|createTempDir
argument_list|(
literal|"_testSplit1"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|indexDir2
operator|=
name|createTempDir
argument_list|(
literal|"_testSplit2"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|indexDir3
operator|=
name|createTempDir
argument_list|(
literal|"_testSplit3"
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplitByPaths
specifier|public
name|void
name|testSplitByPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// add two docs
name|String
name|id1
init|=
literal|"dorothy"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id1
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id2
init|=
literal|"kansas"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id2
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
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
comment|// find minHash/maxHash hash ranges
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
name|getRanges
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
decl_stmt|;
name|request
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|SplitIndexCommand
name|command
init|=
operator|new
name|SplitIndexCommand
argument_list|(
name|request
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|ranges
argument_list|,
operator|new
name|PlainIdRouter
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
operator|new
name|SolrIndexSplitter
argument_list|(
name|command
argument_list|)
operator|.
name|split
argument_list|()
expr_stmt|;
name|Directory
name|directory
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
decl_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"id:dorothy should be present in split index1"
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"dorothy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:kansas should not be present in split index1"
argument_list|,
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"kansas"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"split index1 should have only one document"
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:dorothy should not be present in split index2"
argument_list|,
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"dorothy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:kansas should be present in split index2"
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"kansas"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"split index2 should have only one document"
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
name|request
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// decrefs the searcher
block|}
block|}
comment|// SOLR-5144
DECL|method|testSplitDeletes
specifier|public
name|void
name|testSplitDeletes
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// add two docs
name|String
name|id1
init|=
literal|"dorothy"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id1
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id2
init|=
literal|"kansas"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id2
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
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
name|id2
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete id2
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// find minHash/maxHash hash ranges
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
name|getRanges
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
decl_stmt|;
name|request
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|SplitIndexCommand
name|command
init|=
operator|new
name|SplitIndexCommand
argument_list|(
name|request
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|ranges
argument_list|,
operator|new
name|PlainIdRouter
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
operator|new
name|SolrIndexSplitter
argument_list|(
name|command
argument_list|)
operator|.
name|split
argument_list|()
expr_stmt|;
name|Directory
name|directory
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
decl_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"id:dorothy should be present in split index1"
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"dorothy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:kansas should not be present in split index1"
argument_list|,
literal|0
argument_list|,
name|reader
operator|.
name|docFreq
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"kansas"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"split index1 should have only one document"
argument_list|,
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
comment|// should be empty
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
name|request
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// decrefs the searcher
block|}
block|}
annotation|@
name|Test
DECL|method|testSplitByCores
specifier|public
name|void
name|testSplitByCores
parameter_list|()
throws|throws
name|Exception
block|{
comment|// add two docs
name|String
name|id1
init|=
literal|"dorothy"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id1
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|id2
init|=
literal|"kansas"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|id2
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
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
name|getRanges
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|)
decl_stmt|;
name|SolrCore
name|core1
init|=
literal|null
decl_stmt|,
name|core2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|core1
operator|=
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|create
argument_list|(
literal|"split1"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"dataDir"
argument_list|,
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"configSet"
argument_list|,
literal|"minimal"
argument_list|)
argument_list|)
expr_stmt|;
name|core2
operator|=
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|create
argument_list|(
literal|"split2"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"dataDir"
argument_list|,
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"configSet"
argument_list|,
literal|"minimal"
argument_list|)
argument_list|)
expr_stmt|;
name|LocalSolrQueryRequest
name|request
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|SplitIndexCommand
name|command
init|=
operator|new
name|SplitIndexCommand
argument_list|(
name|request
argument_list|,
literal|null
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|core1
argument_list|,
name|core2
argument_list|)
argument_list|,
name|ranges
argument_list|,
operator|new
name|PlainIdRouter
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
operator|new
name|SolrIndexSplitter
argument_list|(
name|command
argument_list|)
operator|.
name|split
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
name|request
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|EmbeddedSolrServer
name|server1
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
literal|"split1"
argument_list|)
decl_stmt|;
name|EmbeddedSolrServer
name|server2
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
literal|"split2"
argument_list|)
decl_stmt|;
name|server1
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|server2
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:dorothy should be present in split index1"
argument_list|,
literal|1
argument_list|,
name|server1
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:dorothy"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:kansas should not be present in split index1"
argument_list|,
literal|0
argument_list|,
name|server1
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:kansas"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:dorothy should not be present in split index2"
argument_list|,
literal|0
argument_list|,
name|server2
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:dorothy"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id:kansas should be present in split index2"
argument_list|,
literal|1
argument_list|,
name|server2
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"id:kansas"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|unload
argument_list|(
literal|"split2"
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|unload
argument_list|(
literal|"split1"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSplitAlternately
specifier|public
name|void
name|testSplitAlternately
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|request
init|=
literal|null
decl_stmt|;
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// add an even number of docs
name|int
name|max
init|=
operator|(
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|)
operator|*
literal|3
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding {} number of documents"
argument_list|,
name|max
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|max
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|SplitIndexCommand
name|command
init|=
operator|new
name|SplitIndexCommand
argument_list|(
name|request
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|indexDir3
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|PlainIdRouter
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
operator|new
name|SolrIndexSplitter
argument_list|(
name|command
argument_list|)
operator|.
name|split
argument_list|()
expr_stmt|;
name|directory
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir1
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"split index1 has wrong number of documents"
argument_list|,
name|max
operator|/
literal|3
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir2
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"split index2 has wrong number of documents"
argument_list|,
name|max
operator|/
literal|3
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir3
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"split index3 has wrong number of documents"
argument_list|,
name|max
operator|/
literal|3
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
name|request
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// decrefs the searcher
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
comment|// perhaps an assert failed, release the directory
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSplitByRouteKey
specifier|public
name|void
name|testSplitByRouteKey
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|indexDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|CompositeIdRouter
name|r1
init|=
operator|new
name|CompositeIdRouter
argument_list|()
decl_stmt|;
name|String
name|splitKey
init|=
literal|"sea-line!"
decl_stmt|;
name|String
name|key2
init|=
literal|"soul-raising!"
decl_stmt|;
comment|// murmur2 has a collision on the above two keys
name|assertEquals
argument_list|(
name|r1
operator|.
name|keyHashRange
argument_list|(
name|splitKey
argument_list|)
argument_list|,
name|r1
operator|.
name|keyHashRange
argument_list|(
name|key2
argument_list|)
argument_list|)
expr_stmt|;
comment|/*     More strings with collisions on murmur2 for future reference:     "Drava" "dessert spoon"     "Bighorn" "pleasure lover"     "attributable to" "second edition"     "sea-line" "soul-raising"     "lift direction" "testimony meeting"      */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|splitKey
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|key2
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
literal|"*:*"
argument_list|)
argument_list|,
literal|"/response/numFound==20"
argument_list|)
expr_stmt|;
name|DocRouter
operator|.
name|Range
name|splitKeyRange
init|=
name|r1
operator|.
name|keyHashRange
argument_list|(
name|splitKey
argument_list|)
decl_stmt|;
name|LocalSolrQueryRequest
name|request
init|=
literal|null
decl_stmt|;
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
try|try
block|{
name|request
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"dummy"
argument_list|)
expr_stmt|;
name|SplitIndexCommand
name|command
init|=
operator|new
name|SplitIndexCommand
argument_list|(
name|request
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|splitKeyRange
argument_list|)
argument_list|,
operator|new
name|CompositeIdRouter
argument_list|()
argument_list|,
literal|null
argument_list|,
name|splitKey
argument_list|)
decl_stmt|;
operator|new
name|SolrIndexSplitter
argument_list|(
name|command
argument_list|)
operator|.
name|split
argument_list|()
expr_stmt|;
name|directory
operator|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|DirectoryFactory
operator|.
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"split index has wrong number of documents"
argument_list|,
literal|10
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
block|{
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getRanges
specifier|private
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|getRanges
parameter_list|(
name|String
name|id1
parameter_list|,
name|String
name|id2
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
comment|// find minHash/maxHash hash ranges
name|byte
index|[]
name|bytes
init|=
name|id1
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|int
name|minHash
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|bytes
operator|=
name|id2
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|int
name|maxHash
init|=
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|minHash
operator|>
name|maxHash
condition|)
block|{
name|int
name|temp
init|=
name|maxHash
decl_stmt|;
name|maxHash
operator|=
name|minHash
expr_stmt|;
name|minHash
operator|=
name|temp
expr_stmt|;
block|}
name|PlainIdRouter
name|router
init|=
operator|new
name|PlainIdRouter
argument_list|()
decl_stmt|;
name|DocRouter
operator|.
name|Range
name|fullRange
init|=
operator|new
name|DocRouter
operator|.
name|Range
argument_list|(
name|minHash
argument_list|,
name|maxHash
argument_list|)
decl_stmt|;
return|return
name|router
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
name|fullRange
argument_list|)
return|;
block|}
block|}
end_class

end_unit

