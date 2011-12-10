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
name|MapBackedSet
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
name|Collection
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
name|Comparator
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  A<code>FilterIndexReader</code> contains another IndexReader, which it  * uses as its basic source of data, possibly transforming the data along the  * way or providing additional functionality. The class  *<code>FilterIndexReader</code> itself simply implements all abstract methods  * of<code>IndexReader</code> with versions that pass all requests to the  * contained index reader. Subclasses of<code>FilterIndexReader</code> may  * further override some of these methods and may also provide additional  * methods and fields.  *<p><b>Note:</b> The default implementation of {@link FilterIndexReader#doOpenIfChanged}  * throws {@link UnsupportedOperationException} (like the base class),  * so it's not possible to reopen a<code>FilterIndexReader</code>.  * To reopen, you have to first reopen the underlying reader  * and wrap it again with the custom filter.  */
end_comment

begin_class
DECL|class|FilterIndexReader
specifier|public
class|class
name|FilterIndexReader
extends|extends
name|IndexReader
block|{
comment|/** Base class for filtering {@link Fields}    *  implementations. */
DECL|class|FilterFields
specifier|public
specifier|static
class|class
name|FilterFields
extends|extends
name|Fields
block|{
DECL|field|in
specifier|protected
name|Fields
name|in
decl_stmt|;
DECL|method|FilterFields
specifier|public
name|FilterFields
parameter_list|(
name|Fields
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|FieldsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueFieldCount
specifier|public
name|int
name|getUniqueFieldCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getUniqueFieldCount
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link Terms}    *  implementations. */
DECL|class|FilterTerms
specifier|public
specifier|static
class|class
name|FilterTerms
extends|extends
name|Terms
block|{
DECL|field|in
specifier|protected
name|Terms
name|in
decl_stmt|;
DECL|method|FilterTerms
specifier|public
name|FilterTerms
parameter_list|(
name|Terms
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|(
name|TermsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|iterator
argument_list|(
name|reuse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUniqueTermCount
specifier|public
name|long
name|getUniqueTermCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getUniqueTermCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getSumTotalTermFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getSumDocFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|int
name|getDocCount
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getDocCount
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link TermsEnum} implementations. */
DECL|class|FilterFieldsEnum
specifier|public
specifier|static
class|class
name|FilterFieldsEnum
extends|extends
name|FieldsEnum
block|{
DECL|field|in
specifier|protected
name|FieldsEnum
name|in
decl_stmt|;
DECL|method|FilterFieldsEnum
specifier|public
name|FilterFieldsEnum
parameter_list|(
name|FieldsEnum
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|String
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|terms
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link TermsEnum} implementations. */
DECL|class|FilterTermsEnum
specifier|public
specifier|static
class|class
name|FilterTermsEnum
extends|extends
name|TermsEnum
block|{
DECL|field|in
specifier|protected
name|TermsEnum
name|in
decl_stmt|;
DECL|method|FilterTermsEnum
specifier|public
name|FilterTermsEnum
parameter_list|(
name|TermsEnum
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|boolean
name|seekExact
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|seekExact
argument_list|(
name|text
argument_list|,
name|useCache
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekCeil
specifier|public
name|SeekStatus
name|seekCeil
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|boolean
name|useCache
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|seekCeil
argument_list|(
name|text
argument_list|,
name|useCache
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|long
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seekExact
argument_list|(
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|next
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|term
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|ord
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalTermFreq
specifier|public
name|long
name|totalTermFreq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docs
specifier|public
name|DocsEnum
name|docs
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsEnum
name|reuse
parameter_list|,
name|boolean
name|needsFreqs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|,
name|needsFreqs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsAndPositions
specifier|public
name|DocsAndPositionsEnum
name|docsAndPositions
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|DocsAndPositionsEnum
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
name|reuse
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seekExact
specifier|public
name|void
name|seekExact
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termState
specifier|public
name|TermState
name|termState
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|termState
argument_list|()
return|;
block|}
block|}
comment|/** Base class for filtering {@link DocsEnum} implementations. */
DECL|class|FilterDocsEnum
specifier|public
specifier|static
class|class
name|FilterDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|in
specifier|protected
name|DocsEnum
name|in
decl_stmt|;
DECL|method|FilterDocsEnum
specifier|public
name|FilterDocsEnum
parameter_list|(
name|DocsEnum
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
comment|/** Base class for filtering {@link DocsAndPositionsEnum} implementations. */
DECL|class|FilterDocsAndPositionsEnum
specifier|public
specifier|static
class|class
name|FilterDocsAndPositionsEnum
extends|extends
name|DocsAndPositionsEnum
block|{
DECL|field|in
specifier|protected
name|DocsAndPositionsEnum
name|in
decl_stmt|;
DECL|method|FilterDocsAndPositionsEnum
specifier|public
name|FilterDocsAndPositionsEnum
parameter_list|(
name|DocsAndPositionsEnum
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|in
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|in
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|nextPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getPayload
specifier|public
name|BytesRef
name|getPayload
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getPayload
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayload
specifier|public
name|boolean
name|hasPayload
parameter_list|()
block|{
return|return
name|in
operator|.
name|hasPayload
argument_list|()
return|;
block|}
block|}
DECL|field|in
specifier|protected
name|IndexReader
name|in
decl_stmt|;
comment|/**    *<p>Construct a FilterIndexReader based on the specified base reader.    *<p>Note that base reader is closed if this FilterIndexReader is closed.</p>    * @param in specified base reader.    */
DECL|method|FilterIndexReader
specifier|public
name|FilterIndexReader
parameter_list|(
name|IndexReader
name|in
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|readerFinishedListeners
operator|=
operator|new
name|MapBackedSet
argument_list|<
name|ReaderFinishedListener
argument_list|>
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|ReaderFinishedListener
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|directory
argument_list|()
return|;
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
name|in
operator|.
name|getLiveDocs
argument_list|()
return|;
block|}
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
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
name|in
operator|.
name|numDocs
argument_list|()
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
name|in
operator|.
name|maxDoc
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
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
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|hasDeletions
argument_list|()
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
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
parameter_list|(
name|String
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|norms
argument_list|(
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|docFreq
argument_list|(
name|field
argument_list|,
name|t
argument_list|)
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
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|(
name|IndexReader
operator|.
name|FieldOption
name|fieldNames
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|getVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|in
operator|.
name|isCurrent
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSequentialSubReaders
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
name|in
operator|.
name|getSequentialSubReaders
argument_list|()
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
name|in
operator|.
name|getTopReaderContext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCommitUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getCommitUserData
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCommitUserData
argument_list|()
return|;
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
name|in
operator|.
name|fields
argument_list|()
return|;
block|}
comment|/** If the subclass of FilteredIndexReader modifies the    *  contents of the FieldCache, you must override this    *  method to provide a different key */
annotation|@
name|Override
DECL|method|getCoreCacheKey
specifier|public
name|Object
name|getCoreCacheKey
parameter_list|()
block|{
return|return
name|in
operator|.
name|getCoreCacheKey
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"FilterReader("
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addReaderFinishedListener
specifier|public
name|void
name|addReaderFinishedListener
parameter_list|(
name|ReaderFinishedListener
name|listener
parameter_list|)
block|{
name|super
operator|.
name|addReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|in
operator|.
name|addReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeReaderFinishedListener
specifier|public
name|void
name|removeReaderFinishedListener
parameter_list|(
name|ReaderFinishedListener
name|listener
parameter_list|)
block|{
name|super
operator|.
name|removeReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|in
operator|.
name|removeReaderFinishedListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
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
return|return
name|in
operator|.
name|docValues
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexCommit
specifier|public
name|IndexCommit
name|getIndexCommit
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|getIndexCommit
argument_list|()
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
name|in
operator|.
name|getTermInfosIndexDivisor
argument_list|()
return|;
block|}
block|}
end_class

end_unit

