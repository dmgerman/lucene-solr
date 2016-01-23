begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|TypeAttribute
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|CommonGramsFilter
operator|.
name|GRAM_TYPE
import|;
end_import

begin_comment
comment|/**  * Wrap a CommonGramsFilter optimizing phrase queries by only returning single  * words when they are not a member of a bigram.  *   * Example:  *<ul>  *<li>query input to CommonGramsFilter: "the rain in spain falls mainly"  *<li>output of CommomGramsFilter/input to CommonGramsQueryFilter:  * |"the, "the-rain"|"rain" "rain-in"|"in, "in-spain"|"spain"|"falls"|"mainly"  *<li>output of CommonGramsQueryFilter:"the-rain", "rain-in" ,"in-spain",  * "falls", "mainly"  *</ul>  */
end_comment

begin_comment
comment|/*  * See:http://hudson.zones  * .apache.org/hudson/job/Lucene-trunk/javadoc//all/org/apache  * /lucene/analysis/TokenStream.html and  * http://svn.apache.org/viewvc/lucene/java  * /trunk/src/java/org/apache/lucene/analysis/package.html?revision=718798  */
end_comment

begin_class
DECL|class|CommonGramsQueryFilter
specifier|public
specifier|final
class|class
name|CommonGramsQueryFilter
extends|extends
name|TokenFilter
block|{
DECL|field|typeAttribute
specifier|private
specifier|final
name|TypeAttribute
name|typeAttribute
init|=
operator|(
name|TypeAttribute
operator|)
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAttribute
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAttribute
init|=
operator|(
name|PositionIncrementAttribute
operator|)
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|previous
specifier|private
name|State
name|previous
decl_stmt|;
DECL|field|previousType
specifier|private
name|String
name|previousType
decl_stmt|;
comment|/**    * Constructs a new CommonGramsQueryFilter based on the provided CommomGramsFilter     *     * @param input CommonGramsFilter the QueryFilter will use    */
DECL|method|CommonGramsQueryFilter
specifier|public
name|CommonGramsQueryFilter
parameter_list|(
name|CommonGramsFilter
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    */
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
name|previous
operator|=
literal|null
expr_stmt|;
name|previousType
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Output bigrams whenever possible to optimize queries. Only output unigrams    * when they are not a member of a bigram. Example:    *<ul>    *<li>input: "the rain in spain falls mainly"    *<li>output:"the-rain", "rain-in" ,"in-spain", "falls", "mainly"    *</ul>    */
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|State
name|current
init|=
name|captureState
argument_list|()
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
operator|&&
operator|!
name|isGramType
argument_list|()
condition|)
block|{
name|restoreState
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|previous
operator|=
name|current
expr_stmt|;
name|previousType
operator|=
name|typeAttribute
operator|.
name|type
argument_list|()
expr_stmt|;
if|if
condition|(
name|isGramType
argument_list|()
condition|)
block|{
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
name|previous
operator|=
name|current
expr_stmt|;
block|}
if|if
condition|(
name|previous
operator|==
literal|null
operator|||
name|GRAM_TYPE
operator|.
name|equals
argument_list|(
name|previousType
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|restoreState
argument_list|(
name|previous
argument_list|)
expr_stmt|;
name|previous
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|isGramType
argument_list|()
condition|)
block|{
name|posIncAttribute
operator|.
name|setPositionIncrement
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|// ================================================= Helper Methods ================================================
comment|/**    * Convenience method to check if the current type is a gram type    *     * @return {@code true} if the current type is a gram type, {@code false} otherwise    */
DECL|method|isGramType
specifier|public
name|boolean
name|isGramType
parameter_list|()
block|{
return|return
name|GRAM_TYPE
operator|.
name|equals
argument_list|(
name|typeAttribute
operator|.
name|type
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

