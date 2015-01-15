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
name|Set
import|;
end_import

begin_comment
comment|/**  * A query that applies a filter to the results of another query.  *  *<p>Note: the bits are retrieved from the filter each time this  * query is used in a search - use a CachingWrapperFilter to avoid  * regenerating the bits every time.  * @since   1.4  * @see     CachingWrapperFilter  */
end_comment

begin_class
DECL|class|FilteredQuery
specifier|public
class|class
name|FilteredQuery
extends|extends
name|Query
block|{
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|strategy
specifier|private
specifier|final
name|FilterStrategy
name|strategy
decl_stmt|;
comment|/**    * Constructs a new query which applies a filter to the results of the original query.    * {@link Filter#getDocIdSet} will be called every time this query is used in a search.    * @param query  Query to be filtered, cannot be<code>null</code>.    * @param filter Filter to apply to query results, cannot be<code>null</code>.    */
DECL|method|FilteredQuery
specifier|public
name|FilteredQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|this
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|RANDOM_ACCESS_FILTER_STRATEGY
argument_list|)
expr_stmt|;
block|}
comment|/**    * Expert: Constructs a new query which applies a filter to the results of the original query.    * {@link Filter#getDocIdSet} will be called every time this query is used in a search.    * @param query  Query to be filtered, cannot be<code>null</code>.    * @param filter Filter to apply to query results, cannot be<code>null</code>.    * @param strategy a filter strategy used to create a filtered scorer.     *     * @see FilterStrategy    */
DECL|method|FilteredQuery
specifier|public
name|FilteredQuery
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|FilterStrategy
name|strategy
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
operator|||
name|filter
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Query and filter cannot be null."
argument_list|)
throw|;
if|if
condition|(
name|strategy
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"FilterStrategy can not be null"
argument_list|)
throw|;
name|this
operator|.
name|strategy
operator|=
name|strategy
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * Returns a Weight that applies the filter to the enclosed query's Weight.    * This is accomplished by overriding the Scorer returned by the Weight.    */
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
specifier|final
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Weight
name|weight
init|=
name|query
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
return|return
operator|new
name|Weight
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|weight
operator|.
name|getValueForNormalization
argument_list|()
operator|*
name|getBoost
argument_list|()
operator|*
name|getBoost
argument_list|()
return|;
comment|// boost sub-weight
block|}
annotation|@
name|Override
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
name|weight
operator|.
name|normalize
argument_list|(
name|norm
argument_list|,
name|topLevelBoost
operator|*
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
comment|// incorporate boost
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|ir
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|Explanation
name|inner
init|=
name|weight
operator|.
name|explain
argument_list|(
name|ir
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Filter
name|f
init|=
name|FilteredQuery
operator|.
name|this
operator|.
name|filter
decl_stmt|;
name|DocIdSet
name|docIdSet
init|=
name|f
operator|.
name|getDocIdSet
argument_list|(
name|ir
argument_list|,
name|ir
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|DocIdSetIterator
name|docIdSetIterator
init|=
name|docIdSet
operator|==
literal|null
condition|?
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
else|:
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|docIdSetIterator
operator|==
literal|null
condition|)
block|{
name|docIdSetIterator
operator|=
name|DocIdSetIterator
operator|.
name|empty
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|docIdSetIterator
operator|.
name|advance
argument_list|(
name|i
argument_list|)
operator|==
name|i
condition|)
block|{
return|return
name|inner
return|;
block|}
else|else
block|{
name|Explanation
name|result
init|=
operator|new
name|Explanation
argument_list|(
literal|0.0f
argument_list|,
literal|"failure to match filter: "
operator|+
name|f
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|addDetail
argument_list|(
name|inner
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|// return this query
annotation|@
name|Override
specifier|public
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|FilteredQuery
operator|.
name|this
return|;
block|}
comment|// return a filtering scorer
annotation|@
name|Override
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
assert|assert
name|filter
operator|!=
literal|null
assert|;
name|DocIdSet
name|filterDocIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterDocIdSet
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return
literal|null
return|;
block|}
return|return
name|strategy
operator|.
name|filteredScorer
argument_list|(
name|context
argument_list|,
name|weight
argument_list|,
name|filterDocIdSet
argument_list|)
return|;
block|}
comment|// return a filtering top scorer
annotation|@
name|Override
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
assert|assert
name|filter
operator|!=
literal|null
assert|;
name|DocIdSet
name|filterDocIdSet
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|acceptDocs
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterDocIdSet
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return
literal|null
return|;
block|}
return|return
name|strategy
operator|.
name|filteredBulkScorer
argument_list|(
name|context
argument_list|,
name|weight
argument_list|,
name|filterDocIdSet
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * A scorer that consults the filter iff a document was matched by the    * delegate scorer. This is useful if the filter computation is more expensive    * than document scoring or if the filter has a linear running time to compute    * the next matching doc like exact geo distances.    */
DECL|class|QueryFirstScorer
specifier|private
specifier|static
specifier|final
class|class
name|QueryFirstScorer
extends|extends
name|Scorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|scorerDoc
specifier|private
name|int
name|scorerDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|filterBits
specifier|private
specifier|final
name|Bits
name|filterBits
decl_stmt|;
DECL|method|QueryFirstScorer
specifier|protected
name|QueryFirstScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Bits
name|filterBits
parameter_list|,
name|Scorer
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|other
expr_stmt|;
name|this
operator|.
name|filterBits
operator|=
name|filterBits
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
name|int
name|doc
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|doc
operator|=
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|==
name|Scorer
operator|.
name|NO_MORE_DOCS
operator|||
name|filterBits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|scorerDoc
operator|=
name|doc
return|;
block|}
block|}
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
name|int
name|doc
init|=
name|scorer
operator|.
name|advance
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|Scorer
operator|.
name|NO_MORE_DOCS
operator|&&
operator|!
name|filterBits
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
name|scorerDoc
operator|=
name|nextDoc
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|scorerDoc
operator|=
name|doc
return|;
block|}
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
name|scorerDoc
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
name|scorer
operator|.
name|score
argument_list|()
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
name|scorer
operator|.
name|freq
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
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|scorer
argument_list|,
literal|"FILTERED"
argument_list|)
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
name|scorer
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
DECL|class|QueryFirstBulkScorer
specifier|private
specifier|static
class|class
name|QueryFirstBulkScorer
extends|extends
name|BulkScorer
block|{
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|filterBits
specifier|private
specifier|final
name|Bits
name|filterBits
decl_stmt|;
DECL|method|QueryFirstBulkScorer
specifier|public
name|QueryFirstBulkScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|Bits
name|filterBits
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|this
operator|.
name|filterBits
operator|=
name|filterBits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// the normalization trick already applies the boost of this query,
comment|// so we can use the wrapped scorer directly:
name|collector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
if|if
condition|(
name|scorer
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|int
name|scorerDoc
init|=
name|scorer
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|scorerDoc
operator|<
name|maxDoc
condition|)
block|{
if|if
condition|(
name|filterBits
operator|.
name|get
argument_list|(
name|scorerDoc
argument_list|)
condition|)
block|{
name|collector
operator|.
name|collect
argument_list|(
name|scorerDoc
argument_list|)
expr_stmt|;
block|}
name|scorer
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
name|scorer
operator|.
name|docID
argument_list|()
operator|!=
name|Scorer
operator|.
name|NO_MORE_DOCS
return|;
block|}
block|}
comment|/**    * A Scorer that uses a "leap-frog" approach (also called "zig-zag join"). The scorer and the filter    * take turns trying to advance to each other's next matching document, often    * jumping past the target document. When both land on the same document, it's    * collected.    */
DECL|class|LeapFrogScorer
specifier|private
specifier|static
specifier|final
class|class
name|LeapFrogScorer
extends|extends
name|Scorer
block|{
DECL|field|secondary
specifier|private
specifier|final
name|DocIdSetIterator
name|secondary
decl_stmt|;
DECL|field|primary
specifier|private
specifier|final
name|DocIdSetIterator
name|primary
decl_stmt|;
DECL|field|scorer
specifier|private
specifier|final
name|Scorer
name|scorer
decl_stmt|;
DECL|field|primaryDoc
specifier|private
name|int
name|primaryDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|secondaryDoc
specifier|private
name|int
name|secondaryDoc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|LeapFrogScorer
specifier|protected
name|LeapFrogScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|DocIdSetIterator
name|primary
parameter_list|,
name|DocIdSetIterator
name|secondary
parameter_list|,
name|Scorer
name|scorer
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
name|this
operator|.
name|secondary
operator|=
name|secondary
expr_stmt|;
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
DECL|method|advanceToNextCommonDoc
specifier|private
specifier|final
name|int
name|advanceToNextCommonDoc
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|secondaryDoc
operator|<
name|primaryDoc
condition|)
block|{
name|secondaryDoc
operator|=
name|secondary
operator|.
name|advance
argument_list|(
name|primaryDoc
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|secondaryDoc
operator|==
name|primaryDoc
condition|)
block|{
return|return
name|primaryDoc
return|;
block|}
else|else
block|{
name|primaryDoc
operator|=
name|primary
operator|.
name|advance
argument_list|(
name|secondaryDoc
argument_list|)
expr_stmt|;
block|}
block|}
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
name|primaryDoc
operator|=
name|primaryNext
argument_list|()
expr_stmt|;
return|return
name|advanceToNextCommonDoc
argument_list|()
return|;
block|}
DECL|method|primaryNext
specifier|protected
name|int
name|primaryNext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|primary
operator|.
name|nextDoc
argument_list|()
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
if|if
condition|(
name|target
operator|>
name|primaryDoc
condition|)
block|{
name|primaryDoc
operator|=
name|primary
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
return|return
name|advanceToNextCommonDoc
argument_list|()
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
name|secondaryDoc
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
return|return
name|scorer
operator|.
name|score
argument_list|()
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
return|return
name|scorer
operator|.
name|freq
argument_list|()
return|;
block|}
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
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ChildScorer
argument_list|(
name|scorer
argument_list|,
literal|"FILTERED"
argument_list|)
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
name|Math
operator|.
name|min
argument_list|(
name|primary
operator|.
name|cost
argument_list|()
argument_list|,
name|secondary
operator|.
name|cost
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** Rewrites the query. If the wrapped is an instance of    * {@link MatchAllDocsQuery} it returns a {@link ConstantScoreQuery}. Otherwise    * it returns a new {@code FilteredQuery} wrapping the rewritten query. */
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
specifier|final
name|Query
name|queryRewritten
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
name|queryRewritten
operator|!=
name|query
condition|)
block|{
comment|// rewrite to a new FilteredQuery wrapping the rewritten query
specifier|final
name|Query
name|rewritten
init|=
operator|new
name|FilteredQuery
argument_list|(
name|queryRewritten
argument_list|,
name|filter
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
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
else|else
block|{
comment|// nothing to rewrite, we are done!
return|return
name|this
return|;
block|}
block|}
comment|/** Returns this FilteredQuery's (unfiltered) Query */
DECL|method|getQuery
specifier|public
specifier|final
name|Query
name|getQuery
parameter_list|()
block|{
return|return
name|query
return|;
block|}
comment|/** Returns this FilteredQuery's filter */
DECL|method|getFilter
specifier|public
specifier|final
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|/** Returns this FilteredQuery's {@link FilterStrategy} */
DECL|method|getFilterStrategy
specifier|public
name|FilterStrategy
name|getFilterStrategy
parameter_list|()
block|{
return|return
name|this
operator|.
name|strategy
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
name|getQuery
argument_list|()
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
comment|/** Prints a user-readable version of this query. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
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
literal|"filtered("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|query
operator|.
name|toString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")->"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|buffer
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
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is equal to this. */
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
name|o
operator|==
name|this
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
assert|assert
name|o
operator|instanceof
name|FilteredQuery
assert|;
specifier|final
name|FilteredQuery
name|fq
init|=
operator|(
name|FilteredQuery
operator|)
name|o
decl_stmt|;
return|return
name|fq
operator|.
name|query
operator|.
name|equals
argument_list|(
name|this
operator|.
name|query
argument_list|)
operator|&&
name|fq
operator|.
name|filter
operator|.
name|equals
argument_list|(
name|this
operator|.
name|filter
argument_list|)
operator|&&
name|fq
operator|.
name|strategy
operator|.
name|equals
argument_list|(
name|this
operator|.
name|strategy
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object. */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|strategy
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|query
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|hash
operator|=
name|hash
operator|*
literal|31
operator|+
name|filter
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/**    * A {@link FilterStrategy} that conditionally uses a random access filter if    * the given {@link DocIdSet} supports random access (returns a non-null value    * from {@link DocIdSet#bits()}) and    * {@link RandomAccessFilterStrategy#useRandomAccess(Bits, long)} returns    *<code>true</code>. Otherwise this strategy falls back to a "zig-zag join" (    * {@link FilteredQuery#LEAP_FROG_FILTER_FIRST_STRATEGY}) strategy.    *     *<p>    * Note: this strategy is the default strategy in {@link FilteredQuery}    *</p>    */
DECL|field|RANDOM_ACCESS_FILTER_STRATEGY
specifier|public
specifier|static
specifier|final
name|FilterStrategy
name|RANDOM_ACCESS_FILTER_STRATEGY
init|=
operator|new
name|RandomAccessFilterStrategy
argument_list|()
decl_stmt|;
comment|/**    * A filter strategy that uses a "leap-frog" approach (also called "zig-zag join").     * The scorer and the filter    * take turns trying to advance to each other's next matching document, often    * jumping past the target document. When both land on the same document, it's    * collected.    *<p>    * Note: This strategy uses the filter to lead the iteration.    *</p>     */
DECL|field|LEAP_FROG_FILTER_FIRST_STRATEGY
specifier|public
specifier|static
specifier|final
name|FilterStrategy
name|LEAP_FROG_FILTER_FIRST_STRATEGY
init|=
operator|new
name|LeapFrogFilterStrategy
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**    * A filter strategy that uses a "leap-frog" approach (also called "zig-zag join").     * The scorer and the filter    * take turns trying to advance to each other's next matching document, often    * jumping past the target document. When both land on the same document, it's    * collected.    *<p>    * Note: This strategy uses the query to lead the iteration.    *</p>     */
DECL|field|LEAP_FROG_QUERY_FIRST_STRATEGY
specifier|public
specifier|static
specifier|final
name|FilterStrategy
name|LEAP_FROG_QUERY_FIRST_STRATEGY
init|=
operator|new
name|LeapFrogFilterStrategy
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/**    * A filter strategy that advances the Query or rather its {@link Scorer} first and consults the    * filter {@link DocIdSet} for each matched document.    *<p>    * Note: this strategy requires a {@link DocIdSet#bits()} to return a non-null value. Otherwise    * this strategy falls back to {@link FilteredQuery#LEAP_FROG_QUERY_FIRST_STRATEGY}    *</p>    *<p>    * Use this strategy if the filter computation is more expensive than document    * scoring or if the filter has a linear running time to compute the next    * matching doc like exact geo distances.    *</p>    */
DECL|field|QUERY_FIRST_FILTER_STRATEGY
specifier|public
specifier|static
specifier|final
name|FilterStrategy
name|QUERY_FIRST_FILTER_STRATEGY
init|=
operator|new
name|QueryFirstFilterStrategy
argument_list|()
decl_stmt|;
comment|/** Abstract class that defines how the filter ({@link DocIdSet}) applied during document collection. */
DECL|class|FilterStrategy
specifier|public
specifier|static
specifier|abstract
class|class
name|FilterStrategy
block|{
comment|/**      * Returns a filtered {@link Scorer} based on this strategy.      *       * @param context      *          the {@link org.apache.lucene.index.LeafReaderContext} for which to return the {@link Scorer}.      * @param weight the {@link FilteredQuery} {@link Weight} to create the filtered scorer.      * @param docIdSet the filter {@link DocIdSet} to apply      * @return a filtered scorer      *       * @throws IOException if an {@link IOException} occurs      */
DECL|method|filteredScorer
specifier|public
specifier|abstract
name|Scorer
name|filteredScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns a filtered {@link BulkScorer} based on this      * strategy.  This is an optional method: the default      * implementation just calls {@link #filteredScorer} and      * wraps that into a BulkScorer.      *      * @param context      *          the {@link org.apache.lucene.index.LeafReaderContext} for which to return the {@link Scorer}.      * @param weight the {@link FilteredQuery} {@link Weight} to create the filtered scorer.      * @param docIdSet the filter {@link DocIdSet} to apply      * @return a filtered top scorer      */
DECL|method|filteredBulkScorer
specifier|public
name|BulkScorer
name|filteredBulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
block|{
name|Scorer
name|scorer
init|=
name|filteredScorer
argument_list|(
name|context
argument_list|,
name|weight
argument_list|,
name|docIdSet
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
comment|// This impl always scores docs in order, so we can
comment|// ignore scoreDocsInOrder:
return|return
operator|new
name|Weight
operator|.
name|DefaultBulkScorer
argument_list|(
name|scorer
argument_list|)
return|;
block|}
block|}
comment|/**    * A {@link FilterStrategy} that conditionally uses a random access filter if    * the given {@link DocIdSet} supports random access (returns a non-null value    * from {@link DocIdSet#bits()}) and    * {@link RandomAccessFilterStrategy#useRandomAccess(Bits, long)} returns    *<code>true</code>. Otherwise this strategy falls back to a "zig-zag join" (    * {@link FilteredQuery#LEAP_FROG_FILTER_FIRST_STRATEGY}) strategy .    */
DECL|class|RandomAccessFilterStrategy
specifier|public
specifier|static
class|class
name|RandomAccessFilterStrategy
extends|extends
name|FilterStrategy
block|{
annotation|@
name|Override
DECL|method|filteredScorer
specifier|public
name|Scorer
name|filteredScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdSetIterator
name|filterIter
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|filterIter
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return
literal|null
return|;
block|}
specifier|final
name|Bits
name|filterAcceptDocs
init|=
name|docIdSet
operator|.
name|bits
argument_list|()
decl_stmt|;
comment|// force if RA is requested
specifier|final
name|boolean
name|useRandomAccess
init|=
name|filterAcceptDocs
operator|!=
literal|null
operator|&&
name|useRandomAccess
argument_list|(
name|filterAcceptDocs
argument_list|,
name|filterIter
operator|.
name|cost
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|useRandomAccess
condition|)
block|{
comment|// if we are using random access, we return the inner scorer, just with other acceptDocs
return|return
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
name|filterAcceptDocs
argument_list|)
return|;
block|}
else|else
block|{
comment|// we are gonna advance() this scorer, so we set inorder=true/toplevel=false
comment|// we pass null as acceptDocs, as our filter has already respected acceptDocs, no need to do twice
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|(
name|scorer
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|LeapFrogScorer
argument_list|(
name|weight
argument_list|,
name|filterIter
argument_list|,
name|scorer
argument_list|,
name|scorer
argument_list|)
return|;
block|}
block|}
comment|/**      * Expert: decides if a filter should be executed as "random-access" or not.      * random-access means the filter "filters" in a similar way as deleted docs are filtered      * in Lucene. This is faster when the filter accepts many documents.      * However, when the filter is very sparse, it can be faster to execute the query+filter      * as a conjunction in some cases.      *       * The default implementation returns<code>true</code> if the filter matches more than 1%      * of documents      *       * @lucene.internal      */
DECL|method|useRandomAccess
specifier|protected
name|boolean
name|useRandomAccess
parameter_list|(
name|Bits
name|bits
parameter_list|,
name|long
name|filterCost
parameter_list|)
block|{
comment|// if the filter matches more than 1% of documents, we use random-access
return|return
name|filterCost
operator|*
literal|100
operator|>
name|bits
operator|.
name|length
argument_list|()
return|;
block|}
block|}
DECL|class|LeapFrogFilterStrategy
specifier|private
specifier|static
specifier|final
class|class
name|LeapFrogFilterStrategy
extends|extends
name|FilterStrategy
block|{
DECL|field|scorerFirst
specifier|private
specifier|final
name|boolean
name|scorerFirst
decl_stmt|;
DECL|method|LeapFrogFilterStrategy
specifier|private
name|LeapFrogFilterStrategy
parameter_list|(
name|boolean
name|scorerFirst
parameter_list|)
block|{
name|this
operator|.
name|scorerFirst
operator|=
name|scorerFirst
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|filteredScorer
specifier|public
name|Scorer
name|filteredScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DocIdSetIterator
name|filterIter
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|filterIter
operator|==
literal|null
condition|)
block|{
comment|// this means the filter does not accept any documents.
return|return
literal|null
return|;
block|}
comment|// we pass null as acceptDocs, as our filter has already respected acceptDocs, no need to do twice
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|null
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
if|if
condition|(
name|scorerFirst
condition|)
block|{
return|return
operator|new
name|LeapFrogScorer
argument_list|(
name|weight
argument_list|,
name|scorer
argument_list|,
name|filterIter
argument_list|,
name|scorer
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|LeapFrogScorer
argument_list|(
name|weight
argument_list|,
name|filterIter
argument_list|,
name|scorer
argument_list|,
name|scorer
argument_list|)
return|;
block|}
block|}
block|}
comment|/**    * A filter strategy that advances the {@link Scorer} first and consults the    * {@link DocIdSet} for each matched document.    *<p>    * Note: this strategy requires a {@link DocIdSet#bits()} to return a non-null value. Otherwise    * this strategy falls back to {@link FilteredQuery#LEAP_FROG_QUERY_FIRST_STRATEGY}    *</p>    *<p>    * Use this strategy if the filter computation is more expensive than document    * scoring or if the filter has a linear running time to compute the next    * matching doc like exact geo distances.    *</p>    */
DECL|class|QueryFirstFilterStrategy
specifier|private
specifier|static
specifier|final
class|class
name|QueryFirstFilterStrategy
extends|extends
name|FilterStrategy
block|{
annotation|@
name|Override
DECL|method|filteredScorer
specifier|public
name|Scorer
name|filteredScorer
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
block|{
name|Bits
name|filterAcceptDocs
init|=
name|docIdSet
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|filterAcceptDocs
operator|==
literal|null
condition|)
block|{
comment|// Filter does not provide random-access Bits; we
comment|// must fallback to leapfrog:
return|return
name|LEAP_FROG_QUERY_FIRST_STRATEGY
operator|.
name|filteredScorer
argument_list|(
name|context
argument_list|,
name|weight
argument_list|,
name|docIdSet
argument_list|)
return|;
block|}
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|scorer
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|QueryFirstScorer
argument_list|(
name|weight
argument_list|,
name|filterAcceptDocs
argument_list|,
name|scorer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|filteredBulkScorer
specifier|public
name|BulkScorer
name|filteredBulkScorer
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|,
name|Weight
name|weight
parameter_list|,
name|DocIdSet
name|docIdSet
parameter_list|)
throws|throws
name|IOException
block|{
name|Bits
name|filterAcceptDocs
init|=
name|docIdSet
operator|.
name|bits
argument_list|()
decl_stmt|;
if|if
condition|(
name|filterAcceptDocs
operator|==
literal|null
condition|)
block|{
comment|// Filter does not provide random-access Bits; we
comment|// must fallback to leapfrog:
return|return
name|LEAP_FROG_QUERY_FIRST_STRATEGY
operator|.
name|filteredBulkScorer
argument_list|(
name|context
argument_list|,
name|weight
argument_list|,
name|docIdSet
argument_list|)
return|;
block|}
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|scorer
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|QueryFirstBulkScorer
argument_list|(
name|scorer
argument_list|,
name|filterAcceptDocs
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

