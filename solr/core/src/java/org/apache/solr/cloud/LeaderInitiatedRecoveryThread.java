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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
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
name|http
operator|.
name|conn
operator|.
name|ConnectTimeoutException
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
name|HttpSolrClient
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
name|RequestRecovery
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
name|core
operator|.
name|CoreContainer
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
name|net
operator|.
name|ConnectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Background daemon thread that tries to send the REQUESTRECOVERY to a downed  * replica; used by a shard leader to nag a replica into recovering after the  * leader experiences an error trying to send an update request to the replica.  */
end_comment

begin_class
DECL|class|LeaderInitiatedRecoveryThread
specifier|public
class|class
name|LeaderInitiatedRecoveryThread
extends|extends
name|Thread
block|{
DECL|field|log
specifier|public
specifier|final
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LeaderInitiatedRecoveryThread
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|zkController
specifier|protected
name|ZkController
name|zkController
decl_stmt|;
DECL|field|coreContainer
specifier|protected
name|CoreContainer
name|coreContainer
decl_stmt|;
DECL|field|collection
specifier|protected
name|String
name|collection
decl_stmt|;
DECL|field|shardId
specifier|protected
name|String
name|shardId
decl_stmt|;
DECL|field|nodeProps
specifier|protected
name|ZkCoreNodeProps
name|nodeProps
decl_stmt|;
DECL|field|maxTries
specifier|protected
name|int
name|maxTries
decl_stmt|;
DECL|field|leaderCoreNodeName
specifier|protected
name|String
name|leaderCoreNodeName
decl_stmt|;
DECL|method|LeaderInitiatedRecoveryThread
specifier|public
name|LeaderInitiatedRecoveryThread
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|CoreContainer
name|cc
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|shardId
parameter_list|,
name|ZkCoreNodeProps
name|nodeProps
parameter_list|,
name|int
name|maxTries
parameter_list|,
name|String
name|leaderCoreNodeName
parameter_list|)
block|{
name|super
argument_list|(
literal|"LeaderInitiatedRecoveryThread-"
operator|+
name|nodeProps
operator|.
name|getCoreName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|zkController
operator|=
name|zkController
expr_stmt|;
name|this
operator|.
name|coreContainer
operator|=
name|cc
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|nodeProps
operator|=
name|nodeProps
expr_stmt|;
name|this
operator|.
name|maxTries
operator|=
name|maxTries
expr_stmt|;
name|this
operator|.
name|leaderCoreNodeName
operator|=
name|leaderCoreNodeName
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|startMs
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|sendRecoveryCommandWithRetry
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|getName
argument_list|()
operator|+
literal|" failed due to: "
operator|+
name|exc
argument_list|,
name|exc
argument_list|)
expr_stmt|;
if|if
condition|(
name|exc
operator|instanceof
name|SolrException
condition|)
block|{
throw|throw
operator|(
name|SolrException
operator|)
name|exc
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|exc
argument_list|)
throw|;
block|}
block|}
name|long
name|diffMs
init|=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startMs
operator|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" completed successfully after running for "
operator|+
name|Math
operator|.
name|round
argument_list|(
name|diffMs
operator|/
literal|1000L
argument_list|)
operator|+
literal|" secs"
argument_list|)
expr_stmt|;
block|}
DECL|method|sendRecoveryCommandWithRetry
specifier|protected
name|void
name|sendRecoveryCommandWithRetry
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|tries
init|=
literal|0
decl_stmt|;
name|long
name|waitBetweenTriesMs
init|=
literal|5000L
decl_stmt|;
name|boolean
name|continueTrying
init|=
literal|true
decl_stmt|;
name|String
name|recoveryUrl
init|=
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
decl_stmt|;
name|String
name|replicaNodeName
init|=
name|nodeProps
operator|.
name|getNodeName
argument_list|()
decl_stmt|;
name|String
name|coreNeedingRecovery
init|=
name|nodeProps
operator|.
name|getCoreName
argument_list|()
decl_stmt|;
name|String
name|replicaCoreNodeName
init|=
operator|(
operator|(
name|Replica
operator|)
name|nodeProps
operator|.
name|getNodeProps
argument_list|()
operator|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|replicaUrl
init|=
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|" started running to send REQUESTRECOVERY command to "
operator|+
name|replicaUrl
operator|+
literal|"; will try for a max of "
operator|+
operator|(
name|maxTries
operator|*
operator|(
name|waitBetweenTriesMs
operator|/
literal|1000
operator|)
operator|)
operator|+
literal|" secs"
argument_list|)
expr_stmt|;
name|RequestRecovery
name|recoverRequestCmd
init|=
operator|new
name|RequestRecovery
argument_list|()
decl_stmt|;
name|recoverRequestCmd
operator|.
name|setAction
argument_list|(
name|CoreAdminAction
operator|.
name|REQUESTRECOVERY
argument_list|)
expr_stmt|;
name|recoverRequestCmd
operator|.
name|setCoreName
argument_list|(
name|coreNeedingRecovery
argument_list|)
expr_stmt|;
while|while
condition|(
name|continueTrying
operator|&&
operator|++
name|tries
operator|<=
name|maxTries
condition|)
block|{
if|if
condition|(
name|tries
operator|>
literal|1
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Asking core={} coreNodeName={} on "
operator|+
name|recoveryUrl
operator|+
literal|" to recover; unsuccessful after "
operator|+
name|tries
operator|+
literal|" of "
operator|+
name|maxTries
operator|+
literal|" attempts so far ..."
argument_list|,
name|coreNeedingRecovery
argument_list|,
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Asking core={} coreNodeName={} on "
operator|+
name|recoveryUrl
operator|+
literal|" to recover"
argument_list|,
name|coreNeedingRecovery
argument_list|,
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|HttpSolrClient
name|client
init|=
operator|new
name|HttpSolrClient
argument_list|(
name|recoveryUrl
argument_list|)
init|)
block|{
name|client
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|client
operator|.
name|setConnectionTimeout
argument_list|(
literal|15000
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|request
argument_list|(
name|recoverRequestCmd
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Successfully sent "
operator|+
name|CoreAdminAction
operator|.
name|REQUESTRECOVERY
operator|+
literal|" command to core={} coreNodeName={} on "
operator|+
name|recoveryUrl
argument_list|,
name|coreNeedingRecovery
argument_list|,
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
name|continueTrying
operator|=
literal|false
expr_stmt|;
comment|// succeeded, so stop looping
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Throwable
name|rootCause
init|=
name|SolrException
operator|.
name|getRootCause
argument_list|(
name|t
argument_list|)
decl_stmt|;
name|boolean
name|wasCommError
init|=
operator|(
name|rootCause
operator|instanceof
name|ConnectException
operator|||
name|rootCause
operator|instanceof
name|ConnectTimeoutException
operator|||
name|rootCause
operator|instanceof
name|NoHttpResponseException
operator|||
name|rootCause
operator|instanceof
name|SocketException
operator|)
decl_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|recoveryUrl
operator|+
literal|": Could not tell a replica to recover"
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|wasCommError
condition|)
block|{
name|continueTrying
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
comment|// wait a few seconds
if|if
condition|(
name|continueTrying
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitBetweenTriesMs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignoreMe
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
block|}
if|if
condition|(
name|coreContainer
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Stop trying to send recovery command to downed replica core={} coreNodeName={} on "
operator|+
name|replicaNodeName
operator|+
literal|" because my core container is closed."
argument_list|,
name|coreNeedingRecovery
argument_list|,
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
name|continueTrying
operator|=
literal|false
expr_stmt|;
break|break;
block|}
comment|// see if the replica's node is still live, if not, no need to keep doing this loop
name|ZkStateReader
name|zkStateReader
init|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
decl_stmt|;
try|try
block|{
name|zkStateReader
operator|.
name|updateClusterState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error when updating cluster state: "
operator|+
name|exc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|zkStateReader
operator|.
name|getClusterState
argument_list|()
operator|.
name|liveNodesContain
argument_list|(
name|replicaNodeName
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Node "
operator|+
name|replicaNodeName
operator|+
literal|" hosting core "
operator|+
name|coreNeedingRecovery
operator|+
literal|" is no longer live. No need to keep trying to tell it to recover!"
argument_list|)
expr_stmt|;
name|continueTrying
operator|=
literal|false
expr_stmt|;
break|break;
block|}
comment|// stop trying if I'm no longer the leader
if|if
condition|(
name|leaderCoreNodeName
operator|!=
literal|null
operator|&&
name|collection
operator|!=
literal|null
condition|)
block|{
name|String
name|leaderCoreNodeNameFromZk
init|=
literal|null
decl_stmt|;
try|try
block|{
name|leaderCoreNodeNameFromZk
operator|=
name|zkController
operator|.
name|getZkStateReader
argument_list|()
operator|.
name|getLeaderRetry
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
literal|1000
argument_list|)
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to determine if "
operator|+
name|leaderCoreNodeName
operator|+
literal|" is still the leader for "
operator|+
name|collection
operator|+
literal|" "
operator|+
name|shardId
operator|+
literal|" before starting leader-initiated recovery thread for "
operator|+
name|replicaUrl
operator|+
literal|" due to: "
operator|+
name|exc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|leaderCoreNodeName
operator|.
name|equals
argument_list|(
name|leaderCoreNodeNameFromZk
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Stop trying to send recovery command to downed replica core="
operator|+
name|coreNeedingRecovery
operator|+
literal|",coreNodeName="
operator|+
name|replicaCoreNodeName
operator|+
literal|" on "
operator|+
name|replicaNodeName
operator|+
literal|" because "
operator|+
name|leaderCoreNodeName
operator|+
literal|" is no longer the leader! New leader is "
operator|+
name|leaderCoreNodeNameFromZk
argument_list|)
expr_stmt|;
name|continueTrying
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
comment|// additional safeguard against the replica trying to be in the active state
comment|// before acknowledging the leader initiated recovery command
if|if
condition|(
name|collection
operator|!=
literal|null
operator|&&
name|shardId
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// call out to ZooKeeper to get the leader-initiated recovery state
name|String
name|lirState
init|=
name|zkController
operator|.
name|getLeaderInitiatedRecoveryState
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
name|replicaCoreNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|lirState
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Stop trying to send recovery command to downed replica core="
operator|+
name|coreNeedingRecovery
operator|+
literal|",coreNodeName="
operator|+
name|replicaCoreNodeName
operator|+
literal|" on "
operator|+
name|replicaNodeName
operator|+
literal|" because the znode no longer exists."
argument_list|)
expr_stmt|;
name|continueTrying
operator|=
literal|false
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|ZkStateReader
operator|.
name|RECOVERING
operator|.
name|equals
argument_list|(
name|lirState
argument_list|)
condition|)
block|{
comment|// replica has ack'd leader initiated recovery and entered the recovering state
comment|// so we don't need to keep looping to send the command
name|continueTrying
operator|=
literal|false
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Replica "
operator|+
name|coreNeedingRecovery
operator|+
literal|" on node "
operator|+
name|replicaNodeName
operator|+
literal|" ack'd the leader initiated recovery state, "
operator|+
literal|"no need to keep trying to send recovery command"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|leaderCoreNodeName
init|=
name|zkStateReader
operator|.
name|getLeaderRetry
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
literal|5000
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ZkCoreNodeProps
argument_list|>
name|replicaProps
init|=
name|zkStateReader
operator|.
name|getReplicaProps
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
name|leaderCoreNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicaProps
operator|!=
literal|null
operator|&&
name|replicaProps
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|ZkCoreNodeProps
name|prop
range|:
name|replicaProps
control|)
block|{
if|if
condition|(
name|replicaCoreNodeName
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Replica
operator|)
name|prop
operator|.
name|getNodeProps
argument_list|()
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|replicaState
init|=
name|prop
operator|.
name|getState
argument_list|()
decl_stmt|;
if|if
condition|(
name|ZkStateReader
operator|.
name|ACTIVE
operator|.
name|equals
argument_list|(
name|replicaState
argument_list|)
condition|)
block|{
comment|// replica published its state as "active",
comment|// which is bad if lirState is still "down"
if|if
condition|(
name|ZkStateReader
operator|.
name|DOWN
operator|.
name|equals
argument_list|(
name|lirState
argument_list|)
condition|)
block|{
comment|// OK, so the replica thinks it is active, but it never ack'd the leader initiated recovery
comment|// so its state cannot be trusted and it needs to be told to recover again ... and we keep looping here
name|log
operator|.
name|warn
argument_list|(
literal|"Replica core={} coreNodeName={} set to active but the leader thinks it should be in recovery;"
operator|+
literal|" forcing it back to down state to re-run the leader-initiated recovery process; props: "
operator|+
name|replicaProps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|coreNeedingRecovery
argument_list|,
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
comment|// force republish state to "down"
name|zkController
operator|.
name|ensureReplicaInLeaderInitiatedRecovery
argument_list|(
name|collection
argument_list|,
name|shardId
argument_list|,
name|nodeProps
argument_list|,
literal|true
argument_list|,
name|leaderCoreNodeName
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ignoreMe
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to determine state of core={} coreNodeName={} due to: "
operator|+
name|ignoreMe
argument_list|,
name|coreNeedingRecovery
argument_list|,
name|replicaCoreNodeName
argument_list|)
expr_stmt|;
comment|// eventually this loop will exhaust max tries and stop so we can just log this for now
block|}
block|}
block|}
block|}
comment|// replica is no longer in recovery on this node (may be handled on another node)
name|zkController
operator|.
name|removeReplicaFromLeaderInitiatedRecoveryHandling
argument_list|(
name|replicaUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|continueTrying
condition|)
block|{
comment|// ugh! this means the loop timed out before the recovery command could be delivered
comment|// how exotic do we want to get here?
name|log
operator|.
name|error
argument_list|(
literal|"Timed out after waiting for "
operator|+
operator|(
name|tries
operator|*
operator|(
name|waitBetweenTriesMs
operator|/
literal|1000
operator|)
operator|)
operator|+
literal|" secs to send the recovery request to: "
operator|+
name|replicaUrl
operator|+
literal|"; not much more we can do here?"
argument_list|)
expr_stmt|;
comment|// TODO: need to raise a JMX event to allow monitoring tools to take over from here
block|}
block|}
block|}
end_class

end_unit

