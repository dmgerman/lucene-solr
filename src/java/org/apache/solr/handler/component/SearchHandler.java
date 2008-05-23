begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|solr
operator|.
name|handler
operator|.
name|RequestHandlerBase
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
name|RTimer
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
name|SolrQueryResponse
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
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|client
operator|.
name|solrj
operator|.
name|SolrResponse
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CommonsHttpSolrServer
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|BinaryResponseParser
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
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|MultiThreadedHttpConnectionManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  *  * Refer SOLR-281  *  */
end_comment

begin_class
DECL|class|SearchHandler
specifier|public
class|class
name|SearchHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
block|{
DECL|field|INIT_COMPONENTS
specifier|static
specifier|final
name|String
name|INIT_COMPONENTS
init|=
literal|"components"
decl_stmt|;
DECL|field|INIT_FIRST_COMPONENTS
specifier|static
specifier|final
name|String
name|INIT_FIRST_COMPONENTS
init|=
literal|"first-components"
decl_stmt|;
DECL|field|INIT_LAST_COMPONENTS
specifier|static
specifier|final
name|String
name|INIT_LAST_COMPONENTS
init|=
literal|"last-components"
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SearchHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|components
specifier|protected
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|components
init|=
literal|null
decl_stmt|;
DECL|method|getDefaultComponents
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultComponents
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
name|QueryComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|FacetComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|MoreLikeThisComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|HighlightComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|DebugComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
return|return
name|names
return|;
block|}
comment|/**    * Initialize the components based on name    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|Object
name|declaredComponents
init|=
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_COMPONENTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|first
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_FIRST_COMPONENTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|last
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_LAST_COMPONENTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|declaredComponents
operator|==
literal|null
condition|)
block|{
comment|// Use the default component list
name|list
operator|=
name|getDefaultComponents
argument_list|()
expr_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|clist
init|=
name|first
decl_stmt|;
name|clist
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|list
operator|=
name|clist
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|list
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|declaredComponents
expr_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
operator|||
name|last
operator|!=
literal|null
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
name|SERVER_ERROR
argument_list|,
literal|"First/Last components only valid if you do not declare 'components'"
argument_list|)
throw|;
block|}
block|}
comment|// Build the component list
name|components
operator|=
operator|new
name|ArrayList
argument_list|<
name|SearchComponent
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|list
control|)
block|{
name|SearchComponent
name|comp
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|components
operator|.
name|add
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding  component:"
operator|+
name|comp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getComponents
specifier|public
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|getComponents
parameter_list|()
block|{
return|return
name|components
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
throws|,
name|ParseException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
block|{
name|ResponseBuilder
name|rb
init|=
operator|new
name|ResponseBuilder
argument_list|()
decl_stmt|;
name|rb
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|rb
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
name|rb
operator|.
name|components
operator|=
name|components
expr_stmt|;
name|rb
operator|.
name|setDebug
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RTimer
name|timer
init|=
name|rb
operator|.
name|isDebug
argument_list|()
condition|?
operator|new
name|RTimer
argument_list|()
else|:
literal|null
decl_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|timer
operator|==
literal|null
condition|)
block|{
comment|// non-debugging prepare phase
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|c
operator|.
name|prepare
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// debugging prepare phase
name|RTimer
name|subt
init|=
name|timer
operator|.
name|sub
argument_list|(
literal|"prepare"
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|rb
operator|.
name|setTimer
argument_list|(
name|subt
operator|.
name|sub
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|prepare
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|rb
operator|.
name|getTimer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|subt
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rb
operator|.
name|shards
operator|==
literal|null
condition|)
block|{
comment|// a normal non-distributed request
comment|// The semantics of debugging vs not debugging are different enough that
comment|// it makes sense to have two control loops
if|if
condition|(
operator|!
name|rb
operator|.
name|isDebug
argument_list|()
condition|)
block|{
comment|// Process
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|c
operator|.
name|process
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Process
name|RTimer
name|subt
init|=
name|timer
operator|.
name|sub
argument_list|(
literal|"process"
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|rb
operator|.
name|setTimer
argument_list|(
name|subt
operator|.
name|sub
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|process
argument_list|(
name|rb
argument_list|)
expr_stmt|;
name|rb
operator|.
name|getTimer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|subt
operator|.
name|stop
argument_list|()
expr_stmt|;
name|timer
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// add the timing info
if|if
condition|(
name|rb
operator|.
name|getDebugInfo
argument_list|()
operator|==
literal|null
condition|)
block|{
name|rb
operator|.
name|setDebugInfo
argument_list|(
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|getDebugInfo
argument_list|()
operator|.
name|add
argument_list|(
literal|"timing"
argument_list|,
name|timer
operator|.
name|asNamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// a distributed request
name|HttpCommComponent
name|comm
init|=
operator|new
name|HttpCommComponent
argument_list|()
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|outgoing
operator|==
literal|null
condition|)
block|{
name|rb
operator|.
name|outgoing
operator|=
operator|new
name|LinkedList
argument_list|<
name|ShardRequest
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|rb
operator|.
name|finished
operator|=
operator|new
name|ArrayList
argument_list|<
name|ShardRequest
argument_list|>
argument_list|()
expr_stmt|;
name|int
name|nextStage
init|=
literal|0
decl_stmt|;
do|do
block|{
name|rb
operator|.
name|stage
operator|=
name|nextStage
expr_stmt|;
name|nextStage
operator|=
name|ResponseBuilder
operator|.
name|STAGE_DONE
expr_stmt|;
comment|// call all components
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
comment|// the next stage is the minimum of what all components report
name|nextStage
operator|=
name|Math
operator|.
name|min
argument_list|(
name|nextStage
argument_list|,
name|c
operator|.
name|distributedProcess
argument_list|(
name|rb
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check the outgoing queue and send requests
while|while
condition|(
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// submit all current request tasks at once
while|while
condition|(
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ShardRequest
name|sreq
init|=
name|rb
operator|.
name|outgoing
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|sreq
operator|.
name|actualShards
operator|=
name|sreq
operator|.
name|shards
expr_stmt|;
if|if
condition|(
name|sreq
operator|.
name|actualShards
operator|==
name|ShardRequest
operator|.
name|ALL_SHARDS
condition|)
block|{
name|sreq
operator|.
name|actualShards
operator|=
name|rb
operator|.
name|shards
expr_stmt|;
block|}
name|sreq
operator|.
name|responses
operator|=
operator|new
name|ArrayList
argument_list|<
name|ShardResponse
argument_list|>
argument_list|()
expr_stmt|;
comment|// TODO: map from shard to address[]
for|for
control|(
name|String
name|shard
range|:
name|sreq
operator|.
name|actualShards
control|)
block|{
name|ModifiableSolrParams
name|params
init|=
name|sreq
operator|.
name|params
decl_stmt|;
name|params
operator|.
name|remove
argument_list|(
literal|"shards"
argument_list|)
expr_stmt|;
comment|// not a top-level request
name|params
operator|.
name|remove
argument_list|(
literal|"indent"
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
literal|"echoParams"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"isShard"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// a sub (shard) request
name|String
name|shardHandler
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"shards.qt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardHandler
operator|==
literal|null
condition|)
block|{
name|params
operator|.
name|remove
argument_list|(
literal|"qt"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
name|shardHandler
argument_list|)
expr_stmt|;
block|}
name|comm
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|shard
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now wait for replies, but if anyone puts more requests on
comment|// the outgoing queue, send them out immediately (by exiting
comment|// this loop)
while|while
condition|(
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|ShardResponse
name|srsp
init|=
name|comm
operator|.
name|takeCompletedOrError
argument_list|()
decl_stmt|;
if|if
condition|(
name|srsp
operator|==
literal|null
condition|)
break|break;
comment|// no more requests to wait for
comment|// Was there an exception?  If so, abort everything and
comment|// rethrow
if|if
condition|(
name|srsp
operator|.
name|exception
operator|!=
literal|null
condition|)
block|{
name|comm
operator|.
name|cancelAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|srsp
operator|.
name|exception
operator|instanceof
name|SolrException
condition|)
block|{
throw|throw
operator|(
name|SolrException
operator|)
name|srsp
operator|.
name|exception
throw|;
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
name|SERVER_ERROR
argument_list|,
name|srsp
operator|.
name|exception
argument_list|)
throw|;
block|}
block|}
name|rb
operator|.
name|finished
operator|.
name|add
argument_list|(
name|srsp
operator|.
name|req
argument_list|)
expr_stmt|;
comment|// let the components see the responses to the request
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|c
operator|.
name|handleResponses
argument_list|(
name|rb
argument_list|,
name|srsp
operator|.
name|req
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|c
operator|.
name|finishStage
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
comment|// we are done when the next stage is MAX_VALUE
block|}
do|while
condition|(
name|nextStage
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
do|;
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Search using components: "
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
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
block|}
end_class

begin_comment
comment|// TODO: generalize how a comm component can fit into search component framework
end_comment

begin_comment
comment|// TODO: statics should be per-core singletons
end_comment

begin_class
DECL|class|HttpCommComponent
class|class
name|HttpCommComponent
block|{
comment|// We want an executor that doesn't take up any resources if
comment|// it's not used, so it could be created statically for
comment|// the distributed search component if desired.
comment|//
comment|// Consider CallerRuns policy and a lower max threads to throttle
comment|// requests at some point (or should we simply return failure?)
DECL|field|commExecutor
specifier|static
name|Executor
name|commExecutor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
comment|// terminate idle threads after 5 sec
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
comment|// directly hand off tasks
argument_list|)
decl_stmt|;
DECL|field|client
specifier|static
name|HttpClient
name|client
decl_stmt|;
static|static
block|{
name|MultiThreadedHttpConnectionManager
name|mgr
init|=
operator|new
name|MultiThreadedHttpConnectionManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|getParams
argument_list|()
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|getParams
argument_list|()
operator|.
name|setMaxTotalConnections
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
comment|// mgr.getParams().setStaleCheckingEnabled(false);
name|client
operator|=
operator|new
name|HttpClient
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
block|}
DECL|field|completionService
name|CompletionService
argument_list|<
name|ShardResponse
argument_list|>
name|completionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<
name|ShardResponse
argument_list|>
argument_list|(
name|commExecutor
argument_list|)
decl_stmt|;
DECL|field|pending
name|Set
argument_list|<
name|Future
argument_list|<
name|ShardResponse
argument_list|>
argument_list|>
name|pending
init|=
operator|new
name|HashSet
argument_list|<
name|Future
argument_list|<
name|ShardResponse
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|HttpCommComponent
name|HttpCommComponent
parameter_list|()
block|{   }
DECL|class|SimpleSolrResponse
specifier|private
specifier|static
class|class
name|SimpleSolrResponse
extends|extends
name|SolrResponse
block|{
DECL|field|elapsedTime
name|long
name|elapsedTime
decl_stmt|;
DECL|field|nl
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
decl_stmt|;
DECL|method|getElapsedTime
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
return|return
name|elapsedTime
return|;
block|}
DECL|method|getResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getResponse
parameter_list|()
block|{
return|return
name|nl
return|;
block|}
block|}
DECL|method|submit
name|void
name|submit
parameter_list|(
specifier|final
name|ShardRequest
name|sreq
parameter_list|,
specifier|final
name|String
name|shard
parameter_list|,
specifier|final
name|ModifiableSolrParams
name|params
parameter_list|)
block|{
name|Callable
argument_list|<
name|ShardResponse
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|ShardResponse
argument_list|>
argument_list|()
block|{
specifier|public
name|ShardResponse
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|ShardResponse
name|srsp
init|=
operator|new
name|ShardResponse
argument_list|()
decl_stmt|;
name|srsp
operator|.
name|req
operator|=
name|sreq
expr_stmt|;
name|srsp
operator|.
name|shard
operator|=
name|shard
expr_stmt|;
name|SimpleSolrResponse
name|ssr
init|=
operator|new
name|SimpleSolrResponse
argument_list|()
decl_stmt|;
name|srsp
operator|.
name|rsp
operator|=
name|ssr
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
comment|// String url = "http://" + shard + "/select";
name|String
name|url
init|=
literal|"http://"
operator|+
name|shard
decl_stmt|;
name|params
operator|.
name|remove
argument_list|(
literal|"wt"
argument_list|)
expr_stmt|;
comment|// use default (or should we explicitly set it?)
name|params
operator|.
name|remove
argument_list|(
literal|"version"
argument_list|)
expr_stmt|;
name|SolrServer
name|server
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|,
name|client
argument_list|)
decl_stmt|;
comment|// SolrRequest req = new QueryRequest(SolrRequest.METHOD.POST, "/select");
comment|// use generic request to avoid extra processing of queries
name|QueryRequest
name|req
init|=
operator|new
name|QueryRequest
argument_list|(
name|sreq
operator|.
name|params
argument_list|)
decl_stmt|;
name|req
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
name|req
operator|.
name|setResponseParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
expr_stmt|;
comment|// this sets the wt param
comment|// srsp.rsp = server.request(req);
comment|// srsp.rsp = server.query(sreq.params);
name|ssr
operator|.
name|nl
operator|=
name|server
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|srsp
operator|.
name|exception
operator|=
name|th
expr_stmt|;
if|if
condition|(
name|th
operator|instanceof
name|SolrException
condition|)
block|{
name|srsp
operator|.
name|rspCode
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|th
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|srsp
operator|.
name|rspCode
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
name|ssr
operator|.
name|elapsedTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
expr_stmt|;
return|return
name|srsp
return|;
block|}
block|}
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|completionService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** returns a ShardResponse of the last response correlated with a ShardRequest */
DECL|method|take
name|ShardResponse
name|take
parameter_list|()
block|{
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Future
argument_list|<
name|ShardResponse
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
name|pending
operator|.
name|remove
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|ShardResponse
name|rsp
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|req
operator|.
name|responses
operator|.
name|add
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|req
operator|.
name|responses
operator|.
name|size
argument_list|()
operator|==
name|rsp
operator|.
name|req
operator|.
name|actualShards
operator|.
name|length
condition|)
block|{
return|return
name|rsp
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// should be impossible... the problem with catching the exception
comment|// at this level is we don't know what ShardRequest it applied to
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Impossible Exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** returns a ShardResponse of the last response correlated with a ShardRequest,    * or immediately returns a ShardResponse if there was an error detected    */
DECL|method|takeCompletedOrError
name|ShardResponse
name|takeCompletedOrError
parameter_list|()
block|{
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Future
argument_list|<
name|ShardResponse
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
name|pending
operator|.
name|remove
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|ShardResponse
name|rsp
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|rsp
operator|.
name|exception
operator|!=
literal|null
condition|)
return|return
name|rsp
return|;
comment|// if exception, return immediately
comment|// add response to the response list... we do this after the take() and
comment|// not after the completion of "call" so we know when the last response
comment|// for a request was received.  Otherwise we might return the same
comment|// request more than once.
name|rsp
operator|.
name|req
operator|.
name|responses
operator|.
name|add
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|req
operator|.
name|responses
operator|.
name|size
argument_list|()
operator|==
name|rsp
operator|.
name|req
operator|.
name|actualShards
operator|.
name|length
condition|)
block|{
return|return
name|rsp
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// should be impossible... the problem with catching the exception
comment|// at this level is we don't know what ShardRequest it applied to
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Impossible Exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|cancelAll
name|void
name|cancelAll
parameter_list|()
block|{
for|for
control|(
name|Future
argument_list|<
name|ShardResponse
argument_list|>
name|future
range|:
name|pending
control|)
block|{
comment|// TODO: any issues with interrupting?  shouldn't be if
comment|// there are finally blocks to release connections.
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

