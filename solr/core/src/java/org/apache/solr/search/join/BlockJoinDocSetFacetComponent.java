begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|DocIdSet
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
name|DocIdSetIterator
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
name|join
operator|.
name|ToParentBlockJoinQuery
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
name|SolrException
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|BitDocSet
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
name|DocSet
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
name|Filter
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
name|QueryContext
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
name|facet
operator|.
name|BlockJoin
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

begin_comment
comment|/**  * Calculates facets on children documents and aggregates hits by parent documents.  * Enables when child.facet.field parameter specifies a field name for faceting.   * So far it supports string fields only. It requires to search by {@link ToParentBlockJoinQuery}.  * */
end_comment

begin_class
DECL|class|BlockJoinDocSetFacetComponent
specifier|public
class|class
name|BlockJoinDocSetFacetComponent
extends|extends
name|BlockJoinFacetComponentSupport
block|{
DECL|field|bjqKey
specifier|private
specifier|final
name|String
name|bjqKey
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".bjq"
decl_stmt|;
DECL|class|SegmentChildren
specifier|private
specifier|static
specifier|final
class|class
name|SegmentChildren
implements|implements
name|AggregatableDocIter
block|{
DECL|field|allParentsBitsDocSet
specifier|private
specifier|final
name|BitDocSet
name|allParentsBitsDocSet
decl_stmt|;
DECL|field|nextDoc
specifier|private
name|int
name|nextDoc
init|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
decl_stmt|;
DECL|field|disi
specifier|private
name|DocIdSetIterator
name|disi
decl_stmt|;
DECL|field|currentParent
specifier|private
name|int
name|currentParent
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|segment
specifier|final
name|LeafReaderContext
name|segment
decl_stmt|;
DECL|field|childrenMatches
specifier|final
name|DocIdSet
name|childrenMatches
decl_stmt|;
DECL|method|SegmentChildren
specifier|private
name|SegmentChildren
parameter_list|(
name|LeafReaderContext
name|subCtx
parameter_list|,
name|DocIdSet
name|dis
parameter_list|,
name|BitDocSet
name|allParentsBitsDocSet
parameter_list|)
block|{
name|this
operator|.
name|allParentsBitsDocSet
operator|=
name|allParentsBitsDocSet
expr_stmt|;
name|this
operator|.
name|childrenMatches
operator|=
name|dis
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|subCtx
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Integer
name|next
parameter_list|()
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|int
name|lastDoc
init|=
name|nextDoc
decl_stmt|;
assert|assert
name|nextDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
assert|;
if|if
condition|(
name|lastDoc
operator|>
name|currentParent
condition|)
block|{
comment|// we passed the previous block, and need to reevaluate a parent
name|currentParent
operator|=
name|allParentsBitsDocSet
operator|.
name|getBits
argument_list|()
operator|.
name|nextSetBit
argument_list|(
name|lastDoc
operator|+
name|segment
operator|.
name|docBase
argument_list|)
operator|-
name|segment
operator|.
name|docBase
expr_stmt|;
block|}
try|try
block|{
name|nextDoc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|lastDoc
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|currentParent
operator|=
operator|-
literal|1
expr_stmt|;
try|try
block|{
name|disi
operator|=
name|childrenMatches
operator|.
name|iterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|disi
operator|!=
literal|null
condition|)
block|{
name|nextDoc
operator|=
name|disi
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|nextDoc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getAggKey
specifier|public
name|int
name|getAggKey
parameter_list|()
block|{
return|return
name|currentParent
return|;
block|}
block|}
DECL|method|BlockJoinDocSetFacetComponent
specifier|public
name|BlockJoinDocSetFacetComponent
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|prepare
specifier|public
name|void
name|prepare
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getChildFacetFields
argument_list|(
name|rb
operator|.
name|req
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|validateQuery
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
name|rb
operator|.
name|setNeedDocSet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|bjqKey
argument_list|,
name|extractChildQuery
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|extractChildQuery
specifier|private
name|ToParentBlockJoinQuery
name|extractChildQuery
parameter_list|(
name|Query
name|query
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|query
operator|instanceof
name|ToParentBlockJoinQuery
operator|)
condition|)
block|{
if|if
condition|(
name|query
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
operator|(
operator|(
name|BooleanQuery
operator|)
name|query
operator|)
operator|.
name|clauses
argument_list|()
decl_stmt|;
name|ToParentBlockJoinQuery
name|once
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
if|if
condition|(
name|clause
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|ToParentBlockJoinQuery
condition|)
block|{
if|if
condition|(
name|once
operator|==
literal|null
condition|)
block|{
name|once
operator|=
operator|(
name|ToParentBlockJoinQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"can't choose between "
operator|+
name|once
operator|+
literal|" and "
operator|+
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|once
operator|!=
literal|null
condition|)
block|{
return|return
name|once
return|;
block|}
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|NO_TO_PARENT_BJQ_MESSAGE
argument_list|)
throw|;
block|}
else|else
block|{
return|return
operator|(
name|ToParentBlockJoinQuery
operator|)
name|query
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BlockJoinParentQParser
operator|.
name|AllParentsAware
name|bjq
init|=
operator|(
name|BlockJoinParentQParser
operator|.
name|AllParentsAware
operator|)
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|bjqKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|bjq
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DocSet
name|parentResult
init|=
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docSet
decl_stmt|;
specifier|final
name|BitDocSet
name|allParentsBitsDocSet
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocSetBits
argument_list|(
name|bjq
operator|.
name|getParentQuery
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DocSet
name|allChildren
init|=
name|BlockJoin
operator|.
name|toChildren
argument_list|(
name|parentResult
argument_list|,
name|allParentsBitsDocSet
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocSetBits
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
argument_list|,
name|QueryContext
operator|.
name|newContext
argument_list|(
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DocSet
name|childQueryDocSet
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getDocSet
argument_list|(
name|bjq
operator|.
name|getChildQuery
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|DocSet
name|selectedChildren
init|=
name|allChildren
operator|.
name|intersection
argument_list|(
name|childQueryDocSet
argument_list|)
decl_stmt|;
comment|// don't include parent into facet counts
comment|//childResult = childResult.union(parentResult);// just to mimic the current logic
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|rb
operator|.
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|selectedChildren
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
specifier|final
name|BlockJoinFacetAccsHolder
name|facetCounter
init|=
operator|new
name|BlockJoinFacetAccsHolder
argument_list|(
name|rb
operator|.
name|req
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|subIdx
init|=
literal|0
init|;
name|subIdx
operator|<
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|subIdx
operator|++
control|)
block|{
name|LeafReaderContext
name|subCtx
init|=
name|leaves
operator|.
name|get
argument_list|(
name|subIdx
argument_list|)
decl_stmt|;
name|DocIdSet
name|dis
init|=
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|subCtx
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// solr docsets already exclude any deleted docs
name|AggregatableDocIter
name|iter
init|=
operator|new
name|SegmentChildren
argument_list|(
name|subCtx
argument_list|,
name|dis
argument_list|,
name|allParentsBitsDocSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|facetCounter
operator|.
name|doSetNextReader
argument_list|(
name|subCtx
argument_list|)
expr_stmt|;
name|facetCounter
operator|.
name|countFacets
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
block|}
name|facetCounter
operator|.
name|finish
argument_list|()
expr_stmt|;
name|rb
operator|.
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|COLLECTOR_CONTEXT_PARAM
argument_list|,
name|facetCounter
argument_list|)
expr_stmt|;
name|super
operator|.
name|process
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

