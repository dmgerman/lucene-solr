begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|Map
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
name|UnicodeUtil
import|;
end_import

begin_comment
comment|/**  * Abstract base class for performing write operations of Lucene's low-level  * data types.  */
end_comment

begin_class
DECL|class|DataOutput
specifier|public
specifier|abstract
class|class
name|DataOutput
block|{
comment|/** Writes a single byte.    * @see IndexInput#readByte()    */
DECL|method|writeByte
specifier|public
specifier|abstract
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Writes an array of bytes.    * @param b the bytes to write    * @param length the number of bytes to write    * @see DataInput#readBytes(byte[],int,int)    */
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Writes an array of bytes.    * @param b the bytes to write    * @param offset the offset in the byte array    * @param length the number of bytes to write    * @see DataInput#readBytes(byte[],int,int)    */
DECL|method|writeBytes
specifier|public
specifier|abstract
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Writes an int as four bytes.    * @see DataInput#readInt()    */
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|24
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes an int in a variable-length format.  Writes between one and    * five bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    * @see DataInput#readVInt()    */
DECL|method|writeVInt
specifier|public
name|void
name|writeVInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
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
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes a long as eight bytes.    * @see DataInput#readLong()    */
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|i
operator|>>
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes an long in a variable-length format.  Writes between one and five    * bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    * @see DataInput#readVLong()    */
DECL|method|writeVLong
specifier|public
name|void
name|writeVLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
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
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/** Writes a string.    * @see DataInput#readString()    */
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|utf8Result
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|,
name|utf8Result
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
name|utf8Result
operator|.
name|length
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
name|utf8Result
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|utf8Result
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Writes a sub sequence of characters from s as the old    *  format (modified UTF-8 encoded bytes).    * @param s the source of the characters    * @param start the first character in the sequence    * @param length the number of characters in the sequence    * @deprecated -- please pre-convert to utf8 bytes    * instead or use {@link #writeString}    */
annotation|@
name|Deprecated
DECL|method|writeChars
specifier|public
name|void
name|writeChars
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
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
name|end
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|code
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
name|code
operator|>>>
literal|12
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Writes a sub sequence of characters from char[] as    *  the old format (modified UTF-8 encoded bytes).    * @param s the source of the characters    * @param start the first character in the sequence    * @param length the number of characters in the sequence    * @deprecated -- please pre-convert to utf8 bytes instead or use {@link #writeString}    */
annotation|@
name|Deprecated
DECL|method|writeChars
specifier|public
name|void
name|writeChars
parameter_list|(
name|char
index|[]
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
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
name|end
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
name|s
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|code
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
name|code
operator|>>>
literal|12
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|COPY_BUFFER_SIZE
specifier|private
specifier|static
name|int
name|COPY_BUFFER_SIZE
init|=
literal|16384
decl_stmt|;
DECL|field|copyBuffer
specifier|private
name|byte
index|[]
name|copyBuffer
decl_stmt|;
comment|/** Copy numBytes bytes from input to ourself. */
DECL|method|copyBytes
specifier|public
name|void
name|copyBytes
parameter_list|(
name|DataInput
name|input
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|numBytes
operator|>=
literal|0
operator|:
literal|"numBytes="
operator|+
name|numBytes
assert|;
name|long
name|left
init|=
name|numBytes
decl_stmt|;
if|if
condition|(
name|copyBuffer
operator|==
literal|null
condition|)
name|copyBuffer
operator|=
operator|new
name|byte
index|[
name|COPY_BUFFER_SIZE
index|]
expr_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|toCopy
decl_stmt|;
if|if
condition|(
name|left
operator|>
name|COPY_BUFFER_SIZE
condition|)
name|toCopy
operator|=
name|COPY_BUFFER_SIZE
expr_stmt|;
else|else
name|toCopy
operator|=
operator|(
name|int
operator|)
name|left
expr_stmt|;
name|input
operator|.
name|readBytes
argument_list|(
name|copyBuffer
argument_list|,
literal|0
argument_list|,
name|toCopy
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
name|copyBuffer
argument_list|,
literal|0
argument_list|,
name|toCopy
argument_list|)
expr_stmt|;
name|left
operator|-=
name|toCopy
expr_stmt|;
block|}
block|}
DECL|method|writeStringStringMap
specifier|public
name|void
name|writeStringStringMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeInt
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

