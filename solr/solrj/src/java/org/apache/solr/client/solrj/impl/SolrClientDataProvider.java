begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
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
name|Arrays
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
name|client
operator|.
name|solrj
operator|.
name|SolrRequest
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
name|request
operator|.
name|GenericSolrRequest
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
name|response
operator|.
name|SimpleSolrResponse
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
name|autoscaling
operator|.
name|ClusterDataProvider
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
name|autoscaling
operator|.
name|Policy
operator|.
name|ReplicaInfo
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
name|MapWriter
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
name|cloud
operator|.
name|rule
operator|.
name|ImplicitSnitch
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
name|rule
operator|.
name|RemoteCallback
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
name|rule
operator|.
name|SnitchContext
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
name|CommonParams
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
name|params
operator|.
name|SolrParams
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
comment|/**  * Class that implements {@link ClusterStateProvider} accepting a SolrClient  */
end_comment

begin_class
DECL|class|SolrClientDataProvider
specifier|public
class|class
name|SolrClientDataProvider
implements|implements
name|ClusterDataProvider
implements|,
name|MapWriter
block|{
DECL|field|solrClient
specifier|private
specifier|final
name|CloudSolrClient
name|solrClient
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|data
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|liveNodes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
decl_stmt|;
DECL|field|snitchSession
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|snitchSession
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nodeVsTags
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|>
name|nodeVsTags
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SolrClientDataProvider
specifier|public
name|SolrClientDataProvider
parameter_list|(
name|CloudSolrClient
name|solrClient
parameter_list|)
block|{
name|this
operator|.
name|solrClient
operator|=
name|solrClient
expr_stmt|;
name|ZkStateReader
name|zkStateReader
init|=
name|solrClient
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|this
operator|.
name|liveNodes
operator|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterState
operator|.
name|CollectionRef
argument_list|>
name|all
init|=
name|clusterState
operator|.
name|getCollectionStates
argument_list|()
decl_stmt|;
name|all
operator|.
name|forEach
argument_list|(
parameter_list|(
name|collName
parameter_list|,
name|ref
parameter_list|)
lambda|->
block|{
name|DocCollection
name|coll
init|=
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|coll
operator|==
literal|null
condition|)
return|return;
name|coll
operator|.
name|forEachReplica
argument_list|(
parameter_list|(
name|shard
parameter_list|,
name|replica
parameter_list|)
lambda|->
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
argument_list|>
name|nodeData
init|=
name|data
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
name|nodeData
operator|==
literal|null
condition|)
name|data
operator|.
name|put
argument_list|(
name|replica
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|nodeData
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
name|collData
init|=
name|nodeData
operator|.
name|get
argument_list|(
name|collName
argument_list|)
decl_stmt|;
if|if
condition|(
name|collData
operator|==
literal|null
condition|)
name|nodeData
operator|.
name|put
argument_list|(
name|collName
argument_list|,
name|collData
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
name|replicas
init|=
name|collData
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicas
operator|==
literal|null
condition|)
name|collData
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|replicas
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|replicas
operator|.
name|add
argument_list|(
operator|new
name|ReplicaInfo
argument_list|(
name|replica
operator|.
name|getName
argument_list|()
argument_list|,
name|collName
argument_list|,
name|shard
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPolicyNameByCollection
specifier|public
name|String
name|getPolicyNameByCollection
parameter_list|(
name|String
name|coll
parameter_list|)
block|{
name|ClusterState
operator|.
name|CollectionRef
name|state
init|=
name|solrClient
operator|.
name|getClusterStateProvider
argument_list|()
operator|.
name|getState
argument_list|(
name|coll
argument_list|)
decl_stmt|;
return|return
name|state
operator|==
literal|null
operator|||
name|state
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|String
operator|)
name|state
operator|.
name|get
argument_list|()
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"policy"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeValues
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getNodeValues
parameter_list|(
name|String
name|node
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|tags
parameter_list|)
block|{
name|AutoScalingSnitch
name|snitch
init|=
operator|new
name|AutoScalingSnitch
argument_list|()
decl_stmt|;
name|ClientSnitchCtx
name|ctx
init|=
operator|new
name|ClientSnitchCtx
argument_list|(
literal|null
argument_list|,
name|node
argument_list|,
name|snitchSession
argument_list|,
name|solrClient
argument_list|)
decl_stmt|;
name|snitch
operator|.
name|getTags
argument_list|(
name|node
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|tags
argument_list|)
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|nodeVsTags
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|ctx
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ctx
operator|.
name|getTags
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getReplicaInfo
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
argument_list|>
name|getReplicaInfo
parameter_list|(
name|String
name|node
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
block|{
return|return
name|data
operator|.
name|getOrDefault
argument_list|(
name|node
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
return|;
comment|//todo fill other details
block|}
annotation|@
name|Override
DECL|method|getNodes
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getNodes
parameter_list|()
block|{
return|return
name|liveNodes
return|;
block|}
annotation|@
name|Override
DECL|method|writeMap
specifier|public
name|void
name|writeMap
parameter_list|(
name|EntryWriter
name|ew
parameter_list|)
throws|throws
name|IOException
block|{
name|ew
operator|.
name|put
argument_list|(
literal|"liveNodes"
argument_list|,
name|liveNodes
argument_list|)
expr_stmt|;
name|ew
operator|.
name|put
argument_list|(
literal|"replicaInfo"
argument_list|,
name|Utils
operator|.
name|getDeepCopy
argument_list|(
name|data
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|ew
operator|.
name|put
argument_list|(
literal|"nodeValues"
argument_list|,
name|nodeVsTags
argument_list|)
expr_stmt|;
block|}
DECL|class|ClientSnitchCtx
specifier|static
class|class
name|ClientSnitchCtx
extends|extends
name|SnitchContext
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
DECL|field|zkClientClusterStateProvider
name|ZkClientClusterStateProvider
name|zkClientClusterStateProvider
decl_stmt|;
DECL|field|solrClient
name|CloudSolrClient
name|solrClient
decl_stmt|;
DECL|method|ClientSnitchCtx
specifier|public
name|ClientSnitchCtx
parameter_list|(
name|SnitchInfo
name|perSnitch
parameter_list|,
name|String
name|node
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|session
parameter_list|,
name|CloudSolrClient
name|solrClient
parameter_list|)
block|{
name|super
argument_list|(
name|perSnitch
argument_list|,
name|node
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|this
operator|.
name|solrClient
operator|=
name|solrClient
expr_stmt|;
name|this
operator|.
name|zkClientClusterStateProvider
operator|=
operator|(
name|ZkClientClusterStateProvider
operator|)
name|solrClient
operator|.
name|getClusterStateProvider
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getZkJson
specifier|public
name|Map
name|getZkJson
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|KeeperException
throws|,
name|InterruptedException
block|{
return|return
name|Utils
operator|.
name|getJson
argument_list|(
name|zkClientClusterStateProvider
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getZkClient
argument_list|()
argument_list|,
name|path
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|invokeRemote
specifier|public
name|void
name|invokeRemote
parameter_list|(
name|String
name|node
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|,
name|String
name|klas
parameter_list|,
name|RemoteCallback
name|callback
parameter_list|)
block|{      }
DECL|method|invoke
specifier|public
name|SimpleSolrResponse
name|invoke
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|String
name|path
parameter_list|,
name|SolrParams
name|params
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|String
name|url
init|=
name|zkClientClusterStateProvider
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getBaseUrlForNodeName
argument_list|(
name|solrNode
argument_list|)
decl_stmt|;
name|GenericSolrRequest
name|request
init|=
operator|new
name|GenericSolrRequest
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|GET
argument_list|,
name|path
argument_list|,
name|params
argument_list|)
decl_stmt|;
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|()
operator|.
name|withHttpClient
argument_list|(
name|solrClient
operator|.
name|getHttpClient
argument_list|()
argument_list|)
operator|.
name|withBaseSolrUrl
argument_list|(
name|url
argument_list|)
operator|.
name|withResponseParser
argument_list|(
operator|new
name|BinaryResponseParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
init|=
name|client
operator|.
name|request
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|request
operator|.
name|response
operator|.
name|nl
operator|=
name|rsp
expr_stmt|;
return|return
name|request
operator|.
name|response
return|;
block|}
block|}
block|}
comment|//uses metrics API to get node information
DECL|class|AutoScalingSnitch
specifier|static
class|class
name|AutoScalingSnitch
extends|extends
name|ImplicitSnitch
block|{
annotation|@
name|Override
DECL|method|getRemoteInfo
specifier|protected
name|void
name|getRemoteInfo
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedTags
parameter_list|,
name|SnitchContext
name|ctx
parameter_list|)
block|{
name|ClientSnitchCtx
name|snitchContext
init|=
operator|(
name|ClientSnitchCtx
operator|)
name|ctx
decl_stmt|;
name|readSysProps
argument_list|(
name|solrNode
argument_list|,
name|requestedTags
argument_list|,
name|snitchContext
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|groups
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|DISK
argument_list|)
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
literal|"solr.node"
argument_list|)
expr_stmt|;
name|prefixes
operator|.
name|add
argument_list|(
literal|"CONTAINER.fs.usableSpace"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|CORES
argument_list|)
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
literal|"solr.core"
argument_list|)
expr_stmt|;
name|prefixes
operator|.
name|add
argument_list|(
literal|"CORE.coreName"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|SYSLOADAVG
argument_list|)
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
literal|"solr.jvm"
argument_list|)
expr_stmt|;
name|prefixes
operator|.
name|add
argument_list|(
literal|"os.systemLoadAverage"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|HEAPUSAGE
argument_list|)
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
literal|"solr.jvm"
argument_list|)
expr_stmt|;
name|prefixes
operator|.
name|add
argument_list|(
literal|"memory.heap.usage"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|groups
operator|.
name|isEmpty
argument_list|()
operator|||
name|prefixes
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
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
literal|"group"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|groups
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"prefix"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|prefixes
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|SimpleSolrResponse
name|rsp
init|=
name|snitchContext
operator|.
name|invoke
argument_list|(
name|solrNode
argument_list|,
name|CommonParams
operator|.
name|METRICS_PATH
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
name|rsp
operator|.
name|nl
operator|.
name|asMap
argument_list|(
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|DISK
argument_list|)
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
literal|"metrics/solr.node/CONTAINER.fs.usableSpace"
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|DISK
argument_list|,
name|n
operator|.
name|doubleValue
argument_list|()
operator|/
literal|1024.0d
operator|/
literal|1024.0d
operator|/
literal|1024.0d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|CORES
argument_list|)
condition|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Map
name|cores
init|=
operator|(
name|Map
operator|)
name|m
operator|.
name|get
argument_list|(
literal|"metrics"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|cores
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|o
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"solr.core."
argument_list|)
condition|)
name|count
operator|++
expr_stmt|;
block|}
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|CORES
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|SYSLOADAVG
argument_list|)
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
literal|"metrics/solr.jvm/os.systemLoadAverage"
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|SYSLOADAVG
argument_list|,
name|n
operator|.
name|doubleValue
argument_list|()
operator|*
literal|100.0d
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|requestedTags
operator|.
name|contains
argument_list|(
name|HEAPUSAGE
argument_list|)
condition|)
block|{
name|Number
name|n
init|=
operator|(
name|Number
operator|)
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
literal|"metrics/solr.jvm/memory.heap.usage"
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
name|ctx
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
name|HEAPUSAGE
argument_list|,
name|n
operator|.
name|doubleValue
argument_list|()
operator|*
literal|100.0d
argument_list|)
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|readSysProps
specifier|private
name|void
name|readSysProps
parameter_list|(
name|String
name|solrNode
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|requestedTags
parameter_list|,
name|ClientSnitchCtx
name|snitchContext
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|prefixes
init|=
literal|null
decl_stmt|;
name|ModifiableSolrParams
name|params
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sysProp
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|requestedTags
control|)
block|{
if|if
condition|(
operator|!
name|tag
operator|.
name|startsWith
argument_list|(
name|SYSPROP
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|sysProp
operator|==
literal|null
condition|)
block|{
name|prefixes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|sysProp
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|prefixes
operator|.
name|add
argument_list|(
literal|"system.properties"
argument_list|)
expr_stmt|;
block|}
name|sysProp
operator|.
name|add
argument_list|(
name|tag
operator|.
name|substring
argument_list|(
name|SYSPROP
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sysProp
operator|==
literal|null
condition|)
return|return;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"prefix"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|prefixes
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|sysProp
control|)
name|params
operator|.
name|add
argument_list|(
literal|"property"
argument_list|,
name|s
argument_list|)
expr_stmt|;
try|try
block|{
name|SimpleSolrResponse
name|rsp
init|=
name|snitchContext
operator|.
name|invoke
argument_list|(
name|solrNode
argument_list|,
name|CommonParams
operator|.
name|METRICS_PATH
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
name|rsp
operator|.
name|nl
operator|.
name|asMap
argument_list|(
literal|6
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|sysProp
control|)
block|{
name|Object
name|v
init|=
name|Utils
operator|.
name|getObjectByPath
argument_list|(
name|m
argument_list|,
literal|true
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"metrics"
argument_list|,
literal|"solr.jvm"
argument_list|,
literal|"system.properties"
argument_list|,
name|s
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
name|snitchContext
operator|.
name|getTags
argument_list|()
operator|.
name|put
argument_list|(
literal|"sysprop."
operator|+
name|s
argument_list|,
name|v
argument_list|)
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
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

