begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
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
name|Iterator
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
name|codecs
operator|.
name|CodecUtil
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|FieldInfo
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
name|SegmentWriteState
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
name|IndexOutput
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
name|IOUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|VERSION_CURRENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|BYTES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|SORTED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|SORTED_NUMERIC
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|SORTED_NUMERIC_SINGLETON
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|SORTED_SET
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|SORTED_SET_SINGLETON
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
operator|.
name|DirectDocValuesProducer
operator|.
name|NUMBER
import|;
end_import

begin_comment
comment|/**  * Writer for {@link DirectDocValuesFormat}  */
end_comment

begin_class
DECL|class|DirectDocValuesConsumer
class|class
name|DirectDocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
DECL|field|data
DECL|field|meta
name|IndexOutput
name|data
decl_stmt|,
name|meta
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|DirectDocValuesConsumer
name|DirectDocValuesConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|String
name|dataCodec
parameter_list|,
name|String
name|dataExtension
parameter_list|,
name|String
name|metaCodec
parameter_list|,
name|String
name|metaExtension
parameter_list|)
throws|throws
name|IOException
block|{
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|String
name|dataName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|dataExtension
argument_list|)
decl_stmt|;
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|dataName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|String
name|metaName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|metaExtension
argument_list|)
decl_stmt|;
name|meta
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|meta
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addNumericField
specifier|public
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|NUMBER
argument_list|)
expr_stmt|;
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|addNumericFieldValues
specifier|private
name|void
name|addNumericFieldValues
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|boolean
name|missing
init|=
literal|false
decl_stmt|;
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
if|if
condition|(
name|nv
operator|!=
literal|null
condition|)
block|{
name|long
name|v
init|=
name|nv
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValue
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|missing
operator|=
literal|true
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|>=
name|DirectDocValuesFormat
operator|.
name|MAX_SORTED_SET_ORDS
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|field
operator|.
name|name
operator|+
literal|"\" is too large, must be<= "
operator|+
name|DirectDocValuesFormat
operator|.
name|MAX_SORTED_SET_ORDS
operator|+
literal|" values/total ords"
argument_list|)
throw|;
block|}
block|}
name|meta
operator|.
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|missing
condition|)
block|{
name|long
name|start
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|writeMissingBitset
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
name|byte
name|byteWidth
decl_stmt|;
if|if
condition|(
name|minValue
operator|>=
name|Byte
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Byte
operator|.
name|MAX_VALUE
condition|)
block|{
name|byteWidth
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Short
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Short
operator|.
name|MAX_VALUE
condition|)
block|{
name|byteWidth
operator|=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|minValue
operator|>=
name|Integer
operator|.
name|MIN_VALUE
operator|&&
name|maxValue
operator|<=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|byteWidth
operator|=
literal|4
expr_stmt|;
block|}
else|else
block|{
name|byteWidth
operator|=
literal|8
expr_stmt|;
block|}
name|meta
operator|.
name|writeByte
argument_list|(
name|byteWidth
argument_list|)
expr_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|long
name|v
decl_stmt|;
if|if
condition|(
name|nv
operator|!=
literal|null
condition|)
block|{
name|v
operator|=
name|nv
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|v
operator|=
literal|0
expr_stmt|;
block|}
switch|switch
condition|(
name|byteWidth
condition|)
block|{
case|case
literal|1
case|:
name|data
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|v
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|data
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|v
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|data
operator|.
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|v
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|data
operator|.
name|writeLong
argument_list|(
name|v
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// write EOF marker
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|meta
argument_list|)
expr_stmt|;
comment|// write checksum
block|}
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|data
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
name|data
operator|=
name|meta
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addBinaryField
specifier|public
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|BYTES
argument_list|)
expr_stmt|;
name|addBinaryFieldValues
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
DECL|method|addBinaryFieldValues
specifier|private
name|void
name|addBinaryFieldValues
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write the byte[] data
specifier|final
name|long
name|startFP
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|boolean
name|missing
init|=
literal|false
decl_stmt|;
name|long
name|totalBytes
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|data
operator|.
name|writeBytes
argument_list|(
name|v
operator|.
name|bytes
argument_list|,
name|v
operator|.
name|offset
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|totalBytes
operator|+=
name|v
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|totalBytes
operator|>
name|DirectDocValuesFormat
operator|.
name|MAX_TOTAL_BYTES_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|field
operator|.
name|name
operator|+
literal|"\" is too large, cannot have more than DirectDocValuesFormat.MAX_TOTAL_BYTES_LENGTH ("
operator|+
name|DirectDocValuesFormat
operator|.
name|MAX_TOTAL_BYTES_LENGTH
operator|+
literal|") bytes"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|missing
operator|=
literal|true
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|meta
operator|.
name|writeLong
argument_list|(
name|startFP
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|totalBytes
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|missing
condition|)
block|{
name|long
name|start
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|writeMissingBitset
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeLong
argument_list|(
operator|-
literal|1L
argument_list|)
expr_stmt|;
block|}
name|int
name|addr
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|data
operator|.
name|writeInt
argument_list|(
name|addr
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|addr
operator|+=
name|v
operator|.
name|length
expr_stmt|;
block|}
block|}
name|data
operator|.
name|writeInt
argument_list|(
name|addr
argument_list|)
expr_stmt|;
block|}
comment|// TODO: in some cases representing missing with minValue-1 wouldn't take up additional space and so on,
comment|// but this is very simple, and algorithms only check this for values of 0 anyway (doesnt slow down normal decode)
DECL|method|writeMissingBitset
name|void
name|writeMissingBitset
parameter_list|(
name|Iterable
argument_list|<
name|?
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|bits
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|v
range|:
name|values
control|)
block|{
if|if
condition|(
name|count
operator|==
literal|64
condition|)
block|{
name|data
operator|.
name|writeLong
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|bits
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|bits
operator||=
literal|1L
operator|<<
operator|(
name|count
operator|&
literal|0x3f
operator|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|data
operator|.
name|writeLong
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|addSortedField
specifier|public
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
name|SORTED
argument_list|)
expr_stmt|;
comment|// write the ordinals as numerics
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|docToOrd
argument_list|)
expr_stmt|;
comment|// write the values as binary
name|addBinaryFieldValues
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addSortedNumericField
specifier|public
name|void
name|addSortedNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToValueCount
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSingleValued
argument_list|(
name|docToValueCount
argument_list|)
condition|)
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|SORTED_NUMERIC_SINGLETON
argument_list|)
expr_stmt|;
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|singletonView
argument_list|(
name|docToValueCount
argument_list|,
name|values
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|SORTED_NUMERIC
argument_list|)
expr_stmt|;
comment|// First write docToValueCounts, except we "aggregate" the
comment|// counts so they turn into addresses, and add a final
comment|// value = the total aggregate:
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|countToAddressIterator
argument_list|(
name|docToValueCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write values for all docs, appended into one big
comment|// numerics:
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
comment|// note: this might not be the most efficient... but it's fairly simple
annotation|@
name|Override
DECL|method|addSortedSetField
specifier|public
name|void
name|addSortedSetField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrdCount
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|ords
parameter_list|)
throws|throws
name|IOException
block|{
name|meta
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|number
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSingleValued
argument_list|(
name|docToOrdCount
argument_list|)
condition|)
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|SORTED_SET_SINGLETON
argument_list|)
expr_stmt|;
comment|// Write ordinals for all docs, appended into one big
comment|// numerics:
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|singletonView
argument_list|(
name|docToOrdCount
argument_list|,
name|ords
argument_list|,
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
comment|// write the values as binary
name|addBinaryFieldValues
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeByte
argument_list|(
name|SORTED_SET
argument_list|)
expr_stmt|;
comment|// First write docToOrdCounts, except we "aggregate" the
comment|// counts so they turn into addresses, and add a final
comment|// value = the total aggregate:
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|countToAddressIterator
argument_list|(
name|docToOrdCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write ordinals for all docs, appended into one big
comment|// numerics:
name|addNumericFieldValues
argument_list|(
name|field
argument_list|,
name|ords
argument_list|)
expr_stmt|;
comment|// write the values as binary
name|addBinaryFieldValues
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Just aggregates the count values so they become    * "addresses", and adds one more value in the end    * (the final sum)    */
DECL|method|countToAddressIterator
specifier|private
name|Iterable
argument_list|<
name|Number
argument_list|>
name|countToAddressIterator
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|Number
argument_list|>
name|counts
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iter
init|=
name|counts
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|long
name|sum
decl_stmt|;
name|boolean
name|ended
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
operator|||
operator|!
name|ended
return|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
name|long
name|toReturn
init|=
name|sum
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Number
name|n
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|sum
operator|+=
name|n
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|ended
condition|)
block|{
name|ended
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
assert|assert
literal|false
assert|;
block|}
return|return
name|toReturn
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

