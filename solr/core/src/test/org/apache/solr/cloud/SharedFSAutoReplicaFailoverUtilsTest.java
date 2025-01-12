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
name|junit
operator|.
name|After
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
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|ClusterStateMockUtil
operator|.
name|buildClusterState
import|;
end_import

begin_class
DECL|class|SharedFSAutoReplicaFailoverUtilsTest
specifier|public
class|class
name|SharedFSAutoReplicaFailoverUtilsTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|NODE6
specifier|private
specifier|static
specifier|final
name|String
name|NODE6
init|=
literal|"baseUrl6_"
decl_stmt|;
DECL|field|NODE6_URL
specifier|private
specifier|static
specifier|final
name|String
name|NODE6_URL
init|=
literal|"http://baseUrl6"
decl_stmt|;
DECL|field|NODE5
specifier|private
specifier|static
specifier|final
name|String
name|NODE5
init|=
literal|"baseUrl5_"
decl_stmt|;
DECL|field|NODE5_URL
specifier|private
specifier|static
specifier|final
name|String
name|NODE5_URL
init|=
literal|"http://baseUrl5"
decl_stmt|;
DECL|field|NODE4
specifier|private
specifier|static
specifier|final
name|String
name|NODE4
init|=
literal|"baseUrl4_"
decl_stmt|;
DECL|field|NODE4_URL
specifier|private
specifier|static
specifier|final
name|String
name|NODE4_URL
init|=
literal|"http://baseUrl4"
decl_stmt|;
DECL|field|NODE3
specifier|private
specifier|static
specifier|final
name|String
name|NODE3
init|=
literal|"baseUrl3_"
decl_stmt|;
DECL|field|NODE3_URL
specifier|private
specifier|static
specifier|final
name|String
name|NODE3_URL
init|=
literal|"http://baseUrl3"
decl_stmt|;
DECL|field|NODE2
specifier|private
specifier|static
specifier|final
name|String
name|NODE2
init|=
literal|"baseUrl2_"
decl_stmt|;
DECL|field|NODE2_URL
specifier|private
specifier|static
specifier|final
name|String
name|NODE2_URL
init|=
literal|"http://baseUrl2"
decl_stmt|;
DECL|field|NODE1
specifier|private
specifier|static
specifier|final
name|String
name|NODE1
init|=
literal|"baseUrl1_"
decl_stmt|;
DECL|field|NODE1_URL
specifier|private
specifier|static
specifier|final
name|String
name|NODE1_URL
init|=
literal|"http://baseUrl1"
decl_stmt|;
DECL|field|results
specifier|private
name|List
argument_list|<
name|ClusterStateMockUtil
operator|.
name|Result
argument_list|>
name|results
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|results
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
for|for
control|(
name|ClusterStateMockUtil
operator|.
name|Result
name|result
range|:
name|results
control|)
block|{
name|result
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetBestCreateUrlBasics
specifier|public
name|void
name|testGetBestCreateUrlBasics
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1R*r2"
argument_list|,
name|NODE1
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Should be no live node to failover to"
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1R*r2"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Only failover candidate node already has a replica"
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1R*r2sr3"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Node3 does not have a replica from the bad slice and should be the best choice"
argument_list|,
name|NODE3_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1R*r2Fsr3r4r5"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createUrl
operator|.
name|equals
argument_list|(
name|NODE3_URL
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1*r2r3sr3r3sr4"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|,
name|NODE4
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODE4_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1*r2sr3r3sr4sr4"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|,
name|NODE4
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createUrl
operator|.
name|equals
argument_list|(
name|NODE3_URL
argument_list|)
operator|||
name|createUrl
operator|.
name|equals
argument_list|(
name|NODE4_URL
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetBestCreateUrlMultipleCollections
specifier|public
name|void
name|testGetBestCreateUrlMultipleCollections
parameter_list|()
throws|throws
name|Exception
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*r2csr2"
argument_list|,
name|NODE1
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*r2csr2"
argument_list|,
name|NODE1
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*r2csr2"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetBestCreateUrlMultipleCollections2
specifier|public
name|void
name|testGetBestCreateUrlMultipleCollections2
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*r2sr3cr2"
argument_list|,
name|NODE1
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*r2sr3cr2"
argument_list|,
name|NODE1
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODE3_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetBestCreateUrlMultipleCollections3
specifier|public
name|void
name|testGetBestCreateUrlMultipleCollections3
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr5r1sr4r2sr3r6csr2*r6sr5r3sr4r3"
argument_list|,
name|NODE1
argument_list|,
name|NODE4
argument_list|,
name|NODE5
argument_list|,
name|NODE6
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NODE1_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetBestCreateUrlMultipleCollections4
specifier|public
name|void
name|testGetBestCreateUrlMultipleCollections4
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1r4sr3r5sr2r6csr5r6sr4r6sr5*r4"
argument_list|,
name|NODE6
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NODE6_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailOverToEmptySolrInstance
specifier|public
name|void
name|testFailOverToEmptySolrInstance
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr1*r1sr1csr1"
argument_list|,
name|NODE2
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NODE2_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFavorForeignSlices
specifier|public
name|void
name|testFavorForeignSlices
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2csr3r3"
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NODE3_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2csr3r3r3r3r3r3r3"
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODE2_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollectionMaxNodesPerShard
specifier|public
name|void
name|testCollectionMaxNodesPerShard
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|NODE2
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|NODE2
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODE2_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*csr2r2"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|NODE2
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODE2_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMaxCoresPerNode
specifier|public
name|void
name|testMaxCoresPerNode
parameter_list|()
block|{
name|ClusterStateMockUtil
operator|.
name|Result
name|result
init|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|NODE2
argument_list|)
decl_stmt|;
name|String
name|createUrl
init|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|NODE2
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NODE2_URL
argument_list|,
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2sr3sr4"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|,
name|NODE4
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|createUrl
argument_list|)
expr_stmt|;
name|result
operator|=
name|buildClusterState
argument_list|(
name|results
argument_list|,
literal|"csr*sr2sr3sr4"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
name|NODE2
argument_list|,
name|NODE3
argument_list|,
name|NODE4
argument_list|)
expr_stmt|;
name|createUrl
operator|=
name|OverseerAutoReplicaFailoverThread
operator|.
name|getBestCreateUrl
argument_list|(
name|result
operator|.
name|reader
argument_list|,
name|result
operator|.
name|badReplica
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|createUrl
operator|.
name|equals
argument_list|(
name|NODE3_URL
argument_list|)
operator|||
name|createUrl
operator|.
name|equals
argument_list|(
name|NODE4_URL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

