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
name|Objects
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
name|BitSetProducer
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
name|QueryBitSetProducer
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
name|ScoreMode
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
name|lucene
operator|.
name|util
operator|.
name|BitDocIdSet
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
name|BitSet
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
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|BitsFilteredDocIdSet
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
name|QParser
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
name|QueryParsing
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
name|SolrCache
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
name|SolrConstantScoreQuery
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
name|SyntaxError
import|;
end_import

begin_class
DECL|class|BlockJoinParentQParser
specifier|public
class|class
name|BlockJoinParentQParser
extends|extends
name|QParser
block|{
comment|/** implementation detail subject to change */
DECL|field|CACHE_NAME
specifier|public
name|String
name|CACHE_NAME
init|=
literal|"perSegFilter"
decl_stmt|;
DECL|method|getParentFilterLocalParamName
specifier|protected
name|String
name|getParentFilterLocalParamName
parameter_list|()
block|{
return|return
literal|"which"
return|;
block|}
DECL|method|BlockJoinParentQParser
name|BlockJoinParentQParser
parameter_list|(
name|String
name|qstr
parameter_list|,
name|SolrParams
name|localParams
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|)
block|{
name|super
argument_list|(
name|qstr
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|filter
init|=
name|localParams
operator|.
name|get
argument_list|(
name|getParentFilterLocalParamName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|scoreMode
init|=
name|localParams
operator|.
name|get
argument_list|(
literal|"score"
argument_list|,
name|ScoreMode
operator|.
name|None
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|QParser
name|parentParser
init|=
name|subQuery
argument_list|(
name|filter
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Query
name|parentQ
init|=
name|parentParser
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|queryText
init|=
name|localParams
operator|.
name|get
argument_list|(
name|QueryParsing
operator|.
name|V
argument_list|)
decl_stmt|;
comment|// there is no child query, return parent filter from cache
if|if
condition|(
name|queryText
operator|==
literal|null
operator|||
name|queryText
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|SolrConstantScoreQuery
name|wrapped
init|=
operator|new
name|SolrConstantScoreQuery
argument_list|(
name|getFilter
argument_list|(
name|parentQ
argument_list|)
argument_list|)
decl_stmt|;
name|wrapped
operator|.
name|setCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|wrapped
return|;
block|}
name|QParser
name|childrenParser
init|=
name|subQuery
argument_list|(
name|queryText
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Query
name|childrenQuery
init|=
name|childrenParser
operator|.
name|getQuery
argument_list|()
decl_stmt|;
return|return
name|createQuery
argument_list|(
name|parentQ
argument_list|,
name|childrenQuery
argument_list|,
name|scoreMode
argument_list|)
return|;
block|}
DECL|method|createQuery
specifier|protected
name|Query
name|createQuery
parameter_list|(
specifier|final
name|Query
name|parentList
parameter_list|,
name|Query
name|query
parameter_list|,
name|String
name|scoreMode
parameter_list|)
throws|throws
name|SyntaxError
block|{
return|return
operator|new
name|AllParentsAware
argument_list|(
name|query
argument_list|,
name|getFilter
argument_list|(
name|parentList
argument_list|)
operator|.
name|filter
argument_list|,
name|ScoreModeParser
operator|.
name|parse
argument_list|(
name|scoreMode
argument_list|)
argument_list|,
name|parentList
argument_list|)
return|;
block|}
DECL|method|getFilter
name|BitDocIdSetFilterWrapper
name|getFilter
parameter_list|(
name|Query
name|parentList
parameter_list|)
block|{
name|SolrCache
name|parentCache
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getCache
argument_list|(
name|CACHE_NAME
argument_list|)
decl_stmt|;
comment|// lazily retrieve from solr cache
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentCache
operator|!=
literal|null
condition|)
block|{
name|filter
operator|=
operator|(
name|Filter
operator|)
name|parentCache
operator|.
name|get
argument_list|(
name|parentList
argument_list|)
expr_stmt|;
block|}
name|BitDocIdSetFilterWrapper
name|result
decl_stmt|;
if|if
condition|(
name|filter
operator|instanceof
name|BitDocIdSetFilterWrapper
condition|)
block|{
name|result
operator|=
operator|(
name|BitDocIdSetFilterWrapper
operator|)
name|filter
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|BitDocIdSetFilterWrapper
argument_list|(
name|createParentFilter
argument_list|(
name|parentList
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentCache
operator|!=
literal|null
condition|)
block|{
name|parentCache
operator|.
name|put
argument_list|(
name|parentList
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|createParentFilter
specifier|private
name|BitSetProducer
name|createParentFilter
parameter_list|(
name|Query
name|parentQ
parameter_list|)
block|{
return|return
operator|new
name|QueryBitSetProducer
argument_list|(
name|parentQ
argument_list|)
return|;
block|}
DECL|class|AllParentsAware
specifier|static
specifier|final
class|class
name|AllParentsAware
extends|extends
name|ToParentBlockJoinQuery
block|{
DECL|field|parentQuery
specifier|private
specifier|final
name|Query
name|parentQuery
decl_stmt|;
DECL|method|AllParentsAware
specifier|private
name|AllParentsAware
parameter_list|(
name|Query
name|childQuery
parameter_list|,
name|BitSetProducer
name|parentsFilter
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|,
name|Query
name|parentList
parameter_list|)
block|{
name|super
argument_list|(
name|childQuery
argument_list|,
name|parentsFilter
argument_list|,
name|scoreMode
argument_list|)
expr_stmt|;
name|parentQuery
operator|=
name|parentList
expr_stmt|;
block|}
DECL|method|getParentQuery
specifier|public
name|Query
name|getParentQuery
parameter_list|()
block|{
return|return
name|parentQuery
return|;
block|}
block|}
comment|// We need this wrapper since BitDocIdSetFilter does not extend Filter
DECL|class|BitDocIdSetFilterWrapper
specifier|static
class|class
name|BitDocIdSetFilterWrapper
extends|extends
name|Filter
block|{
DECL|field|filter
specifier|final
name|BitSetProducer
name|filter
decl_stmt|;
DECL|method|BitDocIdSetFilterWrapper
name|BitDocIdSetFilterWrapper
parameter_list|(
name|BitSetProducer
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
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
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
name|BitSet
name|set
init|=
name|filter
operator|.
name|getBitSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|BitsFilteredDocIdSet
operator|.
name|wrap
argument_list|(
operator|new
name|BitDocIdSet
argument_list|(
name|set
argument_list|)
argument_list|,
name|acceptDocs
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|filter
operator|+
literal|")"
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
name|Objects
operator|.
name|equals
argument_list|(
name|filter
argument_list|,
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|filter
argument_list|)
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
name|classHash
argument_list|()
operator|+
name|filter
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

