begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.id
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|id
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
comment|/**  * A {@link TokenFilter} that applies {@link IndonesianStemmer} to stem Indonesian words.  */
end_comment

begin_class
DECL|class|IndonesianStemFilter
specifier|public
specifier|final
class|class
name|IndonesianStemFilter
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
name|IndonesianStemmer
name|stemmer
init|=
operator|new
name|IndonesianStemmer
argument_list|()
decl_stmt|;
DECL|field|stemDerivational
specifier|private
specifier|final
name|boolean
name|stemDerivational
decl_stmt|;
comment|/**    * Calls {@link #IndonesianStemFilter(TokenStream, boolean) IndonesianStemFilter(input, true)}    */
DECL|method|IndonesianStemFilter
specifier|public
name|IndonesianStemFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new IndonesianStemFilter.    *<p>    * If<code>stemDerivational</code> is false,     * only inflectional suffixes (particles and possessive pronouns) are stemmed.    */
DECL|method|IndonesianStemFilter
specifier|public
name|IndonesianStemFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|boolean
name|stemDerivational
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemDerivational
operator|=
name|stemDerivational
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
if|if
condition|(
operator|!
name|keywordAtt
operator|.
name|isKeyword
argument_list|()
condition|)
block|{
specifier|final
name|int
name|newlen
init|=
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
argument_list|,
name|stemDerivational
argument_list|)
decl_stmt|;
name|termAtt
operator|.
name|setLength
argument_list|(
name|newlen
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

