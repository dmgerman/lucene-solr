begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|ClientConnectionManager
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
name|conn
operator|.
name|PoolingClientConnectionManager
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
name|SuppressCodecs
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
name|server
operator|.
name|Connector
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
name|server
operator|.
name|Handler
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
name|server
operator|.
name|Server
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
name|server
operator|.
name|bio
operator|.
name|SocketConnector
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
name|server
operator|.
name|nio
operator|.
name|SelectChannelConnector
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
name|server
operator|.
name|session
operator|.
name|HashSessionIdManager
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
name|server
operator|.
name|ssl
operator|.
name|SslSelectChannelConnector
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
name|server
operator|.
name|ssl
operator|.
name|SslSocketConnector
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
name|ssl
operator|.
name|SslContextFactory
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
name|thread
operator|.
name|QueuedThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
literal|"Lucene3x"
argument_list|)
DECL|class|ReplicatorTestCase
specifier|public
specifier|abstract
class|class
name|ReplicatorTestCase
extends|extends
name|LuceneTestCase
block|{
DECL|field|clientConnectionManager
specifier|private
specifier|static
name|ClientConnectionManager
name|clientConnectionManager
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClassReplicatorTestCase
specifier|public
specifier|static
name|void
name|afterClassReplicatorTestCase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|clientConnectionManager
operator|!=
literal|null
condition|)
block|{
name|clientConnectionManager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|clientConnectionManager
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Returns a new {@link Server HTTP Server} instance. To obtain its port, use    * {@link #serverPort(Server)}.    */
DECL|method|newHttpServer
specifier|public
specifier|static
specifier|synchronized
name|Server
name|newHttpServer
parameter_list|(
name|Handler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|Server
name|server
init|=
operator|new
name|Server
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|server
operator|.
name|setHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
specifier|final
name|String
name|connectorName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"tests.jettyConnector"
argument_list|,
literal|"SelectChannel"
argument_list|)
decl_stmt|;
comment|// if this property is true, then jetty will be configured to use SSL
comment|// leveraging the same system properties as java to specify
comment|// the keystore/truststore if they are set
comment|//
comment|// This means we will use the same truststore, keystore (and keys) for
comment|// the server as well as any client actions taken by this JVM in
comment|// talking to that server, but for the purposes of testing that should
comment|// be good enough
specifier|final
name|boolean
name|useSsl
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"tests.jettySsl"
argument_list|)
decl_stmt|;
specifier|final
name|SslContextFactory
name|sslcontext
init|=
operator|new
name|SslContextFactory
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|useSsl
condition|)
block|{
if|if
condition|(
literal|null
operator|!=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|)
condition|)
block|{
name|sslcontext
operator|.
name|setKeyStorePath
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyStore"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|)
condition|)
block|{
name|sslcontext
operator|.
name|setKeyStorePassword
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.keyStorePassword"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|)
condition|)
block|{
name|sslcontext
operator|.
name|setTrustStore
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.trustStore"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|)
condition|)
block|{
name|sslcontext
operator|.
name|setTrustStorePassword
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"javax.net.ssl.trustStorePassword"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sslcontext
operator|.
name|setNeedClientAuth
argument_list|(
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"tests.jettySsl.clientAuth"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Connector
name|connector
decl_stmt|;
specifier|final
name|QueuedThreadPool
name|threadPool
decl_stmt|;
if|if
condition|(
literal|"SelectChannel"
operator|.
name|equals
argument_list|(
name|connectorName
argument_list|)
condition|)
block|{
specifier|final
name|SelectChannelConnector
name|c
init|=
name|useSsl
condition|?
operator|new
name|SslSelectChannelConnector
argument_list|(
name|sslcontext
argument_list|)
else|:
operator|new
name|SelectChannelConnector
argument_list|()
decl_stmt|;
name|c
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|c
operator|.
name|setLowResourcesMaxIdleTime
argument_list|(
literal|1500
argument_list|)
expr_stmt|;
name|connector
operator|=
name|c
expr_stmt|;
name|threadPool
operator|=
operator|(
name|QueuedThreadPool
operator|)
name|c
operator|.
name|getThreadPool
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Socket"
operator|.
name|equals
argument_list|(
name|connectorName
argument_list|)
condition|)
block|{
specifier|final
name|SocketConnector
name|c
init|=
name|useSsl
condition|?
operator|new
name|SslSocketConnector
argument_list|(
name|sslcontext
argument_list|)
else|:
operator|new
name|SocketConnector
argument_list|()
decl_stmt|;
name|c
operator|.
name|setReuseAddress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connector
operator|=
name|c
expr_stmt|;
name|threadPool
operator|=
operator|(
name|QueuedThreadPool
operator|)
name|c
operator|.
name|getThreadPool
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value for system property 'tests.jettyConnector': "
operator|+
name|connectorName
argument_list|)
throw|;
block|}
name|connector
operator|.
name|setPort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|connector
operator|.
name|setHost
argument_list|(
literal|"127.0.0.1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|threadPool
operator|!=
literal|null
condition|)
block|{
name|threadPool
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|setMaxThreads
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|setMaxIdleTimeMs
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|setMaxStopTimeMs
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|setConnectors
argument_list|(
operator|new
name|Connector
index|[]
block|{
name|connector
block|}
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSessionIdManager
argument_list|(
operator|new
name|HashSessionIdManager
argument_list|(
operator|new
name|Random
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|server
return|;
block|}
comment|/** Returns a {@link Server}'s port. */
DECL|method|serverPort
specifier|public
specifier|static
name|int
name|serverPort
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
return|return
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getLocalPort
argument_list|()
return|;
block|}
comment|/** Returns a {@link Server}'s host. */
DECL|method|serverHost
specifier|public
specifier|static
name|String
name|serverHost
parameter_list|(
name|Server
name|server
parameter_list|)
block|{
return|return
name|server
operator|.
name|getConnectors
argument_list|()
index|[
literal|0
index|]
operator|.
name|getHost
argument_list|()
return|;
block|}
comment|/**    * Stops the given HTTP Server instance. This method does its best to guarantee    * that no threads will be left running following this method.    */
DECL|method|stopHttpServer
specifier|public
specifier|static
name|void
name|stopHttpServer
parameter_list|(
name|Server
name|httpServer
parameter_list|)
throws|throws
name|Exception
block|{
name|httpServer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|httpServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a {@link ClientConnectionManager}.    *<p>    *<b>NOTE:</b> do not {@link ClientConnectionManager#shutdown()} this    * connection manager, it will be shutdown automatically after all tests have    * finished.    */
DECL|method|getClientConnectionManager
specifier|public
specifier|static
specifier|synchronized
name|ClientConnectionManager
name|getClientConnectionManager
parameter_list|()
block|{
if|if
condition|(
name|clientConnectionManager
operator|==
literal|null
condition|)
block|{
name|PoolingClientConnectionManager
name|ccm
init|=
operator|new
name|PoolingClientConnectionManager
argument_list|()
decl_stmt|;
name|ccm
operator|.
name|setDefaultMaxPerRoute
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|ccm
operator|.
name|setMaxTotal
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|clientConnectionManager
operator|=
name|ccm
expr_stmt|;
block|}
return|return
name|clientConnectionManager
return|;
block|}
block|}
end_class

end_unit

