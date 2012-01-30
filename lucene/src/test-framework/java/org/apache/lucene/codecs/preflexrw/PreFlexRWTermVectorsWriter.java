begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.preflexrw
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|preflexrw
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|TermVectorsWriter
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
name|lucene3x
operator|.
name|Lucene3xTermVectorsReader
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
name|StringHelper
import|;
end_import

begin_class
DECL|class|PreFlexRWTermVectorsWriter
specifier|public
specifier|final
class|class
name|PreFlexRWTermVectorsWriter
extends|extends
name|TermVectorsWriter
block|{
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
DECL|field|tvx
DECL|field|tvd
DECL|field|tvf
specifier|private
name|IndexOutput
name|tvx
init|=
literal|null
decl_stmt|,
name|tvd
init|=
literal|null
decl_stmt|,
name|tvf
init|=
literal|null
decl_stmt|;
DECL|method|PreFlexRWTermVectorsWriter
specifier|public
name|PreFlexRWTermVectorsWriter
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
comment|// Open files for TermVector storage
name|tvx
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
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeInt
argument_list|(
name|Lucene3xTermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|tvd
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
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeInt
argument_list|(
name|Lucene3xTermVectorsReader
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
name|tvf
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
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeInt
argument_list|(
name|Lucene3xTermVectorsReader
operator|.
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
annotation|@
name|Override
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|(
name|int
name|numVectorFields
parameter_list|)
throws|throws
name|IOException
block|{
name|lastFieldName
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|numVectorFields
operator|=
name|numVectorFields
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvd
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvf
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
name|numVectorFields
argument_list|)
expr_stmt|;
name|fieldCount
operator|=
literal|0
expr_stmt|;
name|fps
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|fps
argument_list|,
name|numVectorFields
argument_list|)
expr_stmt|;
block|}
DECL|field|fps
specifier|private
name|long
name|fps
index|[]
init|=
operator|new
name|long
index|[
literal|10
index|]
decl_stmt|;
comment|// pointers to the tvf before writing each field
DECL|field|fieldCount
specifier|private
name|int
name|fieldCount
init|=
literal|0
decl_stmt|;
comment|// number of fields we have written so far for this document
DECL|field|numVectorFields
specifier|private
name|int
name|numVectorFields
init|=
literal|0
decl_stmt|;
comment|// total number of fields we will write for this document
DECL|field|lastFieldName
specifier|private
name|String
name|lastFieldName
decl_stmt|;
annotation|@
name|Override
DECL|method|startField
specifier|public
name|void
name|startField
parameter_list|(
name|FieldInfo
name|info
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|positions
parameter_list|,
name|boolean
name|offsets
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|lastFieldName
operator|==
literal|null
operator|||
name|info
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|lastFieldName
argument_list|)
operator|>
literal|0
operator|:
literal|"fieldName="
operator|+
name|info
operator|.
name|name
operator|+
literal|" lastFieldName="
operator|+
name|lastFieldName
assert|;
name|lastFieldName
operator|=
name|info
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|positions
operator|=
name|positions
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
name|offsets
expr_stmt|;
name|lastTerm
operator|.
name|length
operator|=
literal|0
expr_stmt|;
name|fps
index|[
name|fieldCount
operator|++
index|]
operator|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|tvd
operator|.
name|writeVInt
argument_list|(
name|info
operator|.
name|number
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|numTerms
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|positions
condition|)
name|bits
operator||=
name|Lucene3xTermVectorsReader
operator|.
name|STORE_POSITIONS_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|offsets
condition|)
name|bits
operator||=
name|Lucene3xTermVectorsReader
operator|.
name|STORE_OFFSET_WITH_TERMVECTOR
expr_stmt|;
name|tvf
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
assert|assert
name|fieldCount
operator|<=
name|numVectorFields
assert|;
if|if
condition|(
name|fieldCount
operator|==
name|numVectorFields
condition|)
block|{
comment|// last field of the document
comment|// this is crazy because the file format is crazy!
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|tvd
operator|.
name|writeVLong
argument_list|(
name|fps
index|[
name|i
index|]
operator|-
name|fps
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|lastTerm
specifier|private
specifier|final
name|BytesRef
name|lastTerm
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// NOTE: we override addProx, so we don't need to buffer when indexing.
comment|// we also don't buffer during bulk merges.
DECL|field|offsetStartBuffer
specifier|private
name|int
name|offsetStartBuffer
index|[]
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
DECL|field|offsetEndBuffer
specifier|private
name|int
name|offsetEndBuffer
index|[]
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
DECL|field|offsetIndex
specifier|private
name|int
name|offsetIndex
init|=
literal|0
decl_stmt|;
DECL|field|offsetFreq
specifier|private
name|int
name|offsetFreq
init|=
literal|0
decl_stmt|;
DECL|field|positions
specifier|private
name|boolean
name|positions
init|=
literal|false
decl_stmt|;
DECL|field|offsets
specifier|private
name|boolean
name|offsets
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|int
name|freq
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|prefix
init|=
name|StringHelper
operator|.
name|bytesDifference
argument_list|(
name|lastTerm
argument_list|,
name|term
argument_list|)
decl_stmt|;
specifier|final
name|int
name|suffix
init|=
name|term
operator|.
name|length
operator|-
name|prefix
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|suffix
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeBytes
argument_list|(
name|term
operator|.
name|bytes
argument_list|,
name|term
operator|.
name|offset
operator|+
name|prefix
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
name|lastTerm
operator|.
name|copyBytes
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|lastOffset
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|offsets
operator|&&
name|positions
condition|)
block|{
comment|// we might need to buffer if its a non-bulk merge
name|offsetStartBuffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|offsetStartBuffer
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|offsetEndBuffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|offsetEndBuffer
argument_list|,
name|freq
argument_list|)
expr_stmt|;
name|offsetIndex
operator|=
literal|0
expr_stmt|;
name|offsetFreq
operator|=
name|freq
expr_stmt|;
block|}
block|}
DECL|field|lastPosition
name|int
name|lastPosition
init|=
literal|0
decl_stmt|;
DECL|field|lastOffset
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|addProx
specifier|public
name|void
name|addProx
parameter_list|(
name|int
name|numProx
parameter_list|,
name|DataInput
name|positions
parameter_list|,
name|DataInput
name|offsets
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: technically we could just copy bytes and not re-encode if we knew the length...
if|if
condition|(
name|positions
operator|!=
literal|null
condition|)
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
name|numProx
condition|;
name|i
operator|++
control|)
block|{
name|tvf
operator|.
name|writeVInt
argument_list|(
name|positions
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|offsets
operator|!=
literal|null
condition|)
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
name|numProx
condition|;
name|i
operator|++
control|)
block|{
name|tvf
operator|.
name|writeVInt
argument_list|(
name|offsets
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|offsets
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addPosition
specifier|public
name|void
name|addPosition
parameter_list|(
name|int
name|position
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|positions
operator|&&
name|offsets
condition|)
block|{
comment|// write position delta
name|tvf
operator|.
name|writeVInt
argument_list|(
name|position
operator|-
name|lastPosition
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
comment|// buffer offsets
name|offsetStartBuffer
index|[
name|offsetIndex
index|]
operator|=
name|startOffset
expr_stmt|;
name|offsetEndBuffer
index|[
name|offsetIndex
index|]
operator|=
name|endOffset
expr_stmt|;
name|offsetIndex
operator|++
expr_stmt|;
comment|// dump buffer if we are done
if|if
condition|(
name|offsetIndex
operator|==
name|offsetFreq
condition|)
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
name|offsetIndex
condition|;
name|i
operator|++
control|)
block|{
name|tvf
operator|.
name|writeVInt
argument_list|(
name|offsetStartBuffer
index|[
name|i
index|]
operator|-
name|lastOffset
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|offsetEndBuffer
index|[
name|i
index|]
operator|-
name|offsetStartBuffer
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|lastOffset
operator|=
name|offsetEndBuffer
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|positions
condition|)
block|{
comment|// write position delta
name|tvf
operator|.
name|writeVInt
argument_list|(
name|position
operator|-
name|lastPosition
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|offsets
condition|)
block|{
comment|// write offset deltas
name|tvf
operator|.
name|writeVInt
argument_list|(
name|startOffset
operator|-
name|lastOffset
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|endOffset
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|lastOffset
operator|=
name|endOffset
expr_stmt|;
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
literal|""
argument_list|,
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_INDEX_EXTENSION
argument_list|)
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_DOCUMENTS_EXTENSION
argument_list|)
argument_list|,
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segment
argument_list|,
literal|""
argument_list|,
name|Lucene3xTermVectorsReader
operator|.
name|VECTORS_FIELDS_EXTENSION
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
literal|16
operator|!=
name|tvx
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
literal|"tvx size mismatch: mergedDocs is "
operator|+
name|numDocs
operator|+
literal|" but tvx size is "
operator|+
name|tvx
operator|.
name|getFilePointer
argument_list|()
operator|+
literal|" file="
operator|+
name|tvx
operator|.
name|toString
argument_list|()
operator|+
literal|"; now aborting this merge to prevent index corruption"
argument_list|)
throw|;
block|}
comment|/** Close all streams. */
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
comment|// make an effort to close all streams we can but remember and re-throw
comment|// the first exception encountered in this process
name|IOUtils
operator|.
name|close
argument_list|(
name|tvx
argument_list|,
name|tvd
argument_list|,
name|tvf
argument_list|)
expr_stmt|;
name|tvx
operator|=
name|tvd
operator|=
name|tvf
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

