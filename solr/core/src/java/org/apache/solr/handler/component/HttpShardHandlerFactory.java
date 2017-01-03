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
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|SolrServerException
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
name|HttpClientUtil
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
name|LBHttpSolrClient
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
name|LBHttpSolrClient
operator|.
name|Builder
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
name|cloud
operator|.
name|Replica
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
name|common
operator|.
name|util
operator|.
name|StrUtils
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
name|URLUtil
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
name|metrics
operator|.
name|SolrMetricProducer
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
name|update
operator|.
name|UpdateShardHandlerConfig
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
name|InstrumentedHttpRequestExecutor
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
name|InstrumentedPoolingHttpClientConnectionManager
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
name|Collection
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|CompletionService
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
name|ExecutorCompletionService
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|SynchronousQueue
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

begin_class
DECL|class|HttpShardHandlerFactory
specifier|public
class|class
name|HttpShardHandlerFactory
extends|extends
name|ShardHandlerFactory
implements|implements
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
implements|,
name|SolrMetricProducer
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
DECL|field|DEFAULT_SCHEME
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_SCHEME
init|=
literal|"http"
decl_stmt|;
comment|// We want an executor that doesn't take up any resources if
comment|// it's not used, so it could be created statically for
comment|// the distributed search component if desired.
comment|//
comment|// Consider CallerRuns policy and a lower max threads to throttle
comment|// requests at some point (or should we simply return failure?)
DECL|field|commExecutor
specifier|private
name|ExecutorService
name|commExecutor
init|=
operator|new
name|ExecutorUtil
operator|.
name|MDCAwareThreadPoolExecutor
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
argument_list|,
comment|// directly hand off tasks
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"httpShardExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|clientConnectionManager
specifier|protected
name|InstrumentedPoolingHttpClientConnectionManager
name|clientConnectionManager
decl_stmt|;
DECL|field|defaultClient
specifier|protected
name|CloseableHttpClient
name|defaultClient
decl_stmt|;
DECL|field|httpRequestExecutor
specifier|protected
name|InstrumentedHttpRequestExecutor
name|httpRequestExecutor
decl_stmt|;
DECL|field|loadbalancer
specifier|private
name|LBHttpSolrClient
name|loadbalancer
decl_stmt|;
comment|//default values:
DECL|field|soTimeout
name|int
name|soTimeout
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_DISTRIBUPDATESOTIMEOUT
decl_stmt|;
DECL|field|connectionTimeout
name|int
name|connectionTimeout
init|=
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_DISTRIBUPDATECONNTIMEOUT
decl_stmt|;
DECL|field|maxConnectionsPerHost
name|int
name|maxConnectionsPerHost
init|=
literal|20
decl_stmt|;
DECL|field|maxConnections
name|int
name|maxConnections
init|=
literal|10000
decl_stmt|;
DECL|field|corePoolSize
name|int
name|corePoolSize
init|=
literal|0
decl_stmt|;
DECL|field|maximumPoolSize
name|int
name|maximumPoolSize
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|keepAliveTime
name|int
name|keepAliveTime
init|=
literal|5
decl_stmt|;
DECL|field|queueSize
name|int
name|queueSize
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|accessPolicy
name|boolean
name|accessPolicy
init|=
literal|false
decl_stmt|;
DECL|field|scheme
specifier|private
name|String
name|scheme
init|=
literal|null
decl_stmt|;
DECL|field|r
specifier|private
specifier|final
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|shufflingReplicaListTransformer
specifier|private
specifier|final
name|ReplicaListTransformer
name|shufflingReplicaListTransformer
init|=
operator|new
name|ShufflingReplicaListTransformer
argument_list|(
name|r
argument_list|)
decl_stmt|;
comment|// URL scheme to be used in distributed search.
DECL|field|INIT_URL_SCHEME
specifier|static
specifier|final
name|String
name|INIT_URL_SCHEME
init|=
literal|"urlScheme"
decl_stmt|;
comment|// The core size of the threadpool servicing requests
DECL|field|INIT_CORE_POOL_SIZE
specifier|static
specifier|final
name|String
name|INIT_CORE_POOL_SIZE
init|=
literal|"corePoolSize"
decl_stmt|;
comment|// The maximum size of the threadpool servicing requests
DECL|field|INIT_MAX_POOL_SIZE
specifier|static
specifier|final
name|String
name|INIT_MAX_POOL_SIZE
init|=
literal|"maximumPoolSize"
decl_stmt|;
comment|// The amount of time idle threads persist for in the queue, before being killed
DECL|field|MAX_THREAD_IDLE_TIME
specifier|static
specifier|final
name|String
name|MAX_THREAD_IDLE_TIME
init|=
literal|"maxThreadIdleTime"
decl_stmt|;
comment|// If the threadpool uses a backing queue, what is its maximum size (-1) to use direct handoff
DECL|field|INIT_SIZE_OF_QUEUE
specifier|static
specifier|final
name|String
name|INIT_SIZE_OF_QUEUE
init|=
literal|"sizeOfQueue"
decl_stmt|;
comment|// Configure if the threadpool favours fairness over throughput
DECL|field|INIT_FAIRNESS_POLICY
specifier|static
specifier|final
name|String
name|INIT_FAIRNESS_POLICY
init|=
literal|"fairnessPolicy"
decl_stmt|;
comment|// Turn on retries for certain IOExceptions, many of which can happen
comment|// due to connection pooling limitations / races
DECL|field|USE_RETRIES
specifier|static
specifier|final
name|String
name|USE_RETRIES
init|=
literal|"useRetries"
decl_stmt|;
comment|/**    * Get {@link ShardHandler} that uses the default http client.    */
annotation|@
name|Override
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|()
block|{
return|return
name|getShardHandler
argument_list|(
name|defaultClient
argument_list|)
return|;
block|}
comment|/**    * Get {@link ShardHandler} that uses custom http client.    */
DECL|method|getShardHandler
specifier|public
name|ShardHandler
name|getShardHandler
parameter_list|(
specifier|final
name|HttpClient
name|httpClient
parameter_list|)
block|{
return|return
operator|new
name|HttpShardHandler
argument_list|(
name|this
argument_list|,
name|httpClient
argument_list|)
return|;
block|}
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NamedList
name|args
init|=
name|info
operator|.
name|initArgs
decl_stmt|;
name|this
operator|.
name|soTimeout
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|,
name|soTimeout
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheme
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_URL_SCHEME
argument_list|,
literal|null
argument_list|,
name|sb
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|endsWith
argument_list|(
name|this
operator|.
name|scheme
argument_list|,
literal|"://"
argument_list|)
condition|)
block|{
name|this
operator|.
name|scheme
operator|=
name|StringUtils
operator|.
name|removeEnd
argument_list|(
name|this
operator|.
name|scheme
argument_list|,
literal|"://"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|connectionTimeout
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|,
name|connectionTimeout
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxConnectionsPerHost
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|,
name|maxConnectionsPerHost
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxConnections
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|,
name|maxConnections
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|corePoolSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_CORE_POOL_SIZE
argument_list|,
name|corePoolSize
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumPoolSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_MAX_POOL_SIZE
argument_list|,
name|maximumPoolSize
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|keepAliveTime
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|MAX_THREAD_IDLE_TIME
argument_list|,
name|keepAliveTime
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|queueSize
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_SIZE_OF_QUEUE
argument_list|,
name|queueSize
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|this
operator|.
name|accessPolicy
operator|=
name|getParameter
argument_list|(
name|args
argument_list|,
name|INIT_FAIRNESS_POLICY
argument_list|,
name|accessPolicy
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"created with {}"
argument_list|,
name|sb
argument_list|)
expr_stmt|;
comment|// magic sysprop to make tests reproducible: set by SolrTestCaseJ4.
name|String
name|v
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.shardhandler.randomSeed"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|setSeed
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|blockingQueue
init|=
operator|(
name|this
operator|.
name|queueSize
operator|==
operator|-
literal|1
operator|)
condition|?
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|accessPolicy
argument_list|)
else|:
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|this
operator|.
name|queueSize
argument_list|,
name|this
operator|.
name|accessPolicy
argument_list|)
decl_stmt|;
name|this
operator|.
name|commExecutor
operator|=
operator|new
name|ExecutorUtil
operator|.
name|MDCAwareThreadPoolExecutor
argument_list|(
name|this
operator|.
name|corePoolSize
argument_list|,
name|this
operator|.
name|maximumPoolSize
argument_list|,
name|this
operator|.
name|keepAliveTime
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|blockingQueue
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"httpShardExecutor"
argument_list|)
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|clientParams
init|=
name|getClientParams
argument_list|()
decl_stmt|;
name|httpRequestExecutor
operator|=
operator|new
name|InstrumentedHttpRequestExecutor
argument_list|()
expr_stmt|;
name|clientConnectionManager
operator|=
operator|new
name|InstrumentedPoolingHttpClientConnectionManager
argument_list|(
name|HttpClientUtil
operator|.
name|getSchemaRegisteryProvider
argument_list|()
operator|.
name|getSchemaRegistry
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
name|clientParams
argument_list|,
name|clientConnectionManager
argument_list|,
literal|false
argument_list|,
name|httpRequestExecutor
argument_list|)
expr_stmt|;
name|this
operator|.
name|loadbalancer
operator|=
name|createLoadbalancer
argument_list|(
name|defaultClient
argument_list|)
expr_stmt|;
block|}
DECL|method|getClientParams
specifier|protected
name|ModifiableSolrParams
name|getClientParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|clientParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|,
name|maxConnectionsPerHost
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|,
name|maxConnections
argument_list|)
expr_stmt|;
return|return
name|clientParams
return|;
block|}
DECL|method|getThreadPoolExecutor
specifier|protected
name|ExecutorService
name|getThreadPoolExecutor
parameter_list|()
block|{
return|return
name|this
operator|.
name|commExecutor
return|;
block|}
DECL|method|createLoadbalancer
specifier|protected
name|LBHttpSolrClient
name|createLoadbalancer
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|)
block|{
name|LBHttpSolrClient
name|client
init|=
operator|new
name|Builder
argument_list|()
operator|.
name|withHttpClient
argument_list|(
name|httpClient
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
name|connectionTimeout
argument_list|)
expr_stmt|;
name|client
operator|.
name|setSoTimeout
argument_list|(
name|soTimeout
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
DECL|method|getParameter
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|T
name|getParameter
parameter_list|(
name|NamedList
name|initArgs
parameter_list|,
name|String
name|configKey
parameter_list|,
name|T
name|defaultValue
parameter_list|,
name|StringBuilder
name|sb
parameter_list|)
block|{
name|T
name|toReturn
init|=
name|defaultValue
decl_stmt|;
if|if
condition|(
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|T
name|temp
init|=
operator|(
name|T
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|configKey
argument_list|)
decl_stmt|;
name|toReturn
operator|=
operator|(
name|temp
operator|!=
literal|null
operator|)
condition|?
name|temp
else|:
name|defaultValue
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|!=
literal|null
operator|&&
name|toReturn
operator|!=
literal|null
condition|)
name|sb
operator|.
name|append
argument_list|(
name|configKey
argument_list|)
operator|.
name|append
argument_list|(
literal|" : "
argument_list|)
operator|.
name|append
argument_list|(
name|toReturn
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
return|return
name|toReturn
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|commExecutor
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|loadbalancer
operator|!=
literal|null
condition|)
block|{
name|loadbalancer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|defaultClient
operator|!=
literal|null
condition|)
block|{
name|HttpClientUtil
operator|.
name|close
argument_list|(
name|defaultClient
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clientConnectionManager
operator|!=
literal|null
condition|)
block|{
name|clientConnectionManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Makes a request to one or more of the given urls, using the configured load balancer.    *    * @param req The solr search request that should be sent through the load balancer    * @param urls The list of solr server urls to load balance across    * @return The response from the request    */
DECL|method|makeLoadBalancedRequest
specifier|public
name|LBHttpSolrClient
operator|.
name|Rsp
name|makeLoadBalancedRequest
parameter_list|(
specifier|final
name|QueryRequest
name|req
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|urls
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
name|loadbalancer
operator|.
name|request
argument_list|(
operator|new
name|LBHttpSolrClient
operator|.
name|Req
argument_list|(
name|req
argument_list|,
name|urls
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a list of urls for the given shard.    *    * @param shard the urls for the shard, separated by '|'    * @return A list of valid urls (including protocol) that are replicas for the shard    */
DECL|method|buildURLList
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|buildURLList
parameter_list|(
name|String
name|shard
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|shard
argument_list|,
literal|"|"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// convert shard to URL
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|urls
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|urls
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|buildUrl
argument_list|(
name|urls
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|urls
return|;
block|}
comment|/**    * A distributed request is made via {@link LBHttpSolrClient} to the first live server in the URL list.    * This means it is just as likely to choose current host as any of the other hosts.    * This function makes sure that the cores of current host are always put first in the URL list.    * If all nodes prefer local-cores then a bad/heavily-loaded node will receive less requests from healthy nodes.    * This will help prevent a distributed deadlock or timeouts in all the healthy nodes due to one bad node.    */
DECL|class|IsOnPreferredHostComparator
specifier|private
class|class
name|IsOnPreferredHostComparator
implements|implements
name|Comparator
argument_list|<
name|Object
argument_list|>
block|{
DECL|field|preferredHostAddress
specifier|final
specifier|private
name|String
name|preferredHostAddress
decl_stmt|;
DECL|method|IsOnPreferredHostComparator
specifier|public
name|IsOnPreferredHostComparator
parameter_list|(
name|String
name|preferredHostAddress
parameter_list|)
block|{
name|this
operator|.
name|preferredHostAddress
operator|=
name|preferredHostAddress
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|left
parameter_list|,
name|Object
name|right
parameter_list|)
block|{
specifier|final
name|boolean
name|lhs
init|=
name|hasPrefix
argument_list|(
name|objectToString
argument_list|(
name|left
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|rhs
init|=
name|hasPrefix
argument_list|(
name|objectToString
argument_list|(
name|right
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|lhs
operator|!=
name|rhs
condition|)
block|{
if|if
condition|(
name|lhs
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|+
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
DECL|method|objectToString
specifier|private
name|String
name|objectToString
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|String
name|s
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|s
operator|=
operator|(
name|String
operator|)
name|o
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Replica
condition|)
block|{
name|s
operator|=
operator|(
operator|(
name|Replica
operator|)
name|o
operator|)
operator|.
name|getCoreUrl
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|method|hasPrefix
specifier|private
name|boolean
name|hasPrefix
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|startsWith
argument_list|(
name|preferredHostAddress
argument_list|)
return|;
block|}
block|}
DECL|method|getReplicaListTransformer
name|ReplicaListTransformer
name|getReplicaListTransformer
parameter_list|(
specifier|final
name|SolrQueryRequest
name|req
parameter_list|)
block|{
specifier|final
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|PREFER_LOCAL_SHARDS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
specifier|final
name|CoreDescriptor
name|coreDescriptor
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
specifier|final
name|ZkController
name|zkController
init|=
name|coreDescriptor
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
specifier|final
name|String
name|preferredHostAddress
init|=
operator|(
name|zkController
operator|!=
literal|null
operator|)
condition|?
name|zkController
operator|.
name|getBaseUrl
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|preferredHostAddress
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Couldn't determine current host address to prefer local shards"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
operator|new
name|ShufflingReplicaListTransformer
argument_list|(
name|r
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|transform
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|choices
parameter_list|)
block|{
if|if
condition|(
name|choices
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|super
operator|.
name|transform
argument_list|(
name|choices
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Trying to prefer local shard on {} among the choices: {}"
argument_list|,
name|preferredHostAddress
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|choices
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|choices
operator|.
name|sort
argument_list|(
operator|new
name|IsOnPreferredHostComparator
argument_list|(
name|preferredHostAddress
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Applied local shard preference for choices: {}"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|choices
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|;
block|}
block|}
return|return
name|shufflingReplicaListTransformer
return|;
block|}
comment|/**    * Creates a new completion service for use by a single set of distributed requests.    */
DECL|method|newCompletionService
specifier|public
name|CompletionService
name|newCompletionService
parameter_list|()
block|{
return|return
operator|new
name|ExecutorCompletionService
argument_list|<
name|ShardResponse
argument_list|>
argument_list|(
name|commExecutor
argument_list|)
return|;
block|}
comment|/**    * Rebuilds the URL replacing the URL scheme of the passed URL with the    * configured scheme replacement.If no scheme was configured, the passed URL's    * scheme is left alone.    */
DECL|method|buildUrl
specifier|private
name|String
name|buildUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
if|if
condition|(
operator|!
name|URLUtil
operator|.
name|hasScheme
argument_list|(
name|url
argument_list|)
condition|)
block|{
return|return
name|StringUtils
operator|.
name|defaultIfEmpty
argument_list|(
name|scheme
argument_list|,
name|DEFAULT_SCHEME
argument_list|)
operator|+
literal|"://"
operator|+
name|url
return|;
block|}
elseif|else
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|scheme
argument_list|)
condition|)
block|{
return|return
name|scheme
operator|+
literal|"://"
operator|+
name|URLUtil
operator|.
name|removeScheme
argument_list|(
name|url
argument_list|)
return|;
block|}
return|return
name|url
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
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
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
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
name|registry
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|metricNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|metricNames
operator|.
name|addAll
argument_list|(
name|clientConnectionManager
operator|.
name|initializeMetrics
argument_list|(
name|manager
argument_list|,
name|registry
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
name|metricNames
operator|.
name|addAll
argument_list|(
name|httpRequestExecutor
operator|.
name|initializeMetrics
argument_list|(
name|manager
argument_list|,
name|registry
argument_list|,
name|scope
argument_list|)
argument_list|)
expr_stmt|;
name|commExecutor
operator|=
name|MetricUtils
operator|.
name|instrumentedExecutorService
argument_list|(
name|commExecutor
argument_list|,
name|manager
operator|.
name|registry
argument_list|(
name|registry
argument_list|)
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"httpShardExecutor"
argument_list|,
name|scope
argument_list|,
literal|"threadPool"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|metricNames
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
literal|"Metrics tracked by HttpShardHandlerFactory for distributed query requests"
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|OTHER
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
literal|null
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
operator|new
name|URL
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

