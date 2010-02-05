begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.fr
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|fr
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
name|KeywordMarkerTokenFilter
import|;
end_import

begin_comment
comment|// for javadoc
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
name|snowball
operator|.
name|SnowballFilter
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
name|TermAttribute
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * A {@link TokenFilter} that stems french words.   *<p>  * The used stemmer can be changed at runtime after the  * filter object is created (as long as it is a {@link FrenchStemmer}).  *</p>  *<p>  * To prevent terms from being stemmed use an instance of  * {@link KeywordMarkerTokenFilter} or a custom {@link TokenFilter} that sets  * the {@link KeywordAttribute} before this {@link TokenStream}.  *</p>  * @see KeywordMarkerTokenFilter  * @deprecated Use {@link SnowballFilter} with   * {@link org.tartarus.snowball.ext.FrenchStemmer} instead, which has the  * same functionality. This filter will be removed in Lucene 4.0  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|FrenchStemFilter
specifier|public
specifier|final
class|class
name|FrenchStemFilter
extends|extends
name|TokenFilter
block|{
comment|/** 	 * The actual token in the input stream. 	 */
DECL|field|stemmer
specifier|private
name|FrenchStemmer
name|stemmer
init|=
literal|null
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
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|field|keywordAttr
specifier|private
specifier|final
name|KeywordAttribute
name|keywordAttr
decl_stmt|;
DECL|method|FrenchStemFilter
specifier|public
name|FrenchStemFilter
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
name|stemmer
operator|=
operator|new
name|FrenchStemmer
argument_list|()
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|keywordAttr
operator|=
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    *     * @param in the {@link TokenStream} to filter    * @param exclusiontable a set of terms not to be stemmed    * @deprecated use {@link KeywordAttribute} with {@link KeywordMarkerTokenFilter} instead.    */
annotation|@
name|Deprecated
comment|// TODO remove in 3.2
DECL|method|FrenchStemFilter
specifier|public
name|FrenchStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Set
argument_list|<
name|?
argument_list|>
name|exclusiontable
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|exclusions
operator|=
name|exclusiontable
expr_stmt|;
block|}
comment|/** 	 * @return  Returns true for the next token in the stream, or false at EOS 	 */
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
name|String
name|term
init|=
name|termAtt
operator|.
name|term
argument_list|()
decl_stmt|;
comment|// Check the exclusion table
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
comment|// If not stemmed, don't waste the time  adjusting the token.
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
name|setTermBuffer
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
comment|/** 	 * Set a alternative/custom {@link FrenchStemmer} for this filter. 	 */
DECL|method|setStemmer
specifier|public
name|void
name|setStemmer
parameter_list|(
name|FrenchStemmer
name|stemmer
parameter_list|)
block|{
if|if
condition|(
name|stemmer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
block|}
block|}
comment|/** 	 * Set an alternative exclusion list for this filter.    * @deprecated use {@link KeywordAttribute} with {@link KeywordMarkerTokenFilter} instead. 	 */
annotation|@
name|Deprecated
comment|// TODO remove in 3.2
DECL|method|setExclusionTable
specifier|public
name|void
name|setExclusionTable
parameter_list|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|exclusiontable
parameter_list|)
block|{
name|exclusions
operator|=
operator|new
name|HashSet
argument_list|(
name|exclusiontable
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

