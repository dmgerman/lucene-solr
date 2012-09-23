begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.block
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|block
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
name|Arrays
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
name|CorruptIndexException
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
name|DataInput
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
name|packed
operator|.
name|PackedInts
operator|.
name|Decoder
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
name|FormatAndBits
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|block
operator|.
name|BlockPostingsFormat
operator|.
name|BLOCK_SIZE
import|;
end_import

begin_comment
comment|/**  * Encode all values in normal area with fixed bit width,   * which is determined by the max value in this block.  */
end_comment

begin_class
DECL|class|ForUtil
specifier|final
class|class
name|ForUtil
block|{
comment|/**    * Special number of bits per value used whenever all values to encode are equal.    */
DECL|field|ALL_VALUES_EQUAL
specifier|private
specifier|static
specifier|final
name|int
name|ALL_VALUES_EQUAL
init|=
literal|0
decl_stmt|;
comment|/**    * Upper limit of the number of bytes that might be required to stored    *<code>BLOCK_SIZE</code> encoded values.    */
DECL|field|MAX_ENCODED_SIZE
specifier|static
specifier|final
name|int
name|MAX_ENCODED_SIZE
init|=
name|BLOCK_SIZE
operator|*
literal|4
decl_stmt|;
comment|/**    * Upper limit of the number of values that might be decoded in a single call to    * {@link #readBlock(IndexInput, byte[], int[])}. Although values after    *<code>BLOCK_SIZE</code> are garbage, it is necessary to allocate value buffers    * whose size is>= MAX_DATA_SIZE to avoid {@link ArrayIndexOutOfBoundsException}s.    */
DECL|field|MAX_DATA_SIZE
specifier|static
specifier|final
name|int
name|MAX_DATA_SIZE
decl_stmt|;
static|static
block|{
name|int
name|maxDataSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|version
init|=
name|PackedInts
operator|.
name|VERSION_START
init|;
name|version
operator|<=
name|PackedInts
operator|.
name|VERSION_CURRENT
condition|;
name|version
operator|++
control|)
block|{
for|for
control|(
name|PackedInts
operator|.
name|Format
name|format
range|:
name|PackedInts
operator|.
name|Format
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|bpv
init|=
literal|1
init|;
name|bpv
operator|<=
literal|32
condition|;
operator|++
name|bpv
control|)
block|{
if|if
condition|(
operator|!
name|format
operator|.
name|isSupported
argument_list|(
name|bpv
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|PackedInts
operator|.
name|Decoder
name|decoder
init|=
name|PackedInts
operator|.
name|getDecoder
argument_list|(
name|format
argument_list|,
name|version
argument_list|,
name|bpv
argument_list|)
decl_stmt|;
specifier|final
name|int
name|iterations
init|=
name|computeIterations
argument_list|(
name|decoder
argument_list|)
decl_stmt|;
name|maxDataSize
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxDataSize
argument_list|,
name|iterations
operator|*
name|decoder
operator|.
name|valueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|MAX_DATA_SIZE
operator|=
name|maxDataSize
expr_stmt|;
block|}
comment|/**    * Compute the number of iterations required to decode<code>BLOCK_SIZE</code>    * values with the provided {@link Decoder}.    */
DECL|method|computeIterations
specifier|private
specifier|static
name|int
name|computeIterations
parameter_list|(
name|PackedInts
operator|.
name|Decoder
name|decoder
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|float
operator|)
name|BLOCK_SIZE
operator|/
name|decoder
operator|.
name|valueCount
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Compute the number of bytes required to encode a block of values that require    *<code>bitsPerValue</code> bits per value with format<code>format</code>.    */
DECL|method|encodedSize
specifier|private
specifier|static
name|int
name|encodedSize
parameter_list|(
name|PackedInts
operator|.
name|Format
name|format
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
return|return
name|format
operator|.
name|nblocks
argument_list|(
name|bitsPerValue
argument_list|,
name|BLOCK_SIZE
argument_list|)
operator|<<
literal|3
return|;
block|}
DECL|field|encodedSizes
specifier|private
specifier|final
name|int
index|[]
name|encodedSizes
decl_stmt|;
DECL|field|encoders
specifier|private
specifier|final
name|PackedInts
operator|.
name|Encoder
index|[]
name|encoders
decl_stmt|;
DECL|field|decoders
specifier|private
specifier|final
name|PackedInts
operator|.
name|Decoder
index|[]
name|decoders
decl_stmt|;
DECL|field|iterations
specifier|private
specifier|final
name|int
index|[]
name|iterations
decl_stmt|;
comment|/**    * Create a new {@link ForUtil} instance and save state into<code>out</code>.    */
DECL|method|ForUtil
name|ForUtil
parameter_list|(
name|float
name|acceptableOverheadRatio
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|encodedSizes
operator|=
operator|new
name|int
index|[
literal|33
index|]
expr_stmt|;
name|encoders
operator|=
operator|new
name|PackedInts
operator|.
name|Encoder
index|[
literal|33
index|]
expr_stmt|;
name|decoders
operator|=
operator|new
name|PackedInts
operator|.
name|Decoder
index|[
literal|33
index|]
expr_stmt|;
name|iterations
operator|=
operator|new
name|int
index|[
literal|33
index|]
expr_stmt|;
for|for
control|(
name|int
name|bpv
init|=
literal|1
init|;
name|bpv
operator|<=
literal|32
condition|;
operator|++
name|bpv
control|)
block|{
specifier|final
name|FormatAndBits
name|formatAndBits
init|=
name|PackedInts
operator|.
name|fastestFormatAndBits
argument_list|(
name|BLOCK_SIZE
argument_list|,
name|bpv
argument_list|,
name|acceptableOverheadRatio
argument_list|)
decl_stmt|;
assert|assert
name|formatAndBits
operator|.
name|format
operator|.
name|isSupported
argument_list|(
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|)
assert|;
assert|assert
name|formatAndBits
operator|.
name|bitsPerValue
operator|<=
literal|32
assert|;
name|encodedSizes
index|[
name|bpv
index|]
operator|=
name|encodedSize
argument_list|(
name|formatAndBits
operator|.
name|format
argument_list|,
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|)
expr_stmt|;
name|encoders
index|[
name|bpv
index|]
operator|=
name|PackedInts
operator|.
name|getEncoder
argument_list|(
name|formatAndBits
operator|.
name|format
argument_list|,
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|)
expr_stmt|;
name|decoders
index|[
name|bpv
index|]
operator|=
name|PackedInts
operator|.
name|getDecoder
argument_list|(
name|formatAndBits
operator|.
name|format
argument_list|,
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|,
name|formatAndBits
operator|.
name|bitsPerValue
argument_list|)
expr_stmt|;
name|iterations
index|[
name|bpv
index|]
operator|=
name|computeIterations
argument_list|(
name|decoders
index|[
name|bpv
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|formatAndBits
operator|.
name|format
operator|.
name|getId
argument_list|()
operator|<<
literal|5
operator||
operator|(
name|formatAndBits
operator|.
name|bitsPerValue
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Restore a {@link ForUtil} from a {@link DataInput}.    */
DECL|method|ForUtil
name|ForUtil
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|packedIntsVersion
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|packedIntsVersion
operator|!=
name|PackedInts
operator|.
name|VERSION_START
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"expected version="
operator|+
name|PackedInts
operator|.
name|VERSION_START
operator|+
literal|" but got version="
operator|+
name|packedIntsVersion
argument_list|)
throw|;
block|}
name|encodedSizes
operator|=
operator|new
name|int
index|[
literal|33
index|]
expr_stmt|;
name|encoders
operator|=
operator|new
name|PackedInts
operator|.
name|Encoder
index|[
literal|33
index|]
expr_stmt|;
name|decoders
operator|=
operator|new
name|PackedInts
operator|.
name|Decoder
index|[
literal|33
index|]
expr_stmt|;
name|iterations
operator|=
operator|new
name|int
index|[
literal|33
index|]
expr_stmt|;
for|for
control|(
name|int
name|bpv
init|=
literal|1
init|;
name|bpv
operator|<=
literal|32
condition|;
operator|++
name|bpv
control|)
block|{
specifier|final
name|int
name|code
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|formatId
init|=
name|code
operator|>>>
literal|5
decl_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
operator|(
name|code
operator|&
literal|31
operator|)
operator|+
literal|1
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Format
name|format
init|=
name|PackedInts
operator|.
name|Format
operator|.
name|byId
argument_list|(
name|formatId
argument_list|)
decl_stmt|;
assert|assert
name|format
operator|.
name|isSupported
argument_list|(
name|bitsPerValue
argument_list|)
assert|;
name|encodedSizes
index|[
name|bpv
index|]
operator|=
name|encodedSize
argument_list|(
name|format
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|encoders
index|[
name|bpv
index|]
operator|=
name|PackedInts
operator|.
name|getEncoder
argument_list|(
name|format
argument_list|,
name|packedIntsVersion
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|decoders
index|[
name|bpv
index|]
operator|=
name|PackedInts
operator|.
name|getDecoder
argument_list|(
name|format
argument_list|,
name|packedIntsVersion
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|iterations
index|[
name|bpv
index|]
operator|=
name|computeIterations
argument_list|(
name|decoders
index|[
name|bpv
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Write a block of data (<code>For</code> format).    *    * @param data     the data to write    * @param encoded  a buffer to use to encode data    * @param out      the destination output    * @throws IOException If there is a low-level I/O error    */
DECL|method|writeBlock
name|void
name|writeBlock
parameter_list|(
name|int
index|[]
name|data
parameter_list|,
name|byte
index|[]
name|encoded
parameter_list|,
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isAllEqual
argument_list|(
name|data
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|ALL_VALUES_EQUAL
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|data
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|numBits
init|=
name|bitsRequired
argument_list|(
name|data
argument_list|)
decl_stmt|;
assert|assert
name|numBits
operator|>
literal|0
operator|&&
name|numBits
operator|<=
literal|32
operator|:
name|numBits
assert|;
specifier|final
name|PackedInts
operator|.
name|Encoder
name|encoder
init|=
name|encoders
index|[
name|numBits
index|]
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|iterations
index|[
name|numBits
index|]
decl_stmt|;
assert|assert
name|iters
operator|*
name|encoder
operator|.
name|valueCount
argument_list|()
operator|>=
name|BLOCK_SIZE
assert|;
specifier|final
name|int
name|encodedSize
init|=
name|encodedSizes
index|[
name|numBits
index|]
decl_stmt|;
assert|assert
operator|(
name|iters
operator|*
name|encoder
operator|.
name|blockCount
argument_list|()
operator|)
operator|<<
literal|3
operator|>=
name|encodedSize
assert|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
name|encoder
operator|.
name|encode
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|encoded
argument_list|,
literal|0
argument_list|,
name|iters
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|encoded
argument_list|,
name|encodedSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read the next block of data (<code>For</code> format).    *    * @param in        the input to use to read data    * @param encoded   a buffer that can be used to store encoded data    * @param decoded   where to write decoded data    * @throws IOException If there is a low-level I/O error    */
DECL|method|readBlock
name|void
name|readBlock
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|byte
index|[]
name|encoded
parameter_list|,
name|int
index|[]
name|decoded
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|numBits
operator|<=
literal|32
operator|:
name|numBits
assert|;
if|if
condition|(
name|numBits
operator|==
name|ALL_VALUES_EQUAL
condition|)
block|{
specifier|final
name|int
name|value
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|decoded
argument_list|,
literal|0
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|encodedSize
init|=
name|encodedSizes
index|[
name|numBits
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|encodedSize
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Decoder
name|decoder
init|=
name|decoders
index|[
name|numBits
index|]
decl_stmt|;
specifier|final
name|int
name|iters
init|=
name|iterations
index|[
name|numBits
index|]
decl_stmt|;
assert|assert
name|iters
operator|*
name|decoder
operator|.
name|valueCount
argument_list|()
operator|>=
name|BLOCK_SIZE
assert|;
name|decoder
operator|.
name|decode
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|decoded
argument_list|,
literal|0
argument_list|,
name|iters
argument_list|)
expr_stmt|;
block|}
comment|/**    * Skip the next block of data.    *    * @param in      the input where to read data    * @throws IOException If there is a low-level I/O error    */
DECL|method|skipBlock
name|void
name|skipBlock
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBits
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numBits
operator|==
name|ALL_VALUES_EQUAL
condition|)
block|{
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
return|return;
block|}
assert|assert
name|numBits
operator|>
literal|0
operator|&&
name|numBits
operator|<=
literal|32
operator|:
name|numBits
assert|;
specifier|final
name|int
name|encodedSize
init|=
name|encodedSizes
index|[
name|numBits
index|]
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|in
operator|.
name|getFilePointer
argument_list|()
operator|+
name|encodedSize
argument_list|)
expr_stmt|;
block|}
DECL|method|isAllEqual
specifier|private
specifier|static
name|boolean
name|isAllEqual
parameter_list|(
specifier|final
name|int
index|[]
name|data
parameter_list|)
block|{
specifier|final
name|long
name|v
init|=
name|data
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
name|BLOCK_SIZE
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|data
index|[
name|i
index|]
operator|!=
name|v
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Compute the number of bits required to serialize any of the longs in    *<code>data</code>.    */
DECL|method|bitsRequired
specifier|private
specifier|static
name|int
name|bitsRequired
parameter_list|(
specifier|final
name|int
index|[]
name|data
parameter_list|)
block|{
name|long
name|or
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
name|BLOCK_SIZE
condition|;
operator|++
name|i
control|)
block|{
assert|assert
name|data
index|[
name|i
index|]
operator|>=
literal|0
assert|;
name|or
operator||=
name|data
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|or
argument_list|)
return|;
block|}
block|}
end_class

end_unit

