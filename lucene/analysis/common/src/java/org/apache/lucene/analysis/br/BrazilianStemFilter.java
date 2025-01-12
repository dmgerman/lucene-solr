begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.br
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|br
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|miscellaneous
operator|.
name|SetKeywordMarkerFilter
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
comment|/**  * A {@link TokenFilter} that applies {@link BrazilianStemmer}.  *<p>  * To prevent terms from being stemmed use an instance of  * {@link SetKeywordMarkerFilter} or a custom {@link TokenFilter} that sets  * the {@link KeywordAttribute} before this {@link TokenStream}.  *</p>  * @see SetKeywordMarkerFilter  *   */
end_comment

begin_class
DECL|class|BrazilianStemFilter
specifier|public
specifier|final
class|class
name|BrazilianStemFilter
extends|extends
name|TokenFilter
block|{
comment|/**    * {@link BrazilianStemmer} in use by this filter.    */
DECL|field|stemmer
specifier|private
name|BrazilianStemmer
name|stemmer
init|=
operator|new
name|BrazilianStemmer
argument_list|()
decl_stmt|;
DECL|field|exclusions
specifier|private
name|Set
argument_list|<
name|?
argument_list|>
name|exclusions
init|=
literal|null
decl_stmt|;
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
DECL|field|keywordAttr
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAttr
init|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Creates a new BrazilianStemFilter     *     * @param in the source {@link TokenStream}     */
DECL|method|BrazilianStemFilter
specifier|public
name|BrazilianStemFilter
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
specifier|final
name|String
name|term
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// Check the exclusion table.
if|if
condition|(
operator|!
name|keywordAttr
operator|.
name|isKeyword
argument_list|()
operator|&&
operator|(
name|exclusions
operator|==
literal|null
operator|||
operator|!
name|exclusions
operator|.
name|contains
argument_list|(
name|term
argument_list|)
operator|)
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|stemmer
operator|.
name|stem
argument_list|(
name|term
argument_list|)
decl_stmt|;
comment|// If not stemmed, don't waste the time adjusting the token.
if|if
condition|(
operator|(
name|s
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|s
operator|.
name|equals
argument_list|(
name|term
argument_list|)
condition|)
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|s
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

