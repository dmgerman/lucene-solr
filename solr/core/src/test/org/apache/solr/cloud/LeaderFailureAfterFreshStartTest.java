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
name|Collection
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
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
name|cloud
operator|.
name|ZkTestServer
operator|.
name|LimitViolationAction
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
name|Slice
operator|.
name|State
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
name|core
operator|.
name|Diagnostics
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
name|ReplicationHandler
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_comment
comment|/**  *   * Test for SOLR-9446  *  * This test is modeled after SyncSliceTest  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|LeaderFailureAfterFreshStartTest
specifier|public
class|class
name|LeaderFailureAfterFreshStartTest
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
DECL|field|success
specifier|private
name|boolean
name|success
init|=
literal|false
decl_stmt|;
DECL|field|docId
name|int
name|docId
init|=
literal|0
decl_stmt|;
DECL|field|nodesDown
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|nodesDown
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|printLayoutOnTearDown
operator|=
literal|true
expr_stmt|;
block|}
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.ulog.numRecordsToKeep"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tests.zk.violationReportAction"
argument_list|)
expr_stmt|;
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|LeaderFailureAfterFreshStartTest
specifier|public
name|LeaderFailureAfterFreshStartTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
name|fixShardCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-tlog.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tlog gets deleted after node restarts if we use CachingDirectoryFactory.
comment|// make sure that tlog stays intact after we restart a node
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.ulog.numRecordsToKeep"
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.zk.violationReportAction"
argument_list|,
name|LimitViolationAction
operator|.
name|IGNORE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
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
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
try|try
block|{
name|CloudJettyRunner
name|initialLeaderJetty
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|otherJetties
init|=
name|getOtherAvailableJetties
argument_list|(
name|initialLeaderJetty
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Leader node_name: {},  url: {}"
argument_list|,
name|initialLeaderJetty
operator|.
name|coreNodeName
argument_list|,
name|initialLeaderJetty
operator|.
name|url
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cloudJettyRunner
range|:
name|otherJetties
control|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Nonleader node_name: {},  url: {}"
argument_list|,
name|cloudJettyRunner
operator|.
name|coreNodeName
argument_list|,
name|cloudJettyRunner
operator|.
name|url
argument_list|)
expr_stmt|;
block|}
name|CloudJettyRunner
name|secondNode
init|=
name|otherJetties
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|CloudJettyRunner
name|freshNode
init|=
name|otherJetties
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// shutdown a node to simulate fresh start
name|otherJetties
operator|.
name|remove
argument_list|(
name|freshNode
argument_list|)
expr_stmt|;
name|forceNodeFailures
argument_list|(
name|singletonList
argument_list|(
name|freshNode
argument_list|)
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// index a few docs and commit
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|indexDoc
argument_list|(
name|id
argument_list|,
name|docId
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"document number "
operator|+
name|docId
operator|++
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|checkShardConsistency
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// start the freshNode
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|freshNode
operator|.
name|jetty
argument_list|)
expr_stmt|;
name|nodesDown
operator|.
name|remove
argument_list|(
name|freshNode
argument_list|)
expr_stmt|;
name|waitTillNodesActive
argument_list|()
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
comment|//TODO check how to see if fresh node went into recovery (may be check count for replication handler on new leader)
name|long
name|numRequestsBefore
init|=
operator|(
name|Long
operator|)
name|secondNode
operator|.
name|jetty
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getCores
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
name|ReplicationHandler
operator|.
name|PATH
argument_list|)
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
decl_stmt|;
comment|// shutdown the original leader
name|log
operator|.
name|info
argument_list|(
literal|"Now shutting down initial leader"
argument_list|)
expr_stmt|;
name|forceNodeFailures
argument_list|(
name|singletonList
argument_list|(
name|initialLeaderJetty
argument_list|)
argument_list|)
expr_stmt|;
name|waitForNewLeader
argument_list|(
name|cloudClient
argument_list|,
literal|"shard1"
argument_list|,
operator|(
name|Replica
operator|)
name|initialLeaderJetty
operator|.
name|client
operator|.
name|info
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Updating mappings from zk"
argument_list|)
expr_stmt|;
name|updateMappingsFromZk
argument_list|(
name|jettys
argument_list|,
name|clients
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|long
name|numRequestsAfter
init|=
operator|(
name|Long
operator|)
name|secondNode
operator|.
name|jetty
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getCores
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
name|ReplicationHandler
operator|.
name|PATH
argument_list|)
operator|.
name|getStatistics
argument_list|()
operator|.
name|get
argument_list|(
literal|"requests"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Node went into replication"
argument_list|,
name|numRequestsBefore
argument_list|,
name|numRequestsAfter
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.disableFingerprint"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|forceNodeFailures
specifier|private
name|void
name|forceNodeFailures
parameter_list|(
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|replicasToShutDown
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|CloudJettyRunner
name|replicaToShutDown
range|:
name|replicasToShutDown
control|)
block|{
name|chaosMonkey
operator|.
name|killJetty
argument_list|(
name|replicaToShutDown
argument_list|)
expr_stmt|;
name|waitForNoShardInconsistency
argument_list|()
expr_stmt|;
block|}
name|int
name|totalDown
init|=
literal|0
decl_stmt|;
name|Set
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|jetties
operator|.
name|addAll
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicasToShutDown
operator|!=
literal|null
condition|)
block|{
name|jetties
operator|.
name|removeAll
argument_list|(
name|replicasToShutDown
argument_list|)
expr_stmt|;
name|totalDown
operator|+=
name|replicasToShutDown
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|jetties
operator|.
name|removeAll
argument_list|(
name|nodesDown
argument_list|)
expr_stmt|;
name|totalDown
operator|+=
name|nodesDown
operator|.
name|size
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|getShardCount
argument_list|()
operator|-
name|totalDown
argument_list|,
name|jetties
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nodesDown
operator|.
name|addAll
argument_list|(
name|replicasToShutDown
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForNewLeader
specifier|static
name|void
name|waitForNewLeader
parameter_list|(
name|CloudSolrClient
name|cloudClient
parameter_list|,
name|String
name|shardName
parameter_list|,
name|Replica
name|oldLeader
parameter_list|,
name|int
name|maxWaitInSecs
parameter_list|)
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Will wait for a node to become leader for {} secs"
argument_list|,
name|maxWaitInSecs
argument_list|)
expr_stmt|;
name|boolean
name|waitForLeader
init|=
literal|true
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|zkStateReader
operator|.
name|forceUpdateCollection
argument_list|(
name|DEFAULT_COLLECTION
argument_list|)
expr_stmt|;
while|while
condition|(
name|waitForLeader
condition|)
block|{
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
name|Slice
name|slice
init|=
name|coll
operator|.
name|getSlice
argument_list|(
name|shardName
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|.
name|getLeader
argument_list|()
operator|!=
name|oldLeader
operator|&&
name|slice
operator|.
name|getState
argument_list|()
operator|==
name|State
operator|.
name|ACTIVE
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"New leader got elected in {} secs"
argument_list|,
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|i
operator|==
name|maxWaitInSecs
condition|)
block|{
name|Diagnostics
operator|.
name|logThreadDumps
argument_list|(
literal|"Could not find new leader in specified timeout"
argument_list|)
expr_stmt|;
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Could not find new leader even after waiting for "
operator|+
name|maxWaitInSecs
operator|+
literal|"secs"
argument_list|)
expr_stmt|;
block|}
name|i
operator|++
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitTillNodesActive
specifier|private
name|void
name|waitTillNodesActive
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|60
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|DocCollection
name|collection1
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
name|Slice
name|slice
init|=
name|collection1
operator|.
name|getSlice
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
name|boolean
name|allActive
init|=
literal|true
decl_stmt|;
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicasToCheck
init|=
literal|null
decl_stmt|;
name|replicasToCheck
operator|=
name|replicas
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|r
lambda|->
name|nodesDown
operator|.
name|contains
argument_list|(
name|r
operator|.
name|getName
argument_list|()
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
expr_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicasToCheck
control|)
block|{
if|if
condition|(
operator|!
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|||
name|replica
operator|.
name|getState
argument_list|()
operator|!=
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
condition|)
block|{
name|allActive
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|allActive
condition|)
block|{
return|return;
block|}
block|}
name|printLayout
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"timeout waiting to see all nodes active"
argument_list|)
expr_stmt|;
block|}
DECL|method|getOtherAvailableJetties
specifier|private
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|getOtherAvailableJetties
parameter_list|(
name|CloudJettyRunner
name|leader
parameter_list|)
block|{
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|candidates
operator|.
name|addAll
argument_list|(
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|leader
operator|!=
literal|null
condition|)
block|{
name|candidates
operator|.
name|remove
argument_list|(
name|leader
argument_list|)
expr_stmt|;
block|}
name|candidates
operator|.
name|removeAll
argument_list|(
name|nodesDown
argument_list|)
expr_stmt|;
return|return
name|candidates
return|;
block|}
DECL|method|indexDoc
specifier|protected
name|void
name|indexDoc
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_s"
argument_list|,
name|RandomStringUtils
operator|.
name|random
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|UpdateRequest
name|ureq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
comment|// skip the randoms - they can deadlock...
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

