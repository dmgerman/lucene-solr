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
name|EnumSet
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
name|IndexDocValuesField
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
name|IndexReader
operator|.
name|ReaderContext
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
name|NoMergePolicy
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
name|Codec
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
name|lucene40
operator|.
name|values
operator|.
name|BytesRefUtils
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
name|IndexDocValues
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
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_class
DECL|class|TestTypePromotion
specifier|public
class|class
name|TestTypePromotion
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Before
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
name|assumeFalse
argument_list|(
literal|"cannot work with preflex codec"
argument_list|,
name|Codec
operator|.
name|getDefault
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Lucene3x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|INTEGERS
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|ValueType
argument_list|>
name|INTEGERS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ValueType
operator|.
name|VAR_INTS
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_16
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_32
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_64
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_8
argument_list|)
decl_stmt|;
DECL|field|FLOATS
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|ValueType
argument_list|>
name|FLOATS
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ValueType
operator|.
name|FLOAT_32
argument_list|,
name|ValueType
operator|.
name|FLOAT_64
argument_list|)
decl_stmt|;
DECL|field|UNSORTED_BYTES
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|ValueType
argument_list|>
name|UNSORTED_BYTES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ValueType
operator|.
name|BYTES_FIXED_DEREF
argument_list|,
name|ValueType
operator|.
name|BYTES_FIXED_STRAIGHT
argument_list|,
name|ValueType
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|,
name|ValueType
operator|.
name|BYTES_VAR_DEREF
argument_list|)
decl_stmt|;
DECL|field|SORTED_BYTES
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|ValueType
argument_list|>
name|SORTED_BYTES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|ValueType
operator|.
name|BYTES_FIXED_SORTED
argument_list|,
name|ValueType
operator|.
name|BYTES_VAR_SORTED
argument_list|)
decl_stmt|;
DECL|method|randomValueType
specifier|public
name|ValueType
name|randomValueType
parameter_list|(
name|EnumSet
argument_list|<
name|ValueType
argument_list|>
name|typeEnum
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
name|ValueType
index|[]
name|array
init|=
name|typeEnum
operator|.
name|toArray
argument_list|(
operator|new
name|ValueType
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
return|return
name|array
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
index|]
return|;
block|}
DECL|enum|TestType
specifier|private
specifier|static
enum|enum
name|TestType
block|{
DECL|enum constant|Int
DECL|enum constant|Float
DECL|enum constant|Byte
name|Int
block|,
name|Float
block|,
name|Byte
block|}
DECL|method|runTest
specifier|private
name|void
name|runTest
parameter_list|(
name|EnumSet
argument_list|<
name|ValueType
argument_list|>
name|types
parameter_list|,
name|TestType
name|type
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
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
argument_list|)
decl_stmt|;
name|int
name|num_1
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|int
name|num_2
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|int
name|num_3
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|num_1
operator|+
name|num_2
operator|+
name|num_3
index|]
decl_stmt|;
name|index
argument_list|(
name|writer
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
name|num_1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|index
argument_list|(
name|writer
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
argument_list|,
name|num_2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// once in a while use addIndexes
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Directory
name|dir_2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer_2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir_2
argument_list|,
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
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|writer_2
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
operator|+
name|num_2
argument_list|,
name|num_3
argument_list|)
expr_stmt|;
name|writer_2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer_2
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dir_2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// do a real merge here
name|IndexReader
name|open
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir_2
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|open
argument_list|)
expr_stmt|;
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir_2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|index
argument_list|(
name|writer
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|types
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
operator|+
name|num_2
argument_list|,
name|num_3
argument_list|)
expr_stmt|;
block|}
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
name|assertValues
argument_list|(
name|type
argument_list|,
name|dir
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertValues
specifier|private
name|void
name|assertValues
parameter_list|(
name|TestType
name|type
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|long
index|[]
name|values
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexReader
name|reader
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
literal|1
argument_list|,
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|ReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|ReaderContext
index|[]
name|children
init|=
name|topReaderContext
operator|.
name|children
argument_list|()
decl_stmt|;
name|IndexDocValues
name|docValues
init|=
name|children
index|[
literal|0
index|]
operator|.
name|reader
operator|.
name|docValues
argument_list|(
literal|"promote"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|children
operator|.
name|length
argument_list|)
expr_stmt|;
name|Source
name|directSource
init|=
name|docValues
operator|.
name|getDirectSource
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|"id: "
operator|+
name|id
operator|+
literal|" doc: "
operator|+
name|i
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Byte
case|:
name|BytesRef
name|bytes
init|=
name|directSource
operator|.
name|getBytes
argument_list|(
name|i
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|value
init|=
literal|0
decl_stmt|;
switch|switch
condition|(
name|bytes
operator|.
name|length
condition|)
block|{
case|case
literal|1
case|:
name|value
operator|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
index|]
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|value
operator|=
name|BytesRefUtils
operator|.
name|asShort
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|value
operator|=
name|BytesRefUtils
operator|.
name|asInt
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|value
operator|=
name|BytesRefUtils
operator|.
name|asLong
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
name|msg
operator|+
literal|" bytessize: "
operator|+
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|msg
operator|+
literal|" byteSize: "
operator|+
name|bytes
operator|.
name|length
argument_list|,
name|values
index|[
name|id
index|]
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|Float
case|:
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|values
index|[
name|id
index|]
argument_list|,
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|directSource
operator|.
name|getFloat
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Int
case|:
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|values
index|[
name|id
index|]
argument_list|,
name|directSource
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
name|docValues
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|void
name|index
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|IndexDocValuesField
name|valField
parameter_list|,
name|ValueType
name|valueType
parameter_list|,
name|long
index|[]
name|values
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
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
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|offset
operator|+
name|num
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|i
operator|+
literal|""
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|valueType
condition|)
block|{
case|case
name|VAR_INTS
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|valField
operator|.
name|setInt
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_16
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextInt
argument_list|(
name|Short
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setInt
argument_list|(
operator|(
name|short
operator|)
name|values
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_32
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|valField
operator|.
name|setInt
argument_list|(
operator|(
name|int
operator|)
name|values
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_64
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|valField
operator|.
name|setInt
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_64
case|:
name|double
name|nextDouble
init|=
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|nextDouble
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setFloat
argument_list|(
name|nextDouble
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT_32
case|:
specifier|final
name|float
name|nextFloat
init|=
name|random
operator|.
name|nextFloat
argument_list|()
decl_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|nextFloat
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setFloat
argument_list|(
name|nextFloat
argument_list|)
expr_stmt|;
break|break;
case|case
name|FIXED_INTS_8
case|:
name|values
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|i
expr_stmt|;
name|valField
operator|.
name|setInt
argument_list|(
operator|(
name|byte
operator|)
name|values
index|[
name|i
index|]
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_FIXED_DEREF
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|values
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextLong
argument_list|()
expr_stmt|;
name|BytesRefUtils
operator|.
name|copyLong
argument_list|(
name|ref
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|valField
operator|.
name|setBytes
argument_list|(
name|ref
argument_list|,
name|valueType
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_VAR_STRAIGHT
case|:
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|BytesRefUtils
operator|.
name|copyInt
argument_list|(
name|ref
argument_list|,
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|BytesRefUtils
operator|.
name|asInt
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesRefUtils
operator|.
name|copyLong
argument_list|(
name|ref
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|BytesRefUtils
operator|.
name|asLong
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
name|valField
operator|.
name|setBytes
argument_list|(
name|ref
argument_list|,
name|valueType
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|(
literal|"unexpected value "
operator|+
name|valueType
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|valField
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
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
DECL|method|testPromoteBytes
specifier|public
name|void
name|testPromoteBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|runTest
argument_list|(
name|UNSORTED_BYTES
argument_list|,
name|TestType
operator|.
name|Byte
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortedPromoteBytes
specifier|public
name|void
name|testSortedPromoteBytes
parameter_list|()
throws|throws
name|IOException
block|{
name|runTest
argument_list|(
name|SORTED_BYTES
argument_list|,
name|TestType
operator|.
name|Byte
argument_list|)
expr_stmt|;
block|}
DECL|method|testPromotInteger
specifier|public
name|void
name|testPromotInteger
parameter_list|()
throws|throws
name|IOException
block|{
name|runTest
argument_list|(
name|INTEGERS
argument_list|,
name|TestType
operator|.
name|Int
argument_list|)
expr_stmt|;
block|}
DECL|method|testPromotFloatingPoint
specifier|public
name|void
name|testPromotFloatingPoint
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|runTest
argument_list|(
name|FLOATS
argument_list|,
name|TestType
operator|.
name|Float
argument_list|)
expr_stmt|;
block|}
DECL|method|testMergeIncompatibleTypes
specifier|public
name|void
name|testMergeIncompatibleTypes
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|writerConfig
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
name|writerConfig
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|NO_COMPOUND_FILES
argument_list|)
expr_stmt|;
comment|// no merges until we are done with adding values
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
argument_list|)
decl_stmt|;
name|int
name|num_1
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|int
name|num_2
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|num_1
operator|+
name|num_2
index|]
decl_stmt|;
name|index
argument_list|(
name|writer
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|INTEGERS
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
name|num_1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// once in a while use addIndexes
name|Directory
name|dir_2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer_2
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir_2
argument_list|,
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
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|writer_2
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|UNSORTED_BYTES
else|:
name|SORTED_BYTES
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
argument_list|,
name|num_2
argument_list|)
expr_stmt|;
name|writer_2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer_2
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|addIndexes
argument_list|(
name|dir_2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// do a real merge here
name|IndexReader
name|open
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir_2
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addIndexes
argument_list|(
name|open
argument_list|)
expr_stmt|;
name|open
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir_2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|index
argument_list|(
name|writer
argument_list|,
operator|new
name|IndexDocValuesField
argument_list|(
literal|"promote"
argument_list|)
argument_list|,
name|randomValueType
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|UNSORTED_BYTES
else|:
name|SORTED_BYTES
argument_list|,
name|random
argument_list|)
argument_list|,
name|values
argument_list|,
name|num_1
argument_list|,
name|num_2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writerConfig
operator|=
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
expr_stmt|;
if|if
condition|(
name|writerConfig
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|NoMergePolicy
condition|)
block|{
name|writerConfig
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure we merge to one segment (merge everything together)
block|}
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|writerConfig
argument_list|)
expr_stmt|;
comment|// now merge
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
name|IndexReader
name|reader
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
literal|1
argument_list|,
name|reader
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|ReaderContext
name|topReaderContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|ReaderContext
index|[]
name|children
init|=
name|topReaderContext
operator|.
name|children
argument_list|()
decl_stmt|;
name|IndexDocValues
name|docValues
init|=
name|children
index|[
literal|0
index|]
operator|.
name|reader
operator|.
name|docValues
argument_list|(
literal|"promote"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docValues
argument_list|)
expr_stmt|;
name|assertValues
argument_list|(
name|TestType
operator|.
name|Byte
argument_list|,
name|dir
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ValueType
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|,
name|docValues
operator|.
name|type
argument_list|()
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

