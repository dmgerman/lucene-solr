begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|ArrayList
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
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|NoHttpResponseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|SolrInputDocument
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
name|core
operator|.
name|SolrCore
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
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|ShardHandlerFactory
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
name|ShardRequest
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
name|ShardResponse
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
name|LocalSolrQueryRequest
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|SolrQueryResponse
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
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
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
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessorFactory
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
name|update
operator|.
name|processor
operator|.
name|RunUpdateProcessorFactory
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
name|update
operator|.
name|processor
operator|.
name|UpdateRequestProcessor
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
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|PeerSync
specifier|public
class|class
name|PeerSync
block|{
DECL|field|log
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PeerSync
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|debug
specifier|public
name|boolean
name|debug
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
DECL|field|replicas
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|replicas
decl_stmt|;
DECL|field|nUpdates
specifier|private
name|int
name|nUpdates
decl_stmt|;
DECL|field|maxUpdates
specifier|private
name|int
name|maxUpdates
decl_stmt|;
comment|// maximum number of updates to request before failing
DECL|field|uhandler
specifier|private
name|UpdateHandler
name|uhandler
decl_stmt|;
DECL|field|ulog
specifier|private
name|UpdateLog
name|ulog
decl_stmt|;
DECL|field|shardHandlerFactory
specifier|private
name|ShardHandlerFactory
name|shardHandlerFactory
decl_stmt|;
DECL|field|shardHandler
specifier|private
name|ShardHandler
name|shardHandler
decl_stmt|;
DECL|field|recentUpdates
specifier|private
name|UpdateLog
operator|.
name|RecentUpdates
name|recentUpdates
decl_stmt|;
DECL|field|startingVersions
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|startingVersions
decl_stmt|;
DECL|field|ourUpdates
specifier|private
name|List
argument_list|<
name|Long
argument_list|>
name|ourUpdates
decl_stmt|;
DECL|field|ourUpdateSet
specifier|private
name|Set
argument_list|<
name|Long
argument_list|>
name|ourUpdateSet
decl_stmt|;
DECL|field|requestedUpdateSet
specifier|private
name|Set
argument_list|<
name|Long
argument_list|>
name|requestedUpdateSet
decl_stmt|;
DECL|field|ourLowThreshold
specifier|private
name|long
name|ourLowThreshold
decl_stmt|;
comment|// 20th percentile
DECL|field|ourHighThreshold
specifier|private
name|long
name|ourHighThreshold
decl_stmt|;
comment|// 80th percentile
comment|// comparator that sorts by absolute value, putting highest first
DECL|field|absComparator
specifier|private
specifier|static
name|Comparator
argument_list|<
name|Long
argument_list|>
name|absComparator
init|=
operator|new
name|Comparator
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Long
name|o1
parameter_list|,
name|Long
name|o2
parameter_list|)
block|{
name|long
name|l1
init|=
name|Math
operator|.
name|abs
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|long
name|l2
init|=
name|Math
operator|.
name|abs
argument_list|(
name|o2
argument_list|)
decl_stmt|;
if|if
condition|(
name|l1
operator|>
name|l2
condition|)
return|return
operator|-
literal|1
return|;
if|if
condition|(
name|l1
operator|<
name|l2
condition|)
return|return
literal|1
return|;
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|// comparator that sorts update records by absolute value of version, putting lowest first
DECL|field|updateRecordComparator
specifier|private
specifier|static
name|Comparator
argument_list|<
name|Object
argument_list|>
name|updateRecordComparator
init|=
operator|new
name|Comparator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o1
operator|instanceof
name|List
operator|)
condition|)
return|return
literal|1
return|;
if|if
condition|(
operator|!
operator|(
name|o2
operator|instanceof
name|List
operator|)
condition|)
return|return
operator|-
literal|1
return|;
name|List
name|lst1
init|=
operator|(
name|List
operator|)
name|o1
decl_stmt|;
name|List
name|lst2
init|=
operator|(
name|List
operator|)
name|o2
decl_stmt|;
name|long
name|l1
init|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|Long
operator|)
name|lst1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|l2
init|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|Long
operator|)
name|lst2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|l1
operator|>
name|l2
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|l1
operator|<
name|l2
condition|)
return|return
operator|-
literal|1
return|;
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
DECL|class|SyncShardRequest
specifier|private
specifier|static
class|class
name|SyncShardRequest
extends|extends
name|ShardRequest
block|{
DECL|field|reportedVersions
name|List
argument_list|<
name|Long
argument_list|>
name|reportedVersions
decl_stmt|;
DECL|field|requestedUpdates
name|List
argument_list|<
name|Long
argument_list|>
name|requestedUpdates
decl_stmt|;
DECL|field|updateException
name|Exception
name|updateException
decl_stmt|;
block|}
comment|/**    *    * @param core    * @param replicas    * @param nUpdates    */
DECL|method|PeerSync
specifier|public
name|PeerSync
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|replicas
parameter_list|,
name|int
name|nUpdates
parameter_list|)
block|{
name|this
operator|.
name|replicas
operator|=
name|replicas
expr_stmt|;
name|this
operator|.
name|nUpdates
operator|=
name|nUpdates
expr_stmt|;
name|this
operator|.
name|maxUpdates
operator|=
name|nUpdates
expr_stmt|;
name|uhandler
operator|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
expr_stmt|;
name|ulog
operator|=
name|uhandler
operator|.
name|getUpdateLog
argument_list|()
expr_stmt|;
name|shardHandlerFactory
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getShardHandlerFactory
argument_list|()
expr_stmt|;
name|shardHandler
operator|=
name|shardHandlerFactory
operator|.
name|getShardHandler
argument_list|()
expr_stmt|;
block|}
comment|/** optional list of updates we had before possibly receiving new updates */
DECL|method|setStartingVersions
specifier|public
name|void
name|setStartingVersions
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|startingVersions
parameter_list|)
block|{
name|this
operator|.
name|startingVersions
operator|=
name|startingVersions
expr_stmt|;
block|}
DECL|method|percentile
specifier|public
name|long
name|percentile
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|arr
parameter_list|,
name|float
name|frac
parameter_list|)
block|{
name|int
name|elem
init|=
call|(
name|int
call|)
argument_list|(
name|arr
operator|.
name|size
argument_list|()
operator|*
name|frac
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|abs
argument_list|(
name|arr
operator|.
name|get
argument_list|(
name|elem
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns true if peer sync was successful, meaning that this core may not be considered to have the latest updates.    *  A commit is not performed.    */
DECL|method|sync
specifier|public
name|boolean
name|sync
parameter_list|()
block|{
if|if
condition|(
name|ulog
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Fire off the requests before getting our own recent updates (for better concurrency)
comment|// This also allows us to avoid getting updates we don't need... if we got our updates and then got their updates, they would
comment|// have newer stuff that we also had (assuming updates are going on and are being forwarded).
for|for
control|(
name|String
name|replica
range|:
name|replicas
control|)
block|{
name|requestVersions
argument_list|(
name|replica
argument_list|)
expr_stmt|;
block|}
name|recentUpdates
operator|=
name|ulog
operator|.
name|getRecentUpdates
argument_list|()
expr_stmt|;
try|try
block|{
name|ourUpdates
operator|=
name|recentUpdates
operator|.
name|getVersions
argument_list|(
name|nUpdates
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|recentUpdates
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|ourUpdates
argument_list|,
name|absComparator
argument_list|)
expr_stmt|;
if|if
condition|(
name|startingVersions
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|startingVersions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// no frame of reference to tell of we've missed updates
return|return
literal|false
return|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|startingVersions
argument_list|,
name|absComparator
argument_list|)
expr_stmt|;
name|ourLowThreshold
operator|=
name|percentile
argument_list|(
name|startingVersions
argument_list|,
literal|0.8f
argument_list|)
expr_stmt|;
name|ourHighThreshold
operator|=
name|percentile
argument_list|(
name|startingVersions
argument_list|,
literal|0.2f
argument_list|)
expr_stmt|;
comment|// now make sure that the starting updates overlap our updates
comment|// there shouldn't be reorders, so any overlap will do.
name|long
name|smallestNewUpdate
init|=
name|Math
operator|.
name|abs
argument_list|(
name|ourUpdates
operator|.
name|get
argument_list|(
name|ourUpdates
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|startingVersions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|<
name|smallestNewUpdate
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"PeerSync: too many updates received since start - startingUpdates no longer overlaps with cour urrentUpdates"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// let's merge the lists
name|List
argument_list|<
name|Long
argument_list|>
name|newList
init|=
operator|new
name|ArrayList
argument_list|(
name|ourUpdates
argument_list|)
decl_stmt|;
for|for
control|(
name|Long
name|ver
range|:
name|startingVersions
control|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|ver
argument_list|)
operator|<
name|smallestNewUpdate
condition|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|ver
argument_list|)
expr_stmt|;
block|}
block|}
name|ourUpdates
operator|=
name|newList
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|ourUpdates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ourLowThreshold
operator|=
name|percentile
argument_list|(
name|ourUpdates
argument_list|,
literal|0.8f
argument_list|)
expr_stmt|;
name|ourHighThreshold
operator|=
name|percentile
argument_list|(
name|ourUpdates
argument_list|,
literal|0.2f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we have no versions and hence no frame of reference to tell if we can use a peers
comment|// updates to bring us into sync
return|return
literal|false
return|;
block|}
block|}
name|ourUpdateSet
operator|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|(
name|ourUpdates
argument_list|)
expr_stmt|;
name|requestedUpdateSet
operator|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|(
name|ourUpdates
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|ShardResponse
name|srsp
init|=
name|shardHandler
operator|.
name|takeCompletedOrError
argument_list|()
decl_stmt|;
if|if
condition|(
name|srsp
operator|==
literal|null
condition|)
break|break;
name|boolean
name|success
init|=
name|handleResponse
argument_list|(
name|srsp
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|shardHandler
operator|.
name|cancelAll
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|requestVersions
specifier|private
name|void
name|requestVersions
parameter_list|(
name|String
name|replica
parameter_list|)
block|{
name|SyncShardRequest
name|sreq
init|=
operator|new
name|SyncShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
literal|1
expr_stmt|;
comment|// TODO: this sucks
if|if
condition|(
name|replica
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
condition|)
name|replica
operator|=
name|replica
operator|.
name|substring
argument_list|(
literal|7
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|replica
block|}
expr_stmt|;
name|sreq
operator|.
name|actualShards
operator|=
name|sreq
operator|.
name|shards
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"getVersions"
argument_list|,
name|nUpdates
argument_list|)
expr_stmt|;
name|shardHandler
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|replica
argument_list|,
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|handleResponse
specifier|private
name|boolean
name|handleResponse
parameter_list|(
name|ShardResponse
name|srsp
parameter_list|)
block|{
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// TODO: look at this more thoroughly - we don't want
comment|// to fail on connection exceptions, but it may make sense
comment|// to determine this based on the number of fails
if|if
condition|(
name|srsp
operator|.
name|getException
argument_list|()
operator|instanceof
name|SolrServerException
condition|)
block|{
name|Throwable
name|solrException
init|=
operator|(
operator|(
name|SolrServerException
operator|)
name|srsp
operator|.
name|getException
argument_list|()
operator|)
operator|.
name|getRootCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|solrException
operator|instanceof
name|ConnectException
operator|||
name|solrException
operator|instanceof
name|NoHttpResponseException
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
comment|// TODO: at least log???
comment|// srsp.getException().printStackTrace(System.out);
return|return
literal|false
return|;
block|}
name|ShardRequest
name|sreq
init|=
name|srsp
operator|.
name|getShardRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|sreq
operator|.
name|purpose
operator|==
literal|1
condition|)
block|{
return|return
name|handleVersions
argument_list|(
name|srsp
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|handleUpdates
argument_list|(
name|srsp
argument_list|)
return|;
block|}
block|}
DECL|method|handleVersions
specifier|private
name|boolean
name|handleVersions
parameter_list|(
name|ShardResponse
name|srsp
parameter_list|)
block|{
comment|// we retrieved the last N updates from the replica
name|List
argument_list|<
name|Long
argument_list|>
name|otherVersions
init|=
operator|(
name|List
argument_list|<
name|Long
argument_list|>
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"versions"
argument_list|)
decl_stmt|;
comment|// TODO: how to handle short lists?
name|SyncShardRequest
name|sreq
init|=
operator|(
name|SyncShardRequest
operator|)
name|srsp
operator|.
name|getShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|reportedVersions
operator|=
name|otherVersions
expr_stmt|;
if|if
condition|(
name|otherVersions
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|boolean
name|completeList
init|=
name|otherVersions
operator|.
name|size
argument_list|()
operator|<
name|nUpdates
decl_stmt|;
comment|// do we have their complete list of updates?
name|Collections
operator|.
name|sort
argument_list|(
name|otherVersions
argument_list|,
name|absComparator
argument_list|)
expr_stmt|;
name|long
name|otherHigh
init|=
name|percentile
argument_list|(
name|otherVersions
argument_list|,
literal|.2f
argument_list|)
decl_stmt|;
name|long
name|otherLow
init|=
name|percentile
argument_list|(
name|otherVersions
argument_list|,
literal|.8f
argument_list|)
decl_stmt|;
if|if
condition|(
name|ourHighThreshold
operator|<
name|otherLow
condition|)
block|{
comment|// Small overlap between version windows and ours is older
comment|// This means that we might miss updates if we attempted to use this method.
comment|// Since there exists just one replica that is so much newer, we must
comment|// fail the sync.
return|return
literal|false
return|;
block|}
if|if
condition|(
name|ourLowThreshold
operator|>
name|otherHigh
condition|)
block|{
comment|// Small overlap between windows and ours is newer.
comment|// Using this list to sync would result in requesting/replaying results we don't need
comment|// and possibly bringing deleted docs back to life.
return|return
literal|true
return|;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|toRequest
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Long
name|otherVersion
range|:
name|otherVersions
control|)
block|{
comment|// stop when the entries get old enough that reorders may lead us to see updates we don't need
if|if
condition|(
operator|!
name|completeList
operator|&&
name|Math
operator|.
name|abs
argument_list|(
name|otherVersion
argument_list|)
operator|<
name|ourLowThreshold
condition|)
break|break;
if|if
condition|(
name|ourUpdateSet
operator|.
name|contains
argument_list|(
name|otherVersion
argument_list|)
operator|||
name|requestedUpdateSet
operator|.
name|contains
argument_list|(
name|otherVersion
argument_list|)
condition|)
block|{
comment|// we either have this update, or already requested it
continue|continue;
block|}
name|toRequest
operator|.
name|add
argument_list|(
name|otherVersion
argument_list|)
expr_stmt|;
name|requestedUpdateSet
operator|.
name|add
argument_list|(
name|otherVersion
argument_list|)
expr_stmt|;
block|}
name|sreq
operator|.
name|requestedUpdates
operator|=
name|toRequest
expr_stmt|;
if|if
condition|(
name|toRequest
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we had (or already requested) all the updates referenced by the replica
return|return
literal|true
return|;
block|}
if|if
condition|(
name|toRequest
operator|.
name|size
argument_list|()
operator|>
name|maxRequests
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|requestUpdates
argument_list|(
name|srsp
argument_list|,
name|toRequest
argument_list|)
return|;
block|}
DECL|method|requestUpdates
specifier|private
name|boolean
name|requestUpdates
parameter_list|(
name|ShardResponse
name|srsp
parameter_list|,
name|List
argument_list|<
name|Long
argument_list|>
name|toRequest
parameter_list|)
block|{
name|String
name|replica
init|=
name|srsp
operator|.
name|getShardRequest
argument_list|()
operator|.
name|shards
index|[
literal|0
index|]
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Requesting updates from "
operator|+
name|replica
operator|+
literal|" versions="
operator|+
name|toRequest
argument_list|)
expr_stmt|;
comment|// reuse our original request object
name|ShardRequest
name|sreq
init|=
name|srsp
operator|.
name|getShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|purpose
operator|=
literal|0
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"getUpdates"
argument_list|,
name|StrUtils
operator|.
name|join
argument_list|(
name|toRequest
argument_list|,
literal|','
argument_list|)
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|responses
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// needs to be zeroed for correct correlation to occur
name|shardHandler
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|sreq
operator|.
name|shards
index|[
literal|0
index|]
argument_list|,
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|handleUpdates
specifier|private
name|boolean
name|handleUpdates
parameter_list|(
name|ShardResponse
name|srsp
parameter_list|)
block|{
comment|// we retrieved the last N updates from the replica
name|List
argument_list|<
name|Object
argument_list|>
name|updates
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|srsp
operator|.
name|getSolrResponse
argument_list|()
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"updates"
argument_list|)
decl_stmt|;
name|SyncShardRequest
name|sreq
init|=
operator|(
name|SyncShardRequest
operator|)
name|srsp
operator|.
name|getShardRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|updates
operator|.
name|size
argument_list|()
operator|<
name|sreq
operator|.
name|requestedUpdates
operator|.
name|size
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"PeerSync: Requested "
operator|+
name|sreq
operator|.
name|requestedUpdates
operator|.
name|size
argument_list|()
operator|+
literal|" updates from "
operator|+
name|sreq
operator|.
name|shards
index|[
literal|0
index|]
operator|+
literal|" but retrieved "
operator|+
name|updates
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
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
name|set
argument_list|(
name|DistributedUpdateProcessor
operator|.
name|SEEN_LEADER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|uhandler
operator|.
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|RunUpdateProcessorFactory
name|runFac
init|=
operator|new
name|RunUpdateProcessorFactory
argument_list|()
decl_stmt|;
name|DistributedUpdateProcessorFactory
name|magicFac
init|=
operator|new
name|DistributedUpdateProcessorFactory
argument_list|()
decl_stmt|;
name|runFac
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|magicFac
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|UpdateRequestProcessor
name|proc
init|=
name|magicFac
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|runFac
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|updates
argument_list|,
name|updateRecordComparator
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
literal|null
decl_stmt|;
name|long
name|lastVersion
init|=
literal|0
decl_stmt|;
try|try
block|{
comment|// Apply oldest updates first
for|for
control|(
name|Object
name|obj
range|:
name|updates
control|)
block|{
comment|// should currently be a List<Oper,Ver,Doc/Id>
name|o
operator|=
name|obj
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|entry
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|o
decl_stmt|;
name|int
name|oper
init|=
operator|(
name|Integer
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|version
init|=
operator|(
name|Long
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
name|lastVersion
operator|&&
name|version
operator|!=
literal|0
condition|)
continue|continue;
name|lastVersion
operator|=
name|version
expr_stmt|;
switch|switch
condition|(
name|oper
condition|)
block|{
case|case
name|UpdateLog
operator|.
name|ADD
case|:
block|{
comment|// byte[] idBytes = (byte[]) entry.get(2);
name|SolrInputDocument
name|sdoc
init|=
operator|(
name|SolrInputDocument
operator|)
name|entry
operator|.
name|get
argument_list|(
name|entry
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|AddUpdateCommand
name|cmd
init|=
operator|new
name|AddUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
comment|// cmd.setIndexedId(new BytesRef(idBytes));
name|cmd
operator|.
name|solrDoc
operator|=
name|sdoc
expr_stmt|;
name|cmd
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setFlags
argument_list|(
name|UpdateCommand
operator|.
name|PEER_SYNC
operator||
name|UpdateCommand
operator|.
name|IGNORE_AUTOCOMMIT
argument_list|)
expr_stmt|;
name|proc
operator|.
name|processAdd
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|UpdateLog
operator|.
name|DELETE
case|:
block|{
name|byte
index|[]
name|idBytes
init|=
operator|(
name|byte
index|[]
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|setIndexedId
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|idBytes
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setFlags
argument_list|(
name|UpdateCommand
operator|.
name|PEER_SYNC
operator||
name|UpdateCommand
operator|.
name|IGNORE_AUTOCOMMIT
argument_list|)
expr_stmt|;
name|proc
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|UpdateLog
operator|.
name|DELETE_BY_QUERY
case|:
block|{
name|String
name|query
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|DeleteUpdateCommand
name|cmd
init|=
operator|new
name|DeleteUpdateCommand
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|cmd
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|cmd
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|setFlags
argument_list|(
name|UpdateCommand
operator|.
name|PEER_SYNC
operator||
name|UpdateCommand
operator|.
name|IGNORE_AUTOCOMMIT
argument_list|)
expr_stmt|;
name|proc
operator|.
name|processDelete
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
break|break;
block|}
default|default:
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
literal|"Unknown Operation! "
operator|+
name|oper
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO: should this be handled separately as a problem with us?
comment|// I guess it probably already will by causing replication to be kicked off.
name|sreq
operator|.
name|updateException
operator|=
name|e
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Error applying updates from "
operator|+
name|sreq
operator|.
name|shards
operator|+
literal|" ,update="
operator|+
name|o
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|sreq
operator|.
name|updateException
operator|=
name|e
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Error applying updates from "
operator|+
name|sreq
operator|.
name|shards
operator|+
literal|" ,update="
operator|+
name|o
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
finally|finally
block|{
try|try
block|{
name|proc
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|sreq
operator|.
name|updateException
operator|=
name|e
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Error applying updates from "
operator|+
name|sreq
operator|.
name|shards
operator|+
literal|" ,finish()"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** Requests and applies recent updates from peers */
DECL|method|sync
specifier|public
specifier|static
name|void
name|sync
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|replicas
parameter_list|,
name|int
name|nUpdates
parameter_list|)
block|{
name|UpdateHandler
name|uhandler
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|ShardHandlerFactory
name|shardHandlerFactory
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getShardHandlerFactory
argument_list|()
decl_stmt|;
name|ShardHandler
name|shardHandler
init|=
name|shardHandlerFactory
operator|.
name|getShardHandler
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|replica
range|:
name|replicas
control|)
block|{
name|ShardRequest
name|sreq
init|=
operator|new
name|ShardRequest
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
name|replica
block|}
expr_stmt|;
name|sreq
operator|.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"qt"
argument_list|,
literal|"/get"
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"distrib"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sreq
operator|.
name|params
operator|.
name|set
argument_list|(
literal|"getVersions"
argument_list|,
name|nUpdates
argument_list|)
expr_stmt|;
name|shardHandler
operator|.
name|submit
argument_list|(
name|sreq
argument_list|,
name|replica
argument_list|,
name|sreq
operator|.
name|params
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|replica
range|:
name|replicas
control|)
block|{
name|ShardResponse
name|srsp
init|=
name|shardHandler
operator|.
name|takeCompletedOrError
argument_list|()
decl_stmt|;
block|}
block|}
block|}
end_class

end_unit

