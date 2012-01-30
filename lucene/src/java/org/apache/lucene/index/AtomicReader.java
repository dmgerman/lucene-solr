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
comment|/** IndexReader is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable.<p> Concrete subclasses of IndexReader are usually constructed with a call to  one of the static<code>open()</code> methods, e.g. {@link  #open(Directory)}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral--they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><b>NOTE</b>: for backwards API compatibility, several methods are not listed   as abstract, but have no useful implementations in this base class and   instead always throw UnsupportedOperationException.  Subclasses are   strongly encouraged to override these methods, but in many cases may not   need to.</p><p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment

begin_class
DECL|class|AtomicReader
specifier|public
specifier|abstract
class|class
name|AtomicReader
extends|extends
name|IndexReader
block|{
DECL|field|readerContext
specifier|private
specifier|final
name|AtomicReaderContext
name|readerContext
init|=
operator|new
name|AtomicReaderContext
argument_list|(
name|this
argument_list|)
decl_stmt|;
DECL|method|AtomicReader
specifier|protected
name|AtomicReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
specifier|final
name|AtomicReaderContext
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
comment|/** Returns true if there are norms stored for this field. */
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
comment|// backward compatible implementation.
comment|// SegmentReader has an efficient implementation.
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|normValues
argument_list|(
name|field
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Returns {@link Fields} for this reader.    * This method may return null if the reader has no    * postings.    *    *<p><b>NOTE</b>: if this is a multi reader ({@link    * #getSequentialSubReaders} is not null) then this    * method will throw UnsupportedOperationException.  If    * you really need a {@link Fields} for such a reader,    * use {@link MultiFields#getFields}.  However, for    * performance reasons, it's best to get all sub-readers    * using {@link ReaderUtil#gatherSubReaders} and iterate    * through them yourself. */
DECL|method|fields
specifier|public
specifier|abstract
name|Fields
name|fields
parameter_list|()
throws|throws
name|IOException
function_decl|;
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
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docFreq
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** Returns the number of documents containing the term    *<code>t</code>.  This method returns 0 if the term or    * field does not exists.  This method does not take into    * account deleted documents that have not yet been merged    * away. */
DECL|method|totalTermFreq
specifier|public
specifier|final
name|long
name|totalTermFreq
parameter_list|(
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** This may return null if the field does not exist.*/
DECL|method|terms
specifier|public
specifier|final
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns {@link DocsEnum} for the specified field&    *  term.  This may return null, if either the field or    *  term does not exist. */
DECL|method|termDocsEnum
specifier|public
specifier|final
name|DocsEnum
name|termDocsEnum
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|boolean
name|needsFreqs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
literal|null
argument_list|,
name|needsFreqs
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns {@link DocsAndPositionsEnum} for the specified    *  field& term.  This may return null, if either the    *  field or term does not exist, or needsOffsets is    *  true but offsets were not indexed for this field. */
DECL|method|termPositionsEnum
specifier|public
specifier|final
name|DocsAndPositionsEnum
name|termPositionsEnum
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|boolean
name|needsOffsets
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
assert|assert
name|term
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
literal|null
argument_list|,
name|needsOffsets
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns {@link DocsEnum} for the specified field and    * {@link TermState}. This may return null, if either the field or the term    * does not exists or the {@link TermState} is invalid for the underlying    * implementation.*/
DECL|method|termDocsEnum
specifier|public
specifier|final
name|DocsEnum
name|termDocsEnum
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|,
name|boolean
name|needsFreqs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
literal|null
assert|;
assert|assert
name|field
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|state
argument_list|)
expr_stmt|;
return|return
name|termsEnum
operator|.
name|docs
argument_list|(
name|liveDocs
argument_list|,
literal|null
argument_list|,
name|needsFreqs
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Returns {@link DocsAndPositionsEnum} for the specified field and    * {@link TermState}. This may return null, if either the field or the term    * does not exists, the {@link TermState} is invalid for the underlying    * implementation, or needsOffsets is true but offsets    * were not indexed for this field. */
DECL|method|termPositionsEnum
specifier|public
specifier|final
name|DocsAndPositionsEnum
name|termPositionsEnum
parameter_list|(
name|Bits
name|liveDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|BytesRef
name|term
parameter_list|,
name|TermState
name|state
parameter_list|,
name|boolean
name|needsOffsets
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|state
operator|!=
literal|null
assert|;
assert|assert
name|field
operator|!=
literal|null
assert|;
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Terms
name|terms
init|=
name|fields
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|termsEnum
operator|.
name|seekExact
argument_list|(
name|term
argument_list|,
name|state
argument_list|)
expr_stmt|;
return|return
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
name|liveDocs
argument_list|,
literal|null
argument_list|,
name|needsOffsets
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Returns the number of unique terms (across all fields)    *  in this reader.    *    *  @return number of unique terms or -1 if this count    *  cannot be easily determined (eg Multi*Readers).    *  Instead, you should call {@link    *  #getSequentialSubReaders} and ask each sub reader for    *  its unique term count. */
DECL|method|getUniqueTermCount
specifier|public
specifier|final
name|long
name|getUniqueTermCount
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Fields
name|fields
init|=
name|fields
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|fields
operator|.
name|getUniqueTermCount
argument_list|()
return|;
block|}
comment|/**    * Returns {@link DocValues} for this field.    * This method may return null if the reader has no per-document    * values stored.    *    *<p><b>NOTE</b>: if this is a multi reader ({@link    * #getSequentialSubReaders} is not null) then this    * method will throw UnsupportedOperationException.  If    * you really need {@link DocValues} for such a reader,    * use {@link MultiDocValues#getDocValues(IndexReader,String)}.  However, for    * performance reasons, it's best to get all sub-readers    * using {@link ReaderUtil#gatherSubReaders} and iterate    * through them yourself. */
DECL|method|docValues
specifier|public
specifier|abstract
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|normValues
specifier|public
specifier|abstract
name|DocValues
name|normValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the {@link FieldInfos} describing all fields in    * this reader.  NOTE: do not make any changes to the    * returned FieldInfos!    *    * @lucene.experimental    */
DECL|method|getFieldInfos
specifier|public
specifier|abstract
name|FieldInfos
name|getFieldInfos
parameter_list|()
function_decl|;
comment|/** Returns the {@link Bits} representing live (not    *  deleted) docs.  A set bit indicates the doc ID has not    *  been deleted.  If this method returns null it means    *  there are no deleted documents (all documents are    *  live).    *    *  The returned instance has been safely published for    *  use by multiple threads without additional    *  synchronization.    */
DECL|method|getLiveDocs
specifier|public
specifier|abstract
name|Bits
name|getLiveDocs
parameter_list|()
function_decl|;
block|}
end_class

end_unit

