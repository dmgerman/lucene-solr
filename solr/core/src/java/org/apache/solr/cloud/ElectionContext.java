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
name|Map
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
name|MatchAllDocsQuery
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
name|cloud
operator|.
name|Slice
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
name|cloud
operator|.
name|ZkStateReader
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
name|UpdateLog
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
name|RefCounted
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
name|KeeperException
operator|.
name|NoNodeException
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|ElectionContext
specifier|public
specifier|abstract
class|class
name|ElectionContext
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
name|ElectionContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|electionPath
specifier|final
name|String
name|electionPath
decl_stmt|;
DECL|field|leaderProps
specifier|final
name|ZkNodeProps
name|leaderProps
decl_stmt|;
DECL|field|id
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|leaderPath
specifier|final
name|String
name|leaderPath
decl_stmt|;
DECL|field|leaderSeqPath
name|String
name|leaderSeqPath
decl_stmt|;
DECL|field|zkClient
specifier|private
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|method|ElectionContext
specifier|public
name|ElectionContext
parameter_list|(
specifier|final
name|String
name|coreNodeName
parameter_list|,
specifier|final
name|String
name|electionPath
parameter_list|,
specifier|final
name|String
name|leaderPath
parameter_list|,
specifier|final
name|ZkNodeProps
name|leaderProps
parameter_list|,
specifier|final
name|SolrZkClient
name|zkClient
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|coreNodeName
expr_stmt|;
name|this
operator|.
name|electionPath
operator|=
name|electionPath
expr_stmt|;
name|this
operator|.
name|leaderPath
operator|=
name|leaderPath
expr_stmt|;
name|this
operator|.
name|leaderProps
operator|=
name|leaderProps
expr_stmt|;
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
DECL|method|cancelElection
specifier|public
name|void
name|cancelElection
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
try|try
block|{
name|zkClient
operator|.
name|delete
argument_list|(
name|leaderSeqPath
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoNodeException
name|e
parameter_list|)
block|{
comment|// fine
name|log
operator|.
name|warn
argument_list|(
literal|"cancelElection did not find election node to remove"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runLeaderProcess
specifier|abstract
name|void
name|runLeaderProcess
parameter_list|(
name|boolean
name|weAreReplacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
function_decl|;
DECL|method|checkIfIamLeaderFired
specifier|public
name|void
name|checkIfIamLeaderFired
parameter_list|()
block|{}
DECL|method|joinedElectionFired
specifier|public
name|void
name|joinedElectionFired
parameter_list|()
block|{}
block|}
end_class

begin_class
DECL|class|ShardLeaderElectionContextBase
class|class
name|ShardLeaderElectionContextBase
extends|extends
name|ElectionContext
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
name|ShardLeaderElectionContextBase
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkClient
specifier|protected
specifier|final
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|shardId
specifier|protected
name|String
name|shardId
decl_stmt|;
DECL|field|collection
specifier|protected
name|String
name|collection
decl_stmt|;
DECL|field|leaderElector
specifier|protected
name|LeaderElector
name|leaderElector
decl_stmt|;
DECL|method|ShardLeaderElectionContextBase
specifier|public
name|ShardLeaderElectionContextBase
parameter_list|(
name|LeaderElector
name|leaderElector
parameter_list|,
specifier|final
name|String
name|shardId
parameter_list|,
specifier|final
name|String
name|collection
parameter_list|,
specifier|final
name|String
name|coreNodeName
parameter_list|,
name|ZkNodeProps
name|props
parameter_list|,
name|ZkStateReader
name|zkStateReader
parameter_list|)
block|{
name|super
argument_list|(
name|coreNodeName
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|collection
operator|+
literal|"/leader_elect/"
operator|+
name|shardId
argument_list|,
name|ZkStateReader
operator|.
name|getShardLeadersPath
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|)
argument_list|,
name|props
argument_list|,
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|leaderElector
operator|=
name|leaderElector
expr_stmt|;
name|this
operator|.
name|zkClient
operator|=
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
try|try
block|{
operator|new
name|ZkCmdExecutor
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|)
operator|.
name|ensureExists
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTIONS_ZKNODE
operator|+
literal|"/"
operator|+
name|collection
argument_list|,
name|zkClient
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
block|}
annotation|@
name|Override
DECL|method|runLeaderProcess
name|void
name|runLeaderProcess
parameter_list|(
name|boolean
name|weAreReplacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|leaderPath
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|leaderProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
assert|assert
name|shardId
operator|!=
literal|null
assert|;
name|ZkNodeProps
name|m
init|=
name|ZkNodeProps
operator|.
name|fromKeyVals
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|ZkStateReader
operator|.
name|LEADER_PROP
argument_list|,
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
name|shardId
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
name|collection
argument_list|,
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|,
name|leaderProps
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|,
name|leaderProps
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
argument_list|,
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
decl_stmt|;
name|Overseer
operator|.
name|getInQueue
argument_list|(
name|zkClient
argument_list|)
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// add core container and stop passing core around...
end_comment

begin_class
DECL|class|ShardLeaderElectionContext
specifier|final
class|class
name|ShardLeaderElectionContext
extends|extends
name|ShardLeaderElectionContextBase
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
name|ShardLeaderElectionContext
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkController
specifier|private
specifier|final
name|ZkController
name|zkController
decl_stmt|;
DECL|field|cc
specifier|private
specifier|final
name|CoreContainer
name|cc
decl_stmt|;
DECL|field|syncStrategy
specifier|private
specifier|final
name|SyncStrategy
name|syncStrategy
decl_stmt|;
DECL|field|isClosed
specifier|private
specifier|volatile
name|boolean
name|isClosed
init|=
literal|false
decl_stmt|;
DECL|method|ShardLeaderElectionContext
specifier|public
name|ShardLeaderElectionContext
parameter_list|(
name|LeaderElector
name|leaderElector
parameter_list|,
specifier|final
name|String
name|shardId
parameter_list|,
specifier|final
name|String
name|collection
parameter_list|,
specifier|final
name|String
name|coreNodeName
parameter_list|,
name|ZkNodeProps
name|props
parameter_list|,
name|ZkController
name|zkController
parameter_list|,
name|CoreContainer
name|cc
parameter_list|)
block|{
name|super
argument_list|(
name|leaderElector
argument_list|,
name|shardId
argument_list|,
name|collection
argument_list|,
name|coreNodeName
argument_list|,
name|props
argument_list|,
name|zkController
operator|.
name|getZkStateReader
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkController
operator|=
name|zkController
expr_stmt|;
name|this
operator|.
name|cc
operator|=
name|cc
expr_stmt|;
name|syncStrategy
operator|=
operator|new
name|SyncStrategy
argument_list|(
name|cc
operator|.
name|getUpdateShardHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|this
operator|.
name|isClosed
operator|=
literal|true
expr_stmt|;
name|syncStrategy
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/*     * weAreReplacement: has someone else been the leader already?    */
annotation|@
name|Override
DECL|method|runLeaderProcess
name|void
name|runLeaderProcess
parameter_list|(
name|boolean
name|weAreReplacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Running the leader process for shard "
operator|+
name|shardId
argument_list|)
expr_stmt|;
name|String
name|coreName
init|=
name|leaderProps
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
comment|// clear the leader in clusterstate
name|ZkNodeProps
name|m
init|=
operator|new
name|ZkNodeProps
argument_list|(
name|Overseer
operator|.
name|QUEUE_OPERATION
argument_list|,
name|ZkStateReader
operator|.
name|LEADER_PROP
argument_list|,
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|,
name|shardId
argument_list|,
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|Overseer
operator|.
name|getInQueue
argument_list|(
name|zkClient
argument_list|)
operator|.
name|offer
argument_list|(
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|leaderVoteWait
init|=
name|cc
operator|.
name|getZkController
argument_list|()
operator|.
name|getLeaderVoteWait
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|weAreReplacement
condition|)
block|{
name|waitForReplicasToComeUp
argument_list|(
name|weAreReplacement
argument_list|,
name|leaderVoteWait
argument_list|)
expr_stmt|;
block|}
name|SolrCore
name|core
init|=
literal|null
decl_stmt|;
try|try
block|{
name|core
operator|=
name|cc
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
name|cancelElection
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
literal|"Fatal Error, SolrCore not found:"
operator|+
name|coreName
operator|+
literal|" in "
operator|+
name|cc
operator|.
name|getCoreNames
argument_list|()
argument_list|)
throw|;
block|}
comment|// should I be leader?
if|if
condition|(
name|weAreReplacement
operator|&&
operator|!
name|shouldIBeLeader
argument_list|(
name|leaderProps
argument_list|,
name|core
argument_list|,
name|weAreReplacement
argument_list|)
condition|)
block|{
name|rejoinLeaderElection
argument_list|(
name|leaderSeqPath
argument_list|,
name|core
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"I may be the new leader - try and sync"
argument_list|)
expr_stmt|;
comment|// we are going to attempt to be the leader
comment|// first cancel any current recovery
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|cancelRecovery
argument_list|()
expr_stmt|;
if|if
condition|(
name|weAreReplacement
condition|)
block|{
comment|// wait a moment for any floating updates to finish
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2500
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
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|success
operator|=
name|syncStrategy
operator|.
name|sync
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|,
name|leaderProps
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Exception while trying to sync"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|success
operator|=
literal|false
expr_stmt|;
block|}
name|UpdateLog
name|ulog
init|=
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
operator|!
name|success
condition|)
block|{
name|boolean
name|hasRecentUpdates
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|ulog
operator|!=
literal|null
condition|)
block|{
comment|// TODO: we could optimize this if necessary
name|UpdateLog
operator|.
name|RecentUpdates
name|recentUpdates
init|=
name|ulog
operator|.
name|getRecentUpdates
argument_list|()
decl_stmt|;
try|try
block|{
name|hasRecentUpdates
operator|=
operator|!
name|recentUpdates
operator|.
name|getVersions
argument_list|(
literal|1
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|recentUpdates
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|hasRecentUpdates
condition|)
block|{
comment|// we failed sync, but we have no versions - we can't sync in that case
comment|// - we were active
comment|// before, so become leader anyway
name|log
operator|.
name|info
argument_list|(
literal|"We failed sync, but we have no versions - we can't sync in that case - we were active before, so become leader anyway"
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// solrcloud_debug
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
try|try
block|{
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searchHolder
init|=
name|core
operator|.
name|getNewestSearcher
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|searchHolder
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
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
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" synched "
operator|+
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searchHolder
operator|.
name|decref
argument_list|()
expr_stmt|;
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
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|null
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|rejoinLeaderElection
argument_list|(
name|leaderSeqPath
argument_list|,
name|core
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"I am the new leader: "
operator|+
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
operator|+
literal|" "
operator|+
name|shardId
argument_list|)
expr_stmt|;
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|setLeader
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|super
operator|.
name|runLeaderProcess
argument_list|(
name|weAreReplacement
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"There was a problem trying to register as the leader"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|core
operator|=
name|cc
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|==
literal|null
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
literal|"Fatal Error, SolrCore not found:"
operator|+
name|coreName
operator|+
literal|" in "
operator|+
name|cc
operator|.
name|getCoreNames
argument_list|()
argument_list|)
throw|;
block|}
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|setLeader
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we could not publish ourselves as leader - rejoin election
name|rejoinLeaderElection
argument_list|(
name|leaderSeqPath
argument_list|,
name|core
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|cancelElection
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|core
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|areAnyOtherReplicasActive
specifier|private
name|boolean
name|areAnyOtherReplicasActive
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|ZkNodeProps
name|leaderProps
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
name|clusterState
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|Slice
name|slice
init|=
name|slices
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|slice
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|Slice
operator|.
name|ACTIVE
argument_list|)
condition|)
block|{
comment|//Return false if the Slice is not active yet.
return|return
literal|false
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|replicasMap
init|=
name|slice
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|shard
range|:
name|replicasMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|state
init|=
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
decl_stmt|;
comment|// System.out.println("state:"
comment|// + state
comment|// + shard.getValue().get(ZkStateReader.NODE_NAME_PROP)
comment|// + " live: "
comment|// + clusterState.liveNodesContain(shard.getValue().get(
comment|// ZkStateReader.NODE_NAME_PROP)));
if|if
condition|(
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
operator|&&
name|clusterState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
operator|&&
operator|!
operator|new
name|ZkCoreNodeProps
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|getCoreUrl
argument_list|()
operator|.
name|equals
argument_list|(
operator|new
name|ZkCoreNodeProps
argument_list|(
name|leaderProps
argument_list|)
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|waitForReplicasToComeUp
specifier|private
name|void
name|waitForReplicasToComeUp
parameter_list|(
name|boolean
name|weAreReplacement
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|timeoutAt
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
decl_stmt|;
specifier|final
name|String
name|shardsElectZkPath
init|=
name|electionPath
operator|+
name|LeaderElector
operator|.
name|ELECTION_NODE
decl_stmt|;
name|Slice
name|slices
init|=
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlice
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|int
name|cnt
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
operator|&&
operator|!
name|isClosed
operator|&&
operator|!
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
comment|// wait for everyone to be up
if|if
condition|(
name|slices
operator|!=
literal|null
condition|)
block|{
name|int
name|found
init|=
literal|0
decl_stmt|;
try|try
block|{
name|found
operator|=
name|zkClient
operator|.
name|getChildren
argument_list|(
name|shardsElectZkPath
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error checking for the number of election participants"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// on startup and after connection timeout, wait for all known shards
if|if
condition|(
name|found
operator|>=
name|slices
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Enough replicas found to continue."
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
if|if
condition|(
name|cnt
operator|%
literal|40
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Waiting until we see more replicas up for shard "
operator|+
name|shardId
operator|+
literal|": total="
operator|+
name|slices
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" found="
operator|+
name|found
operator|+
literal|" timeoutin="
operator|+
operator|(
name|timeoutAt
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|timeoutAt
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Was waiting for replicas to come up, but they are taking too long - assuming they won't come back till later"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Shard not found: "
operator|+
name|shardId
operator|+
literal|" for collection "
operator|+
name|collection
argument_list|)
expr_stmt|;
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|slices
operator|=
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getSlice
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
comment|// System.out.println("###### waitForReplicasToComeUp  : slices=" + slices + " all=" + zkController.getClusterState().getCollectionStates() );
name|cnt
operator|++
expr_stmt|;
block|}
block|}
DECL|method|rejoinLeaderElection
specifier|private
name|void
name|rejoinLeaderElection
parameter_list|(
name|String
name|leaderSeqPath
parameter_list|,
name|SolrCore
name|core
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|KeeperException
throws|,
name|IOException
block|{
comment|// remove our ephemeral and re join the election
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Not rejoining election because CoreContainer is shutdown"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"There may be a better leader candidate than us - going back into recovery"
argument_list|)
expr_stmt|;
name|cancelElection
argument_list|()
expr_stmt|;
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getSolrCoreState
argument_list|()
operator|.
name|doRecovery
argument_list|(
name|cc
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|leaderElector
operator|.
name|joinElection
argument_list|(
name|this
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|shouldIBeLeader
specifier|private
name|boolean
name|shouldIBeLeader
parameter_list|(
name|ZkNodeProps
name|leaderProps
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|boolean
name|weAreReplacement
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Checking if I should try and be the leader."
argument_list|)
expr_stmt|;
if|if
condition|(
name|isClosed
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Bailing on leader process because we have been closed"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|weAreReplacement
condition|)
block|{
comment|// we are the first node starting in the shard - there is a configurable wait
comment|// to make sure others participate in sync and leader election, we can be leader
return|return
literal|true
return|;
block|}
if|if
condition|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getLastPublished
argument_list|()
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"My last published State was Active, it's okay to be the leader."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"My last published State was "
operator|+
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getLastPublished
argument_list|()
operator|+
literal|", I won't be the leader."
argument_list|)
expr_stmt|;
comment|// TODO: and if no one is a good candidate?
return|return
literal|false
return|;
block|}
block|}
end_class

begin_class
DECL|class|OverseerElectionContext
specifier|final
class|class
name|OverseerElectionContext
extends|extends
name|ElectionContext
block|{
DECL|field|zkClient
specifier|private
specifier|final
name|SolrZkClient
name|zkClient
decl_stmt|;
DECL|field|overseer
specifier|private
name|Overseer
name|overseer
decl_stmt|;
DECL|method|OverseerElectionContext
specifier|public
name|OverseerElectionContext
parameter_list|(
name|SolrZkClient
name|zkClient
parameter_list|,
name|Overseer
name|overseer
parameter_list|,
specifier|final
name|String
name|zkNodeName
parameter_list|)
block|{
name|super
argument_list|(
name|zkNodeName
argument_list|,
literal|"/overseer_elect"
argument_list|,
literal|"/overseer_elect/leader"
argument_list|,
literal|null
argument_list|,
name|zkClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|overseer
operator|=
name|overseer
expr_stmt|;
name|this
operator|.
name|zkClient
operator|=
name|zkClient
expr_stmt|;
try|try
block|{
operator|new
name|ZkCmdExecutor
argument_list|(
name|zkClient
operator|.
name|getZkClientTimeout
argument_list|()
argument_list|)
operator|.
name|ensureExists
argument_list|(
literal|"/overseer_elect"
argument_list|,
name|zkClient
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
block|}
annotation|@
name|Override
DECL|method|runLeaderProcess
name|void
name|runLeaderProcess
parameter_list|(
name|boolean
name|weAreReplacement
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
specifier|final
name|String
name|id
init|=
name|leaderSeqPath
operator|.
name|substring
argument_list|(
name|leaderSeqPath
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ZkNodeProps
name|myProps
init|=
operator|new
name|ZkNodeProps
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|leaderPath
argument_list|,
name|ZkStateReader
operator|.
name|toJSON
argument_list|(
name|myProps
argument_list|)
argument_list|,
name|CreateMode
operator|.
name|EPHEMERAL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|overseer
operator|.
name|start
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
DECL|method|cancelElection
specifier|public
name|void
name|cancelElection
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|KeeperException
block|{
name|super
operator|.
name|cancelElection
argument_list|()
expr_stmt|;
name|overseer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|joinedElectionFired
specifier|public
name|void
name|joinedElectionFired
parameter_list|()
block|{
name|overseer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIfIamLeaderFired
specifier|public
name|void
name|checkIfIamLeaderFired
parameter_list|()
block|{
comment|// leader changed - close the overseer
name|overseer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

