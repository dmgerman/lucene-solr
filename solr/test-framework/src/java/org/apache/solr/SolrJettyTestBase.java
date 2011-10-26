begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SolrServer
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
name|EmbeddedSolrServer
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
name|util
operator|.
name|ExternalPaths
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

begin_class
DECL|class|SolrJettyTestBase
specifier|abstract
specifier|public
class|class
name|SolrJettyTestBase
extends|extends
name|SolrTestCaseJ4
block|{
comment|// Try not introduce a dependency on the example schema or config unless you need to.
comment|// using configs in the test directory allows more flexibility to change "example"
comment|// without breaking configs.
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
return|;
block|}
DECL|field|jetty
specifier|public
specifier|static
name|JettySolrRunner
name|jetty
decl_stmt|;
DECL|field|port
specifier|public
specifier|static
name|int
name|port
decl_stmt|;
DECL|field|server
specifier|public
specifier|static
name|SolrServer
name|server
decl_stmt|;
DECL|field|context
specifier|public
specifier|static
name|String
name|context
decl_stmt|;
DECL|method|createJetty
specifier|public
specifier|static
name|JettySolrRunner
name|createJetty
parameter_list|(
name|String
name|solrHome
parameter_list|,
name|String
name|configFile
parameter_list|,
name|String
name|context
parameter_list|)
throws|throws
name|Exception
block|{
comment|// creates the data dir
name|initCore
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"maxWarmingSearchers"
argument_list|)
expr_stmt|;
comment|// this sets the property for jetty starting SolrDispatchFilter
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.solr.home"
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.dir"
argument_list|,
name|dataDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
comment|// disable VelocityResponseWriter from example configuration
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.velocity.enabled"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|context
operator|=
name|context
operator|==
literal|null
condition|?
literal|"/solr"
else|:
name|context
expr_stmt|;
name|SolrJettyTestBase
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|jetty
operator|=
operator|new
name|JettySolrRunner
argument_list|(
name|context
argument_list|,
literal|0
argument_list|,
name|configFile
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|start
argument_list|()
expr_stmt|;
name|port
operator|=
name|jetty
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Jetty Assigned Port#"
operator|+
name|port
argument_list|)
expr_stmt|;
return|return
name|jetty
return|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSolrJettyTestBase
specifier|public
specifier|static
name|void
name|afterSolrJettyTestBase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
block|}
name|server
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getSolrServer
specifier|public
name|SolrServer
name|getSolrServer
parameter_list|()
block|{
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
name|createNewSolrServer
argument_list|()
expr_stmt|;
block|}
return|return
name|server
return|;
block|}
block|}
comment|/**    * Create a new solr server.    * If createJetty was called, an http implementation will be created,    * otherwise an embedded implementation will be created.    * Subclasses should override for other options.    */
DECL|method|createNewSolrServer
specifier|public
name|SolrServer
name|createNewSolrServer
parameter_list|()
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// setup the server...
name|String
name|url
init|=
literal|"http://localhost:"
operator|+
name|port
operator|+
name|context
decl_stmt|;
name|CommonsHttpSolrServer
name|s
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|s
operator|.
name|setConnectionTimeout
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// 1/10th sec
name|s
operator|.
name|setDefaultMaxConnectionsPerHost
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|s
operator|.
name|setMaxTotalConnections
argument_list|(
literal|100
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|EmbeddedSolrServer
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

