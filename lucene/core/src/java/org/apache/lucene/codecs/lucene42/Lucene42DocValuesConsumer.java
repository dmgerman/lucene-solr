begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene42
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
name|HashMap
import|;
end_import

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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|FST
operator|.
name|INPUT_TYPE
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
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
name|packed
operator|.
name|BlockPackedWriter
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * Writes numbers one of two ways:  * 1. packed ints as deltas from minValue  * 2. packed ints as ordinals to a table (if the number of values is small, e.g.<= 256)  *   * the latter is typically much smaller with lucene's sims, as only some byte values are used,  * but its often a nonlinear mapping, especially if you dont use crazy boosts.  */
end_comment

begin_class
DECL|class|Lucene42DocValuesConsumer
class|class
name|Lucene42DocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|NUMBER
specifier|static
specifier|final
name|byte
name|NUMBER
init|=
literal|0
decl_stmt|;
DECL|field|BYTES
specifier|static
specifier|final
name|byte
name|BYTES
init|=
literal|1
decl_stmt|;
DECL|field|FST
specifier|static
specifier|final
name|byte
name|FST
init|=
literal|2
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|data
DECL|field|meta
specifier|final
name|IndexOutput
name|data
decl_stmt|,
name|meta
decl_stmt|;
DECL|method|Lucene42DocValuesConsumer
name|Lucene42DocValuesConsumer
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
name|writeHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_CURRENT
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
name|writeHeader
argument_list|(
name|meta
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_CURRENT
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
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|// TODO: more efficient?
name|HashSet
argument_list|<
name|Long
argument_list|>
name|uniqueValues
init|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
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
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|uniqueValues
operator|.
name|add
argument_list|(
name|v
argument_list|)
condition|)
block|{
if|if
condition|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|>
literal|256
condition|)
block|{
name|uniqueValues
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
if|if
condition|(
name|uniqueValues
operator|!=
literal|null
operator|&&
operator|(
name|delta
operator|<
literal|0
operator|||
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|<
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
operator|)
condition|)
block|{
comment|// smaller to tableize
specifier|final
name|int
name|bitsPerValue
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|uniqueValues
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
comment|// table-compressed
name|Long
index|[]
name|decode
init|=
name|uniqueValues
operator|.
name|toArray
argument_list|(
operator|new
name|Long
index|[
name|uniqueValues
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|encode
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|decode
operator|.
name|length
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
name|decode
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
operator|.
name|writeLong
argument_list|(
name|decode
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|encode
operator|.
name|put
argument_list|(
name|decode
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|data
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|bitsPerValue
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|writer
init|=
name|PackedInts
operator|.
name|getWriterNoHeader
argument_list|(
name|data
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|count
argument_list|,
name|bitsPerValue
argument_list|,
name|PackedInts
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|encode
operator|.
name|get
argument_list|(
name|nv
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
comment|// delta-compressed
name|data
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|data
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
specifier|final
name|BlockPackedWriter
name|writer
init|=
operator|new
name|BlockPackedWriter
argument_list|(
name|data
argument_list|,
name|BLOCK_SIZE
argument_list|)
decl_stmt|;
for|for
control|(
name|Number
name|nv
range|:
name|values
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|nv
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
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
comment|// nocommit: just write this to a RAMfile or something and flush it here, with #fields first.
comment|// this meta is a tiny file so this hurts nobody
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
comment|// write the byte[] data
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
name|int
name|minLength
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|maxLength
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|final
name|long
name|startFP
init|=
name|data
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
for|for
control|(
name|BytesRef
name|v
range|:
name|values
control|)
block|{
name|minLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLength
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|v
operator|.
name|length
argument_list|)
expr_stmt|;
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
name|writeLong
argument_list|(
name|data
operator|.
name|getFilePointer
argument_list|()
operator|-
name|startFP
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|minLength
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
name|maxLength
argument_list|)
expr_stmt|;
comment|// if minLength == maxLength, its a fixed-length byte[], we are done (the addresses are implicit)
comment|// otherwise, we need to record the length fields...
comment|// TODO: make this more efficient. this is just as inefficient as 4.0 codec.... we can do much better.
if|if
condition|(
name|minLength
operator|!=
name|maxLength
condition|)
block|{
name|addNumericField
argument_list|(
name|field
argument_list|,
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
name|BytesRef
argument_list|>
name|inner
init|=
name|values
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
name|addr
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|inner
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
name|BytesRef
name|b
init|=
name|inner
operator|.
name|next
argument_list|()
decl_stmt|;
name|addr
operator|+=
name|b
operator|.
name|length
expr_stmt|;
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|addr
argument_list|)
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
comment|// write the ordinals as numerics
name|addNumericField
argument_list|(
name|field
argument_list|,
name|docToOrd
argument_list|)
expr_stmt|;
comment|// write the values as FST
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
name|FST
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
argument_list|)
expr_stmt|;
name|PositiveIntOutputs
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Builder
argument_list|<
name|Long
argument_list|>
name|builder
init|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
name|long
name|ord
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
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|v
argument_list|,
name|scratch
argument_list|)
argument_list|,
name|ord
argument_list|)
expr_stmt|;
name|ord
operator|++
expr_stmt|;
block|}
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|fst
operator|.
name|save
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeVInt
argument_list|(
operator|(
name|int
operator|)
name|ord
argument_list|)
expr_stmt|;
block|}
comment|// nocommit: can/should we make override merge + make it smarter to pull the values
comment|// directly from disk for fields that arent already loaded up in ram?
block|}
end_class

end_unit

