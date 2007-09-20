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
name|FieldSelector
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
name|FieldSelectorResult
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
name|*
import|;
end_import

begin_comment
comment|/** An IndexReader which reads multiple, parallel indexes.  Each index added  * must have the same number of documents, but typically each contains  * different fields.  Each document contains the union of the fields of all  * documents with the same document number.  When searching, matches for a  * query term are from the first index added that has the field.  *  *<p>This is useful, e.g., with collections that have large fields which  * change rarely and small fields that change more frequently.  The smaller  * fields may be re-indexed in a new index and both indexes may be searched  * together.  *  *<p><strong>Warning:</strong> It is up to you to make sure all indexes  * are created and modified the same way. For example, if you add  * documents to one index, you need to add the same documents in the  * same order to the other indexes.<em>Failure to do so will result in  * undefined behavior</em>.  */
end_comment

begin_class
DECL|class|ParallelReader
specifier|public
class|class
name|ParallelReader
extends|extends
name|IndexReader
block|{
DECL|field|readers
specifier|private
name|List
name|readers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|fieldToReader
specifier|private
name|SortedMap
name|fieldToReader
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
DECL|field|readerToFields
specifier|private
name|Map
name|readerToFields
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|storedFieldReaders
specifier|private
name|List
name|storedFieldReaders
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|int
name|maxDoc
decl_stmt|;
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|hasDeletions
specifier|private
name|boolean
name|hasDeletions
decl_stmt|;
comment|/** Construct a ParallelReader. */
DECL|method|ParallelReader
specifier|public
name|ParallelReader
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Add an IndexReader.   * @throws IOException if there is a low-level IO error   */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|reader
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Add an IndexReader whose stored fields will not be returned.  This can   * accellerate search when stored fields are only needed from a subset of   * the IndexReaders.   *   * @throws IllegalArgumentException if not all indexes contain the same number   *     of documents   * @throws IllegalArgumentException if not all indexes have the same value   *     of {@link IndexReader#maxDoc()}   * @throws IOException if there is a low-level IO error   */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|ignoreStoredFields
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|readers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|reader
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|this
operator|.
name|hasDeletions
operator|=
name|reader
operator|.
name|hasDeletions
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|!=
name|maxDoc
condition|)
comment|// check compatibility
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All readers must have same maxDoc: "
operator|+
name|maxDoc
operator|+
literal|"!="
operator|+
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|reader
operator|.
name|numDocs
argument_list|()
operator|!=
name|numDocs
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All readers must have same numDocs: "
operator|+
name|numDocs
operator|+
literal|"!="
operator|+
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
throw|;
name|Collection
name|fields
init|=
name|reader
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
name|readerToFields
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|Iterator
name|i
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// update fieldToReader map
name|String
name|field
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|==
literal|null
condition|)
name|fieldToReader
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|ignoreStoredFields
condition|)
name|storedFieldReaders
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
comment|// add to storedFieldReaders
name|readers
operator|.
name|add
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
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
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|maxDoc
return|;
block|}
DECL|method|hasDeletions
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|hasDeletions
return|;
block|}
comment|// check first reader
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
if|if
condition|(
name|readers
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
return|return
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|isDeleted
argument_list|(
name|n
argument_list|)
return|;
return|return
literal|false
return|;
block|}
comment|// delete in all readers
DECL|method|doDelete
specifier|protected
name|void
name|doDelete
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|deleteDocument
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
name|hasDeletions
operator|=
literal|true
expr_stmt|;
block|}
comment|// undeleteAll in all readers
DECL|method|doUndeleteAll
specifier|protected
name|void
name|doUndeleteAll
parameter_list|()
throws|throws
name|CorruptIndexException
throws|,
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|undeleteAll
argument_list|()
expr_stmt|;
block|}
name|hasDeletions
operator|=
literal|false
expr_stmt|;
block|}
comment|// append fields from storedFieldReaders
DECL|method|document
specifier|public
name|Document
name|document
parameter_list|(
name|int
name|n
parameter_list|,
name|FieldSelector
name|fieldSelector
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|Document
name|result
init|=
operator|new
name|Document
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
name|storedFieldReaders
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|storedFieldReaders
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|boolean
name|include
init|=
operator|(
name|fieldSelector
operator|==
literal|null
operator|)
decl_stmt|;
if|if
condition|(
operator|!
name|include
condition|)
block|{
name|Iterator
name|it
init|=
operator|(
operator|(
name|Collection
operator|)
name|readerToFields
operator|.
name|get
argument_list|(
name|reader
argument_list|)
operator|)
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
if|if
condition|(
name|fieldSelector
operator|.
name|accept
argument_list|(
operator|(
name|String
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|)
operator|!=
name|FieldSelectorResult
operator|.
name|NO_LOAD
condition|)
block|{
name|include
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|include
condition|)
block|{
name|Iterator
name|fieldIterator
init|=
name|reader
operator|.
name|document
argument_list|(
name|n
argument_list|,
name|fieldSelector
argument_list|)
operator|.
name|getFields
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|Fieldable
operator|)
name|fieldIterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
comment|// get all vectors
DECL|method|getTermFreqVectors
specifier|public
name|TermFreqVector
index|[]
name|getTermFreqVectors
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|ArrayList
name|results
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Iterator
name|i
init|=
name|fieldToReader
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|field
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|TermFreqVector
name|vector
init|=
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|n
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|!=
literal|null
condition|)
name|results
operator|.
name|add
argument_list|(
name|vector
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|TermFreqVector
index|[]
operator|)
name|results
operator|.
name|toArray
argument_list|(
operator|new
name|TermFreqVector
index|[
name|results
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|TermFreqVector
name|getTermFreqVector
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|null
else|:
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|n
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTermFreqVector
specifier|public
name|void
name|getTermFreqVector
parameter_list|(
name|int
name|docNumber
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|ensureOpen
argument_list|()
expr_stmt|;
name|Iterator
name|i
init|=
name|fieldToReader
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|field
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|IndexReader
name|reader
init|=
operator|(
name|IndexReader
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|reader
operator|.
name|getTermFreqVector
argument_list|(
name|docNumber
argument_list|,
name|field
argument_list|,
name|mapper
argument_list|)
expr_stmt|;
block|}
block|}
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
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|false
else|:
name|reader
operator|.
name|hasNorms
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|norms
specifier|public
name|byte
index|[]
name|norms
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
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|null
else|:
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|norms
specifier|public
name|void
name|norms
parameter_list|(
name|String
name|field
parameter_list|,
name|byte
index|[]
name|result
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|norms
argument_list|(
name|field
argument_list|,
name|result
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
DECL|method|doSetNorm
specifier|protected
name|void
name|doSetNorm
parameter_list|(
name|int
name|n
parameter_list|,
name|String
name|field
parameter_list|,
name|byte
name|value
parameter_list|)
throws|throws
name|CorruptIndexException
throws|,
name|IOException
block|{
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|doSetNorm
argument_list|(
name|n
argument_list|,
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParallelTermEnum
argument_list|()
return|;
block|}
DECL|method|terms
specifier|public
name|TermEnum
name|terms
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParallelTermEnum
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|)
decl_stmt|;
return|return
name|reader
operator|==
literal|null
condition|?
literal|0
else|:
name|reader
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParallelTermDocs
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|termDocs
specifier|public
name|TermDocs
name|termDocs
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParallelTermDocs
argument_list|()
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParallelTermPositions
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|termPositions
specifier|public
name|TermPositions
name|termPositions
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|ParallelTermPositions
argument_list|()
return|;
block|}
comment|/**    * Checks recursively if all subreaders are up to date.     */
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|isCurrent
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subreaders are up to date
return|return
literal|true
return|;
block|}
comment|/**    * Checks recursively if all subindexes are optimized     */
DECL|method|isOptimized
specifier|public
name|boolean
name|isOptimized
parameter_list|()
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|isOptimized
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// all subindexes are optimized
return|return
literal|true
return|;
block|}
comment|/** Not implemented.    * @throws UnsupportedOperationException    */
DECL|method|getVersion
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ParallelReader does not support this method."
argument_list|)
throw|;
block|}
DECL|method|doCommit
specifier|protected
name|void
name|doCommit
parameter_list|()
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
DECL|method|doClose
specifier|protected
specifier|synchronized
name|void
name|doClose
parameter_list|()
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getFieldNames
specifier|public
name|Collection
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
name|Set
name|fieldSet
init|=
operator|new
name|HashSet
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
name|readers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|readers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
decl_stmt|;
name|Collection
name|names
init|=
name|reader
operator|.
name|getFieldNames
argument_list|(
name|fieldNames
argument_list|)
decl_stmt|;
name|fieldSet
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldSet
return|;
block|}
DECL|class|ParallelTermEnum
specifier|private
class|class
name|ParallelTermEnum
extends|extends
name|TermEnum
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|fieldIterator
specifier|private
name|Iterator
name|fieldIterator
decl_stmt|;
DECL|field|termEnum
specifier|private
name|TermEnum
name|termEnum
decl_stmt|;
DECL|method|ParallelTermEnum
specifier|public
name|ParallelTermEnum
parameter_list|()
throws|throws
name|IOException
block|{
name|field
operator|=
operator|(
name|String
operator|)
name|fieldToReader
operator|.
name|firstKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
name|termEnum
operator|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
operator|.
name|terms
argument_list|()
expr_stmt|;
block|}
DECL|method|ParallelTermEnum
specifier|public
name|ParallelTermEnum
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|field
operator|=
name|term
operator|.
name|field
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|termEnum
operator|=
name|reader
operator|.
name|terms
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|termEnum
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// another term in this field?
if|if
condition|(
name|termEnum
operator|.
name|next
argument_list|()
operator|&&
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
return|return
literal|true
return|;
comment|// yes, keep going
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// close old termEnum
comment|// find the next field with terms, if any
if|if
condition|(
name|fieldIterator
operator|==
literal|null
condition|)
block|{
name|fieldIterator
operator|=
name|fieldToReader
operator|.
name|tailMap
argument_list|(
name|field
argument_list|)
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|fieldIterator
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// Skip field to get next one
block|}
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|field
operator|=
operator|(
name|String
operator|)
name|fieldIterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|termEnum
operator|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|field
argument_list|)
operator|)
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Term
name|term
init|=
name|termEnum
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
return|return
literal|true
return|;
else|else
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
comment|// no more fields
block|}
DECL|method|term
specifier|public
name|Term
name|term
parameter_list|()
block|{
if|if
condition|(
name|termEnum
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|termEnum
operator|.
name|term
argument_list|()
return|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|()
block|{
if|if
condition|(
name|termEnum
operator|==
literal|null
condition|)
return|return
literal|0
return|;
return|return
name|termEnum
operator|.
name|docFreq
argument_list|()
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
if|if
condition|(
name|termEnum
operator|!=
literal|null
condition|)
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// wrap a TermDocs in order to support seek(Term)
DECL|class|ParallelTermDocs
specifier|private
class|class
name|ParallelTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|termDocs
specifier|protected
name|TermDocs
name|termDocs
decl_stmt|;
DECL|method|ParallelTermDocs
specifier|public
name|ParallelTermDocs
parameter_list|()
block|{}
DECL|method|ParallelTermDocs
specifier|public
name|ParallelTermDocs
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|termDocs
operator|.
name|doc
argument_list|()
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|termDocs
operator|.
name|freq
argument_list|()
return|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|)
decl_stmt|;
name|termDocs
operator|=
name|reader
operator|!=
literal|null
condition|?
name|reader
operator|.
name|termDocs
argument_list|(
name|term
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|termDocs
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|termDocs
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|int
index|[]
name|docs
parameter_list|,
specifier|final
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termDocs
operator|==
literal|null
condition|)
return|return
literal|0
return|;
return|return
name|termDocs
operator|.
name|read
argument_list|(
name|docs
argument_list|,
name|freqs
argument_list|)
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|termDocs
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|termDocs
operator|.
name|skipTo
argument_list|(
name|target
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
if|if
condition|(
name|termDocs
operator|!=
literal|null
condition|)
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ParallelTermPositions
specifier|private
class|class
name|ParallelTermPositions
extends|extends
name|ParallelTermDocs
implements|implements
name|TermPositions
block|{
DECL|method|ParallelTermPositions
specifier|public
name|ParallelTermPositions
parameter_list|()
block|{}
DECL|method|ParallelTermPositions
specifier|public
name|ParallelTermPositions
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader
init|=
operator|(
operator|(
name|IndexReader
operator|)
name|fieldToReader
operator|.
name|get
argument_list|(
name|term
operator|.
name|field
argument_list|()
argument_list|)
operator|)
decl_stmt|;
name|termDocs
operator|=
name|reader
operator|!=
literal|null
condition|?
name|reader
operator|.
name|termPositions
argument_list|(
name|term
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
DECL|method|nextPosition
specifier|public
name|int
name|nextPosition
parameter_list|()
throws|throws
name|IOException
block|{
comment|// It is an error to call this if there is no next position, e.g. if termDocs==null
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|termDocs
operator|)
operator|.
name|nextPosition
argument_list|()
return|;
block|}
DECL|method|getPayloadLength
specifier|public
name|int
name|getPayloadLength
parameter_list|()
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|termDocs
operator|)
operator|.
name|getPayloadLength
argument_list|()
return|;
block|}
DECL|method|getPayload
specifier|public
name|byte
index|[]
name|getPayload
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|termDocs
operator|)
operator|.
name|getPayload
argument_list|(
name|data
argument_list|,
name|offset
argument_list|)
return|;
block|}
comment|// TODO: Remove warning after API has been finalized
DECL|method|isPayloadAvailable
specifier|public
name|boolean
name|isPayloadAvailable
parameter_list|()
block|{
return|return
operator|(
operator|(
name|TermPositions
operator|)
name|termDocs
operator|)
operator|.
name|isPayloadAvailable
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

