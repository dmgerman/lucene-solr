begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|codecs
operator|.
name|Codec
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
name|CorruptIndexException
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
name|lucene
operator|.
name|index
operator|.
name|IndexDeletionPolicy
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
name|index
operator|.
name|MergeScheduler
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
name|ConcurrentMergeScheduler
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
name|NoDeletionPolicy
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
name|NoMergePolicy
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
name|NoMergeScheduler
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
name|store
operator|.
name|LockObtainFailedException
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
name|Version
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_comment
comment|/**  * Create an index.<br>  * Other side effects: index writer object in perfRunData is set.<br>  * Relevant properties:<code>merge.factor (default 10),  * max.buffered (default no flush), compound (default true), ram.flush.mb [default 0],  * merge.policy (default org.apache.lucene.index.LogByteSizeMergePolicy),  * merge.scheduler (default  * org.apache.lucene.index.ConcurrentMergeScheduler),  * concurrent.merge.scheduler.max.thread.count and  * concurrent.merge.scheduler.max.merge.count (defaults per  * ConcurrentMergeScheduler), default.codec</code>.  *<p>  * This task also supports a "writer.info.stream" property with the following  * values:  *<ul>  *<li>SystemOut - sets {@link IndexWriterConfig#setInfoStream(java.io.PrintStream)}  * to {@link System#out}.  *<li>SystemErr - sets {@link IndexWriterConfig#setInfoStream(java.io.PrintStream)}  * to {@link System#err}.  *<li>&lt;file_name&gt; - attempts to create a file given that name and sets  * {@link IndexWriterConfig#setInfoStream(java.io.PrintStream)} to that file. If this  * denotes an invalid file name, or some error occurs, an exception will be  * thrown.  *</ul>  */
end_comment

