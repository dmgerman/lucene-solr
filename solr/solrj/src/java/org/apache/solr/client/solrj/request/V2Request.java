begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|response
operator|.
name|V2Response
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
name|common
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_class
DECL|class|V2Request
specifier|public
class|class
name|V2Request
extends|extends
name|SolrRequest
argument_list|<
name|V2Response
argument_list|>
block|{
comment|//only for debugging purposes
DECL|field|v2Calls
specifier|public
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|AtomicLong
argument_list|>
name|v2Calls
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|COLL_REQ_PATTERN
specifier|static
specifier|final
name|Pattern
name|COLL_REQ_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"/(c|collections)/[^/]+/(?!shards)"
argument_list|)
decl_stmt|;
DECL|field|payload
specifier|private
name|InputStream
name|payload
decl_stmt|;
DECL|field|solrParams
specifier|private
name|SolrParams
name|solrParams
decl_stmt|;
DECL|field|useBinary
specifier|public
specifier|final
name|boolean
name|useBinary
decl_stmt|;
DECL|method|V2Request
specifier|private
name|V2Request
parameter_list|(
name|METHOD
name|m
parameter_list|,
name|String
name|resource
parameter_list|,
name|boolean
name|useBinary
parameter_list|)
block|{
name|super
argument_list|(
name|m
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|this
operator|.
name|useBinary
operator|=
name|useBinary
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
return|return
name|solrParams
return|;
block|}
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|v2Calls
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
name|v2Calls
operator|.
name|get
argument_list|()
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|ContentStreamBase
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|payload
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|useBinary
condition|?
literal|"application/javabin"
else|:
literal|"application/json"
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|isPerCollectionRequest
specifier|public
name|boolean
name|isPerCollectionRequest
parameter_list|()
block|{
return|return
name|COLL_REQ_PATTERN
operator|.
name|matcher
argument_list|(
name|getPath
argument_list|()
argument_list|)
operator|.
name|find
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createResponse
specifier|protected
name|V2Response
name|createResponse
parameter_list|(
name|SolrClient
name|client
parameter_list|)
block|{
return|return
operator|new
name|V2Response
argument_list|()
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|resource
specifier|private
name|String
name|resource
decl_stmt|;
DECL|field|method
specifier|private
name|METHOD
name|method
init|=
name|METHOD
operator|.
name|GET
decl_stmt|;
DECL|field|payload
specifier|private
name|Object
name|payload
decl_stmt|;
DECL|field|params
specifier|private
name|SolrParams
name|params
decl_stmt|;
DECL|field|useBinary
specifier|private
name|boolean
name|useBinary
init|=
literal|false
decl_stmt|;
comment|/**      * Create a Builder object based on the provided resource.      * The default method is GET.      *      * @param resource resource of the request for example "/collections" or "/cores/core-name"      */
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
if|if
condition|(
operator|!
name|resource
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|resource
operator|=
literal|"/"
operator|+
name|resource
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
DECL|method|withMethod
specifier|public
name|Builder
name|withMethod
parameter_list|(
name|METHOD
name|m
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|m
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set payload for request.      * @param payload as UTF-8 String      * @return builder object      */
DECL|method|withPayload
specifier|public
name|Builder
name|withPayload
parameter_list|(
name|String
name|payload
parameter_list|)
block|{
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|payload
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|payload
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|withPayload
specifier|public
name|Builder
name|withPayload
parameter_list|(
name|Object
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withPayload
specifier|public
name|Builder
name|withPayload
parameter_list|(
name|InputStream
name|payload
parameter_list|)
block|{
name|this
operator|.
name|payload
operator|=
name|payload
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withParams
specifier|public
name|Builder
name|withParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|useBinary
specifier|public
name|Builder
name|useBinary
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|this
operator|.
name|useBinary
operator|=
name|flag
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|V2Request
name|build
parameter_list|()
block|{
try|try
block|{
name|V2Request
name|v2Request
init|=
operator|new
name|V2Request
argument_list|(
name|method
argument_list|,
name|resource
argument_list|,
name|useBinary
argument_list|)
decl_stmt|;
name|v2Request
operator|.
name|solrParams
operator|=
name|params
expr_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|payload
operator|instanceof
name|InputStream
condition|)
name|is
operator|=
operator|(
name|InputStream
operator|)
name|payload
expr_stmt|;
elseif|else
if|if
condition|(
name|useBinary
condition|)
name|is
operator|=
name|Utils
operator|.
name|toJavabin
argument_list|(
name|payload
argument_list|)
expr_stmt|;
else|else
name|is
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|v2Request
operator|.
name|payload
operator|=
name|is
expr_stmt|;
return|return
name|v2Request
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
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
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

