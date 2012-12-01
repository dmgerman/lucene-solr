begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ConnectException
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
name|CompletionService
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
name|ExecutionException
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
name|ExecutorCompletionService
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
name|Future
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
name|SolrResponse
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
name|impl
operator|.
name|HttpSolrServer
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
name|LBHttpSolrServer
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
name|QueryRequest
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
name|cloud
operator|.
name|CloudDescriptor
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
name|ZkController
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
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_class
DECL|class|HttpShardHandler
specifier|public
class|class
name|HttpShardHandler
extends|extends
name|ShardHandler
block|{
DECL|field|httpShardHandlerFactory
specifier|private
name|HttpShardHandlerFactory
name|httpShardHandlerFactory
decl_stmt|;
DECL|field|completionService
specifier|private
name|CompletionService
argument_list|<
name|ShardResponse
argument_list|>
name|completionService
decl_stmt|;
DECL|field|pending
specifier|private
name|Set
argument_list|<
name|Future
argument_list|<
name|ShardResponse
argument_list|>
argument_list|>
name|pending
decl_stmt|;
DECL|field|shardToURLs
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
name|shardToURLs
decl_stmt|;
DECL|field|httpClient
specifier|private
name|HttpClient
name|httpClient
decl_stmt|;
DECL|method|HttpShardHandler
specifier|public
name|HttpShardHandler
parameter_list|(
name|HttpShardHandlerFactory
name|httpShardHandlerFactory
parameter_list|,
name|HttpClient
name|httpClient
parameter_list|)
block|{
name|this
operator|.
name|httpClient
operator|=
name|httpClient
expr_stmt|;
name|this
operator|.
name|httpShardHandlerFactory
operator|=
name|httpShardHandlerFactory
expr_stmt|;
name|completionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<
name|ShardResponse
argument_list|>
argument_list|(
name|httpShardHandlerFactory
operator|.
name|commExecutor
argument_list|)
expr_stmt|;
name|pending
operator|=
operator|new
name|HashSet
argument_list|<
name|Future
argument_list|<
name|ShardResponse
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
comment|// maps "localhost:8983|localhost:7574" to a shuffled List("http://localhost:8983","http://localhost:7574")
comment|// This is primarily to keep track of what order we should use to query the replicas of a shard
comment|// so that we use the same replica for all phases of a distributed request.
name|shardToURLs
operator|=
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
expr_stmt|;
block|}
DECL|class|SimpleSolrResponse
specifier|private
specifier|static
class|class
name|SimpleSolrResponse
extends|extends
name|SolrResponse
block|{
DECL|field|elapsedTime
name|long
name|elapsedTime
decl_stmt|;
DECL|field|nl
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
decl_stmt|;
annotation|@
name|Override
DECL|method|getElapsedTime
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
return|return
name|elapsedTime
return|;
block|}
annotation|@
name|Override
DECL|method|getResponse
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getResponse
parameter_list|()
block|{
return|return
name|nl
return|;
block|}
annotation|@
name|Override
DECL|method|setResponse
specifier|public
name|void
name|setResponse
parameter_list|(
name|NamedList
argument_list|<
name|Object
argument_list|>
name|rsp
parameter_list|)
block|{
name|nl
operator|=
name|rsp
expr_stmt|;
block|}
block|}
comment|// Not thread safe... don't use in Callable.
comment|// Don't modify the returned URL list.
DECL|method|getURLs
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getURLs
parameter_list|(
name|String
name|shard
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|shardToURLs
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|urls
operator|==
literal|null
condition|)
block|{
name|urls
operator|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|shard
argument_list|,
literal|"|"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// convert shard to URL
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|urls
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|urls
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|httpShardHandlerFactory
operator|.
name|scheme
operator|+
name|urls
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//
comment|// Shuffle the list instead of use round-robin by default.
comment|// This prevents accidental synchronization where multiple shards could get in sync
comment|// and query the same replica at the same time.
comment|//
if|if
condition|(
name|urls
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
name|Collections
operator|.
name|shuffle
argument_list|(
name|urls
argument_list|,
name|httpShardHandlerFactory
operator|.
name|r
argument_list|)
expr_stmt|;
name|shardToURLs
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|urls
argument_list|)
expr_stmt|;
block|}
return|return
name|urls
return|;
block|}
DECL|method|submit
specifier|public
name|void
name|submit
parameter_list|(
specifier|final
name|ShardRequest
name|sreq
parameter_list|,
specifier|final
name|String
name|shard
parameter_list|,
specifier|final
name|ModifiableSolrParams
name|params
parameter_list|)
block|{
comment|// do this outside of the callable for thread safety reasons
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|getURLs
argument_list|(
name|shard
argument_list|)
decl_stmt|;
name|Callable
argument_list|<
name|ShardResponse
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|ShardResponse
argument_list|>
argument_list|()
block|{
specifier|public
name|ShardResponse
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|ShardResponse
name|srsp
init|=
operator|new
name|ShardResponse
argument_list|()
decl_stmt|;
name|srsp
operator|.
name|setShardRequest
argument_list|(
name|sreq
argument_list|)
expr_stmt|;
name|srsp
operator|.
name|setShard
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|SimpleSolrResponse
name|ssr
init|=
operator|new
name|SimpleSolrResponse
argument_list|()
decl_stmt|;
name|srsp
operator|.
name|setSolrResponse
argument_list|(
name|ssr
argument_list|)
expr_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|WT
argument_list|)
expr_stmt|;
comment|// use default (currently javabin)
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|)
expr_stmt|;
comment|// SolrRequest req = new QueryRequest(SolrRequest.METHOD.POST, "/select");
comment|// use generic request to avoid extra processing of queries
name|QueryRequest
name|req
init|=
operator|new
name|QueryRequest
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|req
operator|.
name|setMethod
argument_list|(
name|SolrRequest
operator|.
name|METHOD
operator|.
name|POST
argument_list|)
expr_stmt|;
comment|// no need to set the response parser as binary is the default
comment|// req.setResponseParser(new BinaryResponseParser());
comment|// if there are no shards available for a slice, urls.size()==0
if|if
condition|(
name|urls
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// TODO: what's the right error code here? We should use the same thing when
comment|// all of the servers for a shard are down.
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"no servers hosting shard: "
operator|+
name|shard
argument_list|)
throw|;
block|}
if|if
condition|(
name|urls
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
name|String
name|url
init|=
name|urls
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|srsp
operator|.
name|setShardAddress
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|SolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|url
argument_list|,
name|httpClient
argument_list|)
decl_stmt|;
name|ssr
operator|.
name|nl
operator|=
name|server
operator|.
name|request
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LBHttpSolrServer
operator|.
name|Rsp
name|rsp
init|=
name|httpShardHandlerFactory
operator|.
name|loadbalancer
operator|.
name|request
argument_list|(
operator|new
name|LBHttpSolrServer
operator|.
name|Req
argument_list|(
name|req
argument_list|,
name|urls
argument_list|)
argument_list|)
decl_stmt|;
name|ssr
operator|.
name|nl
operator|=
name|rsp
operator|.
name|getResponse
argument_list|()
expr_stmt|;
name|srsp
operator|.
name|setShardAddress
argument_list|(
name|rsp
operator|.
name|getServer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConnectException
name|cex
parameter_list|)
block|{
name|srsp
operator|.
name|setException
argument_list|(
name|cex
argument_list|)
expr_stmt|;
comment|//????
block|}
catch|catch
parameter_list|(
name|Throwable
name|th
parameter_list|)
block|{
name|srsp
operator|.
name|setException
argument_list|(
name|th
argument_list|)
expr_stmt|;
if|if
condition|(
name|th
operator|instanceof
name|SolrException
condition|)
block|{
name|srsp
operator|.
name|setResponseCode
argument_list|(
operator|(
operator|(
name|SolrException
operator|)
name|th
operator|)
operator|.
name|code
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|srsp
operator|.
name|setResponseCode
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|ssr
operator|.
name|elapsedTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
expr_stmt|;
return|return
name|srsp
return|;
block|}
block|}
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|completionService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** returns a ShardResponse of the last response correlated with a ShardRequest.  This won't     * return early if it runs into an error.      **/
DECL|method|takeCompletedIncludingErrors
specifier|public
name|ShardResponse
name|takeCompletedIncludingErrors
parameter_list|()
block|{
return|return
name|take
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/** returns a ShardResponse of the last response correlated with a ShardRequest,    * or immediately returns a ShardResponse if there was an error detected    */
DECL|method|takeCompletedOrError
specifier|public
name|ShardResponse
name|takeCompletedOrError
parameter_list|()
block|{
return|return
name|take
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|take
specifier|private
name|ShardResponse
name|take
parameter_list|(
name|boolean
name|bailOnError
parameter_list|)
block|{
while|while
condition|(
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Future
argument_list|<
name|ShardResponse
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
name|pending
operator|.
name|remove
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|ShardResponse
name|rsp
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|bailOnError
operator|&&
name|rsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
return|return
name|rsp
return|;
comment|// if exception, return immediately
comment|// add response to the response list... we do this after the take() and
comment|// not after the completion of "call" so we know when the last response
comment|// for a request was received.  Otherwise we might return the same
comment|// request more than once.
name|rsp
operator|.
name|getShardRequest
argument_list|()
operator|.
name|responses
operator|.
name|add
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rsp
operator|.
name|getShardRequest
argument_list|()
operator|.
name|responses
operator|.
name|size
argument_list|()
operator|==
name|rsp
operator|.
name|getShardRequest
argument_list|()
operator|.
name|actualShards
operator|.
name|length
condition|)
block|{
return|return
name|rsp
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// should be impossible... the problem with catching the exception
comment|// at this level is we don't know what ShardRequest it applied to
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
literal|"Impossible Exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|cancelAll
specifier|public
name|void
name|cancelAll
parameter_list|()
block|{
for|for
control|(
name|Future
argument_list|<
name|ShardResponse
argument_list|>
name|future
range|:
name|pending
control|)
block|{
comment|// TODO: any issues with interrupting?  shouldn't be if
comment|// there are finally blocks to release connections.
name|future
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkDistributed
specifier|public
name|void
name|checkDistributed
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
block|{
name|SolrQueryRequest
name|req
init|=
name|rb
operator|.
name|req
decl_stmt|;
name|SolrParams
name|params
init|=
name|req
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|rb
operator|.
name|isDistrib
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"distrib"
argument_list|,
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|isZooKeeperAware
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|shards
init|=
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|)
decl_stmt|;
comment|// for back compat, a shards param with URLs like localhost:8983/solr will mean that this
comment|// search is distributed.
name|boolean
name|hasShardURL
init|=
name|shards
operator|!=
literal|null
operator|&&
name|shards
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>
literal|0
decl_stmt|;
name|rb
operator|.
name|isDistrib
operator|=
name|hasShardURL
operator||
name|rb
operator|.
name|isDistrib
expr_stmt|;
if|if
condition|(
name|rb
operator|.
name|isDistrib
condition|)
block|{
comment|// since the cost of grabbing cloud state is still up in the air, we grab it only
comment|// if we need it.
name|ClusterState
name|clusterState
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
name|slices
init|=
literal|null
decl_stmt|;
name|CoreDescriptor
name|coreDescriptor
init|=
name|req
operator|.
name|getCore
argument_list|()
operator|.
name|getCoreDescriptor
argument_list|()
decl_stmt|;
name|CloudDescriptor
name|cloudDescriptor
init|=
name|coreDescriptor
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
name|ZkController
name|zkController
init|=
name|coreDescriptor
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
decl_stmt|;
if|if
condition|(
name|shards
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lst
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|shards
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|rb
operator|.
name|shards
operator|=
name|lst
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|lst
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|rb
operator|.
name|slices
operator|=
operator|new
name|String
index|[
name|rb
operator|.
name|shards
operator|.
name|length
index|]
expr_stmt|;
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
comment|// figure out which shards are slices
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rb
operator|.
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|rb
operator|.
name|shards
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// this is a logical shard
name|rb
operator|.
name|slices
index|[
name|i
index|]
operator|=
name|rb
operator|.
name|shards
index|[
name|i
index|]
expr_stmt|;
name|rb
operator|.
name|shards
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
comment|// we weren't provided with a list of slices to query, so find the list that will cover the complete index
name|clusterState
operator|=
name|zkController
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
comment|// This can be more efficient... we only record the name, even though we
comment|// have the shard info we need in the next step of mapping slice->shards
comment|// Stores the comma-separated list of specified collections.
comment|// Eg: "collection1,collection2,collection3"
name|String
name|collections
init|=
name|params
operator|.
name|get
argument_list|(
literal|"collection"
argument_list|)
decl_stmt|;
if|if
condition|(
name|collections
operator|!=
literal|null
condition|)
block|{
comment|// If there were one or more collections specified in the query, split
comment|// each parameter and store as a seperate member of a List.
name|List
argument_list|<
name|String
argument_list|>
name|collectionList
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|collections
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// First create an empty HashMap to add the slice info to.
name|slices
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Slice
argument_list|>
argument_list|()
expr_stmt|;
comment|// In turn, retrieve the slices that cover each collection from the
comment|// cloud state and add them to the Map 'slices'.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|collectionList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|collection
init|=
name|collectionList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ClientUtils
operator|.
name|appendMap
argument_list|(
name|collection
argument_list|,
name|slices
argument_list|,
name|clusterState
operator|.
name|getSlicesMap
argument_list|(
name|collection
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// If no collections were specified, default to the collection for
comment|// this core.
name|slices
operator|=
name|clusterState
operator|.
name|getSlicesMap
argument_list|(
name|cloudDescriptor
operator|.
name|getCollectionName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|slices
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
literal|"Could not find collection:"
operator|+
name|cloudDescriptor
operator|.
name|getCollectionName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// Store the logical slices in the ResponseBuilder and create a new
comment|// String array to hold the physical shards (which will be mapped
comment|// later).
name|rb
operator|.
name|slices
operator|=
name|slices
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|slices
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|rb
operator|.
name|shards
operator|=
operator|new
name|String
index|[
name|rb
operator|.
name|slices
operator|.
name|length
index|]
expr_stmt|;
comment|/***          rb.slices = new String[slices.size()];          for (int i=0; i<rb.slices.length; i++) {          rb.slices[i] = slices.get(i).getName();          }          ***/
block|}
comment|//
comment|// Map slices to shards
comment|//
if|if
condition|(
name|zkController
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
name|rb
operator|.
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|rb
operator|.
name|shards
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|clusterState
operator|==
literal|null
condition|)
block|{
name|clusterState
operator|=
name|zkController
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|slices
operator|=
name|clusterState
operator|.
name|getSlicesMap
argument_list|(
name|cloudDescriptor
operator|.
name|getCollectionName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|sliceName
init|=
name|rb
operator|.
name|slices
index|[
name|i
index|]
decl_stmt|;
name|Slice
name|slice
init|=
name|slices
operator|.
name|get
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
block|{
comment|// Treat this the same as "all servers down" for a slice, and let things continue
comment|// if partial results are acceptable
name|rb
operator|.
name|shards
index|[
name|i
index|]
operator|=
literal|""
expr_stmt|;
continue|continue;
comment|// throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "no such shard: " + sliceName);
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Replica
argument_list|>
name|sliceShards
init|=
name|slice
operator|.
name|getReplicasMap
argument_list|()
decl_stmt|;
comment|// For now, recreate the | delimited list of equivalent servers
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
name|StringBuilder
name|sliceShardsStr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|ZkNodeProps
name|nodeProps
range|:
name|sliceShards
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
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|sliceShardsStr
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
block|}
name|String
name|url
init|=
name|coreNodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
condition|)
name|url
operator|=
name|url
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|sliceShardsStr
operator|.
name|append
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|shards
index|[
name|i
index|]
operator|=
name|sliceShardsStr
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
name|String
name|shards_rows
init|=
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_ROWS
argument_list|)
decl_stmt|;
if|if
condition|(
name|shards_rows
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|shards_rows
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|shards_rows
argument_list|)
expr_stmt|;
block|}
name|String
name|shards_start
init|=
name|params
operator|.
name|get
argument_list|(
name|ShardParams
operator|.
name|SHARDS_START
argument_list|)
decl_stmt|;
if|if
condition|(
name|shards_start
operator|!=
literal|null
condition|)
block|{
name|rb
operator|.
name|shards_start
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|shards_start
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

