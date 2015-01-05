begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|text
operator|.
name|MessageFormat
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
name|Map
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
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|SolrClient
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
name|HttpSolrClient
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|cloud
operator|.
name|ZkController
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
name|solr
operator|.
name|util
operator|.
name|RESTfulServerProvider
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
name|RestTestHarness
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
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|core
operator|.
name|ConfigOverlay
operator|.
name|getObjectByPath
import|;
end_import

begin_class
DECL|class|TestConfigReload
specifier|public
class|class
name|TestConfigReload
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestConfigReload
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|restTestHarnesses
specifier|private
name|List
argument_list|<
name|RestTestHarness
argument_list|>
name|restTestHarnesses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|setupHarnesses
specifier|private
name|void
name|setupHarnesses
parameter_list|()
block|{
for|for
control|(
specifier|final
name|SolrClient
name|client
range|:
name|clients
control|)
block|{
name|RestTestHarness
name|harness
init|=
operator|new
name|RestTestHarness
argument_list|(
operator|new
name|RESTfulServerProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getBaseURL
parameter_list|()
block|{
return|return
operator|(
operator|(
name|HttpSolrClient
operator|)
name|client
operator|)
operator|.
name|getBaseURL
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|restTestHarnesses
operator|.
name|add
argument_list|(
name|harness
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
name|setupHarnesses
argument_list|()
expr_stmt|;
name|reloadTest
argument_list|()
expr_stmt|;
block|}
DECL|method|reloadTest
specifier|private
name|void
name|reloadTest
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrZkClient
name|client
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"live_nodes_count :  "
operator|+
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|confPath
init|=
name|ZkController
operator|.
name|CONFIGS_ZKNODE
operator|+
literal|"/conf1/"
decl_stmt|;
comment|//    checkConfReload(client, confPath + ConfigOverlay.RESOURCE_NAME, "overlay");
name|checkConfReload
argument_list|(
name|client
argument_list|,
name|confPath
operator|+
name|SolrConfig
operator|.
name|DEFAULT_CONF_FILE
argument_list|,
literal|"config"
argument_list|,
literal|"/config"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkConfReload
specifier|private
name|void
name|checkConfReload
parameter_list|(
name|SolrZkClient
name|client
parameter_list|,
name|String
name|resPath
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
try|try
block|{
name|data
operator|=
name|client
operator|.
name|getData
argument_list|(
name|resPath
argument_list|,
literal|null
argument_list|,
name|stat
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
name|data
operator|=
literal|"{}"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"creating_node {}"
argument_list|,
name|resPath
argument_list|)
expr_stmt|;
name|client
operator|.
name|create
argument_list|(
name|resPath
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|long
name|startTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|Stat
name|newStat
init|=
name|client
operator|.
name|setData
argument_list|(
name|resPath
argument_list|,
name|data
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|client
operator|.
name|setData
argument_list|(
literal|"/configs/conf1"
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|newStat
operator|.
name|getVersion
argument_list|()
operator|>
name|stat
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"new_version "
operator|+
name|newStat
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|Integer
name|newVersion
init|=
name|newStat
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|long
name|maxTimeoutSeconds
init|=
literal|20
decl_stmt|;
name|DocCollection
name|coll
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"collection1"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|coll
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
name|urls
operator|.
name|add
argument_list|(
literal|""
operator|+
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
operator|+
literal|"/"
operator|+
name|replica
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|HashSet
argument_list|<
name|String
argument_list|>
name|succeeded
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|<
name|maxTimeoutSeconds
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|url
range|:
name|urls
control|)
block|{
name|Map
name|respMap
init|=
name|getAsMap
argument_list|(
name|url
operator|+
name|uri
operator|+
literal|"?wt=json"
argument_list|)
decl_stmt|;
if|if
condition|(
name|String
operator|.
name|valueOf
argument_list|(
name|newVersion
argument_list|)
operator|.
name|equals
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|getObjectByPath
argument_list|(
name|respMap
argument_list|,
literal|true
argument_list|,
name|asList
argument_list|(
name|name
argument_list|,
literal|"znodeVersion"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
name|succeeded
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|succeeded
operator|.
name|size
argument_list|()
operator|==
name|urls
operator|.
name|size
argument_list|()
condition|)
break|break;
name|succeeded
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"tried these servers {0} succeeded only in {1} "
argument_list|,
name|urls
argument_list|,
name|succeeded
argument_list|)
argument_list|,
name|urls
operator|.
name|size
argument_list|()
argument_list|,
name|succeeded
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getAsMap
specifier|private
name|Map
name|getAsMap
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpGet
name|get
init|=
operator|new
name|HttpGet
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entity
operator|=
name|cloudClient
operator|.
name|getLbClient
argument_list|()
operator|.
name|getHttpClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
name|String
name|response
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
return|return
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

