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
name|solr
operator|.
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|util
operator|.
name|RetryUtil
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

begin_comment
comment|/**  * Verifies cluster state remains consistent after collection reload.  */
end_comment

begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|CollectionReloadTest
specifier|public
class|class
name|CollectionReloadTest
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
literal|1
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
DECL|method|testReloadedLeaderStateAfterZkSessionLoss
specifier|public
name|void
name|testReloadedLeaderStateAfterZkSessionLoss
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"testReloadedLeaderStateAfterZkSessionLoss initialized OK ... running test logic"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|testCollectionName
init|=
literal|"c8n_1x1"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|testCollectionName
argument_list|,
literal|"conf"
argument_list|,
literal|1
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
name|Replica
name|leader
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
name|DEFAULT_TIMEOUT
argument_list|)
decl_stmt|;
name|long
name|coreStartTime
init|=
name|getCoreStatus
argument_list|(
name|leader
argument_list|)
operator|.
name|getCoreStartTime
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|reloadCollection
argument_list|(
name|testCollectionName
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
name|RetryUtil
operator|.
name|retryUntil
argument_list|(
literal|"Timed out waiting for core to reload"
argument_list|,
literal|30
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
parameter_list|()
lambda|->
block|{
name|long
name|restartTime
init|=
literal|0
decl_stmt|;
try|try
block|{
name|restartTime
operator|=
name|getCoreStatus
argument_list|(
name|leader
argument_list|)
operator|.
name|getCoreStartTime
argument_list|()
operator|.
name|getTime
argument_list|()
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
literal|"Exception getting core start time: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|restartTime
operator|>
name|coreStartTime
return|;
block|}
argument_list|)
expr_stmt|;
specifier|final
name|int
name|initialStateVersion
init|=
name|getCollectionState
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|getZNodeVersion
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|expireZkSession
argument_list|(
name|cluster
operator|.
name|getReplicaJetty
argument_list|(
name|leader
argument_list|)
argument_list|)
expr_stmt|;
name|waitForState
argument_list|(
literal|"Timed out waiting for core to re-register as ACTIVE after session expiry"
argument_list|,
name|testCollectionName
argument_list|,
parameter_list|(
name|n
parameter_list|,
name|c
parameter_list|)
lambda|->
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Collection state: {}"
argument_list|,
name|c
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Replica
name|expiredReplica
init|=
name|c
operator|.
name|getReplica
argument_list|(
name|leader
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|expiredReplica
operator|.
name|getState
argument_list|()
operator|==
name|Replica
operator|.
name|State
operator|.
name|ACTIVE
operator|&&
name|c
operator|.
name|getZNodeVersion
argument_list|()
operator|>
name|initialStateVersion
return|;
block|}
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"testReloadedLeaderStateAfterZkSessionLoss succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

