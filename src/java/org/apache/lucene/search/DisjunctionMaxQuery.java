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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|ArrayList
argument_list|<
name|Query
argument_list|>
name|disjuncts
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
comment|/* Multiple of the non-max disjunct scores added into our final score.  Non-zero values support tie-breaking. */
DECL|field|tieBreakerMultiplier
specifier|private
name|float
name|tieBreakerMultiplier
init|=
literal|0.0f
decl_stmt|;
comment|/** Creates a new empty DisjunctionMaxQuery.  Use add() to add the subqueries.    * @param tieBreakerMultiplier the score of each non-maximum disjunct for a document is multiplied by this weight    *        and added into the final score.  If non-zero, the value should be small, on the order of 0.1, which says that    *        10 occurrences of word in a lower-scored field that is also in a higher scored field is just as good as a unique    *        word in the lower scored field (i.e., one that is not in any higher scored field.    */
DECL|method|DisjunctionMaxQuery
specifier|public
name|DisjunctionMaxQuery
parameter_list|(
name|float
name|tieBreakerMultiplier
parameter_list|)
block|{
name|this
operator|.
name|tieBreakerMultiplier
operator|=
name|tieBreakerMultiplier
expr_stmt|;
block|}
comment|/**    * Creates a new DisjunctionMaxQuery    * @param disjuncts a Collection<Query> of all the disjuncts to add    * @param tieBreakerMultiplier   the weight to give to each matching non-maximum disjunct    */
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
name|this
operator|.
name|tieBreakerMultiplier
operator|=
name|tieBreakerMultiplier
expr_stmt|;
name|add
argument_list|(
name|disjuncts
argument_list|)
expr_stmt|;
block|}
comment|/** Add a subquery to this disjunction    * @param query the disjunct added    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
name|disjuncts
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|/** Add a collection of disjuncts to this disjunction    * via Iterable<Query>    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Collection
argument_list|<
name|Query
argument_list|>
name|disjuncts
parameter_list|)
block|{
name|this
operator|.
name|disjuncts
operator|.
name|addAll
argument_list|(
name|disjuncts
argument_list|)
expr_stmt|;
block|}
comment|/** An Iterator<Query> over the disjuncts */
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
name|disjuncts
operator|.
name|iterator
argument_list|()
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
comment|/** The Similarity implementation. */
DECL|field|similarity
specifier|protected
name|Similarity
name|similarity
decl_stmt|;
comment|/** The Weights for our subqueries, in 1-1 correspondence with disjuncts */
DECL|field|weights
specifier|protected
name|ArrayList
argument_list|<
name|Weight
argument_list|>
name|weights
init|=
operator|new
name|ArrayList
argument_list|<
name|Weight
argument_list|>
argument_list|()
decl_stmt|;
comment|// The Weight's for our subqueries, in 1-1 correspondence with disjuncts
comment|/* Construct the Weight for this Query searched by searcher.  Recursively construct subquery weights. */
DECL|method|DisjunctionMaxWeight
specifier|public
name|DisjunctionMaxWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|()
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
name|disjunctQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Return our associated DisjunctionMaxQuery */
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|DisjunctionMaxQuery
operator|.
name|this
return|;
block|}
comment|/* Return our boost */
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|getBoost
argument_list|()
return|;
block|}
comment|/* Compute the sub of squared weights of us applied to our subqueries.  Used for normalization. */
annotation|@
name|Override
DECL|method|sumOfSquaredWeights
specifier|public
name|float
name|sumOfSquaredWeights
parameter_list|()
throws|throws
name|IOException
block|{
name|float
name|max
init|=
literal|0.0f
decl_stmt|,
name|sum
init|=
literal|0.0f
decl_stmt|;
for|for
control|(
name|Weight
name|currentWeight
range|:
name|weights
control|)
block|{
name|float
name|sub
init|=
name|currentWeight
operator|.
name|sumOfSquaredWeights
argument_list|()
decl_stmt|;
name|sum
operator|+=
name|sub
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
name|float
name|boost
init|=
name|getBoost
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
operator|(
name|sum
operator|-
name|max
operator|)
operator|*
name|tieBreakerMultiplier
operator|*
name|tieBreakerMultiplier
operator|)
operator|+
name|max
operator|)
operator|*
name|boost
operator|*
name|boost
return|;
block|}
comment|/* Apply the computed normalization factor to our subqueries */
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|)
block|{
name|norm
operator|*=
name|getBoost
argument_list|()
expr_stmt|;
comment|// Incorporate our boost
for|for
control|(
name|Weight
name|wt
range|:
name|weights
control|)
block|{
name|wt
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Create the scorer used to score our associated DisjunctionMaxQuery */
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
index|[]
name|scorers
init|=
operator|new
name|Scorer
index|[
name|weights
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Weight
name|w
range|:
name|weights
control|)
block|{
name|Scorer
name|subScorer
init|=
name|w
operator|.
name|scorer
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|subScorer
operator|!=
literal|null
operator|&&
name|subScorer
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|scorers
index|[
name|idx
operator|++
index|]
operator|=
name|subScorer
expr_stmt|;
block|}
block|}
if|if
condition|(
name|idx
operator|==
literal|0
condition|)
return|return
literal|null
return|;
comment|// all scorers did not have documents
name|DisjunctionMaxScorer
name|result
init|=
operator|new
name|DisjunctionMaxScorer
argument_list|(
name|tieBreakerMultiplier
argument_list|,
name|similarity
argument_list|,
name|scorers
argument_list|,
name|idx
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
comment|/* Explain the score we computed for doc */
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|disjuncts
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
return|return
name|weights
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|explain
argument_list|(
name|reader
argument_list|,
name|doc
argument_list|)
return|;
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
name|float
name|max
init|=
literal|0.0f
decl_stmt|,
name|sum
init|=
literal|0.0f
decl_stmt|;
name|result
operator|.
name|setDescription
argument_list|(
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
argument_list|)
expr_stmt|;
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
name|reader
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
name|result
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
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
name|result
operator|.
name|setValue
argument_list|(
name|max
operator|+
operator|(
name|sum
operator|-
name|max
operator|)
operator|*
name|tieBreakerMultiplier
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|// end of DisjunctionMaxWeight inner class
comment|/* Create the Weight used to score us */
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DisjunctionMaxWeight
argument_list|(
name|searcher
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
name|int
name|numDisjunctions
init|=
name|disjuncts
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|numDisjunctions
operator|==
literal|1
condition|)
block|{
name|Query
name|singleton
init|=
name|disjuncts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Query
name|result
init|=
name|singleton
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
if|if
condition|(
name|result
operator|==
name|singleton
condition|)
name|result
operator|=
operator|(
name|Query
operator|)
name|result
operator|.
name|clone
argument_list|()
expr_stmt|;
name|result
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
operator|*
name|result
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
name|DisjunctionMaxQuery
name|clone
init|=
literal|null
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
name|numDisjunctions
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|clause
init|=
name|disjuncts
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Query
name|rewrite
init|=
name|clause
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrite
operator|!=
name|clause
condition|)
block|{
if|if
condition|(
name|clone
operator|==
literal|null
condition|)
name|clone
operator|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|disjuncts
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
return|return
name|clone
return|;
else|else
return|return
name|this
return|;
block|}
comment|/** Create a shallow copy of us -- used in rewriting if necessary    * @return a copy of us (but reuse, don't copy, our subqueries) */
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|DisjunctionMaxQuery
name|clone
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|disjuncts
operator|=
operator|(
name|ArrayList
argument_list|<
name|Query
argument_list|>
operator|)
name|this
operator|.
name|disjuncts
operator|.
name|clone
argument_list|()
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|// inherit javadoc
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
name|Query
name|query
range|:
name|disjuncts
control|)
block|{
name|query
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
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
name|int
name|numDisjunctions
init|=
name|disjuncts
operator|.
name|size
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
name|numDisjunctions
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|subquery
init|=
name|disjuncts
operator|.
name|get
argument_list|(
name|i
argument_list|)
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
name|numDisjunctions
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
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
literal|1.0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"^"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getBoost
argument_list|()
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
comment|/** Return true iff we represent the same query as o    * @param o another object    * @return true iff o is a DisjunctionMaxQuery with the same boost and the same subqueries, in the same order, as us    */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|DisjunctionMaxQuery
operator|)
condition|)
return|return
literal|false
return|;
name|DisjunctionMaxQuery
name|other
init|=
operator|(
name|DisjunctionMaxQuery
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|&&
name|this
operator|.
name|tieBreakerMultiplier
operator|==
name|other
operator|.
name|tieBreakerMultiplier
operator|&&
name|this
operator|.
name|disjuncts
operator|.
name|equals
argument_list|(
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
return|return
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
operator|+
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|tieBreakerMultiplier
argument_list|)
operator|+
name|disjuncts
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

