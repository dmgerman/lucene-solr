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
name|io
operator|.
name|OutputStream
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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

begin_comment
comment|/**  * Security Configuration Handler which works on standalone local files  */
end_comment

begin_class
DECL|class|SecurityConfHandlerLocal
specifier|public
class|class
name|SecurityConfHandlerLocal
extends|extends
name|SecurityConfHandler
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
DECL|field|securityJsonPath
specifier|protected
name|Path
name|securityJsonPath
decl_stmt|;
DECL|method|SecurityConfHandlerLocal
specifier|public
name|SecurityConfHandlerLocal
parameter_list|(
name|CoreContainer
name|coreContainer
parameter_list|)
block|{
name|super
argument_list|(
name|coreContainer
argument_list|)
expr_stmt|;
name|securityJsonPath
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|coreContainer
operator|.
name|getSolrHome
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"security.json"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Fetches security props from SOLR_HOME    * @param getFresh NOP    * @return SecurityConfig whose data property either contains security.json, or an empty map if not found    */
annotation|@
name|Override
DECL|method|getSecurityConfig
specifier|public
name|SecurityConfig
name|getSecurityConfig
parameter_list|(
name|boolean
name|getFresh
parameter_list|)
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|securityJsonPath
argument_list|)
condition|)
block|{
try|try
init|(
name|InputStream
name|securityJsonIs
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|securityJsonPath
argument_list|)
init|)
block|{
return|return
operator|new
name|SecurityConfig
argument_list|()
operator|.
name|setData
argument_list|(
name|securityJsonIs
argument_list|)
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
literal|"Failed opening existing security.json file: "
operator|+
name|securityJsonPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|SecurityConfig
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getConf
specifier|protected
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
name|SecurityConfig
name|props
init|=
name|getSecurityConfig
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|props
operator|.
name|getData
argument_list|()
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
annotation|@
name|Override
DECL|method|persistConf
specifier|protected
name|boolean
name|persistConf
parameter_list|(
name|SecurityConfig
name|securityConfig
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|securityConfig
operator|==
literal|null
operator|||
name|securityConfig
operator|.
name|getData
argument_list|()
operator|.
name|isEmpty
argument_list|()
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
literal|"Failed persisting security.json to SOLR_HOME. Object was empty."
argument_list|)
throw|;
block|}
try|try
init|(
name|OutputStream
name|securityJsonOs
init|=
name|Files
operator|.
name|newOutputStream
argument_list|(
name|securityJsonPath
argument_list|)
init|)
block|{
name|securityJsonOs
operator|.
name|write
argument_list|(
name|Utils
operator|.
name|toJSON
argument_list|(
name|securityConfig
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Persisted security.json to {}"
argument_list|,
name|securityJsonPath
argument_list|)
expr_stmt|;
return|return
literal|true
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
literal|"Failed persisting security.json to "
operator|+
name|securityJsonPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
literal|"Edit or read security configuration locally in SOLR_HOME"
return|;
block|}
annotation|@
name|Override
DECL|method|securityConfEdited
specifier|protected
name|void
name|securityConfEdited
parameter_list|()
block|{
comment|// Need to call explicitly since we will not get notified of changes to local security.json
name|cores
operator|.
name|securityNodeChanged
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

