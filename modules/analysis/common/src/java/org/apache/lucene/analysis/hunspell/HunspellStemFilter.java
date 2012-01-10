begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
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
name|util
operator|.
name|List
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
name|hunspell
operator|.
name|HunspellStemmer
operator|.
name|Stem
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
name|KeywordAttribute
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

begin_comment
comment|/**  * TokenFilter that uses hunspell affix rules and words to stem tokens.  Since hunspell supports a word having multiple  * stems, this filter can emit multiple tokens for each consumed token  */
end_comment

begin_class
DECL|class|HunspellStemFilter
specifier|public
specifier|final
class|class
name|HunspellStemFilter
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
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|keywordAtt
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAtt
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|stemmer
specifier|private
specifier|final
name|HunspellStemmer
name|stemmer
decl_stmt|;
DECL|field|buffer
specifier|private
name|List
argument_list|<
name|Stem
argument_list|>
name|buffer
decl_stmt|;
DECL|field|savedState
specifier|private
name|State
name|savedState
decl_stmt|;
DECL|field|dedup
specifier|private
specifier|final
name|boolean
name|dedup
decl_stmt|;
comment|/**    * Creates a new HunspellStemFilter that will stem tokens from the given TokenStream using affix rules in the provided    * HunspellDictionary    *    * @param input TokenStream whose tokens will be stemmed    * @param dictionary HunspellDictionary containing the affix rules and words that will be used to stem the tokens    */
DECL|method|HunspellStemFilter
specifier|public
name|HunspellStemFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|HunspellDictionary
name|dictionary
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
name|dictionary
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new HunspellStemFilter that will stem tokens from the given TokenStream using affix rules in the provided    * HunspellDictionary    *    * @param input TokenStream whose tokens will be stemmed    * @param dictionary HunspellDictionary containing the affix rules and words that will be used to stem the tokens    * @param dedup true if only unique terms should be output.    */
DECL|method|HunspellStemFilter
specifier|public
name|HunspellStemFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|HunspellDictionary
name|dictionary
parameter_list|,
name|boolean
name|dedup
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|dedup
operator|=
name|dedup
expr_stmt|;
name|this
operator|.
name|stemmer
operator|=
operator|new
name|HunspellStemmer
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|buffer
operator|!=
literal|null
operator|&&
operator|!
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Stem
name|nextStem
init|=
name|buffer
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|restoreState
argument_list|(
name|savedState
argument_list|)
expr_stmt|;
name|posIncAtt
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|nextStem
operator|.
name|getStem
argument_list|()
argument_list|,
literal|0
argument_list|,
name|nextStem
operator|.
name|getStemLength
argument_list|()
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|nextStem
operator|.
name|getStemLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
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
name|keywordAtt
operator|.
name|isKeyword
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|buffer
operator|=
name|dedup
condition|?
name|stemmer
operator|.
name|uniqueStems
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
else|:
name|stemmer
operator|.
name|stem
argument_list|(
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we do not know this word, return it unchanged
return|return
literal|true
return|;
block|}
name|Stem
name|stem
init|=
name|buffer
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|stem
operator|.
name|getStem
argument_list|()
argument_list|,
literal|0
argument_list|,
name|stem
operator|.
name|getStemLength
argument_list|()
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|stem
operator|.
name|getStemLength
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|buffer
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|savedState
operator|=
name|captureState
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|buffer
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

