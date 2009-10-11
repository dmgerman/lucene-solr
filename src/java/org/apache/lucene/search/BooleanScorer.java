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
name|Iterator
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
name|IndexReader
import|;
end_import

begin_comment
comment|/* Description from Doug Cutting (excerpted from  * LUCENE-1483):  *  * BooleanScorer uses a ~16k array to score windows of  * docs. So it scores docs 0-16k first, then docs 16-32k,  * etc. For each window it iterates through all query terms  * and accumulates a score in table[doc%16k]. It also stores  * in the table a bitmask representing which terms  * contributed to the score. Non-zero scores are chained in  * a linked list. At the end of scoring each window it then  * iterates through the linked list and, if the bitmask  * matches the boolean constraints, collects a hit. For  * boolean queries with lots of frequent terms this can be  * much faster, since it does not need to update a priority  * queue for each posting, instead performing constant-time  * operations per posting. The only downside is that it  * results in hits being delivered out-of-order within the  * window, which means it cannot be nested within other  * scorers. But it works well as a top-level scorer.  *  * The new BooleanScorer2 implementation instead works by  * merging priority queues of postings, albeit with some  * clever tricks. For example, a pure conjunction (all terms  * required) does not require a priority queue. Instead it  * sorts the posting streams at the start, then repeatedly  * skips the first to to the last. If the first ever equals  * the last, then there's a hit. When some terms are  * required and some terms are optional, the conjunction can  * be evaluated first, then the optional terms can all skip  * to the match and be added to the score. Thus the  * conjunction can reduce the number of priority queue  * updates for the optional terms. */
end_comment

