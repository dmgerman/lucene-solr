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
name|CloudSolrServer
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
name|HttpSolrServer
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
name|RoutingRule
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
name|ZkNodeProps
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
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|DirectUpdateHandler2
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
name|REPLICATION_FACTOR
import|;
end_import

begin_class
DECL|class|MigrateRouteKeyTest
specifier|public
class|class
name|MigrateRouteKeyTest
extends|extends
name|BasicDistributedZkTest
block|{
DECL|method|MigrateRouteKeyTest
specifier|public
name|MigrateRouteKeyTest
parameter_list|()
block|{
name|schemaString
operator|=
literal|"schema15.xml"
expr_stmt|;
comment|// we need a string id
block|}
annotation|@
name|Override
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
block|}
annotation|@
name|Override
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
if|if
condition|(
name|VERBOSE
operator|||
name|printLayoutOnTearDown
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|controlClient
operator|!=
literal|null
condition|)
block|{
name|controlClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cloudClient
operator|!=
literal|null
condition|)
block|{
name|cloudClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|controlClientCloud
operator|!=
literal|null
condition|)
block|{
name|controlClientCloud
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"numShards"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.xml.persist"
argument_list|)
expr_stmt|;
comment|// insurance
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForThingsToLevelOut
argument_list|(
literal|15
argument_list|)
expr_stmt|;
specifier|final
name|String
name|splitKey
init|=
literal|"a!"
decl_stmt|;
specifier|final
name|int
index|[]
name|splitKeyCount
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<
literal|26
operator|*
literal|3
condition|;
name|id
operator|++
control|)
block|{
name|String
name|shardKey
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
operator|(
name|id
operator|%
literal|26
operator|)
argument_list|)
decl_stmt|;
comment|// See comment in ShardRoutingTest for hash distribution
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
name|shardKey
operator|+
literal|"!"
operator|+
name|id
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"n_ti"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|splitKey
operator|.
name|equals
argument_list|(
name|shardKey
operator|+
literal|"!"
argument_list|)
condition|)
name|splitKeyCount
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|splitKeyCount
index|[
literal|0
index|]
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|targetCollection
init|=
literal|"migrate_routekey_test_targetCollection"
decl_stmt|;
name|HashMap
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
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|CloudSolrServer
name|client
init|=
literal|null
decl_stmt|;
try|try
block|{
name|client
operator|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
init|=
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
name|REPLICATION_FACTOR
argument_list|,
literal|1
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|,
literal|5
argument_list|,
name|NUM_SLICES
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|createCollection
argument_list|(
name|collectionInfos
argument_list|,
name|targetCollection
argument_list|,
name|props
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|collectionInfos
operator|.
name|get
argument_list|(
name|targetCollection
argument_list|)
decl_stmt|;
name|checkForCollection
argument_list|(
name|targetCollection
argument_list|,
name|list
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
name|targetCollection
argument_list|,
literal|false
argument_list|)
expr_stmt|;
class|class
name|Indexer
extends|extends
name|Thread
block|{
specifier|final
name|int
name|seconds
decl_stmt|;
specifier|public
name|Indexer
parameter_list|(
name|int
name|seconds
parameter_list|)
block|{
name|this
operator|.
name|seconds
operator|=
name|seconds
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|26
operator|*
literal|3
init|;
name|id
operator|<
literal|500
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|<=
name|seconds
operator|*
literal|1000
condition|;
name|id
operator|++
control|)
block|{
name|String
name|shardKey
init|=
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
operator|(
name|id
operator|%
literal|26
operator|)
argument_list|)
decl_stmt|;
comment|// See comment in ShardRoutingTest for hash distribution
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
name|shardKey
operator|+
literal|"!"
operator|+
name|id
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"n_ti"
argument_list|,
name|id
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|splitKey
operator|.
name|equals
argument_list|(
name|shardKey
operator|+
literal|"!"
argument_list|)
condition|)
name|splitKeyCount
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception while adding document id: "
operator|+
name|doc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|Thread
name|indexer
init|=
operator|new
name|Indexer
argument_list|(
literal|30
argument_list|)
decl_stmt|;
name|indexer
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|url
init|=
name|CustomCollectionTest
operator|.
name|getUrlFromZk
argument_list|(
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
argument_list|,
name|targetCollection
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|collectionClient
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DocCount on target collection does not match"
argument_list|,
literal|0
argument_list|,
name|collectionClient
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
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
name|MIGRATE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"collection"
argument_list|,
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"target.collection"
argument_list|,
name|targetCollection
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"split.key"
argument_list|,
name|splitKey
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"forward.timeout"
argument_list|,
literal|45
argument_list|)
expr_stmt|;
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
name|String
name|baseUrl
init|=
operator|(
operator|(
name|HttpSolrServer
operator|)
name|shardToJetty
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|client
operator|.
name|solrClient
operator|)
operator|.
name|getBaseURL
argument_list|()
decl_stmt|;
name|baseUrl
operator|=
name|baseUrl
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|baseUrl
operator|.
name|length
argument_list|()
operator|-
literal|"collection1"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|HttpSolrServer
name|baseServer
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
name|baseServer
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|setSoTimeout
argument_list|(
literal|60000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|long
name|finishTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|indexer
operator|.
name|join
argument_list|()
expr_stmt|;
try|try
block|{
name|cloudClient
operator|.
name|deleteById
argument_list|(
literal|"a!104"
argument_list|)
expr_stmt|;
name|splitKeyCount
index|[
literal|0
index|]
operator|--
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error deleting document a!104"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|collectionClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|getCommonCloudSolrServer
argument_list|()
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
name|state
init|=
name|getCommonCloudSolrServer
argument_list|()
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
name|state
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Routing rule map is null"
argument_list|,
name|slice
operator|.
name|getRoutingRules
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Routing rule map is empty"
argument_list|,
name|slice
operator|.
name|getRoutingRules
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"No routing rule exists for route key: "
operator|+
name|splitKey
argument_list|,
name|slice
operator|.
name|getRoutingRules
argument_list|()
operator|.
name|get
argument_list|(
name|splitKey
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|ruleRemoved
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|finishTime
operator|<
literal|60000
condition|)
block|{
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|state
operator|=
name|getCommonCloudSolrServer
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|slice
operator|=
name|state
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingRule
argument_list|>
name|routingRules
init|=
name|slice
operator|.
name|getRoutingRules
argument_list|()
decl_stmt|;
if|if
condition|(
name|routingRules
operator|==
literal|null
operator|||
name|routingRules
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|routingRules
operator|.
name|containsKey
argument_list|(
name|splitKey
argument_list|)
condition|)
block|{
name|ruleRemoved
operator|=
literal|true
expr_stmt|;
break|break;
block|}
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
name|splitKey
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Routing rule was not expired"
argument_list|,
name|ruleRemoved
argument_list|)
expr_stmt|;
name|solrQuery
operator|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|setRows
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|collectionClient
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Response from target collection: "
operator|+
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"DocCount on shard1_0 does not match"
argument_list|,
name|splitKeyCount
index|[
literal|0
index|]
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

