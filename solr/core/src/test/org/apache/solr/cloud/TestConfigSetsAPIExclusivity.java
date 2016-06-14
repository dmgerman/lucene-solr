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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|request
operator|.
name|ConfigSetAdminRequest
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
name|ConfigSetAdminRequest
operator|.
name|Create
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
name|ConfigSetAdminRequest
operator|.
name|Delete
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
comment|/**  * Tests the exclusivity of the ConfigSets API.  * Submits a number of API requests concurrently and checks that  * the responses indicate the requests are handled sequentially for  * the same ConfigSet and base ConfigSet.  */
end_comment

begin_class
DECL|class|TestConfigSetsAPIExclusivity
specifier|public
class|class
name|TestConfigSetsAPIExclusivity
extends|extends
name|SolrTestCaseJ4
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
DECL|field|solrCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCluster
decl_stmt|;
DECL|field|GRANDBASE_CONFIGSET_NAME
specifier|private
specifier|static
specifier|final
name|String
name|GRANDBASE_CONFIGSET_NAME
init|=
literal|"grandBaseConfigSet1"
decl_stmt|;
DECL|field|BASE_CONFIGSET_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BASE_CONFIGSET_NAME
init|=
literal|"baseConfigSet1"
decl_stmt|;
DECL|field|CONFIGSET_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CONFIGSET_NAME
init|=
literal|"configSet1"
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
name|solrCluster
operator|=
operator|new
name|MiniSolrCloudCluster
argument_list|(
literal|1
argument_list|,
name|createTempDir
argument_list|()
argument_list|,
name|buildJettyConfig
argument_list|(
literal|"/solr"
argument_list|)
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
name|solrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAPIExclusivity
specifier|public
name|void
name|testAPIExclusivity
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|trials
init|=
literal|20
decl_stmt|;
name|setupBaseConfigSet
argument_list|(
name|GRANDBASE_CONFIGSET_NAME
argument_list|)
expr_stmt|;
name|CreateThread
name|createBaseThread
init|=
operator|new
name|CreateThread
argument_list|(
name|solrCluster
argument_list|,
name|BASE_CONFIGSET_NAME
argument_list|,
name|GRANDBASE_CONFIGSET_NAME
argument_list|,
name|trials
argument_list|)
decl_stmt|;
name|CreateThread
name|createThread
init|=
operator|new
name|CreateThread
argument_list|(
name|solrCluster
argument_list|,
name|CONFIGSET_NAME
argument_list|,
name|BASE_CONFIGSET_NAME
argument_list|,
name|trials
argument_list|)
decl_stmt|;
name|DeleteThread
name|deleteBaseThread
init|=
operator|new
name|DeleteThread
argument_list|(
name|solrCluster
argument_list|,
name|BASE_CONFIGSET_NAME
argument_list|,
name|trials
argument_list|)
decl_stmt|;
name|DeleteThread
name|deleteThread
init|=
operator|new
name|DeleteThread
argument_list|(
name|solrCluster
argument_list|,
name|CONFIGSET_NAME
argument_list|,
name|trials
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ConfigSetsAPIThread
argument_list|>
name|threads
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|createBaseThread
argument_list|,
name|createThread
argument_list|,
name|deleteBaseThread
argument_list|,
name|deleteThread
argument_list|)
decl_stmt|;
for|for
control|(
name|ConfigSetsAPIThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ConfigSetsAPIThread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|Exception
argument_list|>
name|exceptions
init|=
operator|new
name|LinkedList
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ConfigSetsAPIThread
name|thread
range|:
name|threads
control|)
block|{
name|exceptions
operator|.
name|addAll
argument_list|(
name|thread
operator|.
name|getUnexpectedExceptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Unexpected exception: "
operator|+
name|getFirstExceptionOrNull
argument_list|(
name|exceptions
argument_list|)
argument_list|,
literal|0
argument_list|,
name|exceptions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setupBaseConfigSet
specifier|private
name|void
name|setupBaseConfigSet
parameter_list|(
name|String
name|baseConfigSetName
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|File
name|configDir
init|=
name|getFile
argument_list|(
literal|"solr"
argument_list|)
operator|.
name|toPath
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets/configset-2/conf"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
specifier|final
name|File
name|tmpConfigDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|tmpConfigDir
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
name|configDir
argument_list|,
name|tmpConfigDir
argument_list|)
expr_stmt|;
name|solrCluster
operator|.
name|uploadConfigDir
argument_list|(
name|tmpConfigDir
argument_list|,
name|baseConfigSetName
argument_list|)
expr_stmt|;
block|}
DECL|method|getFirstExceptionOrNull
specifier|private
name|Exception
name|getFirstExceptionOrNull
parameter_list|(
name|List
argument_list|<
name|Exception
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|list
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|class|ConfigSetsAPIThread
specifier|private
specifier|static
specifier|abstract
class|class
name|ConfigSetsAPIThread
extends|extends
name|Thread
block|{
DECL|field|solrCluster
specifier|private
name|MiniSolrCloudCluster
name|solrCluster
decl_stmt|;
DECL|field|trials
specifier|private
name|int
name|trials
decl_stmt|;
DECL|field|unexpectedExceptions
specifier|private
name|List
argument_list|<
name|Exception
argument_list|>
name|unexpectedExceptions
init|=
operator|new
name|LinkedList
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|allowedExceptions
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|allowedExceptions
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"ConfigSet already exists"
block|,
literal|"ConfigSet does not exist to delete"
block|,
literal|"Base ConfigSet does not exist"
block|}
argument_list|)
decl_stmt|;
DECL|method|ConfigSetsAPIThread
specifier|public
name|ConfigSetsAPIThread
parameter_list|(
name|MiniSolrCloudCluster
name|solrCluster
parameter_list|,
name|int
name|trials
parameter_list|)
block|{
name|this
operator|.
name|solrCluster
operator|=
name|solrCluster
expr_stmt|;
name|this
operator|.
name|trials
operator|=
name|trials
expr_stmt|;
block|}
DECL|method|createRequest
specifier|public
specifier|abstract
name|ConfigSetAdminRequest
name|createRequest
parameter_list|()
function_decl|;
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|String
name|baseUrl
init|=
name|solrCluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|SolrClient
name|solrClient
init|=
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
decl_stmt|;
name|ConfigSetAdminRequest
name|request
init|=
name|createRequest
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
name|trials
condition|;
operator|++
name|i
control|)
block|{
try|try
block|{
name|request
operator|.
name|process
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|verifyException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|solrClient
operator|.
name|close
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
name|error
argument_list|(
literal|"Error closing client"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyException
specifier|private
name|void
name|verifyException
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
for|for
control|(
name|String
name|ex
range|:
name|allowedExceptions
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|ex
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
name|unexpectedExceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|getUnexpectedExceptions
specifier|public
name|List
argument_list|<
name|Exception
argument_list|>
name|getUnexpectedExceptions
parameter_list|()
block|{
return|return
name|unexpectedExceptions
return|;
block|}
block|}
DECL|class|CreateThread
specifier|private
specifier|static
class|class
name|CreateThread
extends|extends
name|ConfigSetsAPIThread
block|{
DECL|field|configSet
specifier|private
name|String
name|configSet
decl_stmt|;
DECL|field|baseConfigSet
specifier|private
name|String
name|baseConfigSet
decl_stmt|;
DECL|method|CreateThread
specifier|public
name|CreateThread
parameter_list|(
name|MiniSolrCloudCluster
name|solrCluster
parameter_list|,
name|String
name|configSet
parameter_list|,
name|String
name|baseConfigSet
parameter_list|,
name|int
name|trials
parameter_list|)
block|{
name|super
argument_list|(
name|solrCluster
argument_list|,
name|trials
argument_list|)
expr_stmt|;
name|this
operator|.
name|configSet
operator|=
name|configSet
expr_stmt|;
name|this
operator|.
name|baseConfigSet
operator|=
name|baseConfigSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createRequest
specifier|public
name|ConfigSetAdminRequest
name|createRequest
parameter_list|()
block|{
name|Create
name|create
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|create
operator|.
name|setBaseConfigSetName
argument_list|(
name|baseConfigSet
argument_list|)
operator|.
name|setConfigSetName
argument_list|(
name|configSet
argument_list|)
expr_stmt|;
return|return
name|create
return|;
block|}
block|}
DECL|class|DeleteThread
specifier|private
specifier|static
class|class
name|DeleteThread
extends|extends
name|ConfigSetsAPIThread
block|{
DECL|field|configSet
specifier|private
name|String
name|configSet
decl_stmt|;
DECL|method|DeleteThread
specifier|public
name|DeleteThread
parameter_list|(
name|MiniSolrCloudCluster
name|solrCluster
parameter_list|,
name|String
name|configSet
parameter_list|,
name|int
name|trials
parameter_list|)
block|{
name|super
argument_list|(
name|solrCluster
argument_list|,
name|trials
argument_list|)
expr_stmt|;
name|this
operator|.
name|configSet
operator|=
name|configSet
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createRequest
specifier|public
name|ConfigSetAdminRequest
name|createRequest
parameter_list|()
block|{
name|Delete
name|delete
init|=
operator|new
name|Delete
argument_list|()
decl_stmt|;
name|delete
operator|.
name|setConfigSetName
argument_list|(
name|configSet
argument_list|)
expr_stmt|;
return|return
name|delete
return|;
block|}
block|}
block|}
end_class

end_unit

