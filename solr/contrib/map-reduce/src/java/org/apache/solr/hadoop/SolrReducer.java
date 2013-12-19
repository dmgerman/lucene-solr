begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
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
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Reducer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ReflectionUtils
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
name|hadoop
operator|.
name|dedup
operator|.
name|NoChangeUpdateConflictResolver
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
name|hadoop
operator|.
name|dedup
operator|.
name|RetainMostRecentUpdateConflictResolver
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
name|hadoop
operator|.
name|dedup
operator|.
name|UpdateConflictResolver
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
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|api
operator|.
name|ExceptionHandler
import|;
end_import

begin_import
import|import
name|com
operator|.
name|cloudera
operator|.
name|cdk
operator|.
name|morphline
operator|.
name|base
operator|.
name|FaultTolerance
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * This class loads the mapper's SolrInputDocuments into one EmbeddedSolrServer  * per reducer. Each such reducer and Solr server can be seen as a (micro)  * shard. The Solr servers store their data in HDFS.  *   * More specifically, this class consumes a list of&lt;docId, SolrInputDocument&gt;  * pairs, sorted by docId, and sends them to an embedded Solr server to generate  * a Solr index shard from the documents.  */
end_comment

begin_class
DECL|class|SolrReducer
specifier|public
class|class
name|SolrReducer
extends|extends
name|Reducer
argument_list|<
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|,
name|Text
argument_list|,
name|SolrInputDocumentWritable
argument_list|>
block|{
DECL|field|resolver
specifier|private
name|UpdateConflictResolver
name|resolver
decl_stmt|;
DECL|field|heartBeater
specifier|private
name|HeartBeater
name|heartBeater
decl_stmt|;
DECL|field|exceptionHandler
specifier|private
name|ExceptionHandler
name|exceptionHandler
decl_stmt|;
DECL|field|UPDATE_CONFLICT_RESOLVER
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_CONFLICT_RESOLVER
init|=
name|SolrReducer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".updateConflictResolver"
decl_stmt|;
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
name|SolrReducer
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|setup
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|verifyPartitionAssignment
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|SolrRecordWriter
operator|.
name|addReducerContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|UpdateConflictResolver
argument_list|>
name|resolverClass
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getClass
argument_list|(
name|UPDATE_CONFLICT_RESOLVER
argument_list|,
name|RetainMostRecentUpdateConflictResolver
operator|.
name|class
argument_list|,
name|UpdateConflictResolver
operator|.
name|class
argument_list|)
decl_stmt|;
name|this
operator|.
name|resolver
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|resolverClass
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
comment|/*      * Note that ReflectionUtils.newInstance() above also implicitly calls      * resolver.configure(context.getConfiguration()) if the resolver      * implements org.apache.hadoop.conf.Configurable      */
name|this
operator|.
name|exceptionHandler
operator|=
operator|new
name|FaultTolerance
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|FaultTolerance
operator|.
name|IS_PRODUCTION_MODE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|FaultTolerance
operator|.
name|IS_IGNORING_RECOVERABLE_EXCEPTIONS
argument_list|,
literal|false
argument_list|)
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|FaultTolerance
operator|.
name|RECOVERABLE_EXCEPTION_CLASSES
argument_list|,
name|SolrServerException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|heartBeater
operator|=
operator|new
name|HeartBeater
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|reduce
specifier|protected
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterable
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|heartBeater
operator|.
name|needHeartBeat
argument_list|()
expr_stmt|;
try|try
block|{
name|values
operator|=
name|resolve
argument_list|(
name|key
argument_list|,
name|values
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|super
operator|.
name|reduce
argument_list|(
name|key
argument_list|,
name|values
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to process key "
operator|+
name|key
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|context
operator|.
name|getCounter
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".errors"
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|exceptionHandler
operator|.
name|handleException
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|heartBeater
operator|.
name|cancelHeartBeat
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|resolve
specifier|private
name|Iterable
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|resolve
parameter_list|(
specifier|final
name|Text
name|key
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|values
parameter_list|,
specifier|final
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
name|resolver
operator|instanceof
name|NoChangeUpdateConflictResolver
condition|)
block|{
return|return
name|values
return|;
comment|// fast path
block|}
return|return
operator|new
name|Iterable
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|WrapIterator
argument_list|(
name|resolver
operator|.
name|orderUpdates
argument_list|(
name|key
argument_list|,
operator|new
name|UnwrapIterator
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|,
name|context
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|cleanup
specifier|protected
name|void
name|cleanup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|heartBeater
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|cleanup
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/*    * Verify that if a mappers's partitioner sends an item to partition X it implies that said item    * is sent to the reducer with taskID == X. This invariant is currently required for Solr    * documents to end up in the right Solr shard.    */
DECL|method|verifyPartitionAssignment
specifier|private
name|void
name|verifyPartitionAssignment
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"verifyPartitionAssignment"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
condition|)
block|{
name|String
name|partitionStr
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapred.task.partition"
argument_list|)
decl_stmt|;
if|if
condition|(
name|partitionStr
operator|==
literal|null
condition|)
block|{
name|partitionStr
operator|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
literal|"mapreduce.task.partition"
argument_list|)
expr_stmt|;
block|}
name|int
name|partition
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|partitionStr
argument_list|)
decl_stmt|;
name|int
name|taskId
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|partition
operator|==
name|taskId
argument_list|,
literal|"mapred.task.partition: "
operator|+
name|partition
operator|+
literal|" not equal to reducer taskId: "
operator|+
name|taskId
argument_list|)
expr_stmt|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|WrapIterator
specifier|private
specifier|static
specifier|final
class|class
name|WrapIterator
implements|implements
name|Iterator
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
block|{
DECL|field|parent
specifier|private
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|parent
decl_stmt|;
DECL|method|WrapIterator
specifier|private
name|WrapIterator
parameter_list|(
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|parent
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|SolrInputDocumentWritable
name|next
parameter_list|()
block|{
return|return
operator|new
name|SolrInputDocumentWritable
argument_list|(
name|parent
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|UnwrapIterator
specifier|private
specifier|static
specifier|final
class|class
name|UnwrapIterator
implements|implements
name|Iterator
argument_list|<
name|SolrInputDocument
argument_list|>
block|{
DECL|field|parent
specifier|private
name|Iterator
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|parent
decl_stmt|;
DECL|method|UnwrapIterator
specifier|private
name|UnwrapIterator
parameter_list|(
name|Iterator
argument_list|<
name|SolrInputDocumentWritable
argument_list|>
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|parent
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|SolrInputDocument
name|next
parameter_list|()
block|{
return|return
name|parent
operator|.
name|next
argument_list|()
operator|.
name|getSolrInputDocument
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

