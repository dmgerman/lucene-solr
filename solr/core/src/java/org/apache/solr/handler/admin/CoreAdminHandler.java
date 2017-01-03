begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|lang
operator|.
name|StringUtils
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
name|cloud
operator|.
name|CloudDescriptor
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
name|cloud
operator|.
name|ZkController
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
name|SolrException
operator|.
name|ErrorCode
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
name|cloud
operator|.
name|ZkStateReader
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
name|CommonAdminParams
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
name|CoreAdminParams
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
name|ExecutorUtil
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
name|CoreContainer
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
name|CoreDescriptor
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
name|metrics
operator|.
name|SolrMetricManager
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
name|security
operator|.
name|AuthorizationContext
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
name|security
operator|.
name|PermissionNameProvider
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
name|DefaultSolrThreadFactory
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
name|stats
operator|.
name|MetricUtils
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
name|org
operator|.
name|slf4j
operator|.
name|MDC
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
name|CoreAdminParams
operator|.
name|ACTION
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|STATUS
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
name|security
operator|.
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|CORE_EDIT_PERM
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
name|security
operator|.
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|CORE_READ_PERM
import|;
end_import

begin_comment
comment|/**  *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|CoreAdminHandler
specifier|public
class|class
name|CoreAdminHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|PermissionNameProvider
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|coreContainer
specifier|protected
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|requestStatusMap
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|>
name|requestStatusMap
decl_stmt|;
DECL|field|parallelExecutor
specifier|protected
name|ExecutorService
name|parallelExecutor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareFixedThreadPool
argument_list|(
literal|50
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"parallelCoreAdminExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|MAX_TRACKED_REQUESTS
specifier|protected
specifier|static
name|int
name|MAX_TRACKED_REQUESTS
init|=
literal|100
decl_stmt|;
DECL|field|RUNNING
specifier|public
specifier|static
name|String
name|RUNNING
init|=
literal|"running"
decl_stmt|;
DECL|field|COMPLETED
specifier|public
specifier|static
name|String
name|COMPLETED
init|=
literal|"completed"
decl_stmt|;
DECL|field|FAILED
specifier|public
specifier|static
name|String
name|FAILED
init|=
literal|"failed"
decl_stmt|;
DECL|field|RESPONSE
specifier|public
specifier|static
name|String
name|RESPONSE
init|=
literal|"Response"
decl_stmt|;
DECL|field|RESPONSE_STATUS
specifier|public
specifier|static
name|String
name|RESPONSE_STATUS
init|=
literal|"STATUS"
decl_stmt|;
DECL|field|RESPONSE_MESSAGE
specifier|public
specifier|static
name|String
name|RESPONSE_MESSAGE
init|=
literal|"msg"
decl_stmt|;
DECL|method|CoreAdminHandler
specifier|public
name|CoreAdminHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// Unlike most request handlers, CoreContainer initialization
comment|// should happen in the constructor...
name|this
operator|.
name|coreContainer
operator|=
literal|null
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|3
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|RUNNING
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|COMPLETED
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FAILED
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|requestStatusMap
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
comment|/**    * Overloaded ctor to inject CoreContainer into the handler.    *    * @param coreContainer Core Container of the solr webapp installed.    */
DECL|method|CoreAdminHandler
specifier|public
name|CoreAdminHandler
parameter_list|(
specifier|final
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|3
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|RUNNING
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|COMPLETED
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|FAILED
argument_list|,
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|requestStatusMap
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
DECL|method|init
specifier|final
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
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
literal|"CoreAdminHandler should not be configured in solrconf.xml\n"
operator|+
literal|"it is a special Handler configured directly by the RequestDispatcher"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|initializeMetrics
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|initializeMetrics
parameter_list|(
name|SolrMetricManager
name|manager
parameter_list|,
name|String
name|registryName
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|metrics
init|=
name|super
operator|.
name|initializeMetrics
argument_list|(
name|manager
argument_list|,
name|registryName
argument_list|,
name|scope
argument_list|)
decl_stmt|;
name|parallelExecutor
operator|=
name|MetricUtils
operator|.
name|instrumentedExecutorService
argument_list|(
name|parallelExecutor
argument_list|,
name|manager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"parallelCoreAdminExecutor"
argument_list|,
name|getCategory
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|scope
argument_list|,
literal|"threadPool"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|metrics
return|;
block|}
comment|/**    * The instance of CoreContainer this handler handles. This should be the CoreContainer instance that created this    * handler.    *    * @return a CoreContainer instance    */
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|coreContainer
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
block|{
comment|// Make sure the cores is enabled
try|try
block|{
name|CoreContainer
name|cores
init|=
name|getCoreContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|cores
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Core container instance missing"
argument_list|)
throw|;
block|}
comment|//boolean doPersist = false;
specifier|final
name|String
name|taskId
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CommonAdminParams
operator|.
name|ASYNC
argument_list|)
decl_stmt|;
specifier|final
name|TaskObject
name|taskObject
init|=
operator|new
name|TaskObject
argument_list|(
name|taskId
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskId
operator|!=
literal|null
condition|)
block|{
comment|// Put the tasks into the maps for tracking
if|if
condition|(
name|getRequestStatusMap
argument_list|(
name|RUNNING
argument_list|)
operator|.
name|containsKey
argument_list|(
name|taskId
argument_list|)
operator|||
name|getRequestStatusMap
argument_list|(
name|COMPLETED
argument_list|)
operator|.
name|containsKey
argument_list|(
name|taskId
argument_list|)
operator|||
name|getRequestStatusMap
argument_list|(
name|FAILED
argument_list|)
operator|.
name|containsKey
argument_list|(
name|taskId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Duplicate request with the same requestid found."
argument_list|)
throw|;
block|}
name|addTask
argument_list|(
name|RUNNING
argument_list|,
name|taskObject
argument_list|)
expr_stmt|;
block|}
comment|// Pick the action
name|CoreAdminOperation
name|op
init|=
name|opMap
operator|.
name|get
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ACTION
argument_list|,
name|STATUS
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
name|handleCustomAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|CallInfo
name|callInfo
init|=
operator|new
name|CallInfo
argument_list|(
name|this
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|,
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskId
operator|==
literal|null
condition|)
block|{
name|callInfo
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|MDC
operator|.
name|put
argument_list|(
literal|"CoreAdminHandler.asyncId"
argument_list|,
name|taskId
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|put
argument_list|(
literal|"CoreAdminHandler.action"
argument_list|,
name|op
operator|.
name|action
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|parallelExecutor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
name|boolean
name|exceptionCaught
init|=
literal|false
decl_stmt|;
try|try
block|{
name|callInfo
operator|.
name|call
argument_list|()
expr_stmt|;
name|taskObject
operator|.
name|setRspObject
argument_list|(
name|callInfo
operator|.
name|rsp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptionCaught
operator|=
literal|true
expr_stmt|;
name|taskObject
operator|.
name|setRspObjectFromException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|removeTask
argument_list|(
literal|"running"
argument_list|,
name|taskObject
operator|.
name|taskId
argument_list|)
expr_stmt|;
if|if
condition|(
name|exceptionCaught
condition|)
block|{
name|addTask
argument_list|(
literal|"failed"
argument_list|,
name|taskObject
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
name|addTask
argument_list|(
literal|"completed"
argument_list|,
name|taskObject
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|MDC
operator|.
name|remove
argument_list|(
literal|"CoreAdminHandler.asyncId"
argument_list|)
expr_stmt|;
name|MDC
operator|.
name|remove
argument_list|(
literal|"CoreAdminHandler.action"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Handle Custom Action.    *<p>    * This method could be overridden by derived classes to handle custom actions.<br> By default - this method throws a    * solr exception. Derived classes are free to write their derivation if necessary.    */
DECL|method|handleCustomAction
specifier|protected
name|void
name|handleCustomAction
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
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
literal|"Unsupported operation: "
operator|+
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|ACTION
argument_list|)
argument_list|)
throw|;
block|}
DECL|field|paramToProp
specifier|public
specifier|static
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramToProp
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIG
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_CONFIG
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|SCHEMA
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_SCHEMA
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|DATA_DIR
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|ULOG_DIR
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_ULOGDIR
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|CONFIGSET
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_CONFIGSET
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|LOAD_ON_STARTUP
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_LOADONSTARTUP
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|TRANSIENT
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_TRANSIENT
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|SHARD
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_SHARD
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|COLLECTION
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_COLLECTION
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|ROLES
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_ROLES
argument_list|)
decl|.
name|put
argument_list|(
name|CoreAdminParams
operator|.
name|CORE_NODE_NAME
argument_list|,
name|CoreDescriptor
operator|.
name|CORE_NODE_NAME
argument_list|)
decl|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
name|CloudDescriptor
operator|.
name|NUM_SHARDS
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
DECL|method|buildCoreParams
specifier|protected
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|buildCoreParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|coreParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// standard core create parameters
for|for
control|(
name|String
name|param
range|:
name|paramToProp
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|value
init|=
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|coreParams
operator|.
name|put
argument_list|(
name|paramToProp
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|// extra properties
name|Iterator
argument_list|<
name|String
argument_list|>
name|paramsIt
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|paramsIt
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|param
init|=
name|paramsIt
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|param
operator|.
name|startsWith
argument_list|(
name|CoreAdminParams
operator|.
name|PROPERTY_PREFIX
argument_list|)
condition|)
block|{
name|String
name|propName
init|=
name|param
operator|.
name|substring
argument_list|(
name|CoreAdminParams
operator|.
name|PROPERTY_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|propValue
init|=
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|coreParams
operator|.
name|put
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|param
operator|.
name|startsWith
argument_list|(
name|ZkController
operator|.
name|COLLECTION_PARAM_PREFIX
argument_list|)
condition|)
block|{
name|coreParams
operator|.
name|put
argument_list|(
name|param
argument_list|,
name|params
operator|.
name|get
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|coreParams
return|;
block|}
DECL|method|normalizePath
specifier|protected
specifier|static
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
name|File
operator|.
name|separatorChar
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
DECL|method|params
specifier|public
specifier|static
name|ModifiableSolrParams
name|params
parameter_list|(
name|String
modifier|...
name|params
parameter_list|)
block|{
name|ModifiableSolrParams
name|msp
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|msp
operator|.
name|add
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|msp
return|;
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
return|return
literal|"Manage Multiple Solr Cores"
return|;
block|}
annotation|@
name|Override
DECL|method|getPermissionName
specifier|public
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|ctx
parameter_list|)
block|{
name|String
name|action
init|=
name|ctx
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|==
literal|null
condition|)
return|return
name|CORE_READ_PERM
return|;
name|CoreAdminParams
operator|.
name|CoreAdminAction
name|coreAction
init|=
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|get
argument_list|(
name|action
argument_list|)
decl_stmt|;
if|if
condition|(
name|coreAction
operator|==
literal|null
condition|)
return|return
name|CORE_READ_PERM
return|;
return|return
name|coreAction
operator|.
name|isRead
condition|?
name|CORE_READ_PERM
else|:
name|CORE_EDIT_PERM
return|;
block|}
comment|/**    * Helper class to manage the tasks to be tracked.    * This contains the taskId, request and the response (if available).    */
DECL|class|TaskObject
specifier|static
class|class
name|TaskObject
block|{
DECL|field|taskId
name|String
name|taskId
decl_stmt|;
DECL|field|rspInfo
name|String
name|rspInfo
decl_stmt|;
DECL|method|TaskObject
specifier|public
name|TaskObject
parameter_list|(
name|String
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
block|}
DECL|method|getRspObject
specifier|public
name|String
name|getRspObject
parameter_list|()
block|{
return|return
name|rspInfo
return|;
block|}
DECL|method|setRspObject
specifier|public
name|void
name|setRspObject
parameter_list|(
name|SolrQueryResponse
name|rspObject
parameter_list|)
block|{
name|this
operator|.
name|rspInfo
operator|=
name|rspObject
operator|.
name|getToLogAsString
argument_list|(
literal|"TaskId: "
operator|+
name|this
operator|.
name|taskId
argument_list|)
expr_stmt|;
block|}
DECL|method|setRspObjectFromException
specifier|public
name|void
name|setRspObjectFromException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|this
operator|.
name|rspInfo
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Helper method to add a task to a tracking type.    */
DECL|method|addTask
name|void
name|addTask
parameter_list|(
name|String
name|type
parameter_list|,
name|TaskObject
name|o
parameter_list|,
name|boolean
name|limit
parameter_list|)
block|{
synchronized|synchronized
init|(
name|getRequestStatusMap
argument_list|(
name|type
argument_list|)
init|)
block|{
if|if
condition|(
name|limit
operator|&&
name|getRequestStatusMap
argument_list|(
name|type
argument_list|)
operator|.
name|size
argument_list|()
operator|==
name|MAX_TRACKED_REQUESTS
condition|)
block|{
name|String
name|key
init|=
name|getRequestStatusMap
argument_list|(
name|type
argument_list|)
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|getRequestStatusMap
argument_list|(
name|type
argument_list|)
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|addTask
argument_list|(
name|type
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addTask
specifier|private
name|void
name|addTask
parameter_list|(
name|String
name|type
parameter_list|,
name|TaskObject
name|o
parameter_list|)
block|{
synchronized|synchronized
init|(
name|getRequestStatusMap
argument_list|(
name|type
argument_list|)
init|)
block|{
name|getRequestStatusMap
argument_list|(
name|type
argument_list|)
operator|.
name|put
argument_list|(
name|o
operator|.
name|taskId
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper method to remove a task from a tracking map.    */
DECL|method|removeTask
specifier|private
name|void
name|removeTask
parameter_list|(
name|String
name|map
parameter_list|,
name|String
name|taskId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|getRequestStatusMap
argument_list|(
name|map
argument_list|)
init|)
block|{
name|getRequestStatusMap
argument_list|(
name|map
argument_list|)
operator|.
name|remove
argument_list|(
name|taskId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Helper method to get a request status map given the name.    */
DECL|method|getRequestStatusMap
name|Map
argument_list|<
name|String
argument_list|,
name|TaskObject
argument_list|>
name|getRequestStatusMap
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|requestStatusMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Method to ensure shutting down of the ThreadPool Executor.    */
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|parallelExecutor
operator|!=
literal|null
operator|&&
operator|!
name|parallelExecutor
operator|.
name|isShutdown
argument_list|()
condition|)
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|parallelExecutor
argument_list|)
expr_stmt|;
block|}
DECL|field|opMap
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CoreAdminOperation
argument_list|>
name|opMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|CallInfo
specifier|static
class|class
name|CallInfo
block|{
DECL|field|handler
specifier|final
name|CoreAdminHandler
name|handler
decl_stmt|;
DECL|field|req
specifier|final
name|SolrQueryRequest
name|req
decl_stmt|;
DECL|field|rsp
specifier|final
name|SolrQueryResponse
name|rsp
decl_stmt|;
DECL|field|op
specifier|final
name|CoreAdminOperation
name|op
decl_stmt|;
DECL|method|CallInfo
name|CallInfo
parameter_list|(
name|CoreAdminHandler
name|handler
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|CoreAdminOperation
name|op
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|rsp
operator|=
name|rsp
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
block|}
DECL|method|call
name|void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|op
operator|.
name|execute
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
static|static
block|{
for|for
control|(
name|CoreAdminOperation
name|op
range|:
name|CoreAdminOperation
operator|.
name|values
argument_list|()
control|)
name|opMap
operator|.
name|put
argument_list|(
name|op
operator|.
name|action
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**    * used by the INVOKE action of core admin handler    */
DECL|interface|Invocable
specifier|public
interface|interface
name|Invocable
block|{
DECL|method|invoke
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|invoke
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
function_decl|;
block|}
DECL|interface|CoreAdminOp
interface|interface
name|CoreAdminOp
block|{
DECL|method|execute
name|void
name|execute
parameter_list|(
name|CallInfo
name|it
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

