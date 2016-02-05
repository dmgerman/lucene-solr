begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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

begin_class
DECL|class|CdcrParams
specifier|public
class|class
name|CdcrParams
block|{
comment|/**    * The definition of a replica configuration *    */
DECL|field|REPLICA_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|REPLICA_PARAM
init|=
literal|"replica"
decl_stmt|;
comment|/**    * The source collection of a replica *    */
DECL|field|SOURCE_COLLECTION_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SOURCE_COLLECTION_PARAM
init|=
literal|"source"
decl_stmt|;
comment|/**    * The target collection of a replica *    */
DECL|field|TARGET_COLLECTION_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|TARGET_COLLECTION_PARAM
init|=
literal|"target"
decl_stmt|;
comment|/**    * The Zookeeper host of the target cluster hosting the replica *    */
DECL|field|ZK_HOST_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|ZK_HOST_PARAM
init|=
literal|"zkHost"
decl_stmt|;
comment|/**    * The definition of the {@link org.apache.solr.handler.CdcrReplicatorScheduler} configuration *    */
DECL|field|REPLICATOR_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|REPLICATOR_PARAM
init|=
literal|"replicator"
decl_stmt|;
comment|/**    * The thread pool size of the replicator *    */
DECL|field|THREAD_POOL_SIZE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|THREAD_POOL_SIZE_PARAM
init|=
literal|"threadPoolSize"
decl_stmt|;
comment|/**    * The time schedule (in ms) of the replicator *    */
DECL|field|SCHEDULE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|SCHEDULE_PARAM
init|=
literal|"schedule"
decl_stmt|;
comment|/**    * The batch size of the replicator *    */
DECL|field|BATCH_SIZE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|BATCH_SIZE_PARAM
init|=
literal|"batchSize"
decl_stmt|;
comment|/**    * The definition of the {@link org.apache.solr.handler.CdcrUpdateLogSynchronizer} configuration *    */
DECL|field|UPDATE_LOG_SYNCHRONIZER_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_LOG_SYNCHRONIZER_PARAM
init|=
literal|"updateLogSynchronizer"
decl_stmt|;
comment|/**    * The definition of the {@link org.apache.solr.handler.CdcrBufferManager} configuration *    */
DECL|field|BUFFER_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|BUFFER_PARAM
init|=
literal|"buffer"
decl_stmt|;
comment|/**    * The default state at startup of the buffer *    */
DECL|field|DEFAULT_STATE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_STATE_PARAM
init|=
literal|"defaultState"
decl_stmt|;
comment|/**    * The latest update checkpoint on a target cluster *    */
DECL|field|CHECKPOINT
specifier|public
specifier|final
specifier|static
name|String
name|CHECKPOINT
init|=
literal|"checkpoint"
decl_stmt|;
comment|/**    * The last processed version on a source cluster *    */
DECL|field|LAST_PROCESSED_VERSION
specifier|public
specifier|final
specifier|static
name|String
name|LAST_PROCESSED_VERSION
init|=
literal|"lastProcessedVersion"
decl_stmt|;
comment|/**    * A list of replica queues on a source cluster *    */
DECL|field|QUEUES
specifier|public
specifier|final
specifier|static
name|String
name|QUEUES
init|=
literal|"queues"
decl_stmt|;
comment|/**    * The size of a replica queue on a source cluster *    */
DECL|field|QUEUE_SIZE
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_SIZE
init|=
literal|"queueSize"
decl_stmt|;
comment|/**    * The timestamp of the last processed operation in a replica queue *    */
DECL|field|LAST_TIMESTAMP
specifier|public
specifier|final
specifier|static
name|String
name|LAST_TIMESTAMP
init|=
literal|"lastTimestamp"
decl_stmt|;
comment|/**    * A list of qps statistics per collection *    */
DECL|field|OPERATIONS_PER_SECOND
specifier|public
specifier|final
specifier|static
name|String
name|OPERATIONS_PER_SECOND
init|=
literal|"operationsPerSecond"
decl_stmt|;
comment|/**    * Overall counter *    */
DECL|field|COUNTER_ALL
specifier|public
specifier|final
specifier|static
name|String
name|COUNTER_ALL
init|=
literal|"all"
decl_stmt|;
comment|/**    * Counter for Adds *    */
DECL|field|COUNTER_ADDS
specifier|public
specifier|final
specifier|static
name|String
name|COUNTER_ADDS
init|=
literal|"adds"
decl_stmt|;
comment|/**    * Counter for Deletes *    */
DECL|field|COUNTER_DELETES
specifier|public
specifier|final
specifier|static
name|String
name|COUNTER_DELETES
init|=
literal|"deletes"
decl_stmt|;
comment|/**    * A list of errors per target collection *    */
DECL|field|ERRORS
specifier|public
specifier|final
specifier|static
name|String
name|ERRORS
init|=
literal|"errors"
decl_stmt|;
comment|/**    * Counter for consecutive errors encountered by a replicator thread *    */
DECL|field|CONSECUTIVE_ERRORS
specifier|public
specifier|final
specifier|static
name|String
name|CONSECUTIVE_ERRORS
init|=
literal|"consecutiveErrors"
decl_stmt|;
comment|/**    * A list of the last errors encountered by a replicator thread *    */
DECL|field|LAST
specifier|public
specifier|final
specifier|static
name|String
name|LAST
init|=
literal|"last"
decl_stmt|;
comment|/**    * Total size of transaction logs *    */
DECL|field|TLOG_TOTAL_SIZE
specifier|public
specifier|final
specifier|static
name|String
name|TLOG_TOTAL_SIZE
init|=
literal|"tlogTotalSize"
decl_stmt|;
comment|/**    * Total count of transaction logs *    */
DECL|field|TLOG_TOTAL_COUNT
specifier|public
specifier|final
specifier|static
name|String
name|TLOG_TOTAL_COUNT
init|=
literal|"tlogTotalCount"
decl_stmt|;
comment|/**    * The state of the update log synchronizer *    */
DECL|field|UPDATE_LOG_SYNCHRONIZER
specifier|public
specifier|final
specifier|static
name|String
name|UPDATE_LOG_SYNCHRONIZER
init|=
literal|"updateLogSynchronizer"
decl_stmt|;
comment|/**    * The actions supported by the CDCR API    */
DECL|enum|CdcrAction
specifier|public
enum|enum
name|CdcrAction
block|{
DECL|enum constant|START
name|START
block|,
DECL|enum constant|STOP
name|STOP
block|,
DECL|enum constant|STATUS
name|STATUS
block|,
DECL|enum constant|COLLECTIONCHECKPOINT
name|COLLECTIONCHECKPOINT
block|,
DECL|enum constant|SHARDCHECKPOINT
name|SHARDCHECKPOINT
block|,
DECL|enum constant|ENABLEBUFFER
name|ENABLEBUFFER
block|,
DECL|enum constant|DISABLEBUFFER
name|DISABLEBUFFER
block|,
DECL|enum constant|LASTPROCESSEDVERSION
name|LASTPROCESSEDVERSION
block|,
DECL|enum constant|QUEUES
name|QUEUES
block|,
DECL|enum constant|OPS
name|OPS
block|,
DECL|enum constant|ERRORS
name|ERRORS
block|;
DECL|method|get
specifier|public
specifier|static
name|CdcrAction
name|get
parameter_list|(
name|String
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|CdcrAction
operator|.
name|valueOf
argument_list|(
name|p
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
block|}
return|return
literal|null
return|;
block|}
DECL|method|toLower
specifier|public
name|String
name|toLower
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
block|}
comment|/**    * The possible states of the CDCR process    */
DECL|enum|ProcessState
specifier|public
enum|enum
name|ProcessState
block|{
DECL|enum constant|STARTED
name|STARTED
block|,
DECL|enum constant|STOPPED
name|STOPPED
block|;
DECL|method|get
specifier|public
specifier|static
name|ProcessState
name|get
parameter_list|(
name|byte
index|[]
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|ProcessState
operator|.
name|valueOf
argument_list|(
operator|new
name|String
argument_list|(
name|state
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
block|}
return|return
literal|null
return|;
block|}
DECL|method|toLower
specifier|public
name|String
name|toLower
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|toLower
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getParam
specifier|public
specifier|static
name|String
name|getParam
parameter_list|()
block|{
return|return
literal|"process"
return|;
block|}
block|}
comment|/**    * The possible states of the CDCR buffer    */
DECL|enum|BufferState
specifier|public
enum|enum
name|BufferState
block|{
DECL|enum constant|ENABLED
name|ENABLED
block|,
DECL|enum constant|DISABLED
name|DISABLED
block|;
DECL|method|get
specifier|public
specifier|static
name|BufferState
name|get
parameter_list|(
name|byte
index|[]
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
name|BufferState
operator|.
name|valueOf
argument_list|(
operator|new
name|String
argument_list|(
name|state
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{         }
block|}
return|return
literal|null
return|;
block|}
DECL|method|toLower
specifier|public
name|String
name|toLower
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
DECL|method|getBytes
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|toLower
argument_list|()
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getParam
specifier|public
specifier|static
name|String
name|getParam
parameter_list|()
block|{
return|return
literal|"buffer"
return|;
block|}
block|}
block|}
end_class

end_unit

