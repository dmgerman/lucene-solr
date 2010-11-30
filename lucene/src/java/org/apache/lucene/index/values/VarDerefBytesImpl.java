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
name|IOException
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
name|AtomicLong
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
name|Bytes
operator|.
name|BytesBaseSource
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
name|Bytes
operator|.
name|BytesReaderBase
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
name|Bytes
operator|.
name|BytesWriterBase
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
name|FixedDerefBytesImpl
operator|.
name|Reader
operator|.
name|DerefBytesEnum
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
name|DataOutput
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
name|ArrayUtil
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
name|AttributeSource
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
name|ByteBlockPool
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
name|BytesRefHash
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
name|util
operator|.
name|PagedBytes
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
name|ByteBlockPool
operator|.
name|Allocator
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
name|ByteBlockPool
operator|.
name|DirectAllocator
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
name|BytesRefHash
operator|.
name|TrackingDirectBytesStartArray
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
comment|// Stores variable-length byte[] by deref, ie when two docs
end_comment

begin_comment
comment|// have the same value, they store only 1 byte[] and both
end_comment

begin_comment
comment|// docs reference that single source
end_comment

begin_class
DECL|class|VarDerefBytesImpl
class|class
name|VarDerefBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"VarDerefBytes"
decl_stmt|;
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
DECL|class|AddressByteStartArray
specifier|private
specifier|static
specifier|final
class|class
name|AddressByteStartArray
extends|extends
name|TrackingDirectBytesStartArray
block|{
DECL|field|address
name|int
index|[]
name|address
decl_stmt|;
DECL|method|AddressByteStartArray
name|AddressByteStartArray
parameter_list|(
name|int
name|size
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bytesUsed
specifier|public
name|AtomicLong
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
return|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|int
index|[]
name|clear
parameter_list|()
block|{
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|address
operator|.
name|length
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|address
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|super
operator|.
name|clear
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|public
name|int
index|[]
name|grow
parameter_list|()
block|{
assert|assert
name|address
operator|!=
literal|null
assert|;
specifier|final
name|int
name|oldSize
init|=
name|address
operator|.
name|length
decl_stmt|;
specifier|final
name|int
index|[]
name|retVal
init|=
name|super
operator|.
name|grow
argument_list|()
decl_stmt|;
name|address
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|address
argument_list|,
name|retVal
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
name|address
operator|.
name|length
operator|-
name|oldSize
operator|)
argument_list|)
expr_stmt|;
return|return
name|retVal
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|int
index|[]
name|init
parameter_list|()
block|{
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
name|address
operator|=
operator|new
name|int
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|initSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
index|]
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|address
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|init
argument_list|()
return|;
block|}
block|}
DECL|class|Writer
specifier|static
class|class
name|Writer
extends|extends
name|BytesWriterBase
block|{
DECL|field|docToAddress
specifier|private
name|int
index|[]
name|docToAddress
decl_stmt|;
DECL|field|address
specifier|private
name|int
name|address
init|=
literal|1
decl_stmt|;
DECL|field|array
specifier|private
specifier|final
name|AddressByteStartArray
name|array
init|=
operator|new
name|AddressByteStartArray
argument_list|(
literal|1
argument_list|,
name|bytesUsed
argument_list|)
decl_stmt|;
DECL|field|hash
specifier|private
specifier|final
name|BytesRefHash
name|hash
init|=
operator|new
name|BytesRefHash
argument_list|(
name|pool
argument_list|,
literal|16
argument_list|,
name|array
argument_list|)
decl_stmt|;
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
operator|new
name|DirectAllocator
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|)
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|Writer
specifier|public
name|Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|Allocator
name|allocator
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
operator|new
name|ByteBlockPool
argument_list|(
name|allocator
argument_list|)
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|docToAddress
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|synchronized
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
comment|// default
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
name|initDataOut
argument_list|()
expr_stmt|;
specifier|final
name|int
name|e
init|=
name|hash
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|docID
operator|>=
name|docToAddress
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|oldSize
init|=
name|docToAddress
operator|.
name|length
decl_stmt|;
name|docToAddress
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|docToAddress
argument_list|,
literal|1
operator|+
name|docID
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
name|docToAddress
operator|.
name|length
operator|-
name|oldSize
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|docAddress
decl_stmt|;
if|if
condition|(
name|e
operator|>=
literal|0
condition|)
block|{
name|docAddress
operator|=
name|array
operator|.
name|address
index|[
name|e
index|]
operator|=
name|address
expr_stmt|;
name|address
operator|+=
name|writePrefixLength
argument_list|(
name|datOut
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|address
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|docAddress
operator|=
name|array
operator|.
name|address
index|[
operator|(
operator|-
name|e
operator|)
operator|-
literal|1
index|]
expr_stmt|;
block|}
name|docToAddress
index|[
name|docID
index|]
operator|=
name|docAddress
expr_stmt|;
block|}
DECL|method|writePrefixLength
specifier|private
specifier|static
name|int
name|writePrefixLength
parameter_list|(
name|DataOutput
name|datOut
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|<
literal|128
condition|)
block|{
name|datOut
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
else|else
block|{
name|datOut
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|bytes
operator|.
name|length
operator|>>
literal|8
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|datOut
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|bytes
operator|.
name|length
operator|&
literal|0xff
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
block|}
comment|// Important that we get docCount, in case there were
comment|// some last docs that we didn't see
annotation|@
name|Override
DECL|method|finish
specifier|synchronized
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
return|return;
name|initIndexOut
argument_list|()
expr_stmt|;
name|idxOut
operator|.
name|writeInt
argument_list|(
name|address
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// write index
comment|// TODO(simonw): -- allow forcing fixed array (not -1)
comment|// TODO(simonw): check the address calculation / make it more intuitive
specifier|final
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|idxOut
argument_list|,
name|docCount
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|address
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|limit
decl_stmt|;
if|if
condition|(
name|docCount
operator|>
name|docToAddress
operator|.
name|length
condition|)
block|{
name|limit
operator|=
name|docToAddress
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|limit
operator|=
name|docCount
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
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|docToAddress
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|limit
init|;
name|i
operator|<
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
name|hash
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
operator|-
name|docToAddress
operator|.
name|length
operator|)
argument_list|)
expr_stmt|;
name|docToAddress
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|BytesReaderBase
block|{
DECL|method|Reader
name|Reader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|data
init|=
name|cloneData
argument_list|()
decl_stmt|;
specifier|final
name|IndexInput
name|index
init|=
name|cloneIndex
argument_list|()
decl_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|totalBytes
init|=
name|index
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// should be long
return|return
operator|new
name|Source
argument_list|(
name|data
argument_list|,
name|index
argument_list|,
name|totalBytes
argument_list|)
return|;
block|}
DECL|class|Source
specifier|private
specifier|static
class|class
name|Source
extends|extends
name|BytesBaseSource
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|index
decl_stmt|;
DECL|method|Source
specifier|public
name|Source
parameter_list|(
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|,
name|long
name|totalBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|datIn
argument_list|,
name|idxIn
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|totalBytes
argument_list|)
expr_stmt|;
name|index
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|idxIn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBytes
specifier|public
name|BytesRef
name|getBytes
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
name|long
name|address
init|=
name|index
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|address
operator|==
literal|0
condition|?
literal|null
else|:
name|data
operator|.
name|fillSliceWithPrefix
argument_list|(
name|bytesRef
argument_list|,
operator|--
name|address
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|BYTES_VAR_DEREF
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|protected
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|index
operator|.
name|size
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VarDerefBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|)
return|;
block|}
DECL|class|VarDerefBytesEnum
specifier|static
class|class
name|VarDerefBytesEnum
extends|extends
name|DerefBytesEnum
block|{
DECL|method|VarDerefBytesEnum
specifier|public
name|VarDerefBytesEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|datIn
parameter_list|,
name|IndexInput
name|idxIn
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|datIn
argument_list|,
name|idxIn
argument_list|,
operator|-
literal|1
argument_list|,
name|Values
operator|.
name|BYTES_VAR_DEREF
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|protected
name|void
name|fill
parameter_list|(
name|long
name|address
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
name|datIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
operator|--
name|address
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|sizeByte
init|=
name|datIn
operator|.
name|readByte
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
decl_stmt|;
if|if
condition|(
operator|(
name|sizeByte
operator|&
literal|128
operator|)
operator|==
literal|0
condition|)
block|{
comment|// length is 1 byte
name|size
operator|=
name|sizeByte
expr_stmt|;
block|}
else|else
block|{
name|size
operator|=
operator|(
operator|(
name|sizeByte
operator|&
literal|0x7f
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|datIn
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
operator|)
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|ref
operator|.
name|bytes
operator|.
name|length
operator|<
name|size
condition|)
name|ref
operator|.
name|grow
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|ref
operator|.
name|length
operator|=
name|size
expr_stmt|;
name|ref
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|datIn
operator|.
name|readBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Values
name|type
parameter_list|()
block|{
return|return
name|Values
operator|.
name|BYTES_VAR_DEREF
return|;
block|}
block|}
block|}
end_class

end_unit

