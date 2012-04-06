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
name|lucene
operator|.
name|queryparser
operator|.
name|classic
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
name|core
operator|.
name|CloseHook
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
name|response
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
name|util
operator|.
name|SolrPluginUtils
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
implements|,
name|PluginInfoInitialized
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
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SearchHandler
operator|.
name|class
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
DECL|field|shardHandlerFactory
specifier|private
name|ShardHandlerFactory
name|shardHandlerFactory
decl_stmt|;
DECL|field|shfInfo
specifier|private
name|PluginInfo
name|shfInfo
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
literal|6
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
name|StatsComponent
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
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
for|for
control|(
name|PluginInfo
name|child
range|:
name|info
operator|.
name|children
control|)
block|{
if|if
condition|(
literal|"shardHandlerFactory"
operator|.
name|equals
argument_list|(
name|child
operator|.
name|type
argument_list|)
condition|)
block|{
name|this
operator|.
name|shfInfo
operator|=
name|child
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**    * Initialize the components based on name.  Note, if using {@link #INIT_FIRST_COMPONENTS} or {@link #INIT_LAST_COMPONENTS},    * then the {@link DebugComponent} will always occur last.  If this is not desired, then one must explicitly declare all components using    * the {@link #INIT_COMPONENTS} syntax.    */
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
name|boolean
name|makeDebugLast
init|=
literal|true
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
name|makeDebugLast
operator|=
literal|false
expr_stmt|;
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
name|DebugComponent
name|dbgCmp
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
name|comp
operator|instanceof
name|DebugComponent
operator|&&
name|makeDebugLast
operator|==
literal|true
condition|)
block|{
name|dbgCmp
operator|=
operator|(
name|DebugComponent
operator|)
name|comp
expr_stmt|;
block|}
else|else
block|{
name|components
operator|.
name|add
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Adding  component:"
operator|+
name|comp
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|makeDebugLast
operator|==
literal|true
operator|&&
name|dbgCmp
operator|!=
literal|null
condition|)
block|{
name|components
operator|.
name|add
argument_list|(
name|dbgCmp
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Adding  debug component:"
operator|+
name|dbgCmp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shfInfo
operator|==
literal|null
condition|)
block|{
name|shardHandlerFactory
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getShardHandlerFactory
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|shardHandlerFactory
operator|=
name|core
operator|.
name|createInitInstance
argument_list|(
name|shfInfo
argument_list|,
name|ShardHandlerFactory
operator|.
name|class
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|core
operator|.
name|addCloseHook
argument_list|(
operator|new
name|CloseHook
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|preClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|shardHandlerFactory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postClose
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{         }
block|}
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
comment|// int sleep = req.getParams().getInt("sleep",0);
comment|// if (sleep> 0) {log.error("SLEEPING for " + sleep);  Thread.sleep(sleep);}
name|ResponseBuilder
name|rb
init|=
operator|new
name|ResponseBuilder
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|components
argument_list|)
decl_stmt|;
if|if
condition|(
name|rb
operator|.
name|requestInfo
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|requestInfo
operator|.
name|setResponseBuilder
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
name|boolean
name|dbg
init|=
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
decl_stmt|;
name|rb
operator|.
name|setDebug
argument_list|(
name|dbg
argument_list|)
expr_stmt|;
if|if
condition|(
name|dbg
operator|==
literal|false
condition|)
block|{
comment|//if it's true, we are doing everything anyway.
name|SolrPluginUtils
operator|.
name|getDebugInterests
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|)
argument_list|,
name|rb
argument_list|)
expr_stmt|;
block|}
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
name|ShardHandler
name|shardHandler1
init|=
name|shardHandlerFactory
operator|.
name|getShardHandler
argument_list|()
decl_stmt|;
name|shardHandler1
operator|.
name|checkDistributed
argument_list|(
name|rb
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
operator|!
name|rb
operator|.
name|isDistrib
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
name|isDebugTimings
argument_list|()
condition|)
block|{
name|rb
operator|.
name|addDebugInfo
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
block|}
else|else
block|{
comment|// a distributed request
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
operator|new
name|ModifiableSolrParams
argument_list|(
name|sreq
operator|.
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
comment|// not a top-level request
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
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
name|CommonParams
operator|.
name|HEADER_ECHO_PARAMS
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// a sub (shard) request
name|params
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARD_URL
argument_list|,
name|shard
argument_list|)
expr_stmt|;
comment|// so the shard knows what was asked
if|if
condition|(
name|rb
operator|.
name|requestInfo
operator|!=
literal|null
condition|)
block|{
comment|// we could try and detect when this is needed, but it could be tricky
name|params
operator|.
name|set
argument_list|(
literal|"NOW"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|rb
operator|.
name|requestInfo
operator|.
name|getNOW
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|shardQt
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_QT
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardQt
operator|==
literal|null
condition|)
block|{
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
name|shardQt
argument_list|)
expr_stmt|;
block|}
name|shardHandler1
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
name|boolean
name|tolerant
init|=
name|rb
operator|.
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|tolerant
condition|?
name|shardHandler1
operator|.
name|takeCompletedIncludingErrors
argument_list|()
else|:
name|shardHandler1
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
comment|// Was there an exception?
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
comment|// If things are not tolerant, abort everything and rethrow
if|if
condition|(
operator|!
name|tolerant
condition|)
block|{
name|shardHandler1
operator|.
name|cancelAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
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
name|getException
argument_list|()
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
name|getException
argument_list|()
argument_list|)
throw|;
block|}
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
name|getShardRequest
argument_list|()
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
name|getShardRequest
argument_list|()
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
if|if
condition|(
name|components
operator|!=
literal|null
condition|)
block|{
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

end_unit

