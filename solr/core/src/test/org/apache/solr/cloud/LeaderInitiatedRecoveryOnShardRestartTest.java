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
name|Map
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
name|Nightly
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
name|SolrQuery
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
name|impl
operator|.
name|HttpSolrClient
operator|.
name|RemoteSolrException
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
name|SolrZkClient
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
operator|.
name|CollectionAction
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
name|Utils
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
name|processor
operator|.
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
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
name|processor
operator|.
name|DistributingUpdateProcessorFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
operator|.
name|NodeExistsException
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
name|Nightly
DECL|class|LeaderInitiatedRecoveryOnShardRestartTest
specifier|public
class|class
name|LeaderInitiatedRecoveryOnShardRestartTest
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
DECL|method|LeaderInitiatedRecoveryOnShardRestartTest
specifier|public
name|LeaderInitiatedRecoveryOnShardRestartTest
parameter_list|()
throws|throws
name|Exception
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|1
expr_stmt|;
comment|// we want 3 jetties, but we are using the control jetty as one
name|fixShardCount
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|before
specifier|public
specifier|static
name|void
name|before
parameter_list|()
block|{
comment|// we want more realistic leaderVoteWait so raise from
comment|// test default of 10s to 30s.
name|System
operator|.
name|setProperty
argument_list|(
literal|"leaderVoteWait"
argument_list|,
literal|"300000"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|after
specifier|public
specifier|static
name|void
name|after
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"leaderVoteWait"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRestartWithAllInLIR
specifier|public
name|void
name|testRestartWithAllInLIR
parameter_list|()
throws|throws
name|Exception
block|{
comment|// still waiting to be able to properly start with no default collection1,
comment|// delete to remove confusion
name|waitForRecoveriesToFinish
argument_list|(
literal|false
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
name|CollectionAction
operator|.
name|DELETE
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
name|DEFAULT_COLLECTION
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
name|String
name|baseUrl
init|=
operator|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|HttpSolrClient
name|delClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseUrl
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|delClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|delClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|testCollectionName
init|=
literal|"all_in_lir"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|testCollectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stateObj
init|=
name|Utils
operator|.
name|makeMap
argument_list|()
decl_stmt|;
name|stateObj
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
literal|"down"
argument_list|)
expr_stmt|;
name|stateObj
operator|.
name|put
argument_list|(
literal|"createdByNodeName"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|stateObj
operator|.
name|put
argument_list|(
literal|"createdByCoreNodeName"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|znodeData
init|=
name|Utils
operator|.
name|toJSON
argument_list|(
name|stateObj
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/"
operator|+
name|testCollectionName
operator|+
literal|"/leader_initiated_recovery/"
operator|+
name|shardId
operator|+
literal|"/core_node1"
argument_list|,
name|znodeData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/"
operator|+
name|testCollectionName
operator|+
literal|"/leader_initiated_recovery/"
operator|+
name|shardId
operator|+
literal|"/core_node2"
argument_list|,
name|znodeData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/"
operator|+
name|testCollectionName
operator|+
literal|"/leader_initiated_recovery/"
operator|+
name|shardId
operator|+
literal|"/core_node3"
argument_list|,
name|znodeData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// everyone gets a couple docs so that everyone has tlog entries
comment|// and won't become leader simply because they have no tlog versions
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
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc2
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc2
argument_list|,
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"We just added 2 docs, we should be able to find them"
argument_list|,
literal|2
argument_list|,
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
comment|// randomly add too many docs to peer sync to one replica so that only one random replica is the valid leader
comment|// the versions don't matter, they just have to be higher than what the last 2 docs got
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|setBaseURL
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|client
operator|.
name|getBaseURL
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|+
literal|"/"
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
argument_list|,
name|DistribPhase
operator|.
name|FROMLEADER
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
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
literal|101
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|client
argument_list|,
name|params
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|3
operator|+
name|i
argument_list|,
literal|"_version_"
argument_list|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
operator|-
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RemoteSolrException
name|e
parameter_list|)
block|{
comment|// if we got a conflict it's because we tried to send a versioned doc to the leader,
comment|// resend without version
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"conflict"
argument_list|)
condition|)
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
literal|101
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|client
argument_list|,
name|params
argument_list|,
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|3
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|controlJetty
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Start back up"
argument_list|)
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|controlJetty
argument_list|)
expr_stmt|;
comment|// recoveries will not finish without SOLR-8075 and SOLR-8367
name|waitForRecoveriesToFinish
argument_list|(
name|testCollectionName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// now expire each node
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/"
operator|+
name|testCollectionName
operator|+
literal|"/leader_initiated_recovery/"
operator|+
name|shardId
operator|+
literal|"/core_node1"
argument_list|,
name|znodeData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{          }
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/"
operator|+
name|testCollectionName
operator|+
literal|"/leader_initiated_recovery/"
operator|+
name|shardId
operator|+
literal|"/core_node2"
argument_list|,
name|znodeData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{          }
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/collections/"
operator|+
name|testCollectionName
operator|+
literal|"/leader_initiated_recovery/"
operator|+
name|shardId
operator|+
literal|"/core_node3"
argument_list|,
name|znodeData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{          }
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|chaosMonkey
operator|.
name|expireSession
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// recoveries will not finish without SOLR-8075 and SOLR-8367
name|waitForRecoveriesToFinish
argument_list|(
name|testCollectionName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

