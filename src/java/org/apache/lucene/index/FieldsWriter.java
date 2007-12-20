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
comment|/**  * Copyright 2004 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Deflater
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
name|RAMOutputStream
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
name|store
operator|.
name|IndexInput
import|;
end_import

begin_class
DECL|class|FieldsWriter
specifier|final
class|class
name|FieldsWriter
block|{
DECL|field|FIELD_IS_TOKENIZED
specifier|static
specifier|final
name|byte
name|FIELD_IS_TOKENIZED
init|=
literal|0x1
decl_stmt|;
DECL|field|FIELD_IS_BINARY
specifier|static
specifier|final
name|byte
name|FIELD_IS_BINARY
init|=
literal|0x2
decl_stmt|;
DECL|field|FIELD_IS_COMPRESSED
specifier|static
specifier|final
name|byte
name|FIELD_IS_COMPRESSED
init|=
literal|0x4
decl_stmt|;
DECL|field|fieldInfos
specifier|private
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fieldsStream
specifier|private
name|IndexOutput
name|fieldsStream
decl_stmt|;
DECL|field|indexStream
specifier|private
name|IndexOutput
name|indexStream
decl_stmt|;
DECL|field|doClose
specifier|private
name|boolean
name|doClose
decl_stmt|;
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|Directory
name|d
parameter_list|,
name|String
name|segment
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|fieldsStream
operator|=
name|d
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
literal|".fdt"
argument_list|)
expr_stmt|;
name|indexStream
operator|=
name|d
operator|.
name|createOutput
argument_list|(
name|segment
operator|+
literal|".fdx"
argument_list|)
expr_stmt|;
name|doClose
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|FieldsWriter
name|FieldsWriter
parameter_list|(
name|IndexOutput
name|fdx
parameter_list|,
name|IndexOutput
name|fdt
parameter_list|,
name|FieldInfos
name|fn
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldInfos
operator|=
name|fn
expr_stmt|;
name|fieldsStream
operator|=
name|fdt
expr_stmt|;
name|indexStream
operator|=
name|fdx
expr_stmt|;
name|doClose
operator|=
literal|false
expr_stmt|;
block|}
comment|// Writes the contents of buffer into the fields stream
comment|// and adds a new entry for this document into the index
comment|// stream.  This assumes the buffer was already written
comment|// in the correct fields format.
DECL|method|flushDocument
name|void
name|flushDocument
parameter_list|(
name|int
name|numStoredFields
parameter_list|,
name|RAMOutputStream
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|numStoredFields
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|fieldsStream
argument_list|)
expr_stmt|;
block|}
DECL|method|flush
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|fieldsStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|doClose
condition|)
block|{
name|fieldsStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeField
specifier|final
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|fi
parameter_list|,
name|Fieldable
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if the field as an instanceof FieldsReader.FieldForMerge, we're in merge mode
comment|// and field.binaryValue() already returns the compressed value for a field
comment|// with isCompressed()==true, so we disable compression in that case
name|boolean
name|disableCompression
init|=
operator|(
name|field
operator|instanceof
name|FieldsReader
operator|.
name|FieldForMerge
operator|)
decl_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|fi
operator|.
name|number
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isTokenized
argument_list|()
condition|)
name|bits
operator||=
name|FieldsWriter
operator|.
name|FIELD_IS_TOKENIZED
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
name|bits
operator||=
name|FieldsWriter
operator|.
name|FIELD_IS_BINARY
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isCompressed
argument_list|()
condition|)
name|bits
operator||=
name|FieldsWriter
operator|.
name|FIELD_IS_COMPRESSED
expr_stmt|;
name|fieldsStream
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|isCompressed
argument_list|()
condition|)
block|{
comment|// compression is enabled for the current field
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|disableCompression
condition|)
block|{
comment|// optimized case for merging, the data
comment|// is already compressed
name|data
operator|=
name|field
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// check if it is a binary field
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
block|{
name|data
operator|=
name|compress
argument_list|(
name|field
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
name|compress
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|len
init|=
name|data
operator|.
name|length
decl_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// compression is disabled for the current field
if|if
condition|(
name|field
operator|.
name|isBinary
argument_list|()
condition|)
block|{
name|byte
index|[]
name|data
init|=
name|field
operator|.
name|binaryValue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|data
operator|.
name|length
decl_stmt|;
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fieldsStream
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
block|}
block|}
comment|/** Bulk write a contiguous series of documents.  The      *  lengths array is the length (in bytes) of each raw      *  document.  The stream IndexInput is the      *  fieldsStream from which we should bulk-copy all      *  bytes. */
DECL|method|addRawDocuments
specifier|final
name|void
name|addRawDocuments
parameter_list|(
name|IndexInput
name|stream
parameter_list|,
name|int
index|[]
name|lengths
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|position
init|=
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|position
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|position
argument_list|)
expr_stmt|;
name|position
operator|+=
name|lengths
index|[
name|i
index|]
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|copyBytes
argument_list|(
name|stream
argument_list|,
name|position
operator|-
name|start
argument_list|)
expr_stmt|;
assert|assert
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
operator|==
name|position
assert|;
block|}
DECL|method|addDocument
specifier|final
name|void
name|addDocument
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|indexStream
operator|.
name|writeLong
argument_list|(
name|fieldsStream
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|storedCount
init|=
literal|0
decl_stmt|;
name|Iterator
name|fieldIterator
init|=
name|doc
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
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fieldIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|storedCount
operator|++
expr_stmt|;
block|}
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|storedCount
argument_list|)
expr_stmt|;
name|fieldIterator
operator|=
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|fieldIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Fieldable
name|field
init|=
operator|(
name|Fieldable
operator|)
name|fieldIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
name|writeField
argument_list|(
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compress
specifier|private
specifier|final
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|input
parameter_list|)
block|{
comment|// Create the compressor with highest level of compression
name|Deflater
name|compressor
init|=
operator|new
name|Deflater
argument_list|()
decl_stmt|;
name|compressor
operator|.
name|setLevel
argument_list|(
name|Deflater
operator|.
name|BEST_COMPRESSION
argument_list|)
expr_stmt|;
comment|// Give the compressor the data to compress
name|compressor
operator|.
name|setInput
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|compressor
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|/*        * Create an expandable byte array to hold the compressed data.        * You cannot use an array that's the same size as the orginal because        * there is no guarantee that the compressed data will be smaller than        * the uncompressed data.        */
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|input
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// Compress the data
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|int
name|count
init|=
name|compressor
operator|.
name|deflate
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|compressor
operator|.
name|end
argument_list|()
expr_stmt|;
comment|// Get the compressed data
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

