begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

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
name|Locale
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
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toMap
import|;
end_import

begin_interface
DECL|interface|CollectionParams
specifier|public
interface|interface
name|CollectionParams
block|{
comment|/**    * What action    **/
DECL|field|ACTION
name|String
name|ACTION
init|=
literal|"action"
decl_stmt|;
DECL|field|NAME
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
DECL|enum|LockLevel
enum|enum
name|LockLevel
block|{
DECL|enum constant|CLUSTER
name|CLUSTER
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|COLLECTION
name|COLLECTION
argument_list|(
literal|1
argument_list|)
block|,
DECL|enum constant|SHARD
name|SHARD
argument_list|(
literal|2
argument_list|)
block|,
DECL|enum constant|REPLICA
name|REPLICA
argument_list|(
literal|3
argument_list|)
block|,
DECL|enum constant|NONE
name|NONE
argument_list|(
literal|10
argument_list|)
block|;
DECL|field|level
specifier|public
specifier|final
name|int
name|level
decl_stmt|;
DECL|method|LockLevel
name|LockLevel
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|this
operator|.
name|level
operator|=
name|i
expr_stmt|;
block|}
DECL|method|getChild
specifier|public
name|LockLevel
name|getChild
parameter_list|()
block|{
return|return
name|getLevel
argument_list|(
name|level
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|getLevel
specifier|public
specifier|static
name|LockLevel
name|getLevel
parameter_list|(
name|int
name|i
parameter_list|)
block|{
for|for
control|(
name|LockLevel
name|v
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|v
operator|.
name|level
operator|==
name|i
condition|)
return|return
name|v
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|isHigherOrEqual
specifier|public
name|boolean
name|isHigherOrEqual
parameter_list|(
name|LockLevel
name|that
parameter_list|)
block|{
return|return
name|that
operator|.
name|level
operator|<=
name|level
return|;
block|}
block|}
DECL|enum|CollectionAction
enum|enum
name|CollectionAction
block|{
DECL|enum constant|CREATE
name|CREATE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|DELETE
name|DELETE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|RELOAD
name|RELOAD
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|SYNCSHARD
name|SYNCSHARD
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|CREATEALIAS
name|CREATEALIAS
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|DELETEALIAS
name|DELETEALIAS
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|LISTALIASES
name|LISTALIASES
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|SPLITSHARD
name|SPLITSHARD
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|DELETESHARD
name|DELETESHARD
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|CREATESHARD
name|CREATESHARD
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|DELETEREPLICA
name|DELETEREPLICA
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|FORCELEADER
name|FORCELEADER
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|MIGRATE
name|MIGRATE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|ADDROLE
name|ADDROLE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|REMOVEROLE
name|REMOVEROLE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|CLUSTERPROP
name|CLUSTERPROP
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|REQUESTSTATUS
name|REQUESTSTATUS
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|DELETESTATUS
name|DELETESTATUS
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|ADDREPLICA
name|ADDREPLICA
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|MOVEREPLICA
name|MOVEREPLICA
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|OVERSEERSTATUS
name|OVERSEERSTATUS
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|LIST
name|LIST
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|CLUSTERSTATUS
name|CLUSTERSTATUS
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|ADDREPLICAPROP
name|ADDREPLICAPROP
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|REPLICA
argument_list|)
block|,
DECL|enum constant|DELETEREPLICAPROP
name|DELETEREPLICAPROP
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|REPLICA
argument_list|)
block|,
DECL|enum constant|BALANCESHARDUNIQUE
name|BALANCESHARDUNIQUE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
DECL|enum constant|REBALANCELEADERS
name|REBALANCELEADERS
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|MODIFYCOLLECTION
name|MODIFYCOLLECTION
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|MIGRATESTATEFORMAT
name|MIGRATESTATEFORMAT
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|CLUSTER
argument_list|)
block|,
DECL|enum constant|BACKUP
name|BACKUP
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|RESTORE
name|RESTORE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|CREATESNAPSHOT
name|CREATESNAPSHOT
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|DELETESNAPSHOT
name|DELETESNAPSHOT
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|LISTSNAPSHOTS
name|LISTSNAPSHOTS
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
comment|//only for testing. it just waits for specified time
comment|// these are not exposed via collection API commands
comment|// but the overseer is aware of these tasks
DECL|enum constant|MOCK_COLL_TASK
name|MOCK_COLL_TASK
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|COLLECTION
argument_list|)
block|,
DECL|enum constant|MOCK_SHARD_TASK
name|MOCK_SHARD_TASK
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|SHARD
argument_list|)
block|,
comment|//TODO when we have a node level lock use it here
DECL|enum constant|REPLACENODE
name|REPLACENODE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|DELETENODE
name|DELETENODE
argument_list|(
literal|true
argument_list|,
name|LockLevel
operator|.
name|NONE
argument_list|)
block|,
DECL|enum constant|MOCK_REPLICA_TASK
name|MOCK_REPLICA_TASK
argument_list|(
literal|false
argument_list|,
name|LockLevel
operator|.
name|REPLICA
argument_list|)
block|;
DECL|field|isWrite
specifier|public
specifier|final
name|boolean
name|isWrite
decl_stmt|;
DECL|field|lowerName
specifier|public
specifier|final
name|String
name|lowerName
decl_stmt|;
DECL|field|lockLevel
specifier|public
specifier|final
name|LockLevel
name|lockLevel
decl_stmt|;
DECL|method|CollectionAction
name|CollectionAction
parameter_list|(
name|boolean
name|isWrite
parameter_list|,
name|LockLevel
name|level
parameter_list|)
block|{
name|this
operator|.
name|isWrite
operator|=
name|isWrite
expr_stmt|;
name|this
operator|.
name|lockLevel
operator|=
name|level
expr_stmt|;
name|lowerName
operator|=
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
specifier|static
name|CollectionAction
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
return|return
name|actions
operator|.
name|get
argument_list|(
name|p
operator|==
literal|null
condition|?
literal|null
else|:
name|p
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
DECL|method|isEqual
specifier|public
name|boolean
name|isEqual
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|!=
literal|null
operator|&&
name|lowerName
operator|.
name|equals
argument_list|(
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toLower
specifier|public
name|String
name|toLower
parameter_list|()
block|{
return|return
name|lowerName
return|;
block|}
block|}
DECL|field|actions
name|Map
argument_list|<
name|String
argument_list|,
name|CollectionAction
argument_list|>
name|actions
init|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|Stream
operator|.
name|of
argument_list|(
name|CollectionAction
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toMap
argument_list|(
name|CollectionAction
operator|::
name|toLower
argument_list|,
name|Function
operator|.
expr|<
name|CollectionAction
operator|>
name|identity
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

