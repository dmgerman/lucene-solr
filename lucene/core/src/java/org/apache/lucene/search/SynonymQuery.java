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
name|Collections
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
name|index
operator|.
name|TermContext
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
name|TermState
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
name|TermsEnum
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
operator|.
name|SimScorer
import|;
end_import

begin_comment
comment|/**  * A query that treats multiple terms as synonyms.  *<p>  * For scoring purposes, this query tries to score the terms as if you  * had indexed them as one term: it will match any of the terms but  * only invoke the similarity a single time, scoring the sum of all  * term frequencies for the document.  */
end_comment

begin_class
DECL|class|SynonymQuery
specifier|public
specifier|final
class|class
name|SynonymQuery
extends|extends
name|Query
block|{
DECL|field|terms
specifier|private
specifier|final
name|Term
name|terms
index|[]
decl_stmt|;
comment|/**    * Creates a new SynonymQuery, matching any of the supplied terms.    *<p>    * The terms must all have the same field.    */
DECL|method|SynonymQuery
specifier|public
name|SynonymQuery
parameter_list|(
name|Term
modifier|...
name|terms
parameter_list|)
block|{
name|this
operator|.
name|terms
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|terms
argument_list|)
operator|.
name|clone
argument_list|()
expr_stmt|;
comment|// check that all terms are the same field
name|String
name|field
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|field
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|term
operator|.
name|field
argument_list|()
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Synonyms must be across the same field"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|terms
operator|.
name|length
operator|>
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|BooleanQuery
operator|.
name|TooManyClauses
argument_list|()
throw|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|this
operator|.
name|terms
argument_list|)
expr_stmt|;
block|}
DECL|method|getTerms
specifier|public
name|List
argument_list|<
name|Term
argument_list|>
name|getTerms
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
name|terms
argument_list|)
argument_list|)
return|;
block|}
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
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Synonym("
argument_list|)
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
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|Query
name|termQuery
init|=
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|termQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
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
return|return
literal|31
operator|*
name|classHash
argument_list|()
operator|+
name|Arrays
operator|.
name|hashCode
argument_list|(
name|terms
argument_list|)
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
name|other
parameter_list|)
block|{
return|return
name|sameClassAs
argument_list|(
name|other
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|terms
argument_list|,
operator|(
operator|(
name|SynonymQuery
operator|)
name|other
operator|)
operator|.
name|terms
argument_list|)
return|;
block|}
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
comment|// optimize zero and single term cases
if|if
condition|(
name|terms
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|terms
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
name|terms
index|[
literal|0
index|]
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
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
if|if
condition|(
name|needsScores
condition|)
block|{
return|return
operator|new
name|SynonymWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|,
name|boost
argument_list|)
return|;
block|}
else|else
block|{
comment|// if scores are not needed, let BooleanWeight deal with optimizing that case.
name|BooleanQuery
operator|.
name|Builder
name|bq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Term
name|term
range|:
name|terms
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
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
name|searcher
operator|.
name|rewrite
argument_list|(
name|bq
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
return|;
block|}
block|}
DECL|class|SynonymWeight
class|class
name|SynonymWeight
extends|extends
name|Weight
block|{
DECL|field|termContexts
specifier|private
specifier|final
name|TermContext
name|termContexts
index|[]
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|simWeight
specifier|private
specifier|final
name|Similarity
operator|.
name|SimWeight
name|simWeight
decl_stmt|;
DECL|method|SynonymWeight
name|SynonymWeight
parameter_list|(
name|Query
name|query
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|float
name|boost
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|CollectionStatistics
name|collectionStats
init|=
name|searcher
operator|.
name|collectionStatistics
argument_list|(
name|terms
index|[
literal|0
index|]
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|docFreq
init|=
literal|0
decl_stmt|;
name|long
name|totalTermFreq
init|=
literal|0
decl_stmt|;
name|termContexts
operator|=
operator|new
name|TermContext
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
name|termContexts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|termContexts
index|[
name|i
index|]
operator|=
name|TermContext
operator|.
name|build
argument_list|(
name|searcher
operator|.
name|getTopReaderContext
argument_list|()
argument_list|,
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|TermStatistics
name|termStats
init|=
name|searcher
operator|.
name|termStatistics
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|,
name|termContexts
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|docFreq
operator|=
name|Math
operator|.
name|max
argument_list|(
name|termStats
operator|.
name|docFreq
argument_list|()
argument_list|,
name|docFreq
argument_list|)
expr_stmt|;
if|if
condition|(
name|termStats
operator|.
name|totalTermFreq
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|totalTermFreq
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|totalTermFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalTermFreq
operator|+=
name|termStats
operator|.
name|totalTermFreq
argument_list|()
expr_stmt|;
block|}
block|}
name|TermStatistics
name|pseudoStats
init|=
operator|new
name|TermStatistics
argument_list|(
literal|null
argument_list|,
name|docFreq
argument_list|,
name|totalTermFreq
argument_list|)
decl_stmt|;
name|this
operator|.
name|similarity
operator|=
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|simWeight
operator|=
name|similarity
operator|.
name|computeWeight
argument_list|(
name|boost
argument_list|,
name|collectionStats
argument_list|,
name|pseudoStats
argument_list|)
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
name|Term
name|term
range|:
name|SynonymQuery
operator|.
name|this
operator|.
name|terms
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Scorer
name|scorer
init|=
name|scorer
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|!=
literal|null
condition|)
block|{
name|int
name|newDoc
init|=
name|scorer
operator|.
name|iterator
argument_list|()
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|newDoc
operator|==
name|doc
condition|)
block|{
specifier|final
name|float
name|freq
decl_stmt|;
if|if
condition|(
name|scorer
operator|instanceof
name|SynonymScorer
condition|)
block|{
name|SynonymScorer
name|synScorer
init|=
operator|(
name|SynonymScorer
operator|)
name|scorer
decl_stmt|;
name|freq
operator|=
name|synScorer
operator|.
name|tf
argument_list|(
name|synScorer
operator|.
name|getSubMatches
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|scorer
operator|instanceof
name|TermScorer
assert|;
name|freq
operator|=
name|scorer
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
name|SimScorer
name|docScorer
init|=
name|similarity
operator|.
name|simScorer
argument_list|(
name|simWeight
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Explanation
name|freqExplanation
init|=
name|Explanation
operator|.
name|match
argument_list|(
name|freq
argument_list|,
literal|"termFreq="
operator|+
name|freq
argument_list|)
decl_stmt|;
name|Explanation
name|scoreExplanation
init|=
name|docScorer
operator|.
name|explain
argument_list|(
name|doc
argument_list|,
name|freqExplanation
argument_list|)
decl_stmt|;
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|scoreExplanation
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"weight("
operator|+
name|getQuery
argument_list|()
operator|+
literal|" in "
operator|+
name|doc
operator|+
literal|") ["
operator|+
name|similarity
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"], result of:"
argument_list|,
name|scoreExplanation
argument_list|)
return|;
block|}
block|}
return|return
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"no matching term"
argument_list|)
return|;
block|}
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
name|Similarity
operator|.
name|SimScorer
name|simScorer
init|=
name|similarity
operator|.
name|simScorer
argument_list|(
name|simWeight
argument_list|,
name|context
argument_list|)
decl_stmt|;
comment|// we use termscorers + disjunction as an impl detail
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
init|=
operator|new
name|ArrayList
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TermState
name|state
init|=
name|termContexts
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|context
operator|.
name|ord
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|terms
argument_list|(
name|terms
index|[
name|i
index|]
operator|.
name|field
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|terms
index|[
name|i
index|]
operator|.
name|bytes
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|PostingsEnum
name|postings
init|=
name|termsEnum
operator|.
name|postings
argument_list|(
literal|null
argument_list|,
name|PostingsEnum
operator|.
name|FREQS
argument_list|)
decl_stmt|;
name|subScorers
operator|.
name|add
argument_list|(
operator|new
name|TermScorer
argument_list|(
name|this
argument_list|,
name|postings
argument_list|,
name|simScorer
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subScorers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|subScorers
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// we must optimize this case (term not in segment), disjunctionscorer requires>= 2 subs
return|return
name|subScorers
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
name|SynonymScorer
argument_list|(
name|simScorer
argument_list|,
name|this
argument_list|,
name|subScorers
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|SynonymScorer
specifier|static
class|class
name|SynonymScorer
extends|extends
name|DisjunctionScorer
block|{
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
operator|.
name|SimScorer
name|similarity
decl_stmt|;
DECL|method|SynonymScorer
name|SynonymScorer
parameter_list|(
name|Similarity
operator|.
name|SimScorer
name|similarity
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|List
argument_list|<
name|Scorer
argument_list|>
name|subScorers
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|,
name|subScorers
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|similarity
operator|=
name|similarity
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|protected
name|float
name|score
parameter_list|(
name|DisiWrapper
name|topList
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|similarity
operator|.
name|score
argument_list|(
name|topList
operator|.
name|doc
argument_list|,
name|tf
argument_list|(
name|topList
argument_list|)
argument_list|)
return|;
block|}
comment|/** combines TF of all subs. */
DECL|method|tf
specifier|final
name|int
name|tf
parameter_list|(
name|DisiWrapper
name|topList
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|tf
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DisiWrapper
name|w
init|=
name|topList
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
name|tf
operator|+=
name|w
operator|.
name|scorer
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
return|return
name|tf
return|;
block|}
block|}
block|}
end_class

end_unit

