begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

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
name|HashMap
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
name|LinkedHashMap
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
name|Objects
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
name|SolrException
operator|.
name|ErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONWriter
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
name|AUTO_ADD_REPLICAS
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
name|REPLICATION_FACTOR
import|;
end_import

begin_comment
comment|/**  * Models a Collection in zookeeper (but that Java name is obviously taken, hence "DocCollection")  */
end_comment

begin_class
DECL|class|DocCollection
specifier|public
class|class
name|DocCollection
extends|extends
name|ZkNodeProps
implements|implements
name|Iterable
argument_list|<
name|Slice
argument_list|>
block|{
DECL|field|DOC_ROUTER
specifier|public
specifier|static
specifier|final
name|String
name|DOC_ROUTER
init|=
literal|"router"
decl_stmt|;
DECL|field|SHARDS
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS
init|=
literal|"shards"
decl_stmt|;
DECL|field|STATE_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|STATE_FORMAT
init|=
literal|"stateFormat"
decl_stmt|;
DECL|field|RULE
specifier|public
specifier|static
specifier|final
name|String
name|RULE
init|=
literal|"rule"
decl_stmt|;
DECL|field|SNITCH
specifier|public
specifier|static
specifier|final
name|String
name|SNITCH
init|=
literal|"snitch"
decl_stmt|;
DECL|field|znodeVersion
specifier|private
specifier|final
name|int
name|znodeVersion
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|slices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
decl_stmt|;
DECL|field|activeSlices
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|activeSlices
decl_stmt|;
DECL|field|router
specifier|private
specifier|final
name|DocRouter
name|router
decl_stmt|;
DECL|field|znode
specifier|private
specifier|final
name|String
name|znode
decl_stmt|;
DECL|field|replicationFactor
specifier|private
specifier|final
name|Integer
name|replicationFactor
decl_stmt|;
DECL|field|maxShardsPerNode
specifier|private
specifier|final
name|Integer
name|maxShardsPerNode
decl_stmt|;
DECL|field|autoAddReplicas
specifier|private
specifier|final
name|Boolean
name|autoAddReplicas
decl_stmt|;
DECL|method|DocCollection
specifier|public
name|DocCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|DocRouter
name|router
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|slices
argument_list|,
name|props
argument_list|,
name|router
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|ZkStateReader
operator|.
name|CLUSTER_STATE
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param name  The name of the collection    * @param slices The logical shards of the collection.  This is used directly and a copy is not made.    * @param props  The properties of the slice.  This is used directly and a copy is not made.    */
DECL|method|DocCollection
specifier|public
name|DocCollection
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|DocRouter
name|router
parameter_list|,
name|int
name|zkVersion
parameter_list|,
name|String
name|znode
parameter_list|)
block|{
name|super
argument_list|(
name|props
operator|==
literal|null
condition|?
name|props
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
else|:
name|props
argument_list|)
expr_stmt|;
comment|// -1 means any version in ZK CAS, so we choose Integer.MAX_VALUE instead to avoid accidental overwrites
name|this
operator|.
name|znodeVersion
operator|=
name|zkVersion
operator|==
operator|-
literal|1
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|zkVersion
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|slices
operator|=
name|slices
expr_stmt|;
name|this
operator|.
name|activeSlices
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|replicationFactor
operator|=
operator|(
name|Integer
operator|)
name|verifyProp
argument_list|(
name|props
argument_list|,
name|REPLICATION_FACTOR
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxShardsPerNode
operator|=
operator|(
name|Integer
operator|)
name|verifyProp
argument_list|(
name|props
argument_list|,
name|MAX_SHARDS_PER_NODE
argument_list|)
expr_stmt|;
name|Boolean
name|autoAddReplicas
init|=
operator|(
name|Boolean
operator|)
name|verifyProp
argument_list|(
name|props
argument_list|,
name|AUTO_ADD_REPLICAS
argument_list|)
decl_stmt|;
name|this
operator|.
name|autoAddReplicas
operator|=
name|autoAddReplicas
operator|==
literal|null
condition|?
literal|false
else|:
name|autoAddReplicas
expr_stmt|;
name|verifyProp
argument_list|(
name|props
argument_list|,
name|RULE
argument_list|)
expr_stmt|;
name|verifyProp
argument_list|(
name|props
argument_list|,
name|SNITCH
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|>
name|iter
init|=
name|slices
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
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
name|Slice
argument_list|>
name|slice
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|slice
operator|.
name|getValue
argument_list|()
operator|.
name|getState
argument_list|()
operator|==
name|Slice
operator|.
name|State
operator|.
name|ACTIVE
condition|)
name|this
operator|.
name|activeSlices
operator|.
name|put
argument_list|(
name|slice
operator|.
name|getKey
argument_list|()
argument_list|,
name|slice
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
name|this
operator|.
name|znode
operator|=
name|znode
operator|==
literal|null
condition|?
name|ZkStateReader
operator|.
name|CLUSTER_STATE
else|:
name|znode
expr_stmt|;
assert|assert
name|name
operator|!=
literal|null
operator|&&
name|slices
operator|!=
literal|null
assert|;
block|}
DECL|method|verifyProp
specifier|public
specifier|static
name|Object
name|verifyProp
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
name|Object
name|o
init|=
name|props
operator|.
name|get
argument_list|(
name|propName
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
switch|switch
condition|(
name|propName
condition|)
block|{
case|case
name|MAX_SHARDS_PER_NODE
case|:
case|case
name|REPLICATION_FACTOR
case|:
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
case|case
name|AUTO_ADD_REPLICAS
case|:
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
case|case
literal|"snitch"
case|:
case|case
literal|"rule"
case|:
return|return
operator|(
name|List
operator|)
name|o
return|;
default|default:
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unknown property "
operator|+
name|propName
argument_list|)
throw|;
block|}
block|}
comment|/**Use this to make an exact copy of DocCollection with a new set of Slices and every other property as is    * @param slices the new set of Slices    * @return the resulting DocCollection    */
DECL|method|copyWithSlices
specifier|public
name|DocCollection
name|copyWithSlices
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
parameter_list|)
block|{
return|return
operator|new
name|DocCollection
argument_list|(
name|getName
argument_list|()
argument_list|,
name|slices
argument_list|,
name|propMap
argument_list|,
name|router
argument_list|,
name|znodeVersion
argument_list|,
name|znode
argument_list|)
return|;
block|}
comment|/**    * Return collection name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getSlice
specifier|public
name|Slice
name|getSlice
parameter_list|(
name|String
name|sliceName
parameter_list|)
block|{
return|return
name|slices
operator|.
name|get
argument_list|(
name|sliceName
argument_list|)
return|;
block|}
comment|/**    * Gets the list of all slices for this collection.    */
DECL|method|getSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSlices
parameter_list|()
block|{
return|return
name|slices
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Return the list of active slices for this collection.    */
DECL|method|getActiveSlices
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getActiveSlices
parameter_list|()
block|{
return|return
name|activeSlices
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Get the map of all slices (sliceName-&gt;Slice) for this collection.    */
DECL|method|getSlicesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getSlicesMap
parameter_list|()
block|{
return|return
name|slices
return|;
block|}
comment|/**    * Get the map of active slices (sliceName-&gt;Slice) for this collection.    */
DECL|method|getActiveSlicesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|getActiveSlicesMap
parameter_list|()
block|{
return|return
name|activeSlices
return|;
block|}
DECL|method|getZNodeVersion
specifier|public
name|int
name|getZNodeVersion
parameter_list|()
block|{
return|return
name|znodeVersion
return|;
block|}
DECL|method|getStateFormat
specifier|public
name|int
name|getStateFormat
parameter_list|()
block|{
return|return
name|ZkStateReader
operator|.
name|CLUSTER_STATE
operator|.
name|equals
argument_list|(
name|znode
argument_list|)
condition|?
literal|1
else|:
literal|2
return|;
block|}
comment|/**    * @return replication factor for this collection or null if no    *         replication factor exists.    */
DECL|method|getReplicationFactor
specifier|public
name|Integer
name|getReplicationFactor
parameter_list|()
block|{
return|return
name|replicationFactor
return|;
block|}
DECL|method|getAutoAddReplicas
specifier|public
name|boolean
name|getAutoAddReplicas
parameter_list|()
block|{
return|return
name|autoAddReplicas
return|;
block|}
DECL|method|getMaxShardsPerNode
specifier|public
name|int
name|getMaxShardsPerNode
parameter_list|()
block|{
if|if
condition|(
name|maxShardsPerNode
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
name|BAD_REQUEST
argument_list|,
name|MAX_SHARDS_PER_NODE
operator|+
literal|" is not in the cluster state."
argument_list|)
throw|;
block|}
return|return
name|maxShardsPerNode
return|;
block|}
DECL|method|getZNode
specifier|public
name|String
name|getZNode
parameter_list|()
block|{
return|return
name|znode
return|;
block|}
DECL|method|getRouter
specifier|public
name|DocRouter
name|getRouter
parameter_list|()
block|{
return|return
name|router
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DocCollection("
operator|+
name|name
operator|+
literal|")="
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|JSONWriter
name|jsonWriter
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|all
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|slices
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|all
operator|.
name|putAll
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
name|all
operator|.
name|put
argument_list|(
name|SHARDS
argument_list|,
name|slices
argument_list|)
expr_stmt|;
name|jsonWriter
operator|.
name|write
argument_list|(
name|all
argument_list|)
expr_stmt|;
block|}
DECL|method|getReplica
specifier|public
name|Replica
name|getReplica
parameter_list|(
name|String
name|coreNodeName
parameter_list|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|slices
operator|.
name|values
argument_list|()
control|)
block|{
name|Replica
name|replica
init|=
name|slice
operator|.
name|getReplica
argument_list|(
name|coreNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
return|return
name|replica
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getLeader
specifier|public
name|Replica
name|getLeader
parameter_list|(
name|String
name|sliceName
parameter_list|)
block|{
name|Slice
name|slice
init|=
name|getSlice
argument_list|(
name|sliceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|slice
operator|.
name|getLeader
argument_list|()
return|;
block|}
comment|/**    * Check that all replicas in a collection are live    *    * @see CollectionStatePredicate    */
DECL|method|isFullyActive
specifier|public
specifier|static
name|boolean
name|isFullyActive
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|DocCollection
name|collectionState
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|liveNodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionState
operator|==
literal|null
condition|)
return|return
literal|false
return|;
for|for
control|(
name|Slice
name|slice
range|:
name|collectionState
control|)
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|slice
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|isActive
argument_list|(
name|liveNodes
argument_list|)
operator|==
literal|false
condition|)
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Slice
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|slices
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

