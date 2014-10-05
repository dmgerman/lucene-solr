begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene41
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
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
name|EOFException
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|compressing
operator|.
name|CompressionMode
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
name|Decompressor
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
name|StoredFieldVisitor
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
name|AlreadyClosedException
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
name|ByteArrayDataInput
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
name|ChecksumIndexInput
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
comment|/**  * {@link StoredFieldsReader} impl for {@code Lucene41StoredFieldsFormat}.  * @deprecated only for reading old segments  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene41StoredFieldsReader
specifier|final
class|class
name|Lucene41StoredFieldsReader
extends|extends
name|StoredFieldsReader
block|{
comment|// Do not reuse the decompression buffer when there is more than 32kb to decompress
DECL|field|BUFFER_REUSE_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_REUSE_THRESHOLD
init|=
literal|1
operator|<<
literal|15
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
comment|/** Extension of stored fields file */
DECL|field|FIELDS_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS_EXTENSION
init|=
literal|"fdt"
decl_stmt|;
comment|/** Extension of stored fields index file */
DECL|field|FIELDS_INDEX_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|FIELDS_INDEX_EXTENSION
init|=
literal|"fdx"
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|indexReader
specifier|private
specifier|final
name|Lucene41StoredFieldsIndexReader
name|indexReader
decl_stmt|;
DECL|field|maxPointer
specifier|private
specifier|final
name|long
name|maxPointer
decl_stmt|;
DECL|field|fieldsStream
specifier|private
specifier|final
name|IndexInput
name|fieldsStream
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|int
name|chunkSize
decl_stmt|;
DECL|field|packedIntsVersion
specifier|private
specifier|final
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|compressionMode
specifier|private
specifier|final
name|CompressionMode
name|compressionMode
decl_stmt|;
DECL|field|decompressor
specifier|private
specifier|final
name|Decompressor
name|decompressor
decl_stmt|;
DECL|field|bytes
specifier|private
specifier|final
name|BytesRef
name|bytes
decl_stmt|;
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|// used by clone
DECL|method|Lucene41StoredFieldsReader
specifier|private
name|Lucene41StoredFieldsReader
parameter_list|(
name|Lucene41StoredFieldsReader
name|reader
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|reader
operator|.
name|version
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|reader
operator|.
name|fieldInfos
expr_stmt|;
name|this
operator|.
name|fieldsStream
operator|=
name|reader
operator|.
name|fieldsStream
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexReader
operator|=
name|reader
operator|.
name|indexReader
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxPointer
operator|=
name|reader
operator|.
name|maxPointer
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|reader
operator|.
name|chunkSize
expr_stmt|;
name|this
operator|.
name|packedIntsVersion
operator|=
name|reader
operator|.
name|packedIntsVersion
expr_stmt|;
name|this
operator|.
name|compressionMode
operator|=
name|reader
operator|.
name|compressionMode
expr_stmt|;
name|this
operator|.
name|decompressor
operator|=
name|reader
operator|.
name|decompressor
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|numDocs
operator|=
name|reader
operator|.
name|numDocs
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|(
name|reader
operator|.
name|bytes
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|closed
operator|=
literal|false
expr_stmt|;
block|}
comment|/** Sole constructor. */
DECL|method|Lucene41StoredFieldsReader
specifier|public
name|Lucene41StoredFieldsReader
parameter_list|(
name|Directory
name|d
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|String
name|formatName
parameter_list|,
name|CompressionMode
name|compressionMode
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|compressionMode
operator|=
name|compressionMode
expr_stmt|;
specifier|final
name|String
name|segment
init|=
name|si
operator|.
name|name
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|numDocs
operator|=
name|si
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|ChecksumIndexInput
name|indexStream
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|indexStreamFN
init|=
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
decl_stmt|;
specifier|final
name|String
name|fieldsStreamFN
init|=
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
decl_stmt|;
comment|// Load the index into memory
name|indexStream
operator|=
name|d
operator|.
name|openChecksumInput
argument_list|(
name|indexStreamFN
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
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|indexStream
argument_list|,
name|codecNameIdx
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
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
name|indexReader
operator|=
operator|new
name|Lucene41StoredFieldsIndexReader
argument_list|(
name|indexStream
argument_list|,
name|si
argument_list|)
expr_stmt|;
name|long
name|maxPointer
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|version
operator|>=
name|VERSION_CHECKSUM
condition|)
block|{
name|maxPointer
operator|=
name|indexStream
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|indexStream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CodecUtil
operator|.
name|checkEOF
argument_list|(
name|indexStream
argument_list|)
expr_stmt|;
block|}
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStream
operator|=
literal|null
expr_stmt|;
comment|// Open the data file and read metadata
name|fieldsStream
operator|=
name|d
operator|.
name|openInput
argument_list|(
name|fieldsStreamFN
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|>=
name|VERSION_CHECKSUM
condition|)
block|{
if|if
condition|(
name|maxPointer
operator|+
name|CodecUtil
operator|.
name|footerLength
argument_list|()
operator|!=
name|fieldsStream
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid fieldsStream maxPointer (file truncated?): maxPointer="
operator|+
name|maxPointer
operator|+
literal|", length="
operator|+
name|fieldsStream
operator|.
name|length
argument_list|()
argument_list|,
name|fieldsStream
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|maxPointer
operator|=
name|fieldsStream
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|maxPointer
operator|=
name|maxPointer
expr_stmt|;
specifier|final
name|String
name|codecNameDat
init|=
name|formatName
operator|+
name|CODEC_SFX_DAT
decl_stmt|;
specifier|final
name|int
name|fieldsVersion
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|fieldsStream
argument_list|,
name|codecNameDat
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|fieldsVersion
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Version mismatch between stored fields index and data: "
operator|+
name|version
operator|+
literal|" != "
operator|+
name|fieldsVersion
argument_list|,
name|fieldsStream
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|version
operator|>=
name|VERSION_BIG_CHUNKS
condition|)
block|{
name|chunkSize
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|chunkSize
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|packedIntsVersion
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|decompressor
operator|=
name|compressionMode
operator|.
name|newDecompressor
argument_list|()
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
if|if
condition|(
name|version
operator|>=
name|VERSION_CHECKSUM
condition|)
block|{
comment|// NOTE: data file is too costly to verify checksum against all the bytes on open,
comment|// but for now we at least verify proper structure of the checksum footer: which looks
comment|// for FOOTER_MAGIC + algorithmID. This is cheap and can detect some forms of corruption
comment|// such as file truncation.
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|fieldsStream
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
name|this
argument_list|,
name|indexStream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @throws AlreadyClosedException if this FieldsReader is closed    */
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
throws|throws
name|AlreadyClosedException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"this FieldsReader is closed"
argument_list|)
throw|;
block|}
block|}
comment|/**     * Close the underlying {@link IndexInput}s.    */
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
operator|!
name|closed
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|fieldsStream
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
DECL|method|readField
specifier|private
specifier|static
name|void
name|readField
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|,
name|FieldInfo
name|info
parameter_list|,
name|int
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|bits
operator|&
name|TYPE_MASK
condition|)
block|{
case|case
name|BYTE_ARR
case|:
name|int
name|length
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|binaryField
argument_list|(
name|info
argument_list|,
name|data
argument_list|)
expr_stmt|;
break|break;
case|case
name|STRING
case|:
name|length
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|stringField
argument_list|(
name|info
argument_list|,
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_INT
case|:
name|visitor
operator|.
name|intField
argument_list|(
name|info
argument_list|,
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_FLOAT
case|:
name|visitor
operator|.
name|floatField
argument_list|(
name|info
argument_list|,
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_LONG
case|:
name|visitor
operator|.
name|longField
argument_list|(
name|info
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_DOUBLE
case|:
name|visitor
operator|.
name|doubleField
argument_list|(
name|info
argument_list|,
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknown type flag: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|skipField
specifier|private
specifier|static
name|void
name|skipField
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|bits
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|bits
operator|&
name|TYPE_MASK
condition|)
block|{
case|case
name|BYTE_ARR
case|:
case|case
name|STRING
case|:
specifier|final
name|int
name|length
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|in
operator|.
name|skipBytes
argument_list|(
name|length
argument_list|)
expr_stmt|;
break|break;
case|case
name|NUMERIC_INT
case|:
case|case
name|NUMERIC_FLOAT
case|:
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
break|break;
case|case
name|NUMERIC_LONG
case|:
case|case
name|NUMERIC_DOUBLE
case|:
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknown type flag: "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|visitDocument
specifier|public
name|void
name|visitDocument
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|seek
argument_list|(
name|indexReader
operator|.
name|getStartPointer
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|docBase
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
specifier|final
name|int
name|chunkDocs
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
argument_list|<
name|docBase
operator|||
name|docID
operator|>=
name|docBase
operator|+
name|chunkDocs
operator|||
name|docBase
operator|+
name|chunkDocs
argument_list|>
name|numDocs
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Corrupted: docID="
operator|+
name|docID
operator|+
literal|", docBase="
operator|+
name|docBase
operator|+
literal|", chunkDocs="
operator|+
name|chunkDocs
operator|+
literal|", numDocs="
operator|+
name|numDocs
argument_list|,
name|fieldsStream
argument_list|)
throw|;
block|}
specifier|final
name|int
name|numStoredFields
decl_stmt|,
name|offset
decl_stmt|,
name|length
decl_stmt|,
name|totalLength
decl_stmt|;
if|if
condition|(
name|chunkDocs
operator|==
literal|1
condition|)
block|{
name|numStoredFields
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|length
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|totalLength
operator|=
name|length
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|bitsPerStoredFields
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerStoredFields
operator|==
literal|0
condition|)
block|{
name|numStoredFields
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerStoredFields
operator|>
literal|31
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bitsPerStoredFields="
operator|+
name|bitsPerStoredFields
argument_list|,
name|fieldsStream
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|long
name|filePointer
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
init|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|fieldsStream
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerStoredFields
argument_list|)
decl_stmt|;
name|numStoredFields
operator|=
call|(
name|int
call|)
argument_list|(
name|reader
operator|.
name|get
argument_list|(
name|docID
operator|-
name|docBase
argument_list|)
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|seek
argument_list|(
name|filePointer
operator|+
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerStoredFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|bitsPerLength
init|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|bitsPerLength
operator|==
literal|0
condition|)
block|{
name|length
operator|=
name|fieldsStream
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|offset
operator|=
operator|(
name|docID
operator|-
name|docBase
operator|)
operator|*
name|length
expr_stmt|;
name|totalLength
operator|=
name|chunkDocs
operator|*
name|length
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bitsPerStoredFields
operator|>
literal|31
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"bitsPerLength="
operator|+
name|bitsPerLength
argument_list|,
name|fieldsStream
argument_list|)
throw|;
block|}
else|else
block|{
specifier|final
name|PackedInts
operator|.
name|ReaderIterator
name|it
init|=
name|PackedInts
operator|.
name|getReaderIteratorNoHeader
argument_list|(
name|fieldsStream
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|chunkDocs
argument_list|,
name|bitsPerLength
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|int
name|off
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
name|docID
operator|-
name|docBase
condition|;
operator|++
name|i
control|)
block|{
name|off
operator|+=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|offset
operator|=
name|off
expr_stmt|;
name|length
operator|=
operator|(
name|int
operator|)
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
name|off
operator|+=
name|length
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|docID
operator|-
name|docBase
operator|+
literal|1
init|;
name|i
operator|<
name|chunkDocs
condition|;
operator|++
name|i
control|)
block|{
name|off
operator|+=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|totalLength
operator|=
name|off
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|length
operator|==
literal|0
operator|)
operator|!=
operator|(
name|numStoredFields
operator|==
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"length="
operator|+
name|length
operator|+
literal|", numStoredFields="
operator|+
name|numStoredFields
argument_list|,
name|fieldsStream
argument_list|)
throw|;
block|}
if|if
condition|(
name|numStoredFields
operator|==
literal|0
condition|)
block|{
comment|// nothing to do
return|return;
block|}
specifier|final
name|DataInput
name|documentInput
decl_stmt|;
if|if
condition|(
name|version
operator|>=
name|VERSION_BIG_CHUNKS
operator|&&
name|totalLength
operator|>=
literal|2
operator|*
name|chunkSize
condition|)
block|{
assert|assert
name|chunkSize
operator|>
literal|0
assert|;
assert|assert
name|offset
operator|<
name|chunkSize
assert|;
name|decompressor
operator|.
name|decompress
argument_list|(
name|fieldsStream
argument_list|,
name|chunkSize
argument_list|,
name|offset
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|chunkSize
operator|-
name|offset
argument_list|)
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|documentInput
operator|=
operator|new
name|DataInput
argument_list|()
block|{
name|int
name|decompressed
init|=
name|bytes
operator|.
name|length
decl_stmt|;
name|void
name|fillBuffer
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|decompressed
operator|<=
name|length
assert|;
if|if
condition|(
name|decompressed
operator|==
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
specifier|final
name|int
name|toDecompress
init|=
name|Math
operator|.
name|min
argument_list|(
name|length
operator|-
name|decompressed
argument_list|,
name|chunkSize
argument_list|)
decl_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
name|fieldsStream
argument_list|,
name|toDecompress
argument_list|,
literal|0
argument_list|,
name|toDecompress
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|decompressed
operator|+=
name|toDecompress
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|fillBuffer
argument_list|()
expr_stmt|;
block|}
operator|--
name|bytes
operator|.
name|length
expr_stmt|;
return|return
name|bytes
operator|.
name|bytes
index|[
name|bytes
operator|.
name|offset
operator|++
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|len
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|len
operator|-=
name|bytes
operator|.
name|length
expr_stmt|;
name|offset
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
name|fillBuffer
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|offset
operator|+=
name|len
expr_stmt|;
name|bytes
operator|.
name|length
operator|-=
name|len
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|totalLength
operator|<=
name|BUFFER_REUSE_THRESHOLD
condition|?
name|this
operator|.
name|bytes
else|:
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|decompressor
operator|.
name|decompress
argument_list|(
name|fieldsStream
argument_list|,
name|totalLength
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
assert|assert
name|bytes
operator|.
name|length
operator|==
name|length
assert|;
name|documentInput
operator|=
operator|new
name|ByteArrayDataInput
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
for|for
control|(
name|int
name|fieldIDX
init|=
literal|0
init|;
name|fieldIDX
operator|<
name|numStoredFields
condition|;
name|fieldIDX
operator|++
control|)
block|{
specifier|final
name|long
name|infoAndBits
init|=
name|documentInput
operator|.
name|readVLong
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldNumber
init|=
call|(
name|int
call|)
argument_list|(
name|infoAndBits
operator|>>>
name|TYPE_BITS
argument_list|)
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bits
init|=
call|(
name|int
call|)
argument_list|(
name|infoAndBits
operator|&
name|TYPE_MASK
argument_list|)
decl_stmt|;
assert|assert
name|bits
operator|<=
name|NUMERIC_DOUBLE
operator|:
literal|"bits="
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|bits
argument_list|)
assert|;
switch|switch
condition|(
name|visitor
operator|.
name|needsField
argument_list|(
name|fieldInfo
argument_list|)
condition|)
block|{
case|case
name|YES
case|:
name|readField
argument_list|(
name|documentInput
argument_list|,
name|visitor
argument_list|,
name|fieldInfo
argument_list|,
name|bits
argument_list|)
expr_stmt|;
break|break;
case|case
name|NO
case|:
name|skipField
argument_list|(
name|documentInput
argument_list|,
name|bits
argument_list|)
expr_stmt|;
break|break;
case|case
name|STOP
case|:
return|return;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|StoredFieldsReader
name|clone
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|Lucene41StoredFieldsReader
argument_list|(
name|this
argument_list|)
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
return|return
name|indexReader
operator|.
name|ramBytesUsed
argument_list|()
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
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"stored field index"
argument_list|,
name|indexReader
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|version
operator|>=
name|VERSION_CHECKSUM
condition|)
block|{
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|fieldsStream
argument_list|)
expr_stmt|;
block|}
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
literal|"(mode="
operator|+
name|compressionMode
operator|+
literal|",chunksize="
operator|+
name|chunkSize
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

