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
name|concurrent
operator|.
name|ExecutorService
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

begin_class
DECL|class|UpdateShardHandler
specifier|public
class|class
name|UpdateShardHandler
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
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareCachedThreadPool
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"recoveryExecutor"
argument_list|)
argument_list|)
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
name|PoolingHttpClientConnectionManager
name|clientConnectionManager
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
name|PoolingHttpClientConnectionManager
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
name|log
operator|.
name|info
argument_list|(
literal|"Creating UpdateShardHandler HTTP client with params: {}"
argument_list|,
name|clientParams
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
argument_list|)
expr_stmt|;
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
comment|/**    * In general, RecoveryStrategy threads do not do disk IO, but they open and close SolrCores    * in async threads, amoung other things, and can trigger disk IO, so we use this alternate     * executor rather than the 'updateExecutor', which is interrupted on shutdown.    *     * @return executor for {@link RecoveryStrategy} thread which will not be interrupted on close.    */
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
comment|// we interrupt on purpose here, but this exectuor should not run threads that do disk IO!
name|ExecutorUtil
operator|.
name|shutdownWithInterruptAndAwaitTermination
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

