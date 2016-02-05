begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.replicator
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|replicator
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
name|Collections
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
name|Map
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
name|Callable
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
name|DirectoryReader
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
name|replicator
operator|.
name|ReplicationClient
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
name|IOContext
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
name|InfoStream
import|;
end_import

begin_comment
comment|/**  * A {@link ReplicationHandler} for replication of an index and taxonomy pair.  * See {@link IndexReplicationHandler} for more detail. This handler ensures  * that the search and taxonomy indexes are replicated in a consistent way.  *<p>  *<b>NOTE:</b> if you intend to recreate a taxonomy index, you should make sure  * to reopen an IndexSearcher and TaxonomyReader pair via the provided callback,  * to guarantee that both indexes are in sync. This handler does not prevent  * replicating such index and taxonomy pairs, and if they are reopened by a  * different thread, unexpected errors can occur, as well as inconsistency  * between the taxonomy and index readers.  *   * @see IndexReplicationHandler  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|IndexAndTaxonomyReplicationHandler
specifier|public
class|class
name|IndexAndTaxonomyReplicationHandler
implements|implements
name|ReplicationHandler
block|{
comment|/**    * The component used to log messages to the {@link InfoStream#getDefault()    * default} {@link InfoStream}.    */
DECL|field|INFO_STREAM_COMPONENT
specifier|public
specifier|static
specifier|final
name|String
name|INFO_STREAM_COMPONENT
init|=
literal|"IndexAndTaxonomyReplicationHandler"
decl_stmt|;
DECL|field|indexDir
specifier|private
specifier|final
name|Directory
name|indexDir
decl_stmt|;
DECL|field|taxoDir
specifier|private
specifier|final
name|Directory
name|taxoDir
decl_stmt|;
DECL|field|callback
specifier|private
specifier|final
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|callback
decl_stmt|;
DECL|field|currentRevisionFiles
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|currentRevisionFiles
decl_stmt|;
DECL|field|currentVersion
specifier|private
specifier|volatile
name|String
name|currentVersion
decl_stmt|;
DECL|field|infoStream
specifier|private
specifier|volatile
name|InfoStream
name|infoStream
init|=
name|InfoStream
operator|.
name|getDefault
argument_list|()
decl_stmt|;
comment|/**    * Constructor with the given index directory and callback to notify when the    * indexes were updated.    */
DECL|method|IndexAndTaxonomyReplicationHandler
specifier|public
name|IndexAndTaxonomyReplicationHandler
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|,
name|Callable
argument_list|<
name|Boolean
argument_list|>
name|callback
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
name|this
operator|.
name|indexDir
operator|=
name|indexDir
expr_stmt|;
name|this
operator|.
name|taxoDir
operator|=
name|taxoDir
expr_stmt|;
name|currentRevisionFiles
operator|=
literal|null
expr_stmt|;
name|currentVersion
operator|=
literal|null
expr_stmt|;
specifier|final
name|boolean
name|indexExists
init|=
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|taxoExists
init|=
name|DirectoryReader
operator|.
name|indexExists
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexExists
operator|!=
name|taxoExists
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"search and taxonomy indexes must either both exist or not: index="
operator|+
name|indexExists
operator|+
literal|" taxo="
operator|+
name|taxoExists
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexExists
condition|)
block|{
comment|// both indexes exist
specifier|final
name|IndexCommit
name|indexCommit
init|=
name|IndexReplicationHandler
operator|.
name|getLastCommit
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
specifier|final
name|IndexCommit
name|taxoCommit
init|=
name|IndexReplicationHandler
operator|.
name|getLastCommit
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|currentRevisionFiles
operator|=
name|IndexAndTaxonomyRevision
operator|.
name|revisionFiles
argument_list|(
name|indexCommit
argument_list|,
name|taxoCommit
argument_list|)
expr_stmt|;
name|currentVersion
operator|=
name|IndexAndTaxonomyRevision
operator|.
name|revisionVersion
argument_list|(
name|indexCommit
argument_list|,
name|taxoCommit
argument_list|)
expr_stmt|;
specifier|final
name|InfoStream
name|infoStream
init|=
name|InfoStream
operator|.
name|getDefault
argument_list|()
decl_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|,
literal|"constructor(): currentVersion="
operator|+
name|currentVersion
operator|+
literal|" currentRevisionFiles="
operator|+
name|currentRevisionFiles
argument_list|)
expr_stmt|;
name|infoStream
operator|.
name|message
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|,
literal|"constructor(): indexCommit="
operator|+
name|indexCommit
operator|+
literal|" taxoCommit="
operator|+
name|taxoCommit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|currentVersion
specifier|public
name|String
name|currentVersion
parameter_list|()
block|{
return|return
name|currentVersion
return|;
block|}
annotation|@
name|Override
DECL|method|currentRevisionFiles
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|currentRevisionFiles
parameter_list|()
block|{
return|return
name|currentRevisionFiles
return|;
block|}
annotation|@
name|Override
DECL|method|revisionReady
specifier|public
name|void
name|revisionReady
parameter_list|(
name|String
name|version
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|RevisionFile
argument_list|>
argument_list|>
name|revisionFiles
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|copiedFiles
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Directory
argument_list|>
name|sourceDirectory
parameter_list|)
throws|throws
name|IOException
block|{
name|Directory
name|taxoClientDir
init|=
name|sourceDirectory
operator|.
name|get
argument_list|(
name|IndexAndTaxonomyRevision
operator|.
name|TAXONOMY_SOURCE
argument_list|)
decl_stmt|;
name|Directory
name|indexClientDir
init|=
name|sourceDirectory
operator|.
name|get
argument_list|(
name|IndexAndTaxonomyRevision
operator|.
name|INDEX_SOURCE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|taxoFiles
init|=
name|copiedFiles
operator|.
name|get
argument_list|(
name|IndexAndTaxonomyRevision
operator|.
name|TAXONOMY_SOURCE
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|indexFiles
init|=
name|copiedFiles
operator|.
name|get
argument_list|(
name|IndexAndTaxonomyRevision
operator|.
name|INDEX_SOURCE
argument_list|)
decl_stmt|;
name|String
name|taxoSegmentsFile
init|=
name|IndexReplicationHandler
operator|.
name|getSegmentsFile
argument_list|(
name|taxoFiles
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|indexSegmentsFile
init|=
name|IndexReplicationHandler
operator|.
name|getSegmentsFile
argument_list|(
name|indexFiles
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
name|taxoPendingFile
init|=
name|taxoSegmentsFile
operator|==
literal|null
condition|?
literal|null
else|:
literal|"pending_"
operator|+
name|taxoSegmentsFile
decl_stmt|;
name|String
name|indexPendingFile
init|=
literal|"pending_"
operator|+
name|indexSegmentsFile
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// copy taxonomy files before index files
name|IndexReplicationHandler
operator|.
name|copyFiles
argument_list|(
name|taxoClientDir
argument_list|,
name|taxoDir
argument_list|,
name|taxoFiles
argument_list|)
expr_stmt|;
name|IndexReplicationHandler
operator|.
name|copyFiles
argument_list|(
name|indexClientDir
argument_list|,
name|indexDir
argument_list|,
name|indexFiles
argument_list|)
expr_stmt|;
comment|// fsync all copied files (except segmentsFile)
if|if
condition|(
operator|!
name|taxoFiles
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|taxoDir
operator|.
name|sync
argument_list|(
name|taxoFiles
argument_list|)
expr_stmt|;
block|}
name|indexDir
operator|.
name|sync
argument_list|(
name|indexFiles
argument_list|)
expr_stmt|;
comment|// now copy, fsync, and rename segmentsFile, taxonomy first because it is ok if a
comment|// reader sees a more advanced taxonomy than the index.
if|if
condition|(
name|taxoSegmentsFile
operator|!=
literal|null
condition|)
block|{
name|taxoDir
operator|.
name|copyFrom
argument_list|(
name|taxoClientDir
argument_list|,
name|taxoSegmentsFile
argument_list|,
name|taxoPendingFile
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
block|}
name|indexDir
operator|.
name|copyFrom
argument_list|(
name|indexClientDir
argument_list|,
name|indexSegmentsFile
argument_list|,
name|indexPendingFile
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
expr_stmt|;
if|if
condition|(
name|taxoSegmentsFile
operator|!=
literal|null
condition|)
block|{
name|taxoDir
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|taxoPendingFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexDir
operator|.
name|sync
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|indexPendingFile
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|taxoSegmentsFile
operator|!=
literal|null
condition|)
block|{
name|taxoDir
operator|.
name|renameFile
argument_list|(
name|taxoPendingFile
argument_list|,
name|taxoSegmentsFile
argument_list|)
expr_stmt|;
block|}
name|indexDir
operator|.
name|renameFile
argument_list|(
name|indexPendingFile
argument_list|,
name|indexSegmentsFile
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
if|if
condition|(
name|taxoSegmentsFile
operator|!=
literal|null
condition|)
block|{
name|taxoFiles
operator|.
name|add
argument_list|(
name|taxoSegmentsFile
argument_list|)
expr_stmt|;
comment|// add it back so it gets deleted too
name|taxoFiles
operator|.
name|add
argument_list|(
name|taxoPendingFile
argument_list|)
expr_stmt|;
block|}
name|IndexReplicationHandler
operator|.
name|cleanupFilesOnFailure
argument_list|(
name|taxoDir
argument_list|,
name|taxoFiles
argument_list|)
expr_stmt|;
name|indexFiles
operator|.
name|add
argument_list|(
name|indexSegmentsFile
argument_list|)
expr_stmt|;
comment|// add it back so it gets deleted too
name|indexFiles
operator|.
name|add
argument_list|(
name|indexPendingFile
argument_list|)
expr_stmt|;
name|IndexReplicationHandler
operator|.
name|cleanupFilesOnFailure
argument_list|(
name|indexDir
argument_list|,
name|indexFiles
argument_list|)
expr_stmt|;
block|}
block|}
comment|// all files have been successfully copied + sync'd. update the handler's state
name|currentRevisionFiles
operator|=
name|revisionFiles
expr_stmt|;
name|currentVersion
operator|=
name|version
expr_stmt|;
if|if
condition|(
name|infoStream
operator|.
name|isEnabled
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|)
condition|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
name|INFO_STREAM_COMPONENT
argument_list|,
literal|"revisionReady(): currentVersion="
operator|+
name|currentVersion
operator|+
literal|" currentRevisionFiles="
operator|+
name|currentRevisionFiles
argument_list|)
expr_stmt|;
block|}
comment|// Cleanup the index directory from old and unused index files.
comment|// NOTE: we don't use IndexWriter.deleteUnusedFiles here since it may have
comment|// side-effects, e.g. if it hits sudden IO errors while opening the index
comment|// (and can end up deleting the entire index). It is not our job to protect
comment|// against those errors, app will probably hit them elsewhere.
name|IndexReplicationHandler
operator|.
name|cleanupOldIndexFiles
argument_list|(
name|indexDir
argument_list|,
name|indexSegmentsFile
argument_list|,
name|infoStream
argument_list|)
expr_stmt|;
name|IndexReplicationHandler
operator|.
name|cleanupOldIndexFiles
argument_list|(
name|taxoDir
argument_list|,
name|taxoSegmentsFile
argument_list|,
name|infoStream
argument_list|)
expr_stmt|;
comment|// successfully updated the index, notify the callback that the index is
comment|// ready.
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|callback
operator|.
name|call
argument_list|()
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
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Sets the {@link InfoStream} to use for logging messages. */
DECL|method|setInfoStream
specifier|public
name|void
name|setInfoStream
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|)
block|{
if|if
condition|(
name|infoStream
operator|==
literal|null
condition|)
block|{
name|infoStream
operator|=
name|InfoStream
operator|.
name|NO_OUTPUT
expr_stmt|;
block|}
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
block|}
end_class

end_unit

