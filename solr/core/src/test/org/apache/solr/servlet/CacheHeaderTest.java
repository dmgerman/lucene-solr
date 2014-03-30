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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Date
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
name|Header
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
name|impl
operator|.
name|cookie
operator|.
name|DateUtils
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
name|TestUtil
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
name|CommonParams
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_comment
comment|/**  * A test case for the several HTTP cache headers emitted by Solr  */
end_comment

begin_class
DECL|class|CacheHeaderTest
specifier|public
class|class
name|CacheHeaderTest
extends|extends
name|CacheHeaderTestBase
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
name|solrHomeDirectory
operator|=
name|createTempDir
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
DECL|field|CONTENTS
specifier|protected
specifier|static
specifier|final
name|String
name|CONTENTS
init|=
literal|"id\n100\n101\n102"
decl_stmt|;
annotation|@
name|Test
DECL|method|testCacheVetoHandler
specifier|public
name|void
name|testCacheVetoHandler
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|f
init|=
name|makeFile
argument_list|(
name|CONTENTS
argument_list|)
decl_stmt|;
name|HttpRequestBase
name|m
init|=
name|getUpdateMethod
argument_list|(
literal|"GET"
argument_list|,
name|CommonParams
operator|.
name|STREAM_FILE
argument_list|,
name|f
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|CommonParams
operator|.
name|STREAM_CONTENTTYPE
argument_list|,
literal|"text/csv"
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|checkVetoHeaders
argument_list|(
name|response
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCacheVetoException
specifier|public
name|void
name|testCacheVetoException
parameter_list|()
throws|throws
name|Exception
block|{
name|HttpRequestBase
name|m
init|=
name|getSelectMethod
argument_list|(
literal|"GET"
argument_list|,
literal|"q"
argument_list|,
literal|"xyz_ignore_exception:solr"
argument_list|,
literal|"qt"
argument_list|,
literal|"standard"
argument_list|)
decl_stmt|;
comment|// We force an exception from Solr. This should emit "no-cache" HTTP headers
name|HttpResponse
name|response
init|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|200
argument_list|)
expr_stmt|;
name|checkVetoHeaders
argument_list|(
name|response
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|checkVetoHeaders
specifier|protected
name|void
name|checkVetoHeaders
parameter_list|(
name|HttpResponse
name|response
parameter_list|,
name|boolean
name|checkExpires
parameter_list|)
throws|throws
name|Exception
block|{
name|Header
name|head
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Cache-Control"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no Cache-Control header"
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"We got no no-cache in the Cache-Control header ["
operator|+
name|head
operator|+
literal|"]"
argument_list|,
name|head
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
literal|"no-cache"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"We got no no-store in the Cache-Control header ["
operator|+
name|head
operator|+
literal|"]"
argument_list|,
name|head
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
literal|"no-store"
argument_list|)
argument_list|)
expr_stmt|;
name|head
operator|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Pragma"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no Pragma header"
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no-cache"
argument_list|,
name|head
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkExpires
condition|)
block|{
name|head
operator|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Expires"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no Expires header:"
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|response
operator|.
name|getAllHeaders
argument_list|()
argument_list|)
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|Date
name|d
init|=
name|DateUtils
operator|.
name|parseDate
argument_list|(
name|head
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"We got no Expires header far in the past"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|d
operator|.
name|getTime
argument_list|()
operator|>
literal|100000
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doLastModified
specifier|protected
name|void
name|doLastModified
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|Exception
block|{
comment|// We do a first request to get the last modified
comment|// This must result in a 200 OK response
name|HttpRequestBase
name|get
init|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
decl_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got no response code 200 in initial request"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Header
name|head
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Last-Modified"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no Last-Modified header"
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|Date
name|lastModified
init|=
name|DateUtils
operator|.
name|parseDate
argument_list|(
name|head
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// If-Modified-Since tests
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Modified-Since"
argument_list|,
name|DateUtils
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected 304 NotModified response with current date"
argument_list|,
literal|304
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Modified-Since"
argument_list|,
name|DateUtils
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
name|lastModified
operator|.
name|getTime
argument_list|()
operator|-
literal|10000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected 200 OK response with If-Modified-Since in the past"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// If-Unmodified-Since tests
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Unmodified-Since"
argument_list|,
name|DateUtils
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
name|lastModified
operator|.
name|getTime
argument_list|()
operator|-
literal|10000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected 412 Precondition failed with If-Unmodified-Since in the past"
argument_list|,
literal|412
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Unmodified-Since"
argument_list|,
name|DateUtils
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected 200 OK response with If-Unmodified-Since and current date"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test ETag
annotation|@
name|Override
DECL|method|doETag
specifier|protected
name|void
name|doETag
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpRequestBase
name|get
init|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
decl_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got no response code 200 in initial request"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Header
name|head
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"ETag"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no ETag in the response"
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not a valid ETag"
argument_list|,
name|head
operator|.
name|getValue
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
operator|&&
name|head
operator|.
name|getValue
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|etag
init|=
name|head
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// If-None-Match tests
comment|// we set a non matching ETag
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-None-Match"
argument_list|,
literal|"\"xyz123456\""
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If-None-Match: Got no response code 200 in response to non matching ETag"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we set matching ETags
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-None-Match"
argument_list|,
literal|"\"xyz1223\""
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-None-Match"
argument_list|,
literal|"\"1231323423\", \"1211211\",   "
operator|+
name|etag
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If-None-Match: Got no response 304 to matching ETag"
argument_list|,
literal|304
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// we now set the special star ETag
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-None-Match"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If-None-Match: Got no response 304 for star ETag"
argument_list|,
literal|304
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// If-Match tests
comment|// we set a non matching ETag
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Match"
argument_list|,
literal|"\"xyz123456\""
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If-Match: Got no response code 412 in response to non matching ETag"
argument_list|,
literal|412
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we set matching ETags
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Match"
argument_list|,
literal|"\"xyz1223\""
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Match"
argument_list|,
literal|"\"1231323423\", \"1211211\",   "
operator|+
name|etag
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If-Match: Got no response 200 to matching ETag"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
comment|// now we set the special star ETag
name|get
operator|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|get
operator|.
name|addHeader
argument_list|(
literal|"If-Match"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|response
operator|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|get
argument_list|)
expr_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"If-Match: Got no response 200 to star ETag"
argument_list|,
literal|200
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doCacheControl
specifier|protected
name|void
name|doCacheControl
parameter_list|(
name|String
name|method
parameter_list|)
throws|throws
name|Exception
block|{
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
name|HttpRequestBase
name|m
init|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|Header
name|head
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Cache-Control"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"We got a cache-control header in response to POST"
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|head
operator|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Expires"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"We got an Expires  header in response to POST"
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|HttpRequestBase
name|m
init|=
name|getSelectMethod
argument_list|(
name|method
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|checkResponseBody
argument_list|(
name|method
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|Header
name|head
init|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Cache-Control"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no cache-control header"
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|head
operator|=
name|response
operator|.
name|getFirstHeader
argument_list|(
literal|"Expires"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"We got no Expires header in response"
argument_list|,
name|head
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeFile
specifier|protected
name|File
name|makeFile
parameter_list|(
name|String
name|contents
parameter_list|)
block|{
return|return
name|makeFile
argument_list|(
name|contents
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|makeFile
specifier|protected
name|File
name|makeFile
parameter_list|(
name|String
name|contents
parameter_list|,
name|String
name|charset
parameter_list|)
block|{
try|try
block|{
name|File
name|f
init|=
name|TestUtil
operator|.
name|createTempFile
argument_list|(
literal|"cachetest_csv"
argument_list|,
literal|null
argument_list|,
name|initCoreDataDir
argument_list|)
decl_stmt|;
name|Writer
name|out
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|,
name|charset
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|f
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

