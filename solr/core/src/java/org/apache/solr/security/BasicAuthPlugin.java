begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequestWrapper
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
name|HttpServletResponse
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
name|UnsupportedEncodingException
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|ImmutableSet
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
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|auth
operator|.
name|BasicUserPrincipal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|message
operator|.
name|BasicHeader
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
name|SpecProvider
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

begin_class
DECL|class|BasicAuthPlugin
specifier|public
class|class
name|BasicAuthPlugin
extends|extends
name|AuthenticationPlugin
implements|implements
name|ConfigEditablePlugin
implements|,
name|SpecProvider
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
DECL|field|authenticationProvider
specifier|private
name|AuthenticationProvider
name|authenticationProvider
decl_stmt|;
DECL|field|authHeader
specifier|private
specifier|final
specifier|static
name|ThreadLocal
argument_list|<
name|Header
argument_list|>
name|authHeader
init|=
operator|new
name|ThreadLocal
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|blockUnknown
specifier|private
name|boolean
name|blockUnknown
init|=
literal|false
decl_stmt|;
DECL|method|authenticate
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|pwd
parameter_list|)
block|{
return|return
name|authenticationProvider
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|pwd
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{
name|Object
name|o
init|=
name|pluginConfig
operator|.
name|get
argument_list|(
name|BLOCK_UNKNOWN
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|blockUnknown
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|authenticationProvider
operator|=
name|getAuthenticationProvider
argument_list|(
name|pluginConfig
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|edit
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|edit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|latestConf
parameter_list|,
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|commands
parameter_list|)
block|{
for|for
control|(
name|CommandOperation
name|command
range|:
name|commands
control|)
block|{
if|if
condition|(
name|command
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"set-property"
argument_list|)
condition|)
block|{
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
name|command
operator|.
name|getDataMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|PROPS
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|latestConf
operator|.
name|put
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
return|return
name|latestConf
return|;
block|}
else|else
block|{
name|command
operator|.
name|addError
argument_list|(
literal|"Unknown property "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|commands
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|authenticationProvider
operator|instanceof
name|ConfigEditablePlugin
condition|)
block|{
name|ConfigEditablePlugin
name|editablePlugin
init|=
operator|(
name|ConfigEditablePlugin
operator|)
name|authenticationProvider
decl_stmt|;
return|return
name|editablePlugin
operator|.
name|edit
argument_list|(
name|latestConf
argument_list|,
name|commands
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
literal|"This cannot be edited"
argument_list|)
throw|;
block|}
DECL|method|getAuthenticationProvider
specifier|protected
name|AuthenticationProvider
name|getAuthenticationProvider
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
block|{
name|Sha256AuthenticationProvider
name|provider
init|=
operator|new
name|Sha256AuthenticationProvider
argument_list|()
decl_stmt|;
name|provider
operator|.
name|init
argument_list|(
name|pluginConfig
argument_list|)
expr_stmt|;
return|return
name|provider
return|;
block|}
DECL|method|authenticationFailure
specifier|private
name|void
name|authenticationFailure
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|authenticationProvider
operator|.
name|getPromptHeaders
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|sendError
argument_list|(
literal|401
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAuthenticate
specifier|public
name|boolean
name|doAuthenticate
parameter_list|(
name|ServletRequest
name|servletRequest
parameter_list|,
name|ServletResponse
name|servletResponse
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpServletRequest
name|request
init|=
operator|(
name|HttpServletRequest
operator|)
name|servletRequest
decl_stmt|;
name|HttpServletResponse
name|response
init|=
operator|(
name|HttpServletResponse
operator|)
name|servletResponse
decl_stmt|;
name|String
name|authHeader
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Authorization"
argument_list|)
decl_stmt|;
if|if
condition|(
name|authHeader
operator|!=
literal|null
condition|)
block|{
name|BasicAuthPlugin
operator|.
name|authHeader
operator|.
name|set
argument_list|(
operator|new
name|BasicHeader
argument_list|(
literal|"Authorization"
argument_list|,
name|authHeader
argument_list|)
argument_list|)
expr_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|authHeader
argument_list|)
decl_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|basic
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|basic
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Basic"
argument_list|)
condition|)
block|{
try|try
block|{
name|String
name|credentials
init|=
operator|new
name|String
argument_list|(
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|credentials
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
operator|-
literal|1
condition|)
block|{
specifier|final
name|String
name|username
init|=
name|credentials
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|pwd
init|=
name|credentials
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|authenticate
argument_list|(
name|username
argument_list|,
name|pwd
argument_list|)
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Bad auth credentials supplied in Authorization header"
argument_list|)
expr_stmt|;
name|authenticationFailure
argument_list|(
name|response
argument_list|,
literal|"Bad credentials"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|HttpServletRequestWrapper
name|wrapper
init|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|request
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
operator|new
name|BasicUserPrincipal
argument_list|(
name|username
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|wrapper
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
name|authenticationFailure
argument_list|(
name|response
argument_list|,
literal|"Invalid authentication token"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
literal|"Couldn't retrieve authentication"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|blockUnknown
condition|)
block|{
name|authenticationFailure
argument_list|(
name|response
argument_list|,
literal|"require authentication"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|setAttribute
argument_list|(
name|AuthenticationPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|authenticationProvider
operator|.
name|getPromptHeaders
argument_list|()
argument_list|)
expr_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
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
block|{    }
annotation|@
name|Override
DECL|method|closeRequest
specifier|public
name|void
name|closeRequest
parameter_list|()
block|{
name|authHeader
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
DECL|interface|AuthenticationProvider
specifier|public
interface|interface
name|AuthenticationProvider
extends|extends
name|SpecProvider
block|{
DECL|method|init
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|pluginConfig
parameter_list|)
function_decl|;
DECL|method|authenticate
name|boolean
name|authenticate
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|pwd
parameter_list|)
function_decl|;
DECL|method|getPromptHeaders
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getPromptHeaders
parameter_list|()
function_decl|;
block|}
annotation|@
name|Override
DECL|method|getSpec
specifier|public
name|ValidatingJsonMap
name|getSpec
parameter_list|()
block|{
return|return
name|authenticationProvider
operator|.
name|getSpec
argument_list|()
return|;
block|}
DECL|method|getBlockUnknown
specifier|public
name|boolean
name|getBlockUnknown
parameter_list|()
block|{
return|return
name|blockUnknown
return|;
block|}
DECL|field|BLOCK_UNKNOWN
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_UNKNOWN
init|=
literal|"blockUnknown"
decl_stmt|;
DECL|field|PROPS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PROPS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|BLOCK_UNKNOWN
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

