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
name|OffsetAttribute
import|;
end_import

begin_comment
comment|/**  * Lets all tokens pass through until it sees one with a start offset&lt;= a  * configured limit, which won't pass and ends the stream.  This can be useful to  * limit highlighting, for example.  *<p>  * By default, this filter ignores any tokens in the wrapped {@code TokenStream}  * once the limit has been exceeded, which can result in {@code reset()} being  * called prior to {@code incrementToken()} returning {@code false}.  For most  * {@code TokenStream} implementations this should be acceptable, and faster  * then consuming the full stream. If you are wrapping a {@code TokenStream}  * which requires that the full stream of tokens be exhausted in order to  * function properly, use the  * {@link #LimitTokenOffsetFilter(TokenStream, int, boolean)} option.  */
end_comment

begin_class
DECL|class|LimitTokenOffsetFilter
specifier|public
specifier|final
class|class
name|LimitTokenOffsetFilter
extends|extends
name|TokenFilter
block|{
DECL|field|offsetAttrib
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAttrib
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxStartOffset
specifier|private
name|int
name|maxStartOffset
decl_stmt|;
DECL|field|consumeAllTokens
specifier|private
specifier|final
name|boolean
name|consumeAllTokens
decl_stmt|;
comment|// some day we may limit by end offset too but no need right now
comment|/**    * Lets all tokens pass through until it sees one with a start offset&lt;= {@code maxStartOffset}    * which won't pass and ends the stream. It won't consume any tokens afterwards.    *    * @param maxStartOffset the maximum start offset allowed    */
DECL|method|LimitTokenOffsetFilter
specifier|public
name|LimitTokenOffsetFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|maxStartOffset
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|maxStartOffset
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|LimitTokenOffsetFilter
specifier|public
name|LimitTokenOffsetFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|int
name|maxStartOffset
parameter_list|,
name|boolean
name|consumeAllTokens
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxStartOffset
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxStartOffset must be>= zero"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxStartOffset
operator|=
name|maxStartOffset
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
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|offsetAttrib
operator|.
name|startOffset
argument_list|()
operator|<=
name|maxStartOffset
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|consumeAllTokens
condition|)
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
comment|// no-op
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

