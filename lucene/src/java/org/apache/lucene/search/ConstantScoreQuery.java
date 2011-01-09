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
name|IndexReader
operator|.
name|ReaderContext
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
name|util
operator|.
name|ToStringUtils
import|;
end_import

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
name|Set
import|;
end_import

begin_comment
comment|/**  * A query that wraps another query or a filter and simply returns a constant score equal to the  * query boost for every document that matches the filter or query.  * For queries it therefore simply strips of all scores and returns a constant one.  *  *<p><b>NOTE</b>: if the wrapped filter is an instance of  * {@link CachingWrapperFilter}, you'll likely want to  * enforce deletions in the filter (using either {@link  * CachingWrapperFilter.DeletesMode#RECACHE} or {@link  * CachingWrapperFilter.DeletesMode#DYNAMIC}).  */
end_comment

begin_class
DECL|class|ConstantScoreQuery
specifier|public
class|class
name|ConstantScoreQuery
extends|extends
name|Query
block|{
DECL|field|filter
specifier|protected
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|query
specifier|protected
specifier|final
name|Query
name|query
decl_stmt|;
comment|/** Strips off scores from the passed in Query. The hits will get a constant score    * dependent on the boost factor of this query. */
DECL|method|ConstantScoreQuery
specifier|public
name|ConstantScoreQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Query may not be null"
argument_list|)
throw|;
name|this
operator|.
name|filter
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
comment|/** Wraps a Filter as a Query. The hits will get a constant score    * dependent on the boost factor of this query.    * If you simply want to strip off scores from a Query, no longer use    * {@code new ConstantScoreQuery(new QueryWrapperFilter(query))}, instead    * use {@link #ConstantScoreQuery(Query)}!    */
DECL|method|ConstantScoreQuery
specifier|public
name|ConstantScoreQuery
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Filter may not be null"
argument_list|)
throw|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|query
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Returns the encapsulated filter, returns {@code null} if a query is wrapped. */
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|/** Returns the encapsulated query, returns {@code null} if a filter is wrapped. */
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
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
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|Query
name|rewritten
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|query
condition|)
block|{
name|rewritten
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|rewritten
argument_list|)
expr_stmt|;
name|rewritten
operator|.
name|setBoost
argument_list|(
name|this
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rewritten
return|;
block|}
block|}
return|return
name|this
return|;
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
comment|// TODO: OK to not add any terms when wrapped a filter
comment|// and used with MultiSearcher, but may not be OK for
comment|// highlighting.
comment|// If a query was wrapped, we delegate to query.
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
name|query
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
DECL|class|ConstantWeight
specifier|protected
class|class
name|ConstantWeight
extends|extends
name|Weight
block|{
DECL|field|innerWeight
specifier|private
specifier|final
name|Weight
name|innerWeight
decl_stmt|;
DECL|field|similarity
specifier|private
specifier|final
name|Similarity
name|similarity
decl_stmt|;
DECL|field|queryNorm
specifier|private
name|float
name|queryNorm
decl_stmt|;
DECL|field|queryWeight
specifier|private
name|float
name|queryWeight
decl_stmt|;
DECL|method|ConstantWeight
specifier|public
name|ConstantWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|similarity
operator|=
name|getSimilarity
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|innerWeight
operator|=
operator|(
name|query
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQuery
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|ConstantScoreQuery
operator|.
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|float
name|getValue
parameter_list|()
block|{
return|return
name|queryWeight
return|;
block|}
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
comment|// we calculate sumOfSquaredWeights of the inner weight, but ignore it (just to initialize everything)
if|if
condition|(
name|innerWeight
operator|!=
literal|null
condition|)
name|innerWeight
operator|.
name|sumOfSquaredWeights
argument_list|()
expr_stmt|;
name|queryWeight
operator|=
name|getBoost
argument_list|()
expr_stmt|;
return|return
name|queryWeight
operator|*
name|queryWeight
return|;
block|}
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
name|this
operator|.
name|queryNorm
operator|=
name|norm
expr_stmt|;
name|queryWeight
operator|*=
name|this
operator|.
name|queryNorm
expr_stmt|;
comment|// we normalize the inner weight, but ignore it (just to initialize everything)
if|if
condition|(
name|innerWeight
operator|!=
literal|null
condition|)
name|innerWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|ReaderContext
name|context
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
specifier|final
name|DocIdSetIterator
name|disi
decl_stmt|;
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
assert|assert
name|query
operator|==
literal|null
assert|;
specifier|final
name|DocIdSet
name|dis
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|dis
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|disi
operator|=
name|dis
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|query
operator|!=
literal|null
operator|&&
name|innerWeight
operator|!=
literal|null
assert|;
name|disi
operator|=
name|innerWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|scoreDocsInOrder
argument_list|,
name|topScorer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|disi
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|ConstantScorer
argument_list|(
name|similarity
argument_list|,
name|disi
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
operator|(
name|innerWeight
operator|!=
literal|null
operator|)
condition|?
name|innerWeight
operator|.
name|scoresDocsOutOfOrder
argument_list|()
else|:
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|ReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Scorer
name|cs
init|=
name|scorer
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|exists
init|=
operator|(
name|cs
operator|!=
literal|null
operator|&&
name|cs
operator|.
name|advance
argument_list|(
name|doc
argument_list|)
operator|==
name|doc
operator|)
decl_stmt|;
specifier|final
name|ComplexExplanation
name|result
init|=
operator|new
name|ComplexExplanation
argument_list|()
decl_stmt|;
if|if
condition|(
name|exists
condition|)
block|{
name|result
operator|.
name|setDescription
argument_list|(
name|ConstantScoreQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
name|queryWeight
argument_list|)
expr_stmt|;
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
operator|new
name|Explanation
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|setDescription
argument_list|(
name|ConstantScoreQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|" doesn't match id "
operator|+
name|doc
argument_list|)
expr_stmt|;
name|result
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|setMatch
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
DECL|class|ConstantScorer
specifier|protected
class|class
name|ConstantScorer
extends|extends
name|Scorer
block|{
DECL|field|docIdSetIterator
specifier|final
name|DocIdSetIterator
name|docIdSetIterator
decl_stmt|;
DECL|field|theScore
specifier|final
name|float
name|theScore
decl_stmt|;
DECL|method|ConstantScorer
specifier|public
name|ConstantScorer
parameter_list|(
name|Similarity
name|similarity
parameter_list|,
name|DocIdSetIterator
name|docIdSetIterator
parameter_list|,
name|Weight
name|w
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|similarity
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|theScore
operator|=
name|w
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|docIdSetIterator
operator|=
name|docIdSetIterator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|docIdSetIterator
operator|.
name|nextDoc
argument_list|()
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
name|docIdSetIterator
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
name|theScore
return|;
block|}
annotation|@
name|Override
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
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
DECL|method|wrapCollector
specifier|private
name|Collector
name|wrapCollector
parameter_list|(
specifier|final
name|Collector
name|collector
parameter_list|)
block|{
return|return
operator|new
name|Collector
argument_list|()
block|{
annotation|@
name|Override
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
comment|// we must wrap again here, but using the scorer passed in as parameter:
name|collector
operator|.
name|setScorer
argument_list|(
operator|new
name|ConstantScorer
argument_list|(
name|ConstantScorer
operator|.
name|this
operator|.
name|getSimilarity
argument_list|()
argument_list|,
name|scorer
argument_list|,
name|ConstantScorer
operator|.
name|this
operator|.
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|collector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
throws|throws
name|IOException
block|{
name|collector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|collector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|// this optimization allows out of order scoring as top scorer!
annotation|@
name|Override
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
if|if
condition|(
name|docIdSetIterator
operator|instanceof
name|Scorer
condition|)
block|{
operator|(
operator|(
name|Scorer
operator|)
name|docIdSetIterator
operator|)
operator|.
name|score
argument_list|(
name|wrapCollector
argument_list|(
name|collector
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|score
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
comment|// this optimization allows out of order scoring as top scorer,
annotation|@
name|Override
DECL|method|score
specifier|public
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
if|if
condition|(
name|docIdSetIterator
operator|instanceof
name|Scorer
condition|)
block|{
return|return
operator|(
operator|(
name|Scorer
operator|)
name|docIdSetIterator
operator|)
operator|.
name|score
argument_list|(
name|wrapCollector
argument_list|(
name|collector
argument_list|)
argument_list|,
name|max
argument_list|,
name|firstDocID
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|score
argument_list|(
name|collector
argument_list|,
name|max
argument_list|,
name|firstDocID
argument_list|)
return|;
block|}
block|}
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ConstantScoreQuery
operator|.
name|ConstantWeight
argument_list|(
name|searcher
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
return|return
operator|new
name|StringBuilder
argument_list|(
literal|"ConstantScore("
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|query
operator|==
literal|null
operator|)
condition|?
name|filter
operator|.
name|toString
argument_list|()
else|:
name|query
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|o
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
specifier|final
name|ConstantScoreQuery
name|other
init|=
operator|(
name|ConstantScoreQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
operator|(
name|this
operator|.
name|filter
operator|==
literal|null
operator|)
condition|?
name|other
operator|.
name|filter
operator|==
literal|null
else|:
name|this
operator|.
name|filter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|filter
argument_list|)
operator|)
operator|&&
operator|(
operator|(
name|this
operator|.
name|query
operator|==
literal|null
operator|)
condition|?
name|other
operator|.
name|query
operator|==
literal|null
else|:
name|this
operator|.
name|query
operator|.
name|equals
argument_list|(
name|other
operator|.
name|query
argument_list|)
operator|)
return|;
block|}
return|return
literal|false
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
name|super
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
operator|(
name|query
operator|==
literal|null
operator|)
condition|?
name|filter
else|:
name|query
operator|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

