begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|ExecutorService
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
name|Future
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
name|TimeUnit
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
name|TimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|SolrTestCaseJ4
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
name|cloud
operator|.
name|SolrZkClient
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
name|ExecutorUtil
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
name|SolrjNamedThreadFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
DECL|class|DistributedQueueTest
specifier|public
class|class
name|DistributedQueueTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|UTF8
specifier|private
specifier|static
specifier|final
name|Charset
name|UTF8
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|zkServer
specifier|protected
name|ZkTestServer
name|zkServer
decl_stmt|;
DECL|field|zkClient
specifier|protected
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|executor
specifier|protected
name|ExecutorService
name|executor
init|=
name|ExecutorUtil
operator|.
name|newMDCAwareSingleThreadExecutor
argument_list|(
operator|new
name|SolrjNamedThreadFactory
argument_list|(
literal|"dqtest-"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Before
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|setupZk
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDistributedQueue
specifier|public
name|void
name|testDistributedQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dqZNode
init|=
literal|"/distqueue/test"
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|"hello world"
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
decl_stmt|;
name|DistributedQueue
name|dq
init|=
name|makeDistributedQueue
argument_list|(
name|dqZNode
argument_list|)
decl_stmt|;
comment|// basic ops
name|assertNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|dq
operator|.
name|remove
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"NoSuchElementException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|expected
parameter_list|)
block|{
comment|// expected
block|}
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|500
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|dq
operator|.
name|remove
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|dq
operator|.
name|take
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
comment|// waits for data
name|assertNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|dq
operator|.
name|peek
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// wait until data is definitely there before calling remove
name|assertArrayEquals
argument_list|(
name|dq
operator|.
name|remove
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|// should block until the background thread makes the offer
operator|(
operator|new
name|QueueChangerThread
argument_list|(
name|dq
argument_list|,
literal|1000
argument_list|)
operator|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dq
operator|.
name|remove
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|// timeout scenario ... background thread won't offer until long after the peek times out
name|QueueChangerThread
name|qct
init|=
operator|new
name|QueueChangerThread
argument_list|(
name|dq
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|qct
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
name|qct
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDistributedQueueBlocking
specifier|public
name|void
name|testDistributedQueueBlocking
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dqZNode
init|=
literal|"/distqueue/test"
decl_stmt|;
name|String
name|testData
init|=
literal|"hello world"
decl_stmt|;
name|DistributedQueue
name|dq
init|=
name|makeDistributedQueue
argument_list|(
name|dqZNode
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|Future
argument_list|<
name|String
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
operator|new
name|String
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|true
argument_list|)
argument_list|,
name|UTF8
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"TimeoutException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|expected
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|future
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Ultimately trips the watcher, triggering child refresh
name|dq
operator|.
name|offer
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testData
argument_list|,
name|future
operator|.
name|get
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
comment|// After draining the queue, a watcher should be set.
name|assertNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|forceSessionExpire
argument_list|()
expr_stmt|;
comment|// Session expiry should have fired the watcher.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Rerun the earlier test make sure updates are still seen, post reconnection.
name|future
operator|=
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
operator|new
name|String
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|true
argument_list|)
argument_list|,
name|UTF8
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|future
operator|.
name|get
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"TimeoutException expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|expected
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|future
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Ultimately trips the watcher, triggering child refresh
name|dq
operator|.
name|offer
argument_list|(
name|testData
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testData
argument_list|,
name|future
operator|.
name|get
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|poll
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeakChildWatcher
specifier|public
name|void
name|testLeakChildWatcher
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dqZNode
init|=
literal|"/distqueue/test"
decl_stmt|;
name|DistributedQueue
name|dq
init|=
name|makeDistributedQueue
argument_list|(
name|dqZNode
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dq
operator|.
name|peekElements
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|s1
lambda|->
literal|true
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dq
operator|.
name|peekElements
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|s1
lambda|->
literal|true
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
literal|"hello world"
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
comment|// synchronously available
comment|// dirty and watcher state indeterminate here, race with watcher
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
comment|// watcher should have fired now
name|assertNotNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|peekElements
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|s
lambda|->
literal|true
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|watcherCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dq
operator|.
name|isDirty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocallyOffer
specifier|public
name|void
name|testLocallyOffer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dqZNode
init|=
literal|"/distqueue/test"
decl_stmt|;
name|DistributedQueue
name|dq
init|=
name|makeDistributedQueue
argument_list|(
name|dqZNode
argument_list|)
decl_stmt|;
name|dq
operator|.
name|peekElements
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|s
lambda|->
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
decl_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|dq
operator|.
name|peek
argument_list|()
argument_list|)
expr_stmt|;
name|dq
operator|.
name|poll
argument_list|()
expr_stmt|;
name|dq
operator|.
name|peekElements
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|s
lambda|->
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPeekElements
specifier|public
name|void
name|testPeekElements
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dqZNode
init|=
literal|"/distqueue/test"
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|"hello world"
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
decl_stmt|;
name|DistributedQueue
name|dq
init|=
name|makeDistributedQueue
argument_list|(
name|dqZNode
argument_list|)
decl_stmt|;
comment|// Populate with data.
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|Predicate
argument_list|<
name|String
argument_list|>
name|alwaysTrue
init|=
name|s
lambda|->
literal|true
decl_stmt|;
name|Predicate
argument_list|<
name|String
argument_list|>
name|alwaysFalse
init|=
name|s
lambda|->
literal|false
decl_stmt|;
comment|// Should be able to get 0, 1, 2, or 3 instantly
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
literal|3
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|dq
operator|.
name|peekElements
argument_list|(
name|i
argument_list|,
literal|0
argument_list|,
name|alwaysTrue
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Asking for more should return only 3.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|dq
operator|.
name|peekElements
argument_list|(
literal|4
argument_list|,
literal|0
argument_list|,
name|alwaysTrue
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// If we filter everything out, we should block for the full time.
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dq
operator|.
name|peekElements
argument_list|(
literal|4
argument_list|,
literal|1000
argument_list|,
name|alwaysFalse
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|>=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
comment|// If someone adds a new matching element while we're waiting, we should return immediately.
name|executor
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dq
operator|.
name|peekElements
argument_list|(
literal|4
argument_list|,
literal|2000
argument_list|,
name|child
lambda|->
block|{
comment|// The 4th element in the queue will end with a "3".
return|return
name|child
operator|.
name|endsWith
argument_list|(
literal|"3"
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|<
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|>=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
literal|250
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|forceSessionExpire
specifier|private
name|void
name|forceSessionExpire
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|long
name|sessionId
init|=
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
decl_stmt|;
name|zkServer
operator|.
name|expire
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|waitForDisconnected
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|waitForConnected
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|zkClient
operator|.
name|isConnected
argument_list|()
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sessionId
operator|==
name|zkClient
operator|.
name|getSolrZooKeeper
argument_list|()
operator|.
name|getSessionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|makeDistributedQueue
specifier|protected
name|DistributedQueue
name|makeDistributedQueue
parameter_list|(
name|String
name|dqZNode
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|DistributedQueue
argument_list|(
name|zkClient
argument_list|,
name|setupNewDistributedQueueZNode
argument_list|(
name|dqZNode
argument_list|)
argument_list|)
return|;
block|}
DECL|class|QueueChangerThread
specifier|private
specifier|static
class|class
name|QueueChangerThread
extends|extends
name|Thread
block|{
DECL|field|dq
name|DistributedQueue
name|dq
decl_stmt|;
DECL|field|waitBeforeOfferMs
name|long
name|waitBeforeOfferMs
decl_stmt|;
DECL|method|QueueChangerThread
name|QueueChangerThread
parameter_list|(
name|DistributedQueue
name|dq
parameter_list|,
name|long
name|waitBeforeOfferMs
parameter_list|)
block|{
name|this
operator|.
name|dq
operator|=
name|dq
expr_stmt|;
name|this
operator|.
name|waitBeforeOfferMs
operator|=
name|waitBeforeOfferMs
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitBeforeOfferMs
argument_list|)
expr_stmt|;
name|dq
operator|.
name|offer
argument_list|(
name|getName
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF8
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// do nothing
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|exc
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|setupNewDistributedQueueZNode
specifier|protected
name|String
name|setupNewDistributedQueueZNode
parameter_list|(
name|String
name|znodePath
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|zkClient
operator|.
name|exists
argument_list|(
literal|"/"
argument_list|,
literal|true
argument_list|)
condition|)
name|zkClient
operator|.
name|makePath
argument_list|(
literal|"/"
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|znodePath
argument_list|,
literal|true
argument_list|)
condition|)
name|zkClient
operator|.
name|clean
argument_list|(
name|znodePath
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|znodePath
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|znodePath
return|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{     }
name|closeZk
argument_list|()
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|setupZk
specifier|protected
name|void
name|setupZk
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkClientTimeout"
argument_list|,
literal|"8000"
argument_list|)
expr_stmt|;
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"zkHost"
argument_list|,
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|)
expr_stmt|;
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|()
argument_list|,
name|AbstractZkTestCase
operator|.
name|TIMEOUT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|zkClient
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|closeZk
specifier|protected
name|void
name|closeZk
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

