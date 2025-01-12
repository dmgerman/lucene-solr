begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|search
operator|.
name|DocIdSetIterator
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
name|SortField
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
name|store
operator|.
name|DataOutput
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
name|ArrayUtil
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
name|BytesRefBuilder
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
name|Counter
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
name|PagedBytes
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
name|PackedInts
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
name|PackedLongValues
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
import|;
end_import

begin_comment
comment|/** Buffers up pending byte[] per doc, then flushes when  *  segment flushes. */
end_comment

begin_class
DECL|class|BinaryDocValuesWriter
class|class
name|BinaryDocValuesWriter
extends|extends
name|DocValuesWriter
block|{
comment|/** Maximum length for a binary field. */
DECL|field|MAX_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LENGTH
init|=
name|ArrayUtil
operator|.
name|MAX_ARRAY_LENGTH
decl_stmt|;
comment|// 32 KB block sizes for PagedBytes storage:
DECL|field|BLOCK_BITS
specifier|private
specifier|final
specifier|static
name|int
name|BLOCK_BITS
init|=
literal|15
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|PagedBytes
name|bytes
decl_stmt|;
DECL|field|bytesOut
specifier|private
specifier|final
name|DataOutput
name|bytesOut
decl_stmt|;
DECL|field|iwBytesUsed
specifier|private
specifier|final
name|Counter
name|iwBytesUsed
decl_stmt|;
DECL|field|lengths
specifier|private
specifier|final
name|PackedLongValues
operator|.
name|Builder
name|lengths
decl_stmt|;
DECL|field|docsWithField
specifier|private
name|DocsWithFieldSet
name|docsWithField
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|bytesUsed
specifier|private
name|long
name|bytesUsed
decl_stmt|;
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxLength
specifier|private
name|int
name|maxLength
init|=
literal|0
decl_stmt|;
DECL|method|BinaryDocValuesWriter
specifier|public
name|BinaryDocValuesWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|Counter
name|iwBytesUsed
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
operator|new
name|PagedBytes
argument_list|(
name|BLOCK_BITS
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesOut
operator|=
name|bytes
operator|.
name|getDataOutput
argument_list|()
expr_stmt|;
name|this
operator|.
name|lengths
operator|=
name|PackedLongValues
operator|.
name|deltaPackedBuilder
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|this
operator|.
name|iwBytesUsed
operator|=
name|iwBytesUsed
expr_stmt|;
name|this
operator|.
name|docsWithField
operator|=
operator|new
name|DocsWithFieldSet
argument_list|()
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|lengths
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|docsWithField
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|addValue
specifier|public
name|void
name|addValue
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|value
parameter_list|)
block|{
if|if
condition|(
name|docID
operator|<=
name|lastDocID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" appears more than once in this document (only one value is allowed per field)"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field=\""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\": null value not allowed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|.
name|length
operator|>
name|MAX_LENGTH
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DocValuesField \""
operator|+
name|fieldInfo
operator|.
name|name
operator|+
literal|"\" is too large, must be<= "
operator|+
name|MAX_LENGTH
argument_list|)
throw|;
block|}
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|value
operator|.
name|length
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
name|lengths
operator|.
name|add
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|bytesOut
operator|.
name|writeBytes
argument_list|(
name|value
operator|.
name|bytes
argument_list|,
name|value
operator|.
name|offset
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// Should never happen!
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
name|docsWithField
operator|.
name|add
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|updateBytesUsed
argument_list|()
expr_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
block|}
DECL|method|updateBytesUsed
specifier|private
name|void
name|updateBytesUsed
parameter_list|()
block|{
specifier|final
name|long
name|newBytesUsed
init|=
name|lengths
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|bytes
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|docsWithField
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|iwBytesUsed
operator|.
name|addAndGet
argument_list|(
name|newBytesUsed
operator|-
name|bytesUsed
argument_list|)
expr_stmt|;
name|bytesUsed
operator|=
name|newBytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{   }
DECL|method|sortDocValues
specifier|private
name|SortingLeafReader
operator|.
name|CachedBinaryDVs
name|sortDocValues
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|Sorter
operator|.
name|DocMap
name|sortMap
parameter_list|,
name|BinaryDocValues
name|oldValues
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedBitSet
name|docsWithField
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|BytesRef
index|[]
name|values
init|=
operator|new
name|BytesRef
index|[
name|maxDoc
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|docID
init|=
name|oldValues
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|int
name|newDocID
init|=
name|sortMap
operator|.
name|oldToNew
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|docsWithField
operator|.
name|set
argument_list|(
name|newDocID
argument_list|)
expr_stmt|;
name|values
index|[
name|newDocID
index|]
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|oldValues
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SortingLeafReader
operator|.
name|CachedBinaryDVs
argument_list|(
name|values
argument_list|,
name|docsWithField
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDocComparator
name|Sorter
operator|.
name|DocComparator
name|getDocComparator
parameter_list|(
name|int
name|numDoc
parameter_list|,
name|SortField
name|sortField
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"It is forbidden to sort on a binary field"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|Sorter
operator|.
name|DocMap
name|sortMap
parameter_list|,
name|DocValuesConsumer
name|dvConsumer
parameter_list|)
throws|throws
name|IOException
block|{
name|bytes
operator|.
name|freeze
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|PackedLongValues
name|lengths
init|=
name|this
operator|.
name|lengths
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|SortingLeafReader
operator|.
name|CachedBinaryDVs
name|sorted
decl_stmt|;
if|if
condition|(
name|sortMap
operator|!=
literal|null
condition|)
block|{
name|sorted
operator|=
name|sortDocValues
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|sortMap
argument_list|,
operator|new
name|BufferedBinaryDocValues
argument_list|(
name|lengths
argument_list|,
name|maxLength
argument_list|,
name|bytes
operator|.
name|getDataInput
argument_list|()
argument_list|,
name|docsWithField
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sorted
operator|=
literal|null
expr_stmt|;
block|}
name|dvConsumer
operator|.
name|addBinaryField
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|EmptyDocValuesProducer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|fieldInfoIn
parameter_list|)
block|{
if|if
condition|(
name|fieldInfoIn
operator|!=
name|fieldInfo
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong fieldInfo"
argument_list|)
throw|;
block|}
if|if
condition|(
name|sorted
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|BufferedBinaryDocValues
argument_list|(
name|lengths
argument_list|,
name|maxLength
argument_list|,
name|bytes
operator|.
name|getDataInput
argument_list|()
argument_list|,
name|docsWithField
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortingLeafReader
operator|.
name|SortingBinaryDocValues
argument_list|(
name|sorted
argument_list|)
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// iterates over the values we have in ram
DECL|class|BufferedBinaryDocValues
specifier|private
specifier|static
class|class
name|BufferedBinaryDocValues
extends|extends
name|BinaryDocValues
block|{
DECL|field|value
specifier|final
name|BytesRefBuilder
name|value
decl_stmt|;
DECL|field|lengthsIterator
specifier|final
name|PackedLongValues
operator|.
name|Iterator
name|lengthsIterator
decl_stmt|;
DECL|field|docsWithField
specifier|final
name|DocIdSetIterator
name|docsWithField
decl_stmt|;
DECL|field|bytesIterator
specifier|final
name|DataInput
name|bytesIterator
decl_stmt|;
DECL|method|BufferedBinaryDocValues
name|BufferedBinaryDocValues
parameter_list|(
name|PackedLongValues
name|lengths
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|DataInput
name|bytesIterator
parameter_list|,
name|DocIdSetIterator
name|docsWithFields
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
name|this
operator|.
name|value
operator|.
name|grow
argument_list|(
name|maxLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|lengthsIterator
operator|=
name|lengths
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|this
operator|.
name|bytesIterator
operator|=
name|bytesIterator
expr_stmt|;
name|this
operator|.
name|docsWithField
operator|=
name|docsWithFields
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
name|docsWithField
operator|.
name|docID
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
name|int
name|docID
init|=
name|docsWithField
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
name|int
name|length
init|=
name|Math
operator|.
name|toIntExact
argument_list|(
name|lengthsIterator
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|value
operator|.
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|bytesIterator
operator|.
name|readBytes
argument_list|(
name|value
operator|.
name|bytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|docID
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|advanceExact
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|docsWithField
operator|.
name|cost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
return|return
name|value
operator|.
name|get
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

