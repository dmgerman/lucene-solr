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
name|IOException
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
name|HashMap
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
name|Map
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
name|lang
operator|.
name|StringUtils
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
name|CloudSolrClient
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
name|CollectionAdminRequest
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
name|QueryRequest
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
name|UpdateRequest
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
name|common
operator|.
name|SolrDocument
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
name|SolrDocumentList
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
name|ClusterState
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
name|ZkStateReader
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
name|BeforeClass
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
import|;
end_import

begin_comment
comment|/**  * Tests using fromIndex that points to a collection in SolrCloud mode.  */
end_comment

begin_class
DECL|class|DistribJoinFromCollectionTest
specifier|public
class|class
name|DistribJoinFromCollectionTest
extends|extends
name|SolrCloudTestCase
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
DECL|field|scoreModes
specifier|final
specifier|private
specifier|static
name|String
index|[]
name|scoreModes
init|=
block|{
literal|"avg"
block|,
literal|"max"
block|,
literal|"min"
block|,
literal|"total"
block|}
decl_stmt|;
comment|//    resetExceptionIgnores();
DECL|field|toColl
specifier|private
specifier|static
name|String
name|toColl
init|=
literal|"to_2x2"
decl_stmt|;
DECL|field|fromColl
specifier|private
specifier|static
name|String
name|fromColl
init|=
literal|"from_1x4"
decl_stmt|;
DECL|field|toDocId
specifier|private
specifier|static
name|String
name|toDocId
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|configDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
literal|"collection1"
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
name|int
name|nodeCount
init|=
literal|5
decl_stmt|;
name|configureCluster
argument_list|(
name|nodeCount
argument_list|)
operator|.
name|addConfig
argument_list|(
name|configName
argument_list|,
name|configDir
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionProperties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"config"
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"schema"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
comment|// create a collection holding data for the "to" side of the JOIN
name|int
name|shards
init|=
literal|2
decl_stmt|;
name|int
name|replicas
init|=
literal|2
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|toColl
argument_list|,
name|configName
argument_list|,
name|shards
argument_list|,
name|replicas
argument_list|)
operator|.
name|setProperties
argument_list|(
name|collectionProperties
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// get the set of nodes where replicas for the "to" collection exist
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|cs
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|cs
operator|.
name|getCollection
argument_list|(
name|toColl
argument_list|)
operator|.
name|getActiveSlices
argument_list|()
control|)
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
name|nodeSet
operator|.
name|add
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeSet
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// deploy the "from" collection to all nodes where the "to" collection exists
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|fromColl
argument_list|,
name|configName
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|)
operator|.
name|setCreateNodeSet
argument_list|(
name|StringUtils
operator|.
name|join
argument_list|(
name|nodeSet
argument_list|,
literal|","
argument_list|)
argument_list|)
operator|.
name|setProperties
argument_list|(
name|collectionProperties
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|toDocId
operator|=
name|indexDoc
argument_list|(
name|toColl
argument_list|,
literal|1001
argument_list|,
literal|"a"
argument_list|,
literal|null
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|fromColl
argument_list|,
literal|2001
argument_list|,
literal|"a"
argument_list|,
literal|"c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// so the commits fire
block|}
annotation|@
name|Test
DECL|method|testScore
specifier|public
name|void
name|testScore
parameter_list|()
throws|throws
name|Exception
block|{
comment|//without score
name|testJoins
argument_list|(
name|toColl
argument_list|,
name|fromColl
argument_list|,
name|toDocId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoScore
specifier|public
name|void
name|testNoScore
parameter_list|()
throws|throws
name|Exception
block|{
comment|//with score
name|testJoins
argument_list|(
name|toColl
argument_list|,
name|fromColl
argument_list|,
name|toDocId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"DistribJoinFromCollectionTest logic complete ... deleting the "
operator|+
name|toColl
operator|+
literal|" and "
operator|+
name|fromColl
operator|+
literal|" collections"
argument_list|)
expr_stmt|;
comment|// try to clean up
for|for
control|(
name|String
name|c
range|:
operator|new
name|String
index|[]
block|{
name|toColl
block|,
name|fromColl
block|}
control|)
block|{
try|try
block|{
name|CollectionAdminRequest
operator|.
name|Delete
name|req
init|=
name|CollectionAdminRequest
operator|.
name|deleteCollection
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|req
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't fail the test
name|log
operator|.
name|warn
argument_list|(
literal|"Could not delete collection {} after test completed due to: "
operator|+
name|e
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"DistribJoinFromCollectionTest succeeded ... shutting down now!"
argument_list|)
expr_stmt|;
block|}
DECL|method|testJoins
specifier|private
name|void
name|testJoins
parameter_list|(
name|String
name|toColl
parameter_list|,
name|String
name|fromColl
parameter_list|,
name|String
name|toDocId
parameter_list|,
name|boolean
name|isScoresTest
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
comment|// verify the join with fromIndex works
specifier|final
name|String
name|fromQ
init|=
literal|"match_s:c^2"
decl_stmt|;
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
block|{
specifier|final
name|String
name|joinQ
init|=
literal|"{!join "
operator|+
name|anyScoreMode
argument_list|(
name|isScoresTest
argument_list|)
operator|+
literal|"from=join_s fromIndex="
operator|+
name|fromColl
operator|+
literal|" to=join_s}"
operator|+
name|fromQ
decl_stmt|;
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s,score"
argument_list|)
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|(
name|client
operator|.
name|request
argument_list|(
name|qr
argument_list|)
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|hits
init|=
name|rsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 1 doc, got "
operator|+
name|hits
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|toDocId
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"get_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertScore
argument_list|(
name|isScoresTest
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|//negative test before creating an alias
name|checkAbsentFromIndex
argument_list|(
name|fromColl
argument_list|,
name|toColl
argument_list|,
name|isScoresTest
argument_list|)
expr_stmt|;
comment|// create an alias for the fromIndex and then query through the alias
name|String
name|alias
init|=
name|fromColl
operator|+
literal|"Alias"
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|CreateAlias
name|request
init|=
name|CollectionAdminRequest
operator|.
name|createAlias
argument_list|(
name|alias
argument_list|,
name|fromColl
argument_list|)
decl_stmt|;
name|request
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|{
specifier|final
name|String
name|joinQ
init|=
literal|"{!join "
operator|+
name|anyScoreMode
argument_list|(
name|isScoresTest
argument_list|)
operator|+
literal|"from=join_s fromIndex="
operator|+
name|alias
operator|+
literal|" to=join_s}"
operator|+
name|fromQ
decl_stmt|;
specifier|final
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s,score"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|(
name|client
operator|.
name|request
argument_list|(
name|qr
argument_list|)
argument_list|,
name|client
argument_list|)
decl_stmt|;
specifier|final
name|SolrDocumentList
name|hits
init|=
name|rsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected 1 doc"
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|SolrDocument
name|doc
init|=
name|hits
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|toDocId
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"get_s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertScore
argument_list|(
name|isScoresTest
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|//negative test after creating an alias
name|checkAbsentFromIndex
argument_list|(
name|fromColl
argument_list|,
name|toColl
argument_list|,
name|isScoresTest
argument_list|)
expr_stmt|;
block|{
comment|// verify join doesn't work if no match in the "from" index
specifier|final
name|String
name|joinQ
init|=
literal|"{!join "
operator|+
operator|(
name|anyScoreMode
argument_list|(
name|isScoresTest
argument_list|)
operator|)
operator|+
literal|"from=join_s fromIndex="
operator|+
name|fromColl
operator|+
literal|" to=join_s}match_s:d"
decl_stmt|;
specifier|final
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s,score"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|QueryResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|(
name|client
operator|.
name|request
argument_list|(
name|qr
argument_list|)
argument_list|,
name|client
argument_list|)
decl_stmt|;
specifier|final
name|SolrDocumentList
name|hits
init|=
name|rsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected no hits"
argument_list|,
name|hits
operator|.
name|getNumFound
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertScore
specifier|private
name|void
name|assertScore
parameter_list|(
name|boolean
name|isScoresTest
parameter_list|,
name|SolrDocument
name|doc
parameter_list|)
block|{
if|if
condition|(
name|isScoresTest
condition|)
block|{
name|assertThat
argument_list|(
literal|"score join doesn't return 1.0"
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"score"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|not
argument_list|(
literal|"1.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|"Solr join has constant score"
argument_list|,
literal|"1.0"
argument_list|,
name|doc
operator|.
name|getFirstValue
argument_list|(
literal|"score"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|anyScoreMode
specifier|private
name|String
name|anyScoreMode
parameter_list|(
name|boolean
name|isScoresTest
parameter_list|)
block|{
return|return
name|isScoresTest
condition|?
literal|"score="
operator|+
operator|(
name|scoreModes
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|scoreModes
operator|.
name|length
argument_list|)
index|]
operator|)
operator|+
literal|" "
else|:
literal|""
return|;
block|}
DECL|method|checkAbsentFromIndex
specifier|private
name|void
name|checkAbsentFromIndex
parameter_list|(
name|String
name|fromColl
parameter_list|,
name|String
name|toColl
parameter_list|,
name|boolean
name|isScoresTest
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
specifier|final
name|String
name|wrongName
init|=
name|fromColl
operator|+
literal|"WrongName"
decl_stmt|;
specifier|final
name|String
name|joinQ
init|=
literal|"{!join "
operator|+
operator|(
name|anyScoreMode
argument_list|(
name|isScoresTest
argument_list|)
operator|)
operator|+
literal|"from=join_s fromIndex="
operator|+
name|wrongName
operator|+
literal|" to=join_s}match_s:c"
decl_stmt|;
specifier|final
name|QueryRequest
name|qr
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|(
literal|"collection"
argument_list|,
name|toColl
argument_list|,
literal|"q"
argument_list|,
name|joinQ
argument_list|,
literal|"fl"
argument_list|,
literal|"id,get_s,score"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|qr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|HttpSolrClient
operator|.
name|RemoteSolrException
name|ex
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
operator|.
name|code
argument_list|,
name|ex
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|wrongName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|indexDoc
specifier|protected
specifier|static
name|String
name|indexDoc
parameter_list|(
name|String
name|collection
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|joinField
parameter_list|,
name|String
name|matchField
parameter_list|,
name|String
name|getField
parameter_list|)
throws|throws
name|Exception
block|{
name|UpdateRequest
name|up
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|up
operator|.
name|setCommitWithin
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|up
operator|.
name|setParam
argument_list|(
literal|"collection"
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|String
name|docId
init|=
literal|""
operator|+
name|id
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|)
expr_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"join_s"
argument_list|,
name|joinField
argument_list|)
expr_stmt|;
if|if
condition|(
name|matchField
operator|!=
literal|null
condition|)
name|doc
operator|.
name|addField
argument_list|(
literal|"match_s"
argument_list|,
name|matchField
argument_list|)
expr_stmt|;
if|if
condition|(
name|getField
operator|!=
literal|null
condition|)
name|doc
operator|.
name|addField
argument_list|(
literal|"get_s"
argument_list|,
name|getField
argument_list|)
expr_stmt|;
name|up
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
name|up
argument_list|)
expr_stmt|;
return|return
name|docId
return|;
block|}
block|}
end_class

end_unit

