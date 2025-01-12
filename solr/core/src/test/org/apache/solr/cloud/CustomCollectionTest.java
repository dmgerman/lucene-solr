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
name|TestUtil
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
name|DocCollection
operator|.
name|DOC_ROUTER
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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|REPLICATION_FACTOR
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
name|ShardParams
operator|.
name|_ROUTE_
import|;
end_import

begin_comment
comment|/**  * Tests the Custom Sharding API.  */
end_comment

begin_class
DECL|class|CustomCollectionTest
specifier|public
class|class
name|CustomCollectionTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|NODE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|NODE_COUNT
init|=
literal|4
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
name|NODE_COUNT
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf"
argument_list|,
name|configset
argument_list|(
literal|"cloud-dynamic"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|ensureClusterEmpty
specifier|public
name|void
name|ensureClusterEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|.
name|deleteAllCollections
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomCollectionsAPI
specifier|public
name|void
name|testCustomCollectionsAPI
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collection
init|=
literal|"implicitcoll"
decl_stmt|;
name|int
name|replicationFactor
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
operator|+
literal|2
decl_stmt|;
name|int
name|numShards
init|=
literal|3
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
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
name|NODE_COUNT
operator|)
operator|+
literal|1
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
name|collection
argument_list|,
literal|"conf"
argument_list|,
literal|"a,b,c"
argument_list|,
name|replicationFactor
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
name|maxShardsPerNode
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
name|DocCollection
name|coll
init|=
name|getCollectionState
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"implicit"
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|coll
operator|.
name|get
argument_list|(
name|DOC_ROUTER
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|coll
operator|.
name|getStr
argument_list|(
name|REPLICATION_FACTOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|coll
operator|.
name|getStr
argument_list|(
name|MAX_SHARDS_PER_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"A shard of a Collection configured with implicit router must have null range"
argument_list|,
name|coll
operator|.
name|getSlice
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getRange
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|)
operator|.
name|withRoute
argument_list|(
literal|"a"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
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
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
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
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"b"
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
name|assertEquals
argument_list|(
literal|3
argument_list|,
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
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"a"
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
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|deleteByQuery
argument_list|(
name|collection
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
name|collection
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
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
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|)
operator|.
name|withRoute
argument_list|(
literal|"c"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
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
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
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
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"a"
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
name|assertEquals
argument_list|(
literal|3
argument_list|,
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
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"c"
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
comment|//Testing CREATESHARD
name|CollectionAdminRequest
operator|.
name|createShard
argument_list|(
name|collection
argument_list|,
literal|"x"
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
literal|"Expected shard 'x' to be active"
argument_list|,
name|collection
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|c
operator|.
name|getSlice
argument_list|(
literal|"x"
argument_list|)
operator|==
literal|null
condition|)
return|return
literal|false
return|;
for|for
control|(
name|Replica
name|r
range|:
name|c
operator|.
name|getSlice
argument_list|(
literal|"x"
argument_list|)
control|)
block|{
if|if
condition|(
name|r
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
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
argument_list|)
expr_stmt|;
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"66"
argument_list|,
name|_ROUTE_
argument_list|,
literal|"x"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|collection
argument_list|)
expr_stmt|;
comment|// TODO - the local state is cached and causes the request to fail with 'unknown shard'
comment|// assertEquals(1, cluster.getSolrClient().query(collection, new SolrQuery("*:*").setParam(_ROUTE_, "x")).getResults().getNumFound());
block|}
annotation|@
name|Test
DECL|method|testRouteFieldForImplicitRouter
specifier|public
name|void
name|testRouteFieldForImplicitRouter
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numShards
init|=
literal|4
decl_stmt|;
name|int
name|replicationFactor
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
operator|+
literal|2
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
operator|(
operator|(
name|numShards
operator|*
name|replicationFactor
operator|)
operator|/
name|NODE_COUNT
operator|)
operator|+
literal|1
decl_stmt|;
name|String
name|shard_fld
init|=
literal|"shard_s"
decl_stmt|;
specifier|final
name|String
name|collection
init|=
literal|"withShardField"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
name|collection
argument_list|,
literal|"conf"
argument_list|,
literal|"a,b,c,d"
argument_list|,
name|replicationFactor
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
name|maxShardsPerNode
argument_list|)
operator|.
name|setRouterField
argument_list|(
name|shard_fld
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
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|shard_fld
argument_list|,
literal|"a"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
name|shard_fld
argument_list|,
literal|"a"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
name|shard_fld
argument_list|,
literal|"b"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
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
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
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
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"b"
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
name|assertEquals
argument_list|(
literal|2
argument_list|,
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
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"a"
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
block|}
annotation|@
name|Test
DECL|method|testRouteFieldForHashRouter
specifier|public
name|void
name|testRouteFieldForHashRouter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|collectionName
init|=
literal|"routeFieldColl"
decl_stmt|;
name|int
name|numShards
init|=
literal|4
decl_stmt|;
name|int
name|replicationFactor
init|=
literal|2
decl_stmt|;
name|int
name|maxShardsPerNode
init|=
operator|(
operator|(
name|numShards
operator|*
name|replicationFactor
operator|)
operator|/
name|NODE_COUNT
operator|)
operator|+
literal|1
decl_stmt|;
name|String
name|shard_fld
init|=
literal|"shard_s"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
name|numShards
argument_list|,
name|replicationFactor
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
name|maxShardsPerNode
argument_list|)
operator|.
name|setRouterField
argument_list|(
name|shard_fld
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
operator|new
name|UpdateRequest
argument_list|()
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|shard_fld
argument_list|,
literal|"a"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
name|shard_fld
argument_list|,
literal|"a"
argument_list|)
operator|.
name|add
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
name|shard_fld
argument_list|,
literal|"b"
argument_list|)
operator|.
name|commit
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
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
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"a"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"b"
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
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"c"
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
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|deleteByQuery
argument_list|(
name|collectionName
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
name|collectionName
argument_list|,
operator|new
name|SolrInputDocument
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|,
name|shard_fld
argument_list|,
literal|"c!doc1"
argument_list|)
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collectionName
argument_list|,
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setParam
argument_list|(
name|_ROUTE_
argument_list|,
literal|"c!"
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
block|}
annotation|@
name|Test
DECL|method|testCreateShardRepFactor
specifier|public
name|void
name|testCreateShardRepFactor
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|collectionName
init|=
literal|"testCreateShardRepFactor"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollectionWithImplicitRouter
argument_list|(
name|collectionName
argument_list|,
literal|"conf"
argument_list|,
literal|"a,b"
argument_list|,
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
name|CollectionAdminRequest
operator|.
name|createShard
argument_list|(
name|collectionName
argument_list|,
literal|"x"
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
literal|"Not enough active replicas in shard 'x'"
argument_list|,
name|collectionName
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
return|return
name|c
operator|.
name|getSlice
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

