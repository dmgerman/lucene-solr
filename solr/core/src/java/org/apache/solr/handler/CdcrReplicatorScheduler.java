begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

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
name|util
operator|.
name|DefaultSolrThreadFactory
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
name|concurrent
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Schedule the execution of the {@link org.apache.solr.handler.CdcrReplicator} threads at  * regular time interval. It relies on a queue of {@link org.apache.solr.handler.CdcrReplicatorState} in  * order to avoid that one {@link org.apache.solr.handler.CdcrReplicatorState} is used by two threads at the same  * time.  */
end_comment

begin_class
DECL|class|CdcrReplicatorScheduler
class|class
name|CdcrReplicatorScheduler
block|{
DECL|field|isStarted
specifier|private
name|boolean
name|isStarted
init|=
literal|false
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ScheduledExecutorService
name|scheduler
decl_stmt|;
DECL|field|replicatorsPool
specifier|private
name|ExecutorService
name|replicatorsPool
decl_stmt|;
DECL|field|replicatorManager
specifier|private
specifier|final
name|CdcrReplicatorManager
name|replicatorManager
decl_stmt|;
DECL|field|statesQueue
specifier|private
specifier|final
name|ConcurrentLinkedQueue
argument_list|<
name|CdcrReplicatorState
argument_list|>
name|statesQueue
decl_stmt|;
DECL|field|poolSize
specifier|private
name|int
name|poolSize
init|=
name|DEFAULT_POOL_SIZE
decl_stmt|;
DECL|field|timeSchedule
specifier|private
name|int
name|timeSchedule
init|=
name|DEFAULT_TIME_SCHEDULE
decl_stmt|;
DECL|field|batchSize
specifier|private
name|int
name|batchSize
init|=
name|DEFAULT_BATCH_SIZE
decl_stmt|;
DECL|field|DEFAULT_POOL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_POOL_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|DEFAULT_TIME_SCHEDULE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_TIME_SCHEDULE
init|=
literal|10
decl_stmt|;
DECL|field|DEFAULT_BATCH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BATCH_SIZE
init|=
literal|128
decl_stmt|;
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
DECL|method|CdcrReplicatorScheduler
name|CdcrReplicatorScheduler
parameter_list|(
specifier|final
name|CdcrReplicatorManager
name|replicatorStatesManager
parameter_list|,
specifier|final
name|SolrParams
name|replicatorConfiguration
parameter_list|)
block|{
name|this
operator|.
name|replicatorManager
operator|=
name|replicatorStatesManager
expr_stmt|;
name|this
operator|.
name|statesQueue
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<>
argument_list|(
name|replicatorManager
operator|.
name|getReplicatorStates
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicatorConfiguration
operator|!=
literal|null
condition|)
block|{
name|poolSize
operator|=
name|replicatorConfiguration
operator|.
name|getInt
argument_list|(
name|CdcrParams
operator|.
name|THREAD_POOL_SIZE_PARAM
argument_list|,
name|DEFAULT_POOL_SIZE
argument_list|)
expr_stmt|;
name|timeSchedule
operator|=
name|replicatorConfiguration
operator|.
name|getInt
argument_list|(
name|CdcrParams
operator|.
name|SCHEDULE_PARAM
argument_list|,
name|DEFAULT_TIME_SCHEDULE
argument_list|)
expr_stmt|;
name|batchSize
operator|=
name|replicatorConfiguration
operator|.
name|getInt
argument_list|(
name|CdcrParams
operator|.
name|BATCH_SIZE_PARAM
argument_list|,
name|DEFAULT_BATCH_SIZE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isStarted
condition|)
block|{
name|scheduler
operator|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|(
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"cdcr-scheduler"
argument_list|)
argument_list|)
expr_stmt|;
name|replicatorsPool
operator|=
name|ExecutorUtil
operator|.
name|newMDCAwareFixedThreadPool
argument_list|(
name|poolSize
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"cdcr-replicator"
argument_list|)
argument_list|)
expr_stmt|;
comment|// the scheduler thread is executed every second and submits one replication task
comment|// per available state in the queue
name|scheduler
operator|.
name|scheduleWithFixedDelay
argument_list|(
parameter_list|()
lambda|->
block|{
name|int
name|nCandidates
init|=
name|statesQueue
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nCandidates
condition|;
name|i
operator|++
control|)
block|{
comment|// a thread that poll one state from the queue, execute the replication task, and push back
comment|// the state in the queue when the task is completed
name|replicatorsPool
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
name|CdcrReplicatorState
name|state
init|=
name|statesQueue
operator|.
name|poll
argument_list|()
decl_stmt|;
assert|assert
name|state
operator|!=
literal|null
assert|;
comment|// Should never happen
try|try
block|{
if|if
condition|(
operator|!
name|state
operator|.
name|isBootstrapInProgress
argument_list|()
condition|)
block|{
operator|new
name|CdcrReplicator
argument_list|(
name|state
argument_list|,
name|batchSize
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Replicator state is bootstrapping, skipping replication for target collection {}"
argument_list|,
name|state
operator|.
name|getTargetCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|statesQueue
operator|.
name|offer
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|0
argument_list|,
name|timeSchedule
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|isStarted
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|shutdown
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|isStarted
condition|)
block|{
comment|// interrupts are often dangerous in Lucene / Solr code, but the
comment|// test for this will leak threads without
name|replicatorsPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|replicatorsPool
operator|.
name|awaitTermination
argument_list|(
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Thread interrupted while waiting for CDCR replicator threadpool close."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|isStarted
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

