begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|Arrays
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
name|Locale
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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
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
name|HttpException
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
name|HttpRequest
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
name|HttpRequestInterceptor
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
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
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
name|protocol
operator|.
name|HttpContext
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
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
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
name|impl
operator|.
name|HttpClientConfigurer
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
name|util
operator|.
name|RevertDefaultThreadHandlerRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
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
comment|/**  * Test of the MiniSolrCloudCluster functionality with authentication enabled.  */
end_comment

begin_class
annotation|@
name|LuceneTestCase
operator|.
name|Slow
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Solr logs to JUL"
argument_list|)
DECL|class|TestAuthenticationFramework
specifier|public
class|class
name|TestAuthenticationFramework
extends|extends
name|TestMiniSolrCloudCluster
block|{
DECL|method|TestAuthenticationFramework
specifier|public
name|TestAuthenticationFramework
parameter_list|()
block|{
name|NUM_SERVERS
operator|=
literal|5
expr_stmt|;
name|NUM_SHARDS
operator|=
literal|2
expr_stmt|;
name|REPLICATION_FACTOR
operator|=
literal|2
expr_stmt|;
block|}
DECL|field|requestUsername
specifier|static
name|String
name|requestUsername
init|=
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
decl_stmt|;
DECL|field|requestPassword
specifier|static
name|String
name|requestPassword
init|=
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
decl_stmt|;
DECL|field|brokenLocales
specifier|protected
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|brokenLocales
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"th_TH_TH_#u-nu-thai"
argument_list|,
literal|"ja_JP_JP_#u-ca-japanese"
argument_list|,
literal|"hi_IN"
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|ClassRule
DECL|field|solrClassRules
specifier|public
specifier|static
name|TestRule
name|solrClassRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
operator|.
name|around
argument_list|(
operator|new
name|RevertDefaultThreadHandlerRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|brokenLocales
operator|.
name|contains
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
name|setupAuthenticationPlugin
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|setupAuthenticationPlugin
specifier|private
name|void
name|setupAuthenticationPlugin
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|,
literal|"org.apache.solr.cloud.TestAuthenticationFramework$MockAuthenticationPlugin"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Override
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|requestUsername
operator|=
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
expr_stmt|;
name|requestPassword
operator|=
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
expr_stmt|;
comment|// Should pass
name|testCollectionCreateSearchDelete
argument_list|()
expr_stmt|;
name|requestUsername
operator|=
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
expr_stmt|;
name|requestPassword
operator|=
literal|"junkpassword"
expr_stmt|;
comment|// Should fail with 401
try|try
block|{
name|testCollectionCreateSearchDelete
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should've returned a 401 error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Error 401"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Should've returned a 401 error"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"authenticationPlugin"
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|class|MockAuthenticationPlugin
specifier|public
specifier|static
class|class
name|MockAuthenticationPlugin
extends|extends
name|AuthenticationPlugin
block|{
DECL|field|log
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MockAuthenticationPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|expectedUsername
specifier|public
specifier|static
name|String
name|expectedUsername
init|=
literal|"solr"
decl_stmt|;
DECL|field|expectedPassword
specifier|public
specifier|static
name|String
name|expectedPassword
init|=
literal|"s0lrRocks"
decl_stmt|;
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
block|{}
annotation|@
name|Override
DECL|method|doAuthenticate
specifier|public
name|void
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
block|{
name|HttpServletRequest
name|httpRequest
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|String
name|username
init|=
name|httpRequest
operator|.
name|getHeader
argument_list|(
literal|"username"
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|httpRequest
operator|.
name|getHeader
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Username: "
operator|+
name|username
operator|+
literal|", password: "
operator|+
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|MockAuthenticationPlugin
operator|.
name|expectedUsername
operator|.
name|equals
argument_list|(
name|username
argument_list|)
operator|&&
name|MockAuthenticationPlugin
operator|.
name|expectedPassword
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
name|filterChain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
else|else
block|{
operator|(
operator|(
name|HttpServletResponse
operator|)
name|response
operator|)
operator|.
name|sendError
argument_list|(
literal|401
argument_list|,
literal|"Unauthorized request"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDefaultConfigurer
specifier|public
name|HttpClientConfigurer
name|getDefaultConfigurer
parameter_list|()
block|{
return|return
operator|new
name|MockClientConfigurer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|class|MockClientConfigurer
specifier|private
specifier|static
class|class
name|MockClientConfigurer
extends|extends
name|HttpClientConfigurer
block|{
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|DefaultHttpClient
name|httpClient
parameter_list|,
name|SolrParams
name|config
parameter_list|)
block|{
name|super
operator|.
name|configure
argument_list|(
name|httpClient
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|addRequestInterceptor
argument_list|(
operator|new
name|HttpRequestInterceptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|req
parameter_list|,
name|HttpContext
name|rsp
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
name|req
operator|.
name|addHeader
argument_list|(
literal|"username"
argument_list|,
name|requestUsername
argument_list|)
expr_stmt|;
name|req
operator|.
name|addHeader
argument_list|(
literal|"password"
argument_list|,
name|requestPassword
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
