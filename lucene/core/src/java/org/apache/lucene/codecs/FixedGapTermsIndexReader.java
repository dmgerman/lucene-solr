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
name|CodecUtil
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
name|IOUtils
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
name|Collection
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
name|index
operator|.
name|IndexFileNames
import|;
end_import

begin_comment
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|FixedGapTermsIndexReader
specifier|public
class|class
name|FixedGapTermsIndexReader
extends|extends
name|TermsIndexReaderBase
block|{
comment|// NOTE: long is overkill here, since this number is 128
comment|// by default and only indexDivisor * 128 if you change
comment|// the indexDivisor at search time.  But, we use this in a
comment|// number of places to multiply out the actual ord, and we
comment|// will overflow int during those multiplies.  So to avoid
comment|// having to upgrade each multiple to long in multiple
comment|// places (error prone), we use long here:
DECL|field|totalIndexInterval
specifier|private
name|long
name|totalIndexInterval
decl_stmt|;
DECL|field|indexDivisor
specifier|private
name|int
name|indexDivisor
decl_stmt|;
DECL|field|indexInterval
specifier|final
specifier|private
name|int
name|indexInterval
decl_stmt|;
comment|// Closed if indexLoaded is true:
DECL|field|in
specifier|private
name|IndexInput
name|in
decl_stmt|;
DECL|field|indexLoaded
specifier|private
specifier|volatile
name|boolean
name|indexLoaded
decl_stmt|;
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|field|PAGED_BYTES_BITS
specifier|private
specifier|final
specifier|static
name|int
name|PAGED_BYTES_BITS
init|=
literal|15
decl_stmt|;
comment|// all fields share this single logical byte[]
DECL|field|termBytes
specifier|private
specifier|final
name|PagedBytes
name|termBytes
init|=
operator|new
name|PagedBytes
argument_list|(
name|PAGED_BYTES_BITS
argument_list|)
decl_stmt|;
DECL|field|termBytesReader
specifier|private
name|PagedBytes
operator|.
name|Reader
name|termBytesReader
decl_stmt|;
DECL|field|fields
specifier|final
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|FieldIndexData
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|FieldInfo
argument_list|,
name|FieldIndexData
argument_list|>
argument_list|()
decl_stmt|;
comment|// start of the field info data
DECL|field|dirOffset
specifier|protected
name|long
name|dirOffset
decl_stmt|;
DECL|method|FixedGapTermsIndexReader
specifier|public
name|FixedGapTermsIndexReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|int
name|indexDivisor
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termComp
operator|=
name|termComp
expr_stmt|;
assert|assert
name|indexDivisor
operator|==
operator|-
literal|1
operator|||
name|indexDivisor
operator|>
literal|0
assert|;
name|in
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readHeader
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|indexInterval
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexDivisor
operator|=
name|indexDivisor
expr_stmt|;
if|if
condition|(
name|indexDivisor
operator|<
literal|0
condition|)
block|{
name|totalIndexInterval
operator|=
name|indexInterval
expr_stmt|;
block|}
else|else
block|{
comment|// In case terms index gets loaded, later, on demand
name|totalIndexInterval
operator|=
name|indexInterval
operator|*
name|indexDivisor
expr_stmt|;
block|}
assert|assert
name|totalIndexInterval
operator|>
literal|0
assert|;
name|seekDir
argument_list|(
name|in
argument_list|,
name|dirOffset
argument_list|)
expr_stmt|;
comment|// Read directory
specifier|final
name|int
name|numFields
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//System.out.println("FGR: init seg=" + segment + " div=" + indexDivisor + " nF=" + numFields);
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numFields
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|field
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numIndexTerms
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|long
name|termsStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|indexStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|packedIndexStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|long
name|packedOffsetsStart
init|=
name|in
operator|.
name|readVLong
argument_list|()
decl_stmt|;
assert|assert
name|packedIndexStart
operator|>=
name|indexStart
operator|:
literal|"packedStart="
operator|+
name|packedIndexStart
operator|+
literal|" indexStart="
operator|+
name|indexStart
operator|+
literal|" numIndexTerms="
operator|+
name|numIndexTerms
operator|+
literal|" seg="
operator|+
name|segment
assert|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|fieldInfo
argument_list|,
operator|new
name|FieldIndexData
argument_list|(
name|fieldInfo
argument_list|,
name|numIndexTerms
argument_list|,
name|indexStart
argument_list|,
name|termsStart
argument_list|,
name|packedIndexStart
argument_list|,
name|packedOffsetsStart
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexDivisor
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|success
condition|)
block|{
name|indexLoaded
operator|=
literal|true
expr_stmt|;
block|}
name|termBytesReader
operator|=
name|termBytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getDivisor
specifier|public
name|int
name|getDivisor
parameter_list|()
block|{
return|return
name|indexDivisor
return|;
block|}
DECL|method|readHeader
specifier|protected
name|void
name|readHeader
parameter_list|(
name|IndexInput
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|input
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|CODEC_NAME
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|VERSION_START
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|VERSION_START
argument_list|)
expr_stmt|;
name|dirOffset
operator|=
name|input
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
DECL|class|IndexEnum
specifier|private
class|class
name|IndexEnum
extends|extends
name|FieldIndexEnum
block|{
DECL|field|fieldIndex
specifier|private
specifier|final
name|FieldIndexData
operator|.
name|CoreFieldIndex
name|fieldIndex
decl_stmt|;
DECL|field|term
specifier|private
specifier|final
name|BytesRef
name|term
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|ord
specifier|private
name|long
name|ord
decl_stmt|;
DECL|method|IndexEnum
specifier|public
name|IndexEnum
parameter_list|(
name|FieldIndexData
operator|.
name|CoreFieldIndex
name|fieldIndex
parameter_list|)
block|{
name|this
operator|.
name|fieldIndex
operator|=
name|fieldIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|term
specifier|public
name|BytesRef
name|term
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|BytesRef
name|target
parameter_list|)
block|{
name|int
name|lo
init|=
literal|0
decl_stmt|;
comment|// binary search
name|int
name|hi
init|=
name|fieldIndex
operator|.
name|numIndexTerms
operator|-
literal|1
decl_stmt|;
assert|assert
name|totalIndexInterval
operator|>
literal|0
operator|:
literal|"totalIndexInterval="
operator|+
name|totalIndexInterval
assert|;
while|while
condition|(
name|hi
operator|>=
name|lo
condition|)
block|{
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
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|mid
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|mid
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|int
name|delta
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|target
argument_list|,
name|term
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|delta
operator|>
literal|0
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
assert|assert
name|mid
operator|>=
literal|0
assert|;
name|ord
operator|=
name|mid
operator|*
name|totalIndexInterval
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|mid
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|hi
operator|<
literal|0
condition|)
block|{
assert|assert
name|hi
operator|==
operator|-
literal|1
assert|;
name|hi
operator|=
literal|0
expr_stmt|;
block|}
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|hi
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|hi
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|ord
operator|=
name|hi
operator|*
name|totalIndexInterval
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|hi
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
block|{
specifier|final
name|int
name|idx
init|=
literal|1
operator|+
call|(
name|int
call|)
argument_list|(
name|ord
operator|/
name|totalIndexInterval
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
name|fieldIndex
operator|.
name|numIndexTerms
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|ord
operator|+=
name|totalIndexInterval
expr_stmt|;
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|long
name|ord
parameter_list|()
block|{
return|return
name|ord
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|long
name|seek
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
name|int
name|idx
init|=
call|(
name|int
call|)
argument_list|(
name|ord
operator|/
name|totalIndexInterval
argument_list|)
decl_stmt|;
comment|// caller must ensure ord is in bounds
assert|assert
name|idx
operator|<
name|fieldIndex
operator|.
name|numIndexTerms
assert|;
specifier|final
name|long
name|offset
init|=
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|fieldIndex
operator|.
name|termOffsets
operator|.
name|get
argument_list|(
literal|1
operator|+
name|idx
argument_list|)
operator|-
name|offset
argument_list|)
decl_stmt|;
name|termBytesReader
operator|.
name|fillSlice
argument_list|(
name|term
argument_list|,
name|fieldIndex
operator|.
name|termBytesStart
operator|+
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|ord
operator|=
name|idx
operator|*
name|totalIndexInterval
expr_stmt|;
return|return
name|fieldIndex
operator|.
name|termsStart
operator|+
name|fieldIndex
operator|.
name|termsDictOffsets
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|supportsOrd
specifier|public
name|boolean
name|supportsOrd
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|class|FieldIndexData
specifier|private
specifier|final
class|class
name|FieldIndexData
block|{
DECL|field|coreIndex
specifier|volatile
name|CoreFieldIndex
name|coreIndex
decl_stmt|;
DECL|field|indexStart
specifier|private
specifier|final
name|long
name|indexStart
decl_stmt|;
DECL|field|termsStart
specifier|private
specifier|final
name|long
name|termsStart
decl_stmt|;
DECL|field|packedIndexStart
specifier|private
specifier|final
name|long
name|packedIndexStart
decl_stmt|;
DECL|field|packedOffsetsStart
specifier|private
specifier|final
name|long
name|packedOffsetsStart
decl_stmt|;
DECL|field|numIndexTerms
specifier|private
specifier|final
name|int
name|numIndexTerms
decl_stmt|;
DECL|method|FieldIndexData
specifier|public
name|FieldIndexData
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|int
name|numIndexTerms
parameter_list|,
name|long
name|indexStart
parameter_list|,
name|long
name|termsStart
parameter_list|,
name|long
name|packedIndexStart
parameter_list|,
name|long
name|packedOffsetsStart
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsStart
operator|=
name|termsStart
expr_stmt|;
name|this
operator|.
name|indexStart
operator|=
name|indexStart
expr_stmt|;
name|this
operator|.
name|packedIndexStart
operator|=
name|packedIndexStart
expr_stmt|;
name|this
operator|.
name|packedOffsetsStart
operator|=
name|packedOffsetsStart
expr_stmt|;
name|this
operator|.
name|numIndexTerms
operator|=
name|numIndexTerms
expr_stmt|;
if|if
condition|(
name|indexDivisor
operator|>
literal|0
condition|)
block|{
name|loadTermsIndex
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|loadTermsIndex
specifier|private
name|void
name|loadTermsIndex
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|coreIndex
operator|==
literal|null
condition|)
block|{
name|coreIndex
operator|=
operator|new
name|CoreFieldIndex
argument_list|(
name|indexStart
argument_list|,
name|termsStart
argument_list|,
name|packedIndexStart
argument_list|,
name|packedOffsetsStart
argument_list|,
name|numIndexTerms
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CoreFieldIndex
specifier|private
specifier|final
class|class
name|CoreFieldIndex
block|{
comment|// where this field's terms begin in the packed byte[]
comment|// data
DECL|field|termBytesStart
specifier|final
name|long
name|termBytesStart
decl_stmt|;
comment|// offset into index termBytes
DECL|field|termOffsets
specifier|final
name|PackedInts
operator|.
name|Reader
name|termOffsets
decl_stmt|;
comment|// index pointers into main terms dict
DECL|field|termsDictOffsets
specifier|final
name|PackedInts
operator|.
name|Reader
name|termsDictOffsets
decl_stmt|;
DECL|field|numIndexTerms
specifier|final
name|int
name|numIndexTerms
decl_stmt|;
DECL|field|termsStart
specifier|final
name|long
name|termsStart
decl_stmt|;
DECL|method|CoreFieldIndex
specifier|public
name|CoreFieldIndex
parameter_list|(
name|long
name|indexStart
parameter_list|,
name|long
name|termsStart
parameter_list|,
name|long
name|packedIndexStart
parameter_list|,
name|long
name|packedOffsetsStart
parameter_list|,
name|int
name|numIndexTerms
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsStart
operator|=
name|termsStart
expr_stmt|;
name|termBytesStart
operator|=
name|termBytes
operator|.
name|getPointer
argument_list|()
expr_stmt|;
name|IndexInput
name|clone
init|=
operator|(
name|IndexInput
operator|)
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
name|indexStart
argument_list|)
expr_stmt|;
comment|// -1 is passed to mean "don't load term index", but
comment|// if we are then later loaded it's overwritten with
comment|// a real value
assert|assert
name|indexDivisor
operator|>
literal|0
assert|;
name|this
operator|.
name|numIndexTerms
operator|=
literal|1
operator|+
operator|(
name|numIndexTerms
operator|-
literal|1
operator|)
operator|/
name|indexDivisor
expr_stmt|;
assert|assert
name|this
operator|.
name|numIndexTerms
operator|>
literal|0
operator|:
literal|"numIndexTerms="
operator|+
name|numIndexTerms
operator|+
literal|" indexDivisor="
operator|+
name|indexDivisor
assert|;
if|if
condition|(
name|indexDivisor
operator|==
literal|1
condition|)
block|{
comment|// Default (load all index terms) is fast -- slurp in the images from disk:
try|try
block|{
specifier|final
name|long
name|numTermBytes
init|=
name|packedIndexStart
operator|-
name|indexStart
decl_stmt|;
name|termBytes
operator|.
name|copy
argument_list|(
name|clone
argument_list|,
name|numTermBytes
argument_list|)
expr_stmt|;
comment|// records offsets into main terms dict file
name|termsDictOffsets
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|clone
argument_list|)
expr_stmt|;
assert|assert
name|termsDictOffsets
operator|.
name|size
argument_list|()
operator|==
name|numIndexTerms
assert|;
comment|// records offsets into byte[] term data
name|termOffsets
operator|=
name|PackedInts
operator|.
name|getReader
argument_list|(
name|clone
argument_list|)
expr_stmt|;
assert|assert
name|termOffsets
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|+
name|numIndexTerms
assert|;
block|}
finally|finally
block|{
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Get packed iterators
specifier|final
name|IndexInput
name|clone1
init|=
operator|(
name|IndexInput
operator|)
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
specifier|final
name|IndexInput
name|clone2
init|=
operator|(
name|IndexInput
operator|)
name|in
operator|.
name|clone
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Subsample the index terms
name|clone1
operator|.
name|seek
argument_list|(
name|packedIndexStart
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|termsDictOffsetsIter
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|clone1
argument_list|)
decl_stmt|;
name|clone2
operator|.
name|seek
argument_list|(
name|packedOffsetsStart
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|termOffsetsIter
init|=
name|PackedInts
operator|.
name|getReaderIterator
argument_list|(
name|clone2
argument_list|)
decl_stmt|;
comment|// TODO: often we can get by w/ fewer bits per
comment|// value, below.. .but this'd be more complex:
comment|// we'd have to try @ fewer bits and then grow
comment|// if we overflowed it.
name|PackedInts
operator|.
name|Mutable
name|termsDictOffsetsM
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|this
operator|.
name|numIndexTerms
argument_list|,
name|termsDictOffsetsIter
operator|.
name|getBitsPerValue
argument_list|()
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|Mutable
name|termOffsetsM
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|this
operator|.
name|numIndexTerms
operator|+
literal|1
argument_list|,
name|termOffsetsIter
operator|.
name|getBitsPerValue
argument_list|()
argument_list|)
decl_stmt|;
name|termsDictOffsets
operator|=
name|termsDictOffsetsM
expr_stmt|;
name|termOffsets
operator|=
name|termOffsetsM
expr_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
name|long
name|termOffsetUpto
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|upto
operator|<
name|this
operator|.
name|numIndexTerms
condition|)
block|{
comment|// main file offset copies straight over
name|termsDictOffsetsM
operator|.
name|set
argument_list|(
name|upto
argument_list|,
name|termsDictOffsetsIter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|termOffsetsM
operator|.
name|set
argument_list|(
name|upto
argument_list|,
name|termOffsetUpto
argument_list|)
expr_stmt|;
name|long
name|termOffset
init|=
name|termOffsetsIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|nextTermOffset
init|=
name|termOffsetsIter
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numTermBytes
init|=
call|(
name|int
call|)
argument_list|(
name|nextTermOffset
operator|-
name|termOffset
argument_list|)
decl_stmt|;
name|clone
operator|.
name|seek
argument_list|(
name|indexStart
operator|+
name|termOffset
argument_list|)
expr_stmt|;
assert|assert
name|indexStart
operator|+
name|termOffset
operator|<
name|clone
operator|.
name|length
argument_list|()
operator|:
literal|"indexStart="
operator|+
name|indexStart
operator|+
literal|" termOffset="
operator|+
name|termOffset
operator|+
literal|" len="
operator|+
name|clone
operator|.
name|length
argument_list|()
assert|;
assert|assert
name|indexStart
operator|+
name|termOffset
operator|+
name|numTermBytes
operator|<
name|clone
operator|.
name|length
argument_list|()
assert|;
name|termBytes
operator|.
name|copy
argument_list|(
name|clone
argument_list|,
name|numTermBytes
argument_list|)
expr_stmt|;
name|termOffsetUpto
operator|+=
name|numTermBytes
expr_stmt|;
name|upto
operator|++
expr_stmt|;
if|if
condition|(
name|upto
operator|==
name|this
operator|.
name|numIndexTerms
condition|)
block|{
break|break;
block|}
comment|// skip terms:
name|termsDictOffsetsIter
operator|.
name|next
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexDivisor
operator|-
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|termOffsetsIter
operator|.
name|next
argument_list|()
expr_stmt|;
name|termsDictOffsetsIter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
name|termOffsetsM
operator|.
name|set
argument_list|(
name|upto
argument_list|,
name|termOffsetUpto
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|clone1
operator|.
name|close
argument_list|()
expr_stmt|;
name|clone2
operator|.
name|close
argument_list|()
expr_stmt|;
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getFieldEnum
specifier|public
name|FieldIndexEnum
name|getFieldEnum
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
specifier|final
name|FieldIndexData
name|fieldData
init|=
name|fields
operator|.
name|get
argument_list|(
name|fieldInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldData
operator|.
name|coreIndex
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
operator|new
name|IndexEnum
argument_list|(
name|fieldData
operator|.
name|coreIndex
argument_list|)
return|;
block|}
block|}
DECL|method|files
specifier|public
specifier|static
name|void
name|files
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|FixedGapTermsIndexWriter
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|in
operator|!=
literal|null
operator|&&
operator|!
name|indexLoaded
condition|)
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|seekDir
specifier|protected
name|void
name|seekDir
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|long
name|dirOffset
parameter_list|)
throws|throws
name|IOException
block|{
name|input
operator|.
name|seek
argument_list|(
name|dirOffset
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

