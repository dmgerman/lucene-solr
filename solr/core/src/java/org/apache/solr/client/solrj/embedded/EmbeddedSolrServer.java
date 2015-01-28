begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
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
name|embedded
package|;
end_package

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
name|Strings
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
name|StreamingResponseCallback
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
name|SolrDocument
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
name|SolrDocumentList
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
name|ModifiableSolrParams
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
name|JavaBinCodec
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
name|core
operator|.
name|CoreContainer
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
name|request
operator|.
name|SolrRequestInfo
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
name|ResultContext
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
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrRequestParsers
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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

begin_comment
comment|/**  * SolrClient that connects directly to SolrCore.  *<p>  * TODO -- this implementation sends the response to XML and then parses it.    * It *should* be able to convert the response directly into a named list.  *   *  * @since solr 1.3  */
end_comment

begin_class
DECL|class|EmbeddedSolrServer
specifier|public
class|class
name|EmbeddedSolrServer
extends|extends
name|SolrClient
block|{
DECL|field|coreContainer
specifier|protected
specifier|final
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|coreName
specifier|protected
specifier|final
name|String
name|coreName
decl_stmt|;
DECL|field|_parser
specifier|private
specifier|final
name|SolrRequestParsers
name|_parser
decl_stmt|;
comment|/**    * Use the other constructor using a CoreContainer and a name.    */
DECL|method|EmbeddedSolrServer
specifier|public
name|EmbeddedSolrServer
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a SolrServer.    * @param coreContainer the core container    * @param coreName the core name    */
DECL|method|EmbeddedSolrServer
specifier|public
name|EmbeddedSolrServer
parameter_list|(
name|CoreContainer
name|coreContainer
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
if|if
condition|(
name|coreContainer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"CoreContainer instance required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|coreName
argument_list|)
condition|)
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
literal|"Core name cannot be empty"
argument_list|)
throw|;
name|this
operator|.
name|coreContainer
operator|=
name|coreContainer
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
name|_parser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|request
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|String
name|path
init|=
name|request
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
operator|||
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/select"
expr_stmt|;
block|}
comment|// Check for cores action
name|SolrCore
name|core
init|=
name|coreContainer
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|==
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
literal|"No such core: "
operator|+
name|coreName
argument_list|)
throw|;
block|}
name|SolrParams
name|params
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
block|}
comment|// Extract the handler from the path or params
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
if|if
condition|(
literal|"/select"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
literal|"/select/"
operator|.
name|equalsIgnoreCase
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|String
name|qt
init|=
name|params
operator|.
name|get
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|)
decl_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|qt
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|==
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
name|BAD_REQUEST
argument_list|,
literal|"unknown handler: "
operator|+
name|qt
argument_list|)
throw|;
block|}
block|}
comment|// Perhaps the path is to manage the cores
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|handler
operator|=
name|coreContainer
operator|.
name|getRequestHandler
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"unknown handler: "
operator|+
name|path
argument_list|)
throw|;
block|}
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
name|req
operator|=
name|_parser
operator|.
name|buildRequestFrom
argument_list|(
name|core
argument_list|,
name|params
argument_list|,
name|request
operator|.
name|getContentStreams
argument_list|()
argument_list|)
expr_stmt|;
name|req
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
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrRequestInfo
operator|.
name|setRequestInfo
argument_list|(
operator|new
name|SolrRequestInfo
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|core
operator|.
name|execute
argument_list|(
name|handler
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rsp
operator|.
name|getException
argument_list|()
operator|instanceof
name|SolrException
condition|)
block|{
throw|throw
name|rsp
operator|.
name|getException
argument_list|()
throw|;
block|}
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|rsp
operator|.
name|getException
argument_list|()
argument_list|)
throw|;
block|}
comment|// Check if this should stream results
if|if
condition|(
name|request
operator|.
name|getStreamingResponseCallback
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|StreamingResponseCallback
name|callback
init|=
name|request
operator|.
name|getStreamingResponseCallback
argument_list|()
decl_stmt|;
name|BinaryResponseWriter
operator|.
name|Resolver
name|resolver
init|=
operator|new
name|BinaryResponseWriter
operator|.
name|Resolver
argument_list|(
name|req
argument_list|,
name|rsp
operator|.
name|getReturnFields
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|writeResults
parameter_list|(
name|ResultContext
name|ctx
parameter_list|,
name|JavaBinCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
comment|// write an empty list...
name|SolrDocumentList
name|docs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|docs
operator|.
name|setNumFound
argument_list|(
name|ctx
operator|.
name|docs
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|.
name|setStart
argument_list|(
name|ctx
operator|.
name|docs
operator|.
name|offset
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|.
name|setMaxScore
argument_list|(
name|ctx
operator|.
name|docs
operator|.
name|maxScore
argument_list|()
argument_list|)
expr_stmt|;
name|codec
operator|.
name|writeSolrDocumentList
argument_list|(
name|docs
argument_list|)
expr_stmt|;
comment|// This will transform
name|writeResultsBody
argument_list|(
name|ctx
argument_list|,
name|codec
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|writeSolrDocument
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
block|{
name|callback
operator|.
name|streamSolrDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//super.writeSolrDocument( doc, fields );
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeSolrDocumentList
parameter_list|(
name|SolrDocumentList
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SolrDocumentList
name|tmp
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|tmp
operator|.
name|setMaxScore
argument_list|(
name|docs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setNumFound
argument_list|(
name|docs
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|setStart
argument_list|(
name|docs
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|docs
operator|=
name|tmp
expr_stmt|;
block|}
name|callback
operator|.
name|streamDocListInfo
argument_list|(
name|docs
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|docs
operator|.
name|getStart
argument_list|()
argument_list|,
name|docs
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|writeSolrDocumentList
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|marshal
argument_list|(
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
operator|new
name|JavaBinCodec
argument_list|(
name|resolver
argument_list|)
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|// Now write it out
name|NamedList
argument_list|<
name|Object
argument_list|>
name|normalized
init|=
name|BinaryResponseWriter
operator|.
name|getParsedResponse
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
return|return
name|normalized
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|SolrException
name|iox
parameter_list|)
block|{
throw|throw
name|iox
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
name|SolrRequestInfo
operator|.
name|clearRequestInfo
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Shutdown all cores within the EmbeddedSolrServer instance    */
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|coreContainer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Getter method for the CoreContainer    * @return the core container    */
DECL|method|getCoreContainer
specifier|public
name|CoreContainer
name|getCoreContainer
parameter_list|()
block|{
return|return
name|coreContainer
return|;
block|}
block|}
end_class

end_unit

