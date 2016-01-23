begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|URLDecoder
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|FileItem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|disk
operator|.
name|DiskFileItemFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|fileupload
operator|.
name|servlet
operator|.
name|ServletFileUpload
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
name|params
operator|.
name|CommonParams
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
name|MultiMapSolrParams
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
name|SolrParams
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
name|ContentStream
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
name|ContentStreamBase
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
name|Config
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
name|SolrCore
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
name|ServletSolrParams
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
name|request
operator|.
name|SolrQueryRequestBase
import|;
end_import

begin_class
DECL|class|SolrRequestParsers
specifier|public
class|class
name|SolrRequestParsers
block|{
DECL|field|log
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrRequestParsers
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Should these constants be in a more public place?
DECL|field|MULTIPART
specifier|public
specifier|static
specifier|final
name|String
name|MULTIPART
init|=
literal|"multipart"
decl_stmt|;
DECL|field|RAW
specifier|public
specifier|static
specifier|final
name|String
name|RAW
init|=
literal|"raw"
decl_stmt|;
DECL|field|SIMPLE
specifier|public
specifier|static
specifier|final
name|String
name|SIMPLE
init|=
literal|"simple"
decl_stmt|;
DECL|field|STANDARD
specifier|public
specifier|static
specifier|final
name|String
name|STANDARD
init|=
literal|"standard"
decl_stmt|;
DECL|field|parsers
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrRequestParser
argument_list|>
name|parsers
decl_stmt|;
DECL|field|enableRemoteStreams
specifier|private
name|boolean
name|enableRemoteStreams
init|=
literal|false
decl_stmt|;
DECL|field|handleSelect
specifier|private
name|boolean
name|handleSelect
init|=
literal|true
decl_stmt|;
DECL|field|standard
specifier|private
name|StandardRequestParser
name|standard
decl_stmt|;
comment|/**    * Pass in an xml configuration.  A null configuration will enable    * everythign with maximum values.    */
DECL|method|SolrRequestParsers
specifier|public
name|SolrRequestParsers
parameter_list|(
name|Config
name|globalConfig
parameter_list|)
block|{
name|long
name|uploadLimitKB
init|=
literal|1048
decl_stmt|;
comment|// 2MB default
if|if
condition|(
name|globalConfig
operator|==
literal|null
condition|)
block|{
name|uploadLimitKB
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|enableRemoteStreams
operator|=
literal|true
expr_stmt|;
name|handleSelect
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|uploadLimitKB
operator|=
name|globalConfig
operator|.
name|getInt
argument_list|(
literal|"requestDispatcher/requestParsers/@multipartUploadLimitInKB"
argument_list|,
operator|(
name|int
operator|)
name|uploadLimitKB
argument_list|)
expr_stmt|;
name|enableRemoteStreams
operator|=
name|globalConfig
operator|.
name|getBool
argument_list|(
literal|"requestDispatcher/requestParsers/@enableRemoteStreaming"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Let this filter take care of /select?xxx format
name|handleSelect
operator|=
name|globalConfig
operator|.
name|getBool
argument_list|(
literal|"requestDispatcher/@handleSelect"
argument_list|,
name|handleSelect
argument_list|)
expr_stmt|;
block|}
name|MultipartRequestParser
name|multi
init|=
operator|new
name|MultipartRequestParser
argument_list|(
name|uploadLimitKB
argument_list|)
decl_stmt|;
name|RawRequestParser
name|raw
init|=
operator|new
name|RawRequestParser
argument_list|()
decl_stmt|;
name|standard
operator|=
operator|new
name|StandardRequestParser
argument_list|(
name|multi
argument_list|,
name|raw
argument_list|)
expr_stmt|;
comment|// I don't see a need to have this publicly configured just yet
comment|// adding it is trivial
name|parsers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrRequestParser
argument_list|>
argument_list|()
expr_stmt|;
name|parsers
operator|.
name|put
argument_list|(
name|MULTIPART
argument_list|,
name|multi
argument_list|)
expr_stmt|;
name|parsers
operator|.
name|put
argument_list|(
name|RAW
argument_list|,
name|raw
argument_list|)
expr_stmt|;
name|parsers
operator|.
name|put
argument_list|(
name|SIMPLE
argument_list|,
operator|new
name|SimpleRequestParser
argument_list|()
argument_list|)
expr_stmt|;
name|parsers
operator|.
name|put
argument_list|(
name|STANDARD
argument_list|,
name|standard
argument_list|)
expr_stmt|;
name|parsers
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|standard
argument_list|)
expr_stmt|;
block|}
DECL|method|parse
specifier|public
name|SolrQueryRequest
name|parse
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|path
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrRequestParser
name|parser
init|=
name|standard
decl_stmt|;
comment|// TODO -- in the future, we could pick a different parser based on the request
comment|// Pick the parser from the request...
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SolrParams
name|params
init|=
name|parser
operator|.
name|parseParamsAndFillStreams
argument_list|(
name|req
argument_list|,
name|streams
argument_list|)
decl_stmt|;
name|SolrQueryRequest
name|sreq
init|=
name|buildRequestFrom
argument_list|(
name|core
argument_list|,
name|params
argument_list|,
name|streams
argument_list|)
decl_stmt|;
comment|// Handlers and login will want to know the path. If it contains a ':'
comment|// the handler could use it for RESTful URLs
name|sreq
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return
name|sreq
return|;
block|}
DECL|method|buildRequestFrom
specifier|public
name|SolrQueryRequest
name|buildRequestFrom
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The content type will be applied to all streaming content
name|String
name|contentType
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|STREAM_CONTENTTYPE
argument_list|)
decl_stmt|;
comment|// Handle anything with a remoteURL
name|String
index|[]
name|strs
init|=
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|STREAM_URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|strs
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|enableRemoteStreams
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
name|BAD_REQUEST
argument_list|,
literal|"Remote Streaming is disabled."
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|String
name|url
range|:
name|strs
control|)
block|{
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|URLStream
argument_list|(
operator|new
name|URL
argument_list|(
name|url
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
name|streams
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Handle streaming files
name|strs
operator|=
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|STREAM_FILE
argument_list|)
expr_stmt|;
if|if
condition|(
name|strs
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|enableRemoteStreams
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
name|BAD_REQUEST
argument_list|,
literal|"Remote Streaming is disabled."
argument_list|)
throw|;
block|}
for|for
control|(
specifier|final
name|String
name|file
range|:
name|strs
control|)
block|{
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|FileStream
argument_list|(
operator|new
name|File
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
name|streams
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Check for streams in the request parameters
name|strs
operator|=
name|params
operator|.
name|getParams
argument_list|(
name|CommonParams
operator|.
name|STREAM_BODY
argument_list|)
expr_stmt|;
if|if
condition|(
name|strs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|String
name|body
range|:
name|strs
control|)
block|{
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|body
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|stream
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
name|streams
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
name|SolrQueryRequestBase
name|q
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{ }
decl_stmt|;
if|if
condition|(
name|streams
operator|!=
literal|null
operator|&&
name|streams
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|q
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
comment|/**    * Given a standard query string map it into solr params    */
DECL|method|parseQueryString
specifier|public
specifier|static
name|MultiMapSolrParams
name|parseQueryString
parameter_list|(
name|String
name|queryString
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
operator|&&
name|queryString
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
for|for
control|(
name|String
name|kv
range|:
name|queryString
operator|.
name|split
argument_list|(
literal|"&"
argument_list|)
control|)
block|{
name|int
name|idx
init|=
name|kv
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|String
name|name
init|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|kv
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|kv
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|MultiMapSolrParams
operator|.
name|addParam
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|name
init|=
name|URLDecoder
operator|.
name|decode
argument_list|(
name|kv
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|MultiMapSolrParams
operator|.
name|addParam
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uex
parameter_list|)
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
name|uex
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|MultiMapSolrParams
argument_list|(
name|map
argument_list|)
return|;
block|}
DECL|method|isHandleSelect
specifier|public
name|boolean
name|isHandleSelect
parameter_list|()
block|{
return|return
name|handleSelect
return|;
block|}
DECL|method|setHandleSelect
specifier|public
name|void
name|setHandleSelect
parameter_list|(
name|boolean
name|handleSelect
parameter_list|)
block|{
name|this
operator|.
name|handleSelect
operator|=
name|handleSelect
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|//-----------------------------------------------------------------
end_comment

begin_comment
comment|//-----------------------------------------------------------------
end_comment

begin_comment
comment|// I guess we don't really even need the interface, but i'll keep it here just for kicks
end_comment

begin_interface
DECL|interface|SolrRequestParser
interface|interface
name|SolrRequestParser
block|{
DECL|method|parseParamsAndFillStreams
specifier|public
name|SolrParams
name|parseParamsAndFillStreams
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

begin_comment
comment|//-----------------------------------------------------------------
end_comment

begin_comment
comment|//-----------------------------------------------------------------
end_comment

begin_comment
comment|/**  * The simple parser just uses the params directly  */
end_comment

begin_class
DECL|class|SimpleRequestParser
class|class
name|SimpleRequestParser
implements|implements
name|SolrRequestParser
block|{
DECL|method|parseParamsAndFillStreams
specifier|public
name|SolrParams
name|parseParamsAndFillStreams
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|ServletSolrParams
argument_list|(
name|req
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Wrap an HttpServletRequest as a ContentStream  */
end_comment

begin_class
DECL|class|HttpRequestContentStream
class|class
name|HttpRequestContentStream
extends|extends
name|ContentStreamBase
block|{
DECL|field|req
specifier|private
specifier|final
name|HttpServletRequest
name|req
decl_stmt|;
DECL|method|HttpRequestContentStream
specifier|public
name|HttpRequestContentStream
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|req
operator|=
name|req
expr_stmt|;
name|contentType
operator|=
name|req
operator|.
name|getContentType
argument_list|()
expr_stmt|;
comment|// name = ???
comment|// sourceInfo = ???
name|String
name|v
init|=
name|req
operator|.
name|getHeader
argument_list|(
literal|"Content-Length"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|size
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getStream
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|req
operator|.
name|getInputStream
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Wrap a FileItem as a ContentStream  */
end_comment

begin_class
DECL|class|FileItemContentStream
class|class
name|FileItemContentStream
extends|extends
name|ContentStreamBase
block|{
DECL|field|item
specifier|private
specifier|final
name|FileItem
name|item
decl_stmt|;
DECL|method|FileItemContentStream
specifier|public
name|FileItemContentStream
parameter_list|(
name|FileItem
name|f
parameter_list|)
block|{
name|item
operator|=
name|f
expr_stmt|;
name|contentType
operator|=
name|item
operator|.
name|getContentType
argument_list|()
expr_stmt|;
name|name
operator|=
name|item
operator|.
name|getName
argument_list|()
expr_stmt|;
name|sourceInfo
operator|=
name|item
operator|.
name|getFieldName
argument_list|()
expr_stmt|;
name|size
operator|=
name|item
operator|.
name|getSize
argument_list|()
expr_stmt|;
block|}
DECL|method|getStream
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|item
operator|.
name|getInputStream
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * The raw parser just uses the params directly  */
end_comment

begin_class
DECL|class|RawRequestParser
class|class
name|RawRequestParser
implements|implements
name|SolrRequestParser
block|{
DECL|method|parseParamsAndFillStreams
specifier|public
name|SolrParams
name|parseParamsAndFillStreams
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The javadocs for HttpServletRequest are clear that req.getReader() should take
comment|// care of any character encoding issues.  BUT, there are problems while running on
comment|// some servlet containers: including Tomcat 5 and resin.
comment|//
comment|// Rather than return req.getReader(), this uses the default ContentStreamBase method
comment|// that checks for charset definitions in the ContentType.
name|streams
operator|.
name|add
argument_list|(
operator|new
name|HttpRequestContentStream
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|SolrRequestParsers
operator|.
name|parseQueryString
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Extract Multipart streams  */
end_comment

begin_class
DECL|class|MultipartRequestParser
class|class
name|MultipartRequestParser
implements|implements
name|SolrRequestParser
block|{
DECL|field|uploadLimitKB
specifier|private
name|long
name|uploadLimitKB
decl_stmt|;
DECL|method|MultipartRequestParser
specifier|public
name|MultipartRequestParser
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|uploadLimitKB
operator|=
name|limit
expr_stmt|;
block|}
DECL|method|parseParamsAndFillStreams
specifier|public
name|SolrParams
name|parseParamsAndFillStreams
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|ServletFileUpload
operator|.
name|isMultipartContent
argument_list|(
name|req
argument_list|)
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
name|BAD_REQUEST
argument_list|,
literal|"Not multipart content! "
operator|+
name|req
operator|.
name|getContentType
argument_list|()
argument_list|)
throw|;
block|}
name|MultiMapSolrParams
name|params
init|=
name|SolrRequestParsers
operator|.
name|parseQueryString
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Create a factory for disk-based file items
name|DiskFileItemFactory
name|factory
init|=
operator|new
name|DiskFileItemFactory
argument_list|()
decl_stmt|;
comment|// Set factory constraints
comment|// TODO - configure factory.setSizeThreshold(yourMaxMemorySize);
comment|// TODO - configure factory.setRepository(yourTempDirectory);
comment|// Create a new file upload handler
name|ServletFileUpload
name|upload
init|=
operator|new
name|ServletFileUpload
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|upload
operator|.
name|setSizeMax
argument_list|(
name|uploadLimitKB
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// Parse the request
name|List
name|items
init|=
name|upload
operator|.
name|parseRequest
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|Iterator
name|iter
init|=
name|items
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|FileItem
name|item
init|=
operator|(
name|FileItem
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// If its a form field, put it in our parameter map
if|if
condition|(
name|item
operator|.
name|isFormField
argument_list|()
condition|)
block|{
name|MultiMapSolrParams
operator|.
name|addParam
argument_list|(
name|item
operator|.
name|getFieldName
argument_list|()
argument_list|,
name|item
operator|.
name|getString
argument_list|()
argument_list|,
name|params
operator|.
name|getMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Only add it if it actually has something...
elseif|else
if|if
condition|(
name|item
operator|.
name|getSize
argument_list|()
operator|>
literal|0
condition|)
block|{
name|streams
operator|.
name|add
argument_list|(
operator|new
name|FileItemContentStream
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|params
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * The default Logic  */
end_comment

begin_class
DECL|class|StandardRequestParser
class|class
name|StandardRequestParser
implements|implements
name|SolrRequestParser
block|{
DECL|field|multipart
name|MultipartRequestParser
name|multipart
decl_stmt|;
DECL|field|raw
name|RawRequestParser
name|raw
decl_stmt|;
DECL|method|StandardRequestParser
name|StandardRequestParser
parameter_list|(
name|MultipartRequestParser
name|multi
parameter_list|,
name|RawRequestParser
name|raw
parameter_list|)
block|{
name|this
operator|.
name|multipart
operator|=
name|multi
expr_stmt|;
name|this
operator|.
name|raw
operator|=
name|raw
expr_stmt|;
block|}
DECL|method|parseParamsAndFillStreams
specifier|public
name|SolrParams
name|parseParamsAndFillStreams
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|method
init|=
name|req
operator|.
name|getMethod
argument_list|()
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"GET"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
operator|||
literal|"HEAD"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
return|return
operator|new
name|ServletSolrParams
argument_list|(
name|req
argument_list|)
return|;
block|}
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
name|String
name|contentType
init|=
name|req
operator|.
name|getContentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
name|int
name|idx
init|=
name|contentType
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
comment|// remove the charset definition "; charset=utf-8"
name|contentType
operator|=
name|contentType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"application/x-www-form-urlencoded"
operator|.
name|equals
argument_list|(
name|contentType
operator|.
name|toLowerCase
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|ServletSolrParams
argument_list|(
name|req
argument_list|)
return|;
comment|// just get the params from parameterMap
block|}
if|if
condition|(
name|ServletFileUpload
operator|.
name|isMultipartContent
argument_list|(
name|req
argument_list|)
condition|)
block|{
return|return
name|multipart
operator|.
name|parseParamsAndFillStreams
argument_list|(
name|req
argument_list|,
name|streams
argument_list|)
return|;
block|}
block|}
return|return
name|raw
operator|.
name|parseParamsAndFillStreams
argument_list|(
name|req
argument_list|,
name|streams
argument_list|)
return|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Unsupported method: "
operator|+
name|method
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

