begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.authentication
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|authentication
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|data
operator|.
name|GDataAccount
operator|.
name|AccountRole
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
name|gdata
operator|.
name|server
operator|.
name|GDataRequest
import|;
end_import

begin_comment
comment|/**  * The GData protocol is based on the widly know REST approach and therefor  * client authentication will mostly be provided via a REST interface.  *<p>  * This interface describes internally used authentication methods to be  * implemented by http based authenticator implementations. The GData Server  * basically has 2 different REST interfaces need authentication. One is for  * altering feed entries and the other for administration actions.  *</p>  *<p>The interface altering entries work with {@link com.google.gdata.client.Service.GDataRequest} object created by the handler and passed to the {@link org.apache.lucene.gdata.server.Service} instance.  * Administration interfaces use the plain {@link javax.servlet.http.HttpServletRequest} inside the handler.  * For each type of interface a authentication type a method has to be provided by implementing classes.</p>   *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GDataHttpAuthenticator
specifier|public
interface|interface
name|GDataHttpAuthenticator
block|{
comment|/**      * Authenticates the client request based on the given GdataRequst and required account role      * @param request - the gdata request      * @param role - the required role for passing the authentication      *       * @return<code>true</code> if the request successfully authenticates, otherwise<code>false</code>      */
DECL|method|authenticateAccount
specifier|public
name|boolean
name|authenticateAccount
parameter_list|(
specifier|final
name|GDataRequest
name|request
parameter_list|,
name|AccountRole
name|role
parameter_list|)
function_decl|;
comment|/**      * Authenticates the client request based on the given requst and required account role      * @param request - the client request      * @param role - the required role for passing the authentication      * @return<code>true</code> if the request successfully authenticates, otherwise<code>false</code>      */
DECL|method|authenticateAccount
specifier|public
name|boolean
name|authenticateAccount
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
name|AccountRole
name|role
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

