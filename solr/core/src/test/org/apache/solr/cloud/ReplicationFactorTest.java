begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|ArrayList
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
name|Locale
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|UpdateRequest
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
name|CollectionAdminResponse
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
name|SolrInputDocument
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
name|ZkCoreNodeProps
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
name|junit
operator|.
name|Test
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
comment|/**  * Tests a client application's ability to get replication factor  * information back from the cluster after an add or update.  */
end_comment

begin_class
annotation|@
name|Slow
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
annotation|@
name|LuceneTestCase
operator|.
name|BadApple
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-6944"
argument_list|)
DECL|class|ReplicationFactorTest
specifier|public
class|class
name|ReplicationFactorTest
extends|extends
name|AbstractFullDistribZkTestBase
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
DECL|method|ReplicationFactorTest
specifier|public
name|ReplicationFactorTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|3
expr_stmt|;
name|fixShardCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
comment|/**    * Overrides the parent implementation so that we can configure a socket proxy    * to sit infront of each Jetty server, which gives us the ability to simulate    * network partitions without having to fuss with IPTables (which is not very    * cross platform friendly).    */
annotation|@
name|Override
DECL|method|createJetty
specifier|public
name|JettySolrRunner
name|createJetty
parameter_list|(
name|File
name|solrHome
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|String
name|shardList
parameter_list|,
name|String
name|solrConfigOverride
parameter_list|,
name|String
name|schemaOverride
parameter_list|,
name|Replica
operator|.
name|Type
name|replicaType
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createProxiedJetty
argument_list|(
name|solrHome
argument_list|,
name|dataDir
argument_list|,
name|shardList
argument_list|,
name|solrConfigOverride
argument_list|,
name|schemaOverride
argument_list|,
name|replicaType
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"replication factor test running"
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// test a 1x3 collection
name|log
operator|.
name|info
argument_list|(
literal|"Testing replication factor handling for repfacttest_c8n_1x3"
argument_list|)
expr_stmt|;
name|testRf3
argument_list|()
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// test handling when not using direct updates
name|log
operator|.
name|info
argument_list|(
literal|"Now testing replication factor handling for repfacttest_c8n_2x2"
argument_list|)
expr_stmt|;
name|testRf2NotUsingDirectUpdates
argument_list|()
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"replication factor testing complete! final clusterState is: "
operator|+
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRf2NotUsingDirectUpdates
specifier|protected
name|void
name|testRf2NotUsingDirectUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numShards
init|=
literal|2
decl_stmt|;
name|int
name|replicationFactor
init|=
literal|2
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
literal|1
decl_stmt|;
name|String
name|testCollectionName
init|=
literal|"repfacttest_c8n_2x2"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|int
name|minRf
init|=
literal|2
decl_stmt|;
name|CollectionAdminResponse
name|resp
init|=
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
name|maxShardsPerNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"failure"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|resp
operator|=
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
name|maxShardsPerNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|resp
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"failure"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Could not create "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
block|}
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected active 1 replicas for "
operator|+
name|testCollectionName
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
literal|10
argument_list|)
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
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|i
argument_list|)
expr_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// send directly to the leader using HttpSolrServer instead of CloudSolrServer (to test support for non-direct updates)
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|setParam
argument_list|(
name|UpdateRequest
operator|.
name|MIN_REPFACT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|minRf
argument_list|)
argument_list|)
expr_stmt|;
name|up
operator|.
name|add
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|Replica
name|leader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|sendNonDirectUpdateRequestReplicaWithRetry
argument_list|(
name|leader
argument_list|,
name|up
argument_list|,
literal|2
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|sendNonDirectUpdateRequestReplicaWithRetry
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|up
argument_list|,
literal|2
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// so now kill the replica of shard2 and verify the achieved rf is only 1
name|List
argument_list|<
name|Replica
argument_list|>
name|shard2Replicas
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard2"
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected active 1 replicas for "
operator|+
name|testCollectionName
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|getProxyForReplica
argument_list|(
name|shard2Replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// shard1 will have rf=2 but shard2 will only have rf=1
name|sendNonDirectUpdateRequestReplicaWithRetry
argument_list|(
name|leader
argument_list|,
name|up
argument_list|,
literal|1
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|sendNonDirectUpdateRequestReplicaWithRetry
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|up
argument_list|,
literal|1
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// heal the partition
name|getProxyForReplica
argument_list|(
name|shard2Replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
DECL|method|sendNonDirectUpdateRequestReplicaWithRetry
specifier|protected
name|void
name|sendNonDirectUpdateRequestReplicaWithRetry
parameter_list|(
name|Replica
name|replica
parameter_list|,
name|UpdateRequest
name|up
parameter_list|,
name|int
name|expectedRf
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|sendNonDirectUpdateRequestReplica
argument_list|(
name|replica
argument_list|,
name|up
argument_list|,
name|expectedRf
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|sendNonDirectUpdateRequestReplica
argument_list|(
name|replica
argument_list|,
name|up
argument_list|,
name|expectedRf
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|sendNonDirectUpdateRequestReplica
specifier|protected
name|void
name|sendNonDirectUpdateRequestReplica
parameter_list|(
name|Replica
name|replica
parameter_list|,
name|UpdateRequest
name|up
parameter_list|,
name|int
name|expectedRf
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|Exception
block|{
name|ZkCoreNodeProps
name|zkProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|zkProps
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"/"
operator|+
name|collection
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|solrServer
init|=
name|getHttpSolrClient
argument_list|(
name|url
argument_list|)
init|)
block|{
name|NamedList
name|resp
init|=
name|solrServer
operator|.
name|request
argument_list|(
name|up
argument_list|)
decl_stmt|;
name|NamedList
name|hdr
init|=
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
decl_stmt|;
name|Integer
name|batchRf
init|=
operator|(
name|Integer
operator|)
name|hdr
operator|.
name|get
argument_list|(
name|UpdateRequest
operator|.
name|REPFACT
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected rf="
operator|+
name|expectedRf
operator|+
literal|" for batch but got "
operator|+
name|batchRf
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|,
name|batchRf
operator|==
name|expectedRf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRf3
specifier|protected
name|void
name|testRf3
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numShards
init|=
literal|1
decl_stmt|;
name|int
name|replicationFactor
init|=
literal|3
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
literal|1
decl_stmt|;
name|String
name|testCollectionName
init|=
literal|"repfacttest_c8n_1x3"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|int
name|minRf
init|=
literal|2
decl_stmt|;
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
name|maxShardsPerNode
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 2 active replicas for "
operator|+
name|testCollectionName
argument_list|,
name|replicas
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|int
name|rf
init|=
name|sendDoc
argument_list|(
literal|1
argument_list|,
name|minRf
argument_list|)
decl_stmt|;
name|assertRf
argument_list|(
literal|3
argument_list|,
literal|"all replicas should be active"
argument_list|,
name|rf
argument_list|)
expr_stmt|;
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|rf
operator|=
name|sendDoc
argument_list|(
literal|2
argument_list|,
name|minRf
argument_list|)
expr_stmt|;
name|assertRf
argument_list|(
literal|2
argument_list|,
literal|"one replica should be down"
argument_list|,
name|rf
argument_list|)
expr_stmt|;
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|rf
operator|=
name|sendDoc
argument_list|(
literal|3
argument_list|,
name|minRf
argument_list|)
expr_stmt|;
name|assertRf
argument_list|(
literal|1
argument_list|,
literal|"both replicas should be down"
argument_list|,
name|rf
argument_list|)
expr_stmt|;
comment|// heal the partitions
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// give time for the healed partition to get propagated
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|rf
operator|=
name|sendDoc
argument_list|(
literal|4
argument_list|,
name|minRf
argument_list|)
expr_stmt|;
name|assertRf
argument_list|(
literal|3
argument_list|,
literal|"partitions to replicas have been healed"
argument_list|,
name|rf
argument_list|)
expr_stmt|;
comment|// now send a batch
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<
literal|15
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|i
argument_list|)
expr_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|int
name|batchRf
init|=
name|sendDocsWithRetry
argument_list|(
name|batch
argument_list|,
name|minRf
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertRf
argument_list|(
literal|3
argument_list|,
literal|"batch should have succeeded on all replicas"
argument_list|,
name|batchRf
argument_list|)
expr_stmt|;
comment|// add some chaos to the batch
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now send a batch
name|batch
operator|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|15
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|i
argument_list|)
expr_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|batchRf
operator|=
name|sendDocsWithRetry
argument_list|(
name|batch
argument_list|,
name|minRf
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertRf
argument_list|(
literal|2
argument_list|,
literal|"batch should have succeeded on 2 replicas (only one replica should be down)"
argument_list|,
name|batchRf
argument_list|)
expr_stmt|;
comment|// close the 2nd replica, and send a 3rd batch with expected achieved rf=1
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|batch
operator|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|30
init|;
name|i
operator|<
literal|45
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|i
argument_list|)
expr_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|batchRf
operator|=
name|sendDocsWithRetry
argument_list|(
name|batch
argument_list|,
name|minRf
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertRf
argument_list|(
literal|1
argument_list|,
literal|"batch should have succeeded on the leader only (both replicas should be down)"
argument_list|,
name|batchRf
argument_list|)
expr_stmt|;
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|getProxyForReplica
argument_list|(
name|replicas
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|reopen
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|,
literal|30
argument_list|)
expr_stmt|;
block|}
DECL|method|sendDoc
specifier|protected
name|int
name|sendDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|int
name|minRf
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|setParam
argument_list|(
name|UpdateRequest
operator|.
name|MIN_REPFACT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|minRf
argument_list|)
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
name|id
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|docId
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"a_t"
argument_list|,
literal|"hello"
operator|+
name|docId
argument_list|)
expr_stmt|;
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
return|return
name|cloudClient
operator|.
name|getMinAchievedReplicationFactor
argument_list|(
name|cloudClient
operator|.
name|getDefaultCollection
argument_list|()
argument_list|,
name|cloudClient
operator|.
name|request
argument_list|(
name|up
argument_list|)
argument_list|)
return|;
block|}
DECL|method|assertRf
specifier|protected
name|void
name|assertRf
parameter_list|(
name|int
name|expected
parameter_list|,
name|String
name|explain
parameter_list|,
name|int
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|actual
operator|!=
name|expected
condition|)
block|{
name|String
name|assertionFailedMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"Expected rf=%d because %s but got %d"
argument_list|,
name|expected
argument_list|,
name|explain
argument_list|,
name|actual
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|assertionFailedMessage
operator|+
literal|"; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

