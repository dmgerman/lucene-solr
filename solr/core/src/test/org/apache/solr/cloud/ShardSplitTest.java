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
name|net
operator|.
name|MalformedURLException
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|params
operator|.
name|CoreConnectionPNames
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
name|SolrServer
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
name|SolrDocument
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
name|HashBasedRouter
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
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
operator|.
name|CollectionsHandler
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

begin_class
DECL|class|ShardSplitTest
specifier|public
class|class
name|ShardSplitTest
extends|extends
name|BasicDistributedZkTest
block|{
DECL|field|SHARD1_0
specifier|public
specifier|static
specifier|final
name|String
name|SHARD1_0
init|=
name|SHARD1
operator|+
literal|"_0"
decl_stmt|;
DECL|field|SHARD1_1
specifier|public
specifier|static
specifier|final
name|String
name|SHARD1_1
init|=
name|SHARD1
operator|+
literal|"_1"
decl_stmt|;
DECL|method|ShardSplitTest
specifier|public
name|ShardSplitTest
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
name|ClusterState
name|clusterState
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
specifier|final
name|DocRouter
name|router
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|)
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|Slice
name|shard1
init|=
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1
argument_list|)
decl_stmt|;
name|DocRouter
operator|.
name|Range
name|shard1Range
init|=
name|shard1
operator|.
name|getRange
argument_list|()
operator|!=
literal|null
condition|?
name|shard1
operator|.
name|getRange
argument_list|()
else|:
name|router
operator|.
name|fullRange
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
init|=
name|router
operator|.
name|partitionRange
argument_list|(
literal|2
argument_list|,
name|shard1Range
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|docCounts
init|=
operator|new
name|int
index|[
name|ranges
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|numReplicas
init|=
name|shard1
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|0
init|;
name|id
operator|<=
literal|100
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
name|indexAndUpdateCount
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|shardKey
operator|+
literal|"!"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|Thread
name|indexThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|max
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|401
argument_list|)
decl_stmt|;
name|int
name|sleep
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|25
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SHARDSPLITTEST: Going to add "
operator|+
name|max
operator|+
literal|" number of docs at 1 doc per "
operator|+
name|sleep
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|deleted
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|id
init|=
literal|101
init|;
name|id
operator|<
name|max
condition|;
name|id
operator|++
control|)
block|{
try|try
block|{
name|indexAndUpdateCount
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
if|if
condition|(
name|usually
argument_list|(
name|random
argument_list|)
condition|)
block|{
name|String
name|delId
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|id
operator|-
literal|101
operator|+
literal|1
argument_list|)
operator|+
literal|101
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleted
operator|.
name|contains
argument_list|(
name|delId
argument_list|)
condition|)
continue|continue;
try|try
block|{
name|deleteAndUpdateCount
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|delId
argument_list|)
expr_stmt|;
name|deleted
operator|.
name|add
argument_list|(
name|delId
argument_list|)
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
literal|"Exception while deleting docs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Exception while adding docs"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|splitShard
argument_list|(
name|SHARD1
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Layout after split: \n"
argument_list|)
expr_stmt|;
name|printLayout
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|HttpSolrServer
operator|.
name|RemoteSolrException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|code
argument_list|()
operator|!=
literal|500
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|log
operator|.
name|error
argument_list|(
literal|"SPLITSHARD failed. "
operator|+
operator|(
name|i
operator|<
literal|2
condition|?
literal|" Retring split"
else|:
literal|""
operator|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
literal|2
condition|)
block|{
name|fail
argument_list|(
literal|"SPLITSHARD was not successful even after three tries"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
try|try
block|{
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Indexing thread interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|checkDocCountsAndShardStates
argument_list|(
name|docCounts
argument_list|,
name|numReplicas
argument_list|)
expr_stmt|;
comment|// todo can't call waitForThingsToLevelOut because it looks for jettys of all shards
comment|// and the new sub-shards don't have any.
name|waitForRecoveriesToFinish
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//waitForThingsToLevelOut(15);
block|}
DECL|method|checkDocCountsAndShardStates
specifier|protected
name|void
name|checkDocCountsAndShardStates
parameter_list|(
name|int
index|[]
name|docCounts
parameter_list|,
name|int
name|numReplicas
parameter_list|)
throws|throws
name|Exception
block|{
name|ClusterState
name|clusterState
init|=
literal|null
decl_stmt|;
name|Slice
name|slice1_0
init|=
literal|null
decl_stmt|,
name|slice1_1
init|=
literal|null
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|i
operator|=
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
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|slice1_0
operator|=
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard1_0"
argument_list|)
expr_stmt|;
name|slice1_1
operator|=
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
literal|"shard1_1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Slice
operator|.
name|ACTIVE
operator|.
name|equals
argument_list|(
name|slice1_0
operator|.
name|getState
argument_list|()
argument_list|)
operator|&&
name|Slice
operator|.
name|ACTIVE
operator|.
name|equals
argument_list|(
name|slice1_1
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"ShardSplitTest waited for {} ms for shard state to be set to active"
argument_list|,
name|i
operator|*
literal|500
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Cluster state does not contain shard1_0"
argument_list|,
name|slice1_0
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Cluster state does not contain shard1_0"
argument_list|,
name|slice1_1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shard1_0 is not active"
argument_list|,
name|Slice
operator|.
name|ACTIVE
argument_list|,
name|slice1_0
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shard1_1 is not active"
argument_list|,
name|Slice
operator|.
name|ACTIVE
argument_list|,
name|slice1_1
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of replicas created for shard1_0"
argument_list|,
name|numReplicas
argument_list|,
name|slice1_0
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of replicas created for shard1_1"
argument_list|,
name|numReplicas
argument_list|,
name|slice1_1
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// can't use checkShardConsistency because it insists on jettys and clients for each shard
name|checkSubShardConsistency
argument_list|(
name|SHARD1_0
argument_list|)
expr_stmt|;
name|checkSubShardConsistency
argument_list|(
name|SHARD1_1
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|query
init|=
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
operator|.
name|setFields
argument_list|(
literal|"id"
argument_list|,
literal|"_version_"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ZkCoreNodeProps
name|shard1_0
init|=
name|getLeaderUrlFromZk
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1_0
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|shard1_0Server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|shard1_0
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|shard1_0Server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|long
name|shard10Count
init|=
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|ZkCoreNodeProps
name|shard1_1
init|=
name|getLeaderUrlFromZk
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|SHARD1_1
argument_list|)
decl_stmt|;
name|HttpSolrServer
name|shard1_1Server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|shard1_1
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
decl_stmt|;
name|QueryResponse
name|response2
init|=
name|shard1_1Server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|long
name|shard11Count
init|=
name|response2
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|logDebugHelp
argument_list|(
name|docCounts
argument_list|,
name|response
argument_list|,
name|shard10Count
argument_list|,
name|response2
argument_list|,
name|shard11Count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong doc count on shard1_0"
argument_list|,
name|docCounts
index|[
literal|0
index|]
argument_list|,
name|shard10Count
argument_list|)
expr_stmt|;
comment|//assertEquals("Wrong doc count on shard1_1", docCounts[1], shard11Count);
block|}
DECL|method|checkSubShardConsistency
specifier|protected
name|void
name|checkSubShardConsistency
parameter_list|(
name|String
name|shard
parameter_list|)
throws|throws
name|SolrServerException
block|{
name|SolrQuery
name|query
init|=
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
operator|.
name|setFields
argument_list|(
literal|"id"
argument_list|,
literal|"_version_"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
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
name|clusterState
operator|.
name|getSlice
argument_list|(
name|AbstractDistribZkTestBase
operator|.
name|DEFAULT_COLLECTION
argument_list|,
name|shard
argument_list|)
decl_stmt|;
name|long
index|[]
name|numFound
init|=
operator|new
name|long
index|[
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|c
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|String
name|coreUrl
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|replica
argument_list|)
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|coreUrl
argument_list|)
decl_stmt|;
name|QueryResponse
name|response
init|=
name|server
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|numFound
index|[
name|c
operator|++
index|]
operator|=
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Shard: "
operator|+
name|shard
operator|+
literal|" Replica: {} has {} docs"
argument_list|,
name|coreUrl
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Shard: "
operator|+
name|shard
operator|+
literal|" Replica: "
operator|+
name|coreUrl
operator|+
literal|" has 0 docs"
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|shard
operator|+
literal|" is not consistent"
argument_list|,
name|numFound
index|[
literal|0
index|]
argument_list|,
name|numFound
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|splitShard
specifier|protected
name|void
name|splitShard
parameter_list|(
name|String
name|shardId
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
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
name|SPLITSHARD
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
literal|"collection1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"shard"
argument_list|,
name|shardId
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
call|(
name|int
call|)
argument_list|(
name|CollectionsHandler
operator|.
name|DEFAULT_ZK_TIMEOUT
operator|*
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|baseServer
operator|.
name|request
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|indexAndUpdateCount
specifier|protected
name|void
name|indexAndUpdateCount
parameter_list|(
name|DocRouter
name|router
parameter_list|,
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
parameter_list|,
name|int
index|[]
name|docCounts
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|Exception
block|{
name|index
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
literal|"n_ti"
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
name|getHashRangeIdx
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|!=
operator|-
literal|1
condition|)
block|{
name|docCounts
index|[
name|idx
index|]
operator|++
expr_stmt|;
block|}
block|}
DECL|method|deleteAndUpdateCount
specifier|protected
name|void
name|deleteAndUpdateCount
parameter_list|(
name|DocRouter
name|router
parameter_list|,
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
parameter_list|,
name|int
index|[]
name|docCounts
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|controlClient
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|deleteById
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
name|getHashRangeIdx
argument_list|(
name|router
argument_list|,
name|ranges
argument_list|,
name|docCounts
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|!=
operator|-
literal|1
condition|)
block|{
name|docCounts
index|[
name|idx
index|]
operator|--
expr_stmt|;
block|}
block|}
DECL|method|getHashRangeIdx
specifier|private
name|int
name|getHashRangeIdx
parameter_list|(
name|DocRouter
name|router
parameter_list|,
name|List
argument_list|<
name|DocRouter
operator|.
name|Range
argument_list|>
name|ranges
parameter_list|,
name|int
index|[]
name|docCounts
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|int
name|hash
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|router
operator|instanceof
name|HashBasedRouter
condition|)
block|{
name|HashBasedRouter
name|hashBasedRouter
init|=
operator|(
name|HashBasedRouter
operator|)
name|router
decl_stmt|;
name|hash
operator|=
name|hashBasedRouter
operator|.
name|sliceHash
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ranges
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DocRouter
operator|.
name|Range
name|range
init|=
name|ranges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|range
operator|.
name|includes
argument_list|(
name|hash
argument_list|)
condition|)
return|return
name|i
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|logDebugHelp
specifier|protected
name|void
name|logDebugHelp
parameter_list|(
name|int
index|[]
name|docCounts
parameter_list|,
name|QueryResponse
name|response
parameter_list|,
name|long
name|shard10Count
parameter_list|,
name|QueryResponse
name|response2
parameter_list|,
name|long
name|shard11Count
parameter_list|)
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
name|docCounts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docCount
init|=
name|docCounts
index|[
name|i
index|]
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Expected docCount for shard1_{} = {}"
argument_list|,
name|i
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Actual docCount for shard1_0 = {}"
argument_list|,
name|shard10Count
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Actual docCount for shard1_1 = {}"
argument_list|,
name|shard11Count
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|idVsVersion
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocument
argument_list|>
name|shard10Docs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrDocument
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrDocument
argument_list|>
name|shard11Docs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrDocument
argument_list|>
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
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SolrDocument
name|document
init|=
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|idVsVersion
operator|.
name|put
argument_list|(
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"_version_"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SolrDocument
name|old
init|=
name|shard10Docs
operator|.
name|put
argument_list|(
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"EXTRA: ID: "
operator|+
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" on shard1_0. Old version: "
operator|+
name|old
operator|.
name|getFieldValue
argument_list|(
literal|"_version_"
argument_list|)
operator|+
literal|" new version: "
operator|+
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"_version_"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|response2
operator|.
name|getResults
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SolrDocument
name|document
init|=
name|response2
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|version
init|=
name|idVsVersion
operator|.
name|get
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"DUPLICATE: ID: "
operator|+
name|value
operator|+
literal|" , shard1_0Version: "
operator|+
name|version
operator|+
literal|" shard1_1Version:"
operator|+
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"_version_"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SolrDocument
name|old
init|=
name|shard11Docs
operator|.
name|put
argument_list|(
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|document
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"EXTRA: ID: "
operator|+
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
operator|+
literal|" on shard1_1. Old version: "
operator|+
name|old
operator|.
name|getFieldValue
argument_list|(
literal|"_version_"
argument_list|)
operator|+
literal|" new version: "
operator|+
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"_version_"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createNewSolrServer
specifier|protected
name|SolrServer
name|createNewSolrServer
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|baseUrl
parameter_list|)
block|{
name|HttpSolrServer
name|server
init|=
operator|(
name|HttpSolrServer
operator|)
name|super
operator|.
name|createNewSolrServer
argument_list|(
name|collection
argument_list|,
name|baseUrl
argument_list|)
decl_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
annotation|@
name|Override
DECL|method|createNewSolrServer
specifier|protected
name|SolrServer
name|createNewSolrServer
parameter_list|(
name|int
name|port
parameter_list|)
block|{
name|HttpSolrServer
name|server
init|=
operator|(
name|HttpSolrServer
operator|)
name|super
operator|.
name|createNewSolrServer
argument_list|(
name|port
argument_list|)
decl_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
annotation|@
name|Override
DECL|method|createCloudClient
specifier|protected
name|CloudSolrServer
name|createCloudClient
parameter_list|(
name|String
name|defaultCollection
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|CloudSolrServer
name|client
init|=
name|super
operator|.
name|createCloudClient
argument_list|(
name|defaultCollection
argument_list|)
decl_stmt|;
name|client
operator|.
name|getLbServer
argument_list|()
operator|.
name|getHttpClient
argument_list|()
operator|.
name|getParams
argument_list|()
operator|.
name|setParameter
argument_list|(
name|CoreConnectionPNames
operator|.
name|SO_TIMEOUT
argument_list|,
literal|5
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

