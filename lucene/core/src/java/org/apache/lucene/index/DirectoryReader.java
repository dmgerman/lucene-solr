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
name|FileNotFoundException
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
name|nio
operator|.
name|file
operator|.
name|NoSuchFileException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SearcherManager
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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

begin_comment
comment|/** DirectoryReader is an implementation of {@link CompositeReader}  that can read indexes in a {@link Directory}.<p>DirectoryReader instances are usually constructed with a call to  one of the static<code>open()</code> methods, e.g. {@link  #open(Directory)}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral -- they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment

begin_class
DECL|class|DirectoryReader
specifier|public
specifier|abstract
class|class
name|DirectoryReader
extends|extends
name|BaseCompositeReader
argument_list|<
name|LeafReader
argument_list|>
block|{
comment|/** The index directory. */
DECL|field|directory
specifier|protected
specifier|final
name|Directory
name|directory
decl_stmt|;
comment|/** Returns a IndexReader reading the index in the given    *  Directory    * @param directory the index directory    * @throws IOException if there is a low-level IO error    */
DECL|method|open
specifier|public
specifier|static
name|DirectoryReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StandardDirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Open a near real time IndexReader from the {@link org.apache.lucene.index.IndexWriter}.    *    * @param writer The IndexWriter to open from    * @return The new IndexReader    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    *    * @see #openIfChanged(DirectoryReader,IndexWriter,boolean)    *    * @lucene.experimental    */
DECL|method|open
specifier|public
specifier|static
name|DirectoryReader
name|open
parameter_list|(
specifier|final
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|open
argument_list|(
name|writer
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Expert: open a near real time IndexReader from the {@link org.apache.lucene.index.IndexWriter},    * controlling whether past deletions should be applied.    *    * @param writer The IndexWriter to open from    * @param applyAllDeletes If true, all buffered deletes will    * be applied (made visible) in the returned reader.  If    * false, the deletes are not applied but remain buffered    * (in IndexWriter) so that they will be applied in the    * future.  Applying deletes can be costly, so if your app    * can tolerate deleted documents being returned you might    * gain some performance by passing false.    * @param writeAllDeletes If true, new deletes will be written    * down to index files instead of carried over from writer to    * reader in heap    *    * @see #open(IndexWriter)    *    * @lucene.experimental    */
DECL|method|open
specifier|public
specifier|static
name|DirectoryReader
name|open
parameter_list|(
specifier|final
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|,
name|boolean
name|writeAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writer
operator|.
name|getReader
argument_list|(
name|applyAllDeletes
argument_list|,
name|writeAllDeletes
argument_list|)
return|;
block|}
comment|/** Expert: returns an IndexReader reading the index in the given    *  {@link IndexCommit}.    * @param commit the commit point to open    * @throws IOException if there is a low-level IO error    */
DECL|method|open
specifier|public
specifier|static
name|DirectoryReader
name|open
parameter_list|(
specifier|final
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|StandardDirectoryReader
operator|.
name|open
argument_list|(
name|commit
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|commit
argument_list|)
return|;
block|}
comment|/**    * If the index has changed since the provided reader was    * opened, open and return a new reader; else, return    * null.  The new reader, if not null, will be the same    * type of reader as the previous one, ie an NRT reader    * will open a new NRT reader, a MultiReader will open a    * new MultiReader,  etc.    *    *<p>This method is typically far less costly than opening a    * fully new<code>DirectoryReader</code> as it shares    * resources (for example sub-readers) with the provided    *<code>DirectoryReader</code>, when possible.    *    *<p>The provided reader is not closed (you are responsible    * for doing so); if a new reader is returned you also    * must eventually close it.  Be sure to never close a    * reader while other threads are still using it; see    * {@link SearcherManager} to simplify managing this.    *    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @return null if there are no changes; else, a new    * DirectoryReader instance which you must eventually close    */
DECL|method|openIfChanged
specifier|public
specifier|static
name|DirectoryReader
name|openIfChanged
parameter_list|(
name|DirectoryReader
name|oldReader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DirectoryReader
name|newReader
init|=
name|oldReader
operator|.
name|doOpenIfChanged
argument_list|()
decl_stmt|;
assert|assert
name|newReader
operator|!=
name|oldReader
assert|;
return|return
name|newReader
return|;
block|}
comment|/**    * If the IndexCommit differs from what the    * provided reader is searching, open and return a new    * reader; else, return null.    *    * @see #openIfChanged(DirectoryReader)    */
DECL|method|openIfChanged
specifier|public
specifier|static
name|DirectoryReader
name|openIfChanged
parameter_list|(
name|DirectoryReader
name|oldReader
parameter_list|,
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DirectoryReader
name|newReader
init|=
name|oldReader
operator|.
name|doOpenIfChanged
argument_list|(
name|commit
argument_list|)
decl_stmt|;
assert|assert
name|newReader
operator|!=
name|oldReader
assert|;
return|return
name|newReader
return|;
block|}
comment|/**    * Expert: If there changes (committed or not) in the    * {@link IndexWriter} versus what the provided reader is    * searching, then open and return a new    * IndexReader searching both committed and uncommitted    * changes from the writer; else, return null (though, the    * current implementation never returns null).    *    *<p>This provides "near real-time" searching, in that    * changes made during an {@link IndexWriter} session can be    * quickly made available for searching without closing    * the writer nor calling {@link IndexWriter#commit}.    *    *<p>It's<i>near</i> real-time because there is no hard    * guarantee on how quickly you can get a new reader after    * making changes with IndexWriter.  You'll have to    * experiment in your situation to determine if it's    * fast enough.  As this is a new and experimental    * feature, please report back on your findings so we can    * learn, improve and iterate.</p>    *    *<p>The very first time this method is called, this    * writer instance will make every effort to pool the    * readers that it opens for doing merges, applying    * deletes, etc.  This means additional resources (RAM,    * file descriptors, CPU time) will be consumed.</p>    *    *<p>For lower latency on reopening a reader, you should    * call {@link IndexWriterConfig#setMergedSegmentWarmer} to    * pre-warm a newly merged segment before it's committed    * to the index.  This is important for minimizing    * index-to-search delay after a large merge.</p>    *    *<p>If an addIndexes* call is running in another thread,    * then this reader will only search those segments from    * the foreign index that have been successfully copied    * over, so far.</p>    *    *<p><b>NOTE</b>: Once the writer is closed, any    * outstanding readers may continue to be used.  However,    * if you attempt to reopen any of those readers, you'll    * hit an {@link org.apache.lucene.store.AlreadyClosedException}.</p>    *    * @return DirectoryReader that covers entire index plus all    * changes made so far by this IndexWriter instance, or    * null if there are no new changes    *    * @param writer The IndexWriter to open from    *    * @throws IOException if there is a low-level IO error    *    * @lucene.experimental    */
DECL|method|openIfChanged
specifier|public
specifier|static
name|DirectoryReader
name|openIfChanged
parameter_list|(
name|DirectoryReader
name|oldReader
parameter_list|,
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|openIfChanged
argument_list|(
name|oldReader
argument_list|,
name|writer
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Expert: Opens a new reader, if there are any changes, controlling whether past deletions should be applied.    *    * @see #openIfChanged(DirectoryReader,IndexWriter)    *    * @param writer The IndexWriter to open from    *    * @param applyAllDeletes If true, all buffered deletes will    * be applied (made visible) in the returned reader.  If    * false, the deletes are not applied but remain buffered    * (in IndexWriter) so that they will be applied in the    * future.  Applying deletes can be costly, so if your app    * can tolerate deleted documents being returned you might    * gain some performance by passing false.    *    * @throws IOException if there is a low-level IO error    *    * @lucene.experimental    */
DECL|method|openIfChanged
specifier|public
specifier|static
name|DirectoryReader
name|openIfChanged
parameter_list|(
name|DirectoryReader
name|oldReader
parameter_list|,
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|DirectoryReader
name|newReader
init|=
name|oldReader
operator|.
name|doOpenIfChanged
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
decl_stmt|;
assert|assert
name|newReader
operator|!=
name|oldReader
assert|;
return|return
name|newReader
return|;
block|}
comment|/** Returns all commit points that exist in the Directory.    *  Normally, because the default is {@link    *  KeepOnlyLastCommitDeletionPolicy}, there would be only    *  one commit point.  But if you're using a custom {@link    *  IndexDeletionPolicy} then there could be many commits.    *  Once you have a given commit, you can open a reader on    *  it by calling {@link DirectoryReader#open(IndexCommit)}    *  There must be at least one commit in    *  the Directory, else this method throws {@link    *  IndexNotFoundException}.  Note that if a commit is in    *  progress while this method is running, that commit    *  may or may not be returned.    *      *  @return a sorted list of {@link IndexCommit}s, from oldest     *  to latest. */
DECL|method|listCommits
specifier|public
specifier|static
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|listCommits
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
index|[]
name|files
init|=
name|dir
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|IndexCommit
argument_list|>
name|commits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|SegmentInfos
name|latest
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|dir
argument_list|)
decl_stmt|;
specifier|final
name|long
name|currentGen
init|=
name|latest
operator|.
name|getGeneration
argument_list|()
decl_stmt|;
name|commits
operator|.
name|add
argument_list|(
operator|new
name|StandardDirectoryReader
operator|.
name|ReaderCommit
argument_list|(
literal|null
argument_list|,
name|latest
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|final
name|String
name|fileName
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|startsWith
argument_list|(
name|IndexFileNames
operator|.
name|SEGMENTS
argument_list|)
operator|&&
operator|!
name|fileName
operator|.
name|equals
argument_list|(
name|IndexFileNames
operator|.
name|OLD_SEGMENTS_GEN
argument_list|)
operator|&&
name|SegmentInfos
operator|.
name|generationFromSegmentsFileName
argument_list|(
name|fileName
argument_list|)
operator|<
name|currentGen
condition|)
block|{
name|SegmentInfos
name|sis
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// IOException allowed to throw there, in case
comment|// segments_N is corrupt
name|sis
operator|=
name|SegmentInfos
operator|.
name|readCommit
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|fnfe
parameter_list|)
block|{
comment|// LUCENE-948: on NFS (and maybe others), if
comment|// you have writers switching back and forth
comment|// between machines, it's very likely that the
comment|// dir listing will be stale and will claim a
comment|// file segments_X exists when in fact it
comment|// doesn't.  So, we catch this and handle it
comment|// as if the file does not exist
block|}
if|if
condition|(
name|sis
operator|!=
literal|null
condition|)
block|{
name|commits
operator|.
name|add
argument_list|(
operator|new
name|StandardDirectoryReader
operator|.
name|ReaderCommit
argument_list|(
literal|null
argument_list|,
name|sis
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Ensure that the commit points are sorted in ascending order.
name|Collections
operator|.
name|sort
argument_list|(
name|commits
argument_list|)
expr_stmt|;
return|return
name|commits
return|;
block|}
comment|/**    * Returns<code>true</code> if an index likely exists at    * the specified directory.  Note that if a corrupt index    * exists, or if an index in the process of committing     * @param  directory the directory to check for an index    * @return<code>true</code> if an index exists;<code>false</code> otherwise    */
DECL|method|indexExists
specifier|public
specifier|static
name|boolean
name|indexExists
parameter_list|(
name|Directory
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// LUCENE-2812, LUCENE-2727, LUCENE-4738: this logic will
comment|// return true in cases that should arguably be false,
comment|// such as only IW.prepareCommit has been called, or a
comment|// corrupt first commit, but it's too deadly to make
comment|// this logic "smarter" and risk accidentally returning
comment|// false due to various cases like file description
comment|// exhaustion, access denied, etc., because in that
comment|// case IndexWriter may delete the entire index.  It's
comment|// safer to err towards "index exists" than try to be
comment|// smart about detecting not-yet-fully-committed or
comment|// corrupt indices.  This means that IndexWriter will
comment|// throw an exception on such indices and the app must
comment|// resolve the situation manually:
name|String
index|[]
name|files
init|=
name|directory
operator|.
name|listAll
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|IndexFileNames
operator|.
name|SEGMENTS
operator|+
literal|"_"
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Expert: Constructs a {@code DirectoryReader} on the given subReaders.    * @param segmentReaders the wrapped atomic index segment readers. This array is    * returned by {@link #getSequentialSubReaders} and used to resolve the correct    * subreader for docID-based methods.<b>Please note:</b> This array is<b>not</b>    * cloned and not protected for modification outside of this reader.    * Subclasses of {@code DirectoryReader} should take care to not allow    * modification of this internal array, e.g. {@link #doOpenIfChanged()}.    */
DECL|method|DirectoryReader
specifier|protected
name|DirectoryReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|LeafReader
index|[]
name|segmentReaders
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|segmentReaders
argument_list|)
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
block|}
comment|/** Returns the directory this index resides in. */
DECL|method|directory
specifier|public
specifier|final
name|Directory
name|directory
parameter_list|()
block|{
comment|// Don't ensureOpen here -- in certain cases, when a
comment|// cloned/reopened reader needs to commit, it may call
comment|// this method on the closed original reader
return|return
name|directory
return|;
block|}
comment|/** Implement this method to support {@link #openIfChanged(DirectoryReader)}.    * If this reader does not support reopen, return {@code null}, so    * client code is happy. This should be consistent with {@link #isCurrent}    * (should always return {@code true}) if reopen is not supported.    * @throws IOException if there is a low-level IO error    * @return null if there are no changes; else, a new    * DirectoryReader instance.    */
DECL|method|doOpenIfChanged
specifier|protected
specifier|abstract
name|DirectoryReader
name|doOpenIfChanged
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Implement this method to support {@link #openIfChanged(DirectoryReader,IndexCommit)}.    * If this reader does not support reopen from a specific {@link IndexCommit},    * throw {@link UnsupportedOperationException}.    * @throws IOException if there is a low-level IO error    * @return null if there are no changes; else, a new    * DirectoryReader instance.    */
DECL|method|doOpenIfChanged
specifier|protected
specifier|abstract
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
specifier|final
name|IndexCommit
name|commit
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Implement this method to support {@link #openIfChanged(DirectoryReader,IndexWriter,boolean)}.    * If this reader does not support reopen from {@link IndexWriter},    * throw {@link UnsupportedOperationException}.    * @throws IOException if there is a low-level IO error    * @return null if there are no changes; else, a new    * DirectoryReader instance.    */
DECL|method|doOpenIfChanged
specifier|protected
specifier|abstract
name|DirectoryReader
name|doOpenIfChanged
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Version number when this IndexReader was opened.    *    *<p>This method    * returns the version recorded in the commit that the    * reader opened.  This version is advanced every time    * a change is made with {@link IndexWriter}.</p>    */
DECL|method|getVersion
specifier|public
specifier|abstract
name|long
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Check whether any new changes have occurred to the    * index since this reader was opened.    *    *<p>If this reader was created by calling {@link #open},      * then this method checks if any further commits     * (see {@link IndexWriter#commit}) have occurred in the     * directory.</p>    *    *<p>If instead this reader is a near real-time reader    * (ie, obtained by a call to {@link    * DirectoryReader#open(IndexWriter)}, or by calling {@link #openIfChanged}    * on a near real-time reader), then this method checks if    * either a new commit has occurred, or any new    * uncommitted changes have taken place via the writer.    * Note that even if the writer has only performed    * merging, this method will still return false.</p>    *    *<p>In any event, if this returns false, you should call    * {@link #openIfChanged} to get a new reader that sees the    * changes.</p>    *    * @throws IOException           if there is a low-level IO error    */
DECL|method|isCurrent
specifier|public
specifier|abstract
name|boolean
name|isCurrent
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: return the IndexCommit that this reader has opened.    * @lucene.experimental    */
DECL|method|getIndexCommit
specifier|public
specifier|abstract
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

