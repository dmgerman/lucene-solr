begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|SolrTestCaseJ4
operator|.
name|SuppressSSL
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
name|SolrException
operator|.
name|ErrorCode
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
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

begin_comment
comment|/**  * See SOLR-2854.  */
end_comment

begin_class
annotation|@
name|SuppressSSL
comment|// does not yet work with ssl yet - uses raw java.net.URL API rather than HttpClient
DECL|class|TestRemoteStreaming
specifier|public
class|class
name|TestRemoteStreaming
extends|extends
name|SolrJettyTestBase
block|{
DECL|field|solrHomeDirectory
specifier|private
specifier|static
name|File
name|solrHomeDirectory
decl_stmt|;
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
comment|//this one has handleSelect=true which a test here needs
name|solrHomeDirectory
operator|=
name|createTempDir
argument_list|(
name|LuceneTestCase
operator|.
name|getTestClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
expr_stmt|;
name|setupJettyTestHome
argument_list|(
name|solrHomeDirectory
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
name|createJetty
argument_list|(
name|solrHomeDirectory
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterTest
specifier|public
specifier|static
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{    }
annotation|@
name|Before
DECL|method|doBefore
specifier|public
name|void
name|doBefore
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|//add document and commit, and ensure it's there
name|SolrClient
name|client
init|=
name|getSolrClient
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1234"
argument_list|)
expr_stmt|;
name|client
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|searchFindsIt
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMakeDeleteAllUrl
specifier|public
name|void
name|testMakeDeleteAllUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|getUrlForString
argument_list|(
name|makeDeleteAllUrl
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|searchFindsIt
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStreamUrl
specifier|public
name|void
name|testStreamUrl
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|String
name|streamUrl
init|=
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/select?q=*:*&fl=id&wt=csv"
decl_stmt|;
name|String
name|getUrl
init|=
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/debug/dump?wt=xml&stream.url="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|streamUrl
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|String
name|content
init|=
name|getUrlForString
argument_list|(
name|getUrl
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"1234"
argument_list|)
argument_list|)
expr_stmt|;
comment|//System.out.println(content);
block|}
DECL|method|getUrlForString
specifier|private
name|String
name|getUrlForString
parameter_list|(
name|String
name|getUrl
parameter_list|)
throws|throws
name|IOException
block|{
name|Object
name|obj
init|=
operator|new
name|URL
argument_list|(
name|getUrl
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|InputStream
condition|)
block|{
name|InputStream
name|inputStream
init|=
operator|(
name|InputStream
operator|)
name|obj
decl_stmt|;
try|try
block|{
name|StringWriter
name|strWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|strWriter
argument_list|)
expr_stmt|;
return|return
name|strWriter
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Do a select query with the stream.url. Solr should fail */
annotation|@
name|Test
DECL|method|testNoUrlAccess
specifier|public
name|void
name|testNoUrlAccess
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"*:*"
argument_list|)
expr_stmt|;
comment|//for anything
name|query
operator|.
name|add
argument_list|(
literal|"stream.url"
argument_list|,
name|makeDeleteAllUrl
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|se
parameter_list|)
block|{
name|assertSame
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|ErrorCode
operator|.
name|getErrorCode
argument_list|(
name|se
operator|.
name|code
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** SOLR-3161    * Technically stream.body isn't remote streaming, but there wasn't a better place for this test method. */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testQtUpdateFails
specifier|public
name|void
name|testQtUpdateFails
parameter_list|()
throws|throws
name|SolrServerException
block|{
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
literal|"*:*"
argument_list|)
expr_stmt|;
comment|//for anything
name|query
operator|.
name|add
argument_list|(
literal|"echoHandler"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//sneaky sneaky
name|query
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|,
literal|"/update"
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
literal|"stream.body"
argument_list|,
literal|"<delete><query>*:*</query></delete>"
argument_list|)
expr_stmt|;
name|QueryRequest
name|queryRequest
init|=
operator|new
name|QueryRequest
argument_list|(
name|query
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
comment|//don't let superclass substitute qt for the path
return|return
literal|"/select"
return|;
block|}
block|}
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|queryRequest
operator|.
name|process
argument_list|(
name|getSolrClient
argument_list|()
argument_list|)
decl_stmt|;
comment|//!! should *fail* above for security purposes
name|String
name|handler
init|=
operator|(
name|String
operator|)
name|rsp
operator|.
name|getHeader
argument_list|()
operator|.
name|get
argument_list|(
literal|"handler"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
comment|/** Compose a url that if you get it, it will delete all the data. */
DECL|method|makeDeleteAllUrl
specifier|private
name|String
name|makeDeleteAllUrl
parameter_list|()
throws|throws
name|UnsupportedEncodingException
block|{
name|HttpSolrClient
name|client
init|=
operator|(
name|HttpSolrClient
operator|)
name|getSolrClient
argument_list|()
decl_stmt|;
name|String
name|deleteQuery
init|=
literal|"<delete><query>*:*</query></delete>"
decl_stmt|;
return|return
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/update?commit=true&stream.body="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|deleteQuery
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|method|searchFindsIt
specifier|private
name|boolean
name|searchFindsIt
parameter_list|()
throws|throws
name|SolrServerException
block|{
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
literal|"id:1234"
argument_list|)
expr_stmt|;
name|QueryResponse
name|rsp
init|=
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
return|return
name|rsp
operator|.
name|getResults
argument_list|()
operator|.
name|getNumFound
argument_list|()
operator|!=
literal|0
return|;
block|}
block|}
end_class

end_unit

