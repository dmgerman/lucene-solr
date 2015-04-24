begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|List
import|;
end_import

begin_comment
comment|/**  * Base class for Scorers that score disjunctions.  */
end_comment

begin_class
DECL|class|DisjunctionScorer
specifier|abstract
class|class
name|DisjunctionScorer
extends|extends
name|Scorer
block|{
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
DECL|field|subScorers
specifier|private
specifier|final
name|DisiPriorityQueue
argument_list|<
name|Scorer
argument_list|>
name|subScorers
decl_stmt|;
DECL|field|cost
specifier|private
specifier|final
name|long
name|cost
decl_stmt|;
comment|/** Linked list of scorers which are on the current doc */
DECL|field|topScorers
specifier|private
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|topScorers
decl_stmt|;
DECL|method|DisjunctionScorer
specifier|protected
name|DisjunctionScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
if|if
condition|(
name|subScorers
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"There must be at least 2 subScorers"
argument_list|)
throw|;
block|}
name|this
operator|.
name|subScorers
operator|=
operator|new
name|DisiPriorityQueue
argument_list|<
name|Scorer
argument_list|>
argument_list|(
name|subScorers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Scorer
name|scorer
range|:
name|subScorers
control|)
block|{
specifier|final
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|w
init|=
operator|new
name|DisiWrapper
argument_list|<>
argument_list|(
name|scorer
argument_list|)
decl_stmt|;
name|cost
operator|+=
name|w
operator|.
name|cost
expr_stmt|;
name|this
operator|.
name|subScorers
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
name|boolean
name|hasApproximation
init|=
literal|false
decl_stmt|;
for|for
control|(
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|w
range|:
name|subScorers
control|)
block|{
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|!=
literal|null
condition|)
block|{
name|hasApproximation
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|hasApproximation
condition|)
block|{
comment|// none of the sub scorers supports approximations
return|return
literal|null
return|;
block|}
comment|// note it is important to share the same pq as this scorer so that
comment|// rebalancing the pq through the approximation will also rebalance
comment|// the pq in this scorer.
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
operator|new
name|DisjunctionDISIApproximation
argument_list|<
name|Scorer
argument_list|>
argument_list|(
name|subScorers
argument_list|)
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
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|topScorers
init|=
name|subScorers
operator|.
name|topList
argument_list|()
decl_stmt|;
comment|// remove the head of the list as long as it does not match
while|while
condition|(
name|topScorers
operator|.
name|twoPhaseView
operator|!=
literal|null
operator|&&
operator|!
name|topScorers
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
condition|)
block|{
name|topScorers
operator|=
name|topScorers
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|topScorers
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// now we know we have at least one match since the first element of 'matchList' matches
if|if
condition|(
name|needsScores
condition|)
block|{
comment|// if scores or freqs are needed, we also need to remove scorers
comment|// from the top list that do not actually match
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|previous
init|=
name|topScorers
decl_stmt|;
for|for
control|(
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|w
init|=
name|topScorers
operator|.
name|next
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|next
control|)
block|{
if|if
condition|(
name|w
operator|.
name|twoPhaseView
operator|!=
literal|null
operator|&&
operator|!
name|w
operator|.
name|twoPhaseView
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// w does not match, remove it
name|previous
operator|.
name|next
operator|=
name|w
operator|.
name|next
expr_stmt|;
block|}
else|else
block|{
name|previous
operator|=
name|w
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// since we don't need scores, let's pretend we have a single match
name|topScorers
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
comment|// We need to explicitely set the list of top scorers to avoid the
comment|// laziness of DisjunctionScorer.score() that would take all scorers
comment|// positioned on the same doc as the top of the pq, including
comment|// non-matching scorers
name|DisjunctionScorer
operator|.
name|this
operator|.
name|topScorers
operator|=
name|topScorers
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
specifier|final
name|long
name|cost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
specifier|final
name|int
name|docID
parameter_list|()
block|{
return|return
name|subScorers
operator|.
name|top
argument_list|()
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
specifier|final
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|topScorers
operator|=
literal|null
expr_stmt|;
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|top
init|=
name|subScorers
operator|.
name|top
argument_list|()
decl_stmt|;
specifier|final
name|int
name|doc
init|=
name|top
operator|.
name|doc
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|iterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|top
operator|=
name|subScorers
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|==
name|doc
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
specifier|final
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|topScorers
operator|=
literal|null
expr_stmt|;
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|top
init|=
name|subScorers
operator|.
name|top
argument_list|()
decl_stmt|;
do|do
block|{
name|top
operator|.
name|doc
operator|=
name|top
operator|.
name|iterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|top
operator|=
name|subScorers
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|top
operator|.
name|doc
operator|<
name|target
condition|)
do|;
return|return
name|top
operator|.
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
specifier|final
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|topScorers
operator|==
literal|null
condition|)
block|{
name|topScorers
operator|=
name|subScorers
operator|.
name|topList
argument_list|()
expr_stmt|;
block|}
name|int
name|freq
init|=
literal|1
decl_stmt|;
for|for
control|(
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|w
init|=
name|topScorers
operator|.
name|next
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|next
control|)
block|{
name|freq
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|topScorers
operator|==
literal|null
condition|)
block|{
name|topScorers
operator|=
name|subScorers
operator|.
name|topList
argument_list|()
expr_stmt|;
block|}
return|return
name|score
argument_list|(
name|topScorers
argument_list|)
return|;
block|}
comment|/** Compute the score for the given linked list of scorers. */
DECL|method|score
specifier|protected
specifier|abstract
name|float
name|score
parameter_list|(
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|topList
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|getChildren
specifier|public
specifier|final
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ChildScorer
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DisiWrapper
argument_list|<
name|Scorer
argument_list|>
name|scorer
range|:
name|subScorers
control|)
block|{
name|children
operator|.
name|add
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|scorer
operator|.
name|iterator
argument_list|,
literal|"SHOULD"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

