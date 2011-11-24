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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumMap
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexDocValuesArray
specifier|abstract
class|class
name|IndexDocValuesArray
extends|extends
name|Source
block|{
DECL|field|TEMPLATES
specifier|static
specifier|final
name|Map
argument_list|<
name|ValueType
argument_list|,
name|IndexDocValuesArray
argument_list|>
name|TEMPLATES
decl_stmt|;
static|static
block|{
name|EnumMap
argument_list|<
name|ValueType
argument_list|,
name|IndexDocValuesArray
argument_list|>
name|templates
init|=
operator|new
name|EnumMap
argument_list|<
name|ValueType
argument_list|,
name|IndexDocValuesArray
argument_list|>
argument_list|(
name|ValueType
operator|.
name|class
argument_list|)
decl_stmt|;
name|templates
operator|.
name|put
argument_list|(
name|ValueType
operator|.
name|FIXED_INTS_16
argument_list|,
operator|new
name|ShortValues
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|.
name|put
argument_list|(
name|ValueType
operator|.
name|FIXED_INTS_32
argument_list|,
operator|new
name|IntValues
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|.
name|put
argument_list|(
name|ValueType
operator|.
name|FIXED_INTS_64
argument_list|,
operator|new
name|LongValues
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|.
name|put
argument_list|(
name|ValueType
operator|.
name|FIXED_INTS_8
argument_list|,
operator|new
name|ByteValues
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|.
name|put
argument_list|(
name|ValueType
operator|.
name|FLOAT_32
argument_list|,
operator|new
name|FloatValues
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|.
name|put
argument_list|(
name|ValueType
operator|.
name|FLOAT_64
argument_list|,
operator|new
name|DoubleValues
argument_list|()
argument_list|)
expr_stmt|;
name|TEMPLATES
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|templates
argument_list|)
expr_stmt|;
block|}
DECL|field|bytesPerValue
specifier|protected
specifier|final
name|int
name|bytesPerValue
decl_stmt|;
DECL|method|IndexDocValuesArray
name|IndexDocValuesArray
parameter_list|(
name|int
name|bytesPerValue
parameter_list|,
name|ValueType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesPerValue
operator|=
name|bytesPerValue
expr_stmt|;
block|}
DECL|method|newFromInput
specifier|public
specifier|abstract
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|hasArray
specifier|public
specifier|final
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|toBytes
name|void
name|toBytes
parameter_list|(
name|long
name|value
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|copyLong
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|toBytes
name|void
name|toBytes
parameter_list|(
name|double
name|value
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|copyLong
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|ByteValues
specifier|final
specifier|static
class|class
name|ByteValues
extends|extends
name|IndexDocValuesArray
block|{
DECL|field|values
specifier|private
specifier|final
name|byte
index|[]
name|values
decl_stmt|;
DECL|method|ByteValues
name|ByteValues
parameter_list|()
block|{
name|super
argument_list|(
literal|1
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_8
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|ByteValues
specifier|private
name|ByteValues
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|1
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_8
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|byte
index|[
name|numDocs
index|]
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|byte
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|values
operator|.
name|length
assert|;
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|newFromInput
specifier|public
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ByteValues
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
DECL|method|toBytes
name|void
name|toBytes
parameter_list|(
name|long
name|value
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|bytes
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0xFFL
operator|&
name|value
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|class|ShortValues
specifier|final
specifier|static
class|class
name|ShortValues
extends|extends
name|IndexDocValuesArray
block|{
DECL|field|values
specifier|private
specifier|final
name|short
index|[]
name|values
decl_stmt|;
DECL|method|ShortValues
name|ShortValues
parameter_list|()
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_SHORT
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_16
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|short
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|ShortValues
specifier|private
name|ShortValues
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_SHORT
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_16
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|short
index|[
name|numDocs
index|]
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|short
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|values
operator|.
name|length
assert|;
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|newFromInput
specifier|public
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ShortValues
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
DECL|method|toBytes
name|void
name|toBytes
parameter_list|(
name|long
name|value
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|copyShort
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|0xFFFFL
operator|&
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|class|IntValues
specifier|final
specifier|static
class|class
name|IntValues
extends|extends
name|IndexDocValuesArray
block|{
DECL|field|values
specifier|private
specifier|final
name|int
index|[]
name|values
decl_stmt|;
DECL|method|IntValues
name|IntValues
parameter_list|()
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_32
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|IntValues
specifier|private
name|IntValues
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_32
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|numDocs
index|]
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|int
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|values
operator|.
name|length
assert|;
return|return
literal|0xFFFFFFFF
operator|&
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|newFromInput
specifier|public
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|IntValues
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
DECL|method|toBytes
name|void
name|toBytes
parameter_list|(
name|long
name|value
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|copyInt
argument_list|(
call|(
name|int
call|)
argument_list|(
literal|0xFFFFFFFF
operator|&
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|class|LongValues
specifier|final
specifier|static
class|class
name|LongValues
extends|extends
name|IndexDocValuesArray
block|{
DECL|field|values
specifier|private
specifier|final
name|long
index|[]
name|values
decl_stmt|;
DECL|method|LongValues
name|LongValues
parameter_list|()
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_64
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|LongValues
specifier|private
name|LongValues
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
argument_list|,
name|ValueType
operator|.
name|FIXED_INTS_64
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[
name|numDocs
index|]
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|long
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|getInt
specifier|public
name|long
name|getInt
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|values
operator|.
name|length
assert|;
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|newFromInput
specifier|public
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LongValues
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
block|}
empty_stmt|;
DECL|class|FloatValues
specifier|final
specifier|static
class|class
name|FloatValues
extends|extends
name|IndexDocValuesArray
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|FloatValues
name|FloatValues
parameter_list|()
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
argument_list|,
name|ValueType
operator|.
name|FLOAT_32
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|float
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|FloatValues
specifier|private
name|FloatValues
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
argument_list|,
name|ValueType
operator|.
name|FLOAT_32
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|float
index|[
name|numDocs
index|]
expr_stmt|;
comment|/*        * we always read BIG_ENDIAN here since the writer serialized plain bytes        * we can simply read the ints / longs back in using readInt / readLong        */
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
name|values
index|[
name|i
index|]
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|input
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|float
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|values
operator|.
name|length
assert|;
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|toBytes
name|void
name|toBytes
parameter_list|(
name|double
name|value
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|bytesRef
operator|.
name|copyInt
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
operator|(
name|float
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newFromInput
specifier|public
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FloatValues
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
block|}
empty_stmt|;
DECL|class|DoubleValues
specifier|final
specifier|static
class|class
name|DoubleValues
extends|extends
name|IndexDocValuesArray
block|{
DECL|field|values
specifier|private
specifier|final
name|double
index|[]
name|values
decl_stmt|;
DECL|method|DoubleValues
name|DoubleValues
parameter_list|()
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_DOUBLE
argument_list|,
name|ValueType
operator|.
name|FLOAT_64
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|double
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|DoubleValues
specifier|private
name|DoubleValues
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_DOUBLE
argument_list|,
name|ValueType
operator|.
name|FLOAT_64
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|double
index|[
name|numDocs
index|]
expr_stmt|;
comment|/*        * we always read BIG_ENDIAN here since the writer serialized plain bytes        * we can simply read the ints / longs back in using readInt / readLong        */
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
name|values
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|input
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|double
index|[]
name|getArray
parameter_list|()
block|{
return|return
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|values
operator|.
name|length
assert|;
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|newFromInput
specifier|public
name|IndexDocValuesArray
name|newFromInput
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DoubleValues
argument_list|(
name|input
argument_list|,
name|numDocs
argument_list|)
return|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

