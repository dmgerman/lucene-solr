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

begin_comment
comment|/**  * A {@link ByteBlockPool.Allocator} implementation that recycles unused byte  * blocks in a buffer and reuses them in subsequent calls to  * {@link #getByteBlock()}.  *<p>  * Note: This class is not thread-safe  *</p>  * @lucene.internal  */
end_comment

begin_class
DECL|class|RecyclingByteBlockAllocator
specifier|public
specifier|final
class|class
name|RecyclingByteBlockAllocator
extends|extends
name|ByteBlockPool
operator|.
name|Allocator
block|{
DECL|field|freeByteBlocks
specifier|private
name|byte
index|[]
index|[]
name|freeByteBlocks
decl_stmt|;
DECL|field|maxBufferedBlocks
specifier|private
specifier|final
name|int
name|maxBufferedBlocks
decl_stmt|;
DECL|field|freeBlocks
specifier|private
name|int
name|freeBlocks
init|=
literal|0
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|field|DEFAULT_BUFFERED_BLOCKS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFERED_BLOCKS
init|=
literal|64
decl_stmt|;
comment|/**    * Creates a new {@link RecyclingByteBlockAllocator}    *     * @param blockSize    *          the block size in bytes    * @param maxBufferedBlocks    *          maximum number of buffered byte block    * @param bytesUsed    *          {@link Counter} reference counting internally allocated bytes    */
DECL|method|RecyclingByteBlockAllocator
specifier|public
name|RecyclingByteBlockAllocator
parameter_list|(
name|int
name|blockSize
parameter_list|,
name|int
name|maxBufferedBlocks
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
name|freeByteBlocks
operator|=
operator|new
name|byte
index|[
name|maxBufferedBlocks
index|]
index|[]
expr_stmt|;
name|this
operator|.
name|maxBufferedBlocks
operator|=
name|maxBufferedBlocks
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
comment|/**    * Creates a new {@link RecyclingByteBlockAllocator}.    *     * @param blockSize    *          the block size in bytes    * @param maxBufferedBlocks    *          maximum number of buffered byte block    */
DECL|method|RecyclingByteBlockAllocator
specifier|public
name|RecyclingByteBlockAllocator
parameter_list|(
name|int
name|blockSize
parameter_list|,
name|int
name|maxBufferedBlocks
parameter_list|)
block|{
name|this
argument_list|(
name|blockSize
argument_list|,
name|maxBufferedBlocks
argument_list|,
name|Counter
operator|.
name|newCounter
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link RecyclingByteBlockAllocator} with a block size of    * {@link ByteBlockPool#BYTE_BLOCK_SIZE}, upper buffered docs limit of    * {@link #DEFAULT_BUFFERED_BLOCKS} ({@value #DEFAULT_BUFFERED_BLOCKS}).    *     */
DECL|method|RecyclingByteBlockAllocator
specifier|public
name|RecyclingByteBlockAllocator
parameter_list|()
block|{
name|this
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|,
literal|64
argument_list|,
name|Counter
operator|.
name|newCounter
argument_list|(
literal|false
argument_list|)
argument_list|)
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
if|if
condition|(
name|freeBlocks
operator|==
literal|0
condition|)
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
specifier|final
name|byte
index|[]
name|b
init|=
name|freeByteBlocks
index|[
operator|--
name|freeBlocks
index|]
decl_stmt|;
name|freeByteBlocks
index|[
name|freeBlocks
index|]
operator|=
literal|null
expr_stmt|;
return|return
name|b
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
specifier|final
name|int
name|numBlocks
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxBufferedBlocks
operator|-
name|freeBlocks
argument_list|,
name|end
operator|-
name|start
argument_list|)
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|freeBlocks
operator|+
name|numBlocks
decl_stmt|;
if|if
condition|(
name|size
operator|>=
name|freeByteBlocks
operator|.
name|length
condition|)
block|{
specifier|final
name|byte
index|[]
index|[]
name|newBlocks
init|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|size
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|freeByteBlocks
argument_list|,
literal|0
argument_list|,
name|newBlocks
argument_list|,
literal|0
argument_list|,
name|freeBlocks
argument_list|)
expr_stmt|;
name|freeByteBlocks
operator|=
name|newBlocks
expr_stmt|;
block|}
specifier|final
name|int
name|stop
init|=
name|start
operator|+
name|numBlocks
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|stop
condition|;
name|i
operator|++
control|)
block|{
name|freeByteBlocks
index|[
name|freeBlocks
operator|++
index|]
operator|=
name|blocks
index|[
name|i
index|]
expr_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|stop
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
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|end
operator|-
name|stop
operator|)
operator|*
name|blockSize
argument_list|)
expr_stmt|;
assert|assert
name|bytesUsed
operator|.
name|get
argument_list|()
operator|>=
literal|0
assert|;
block|}
comment|/**    * @return the number of currently buffered blocks    */
DECL|method|numBufferedBlocks
specifier|public
name|int
name|numBufferedBlocks
parameter_list|()
block|{
return|return
name|freeBlocks
return|;
block|}
comment|/**    * @return the number of bytes currently allocated by this {@link Allocator}    */
DECL|method|bytesUsed
specifier|public
name|long
name|bytesUsed
parameter_list|()
block|{
return|return
name|bytesUsed
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * @return the maximum number of buffered byte blocks    */
DECL|method|maxBufferedBlocks
specifier|public
name|int
name|maxBufferedBlocks
parameter_list|()
block|{
return|return
name|maxBufferedBlocks
return|;
block|}
comment|/**    * Removes the given number of byte blocks from the buffer if possible.    *     * @param num    *          the number of byte blocks to remove    * @return the number of actually removed buffers    */
DECL|method|freeBlocks
specifier|public
name|int
name|freeBlocks
parameter_list|(
name|int
name|num
parameter_list|)
block|{
assert|assert
name|num
operator|>=
literal|0
operator|:
literal|"free blocks must be>= 0 but was: "
operator|+
name|num
assert|;
specifier|final
name|int
name|stop
decl_stmt|;
specifier|final
name|int
name|count
decl_stmt|;
if|if
condition|(
name|num
operator|>
name|freeBlocks
condition|)
block|{
name|stop
operator|=
literal|0
expr_stmt|;
name|count
operator|=
name|freeBlocks
expr_stmt|;
block|}
else|else
block|{
name|stop
operator|=
name|freeBlocks
operator|-
name|num
expr_stmt|;
name|count
operator|=
name|num
expr_stmt|;
block|}
while|while
condition|(
name|freeBlocks
operator|>
name|stop
condition|)
block|{
name|freeByteBlocks
index|[
operator|--
name|freeBlocks
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
name|count
operator|*
name|blockSize
argument_list|)
expr_stmt|;
assert|assert
name|bytesUsed
operator|.
name|get
argument_list|()
operator|>=
literal|0
assert|;
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

