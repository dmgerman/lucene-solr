begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLDecoder
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
name|Random
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
name|concurrent
operator|.
name|TimeoutException
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
name|SolrServer
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
name|IsUpdateRequest
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
name|util
operator|.
name|ClientUtils
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
name|common
operator|.
name|cloud
operator|.
name|ZooKeeperException
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
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_comment
comment|/**  * SolrJ client class to communicate with SolrCloud.  * Instances of this class communicate with Zookeeper to discover  * Solr endpoints for SolrCloud collections, and then use the   * {@link LBHttpSolrServer} to issue requests.  */
end_comment

begin_class
DECL|class|CloudSolrServer
specifier|public
class|class
name|CloudSolrServer
extends|extends
name|SolrServer
block|{
DECL|field|zkStateReader
specifier|private
specifier|volatile
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|zkHost
specifier|private
name|String
name|zkHost
decl_stmt|;
comment|// the zk server address
DECL|field|zkConnectTimeout
specifier|private
name|int
name|zkConnectTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|zkClientTimeout
specifier|private
name|int
name|zkClientTimeout
init|=
literal|10000
decl_stmt|;
DECL|field|defaultCollection
specifier|private
specifier|volatile
name|String
name|defaultCollection
decl_stmt|;
DECL|field|lbServer
specifier|private
name|LBHttpSolrServer
name|lbServer
decl_stmt|;
DECL|field|myClient
specifier|private
name|HttpClient
name|myClient
decl_stmt|;
DECL|field|rand
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|cachLock
specifier|private
name|Object
name|cachLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// since the state shouldn't change often, should be very cheap reads
DECL|field|urlLists
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|urlLists
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|leaderUrlLists
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|leaderUrlLists
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|replicasLists
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|replicasLists
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|lastClusterStateHashCode
specifier|private
specifier|volatile
name|int
name|lastClusterStateHashCode
decl_stmt|;
DECL|field|updatesToLeaders
specifier|private
specifier|final
name|boolean
name|updatesToLeaders
decl_stmt|;
comment|/**    * @param zkHost The client endpoint of the zookeeper quorum containing the cloud state,    * in the form HOST:PORT.    */
DECL|method|CloudSolrServer
specifier|public
name|CloudSolrServer
parameter_list|(
name|String
name|zkHost
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|myClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|lbServer
operator|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|myClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|updatesToLeaders
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|CloudSolrServer
specifier|public
name|CloudSolrServer
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|boolean
name|updatesToLeaders
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|myClient
operator|=
name|HttpClientUtil
operator|.
name|createClient
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|lbServer
operator|=
operator|new
name|LBHttpSolrServer
argument_list|(
name|myClient
argument_list|)
expr_stmt|;
name|this
operator|.
name|updatesToLeaders
operator|=
name|updatesToLeaders
expr_stmt|;
block|}
comment|/**    * @param zkHost The client endpoint of the zookeeper quorum containing the cloud state,    * in the form HOST:PORT.    * @param lbServer LBHttpSolrServer instance for requests.     */
DECL|method|CloudSolrServer
specifier|public
name|CloudSolrServer
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|LBHttpSolrServer
name|lbServer
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|lbServer
operator|=
name|lbServer
expr_stmt|;
name|this
operator|.
name|updatesToLeaders
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * @param zkHost The client endpoint of the zookeeper quorum containing the cloud state,    * in the form HOST:PORT.    * @param lbServer LBHttpSolrServer instance for requests.     * @param updatesToLeaders sends updates only to leaders - defaults to true    */
DECL|method|CloudSolrServer
specifier|public
name|CloudSolrServer
parameter_list|(
name|String
name|zkHost
parameter_list|,
name|LBHttpSolrServer
name|lbServer
parameter_list|,
name|boolean
name|updatesToLeaders
parameter_list|)
block|{
name|this
operator|.
name|zkHost
operator|=
name|zkHost
expr_stmt|;
name|this
operator|.
name|lbServer
operator|=
name|lbServer
expr_stmt|;
name|this
operator|.
name|updatesToLeaders
operator|=
name|updatesToLeaders
expr_stmt|;
block|}
DECL|method|getZkStateReader
specifier|public
name|ZkStateReader
name|getZkStateReader
parameter_list|()
block|{
return|return
name|zkStateReader
return|;
block|}
comment|/** Sets the default collection for request */
DECL|method|setDefaultCollection
specifier|public
name|void
name|setDefaultCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|this
operator|.
name|defaultCollection
operator|=
name|collection
expr_stmt|;
block|}
comment|/** Set the connect timeout to the zookeeper ensemble in ms */
DECL|method|setZkConnectTimeout
specifier|public
name|void
name|setZkConnectTimeout
parameter_list|(
name|int
name|zkConnectTimeout
parameter_list|)
block|{
name|this
operator|.
name|zkConnectTimeout
operator|=
name|zkConnectTimeout
expr_stmt|;
block|}
comment|/** Set the timeout to the zookeeper ensemble in ms */
DECL|method|setZkClientTimeout
specifier|public
name|void
name|setZkClientTimeout
parameter_list|(
name|int
name|zkClientTimeout
parameter_list|)
block|{
name|this
operator|.
name|zkClientTimeout
operator|=
name|zkClientTimeout
expr_stmt|;
block|}
comment|/**    * Connect to the zookeeper ensemble.    * This is an optional method that may be used to force a connect before any other requests are sent.    *    */
DECL|method|connect
specifier|public
name|void
name|connect
parameter_list|()
block|{
if|if
condition|(
name|zkStateReader
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|zkStateReader
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|ZkStateReader
name|zk
init|=
operator|new
name|ZkStateReader
argument_list|(
name|zkHost
argument_list|,
name|zkConnectTimeout
argument_list|,
name|zkClientTimeout
argument_list|)
decl_stmt|;
name|zk
operator|.
name|createClusterStateWatchersAndUpdate
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
name|zk
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ZooKeeperException
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
catch|catch
parameter_list|(
name|KeeperException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ZooKeeperException
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ZooKeeperException
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
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ZooKeeperException
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
block|}
annotation|@
name|Override
DECL|method|request
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
name|SolrRequest
name|request
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|connect
argument_list|()
expr_stmt|;
comment|// TODO: if you can hash here, you could favor the shard leader
name|ClusterState
name|clusterState
init|=
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
decl_stmt|;
name|boolean
name|sendToLeaders
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|replicas
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|instanceof
name|IsUpdateRequest
operator|&&
name|updatesToLeaders
condition|)
block|{
name|sendToLeaders
operator|=
literal|true
expr_stmt|;
name|replicas
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|SolrParams
name|reqParams
init|=
name|request
operator|.
name|getParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|reqParams
operator|==
literal|null
condition|)
block|{
name|reqParams
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|theUrlList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/admin/collections"
argument_list|)
operator|||
name|request
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/admin/cores"
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|liveNode
range|:
name|liveNodes
control|)
block|{
name|int
name|splitPointBetweenHostPortAndContext
init|=
name|liveNode
operator|.
name|indexOf
argument_list|(
literal|"_"
argument_list|)
decl_stmt|;
name|theUrlList
operator|.
name|add
argument_list|(
literal|"http://"
operator|+
name|liveNode
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|splitPointBetweenHostPortAndContext
argument_list|)
operator|+
literal|"/"
operator|+
name|URLDecoder
operator|.
name|decode
argument_list|(
name|liveNode
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|substring
argument_list|(
name|splitPointBetweenHostPortAndContext
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|collection
init|=
name|reqParams
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|,
name|defaultCollection
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"No collection param specified on request and no default collection has been set."
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|collectionsList
init|=
name|getCollectionList
argument_list|(
name|clusterState
argument_list|,
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
name|collectionsList
operator|.
name|size
argument_list|()
operator|==
literal|0
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
literal|"Could not find collection: "
operator|+
name|collection
argument_list|)
throw|;
block|}
name|collection
operator|=
name|collectionsList
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|StringBuilder
name|collectionString
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|collectionsList
operator|.
name|iterator
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
name|collectionsList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|col
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|collectionString
operator|.
name|append
argument_list|(
name|col
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|collectionsList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|collectionString
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: not a big deal because of the caching, but we could avoid looking
comment|// at every shard
comment|// when getting leaders if we tweaked some things
comment|// Retrieve slices from the cloud state and, for each collection
comment|// specified,
comment|// add it to the Map of slices.
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|collectionName
range|:
name|collectionsList
control|)
block|{
name|Collection
argument_list|<
name|Slice
argument_list|>
name|colSlices
init|=
name|clusterState
operator|.
name|getActiveSlices
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|colSlices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
literal|"Could not find collection:"
operator|+
name|collectionName
argument_list|)
throw|;
block|}
name|ClientUtils
operator|.
name|addSlices
argument_list|(
name|slices
argument_list|,
name|collectionName
argument_list|,
name|colSlices
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
init|=
name|clusterState
operator|.
name|getLiveNodes
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|cachLock
init|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|leaderUrlList
init|=
name|leaderUrlLists
operator|.
name|get
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urlList
init|=
name|urlLists
operator|.
name|get
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|replicasList
init|=
name|replicasLists
operator|.
name|get
argument_list|(
name|collection
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|sendToLeaders
operator|&&
name|leaderUrlList
operator|==
literal|null
operator|)
operator|||
operator|(
operator|!
name|sendToLeaders
operator|&&
name|urlList
operator|==
literal|null
operator|)
operator|||
name|clusterState
operator|.
name|hashCode
argument_list|()
operator|!=
name|this
operator|.
name|lastClusterStateHashCode
condition|)
block|{
comment|// build a map of unique nodes
comment|// TODO: allow filtering by group, role, etc
name|Map
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
name|nodes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ZkNodeProps
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|urlList2
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
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
for|for
control|(
name|ZkNodeProps
name|nodeProps
range|:
name|slice
operator|.
name|getReplicasMap
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|ZkCoreNodeProps
name|coreNodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|nodeProps
argument_list|)
decl_stmt|;
name|String
name|node
init|=
name|coreNodeProps
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|liveNodes
operator|.
name|contains
argument_list|(
name|coreNodeProps
operator|.
name|getNodeName
argument_list|()
argument_list|)
operator|||
operator|!
name|coreNodeProps
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|ZkStateReader
operator|.
name|ACTIVE
argument_list|)
condition|)
continue|continue;
if|if
condition|(
name|nodes
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|nodeProps
argument_list|)
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|sendToLeaders
operator|||
operator|(
name|sendToLeaders
operator|&&
name|coreNodeProps
operator|.
name|isLeader
argument_list|()
operator|)
condition|)
block|{
name|String
name|url
init|=
name|coreNodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|urlList2
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sendToLeaders
condition|)
block|{
name|String
name|url
init|=
name|coreNodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|replicas
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|sendToLeaders
condition|)
block|{
name|this
operator|.
name|leaderUrlLists
operator|.
name|put
argument_list|(
name|collection
argument_list|,
name|urlList2
argument_list|)
expr_stmt|;
name|leaderUrlList
operator|=
name|urlList2
expr_stmt|;
name|this
operator|.
name|replicasLists
operator|.
name|put
argument_list|(
name|collection
argument_list|,
name|replicas
argument_list|)
expr_stmt|;
name|replicasList
operator|=
name|replicas
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|urlLists
operator|.
name|put
argument_list|(
name|collection
argument_list|,
name|urlList2
argument_list|)
expr_stmt|;
name|urlList
operator|=
name|urlList2
expr_stmt|;
block|}
name|this
operator|.
name|lastClusterStateHashCode
operator|=
name|clusterState
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sendToLeaders
condition|)
block|{
name|theUrlList
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|leaderUrlList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|theUrlList
operator|.
name|addAll
argument_list|(
name|leaderUrlList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|theUrlList
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|urlList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|theUrlList
operator|.
name|addAll
argument_list|(
name|urlList
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|theUrlList
argument_list|,
name|rand
argument_list|)
expr_stmt|;
if|if
condition|(
name|sendToLeaders
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|theReplicas
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|replicasList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|theReplicas
operator|.
name|addAll
argument_list|(
name|replicasList
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|theReplicas
argument_list|,
name|rand
argument_list|)
expr_stmt|;
comment|// System.out.println("leaders:" + theUrlList);
comment|// System.out.println("replicas:" + theReplicas);
name|theUrlList
operator|.
name|addAll
argument_list|(
name|theReplicas
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// System.out.println("########################## MAKING REQUEST TO " +
comment|// theUrlList);
name|LBHttpSolrServer
operator|.
name|Req
name|req
init|=
operator|new
name|LBHttpSolrServer
operator|.
name|Req
argument_list|(
name|request
argument_list|,
name|theUrlList
argument_list|)
decl_stmt|;
name|LBHttpSolrServer
operator|.
name|Rsp
name|rsp
init|=
name|lbServer
operator|.
name|request
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|rsp
operator|.
name|getResponse
argument_list|()
return|;
block|}
DECL|method|getCollectionList
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getCollectionList
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|collection
parameter_list|)
block|{
comment|// Extract each comma separated collection name and store in a List.
name|List
argument_list|<
name|String
argument_list|>
name|rawCollectionsList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|collection
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
name|collectionsList
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// validate collections
for|for
control|(
name|String
name|collectionName
range|:
name|rawCollectionsList
control|)
block|{
if|if
condition|(
operator|!
name|clusterState
operator|.
name|getCollections
argument_list|()
operator|.
name|contains
argument_list|(
name|collectionName
argument_list|)
condition|)
block|{
name|Aliases
name|aliases
init|=
name|zkStateReader
operator|.
name|getAliases
argument_list|()
decl_stmt|;
name|String
name|alias
init|=
name|aliases
operator|.
name|getCollectionAlias
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|alias
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|aliasList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|alias
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|collectionsList
operator|.
name|addAll
argument_list|(
name|aliasList
argument_list|)
expr_stmt|;
continue|continue;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Collection not found: "
operator|+
name|collectionName
argument_list|)
throw|;
block|}
name|collectionsList
operator|.
name|add
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
block|}
return|return
name|collectionsList
return|;
block|}
annotation|@
name|Override
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|zkStateReader
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|zkStateReader
operator|!=
literal|null
condition|)
name|zkStateReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|myClient
operator|!=
literal|null
condition|)
block|{
name|myClient
operator|.
name|getConnectionManager
argument_list|()
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getLbServer
specifier|public
name|LBHttpSolrServer
name|getLbServer
parameter_list|()
block|{
return|return
name|lbServer
return|;
block|}
DECL|method|isUpdatesToLeaders
specifier|public
name|boolean
name|isUpdatesToLeaders
parameter_list|()
block|{
return|return
name|updatesToLeaders
return|;
block|}
comment|// for tests
DECL|method|getUrlLists
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getUrlLists
parameter_list|()
block|{
return|return
name|urlLists
return|;
block|}
comment|//for tests
DECL|method|getLeaderUrlLists
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getLeaderUrlLists
parameter_list|()
block|{
return|return
name|leaderUrlLists
return|;
block|}
comment|//for tests
DECL|method|getReplicasLists
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getReplicasLists
parameter_list|()
block|{
return|return
name|replicasLists
return|;
block|}
block|}
end_class

end_unit

