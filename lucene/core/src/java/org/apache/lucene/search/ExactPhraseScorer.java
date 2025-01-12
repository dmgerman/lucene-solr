begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|ArrayList
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
name|index
operator|.
name|PostingsEnum
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
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_class
DECL|class|ExactPhraseScorer
specifier|final
class|class
name|ExactPhraseScorer
extends|extends
name|Scorer
block|{
DECL|class|PostingsAndPosition
specifier|private
specifier|static
class|class
name|PostingsAndPosition
block|{
DECL|field|postings
specifier|private
specifier|final
name|PostingsEnum
name|postings
decl_stmt|;
DECL|field|offset
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
DECL|field|freq
DECL|field|upTo
DECL|field|pos
specifier|private
name|int
name|freq
decl_stmt|,
name|upTo
decl_stmt|,
name|pos
decl_stmt|;
DECL|method|PostingsAndPosition
specifier|public
name|PostingsAndPosition
parameter_list|(
name|PostingsEnum
name|postings
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|postings
operator|=
name|postings
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
block|}
DECL|field|conjunction
specifier|private
specifier|final
name|DocIdSetIterator
name|conjunction
decl_stmt|;
DECL|field|postings
specifier|private
specifier|final
name|PostingsAndPosition
index|[]
name|postings
decl_stmt|;
DECL|field|freq
specifier|private
name|int
name|freq
decl_stmt|;
DECL|field|docScorer
specifier|private
specifier|final
name|Similarity
operator|.
name|SimScorer
name|docScorer
decl_stmt|;
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|matchCost
specifier|private
name|float
name|matchCost
decl_stmt|;
DECL|method|ExactPhraseScorer
name|ExactPhraseScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|PhraseQuery
operator|.
name|PostingsAndFreq
index|[]
name|postings
parameter_list|,
name|Similarity
operator|.
name|SimScorer
name|docScorer
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|matchCost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
name|List
argument_list|<
name|DocIdSetIterator
argument_list|>
name|iterators
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PostingsAndPosition
argument_list|>
name|postingsAndPositions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PhraseQuery
operator|.
name|PostingsAndFreq
name|posting
range|:
name|postings
control|)
block|{
name|iterators
operator|.
name|add
argument_list|(
name|posting
operator|.
name|postings
argument_list|)
expr_stmt|;
name|postingsAndPositions
operator|.
name|add
argument_list|(
operator|new
name|PostingsAndPosition
argument_list|(
name|posting
operator|.
name|postings
argument_list|,
name|posting
operator|.
name|position
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|conjunction
operator|=
name|ConjunctionDISI
operator|.
name|intersectIterators
argument_list|(
name|iterators
argument_list|)
expr_stmt|;
assert|assert
name|TwoPhaseIterator
operator|.
name|unwrap
argument_list|(
name|conjunction
argument_list|)
operator|==
literal|null
assert|;
name|this
operator|.
name|postings
operator|=
name|postingsAndPositions
operator|.
name|toArray
argument_list|(
operator|new
name|PostingsAndPosition
index|[
name|postingsAndPositions
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchCost
operator|=
name|matchCost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|twoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|conjunction
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|phraseFreq
argument_list|()
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|matchCost
parameter_list|()
block|{
return|return
name|matchCost
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|()
argument_list|)
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
return|return
literal|"ExactPhraseScorer("
operator|+
name|weight
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|conjunction
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|docScorer
operator|.
name|score
argument_list|(
name|docID
argument_list|()
argument_list|,
name|freq
argument_list|)
return|;
block|}
comment|/** Advance the given pos enum to the first doc on or after {@code target}.    *  Return {@code false} if the enum was exhausted before reaching    *  {@code target} and {@code true} otherwise. */
DECL|method|advancePosition
specifier|private
specifier|static
name|boolean
name|advancePosition
parameter_list|(
name|PostingsAndPosition
name|posting
parameter_list|,
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|posting
operator|.
name|pos
operator|<
name|target
condition|)
block|{
if|if
condition|(
name|posting
operator|.
name|upTo
operator|==
name|posting
operator|.
name|freq
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|posting
operator|.
name|pos
operator|=
name|posting
operator|.
name|postings
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|posting
operator|.
name|upTo
operator|+=
literal|1
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|phraseFreq
specifier|private
name|int
name|phraseFreq
parameter_list|()
throws|throws
name|IOException
block|{
comment|// reset state
specifier|final
name|PostingsAndPosition
index|[]
name|postings
init|=
name|this
operator|.
name|postings
decl_stmt|;
for|for
control|(
name|PostingsAndPosition
name|posting
range|:
name|postings
control|)
block|{
name|posting
operator|.
name|freq
operator|=
name|posting
operator|.
name|postings
operator|.
name|freq
argument_list|()
expr_stmt|;
name|posting
operator|.
name|pos
operator|=
name|posting
operator|.
name|postings
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|posting
operator|.
name|upTo
operator|=
literal|1
expr_stmt|;
block|}
name|int
name|freq
init|=
literal|0
decl_stmt|;
specifier|final
name|PostingsAndPosition
name|lead
init|=
name|postings
index|[
literal|0
index|]
decl_stmt|;
name|advanceHead
label|:
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|phrasePos
init|=
name|lead
operator|.
name|pos
operator|-
name|lead
operator|.
name|offset
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|postings
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|PostingsAndPosition
name|posting
init|=
name|postings
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|int
name|expectedPos
init|=
name|phrasePos
operator|+
name|posting
operator|.
name|offset
decl_stmt|;
comment|// advance up to the same position as the lead
if|if
condition|(
name|advancePosition
argument_list|(
name|posting
argument_list|,
name|expectedPos
argument_list|)
operator|==
literal|false
condition|)
block|{
break|break
name|advanceHead
break|;
block|}
if|if
condition|(
name|posting
operator|.
name|pos
operator|!=
name|expectedPos
condition|)
block|{
comment|// we advanced too far
if|if
condition|(
name|advancePosition
argument_list|(
name|lead
argument_list|,
name|posting
operator|.
name|pos
operator|-
name|posting
operator|.
name|offset
operator|+
name|lead
operator|.
name|offset
argument_list|)
condition|)
block|{
continue|continue
name|advanceHead
continue|;
block|}
else|else
block|{
break|break
name|advanceHead
break|;
block|}
block|}
block|}
name|freq
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|needsScores
operator|==
literal|false
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|lead
operator|.
name|upTo
operator|==
name|lead
operator|.
name|freq
condition|)
block|{
break|break;
block|}
name|lead
operator|.
name|pos
operator|=
name|lead
operator|.
name|postings
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
name|lead
operator|.
name|upTo
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|this
operator|.
name|freq
operator|=
name|freq
return|;
block|}
block|}
end_class

end_unit

