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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** A Tokenizer is a TokenStream whose input is a Reader.<p>   This is an abstract class.  */
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
block|}
end_class

end_unit

