begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitUtil
operator|.
name|zigZagDecode
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
name|Arrays
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
name|index
operator|.
name|CorruptIndexException
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
name|SegmentInfo
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
name|IndexInput
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
name|Accountable
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
name|Accountables
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
name|RamUsageEstimator
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

begin_comment
comment|/**  * Random-access reader for {@link CompressingStoredFieldsIndexWriter}.  * @lucene.internal  */
end_comment

begin_class
DECL|class|CompressingStoredFieldsIndexReader
specifier|public
specifier|final
class|class
name|CompressingStoredFieldsIndexReader
implements|implements
name|Cloneable
implements|,
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|CompressingStoredFieldsIndexReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|maxDoc
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|docBases
specifier|final
name|int
index|[]
name|docBases
decl_stmt|;
DECL|field|startPointers
specifier|final
name|long
index|[]
name|startPointers
decl_stmt|;
DECL|field|avgChunkDocs
specifier|final
name|int
index|[]
name|avgChunkDocs
decl_stmt|;
DECL|field|avgChunkSizes
specifier|final
name|long
index|[]
name|avgChunkSizes
decl_stmt|;
DECL|field|docBasesDeltas
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|docBasesDeltas
decl_stmt|;
comment|// delta from the avg
DECL|field|startPointersDeltas
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|startPointersDeltas
decl_stmt|;
comment|// delta from the avg
comment|// It is the responsibility of the caller to close fieldsIndexIn after this constructor
comment|// has been called
DECL|method|CompressingStoredFieldsIndexReader
name|CompressingStoredFieldsIndexReader
parameter_list|(
name|IndexInput
name|fieldsIndexIn
parameter_list|,
name|SegmentInfo
name|si
parameter_list|)
throws|throws
name|IOException
block|{
name|maxDoc
operator|=
name|si
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|int
index|[]
name|docBases
init|=
operator|new
name|int
index|[
literal|16
index|]
decl_stmt|;
name|long
index|[]
name|startPointers
init|=
operator|new
name|long
index|[
literal|16
index|]
decl_stmt|;
name|int
index|[]
name|avgChunkDocs
init|=
operator|new
name|int
index|[
literal|16
index|]
decl_stmt|;
name|long
index|[]
name|avgChunkSizes
init|=
operator|new
name|long
index|[
literal|16
index|]
decl_stmt|;
name|PackedInts
operator|.
name|Reader
index|[]
name|docBasesDeltas
init|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
literal|16
index|]
decl_stmt|;
name|PackedInts
operator|.
name|Reader
index|[]
name|startPointersDeltas
init|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
literal|16
index|]
decl_stmt|;
specifier|final
name|int
name|packedIntsVersion
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|blockCount
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|numChunks
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|numChunks
operator|==
literal|0
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|blockCount
operator|==
name|docBases
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|blockCount
operator|+
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|docBases
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBases
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|startPointers
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointers
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|avgChunkDocs
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkDocs
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|avgChunkSizes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkSizes
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|docBasesDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBasesDeltas
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
name|startPointersDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointersDeltas
argument_list|,
name|newSize
argument_list|)
expr_stmt|;
block|}
comment|// doc bases
name|docBases
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|avgChunkDocs
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
expr_stmt|;
specifier|final
name|int
name|bitsPerDocBase
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerDocBase
operator|>
literal|32
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted bitsPerDocBase (resource="
operator|+
name|fieldsIndexIn
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|docBasesDeltas
index|[
name|blockCount
index|]
operator|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|numChunks
argument_list|,
name|bitsPerDocBase
argument_list|)
expr_stmt|;
comment|// start pointers
name|startPointers
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|avgChunkSizes
index|[
name|blockCount
index|]
operator|=
name|fieldsIndexIn
operator|.
name|readVLong
argument_list|()
expr_stmt|;
specifier|final
name|int
name|bitsPerStartPointer
init|=
name|fieldsIndexIn
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerStartPointer
operator|>
literal|64
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted bitsPerStartPointer (resource="
operator|+
name|fieldsIndexIn
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|startPointersDeltas
index|[
name|blockCount
index|]
operator|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|fieldsIndexIn
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|numChunks
argument_list|,
name|bitsPerStartPointer
argument_list|)
expr_stmt|;
operator|++
name|blockCount
expr_stmt|;
block|}
name|this
operator|.
name|docBases
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBases
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|startPointers
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointers
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|avgChunkDocs
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkDocs
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|avgChunkSizes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|avgChunkSizes
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|docBasesDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|docBasesDeltas
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|startPointersDeltas
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|startPointersDeltas
argument_list|,
name|blockCount
argument_list|)
expr_stmt|;
block|}
DECL|method|block
specifier|private
name|int
name|block
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
decl_stmt|,
name|hi
init|=
name|docBases
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|lo
operator|<=
name|hi
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|midValue
init|=
name|docBases
index|[
name|mid
index|]
decl_stmt|;
if|if
condition|(
name|midValue
operator|==
name|docID
condition|)
block|{
return|return
name|mid
return|;
block|}
elseif|else
if|if
condition|(
name|midValue
operator|<
name|docID
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|hi
return|;
block|}
DECL|method|relativeDocBase
specifier|private
name|int
name|relativeDocBase
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|relativeChunk
parameter_list|)
block|{
specifier|final
name|int
name|expected
init|=
name|avgChunkDocs
index|[
name|block
index|]
operator|*
name|relativeChunk
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|zigZagDecode
argument_list|(
name|docBasesDeltas
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|relativeChunk
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|expected
operator|+
operator|(
name|int
operator|)
name|delta
return|;
block|}
DECL|method|relativeStartPointer
specifier|private
name|long
name|relativeStartPointer
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|relativeChunk
parameter_list|)
block|{
specifier|final
name|long
name|expected
init|=
name|avgChunkSizes
index|[
name|block
index|]
operator|*
name|relativeChunk
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|zigZagDecode
argument_list|(
name|startPointersDeltas
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|relativeChunk
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|expected
operator|+
name|delta
return|;
block|}
DECL|method|relativeChunk
specifier|private
name|int
name|relativeChunk
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|relativeDoc
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
decl_stmt|,
name|hi
init|=
name|docBasesDeltas
index|[
name|block
index|]
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|lo
operator|<=
name|hi
condition|)
block|{
specifier|final
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
specifier|final
name|int
name|midValue
init|=
name|relativeDocBase
argument_list|(
name|block
argument_list|,
name|mid
argument_list|)
decl_stmt|;
if|if
condition|(
name|midValue
operator|==
name|relativeDoc
condition|)
block|{
return|return
name|mid
return|;
block|}
elseif|else
if|if
condition|(
name|midValue
operator|<
name|relativeDoc
condition|)
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|hi
return|;
block|}
DECL|method|getStartPointer
name|long
name|getStartPointer
parameter_list|(
name|int
name|docID
parameter_list|)
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
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"docID out of range [0-"
operator|+
name|maxDoc
operator|+
literal|"]: "
operator|+
name|docID
argument_list|)
throw|;
block|}
specifier|final
name|int
name|block
init|=
name|block
argument_list|(
name|docID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|relativeChunk
init|=
name|relativeChunk
argument_list|(
name|block
argument_list|,
name|docID
operator|-
name|docBases
index|[
name|block
index|]
argument_list|)
decl_stmt|;
return|return
name|startPointers
index|[
name|block
index|]
operator|+
name|relativeStartPointer
argument_list|(
name|block
argument_list|,
name|relativeChunk
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|CompressingStoredFieldsIndexReader
name|clone
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|res
init|=
name|BASE_RAM_BYTES_USED
decl_stmt|;
name|res
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|docBasesDeltas
argument_list|)
expr_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Reader
name|r
range|:
name|docBasesDeltas
control|)
block|{
name|res
operator|+=
name|r
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|res
operator|+=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|startPointersDeltas
argument_list|)
expr_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Reader
name|r
range|:
name|startPointersDeltas
control|)
block|{
name|res
operator|+=
name|r
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|res
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|docBases
argument_list|)
expr_stmt|;
name|res
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|startPointers
argument_list|)
expr_stmt|;
name|res
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|avgChunkDocs
argument_list|)
expr_stmt|;
name|res
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|avgChunkSizes
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|docBaseDeltaBytes
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|docBasesDeltas
argument_list|)
decl_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Reader
name|r
range|:
name|docBasesDeltas
control|)
block|{
name|docBaseDeltaBytes
operator|+=
name|r
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"doc base deltas"
argument_list|,
name|docBaseDeltaBytes
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|startPointerDeltaBytes
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|startPointersDeltas
argument_list|)
decl_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Reader
name|r
range|:
name|startPointersDeltas
control|)
block|{
name|startPointerDeltaBytes
operator|+=
name|r
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"start pointer deltas"
argument_list|,
name|startPointerDeltaBytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|resources
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
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(blocks="
operator|+
name|docBases
operator|.
name|length
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

