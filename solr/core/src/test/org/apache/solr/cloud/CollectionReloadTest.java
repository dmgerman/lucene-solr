begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|CollectionParams
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
comment|/**  * Verifies cluster state remains consistent after collection reload.  */
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
DECL|class|CollectionReloadTest
specifier|public
class|class
name|CollectionReloadTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
specifier|transient
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CollectionReloadTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|CollectionReloadTest
specifier|public
name|CollectionReloadTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReloadedLeaderStateAfterZkSessionLoss
specifier|public
name|void
name|testReloadedLeaderStateAfterZkSessionLoss
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"testReloadedLeaderStateAfterZkSessionLoss initialized OK ... running test logic"
argument_list|)
expr_stmt|;
name|String
name|testCollectionName
init|=
literal|"c8n_1x1"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|createCollectionRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|Replica
name|leader
init|=
literal|null
decl_stmt|;
name|String
name|replicaState
init|=
literal|null
decl_stmt|;
name|int
name|timeoutSecs
init|=
literal|30
decl_stmt|;
name|long
name|timeout
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeoutSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|Replica
name|tmp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tmp
operator|=
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{}
if|if
condition|(
name|tmp
operator|!=
literal|null
operator|&&
literal|"active"
operator|.
name|equals
argument_list|(
name|tmp
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|leader
operator|=
name|tmp
expr_stmt|;
name|replicaState
operator|=
literal|"active"
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Could not find active leader for "
operator|+
name|shardId
operator|+
literal|" of "
operator|+
name|testCollectionName
operator|+
literal|" after "
operator|+
name|timeoutSecs
operator|+
literal|" secs; clusterState: "
operator|+
name|printClusterStateInfo
argument_list|(
name|testCollectionName
argument_list|)
argument_list|,
name|leader
argument_list|)
expr_stmt|;
comment|// reload collection and wait to see the core report it has been reloaded
name|boolean
name|wasReloaded
init|=
name|reloadCollection
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Collection '"
operator|+
name|testCollectionName
operator|+
literal|"' failed to reload within a reasonable amount of time!"
argument_list|,
name|wasReloaded
argument_list|)
expr_stmt|;
comment|// cause session loss
name|chaosMonkey
operator|.
name|expireSession
argument_list|(
name|getJettyOnPort
argument_list|(
name|getReplicaPort
argument_list|(
name|leader
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: have to wait a while for the node to get marked down after ZK session loss
comment|// but tests shouldn't be so timing dependent!
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
comment|// wait up to 15 seconds to see the replica in the active state
name|timeoutSecs
operator|=
literal|15
expr_stmt|;
name|timeout
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
name|timeoutSecs
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
comment|// state of leader should be active after session loss recovery - see SOLR-7338
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ClusterState
name|cs
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Slice
name|slice
init|=
name|cs
operator|.
name|getSlice
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|replicaState
operator|=
name|slice
operator|.
name|getReplica
argument_list|(
name|leader
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"active"
operator|.
name|equals
argument_list|(
name|replicaState
argument_list|)
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Leader state should be active after recovering from ZK session loss, but after "
operator|+
name|timeoutSecs
operator|+
literal|" seconds, it is "
operator|+
name|replicaState
argument_list|,
literal|"active"
argument_list|,
name|replicaState
argument_list|)
expr_stmt|;
comment|// try to clean up
try|try
block|{
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"testReloadedLeaderStateAfterZkSessionLoss succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
DECL|method|reloadCollection
specifier|protected
name|boolean
name|reloadCollection
parameter_list|(
name|Replica
name|replica
parameter_list|,
name|String
name|testCollectionName
parameter_list|)
throws|throws
name|Exception
block|{
name|ZkCoreNodeProps
name|coreProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|String
name|coreName
init|=
name|coreProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
name|boolean
name|reloadedOk
init|=
literal|false
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|coreProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
init|)
block|{
name|CoreAdminResponse
name|statusResp
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|coreName
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|long
name|leaderCoreStartTime
init|=
name|statusResp
operator|.
name|getStartTime
argument_list|(
name|coreName
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// send reload command for the collection
name|log
operator|.
name|info
argument_list|(
literal|"Sending RELOAD command for "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"action"
argument_list|,
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|RELOAD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"name"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/collections"
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// reload can take a short while
comment|// verify reload is done, waiting up to 30 seconds for slow test environments
name|long
name|timeout
init|=
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|statusResp
operator|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|coreName
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|long
name|startTimeAfterReload
init|=
name|statusResp
operator|.
name|getStartTime
argument_list|(
name|coreName
argument_list|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|startTimeAfterReload
operator|>
name|leaderCoreStartTime
condition|)
block|{
name|reloadedOk
operator|=
literal|true
expr_stmt|;
break|break;
block|}
comment|// else ... still waiting to see the reloaded core report a later start time
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|reloadedOk
return|;
block|}
DECL|method|createCollectionRetry
specifier|private
name|void
name|createCollectionRetry
parameter_list|(
name|String
name|testCollectionName
parameter_list|,
name|int
name|numShards
parameter_list|,
name|int
name|replicationFactor
parameter_list|,
name|int
name|maxShardsPerNode
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
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
name|Delete
name|req
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Delete
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCollectionName
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|req
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
name|fail
argument_list|(
literal|"Could not create "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

