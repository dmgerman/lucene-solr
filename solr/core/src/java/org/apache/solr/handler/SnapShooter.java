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
name|io
operator|.
name|IOException
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|function
operator|.
name|Consumer
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
name|DirectoryFactory
operator|.
name|DirContext
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
name|IndexDeletionPolicyWrapper
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
name|backup
operator|.
name|repository
operator|.
name|BackupRepository
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
name|backup
operator|.
name|repository
operator|.
name|BackupRepository
operator|.
name|PathType
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
name|backup
operator|.
name|repository
operator|.
name|LocalFileSystemRepository
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
name|search
operator|.
name|SolrIndexSearcher
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
comment|/**  *<p> Provides functionality equivalent to the snapshooter script</p>  * This is no longer used in standard replication.  *  *  * @since solr 1.4  */
end_comment

begin_class
DECL|class|SnapShooter
specifier|public
class|class
name|SnapShooter
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
DECL|field|solrCore
specifier|private
name|SolrCore
name|solrCore
decl_stmt|;
DECL|field|snapshotName
specifier|private
name|String
name|snapshotName
init|=
literal|null
decl_stmt|;
DECL|field|directoryName
specifier|private
name|String
name|directoryName
init|=
literal|null
decl_stmt|;
DECL|field|baseSnapDirPath
specifier|private
name|URI
name|baseSnapDirPath
init|=
literal|null
decl_stmt|;
DECL|field|snapshotDirPath
specifier|private
name|URI
name|snapshotDirPath
init|=
literal|null
decl_stmt|;
DECL|field|backupRepo
specifier|private
name|BackupRepository
name|backupRepo
init|=
literal|null
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|SnapShooter
specifier|public
name|SnapShooter
parameter_list|(
name|SolrCore
name|core
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|snapshotName
parameter_list|)
block|{
name|String
name|snapDirStr
init|=
literal|null
decl_stmt|;
comment|// Note - This logic is only applicable to the usecase where a shared file-system is exposed via
comment|// local file-system interface (primarily for backwards compatibility). For other use-cases, users
comment|// will be required to specify "location" where the backup should be stored.
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|snapDirStr
operator|=
name|core
operator|.
name|getDataDir
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|snapDirStr
operator|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getInstanceDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|location
argument_list|)
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|initialize
argument_list|(
operator|new
name|LocalFileSystemRepository
argument_list|()
argument_list|,
name|core
argument_list|,
name|snapDirStr
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
block|}
DECL|method|SnapShooter
specifier|public
name|SnapShooter
parameter_list|(
name|BackupRepository
name|backupRepo
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|snapshotName
parameter_list|)
block|{
name|initialize
argument_list|(
name|backupRepo
argument_list|,
name|core
argument_list|,
name|location
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|private
name|void
name|initialize
parameter_list|(
name|BackupRepository
name|backupRepo
parameter_list|,
name|SolrCore
name|core
parameter_list|,
name|String
name|location
parameter_list|,
name|String
name|snapshotName
parameter_list|)
block|{
name|this
operator|.
name|solrCore
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|this
operator|.
name|backupRepo
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|backupRepo
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseSnapDirPath
operator|=
name|backupRepo
operator|.
name|createURI
argument_list|(
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|location
argument_list|)
argument_list|)
operator|.
name|normalize
argument_list|()
expr_stmt|;
name|this
operator|.
name|snapshotName
operator|=
name|snapshotName
expr_stmt|;
if|if
condition|(
name|snapshotName
operator|!=
literal|null
condition|)
block|{
name|directoryName
operator|=
literal|"snapshot."
operator|+
name|snapshotName
expr_stmt|;
block|}
else|else
block|{
name|SimpleDateFormat
name|fmt
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DATE_FMT
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|directoryName
operator|=
literal|"snapshot."
operator|+
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|snapshotDirPath
operator|=
name|backupRepo
operator|.
name|createURI
argument_list|(
name|location
argument_list|,
name|directoryName
argument_list|)
expr_stmt|;
block|}
DECL|method|getBackupRepository
specifier|public
name|BackupRepository
name|getBackupRepository
parameter_list|()
block|{
return|return
name|backupRepo
return|;
block|}
comment|/**    * Gets the parent directory of the snapshots. This is the {@code location}    * given in the constructor.    */
DECL|method|getLocation
specifier|public
name|URI
name|getLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|baseSnapDirPath
return|;
block|}
DECL|method|validateDeleteSnapshot
specifier|public
name|void
name|validateDeleteSnapshot
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|this
operator|.
name|snapshotName
argument_list|)
expr_stmt|;
name|boolean
name|dirFound
init|=
literal|false
decl_stmt|;
name|String
index|[]
name|paths
decl_stmt|;
try|try
block|{
name|paths
operator|=
name|backupRepo
operator|.
name|listAll
argument_list|(
name|baseSnapDirPath
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|this
operator|.
name|directoryName
argument_list|)
operator|&&
name|backupRepo
operator|.
name|getPathType
argument_list|(
name|baseSnapDirPath
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
argument_list|)
operator|==
name|PathType
operator|.
name|DIRECTORY
condition|)
block|{
name|dirFound
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|dirFound
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Snapshot "
operator|+
name|snapshotName
operator|+
literal|" cannot be found in directory: "
operator|+
name|baseSnapDirPath
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
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
literal|"Unable to find snapshot "
operator|+
name|snapshotName
operator|+
literal|" in directory: "
operator|+
name|baseSnapDirPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|deleteSnapAsync
specifier|protected
name|void
name|deleteSnapAsync
parameter_list|(
specifier|final
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
name|deleteNamedSnapshot
argument_list|(
name|replicationHandler
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|validateCreateSnapshot
specifier|public
name|void
name|validateCreateSnapshot
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Note - Removed the current behavior of creating the directory hierarchy.
comment|// Do we really need to provide this support?
if|if
condition|(
operator|!
name|backupRepo
operator|.
name|exists
argument_list|(
name|baseSnapDirPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|" Directory does not exist: "
operator|+
name|snapshotDirPath
argument_list|)
throw|;
block|}
if|if
condition|(
name|backupRepo
operator|.
name|exists
argument_list|(
name|snapshotDirPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"Snapshot directory already exists: "
operator|+
name|snapshotDirPath
argument_list|)
throw|;
block|}
block|}
DECL|method|createSnapshot
specifier|public
name|NamedList
name|createSnapshot
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDeletionPolicyWrapper
name|deletionPolicy
init|=
name|solrCore
operator|.
name|getDeletionPolicy
argument_list|()
decl_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|solrCore
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
try|try
block|{
comment|//TODO should we try solrCore.getDeletionPolicy().getLatestCommit() first?
name|IndexCommit
name|indexCommit
init|=
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getIndexCommit
argument_list|()
decl_stmt|;
name|deletionPolicy
operator|.
name|saveCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|createSnapshot
argument_list|(
name|indexCommit
argument_list|)
return|;
block|}
finally|finally
block|{
name|deletionPolicy
operator|.
name|releaseCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createSnapAsync
specifier|public
name|void
name|createSnapAsync
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|,
specifier|final
name|int
name|numberToKeep
parameter_list|,
name|Consumer
argument_list|<
name|NamedList
argument_list|>
name|result
parameter_list|)
block|{
name|solrCore
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|saveCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO should use Solr's ExecutorUtil
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|result
operator|.
name|accept
argument_list|(
name|createSnapshot
argument_list|(
name|indexCommit
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while creating snapshot"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|NamedList
name|snapShootDetails
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|snapShootDetails
operator|.
name|add
argument_list|(
literal|"snapShootException"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|accept
argument_list|(
name|snapShootDetails
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|solrCore
operator|.
name|getDeletionPolicy
argument_list|()
operator|.
name|releaseCommitPoint
argument_list|(
name|indexCommit
operator|.
name|getGeneration
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|snapshotName
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|deleteOldBackups
argument_list|(
name|numberToKeep
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete old snapshots "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// note: remember to reserve the indexCommit first so it won't get deleted concurrently
DECL|method|createSnapshot
specifier|protected
name|NamedList
name|createSnapshot
parameter_list|(
specifier|final
name|IndexCommit
name|indexCommit
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating backup snapshot "
operator|+
operator|(
name|snapshotName
operator|==
literal|null
condition|?
literal|"<not named>"
else|:
name|snapshotName
operator|)
operator|+
literal|" at "
operator|+
name|baseSnapDirPath
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|details
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"startTime"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//bad; should be Instant.now().toString()
name|Collection
argument_list|<
name|String
argument_list|>
name|files
init|=
name|indexCommit
operator|.
name|getFileNames
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
name|solrCore
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|get
argument_list|(
name|solrCore
operator|.
name|getIndexDir
argument_list|()
argument_list|,
name|DirContext
operator|.
name|DEFAULT
argument_list|,
name|solrCore
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|indexConfig
operator|.
name|lockType
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|String
name|fileName
range|:
name|files
control|)
block|{
name|backupRepo
operator|.
name|copyFileFrom
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|snapshotDirPath
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|solrCore
operator|.
name|getDirectoryFactory
argument_list|()
operator|.
name|release
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|details
operator|.
name|add
argument_list|(
literal|"fileCount"
argument_list|,
name|files
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapshotCompletedAt"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//bad; should be Instant.now().toString()
name|details
operator|.
name|add
argument_list|(
literal|"snapshotName"
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Done creating backup snapshot: "
operator|+
operator|(
name|snapshotName
operator|==
literal|null
condition|?
literal|"<not named>"
else|:
name|snapshotName
operator|)
operator|+
literal|" at "
operator|+
name|baseSnapDirPath
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|details
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|backupRepo
operator|.
name|deleteDirectory
argument_list|(
name|snapshotDirPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteOldBackups
specifier|private
name|void
name|deleteOldBackups
parameter_list|(
name|int
name|numberToKeep
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|paths
init|=
name|backupRepo
operator|.
name|listAll
argument_list|(
name|baseSnapDirPath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|OldBackupDirectory
argument_list|>
name|dirs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|paths
control|)
block|{
if|if
condition|(
name|backupRepo
operator|.
name|getPathType
argument_list|(
name|baseSnapDirPath
operator|.
name|resolve
argument_list|(
name|f
argument_list|)
argument_list|)
operator|==
name|PathType
operator|.
name|DIRECTORY
condition|)
block|{
name|OldBackupDirectory
name|obd
init|=
operator|new
name|OldBackupDirectory
argument_list|(
name|baseSnapDirPath
argument_list|,
name|f
argument_list|)
decl_stmt|;
if|if
condition|(
name|obd
operator|.
name|getTimestamp
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|dirs
operator|.
name|add
argument_list|(
name|obd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|numberToKeep
operator|>
name|dirs
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|OldBackupDirectory
name|dir
range|:
name|dirs
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
name|numberToKeep
condition|)
block|{
name|backupRepo
operator|.
name|deleteDirectory
argument_list|(
name|dir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|deleteNamedSnapshot
specifier|protected
name|void
name|deleteNamedSnapshot
parameter_list|(
name|ReplicationHandler
name|replicationHandler
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting snapshot: "
operator|+
name|snapshotName
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|details
init|=
operator|new
name|NamedList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|URI
name|path
init|=
name|baseSnapDirPath
operator|.
name|resolve
argument_list|(
literal|"snapshot."
operator|+
name|snapshotName
argument_list|)
decl_stmt|;
name|backupRepo
operator|.
name|deleteDirectory
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"success"
argument_list|)
expr_stmt|;
name|details
operator|.
name|add
argument_list|(
literal|"snapshotDeletedAt"
argument_list|,
operator|new
name|Date
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|details
operator|.
name|add
argument_list|(
literal|"status"
argument_list|,
literal|"Unable to delete snapshot: "
operator|+
name|snapshotName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete snapshot: "
operator|+
name|snapshotName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|replicationHandler
operator|.
name|snapShootDetails
operator|=
name|details
expr_stmt|;
block|}
DECL|field|DATE_FMT
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FMT
init|=
literal|"yyyyMMddHHmmssSSS"
decl_stmt|;
block|}
end_class

end_unit

