begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Lock
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

begin_comment
comment|/**  * IndexReader implementation that has access to a Directory.   * Instances that have a SegmentInfos object (i. e. segmentInfos != null)  * "own" the directory, which means that they try to acquire a write lock  * whenever index modifications are performed.  */
end_comment

begin_class
DECL|class|DirectoryIndexReader
specifier|abstract
class|class
name|DirectoryIndexReader
extends|extends
name|IndexReader
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|closeDirectory
specifier|private
name|boolean
name|closeDirectory
decl_stmt|;
DECL|field|deletionPolicy
specifier|private
name|IndexDeletionPolicy
name|deletionPolicy
decl_stmt|;
DECL|field|segmentInfos
specifier|private
name|SegmentInfos
name|segmentInfos
decl_stmt|;
DECL|field|writeLock
specifier|private
name|Lock
name|writeLock
decl_stmt|;
DECL|field|stale
specifier|private
name|boolean
name|stale
decl_stmt|;
comment|/** Used by commit() to record pre-commit state in case    * rollback is necessary */
DECL|field|rollbackHasChanges
specifier|private
name|boolean
name|rollbackHasChanges
decl_stmt|;
DECL|field|rollbackSegmentInfos
specifier|private
name|SegmentInfos
name|rollbackSegmentInfos
decl_stmt|;
DECL|method|init
name|void
name|init
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|)
block|{
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segmentInfos
operator|=
name|segmentInfos
expr_stmt|;
name|this
operator|.
name|closeDirectory
operator|=
name|closeDirectory
expr_stmt|;
block|}
DECL|method|DirectoryIndexReader
specifier|protected
name|DirectoryIndexReader
parameter_list|()
block|{}
DECL|method|DirectoryIndexReader
name|DirectoryIndexReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|boolean
name|closeDirectory
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|init
argument_list|(
name|directory
argument_list|,
name|segmentInfos
argument_list|,
name|closeDirectory
argument_list|)
expr_stmt|;
block|}
DECL|method|setDeletionPolicy
specifier|public
name|void
name|setDeletionPolicy
parameter_list|(
name|IndexDeletionPolicy
name|deletionPolicy
parameter_list|)
block|{
name|this
operator|.
name|deletionPolicy
operator|=
name|deletionPolicy
expr_stmt|;
block|}
comment|/** Returns the directory this index resides in.    */
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|directory
return|;
block|}
comment|/**    * Version number when this IndexReader was opened.    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|segmentInfos
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/**    * Check whether this IndexReader is still using the    * current (i.e., most recently committed) version of the    * index.  If a writer has committed any changes to the    * index since this reader was opened, this will return    *<code>false</code>, in which case you must open a new    * IndexReader in order to see the changes.  See the    * description of the<a href="IndexWriter.html#autoCommit"><code>autoCommit</code></a>    * flag which controls when the {@link IndexWriter}    * actually commits changes to the index.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|isCurrent
specifier|public
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
operator|==
name|segmentInfos
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/**    * Checks is the index is optimized (if it has a single segment and no deletions)    * @return<code>true</code> if the index is optimized;<code>false</code> otherwise    */
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|segmentInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|hasDeletions
argument_list|()
operator|==
literal|false
return|;
block|}
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
name|closed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|closeDirectory
condition|)
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Commit changes resulting from delete, undeleteAll, or    * setNorm operations    *    * If an exception is hit, then either no changes or all    * changes will have been committed to the index    * (transactional semantics).    * @throws IOException if there is a low-level IO error    */
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasChanges
condition|)
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
comment|// Default deleter (for backwards compatibility) is
comment|// KeepOnlyLastCommitDeleter:
name|IndexFileDeleter
name|deleter
init|=
operator|new
name|IndexFileDeleter
argument_list|(
name|directory
argument_list|,
name|deletionPolicy
operator|==
literal|null
condition|?
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
else|:
name|deletionPolicy
argument_list|,
name|segmentInfos
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Checkpoint the state we are about to change, in
comment|// case we have to roll back:
name|startCommit
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|commitChanges
argument_list|()
expr_stmt|;
name|segmentInfos
operator|.
name|write
argument_list|(
name|directory
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
comment|// Rollback changes that were made to
comment|// SegmentInfos but failed to get [fully]
comment|// committed.  This way this reader instance
comment|// remains consistent (matched to what's
comment|// actually in the index):
name|rollbackCommit
argument_list|()
expr_stmt|;
comment|// Recompute deletable files& remove them (so
comment|// partially written .del files, etc, are
comment|// removed):
name|deleter
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Have the deleter remove any now unreferenced
comment|// files due to this commit:
name|deleter
operator|.
name|checkpoint
argument_list|(
name|segmentInfos
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeLock
operator|!=
literal|null
condition|)
block|{
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
name|commitChanges
argument_list|()
expr_stmt|;
block|}
name|hasChanges
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|commitChanges
specifier|protected
specifier|abstract
name|void
name|commitChanges
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Tries to acquire the WriteLock on this directory.    * this method is only valid if this IndexReader is directory owner.    *     * @throws StaleReaderException if the index has changed    * since this reader was opened    * @throws CorruptIndexException if the index is corrupt    * @throws LockObtainFailedException if another writer    *  has this index open (<code>write.lock</code> could not    *  be obtained)    * @throws IOException if there is a low-level IO error    */
DECL|method|acquireWriteLock
specifier|protected
name|void
name|acquireWriteLock
parameter_list|()
throws|throws
name|StaleReaderException
throws|,
name|CorruptIndexException
throws|,
name|LockObtainFailedException
throws|,
name|IOException
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|stale
condition|)
throw|throw
operator|new
name|StaleReaderException
argument_list|(
literal|"IndexReader out of date and no longer valid for delete, undelete, or setNorm operations"
argument_list|)
throw|;
if|if
condition|(
name|writeLock
operator|==
literal|null
condition|)
block|{
name|Lock
name|writeLock
init|=
name|directory
operator|.
name|makeLock
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeLock
operator|.
name|obtain
argument_list|(
name|IndexWriter
operator|.
name|WRITE_LOCK_TIMEOUT
argument_list|)
condition|)
comment|// obtain write lock
throw|throw
operator|new
name|LockObtainFailedException
argument_list|(
literal|"Index locked for write: "
operator|+
name|writeLock
argument_list|)
throw|;
name|this
operator|.
name|writeLock
operator|=
name|writeLock
expr_stmt|;
comment|// we have to check whether index has changed since this reader was opened.
comment|// if so, this reader is no longer valid for deletion
if|if
condition|(
name|SegmentInfos
operator|.
name|readCurrentVersion
argument_list|(
name|directory
argument_list|)
operator|>
name|segmentInfos
operator|.
name|getVersion
argument_list|()
condition|)
block|{
name|stale
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
name|this
operator|.
name|writeLock
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|StaleReaderException
argument_list|(
literal|"IndexReader out of date and no longer valid for delete, undelete, or setNorm operations"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**    * Should internally checkpoint state that will change    * during commit so that we can rollback if necessary.    */
DECL|method|startCommit
name|void
name|startCommit
parameter_list|()
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
name|rollbackSegmentInfos
operator|=
operator|(
name|SegmentInfos
operator|)
name|segmentInfos
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
name|rollbackHasChanges
operator|=
name|hasChanges
expr_stmt|;
block|}
comment|/**    * Rolls back state to just before the commit (this is    * called by commit() if there is some exception while    * committing).    */
DECL|method|rollbackCommit
name|void
name|rollbackCommit
parameter_list|()
block|{
if|if
condition|(
name|segmentInfos
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// Rollback each segmentInfo.  Because the
comment|// SegmentReader holds a reference to the
comment|// SegmentInfo we can't [easily] just replace
comment|// segmentInfos, so we reset it in place instead:
name|segmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
operator|.
name|reset
argument_list|(
name|rollbackSegmentInfos
operator|.
name|info
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rollbackSegmentInfos
operator|=
literal|null
expr_stmt|;
block|}
name|hasChanges
operator|=
name|rollbackHasChanges
expr_stmt|;
block|}
comment|/** Release the write lock, if needed. */
DECL|method|finalize
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
if|if
condition|(
name|writeLock
operator|!=
literal|null
condition|)
block|{
name|writeLock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// release write lock
name|writeLock
operator|=
literal|null
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

