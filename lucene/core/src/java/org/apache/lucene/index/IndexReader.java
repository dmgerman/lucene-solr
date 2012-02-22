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
name|Closeable
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
name|LinkedHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
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
name|document
operator|.
name|Document
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
name|DocumentStoredFieldVisitor
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
name|*
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
name|BytesRef
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
name|ReaderUtil
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_comment
comment|/** IndexReader is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable.<p>There are two different types of IndexReaders:<ul><li>{@link AtomicReader}: These indexes do not consist of several sub-readers,   they are atomic. They support retrieval of stored fields, doc values, terms,   and postings.<li>{@link CompositeReader}: Instances (like {@link DirectoryReader})   of this reader can only   be used to get stored fields from the underlying AtomicReaders,   but it is not possible to directly retrieve postings. To do that, get   the sub-readers via {@link CompositeReader#getSequentialSubReaders}.   Alternatively, you can mimic an {@link AtomicReader} (with a serious slowdown),   by wrapping composite readers with {@link SlowCompositeReaderWrapper}.</ul><p>IndexReader instances for indexes on disk are usually constructed  with a call to one of the static<code>DirectoryReader,open()</code> methods,  e.g. {@link DirectoryReader#open(Directory)}. {@link DirectoryReader} implements  the {@link CompositeReader} interface, it is not possible to directly get postings.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral -- they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment

begin_class
DECL|class|IndexReader
specifier|public
specifier|abstract
class|class
name|IndexReader
implements|implements
name|Closeable
block|{
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|closedByChild
specifier|private
name|boolean
name|closedByChild
init|=
literal|false
decl_stmt|;
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
DECL|method|IndexReader
name|IndexReader
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|this
operator|instanceof
name|CompositeReader
operator|||
name|this
operator|instanceof
name|AtomicReader
operator|)
condition|)
throw|throw
operator|new
name|Error
argument_list|(
literal|"IndexReader should never be directly extended, subclass AtomicReader or CompositeReader instead."
argument_list|)
throw|;
block|}
comment|/**    * A custom listener that's invoked when the IndexReader    * is closed.    *    * @lucene.experimental    */
DECL|interface|ReaderClosedListener
specifier|public
specifier|static
interface|interface
name|ReaderClosedListener
block|{
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
function_decl|;
block|}
DECL|field|readerClosedListeners
specifier|private
specifier|final
name|Set
argument_list|<
name|ReaderClosedListener
argument_list|>
name|readerClosedListeners
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
operator|new
name|LinkedHashSet
argument_list|<
name|ReaderClosedListener
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|parentReaders
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexReader
argument_list|>
name|parentReaders
init|=
name|Collections
operator|.
name|synchronizedSet
argument_list|(
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|WeakHashMap
argument_list|<
name|IndexReader
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Expert: adds a {@link ReaderClosedListener}.  The    * provided listener will be invoked when this reader is closed.    *    * @lucene.experimental */
DECL|method|addReaderClosedListener
specifier|public
specifier|final
name|void
name|addReaderClosedListener
parameter_list|(
name|ReaderClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|readerClosedListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: remove a previously added {@link ReaderClosedListener}.    *    * @lucene.experimental */
DECL|method|removeReaderClosedListener
specifier|public
specifier|final
name|void
name|removeReaderClosedListener
parameter_list|(
name|ReaderClosedListener
name|listener
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|readerClosedListeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: This method is called by {@code IndexReader}s which wrap other readers    * (e.g. {@link CompositeReader} or {@link FilterAtomicReader}) to register the parent    * at the child (this reader) on construction of the parent. When this reader is closed,    * it will mark all registered parents as closed, too. The references to parent readers    * are weak only, so they can be GCed once they are no longer in use.    * @lucene.experimental */
DECL|method|registerParentReader
specifier|public
specifier|final
name|void
name|registerParentReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|parentReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
DECL|method|notifyReaderClosedListeners
specifier|private
name|void
name|notifyReaderClosedListeners
parameter_list|()
block|{
synchronized|synchronized
init|(
name|readerClosedListeners
init|)
block|{
for|for
control|(
name|ReaderClosedListener
name|listener
range|:
name|readerClosedListeners
control|)
block|{
name|listener
operator|.
name|onClose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|reportCloseToParentReaders
specifier|private
name|void
name|reportCloseToParentReaders
parameter_list|()
block|{
synchronized|synchronized
init|(
name|parentReaders
init|)
block|{
for|for
control|(
name|IndexReader
name|parent
range|:
name|parentReaders
control|)
block|{
name|parent
operator|.
name|closedByChild
operator|=
literal|true
expr_stmt|;
comment|// cross memory barrier by a fake write:
name|parent
operator|.
name|refCount
operator|.
name|addAndGet
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// recurse:
name|parent
operator|.
name|reportCloseToParentReaders
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Expert: returns the current refCount for this reader */
DECL|method|getRefCount
specifier|public
specifier|final
name|int
name|getRefCount
parameter_list|()
block|{
comment|// NOTE: don't ensureOpen, so that callers can see
comment|// refCount is 0 (reader is closed)
return|return
name|refCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Expert: increments the refCount of this IndexReader    * instance.  RefCounts are used to determine when a    * reader can be closed safely, i.e. as soon as there are    * no more references.  Be sure to always call a    * corresponding {@link #decRef}, in a finally clause;    * otherwise the reader may never be closed.  Note that    * {@link #close} simply calls decRef(), which means that    * the IndexReader will not really be closed until {@link    * #decRef} has been called for all outstanding    * references.    *    * @see #decRef    * @see #tryIncRef    */
DECL|method|incRef
specifier|public
specifier|final
name|void
name|incRef
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Expert: increments the refCount of this IndexReader    * instance only if the IndexReader has not been closed yet    * and returns<code>true</code> iff the refCount was    * successfully incremented, otherwise<code>false</code>.    * If this method returns<code>false</code> the reader is either    * already closed or is currently been closed. Either way this    * reader instance shouldn't be used by an application unless    *<code>true</code> is returned.    *<p>    * RefCounts are used to determine when a    * reader can be closed safely, i.e. as soon as there are    * no more references.  Be sure to always call a    * corresponding {@link #decRef}, in a finally clause;    * otherwise the reader may never be closed.  Note that    * {@link #close} simply calls decRef(), which means that    * the IndexReader will not really be closed until {@link    * #decRef} has been called for all outstanding    * references.    *    * @see #decRef    * @see #incRef    */
DECL|method|tryIncRef
specifier|public
specifier|final
name|boolean
name|tryIncRef
parameter_list|()
block|{
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|refCount
operator|.
name|get
argument_list|()
operator|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|refCount
operator|.
name|compareAndSet
argument_list|(
name|count
argument_list|,
name|count
operator|+
literal|1
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
comment|/**    * Expert: decreases the refCount of this IndexReader    * instance.  If the refCount drops to 0, then this    * reader is closed.  If an exception is hit, the refCount    * is unchanged.    *    * @throws IOException in case an IOException occurs in  doClose()    *    * @see #incRef    */
DECL|method|decRef
specifier|public
specifier|final
name|void
name|decRef
parameter_list|()
throws|throws
name|IOException
block|{
comment|// only check refcount here (don't call ensureOpen()), so we can
comment|// still close the reader if it was made invalid by a child:
if|if
condition|(
name|refCount
operator|.
name|get
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexReader is closed"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|rc
init|=
name|refCount
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
literal|0
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|doClose
argument_list|()
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
comment|// Put reference back on failure
name|refCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
name|reportCloseToParentReaders
argument_list|()
expr_stmt|;
name|notifyReaderClosedListeners
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"too many decRef calls: refCount is "
operator|+
name|rc
operator|+
literal|" after decrement"
argument_list|)
throw|;
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this IndexReader is closed    */
DECL|method|ensureOpen
specifier|protected
specifier|final
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|refCount
operator|.
name|get
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexReader is closed"
argument_list|)
throw|;
block|}
comment|// the happens before rule on reading the refCount, which must be after the fake write,
comment|// ensures that we see the value:
if|if
condition|(
name|closedByChild
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this IndexReader cannot be used anymore as one of its child readers was closed"
argument_list|)
throw|;
block|}
block|}
comment|/** {@inheritDoc}    *<p>For caching purposes, {@code IndexReader} subclasses are not allowed    * to implement equals/hashCode, so methods are declared final.    * To lookup instances from caches use {@link #getCoreCacheKey} and     * {@link #getCombinedCoreAndDeletesKey}.    */
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|this
operator|==
name|obj
operator|)
return|;
block|}
comment|/** {@inheritDoc}    *<p>For caching purposes, {@code IndexReader} subclasses are not allowed    * to implement equals/hashCode, so methods are declared final.    * To lookup instances from caches use {@link #getCoreCacheKey} and     * {@link #getCombinedCoreAndDeletesKey}.    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Returns a IndexReader reading the index in the given    *  Directory    * @param directory the index directory    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link DirectoryReader#open(Directory)}    */
annotation|@
name|Deprecated
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
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
return|;
block|}
comment|/** Expert: Returns a IndexReader reading the index in the given    *  Directory with the given termInfosIndexDivisor.    * @param directory the index directory    * @param termInfosIndexDivisor Subsamples which indexed    *  terms are loaded into RAM. This has the same effect as {@link    *  IndexWriterConfig#setTermIndexInterval} except that setting    *  must be done at indexing time while this setting can be    *  set per reader.  When set to N, then one in every    *  N*termIndexInterval terms in the index is loaded into    *  memory.  By setting this to a value> 1 you can reduce    *  memory usage, at the expense of higher latency when    *  loading a TermInfo.  The default value is 1.  Set this    *  to -1 to skip loading the terms index entirely.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link DirectoryReader#open(Directory,int)}    */
annotation|@
name|Deprecated
DECL|method|open
specifier|public
specifier|static
name|DirectoryReader
name|open
parameter_list|(
specifier|final
name|Directory
name|directory
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|,
name|termInfosIndexDivisor
argument_list|)
return|;
block|}
comment|/**    * Open a near real time IndexReader from the {@link org.apache.lucene.index.IndexWriter}.    *    * @param writer The IndexWriter to open from    * @param applyAllDeletes If true, all buffered deletes will    * be applied (made visible) in the returned reader.  If    * false, the deletes are not applied but remain buffered    * (in IndexWriter) so that they will be applied in the    * future.  Applying deletes can be costly, so if your app    * can tolerate deleted documents being returned you might    * gain some performance by passing false.    * @return The new IndexReader    * @throws CorruptIndexException    * @throws IOException if there is a low-level IO error    *    * @see DirectoryReader#openIfChanged(DirectoryReader,IndexWriter,boolean)    *    * @lucene.experimental    * @deprecated Use {@link DirectoryReader#open(IndexWriter,boolean)}    */
annotation|@
name|Deprecated
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
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
return|;
block|}
comment|/** Expert: returns an IndexReader reading the index in the given    *  {@link IndexCommit}.    * @param commit the commit point to open    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link DirectoryReader#open(IndexCommit)}    */
annotation|@
name|Deprecated
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
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|commit
argument_list|)
return|;
block|}
comment|/** Expert: returns an IndexReader reading the index in the given    *  {@link IndexCommit} and termInfosIndexDivisor.    * @param commit the commit point to open    * @param termInfosIndexDivisor Subsamples which indexed    *  terms are loaded into RAM. This has the same effect as {@link    *  IndexWriterConfig#setTermIndexInterval} except that setting    *  must be done at indexing time while this setting can be    *  set per reader.  When set to N, then one in every    *  N*termIndexInterval terms in the index is loaded into    *  memory.  By setting this to a value> 1 you can reduce    *  memory usage, at the expense of higher latency when    *  loading a TermInfo.  The default value is 1.  Set this    *  to -1 to skip loading the terms index entirely.    * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    * @deprecated Use {@link DirectoryReader#open(IndexCommit,int)}    */
annotation|@
name|Deprecated
DECL|method|open
specifier|public
specifier|static
name|DirectoryReader
name|open
parameter_list|(
specifier|final
name|IndexCommit
name|commit
parameter_list|,
name|int
name|termInfosIndexDivisor
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|open
argument_list|(
name|commit
argument_list|,
name|termInfosIndexDivisor
argument_list|)
return|;
block|}
comment|/** Retrieve term vectors for this document, or null if    *  term vectors were not indexed.  The returned Fields    *  instance acts like a single-document inverted index    *  (the docID will be 0). */
DECL|method|getTermVectors
specifier|public
specifier|abstract
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Retrieve term vector for this document and field, or    *  null if term vectors were not indexed.  The returned    *  Fields instance acts like a single-document inverted    *  index (the docID will be 0). */
DECL|method|getTermVector
specifier|public
specifier|final
name|Terms
name|getTermVector
parameter_list|(
name|int
name|docID
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|vectors
init|=
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectors
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|vectors
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns the number of documents in this index. */
DECL|method|numDocs
specifier|public
specifier|abstract
name|int
name|numDocs
parameter_list|()
function_decl|;
comment|/** Returns one greater than the largest possible document number.    * This may be used to, e.g., determine how big to allocate an array which    * will have an element for every document number in an index.    */
DECL|method|maxDoc
specifier|public
specifier|abstract
name|int
name|maxDoc
parameter_list|()
function_decl|;
comment|/** Returns the number of deleted documents. */
DECL|method|numDeletedDocs
specifier|public
specifier|final
name|int
name|numDeletedDocs
parameter_list|()
block|{
return|return
name|maxDoc
argument_list|()
operator|-
name|numDocs
argument_list|()
return|;
block|}
comment|/** Expert: visits the fields of a stored document, for    *  custom processing/loading of each field.  If you    *  simply want to load all fields, use {@link    *  #document(int)}.  If you want to load a subset, use    *  {@link DocumentStoredFieldVisitor}.  */
DECL|method|document
specifier|public
specifier|abstract
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
function_decl|;
comment|/**    * Returns the stored fields of the<code>n</code><sup>th</sup>    *<code>Document</code> in this index.  This is just    * sugar for using {@link DocumentStoredFieldVisitor}.    *<p>    *<b>NOTE:</b> for performance reasons, this method does not check if the    * requested document is deleted, and therefore asking for a deleted document    * may yield unspecified results. Usually this is not required, however you    * can test if the doc is deleted by checking the {@link    * Bits} returned from {@link MultiFields#getLiveDocs}.    *    *<b>NOTE:</b> only the content of a field is returned,    * if that field was stored during indexing.  Metadata    * like boost, omitNorm, IndexOptions, tokenized, etc.,    * are not preserved.    *     * @throws CorruptIndexException if the index is corrupt    * @throws IOException if there is a low-level IO error    */
comment|// TODO: we need a separate StoredField, so that the
comment|// Document returned here contains that class not
comment|// IndexableField
DECL|method|document
specifier|public
specifier|final
name|Document
name|document
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|()
decl_stmt|;
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/**    * Like {@link #document(int)} but only loads the specified    * fields.  Note that this is simply sugar for {@link    * DocumentStoredFieldVisitor#DocumentStoredFieldVisitor(Set)}.    */
DECL|method|document
specifier|public
specifier|final
name|Document
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fieldsToLoad
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
specifier|final
name|DocumentStoredFieldVisitor
name|visitor
init|=
operator|new
name|DocumentStoredFieldVisitor
argument_list|(
name|fieldsToLoad
argument_list|)
decl_stmt|;
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
return|return
name|visitor
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/** Returns true if any documents have been deleted */
DECL|method|hasDeletions
specifier|public
specifier|abstract
name|boolean
name|hasDeletions
parameter_list|()
function_decl|;
comment|/**    * Closes files associated with this index.    * Also saves any new deletions to disk.    * No other methods should be called after this has been called.    * @throws IOException if there is a low-level IO error    */
DECL|method|close
specifier|public
specifier|final
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|decRef
argument_list|()
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/** Implements close. */
DECL|method|doClose
specifier|protected
specifier|abstract
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Expert: Returns a the root {@link IndexReaderContext} for this    * {@link IndexReader}'s sub-reader tree. Iff this reader is composed of sub    * readers ,ie. this reader being a composite reader, this method returns a    * {@link CompositeReaderContext} holding the reader's direct children as well as a    * view of the reader tree's atomic leaf contexts. All sub-    * {@link IndexReaderContext} instances referenced from this readers top-level    * context are private to this reader and are not shared with another context    * tree. For example, IndexSearcher uses this API to drive searching by one    * atomic leaf reader at a time. If this reader is not composed of child    * readers, this method returns an {@link AtomicReaderContext}.    *<p>    * Note: Any of the sub-{@link CompositeReaderContext} instances reference from this    * top-level context holds a<code>null</code> {@link CompositeReaderContext#leaves}    * reference. Only the top-level context maintains the convenience leaf-view    * for performance reasons.    *     * @lucene.experimental    */
DECL|method|getTopReaderContext
specifier|public
specifier|abstract
name|IndexReaderContext
name|getTopReaderContext
parameter_list|()
function_decl|;
comment|/** Expert: Returns a key for this IndexReader, so FieldCache/CachingWrapperFilter can find    * it again.    * This key must not have equals()/hashCode() methods, so&quot;equals&quot; means&quot;identical&quot;. */
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
comment|// Don't can ensureOpen since FC calls this (to evict)
comment|// on close
return|return
name|this
return|;
block|}
comment|/** Expert: Returns a key for this IndexReader that also includes deletions,    * so FieldCache/CachingWrapperFilter can find it again.    * This key must not have equals()/hashCode() methods, so&quot;equals&quot; means&quot;identical&quot;. */
DECL|method|getCombinedCoreAndDeletesKey
specifier|public
name|Object
name|getCombinedCoreAndDeletesKey
parameter_list|()
block|{
comment|// Don't can ensureOpen since FC calls this (to evict)
comment|// on close
return|return
name|this
return|;
block|}
comment|/** Returns the number of documents containing the     *<code>term</code>.  This method returns 0 if the term or    * field does not exists.  This method does not take into    * account deleted documents that have not yet been merged    * away. */
DECL|method|docFreq
specifier|public
specifier|final
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docFreq
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the number of documents containing the    *<code>term</code>.  This method returns 0 if the term or    * field does not exists.  This method does not take into    * account deleted documents that have not yet been merged    * away. */
DECL|method|docFreq
specifier|public
specifier|abstract
name|int
name|docFreq
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

