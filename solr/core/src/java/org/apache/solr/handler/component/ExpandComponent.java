begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntObjectMap
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
name|IntObjectOpenHashMap
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
name|IntOpenHashSet
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
name|cursors
operator|.
name|IntObjectCursor
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
name|cursors
operator|.
name|ObjectCursor
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
name|AtomicReader
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
name|AtomicReaderContext
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
name|DocValues
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
name|SortedDocValues
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
name|lucene
operator|.
name|util
operator|.
name|CharsRef
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
name|CharsRefBuilder
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
name|FixedBitSet
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
name|SolrDocumentList
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
name|ExpandParams
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
name|ShardParams
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
name|core
operator|.
name|PluginInfo
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
name|core
operator|.
name|SolrCore
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
name|schema
operator|.
name|FieldType
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
name|CollapsingQParserPlugin
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
name|DocIterator
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
name|DocList
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
name|DocSlice
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
name|SolrIndexSearcher
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
name|util
operator|.
name|plugin
operator|.
name|PluginInfoInitialized
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|HashMap
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

begin_comment
comment|/**  * The ExpandComponent is designed to work with the CollapsingPostFilter.  * The CollapsingPostFilter collapses a result set on a field.  *<p/>  * The ExpandComponent expands the collapsed groups for a single page.  *<p/>  * http parameters:  *<p/>  * expand=true<br/>  * expand.rows=5<br/>  * expand.sort=field asc|desc<br/>  * expand.q=*:* (optional, overrides the main query)<br/>  * expand.fq=type:child (optional, overrides the main filter queries)<br/>  * expand.field=field (mandatory if the not used with the CollapsingQParserPlugin)<br/>  */
end_comment

