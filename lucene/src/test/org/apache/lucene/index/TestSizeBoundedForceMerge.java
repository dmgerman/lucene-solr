begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|RAMDirectory
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

begin_class
DECL|class|TestSizeBoundedForceMerge
specifier|public
class|class
name|TestSizeBoundedForceMerge
extends|extends
name|LuceneTestCase
block|{
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|newWriterConfig
specifier|private
specifier|static
name|IndexWriterConfig
name|newWriterConfig
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DEFAULT_RAM_BUFFER_SIZE_MB
argument_list|)
expr_stmt|;
comment|// prevent any merges by default.
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|testByteSizeLimit
specifier|public
name|void
name|testByteSizeLimit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that the max merge size constraint is applied during forceMerge.
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// Prepare an index w/ several small segments and a large one.
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
specifier|final
name|int
name|numSegments
init|=
literal|15
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSegments
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numDocs
init|=
name|i
operator|==
literal|7
condition|?
literal|30
else|:
literal|1
decl_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|double
name|min
init|=
name|sis
operator|.
name|info
argument_list|(
literal|0
argument_list|)
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogByteSizeMergePolicy
name|lmp
init|=
operator|new
name|LogByteSizeMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeMBForForcedMerge
argument_list|(
operator|(
name|min
operator|+
literal|1
operator|)
operator|/
operator|(
literal|1
operator|<<
literal|20
operator|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Should only be 3 segments in the index, because one of them exceeds the size limit
name|sis
operator|=
operator|new
name|SegmentInfos
argument_list|()
expr_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNumDocsLimit
specifier|public
name|void
name|testNumDocsLimit
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that the max merge docs constraint is applied during forceMerge.
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
comment|// Prepare an index w/ several small segments and a large one.
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Should only be 3 segments in the index, because one of them exceeds the size limit
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLastSegmentTooLarge
specifier|public
name|void
name|testLastSegmentTooLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstSegmentTooLarge
specifier|public
name|void
name|testFirstSegmentTooLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllSegmentsSmall
specifier|public
name|void
name|testAllSegmentsSmall
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllSegmentsLarge
specifier|public
name|void
name|testAllSegmentsLarge
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneLargeOneSmall
specifier|public
name|void
name|testOneLargeOneSmall
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeFactor
specifier|public
name|void
name|testMergeFactor
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|lmp
operator|.
name|setMergeFactor
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Should only be 4 segments in the index, because of the merge factor and
comment|// max merge docs settings.
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* nocommit: Fix tests to use an id and delete by term   public void testSingleMergeableSegment() throws Exception {     Directory dir = new RAMDirectory();          IndexWriterConfig conf = newWriterConfig();     IndexWriter writer = new IndexWriter(dir, conf);          addDocs(writer, 3);     addDocs(writer, 5);     addDocs(writer, 3);          writer.close();        // delete the last document, so that the last segment is merged.     IndexReader r = IndexReader.open(dir);     r.deleteDocument(r.numDocs() - 1);     r.close();          conf = newWriterConfig();     LogMergePolicy lmp = new LogDocMergePolicy();     lmp.setMaxMergeDocs(3);     conf.setMergePolicy(lmp);          writer = new IndexWriter(dir, conf);     writer.forceMerge(1);     writer.close();          // Verify that the last segment does not have deletions.     SegmentInfos sis = new SegmentInfos();     sis.read(dir);     assertEquals(3, sis.size());     assertFalse(sis.info(2).hasDeletions());   }   */
DECL|method|testSingleNonMergeableSegment
specifier|public
name|void
name|testSingleNonMergeableSegment
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|conf
init|=
name|newWriterConfig
argument_list|()
decl_stmt|;
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
name|addDocs
argument_list|(
name|writer
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|=
name|newWriterConfig
argument_list|()
expr_stmt|;
name|LogMergePolicy
name|lmp
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|lmp
operator|.
name|setMaxMergeDocs
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|lmp
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Verify that the last segment does not have deletions.
name|SegmentInfos
name|sis
init|=
operator|new
name|SegmentInfos
argument_list|()
decl_stmt|;
name|sis
operator|.
name|read
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sis
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* nocommit: Fix tests to use an id and delete by term   public void testSingleMergeableTooLargeSegment() throws Exception {     Directory dir = new RAMDirectory();          IndexWriterConfig conf = newWriterConfig();     IndexWriter writer = new IndexWriter(dir, conf);          addDocs(writer, 5);          writer.close();        // delete the last document     IndexReader r = IndexReader.open(dir);     r.deleteDocument(r.numDocs() - 1);     r.close();          conf = newWriterConfig();     LogMergePolicy lmp = new LogDocMergePolicy();     lmp.setMaxMergeDocs(2);     conf.setMergePolicy(lmp);          writer = new IndexWriter(dir, conf);     writer.forceMerge(1);     writer.close();          // Verify that the last segment does not have deletions.     SegmentInfos sis = new SegmentInfos();     sis.read(dir);     assertEquals(1, sis.size());     assertTrue(sis.info(0).hasDeletions());   }   */
block|}
end_class

end_unit

