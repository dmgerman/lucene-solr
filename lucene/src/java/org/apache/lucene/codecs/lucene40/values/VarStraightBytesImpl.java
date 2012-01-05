begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|codecs
operator|.
name|lucene40
operator|.
name|values
operator|.
name|Bytes
operator|.
name|BytesSourceBase
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
name|lucene40
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
name|DocValues
operator|.
name|Type
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
name|IOContext
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
name|Counter
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
name|DirectTrackingAllocator
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
operator|.
name|ReaderIterator
import|;
end_import

begin_comment
comment|// Variable length byte[] per document, no sharing
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|VarStraightBytesImpl
class|class
name|VarStraightBytesImpl
block|{
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"VarStraightBytes"
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
DECL|field|address
specifier|private
name|long
name|address
decl_stmt|;
comment|// start at -1 if the first added value is> 0
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|docToAddress
specifier|private
name|long
index|[]
name|docToAddress
decl_stmt|;
DECL|field|pool
specifier|private
specifier|final
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|field|datOut
specifier|private
name|IndexOutput
name|datOut
decl_stmt|;
DECL|field|merge
specifier|private
name|boolean
name|merge
init|=
literal|false
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
name|Counter
name|bytesUsed
parameter_list|,
name|IOContext
name|context
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
name|bytesUsed
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|DirectTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|docToAddress
operator|=
operator|new
name|long
index|[
literal|1
index|]
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
comment|// init
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
comment|// Fills up to but not including this docID
DECL|method|fill
specifier|private
name|void
name|fill
parameter_list|(
specifier|final
name|int
name|docID
parameter_list|,
specifier|final
name|long
name|nextAddress
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|>=
name|docToAddress
operator|.
name|length
condition|)
block|{
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
operator|(
name|docToAddress
operator|.
name|length
operator|-
name|oldSize
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|lastDocID
operator|+
literal|1
init|;
name|i
operator|<
name|docID
condition|;
name|i
operator|++
control|)
block|{
name|docToAddress
index|[
name|i
index|]
operator|=
name|nextAddress
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|protected
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
assert|assert
operator|!
name|merge
assert|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
comment|// default
block|}
name|fill
argument_list|(
name|docID
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|docToAddress
index|[
name|docID
index|]
operator|=
name|address
expr_stmt|;
name|pool
operator|.
name|copy
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|address
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|SingleSubMergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|merge
operator|=
literal|true
expr_stmt|;
name|datOut
operator|=
name|getOrCreateDataOut
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|state
operator|.
name|liveDocs
operator|==
literal|null
operator|&&
name|state
operator|.
name|reader
operator|instanceof
name|VarStraightReader
condition|)
block|{
comment|// bulk merge since we don't have any deletes
name|VarStraightReader
name|reader
init|=
operator|(
name|VarStraightReader
operator|)
name|state
operator|.
name|reader
decl_stmt|;
specifier|final
name|int
name|maxDocs
init|=
name|reader
operator|.
name|maxDoc
decl_stmt|;
if|if
condition|(
name|maxDocs
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|lastDocID
operator|+
literal|1
operator|<
name|state
operator|.
name|docBase
condition|)
block|{
name|fill
argument_list|(
name|state
operator|.
name|docBase
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|lastDocID
operator|=
name|state
operator|.
name|docBase
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|long
name|numDataBytes
decl_stmt|;
specifier|final
name|IndexInput
name|cloneIdx
init|=
name|reader
operator|.
name|cloneIndex
argument_list|()
decl_stmt|;
try|try
block|{
name|numDataBytes
operator|=
name|cloneIdx
operator|.
name|readVLong
argument_list|()
expr_stmt|;
specifier|final
name|ReaderIterator
name|iter
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|cloneIdx
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
name|maxDocs
condition|;
name|i
operator|++
control|)
block|{
name|long
name|offset
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
operator|++
name|lastDocID
expr_stmt|;
if|if
condition|(
name|lastDocID
operator|>=
name|docToAddress
operator|.
name|length
condition|)
block|{
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
name|lastDocID
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|docToAddress
operator|.
name|length
operator|-
name|oldSize
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
name|docToAddress
index|[
name|lastDocID
index|]
operator|=
name|address
operator|+
name|offset
expr_stmt|;
block|}
name|address
operator|+=
name|numDataBytes
expr_stmt|;
comment|// this is the address after all addr pointers are updated
name|iter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|cloneIdx
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexInput
name|cloneData
init|=
name|reader
operator|.
name|cloneData
argument_list|()
decl_stmt|;
try|try
block|{
name|datOut
operator|.
name|copyBytes
argument_list|(
name|cloneData
argument_list|,
name|numDataBytes
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|cloneData
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|merge
argument_list|(
name|state
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
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|sourceDoc
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|merge
assert|;
assert|assert
name|lastDocID
operator|<
name|docID
assert|;
name|currentMergeSource
operator|.
name|getBytes
argument_list|(
name|sourceDoc
argument_list|,
name|bytesRef
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytesRef
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
comment|// default
block|}
name|fill
argument_list|(
name|docID
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|datOut
operator|.
name|writeBytes
argument_list|(
name|bytesRef
operator|.
name|bytes
argument_list|,
name|bytesRef
operator|.
name|offset
argument_list|,
name|bytesRef
operator|.
name|length
argument_list|)
expr_stmt|;
name|docToAddress
index|[
name|docID
index|]
operator|=
name|address
expr_stmt|;
name|address
operator|+=
name|bytesRef
operator|.
name|length
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
assert|assert
operator|(
operator|!
name|merge
operator|&&
name|datOut
operator|==
literal|null
operator|)
operator|||
operator|(
name|merge
operator|&&
name|datOut
operator|!=
literal|null
operator|)
assert|;
specifier|final
name|IndexOutput
name|datOut
init|=
name|getOrCreateDataOut
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|merge
condition|)
block|{
comment|// header is already written in getDataOut()
name|pool
operator|.
name|writePool
argument_list|(
name|datOut
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
name|datOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|dropBuffersAndReset
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|false
expr_stmt|;
specifier|final
name|IndexOutput
name|idxOut
init|=
name|getOrCreateIndexOut
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|lastDocID
operator|==
operator|-
literal|1
condition|)
block|{
name|idxOut
operator|.
name|writeVLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
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
operator|+
literal|1
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
comment|// docCount+1 so we write sentinel
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docCount
operator|+
literal|1
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
else|else
block|{
name|fill
argument_list|(
name|docCount
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|idxOut
operator|.
name|writeVLong
argument_list|(
name|address
argument_list|)
expr_stmt|;
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
operator|+
literal|1
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|address
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
name|docToAddress
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// write sentinel
name|w
operator|.
name|add
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|docToAddress
operator|.
name|length
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|docToAddress
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|idxOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|idxOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
block|}
DECL|class|VarStraightReader
specifier|public
specifier|static
class|class
name|VarStraightReader
extends|extends
name|BytesReaderBase
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|VarStraightReader
name|VarStraightReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|IOContext
name|context
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
argument_list|,
name|context
argument_list|,
name|Type
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
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
return|return
operator|new
name|VarStraightSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectSource
specifier|public
name|Source
name|getDirectSource
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DirectVarStraightSource
argument_list|(
name|cloneData
argument_list|()
argument_list|,
name|cloneIndex
argument_list|()
argument_list|,
name|type
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|VarStraightSource
specifier|private
specifier|static
specifier|final
class|class
name|VarStraightSource
extends|extends
name|BytesSourceBase
block|{
DECL|field|addresses
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|addresses
decl_stmt|;
DECL|method|VarStraightSource
specifier|public
name|VarStraightSource
parameter_list|(
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
name|idxIn
operator|.
name|readVLong
argument_list|()
argument_list|,
name|Type
operator|.
name|BYTES_VAR_STRAIGHT
argument_list|)
expr_stmt|;
name|addresses
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
specifier|final
name|long
name|address
init|=
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
return|return
name|data
operator|.
name|fillSlice
argument_list|(
name|bytesRef
argument_list|,
name|address
argument_list|,
call|(
name|int
call|)
argument_list|(
name|addresses
operator|.
name|get
argument_list|(
name|docID
operator|+
literal|1
argument_list|)
operator|-
name|address
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|DirectVarStraightSource
specifier|public
specifier|final
specifier|static
class|class
name|DirectVarStraightSource
extends|extends
name|DirectSource
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|index
decl_stmt|;
DECL|method|DirectVarStraightSource
name|DirectVarStraightSource
parameter_list|(
name|IndexInput
name|data
parameter_list|,
name|IndexInput
name|index
parameter_list|,
name|Type
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|data
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|index
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|PackedInts
operator|.
name|getDirectReader
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|position
specifier|protected
name|int
name|position
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|offset
init|=
name|index
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|baseOffset
operator|+
name|offset
argument_list|)
expr_stmt|;
comment|// Safe to do 1+docID because we write sentinel at the end:
specifier|final
name|long
name|nextOffset
init|=
name|index
operator|.
name|get
argument_list|(
literal|1
operator|+
name|docID
argument_list|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|nextOffset
operator|-
name|offset
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

