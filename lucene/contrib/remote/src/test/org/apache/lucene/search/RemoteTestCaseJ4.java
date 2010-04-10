begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|Naming
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|NotBoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|registry
operator|.
name|LocateRegistry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|server
operator|.
name|RMIClientSocketFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|server
operator|.
name|RMIServerSocketFactory
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
name|LuceneTestCaseJ4
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

begin_comment
comment|/**  * Base class for remote tests.  *<p>  * Call {@link #startServer(Searchable)} in a {@link #BeforeClass} annotated method  * to start the server.  * Call {@link #lookupRemote} to get a RemoteSearchable.  */
end_comment

begin_class
DECL|class|RemoteTestCaseJ4
specifier|public
specifier|abstract
class|class
name|RemoteTestCaseJ4
extends|extends
name|LuceneTestCaseJ4
block|{
DECL|field|port
specifier|private
specifier|static
name|int
name|port
decl_stmt|;
DECL|method|startServer
specifier|public
specifier|static
name|void
name|startServer
parameter_list|(
name|Searchable
name|searchable
parameter_list|)
throws|throws
name|Exception
block|{
comment|// publish it
comment|// use our own factories for testing, so we can bind to an ephemeral port.
name|RMIClientSocketFactory
name|clientFactory
init|=
operator|new
name|RMIClientSocketFactory
argument_list|()
block|{
specifier|public
name|Socket
name|createSocket
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Socket
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
return|;
block|}
block|}
decl_stmt|;
class|class
name|TestRMIServerSocketFactory
implements|implements
name|RMIServerSocketFactory
block|{
name|ServerSocket
name|socket
decl_stmt|;
specifier|public
name|ServerSocket
name|createServerSocket
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|socket
operator|=
operator|new
name|ServerSocket
argument_list|(
name|port
argument_list|)
operator|)
return|;
block|}
block|}
empty_stmt|;
name|TestRMIServerSocketFactory
name|serverFactory
init|=
operator|new
name|TestRMIServerSocketFactory
argument_list|()
decl_stmt|;
name|LocateRegistry
operator|.
name|createRegistry
argument_list|(
literal|0
argument_list|,
name|clientFactory
argument_list|,
name|serverFactory
argument_list|)
expr_stmt|;
name|RemoteSearchable
name|impl
init|=
operator|new
name|RemoteSearchable
argument_list|(
name|searchable
argument_list|)
decl_stmt|;
name|port
operator|=
name|serverFactory
operator|.
name|socket
operator|.
name|getLocalPort
argument_list|()
expr_stmt|;
name|Naming
operator|.
name|rebind
argument_list|(
literal|"//localhost:"
operator|+
name|port
operator|+
literal|"/Searchable"
argument_list|,
name|impl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopServer
specifier|public
specifier|static
name|void
name|stopServer
parameter_list|()
block|{
try|try
block|{
name|Naming
operator|.
name|unbind
argument_list|(
literal|"//localhost:"
operator|+
name|port
operator|+
literal|"/Searchable"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|e
parameter_list|)
block|{     }
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{     }
catch|catch
parameter_list|(
name|NotBoundException
name|e
parameter_list|)
block|{     }
block|}
DECL|method|lookupRemote
specifier|public
specifier|static
name|Searchable
name|lookupRemote
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|Searchable
operator|)
name|Naming
operator|.
name|lookup
argument_list|(
literal|"//localhost:"
operator|+
name|port
operator|+
literal|"/Searchable"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

