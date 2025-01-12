begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
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

begin_comment
comment|/**  * Write monotonically-increasing sequences of integers. This writer splits  * data into blocks and then for each block, computes the average slope, the  * minimum value and only encode the delta from the expected value using a  * {@link DirectWriter}.  *   * @see DirectMonotonicReader  * @lucene.internal   */
end_comment

begin_class
DECL|class|DirectMonotonicWriter
specifier|public
specifier|final
class|class
name|DirectMonotonicWriter
block|{
DECL|field|MIN_BLOCK_SHIFT
specifier|public
specifier|static
specifier|final
name|int
name|MIN_BLOCK_SHIFT
init|=
literal|2
decl_stmt|;
DECL|field|MAX_BLOCK_SHIFT
specifier|public
specifier|static
specifier|final
name|int
name|MAX_BLOCK_SHIFT
init|=
literal|22
decl_stmt|;
DECL|field|meta
specifier|final
name|IndexOutput
name|meta
decl_stmt|;
DECL|field|data
specifier|final
name|IndexOutput
name|data
decl_stmt|;
DECL|field|numValues
specifier|final
name|long
name|numValues
decl_stmt|;
DECL|field|baseDataPointer
specifier|final
name|long
name|baseDataPointer
decl_stmt|;
DECL|field|buffer
specifier|final
name|long
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferSize
name|int
name|bufferSize
decl_stmt|;
DECL|field|count
name|long
name|count
decl_stmt|;
DECL|field|finished
name|boolean
name|finished
decl_stmt|;
DECL|method|DirectMonotonicWriter
name|DirectMonotonicWriter
parameter_list|(
name|IndexOutput
name|metaOut
parameter_list|,
name|IndexOutput
name|dataOut
parameter_list|,
name|long
name|numValues
parameter_list|,
name|int
name|blockShift
parameter_list|)
block|{
name|this
operator|.
name|meta
operator|=
name|metaOut
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|dataOut
expr_stmt|;
name|this
operator|.
name|numValues
operator|=
name|numValues
expr_stmt|;
if|if
condition|(
name|blockShift
argument_list|<
literal|2
operator|||
name|blockShift
argument_list|>
literal|30
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"blockShift must be in [3-30], got "
operator|+
name|blockShift
argument_list|)
throw|;
block|}
specifier|final
name|int
name|blockSize
init|=
literal|1
operator|<<
name|blockShift
decl_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|new
name|long
index|[
name|blockSize
index|]
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|baseDataPointer
operator|=
name|dataOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
DECL|method|flush
specifier|private
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|bufferSize
operator|!=
literal|0
assert|;
specifier|final
name|float
name|avgInc
init|=
call|(
name|float
call|)
argument_list|(
call|(
name|double
call|)
argument_list|(
name|buffer
index|[
name|bufferSize
operator|-
literal|1
index|]
operator|-
name|buffer
index|[
literal|0
index|]
argument_list|)
operator|/
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|bufferSize
operator|-
literal|1
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
name|bufferSize
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|expected
init|=
call|(
name|long
call|)
argument_list|(
name|avgInc
operator|*
operator|(
name|long
operator|)
name|i
argument_list|)
decl_stmt|;
name|buffer
index|[
name|i
index|]
operator|-=
name|expected
expr_stmt|;
block|}
name|long
name|min
init|=
name|buffer
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|bufferSize
condition|;
operator|++
name|i
control|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|,
name|min
argument_list|)
expr_stmt|;
block|}
name|long
name|maxDelta
init|=
literal|0
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
name|bufferSize
condition|;
operator|++
name|i
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|-=
name|min
expr_stmt|;
comment|// use | will change nothing when it comes to computing required bits
comment|// but has the benefit of working fine with negative values too
comment|// (in case of overflow)
name|maxDelta
operator||=
name|buffer
index|[
name|i
index|]
expr_stmt|;
block|}
name|meta
operator|.
name|writeLong
argument_list|(
name|min
argument_list|)
expr_stmt|;
name|meta
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|avgInc
argument_list|)
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
name|baseDataPointer
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxDelta
operator|==
literal|0
condition|)
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
block|}
else|else
block|{
specifier|final
name|int
name|bitsRequired
init|=
name|DirectWriter
operator|.
name|unsignedBitsRequired
argument_list|(
name|maxDelta
argument_list|)
decl_stmt|;
name|DirectWriter
name|writer
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|data
argument_list|,
name|bufferSize
argument_list|,
name|bitsRequired
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
name|bufferSize
condition|;
operator|++
name|i
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|buffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|meta
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bitsRequired
argument_list|)
expr_stmt|;
block|}
name|bufferSize
operator|=
literal|0
expr_stmt|;
block|}
DECL|field|previous
name|long
name|previous
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
comment|/** Write a new value. Note that data might not make it to storage until    * {@link #finish()} is called.    *  @throws IllegalArgumentException if values don't come in order */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|v
operator|<
name|previous
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Values do not come in order: "
operator|+
name|previous
operator|+
literal|", "
operator|+
name|v
argument_list|)
throw|;
block|}
if|if
condition|(
name|bufferSize
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|buffer
index|[
name|bufferSize
operator|++
index|]
operator|=
name|v
expr_stmt|;
name|previous
operator|=
name|v
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|/** This must be called exactly once after all values have been {@link #add(long) added}. */
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|!=
name|numValues
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Wrong number of values added, expected: "
operator|+
name|numValues
operator|+
literal|", got: "
operator|+
name|count
argument_list|)
throw|;
block|}
if|if
condition|(
name|finished
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"#finish has been called already"
argument_list|)
throw|;
block|}
if|if
condition|(
name|bufferSize
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
name|finished
operator|=
literal|true
expr_stmt|;
block|}
comment|/** Returns an instance suitable for encoding {@code numValues} into monotonic    *  blocks of 2<sup>{@code blockShift}</sup> values. Metadata will be written    *  to {@code metaOut} and actual data to {@code dataOut}. */
DECL|method|getInstance
specifier|public
specifier|static
name|DirectMonotonicWriter
name|getInstance
parameter_list|(
name|IndexOutput
name|metaOut
parameter_list|,
name|IndexOutput
name|dataOut
parameter_list|,
name|long
name|numValues
parameter_list|,
name|int
name|blockShift
parameter_list|)
block|{
return|return
operator|new
name|DirectMonotonicWriter
argument_list|(
name|metaOut
argument_list|,
name|dataOut
argument_list|,
name|numValues
argument_list|,
name|blockShift
argument_list|)
return|;
block|}
block|}
end_class

end_unit

