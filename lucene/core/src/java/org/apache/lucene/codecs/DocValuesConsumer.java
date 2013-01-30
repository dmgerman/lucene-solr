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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|NoSuchElementException
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
name|AtomicReader
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
name|BinaryDocValues
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
name|NumericDocValues
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
name|SegmentWriteState
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
name|SortedDocValues
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
name|FixedBitSet
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
name|PriorityQueue
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
name|packed
operator|.
name|AppendingLongBuffer
import|;
end_import

begin_comment
comment|/**   * Abstract API that consumes numeric, binary and  * sorted docvalues.  Concrete implementations of this  * actually do "something" with the docvalues (write it into  * the index in a specific format).  *<p>  * The lifecycle is:  *<ol>  *<li>DocValuesConsumer is created by   *       {@link DocValuesFormat#fieldsConsumer(SegmentWriteState)} or  *       {@link NormsFormat#normsConsumer(SegmentWriteState)}.  *<li>{@link #addNumericField}, {@link #addBinaryField},  *       or {@link #addSortedField} are called for each Numeric,  *       Binary, or Sorted docvalues field. The API is a "pull" rather  *       than "push", and the implementation is free to iterate over the   *       values multiple times ({@link Iterable#iterator()}).  *<li>After all fields are added, the consumer is {@link #close}d.  *</ol>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DocValuesConsumer
specifier|public
specifier|abstract
class|class
name|DocValuesConsumer
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|DocValuesConsumer
specifier|protected
name|DocValuesConsumer
parameter_list|()
block|{}
comment|/**    * Writes numeric docvalues for a field.    * @param field field information    * @param values Iterable of numeric values (one for each document).    * @throws IOException if an I/O error occurred.    */
DECL|method|addNumericField
specifier|public
specifier|abstract
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes binary docvalues for a field.    * @param field field information    * @param values Iterable of binary values (one for each document).    * @throws IOException if an I/O error occurred.    */
DECL|method|addBinaryField
specifier|public
specifier|abstract
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Writes pre-sorted binary docvalues for a field.    * @param field field information    * @param values Iterable of binary values in sorted order (deduplicated).    * @param docToOrd Iterable of ordinals (one for each document).    * @throws IOException if an I/O error occurred.    */
DECL|method|addSortedField
specifier|public
specifier|abstract
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Merges the numeric docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addNumericField}, passing    * an Iterable that merges and filters deleted documents on the fly.    */
DECL|method|mergeNumericField
specifier|public
name|void
name|mergeNumericField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|List
argument_list|<
name|NumericDocValues
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IOException
block|{
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
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|long
name|nextValue
decl_stmt|;
name|AtomicReader
name|currentReader
decl_stmt|;
name|NumericDocValues
name|currentValues
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
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
argument_list|()
throw|;
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
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
comment|// TODO: make a mutable number
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentReader
operator|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentValues
operator|=
name|toMerge
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
name|nextValue
operator|=
name|currentValues
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
expr_stmt|;
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merges the binary docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addBinaryField}, passing    * an Iterable that merges and filters deleted documents on the fly.    */
DECL|method|mergeBinaryField
specifier|public
name|void
name|mergeBinaryField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|List
argument_list|<
name|BinaryDocValues
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IOException
block|{
name|addBinaryField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|BytesRef
name|nextValue
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|AtomicReader
name|currentReader
decl_stmt|;
name|BinaryDocValues
name|currentValues
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
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
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
comment|// TODO: make a mutable number
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|toMerge
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentReader
operator|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentValues
operator|=
name|toMerge
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
name|currentValues
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|,
name|nextValue
argument_list|)
expr_stmt|;
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|SortedBytesMerger
specifier|static
class|class
name|SortedBytesMerger
block|{
DECL|field|numMergedTerms
specifier|public
name|int
name|numMergedTerms
decl_stmt|;
DECL|field|ordToReaderId
specifier|final
name|AppendingLongBuffer
name|ordToReaderId
init|=
operator|new
name|AppendingLongBuffer
argument_list|()
decl_stmt|;
DECL|field|segStates
specifier|final
name|List
argument_list|<
name|SegmentState
argument_list|>
name|segStates
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentState
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|SegmentState
specifier|private
specifier|static
class|class
name|SegmentState
block|{
DECL|field|segmentID
name|int
name|segmentID
decl_stmt|;
DECL|field|reader
name|AtomicReader
name|reader
decl_stmt|;
DECL|field|liveTerms
name|FixedBitSet
name|liveTerms
decl_stmt|;
DECL|field|ord
name|int
name|ord
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|values
name|SortedDocValues
name|values
decl_stmt|;
DECL|field|scratch
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|lastOrd
name|int
name|lastOrd
init|=
operator|-
literal|1
decl_stmt|;
comment|// last REAL ord we looked up: nocommit: clean this up
DECL|field|ordDeltas
name|AppendingLongBuffer
name|ordDeltas
init|=
operator|new
name|AppendingLongBuffer
argument_list|()
decl_stmt|;
comment|// nocommit can we factor out the compressed fields
comment|// compression?  ie we have a good idea "roughly" what
comment|// the ord should be (linear projection) so we only
comment|// need to encode the delta from that ...:
DECL|field|segOrdToMergedOrd
name|int
index|[]
name|segOrdToMergedOrd
decl_stmt|;
DECL|method|nextTerm
specifier|public
name|BytesRef
name|nextTerm
parameter_list|()
block|{
while|while
condition|(
name|ord
operator|<
name|values
operator|.
name|getValueCount
argument_list|()
operator|-
literal|1
condition|)
block|{
name|ord
operator|++
expr_stmt|;
if|if
condition|(
name|liveTerms
operator|==
literal|null
operator|||
name|liveTerms
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|lastOrd
operator|=
name|ord
expr_stmt|;
return|return
name|scratch
return|;
block|}
else|else
block|{
comment|// Skip "deleted" terms (ie, terms that were not
comment|// referenced by any live docs): nocommit: why?!
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
DECL|class|TermMergeQueue
specifier|private
specifier|static
class|class
name|TermMergeQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SegmentState
argument_list|>
block|{
DECL|method|TermMergeQueue
specifier|public
name|TermMergeQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|SegmentState
name|a
parameter_list|,
name|SegmentState
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|scratch
operator|.
name|compareTo
argument_list|(
name|b
operator|.
name|scratch
argument_list|)
operator|<=
literal|0
return|;
block|}
block|}
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|List
argument_list|<
name|SortedDocValues
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First pass: mark "live" terms
for|for
control|(
name|int
name|readerIDX
init|=
literal|0
init|;
name|readerIDX
operator|<
name|toMerge
operator|.
name|size
argument_list|()
condition|;
name|readerIDX
operator|++
control|)
block|{
name|AtomicReader
name|reader
init|=
name|mergeState
operator|.
name|readers
operator|.
name|get
argument_list|(
name|readerIDX
argument_list|)
decl_stmt|;
comment|// nocommit what if this is null...?  need default source?
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|SegmentState
name|state
init|=
operator|new
name|SegmentState
argument_list|()
decl_stmt|;
name|state
operator|.
name|segmentID
operator|=
name|readerIDX
expr_stmt|;
name|state
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|state
operator|.
name|values
operator|=
name|toMerge
operator|.
name|get
argument_list|(
name|readerIDX
argument_list|)
expr_stmt|;
name|segStates
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
assert|assert
name|state
operator|.
name|values
operator|.
name|getValueCount
argument_list|()
operator|<
name|Integer
operator|.
name|MAX_VALUE
assert|;
if|if
condition|(
name|reader
operator|.
name|hasDeletions
argument_list|()
condition|)
block|{
name|state
operator|.
name|liveTerms
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|state
operator|.
name|values
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
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
operator|.
name|get
argument_list|(
name|docID
argument_list|)
condition|)
block|{
name|state
operator|.
name|liveTerms
operator|.
name|set
argument_list|(
name|state
operator|.
name|values
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// nocommit we can unload the bits to disk to reduce
comment|// transient ram spike...
block|}
comment|// Second pass: merge only the live terms
name|TermMergeQueue
name|q
init|=
operator|new
name|TermMergeQueue
argument_list|(
name|segStates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SegmentState
name|segState
range|:
name|segStates
control|)
block|{
if|if
condition|(
name|segState
operator|.
name|nextTerm
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// nocommit we could defer this to 3rd pass (and
comment|// reduce transient RAM spike) but then
comment|// we'd spend more effort computing the mapping...:
name|segState
operator|.
name|segOrdToMergedOrd
operator|=
operator|new
name|int
index|[
name|segState
operator|.
name|values
operator|.
name|getValueCount
argument_list|()
index|]
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|segState
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|lastOrds
index|[]
init|=
operator|new
name|int
index|[
name|segStates
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|BytesRef
name|lastTerm
init|=
literal|null
decl_stmt|;
name|int
name|ord
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|q
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|SegmentState
name|top
init|=
name|q
operator|.
name|top
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastTerm
operator|==
literal|null
operator|||
operator|!
name|lastTerm
operator|.
name|equals
argument_list|(
name|top
operator|.
name|scratch
argument_list|)
condition|)
block|{
comment|// a new unique term: record its segment ID / sourceOrd pair
name|int
name|readerId
init|=
name|top
operator|.
name|segmentID
decl_stmt|;
name|ordToReaderId
operator|.
name|add
argument_list|(
name|readerId
argument_list|)
expr_stmt|;
name|int
name|sourceOrd
init|=
name|top
operator|.
name|lastOrd
decl_stmt|;
name|int
name|delta
init|=
name|sourceOrd
operator|-
name|lastOrds
index|[
name|readerId
index|]
decl_stmt|;
name|lastOrds
index|[
name|readerId
index|]
operator|=
name|sourceOrd
expr_stmt|;
name|top
operator|.
name|ordDeltas
operator|.
name|add
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|lastTerm
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|top
operator|.
name|scratch
argument_list|)
expr_stmt|;
name|ord
operator|++
expr_stmt|;
block|}
name|top
operator|.
name|segOrdToMergedOrd
index|[
name|top
operator|.
name|ord
index|]
operator|=
name|ord
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|nextTerm
argument_list|()
operator|==
literal|null
condition|)
block|{
name|q
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|q
operator|.
name|updateTop
argument_list|()
expr_stmt|;
block|}
block|}
name|numMergedTerms
operator|=
name|ord
expr_stmt|;
comment|// clear our bitsets for GC: we dont need them anymore (e.g. while flushing merged stuff to codec)
for|for
control|(
name|SegmentState
name|state
range|:
name|segStates
control|)
block|{
name|state
operator|.
name|liveTerms
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/*     public void finish(SortedDocValuesConsumer consumer) throws IOException {        // Third pass: write merged result       for(BytesRef term : mergedTerms) {         consumer.addValue(term);       }        for(SegmentState segState : segStates) {         Bits liveDocs = segState.reader.getLiveDocs();         int maxDoc = segState.reader.maxDoc();         for(int docID=0;docID<maxDoc;docID++) {           if (liveDocs == null || liveDocs.get(docID)) {             int segOrd = segState.values.getOrd(docID);             int mergedOrd = segState.segOrdToMergedOrd[segOrd];             consumer.addDoc(mergedOrd);           }         }       }     }     */
block|}
comment|/**    * Merges the sorted docvalues from<code>toMerge</code>.    *<p>    * The default implementation calls {@link #addSortedField}, passing    * an Iterable that merges ordinals and values and filters deleted documents .    */
DECL|method|mergeSortedField
specifier|public
name|void
name|mergeSortedField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
specifier|final
name|MergeState
name|mergeState
parameter_list|,
name|List
argument_list|<
name|SortedDocValues
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedBytesMerger
name|merger
init|=
operator|new
name|SortedBytesMerger
argument_list|()
decl_stmt|;
comment|// Does the heavy lifting to merge sort all "live" ords:
name|merger
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
name|addSortedField
argument_list|(
name|fieldInfo
argument_list|,
comment|// ord -> value
operator|new
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
name|iterator
parameter_list|()
block|{
comment|// for each next(), tells us what reader to go to
specifier|final
name|AppendingLongBuffer
operator|.
name|Iterator
name|readerIDs
init|=
name|merger
operator|.
name|ordToReaderId
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// for each next(), gives us the original ord
specifier|final
name|AppendingLongBuffer
operator|.
name|Iterator
name|ordDeltas
index|[]
init|=
operator|new
name|AppendingLongBuffer
operator|.
name|Iterator
index|[
name|merger
operator|.
name|segStates
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|int
name|lastOrds
index|[]
init|=
operator|new
name|int
index|[
name|ordDeltas
operator|.
name|length
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
name|ordDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ordDeltas
index|[
name|i
index|]
operator|=
name|merger
operator|.
name|segStates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|ordDeltas
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
specifier|final
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
name|int
name|ordUpto
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|ordUpto
operator|<
name|merger
operator|.
name|numMergedTerms
return|;
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
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|int
name|readerID
init|=
operator|(
name|int
operator|)
name|readerIDs
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|ord
init|=
name|lastOrds
index|[
name|readerID
index|]
operator|+
operator|(
name|int
operator|)
name|ordDeltas
index|[
name|readerID
index|]
operator|.
name|next
argument_list|()
decl_stmt|;
name|merger
operator|.
name|segStates
operator|.
name|get
argument_list|(
name|readerID
argument_list|)
operator|.
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|lastOrds
index|[
name|readerID
index|]
operator|=
name|ord
expr_stmt|;
name|ordUpto
operator|++
expr_stmt|;
return|return
name|scratch
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|,
comment|// doc -> ord
operator|new
name|Iterable
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
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
return|return
operator|new
name|Iterator
argument_list|<
name|Number
argument_list|>
argument_list|()
block|{
name|int
name|readerUpto
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docIDUpto
decl_stmt|;
name|int
name|nextValue
decl_stmt|;
name|SortedBytesMerger
operator|.
name|SegmentState
name|currentReader
decl_stmt|;
name|Bits
name|currentLiveDocs
decl_stmt|;
name|boolean
name|nextIsSet
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextIsSet
operator|||
name|setNext
argument_list|()
return|;
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
argument_list|()
throw|;
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
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
assert|assert
name|nextIsSet
assert|;
name|nextIsSet
operator|=
literal|false
expr_stmt|;
comment|// nocommit make a mutable number
return|return
name|nextValue
return|;
block|}
specifier|private
name|boolean
name|setNext
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|readerUpto
operator|==
name|merger
operator|.
name|segStates
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentReader
operator|==
literal|null
operator|||
name|docIDUpto
operator|==
name|currentReader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
condition|)
block|{
name|readerUpto
operator|++
expr_stmt|;
if|if
condition|(
name|readerUpto
operator|<
name|merger
operator|.
name|segStates
operator|.
name|size
argument_list|()
condition|)
block|{
name|currentReader
operator|=
name|merger
operator|.
name|segStates
operator|.
name|get
argument_list|(
name|readerUpto
argument_list|)
expr_stmt|;
name|currentLiveDocs
operator|=
name|currentReader
operator|.
name|reader
operator|.
name|getLiveDocs
argument_list|()
expr_stmt|;
block|}
name|docIDUpto
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|currentLiveDocs
operator|==
literal|null
operator|||
name|currentLiveDocs
operator|.
name|get
argument_list|(
name|docIDUpto
argument_list|)
condition|)
block|{
name|nextIsSet
operator|=
literal|true
expr_stmt|;
name|int
name|segOrd
init|=
name|currentReader
operator|.
name|values
operator|.
name|getOrd
argument_list|(
name|docIDUpto
argument_list|)
decl_stmt|;
name|nextValue
operator|=
name|currentReader
operator|.
name|segOrdToMergedOrd
index|[
name|segOrd
index|]
expr_stmt|;
name|docIDUpto
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
name|docIDUpto
operator|++
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

