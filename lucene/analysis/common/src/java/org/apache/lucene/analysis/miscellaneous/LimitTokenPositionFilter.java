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
name|PositionIncrementAttribute
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
comment|/**  * This TokenFilter limits its emitted tokens to those with positions that  * are not greater than the configured limit.  *<p>  * By default, this filter ignores any tokens in the wrapped {@code TokenStream}  * once the limit has been exceeded, which can result in {@code reset()} being   * called prior to {@code incrementToken()} returning {@code false}.  For most   * {@code TokenStream} implementations this should be acceptable, and faster   * then consuming the full stream. If you are wrapping a {@code TokenStream}  * which requires that the full stream of tokens be exhausted in order to   * function properly, use the   * {@link #LimitTokenPositionFilter(TokenStream,int,boolean) consumeAllTokens}  * option.  */
end_comment

begin_class
DECL|class|LimitTokenPositionFilter
specifier|public
specifier|final
class|class
name|LimitTokenPositionFilter
extends|extends
name|TokenFilter
block|{
DECL|field|maxTokenPosition
specifier|private
specifier|final
name|int
name|maxTokenPosition
decl_stmt|;
DECL|field|consumeAllTokens
specifier|private
specifier|final
name|boolean
name|consumeAllTokens
decl_stmt|;
DECL|field|tokenPosition
specifier|private
name|int
name|tokenPosition
init|=
literal|0
decl_stmt|;
DECL|field|exhausted
specifier|private
name|boolean
name|exhausted
init|=
literal|false
decl_stmt|;
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Build a filter that only accepts tokens up to and including the given maximum position.    * This filter will not consume any tokens with position greater than the maxTokenPosition limit.     * @param in the stream to wrap    * @param maxTokenPosition max position of tokens to produce (1st token always has position 1)    *                             * @see #LimitTokenPositionFilter(TokenStream,int,boolean)    */
DECL|method|LimitTokenPositionFilter
specifier|public
name|LimitTokenPositionFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|maxTokenPosition
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|maxTokenPosition
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build a filter that limits the maximum position of tokens to emit.    *     * @param in the stream to wrap    * @param maxTokenPosition max position of tokens to produce (1st token always has position 1)    * @param consumeAllTokens whether all tokens from the wrapped input stream must be consumed    *                         even if maxTokenPosition is exceeded.    */
DECL|method|LimitTokenPositionFilter
specifier|public
name|LimitTokenPositionFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|maxTokenPosition
parameter_list|,
name|boolean
name|consumeAllTokens
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxTokenPosition
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTokenPosition must be greater than zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxTokenPosition
operator|=
name|maxTokenPosition
expr_stmt|;
name|this
operator|.
name|consumeAllTokens
operator|=
name|consumeAllTokens
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
name|exhausted
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|tokenPosition
operator|+=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
if|if
condition|(
name|tokenPosition
operator|<=
name|maxTokenPosition
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
while|while
condition|(
name|consumeAllTokens
operator|&&
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|/* NOOP */
block|}
name|exhausted
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
name|exhausted
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
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
name|tokenPosition
operator|=
literal|0
expr_stmt|;
name|exhausted
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

