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
name|IOException
import|;
end_import

begin_comment
comment|/** A TokenStream enumerates the sequence of tokens, either from   fields of a document or from query text.<p>   This is an abstract class.  Concrete subclasses are:<ul><li>{@link Tokenizer}, a TokenStream   whose input is a Reader; and<li>{@link TokenFilter}, a TokenStream   whose input is another TokenStream.</ul>   */
end_comment

begin_class
DECL|class|TokenStream
specifier|public
specifier|abstract
class|class
name|TokenStream
block|{
comment|/** Returns the next token in the stream, or null at EOS. */
DECL|method|next
specifier|public
specifier|abstract
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Resets this stream to the beginning. This is an    *  optional operation, so subclasses may or may not    *  implement this method. Reset() is not needed for    *  the standard indexing process. However, if the Tokens     *  of a TokenStream are intended to be consumed more than     *  once, it is neccessary to implement reset().     */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{}
comment|/** Releases resources associated with this stream. */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

