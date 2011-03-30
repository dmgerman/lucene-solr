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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|BytesRefHash
operator|.
name|BytesStartArray
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
name|MaxBytesLengthExceededException
import|;
end_import

begin_class
DECL|class|TermsHashPerField
specifier|final
class|class
name|TermsHashPerField
extends|extends
name|InvertedDocConsumerPerField
block|{
DECL|field|HASH_INIT_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|HASH_INIT_SIZE
init|=
literal|4
decl_stmt|;
DECL|field|consumer
specifier|final
name|TermsHashConsumerPerField
name|consumer
decl_stmt|;
DECL|field|termsHash
specifier|final
name|TermsHash
name|termsHash
decl_stmt|;
DECL|field|nextPerField
specifier|final
name|TermsHashPerField
name|nextPerField
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|termAtt
name|TermToBytesRefAttribute
name|termAtt
decl_stmt|;
DECL|field|termBytesRef
name|BytesRef
name|termBytesRef
decl_stmt|;
comment|// Copied from our perThread
DECL|field|intPool
specifier|final
name|IntBlockPool
name|intPool
decl_stmt|;
DECL|field|bytePool
specifier|final
name|ByteBlockPool
name|bytePool
decl_stmt|;
DECL|field|termBytePool
specifier|final
name|ByteBlockPool
name|termBytePool
decl_stmt|;
DECL|field|streamCount
specifier|final
name|int
name|streamCount
decl_stmt|;
DECL|field|numPostingInt
specifier|final
name|int
name|numPostingInt
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|bytesHash
specifier|final
name|BytesRefHash
name|bytesHash
decl_stmt|;
DECL|field|postingsArray
name|ParallelPostingsArray
name|postingsArray
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|method|TermsHashPerField
specifier|public
name|TermsHashPerField
parameter_list|(
name|DocInverterPerField
name|docInverterPerField
parameter_list|,
specifier|final
name|TermsHash
name|termsHash
parameter_list|,
specifier|final
name|TermsHash
name|nextTermsHash
parameter_list|,
specifier|final
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|intPool
operator|=
name|termsHash
operator|.
name|intPool
expr_stmt|;
name|bytePool
operator|=
name|termsHash
operator|.
name|bytePool
expr_stmt|;
name|termBytePool
operator|=
name|termsHash
operator|.
name|termBytePool
expr_stmt|;
name|docState
operator|=
name|termsHash
operator|.
name|docState
expr_stmt|;
name|this
operator|.
name|termsHash
operator|=
name|termsHash
expr_stmt|;
name|bytesUsed
operator|=
name|termsHash
operator|.
name|trackAllocations
condition|?
name|termsHash
operator|.
name|docWriter
operator|.
name|bytesUsed
else|:
operator|new
name|AtomicLong
argument_list|()
expr_stmt|;
name|fieldState
operator|=
name|docInverterPerField
operator|.
name|fieldState
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|termsHash
operator|.
name|consumer
operator|.
name|addField
argument_list|(
name|this
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
name|PostingsBytesStartArray
name|byteStarts
init|=
operator|new
name|PostingsBytesStartArray
argument_list|(
name|this
argument_list|,
name|bytesUsed
argument_list|)
decl_stmt|;
name|bytesHash
operator|=
operator|new
name|BytesRefHash
argument_list|(
name|termBytePool
argument_list|,
name|HASH_INIT_SIZE
argument_list|,
name|byteStarts
argument_list|)
expr_stmt|;
name|streamCount
operator|=
name|consumer
operator|.
name|getStreamCount
argument_list|()
expr_stmt|;
name|numPostingInt
operator|=
literal|2
operator|*
name|streamCount
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
if|if
condition|(
name|nextTermsHash
operator|!=
literal|null
condition|)
name|nextPerField
operator|=
operator|(
name|TermsHashPerField
operator|)
name|nextTermsHash
operator|.
name|addField
argument_list|(
name|docInverterPerField
argument_list|,
name|fieldInfo
argument_list|)
expr_stmt|;
else|else
name|nextPerField
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|shrinkHash
name|void
name|shrinkHash
parameter_list|(
name|int
name|targetSize
parameter_list|)
block|{
comment|// Fully free the bytesHash on each flush but keep the pool untouched
comment|// bytesHash.clear will clear the ByteStartArray and in turn the ParallelPostingsArray too
name|bytesHash
operator|.
name|clear
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|bytesHash
operator|.
name|clear
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
name|nextPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
name|nextPerField
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
DECL|method|initReader
specifier|public
name|void
name|initReader
parameter_list|(
name|ByteSliceReader
name|reader
parameter_list|,
name|int
name|termID
parameter_list|,
name|int
name|stream
parameter_list|)
block|{
assert|assert
name|stream
operator|<
name|streamCount
assert|;
name|int
name|intStart
init|=
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|ints
init|=
name|intPool
operator|.
name|buffers
index|[
name|intStart
operator|>>
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_SHIFT
index|]
decl_stmt|;
specifier|final
name|int
name|upto
init|=
name|intStart
operator|&
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_MASK
decl_stmt|;
name|reader
operator|.
name|init
argument_list|(
name|bytePool
argument_list|,
name|postingsArray
operator|.
name|byteStarts
index|[
name|termID
index|]
operator|+
name|stream
operator|*
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|,
name|ints
index|[
name|upto
operator|+
name|stream
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** Collapse the hash table& sort in-place. */
DECL|method|sortPostings
specifier|public
name|int
index|[]
name|sortPostings
parameter_list|(
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|)
block|{
return|return
name|bytesHash
operator|.
name|sort
argument_list|(
name|termComp
argument_list|)
return|;
block|}
DECL|field|doCall
specifier|private
name|boolean
name|doCall
decl_stmt|;
DECL|field|doNextCall
specifier|private
name|boolean
name|doNextCall
decl_stmt|;
annotation|@
name|Override
DECL|method|start
name|void
name|start
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
name|termAtt
operator|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|getAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|termBytesRef
operator|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|(
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
block|{
name|nextPerField
operator|.
name|start
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|start
name|boolean
name|start
parameter_list|(
name|Fieldable
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|doCall
operator|=
name|consumer
operator|.
name|start
argument_list|(
name|fields
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|bytesHash
operator|.
name|reinit
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
name|doNextCall
operator|=
name|nextPerField
operator|.
name|start
argument_list|(
name|fields
argument_list|,
name|count
argument_list|)
expr_stmt|;
return|return
name|doCall
operator|||
name|doNextCall
return|;
block|}
comment|// Secondary entry point (for 2nd& subsequent TermsHash),
comment|// because token text has already been "interned" into
comment|// textStart, so we hash by textStart
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|textStart
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|termID
init|=
name|bytesHash
operator|.
name|addByPoolOffset
argument_list|(
name|textStart
argument_list|)
decl_stmt|;
if|if
condition|(
name|termID
operator|>=
literal|0
condition|)
block|{
comment|// New posting
comment|// First time we are seeing this token since we last
comment|// flushed the hash.
comment|// Init stream slices
if|if
condition|(
name|numPostingInt
operator|+
name|intPool
operator|.
name|intUpto
operator|>
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_SIZE
condition|)
name|intPool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
operator|-
name|bytePool
operator|.
name|byteUpto
operator|<
name|numPostingInt
operator|*
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
condition|)
name|bytePool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
name|intUptos
operator|=
name|intPool
operator|.
name|buffer
expr_stmt|;
name|intUptoStart
operator|=
name|intPool
operator|.
name|intUpto
expr_stmt|;
name|intPool
operator|.
name|intUpto
operator|+=
name|streamCount
expr_stmt|;
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
operator|=
name|intUptoStart
operator|+
name|intPool
operator|.
name|intOffset
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
name|streamCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|upto
init|=
name|bytePool
operator|.
name|newSlice
argument_list|(
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|)
decl_stmt|;
name|intUptos
index|[
name|intUptoStart
operator|+
name|i
index|]
operator|=
name|upto
operator|+
name|bytePool
operator|.
name|byteOffset
expr_stmt|;
block|}
name|postingsArray
operator|.
name|byteStarts
index|[
name|termID
index|]
operator|=
name|intUptos
index|[
name|intUptoStart
index|]
expr_stmt|;
name|consumer
operator|.
name|newTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termID
operator|=
operator|(
operator|-
name|termID
operator|)
operator|-
literal|1
expr_stmt|;
name|int
name|intStart
init|=
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
decl_stmt|;
name|intUptos
operator|=
name|intPool
operator|.
name|buffers
index|[
name|intStart
operator|>>
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_SHIFT
index|]
expr_stmt|;
name|intUptoStart
operator|=
name|intStart
operator|&
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_MASK
expr_stmt|;
name|consumer
operator|.
name|addTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Primary entry point (for first TermsHash)
annotation|@
name|Override
DECL|method|add
name|void
name|add
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We are first in the chain so we must "intern" the
comment|// term text into textStart address
comment|// Get the text& hash of this term.
name|int
name|termID
decl_stmt|;
try|try
block|{
name|termID
operator|=
name|bytesHash
operator|.
name|add
argument_list|(
name|termBytesRef
argument_list|,
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MaxBytesLengthExceededException
name|e
parameter_list|)
block|{
comment|// Not enough room in current block
comment|// Just skip this term, to remain as robust as
comment|// possible during indexing.  A TokenFilter
comment|// can be inserted into the analyzer chain if
comment|// other behavior is wanted (pruning the term
comment|// to a prefix, throwing an exception, etc).
if|if
condition|(
name|docState
operator|.
name|maxTermPrefix
operator|==
literal|null
condition|)
block|{
specifier|final
name|int
name|saved
init|=
name|termBytesRef
operator|.
name|length
decl_stmt|;
try|try
block|{
name|termBytesRef
operator|.
name|length
operator|=
name|Math
operator|.
name|min
argument_list|(
literal|30
argument_list|,
name|DocumentsWriterPerThread
operator|.
name|MAX_TERM_LENGTH_UTF8
argument_list|)
expr_stmt|;
name|docState
operator|.
name|maxTermPrefix
operator|=
name|termBytesRef
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|termBytesRef
operator|.
name|length
operator|=
name|saved
expr_stmt|;
block|}
block|}
name|consumer
operator|.
name|skippingLongTerm
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|termID
operator|>=
literal|0
condition|)
block|{
comment|// New posting
name|bytesHash
operator|.
name|byteStart
argument_list|(
name|termID
argument_list|)
expr_stmt|;
comment|// Init stream slices
if|if
condition|(
name|numPostingInt
operator|+
name|intPool
operator|.
name|intUpto
operator|>
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_SIZE
condition|)
block|{
name|intPool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
operator|-
name|bytePool
operator|.
name|byteUpto
operator|<
name|numPostingInt
operator|*
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
condition|)
block|{
name|bytePool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
name|intUptos
operator|=
name|intPool
operator|.
name|buffer
expr_stmt|;
name|intUptoStart
operator|=
name|intPool
operator|.
name|intUpto
expr_stmt|;
name|intPool
operator|.
name|intUpto
operator|+=
name|streamCount
expr_stmt|;
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
operator|=
name|intUptoStart
operator|+
name|intPool
operator|.
name|intOffset
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
name|streamCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|upto
init|=
name|bytePool
operator|.
name|newSlice
argument_list|(
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|)
decl_stmt|;
name|intUptos
index|[
name|intUptoStart
operator|+
name|i
index|]
operator|=
name|upto
operator|+
name|bytePool
operator|.
name|byteOffset
expr_stmt|;
block|}
name|postingsArray
operator|.
name|byteStarts
index|[
name|termID
index|]
operator|=
name|intUptos
index|[
name|intUptoStart
index|]
expr_stmt|;
name|consumer
operator|.
name|newTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termID
operator|=
operator|(
operator|-
name|termID
operator|)
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|intStart
init|=
name|postingsArray
operator|.
name|intStarts
index|[
name|termID
index|]
decl_stmt|;
name|intUptos
operator|=
name|intPool
operator|.
name|buffers
index|[
name|intStart
operator|>>
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_SHIFT
index|]
expr_stmt|;
name|intUptoStart
operator|=
name|intStart
operator|&
name|DocumentsWriterPerThread
operator|.
name|INT_BLOCK_MASK
expr_stmt|;
name|consumer
operator|.
name|addTerm
argument_list|(
name|termID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doNextCall
condition|)
name|nextPerField
operator|.
name|add
argument_list|(
name|postingsArray
operator|.
name|textStarts
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
block|}
DECL|field|intUptos
name|int
index|[]
name|intUptos
decl_stmt|;
DECL|field|intUptoStart
name|int
name|intUptoStart
decl_stmt|;
DECL|method|writeByte
name|void
name|writeByte
parameter_list|(
name|int
name|stream
parameter_list|,
name|byte
name|b
parameter_list|)
block|{
name|int
name|upto
init|=
name|intUptos
index|[
name|intUptoStart
operator|+
name|stream
index|]
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bytePool
operator|.
name|buffers
index|[
name|upto
operator|>>
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SHIFT
index|]
decl_stmt|;
assert|assert
name|bytes
operator|!=
literal|null
assert|;
name|int
name|offset
init|=
name|upto
operator|&
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_MASK
decl_stmt|;
if|if
condition|(
name|bytes
index|[
name|offset
index|]
operator|!=
literal|0
condition|)
block|{
comment|// End of slice; allocate a new one
name|offset
operator|=
name|bytePool
operator|.
name|allocSlice
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|bytes
operator|=
name|bytePool
operator|.
name|buffer
expr_stmt|;
name|intUptos
index|[
name|intUptoStart
operator|+
name|stream
index|]
operator|=
name|offset
operator|+
name|bytePool
operator|.
name|byteOffset
expr_stmt|;
block|}
name|bytes
index|[
name|offset
index|]
operator|=
name|b
expr_stmt|;
operator|(
name|intUptos
index|[
name|intUptoStart
operator|+
name|stream
index|]
operator|)
operator|++
expr_stmt|;
block|}
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|int
name|stream
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
comment|// TODO: optimize
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|len
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
name|end
condition|;
name|i
operator|++
control|)
name|writeByte
argument_list|(
name|stream
argument_list|,
name|b
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|writeVInt
name|void
name|writeVInt
parameter_list|(
name|int
name|stream
parameter_list|,
name|int
name|i
parameter_list|)
block|{
assert|assert
name|stream
operator|<
name|streamCount
assert|;
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7F
operator|)
operator|!=
literal|0
condition|)
block|{
name|writeByte
argument_list|(
name|stream
argument_list|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7f
operator|)
operator||
literal|0x80
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|writeByte
argument_list|(
name|stream
argument_list|,
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|consumer
operator|.
name|finish
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextPerField
operator|!=
literal|null
condition|)
name|nextPerField
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|class|PostingsBytesStartArray
specifier|private
specifier|static
specifier|final
class|class
name|PostingsBytesStartArray
extends|extends
name|BytesStartArray
block|{
DECL|field|perField
specifier|private
specifier|final
name|TermsHashPerField
name|perField
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|AtomicLong
name|bytesUsed
decl_stmt|;
DECL|method|PostingsBytesStartArray
specifier|private
name|PostingsBytesStartArray
parameter_list|(
name|TermsHashPerField
name|perField
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|perField
operator|=
name|perField
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
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
name|perField
operator|.
name|postingsArray
operator|==
literal|null
condition|)
block|{
name|perField
operator|.
name|postingsArray
operator|=
name|perField
operator|.
name|consumer
operator|.
name|createPostingsArray
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|perField
operator|.
name|postingsArray
operator|.
name|size
operator|*
name|perField
operator|.
name|postingsArray
operator|.
name|bytesPerPosting
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|perField
operator|.
name|postingsArray
operator|.
name|textStarts
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
name|ParallelPostingsArray
name|postingsArray
init|=
name|perField
operator|.
name|postingsArray
decl_stmt|;
specifier|final
name|int
name|oldSize
init|=
name|perField
operator|.
name|postingsArray
operator|.
name|size
decl_stmt|;
name|postingsArray
operator|=
name|perField
operator|.
name|postingsArray
operator|=
name|postingsArray
operator|.
name|grow
argument_list|()
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|postingsArray
operator|.
name|bytesPerPosting
argument_list|()
operator|*
operator|(
name|postingsArray
operator|.
name|size
operator|-
name|oldSize
operator|)
operator|)
argument_list|)
expr_stmt|;
return|return
name|postingsArray
operator|.
name|textStarts
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
name|perField
operator|.
name|postingsArray
operator|!=
literal|null
condition|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|perField
operator|.
name|postingsArray
operator|.
name|size
operator|*
name|perField
operator|.
name|postingsArray
operator|.
name|bytesPerPosting
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|perField
operator|.
name|postingsArray
operator|=
literal|null
expr_stmt|;
block|}
return|return
literal|null
return|;
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
block|}
block|}
end_class

end_unit

