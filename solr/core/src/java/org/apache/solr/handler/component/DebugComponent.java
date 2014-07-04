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
name|CommonParams
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
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|util
operator|.
name|SolrPluginUtils
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
name|Arrays
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import static
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
name|CommonParams
operator|.
name|FQ
import|;
end_import

begin_comment
comment|/**  * Adds debugging information to a request.  *   *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|DebugComponent
specifier|public
class|class
name|DebugComponent
extends|extends
name|SearchComponent
block|{
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"debug"
decl_stmt|;
comment|/**    * A counter to ensure that no RID is equal, even if they fall in the same millisecond    */
DECL|field|ridCounter
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|ridCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|/**    * Map containing all the possible stages as key and    * the corresponding readable purpose as value    */
DECL|field|stages
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|stages
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ResponseBuilder
operator|.
name|STAGE_START
argument_list|,
literal|"START"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ResponseBuilder
operator|.
name|STAGE_PARSE_QUERY
argument_list|,
literal|"PARSE_QUERY"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ResponseBuilder
operator|.
name|STAGE_TOP_GROUPS
argument_list|,
literal|"TOP_GROUPS"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ResponseBuilder
operator|.
name|STAGE_EXECUTE_QUERY
argument_list|,
literal|"EXECUTE_QUERY"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
argument_list|,
literal|"GET_FIELDS"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|ResponseBuilder
operator|.
name|STAGE_DONE
argument_list|,
literal|"DONE"
argument_list|)
expr_stmt|;
name|stages
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
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
name|isDebugTrack
argument_list|()
operator|&&
name|rb
operator|.
name|isDistrib
condition|)
block|{
name|doDebugTrack
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
block|}
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
name|rb
operator|.
name|isDebug
argument_list|()
condition|)
block|{
name|DocList
name|results
init|=
literal|null
decl_stmt|;
comment|//some internal grouping requests won't have results value set
if|if
condition|(
name|rb
operator|.
name|getResults
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|results
operator|=
name|rb
operator|.
name|getResults
argument_list|()
operator|.
name|docList
expr_stmt|;
block|}
name|NamedList
name|stdinfo
init|=
name|SolrPluginUtils
operator|.
name|doStandardDebug
argument_list|(
name|rb
operator|.
name|req
argument_list|,
name|rb
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|rb
operator|.
name|wrap
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
argument_list|,
name|results
argument_list|,
name|rb
operator|.
name|isDebugQuery
argument_list|()
argument_list|,
name|rb
operator|.
name|isDebugResults
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
name|info
init|=
name|rb
operator|.
name|getDebugInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|rb
operator|.
name|setDebugInfo
argument_list|(
name|stdinfo
argument_list|)
expr_stmt|;
name|info
operator|=
name|stdinfo
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|addAll
argument_list|(
name|stdinfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
operator|&&
name|rb
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|getQparser
argument_list|()
operator|.
name|addDebugInfo
argument_list|(
name|rb
operator|.
name|getDebugInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|rb
operator|.
name|getDebugInfo
argument_list|()
condition|)
block|{
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
operator|&&
literal|null
operator|!=
name|rb
operator|.
name|getFilters
argument_list|()
condition|)
block|{
name|info
operator|.
name|add
argument_list|(
literal|"filter_queries"
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|FQ
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|rb
operator|.
name|getFilters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Query
name|fq
range|:
name|rb
operator|.
name|getFilters
argument_list|()
control|)
block|{
name|fqs
operator|.
name|add
argument_list|(
name|QueryParsing
operator|.
name|toString
argument_list|(
name|fq
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getSchema
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|add
argument_list|(
literal|"parsed_filter_queries"
argument_list|,
name|fqs
argument_list|)
expr_stmt|;
block|}
comment|// Add this directly here?
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|rb
operator|.
name|getDebugInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|doDebugTrack
specifier|private
name|void
name|doDebugTrack
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
name|String
name|rid
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|rid
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|rid
argument_list|)
condition|)
block|{
name|rid
operator|=
name|generateRid
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|,
name|rid
argument_list|)
expr_stmt|;
comment|//add rid to the request so that shards see it
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|addDebug
argument_list|(
name|rid
argument_list|,
literal|"track"
argument_list|,
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
expr_stmt|;
comment|//to see it in the response
name|rb
operator|.
name|rsp
operator|.
name|addToLog
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|,
name|rid
argument_list|)
expr_stmt|;
comment|//to see it in the logs of the landing core
block|}
DECL|method|generateRid
specifier|private
name|String
name|generateRid
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|String
name|hostName
init|=
name|rb
operator|.
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getHostName
argument_list|()
decl_stmt|;
return|return
name|hostName
operator|+
literal|"-"
operator|+
name|rb
operator|.
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"-"
operator|+
name|ridCounter
operator|.
name|getAndIncrement
argument_list|()
return|;
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
block|{
if|if
condition|(
operator|!
name|rb
operator|.
name|isDebug
argument_list|()
condition|)
return|return;
comment|// Turn on debug to get explain only when retrieving fields
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
name|sreq
operator|.
name|purpose
operator||=
name|ShardRequest
operator|.
name|PURPOSE_GET_DEBUG
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDebugAll
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|QUERY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|RESULTS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugTimings
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|TIMING
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugTrack
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
name|CommonParams
operator|.
name|TRACK
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|,
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_ID
argument_list|)
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|REQUEST_PURPOSE
argument_list|,
name|SolrPluginUtils
operator|.
name|getRequestPurpose
argument_list|(
name|sreq
operator|.
name|purpose
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|rb
operator|.
name|isDebugTrack
argument_list|()
operator|&&
name|rb
operator|.
name|isDistrib
operator|&&
operator|!
name|rb
operator|.
name|finished
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|stageList
init|=
call|(
name|NamedList
argument_list|<
name|Object
argument_list|>
call|)
argument_list|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|rb
operator|.
name|getDebugInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"track"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|stages
operator|.
name|get
argument_list|(
name|rb
operator|.
name|stage
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|stageList
operator|==
literal|null
condition|)
block|{
name|stageList
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
name|rb
operator|.
name|addDebug
argument_list|(
name|stageList
argument_list|,
literal|"track"
argument_list|,
name|stages
operator|.
name|get
argument_list|(
name|rb
operator|.
name|stage
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ShardResponse
name|response
range|:
name|sreq
operator|.
name|responses
control|)
block|{
name|stageList
operator|.
name|add
argument_list|(
name|response
operator|.
name|getShard
argument_list|()
argument_list|,
name|getTrackResponse
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|EXCLUDE_SET
specifier|private
specifier|final
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|EXCLUDE_SET
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"explain"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
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
name|rb
operator|.
name|isDebug
argument_list|()
operator|&&
name|rb
operator|.
name|stage
operator|==
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|info
init|=
name|rb
operator|.
name|getDebugInfo
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|explain
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
index|[]
name|arr
init|=
operator|new
name|NamedList
operator|.
name|NamedListEntry
index|[
name|rb
operator|.
name|resultIds
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// Will be set to true if there is at least one response with PURPOSE_GET_DEBUG
name|boolean
name|hasGetDebugResponses
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ShardRequest
name|sreq
range|:
name|rb
operator|.
name|finished
control|)
block|{
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
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// can't expect the debug content if there was an exception for this request
comment|// this should only happen when using shards.tolerant=true
continue|continue;
block|}
name|NamedList
name|sdebug
init|=
operator|(
name|NamedList
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"debug"
argument_list|)
decl_stmt|;
name|info
operator|=
operator|(
name|NamedList
operator|)
name|merge
argument_list|(
name|sdebug
argument_list|,
name|info
argument_list|,
name|EXCLUDE_SET
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|sreq
operator|.
name|purpose
operator|&
name|ShardRequest
operator|.
name|PURPOSE_GET_DEBUG
operator|)
operator|!=
literal|0
condition|)
block|{
name|hasGetDebugResponses
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|NamedList
name|sexplain
init|=
operator|(
name|NamedList
operator|)
name|sdebug
operator|.
name|get
argument_list|(
literal|"explain"
argument_list|)
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
name|sexplain
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|sexplain
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// TODO: lookup won't work for non-string ids... String vs Float
name|ShardDoc
name|sdoc
init|=
name|rb
operator|.
name|resultIds
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|sdoc
operator|.
name|positionInResponse
decl_stmt|;
name|arr
index|[
name|idx
index|]
operator|=
operator|new
name|NamedList
operator|.
name|NamedListEntry
argument_list|<>
argument_list|(
name|id
argument_list|,
name|sexplain
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|explain
operator|=
name|SolrPluginUtils
operator|.
name|removeNulls
argument_list|(
name|arr
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hasGetDebugResponses
condition|)
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|// No responses were received from shards. Show local query info.
name|SolrPluginUtils
operator|.
name|doStandardQueryDebug
argument_list|(
name|rb
operator|.
name|req
argument_list|,
name|rb
operator|.
name|getQueryString
argument_list|()
argument_list|,
name|rb
operator|.
name|wrap
argument_list|(
name|rb
operator|.
name|getQuery
argument_list|()
argument_list|)
argument_list|,
name|rb
operator|.
name|isDebugQuery
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDebugQuery
argument_list|()
operator|&&
name|rb
operator|.
name|getQparser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|getQparser
argument_list|()
operator|.
name|addDebugInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|rb
operator|.
name|isDebugResults
argument_list|()
condition|)
block|{
name|int
name|idx
init|=
name|info
operator|.
name|indexOf
argument_list|(
literal|"explain"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|info
operator|.
name|setVal
argument_list|(
name|idx
argument_list|,
name|explain
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|add
argument_list|(
literal|"explain"
argument_list|,
name|explain
argument_list|)
expr_stmt|;
block|}
block|}
name|rb
operator|.
name|setDebugInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|rb
operator|.
name|rsp
operator|.
name|add
argument_list|(
literal|"debug"
argument_list|,
name|rb
operator|.
name|getDebugInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTrackResponse
specifier|private
name|NamedList
argument_list|<
name|String
argument_list|>
name|getTrackResponse
parameter_list|(
name|ShardResponse
name|shardResponse
parameter_list|)
block|{
name|NamedList
argument_list|<
name|String
argument_list|>
name|namedList
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardResponse
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|namedList
operator|.
name|add
argument_list|(
literal|"Exception"
argument_list|,
name|shardResponse
operator|.
name|getException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|namedList
return|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|responseNL
init|=
name|shardResponse
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|responseHeader
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|responseNL
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
decl_stmt|;
if|if
condition|(
name|responseHeader
operator|!=
literal|null
condition|)
block|{
name|namedList
operator|.
name|add
argument_list|(
literal|"QTime"
argument_list|,
name|responseHeader
operator|.
name|get
argument_list|(
literal|"QTime"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|namedList
operator|.
name|add
argument_list|(
literal|"ElapsedTime"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|shardResponse
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getElapsedTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|namedList
operator|.
name|add
argument_list|(
literal|"RequestPurpose"
argument_list|,
name|shardResponse
operator|.
name|getShardRequest
argument_list|()
operator|.
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|REQUEST_PURPOSE
argument_list|)
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|docList
init|=
operator|(
name|SolrDocumentList
operator|)
name|shardResponse
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"response"
argument_list|)
decl_stmt|;
if|if
condition|(
name|docList
operator|!=
literal|null
condition|)
block|{
name|namedList
operator|.
name|add
argument_list|(
literal|"NumFound"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|docList
operator|.
name|getNumFound
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|namedList
operator|.
name|add
argument_list|(
literal|"Response"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|responseNL
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|namedList
return|;
block|}
DECL|method|merge
name|Object
name|merge
parameter_list|(
name|Object
name|source
parameter_list|,
name|Object
name|dest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|exclude
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
return|return
name|dest
return|;
if|if
condition|(
name|dest
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|source
operator|instanceof
name|NamedList
condition|)
block|{
name|dest
operator|=
name|source
operator|instanceof
name|SimpleOrderedMap
condition|?
operator|new
name|SimpleOrderedMap
argument_list|()
else|:
operator|new
name|NamedList
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
name|source
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|dest
operator|instanceof
name|Collection
condition|)
block|{
if|if
condition|(
name|source
operator|instanceof
name|Collection
condition|)
block|{
operator|(
operator|(
name|Collection
operator|)
name|dest
operator|)
operator|.
name|addAll
argument_list|(
operator|(
name|Collection
operator|)
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|Collection
operator|)
name|dest
operator|)
operator|.
name|add
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
elseif|else
if|if
condition|(
name|source
operator|instanceof
name|Number
condition|)
block|{
if|if
condition|(
name|dest
operator|instanceof
name|Number
condition|)
block|{
if|if
condition|(
name|source
operator|instanceof
name|Double
operator|||
name|dest
operator|instanceof
name|Double
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|source
operator|)
operator|.
name|doubleValue
argument_list|()
operator|+
operator|(
operator|(
name|Number
operator|)
name|dest
operator|)
operator|.
name|doubleValue
argument_list|()
return|;
block|}
return|return
operator|(
operator|(
name|Number
operator|)
name|source
operator|)
operator|.
name|longValue
argument_list|()
operator|+
operator|(
operator|(
name|Number
operator|)
name|dest
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|// fall through
block|}
elseif|else
if|if
condition|(
name|source
operator|instanceof
name|String
condition|)
block|{
if|if
condition|(
name|source
operator|.
name|equals
argument_list|(
name|dest
argument_list|)
condition|)
block|{
return|return
name|dest
return|;
block|}
comment|// fall through
block|}
block|}
if|if
condition|(
name|source
operator|instanceof
name|NamedList
operator|&&
name|dest
operator|instanceof
name|NamedList
condition|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|tmp
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|sl
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|source
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|dl
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|dest
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
name|sl
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|skey
init|=
name|sl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|exclude
operator|!=
literal|null
operator|&&
name|exclude
operator|.
name|contains
argument_list|(
name|skey
argument_list|)
condition|)
continue|continue;
name|Object
name|sval
init|=
name|sl
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|didx
init|=
operator|-
literal|1
decl_stmt|;
comment|// optimize case where elements are in same position
if|if
condition|(
name|i
operator|<
name|dl
operator|.
name|size
argument_list|()
condition|)
block|{
name|String
name|dkey
init|=
name|dl
operator|.
name|getName
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|skey
operator|==
name|dkey
operator|||
operator|(
name|skey
operator|!=
literal|null
operator|&&
name|skey
operator|.
name|equals
argument_list|(
name|dkey
argument_list|)
operator|)
condition|)
block|{
name|didx
operator|=
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
name|didx
operator|==
operator|-
literal|1
condition|)
block|{
name|didx
operator|=
name|dl
operator|.
name|indexOf
argument_list|(
name|skey
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|didx
operator|==
operator|-
literal|1
condition|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|skey
argument_list|,
name|merge
argument_list|(
name|sval
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dl
operator|.
name|setVal
argument_list|(
name|didx
argument_list|,
name|merge
argument_list|(
name|sval
argument_list|,
name|dl
operator|.
name|getVal
argument_list|(
name|didx
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dl
operator|.
name|addAll
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
return|return
name|dl
return|;
block|}
comment|// merge unlike elements in a list
name|List
argument_list|<
name|Object
argument_list|>
name|t
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|t
operator|.
name|add
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|t
operator|.
name|add
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
comment|/////////////////////////////////////////////
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
literal|"Debug Information"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
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
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

