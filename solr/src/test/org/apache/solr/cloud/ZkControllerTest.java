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
name|Map
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
name|CloudState
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
name|SolrConfig
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
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
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
name|junit
operator|.
name|Assert
operator|.
name|*
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
DECL|field|TEST_NODE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_NODE_NAME
init|=
literal|"test_node_name"
decl_stmt|;
DECL|field|URL3
specifier|private
specifier|static
specifier|final
name|String
name|URL3
init|=
literal|"http://localhost:3133/solr/core1"
decl_stmt|;
DECL|field|URL2
specifier|private
specifier|static
specifier|final
name|String
name|URL2
init|=
literal|"http://localhost:3123/solr/core1"
decl_stmt|;
DECL|field|SHARD3
specifier|private
specifier|static
specifier|final
name|String
name|SHARD3
init|=
literal|"localhost:3123_solr_core3"
decl_stmt|;
DECL|field|SHARD2
specifier|private
specifier|static
specifier|final
name|String
name|SHARD2
init|=
literal|"localhost:3123_solr_core2"
decl_stmt|;
DECL|field|SHARD1
specifier|private
specifier|static
specifier|final
name|String
name|SHARD1
init|=
literal|"localhost:3123_solr_core1"
decl_stmt|;
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
literal|10000
decl_stmt|;
DECL|field|URL1
specifier|private
specifier|static
specifier|final
name|String
name|URL1
init|=
literal|"http://localhost:3133/solr/core0"
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
DECL|method|testReadShards
specifier|public
name|void
name|testReadShards
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
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
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
name|String
name|shardsPath
init|=
literal|"/collections/collection1/shards/shardid1"
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|shardsPath
argument_list|)
expr_stmt|;
name|addShardToZk
argument_list|(
name|zkClient
argument_list|,
name|shardsPath
argument_list|,
name|SHARD1
argument_list|,
name|URL1
argument_list|)
expr_stmt|;
name|addShardToZk
argument_list|(
name|zkClient
argument_list|,
name|shardsPath
argument_list|,
name|SHARD2
argument_list|,
name|URL2
argument_list|)
expr_stmt|;
name|addShardToZk
argument_list|(
name|zkClient
argument_list|,
name|shardsPath
argument_list|,
name|SHARD3
argument_list|,
name|URL3
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
name|zkController
operator|=
operator|new
name|ZkController
argument_list|(
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|1000
argument_list|,
literal|"localhost"
argument_list|,
literal|"8983"
argument_list|,
literal|"solr"
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|updateCloudState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|CloudState
name|cloudInfo
init|=
name|zkController
operator|.
name|getCloudState
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|cloudInfo
operator|.
name|getSlices
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|slices
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
operator|.
name|values
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|shards
init|=
name|slice
operator|.
name|getShards
argument_list|()
decl_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
for|for
control|(
name|String
name|shardName
range|:
name|shards
operator|.
name|keySet
argument_list|()
control|)
block|{
name|ZkNodeProps
name|props
init|=
name|shards
operator|.
name|get
argument_list|(
name|shardName
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"shard:"
operator|+
name|shardName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"props:"
operator|+
name|props
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotNull
argument_list|(
name|shards
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|shards
operator|.
name|get
argument_list|(
name|SHARD2
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|shards
operator|.
name|get
argument_list|(
name|SHARD3
argument_list|)
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|props
init|=
name|shards
operator|.
name|get
argument_list|(
name|SHARD1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|URL1
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|URL_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_NODE_NAME
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|=
name|shards
operator|.
name|get
argument_list|(
name|SHARD2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|URL2
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|URL_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_NODE_NAME
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|=
name|shards
operator|.
name|get
argument_list|(
name|SHARD3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|URL3
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|URL_PROP
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TEST_NODE_NAME
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
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
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
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
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|props
init|=
operator|new
name|ZkNodeProps
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
name|props
operator|.
name|store
argument_list|()
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
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
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
name|TIMEOUT
argument_list|,
literal|"localhost"
argument_list|,
literal|"8983"
argument_list|,
literal|"/solr"
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
name|server
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|TIMEOUT
argument_list|,
literal|1000
argument_list|,
literal|"localhost"
argument_list|,
literal|"8983"
argument_list|,
literal|"/solr"
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|uploadToZK
argument_list|(
operator|new
name|File
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
block|}
finally|finally
block|{
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
DECL|method|addShardToZk
specifier|private
name|void
name|addShardToZk
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|String
name|shardsPath
parameter_list|,
name|String
name|zkNodeName
parameter_list|,
name|String
name|url
parameter_list|)
throws|throws
name|IOException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ZkNodeProps
name|props
init|=
operator|new
name|ZkNodeProps
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|URL_PROP
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME
argument_list|,
name|TEST_NODE_NAME
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|props
operator|.
name|store
argument_list|()
decl_stmt|;
name|zkClient
operator|.
name|create
argument_list|(
name|shardsPath
operator|+
literal|"/"
operator|+
name|zkNodeName
argument_list|,
name|bytes
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrConfig
operator|.
name|severeErrors
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

