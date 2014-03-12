begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LinkedHashMap
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

begin_comment
comment|/**  * A Slice contains immutable information about a logical shard (all replicas that share the same shard id).  */
end_comment

begin_class
DECL|class|Slice
specifier|public
class|class
name|Slice
extends|extends
name|ZkNodeProps
block|{
DECL|field|REPLICAS
specifier|public
specifier|static
name|String
name|REPLICAS
init|=
literal|"replicas"
decl_stmt|;
DECL|field|RANGE
specifier|public
specifier|static
name|String
name|RANGE
init|=
literal|"range"
decl_stmt|;
DECL|field|STATE
specifier|public
specifier|static
name|String
name|STATE
init|=
literal|"state"
decl_stmt|;
DECL|field|LEADER
specifier|public
specifier|static
name|String
name|LEADER
init|=
literal|"leader"
decl_stmt|;
comment|// FUTURE: do we want to record the leader as a slice property in the JSON (as opposed to isLeader as a replica property?)
DECL|field|ACTIVE
specifier|public
specifier|static
name|String
name|ACTIVE
init|=
literal|"active"
decl_stmt|;
DECL|field|INACTIVE
specifier|public
specifier|static
name|String
name|INACTIVE
init|=
literal|"inactive"
decl_stmt|;
DECL|field|CONSTRUCTION
specifier|public
specifier|static
name|String
name|CONSTRUCTION
init|=
literal|"construction"
decl_stmt|;
DECL|field|RECOVERY
specifier|public
specifier|static
name|String
name|RECOVERY
init|=
literal|"recovery"
decl_stmt|;
DECL|field|PARENT
specifier|public
specifier|static
name|String
name|PARENT
init|=
literal|"parent"
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|range
specifier|private
specifier|final
name|DocRouter
operator|.
name|Range
name|range
decl_stmt|;
DECL|field|replicationFactor
specifier|private
specifier|final
name|Integer
name|replicationFactor
decl_stmt|;
comment|// FUTURE: optional per-slice override of the collection replicationFactor
DECL|field|replicas
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|replicas
decl_stmt|;
DECL|field|leader
specifier|private
specifier|final
name|Replica
name|leader
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|String
name|state
decl_stmt|;
DECL|field|parent
specifier|private
specifier|final
name|String
name|parent
decl_stmt|;
DECL|field|routingRules
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingRule
argument_list|>
name|routingRules
decl_stmt|;
comment|/**    * @param name  The name of the slice    * @param replicas The replicas of the slice.  This is used directly and a copy is not made.  If null, replicas will be constructed from props.    * @param props  The properties of the slice - a shallow copy will always be made.    */
DECL|method|Slice
specifier|public
name|Slice
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|replicas
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|props
parameter_list|)
block|{
name|super
argument_list|(
name|props
operator|==
literal|null
condition|?
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|2
argument_list|)
else|:
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|props
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|Object
name|rangeObj
init|=
name|propMap
operator|.
name|get
argument_list|(
name|RANGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|propMap
operator|.
name|containsKey
argument_list|(
name|STATE
argument_list|)
operator|&&
name|propMap
operator|.
name|get
argument_list|(
name|STATE
argument_list|)
operator|!=
literal|null
condition|)
name|this
operator|.
name|state
operator|=
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|STATE
argument_list|)
expr_stmt|;
else|else
block|{
name|this
operator|.
name|state
operator|=
name|ACTIVE
expr_stmt|;
comment|//Default to ACTIVE
name|propMap
operator|.
name|put
argument_list|(
name|STATE
argument_list|,
name|this
operator|.
name|state
argument_list|)
expr_stmt|;
block|}
name|DocRouter
operator|.
name|Range
name|tmpRange
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rangeObj
operator|instanceof
name|DocRouter
operator|.
name|Range
condition|)
block|{
name|tmpRange
operator|=
operator|(
name|DocRouter
operator|.
name|Range
operator|)
name|rangeObj
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rangeObj
operator|!=
literal|null
condition|)
block|{
comment|// Doesn't support custom implementations of Range, but currently not needed.
name|tmpRange
operator|=
name|DocRouter
operator|.
name|DEFAULT
operator|.
name|fromString
argument_list|(
name|rangeObj
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|range
operator|=
name|tmpRange
expr_stmt|;
comment|/** debugging.  this isn't an error condition for custom sharding.     if (range == null) {       System.out.println("###### NO RANGE for " + name + " props=" + props);     }     **/
if|if
condition|(
name|propMap
operator|.
name|containsKey
argument_list|(
name|PARENT
argument_list|)
operator|&&
name|propMap
operator|.
name|get
argument_list|(
name|PARENT
argument_list|)
operator|!=
literal|null
condition|)
name|this
operator|.
name|parent
operator|=
operator|(
name|String
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|PARENT
argument_list|)
expr_stmt|;
else|else
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|replicationFactor
operator|=
literal|null
expr_stmt|;
comment|// future
comment|// add the replicas *after* the other properties (for aesthetics, so it's easy to find slice properties in the JSON output)
name|this
operator|.
name|replicas
operator|=
name|replicas
operator|!=
literal|null
condition|?
name|replicas
else|:
name|makeReplicas
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propMap
operator|.
name|get
argument_list|(
name|REPLICAS
argument_list|)
argument_list|)
expr_stmt|;
name|propMap
operator|.
name|put
argument_list|(
name|REPLICAS
argument_list|,
name|this
operator|.
name|replicas
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rules
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|propMap
operator|.
name|get
argument_list|(
literal|"routingRules"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rules
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|routingRules
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|rules
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|o
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|RoutingRule
name|rule
init|=
operator|new
name|RoutingRule
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|map
argument_list|)
decl_stmt|;
name|routingRules
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|rule
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|routingRules
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|RoutingRule
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|this
operator|.
name|routingRules
operator|=
literal|null
expr_stmt|;
block|}
name|leader
operator|=
name|findLeader
argument_list|()
expr_stmt|;
block|}
DECL|method|makeReplicas
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|makeReplicas
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|genericReplicas
parameter_list|)
block|{
if|if
condition|(
name|genericReplicas
operator|==
literal|null
condition|)
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|(
literal|1
argument_list|)
return|;
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|result
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|genericReplicas
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|genericReplicas
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Replica
name|r
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|Replica
condition|)
block|{
name|r
operator|=
operator|(
name|Replica
operator|)
name|val
expr_stmt|;
block|}
else|else
block|{
name|r
operator|=
operator|new
name|Replica
argument_list|(
name|name
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|val
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|findLeader
specifier|private
name|Replica
name|findLeader
parameter_list|()
block|{
for|for
control|(
name|Replica
name|replica
range|:
name|replicas
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|getStr
argument_list|(
name|LEADER
argument_list|)
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
comment|/**    * Return slice name (shard id).    */
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
comment|/**    * Gets the list of replicas for this slice.    */
DECL|method|getReplicas
specifier|public
name|Collection
argument_list|<
name|Replica
argument_list|>
name|getReplicas
parameter_list|()
block|{
return|return
name|replicas
operator|.
name|values
argument_list|()
return|;
block|}
comment|/**    * Get the map of coreNodeName to replicas for this slice.    */
DECL|method|getReplicasMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|getReplicasMap
parameter_list|()
block|{
return|return
name|replicas
return|;
block|}
DECL|method|getReplicasCopy
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|getReplicasCopy
parameter_list|()
block|{
return|return
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|replicas
argument_list|)
return|;
block|}
DECL|method|getLeader
specifier|public
name|Replica
name|getLeader
parameter_list|()
block|{
return|return
name|leader
return|;
block|}
DECL|method|getReplica
specifier|public
name|Replica
name|getReplica
parameter_list|(
name|String
name|replicaName
parameter_list|)
block|{
return|return
name|replicas
operator|.
name|get
argument_list|(
name|replicaName
argument_list|)
return|;
block|}
DECL|method|getRange
specifier|public
name|DocRouter
operator|.
name|Range
name|getRange
parameter_list|()
block|{
return|return
name|range
return|;
block|}
DECL|method|getState
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|getParent
specifier|public
name|String
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
DECL|method|getRoutingRules
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingRule
argument_list|>
name|getRoutingRules
parameter_list|()
block|{
return|return
name|routingRules
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
name|name
operator|+
literal|':'
operator|+
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|propMap
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
name|jsonWriter
operator|.
name|write
argument_list|(
name|propMap
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

