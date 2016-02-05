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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|util
operator|.
name|BitDocIdSet
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
name|FixedBitSet
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
name|LuceneTestCase
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestConjunctionDISI
specifier|public
class|class
name|TestConjunctionDISI
extends|extends
name|LuceneTestCase
block|{
DECL|method|approximation
specifier|private
specifier|static
name|TwoPhaseIterator
name|approximation
parameter_list|(
specifier|final
name|DocIdSetIterator
name|iterator
parameter_list|,
specifier|final
name|FixedBitSet
name|confirmed
parameter_list|)
block|{
return|return
operator|new
name|TwoPhaseIterator
argument_list|(
name|iterator
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
name|confirmed
operator|.
name|get
argument_list|(
name|iterator
operator|.
name|docID
argument_list|()
argument_list|)
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
literal|5
return|;
comment|// #operations in FixedBitSet#get()
block|}
block|}
return|;
block|}
DECL|method|scorer
specifier|private
specifier|static
name|Scorer
name|scorer
parameter_list|(
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|)
block|{
return|return
name|scorer
argument_list|(
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
argument_list|,
name|twoPhaseIterator
argument_list|)
return|;
block|}
comment|/**    * Create a {@link Scorer} that wraps the given {@link DocIdSetIterator}. It    * also accepts a {@link TwoPhaseIterator} view, which is exposed in    * {@link Scorer#twoPhaseIterator()}. When the two-phase view is not null,    * then {@link DocIdSetIterator#nextDoc()} and {@link DocIdSetIterator#advance(int)} will raise    * an exception in order to make sure that {@link ConjunctionDISI} takes    * advantage of the {@link TwoPhaseIterator} view.    */
DECL|method|scorer
specifier|private
specifier|static
name|Scorer
name|scorer
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|,
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|)
block|{
return|return
operator|new
name|Scorer
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|it
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|twoPhaseIterator
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ConjunctionDISI should call the two-phase iterator"
argument_list|)
throw|;
block|}
return|return
name|it
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|twoPhaseIterator
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ConjunctionDISI should call the two-phase iterator"
argument_list|)
throw|;
block|}
return|return
name|it
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
if|if
condition|(
name|twoPhaseIterator
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ConjunctionDISI should call the two-phase iterator"
argument_list|)
throw|;
block|}
return|return
name|it
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
name|twoPhaseIterator
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
if|if
condition|(
name|twoPhaseIterator
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ConjunctionDISI should call the two-phase iterator"
argument_list|)
throw|;
block|}
return|return
name|it
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
DECL|method|randomSet
specifier|private
specifier|static
name|FixedBitSet
name|randomSet
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
specifier|final
name|int
name|step
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|step
argument_list|)
init|;
name|doc
operator|<
name|maxDoc
condition|;
name|doc
operator|+=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|step
argument_list|)
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
DECL|method|clearRandomBits
specifier|private
specifier|static
name|FixedBitSet
name|clearRandomBits
parameter_list|(
name|FixedBitSet
name|other
parameter_list|)
block|{
specifier|final
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|other
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|set
operator|.
name|or
argument_list|(
name|other
argument_list|)
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
name|set
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|set
operator|.
name|clear
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|set
return|;
block|}
DECL|method|intersect
specifier|private
specifier|static
name|FixedBitSet
name|intersect
parameter_list|(
name|FixedBitSet
index|[]
name|bitSets
parameter_list|)
block|{
specifier|final
name|FixedBitSet
name|intersection
init|=
operator|new
name|FixedBitSet
argument_list|(
name|bitSets
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|intersection
operator|.
name|or
argument_list|(
name|bitSets
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|bitSets
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|intersection
operator|.
name|and
argument_list|(
name|bitSets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|intersection
return|;
block|}
DECL|method|toBitSet
specifier|private
specifier|static
name|FixedBitSet
name|toBitSet
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|DocIdSetIterator
name|iterator
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FixedBitSet
name|set
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|set
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
comment|// Test that the conjunction iterator is correct
DECL|method|testConjunction
specifier|public
name|void
name|testConjunction
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIterators
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
index|[]
name|sets
init|=
operator|new
name|FixedBitSet
index|[
name|numIterators
index|]
decl_stmt|;
specifier|final
name|Scorer
index|[]
name|iterators
init|=
operator|new
name|Scorer
index|[
name|numIterators
index|]
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
name|iterators
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FixedBitSet
name|set
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// simple iterator
name|sets
index|[
name|i
index|]
operator|=
name|set
expr_stmt|;
name|iterators
index|[
name|i
index|]
operator|=
operator|new
name|ConstantScoreScorer
argument_list|(
literal|null
argument_list|,
literal|0f
argument_list|,
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// scorer with approximation
specifier|final
name|FixedBitSet
name|confirmed
init|=
name|clearRandomBits
argument_list|(
name|set
argument_list|)
decl_stmt|;
name|sets
index|[
name|i
index|]
operator|=
name|confirmed
expr_stmt|;
specifier|final
name|TwoPhaseIterator
name|approximation
init|=
name|approximation
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|confirmed
argument_list|)
decl_stmt|;
name|iterators
index|[
name|i
index|]
operator|=
name|scorer
argument_list|(
name|approximation
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|ConjunctionDISI
name|conjunction
init|=
name|ConjunctionDISI
operator|.
name|intersectScorers
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|iterators
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|intersect
argument_list|(
name|sets
argument_list|)
argument_list|,
name|toBitSet
argument_list|(
name|maxDoc
argument_list|,
name|conjunction
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Test that the conjunction approximation is correct
DECL|method|testConjunctionApproximation
specifier|public
name|void
name|testConjunctionApproximation
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIterators
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
index|[]
name|sets
init|=
operator|new
name|FixedBitSet
index|[
name|numIterators
index|]
decl_stmt|;
specifier|final
name|Scorer
index|[]
name|iterators
init|=
operator|new
name|Scorer
index|[
name|numIterators
index|]
decl_stmt|;
name|boolean
name|hasApproximation
init|=
literal|false
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
name|iterators
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FixedBitSet
name|set
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// simple iterator
name|sets
index|[
name|i
index|]
operator|=
name|set
expr_stmt|;
name|iterators
index|[
name|i
index|]
operator|=
operator|new
name|ConstantScoreScorer
argument_list|(
literal|null
argument_list|,
literal|0f
argument_list|,
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// scorer with approximation
specifier|final
name|FixedBitSet
name|confirmed
init|=
name|clearRandomBits
argument_list|(
name|set
argument_list|)
decl_stmt|;
name|sets
index|[
name|i
index|]
operator|=
name|confirmed
expr_stmt|;
specifier|final
name|TwoPhaseIterator
name|approximation
init|=
name|approximation
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|confirmed
argument_list|)
decl_stmt|;
name|iterators
index|[
name|i
index|]
operator|=
name|scorer
argument_list|(
name|approximation
argument_list|)
expr_stmt|;
name|hasApproximation
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|final
name|ConjunctionDISI
name|conjunction
init|=
name|ConjunctionDISI
operator|.
name|intersectScorers
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|iterators
argument_list|)
argument_list|)
decl_stmt|;
name|TwoPhaseIterator
name|twoPhaseIterator
init|=
name|conjunction
operator|.
name|asTwoPhaseIterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hasApproximation
argument_list|,
name|twoPhaseIterator
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasApproximation
condition|)
block|{
name|assertEquals
argument_list|(
name|intersect
argument_list|(
name|sets
argument_list|)
argument_list|,
name|toBitSet
argument_list|(
name|maxDoc
argument_list|,
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// This test makes sure that when nesting scorers with ConjunctionDISI, confirmations are pushed to the root.
DECL|method|testRecursiveConjunctionApproximation
specifier|public
name|void
name|testRecursiveConjunctionApproximation
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIterators
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
index|[]
name|sets
init|=
operator|new
name|FixedBitSet
index|[
name|numIterators
index|]
decl_stmt|;
name|Scorer
name|conjunction
init|=
literal|null
decl_stmt|;
name|boolean
name|hasApproximation
init|=
literal|false
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
name|numIterators
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FixedBitSet
name|set
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
specifier|final
name|Scorer
name|newIterator
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// simple iterator
name|sets
index|[
name|i
index|]
operator|=
name|set
expr_stmt|;
name|newIterator
operator|=
operator|new
name|ConstantScoreScorer
argument_list|(
literal|null
argument_list|,
literal|0f
argument_list|,
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// scorer with approximation
specifier|final
name|FixedBitSet
name|confirmed
init|=
name|clearRandomBits
argument_list|(
name|set
argument_list|)
decl_stmt|;
name|sets
index|[
name|i
index|]
operator|=
name|confirmed
expr_stmt|;
specifier|final
name|TwoPhaseIterator
name|approximation
init|=
name|approximation
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|confirmed
argument_list|)
decl_stmt|;
name|newIterator
operator|=
name|scorer
argument_list|(
name|approximation
argument_list|)
expr_stmt|;
name|hasApproximation
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|conjunction
operator|==
literal|null
condition|)
block|{
name|conjunction
operator|=
name|newIterator
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|ConjunctionDISI
name|conj
init|=
name|ConjunctionDISI
operator|.
name|intersectScorers
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|conjunction
argument_list|,
name|newIterator
argument_list|)
argument_list|)
decl_stmt|;
name|conjunction
operator|=
name|scorer
argument_list|(
name|conj
argument_list|,
name|conj
operator|.
name|asTwoPhaseIterator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|TwoPhaseIterator
name|twoPhaseIterator
init|=
name|conjunction
operator|.
name|twoPhaseIterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hasApproximation
argument_list|,
name|twoPhaseIterator
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasApproximation
condition|)
block|{
name|assertEquals
argument_list|(
name|intersect
argument_list|(
name|sets
argument_list|)
argument_list|,
name|toBitSet
argument_list|(
name|maxDoc
argument_list|,
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|intersect
argument_list|(
name|sets
argument_list|)
argument_list|,
name|toBitSet
argument_list|(
name|maxDoc
argument_list|,
name|conjunction
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testCollapseSubConjunctions
specifier|public
name|void
name|testCollapseSubConjunctions
parameter_list|(
name|boolean
name|wrapWithScorer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numIterators
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
index|[]
name|sets
init|=
operator|new
name|FixedBitSet
index|[
name|numIterators
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Scorer
argument_list|>
name|scorers
init|=
operator|new
name|LinkedList
argument_list|<>
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
name|numIterators
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FixedBitSet
name|set
init|=
name|randomSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// simple iterator
name|sets
index|[
name|i
index|]
operator|=
name|set
expr_stmt|;
name|scorers
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreScorer
argument_list|(
literal|null
argument_list|,
literal|0f
argument_list|,
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// scorer with approximation
specifier|final
name|FixedBitSet
name|confirmed
init|=
name|clearRandomBits
argument_list|(
name|set
argument_list|)
decl_stmt|;
name|sets
index|[
name|i
index|]
operator|=
name|confirmed
expr_stmt|;
specifier|final
name|TwoPhaseIterator
name|approximation
init|=
name|approximation
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|confirmed
argument_list|)
decl_stmt|;
name|scorers
operator|.
name|add
argument_list|(
name|scorer
argument_list|(
name|approximation
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// make some sub sequences into sub conjunctions
specifier|final
name|int
name|subIters
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|subIter
init|=
literal|0
init|;
name|subIter
argument_list|<
name|subIters
operator|&&
name|scorers
operator|.
name|size
operator|(
operator|)
argument_list|>
literal|3
condition|;
operator|++
name|subIter
control|)
block|{
specifier|final
name|int
name|subSeqStart
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|scorers
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|int
name|subSeqEnd
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|subSeqStart
operator|+
literal|2
argument_list|,
name|scorers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Scorer
argument_list|>
name|subIterators
init|=
name|scorers
operator|.
name|subList
argument_list|(
name|subSeqStart
argument_list|,
name|subSeqEnd
argument_list|)
decl_stmt|;
name|Scorer
name|subConjunction
decl_stmt|;
if|if
condition|(
name|wrapWithScorer
condition|)
block|{
name|subConjunction
operator|=
operator|new
name|ConjunctionScorer
argument_list|(
literal|null
argument_list|,
name|subIterators
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|subConjunction
operator|=
operator|new
name|ConstantScoreScorer
argument_list|(
literal|null
argument_list|,
literal|0f
argument_list|,
name|ConjunctionDISI
operator|.
name|intersectScorers
argument_list|(
name|subIterators
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|scorers
operator|.
name|set
argument_list|(
name|subSeqStart
argument_list|,
name|subConjunction
argument_list|)
expr_stmt|;
name|int
name|toRemove
init|=
name|subSeqEnd
operator|-
name|subSeqStart
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|toRemove
operator|--
operator|>
literal|0
condition|)
block|{
name|scorers
operator|.
name|remove
argument_list|(
name|subSeqStart
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|scorers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// ConjunctionDISI needs two iterators
name|scorers
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreScorer
argument_list|(
literal|null
argument_list|,
literal|0f
argument_list|,
name|DocIdSetIterator
operator|.
name|all
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ConjunctionDISI
name|conjunction
init|=
name|ConjunctionDISI
operator|.
name|intersectScorers
argument_list|(
name|scorers
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|intersect
argument_list|(
name|sets
argument_list|)
argument_list|,
name|toBitSet
argument_list|(
name|maxDoc
argument_list|,
name|conjunction
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCollapseSubConjunctionDISIs
specifier|public
name|void
name|testCollapseSubConjunctionDISIs
parameter_list|()
throws|throws
name|IOException
block|{
name|testCollapseSubConjunctions
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testCollapseSubConjunctionScorers
specifier|public
name|void
name|testCollapseSubConjunctionScorers
parameter_list|()
throws|throws
name|IOException
block|{
name|testCollapseSubConjunctions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

