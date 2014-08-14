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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|util
operator|.
name|StrUtils
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
name|Comparator
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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|OverseerCollectionProcessor
operator|.
name|CREATE_NODE_SET
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
name|OverseerCollectionProcessor
operator|.
name|NUM_SLICES
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
name|MAX_SHARDS_PER_NODE
import|;
end_import

begin_class
DECL|class|Assign
specifier|public
class|class
name|Assign
block|{
DECL|field|COUNT
specifier|private
specifier|static
name|Pattern
name|COUNT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"core_node(\\d+)"
argument_list|)
decl_stmt|;
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
name|Assign
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|assignNode
specifier|public
specifier|static
name|String
name|assignNode
parameter_list|(
name|String
name|collection
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|sliceMap
init|=
name|state
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|sliceMap
operator|==
literal|null
condition|)
block|{
return|return
literal|"core_node1"
return|;
block|}
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|sliceMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|COUNT
operator|.
name|matcher
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|"core_node"
operator|+
operator|(
name|max
operator|+
literal|1
operator|)
return|;
block|}
comment|/**    * Assign a new unique id up to slices count - then add replicas evenly.    *     * @return the assigned shard id    */
DECL|method|assignShard
specifier|public
specifier|static
name|String
name|assignShard
parameter_list|(
name|String
name|collection
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|Integer
name|numShards
parameter_list|)
block|{
if|if
condition|(
name|numShards
operator|==
literal|null
condition|)
block|{
name|numShards
operator|=
literal|1
expr_stmt|;
block|}
name|String
name|returnShardId
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|sliceMap
init|=
name|state
operator|.
name|getActiveSlicesMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
comment|// TODO: now that we create shards ahead of time, is this code needed?  Esp since hash ranges aren't assigned when creating via this method?
if|if
condition|(
name|sliceMap
operator|==
literal|null
condition|)
block|{
return|return
literal|"shard1"
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|shardIdNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|sliceMap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardIdNames
operator|.
name|size
argument_list|()
operator|<
name|numShards
condition|)
block|{
return|return
literal|"shard"
operator|+
operator|(
name|shardIdNames
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|)
return|;
block|}
comment|// TODO: don't need to sort to find shard with fewest replicas!
comment|// else figure out which shard needs more replicas
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|shardId
range|:
name|shardIdNames
control|)
block|{
name|int
name|cnt
init|=
name|sliceMap
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|shardIdNames
argument_list|,
operator|new
name|Comparator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|String
name|o1
parameter_list|,
name|String
name|o2
parameter_list|)
block|{
name|Integer
name|one
init|=
name|map
operator|.
name|get
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|Integer
name|two
init|=
name|map
operator|.
name|get
argument_list|(
name|o2
argument_list|)
decl_stmt|;
return|return
name|one
operator|.
name|compareTo
argument_list|(
name|two
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|returnShardId
operator|=
name|shardIdNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|returnShardId
return|;
block|}
DECL|class|Node
specifier|static
class|class
name|Node
block|{
DECL|field|nodeName
specifier|public
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|field|thisCollectionNodes
specifier|public
name|int
name|thisCollectionNodes
init|=
literal|0
decl_stmt|;
DECL|field|totalNodes
specifier|public
name|int
name|totalNodes
init|=
literal|0
decl_stmt|;
DECL|method|Node
name|Node
parameter_list|(
name|String
name|nodeName
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
expr_stmt|;
block|}
DECL|method|weight
specifier|public
name|int
name|weight
parameter_list|()
block|{
return|return
operator|(
name|thisCollectionNodes
operator|*
literal|100
operator|)
operator|+
name|totalNodes
return|;
block|}
block|}
DECL|method|getNodesForNewShard
specifier|public
specifier|static
name|ArrayList
argument_list|<
name|Node
argument_list|>
name|getNodesForNewShard
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|collectionName
parameter_list|,
name|int
name|numSlices
parameter_list|,
name|int
name|maxShardsPerNode
parameter_list|,
name|int
name|repFactor
parameter_list|,
name|String
name|createNodeSetStr
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|createNodeList
init|=
name|createNodeSetStr
operator|==
literal|null
condition|?
literal|null
else|:
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|createNodeSetStr
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodeList
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
name|nodeList
operator|.
name|addAll
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|createNodeList
operator|!=
literal|null
condition|)
name|nodeList
operator|.
name|retainAll
argument_list|(
name|createNodeList
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Node
argument_list|>
name|nodeNameVsShardCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|nodeList
control|)
name|nodeNameVsShardCount
operator|.
name|put
argument_list|(
name|s
argument_list|,
operator|new
name|Node
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|clusterState
operator|.
name|getCollections
argument_list|()
control|)
block|{
name|DocCollection
name|c
init|=
name|clusterState
operator|.
name|getCollection
argument_list|(
name|s
argument_list|)
decl_stmt|;
comment|//identify suitable nodes  by checking the no:of cores in each of them
for|for
control|(
name|Slice
name|slice
range|:
name|c
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|Collection
argument_list|<
name|Replica
argument_list|>
name|replicas
init|=
name|slice
operator|.
name|getReplicas
argument_list|()
decl_stmt|;
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
control|)
block|{
name|Node
name|count
init|=
name|nodeNameVsShardCount
operator|.
name|get
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|count
operator|.
name|totalNodes
operator|++
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
name|count
operator|.
name|thisCollectionNodes
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|.
name|thisCollectionNodes
operator|>=
name|maxShardsPerNode
condition|)
name|nodeNameVsShardCount
operator|.
name|remove
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|nodeNameVsShardCount
operator|.
name|size
argument_list|()
operator|<=
literal|0
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
literal|"Cannot create collection "
operator|+
name|collectionName
operator|+
literal|". No live Solr-instances"
operator|+
operator|(
operator|(
name|createNodeList
operator|!=
literal|null
operator|)
condition|?
literal|" among Solr-instances specified in "
operator|+
name|CREATE_NODE_SET
operator|+
literal|":"
operator|+
name|createNodeSetStr
else|:
literal|""
operator|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|repFactor
operator|>
name|nodeNameVsShardCount
operator|.
name|size
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Specified "
operator|+
name|ZkStateReader
operator|.
name|REPLICATION_FACTOR
operator|+
literal|" of "
operator|+
name|repFactor
operator|+
literal|" on collection "
operator|+
name|collectionName
operator|+
literal|" is higher than or equal to the number of Solr instances currently live or part of your "
operator|+
name|CREATE_NODE_SET
operator|+
literal|"("
operator|+
name|nodeList
operator|.
name|size
argument_list|()
operator|+
literal|"). Its unusual to run two replica of the same slice on the same Solr-instance."
argument_list|)
expr_stmt|;
block|}
name|int
name|maxCoresAllowedToCreate
init|=
name|maxShardsPerNode
operator|*
name|nodeList
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|requestedCoresToCreate
init|=
name|numSlices
operator|*
name|repFactor
decl_stmt|;
name|int
name|minCoresToCreate
init|=
name|requestedCoresToCreate
decl_stmt|;
if|if
condition|(
name|maxCoresAllowedToCreate
operator|<
name|minCoresToCreate
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
literal|"Cannot create shards "
operator|+
name|collectionName
operator|+
literal|". Value of "
operator|+
name|MAX_SHARDS_PER_NODE
operator|+
literal|" is "
operator|+
name|maxShardsPerNode
operator|+
literal|", and the number of live nodes is "
operator|+
name|nodeList
operator|.
name|size
argument_list|()
operator|+
literal|". This allows a maximum of "
operator|+
name|maxCoresAllowedToCreate
operator|+
literal|" to be created. Value of "
operator|+
name|NUM_SLICES
operator|+
literal|" is "
operator|+
name|numSlices
operator|+
literal|" and value of "
operator|+
name|ZkStateReader
operator|.
name|REPLICATION_FACTOR
operator|+
literal|" is "
operator|+
name|repFactor
operator|+
literal|". This requires "
operator|+
name|requestedCoresToCreate
operator|+
literal|" shards to be created (higher than the allowed number)"
argument_list|)
throw|;
block|}
name|ArrayList
argument_list|<
name|Node
argument_list|>
name|sortedNodeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodeNameVsShardCount
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|sortedNodeList
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Node
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Node
name|x
parameter_list|,
name|Node
name|y
parameter_list|)
block|{
return|return
operator|(
name|x
operator|.
name|weight
argument_list|()
operator|<
name|y
operator|.
name|weight
argument_list|()
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|x
operator|.
name|weight
argument_list|()
operator|==
name|y
operator|.
name|weight
argument_list|()
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|sortedNodeList
return|;
block|}
block|}
end_class

end_unit

