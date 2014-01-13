begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|solr
operator|.
name|cloud
operator|.
name|RecoveryStrategy
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
name|DirectoryFactory
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
name|util
operator|.
name|RefCounted
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
DECL|class|DefaultSolrCoreState
specifier|public
specifier|final
class|class
name|DefaultSolrCoreState
extends|extends
name|SolrCoreState
implements|implements
name|RecoveryStrategy
operator|.
name|RecoveryListener
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
name|DefaultSolrCoreState
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SKIP_AUTO_RECOVERY
specifier|private
specifier|final
name|boolean
name|SKIP_AUTO_RECOVERY
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"solrcloud.skip.autorecovery"
argument_list|)
decl_stmt|;
DECL|field|recoveryLock
specifier|private
specifier|final
name|Object
name|recoveryLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// protects pauseWriter and writerFree
DECL|field|writerPauseLock
specifier|private
specifier|final
name|Object
name|writerPauseLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|SolrIndexWriter
name|indexWriter
init|=
literal|null
decl_stmt|;
DECL|field|directoryFactory
specifier|private
name|DirectoryFactory
name|directoryFactory
decl_stmt|;
DECL|field|recoveryRunning
specifier|private
specifier|volatile
name|boolean
name|recoveryRunning
decl_stmt|;
DECL|field|recoveryStrat
specifier|private
name|RecoveryStrategy
name|recoveryStrat
decl_stmt|;
DECL|field|refCntWriter
specifier|private
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|refCntWriter
decl_stmt|;
DECL|field|pauseWriter
specifier|private
name|boolean
name|pauseWriter
decl_stmt|;
DECL|field|writerFree
specifier|private
name|boolean
name|writerFree
init|=
literal|true
decl_stmt|;
DECL|field|commitLock
specifier|protected
specifier|final
name|ReentrantLock
name|commitLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|method|DefaultSolrCoreState
specifier|public
name|DefaultSolrCoreState
parameter_list|(
name|DirectoryFactory
name|directoryFactory
parameter_list|)
block|{
name|this
operator|.
name|directoryFactory
operator|=
name|directoryFactory
expr_stmt|;
block|}
DECL|method|closeIndexWriter
specifier|private
name|void
name|closeIndexWriter
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"SolrCoreState ref count has reached 0 - closing IndexWriter"
argument_list|)
expr_stmt|;
if|if
condition|(
name|closer
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"closing IndexWriter with IndexWriterCloser"
argument_list|)
expr_stmt|;
name|closer
operator|.
name|closeWriter
argument_list|(
name|indexWriter
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"closing IndexWriter..."
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|indexWriter
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during shutdown of writer."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getIndexWriter
specifier|public
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
name|getIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|writerPauseLock
init|)
block|{
if|if
condition|(
name|closed
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
literal|"SolrCoreState already closed"
argument_list|)
throw|;
block|}
while|while
condition|(
name|pauseWriter
condition|)
block|{
try|try
block|{
name|writerPauseLock
operator|.
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
if|if
condition|(
name|closed
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
literal|"Already closed"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|core
operator|==
literal|null
condition|)
block|{
comment|// core == null is a signal to just return the current writer, or null
comment|// if none.
name|initRefCntWriter
argument_list|()
expr_stmt|;
if|if
condition|(
name|refCntWriter
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|writerFree
operator|=
literal|false
expr_stmt|;
name|writerPauseLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|refCntWriter
operator|!=
literal|null
condition|)
name|refCntWriter
operator|.
name|incref
argument_list|()
expr_stmt|;
return|return
name|refCntWriter
return|;
block|}
if|if
condition|(
name|indexWriter
operator|==
literal|null
condition|)
block|{
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
block|}
name|initRefCntWriter
argument_list|()
expr_stmt|;
name|writerFree
operator|=
literal|false
expr_stmt|;
name|writerPauseLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
name|refCntWriter
operator|.
name|incref
argument_list|()
expr_stmt|;
return|return
name|refCntWriter
return|;
block|}
block|}
DECL|method|initRefCntWriter
specifier|private
name|void
name|initRefCntWriter
parameter_list|()
block|{
if|if
condition|(
name|refCntWriter
operator|==
literal|null
operator|&&
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|refCntWriter
operator|=
operator|new
name|RefCounted
argument_list|<
name|IndexWriter
argument_list|>
argument_list|(
name|indexWriter
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
synchronized|synchronized
init|(
name|writerPauseLock
init|)
block|{
name|writerFree
operator|=
literal|true
expr_stmt|;
name|writerPauseLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newIndexWriter
specifier|public
specifier|synchronized
name|void
name|newIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|rollback
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating new IndexWriter..."
argument_list|)
expr_stmt|;
name|String
name|coreName
init|=
name|core
operator|.
name|getName
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|writerPauseLock
init|)
block|{
if|if
condition|(
name|closed
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
literal|"Already closed"
argument_list|)
throw|;
block|}
comment|// we need to wait for the Writer to fall out of use
comment|// first lets stop it from being lent out
name|pauseWriter
operator|=
literal|true
expr_stmt|;
comment|// then lets wait until its out of use
name|log
operator|.
name|info
argument_list|(
literal|"Waiting until IndexWriter is unused... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|writerFree
condition|)
block|{
try|try
block|{
name|writerPauseLock
operator|.
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
if|if
condition|(
name|closed
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
literal|"SolrCoreState already closed"
argument_list|)
throw|;
block|}
block|}
try|try
block|{
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|rollback
condition|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Closing old IndexWriter... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Error closing old IndexWriter. core="
operator|+
name|coreName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Rollback old IndexWriter... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|rollback
argument_list|()
expr_stmt|;
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
literal|"Error rolling back old IndexWriter. core="
operator|+
name|coreName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"New IndexWriter is ready to be used."
argument_list|)
expr_stmt|;
comment|// we need to null this so it picks up the new writer next get call
name|refCntWriter
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|pauseWriter
operator|=
literal|false
expr_stmt|;
name|writerPauseLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|closeIndexWriter
specifier|public
specifier|synchronized
name|void
name|closeIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|boolean
name|rollback
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Closing IndexWriter..."
argument_list|)
expr_stmt|;
name|String
name|coreName
init|=
name|core
operator|.
name|getName
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|writerPauseLock
init|)
block|{
if|if
condition|(
name|closed
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
literal|"Already closed"
argument_list|)
throw|;
block|}
comment|// we need to wait for the Writer to fall out of use
comment|// first lets stop it from being lent out
name|pauseWriter
operator|=
literal|true
expr_stmt|;
comment|// then lets wait until its out of use
name|log
operator|.
name|info
argument_list|(
literal|"Waiting until IndexWriter is unused... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|writerFree
condition|)
block|{
try|try
block|{
name|writerPauseLock
operator|.
name|wait
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
if|if
condition|(
name|closed
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
literal|"SolrCoreState already closed"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|rollback
condition|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Closing old IndexWriter... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"Error closing old IndexWriter. core="
operator|+
name|coreName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Rollback old IndexWriter... core="
operator|+
name|coreName
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|rollback
argument_list|()
expr_stmt|;
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
literal|"Error rolling back old IndexWriter. core="
operator|+
name|coreName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|openIndexWriter
specifier|public
specifier|synchronized
name|void
name|openIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating new IndexWriter..."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|writerPauseLock
init|)
block|{
if|if
condition|(
name|closed
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
literal|"Already closed"
argument_list|)
throw|;
block|}
try|try
block|{
name|indexWriter
operator|=
name|createMainIndexWriter
argument_list|(
name|core
argument_list|,
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"New IndexWriter is ready to be used."
argument_list|)
expr_stmt|;
comment|// we need to null this so it picks up the new writer next get call
name|refCntWriter
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|pauseWriter
operator|=
literal|false
expr_stmt|;
name|writerPauseLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|rollbackIndexWriter
specifier|public
specifier|synchronized
name|void
name|rollbackIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|newIndexWriter
argument_list|(
name|core
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createMainIndexWriter
specifier|protected
name|SolrIndexWriter
name|createMainIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SolrIndexWriter
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|core
operator|.
name|getNewIndexDir
argument_list|()
argument_list|,
name|core
operator|.
name|getDirectoryFactory
argument_list|()
argument_list|,
literal|false
argument_list|,
name|core
operator|.
name|getLatestSchema
argument_list|()
argument_list|,
name|core
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
argument_list|,
name|core
operator|.
name|getDeletionPolicy
argument_list|()
argument_list|,
name|core
operator|.
name|getCodec
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectoryFactory
specifier|public
name|DirectoryFactory
name|getDirectoryFactory
parameter_list|()
block|{
return|return
name|directoryFactory
return|;
block|}
annotation|@
name|Override
DECL|method|doRecovery
specifier|public
name|void
name|doRecovery
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd
parameter_list|)
block|{
if|if
condition|(
name|SKIP_AUTO_RECOVERY
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping recovery according to sys prop solrcloud.skip.autorecovery"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// check before we grab the lock
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping recovery because Solr is shutdown"
argument_list|)
expr_stmt|;
return|return;
block|}
synchronized|synchronized
init|(
name|recoveryLock
init|)
block|{
comment|// to be air tight we must also check after lock
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping recovery because Solr is shutdown"
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Running recovery - first canceling any ongoing recovery"
argument_list|)
expr_stmt|;
name|cancelRecovery
argument_list|()
expr_stmt|;
while|while
condition|(
name|recoveryRunning
condition|)
block|{
try|try
block|{
name|recoveryLock
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{          }
comment|// check again for those that were waiting
if|if
condition|(
name|cc
operator|.
name|isShutDown
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Skipping recovery because Solr is shutdown"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|closed
condition|)
return|return;
block|}
comment|// if true, we are recovering after startup and shouldn't have (or be receiving) additional updates (except for local tlog recovery)
name|boolean
name|recoveringAfterStartup
init|=
name|recoveryStrat
operator|==
literal|null
decl_stmt|;
name|recoveryStrat
operator|=
operator|new
name|RecoveryStrategy
argument_list|(
name|cc
argument_list|,
name|cd
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|recoveryStrat
operator|.
name|setRecoveringAfterStartup
argument_list|(
name|recoveringAfterStartup
argument_list|)
expr_stmt|;
name|recoveryStrat
operator|.
name|start
argument_list|()
expr_stmt|;
name|recoveryRunning
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|cancelRecovery
specifier|public
name|void
name|cancelRecovery
parameter_list|()
block|{
synchronized|synchronized
init|(
name|recoveryLock
init|)
block|{
if|if
condition|(
name|recoveryStrat
operator|!=
literal|null
operator|&&
name|recoveryRunning
condition|)
block|{
name|recoveryStrat
operator|.
name|close
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|recoveryStrat
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// not interruptible - keep waiting
continue|continue;
block|}
break|break;
block|}
name|recoveryRunning
operator|=
literal|false
expr_stmt|;
name|recoveryLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|recovered
specifier|public
name|void
name|recovered
parameter_list|()
block|{
name|recoveryRunning
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|failed
specifier|public
name|void
name|failed
parameter_list|()
block|{
name|recoveryRunning
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|IndexWriterCloser
name|closer
parameter_list|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|cancelRecovery
argument_list|()
expr_stmt|;
name|closeIndexWriter
argument_list|(
name|closer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommitLock
specifier|public
name|Lock
name|getCommitLock
parameter_list|()
block|{
return|return
name|commitLock
return|;
block|}
block|}
end_class

end_unit

