begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  * Marks terms as keywords via the {@link KeywordAttribute}. Each token  * that matches the provided pattern is marked as a keyword by setting  * {@link KeywordAttribute#setKeyword(boolean)} to<code>true</code>.  */
end_comment

begin_class
DECL|class|PatternKeywordMarkerFilter
specifier|public
specifier|final
class|class
name|PatternKeywordMarkerFilter
extends|extends
name|KeywordMarkerFilter
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
DECL|field|matcher
specifier|private
specifier|final
name|Matcher
name|matcher
decl_stmt|;
comment|/**    * Create a new {@link PatternKeywordMarkerFilter}, that marks the current    * token as a keyword if the tokens term buffer matches the provided    * {@link Pattern} via the {@link KeywordAttribute}.    *     * @param in    *          TokenStream to filter    * @param pattern    *          the pattern to apply to the incoming term buffer    **/
DECL|method|PatternKeywordMarkerFilter
specifier|public
name|PatternKeywordMarkerFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isKeyword
specifier|protected
name|boolean
name|isKeyword
parameter_list|()
block|{
name|matcher
operator|.
name|reset
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
return|return
name|matcher
operator|.
name|matches
argument_list|()
return|;
block|}
block|}
end_class

end_unit

