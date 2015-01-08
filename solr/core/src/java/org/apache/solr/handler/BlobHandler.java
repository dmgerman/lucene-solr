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

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
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
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|StorableField
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
name|index
operator|.
name|StoredDocument
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|Sort
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
name|search
operator|.
name|SortField
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|TopDocs
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
name|search
operator|.
name|TopFieldDocs
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
name|MapSolrParams
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
name|UpdateParams
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
name|PluginInfo
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
name|schema
operator|.
name|FieldType
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
name|search
operator|.
name|QParser
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
name|AddUpdateCommand
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
name|CommitUpdateCommand
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
name|processor
operator|.
name|UpdateRequestProcessor
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
name|processor
operator|.
name|UpdateRequestProcessorChain
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|PluginInfoInitialized
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
import|import static
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
operator|.
name|makeMap
import|;
end_import

begin_class
DECL|class|BlobHandler
specifier|public
class|class
name|BlobHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|PluginInfoInitialized
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BlobHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAX_SZ
specifier|private
specifier|static
specifier|final
name|long
name|MAX_SZ
init|=
literal|5
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|//2MB
DECL|field|maxSize
specifier|private
name|long
name|maxSize
init|=
name|MAX_SZ
decl_stmt|;
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
specifier|final
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|httpMethod
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"httpMethod"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|SolrConfigHandler
operator|.
name|setWt
argument_list|(
name|req
argument_list|,
literal|"json"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pieces
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|String
name|blobName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pieces
operator|.
name|size
argument_list|()
operator|>=
literal|3
condition|)
name|blobName
operator|=
name|pieces
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"POST"
operator|.
name|equals
argument_list|(
name|httpMethod
argument_list|)
condition|)
block|{
if|if
condition|(
name|blobName
operator|==
literal|null
operator|||
name|blobName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"Name not found"
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|err
init|=
name|SolrConfigHandler
operator|.
name|validateName
argument_list|(
name|blobName
argument_list|)
decl_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
name|err
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|req
operator|.
name|getContentStreams
argument_list|()
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"No stream"
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|ContentStream
name|stream
range|:
name|req
operator|.
name|getContentStreams
argument_list|()
control|)
block|{
name|ByteBuffer
name|payload
init|=
name|SimplePostTool
operator|.
name|inputStreamToByteArray
argument_list|(
name|stream
operator|.
name|getStream
argument_list|()
argument_list|,
name|maxSize
argument_list|)
decl_stmt|;
name|MessageDigest
name|m
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
decl_stmt|;
name|m
operator|.
name|update
argument_list|(
name|payload
operator|.
name|array
argument_list|()
argument_list|,
name|payload
operator|.
name|position
argument_list|()
argument_list|,
name|payload
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|md5
init|=
operator|new
name|BigInteger
argument_list|(
literal|1
argument_list|,
name|m
operator|.
name|digest
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|TopDocs
name|duplicate
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"md5"
argument_list|,
name|md5
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|duplicate
operator|.
name|totalHits
operator|>
literal|0
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"duplicate entry"
argument_list|)
expr_stmt|;
name|req
operator|.
name|forward
argument_list|(
literal|null
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
operator|(
name|Map
operator|)
name|makeMap
argument_list|(
literal|"q"
argument_list|,
literal|"md5:"
operator|+
name|md5
argument_list|,
literal|"fl"
argument_list|,
literal|"id,size,version,timestamp,blobName"
argument_list|)
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
name|TopFieldDocs
name|docs
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"blobName"
argument_list|,
name|blobName
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"version"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|version
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|docs
operator|.
name|totalHits
operator|>
literal|0
condition|)
block|{
name|StoredDocument
name|doc
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|doc
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Number
name|n
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"version"
argument_list|)
operator|.
name|numericValue
argument_list|()
decl_stmt|;
name|version
operator|=
name|n
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
name|version
operator|++
expr_stmt|;
name|String
name|id
init|=
name|blobName
operator|+
literal|"/"
operator|+
name|version
decl_stmt|;
name|indexMap
argument_list|(
name|req
argument_list|,
name|makeMap
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
literal|"md5"
argument_list|,
name|md5
argument_list|,
literal|"blobName"
argument_list|,
name|blobName
argument_list|,
literal|"version"
argument_list|,
name|version
argument_list|,
literal|"timestamp"
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
literal|"size"
argument_list|,
name|payload
operator|.
name|limit
argument_list|()
argument_list|,
literal|"blob"
argument_list|,
name|payload
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|int
name|version
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|pieces
operator|.
name|size
argument_list|()
operator|>
literal|3
condition|)
block|{
try|try
block|{
name|version
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|pieces
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"error"
argument_list|,
literal|"Invalid version"
operator|+
name|pieces
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|ReplicationHandler
operator|.
name|FILE_STREAM
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
name|CommonParams
operator|.
name|WT
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|blobName
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
name|NOT_FOUND
argument_list|,
literal|"Please send the request in the format /blob/<blobName>/<version>"
argument_list|)
throw|;
block|}
else|else
block|{
name|String
name|q
init|=
literal|"blobName:{0}"
decl_stmt|;
if|if
condition|(
name|version
operator|!=
operator|-
literal|1
condition|)
name|q
operator|=
literal|"id:{0}/{1}"
expr_stmt|;
name|QParser
name|qparser
init|=
name|QParser
operator|.
name|getParser
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|q
argument_list|,
name|blobName
argument_list|,
name|version
argument_list|)
argument_list|,
literal|"lucene"
argument_list|,
name|req
argument_list|)
decl_stmt|;
specifier|final
name|TopDocs
name|docs
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
name|qparser
operator|.
name|parse
argument_list|()
argument_list|,
literal|1
argument_list|,
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"version"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|docs
operator|.
name|totalHits
operator|>
literal|0
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
name|ReplicationHandler
operator|.
name|FILE_STREAM
argument_list|,
operator|new
name|SolrCore
operator|.
name|RawWriter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|StoredDocument
name|doc
init|=
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|doc
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|StorableField
name|sf
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"blob"
argument_list|)
decl_stmt|;
name|FieldType
name|fieldType
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
literal|"blob"
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buf
init|=
operator|(
name|ByteBuffer
operator|)
name|fieldType
operator|.
name|toObject
argument_list|(
name|sf
argument_list|)
decl_stmt|;
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
block|{
comment|//should never happen unless a user wrote this document directly
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
literal|"Invalid document . No field called blob"
argument_list|)
throw|;
block|}
else|else
block|{
name|os
operator|.
name|write
argument_list|(
name|buf
operator|.
name|array
argument_list|()
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|NOT_FOUND
argument_list|,
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"Invalid combination of blobName {0} and version {1}"
argument_list|,
name|blobName
argument_list|,
name|version
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|String
name|q
init|=
literal|"*:*"
decl_stmt|;
if|if
condition|(
name|blobName
operator|!=
literal|null
condition|)
block|{
name|q
operator|=
literal|"blobName:{0}"
expr_stmt|;
if|if
condition|(
name|version
operator|!=
operator|-
literal|1
condition|)
block|{
name|q
operator|=
literal|"id:{0}/{1}"
expr_stmt|;
block|}
block|}
name|req
operator|.
name|forward
argument_list|(
literal|null
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
operator|(
name|Map
operator|)
name|makeMap
argument_list|(
literal|"q"
argument_list|,
name|MessageFormat
operator|.
name|format
argument_list|(
name|q
argument_list|,
name|blobName
argument_list|,
name|version
argument_list|)
argument_list|,
literal|"fl"
argument_list|,
literal|"id,size,version,timestamp,blobName,md5"
argument_list|,
literal|"sort"
argument_list|,
literal|"version desc"
argument_list|)
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|indexMap
specifier|public
specifier|static
name|void
name|indexMap
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|SolrInputDocument
name|solrDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|doc
operator|.
name|entrySet
argument_list|()
control|)
name|solrDoc
operator|.
name|addField
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateRequestProcessorChain
name|processorChain
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getUpdateProcessingChain
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|UpdateParams
operator|.
name|UPDATE_CHAIN
argument_list|)
argument_list|)
decl_stmt|;
name|UpdateRequestProcessor
name|processor
init|=
name|processorChain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|solrDoc
operator|=
name|solrDoc
expr_stmt|;
name|processor
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
name|processorChain
operator|.
name|createProcessor
argument_list|(
name|req
argument_list|,
literal|null
argument_list|)
operator|.
name|processCommit
argument_list|(
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubHandler
specifier|public
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|subPath
parameter_list|)
block|{
if|if
condition|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|subPath
argument_list|,
literal|'/'
argument_list|)
operator|.
name|size
argument_list|()
operator|>
literal|4
condition|)
return|return
literal|null
return|;
return|return
name|this
return|;
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Load Jars into a system index"
return|;
block|}
DECL|field|SCHEMA
specifier|public
specifier|static
specifier|final
name|String
name|SCHEMA
init|=
literal|"<?xml version='1.0' ?>\n"
operator|+
literal|"<schema name='_system collection or core' version='1.1'>\n"
operator|+
literal|"<fieldtype name='string'  class='solr.StrField' sortMissingLast='true' omitNorms='true'/>\n"
operator|+
literal|"<fieldType name='long' class='solr.TrieLongField' precisionStep='0' positionIncrementGap='0'/>\n"
operator|+
literal|"<fieldType name='bytes' class='solr.BinaryField'/>\n"
operator|+
literal|"<fieldType name='date' class='solr.TrieDateField'/>\n"
operator|+
literal|"<field name='id'   type='string'   indexed='true'  stored='true'  multiValued='false' required='true'/>\n"
operator|+
literal|"<field name='md5'   type='string'   indexed='true'  stored='true'  multiValued='false' required='true'/>\n"
operator|+
literal|"<field name='blob'      type='bytes'   indexed='false' stored='true'  multiValued='false' />\n"
operator|+
literal|"<field name='size'      type='long'   indexed='true' stored='true'  multiValued='false' />\n"
operator|+
literal|"<field name='version'   type='long'     indexed='true'  stored='true'  multiValued='false' />\n"
operator|+
literal|"<field name='timestamp'   type='date'   indexed='true'  stored='true'  multiValued='false' />\n"
operator|+
literal|"<field name='blobName'      type='string'   indexed='true'  stored='true'  multiValued='false' />\n"
operator|+
literal|"<field name='_version_' type='long'     indexed='true'  stored='true'/>\n"
operator|+
literal|"<uniqueKey>id</uniqueKey>\n"
operator|+
literal|"</schema>"
decl_stmt|;
DECL|field|CONF
specifier|public
specifier|static
specifier|final
name|String
name|CONF
init|=
literal|"<?xml version='1.0' ?>\n"
operator|+
literal|"<config>\n"
operator|+
literal|"<luceneMatchVersion>LATEST</luceneMatchVersion>\n"
operator|+
literal|"<directoryFactory name='DirectoryFactory' class='${solr.directoryFactory:solr.StandardDirectoryFactory}'/>\n"
operator|+
literal|"<updateHandler class='solr.DirectUpdateHandler2'>\n"
operator|+
literal|"<updateLog>\n"
operator|+
literal|"<str name='dir'>${solr.ulog.dir:}</str>\n"
operator|+
literal|"</updateLog>\n"
operator|+
literal|"</updateHandler>\n"
operator|+
literal|"<requestHandler name='standard' class='solr.StandardRequestHandler' default='true' />\n"
operator|+
literal|"<requestHandler name='/analysis/field' startup='lazy' class='solr.FieldAnalysisRequestHandler' />\n"
operator|+
literal|"<requestHandler name='/blob' class='solr.BlobHandler'>\n"
operator|+
literal|"<lst name='invariants'>\n"
operator|+
literal|"<str name='maxSize'>${blob.max.size.mb:5}</str>\n"
operator|+
literal|"</lst>\n"
operator|+
literal|"</requestHandler>\n"
operator|+
literal|"</config>"
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|info
operator|.
name|initArgs
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|NamedList
name|invariants
init|=
operator|(
name|NamedList
operator|)
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|)
decl_stmt|;
if|if
condition|(
name|invariants
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|invariants
operator|.
name|get
argument_list|(
literal|"maxSize"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|maxSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|maxSize
operator|=
name|maxSize
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

