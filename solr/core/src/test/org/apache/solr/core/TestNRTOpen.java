begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|AtomicReaderContext
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
name|AfterClass
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

begin_class
DECL|class|TestNRTOpen
specifier|public
class|class
name|TestNRTOpen
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
comment|// use a filesystem, because we need to create an index, then "start up solr"
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
comment|// and dont delete it initially
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// set these so that merges won't break the test
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.maxBufferedDocs"
argument_list|,
literal|"100000"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.tests.mergePolicy"
argument_list|,
literal|"org.apache.lucene.index.LogDocMergePolicy"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
comment|// add a doc
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|myDir
init|=
name|initCoreDataDir
decl_stmt|;
name|deleteCore
argument_list|()
expr_stmt|;
comment|// boot up again over the same index
name|initCoreDataDir
operator|=
name|myDir
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|)
expr_stmt|;
comment|// startup
name|assertNRT
argument_list|(
literal|1
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
throws|throws
name|Exception
block|{
comment|// ensure we clean up after ourselves, this will fire before superclass...
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.tests.maxBufferedDocs"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.tests.mergePolicy"
argument_list|)
expr_stmt|;
block|}
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
comment|// delete all, then add initial doc
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testReaderIsNRT
specifier|public
name|void
name|testReaderIsNRT
parameter_list|()
block|{
comment|// core reload
name|String
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|assertNRT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// add a doc and soft commit
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"baz"
argument_list|,
literal|"doc"
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
name|assertNRT
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// add a doc and hard commit
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"bazz"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertNRT
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// add a doc and core reload
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"bazzz"
argument_list|,
literal|"doc2"
argument_list|)
argument_list|)
expr_stmt|;
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|assertNRT
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|testSharedCores
specifier|public
name|void
name|testSharedCores
parameter_list|()
block|{
comment|// clear out any junk
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|s1
init|=
name|getCoreCacheKeys
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// add a doc, will go in a new segment
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"baz"
argument_list|,
literal|"doc"
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
name|Set
argument_list|<
name|Object
argument_list|>
name|s2
init|=
name|getCoreCacheKeys
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|s2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s2
operator|.
name|containsAll
argument_list|(
name|s1
argument_list|)
argument_list|)
expr_stmt|;
comment|// add two docs, will go in a new segment
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"foo"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"foo2"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|s3
init|=
name|getCoreCacheKeys
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|s3
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|s3
operator|.
name|containsAll
argument_list|(
name|s2
argument_list|)
argument_list|)
expr_stmt|;
comment|// delete a doc
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"foo2:doc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// same cores
name|assertEquals
argument_list|(
name|s3
argument_list|,
name|getCoreCacheKeys
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNRT
specifier|static
name|void
name|assertNRT
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|DirectoryReader
name|ir
init|=
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getRawReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|maxDoc
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected NRT reader, got: "
operator|+
name|ir
argument_list|,
name|ir
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|":nrt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getCoreCacheKeys
specifier|private
name|Set
argument_list|<
name|Object
argument_list|>
name|getCoreCacheKeys
parameter_list|()
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|set
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|IdentityHashMap
argument_list|<
name|Object
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|DirectoryReader
name|ir
init|=
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getRawReader
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|ir
operator|.
name|leaves
argument_list|()
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getCoreCacheKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
block|}
end_class

end_unit

