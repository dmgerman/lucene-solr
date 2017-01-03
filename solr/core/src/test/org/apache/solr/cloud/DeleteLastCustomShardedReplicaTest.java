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
DECL|class|DeleteLastCustomShardedReplicaTest
specifier|public
class|class
name|DeleteLastCustomShardedReplicaTest
extends|extends
name|SolrCloudTestCase
block|{
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
name|collectionName
init|=
literal|"customcollreplicadeletion"
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
name|setMaxShardsPerNode
argument_list|(
literal|5
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
name|collectionState
init|=
name|getCollectionState
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
name|getRandomReplica
argument_list|(
name|collectionState
operator|.
name|getSlice
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|deleteReplica
argument_list|(
name|collectionName
argument_list|,
literal|"a"
argument_list|,
name|replica
operator|.
name|getName
argument_list|()
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
literal|"Expected shard 'a' to have no replicas"
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
literal|"a"
argument_list|)
operator|==
literal|null
operator|||
name|c
operator|.
name|getSlice
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getReplicas
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

