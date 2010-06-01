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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AttributeSource
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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * Tokenizes the input from an edge into n-grams of given size(s).  *<p>  * This {@link Tokenizer} create n-grams from the beginning edge or ending edge of a input token.  * MaxGram can't be larger than 1024 because of limitation.  *</p>  */
end_comment

begin_class
DECL|class|EdgeNGramTokenizer
specifier|public
specifier|final
class|class
name|EdgeNGramTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|DEFAULT_SIDE
specifier|public
specifier|static
specifier|final
name|Side
name|DEFAULT_SIDE
init|=
name|Side
operator|.
name|FRONT
decl_stmt|;
DECL|field|DEFAULT_MAX_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_GRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MIN_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_GRAM_SIZE
init|=
literal|1
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
comment|/** Specifies which side of the input the n-gram should be generated from */
DECL|enum|Side
specifier|public
specifier|static
enum|enum
name|Side
block|{
comment|/** Get the n-gram from the front of the input */
DECL|enum constant|FRONT
name|FRONT
block|{
annotation|@
name|Override
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
literal|"front"
return|;
block|}
block|}
block|,
comment|/** Get the n-gram from the end of the input */
DECL|enum constant|BACK
name|BACK
block|{
annotation|@
name|Override
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
literal|"back"
return|;
block|}
block|}
block|;
DECL|method|getLabel
specifier|public
specifier|abstract
name|String
name|getLabel
parameter_list|()
function_decl|;
comment|// Get the appropriate Side from a string
DECL|method|getSide
specifier|public
specifier|static
name|Side
name|getSide
parameter_list|(
name|String
name|sideName
parameter_list|)
block|{
if|if
condition|(
name|FRONT
operator|.
name|getLabel
argument_list|()
operator|.
name|equals
argument_list|(
name|sideName
argument_list|)
condition|)
block|{
return|return
name|FRONT
return|;
block|}
if|if
condition|(
name|BACK
operator|.
name|getLabel
argument_list|()
operator|.
name|equals
argument_list|(
name|sideName
argument_list|)
condition|)
block|{
return|return
name|BACK
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|field|minGram
specifier|private
name|int
name|minGram
decl_stmt|;
DECL|field|maxGram
specifier|private
name|int
name|maxGram
decl_stmt|;
DECL|field|gramSize
specifier|private
name|int
name|gramSize
decl_stmt|;
DECL|field|side
specifier|private
name|Side
name|side
decl_stmt|;
DECL|field|started
specifier|private
name|boolean
name|started
init|=
literal|false
decl_stmt|;
DECL|field|inLen
specifier|private
name|int
name|inLen
decl_stmt|;
DECL|field|inStr
specifier|private
name|String
name|inStr
decl_stmt|;
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param input {@link Reader} holding the input to be tokenized    * @param side the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|Side
name|side
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
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|side
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param source {@link AttributeSource} to use    * @param input {@link Reader} holding the input to be tokenized    * @param side the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|,
name|Side
name|side
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
name|source
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|side
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *     * @param factory {@link org.apache.lucene.util.AttributeSource.AttributeFactory} to use    * @param input {@link Reader} holding the input to be tokenized    * @param side the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|Side
name|side
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
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|side
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param input {@link Reader} holding the input to be tokenized    * @param sideLabel the name of the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|String
name|sideLabel
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|Side
operator|.
name|getSide
argument_list|(
name|sideLabel
argument_list|)
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param source {@link AttributeSource} to use    * @param input {@link Reader} holding the input to be tokenized    * @param sideLabel the name of the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|,
name|String
name|sideLabel
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|this
argument_list|(
name|source
argument_list|,
name|input
argument_list|,
name|Side
operator|.
name|getSide
argument_list|(
name|sideLabel
argument_list|)
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *     * @param factory {@link org.apache.lucene.util.AttributeSource.AttributeFactory} to use    * @param input {@link Reader} holding the input to be tokenized    * @param sideLabel the name of the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|,
name|String
name|sideLabel
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|this
argument_list|(
name|factory
argument_list|,
name|input
argument_list|,
name|Side
operator|.
name|getSide
argument_list|(
name|sideLabel
argument_list|)
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Side
name|side
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
if|if
condition|(
name|side
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"sideLabel must be either front or back"
argument_list|)
throw|;
block|}
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
name|this
operator|.
name|side
operator|=
name|side
expr_stmt|;
block|}
comment|/** Returns the next token in the stream, or null at EOS. */
annotation|@
name|Override
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
comment|// if we are just starting, read the whole input
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
name|int
name|charsRead
init|=
name|input
operator|.
name|read
argument_list|(
name|chars
argument_list|)
decl_stmt|;
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
comment|// remove any leading or trailing spaces
name|inLen
operator|=
name|inStr
operator|.
name|length
argument_list|()
expr_stmt|;
name|gramSize
operator|=
name|minGram
expr_stmt|;
block|}
comment|// if the remaining input is too short, we can't generate any n-grams
if|if
condition|(
name|gramSize
operator|>
name|inLen
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// if we have hit the end of our n-gram size range, quit
if|if
condition|(
name|gramSize
operator|>
name|maxGram
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// grab gramSize chars from front or back
name|int
name|start
init|=
name|side
operator|==
name|Side
operator|.
name|FRONT
condition|?
literal|0
else|:
name|inLen
operator|-
name|gramSize
decl_stmt|;
name|int
name|end
init|=
name|start
operator|+
name|gramSize
decl_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|inStr
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|end
argument_list|)
argument_list|)
expr_stmt|;
name|gramSize
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
specifier|final
name|int
name|finalOffset
init|=
name|inLen
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
name|reset
argument_list|()
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
block|}
block|}
end_class

end_unit

