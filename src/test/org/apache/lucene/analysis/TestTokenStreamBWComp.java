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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|index
operator|.
name|Payload
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
name|LuceneTestCase
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
name|*
import|;
end_import

begin_comment
comment|/** This class tests some special cases of backwards compatibility when using the new TokenStream API with old analyzers */
end_comment

begin_class
DECL|class|TestTokenStreamBWComp
specifier|public
class|class
name|TestTokenStreamBWComp
extends|extends
name|LuceneTestCase
block|{
DECL|field|doc
specifier|private
specifier|final
name|String
name|doc
init|=
literal|"This is the new TokenStream api"
decl_stmt|;
DECL|field|stopwords
specifier|private
specifier|final
name|String
index|[]
name|stopwords
init|=
operator|new
name|String
index|[]
block|{
literal|"is"
block|,
literal|"the"
block|,
literal|"this"
block|}
decl_stmt|;
DECL|class|POSToken
specifier|public
specifier|static
class|class
name|POSToken
extends|extends
name|Token
block|{
DECL|field|PROPERNOUN
specifier|public
specifier|static
specifier|final
name|int
name|PROPERNOUN
init|=
literal|1
decl_stmt|;
DECL|field|NO_NOUN
specifier|public
specifier|static
specifier|final
name|int
name|NO_NOUN
init|=
literal|2
decl_stmt|;
DECL|field|partOfSpeech
specifier|private
name|int
name|partOfSpeech
decl_stmt|;
DECL|method|setPartOfSpeech
specifier|public
name|void
name|setPartOfSpeech
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|partOfSpeech
operator|=
name|pos
expr_stmt|;
block|}
DECL|method|getPartOfSpeech
specifier|public
name|int
name|getPartOfSpeech
parameter_list|()
block|{
return|return
name|this
operator|.
name|partOfSpeech
return|;
block|}
block|}
DECL|class|PartOfSpeechTaggingFilter
specifier|static
class|class
name|PartOfSpeechTaggingFilter
extends|extends
name|TokenFilter
block|{
DECL|method|PartOfSpeechTaggingFilter
specifier|protected
name|PartOfSpeechTaggingFilter
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
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|POSToken
name|pt
init|=
operator|new
name|POSToken
argument_list|()
decl_stmt|;
name|pt
operator|.
name|reinit
argument_list|(
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|pt
operator|.
name|termLength
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|Character
operator|.
name|isUpperCase
argument_list|(
name|pt
operator|.
name|termBuffer
argument_list|()
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|pt
operator|.
name|setPartOfSpeech
argument_list|(
name|POSToken
operator|.
name|PROPERNOUN
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pt
operator|.
name|setPartOfSpeech
argument_list|(
name|POSToken
operator|.
name|NO_NOUN
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pt
return|;
block|}
block|}
DECL|class|PartOfSpeechAnnotatingFilter
specifier|static
class|class
name|PartOfSpeechAnnotatingFilter
extends|extends
name|TokenFilter
block|{
DECL|field|PROPER_NOUN_ANNOTATION
specifier|public
specifier|final
specifier|static
name|byte
name|PROPER_NOUN_ANNOTATION
init|=
literal|1
decl_stmt|;
DECL|method|PartOfSpeechAnnotatingFilter
specifier|protected
name|PartOfSpeechAnnotatingFilter
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
block|}
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|t
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|t
operator|instanceof
name|POSToken
condition|)
block|{
name|POSToken
name|pt
init|=
operator|(
name|POSToken
operator|)
name|t
decl_stmt|;
if|if
condition|(
name|pt
operator|.
name|getPartOfSpeech
argument_list|()
operator|==
name|POSToken
operator|.
name|PROPERNOUN
condition|)
block|{
name|pt
operator|.
name|setPayload
argument_list|(
operator|new
name|Payload
argument_list|(
operator|new
name|byte
index|[]
block|{
name|PROPER_NOUN_ANNOTATION
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pt
return|;
block|}
else|else
block|{
return|return
name|t
return|;
block|}
block|}
block|}
comment|// test the chain: The one and only term "TokenStream" should be declared as proper noun:
DECL|method|testTeeSinkCustomTokenNewAPI
specifier|public
name|void
name|testTeeSinkCustomTokenNewAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testTeeSinkCustomTokenOldAPI
specifier|public
name|void
name|testTeeSinkCustomTokenOldAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testTeeSinkCustomTokenVeryOldAPI
specifier|public
name|void
name|testTeeSinkCustomTokenVeryOldAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testTeeSinkCustomToken
specifier|private
name|void
name|testTeeSinkCustomToken
parameter_list|(
name|int
name|api
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|PartOfSpeechTaggingFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|stream
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|SinkTokenizer
name|sink
init|=
operator|new
name|SinkTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|stream1
init|=
operator|new
name|PartOfSpeechAnnotatingFilter
argument_list|(
name|sink
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|TeeTokenFilter
argument_list|(
name|stream
argument_list|,
name|sink
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|PartOfSpeechAnnotatingFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|api
condition|)
block|{
case|case
literal|0
case|:
name|consumeStreamNewAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamNewAPI
argument_list|(
name|stream1
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|consumeStreamOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamOldAPI
argument_list|(
name|stream1
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|consumeStreamVeryOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamVeryOldAPI
argument_list|(
name|stream1
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// test caching the special custom POSToken works in all cases
DECL|method|testCachingCustomTokenNewAPI
specifier|public
name|void
name|testCachingCustomTokenNewAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testCachingCustomTokenOldAPI
specifier|public
name|void
name|testCachingCustomTokenOldAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testCachingCustomTokenVeryOldAPI
specifier|public
name|void
name|testCachingCustomTokenVeryOldAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testCachingCustomTokenMixed
specifier|public
name|void
name|testCachingCustomTokenMixed
parameter_list|()
throws|throws
name|IOException
block|{
name|testTeeSinkCustomToken
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testCachingCustomToken
specifier|private
name|void
name|testCachingCustomToken
parameter_list|(
name|int
name|api
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|PartOfSpeechTaggingFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|stream
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|CachingTokenFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|//<- the caching is done before the annotating!
name|stream
operator|=
operator|new
name|PartOfSpeechAnnotatingFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|api
condition|)
block|{
case|case
literal|0
case|:
name|consumeStreamNewAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamNewAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|consumeStreamOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|consumeStreamVeryOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamVeryOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|consumeStreamNewAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamVeryOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamNewAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|consumeStreamVeryOldAPI
argument_list|(
name|stream
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
DECL|method|consumeStreamNewAPI
specifier|private
specifier|static
name|void
name|consumeStreamNewAPI
parameter_list|(
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|PayloadAttribute
name|payloadAtt
init|=
operator|(
name|PayloadAttribute
operator|)
name|stream
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|TermAttribute
name|termAtt
init|=
operator|(
name|TermAttribute
operator|)
name|stream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|term
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
name|Payload
name|p
init|=
name|payloadAtt
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|getData
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|p
operator|.
name|getData
argument_list|()
index|[
literal|0
index|]
operator|==
name|PartOfSpeechAnnotatingFilter
operator|.
name|PROPER_NOUN_ANNOTATION
condition|)
block|{
name|assertTrue
argument_list|(
literal|"only TokenStream is a proper noun"
argument_list|,
literal|"tokenstream"
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"all other tokens (if this test fails, the special POSToken subclass is not correctly passed through the chain)"
argument_list|,
literal|"tokenstream"
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|consumeStreamOldAPI
specifier|private
specifier|static
name|void
name|consumeStreamOldAPI
parameter_list|(
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|reusableToken
operator|=
name|stream
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|term
init|=
name|reusableToken
operator|.
name|term
argument_list|()
decl_stmt|;
name|Payload
name|p
init|=
name|reusableToken
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|getData
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|p
operator|.
name|getData
argument_list|()
index|[
literal|0
index|]
operator|==
name|PartOfSpeechAnnotatingFilter
operator|.
name|PROPER_NOUN_ANNOTATION
condition|)
block|{
name|assertTrue
argument_list|(
literal|"only TokenStream is a proper noun"
argument_list|,
literal|"tokenstream"
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"all other tokens (if this test fails, the special POSToken subclass is not correctly passed through the chain)"
argument_list|,
literal|"tokenstream"
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|consumeStreamVeryOldAPI
specifier|private
specifier|static
name|void
name|consumeStreamVeryOldAPI
parameter_list|(
name|TokenStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|term
init|=
name|token
operator|.
name|term
argument_list|()
decl_stmt|;
name|Payload
name|p
init|=
name|token
operator|.
name|getPayload
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|getData
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|p
operator|.
name|getData
argument_list|()
index|[
literal|0
index|]
operator|==
name|PartOfSpeechAnnotatingFilter
operator|.
name|PROPER_NOUN_ANNOTATION
condition|)
block|{
name|assertTrue
argument_list|(
literal|"only TokenStream is a proper noun"
argument_list|,
literal|"tokenstream"
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
literal|"all other tokens (if this test fails, the special POSToken subclass is not correctly passed through the chain)"
argument_list|,
literal|"tokenstream"
operator|.
name|equals
argument_list|(
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// test if tokenization fails, if only the new API is allowed and an old TokenStream is in the chain
DECL|method|testOnlyNewAPI
specifier|public
name|void
name|testOnlyNewAPI
parameter_list|()
throws|throws
name|IOException
block|{
name|TokenStream
operator|.
name|setOnlyUseNewAPI
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
comment|// this should fail with UOE
try|try
block|{
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|PartOfSpeechTaggingFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
comment|//<-- this one is evil!
name|stream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|stream
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
empty_stmt|;
name|fail
argument_list|(
literal|"If only the new API is allowed, this should fail with an UOE"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
name|assertTrue
argument_list|(
operator|(
name|PartOfSpeechTaggingFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" does not implement incrementToken() which is needed for onlyUseNewAPI."
operator|)
operator|.
name|equals
argument_list|(
name|uoe
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// this should pass, as all core token streams support the new API
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|stream
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
empty_stmt|;
comment|// Test, if all attributes are implemented by their implementation, not Token/TokenWrapper
name|assertTrue
argument_list|(
literal|"TermAttribute is implemented by TermAttributeImpl"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TermAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OffsetAttribute is implemented by OffsetAttributeImpl"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|OffsetAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FlagsAttribute is implemented by FlagsAttributeImpl"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|FlagsAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PayloadAttribute is implemented by PayloadAttributeImpl"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PayloadAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PositionIncrementAttribute is implemented by PositionIncrementAttributeImpl"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|PositionIncrementAttributeImpl
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TypeAttribute is implemented by TypeAttributeImpl"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TypeAttributeImpl
argument_list|)
expr_stmt|;
comment|// Test if the wrapper API (onlyUseNewAPI==false) uses TokenWrapper
comment|// as attribute instance.
comment|// TokenWrapper encapsulates a Token instance that can be exchanged
comment|// by another Token instance without changing the AttributeImpl instance
comment|// itsself.
name|TokenStream
operator|.
name|setOnlyUseNewAPI
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TermAttribute is implemented by TokenWrapper"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TokenWrapper
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"OffsetAttribute is implemented by TokenWrapper"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TokenWrapper
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FlagsAttribute is implemented by TokenWrapper"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|FlagsAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TokenWrapper
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PayloadAttribute is implemented by TokenWrapper"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TokenWrapper
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"PositionIncrementAttribute is implemented by TokenWrapper"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TokenWrapper
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"TypeAttribute is implemented by TokenWrapper"
argument_list|,
name|stream
operator|.
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
operator|instanceof
name|TokenWrapper
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|TokenStream
operator|.
name|setOnlyUseNewAPI
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testOverridesAny
specifier|public
name|void
name|testOverridesAny
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|stream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doc
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|=
operator|new
name|TokenFilter
argument_list|(
name|stream
argument_list|)
block|{
comment|// we implement nothing, only un-abstract it
block|}
expr_stmt|;
name|stream
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|StopFilter
argument_list|(
name|stream
argument_list|,
name|stopwords
argument_list|)
expr_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
empty_stmt|;
name|fail
argument_list|(
literal|"One TokenFilter does not override any of the required methods, so it should fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|uoe
operator|.
name|getMessage
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"does not implement any of incrementToken(), next(Token), next()."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

