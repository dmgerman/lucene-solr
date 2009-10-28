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
comment|/** A Tokenizer is a TokenStream whose input is a Reader.<p>   This is an abstract class; subclasses must override {@link #incrementToken()}<p>   NOTE: Subclasses overriding {@link #incrementToken()} must   call {@link AttributeSource#clearAttributes()} before   setting attributes.   Subclasses overriding {@link #incrementToken()} must call   {@link Token#clear()} before setting Token attributes.   */
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
name|CharReader
operator|.
name|get
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a tokenizer with null input using the given AttributeFactory. */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a token stream processing the given input using the given AttributeFactory. */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|CharReader
operator|.
name|get
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a token stream processing the given input using the given AttributeSource. */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a token stream processing the given input using the given AttributeSource. */
DECL|method|Tokenizer
specifier|protected
name|Tokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|input
operator|=
name|CharReader
operator|.
name|get
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/** By default, closes the input Reader. */
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
comment|/** Return the corrected offset. If {@link #input} is a {@link CharStream} subclass    * this method calls {@link CharStream#correctOffset}, else returns<code>currentOff</code>.    * @param currentOff offset as seen in the output    * @return corrected offset based on the input    * @see CharStream#correctOffset    */
DECL|method|correctOffset
specifier|protected
specifier|final
name|int
name|correctOffset
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
return|return
operator|(
name|input
operator|instanceof
name|CharStream
operator|)
condition|?
operator|(
operator|(
name|CharStream
operator|)
name|input
operator|)
operator|.
name|correctOffset
argument_list|(
name|currentOff
argument_list|)
else|:
name|currentOff
return|;
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

