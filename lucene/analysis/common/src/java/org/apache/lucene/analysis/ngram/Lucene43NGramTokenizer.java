begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Tokenizer
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
name|util
operator|.
name|AttributeFactory
import|;
end_import

begin_comment
comment|/**  * Old broken version of {@link NGramTokenizer}.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene43NGramTokenizer
specifier|public
specifier|final
class|class
name|Lucene43NGramTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_MIN_NGRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_NGRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MAX_NGRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_NGRAM_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|minGram
DECL|field|maxGram
specifier|private
name|int
name|minGram
decl_stmt|,
name|maxGram
decl_stmt|;
DECL|field|gramSize
specifier|private
name|int
name|gramSize
decl_stmt|;
DECL|field|pos
specifier|private
name|int
name|pos
decl_stmt|;
DECL|field|inLen
specifier|private
name|int
name|inLen
decl_stmt|;
comment|// length of the input AFTER trim()
DECL|field|charsRead
specifier|private
name|int
name|charsRead
decl_stmt|;
comment|// length of the input
DECL|field|inStr
specifier|private
name|String
name|inStr
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
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
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates NGramTokenizer with given min and max n-grams.    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|Lucene43NGramTokenizer
specifier|public
name|Lucene43NGramTokenizer
parameter_list|(
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|init
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates NGramTokenizer with given min and max n-grams.    * @param factory {@link org.apache.lucene.util.AttributeFactory} to use    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|Lucene43NGramTokenizer
specifier|public
name|Lucene43NGramTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates NGramTokenizer with default min and max n-grams.    */
DECL|method|Lucene43NGramTokenizer
specifier|public
name|Lucene43NGramTokenizer
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_MIN_NGRAM_SIZE
argument_list|,
name|DEFAULT_MAX_NGRAM_SIZE
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
if|if
condition|(
name|minGram
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must be greater than zero"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minGram
operator|>
name|maxGram
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minGram must not be greater than maxGram"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minGram
operator|=
name|minGram
expr_stmt|;
name|this
operator|.
name|maxGram
operator|=
name|maxGram
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
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
name|clearAttributes
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|started
condition|)
block|{
name|started
operator|=
literal|true
expr_stmt|;
name|gramSize
operator|=
name|minGram
expr_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|charsRead
operator|=
literal|0
expr_stmt|;
comment|// TODO: refactor to a shared readFully somewhere:
while|while
condition|(
name|charsRead
operator|<
name|chars
operator|.
name|length
condition|)
block|{
name|int
name|inc
init|=
name|input
operator|.
name|read
argument_list|(
name|chars
argument_list|,
name|charsRead
argument_list|,
name|chars
operator|.
name|length
operator|-
name|charsRead
argument_list|)
decl_stmt|;
if|if
condition|(
name|inc
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|charsRead
operator|+=
name|inc
expr_stmt|;
block|}
name|inStr
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|charsRead
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// remove any trailing empty strings
if|if
condition|(
name|charsRead
operator|==
name|chars
operator|.
name|length
condition|)
block|{
comment|// Read extra throwaway chars so that on end() we
comment|// report the correct offset:
name|char
index|[]
name|throwaway
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|inc
init|=
name|input
operator|.
name|read
argument_list|(
name|throwaway
argument_list|,
literal|0
argument_list|,
name|throwaway
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|inc
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|charsRead
operator|+=
name|inc
expr_stmt|;
block|}
block|}
name|inLen
operator|=
name|inStr
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|inLen
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|pos
operator|+
name|gramSize
operator|>
name|inLen
condition|)
block|{
comment|// if we hit the end of the string
name|pos
operator|=
literal|0
expr_stmt|;
comment|// reset to beginning of string
name|gramSize
operator|++
expr_stmt|;
comment|// increase n-gram size
if|if
condition|(
name|gramSize
operator|>
name|maxGram
condition|)
comment|// we are done
return|return
literal|false
return|;
if|if
condition|(
name|pos
operator|+
name|gramSize
operator|>
name|inLen
condition|)
return|return
literal|false
return|;
block|}
name|int
name|oldPos
init|=
name|pos
decl_stmt|;
name|pos
operator|++
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|inStr
argument_list|,
name|oldPos
argument_list|,
name|oldPos
operator|+
name|gramSize
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|oldPos
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|oldPos
operator|+
name|gramSize
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
name|void
name|end
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// set final offset
specifier|final
name|int
name|finalOffset
init|=
name|correctOffset
argument_list|(
name|charsRead
argument_list|)
decl_stmt|;
name|this
operator|.
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|started
operator|=
literal|false
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit

