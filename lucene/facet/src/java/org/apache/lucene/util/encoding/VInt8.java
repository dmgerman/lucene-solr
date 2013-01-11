begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.encoding
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|encoding
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
name|BytesRef
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Variable-length encoding of 32-bit integers, into 8-bit bytes. A number is  * encoded as follows:  *<ul>  *<li>If it is less than 127 and non-negative (i.e., if the number uses only 7  * bits), it is encoded as as single byte: 0bbbbbbb.  *<li>If its highest nonzero bit is greater than bit 6 (0x40), it is  * represented as a series of bytes, each byte's 7 LSB containing bits from the  * original value, with the MSB set for all but the last byte. The first encoded  * byte contains the highest nonzero bits from the original; the second byte  * contains the next 7 MSB; and so on, with the last byte containing the 7 LSB  * of the original.  *</ul>  * Examples:  *<ol>  *<li>n = 117 = 1110101: This has fewer than 8 significant bits, and so is  * encoded as 01110101 = 0x75.  *<li>n = 100000 = (binary) 11000011010100000. This has 17 significant bits,  * and so needs three Vint8 bytes. Left-zero-pad it to a multiple of 7 bits,  * then split it into chunks of 7 and add an MSB, 0 for the last byte, 1 for the  * others: 1|0000110 1|0001101 0|0100000 = 0x86 0x8D 0x20.  *</ol>  * {@link #encode(int, BytesRef)} and {@link #decode(BytesRef)} will correctly  * handle any 32-bit integer, but for negative numbers, and positive numbers  * with more than 28 significant bits, encoding requires 5 bytes; this is not an  * efficient encoding scheme for large positive numbers or any negative number.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|VInt8
specifier|public
class|class
name|VInt8
block|{
comment|/** The maximum number of bytes needed to encode an integer. */
DECL|field|MAXIMUM_BYTES_NEEDED
specifier|public
specifier|static
specifier|final
name|int
name|MAXIMUM_BYTES_NEEDED
init|=
literal|5
decl_stmt|;
comment|/**    * Decodes an int from the given bytes, starting at {@link BytesRef#offset}.    * Returns the decoded bytes and updates {@link BytesRef#offset}.    */
DECL|method|decode
specifier|public
specifier|static
name|int
name|decode
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
comment|/*     This is the original code of this method, but a Hotspot bug     corrupted the for-loop of DataInput.readVInt() (see LUCENE-2975)     so the loop was unwounded here too, to be on the safe side     int value = 0;     while (true) {       byte first = bytes.bytes[bytes.offset++];       value |= first& 0x7F;       if ((first& 0x80) == 0) {         return value;       }       value<<= 7;     }     */
comment|// byte 1
name|byte
name|b
init|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|b
return|;
comment|// byte 2
name|int
name|value
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
name|b
operator|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|++
index|]
expr_stmt|;
name|value
operator|=
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
name|b
operator|&
literal|0x7F
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|value
return|;
comment|// byte 3
name|b
operator|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|++
index|]
expr_stmt|;
name|value
operator|=
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
name|b
operator|&
literal|0x7F
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|value
return|;
comment|// byte 4
name|b
operator|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|++
index|]
expr_stmt|;
name|value
operator|=
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
name|b
operator|&
literal|0x7F
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|value
return|;
comment|// byte 5
name|b
operator|=
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|++
index|]
expr_stmt|;
return|return
operator|(
name|value
operator|<<
literal|7
operator|)
operator||
name|b
operator|&
literal|0x7F
return|;
block|}
comment|/**    * Encodes the given number into bytes, starting at {@link BytesRef#length}.    * Assumes that the array is large enough.    */
DECL|method|encode
specifier|public
specifier|static
name|void
name|encode
parameter_list|(
name|int
name|value
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x7F
operator|)
operator|==
literal|0
condition|)
block|{
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
name|bytes
operator|.
name|length
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x3FFF
operator|)
operator|==
literal|0
condition|)
block|{
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|+=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0x1FFFFF
operator|)
operator|==
literal|0
condition|)
block|{
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x1FC000
operator|)
operator|>>
literal|14
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|+=
literal|3
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|value
operator|&
operator|~
literal|0xFFFFFFF
operator|)
operator|==
literal|0
condition|)
block|{
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0xFE00000
operator|)
operator|>>
literal|21
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x1FC000
operator|)
operator|>>
literal|14
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|+=
literal|4
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0xF0000000
operator|)
operator|>>
literal|28
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0xFE00000
operator|)
operator|>>
literal|21
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x1FC000
operator|)
operator|>>
literal|14
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|value
operator|&
literal|0x3F80
operator|)
operator|>>
literal|7
operator|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|length
operator|+
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|&
literal|0x7F
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|length
operator|+=
literal|5
expr_stmt|;
block|}
block|}
DECL|method|VInt8
specifier|private
name|VInt8
parameter_list|()
block|{
comment|// Just making it impossible to instantiate.
block|}
block|}
end_class

end_unit

