begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
package|;
end_package

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
name|analysis
operator|.
name|MockTokenizer
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
name|IntsRef
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
name|encoding
operator|.
name|DGapIntEncoder
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
name|encoding
operator|.
name|IntEncoder
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
name|encoding
operator|.
name|SortingIntEncoder
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
name|encoding
operator|.
name|UniqueValuesIntEncoder
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
name|encoding
operator|.
name|VInt8IntEncoder
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|CategoryListIteratorTest
specifier|public
class|class
name|CategoryListIteratorTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|data
specifier|static
specifier|final
name|IntsRef
index|[]
name|data
init|=
operator|new
name|IntsRef
index|[]
block|{
operator|new
name|IntsRef
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
block|,
operator|new
name|IntsRef
argument_list|(
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|4
block|}
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
block|,
operator|new
name|IntsRef
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
block|,
operator|new
name|IntsRef
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|}
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testPayloadCategoryListIteraor
specifier|public
name|void
name|testPayloadCategoryListIteraor
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
specifier|final
name|IntEncoder
name|encoder
init|=
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
argument_list|,
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|buf
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
name|data
operator|.
name|length
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
name|encoder
operator|.
name|encode
argument_list|(
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|buf
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|totalCategories
init|=
literal|0
decl_stmt|;
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|CategoryListIterator
name|cli
init|=
operator|new
name|DocValuesCategoryListIterator
argument_list|(
literal|"f"
argument_list|,
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"failed to initalize iterator"
argument_list|,
name|cli
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|dataIdx
init|=
name|context
operator|.
name|docBase
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
operator|,
name|dataIdx
operator|++
control|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|values
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
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
name|data
index|[
name|dataIdx
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|data
index|[
name|dataIdx
index|]
operator|.
name|ints
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|cli
operator|.
name|getOrdinals
argument_list|(
name|doc
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no ordinals for document "
operator|+
name|doc
argument_list|,
name|ordinals
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ordinals
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"expected category not found: "
operator|+
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
argument_list|,
name|values
operator|.
name|contains
argument_list|(
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|totalCategories
operator|+=
name|ordinals
operator|.
name|length
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Missing categories!"
argument_list|,
literal|10
argument_list|,
name|totalCategories
argument_list|)
expr_stmt|;
name|reader
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
DECL|method|testPayloadIteratorWithInvalidDoc
specifier|public
name|void
name|testPayloadIteratorWithInvalidDoc
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
specifier|final
name|IntEncoder
name|encoder
init|=
operator|new
name|SortingIntEncoder
argument_list|(
operator|new
name|UniqueValuesIntEncoder
argument_list|(
operator|new
name|DGapIntEncoder
argument_list|(
operator|new
name|VInt8IntEncoder
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// NOTE: test is wired to LogMP... because test relies on certain docids having payloads
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
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
name|data
operator|.
name|length
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
name|i
operator|==
literal|0
condition|)
block|{
name|BytesRef
name|buf
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|IntsRef
operator|.
name|deepCopyOf
argument_list|(
name|data
index|[
name|i
index|]
argument_list|)
argument_list|,
name|buf
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
argument_list|,
name|buf
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"f"
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|int
name|totalCategories
init|=
literal|0
decl_stmt|;
name|IntsRef
name|ordinals
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|CategoryListIterator
name|cli
init|=
operator|new
name|DocValuesCategoryListIterator
argument_list|(
literal|"f"
argument_list|,
name|encoder
operator|.
name|createMatchingDecoder
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|AtomicReaderContext
name|context
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
literal|"failed to initalize iterator"
argument_list|,
name|cli
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|maxDoc
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|dataIdx
init|=
name|context
operator|.
name|docBase
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
literal|0
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|++
operator|,
name|dataIdx
operator|++
control|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|values
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
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
name|data
index|[
name|dataIdx
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|data
index|[
name|dataIdx
index|]
operator|.
name|ints
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|cli
operator|.
name|getOrdinals
argument_list|(
name|doc
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataIdx
operator|==
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"document 0 must have ordinals"
argument_list|,
name|ordinals
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ordinals
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"expected category not found: "
operator|+
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
argument_list|,
name|values
operator|.
name|contains
argument_list|(
name|ordinals
operator|.
name|ints
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|totalCategories
operator|+=
name|ordinals
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"only document 0 should have ordinals"
argument_list|,
name|ordinals
operator|.
name|length
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
literal|"Wrong number of total categories!"
argument_list|,
literal|2
argument_list|,
name|totalCategories
argument_list|)
expr_stmt|;
name|reader
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

