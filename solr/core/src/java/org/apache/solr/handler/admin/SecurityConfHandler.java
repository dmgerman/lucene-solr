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
name|cloud
operator|.
name|ZkStateReader
operator|.
name|ConfigData
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
name|SolrConfigHandler
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_class
DECL|class|SecurityConfHandler
specifier|public
class|class
name|SecurityConfHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|PermissionNameProvider
block|{
DECL|field|cores
specifier|private
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
name|SolrConfigHandler
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
init|;
condition|;
control|)
block|{
name|ConfigData
name|data
init|=
name|getSecurityProps
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
name|SolrException
operator|.
name|ErrorCode
operator|.
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
comment|//no edits
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
name|SolrException
operator|.
name|ErrorCode
operator|.
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
name|data
operator|.
name|version
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//encode the expected zkversion
name|data
operator|.
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
literal|"/security.json"
argument_list|,
name|Utils
operator|.
name|toJSON
argument_list|(
name|data
operator|.
name|data
argument_list|)
argument_list|,
name|data
operator|.
name|version
argument_list|)
condition|)
return|return;
block|}
block|}
block|}
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
DECL|method|getSecurityProps
name|ConfigData
name|getSecurityProps
parameter_list|(
name|boolean
name|getFresh
parameter_list|)
block|{
return|return
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getSecurityProps
argument_list|(
name|getFresh
argument_list|)
return|;
block|}
DECL|method|persistConf
name|boolean
name|persistConf
parameter_list|(
name|String
name|path
parameter_list|,
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|version
parameter_list|)
block|{
try|try
block|{
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
operator|.
name|setData
argument_list|(
name|path
argument_list|,
name|buf
argument_list|,
name|version
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|BadVersionException
name|bdve
parameter_list|)
block|{
return|return
literal|false
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
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|" Unable to persist conf"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getConf
specifier|private
name|void
name|getConf
parameter_list|(
name|SolrQueryResponse
name|rsp
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|ConfigData
name|map
init|=
name|cores
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getSecurityProps
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|map
operator|==
literal|null
condition|?
literal|null
else|:
name|map
operator|.
name|data
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
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
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"No "
operator|+
name|key
operator|+
literal|" configured"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|add
argument_list|(
name|key
operator|+
literal|".enabled"
argument_list|,
name|getPlugin
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
end_class

end_unit

