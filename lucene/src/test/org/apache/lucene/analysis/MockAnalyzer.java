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
name|io
operator|.
name|Reader
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
comment|/**  * Analyzer for testing  */
end_comment

begin_class
DECL|class|MockAnalyzer
specifier|public
specifier|final
class|class
name|MockAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|runAutomaton
specifier|private
specifier|final
name|CharacterRunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|lowerCase
specifier|private
specifier|final
name|boolean
name|lowerCase
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|CharacterRunAutomaton
name|filter
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
specifier|final
name|boolean
name|enablePositionIncrements
decl_stmt|;
DECL|field|positionIncrementGap
specifier|private
name|int
name|positionIncrementGap
decl_stmt|;
comment|/**    * Creates a new MockAnalyzer.    *     * @param runAutomaton DFA describing how tokenization should happen (e.g. [a-zA-Z]+)    * @param lowerCase true if the tokenizer should lowercase terms    * @param filter DFA describing how terms should be filtered (set of stopwords, etc)    * @param enablePositionIncrements true if position increments should reflect filtered terms.    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|,
name|CharacterRunAutomaton
name|filter
parameter_list|,
name|boolean
name|enablePositionIncrements
parameter_list|)
block|{
name|this
operator|.
name|runAutomaton
operator|=
name|runAutomaton
expr_stmt|;
name|this
operator|.
name|lowerCase
operator|=
name|lowerCase
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|enablePositionIncrements
operator|=
name|enablePositionIncrements
expr_stmt|;
block|}
comment|/**    * Creates a new MockAnalyzer, with no filtering.    *     * @param runAutomaton DFA describing how tokenization should happen (e.g. [a-zA-Z]+)    * @param lowerCase true if the tokenizer should lowercase terms    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|)
block|{
name|this
argument_list|(
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|,
name|MockTokenFilter
operator|.
name|EMPTY_STOPSET
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**     * Create a Whitespace-lowercasing analyzer with no stopwords removal     */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|()
block|{
name|this
argument_list|(
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|MockTokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|)
decl_stmt|;
return|return
operator|new
name|MockTokenFilter
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|,
name|enablePositionIncrements
argument_list|)
return|;
block|}
DECL|class|SavedStreams
specifier|private
class|class
name|SavedStreams
block|{
DECL|field|tokenizer
name|MockTokenizer
name|tokenizer
decl_stmt|;
DECL|field|filter
name|MockTokenFilter
name|filter
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SavedStreams
name|saved
init|=
operator|(
name|SavedStreams
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|saved
operator|==
literal|null
condition|)
block|{
name|saved
operator|=
operator|new
name|SavedStreams
argument_list|()
expr_stmt|;
name|saved
operator|.
name|tokenizer
operator|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|)
expr_stmt|;
name|saved
operator|.
name|filter
operator|=
operator|new
name|MockTokenFilter
argument_list|(
name|saved
operator|.
name|tokenizer
argument_list|,
name|filter
argument_list|,
name|enablePositionIncrements
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|saved
argument_list|)
expr_stmt|;
return|return
name|saved
operator|.
name|filter
return|;
block|}
else|else
block|{
name|saved
operator|.
name|tokenizer
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|saved
operator|.
name|filter
return|;
block|}
block|}
DECL|method|setPositionIncrementGap
specifier|public
name|void
name|setPositionIncrementGap
parameter_list|(
name|int
name|positionIncrementGap
parameter_list|)
block|{
name|this
operator|.
name|positionIncrementGap
operator|=
name|positionIncrementGap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|positionIncrementGap
return|;
block|}
block|}
end_class

end_unit

