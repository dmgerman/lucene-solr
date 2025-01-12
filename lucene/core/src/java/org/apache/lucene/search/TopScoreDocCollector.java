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

begin_comment
comment|/**  * A {@link Collector} implementation that collects the top-scoring hits,  * returning them as a {@link TopDocs}. This is used by {@link IndexSearcher} to  * implement {@link TopDocs}-based search. Hits are sorted by score descending  * and then (when the scores are tied) docID ascending. When you create an  * instance of this collector you should know in advance whether documents are  * going to be collected in doc Id order or not.  *  *<p><b>NOTE</b>: The values {@link Float#NaN} and  * {@link Float#NEGATIVE_INFINITY} are not valid scores.  This  * collector will not properly collect hits with such  * scores.  */
end_comment

begin_class
DECL|class|TopScoreDocCollector
specifier|public
specifier|abstract
class|class
name|TopScoreDocCollector
extends|extends
name|TopDocsCollector
argument_list|<
name|ScoreDoc
argument_list|>
block|{
DECL|class|ScorerLeafCollector
specifier|abstract
specifier|static
class|class
name|ScorerLeafCollector
implements|implements
name|LeafCollector
block|{
DECL|field|scorer
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
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
block|}
DECL|class|SimpleTopScoreDocCollector
specifier|private
specifier|static
class|class
name|SimpleTopScoreDocCollector
extends|extends
name|TopScoreDocCollector
block|{
DECL|method|SimpleTopScoreDocCollector
name|SimpleTopScoreDocCollector
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
name|super
argument_list|(
name|numHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
return|return
operator|new
name|ScorerLeafCollector
argument_list|()
block|{
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
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
comment|// This collector cannot handle these scores:
assert|assert
name|score
operator|!=
name|Float
operator|.
name|NEGATIVE_INFINITY
assert|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
name|totalHits
operator|++
expr_stmt|;
if|if
condition|(
name|score
operator|<=
name|pqTop
operator|.
name|score
condition|)
block|{
comment|// Since docs are returned in-order (i.e., increasing doc Id), a document
comment|// with equal score to pqTop.score cannot compete since HitQueue favors
comment|// documents with lower doc Ids. Therefore reject those docs too.
return|return;
block|}
name|pqTop
operator|.
name|doc
operator|=
name|doc
operator|+
name|docBase
expr_stmt|;
name|pqTop
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|pqTop
operator|=
name|pq
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
DECL|class|PagingTopScoreDocCollector
specifier|private
specifier|static
class|class
name|PagingTopScoreDocCollector
extends|extends
name|TopScoreDocCollector
block|{
DECL|field|after
specifier|private
specifier|final
name|ScoreDoc
name|after
decl_stmt|;
DECL|field|collectedHits
specifier|private
name|int
name|collectedHits
decl_stmt|;
DECL|method|PagingTopScoreDocCollector
name|PagingTopScoreDocCollector
parameter_list|(
name|int
name|numHits
parameter_list|,
name|ScoreDoc
name|after
parameter_list|)
block|{
name|super
argument_list|(
name|numHits
argument_list|)
expr_stmt|;
name|this
operator|.
name|after
operator|=
name|after
expr_stmt|;
name|this
operator|.
name|collectedHits
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|topDocsSize
specifier|protected
name|int
name|topDocsSize
parameter_list|()
block|{
return|return
name|collectedHits
operator|<
name|pq
operator|.
name|size
argument_list|()
condition|?
name|collectedHits
else|:
name|pq
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newTopDocs
specifier|protected
name|TopDocs
name|newTopDocs
parameter_list|(
name|ScoreDoc
index|[]
name|results
parameter_list|,
name|int
name|start
parameter_list|)
block|{
return|return
name|results
operator|==
literal|null
condition|?
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
operator|new
name|ScoreDoc
index|[
literal|0
index|]
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
else|:
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|results
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|docBase
init|=
name|context
operator|.
name|docBase
decl_stmt|;
specifier|final
name|int
name|afterDoc
init|=
name|after
operator|.
name|doc
operator|-
name|context
operator|.
name|docBase
decl_stmt|;
return|return
operator|new
name|ScorerLeafCollector
argument_list|()
block|{
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
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
comment|// This collector cannot handle these scores:
assert|assert
name|score
operator|!=
name|Float
operator|.
name|NEGATIVE_INFINITY
assert|;
assert|assert
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
assert|;
name|totalHits
operator|++
expr_stmt|;
if|if
condition|(
name|score
operator|>
name|after
operator|.
name|score
operator|||
operator|(
name|score
operator|==
name|after
operator|.
name|score
operator|&&
name|doc
operator|<=
name|afterDoc
operator|)
condition|)
block|{
comment|// hit was collected on a previous page
return|return;
block|}
if|if
condition|(
name|score
operator|<=
name|pqTop
operator|.
name|score
condition|)
block|{
comment|// Since docs are returned in-order (i.e., increasing doc Id), a document
comment|// with equal score to pqTop.score cannot compete since HitQueue favors
comment|// documents with lower doc Ids. Therefore reject those docs too.
return|return;
block|}
name|collectedHits
operator|++
expr_stmt|;
name|pqTop
operator|.
name|doc
operator|=
name|doc
operator|+
name|docBase
expr_stmt|;
name|pqTop
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|pqTop
operator|=
name|pq
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|/**    * Creates a new {@link TopScoreDocCollector} given the number of hits to    * collect and whether documents are scored in order by the input    * {@link Scorer} to {@link LeafCollector#setScorer(Scorer)}.    *    *<p><b>NOTE</b>: The instances returned by this method    * pre-allocate a full array of length    *<code>numHits</code>, and fill the array with sentinel    * objects.    */
DECL|method|create
specifier|public
specifier|static
name|TopScoreDocCollector
name|create
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|numHits
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Creates a new {@link TopScoreDocCollector} given the number of hits to    * collect, the bottom of the previous page, and whether documents are scored in order by the input    * {@link Scorer} to {@link LeafCollector#setScorer(Scorer)}.    *    *<p><b>NOTE</b>: The instances returned by this method    * pre-allocate a full array of length    *<code>numHits</code>, and fill the array with sentinel    * objects.    */
DECL|method|create
specifier|public
specifier|static
name|TopScoreDocCollector
name|create
parameter_list|(
name|int
name|numHits
parameter_list|,
name|ScoreDoc
name|after
parameter_list|)
block|{
if|if
condition|(
name|numHits
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numHits must be> 0; please use TotalHitCountCollector if you just need the total hit count"
argument_list|)
throw|;
block|}
if|if
condition|(
name|after
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|SimpleTopScoreDocCollector
argument_list|(
name|numHits
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|PagingTopScoreDocCollector
argument_list|(
name|numHits
argument_list|,
name|after
argument_list|)
return|;
block|}
block|}
DECL|field|pqTop
name|ScoreDoc
name|pqTop
decl_stmt|;
comment|// prevents instantiation
DECL|method|TopScoreDocCollector
name|TopScoreDocCollector
parameter_list|(
name|int
name|numHits
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|HitQueue
argument_list|(
name|numHits
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// HitQueue implements getSentinelObject to return a ScoreDoc, so we know
comment|// that at this point top() is already initialized.
name|pqTop
operator|=
name|pq
operator|.
name|top
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTopDocs
specifier|protected
name|TopDocs
name|newTopDocs
parameter_list|(
name|ScoreDoc
index|[]
name|results
parameter_list|,
name|int
name|start
parameter_list|)
block|{
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
return|return
name|EMPTY_TOPDOCS
return|;
block|}
comment|// We need to compute maxScore in order to set it in TopDocs. If start == 0,
comment|// it means the largest element is already in results, use its score as
comment|// maxScore. Otherwise pop everything else, until the largest element is
comment|// extracted and use its score as maxScore.
name|float
name|maxScore
init|=
name|Float
operator|.
name|NaN
decl_stmt|;
if|if
condition|(
name|start
operator|==
literal|0
condition|)
block|{
name|maxScore
operator|=
name|results
index|[
literal|0
index|]
operator|.
name|score
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
name|pq
operator|.
name|size
argument_list|()
init|;
name|i
operator|>
literal|1
condition|;
name|i
operator|--
control|)
block|{
name|pq
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
name|maxScore
operator|=
name|pq
operator|.
name|pop
argument_list|()
operator|.
name|score
expr_stmt|;
block|}
return|return
operator|new
name|TopDocs
argument_list|(
name|totalHits
argument_list|,
name|results
argument_list|,
name|maxScore
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

