begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
package|;
end_package

begin_comment
comment|/**  * A simple StringBuffer replacement that aims to   * reduce copying as much as possible. The buffer  * grows as necessary.  * This class is not thread safe.  */
end_comment

begin_class
DECL|class|CharBuffer
specifier|public
class|class
name|CharBuffer
block|{
DECL|field|c
specifier|private
name|char
index|[]
name|c
decl_stmt|;
comment|/**      * Actually used number of characters in the array.       * It is also the index at which      * a new character will be inserted into<code>c</code>.       */
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
comment|/**      * Creates a new CharBuffer with an initial capacity of 32 characters.      */
DECL|method|CharBuffer
specifier|public
name|CharBuffer
parameter_list|()
block|{
name|this
argument_list|(
literal|32
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new CharBuffer with an initial capacity       * of<code>length</code> characters.      */
DECL|method|CharBuffer
specifier|public
name|CharBuffer
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't create an empty CharBuffer"
argument_list|)
throw|;
block|}
name|this
operator|.
name|c
operator|=
operator|new
name|char
index|[
name|length
index|]
expr_stmt|;
block|}
comment|/**      * Empties the buffer. The capacity still remains the same, so no memory is freed.      */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|length
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * Returns the number of characters in the buffer.      * @return the number of characters      */
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**      * Returns the current capacity of the buffer.      * @return the maximum number of characters that can be stored in this buffer without      * resizing it.      */
DECL|method|capacity
specifier|public
name|int
name|capacity
parameter_list|()
block|{
return|return
name|c
operator|.
name|length
return|;
block|}
comment|/**      * Appends the contents of<code>cb</code> to the end of this CharBuffer.      * @param cb the CharBuffer to append or null      */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|CharBuffer
name|cb
parameter_list|)
block|{
if|if
condition|(
name|cb
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|provideCapacity
argument_list|(
name|length
operator|+
name|cb
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|cb
operator|.
name|c
argument_list|,
literal|0
argument_list|,
name|c
argument_list|,
name|length
argument_list|,
name|cb
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|+=
name|cb
operator|.
name|length
expr_stmt|;
block|}
comment|/**      * Appends<code>s</code> to the end of this CharBuffer.      * This method involves copying the new data once!      * @param s the String to append or null      */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|append
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Appends<code>sb</code> to the end of this CharBuffer.      * This method involves copying the new data once!      * @param sb the StringBuffer to append or null      */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|StringBuffer
name|sb
parameter_list|)
block|{
if|if
condition|(
name|sb
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|provideCapacity
argument_list|(
name|length
operator|+
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|,
name|c
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|length
operator|+=
name|sb
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|/**      * Appends<code>data</code> to the end of this CharBuffer.      * This method involves copying the new data once!      * @param data the char[] to append or null      */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|char
index|[]
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|provideCapacity
argument_list|(
name|length
operator|+
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|c
argument_list|,
name|length
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|length
operator|+=
name|data
operator|.
name|length
expr_stmt|;
block|}
comment|/**      * Appends a single character to the end of this CharBuffer.      * This method involves copying the new data once!      * @param data the char to append      */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|char
name|data
parameter_list|)
block|{
name|provideCapacity
argument_list|(
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|c
index|[
name|length
index|]
operator|=
name|data
expr_stmt|;
name|length
operator|++
expr_stmt|;
block|}
comment|/**      * Shrinks the capacity of the buffer to the current length if necessary.      * This method involves copying the data once!      */
DECL|method|shrink
specifier|public
name|void
name|shrink
parameter_list|()
block|{
if|if
condition|(
name|c
operator|.
name|length
operator|==
name|length
condition|)
block|{
return|return;
block|}
name|char
index|[]
name|newc
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|newc
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|c
operator|=
name|newc
expr_stmt|;
block|}
comment|/**     * Removes trailing whitespace.     */
DECL|method|trimTrailingWhitespace
specifier|public
name|void
name|trimTrailingWhitespace
parameter_list|()
block|{
while|while
condition|(
name|length
operator|>
literal|0
operator|&&
name|Character
operator|.
name|isWhitespace
argument_list|(
name|c
index|[
name|length
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
name|length
operator|--
expr_stmt|;
block|}
block|}
comment|/**      * Returns the contents of the buffer as a char[]. The returned array may      * be the internal array of the buffer, so the caller must take care when      * modifying it.      * This method allows to avoid copying if the caller knows the exact capacity      * before.      */
DECL|method|getCharacters
specifier|public
name|char
index|[]
name|getCharacters
parameter_list|()
block|{
if|if
condition|(
name|c
operator|.
name|length
operator|==
name|length
condition|)
block|{
return|return
name|c
return|;
block|}
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|chars
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|chars
return|;
block|}
comment|/**     * Returns the character at the specified position.     */
DECL|method|charAt
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|c
index|[
name|pos
index|]
return|;
block|}
comment|/**      * Converts the contents of the buffer into a StringBuffer.      * This method involves copying the new data once!      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**      * Copies the data into a new array of at least<code>capacity</code> size.      */
DECL|method|provideCapacity
specifier|public
name|void
name|provideCapacity
parameter_list|(
specifier|final
name|int
name|capacity
parameter_list|)
block|{
if|if
condition|(
name|c
operator|.
name|length
operator|>=
name|capacity
condition|)
block|{
return|return;
block|}
name|int
name|newcapacity
init|=
operator|(
operator|(
name|capacity
operator|*
literal|3
operator|)
operator|>>
literal|1
operator|)
operator|+
literal|1
decl_stmt|;
name|char
index|[]
name|newc
init|=
operator|new
name|char
index|[
name|newcapacity
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|c
argument_list|,
literal|0
argument_list|,
name|newc
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|c
operator|=
name|newc
expr_stmt|;
block|}
block|}
end_class

end_unit

