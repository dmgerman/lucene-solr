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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
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
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
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
name|LockObtainFailedException
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
name|LineFileDocs
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ThrottledIndexOutput
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

begin_class
DECL|class|TestFlushByRamOrCountsPolicy
specifier|public
class|class
name|TestFlushByRamOrCountsPolicy
extends|extends
name|LuceneTestCase
block|{
DECL|field|lineDocFile
specifier|private
name|LineFileDocs
name|lineDocFile
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
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
name|lineDocFile
operator|=
operator|new
name|LineFileDocs
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|testFlushByRam
specifier|public
name|void
name|testFlushByRam
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|int
index|[]
name|numThreads
init|=
operator|new
name|int
index|[]
block|{
literal|3
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|12
argument_list|)
block|,
literal|1
block|}
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
name|numThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|runFlushByRam
argument_list|(
name|numThreads
index|[
name|i
index|]
argument_list|,
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// with a 512 mb ram buffer we should never stall
name|runFlushByRam
argument_list|(
name|numThreads
index|[
name|i
index|]
argument_list|,
literal|512.d
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runFlushByRam
specifier|protected
name|void
name|runFlushByRam
parameter_list|(
name|int
name|numThreads
parameter_list|,
name|double
name|maxRam
parameter_list|,
name|boolean
name|ensureNotStalled
parameter_list|)
throws|throws
name|IOException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|numDocumentsToIndex
init|=
literal|50
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|150
argument_list|)
decl_stmt|;
name|AtomicInteger
name|numDocs
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numDocumentsToIndex
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|MockDefaultFlushPolicy
name|flushPolicy
init|=
operator|new
name|MockDefaultFlushPolicy
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setFlushPolicy
argument_list|(
name|flushPolicy
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDWPT
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|DocumentsWriterPerThreadPool
name|threadPool
init|=
operator|new
name|ThreadAffinityDocumentsWriterThreadPool
argument_list|(
name|numDWPT
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setIndexerThreadPool
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
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
name|iwc
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|flushPolicy
operator|.
name|flushOnDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|flushPolicy
operator|.
name|flushOnDeleteTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|flushPolicy
operator|.
name|flushOnRAM
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentsWriter
name|docsWriter
init|=
name|writer
operator|.
name|getDocsWriter
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsWriter
argument_list|)
expr_stmt|;
name|DocumentsWriterFlushControl
name|flushControl
init|=
name|docsWriter
operator|.
name|flushControl
decl_stmt|;
name|assertEquals
argument_list|(
literal|" bytes must be 0 after init"
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|IndexThread
index|[]
name|threads
init|=
operator|new
name|IndexThread
index|[
name|numThreads
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|=
operator|new
name|IndexThread
argument_list|(
name|numDocs
argument_list|,
name|numThreads
argument_list|,
name|writer
argument_list|,
name|lineDocFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|threads
index|[
name|x
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|maxRAMBytes
init|=
call|(
name|long
call|)
argument_list|(
name|iwc
operator|.
name|getRAMBufferSizeMB
argument_list|()
operator|*
literal|1024.
operator|*
literal|1024.
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|" all flushes must be due numThreads="
operator|+
name|numThreads
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"peak bytes without flush exceeded watermark"
argument_list|,
name|flushPolicy
operator|.
name|peakBytesWithoutFlush
operator|<=
name|maxRAMBytes
argument_list|)
expr_stmt|;
name|assertActiveBytesAfter
argument_list|(
name|flushControl
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushPolicy
operator|.
name|hasMarkedPending
condition|)
block|{
name|assertTrue
argument_list|(
name|maxRAMBytes
operator|<
name|flushControl
operator|.
name|peakActiveBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ensureNotStalled
condition|)
block|{
name|assertFalse
argument_list|(
name|docsWriter
operator|.
name|healthiness
operator|.
name|wasStalled
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|flushControl
operator|.
name|activeBytes
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFlushDocCount
specifier|public
name|void
name|testFlushDocCount
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|int
index|[]
name|numThreads
init|=
operator|new
name|int
index|[]
block|{
literal|3
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|12
argument_list|)
block|,
literal|1
block|}
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
name|numThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|numDocumentsToIndex
init|=
literal|50
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|150
argument_list|)
decl_stmt|;
name|AtomicInteger
name|numDocs
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numDocumentsToIndex
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|MockDefaultFlushPolicy
name|flushPolicy
init|=
operator|new
name|MockDefaultFlushPolicy
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
operator|.
name|setFlushPolicy
argument_list|(
name|flushPolicy
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDWPT
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|DocumentsWriterPerThreadPool
name|threadPool
init|=
operator|new
name|ThreadAffinityDocumentsWriterThreadPool
argument_list|(
name|numDWPT
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setIndexerThreadPool
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
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
name|iwc
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|flushPolicy
operator|.
name|flushOnDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|flushPolicy
operator|.
name|flushOnDeleteTerms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|flushPolicy
operator|.
name|flushOnRAM
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentsWriter
name|docsWriter
init|=
name|writer
operator|.
name|getDocsWriter
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsWriter
argument_list|)
expr_stmt|;
name|DocumentsWriterFlushControl
name|flushControl
init|=
name|docsWriter
operator|.
name|flushControl
decl_stmt|;
name|assertEquals
argument_list|(
literal|" bytes must be 0 after init"
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|IndexThread
index|[]
name|threads
init|=
operator|new
name|IndexThread
index|[
name|numThreads
index|[
name|i
index|]
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|=
operator|new
name|IndexThread
argument_list|(
name|numDocs
argument_list|,
name|numThreads
index|[
name|i
index|]
argument_list|,
name|writer
argument_list|,
name|lineDocFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|threads
index|[
name|x
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|" all flushes must be due numThreads="
operator|+
name|numThreads
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"peak bytes without flush exceeded watermark"
argument_list|,
name|flushPolicy
operator|.
name|peakDocCountWithoutFlush
operator|<=
name|iwc
operator|.
name|getMaxBufferedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertActiveBytesAfter
argument_list|(
name|flushControl
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|flushControl
operator|.
name|activeBytes
argument_list|()
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|int
name|numThreads
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numDocumentsToIndex
init|=
literal|100
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|300
argument_list|)
decl_stmt|;
name|AtomicInteger
name|numDocs
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numDocumentsToIndex
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|MockDefaultFlushPolicy
name|flushPolicy
init|=
operator|new
name|MockDefaultFlushPolicy
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setFlushPolicy
argument_list|(
name|flushPolicy
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDWPT
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|DocumentsWriterPerThreadPool
name|threadPool
init|=
operator|new
name|ThreadAffinityDocumentsWriterThreadPool
argument_list|(
name|numDWPT
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setIndexerThreadPool
argument_list|(
name|threadPool
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
name|iwc
argument_list|)
decl_stmt|;
name|DocumentsWriter
name|docsWriter
init|=
name|writer
operator|.
name|getDocsWriter
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsWriter
argument_list|)
expr_stmt|;
name|DocumentsWriterFlushControl
name|flushControl
init|=
name|docsWriter
operator|.
name|flushControl
decl_stmt|;
name|assertEquals
argument_list|(
literal|" bytes must be 0 after init"
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|IndexThread
index|[]
name|threads
init|=
operator|new
name|IndexThread
index|[
name|numThreads
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|=
operator|new
name|IndexThread
argument_list|(
name|numDocs
argument_list|,
name|numThreads
argument_list|,
name|writer
argument_list|,
name|lineDocFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|threads
index|[
name|x
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|" all flushes must be due"
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushPolicy
operator|.
name|flushOnRAM
argument_list|()
operator|&&
operator|!
name|flushPolicy
operator|.
name|flushOnDocCount
argument_list|()
operator|&&
operator|!
name|flushPolicy
operator|.
name|flushOnDeleteTerms
argument_list|()
condition|)
block|{
specifier|final
name|long
name|maxRAMBytes
init|=
call|(
name|long
call|)
argument_list|(
name|iwc
operator|.
name|getRAMBufferSizeMB
argument_list|()
operator|*
literal|1024.
operator|*
literal|1024.
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"peak bytes without flush exceeded watermark"
argument_list|,
name|flushPolicy
operator|.
name|peakBytesWithoutFlush
operator|<=
name|maxRAMBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushPolicy
operator|.
name|hasMarkedPending
condition|)
block|{
name|assertTrue
argument_list|(
literal|"max: "
operator|+
name|maxRAMBytes
operator|+
literal|" "
operator|+
name|flushControl
operator|.
name|peakActiveBytes
argument_list|,
name|maxRAMBytes
operator|<=
name|flushControl
operator|.
name|peakActiveBytes
argument_list|)
expr_stmt|;
block|}
block|}
name|assertActiveBytesAfter
argument_list|(
name|flushControl
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|flushControl
operator|.
name|activeBytes
argument_list|()
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|flushPolicy
operator|.
name|flushOnRAM
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
literal|"never stall if we don't flush on RAM"
argument_list|,
name|docsWriter
operator|.
name|healthiness
operator|.
name|wasStalled
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"never block if we don't flush on RAM"
argument_list|,
name|docsWriter
operator|.
name|healthiness
operator|.
name|hasBlocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
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
DECL|method|testHealthyness
specifier|public
name|void
name|testHealthyness
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|int
index|[]
name|numThreads
init|=
operator|new
name|int
index|[]
block|{
literal|4
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
block|,
literal|1
block|}
decl_stmt|;
specifier|final
name|int
name|numDocumentsToIndex
init|=
literal|50
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
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
name|numThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|AtomicInteger
name|numDocs
init|=
operator|new
name|AtomicInteger
argument_list|(
name|numDocumentsToIndex
argument_list|)
decl_stmt|;
name|MockDirectoryWrapper
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
comment|// mock a very slow harddisk here so that flushing is very slow
name|dir
operator|.
name|setThrottledIndexOutput
argument_list|(
operator|new
name|ThrottledIndexOutput
argument_list|(
name|ThrottledIndexOutput
operator|.
name|mBitsToBytes
argument_list|(
literal|40
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|,
literal|5
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|iwc
operator|.
name|setMaxBufferedDeleteTerms
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
expr_stmt|;
name|FlushPolicy
name|flushPolicy
init|=
operator|new
name|FlushByRamOrCountsPolicy
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setFlushPolicy
argument_list|(
name|flushPolicy
argument_list|)
expr_stmt|;
name|DocumentsWriterPerThreadPool
name|threadPool
init|=
operator|new
name|ThreadAffinityDocumentsWriterThreadPool
argument_list|(
name|numThreads
index|[
name|i
index|]
operator|==
literal|1
condition|?
literal|1
else|:
literal|2
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setIndexerThreadPool
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
comment|// with such a small ram buffer we should be stalled quiet quickly
name|iwc
operator|.
name|setRAMBufferSizeMB
argument_list|(
literal|0.25
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
name|iwc
argument_list|)
decl_stmt|;
name|IndexThread
index|[]
name|threads
init|=
operator|new
name|IndexThread
index|[
name|numThreads
index|[
name|i
index|]
index|]
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|=
operator|new
name|IndexThread
argument_list|(
name|numDocs
argument_list|,
name|numThreads
index|[
name|i
index|]
argument_list|,
name|writer
argument_list|,
name|lineDocFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|threads
index|[
name|x
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threads
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|threads
index|[
name|x
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|DocumentsWriter
name|docsWriter
init|=
name|writer
operator|.
name|getDocsWriter
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|docsWriter
argument_list|)
expr_stmt|;
name|DocumentsWriterFlushControl
name|flushControl
init|=
name|docsWriter
operator|.
name|flushControl
decl_stmt|;
name|assertEquals
argument_list|(
literal|" all flushes must be due"
argument_list|,
literal|0
argument_list|,
name|flushControl
operator|.
name|flushBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDocumentsToIndex
argument_list|,
name|writer
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|numThreads
index|[
name|i
index|]
operator|==
literal|1
condition|)
block|{
name|assertFalse
argument_list|(
literal|"single thread must not stall"
argument_list|,
name|docsWriter
operator|.
name|healthiness
operator|.
name|wasStalled
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"single thread must not block numThreads: "
operator|+
name|numThreads
index|[
name|i
index|]
argument_list|,
name|docsWriter
operator|.
name|healthiness
operator|.
name|hasBlocked
argument_list|()
argument_list|)
expr_stmt|;
comment|// this assumption is too strict in this test
comment|//      } else {
comment|//        if (docsWriter.healthiness.wasStalled) {
comment|//          // TODO maybe this assumtion is too strickt
comment|//          assertTrue(" we should have blocked here numThreads: "
comment|//              + numThreads[i], docsWriter.healthiness.hasBlocked());
comment|//        }
block|}
name|assertActiveBytesAfter
argument_list|(
name|flushControl
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|assertActiveBytesAfter
specifier|protected
name|void
name|assertActiveBytesAfter
parameter_list|(
name|DocumentsWriterFlushControl
name|flushControl
parameter_list|)
block|{
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|allActiveThreads
init|=
name|flushControl
operator|.
name|allActiveThreads
argument_list|()
decl_stmt|;
name|long
name|bytesUsed
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|allActiveThreads
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|bytesUsed
operator|+=
name|allActiveThreads
operator|.
name|next
argument_list|()
operator|.
name|perThread
operator|.
name|bytesUsed
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|bytesUsed
argument_list|,
name|flushControl
operator|.
name|activeBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|IndexThread
specifier|public
class|class
name|IndexThread
extends|extends
name|Thread
block|{
DECL|field|writer
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|iwc
name|IndexWriterConfig
name|iwc
decl_stmt|;
DECL|field|docs
name|LineFileDocs
name|docs
decl_stmt|;
DECL|field|pendingDocs
specifier|private
name|AtomicInteger
name|pendingDocs
decl_stmt|;
DECL|field|doRandomCommit
specifier|private
specifier|final
name|boolean
name|doRandomCommit
decl_stmt|;
DECL|method|IndexThread
specifier|public
name|IndexThread
parameter_list|(
name|AtomicInteger
name|pendingDocs
parameter_list|,
name|int
name|numThreads
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|LineFileDocs
name|docs
parameter_list|,
name|boolean
name|doRandomCommit
parameter_list|)
block|{
name|this
operator|.
name|pendingDocs
operator|=
name|pendingDocs
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|iwc
operator|=
name|writer
operator|.
name|getConfig
argument_list|()
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|doRandomCommit
operator|=
name|doRandomCommit
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|long
name|ramSize
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pendingDocs
operator|.
name|decrementAndGet
argument_list|()
operator|>
operator|-
literal|1
condition|)
block|{
name|Document
name|doc
init|=
name|docs
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|long
name|newRamSize
init|=
name|writer
operator|.
name|ramSizeInBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|newRamSize
operator|!=
name|ramSize
condition|)
block|{
name|ramSize
operator|=
name|newRamSize
expr_stmt|;
block|}
if|if
condition|(
name|doRandomCommit
condition|)
block|{
name|int
name|commit
decl_stmt|;
synchronized|synchronized
init|(
name|random
init|)
block|{
name|commit
operator|=
name|random
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commit
operator|==
literal|0
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|MockDefaultFlushPolicy
specifier|private
specifier|static
class|class
name|MockDefaultFlushPolicy
extends|extends
name|FlushByRamOrCountsPolicy
block|{
DECL|field|peakBytesWithoutFlush
name|long
name|peakBytesWithoutFlush
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|peakDocCountWithoutFlush
name|long
name|peakDocCountWithoutFlush
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|hasMarkedPending
name|boolean
name|hasMarkedPending
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|onDelete
specifier|public
name|void
name|onDelete
parameter_list|(
name|DocumentsWriterFlushControl
name|control
parameter_list|,
name|ThreadState
name|state
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|ThreadState
argument_list|>
name|pending
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ThreadState
argument_list|>
name|notPending
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
argument_list|>
argument_list|()
decl_stmt|;
name|findPending
argument_list|(
name|control
argument_list|,
name|pending
argument_list|,
name|notPending
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|flushCurrent
init|=
name|state
operator|.
name|flushPending
decl_stmt|;
specifier|final
name|ThreadState
name|toFlush
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|flushPending
condition|)
block|{
name|toFlush
operator|=
name|state
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|flushOnDeleteTerms
argument_list|()
operator|&&
name|state
operator|.
name|perThread
operator|.
name|pendingDeletes
operator|.
name|numTermDeletes
operator|.
name|get
argument_list|()
operator|>=
name|indexWriterConfig
operator|.
name|getMaxBufferedDeleteTerms
argument_list|()
condition|)
block|{
name|toFlush
operator|=
name|state
expr_stmt|;
block|}
else|else
block|{
name|toFlush
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|onDelete
argument_list|(
name|control
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|toFlush
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|flushCurrent
condition|)
block|{
name|assertTrue
argument_list|(
name|pending
operator|.
name|remove
argument_list|(
name|toFlush
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|notPending
operator|.
name|remove
argument_list|(
name|toFlush
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|toFlush
operator|.
name|flushPending
argument_list|)
expr_stmt|;
name|hasMarkedPending
operator|=
literal|true
expr_stmt|;
block|}
for|for
control|(
name|ThreadState
name|threadState
range|:
name|notPending
control|)
block|{
name|assertFalse
argument_list|(
name|threadState
operator|.
name|flushPending
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onInsert
specifier|public
name|void
name|onInsert
parameter_list|(
name|DocumentsWriterFlushControl
name|control
parameter_list|,
name|ThreadState
name|state
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|ThreadState
argument_list|>
name|pending
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|ThreadState
argument_list|>
name|notPending
init|=
operator|new
name|ArrayList
argument_list|<
name|DocumentsWriterPerThreadPool
operator|.
name|ThreadState
argument_list|>
argument_list|()
decl_stmt|;
name|findPending
argument_list|(
name|control
argument_list|,
name|pending
argument_list|,
name|notPending
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|flushCurrent
init|=
name|state
operator|.
name|flushPending
decl_stmt|;
name|long
name|activeBytes
init|=
name|control
operator|.
name|activeBytes
argument_list|()
decl_stmt|;
specifier|final
name|ThreadState
name|toFlush
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|flushPending
condition|)
block|{
name|toFlush
operator|=
name|state
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|flushOnDocCount
argument_list|()
operator|&&
name|state
operator|.
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
operator|>=
name|indexWriterConfig
operator|.
name|getMaxBufferedDocs
argument_list|()
condition|)
block|{
name|toFlush
operator|=
name|state
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|flushOnRAM
argument_list|()
operator|&&
name|activeBytes
operator|>=
call|(
name|long
call|)
argument_list|(
name|indexWriterConfig
operator|.
name|getRAMBufferSizeMB
argument_list|()
operator|*
literal|1024.
operator|*
literal|1024.
argument_list|)
condition|)
block|{
name|toFlush
operator|=
name|findLargestNonPendingWriter
argument_list|(
name|control
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|toFlush
operator|.
name|flushPending
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|toFlush
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|onInsert
argument_list|(
name|control
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|toFlush
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|flushCurrent
condition|)
block|{
name|assertTrue
argument_list|(
name|pending
operator|.
name|remove
argument_list|(
name|toFlush
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|notPending
operator|.
name|remove
argument_list|(
name|toFlush
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|toFlush
operator|.
name|flushPending
argument_list|)
expr_stmt|;
name|hasMarkedPending
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|peakBytesWithoutFlush
operator|=
name|Math
operator|.
name|max
argument_list|(
name|activeBytes
argument_list|,
name|peakBytesWithoutFlush
argument_list|)
expr_stmt|;
name|peakDocCountWithoutFlush
operator|=
name|Math
operator|.
name|max
argument_list|(
name|state
operator|.
name|perThread
operator|.
name|getNumDocsInRAM
argument_list|()
argument_list|,
name|peakDocCountWithoutFlush
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ThreadState
name|threadState
range|:
name|notPending
control|)
block|{
name|assertFalse
argument_list|(
name|threadState
operator|.
name|flushPending
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|findPending
specifier|static
name|void
name|findPending
parameter_list|(
name|DocumentsWriterFlushControl
name|flushControl
parameter_list|,
name|ArrayList
argument_list|<
name|ThreadState
argument_list|>
name|pending
parameter_list|,
name|ArrayList
argument_list|<
name|ThreadState
argument_list|>
name|notPending
parameter_list|)
block|{
name|Iterator
argument_list|<
name|ThreadState
argument_list|>
name|allActiveThreads
init|=
name|flushControl
operator|.
name|allActiveThreads
argument_list|()
decl_stmt|;
while|while
condition|(
name|allActiveThreads
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ThreadState
name|next
init|=
name|allActiveThreads
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|flushPending
condition|)
block|{
name|pending
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|notPending
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

