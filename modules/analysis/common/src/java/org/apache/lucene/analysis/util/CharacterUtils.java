begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|io
operator|.
name|Reader
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
name|Version
import|;
end_import

begin_comment
comment|/**  * {@link CharacterUtils} provides a unified interface to Character-related  * operations to implement backwards compatible character operations based on a  * {@link Version} instance.  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|CharacterUtils
specifier|public
specifier|abstract
class|class
name|CharacterUtils
block|{
DECL|field|JAVA_4
specifier|private
specifier|static
specifier|final
name|Java4CharacterUtils
name|JAVA_4
init|=
operator|new
name|Java4CharacterUtils
argument_list|()
decl_stmt|;
DECL|field|JAVA_5
specifier|private
specifier|static
specifier|final
name|Java5CharacterUtils
name|JAVA_5
init|=
operator|new
name|Java5CharacterUtils
argument_list|()
decl_stmt|;
comment|/**    * Returns a {@link CharacterUtils} implementation according to the given    * {@link Version} instance.    *     * @param matchVersion    *          a version instance    * @return a {@link CharacterUtils} implementation according to the given    *         {@link Version} instance.    */
DECL|method|getInstance
specifier|public
specifier|static
name|CharacterUtils
name|getInstance
parameter_list|(
specifier|final
name|Version
name|matchVersion
parameter_list|)
block|{
return|return
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|)
condition|?
name|JAVA_5
else|:
name|JAVA_4
return|;
block|}
comment|/**    * Returns the code point at the given index of the char array.    * Depending on the {@link Version} passed to    * {@link CharacterUtils#getInstance(Version)} this method mimics the behavior    * of {@link Character#codePointAt(char[], int)} as it would have been    * available on a Java 1.4 JVM or on a later virtual machine version.    *     * @param chars    *          a character array    * @param offset    *          the offset to the char values in the chars array to be converted    *     * @return the Unicode code point at the given index    * @throws NullPointerException    *           - if the array is null.    * @throws IndexOutOfBoundsException    *           - if the value offset is negative or not less than the length of    *           the char array.    */
DECL|method|codePointAt
specifier|public
specifier|abstract
name|int
name|codePointAt
parameter_list|(
specifier|final
name|char
index|[]
name|chars
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
function_decl|;
comment|/**    * Returns the code point at the given index of the {@link CharSequence}.    * Depending on the {@link Version} passed to    * {@link CharacterUtils#getInstance(Version)} this method mimics the behavior    * of {@link Character#codePointAt(char[], int)} as it would have been    * available on a Java 1.4 JVM or on a later virtual machine version.    *     * @param seq    *          a character sequence    * @param offset    *          the offset to the char values in the chars array to be converted    *     * @return the Unicode code point at the given index    * @throws NullPointerException    *           - if the sequence is null.    * @throws IndexOutOfBoundsException    *           - if the value offset is negative or not less than the length of    *           the character sequence.    */
DECL|method|codePointAt
specifier|public
specifier|abstract
name|int
name|codePointAt
parameter_list|(
specifier|final
name|CharSequence
name|seq
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
function_decl|;
comment|/**    * Returns the code point at the given index of the char array where only elements    * with index less than the limit are used.    * Depending on the {@link Version} passed to    * {@link CharacterUtils#getInstance(Version)} this method mimics the behavior    * of {@link Character#codePointAt(char[], int)} as it would have been    * available on a Java 1.4 JVM or on a later virtual machine version.    *     * @param chars    *          a character array    * @param offset    *          the offset to the char values in the chars array to be converted    * @param limit the index afer the last element that should be used to calculate    *        codepoint.      *     * @return the Unicode code point at the given index    * @throws NullPointerException    *           - if the array is null.    * @throws IndexOutOfBoundsException    *           - if the value offset is negative or not less than the length of    *           the char array.    */
DECL|method|codePointAt
specifier|public
specifier|abstract
name|int
name|codePointAt
parameter_list|(
specifier|final
name|char
index|[]
name|chars
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
function_decl|;
comment|/**    * Creates a new {@link CharacterBuffer} and allocates a<code>char[]</code>    * of the given bufferSize.    *     * @param bufferSize    *          the internal char buffer size, must be<code>&gt;= 2</code>    * @return a new {@link CharacterBuffer} instance.    */
DECL|method|newCharacterBuffer
specifier|public
specifier|static
name|CharacterBuffer
name|newCharacterBuffer
parameter_list|(
specifier|final
name|int
name|bufferSize
parameter_list|)
block|{
if|if
condition|(
name|bufferSize
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"buffersize must be>= 2"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CharacterBuffer
argument_list|(
operator|new
name|char
index|[
name|bufferSize
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Fills the {@link CharacterBuffer} with characters read from the given    * reader {@link Reader}. This method tries to read as many characters into    * the {@link CharacterBuffer} as possible, each call to fill will start    * filling the buffer from offset<code>0</code> up to the length of the size    * of the internal character array.    *<p>    * Depending on the {@link Version} passed to    * {@link CharacterUtils#getInstance(Version)} this method implements    * supplementary character awareness when filling the given buffer. For all    * {@link Version}&gt; 3.0 {@link #fill(CharacterBuffer, Reader)} guarantees    * that the given {@link CharacterBuffer} will never contain a high surrogate    * character as the last element in the buffer unless it is the last available    * character in the reader. In other words, high and low surrogate pairs will    * always be preserved across buffer boarders.    *</p>    *     * @param buffer    *          the buffer to fill.    * @param reader    *          the reader to read characters from.    * @return<code>true</code> if and only if no more characters are available    *         in the reader, otherwise<code>false</code>.    * @throws IOException    *           if the reader throws an {@link IOException}.    */
DECL|method|fill
specifier|public
specifier|abstract
name|boolean
name|fill
parameter_list|(
name|CharacterBuffer
name|buffer
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|class|Java5CharacterUtils
specifier|private
specifier|static
specifier|final
class|class
name|Java5CharacterUtils
extends|extends
name|CharacterUtils
block|{
DECL|method|Java5CharacterUtils
name|Java5CharacterUtils
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|codePointAt
specifier|public
name|int
name|codePointAt
parameter_list|(
specifier|final
name|char
index|[]
name|chars
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
name|Character
operator|.
name|codePointAt
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|codePointAt
specifier|public
name|int
name|codePointAt
parameter_list|(
specifier|final
name|CharSequence
name|seq
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
name|Character
operator|.
name|codePointAt
argument_list|(
name|seq
argument_list|,
name|offset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|codePointAt
specifier|public
name|int
name|codePointAt
parameter_list|(
specifier|final
name|char
index|[]
name|chars
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
block|{
return|return
name|Character
operator|.
name|codePointAt
argument_list|(
name|chars
argument_list|,
name|offset
argument_list|,
name|limit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|public
name|boolean
name|fill
parameter_list|(
specifier|final
name|CharacterBuffer
name|buffer
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|char
index|[]
name|charBuffer
init|=
name|buffer
operator|.
name|buffer
decl_stmt|;
name|buffer
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
specifier|final
name|int
name|offset
decl_stmt|;
comment|// Install the previously saved ending high surrogate:
if|if
condition|(
name|buffer
operator|.
name|lastTrailingHighSurrogate
operator|!=
literal|0
condition|)
block|{
name|charBuffer
index|[
literal|0
index|]
operator|=
name|buffer
operator|.
name|lastTrailingHighSurrogate
expr_stmt|;
name|offset
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|int
name|read
init|=
name|reader
operator|.
name|read
argument_list|(
name|charBuffer
argument_list|,
name|offset
argument_list|,
name|charBuffer
operator|.
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|length
operator|=
name|offset
expr_stmt|;
name|buffer
operator|.
name|lastTrailingHighSurrogate
operator|=
literal|0
expr_stmt|;
return|return
name|offset
operator|!=
literal|0
return|;
block|}
assert|assert
name|read
operator|>
literal|0
assert|;
name|buffer
operator|.
name|length
operator|=
name|read
operator|+
name|offset
expr_stmt|;
comment|// If we read only a single char, and that char was a
comment|// high surrogate, read again:
if|if
condition|(
name|buffer
operator|.
name|length
operator|==
literal|1
operator|&&
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|charBuffer
index|[
name|buffer
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
specifier|final
name|int
name|read2
init|=
name|reader
operator|.
name|read
argument_list|(
name|charBuffer
argument_list|,
literal|1
argument_list|,
name|charBuffer
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|read2
operator|==
operator|-
literal|1
condition|)
block|{
comment|// NOTE: mal-formed input (ended on a high
comment|// surrogate)!  Consumer must deal with it...
return|return
literal|true
return|;
block|}
assert|assert
name|read2
operator|>
literal|0
assert|;
name|buffer
operator|.
name|length
operator|+=
name|read2
expr_stmt|;
block|}
if|if
condition|(
name|buffer
operator|.
name|length
operator|>
literal|1
operator|&&
name|Character
operator|.
name|isHighSurrogate
argument_list|(
name|charBuffer
index|[
name|buffer
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|lastTrailingHighSurrogate
operator|=
name|charBuffer
index|[
operator|--
name|buffer
operator|.
name|length
index|]
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|lastTrailingHighSurrogate
operator|=
literal|0
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|class|Java4CharacterUtils
specifier|private
specifier|static
specifier|final
class|class
name|Java4CharacterUtils
extends|extends
name|CharacterUtils
block|{
DECL|method|Java4CharacterUtils
name|Java4CharacterUtils
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|codePointAt
specifier|public
name|int
name|codePointAt
parameter_list|(
specifier|final
name|char
index|[]
name|chars
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
name|chars
index|[
name|offset
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|codePointAt
specifier|public
name|int
name|codePointAt
parameter_list|(
specifier|final
name|CharSequence
name|seq
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
name|seq
operator|.
name|charAt
argument_list|(
name|offset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|codePointAt
specifier|public
name|int
name|codePointAt
parameter_list|(
specifier|final
name|char
index|[]
name|chars
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|>=
name|limit
condition|)
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"offset must be less than limit"
argument_list|)
throw|;
return|return
name|chars
index|[
name|offset
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|public
name|boolean
name|fill
parameter_list|(
specifier|final
name|CharacterBuffer
name|buffer
parameter_list|,
specifier|final
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
specifier|final
name|int
name|read
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
operator|.
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|==
operator|-
literal|1
condition|)
return|return
literal|false
return|;
name|buffer
operator|.
name|length
operator|=
name|read
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * A simple IO buffer to use with    * {@link CharacterUtils#fill(CharacterBuffer, Reader)}.    */
DECL|class|CharacterBuffer
specifier|public
specifier|static
specifier|final
class|class
name|CharacterBuffer
block|{
DECL|field|buffer
specifier|private
specifier|final
name|char
index|[]
name|buffer
decl_stmt|;
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
comment|// NOTE: not private so outer class can access without
comment|// $access methods:
DECL|field|lastTrailingHighSurrogate
name|char
name|lastTrailingHighSurrogate
decl_stmt|;
DECL|method|CharacterBuffer
name|CharacterBuffer
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|/**      * Returns the internal buffer      *       * @return the buffer      */
DECL|method|getBuffer
specifier|public
name|char
index|[]
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
comment|/**      * Returns the data offset in the internal buffer.      *       * @return the offset      */
DECL|method|getOffset
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**      * Return the length of the data in the internal buffer starting at      * {@link #getOffset()}      *       * @return the length      */
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**      * Resets the CharacterBuffer. All internals are reset to its default      * values.      */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|offset
operator|=
literal|0
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|lastTrailingHighSurrogate
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