begin_class
DECL|class|ExpandComponent
specifier|public
class|class
name|ExpandComponent
extends|extends
name|SearchComponent
implements|implements
name|PluginInfoInitialized
implements|,
name|SolrCoreAware
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"expand"
decl_stmt|;
DECL|field|info
specifier|private
name|PluginInfo
name|info
init|=
name|PluginInfo
operator|.
name|EMPTY_INFO
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
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
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|ExpandParams
operator|.
name|EXPAND
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|rb
operator|.
name|doExpand
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{    }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
if|if
condition|(
operator|!
name|rb
operator|.
name|doExpand
condition|)
block|{
return|return;
block|}
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|boolean
name|isShard
init|=
name|params
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|ids
init|=
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|IDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
operator|&&
name|isShard
condition|)
block|{
return|return;
block|}
name|String
name|field
init|=
name|params
operator|.
name|get
argument_list|(
name|ExpandParams
operator|.
name|EXPAND_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|filters
init|=
name|rb
operator|.
name|getFilters
argument_list|()
decl_stmt|;
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Query
name|q
range|:
name|filters
control|)
block|{
if|if
condition|(
name|q
operator|instanceof
name|CollapsingQParserPlugin
operator|.
name|CollapsingPostFilter
condition|)
block|{
name|CollapsingQParserPlugin
operator|.
name|CollapsingPostFilter
name|cp
init|=
operator|(
name|CollapsingQParserPlugin
operator|.
name|CollapsingPostFilter
operator|)
name|q
decl_stmt|;
name|field
operator|=
name|cp
operator|.
name|getField
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expand field is null."
argument_list|)
throw|;
block|}
name|String
name|sortParam
init|=
name|params
operator|.
name|get
argument_list|(
name|ExpandParams
operator|.
name|EXPAND_SORT
argument_list|)
decl_stmt|;
name|String
index|[]
name|fqs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|ExpandParams
operator|.
name|EXPAND_FQ
argument_list|)
decl_stmt|;
name|String
name|qs
init|=
name|params
operator|.
name|get
argument_list|(
name|ExpandParams
operator|.
name|EXPAND_Q
argument_list|)
decl_stmt|;
name|int
name|limit
init|=
name|params
operator|.
name|getInt
argument_list|(
name|ExpandParams
operator|.
name|EXPAND_ROWS
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Sort
name|sort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sortParam
operator|!=
literal|null
condition|)
block|{
name|sort
operator|=
name|QueryParsing
operator|.
name|parseSortSpec
argument_list|(
name|sortParam
argument_list|,
name|rb
operator|.
name|req
argument_list|)
operator|.
name|getSort
argument_list|()
expr_stmt|;
block|}
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|qs
operator|==
literal|null
condition|)
block|{
name|query
operator|=
name|rb
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|QParser
name|parser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|qs
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|query
operator|=
name|parser
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|List
argument_list|<
name|Query
argument_list|>
name|newFilters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|fqs
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|filters
init|=
name|rb
operator|.
name|getFilters
argument_list|()
decl_stmt|;
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Query
name|q
range|:
name|filters
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|q
operator|instanceof
name|CollapsingQParserPlugin
operator|.
name|CollapsingPostFilter
operator|)
condition|)
block|{
name|newFilters
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
try|try
block|{
for|for
control|(
name|String
name|fq
range|:
name|fqs
control|)
block|{
if|if
condition|(
name|fq
operator|!=
literal|null
operator|&&
name|fq
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
name|fq
operator|.
name|equals
argument_list|(
literal|"*:*"
argument_list|)
condition|)
block|{
name|QParser
name|fqp
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|fq
argument_list|,
literal|null
argument_list|,
name|req
argument_list|)
decl_stmt|;
name|newFilters
operator|.
name|add
argument_list|(
name|fqp
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|SolrIndexSearcher
name|searcher
init|=
name|req
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|AtomicReader
name|reader
init|=
name|searcher
operator|.
name|getAtomicReader
argument_list|()
decl_stmt|;
name|SortedDocValues
name|values
init|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
name|FixedBitSet
name|groupBits
init|=
operator|new
name|FixedBitSet
argument_list|(
name|values
operator|.
name|getValueCount
argument_list|()
argument_list|)
decl_stmt|;
name|DocList
name|docList
init|=
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
decl_stmt|;
name|IntOpenHashSet
name|collapsedSet
init|=
operator|new
name|IntOpenHashSet
argument_list|(
name|docList
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
name|DocIterator
name|idit
init|=
name|docList
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|idit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|doc
init|=
name|idit
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|ord
init|=
name|values
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>
operator|-
literal|1
condition|)
block|{
name|groupBits
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|collapsedSet
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|Collector
name|collector
decl_stmt|;
if|if
condition|(
name|sort
operator|!=
literal|null
condition|)
name|sort
operator|=
name|sort
operator|.
name|rewrite
argument_list|(
name|searcher
argument_list|)
expr_stmt|;
name|GroupExpandCollector
name|groupExpandCollector
init|=
operator|new
name|GroupExpandCollector
argument_list|(
name|values
argument_list|,
name|groupBits
argument_list|,
name|collapsedSet
argument_list|,
name|limit
argument_list|,
name|sort
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
operator|.
name|ProcessedFilter
name|pfilter
init|=
name|searcher
operator|.
name|getProcessedFilter
argument_list|(
literal|null
argument_list|,
name|newFilters
argument_list|)
decl_stmt|;
if|if
condition|(
name|pfilter
operator|.
name|postFilter
operator|!=
literal|null
condition|)
block|{
name|pfilter
operator|.
name|postFilter
operator|.
name|setLastDelegate
argument_list|(
name|groupExpandCollector
argument_list|)
expr_stmt|;
name|collector
operator|=
name|pfilter
operator|.
name|postFilter
expr_stmt|;
block|}
else|else
block|{
name|collector
operator|=
name|groupExpandCollector
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|pfilter
operator|.
name|filter
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|IntObjectMap
name|groups
init|=
name|groupExpandCollector
operator|.
name|getGroups
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DocSlice
argument_list|>
name|outMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|CharsRefBuilder
name|charsRef
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|searcher
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|field
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
for|for
control|(
name|IntObjectCursor
name|cursor
range|:
operator|(
name|Iterable
argument_list|<
name|IntObjectCursor
argument_list|>
operator|)
name|groups
control|)
block|{
name|int
name|ord
init|=
name|cursor
operator|.
name|key
decl_stmt|;
name|TopDocsCollector
name|topDocsCollector
init|=
operator|(
name|TopDocsCollector
operator|)
name|cursor
operator|.
name|value
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|topDocsCollector
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
if|if
condition|(
name|scoreDocs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|scoreDocs
operator|.
name|length
index|]
decl_stmt|;
name|float
index|[]
name|scores
init|=
operator|new
name|float
index|[
name|scoreDocs
operator|.
name|length
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|i
index|]
decl_stmt|;
name|docs
index|[
name|i
index|]
operator|=
name|scoreDoc
operator|.
name|doc
expr_stmt|;
name|scores
index|[
name|i
index|]
operator|=
name|scoreDoc
operator|.
name|score
expr_stmt|;
block|}
name|DocSlice
name|slice
init|=
operator|new
name|DocSlice
argument_list|(
literal|0
argument_list|,
name|docs
operator|.
name|length
argument_list|,
name|docs
argument_list|,
name|scores
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|,
name|topDocs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BytesRef
name|bytesRef
init|=
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|indexedToReadable
argument_list|(
name|bytesRef
argument_list|,
name|charsRef
argument_list|)
expr_stmt|;
name|String
name|group
init|=
name|charsRef
operator|.
name|toString
argument_list|()
decl_stmt|;
name|outMap
operator|.
name|put
argument_list|(
name|group
argument_list|,
name|slice
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"expanded"
argument_list|,
name|outMap
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|modifyRequest
specifier|public
name|void
name|modifyRequest
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|SearchComponent
name|who
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{    }
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|handleResponses
specifier|public
name|void
name|handleResponses
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|sreq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doExpand
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_FIELDS
operator|)
operator|!=
literal|0
condition|)
block|{
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
name|Map
name|expanded
init|=
operator|(
name|Map
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"expanded"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expanded
operator|==
literal|null
condition|)
block|{
name|expanded
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
literal|"expanded"
argument_list|,
name|expanded
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ShardResponse
name|srsp
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|NamedList
name|response
init|=
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|Map
name|ex
init|=
operator|(
name|Map
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"expanded"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrDocumentList
argument_list|>
name|entry
range|:
operator|(
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrDocumentList
argument_list|>
argument_list|>
operator|)
name|ex
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|expanded
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|finishStage
specifier|public
name|void
name|finishStage
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|doExpand
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|rb
operator|.
name|stage
operator|!=
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
return|return;
block|}
name|Map
name|expanded
init|=
operator|(
name|Map
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
literal|"expanded"
argument_list|)
decl_stmt|;
if|if
condition|(
name|expanded
operator|==
literal|null
condition|)
block|{
name|expanded
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
block|}
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"expanded"
argument_list|,
name|expanded
argument_list|)
expr_stmt|;
block|}
DECL|class|GroupExpandCollector
specifier|private
class|class
name|GroupExpandCollector
implements|implements
name|Collector
block|{
DECL|field|docValues
specifier|private
name|SortedDocValues
name|docValues
decl_stmt|;
DECL|field|groups
specifier|private
name|IntObjectMap
argument_list|<
name|Collector
argument_list|>
name|groups
decl_stmt|;
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
DECL|field|groupBits
specifier|private
name|FixedBitSet
name|groupBits
decl_stmt|;
DECL|field|collapsedSet
specifier|private
name|IntOpenHashSet
name|collapsedSet
decl_stmt|;
DECL|method|GroupExpandCollector
specifier|public
name|GroupExpandCollector
parameter_list|(
name|SortedDocValues
name|docValues
parameter_list|,
name|FixedBitSet
name|groupBits
parameter_list|,
name|IntOpenHashSet
name|collapsedSet
parameter_list|,
name|int
name|limit
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numGroups
init|=
name|collapsedSet
operator|.
name|size
argument_list|()
decl_stmt|;
name|groups
operator|=
operator|new
name|IntObjectOpenHashMap
argument_list|<>
argument_list|(
name|numGroups
operator|*
literal|2
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iterator
init|=
name|groupBits
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|int
name|group
decl_stmt|;
while|while
condition|(
operator|(
name|group
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|Collector
name|collector
init|=
operator|(
name|sort
operator|==
literal|null
operator|)
condition|?
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|limit
argument_list|,
literal|true
argument_list|)
else|:
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|limit
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|group
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|collapsedSet
operator|=
name|collapsedSet
expr_stmt|;
name|this
operator|.
name|groupBits
operator|=
name|groupBits
expr_stmt|;
name|this
operator|.
name|docValues
operator|=
name|docValues
expr_stmt|;
block|}
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|AtomicReaderContext
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
name|IntObjectMap
argument_list|<
name|LeafCollector
argument_list|>
name|leafCollectors
init|=
operator|new
name|IntObjectOpenHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IntObjectCursor
argument_list|<
name|Collector
argument_list|>
name|entry
range|:
name|groups
control|)
block|{
name|leafCollectors
operator|.
name|put
argument_list|(
name|entry
operator|.
name|key
argument_list|,
name|entry
operator|.
name|value
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LeafCollector
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
for|for
control|(
name|ObjectCursor
argument_list|<
name|LeafCollector
argument_list|>
name|c
range|:
name|leafCollectors
operator|.
name|values
argument_list|()
control|)
block|{
name|c
operator|.
name|value
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|doc
init|=
name|docId
operator|+
name|docBase
decl_stmt|;
name|int
name|ord
init|=
name|docValues
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>
operator|-
literal|1
operator|&&
name|groupBits
operator|.
name|get
argument_list|(
name|ord
argument_list|)
operator|&&
operator|!
name|collapsedSet
operator|.
name|contains
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|LeafCollector
name|c
init|=
name|leafCollectors
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|c
operator|.
name|collect
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
DECL|method|getGroups
specifier|public
name|IntObjectMap
argument_list|<
name|Collector
argument_list|>
name|getGroups
parameter_list|()
block|{
return|return
name|groups
return|;
block|}
block|}
comment|////////////////////////////////////////////
comment|///  SolrInfoMBean
comment|////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Expand Component"
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|URL
index|[]
block|{
operator|new
name|URL
argument_list|(
literal|"http://wiki.apache.org/solr/ExpandComponent"
argument_list|)
block|}
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
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
block|}
end_class

end_unit