begin_class
DECL|class|BooleanScorer
specifier|final
class|class
name|BooleanScorer
extends|extends
name|Scorer
block|{
DECL|class|BooleanScorerCollector
specifier|private
specifier|static
specifier|final
class|class
name|BooleanScorerCollector
extends|extends
name|Collector
block|{
DECL|field|bucketTable
specifier|private
name|BucketTable
name|bucketTable
decl_stmt|;
DECL|field|mask
specifier|private
name|int
name|mask
decl_stmt|;
DECL|field|scorer
specifier|private
name|Scorer
name|scorer
decl_stmt|;
DECL|method|BooleanScorerCollector
specifier|public
name|BooleanScorerCollector
parameter_list|(
name|int
name|mask
parameter_list|,
name|BucketTable
name|bucketTable
parameter_list|)
block|{
name|this
operator|.
name|mask
operator|=
name|mask
expr_stmt|;
name|this
operator|.
name|bucketTable
operator|=
name|bucketTable
expr_stmt|;
block|}
DECL|method|collect
specifier|public
specifier|final
name|void
name|collect
parameter_list|(
specifier|final
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BucketTable
name|table
init|=
name|bucketTable
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|doc
operator|&
name|BucketTable
operator|.
name|MASK
decl_stmt|;
name|Bucket
name|bucket
init|=
name|table
operator|.
name|buckets
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|bucket
operator|==
literal|null
condition|)
name|table
operator|.
name|buckets
index|[
name|i
index|]
operator|=
name|bucket
operator|=
operator|new
name|Bucket
argument_list|()
expr_stmt|;
if|if
condition|(
name|bucket
operator|.
name|doc
operator|!=
name|doc
condition|)
block|{
comment|// invalid bucket
name|bucket
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
comment|// set doc
name|bucket
operator|.
name|score
operator|=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
comment|// initialize score
name|bucket
operator|.
name|bits
operator|=
name|mask
expr_stmt|;
comment|// initialize mask
name|bucket
operator|.
name|coord
operator|=
literal|1
expr_stmt|;
comment|// initialize coord
name|bucket
operator|.
name|next
operator|=
name|table
operator|.
name|first
expr_stmt|;
comment|// push onto valid list
name|table
operator|.
name|first
operator|=
name|bucket
expr_stmt|;
block|}
else|else
block|{
comment|// valid bucket
name|bucket
operator|.
name|score
operator|+=
name|scorer
operator|.
name|score
argument_list|()
expr_stmt|;
comment|// increment score
name|bucket
operator|.
name|bits
operator||=
name|mask
expr_stmt|;
comment|// add bits in mask
name|bucket
operator|.
name|coord
operator|++
expr_stmt|;
comment|// increment coord
block|}
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
block|{
comment|// not needed by this implementation
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// An internal class which is used in score(Collector, int) for setting the
comment|// current score. This is required since Collector exposes a setScorer method
comment|// and implementations that need the score will call scorer.score().
comment|// Therefore the only methods that are implemented are score() and doc().
DECL|class|BucketScorer
specifier|private
specifier|static
specifier|final
class|class
name|BucketScorer
extends|extends
name|Scorer
block|{
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
name|NO_MORE_DOCS
decl_stmt|;
DECL|method|BucketScorer
specifier|public
name|BucketScorer
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|NO_MORE_DOCS
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
block|}
DECL|class|Bucket
specifier|static
specifier|final
class|class
name|Bucket
block|{
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
comment|// tells if bucket is valid
DECL|field|score
name|float
name|score
decl_stmt|;
comment|// incremental score
DECL|field|bits
name|int
name|bits
decl_stmt|;
comment|// used for bool constraints
DECL|field|coord
name|int
name|coord
decl_stmt|;
comment|// count of terms in score
DECL|field|next
name|Bucket
name|next
decl_stmt|;
comment|// next valid bucket
block|}
comment|/** A simple hash table of document scores within a range. */
DECL|class|BucketTable
specifier|static
specifier|final
class|class
name|BucketTable
block|{
DECL|field|SIZE
specifier|public
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|1
operator|<<
literal|11
decl_stmt|;
DECL|field|MASK
specifier|public
specifier|static
specifier|final
name|int
name|MASK
init|=
name|SIZE
operator|-
literal|1
decl_stmt|;
DECL|field|buckets
specifier|final
name|Bucket
index|[]
name|buckets
init|=
operator|new
name|Bucket
index|[
name|SIZE
index|]
decl_stmt|;
DECL|field|first
name|Bucket
name|first
init|=
literal|null
decl_stmt|;
comment|// head of valid list
DECL|method|BucketTable
specifier|public
name|BucketTable
parameter_list|()
block|{}
DECL|method|newCollector
specifier|public
name|Collector
name|newCollector
parameter_list|(
name|int
name|mask
parameter_list|)
block|{
return|return
operator|new
name|BooleanScorerCollector
argument_list|(
name|mask
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|size
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|SIZE
return|;
block|}
block|}
DECL|class|SubScorer
specifier|static
specifier|final
class|class
name|SubScorer
block|{
DECL|field|scorer
specifier|public
name|Scorer
name|scorer
decl_stmt|;
DECL|field|required
specifier|public
name|boolean
name|required
init|=
literal|false
decl_stmt|;
DECL|field|prohibited
specifier|public
name|boolean
name|prohibited
init|=
literal|false
decl_stmt|;
DECL|field|collector
specifier|public
name|Collector
name|collector
decl_stmt|;
DECL|field|next
specifier|public
name|SubScorer
name|next
decl_stmt|;
DECL|method|SubScorer
specifier|public
name|SubScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|boolean
name|required
parameter_list|,
name|boolean
name|prohibited
parameter_list|,
name|Collector
name|collector
parameter_list|,
name|SubScorer
name|next
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
name|this
operator|.
name|prohibited
operator|=
name|prohibited
expr_stmt|;
name|this
operator|.
name|collector
operator|=
name|collector
expr_stmt|;
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
block|}
DECL|field|scorers
specifier|private
name|SubScorer
name|scorers
init|=
literal|null
decl_stmt|;
DECL|field|bucketTable
specifier|private
name|BucketTable
name|bucketTable
init|=
operator|new
name|BucketTable
argument_list|()
decl_stmt|;
DECL|field|maxCoord
specifier|private
name|int
name|maxCoord
init|=
literal|1
decl_stmt|;
DECL|field|coordFactors
specifier|private
specifier|final
name|float
index|[]
name|coordFactors
decl_stmt|;
DECL|field|requiredMask
specifier|private
name|int
name|requiredMask
init|=
literal|0
decl_stmt|;
DECL|field|prohibitedMask
specifier|private
name|int
name|prohibitedMask
init|=
literal|0
decl_stmt|;
DECL|field|nextMask
specifier|private
name|int
name|nextMask
init|=
literal|1
decl_stmt|;
DECL|field|minNrShouldMatch
specifier|private
specifier|final
name|int
name|minNrShouldMatch
decl_stmt|;
DECL|field|end
specifier|private
name|int
name|end
decl_stmt|;
DECL|field|current
specifier|private
name|Bucket
name|current
decl_stmt|;
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|BooleanScorer
name|BooleanScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|int
name|minNrShouldMatch
parameter_list|,
name|List
name|optionalScorers
parameter_list|,
name|List
name|prohibitedScorers
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
name|this
operator|.
name|minNrShouldMatch
operator|=
name|minNrShouldMatch
expr_stmt|;
if|if
condition|(
name|optionalScorers
operator|!=
literal|null
operator|&&
name|optionalScorers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
name|si
init|=
name|optionalScorers
operator|.
name|iterator
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Scorer
name|scorer
init|=
operator|(
name|Scorer
operator|)
name|si
operator|.
name|next
argument_list|()
decl_stmt|;
name|maxCoord
operator|++
expr_stmt|;
if|if
condition|(
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|scorers
operator|=
operator|new
name|SubScorer
argument_list|(
name|scorer
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|bucketTable
operator|.
name|newCollector
argument_list|(
literal|0
argument_list|)
argument_list|,
name|scorers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|prohibitedScorers
operator|!=
literal|null
operator|&&
name|prohibitedScorers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Iterator
name|si
init|=
name|prohibitedScorers
operator|.
name|iterator
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Scorer
name|scorer
init|=
operator|(
name|Scorer
operator|)
name|si
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|mask
init|=
name|nextMask
decl_stmt|;
name|nextMask
operator|=
name|nextMask
operator|<<
literal|1
expr_stmt|;
name|prohibitedMask
operator||=
name|mask
expr_stmt|;
comment|// update prohibited mask
if|if
condition|(
name|scorer
operator|.
name|nextDoc
argument_list|()
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|scorers
operator|=
operator|new
name|SubScorer
argument_list|(
name|scorer
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|bucketTable
operator|.
name|newCollector
argument_list|(
name|mask
argument_list|)
argument_list|,
name|scorers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|coordFactors
operator|=
operator|new
name|float
index|[
name|maxCoord
index|]
expr_stmt|;
name|Similarity
name|sim
init|=
name|getSimilarity
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
name|maxCoord
condition|;
name|i
operator|++
control|)
block|{
name|coordFactors
index|[
name|i
index|]
operator|=
name|sim
operator|.
name|coord
argument_list|(
name|i
argument_list|,
name|maxCoord
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// firstDocID is ignored since nextDoc() initializes 'current'
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|,
name|int
name|max
parameter_list|,
name|int
name|firstDocID
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|more
decl_stmt|;
name|Bucket
name|tmp
decl_stmt|;
name|BucketScorer
name|bs
init|=
operator|new
name|BucketScorer
argument_list|()
decl_stmt|;
comment|// The internal loop will set the score and doc before calling collect.
name|collector
operator|.
name|setScorer
argument_list|(
name|bs
argument_list|)
expr_stmt|;
do|do
block|{
name|bucketTable
operator|.
name|first
operator|=
literal|null
expr_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
comment|// more queued
comment|// check prohibited& required
if|if
condition|(
operator|(
name|current
operator|.
name|bits
operator|&
name|prohibitedMask
operator|)
operator|==
literal|0
operator|&&
operator|(
name|current
operator|.
name|bits
operator|&
name|requiredMask
operator|)
operator|==
name|requiredMask
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|doc
operator|>=
name|max
condition|)
block|{
name|tmp
operator|=
name|current
expr_stmt|;
name|current
operator|=
name|current
operator|.
name|next
expr_stmt|;
name|tmp
operator|.
name|next
operator|=
name|bucketTable
operator|.
name|first
expr_stmt|;
name|bucketTable
operator|.
name|first
operator|=
name|tmp
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|current
operator|.
name|coord
operator|>=
name|minNrShouldMatch
condition|)
block|{
name|bs
operator|.
name|score
operator|=
name|current
operator|.
name|score
operator|*
name|coordFactors
index|[
name|current
operator|.
name|coord
index|]
expr_stmt|;
name|bs
operator|.
name|doc
operator|=
name|current
operator|.
name|doc
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
name|current
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|current
operator|=
name|current
operator|.
name|next
expr_stmt|;
comment|// pop the queue
block|}
if|if
condition|(
name|bucketTable
operator|.
name|first
operator|!=
literal|null
condition|)
block|{
name|current
operator|=
name|bucketTable
operator|.
name|first
expr_stmt|;
name|bucketTable
operator|.
name|first
operator|=
name|current
operator|.
name|next
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// refill the queue
name|more
operator|=
literal|false
expr_stmt|;
name|end
operator|+=
name|BucketTable
operator|.
name|SIZE
expr_stmt|;
for|for
control|(
name|SubScorer
name|sub
init|=
name|scorers
init|;
name|sub
operator|!=
literal|null
condition|;
name|sub
operator|=
name|sub
operator|.
name|next
control|)
block|{
name|int
name|subScorerDocID
init|=
name|sub
operator|.
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|subScorerDocID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|more
operator||=
name|sub
operator|.
name|scorer
operator|.
name|score
argument_list|(
name|sub
operator|.
name|collector
argument_list|,
name|end
argument_list|,
name|subScorerDocID
argument_list|)
expr_stmt|;
block|}
block|}
name|current
operator|=
name|bucketTable
operator|.
name|first
expr_stmt|;
block|}
do|while
condition|(
name|current
operator|!=
literal|null
operator|||
name|more
condition|)
do|;
return|return
literal|false
return|;
block|}
comment|/** @deprecated use {@link #score(Collector, int, int)} instead. */
DECL|method|score
specifier|protected
name|boolean
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|score
argument_list|(
operator|new
name|HitCollectorWrapper
argument_list|(
name|hc
argument_list|)
argument_list|,
name|max
argument_list|,
name|docID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|more
decl_stmt|;
do|do
block|{
while|while
condition|(
name|bucketTable
operator|.
name|first
operator|!=
literal|null
condition|)
block|{
comment|// more queued
name|current
operator|=
name|bucketTable
operator|.
name|first
expr_stmt|;
name|bucketTable
operator|.
name|first
operator|=
name|current
operator|.
name|next
expr_stmt|;
comment|// pop the queue
comment|// check prohibited& required, and minNrShouldMatch
if|if
condition|(
operator|(
name|current
operator|.
name|bits
operator|&
name|prohibitedMask
operator|)
operator|==
literal|0
operator|&&
operator|(
name|current
operator|.
name|bits
operator|&
name|requiredMask
operator|)
operator|==
name|requiredMask
operator|&&
name|current
operator|.
name|coord
operator|>=
name|minNrShouldMatch
condition|)
block|{
return|return
name|doc
operator|=
name|current
operator|.
name|doc
return|;
block|}
block|}
comment|// refill the queue
name|more
operator|=
literal|false
expr_stmt|;
name|end
operator|+=
name|BucketTable
operator|.
name|SIZE
expr_stmt|;
for|for
control|(
name|SubScorer
name|sub
init|=
name|scorers
init|;
name|sub
operator|!=
literal|null
condition|;
name|sub
operator|=
name|sub
operator|.
name|next
control|)
block|{
name|Scorer
name|scorer
init|=
name|sub
operator|.
name|scorer
decl_stmt|;
name|sub
operator|.
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|int
name|doc
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
while|while
condition|(
name|doc
operator|<
name|end
condition|)
block|{
name|sub
operator|.
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
name|more
operator||=
operator|(
name|doc
operator|!=
name|NO_MORE_DOCS
operator|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|bucketTable
operator|.
name|first
operator|!=
literal|null
operator|||
name|more
condition|)
do|;
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|current
operator|.
name|score
operator|*
name|coordFactors
index|[
name|current
operator|.
name|coord
index|]
return|;
block|}
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|Collector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
name|collector
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated use {@link #score(Collector)} instead. */
DECL|method|score
specifier|public
name|void
name|score
parameter_list|(
name|HitCollector
name|hc
parameter_list|)
throws|throws
name|IOException
block|{
name|score
argument_list|(
operator|new
name|HitCollectorWrapper
argument_list|(
name|hc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"boolean("
argument_list|)
expr_stmt|;
for|for
control|(
name|SubScorer
name|sub
init|=
name|scorers
init|;
name|sub
operator|!=
literal|null
condition|;
name|sub
operator|=
name|sub
operator|.
name|next
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|sub
operator|.
name|scorer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

