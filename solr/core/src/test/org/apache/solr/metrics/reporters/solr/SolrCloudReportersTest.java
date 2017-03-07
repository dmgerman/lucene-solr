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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
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
name|io
operator|.
name|IOUtils
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
name|CollectionAdminRequest
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
name|SolrCloudTestCase
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
name|metrics
operator|.
name|AggregateMetric
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
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SolrCloudReportersTest
specifier|public
class|class
name|SolrCloudReportersTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|leaderRegistries
name|int
name|leaderRegistries
decl_stmt|;
DECL|field|clusterRegistries
name|int
name|clusterRegistries
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|configureDummyCluster
specifier|public
specifier|static
name|void
name|configureDummyCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|0
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|closePreviousCluster
specifier|public
name|void
name|closePreviousCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|shutdownCluster
argument_list|()
expr_stmt|;
name|leaderRegistries
operator|=
literal|0
expr_stmt|;
name|clusterRegistries
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExplicitConfiguration
specifier|public
name|void
name|testExplicitConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|solrXml
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|SolrCloudReportersTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/solr/solr-solrreporter.xml"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|configureCluster
argument_list|(
literal|2
argument_list|)
operator|.
name|withSolrXml
argument_list|(
name|solrXml
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|uploadConfigSet
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"configsets"
argument_list|,
literal|"minimal"
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ZK: "
operator|+
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"test_collection"
argument_list|,
literal|"test"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|4
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected test_collection with 2 shards and 2 replicas"
argument_list|,
literal|"test_collection"
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|forEach
argument_list|(
name|jetty
lambda|->
block|{
name|CoreContainer
name|cc
init|=
name|jetty
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
comment|// verify registry names
for|for
control|(
name|String
name|name
range|:
name|cc
operator|.
name|getCoreNames
argument_list|()
control|)
block|{
name|SolrCore
name|core
init|=
name|cc
operator|.
name|getCore
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|registryName
init|=
name|core
operator|.
name|getCoreMetricManager
argument_list|()
operator|.
name|getRegistryName
argument_list|()
decl_stmt|;
name|String
name|leaderRegistryName
init|=
name|core
operator|.
name|getCoreMetricManager
argument_list|()
operator|.
name|getLeaderRegistryName
argument_list|()
decl_stmt|;
name|String
name|coreName
init|=
name|core
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|collectionName
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|String
name|coreNodeName
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
name|String
name|replicaName
init|=
name|coreName
operator|.
name|split
argument_list|(
literal|"_"
argument_list|)
index|[
literal|3
index|]
decl_stmt|;
name|String
name|shardId
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"solr.core."
operator|+
name|collectionName
operator|+
literal|"."
operator|+
name|shardId
operator|+
literal|"."
operator|+
name|replicaName
argument_list|,
name|registryName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solr.collection."
operator|+
name|collectionName
operator|+
literal|"."
operator|+
name|shardId
operator|+
literal|".leader"
argument_list|,
name|leaderRegistryName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|SolrMetricManager
name|metricManager
init|=
name|cc
operator|.
name|getMetricManager
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrMetricReporter
argument_list|>
name|reporters
init|=
name|metricManager
operator|.
name|getReporters
argument_list|(
literal|"solr.cluster"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|reporters
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|.
name|toString
argument_list|()
argument_list|,
name|reporter
operator|instanceof
name|SolrClusterReporter
argument_list|)
expr_stmt|;
name|SolrClusterReporter
name|sor
init|=
operator|(
name|SolrClusterReporter
operator|)
name|reporter
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|sor
operator|.
name|getPeriod
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|registryName
range|:
name|metricManager
operator|.
name|registryNames
argument_list|(
literal|".*\\.shard[0-9]\\.replica.*"
argument_list|)
control|)
block|{
name|reporters
operator|=
name|metricManager
operator|.
name|getReporters
argument_list|(
name|registryName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reporters
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|reporter
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|reporters
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
name|reporter
operator|=
name|reporters
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|.
name|toString
argument_list|()
argument_list|,
name|reporter
operator|instanceof
name|SolrShardReporter
argument_list|)
expr_stmt|;
name|SolrShardReporter
name|srr
init|=
operator|(
name|SolrShardReporter
operator|)
name|reporter
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|srr
operator|.
name|getPeriod
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|registryName
range|:
name|metricManager
operator|.
name|registryNames
argument_list|(
literal|".*\\.leader"
argument_list|)
control|)
block|{
name|leaderRegistries
operator|++
expr_stmt|;
name|reporters
operator|=
name|metricManager
operator|.
name|getReporters
argument_list|(
name|registryName
argument_list|)
expr_stmt|;
comment|// no reporters registered for leader registry
name|assertEquals
argument_list|(
name|reporters
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify specific metrics
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|metrics
init|=
name|metricManager
operator|.
name|registry
argument_list|(
name|registryName
argument_list|)
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|String
name|key
init|=
literal|"QUERY./select.requests.count"
decl_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|instanceof
name|AggregateMetric
argument_list|)
expr_stmt|;
name|key
operator|=
literal|"UPDATE./update/json.requests.count"
expr_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|instanceof
name|AggregateMetric
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|metricManager
operator|.
name|registryNames
argument_list|()
operator|.
name|contains
argument_list|(
literal|"solr.cluster"
argument_list|)
condition|)
block|{
name|clusterRegistries
operator|++
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|metrics
init|=
name|metricManager
operator|.
name|registry
argument_list|(
literal|"solr.cluster"
argument_list|)
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|String
name|key
init|=
literal|"jvm.memory.heap.init.value"
decl_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|instanceof
name|AggregateMetric
argument_list|)
expr_stmt|;
name|key
operator|=
literal|"leader.test_collection.shard1.UPDATE./update/json.requests.count.max"
expr_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|key
argument_list|,
name|metrics
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|instanceof
name|AggregateMetric
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"leaderRegistries"
argument_list|,
literal|2
argument_list|,
name|leaderRegistries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"clusterRegistries"
argument_list|,
literal|1
argument_list|,
name|clusterRegistries
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultPlugins
specifier|public
name|void
name|testDefaultPlugins
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|solrXml
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|SolrCloudReportersTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/solr/solr.xml"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|configureCluster
argument_list|(
literal|2
argument_list|)
operator|.
name|withSolrXml
argument_list|(
name|solrXml
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|uploadConfigSet
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|TEST_PATH
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"configsets"
argument_list|,
literal|"minimal"
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ZK: "
operator|+
name|cluster
operator|.
name|getZkServer
argument_list|()
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
literal|"test_collection"
argument_list|,
literal|"test"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|4
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected test_collection with 2 shards and 2 replicas"
argument_list|,
literal|"test_collection"
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|forEach
argument_list|(
name|jetty
lambda|->
block|{
name|CoreContainer
name|cc
init|=
name|jetty
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|SolrMetricManager
name|metricManager
init|=
name|cc
operator|.
name|getMetricManager
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrMetricReporter
argument_list|>
name|reporters
init|=
name|metricManager
operator|.
name|getReporters
argument_list|(
literal|"solr.cluster"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|reporters
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|registryName
range|:
name|metricManager
operator|.
name|registryNames
argument_list|(
literal|".*\\.shard[0-9]\\.replica.*"
argument_list|)
control|)
block|{
name|reporters
operator|=
name|metricManager
operator|.
name|getReporters
argument_list|(
name|registryName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reporters
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

