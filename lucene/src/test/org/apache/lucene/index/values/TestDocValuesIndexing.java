begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|Closeable
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Fieldable
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
name|ValuesField
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
name|Index
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
name|index
operator|.
name|CorruptIndexException
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
name|Fields
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
name|FieldsEnum
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
name|LogDocMergePolicy
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
name|LogMergePolicy
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
name|MergePolicy
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
name|MultiFields
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
name|index
operator|.
name|codecs
operator|.
name|CodecProvider
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
name|codecs
operator|.
name|docvalues
operator|.
name|DocValuesCodec
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
name|values
operator|.
name|DocValues
operator|.
name|Source
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
name|FloatsRef
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
name|LongsRef
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
name|OpenBitSet
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

begin_comment
comment|/**  *   * Tests DocValues integration into IndexWriter& Codecs  *   */
end_comment

begin_class
DECL|class|TestDocValuesIndexing
specifier|public
class|class
name|TestDocValuesIndexing
extends|extends
name|LuceneTestCase
block|{
comment|// TODO Add a test for addIndexes
comment|// TODO add test for unoptimized case with deletes
DECL|field|docValuesCodec
specifier|private
specifier|static
name|DocValuesCodec
name|docValuesCodec
decl_stmt|;
DECL|field|provider
specifier|private
specifier|static
name|CodecProvider
name|provider
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClassLuceneTestCaseJ4
specifier|public
specifier|static
name|void
name|beforeClassLuceneTestCaseJ4
parameter_list|()
block|{
name|LuceneTestCase
operator|.
name|beforeClassLuceneTestCaseJ4
argument_list|()
expr_stmt|;
name|provider
operator|=
operator|new
name|CodecProvider
argument_list|()
expr_stmt|;
name|docValuesCodec
operator|=
operator|new
name|DocValuesCodec
argument_list|(
name|CodecProvider
operator|.
name|getDefault
argument_list|()
operator|.
name|lookup
argument_list|(
name|CodecProvider
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|provider
operator|.
name|register
argument_list|(
name|docValuesCodec
argument_list|)
expr_stmt|;
name|provider
operator|.
name|setDefaultFieldCodec
argument_list|(
name|docValuesCodec
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClassLuceneTestCaseJ4
specifier|public
specifier|static
name|void
name|afterClassLuceneTestCaseJ4
parameter_list|()
block|{
name|LuceneTestCase
operator|.
name|afterClassLuceneTestCaseJ4
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests complete indexing of {@link Values} including deletions, merging and    * sparse value fields on Compound-File    */
DECL|method|testCFSIndex
specifier|public
name|void
name|testCFSIndex
parameter_list|()
throws|throws
name|IOException
block|{
comment|// without deletions
name|IndexWriterConfig
name|cfg
init|=
name|writerConfig
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// primitives - no deletes
name|runTestNumerics
argument_list|(
name|cfg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cfg
operator|=
name|writerConfig
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// bytes - no deletes
name|runTestIndexBytes
argument_list|(
name|cfg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// with deletions
name|cfg
operator|=
name|writerConfig
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// primitives
name|runTestNumerics
argument_list|(
name|cfg
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|=
name|writerConfig
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// bytes
name|runTestIndexBytes
argument_list|(
name|cfg
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests complete indexing of {@link Values} including deletions, merging and    * sparse value fields on None-Compound-File    */
DECL|method|testIndex
specifier|public
name|void
name|testIndex
parameter_list|()
throws|throws
name|IOException
block|{
comment|//
comment|// without deletions
name|IndexWriterConfig
name|cfg
init|=
name|writerConfig
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// primitives - no deletes
name|runTestNumerics
argument_list|(
name|cfg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cfg
operator|=
name|writerConfig
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// bytes - no deletes
name|runTestIndexBytes
argument_list|(
name|cfg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// with deletions
name|cfg
operator|=
name|writerConfig
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// primitives
name|runTestNumerics
argument_list|(
name|cfg
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cfg
operator|=
name|writerConfig
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// bytes
name|runTestIndexBytes
argument_list|(
name|cfg
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|writerConfig
specifier|private
name|IndexWriterConfig
name|writerConfig
parameter_list|(
name|boolean
name|useCompoundFile
parameter_list|)
block|{
specifier|final
name|IndexWriterConfig
name|cfg
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|MergePolicy
name|mergePolicy
init|=
name|cfg
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergePolicy
operator|instanceof
name|LogMergePolicy
condition|)
block|{
operator|(
operator|(
name|LogMergePolicy
operator|)
name|mergePolicy
operator|)
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|useCompoundFile
condition|)
block|{
name|LogMergePolicy
name|policy
init|=
operator|new
name|LogDocMergePolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setUseCompoundFile
argument_list|(
name|useCompoundFile
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setMergePolicy
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
name|cfg
operator|.
name|setCodecProvider
argument_list|(
name|provider
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|runTestNumerics
specifier|public
name|void
name|runTestNumerics
parameter_list|(
name|IndexWriterConfig
name|cfg
parameter_list|,
name|boolean
name|withDeletions
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numValues
init|=
literal|350
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Values
argument_list|>
name|numVariantList
init|=
operator|new
name|ArrayList
argument_list|<
name|Values
argument_list|>
argument_list|(
name|NUMERICS
argument_list|)
decl_stmt|;
comment|// run in random order to test if fill works correctly during merges
name|Collections
operator|.
name|shuffle
argument_list|(
name|numVariantList
argument_list|,
name|random
argument_list|)
expr_stmt|;
for|for
control|(
name|Values
name|val
range|:
name|numVariantList
control|)
block|{
name|OpenBitSet
name|deleted
init|=
name|indexValues
argument_list|(
name|w
argument_list|,
name|numValues
argument_list|,
name|val
argument_list|,
name|numVariantList
argument_list|,
name|withDeletions
argument_list|,
literal|7
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Closeable
argument_list|>
name|closeables
init|=
operator|new
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
argument_list|()
decl_stmt|;
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numRemainingValues
init|=
call|(
name|int
call|)
argument_list|(
name|numValues
operator|-
name|deleted
operator|.
name|cardinality
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|base
init|=
name|r
operator|.
name|numDocs
argument_list|()
operator|-
name|numRemainingValues
decl_stmt|;
switch|switch
condition|(
name|val
condition|)
block|{
case|case
name|PACKED_INTS
case|:
case|case
name|PACKED_INTS_FIXED
case|:
block|{
name|DocValues
name|intsReader
init|=
name|getDocValues
argument_list|(
name|r
argument_list|,
name|val
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|intsReader
argument_list|)
expr_stmt|;
name|Source
name|ints
init|=
name|getSource
argument_list|(
name|intsReader
argument_list|)
decl_stmt|;
name|ValuesEnum
name|intsEnum
init|=
name|intsReader
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|intsEnum
argument_list|)
expr_stmt|;
name|LongsRef
name|enumRef
init|=
name|intsEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
operator|.
name|ints
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
name|base
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"index "
operator|+
name|i
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|val
operator|.
name|name
argument_list|()
operator|+
literal|" base: "
operator|+
name|base
operator|+
literal|" index: "
operator|+
name|i
argument_list|,
name|i
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|intsEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
else|:
name|intsEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|enumRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|expected
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|base
init|;
name|i
operator|<
name|r
operator|.
name|numDocs
argument_list|()
condition|;
name|i
operator|++
operator|,
name|expected
operator|++
control|)
block|{
while|while
condition|(
name|deleted
operator|.
name|get
argument_list|(
name|expected
argument_list|)
condition|)
block|{
name|expected
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"advance failed at index: "
operator|+
name|i
operator|+
literal|" of "
operator|+
name|r
operator|.
name|numDocs
argument_list|()
operator|+
literal|" docs"
argument_list|,
name|i
argument_list|,
name|intsEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|enumRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|ints
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
block|{
name|DocValues
name|floatReader
init|=
name|getDocValues
argument_list|(
name|r
argument_list|,
name|val
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|floatReader
argument_list|)
expr_stmt|;
name|Source
name|floats
init|=
name|getSource
argument_list|(
name|floatReader
argument_list|)
decl_stmt|;
name|ValuesEnum
name|floatEnum
init|=
name|floatReader
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|floatEnum
argument_list|)
expr_stmt|;
name|FloatsRef
name|enumRef
init|=
name|floatEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
operator|.
name|floats
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
name|base
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|" floats failed for doc: "
operator|+
name|i
operator|+
literal|" base: "
operator|+
name|base
argument_list|,
literal|0.0d
argument_list|,
name|floats
operator|.
name|getFloat
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0d
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|floatEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
else|:
name|floatEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"index "
operator|+
name|i
argument_list|,
literal|0.0
argument_list|,
name|enumRef
operator|.
name|get
argument_list|()
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
name|int
name|expected
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|base
init|;
name|i
operator|<
name|r
operator|.
name|numDocs
argument_list|()
condition|;
name|i
operator|++
operator|,
name|expected
operator|++
control|)
block|{
while|while
condition|(
name|deleted
operator|.
name|get
argument_list|(
name|expected
argument_list|)
condition|)
block|{
name|expected
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"advance failed at index: "
operator|+
name|i
operator|+
literal|" of "
operator|+
name|r
operator|.
name|numDocs
argument_list|()
operator|+
literal|" docs base:"
operator|+
name|base
argument_list|,
name|i
argument_list|,
name|floatEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"index "
operator|+
name|i
argument_list|,
literal|2.0
operator|*
name|expected
argument_list|,
name|enumRef
operator|.
name|get
argument_list|()
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"index "
operator|+
name|i
argument_list|,
literal|2.0
operator|*
name|expected
argument_list|,
name|floats
operator|.
name|getFloat
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.00001
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
name|fail
argument_list|(
literal|"unexpected value "
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
name|closeables
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
for|for
control|(
name|Closeable
name|toClose
range|:
name|closeables
control|)
block|{
name|toClose
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|BYTES
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Values
argument_list|>
name|BYTES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Values
operator|.
name|BYTES_FIXED_DEREF
argument_list|,
name|Values
operator|.
name|BYTES_FIXED_SORTED
argument_list|,
name|Values
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|,
name|Values
operator|.
name|BYTES_VAR_DEREF
argument_list|,
name|Values
operator|.
name|BYTES_VAR_SORTED
argument_list|,
name|Values
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|)
decl_stmt|;
DECL|field|NUMERICS
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Values
argument_list|>
name|NUMERICS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|Values
operator|.
name|PACKED_INTS
argument_list|,
name|Values
operator|.
name|PACKED_INTS_FIXED
argument_list|,
name|Values
operator|.
name|SIMPLE_FLOAT_4BYTE
argument_list|,
name|Values
operator|.
name|SIMPLE_FLOAT_8BYTE
argument_list|)
decl_stmt|;
DECL|field|IDX_VALUES
specifier|private
specifier|static
name|Index
index|[]
name|IDX_VALUES
init|=
operator|new
name|Index
index|[]
block|{
name|Index
operator|.
name|ANALYZED
block|,
name|Index
operator|.
name|ANALYZED_NO_NORMS
block|,
name|Index
operator|.
name|NOT_ANALYZED
block|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
block|,
name|Index
operator|.
name|NO
block|}
decl_stmt|;
DECL|method|indexValues
specifier|private
name|OpenBitSet
name|indexValues
parameter_list|(
name|IndexWriter
name|w
parameter_list|,
name|int
name|numValues
parameter_list|,
name|Values
name|value
parameter_list|,
name|List
argument_list|<
name|Values
argument_list|>
name|valueVarList
parameter_list|,
name|boolean
name|withDeletions
parameter_list|,
name|int
name|multOfSeven
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|boolean
name|isNumeric
init|=
name|NUMERICS
operator|.
name|contains
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|OpenBitSet
name|deleted
init|=
operator|new
name|OpenBitSet
argument_list|(
name|numValues
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Index
name|idx
init|=
name|IDX_VALUES
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|IDX_VALUES
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|Fieldable
name|field
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|ValuesField
argument_list|(
name|value
operator|.
name|name
argument_list|()
argument_list|)
else|:
name|newField
argument_list|(
name|value
operator|.
name|name
argument_list|()
argument_list|,
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|,
literal|10
argument_list|)
argument_list|,
name|idx
operator|==
name|Index
operator|.
name|NO
condition|?
name|Store
operator|.
name|YES
else|:
name|Store
operator|.
name|NO
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|ValuesAttribute
name|valuesAttribute
init|=
name|ValuesField
operator|.
name|values
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|valuesAttribute
operator|.
name|setType
argument_list|(
name|value
argument_list|)
expr_stmt|;
specifier|final
name|LongsRef
name|intsRef
init|=
name|valuesAttribute
operator|.
name|ints
argument_list|()
decl_stmt|;
specifier|final
name|FloatsRef
name|floatsRef
init|=
name|valuesAttribute
operator|.
name|floats
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|bytesRef
init|=
name|valuesAttribute
operator|.
name|bytes
argument_list|()
decl_stmt|;
specifier|final
name|String
name|idBase
init|=
name|value
operator|.
name|name
argument_list|()
operator|+
literal|"_"
decl_stmt|;
specifier|final
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|multOfSeven
index|]
decl_stmt|;
if|if
condition|(
name|bytesRef
operator|!=
literal|null
condition|)
block|{
name|bytesRef
operator|.
name|bytes
operator|=
name|b
expr_stmt|;
name|bytesRef
operator|.
name|length
operator|=
name|b
operator|.
name|length
expr_stmt|;
name|bytesRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
block|}
name|byte
name|upto
init|=
literal|0
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
name|numValues
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isNumeric
condition|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
name|PACKED_INTS
case|:
case|case
name|PACKED_INTS_FIXED
case|:
name|intsRef
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIMPLE_FLOAT_4BYTE
case|:
case|case
name|SIMPLE_FLOAT_8BYTE
case|:
name|floatsRef
operator|.
name|set
argument_list|(
literal|2.0f
operator|*
name|i
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"unexpected value "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|b
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|b
index|[
name|j
index|]
operator|=
name|upto
operator|++
expr_stmt|;
block|}
block|}
name|doc
operator|.
name|removeFields
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|idBase
operator|+
name|i
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|7
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|withDeletions
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|Values
name|val
init|=
name|valueVarList
operator|.
name|get
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
literal|1
operator|+
name|valueVarList
operator|.
name|indexOf
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|randInt
init|=
name|val
operator|==
name|value
condition|?
name|random
operator|.
name|nextInt
argument_list|(
literal|1
operator|+
name|i
argument_list|)
else|:
name|random
operator|.
name|nextInt
argument_list|(
name|numValues
argument_list|)
decl_stmt|;
name|w
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
name|val
operator|.
name|name
argument_list|()
operator|+
literal|"_"
operator|+
name|randInt
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|==
name|value
condition|)
block|{
name|deleted
operator|.
name|set
argument_list|(
name|randInt
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// TODO test unoptimized with deletions
if|if
condition|(
name|withDeletions
operator|||
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
name|w
operator|.
name|optimize
argument_list|()
expr_stmt|;
return|return
name|deleted
return|;
block|}
DECL|method|runTestIndexBytes
specifier|public
name|void
name|runTestIndexBytes
parameter_list|(
name|IndexWriterConfig
name|cfg
parameter_list|,
name|boolean
name|withDeletions
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
specifier|final
name|Directory
name|d
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Values
argument_list|>
name|byteVariantList
init|=
operator|new
name|ArrayList
argument_list|<
name|Values
argument_list|>
argument_list|(
name|BYTES
argument_list|)
decl_stmt|;
comment|// run in random order to test if fill works correctly during merges
name|Collections
operator|.
name|shuffle
argument_list|(
name|byteVariantList
argument_list|,
name|random
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numValues
init|=
literal|179
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|151
argument_list|)
decl_stmt|;
for|for
control|(
name|Values
name|byteIndexValue
range|:
name|byteVariantList
control|)
block|{
name|List
argument_list|<
name|Closeable
argument_list|>
name|closeables
init|=
operator|new
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|bytesSize
init|=
literal|7
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|OpenBitSet
name|deleted
init|=
name|indexValues
argument_list|(
name|w
argument_list|,
name|numValues
argument_list|,
name|byteIndexValue
argument_list|,
name|byteVariantList
argument_list|,
name|withDeletions
argument_list|,
name|bytesSize
argument_list|)
decl_stmt|;
specifier|final
name|IndexReader
name|r
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|w
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|r
operator|.
name|numDeletedDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numRemainingValues
init|=
call|(
name|int
call|)
argument_list|(
name|numValues
operator|-
name|deleted
operator|.
name|cardinality
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|base
init|=
name|r
operator|.
name|numDocs
argument_list|()
operator|-
name|numRemainingValues
decl_stmt|;
name|DocValues
name|bytesReader
init|=
name|getDocValues
argument_list|(
name|r
argument_list|,
name|byteIndexValue
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"field "
operator|+
name|byteIndexValue
operator|.
name|name
argument_list|()
operator|+
literal|" returned null reader - maybe merged failed"
argument_list|,
name|bytesReader
argument_list|)
expr_stmt|;
name|Source
name|bytes
init|=
name|getSource
argument_list|(
name|bytesReader
argument_list|)
decl_stmt|;
name|ValuesEnum
name|bytesEnum
init|=
name|bytesReader
operator|.
name|getEnum
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|bytesEnum
argument_list|)
expr_stmt|;
specifier|final
name|ValuesAttribute
name|attr
init|=
name|bytesEnum
operator|.
name|addAttribute
argument_list|(
name|ValuesAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|byte
name|upto
init|=
literal|0
decl_stmt|;
comment|// test the filled up slots for correctness
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|base
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|br
init|=
name|bytes
operator|.
name|getBytes
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|" field: "
operator|+
name|byteIndexValue
operator|.
name|name
argument_list|()
operator|+
literal|" at index: "
operator|+
name|i
operator|+
literal|" base: "
operator|+
name|base
operator|+
literal|" numDocs:"
operator|+
name|r
operator|.
name|numDocs
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|byteIndexValue
condition|)
block|{
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|assertEquals
argument_list|(
name|i
argument_list|,
name|bytesEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// fixed straight returns bytesref with zero bytes all of fixed
comment|// length
name|assertNotNull
argument_list|(
literal|"expected none null - "
operator|+
name|msg
argument_list|,
name|br
argument_list|)
expr_stmt|;
if|if
condition|(
name|br
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
literal|"expected zero bytes of length "
operator|+
name|bytesSize
operator|+
literal|" - "
operator|+
name|msg
argument_list|,
name|bytesSize
argument_list|,
name|br
operator|.
name|length
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
name|br
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Byte at index "
operator|+
name|j
operator|+
literal|" doesn't match - "
operator|+
name|msg
argument_list|,
literal|0
argument_list|,
name|br
operator|.
name|bytes
index|[
name|br
operator|.
name|offset
operator|+
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
default|default:
name|assertNotNull
argument_list|(
literal|"expected none null - "
operator|+
name|msg
argument_list|,
name|br
argument_list|)
expr_stmt|;
if|if
condition|(
name|br
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|bytes
operator|.
name|getBytes
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"expected empty bytes - "
operator|+
name|br
operator|.
name|utf8ToString
argument_list|()
operator|+
name|msg
argument_list|,
literal|0
argument_list|,
name|br
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|BytesRef
name|enumRef
init|=
name|attr
operator|.
name|bytes
argument_list|()
decl_stmt|;
comment|// test the actual doc values added in this iteration
name|assertEquals
argument_list|(
name|base
operator|+
name|numRemainingValues
argument_list|,
name|r
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|v
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|base
init|;
name|i
operator|<
name|r
operator|.
name|numDocs
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|msg
init|=
literal|" field: "
operator|+
name|byteIndexValue
operator|.
name|name
argument_list|()
operator|+
literal|" at index: "
operator|+
name|i
operator|+
literal|" base: "
operator|+
name|base
operator|+
literal|" numDocs:"
operator|+
name|r
operator|.
name|numDocs
argument_list|()
operator|+
literal|" bytesSize: "
operator|+
name|bytesSize
decl_stmt|;
while|while
condition|(
name|withDeletions
operator|&&
name|deleted
operator|.
name|get
argument_list|(
name|v
operator|++
argument_list|)
condition|)
block|{
name|upto
operator|+=
name|bytesSize
expr_stmt|;
block|}
name|BytesRef
name|br
init|=
name|bytes
operator|.
name|getBytes
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytesEnum
operator|.
name|docID
argument_list|()
operator|!=
name|i
condition|)
name|assertEquals
argument_list|(
literal|"seek failed for index "
operator|+
name|i
operator|+
literal|" "
operator|+
name|msg
argument_list|,
name|i
argument_list|,
name|bytesEnum
operator|.
name|advance
argument_list|(
name|i
argument_list|)
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
name|br
operator|.
name|length
condition|;
name|j
operator|++
operator|,
name|upto
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"EnumRef Byte at index "
operator|+
name|j
operator|+
literal|" doesn't match - "
operator|+
name|msg
argument_list|,
name|upto
argument_list|,
name|enumRef
operator|.
name|bytes
index|[
name|enumRef
operator|.
name|offset
operator|+
name|j
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"SourceRef Byte at index "
operator|+
name|j
operator|+
literal|" doesn't match - "
operator|+
name|msg
argument_list|,
name|upto
argument_list|,
name|br
operator|.
name|bytes
index|[
name|br
operator|.
name|offset
operator|+
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// clean up
name|closeables
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
for|for
control|(
name|Closeable
name|toClose
range|:
name|closeables
control|)
block|{
name|toClose
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getDocValues
specifier|private
name|DocValues
name|getDocValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|optimized
init|=
name|reader
operator|.
name|isOptimized
argument_list|()
decl_stmt|;
name|Fields
name|fields
init|=
name|optimized
condition|?
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
index|[
literal|0
index|]
operator|.
name|fields
argument_list|()
else|:
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
name|optimized
condition|?
literal|3
else|:
literal|2
argument_list|)
condition|)
block|{
comment|// case 2 only if optimized
case|case
literal|0
case|:
return|return
name|fields
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
case|case
literal|1
case|:
name|FieldsEnum
name|iterator
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|name
decl_stmt|;
while|while
condition|(
operator|(
name|name
operator|=
name|iterator
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
return|return
name|iterator
operator|.
name|docValues
argument_list|()
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no such field "
operator|+
name|field
argument_list|)
throw|;
case|case
literal|2
case|:
comment|// this only works if we are on an optimized index!
return|return
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
index|[
literal|0
index|]
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
DECL|method|getSource
specifier|private
name|Source
name|getSource
parameter_list|(
name|DocValues
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// getSource uses cache internally
return|return
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|values
operator|.
name|load
argument_list|()
else|:
name|values
operator|.
name|getSource
argument_list|()
return|;
block|}
block|}
end_class

end_unit

