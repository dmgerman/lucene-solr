begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|spell
operator|.
name|SuggestMode
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  *<p>  * A spell checker whose sole function is to offer suggestions by combining  * multiple terms into one word and/or breaking terms into multiple words.  *</p>  */
end_comment

begin_class
DECL|class|WordBreakSpellChecker
specifier|public
class|class
name|WordBreakSpellChecker
block|{
DECL|field|minSuggestionFrequency
specifier|private
name|int
name|minSuggestionFrequency
init|=
literal|1
decl_stmt|;
DECL|field|minBreakWordLength
specifier|private
name|int
name|minBreakWordLength
init|=
literal|1
decl_stmt|;
DECL|field|maxCombineWordLength
specifier|private
name|int
name|maxCombineWordLength
init|=
literal|20
decl_stmt|;
DECL|field|maxChanges
specifier|private
name|int
name|maxChanges
init|=
literal|1
decl_stmt|;
DECL|field|maxEvaluations
specifier|private
name|int
name|maxEvaluations
init|=
literal|1000
decl_stmt|;
comment|/** Term that can be used to prohibit adjacent terms from being combined */
DECL|field|SEPARATOR_TERM
specifier|public
specifier|static
specifier|final
name|Term
name|SEPARATOR_TERM
init|=
operator|new
name|Term
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/**     * Creates a new spellchecker with default configuration values    * @see #setMaxChanges(int)    * @see #setMaxCombineWordLength(int)    * @see #setMaxEvaluations(int)    * @see #setMinBreakWordLength(int)    * @see #setMinSuggestionFrequency(int)    */
DECL|method|WordBreakSpellChecker
specifier|public
name|WordBreakSpellChecker
parameter_list|()
block|{}
comment|/**    *<p>    * Determines the order to list word break suggestions    *</p>    */
DECL|enum|BreakSuggestionSortMethod
specifier|public
enum|enum
name|BreakSuggestionSortMethod
block|{
comment|/**      *<p>      * Sort by Number of word breaks, then by the Sum of all the component      * term's frequencies      *</p>      */
DECL|enum constant|NUM_CHANGES_THEN_SUMMED_FREQUENCY
name|NUM_CHANGES_THEN_SUMMED_FREQUENCY
block|,
comment|/**      *<p>      * Sort by Number of word breaks, then by the Maximum of all the component      * term's frequencies      *</p>      */
DECL|enum constant|NUM_CHANGES_THEN_MAX_FREQUENCY
name|NUM_CHANGES_THEN_MAX_FREQUENCY
block|}
comment|/**    *<p>    * Generate suggestions by breaking the passed-in term into multiple words.    * The scores returned are equal to the number of word breaks needed so a    * lower score is generally preferred over a higher score.    *</p>    *     * @param suggestMode    *          - default = {@link SuggestMode#SUGGEST_WHEN_NOT_IN_INDEX}    * @param sortMethod    *          - default =    *          {@link BreakSuggestionSortMethod#NUM_CHANGES_THEN_MAX_FREQUENCY}    * @return one or more arrays of words formed by breaking up the original term    * @throws IOException If there is a low-level I/O error.    */
DECL|method|suggestWordBreaks
specifier|public
name|SuggestWord
index|[]
index|[]
name|suggestWordBreaks
parameter_list|(
name|Term
name|term
parameter_list|,
name|int
name|maxSuggestions
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|SuggestMode
name|suggestMode
parameter_list|,
name|BreakSuggestionSortMethod
name|sortMethod
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|maxSuggestions
operator|<
literal|1
condition|)
block|{
return|return
operator|new
name|SuggestWord
index|[
literal|0
index|]
index|[
literal|0
index|]
return|;
block|}
if|if
condition|(
name|suggestMode
operator|==
literal|null
condition|)
block|{
name|suggestMode
operator|=
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
expr_stmt|;
block|}
if|if
condition|(
name|sortMethod
operator|==
literal|null
condition|)
block|{
name|sortMethod
operator|=
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
expr_stmt|;
block|}
name|int
name|queueInitialCapacity
init|=
name|maxSuggestions
operator|>
literal|10
condition|?
literal|10
else|:
name|maxSuggestions
decl_stmt|;
name|Comparator
argument_list|<
name|SuggestWordArrayWrapper
argument_list|>
name|queueComparator
init|=
name|sortMethod
operator|==
name|BreakSuggestionSortMethod
operator|.
name|NUM_CHANGES_THEN_MAX_FREQUENCY
condition|?
operator|new
name|LengthThenMaxFreqComparator
argument_list|()
else|:
operator|new
name|LengthThenSumFreqComparator
argument_list|()
decl_stmt|;
name|Queue
argument_list|<
name|SuggestWordArrayWrapper
argument_list|>
name|suggestions
init|=
operator|new
name|PriorityQueue
argument_list|<
name|SuggestWordArrayWrapper
argument_list|>
argument_list|(
name|queueInitialCapacity
argument_list|,
name|queueComparator
argument_list|)
decl_stmt|;
name|int
name|origFreq
init|=
name|ir
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|origFreq
operator|>
literal|0
operator|&&
name|suggestMode
operator|==
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
condition|)
block|{
return|return
operator|new
name|SuggestWord
index|[
literal|0
index|]
index|[]
return|;
block|}
name|int
name|useMinSuggestionFrequency
init|=
name|minSuggestionFrequency
decl_stmt|;
if|if
condition|(
name|suggestMode
operator|==
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
condition|)
block|{
name|useMinSuggestionFrequency
operator|=
operator|(
name|origFreq
operator|==
literal|0
condition|?
literal|1
else|:
name|origFreq
operator|)
expr_stmt|;
block|}
name|generateBreakUpSuggestions
argument_list|(
name|term
argument_list|,
name|ir
argument_list|,
literal|1
argument_list|,
name|maxSuggestions
argument_list|,
name|useMinSuggestionFrequency
argument_list|,
operator|new
name|SuggestWord
index|[
literal|0
index|]
argument_list|,
name|suggestions
argument_list|,
literal|0
argument_list|,
name|sortMethod
argument_list|)
expr_stmt|;
name|SuggestWord
index|[]
index|[]
name|suggestionArray
init|=
operator|new
name|SuggestWord
index|[
name|suggestions
operator|.
name|size
argument_list|()
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|suggestions
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|suggestionArray
index|[
name|i
index|]
operator|=
name|suggestions
operator|.
name|remove
argument_list|()
operator|.
name|suggestWords
expr_stmt|;
block|}
return|return
name|suggestionArray
return|;
block|}
comment|/**    *<p>    * Generate suggestions by combining one or more of the passed-in terms into    * single words. The returned {@link CombineSuggestion} contains both a    * {@link SuggestWord} and also an array detailing which passed-in terms were    * involved in creating this combination. The scores returned are equal to the    * number of word combinations needed, also one less than the length of the    * array {@link CombineSuggestion#originalTermIndexes}. Generally, a    * suggestion with a lower score is preferred over a higher score.    *</p>    *<p>    * To prevent two adjacent terms from being combined (for instance, if one is    * mandatory and the other is prohibited), separate the two terms with    * {@link WordBreakSpellChecker#SEPARATOR_TERM}    *</p>    *<p>    * When suggestMode equals {@link SuggestMode#SUGGEST_WHEN_NOT_IN_INDEX}, each    * suggestion will include at least one term not in the index.    *</p>    *<p>    * When suggestMode equals {@link SuggestMode#SUGGEST_MORE_POPULAR}, each    * suggestion will have the same, or better frequency than the most-popular    * included term.    *</p>    *     * @return an array of words generated by combining original terms    * @throws IOException If there is a low-level I/O error.    */
DECL|method|suggestWordCombinations
specifier|public
name|CombineSuggestion
index|[]
name|suggestWordCombinations
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|,
name|int
name|maxSuggestions
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|SuggestMode
name|suggestMode
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|maxSuggestions
operator|<
literal|1
condition|)
block|{
return|return
operator|new
name|CombineSuggestion
index|[
literal|0
index|]
return|;
block|}
name|int
index|[]
name|origFreqs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|suggestMode
operator|!=
name|SuggestMode
operator|.
name|SUGGEST_ALWAYS
condition|)
block|{
name|origFreqs
operator|=
operator|new
name|int
index|[
name|terms
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|origFreqs
index|[
name|i
index|]
operator|=
name|ir
operator|.
name|docFreq
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|queueInitialCapacity
init|=
name|maxSuggestions
operator|>
literal|10
condition|?
literal|10
else|:
name|maxSuggestions
decl_stmt|;
name|Comparator
argument_list|<
name|CombineSuggestionWrapper
argument_list|>
name|queueComparator
init|=
operator|new
name|CombinationsThenFreqComparator
argument_list|()
decl_stmt|;
name|Queue
argument_list|<
name|CombineSuggestionWrapper
argument_list|>
name|suggestions
init|=
operator|new
name|PriorityQueue
argument_list|<
name|CombineSuggestionWrapper
argument_list|>
argument_list|(
name|queueInitialCapacity
argument_list|,
name|queueComparator
argument_list|)
decl_stmt|;
name|int
name|thisTimeEvaluations
init|=
literal|0
decl_stmt|;
name|BytesRef
name|reuse
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|terms
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|terms
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|SEPARATOR_TERM
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|int
name|byteLength
init|=
name|terms
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|byteLength
operator|>
name|maxCombineWordLength
condition|)
block|{
continue|continue;
block|}
name|reuse
operator|.
name|grow
argument_list|(
name|byteLength
argument_list|)
expr_stmt|;
name|reuse
operator|.
name|length
operator|=
name|byteLength
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|terms
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|bytes
argument_list|,
name|terms
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|offset
argument_list|,
name|reuse
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|byteLength
argument_list|)
expr_stmt|;
name|int
name|maxFreq
init|=
literal|0
decl_stmt|;
name|int
name|minFreq
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|origFreqs
operator|!=
literal|null
condition|)
block|{
name|maxFreq
operator|=
name|origFreqs
index|[
name|i
index|]
expr_stmt|;
name|minFreq
operator|=
name|origFreqs
index|[
name|i
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|terms
operator|.
name|length
operator|&&
name|j
operator|-
name|i
operator|<=
name|maxChanges
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|terms
index|[
name|j
index|]
operator|.
name|equals
argument_list|(
name|SEPARATOR_TERM
argument_list|)
condition|)
block|{
break|break;
block|}
name|byteLength
operator|+=
name|terms
index|[
name|j
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|byteLength
operator|>
name|maxCombineWordLength
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|origFreqs
operator|!=
literal|null
condition|)
block|{
name|maxFreq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxFreq
argument_list|,
name|origFreqs
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|minFreq
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minFreq
argument_list|,
name|origFreqs
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|reuse
operator|.
name|grow
argument_list|(
name|byteLength
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|terms
index|[
name|j
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|bytes
argument_list|,
name|terms
index|[
name|j
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|offset
argument_list|,
name|reuse
operator|.
name|bytes
argument_list|,
name|reuse
operator|.
name|length
argument_list|,
name|terms
index|[
name|j
index|]
operator|.
name|bytes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|reuse
operator|.
name|length
operator|=
name|byteLength
expr_stmt|;
name|Term
name|combinedTerm
init|=
operator|new
name|Term
argument_list|(
name|terms
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
argument_list|,
name|reuse
argument_list|)
decl_stmt|;
name|int
name|combinedTermFreq
init|=
name|ir
operator|.
name|docFreq
argument_list|(
name|combinedTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggestMode
operator|!=
name|SuggestMode
operator|.
name|SUGGEST_MORE_POPULAR
operator|||
name|combinedTermFreq
operator|>=
name|maxFreq
condition|)
block|{
if|if
condition|(
name|suggestMode
operator|!=
name|SuggestMode
operator|.
name|SUGGEST_WHEN_NOT_IN_INDEX
operator|||
name|minFreq
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|combinedTermFreq
operator|>=
name|minSuggestionFrequency
condition|)
block|{
name|int
index|[]
name|origIndexes
init|=
operator|new
name|int
index|[
name|j
operator|-
name|i
operator|+
literal|1
index|]
decl_stmt|;
name|origIndexes
index|[
literal|0
index|]
operator|=
name|i
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|k
operator|<
name|origIndexes
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|origIndexes
index|[
name|k
index|]
operator|=
name|i
operator|+
name|k
expr_stmt|;
block|}
name|SuggestWord
name|word
init|=
operator|new
name|SuggestWord
argument_list|()
decl_stmt|;
name|word
operator|.
name|freq
operator|=
name|combinedTermFreq
expr_stmt|;
name|word
operator|.
name|score
operator|=
name|origIndexes
operator|.
name|length
operator|-
literal|1
expr_stmt|;
name|word
operator|.
name|string
operator|=
name|combinedTerm
operator|.
name|text
argument_list|()
expr_stmt|;
name|CombineSuggestionWrapper
name|suggestion
init|=
operator|new
name|CombineSuggestionWrapper
argument_list|(
operator|new
name|CombineSuggestion
argument_list|(
name|word
argument_list|,
name|origIndexes
argument_list|)
argument_list|,
operator|(
name|origIndexes
operator|.
name|length
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
name|suggestions
operator|.
name|offer
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestions
operator|.
name|size
argument_list|()
operator|>
name|maxSuggestions
condition|)
block|{
name|suggestions
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|thisTimeEvaluations
operator|++
expr_stmt|;
if|if
condition|(
name|thisTimeEvaluations
operator|==
name|maxEvaluations
condition|)
block|{
break|break;
block|}
block|}
block|}
name|CombineSuggestion
index|[]
name|combineSuggestions
init|=
operator|new
name|CombineSuggestion
index|[
name|suggestions
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|suggestions
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|combineSuggestions
index|[
name|i
index|]
operator|=
name|suggestions
operator|.
name|remove
argument_list|()
operator|.
name|combineSuggestion
expr_stmt|;
block|}
return|return
name|combineSuggestions
return|;
block|}
DECL|method|generateBreakUpSuggestions
specifier|private
name|int
name|generateBreakUpSuggestions
parameter_list|(
name|Term
name|term
parameter_list|,
name|IndexReader
name|ir
parameter_list|,
name|int
name|numberBreaks
parameter_list|,
name|int
name|maxSuggestions
parameter_list|,
name|int
name|useMinSuggestionFrequency
parameter_list|,
name|SuggestWord
index|[]
name|prefix
parameter_list|,
name|Queue
argument_list|<
name|SuggestWordArrayWrapper
argument_list|>
name|suggestions
parameter_list|,
name|int
name|totalEvaluations
parameter_list|,
name|BreakSuggestionSortMethod
name|sortMethod
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|termLength
init|=
name|term
operator|.
name|bytes
argument_list|()
operator|.
name|length
decl_stmt|;
name|int
name|useMinBreakWordLength
init|=
name|minBreakWordLength
decl_stmt|;
if|if
condition|(
name|useMinBreakWordLength
operator|<
literal|1
condition|)
block|{
name|useMinBreakWordLength
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|termLength
operator|<=
operator|(
name|useMinBreakWordLength
operator|*
literal|2
operator|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|thisTimeEvaluations
init|=
literal|0
decl_stmt|;
name|BytesRef
name|termBytes
init|=
name|term
operator|.
name|bytes
argument_list|()
operator|.
name|clone
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|useMinBreakWordLength
init|;
name|i
operator|<
operator|(
name|termLength
operator|-
name|useMinBreakWordLength
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|SuggestWord
name|leftWord
init|=
name|generateSuggestWord
argument_list|(
name|ir
argument_list|,
name|termBytes
argument_list|,
literal|0
argument_list|,
name|i
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|leftWord
operator|.
name|freq
operator|>=
name|useMinSuggestionFrequency
condition|)
block|{
name|SuggestWord
name|rightWord
init|=
name|generateSuggestWord
argument_list|(
name|ir
argument_list|,
name|termBytes
argument_list|,
name|i
argument_list|,
name|termLength
operator|-
name|i
argument_list|,
name|term
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rightWord
operator|.
name|freq
operator|>=
name|useMinSuggestionFrequency
condition|)
block|{
name|SuggestWordArrayWrapper
name|suggestion
init|=
operator|new
name|SuggestWordArrayWrapper
argument_list|(
name|newSuggestion
argument_list|(
name|prefix
argument_list|,
name|leftWord
argument_list|,
name|rightWord
argument_list|)
argument_list|)
decl_stmt|;
name|suggestions
operator|.
name|offer
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestions
operator|.
name|size
argument_list|()
operator|>
name|maxSuggestions
condition|)
block|{
name|suggestions
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
name|int
name|newNumberBreaks
init|=
name|numberBreaks
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|newNumberBreaks
operator|<=
name|maxChanges
condition|)
block|{
name|int
name|evaluations
init|=
name|generateBreakUpSuggestions
argument_list|(
operator|new
name|Term
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|rightWord
operator|.
name|string
argument_list|)
argument_list|,
name|ir
argument_list|,
name|newNumberBreaks
argument_list|,
name|maxSuggestions
argument_list|,
name|useMinSuggestionFrequency
argument_list|,
name|newPrefix
argument_list|(
name|prefix
argument_list|,
name|leftWord
argument_list|)
argument_list|,
name|suggestions
argument_list|,
name|totalEvaluations
argument_list|,
name|sortMethod
argument_list|)
decl_stmt|;
name|totalEvaluations
operator|+=
name|evaluations
expr_stmt|;
block|}
block|}
name|thisTimeEvaluations
operator|++
expr_stmt|;
name|totalEvaluations
operator|++
expr_stmt|;
if|if
condition|(
name|totalEvaluations
operator|>=
name|maxEvaluations
condition|)
block|{
break|break;
block|}
block|}
return|return
name|thisTimeEvaluations
return|;
block|}
DECL|method|newPrefix
specifier|private
name|SuggestWord
index|[]
name|newPrefix
parameter_list|(
name|SuggestWord
index|[]
name|oldPrefix
parameter_list|,
name|SuggestWord
name|append
parameter_list|)
block|{
name|SuggestWord
index|[]
name|newPrefix
init|=
operator|new
name|SuggestWord
index|[
name|oldPrefix
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|oldPrefix
argument_list|,
literal|0
argument_list|,
name|newPrefix
argument_list|,
literal|0
argument_list|,
name|oldPrefix
operator|.
name|length
argument_list|)
expr_stmt|;
name|newPrefix
index|[
name|newPrefix
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|append
expr_stmt|;
return|return
name|newPrefix
return|;
block|}
DECL|method|newSuggestion
specifier|private
name|SuggestWord
index|[]
name|newSuggestion
parameter_list|(
name|SuggestWord
index|[]
name|prefix
parameter_list|,
name|SuggestWord
name|append1
parameter_list|,
name|SuggestWord
name|append2
parameter_list|)
block|{
name|SuggestWord
index|[]
name|newSuggestion
init|=
operator|new
name|SuggestWord
index|[
name|prefix
operator|.
name|length
operator|+
literal|2
index|]
decl_stmt|;
name|int
name|score
init|=
name|prefix
operator|.
name|length
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SuggestWord
name|word
init|=
operator|new
name|SuggestWord
argument_list|()
decl_stmt|;
name|word
operator|.
name|string
operator|=
name|prefix
index|[
name|i
index|]
operator|.
name|string
expr_stmt|;
name|word
operator|.
name|freq
operator|=
name|prefix
index|[
name|i
index|]
operator|.
name|freq
expr_stmt|;
name|word
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|newSuggestion
index|[
name|i
index|]
operator|=
name|word
expr_stmt|;
block|}
name|append1
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|append2
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|newSuggestion
index|[
name|newSuggestion
operator|.
name|length
operator|-
literal|2
index|]
operator|=
name|append1
expr_stmt|;
name|newSuggestion
index|[
name|newSuggestion
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|append2
expr_stmt|;
return|return
name|newSuggestion
return|;
block|}
DECL|method|generateSuggestWord
specifier|private
name|SuggestWord
name|generateSuggestWord
parameter_list|(
name|IndexReader
name|ir
parameter_list|,
name|BytesRef
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|String
name|fieldname
parameter_list|)
throws|throws
name|IOException
block|{
name|bytes
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|bytes
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
name|fieldname
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|int
name|freq
init|=
name|ir
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|SuggestWord
name|word
init|=
operator|new
name|SuggestWord
argument_list|()
decl_stmt|;
name|word
operator|.
name|freq
operator|=
name|freq
expr_stmt|;
name|word
operator|.
name|score
operator|=
literal|1
expr_stmt|;
name|word
operator|.
name|string
operator|=
name|term
operator|.
name|text
argument_list|()
expr_stmt|;
return|return
name|word
return|;
block|}
comment|/**    * Returns the minimum frequency a term must have    * to be part of a suggestion.    * @see #setMinSuggestionFrequency(int)    */
DECL|method|getMinSuggestionFrequency
specifier|public
name|int
name|getMinSuggestionFrequency
parameter_list|()
block|{
return|return
name|minSuggestionFrequency
return|;
block|}
comment|/**    * Returns the maximum length of a combined suggestion    * @see #setMaxCombineWordLength(int)    */
DECL|method|getMaxCombineWordLength
specifier|public
name|int
name|getMaxCombineWordLength
parameter_list|()
block|{
return|return
name|maxCombineWordLength
return|;
block|}
comment|/**    * Returns the minimum size of a broken word    * @see #setMinBreakWordLength(int)    */
DECL|method|getMinBreakWordLength
specifier|public
name|int
name|getMinBreakWordLength
parameter_list|()
block|{
return|return
name|minBreakWordLength
return|;
block|}
comment|/**    * Returns the maximum number of changes to perform on the input    * @see #setMaxChanges(int)    */
DECL|method|getMaxChanges
specifier|public
name|int
name|getMaxChanges
parameter_list|()
block|{
return|return
name|maxChanges
return|;
block|}
comment|/**    * Returns the maximum number of word combinations to evaluate.    * @see #setMaxEvaluations(int)    */
DECL|method|getMaxEvaluations
specifier|public
name|int
name|getMaxEvaluations
parameter_list|()
block|{
return|return
name|maxEvaluations
return|;
block|}
comment|/**    *<p>    * The minimum frequency a term must have to be included as part of a    * suggestion. Default=1 Not applicable when used with    * {@link SuggestMode#SUGGEST_MORE_POPULAR}    *</p>    *     * @see #getMinSuggestionFrequency()    */
DECL|method|setMinSuggestionFrequency
specifier|public
name|void
name|setMinSuggestionFrequency
parameter_list|(
name|int
name|minSuggestionFrequency
parameter_list|)
block|{
name|this
operator|.
name|minSuggestionFrequency
operator|=
name|minSuggestionFrequency
expr_stmt|;
block|}
comment|/**    *<p>    * The maximum length of a suggestion made by combining 1 or more original    * terms. Default=20    *</p>    *     * @see #getMaxCombineWordLength()    */
DECL|method|setMaxCombineWordLength
specifier|public
name|void
name|setMaxCombineWordLength
parameter_list|(
name|int
name|maxCombineWordLength
parameter_list|)
block|{
name|this
operator|.
name|maxCombineWordLength
operator|=
name|maxCombineWordLength
expr_stmt|;
block|}
comment|/**    *<p>    * The minimum length to break words down to. Default=1    *</p>    *     * @see #getMinBreakWordLength()    */
DECL|method|setMinBreakWordLength
specifier|public
name|void
name|setMinBreakWordLength
parameter_list|(
name|int
name|minBreakWordLength
parameter_list|)
block|{
name|this
operator|.
name|minBreakWordLength
operator|=
name|minBreakWordLength
expr_stmt|;
block|}
comment|/**    *<p>    * The maximum numbers of changes (word breaks or combinations) to make on the    * original term(s). Default=1    *</p>    *     * @see #getMaxChanges()    */
DECL|method|setMaxChanges
specifier|public
name|void
name|setMaxChanges
parameter_list|(
name|int
name|maxChanges
parameter_list|)
block|{
name|this
operator|.
name|maxChanges
operator|=
name|maxChanges
expr_stmt|;
block|}
comment|/**    *<p>    * The maximum number of word combinations to evaluate. Default=1000. A higher    * value might improve result quality. A lower value might improve    * performance.    *</p>    *     * @see #getMaxEvaluations()    */
DECL|method|setMaxEvaluations
specifier|public
name|void
name|setMaxEvaluations
parameter_list|(
name|int
name|maxEvaluations
parameter_list|)
block|{
name|this
operator|.
name|maxEvaluations
operator|=
name|maxEvaluations
expr_stmt|;
block|}
DECL|class|LengthThenMaxFreqComparator
specifier|private
class|class
name|LengthThenMaxFreqComparator
implements|implements
name|Comparator
argument_list|<
name|SuggestWordArrayWrapper
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|SuggestWordArrayWrapper
name|o1
parameter_list|,
name|SuggestWordArrayWrapper
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|suggestWords
operator|.
name|length
operator|!=
name|o2
operator|.
name|suggestWords
operator|.
name|length
condition|)
block|{
return|return
name|o2
operator|.
name|suggestWords
operator|.
name|length
operator|-
name|o1
operator|.
name|suggestWords
operator|.
name|length
return|;
block|}
if|if
condition|(
name|o1
operator|.
name|freqMax
operator|!=
name|o2
operator|.
name|freqMax
condition|)
block|{
return|return
name|o1
operator|.
name|freqMax
operator|-
name|o2
operator|.
name|freqMax
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|class|LengthThenSumFreqComparator
specifier|private
class|class
name|LengthThenSumFreqComparator
implements|implements
name|Comparator
argument_list|<
name|SuggestWordArrayWrapper
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|SuggestWordArrayWrapper
name|o1
parameter_list|,
name|SuggestWordArrayWrapper
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|suggestWords
operator|.
name|length
operator|!=
name|o2
operator|.
name|suggestWords
operator|.
name|length
condition|)
block|{
return|return
name|o2
operator|.
name|suggestWords
operator|.
name|length
operator|-
name|o1
operator|.
name|suggestWords
operator|.
name|length
return|;
block|}
if|if
condition|(
name|o1
operator|.
name|freqSum
operator|!=
name|o2
operator|.
name|freqSum
condition|)
block|{
return|return
name|o1
operator|.
name|freqSum
operator|-
name|o2
operator|.
name|freqSum
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|class|CombinationsThenFreqComparator
specifier|private
class|class
name|CombinationsThenFreqComparator
implements|implements
name|Comparator
argument_list|<
name|CombineSuggestionWrapper
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|CombineSuggestionWrapper
name|o1
parameter_list|,
name|CombineSuggestionWrapper
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|numCombinations
operator|!=
name|o2
operator|.
name|numCombinations
condition|)
block|{
return|return
name|o2
operator|.
name|numCombinations
operator|-
name|o1
operator|.
name|numCombinations
return|;
block|}
if|if
condition|(
name|o1
operator|.
name|combineSuggestion
operator|.
name|suggestion
operator|.
name|freq
operator|!=
name|o2
operator|.
name|combineSuggestion
operator|.
name|suggestion
operator|.
name|freq
condition|)
block|{
return|return
name|o1
operator|.
name|combineSuggestion
operator|.
name|suggestion
operator|.
name|freq
operator|-
name|o2
operator|.
name|combineSuggestion
operator|.
name|suggestion
operator|.
name|freq
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
DECL|class|SuggestWordArrayWrapper
specifier|private
class|class
name|SuggestWordArrayWrapper
block|{
DECL|field|suggestWords
specifier|final
name|SuggestWord
index|[]
name|suggestWords
decl_stmt|;
DECL|field|freqMax
specifier|final
name|int
name|freqMax
decl_stmt|;
DECL|field|freqSum
specifier|final
name|int
name|freqSum
decl_stmt|;
DECL|method|SuggestWordArrayWrapper
name|SuggestWordArrayWrapper
parameter_list|(
name|SuggestWord
index|[]
name|suggestWords
parameter_list|)
block|{
name|this
operator|.
name|suggestWords
operator|=
name|suggestWords
expr_stmt|;
name|int
name|aFreqSum
init|=
literal|0
decl_stmt|;
name|int
name|aFreqMax
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SuggestWord
name|sw
range|:
name|suggestWords
control|)
block|{
name|aFreqSum
operator|+=
name|sw
operator|.
name|freq
expr_stmt|;
name|aFreqMax
operator|=
name|Math
operator|.
name|max
argument_list|(
name|aFreqMax
argument_list|,
name|sw
operator|.
name|freq
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|freqSum
operator|=
name|aFreqSum
expr_stmt|;
name|this
operator|.
name|freqMax
operator|=
name|aFreqMax
expr_stmt|;
block|}
block|}
DECL|class|CombineSuggestionWrapper
specifier|private
class|class
name|CombineSuggestionWrapper
block|{
DECL|field|combineSuggestion
specifier|final
name|CombineSuggestion
name|combineSuggestion
decl_stmt|;
DECL|field|numCombinations
specifier|final
name|int
name|numCombinations
decl_stmt|;
DECL|method|CombineSuggestionWrapper
name|CombineSuggestionWrapper
parameter_list|(
name|CombineSuggestion
name|combineSuggestion
parameter_list|,
name|int
name|numCombinations
parameter_list|)
block|{
name|this
operator|.
name|combineSuggestion
operator|=
name|combineSuggestion
expr_stmt|;
name|this
operator|.
name|numCombinations
operator|=
name|numCombinations
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

