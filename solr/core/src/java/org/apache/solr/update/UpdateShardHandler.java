begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|Set
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
name|ThreadFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingHttpClientConnectionManager
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
name|cloud
operator|.
name|RecoveryStrategy
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
name|SolrjNamedThreadFactory
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
name|SolrInfoBean
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
name|util
operator|.
name|stats
operator|.
name|HttpClientMetricNameStrategy
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
import|import static
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
operator|.
name|KNOWN_METRIC_NAME_STRATEGIES
import|;
end_import

begin_class
DECL|class|UpdateShardHandler
specifier|public
class|class
name|UpdateShardHandler
implements|implements
name|SolrMetricProducer
implements|,
name|SolrInfoBean
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
comment|/*    * A downside to configuring an upper bound will be big update reorders (when that upper bound is hit)    * and then undetected shard inconsistency as a result.    * This update executor is used for different things too... both update streams (which may be very long lived)    * and control messages (peersync? LIR?) and could lead to starvation if limited.    * Therefore this thread pool is left unbounded. See SOLR-8205    */
DECL|field|updateExecutor
specifier|private
name|ExecutorService
name|updateExecutor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"updateExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|recoveryExecutor
specifier|private
name|ExecutorService
name|recoveryExecutor
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|CloseableHttpClient
name|client
decl_stmt|;
DECL|field|clientConnectionManager
specifier|private
specifier|final
name|InstrumentedPoolingHttpClientConnectionManager
name|clientConnectionManager
decl_stmt|;
DECL|field|httpRequestExecutor
specifier|private
specifier|final
name|InstrumentedHttpRequestExecutor
name|httpRequestExecutor
decl_stmt|;
DECL|field|metricNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|metricNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|registry
specifier|private
name|MetricRegistry
name|registry
decl_stmt|;
DECL|method|UpdateShardHandler
specifier|public
name|UpdateShardHandler
parameter_list|(
name|UpdateShardHandlerConfig
name|cfg
parameter_list|)
block|{
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
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|clientConnectionManager
operator|.
name|setMaxTotal
argument_list|(
name|cfg
operator|.
name|getMaxUpdateConnections
argument_list|()
argument_list|)
expr_stmt|;
name|clientConnectionManager
operator|.
name|setDefaultMaxPerRoute
argument_list|(
name|cfg
operator|.
name|getMaxUpdateConnectionsPerHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ModifiableSolrParams
name|clientParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_SO_TIMEOUT
argument_list|,
name|cfg
operator|.
name|getDistributedSocketTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_CONNECTION_TIMEOUT
argument_list|,
name|cfg
operator|.
name|getDistributedConnectionTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|HttpClientMetricNameStrategy
name|metricNameStrategy
init|=
name|KNOWN_METRIC_NAME_STRATEGIES
operator|.
name|get
argument_list|(
name|UpdateShardHandlerConfig
operator|.
name|DEFAULT_METRICNAMESTRATEGY
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|metricNameStrategy
operator|=
name|KNOWN_METRIC_NAME_STRATEGIES
operator|.
name|get
argument_list|(
name|cfg
operator|.
name|getMetricNameStrategy
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|metricNameStrategy
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
name|SERVER_ERROR
argument_list|,
literal|"Unknown metricNameStrategy: "
operator|+
name|cfg
operator|.
name|getMetricNameStrategy
argument_list|()
operator|+
literal|" found. Must be one of: "
operator|+
name|KNOWN_METRIC_NAME_STRATEGIES
operator|.
name|keySet
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|httpRequestExecutor
operator|=
operator|new
name|InstrumentedHttpRequestExecutor
argument_list|(
name|metricNameStrategy
argument_list|)
expr_stmt|;
name|client
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
comment|// following is done only for logging complete configuration.
comment|// The maxConnections and maxConnectionsPerHost have already been specified on the connection manager
if|if
condition|(
name|cfg
operator|!=
literal|null
condition|)
block|{
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS
argument_list|,
name|cfg
operator|.
name|getMaxUpdateConnections
argument_list|()
argument_list|)
expr_stmt|;
name|clientParams
operator|.
name|set
argument_list|(
name|HttpClientUtil
operator|.
name|PROP_MAX_CONNECTIONS_PER_HOST
argument_list|,
name|cfg
operator|.
name|getMaxUpdateConnectionsPerHost
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Created UpdateShardHandler HTTP client with params: {}"
argument_list|,
name|clientParams
argument_list|)
expr_stmt|;
name|ThreadFactory
name|recoveryThreadFactory
init|=
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"recoveryExecutor"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfg
operator|!=
literal|null
operator|&&
name|cfg
operator|.
name|getMaxRecoveryThreads
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Creating recoveryExecutor with pool size {}"
argument_list|,
name|cfg
operator|.
name|getMaxRecoveryThreads
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryExecutor
operator|=
name|ExecutorUtil
operator|.
name|newMDCAwareFixedThreadPool
argument_list|(
name|cfg
operator|.
name|getMaxRecoveryThreads
argument_list|()
argument_list|,
name|recoveryThreadFactory
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Creating recoveryExecutor with unbounded pool"
argument_list|)
expr_stmt|;
name|recoveryExecutor
operator|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
name|recoveryThreadFactory
argument_list|)
expr_stmt|;
block|}
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
DECL|method|initializeMetrics
specifier|public
name|void
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
name|registry
operator|=
name|manager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
expr_stmt|;
name|String
name|expandedScope
init|=
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
name|scope
argument_list|,
name|getCategory
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|clientConnectionManager
operator|.
name|initializeMetrics
argument_list|(
name|manager
argument_list|,
name|registryName
argument_list|,
name|expandedScope
argument_list|)
expr_stmt|;
name|httpRequestExecutor
operator|.
name|initializeMetrics
argument_list|(
name|manager
argument_list|,
name|registryName
argument_list|,
name|expandedScope
argument_list|)
expr_stmt|;
name|updateExecutor
operator|=
name|MetricUtils
operator|.
name|instrumentedExecutorService
argument_list|(
name|updateExecutor
argument_list|,
name|this
argument_list|,
name|registry
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"updateExecutor"
argument_list|,
name|expandedScope
argument_list|,
literal|"threadPool"
argument_list|)
argument_list|)
expr_stmt|;
name|recoveryExecutor
operator|=
name|MetricUtils
operator|.
name|instrumentedExecutorService
argument_list|(
name|recoveryExecutor
argument_list|,
name|this
argument_list|,
name|registry
argument_list|,
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
literal|"recoveryExecutor"
argument_list|,
name|expandedScope
argument_list|,
literal|"threadPool"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Metrics tracked by UpdateShardHandler related to distributed updates and recovery"
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
name|UPDATE
return|;
block|}
annotation|@
name|Override
DECL|method|getMetricNames
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getMetricNames
parameter_list|()
block|{
return|return
name|metricNames
return|;
block|}
annotation|@
name|Override
DECL|method|getMetricRegistry
specifier|public
name|MetricRegistry
name|getMetricRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
DECL|method|getHttpClient
specifier|public
name|HttpClient
name|getHttpClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
comment|/**    * This method returns an executor that is not meant for disk IO and that will    * be interrupted on shutdown.    *     * @return an executor for update related activities that do not do disk IO.    */
DECL|method|getUpdateExecutor
specifier|public
name|ExecutorService
name|getUpdateExecutor
parameter_list|()
block|{
return|return
name|updateExecutor
return|;
block|}
DECL|method|getConnectionManager
specifier|public
name|PoolingHttpClientConnectionManager
name|getConnectionManager
parameter_list|()
block|{
return|return
name|clientConnectionManager
return|;
block|}
comment|/**    * In general, RecoveryStrategy threads do not do disk IO, but they open and close SolrCores    * in async threads, among other things, and can trigger disk IO, so we use this alternate     * executor rather than the 'updateExecutor', which is interrupted on shutdown.    *     * @return executor for {@link RecoveryStrategy} thread which will not be interrupted on close.    */
DECL|method|getRecoveryExecutor
specifier|public
name|ExecutorService
name|getRecoveryExecutor
parameter_list|()
block|{
return|return
name|recoveryExecutor
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
comment|// do not interrupt, do not interrupt
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|updateExecutor
argument_list|)
expr_stmt|;
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|recoveryExecutor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|HttpClientUtil
operator|.
name|close
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|clientConnectionManager
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

