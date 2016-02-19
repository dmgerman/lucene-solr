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
name|ArrayList
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
name|concurrent
operator|.
name|ExecutorService
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
name|client
operator|.
name|HttpClient
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
name|CoreAdminRequest
operator|.
name|RequestRecovery
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
name|CoreAdminParams
operator|.
name|CoreAdminAction
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
name|CoreDescriptor
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
name|component
operator|.
name|ShardHandler
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
name|component
operator|.
name|ShardRequest
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
name|component
operator|.
name|ShardResponse
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
name|logging
operator|.
name|MDCLoggingContext
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
name|PeerSync
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
name|UpdateShardHandler
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

begin_class
DECL|class|SyncStrategy
specifier|public
class|class
name|SyncStrategy
block|{
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
DECL|field|SKIP_AUTO_RECOVERY
specifier|private
specifier|final
name|boolean
name|SKIP_AUTO_RECOVERY
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
decl_stmt|;
DECL|field|shardHandler
specifier|private
specifier|final
name|ShardHandler
name|shardHandler
decl_stmt|;
DECL|field|isClosed
specifier|private
specifier|volatile
name|boolean
name|isClosed
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|HttpClient
name|client
decl_stmt|;
DECL|field|updateExecutor
specifier|private
specifier|final
name|ExecutorService
name|updateExecutor
decl_stmt|;
DECL|field|recoveryRequests
specifier|private
specifier|final
name|List
argument_list|<
name|RecoveryRequest
argument_list|>
name|recoveryRequests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|RecoveryRequest
specifier|private
specifier|static
class|class
name|RecoveryRequest
block|{
DECL|field|leaderProps
name|ZkNodeProps
name|leaderProps
decl_stmt|;
DECL|field|baseUrl
name|String
name|baseUrl
decl_stmt|;
DECL|field|coreName
name|String
name|coreName
decl_stmt|;
block|}
DECL|method|SyncStrategy
specifier|public
name|SyncStrategy
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
block|{
name|UpdateShardHandler
name|updateShardHandler
init|=
name|cc
operator|.
name|getUpdateShardHandler
argument_list|()
decl_stmt|;
name|client
operator|=
name|updateShardHandler
operator|.
name|getHttpClient
argument_list|()
expr_stmt|;
name|shardHandler
operator|=
name|cc
operator|.
name|getShardHandlerFactory
argument_list|()
operator|.
name|getShardHandler
argument_list|()
expr_stmt|;
name|updateExecutor
operator|=
name|updateShardHandler
operator|.
name|getUpdateExecutor
argument_list|()
expr_stmt|;
block|}
DECL|class|ShardCoreRequest
specifier|private
specifier|static
class|class
name|ShardCoreRequest
extends|extends
name|ShardRequest
block|{
DECL|field|coreName
name|String
name|coreName
decl_stmt|;
DECL|field|baseUrl
specifier|public
name|String
name|baseUrl
decl_stmt|;
block|}
DECL|method|sync
specifier|public
name|boolean
name|sync
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|ZkNodeProps
name|leaderProps
parameter_list|)
block|{
return|return
name|sync
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|,
name|leaderProps
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|sync
specifier|public
name|boolean
name|sync
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|ZkNodeProps
name|leaderProps
parameter_list|,
name|boolean
name|peerSyncOnlyWithActive
parameter_list|)
block|{
if|if
condition|(
name|SKIP_AUTO_RECOVERY
condition|)
block|{
return|return
literal|true
return|;
block|}
name|MDCLoggingContext
operator|.
name|setCore
argument_list|(
name|core
argument_list|)
expr_stmt|;
try|try
block|{
name|boolean
name|success
decl_stmt|;
if|if
condition|(
name|isClosed
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Closed, skipping sync up."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|recoveryRequests
operator|.
name|clear
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sync replicas to "
operator|+
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"No UpdateLog found - cannot sync"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|success
operator|=
name|syncReplicas
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|,
name|leaderProps
argument_list|,
name|peerSyncOnlyWithActive
argument_list|)
expr_stmt|;
return|return
name|success
return|;
block|}
finally|finally
block|{
name|MDCLoggingContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|syncReplicas
specifier|private
name|boolean
name|syncReplicas
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|ZkNodeProps
name|leaderProps
parameter_list|,
name|boolean
name|peerSyncOnlyWithActive
parameter_list|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|CloudDescriptor
name|cloudDesc
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
name|String
name|collection
init|=
name|cloudDesc
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|String
name|shardId
init|=
name|cloudDesc
operator|.
name|getShardId
argument_list|()
decl_stmt|;
if|if
condition|(
name|isClosed
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"We have been closed, won't sync with replicas"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// first sync ourselves - we are the potential leader after all
try|try
block|{
name|success
operator|=
name|syncWithReplicas
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|,
name|leaderProps
argument_list|,
name|collection
argument_list|,
name|shardId
argument_list|,
name|peerSyncOnlyWithActive
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
literal|"Sync Failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|isClosed
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"We have been closed, won't attempt to sync replicas back to leader"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|success
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Sync Success - now sync replicas to me"
argument_list|)
expr_stmt|;
name|syncToMe
argument_list|(
name|zkController
argument_list|,
name|collection
argument_list|,
name|shardId
argument_list|,
name|leaderProps
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|getNumRecordsToKeep
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Leader's attempt to sync with shard failed, moving to the next candidate"
argument_list|)
expr_stmt|;
comment|// lets see who seems ahead...
block|}
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
literal|"Sync Failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
DECL|method|syncWithReplicas
specifier|private
name|boolean
name|syncWithReplicas
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|ZkNodeProps
name|props
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|,
name|boolean
name|peerSyncOnlyWithActive
parameter_list|)
block|{
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|nodes
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getReplicaProps
argument_list|(
name|collection
argument_list|,
name|shardId
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
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
comment|// I have no replicas
return|return
literal|true
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|syncWith
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ZkCoreNodeProps
name|node
range|:
name|nodes
control|)
block|{
name|syncWith
operator|.
name|add
argument_list|(
name|node
operator|.
name|getCoreUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// if we can't reach a replica for sync, we still consider the overall sync a success
comment|// TODO: as an assurance, we should still try and tell the sync nodes that we couldn't reach
comment|// to recover once more?
comment|// Fingerprinting here is off because the we currently rely on having at least one of the nodes return "true", and if replicas are out-of-sync
comment|// we still need to pick one as leader.  A followup sync from the replica to the new leader (with fingerprinting on) should then fail and
comment|// initiate recovery-by-replication.
name|PeerSync
name|peerSync
init|=
operator|new
name|PeerSync
argument_list|(
name|core
argument_list|,
name|syncWith
argument_list|,
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
operator|.
name|getNumRecordsToKeep
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|peerSyncOnlyWithActive
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|peerSync
operator|.
name|sync
argument_list|()
return|;
block|}
DECL|method|syncToMe
specifier|private
name|void
name|syncToMe
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|,
name|ZkNodeProps
name|leaderProps
parameter_list|,
name|CoreDescriptor
name|cd
parameter_list|,
name|int
name|nUpdates
parameter_list|)
block|{
comment|// sync everyone else
comment|// TODO: we should do this in parallel at least
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|nodes
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getReplicaProps
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
name|cd
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCoreNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
operator|+
literal|" has no replicas"
argument_list|)
expr_stmt|;
return|return;
block|}
name|ZkCoreNodeProps
name|zkLeader
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|leaderProps
argument_list|)
decl_stmt|;
for|for
control|(
name|ZkCoreNodeProps
name|node
range|:
name|nodes
control|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
operator|+
literal|": try and ask "
operator|+
name|node
operator|.
name|getCoreUrl
argument_list|()
operator|+
literal|" to sync"
argument_list|)
expr_stmt|;
name|requestSync
argument_list|(
name|node
operator|.
name|getBaseUrl
argument_list|()
argument_list|,
name|node
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
name|zkLeader
operator|.
name|getCoreUrl
argument_list|()
argument_list|,
name|node
operator|.
name|getCoreName
argument_list|()
argument_list|,
name|nUpdates
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
literal|"Error syncing replica to leader"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
name|ShardResponse
name|srsp
init|=
name|shardHandler
operator|.
name|takeCompletedOrError
argument_list|()
decl_stmt|;
if|if
condition|(
name|srsp
operator|==
literal|null
condition|)
break|break;
name|boolean
name|success
init|=
name|handleResponse
argument_list|(
name|srsp
argument_list|)
decl_stmt|;
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Sync request error: "
operator|+
name|srsp
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
operator|+
literal|": Sync failed - we will ask replica ("
operator|+
name|srsp
operator|.
name|getShardAddress
argument_list|()
operator|+
literal|") to recover."
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
literal|"We have been closed, don't request that a replica recover"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RecoveryRequest
name|rr
init|=
operator|new
name|RecoveryRequest
argument_list|()
decl_stmt|;
name|rr
operator|.
name|leaderProps
operator|=
name|leaderProps
expr_stmt|;
name|rr
operator|.
name|baseUrl
operator|=
operator|(
operator|(
name|ShardCoreRequest
operator|)
name|srsp
operator|.
name|getShardRequest
argument_list|()
operator|)
operator|.
name|baseUrl
expr_stmt|;
name|rr
operator|.
name|coreName
operator|=
operator|(
operator|(
name|ShardCoreRequest
operator|)
name|srsp
operator|.
name|getShardRequest
argument_list|()
operator|)
operator|.
name|coreName
expr_stmt|;
name|recoveryRequests
operator|.
name|add
argument_list|(
name|rr
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
operator|+
literal|": "
operator|+
literal|" sync completed with "
operator|+
name|srsp
operator|.
name|getShardAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handleResponse
specifier|private
name|boolean
name|handleResponse
parameter_list|(
name|ShardResponse
name|srsp
parameter_list|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
decl_stmt|;
comment|// TODO: why does this return null sometimes?
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Boolean
name|success
init|=
operator|(
name|Boolean
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"sync"
argument_list|)
decl_stmt|;
if|if
condition|(
name|success
operator|==
literal|null
condition|)
block|{
name|success
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
DECL|method|requestSync
specifier|private
name|void
name|requestSync
parameter_list|(
name|String
name|baseUrl
parameter_list|,
name|String
name|replica
parameter_list|,
name|String
name|leaderUrl
parameter_list|,
name|String
name|coreName
parameter_list|,
name|int
name|nUpdates
parameter_list|)
block|{
name|ShardCoreRequest
name|sreq
init|=
operator|new
name|ShardCoreRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
name|sreq
operator|.
name|baseUrl
operator|=
name|baseUrl
expr_stmt|;
name|sreq
operator|.
name|purpose
operator|=
literal|1
expr_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|replica
block|}
expr_stmt|;
name|sreq
operator|.
name|actualShards
operator|=
name|sreq
operator|.
name|shards
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"getVersions"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nUpdates
argument_list|)
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"sync"
argument_list|,
name|leaderUrl
argument_list|)
expr_stmt|;
name|shardHandler
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|replica
argument_list|,
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
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
block|}
DECL|method|requestRecoveries
specifier|public
name|void
name|requestRecoveries
parameter_list|()
block|{
for|for
control|(
name|RecoveryRequest
name|rr
range|:
name|recoveryRequests
control|)
block|{
try|try
block|{
name|requestRecovery
argument_list|(
name|rr
operator|.
name|leaderProps
argument_list|,
name|rr
operator|.
name|baseUrl
argument_list|,
name|rr
operator|.
name|coreName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Problem requesting that a replica recover"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|requestRecovery
specifier|private
name|void
name|requestRecovery
parameter_list|(
specifier|final
name|ZkNodeProps
name|leaderProps
parameter_list|,
specifier|final
name|String
name|baseUrl
parameter_list|,
specifier|final
name|String
name|coreName
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|()
block|{
block|{
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|RequestRecovery
name|recoverRequestCmd
init|=
operator|new
name|RequestRecovery
argument_list|()
decl_stmt|;
name|recoverRequestCmd
operator|.
name|setAction
argument_list|(
name|CoreAdminAction
operator|.
name|REQUESTRECOVERY
argument_list|)
expr_stmt|;
name|recoverRequestCmd
operator|.
name|setCoreName
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|baseUrl
argument_list|,
name|SyncStrategy
operator|.
name|this
operator|.
name|client
argument_list|)
init|)
block|{
name|client
operator|.
name|setConnectionTimeout
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setSoTimeout
argument_list|(
literal|120000
argument_list|)
expr_stmt|;
name|client
operator|.
name|request
argument_list|(
name|recoverRequestCmd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|leaderProps
argument_list|)
operator|+
literal|": Could not tell a replica to recover"
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|t
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|updateExecutor
operator|.
name|execute
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
DECL|method|params
specifier|public
specifier|static
name|ModifiableSolrParams
name|params
parameter_list|(
name|String
modifier|...
name|params
parameter_list|)
block|{
name|ModifiableSolrParams
name|msp
init|=
operator|new
name|ModifiableSolrParams
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
name|params
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|msp
operator|.
name|add
argument_list|(
name|params
index|[
name|i
index|]
argument_list|,
name|params
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|msp
return|;
block|}
block|}
end_class

end_unit

