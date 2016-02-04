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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
operator|.
name|SuppressSSL
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
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CoreAdminRequest
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
name|CoreAdminRequest
operator|.
name|Unload
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
name|ZkCoreNodeProps
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|SolrParams
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
name|util
operator|.
name|ExecutorUtil
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
name|update
operator|.
name|DirectUpdateHandler2
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
name|DefaultSolrThreadFactory
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
name|TimeOut
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
name|Collection
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
name|concurrent
operator|.
name|SynchronousQueue
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
name|ThreadPoolExecutor
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

begin_comment
comment|/**  * This test simply does a bunch of basic things in solrcloud mode and asserts things  * work as expected.  */
end_comment

begin_class
annotation|@
name|Slow
annotation|@
name|SuppressSSL
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-5776"
argument_list|)
DECL|class|UnloadDistributedZkTest
specifier|public
class|class
name|UnloadDistributedZkTest
extends|extends
name|BasicDistributedZkTest
block|{
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr-no-core.xml"
return|;
block|}
DECL|method|UnloadDistributedZkTest
specifier|public
name|UnloadDistributedZkTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|testCoreUnloadAndLeaders
argument_list|()
expr_stmt|;
comment|// long
name|testUnloadLotsOfCores
argument_list|()
expr_stmt|;
comment|// long
name|testUnloadShardAndCollection
argument_list|()
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|super
operator|.
name|printLayout
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkCoreNamePresenceAndSliceCount
specifier|private
name|void
name|checkCoreNamePresenceAndSliceCount
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|String
name|coreName
parameter_list|,
name|boolean
name|shouldBePresent
parameter_list|,
name|int
name|expectedSliceCount
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|TimeOut
name|timeout
init|=
operator|new
name|TimeOut
argument_list|(
literal|45
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|Boolean
name|isPresent
init|=
literal|null
decl_stmt|;
comment|// null meaning "don't know"
while|while
condition|(
literal|null
operator|==
name|isPresent
operator|||
name|shouldBePresent
operator|!=
name|isPresent
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
specifier|final
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlices
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
name|printLayout
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"checkCoreNamePresenceAndSliceCount failed:"
operator|+
literal|" collection="
operator|+
name|collectionName
operator|+
literal|" CoreName="
operator|+
name|coreName
operator|+
literal|" shouldBePresent="
operator|+
name|shouldBePresent
operator|+
literal|" isPresent="
operator|+
name|isPresent
operator|+
literal|" expectedSliceCount="
operator|+
name|expectedSliceCount
operator|+
literal|" actualSliceCount="
operator|+
name|slices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expectedSliceCount
operator|==
operator|(
name|slices
operator|==
literal|null
condition|?
literal|0
else|:
name|slices
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
name|isPresent
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|slices
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|slices
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
block|{
if|if
condition|(
name|coreName
operator|.
name|equals
argument_list|(
name|replica
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
argument_list|)
condition|)
block|{
name|isPresent
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUnloadShardAndCollection
specifier|private
name|void
name|testUnloadShardAndCollection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numShards
init|=
literal|2
decl_stmt|;
specifier|final
name|String
name|collection
init|=
literal|"test_unload_shard_and_collection"
decl_stmt|;
specifier|final
name|String
name|coreName1
init|=
name|collection
operator|+
literal|"_1"
decl_stmt|;
specifier|final
name|String
name|coreName2
init|=
name|collection
operator|+
literal|"_2"
decl_stmt|;
comment|// create one leader and one replica
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
name|coreName1
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|String
name|coreDataDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|coreDataDir
argument_list|)
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setNumShards
argument_list|(
name|numShards
argument_list|)
expr_stmt|;
name|SolrClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|url1
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url1
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
name|createCmd
operator|=
operator|new
name|Create
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
name|coreName2
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|coreDataDir
operator|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|coreDataDir
argument_list|)
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
comment|// does not mean they are active and up yet :*
name|waitForRecoveriesToFinish
argument_list|(
name|collection
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|unloadInOrder
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|String
name|unloadCmdCoreName1
init|=
operator|(
name|unloadInOrder
condition|?
name|coreName1
else|:
name|coreName2
operator|)
decl_stmt|;
specifier|final
name|String
name|unloadCmdCoreName2
init|=
operator|(
name|unloadInOrder
condition|?
name|coreName2
else|:
name|coreName1
operator|)
decl_stmt|;
comment|// now unload one of the two
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
name|unloadCmdCoreName1
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
comment|// there should still be two shards (as of SOLR-5209)
name|checkCoreNamePresenceAndSliceCount
argument_list|(
name|collection
argument_list|,
name|unloadCmdCoreName1
argument_list|,
literal|false
comment|/* shouldBePresent */
argument_list|,
name|numShards
comment|/* expectedSliceCount */
argument_list|)
expr_stmt|;
comment|// now unload one of the other
name|unloadCmd
operator|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
name|unloadCmdCoreName2
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
name|checkCoreNamePresenceAndSliceCount
argument_list|(
name|collection
argument_list|,
name|unloadCmdCoreName2
argument_list|,
literal|false
comment|/* shouldBePresent */
argument_list|,
name|numShards
comment|/* expectedSliceCount */
argument_list|)
expr_stmt|;
block|}
comment|//printLayout();
comment|// the collection should still be present (as of SOLR-5209 replica removal does not cascade to remove the slice and collection)
name|assertTrue
argument_list|(
literal|"No longer found collection "
operator|+
name|collection
argument_list|,
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|hasCollection
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * @throws Exception on any problem    */
DECL|method|testCoreUnloadAndLeaders
specifier|private
name|void
name|testCoreUnloadAndLeaders
parameter_list|()
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
name|String
name|core1DataDir
init|=
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
literal|"unloadcollection1"
operator|+
literal|"_1n"
decl_stmt|;
comment|// create a new collection collection
name|SolrClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|url1
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url1
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection1"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core1DataDir
argument_list|)
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
block|}
name|ZkStateReader
name|zkStateReader
init|=
name|getCommonCloudSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|zkStateReader
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|int
name|slices
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|slices
argument_list|)
expr_stmt|;
name|client
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|String
name|url2
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url2
argument_list|)
init|)
block|{
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection2"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|String
name|core2dataDir
init|=
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
literal|"unloadcollection1"
operator|+
literal|"_2n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core2dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
block|}
name|zkStateReader
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
name|slices
operator|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|slices
argument_list|)
expr_stmt|;
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ZkCoreNodeProps
name|leaderProps
init|=
name|getLeaderUrlFromZk
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|)
decl_stmt|;
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
try|try
init|(
name|HttpSolrClient
name|collectionClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
init|)
block|{
comment|// lets try and use the solrj client to index and retrieve a couple
comment|// documents
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|6
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc2
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|7
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy3 sat on a walls"
argument_list|)
decl_stmt|;
name|SolrInputDocument
name|doc3
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
literal|8
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy2 sat on a walled"
argument_list|)
decl_stmt|;
name|collectionClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|add
argument_list|(
name|doc2
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|add
argument_list|(
name|doc3
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|// create another replica for our collection
name|client
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|String
name|url3
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url3
argument_list|)
init|)
block|{
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection3"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|String
name|core3dataDir
init|=
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
literal|"unloadcollection"
operator|+
literal|"_3n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core3dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
block|}
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// so that we start with some versions when we reload...
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|false
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|addClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url3
operator|+
literal|"/unloadcollection3"
argument_list|)
init|)
block|{
name|addClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
comment|// add a few docs
for|for
control|(
name|int
name|x
init|=
literal|20
init|;
name|x
operator|<
literal|100
condition|;
name|x
operator|++
control|)
block|{
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
name|x
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|addClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// don't commit so they remain in the tran log
comment|//collectionClient.commit();
comment|// unload the leader
try|try
init|(
name|HttpSolrClient
name|collectionClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|leaderProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
init|)
block|{
name|collectionClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
name|leaderProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|p
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|unloadCmd
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|collectionClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
block|}
comment|//    Thread.currentThread().sleep(500);
comment|//    printLayout();
name|int
name|tries
init|=
literal|50
decl_stmt|;
while|while
condition|(
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|zkStateReader
operator|.
name|getLeaderUrl
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|tries
operator|--
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Leader never changed"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ensure there is a leader
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|addClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url2
operator|+
literal|"/unloadcollection2"
argument_list|)
init|)
block|{
name|addClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|addClient
operator|.
name|setSoTimeout
argument_list|(
literal|90000
argument_list|)
expr_stmt|;
comment|// add a few docs while the leader is down
for|for
control|(
name|int
name|x
init|=
literal|101
init|;
name|x
operator|<
literal|200
condition|;
name|x
operator|++
control|)
block|{
name|SolrInputDocument
name|doc1
init|=
name|getDoc
argument_list|(
name|id
argument_list|,
name|x
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
decl_stmt|;
name|addClient
operator|.
name|add
argument_list|(
name|doc1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// create another replica for our collection
name|client
operator|=
name|clients
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|String
name|url4
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url4
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
literal|"unloadcollection4"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|String
name|core4dataDir
init|=
name|tmpDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
name|System
operator|.
name|nanoTime
argument_list|()
operator|+
literal|"unloadcollection"
operator|+
literal|"_4n"
decl_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core4dataDir
argument_list|)
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
block|}
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// unload the leader again
name|leaderProps
operator|=
name|getLeaderUrlFromZk
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|collectionClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|leaderProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
init|)
block|{
name|collectionClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|collectionClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
name|leaderProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|SolrParams
name|p
init|=
operator|(
name|ModifiableSolrParams
operator|)
name|unloadCmd
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|collectionClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
block|}
name|tries
operator|=
literal|50
expr_stmt|;
while|while
condition|(
name|leaderProps
operator|.
name|getCoreUrl
argument_list|()
operator|.
name|equals
argument_list|(
name|zkStateReader
operator|.
name|getLeaderUrl
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|tries
operator|--
operator|==
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"Leader never changed"
argument_list|)
expr_stmt|;
block|}
block|}
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
literal|"unloadcollection"
argument_list|,
literal|"shard1"
argument_list|,
literal|15000
argument_list|)
expr_stmt|;
comment|// set this back
name|DirectUpdateHandler2
operator|.
name|commitOnClose
operator|=
literal|true
expr_stmt|;
comment|// bring the downed leader back as replica
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|leaderProps
operator|.
name|getBaseUrl
argument_list|()
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|Create
name|createCmd
init|=
operator|new
name|Create
argument_list|()
decl_stmt|;
name|createCmd
operator|.
name|setCoreName
argument_list|(
name|leaderProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setCollection
argument_list|(
literal|"unloadcollection"
argument_list|)
expr_stmt|;
name|createCmd
operator|.
name|setDataDir
argument_list|(
name|getDataDir
argument_list|(
name|core1DataDir
argument_list|)
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|request
argument_list|(
name|createCmd
argument_list|)
expr_stmt|;
block|}
name|waitForRecoveriesToFinish
argument_list|(
literal|"unloadcollection"
argument_list|,
name|zkStateReader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|long
name|found1
decl_stmt|,
name|found3
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url2
operator|+
literal|"/unloadcollection"
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|found1
operator|=
name|adminClient
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url3
operator|+
literal|"/unloadcollection"
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|found3
operator|=
name|adminClient
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url4
operator|+
literal|"/unloadcollection"
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|commit
argument_list|()
expr_stmt|;
name|SolrQuery
name|q
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|q
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|long
name|found4
init|=
name|adminClient
operator|.
name|query
argument_list|(
name|q
argument_list|)
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
comment|// all 3 shards should now have the same number of docs
name|assertEquals
argument_list|(
name|found1
argument_list|,
name|found3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|found3
argument_list|,
name|found4
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUnloadLotsOfCores
specifier|private
name|void
name|testUnloadLotsOfCores
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|String
name|url3
init|=
name|getBaseUrl
argument_list|(
name|client
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|HttpSolrClient
name|adminClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url3
argument_list|)
init|)
block|{
name|adminClient
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|adminClient
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|int
name|cnt
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|ThreadPoolExecutor
name|executor
init|=
operator|new
name|ExecutorUtil
operator|.
name|MDCAwareThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"testExecutor"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
comment|// create the cores
name|createCores
argument_list|(
name|adminClient
argument_list|,
name|executor
argument_list|,
literal|"multiunload"
argument_list|,
literal|2
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
name|executor
operator|=
operator|new
name|ExecutorUtil
operator|.
name|MDCAwareThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"testExecutor"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|cnt
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|freezeJ
init|=
name|j
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Unload
name|unloadCmd
init|=
operator|new
name|Unload
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|unloadCmd
operator|.
name|setCoreName
argument_list|(
literal|"multiunload"
operator|+
name|freezeJ
argument_list|)
expr_stmt|;
try|try
block|{
name|adminClient
operator|.
name|request
argument_list|(
name|unloadCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|ExecutorUtil
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

