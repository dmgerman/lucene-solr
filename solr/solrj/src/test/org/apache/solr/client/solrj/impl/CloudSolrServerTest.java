begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.impl
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
operator|.
name|impl
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
name|AbstractZkTestCase
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
name|ExternalPaths
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
name|AfterClass
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
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * This test would be faster if we simulated the zk state instead.  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|CloudSolrServerTest
specifier|public
class|class
name|CloudSolrServerTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|SOLR_HOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_HOME
init|=
name|ExternalPaths
operator|.
name|SOURCE_HOME
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"src"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"test-files"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solrj"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"solr"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeSuperClass
specifier|public
specifier|static
name|void
name|beforeSuperClass
parameter_list|()
block|{
name|AbstractZkTestCase
operator|.
name|SOLRHOME
operator|=
operator|new
name|File
argument_list|(
name|SOLR_HOME
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterSuperClass
specifier|public
specifier|static
name|void
name|afterSuperClass
parameter_list|()
block|{        }
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
DECL|method|SOLR_HOME
specifier|public
specifier|static
name|String
name|SOLR_HOME
parameter_list|()
block|{
return|return
name|SOLR_HOME
return|;
block|}
annotation|@
name|Before
annotation|@
name|Override
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
comment|// we expect this time of exception as shards go up and down...
comment|//ignoreException(".*");
name|System
operator|.
name|setProperty
argument_list|(
literal|"numShards"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|sliceCount
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
DECL|method|CloudSolrServerTest
specifier|public
name|CloudSolrServerTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|shardCount
operator|=
literal|6
expr_stmt|;
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
name|assertNotNull
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|waitForThingsToLevelOut
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
literal|"a_t"
argument_list|,
literal|"to come to the aid of their country."
argument_list|)
expr_stmt|;
comment|// compare leaders list
name|CloudJettyRunner
name|shard1Leader
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
name|CloudJettyRunner
name|shard2Leader
init|=
name|shardToLeaderJetty
operator|.
name|get
argument_list|(
literal|"shard2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|cloudClient
operator|.
name|getLeaderUrlLists
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|leaderUrlSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|leaderUrlSet
operator|.
name|addAll
argument_list|(
name|cloudClient
operator|.
name|getLeaderUrlLists
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"fail check for leader:"
operator|+
name|shard1Leader
operator|.
name|url
operator|+
literal|" in "
operator|+
name|leaderUrlSet
argument_list|,
name|leaderUrlSet
operator|.
name|contains
argument_list|(
name|shard1Leader
operator|.
name|url
operator|+
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"fail check for leader:"
operator|+
name|shard2Leader
operator|.
name|url
operator|+
literal|" in "
operator|+
name|leaderUrlSet
argument_list|,
name|leaderUrlSet
operator|.
name|contains
argument_list|(
name|shard2Leader
operator|.
name|url
operator|+
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
comment|// compare replicas list
name|Set
argument_list|<
name|String
argument_list|>
name|replicas
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CloudJettyRunner
argument_list|>
name|jetties
init|=
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard1"
argument_list|)
decl_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cjetty
range|:
name|jetties
control|)
block|{
name|replicas
operator|.
name|add
argument_list|(
name|cjetty
operator|.
name|url
argument_list|)
expr_stmt|;
block|}
name|jetties
operator|=
name|shardToJetty
operator|.
name|get
argument_list|(
literal|"shard2"
argument_list|)
expr_stmt|;
for|for
control|(
name|CloudJettyRunner
name|cjetty
range|:
name|jetties
control|)
block|{
name|replicas
operator|.
name|add
argument_list|(
name|cjetty
operator|.
name|url
argument_list|)
expr_stmt|;
block|}
name|replicas
operator|.
name|remove
argument_list|(
name|shard1Leader
operator|.
name|url
argument_list|)
expr_stmt|;
name|replicas
operator|.
name|remove
argument_list|(
name|shard2Leader
operator|.
name|url
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|replicas
operator|.
name|size
argument_list|()
argument_list|,
name|cloudClient
operator|.
name|getReplicasLists
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection1"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|url
range|:
name|cloudClient
operator|.
name|getReplicasLists
argument_list|()
operator|.
name|get
argument_list|(
literal|"collection1"
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
literal|"fail check for replica:"
operator|+
name|url
operator|+
literal|" in "
operator|+
name|replicas
argument_list|,
name|replicas
operator|.
name|contains
argument_list|(
name|stripTrailingSlash
argument_list|(
name|url
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stripTrailingSlash
specifier|private
name|String
name|stripTrailingSlash
parameter_list|(
name|String
name|url
parameter_list|)
block|{
if|if
condition|(
name|url
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|url
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|url
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
return|return
name|url
return|;
block|}
annotation|@
name|Override
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
name|getDoc
argument_list|(
name|fields
argument_list|)
decl_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|getDoc
name|SolrInputDocument
name|getDoc
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

