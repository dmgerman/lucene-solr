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
name|nio
operator|.
name|IntBuffer
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
specifier|public
specifier|final
class|class
name|ForUtil
block|{
DECL|field|MASK
specifier|protected
specifier|static
specifier|final
name|int
index|[]
name|MASK
init|=
block|{
literal|0x00000000
block|,
literal|0x00000001
block|,
literal|0x00000003
block|,
literal|0x00000007
block|,
literal|0x0000000f
block|,
literal|0x0000001f
block|,
literal|0x0000003f
block|,
literal|0x0000007f
block|,
literal|0x000000ff
block|,
literal|0x000001ff
block|,
literal|0x000003ff
block|,
literal|0x000007ff
block|,
literal|0x00000fff
block|,
literal|0x00001fff
block|,
literal|0x00003fff
block|,
literal|0x00007fff
block|,
literal|0x0000ffff
block|,
literal|0x0001ffff
block|,
literal|0x0003ffff
block|,
literal|0x0007ffff
block|,
literal|0x000fffff
block|,
literal|0x001fffff
block|,
literal|0x003fffff
block|,
literal|0x007fffff
block|,
literal|0x00ffffff
block|,
literal|0x01ffffff
block|,
literal|0x03ffffff
block|,
literal|0x07ffffff
block|,
literal|0x0fffffff
block|,
literal|0x1fffffff
block|,
literal|0x3fffffff
block|,
literal|0x7fffffff
block|,
literal|0xffffffff
block|}
decl_stmt|;
comment|/** Compress given int[] into Integer buffer, with For format    *    * @param data        uncompressed data    * @param intBuffer   integer buffer to hold compressed data    * @return the header for the current block     */
DECL|method|compress
specifier|static
name|int
name|compress
parameter_list|(
specifier|final
name|int
index|[]
name|data
parameter_list|,
name|IntBuffer
name|intBuffer
parameter_list|)
block|{
name|int
name|numBits
init|=
name|getNumBits
argument_list|(
name|data
argument_list|)
decl_stmt|;
if|if
condition|(
name|numBits
operator|==
literal|0
condition|)
block|{
return|return
name|compressDuplicateBlock
argument_list|(
name|data
argument_list|,
name|intBuffer
argument_list|)
return|;
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
name|encodeNormalValue
argument_list|(
name|intBuffer
argument_list|,
name|i
argument_list|,
name|data
index|[
name|i
index|]
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
return|return
name|numBits
return|;
block|}
comment|/**    * Save only one int when the whole block equals to a    * single value.    */
DECL|method|compressDuplicateBlock
specifier|static
name|int
name|compressDuplicateBlock
parameter_list|(
specifier|final
name|int
index|[]
name|data
parameter_list|,
name|IntBuffer
name|intBuffer
parameter_list|)
block|{
name|intBuffer
operator|.
name|put
argument_list|(
literal|0
argument_list|,
name|data
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/** Decompress given Integer buffer into int array.    *    * @param intBuffer   integer buffer to hold compressed data    * @param data        int array to hold uncompressed data    * @param header      header of current block, which contains numFrameBits    */
DECL|method|decompress
specifier|static
name|void
name|decompress
parameter_list|(
name|IntBuffer
name|intBuffer
parameter_list|,
name|int
index|[]
name|data
parameter_list|,
name|int
name|header
parameter_list|)
block|{
comment|// since this buffer is reused at upper level, rewind first
name|intBuffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
comment|// NOTE: header == numBits now, but we may change that
specifier|final
name|int
name|numBits
init|=
name|header
decl_stmt|;
assert|assert
name|numBits
operator|>=
literal|0
operator|&&
name|numBits
operator|<
literal|32
assert|;
name|decompressCore
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|,
name|numBits
argument_list|)
expr_stmt|;
block|}
DECL|method|decompressCore
specifier|public
specifier|static
name|void
name|decompressCore
parameter_list|(
name|IntBuffer
name|intBuffer
parameter_list|,
name|int
index|[]
name|data
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
switch|switch
condition|(
name|numBits
condition|)
block|{
case|case
literal|0
case|:
name|PackedIntsDecompress
operator|.
name|decode0
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|PackedIntsDecompress
operator|.
name|decode1
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|PackedIntsDecompress
operator|.
name|decode2
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|PackedIntsDecompress
operator|.
name|decode3
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|PackedIntsDecompress
operator|.
name|decode4
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|PackedIntsDecompress
operator|.
name|decode5
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|PackedIntsDecompress
operator|.
name|decode6
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|PackedIntsDecompress
operator|.
name|decode7
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|PackedIntsDecompress
operator|.
name|decode8
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|PackedIntsDecompress
operator|.
name|decode9
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|10
case|:
name|PackedIntsDecompress
operator|.
name|decode10
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|11
case|:
name|PackedIntsDecompress
operator|.
name|decode11
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|12
case|:
name|PackedIntsDecompress
operator|.
name|decode12
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|13
case|:
name|PackedIntsDecompress
operator|.
name|decode13
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|14
case|:
name|PackedIntsDecompress
operator|.
name|decode14
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|15
case|:
name|PackedIntsDecompress
operator|.
name|decode15
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|16
case|:
name|PackedIntsDecompress
operator|.
name|decode16
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|17
case|:
name|PackedIntsDecompress
operator|.
name|decode17
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|18
case|:
name|PackedIntsDecompress
operator|.
name|decode18
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|19
case|:
name|PackedIntsDecompress
operator|.
name|decode19
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|20
case|:
name|PackedIntsDecompress
operator|.
name|decode20
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|21
case|:
name|PackedIntsDecompress
operator|.
name|decode21
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|22
case|:
name|PackedIntsDecompress
operator|.
name|decode22
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|23
case|:
name|PackedIntsDecompress
operator|.
name|decode23
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|24
case|:
name|PackedIntsDecompress
operator|.
name|decode24
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|25
case|:
name|PackedIntsDecompress
operator|.
name|decode25
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|26
case|:
name|PackedIntsDecompress
operator|.
name|decode26
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|27
case|:
name|PackedIntsDecompress
operator|.
name|decode27
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|28
case|:
name|PackedIntsDecompress
operator|.
name|decode28
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|29
case|:
name|PackedIntsDecompress
operator|.
name|decode29
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|30
case|:
name|PackedIntsDecompress
operator|.
name|decode30
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
literal|31
case|:
name|PackedIntsDecompress
operator|.
name|decode31
argument_list|(
name|intBuffer
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
comment|// nocommit have default throw exc?  or add assert up above
block|}
block|}
DECL|method|encodeNormalValue
specifier|static
name|void
name|encodeNormalValue
parameter_list|(
name|IntBuffer
name|intBuffer
parameter_list|,
name|int
name|pos
parameter_list|,
name|int
name|value
parameter_list|,
name|int
name|numBits
parameter_list|)
block|{
specifier|final
name|int
name|globalBitPos
init|=
name|numBits
operator|*
name|pos
decl_stmt|;
comment|// position in bit stream
specifier|final
name|int
name|localBitPos
init|=
name|globalBitPos
operator|&
literal|31
decl_stmt|;
comment|// position inside an int
name|int
name|intPos
init|=
name|globalBitPos
operator|/
literal|32
decl_stmt|;
comment|// which integer to locate
name|setBufferIntBits
argument_list|(
name|intBuffer
argument_list|,
name|intPos
argument_list|,
name|localBitPos
argument_list|,
name|numBits
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|localBitPos
operator|+
name|numBits
operator|)
operator|>
literal|32
condition|)
block|{
comment|// value does not fit in this int, fill tail
name|setBufferIntBits
argument_list|(
name|intBuffer
argument_list|,
name|intPos
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
operator|(
name|localBitPos
operator|+
name|numBits
operator|-
literal|32
operator|)
argument_list|,
operator|(
name|value
operator|>>>
operator|(
literal|32
operator|-
name|localBitPos
operator|)
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setBufferIntBits
specifier|static
name|void
name|setBufferIntBits
parameter_list|(
name|IntBuffer
name|intBuffer
parameter_list|,
name|int
name|intPos
parameter_list|,
name|int
name|firstBitPos
parameter_list|,
name|int
name|numBits
parameter_list|,
name|int
name|value
parameter_list|)
block|{
assert|assert
operator|(
name|value
operator|&
operator|~
name|MASK
index|[
name|numBits
index|]
operator|)
operator|==
literal|0
assert|;
comment|// safely discards those msb parts when firstBitPos+numBits>32
name|intBuffer
operator|.
name|put
argument_list|(
name|intPos
argument_list|,
operator|(
name|intBuffer
operator|.
name|get
argument_list|(
name|intPos
argument_list|)
operator|&
operator|~
operator|(
name|MASK
index|[
name|numBits
index|]
operator|<<
name|firstBitPos
operator|)
operator|)
operator||
operator|(
name|value
operator|<<
name|firstBitPos
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns number of bits necessary to represent max value.    */
DECL|method|getNumBits
specifier|static
name|int
name|getNumBits
parameter_list|(
specifier|final
name|int
index|[]
name|data
parameter_list|)
block|{
if|if
condition|(
name|isAllEqual
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|size
init|=
name|data
operator|.
name|length
decl_stmt|;
name|int
name|optBits
init|=
literal|1
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
name|size
condition|;
operator|++
name|i
control|)
block|{
while|while
condition|(
operator|(
name|data
index|[
name|i
index|]
operator|&
operator|~
name|MASK
index|[
name|optBits
index|]
operator|)
operator|!=
literal|0
condition|)
block|{
name|optBits
operator|++
expr_stmt|;
block|}
block|}
assert|assert
name|optBits
operator|<
literal|32
assert|;
return|return
name|optBits
return|;
block|}
comment|// nocommit: we must have a util function for this, hmm?
DECL|method|isAllEqual
specifier|protected
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
name|int
name|len
init|=
name|data
operator|.
name|length
decl_stmt|;
name|int
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
name|len
condition|;
name|i
operator|++
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
comment|/**     * Expert: get compressed block size(in byte)      */
DECL|method|getEncodedSize
specifier|static
name|int
name|getEncodedSize
parameter_list|(
name|int
name|numBits
parameter_list|)
block|{
comment|// NOTE: works only because BLOCK_SIZE is 0 mod 8:
return|return
name|numBits
operator|==
literal|0
condition|?
literal|4
else|:
name|numBits
operator|*
name|BLOCK_SIZE
operator|/
literal|8
return|;
block|}
block|}
end_class

end_unit

