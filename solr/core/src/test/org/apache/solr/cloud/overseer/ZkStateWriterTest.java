begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud.overseer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|overseer
package|;
end_package

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
name|SolrTestCaseJ4
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
name|AbstractZkTestCase
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
name|Overseer
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
name|OverseerTest
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
name|ZkController
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
name|DocRouter
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
DECL|class|ZkStateWriterTest
specifier|public
class|class
name|ZkStateWriterTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|method|testZkStateWriterBatching
specifier|public
name|void
name|testZkStateWriterBatching
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testZkStateWriterBatching"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Deletes can always be batched"
argument_list|,
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
operator|new
name|ZkWriteCommand
argument_list|(
literal|"xyz"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Deletes can always be batched"
argument_list|,
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
operator|new
name|ZkWriteCommand
argument_list|(
literal|"xyz"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create new collection with stateFormat = 2
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"First requests can always be batched"
argument_list|,
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ZkWriteCommand
name|c2
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c2"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c2"
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c2"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Different (new) collection create cannot be batched together with another create"
argument_list|,
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate three state changes on same collection, all should be batched together before
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
comment|// and after too
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
comment|// simulate three state changes on two different collections with stateFormat=2, none should be batched
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
comment|// flushAfter has to be called as it updates the internal batching related info
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
comment|// create a collection in stateFormat = 1 i.e. inside the main cluster state
name|ZkWriteCommand
name|c3
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c3"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c3"
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|)
argument_list|)
decl_stmt|;
name|clusterState
operator|=
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|clusterState
argument_list|,
name|c3
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// simulate three state changes in c3, all should be batched
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// simulate state change in c3 (stateFormat=1) interleaved with state changes from c1,c2 (stateFormat=2)
comment|// none should be batched together
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"different stateFormat, should be flushed"
argument_list|,
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"different stateFormat, should be flushed"
argument_list|,
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c3
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"different stateFormat, should be flushed"
argument_list|,
name|writer
operator|.
name|maybeFlushBefore
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|maybeFlushAfter
argument_list|(
name|c2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSingleLegacyCollection
specifier|public
name|void
name|testSingleLegacyCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testSingleLegacyCollection"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create new collection with stateFormat = 1
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"c1"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|exists
init|=
name|zkClient
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|exists
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSingleExternalCollection
specifier|public
name|void
name|testSingleExternalCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testSingleExternalCollection"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create new collection with stateFormat = 2
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"c1"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1/state.json"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"c1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testExternalModificationToSharedClusterState
specifier|public
name|void
name|testExternalModificationToSharedClusterState
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testExternalModification"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// create collection 1 with stateFormat = 1
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|reader
operator|.
name|forceUpdateCollection
argument_list|(
literal|"c1"
argument_list|)
expr_stmt|;
name|reader
operator|.
name|forceUpdateCollection
argument_list|(
literal|"c2"
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|reader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
comment|// keep a reference to the current cluster state object
name|assertTrue
argument_list|(
name|clusterState
operator|.
name|hasCollection
argument_list|(
literal|"c1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|clusterState
operator|.
name|hasCollection
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Simulate an external modification to /clusterstate.json
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|setData
argument_list|(
literal|"/clusterstate.json"
argument_list|,
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// enqueue another c1 so that ZkStateWriter has pending updates
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|clusterState
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|hasPendingUpdates
argument_list|()
argument_list|)
expr_stmt|;
comment|// create collection 2 with stateFormat = 1
name|ZkWriteCommand
name|c2
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c2"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c2"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|clusterState
argument_list|,
name|c2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// we are sending in the old cluster state object
name|fail
argument_list|(
literal|"Enqueue should not have succeeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|bve
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enqueueUpdate after BadVersionException should not have suceeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"writePendingUpdates after BadVersionException should not have suceeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testExternalModificationToStateFormat2
specifier|public
name|void
name|testExternalModificationToStateFormat2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|createTempDir
argument_list|(
literal|"testExternalModificationToStateFormat2"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|OverseerTest
operator|.
name|DEFAULT_CONNECTION_TIMEOUT
argument_list|)
expr_stmt|;
name|ZkController
operator|.
name|createClusterZkNodes
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|ZkStateWriter
name|writer
init|=
operator|new
name|ZkStateWriter
argument_list|(
name|reader
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/c2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ClusterState
name|state
init|=
name|reader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
comment|// create collection 2 with stateFormat = 2
name|ZkWriteCommand
name|c2
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c2"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c2"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|=
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|writer
operator|.
name|hasPendingUpdates
argument_list|()
argument_list|)
expr_stmt|;
comment|// first write is flushed immediately
name|int
name|sharedClusterStateVersion
init|=
name|state
operator|.
name|getZkClusterStateVersion
argument_list|()
decl_stmt|;
name|int
name|stateFormat2Version
init|=
name|state
operator|.
name|getCollection
argument_list|(
literal|"c2"
argument_list|)
operator|.
name|getZNodeVersion
argument_list|()
decl_stmt|;
comment|// Simulate an external modification to /collections/c2/state.json
name|byte
index|[]
name|data
init|=
name|zkClient
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
literal|"c2"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|setData
argument_list|(
name|ZkStateReader
operator|.
name|getCollectionPath
argument_list|(
literal|"c2"
argument_list|)
argument_list|,
name|data
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// get the most up-to-date state
name|reader
operator|.
name|forceUpdateCollection
argument_list|(
literal|"c2"
argument_list|)
expr_stmt|;
name|state
operator|=
name|reader
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasCollection
argument_list|(
literal|"c2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sharedClusterStateVersion
argument_list|,
operator|(
name|int
operator|)
name|state
operator|.
name|getZkClusterStateVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stateFormat2Version
operator|+
literal|1
argument_list|,
name|state
operator|.
name|getCollection
argument_list|(
literal|"c2"
argument_list|)
operator|.
name|getZNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|// enqueue an update to stateFormat2 collection such that update is pending
name|state
operator|=
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|state
argument_list|,
name|c2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|hasPendingUpdates
argument_list|()
argument_list|)
expr_stmt|;
comment|// get the most up-to-date state
name|reader
operator|.
name|forceUpdateCollection
argument_list|(
literal|"c2"
argument_list|)
expr_stmt|;
name|state
operator|=
name|reader
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
comment|// enqueue a stateFormat=1 collection which should cause a flush
name|ZkWriteCommand
name|c1
init|=
operator|new
name|ZkWriteCommand
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|DocCollection
argument_list|(
literal|"c1"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|,
name|DocRouter
operator|.
name|DEFAULT
argument_list|,
literal|0
argument_list|,
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|state
argument_list|,
name|c1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Enqueue should not have succeeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|bve
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|writer
operator|.
name|enqueueUpdate
argument_list|(
name|reader
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|c2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"enqueueUpdate after BadVersionException should not have suceeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|writer
operator|.
name|writePendingUpdates
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"writePendingUpdates after BadVersionException should not have suceeded"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|zkClient
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

