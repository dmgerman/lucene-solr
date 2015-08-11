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
name|conf
operator|.
name|Configuration
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
name|filecache
operator|.
name|DistributedCache
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|RecordWriter
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
name|HdfsDirectoryFactory
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
name|core
operator|.
name|SolrResourceLoader
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
name|Arrays
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|TimeUnit
import|;
end_import

begin_class
DECL|class|SolrRecordWriter
class|class
name|SolrRecordWriter
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
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
name|SolrRecordWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|allowedConfigDirectories
specifier|public
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|allowedConfigDirectories
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"conf"
block|,
literal|"lib"
block|,
literal|"solr.xml"
block|}
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|requiredConfigDirectories
specifier|public
specifier|final
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|requiredConfigDirectories
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|requiredConfigDirectories
operator|.
name|add
argument_list|(
literal|"conf"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the list of directories names that may be included in the    * configuration data passed to the tasks.    *     * @return an UnmodifiableList of directory names    */
DECL|method|getAllowedConfigDirectories
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getAllowedConfigDirectories
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|allowedConfigDirectories
argument_list|)
return|;
block|}
comment|/**    * check if the passed in directory is required to be present in the    * configuration data set.    *     * @param directory The directory to check    * @return true if the directory is required.    */
DECL|method|isRequiredConfigDirectory
specifier|public
specifier|static
name|boolean
name|isRequiredConfigDirectory
parameter_list|(
specifier|final
name|String
name|directory
parameter_list|)
block|{
return|return
name|requiredConfigDirectories
operator|.
name|contains
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/** The path that the final index will be written to */
comment|/** The location in a local temporary directory that the index is built in. */
comment|//  /**
comment|//   * If true, create a zip file of the completed index in the final storage
comment|//   * location A .zip will be appended to the final output name if it is not
comment|//   * already present.
comment|//   */
comment|//  private boolean outputZipFile = false;
DECL|field|heartBeater
specifier|private
specifier|final
name|HeartBeater
name|heartBeater
decl_stmt|;
DECL|field|batchWriter
specifier|private
specifier|final
name|BatchWriter
name|batchWriter
decl_stmt|;
DECL|field|batch
specifier|private
specifier|final
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
decl_stmt|;
DECL|field|batchSize
specifier|private
specifier|final
name|int
name|batchSize
decl_stmt|;
DECL|field|numDocsWritten
specifier|private
name|long
name|numDocsWritten
init|=
literal|0
decl_stmt|;
DECL|field|nextLogTime
specifier|private
name|long
name|nextLogTime
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
DECL|field|contextMap
specifier|private
specifier|static
name|HashMap
argument_list|<
name|TaskID
argument_list|,
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
operator|.
name|Context
argument_list|>
name|contextMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|SolrRecordWriter
specifier|public
name|SolrRecordWriter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|outputShardDir
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
name|this
operator|.
name|batch
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|batchSize
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
comment|// setLogLevel("org.apache.solr.core", "WARN");
comment|// setLogLevel("org.apache.solr.update", "WARN");
name|heartBeater
operator|=
operator|new
name|HeartBeater
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
name|heartBeater
operator|.
name|needHeartBeat
argument_list|()
expr_stmt|;
name|Path
name|solrHomeDir
init|=
name|SolrRecordWriter
operator|.
name|findSolrConfig
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|outputShardDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|EmbeddedSolrServer
name|solr
init|=
name|createEmbeddedSolrServer
argument_list|(
name|solrHomeDir
argument_list|,
name|fs
argument_list|,
name|outputShardDir
argument_list|)
decl_stmt|;
name|batchWriter
operator|=
operator|new
name|BatchWriter
argument_list|(
name|solr
argument_list|,
name|batchSize
argument_list|,
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
argument_list|,
name|SolrOutputFormat
operator|.
name|getSolrWriterThreadCount
argument_list|(
name|conf
argument_list|)
argument_list|,
name|SolrOutputFormat
operator|.
name|getSolrWriterQueueSize
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"Failed to initialize record writer for %s, %s"
argument_list|,
name|context
operator|.
name|getJobName
argument_list|()
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"mapred.task.id"
argument_list|)
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
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
DECL|method|createEmbeddedSolrServer
specifier|public
specifier|static
name|EmbeddedSolrServer
name|createEmbeddedSolrServer
parameter_list|(
name|Path
name|solrHomeDir
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|Path
name|outputShardDir
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating embedded Solr server with solrHomeDir: "
operator|+
name|solrHomeDir
operator|+
literal|", fs: "
operator|+
name|fs
operator|+
literal|", outputShardDir: "
operator|+
name|outputShardDir
argument_list|)
expr_stmt|;
name|Path
name|solrDataDir
init|=
operator|new
name|Path
argument_list|(
name|outputShardDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|String
name|dataDirStr
init|=
name|solrDataDir
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHomeDir
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
literal|"Constructed instance information solr.home %s (%s), instance dir %s, conf dir %s, writing index to solr.data.dir %s, with permdir %s"
argument_list|,
name|solrHomeDir
argument_list|,
name|solrHomeDir
operator|.
name|toUri
argument_list|()
argument_list|,
name|loader
operator|.
name|getInstanceDir
argument_list|()
argument_list|,
name|loader
operator|.
name|getConfigDir
argument_list|()
argument_list|,
name|dataDirStr
argument_list|,
name|outputShardDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: This is fragile and should be well documented
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.directoryFactory"
argument_list|,
name|HdfsDirectoryFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.lock.type"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.nrtcachingdirectory"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.hdfs.blockcache.enabled"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.autoCommit.maxTime"
argument_list|,
literal|"600000"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.autoSoftCommit.maxTime"
argument_list|,
literal|"-1"
argument_list|)
expr_stmt|;
name|CoreContainer
name|container
init|=
operator|new
name|CoreContainer
argument_list|(
name|loader
argument_list|)
decl_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|CoreDescriptor
operator|.
name|CORE_DATADIR
argument_list|,
name|dataDirStr
argument_list|)
expr_stmt|;
name|CoreDescriptor
name|descr
init|=
operator|new
name|CoreDescriptor
argument_list|(
name|container
argument_list|,
literal|"core1"
argument_list|,
name|solrHomeDir
operator|.
name|toString
argument_list|()
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|SolrCore
name|core
init|=
name|container
operator|.
name|create
argument_list|(
name|descr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|core
operator|.
name|getDirectoryFactory
argument_list|()
operator|instanceof
name|HdfsDirectoryFactory
operator|)
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Invalid configuration. Currently, the only DirectoryFactory supported is "
operator|+
name|HdfsDirectoryFactory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
throw|;
block|}
name|EmbeddedSolrServer
name|solr
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|container
argument_list|,
literal|"core1"
argument_list|)
decl_stmt|;
return|return
name|solr
return|;
block|}
DECL|method|incrementCounter
specifier|public
specifier|static
name|void
name|incrementCounter
parameter_list|(
name|TaskID
name|taskId
parameter_list|,
name|String
name|groupName
parameter_list|,
name|String
name|counterName
parameter_list|,
name|long
name|incr
parameter_list|)
block|{
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
operator|.
name|Context
name|context
init|=
name|contextMap
operator|.
name|get
argument_list|(
name|taskId
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|groupName
argument_list|,
name|counterName
argument_list|)
operator|.
name|increment
argument_list|(
name|incr
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|incrementCounter
specifier|public
specifier|static
name|void
name|incrementCounter
parameter_list|(
name|TaskID
name|taskId
parameter_list|,
name|Enum
argument_list|<
name|?
argument_list|>
name|counterName
parameter_list|,
name|long
name|incr
parameter_list|)
block|{
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
operator|.
name|Context
name|context
init|=
name|contextMap
operator|.
name|get
argument_list|(
name|taskId
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getCounter
argument_list|(
name|counterName
argument_list|)
operator|.
name|increment
argument_list|(
name|incr
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addReducerContext
specifier|public
specifier|static
name|void
name|addReducerContext
parameter_list|(
name|Reducer
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
operator|.
name|Context
name|context
parameter_list|)
block|{
name|TaskID
name|taskID
init|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|getTaskID
argument_list|()
decl_stmt|;
name|contextMap
operator|.
name|put
argument_list|(
name|taskID
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|findSolrConfig
specifier|public
specifier|static
name|Path
name|findSolrConfig
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// FIXME when mrunit supports the new cache apis
comment|//URI[] localArchives = context.getCacheArchives();
name|Path
index|[]
name|localArchives
init|=
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|unpackedDir
range|:
name|localArchives
control|)
block|{
if|if
condition|(
name|unpackedDir
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|SolrOutputFormat
operator|.
name|getZipName
argument_list|(
name|conf
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using this unpacked directory as solr home: {}"
argument_list|,
name|unpackedDir
argument_list|)
expr_stmt|;
return|return
name|unpackedDir
return|;
block|}
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"No local cache archives, where is %s:%s"
argument_list|,
name|SolrOutputFormat
operator|.
name|getSetupOk
argument_list|()
argument_list|,
name|SolrOutputFormat
operator|.
name|getZipName
argument_list|(
name|conf
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
comment|/**    * Write a record. This method accumulates records in to a batch, and when    * {@link #batchSize} items are present flushes it to the indexer. The writes    * can take a substantial amount of time, depending on {@link #batchSize}. If    * there is heavy disk contention the writes may take more than the 600 second    * default timeout.    */
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|heartBeater
operator|.
name|needHeartBeat
argument_list|()
expr_stmt|;
try|try
block|{
try|try
block|{
name|SolrInputDocumentWritable
name|sidw
init|=
operator|(
name|SolrInputDocumentWritable
operator|)
name|value
decl_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|sidw
operator|.
name|getSolrInputDocument
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>=
name|batchSize
condition|)
block|{
name|batchWriter
operator|.
name|queueBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|numDocsWritten
operator|+=
name|batch
operator|.
name|size
argument_list|()
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|>=
name|nextLogTime
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"docsWritten: {}"
argument_list|,
name|numDocsWritten
argument_list|)
expr_stmt|;
name|nextLogTime
operator|+=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|convert
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
name|batch
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|heartBeater
operator|.
name|setProgress
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|heartBeater
operator|.
name|needHeartBeat
argument_list|()
expr_stmt|;
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|batchWriter
operator|.
name|queueBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|numDocsWritten
operator|+=
name|batch
operator|.
name|size
argument_list|()
expr_stmt|;
name|batch
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"docsWritten: {}"
argument_list|,
name|numDocsWritten
argument_list|)
expr_stmt|;
name|batchWriter
operator|.
name|close
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|//      if (outputZipFile) {
comment|//        context.setStatus("Writing Zip");
comment|//        packZipFile(); // Written to the perm location
comment|//      } else {
comment|//        context.setStatus("Copying Index");
comment|//        fs.completeLocalOutput(perm, temp); // copy to dfs
comment|//      }
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
throw|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|heartBeater
operator|.
name|cancelHeartBeat
argument_list|()
expr_stmt|;
name|heartBeater
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//      File tempFile = new File(temp.toString());
comment|//      if (tempFile.exists()) {
comment|//        FileUtils.forceDelete(new File(temp.toString()));
comment|//      }
block|}
name|context
operator|.
name|setStatus
argument_list|(
literal|"Done"
argument_list|)
expr_stmt|;
block|}
comment|//  private void packZipFile() throws IOException {
comment|//    FSDataOutputStream out = null;
comment|//    ZipOutputStream zos = null;
comment|//    int zipCount = 0;
comment|//    LOG.info("Packing zip file for " + perm);
comment|//    try {
comment|//      out = fs.create(perm, false);
comment|//      zos = new ZipOutputStream(out);
comment|//
comment|//      String name = perm.getName().replaceAll(".zip$", "");
comment|//      LOG.info("adding index directory" + temp);
comment|//      zipCount = zipDirectory(conf, zos, name, temp.toString(), temp);
comment|//      /**
comment|//      for (String configDir : allowedConfigDirectories) {
comment|//        if (!isRequiredConfigDirectory(configDir)) {
comment|//          continue;
comment|//        }
comment|//        final Path confPath = new Path(solrHome, configDir);
comment|//        LOG.info("adding configdirectory" + confPath);
comment|//
comment|//        zipCount += zipDirectory(conf, zos, name, solrHome.toString(), confPath);
comment|//      }
comment|//      **/
comment|//    } catch (Throwable ohFoo) {
comment|//      LOG.error("packZipFile exception", ohFoo);
comment|//      if (ohFoo instanceof RuntimeException) {
comment|//        throw (RuntimeException) ohFoo;
comment|//      }
comment|//      if (ohFoo instanceof IOException) {
comment|//        throw (IOException) ohFoo;
comment|//      }
comment|//      throw new IOException(ohFoo);
comment|//
comment|//    } finally {
comment|//      if (zos != null) {
comment|//        if (zipCount == 0) { // If no entries were written, only close out, as
comment|//                             // the zip will throw an error
comment|//          LOG.error("No entries written to zip file " + perm);
comment|//          fs.delete(perm, false);
comment|//          // out.close();
comment|//        } else {
comment|//          LOG.info(String.format("Wrote %d items to %s for %s", zipCount, perm,
comment|//              temp));
comment|//          zos.close();
comment|//        }
comment|//      }
comment|//    }
comment|//  }
comment|//
comment|//  /**
comment|//   * Write a file to a zip output stream, removing leading path name components
comment|//   * from the actual file name when creating the zip file entry.
comment|//   *
comment|//   * The entry placed in the zip file is<code>baseName</code>/
comment|//   *<code>relativePath</code>, where<code>relativePath</code> is constructed
comment|//   * by removing a leading<code>root</code> from the path for
comment|//   *<code>itemToZip</code>.
comment|//   *
comment|//   * If<code>itemToZip</code> is an empty directory, it is ignored. If
comment|//   *<code>itemToZip</code> is a directory, the contents of the directory are
comment|//   * added recursively.
comment|//   *
comment|//   * @param zos The zip output stream
comment|//   * @param baseName The base name to use for the file name entry in the zip
comment|//   *        file
comment|//   * @param root The path to remove from<code>itemToZip</code> to make a
comment|//   *        relative path name
comment|//   * @param itemToZip The path to the file to be added to the zip file
comment|//   * @return the number of entries added
comment|//   * @throws IOException
comment|//   */
comment|//  static public int zipDirectory(final Configuration conf,
comment|//      final ZipOutputStream zos, final String baseName, final String root,
comment|//      final Path itemToZip) throws IOException {
comment|//    LOG
comment|//        .info(String
comment|//            .format("zipDirectory: %s %s %s", baseName, root, itemToZip));
comment|//    LocalFileSystem localFs = FileSystem.getLocal(conf);
comment|//    int count = 0;
comment|//
comment|//    final FileStatus itemStatus = localFs.getFileStatus(itemToZip);
comment|//    if (itemStatus.isDirectory()) {
comment|//      final FileStatus[] statai = localFs.listStatus(itemToZip);
comment|//
comment|//      // Add a directory entry to the zip file
comment|//      final String zipDirName = relativePathForZipEntry(itemToZip.toUri()
comment|//          .getPath(), baseName, root);
comment|//      final ZipEntry dirZipEntry = new ZipEntry(zipDirName
comment|//          + Path.SEPARATOR_CHAR);
comment|//      LOG.info(String.format("Adding directory %s to zip", zipDirName));
comment|//      zos.putNextEntry(dirZipEntry);
comment|//      zos.closeEntry();
comment|//      count++;
comment|//
comment|//      if (statai == null || statai.length == 0) {
comment|//        LOG.info(String.format("Skipping empty directory %s", itemToZip));
comment|//        return count;
comment|//      }
comment|//      for (FileStatus status : statai) {
comment|//        count += zipDirectory(conf, zos, baseName, root, status.getPath());
comment|//      }
comment|//      LOG.info(String.format("Wrote %d entries for directory %s", count,
comment|//          itemToZip));
comment|//      return count;
comment|//    }
comment|//
comment|//    final String inZipPath = relativePathForZipEntry(itemToZip.toUri()
comment|//        .getPath(), baseName, root);
comment|//
comment|//    if (inZipPath.length() == 0) {
comment|//      LOG.warn(String.format("Skipping empty zip file path for %s (%s %s)",
comment|//          itemToZip, root, baseName));
comment|//      return 0;
comment|//    }
comment|//
comment|//    // Take empty files in case the place holder is needed
comment|//    FSDataInputStream in = null;
comment|//    try {
comment|//      in = localFs.open(itemToZip);
comment|//      final ZipEntry ze = new ZipEntry(inZipPath);
comment|//      ze.setTime(itemStatus.getModificationTime());
comment|//      // Comments confuse looking at the zip file
comment|//      // ze.setComment(itemToZip.toString());
comment|//      zos.putNextEntry(ze);
comment|//
comment|//      IOUtils.copyBytes(in, zos, conf, false);
comment|//      zos.closeEntry();
comment|//      LOG.info(String.format("Wrote %d entries for file %s", count, itemToZip));
comment|//      return 1;
comment|//    } finally {
comment|//      in.close();
comment|//    }
comment|//
comment|//  }
comment|//
comment|//  static String relativePathForZipEntry(final String rawPath,
comment|//      final String baseName, final String root) {
comment|//    String relativePath = rawPath.replaceFirst(Pattern.quote(root.toString()),
comment|//        "");
comment|//    LOG.info(String.format("RawPath %s, baseName %s, root %s, first %s",
comment|//        rawPath, baseName, root, relativePath));
comment|//
comment|//    if (relativePath.startsWith(Path.SEPARATOR)) {
comment|//      relativePath = relativePath.substring(1);
comment|//    }
comment|//    LOG.info(String.format(
comment|//        "RawPath %s, baseName %s, root %s, post leading slash %s", rawPath,
comment|//        baseName, root, relativePath));
comment|//    if (relativePath.isEmpty()) {
comment|//      LOG.warn(String.format(
comment|//          "No data after root (%s) removal from raw path %s", root, rawPath));
comment|//      return baseName;
comment|//    }
comment|//    // Construct the path that will be written to the zip file, including
comment|//    // removing any leading '/' characters
comment|//    String inZipPath = baseName + Path.SEPARATOR_CHAR + relativePath;
comment|//
comment|//    LOG.info(String.format("RawPath %s, baseName %s, root %s, inZip 1 %s",
comment|//        rawPath, baseName, root, inZipPath));
comment|//    if (inZipPath.startsWith(Path.SEPARATOR)) {
comment|//      inZipPath = inZipPath.substring(1);
comment|//    }
comment|//    LOG.info(String.format("RawPath %s, baseName %s, root %s, inZip 2 %s",
comment|//        rawPath, baseName, root, inZipPath));
comment|//
comment|//    return inZipPath;
comment|//
comment|//  }
comment|//
comment|/*   static boolean setLogLevel(String packageName, String level) {     Log logger = LogFactory.getLog(packageName);     if (logger == null) {       return false;     }     // look for: org.apache.commons.logging.impl.SLF4JLocationAwareLog     LOG.warn("logger class:"+logger.getClass().getName());     if (logger instanceof Log4JLogger) {       process(((Log4JLogger) logger).getLogger(), level);       return true;     }     if (logger instanceof Jdk14Logger) {       process(((Jdk14Logger) logger).getLogger(), level);       return true;     }     return false;   }    public static void process(org.apache.log4j.Logger log, String level) {     if (level != null) {       log.setLevel(org.apache.log4j.Level.toLevel(level));     }   }    public static void process(java.util.logging.Logger log, String level) {     if (level != null) {       log.setLevel(java.util.logging.Level.parse(level));     }   }   */
block|}
end_class

end_unit

