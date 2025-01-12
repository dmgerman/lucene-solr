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
name|Arrays
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|LeafReaderContext
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

begin_comment
comment|/**  * A query that generates the union of documents produced by its subqueries, and that scores each document with the maximum  * score for that document as produced by any subquery, plus a tie breaking increment for any additional matching subqueries.  * This is useful when searching for a word in multiple fields with different boost factors (so that the fields cannot be  * combined equivalently into a single search field).  We want the primary score to be the one associated with the highest boost,  * not the sum of the field scores (as BooleanQuery would give).  * If the query is "albino elephant" this ensures that "albino" matching one field and "elephant" matching  * another gets a higher score than "albino" matching both fields.  * To get this result, use both BooleanQuery and DisjunctionMaxQuery:  for each term a DisjunctionMaxQuery searches for it in  * each field, while the set of these DisjunctionMaxQuery's is combined into a BooleanQuery.  * The tie breaker capability allows results that include the same term in multiple fields to be judged better than results that  * include this term in only the best of those multiple fields, without confusing this with the better case of two different terms  * in the multiple fields.  */
end_comment

begin_class
DECL|class|DisjunctionMaxQuery
specifier|public
specifier|final
class|class
name|DisjunctionMaxQuery
extends|extends
name|Query
implements|implements
name|Iterable
argument_list|<
name|Query
argument_list|>
block|{
comment|/* The subqueries */
DECL|field|disjuncts
specifier|private
specifier|final
name|Query
index|[]
name|disjuncts
decl_stmt|;
comment|/* Multiple of the non-max disjunct scores added into our final score.  Non-zero values support tie-breaking. */
DECL|field|tieBreakerMultiplier
specifier|private
specifier|final
name|float
name|tieBreakerMultiplier
decl_stmt|;
comment|/**    * Creates a new DisjunctionMaxQuery    * @param disjuncts a {@code Collection<Query>} of all the disjuncts to add    * @param tieBreakerMultiplier  the score of each non-maximum disjunct for a document is multiplied by this weight    *        and added into the final score.  If non-zero, the value should be small, on the order of 0.1, which says that    *        10 occurrences of word in a lower-scored field that is also in a higher scored field is just as good as a unique    *        word in the lower scored field (i.e., one that is not in any higher scored field.    */
DECL|method|DisjunctionMaxQuery
specifier|public
name|DisjunctionMaxQuery
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|disjuncts
parameter_list|,
name|float
name|tieBreakerMultiplier
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|disjuncts
argument_list|,
literal|"Collection of Querys must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|tieBreakerMultiplier
operator|=
name|tieBreakerMultiplier
expr_stmt|;
name|this
operator|.
name|disjuncts
operator|=
name|disjuncts
operator|.
name|toArray
argument_list|(
operator|new
name|Query
index|[
name|disjuncts
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
comment|/** @return An {@code Iterator<Query>} over the disjuncts */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Query
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|getDisjuncts
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * @return the disjuncts.    */
DECL|method|getDisjuncts
specifier|public
name|List
argument_list|<
name|Query
argument_list|>
name|getDisjuncts
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|disjuncts
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @return tie breaker value for multiple matches.    */
DECL|method|getTieBreakerMultiplier
specifier|public
name|float
name|getTieBreakerMultiplier
parameter_list|()
block|{
return|return
name|tieBreakerMultiplier
return|;
block|}
comment|/**    * Expert: the Weight for DisjunctionMaxQuery, used to    * normalize, score and explain these queries.    *    *<p>NOTE: this API and implementation is subject to    * change suddenly in the next release.</p>    */
DECL|class|DisjunctionMaxWeight
specifier|protected
class|class
name|DisjunctionMaxWeight
extends|extends
name|Weight
block|{
comment|/** The Weights for our subqueries, in 1-1 correspondence with disjuncts */
DECL|field|weights
specifier|protected
specifier|final
name|ArrayList
argument_list|<
name|Weight
argument_list|>
name|weights
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// The Weight's for our subqueries, in 1-1 correspondence with disjuncts
DECL|field|needsScores
specifier|private
specifier|final
name|boolean
name|needsScores
decl_stmt|;
comment|/** Construct the Weight for this Query searched by searcher.  Recursively construct subquery weights. */
DECL|method|DisjunctionMaxWeight
specifier|public
name|DisjunctionMaxWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|DisjunctionMaxQuery
operator|.
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|Query
name|disjunctQuery
range|:
name|disjuncts
control|)
block|{
name|weights
operator|.
name|add
argument_list|(
name|searcher
operator|.
name|createWeight
argument_list|(
name|disjunctQuery
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|needsScores
operator|=
name|needsScores
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
for|for
control|(
name|Weight
name|weight
range|:
name|weights
control|)
block|{
name|weight
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Create the scorer used to score our associated DisjunctionMaxQuery */
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Scorer
argument_list|>
name|scorers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
comment|// we will advance() subscorers
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|!=
literal|null
condition|)
block|{
name|scorers
operator|.
name|add
argument_list|(
name|subScorer
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|scorers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no sub-scorers had any documents
return|return
literal|null
return|;
block|}
elseif|else
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
comment|// only one sub-scorer in this segment
return|return
name|scorers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DisjunctionMaxScorer
argument_list|(
name|this
argument_list|,
name|tieBreakerMultiplier
argument_list|,
name|scorers
argument_list|,
name|needsScores
argument_list|)
return|;
block|}
block|}
comment|/** Explain the score we computed for doc */
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|match
init|=
literal|false
decl_stmt|;
name|float
name|max
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|,
name|sum
init|=
literal|0.0f
decl_stmt|;
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Weight
name|wt
range|:
name|weights
control|)
block|{
name|Explanation
name|e
init|=
name|wt
operator|.
name|explain
argument_list|(
name|context
argument_list|,
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|match
operator|=
literal|true
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|sum
operator|+=
name|e
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|match
condition|)
block|{
specifier|final
name|float
name|score
init|=
name|max
operator|+
operator|(
name|sum
operator|-
name|max
operator|)
operator|*
name|tieBreakerMultiplier
decl_stmt|;
specifier|final
name|String
name|desc
init|=
name|tieBreakerMultiplier
operator|==
literal|0.0f
condition|?
literal|"max of:"
else|:
literal|"max plus "
operator|+
name|tieBreakerMultiplier
operator|+
literal|" times others of:"
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|score
argument_list|,
name|desc
argument_list|,
name|subs
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No matching clause"
argument_list|)
return|;
block|}
block|}
block|}
comment|// end of DisjunctionMaxWeight inner class
comment|/** Create the Weight used to score us */
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DisjunctionMaxWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
return|;
block|}
comment|/** Optimize our representation and our subqueries representations    * @param reader the IndexReader we query    * @return an optimized copy of us (which may not be a copy if there is nothing to optimize) */
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|disjuncts
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|disjuncts
index|[
literal|0
index|]
return|;
block|}
if|if
condition|(
name|tieBreakerMultiplier
operator|==
literal|1.0f
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|builder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|sub
range|:
name|disjuncts
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|sub
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
name|boolean
name|actuallyRewritten
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|Query
argument_list|>
name|rewrittenDisjuncts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|sub
range|:
name|disjuncts
control|)
block|{
name|Query
name|rewrittenSub
init|=
name|sub
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|actuallyRewritten
operator||=
name|rewrittenSub
operator|!=
name|sub
expr_stmt|;
name|rewrittenDisjuncts
operator|.
name|add
argument_list|(
name|rewrittenSub
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actuallyRewritten
condition|)
block|{
return|return
operator|new
name|DisjunctionMaxQuery
argument_list|(
name|rewrittenDisjuncts
argument_list|,
name|tieBreakerMultiplier
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
comment|/** Prettyprint us.    * @param field the field to which we are applied    * @return a string that shows what we do, of the form "(disjunct1 | disjunct2 | ... | disjunctn)^boost"    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
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
literal|"("
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
name|disjuncts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|subquery
init|=
name|disjuncts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|subquery
operator|instanceof
name|BooleanQuery
condition|)
block|{
comment|// wrap sub-bools in parens
name|buffer
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|subquery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
name|buffer
operator|.
name|append
argument_list|(
name|subquery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|disjuncts
operator|.
name|length
operator|-
literal|1
condition|)
name|buffer
operator|.
name|append
argument_list|(
literal|" | "
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
if|if
condition|(
name|tieBreakerMultiplier
operator|!=
literal|0.0f
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"~"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|tieBreakerMultiplier
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Return true iff we represent the same query as o    * @param other another object    * @return true iff o is a DisjunctionMaxQuery with the same boost and the same subqueries, in the same order, as us    */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|DisjunctionMaxQuery
name|other
parameter_list|)
block|{
return|return
name|tieBreakerMultiplier
operator|==
name|other
operator|.
name|tieBreakerMultiplier
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|disjuncts
argument_list|,
name|other
operator|.
name|disjuncts
argument_list|)
return|;
block|}
comment|/** Compute a hash code for hashing us    * @return the hash code    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|classHash
argument_list|()
decl_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|tieBreakerMultiplier
argument_list|)
expr_stmt|;
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|disjuncts
argument_list|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

