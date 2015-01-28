begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptContext
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
name|TaskID
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
name|embedded
operator|.
name|EmbeddedSolrServer
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
name|UpdateResponse
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
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Enables adding batches of documents to an EmbeddedSolrServer.  */
end_comment

begin_class
DECL|class|BatchWriter
class|class
name|BatchWriter
block|{
DECL|field|solr
specifier|private
specifier|final
name|EmbeddedSolrServer
name|solr
decl_stmt|;
DECL|field|batchWriteException
specifier|private
specifier|volatile
name|Exception
name|batchWriteException
init|=
literal|null
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
name|BatchWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|getBatchWriteException
specifier|public
name|Exception
name|getBatchWriteException
parameter_list|()
block|{
return|return
name|batchWriteException
return|;
block|}
DECL|method|setBatchWriteException
specifier|public
name|void
name|setBatchWriteException
parameter_list|(
name|Exception
name|batchWriteException
parameter_list|)
block|{
name|this
operator|.
name|batchWriteException
operator|=
name|batchWriteException
expr_stmt|;
block|}
comment|/** The number of writing threads. */
DECL|field|writerThreads
specifier|final
name|int
name|writerThreads
decl_stmt|;
comment|/** Queue Size */
DECL|field|queueSize
specifier|final
name|int
name|queueSize
decl_stmt|;
DECL|field|batchPool
specifier|private
specifier|final
name|ThreadPoolExecutor
name|batchPool
decl_stmt|;
DECL|field|taskId
specifier|private
name|TaskID
name|taskId
init|=
literal|null
decl_stmt|;
comment|/**    * The number of in progress batches, must be zero before the close can    * actually start closing    */
DECL|field|executingBatches
name|AtomicInteger
name|executingBatches
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**    * Create the batch writer object, set the thread to daemon mode, and start    * it.    *     */
DECL|class|Batch
specifier|final
class|class
name|Batch
implements|implements
name|Runnable
block|{
DECL|field|documents
specifier|private
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|documents
decl_stmt|;
DECL|field|result
specifier|private
name|UpdateResponse
name|result
decl_stmt|;
DECL|method|Batch
specifier|public
name|Batch
parameter_list|(
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
parameter_list|)
block|{
name|documents
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|batch
argument_list|)
expr_stmt|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|executingBatches
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|result
operator|=
name|runUpdate
argument_list|(
name|documents
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|executingBatches
operator|.
name|getAndDecrement
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getDocuments
specifier|protected
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|getDocuments
parameter_list|()
block|{
return|return
name|documents
return|;
block|}
DECL|method|setDocuments
specifier|protected
name|void
name|setDocuments
parameter_list|(
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|documents
parameter_list|)
block|{
name|this
operator|.
name|documents
operator|=
name|documents
expr_stmt|;
block|}
DECL|method|getResult
specifier|protected
name|UpdateResponse
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
DECL|method|setResult
specifier|protected
name|void
name|setResult
parameter_list|(
name|UpdateResponse
name|result
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|(
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|documents
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|documents
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|documents
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|documents
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|documents
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|documents
operator|.
name|addAll
argument_list|(
name|documents
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|reset
specifier|protected
name|void
name|reset
parameter_list|(
name|SolrInputDocument
name|document
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|documents
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|documents
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|documents
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|documents
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|runUpdate
specifier|protected
name|UpdateResponse
name|runUpdate
parameter_list|(
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|batchToWrite
parameter_list|)
block|{
try|try
block|{
name|UpdateResponse
name|result
init|=
name|solr
operator|.
name|add
argument_list|(
name|batchToWrite
argument_list|)
decl_stmt|;
name|SolrRecordWriter
operator|.
name|incrementCounter
argument_list|(
name|taskId
argument_list|,
name|SolrCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrCounters
operator|.
name|BATCHES_WRITTEN
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|SolrRecordWriter
operator|.
name|incrementCounter
argument_list|(
name|taskId
argument_list|,
name|SolrCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrCounters
operator|.
name|DOCUMENTS_WRITTEN
operator|.
name|toString
argument_list|()
argument_list|,
name|batchToWrite
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|SolrRecordWriter
operator|.
name|incrementCounter
argument_list|(
name|taskId
argument_list|,
name|SolrCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrCounters
operator|.
name|BATCH_WRITE_TIME
operator|.
name|toString
argument_list|()
argument_list|,
name|result
operator|.
name|getElapsedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|Exception
condition|)
block|{
name|setBatchWriteException
argument_list|(
operator|(
name|Exception
operator|)
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setBatchWriteException
argument_list|(
operator|new
name|Exception
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SolrRecordWriter
operator|.
name|incrementCounter
argument_list|(
name|taskId
argument_list|,
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
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to process batch"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|BatchWriter
specifier|public
name|BatchWriter
parameter_list|(
name|EmbeddedSolrServer
name|solr
parameter_list|,
name|int
name|batchSize
parameter_list|,
name|TaskID
name|tid
parameter_list|,
name|int
name|writerThreads
parameter_list|,
name|int
name|queueSize
parameter_list|)
block|{
name|this
operator|.
name|solr
operator|=
name|solr
expr_stmt|;
name|this
operator|.
name|writerThreads
operator|=
name|writerThreads
expr_stmt|;
name|this
operator|.
name|queueSize
operator|=
name|queueSize
expr_stmt|;
name|taskId
operator|=
name|tid
expr_stmt|;
comment|// we need to obtain the settings before the constructor
if|if
condition|(
name|writerThreads
operator|!=
literal|0
condition|)
block|{
name|batchPool
operator|=
operator|new
name|ThreadPoolExecutor
argument_list|(
name|writerThreads
argument_list|,
name|writerThreads
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|queueSize
argument_list|)
argument_list|,
operator|new
name|ThreadPoolExecutor
operator|.
name|CallerRunsPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// single threaded case
name|batchPool
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|queueBatch
specifier|public
name|void
name|queueBatch
parameter_list|(
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|throwIf
argument_list|()
expr_stmt|;
name|Batch
name|b
init|=
operator|new
name|Batch
argument_list|(
name|batch
argument_list|)
decl_stmt|;
if|if
condition|(
name|batchPool
operator|!=
literal|null
condition|)
block|{
name|batchPool
operator|.
name|execute
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// single threaded case
name|b
operator|.
name|run
argument_list|()
expr_stmt|;
name|throwIf
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|SolrServerException
throws|,
name|IOException
block|{
if|if
condition|(
name|batchPool
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|setStatus
argument_list|(
literal|"Waiting for batches to complete"
argument_list|)
expr_stmt|;
name|batchPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|batchPool
operator|.
name|isTerminated
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"Waiting for %d items and %d threads to finish executing"
argument_list|,
name|batchPool
operator|.
name|getQueue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|batchPool
operator|.
name|getActiveCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|batchPool
operator|.
name|awaitTermination
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|setStatus
argument_list|(
literal|"Committing Solr Phase 1"
argument_list|)
expr_stmt|;
name|solr
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
literal|"Optimizing Solr"
argument_list|)
expr_stmt|;
name|int
name|maxSegments
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInt
argument_list|(
name|SolrOutputFormat
operator|.
name|SOLR_RECORD_WRITER_MAX_SEGMENTS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Optimizing Solr: forcing merge down to {} segments"
argument_list|,
name|maxSegments
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|solr
operator|.
name|optimize
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|maxSegments
argument_list|)
expr_stmt|;
name|context
operator|.
name|getCounter
argument_list|(
name|SolrCounters
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|SolrCounters
operator|.
name|PHYSICAL_REDUCER_MERGE_TIME
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
name|float
name|secs
init|=
operator|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
operator|)
operator|/
call|(
name|float
call|)
argument_list|(
literal|10
operator|^
literal|9
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Optimizing Solr: done forcing merge down to {} segments in {} secs"
argument_list|,
name|maxSegments
argument_list|,
name|secs
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
literal|"Committing Solr Phase 2"
argument_list|)
expr_stmt|;
name|solr
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
literal|"Shutting down Solr"
argument_list|)
expr_stmt|;
name|solr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Throw a legal exception if a previous batch write had an exception. The    * previous state is cleared. Uses {@link #batchWriteException} for the state    * from the last exception.    *     * This will loose individual exceptions if the exceptions happen rapidly.    *     * @throws IOException On low level IO error    * @throws SolrServerException On Solr Exception    */
DECL|method|throwIf
specifier|private
name|void
name|throwIf
parameter_list|()
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
specifier|final
name|Exception
name|last
init|=
name|batchWriteException
decl_stmt|;
name|batchWriteException
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|last
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|last
operator|instanceof
name|SolrServerException
condition|)
block|{
throw|throw
operator|(
name|SolrServerException
operator|)
name|last
throw|;
block|}
if|if
condition|(
name|last
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|last
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Batch Write Failure"
argument_list|,
name|last
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

