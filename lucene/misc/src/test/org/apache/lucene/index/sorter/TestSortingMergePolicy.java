begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|StringField
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
name|AtomicReader
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
name|NumericDocValues
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
name|RandomIndexWriter
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
name|SlowCompositeReaderWrapper
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

begin_class
DECL|class|TestSortingMergePolicy
specifier|public
class|class
name|TestSortingMergePolicy
extends|extends
name|LuceneTestCase
block|{
DECL|field|DELETE_TERM
specifier|private
specifier|static
specifier|final
name|String
name|DELETE_TERM
init|=
literal|"abc"
decl_stmt|;
DECL|field|dir1
DECL|field|dir2
specifier|private
name|Directory
name|dir1
decl_stmt|,
name|dir2
decl_stmt|;
DECL|field|sorter
specifier|private
name|Sorter
name|sorter
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|sortedReader
specifier|private
name|IndexReader
name|sortedReader
decl_stmt|;
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
name|sorter
operator|=
operator|new
name|NumericDocValuesSorter
argument_list|(
literal|"ndv"
argument_list|)
expr_stmt|;
name|createRandomIndexes
argument_list|()
expr_stmt|;
block|}
DECL|method|randomDocument
specifier|private
name|Document
name|randomDocument
parameter_list|()
block|{
specifier|final
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
name|NumericDocValuesField
argument_list|(
literal|"ndv"
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"s"
argument_list|,
name|rarely
argument_list|()
condition|?
name|DELETE_TERM
else|:
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|)
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|createRandomIndexes
specifier|private
name|void
name|createRandomIndexes
parameter_list|()
throws|throws
name|IOException
block|{
name|dir1
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|dir2
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc1
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc2
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|iwc2
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|SortingMergePolicy
argument_list|(
name|iwc2
operator|.
name|getMergePolicy
argument_list|()
argument_list|,
name|sorter
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|iw1
init|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|dir1
argument_list|,
name|iwc1
argument_list|)
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|iw2
init|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|dir2
argument_list|,
name|iwc2
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
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|randomDocument
argument_list|()
decl_stmt|;
name|iw1
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|numDocs
operator|/
literal|2
operator|||
name|rarely
argument_list|()
condition|)
block|{
name|iw1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|iw2
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|iw1
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|DELETE_TERM
argument_list|)
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|DELETE_TERM
argument_list|)
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw2
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir1
argument_list|)
expr_stmt|;
name|sortedReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir2
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|sortedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSorted
specifier|private
name|void
name|assertSorted
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NumericDocValues
name|ndv
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
literal|"ndv"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|reader
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|assertTrue
argument_list|(
name|ndv
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|<
name|ndv
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortingMP
specifier|public
name|void
name|testSortingMP
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|AtomicReader
name|sortedReader1
init|=
name|SortingAtomicReader
operator|.
name|wrap
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
argument_list|,
name|sorter
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReader
name|sortedReader2
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|sortedReader
argument_list|)
decl_stmt|;
name|assertSorted
argument_list|(
name|sortedReader1
argument_list|)
expr_stmt|;
name|assertSorted
argument_list|(
name|sortedReader2
argument_list|)
expr_stmt|;
name|assertReaderEquals
argument_list|(
literal|""
argument_list|,
name|sortedReader1
argument_list|,
name|sortedReader2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

