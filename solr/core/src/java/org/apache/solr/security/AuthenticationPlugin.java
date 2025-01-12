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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|AuthenticationPlugin
specifier|public
specifier|abstract
class|class
name|AuthenticationPlugin
implements|implements
name|Closeable
block|{
DECL|field|AUTHENTICATION_PLUGIN_PROP
specifier|final
specifier|public
specifier|static
name|String
name|AUTHENTICATION_PLUGIN_PROP
init|=
literal|"authenticationPlugin"
decl_stmt|;
comment|/**    * This is called upon loading up of a plugin, used for setting it up.    * @param pluginConfig Config parameters, possibly from a ZK source    */
DECL|method|init
specifier|public
specifier|abstract
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
comment|/**    * This method attempts to authenticate the request. Upon a successful authentication, this    * must call the next filter in the filter chain and set the user principal of the request,    * or else, upon an error or an authentication failure, throw an exception.    *    * @param request the http request    * @param response the http response    * @param filterChain the servlet filter chain    * @return false if the request not be processed by Solr (not continue), i.e.    * the response and status code have already been sent.    * @throws Exception any exception thrown during the authentication, e.g. PrivilegedActionException    */
DECL|method|doAuthenticate
specifier|public
specifier|abstract
name|boolean
name|doAuthenticate
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Cleanup any per request  data    */
DECL|method|closeRequest
specifier|public
name|void
name|closeRequest
parameter_list|()
block|{   }
block|}
end_class

end_unit

