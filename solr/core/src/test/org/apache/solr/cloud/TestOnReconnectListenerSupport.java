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
name|Set
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
name|common
operator|.
name|cloud
operator|.
name|OnReconnect
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
name|CoreContainer
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
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|ZkIndexSchemaReader
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
name|CORE_NAME_PROP
import|;
end_import

begin_class
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|TestOnReconnectListenerSupport
specifier|public
class|class
name|TestOnReconnectListenerSupport
extends|extends
name|AbstractFullDistribZkTestBase
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
DECL|method|TestOnReconnectListenerSupport
specifier|public
name|TestOnReconnectListenerSupport
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|fixShardCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|initSysProperties
specifier|public
specifier|static
name|void
name|initSysProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig-managed-schema.xml"
return|;
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
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|String
name|testCollectionName
init|=
literal|"c8n_onreconnect_1x1"
decl_stmt|;
name|String
name|shardId
init|=
literal|"shard1"
decl_stmt|;
name|createCollectionRetry
argument_list|(
name|testCollectionName
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
name|Replica
name|leader
init|=
name|getShardLeader
argument_list|(
name|testCollectionName
argument_list|,
name|shardId
argument_list|,
literal|30
comment|/* timeout secs */
argument_list|)
decl_stmt|;
name|JettySolrRunner
name|leaderJetty
init|=
name|getJettyOnPort
argument_list|(
name|getReplicaPort
argument_list|(
name|leader
argument_list|)
argument_list|)
decl_stmt|;
comment|// get the ZkController for the node hosting the leader
name|CoreContainer
name|cores
init|=
name|leaderJetty
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
name|ZkController
name|zkController
init|=
name|cores
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"ZkController is null"
argument_list|,
name|zkController
argument_list|)
expr_stmt|;
name|String
name|leaderCoreName
init|=
name|leader
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|String
name|leaderCoreId
decl_stmt|;
try|try
init|(
name|SolrCore
name|leaderCore
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|leaderCoreName
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
literal|"SolrCore for "
operator|+
name|leaderCoreName
operator|+
literal|" not found!"
argument_list|,
name|leaderCore
argument_list|)
expr_stmt|;
name|leaderCoreId
operator|=
name|leaderCore
operator|.
name|getName
argument_list|()
operator|+
literal|":"
operator|+
name|leaderCore
operator|.
name|getStartNanoTime
argument_list|()
expr_stmt|;
block|}
comment|// verify the ZkIndexSchemaReader is a registered OnReconnect listener
name|Set
argument_list|<
name|OnReconnect
argument_list|>
name|listeners
init|=
name|zkController
operator|.
name|getCurrentOnReconnectListeners
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"ZkController returned null OnReconnect listeners"
argument_list|,
name|listeners
argument_list|)
expr_stmt|;
name|ZkIndexSchemaReader
name|expectedListener
init|=
literal|null
decl_stmt|;
for|for
control|(
name|OnReconnect
name|listener
range|:
name|listeners
control|)
block|{
if|if
condition|(
name|listener
operator|instanceof
name|ZkIndexSchemaReader
condition|)
block|{
name|ZkIndexSchemaReader
name|reader
init|=
operator|(
name|ZkIndexSchemaReader
operator|)
name|listener
decl_stmt|;
if|if
condition|(
name|leaderCoreId
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getUniqueCoreId
argument_list|()
argument_list|)
condition|)
block|{
name|expectedListener
operator|=
name|reader
expr_stmt|;
break|break;
block|}
block|}
block|}
name|assertNotNull
argument_list|(
literal|"ZkIndexSchemaReader for core "
operator|+
name|leaderCoreName
operator|+
literal|" not registered as an OnReconnect listener and should be"
argument_list|,
name|expectedListener
argument_list|)
expr_stmt|;
comment|// reload the collection
name|boolean
name|wasReloaded
init|=
name|reloadCollection
argument_list|(
name|leader
argument_list|,
name|testCollectionName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Collection '"
operator|+
name|testCollectionName
operator|+
literal|"' failed to reload within a reasonable amount of time!"
argument_list|,
name|wasReloaded
argument_list|)
expr_stmt|;
comment|// after reload, the new core should be registered as an OnReconnect listener and the old should not be
name|String
name|reloadedLeaderCoreId
decl_stmt|;
try|try
init|(
name|SolrCore
name|leaderCore
init|=
name|cores
operator|.
name|getCore
argument_list|(
name|leaderCoreName
argument_list|)
init|)
block|{
name|reloadedLeaderCoreId
operator|=
name|leaderCore
operator|.
name|getName
argument_list|()
operator|+
literal|":"
operator|+
name|leaderCore
operator|.
name|getStartNanoTime
argument_list|()
expr_stmt|;
block|}
comment|// they shouldn't be equal after reload
name|assertTrue
argument_list|(
operator|!
name|leaderCoreId
operator|.
name|equals
argument_list|(
name|reloadedLeaderCoreId
argument_list|)
argument_list|)
expr_stmt|;
name|listeners
operator|=
name|zkController
operator|.
name|getCurrentOnReconnectListeners
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"ZkController returned null OnReconnect listeners"
argument_list|,
name|listeners
argument_list|)
expr_stmt|;
name|expectedListener
operator|=
literal|null
expr_stmt|;
comment|// reset
for|for
control|(
name|OnReconnect
name|listener
range|:
name|listeners
control|)
block|{
if|if
condition|(
name|listener
operator|instanceof
name|ZkIndexSchemaReader
condition|)
block|{
name|ZkIndexSchemaReader
name|reader
init|=
operator|(
name|ZkIndexSchemaReader
operator|)
name|listener
decl_stmt|;
if|if
condition|(
name|leaderCoreId
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getUniqueCoreId
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Previous core "
operator|+
name|leaderCoreId
operator|+
literal|" should no longer be a registered OnReconnect listener! Current listeners: "
operator|+
name|listeners
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|reloadedLeaderCoreId
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getUniqueCoreId
argument_list|()
argument_list|)
condition|)
block|{
name|expectedListener
operator|=
name|reader
expr_stmt|;
break|break;
block|}
block|}
block|}
name|assertNotNull
argument_list|(
literal|"ZkIndexSchemaReader for core "
operator|+
name|reloadedLeaderCoreId
operator|+
literal|" not registered as an OnReconnect listener and should be"
argument_list|,
name|expectedListener
argument_list|)
expr_stmt|;
comment|// try to clean up
try|try
block|{
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
name|testCollectionName
argument_list|)
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed"
argument_list|,
name|testCollectionName
argument_list|)
expr_stmt|;
block|}
name|listeners
operator|=
name|zkController
operator|.
name|getCurrentOnReconnectListeners
argument_list|()
expr_stmt|;
for|for
control|(
name|OnReconnect
name|listener
range|:
name|listeners
control|)
block|{
if|if
condition|(
name|listener
operator|instanceof
name|ZkIndexSchemaReader
condition|)
block|{
name|ZkIndexSchemaReader
name|reader
init|=
operator|(
name|ZkIndexSchemaReader
operator|)
name|listener
decl_stmt|;
if|if
condition|(
name|reloadedLeaderCoreId
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getUniqueCoreId
argument_list|()
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Previous core "
operator|+
name|reloadedLeaderCoreId
operator|+
literal|" should no longer be a registered OnReconnect listener after collection delete!"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"TestOnReconnectListenerSupport succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

