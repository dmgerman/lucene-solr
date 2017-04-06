begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|hdfs
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
name|Map
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Nightly
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakFilters
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
name|Counter
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
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|embedded
operator|.
name|JettySolrRunner
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
name|CloudSolrClient
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
name|HttpSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CoreAdminRequest
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
name|CoreStatus
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
name|response
operator|.
name|CoreAdminResponse
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
name|CollectionsAPIDistributedZkTest
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
name|cloud
operator|.
name|Slice
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
name|util
operator|.
name|BadHdfsThreadsFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_class
annotation|@
name|Slow
annotation|@
name|Nightly
annotation|@
name|ThreadLeakFilters
argument_list|(
name|defaultFilters
operator|=
literal|true
argument_list|,
name|filters
operator|=
block|{
name|BadHdfsThreadsFilter
operator|.
name|class
comment|// hdfs currently leaks thread(s)
block|}
argument_list|)
DECL|class|HdfsCollectionsAPIDistributedZkTest
specifier|public
class|class
name|HdfsCollectionsAPIDistributedZkTest
extends|extends
name|CollectionsAPIDistributedZkTest
block|{
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.blockcache.blocksperbank"
argument_list|,
literal|"512"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.hdfs.numdatanodes"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|()
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|configset
argument_list|(
literal|"cloud-hdfs"
argument_list|)
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|configset
argument_list|(
literal|"cloud-hdfs"
argument_list|)
argument_list|,
literal|"conf2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|,
name|HdfsTestUtil
operator|.
name|getDataDir
argument_list|(
name|dfsCluster
argument_list|,
literal|"data"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// need to close before the MiniDFSCluster
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.blockcache.blocksperbank"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.hdfs.numdatanodes"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.hdfs.home"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|moveReplicaTest
specifier|public
name|void
name|moveReplicaTest
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|waitForAllNodes
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|String
name|coll
init|=
literal|"movereplicatest_coll"
decl_stmt|;
name|CloudSolrClient
name|cloudClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
init|=
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|create
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|create
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|cloudClient
operator|.
name|add
argument_list|(
name|coll
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|(
name|coll
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|coll
argument_list|)
operator|.
name|getSlices
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|slices
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|Slice
name|slice
init|=
literal|null
decl_stmt|;
name|Replica
name|replica
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Slice
name|s
range|:
name|slices
control|)
block|{
name|slice
operator|=
name|s
expr_stmt|;
for|for
control|(
name|Replica
name|r
range|:
name|s
operator|.
name|getReplicas
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getLeader
argument_list|()
operator|!=
name|r
condition|)
block|{
name|replica
operator|=
name|r
expr_stmt|;
block|}
block|}
block|}
name|String
name|dataDir
init|=
name|getDataDir
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|liveNodes
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|targetNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|liveNodes
control|)
block|{
if|if
condition|(
operator|!
name|replica
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|targetNode
operator|=
name|node
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
name|targetNode
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|MoveReplica
name|moveReplica
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|MoveReplica
argument_list|(
name|coll
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|targetNode
argument_list|)
decl_stmt|;
name|moveReplica
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|checkNumOfCores
argument_list|(
name|cloudClient
argument_list|,
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkNumOfCores
argument_list|(
name|cloudClient
argument_list|,
name|targetNode
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Wait for recovery finish failed"
argument_list|,
name|coll
argument_list|,
name|clusterShape
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|slice
operator|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|coll
argument_list|)
operator|.
name|getSlice
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Replica
name|newReplica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
if|if
condition|(
name|getDataDir
argument_list|(
name|newReplica
argument_list|)
operator|.
name|equals
argument_list|(
name|dataDir
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|found
argument_list|)
expr_stmt|;
comment|// data dir is reused so replication will be skipped
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
control|)
block|{
name|SolrMetricManager
name|manager
init|=
name|jetty
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getMetricManager
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|registryNames
init|=
name|manager
operator|.
name|registryNames
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|s
lambda|->
name|s
operator|.
name|startsWith
argument_list|(
literal|"solr.core."
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|registry
range|:
name|registryNames
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Metric
argument_list|>
name|metrics
init|=
name|manager
operator|.
name|registry
argument_list|(
name|registry
argument_list|)
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|Counter
name|counter
init|=
operator|(
name|Counter
operator|)
name|metrics
operator|.
name|get
argument_list|(
literal|"REPLICATION./replication.requests"
argument_list|)
decl_stmt|;
if|if
condition|(
name|counter
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|counter
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|checkNumOfCores
specifier|private
name|void
name|checkNumOfCores
parameter_list|(
name|CloudSolrClient
name|cloudClient
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|int
name|expectedCores
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|assertEquals
argument_list|(
name|nodeName
operator|+
literal|" does not have expected number of cores"
argument_list|,
name|expectedCores
argument_list|,
name|getNumOfCores
argument_list|(
name|cloudClient
argument_list|,
name|nodeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumOfCores
specifier|private
name|int
name|getNumOfCores
parameter_list|(
name|CloudSolrClient
name|cloudClient
parameter_list|,
name|String
name|nodeName
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
try|try
init|(
name|HttpSolrClient
name|coreclient
init|=
name|getHttpSolrClient
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|nodeName
argument_list|)
argument_list|)
init|)
block|{
name|CoreAdminResponse
name|status
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
literal|null
argument_list|,
name|coreclient
argument_list|)
decl_stmt|;
return|return
name|status
operator|.
name|getCoreStatus
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
DECL|method|getDataDir
specifier|private
name|String
name|getDataDir
parameter_list|(
name|Replica
name|replica
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
try|try
init|(
name|HttpSolrClient
name|coreclient
init|=
name|getHttpSolrClient
argument_list|(
name|replica
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
init|)
block|{
name|CoreStatus
name|status
init|=
name|CoreAdminRequest
operator|.
name|getCoreStatus
argument_list|(
name|replica
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|coreclient
argument_list|)
decl_stmt|;
return|return
name|status
operator|.
name|getDataDirectory
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

