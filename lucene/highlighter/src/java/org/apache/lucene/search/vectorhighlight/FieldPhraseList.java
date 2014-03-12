begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|vectorhighlight
operator|.
name|FieldQuery
operator|.
name|QueryPhraseMap
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
name|vectorhighlight
operator|.
name|FieldTermStack
operator|.
name|TermInfo
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
name|MergedIterator
import|;
end_import

begin_comment
comment|/**  * FieldPhraseList has a list of WeightedPhraseInfo that is used by FragListBuilder  * to create a FieldFragList object.  */
end_comment

begin_class
DECL|class|FieldPhraseList
specifier|public
class|class
name|FieldPhraseList
block|{
comment|/**    * List of non-overlapping WeightedPhraseInfo objects.    */
DECL|field|phraseList
name|LinkedList
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|phraseList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * create a FieldPhraseList that has no limit on the number of phrases to analyze    *     * @param fieldTermStack FieldTermStack object    * @param fieldQuery FieldQuery object    */
DECL|method|FieldPhraseList
specifier|public
name|FieldPhraseList
parameter_list|(
name|FieldTermStack
name|fieldTermStack
parameter_list|,
name|FieldQuery
name|fieldQuery
parameter_list|)
block|{
name|this
argument_list|(
name|fieldTermStack
argument_list|,
name|fieldQuery
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**    * return the list of WeightedPhraseInfo.    *     * @return phraseList.    */
DECL|method|getPhraseList
specifier|public
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|getPhraseList
parameter_list|()
block|{
return|return
name|phraseList
return|;
block|}
comment|/**    * a constructor.    *     * @param fieldTermStack FieldTermStack object    * @param fieldQuery FieldQuery object    * @param phraseLimit maximum size of phraseList    */
DECL|method|FieldPhraseList
specifier|public
name|FieldPhraseList
parameter_list|(
name|FieldTermStack
name|fieldTermStack
parameter_list|,
name|FieldQuery
name|fieldQuery
parameter_list|,
name|int
name|phraseLimit
parameter_list|)
block|{
specifier|final
name|String
name|field
init|=
name|fieldTermStack
operator|.
name|getFieldName
argument_list|()
decl_stmt|;
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
name|phraseCandidate
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|QueryPhraseMap
name|currMap
init|=
literal|null
decl_stmt|;
name|QueryPhraseMap
name|nextMap
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|fieldTermStack
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|phraseList
operator|.
name|size
argument_list|()
operator|<
name|phraseLimit
operator|)
condition|)
block|{
name|phraseCandidate
operator|.
name|clear
argument_list|()
expr_stmt|;
name|TermInfo
name|ti
init|=
name|fieldTermStack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|currMap
operator|=
name|fieldQuery
operator|.
name|getFieldTermMap
argument_list|(
name|field
argument_list|,
name|ti
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
comment|// if not found, discard top TermInfo from stack, then try next element
if|if
condition|(
name|currMap
operator|==
literal|null
condition|)
continue|continue;
comment|// if found, search the longest phrase
name|phraseCandidate
operator|.
name|add
argument_list|(
name|ti
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|ti
operator|=
name|fieldTermStack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|nextMap
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
name|nextMap
operator|=
name|currMap
operator|.
name|getTermMap
argument_list|(
name|ti
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|==
literal|null
operator|||
name|nextMap
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|ti
operator|!=
literal|null
condition|)
name|fieldTermStack
operator|.
name|push
argument_list|(
name|ti
argument_list|)
expr_stmt|;
if|if
condition|(
name|currMap
operator|.
name|isValidTermOrPhrase
argument_list|(
name|phraseCandidate
argument_list|)
condition|)
block|{
name|addIfNoOverlap
argument_list|(
operator|new
name|WeightedPhraseInfo
argument_list|(
name|phraseCandidate
argument_list|,
name|currMap
operator|.
name|getBoost
argument_list|()
argument_list|,
name|currMap
operator|.
name|getTermOrPhraseNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|phraseCandidate
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|fieldTermStack
operator|.
name|push
argument_list|(
name|phraseCandidate
operator|.
name|removeLast
argument_list|()
argument_list|)
expr_stmt|;
name|currMap
operator|=
name|fieldQuery
operator|.
name|searchPhrase
argument_list|(
name|field
argument_list|,
name|phraseCandidate
argument_list|)
expr_stmt|;
if|if
condition|(
name|currMap
operator|!=
literal|null
condition|)
block|{
name|addIfNoOverlap
argument_list|(
operator|new
name|WeightedPhraseInfo
argument_list|(
name|phraseCandidate
argument_list|,
name|currMap
operator|.
name|getBoost
argument_list|()
argument_list|,
name|currMap
operator|.
name|getTermOrPhraseNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
break|break;
block|}
else|else
block|{
name|phraseCandidate
operator|.
name|add
argument_list|(
name|ti
argument_list|)
expr_stmt|;
name|currMap
operator|=
name|nextMap
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Merging constructor.    *    * @param toMerge FieldPhraseLists to merge to build this one    */
DECL|method|FieldPhraseList
specifier|public
name|FieldPhraseList
parameter_list|(
name|FieldPhraseList
index|[]
name|toMerge
parameter_list|)
block|{
comment|// Merge all overlapping WeightedPhraseInfos
comment|// Step 1.  Sort by startOffset, endOffset, and boost, in that order.
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
name|Iterator
argument_list|<
name|WeightedPhraseInfo
argument_list|>
index|[]
name|allInfos
init|=
operator|new
name|Iterator
index|[
name|toMerge
operator|.
name|length
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FieldPhraseList
name|fplToMerge
range|:
name|toMerge
control|)
block|{
name|allInfos
index|[
name|index
operator|++
index|]
operator|=
name|fplToMerge
operator|.
name|phraseList
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
name|MergedIterator
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|itr
init|=
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
literal|false
argument_list|,
name|allInfos
argument_list|)
decl_stmt|;
comment|// Step 2.  Walk the sorted list merging infos that overlap
name|phraseList
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|work
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|WeightedPhraseInfo
name|first
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|work
operator|.
name|add
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|int
name|workEndOffset
init|=
name|first
operator|.
name|getEndOffset
argument_list|()
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|WeightedPhraseInfo
name|current
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|getStartOffset
argument_list|()
operator|<=
name|workEndOffset
condition|)
block|{
name|workEndOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|workEndOffset
argument_list|,
name|current
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
name|work
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|work
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|phraseList
operator|.
name|add
argument_list|(
name|work
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|work
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|phraseList
operator|.
name|add
argument_list|(
operator|new
name|WeightedPhraseInfo
argument_list|(
name|work
argument_list|)
argument_list|)
expr_stmt|;
name|work
operator|.
name|clear
argument_list|()
expr_stmt|;
name|work
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|workEndOffset
operator|=
name|current
operator|.
name|getEndOffset
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|work
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|phraseList
operator|.
name|add
argument_list|(
name|work
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|phraseList
operator|.
name|add
argument_list|(
operator|new
name|WeightedPhraseInfo
argument_list|(
name|work
argument_list|)
argument_list|)
expr_stmt|;
name|work
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addIfNoOverlap
specifier|public
name|void
name|addIfNoOverlap
parameter_list|(
name|WeightedPhraseInfo
name|wpi
parameter_list|)
block|{
for|for
control|(
name|WeightedPhraseInfo
name|existWpi
range|:
name|getPhraseList
argument_list|()
control|)
block|{
if|if
condition|(
name|existWpi
operator|.
name|isOffsetOverlap
argument_list|(
name|wpi
argument_list|)
condition|)
block|{
comment|// WeightedPhraseInfo.addIfNoOverlap() dumps the second part of, for example, hyphenated words (social-economics).
comment|// The result is that all informations in TermInfo are lost and not available for further operations.
name|existWpi
operator|.
name|getTermsInfos
argument_list|()
operator|.
name|addAll
argument_list|(
name|wpi
operator|.
name|getTermsInfos
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|getPhraseList
argument_list|()
operator|.
name|add
argument_list|(
name|wpi
argument_list|)
expr_stmt|;
block|}
comment|/**    * Represents the list of term offsets and boost for some text    */
DECL|class|WeightedPhraseInfo
specifier|public
specifier|static
class|class
name|WeightedPhraseInfo
implements|implements
name|Comparable
argument_list|<
name|WeightedPhraseInfo
argument_list|>
block|{
DECL|field|termsOffsets
specifier|private
name|List
argument_list|<
name|Toffs
argument_list|>
name|termsOffsets
decl_stmt|;
comment|// usually termsOffsets.size() == 1,
comment|// but if position-gap> 1 and slop> 0 then size() could be greater than 1
DECL|field|boost
specifier|private
name|float
name|boost
decl_stmt|;
comment|// query boost
DECL|field|seqnum
specifier|private
name|int
name|seqnum
decl_stmt|;
DECL|field|termsInfos
specifier|private
name|ArrayList
argument_list|<
name|TermInfo
argument_list|>
name|termsInfos
decl_stmt|;
comment|/**      * Text of the match, calculated on the fly.  Use for debugging only.      * @return the text      */
DECL|method|getText
specifier|public
name|String
name|getText
parameter_list|()
block|{
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|TermInfo
name|ti
range|:
name|termsInfos
control|)
block|{
name|text
operator|.
name|append
argument_list|(
name|ti
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * @return the termsOffsets      */
DECL|method|getTermsOffsets
specifier|public
name|List
argument_list|<
name|Toffs
argument_list|>
name|getTermsOffsets
parameter_list|()
block|{
return|return
name|termsOffsets
return|;
block|}
comment|/**      * @return the boost      */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/**      * @return the termInfos       */
DECL|method|getTermsInfos
specifier|public
name|List
argument_list|<
name|TermInfo
argument_list|>
name|getTermsInfos
parameter_list|()
block|{
return|return
name|termsInfos
return|;
block|}
DECL|method|WeightedPhraseInfo
specifier|public
name|WeightedPhraseInfo
parameter_list|(
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
name|terms
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
argument_list|(
name|terms
argument_list|,
name|boost
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|WeightedPhraseInfo
specifier|public
name|WeightedPhraseInfo
parameter_list|(
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
name|terms
parameter_list|,
name|float
name|boost
parameter_list|,
name|int
name|seqnum
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|seqnum
operator|=
name|seqnum
expr_stmt|;
comment|// We keep TermInfos for further operations
name|termsInfos
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|termsOffsets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TermInfo
name|ti
init|=
name|terms
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|termsOffsets
operator|.
name|add
argument_list|(
operator|new
name|Toffs
argument_list|(
name|ti
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|ti
operator|.
name|getEndOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return;
block|}
name|int
name|pos
init|=
name|ti
operator|.
name|getPosition
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|terms
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ti
operator|=
name|terms
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|getPosition
argument_list|()
operator|-
name|pos
operator|==
literal|1
condition|)
block|{
name|Toffs
name|to
init|=
name|termsOffsets
operator|.
name|get
argument_list|(
name|termsOffsets
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|to
operator|.
name|setEndOffset
argument_list|(
name|ti
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsOffsets
operator|.
name|add
argument_list|(
operator|new
name|Toffs
argument_list|(
name|ti
operator|.
name|getStartOffset
argument_list|()
argument_list|,
name|ti
operator|.
name|getEndOffset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pos
operator|=
name|ti
operator|.
name|getPosition
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Merging constructor.  Note that this just grabs seqnum from the first info.      */
DECL|method|WeightedPhraseInfo
specifier|public
name|WeightedPhraseInfo
parameter_list|(
name|Collection
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|toMerge
parameter_list|)
block|{
comment|// Pretty much the same idea as merging FieldPhraseLists:
comment|// Step 1.  Sort by startOffset, endOffset
comment|//          While we are here merge the boosts and termInfos
name|Iterator
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|toMergeItr
init|=
name|toMerge
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|toMergeItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"toMerge must contain at least one WeightedPhraseInfo."
argument_list|)
throw|;
block|}
name|WeightedPhraseInfo
name|first
init|=
name|toMergeItr
operator|.
name|next
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
name|Iterator
argument_list|<
name|Toffs
argument_list|>
index|[]
name|allToffs
init|=
operator|new
name|Iterator
index|[
name|toMerge
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|termsInfos
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|seqnum
operator|=
name|first
operator|.
name|seqnum
expr_stmt|;
name|boost
operator|=
name|first
operator|.
name|boost
expr_stmt|;
name|allToffs
index|[
literal|0
index|]
operator|=
name|first
operator|.
name|termsOffsets
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|int
name|index
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|toMergeItr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|WeightedPhraseInfo
name|info
init|=
name|toMergeItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|boost
operator|+=
name|info
operator|.
name|boost
expr_stmt|;
name|termsInfos
operator|.
name|addAll
argument_list|(
name|info
operator|.
name|termsInfos
argument_list|)
expr_stmt|;
name|allToffs
index|[
name|index
operator|++
index|]
operator|=
name|info
operator|.
name|termsOffsets
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
comment|// Step 2.  Walk the sorted list merging overlaps
name|MergedIterator
argument_list|<
name|Toffs
argument_list|>
name|itr
init|=
operator|new
name|MergedIterator
argument_list|<>
argument_list|(
literal|false
argument_list|,
name|allToffs
argument_list|)
decl_stmt|;
name|termsOffsets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return;
block|}
name|Toffs
name|work
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Toffs
name|current
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|current
operator|.
name|startOffset
operator|<=
name|work
operator|.
name|endOffset
condition|)
block|{
name|work
operator|.
name|endOffset
operator|=
name|Math
operator|.
name|max
argument_list|(
name|work
operator|.
name|endOffset
argument_list|,
name|current
operator|.
name|endOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsOffsets
operator|.
name|add
argument_list|(
name|work
argument_list|)
expr_stmt|;
name|work
operator|=
name|current
expr_stmt|;
block|}
block|}
name|termsOffsets
operator|.
name|add
argument_list|(
name|work
argument_list|)
expr_stmt|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|termsOffsets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|termsOffsets
operator|.
name|get
argument_list|(
name|termsOffsets
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|endOffset
return|;
block|}
DECL|method|isOffsetOverlap
specifier|public
name|boolean
name|isOffsetOverlap
parameter_list|(
name|WeightedPhraseInfo
name|other
parameter_list|)
block|{
name|int
name|so
init|=
name|getStartOffset
argument_list|()
decl_stmt|;
name|int
name|eo
init|=
name|getEndOffset
argument_list|()
decl_stmt|;
name|int
name|oso
init|=
name|other
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
name|int
name|oeo
init|=
name|other
operator|.
name|getEndOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|so
operator|<=
name|oso
operator|&&
name|oso
operator|<
name|eo
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|so
operator|<
name|oeo
operator|&&
name|oeo
operator|<=
name|eo
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|oso
operator|<=
name|so
operator|&&
name|so
operator|<
name|oeo
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|oso
operator|<
name|eo
operator|&&
name|eo
operator|<=
name|oeo
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getText
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|boost
argument_list|)
operator|.
name|append
argument_list|(
literal|")("
argument_list|)
expr_stmt|;
for|for
control|(
name|Toffs
name|to
range|:
name|termsOffsets
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|to
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * @return the seqnum      */
DECL|method|getSeqnum
specifier|public
name|int
name|getSeqnum
parameter_list|()
block|{
return|return
name|seqnum
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|WeightedPhraseInfo
name|other
parameter_list|)
block|{
name|int
name|diff
init|=
name|getStartOffset
argument_list|()
operator|-
name|other
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
name|diff
operator|=
name|getEndOffset
argument_list|()
operator|-
name|other
operator|.
name|getEndOffset
argument_list|()
expr_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|signum
argument_list|(
name|getBoost
argument_list|()
operator|-
name|other
operator|.
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getStartOffset
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getEndOffset
argument_list|()
expr_stmt|;
name|long
name|b
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|b
operator|^
operator|(
name|b
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|WeightedPhraseInfo
name|other
init|=
operator|(
name|WeightedPhraseInfo
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|getStartOffset
argument_list|()
operator|!=
name|other
operator|.
name|getStartOffset
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getEndOffset
argument_list|()
operator|!=
name|other
operator|.
name|getEndOffset
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
name|other
operator|.
name|getBoost
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Term offsets (start + end)      */
DECL|class|Toffs
specifier|public
specifier|static
class|class
name|Toffs
implements|implements
name|Comparable
argument_list|<
name|Toffs
argument_list|>
block|{
DECL|field|startOffset
specifier|private
name|int
name|startOffset
decl_stmt|;
DECL|field|endOffset
specifier|private
name|int
name|endOffset
decl_stmt|;
DECL|method|Toffs
specifier|public
name|Toffs
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
DECL|method|setEndOffset
specifier|public
name|void
name|setEndOffset
parameter_list|(
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
DECL|method|getStartOffset
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
DECL|method|getEndOffset
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Toffs
name|other
parameter_list|)
block|{
name|int
name|diff
init|=
name|getStartOffset
argument_list|()
operator|-
name|other
operator|.
name|getStartOffset
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|!=
literal|0
condition|)
block|{
return|return
name|diff
return|;
block|}
return|return
name|getEndOffset
argument_list|()
operator|-
name|other
operator|.
name|getEndOffset
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getStartOffset
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|getEndOffset
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Toffs
name|other
init|=
operator|(
name|Toffs
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|getStartOffset
argument_list|()
operator|!=
name|other
operator|.
name|getStartOffset
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getEndOffset
argument_list|()
operator|!=
name|other
operator|.
name|getEndOffset
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
operator|.
name|append
argument_list|(
name|startOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|endOffset
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

