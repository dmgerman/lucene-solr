begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics.reporters.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|reporters
operator|.
name|solr
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
name|ArrayList
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
name|List
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
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
name|common
operator|.
name|cloud
operator|.
name|ClusterState
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
name|DocCollection
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
name|admin
operator|.
name|MetricsCollectorHandler
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
name|SolrMetricReporter
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

begin_comment
comment|/**  * This class reports selected metrics from replicas to shard leader.  *<p>The following configuration properties are supported:</p>  *<ul>  *<li>handler - (optional str) handler path where reports are sent. Default is  *   {@link MetricsCollectorHandler#HANDLER_PATH}.</li>  *<li>period - (optional int) how often reports are sent, in seconds. Default is 60. Setting this  *   to 0 disables the reporter.</li>  *<li>filter - (optional multiple str) regex expression(s) matching selected metrics to be reported.</li>  *</ul>  * NOTE: this reporter uses predefined "replica" group, and it's always created even if explicit configuration  * is missing. Default configuration uses filters defined in {@link #DEFAULT_FILTERS}.  *<p>Example configuration:</p>  *<pre>  *&lt;reporter name="test" group="replica"&gt;  *&lt;int name="period"&gt;11&lt;/int&gt;  *&lt;str name="filter"&gt;UPDATE\./update/.*requests&lt;/str&gt;  *&lt;str name="filter"&gt;QUERY\./select.*requests&lt;/str&gt;  *&lt;/reporter&gt;  *</pre>  */
end_comment

