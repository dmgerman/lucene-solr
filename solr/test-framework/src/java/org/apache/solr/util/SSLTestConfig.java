begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyManagementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStoreException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|UnrecoverableKeyException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
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
name|conn
operator|.
name|scheme
operator|.
name|Scheme
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
name|conn
operator|.
name|scheme
operator|.
name|SchemeRegistry
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
name|conn
operator|.
name|ssl
operator|.
name|SSLContexts
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
name|conn
operator|.
name|ssl
operator|.
name|SSLSocketFactory
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
name|conn
operator|.
name|ssl
operator|.
name|TrustSelfSignedStrategy
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|SSLConfig
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
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|security
operator|.
name|CertificateUtils
import|;
end_import

begin_class
DECL|class|SSLTestConfig
specifier|public
class|class
name|SSLTestConfig
extends|extends
name|SSLConfig
block|{
DECL|field|TEST_KEYSTORE
specifier|public
specifier|static
name|File
name|TEST_KEYSTORE
init|=
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|File
argument_list|(
name|ExternalPaths
operator|.
name|EXAMPLE_HOME
argument_list|,
literal|"../etc/solrtest.keystore"
argument_list|)
decl_stmt|;
DECL|field|TEST_KEYSTORE_PATH
specifier|private
specifier|static
name|String
name|TEST_KEYSTORE_PATH
init|=
name|TEST_KEYSTORE
operator|!=
literal|null
operator|&&
name|TEST_KEYSTORE
operator|.
name|exists
argument_list|()
condition|?
name|TEST_KEYSTORE
operator|.
name|getAbsolutePath
argument_list|()
else|:
literal|null
decl_stmt|;
DECL|field|TEST_KEYSTORE_PASSWORD
specifier|private
specifier|static
name|String
name|TEST_KEYSTORE_PASSWORD
init|=
literal|"secret"
decl_stmt|;
DECL|field|DEFAULT_CONFIGURER
specifier|private
specifier|static
name|HttpClientConfigurer
name|DEFAULT_CONFIGURER
init|=
operator|new
name|HttpClientConfigurer
argument_list|()
decl_stmt|;
DECL|method|SSLTestConfig
specifier|public
name|SSLTestConfig
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|SSLTestConfig
specifier|public
name|SSLTestConfig
parameter_list|(
name|boolean
name|useSSL
parameter_list|,
name|boolean
name|clientAuth
parameter_list|)
block|{
name|this
argument_list|(
name|useSSL
argument_list|,
name|clientAuth
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
block|}
DECL|method|SSLTestConfig
specifier|public
name|SSLTestConfig
parameter_list|(
name|boolean
name|useSSL
parameter_list|,
name|boolean
name|clientAuth
parameter_list|,
name|String
name|keyStore
parameter_list|,
name|String
name|keyStorePassword
parameter_list|,
name|String
name|trustStore
parameter_list|,
name|String
name|trustStorePassword
parameter_list|)
block|{
name|super
argument_list|(
name|useSSL
argument_list|,
name|clientAuth
argument_list|,
name|keyStore
argument_list|,
name|keyStorePassword
argument_list|,
name|trustStore
argument_list|,
name|trustStorePassword
argument_list|)
expr_stmt|;
block|}
comment|/**    * Will provide an HttpClientConfigurer for SSL support (adds https and    * removes http schemes) is SSL is enabled, otherwise return the default    * configurer    */
DECL|method|getHttpClientConfigurer
specifier|public
name|HttpClientConfigurer
name|getHttpClientConfigurer
parameter_list|()
block|{
return|return
name|isSSLMode
argument_list|()
condition|?
operator|new
name|SSLHttpClientConfigurer
argument_list|()
else|:
name|DEFAULT_CONFIGURER
return|;
block|}
comment|/**    * Builds a new SSLContext with the given configuration and allows the uses of    * self-signed certificates during testing.    */
DECL|method|buildSSLContext
specifier|protected
name|SSLContext
name|buildSSLContext
parameter_list|()
throws|throws
name|KeyManagementException
throws|,
name|UnrecoverableKeyException
throws|,
name|NoSuchAlgorithmException
throws|,
name|KeyStoreException
block|{
return|return
name|SSLContexts
operator|.
name|custom
argument_list|()
operator|.
name|loadKeyMaterial
argument_list|(
name|buildKeyStore
argument_list|(
name|getKeyStore
argument_list|()
argument_list|,
name|getKeyStorePassword
argument_list|()
argument_list|)
argument_list|,
name|getKeyStorePassword
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
operator|.
name|loadTrustMaterial
argument_list|(
name|buildKeyStore
argument_list|(
name|getTrustStore
argument_list|()
argument_list|,
name|getTrustStorePassword
argument_list|()
argument_list|)
argument_list|,
operator|new
name|TrustSelfSignedStrategy
argument_list|()
argument_list|)
operator|.
name|setSecureRandom
argument_list|(
operator|new
name|NullSecureRandom
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|buildKeyStore
specifier|protected
specifier|static
name|KeyStore
name|buildKeyStore
parameter_list|(
name|String
name|keyStoreLocation
parameter_list|,
name|String
name|password
parameter_list|)
block|{
try|try
block|{
return|return
name|CertificateUtils
operator|.
name|getKeyStore
argument_list|(
literal|null
argument_list|,
name|keyStoreLocation
argument_list|,
literal|"JKS"
argument_list|,
literal|null
argument_list|,
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to build KeyStore from file: "
operator|+
name|keyStoreLocation
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|class|SSLHttpClientConfigurer
specifier|private
class|class
name|SSLHttpClientConfigurer
extends|extends
name|HttpClientConfigurer
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|configure
specifier|protected
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
name|SchemeRegistry
name|registry
init|=
name|httpClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|getSchemeRegistry
argument_list|()
decl_stmt|;
comment|// Make sure no tests cheat by using HTTP
name|registry
operator|.
name|unregister
argument_list|(
literal|"http"
argument_list|)
expr_stmt|;
try|try
block|{
name|registry
operator|.
name|register
argument_list|(
operator|new
name|Scheme
argument_list|(
literal|"https"
argument_list|,
literal|443
argument_list|,
operator|new
name|SSLSocketFactory
argument_list|(
name|buildSSLContext
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyManagementException
decl||
name|UnrecoverableKeyException
decl||
name|NoSuchAlgorithmException
decl||
name|KeyStoreException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to setup https scheme for HTTPClient to test SSL."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|setSSLSystemProperties
specifier|public
specifier|static
name|void
name|setSSLSystemProperties
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|,
name|TEST_KEYSTORE_PATH
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|,
name|TEST_KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
block|}
DECL|method|clearSSLSystemProperties
specifier|public
specifier|static
name|void
name|clearSSLSystemProperties
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|)
expr_stmt|;
block|}
comment|/**    * We use this to avoid SecureRandom blocking issues due to too many    * instances or not enough random entropy. Tests do not need secure SSL.    */
DECL|class|NullSecureRandom
specifier|private
specifier|static
class|class
name|NullSecureRandom
extends|extends
name|SecureRandom
block|{
DECL|method|generateSeed
specifier|public
name|byte
index|[]
name|generateSeed
parameter_list|(
name|int
name|numBytes
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
DECL|method|nextBytes
specifier|synchronized
specifier|public
name|void
name|nextBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{     }
DECL|method|setSeed
specifier|synchronized
specifier|public
name|void
name|setSeed
parameter_list|(
name|byte
index|[]
name|seed
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

