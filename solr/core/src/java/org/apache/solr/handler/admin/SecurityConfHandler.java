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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|handler
operator|.
name|RequestHandlerBase
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
name|handler
operator|.
name|RequestHandlerUtils
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
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
operator|.
name|AuthenticationPlugin
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
name|security
operator|.
name|AuthorizationContext
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
name|security
operator|.
name|AuthorizationPlugin
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
name|security
operator|.
name|ConfigEditablePlugin
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
name|security
operator|.
name|PermissionNameProvider
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
operator|.
name|ReqHandlerToApi
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
name|SpecProvider
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
import|;
end_import

begin_class
DECL|class|SecurityConfHandler
specifier|public
specifier|abstract
class|class
name|SecurityConfHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|PermissionNameProvider
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cores
specifier|protected
name|CoreContainer
name|cores
decl_stmt|;
DECL|method|SecurityConfHandler
specifier|public
name|SecurityConfHandler
parameter_list|(
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|this
operator|.
name|cores
operator|=
name|coreContainer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPermissionName
specifier|public
name|PermissionNameProvider
operator|.
name|Name
name|getPermissionName
parameter_list|(
name|AuthorizationContext
name|ctx
parameter_list|)
block|{
switch|switch
condition|(
name|ctx
operator|.
name|getHttpMethod
argument_list|()
condition|)
block|{
case|case
literal|"GET"
case|:
return|return
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|SECURITY_READ_PERM
return|;
case|case
literal|"POST"
case|:
return|return
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|SECURITY_EDIT_PERM
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
name|RequestHandlerUtils
operator|.
name|setWt
argument_list|(
name|req
argument_list|,
name|CommonParams
operator|.
name|JSON
argument_list|)
expr_stmt|;
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
name|String
name|key
init|=
name|path
operator|.
name|substring
argument_list|(
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"GET"
operator|.
name|equals
argument_list|(
name|httpMethod
argument_list|)
condition|)
block|{
name|getConf
argument_list|(
name|rsp
argument_list|,
name|key
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
name|httpMethod
argument_list|)
condition|)
block|{
name|Object
name|plugin
init|=
name|getPlugin
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|doEdit
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|path
argument_list|,
name|key
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doEdit
specifier|private
name|void
name|doEdit
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|String
name|path
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|Object
name|plugin
parameter_list|)
throws|throws
name|IOException
block|{
name|ConfigEditablePlugin
name|configEditablePlugin
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|plugin
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
literal|"No "
operator|+
name|key
operator|+
literal|" plugin configured"
argument_list|)
throw|;
block|}
if|if
condition|(
name|plugin
operator|instanceof
name|ConfigEditablePlugin
condition|)
block|{
name|configEditablePlugin
operator|=
operator|(
name|ConfigEditablePlugin
operator|)
name|plugin
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
name|BAD_REQUEST
argument_list|,
name|key
operator|+
literal|" plugin is not editable"
argument_list|)
throw|;
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
literal|"No contentStream"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|ops
init|=
name|CommandOperation
operator|.
name|readCommands
argument_list|(
name|req
operator|.
name|getContentStreams
argument_list|()
argument_list|,
name|rsp
operator|.
name|getValues
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ops
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
literal|"No commands"
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|count
init|=
literal|1
init|;
name|count
operator|<=
literal|3
condition|;
name|count
operator|++
control|)
block|{
name|SecurityConfig
name|securityConfig
init|=
name|getSecurityConfig
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|securityConfig
operator|.
name|getData
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|latestConf
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|latestConf
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"No configuration present for "
operator|+
name|key
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|commandsCopy
init|=
name|CommandOperation
operator|.
name|clone
argument_list|(
name|ops
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|out
init|=
name|configEditablePlugin
operator|.
name|edit
argument_list|(
name|Utils
operator|.
name|getDeepCopy
argument_list|(
name|latestConf
argument_list|,
literal|4
argument_list|)
argument_list|,
name|commandsCopy
argument_list|)
decl_stmt|;
if|if
condition|(
name|out
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|Map
argument_list|>
name|errs
init|=
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|commandsCopy
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|rsp
operator|.
name|add
argument_list|(
name|CommandOperation
operator|.
name|ERR_MSGS
argument_list|,
name|errs
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"No edits made"
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|latestConf
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|,
name|out
operator|.
name|get
argument_list|(
literal|"class"
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"class cannot be modified"
argument_list|)
throw|;
block|}
name|Map
name|meta
init|=
name|getMapValue
argument_list|(
name|out
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|meta
operator|.
name|put
argument_list|(
literal|"v"
argument_list|,
name|securityConfig
operator|.
name|getVersion
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//encode the expected zkversion
name|data
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistConf
argument_list|(
name|securityConfig
argument_list|)
condition|)
block|{
name|securityConfEdited
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Security edit operation failed {} time(s)"
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Failed to persist security config after 3 attempts. Giving up"
argument_list|)
throw|;
block|}
comment|/**    * Hook where you can do stuff after a config has been edited. Defaults to NOP    */
DECL|method|securityConfEdited
specifier|protected
name|void
name|securityConfEdited
parameter_list|()
block|{}
DECL|method|getPlugin
name|Object
name|getPlugin
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Object
name|plugin
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"authentication"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
name|plugin
operator|=
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"authorization"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
name|plugin
operator|=
name|cores
operator|.
name|getAuthorizationPlugin
argument_list|()
expr_stmt|;
return|return
name|plugin
return|;
block|}
DECL|method|getConf
specifier|protected
specifier|abstract
name|void
name|getConf
parameter_list|(
name|SolrQueryResponse
name|rsp
parameter_list|,
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|getMapValue
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getMapValue
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|lookupMap
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|lookupMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|lookupMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|m
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
DECL|method|getListValue
specifier|public
specifier|static
name|List
name|getListValue
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|lookupMap
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|List
name|l
init|=
operator|(
name|List
operator|)
name|lookupMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
name|lookupMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|l
operator|=
operator|new
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|l
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Edit or read security configuration"
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|ADMIN
return|;
block|}
comment|/**    * Gets security.json from source    */
DECL|method|getSecurityConfig
specifier|public
specifier|abstract
name|SecurityConfig
name|getSecurityConfig
parameter_list|(
name|boolean
name|getFresh
parameter_list|)
function_decl|;
comment|/**    * Persist security.json to the source, optionally with a version    */
DECL|method|persistConf
specifier|protected
specifier|abstract
name|boolean
name|persistConf
parameter_list|(
name|SecurityConfig
name|securityConfig
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Object to hold security.json as nested<code>Map&lt;String,Object&gt;</code> and optionally its version.    * The version property is optional and defaults to -1 if not initialized.    * The data object defaults to EMPTY_MAP if not set    */
DECL|class|SecurityConfig
specifier|public
specifier|static
class|class
name|SecurityConfig
block|{
DECL|field|data
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
init|=
name|Collections
operator|.
name|EMPTY_MAP
decl_stmt|;
DECL|field|version
specifier|private
name|int
name|version
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|SecurityConfig
specifier|public
name|SecurityConfig
parameter_list|()
block|{}
comment|/**      * Sets the data as a Map      * @param data a Map      * @return SecurityConf object (builder pattern)      */
DECL|method|setData
specifier|public
name|SecurityConfig
name|setData
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the data as an Object, but the object needs to be of type Map      * @param data an Object of type Map&lt;String,Object&gt;      * @return SecurityConf object (builder pattern)      */
DECL|method|setData
specifier|public
name|SecurityConfig
name|setData
parameter_list|(
name|Object
name|data
parameter_list|)
block|{
if|if
condition|(
name|data
operator|instanceof
name|Map
condition|)
block|{
name|this
operator|.
name|data
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|data
expr_stmt|;
return|return
name|this
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SERVER_ERROR
argument_list|,
literal|"Illegal format when parsing security.json, not object"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sets version      * @param version integer for version. Depends on underlying storage      * @return SecurityConf object (builder pattern)      */
DECL|method|setVersion
specifier|public
name|SecurityConfig
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
DECL|method|getVersion
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**      * Set data from input stream      * @param securityJsonInputStream an input stream for security.json      * @return this (builder pattern)      */
DECL|method|setData
specifier|public
name|SecurityConfig
name|setData
parameter_list|(
name|InputStream
name|securityJsonInputStream
parameter_list|)
block|{
return|return
name|setData
argument_list|(
name|Utils
operator|.
name|fromJSON
argument_list|(
name|securityJsonInputStream
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SecurityConfig: version="
operator|+
name|version
operator|+
literal|", data="
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|data
argument_list|)
return|;
block|}
block|}
DECL|field|apis
specifier|private
name|Collection
argument_list|<
name|Api
argument_list|>
name|apis
decl_stmt|;
DECL|field|authcPlugin
specifier|private
name|AuthenticationPlugin
name|authcPlugin
decl_stmt|;
DECL|field|authzPlugin
specifier|private
name|AuthorizationPlugin
name|authzPlugin
decl_stmt|;
annotation|@
name|Override
DECL|method|getApis
specifier|public
name|Collection
argument_list|<
name|Api
argument_list|>
name|getApis
parameter_list|()
block|{
if|if
condition|(
name|apis
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|apis
operator|==
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|Api
argument_list|>
name|apis
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SpecProvider
name|authcCommands
init|=
name|Utils
operator|.
name|getSpec
argument_list|(
literal|"cluster.security.authentication.Commands"
argument_list|)
decl_stmt|;
specifier|final
name|SpecProvider
name|authzCommands
init|=
name|Utils
operator|.
name|getSpec
argument_list|(
literal|"cluster.security.authorization.Commands"
argument_list|)
decl_stmt|;
name|apis
operator|.
name|add
argument_list|(
operator|new
name|ReqHandlerToApi
argument_list|(
name|this
argument_list|,
name|Utils
operator|.
name|getSpec
argument_list|(
literal|"cluster.security.authentication"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|apis
operator|.
name|add
argument_list|(
operator|new
name|ReqHandlerToApi
argument_list|(
name|this
argument_list|,
name|Utils
operator|.
name|getSpec
argument_list|(
literal|"cluster.security.authorization"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|SpecProvider
name|authcSpecProvider
init|=
parameter_list|()
lambda|->
block|{
name|AuthenticationPlugin
name|authcPlugin
init|=
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
decl_stmt|;
return|return
name|authcPlugin
operator|!=
literal|null
operator|&&
name|authcPlugin
operator|instanceof
name|SpecProvider
condition|?
operator|(
operator|(
name|SpecProvider
operator|)
name|authcPlugin
operator|)
operator|.
name|getSpec
argument_list|()
else|:
name|authcCommands
operator|.
name|getSpec
argument_list|()
return|;
block|}
decl_stmt|;
name|apis
operator|.
name|add
argument_list|(
operator|new
name|ReqHandlerToApi
argument_list|(
name|this
argument_list|,
name|authcSpecProvider
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|JsonSchemaValidator
argument_list|>
name|getCommandSchema
parameter_list|()
block|{
comment|//it is possible that the Authentication plugin is modified since the last call. invalidate the
comment|// the cached commandSchema
if|if
condition|(
name|SecurityConfHandler
operator|.
name|this
operator|.
name|authcPlugin
operator|!=
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
condition|)
name|commandSchema
operator|=
literal|null
expr_stmt|;
name|SecurityConfHandler
operator|.
name|this
operator|.
name|authcPlugin
operator|=
name|cores
operator|.
name|getAuthenticationPlugin
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getCommandSchema
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|SpecProvider
name|authzSpecProvider
init|=
parameter_list|()
lambda|->
block|{
name|AuthorizationPlugin
name|authzPlugin
init|=
name|cores
operator|.
name|getAuthorizationPlugin
argument_list|()
decl_stmt|;
return|return
name|authzPlugin
operator|!=
literal|null
operator|&&
name|authzPlugin
operator|instanceof
name|SpecProvider
condition|?
operator|(
operator|(
name|SpecProvider
operator|)
name|authzPlugin
operator|)
operator|.
name|getSpec
argument_list|()
else|:
name|authzCommands
operator|.
name|getSpec
argument_list|()
return|;
block|}
decl_stmt|;
name|apis
operator|.
name|add
argument_list|(
operator|new
name|ApiBag
operator|.
name|ReqHandlerToApi
argument_list|(
name|this
argument_list|,
name|authzSpecProvider
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|JsonSchemaValidator
argument_list|>
name|getCommandSchema
parameter_list|()
block|{
comment|//it is possible that the Authorization plugin is modified since the last call. invalidate the
comment|// the cached commandSchema
if|if
condition|(
name|SecurityConfHandler
operator|.
name|this
operator|.
name|authzPlugin
operator|!=
name|cores
operator|.
name|getAuthorizationPlugin
argument_list|()
condition|)
name|commandSchema
operator|=
literal|null
expr_stmt|;
name|SecurityConfHandler
operator|.
name|this
operator|.
name|authzPlugin
operator|=
name|cores
operator|.
name|getAuthorizationPlugin
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|getCommandSchema
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|apis
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|apis
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|this
operator|.
name|apis
return|;
block|}
annotation|@
name|Override
DECL|method|registerV2
specifier|public
name|Boolean
name|registerV2
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit

