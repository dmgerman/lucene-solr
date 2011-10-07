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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|util
operator|.
name|CodecUtil
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
name|Constants
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

begin_comment
comment|/**  * Simplistic compression for array of unsigned long values.  * Each value is>= 0 and<= a specified maximum value.  The  * values are stored as packed ints, with each value  * consuming a fixed number of bits.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|PackedInts
specifier|public
class|class
name|PackedInts
block|{
DECL|field|CODEC_NAME
specifier|private
specifier|final
specifier|static
name|String
name|CODEC_NAME
init|=
literal|"PackedInts"
decl_stmt|;
DECL|field|VERSION_START
specifier|private
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|private
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
comment|/**    * A read-only random access array of positive integers.    * @lucene.internal    */
DECL|interface|Reader
specifier|public
specifier|static
interface|interface
name|Reader
block|{
comment|/**      * @param index the position of the wanted value.      * @return the value at the stated index.      */
DECL|method|get
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**      * @return the number of bits used to store any given value.      *         Note: This does not imply that memory usage is      *         {@code bitsPerValue * #values} as implementations are free to      *         use non-space-optimal packing of bits.      */
DECL|method|getBitsPerValue
name|int
name|getBitsPerValue
parameter_list|()
function_decl|;
comment|/**      * @return the number of values.      */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
block|}
comment|/**    * Run-once iterator interface, to decode previously saved PackedInts.    */
DECL|interface|ReaderIterator
specifier|public
specifier|static
interface|interface
name|ReaderIterator
extends|extends
name|Closeable
block|{
comment|/** Returns next value */
DECL|method|next
name|long
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns number of bits per value */
DECL|method|getBitsPerValue
name|int
name|getBitsPerValue
parameter_list|()
function_decl|;
comment|/** Returns number of values */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/** Returns the current position */
DECL|method|ord
name|int
name|ord
parameter_list|()
function_decl|;
comment|/** Skips to the given ordinal and returns its value.      * @return the value at the given position      * @throws IOException if reading the value throws an IOException*/
DECL|method|advance
name|long
name|advance
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|interface|RandomAccessReaderIterator
specifier|public
specifier|static
interface|interface
name|RandomAccessReaderIterator
extends|extends
name|ReaderIterator
block|{
comment|/**      * @param index the position of the wanted value.      * @return the value at the stated index.      */
DECL|method|get
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * A packed integer array that can be modified.    * @lucene.internal    */
DECL|interface|Mutable
specifier|public
specifier|static
interface|interface
name|Mutable
extends|extends
name|Reader
block|{
comment|/**      * Set the value at the given index in the array.      * @param index where the value should be positioned.      * @param value a value conforming to the constraints set by the array.      */
DECL|method|set
name|void
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/**      * Sets all values to 0.      */
DECL|method|clear
name|void
name|clear
parameter_list|()
function_decl|;
block|}
comment|/**    * A simple base for Readers that keeps track of valueCount and bitsPerValue.    * @lucene.internal    */
DECL|class|ReaderImpl
specifier|public
specifier|static
specifier|abstract
class|class
name|ReaderImpl
implements|implements
name|Reader
block|{
DECL|field|bitsPerValue
specifier|protected
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|valueCount
specifier|protected
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|ReaderImpl
specifier|protected
name|ReaderImpl
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
block|}
DECL|method|getBitsPerValue
specifier|public
name|int
name|getBitsPerValue
parameter_list|()
block|{
return|return
name|bitsPerValue
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
DECL|method|getMaxValue
specifier|public
name|long
name|getMaxValue
parameter_list|()
block|{
comment|// Convenience method
return|return
name|maxValue
argument_list|(
name|bitsPerValue
argument_list|)
return|;
block|}
block|}
comment|/** A write-once Writer.    * @lucene.internal    */
DECL|class|Writer
specifier|public
specifier|static
specifier|abstract
class|class
name|Writer
block|{
DECL|field|out
specifier|protected
specifier|final
name|DataOutput
name|out
decl_stmt|;
DECL|field|bitsPerValue
specifier|protected
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|valueCount
specifier|protected
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|Writer
specifier|protected
name|Writer
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|bitsPerValue
operator|<=
literal|64
assert|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|bitsPerValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
block|}
DECL|method|add
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Retrieve PackedInt data from the DataInput and return a packed int    * structure based on it.    * @param in positioned at the beginning of a stored packed int structure.    * @return a read only random access capable array of positive integers.    * @throws IOException if the structure could not be retrieved.    * @lucene.internal    */
DECL|method|getReader
specifier|public
specifier|static
name|Reader
name|getReader
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
specifier|final
name|int
name|valueCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|bitsPerValue
condition|)
block|{
case|case
literal|8
case|:
return|return
operator|new
name|Direct8
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|16
case|:
return|return
operator|new
name|Direct16
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|32
case|:
return|return
operator|new
name|Direct32
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
case|case
literal|64
case|:
return|return
operator|new
name|Direct64
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|)
return|;
default|default:
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
operator|||
name|bitsPerValue
operator|>=
literal|32
condition|)
block|{
return|return
operator|new
name|Packed64
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Packed32
argument_list|(
name|in
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Retrieve PackedInts as a {@link ReaderIterator}    * @param in positioned at the beginning of a stored packed int structure.    * @return an iterator to access the values    * @throws IOException if the structure could not be retrieved.    * @lucene.internal    */
DECL|method|getReaderIterator
specifier|public
specifier|static
name|ReaderIterator
name|getReaderIterator
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getRandomAccessReaderIterator
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**    * Retrieve PackedInts as a {@link RandomAccessReaderIterator}    * @param in positioned at the beginning of a stored packed int structure.    * @return an iterator to access the values    * @throws IOException if the structure could not be retrieved.    * @lucene.internal    */
DECL|method|getRandomAccessReaderIterator
specifier|public
specifier|static
name|RandomAccessReaderIterator
name|getRandomAccessReaderIterator
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
assert|assert
name|bitsPerValue
operator|>
literal|0
operator|&&
name|bitsPerValue
operator|<=
literal|64
operator|:
literal|"bitsPerValue="
operator|+
name|bitsPerValue
assert|;
specifier|final
name|int
name|valueCount
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
return|return
operator|new
name|PackedReaderIterator
argument_list|(
name|bitsPerValue
argument_list|,
name|valueCount
argument_list|,
name|in
argument_list|)
return|;
block|}
comment|/**    * Create a packed integer array with the given amount of values initialized    * to 0. the valueCount and the bitsPerValue cannot be changed after creation.    * All Mutables known by this factory are kept fully in RAM.    * @param valueCount   the number of elements.    * @param bitsPerValue the number of bits available for any given value.    * @return a mutable packed integer array.    * @throws java.io.IOException if the Mutable could not be created. With the    *         current implementations, this never happens, but the method    *         signature allows for future persistence-backed Mutables.    * @lucene.internal    */
DECL|method|getMutable
specifier|public
specifier|static
name|Mutable
name|getMutable
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
switch|switch
condition|(
name|bitsPerValue
condition|)
block|{
case|case
literal|8
case|:
return|return
operator|new
name|Direct8
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|16
case|:
return|return
operator|new
name|Direct16
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|32
case|:
return|return
operator|new
name|Direct32
argument_list|(
name|valueCount
argument_list|)
return|;
case|case
literal|64
case|:
return|return
operator|new
name|Direct64
argument_list|(
name|valueCount
argument_list|)
return|;
default|default:
if|if
condition|(
name|Constants
operator|.
name|JRE_IS_64BIT
operator|||
name|bitsPerValue
operator|>=
literal|32
condition|)
block|{
return|return
operator|new
name|Packed64
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Packed32
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * Create a packed integer array writer for the given number of values at the    * given bits/value. Writers append to the given IndexOutput and has very    * low memory overhead.    * @param out          the destination for the produced bits.    * @param valueCount   the number of elements.    * @param bitsPerValue the number of bits available for any given value.    * @return a Writer ready for receiving values.    * @throws IOException if bits could not be written to out.    * @lucene.internal    */
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|PackedWriter
argument_list|(
name|out
argument_list|,
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
return|;
block|}
comment|/** Returns how many bits are required to hold values up    *  to and including maxValue    * @param maxValue the maximum value that should be representable.    * @return the amount of bits needed to represent values from 0 to maxValue.    * @lucene.internal    */
DECL|method|bitsRequired
specifier|public
specifier|static
name|int
name|bitsRequired
parameter_list|(
name|long
name|maxValue
parameter_list|)
block|{
comment|// Very high long values does not translate well to double, so we do an
comment|// explicit check for the edge cases
if|if
condition|(
name|maxValue
operator|>
literal|0x3FFFFFFFFFFFFFFFL
condition|)
block|{
return|return
literal|63
return|;
block|}
if|if
condition|(
name|maxValue
operator|>
literal|0x1FFFFFFFFFFFFFFFL
condition|)
block|{
return|return
literal|62
return|;
block|}
return|return
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|Math
operator|.
name|log
argument_list|(
literal|1
operator|+
name|maxValue
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
literal|2.0
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Calculates the maximum unsigned long that can be expressed with the given    * number of bits.    * @param bitsPerValue the number of bits available for any given value.    * @return the maximum value for the given bits.    * @lucene.internal    */
DECL|method|maxValue
specifier|public
specifier|static
name|long
name|maxValue
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
return|return
name|bitsPerValue
operator|==
literal|64
condition|?
name|Long
operator|.
name|MAX_VALUE
else|:
operator|~
operator|(
operator|~
literal|0L
operator|<<
name|bitsPerValue
operator|)
return|;
block|}
comment|/** Rounds bitsPerValue up to 8, 16, 32 or 64. */
DECL|method|getNextFixedSize
specifier|public
specifier|static
name|int
name|getNextFixedSize
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
if|if
condition|(
name|bitsPerValue
operator|<=
literal|8
condition|)
block|{
return|return
literal|8
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|16
condition|)
block|{
return|return
literal|16
return|;
block|}
elseif|else
if|if
condition|(
name|bitsPerValue
operator|<=
literal|32
condition|)
block|{
return|return
literal|32
return|;
block|}
else|else
block|{
return|return
literal|64
return|;
block|}
block|}
comment|/** Possibly wastes some storage in exchange for faster lookups */
DECL|method|getRoundedFixedSize
specifier|public
specifier|static
name|int
name|getRoundedFixedSize
parameter_list|(
name|int
name|bitsPerValue
parameter_list|)
block|{
if|if
condition|(
name|bitsPerValue
operator|>
literal|58
operator|||
operator|(
name|bitsPerValue
argument_list|<
literal|32
operator|&&
name|bitsPerValue
argument_list|>
literal|29
operator|)
condition|)
block|{
comment|// 10% space-waste is ok
return|return
name|getNextFixedSize
argument_list|(
name|bitsPerValue
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|bitsPerValue
return|;
block|}
block|}
block|}
end_class

end_unit

