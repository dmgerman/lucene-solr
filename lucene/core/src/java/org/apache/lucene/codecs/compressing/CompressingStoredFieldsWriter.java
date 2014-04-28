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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40StoredFieldsWriter
operator|.
name|FIELDS_EXTENSION
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40StoredFieldsWriter
operator|.
name|FIELDS_INDEX_EXTENSION
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
name|Arrays
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
name|codecs
operator|.
name|StoredFieldsReader
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
name|StoredFieldsWriter
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
name|compressing
operator|.
name|CompressingStoredFieldsReader
operator|.
name|ChunkIterator
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
name|IndexFileNames
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
name|index
operator|.
name|SegmentReader
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
name|StorableField
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
name|StoredDocument
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
name|IndexOutput
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
name|GrowableByteArrayDataOutput
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * {@link StoredFieldsWriter} impl for {@link CompressingStoredFieldsFormat}.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompressingStoredFieldsWriter
specifier|public
specifier|final
class|class
name|CompressingStoredFieldsWriter
extends|extends
name|StoredFieldsWriter
block|{
comment|// hard limit on the maximum number of documents per chunk
DECL|field|MAX_DOCUMENTS_PER_CHUNK
specifier|static
specifier|final
name|int
name|MAX_DOCUMENTS_PER_CHUNK
init|=
literal|128
decl_stmt|;
DECL|field|STRING
specifier|static
specifier|final
name|int
name|STRING
init|=
literal|0x00
decl_stmt|;
DECL|field|BYTE_ARR
specifier|static
specifier|final
name|int
name|BYTE_ARR
init|=
literal|0x01
decl_stmt|;
DECL|field|NUMERIC_INT
specifier|static
specifier|final
name|int
name|NUMERIC_INT
init|=
literal|0x02
decl_stmt|;
DECL|field|NUMERIC_FLOAT
specifier|static
specifier|final
name|int
name|NUMERIC_FLOAT
init|=
literal|0x03
decl_stmt|;
DECL|field|NUMERIC_LONG
specifier|static
specifier|final
name|int
name|NUMERIC_LONG
init|=
literal|0x04
decl_stmt|;
DECL|field|NUMERIC_DOUBLE
specifier|static
specifier|final
name|int
name|NUMERIC_DOUBLE
init|=
literal|0x05
decl_stmt|;
DECL|field|TYPE_BITS
specifier|static
specifier|final
name|int
name|TYPE_BITS
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|NUMERIC_DOUBLE
argument_list|)
decl_stmt|;
DECL|field|TYPE_MASK
specifier|static
specifier|final
name|int
name|TYPE_MASK
init|=
operator|(
name|int
operator|)
name|PackedInts
operator|.
name|maxValue
argument_list|(
name|TYPE_BITS
argument_list|)
decl_stmt|;
DECL|field|CODEC_SFX_IDX
specifier|static
specifier|final
name|String
name|CODEC_SFX_IDX
init|=
literal|"Index"
decl_stmt|;
DECL|field|CODEC_SFX_DAT
specifier|static
specifier|final
name|String
name|CODEC_SFX_DAT
init|=
literal|"Data"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_BIG_CHUNKS
specifier|static
specifier|final
name|int
name|VERSION_BIG_CHUNKS
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_CHECKSUM
specifier|static
specifier|final
name|int
name|VERSION_CHECKSUM
init|=
literal|2
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_CHECKSUM
decl_stmt|;
DECL|field|directory
specifier|private
specifier|final
name|Directory
name|directory
decl_stmt|;
DECL|field|segment
specifier|private
specifier|final
name|String
name|segment
decl_stmt|;
DECL|field|segmentSuffix
specifier|private
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|CompressingStoredFieldsIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|fieldsStream
specifier|private
name|IndexOutput
name|fieldsStream
decl_stmt|;
DECL|field|compressionMode
specifier|private
specifier|final
name|CompressionMode
name|compressionMode
decl_stmt|;
DECL|field|compressor
specifier|private
specifier|final
name|Compressor
name|compressor
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|int
name|chunkSize
decl_stmt|;
DECL|field|bufferedDocs
specifier|private
specifier|final
name|GrowableByteArrayDataOutput
name|bufferedDocs
decl_stmt|;
DECL|field|numStoredFields
specifier|private
name|int
index|[]
name|numStoredFields
decl_stmt|;
comment|// number of stored fields
DECL|field|endOffsets
specifier|private
name|int
index|[]
name|endOffsets
decl_stmt|;
comment|// end offsets in bufferedDocs
DECL|field|docBase
specifier|private
name|int
name|docBase
decl_stmt|;
comment|// doc ID at the beginning of the chunk
DECL|field|numBufferedDocs
specifier|private
name|int
name|numBufferedDocs
decl_stmt|;
comment|// docBase + numBufferedDocs == current doc ID
comment|/** Sole constructor. */
DECL|method|CompressingStoredFieldsWriter
specifier|public
name|CompressingStoredFieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|String
name|formatName
parameter_list|,
name|CompressionMode
name|compressionMode
parameter_list|,
name|int
name|chunkSize
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|directory
operator|!=
literal|null
assert|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|segment
operator|=
name|si
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
name|this
operator|.
name|compressionMode
operator|=
name|compressionMode
expr_stmt|;
name|this
operator|.
name|compressor
operator|=
name|compressionMode
operator|.
name|newCompressor
argument_list|()
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|bufferedDocs
operator|=
operator|new
name|GrowableByteArrayDataOutput
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|numStoredFields
operator|=
operator|new
name|int
index|[
literal|16
index|]
expr_stmt|;
name|this
operator|.
name|endOffsets
operator|=
operator|new
name|int
index|[
literal|16
index|]
expr_stmt|;
name|this
operator|.
name|numBufferedDocs
operator|=
literal|0
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IndexOutput
name|indexStream
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|fieldsStream
operator|=
name|directory
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
specifier|final
name|String
name|codecNameIdx
init|=
name|formatName
operator|+
name|CODEC_SFX_IDX
decl_stmt|;
specifier|final
name|String
name|codecNameDat
init|=
name|formatName
operator|+
name|CODEC_SFX_DAT
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|indexStream
argument_list|,
name|codecNameIdx
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|fieldsStream
argument_list|,
name|codecNameDat
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
assert|assert
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|codecNameDat
argument_list|)
operator|==
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
assert|;
assert|assert
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|codecNameIdx
argument_list|)
operator|==
name|indexStream
operator|.
name|getFilePointer
argument_list|()
assert|;
name|indexWriter
operator|=
operator|new
name|CompressingStoredFieldsIndexWriter
argument_list|(
name|indexStream
argument_list|)
expr_stmt|;
name|indexStream
operator|=
literal|null
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|PackedInts
operator|.
name|VERSION_CURRENT
argument_list|)
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|indexStream
argument_list|)
expr_stmt|;
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
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
try|try
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fieldsStream
argument_list|,
name|indexWriter
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fieldsStream
operator|=
literal|null
expr_stmt|;
name|indexWriter
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|field|numStoredFieldsInDoc
specifier|private
name|int
name|numStoredFieldsInDoc
decl_stmt|;
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|finishDocument
specifier|public
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|numBufferedDocs
operator|==
name|this
operator|.
name|numStoredFields
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|newLength
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|numBufferedDocs
operator|+
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|this
operator|.
name|numStoredFields
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|numStoredFields
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
name|endOffsets
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|endOffsets
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|numStoredFields
index|[
name|numBufferedDocs
index|]
operator|=
name|numStoredFieldsInDoc
expr_stmt|;
name|numStoredFieldsInDoc
operator|=
literal|0
expr_stmt|;
name|endOffsets
index|[
name|numBufferedDocs
index|]
operator|=
name|bufferedDocs
operator|.
name|length
expr_stmt|;
operator|++
name|numBufferedDocs
expr_stmt|;
if|if
condition|(
name|triggerFlush
argument_list|()
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|saveInts
specifier|private
specifier|static
name|void
name|saveInts
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|length
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|length
operator|>
literal|0
assert|;
if|if
condition|(
name|length
operator|==
literal|1
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|allEqual
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|!=
name|values
index|[
literal|0
index|]
condition|)
block|{
name|allEqual
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|allEqual
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|max
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|max
operator||=
name|values
index|[
name|i
index|]
expr_stmt|;
block|}
specifier|final
name|int
name|bitsRequired
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|max
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|bitsRequired
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriterNoHeader
argument_list|(
name|out
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|length
argument_list|,
name|bitsRequired
argument_list|,
literal|1
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeHeader
specifier|private
name|void
name|writeHeader
parameter_list|(
name|int
name|docBase
parameter_list|,
name|int
name|numBufferedDocs
parameter_list|,
name|int
index|[]
name|numStoredFields
parameter_list|,
name|int
index|[]
name|lengths
parameter_list|)
throws|throws
name|IOException
block|{
comment|// save docBase and numBufferedDocs
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|docBase
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|numBufferedDocs
argument_list|)
expr_stmt|;
comment|// save numStoredFields
name|saveInts
argument_list|(
name|numStoredFields
argument_list|,
name|numBufferedDocs
argument_list|,
name|fieldsStream
argument_list|)
expr_stmt|;
comment|// save lengths
name|saveInts
argument_list|(
name|lengths
argument_list|,
name|numBufferedDocs
argument_list|,
name|fieldsStream
argument_list|)
expr_stmt|;
block|}
DECL|method|triggerFlush
specifier|private
name|boolean
name|triggerFlush
parameter_list|()
block|{
return|return
name|bufferedDocs
operator|.
name|length
operator|>=
name|chunkSize
operator|||
comment|// chunks of at least chunkSize bytes
name|numBufferedDocs
operator|>=
name|MAX_DOCUMENTS_PER_CHUNK
return|;
block|}
DECL|method|flush
specifier|private
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|indexWriter
operator|.
name|writeIndex
argument_list|(
name|numBufferedDocs
argument_list|,
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
comment|// transform end offsets into lengths
specifier|final
name|int
index|[]
name|lengths
init|=
name|endOffsets
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numBufferedDocs
operator|-
literal|1
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|lengths
index|[
name|i
index|]
operator|=
name|endOffsets
index|[
name|i
index|]
operator|-
name|endOffsets
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
assert|assert
name|lengths
index|[
name|i
index|]
operator|>=
literal|0
assert|;
block|}
name|writeHeader
argument_list|(
name|docBase
argument_list|,
name|numBufferedDocs
argument_list|,
name|numStoredFields
argument_list|,
name|lengths
argument_list|)
expr_stmt|;
comment|// compress stored fields to fieldsStream
if|if
condition|(
name|bufferedDocs
operator|.
name|length
operator|>=
literal|2
operator|*
name|chunkSize
condition|)
block|{
comment|// big chunk, slice it
for|for
control|(
name|int
name|compressed
init|=
literal|0
init|;
name|compressed
operator|<
name|bufferedDocs
operator|.
name|length
condition|;
name|compressed
operator|+=
name|chunkSize
control|)
block|{
name|compressor
operator|.
name|compress
argument_list|(
name|bufferedDocs
operator|.
name|bytes
argument_list|,
name|compressed
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|chunkSize
argument_list|,
name|bufferedDocs
operator|.
name|length
operator|-
name|compressed
argument_list|)
argument_list|,
name|fieldsStream
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|compressor
operator|.
name|compress
argument_list|(
name|bufferedDocs
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|bufferedDocs
operator|.
name|length
argument_list|,
name|fieldsStream
argument_list|)
expr_stmt|;
block|}
comment|// reset
name|docBase
operator|+=
name|numBufferedDocs
expr_stmt|;
name|numBufferedDocs
operator|=
literal|0
expr_stmt|;
name|bufferedDocs
operator|.
name|length
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeField
specifier|public
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|StorableField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
operator|++
name|numStoredFieldsInDoc
expr_stmt|;
name|int
name|bits
init|=
literal|0
decl_stmt|;
specifier|final
name|BytesRef
name|bytes
decl_stmt|;
specifier|final
name|String
name|string
decl_stmt|;
name|Number
name|number
init|=
name|field
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|number
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|number
operator|instanceof
name|Byte
operator|||
name|number
operator|instanceof
name|Short
operator|||
name|number
operator|instanceof
name|Integer
condition|)
block|{
name|bits
operator|=
name|NUMERIC_INT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Long
condition|)
block|{
name|bits
operator|=
name|NUMERIC_LONG
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Float
condition|)
block|{
name|bits
operator|=
name|NUMERIC_FLOAT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Double
condition|)
block|{
name|bits
operator|=
name|NUMERIC_DOUBLE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot store numeric type "
operator|+
name|number
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|string
operator|=
literal|null
expr_stmt|;
name|bytes
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|bytes
operator|=
name|field
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|bits
operator|=
name|BYTE_ARR
expr_stmt|;
name|string
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|bits
operator|=
name|STRING
expr_stmt|;
name|string
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|string
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field "
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|" is stored but does not have binaryValue, stringValue nor numericValue"
argument_list|)
throw|;
block|}
block|}
block|}
specifier|final
name|long
name|infoAndBits
init|=
operator|(
operator|(
operator|(
name|long
operator|)
name|info
operator|.
name|number
operator|)
operator|<<
name|TYPE_BITS
operator|)
operator||
name|bits
decl_stmt|;
name|bufferedDocs
operator|.
name|writeVLong
argument_list|(
name|infoAndBits
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|bufferedDocs
operator|.
name|writeVInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|bufferedDocs
operator|.
name|writeBytes
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|string
operator|!=
literal|null
condition|)
block|{
name|bufferedDocs
operator|.
name|writeString
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|number
operator|instanceof
name|Byte
operator|||
name|number
operator|instanceof
name|Short
operator|||
name|number
operator|instanceof
name|Integer
condition|)
block|{
name|bufferedDocs
operator|.
name|writeInt
argument_list|(
name|number
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Long
condition|)
block|{
name|bufferedDocs
operator|.
name|writeLong
argument_list|(
name|number
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Float
condition|)
block|{
name|bufferedDocs
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|number
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Double
condition|)
block|{
name|bufferedDocs
operator|.
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|number
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Cannot get here"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|deleteFilesIgnoringExceptions
argument_list|(
name|directory
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
name|segmentSuffix
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|FieldInfos
name|fis
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numBufferedDocs
operator|>
literal|0
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|bufferedDocs
operator|.
name|length
operator|==
literal|0
assert|;
block|}
if|if
condition|(
name|docBase
operator|!=
name|numDocs
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Wrote "
operator|+
name|docBase
operator|+
literal|" docs, finish called with numDocs="
operator|+
name|numDocs
argument_list|)
throw|;
block|}
name|indexWriter
operator|.
name|finish
argument_list|(
name|numDocs
argument_list|,
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|fieldsStream
argument_list|)
expr_stmt|;
assert|assert
name|bufferedDocs
operator|.
name|length
operator|==
literal|0
assert|;
block|}
annotation|@
name|Override
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
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|SegmentReader
name|matchingSegmentReader
init|=
name|mergeState
operator|.
name|matchingSegmentReaders
index|[
name|idx
operator|++
index|]
decl_stmt|;
name|CompressingStoredFieldsReader
name|matchingFieldsReader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|matchingSegmentReader
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StoredFieldsReader
name|fieldsReader
init|=
name|matchingSegmentReader
operator|.
name|getFieldsReader
argument_list|()
decl_stmt|;
comment|// we can only bulk-copy if the matching reader is also a CompressingStoredFieldsReader
if|if
condition|(
name|fieldsReader
operator|!=
literal|null
operator|&&
name|fieldsReader
operator|instanceof
name|CompressingStoredFieldsReader
condition|)
block|{
name|matchingFieldsReader
operator|=
operator|(
name|CompressingStoredFieldsReader
operator|)
name|fieldsReader
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|maxDoc
init|=
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
name|getLiveDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|matchingFieldsReader
operator|==
literal|null
operator|||
name|matchingFieldsReader
operator|.
name|getVersion
argument_list|()
operator|!=
name|VERSION_CURRENT
comment|// means reader version is not the same as the writer version
operator|||
name|matchingFieldsReader
operator|.
name|getCompressionMode
argument_list|()
operator|!=
name|compressionMode
operator|||
name|matchingFieldsReader
operator|.
name|getChunkSize
argument_list|()
operator|!=
name|chunkSize
condition|)
block|{
comment|// the way data is decompressed depends on the chunk size
comment|// naive merge...
for|for
control|(
name|int
name|i
init|=
name|nextLiveDoc
argument_list|(
literal|0
argument_list|,
name|liveDocs
argument_list|,
name|maxDoc
argument_list|)
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|=
name|nextLiveDoc
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|liveDocs
argument_list|,
name|maxDoc
argument_list|)
control|)
block|{
name|StoredDocument
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|addDocument
argument_list|(
name|doc
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
operator|++
name|docCount
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
else|else
block|{
name|int
name|docID
init|=
name|nextLiveDoc
argument_list|(
literal|0
argument_list|,
name|liveDocs
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|docID
operator|<
name|maxDoc
condition|)
block|{
comment|// not all docs were deleted
specifier|final
name|ChunkIterator
name|it
init|=
name|matchingFieldsReader
operator|.
name|chunkIterator
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
index|[]
name|startOffsets
init|=
operator|new
name|int
index|[
literal|0
index|]
decl_stmt|;
do|do
block|{
comment|// go to the next chunk that contains docID
name|it
operator|.
name|next
argument_list|(
name|docID
argument_list|)
expr_stmt|;
comment|// transform lengths into offsets
if|if
condition|(
name|startOffsets
operator|.
name|length
operator|<
name|it
operator|.
name|chunkDocs
condition|)
block|{
name|startOffsets
operator|=
operator|new
name|int
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|it
operator|.
name|chunkDocs
argument_list|,
literal|4
argument_list|)
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|it
operator|.
name|chunkDocs
condition|;
operator|++
name|i
control|)
block|{
name|startOffsets
index|[
name|i
index|]
operator|=
name|startOffsets
index|[
name|i
operator|-
literal|1
index|]
operator|+
name|it
operator|.
name|lengths
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
if|if
condition|(
name|numBufferedDocs
operator|==
literal|0
comment|// starting a new chunk
operator|&&
name|startOffsets
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|<
name|chunkSize
comment|// chunk is small enough
operator|&&
name|startOffsets
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|+
name|it
operator|.
name|lengths
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|>=
name|chunkSize
comment|// chunk is large enough
operator|&&
name|nextDeletedDoc
argument_list|(
name|it
operator|.
name|docBase
argument_list|,
name|liveDocs
argument_list|,
name|it
operator|.
name|docBase
operator|+
name|it
operator|.
name|chunkDocs
argument_list|)
operator|==
name|it
operator|.
name|docBase
operator|+
name|it
operator|.
name|chunkDocs
condition|)
block|{
comment|// no deletion in the chunk
assert|assert
name|docID
operator|==
name|it
operator|.
name|docBase
assert|;
comment|// no need to decompress, just copy data
name|indexWriter
operator|.
name|writeIndex
argument_list|(
name|it
operator|.
name|chunkDocs
argument_list|,
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|writeHeader
argument_list|(
name|this
operator|.
name|docBase
argument_list|,
name|it
operator|.
name|chunkDocs
argument_list|,
name|it
operator|.
name|numStoredFields
argument_list|,
name|it
operator|.
name|lengths
argument_list|)
expr_stmt|;
name|it
operator|.
name|copyCompressedData
argument_list|(
name|fieldsStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|docBase
operator|+=
name|it
operator|.
name|chunkDocs
expr_stmt|;
name|docID
operator|=
name|nextLiveDoc
argument_list|(
name|it
operator|.
name|docBase
operator|+
name|it
operator|.
name|chunkDocs
argument_list|,
name|liveDocs
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|docCount
operator|+=
name|it
operator|.
name|chunkDocs
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
operator|*
name|it
operator|.
name|chunkDocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// decompress
name|it
operator|.
name|decompress
argument_list|()
expr_stmt|;
if|if
condition|(
name|startOffsets
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|+
name|it
operator|.
name|lengths
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|!=
name|it
operator|.
name|bytes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted: expected chunk size="
operator|+
name|startOffsets
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|+
name|it
operator|.
name|lengths
index|[
name|it
operator|.
name|chunkDocs
operator|-
literal|1
index|]
operator|+
literal|", got "
operator|+
name|it
operator|.
name|bytes
operator|.
name|length
argument_list|)
throw|;
block|}
comment|// copy non-deleted docs
for|for
control|(
init|;
name|docID
operator|<
name|it
operator|.
name|docBase
operator|+
name|it
operator|.
name|chunkDocs
condition|;
name|docID
operator|=
name|nextLiveDoc
argument_list|(
name|docID
operator|+
literal|1
argument_list|,
name|liveDocs
argument_list|,
name|maxDoc
argument_list|)
control|)
block|{
specifier|final
name|int
name|diff
init|=
name|docID
operator|-
name|it
operator|.
name|docBase
decl_stmt|;
name|startDocument
argument_list|()
expr_stmt|;
name|bufferedDocs
operator|.
name|writeBytes
argument_list|(
name|it
operator|.
name|bytes
operator|.
name|bytes
argument_list|,
name|it
operator|.
name|bytes
operator|.
name|offset
operator|+
name|startOffsets
index|[
name|diff
index|]
argument_list|,
name|it
operator|.
name|lengths
index|[
name|diff
index|]
argument_list|)
expr_stmt|;
name|numStoredFieldsInDoc
operator|=
name|it
operator|.
name|numStoredFields
index|[
name|diff
index|]
expr_stmt|;
name|finishDocument
argument_list|()
expr_stmt|;
operator|++
name|docCount
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
block|}
do|while
condition|(
name|docID
operator|<
name|maxDoc
condition|)
do|;
name|it
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|finish
argument_list|(
name|mergeState
operator|.
name|fieldInfos
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
return|return
name|docCount
return|;
block|}
DECL|method|nextLiveDoc
specifier|private
specifier|static
name|int
name|nextLiveDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
return|return
name|doc
return|;
block|}
while|while
condition|(
name|doc
operator|<
name|maxDoc
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
operator|++
name|doc
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
DECL|method|nextDeletedDoc
specifier|private
specifier|static
name|int
name|nextDeletedDoc
parameter_list|(
name|int
name|doc
parameter_list|,
name|Bits
name|liveDocs
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
condition|)
block|{
return|return
name|maxDoc
return|;
block|}
while|while
condition|(
name|doc
operator|<
name|maxDoc
operator|&&
name|liveDocs
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
operator|++
name|doc
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

