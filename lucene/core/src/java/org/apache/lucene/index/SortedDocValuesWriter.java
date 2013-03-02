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
import|import static
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
name|BYTE_BLOCK_SIZE
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
operator|.
name|DirectBytesStartArray
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
name|packed
operator|.
name|AppendingLongBuffer
import|;
end_import

begin_comment
comment|/** Buffers up pending byte[] per doc, deref and sorting via  *  int ord, then flushes when segment flushes. */
end_comment

begin_class
DECL|class|SortedDocValuesWriter
class|class
name|SortedDocValuesWriter
extends|extends
name|DocValuesWriter
block|{
DECL|field|hash
specifier|final
name|BytesRefHash
name|hash
decl_stmt|;
DECL|field|pending
specifier|private
name|AppendingLongBuffer
name|pending
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
comment|// this currently only tracks differences in 'pending'
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|BytesRef
name|EMPTY
init|=
operator|new
name|BytesRef
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|)
decl_stmt|;
DECL|method|SortedDocValuesWriter
specifier|public
name|SortedDocValuesWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|iwBytesUsed
operator|=
name|iwBytesUsed
expr_stmt|;
name|hash
operator|=
operator|new
name|BytesRefHash
argument_list|(
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
argument_list|(
name|iwBytesUsed
argument_list|)
argument_list|)
argument_list|,
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
operator|new
name|DirectBytesStartArray
argument_list|(
name|BytesRefHash
operator|.
name|DEFAULT_CAPACITY
argument_list|,
name|iwBytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|pending
operator|=
operator|new
name|AppendingLongBuffer
argument_list|()
expr_stmt|;
name|bytesUsed
operator|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<
name|pending
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" appears more than once in this document (only one value is allowed per field)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\": null value not allowed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|.
name|length
operator|>
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" is too large, must be<= "
operator|+
operator|(
name|BYTE_BLOCK_SIZE
operator|-
literal|2
operator|)
argument_list|)
throw|;
block|}
comment|// Fill in any holes:
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|<
name|docID
condition|)
block|{
name|addOneValue
argument_list|(
name|EMPTY
argument_list|)
expr_stmt|;
block|}
name|addOneValue
argument_list|(
name|value
argument_list|)
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
name|maxDoc
parameter_list|)
block|{
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|<
name|maxDoc
condition|)
block|{
name|addOneValue
argument_list|(
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addOneValue
specifier|private
name|void
name|addOneValue
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|int
name|termID
init|=
name|hash
operator|.
name|add
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|termID
operator|<
literal|0
condition|)
block|{
name|termID
operator|=
operator|-
name|termID
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// reserve additional space for each unique value:
comment|// 1. when indexing, when hash is 50% full, rehash() suddenly needs 2*size ints.
comment|//    TODO: can this same OOM happen in THPF?
comment|// 2. when flushing, we need 1 int per value (slot in the ordMap).
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
name|pending
operator|.
name|add
argument_list|(
name|termID
argument_list|)
expr_stmt|;
name|updateBytesUsed
argument_list|()
expr_stmt|;
block|}
DECL|method|updateBytesUsed
specifier|private
name|void
name|updateBytesUsed
parameter_list|()
block|{
specifier|final
name|long
name|newBytesUsed
init|=
name|pending
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|newBytesUsed
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|newBytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|DocValuesConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
decl_stmt|;
assert|assert
name|pending
operator|.
name|size
argument_list|()
operator|==
name|maxDoc
assert|;
specifier|final
name|int
name|valueCount
init|=
name|hash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|sortedValues
init|=
name|hash
operator|.
name|sort
argument_list|(
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|ordMap
init|=
operator|new
name|int
index|[
name|valueCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|valueCount
condition|;
name|ord
operator|++
control|)
block|{
name|ordMap
index|[
name|sortedValues
index|[
name|ord
index|]
index|]
operator|=
name|ord
expr_stmt|;
block|}
name|dvConsumer
operator|.
name|addSortedField
argument_list|(
name|fieldInfo
argument_list|,
comment|// ord -> value
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ValuesIterator
argument_list|(
name|sortedValues
argument_list|,
name|valueCount
argument_list|)
return|;
block|}
block|}
argument_list|,
comment|// doc -> ord
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
return|return
operator|new
name|OrdsIterator
argument_list|(
name|ordMap
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{   }
comment|// iterates over the unique values we have in ram
DECL|class|ValuesIterator
specifier|private
class|class
name|ValuesIterator
implements|implements
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|field|sortedValues
specifier|final
name|int
name|sortedValues
index|[]
decl_stmt|;
DECL|field|scratch
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|valueCount
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|field|ordUpto
name|int
name|ordUpto
decl_stmt|;
DECL|method|ValuesIterator
name|ValuesIterator
parameter_list|(
name|int
name|sortedValues
index|[]
parameter_list|,
name|int
name|valueCount
parameter_list|)
block|{
name|this
operator|.
name|sortedValues
operator|=
name|sortedValues
expr_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|ordUpto
operator|<
name|valueCount
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|hash
operator|.
name|get
argument_list|(
name|sortedValues
index|[
name|ordUpto
index|]
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|ordUpto
operator|++
expr_stmt|;
return|return
name|scratch
return|;
block|}
annotation|@
name|Override
DECL|method|remove
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
comment|// iterates over the ords for each doc we have in ram
DECL|class|OrdsIterator
specifier|private
class|class
name|OrdsIterator
implements|implements
name|Iterator
argument_list|<
name|Number
argument_list|>
block|{
DECL|field|iter
specifier|final
name|AppendingLongBuffer
operator|.
name|Iterator
name|iter
init|=
name|pending
operator|.
name|iterator
argument_list|()
decl_stmt|;
DECL|field|ordMap
specifier|final
name|int
name|ordMap
index|[]
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|docUpto
name|int
name|docUpto
decl_stmt|;
DECL|method|OrdsIterator
name|OrdsIterator
parameter_list|(
name|int
name|ordMap
index|[]
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|ordMap
operator|=
name|ordMap
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
assert|assert
name|pending
operator|.
name|size
argument_list|()
operator|==
name|maxDoc
assert|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|docUpto
operator|<
name|maxDoc
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|int
name|ord
init|=
operator|(
name|int
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|docUpto
operator|++
expr_stmt|;
comment|// TODO: make reusable Number
return|return
name|ordMap
index|[
name|ord
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|remove
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
block|}
end_class

end_unit

