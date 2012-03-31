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
name|HashMap
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Abstract base class for performing read operations of Lucene's low-level  * data types.  */
end_comment

begin_class
DECL|class|DataInput
specifier|public
specifier|abstract
class|class
name|DataInput
implements|implements
name|Cloneable
block|{
comment|/** Reads and returns a single byte.    * @see DataOutput#writeByte(byte)    */
DECL|method|readByte
specifier|public
specifier|abstract
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Reads a specified number of bytes into an array at the specified offset.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @see DataOutput#writeBytes(byte[],int)    */
DECL|method|readBytes
specifier|public
specifier|abstract
name|void
name|readBytes
parameter_list|(
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
throws|throws
name|IOException
function_decl|;
comment|/** Reads a specified number of bytes into an array at the    * specified offset with control over whether the read    * should be buffered (callers who have their own buffer    * should pass in "false" for useBuffer).  Currently only    * {@link BufferedIndexInput} respects this parameter.    * @param b the array to read bytes into    * @param offset the offset in the array to start storing bytes    * @param len the number of bytes to read    * @param useBuffer set to false if the caller will handle    * buffering.    * @see DataOutput#writeBytes(byte[],int)    */
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|boolean
name|useBuffer
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Default to ignoring useBuffer entirely
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/** Reads two bytes and returns a short.    * @see DataOutput#writeByte(byte)    */
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
argument_list|)
return|;
block|}
comment|/** Reads four bytes and returns an int.    * @see DataOutput#writeInt(int)    */
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
return|;
block|}
comment|/** Reads an int stored in variable-length format.  Reads between one and    * five bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported.    * @see DataOutput#writeVInt(int)    */
DECL|method|readVInt
specifier|public
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* This is the original code of this method,      * but a Hotspot bug (see LUCENE-2975) corrupts the for-loop if      * readByte() is inlined. So the loop was unwinded!     byte b = readByte();     int i = b& 0x7F;     for (int shift = 7; (b& 0x80) != 0; shift += 7) {       b = readByte();       i |= (b& 0x7F)<< shift;     }     return i;     */
name|byte
name|b
init|=
name|readByte
argument_list|()
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
name|int
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
comment|// Warning: the next ands use 0x0F / 0xF0 - beware copy/paste errors:
name|i
operator||=
operator|(
name|b
operator|&
literal|0x0F
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0xF0
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid vInt detected (too many bits)"
argument_list|)
throw|;
block|}
comment|/** Reads eight bytes and returns a long.    * @see DataOutput#writeLong(long)    */
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|readInt
argument_list|()
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|readInt
argument_list|()
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
comment|/** Reads a long stored in variable-length format.  Reads between one and    * nine bytes.  Smaller values take fewer bytes.  Negative numbers are not    * supported. */
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* This is the original code of this method,      * but a Hotspot bug (see LUCENE-2975) corrupts the for-loop if      * readByte() is inlined. So the loop was unwinded!     byte b = readByte();     long i = b& 0x7F;     for (int shift = 7; (b& 0x80) != 0; shift += 7) {       b = readByte();       i |= (b& 0x7FL)<< shift;     }     return i;     */
name|byte
name|b
init|=
name|readByte
argument_list|()
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
name|long
name|i
init|=
name|b
operator|&
literal|0x7FL
decl_stmt|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|35
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|42
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|49
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|56
expr_stmt|;
if|if
condition|(
name|b
operator|>=
literal|0
condition|)
return|return
name|i
return|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid vLong detected (negative values disallowed)"
argument_list|)
throw|;
block|}
comment|/** Reads a string.    * @see DataOutput#writeString(String)    */
DECL|method|readString
specifier|public
name|String
name|readString
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
return|;
block|}
comment|/** Returns a clone of this stream.    *    *<p>Clones of a stream access the same data, and are positioned at the same    * point as the stream they were cloned from.    *    *<p>Expert: Subclasses must ensure that clones may be positioned at    * different points in the input from each other and from the stream they    * were cloned from.    */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|DataInput
name|clone
parameter_list|()
block|{
name|DataInput
name|clone
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clone
operator|=
operator|(
name|DataInput
operator|)
name|super
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{}
return|return
name|clone
return|;
block|}
DECL|method|readStringStringMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readStringStringMap
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|readInt
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|key
init|=
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|val
init|=
name|readString
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

