begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|search
operator|.
name|Scorer
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
name|join
operator|.
name|ToParentBlockJoinQuery
operator|.
name|ChildrenMatchesScorer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DelegatingCollector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
operator|.
name|BlockJoinFieldFacetAccumulator
operator|.
name|AggregatableDocIter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
operator|.
name|BlockJoinFieldFacetAccumulator
operator|.
name|SortedIntsAggDocIterator
import|;
end_import

begin_comment
comment|/**  * For each collected parent document creates matched block, which is a docSet with matched children and parent doc  * itself. Then updates each BlockJoinFieldFacetAccumulator with the created matched block.  */
end_comment

begin_class
DECL|class|BlockJoinFacetCollector
class|class
name|BlockJoinFacetCollector
extends|extends
name|DelegatingCollector
block|{
DECL|field|blockJoinFieldFacetAccumulators
specifier|private
name|BlockJoinFieldFacetAccumulator
index|[]
name|blockJoinFieldFacetAccumulators
decl_stmt|;
DECL|field|firstSegment
specifier|private
name|boolean
name|firstSegment
init|=
literal|true
decl_stmt|;
DECL|field|blockJoinScorer
specifier|private
name|ChildrenMatchesScorer
name|blockJoinScorer
decl_stmt|;
DECL|field|childDocs
specifier|private
name|int
index|[]
name|childDocs
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
DECL|method|BlockJoinFacetCollector
name|BlockJoinFacetCollector
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|facetFieldNames
init|=
name|BlockJoinFacetComponent
operator|.
name|getChildFacetFields
argument_list|(
name|req
argument_list|)
decl_stmt|;
assert|assert
name|facetFieldNames
operator|!=
literal|null
assert|;
name|blockJoinFieldFacetAccumulators
operator|=
operator|new
name|BlockJoinFieldFacetAccumulator
index|[
name|facetFieldNames
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
name|facetFieldNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|blockJoinFieldFacetAccumulators
index|[
name|i
index|]
operator|=
operator|new
name|BlockJoinFieldFacetAccumulator
argument_list|(
name|facetFieldNames
index|[
name|i
index|]
argument_list|,
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|super
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
name|blockJoinScorer
operator|=
name|getToParentScorer
argument_list|(
name|scorer
argument_list|,
operator|new
name|LinkedList
argument_list|<
name|Scorer
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockJoinScorer
operator|!=
literal|null
condition|)
block|{
comment|// instruct scorer to keep track of the child docIds for retrieval purposes.
name|blockJoinScorer
operator|.
name|trackPendingChildHits
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getToParentScorer
specifier|private
name|ChildrenMatchesScorer
name|getToParentScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|,
name|Queue
argument_list|<
name|Scorer
argument_list|>
name|queue
parameter_list|)
block|{
if|if
condition|(
name|scorer
operator|==
literal|null
operator|||
name|scorer
operator|instanceof
name|ChildrenMatchesScorer
condition|)
block|{
return|return
operator|(
name|ChildrenMatchesScorer
operator|)
name|scorer
return|;
block|}
else|else
block|{
for|for
control|(
name|Scorer
operator|.
name|ChildScorer
name|child
range|:
name|scorer
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|child
operator|.
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|getToParentScorer
argument_list|(
name|queue
operator|.
name|poll
argument_list|()
argument_list|,
name|queue
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|doSetNextReader
specifier|protected
name|void
name|doSetNextReader
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BlockJoinFieldFacetAccumulator
name|blockJoinFieldFacetAccumulator
range|:
name|blockJoinFieldFacetAccumulators
control|)
block|{
if|if
condition|(
operator|!
name|firstSegment
condition|)
block|{
name|blockJoinFieldFacetAccumulator
operator|.
name|migrateGlobal
argument_list|()
expr_stmt|;
block|}
name|blockJoinFieldFacetAccumulator
operator|.
name|setNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|firstSegment
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|doSetNextReader
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
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
name|incrementFacets
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|BlockJoinFieldFacetAccumulator
name|blockJoinFieldFacetAccumulator
range|:
name|blockJoinFieldFacetAccumulators
control|)
block|{
name|blockJoinFieldFacetAccumulator
operator|.
name|migrateGlobal
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|incrementFacets
specifier|protected
name|void
name|incrementFacets
parameter_list|(
name|int
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
index|[]
name|docNums
init|=
name|blockJoinScorer
operator|.
name|swapChildDocs
argument_list|(
name|childDocs
argument_list|)
decl_stmt|;
comment|// now we don't
comment|//includeParentDoc(parent);
comment|//final int childCountPlusParent = childTracking.getChildCount()+1;
specifier|final
name|int
name|childCountNoParent
init|=
name|blockJoinScorer
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
specifier|final
name|SortedIntsAggDocIterator
name|iter
init|=
operator|new
name|SortedIntsAggDocIterator
argument_list|(
name|docNums
argument_list|,
name|childCountNoParent
argument_list|,
name|parent
argument_list|)
decl_stmt|;
name|countFacets
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
comment|/** is not used    protected int[] includeParentDoc(int parent) {     final int[] docNums = ArrayUtil.grow(childTracking.getChildDocs(), childTracking.getChildCount()+1);     childTracking.setChildDocs(docNums); // we include parent into block, I'm not sure whether it makes sense     docNums[childTracking.getChildCount()]=parent;     return docNums;   }*/
DECL|method|countFacets
specifier|protected
name|void
name|countFacets
parameter_list|(
specifier|final
name|AggregatableDocIter
name|iter
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|BlockJoinFieldFacetAccumulator
name|blockJoinFieldFacetAccumulator
range|:
name|blockJoinFieldFacetAccumulators
control|)
block|{
name|blockJoinFieldFacetAccumulator
operator|.
name|updateCountsWithMatchedBlock
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFacets
name|NamedList
name|getFacets
parameter_list|()
block|{
name|NamedList
argument_list|<
name|NamedList
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|facets
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|blockJoinFieldFacetAccumulators
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|BlockJoinFieldFacetAccumulator
name|state
range|:
name|blockJoinFieldFacetAccumulators
control|)
block|{
name|facets
operator|.
name|add
argument_list|(
name|state
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|state
operator|.
name|getFacetValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|facets
return|;
block|}
block|}
end_class

end_unit
