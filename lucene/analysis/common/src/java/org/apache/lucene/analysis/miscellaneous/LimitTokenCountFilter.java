begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * This TokenFilter limits the number of tokens while indexing. It is  * a replacement for the maximum field length setting inside {@link org.apache.lucene.index.IndexWriter}.  *<p>  * By default, this filter ignores any tokens in the wrapped {@code TokenStream}  * once the limit has been reached, which can result in {@code reset()} being   * called prior to {@code incrementToken()} returning {@code false}.  For most   * {@code TokenStream} implementations this should be acceptable, and faster   * then consuming the full stream. If you are wrapping a {@code TokenStream}   * which requires that the full stream of tokens be exhausted in order to   * function properly, use the   * {@link #LimitTokenCountFilter(TokenStream,int,boolean) consumeAllTokens}   * option.  */
end_comment

begin_class
DECL|class|LimitTokenCountFilter
specifier|public
specifier|final
class|class
name|LimitTokenCountFilter
extends|extends
name|TokenFilter
block|{
DECL|field|maxTokenCount
specifier|private
specifier|final
name|int
name|maxTokenCount
decl_stmt|;
DECL|field|consumeAllTokens
specifier|private
specifier|final
name|boolean
name|consumeAllTokens
decl_stmt|;
DECL|field|tokenCount
specifier|private
name|int
name|tokenCount
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
comment|/**    * Build a filter that only accepts tokens up to a maximum number.    * This filter will not consume any tokens beyond the maxTokenCount limit    *    * @see #LimitTokenCountFilter(TokenStream,int,boolean)    */
DECL|method|LimitTokenCountFilter
specifier|public
name|LimitTokenCountFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|maxTokenCount
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|maxTokenCount
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build an filter that limits the maximum number of tokens per field.    * @param in the stream to wrap    * @param maxTokenCount max number of tokens to produce    * @param consumeAllTokens whether all tokens from the input must be consumed even if maxTokenCount is reached.    */
DECL|method|LimitTokenCountFilter
specifier|public
name|LimitTokenCountFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|int
name|maxTokenCount
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
name|maxTokenCount
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxTokenCount must be greater than zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxTokenCount
operator|=
name|maxTokenCount
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
elseif|else
if|if
condition|(
name|tokenCount
operator|<
name|maxTokenCount
condition|)
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|tokenCount
operator|++
expr_stmt|;
return|return
literal|true
return|;
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
name|tokenCount
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

