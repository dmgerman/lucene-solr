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
name|Bits
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

begin_comment
comment|/**  * A query that wraps another query and simply returns a constant score equal to the  * query boost for every document that matches the query.  * It therefore simply strips of all scores and returns a constant one.  */
end_comment

begin_class
DECL|class|ConstantScoreQuery
specifier|public
class|class
name|ConstantScoreQuery
extends|extends
name|Query
block|{
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
name|this
operator|.
name|query
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|,
literal|"Query must not be null"
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the encapsulated query. */
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
operator|.
name|getClass
argument_list|()
operator|==
name|getClass
argument_list|()
condition|)
block|{
if|if
condition|(
name|getBoost
argument_list|()
operator|!=
name|rewritten
operator|.
name|getBoost
argument_list|()
condition|)
block|{
name|rewritten
operator|=
name|rewritten
operator|.
name|clone
argument_list|()
expr_stmt|;
name|rewritten
operator|.
name|setBoost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rewritten
return|;
block|}
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
return|return
name|this
return|;
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
name|Weight
name|innerWeight
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|ConstantScoreQuery
operator|.
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|innerWeight
operator|=
name|innerWeight
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
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we calculate sumOfSquaredWeights of the inner weight, but ignore it (just to initialize everything)
name|innerWeight
operator|.
name|getValueForNormalization
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
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{
name|this
operator|.
name|queryNorm
operator|=
name|norm
operator|*
name|topLevelBoost
expr_stmt|;
name|queryWeight
operator|*=
name|this
operator|.
name|queryNorm
expr_stmt|;
comment|// we normalize the inner weight, but ignore it (just to initialize everything)
name|innerWeight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bulkScorer
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|BulkScorer
name|bulkScorer
init|=
name|innerWeight
operator|.
name|bulkScorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|bulkScorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantBulkScorer
argument_list|(
name|bulkScorer
argument_list|,
name|this
argument_list|,
name|queryWeight
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
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|innerWeight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|scorer
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ConstantScoreScorer
argument_list|(
name|scorer
argument_list|,
name|queryWeight
argument_list|)
return|;
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
specifier|final
name|Scorer
name|cs
init|=
name|scorer
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
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
if|if
condition|(
name|exists
condition|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|queryWeight
argument_list|,
name|ConstantScoreQuery
operator|.
name|this
operator|.
name|toString
argument_list|()
operator|+
literal|", product of:"
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
name|queryNorm
argument_list|,
literal|"queryNorm"
argument_list|)
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
return|;
block|}
block|}
block|}
comment|/** We return this as our {@link BulkScorer} so that if the CSQ    *  wraps a query with its own optimized top-level    *  scorer (e.g. BooleanScorer) we can use that    *  top-level scorer. */
DECL|class|ConstantBulkScorer
specifier|protected
class|class
name|ConstantBulkScorer
extends|extends
name|BulkScorer
block|{
DECL|field|bulkScorer
specifier|final
name|BulkScorer
name|bulkScorer
decl_stmt|;
DECL|field|weight
specifier|final
name|Weight
name|weight
decl_stmt|;
DECL|field|theScore
specifier|final
name|float
name|theScore
decl_stmt|;
DECL|method|ConstantBulkScorer
specifier|public
name|ConstantBulkScorer
parameter_list|(
name|BulkScorer
name|bulkScorer
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|float
name|theScore
parameter_list|)
block|{
name|this
operator|.
name|bulkScorer
operator|=
name|bulkScorer
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
name|this
operator|.
name|theScore
operator|=
name|theScore
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|bulkScorer
operator|.
name|score
argument_list|(
name|wrapCollector
argument_list|(
name|collector
argument_list|)
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
DECL|method|wrapCollector
specifier|private
name|LeafCollector
name|wrapCollector
parameter_list|(
name|LeafCollector
name|collector
parameter_list|)
block|{
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|collector
argument_list|)
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
name|in
operator|.
name|setScorer
argument_list|(
operator|new
name|ConstantScoreScorer
argument_list|(
name|scorer
argument_list|,
name|theScore
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|bulkScorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
DECL|class|ConstantScoreScorer
specifier|protected
class|class
name|ConstantScoreScorer
extends|extends
name|FilterScorer
block|{
DECL|field|score
specifier|private
specifier|final
name|float
name|score
decl_stmt|;
DECL|method|ConstantScoreScorer
specifier|public
name|ConstantScoreScorer
parameter_list|(
name|Scorer
name|wrapped
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|super
argument_list|(
name|wrapped
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
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
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|in
argument_list|,
literal|"constant"
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|ConstantDocIdSetIteratorScorer
specifier|protected
class|class
name|ConstantDocIdSetIteratorScorer
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
DECL|method|ConstantDocIdSetIteratorScorer
specifier|public
name|ConstantDocIdSetIteratorScorer
parameter_list|(
name|DocIdSetIterator
name|docIdSetIterator
parameter_list|,
name|Weight
name|w
parameter_list|,
name|float
name|theScore
parameter_list|)
block|{
name|super
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|this
operator|.
name|theScore
operator|=
name|theScore
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
assert|assert
name|docIdSetIterator
operator|.
name|docID
argument_list|()
operator|!=
name|NO_MORE_DOCS
assert|;
return|return
name|theScore
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
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
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|docIdSetIterator
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ChildScorer
argument_list|(
operator|(
name|Scorer
operator|)
name|docIdSetIterator
argument_list|,
literal|"constant"
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
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
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|innerWeight
init|=
name|searcher
operator|.
name|createWeight
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|needsScores
condition|)
block|{
return|return
operator|new
name|ConstantScoreQuery
operator|.
name|ConstantWeight
argument_list|(
name|innerWeight
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|innerWeight
return|;
block|}
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
name|query
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

