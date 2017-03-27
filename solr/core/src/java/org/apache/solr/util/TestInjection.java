begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

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
name|Timer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimerTask
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|search
operator|.
name|IndexSearcher
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
name|HttpSolrClient
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
name|request
operator|.
name|QueryRequest
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
name|cloud
operator|.
name|ZkController
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
name|NonExistentCoreException
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
name|Replica
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
name|CommonParams
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
name|ModifiableSolrParams
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
name|NamedList
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SuppressForbidden
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
name|core
operator|.
name|CoreContainer
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
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|ReplicationHandler
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
name|search
operator|.
name|SolrIndexSearcher
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
name|update
operator|.
name|SolrIndexWriter
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|ReplicationHandler
operator|.
name|CMD_DETAILS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|ReplicationHandler
operator|.
name|COMMAND
import|;
end_import

begin_comment
comment|/**  * Allows random faults to be injected in running code during test runs.  *   * Set static strings to "true" or "false" or "true:60" for true 60% of the time.  *   * All methods are No-Ops unless<code>LuceneTestCase</code> is loadable via the ClassLoader used   * to load this class.<code>LuceneTestCase.random()</code> is used as the source of all entropy.  *   * @lucene.internal  */
end_comment

begin_class
DECL|class|TestInjection
specifier|public
class|class
name|TestInjection
block|{
DECL|class|TestShutdownFailError
specifier|public
specifier|static
class|class
name|TestShutdownFailError
extends|extends
name|OutOfMemoryError
block|{
DECL|method|TestShutdownFailError
specifier|public
name|TestShutdownFailError
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
DECL|field|ENABLED_PERCENT
specifier|private
specifier|static
specifier|final
name|Pattern
name|ENABLED_PERCENT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(true|false)(?:\\:(\\d+))?$"
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
argument_list|)
decl_stmt|;
DECL|field|LUCENE_TEST_CASE_FQN
specifier|private
specifier|static
specifier|final
name|String
name|LUCENE_TEST_CASE_FQN
init|=
literal|"org.apache.lucene.util.LuceneTestCase"
decl_stmt|;
comment|/**     * If null, then we are not being run as part of a test, and all TestInjection events should be No-Ops.    * If non-null, then this class should be used for accessing random entropy    * @see #random    */
DECL|field|LUCENE_TEST_CASE
specifier|private
specifier|static
specifier|final
name|Class
name|LUCENE_TEST_CASE
decl_stmt|;
static|static
block|{
name|Class
name|nonFinalTemp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ClassLoader
name|classLoader
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|nonFinalTemp
operator|=
name|classLoader
operator|.
name|loadClass
argument_list|(
name|LUCENE_TEST_CASE_FQN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"TestInjection methods will all be No-Ops since LuceneTestCase not found"
argument_list|)
expr_stmt|;
block|}
name|LUCENE_TEST_CASE
operator|=
name|nonFinalTemp
expr_stmt|;
block|}
comment|/**    * Returns a random to be used by the current thread if available, otherwise    * returns null.    * @see #LUCENE_TEST_CASE    */
DECL|method|random
specifier|static
name|Random
name|random
parameter_list|()
block|{
comment|// non-private for testing
if|if
condition|(
literal|null
operator|==
name|LUCENE_TEST_CASE
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
try|try
block|{
name|Method
name|randomMethod
init|=
name|LUCENE_TEST_CASE
operator|.
name|getMethod
argument_list|(
literal|"random"
argument_list|)
decl_stmt|;
return|return
operator|(
name|Random
operator|)
name|randomMethod
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to use reflection to invoke LuceneTestCase.random()"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|nonGracefullClose
specifier|public
specifier|static
name|String
name|nonGracefullClose
init|=
literal|null
decl_stmt|;
DECL|field|failReplicaRequests
specifier|public
specifier|static
name|String
name|failReplicaRequests
init|=
literal|null
decl_stmt|;
DECL|field|failUpdateRequests
specifier|public
specifier|static
name|String
name|failUpdateRequests
init|=
literal|null
decl_stmt|;
DECL|field|nonExistentCoreExceptionAfterUnload
specifier|public
specifier|static
name|String
name|nonExistentCoreExceptionAfterUnload
init|=
literal|null
decl_stmt|;
DECL|field|updateLogReplayRandomPause
specifier|public
specifier|static
name|String
name|updateLogReplayRandomPause
init|=
literal|null
decl_stmt|;
DECL|field|updateRandomPause
specifier|public
specifier|static
name|String
name|updateRandomPause
init|=
literal|null
decl_stmt|;
DECL|field|prepRecoveryOpPauseForever
specifier|public
specifier|static
name|String
name|prepRecoveryOpPauseForever
init|=
literal|null
decl_stmt|;
DECL|field|randomDelayInCoreCreation
specifier|public
specifier|static
name|String
name|randomDelayInCoreCreation
init|=
literal|null
decl_stmt|;
DECL|field|randomDelayMaxInCoreCreationInSec
specifier|public
specifier|static
name|int
name|randomDelayMaxInCoreCreationInSec
init|=
literal|10
decl_stmt|;
DECL|field|splitFailureBeforeReplicaCreation
specifier|public
specifier|static
name|String
name|splitFailureBeforeReplicaCreation
init|=
literal|null
decl_stmt|;
DECL|field|waitForReplicasInSync
specifier|public
specifier|static
name|String
name|waitForReplicasInSync
init|=
literal|"true:60"
decl_stmt|;
DECL|field|timers
specifier|private
specifier|static
name|Set
argument_list|<
name|Timer
argument_list|>
name|timers
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Timer
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|countPrepRecoveryOpPauseForever
specifier|private
specifier|static
name|AtomicInteger
name|countPrepRecoveryOpPauseForever
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|reset
specifier|public
specifier|static
name|void
name|reset
parameter_list|()
block|{
name|nonGracefullClose
operator|=
literal|null
expr_stmt|;
name|failReplicaRequests
operator|=
literal|null
expr_stmt|;
name|failUpdateRequests
operator|=
literal|null
expr_stmt|;
name|nonExistentCoreExceptionAfterUnload
operator|=
literal|null
expr_stmt|;
name|updateLogReplayRandomPause
operator|=
literal|null
expr_stmt|;
name|updateRandomPause
operator|=
literal|null
expr_stmt|;
name|randomDelayInCoreCreation
operator|=
literal|null
expr_stmt|;
name|splitFailureBeforeReplicaCreation
operator|=
literal|null
expr_stmt|;
name|prepRecoveryOpPauseForever
operator|=
literal|null
expr_stmt|;
name|countPrepRecoveryOpPauseForever
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Timer
name|timer
range|:
name|timers
control|)
block|{
name|timer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|injectRandomDelayInCoreCreation
specifier|public
specifier|static
name|boolean
name|injectRandomDelayInCoreCreation
parameter_list|()
block|{
if|if
condition|(
name|randomDelayInCoreCreation
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|randomDelayInCoreCreation
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
name|int
name|delay
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|randomDelayMaxInCoreCreationInSec
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Inject random core creation delay of {}s"
argument_list|,
name|delay
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
operator|*
literal|1000
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectNonGracefullClose
specifier|public
specifier|static
name|boolean
name|injectNonGracefullClose
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
operator|&&
name|nonGracefullClose
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|nonGracefullClose
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|TestShutdownFailError
argument_list|(
literal|"Test exception for non graceful close"
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|Thread
name|cthread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|TimerTask
name|task
init|=
operator|new
name|TimerTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// as long as places that catch interruptedexception reset that
comment|// interrupted status,
comment|// we should only need to do it once
try|try
block|{
comment|// call random() again to get the correct one for this thread
name|Random
name|taskRand
init|=
name|random
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|taskRand
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                              }
name|cthread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|timers
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Timer
name|timer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
name|timers
operator|.
name|add
argument_list|(
name|timer
argument_list|)
expr_stmt|;
name|timer
operator|.
name|schedule
argument_list|(
name|task
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectFailReplicaRequests
specifier|public
specifier|static
name|boolean
name|injectFailReplicaRequests
parameter_list|()
block|{
if|if
condition|(
name|failReplicaRequests
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|failReplicaRequests
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Random test update fail"
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectFailUpdateRequests
specifier|public
specifier|static
name|boolean
name|injectFailUpdateRequests
parameter_list|()
block|{
if|if
condition|(
name|failUpdateRequests
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|failUpdateRequests
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Random test update fail"
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectNonExistentCoreExceptionAfterUnload
specifier|public
specifier|static
name|boolean
name|injectNonExistentCoreExceptionAfterUnload
parameter_list|(
name|String
name|cname
parameter_list|)
block|{
if|if
condition|(
name|nonExistentCoreExceptionAfterUnload
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|nonExistentCoreExceptionAfterUnload
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
throw|throw
operator|new
name|NonExistentCoreException
argument_list|(
literal|"Core not found to unload: "
operator|+
name|cname
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectUpdateLogReplayRandomPause
specifier|public
specifier|static
name|boolean
name|injectUpdateLogReplayRandomPause
parameter_list|()
block|{
if|if
condition|(
name|updateLogReplayRandomPause
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|updateLogReplayRandomPause
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
name|long
name|rndTime
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"inject random log replay delay of {}ms"
argument_list|,
name|rndTime
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rndTime
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectUpdateRandomPause
specifier|public
specifier|static
name|boolean
name|injectUpdateRandomPause
parameter_list|()
block|{
if|if
condition|(
name|updateRandomPause
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|updateRandomPause
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
name|long
name|rndTime
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|>
literal|2
condition|)
block|{
name|rndTime
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rndTime
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"inject random update delay of {}ms"
argument_list|,
name|rndTime
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rndTime
argument_list|)
expr_stmt|;
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
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectPrepRecoveryOpPauseForever
specifier|public
specifier|static
name|boolean
name|injectPrepRecoveryOpPauseForever
parameter_list|()
block|{
if|if
condition|(
name|prepRecoveryOpPauseForever
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|prepRecoveryOpPauseForever
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
comment|// Prevent for continuous pause forever
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
operator|&&
name|countPrepRecoveryOpPauseForever
operator|.
name|get
argument_list|()
operator|<
literal|2
condition|)
block|{
name|countPrepRecoveryOpPauseForever
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"inject pause forever for prep recovery op"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
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
block|}
block|}
else|else
block|{
name|countPrepRecoveryOpPauseForever
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|injectSplitFailureBeforeReplicaCreation
specifier|public
specifier|static
name|boolean
name|injectSplitFailureBeforeReplicaCreation
parameter_list|()
block|{
if|if
condition|(
name|splitFailureBeforeReplicaCreation
operator|!=
literal|null
condition|)
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rand
condition|)
return|return
literal|true
return|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|splitFailureBeforeReplicaCreation
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
name|int
name|chanceIn100
init|=
name|pair
operator|.
name|second
argument_list|()
decl_stmt|;
if|if
condition|(
name|enabled
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|>=
operator|(
literal|100
operator|-
name|chanceIn100
operator|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Injecting failure in creating replica for sub-shard"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unable to create replica"
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis, because COMMIT_TIME_MSEC_KEY use currentTimeMillis as value"
argument_list|)
DECL|method|waitForInSyncWithLeader
specifier|public
specifier|static
name|boolean
name|waitForInSyncWithLeader
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|ZkController
name|zkController
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|waitForReplicasInSync
operator|==
literal|null
condition|)
return|return
literal|true
return|;
name|log
operator|.
name|info
argument_list|(
literal|"Start waiting for replica in sync with leader"
argument_list|)
expr_stmt|;
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|pair
init|=
name|parseValue
argument_list|(
name|waitForReplicasInSync
argument_list|)
decl_stmt|;
name|boolean
name|enabled
init|=
name|pair
operator|.
name|first
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|enabled
condition|)
return|return
literal|true
return|;
name|long
name|t
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|100
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pair
operator|.
name|second
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|core
operator|.
name|isClosed
argument_list|()
condition|)
return|return
literal|true
return|;
name|Replica
name|leaderReplica
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|leaderClient
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|leaderReplica
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
name|ReplicationHandler
operator|.
name|PATH
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|COMMAND
argument_list|,
name|CMD_DETAILS
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|leaderClient
operator|.
name|request
argument_list|(
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|leaderVersion
init|=
call|(
name|long
call|)
argument_list|(
operator|(
name|NamedList
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"details"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"indexVersion"
argument_list|)
decl_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|localVersion
init|=
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getIndexCommit
argument_list|()
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|SolrIndexWriter
operator|.
name|COMMIT_TIME_MSEC_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|localVersion
operator|==
literal|null
operator|&&
name|leaderVersion
operator|==
literal|0
operator|&&
operator|!
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|hasUncommittedChanges
argument_list|()
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|localVersion
operator|!=
literal|null
operator|&&
name|Long
operator|.
name|parseLong
argument_list|(
name|localVersion
argument_list|)
operator|==
name|leaderVersion
operator|&&
operator|(
name|leaderVersion
operator|>=
name|t
operator|||
name|i
operator|>=
literal|6
operator|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Waiting time for replica in sync with leader: {}"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|currentTime
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Exception when wait for replicas in sync with master"
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|parseValue
specifier|private
specifier|static
name|Pair
argument_list|<
name|Boolean
argument_list|,
name|Integer
argument_list|>
name|parseValue
parameter_list|(
name|String
name|raw
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|ENABLED_PERCENT
operator|.
name|matcher
argument_list|(
name|raw
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No match, probably bad syntax: "
operator|+
name|raw
argument_list|)
throw|;
name|String
name|val
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|percent
init|=
literal|"100"
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|groupCount
argument_list|()
operator|==
literal|2
condition|)
block|{
name|percent
operator|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|val
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|percent
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

