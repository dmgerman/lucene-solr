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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|SolrException
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
name|NodeConfig
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
name|servlet
operator|.
name|SolrDispatchFilter
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
DECL|class|SolrXmlInZkTest
specifier|public
class|class
name|SolrXmlInZkTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
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
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|reader
specifier|private
name|ZkStateReader
name|reader
decl_stmt|;
DECL|field|cfg
specifier|private
name|NodeConfig
name|cfg
decl_stmt|;
DECL|field|solrDispatchFilter
specifier|private
name|SolrDispatchFilter
name|solrDispatchFilter
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|,
literal|"zookeeper"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|public
name|void
name|after
parameter_list|()
block|{
if|if
condition|(
name|solrDispatchFilter
operator|!=
literal|null
condition|)
block|{
name|solrDispatchFilter
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setUpZkAndDiskXml
specifier|private
name|void
name|setUpZkAndDiskXml
parameter_list|(
name|boolean
name|toZk
parameter_list|,
name|boolean
name|leaveOnLocal
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|tmpDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|File
name|solrHome
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"home"
argument_list|)
decl_stmt|;
name|copyMinConf
argument_list|(
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"myCollect"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|leaveOnLocal
condition|)
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
argument_list|,
literal|"solr-stress-new.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
literal|"solr.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ignoreException
argument_list|(
literal|"No UpdateLog found - cannot sync"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"No UpdateLog found - cannot recover"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|,
literal|"8000"
argument_list|)
expr_stmt|;
name|zkDir
operator|=
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"zookeeper"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"/server1/data"
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|AbstractZkTestCase
operator|.
name|buildZooKeeper
argument_list|(
name|zkServer
operator|.
name|getZkHost
argument_list|()
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
if|if
condition|(
name|toZk
condition|)
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"solr.xml"
argument_list|,
name|XML_FOR_ZK
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_START "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
comment|// set some system properties for use by tests
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|Method
name|method
init|=
name|SolrDispatchFilter
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"loadNodeConfig"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Properties
operator|.
name|class
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrDispatchFilter
operator|!=
literal|null
condition|)
name|solrDispatchFilter
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|solrDispatchFilter
operator|=
operator|new
name|SolrDispatchFilter
argument_list|()
expr_stmt|;
name|Object
name|obj
init|=
name|method
operator|.
name|invoke
argument_list|(
name|solrDispatchFilter
argument_list|,
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|cfg
operator|=
operator|(
name|NodeConfig
operator|)
name|obj
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"####SETUP_END "
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|closeZK
specifier|private
name|void
name|closeZK
parameter_list|()
throws|throws
name|Exception
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
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testXmlOnBoth
specifier|public
name|void
name|testXmlOnBoth
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setUpZkAndDiskXml
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have gotten a new port the xml file sent to ZK, overrides the copy on disk"
argument_list|,
name|cfg
operator|.
name|getCloudConfig
argument_list|()
operator|.
name|getSolrHostPort
argument_list|()
argument_list|,
literal|9045
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeZK
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testXmlInZkOnly
specifier|public
name|void
name|testXmlInZkOnly
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setUpZkAndDiskXml
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have gotten a new port the xml file sent to ZK"
argument_list|,
name|cfg
operator|.
name|getCloudConfig
argument_list|()
operator|.
name|getSolrHostPort
argument_list|()
argument_list|,
literal|9045
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeZK
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNotInZkAndShouldBe
specifier|public
name|void
name|testNotInZkAndShouldBe
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|setUpZkAndDiskXml
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have gotten an exception here!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Should have an exception here, file not in ZK."
argument_list|,
literal|"Could not load solr.xml from zookeeper"
argument_list|,
name|ite
operator|.
name|getTargetException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeZK
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNotInZkOrOnDisk
specifier|public
name|void
name|testNotInZkOrOnDisk
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"hostPort"
argument_list|,
literal|"8787"
argument_list|)
expr_stmt|;
name|setUpZkAndDiskXml
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// solr.xml not on disk either
name|fail
argument_list|(
literal|"Should have thrown an exception here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should be failing to create default solr.xml in code"
argument_list|,
name|ite
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"solr.xml does not exist"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeZK
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOnDiskOnly
specifier|public
name|void
name|testOnDiskOnly
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|)
expr_stmt|;
name|setUpZkAndDiskXml
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have gotten the default port"
argument_list|,
name|cfg
operator|.
name|getCloudConfig
argument_list|()
operator|.
name|getSolrHostPort
argument_list|()
argument_list|,
literal|8983
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeZK
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadSysProp
specifier|public
name|void
name|testBadSysProp
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solrxml.location"
argument_list|,
literal|"solrHomeDir"
argument_list|)
expr_stmt|;
name|setUpZkAndDiskXml
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception in SolrXmlInZkTest.testBadSysProp"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Should have an exception in SolrXmlInZkTest.testBadSysProp, sysprop set to bogus value."
argument_list|,
name|ite
operator|.
name|getTargetException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Bad solr.solrxml.location set: solrHomeDir - should be 'solrhome' or 'zookeeper'"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeZK
argument_list|()
expr_stmt|;
block|}
block|}
comment|//SolrDispatchFilter.protected static ConfigSolr loadConfigSolr(SolrResourceLoader loader) {
annotation|@
name|Test
DECL|method|testZkHostDiscovery
specifier|public
name|void
name|testZkHostDiscovery
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|NoSuchMethodException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|InvocationTargetException
block|{
comment|// Should see an error when zkHost is not defined but solr.solrxml.location is set to zookeeper.
name|System
operator|.
name|clearProperty
argument_list|(
literal|"zkHost"
argument_list|)
expr_stmt|;
try|try
block|{
name|Method
name|method
init|=
name|SolrDispatchFilter
operator|.
name|class
operator|.
name|getDeclaredMethod
argument_list|(
literal|"loadNodeConfig"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|Properties
operator|.
name|class
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|solrDispatchFilter
operator|!=
literal|null
condition|)
name|solrDispatchFilter
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|solrDispatchFilter
operator|=
operator|new
name|SolrDispatchFilter
argument_list|()
expr_stmt|;
name|method
operator|.
name|invoke
argument_list|(
name|solrDispatchFilter
argument_list|,
literal|""
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Should be catching a SolrException"
argument_list|,
name|ite
operator|.
name|getTargetException
argument_list|()
operator|instanceof
name|SolrException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Caught Solr exception"
argument_list|,
name|ite
operator|.
name|getTargetException
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Could not load solr.xml from zookeeper: zkHost system property not set"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Just a random port, I'm not going to use it but just check that the Solr instance constructed from the XML
comment|// file in ZK overrides the default port.
DECL|field|XML_FOR_ZK
specifier|private
specifier|final
name|String
name|XML_FOR_ZK
init|=
literal|"<solr>"
operator|+
literal|"<solrcloud>"
operator|+
literal|"<str name=\"host\">127.0.0.1</str>"
operator|+
literal|"<int name=\"hostPort\">9045</int>"
operator|+
literal|"<str name=\"hostContext\">${hostContext:solr}</str>"
operator|+
literal|"</solrcloud>"
operator|+
literal|"<shardHandlerFactory name=\"shardHandlerFactory\" class=\"HttpShardHandlerFactory\">"
operator|+
literal|"<int name=\"socketTimeout\">${socketTimeout:120000}</int>"
operator|+
literal|"<int name=\"connTimeout\">${connTimeout:15000}</int>"
operator|+
literal|"</shardHandlerFactory>"
operator|+
literal|"</solr>"
decl_stmt|;
block|}
end_class

end_unit

