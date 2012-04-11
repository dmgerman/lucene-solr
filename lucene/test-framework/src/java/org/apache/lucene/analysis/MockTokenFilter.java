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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|BasicAutomata
operator|.
name|makeEmpty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|BasicAutomata
operator|.
name|makeString
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
name|Arrays
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
name|util
operator|.
name|automaton
operator|.
name|BasicOperations
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
name|automaton
operator|.
name|CharacterRunAutomaton
import|;
end_import

begin_comment
comment|/**  * A tokenfilter for testing that removes terms accepted by a DFA.  *<ul>  *<li>Union a list of singletons to act like a stopfilter.  *<li>Use the complement to act like a keepwordfilter  *<li>Use a regex like<code>.{12,}</code> to act like a lengthfilter  *</ul>  */
end_comment

begin_class
DECL|class|MockTokenFilter
specifier|public
specifier|final
class|class
name|MockTokenFilter
extends|extends
name|TokenFilter
block|{
comment|/** Empty set of stopwords */
DECL|field|EMPTY_STOPSET
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|EMPTY_STOPSET
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|makeEmpty
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Set of common english stopwords */
DECL|field|ENGLISH_STOPSET
specifier|public
specifier|static
specifier|final
name|CharacterRunAutomaton
name|ENGLISH_STOPSET
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|BasicOperations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|makeString
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"an"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"and"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"are"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"as"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"at"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"be"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"but"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"by"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"for"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"if"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"in"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"into"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"is"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"it"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"no"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"not"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"of"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"on"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"or"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"such"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"that"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"the"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"their"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"then"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"there"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"these"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"they"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"this"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"to"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"was"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"will"
argument_list|)
argument_list|,
name|makeString
argument_list|(
literal|"with"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|CharacterRunAutomaton
name|filter
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
init|=
literal|true
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
DECL|field|posIncrAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncrAtt
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a new MockTokenFilter.    *     * @param input TokenStream to filter    * @param filter DFA representing the terms that should be removed.    */
DECL|method|MockTokenFilter
specifier|public
name|MockTokenFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|CharacterRunAutomaton
name|filter
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
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
comment|// TODO: fix me when posInc=false, to work like FilteringTokenFilter in that case and not return
comment|// initial token with posInc=0 ever
comment|// return the first non-stop word found
name|int
name|skippedPositions
init|=
literal|0
decl_stmt|;
while|while
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
name|filter
operator|.
name|run
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
condition|)
block|{
if|if
condition|(
name|enablePositionIncrements
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|+
name|skippedPositions
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
name|skippedPositions
operator|+=
name|posIncrAtt
operator|.
name|getPositionIncrement
argument_list|()
expr_stmt|;
block|}
comment|// reached EOS -- return false
return|return
literal|false
return|;
block|}
comment|/**    * @see #setEnablePositionIncrements(boolean)    */
DECL|method|getEnablePositionIncrements
specifier|public
name|boolean
name|getEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
comment|/**    * If<code>true</code>, this Filter will preserve    * positions of the incoming tokens (ie, accumulate and    * set position increments of the removed stop tokens).    */
DECL|method|setEnablePositionIncrements
specifier|public
name|void
name|setEnablePositionIncrements
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enable
expr_stmt|;
block|}
block|}
end_class

end_unit

