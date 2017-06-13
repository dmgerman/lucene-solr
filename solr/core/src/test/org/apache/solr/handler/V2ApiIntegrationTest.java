begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|V2Request
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
name|SolrCloudTestCase
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
name|util
operator|.
name|NamedList
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
name|Utils
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

begin_class
DECL|class|V2ApiIntegrationTest
specifier|public
class|class
name|V2ApiIntegrationTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|COLL_NAME
specifier|private
specifier|static
name|String
name|COLL_NAME
init|=
literal|"collection1"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createCluster
specifier|public
specifier|static
name|void
name|createCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|configureCluster
argument_list|(
literal|2
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
literal|"cloud-managed"
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
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|COLL_NAME
argument_list|,
literal|"conf1"
argument_list|,
literal|1
argument_list|,
literal|2
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
block|}
annotation|@
name|Test
DECL|method|testWelcomeMessage
specifier|public
name|void
name|testWelcomeMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|NamedList
name|res
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|""
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|NamedList
name|header
init|=
operator|(
name|NamedList
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|header
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/_introspect"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|=
operator|(
name|NamedList
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|header
operator|.
name|get
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntrospect
specifier|public
name|void
name|testIntrospect
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"command"
argument_list|,
literal|"XXXX"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"method"
argument_list|,
literal|"POST"
argument_list|)
expr_stmt|;
name|Map
name|result
init|=
name|resAsMap
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/"
operator|+
name|COLL_NAME
operator|+
literal|"/_introspect"
argument_list|)
operator|.
name|withParams
argument_list|(
name|params
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Command not found!"
argument_list|,
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|result
argument_list|,
literal|false
argument_list|,
literal|"/spec[0]/commands/XXXX"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleWarning
specifier|public
name|void
name|testSingleWarning
parameter_list|()
throws|throws
name|Exception
block|{
name|NamedList
name|resp
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/"
operator|+
name|COLL_NAME
operator|+
literal|"/_introspect"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|List
name|warnings
init|=
name|resp
operator|.
name|getAll
argument_list|(
literal|"WARNING"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|warnings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetPropertyValidationOfCluster
specifier|public
name|void
name|testSetPropertyValidationOfCluster
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|NamedList
name|resp
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/cluster"
argument_list|)
operator|.
name|withMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
operator|.
name|withPayload
argument_list|(
literal|"{set-property: {name: autoAddReplicas, val:false}}"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|resp
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"status=0"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|request
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/cluster"
argument_list|)
operator|.
name|withMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
operator|.
name|withPayload
argument_list|(
literal|"{set-property: {name: autoAddReplicas, val:null}}"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|resp
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"status=0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollectionsApi
specifier|public
name|void
name|testCollectionsApi
parameter_list|()
throws|throws
name|Exception
block|{
name|CloudSolrClient
name|client
init|=
name|cluster
operator|.
name|getSolrClient
argument_list|()
decl_stmt|;
name|Map
name|result
init|=
name|resAsMap
argument_list|(
name|client
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/"
operator|+
name|COLL_NAME
operator|+
literal|"/get/_introspect"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/c/collection1/get"
argument_list|,
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|result
argument_list|,
literal|true
argument_list|,
literal|"/spec[0]/url/paths[0]"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|resAsMap
argument_list|(
name|client
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/collections/"
operator|+
name|COLL_NAME
operator|+
literal|"/get/_introspect"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/collections/collection1/get"
argument_list|,
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|result
argument_list|,
literal|true
argument_list|,
literal|"/spec[0]/url/paths[0]"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|tempDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|client
operator|.
name|request
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c"
argument_list|)
operator|.
name|withMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
operator|.
name|withPayload
argument_list|(
literal|"{backup-collection:{name: backup_test, collection: "
operator|+
name|COLL_NAME
operator|+
literal|" , location: '"
operator|+
name|tempDir
operator|+
literal|"' }}"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|resAsMap
specifier|private
name|Map
name|resAsMap
parameter_list|(
name|CloudSolrClient
name|client
parameter_list|,
name|V2Request
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|rsp
operator|.
name|asMap
argument_list|(
literal|100
argument_list|)
return|;
block|}
block|}
end_class

end_unit

