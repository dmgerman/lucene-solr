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
name|Executors
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
name|ScheduledExecutorService
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
name|SolrRequest
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
name|SolrServerException
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
name|cloud
operator|.
name|ClusterState
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
name|ZkCoreNodeProps
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
name|ZkNodeProps
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
name|update
operator|.
name|CdcrUpdateLog
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

begin_comment
comment|/**  *<p>  * Synchronize periodically the update log of non-leader nodes with their leaders.  *</p>  *<p>  * Non-leader nodes must always buffer updates in case of leader failures. They have to periodically  * synchronize their update logs with their leader to remove old transaction logs that will never be used anymore.  * This is performed by a background thread that is scheduled with a fixed delay. The background thread is sending  * the action {@link org.apache.solr.handler.CdcrParams.CdcrAction#LASTPROCESSEDVERSION} to the leader to retrieve  * the lowest last version number processed. This version is then used to move forward the buffer log reader.  *</p>  */
end_comment

begin_class
DECL|class|CdcrUpdateLogSynchronizer
class|class
name|CdcrUpdateLogSynchronizer
implements|implements
name|CdcrStateManager
operator|.
name|CdcrStateObserver
block|{
DECL|field|leaderStateManager
specifier|private
name|CdcrLeaderStateManager
name|leaderStateManager
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ScheduledExecutorService
name|scheduler
decl_stmt|;
DECL|field|core
specifier|private
specifier|final
name|SolrCore
name|core
decl_stmt|;
DECL|field|collection
specifier|private
specifier|final
name|String
name|collection
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|String
name|shardId
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|field|timeSchedule
specifier|private
name|int
name|timeSchedule
init|=
name|DEFAULT_TIME_SCHEDULE
decl_stmt|;
DECL|field|DEFAULT_TIME_SCHEDULE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_TIME_SCHEDULE
init|=
literal|60000
decl_stmt|;
comment|// by default, every minute
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
DECL|method|CdcrUpdateLogSynchronizer
name|CdcrUpdateLogSynchronizer
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|path
parameter_list|,
name|SolrParams
name|updateLogSynchonizerConfiguration
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getShardId
argument_list|()
expr_stmt|;
if|if
condition|(
name|updateLogSynchonizerConfiguration
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|timeSchedule
operator|=
name|updateLogSynchonizerConfiguration
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
block|}
block|}
DECL|method|setLeaderStateManager
name|void
name|setLeaderStateManager
parameter_list|(
specifier|final
name|CdcrLeaderStateManager
name|leaderStateManager
parameter_list|)
block|{
name|this
operator|.
name|leaderStateManager
operator|=
name|leaderStateManager
expr_stmt|;
name|this
operator|.
name|leaderStateManager
operator|.
name|register
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stateUpdate
specifier|public
name|void
name|stateUpdate
parameter_list|()
block|{
comment|// If I am not the leader, I need to synchronise periodically my update log with my leader.
if|if
condition|(
operator|!
name|leaderStateManager
operator|.
name|amILeader
argument_list|()
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
literal|"cdcr-update-log-synchronizer"
argument_list|)
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|UpdateLogSynchronisation
argument_list|()
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
return|return;
block|}
name|this
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|isStarted
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|scheduler
operator|!=
literal|null
return|;
block|}
DECL|method|shutdown
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|scheduler
operator|!=
literal|null
condition|)
block|{
comment|// interrupts are often dangerous in Lucene / Solr code, but the
comment|// test for this will leak threads without
name|scheduler
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|scheduler
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|class|UpdateLogSynchronisation
specifier|private
class|class
name|UpdateLogSynchronisation
implements|implements
name|Runnable
block|{
DECL|method|getLeaderUrl
specifier|private
name|String
name|getLeaderUrl
parameter_list|()
block|{
name|ZkController
name|zkController
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
name|ClusterState
name|cstate
init|=
name|zkController
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|ZkNodeProps
name|leaderProps
init|=
name|cstate
operator|.
name|getLeader
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaderProps
operator|==
literal|null
condition|)
block|{
comment|// we might not have a leader yet, returns null
return|return
literal|null
return|;
block|}
name|ZkCoreNodeProps
name|nodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|leaderProps
argument_list|)
decl_stmt|;
return|return
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|String
name|leaderUrl
init|=
name|getLeaderUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|leaderUrl
operator|==
literal|null
condition|)
block|{
comment|// we might not have a leader yet, stop and try again later
return|return;
block|}
name|HttpSolrClient
name|server
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|leaderUrl
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|server
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
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
name|ACTION
argument_list|,
name|CdcrParams
operator|.
name|CdcrAction
operator|.
name|LASTPROCESSEDVERSION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SolrRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|long
name|lastVersion
decl_stmt|;
try|try
block|{
name|NamedList
name|response
init|=
name|server
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|lastVersion
operator|=
operator|(
name|Long
operator|)
name|response
operator|.
name|get
argument_list|(
name|CdcrParams
operator|.
name|LAST_PROCESSED_VERSION
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"My leader {} says its last processed _version_ number is: {}. I am {}"
argument_list|,
name|leaderUrl
argument_list|,
name|lastVersion
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|SolrServerException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Couldn't get last processed version from leader {}: {}"
argument_list|,
name|leaderUrl
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
try|try
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Caught exception trying to close server: "
argument_list|,
name|leaderUrl
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if we received -1, it means that the log reader on the leader has not yet started to read log entries
comment|// do nothing
if|if
condition|(
name|lastVersion
operator|==
operator|-
literal|1
condition|)
block|{
return|return;
block|}
try|try
block|{
name|CdcrUpdateLog
name|ulog
init|=
operator|(
name|CdcrUpdateLog
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
decl_stmt|;
if|if
condition|(
name|ulog
operator|.
name|isBuffering
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Advancing replica buffering tlog reader to {} @ {}:{}"
argument_list|,
name|lastVersion
argument_list|,
name|collection
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|ulog
operator|.
name|getBufferToggle
argument_list|()
operator|.
name|seek
argument_list|(
name|lastVersion
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
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"Couldn't advance replica buffering tlog reader to {} (to remove old tlogs): {}"
argument_list|,
name|lastVersion
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Couldn't advance replica buffering tlog reader to {} (to remove old tlogs): {}"
argument_list|,
name|lastVersion
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Caught unexpected exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

