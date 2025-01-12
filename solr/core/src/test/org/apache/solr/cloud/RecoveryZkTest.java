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
name|List
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
DECL|class|RecoveryZkTest
specifier|public
class|class
name|RecoveryZkTest
extends|extends
name|SolrCloudTestCase
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
block|}
DECL|field|indexThread
specifier|private
name|StoppableIndexingThread
name|indexThread
decl_stmt|;
DECL|field|indexThread2
specifier|private
name|StoppableIndexingThread
name|indexThread2
decl_stmt|;
annotation|@
name|After
DECL|method|stopThreads
specifier|public
name|void
name|stopThreads
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|join
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
specifier|final
name|String
name|collection
init|=
literal|"recoverytest"
decl_stmt|;
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
comment|// start a couple indexing threads
name|int
index|[]
name|maxDocList
init|=
operator|new
name|int
index|[]
block|{
literal|300
block|,
literal|700
block|,
literal|1200
block|,
literal|1350
block|,
literal|3000
block|}
decl_stmt|;
name|int
index|[]
name|maxDocNightlyList
init|=
operator|new
name|int
index|[]
block|{
literal|3000
block|,
literal|7000
block|,
literal|12000
block|,
literal|30000
block|,
literal|45000
block|,
literal|60000
block|}
decl_stmt|;
name|int
name|maxDoc
decl_stmt|;
if|if
condition|(
operator|!
name|TEST_NIGHTLY
condition|)
block|{
name|maxDoc
operator|=
name|maxDocList
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDocList
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
else|else
block|{
name|maxDoc
operator|=
name|maxDocNightlyList
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|maxDocList
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Indexing {} documents"
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|indexThread
operator|=
operator|new
name|StoppableIndexingThread
argument_list|(
literal|null
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
literal|"1"
argument_list|,
literal|true
argument_list|,
name|maxDoc
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|indexThread2
operator|=
operator|new
name|StoppableIndexingThread
argument_list|(
literal|null
argument_list|,
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
literal|"2"
argument_list|,
literal|true
argument_list|,
name|maxDoc
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexThread2
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// give some time to index...
name|int
index|[]
name|waitTimes
init|=
operator|new
name|int
index|[]
block|{
literal|200
block|,
literal|2000
block|,
literal|3000
block|}
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTimes
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|waitTimes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// bring shard replica down
name|DocCollection
name|state
init|=
name|getCollectionState
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|Replica
name|leader
init|=
name|state
operator|.
name|getLeader
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
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
decl_stmt|;
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
comment|// wait a moment - lets allow some docs to be indexed so replication time is non 0
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTimes
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|waitTimes
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
argument_list|)
expr_stmt|;
comment|// bring shard replica up
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// make sure replication can start
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
comment|// stop indexing threads
name|indexThread
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|safeStop
argument_list|()
expr_stmt|;
name|indexThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|indexThread2
operator|.
name|join
argument_list|()
expr_stmt|;
operator|new
name|UpdateRequest
argument_list|()
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
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|waitForState
argument_list|(
name|collection
argument_list|,
literal|120
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|clusterShape
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that leader and replica have same doc count
name|state
operator|=
name|getCollectionState
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|assertShardConsistency
argument_list|(
name|state
operator|.
name|getSlice
argument_list|(
literal|"shard1"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|assertShardConsistency
specifier|private
name|void
name|assertShardConsistency
parameter_list|(
name|Slice
name|shard
parameter_list|,
name|boolean
name|expectDocs
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|shard
operator|.
name|getReplicas
argument_list|(
name|r
lambda|->
name|r
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|long
index|[]
name|numCounts
init|=
operator|new
name|long
index|[
name|replicas
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|replica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
operator|.
name|withHttpClient
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getHttpClient
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|numCounts
index|[
name|i
index|]
operator|=
name|client
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
operator|.
name|add
argument_list|(
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|replicas
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|numCounts
index|[
name|j
index|]
operator|!=
name|numCounts
index|[
name|j
operator|-
literal|1
index|]
condition|)
name|fail
argument_list|(
literal|"Mismatch in counts between replicas"
argument_list|)
expr_stmt|;
comment|// TODO improve this!
if|if
condition|(
name|numCounts
index|[
name|j
index|]
operator|==
literal|0
operator|&&
name|expectDocs
condition|)
name|fail
argument_list|(
literal|"Expected docs on shard "
operator|+
name|shard
operator|.
name|getName
argument_list|()
operator|+
literal|" but found none"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

