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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|locks
operator|.
name|Condition
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
name|locks
operator|.
name|ReentrantLock
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
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
name|annotations
operator|.
name|VisibleForTesting
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
name|base
operator|.
name|Preconditions
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
name|SolrException
operator|.
name|ErrorCode
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
name|cloud
operator|.
name|ZkCmdExecutor
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
name|Pair
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
name|CreateMode
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
name|KeeperException
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
name|WatchedEvent
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
comment|/**  * A distributed queue.  */
end_comment

begin_class
DECL|class|DistributedQueue
specifier|public
class|class
name|DistributedQueue
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
DECL|field|PREFIX
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"qn-"
decl_stmt|;
comment|/**    * Theory of operation:    *<p>    * Under ordinary circumstances we neither watch nor poll for children in ZK.    * Instead we keep an in-memory list of known child names.  When the in-memory    * list is exhausted, we then fetch from ZK.    *<p>    * We only bother setting a child watcher when the queue has no children in ZK.    */
DECL|field|_IMPLEMENTATION_NOTES
specifier|private
specifier|static
specifier|final
name|Object
name|_IMPLEMENTATION_NOTES
init|=
literal|null
decl_stmt|;
DECL|field|dir
specifier|final
name|String
name|dir
decl_stmt|;
DECL|field|zookeeper
specifier|final
name|SolrZkClient
name|zookeeper
decl_stmt|;
DECL|field|stats
specifier|final
name|Overseer
operator|.
name|Stats
name|stats
decl_stmt|;
comment|/**    * A lock that guards all of the mutable state that follows.    */
DECL|field|updateLock
specifier|private
specifier|final
name|ReentrantLock
name|updateLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/**    * Contains the last set of children fetched from ZK. Elements are removed from the head of    * this in-memory set as they are consumed from the queue.  Due to the distributed nature    * of the queue, elements may appear in this set whose underlying nodes have been consumed in ZK.    * Therefore, methods like {@link #peek()} have to double-check actual node existence, and methods    * like {@link #poll()} must resolve any races by attempting to delete the underlying node.    */
DECL|field|knownChildren
specifier|private
name|TreeSet
argument_list|<
name|String
argument_list|>
name|knownChildren
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Used to wait on ZK changes to the child list; you must hold {@link #updateLock} before waiting on this condition.    */
DECL|field|changed
specifier|private
specifier|final
name|Condition
name|changed
init|=
name|updateLock
operator|.
name|newCondition
argument_list|()
decl_stmt|;
comment|/**    * If non-null, the last watcher to listen for child changes.  If null, the in-memory contents are dirty.    */
DECL|field|lastWatcher
specifier|private
name|ChildWatcher
name|lastWatcher
init|=
literal|null
decl_stmt|;
DECL|method|DistributedQueue
specifier|public
name|DistributedQueue
parameter_list|(
name|SolrZkClient
name|zookeeper
parameter_list|,
name|String
name|dir
parameter_list|)
block|{
name|this
argument_list|(
name|zookeeper
argument_list|,
name|dir
argument_list|,
operator|new
name|Overseer
operator|.
name|Stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DistributedQueue
specifier|public
name|DistributedQueue
parameter_list|(
name|SolrZkClient
name|zookeeper
parameter_list|,
name|String
name|dir
parameter_list|,
name|Overseer
operator|.
name|Stats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|ZkCmdExecutor
name|cmdExecutor
init|=
operator|new
name|ZkCmdExecutor
argument_list|(
name|zookeeper
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|cmdExecutor
operator|.
name|ensureExists
argument_list|(
name|dir
argument_list|,
name|zookeeper
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|zookeeper
operator|=
name|zookeeper
expr_stmt|;
name|this
operator|.
name|stats
operator|=
name|stats
expr_stmt|;
block|}
comment|/**    * Returns the data at the first element of the queue, or null if the queue is    * empty.    *    * @return data at the first element of the queue, or null.    */
DECL|method|peek
specifier|public
name|byte
index|[]
name|peek
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Timer
operator|.
name|Context
name|time
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_peek"
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|firstElement
argument_list|()
return|;
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the data at the first element of the queue, or null if the queue is    * empty and block is false.    *    * @param block if true, blocks until an element enters the queue    * @return data at the first element of the queue, or null.    */
DECL|method|peek
specifier|public
name|byte
index|[]
name|peek
parameter_list|(
name|boolean
name|block
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|block
condition|?
name|peek
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
else|:
name|peek
argument_list|()
return|;
block|}
comment|/**    * Returns the data at the first element of the queue, or null if the queue is    * empty after wait ms.    *    * @param wait max wait time in ms.    * @return data at the first element of the queue, or null.    */
DECL|method|peek
specifier|public
name|byte
index|[]
name|peek
parameter_list|(
name|long
name|wait
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|wait
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Timer
operator|.
name|Context
name|time
decl_stmt|;
if|if
condition|(
name|wait
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|time
operator|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_peek_wait_forever"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|time
operator|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_peek_wait"
operator|+
name|wait
argument_list|)
expr_stmt|;
block|}
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
name|long
name|waitNanos
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
name|wait
argument_list|)
decl_stmt|;
while|while
condition|(
name|waitNanos
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|result
init|=
name|firstElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|waitNanos
operator|=
name|changed
operator|.
name|awaitNanos
argument_list|(
name|waitNanos
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Attempts to remove the head of the queue and return it. Returns null if the    * queue is empty.    *    * @return Head of the queue or null.    */
DECL|method|poll
specifier|public
name|byte
index|[]
name|poll
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Timer
operator|.
name|Context
name|time
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_poll"
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|removeFirst
argument_list|()
return|;
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Attempts to remove the head of the queue and return it.    *    * @return The former head of the queue    */
DECL|method|remove
specifier|public
name|byte
index|[]
name|remove
parameter_list|()
throws|throws
name|NoSuchElementException
throws|,
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Timer
operator|.
name|Context
name|time
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_remove"
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|result
init|=
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|result
return|;
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Removes the head of the queue and returns it, blocks until it succeeds.    *    * @return The former head of the queue    */
DECL|method|take
specifier|public
name|byte
index|[]
name|take
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
comment|// Same as for element. Should refactor this.
name|Timer
operator|.
name|Context
name|timer
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_take"
argument_list|)
decl_stmt|;
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|byte
index|[]
name|result
init|=
name|removeFirst
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|changed
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|timer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Inserts data into queue.  Successfully calling this method does NOT guarantee    * that the element will be immediately available in the in-memory queue. In particular,    * calling this method on an empty queue will not necessarily cause {@link #poll()} to    * return the offered element.  Use a blocking method if you must wait for the offered    * element to become visible.    */
DECL|method|offer
specifier|public
name|void
name|offer
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|Timer
operator|.
name|Context
name|time
init|=
name|stats
operator|.
name|time
argument_list|(
name|dir
operator|+
literal|"_offer"
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
comment|// We don't need to explicitly set isDirty here; if there is a watcher, it will
comment|// see the update and set the bit itself; if there is no watcher we can defer
comment|// the update anyway.
name|zookeeper
operator|.
name|create
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|PREFIX
argument_list|,
name|data
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT_SEQUENTIAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
try|try
block|{
name|zookeeper
operator|.
name|create
argument_list|(
name|dir
argument_list|,
operator|new
name|byte
index|[
literal|0
index|]
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NodeExistsException
name|ne
parameter_list|)
block|{
comment|// someone created it
block|}
block|}
block|}
block|}
finally|finally
block|{
name|time
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getStats
specifier|public
name|Overseer
operator|.
name|Stats
name|getStats
parameter_list|()
block|{
return|return
name|stats
return|;
block|}
comment|/**    * Returns the name if the first known child node, or {@code null} if the queue is empty.    * This is the only place {@link #knownChildren} is ever updated!    * The caller must double check that the actual node still exists, since the in-memory    * list is inherently stale.    */
DECL|method|firstChild
specifier|private
name|String
name|firstChild
parameter_list|(
name|boolean
name|remove
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
comment|// If we're not in a dirty state, and we have in-memory children, return from in-memory.
if|if
condition|(
name|lastWatcher
operator|!=
literal|null
operator|&&
operator|!
name|knownChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|remove
condition|?
name|knownChildren
operator|.
name|pollFirst
argument_list|()
else|:
name|knownChildren
operator|.
name|first
argument_list|()
return|;
block|}
comment|// Try to fetch an updated list of children from ZK.
name|ChildWatcher
name|newWatcher
init|=
operator|new
name|ChildWatcher
argument_list|()
decl_stmt|;
name|knownChildren
operator|=
name|fetchZkChildren
argument_list|(
name|newWatcher
argument_list|)
expr_stmt|;
name|lastWatcher
operator|=
name|newWatcher
expr_stmt|;
comment|// only set after fetchZkChildren returns successfully
if|if
condition|(
name|knownChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|changed
operator|.
name|signalAll
argument_list|()
expr_stmt|;
return|return
name|remove
condition|?
name|knownChildren
operator|.
name|pollFirst
argument_list|()
else|:
name|knownChildren
operator|.
name|first
argument_list|()
return|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return the current set of children from ZK; does not change internal state.    */
DECL|method|fetchZkChildren
name|TreeSet
argument_list|<
name|String
argument_list|>
name|fetchZkChildren
parameter_list|(
name|Watcher
name|watcher
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|TreeSet
argument_list|<
name|String
argument_list|>
name|orderedChildren
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|childNames
init|=
name|zookeeper
operator|.
name|getChildren
argument_list|(
name|dir
argument_list|,
name|watcher
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|stats
operator|.
name|setQueueLength
argument_list|(
name|childNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|childName
range|:
name|childNames
control|)
block|{
comment|// Check format
if|if
condition|(
operator|!
name|childName
operator|.
name|regionMatches
argument_list|(
literal|0
argument_list|,
name|PREFIX
argument_list|,
literal|0
argument_list|,
name|PREFIX
operator|.
name|length
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found child node with improper name: "
operator|+
name|childName
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|orderedChildren
operator|.
name|add
argument_list|(
name|childName
argument_list|)
expr_stmt|;
block|}
return|return
name|orderedChildren
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
name|zookeeper
operator|.
name|makePath
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// go back to the loop and try again
block|}
block|}
block|}
comment|/**    * Return the currently-known set of elements, using child names from memory. If no children are found, or no    * children pass {@code acceptFilter}, waits up to {@code waitMillis} for at least one child to become available.    *<p/>    * Package-private to support {@link OverseerTaskQueue} specifically.    */
DECL|method|peekElements
name|Collection
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|peekElements
parameter_list|(
name|int
name|max
parameter_list|,
name|long
name|waitMillis
parameter_list|,
name|Predicate
argument_list|<
name|String
argument_list|>
name|acceptFilter
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|foundChildren
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|waitNanos
init|=
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toNanos
argument_list|(
name|waitMillis
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// Trigger a fetch if needed.
name|firstChild
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|String
name|child
range|:
name|knownChildren
control|)
block|{
if|if
condition|(
name|acceptFilter
operator|.
name|test
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|foundChildren
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|foundChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|waitNanos
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
name|waitNanos
operator|=
name|changed
operator|.
name|awaitNanos
argument_list|(
name|waitNanos
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundChildren
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
comment|// Technically we could restart the method if we fail to actually obtain any valid children
comment|// from ZK, but this is a super rare case, and the latency of the ZK fetches would require
comment|// much more sophisticated waitNanos tracking.
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|foundChildren
control|)
block|{
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|>=
name|max
condition|)
block|{
break|break;
block|}
try|try
block|{
name|byte
index|[]
name|data
init|=
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|child
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|Pair
argument_list|<>
argument_list|(
name|child
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// Another client deleted the node first, remove the in-memory and continue.
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
name|knownChildren
operator|.
name|remove
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**    * Return the head of the queue without modifying the queue.    *    * @return the data at the head of the queue.    */
DECL|method|firstElement
specifier|private
name|byte
index|[]
name|firstElement
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|firstChild
init|=
name|firstChild
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstChild
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
return|return
name|zookeeper
operator|.
name|getData
argument_list|(
name|dir
operator|+
literal|"/"
operator|+
name|firstChild
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// Another client deleted the node first, remove the in-memory and retry.
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
name|knownChildren
operator|.
name|remove
argument_list|(
name|firstChild
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|removeFirst
specifier|private
name|byte
index|[]
name|removeFirst
parameter_list|()
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|firstChild
init|=
name|firstChild
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstChild
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|String
name|path
init|=
name|dir
operator|+
literal|"/"
operator|+
name|firstChild
decl_stmt|;
name|byte
index|[]
name|result
init|=
name|zookeeper
operator|.
name|getData
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|zookeeper
operator|.
name|delete
argument_list|(
name|path
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|KeeperException
operator|.
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// Another client deleted the node first, remove the in-memory and retry.
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
name|knownChildren
operator|.
name|remove
argument_list|(
name|firstChild
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|hasWatcher
annotation|@
name|VisibleForTesting
name|boolean
name|hasWatcher
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|updateLock
operator|.
name|lockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|lastWatcher
operator|!=
literal|null
return|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ChildWatcher
specifier|private
class|class
name|ChildWatcher
implements|implements
name|Watcher
block|{
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|WatchedEvent
name|event
parameter_list|)
block|{
comment|// session events are not change events, and do not remove the watcher; except for Expired
if|if
condition|(
name|Event
operator|.
name|EventType
operator|.
name|None
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
operator|!
name|Event
operator|.
name|KeeperState
operator|.
name|Expired
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
name|updateLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// this watcher is automatically cleared when fired
if|if
condition|(
name|lastWatcher
operator|==
name|this
condition|)
block|{
name|lastWatcher
operator|=
literal|null
expr_stmt|;
block|}
comment|// optimistically signal any waiters that the queue may not be empty now, so they can wake up and retry
name|changed
operator|.
name|signalAll
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|updateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

