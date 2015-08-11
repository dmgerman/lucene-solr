begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.el
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|el
package|;
end_package

begin_comment
comment|/*  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|CharacterUtils
import|;
end_import

begin_comment
comment|/**  * Normalizes token text to lower case, removes some Greek diacritics,  * and standardizes final sigma to sigma.   */
end_comment

begin_class
DECL|class|GreekLowerCaseFilter
specifier|public
specifier|final
class|class
name|GreekLowerCaseFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|charUtils
specifier|private
specifier|final
name|CharacterUtils
name|charUtils
init|=
name|CharacterUtils
operator|.
name|getInstance
argument_list|()
decl_stmt|;
comment|/**    * Create a GreekLowerCaseFilter that normalizes Greek token text.    *     * @param in TokenStream to filter    */
DECL|method|GreekLowerCaseFilter
specifier|public
name|GreekLowerCaseFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
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
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|char
index|[]
name|chArray
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|chLen
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|chLen
condition|;
control|)
block|{
name|i
operator|+=
name|Character
operator|.
name|toChars
argument_list|(
name|lowerCase
argument_list|(
name|charUtils
operator|.
name|codePointAt
argument_list|(
name|chArray
argument_list|,
name|i
argument_list|,
name|chLen
argument_list|)
argument_list|)
argument_list|,
name|chArray
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|lowerCase
specifier|private
name|int
name|lowerCase
parameter_list|(
name|int
name|codepoint
parameter_list|)
block|{
switch|switch
condition|(
name|codepoint
condition|)
block|{
comment|/* There are two lowercase forms of sigma:        *   U+03C2: small final sigma (end of word)        *   U+03C3: small sigma (otherwise)        *           * Standardize both to U+03C3        */
case|case
literal|'\u03C2'
case|:
comment|/* small final sigma */
return|return
literal|'\u03C3'
return|;
comment|/* small sigma */
comment|/* Some greek characters contain diacritics.        * This filter removes these, converting to the lowercase base form.        */
case|case
literal|'\u0386'
case|:
comment|/* capital alpha with tonos */
case|case
literal|'\u03AC'
case|:
comment|/* small alpha with tonos */
return|return
literal|'\u03B1'
return|;
comment|/* small alpha */
case|case
literal|'\u0388'
case|:
comment|/* capital epsilon with tonos */
case|case
literal|'\u03AD'
case|:
comment|/* small epsilon with tonos */
return|return
literal|'\u03B5'
return|;
comment|/* small epsilon */
case|case
literal|'\u0389'
case|:
comment|/* capital eta with tonos */
case|case
literal|'\u03AE'
case|:
comment|/* small eta with tonos */
return|return
literal|'\u03B7'
return|;
comment|/* small eta */
case|case
literal|'\u038A'
case|:
comment|/* capital iota with tonos */
case|case
literal|'\u03AA'
case|:
comment|/* capital iota with dialytika */
case|case
literal|'\u03AF'
case|:
comment|/* small iota with tonos */
case|case
literal|'\u03CA'
case|:
comment|/* small iota with dialytika */
case|case
literal|'\u0390'
case|:
comment|/* small iota with dialytika and tonos */
return|return
literal|'\u03B9'
return|;
comment|/* small iota */
case|case
literal|'\u038E'
case|:
comment|/* capital upsilon with tonos */
case|case
literal|'\u03AB'
case|:
comment|/* capital upsilon with dialytika */
case|case
literal|'\u03CD'
case|:
comment|/* small upsilon with tonos */
case|case
literal|'\u03CB'
case|:
comment|/* small upsilon with dialytika */
case|case
literal|'\u03B0'
case|:
comment|/* small upsilon with dialytika and tonos */
return|return
literal|'\u03C5'
return|;
comment|/* small upsilon */
case|case
literal|'\u038C'
case|:
comment|/* capital omicron with tonos */
case|case
literal|'\u03CC'
case|:
comment|/* small omicron with tonos */
return|return
literal|'\u03BF'
return|;
comment|/* small omicron */
case|case
literal|'\u038F'
case|:
comment|/* capital omega with tonos */
case|case
literal|'\u03CE'
case|:
comment|/* small omega with tonos */
return|return
literal|'\u03C9'
return|;
comment|/* small omega */
comment|/* The previous implementation did the conversion below.        * Only implemented for backwards compatibility with old indexes.        */
case|case
literal|'\u03A2'
case|:
comment|/* reserved */
return|return
literal|'\u03C2'
return|;
comment|/* small final sigma */
default|default:
return|return
name|Character
operator|.
name|toLowerCase
argument_list|(
name|codepoint
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

