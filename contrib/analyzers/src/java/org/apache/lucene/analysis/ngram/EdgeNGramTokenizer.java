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
name|Tokenizer
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
comment|/**  * Tokenizes the input into n-grams of the given size.  * @author Otis Gospodnetic  */
end_comment

begin_class
DECL|class|EdgeNGramTokenizer
specifier|public
class|class
name|EdgeNGramTokenizer
extends|extends
name|Tokenizer
block|{
comment|// which side to get the n-gram from
comment|// TODO: switch to using this enum when we move to 1.5+
comment|//  public enum Side {
comment|//    FRONT (),
comment|//    BACK ();
comment|//  }
comment|/** Specifies which side of the input the n-gram should be generated from */
DECL|class|Side
specifier|public
specifier|static
class|class
name|Side
block|{
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
DECL|method|Side
specifier|private
name|Side
parameter_list|(
name|String
name|label
parameter_list|)
block|{}
block|}
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
DECL|field|started
specifier|private
name|boolean
name|started
init|=
literal|false
decl_stmt|;
comment|/**    * Creates EdgeNGramTokenizer that can generate an n-gram of the given size.    * @param input Reader holding the input to be tokenized    * @param side the {@link Side} from which to chop off an n-gram     * @param gramSize the size of the n-gram to generate    */
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
name|gramSize
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|gramSize
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"gramSize must be greater than zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|gramSize
operator|=
name|gramSize
expr_stmt|;
name|this
operator|.
name|side
operator|=
name|side
expr_stmt|;
block|}
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|Reader
name|input
parameter_list|,
name|String
name|side
parameter_list|,
name|int
name|gramSize
parameter_list|)
block|{    }
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
comment|// if we already returned the edge n-gram, we are done
if|if
condition|(
name|started
condition|)
return|return
literal|null
return|;
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
name|input
operator|.
name|read
argument_list|(
name|chars
argument_list|)
expr_stmt|;
name|inStr
operator|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
comment|// remove any trailing empty strings
name|inLen
operator|=
name|inStr
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|// if the input is too short, we can't generate any n-grams
if|if
condition|(
name|gramSize
operator|>
name|inLen
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|side
operator|==
name|Side
operator|.
name|FRONT
condition|)
return|return
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
return|;
else|else
return|return
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
return|;
block|}
DECL|method|side
specifier|static
name|Side
name|side
parameter_list|(
name|String
name|label
parameter_list|)
block|{
if|if
condition|(
name|label
operator|==
literal|null
operator|||
name|label
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Label must be either 'front' or 'back'"
argument_list|)
throw|;
if|if
condition|(
name|label
operator|.
name|equals
argument_list|(
literal|"front"
argument_list|)
condition|)
return|return
name|Side
operator|.
name|FRONT
return|;
else|else
return|return
name|Side
operator|.
name|BACK
return|;
block|}
block|}
end_class

end_unit

