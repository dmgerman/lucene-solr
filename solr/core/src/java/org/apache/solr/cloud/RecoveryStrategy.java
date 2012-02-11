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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Future
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
name|impl
operator|.
name|CommonsHttpSolrServer
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
name|CoreAdminRequest
operator|.
name|PrepRecovery
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
name|core
operator|.
name|RequestHandlers
operator|.
name|LazyRequestHandlerWrapper
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
name|ReplicationHandler
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
name|SolrRequestHandler
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
name|UpdateLog
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
name|UpdateLog
operator|.
name|RecoveryInfo
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

begin_class
DECL|class|RecoveryStrategy
specifier|public
class|class
name|RecoveryStrategy
extends|extends
name|Thread
block|{
DECL|field|MAX_RETRIES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_RETRIES
init|=
literal|100
decl_stmt|;
DECL|field|INTERRUPTED
specifier|private
specifier|static
specifier|final
name|int
name|INTERRUPTED
init|=
literal|101
decl_stmt|;
DECL|field|START_TIMEOUT
specifier|private
specifier|static
specifier|final
name|int
name|START_TIMEOUT
init|=
literal|100
decl_stmt|;
DECL|field|REPLICATION_HANDLER
specifier|private
specifier|static
specifier|final
name|String
name|REPLICATION_HANDLER
init|=
literal|"/replication"
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
name|RecoveryStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|close
specifier|private
specifier|volatile
name|boolean
name|close
init|=
literal|false
decl_stmt|;
DECL|field|zkController
specifier|private
name|ZkController
name|zkController
decl_stmt|;
DECL|field|baseUrl
specifier|private
name|String
name|baseUrl
decl_stmt|;
DECL|field|coreZkNodeName
specifier|private
name|String
name|coreZkNodeName
decl_stmt|;
DECL|field|zkStateReader
specifier|private
name|ZkStateReader
name|zkStateReader
decl_stmt|;
DECL|field|coreName
specifier|private
specifier|volatile
name|String
name|coreName
decl_stmt|;
DECL|field|retries
specifier|private
name|int
name|retries
decl_stmt|;
DECL|field|core
specifier|private
name|SolrCore
name|core
decl_stmt|;
DECL|method|RecoveryStrategy
specifier|public
name|RecoveryStrategy
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|core
operator|.
name|getName
argument_list|()
expr_stmt|;
name|setName
argument_list|(
literal|"RecoveryThread"
argument_list|)
expr_stmt|;
name|zkController
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
expr_stmt|;
name|zkStateReader
operator|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
expr_stmt|;
name|baseUrl
operator|=
name|zkController
operator|.
name|getBaseUrl
argument_list|()
expr_stmt|;
name|coreZkNodeName
operator|=
name|zkController
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"_"
operator|+
name|coreName
expr_stmt|;
block|}
comment|// make sure any threads stop retrying
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|close
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|recoveryFailed
specifier|private
name|void
name|recoveryFailed
parameter_list|(
specifier|final
name|SolrCore
name|core
parameter_list|,
specifier|final
name|ZkController
name|zkController
parameter_list|,
specifier|final
name|String
name|baseUrl
parameter_list|,
specifier|final
name|String
name|shardZkNodeName
parameter_list|,
specifier|final
name|CoreDescriptor
name|cd
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Recovery failed - I give up."
argument_list|)
expr_stmt|;
name|zkController
operator|.
name|publishAsRecoveryFailed
argument_list|(
name|baseUrl
argument_list|,
name|cd
argument_list|,
name|shardZkNodeName
argument_list|,
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|close
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|replicate
specifier|private
name|void
name|replicate
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|String
name|shardZkNodeName
parameter_list|,
name|ZkNodeProps
name|leaderprops
parameter_list|,
name|String
name|baseUrl
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
comment|// start buffer updates to tran log
comment|// and do recovery - either replay via realtime get (eventually)
comment|// or full index replication
name|String
name|leaderBaseUrl
init|=
name|leaderprops
operator|.
name|get
argument_list|(
name|ZkStateReader
operator|.
name|BASE_URL_PROP
argument_list|)
decl_stmt|;
name|ZkCoreNodeProps
name|leaderCNodeProps
init|=
operator|new
name|ZkCoreNodeProps
argument_list|(
name|leaderprops
argument_list|)
decl_stmt|;
name|String
name|leaderUrl
init|=
name|leaderCNodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|String
name|leaderCoreName
init|=
name|leaderCNodeProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Attempt to replicate from "
operator|+
name|leaderUrl
argument_list|)
expr_stmt|;
comment|// if we are the leader, either we are trying to recover faster
comment|// then our ephemeral timed out or we are the only node
if|if
condition|(
operator|!
name|leaderBaseUrl
operator|.
name|equals
argument_list|(
name|baseUrl
argument_list|)
condition|)
block|{
name|CommonsHttpSolrServer
name|server
init|=
operator|new
name|CommonsHttpSolrServer
argument_list|(
name|leaderBaseUrl
argument_list|)
decl_stmt|;
name|server
operator|.
name|setSoTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
name|PrepRecovery
name|prepCmd
init|=
operator|new
name|PrepRecovery
argument_list|()
decl_stmt|;
name|prepCmd
operator|.
name|setCoreName
argument_list|(
name|leaderCoreName
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setNodeName
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|prepCmd
operator|.
name|setCoreNodeName
argument_list|(
name|shardZkNodeName
argument_list|)
expr_stmt|;
name|server
operator|.
name|request
argument_list|(
name|prepCmd
argument_list|)
expr_stmt|;
name|server
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// use rep handler directly, so we can do this sync rather than async
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
name|REPLICATION_HANDLER
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|LazyRequestHandlerWrapper
condition|)
block|{
name|handler
operator|=
operator|(
operator|(
name|LazyRequestHandlerWrapper
operator|)
name|handler
operator|)
operator|.
name|getWrappedHandler
argument_list|()
expr_stmt|;
block|}
name|ReplicationHandler
name|replicationHandler
init|=
operator|(
name|ReplicationHandler
operator|)
name|handler
decl_stmt|;
if|if
condition|(
name|replicationHandler
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
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"Skipping recovery, no "
operator|+
name|REPLICATION_HANDLER
operator|+
literal|" handler found"
argument_list|)
throw|;
block|}
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|set
argument_list|(
name|ReplicationHandler
operator|.
name|MASTER_URL
argument_list|,
name|leaderUrl
operator|+
literal|"replication"
argument_list|)
expr_stmt|;
if|if
condition|(
name|close
condition|)
name|retries
operator|=
name|INTERRUPTED
expr_stmt|;
name|boolean
name|success
init|=
name|replicationHandler
operator|.
name|doFetch
argument_list|(
name|solrParams
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// TODO: look into making sure fore=true does not download files we already have
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Replication for recovery failed."
argument_list|)
throw|;
block|}
comment|// solrcloud_debug
comment|//      try {
comment|//        RefCounted<SolrIndexSearcher> searchHolder = core.getNewestSearcher(false);
comment|//        SolrIndexSearcher searcher = searchHolder.get();
comment|//        try {
comment|//          System.out.println(core.getCoreDescriptor().getCoreContainer().getZkController().getNodeName() + " replicated "
comment|//              + searcher.search(new MatchAllDocsQuery(), 1).totalHits + " from " + leaderUrl + " gen:" + core.getDeletionPolicy().getLatestCommit().getGeneration() + " data:" + core.getDataDir());
comment|//        } finally {
comment|//          searchHolder.decref();
comment|//        }
comment|//      } catch (Exception e) {
comment|//
comment|//      }
block|}
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|replayed
init|=
literal|false
decl_stmt|;
name|boolean
name|succesfulRecovery
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|succesfulRecovery
operator|&&
operator|!
name|close
operator|&&
operator|!
name|isInterrupted
argument_list|()
condition|)
block|{
name|UpdateLog
name|ulog
init|=
name|core
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
decl_stmt|;
if|if
condition|(
name|ulog
operator|==
literal|null
condition|)
return|return;
name|ulog
operator|.
name|bufferUpdates
argument_list|()
expr_stmt|;
name|replayed
operator|=
literal|false
expr_stmt|;
name|CloudDescriptor
name|cloudDesc
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
decl_stmt|;
try|try
block|{
name|zkController
operator|.
name|publish
argument_list|(
name|core
argument_list|,
name|ZkStateReader
operator|.
name|RECOVERING
argument_list|)
expr_stmt|;
name|ZkNodeProps
name|leaderprops
init|=
name|zkStateReader
operator|.
name|getLeaderProps
argument_list|(
name|cloudDesc
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|cloudDesc
operator|.
name|getShardId
argument_list|()
argument_list|)
decl_stmt|;
comment|// System.out.println("recover " + shardZkNodeName + " against " +
comment|// leaderprops);
name|replicate
argument_list|(
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|core
argument_list|,
name|coreZkNodeName
argument_list|,
name|leaderprops
argument_list|,
name|ZkCoreNodeProps
operator|.
name|getCoreUrl
argument_list|(
name|baseUrl
argument_list|,
name|coreName
argument_list|)
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|ulog
argument_list|)
expr_stmt|;
name|replayed
operator|=
literal|true
expr_stmt|;
comment|// if there are pending recovery requests, don't advert as active
name|zkController
operator|.
name|publishAsActive
argument_list|(
name|baseUrl
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|,
name|coreZkNodeName
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
name|succesfulRecovery
operator|=
literal|true
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
name|log
operator|.
name|warn
argument_list|(
literal|"Recovery was interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|retries
operator|=
name|INTERRUPTED
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Error while trying to recover"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|replayed
condition|)
block|{
try|try
block|{
name|ulog
operator|.
name|dropBufferedUpdates
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|""
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|succesfulRecovery
condition|)
block|{
comment|// lets pause for a moment and we need to try again...
comment|// TODO: we don't want to retry for some problems?
comment|// Or do a fall off retry...
try|try
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|"Recovery failed - trying again..."
argument_list|)
expr_stmt|;
name|retries
operator|++
expr_stmt|;
if|if
condition|(
name|retries
operator|>=
name|MAX_RETRIES
condition|)
block|{
if|if
condition|(
name|retries
operator|==
name|INTERRUPTED
condition|)
block|{              }
else|else
block|{
comment|// TODO: for now, give up after 10 tries - should we do more?
name|recoveryFailed
argument_list|(
name|core
argument_list|,
name|zkController
argument_list|,
name|baseUrl
argument_list|,
name|coreZkNodeName
argument_list|,
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
literal|""
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|START_TIMEOUT
operator|*
name|retries
argument_list|,
literal|60000
argument_list|)
argument_list|)
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
name|log
operator|.
name|warn
argument_list|(
literal|"Recovery was interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|retries
operator|=
name|INTERRUPTED
expr_stmt|;
block|}
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Finished recovery process"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|replay
specifier|private
name|Future
argument_list|<
name|RecoveryInfo
argument_list|>
name|replay
parameter_list|(
name|UpdateLog
name|ulog
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
name|Future
argument_list|<
name|RecoveryInfo
argument_list|>
name|future
init|=
name|ulog
operator|.
name|applyBufferedUpdates
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|==
literal|null
condition|)
block|{
comment|// no replay needed\
name|log
operator|.
name|info
argument_list|(
literal|"No replay needed"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// wait for replay
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// solrcloud_debug
comment|//    try {
comment|//      RefCounted<SolrIndexSearcher> searchHolder = core.getNewestSearcher(false);
comment|//      SolrIndexSearcher searcher = searchHolder.get();
comment|//      try {
comment|//        System.out.println(core.getCoreDescriptor().getCoreContainer().getZkController().getNodeName() + " replayed "
comment|//            + searcher.search(new MatchAllDocsQuery(), 1).totalHits);
comment|//      } finally {
comment|//        searchHolder.decref();
comment|//      }
comment|//    } catch (Exception e) {
comment|//
comment|//    }
return|return
name|future
return|;
block|}
block|}
end_class

end_unit

