begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|Comparator
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|FieldInfo
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
name|FieldInfos
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
name|Fields
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
name|FieldsEnum
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
name|MergeState
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
name|Terms
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
name|TermsEnum
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
name|DataInput
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

begin_comment
comment|/**  * Codec API for writing term vectors:  *<p>  *<ol>  *<li>For every document, {@link #startDocument(int)} is called,  *       informing the Codec how many fields will be written.  *<li>{@link #startField(FieldInfo, int, boolean, boolean)} is called for   *       each field in the document, informing the codec how many terms  *       will be written for that field, and whether or not positions  *       or offsets are enabled.  *<li>Within each field, {@link #startTerm(BytesRef, int)} is called  *       for each term.  *<li>If offsets and/or positions are enabled, then   *       {@link #addPosition(int, int, int)} will be called for each term  *       occurrence.  *<li>After all documents have been written, {@link #finish(int)}   *       is called for verification/sanity-checks.  *<li>Finally the writer is closed ({@link #close()})  *</ol>  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|TermVectorsWriter
specifier|public
specifier|abstract
class|class
name|TermVectorsWriter
implements|implements
name|Closeable
block|{
comment|/** Called before writing the term vectors of the document.    *  {@link #startField(FieldInfo, int, boolean, boolean)} will     *  be called<code>numVectorFields</code> times. Note that if term     *  vectors are enabled, this is called even if the document     *  has no vector fields, in this case<code>numVectorFields</code>     *  will be zero. */
DECL|method|startDocument
specifier|public
specifier|abstract
name|void
name|startDocument
parameter_list|(
name|int
name|numVectorFields
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called before writing the terms of the field.    *  {@link #startTerm(BytesRef, int)} will be called<code>numTerms</code> times. */
DECL|method|startField
specifier|public
specifier|abstract
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|positions
parameter_list|,
name|boolean
name|offsets
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Adds a term and its term frequency<code>freq</code>.    * If this field has positions and/or offsets enabled, then    * {@link #addPosition(int, int, int)} will be called     *<code>freq</code> times respectively.    */
DECL|method|startTerm
specifier|public
specifier|abstract
name|void
name|startTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Adds a term position and offsets */
DECL|method|addPosition
specifier|public
specifier|abstract
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Aborts writing entirely, implementation should remove    *  any partially-written files, etc. */
DECL|method|abort
specifier|public
specifier|abstract
name|void
name|abort
parameter_list|()
function_decl|;
comment|/** Called before {@link #close()}, passing in the number    *  of documents that were written. Note that this is     *  intentionally redundant (equivalent to the number of    *  calls to {@link #startDocument(int)}, but a Codec should    *  check that this is the case to detect the JRE bug described     *  in LUCENE-1282. */
DECL|method|finish
specifier|public
specifier|abstract
name|void
name|finish
parameter_list|(
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Called by IndexWriter when writing new segments.    *<p>    * This is an expert API that allows the codec to consume     * positions and offsets directly from the indexer.    *<p>    * The default implementation calls {@link #addPosition(int, int, int)},    * but subclasses can override this if they want to efficiently write     * all the positions, then all the offsets, for example.    *<p>    * NOTE: This API is extremely expert and subject to change or removal!!!    * @lucene.internal    */
comment|// TODO: we should probably nuke this and make a more efficient 4.x format
comment|// PreFlex-RW could then be slow and buffer (its only used in tests...)
DECL|method|addProx
specifier|public
name|void
name|addProx
parameter_list|(
name|int
name|numProx
parameter_list|,
name|DataInput
name|positions
parameter_list|,
name|DataInput
name|offsets
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|position
init|=
literal|0
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
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
name|numProx
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|startOffset
decl_stmt|;
specifier|final
name|int
name|endOffset
decl_stmt|;
if|if
condition|(
name|positions
operator|==
literal|null
condition|)
block|{
name|position
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|position
operator|+=
name|positions
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
block|{
name|startOffset
operator|=
name|endOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|startOffset
operator|=
name|lastOffset
operator|+
name|offsets
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|startOffset
operator|+
name|offsets
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|lastOffset
operator|=
name|endOffset
expr_stmt|;
block|}
name|addPosition
argument_list|(
name|position
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Merges in the term vectors from the readers in     *<code>mergeState</code>. The default implementation skips    *  over deleted documents, and uses {@link #startDocument(int)},    *  {@link #startField(FieldInfo, int, boolean, boolean)},     *  {@link #startTerm(BytesRef, int)}, {@link #addPosition(int, int, int)},    *  and {@link #finish(int)},    *  returning the number of documents that were written.    *  Implementations can override this method for more sophisticated    *  merging (bulk-byte copying, etc). */
DECL|method|merge
specifier|public
name|int
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|docCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|liveDocs
decl_stmt|;
for|for
control|(
name|int
name|docID
init|=
literal|0
init|;
name|docID
operator|<
name|maxDoc
condition|;
name|docID
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
comment|// skip deleted docs
continue|continue;
block|}
comment|// NOTE: it's very important to first assign to vectors then pass it to
comment|// termVectorsWriter.addAllDocVectors; see LUCENE-1282
name|Fields
name|vectors
init|=
name|reader
operator|.
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|addAllDocVectors
argument_list|(
name|vectors
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
block|}
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
return|return
name|docCount
return|;
block|}
comment|/** Safe (but, slowish) default method to write every    *  vector field in the document.  This default    *  implementation requires that the vectors implement    *  both Fields.getUniqueFieldCount and    *  Terms.getUniqueTermCount. */
DECL|method|addAllDocVectors
specifier|protected
specifier|final
name|void
name|addAllDocVectors
parameter_list|(
name|Fields
name|vectors
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|vectors
operator|==
literal|null
condition|)
block|{
name|startDocument
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|numFields
init|=
name|vectors
operator|.
name|getUniqueFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|numFields
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"vectors.getUniqueFieldCount() must be implemented (it returned -1)"
argument_list|)
throw|;
block|}
name|startDocument
argument_list|(
name|numFields
argument_list|)
expr_stmt|;
specifier|final
name|FieldsEnum
name|fieldsEnum
init|=
name|vectors
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|fieldName
decl_stmt|;
name|String
name|lastFieldName
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|fieldName
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
assert|assert
name|lastFieldName
operator|==
literal|null
operator|||
name|fieldName
operator|.
name|compareTo
argument_list|(
name|lastFieldName
argument_list|)
operator|>
literal|0
operator|:
literal|"lastFieldName="
operator|+
name|lastFieldName
operator|+
literal|" fieldName="
operator|+
name|fieldName
assert|;
name|lastFieldName
operator|=
name|fieldName
expr_stmt|;
specifier|final
name|Terms
name|terms
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
comment|// FieldsEnum shouldn't lie...
continue|continue;
block|}
specifier|final
name|int
name|numTerms
init|=
operator|(
name|int
operator|)
name|terms
operator|.
name|getUniqueTermCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|numTerms
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"vector.getUniqueTermCount() must be implemented (it returned -1)"
argument_list|)
throw|;
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
name|DocsAndPositionsEnum
name|docsAndPositionsEnum
init|=
literal|null
decl_stmt|;
name|boolean
name|startedField
init|=
literal|false
decl_stmt|;
comment|// NOTE: this is tricky, because TermVectors allow
comment|// indexing offsets but NOT positions.  So we must
comment|// lazily init the field by checking whether first
comment|// position we see is -1 or not.
name|int
name|termCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|termsEnum
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|termCount
operator|++
expr_stmt|;
specifier|final
name|int
name|freq
init|=
operator|(
name|int
operator|)
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|startedField
condition|)
block|{
name|startTerm
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|,
name|freq
argument_list|)
expr_stmt|;
block|}
comment|// TODO: we need a "query" API where we can ask (via
comment|// flex API) what this term was indexed with...
comment|// Both positions& offsets:
name|docsAndPositionsEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|hasOffsets
decl_stmt|;
name|boolean
name|hasPositions
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|docsAndPositionsEnum
operator|==
literal|null
condition|)
block|{
comment|// Fallback: no offsets
name|docsAndPositionsEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|hasOffsets
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|hasOffsets
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|docsAndPositionsEnum
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|docID
init|=
name|docsAndPositionsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
assert|assert
name|docID
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
assert|;
assert|assert
name|docsAndPositionsEnum
operator|.
name|freq
argument_list|()
operator|==
name|freq
assert|;
for|for
control|(
name|int
name|posUpto
init|=
literal|0
init|;
name|posUpto
operator|<
name|freq
condition|;
name|posUpto
operator|++
control|)
block|{
specifier|final
name|int
name|pos
init|=
name|docsAndPositionsEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|startedField
condition|)
block|{
assert|assert
name|numTerms
operator|>
literal|0
assert|;
name|hasPositions
operator|=
name|pos
operator|!=
operator|-
literal|1
expr_stmt|;
name|startField
argument_list|(
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|hasPositions
argument_list|,
name|hasOffsets
argument_list|)
expr_stmt|;
name|startTerm
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|startedField
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|int
name|startOffset
decl_stmt|;
specifier|final
name|int
name|endOffset
decl_stmt|;
if|if
condition|(
name|hasOffsets
condition|)
block|{
name|startOffset
operator|=
name|docsAndPositionsEnum
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|endOffset
operator|=
name|docsAndPositionsEnum
operator|.
name|endOffset
argument_list|()
expr_stmt|;
assert|assert
name|startOffset
operator|!=
operator|-
literal|1
assert|;
assert|assert
name|endOffset
operator|!=
operator|-
literal|1
assert|;
block|}
else|else
block|{
name|startOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|endOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
assert|assert
operator|!
name|hasPositions
operator|||
name|pos
operator|>=
literal|0
assert|;
name|addPosition
argument_list|(
name|pos
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|startedField
condition|)
block|{
assert|assert
name|numTerms
operator|>
literal|0
assert|;
name|startField
argument_list|(
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|hasPositions
argument_list|,
name|hasOffsets
argument_list|)
expr_stmt|;
name|startTerm
argument_list|(
name|termsEnum
operator|.
name|term
argument_list|()
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|startedField
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|termCount
operator|==
name|numTerms
assert|;
block|}
block|}
comment|/** Return the BytesRef Comparator used to sort terms    *  before feeding to this API. */
DECL|method|getComparator
specifier|public
specifier|abstract
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

