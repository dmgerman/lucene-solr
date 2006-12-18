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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexFileNames
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
name|IndexFileNameFilter
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
name|SegmentInfos
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A utility class (used by both IndexReader and  * IndexWriter) to keep track of files that need to be  * deleted because they are no longer referenced by the  * index.  */
end_comment

begin_class
DECL|class|IndexFileDeleter
specifier|public
class|class
name|IndexFileDeleter
block|{
DECL|field|deletable
specifier|private
name|Vector
name|deletable
decl_stmt|;
DECL|field|pending
specifier|private
name|HashSet
name|pending
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|segmentInfos
specifier|private
name|SegmentInfos
name|segmentInfos
decl_stmt|;
DECL|field|infoStream
specifier|private
name|PrintStream
name|infoStream
decl_stmt|;
DECL|method|IndexFileDeleter
specifier|public
name|IndexFileDeleter
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|,
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|segmentInfos
operator|=
name|segmentInfos
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
DECL|method|setSegmentInfos
name|void
name|setSegmentInfos
parameter_list|(
name|SegmentInfos
name|segmentInfos
parameter_list|)
block|{
name|this
operator|.
name|segmentInfos
operator|=
name|segmentInfos
expr_stmt|;
block|}
DECL|method|getSegmentInfos
name|SegmentInfos
name|getSegmentInfos
parameter_list|()
block|{
return|return
name|segmentInfos
return|;
block|}
DECL|method|setInfoStream
name|void
name|setInfoStream
parameter_list|(
name|PrintStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
comment|/** Determine index files that are no longer referenced    * and therefore should be deleted.  This is called once    * (by the writer), and then subsequently we add onto    * deletable any files that are no longer needed at the    * point that we create the unused file (eg when merging    * segments), and we only remove from deletable when a    * file is successfully deleted.    */
DECL|method|findDeletableFiles
specifier|public
name|void
name|findDeletableFiles
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Gather all "current" segments:
name|HashMap
name|current
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|segmentInfos
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|SegmentInfo
name|segmentInfo
init|=
operator|(
name|SegmentInfo
operator|)
name|segmentInfos
operator|.
name|elementAt
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|current
operator|.
name|put
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentInfo
argument_list|)
expr_stmt|;
block|}
comment|// Then go through all files in the Directory that are
comment|// Lucene index files, and add to deletable if they are
comment|// not referenced by the current segments info:
name|String
name|segmentsInfosFileName
init|=
name|segmentInfos
operator|.
name|getCurrentSegmentFileName
argument_list|()
decl_stmt|;
name|IndexFileNameFilter
name|filter
init|=
name|IndexFileNameFilter
operator|.
name|getFilter
argument_list|()
decl_stmt|;
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|list
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|filter
operator|.
name|accept
argument_list|(
literal|null
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
operator|&&
operator|!
name|files
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|segmentsInfosFileName
argument_list|)
operator|&&
operator|!
name|files
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS_GEN
argument_list|)
condition|)
block|{
name|String
name|segmentName
decl_stmt|;
name|String
name|extension
decl_stmt|;
comment|// First remove any extension:
name|int
name|loc
init|=
name|files
index|[
name|i
index|]
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|loc
operator|!=
operator|-
literal|1
condition|)
block|{
name|extension
operator|=
name|files
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|1
operator|+
name|loc
argument_list|)
expr_stmt|;
name|segmentName
operator|=
name|files
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|loc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|extension
operator|=
literal|null
expr_stmt|;
name|segmentName
operator|=
name|files
index|[
name|i
index|]
expr_stmt|;
block|}
comment|// Then, remove any generation count:
name|loc
operator|=
name|segmentName
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|,
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|loc
operator|!=
operator|-
literal|1
condition|)
block|{
name|segmentName
operator|=
name|segmentName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|loc
argument_list|)
expr_stmt|;
block|}
comment|// Delete this file if it's not a "current" segment,
comment|// or, it is a single index file but there is now a
comment|// corresponding compound file:
name|boolean
name|doDelete
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|current
operator|.
name|containsKey
argument_list|(
name|segmentName
argument_list|)
condition|)
block|{
comment|// Delete if segment is not referenced:
name|doDelete
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// OK, segment is referenced, but file may still
comment|// be orphan'd:
name|SegmentInfo
name|info
init|=
operator|(
name|SegmentInfo
operator|)
name|current
operator|.
name|get
argument_list|(
name|segmentName
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|isCFSFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
operator|&&
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
comment|// This file is in fact stored in a CFS file for
comment|// this segment:
name|doDelete
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"del"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
condition|)
block|{
comment|// This is a _segmentName_N.del file:
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getDelFileName
argument_list|()
argument_list|)
condition|)
block|{
comment|// If this is a seperate .del file, but it
comment|// doesn't match the current del filename for
comment|// this segment, then delete it:
name|doDelete
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|extension
operator|!=
literal|null
operator|&&
name|extension
operator|.
name|startsWith
argument_list|(
literal|"s"
argument_list|)
operator|&&
name|extension
operator|.
name|matches
argument_list|(
literal|"s\\d+"
argument_list|)
condition|)
block|{
name|int
name|field
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|extension
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|// This is a _segmentName_N.sX file:
if|if
condition|(
operator|!
name|files
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getNormFileName
argument_list|(
name|field
argument_list|)
argument_list|)
condition|)
block|{
comment|// This is an orphan'd separate norms file:
name|doDelete
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"cfs"
operator|.
name|equals
argument_list|(
name|extension
argument_list|)
operator|&&
operator|!
name|info
operator|.
name|getUseCompoundFile
argument_list|()
condition|)
block|{
comment|// This is a partially written
comment|// _segmentName.cfs:
name|doDelete
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|doDelete
condition|)
block|{
name|addDeletableFile
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
block|{
name|infoStream
operator|.
name|println
argument_list|(
literal|"IndexFileDeleter: file \""
operator|+
name|files
index|[
name|i
index|]
operator|+
literal|"\" is unreferenced in index and will be deleted on next commit"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/*    * Some operating systems (e.g. Windows) don't permit a file to be deleted    * while it is opened for read (e.g. by another process or thread). So we    * assume that when a delete fails it is because the file is open in another    * process, and queue the file for subsequent deletion.    */
DECL|method|deleteSegments
specifier|public
specifier|final
name|void
name|deleteSegments
parameter_list|(
name|Vector
name|segments
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteFiles
argument_list|()
expr_stmt|;
comment|// try to delete files that we couldn't before
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|segments
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|directory
argument_list|()
operator|==
name|this
operator|.
name|directory
condition|)
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
comment|// try to delete our files
else|else
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|,
name|reader
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
comment|// delete other files
block|}
block|}
comment|/**    * Delete these segments, as long as they are not listed    * in protectedSegments.  If they are, then, instead, add    * them to the pending set.   */
DECL|method|deleteSegments
specifier|public
specifier|final
name|void
name|deleteSegments
parameter_list|(
name|Vector
name|segments
parameter_list|,
name|HashSet
name|protectedSegments
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteFiles
argument_list|()
expr_stmt|;
comment|// try to delete files that we couldn't before
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|segments
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|directory
argument_list|()
operator|==
name|this
operator|.
name|directory
condition|)
block|{
if|if
condition|(
name|protectedSegments
operator|.
name|contains
argument_list|(
name|reader
operator|.
name|getSegmentName
argument_list|()
argument_list|)
condition|)
block|{
name|addPendingFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
comment|// record these for deletion on commit
block|}
else|else
block|{
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
comment|// try to delete our files
block|}
block|}
else|else
block|{
name|deleteFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|,
name|reader
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
comment|// delete other files
block|}
block|}
block|}
DECL|method|deleteFiles
specifier|public
specifier|final
name|void
name|deleteFiles
parameter_list|(
name|Vector
name|files
parameter_list|,
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
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
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|directory
operator|.
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFiles
specifier|public
specifier|final
name|void
name|deleteFiles
parameter_list|(
name|Vector
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteFiles
argument_list|()
expr_stmt|;
comment|// try to delete files that we couldn't before
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|deleteFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deleteFile
specifier|public
specifier|final
name|void
name|deleteFile
parameter_list|(
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// try to delete each file
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// if delete fails
if|if
condition|(
name|directory
operator|.
name|fileExists
argument_list|(
name|file
argument_list|)
condition|)
block|{
if|if
condition|(
name|infoStream
operator|!=
literal|null
condition|)
name|infoStream
operator|.
name|println
argument_list|(
literal|"IndexFileDeleter: unable to remove file \""
operator|+
name|file
operator|+
literal|"\": "
operator|+
name|e
operator|.
name|toString
argument_list|()
operator|+
literal|"; Will re-try later."
argument_list|)
expr_stmt|;
name|addDeletableFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// add to deletable
block|}
block|}
block|}
DECL|method|clearPendingFiles
specifier|final
name|void
name|clearPendingFiles
parameter_list|()
block|{
name|pending
operator|=
literal|null
expr_stmt|;
block|}
comment|/*     Record that the files for these segments should be     deleted, once the pending deletes are committed.    */
DECL|method|addPendingSegments
specifier|final
name|void
name|addPendingSegments
parameter_list|(
name|Vector
name|segments
parameter_list|)
throws|throws
name|IOException
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
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SegmentReader
name|reader
init|=
operator|(
name|SegmentReader
operator|)
name|segments
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|directory
argument_list|()
operator|==
name|this
operator|.
name|directory
condition|)
block|{
name|addPendingFiles
argument_list|(
name|reader
operator|.
name|files
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*     Record list of files for deletion, but do not delete     them until commitPendingFiles is called.   */
DECL|method|addPendingFiles
specifier|final
name|void
name|addPendingFiles
parameter_list|(
name|Vector
name|files
parameter_list|)
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
name|files
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|addPendingFile
argument_list|(
operator|(
name|String
operator|)
name|files
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*     Record a file for deletion, but do not delete it until     commitPendingFiles is called.   */
DECL|method|addPendingFile
specifier|final
name|void
name|addPendingFile
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
if|if
condition|(
name|pending
operator|==
literal|null
condition|)
block|{
name|pending
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
block|}
name|pending
operator|.
name|add
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|commitPendingFiles
specifier|final
name|void
name|commitPendingFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pending
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|deletable
operator|==
literal|null
condition|)
block|{
name|deletable
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
block|}
name|Iterator
name|it
init|=
name|pending
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|deletable
operator|.
name|addElement
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pending
operator|=
literal|null
expr_stmt|;
name|deleteFiles
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|addDeletableFile
specifier|public
specifier|final
name|void
name|addDeletableFile
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
if|if
condition|(
name|deletable
operator|==
literal|null
condition|)
block|{
name|deletable
operator|=
operator|new
name|Vector
argument_list|()
expr_stmt|;
block|}
name|deletable
operator|.
name|addElement
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFiles
specifier|public
specifier|final
name|void
name|deleteFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|deletable
operator|!=
literal|null
condition|)
block|{
name|Vector
name|oldDeletable
init|=
name|deletable
decl_stmt|;
name|deletable
operator|=
literal|null
expr_stmt|;
name|deleteFiles
argument_list|(
name|oldDeletable
argument_list|)
expr_stmt|;
comment|// try to delete deletable
block|}
block|}
block|}
end_class

end_unit

