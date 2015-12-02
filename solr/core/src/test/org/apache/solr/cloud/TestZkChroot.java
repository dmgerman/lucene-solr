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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|SolrJettyTestBase
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
name|ZkConfigManager
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
name|ZooKeeperException
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestZkChroot
specifier|public
class|class
name|TestZkChroot
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|cores
specifier|protected
name|CoreContainer
name|cores
init|=
literal|null
decl_stmt|;
DECL|field|home
specifier|private
name|Path
name|home
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkDir
specifier|protected
name|String
name|zkDir
decl_stmt|;
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
name|zkDir
operator|=
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|zkDir
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|home
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|SolrJettyTestBase
operator|.
name|legacyExampleCollection1SolrHome
argument_list|()
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
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
block|{
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cores
operator|=
literal|null
expr_stmt|;
block|}
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|zkServer
operator|=
literal|null
expr_stmt|;
name|zkDir
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChrootBootstrap
specifier|public
name|void
name|testChrootBootstrap
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|chroot
init|=
literal|"/foo/bar"
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_conf"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkHost
argument_list|()
operator|+
name|chroot
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
name|SolrZkClient
name|zkClient2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cores
operator|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|zkClient
operator|=
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
operator|+
literal|"/clusterstate.json"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|zkClient2
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient2
operator|.
name|exists
argument_list|(
name|chroot
operator|+
literal|"/clusterstate.json"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|zkClient2
operator|.
name|exists
argument_list|(
literal|"/clusterstate.json"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkClient2
operator|!=
literal|null
condition|)
name|zkClient2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNoBootstrapConf
specifier|public
name|void
name|testNoBootstrapConf
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|chroot
init|=
literal|"/foo/bar2"
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_conf"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkHost
argument_list|()
operator|+
name|chroot
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Path '"
operator|+
name|chroot
operator|+
literal|"' should not exist before the test"
argument_list|,
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cores
operator|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"There should be a zk exception, as the initial path doesn't exist"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ZooKeeperException
name|e
parameter_list|)
block|{
comment|// expected
name|assertFalse
argument_list|(
literal|"Path shouldn't have been created"
argument_list|,
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// check the path was not created
block|}
finally|finally
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testWithUploadDir
specifier|public
name|void
name|testWithUploadDir
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|chroot
init|=
literal|"/foo/bar3"
decl_stmt|;
name|String
name|configName
init|=
literal|"testWithUploadDir"
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_conf"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_confdir"
argument_list|,
name|home
operator|+
literal|"/collection1/conf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"collection.configName"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkHost
argument_list|()
operator|+
name|chroot
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Path '"
operator|+
name|chroot
operator|+
literal|"' should not exist before the test"
argument_list|,
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cores
operator|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"solrconfig.xml should have been uploaded to zk to the correct config directory"
argument_list|,
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
operator|+
name|ZkConfigManager
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/"
operator|+
name|configName
operator|+
literal|"/solrconfig.xml"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testInitPathExists
specifier|public
name|void
name|testInitPathExists
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|chroot
init|=
literal|"/foo/bar4"
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"bootstrap_conf"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkHost
argument_list|()
operator|+
name|chroot
argument_list|)
expr_stmt|;
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/foo/bar4"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
operator|+
literal|"/clusterstate.json"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|cores
operator|=
name|CoreContainer
operator|.
name|createAndLoad
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|exists
argument_list|(
name|chroot
operator|+
literal|"/clusterstate.json"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cores
operator|!=
literal|null
condition|)
name|cores
operator|.
name|shutdown
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

