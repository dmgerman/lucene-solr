begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|facet
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|Collector
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
name|ConstantScoreQuery
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
name|FieldDoc
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
name|Filter
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
name|IndexSearcher
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
name|MatchAllDocsQuery
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
name|Query
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
name|ScoreDoc
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
name|Sort
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
name|TermQuery
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
name|TopDocs
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
name|TopFieldCollector
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
name|TopScoreDocCollector
import|;
end_import

begin_comment
comment|/**       * Computes drill down and sideways counts for the provided  * {@link DrillDownQuery}.  Drill sideways counts include  * alternative values/aggregates for the drill-down  * dimensions so that a dimension does not disappear after  * the user drills down into it.  *  *<p> Use one of the static search  * methods to do the search, and then get the hits and facet  * results from the returned {@link DrillSidewaysResult}.  *  *<p><b>NOTE</b>: this allocates one {@link  * FacetsCollector} for each drill-down, plus one.  If your  * index has high number of facet labels then this will  * multiply your memory usage.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DrillSideways
specifier|public
class|class
name|DrillSideways
block|{
DECL|field|searcher
specifier|protected
specifier|final
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|taxoReader
specifier|protected
specifier|final
name|TaxonomyReader
name|taxoReader
decl_stmt|;
comment|/** Create a new {@code DrillSideways} instance. */
DECL|method|DrillSideways
specifier|public
name|DrillSideways
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|TaxonomyReader
name|taxoReader
parameter_list|)
block|{
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|taxoReader
operator|=
name|taxoReader
expr_stmt|;
block|}
comment|/**    * Search, collecting hits with a {@link Collector}, and    * computing drill down and sideways counts.    */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|Collector
name|hitCollector
parameter_list|,
name|FacetSearchParams
name|fsp
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|drillDownDims
init|=
name|query
operator|.
name|getDims
argument_list|()
decl_stmt|;
if|if
condition|(
name|drillDownDims
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"there must be at least one drill-down"
argument_list|)
throw|;
block|}
name|BooleanQuery
name|ddq
init|=
name|query
operator|.
name|getBooleanQuery
argument_list|()
decl_stmt|;
name|BooleanClause
index|[]
name|clauses
init|=
name|ddq
operator|.
name|getClauses
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
if|if
condition|(
name|fr
operator|.
name|categoryPath
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"all FacetRequests must have CategoryPath with length> 0"
argument_list|)
throw|;
block|}
block|}
name|Query
name|baseQuery
decl_stmt|;
name|int
name|startClause
decl_stmt|;
if|if
condition|(
name|clauses
operator|.
name|length
operator|==
name|drillDownDims
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// TODO: we could optimize this pure-browse case by
comment|// making a custom scorer instead:
name|baseQuery
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
name|startClause
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|clauses
operator|.
name|length
operator|==
literal|1
operator|+
name|drillDownDims
operator|.
name|size
argument_list|()
assert|;
name|baseQuery
operator|=
name|clauses
index|[
literal|0
index|]
operator|.
name|getQuery
argument_list|()
expr_stmt|;
name|startClause
operator|=
literal|1
expr_stmt|;
block|}
name|Term
index|[]
index|[]
name|drillDownTerms
init|=
operator|new
name|Term
index|[
name|clauses
operator|.
name|length
operator|-
name|startClause
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startClause
init|;
name|i
operator|<
name|clauses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Query
name|q
init|=
name|clauses
index|[
name|i
index|]
operator|.
name|getQuery
argument_list|()
decl_stmt|;
assert|assert
name|q
operator|instanceof
name|ConstantScoreQuery
assert|;
name|q
operator|=
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|q
operator|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
assert|assert
name|q
operator|instanceof
name|TermQuery
operator|||
name|q
operator|instanceof
name|BooleanQuery
assert|;
if|if
condition|(
name|q
operator|instanceof
name|TermQuery
condition|)
block|{
name|drillDownTerms
index|[
name|i
operator|-
name|startClause
index|]
operator|=
operator|new
name|Term
index|[]
block|{
operator|(
operator|(
name|TermQuery
operator|)
name|q
operator|)
operator|.
name|getTerm
argument_list|()
block|}
expr_stmt|;
block|}
else|else
block|{
name|BooleanQuery
name|q2
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|BooleanClause
index|[]
name|clauses2
init|=
name|q2
operator|.
name|getClauses
argument_list|()
decl_stmt|;
name|drillDownTerms
index|[
name|i
operator|-
name|startClause
index|]
operator|=
operator|new
name|Term
index|[
name|clauses2
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|clauses2
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
assert|assert
name|clauses2
index|[
name|j
index|]
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|TermQuery
assert|;
name|drillDownTerms
index|[
name|i
operator|-
name|startClause
index|]
index|[
name|j
index|]
operator|=
operator|(
operator|(
name|TermQuery
operator|)
name|clauses2
index|[
name|j
index|]
operator|.
name|getQuery
argument_list|()
operator|)
operator|.
name|getTerm
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|FacetsCollector
name|drillDownCollector
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|getDrillDownAccumulator
argument_list|(
name|fsp
argument_list|)
argument_list|)
decl_stmt|;
name|FacetsCollector
index|[]
name|drillSidewaysCollectors
init|=
operator|new
name|FacetsCollector
index|[
name|drillDownDims
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
name|String
name|dim
range|:
name|drillDownDims
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
assert|assert
name|fr
operator|.
name|categoryPath
operator|.
name|length
operator|>
literal|0
assert|;
if|if
condition|(
name|fr
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|dim
argument_list|)
condition|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|fr
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"could not find FacetRequest for drill-sideways dimension \""
operator|+
name|dim
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|drillSidewaysCollectors
index|[
name|idx
operator|++
index|]
operator|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|getDrillSidewaysAccumulator
argument_list|(
name|dim
argument_list|,
operator|new
name|FacetSearchParams
argument_list|(
name|fsp
operator|.
name|indexingParams
argument_list|,
name|requests
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|DrillSidewaysQuery
name|dsq
init|=
operator|new
name|DrillSidewaysQuery
argument_list|(
name|baseQuery
argument_list|,
name|drillDownCollector
argument_list|,
name|drillSidewaysCollectors
argument_list|,
name|drillDownTerms
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|dsq
argument_list|,
name|hitCollector
argument_list|)
expr_stmt|;
name|int
name|numDims
init|=
name|drillDownDims
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
index|[]
name|drillSidewaysResults
init|=
operator|new
name|List
index|[
name|numDims
index|]
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|drillDownResults
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|mergedResults
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
name|int
index|[]
name|requestUpto
init|=
operator|new
name|int
index|[
name|drillDownDims
operator|.
name|size
argument_list|()
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
name|fsp
operator|.
name|facetRequests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FacetRequest
name|fr
init|=
name|fsp
operator|.
name|facetRequests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
assert|assert
name|fr
operator|.
name|categoryPath
operator|.
name|length
operator|>
literal|0
assert|;
name|Integer
name|dimIndex
init|=
name|drillDownDims
operator|.
name|get
argument_list|(
name|fr
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|dimIndex
operator|==
literal|null
condition|)
block|{
comment|// Pure drill down dim (the current query didn't
comment|// drill down on this dim):
if|if
condition|(
name|drillDownResults
operator|==
literal|null
condition|)
block|{
comment|// Lazy init, in case all requests were against
comment|// drill-sideways dims:
name|drillDownResults
operator|=
name|drillDownCollector
operator|.
name|getFacetResults
argument_list|()
expr_stmt|;
block|}
name|mergedResults
operator|.
name|add
argument_list|(
name|drillDownResults
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Drill sideways dim:
name|int
name|dim
init|=
name|dimIndex
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|sidewaysResult
init|=
name|drillSidewaysResults
index|[
name|dim
index|]
decl_stmt|;
if|if
condition|(
name|sidewaysResult
operator|==
literal|null
condition|)
block|{
comment|// Lazy init, in case no facet request is against
comment|// a given drill down dim:
name|sidewaysResult
operator|=
name|drillSidewaysCollectors
index|[
name|dim
index|]
operator|.
name|getFacetResults
argument_list|()
expr_stmt|;
name|drillSidewaysResults
index|[
name|dim
index|]
operator|=
name|sidewaysResult
expr_stmt|;
block|}
name|mergedResults
operator|.
name|add
argument_list|(
name|sidewaysResult
operator|.
name|get
argument_list|(
name|requestUpto
index|[
name|dim
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|requestUpto
index|[
name|dim
index|]
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|DrillSidewaysResult
argument_list|(
name|mergedResults
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Search, sorting by {@link Sort}, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|DrillDownQuery
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|FieldDoc
name|after
parameter_list|,
name|int
name|topN
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|boolean
name|doDocScores
parameter_list|,
name|boolean
name|doMaxScore
parameter_list|,
name|FacetSearchParams
name|fsp
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|DrillDownQuery
argument_list|(
name|filter
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TopFieldCollector
name|hitCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
name|after
argument_list|,
literal|true
argument_list|,
name|doDocScores
argument_list|,
name|doMaxScore
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DrillSidewaysResult
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|hitCollector
argument_list|,
name|fsp
argument_list|)
decl_stmt|;
name|r
operator|.
name|hits
operator|=
name|hitCollector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
else|else
block|{
return|return
name|search
argument_list|(
name|after
argument_list|,
name|query
argument_list|,
name|topN
argument_list|,
name|fsp
argument_list|)
return|;
block|}
block|}
comment|/**    * Search, sorting by score, and computing    * drill down and sideways counts.    */
DECL|method|search
specifier|public
name|DrillSidewaysResult
name|search
parameter_list|(
name|ScoreDoc
name|after
parameter_list|,
name|DrillDownQuery
name|query
parameter_list|,
name|int
name|topN
parameter_list|,
name|FacetSearchParams
name|fsp
parameter_list|)
throws|throws
name|IOException
block|{
name|TopScoreDocCollector
name|hitCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|topN
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|,
name|after
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|DrillSidewaysResult
name|r
init|=
name|search
argument_list|(
name|query
argument_list|,
name|hitCollector
argument_list|,
name|fsp
argument_list|)
decl_stmt|;
name|r
operator|.
name|hits
operator|=
name|hitCollector
operator|.
name|topDocs
argument_list|()
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/** Override this to use a custom drill-down {@link    *  FacetsAccumulator}. */
DECL|method|getDrillDownAccumulator
specifier|protected
name|FacetsAccumulator
name|getDrillDownAccumulator
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FacetsAccumulator
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|taxoReader
argument_list|)
return|;
block|}
comment|/** Override this to use a custom drill-sideways {@link    *  FacetsAccumulator}. */
DECL|method|getDrillSidewaysAccumulator
specifier|protected
name|FacetsAccumulator
name|getDrillSidewaysAccumulator
parameter_list|(
name|String
name|dim
parameter_list|,
name|FacetSearchParams
name|fsp
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FacetsAccumulator
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|taxoReader
argument_list|)
return|;
block|}
comment|/** Represents the returned result from a drill sideways    *  search. */
DECL|class|DrillSidewaysResult
specifier|public
specifier|static
class|class
name|DrillSidewaysResult
block|{
comment|/** Combined drill down& sideways results. */
DECL|field|facetResults
specifier|public
specifier|final
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
decl_stmt|;
comment|/** Hits. */
DECL|field|hits
specifier|public
name|TopDocs
name|hits
decl_stmt|;
DECL|method|DrillSidewaysResult
name|DrillSidewaysResult
parameter_list|(
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
parameter_list|,
name|TopDocs
name|hits
parameter_list|)
block|{
name|this
operator|.
name|facetResults
operator|=
name|facetResults
expr_stmt|;
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

