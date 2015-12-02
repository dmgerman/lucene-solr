begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|concurrent
operator|.
name|TimeUnit
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
name|cloud
operator|.
name|Overseer
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
name|OverseerSolrResponse
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
name|OverseerTaskQueue
operator|.
name|QueueEvent
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
name|SolrZkClient
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
name|ZkConfigManager
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
name|ZkNodeProps
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
name|ConfigSetParams
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
name|ConfigSetParams
operator|.
name|ConfigSetAction
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
name|util
operator|.
name|Utils
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
name|zookeeper
operator|.
name|KeeperException
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
name|cloud
operator|.
name|OverseerConfigSetMessageHandler
operator|.
name|BASE_CONFIGSET
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
name|cloud
operator|.
name|OverseerConfigSetMessageHandler
operator|.
name|CONFIGSETS_ACTION_PREFIX
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
name|cloud
operator|.
name|OverseerConfigSetMessageHandler
operator|.
name|PROPERTY_PREFIX
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
name|NAME
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
name|ConfigSetParams
operator|.
name|ConfigSetAction
operator|.
name|*
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|Overseer
operator|.
name|QUEUE_OPERATION
import|;
end_import

begin_comment
comment|/**  * A {@link org.apache.solr.request.SolrRequestHandler} for ConfigSets API requests.  */
end_comment

