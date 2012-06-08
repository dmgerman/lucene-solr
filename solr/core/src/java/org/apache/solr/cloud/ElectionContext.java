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
name|CloudState
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
name|NodeExistsException
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|ElectionContext
specifier|public
specifier|abstract
class|class
name|ElectionContext
block|{
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
name|shardZkNodeName
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
name|shardZkNodeName
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
comment|// the given core may or may not be null - if you need access to the current core, you must pass
comment|// the core container and core name to your context impl - then use this core ref if it is not null
comment|// else access it from the core container
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
block|}
end_class

begin_class
DECL|class|ShardLeaderElectionContextBase
class|class
name|ShardLeaderElectionContextBase
extends|extends
name|ElectionContext
block|{
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
name|shardZkNodeName
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
name|shardZkNodeName
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
try|try
block|{
name|zkClient
operator|.
name|makePath
argument_list|(
name|leaderPath
argument_list|,
name|leaderProps
operator|==
literal|null
condition|?
literal|null
else|:
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
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{
comment|// if a previous leader ephemeral still exists for some reason, try and
comment|// remove it
name|zkClient
operator|.
name|delete
argument_list|(
name|leaderPath
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|zkClient
operator|.
name|makePath
argument_list|(
name|leaderPath
argument_list|,
name|leaderProps
operator|==
literal|null
condition|?
literal|null
else|:
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
block|}
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
literal|"leader"
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
DECL|field|zkController
specifier|private
name|ZkController
name|zkController
decl_stmt|;
DECL|field|cc
specifier|private
name|CoreContainer
name|cc
decl_stmt|;
DECL|field|syncStrategy
specifier|private
name|SyncStrategy
name|syncStrategy
init|=
operator|new
name|SyncStrategy
argument_list|()
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
name|shardZkNodeName
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
name|shardZkNodeName
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
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|String
name|coreName
init|=
name|leaderProps
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// the first time we are run, we will get a startupCore - after
comment|// we will get null and must use cc.getCore
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
argument_list|)
condition|)
block|{
comment|// System.out.println("there is a better leader candidate it appears");
name|rejoinLeaderElection
argument_list|(
name|leaderSeqPath
argument_list|,
name|core
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|weAreReplacement
condition|)
block|{
if|if
condition|(
name|zkClient
operator|.
name|exists
argument_list|(
name|leaderPath
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|delete
argument_list|(
name|leaderPath
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|//          System.out.println("I may be the new Leader:" + leaderPath
comment|//              + " - I need to try and sync");
name|boolean
name|success
init|=
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
decl_stmt|;
if|if
condition|(
operator|!
name|success
operator|&&
name|anyoneElseActive
argument_list|()
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
block|}
comment|// If I am going to be the leader I have to be active
comment|// System.out.println("I am leader go active");
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
name|zkController
operator|.
name|publish
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|ACTIVE
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
block|}
name|super
operator|.
name|runLeaderProcess
argument_list|(
name|weAreReplacement
argument_list|)
expr_stmt|;
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
comment|// System.out.println("sync failed, delete our election node:"
comment|// + leaderSeqPath);
name|zkController
operator|.
name|publish
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|ZkStateReader
operator|.
name|DOWN
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
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|leaderElector
operator|.
name|joinElection
argument_list|(
name|this
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
parameter_list|)
block|{
name|CloudState
name|cloudState
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getCloudState
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
name|cloudState
operator|.
name|getSlices
argument_list|(
name|this
operator|.
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
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|shards
init|=
name|slice
operator|.
name|getShards
argument_list|()
decl_stmt|;
name|boolean
name|foundSomeoneElseActive
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|shard
range|:
name|shards
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
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
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
name|cloudState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
comment|// we are alive
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
operator|(
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
operator|)
operator|&&
name|cloudState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|get
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
name|foundSomeoneElseActive
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
operator|!
name|foundSomeoneElseActive
return|;
block|}
DECL|method|anyoneElseActive
specifier|private
name|boolean
name|anyoneElseActive
parameter_list|()
block|{
name|CloudState
name|cloudState
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getCloudState
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
name|cloudState
operator|.
name|getSlices
argument_list|(
name|this
operator|.
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
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|shards
init|=
name|slice
operator|.
name|getShards
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
name|ZkNodeProps
argument_list|>
name|shard
range|:
name|shards
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
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|state
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
operator|)
operator|&&
name|cloudState
operator|.
name|liveNodesContain
argument_list|(
name|shard
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
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
DECL|field|stateReader
specifier|private
specifier|final
name|ZkStateReader
name|stateReader
decl_stmt|;
DECL|method|OverseerElectionContext
specifier|public
name|OverseerElectionContext
parameter_list|(
specifier|final
name|String
name|zkNodeName
parameter_list|,
name|ZkStateReader
name|stateReader
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
name|stateReader
operator|.
name|getZkClient
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|stateReader
operator|=
name|stateReader
expr_stmt|;
name|this
operator|.
name|zkClient
operator|=
name|stateReader
operator|.
name|getZkClient
argument_list|()
expr_stmt|;
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
try|try
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
block|}
catch|catch
parameter_list|(
name|NodeExistsException
name|e
parameter_list|)
block|{
comment|// if a previous leader ephemeral still exists for some reason, try and
comment|// remove it
name|zkClient
operator|.
name|delete
argument_list|(
name|leaderPath
argument_list|,
operator|-
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
block|}
operator|new
name|Overseer
argument_list|(
name|stateReader
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

