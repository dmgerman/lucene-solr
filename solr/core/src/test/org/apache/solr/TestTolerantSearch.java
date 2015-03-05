begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

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
name|params
operator|.
name|ShardParams
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
name|SolrQueryRequest
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
name|BinaryResponseWriter
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestTolerantSearch
specifier|public
class|class
name|TestTolerantSearch
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|collection1
specifier|private
specifier|static
name|SolrClient
name|collection1
decl_stmt|;
DECL|field|collection2
specifier|private
specifier|static
name|SolrClient
name|collection2
decl_stmt|;
DECL|field|shard1
specifier|private
specifier|static
name|String
name|shard1
decl_stmt|;
DECL|field|shard2
specifier|private
specifier|static
name|String
name|shard2
decl_stmt|;
DECL|field|solrHome
specifier|private
specifier|static
name|File
name|solrHome
decl_stmt|;
DECL|method|createSolrHome
specifier|private
specifier|static
name|File
name|createSolrHome
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|workDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|setupJettyTestHome
argument_list|(
name|workDir
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
operator|new
name|File
argument_list|(
name|SolrTestCaseJ4
operator|.
name|TEST_HOME
argument_list|()
operator|+
literal|"/collection1/conf/solrconfig-tolerant-search.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"/collection1/conf/solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"collection1"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"collection2"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|workDir
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|createThings
specifier|public
specifier|static
name|void
name|createThings
parameter_list|()
throws|throws
name|Exception
block|{
name|solrHome
operator|=
name|createSolrHome
argument_list|()
expr_stmt|;
name|createJetty
argument_list|(
name|solrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|url
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|collection1
operator|=
operator|new
name|HttpSolrClient
argument_list|(
name|url
operator|+
literal|"/collection1"
argument_list|)
expr_stmt|;
name|collection2
operator|=
operator|new
name|HttpSolrClient
argument_list|(
name|url
operator|+
literal|"/collection2"
argument_list|)
expr_stmt|;
name|String
name|urlCollection1
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
literal|"collection1"
decl_stmt|;
name|String
name|urlCollection2
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
literal|"collection2"
decl_stmt|;
name|shard1
operator|=
name|urlCollection1
operator|.
name|replaceAll
argument_list|(
literal|"https?://"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|shard2
operator|=
name|urlCollection2
operator|.
name|replaceAll
argument_list|(
literal|"https?://"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|//create second core
try|try
init|(
name|HttpSolrClient
name|nodeClient
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|url
argument_list|)
init|)
block|{
name|CoreAdminRequest
operator|.
name|Create
name|req
init|=
operator|new
name|CoreAdminRequest
operator|.
name|Create
argument_list|()
decl_stmt|;
name|req
operator|.
name|setCoreName
argument_list|(
literal|"collection2"
argument_list|)
expr_stmt|;
name|req
operator|.
name|setConfigSet
argument_list|(
literal|"collection1"
argument_list|)
expr_stmt|;
name|nodeClient
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"subject"
argument_list|,
literal|"batman"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"title"
argument_list|,
literal|"foo bar"
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"subject"
argument_list|,
literal|"superman"
argument_list|)
expr_stmt|;
name|collection2
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collection2
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"subject"
argument_list|,
literal|"aquaman"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"title"
argument_list|,
literal|"foo bar"
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collection1
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|destroyThings
specifier|public
specifier|static
name|void
name|destroyThings
parameter_list|()
throws|throws
name|Exception
block|{
name|collection1
operator|.
name|close
argument_list|()
expr_stmt|;
name|collection2
operator|.
name|close
argument_list|()
expr_stmt|;
name|collection1
operator|=
literal|null
expr_stmt|;
name|collection2
operator|=
literal|null
expr_stmt|;
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testGetFieldsPhaseError
specifier|public
name|void
name|testGetFieldsPhaseError
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|BadResponseWriter
operator|.
name|failOnGetFields
operator|=
literal|true
expr_stmt|;
name|BadResponseWriter
operator|.
name|failOnGetTopIds
operator|=
literal|false
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"subject:batman OR subject:superman"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|shard1
operator|+
literal|","
operator|+
name|shard2
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"stats"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"stats.field"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.fl"
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.count"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.mintf"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.mindf"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setHighlight
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetField
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFacet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"Dummy exception in BadResponseWriter"
argument_list|)
expr_stmt|;
try|try
block|{
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should get an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//expected
block|}
name|query
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|getBooleanArg
argument_list|(
literal|"partialResults"
argument_list|)
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|shardsInfo
init|=
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"shards.info"
argument_list|)
operator|)
decl_stmt|;
name|boolean
name|foundError
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
name|shardsInfo
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|shardsInfo
operator|.
name|getName
argument_list|(
name|i
argument_list|)
operator|.
name|contains
argument_list|(
literal|"collection2"
argument_list|)
condition|)
block|{
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|shardsInfo
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|foundError
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|foundError
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"batman"
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstValue
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|unIgnoreException
argument_list|(
literal|"Dummy exception in BadResponseWriter"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testGetTopIdsPhaseError
specifier|public
name|void
name|testGetTopIdsPhaseError
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|BadResponseWriter
operator|.
name|failOnGetTopIds
operator|=
literal|true
expr_stmt|;
name|BadResponseWriter
operator|.
name|failOnGetFields
operator|=
literal|false
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQuery
argument_list|(
literal|"subject:batman OR subject:superman"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|query
operator|.
name|addField
argument_list|(
literal|"subject"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|shard1
operator|+
literal|","
operator|+
name|shard2
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"debug"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"stats"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"stats.field"
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.fl"
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.count"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.mintf"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"mlt.mindf"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setHighlight
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|addFacetField
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|query
operator|.
name|setFacet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"Dummy exception in BadResponseWriter"
argument_list|)
expr_stmt|;
try|try
block|{
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should get an exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//expected
block|}
name|query
operator|.
name|set
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|QueryResponse
name|response
init|=
name|collection1
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|getResponseHeader
argument_list|()
operator|.
name|getBooleanArg
argument_list|(
literal|"partialResults"
argument_list|)
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|shardsInfo
init|=
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"shards.info"
argument_list|)
operator|)
decl_stmt|;
name|boolean
name|foundError
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
name|shardsInfo
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|shardsInfo
operator|.
name|getName
argument_list|(
name|i
argument_list|)
operator|.
name|contains
argument_list|(
literal|"collection2"
argument_list|)
condition|)
block|{
name|assertNotNull
argument_list|(
operator|(
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|shardsInfo
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
name|foundError
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|assertTrue
argument_list|(
name|foundError
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"batman"
argument_list|,
name|response
operator|.
name|getResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstValue
argument_list|(
literal|"subject"
argument_list|)
argument_list|)
expr_stmt|;
name|unIgnoreException
argument_list|(
literal|"Dummy exception in BadResponseWriter"
argument_list|)
expr_stmt|;
block|}
DECL|class|BadResponseWriter
specifier|public
specifier|static
class|class
name|BadResponseWriter
extends|extends
name|BinaryResponseWriter
block|{
DECL|field|failOnGetFields
specifier|private
specifier|static
name|boolean
name|failOnGetFields
init|=
literal|false
decl_stmt|;
DECL|field|failOnGetTopIds
specifier|private
specifier|static
name|boolean
name|failOnGetTopIds
init|=
literal|false
decl_stmt|;
DECL|method|BadResponseWriter
specifier|public
name|BadResponseWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
comment|// I want to fail on the shard request, not the original user request, and only on the
comment|// GET_FIELDS phase
if|if
condition|(
name|failOnGetFields
operator|&&
literal|"collection2"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
literal|"subject:batman OR subject:superman"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"q"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|&&
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"ids"
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Dummy exception in BadResponseWriter"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|failOnGetTopIds
operator|&&
literal|"collection2"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
literal|"subject:batman OR subject:superman"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"q"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|&&
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"ids"
argument_list|)
operator|==
literal|null
operator|&&
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
literal|"isShard"
argument_list|,
literal|false
argument_list|)
operator|==
literal|true
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Dummy exception in BadResponseWriter"
argument_list|)
throw|;
block|}
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|req
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

