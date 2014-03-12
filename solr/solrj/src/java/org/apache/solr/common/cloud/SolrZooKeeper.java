begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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
name|concurrent
operator|.
name|CopyOnWriteArraySet
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
name|ClientCnxn
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
name|Watcher
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
name|ZooKeeper
import|;
end_import

begin_comment
comment|// we use this class to expose nasty stuff for tests
end_comment

begin_class
DECL|class|SolrZooKeeper
specifier|public
class|class
name|SolrZooKeeper
extends|extends
name|ZooKeeper
block|{
DECL|field|spawnedThreads
specifier|final
name|Set
argument_list|<
name|Thread
argument_list|>
name|spawnedThreads
init|=
operator|new
name|CopyOnWriteArraySet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// for test debug
comment|//static Map<SolrZooKeeper,Exception> clients = new ConcurrentHashMap<SolrZooKeeper,Exception>();
DECL|method|SolrZooKeeper
specifier|public
name|SolrZooKeeper
parameter_list|(
name|String
name|connectString
parameter_list|,
name|int
name|sessionTimeout
parameter_list|,
name|Watcher
name|watcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|connectString
argument_list|,
name|sessionTimeout
argument_list|,
name|watcher
argument_list|)
expr_stmt|;
comment|//clients.put(this, new RuntimeException());
block|}
DECL|method|getConnection
specifier|public
name|ClientCnxn
name|getConnection
parameter_list|()
block|{
return|return
name|cnxn
return|;
block|}
DECL|method|getSocketAddress
specifier|public
name|SocketAddress
name|getSocketAddress
parameter_list|()
block|{
return|return
name|testableLocalSocketAddress
argument_list|()
return|;
block|}
comment|/**    * Cause this ZooKeeper object to stop receiving from the ZooKeeperServer    * for the given number of milliseconds.    * @param ms the number of milliseconds to pause.    */
DECL|method|pauseCnxn
specifier|public
name|void
name|pauseCnxn
parameter_list|(
specifier|final
name|long
name|ms
parameter_list|)
block|{
specifier|final
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
specifier|final
name|ClientCnxn
name|cnxn
init|=
name|getConnection
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|cnxn
init|)
block|{
try|try
block|{
specifier|final
name|Field
name|sendThreadFld
init|=
name|cnxn
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredField
argument_list|(
literal|"sendThread"
argument_list|)
decl_stmt|;
name|sendThreadFld
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|sendThread
init|=
name|sendThreadFld
operator|.
name|get
argument_list|(
name|cnxn
argument_list|)
decl_stmt|;
if|if
condition|(
name|sendThread
operator|!=
literal|null
condition|)
block|{
name|Method
name|method
init|=
name|sendThread
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"testableCloseSocket"
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|method
operator|.
name|invoke
argument_list|(
name|sendThread
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
comment|// is fine
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Closing Zookeeper send channel failed."
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|ms
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
name|spawnedThreads
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|spawnedThreads
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|InterruptedException
block|{
for|for
control|(
name|Thread
name|t
range|:
name|spawnedThreads
control|)
block|{
if|if
condition|(
name|t
operator|.
name|isAlive
argument_list|()
condition|)
name|t
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//  public static void assertCloses() {
comment|//    if (clients.size()> 0) {
comment|//      Iterator<Exception> stacktraces = clients.values().iterator();
comment|//      Exception cause = null;
comment|//      cause = stacktraces.next();
comment|//      throw new RuntimeException("Found a bad one!", cause);
comment|//    }
comment|//  }
block|}
end_class

end_unit

