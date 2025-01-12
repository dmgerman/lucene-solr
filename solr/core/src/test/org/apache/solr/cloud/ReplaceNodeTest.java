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
name|ArrayList
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
name|EnumSet
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
name|CoreAdminRequest
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
name|CoreAdminResponse
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
name|RequestStatusState
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
name|util
operator|.
name|StrUtils
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

begin_class
DECL|class|ReplaceNodeTest
specifier|public
class|class
name|ReplaceNodeTest
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
name|configureCluster
argument_list|(
literal|6
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"conf1"
argument_list|,
name|TEST_PATH
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"configsets"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"cloud-dynamic"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"conf"
argument_list|)
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
DECL|method|getSolrXml
specifier|protected
name|String
name|getSolrXml
parameter_list|()
block|{
return|return
literal|"solr.xml"
return|;
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
name|cluster
operator|.
name|waitForAllNodes
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|String
name|coll
init|=
literal|"replacenodetest_coll"
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"total_jettys: "
operator|+
name|cluster
operator|.
name|getJettySolrRunners
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|CloudSolrClient
name|cloudClient
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
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
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|liveNodes
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|l
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|emptyNode
init|=
name|l
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|node2bdecommissioned
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|create
decl_stmt|;
comment|// NOTE: always using the createCollection that takes in 'int' for all types of replicas, so we never
comment|// have to worry about null checking when comparing the Create command with the final Slices
name|create
operator|=
name|pickRandom
argument_list|(
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|,
comment|// check also replicationFactor 1
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|coll
argument_list|,
literal|"conf1"
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|create
operator|.
name|setCreateNodeSet
argument_list|(
name|StrUtils
operator|.
name|join
argument_list|(
name|l
argument_list|,
literal|','
argument_list|)
argument_list|)
operator|.
name|setMaxShardsPerNode
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|cloudClient
operator|.
name|request
argument_list|(
name|create
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"excluded_node : {}  "
argument_list|,
name|emptyNode
argument_list|)
expr_stmt|;
operator|new
name|CollectionAdminRequest
operator|.
name|ReplaceNode
argument_list|(
name|node2bdecommissioned
argument_list|,
name|emptyNode
argument_list|)
operator|.
name|processAsync
argument_list|(
literal|"000"
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
name|CollectionAdminRequest
operator|.
name|RequestStatus
name|requestStatus
init|=
name|CollectionAdminRequest
operator|.
name|requestStatus
argument_list|(
literal|"000"
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|CollectionAdminRequest
operator|.
name|RequestStatusResponse
name|rsp
init|=
name|requestStatus
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getRequestStatus
argument_list|()
operator|==
name|RequestStatusState
operator|.
name|COMPLETED
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|assertFalse
argument_list|(
name|rsp
operator|.
name|getRequestStatus
argument_list|()
operator|==
name|RequestStatusState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|success
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|coreclient
init|=
name|getHttpSolrClient
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|node2bdecommissioned
argument_list|)
argument_list|)
init|)
block|{
name|CoreAdminResponse
name|status
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
literal|null
argument_list|,
name|coreclient
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|getCoreStatus
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
comment|//let's do it back
operator|new
name|CollectionAdminRequest
operator|.
name|ReplaceNode
argument_list|(
name|emptyNode
argument_list|,
name|node2bdecommissioned
argument_list|)
operator|.
name|setParallel
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|.
name|processAsync
argument_list|(
literal|"001"
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
name|requestStatus
operator|=
name|CollectionAdminRequest
operator|.
name|requestStatus
argument_list|(
literal|"001"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|CollectionAdminRequest
operator|.
name|RequestStatusResponse
name|rsp
init|=
name|requestStatus
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
decl_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getRequestStatus
argument_list|()
operator|==
name|RequestStatusState
operator|.
name|COMPLETED
condition|)
block|{
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|assertFalse
argument_list|(
name|rsp
operator|.
name|getRequestStatus
argument_list|()
operator|==
name|RequestStatusState
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|success
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|coreclient
init|=
name|getHttpSolrClient
argument_list|(
name|cloudClient
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|emptyNode
argument_list|)
argument_list|)
init|)
block|{
name|CoreAdminResponse
name|status
init|=
name|CoreAdminRequest
operator|.
name|getStatus
argument_list|(
literal|null
argument_list|,
name|coreclient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expecting no cores but found some: "
operator|+
name|status
operator|.
name|getCoreStatus
argument_list|()
argument_list|,
literal|0
argument_list|,
name|status
operator|.
name|getCoreStatus
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocCollection
name|collection
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
name|coll
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|create
operator|.
name|getNumShards
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|collection
operator|.
name|getSlices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|s
range|:
name|collection
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|create
operator|.
name|getNumNrtReplicas
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|s
operator|.
name|getReplicas
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|Replica
operator|.
name|Type
operator|.
name|NRT
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|create
operator|.
name|getNumTlogReplicas
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|s
operator|.
name|getReplicas
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|Replica
operator|.
name|Type
operator|.
name|TLOG
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|create
operator|.
name|getNumPullReplicas
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|s
operator|.
name|getReplicas
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|Replica
operator|.
name|Type
operator|.
name|PULL
argument_list|)
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

