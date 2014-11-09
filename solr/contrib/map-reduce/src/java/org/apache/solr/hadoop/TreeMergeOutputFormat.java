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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|List
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
name|io
operator|.
name|NullWritable
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
name|lib
operator|.
name|output
operator|.
name|FileOutputFormat
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|LogMergePolicy
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
name|MergePolicy
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
name|TieredMergePolicy
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
name|misc
operator|.
name|IndexMergeTool
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|NoLockFactory
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
name|store
operator|.
name|hdfs
operator|.
name|HdfsDirectory
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
comment|/**  * See {@link IndexMergeTool}.  */
end_comment

begin_class
DECL|class|TreeMergeOutputFormat
specifier|public
class|class
name|TreeMergeOutputFormat
extends|extends
name|FileOutputFormat
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getRecordWriter
specifier|public
name|RecordWriter
name|getRecordWriter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Utils
operator|.
name|getLogConfigFile
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|workDir
init|=
name|getDefaultWorkFile
argument_list|(
name|context
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
operator|new
name|TreeMergeRecordWriter
argument_list|(
name|context
argument_list|,
name|workDir
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////////////
comment|// Nested classes:
comment|///////////////////////////////////////////////////////////////////////////////
DECL|class|TreeMergeRecordWriter
specifier|private
specifier|static
specifier|final
class|class
name|TreeMergeRecordWriter
extends|extends
name|RecordWriter
argument_list|<
name|Text
argument_list|,
name|NullWritable
argument_list|>
block|{
DECL|field|workDir
specifier|private
specifier|final
name|Path
name|workDir
decl_stmt|;
DECL|field|shards
specifier|private
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|shards
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|heartBeater
specifier|private
specifier|final
name|HeartBeater
name|heartBeater
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|TaskAttemptContext
name|context
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
name|TreeMergeRecordWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TreeMergeRecordWriter
specifier|public
name|TreeMergeRecordWriter
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|Path
name|workDir
parameter_list|)
block|{
name|this
operator|.
name|workDir
operator|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"data/index"
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
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Text
name|key
parameter_list|,
name|NullWritable
name|value
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"map key: {}"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|heartBeater
operator|.
name|needHeartBeat
argument_list|()
expr_stmt|;
try|try
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|shards
operator|.
name|add
argument_list|(
name|path
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
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Task "
operator|+
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|+
literal|" merging into dstDir: "
operator|+
name|workDir
operator|+
literal|", srcDirs: "
operator|+
name|shards
argument_list|)
expr_stmt|;
name|writeShardNumberFile
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|heartBeater
operator|.
name|needHeartBeat
argument_list|()
expr_stmt|;
try|try
block|{
name|Directory
name|mergedIndex
init|=
operator|new
name|HdfsDirectory
argument_list|(
name|workDir
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO: shouldn't we pull the Version from the solrconfig.xml?
name|IndexWriterConfig
name|writerConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
literal|null
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setUseCompoundFile
argument_list|(
literal|false
argument_list|)
comment|//.setMergePolicy(mergePolicy) // TODO: grab tuned MergePolicy from solrconfig.xml?
comment|//.setMergeScheduler(...) // TODO: grab tuned MergeScheduler from solrconfig.xml?
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|writerConfig
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
comment|//        writerConfig.setRAMBufferSizeMB(100); // improve performance
comment|//        writerConfig.setMaxThreadStates(1);
comment|// disable compound file to improve performance
comment|// also see http://lucene.472066.n3.nabble.com/Questions-on-compound-file-format-td489105.html
comment|// also see defaults in SolrIndexConfig
name|MergePolicy
name|mergePolicy
init|=
name|writerConfig
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"mergePolicy was: {}"
argument_list|,
name|mergePolicy
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergePolicy
operator|instanceof
name|TieredMergePolicy
condition|)
block|{
operator|(
operator|(
name|TieredMergePolicy
operator|)
name|mergePolicy
operator|)
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
comment|//          ((TieredMergePolicy) mergePolicy).setMaxMergeAtOnceExplicit(10000);
comment|//          ((TieredMergePolicy) mergePolicy).setMaxMergeAtOnce(10000);
comment|//          ((TieredMergePolicy) mergePolicy).setSegmentsPerTier(10000);
block|}
elseif|else
if|if
condition|(
name|mergePolicy
operator|instanceof
name|LogMergePolicy
condition|)
block|{
operator|(
operator|(
name|LogMergePolicy
operator|)
name|mergePolicy
operator|)
operator|.
name|setNoCFSRatio
argument_list|(
literal|0.0
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Using mergePolicy: {}"
argument_list|,
name|mergePolicy
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|mergedIndex
argument_list|,
name|writerConfig
argument_list|)
decl_stmt|;
name|Directory
index|[]
name|indexes
init|=
operator|new
name|Directory
index|[
name|shards
operator|.
name|size
argument_list|()
index|]
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
name|shards
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|indexes
index|[
name|i
index|]
operator|=
operator|new
name|HdfsDirectory
argument_list|(
name|shards
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|,
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|setStatus
argument_list|(
literal|"Logically merging "
operator|+
name|shards
operator|.
name|size
argument_list|()
operator|+
literal|" shards into one shard"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Logically merging "
operator|+
name|shards
operator|.
name|size
argument_list|()
operator|+
literal|" shards into one shard: "
operator|+
name|workDir
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
name|writer
operator|.
name|addIndexes
argument_list|(
name|indexes
argument_list|)
expr_stmt|;
comment|// TODO: avoid intermediate copying of files into dst directory; rename the files into the dir instead (cp -> rename)
comment|// This can improve performance and turns this phase into a true "logical" merge, completing in constant time.
comment|// See https://issues.apache.org/jira/browse/LUCENE-4746
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
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
name|LOGICAL_TREE_MERGE_TIME
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
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
literal|"Logical merge took {} secs"
argument_list|,
name|secs
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
name|TreeMergeMapper
operator|.
name|MAX_SEGMENTS_ON_TREE_MERGE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
literal|"Optimizing Solr: forcing mtree merge down to "
operator|+
name|maxSegments
operator|+
literal|" segments"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Optimizing Solr: forcing tree merge down to {} segments"
argument_list|,
name|maxSegments
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
if|if
condition|(
name|maxSegments
operator|<
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|writer
operator|.
name|forceMerge
argument_list|(
name|maxSegments
argument_list|)
expr_stmt|;
comment|// TODO: consider perf enhancement for no-deletes merges: bulk-copy the postings data
comment|// see http://lucene.472066.n3.nabble.com/Experience-with-large-merge-factors-tp1637832p1647046.html
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
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
name|PHYSICAL_TREE_MERGE_TIME
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|increment
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
name|secs
operator|=
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
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Optimizing Solr: done forcing tree merge down to {} segments in {} secs"
argument_list|,
name|maxSegments
argument_list|,
name|secs
argument_list|)
expr_stmt|;
name|start
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Optimizing Solr: Closing index writer"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|secs
operator|=
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
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Optimizing Solr: Done closing index writer in {} secs"
argument_list|,
name|secs
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStatus
argument_list|(
literal|"Done"
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
name|heartBeater
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*      * For background see MapReduceIndexerTool.renameTreeMergeShardDirs()      *       * Also see MapReduceIndexerTool.run() method where it uses      * NLineInputFormat.setNumLinesPerSplit(job, options.fanout)      */
DECL|method|writeShardNumberFile
specifier|private
name|void
name|writeShardNumberFile
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|shards
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|shard
init|=
name|shards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// move up from "data/index"
name|String
name|taskId
init|=
name|shard
operator|.
name|substring
argument_list|(
literal|"part-m-"
operator|.
name|length
argument_list|()
argument_list|,
name|shard
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
comment|// e.g. part-m-00001
name|int
name|taskNum
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|taskId
argument_list|)
decl_stmt|;
name|int
name|outputShardNum
init|=
name|taskNum
operator|/
name|shards
operator|.
name|size
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Merging into outputShardNum: "
operator|+
name|outputShardNum
operator|+
literal|" from taskId: "
operator|+
name|taskId
argument_list|)
expr_stmt|;
name|Path
name|shardNumberFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|,
name|TreeMergeMapper
operator|.
name|SOLR_SHARD_NUMBER
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
name|shardNumberFile
operator|.
name|getFileSystem
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|create
argument_list|(
name|shardNumberFile
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|outputShardNum
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