begin_class
DECL|class|ConfigSetsHandler
specifier|public
class|class
name|ConfigSetsHandler
extends|extends
name|RequestHandlerBase
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
DECL|field|DEFAULT_ZK_TIMEOUT
specifier|public
specifier|static
name|long
name|DEFAULT_ZK_TIMEOUT
init|=
literal|300
operator|*
literal|1000
decl_stmt|;
comment|/**    * Overloaded ctor to inject CoreContainer into the handler.    *    * @param coreContainer Core Container of the solr webapp installed.    */
DECL|method|ConfigSetsHandler
specifier|public
name|ConfigSetsHandler
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
block|{    }
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
if|if
condition|(
name|coreContainer
operator|==
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
name|BAD_REQUEST
argument_list|,
literal|"Core container instance missing"
argument_list|)
throw|;
block|}
comment|// Make sure that the core is ZKAware
if|if
condition|(
operator|!
name|coreContainer
operator|.
name|isZooKeeperAware
argument_list|()
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
literal|"Solr instance is not running in SolrCloud mode."
argument_list|)
throw|;
block|}
comment|// Pick the action
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|String
name|a
init|=
name|params
operator|.
name|get
argument_list|(
name|ConfigSetParams
operator|.
name|ACTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
condition|)
block|{
name|ConfigSetAction
name|action
init|=
name|ConfigSetAction
operator|.
name|get
argument_list|(
name|a
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|==
literal|null
condition|)
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
literal|"Unknown action: "
operator|+
name|a
argument_list|)
throw|;
name|ConfigSetOperation
name|operation
init|=
name|ConfigSetOperation
operator|.
name|get
argument_list|(
name|action
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Invoked ConfigSet Action :{} with params {} "
argument_list|,
name|action
operator|.
name|toLower
argument_list|()
argument_list|,
name|req
operator|.
name|getParamString
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
name|operation
operator|.
name|call
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// We need to differentiate between collection and configsets actions since they currently
comment|// use the same underlying queue.
name|result
operator|.
name|put
argument_list|(
name|QUEUE_OPERATION
argument_list|,
name|CONFIGSETS_ACTION_PREFIX
operator|+
name|operation
operator|.
name|action
operator|.
name|toLower
argument_list|()
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|props
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|handleResponse
argument_list|(
name|operation
operator|.
name|action
operator|.
name|toLower
argument_list|()
argument_list|,
name|props
argument_list|,
name|rsp
argument_list|,
name|DEFAULT_ZK_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"action is a required param"
argument_list|)
throw|;
block|}
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|handleResponse
specifier|private
name|void
name|handleResponse
parameter_list|(
name|String
name|operation
parameter_list|,
name|ZkNodeProps
name|m
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|long
name|time
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|QueueEvent
name|event
init|=
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getOverseerConfigSetQueue
argument_list|()
operator|.
name|offer
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getBytes
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SolrResponse
name|response
init|=
name|SolrResponse
operator|.
name|deserialize
argument_list|(
name|event
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
name|SimpleOrderedMap
name|exp
init|=
operator|(
name|SimpleOrderedMap
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"exception"
argument_list|)
decl_stmt|;
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|Integer
name|code
init|=
operator|(
name|Integer
operator|)
name|exp
operator|.
name|get
argument_list|(
literal|"rspCode"
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setException
argument_list|(
operator|new
name|SolrException
argument_list|(
name|code
operator|!=
literal|null
operator|&&
name|code
operator|!=
operator|-
literal|1
condition|?
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|code
argument_list|)
else|:
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
operator|(
name|String
operator|)
name|exp
operator|.
name|get
argument_list|(
literal|"msg"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|time
operator|>=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|operation
operator|+
literal|" the configset time out:"
operator|+
name|timeout
operator|/
literal|1000
operator|+
literal|"s"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|operation
operator|+
literal|" the configset error [Watcher fired on path: "
operator|+
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|.
name|getPath
argument_list|()
operator|+
literal|" state: "
operator|+
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|.
name|getState
argument_list|()
operator|+
literal|" type "
operator|+
name|event
operator|.
name|getWatchedEvent
argument_list|()
operator|.
name|getType
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|operation
operator|+
literal|" the configset unknown case"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|copyPropertiesWithPrefix
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|copyPropertiesWithPrefix
parameter_list|(
name|SolrParams
name|params
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|params
operator|.
name|getParameterNamesIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|param
init|=
name|iter
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
name|prefix
argument_list|)
condition|)
block|{
name|props
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
name|props
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Manage SolrCloud ConfigSets"
return|;
block|}
DECL|enum|ConfigSetOperation
enum|enum
name|ConfigSetOperation
block|{
DECL|method|CREATE_OP
DECL|method|CREATE_OP
name|CREATE_OP
parameter_list|(
name|CREATE
parameter_list|)
block|{
annotation|@
name|Override
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ConfigSetsHandler
name|h
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|getAll
argument_list|(
literal|null
argument_list|,
name|NAME
argument_list|,
name|BASE_CONFIGSET
argument_list|)
decl_stmt|;
return|return
name|copyPropertiesWithPrefix
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
argument_list|,
name|props
argument_list|,
name|PROPERTY_PREFIX
operator|+
literal|"."
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|DELETE_OP
DECL|method|DELETE_OP
name|DELETE_OP
parameter_list|(
name|DELETE
parameter_list|)
block|{
annotation|@
name|Override
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ConfigSetsHandler
name|h
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|required
argument_list|()
operator|.
name|getAll
argument_list|(
literal|null
argument_list|,
name|NAME
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|LIST_OP
DECL|method|LIST_OP
name|LIST_OP
parameter_list|(
name|LIST
parameter_list|)
block|{
annotation|@
name|Override
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ConfigSetsHandler
name|h
parameter_list|)
throws|throws
name|Exception
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|results
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|SolrZkClient
name|zk
init|=
name|h
operator|.
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|ZkConfigManager
name|zkConfigManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zk
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|configSetsList
init|=
name|zkConfigManager
operator|.
name|listConfigs
argument_list|()
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
literal|"configSets"
argument_list|,
name|configSetsList
argument_list|)
expr_stmt|;
name|SolrResponse
name|response
init|=
operator|new
name|OverseerSolrResponse
argument_list|(
name|results
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|addAll
argument_list|(
name|response
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|;
DECL|field|action
name|ConfigSetAction
name|action
decl_stmt|;
DECL|method|ConfigSetOperation
name|ConfigSetOperation
parameter_list|(
name|ConfigSetAction
name|action
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
DECL|method|call
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|ConfigSetsHandler
name|h
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|get
specifier|public
specifier|static
name|ConfigSetOperation
name|get
parameter_list|(
name|ConfigSetAction
name|action
parameter_list|)
block|{
for|for
control|(
name|ConfigSetOperation
name|op
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|op
operator|.
name|action
operator|==
name|action
condition|)
return|return
name|op
return|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"No such action"
operator|+
name|action
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

