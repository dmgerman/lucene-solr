begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/** A TokenFilter is a TokenStream whose input is another TokenStream.<p>   This is an abstract class; subclasses must override {@link #incrementToken()}.   @see TokenStream   */
end_comment

begin_class
DECL|class|TokenFilter
specifier|public
specifier|abstract
class|class
name|TokenFilter
extends|extends
name|TokenStream
block|{
comment|/** The source of tokens for this filter. */
DECL|field|input
specifier|protected
specifier|final
name|TokenStream
name|input
decl_stmt|;
comment|/** Construct a token stream filtering the given input. */
DECL|method|TokenFilter
specifier|protected
name|TokenFilter
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
name|input
operator|=
name|input
expr_stmt|;
block|}
comment|/**     * {@inheritDoc}    *<p>     *<b>NOTE:</b>     * The default implementation chains the call to the input TokenStream, so    * be sure to call<code>super.end()</code> first when overriding this method.    */
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
name|input
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    *<b>NOTE:</b>     * The default implementation chains the call to the input TokenStream, so    * be sure to call<code>super.close()</code> when overriding this method.    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    *<b>NOTE:</b>     * The default implementation chains the call to the input TokenStream, so    * be sure to call<code>super.reset()</code> when overriding this method.    */
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
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

