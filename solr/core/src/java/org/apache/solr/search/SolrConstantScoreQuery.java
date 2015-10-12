begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|ConstantScoreScorer
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
name|ConstantScoreWeight
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
name|Weight
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * A query that wraps a filter and simply returns a constant score equal to the  * query boost for every document in the filter.   This Solr extension also supports  * weighting of a SolrFilter.  *  * Experimental and subject to change.  */
end_comment

begin_class
DECL|class|SolrConstantScoreQuery
specifier|public
class|class
name|SolrConstantScoreQuery
extends|extends
name|Query
implements|implements
name|ExtendedQuery
block|{
DECL|field|filter
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
DECL|field|cache
name|boolean
name|cache
init|=
literal|true
decl_stmt|;
comment|// cache by default
DECL|field|cost
name|int
name|cost
decl_stmt|;
DECL|method|SolrConstantScoreQuery
specifier|public
name|SolrConstantScoreQuery
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/** Returns the encapsulated filter */
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
annotation|@
name|Override
DECL|method|setCache
specifier|public
name|void
name|setCache
parameter_list|(
name|boolean
name|cache
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCache
specifier|public
name|boolean
name|getCache
parameter_list|()
block|{
return|return
name|cache
return|;
block|}
annotation|@
name|Override
DECL|method|setCacheSep
specifier|public
name|void
name|setCacheSep
parameter_list|(
name|boolean
name|cacheSep
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getCacheSep
specifier|public
name|boolean
name|getCacheSep
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setCost
specifier|public
name|void
name|setCost
parameter_list|(
name|int
name|cost
parameter_list|)
block|{
name|this
operator|.
name|cost
operator|=
name|cost
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCost
specifier|public
name|int
name|getCost
parameter_list|()
block|{
return|return
name|cost
return|;
block|}
DECL|class|ConstantWeight
specifier|protected
class|class
name|ConstantWeight
extends|extends
name|ConstantScoreWeight
block|{
DECL|field|context
specifier|private
name|Map
name|context
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
name|super
argument_list|(
name|SolrConstantScoreQuery
operator|.
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|ValueSource
operator|.
name|newContext
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
if|if
condition|(
name|filter
operator|instanceof
name|SolrFilter
condition|)
operator|(
operator|(
name|SolrFilter
operator|)
name|filter
operator|)
operator|.
name|createWeight
argument_list|(
name|context
argument_list|,
name|searcher
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
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|DocIdSet
name|docIdSet
init|=
name|filter
operator|instanceof
name|SolrFilter
condition|?
operator|(
operator|(
name|SolrFilter
operator|)
name|filter
operator|)
operator|.
name|getDocIdSet
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
else|:
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|docIdSet
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DocIdSetIterator
name|iterator
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
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
name|this
argument_list|,
name|score
argument_list|()
argument_list|,
name|iterator
argument_list|)
return|;
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
return|return
operator|new
name|SolrConstantScoreQuery
operator|.
name|ConstantWeight
argument_list|(
name|searcher
argument_list|)
return|;
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
name|field
parameter_list|)
block|{
return|return
name|ExtendedQueryBase
operator|.
name|getOptionsString
argument_list|(
name|this
argument_list|)
operator|+
literal|"ConstantScore("
operator|+
name|filter
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
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
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|==
literal|false
condition|)
return|return
literal|false
return|;
name|SolrConstantScoreQuery
name|other
init|=
operator|(
name|SolrConstantScoreQuery
operator|)
name|o
decl_stmt|;
return|return
name|filter
operator|.
name|equals
argument_list|(
name|other
operator|.
name|filter
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
return|return
literal|31
operator|*
name|super
operator|.
name|hashCode
argument_list|()
operator|+
name|filter
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

