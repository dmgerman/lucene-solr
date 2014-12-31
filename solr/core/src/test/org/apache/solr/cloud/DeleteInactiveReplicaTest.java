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
name|SolrClient
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
name|CoreAdminParams
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
name|net
operator|.
name|URL
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
name|CollectionsAPIDistributedZkTest
operator|.
name|setClusterProp
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

begin_comment
comment|//@Ignore("Not currently valid see SOLR-5580")
end_comment

begin_class
DECL|class|DeleteInactiveReplicaTest
specifier|public
class|class
name|DeleteInactiveReplicaTest
extends|extends
name|DeleteReplicaTest
block|{
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
name|deleteInactiveReplicaTest
argument_list|()
expr_stmt|;
block|}
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
block|}
DECL|method|deleteInactiveReplicaTest
specifier|private
name|void
name|deleteInactiveReplicaTest
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|createCloudClient
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|String
name|collectionName
init|=
literal|"delDeadColl"
decl_stmt|;
name|setClusterProp
argument_list|(
name|client
argument_list|,
name|ZkStateReader
operator|.
name|LEGACY_CLOUD
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
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
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|boolean
name|stopped
init|=
literal|false
decl_stmt|;
name|JettySolrRunner
name|stoppedJetty
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Replica
name|replica1
init|=
literal|null
decl_stmt|;
name|Slice
name|shard1
init|=
literal|null
decl_stmt|;
name|long
name|timeout
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|3000
decl_stmt|;
name|DocCollection
name|testcoll
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|stopped
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|timeout
condition|)
block|{
name|testcoll
operator|=
name|client
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
expr_stmt|;
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
name|sb
operator|.
name|append
argument_list|(
name|jetty
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|testcoll
operator|.
name|getActiveSlices
argument_list|()
control|)
block|{
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
for|for
control|(
name|JettySolrRunner
name|jetty
range|:
name|jettys
control|)
block|{
name|URL
name|baseUrl
init|=
literal|null
decl_stmt|;
try|try
block|{
name|baseUrl
operator|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
continue|continue;
block|}
if|if
condition|(
name|baseUrl
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|stoppedJetty
operator|=
name|jetty
expr_stmt|;
name|ChaosMonkey
operator|.
name|stop
argument_list|(
name|jetty
argument_list|)
expr_stmt|;
name|replica1
operator|=
name|replica
expr_stmt|;
name|shard1
operator|=
name|slice
expr_stmt|;
name|stopped
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|stopped
condition|)
block|{
name|fail
argument_list|(
literal|"Could not find jetty to stop in collection "
operator|+
name|testcoll
operator|+
literal|" jettys: "
operator|+
name|sb
argument_list|)
expr_stmt|;
block|}
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
name|client
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
expr_stmt|;
if|if
condition|(
operator|!
literal|"active"
operator|.
name|equals
argument_list|(
name|testcoll
operator|.
name|getSlice
argument_list|(
name|shard1
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getReplica
argument_list|(
name|replica1
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getStr
argument_list|(
name|Slice
operator|.
name|STATE
argument_list|)
argument_list|)
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|success
condition|)
break|break;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"removed_replicas {}/{} "
argument_list|,
name|shard1
operator|.
name|getName
argument_list|()
argument_list|,
name|replica1
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
name|ChaosMonkey
operator|.
name|start
argument_list|(
name|stoppedJetty
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"restarted jetty"
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
name|makeMap
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/cores"
argument_list|,
literal|"action"
argument_list|,
literal|"status"
argument_list|)
decl_stmt|;
name|SolrClient
name|queryClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resp
init|=
name|queryClient
operator|.
name|request
argument_list|(
operator|new
name|QueryRequest
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"The core is up and running again"
argument_list|,
operator|(
operator|(
name|NamedList
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
name|replica1
operator|.
name|getStr
argument_list|(
literal|"core"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|queryClient
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|queryClient
operator|=
literal|null
expr_stmt|;
name|Exception
name|exp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|m
operator|=
name|makeMap
argument_list|(
literal|"action"
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|CREATE
operator|.
name|toString
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
name|collectionName
argument_list|,
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
literal|"shard2"
argument_list|,
name|CoreAdminParams
operator|.
name|NAME
argument_list|,
literal|"testcore"
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
operator|new
name|MapSolrParams
argument_list|(
name|m
argument_list|)
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
literal|"/admin/cores"
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exp
operator|=
name|e
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"error_expected"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Exception expected"
argument_list|,
name|exp
argument_list|)
expr_stmt|;
name|setClusterProp
argument_list|(
name|client
argument_list|,
name|ZkStateReader
operator|.
name|LEGACY_CLOUD
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|client
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

