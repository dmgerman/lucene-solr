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
name|FilenameFilter
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Random
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
name|commons
operator|.
name|cli
operator|.
name|CommandLine
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
name|SolrQuery
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
name|ContentStreamUpdateRequest
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
name|util
operator|.
name|ExternalPaths
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
name|SolrCLI
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
comment|/**  * Emulates bin/solr -e cloud -noprompt; bin/post -c gettingstarted example/exampledocs/*.xml;  * this test is useful for catching regressions in indexing the example docs in collections that  * use data-driven schema and managed schema features provided by configsets/data_driven_schema_configs.  */
end_comment

begin_class
DECL|class|SolrCloudExampleTest
specifier|public
class|class
name|SolrCloudExampleTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
specifier|transient
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrCloudExampleTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|SolrCloudExampleTest
specifier|public
name|SolrCloudExampleTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadDocsIntoGettingStartedCollection
specifier|public
name|void
name|testLoadDocsIntoGettingStartedCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForThingsToLevelOut
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"testLoadDocsIntoGettingStartedCollection initialized OK ... running test logic"
argument_list|)
expr_stmt|;
name|String
name|testCollectionName
init|=
literal|"gettingstarted"
decl_stmt|;
name|File
name|data_driven_schema_configs
init|=
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SCHEMALESS_CONFIGSET
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|data_driven_schema_configs
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" not found!"
argument_list|,
name|data_driven_schema_configs
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
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
decl_stmt|;
if|if
condition|(
name|liveNodes
operator|.
name|isEmpty
argument_list|()
condition|)
name|fail
argument_list|(
literal|"No live nodes found! Cannot create a collection until there is at least 1 live node in the cluster."
argument_list|)
expr_stmt|;
name|String
name|firstLiveNode
init|=
name|liveNodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|solrUrl
init|=
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|firstLiveNode
argument_list|)
decl_stmt|;
comment|// create the gettingstarted collection just like the bin/solr script would do
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"create_collection"
block|,
literal|"-name"
block|,
name|testCollectionName
block|,
literal|"-shards"
block|,
literal|"2"
block|,
literal|"-replicationFactor"
block|,
literal|"2"
block|,
literal|"-confname"
block|,
name|testCollectionName
block|,
literal|"-confdir"
block|,
literal|"data_driven_schema_configs"
block|,
literal|"-configsetsDir"
block|,
name|data_driven_schema_configs
operator|.
name|getParentFile
argument_list|()
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
block|,
literal|"-solrUrl"
block|,
name|solrUrl
block|}
decl_stmt|;
name|SolrCLI
operator|.
name|CreateCollectionTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|CreateCollectionTool
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
init|=
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating the '"
operator|+
name|testCollectionName
operator|+
literal|"' collection using SolrCLI with: "
operator|+
name|solrUrl
argument_list|)
expr_stmt|;
name|tool
operator|.
name|runTool
argument_list|(
name|cli
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Collection '"
operator|+
name|testCollectionName
operator|+
literal|"' doesn't exist after trying to create it!"
argument_list|,
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|hasCollection
argument_list|(
name|testCollectionName
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the collection is usable ...
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard1"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|ensureAllReplicasAreActive
argument_list|(
name|testCollectionName
argument_list|,
literal|"shard2"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|setDefaultCollection
argument_list|(
name|testCollectionName
argument_list|)
expr_stmt|;
comment|// now index docs like bin/post would do but we can't use SimplePostTool because it uses System.exit when
comment|// it encounters an error, which JUnit doesn't like ...
name|log
operator|.
name|info
argument_list|(
literal|"Created collection, now posting example docs!"
argument_list|)
expr_stmt|;
name|File
name|exampleDocsDir
init|=
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|SOURCE_HOME
argument_list|,
literal|"example/exampledocs"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exampleDocsDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" not found!"
argument_list|,
name|exampleDocsDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|File
argument_list|>
name|xmlFiles
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|exampleDocsDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|// force a deterministic random ordering of the files so seeds reproduce regardless of platform/filesystem
name|Collections
operator|.
name|sort
argument_list|(
name|xmlFiles
argument_list|,
operator|new
name|Comparator
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|File
name|o1
parameter_list|,
name|File
name|o2
parameter_list|)
block|{
comment|// don't rely on File.compareTo, it's behavior varies by OS
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|xmlFiles
argument_list|,
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// if you add/remove example XML docs, you'll have to fix these expected values
name|int
name|expectedXmlFileCount
init|=
literal|14
decl_stmt|;
name|int
name|expectedXmlDocCount
init|=
literal|32
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected # of example XML files in "
operator|+
name|exampleDocsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|expectedXmlFileCount
argument_list|,
name|xmlFiles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|File
name|xml
range|:
name|xmlFiles
control|)
block|{
name|ContentStreamUpdateRequest
name|req
init|=
operator|new
name|ContentStreamUpdateRequest
argument_list|(
literal|"/update"
argument_list|)
decl_stmt|;
name|req
operator|.
name|addFile
argument_list|(
name|xml
argument_list|,
literal|"application/xml"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"POSTing "
operator|+
name|xml
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
name|cloudClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|QueryResponse
name|qr
init|=
name|cloudClient
operator|.
name|query
argument_list|(
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numFound
init|=
operator|(
name|int
operator|)
name|qr
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"*:* found unexpected number of documents"
argument_list|,
name|expectedXmlDocCount
argument_list|,
name|numFound
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Updating Config for "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
name|doTestConfigUpdate
argument_list|(
name|testCollectionName
argument_list|,
name|solrUrl
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Running healthcheck for "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
name|doTestHealthcheck
argument_list|(
name|testCollectionName
argument_list|,
name|cloudClient
operator|.
name|getZkHost
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify the delete action works too
name|log
operator|.
name|info
argument_list|(
literal|"Running delete for "
operator|+
name|testCollectionName
argument_list|)
expr_stmt|;
name|doTestDeleteAction
argument_list|(
name|testCollectionName
argument_list|,
name|solrUrl
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"testLoadDocsIntoGettingStartedCollection succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestHealthcheck
specifier|protected
name|void
name|doTestHealthcheck
parameter_list|(
name|String
name|testCollectionName
parameter_list|,
name|String
name|zkHost
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"healthcheck"
block|,
literal|"-collection"
block|,
name|testCollectionName
block|,
literal|"-zkHost"
block|,
name|zkHost
block|}
decl_stmt|;
name|SolrCLI
operator|.
name|HealthcheckTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|HealthcheckTool
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
init|=
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Healthcheck action failed!"
argument_list|,
name|tool
operator|.
name|runTool
argument_list|(
name|cli
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestDeleteAction
specifier|protected
name|void
name|doTestDeleteAction
parameter_list|(
name|String
name|testCollectionName
parameter_list|,
name|String
name|solrUrl
parameter_list|)
throws|throws
name|Exception
block|{
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"delete"
block|,
literal|"-name"
block|,
name|testCollectionName
block|,
literal|"-solrUrl"
block|,
name|solrUrl
block|}
decl_stmt|;
name|SolrCLI
operator|.
name|DeleteTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|DeleteTool
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
init|=
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Delete action failed!"
argument_list|,
name|tool
operator|.
name|runTool
argument_list|(
name|cli
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|SolrCLI
operator|.
name|safeCheckCollectionExists
argument_list|(
name|solrUrl
argument_list|,
name|testCollectionName
argument_list|)
argument_list|)
expr_stmt|;
comment|// it should not exist anymore
block|}
comment|/**    * Uses the SolrCLI config action to activate soft auto-commits for the getting started collection.    */
DECL|method|doTestConfigUpdate
specifier|protected
name|void
name|doTestConfigUpdate
parameter_list|(
name|String
name|testCollectionName
parameter_list|,
name|String
name|solrUrl
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|solrUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|solrUrl
operator|+=
literal|"/"
expr_stmt|;
name|String
name|configUrl
init|=
name|solrUrl
operator|+
name|testCollectionName
operator|+
literal|"/config"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configJson
init|=
name|SolrCLI
operator|.
name|getJson
argument_list|(
name|configUrl
argument_list|)
decl_stmt|;
name|Object
name|maxTimeFromConfig
init|=
name|SolrCLI
operator|.
name|atPath
argument_list|(
literal|"/config/updateHandler/autoSoftCommit/maxTime"
argument_list|,
name|configJson
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|maxTimeFromConfig
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|maxTimeFromConfig
argument_list|)
expr_stmt|;
name|String
name|prop
init|=
literal|"updateHandler.autoSoftCommit.maxTime"
decl_stmt|;
name|Long
name|maxTime
init|=
operator|new
name|Long
argument_list|(
literal|3000L
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"config"
block|,
literal|"-collection"
block|,
name|testCollectionName
block|,
literal|"-property"
block|,
name|prop
block|,
literal|"-value"
block|,
name|maxTime
operator|.
name|toString
argument_list|()
block|,
literal|"-solrUrl"
block|,
name|solrUrl
block|}
decl_stmt|;
name|SolrCLI
operator|.
name|ConfigTool
name|tool
init|=
operator|new
name|SolrCLI
operator|.
name|ConfigTool
argument_list|()
decl_stmt|;
name|CommandLine
name|cli
init|=
name|SolrCLI
operator|.
name|processCommandLineArgs
argument_list|(
name|SolrCLI
operator|.
name|joinCommonAndToolOptions
argument_list|(
name|tool
operator|.
name|getOptions
argument_list|()
argument_list|)
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending set-property '"
operator|+
name|prop
operator|+
literal|"'="
operator|+
name|maxTime
operator|+
literal|" to SolrCLI.ConfigTool."
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Set config property failed!"
argument_list|,
name|tool
operator|.
name|runTool
argument_list|(
name|cli
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|configJson
operator|=
name|SolrCLI
operator|.
name|getJson
argument_list|(
name|configUrl
argument_list|)
expr_stmt|;
name|maxTimeFromConfig
operator|=
name|SolrCLI
operator|.
name|atPath
argument_list|(
literal|"/config/updateHandler/autoSoftCommit/maxTime"
argument_list|,
name|configJson
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|maxTimeFromConfig
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|maxTime
argument_list|,
name|maxTimeFromConfig
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

