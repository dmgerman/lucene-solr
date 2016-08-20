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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|AtomicReference
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
name|OverseerCollectionMessageHandler
operator|.
name|Cmd
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
name|DocCollection
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
name|common
operator|.
name|params
operator|.
name|CoreAdminParams
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
name|StrUtils
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
name|zookeeper
operator|.
name|KeeperException
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
name|cloud
operator|.
name|OverseerCollectionMessageHandler
operator|.
name|ONLY_IF_DOWN
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|COLLECTION_PROP
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|REPLICA_PROP
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
name|common
operator|.
name|cloud
operator|.
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
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
name|common
operator|.
name|params
operator|.
name|CommonAdminParams
operator|.
name|ASYNC
import|;
end_import

begin_class
DECL|class|DeleteReplicaCmd
specifier|public
class|class
name|DeleteReplicaCmd
implements|implements
name|Cmd
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
DECL|field|ocmh
specifier|private
specifier|final
name|OverseerCollectionMessageHandler
name|ocmh
decl_stmt|;
DECL|method|DeleteReplicaCmd
specifier|public
name|DeleteReplicaCmd
parameter_list|(
name|OverseerCollectionMessageHandler
name|ocmh
parameter_list|)
block|{
name|this
operator|.
name|ocmh
operator|=
name|ocmh
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|call
specifier|public
name|void
name|call
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|,
name|NamedList
name|results
parameter_list|)
throws|throws
name|Exception
block|{
name|deleteReplica
argument_list|(
name|clusterState
argument_list|,
name|message
argument_list|,
name|results
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|deleteReplica
name|void
name|deleteReplica
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ZkNodeProps
name|message
parameter_list|,
name|NamedList
name|results
parameter_list|,
name|Runnable
name|onComplete
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|ocmh
operator|.
name|checkRequired
argument_list|(
name|message
argument_list|,
name|COLLECTION_PROP
argument_list|,
name|SHARD_ID_PROP
argument_list|,
name|REPLICA_PROP
argument_list|)
expr_stmt|;
name|String
name|collectionName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|COLLECTION_PROP
argument_list|)
decl_stmt|;
name|String
name|shard
init|=
name|message
operator|.
name|getStr
argument_list|(
name|SHARD_ID_PROP
argument_list|)
decl_stmt|;
name|String
name|replicaName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|REPLICA_PROP
argument_list|)
decl_stmt|;
name|boolean
name|parallel
init|=
name|message
operator|.
name|getBool
argument_list|(
literal|"parallel"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DocCollection
name|coll
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Slice
name|slice
init|=
name|coll
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid shard name : "
operator|+
name|shard
operator|+
literal|" in collection : "
operator|+
name|collectionName
argument_list|)
throw|;
block|}
name|Replica
name|replica
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|replicaName
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|==
literal|null
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|r
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
name|l
operator|.
name|add
argument_list|(
name|r
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Invalid replica : "
operator|+
name|replicaName
operator|+
literal|" in shard/collection : "
operator|+
name|shard
operator|+
literal|"/"
operator|+
name|collectionName
operator|+
literal|" available replicas are "
operator|+
name|StrUtils
operator|.
name|join
argument_list|(
name|l
argument_list|,
literal|','
argument_list|)
argument_list|)
throw|;
block|}
comment|// If users are being safe and only want to remove a shard if it is down, they can specify onlyIfDown=true
comment|// on the command.
if|if
condition|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|message
operator|.
name|getStr
argument_list|(
name|ONLY_IF_DOWN
argument_list|)
argument_list|)
operator|&&
name|replica
operator|.
name|getState
argument_list|()
operator|!=
name|Replica
operator|.
name|State
operator|.
name|DOWN
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Attempted to remove replica : "
operator|+
name|collectionName
operator|+
literal|"/"
operator|+
name|shard
operator|+
literal|"/"
operator|+
name|replicaName
operator|+
literal|" with onlyIfDown='true', but state is '"
operator|+
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|ShardHandler
name|shardHandler
init|=
name|ocmh
operator|.
name|shardHandlerFactory
operator|.
name|getShardHandler
argument_list|()
decl_stmt|;
name|String
name|core
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
name|String
name|asyncId
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ASYNC
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|requestMap
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncId
operator|!=
literal|null
condition|)
block|{
name|requestMap
operator|.
name|set
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|UNLOAD
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_INDEX
argument_list|,
name|message
operator|.
name|getBool
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_INDEX
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_INSTANCE_DIR
argument_list|,
name|message
operator|.
name|getBool
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_INSTANCE_DIR
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_DATA_DIR
argument_list|,
name|message
operator|.
name|getBool
argument_list|(
name|CoreAdminParams
operator|.
name|DELETE_DATA_DIR
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|isLive
init|=
name|ocmh
operator|.
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
operator|.
name|contains
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isLive
condition|)
block|{
name|ocmh
operator|.
name|sendShardRequest
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|params
argument_list|,
name|shardHandler
argument_list|,
name|asyncId
argument_list|,
name|requestMap
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|callable
init|=
parameter_list|()
lambda|->
block|{
try|try
block|{
if|if
condition|(
name|isLive
condition|)
block|{
name|ocmh
operator|.
name|processResponses
argument_list|(
name|results
argument_list|,
name|shardHandler
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|asyncId
argument_list|,
name|requestMap
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|//check if the core unload removed the corenode zk entry
if|if
condition|(
name|ocmh
operator|.
name|waitForCoreNodeGone
argument_list|(
name|collectionName
argument_list|,
name|shard
argument_list|,
name|replicaName
argument_list|,
literal|5000
argument_list|)
condition|)
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
comment|// try and ensure core info is removed from cluster state
name|ocmh
operator|.
name|deleteCoreNode
argument_list|(
name|collectionName
argument_list|,
name|replicaName
argument_list|,
name|replica
argument_list|,
name|core
argument_list|)
expr_stmt|;
if|if
condition|(
name|ocmh
operator|.
name|waitForCoreNodeGone
argument_list|(
name|collectionName
argument_list|,
name|shard
argument_list|,
name|replicaName
argument_list|,
literal|30000
argument_list|)
condition|)
return|return
name|Boolean
operator|.
name|TRUE
return|;
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|results
operator|.
name|add
argument_list|(
literal|"failure"
argument_list|,
literal|"Could not complete delete "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|onComplete
operator|!=
literal|null
condition|)
name|onComplete
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|parallel
condition|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|callable
operator|.
name|call
argument_list|()
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Could not  remove replica : "
operator|+
name|collectionName
operator|+
literal|"/"
operator|+
name|shard
operator|+
literal|"/"
operator|+
name|replicaName
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|UNKNOWN
argument_list|,
literal|"Error waiting for corenode gone"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|ocmh
operator|.
name|tpe
operator|.
name|submit
argument_list|(
name|callable
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

