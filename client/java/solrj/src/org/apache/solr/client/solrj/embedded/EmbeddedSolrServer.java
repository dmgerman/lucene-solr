begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|StringReader
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
name|ResponseParser
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
name|SolrServer
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
name|BinaryResponseParser
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
name|XMLResponseParser
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
name|MultiCore
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
name|request
operator|.
name|QueryResponseWriter
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
name|servlet
operator|.
name|SolrRequestParsers
import|;
end_import

begin_comment
comment|/**  * SolrServer that connects directly to SolrCore  *   * TODO -- this implementation sends the response to XML and then parses it.    * It *should* be able to convert the response directly into a named list.  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|EmbeddedSolrServer
specifier|public
class|class
name|EmbeddedSolrServer
extends|extends
name|SolrServer
block|{
DECL|field|multicore
specifier|protected
specifier|final
name|MultiCore
name|multicore
decl_stmt|;
comment|// either multicore
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
comment|// or single core
DECL|field|coreName
specifier|protected
specifier|final
name|String
name|coreName
decl_stmt|;
comment|// use MultiCore registry
DECL|field|_parser
specifier|private
specifier|final
name|SolrRequestParsers
name|_parser
decl_stmt|;
DECL|method|EmbeddedSolrServer
specifier|public
name|EmbeddedSolrServer
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"SolrCore instance required"
argument_list|)
throw|;
block|}
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|multicore
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
literal|null
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
DECL|method|EmbeddedSolrServer
specifier|public
name|EmbeddedSolrServer
parameter_list|(
name|MultiCore
name|multicore
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
if|if
condition|(
name|multicore
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"MultiCore instance required"
argument_list|)
throw|;
block|}
name|this
operator|.
name|core
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|multicore
operator|=
name|multicore
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
name|SolrCore
name|c
init|=
name|multicore
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown core: "
operator|+
name|coreName
argument_list|)
throw|;
block|}
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
comment|// Check for multicore action
name|SolrCore
name|core
init|=
name|this
operator|.
name|core
decl_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|core
operator|=
name|multicore
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
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
literal|"Unknown core: "
operator|+
name|coreName
argument_list|)
throw|;
block|}
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
operator|&&
name|multicore
operator|!=
literal|null
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|multicore
operator|.
name|getAdminPath
argument_list|()
argument_list|)
operator|&&
name|multicore
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|handler
operator|=
name|multicore
operator|.
name|getMultiCoreHandler
argument_list|()
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
try|try
block|{
name|SolrQueryRequest
name|req
init|=
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
decl_stmt|;
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
comment|// Now write it out
name|NamedList
argument_list|<
name|Object
argument_list|>
name|normalized
init|=
name|getParsedResponse
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
decl_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|normalized
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
block|}
comment|/**    * TODO -- in the future, this could perhaps transform the NamedList without serializing it    * then parsing it from the serialized form.    *     * @param req    * @param rsp    * @return a response object equivalent to what you get from the XML/JSON/javabin parser. Documents    * become SolrDocuments, DocList becomes SolrDocumentList etc.    */
DECL|method|getParsedResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getParsedResponse
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
try|try
block|{
name|BinaryResponseWriter
name|writer
init|=
operator|new
name|BinaryResponseWriter
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|bos
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|BinaryResponseParser
name|parser
init|=
operator|new
name|BinaryResponseParser
argument_list|()
decl_stmt|;
return|return
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
literal|"UTF-8"
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
block|}
end_class

end_unit

