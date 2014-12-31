begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
package|;
end_package

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
name|HttpHead
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
name|client
operator|.
name|methods
operator|.
name|HttpRequestBase
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
name|utils
operator|.
name|URLEncodedUtils
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
name|message
operator|.
name|BasicNameValuePair
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
name|impl
operator|.
name|HttpSolrClient
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|ArrayList
import|;
end_import

begin_class
DECL|class|CacheHeaderTestBase
specifier|public
specifier|abstract
class|class
name|CacheHeaderTestBase
extends|extends
name|SolrJettyTestBase
block|{
DECL|method|getSelectMethod
specifier|protected
name|HttpRequestBase
name|getSelectMethod
parameter_list|(
name|String
name|method
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|URISyntaxException
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
name|HttpRequestBase
name|m
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|BasicNameValuePair
argument_list|>
name|qparams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|qparams
operator|.
name|add
argument_list|(
operator|new
name|BasicNameValuePair
argument_list|(
literal|"q"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
expr_stmt|;
name|qparams
operator|.
name|add
argument_list|(
operator|new
name|BasicNameValuePair
argument_list|(
literal|"qt"
argument_list|,
literal|"standard"
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|length
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|qparams
operator|.
name|add
argument_list|(
operator|new
name|BasicNameValuePair
argument_list|(
name|params
index|[
name|i
operator|*
literal|2
index|]
argument_list|,
name|params
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/select?"
operator|+
name|URLEncodedUtils
operator|.
name|format
argument_list|(
name|qparams
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"GET"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|m
operator|=
operator|new
name|HttpGet
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"HEAD"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|m
operator|=
operator|new
name|HttpHead
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"POST"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|m
operator|=
operator|new
name|HttpPost
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
DECL|method|getUpdateMethod
specifier|protected
name|HttpRequestBase
name|getUpdateMethod
parameter_list|(
name|String
name|method
parameter_list|,
name|String
modifier|...
name|params
parameter_list|)
throws|throws
name|URISyntaxException
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
name|HttpRequestBase
name|m
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|BasicNameValuePair
argument_list|>
name|qparams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|params
operator|.
name|length
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|qparams
operator|.
name|add
argument_list|(
operator|new
name|BasicNameValuePair
argument_list|(
name|params
index|[
name|i
operator|*
literal|2
index|]
argument_list|,
name|params
index|[
name|i
operator|*
literal|2
operator|+
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|client
operator|.
name|getBaseURL
argument_list|()
operator|+
literal|"/update?"
operator|+
name|URLEncodedUtils
operator|.
name|format
argument_list|(
name|qparams
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"GET"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|m
operator|=
operator|new
name|HttpGet
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"POST"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|m
operator|=
operator|new
name|HttpPost
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"HEAD"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|m
operator|=
operator|new
name|HttpHead
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
DECL|method|getClient
specifier|protected
name|HttpClient
name|getClient
parameter_list|()
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
return|return
name|client
operator|.
name|getHttpClient
argument_list|()
return|;
block|}
DECL|method|checkResponseBody
specifier|protected
name|void
name|checkResponseBody
parameter_list|(
name|String
name|method
parameter_list|,
name|HttpResponse
name|resp
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|responseBody
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|resp
operator|.
name|getEntity
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|responseBody
operator|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|resp
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"GET"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
case|case
literal|200
case|:
name|assertTrue
argument_list|(
literal|"Response body was empty for method "
operator|+
name|method
argument_list|,
name|responseBody
operator|!=
literal|null
operator|&&
name|responseBody
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
literal|304
case|:
name|assertTrue
argument_list|(
literal|"Response body was not empty for method "
operator|+
name|method
argument_list|,
name|responseBody
operator|==
literal|null
operator|||
name|responseBody
operator|.
name|length
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
literal|412
case|:
name|assertTrue
argument_list|(
literal|"Response body was not empty for method "
operator|+
name|method
argument_list|,
name|responseBody
operator|==
literal|null
operator|||
name|responseBody
operator|.
name|length
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
break|break;
default|default:
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|responseBody
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unknown request response"
argument_list|,
literal|0
argument_list|,
name|resp
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|"HEAD"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Response body was not empty for method "
operator|+
name|method
argument_list|,
name|responseBody
operator|==
literal|null
operator|||
name|responseBody
operator|.
name|length
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The tests
annotation|@
name|Test
DECL|method|testLastModified
specifier|public
name|void
name|testLastModified
parameter_list|()
throws|throws
name|Exception
block|{
name|doLastModified
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|doLastModified
argument_list|(
literal|"HEAD"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEtag
specifier|public
name|void
name|testEtag
parameter_list|()
throws|throws
name|Exception
block|{
name|doETag
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|doETag
argument_list|(
literal|"HEAD"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCacheControl
specifier|public
name|void
name|testCacheControl
parameter_list|()
throws|throws
name|Exception
block|{
name|doCacheControl
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|doCacheControl
argument_list|(
literal|"HEAD"
argument_list|)
expr_stmt|;
name|doCacheControl
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
block|}
DECL|method|doCacheControl
specifier|protected
specifier|abstract
name|void
name|doCacheControl
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|doETag
specifier|protected
specifier|abstract
name|void
name|doETag
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|doLastModified
specifier|protected
specifier|abstract
name|void
name|doLastModified
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

