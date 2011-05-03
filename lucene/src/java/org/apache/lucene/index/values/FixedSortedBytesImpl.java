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
name|Comparator
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
name|BytesBaseSortedSource
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
comment|// Stores fixed-length byte[] by deref, ie when two docs
end_comment

begin_comment
comment|// have the same value, they store only 1 byte[]
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FixedSortedBytesImpl
class|class
name|FixedSortedBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"FixedSortedBytes"
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
DECL|class|Writer
specifier|static
class|class
name|Writer
extends|extends
name|BytesWriterBase
block|{
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docToEntry
specifier|private
name|int
index|[]
name|docToEntry
decl_stmt|;
DECL|field|comp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
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
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
operator|new
name|TrackingDirectBytesStartArray
argument_list|(
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
name|bytesUsed
argument_list|)
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
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
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
name|comp
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
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
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
literal|true
argument_list|,
literal|true
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
name|docToEntry
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
comment|// docToEntry[0] = -1;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|this
operator|.
name|comp
operator|=
name|comp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
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
comment|// default - skip it
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|.
name|length
operator|!=
name|size
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expected bytes size="
operator|+
name|size
operator|+
literal|" but got "
operator|+
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
if|if
condition|(
name|docID
operator|>=
name|docToEntry
operator|.
name|length
condition|)
block|{
specifier|final
name|int
index|[]
name|newArray
init|=
operator|new
name|int
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|docID
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|docToEntry
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|docToEntry
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|newArray
operator|.
name|length
operator|-
name|docToEntry
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|docToEntry
operator|=
name|newArray
expr_stmt|;
block|}
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
name|docToEntry
index|[
name|docID
index|]
operator|=
literal|1
operator|+
operator|(
name|e
operator|<
literal|0
condition|?
operator|(
operator|-
name|e
operator|)
operator|-
literal|1
else|:
name|e
operator|)
expr_stmt|;
block|}
comment|// Important that we get docCount, in case there were
comment|// some last docs that we didn't see
annotation|@
name|Override
DECL|method|finish
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
try|try
block|{
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no data added
name|datOut
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|sortedEntries
init|=
name|hash
operator|.
name|sort
argument_list|(
name|comp
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|address
init|=
operator|new
name|int
index|[
name|count
index|]
decl_stmt|;
comment|// first dump bytes data, recording address as we go
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|e
init|=
name|sortedEntries
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
init|=
name|hash
operator|.
name|get
argument_list|(
name|e
argument_list|,
operator|new
name|BytesRef
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|size
assert|;
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
index|[
name|e
index|]
operator|=
literal|1
operator|+
name|i
expr_stmt|;
block|}
name|idxOut
operator|.
name|writeInt
argument_list|(
name|count
argument_list|)
expr_stmt|;
comment|// next write index
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
name|count
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
name|docToEntry
operator|.
name|length
condition|)
block|{
name|limit
operator|=
name|docToEntry
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
specifier|final
name|int
name|e
init|=
name|docToEntry
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|e
operator|==
literal|0
condition|)
block|{
comment|// null is encoded as zero
name|w
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|e
operator|>
literal|0
operator|&&
name|e
operator|<=
name|count
operator|:
literal|"index must  0>&&<= "
operator|+
name|count
operator|+
literal|" was: "
operator|+
name|e
assert|;
name|w
operator|.
name|add
argument_list|(
name|address
index|[
name|e
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
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
block|}
finally|finally
block|{
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
operator|(
operator|-
name|docToEntry
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|docToEntry
operator|=
literal|null
expr_stmt|;
name|hash
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|Reader
specifier|public
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
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load
specifier|public
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
name|load
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|loadSorted
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|loadSorted
specifier|public
name|SortedSource
name|loadSorted
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|idxInput
init|=
name|cloneIndex
argument_list|()
decl_stmt|;
specifier|final
name|IndexInput
name|datInput
init|=
name|cloneData
argument_list|()
decl_stmt|;
name|datInput
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
operator|+
literal|4
argument_list|)
expr_stmt|;
name|idxInput
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
return|return
operator|new
name|Source
argument_list|(
name|datInput
argument_list|,
name|idxInput
argument_list|,
name|size
argument_list|,
name|idxInput
operator|.
name|readInt
argument_list|()
argument_list|,
name|comp
argument_list|)
return|;
block|}
DECL|class|Source
specifier|private
specifier|static
class|class
name|Source
extends|extends
name|BytesBaseSortedSource
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|index
decl_stmt|;
DECL|field|numValue
specifier|private
specifier|final
name|int
name|numValue
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
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
name|int
name|size
parameter_list|,
name|int
name|numValues
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
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
name|comp
argument_list|,
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
argument_list|,
name|size
operator|*
name|numValues
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|numValue
operator|=
name|numValues
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
name|closeIndexInput
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|index
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getByValue
specifier|public
name|LookupResult
name|getByValue
parameter_list|(
name|BytesRef
name|bytes
parameter_list|,
name|BytesRef
name|tmpRef
parameter_list|)
block|{
return|return
name|binarySearch
argument_list|(
name|bytes
argument_list|,
name|tmpRef
argument_list|,
literal|0
argument_list|,
name|numValue
operator|-
literal|1
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
return|return
name|numValue
return|;
block|}
annotation|@
name|Override
DECL|method|deref
specifier|protected
name|BytesRef
name|deref
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|bytesRef
parameter_list|)
block|{
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
operator|(
name|ord
operator|*
name|size
operator|)
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|Type
operator|.
name|BYTES_FIXED_SORTED
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
name|DocValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do unsorted
return|return
operator|new
name|DerefBytesEnum
argument_list|(
name|source
argument_list|,
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|Type
operator|.
name|BYTES_FIXED_SORTED
return|;
block|}
block|}
block|}
end_class

end_unit

