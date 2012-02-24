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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|SolrZkClient
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
name|core
operator|.
name|CoreDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
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
DECL|class|ZkControllerTest
specifier|public
class|class
name|ZkControllerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|COLLECTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_NAME
init|=
literal|"collection1"
decl_stmt|;
DECL|field|TIMEOUT
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|1000
decl_stmt|;
DECL|field|DEBUG
specifier|private
specifier|static
specifier|final
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadConfigName
specifier|public
name|void
name|testReadConfigName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
decl_stmt|;
name|String
name|actualConfigName
init|=
literal|"firstConfig"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|actualConfigName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
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
name|props
operator|.
name|put
argument_list|(
literal|"configName"
argument_list|,
name|actualConfigName
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|zkProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|COLLECTION_NAME
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|zkProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|ZkController
name|zkController
init|=
operator|new
name|ZkController
argument_list|(
literal|null
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|10000
argument_list|,
literal|"localhost"
argument_list|,
literal|"8983"
argument_list|,
literal|"solr"
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|configName
init|=
name|zkController
operator|.
name|readConfigName
argument_list|(
name|COLLECTION_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|configName
argument_list|,
name|actualConfigName
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUploadToCloud
specifier|public
name|void
name|testUploadToCloud
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
name|boolean
name|testFinished
init|=
literal|false
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
literal|null
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|10000
argument_list|,
literal|"localhost"
argument_list|,
literal|"8983"
argument_list|,
literal|"solr"
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|uploadToZK
argument_list|(
name|getFile
argument_list|(
literal|"solr/conf"
argument_list|)
argument_list|,
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/config1"
argument_list|)
expr_stmt|;
comment|// uploading again should overwrite, not error...
name|zkController
operator|.
name|uploadToZK
argument_list|(
name|getFile
argument_list|(
literal|"solr/conf"
argument_list|)
argument_list|,
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/config1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|zkController
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
name|testFinished
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|testFinished
condition|)
block|{
name|zkController
operator|.
name|getZkClient
argument_list|()
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCoreUnload
specifier|public
name|void
name|testCoreUnload
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|zkDir
init|=
name|dataDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper/server1/data"
decl_stmt|;
name|ZkTestServer
name|server
init|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
decl_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|.
name|run
argument_list|()
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|tryCleanSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|makeSolrZkNode
argument_list|(
name|server
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|ZkStateReader
operator|.
name|LIVE_NODES_ZKNODE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ZkStateReader
name|reader
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|reader
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
literal|null
argument_list|,
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|10000
argument_list|,
literal|"localhost"
argument_list|,
literal|"8983"
argument_list|,
literal|"solr"
argument_list|,
operator|new
name|CurrentCoreDescriptorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|getCurrentDescriptors
parameter_list|()
block|{
comment|// do nothing
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_confdir"
argument_list|,
name|getFile
argument_list|(
literal|"solr/conf"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numShards
init|=
literal|2
decl_stmt|;
specifier|final
name|String
index|[]
name|ids
init|=
operator|new
name|String
index|[
name|numShards
index|]
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
name|numShards
condition|;
name|i
operator|++
control|)
block|{
name|CloudDescriptor
name|collection1Desc
init|=
operator|new
name|CloudDescriptor
argument_list|()
decl_stmt|;
name|collection1Desc
operator|.
name|setCollectionName
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|CoreDescriptor
name|desc1
init|=
operator|new
name|CoreDescriptor
argument_list|(
literal|null
argument_list|,
literal|"core"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|desc1
operator|.
name|setCloudDescriptor
argument_list|(
name|collection1Desc
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|preRegisterSetup
argument_list|(
literal|null
argument_list|,
name|desc1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ids
index|[
name|i
index|]
operator|=
name|zkController
operator|.
name|register
argument_list|(
literal|"core"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|desc1
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"shard1"
argument_list|,
name|ids
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shard1"
argument_list|,
name|ids
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|reader
operator|.
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Shard(s) missing from cloudstate"
argument_list|,
literal|2
argument_list|,
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getCloudState
argument_list|()
operator|.
name|getSlice
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|)
operator|.
name|getShards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// unregister current leader
specifier|final
name|ZkNodeProps
name|shard1LeaderProps
init|=
name|reader
operator|.
name|getLeaderProps
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|leaderUrl
init|=
name|reader
operator|.
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
decl_stmt|;
specifier|final
name|CloudDescriptor
name|collection1Desc
init|=
operator|new
name|CloudDescriptor
argument_list|()
decl_stmt|;
name|collection1Desc
operator|.
name|setCollectionName
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
specifier|final
name|CoreDescriptor
name|desc1
init|=
operator|new
name|CoreDescriptor
argument_list|(
literal|null
argument_list|,
name|shard1LeaderProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|desc1
operator|.
name|setCloudDescriptor
argument_list|(
name|collection1Desc
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|unregister
argument_list|(
name|shard1LeaderProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|,
name|collection1Desc
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
literal|"New leader was not promoted after unregistering the current leader."
argument_list|,
name|leaderUrl
argument_list|,
name|reader
operator|.
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"New leader was null."
argument_list|,
name|reader
operator|.
name|getLeaderUrl
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shard was not unregistered"
argument_list|,
literal|1
argument_list|,
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getCloudState
argument_list|()
operator|.
name|getSlice
argument_list|(
literal|"collection1"
argument_list|,
literal|"shard1"
argument_list|)
operator|.
name|getShards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|DEBUG
condition|)
block|{
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
name|zkController
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
name|ZkStateReader
operator|.
name|NUM_SHARDS_PROP
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// wait just a bit for any zk client threads to outlast timeout
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

