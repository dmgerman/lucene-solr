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
name|Token
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
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * Tokenizes the given token into n-grams of given size(s).  *  * This filter create n-grams from the beginning edge or ending edge of a input token.  *   */
end_comment

begin_class
DECL|class|EdgeNGramTokenFilter
specifier|public
class|class
name|EdgeNGramTokenFilter
extends|extends
name|TokenFilter
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
comment|// Replace this with an enum when the Java 1.5 upgrade is made, the impl will be simplified
comment|/** Specifies which side of the input the n-gram should be generated from */
DECL|class|Side
specifier|public
specifier|static
class|class
name|Side
block|{
DECL|field|label
specifier|private
name|String
name|label
decl_stmt|;
comment|/** Get the n-gram from the front of the input */
DECL|field|FRONT
specifier|public
specifier|static
name|Side
name|FRONT
init|=
operator|new
name|Side
argument_list|(
literal|"front"
argument_list|)
decl_stmt|;
comment|/** Get the n-gram from the end of the input */
DECL|field|BACK
specifier|public
specifier|static
name|Side
name|BACK
init|=
operator|new
name|Side
argument_list|(
literal|"back"
argument_list|)
decl_stmt|;
comment|// Private ctor
DECL|method|Side
specifier|private
name|Side
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
block|}
DECL|method|getLabel
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
name|label
return|;
block|}
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
elseif|else
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
DECL|field|side
specifier|private
name|Side
name|side
decl_stmt|;
DECL|field|ngrams
specifier|private
name|LinkedList
name|ngrams
decl_stmt|;
DECL|method|EdgeNGramTokenFilter
specifier|protected
name|EdgeNGramTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|ngrams
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range    *    * @param input TokenStream holding the input to be tokenized    * @param side the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenFilter
specifier|public
name|EdgeNGramTokenFilter
parameter_list|(
name|TokenStream
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
name|this
operator|.
name|ngrams
operator|=
operator|new
name|LinkedList
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenFilter that can generate n-grams in the sizes of the given range    *    * @param input TokenStream holding the input to be tokenized    * @param sideLabel the name of the {@link Side} from which to chop off an n-gram    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenFilter
specifier|public
name|EdgeNGramTokenFilter
parameter_list|(
name|TokenStream
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
comment|/** Returns the next token in the stream, or null at EOS. */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|ngrams
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|Token
operator|)
name|ngrams
operator|.
name|removeFirst
argument_list|()
return|;
block|}
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ngram
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|ngrams
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
return|return
operator|(
name|Token
operator|)
name|ngrams
operator|.
name|removeFirst
argument_list|()
return|;
else|else
return|return
literal|null
return|;
block|}
DECL|method|ngram
specifier|private
name|void
name|ngram
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
name|String
name|inStr
init|=
name|token
operator|.
name|termText
argument_list|()
decl_stmt|;
name|int
name|inLen
init|=
name|inStr
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|gramSize
init|=
name|minGram
decl_stmt|;
while|while
condition|(
name|gramSize
operator|<=
name|maxGram
condition|)
block|{
comment|// if the remaining input is too short, we can't generate any n-grams
if|if
condition|(
name|gramSize
operator|>
name|inLen
condition|)
block|{
return|return;
block|}
comment|// if we have hit the end of our n-gram size range, quit
if|if
condition|(
name|gramSize
operator|>
name|maxGram
condition|)
block|{
return|return;
block|}
name|Token
name|tok
decl_stmt|;
if|if
condition|(
name|side
operator|==
name|Side
operator|.
name|FRONT
condition|)
block|{
name|tok
operator|=
operator|new
name|Token
argument_list|(
name|inStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|gramSize
argument_list|)
argument_list|,
literal|0
argument_list|,
name|gramSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tok
operator|=
operator|new
name|Token
argument_list|(
name|inStr
operator|.
name|substring
argument_list|(
name|inLen
operator|-
name|gramSize
argument_list|)
argument_list|,
name|inLen
operator|-
name|gramSize
argument_list|,
name|inLen
argument_list|)
expr_stmt|;
block|}
name|ngrams
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
name|gramSize
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

