begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
import|;
end_import

begin_comment
comment|/**   * Class that Posting and PostingVector use to write byte  * streams into shared fixed-size byte[] arrays.  The idea  * is to allocate slices of increasing lengths For  * example, the first slice is 5 bytes, the next slice is  * 14, etc.  We start by writing our bytes into the first  * 5 bytes.  When we hit the end of the slice, we allocate  * the next slice and then write the address of the new  * slice into the last 4 bytes of the previous slice (the  * "forwarding address").  *  * Each slice is filled with 0's initially, and we mark  * the end with a non-zero byte.  This way the methods  * that are writing into the slice don't need to record  * its length and instead allocate a new slice once they  * hit a non-zero byte.   *   * @lucene.internal  **/
end_comment

begin_class
DECL|class|ByteBlockPool
specifier|public
specifier|final
class|class
name|ByteBlockPool
block|{
DECL|field|BYTE_BLOCK_SHIFT
specifier|public
specifier|final
specifier|static
name|int
name|BYTE_BLOCK_SHIFT
init|=
literal|15
decl_stmt|;
DECL|field|BYTE_BLOCK_SIZE
specifier|public
specifier|final
specifier|static
name|int
name|BYTE_BLOCK_SIZE
init|=
literal|1
operator|<<
name|BYTE_BLOCK_SHIFT
decl_stmt|;
DECL|field|BYTE_BLOCK_MASK
specifier|public
specifier|final
specifier|static
name|int
name|BYTE_BLOCK_MASK
init|=
name|BYTE_BLOCK_SIZE
operator|-
literal|1
decl_stmt|;
comment|/** Abstract class for allocating and freeing byte    *  blocks. */
DECL|class|Allocator
specifier|public
specifier|abstract
specifier|static
class|class
name|Allocator
block|{
DECL|field|blockSize
specifier|protected
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|Allocator
specifier|public
name|Allocator
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|blockSize
operator|=
name|blockSize
expr_stmt|;
block|}
DECL|method|recycleByteBlocks
specifier|public
specifier|abstract
name|void
name|recycleByteBlocks
parameter_list|(
name|byte
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
function_decl|;
DECL|method|recycleByteBlocks
specifier|public
name|void
name|recycleByteBlocks
parameter_list|(
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|blocks
parameter_list|)
block|{
specifier|final
name|byte
index|[]
index|[]
name|b
init|=
name|blocks
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|blocks
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
decl_stmt|;
name|recycleByteBlocks
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|getByteBlock
specifier|public
name|byte
index|[]
name|getByteBlock
parameter_list|()
block|{
return|return
operator|new
name|byte
index|[
name|blockSize
index|]
return|;
block|}
block|}
comment|/** A simple {@link Allocator} that never recycles. */
DECL|class|DirectAllocator
specifier|public
specifier|static
specifier|final
class|class
name|DirectAllocator
extends|extends
name|Allocator
block|{
DECL|method|DirectAllocator
specifier|public
name|DirectAllocator
parameter_list|()
block|{
name|this
argument_list|(
name|BYTE_BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|DirectAllocator
specifier|public
name|DirectAllocator
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|super
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recycleByteBlocks
specifier|public
name|void
name|recycleByteBlocks
parameter_list|(
name|byte
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{     }
block|}
comment|/** A simple {@link Allocator} that never recycles, but    *  tracks how much total RAM is in use. */
DECL|class|DirectTrackingAllocator
specifier|public
specifier|static
class|class
name|DirectTrackingAllocator
extends|extends
name|Allocator
block|{
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|method|DirectTrackingAllocator
specifier|public
name|DirectTrackingAllocator
parameter_list|(
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|this
argument_list|(
name|BYTE_BLOCK_SIZE
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|DirectTrackingAllocator
specifier|public
name|DirectTrackingAllocator
parameter_list|(
name|int
name|blockSize
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|super
argument_list|(
name|blockSize
argument_list|)
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
DECL|method|getByteBlock
specifier|public
name|byte
index|[]
name|getByteBlock
parameter_list|()
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
return|return
operator|new
name|byte
index|[
name|blockSize
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|recycleByteBlocks
specifier|public
name|void
name|recycleByteBlocks
parameter_list|(
name|byte
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|*
name|blockSize
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
empty_stmt|;
comment|/**    * array of buffers currently used in the pool. Buffers are allocated if    * needed don't modify this outside of this class.    */
DECL|field|buffers
specifier|public
name|byte
index|[]
index|[]
name|buffers
init|=
operator|new
name|byte
index|[
literal|10
index|]
index|[]
decl_stmt|;
comment|/** index into the buffers array pointing to the current buffer used as the head */
DECL|field|bufferUpto
specifier|private
name|int
name|bufferUpto
init|=
operator|-
literal|1
decl_stmt|;
comment|// Which buffer we are upto
comment|/** Where we are in head buffer */
DECL|field|byteUpto
specifier|public
name|int
name|byteUpto
init|=
name|BYTE_BLOCK_SIZE
decl_stmt|;
comment|/** Current head buffer */
DECL|field|buffer
specifier|public
name|byte
index|[]
name|buffer
decl_stmt|;
comment|/** Current head offset */
DECL|field|byteOffset
specifier|public
name|int
name|byteOffset
init|=
operator|-
name|BYTE_BLOCK_SIZE
decl_stmt|;
DECL|field|allocator
specifier|private
specifier|final
name|Allocator
name|allocator
decl_stmt|;
DECL|method|ByteBlockPool
specifier|public
name|ByteBlockPool
parameter_list|(
name|Allocator
name|allocator
parameter_list|)
block|{
name|this
operator|.
name|allocator
operator|=
name|allocator
expr_stmt|;
block|}
comment|/**    * Resets the pool to its initial state reusing the first buffer and fills all    * buffers with<tt>0</tt> bytes before they reused or passed to    * {@link Allocator#recycleByteBlocks(byte[][], int, int)}. Calling    * {@link ByteBlockPool#nextBuffer()} is not needed after reset.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|reset
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: Resets the pool to its initial state reusing the first buffer. Calling    * {@link ByteBlockPool#nextBuffer()} is not needed after reset.     * @param zeroFillBuffers if<code>true</code> the buffers are filled with<tt>0</tt>.     *        This should be set to<code>true</code> if this pool is used with slices.    * @param reuseFirst if<code>true</code> the first buffer will be reused and calling    *        {@link ByteBlockPool#nextBuffer()} is not needed after reset iff the     *        block pool was used before ie. {@link ByteBlockPool#nextBuffer()} was called before.    */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|boolean
name|zeroFillBuffers
parameter_list|,
name|boolean
name|reuseFirst
parameter_list|)
block|{
if|if
condition|(
name|bufferUpto
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// We allocated at least one buffer
if|if
condition|(
name|zeroFillBuffers
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bufferUpto
condition|;
name|i
operator|++
control|)
block|{
comment|// Fully zero fill buffers that we fully used
name|Arrays
operator|.
name|fill
argument_list|(
name|buffers
index|[
name|i
index|]
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// Partial zero fill the final buffer
name|Arrays
operator|.
name|fill
argument_list|(
name|buffers
index|[
name|bufferUpto
index|]
argument_list|,
literal|0
argument_list|,
name|byteUpto
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bufferUpto
operator|>
literal|0
operator|||
operator|!
name|reuseFirst
condition|)
block|{
specifier|final
name|int
name|offset
init|=
name|reuseFirst
condition|?
literal|1
else|:
literal|0
decl_stmt|;
comment|// Recycle all but the first buffer
name|allocator
operator|.
name|recycleByteBlocks
argument_list|(
name|buffers
argument_list|,
name|offset
argument_list|,
literal|1
operator|+
name|bufferUpto
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|buffers
argument_list|,
name|offset
argument_list|,
literal|1
operator|+
name|bufferUpto
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reuseFirst
condition|)
block|{
comment|// Re-use the first buffer
name|bufferUpto
operator|=
literal|0
expr_stmt|;
name|byteUpto
operator|=
literal|0
expr_stmt|;
name|byteOffset
operator|=
literal|0
expr_stmt|;
name|buffer
operator|=
name|buffers
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|bufferUpto
operator|=
operator|-
literal|1
expr_stmt|;
name|byteUpto
operator|=
name|BYTE_BLOCK_SIZE
expr_stmt|;
name|byteOffset
operator|=
operator|-
name|BYTE_BLOCK_SIZE
expr_stmt|;
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Advances the pool to its next buffer. This method should be called once    * after the constructor to initialize the pool. In contrast to the    * constructor a {@link ByteBlockPool#reset()} call will advance the pool to    * its first buffer immediately.    */
DECL|method|nextBuffer
specifier|public
name|void
name|nextBuffer
parameter_list|()
block|{
if|if
condition|(
literal|1
operator|+
name|bufferUpto
operator|==
name|buffers
operator|.
name|length
condition|)
block|{
name|byte
index|[]
index|[]
name|newBuffers
init|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|buffers
operator|.
name|length
operator|+
literal|1
argument_list|,
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffers
argument_list|,
literal|0
argument_list|,
name|newBuffers
argument_list|,
literal|0
argument_list|,
name|buffers
operator|.
name|length
argument_list|)
expr_stmt|;
name|buffers
operator|=
name|newBuffers
expr_stmt|;
block|}
name|buffer
operator|=
name|buffers
index|[
literal|1
operator|+
name|bufferUpto
index|]
operator|=
name|allocator
operator|.
name|getByteBlock
argument_list|()
expr_stmt|;
name|bufferUpto
operator|++
expr_stmt|;
name|byteUpto
operator|=
literal|0
expr_stmt|;
name|byteOffset
operator|+=
name|BYTE_BLOCK_SIZE
expr_stmt|;
block|}
comment|/**    * Allocates a new slice with the given size.     * @see ByteBlockPool#FIRST_LEVEL_SIZE    */
DECL|method|newSlice
specifier|public
name|int
name|newSlice
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|byteUpto
operator|>
name|BYTE_BLOCK_SIZE
operator|-
name|size
condition|)
name|nextBuffer
argument_list|()
expr_stmt|;
specifier|final
name|int
name|upto
init|=
name|byteUpto
decl_stmt|;
name|byteUpto
operator|+=
name|size
expr_stmt|;
name|buffer
index|[
name|byteUpto
operator|-
literal|1
index|]
operator|=
literal|16
expr_stmt|;
return|return
name|upto
return|;
block|}
comment|// Size of each slice.  These arrays should be at most 16
comment|// elements (index is encoded with 4 bits).  First array
comment|// is just a compact way to encode X+1 with a max.  Second
comment|// array is the length of each slice, ie first slice is 5
comment|// bytes, next slice is 14 bytes, etc.
comment|/**    * An array holding the offset into the {@link ByteBlockPool#LEVEL_SIZE_ARRAY}    * to quickly navigate to the next slice level.    */
DECL|field|NEXT_LEVEL_ARRAY
specifier|public
specifier|final
specifier|static
name|int
index|[]
name|NEXT_LEVEL_ARRAY
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|9
block|}
decl_stmt|;
comment|/**    * An array holding the level sizes for byte slices.    */
DECL|field|LEVEL_SIZE_ARRAY
specifier|public
specifier|final
specifier|static
name|int
index|[]
name|LEVEL_SIZE_ARRAY
init|=
block|{
literal|5
block|,
literal|14
block|,
literal|20
block|,
literal|30
block|,
literal|40
block|,
literal|40
block|,
literal|80
block|,
literal|80
block|,
literal|120
block|,
literal|200
block|}
decl_stmt|;
comment|/**    * The first level size for new slices    * @see ByteBlockPool#newSlice(int)    */
DECL|field|FIRST_LEVEL_SIZE
specifier|public
specifier|final
specifier|static
name|int
name|FIRST_LEVEL_SIZE
init|=
name|LEVEL_SIZE_ARRAY
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Creates a new byte slice with the given starting size and     * returns the slices offset in the pool.    */
DECL|method|allocSlice
specifier|public
name|int
name|allocSlice
parameter_list|(
specifier|final
name|byte
index|[]
name|slice
parameter_list|,
specifier|final
name|int
name|upto
parameter_list|)
block|{
specifier|final
name|int
name|level
init|=
name|slice
index|[
name|upto
index|]
operator|&
literal|15
decl_stmt|;
specifier|final
name|int
name|newLevel
init|=
name|NEXT_LEVEL_ARRAY
index|[
name|level
index|]
decl_stmt|;
specifier|final
name|int
name|newSize
init|=
name|LEVEL_SIZE_ARRAY
index|[
name|newLevel
index|]
decl_stmt|;
comment|// Maybe allocate another block
if|if
condition|(
name|byteUpto
operator|>
name|BYTE_BLOCK_SIZE
operator|-
name|newSize
condition|)
block|{
name|nextBuffer
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|newUpto
init|=
name|byteUpto
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|newUpto
operator|+
name|byteOffset
decl_stmt|;
name|byteUpto
operator|+=
name|newSize
expr_stmt|;
comment|// Copy forward the past 3 bytes (which we are about
comment|// to overwrite with the forwarding address):
name|buffer
index|[
name|newUpto
index|]
operator|=
name|slice
index|[
name|upto
operator|-
literal|3
index|]
expr_stmt|;
name|buffer
index|[
name|newUpto
operator|+
literal|1
index|]
operator|=
name|slice
index|[
name|upto
operator|-
literal|2
index|]
expr_stmt|;
name|buffer
index|[
name|newUpto
operator|+
literal|2
index|]
operator|=
name|slice
index|[
name|upto
operator|-
literal|1
index|]
expr_stmt|;
comment|// Write forwarding address at end of last slice:
name|slice
index|[
name|upto
operator|-
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|slice
index|[
name|upto
operator|-
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|slice
index|[
name|upto
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|offset
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|slice
index|[
name|upto
index|]
operator|=
operator|(
name|byte
operator|)
name|offset
expr_stmt|;
comment|// Write new level:
name|buffer
index|[
name|byteUpto
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|16
operator||
name|newLevel
argument_list|)
expr_stmt|;
return|return
name|newUpto
operator|+
literal|3
return|;
block|}
comment|/** Fill the provided {@link BytesRef} with the bytes at the specified offset/length slice.    *  This will avoid copying the bytes, if the slice fits into a single block; otherwise, it uses    *  the provided {@linkl BytesRefBuilder} to copy bytes over. */
DECL|method|setBytesRef
name|void
name|setBytesRef
parameter_list|(
name|BytesRefBuilder
name|builder
parameter_list|,
name|BytesRef
name|result
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|result
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|int
name|bufferIndex
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|>>
name|BYTE_BLOCK_SHIFT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|buffers
index|[
name|bufferIndex
index|]
decl_stmt|;
name|int
name|pos
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|&
name|BYTE_BLOCK_MASK
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|+
name|length
operator|<=
name|BYTE_BLOCK_SIZE
condition|)
block|{
comment|// common case where the slice lives in a single block: just reference the buffer directly without copying
name|result
operator|.
name|bytes
operator|=
name|buffer
expr_stmt|;
name|result
operator|.
name|offset
operator|=
name|pos
expr_stmt|;
block|}
else|else
block|{
comment|// uncommon case: the slice spans at least 2 blocks, so we must copy the bytes:
name|builder
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
name|builder
operator|.
name|get
argument_list|()
operator|.
name|bytes
expr_stmt|;
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|readBytes
argument_list|(
name|offset
argument_list|,
name|result
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Fill in a BytesRef from term's length& bytes encoded in
comment|// byte block
DECL|method|setBytesRef
specifier|public
name|void
name|setBytesRef
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|textStart
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
name|term
operator|.
name|bytes
operator|=
name|buffers
index|[
name|textStart
operator|>>
name|BYTE_BLOCK_SHIFT
index|]
decl_stmt|;
name|int
name|pos
init|=
name|textStart
operator|&
name|BYTE_BLOCK_MASK
decl_stmt|;
if|if
condition|(
operator|(
name|bytes
index|[
name|pos
index|]
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
comment|// length is 1 byte
name|term
operator|.
name|length
operator|=
name|bytes
index|[
name|pos
index|]
expr_stmt|;
name|term
operator|.
name|offset
operator|=
name|pos
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// length is 2 bytes
name|term
operator|.
name|length
operator|=
operator|(
name|bytes
index|[
name|pos
index|]
operator|&
literal|0x7f
operator|)
operator|+
operator|(
operator|(
name|bytes
index|[
name|pos
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|7
operator|)
expr_stmt|;
name|term
operator|.
name|offset
operator|=
name|pos
operator|+
literal|2
expr_stmt|;
block|}
assert|assert
name|term
operator|.
name|length
operator|>=
literal|0
assert|;
block|}
comment|/**    * Appends the bytes in the provided {@link BytesRef} at    * the current position.    */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|BytesRef
name|bytes
parameter_list|)
block|{
name|int
name|length
init|=
name|bytes
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|offset
init|=
name|bytes
operator|.
name|offset
decl_stmt|;
name|int
name|overflow
init|=
operator|(
name|length
operator|+
name|byteUpto
operator|)
operator|-
name|BYTE_BLOCK_SIZE
decl_stmt|;
do|do
block|{
if|if
condition|(
name|overflow
operator|<=
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|byteUpto
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|byteUpto
operator|+=
name|length
expr_stmt|;
break|break;
block|}
else|else
block|{
specifier|final
name|int
name|bytesToCopy
init|=
name|length
operator|-
name|overflow
decl_stmt|;
if|if
condition|(
name|bytesToCopy
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|byteUpto
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|bytesToCopy
expr_stmt|;
name|length
operator|-=
name|bytesToCopy
expr_stmt|;
block|}
name|nextBuffer
argument_list|()
expr_stmt|;
name|overflow
operator|=
name|overflow
operator|-
name|BYTE_BLOCK_SIZE
expr_stmt|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
comment|/**    * Reads bytes bytes out of the pool starting at the given offset with the given      * length into the given byte array at offset<tt>off</tt>.    *<p>Note: this method allows to copy across block boundaries.</p>    */
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|byte
name|bytes
index|[]
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|int
name|bytesOffset
init|=
name|off
decl_stmt|;
name|int
name|bytesLength
init|=
name|length
decl_stmt|;
name|int
name|bufferIndex
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|>>
name|BYTE_BLOCK_SHIFT
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
name|buffers
index|[
name|bufferIndex
index|]
decl_stmt|;
name|int
name|pos
init|=
call|(
name|int
call|)
argument_list|(
name|offset
operator|&
name|BYTE_BLOCK_MASK
argument_list|)
decl_stmt|;
name|int
name|overflow
init|=
operator|(
name|pos
operator|+
name|length
operator|)
operator|-
name|BYTE_BLOCK_SIZE
decl_stmt|;
do|do
block|{
if|if
condition|(
name|overflow
operator|<=
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|bytes
argument_list|,
name|bytesOffset
argument_list|,
name|bytesLength
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
specifier|final
name|int
name|bytesToCopy
init|=
name|length
operator|-
name|overflow
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|pos
argument_list|,
name|bytes
argument_list|,
name|bytesOffset
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
name|bytesLength
operator|-=
name|bytesToCopy
expr_stmt|;
name|bytesOffset
operator|+=
name|bytesToCopy
expr_stmt|;
name|buffer
operator|=
name|buffers
index|[
operator|++
name|bufferIndex
index|]
expr_stmt|;
name|overflow
operator|=
name|overflow
operator|-
name|BYTE_BLOCK_SIZE
expr_stmt|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
block|}
end_class

end_unit

