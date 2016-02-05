begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|Collections
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
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
name|Aliases
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
name|DocRouter
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
name|ShardParams
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
name|SimpleOrderedMap
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
name|common
operator|.
name|util
operator|.
name|Utils
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

begin_class
DECL|class|ClusterStatus
specifier|public
class|class
name|ClusterStatus
block|{
DECL|field|zkStateReader
specifier|private
specifier|final
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|collection
specifier|private
specifier|final
name|String
name|collection
decl_stmt|;
DECL|field|message
specifier|private
name|ZkNodeProps
name|message
decl_stmt|;
DECL|method|ClusterStatus
specifier|public
name|ClusterStatus
parameter_list|(
name|ZkStateReader
name|zkStateReader
parameter_list|,
name|ZkNodeProps
name|props
parameter_list|)
block|{
name|this
operator|.
name|zkStateReader
operator|=
name|zkStateReader
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|props
expr_stmt|;
name|collection
operator|=
name|props
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|COLLECTION_PROP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getClusterStatus
specifier|public
name|void
name|getClusterStatus
parameter_list|(
name|NamedList
name|results
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
name|zkStateReader
operator|.
name|updateClusterState
argument_list|()
expr_stmt|;
comment|// read aliases
name|Aliases
name|aliases
init|=
name|zkStateReader
operator|.
name|getAliases
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|collectionVsAliases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aliasVsCollections
init|=
name|aliases
operator|.
name|getCollectionAliasMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|aliasVsCollections
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|aliasVsCollections
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|colls
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|','
argument_list|)
decl_stmt|;
name|String
name|alias
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|coll
range|:
name|colls
control|)
block|{
if|if
condition|(
name|collection
operator|==
literal|null
operator|||
name|collection
operator|.
name|equals
argument_list|(
name|coll
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|collectionVsAliases
operator|.
name|get
argument_list|(
name|coll
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|collectionVsAliases
operator|.
name|put
argument_list|(
name|coll
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|Map
name|roles
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|exists
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|roles
operator|=
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|getData
argument_list|(
name|ZkStateReader
operator|.
name|ROLES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
comment|// convert cluster state into a map of writable types
name|byte
index|[]
name|bytes
init|=
name|Utils
operator|.
name|toJSON
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stateMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|collections
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|routeKey
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|)
decl_stmt|;
name|String
name|shard
init|=
name|message
operator|.
name|getStr
argument_list|(
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|collections
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|clusterState
operator|.
name|getCollections
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collections
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
name|NamedList
argument_list|<
name|Object
argument_list|>
name|collectionProps
init|=
operator|new
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|collections
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collectionStatus
init|=
literal|null
decl_stmt|;
name|DocCollection
name|clusterStateCollection
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|requestedShards
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|routeKey
operator|!=
literal|null
condition|)
block|{
name|DocRouter
name|router
init|=
name|clusterStateCollection
operator|.
name|getRouter
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Slice
argument_list|>
name|slices
init|=
name|router
operator|.
name|getSearchSlices
argument_list|(
name|routeKey
argument_list|,
literal|null
argument_list|,
name|clusterStateCollection
argument_list|)
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|slices
control|)
block|{
name|requestedShards
operator|.
name|add
argument_list|(
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|requestedShards
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clusterStateCollection
operator|.
name|getStateFormat
argument_list|()
operator|>
literal|1
condition|)
block|{
name|bytes
operator|=
name|Utils
operator|.
name|toJSON
argument_list|(
name|clusterStateCollection
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|docCollection
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|Utils
operator|.
name|fromJSON
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|collectionStatus
operator|=
name|getCollectionStatus
argument_list|(
name|docCollection
argument_list|,
name|name
argument_list|,
name|requestedShards
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectionStatus
operator|=
name|getCollectionStatus
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|stateMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|,
name|name
argument_list|,
name|requestedShards
argument_list|)
expr_stmt|;
block|}
name|collectionStatus
operator|.
name|put
argument_list|(
literal|"znodeVersion"
argument_list|,
name|clusterStateCollection
operator|.
name|getZNodeVersion
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionVsAliases
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|collectionVsAliases
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|collectionStatus
operator|.
name|put
argument_list|(
literal|"aliases"
argument_list|,
name|collectionVsAliases
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|configName
init|=
name|zkStateReader
operator|.
name|readConfigName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|collectionStatus
operator|.
name|put
argument_list|(
literal|"configName"
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|collectionProps
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|collectionStatus
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|zkStateReader
operator|.
name|getZkClient
argument_list|()
operator|.
name|getChildren
argument_list|(
name|ZkStateReader
operator|.
name|LIVE_NODES_ZKNODE
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// now we need to walk the collectionProps tree to cross-check replica state with live nodes
name|crossCheckReplicaStateWithLiveNodes
argument_list|(
name|liveNodes
argument_list|,
name|collectionProps
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|clusterStatus
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|clusterStatus
operator|.
name|add
argument_list|(
literal|"collections"
argument_list|,
name|collectionProps
argument_list|)
expr_stmt|;
comment|// read cluster properties
name|Map
name|clusterProps
init|=
name|zkStateReader
operator|.
name|getClusterProps
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterProps
operator|!=
literal|null
operator|&&
operator|!
name|clusterProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|clusterStatus
operator|.
name|add
argument_list|(
literal|"properties"
argument_list|,
name|clusterProps
argument_list|)
expr_stmt|;
block|}
comment|// add the alias map too
if|if
condition|(
name|aliasVsCollections
operator|!=
literal|null
operator|&&
operator|!
name|aliasVsCollections
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|clusterStatus
operator|.
name|add
argument_list|(
literal|"aliases"
argument_list|,
name|aliasVsCollections
argument_list|)
expr_stmt|;
block|}
comment|// add the roles map
if|if
condition|(
name|roles
operator|!=
literal|null
condition|)
block|{
name|clusterStatus
operator|.
name|add
argument_list|(
literal|"roles"
argument_list|,
name|roles
argument_list|)
expr_stmt|;
block|}
comment|// add live_nodes
name|clusterStatus
operator|.
name|add
argument_list|(
literal|"live_nodes"
argument_list|,
name|liveNodes
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
literal|"cluster"
argument_list|,
name|clusterStatus
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get collection status from cluster state.    * Can return collection status by given shard name.    *    *    * @param collection collection map parsed from JSON-serialized {@link ClusterState}    * @param name  collection name    * @param requestedShards a set of shards to be returned in the status.    *                        An empty or null values indicates<b>all</b> shards.    * @return map of collection properties    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getCollectionStatus
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getCollectionStatus
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collection
parameter_list|,
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedShards
parameter_list|)
block|{
if|if
condition|(
name|collection
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
literal|"Collection: "
operator|+
name|name
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
if|if
condition|(
name|requestedShards
operator|==
literal|null
operator|||
name|requestedShards
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|collection
return|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|shards
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|collection
operator|.
name|get
argument_list|(
literal|"shards"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|selected
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|selectedShard
range|:
name|requestedShards
control|)
block|{
if|if
condition|(
operator|!
name|shards
operator|.
name|containsKey
argument_list|(
name|selectedShard
argument_list|)
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
literal|"Collection: "
operator|+
name|name
operator|+
literal|" shard: "
operator|+
name|selectedShard
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|selected
operator|.
name|put
argument_list|(
name|selectedShard
argument_list|,
name|shards
operator|.
name|get
argument_list|(
name|selectedShard
argument_list|)
argument_list|)
expr_stmt|;
name|collection
operator|.
name|put
argument_list|(
literal|"shards"
argument_list|,
name|selected
argument_list|)
expr_stmt|;
block|}
return|return
name|collection
return|;
block|}
block|}
comment|/**    * Walks the tree of collection status to verify that any replicas not reporting a "down" status is    * on a live node, if any replicas reporting their status as "active" but the node is not live is    * marked as "down"; used by CLUSTERSTATUS.    * @param liveNodes List of currently live node names.    * @param collectionProps Map of collection status information pulled directly from ZooKeeper.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|crossCheckReplicaStateWithLiveNodes
specifier|protected
name|void
name|crossCheckReplicaStateWithLiveNodes
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|NamedList
argument_list|<
name|Object
argument_list|>
name|collectionProps
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|colls
init|=
name|collectionProps
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|colls
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|next
init|=
name|colls
operator|.
name|next
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|collMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|next
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|shards
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|collMap
operator|.
name|get
argument_list|(
literal|"shards"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|nextShard
range|:
name|shards
operator|.
name|values
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|shardMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|nextShard
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|replicas
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|shardMap
operator|.
name|get
argument_list|(
literal|"replicas"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|nextReplica
range|:
name|replicas
operator|.
name|values
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|replicaMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|nextReplica
decl_stmt|;
if|if
condition|(
name|Replica
operator|.
name|State
operator|.
name|getState
argument_list|(
operator|(
name|String
operator|)
name|replicaMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|)
argument_list|)
operator|!=
name|Replica
operator|.
name|State
operator|.
name|DOWN
condition|)
block|{
comment|// not down, so verify the node is live
name|String
name|node_name
init|=
operator|(
name|String
operator|)
name|replicaMap
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|liveNodes
operator|.
name|contains
argument_list|(
name|node_name
argument_list|)
condition|)
block|{
comment|// node is not live, so this replica is actually down
name|replicaMap
operator|.
name|put
argument_list|(
name|ZkStateReader
operator|.
name|STATE_PROP
argument_list|,
name|Replica
operator|.
name|State
operator|.
name|DOWN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

