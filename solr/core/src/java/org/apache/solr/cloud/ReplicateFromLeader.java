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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
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
name|core
operator|.
name|CoreContainer
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
name|SolrConfig
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
name|update
operator|.
name|CommitUpdateCommand
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
name|SolrIndexWriter
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
DECL|class|ReplicateFromLeader
specifier|public
class|class
name|ReplicateFromLeader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
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
DECL|field|cc
specifier|private
name|CoreContainer
name|cc
decl_stmt|;
DECL|field|coreName
specifier|private
name|String
name|coreName
decl_stmt|;
DECL|field|replicationProcess
specifier|private
name|ReplicationHandler
name|replicationProcess
decl_stmt|;
DECL|field|lastVersion
specifier|private
name|long
name|lastVersion
init|=
literal|0
decl_stmt|;
DECL|method|ReplicateFromLeader
specifier|public
name|ReplicateFromLeader
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
name|this
operator|.
name|cc
operator|=
name|cc
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|coreName
expr_stmt|;
block|}
comment|/**    * Start a replication handler thread that will periodically pull indices from the shard leader    * @param switchTransactionLog if true, ReplicationHandler will rotate the transaction log once    * the replication is done    */
DECL|method|startReplication
specifier|public
name|void
name|startReplication
parameter_list|(
name|boolean
name|switchTransactionLog
parameter_list|)
throws|throws
name|InterruptedException
block|{
try|try
init|(
name|SolrCore
name|core
init|=
name|cc
operator|.
name|getCore
argument_list|(
name|coreName
argument_list|)
init|)
block|{
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
return|return;
block|}
else|else
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
literal|"SolrCore not found:"
operator|+
name|coreName
operator|+
literal|" in "
operator|+
name|cc
operator|.
name|getLoadedCoreNames
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|SolrConfig
operator|.
name|UpdateHandlerInfo
name|uinfo
init|=
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getUpdateHandlerInfo
argument_list|()
decl_stmt|;
name|String
name|pollIntervalStr
init|=
literal|"00:00:03"
decl_stmt|;
if|if
condition|(
name|uinfo
operator|.
name|autoCommmitMaxTime
operator|!=
operator|-
literal|1
condition|)
block|{
name|pollIntervalStr
operator|=
name|toPollIntervalStr
argument_list|(
name|uinfo
operator|.
name|autoCommmitMaxTime
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|uinfo
operator|.
name|autoSoftCommmitMaxTime
operator|!=
operator|-
literal|1
condition|)
block|{
name|pollIntervalStr
operator|=
name|toPollIntervalStr
argument_list|(
name|uinfo
operator|.
name|autoSoftCommmitMaxTime
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Will start replication from leader with poll interval: {}"
argument_list|,
name|pollIntervalStr
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|slaveConfig
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|slaveConfig
operator|.
name|add
argument_list|(
literal|"fetchFromLeader"
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|slaveConfig
operator|.
name|add
argument_list|(
literal|"pollInterval"
argument_list|,
name|pollIntervalStr
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|replicationConfig
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|replicationConfig
operator|.
name|add
argument_list|(
literal|"slave"
argument_list|,
name|slaveConfig
argument_list|)
expr_stmt|;
name|String
name|lastCommitVersion
init|=
name|getCommitVersion
argument_list|(
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastCommitVersion
operator|!=
literal|null
condition|)
block|{
name|lastVersion
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|lastCommitVersion
argument_list|)
expr_stmt|;
block|}
name|replicationProcess
operator|=
operator|new
name|ReplicationHandler
argument_list|()
expr_stmt|;
if|if
condition|(
name|switchTransactionLog
condition|)
block|{
name|replicationProcess
operator|.
name|setPollListener
argument_list|(
parameter_list|(
name|solrCore
parameter_list|,
name|pollSuccess
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|pollSuccess
condition|)
block|{
name|String
name|commitVersion
init|=
name|getCommitVersion
argument_list|(
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitVersion
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|Long
operator|.
name|parseLong
argument_list|(
name|commitVersion
argument_list|)
operator|==
name|lastVersion
condition|)
return|return;
name|UpdateLog
name|updateLog
init|=
name|solrCore
operator|.
name|getUpdateHandler
argument_list|()
operator|.
name|getUpdateLog
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
decl_stmt|;
name|CommitUpdateCommand
name|cuc
init|=
operator|new
name|CommitUpdateCommand
argument_list|(
name|req
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|cuc
operator|.
name|setVersion
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|commitVersion
argument_list|)
argument_list|)
expr_stmt|;
name|updateLog
operator|.
name|copyOverOldUpdates
argument_list|(
name|cuc
argument_list|)
expr_stmt|;
name|lastVersion
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|commitVersion
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|replicationProcess
operator|.
name|init
argument_list|(
name|replicationConfig
argument_list|)
expr_stmt|;
name|replicationProcess
operator|.
name|inform
argument_list|(
name|core
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCommitVersion
specifier|public
specifier|static
name|String
name|getCommitVersion
parameter_list|(
name|SolrCore
name|solrCore
parameter_list|)
block|{
name|IndexCommit
name|commit
init|=
name|solrCore
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|getLatestCommit
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|commitVersion
init|=
name|commit
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|SolrIndexWriter
operator|.
name|COMMIT_COMMAND_VERSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitVersion
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|commitVersion
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get commit command version from index commit point "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|toPollIntervalStr
specifier|private
specifier|static
name|String
name|toPollIntervalStr
parameter_list|(
name|int
name|ms
parameter_list|)
block|{
name|int
name|sec
init|=
name|ms
operator|/
literal|1000
decl_stmt|;
name|int
name|hour
init|=
name|sec
operator|/
literal|3600
decl_stmt|;
name|sec
operator|=
name|sec
operator|%
literal|3600
expr_stmt|;
name|int
name|min
init|=
name|sec
operator|/
literal|60
decl_stmt|;
name|sec
operator|=
name|sec
operator|%
literal|60
expr_stmt|;
return|return
name|hour
operator|+
literal|":"
operator|+
name|min
operator|+
literal|":"
operator|+
name|sec
return|;
block|}
DECL|method|stopReplication
specifier|public
name|void
name|stopReplication
parameter_list|()
block|{
if|if
condition|(
name|replicationProcess
operator|!=
literal|null
condition|)
block|{
name|replicationProcess
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

