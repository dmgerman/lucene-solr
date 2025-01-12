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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
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
name|response
operator|.
name|QueryResponse
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
name|AfterClass
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

begin_class
annotation|@
name|Slow
DECL|class|MissingSegmentRecoveryTest
specifier|public
class|class
name|MissingSegmentRecoveryTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|collection
specifier|final
name|String
name|collection
init|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
DECL|field|leader
name|Replica
name|leader
decl_stmt|;
DECL|field|replica
name|Replica
name|replica
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|2
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
argument_list|,
name|configset
argument_list|(
literal|"cloud-minimal"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|useFactory
argument_list|(
literal|"solr.StandardDirectoryFactory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collection
argument_list|,
literal|"conf"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|1
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
literal|"Expected a collection with one shard and two replicas"
argument_list|,
name|collection
argument_list|,
name|clusterShape
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|setDefaultCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
literal|10
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
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DocCollection
name|state
init|=
name|getCollectionState
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|leader
operator|=
name|state
operator|.
name|getLeader
argument_list|(
literal|"shard1"
argument_list|)
expr_stmt|;
name|replica
operator|=
name|getRandomReplica
argument_list|(
name|state
operator|.
name|getSlice
argument_list|(
literal|"shard1"
argument_list|)
argument_list|,
parameter_list|(
name|r
parameter_list|)
lambda|->
name|leader
operator|!=
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"CoreInitFailedAction"
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
name|collection
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
block|}
annotation|@
name|AfterClass
DECL|method|teardownCluster
specifier|public
specifier|static
name|void
name|teardownCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|resetFactory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeaderRecovery
specifier|public
name|void
name|testLeaderRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"CoreInitFailedAction"
argument_list|,
literal|"fromleader"
argument_list|)
expr_stmt|;
comment|// Simulate failure by truncating the segment_* files
for|for
control|(
name|File
name|segment
range|:
name|getSegmentFiles
argument_list|(
name|replica
argument_list|)
control|)
block|{
name|truncate
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
comment|// Might not need a sledge-hammer to reload the core
name|JettySolrRunner
name|jetty
init|=
name|cluster
operator|.
name|getReplicaJetty
argument_list|(
name|replica
argument_list|)
decl_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForState
argument_list|(
literal|"Expected a collection with one shard and two replicas"
argument_list|,
name|collection
argument_list|,
name|clusterShape
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|QueryResponse
name|resp
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collection
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getSegmentFiles
specifier|private
name|File
index|[]
name|getSegmentFiles
parameter_list|(
name|Replica
name|replica
parameter_list|)
block|{
try|try
init|(
name|SolrCore
name|core
init|=
name|cluster
operator|.
name|getReplicaJetty
argument_list|(
name|replica
argument_list|)
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getCore
argument_list|(
name|replica
operator|.
name|getCoreName
argument_list|()
argument_list|)
init|)
block|{
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
argument_list|,
literal|"index"
argument_list|)
decl_stmt|;
return|return
name|indexDir
operator|.
name|listFiles
argument_list|(
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
lambda|->
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"segments_"
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
block|}
DECL|method|truncate
specifier|private
name|void
name|truncate
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|Files
operator|.
name|write
argument_list|(
name|file
operator|.
name|toPath
argument_list|()
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|StandardOpenOption
operator|.
name|TRUNCATE_EXISTING
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

