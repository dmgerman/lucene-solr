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
name|registry
operator|.
name|ServerComponent
import|;
end_import

begin_comment
comment|/**  * Implementations of the AuthenticationController interface contain all the  * logic for processing token based authentification. A token is an encoded  * unique<tt>String</tt> value passed back to the client if successfully  * authenticated. Clients provide account name, password, the requested service  * and the name of the application used for accessing the the gdata service.  *<p>  * The algorithmn to create and reauthenticate the token can be choosen by the  * implementor.<br/> This interface extends  * {@link org.apache.lucene.gdata.server.registry.ServerComponent} e.g.  * implementing classes can be registered as a  * {@link org.apache.lucene.gdata.server.registry.Component} in the  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry} to be  * accessed via the provided lookup service  *</p>  *   * @see org.apache.lucene.gdata.server.authentication.BlowfishAuthenticationController  * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|AuthenticationController
specifier|public
interface|interface
name|AuthenticationController
extends|extends
name|ServerComponent
block|{
comment|/**      * The header name containing the authentication token provided by the      * client      */
DECL|field|AUTHORIZATION_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|AUTHORIZATION_HEADER
init|=
literal|"Authorization"
decl_stmt|;
comment|/**      * Authentication parameter for the account name. Provided by the client to      * recieve the auth token.      */
DECL|field|ACCOUNT_PARAMETER
specifier|public
specifier|static
specifier|final
name|String
name|ACCOUNT_PARAMETER
init|=
literal|"Email"
decl_stmt|;
comment|/**      * Authentication parameter for the account password. Provided by the client      * to recieve the auth token.      */
DECL|field|PASSWORD_PARAMETER
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD_PARAMETER
init|=
literal|"Passwd"
decl_stmt|;
comment|/**      * Authentication parameter for the requested service. Provided by the      * client to recieve the auth token.      */
DECL|field|SERVICE_PARAMETER
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_PARAMETER
init|=
literal|"service"
decl_stmt|;
comment|/**      * Authentication parameter for the application name of the clients      * application. This is just used for loggin purposes      */
DECL|field|APPLICATION_PARAMETER
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_PARAMETER
init|=
literal|"source"
decl_stmt|;
comment|/**      * The key used for respond the auth token to the client. Either as a cookie      * (key as cookie name) or as plain response (TOKEN_KEY=TOKEN)      */
DECL|field|TOKEN_KEY
specifier|public
specifier|final
specifier|static
name|String
name|TOKEN_KEY
init|=
literal|"Auth"
decl_stmt|;
comment|/**      * Creates a authentication token for the given account. The token will be      * calculated based on a part of the clients ip address, the account role      * and the account name and the time in millisecond at the point of      * creation.      *       * @param account -      *            the account to create the token for      * @param requestIp -      *            the clients request ip address      * @return - a BASE64 encoded authentification token      */
DECL|method|authenticatAccount
specifier|public
specifier|abstract
name|String
name|authenticatAccount
parameter_list|(
name|GDataAccount
name|account
parameter_list|,
name|String
name|requestIp
parameter_list|)
function_decl|;
comment|/**      * Authenticates the given auth token and checks the given parameter for      * matching the information contained inside the token.      *<p>      * if the given account name is<code>null</code> the authentication will      * ignore the account name and the decision whether the token is valid or      * not will be based on the given role compared to the role inside the token      *</p>      *       * @param token -      *            the token to authenticate      * @param requestIp -      *            the client request IP address      * @param role -      *            the required role      * @param accountName -      *            the name of the account      * @return<code>true</code> if the given values match the values inside      *         the token and if the timestamp plus the configured timeout is      *         greater than the current time, if one of the values does not      *         match or the token has timed out it will return      *<code>false</code>      */
DECL|method|authenticateToken
specifier|public
specifier|abstract
name|boolean
name|authenticateToken
parameter_list|(
specifier|final
name|String
name|token
parameter_list|,
specifier|final
name|String
name|requestIp
parameter_list|,
name|AccountRole
name|role
parameter_list|,
name|String
name|accountName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

