begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|analysis
operator|.
name|CharArraySet
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
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import

begin_comment
comment|/**   * A filter to apply normal capitalization rules to Tokens.  It will make the first letter  * capital and the rest lower case.  *<p>  * This filter is particularly useful to build nice looking facet parameters.  This filter  * is not appropriate if you intend to use a prefix query.  */
end_comment

begin_class
DECL|class|CapitalizationFilter
specifier|public
specifier|final
class|class
name|CapitalizationFilter
extends|extends
name|TokenFilter
block|{
DECL|field|DEFAULT_MAX_WORD_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_WORD_COUNT
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|DEFAULT_MAX_TOKEN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_TOKEN_LENGTH
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|onlyFirstWord
specifier|private
specifier|final
name|boolean
name|onlyFirstWord
decl_stmt|;
DECL|field|keep
specifier|private
specifier|final
name|CharArraySet
name|keep
decl_stmt|;
DECL|field|forceFirstLetter
specifier|private
specifier|final
name|boolean
name|forceFirstLetter
decl_stmt|;
DECL|field|okPrefix
specifier|private
specifier|final
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
decl_stmt|;
DECL|field|minWordLength
specifier|private
specifier|final
name|int
name|minWordLength
decl_stmt|;
DECL|field|maxWordCount
specifier|private
specifier|final
name|int
name|maxWordCount
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
specifier|final
name|int
name|maxTokenLength
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates a CapitalizationFilter with the default parameters.    *<p>    * Calls {@link #CapitalizationFilter(TokenStream, boolean, CharArraySet, boolean, Collection, int, int, int)    *   CapitalizationFilter(in, true, null, true, null, 0, DEFAULT_MAX_WORD_COUNT, DEFAULT_MAX_TOKEN_LENGTH)}    */
DECL|method|CapitalizationFilter
specifier|public
name|CapitalizationFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|DEFAULT_MAX_WORD_COUNT
argument_list|,
name|DEFAULT_MAX_TOKEN_LENGTH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a CapitalizationFilter with the specified parameters.    * @param in input tokenstream     * @param onlyFirstWord should each word be capitalized or all of the words?    * @param keep a keep word list.  Each word that should be kept separated by whitespace.    * @param forceFirstLetter Force the first letter to be capitalized even if it is in the keep list.    * @param okPrefix do not change word capitalization if a word begins with something in this list.    * @param minWordLength how long the word needs to be to get capitalization applied.  If the    *                      minWordLength is 3, "and"&gt; "And" but "or" stays "or".    * @param maxWordCount if the token contains more then maxWordCount words, the capitalization is    *                     assumed to be correct.    * @param maxTokenLength ???    */
DECL|method|CapitalizationFilter
specifier|public
name|CapitalizationFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|boolean
name|onlyFirstWord
parameter_list|,
name|CharArraySet
name|keep
parameter_list|,
name|boolean
name|forceFirstLetter
parameter_list|,
name|Collection
argument_list|<
name|char
index|[]
argument_list|>
name|okPrefix
parameter_list|,
name|int
name|minWordLength
parameter_list|,
name|int
name|maxWordCount
parameter_list|,
name|int
name|maxTokenLength
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|onlyFirstWord
operator|=
name|onlyFirstWord
expr_stmt|;
name|this
operator|.
name|keep
operator|=
name|keep
expr_stmt|;
name|this
operator|.
name|forceFirstLetter
operator|=
name|forceFirstLetter
expr_stmt|;
name|this
operator|.
name|okPrefix
operator|=
name|okPrefix
expr_stmt|;
if|if
condition|(
name|minWordLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minWordLength must be greater than or equal to zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxWordCount
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxWordCount must be greater than zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxTokenLength
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTokenLength must be greater than zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minWordLength
operator|=
name|minWordLength
expr_stmt|;
name|this
operator|.
name|maxWordCount
operator|=
name|maxWordCount
expr_stmt|;
name|this
operator|.
name|maxTokenLength
operator|=
name|maxTokenLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|termBufferLength
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|backup
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|maxWordCount
operator|<
name|DEFAULT_MAX_WORD_COUNT
condition|)
block|{
comment|//make a backup in case we exceed the word count
name|backup
operator|=
operator|new
name|char
index|[
name|termBufferLength
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|backup
argument_list|,
literal|0
argument_list|,
name|termBufferLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|termBufferLength
operator|<
name|maxTokenLength
condition|)
block|{
name|int
name|wordCount
init|=
literal|0
decl_stmt|;
name|int
name|lastWordStart
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
name|termBufferLength
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|termBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|<=
literal|' '
operator|||
name|c
operator|==
literal|'.'
condition|)
block|{
name|int
name|len
init|=
name|i
operator|-
name|lastWordStart
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|processWord
argument_list|(
name|termBuffer
argument_list|,
name|lastWordStart
argument_list|,
name|len
argument_list|,
name|wordCount
operator|++
argument_list|)
expr_stmt|;
name|lastWordStart
operator|=
name|i
operator|+
literal|1
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|// process the last word
if|if
condition|(
name|lastWordStart
operator|<
name|termBufferLength
condition|)
block|{
name|processWord
argument_list|(
name|termBuffer
argument_list|,
name|lastWordStart
argument_list|,
name|termBufferLength
operator|-
name|lastWordStart
argument_list|,
name|wordCount
operator|++
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|wordCount
operator|>
name|maxWordCount
condition|)
block|{
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|backup
argument_list|,
literal|0
argument_list|,
name|termBufferLength
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|processWord
specifier|private
name|void
name|processWord
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
parameter_list|,
name|int
name|wordCount
parameter_list|)
block|{
if|if
condition|(
name|length
operator|<
literal|1
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|onlyFirstWord
operator|&&
name|wordCount
operator|>
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|offset
operator|+
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|buffer
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|keep
operator|!=
literal|null
operator|&&
name|keep
operator|.
name|contains
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
condition|)
block|{
if|if
condition|(
name|wordCount
operator|==
literal|0
operator|&&
name|forceFirstLetter
condition|)
block|{
name|buffer
index|[
name|offset
index|]
operator|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|buffer
index|[
name|offset
index|]
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|length
operator|<
name|minWordLength
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|okPrefix
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|char
index|[]
name|prefix
range|:
name|okPrefix
control|)
block|{
if|if
condition|(
name|length
operator|>=
name|prefix
operator|.
name|length
condition|)
block|{
comment|//don't bother checking if the buffer length is less than the prefix
name|boolean
name|match
init|=
literal|true
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
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|prefix
index|[
name|i
index|]
operator|!=
name|buffer
index|[
name|offset
operator|+
name|i
index|]
condition|)
block|{
name|match
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|match
operator|==
literal|true
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
comment|// We know it has at least one character
comment|/*char[] chars = w.toCharArray();     StringBuilder word = new StringBuilder( w.length() );     word.append( Character.toUpperCase( chars[0] ) );*/
name|buffer
index|[
name|offset
index|]
operator|=
name|Character
operator|.
name|toUpperCase
argument_list|(
name|buffer
index|[
name|offset
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|offset
operator|+
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|buffer
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|//return word.toString();
block|}
block|}
end_class

end_unit

