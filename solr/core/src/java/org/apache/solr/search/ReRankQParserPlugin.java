begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntFloatHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntIntHashMap
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
name|LeafCollector
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
name|QueryRescorer
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
name|Rescorer
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
name|TopDocsCollector
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
name|BytesRef
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
name|handler
operator|.
name|component
operator|.
name|MergeStrategy
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
name|QueryElevationComponent
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
name|request
operator|.
name|SolrRequestInfo
import|;
end_import

begin_comment
comment|/* * *  Syntax: q=*:*&rq={!rerank reRankQuery=$rqq reRankDocs=300 reRankWeight=3} * */
end_comment

begin_class
DECL|class|ReRankQParserPlugin
specifier|public
class|class
name|ReRankQParserPlugin
extends|extends
name|QParserPlugin
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"rerank"
decl_stmt|;
DECL|field|defaultQuery
specifier|private
specifier|static
name|Query
name|defaultQuery
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
DECL|field|RERANK_QUERY
specifier|public
specifier|static
specifier|final
name|String
name|RERANK_QUERY
init|=
literal|"reRankQuery"
decl_stmt|;
DECL|field|RERANK_DOCS
specifier|public
specifier|static
specifier|final
name|String
name|RERANK_DOCS
init|=
literal|"reRankDocs"
decl_stmt|;
DECL|field|RERANK_DOCS_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|RERANK_DOCS_DEFAULT
init|=
literal|200
decl_stmt|;
DECL|field|RERANK_WEIGHT
specifier|public
specifier|static
specifier|final
name|String
name|RERANK_WEIGHT
init|=
literal|"reRankWeight"
decl_stmt|;
DECL|field|RERANK_WEIGHT_DEFAULT
specifier|public
specifier|static
specifier|final
name|double
name|RERANK_WEIGHT_DEFAULT
init|=
literal|2.0d
decl_stmt|;
DECL|method|createParser
specifier|public
name|QParser
name|createParser
parameter_list|(
name|String
name|query
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
return|return
operator|new
name|ReRankQParser
argument_list|(
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
return|;
block|}
DECL|class|ReRankQParser
specifier|private
class|class
name|ReRankQParser
extends|extends
name|QParser
block|{
DECL|method|ReRankQParser
specifier|public
name|ReRankQParser
parameter_list|(
name|String
name|query
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
name|query
argument_list|,
name|localParams
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|()
throws|throws
name|SyntaxError
block|{
name|String
name|reRankQueryString
init|=
name|localParams
operator|.
name|get
argument_list|(
name|RERANK_QUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|reRankQueryString
operator|==
literal|null
operator|||
name|reRankQueryString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
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
name|RERANK_QUERY
operator|+
literal|" parameter is mandatory"
argument_list|)
throw|;
block|}
name|QParser
name|reRankParser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|reRankQueryString
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|Query
name|reRankQuery
init|=
name|reRankParser
operator|.
name|parse
argument_list|()
decl_stmt|;
name|int
name|reRankDocs
init|=
name|localParams
operator|.
name|getInt
argument_list|(
name|RERANK_DOCS
argument_list|,
name|RERANK_DOCS_DEFAULT
argument_list|)
decl_stmt|;
name|reRankDocs
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|reRankDocs
argument_list|)
expr_stmt|;
comment|//
name|double
name|reRankWeight
init|=
name|localParams
operator|.
name|getDouble
argument_list|(
name|RERANK_WEIGHT
argument_list|,
name|RERANK_WEIGHT_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReRankQuery
argument_list|(
name|reRankQuery
argument_list|,
name|reRankDocs
argument_list|,
name|reRankWeight
argument_list|)
return|;
block|}
block|}
DECL|class|ReRankQueryRescorer
specifier|private
specifier|final
class|class
name|ReRankQueryRescorer
extends|extends
name|QueryRescorer
block|{
DECL|field|reRankWeight
specifier|final
name|double
name|reRankWeight
decl_stmt|;
DECL|method|ReRankQueryRescorer
specifier|public
name|ReRankQueryRescorer
parameter_list|(
name|Query
name|reRankQuery
parameter_list|,
name|double
name|reRankWeight
parameter_list|)
block|{
name|super
argument_list|(
name|reRankQuery
argument_list|)
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|combine
specifier|protected
name|float
name|combine
parameter_list|(
name|float
name|firstPassScore
parameter_list|,
name|boolean
name|secondPassMatches
parameter_list|,
name|float
name|secondPassScore
parameter_list|)
block|{
name|float
name|score
init|=
name|firstPassScore
decl_stmt|;
if|if
condition|(
name|secondPassMatches
condition|)
block|{
name|score
operator|+=
name|reRankWeight
operator|*
name|secondPassScore
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
block|}
DECL|class|ReRankQuery
specifier|private
specifier|final
class|class
name|ReRankQuery
extends|extends
name|RankQuery
block|{
DECL|field|mainQuery
specifier|private
name|Query
name|mainQuery
init|=
name|defaultQuery
decl_stmt|;
DECL|field|reRankQuery
specifier|final
specifier|private
name|Query
name|reRankQuery
decl_stmt|;
DECL|field|reRankDocs
specifier|final
specifier|private
name|int
name|reRankDocs
decl_stmt|;
DECL|field|reRankWeight
specifier|final
specifier|private
name|double
name|reRankWeight
decl_stmt|;
DECL|field|reRankQueryRescorer
specifier|final
specifier|private
name|Rescorer
name|reRankQueryRescorer
decl_stmt|;
DECL|field|boostedPriority
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
decl_stmt|;
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
name|mainQuery
operator|.
name|hashCode
argument_list|()
operator|+
name|reRankQuery
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|int
operator|)
name|reRankWeight
operator|+
name|reRankDocs
return|;
block|}
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
name|equalsTo
argument_list|(
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
argument_list|)
return|;
block|}
DECL|method|equalsTo
specifier|private
name|boolean
name|equalsTo
parameter_list|(
name|ReRankQuery
name|rrq
parameter_list|)
block|{
return|return
name|mainQuery
operator|.
name|equals
argument_list|(
name|rrq
operator|.
name|mainQuery
argument_list|)
operator|&&
name|reRankQuery
operator|.
name|equals
argument_list|(
name|rrq
operator|.
name|reRankQuery
argument_list|)
operator|&&
name|reRankWeight
operator|==
name|rrq
operator|.
name|reRankWeight
operator|&&
name|reRankDocs
operator|==
name|rrq
operator|.
name|reRankDocs
return|;
block|}
DECL|method|ReRankQuery
specifier|public
name|ReRankQuery
parameter_list|(
name|Query
name|reRankQuery
parameter_list|,
name|int
name|reRankDocs
parameter_list|,
name|double
name|reRankWeight
parameter_list|)
block|{
name|this
operator|.
name|reRankQuery
operator|=
name|reRankQuery
expr_stmt|;
name|this
operator|.
name|reRankDocs
operator|=
name|reRankDocs
expr_stmt|;
name|this
operator|.
name|reRankWeight
operator|=
name|reRankWeight
expr_stmt|;
name|this
operator|.
name|reRankQueryRescorer
operator|=
operator|new
name|ReRankQueryRescorer
argument_list|(
name|reRankQuery
argument_list|,
name|reRankWeight
argument_list|)
expr_stmt|;
block|}
DECL|method|wrap
specifier|public
name|RankQuery
name|wrap
parameter_list|(
name|Query
name|_mainQuery
parameter_list|)
block|{
if|if
condition|(
name|_mainQuery
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|mainQuery
operator|=
name|_mainQuery
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|getMergeStrategy
specifier|public
name|MergeStrategy
name|getMergeStrategy
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getTopDocsCollector
specifier|public
name|TopDocsCollector
name|getTopDocsCollector
parameter_list|(
name|int
name|len
parameter_list|,
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|boostedPriority
operator|==
literal|null
condition|)
block|{
name|SolrRequestInfo
name|info
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|Map
name|context
init|=
name|info
operator|.
name|getReq
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|this
operator|.
name|boostedPriority
operator|=
operator|(
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
operator|)
name|context
operator|.
name|get
argument_list|(
name|QueryElevationComponent
operator|.
name|BOOSTED_PRIORITY
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ReRankCollector
argument_list|(
name|reRankDocs
argument_list|,
name|len
argument_list|,
name|reRankQueryRescorer
argument_list|,
name|cmd
argument_list|,
name|searcher
argument_list|,
name|boostedPriority
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
name|s
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// default initialCapacity of 16 won't be enough
name|sb
operator|.
name|append
argument_list|(
literal|"{!"
argument_list|)
operator|.
name|append
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" mainQuery='"
argument_list|)
operator|.
name|append
argument_list|(
name|mainQuery
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RERANK_QUERY
argument_list|)
operator|.
name|append
argument_list|(
literal|"='"
argument_list|)
operator|.
name|append
argument_list|(
name|reRankQuery
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RERANK_DOCS
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|reRankDocs
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|RERANK_WEIGHT
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|reRankWeight
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|q
init|=
name|mainQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|!=
name|mainQuery
condition|)
block|{
return|return
operator|new
name|ReRankQuery
argument_list|(
name|reRankQuery
argument_list|,
name|reRankDocs
argument_list|,
name|reRankWeight
argument_list|)
operator|.
name|wrap
argument_list|(
name|q
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
return|;
block|}
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
specifier|final
name|Weight
name|mainWeight
init|=
name|mainQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|,
name|needsScores
argument_list|,
name|boost
argument_list|)
decl_stmt|;
return|return
operator|new
name|ReRankWeight
argument_list|(
name|mainQuery
argument_list|,
name|reRankQueryRescorer
argument_list|,
name|searcher
argument_list|,
name|mainWeight
argument_list|)
return|;
block|}
block|}
DECL|class|ReRankCollector
specifier|private
class|class
name|ReRankCollector
extends|extends
name|TopDocsCollector
block|{
DECL|field|mainCollector
specifier|final
specifier|private
name|TopDocsCollector
name|mainCollector
decl_stmt|;
DECL|field|searcher
specifier|final
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reRankDocs
specifier|final
specifier|private
name|int
name|reRankDocs
decl_stmt|;
DECL|field|length
specifier|final
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|boostedPriority
specifier|final
specifier|private
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
decl_stmt|;
DECL|field|reRankQueryRescorer
specifier|final
specifier|private
name|Rescorer
name|reRankQueryRescorer
decl_stmt|;
DECL|method|ReRankCollector
specifier|public
name|ReRankCollector
parameter_list|(
name|int
name|reRankDocs
parameter_list|,
name|int
name|length
parameter_list|,
name|Rescorer
name|reRankQueryRescorer
parameter_list|,
name|QueryCommand
name|cmd
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boostedPriority
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|reRankDocs
operator|=
name|reRankDocs
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|boostedPriority
operator|=
name|boostedPriority
expr_stmt|;
name|Sort
name|sort
init|=
name|cmd
operator|.
name|getSort
argument_list|()
decl_stmt|;
if|if
condition|(
name|sort
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|mainCollector
operator|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sort
operator|=
name|sort
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|mainCollector
operator|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|this
operator|.
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|searcher
operator|=
name|searcher
expr_stmt|;
name|this
operator|.
name|reRankQueryRescorer
operator|=
name|reRankQueryRescorer
expr_stmt|;
block|}
DECL|method|getTotalHits
specifier|public
name|int
name|getTotalHits
parameter_list|()
block|{
return|return
name|mainCollector
operator|.
name|getTotalHits
argument_list|()
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
return|return
name|mainCollector
operator|.
name|getLeafCollector
argument_list|(
name|context
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
DECL|method|topDocs
specifier|public
name|TopDocs
name|topDocs
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|howMany
parameter_list|)
block|{
try|try
block|{
name|TopDocs
name|mainDocs
init|=
name|mainCollector
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|max
argument_list|(
name|reRankDocs
argument_list|,
name|length
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|mainDocs
operator|.
name|totalHits
operator|==
literal|0
operator|||
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|mainDocs
return|;
block|}
name|ScoreDoc
index|[]
name|mainScoreDocs
init|=
name|mainDocs
operator|.
name|scoreDocs
decl_stmt|;
name|ScoreDoc
index|[]
name|reRankScoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|Math
operator|.
name|min
argument_list|(
name|mainScoreDocs
operator|.
name|length
argument_list|,
name|reRankDocs
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
argument_list|,
literal|0
argument_list|,
name|reRankScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|mainDocs
operator|.
name|scoreDocs
operator|=
name|reRankScoreDocs
expr_stmt|;
name|TopDocs
name|rescoredDocs
init|=
name|reRankQueryRescorer
operator|.
name|rescore
argument_list|(
name|searcher
argument_list|,
name|mainDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
decl_stmt|;
comment|//Lower howMany to return if we've collected fewer documents.
name|howMany
operator|=
name|Math
operator|.
name|min
argument_list|(
name|howMany
argument_list|,
name|mainScoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|boostedPriority
operator|!=
literal|null
condition|)
block|{
name|SolrRequestInfo
name|info
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|Map
name|requestContext
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|requestContext
operator|=
name|info
operator|.
name|getReq
argument_list|()
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
name|IntIntHashMap
name|boostedDocs
init|=
name|QueryElevationComponent
operator|.
name|getBoostDocs
argument_list|(
operator|(
name|SolrIndexSearcher
operator|)
name|searcher
argument_list|,
name|boostedPriority
argument_list|,
name|requestContext
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
operator|new
name|BoostedComp
argument_list|(
name|boostedDocs
argument_list|,
name|mainDocs
operator|.
name|scoreDocs
argument_list|,
name|rescoredDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|howMany
operator|==
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
return|return
name|rescoredDocs
return|;
comment|// Just return the rescoredDocs
block|}
elseif|else
if|if
condition|(
name|howMany
operator|>
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
comment|//We need to return more then we've reRanked, so create the combined page.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mainScoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//lay down the initial docs
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|rescoredDocs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//overlay the re-ranked docs.
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
else|else
block|{
comment|//We've rescored more then we need to return.
name|ScoreDoc
index|[]
name|scoreDocs
init|=
operator|new
name|ScoreDoc
index|[
name|howMany
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|rescoredDocs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|howMany
argument_list|)
expr_stmt|;
name|rescoredDocs
operator|.
name|scoreDocs
operator|=
name|scoreDocs
expr_stmt|;
return|return
name|rescoredDocs
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|BoostedComp
specifier|public
class|class
name|BoostedComp
implements|implements
name|Comparator
block|{
DECL|field|boostedMap
name|IntFloatHashMap
name|boostedMap
decl_stmt|;
DECL|method|BoostedComp
specifier|public
name|BoostedComp
parameter_list|(
name|IntIntHashMap
name|boostedDocs
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|boostedMap
operator|=
operator|new
name|IntFloatHashMap
argument_list|(
name|boostedDocs
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
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
name|scoreDocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|idx
decl_stmt|;
if|if
condition|(
operator|(
name|idx
operator|=
name|boostedDocs
operator|.
name|indexOf
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|boostedMap
operator|.
name|put
argument_list|(
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
argument_list|,
name|maxScore
operator|+
name|boostedDocs
operator|.
name|indexGet
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|ScoreDoc
name|doc1
init|=
operator|(
name|ScoreDoc
operator|)
name|o1
decl_stmt|;
name|ScoreDoc
name|doc2
init|=
operator|(
name|ScoreDoc
operator|)
name|o2
decl_stmt|;
name|float
name|score1
init|=
name|doc1
operator|.
name|score
decl_stmt|;
name|float
name|score2
init|=
name|doc2
operator|.
name|score
decl_stmt|;
name|int
name|idx
decl_stmt|;
if|if
condition|(
operator|(
name|idx
operator|=
name|boostedMap
operator|.
name|indexOf
argument_list|(
name|doc1
operator|.
name|doc
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|score1
operator|=
name|boostedMap
operator|.
name|indexGet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|idx
operator|=
name|boostedMap
operator|.
name|indexOf
argument_list|(
name|doc2
operator|.
name|doc
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|score2
operator|=
name|boostedMap
operator|.
name|indexGet
argument_list|(
name|idx
argument_list|)
expr_stmt|;
block|}
return|return
operator|-
name|Float
operator|.
name|compare
argument_list|(
name|score1
argument_list|,
name|score2
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

