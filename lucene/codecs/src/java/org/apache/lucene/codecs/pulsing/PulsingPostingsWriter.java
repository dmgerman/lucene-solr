begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.pulsing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pulsing
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
name|codecs
operator|.
name|BlockTermState
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
name|PostingsWriterBase
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
operator|.
name|IndexOptions
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
name|SegmentWriteState
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
name|TermsEnum
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
name|IOUtils
import|;
end_import

begin_comment
comment|// TODO: we now inline based on total TF of the term,
end_comment

begin_comment
comment|// but it might be better to inline by "net bytes used"
end_comment

begin_comment
comment|// so that a term that has only 1 posting but a huge
end_comment

begin_comment
comment|// payload would not be inlined.  Though this is
end_comment

begin_comment
comment|// presumably rare in practice...
end_comment

begin_comment
comment|/**   * Writer for the pulsing format.   *<p>  * Wraps another postings implementation and decides   * (based on total number of occurrences), whether a terms   * postings should be inlined into the term dictionary,  * or passed through to the wrapped writer.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|PulsingPostingsWriter
specifier|public
specifier|final
class|class
name|PulsingPostingsWriter
extends|extends
name|PostingsWriterBase
block|{
DECL|field|CODEC
specifier|final
specifier|static
name|String
name|CODEC
init|=
literal|"PulsedPostingsWriter"
decl_stmt|;
comment|// recording field summary
DECL|field|SUMMARY_EXTENSION
specifier|final
specifier|static
name|String
name|SUMMARY_EXTENSION
init|=
literal|"smy"
decl_stmt|;
comment|// To add a new version, increment from the last one, and
comment|// change VERSION_CURRENT to point to your new version:
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_META_ARRAY
specifier|final
specifier|static
name|int
name|VERSION_META_ARRAY
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_META_ARRAY
decl_stmt|;
DECL|field|segmentState
specifier|private
name|SegmentWriteState
name|segmentState
decl_stmt|;
DECL|field|fields
specifier|private
name|List
argument_list|<
name|FieldMetaData
argument_list|>
name|fields
decl_stmt|;
comment|// Reused by writeTerm:
DECL|field|docsEnum
specifier|private
name|DocsEnum
name|docsEnum
decl_stmt|;
DECL|field|posEnum
specifier|private
name|DocsAndPositionsEnum
name|posEnum
decl_stmt|;
DECL|field|enumFlags
specifier|private
name|int
name|enumFlags
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|RAMOutputStream
name|buffer
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
decl_stmt|;
comment|// information for wrapped PF, in current field
DECL|field|longsSize
specifier|private
name|int
name|longsSize
decl_stmt|;
DECL|field|longs
specifier|private
name|long
index|[]
name|longs
decl_stmt|;
DECL|field|fieldHasFreqs
specifier|private
name|boolean
name|fieldHasFreqs
decl_stmt|;
DECL|field|fieldHasPositions
specifier|private
name|boolean
name|fieldHasPositions
decl_stmt|;
DECL|field|fieldHasOffsets
specifier|private
name|boolean
name|fieldHasOffsets
decl_stmt|;
DECL|field|fieldHasPayloads
specifier|private
name|boolean
name|fieldHasPayloads
decl_stmt|;
DECL|field|absolute
name|boolean
name|absolute
decl_stmt|;
DECL|class|PulsingTermState
specifier|private
specifier|static
class|class
name|PulsingTermState
extends|extends
name|BlockTermState
block|{
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|wrappedState
specifier|private
name|BlockTermState
name|wrappedState
decl_stmt|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
literal|"inlined"
return|;
block|}
else|else
block|{
return|return
literal|"not inlined wrapped="
operator|+
name|wrappedState
return|;
block|}
block|}
block|}
DECL|class|FieldMetaData
specifier|private
specifier|static
specifier|final
class|class
name|FieldMetaData
block|{
DECL|field|fieldNumber
name|int
name|fieldNumber
decl_stmt|;
DECL|field|longsSize
name|int
name|longsSize
decl_stmt|;
DECL|method|FieldMetaData
name|FieldMetaData
parameter_list|(
name|int
name|number
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|fieldNumber
operator|=
name|number
expr_stmt|;
name|longsSize
operator|=
name|size
expr_stmt|;
block|}
block|}
comment|// TODO: -- lazy init this?  ie, if every single term
comment|// was inlined (eg for a "primary key" field) then we
comment|// never need to use this fallback?  Fallback writer for
comment|// non-inlined terms:
DECL|field|wrappedPostingsWriter
specifier|final
name|PostingsWriterBase
name|wrappedPostingsWriter
decl_stmt|;
DECL|field|maxPositions
specifier|final
name|int
name|maxPositions
decl_stmt|;
comment|/** If the total number of positions (summed across all docs    *  for this term) is<= maxPositions, then the postings are    *  inlined into terms dict */
DECL|method|PulsingPostingsWriter
specifier|public
name|PulsingPostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|int
name|maxPositions
parameter_list|,
name|PostingsWriterBase
name|wrappedPostingsWriter
parameter_list|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxPositions
operator|=
name|maxPositions
expr_stmt|;
comment|// We simply wrap another postings writer, but only call
comment|// on it when tot positions is>= the cutoff:
name|this
operator|.
name|wrappedPostingsWriter
operator|=
name|wrappedPostingsWriter
expr_stmt|;
name|this
operator|.
name|segmentState
operator|=
name|state
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|termsOut
argument_list|,
name|CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeVInt
argument_list|(
name|maxPositions
argument_list|)
expr_stmt|;
comment|// encode maxPositions in header
name|wrappedPostingsWriter
operator|.
name|init
argument_list|(
name|termsOut
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTerm
specifier|public
name|BlockTermState
name|writeTerm
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|TermsEnum
name|termsEnum
parameter_list|,
name|FixedBitSet
name|docsSeen
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First pass: figure out whether we should pulse this term
name|long
name|posCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|fieldHasPositions
operator|==
literal|false
condition|)
block|{
comment|// No positions:
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
name|enumFlags
argument_list|)
expr_stmt|;
assert|assert
name|docsEnum
operator|!=
literal|null
assert|;
while|while
condition|(
name|posCount
operator|<=
name|maxPositions
condition|)
block|{
if|if
condition|(
name|docsEnum
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|posCount
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|posEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posEnum
argument_list|,
name|enumFlags
argument_list|)
expr_stmt|;
assert|assert
name|posEnum
operator|!=
literal|null
assert|;
while|while
condition|(
name|posCount
operator|<=
name|maxPositions
condition|)
block|{
if|if
condition|(
name|posEnum
operator|.
name|nextDoc
argument_list|()
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|posCount
operator|+=
name|posEnum
operator|.
name|freq
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|posCount
operator|==
literal|0
condition|)
block|{
comment|// All docs were deleted
return|return
literal|null
return|;
block|}
comment|// Second pass: write postings
if|if
condition|(
name|posCount
operator|>
name|maxPositions
condition|)
block|{
comment|// Too many positions; do not pulse.  Just lset
comment|// wrapped postingsWriter encode the postings:
name|PulsingTermState
name|state
init|=
operator|new
name|PulsingTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|wrappedState
operator|=
name|wrappedPostingsWriter
operator|.
name|writeTerm
argument_list|(
name|term
argument_list|,
name|termsEnum
argument_list|,
name|docsSeen
argument_list|)
expr_stmt|;
name|state
operator|.
name|docFreq
operator|=
name|state
operator|.
name|wrappedState
operator|.
name|docFreq
expr_stmt|;
name|state
operator|.
name|totalTermFreq
operator|=
name|state
operator|.
name|wrappedState
operator|.
name|totalTermFreq
expr_stmt|;
return|return
name|state
return|;
block|}
else|else
block|{
comment|// Pulsed:
if|if
condition|(
name|fieldHasPositions
operator|==
literal|false
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
name|enumFlags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posEnum
operator|=
name|termsEnum
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|posEnum
argument_list|,
name|enumFlags
argument_list|)
expr_stmt|;
name|docsEnum
operator|=
name|posEnum
expr_stmt|;
block|}
assert|assert
name|docsEnum
operator|!=
literal|null
assert|;
comment|// There were few enough total occurrences for this
comment|// term, so we fully inline our postings data into
comment|// terms dict, now:
comment|// TODO: it'd be better to share this encoding logic
comment|// in some inner codec that knows how to write a
comment|// single doc / single position, etc.  This way if a
comment|// given codec wants to store other interesting
comment|// stuff, it could use this pulsing codec to do so
name|int
name|lastDocID
init|=
literal|0
decl_stmt|;
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastOffsetLength
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|docFreq
init|=
literal|0
decl_stmt|;
name|long
name|totalTermFreq
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|docID
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|docID
operator|==
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|)
block|{
break|break;
block|}
name|docsSeen
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|int
name|delta
init|=
name|docID
operator|-
name|lastDocID
decl_stmt|;
name|lastDocID
operator|=
name|docID
expr_stmt|;
name|docFreq
operator|++
expr_stmt|;
if|if
condition|(
name|fieldHasFreqs
condition|)
block|{
name|int
name|freq
init|=
name|docsEnum
operator|.
name|freq
argument_list|()
decl_stmt|;
name|totalTermFreq
operator|+=
name|freq
expr_stmt|;
if|if
condition|(
name|freq
operator|==
literal|1
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|delta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasPositions
condition|)
block|{
name|int
name|lastPos
init|=
literal|0
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|posIDX
init|=
literal|0
init|;
name|posIDX
operator|<
name|freq
condition|;
name|posIDX
operator|++
control|)
block|{
name|int
name|pos
init|=
name|posEnum
operator|.
name|nextPosition
argument_list|()
decl_stmt|;
name|int
name|posDelta
init|=
name|pos
operator|-
name|lastPos
decl_stmt|;
name|lastPos
operator|=
name|pos
expr_stmt|;
name|int
name|payloadLength
decl_stmt|;
name|BytesRef
name|payload
decl_stmt|;
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|payload
operator|=
name|posEnum
operator|.
name|getPayload
argument_list|()
expr_stmt|;
name|payloadLength
operator|=
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|payload
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
operator|(
name|posDelta
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|posDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|payloadLength
operator|=
literal|0
expr_stmt|;
name|payload
operator|=
literal|null
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|posDelta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldHasOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|posEnum
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|posEnum
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|int
name|offsetDelta
init|=
name|startOffset
operator|-
name|lastOffset
decl_stmt|;
name|int
name|offsetLength
init|=
name|endOffset
operator|-
name|startOffset
decl_stmt|;
if|if
condition|(
name|offsetLength
operator|!=
name|lastOffsetLength
condition|)
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|offsetDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|lastOffset
operator|=
name|startOffset
expr_stmt|;
name|lastOffsetLength
operator|=
name|offsetLength
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
assert|assert
name|fieldHasPayloads
assert|;
assert|assert
name|payload
operator|!=
literal|null
assert|;
name|buffer
operator|.
name|writeBytes
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|buffer
operator|.
name|writeVInt
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
name|PulsingTermState
name|state
init|=
operator|new
name|PulsingTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|buffer
operator|.
name|getFilePointer
argument_list|()
index|]
expr_stmt|;
name|state
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
name|state
operator|.
name|totalTermFreq
operator|=
name|fieldHasFreqs
condition|?
name|totalTermFreq
else|:
operator|-
literal|1
expr_stmt|;
name|buffer
operator|.
name|writeTo
argument_list|(
name|state
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|state
return|;
block|}
block|}
comment|// TODO: -- should we NOT reuse across fields?  would
comment|// be cleaner
comment|// Currently, this instance is re-used across fields, so
comment|// our parent calls setField whenever the field changes
annotation|@
name|Override
DECL|method|setField
specifier|public
name|int
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
expr_stmt|;
comment|//if (DEBUG) System.out.println("PW field=" + fieldInfo.name + " indexOptions=" + indexOptions);
name|fieldHasPayloads
operator|=
name|fieldInfo
operator|.
name|hasPayloads
argument_list|()
expr_stmt|;
name|absolute
operator|=
literal|false
expr_stmt|;
name|longsSize
operator|=
name|wrappedPostingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|longs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FieldMetaData
argument_list|(
name|fieldInfo
operator|.
name|number
argument_list|,
name|longsSize
argument_list|)
argument_list|)
expr_stmt|;
name|fieldHasFreqs
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|fieldHasPositions
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|fieldHasOffsets
operator|=
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
operator|>=
literal|0
expr_stmt|;
if|if
condition|(
name|fieldHasFreqs
operator|==
literal|false
condition|)
block|{
name|enumFlags
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldHasPositions
operator|==
literal|false
condition|)
block|{
name|enumFlags
operator|=
name|DocsEnum
operator|.
name|FLAG_FREQS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldHasOffsets
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|enumFlags
operator|=
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
expr_stmt|;
block|}
else|else
block|{
name|enumFlags
operator|=
literal|0
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|fieldHasPayloads
condition|)
block|{
name|enumFlags
operator|=
name|DocsAndPositionsEnum
operator|.
name|FLAG_PAYLOADS
operator||
name|DocsAndPositionsEnum
operator|.
name|FLAG_OFFSETS
expr_stmt|;
block|}
else|else
block|{
name|enumFlags
operator|=
name|DocsAndPositionsEnum
operator|.
name|FLAG_OFFSETS
expr_stmt|;
block|}
block|}
return|return
literal|0
return|;
comment|//DEBUG = BlockTreeTermsWriter.DEBUG;
block|}
annotation|@
name|Override
DECL|method|encodeTerm
specifier|public
name|void
name|encodeTerm
parameter_list|(
name|long
index|[]
name|empty
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_state
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
name|PulsingTermState
name|state
init|=
operator|(
name|PulsingTermState
operator|)
name|_state
decl_stmt|;
assert|assert
name|empty
operator|.
name|length
operator|==
literal|0
assert|;
name|this
operator|.
name|absolute
operator|=
name|this
operator|.
name|absolute
operator|||
name|absolute
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|bytes
operator|==
literal|null
condition|)
block|{
name|wrappedPostingsWriter
operator|.
name|encodeTerm
argument_list|(
name|longs
argument_list|,
name|buffer
argument_list|,
name|fieldInfo
argument_list|,
name|state
operator|.
name|wrappedState
argument_list|,
name|this
operator|.
name|absolute
argument_list|)
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
name|longsSize
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|longs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|this
operator|.
name|absolute
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|state
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|state
operator|.
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|absolute
operator|=
name|this
operator|.
name|absolute
operator|||
name|absolute
expr_stmt|;
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
name|wrappedPostingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|wrappedPostingsWriter
operator|instanceof
name|PulsingPostingsWriter
operator|||
name|VERSION_CURRENT
operator|<
name|VERSION_META_ARRAY
condition|)
block|{
return|return;
block|}
name|String
name|summaryFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentState
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentState
operator|.
name|segmentSuffix
argument_list|,
name|SUMMARY_EXTENSION
argument_list|)
decl_stmt|;
name|IndexOutput
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
name|segmentState
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|summaryFileName
argument_list|,
name|segmentState
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|fields
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldMetaData
name|field
range|:
name|fields
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|fieldNumber
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|longsSize
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

