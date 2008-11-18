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
name|Reader
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
comment|/** A Tokenizer is a TokenStream whose input is a Reader.<p>   This is an abstract class.<p><b>NOTE:</b> In order to enable the new API the method   {@link #useNewAPI()} has to be called with useNewAPI=true.   Otherwise the deprecated method {@link #next(Token)} will    be used by Lucene consumers (indexer and queryparser) to   consume the tokens. {@link #next(Token)} will be removed   in Lucene 3.0.<p>   NOTE: To use the old API subclasses must override {@link #next(Token)}.   It's also OK to instead override {@link #next()} but that   method is slower compared to {@link #next(Token)}.<p>   NOTE: subclasses overriding {@link #next(Token)} must     call {@link Token#clear()}.  *<p><font color="#FF0000">  * WARNING: The status of the new TokenStream, AttributeSource and Attributes is experimental.   * The APIs introduced in these classes with Lucene 2.9 might change in the future.   * We will make our best efforts to keep the APIs backwards-compatible.</font>  */
end_comment

begin_class
DECL|class|Tokenizer
specifier|public
specifier|abstract
class|class
name|Tokenizer
extends|extends
name|TokenStream
block|{
comment|/** The text source for this Tokenizer. */
DECL|field|input
specifier|protected
name|Reader
name|input
decl_stmt|;
comment|/** Construct a tokenizer with null input. */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|()
block|{}
comment|/** Construct a token stream processing the given input. */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
comment|/** By default, closes the input Reader. */
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
comment|/** Expert: Reset the tokenizer to a new reader.  Typically, an    *  analyzer (in its reusableTokenStream method) will use    *  this to re-use a previously created tokenizer. */
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
name|this
operator|.
name|input
operator|=
name|input
expr_stmt|;
block|}
block|}
end_class

end_unit

