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
name|CharArraySet
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Marks terms as keywords via the {@link KeywordAttribute}. Each token  * contained in the provided is marked as a keyword by setting  * {@link KeywordAttribute#setKeyword(boolean)} to<code>true</code>.  *   * @see KeywordAttribute  */
end_comment

begin_class
DECL|class|KeywordMarkerFilter
specifier|public
specifier|final
class|class
name|KeywordMarkerFilter
extends|extends
name|TokenFilter
block|{
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
DECL|field|keywordSet
specifier|private
specifier|final
name|CharArraySet
name|keywordSet
decl_stmt|;
comment|/**    * Create a new KeywordMarkerFilter, that marks the current token as a    * keyword if the tokens term buffer is contained in the given set via the    * {@link KeywordAttribute}.    *     * @param in    *          TokenStream to filter    * @param keywordSet    *          the keywords set to lookup the current termbuffer    */
DECL|method|KeywordMarkerFilter
specifier|public
name|KeywordMarkerFilter
parameter_list|(
specifier|final
name|TokenStream
name|in
parameter_list|,
specifier|final
name|CharArraySet
name|keywordSet
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|keywordSet
operator|=
name|keywordSet
expr_stmt|;
block|}
comment|/**    * Create a new KeywordMarkerFilter, that marks the current token as a    * keyword if the tokens term buffer is contained in the given set via the    * {@link KeywordAttribute}.    *     * @param in    *          TokenStream to filter    * @param keywordSet    *          the keywords set to lookup the current termbuffer    */
DECL|method|KeywordMarkerFilter
specifier|public
name|KeywordMarkerFilter
parameter_list|(
specifier|final
name|TokenStream
name|in
parameter_list|,
specifier|final
name|Set
argument_list|<
name|?
argument_list|>
name|keywordSet
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
name|keywordSet
operator|instanceof
name|CharArraySet
condition|?
operator|(
name|CharArraySet
operator|)
name|keywordSet
else|:
name|CharArraySet
operator|.
name|copy
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|,
name|keywordSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|keywordAttr
operator|.
name|setKeyword
argument_list|(
name|keywordSet
operator|.
name|contains
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
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

