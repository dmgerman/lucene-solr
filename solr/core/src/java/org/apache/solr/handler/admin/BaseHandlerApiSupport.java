begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Objects
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
name|collect
operator|.
name|ImmutableList
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
name|api
operator|.
name|Api
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
name|api
operator|.
name|ApiSupport
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
name|request
operator|.
name|CollectionApiMapping
operator|.
name|CommandMeta
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
name|CollectionApiMapping
operator|.
name|V2EndPoint
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
name|Utils
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
name|response
operator|.
name|SolrQueryResponse
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
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
name|util
operator|.
name|StrUtils
operator|.
name|splitSmart
import|;
end_import

begin_comment
comment|/**  * This is a utility class to provide an easy mapping of request handlers which support multiple commands  * to the V2 API format (core admin api, collections api). This helps in automatically mapping paths  * to actions and old parameter names to new parameter names  */
end_comment

begin_class
DECL|class|BaseHandlerApiSupport
specifier|public
specifier|abstract
class|class
name|BaseHandlerApiSupport
implements|implements
name|ApiSupport
block|{
DECL|field|commandsMapping
specifier|protected
specifier|final
name|Map
argument_list|<
name|SolrRequest
operator|.
name|METHOD
argument_list|,
name|Map
argument_list|<
name|V2EndPoint
argument_list|,
name|List
argument_list|<
name|ApiCommand
argument_list|>
argument_list|>
argument_list|>
name|commandsMapping
decl_stmt|;
DECL|method|BaseHandlerApiSupport
specifier|protected
name|BaseHandlerApiSupport
parameter_list|()
block|{
name|commandsMapping
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|ApiCommand
name|cmd
range|:
name|getCommands
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|V2EndPoint
argument_list|,
name|List
argument_list|<
name|ApiCommand
argument_list|>
argument_list|>
name|m
init|=
name|commandsMapping
operator|.
name|get
argument_list|(
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|commandsMapping
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getHttpMethod
argument_list|()
argument_list|,
name|m
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ApiCommand
argument_list|>
name|list
init|=
name|m
operator|.
name|get
argument_list|(
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getEndPoint
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
name|m
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getEndPoint
argument_list|()
argument_list|,
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getApis
specifier|public
specifier|synchronized
name|Collection
argument_list|<
name|Api
argument_list|>
name|getApis
parameter_list|()
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Api
argument_list|>
name|l
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|V2EndPoint
name|op
range|:
name|getEndPoints
argument_list|()
control|)
name|l
operator|.
name|add
argument_list|(
name|getApi
argument_list|(
name|op
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|l
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getApi
specifier|private
name|Api
name|getApi
parameter_list|(
specifier|final
name|V2EndPoint
name|op
parameter_list|)
block|{
specifier|final
name|BaseHandlerApiSupport
name|apiHandler
init|=
name|this
decl_stmt|;
return|return
operator|new
name|Api
argument_list|(
name|ApiBag
operator|.
name|getSpec
argument_list|(
name|op
operator|.
name|getSpecName
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
block|{
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|SolrRequest
operator|.
name|METHOD
name|method
init|=
name|SolrRequest
operator|.
name|METHOD
operator|.
name|valueOf
argument_list|(
name|req
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ApiCommand
argument_list|>
name|commands
init|=
name|commandsMapping
operator|.
name|get
argument_list|(
name|method
argument_list|)
operator|.
name|get
argument_list|(
name|op
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|method
operator|==
name|POST
condition|)
block|{
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|cmds
init|=
name|req
operator|.
name|getCommands
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmds
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
literal|"Only one command is allowed"
argument_list|)
throw|;
name|CommandOperation
name|c
init|=
name|cmds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|cmds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ApiCommand
name|command
init|=
literal|null
decl_stmt|;
name|String
name|commandName
init|=
name|c
operator|==
literal|null
condition|?
literal|null
else|:
name|c
operator|.
name|name
decl_stmt|;
for|for
control|(
name|ApiCommand
name|cmd
range|:
name|commands
control|)
block|{
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|commandName
argument_list|)
condition|)
block|{
name|command
operator|=
name|cmd
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
literal|" no such command "
operator|+
name|c
argument_list|)
throw|;
block|}
name|wrapParams
argument_list|(
name|req
argument_list|,
name|c
argument_list|,
name|command
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|command
operator|.
name|invoke
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|apiHandler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|commands
operator|==
literal|null
operator|||
name|commands
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
literal|"No support for : "
operator|+
name|method
operator|+
literal|" at :"
operator|+
name|req
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|commands
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|ApiCommand
name|command
range|:
name|commands
control|)
block|{
if|if
condition|(
name|command
operator|.
name|meta
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|commands
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|command
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|wrapParams
argument_list|(
name|req
argument_list|,
operator|new
name|CommandOperation
argument_list|(
literal|""
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
argument_list|,
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|commands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|invoke
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|apiHandler
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|req
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
DECL|method|wrapParams
specifier|private
specifier|static
name|void
name|wrapParams
parameter_list|(
specifier|final
name|SolrQueryRequest
name|req
parameter_list|,
specifier|final
name|CommandOperation
name|co
parameter_list|,
specifier|final
name|ApiCommand
name|cmd
parameter_list|,
specifier|final
name|boolean
name|useRequestParams
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pathValues
init|=
name|req
operator|.
name|getPathTemplateValues
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|co
operator|==
literal|null
operator|||
operator|!
operator|(
name|co
operator|.
name|getCommandData
argument_list|()
operator|instanceof
name|Map
operator|)
condition|?
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|""
argument_list|,
name|co
operator|.
name|getCommandData
argument_list|()
argument_list|)
else|:
name|co
operator|.
name|getDataMap
argument_list|()
decl_stmt|;
specifier|final
name|SolrParams
name|origParams
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|req
operator|.
name|setParams
argument_list|(
operator|new
name|SolrParams
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|Object
name|vals
init|=
name|getParams0
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|vals
operator|instanceof
name|String
condition|)
return|return
operator|(
name|String
operator|)
name|vals
return|;
if|if
condition|(
name|vals
operator|instanceof
name|Boolean
operator|||
name|vals
operator|instanceof
name|Number
condition|)
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|vals
argument_list|)
return|;
if|if
condition|(
name|vals
operator|instanceof
name|String
index|[]
operator|&&
operator|(
operator|(
name|String
index|[]
operator|)
name|vals
operator|)
operator|.
name|length
operator|>
literal|0
condition|)
return|return
operator|(
operator|(
name|String
index|[]
operator|)
name|vals
operator|)
index|[
literal|0
index|]
return|;
return|return
literal|null
return|;
block|}
specifier|private
name|Object
name|getParams0
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|param
operator|=
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getParamSubstitute
argument_list|(
name|param
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|param
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|>
literal|0
condition|?
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|map
argument_list|,
literal|true
argument_list|,
name|splitSmart
argument_list|(
name|param
argument_list|,
literal|'.'
argument_list|)
argument_list|)
else|:
name|map
operator|.
name|get
argument_list|(
name|param
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
name|o
operator|=
name|pathValues
operator|.
name|get
argument_list|(
name|param
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
operator|&&
name|useRequestParams
condition|)
name|o
operator|=
name|origParams
operator|.
name|getParams
argument_list|(
name|param
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
name|l
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
return|return
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
return|return
name|o
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|param
parameter_list|)
block|{
name|Object
name|vals
init|=
name|getParams0
argument_list|(
name|param
argument_list|)
decl_stmt|;
return|return
name|vals
operator|==
literal|null
operator|||
name|vals
operator|instanceof
name|String
index|[]
condition|?
operator|(
name|String
index|[]
operator|)
name|vals
else|:
operator|new
name|String
index|[]
block|{
name|vals
operator|.
name|toString
argument_list|()
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|getParameterNamesIterator
parameter_list|()
block|{
return|return
name|cmd
operator|.
name|meta
argument_list|()
operator|.
name|getParamNames
argument_list|(
name|co
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommands
specifier|protected
specifier|abstract
name|Collection
argument_list|<
name|ApiCommand
argument_list|>
name|getCommands
parameter_list|()
function_decl|;
DECL|method|getEndPoints
specifier|protected
specifier|abstract
name|Collection
argument_list|<
name|V2EndPoint
argument_list|>
name|getEndPoints
parameter_list|()
function_decl|;
DECL|interface|ApiCommand
specifier|public
interface|interface
name|ApiCommand
block|{
DECL|method|meta
name|CommandMeta
name|meta
parameter_list|()
function_decl|;
DECL|method|invoke
name|void
name|invoke
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|BaseHandlerApiSupport
name|apiHandler
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
block|}
end_class

end_unit

