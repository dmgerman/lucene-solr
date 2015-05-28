begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ByteArrayEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
name|response
operator|.
name|CollectionAdminResponse
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
name|core
operator|.
name|ConfigOverlay
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
name|SimplePostTool
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
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|ConfigOverlay
operator|.
name|getObjectByPath
import|;
end_import

begin_class
DECL|class|TestBlobHandler
specifier|public
class|class
name|TestBlobHandler
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestBlobHandler
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|doBlobHandlerTest
specifier|public
name|void
name|doBlobHandlerTest
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|SolrClient
name|client
init|=
name|createNewSolrClient
argument_list|(
literal|""
argument_list|,
name|getBaseUrl
argument_list|(
operator|(
name|HttpSolrClient
operator|)
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|CollectionAdminResponse
name|response1
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|createCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
literal|".system"
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|response1
operator|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response1
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response1
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
name|DocCollection
name|sysColl
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
literal|".system"
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
name|sysColl
operator|.
name|getActiveSlicesMap
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getLeader
argument_list|()
decl_stmt|;
name|String
name|baseUrl
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|baseUrl
operator|+
literal|"/.system/config/requestHandler"
decl_stmt|;
name|Map
name|map
init|=
name|TestSolrConfigHandlerConcurrent
operator|.
name|getAsMap
argument_list|(
name|url
argument_list|,
name|cloudClient
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"solr.BlobHandler"
argument_list|,
name|getObjectByPath
argument_list|(
name|map
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"config"
argument_list|,
literal|"requestHandler"
argument_list|,
literal|"/blob"
argument_list|,
literal|"class"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytarr
init|=
operator|new
name|byte
index|[
literal|1024
index|]
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
name|bytarr
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|bytarr
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|127
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytarr2
init|=
operator|new
name|byte
index|[
literal|2048
index|]
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
name|bytarr2
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|bytarr2
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|i
operator|%
literal|127
argument_list|)
expr_stmt|;
name|String
name|blobName
init|=
literal|"test"
decl_stmt|;
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
name|baseUrl
argument_list|,
name|blobName
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytarr
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|postAndCheck
argument_list|(
name|cloudClient
argument_list|,
name|baseUrl
argument_list|,
name|blobName
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytarr2
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|url
operator|=
name|baseUrl
operator|+
literal|"/.system/blob/test/1"
expr_stmt|;
name|map
operator|=
name|TestSolrConfigHandlerConcurrent
operator|.
name|getAsMap
argument_list|(
name|url
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
name|List
name|l
init|=
operator|(
name|List
operator|)
name|ConfigOverlay
operator|.
name|getObjectByPath
argument_list|(
name|map
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"docs"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|""
operator|+
name|map
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|""
operator|+
name|map
argument_list|,
name|l
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|map
operator|=
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|bytarr
operator|.
name|length
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"size"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|compareInputAndOutput
argument_list|(
name|baseUrl
operator|+
literal|"/.system/blob/test?wt=filestream"
argument_list|,
name|bytarr2
argument_list|)
expr_stmt|;
name|compareInputAndOutput
argument_list|(
name|baseUrl
operator|+
literal|"/.system/blob/test/1?wt=filestream"
argument_list|,
name|bytarr
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createSystemCollection
specifier|public
specifier|static
name|void
name|createSystemCollection
parameter_list|(
name|SolrClient
name|client
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|CollectionAdminResponse
name|response1
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|Create
name|createCollectionRequest
init|=
operator|new
name|CollectionAdminRequest
operator|.
name|Create
argument_list|()
operator|.
name|setCollectionName
argument_list|(
literal|".system"
argument_list|)
operator|.
name|setNumShards
argument_list|(
literal|1
argument_list|)
operator|.
name|setReplicationFactor
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|response1
operator|=
name|createCollectionRequest
operator|.
name|process
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response1
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|response1
operator|.
name|isSuccess
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|postAndCheck
specifier|public
specifier|static
name|void
name|postAndCheck
parameter_list|(
name|CloudSolrClient
name|cloudClient
parameter_list|,
name|String
name|baseUrl
parameter_list|,
name|String
name|blobName
parameter_list|,
name|ByteBuffer
name|bytes
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|postData
argument_list|(
name|cloudClient
argument_list|,
name|baseUrl
argument_list|,
name|blobName
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|String
name|url
decl_stmt|;
name|Map
name|map
init|=
literal|null
decl_stmt|;
name|List
name|l
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
literal|150
condition|;
name|i
operator|++
control|)
block|{
comment|//15 secs
name|url
operator|=
name|baseUrl
operator|+
literal|"/.system/blob/"
operator|+
name|blobName
expr_stmt|;
name|map
operator|=
name|TestSolrConfigHandlerConcurrent
operator|.
name|getAsMap
argument_list|(
name|url
argument_list|,
name|cloudClient
argument_list|)
expr_stmt|;
name|String
name|numFound
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|ConfigOverlay
operator|.
name|getObjectByPath
argument_list|(
name|map
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"numFound"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
literal|""
operator|+
name|count
operator|)
operator|.
name|equals
argument_list|(
name|numFound
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
continue|continue;
block|}
name|l
operator|=
operator|(
name|List
operator|)
name|ConfigOverlay
operator|.
name|getObjectByPath
argument_list|(
name|map
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"response"
argument_list|,
literal|"docs"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|map
operator|=
operator|(
name|Map
operator|)
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
operator|+
name|bytes
operator|.
name|limit
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"size"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|fail
argument_list|(
name|StrUtils
operator|.
name|formatString
argument_list|(
literal|"Could not successfully add blob after {0} attempts. Expecting {1} items. time elapsed {2}  output  for url is {3}"
argument_list|,
name|i
argument_list|,
name|count
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|,
name|getAsString
argument_list|(
name|map
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getAsString
specifier|public
specifier|static
name|String
name|getAsString
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|map
argument_list|)
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
DECL|method|compareInputAndOutput
specifier|private
name|void
name|compareInputAndOutput
parameter_list|(
name|String
name|url
parameter_list|,
name|byte
index|[]
name|bytarr
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpClient
name|httpClient
init|=
name|cloudClient
operator|.
name|getLbClient
argument_list|()
operator|.
name|getHttpClient
argument_list|()
decl_stmt|;
name|HttpGet
name|httpGet
init|=
operator|new
name|HttpGet
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|HttpResponse
name|entity
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|httpGet
argument_list|)
decl_stmt|;
name|ByteBuffer
name|b
init|=
name|SimplePostTool
operator|.
name|inputStreamToByteArray
argument_list|(
name|entity
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
name|b
operator|.
name|limit
argument_list|()
argument_list|,
name|bytarr
operator|.
name|length
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
name|bytarr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|b
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|bytarr
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|httpGet
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|postData
specifier|public
specifier|static
name|void
name|postData
parameter_list|(
name|CloudSolrClient
name|cloudClient
parameter_list|,
name|String
name|baseUrl
parameter_list|,
name|String
name|blobName
parameter_list|,
name|ByteBuffer
name|bytarr
parameter_list|)
throws|throws
name|IOException
block|{
name|HttpPost
name|httpPost
init|=
literal|null
decl_stmt|;
name|HttpEntity
name|entity
decl_stmt|;
name|String
name|response
init|=
literal|null
decl_stmt|;
try|try
block|{
name|httpPost
operator|=
operator|new
name|HttpPost
argument_list|(
name|baseUrl
operator|+
literal|"/.system/blob/"
operator|+
name|blobName
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/octet-stream"
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|setEntity
argument_list|(
operator|new
name|ByteArrayEntity
argument_list|(
name|bytarr
operator|.
name|array
argument_list|()
argument_list|,
name|bytarr
operator|.
name|arrayOffset
argument_list|()
argument_list|,
name|bytarr
operator|.
name|limit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|entity
operator|=
name|cloudClient
operator|.
name|getLbClient
argument_list|()
operator|.
name|getHttpClient
argument_list|()
operator|.
name|execute
argument_list|(
name|httpPost
argument_list|)
operator|.
name|getEntity
argument_list|()
expr_stmt|;
try|try
block|{
name|response
operator|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Error in posting blob "
operator|+
name|getAsString
argument_list|(
name|m
argument_list|)
argument_list|,
name|m
operator|.
name|containsKey
argument_list|(
literal|"error"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSONParser
operator|.
name|ParseException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"$ERROR$"
argument_list|,
name|response
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|httpPost
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

