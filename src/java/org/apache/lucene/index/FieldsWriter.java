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
name|Enumeration
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
name|Field
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
name|IndexOutput
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
name|short
name|FIELD_IS_TOKENIZED
init|=
literal|1
decl_stmt|;
DECL|field|FIELD_IS_BINARY
specifier|static
specifier|final
name|short
name|FIELD_IS_BINARY
init|=
literal|2
decl_stmt|;
DECL|field|FIELD_IS_COMPRESSED
specifier|static
specifier|final
name|short
name|FIELD_IS_COMPRESSED
init|=
literal|4
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
block|}
DECL|method|close
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
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
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
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
name|fields
operator|=
name|doc
operator|.
name|fields
argument_list|()
expr_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Field
name|field
init|=
operator|(
name|Field
operator|)
name|fields
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|isStored
argument_list|()
condition|)
block|{
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|fieldInfos
operator|.
name|fieldNumber
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|)
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

