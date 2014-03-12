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
name|HashMap
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|atomic
operator|.
name|AtomicInteger
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|DocValuesFormat
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
name|LiveDocsFormat
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
name|document
operator|.
name|NumericDocValuesField
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
name|NumericFieldUpdates
operator|.
name|UpdatesIterator
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
name|store
operator|.
name|TrackingDirectoryWrapper
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
name|Bits
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
name|IOUtils
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
name|MutableBits
import|;
end_import

begin_comment
comment|// Used by IndexWriter to hold open SegmentReaders (for
end_comment

begin_comment
comment|// searching or merging), plus pending deletes and updates,
end_comment

begin_comment
comment|// for a given segment
end_comment

begin_class
DECL|class|ReadersAndUpdates
class|class
name|ReadersAndUpdates
block|{
comment|// Not final because we replace (clone) when we need to
comment|// change it and it's been shared:
DECL|field|info
specifier|public
specifier|final
name|SegmentCommitInfo
name|info
decl_stmt|;
comment|// Tracks how many consumers are using this instance:
DECL|field|refCount
specifier|private
specifier|final
name|AtomicInteger
name|refCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
comment|// Set once (null, and then maybe set, and never set again):
DECL|field|reader
specifier|private
name|SegmentReader
name|reader
decl_stmt|;
comment|// Holds the current shared (readable and writable)
comment|// liveDocs.  This is null when there are no deleted
comment|// docs, and it's copy-on-write (cloned whenever we need
comment|// to change it but it's been shared to an external NRT
comment|// reader).
DECL|field|liveDocs
specifier|private
name|Bits
name|liveDocs
decl_stmt|;
comment|// How many further deletions we've done against
comment|// liveDocs vs when we loaded it or last wrote it:
DECL|field|pendingDeleteCount
specifier|private
name|int
name|pendingDeleteCount
decl_stmt|;
comment|// True if the current liveDocs is referenced by an
comment|// external NRT reader:
DECL|field|liveDocsShared
specifier|private
name|boolean
name|liveDocsShared
decl_stmt|;
comment|// Indicates whether this segment is currently being merged. While a segment
comment|// is merging, all field updates are also registered in the
comment|// mergingNumericUpdates map. Also, calls to writeFieldUpdates merge the
comment|// updates with mergingNumericUpdates.
comment|// That way, when the segment is done merging, IndexWriter can apply the
comment|// updates on the merged segment too.
DECL|field|isMerging
specifier|private
name|boolean
name|isMerging
init|=
literal|false
decl_stmt|;
DECL|field|mergingNumericUpdates
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NumericFieldUpdates
argument_list|>
name|mergingNumericUpdates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ReadersAndUpdates
specifier|public
name|ReadersAndUpdates
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|SegmentCommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|liveDocsShared
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|incRef
specifier|public
name|void
name|incRef
parameter_list|()
block|{
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
assert|assert
name|rc
operator|>
literal|1
assert|;
block|}
DECL|method|decRef
specifier|public
name|void
name|decRef
parameter_list|()
block|{
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
assert|assert
name|rc
operator|>=
literal|0
assert|;
block|}
DECL|method|refCount
specifier|public
name|int
name|refCount
parameter_list|()
block|{
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
name|rc
operator|>=
literal|0
assert|;
return|return
name|rc
return|;
block|}
DECL|method|getPendingDeleteCount
specifier|public
specifier|synchronized
name|int
name|getPendingDeleteCount
parameter_list|()
block|{
return|return
name|pendingDeleteCount
return|;
block|}
comment|// Call only from assert!
DECL|method|verifyDocCounts
specifier|public
specifier|synchronized
name|boolean
name|verifyDocCounts
parameter_list|()
block|{
name|int
name|count
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|count
operator|=
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
assert|assert
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|-
name|pendingDeleteCount
operator|==
name|count
operator|:
literal|"info.docCount="
operator|+
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|+
literal|" info.getDelCount()="
operator|+
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
literal|" pendingDeleteCount="
operator|+
name|pendingDeleteCount
operator|+
literal|" count="
operator|+
name|count
assert|;
return|return
literal|true
return|;
block|}
comment|/** Returns a {@link SegmentReader}. */
DECL|method|getReader
specifier|public
name|SegmentReader
name|getReader
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
comment|// We steal returned ref:
name|reader
operator|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
name|liveDocs
operator|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Ref for caller
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|reader
return|;
block|}
DECL|method|release
specifier|public
specifier|synchronized
name|void
name|release
parameter_list|(
name|SegmentReader
name|sr
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|info
operator|==
name|sr
operator|.
name|getSegmentInfo
argument_list|()
assert|;
name|sr
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
DECL|method|delete
specifier|public
specifier|synchronized
name|boolean
name|delete
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
assert|assert
name|docID
operator|>=
literal|0
operator|&&
name|docID
operator|<
name|liveDocs
operator|.
name|length
argument_list|()
operator|:
literal|"out of bounds: docid="
operator|+
name|docID
operator|+
literal|" liveDocsLength="
operator|+
name|liveDocs
operator|.
name|length
argument_list|()
operator|+
literal|" seg="
operator|+
name|info
operator|.
name|info
operator|.
name|name
operator|+
literal|" docCount="
operator|+
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
assert|;
assert|assert
operator|!
name|liveDocsShared
assert|;
specifier|final
name|boolean
name|didDelete
init|=
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|didDelete
condition|)
block|{
operator|(
operator|(
name|MutableBits
operator|)
name|liveDocs
operator|)
operator|.
name|clear
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|pendingDeleteCount
operator|++
expr_stmt|;
comment|//System.out.println("  new del seg=" + info + " docID=" + docID + " pendingDelCount=" + pendingDeleteCount + " totDelCount=" + (info.docCount-liveDocs.count()));
block|}
return|return
name|didDelete
return|;
block|}
comment|// NOTE: removes callers ref
DECL|method|dropReaders
specifier|public
specifier|synchronized
name|void
name|dropReaders
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: can we somehow use IOUtils here...?  problem is
comment|// we are calling .decRef not .close)...
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
comment|//System.out.println("  pool.drop info=" + info + " rc=" + reader.getRefCount());
try|try
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|decRef
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a ref to a clone. NOTE: you should decRef() the reader when you're    * dont (ie do not call close()).    */
DECL|method|getReadOnlyClone
specifier|public
specifier|synchronized
name|SegmentReader
name|getReadOnlyClone
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
name|getReader
argument_list|(
name|context
argument_list|)
operator|.
name|decRef
argument_list|()
expr_stmt|;
assert|assert
name|reader
operator|!=
literal|null
assert|;
block|}
name|liveDocsShared
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SegmentReader
argument_list|(
name|reader
operator|.
name|getSegmentInfo
argument_list|()
argument_list|,
name|reader
argument_list|,
name|liveDocs
argument_list|,
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|-
name|pendingDeleteCount
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|reader
operator|.
name|getLiveDocs
argument_list|()
operator|==
name|liveDocs
assert|;
name|reader
operator|.
name|incRef
argument_list|()
expr_stmt|;
return|return
name|reader
return|;
block|}
block|}
DECL|method|initWritableLiveDocs
specifier|public
specifier|synchronized
name|void
name|initWritableLiveDocs
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
assert|assert
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|>
literal|0
assert|;
comment|//System.out.println("initWritableLivedocs seg=" + info + " liveDocs=" + liveDocs + " shared=" + shared);
if|if
condition|(
name|liveDocsShared
condition|)
block|{
comment|// Copy on write: this means we've cloned a
comment|// SegmentReader sharing the current liveDocs
comment|// instance; must now make a private clone so we can
comment|// change it:
name|LiveDocsFormat
name|liveDocsFormat
init|=
name|info
operator|.
name|info
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
decl_stmt|;
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
comment|//System.out.println("create BV seg=" + info);
name|liveDocs
operator|=
name|liveDocsFormat
operator|.
name|newLiveDocs
argument_list|(
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|liveDocs
operator|=
name|liveDocsFormat
operator|.
name|newLiveDocs
argument_list|(
name|liveDocs
argument_list|)
expr_stmt|;
block|}
name|liveDocsShared
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|getLiveDocs
specifier|public
specifier|synchronized
name|Bits
name|getLiveDocs
parameter_list|()
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
return|return
name|liveDocs
return|;
block|}
DECL|method|getReadOnlyLiveDocs
specifier|public
specifier|synchronized
name|Bits
name|getReadOnlyLiveDocs
parameter_list|()
block|{
comment|//System.out.println("getROLiveDocs seg=" + info);
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
name|liveDocsShared
operator|=
literal|true
expr_stmt|;
comment|//if (liveDocs != null) {
comment|//System.out.println("  liveCount=" + liveDocs.count());
comment|//}
return|return
name|liveDocs
return|;
block|}
DECL|method|dropChanges
specifier|public
specifier|synchronized
name|void
name|dropChanges
parameter_list|()
block|{
comment|// Discard (don't save) changes when we are dropping
comment|// the reader; this is used only on the sub-readers
comment|// after a successful merge.  If deletes had
comment|// accumulated on those sub-readers while the merge
comment|// is running, by now we have carried forward those
comment|// deletes onto the newly merged segment, so we can
comment|// discard them on the sub-readers:
name|pendingDeleteCount
operator|=
literal|0
expr_stmt|;
name|dropMergingUpdates
argument_list|()
expr_stmt|;
block|}
comment|// Commit live docs (writes new _X_N.del files) and field updates (writes new
comment|// _X_N updates files) to the directory; returns true if it wrote any file
comment|// and false if there were no new deletes or updates to write:
DECL|method|writeLiveDocs
specifier|public
specifier|synchronized
name|boolean
name|writeLiveDocs
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
comment|//System.out.println("rld.writeLiveDocs seg=" + info + " pendingDelCount=" + pendingDeleteCount + " numericUpdates=" + numericUpdates);
if|if
condition|(
name|pendingDeleteCount
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// We have new deletes
assert|assert
name|liveDocs
operator|.
name|length
argument_list|()
operator|==
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
assert|;
comment|// Do this so we can delete any created files on
comment|// exception; this saves all codecs from having to do
comment|// it:
name|TrackingDirectoryWrapper
name|trackingDir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
name|dir
argument_list|)
decl_stmt|;
comment|// We can write directly to the actual name (vs to a
comment|// .tmp& renaming it) because the file is not live
comment|// until segments file is written:
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Codec
name|codec
init|=
name|info
operator|.
name|info
operator|.
name|getCodec
argument_list|()
decl_stmt|;
name|codec
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|writeLiveDocs
argument_list|(
operator|(
name|MutableBits
operator|)
name|liveDocs
argument_list|,
name|trackingDir
argument_list|,
name|info
argument_list|,
name|pendingDeleteCount
argument_list|,
name|IOContext
operator|.
name|DEFAULT
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
comment|// Advance only the nextWriteDelGen so that a 2nd
comment|// attempt to write will write to a new file
name|info
operator|.
name|advanceNextWriteDelGen
argument_list|()
expr_stmt|;
comment|// Delete any partially created file(s):
for|for
control|(
name|String
name|fileName
range|:
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
control|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore so we throw only the first exc
block|}
block|}
block|}
block|}
comment|// If we hit an exc in the line above (eg disk full)
comment|// then info's delGen remains pointing to the previous
comment|// (successfully written) del docs:
name|info
operator|.
name|advanceDelGen
argument_list|()
expr_stmt|;
name|info
operator|.
name|setDelCount
argument_list|(
name|info
operator|.
name|getDelCount
argument_list|()
operator|+
name|pendingDeleteCount
argument_list|)
expr_stmt|;
name|pendingDeleteCount
operator|=
literal|0
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// Writes field updates (new _X_N updates files) to the directory
DECL|method|writeFieldUpdates
specifier|public
specifier|synchronized
name|void
name|writeFieldUpdates
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|NumericFieldUpdates
argument_list|>
name|numericFieldUpdates
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
comment|//System.out.println("rld.writeFieldUpdates: seg=" + info + " numericFieldUpdates=" + numericFieldUpdates);
assert|assert
name|numericFieldUpdates
operator|!=
literal|null
operator|&&
operator|!
name|numericFieldUpdates
operator|.
name|isEmpty
argument_list|()
assert|;
comment|// Do this so we can delete any created files on
comment|// exception; this saves all codecs from having to do
comment|// it:
name|TrackingDirectoryWrapper
name|trackingDir
init|=
operator|new
name|TrackingDirectoryWrapper
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
specifier|final
name|Codec
name|codec
init|=
name|info
operator|.
name|info
operator|.
name|getCodec
argument_list|()
decl_stmt|;
comment|// reader could be null e.g. for a just merged segment (from
comment|// IndexWriter.commitMergedDeletes).
specifier|final
name|SegmentReader
name|reader
init|=
name|this
operator|.
name|reader
operator|==
literal|null
condition|?
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|IOContext
operator|.
name|READONCE
argument_list|)
else|:
name|this
operator|.
name|reader
decl_stmt|;
try|try
block|{
comment|// clone FieldInfos so that we can update their dvGen separately from
comment|// the reader's infos and write them to a new fieldInfos_gen file
name|FieldInfos
operator|.
name|Builder
name|builder
init|=
operator|new
name|FieldInfos
operator|.
name|Builder
argument_list|(
name|writer
operator|.
name|globalFieldNumberMap
argument_list|)
decl_stmt|;
comment|// cannot use builder.add(reader.getFieldInfos()) because it does not
comment|// clone FI.attributes as well FI.dvGen
for|for
control|(
name|FieldInfo
name|fi
range|:
name|reader
operator|.
name|getFieldInfos
argument_list|()
control|)
block|{
name|FieldInfo
name|clone
init|=
name|builder
operator|.
name|add
argument_list|(
name|fi
argument_list|)
decl_stmt|;
comment|// copy the stuff FieldInfos.Builder doesn't copy
if|if
condition|(
name|fi
operator|.
name|attributes
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|fi
operator|.
name|attributes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|clone
operator|.
name|putAttribute
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|clone
operator|.
name|setDocValuesGen
argument_list|(
name|fi
operator|.
name|getDocValuesGen
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create new fields or update existing ones to have NumericDV type
for|for
control|(
name|String
name|f
range|:
name|numericFieldUpdates
operator|.
name|keySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|addOrUpdate
argument_list|(
name|f
argument_list|,
name|NumericDocValuesField
operator|.
name|TYPE
argument_list|)
expr_stmt|;
block|}
name|fieldInfos
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
specifier|final
name|long
name|nextFieldInfosGen
init|=
name|info
operator|.
name|getNextFieldInfosGen
argument_list|()
decl_stmt|;
specifier|final
name|String
name|segmentSuffix
init|=
name|Long
operator|.
name|toString
argument_list|(
name|nextFieldInfosGen
argument_list|,
name|Character
operator|.
name|MAX_RADIX
argument_list|)
decl_stmt|;
specifier|final
name|SegmentWriteState
name|state
init|=
operator|new
name|SegmentWriteState
argument_list|(
literal|null
argument_list|,
name|trackingDir
argument_list|,
name|info
operator|.
name|info
argument_list|,
name|fieldInfos
argument_list|,
literal|null
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|,
name|segmentSuffix
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesFormat
name|docValuesFormat
init|=
name|codec
operator|.
name|docValuesFormat
argument_list|()
decl_stmt|;
specifier|final
name|DocValuesConsumer
name|fieldsConsumer
init|=
name|docValuesFormat
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
name|boolean
name|fieldsConsumerSuccess
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|//          System.out.println("[" + Thread.currentThread().getName() + "] RLD.writeLiveDocs: applying updates; seg=" + info + " updates=" + numericUpdates);
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|NumericFieldUpdates
argument_list|>
name|e
range|:
name|numericFieldUpdates
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|field
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|NumericFieldUpdates
name|fieldUpdates
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|fieldInfo
operator|!=
literal|null
assert|;
name|fieldInfo
operator|.
name|setDocValuesGen
argument_list|(
name|nextFieldInfosGen
argument_list|)
expr_stmt|;
comment|// write the numeric updates to a new gen'd docvalues file
name|fieldsConsumer
operator|.
name|addNumericField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
specifier|final
name|NumericDocValues
name|currentValues
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|Bits
name|docsWithField
init|=
name|reader
operator|.
name|getDocsWithField
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|UpdatesIterator
name|updatesIter
init|=
name|fieldUpdates
operator|.
name|getUpdates
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Number
argument_list|>
name|iterator
parameter_list|()
block|{
name|updatesIter
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|curDoc
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|updateDoc
init|=
name|updatesIter
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|curDoc
operator|<
name|maxDoc
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|next
parameter_list|()
block|{
if|if
condition|(
operator|++
name|curDoc
operator|>=
name|maxDoc
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"no more documents to return values for"
argument_list|)
throw|;
block|}
if|if
condition|(
name|curDoc
operator|==
name|updateDoc
condition|)
block|{
comment|// this document has an updated value
name|Long
name|value
init|=
name|updatesIter
operator|.
name|value
argument_list|()
decl_stmt|;
comment|// either null (unset value) or updated value
name|updateDoc
operator|=
name|updatesIter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
comment|// prepare for next round
return|return
name|value
return|;
block|}
else|else
block|{
comment|// no update for this document
assert|assert
name|curDoc
operator|<
name|updateDoc
assert|;
if|if
condition|(
name|currentValues
operator|!=
literal|null
operator|&&
name|docsWithField
operator|.
name|get
argument_list|(
name|curDoc
argument_list|)
condition|)
block|{
comment|// only read the current value if the document had a value before
return|return
name|currentValues
operator|.
name|get
argument_list|(
name|curDoc
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this iterator does not support removing elements"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|codec
operator|.
name|fieldInfosFormat
argument_list|()
operator|.
name|getFieldInfosWriter
argument_list|()
operator|.
name|write
argument_list|(
name|trackingDir
argument_list|,
name|info
operator|.
name|info
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|fieldInfos
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|fieldsConsumerSuccess
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fieldsConsumerSuccess
condition|)
block|{
name|fieldsConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|fieldsConsumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
name|this
operator|.
name|reader
condition|)
block|{
comment|//          System.out.println("[" + Thread.currentThread().getName() + "] RLD.writeLiveDocs: closeReader " + reader);
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
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
comment|// Advance only the nextWriteDocValuesGen so that a 2nd
comment|// attempt to write will write to a new file
name|info
operator|.
name|advanceNextWriteFieldInfosGen
argument_list|()
expr_stmt|;
comment|// Delete any partially created file(s):
for|for
control|(
name|String
name|fileName
range|:
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
control|)
block|{
try|try
block|{
name|dir
operator|.
name|deleteFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore so we throw only the first exc
block|}
block|}
block|}
block|}
name|info
operator|.
name|advanceFieldInfosGen
argument_list|()
expr_stmt|;
comment|// copy all the updates to mergingUpdates, so they can later be applied to the merged segment
if|if
condition|(
name|isMerging
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|NumericFieldUpdates
argument_list|>
name|e
range|:
name|numericFieldUpdates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|NumericFieldUpdates
name|fieldUpdates
init|=
name|mergingNumericUpdates
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldUpdates
operator|==
literal|null
condition|)
block|{
name|mergingNumericUpdates
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldUpdates
operator|.
name|merge
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// create a new map, keeping only the gens that are in use
name|Map
argument_list|<
name|Long
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|genUpdatesFiles
init|=
name|info
operator|.
name|getUpdatesFiles
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|newGenUpdatesFiles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|long
name|fieldInfosGen
init|=
name|info
operator|.
name|getFieldInfosGen
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|fieldInfos
control|)
block|{
name|long
name|dvGen
init|=
name|fi
operator|.
name|getDocValuesGen
argument_list|()
decl_stmt|;
if|if
condition|(
name|dvGen
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|newGenUpdatesFiles
operator|.
name|containsKey
argument_list|(
name|dvGen
argument_list|)
condition|)
block|{
if|if
condition|(
name|dvGen
operator|==
name|fieldInfosGen
condition|)
block|{
name|newGenUpdatesFiles
operator|.
name|put
argument_list|(
name|fieldInfosGen
argument_list|,
name|trackingDir
operator|.
name|getCreatedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newGenUpdatesFiles
operator|.
name|put
argument_list|(
name|dvGen
argument_list|,
name|genUpdatesFiles
operator|.
name|get
argument_list|(
name|dvGen
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|info
operator|.
name|setGenUpdatesFiles
argument_list|(
name|newGenUpdatesFiles
argument_list|)
expr_stmt|;
comment|// wrote new files, should checkpoint()
name|writer
operator|.
name|checkpoint
argument_list|()
expr_stmt|;
comment|// if there is a reader open, reopen it to reflect the updates
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|SegmentReader
name|newReader
init|=
operator|new
name|SegmentReader
argument_list|(
name|info
argument_list|,
name|reader
argument_list|,
name|liveDocs
argument_list|,
name|info
operator|.
name|info
operator|.
name|getDocCount
argument_list|()
operator|-
name|info
operator|.
name|getDelCount
argument_list|()
operator|-
name|pendingDeleteCount
argument_list|)
decl_stmt|;
name|boolean
name|reopened
init|=
literal|false
decl_stmt|;
try|try
block|{
name|reader
operator|.
name|decRef
argument_list|()
expr_stmt|;
name|reader
operator|=
name|newReader
expr_stmt|;
name|reopened
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|reopened
condition|)
block|{
name|newReader
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Returns a reader for merge. This method applies field updates if there are    * any and marks that this segment is currently merging.    */
DECL|method|getReaderForMerge
specifier|synchronized
name|SegmentReader
name|getReaderForMerge
parameter_list|(
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|writer
argument_list|)
assert|;
comment|// must execute these two statements as atomic operation, otherwise we
comment|// could lose updates if e.g. another thread calls writeFieldUpdates in
comment|// between, or the updates are applied to the obtained reader, but then
comment|// re-applied in IW.commitMergedDeletes (unnecessary work and potential
comment|// bugs).
name|isMerging
operator|=
literal|true
expr_stmt|;
return|return
name|getReader
argument_list|(
name|context
argument_list|)
return|;
block|}
comment|/**    * Drops all merging updates. Called from IndexWriter after this segment    * finished merging (whether successfully or not).    */
DECL|method|dropMergingUpdates
specifier|public
specifier|synchronized
name|void
name|dropMergingUpdates
parameter_list|()
block|{
name|mergingNumericUpdates
operator|.
name|clear
argument_list|()
expr_stmt|;
name|isMerging
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Returns updates that came in while this segment was merging. */
DECL|method|getMergingFieldUpdates
specifier|public
specifier|synchronized
name|Map
argument_list|<
name|String
argument_list|,
name|NumericFieldUpdates
argument_list|>
name|getMergingFieldUpdates
parameter_list|()
block|{
return|return
name|mergingNumericUpdates
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ReadersAndLiveDocs(seg="
argument_list|)
operator|.
name|append
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" pendingDeleteCount="
argument_list|)
operator|.
name|append
argument_list|(
name|pendingDeleteCount
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" liveDocsShared="
argument_list|)
operator|.
name|append
argument_list|(
name|liveDocsShared
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

