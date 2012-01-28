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
name|codecs
operator|.
name|PerDocProducer
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
name|StoredFieldsReader
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
name|TermVectorsReader
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
name|FieldCache
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SegmentReader
specifier|public
specifier|final
class|class
name|SegmentReader
extends|extends
name|IndexReader
block|{
DECL|field|si
specifier|private
specifier|final
name|SegmentInfo
name|si
decl_stmt|;
DECL|field|readerContext
specifier|private
specifier|final
name|ReaderContext
name|readerContext
init|=
operator|new
name|AtomicReaderContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
comment|// Normally set to si.docCount - si.delDocCount, unless we
comment|// were created as an NRT reader from IW, in which case IW
comment|// tells us the docCount:
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|core
specifier|final
name|SegmentCoreReaders
name|core
decl_stmt|;
comment|/**    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
DECL|method|SegmentReader
specifier|public
name|SegmentReader
parameter_list|(
name|SegmentInfo
name|si
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|si
operator|=
name|si
expr_stmt|;
name|core
operator|=
operator|new
name|SegmentCoreReaders
argument_list|(
name|this
argument_list|,
name|si
operator|.
name|dir
argument_list|,
name|si
argument_list|,
name|context
argument_list|,
name|termInfosIndexDivisor
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|si
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
comment|// NOTE: the bitvector is stored using the regular directory, not cfs
name|liveDocs
operator|=
name|si
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|readLiveDocs
argument_list|(
name|directory
argument_list|()
argument_list|,
name|si
argument_list|,
operator|new
name|IOContext
argument_list|(
name|IOContext
operator|.
name|READ
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|si
operator|.
name|getDelCount
argument_list|()
operator|==
literal|0
assert|;
name|liveDocs
operator|=
literal|null
expr_stmt|;
block|}
name|numDocs
operator|=
name|si
operator|.
name|docCount
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
comment|// With lock-less commits, it's entirely possible (and
comment|// fine) to hit a FileNotFound exception above.  In
comment|// this case, we want to explicitly close any subset
comment|// of things that were opened so that we don't have to
comment|// wait for a GC to do so.
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|core
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Create new SegmentReader sharing core from a previous
comment|// SegmentReader and loading new live docs from a new
comment|// deletes file.  Used by openIfChanged.
DECL|method|SegmentReader
name|SegmentReader
parameter_list|(
name|SegmentInfo
name|si
parameter_list|,
name|SegmentCoreReaders
name|core
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|si
argument_list|,
name|core
argument_list|,
name|si
operator|.
name|getCodec
argument_list|()
operator|.
name|liveDocsFormat
argument_list|()
operator|.
name|readLiveDocs
argument_list|(
name|si
operator|.
name|dir
argument_list|,
name|si
argument_list|,
name|context
argument_list|)
argument_list|,
name|si
operator|.
name|docCount
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Create new SegmentReader sharing core from a previous
comment|// SegmentReader and using the provided in-memory
comment|// liveDocs.  Used by IndexWriter to provide a new NRT
comment|// reader:
DECL|method|SegmentReader
name|SegmentReader
parameter_list|(
name|SegmentInfo
name|si
parameter_list|,
name|SegmentCoreReaders
name|core
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|si
operator|=
name|si
expr_stmt|;
name|this
operator|.
name|core
operator|=
name|core
expr_stmt|;
name|core
operator|.
name|incRef
argument_list|()
expr_stmt|;
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
name|this
operator|.
name|liveDocs
operator|=
name|liveDocs
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLiveDocs
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|liveDocs
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("SR.close seg=" + si);
name|core
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|liveDocs
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getFieldInfos
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|core
operator|.
name|fieldInfos
return|;
block|}
comment|/** @lucene.internal */
DECL|method|getFieldsReader
specifier|public
name|StoredFieldsReader
name|getFieldsReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|fieldsReaderLocal
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|document
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
name|docID
operator|>=
name|maxDoc
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docID must be>= 0 and< maxDoc="
operator|+
name|maxDoc
argument_list|()
operator|+
literal|" (got docID="
operator|+
name|docID
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|getFieldsReader
argument_list|()
operator|.
name|visitDocument
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fields
specifier|public
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|si
operator|.
name|docCount
return|;
block|}
annotation|@
name|Override
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|FieldInfo
name|fi
init|=
name|core
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|fi
operator|.
name|normsPresent
argument_list|()
return|;
block|}
comment|/** @lucene.internal */
DECL|method|getTermVectorsReader
specifier|public
name|TermVectorsReader
name|getTermVectorsReader
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|core
operator|.
name|termVectorsLocal
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Return a term frequency vector for the specified document and field. The    *  vector returned contains term numbers and frequencies for all terms in    *  the specified field of this document, if the field had storeTermVector    *  flag set.  If the flag was not set, the method returns null.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getTermVectors
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|TermVectorsReader
name|termVectorsReader
init|=
name|getTermVectorsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|termVectorsReader
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|termVectorsReader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
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
comment|// SegmentInfo.toString takes dir and number of
comment|// *pending* deletions; so we reverse compute that here:
return|return
name|si
operator|.
name|toString
argument_list|(
name|core
operator|.
name|dir
argument_list|,
name|si
operator|.
name|docCount
operator|-
name|numDocs
operator|-
name|si
operator|.
name|getDelCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
name|ReaderContext
name|getTopReaderContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|readerContext
return|;
block|}
comment|/**    * Return the name of the segment this reader is reading.    */
DECL|method|getSegmentName
specifier|public
name|String
name|getSegmentName
parameter_list|()
block|{
return|return
name|core
operator|.
name|segment
return|;
block|}
comment|/**    * Return the SegmentInfo of the segment this reader is reading.    */
DECL|method|getSegmentInfo
name|SegmentInfo
name|getSegmentInfo
parameter_list|()
block|{
return|return
name|si
return|;
block|}
comment|/** Returns the directory this index resides in. */
annotation|@
name|Override
DECL|method|directory
specifier|public
name|Directory
name|directory
parameter_list|()
block|{
comment|// Don't ensureOpen here -- in certain cases, when a
comment|// cloned/reopened reader needs to commit, it may call
comment|// this method on the closed original reader
return|return
name|core
operator|.
name|dir
return|;
block|}
comment|// This is necessary so that cloned SegmentReaders (which
comment|// share the underlying postings data) will map to the
comment|// same entry in the FieldCache.  See LUCENE-1579.
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|core
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|getTermInfosIndexDivisor
specifier|public
name|int
name|getTermInfosIndexDivisor
parameter_list|()
block|{
return|return
name|core
operator|.
name|termsIndexDivisor
return|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|PerDocProducer
name|perDoc
init|=
name|core
operator|.
name|perDocProducer
decl_stmt|;
if|if
condition|(
name|perDoc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|perDoc
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|normValues
specifier|public
name|DocValues
name|normValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|PerDocProducer
name|perDoc
init|=
name|core
operator|.
name|norms
decl_stmt|;
if|if
condition|(
name|perDoc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|perDoc
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**    * Called when the shared core for this SegmentReader    * is closed.    *<p>    * This listener is called only once all SegmentReaders     * sharing the same core are closed.  At this point it     * is safe for apps to evict this reader from any caches     * keyed on {@link #getCoreCacheKey}.  This is the same     * interface that {@link FieldCache} uses, internally,     * to evict entries.</p>    *     * @lucene.experimental    */
DECL|interface|CoreClosedListener
specifier|public
specifier|static
interface|interface
name|CoreClosedListener
block|{
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|SegmentReader
name|owner
parameter_list|)
function_decl|;
block|}
comment|/** Expert: adds a CoreClosedListener to this reader's shared core */
DECL|method|addCoreClosedListener
specifier|public
name|void
name|addCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|core
operator|.
name|addCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: removes a CoreClosedListener from this reader's shared core */
DECL|method|removeCoreClosedListener
specifier|public
name|void
name|removeCoreClosedListener
parameter_list|(
name|CoreClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|core
operator|.
name|removeCoreClosedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

