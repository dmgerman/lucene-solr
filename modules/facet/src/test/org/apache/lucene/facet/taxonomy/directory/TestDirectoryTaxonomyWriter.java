begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|directory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|InconsistentTaxonomyException
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
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|TaxonomyWriterCache
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
name|IndexReader
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|AlreadyClosedException
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestDirectoryTaxonomyWriter
specifier|public
class|class
name|TestDirectoryTaxonomyWriter
extends|extends
name|LuceneTestCase
block|{
comment|// A No-Op TaxonomyWriterCache which always discards all given categories, and
comment|// always returns true in put(), to indicate some cache entries were cleared.
DECL|class|NoOpCache
specifier|private
specifier|static
class|class
name|NoOpCache
implements|implements
name|TaxonomyWriterCache
block|{
DECL|method|NoOpCache
name|NoOpCache
parameter_list|()
block|{ }
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|categoryPath
argument_list|)
return|;
block|}
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|,
name|int
name|prefixLen
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
DECL|method|hasRoom
specifier|public
name|boolean
name|hasRoom
parameter_list|(
name|int
name|numberOfEntries
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCommit
specifier|public
name|void
name|testCommit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verifies that nothing is committed to the underlying Directory, if
comment|// commit() wasn't called.
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|ltw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
argument_list|)
expr_stmt|;
name|ltw
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// first commit, so that an index will be created
name|ltw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"No categories should have been committed to the underlying directory"
argument_list|,
literal|1
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|ltw
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitUserData
specifier|public
name|void
name|testCommitUserData
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verifies taxonomy commit data
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
decl_stmt|;
name|taxoWriter
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|userCommitData
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|userCommitData
operator|.
name|put
argument_list|(
literal|"testing"
argument_list|,
literal|"1 2 3"
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|(
name|userCommitData
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2 categories plus root should have been committed to the underlying directory"
argument_list|,
literal|3
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readUserCommitData
init|=
name|r
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"wrong value extracted from commit data"
argument_list|,
literal|"1 2 3"
operator|.
name|equals
argument_list|(
name|readUserCommitData
operator|.
name|get
argument_list|(
literal|"testing"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"index.create.time not found in commitData"
argument_list|,
name|readUserCommitData
operator|.
name|get
argument_list|(
name|DirectoryTaxonomyWriter
operator|.
name|INDEX_CREATE_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// open DirTaxoWriter again and commit, INDEX_CREATE_TIME should still exist
comment|// in the commit data, otherwise DirTaxoReader.refresh() might not detect
comment|// that the taxonomy index has been recreated.
name|taxoWriter
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a category so that commit will happen
name|taxoWriter
operator|.
name|commit
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"just"
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|r
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|readUserCommitData
operator|=
name|r
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"index.create.time not found in commitData"
argument_list|,
name|readUserCommitData
operator|.
name|get
argument_list|(
name|DirectoryTaxonomyWriter
operator|.
name|INDEX_CREATE_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRollback
specifier|public
name|void
name|testRollback
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verifies that if callback is called, DTW is closed.
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|dtw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dtw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|dtw
operator|.
name|rollback
argument_list|()
expr_stmt|;
try|try
block|{
name|dtw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to add a category following rollback."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnsureOpen
specifier|public
name|void
name|testEnsureOpen
parameter_list|()
throws|throws
name|Exception
block|{
comment|// verifies that an exception is thrown if DTW was closed
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|dtw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|dtw
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|dtw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not have succeeded to add a category following close."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|touchTaxo
specifier|private
name|void
name|touchTaxo
parameter_list|(
name|DirectoryTaxonomyWriter
name|taxoWriter
parameter_list|,
name|CategoryPath
name|cp
parameter_list|)
throws|throws
name|IOException
block|{
name|taxoWriter
operator|.
name|addCategory
argument_list|(
name|cp
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"just"
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecreateAndRefresh
specifier|public
name|void
name|testRecreateAndRefresh
parameter_list|()
throws|throws
name|Exception
block|{
comment|// DirTaxoWriter lost the INDEX_CREATE_TIME property if it was opened in
comment|// CREATE_OR_APPEND (or commit(userData) called twice), which could lead to
comment|// DirTaxoReader succeeding to refresh().
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
decl_stmt|;
name|touchTaxo
argument_list|(
name|taxoWriter
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|DirectoryTaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|touchTaxo
argument_list|(
name|taxoWriter
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// this should not fail
name|taxoReader
operator|.
name|refresh
argument_list|()
expr_stmt|;
comment|// now recreate the taxonomy, and check that the timestamp is preserved after opening DirTW again.
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoWriter
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
expr_stmt|;
name|touchTaxo
argument_list|(
name|taxoWriter
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoWriter
operator|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
expr_stmt|;
name|touchTaxo
argument_list|(
name|taxoWriter
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// this should fail
try|try
block|{
name|taxoReader
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"IconsistentTaxonomyException should have been thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InconsistentTaxonomyException
name|e
parameter_list|)
block|{
comment|// ok, expected
block|}
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUndefinedCreateTime
specifier|public
name|void
name|testUndefinedCreateTime
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that if the taxonomy index doesn't have the INDEX_CREATE_TIME
comment|// property (supports pre-3.6 indexes), all still works.
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// create an empty index first, so that DirTaxoWriter initializes createTime to null.
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dir
argument_list|,
name|OpenMode
operator|.
name|CREATE_OR_APPEND
argument_list|,
operator|new
name|NoOpCache
argument_list|()
argument_list|)
decl_stmt|;
comment|// we cannot commit null keys/values, this ensures that if DirTW.createTime is null, we can still commit.
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|taxoReader
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

