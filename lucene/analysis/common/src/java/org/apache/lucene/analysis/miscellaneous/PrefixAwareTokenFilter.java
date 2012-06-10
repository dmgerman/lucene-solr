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
name|FlagsAttribute
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
name|PayloadAttribute
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
name|CharTermAttribute
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
name|TypeAttribute
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
name|util
operator|.
name|BytesRef
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
comment|/**  * Joins two token streams and leaves the last token of the first stream available  * to be used when updating the token values in the second stream based on that token.  *  * The default implementation adds last prefix token end offset to the suffix token start and end offsets.  *<p/>  *<b>NOTE:</b> This filter might not behave correctly if used with custom Attributes, i.e. Attributes other than  * the ones located in org.apache.lucene.analysis.tokenattributes.   */
end_comment

begin_class
DECL|class|PrefixAwareTokenFilter
specifier|public
class|class
name|PrefixAwareTokenFilter
extends|extends
name|TokenStream
block|{
DECL|field|prefix
specifier|private
name|TokenStream
name|prefix
decl_stmt|;
DECL|field|suffix
specifier|private
name|TokenStream
name|suffix
decl_stmt|;
DECL|field|termAtt
specifier|private
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|payloadAtt
specifier|private
name|PayloadAttribute
name|payloadAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
DECL|field|flagsAtt
specifier|private
name|FlagsAttribute
name|flagsAtt
decl_stmt|;
DECL|field|p_termAtt
specifier|private
name|CharTermAttribute
name|p_termAtt
decl_stmt|;
DECL|field|p_posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|p_posIncrAtt
decl_stmt|;
DECL|field|p_payloadAtt
specifier|private
name|PayloadAttribute
name|p_payloadAtt
decl_stmt|;
DECL|field|p_offsetAtt
specifier|private
name|OffsetAttribute
name|p_offsetAtt
decl_stmt|;
DECL|field|p_typeAtt
specifier|private
name|TypeAttribute
name|p_typeAtt
decl_stmt|;
DECL|field|p_flagsAtt
specifier|private
name|FlagsAttribute
name|p_flagsAtt
decl_stmt|;
DECL|method|PrefixAwareTokenFilter
specifier|public
name|PrefixAwareTokenFilter
parameter_list|(
name|TokenStream
name|prefix
parameter_list|,
name|TokenStream
name|suffix
parameter_list|)
block|{
name|super
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|this
operator|.
name|suffix
operator|=
name|suffix
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|prefixExhausted
operator|=
literal|false
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|payloadAtt
operator|=
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|flagsAtt
operator|=
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|p_termAtt
operator|=
name|prefix
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|p_posIncrAtt
operator|=
name|prefix
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|p_payloadAtt
operator|=
name|prefix
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|p_offsetAtt
operator|=
name|prefix
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|p_typeAtt
operator|=
name|prefix
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|p_flagsAtt
operator|=
name|prefix
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|field|previousPrefixToken
specifier|private
name|Token
name|previousPrefixToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
DECL|field|reusableToken
specifier|private
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
DECL|field|prefixExhausted
specifier|private
name|boolean
name|prefixExhausted
decl_stmt|;
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|prefixExhausted
condition|)
block|{
name|Token
name|nextToken
init|=
name|getNextPrefixInputToken
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
literal|null
condition|)
block|{
name|prefixExhausted
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|previousPrefixToken
operator|.
name|reinit
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
comment|// Make it a deep copy
name|BytesRef
name|p
init|=
name|previousPrefixToken
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|previousPrefixToken
operator|.
name|setPayload
argument_list|(
name|p
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|setCurrentToken
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|Token
name|nextToken
init|=
name|getNextSuffixInputToken
argument_list|(
name|reusableToken
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextToken
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|nextToken
operator|=
name|updateSuffixToken
argument_list|(
name|nextToken
argument_list|,
name|previousPrefixToken
argument_list|)
expr_stmt|;
name|setCurrentToken
argument_list|(
name|nextToken
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|setCurrentToken
specifier|private
name|void
name|setCurrentToken
parameter_list|(
name|Token
name|token
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
return|return;
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|token
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|token
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|token
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|flagsAtt
operator|.
name|setFlags
argument_list|(
name|token
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|typeAtt
operator|.
name|setType
argument_list|(
name|token
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|payloadAtt
operator|.
name|setPayload
argument_list|(
name|token
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNextPrefixInputToken
specifier|private
name|Token
name|getNextPrefixInputToken
parameter_list|(
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|prefix
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|null
return|;
name|token
operator|.
name|copyBuffer
argument_list|(
name|p_termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|p_termAtt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
name|p_posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setFlags
argument_list|(
name|p_flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setOffset
argument_list|(
name|p_offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|p_offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setType
argument_list|(
name|p_typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPayload
argument_list|(
name|p_payloadAtt
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
DECL|method|getNextSuffixInputToken
specifier|private
name|Token
name|getNextSuffixInputToken
parameter_list|(
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|suffix
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|null
return|;
name|token
operator|.
name|copyBuffer
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPositionIncrement
argument_list|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setFlags
argument_list|(
name|flagsAtt
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setOffset
argument_list|(
name|offsetAtt
operator|.
name|startOffset
argument_list|()
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setType
argument_list|(
name|typeAtt
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setPayload
argument_list|(
name|payloadAtt
operator|.
name|getPayload
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|token
return|;
block|}
comment|/**    * The default implementation adds last prefix token end offset to the suffix token start and end offsets.    *    * @param suffixToken a token from the suffix stream    * @param lastPrefixToken the last token from the prefix stream    * @return consumer token    */
DECL|method|updateSuffixToken
specifier|public
name|Token
name|updateSuffixToken
parameter_list|(
name|Token
name|suffixToken
parameter_list|,
name|Token
name|lastPrefixToken
parameter_list|)
block|{
name|suffixToken
operator|.
name|setOffset
argument_list|(
name|lastPrefixToken
operator|.
name|endOffset
argument_list|()
operator|+
name|suffixToken
operator|.
name|startOffset
argument_list|()
argument_list|,
name|lastPrefixToken
operator|.
name|endOffset
argument_list|()
operator|+
name|suffixToken
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|suffixToken
return|;
block|}
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
name|prefix
operator|.
name|end
argument_list|()
expr_stmt|;
name|suffix
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
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
name|prefix
operator|.
name|close
argument_list|()
expr_stmt|;
name|suffix
operator|.
name|close
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|prefixExhausted
operator|=
literal|false
expr_stmt|;
name|prefix
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|suffix
operator|!=
literal|null
condition|)
block|{
name|suffix
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getPrefix
specifier|public
name|TokenStream
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
DECL|method|setPrefix
specifier|public
name|void
name|setPrefix
parameter_list|(
name|TokenStream
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|getSuffix
specifier|public
name|TokenStream
name|getSuffix
parameter_list|()
block|{
return|return
name|suffix
return|;
block|}
DECL|method|setSuffix
specifier|public
name|void
name|setSuffix
parameter_list|(
name|TokenStream
name|suffix
parameter_list|)
block|{
name|this
operator|.
name|suffix
operator|=
name|suffix
expr_stmt|;
block|}
block|}
end_class

end_unit