begin_class
DECL|class|CreateIndexTask
specifier|public
class|class
name|CreateIndexTask
extends|extends
name|PerfTask
block|{
DECL|method|CreateIndexTask
specifier|public
name|CreateIndexTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndexDeletionPolicy
specifier|public
specifier|static
name|IndexDeletionPolicy
name|getIndexDeletionPolicy
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|String
name|deletionPolicyName
init|=
name|config
operator|.
name|get
argument_list|(
literal|"deletion.policy"
argument_list|,
literal|"org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy"
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletionPolicyName
operator|.
name|equals
argument_list|(
name|NoDeletionPolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|NoDeletionPolicy
operator|.
name|INSTANCE
return|;
block|}
else|else
block|{
try|try
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|deletionPolicyName
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|IndexDeletionPolicy
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|deletionPolicyName
operator|+
literal|"' as IndexDeletionPolicy"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|IOException
block|{
name|PerfRunData
name|runData
init|=
name|getRunData
argument_list|()
decl_stmt|;
name|Config
name|config
init|=
name|runData
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|runData
operator|.
name|setIndexWriter
argument_list|(
name|configureWriter
argument_list|(
name|config
argument_list|,
name|runData
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
DECL|method|createWriterConfig
specifier|public
specifier|static
name|IndexWriterConfig
name|createWriterConfig
parameter_list|(
name|Config
name|config
parameter_list|,
name|PerfRunData
name|runData
parameter_list|,
name|OpenMode
name|mode
parameter_list|,
name|IndexCommit
name|commit
parameter_list|)
block|{
name|Version
name|version
init|=
name|Version
operator|.
name|valueOf
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"writer.version"
argument_list|,
name|Version
operator|.
name|LUCENE_40
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|iwConf
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|version
argument_list|,
name|runData
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|iwConf
operator|.
name|setOpenMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
name|IndexDeletionPolicy
name|indexDeletionPolicy
init|=
name|getIndexDeletionPolicy
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|iwConf
operator|.
name|setIndexDeletionPolicy
argument_list|(
name|indexDeletionPolicy
argument_list|)
expr_stmt|;
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
name|iwConf
operator|.
name|setIndexCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
specifier|final
name|String
name|mergeScheduler
init|=
name|config
operator|.
name|get
argument_list|(
literal|"merge.scheduler"
argument_list|,
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergeScheduler
operator|.
name|equals
argument_list|(
name|NoMergeScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|iwConf
operator|.
name|setMergeScheduler
argument_list|(
name|NoMergeScheduler
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|iwConf
operator|.
name|setMergeScheduler
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|mergeScheduler
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|MergeScheduler
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
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
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|mergeScheduler
operator|+
literal|"' as merge scheduler"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|mergeScheduler
operator|.
name|equals
argument_list|(
literal|"org.apache.lucene.index.ConcurrentMergeScheduler"
argument_list|)
condition|)
block|{
name|ConcurrentMergeScheduler
name|cms
init|=
operator|(
name|ConcurrentMergeScheduler
operator|)
name|iwConf
operator|.
name|getMergeScheduler
argument_list|()
decl_stmt|;
name|int
name|v
init|=
name|config
operator|.
name|get
argument_list|(
literal|"concurrent.merge.scheduler.max.thread.count"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
operator|-
literal|1
condition|)
block|{
name|cms
operator|.
name|setMaxThreadCount
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|v
operator|=
name|config
operator|.
name|get
argument_list|(
literal|"concurrent.merge.scheduler.max.merge.count"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|!=
operator|-
literal|1
condition|)
block|{
name|cms
operator|.
name|setMaxMergeCount
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|String
name|defaultCodec
init|=
name|config
operator|.
name|get
argument_list|(
literal|"default.codec"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultCodec
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Codec
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|defaultCodec
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|Codec
operator|.
name|class
argument_list|)
decl_stmt|;
name|Codec
operator|.
name|setDefault
argument_list|(
name|clazz
operator|.
name|newInstance
argument_list|()
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
name|RuntimeException
argument_list|(
literal|"Couldn't instantiate Codec: "
operator|+
name|defaultCodec
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|final
name|String
name|mergePolicy
init|=
name|config
operator|.
name|get
argument_list|(
literal|"merge.policy"
argument_list|,
literal|"org.apache.lucene.index.LogByteSizeMergePolicy"
argument_list|)
decl_stmt|;
name|boolean
name|isCompound
init|=
name|config
operator|.
name|get
argument_list|(
literal|"compound"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|mergePolicy
operator|.
name|equals
argument_list|(
name|NoMergePolicy
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|iwConf
operator|.
name|setMergePolicy
argument_list|(
name|isCompound
condition|?
name|NoMergePolicy
operator|.
name|COMPOUND_FILES
else|:
name|NoMergePolicy
operator|.
name|NO_COMPOUND_FILES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|iwConf
operator|.
name|setMergePolicy
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
name|mergePolicy
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|MergePolicy
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|()
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
name|RuntimeException
argument_list|(
literal|"unable to instantiate class '"
operator|+
name|mergePolicy
operator|+
literal|"' as merge policy"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|iwConf
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|LogMergePolicy
condition|)
block|{
name|LogMergePolicy
name|logMergePolicy
init|=
operator|(
name|LogMergePolicy
operator|)
name|iwConf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|logMergePolicy
operator|.
name|setUseCompoundFile
argument_list|(
name|isCompound
argument_list|)
expr_stmt|;
name|logMergePolicy
operator|.
name|setMergeFactor
argument_list|(
name|config
operator|.
name|get
argument_list|(
literal|"merge.factor"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_MERGE_PFACTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|iwConf
operator|.
name|getMergePolicy
argument_list|()
operator|instanceof
name|TieredMergePolicy
condition|)
block|{
name|TieredMergePolicy
name|tieredMergePolicy
init|=
operator|(
name|TieredMergePolicy
operator|)
name|iwConf
operator|.
name|getMergePolicy
argument_list|()
decl_stmt|;
name|tieredMergePolicy
operator|.
name|setUseCompoundFile
argument_list|(
name|isCompound
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|double
name|ramBuffer
init|=
name|config
operator|.
name|get
argument_list|(
literal|"ram.flush.mb"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_RAM_FLUSH_MB
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxBuffered
init|=
name|config
operator|.
name|get
argument_list|(
literal|"max.buffered"
argument_list|,
name|OpenIndexTask
operator|.
name|DEFAULT_MAX_BUFFERED
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxBuffered
operator|==
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
condition|)
block|{
name|iwConf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBuffer
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBuffered
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|iwConf
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBuffered
argument_list|)
expr_stmt|;
name|iwConf
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|ramBuffer
argument_list|)
expr_stmt|;
block|}
return|return
name|iwConf
return|;
block|}
DECL|method|configureWriter
specifier|public
specifier|static
name|IndexWriter
name|configureWriter
parameter_list|(
name|Config
name|config
parameter_list|,
name|PerfRunData
name|runData
parameter_list|,
name|OpenMode
name|mode
parameter_list|,
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
name|IndexWriterConfig
name|iwc
init|=
name|createWriterConfig
argument_list|(
name|config
argument_list|,
name|runData
argument_list|,
name|mode
argument_list|,
name|commit
argument_list|)
decl_stmt|;
name|String
name|infoStreamVal
init|=
name|config
operator|.
name|get
argument_list|(
literal|"writer.info.stream"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|infoStreamVal
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|infoStreamVal
operator|.
name|equals
argument_list|(
literal|"SystemOut"
argument_list|)
condition|)
block|{
name|iwc
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|infoStreamVal
operator|.
name|equals
argument_list|(
literal|"SystemErr"
argument_list|)
condition|)
block|{
name|iwc
operator|.
name|setInfoStream
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|infoStreamVal
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
name|iwc
operator|.
name|setInfoStream
argument_list|(
operator|new
name|PrintStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|runData
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
return|return
name|writer
return|;
block|}
block|}
end_class

end_unit

