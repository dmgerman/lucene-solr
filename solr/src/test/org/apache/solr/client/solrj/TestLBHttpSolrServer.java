begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|commons
operator|.
name|httpclient
operator|.
name|HttpClient
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
name|CommonsHttpSolrServer
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
name|LBHttpSolrServer
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
name|response
operator|.
name|QueryResponse
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
name|response
operator|.
name|UpdateResponse
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
name|SolrInputDocument
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
name|AbstractSolrTestCase
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Test for LBHttpSolrServer  *  * @version $Id$  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TestLBHttpSolrServer
specifier|public
class|class
name|TestLBHttpSolrServer
extends|extends
name|TestCase
block|{
DECL|field|solr
name|SolrInstance
index|[]
name|solr
init|=
operator|new
name|SolrInstance
index|[
literal|3
index|]
decl_stmt|;
DECL|field|httpClient
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|solr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|solr
index|[
name|i
index|]
operator|=
operator|new
name|SolrInstance
argument_list|(
literal|"solr"
operator|+
name|i
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|solr
index|[
name|i
index|]
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|solr
index|[
name|i
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
name|addDocs
argument_list|(
name|solr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addDocs
specifier|private
name|void
name|addDocs
parameter_list|(
name|SolrInstance
name|solrInstance
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"name"
argument_list|,
name|solrInstance
operator|.
name|name
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|CommonsHttpSolrServer
name|solrServer
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|solrInstance
operator|.
name|getUrl
argument_list|()
argument_list|,
name|httpClient
argument_list|)
decl_stmt|;
name|UpdateResponse
name|resp
init|=
name|solrServer
operator|.
name|add
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|resp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|=
name|solrServer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|resp
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
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
for|for
control|(
name|SolrInstance
name|aSolr
range|:
name|solr
control|)
block|{
name|aSolr
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|solr
operator|.
name|length
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
name|solr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|s
index|[
name|i
index|]
operator|=
name|solr
index|[
name|i
index|]
operator|.
name|getUrl
argument_list|()
expr_stmt|;
block|}
name|LBHttpSolrServer
name|lbHttpSolrServer
init|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|httpClient
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|lbHttpSolrServer
operator|.
name|setAliveCheckInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|QueryResponse
name|resp
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|s
control|)
block|{
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Kill a server and test again
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|s
control|)
block|{
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|names
operator|.
name|contains
argument_list|(
literal|"solr1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Start the killed server once again
name|solr
index|[
literal|1
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
comment|// Wait for the alive check to complete
name|Thread
operator|.
name|sleep
argument_list|(
literal|1200
argument_list|)
expr_stmt|;
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|s
control|)
block|{
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoServers
specifier|public
name|void
name|testTwoServers
parameter_list|()
throws|throws
name|Exception
block|{
name|LBHttpSolrServer
name|lbHttpSolrServer
init|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|httpClient
argument_list|,
name|solr
index|[
literal|0
index|]
operator|.
name|getUrl
argument_list|()
argument_list|,
name|solr
index|[
literal|1
index|]
operator|.
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
name|lbHttpSolrServer
operator|.
name|setAliveCheckInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|QueryResponse
name|resp
init|=
literal|null
decl_stmt|;
name|solr
index|[
literal|0
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|0
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr1"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
name|name
operator|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr1"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|solr
index|[
literal|1
index|]
operator|.
name|jetty
operator|=
literal|null
expr_stmt|;
name|solr
index|[
literal|0
index|]
operator|.
name|startJetty
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1200
argument_list|)
expr_stmt|;
try|try
block|{
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
comment|// try again after a pause in case the error is lack of time to start server
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|resp
operator|=
name|lbHttpSolrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|resp
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"name"
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"solr0"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|class|SolrInstance
specifier|private
class|class
name|SolrInstance
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|homeDir
name|File
name|homeDir
decl_stmt|;
DECL|field|dataDir
name|File
name|dataDir
decl_stmt|;
DECL|field|confDir
name|File
name|confDir
decl_stmt|;
DECL|field|port
name|int
name|port
decl_stmt|;
DECL|field|jetty
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|method|SolrInstance
specifier|public
name|SolrInstance
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
block|}
DECL|method|getHomeDir
specifier|public
name|String
name|getHomeDir
parameter_list|()
block|{
return|return
name|homeDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getUrl
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/solr"
return|;
block|}
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"."
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"schema-replication1.xml"
return|;
block|}
DECL|method|getConfDir
specifier|public
name|String
name|getConfDir
parameter_list|()
block|{
return|return
name|confDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getDataDir
specifier|public
name|String
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
name|String
name|fname
init|=
literal|""
decl_stmt|;
name|fname
operator|=
literal|"."
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"conf"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrconfig-slave1.xml"
expr_stmt|;
return|return
name|fname
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|home
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
operator|+
name|File
operator|.
name|separator
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|homeDir
operator|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|dataDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"data"
argument_list|)
expr_stmt|;
name|confDir
operator|=
operator|new
name|File
argument_list|(
name|homeDir
argument_list|,
literal|"conf"
argument_list|)
expr_stmt|;
name|homeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|dataDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|getSolrConfigFile
argument_list|()
argument_list|)
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|getSchemaFile
argument_list|()
argument_list|)
argument_list|,
name|f
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
try|try
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{       }
name|AbstractSolrTestCase
operator|.
name|recurseDelete
argument_list|(
name|homeDir
argument_list|)
expr_stmt|;
block|}
DECL|method|startJetty
specifier|public
name|void
name|startJetty
parameter_list|()
throws|throws
name|Exception
block|{
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
literal|"/solr"
argument_list|,
name|port
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|getHomeDir
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|getDataDir
argument_list|()
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|newPort
init|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
decl_stmt|;
if|if
condition|(
name|port
operator|!=
literal|0
operator|&&
name|newPort
operator|!=
name|port
condition|)
block|{
name|TestCase
operator|.
name|fail
argument_list|(
literal|"TESTING FAILURE: could not grab requested port."
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|port
operator|=
name|newPort
expr_stmt|;
comment|//      System.out.println("waiting.........");
comment|//      Thread.sleep(5000);
block|}
block|}
block|}
end_class

end_unit

