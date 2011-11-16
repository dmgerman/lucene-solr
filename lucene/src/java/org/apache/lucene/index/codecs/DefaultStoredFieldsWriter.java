begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not  * use this file except in compliance with the License. You may obtain a copy of  * the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|IndexableField
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
name|MergePolicy
operator|.
name|MergeAbortedException
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
name|IOUtils
import|;
end_import

begin_comment
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|DefaultStoredFieldsWriter
specifier|public
specifier|final
class|class
name|DefaultStoredFieldsWriter
extends|extends
name|StoredFieldsWriter
block|{
comment|// NOTE: bit 0 is free here!  You can steal it!
DECL|field|FIELD_IS_BINARY
specifier|static
specifier|final
name|int
name|FIELD_IS_BINARY
init|=
literal|1
operator|<<
literal|1
decl_stmt|;
comment|// the old bit 1<< 2 was compressed, is now left out
DECL|field|_NUMERIC_BIT_SHIFT
specifier|private
specifier|static
specifier|final
name|int
name|_NUMERIC_BIT_SHIFT
init|=
literal|3
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_MASK
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_MASK
init|=
literal|0x07
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_INT
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_INT
init|=
literal|1
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_LONG
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_LONG
init|=
literal|2
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_FLOAT
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_FLOAT
init|=
literal|3
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
DECL|field|FIELD_IS_NUMERIC_DOUBLE
specifier|static
specifier|final
name|int
name|FIELD_IS_NUMERIC_DOUBLE
init|=
literal|4
operator|<<
name|_NUMERIC_BIT_SHIFT
decl_stmt|;
comment|// currently unused: static final int FIELD_IS_NUMERIC_SHORT = 5<< _NUMERIC_BIT_SHIFT;
comment|// currently unused: static final int FIELD_IS_NUMERIC_BYTE = 6<< _NUMERIC_BIT_SHIFT;
comment|// the next possible bits are: 1<< 6; 1<< 7
comment|// Lucene 3.0: Removal of compressed fields
DECL|field|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
init|=
literal|2
decl_stmt|;
comment|// Lucene 3.2: NumericFields are stored in binary format
DECL|field|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
specifier|static
specifier|final
name|int
name|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
init|=
literal|3
decl_stmt|;
comment|// NOTE: if you introduce a new format, make it 1 higher
comment|// than the current one, and always change this if you
comment|// switch to a new format!
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_LUCENE_3_2_NUMERIC_FIELDS
decl_stmt|;
comment|// when removing support for old versions, leave the last supported version here
DECL|field|FORMAT_MINIMUM
specifier|static
specifier|final
name|int
name|FORMAT_MINIMUM
init|=
name|FORMAT_LUCENE_3_0_NO_COMPRESSED_FIELDS
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
DECL|method|DefaultStoredFieldsWriter
specifier|public
name|DefaultStoredFieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|String
name|segment
parameter_list|,
name|IOContext
name|context
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
name|segment
expr_stmt|;
name|boolean
name|success
init|=
literal|false
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
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|indexStream
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
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fieldsStream
operator|.
name|writeInt
argument_list|(
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|indexStream
operator|.
name|writeInt
argument_list|(
name|FORMAT_CURRENT
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
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Writes the contents of buffer into the fields stream
comment|// and adds a new entry for this document into the index
comment|// stream.  This assumes the buffer was already written
comment|// in the correct fields format.
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|(
name|int
name|numStoredFields
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
block|}
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
name|indexStream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fieldsStream
operator|=
name|indexStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{
try|try
block|{
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|FIELDS_INDEX_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignored
parameter_list|)
block|{}
block|}
DECL|method|writeField
specifier|public
specifier|final
name|void
name|writeField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|IndexableField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|info
operator|.
name|number
argument_list|)
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
comment|// TODO: maybe a field should serialize itself?
comment|// this way we don't bake into indexer all these
comment|// specific encodings for different fields?  and apps
comment|// can customize...
if|if
condition|(
name|field
operator|.
name|numeric
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|field
operator|.
name|numericDataType
argument_list|()
condition|)
block|{
case|case
name|INT
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_INT
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_LONG
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_FLOAT
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|bits
operator||=
name|FIELD_IS_NUMERIC_DOUBLE
expr_stmt|;
break|break;
default|default:
assert|assert
literal|false
operator|:
literal|"Should never get here"
assert|;
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
operator||=
name|FIELD_IS_BINARY
expr_stmt|;
name|string
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|string
operator|=
name|field
operator|.
name|stringValue
argument_list|()
expr_stmt|;
block|}
block|}
name|fieldsStream
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bits
argument_list|)
expr_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|fieldsStream
operator|.
name|writeVInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|fieldsStream
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
else|else
block|{
specifier|final
name|Number
name|n
init|=
name|field
operator|.
name|numericValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
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
switch|switch
condition|(
name|field
operator|.
name|numericDataType
argument_list|()
condition|)
block|{
case|case
name|INT
case|:
name|fieldsStream
operator|.
name|writeInt
argument_list|(
name|n
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|LONG
case|:
name|fieldsStream
operator|.
name|writeLong
argument_list|(
name|n
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOAT
case|:
name|fieldsStream
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|n
operator|.
name|floatValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DOUBLE
case|:
name|fieldsStream
operator|.
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|n
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
assert|assert
literal|false
operator|:
literal|"Should never get here"
assert|;
block|}
block|}
block|}
comment|/** Bulk write a contiguous series of documents.  The    *  lengths array is the length (in bytes) of each raw    *  document.  The stream IndexInput is the    *  fieldsStream from which we should bulk-copy all    *  bytes. */
DECL|method|addRawDocuments
specifier|public
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
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|4
operator|+
operator|(
operator|(
name|long
operator|)
name|numDocs
operator|)
operator|*
literal|8
operator|!=
name|indexStream
operator|.
name|getFilePointer
argument_list|()
condition|)
comment|// This is most likely a bug in Sun JRE 1.6.0_04/_05;
comment|// we detect that the bug has struck, here, and
comment|// throw an exception to prevent the corruption from
comment|// entering the index.  See LUCENE-1282 for
comment|// details.
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"mergeFields produced an invalid result: docCount is "
operator|+
name|numDocs
operator|+
literal|" but fdx file size is "
operator|+
name|indexStream
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" file="
operator|+
name|indexStream
operator|.
name|toString
argument_list|()
operator|+
literal|"; now aborting this merge to prevent index corruption"
argument_list|)
throw|;
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
comment|// Used for bulk-reading raw bytes for stored fields
name|int
name|rawDocLengths
index|[]
init|=
operator|new
name|int
index|[
name|MAX_RAW_MERGE_DOCS
index|]
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
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
name|DefaultStoredFieldsReader
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
comment|// we can only bulk-copy if the matching reader is also a DefaultFieldsReader
if|if
condition|(
name|fieldsReader
operator|!=
literal|null
operator|&&
name|fieldsReader
operator|instanceof
name|DefaultStoredFieldsReader
condition|)
block|{
name|matchingFieldsReader
operator|=
operator|(
name|DefaultStoredFieldsReader
operator|)
name|fieldsReader
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reader
operator|.
name|liveDocs
operator|!=
literal|null
condition|)
block|{
name|docCount
operator|+=
name|copyFieldsWithDeletions
argument_list|(
name|mergeState
argument_list|,
name|reader
argument_list|,
name|matchingFieldsReader
argument_list|,
name|rawDocLengths
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|docCount
operator|+=
name|copyFieldsNoDeletions
argument_list|(
name|mergeState
argument_list|,
name|reader
argument_list|,
name|matchingFieldsReader
argument_list|,
name|rawDocLengths
argument_list|)
expr_stmt|;
block|}
block|}
name|finish
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
return|return
name|docCount
return|;
block|}
comment|/** Maximum number of contiguous documents to bulk-copy       when merging stored fields */
DECL|field|MAX_RAW_MERGE_DOCS
specifier|private
specifier|final
specifier|static
name|int
name|MAX_RAW_MERGE_DOCS
init|=
literal|4192
decl_stmt|;
DECL|method|copyFieldsWithDeletions
specifier|private
name|int
name|copyFieldsWithDeletions
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
parameter_list|,
specifier|final
name|DefaultStoredFieldsReader
name|matchingFieldsReader
parameter_list|,
name|int
name|rawDocLengths
index|[]
parameter_list|)
throws|throws
name|IOException
throws|,
name|MergeAbortedException
throws|,
name|CorruptIndexException
block|{
name|int
name|docCount
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
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
name|liveDocs
decl_stmt|;
assert|assert
name|liveDocs
operator|!=
literal|null
assert|;
if|if
condition|(
name|matchingFieldsReader
operator|!=
literal|null
condition|)
block|{
comment|// We can bulk-copy because the fieldInfos are "congruent"
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|maxDoc
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
block|{
comment|// skip deleted docs
operator|++
name|j
expr_stmt|;
continue|continue;
block|}
comment|// We can optimize this case (doing a bulk byte copy) since the field
comment|// numbers are identical
name|int
name|start
init|=
name|j
decl_stmt|,
name|numDocs
init|=
literal|0
decl_stmt|;
do|do
block|{
name|j
operator|++
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
if|if
condition|(
name|j
operator|>=
name|maxDoc
condition|)
break|break;
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
block|{
name|j
operator|++
expr_stmt|;
break|break;
block|}
block|}
do|while
condition|(
name|numDocs
operator|<
name|MAX_RAW_MERGE_DOCS
condition|)
do|;
name|IndexInput
name|stream
init|=
name|matchingFieldsReader
operator|.
name|rawDocs
argument_list|(
name|rawDocLengths
argument_list|,
name|start
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
name|addRawDocuments
argument_list|(
name|stream
argument_list|,
name|rawDocLengths
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|docCount
operator|+=
name|numDocs
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
operator|*
name|numDocs
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|maxDoc
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
condition|)
block|{
comment|// skip deleted docs
continue|continue;
block|}
comment|// TODO: this could be more efficient using
comment|// FieldVisitor instead of loading/writing entire
comment|// doc; ie we just have to renumber the field number
comment|// on the fly?
comment|// NOTE: it's very important to first assign to doc then pass it to
comment|// fieldsWriter.addDocument; see LUCENE-1282
name|Document
name|doc
init|=
name|reader
operator|.
name|reader
operator|.
name|document
argument_list|(
name|j
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
name|docCount
operator|++
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
return|return
name|docCount
return|;
block|}
DECL|method|copyFieldsNoDeletions
specifier|private
name|int
name|copyFieldsNoDeletions
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
parameter_list|,
specifier|final
name|DefaultStoredFieldsReader
name|matchingFieldsReader
parameter_list|,
name|int
name|rawDocLengths
index|[]
parameter_list|)
throws|throws
name|IOException
throws|,
name|MergeAbortedException
throws|,
name|CorruptIndexException
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|int
name|docCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|matchingFieldsReader
operator|!=
literal|null
condition|)
block|{
comment|// We can bulk-copy because the fieldInfos are "congruent"
while|while
condition|(
name|docCount
operator|<
name|maxDoc
condition|)
block|{
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|MAX_RAW_MERGE_DOCS
argument_list|,
name|maxDoc
operator|-
name|docCount
argument_list|)
decl_stmt|;
name|IndexInput
name|stream
init|=
name|matchingFieldsReader
operator|.
name|rawDocs
argument_list|(
name|rawDocLengths
argument_list|,
name|docCount
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|addRawDocuments
argument_list|(
name|stream
argument_list|,
name|rawDocLengths
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|docCount
operator|+=
name|len
expr_stmt|;
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
operator|*
name|len
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
init|;
name|docCount
operator|<
name|maxDoc
condition|;
name|docCount
operator|++
control|)
block|{
comment|// NOTE: it's very important to first assign to doc then pass it to
comment|// fieldsWriter.addDocument; see LUCENE-1282
name|Document
name|doc
init|=
name|reader
operator|.
name|reader
operator|.
name|document
argument_list|(
name|docCount
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
return|return
name|docCount
return|;
block|}
block|}
end_class

end_unit

