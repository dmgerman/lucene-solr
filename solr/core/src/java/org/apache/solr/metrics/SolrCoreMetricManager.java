begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|NodeConfig
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
name|core
operator|.
name|SolrInfoMBean
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
comment|/**  * Responsible for collecting metrics from {@link SolrMetricProducer}'s  * and exposing metrics to {@link SolrMetricReporter}'s.  */
end_comment

begin_class
DECL|class|SolrCoreMetricManager
specifier|public
class|class
name|SolrCoreMetricManager
implements|implements
name|Closeable
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
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|metricManager
specifier|private
specifier|final
name|SolrMetricManager
name|metricManager
decl_stmt|;
DECL|field|registryName
specifier|private
name|String
name|registryName
decl_stmt|;
comment|/**    * Constructs a metric manager.    *    * @param core the metric manager's core    */
DECL|method|SolrCoreMetricManager
specifier|public
name|SolrCoreMetricManager
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
name|this
operator|.
name|metricManager
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getMetricManager
argument_list|()
expr_stmt|;
name|registryName
operator|=
name|createRegistryName
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Load reporters configured globally and specific to {@link org.apache.solr.core.SolrInfoMBean.Group#core}    * group or with a registry name specific to this core.    */
DECL|method|loadReporters
specifier|public
name|void
name|loadReporters
parameter_list|()
block|{
name|NodeConfig
name|nodeConfig
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|PluginInfo
index|[]
name|pluginInfos
init|=
name|nodeConfig
operator|.
name|getMetricReporterPlugins
argument_list|()
decl_stmt|;
name|metricManager
operator|.
name|loadReporters
argument_list|(
name|pluginInfos
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|,
name|SolrInfoMBean
operator|.
name|Group
operator|.
name|core
argument_list|,
name|registryName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure that metrics already collected that correspond to the old core name    * are carried over and will be used under the new core name.    * This method also reloads reporters so that they use the new core name.    */
DECL|method|afterCoreSetName
specifier|public
name|void
name|afterCoreSetName
parameter_list|()
block|{
name|String
name|oldRegistryName
init|=
name|registryName
decl_stmt|;
name|registryName
operator|=
name|createRegistryName
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldRegistryName
operator|.
name|equals
argument_list|(
name|registryName
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// close old reporters
name|metricManager
operator|.
name|closeReporters
argument_list|(
name|oldRegistryName
argument_list|)
expr_stmt|;
name|metricManager
operator|.
name|moveMetrics
argument_list|(
name|oldRegistryName
argument_list|,
name|registryName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// old registry is no longer used - we have moved the metrics
name|metricManager
operator|.
name|removeRegistry
argument_list|(
name|oldRegistryName
argument_list|)
expr_stmt|;
comment|// load reporters again, using the new core name
name|loadReporters
argument_list|()
expr_stmt|;
block|}
comment|/**    * Registers a mapping of name/metric's with the manager's metric registry.    *    * @param scope     the scope of the metrics to be registered (e.g. `/admin/ping`)    * @param producer  producer of metrics to be registered    */
DECL|method|registerMetricProducer
specifier|public
name|void
name|registerMetricProducer
parameter_list|(
name|String
name|scope
parameter_list|,
name|SolrMetricProducer
name|producer
parameter_list|)
block|{
if|if
condition|(
name|scope
operator|==
literal|null
operator|||
name|producer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"registerMetricProducer() called with illegal arguments: "
operator|+
literal|"scope = "
operator|+
name|scope
operator|+
literal|", producer = "
operator|+
name|producer
argument_list|)
throw|;
block|}
name|producer
operator|.
name|initializeMetrics
argument_list|(
name|metricManager
argument_list|,
name|getRegistryName
argument_list|()
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closes reporters specific to this core.    */
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
name|metricManager
operator|.
name|closeReporters
argument_list|(
name|getRegistryName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
return|return
name|core
return|;
block|}
comment|/**    * Retrieves the metric registry name of the manager.    *    * In order to make it easier for reporting tools to aggregate metrics from    * different cores that logically belong to a single collection we convert the    * core name into a dot-separated hierarchy of: collection name, shard name (with optional split)    * and replica name.    *    *<p>For example, when the core name looks like this but it's NOT a SolrCloud collection:    *<code>my_collection_shard1_1_replica1</code> then this will be used as the registry name (plus    * the required<code>solr.core</code> prefix). However,    * if this is a SolrCloud collection<code>my_collection</code> then the registry name will become    *<code>solr.core.my_collection.shard1_1.replica1</code>.</p>    *    *    * @return the metric registry name of the manager.    */
DECL|method|getRegistryName
specifier|public
name|String
name|getRegistryName
parameter_list|()
block|{
return|return
name|registryName
return|;
block|}
comment|/* package visibility for tests. */
DECL|method|createRegistryName
name|String
name|createRegistryName
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
if|if
condition|(
name|collectionName
operator|==
literal|null
operator|||
operator|(
name|collectionName
operator|!=
literal|null
operator|&&
operator|!
name|coreName
operator|.
name|startsWith
argument_list|(
name|collectionName
operator|+
literal|"_"
argument_list|)
operator|)
condition|)
block|{
comment|// single core, or unknown naming scheme
return|return
name|SolrMetricManager
operator|.
name|getRegistryName
argument_list|(
name|SolrInfoMBean
operator|.
name|Group
operator|.
name|core
argument_list|,
name|coreName
argument_list|)
return|;
block|}
comment|// split "collection1_shard1_1_replica1" into parts
name|String
name|str
init|=
name|coreName
operator|.
name|substring
argument_list|(
name|collectionName
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|shard
decl_stmt|;
name|String
name|replica
init|=
literal|null
decl_stmt|;
name|int
name|pos
init|=
name|str
operator|.
name|lastIndexOf
argument_list|(
literal|"_replica"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
comment|// ?? no _replicaN part ??
name|shard
operator|=
name|str
expr_stmt|;
block|}
else|else
block|{
name|shard
operator|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|replica
operator|=
name|str
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|SolrMetricManager
operator|.
name|getRegistryName
argument_list|(
name|SolrInfoMBean
operator|.
name|Group
operator|.
name|core
argument_list|,
name|collectionName
argument_list|,
name|shard
argument_list|,
name|replica
argument_list|)
return|;
block|}
block|}
end_class

end_unit

