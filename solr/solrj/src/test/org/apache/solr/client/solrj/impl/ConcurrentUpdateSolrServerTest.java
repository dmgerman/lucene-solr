begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|Map
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
name|ExecutorService
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
name|Executors
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|solr
operator|.
name|SolrJettyTestBase
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
name|JavaBinUpdateRequestCodec
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
name|util
operator|.
name|SolrjNamedThreadFactory
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
DECL|class|ConcurrentUpdateSolrServerTest
specifier|public
class|class
name|ConcurrentUpdateSolrServerTest
extends|extends
name|SolrJettyTestBase
block|{
comment|/**    * Mock endpoint where the CUSS being tested in this class sends requests.    */
DECL|class|TestServlet
specifier|public
specifier|static
class|class
name|TestServlet
extends|extends
name|HttpServlet
implements|implements
name|JavaBinUpdateRequestCodec
operator|.
name|StreamingUpdateHandler
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|clear
specifier|public
specifier|static
name|void
name|clear
parameter_list|()
block|{
name|lastMethod
operator|=
literal|null
expr_stmt|;
name|headers
operator|=
literal|null
expr_stmt|;
name|parameters
operator|=
literal|null
expr_stmt|;
name|errorCode
operator|=
literal|null
expr_stmt|;
name|numReqsRcvd
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numDocsRcvd
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|field|errorCode
specifier|public
specifier|static
name|Integer
name|errorCode
init|=
literal|null
decl_stmt|;
DECL|field|lastMethod
specifier|public
specifier|static
name|String
name|lastMethod
init|=
literal|null
decl_stmt|;
DECL|field|headers
specifier|public
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
literal|null
decl_stmt|;
DECL|field|parameters
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|parameters
init|=
literal|null
decl_stmt|;
DECL|field|numReqsRcvd
specifier|public
specifier|static
name|AtomicInteger
name|numReqsRcvd
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|numDocsRcvd
specifier|public
specifier|static
name|AtomicInteger
name|numDocsRcvd
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|setErrorCode
specifier|public
specifier|static
name|void
name|setErrorCode
parameter_list|(
name|Integer
name|code
parameter_list|)
block|{
name|errorCode
operator|=
name|code
expr_stmt|;
block|}
DECL|method|setHeaders
specifier|private
name|void
name|setHeaders
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|Enumeration
argument_list|<
name|String
argument_list|>
name|headerNames
init|=
name|req
operator|.
name|getHeaderNames
argument_list|()
decl_stmt|;
name|headers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
while|while
condition|(
name|headerNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|headerNames
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|req
operator|.
name|getHeader
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setParameters
specifier|private
name|void
name|setParameters
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
comment|//parameters = req.getParameterMap();
block|}
annotation|@
name|Override
DECL|method|doPost
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|numReqsRcvd
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|lastMethod
operator|=
literal|"post"
expr_stmt|;
name|recordRequest
argument_list|(
name|req
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|InputStream
name|reqIn
init|=
name|req
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|JavaBinUpdateRequestCodec
name|javabin
init|=
operator|new
name|JavaBinUpdateRequestCodec
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
name|javabin
operator|.
name|unmarshal
argument_list|(
name|reqIn
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
break|break;
comment|// this is expected
block|}
block|}
block|}
DECL|method|recordRequest
specifier|private
name|void
name|recordRequest
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|resp
parameter_list|)
block|{
name|setHeaders
argument_list|(
name|req
argument_list|)
expr_stmt|;
name|setParameters
argument_list|(
name|req
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|errorCode
condition|)
block|{
try|try
block|{
name|resp
operator|.
name|sendError
argument_list|(
name|errorCode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"sendError IO fail in TestServlet"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|,
name|UpdateRequest
name|req
parameter_list|,
name|Integer
name|commitWithin
parameter_list|,
name|Boolean
name|override
parameter_list|)
block|{
name|numDocsRcvd
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
comment|// end TestServlet
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|createJetty
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|jetty
operator|.
name|getDispatchFilter
argument_list|()
operator|.
name|getServletHandler
argument_list|()
operator|.
name|addServletWithMapping
argument_list|(
name|TestServlet
operator|.
name|class
argument_list|,
literal|"/cuss/*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConcurrentUpdate
specifier|public
name|void
name|testConcurrentUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|TestServlet
operator|.
name|clear
argument_list|()
expr_stmt|;
name|String
name|serverUrl
init|=
name|jetty
operator|.
name|getBaseUrl
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"/cuss/foo"
decl_stmt|;
name|int
name|cussThreadCount
init|=
literal|2
decl_stmt|;
name|int
name|cussQueueSize
init|=
literal|100
decl_stmt|;
comment|// for tracking callbacks from CUSS
specifier|final
name|AtomicInteger
name|successCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicInteger
name|errorCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|errors
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
name|ConcurrentUpdateSolrServer
name|cuss
init|=
operator|new
name|ConcurrentUpdateSolrServer
argument_list|(
name|serverUrl
argument_list|,
name|cussQueueSize
argument_list|,
name|cussThreadCount
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleError
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|errorCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|errors
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|ex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|HttpResponse
name|resp
parameter_list|)
block|{
name|successCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|cuss
operator|.
name|setParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
expr_stmt|;
name|cuss
operator|.
name|setRequestWriter
argument_list|(
operator|new
name|BinaryRequestWriter
argument_list|()
argument_list|)
expr_stmt|;
name|cuss
operator|.
name|setPollQueueTime
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// ensure it doesn't block where there's nothing to do yet
name|cuss
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|int
name|poolSize
init|=
literal|5
decl_stmt|;
name|ExecutorService
name|threadPool
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|poolSize
argument_list|,
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"testCUSS"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
literal|100
decl_stmt|;
name|int
name|numRunnables
init|=
literal|5
decl_stmt|;
for|for
control|(
name|int
name|r
init|=
literal|0
init|;
name|r
operator|<
name|numRunnables
condition|;
name|r
operator|++
control|)
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|SendDocsRunnable
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|r
argument_list|)
argument_list|,
name|numDocs
argument_list|,
name|cuss
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure all docs are sent
name|threadPool
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// wait until all requests are processed by CUSS
name|cuss
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
name|cuss
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"post"
argument_list|,
name|TestServlet
operator|.
name|lastMethod
argument_list|)
expr_stmt|;
comment|// expect all requests to be successful
name|int
name|expectedSuccesses
init|=
name|TestServlet
operator|.
name|numReqsRcvd
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|expectedSuccesses
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// at least one request must have been sent
name|assertTrue
argument_list|(
literal|"Expected no errors but got "
operator|+
name|errorCounter
operator|.
name|get
argument_list|()
operator|+
literal|", due to: "
operator|+
name|errors
operator|.
name|toString
argument_list|()
argument_list|,
name|errorCounter
operator|.
name|get
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected "
operator|+
name|expectedSuccesses
operator|+
literal|" successes, but got "
operator|+
name|successCounter
operator|.
name|get
argument_list|()
argument_list|,
name|successCounter
operator|.
name|get
argument_list|()
operator|==
name|expectedSuccesses
argument_list|)
expr_stmt|;
name|int
name|expectedDocs
init|=
name|numDocs
operator|*
name|numRunnables
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Expected CUSS to send "
operator|+
name|expectedDocs
operator|+
literal|" but got "
operator|+
name|TestServlet
operator|.
name|numDocsRcvd
operator|.
name|get
argument_list|()
argument_list|,
name|TestServlet
operator|.
name|numDocsRcvd
operator|.
name|get
argument_list|()
operator|==
name|expectedDocs
argument_list|)
expr_stmt|;
block|}
DECL|class|SendDocsRunnable
class|class
name|SendDocsRunnable
implements|implements
name|Runnable
block|{
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|cuss
specifier|private
name|ConcurrentUpdateSolrServer
name|cuss
decl_stmt|;
DECL|method|SendDocsRunnable
name|SendDocsRunnable
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|ConcurrentUpdateSolrServer
name|cuss
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
name|this
operator|.
name|cuss
operator|=
name|cuss
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|numDocs
condition|;
name|d
operator|++
control|)
block|{
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
name|id
operator|+
literal|"_"
operator|+
name|d
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
name|docId
argument_list|)
expr_stmt|;
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
try|try
block|{
name|cuss
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

