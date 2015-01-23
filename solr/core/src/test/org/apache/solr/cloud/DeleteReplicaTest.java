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
name|SolrRequest
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
name|SolrException
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
name|MapSolrParams
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
name|SolrParams
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|NUM_SLICES
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
name|OverseerCollectionProcessor
operator|.
name|ONLY_IF_DOWN
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
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
operator|.
name|makeMap
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|MAX_SHARDS_PER_NODE
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
name|common
operator|.
name|params
operator|.
name|CollectionParams
operator|.
name|CollectionAction
operator|.
name|DELETEREPLICA
import|;
end_import

begin_class
DECL|class|DeleteReplicaTest
specifier|public
class|class
name|DeleteReplicaTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|client
specifier|private
name|CloudSolrClient
name|client
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeThisClass2
specifier|public
specifier|static
name|void
name|beforeThisClass2
parameter_list|()
throws|throws
name|Exception
block|{    }
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
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|client
operator|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|DeleteReplicaTest
specifier|public
name|DeleteReplicaTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|checkCreatedVsState
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|4
argument_list|)
DECL|method|deleteLiveReplicaTest
specifier|public
name|void
name|deleteLiveReplicaTest
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"delLiveColl"
decl_stmt|;
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|createCollection
argument_list|(
name|collectionName
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|collectionName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|DocCollection
name|testcoll
init|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Slice
name|shard1
init|=
literal|null
decl_stmt|;
name|Replica
name|replica1
init|=
literal|null
decl_stmt|;
comment|// Get an active replica
for|for
control|(
name|Slice
name|slice
range|:
name|testcoll
operator|.
name|getSlices
argument_list|()
control|)
block|{
if|if
condition|(
name|replica1
operator|!=
literal|null
condition|)
break|break;
if|if
condition|(
literal|"active"
operator|.
name|equals
argument_list|(
name|slice
operator|.
name|getStr
argument_list|(
literal|"state"
argument_list|)
argument_list|)
condition|)
block|{
name|shard1
operator|=
name|slice
expr_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|shard1
operator|.
name|getReplicas
argument_list|()
control|)
block|{
if|if
condition|(
literal|"active"
operator|.
name|equals
argument_list|(
name|replica
operator|.
name|getStr
argument_list|(
literal|"state"
argument_list|)
argument_list|)
condition|)
block|{
name|replica1
operator|=
name|replica
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
name|replica1
operator|==
literal|null
condition|)
name|fail
argument_list|(
literal|"no active replicas found"
argument_list|)
expr_stmt|;
name|HttpSolrClient
name|replica1Client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
literal|"base_url"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|dataDir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CoreAdminResponse
name|status
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
argument_list|,
name|replica1Client
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|coreStatus
init|=
name|status
operator|.
name|getCoreStatus
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
argument_list|)
decl_stmt|;
name|dataDir
operator|=
operator|(
name|String
operator|)
name|coreStatus
operator|.
name|get
argument_list|(
literal|"dataDir"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|replica1Client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// Should not be able to delete a replica that is up if onlyIfDown=true.
name|tryToRemoveOnlyIfDown
argument_list|(
name|collectionName
argument_list|,
name|client
argument_list|,
name|replica1
argument_list|,
name|shard1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception here because the replica is NOT down"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Should see 400 here "
argument_list|,
name|se
operator|.
name|code
argument_list|()
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have had a good message here"
argument_list|,
name|se
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"with onlyIfDown='true', but state is 'active'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// This bit is a little weak in that if we're screwing up and actually deleting the replica, we might get back
comment|// here _before_ the datadir is deleted. But I'd rather not introduce a delay here.
name|assertTrue
argument_list|(
literal|"dataDir for "
operator|+
name|replica1
operator|.
name|getName
argument_list|()
operator|+
literal|" should NOT have been deleted by deleteReplica API with onlyIfDown='true'"
argument_list|,
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|removeAndWaitForReplicaGone
argument_list|(
name|collectionName
argument_list|,
name|client
argument_list|,
name|replica1
argument_list|,
name|shard1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"dataDir for "
operator|+
name|replica1
operator|.
name|getName
argument_list|()
operator|+
literal|" should have been deleted by deleteReplica API"
argument_list|,
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|tryToRemoveOnlyIfDown
specifier|protected
name|void
name|tryToRemoveOnlyIfDown
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|CloudSolrClient
name|client
parameter_list|,
name|Replica
name|replica
parameter_list|,
name|String
name|shard
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
name|collectionName
argument_list|,
literal|"action"
argument_list|,
name|DELETEREPLICA
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"shard"
argument_list|,
name|shard
argument_list|,
literal|"replica"
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|ONLY_IF_DOWN
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|SolrRequest
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
block|}
DECL|method|removeAndWaitForReplicaGone
specifier|protected
name|void
name|removeAndWaitForReplicaGone
parameter_list|(
name|String
name|COLL_NAME
parameter_list|,
name|CloudSolrClient
name|client
parameter_list|,
name|Replica
name|replica
parameter_list|,
name|String
name|shard
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"collection"
argument_list|,
name|COLL_NAME
argument_list|,
literal|"action"
argument_list|,
name|DELETEREPLICA
operator|.
name|toLower
argument_list|()
argument_list|,
literal|"shard"
argument_list|,
name|shard
argument_list|,
literal|"replica"
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|SolrRequest
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
name|long
name|endAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|3000
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|DocCollection
name|testcoll
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endAt
condition|)
block|{
name|testcoll
operator|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|COLL_NAME
argument_list|)
expr_stmt|;
name|success
operator|=
name|testcoll
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
operator|.
name|getReplica
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"replica cleaned up {}/{} core {}"
argument_list|,
name|shard
operator|+
literal|"/"
operator|+
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|replica
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"current state {}"
argument_list|,
name|testcoll
argument_list|)
expr_stmt|;
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Replica not cleaned up"
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|createCollection
specifier|protected
name|void
name|createCollection
parameter_list|(
name|String
name|COLL_NAME
parameter_list|,
name|CloudSolrClient
name|client
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|replicationFactor
init|=
literal|2
decl_stmt|;
name|int
name|numShards
init|=
literal|2
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
operator|(
operator|(
operator|(
operator|(
name|numShards
operator|+
literal|1
operator|)
operator|*
name|replicationFactor
operator|)
operator|/
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|)
operator|+
literal|1
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|makeMap
argument_list|(
name|ZkStateReader
operator|.
name|REPLICATION_FACTOR
argument_list|,
name|replicationFactor
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|,
name|maxShardsPerNode
argument_list|,
name|NUM_SLICES
argument_list|,
name|numShards
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|collectionInfos
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|createCollection
argument_list|(
name|collectionInfos
argument_list|,
name|COLL_NAME
argument_list|,
name|props
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

