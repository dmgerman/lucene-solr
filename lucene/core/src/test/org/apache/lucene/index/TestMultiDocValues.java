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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|BinaryDocValuesField
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
name|SortedDocValuesField
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
name|SortedSetDocValuesField
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
name|Bits
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
name|BytesRef
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
comment|/** Tests MultiDocValues versus ordinary segment merging */
end_comment

begin_class
DECL|class|TestMultiDocValues
specifier|public
class|class
name|TestMultiDocValues
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNumerics
specifier|public
name|void
name|testNumerics
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"numbers"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|i
operator|++
control|)
block|{
name|field
operator|.
name|setLongValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|NumericDocValues
name|multi
init|=
name|MultiDocValues
operator|.
name|getNumericValues
argument_list|(
name|ir
argument_list|,
literal|"numbers"
argument_list|)
decl_stmt|;
name|NumericDocValues
name|single
init|=
name|merged
operator|.
name|getNumericDocValues
argument_list|(
literal|"numbers"
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
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|single
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|multi
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
DECL|method|testBinary
specifier|public
name|void
name|testBinary
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"bytes"
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|i
operator|++
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|BinaryDocValues
name|multi
init|=
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|ir
argument_list|,
literal|"bytes"
argument_list|)
decl_stmt|;
name|BinaryDocValues
name|single
init|=
name|merged
operator|.
name|getBinaryDocValues
argument_list|(
literal|"bytes"
argument_list|)
decl_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|()
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
name|i
operator|++
control|)
block|{
name|single
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|multi
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
DECL|method|testSorted
specifier|public
name|void
name|testSorted
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"bytes"
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|i
operator|++
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|SortedDocValues
name|multi
init|=
name|MultiDocValues
operator|.
name|getSortedValues
argument_list|(
name|ir
argument_list|,
literal|"bytes"
argument_list|)
decl_stmt|;
name|SortedDocValues
name|single
init|=
name|merged
operator|.
name|getSortedDocValues
argument_list|(
literal|"bytes"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|single
operator|.
name|getValueCount
argument_list|()
argument_list|,
name|multi
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|()
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
name|i
operator|++
control|)
block|{
comment|// check ord
name|assertEquals
argument_list|(
name|single
operator|.
name|getOrd
argument_list|(
name|i
argument_list|)
argument_list|,
name|multi
operator|.
name|getOrd
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// check ord value
name|single
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|multi
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
comment|// tries to make more dups than testSorted
DECL|method|testSortedWithLotsOfDups
specifier|public
name|void
name|testSortedWithLotsOfDups
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
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|Field
name|field
init|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"bytes"
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|i
operator|++
control|)
block|{
name|ref
operator|.
name|copyChars
argument_list|(
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|SortedDocValues
name|multi
init|=
name|MultiDocValues
operator|.
name|getSortedValues
argument_list|(
name|ir
argument_list|,
literal|"bytes"
argument_list|)
decl_stmt|;
name|SortedDocValues
name|single
init|=
name|merged
operator|.
name|getSortedDocValues
argument_list|(
literal|"bytes"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|single
operator|.
name|getValueCount
argument_list|()
argument_list|,
name|multi
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|()
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
name|i
operator|++
control|)
block|{
comment|// check ord
name|assertEquals
argument_list|(
name|single
operator|.
name|getOrd
argument_list|(
name|i
argument_list|)
argument_list|,
name|multi
operator|.
name|getOrd
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// check ord value
name|single
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|multi
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
DECL|method|testSortedSet
specifier|public
name|void
name|testSortedSet
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"codec does not support SORTED_SET"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
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
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numValues
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"bytes"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|SortedSetDocValues
name|multi
init|=
name|MultiDocValues
operator|.
name|getSortedSetValues
argument_list|(
name|ir
argument_list|,
literal|"bytes"
argument_list|)
decl_stmt|;
name|SortedSetDocValues
name|single
init|=
name|merged
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"bytes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|multi
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|single
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|single
operator|.
name|getValueCount
argument_list|()
argument_list|,
name|multi
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|// check values
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|single
operator|.
name|getValueCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|single
operator|.
name|lookupOrd
argument_list|(
name|i
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|multi
operator|.
name|lookupOrd
argument_list|(
name|i
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
comment|// check ord list
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
name|single
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|expectedList
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|single
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|expectedList
operator|.
name|add
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
name|multi
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|multi
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedList
operator|.
name|get
argument_list|(
name|upto
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedList
operator|.
name|size
argument_list|()
argument_list|,
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
comment|// tries to make more dups than testSortedSet
DECL|method|testSortedSetWithDups
specifier|public
name|void
name|testSortedSetWithDups
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"codec does not support SORTED_SET"
argument_list|,
name|defaultCodecSupportsSortedSet
argument_list|()
argument_list|)
expr_stmt|;
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
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
name|int
name|numValues
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numValues
condition|;
name|j
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesField
argument_list|(
literal|"bytes"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|SortedSetDocValues
name|multi
init|=
name|MultiDocValues
operator|.
name|getSortedSetValues
argument_list|(
name|ir
argument_list|,
literal|"bytes"
argument_list|)
decl_stmt|;
name|SortedSetDocValues
name|single
init|=
name|merged
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"bytes"
argument_list|)
decl_stmt|;
if|if
condition|(
name|multi
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|single
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|single
operator|.
name|getValueCount
argument_list|()
argument_list|,
name|multi
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|BytesRef
name|actual
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|// check values
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|single
operator|.
name|getValueCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|single
operator|.
name|lookupOrd
argument_list|(
name|i
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|multi
operator|.
name|lookupOrd
argument_list|(
name|i
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
comment|// check ord list
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
name|single
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|expectedList
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|ord
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|single
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|expectedList
operator|.
name|add
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
name|multi
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ord
operator|=
name|multi
operator|.
name|nextOrd
argument_list|()
operator|)
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedList
operator|.
name|get
argument_list|(
name|upto
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedList
operator|.
name|size
argument_list|()
argument_list|,
name|upto
argument_list|)
expr_stmt|;
block|}
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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
DECL|method|testDocsWithField
specifier|public
name|void
name|testDocsWithField
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"codec does not support docsWithField"
argument_list|,
name|defaultCodecSupportsDocsWithField
argument_list|()
argument_list|)
expr_stmt|;
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
name|random
argument_list|()
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"numbers"
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"numbersAlways"
argument_list|,
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|17
argument_list|)
operator|==
literal|0
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|DirectoryReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DirectoryReader
name|ir2
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|AtomicReader
name|merged
init|=
name|getOnlySegmentReader
argument_list|(
name|ir2
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Bits
name|multi
init|=
name|MultiDocValues
operator|.
name|getDocsWithField
argument_list|(
name|ir
argument_list|,
literal|"numbers"
argument_list|)
decl_stmt|;
name|Bits
name|single
init|=
name|merged
operator|.
name|getDocsWithField
argument_list|(
literal|"numbers"
argument_list|)
decl_stmt|;
if|if
condition|(
name|multi
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|single
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|single
operator|.
name|length
argument_list|()
argument_list|,
name|multi
operator|.
name|length
argument_list|()
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|single
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|multi
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|multi
operator|=
name|MultiDocValues
operator|.
name|getDocsWithField
argument_list|(
name|ir
argument_list|,
literal|"numbersAlways"
argument_list|)
expr_stmt|;
name|single
operator|=
name|merged
operator|.
name|getDocsWithField
argument_list|(
literal|"numbersAlways"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|single
operator|.
name|length
argument_list|()
argument_list|,
name|multi
operator|.
name|length
argument_list|()
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|single
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|multi
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir2
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

