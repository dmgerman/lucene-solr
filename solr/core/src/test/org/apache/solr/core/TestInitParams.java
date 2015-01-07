begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
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
name|request
operator|.
name|SolrRequestHandler
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
name|response
operator|.
name|SolrQueryResponse
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
name|HashMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
import|;
end_import

begin_class
DECL|class|TestInitParams
specifier|public
class|class
name|TestInitParams
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-paramset.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComponentWithInitParams
specifier|public
name|void
name|testComponentWithInitParams
parameter_list|()
block|{
for|for
control|(
name|String
name|s
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"/dump1"
argument_list|,
literal|"/dump3"
argument_list|,
literal|"/root/dump5"
argument_list|,
literal|"/root1/anotherlevel/dump6"
argument_list|)
control|)
block|{
name|SolrRequestHandler
name|handler
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|(
literal|"initArgs"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"initArgs"
argument_list|)
decl_stmt|;
name|NamedList
name|def
init|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|APPENDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|InitParams
name|initParams
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getInitParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|PluginInfo
name|pluginInfo
init|=
operator|new
name|PluginInfo
argument_list|(
literal|"requestHandler"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|,
operator|new
name|NamedList
argument_list|<>
argument_list|(
name|singletonMap
argument_list|(
literal|"defaults"
argument_list|,
operator|new
name|NamedList
argument_list|(
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
literal|"a"
argument_list|,
literal|"A1"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|initParams
operator|.
name|apply
argument_list|(
name|pluginInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|initParams
operator|.
name|defaults
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiInitParams
specifier|public
name|void
name|testMultiInitParams
parameter_list|()
block|{
name|SolrRequestHandler
name|handler
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/dump6"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|(
literal|"initArgs"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"initArgs"
argument_list|)
decl_stmt|;
name|NamedList
name|def
init|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"P"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|APPENDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testComponentWithConflictingInitParams
specifier|public
name|void
name|testComponentWithConflictingInitParams
parameter_list|()
block|{
name|SolrRequestHandler
name|handler
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/dump2"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|(
literal|"initArgs"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"initArgs"
argument_list|)
decl_stmt|;
name|NamedList
name|def
init|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A1"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B1"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|APPENDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"C1"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
name|def
operator|.
name|getAll
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNestedRequestHandler
specifier|public
name|void
name|testNestedRequestHandler
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/greedypath"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/greedypath/some/path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/greedypath/some/other/path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/greedypath/unknownpath"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testElevateExample
specifier|public
name|void
name|testElevateExample
parameter_list|()
block|{
name|SolrRequestHandler
name|handler
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/elevate"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|(
literal|"initArgs"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|rsp
operator|.
name|getValues
argument_list|()
operator|.
name|get
argument_list|(
literal|"initArgs"
argument_list|)
decl_stmt|;
name|NamedList
name|def
init|=
operator|(
name|NamedList
operator|)
name|nl
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"text"
argument_list|,
name|def
operator|.
name|get
argument_list|(
literal|"df"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMatchPath
specifier|public
name|void
name|testMatchPath
parameter_list|()
block|{
name|InitParams
name|initParams
init|=
operator|new
name|InitParams
argument_list|(
operator|new
name|PluginInfo
argument_list|(
name|InitParams
operator|.
name|TYPE
argument_list|,
name|ZkNodeProps
operator|.
name|makeMap
argument_list|(
literal|"path"
argument_list|,
literal|"/update/json/docs"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|initParams
operator|.
name|matchPath
argument_list|(
literal|"/update"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|initParams
operator|.
name|matchPath
argument_list|(
literal|"/update/json/docs"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

