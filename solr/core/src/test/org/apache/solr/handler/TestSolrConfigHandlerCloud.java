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
name|Arrays
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|LukeRequest
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
name|StrUtils
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
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|RequestParams
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
name|TestSolrConfigHandler
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
name|junit
operator|.
name|Test
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
name|handler
operator|.
name|TestBlobHandler
operator|.
name|getAsString
import|;
end_import

begin_class
DECL|class|TestSolrConfigHandlerCloud
specifier|public
class|class
name|TestSolrConfigHandlerCloud
extends|extends
name|AbstractFullDistribZkTestBase
block|{
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
operator|(
operator|(
name|HttpSolrClient
operator|)
name|client
operator|)
operator|::
name|getBaseURL
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
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
for|for
control|(
name|RestTestHarness
name|r
range|:
name|restTestHarnesses
control|)
block|{
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|setupHarnesses
argument_list|()
expr_stmt|;
name|testReqHandlerAPIs
argument_list|()
expr_stmt|;
name|testReqParams
argument_list|()
expr_stmt|;
name|testAdminPath
argument_list|()
expr_stmt|;
block|}
DECL|method|testAdminPath
specifier|private
name|void
name|testAdminPath
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testServerBaseUrl
init|=
name|getRandomServer
argument_list|(
name|cloudClient
argument_list|,
literal|"collection1"
argument_list|)
decl_stmt|;
name|RestTestHarness
name|writeHarness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"'create-requesthandler' : { 'name' : '/admin/luke', "
operator|+
literal|"'class': 'org.apache.solr.handler.DumpRequestHandler'}}"
decl_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
name|writeHarness
argument_list|,
name|testServerBaseUrl
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"class"
argument_list|)
argument_list|,
literal|"org.apache.solr.handler.DumpRequestHandler"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|cloudClient
operator|.
name|request
argument_list|(
operator|new
name|LukeRequest
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|testReqHandlerAPIs
specifier|private
name|void
name|testReqHandlerAPIs
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testServerBaseUrl
init|=
name|getRandomServer
argument_list|(
name|cloudClient
argument_list|,
literal|"collection1"
argument_list|)
decl_stmt|;
name|RestTestHarness
name|writeHarness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TestSolrConfigHandler
operator|.
name|reqhandlertests
argument_list|(
name|writeHarness
argument_list|,
name|testServerBaseUrl
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomServer
specifier|public
specifier|static
name|String
name|getRandomServer
parameter_list|(
name|CloudSolrClient
name|cloudClient
parameter_list|,
name|String
name|collName
parameter_list|)
block|{
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
name|collName
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
return|return
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testReqParams
specifier|private
name|void
name|testReqParams
parameter_list|()
throws|throws
name|Exception
block|{
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
name|RestTestHarness
name|writeHarness
init|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|payload
init|=
literal|" {\n"
operator|+
literal|"  'set' : {'x': {"
operator|+
literal|"                    'a':'A val',\n"
operator|+
literal|"                    'b': 'B val'}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
decl_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|Map
name|result
init|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"x"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|"A val"
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|"B val"
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"x"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|"'update-requesthandler' : { 'name' : '/dump', 'class': 'org.apache.solr.handler.DumpRequestHandler' }\n"
operator|+
literal|"}"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/dump"
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|"/dump"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/dump?wt=json&useParams=x"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|"A val"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|""
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
name|RequestParams
operator|.
name|USEPARAM
argument_list|)
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/dump?wt=json&useParams=x&a=fomrequest"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|"fomrequest"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|"{\n"
operator|+
literal|"'create-requesthandler' : { 'name' : '/dump1', 'class': 'org.apache.solr.handler.DumpRequestHandler', 'useParams':'x' }\n"
operator|+
literal|"}"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/overlay?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"overlay"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/dump1"
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|"/dump1"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/dump1?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|"A val"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|writeHarness
operator|=
name|restTestHarnesses
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|restTestHarnesses
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'y':{\n"
operator|+
literal|"                'c':'CY val',\n"
operator|+
literal|"                'b': 'BY val', "
operator|+
literal|"                'i': 20, "
operator|+
literal|"                'd': ['val 1', 'val 2']}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|"CY val"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|20l
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"i"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/dump?wt=json&useParams=y"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|"CY val"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|"BY val"
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|null
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"val 1"
argument_list|,
literal|"val 2"
argument_list|)
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|"20"
argument_list|,
name|asList
argument_list|(
literal|"params"
argument_list|,
literal|"i"
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'update' : {'y': {\n"
operator|+
literal|"                'c':'CY val modified',\n"
operator|+
literal|"                'e':'EY val',\n"
operator|+
literal|"                'b': 'BY val'"
operator|+
literal|"}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|"CY val modified"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|"EY val"
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {\n"
operator|+
literal|"  'set' : {'y': {\n"
operator|+
literal|"                'p':'P val',\n"
operator|+
literal|"                'q': 'Q val'"
operator|+
literal|"}\n"
operator|+
literal|"             }\n"
operator|+
literal|"  }"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|result
operator|=
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"p"
argument_list|)
argument_list|,
literal|"P val"
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|compareValues
argument_list|(
name|result
argument_list|,
literal|null
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|payload
operator|=
literal|" {'delete' : 'y'}"
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|runConfigCommand
argument_list|(
name|writeHarness
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|payload
argument_list|)
expr_stmt|;
name|TestSolrConfigHandler
operator|.
name|testForResponseElement
argument_list|(
literal|null
argument_list|,
name|urls
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|urls
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"/config/params?wt=json"
argument_list|,
name|cloudClient
argument_list|,
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"params"
argument_list|,
literal|"y"
argument_list|,
literal|"p"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
DECL|method|compareValues
specifier|public
specifier|static
name|void
name|compareValues
parameter_list|(
name|Map
name|result
parameter_list|,
name|Object
name|expected
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|jsonPath
parameter_list|)
block|{
name|Object
name|val
init|=
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|result
argument_list|,
literal|false
argument_list|,
name|jsonPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"Could not get expected value  {0} for path {1} full output {2}"
argument_list|,
name|expected
argument_list|,
name|jsonPath
argument_list|,
name|getAsString
argument_list|(
name|result
argument_list|)
argument_list|)
argument_list|,
name|expected
operator|instanceof
name|Predicate
condition|?
operator|(
operator|(
name|Predicate
operator|)
name|expected
operator|)
operator|.
name|test
argument_list|(
name|val
argument_list|)
else|:
name|Objects
operator|.
name|equals
argument_list|(
name|expected
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

