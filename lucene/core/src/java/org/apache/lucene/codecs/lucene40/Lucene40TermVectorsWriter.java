begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
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
name|TermVectorsReader
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
name|InvertedFields
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

begin_comment
comment|// TODO: make a new 4.0 TV format that encodes better
end_comment

begin_comment
comment|//   - use startOffset (not endOffset) as base for delta on
end_comment

begin_comment
comment|//     next startOffset because today for syns or ngrams or
end_comment

begin_comment
comment|//     WDF or shingles etc. we are encoding negative vints
end_comment

begin_comment
comment|//     (= slow, 5 bytes per)
end_comment

begin_comment
comment|//   - if doc has no term vectors, write 0 into the tvx
end_comment

begin_comment
comment|//     file; saves a seek to tvd only to read a 0 vint (and
end_comment

begin_comment
comment|//     saves a byte in tvd)
end_comment

begin_class
DECL|class|Lucene40TermVectorsWriter
specifier|public
specifier|final
class|class
name|Lucene40TermVectorsWriter
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
DECL|method|Lucene40TermVectorsWriter
specifier|public
name|Lucene40TermVectorsWriter
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
operator|.
name|STORE_POSITIONS_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|offsets
condition|)
name|bits
operator||=
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
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
name|Lucene40TermVectorsReader
operator|.
name|VECTORS_FIELDS_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Do a bulk copy of numDocs documents from reader to our    * streams.  This is used to expedite merging, if the    * field numbers are congruent.    */
DECL|method|addRawDocuments
specifier|private
name|void
name|addRawDocuments
parameter_list|(
name|Lucene40TermVectorsReader
name|reader
parameter_list|,
name|int
index|[]
name|tvdLengths
parameter_list|,
name|int
index|[]
name|tvfLengths
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|tvdPosition
init|=
name|tvd
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|tvfPosition
init|=
name|tvf
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|long
name|tvdStart
init|=
name|tvdPosition
decl_stmt|;
name|long
name|tvfStart
init|=
name|tvfPosition
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
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvdPosition
argument_list|)
expr_stmt|;
name|tvdPosition
operator|+=
name|tvdLengths
index|[
name|i
index|]
expr_stmt|;
name|tvx
operator|.
name|writeLong
argument_list|(
name|tvfPosition
argument_list|)
expr_stmt|;
name|tvfPosition
operator|+=
name|tvfLengths
index|[
name|i
index|]
expr_stmt|;
block|}
name|tvd
operator|.
name|copyBytes
argument_list|(
name|reader
operator|.
name|getTvdStream
argument_list|()
argument_list|,
name|tvdPosition
operator|-
name|tvdStart
argument_list|)
expr_stmt|;
name|tvf
operator|.
name|copyBytes
argument_list|(
name|reader
operator|.
name|getTvfStream
argument_list|()
argument_list|,
name|tvfPosition
operator|-
name|tvfStart
argument_list|)
expr_stmt|;
assert|assert
name|tvd
operator|.
name|getFilePointer
argument_list|()
operator|==
name|tvdPosition
assert|;
assert|assert
name|tvf
operator|.
name|getFilePointer
argument_list|()
operator|==
name|tvfPosition
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
comment|// Used for bulk-reading raw bytes for term vectors
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
name|rawDocLengths2
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
name|int
name|numDocs
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
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
name|Lucene40TermVectorsReader
name|matchingVectorsReader
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
name|TermVectorsReader
name|vectorsReader
init|=
name|matchingSegmentReader
operator|.
name|getTermVectorsReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|vectorsReader
operator|!=
literal|null
operator|&&
name|vectorsReader
operator|instanceof
name|Lucene40TermVectorsReader
condition|)
block|{
comment|// If the TV* files are an older format then they cannot read raw docs:
if|if
condition|(
operator|(
operator|(
name|Lucene40TermVectorsReader
operator|)
name|vectorsReader
operator|)
operator|.
name|canReadRawDocs
argument_list|()
condition|)
block|{
name|matchingVectorsReader
operator|=
operator|(
name|Lucene40TermVectorsReader
operator|)
name|vectorsReader
expr_stmt|;
block|}
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
name|numDocs
operator|+=
name|copyVectorsWithDeletions
argument_list|(
name|mergeState
argument_list|,
name|matchingVectorsReader
argument_list|,
name|reader
argument_list|,
name|rawDocLengths
argument_list|,
name|rawDocLengths2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|numDocs
operator|+=
name|copyVectorsNoDeletions
argument_list|(
name|mergeState
argument_list|,
name|matchingVectorsReader
argument_list|,
name|reader
argument_list|,
name|rawDocLengths
argument_list|,
name|rawDocLengths2
argument_list|)
expr_stmt|;
block|}
block|}
name|finish
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
return|return
name|numDocs
return|;
block|}
comment|/** Maximum number of contiguous documents to bulk-copy       when merging term vectors */
DECL|field|MAX_RAW_MERGE_DOCS
specifier|private
specifier|final
specifier|static
name|int
name|MAX_RAW_MERGE_DOCS
init|=
literal|4192
decl_stmt|;
DECL|method|copyVectorsWithDeletions
specifier|private
name|int
name|copyVectorsWithDeletions
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|Lucene40TermVectorsReader
name|matchingVectorsReader
parameter_list|,
specifier|final
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
parameter_list|,
name|int
name|rawDocLengths
index|[]
parameter_list|,
name|int
name|rawDocLengths2
index|[]
parameter_list|)
throws|throws
name|IOException
throws|,
name|MergeAbortedException
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
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|liveDocs
decl_stmt|;
name|int
name|totalNumDocs
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|matchingVectorsReader
operator|!=
literal|null
condition|)
block|{
comment|// We can bulk-copy because the fieldInfos are "congruent"
for|for
control|(
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
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
name|docNum
argument_list|)
condition|)
block|{
comment|// skip deleted docs
operator|++
name|docNum
expr_stmt|;
continue|continue;
block|}
comment|// We can optimize this case (doing a bulk byte copy) since the field
comment|// numbers are identical
name|int
name|start
init|=
name|docNum
decl_stmt|,
name|numDocs
init|=
literal|0
decl_stmt|;
do|do
block|{
name|docNum
operator|++
expr_stmt|;
name|numDocs
operator|++
expr_stmt|;
if|if
condition|(
name|docNum
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
name|docNum
argument_list|)
condition|)
block|{
name|docNum
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
name|matchingVectorsReader
operator|.
name|rawDocs
argument_list|(
name|rawDocLengths
argument_list|,
name|rawDocLengths2
argument_list|,
name|start
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|addRawDocuments
argument_list|(
name|matchingVectorsReader
argument_list|,
name|rawDocLengths
argument_list|,
name|rawDocLengths2
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|totalNumDocs
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
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|maxDoc
condition|;
name|docNum
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
name|docNum
argument_list|)
condition|)
block|{
comment|// skip deleted docs
continue|continue;
block|}
comment|// NOTE: it's very important to first assign to vectors then pass it to
comment|// termVectorsWriter.addAllDocVectors; see LUCENE-1282
name|InvertedFields
name|vectors
init|=
name|reader
operator|.
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docNum
argument_list|)
decl_stmt|;
name|addAllDocVectors
argument_list|(
name|vectors
argument_list|,
name|mergeState
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
name|totalNumDocs
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
name|totalNumDocs
return|;
block|}
DECL|method|copyVectorsNoDeletions
specifier|private
name|int
name|copyVectorsNoDeletions
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
specifier|final
name|Lucene40TermVectorsReader
name|matchingVectorsReader
parameter_list|,
specifier|final
name|MergeState
operator|.
name|IndexReaderAndLiveDocs
name|reader
parameter_list|,
name|int
name|rawDocLengths
index|[]
parameter_list|,
name|int
name|rawDocLengths2
index|[]
parameter_list|)
throws|throws
name|IOException
throws|,
name|MergeAbortedException
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
if|if
condition|(
name|matchingVectorsReader
operator|!=
literal|null
condition|)
block|{
comment|// We can bulk-copy because the fieldInfos are "congruent"
name|int
name|docCount
init|=
literal|0
decl_stmt|;
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
name|matchingVectorsReader
operator|.
name|rawDocs
argument_list|(
name|rawDocLengths
argument_list|,
name|rawDocLengths2
argument_list|,
name|docCount
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|addRawDocuments
argument_list|(
name|matchingVectorsReader
argument_list|,
name|rawDocLengths
argument_list|,
name|rawDocLengths2
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
name|int
name|docNum
init|=
literal|0
init|;
name|docNum
operator|<
name|maxDoc
condition|;
name|docNum
operator|++
control|)
block|{
comment|// NOTE: it's very important to first assign to vectors then pass it to
comment|// termVectorsWriter.addAllDocVectors; see LUCENE-1282
name|InvertedFields
name|vectors
init|=
name|reader
operator|.
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docNum
argument_list|)
decl_stmt|;
name|addAllDocVectors
argument_list|(
name|vectors
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
name|maxDoc
return|;
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
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

