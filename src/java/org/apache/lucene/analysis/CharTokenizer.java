begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|TermAttribute
import|;
end_import

begin_comment
comment|/** An abstract base class for simple, character-oriented tokenizers.*/
end_comment

begin_class
DECL|class|CharTokenizer
specifier|public
specifier|abstract
class|class
name|CharTokenizer
extends|extends
name|Tokenizer
block|{
DECL|method|CharTokenizer
specifier|public
name|CharTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|offset
DECL|field|bufferIndex
DECL|field|dataLen
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|,
name|bufferIndex
init|=
literal|0
decl_stmt|,
name|dataLen
init|=
literal|0
decl_stmt|;
DECL|field|MAX_WORD_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MAX_WORD_LEN
init|=
literal|255
decl_stmt|;
DECL|field|IO_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|IO_BUFFER_SIZE
init|=
literal|4096
decl_stmt|;
DECL|field|ioBuffer
specifier|private
specifier|final
name|char
index|[]
name|ioBuffer
init|=
operator|new
name|char
index|[
name|IO_BUFFER_SIZE
index|]
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
comment|/** Returns true iff a character should be included in a token.  This    * tokenizer generates as tokens adjacent sequences of characters which    * satisfy this predicate.  Characters for which this is false are used to    * define token boundaries and are not included in tokens. */
DECL|method|isTokenChar
specifier|protected
specifier|abstract
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
function_decl|;
comment|/** Called on each token character to normalize it before it is added to the    * token.  The default implementation does nothing.  Subclasses may use this    * to, e.g., lowercase tokens. */
DECL|method|normalize
specifier|protected
name|char
name|normalize
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
name|c
return|;
block|}
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
name|int
name|start
init|=
name|bufferIndex
decl_stmt|;
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|bufferIndex
operator|>=
name|dataLen
condition|)
block|{
name|offset
operator|+=
name|dataLen
expr_stmt|;
name|dataLen
operator|=
name|input
operator|.
name|read
argument_list|(
name|ioBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
break|break;
else|else
return|return
literal|false
return|;
block|}
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|char
name|c
init|=
name|ioBuffer
index|[
name|bufferIndex
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|isTokenChar
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// if it's a token char
if|if
condition|(
name|length
operator|==
literal|0
condition|)
comment|// start of token
name|start
operator|=
name|offset
operator|+
name|bufferIndex
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|length
operator|==
name|buffer
operator|.
name|length
condition|)
name|buffer
operator|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
literal|1
operator|+
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|normalize
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// buffer it, normalized
if|if
condition|(
name|length
operator|==
name|MAX_WORD_LEN
condition|)
comment|// buffer overflow!
break|break;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
comment|// at non-Letter w/ chars
break|break;
comment|// return 'em
block|}
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setStartOffset
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setEndOffset
argument_list|(
name|start
operator|+
name|length
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** @deprecated */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|(
specifier|final
name|Token
name|reusableToken
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|reusableToken
operator|!=
literal|null
assert|;
name|reusableToken
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
name|int
name|start
init|=
name|bufferIndex
decl_stmt|;
name|char
index|[]
name|buffer
init|=
name|reusableToken
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|bufferIndex
operator|>=
name|dataLen
condition|)
block|{
name|offset
operator|+=
name|dataLen
expr_stmt|;
name|dataLen
operator|=
name|input
operator|.
name|read
argument_list|(
name|ioBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataLen
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|length
operator|>
literal|0
condition|)
break|break;
else|else
return|return
literal|null
return|;
block|}
name|bufferIndex
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|char
name|c
init|=
name|ioBuffer
index|[
name|bufferIndex
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|isTokenChar
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// if it's a token char
if|if
condition|(
name|length
operator|==
literal|0
condition|)
comment|// start of token
name|start
operator|=
name|offset
operator|+
name|bufferIndex
operator|-
literal|1
expr_stmt|;
elseif|else
if|if
condition|(
name|length
operator|==
name|buffer
operator|.
name|length
condition|)
name|buffer
operator|=
name|reusableToken
operator|.
name|resizeTermBuffer
argument_list|(
literal|1
operator|+
name|length
argument_list|)
expr_stmt|;
name|buffer
index|[
name|length
operator|++
index|]
operator|=
name|normalize
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// buffer it, normalized
if|if
condition|(
name|length
operator|==
name|MAX_WORD_LEN
condition|)
comment|// buffer overflow!
break|break;
block|}
elseif|else
if|if
condition|(
name|length
operator|>
literal|0
condition|)
comment|// at non-Letter w/ chars
break|break;
comment|// return 'em
block|}
name|reusableToken
operator|.
name|setTermLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setStartOffset
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|reusableToken
operator|.
name|setEndOffset
argument_list|(
name|start
operator|+
name|length
argument_list|)
expr_stmt|;
return|return
name|reusableToken
return|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|bufferIndex
operator|=
literal|0
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|dataLen
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

