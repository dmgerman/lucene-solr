begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|facet
operator|.
name|FacetField
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
name|FacetsConfig
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
name|TaxonomyWriter
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
name|replicator
operator|.
name|IndexAndTaxonomyRevision
operator|.
name|SnapshotDirectoryTaxonomyWriter
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
DECL|class|IndexAndTaxonomyRevisionTest
specifier|public
class|class
name|IndexAndTaxonomyRevisionTest
extends|extends
name|ReplicatorTestCase
block|{
DECL|method|newDocument
specifier|private
name|Document
name|newDocument
parameter_list|(
name|TaxonomyWriter
name|taxoWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"A"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
return|;
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
name|indexDir
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
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SnapshotDirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|SnapshotDirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
comment|// should fail when there are no commits to snapshot
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|IndexAndTaxonomyRevision
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxoWriter
argument_list|,
name|taxoDir
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
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
name|indexDir
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
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SnapshotDirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|SnapshotDirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
try|try
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|taxoWriter
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Revision
name|rev1
init|=
operator|new
name|IndexAndTaxonomyRevision
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
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
name|indexDir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|slowFileExists
argument_list|(
name|taxoDir
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
name|IndexAndTaxonomyRevision
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
comment|// create revision again, so the files are snapshotted
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|taxoWriter
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
operator|new
name|IndexAndTaxonomyRevision
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
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
name|indexDir
argument_list|,
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
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
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|,
name|taxoDir
argument_list|,
name|indexDir
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
name|indexDir
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
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SnapshotDirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|SnapshotDirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
try|try
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|taxoWriter
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Revision
name|rev
init|=
operator|new
name|IndexAndTaxonomyRevision
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
decl_stmt|;
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
literal|2
argument_list|,
name|sourceFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|List
argument_list|<
name|RevisionFile
argument_list|>
name|files
range|:
name|sourceFiles
operator|.
name|values
argument_list|()
control|)
block|{
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
argument_list|)
expr_stmt|;
block|}
name|indexWriter
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
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|,
name|taxoDir
argument_list|,
name|indexDir
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
name|indexDir
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
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SnapshotDirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|SnapshotDirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
try|try
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|taxoWriter
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Revision
name|rev
init|=
operator|new
name|IndexAndTaxonomyRevision
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|e
range|:
name|rev
operator|.
name|getSourceFiles
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|source
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"resource"
argument_list|)
comment|// silly, both directories are closed in the end
name|Directory
name|dir
init|=
name|source
operator|.
name|equals
argument_list|(
name|IndexAndTaxonomyRevision
operator|.
name|INDEX_SOURCE
argument_list|)
condition|?
name|indexDir
else|:
name|taxoDir
decl_stmt|;
for|for
control|(
name|RevisionFile
name|file
range|:
name|e
operator|.
name|getValue
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
block|}
name|indexWriter
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
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|,
name|taxoDir
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

