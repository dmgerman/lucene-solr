begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|api
operator|.
name|ApiBag
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
name|util
operator|.
name|ValidatingJsonMap
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
name|SuppressForbidden
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
name|SolrIndexSearcher
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
name|CommandOperation
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
name|JsonSchemaValidator
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
name|RTimerTree
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
name|RefCounted
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
name|IndexSchema
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
name|core
operator|.
name|SolrCore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|HashMap
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

begin_comment
comment|/**  * Base implementation of<code>SolrQueryRequest</code> that provides some  * convenience methods for accessing parameters, and manages an IndexSearcher  * reference.  *  *<p>  * The<code>close()</code> method must be called on any instance of this  * class once it is no longer in use.  *</p>  *  *  *  */
end_comment

begin_class
DECL|class|SolrQueryRequestBase
specifier|public
specifier|abstract
class|class
name|SolrQueryRequestBase
implements|implements
name|SolrQueryRequest
implements|,
name|Closeable
block|{
DECL|field|core
specifier|protected
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|origParams
specifier|protected
specifier|final
name|SolrParams
name|origParams
decl_stmt|;
DECL|field|schema
specifier|protected
specifier|volatile
name|IndexSchema
name|schema
decl_stmt|;
DECL|field|params
specifier|protected
name|SolrParams
name|params
decl_stmt|;
DECL|field|context
specifier|protected
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|context
decl_stmt|;
DECL|field|streams
specifier|protected
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|streams
decl_stmt|;
DECL|field|json
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
decl_stmt|;
DECL|field|requestTimer
specifier|private
specifier|final
name|RTimerTree
name|requestTimer
decl_stmt|;
DECL|field|startTime
specifier|protected
specifier|final
name|long
name|startTime
decl_stmt|;
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis to get start time for request (to be used for stats/debugging)"
argument_list|)
DECL|method|SolrQueryRequestBase
specifier|public
name|SolrQueryRequestBase
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|RTimerTree
name|requestTimer
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|schema
operator|=
literal|null
operator|==
name|core
condition|?
literal|null
else|:
name|core
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|this
operator|.
name|origParams
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|requestTimer
operator|=
name|requestTimer
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
DECL|method|SolrQueryRequestBase
specifier|public
name|SolrQueryRequestBase
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|this
argument_list|(
name|core
argument_list|,
name|params
argument_list|,
operator|new
name|RTimerTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContext
specifier|public
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getContext
parameter_list|()
block|{
comment|// SolrQueryRequest as a whole isn't thread safe, and this isn't either.
if|if
condition|(
name|context
operator|==
literal|null
condition|)
name|context
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
return|return
name|context
return|;
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
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|getOriginalParams
specifier|public
name|SolrParams
name|getOriginalParams
parameter_list|()
block|{
return|return
name|origParams
return|;
block|}
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
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
block|}
comment|// Get the start time of this request in milliseconds
annotation|@
name|Override
DECL|method|getStartTime
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
annotation|@
name|Override
DECL|method|getRequestTimer
specifier|public
name|RTimerTree
name|getRequestTimer
parameter_list|()
block|{
return|return
name|requestTimer
return|;
block|}
comment|// The index searcher associated with this request
DECL|field|searcherHolder
specifier|protected
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherHolder
decl_stmt|;
annotation|@
name|Override
DECL|method|getSearcher
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
block|{
if|if
condition|(
name|core
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|//a request for a core admin will not have a core
comment|// should this reach out and get a searcher from the core singleton, or
comment|// should the core populate one in a factory method to create requests?
comment|// or there could be a setSearcher() method that Solr calls
if|if
condition|(
name|searcherHolder
operator|==
literal|null
condition|)
block|{
name|searcherHolder
operator|=
name|core
operator|.
name|getSearcher
argument_list|()
expr_stmt|;
block|}
return|return
name|searcherHolder
operator|.
name|get
argument_list|()
return|;
block|}
comment|// The solr core (coordinator, etc) associated with this request
annotation|@
name|Override
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
block|{
return|return
name|core
return|;
block|}
comment|// The index schema associated with this request
annotation|@
name|Override
DECL|method|getSchema
specifier|public
name|IndexSchema
name|getSchema
parameter_list|()
block|{
comment|//a request for a core admin will no have a core
return|return
name|schema
return|;
block|}
annotation|@
name|Override
DECL|method|updateSchemaToLatest
specifier|public
name|void
name|updateSchemaToLatest
parameter_list|()
block|{
name|schema
operator|=
name|core
operator|.
name|getLatestSchema
argument_list|()
expr_stmt|;
block|}
comment|/**    * Frees resources associated with this request, this method<b>must</b>    * be called when the object is no longer in use.    */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|searcherHolder
operator|!=
literal|null
condition|)
block|{
name|searcherHolder
operator|.
name|decref
argument_list|()
expr_stmt|;
name|searcherHolder
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/** A Collection of ContentStreams passed to the request    */
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
block|{
return|return
name|streams
return|;
block|}
DECL|method|setContentStreams
specifier|public
name|void
name|setContentStreams
parameter_list|(
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|s
parameter_list|)
block|{
name|streams
operator|=
name|s
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getParamString
specifier|public
name|String
name|getParamString
parameter_list|()
block|{
return|return
name|origParams
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|'{'
operator|+
name|params
operator|+
literal|'}'
return|;
block|}
annotation|@
name|Override
DECL|method|getJSON
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getJSON
parameter_list|()
block|{
return|return
name|json
return|;
block|}
annotation|@
name|Override
DECL|method|setJSON
specifier|public
name|void
name|setJSON
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
parameter_list|)
block|{
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUserPrincipal
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|field|parsedCommands
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|parsedCommands
decl_stmt|;
DECL|method|getCommands
specifier|public
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|getCommands
parameter_list|(
name|boolean
name|validateInput
parameter_list|)
block|{
if|if
condition|(
name|parsedCommands
operator|==
literal|null
condition|)
block|{
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|contentStreams
init|=
name|getContentStreams
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentStreams
operator|==
literal|null
condition|)
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
literal|"No content stream"
argument_list|)
throw|;
for|for
control|(
name|ContentStream
name|contentStream
range|:
name|contentStreams
control|)
block|{
name|parsedCommands
operator|=
name|ApiBag
operator|.
name|getCommandOperations
argument_list|(
name|contentStream
argument_list|,
name|getValidators
argument_list|()
argument_list|,
name|validateInput
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|CommandOperation
operator|.
name|clone
argument_list|(
name|parsedCommands
argument_list|)
return|;
block|}
DECL|method|getSpec
specifier|protected
name|ValidatingJsonMap
name|getSpec
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getValidators
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|JsonSchemaValidator
argument_list|>
name|getValidators
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
block|}
block|}
end_class

end_unit

