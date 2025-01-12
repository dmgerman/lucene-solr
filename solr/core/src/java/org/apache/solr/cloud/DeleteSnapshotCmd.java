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
name|CORE_NAME_PROP
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
name|CommonParams
operator|.
name|NAME
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
name|Optional
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
name|Replica
operator|.
name|State
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
name|solr
operator|.
name|core
operator|.
name|snapshots
operator|.
name|CollectionSnapshotMetaData
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
name|snapshots
operator|.
name|CollectionSnapshotMetaData
operator|.
name|CoreSnapshotMetaData
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
name|snapshots
operator|.
name|CollectionSnapshotMetaData
operator|.
name|SnapshotStatus
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
name|snapshots
operator|.
name|SolrSnapshotManager
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
comment|/**  * This class implements the functionality of deleting a collection level snapshot.  */
end_comment

begin_class
DECL|class|DeleteSnapshotCmd
specifier|public
class|class
name|DeleteSnapshotCmd
implements|implements
name|OverseerCollectionMessageHandler
operator|.
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
DECL|method|DeleteSnapshotCmd
specifier|public
name|DeleteSnapshotCmd
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
DECL|method|call
specifier|public
name|void
name|call
parameter_list|(
name|ClusterState
name|state
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
name|commitName
init|=
name|message
operator|.
name|getStr
argument_list|(
name|CoreAdminParams
operator|.
name|COMMIT_NAME
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|requestMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|NamedList
name|shardRequestResults
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
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
name|SolrZkClient
name|zkClient
init|=
name|ocmh
operator|.
name|overseer
operator|.
name|getZkController
argument_list|()
operator|.
name|getZkClient
argument_list|()
decl_stmt|;
name|Optional
argument_list|<
name|CollectionSnapshotMetaData
argument_list|>
name|meta
init|=
name|SolrSnapshotManager
operator|.
name|getCollectionLevelSnapshot
argument_list|(
name|zkClient
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|meta
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Snapshot not found. Nothing to do.
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Deleting a snapshot for collection={} with commitName={}"
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|existingCores
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Slice
name|s
range|:
name|ocmh
operator|.
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
operator|.
name|getSlices
argument_list|()
control|)
block|{
for|for
control|(
name|Replica
name|r
range|:
name|s
operator|.
name|getReplicas
argument_list|()
control|)
block|{
name|existingCores
operator|.
name|add
argument_list|(
name|r
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|coresWithSnapshot
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreSnapshotMetaData
name|m
range|:
name|meta
operator|.
name|get
argument_list|()
operator|.
name|getReplicaSnapshots
argument_list|()
control|)
block|{
if|if
condition|(
name|existingCores
operator|.
name|contains
argument_list|(
name|m
operator|.
name|getCoreName
argument_list|()
argument_list|)
condition|)
block|{
name|coresWithSnapshot
operator|.
name|add
argument_list|(
name|m
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Existing cores with snapshot for collection={} are {}"
argument_list|,
name|collectionName
argument_list|,
name|existingCores
argument_list|)
expr_stmt|;
for|for
control|(
name|Slice
name|slice
range|:
name|ocmh
operator|.
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollection
argument_list|(
name|collectionName
argument_list|)
operator|.
name|getSlices
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
if|if
condition|(
name|replica
operator|.
name|getState
argument_list|()
operator|==
name|State
operator|.
name|DOWN
condition|)
block|{
continue|continue;
comment|// Since replica is down - no point sending a request.
block|}
comment|// Note - when a snapshot is found in_progress state - it is the result of overseer
comment|// failure while handling the snapshot creation. Since we don't know the exact set of
comment|// replicas to contact at this point, we try on all replicas.
if|if
condition|(
name|meta
operator|.
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|==
name|SnapshotStatus
operator|.
name|InProgress
operator|||
name|coresWithSnapshot
operator|.
name|contains
argument_list|(
name|replica
operator|.
name|getCoreName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|coreName
init|=
name|replica
operator|.
name|getStr
argument_list|(
name|CORE_NAME_PROP
argument_list|)
decl_stmt|;
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
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminAction
operator|.
name|DELETESNAPSHOT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|NAME
argument_list|,
name|slice
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CORE_NAME_PROP
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CoreAdminParams
operator|.
name|COMMIT_NAME
argument_list|,
name|commitName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending deletesnapshot request to core={} with commitName={}"
argument_list|,
name|coreName
argument_list|,
name|commitName
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ocmh
operator|.
name|processResponses
argument_list|(
name|shardRequestResults
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
argument_list|)
expr_stmt|;
name|NamedList
name|success
init|=
operator|(
name|NamedList
operator|)
name|shardRequestResults
operator|.
name|get
argument_list|(
literal|"success"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|replicas
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|success
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|success
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NamedList
name|resp
init|=
operator|(
name|NamedList
operator|)
name|success
operator|.
name|getVal
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// Unfortunately async processing logic doesn't provide the "core" name automatically.
name|String
name|coreName
init|=
operator|(
name|String
operator|)
name|resp
operator|.
name|get
argument_list|(
literal|"core"
argument_list|)
decl_stmt|;
name|coresWithSnapshot
operator|.
name|remove
argument_list|(
name|coreName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|coresWithSnapshot
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// One or more failures.
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to delete a snapshot for collection {} with commitName = {}. Snapshot could not be deleted for following cores {}"
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|,
name|coresWithSnapshot
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CoreSnapshotMetaData
argument_list|>
name|replicasWithSnapshot
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CoreSnapshotMetaData
name|m
range|:
name|meta
operator|.
name|get
argument_list|()
operator|.
name|getReplicaSnapshots
argument_list|()
control|)
block|{
if|if
condition|(
name|coresWithSnapshot
operator|.
name|contains
argument_list|(
name|m
operator|.
name|getCoreName
argument_list|()
argument_list|)
condition|)
block|{
name|replicasWithSnapshot
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update the ZK meta-data to include only cores with the snapshot. This will enable users to figure out
comment|// which cores still contain the named snapshot.
name|CollectionSnapshotMetaData
name|newResult
init|=
operator|new
name|CollectionSnapshotMetaData
argument_list|(
name|meta
operator|.
name|get
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|SnapshotStatus
operator|.
name|Failed
argument_list|,
name|meta
operator|.
name|get
argument_list|()
operator|.
name|getCreationDate
argument_list|()
argument_list|,
name|replicasWithSnapshot
argument_list|)
decl_stmt|;
name|SolrSnapshotManager
operator|.
name|updateCollectionLevelSnapshot
argument_list|(
name|zkClient
argument_list|,
name|collectionName
argument_list|,
name|newResult
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Saved snapshot information for collection={} with commitName={} in Zookeeper as follows"
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|,
name|Utils
operator|.
name|toJSON
argument_list|(
name|newResult
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Failed to delete snapshot on cores "
operator|+
name|coresWithSnapshot
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Delete the ZK path so that we eliminate the references of this snapshot from collection level meta-data.
name|SolrSnapshotManager
operator|.
name|deleteCollectionLevelSnapshot
argument_list|(
name|zkClient
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Deleted Zookeeper snapshot metdata for collection={} with commitName={}"
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Successfully deleted snapshot for collection={} with commitName={}"
argument_list|,
name|collectionName
argument_list|,
name|commitName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

