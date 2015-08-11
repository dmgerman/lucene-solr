begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2004 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License. You may obtain a  * copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.stempel
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|stempel
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

begin_comment
comment|/**  * Transforms the token stream as per the stemming algorithm.  *<p>  * Note: the input to the stemming filter must already be in lower case, so you  * will need to use LowerCaseFilter or LowerCaseTokenizer farther down the  * Tokenizer chain in order for this to work properly!  */
end_comment

begin_class
DECL|class|StempelFilter
specifier|public
specifier|final
class|class
name|StempelFilter
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
name|StempelStemmer
name|stemmer
decl_stmt|;
DECL|field|minLength
specifier|private
specifier|final
name|int
name|minLength
decl_stmt|;
comment|/**    * Minimum length of input words to be processed. Shorter words are returned    * unchanged.    */
DECL|field|DEFAULT_MIN_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_LENGTH
init|=
literal|3
decl_stmt|;
comment|/**    * Create filter using the supplied stemming table.    *     * @param in input token stream    * @param stemmer stemmer    */
DECL|method|StempelFilter
specifier|public
name|StempelFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|StempelStemmer
name|stemmer
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|stemmer
argument_list|,
name|DEFAULT_MIN_LENGTH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create filter using the supplied stemming table.    *     * @param in input token stream    * @param stemmer stemmer    * @param minLength For performance reasons words shorter than minLength    * characters are not processed, but simply returned.    */
DECL|method|StempelFilter
specifier|public
name|StempelFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|StempelStemmer
name|stemmer
parameter_list|,
name|int
name|minLength
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
name|this
operator|.
name|minLength
operator|=
name|minLength
expr_stmt|;
block|}
comment|/** Returns the next input Token, after being stemmed */
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
if|if
condition|(
operator|!
name|keywordAtt
operator|.
name|isKeyword
argument_list|()
operator|&&
name|termAtt
operator|.
name|length
argument_list|()
operator|>
name|minLength
condition|)
block|{
name|StringBuilder
name|sb
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|termAtt
argument_list|)
decl_stmt|;
if|if
condition|(
name|sb
operator|!=
literal|null
condition|)
comment|// if we can't stem it, return unchanged
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|sb
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
block|}
end_class

end_unit

