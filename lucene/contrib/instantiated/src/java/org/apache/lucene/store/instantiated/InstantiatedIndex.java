begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package

begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ArrayList
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
name|HashMap
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
name|Set
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
name|analysis
operator|.
name|Analyzer
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
name|Fieldable
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
name|IndexReader
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
name|MultiNorms
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
name|Term
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
name|MultiFields
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
name|TermPositionVector
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
name|util
operator|.
name|BitVector
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
name|CharsRef
import|;
end_import

begin_comment
comment|/**  * Represented as a coupled graph of class instances, this  * all-in-memory index store implementation delivers search  * results up to a 100 times faster than the file-centric RAMDirectory  * at the cost of greater RAM consumption.  *<p>  * @lucene.experimental  *<p>  * There are no read and write locks in this store.  * {@link InstantiatedIndexReader} {@link InstantiatedIndexReader#isCurrent()} all the time  * and {@link org.apache.lucene.store.instantiated.InstantiatedIndexWriter}  * will attempt to update instances of the object graph in memory  * at the same time as a searcher is reading from it.  *  * Consider using InstantiatedIndex as if it was immutable.  */
end_comment

begin_class
DECL|class|InstantiatedIndex
specifier|public
class|class
name|InstantiatedIndex
implements|implements
name|Closeable
block|{
DECL|field|version
specifier|private
name|long
name|version
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|documentsByNumber
specifier|private
name|InstantiatedDocument
index|[]
name|documentsByNumber
decl_stmt|;
DECL|field|deletedDocuments
specifier|private
name|BitVector
name|deletedDocuments
decl_stmt|;
DECL|field|termsByFieldAndText
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InstantiatedTerm
argument_list|>
argument_list|>
name|termsByFieldAndText
decl_stmt|;
DECL|field|orderedTerms
specifier|private
name|InstantiatedTerm
index|[]
name|orderedTerms
decl_stmt|;
DECL|field|normsByFieldNameAndDocumentNumber
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|normsByFieldNameAndDocumentNumber
decl_stmt|;
DECL|field|fieldSettings
specifier|private
name|FieldSettings
name|fieldSettings
decl_stmt|;
comment|/**    * Creates an empty instantiated index for you to fill with data using an {@link org.apache.lucene.store.instantiated.InstantiatedIndexWriter}.     */
DECL|method|InstantiatedIndex
specifier|public
name|InstantiatedIndex
parameter_list|()
block|{
name|initialize
argument_list|()
expr_stmt|;
block|}
DECL|method|initialize
name|void
name|initialize
parameter_list|()
block|{
comment|// todo: clear index without loosing memory (uncouple stuff)
name|termsByFieldAndText
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InstantiatedTerm
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|fieldSettings
operator|=
operator|new
name|FieldSettings
argument_list|()
expr_stmt|;
name|orderedTerms
operator|=
operator|new
name|InstantiatedTerm
index|[
literal|0
index|]
expr_stmt|;
name|documentsByNumber
operator|=
operator|new
name|InstantiatedDocument
index|[
literal|0
index|]
expr_stmt|;
name|normsByFieldNameAndDocumentNumber
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new instantiated index that looks just like the index in a specific state as represented by a reader.    *    * @param sourceIndexReader the source index this new instantiated index will be copied from.    * @throws IOException if the source index is not optimized, or when accessing the source.    */
DECL|method|InstantiatedIndex
specifier|public
name|InstantiatedIndex
parameter_list|(
name|IndexReader
name|sourceIndexReader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|sourceIndexReader
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new instantiated index that looks just like the index in a specific state as represented by a reader.    *    * @param sourceIndexReader the source index this new instantiated index will be copied from.    * @param fields fields to be added, or null for all    * @throws IOException if the source index is not optimized, or when accessing the source.    */
DECL|method|InstantiatedIndex
specifier|public
name|InstantiatedIndex
parameter_list|(
name|IndexReader
name|sourceIndexReader
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|sourceIndexReader
operator|.
name|isOptimized
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
literal|"Source index is not optimized."
operator|)
argument_list|)
expr_stmt|;
comment|//throw new IOException("Source index is not optimized.");
block|}
name|initialize
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|allFieldNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|ALL
argument_list|)
decl_stmt|;
comment|// load field options
name|Collection
argument_list|<
name|String
argument_list|>
name|indexedNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|indexedNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|indexed
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|indexedNoVecNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_NO_TERMVECTOR
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|indexedNoVecNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storeTermVector
operator|=
literal|false
expr_stmt|;
name|setting
operator|.
name|indexed
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|indexedVecNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|INDEXED_WITH_TERMVECTOR
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|indexedVecNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
name|setting
operator|.
name|indexed
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|payloadNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|STORES_PAYLOADS
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|payloadNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|termVecNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|termVecNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storeTermVector
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|termVecOffsetNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_OFFSET
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|termVecOffsetNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|termVecPosNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|termVecPosNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|termVecPosOffNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|TERMVECTOR_WITH_POSITION_OFFSET
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|termVecPosOffNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|storeOffsetWithTermVector
operator|=
literal|true
expr_stmt|;
name|setting
operator|.
name|storePositionWithTermVector
operator|=
literal|true
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|unindexedNames
init|=
name|sourceIndexReader
operator|.
name|getFieldNames
argument_list|(
name|IndexReader
operator|.
name|FieldOption
operator|.
name|UNINDEXED
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|unindexedNames
control|)
block|{
name|FieldSetting
name|setting
init|=
name|fieldSettings
operator|.
name|get
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|setting
operator|.
name|indexed
operator|=
literal|false
expr_stmt|;
block|}
name|documentsByNumber
operator|=
operator|new
name|InstantiatedDocument
index|[
name|sourceIndexReader
operator|.
name|maxDoc
argument_list|()
index|]
expr_stmt|;
if|if
condition|(
name|sourceIndexReader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|deletedDocuments
operator|=
operator|new
name|BitVector
argument_list|(
name|sourceIndexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create documents
specifier|final
name|Bits
name|delDocs
init|=
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|sourceIndexReader
argument_list|)
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
name|sourceIndexReader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|delDocs
operator|!=
literal|null
operator|&&
name|delDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|deletedDocuments
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|InstantiatedDocument
name|document
init|=
operator|new
name|InstantiatedDocument
argument_list|()
decl_stmt|;
comment|// copy stored fields from source reader
name|Document
name|sourceDocument
init|=
name|sourceIndexReader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|sourceDocument
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|document
operator|.
name|getDocument
argument_list|()
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
name|document
operator|.
name|setDocumentNumber
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|documentsByNumber
index|[
name|i
index|]
operator|=
name|document
expr_stmt|;
for|for
control|(
name|Fieldable
name|field
range|:
name|document
operator|.
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|isTermVectorStored
argument_list|()
condition|)
block|{
if|if
condition|(
name|document
operator|.
name|getVectorSpace
argument_list|()
operator|==
literal|null
condition|)
block|{
name|document
operator|.
name|setVectorSpace
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|document
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|InstantiatedTermDocumentInformation
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// create norms
for|for
control|(
name|String
name|fieldName
range|:
name|allFieldNames
control|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|byte
name|norms
index|[]
init|=
name|MultiNorms
operator|.
name|norms
argument_list|(
name|sourceIndexReader
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
name|getNormsByFieldNameAndDocumentNumber
argument_list|()
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|norms
argument_list|)
expr_stmt|;
block|}
block|}
comment|// create terms
for|for
control|(
name|String
name|fieldName
range|:
name|allFieldNames
control|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|getTermsByFieldAndText
argument_list|()
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|InstantiatedTerm
argument_list|>
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|InstantiatedTerm
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|InstantiatedTerm
argument_list|>
argument_list|(
literal|5000
operator|*
name|getTermsByFieldAndText
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Fields
name|fieldsC
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|sourceIndexReader
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldsC
operator|!=
literal|null
condition|)
block|{
name|FieldsEnum
name|fieldsEnum
init|=
name|fieldsC
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|field
decl_stmt|;
specifier|final
name|CharsRef
name|spare
init|=
operator|new
name|CharsRef
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|field
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
if|if
condition|(
name|fields
operator|==
literal|null
operator|||
name|fields
operator|.
name|contains
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
name|BytesRef
name|text
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|termText
init|=
name|text
operator|.
name|utf8ToChars
argument_list|(
name|spare
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|InstantiatedTerm
name|instantiatedTerm
init|=
operator|new
name|InstantiatedTerm
argument_list|(
name|field
argument_list|,
name|termText
argument_list|)
decl_stmt|;
specifier|final
name|long
name|totalTermFreq
init|=
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|totalTermFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|instantiatedTerm
operator|.
name|addPositionsCount
argument_list|(
name|totalTermFreq
argument_list|)
expr_stmt|;
block|}
name|getTermsByFieldAndText
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|.
name|put
argument_list|(
name|termText
argument_list|,
name|instantiatedTerm
argument_list|)
expr_stmt|;
name|instantiatedTerm
operator|.
name|setTermIndex
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|terms
operator|.
name|add
argument_list|(
name|instantiatedTerm
argument_list|)
expr_stmt|;
name|instantiatedTerm
operator|.
name|setAssociatedDocuments
argument_list|(
operator|new
name|InstantiatedTermDocumentInformation
index|[
name|termsEnum
operator|.
name|docFreq
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|orderedTerms
operator|=
name|terms
operator|.
name|toArray
argument_list|(
operator|new
name|InstantiatedTerm
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
comment|// create term-document informations
for|for
control|(
name|InstantiatedTerm
name|term
range|:
name|orderedTerms
control|)
block|{
name|DocsAndPositionsEnum
name|termPositions
init|=
name|MultiFields
operator|.
name|getTermPositionsEnum
argument_list|(
name|sourceIndexReader
argument_list|,
name|MultiFields
operator|.
name|getDeletedDocs
argument_list|(
name|sourceIndexReader
argument_list|)
argument_list|,
name|term
operator|.
name|getTerm
argument_list|()
operator|.
name|field
argument_list|()
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|term
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|position
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|termPositions
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|InstantiatedDocument
name|document
init|=
name|documentsByNumber
index|[
name|termPositions
operator|.
name|docID
argument_list|()
index|]
decl_stmt|;
name|byte
index|[]
index|[]
name|payloads
init|=
operator|new
name|byte
index|[
name|termPositions
operator|.
name|freq
argument_list|()
index|]
index|[]
decl_stmt|;
name|int
index|[]
name|positions
init|=
operator|new
name|int
index|[
name|termPositions
operator|.
name|freq
argument_list|()
index|]
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
name|termPositions
operator|.
name|freq
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|=
name|termPositions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
if|if
condition|(
name|termPositions
operator|.
name|hasPayload
argument_list|()
condition|)
block|{
name|BytesRef
name|br
init|=
name|termPositions
operator|.
name|getPayload
argument_list|()
decl_stmt|;
name|payloads
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|br
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|br
operator|.
name|bytes
argument_list|,
name|br
operator|.
name|offset
argument_list|,
name|payloads
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|br
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
name|InstantiatedTermDocumentInformation
name|termDocumentInformation
init|=
operator|new
name|InstantiatedTermDocumentInformation
argument_list|(
name|term
argument_list|,
name|document
argument_list|,
name|positions
argument_list|,
name|payloads
argument_list|)
decl_stmt|;
name|term
operator|.
name|getAssociatedDocuments
argument_list|()
index|[
name|position
operator|++
index|]
operator|=
name|termDocumentInformation
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|getVectorSpace
argument_list|()
operator|!=
literal|null
operator|&&
name|document
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|containsKey
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
condition|)
block|{
name|document
operator|.
name|getVectorSpace
argument_list|()
operator|.
name|get
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|termDocumentInformation
argument_list|)
expr_stmt|;
block|}
comment|//        termDocumentInformation.setIndexFromTerm(indexFromTerm++);
block|}
block|}
comment|// load offsets to term-document informations
for|for
control|(
name|InstantiatedDocument
name|document
range|:
name|getDocumentsByNumber
argument_list|()
control|)
block|{
if|if
condition|(
name|document
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// deleted
block|}
for|for
control|(
name|Fieldable
name|field
range|:
name|document
operator|.
name|getDocument
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|isTermVectorStored
argument_list|()
operator|&&
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
condition|)
block|{
name|TermPositionVector
name|termPositionVector
init|=
operator|(
name|TermPositionVector
operator|)
name|sourceIndexReader
operator|.
name|getTermFreqVector
argument_list|(
name|document
operator|.
name|getDocumentNumber
argument_list|()
argument_list|,
name|field
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|termPositionVector
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
name|termPositionVector
operator|.
name|getTerms
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|token
init|=
name|termPositionVector
operator|.
name|getTerms
argument_list|()
index|[
name|i
index|]
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|InstantiatedTerm
name|term
init|=
name|findTerm
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|token
argument_list|)
decl_stmt|;
name|InstantiatedTermDocumentInformation
name|termDocumentInformation
init|=
name|term
operator|.
name|getAssociatedDocument
argument_list|(
name|document
operator|.
name|getDocumentNumber
argument_list|()
argument_list|)
decl_stmt|;
name|termDocumentInformation
operator|.
name|setTermOffsets
argument_list|(
name|termPositionVector
operator|.
name|getOffsets
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|indexWriterFactory
specifier|public
name|InstantiatedIndexWriter
name|indexWriterFactory
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|InstantiatedIndexWriter
argument_list|(
name|this
argument_list|,
name|analyzer
argument_list|,
name|create
argument_list|)
return|;
block|}
DECL|method|indexReaderFactory
specifier|public
name|InstantiatedIndexReader
name|indexReaderFactory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|InstantiatedIndexReader
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// todo: decouple everything
block|}
DECL|method|findTerm
name|InstantiatedTerm
name|findTerm
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
name|findTerm
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|,
name|term
operator|.
name|text
argument_list|()
argument_list|)
return|;
block|}
DECL|method|findTerm
name|InstantiatedTerm
name|findTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|InstantiatedTerm
argument_list|>
name|termsByField
init|=
name|termsByFieldAndText
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|termsByField
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|termsByField
operator|.
name|get
argument_list|(
name|text
argument_list|)
return|;
block|}
block|}
DECL|method|getTermsByFieldAndText
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InstantiatedTerm
argument_list|>
argument_list|>
name|getTermsByFieldAndText
parameter_list|()
block|{
return|return
name|termsByFieldAndText
return|;
block|}
DECL|method|getOrderedTerms
specifier|public
name|InstantiatedTerm
index|[]
name|getOrderedTerms
parameter_list|()
block|{
return|return
name|orderedTerms
return|;
block|}
DECL|method|getDocumentsByNumber
specifier|public
name|InstantiatedDocument
index|[]
name|getDocumentsByNumber
parameter_list|()
block|{
return|return
name|documentsByNumber
return|;
block|}
DECL|method|getNormsByFieldNameAndDocumentNumber
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|getNormsByFieldNameAndDocumentNumber
parameter_list|()
block|{
return|return
name|normsByFieldNameAndDocumentNumber
return|;
block|}
DECL|method|setNormsByFieldNameAndDocumentNumber
name|void
name|setNormsByFieldNameAndDocumentNumber
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|normsByFieldNameAndDocumentNumber
parameter_list|)
block|{
name|this
operator|.
name|normsByFieldNameAndDocumentNumber
operator|=
name|normsByFieldNameAndDocumentNumber
expr_stmt|;
block|}
DECL|method|getDeletedDocuments
specifier|public
name|BitVector
name|getDeletedDocuments
parameter_list|()
block|{
return|return
name|deletedDocuments
return|;
block|}
DECL|method|setDeletedDocuments
name|void
name|setDeletedDocuments
parameter_list|(
name|BitVector
name|deletedDocuments
parameter_list|)
block|{
name|this
operator|.
name|deletedDocuments
operator|=
name|deletedDocuments
expr_stmt|;
block|}
DECL|method|setOrderedTerms
name|void
name|setOrderedTerms
parameter_list|(
name|InstantiatedTerm
index|[]
name|orderedTerms
parameter_list|)
block|{
name|this
operator|.
name|orderedTerms
operator|=
name|orderedTerms
expr_stmt|;
block|}
DECL|method|setDocumentsByNumber
name|void
name|setDocumentsByNumber
parameter_list|(
name|InstantiatedDocument
index|[]
name|documentsByNumber
parameter_list|)
block|{
name|this
operator|.
name|documentsByNumber
operator|=
name|documentsByNumber
expr_stmt|;
block|}
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|setVersion
name|void
name|setVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|getFieldSettings
name|FieldSettings
name|getFieldSettings
parameter_list|()
block|{
return|return
name|fieldSettings
return|;
block|}
block|}
end_class

end_unit

