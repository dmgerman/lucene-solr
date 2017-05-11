begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
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
name|util
operator|.
name|NamedList
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
DECL|class|TestV2Request
specifier|public
class|class
name|TestV2Request
extends|extends
name|SolrCloudTestCase
block|{
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
literal|4
argument_list|)
operator|.
name|addConfig
argument_list|(
literal|"config"
argument_list|,
name|getFile
argument_list|(
literal|"solrj/solr/collection1/conf"
argument_list|)
operator|.
name|toPath
argument_list|()
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSuccess
specifier|public
name|void
name|assertSuccess
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|V2Request
name|request
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The request failed"
argument_list|,
name|res
operator|.
name|get
argument_list|(
literal|"responseHeader"
argument_list|)
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
DECL|method|testIsCollectionRequest
specifier|public
name|void
name|testIsCollectionRequest
parameter_list|()
block|{
name|assertFalse
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/collections"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/collections/a/shards"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/collections/a/shards/"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/collections/a/update"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/a/update"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/a/schema"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/a"
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|isPerCollectionRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHttpSolrClient
specifier|public
name|void
name|testHttpSolrClient
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrClient
name|solrClient
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|cluster
operator|.
name|getJettySolrRunner
argument_list|(
literal|0
argument_list|)
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|doTest
argument_list|(
name|solrClient
argument_list|)
expr_stmt|;
name|solrClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCloudSolrClient
specifier|public
name|void
name|testCloudSolrClient
parameter_list|()
throws|throws
name|Exception
block|{
name|doTest
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doTest
specifier|private
name|void
name|doTest
parameter_list|(
name|SolrClient
name|client
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|assertSuccess
argument_list|(
name|client
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/collections"
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
literal|"{"
operator|+
literal|"  'create' : {"
operator|+
literal|"    'name' : 'test',"
operator|+
literal|"    'numShards' : 2,"
operator|+
literal|"    'replicationFactor' : 2,"
operator|+
literal|"    'config' : 'config'"
operator|+
literal|"  }"
operator|+
literal|"}"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
name|client
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
name|client
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/_introspect"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertSuccess
argument_list|(
name|client
argument_list|,
operator|new
name|V2Request
operator|.
name|Builder
argument_list|(
literal|"/c/test"
argument_list|)
operator|.
name|withMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|DELETE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|res
init|=
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
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|List
name|collections
init|=
operator|(
name|List
operator|)
name|res
operator|.
name|get
argument_list|(
literal|"collections"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|collections
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