begin_class
DECL|class|SolrShardReporter
specifier|public
class|class
name|SolrShardReporter
extends|extends
name|SolrMetricReporter
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
DECL|field|DEFAULT_FILTERS
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|DEFAULT_FILTERS
init|=
operator|new
name|ArrayList
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"TLOG.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"CORE\\.fs.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"REPLICATION.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"INDEX\\.flush.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"INDEX\\.merge\\.major.*"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"UPDATE\\./update/.*requests"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"QUERY\\./select.*requests"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|field|handler
specifier|private
name|String
name|handler
init|=
name|MetricsCollectorHandler
operator|.
name|HANDLER_PATH
decl_stmt|;
DECL|field|period
specifier|private
name|int
name|period
init|=
name|SolrMetricManager
operator|.
name|DEFAULT_CLOUD_REPORTER_PERIOD
decl_stmt|;
DECL|field|filters
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reporter
specifier|private
name|SolrReporter
name|reporter
decl_stmt|;
comment|/**    * Create a reporter for metrics managed in a named registry.    *    * @param metricManager metric manager    * @param registryName  registry to use, one of registries managed by    *                      {@link SolrMetricManager}    */
DECL|method|SolrShardReporter
specifier|public
name|SolrShardReporter
parameter_list|(
name|SolrMetricManager
name|metricManager
parameter_list|,
name|String
name|registryName
parameter_list|)
block|{
name|super
argument_list|(
name|metricManager
argument_list|,
name|registryName
argument_list|)
expr_stmt|;
block|}
DECL|method|setHandler
specifier|public
name|void
name|setHandler
parameter_list|(
name|String
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
DECL|method|setPeriod
specifier|public
name|void
name|setPeriod
parameter_list|(
name|int
name|period
parameter_list|)
block|{
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
block|}
DECL|method|setFilter
specifier|public
name|void
name|setFilter
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|filterConfig
parameter_list|)
block|{
if|if
condition|(
name|filterConfig
operator|==
literal|null
operator|||
name|filterConfig
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|filters
operator|.
name|addAll
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|setFilter
specifier|public
name|void
name|setFilter
parameter_list|(
name|String
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|!=
literal|null
operator|&&
operator|!
name|filter
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|filters
operator|.
name|add
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
block|}
comment|// for unit tests
DECL|method|getPeriod
name|int
name|getPeriod
parameter_list|()
block|{
return|return
name|period
return|;
block|}
annotation|@
name|Override
DECL|method|doInit
specifier|protected
name|void
name|doInit
parameter_list|()
block|{
if|if
condition|(
name|filters
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|filters
operator|=
name|DEFAULT_FILTERS
expr_stmt|;
block|}
comment|// start in setCore(SolrCore) when core is available
block|}
annotation|@
name|Override
DECL|method|validate
specifier|protected
name|void
name|validate
parameter_list|()
throws|throws
name|IllegalStateException
block|{
comment|// Nothing to validate
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|reporter
operator|!=
literal|null
condition|)
block|{
name|reporter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setCore
specifier|public
name|void
name|setCore
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|reporter
operator|!=
literal|null
condition|)
block|{
name|reporter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|enabled
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Reporter disabled for registry "
operator|+
name|registryName
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// not a cloud core
name|log
operator|.
name|warn
argument_list|(
literal|"Not initializing shard reporter for non-cloud core "
operator|+
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|period
operator|<
literal|1
condition|)
block|{
comment|// don't start it
name|log
operator|.
name|warn
argument_list|(
literal|"period="
operator|+
name|period
operator|+
literal|", not starting shard reporter "
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// our id is coreNodeName
name|String
name|id
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
decl_stmt|;
comment|// target registry is the leaderRegistryName
name|String
name|groupId
init|=
name|core
operator|.
name|getCoreMetricManager
argument_list|()
operator|.
name|getLeaderRegistryName
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupId
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No leaderRegistryName for core "
operator|+
name|core
operator|+
literal|", not starting the reporter..."
argument_list|)
expr_stmt|;
return|return;
block|}
name|SolrReporter
operator|.
name|Report
name|spec
init|=
operator|new
name|SolrReporter
operator|.
name|Report
argument_list|(
name|groupId
argument_list|,
literal|null
argument_list|,
name|registryName
argument_list|,
name|filters
argument_list|)
decl_stmt|;
name|reporter
operator|=
name|SolrReporter
operator|.
name|Builder
operator|.
name|forReports
argument_list|(
name|metricManager
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|spec
argument_list|)
argument_list|)
operator|.
name|convertRatesTo
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|convertDurationsTo
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|withHandler
argument_list|(
name|handler
argument_list|)
operator|.
name|withReporterId
argument_list|(
name|id
argument_list|)
operator|.
name|setCompact
argument_list|(
literal|true
argument_list|)
operator|.
name|cloudClient
argument_list|(
literal|false
argument_list|)
comment|// we want to send reports specifically to a selected leader instance
operator|.
name|skipAggregateValues
argument_list|(
literal|true
argument_list|)
comment|// we don't want to transport details of aggregates
operator|.
name|skipHistograms
argument_list|(
literal|true
argument_list|)
comment|// we don't want to transport histograms
operator|.
name|build
argument_list|(
name|core
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getUpdateShardHandler
argument_list|()
operator|.
name|getHttpClient
argument_list|()
argument_list|,
operator|new
name|LeaderUrlSupplier
argument_list|(
name|core
argument_list|)
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|start
argument_list|(
name|period
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
DECL|class|LeaderUrlSupplier
specifier|private
specifier|static
class|class
name|LeaderUrlSupplier
implements|implements
name|Supplier
argument_list|<
name|String
argument_list|>
block|{
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|method|LeaderUrlSupplier
name|LeaderUrlSupplier
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|String
name|get
parameter_list|()
block|{
name|CloudDescriptor
name|cd
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
if|if
condition|(
name|cd
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ClusterState
name|state
init|=
name|core
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|collection
init|=
name|state
operator|.
name|getCollection
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
name|collection
operator|.
name|getLeader
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getShardId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No leader for "
operator|+
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|String
name|baseUrl
init|=
name|replica
operator|.
name|getStr
argument_list|(
literal|"base_url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseUrl
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No base_url for replica "
operator|+
name|replica
argument_list|)
expr_stmt|;
block|}
return|return
name|baseUrl
return|;
block|}
block|}
block|}
end_class

end_unit

