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
name|document
operator|.
name|Field
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
name|FieldType
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
name|TextField
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Some tests for {@link ParallelAtomicReader}s with empty indexes  */
end_comment

begin_class
DECL|class|TestParallelReaderEmptyIndex
specifier|public
class|class
name|TestParallelReaderEmptyIndex
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Creates two empty indexes and wraps a ParallelReader around. Adding this    * reader to a new index should not throw any exception.    *     * @throws IOException    */
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|rd1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd1
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// create a copy:
name|Directory
name|rd2
init|=
name|newDirectory
argument_list|(
name|rd1
argument_list|)
decl_stmt|;
name|Directory
name|rdOut
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iwOut
init|=
operator|new
name|IndexWriter
argument_list|(
name|rdOut
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|ParallelAtomicReader
name|apr
init|=
operator|new
name|ParallelAtomicReader
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd1
argument_list|)
argument_list|)
argument_list|,
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// When unpatched, Lucene crashes here with a NoSuchElementException (caused by ParallelTermEnum)
name|iwOut
operator|.
name|addIndexes
argument_list|(
name|apr
argument_list|)
expr_stmt|;
name|iwOut
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 2nd try with a readerless parallel reader
name|iwOut
operator|.
name|addIndexes
argument_list|(
operator|new
name|ParallelAtomicReader
argument_list|()
argument_list|)
expr_stmt|;
name|iwOut
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ParallelCompositeReader
name|cpr
init|=
operator|new
name|ParallelCompositeReader
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd1
argument_list|)
argument_list|,
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd2
argument_list|)
argument_list|)
decl_stmt|;
comment|// When unpatched, Lucene crashes here with a NoSuchElementException (caused by ParallelTermEnum)
name|iwOut
operator|.
name|addIndexes
argument_list|(
name|cpr
argument_list|)
expr_stmt|;
name|iwOut
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// 2nd try with a readerless parallel reader
name|iwOut
operator|.
name|addIndexes
argument_list|(
operator|new
name|ParallelCompositeReader
argument_list|()
argument_list|)
expr_stmt|;
name|iwOut
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iwOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|rdOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd1
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * This method creates an empty index (numFields=0, numDocs=0) but is marked    * to have TermVectors. Adding this index to another index should not throw    * any exception.    */
DECL|method|testEmptyIndexWithVectors
specifier|public
name|void
name|testEmptyIndexWithVectors
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|rd1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: make 1st writer"
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd1
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|idField
init|=
name|newField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|customType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"test"
argument_list|,
literal|""
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|idField
operator|.
name|setStringValue
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"test"
argument_list|,
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
argument_list|)
expr_stmt|;
name|idField
operator|.
name|setStringValue
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexWriterConfig
name|dontMergeConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: make 2nd writer"
argument_list|)
expr_stmt|;
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd1
argument_list|,
name|dontMergeConfig
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|ir
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ir
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ir
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|=
operator|new
name|IndexWriter
argument_list|(
name|rd1
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Directory
name|rd2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd2
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Directory
name|rdOut
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|iwOut
init|=
operator|new
name|IndexWriter
argument_list|(
name|rdOut
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DirectoryReader
name|reader1
decl_stmt|,
name|reader2
decl_stmt|;
name|ParallelAtomicReader
name|pr
init|=
operator|new
name|ParallelAtomicReader
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader1
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd1
argument_list|)
argument_list|)
argument_list|,
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader2
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|rd2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// When unpatched, Lucene crashes here with an ArrayIndexOutOfBoundsException (caused by TermVectorsWriter)
name|iwOut
operator|.
name|addIndexes
argument_list|(
name|pr
argument_list|)
expr_stmt|;
comment|// ParallelReader closes any IndexReader you added to it:
name|pr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// assert subreaders were closed
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|rd1
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd2
operator|.
name|close
argument_list|()
expr_stmt|;
name|iwOut
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iwOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|rdOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

