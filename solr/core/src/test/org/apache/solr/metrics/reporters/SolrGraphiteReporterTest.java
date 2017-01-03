begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics.reporters
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|reporters
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|core
operator|.
name|SolrResourceLoader
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
name|SolrXmlConfig
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
name|metrics
operator|.
name|SolrMetricManager
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
name|metrics
operator|.
name|SolrMetricReporter
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
name|util
operator|.
name|TestHarness
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SolrGraphiteReporterTest
specifier|public
class|class
name|SolrGraphiteReporterTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testReporter
specifier|public
name|void
name|testReporter
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|home
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
decl_stmt|;
comment|// define these properties, they are used in solrconfig.xml
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop1"
argument_list|,
literal|"propone"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.sys.prop2"
argument_list|,
literal|"proptwo"
argument_list|)
expr_stmt|;
name|MockGraphite
name|mock
init|=
operator|new
name|MockGraphite
argument_list|()
decl_stmt|;
try|try
block|{
name|mock
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// define the port where MockGraphite is running
name|System
operator|.
name|setProperty
argument_list|(
literal|"mock-graphite-port"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|mock
operator|.
name|port
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|solrXml
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|home
operator|.
name|toString
argument_list|()
argument_list|,
literal|"solr-graphitereporter.xml"
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|NodeConfig
name|cfg
init|=
name|SolrXmlConfig
operator|.
name|fromString
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
name|home
argument_list|)
argument_list|,
name|solrXml
argument_list|)
decl_stmt|;
name|CoreContainer
name|cc
init|=
name|createCoreContainer
argument_list|(
name|cfg
argument_list|,
operator|new
name|TestHarness
operator|.
name|TestCoresLocator
argument_list|(
name|DEFAULT_TEST_CORENAME
argument_list|,
name|initCoreDataDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|h
operator|.
name|coreName
operator|=
name|DEFAULT_TEST_CORENAME
expr_stmt|;
name|SolrMetricManager
name|metricManager
init|=
name|cc
operator|.
name|getMetricManager
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SolrMetricReporter
argument_list|>
name|reporters
init|=
name|metricManager
operator|.
name|getReporters
argument_list|(
literal|"solr.node"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reporters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SolrMetricReporter
name|reporter
init|=
name|reporters
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|reporter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|reporter
operator|instanceof
name|SolrGraphiteReporter
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mock
operator|.
name|lines
operator|.
name|size
argument_list|()
operator|>=
literal|3
argument_list|)
expr_stmt|;
name|String
index|[]
name|frozenLines
init|=
operator|(
name|String
index|[]
operator|)
name|mock
operator|.
name|lines
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|mock
operator|.
name|lines
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|frozenLines
control|)
block|{
name|assertTrue
argument_list|(
name|line
argument_list|,
name|line
operator|.
name|startsWith
argument_list|(
literal|"test.solr.node.cores."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|mock
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|MockGraphite
specifier|private
class|class
name|MockGraphite
extends|extends
name|Thread
block|{
DECL|field|lines
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|server
specifier|private
name|ServerSocket
name|server
init|=
literal|null
decl_stmt|;
DECL|field|port
specifier|private
name|int
name|port
decl_stmt|;
DECL|field|stop
specifier|private
name|boolean
name|stop
decl_stmt|;
DECL|method|MockGraphite
name|MockGraphite
parameter_list|()
throws|throws
name|Exception
block|{
name|server
operator|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|port
operator|=
name|server
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|stop
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
condition|)
block|{
try|try
block|{
name|Socket
name|s
init|=
name|server
operator|.
name|accept
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|s
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|lines
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|stop
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|stop
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

