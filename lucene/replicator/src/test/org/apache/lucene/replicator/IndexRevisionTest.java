begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|InputStream
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
name|document
operator|.
name|Document
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
name|IndexFileNames
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
name|KeepOnlyLastCommitDeletionPolicy
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
name|SnapshotDeletionPolicy
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
name|store
operator|.
name|IOContext
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
name|IndexInput
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
name|MockDirectoryWrapper
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|IndexRevisionTest
specifier|public
class|class
name|IndexRevisionTest
extends|extends
name|ReplicatorTestCase
block|{
annotation|@
name|Test
DECL|method|testNoSnapshotDeletionPolicy
specifier|public
name|void
name|testNoSnapshotDeletionPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed when IndexDeletionPolicy is not Snapshot"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNoCommit
specifier|public
name|void
name|testNoCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|assertNotNull
argument_list|(
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed when there are no commits to snapshot"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRevisionRelease
specifier|public
name|void
name|testRevisionRelease
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// we look to see that certain files are deleted:
if|if
condition|(
name|dir
operator|instanceof
name|MockDirectoryWrapper
condition|)
block|{
operator|(
operator|(
name|MockDirectoryWrapper
operator|)
name|dir
operator|)
operator|.
name|setEnableVirusScanner
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Revision
name|rev1
init|=
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
decl_stmt|;
comment|// releasing that revision should not delete the files
name|rev1
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
name|rev1
operator|=
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
expr_stmt|;
comment|// create revision again, so the files are snapshotted
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
name|rev1
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// this release should trigger the delete of segments_1
name|assertFalse
argument_list|(
name|slowFileExists
argument_list|(
name|dir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|writer
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSegmentsFileLast
specifier|public
name|void
name|testSegmentsFileLast
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Revision
name|rev
init|=
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|sourceFiles
init|=
name|rev
operator|.
name|getSourceFiles
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sourceFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RevisionFile
argument_list|>
name|files
init|=
name|sourceFiles
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|lastFile
init|=
name|files
operator|.
name|get
argument_list|(
name|files
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|fileName
decl_stmt|;
name|assertTrue
argument_list|(
name|lastFile
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|&&
operator|!
name|lastFile
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOpen
specifier|public
name|void
name|testOpen
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setIndexDeletionPolicy
argument_list|(
operator|new
name|SnapshotDeletionPolicy
argument_list|(
name|conf
operator|.
name|getIndexDeletionPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Revision
name|rev
init|=
operator|new
name|IndexRevision
argument_list|(
name|writer
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|sourceFiles
init|=
name|rev
operator|.
name|getSourceFiles
argument_list|()
decl_stmt|;
name|String
name|source
init|=
name|sourceFiles
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|RevisionFile
name|file
range|:
name|sourceFiles
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
control|)
block|{
name|IndexInput
name|src
init|=
name|dir
operator|.
name|openInput
argument_list|(
name|file
operator|.
name|fileName
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|rev
operator|.
name|open
argument_list|(
name|source
argument_list|,
name|file
operator|.
name|fileName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|src
operator|.
name|length
argument_list|()
argument_list|,
name|in
operator|.
name|available
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|srcBytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|src
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|byte
index|[]
name|inBytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|src
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|int
name|skip
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
if|if
condition|(
name|skip
operator|>=
name|src
operator|.
name|length
argument_list|()
condition|)
block|{
name|skip
operator|=
literal|0
expr_stmt|;
block|}
name|in
operator|.
name|skip
argument_list|(
name|skip
argument_list|)
expr_stmt|;
name|src
operator|.
name|seek
argument_list|(
name|skip
argument_list|)
expr_stmt|;
name|offset
operator|=
name|skip
expr_stmt|;
block|}
name|src
operator|.
name|readBytes
argument_list|(
name|srcBytes
argument_list|,
name|offset
argument_list|,
name|srcBytes
operator|.
name|length
operator|-
name|offset
argument_list|)
expr_stmt|;
name|in
operator|.
name|read
argument_list|(
name|inBytes
argument_list|,
name|offset
argument_list|,
name|inBytes
operator|.
name|length
operator|-
name|offset
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|srcBytes
argument_list|,
name|inBytes
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|src
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

