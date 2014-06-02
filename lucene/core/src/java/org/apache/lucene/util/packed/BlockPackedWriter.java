begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|BitUtil
operator|.
name|zigZagEncode
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

begin_comment
comment|/**  * A writer for large sequences of longs.  *<p>  * The sequence is divided into fixed-size blocks and for each block, the  * difference between each value and the minimum value of the block is encoded  * using as few bits as possible. Memory usage of this class is proportional to  * the block size. Each block has an overhead between 1 and 10 bytes to store  * the minimum value and the number of bits per value of the block.  *<p>  * Format:  *<ul>  *<li>&lt;BLock&gt;<sup>BlockCount</sup>  *<li>BlockCount:&lceil; ValueCount / BlockSize&rceil;  *<li>Block:&lt;Header, (Ints)&gt;  *<li>Header:&lt;Token, (MinValue)&gt;  *<li>Token: a {@link DataOutput#writeByte(byte) byte}, first 7 bits are the  *     number of bits per value (<tt>bitsPerValue</tt>). If the 8th bit is 1,  *     then MinValue (see next) is<tt>0</tt>, otherwise MinValue and needs to  *     be decoded  *<li>MinValue: a  *<a href="https://developers.google.com/protocol-buffers/docs/encoding#types">zigzag-encoded</a>  *     {@link DataOutput#writeVLong(long) variable-length long} whose value  *     should be added to every int from the block to restore the original  *     values  *<li>Ints: If the number of bits per value is<tt>0</tt>, then there is  *     nothing to decode and all ints are equal to MinValue. Otherwise: BlockSize  *     {@link PackedInts packed ints} encoded on exactly<tt>bitsPerValue</tt>  *     bits per value. They are the subtraction of the original values and  *     MinValue  *</ul>  * @see BlockPackedReaderIterator  * @see BlockPackedReader  * @lucene.internal  */
end_comment

begin_class
DECL|class|BlockPackedWriter
specifier|public
specifier|final
class|class
name|BlockPackedWriter
extends|extends
name|AbstractBlockPackedWriter
block|{
DECL|field|acceptableOverheadRatio
specifier|final
name|float
name|acceptableOverheadRatio
decl_stmt|;
comment|/**    * Sole constructor.    * @param blockSize the number of values of a single block, must be a power of 2    * @param acceptableOverheadRatio an acceptable overhead ratio per value    */
DECL|method|BlockPackedWriter
specifier|public
name|BlockPackedWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|acceptableOverheadRatio
operator|=
name|acceptableOverheadRatio
expr_stmt|;
block|}
DECL|method|flush
specifier|protected
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|off
operator|>
literal|0
assert|;
name|long
name|min
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|,
name|max
init|=
name|Long
operator|.
name|MIN_VALUE
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
name|off
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
name|values
index|[
name|i
index|]
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|values
index|[
name|i
index|]
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|delta
init|=
name|max
operator|-
name|min
decl_stmt|;
name|int
name|bitsRequired
init|=
name|delta
operator|<
literal|0
condition|?
literal|64
else|:
name|delta
operator|==
literal|0L
condition|?
literal|0
else|:
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
argument_list|)
decl_stmt|;
name|bitsRequired
operator|=
name|PackedInts
operator|.
name|fastestDirectBits
argument_list|(
name|bitsRequired
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
if|if
condition|(
name|bitsRequired
operator|==
literal|64
condition|)
block|{
comment|// no need to delta-encode
name|min
operator|=
literal|0L
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|min
operator|>
literal|0L
condition|)
block|{
comment|// make min as small as possible so that writeVLong requires fewer bytes
name|min
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|max
operator|-
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|bitsRequired
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|token
init|=
operator|(
name|bitsRequired
operator|<<
name|BPV_SHIFT
operator|)
operator||
operator|(
name|min
operator|==
literal|0
condition|?
name|MIN_VALUE_EQUALS_0
else|:
literal|0
operator|)
decl_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|!=
literal|0
condition|)
block|{
name|writeVLong
argument_list|(
name|out
argument_list|,
name|zigZagEncode
argument_list|(
name|min
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsRequired
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|min
operator|!=
literal|0
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
name|off
condition|;
operator|++
name|i
control|)
block|{
name|values
index|[
name|i
index|]
operator|-=
name|min
expr_stmt|;
block|}
block|}
name|writeValues
argument_list|(
name|bitsRequired
argument_list|)
expr_stmt|;
block|}
name|off
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

