begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
package|;
end_package

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
name|lucene50
operator|.
name|ForUtil
operator|.
name|MAX_DATA_SIZE
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
name|lucene50
operator|.
name|ForUtil
operator|.
name|MAX_ENCODED_SIZE
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|BLOCK_SIZE
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|DOC_CODEC
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|MAX_SKIP_LEVELS
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|PAY_CODEC
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|POS_CODEC
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|TERMS_CODEC
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|VERSION_CURRENT
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
name|PushPostingsWriterBase
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
name|lucene50
operator|.
name|Lucene50PostingsFormat
operator|.
name|IntBlockTermState
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
name|IndexWriter
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * Concrete class that writes docId(maybe frq,pos,offset,payloads) list  * with postings format.  *  * Postings list for each term will be stored separately.   *  * @see Lucene50SkipWriter for details about skipping setting and postings layout.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene50PostingsWriter
specifier|public
specifier|final
class|class
name|Lucene50PostingsWriter
extends|extends
name|PushPostingsWriterBase
block|{
DECL|field|docOut
name|IndexOutput
name|docOut
decl_stmt|;
DECL|field|posOut
name|IndexOutput
name|posOut
decl_stmt|;
DECL|field|payOut
name|IndexOutput
name|payOut
decl_stmt|;
DECL|field|emptyState
specifier|final
specifier|static
name|IntBlockTermState
name|emptyState
init|=
operator|new
name|IntBlockTermState
argument_list|()
decl_stmt|;
DECL|field|lastState
name|IntBlockTermState
name|lastState
decl_stmt|;
comment|// Holds starting file pointers for current term:
DECL|field|docStartFP
specifier|private
name|long
name|docStartFP
decl_stmt|;
DECL|field|posStartFP
specifier|private
name|long
name|posStartFP
decl_stmt|;
DECL|field|payStartFP
specifier|private
name|long
name|payStartFP
decl_stmt|;
DECL|field|docDeltaBuffer
specifier|final
name|int
index|[]
name|docDeltaBuffer
decl_stmt|;
DECL|field|freqBuffer
specifier|final
name|int
index|[]
name|freqBuffer
decl_stmt|;
DECL|field|docBufferUpto
specifier|private
name|int
name|docBufferUpto
decl_stmt|;
DECL|field|posDeltaBuffer
specifier|final
name|int
index|[]
name|posDeltaBuffer
decl_stmt|;
DECL|field|payloadLengthBuffer
specifier|final
name|int
index|[]
name|payloadLengthBuffer
decl_stmt|;
DECL|field|offsetStartDeltaBuffer
specifier|final
name|int
index|[]
name|offsetStartDeltaBuffer
decl_stmt|;
DECL|field|offsetLengthBuffer
specifier|final
name|int
index|[]
name|offsetLengthBuffer
decl_stmt|;
DECL|field|posBufferUpto
specifier|private
name|int
name|posBufferUpto
decl_stmt|;
DECL|field|payloadBytes
specifier|private
name|byte
index|[]
name|payloadBytes
decl_stmt|;
DECL|field|payloadByteUpto
specifier|private
name|int
name|payloadByteUpto
decl_stmt|;
DECL|field|lastBlockDocID
specifier|private
name|int
name|lastBlockDocID
decl_stmt|;
DECL|field|lastBlockPosFP
specifier|private
name|long
name|lastBlockPosFP
decl_stmt|;
DECL|field|lastBlockPayFP
specifier|private
name|long
name|lastBlockPayFP
decl_stmt|;
DECL|field|lastBlockPosBufferUpto
specifier|private
name|int
name|lastBlockPosBufferUpto
decl_stmt|;
DECL|field|lastBlockPayloadByteUpto
specifier|private
name|int
name|lastBlockPayloadByteUpto
decl_stmt|;
DECL|field|lastDocID
specifier|private
name|int
name|lastDocID
decl_stmt|;
DECL|field|lastPosition
specifier|private
name|int
name|lastPosition
decl_stmt|;
DECL|field|lastStartOffset
specifier|private
name|int
name|lastStartOffset
decl_stmt|;
DECL|field|docCount
specifier|private
name|int
name|docCount
decl_stmt|;
DECL|field|encoded
specifier|final
name|byte
index|[]
name|encoded
decl_stmt|;
DECL|field|forUtil
specifier|private
specifier|final
name|ForUtil
name|forUtil
decl_stmt|;
DECL|field|skipWriter
specifier|private
specifier|final
name|Lucene50SkipWriter
name|skipWriter
decl_stmt|;
comment|/** Creates a postings writer */
DECL|method|Lucene50PostingsWriter
specifier|public
name|Lucene50PostingsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|float
name|acceptableOverheadRatio
init|=
name|PackedInts
operator|.
name|COMPACT
decl_stmt|;
name|String
name|docFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|Lucene50PostingsFormat
operator|.
name|DOC_EXTENSION
argument_list|)
decl_stmt|;
name|docOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|docFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|IndexOutput
name|posOut
init|=
literal|null
decl_stmt|;
name|IndexOutput
name|payOut
init|=
literal|null
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|docOut
argument_list|,
name|DOC_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|forUtil
operator|=
operator|new
name|ForUtil
argument_list|(
name|acceptableOverheadRatio
argument_list|,
name|docOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasProx
argument_list|()
condition|)
block|{
name|posDeltaBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
name|String
name|posFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|Lucene50PostingsFormat
operator|.
name|POS_EXTENSION
argument_list|)
decl_stmt|;
name|posOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|posFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|posOut
argument_list|,
name|POS_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasPayloads
argument_list|()
condition|)
block|{
name|payloadBytes
operator|=
operator|new
name|byte
index|[
literal|128
index|]
expr_stmt|;
name|payloadLengthBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
block|}
else|else
block|{
name|payloadBytes
operator|=
literal|null
expr_stmt|;
name|payloadLengthBuffer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
name|offsetStartDeltaBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
name|offsetLengthBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
block|}
else|else
block|{
name|offsetStartDeltaBuffer
operator|=
literal|null
expr_stmt|;
name|offsetLengthBuffer
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|fieldInfos
operator|.
name|hasPayloads
argument_list|()
operator|||
name|state
operator|.
name|fieldInfos
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
name|String
name|payFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|,
name|Lucene50PostingsFormat
operator|.
name|PAY_EXTENSION
argument_list|)
decl_stmt|;
name|payOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|payFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|payOut
argument_list|,
name|PAY_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|posDeltaBuffer
operator|=
literal|null
expr_stmt|;
name|payloadLengthBuffer
operator|=
literal|null
expr_stmt|;
name|offsetStartDeltaBuffer
operator|=
literal|null
expr_stmt|;
name|offsetLengthBuffer
operator|=
literal|null
expr_stmt|;
name|payloadBytes
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|payOut
operator|=
name|payOut
expr_stmt|;
name|this
operator|.
name|posOut
operator|=
name|posOut
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
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
block|}
name|docDeltaBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
name|freqBuffer
operator|=
operator|new
name|int
index|[
name|MAX_DATA_SIZE
index|]
expr_stmt|;
comment|// TODO: should we try skipping every 2/4 blocks...?
name|skipWriter
operator|=
operator|new
name|Lucene50SkipWriter
argument_list|(
name|MAX_SKIP_LEVELS
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
name|encoded
operator|=
operator|new
name|byte
index|[
name|MAX_ENCODED_SIZE
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|IntBlockTermState
name|newTermState
parameter_list|()
block|{
return|return
operator|new
name|IntBlockTermState
argument_list|()
return|;
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
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|termsOut
argument_list|,
name|TERMS_CODEC
argument_list|,
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
name|termsOut
operator|.
name|writeVInt
argument_list|(
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|skipWriter
operator|.
name|setField
argument_list|(
name|writePositions
argument_list|,
name|writeOffsets
argument_list|,
name|writePayloads
argument_list|)
expr_stmt|;
name|lastState
operator|=
name|emptyState
expr_stmt|;
if|if
condition|(
name|writePositions
condition|)
block|{
if|if
condition|(
name|writePayloads
operator|||
name|writeOffsets
condition|)
block|{
return|return
literal|3
return|;
comment|// doc + pos + pay FP
block|}
else|else
block|{
return|return
literal|2
return|;
comment|// doc + pos FP
block|}
block|}
else|else
block|{
return|return
literal|1
return|;
comment|// doc FP
block|}
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
block|{
name|docStartFP
operator|=
name|docOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|writePositions
condition|)
block|{
name|posStartFP
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|writePayloads
operator|||
name|writeOffsets
condition|)
block|{
name|payStartFP
operator|=
name|payOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|lastBlockDocID
operator|=
operator|-
literal|1
expr_stmt|;
name|skipWriter
operator|.
name|resetSkip
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDoc
specifier|public
name|void
name|startDoc
parameter_list|(
name|int
name|docID
parameter_list|,
name|int
name|termDocFreq
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Have collected a block of docs, and get a new doc.
comment|// Should write skip data as well as postings list for
comment|// current block.
if|if
condition|(
name|lastBlockDocID
operator|!=
operator|-
literal|1
operator|&&
name|docBufferUpto
operator|==
literal|0
condition|)
block|{
name|skipWriter
operator|.
name|bufferSkip
argument_list|(
name|lastBlockDocID
argument_list|,
name|docCount
argument_list|,
name|lastBlockPosFP
argument_list|,
name|lastBlockPayFP
argument_list|,
name|lastBlockPosBufferUpto
argument_list|,
name|lastBlockPayloadByteUpto
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|docDelta
init|=
name|docID
operator|-
name|lastDocID
decl_stmt|;
if|if
condition|(
name|docID
operator|<
literal|0
operator|||
operator|(
name|docCount
operator|>
literal|0
operator|&&
name|docDelta
operator|<=
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"docs out of order ("
operator|+
name|docID
operator|+
literal|"<= "
operator|+
name|lastDocID
operator|+
literal|" )"
argument_list|,
name|docOut
argument_list|)
throw|;
block|}
name|docDeltaBuffer
index|[
name|docBufferUpto
index|]
operator|=
name|docDelta
expr_stmt|;
if|if
condition|(
name|writeFreqs
condition|)
block|{
name|freqBuffer
index|[
name|docBufferUpto
index|]
operator|=
name|termDocFreq
expr_stmt|;
block|}
name|docBufferUpto
operator|++
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
if|if
condition|(
name|docBufferUpto
operator|==
name|BLOCK_SIZE
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|docDeltaBuffer
argument_list|,
name|encoded
argument_list|,
name|docOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|writeFreqs
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|freqBuffer
argument_list|,
name|encoded
argument_list|,
name|docOut
argument_list|)
expr_stmt|;
block|}
comment|// NOTE: don't set docBufferUpto back to 0 here;
comment|// finishDoc will do so (because it needs to see that
comment|// the block was filled so it can save skip data)
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|lastStartOffset
operator|=
literal|0
expr_stmt|;
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
name|BytesRef
name|payload
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
name|position
operator|>
name|IndexWriter
operator|.
name|MAX_POSITION
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"position="
operator|+
name|position
operator|+
literal|" is too large (> IndexWriter.MAX_POSITION="
operator|+
name|IndexWriter
operator|.
name|MAX_POSITION
operator|+
literal|")"
argument_list|,
name|docOut
argument_list|)
throw|;
block|}
if|if
condition|(
name|position
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"position="
operator|+
name|position
operator|+
literal|" is< 0"
argument_list|,
name|docOut
argument_list|)
throw|;
block|}
name|posDeltaBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|position
operator|-
name|lastPosition
expr_stmt|;
if|if
condition|(
name|writePayloads
condition|)
block|{
if|if
condition|(
name|payload
operator|==
literal|null
operator|||
name|payload
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// no payload
name|payloadLengthBuffer
index|[
name|posBufferUpto
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|payloadLengthBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|payload
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|payloadByteUpto
operator|+
name|payload
operator|.
name|length
operator|>
name|payloadBytes
operator|.
name|length
condition|)
block|{
name|payloadBytes
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|payloadBytes
argument_list|,
name|payloadByteUpto
operator|+
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|payload
operator|.
name|bytes
argument_list|,
name|payload
operator|.
name|offset
argument_list|,
name|payloadBytes
argument_list|,
name|payloadByteUpto
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|payloadByteUpto
operator|+=
name|payload
operator|.
name|length
expr_stmt|;
block|}
block|}
if|if
condition|(
name|writeOffsets
condition|)
block|{
assert|assert
name|startOffset
operator|>=
name|lastStartOffset
assert|;
assert|assert
name|endOffset
operator|>=
name|startOffset
assert|;
name|offsetStartDeltaBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|startOffset
operator|-
name|lastStartOffset
expr_stmt|;
name|offsetLengthBuffer
index|[
name|posBufferUpto
index|]
operator|=
name|endOffset
operator|-
name|startOffset
expr_stmt|;
name|lastStartOffset
operator|=
name|startOffset
expr_stmt|;
block|}
name|posBufferUpto
operator|++
expr_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|posBufferUpto
operator|==
name|BLOCK_SIZE
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|posDeltaBuffer
argument_list|,
name|encoded
argument_list|,
name|posOut
argument_list|)
expr_stmt|;
if|if
condition|(
name|writePayloads
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|payloadLengthBuffer
argument_list|,
name|encoded
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
name|payOut
operator|.
name|writeVInt
argument_list|(
name|payloadByteUpto
argument_list|)
expr_stmt|;
name|payOut
operator|.
name|writeBytes
argument_list|(
name|payloadBytes
argument_list|,
literal|0
argument_list|,
name|payloadByteUpto
argument_list|)
expr_stmt|;
name|payloadByteUpto
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|writeOffsets
condition|)
block|{
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|offsetStartDeltaBuffer
argument_list|,
name|encoded
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
name|forUtil
operator|.
name|writeBlock
argument_list|(
name|offsetLengthBuffer
argument_list|,
name|encoded
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
name|posBufferUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Since we don't know df for current term, we had to buffer
comment|// those skip data for each block, and when a new doc comes,
comment|// write them to skip file.
if|if
condition|(
name|docBufferUpto
operator|==
name|BLOCK_SIZE
condition|)
block|{
name|lastBlockDocID
operator|=
name|lastDocID
expr_stmt|;
if|if
condition|(
name|posOut
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|payOut
operator|!=
literal|null
condition|)
block|{
name|lastBlockPayFP
operator|=
name|payOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
name|lastBlockPosFP
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastBlockPosBufferUpto
operator|=
name|posBufferUpto
expr_stmt|;
name|lastBlockPayloadByteUpto
operator|=
name|payloadByteUpto
expr_stmt|;
block|}
name|docBufferUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BlockTermState
name|_state
parameter_list|)
throws|throws
name|IOException
block|{
name|IntBlockTermState
name|state
init|=
operator|(
name|IntBlockTermState
operator|)
name|_state
decl_stmt|;
assert|assert
name|state
operator|.
name|docFreq
operator|>
literal|0
assert|;
comment|// TODO: wasteful we are counting this (counting # docs
comment|// for this term) in two places?
assert|assert
name|state
operator|.
name|docFreq
operator|==
name|docCount
operator|:
name|state
operator|.
name|docFreq
operator|+
literal|" vs "
operator|+
name|docCount
assert|;
comment|// docFreq == 1, don't write the single docid/freq to a separate file along with a pointer to it.
specifier|final
name|int
name|singletonDocID
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|docFreq
operator|==
literal|1
condition|)
block|{
comment|// pulse the singleton docid into the term dictionary, freq is implicitly totalTermFreq
name|singletonDocID
operator|=
name|docDeltaBuffer
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|singletonDocID
operator|=
operator|-
literal|1
expr_stmt|;
comment|// vInt encode the remaining doc deltas and freqs:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|docBufferUpto
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|docDelta
init|=
name|docDeltaBuffer
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|freqBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|writeFreqs
condition|)
block|{
name|docOut
operator|.
name|writeVInt
argument_list|(
name|docDelta
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|freqBuffer
index|[
name|i
index|]
operator|==
literal|1
condition|)
block|{
name|docOut
operator|.
name|writeVInt
argument_list|(
operator|(
name|docDelta
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
name|docOut
operator|.
name|writeVInt
argument_list|(
name|docDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
name|docOut
operator|.
name|writeVInt
argument_list|(
name|freq
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|long
name|lastPosBlockOffset
decl_stmt|;
if|if
condition|(
name|writePositions
condition|)
block|{
comment|// totalTermFreq is just total number of positions(or payloads, or offsets)
comment|// associated with current term.
assert|assert
name|state
operator|.
name|totalTermFreq
operator|!=
operator|-
literal|1
assert|;
if|if
condition|(
name|state
operator|.
name|totalTermFreq
operator|>
name|BLOCK_SIZE
condition|)
block|{
comment|// record file offset for last pos in last block
name|lastPosBlockOffset
operator|=
name|posOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|posStartFP
expr_stmt|;
block|}
else|else
block|{
name|lastPosBlockOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|posBufferUpto
operator|>
literal|0
condition|)
block|{
comment|// TODO: should we send offsets/payloads to
comment|// .pay...?  seems wasteful (have to store extra
comment|// vLong for low (< BLOCK_SIZE) DF terms = vast vast
comment|// majority)
comment|// vInt encode the remaining positions/payloads/offsets:
name|int
name|lastPayloadLength
init|=
operator|-
literal|1
decl_stmt|;
comment|// force first payload length to be written
name|int
name|lastOffsetLength
init|=
operator|-
literal|1
decl_stmt|;
comment|// force first offset length to be written
name|int
name|payloadBytesReadUpto
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
name|posBufferUpto
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|posDelta
init|=
name|posDeltaBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|writePayloads
condition|)
block|{
specifier|final
name|int
name|payloadLength
init|=
name|payloadLengthBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|payloadLength
operator|!=
name|lastPayloadLength
condition|)
block|{
name|lastPayloadLength
operator|=
name|payloadLength
expr_stmt|;
name|posOut
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
name|posOut
operator|.
name|writeVInt
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|posDelta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|!=
literal|0
condition|)
block|{
name|posOut
operator|.
name|writeBytes
argument_list|(
name|payloadBytes
argument_list|,
name|payloadBytesReadUpto
argument_list|,
name|payloadLength
argument_list|)
expr_stmt|;
name|payloadBytesReadUpto
operator|+=
name|payloadLength
expr_stmt|;
block|}
block|}
else|else
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|posDelta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writeOffsets
condition|)
block|{
name|int
name|delta
init|=
name|offsetStartDeltaBuffer
index|[
name|i
index|]
decl_stmt|;
name|int
name|length
init|=
name|offsetLengthBuffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|length
operator|==
name|lastOffsetLength
condition|)
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posOut
operator|.
name|writeVInt
argument_list|(
name|delta
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
name|posOut
operator|.
name|writeVInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|lastOffsetLength
operator|=
name|length
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|writePayloads
condition|)
block|{
assert|assert
name|payloadBytesReadUpto
operator|==
name|payloadByteUpto
assert|;
name|payloadByteUpto
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|lastPosBlockOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|long
name|skipOffset
decl_stmt|;
if|if
condition|(
name|docCount
operator|>
name|BLOCK_SIZE
condition|)
block|{
name|skipOffset
operator|=
name|skipWriter
operator|.
name|writeSkip
argument_list|(
name|docOut
argument_list|)
operator|-
name|docStartFP
expr_stmt|;
block|}
else|else
block|{
name|skipOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|state
operator|.
name|docStartFP
operator|=
name|docStartFP
expr_stmt|;
name|state
operator|.
name|posStartFP
operator|=
name|posStartFP
expr_stmt|;
name|state
operator|.
name|payStartFP
operator|=
name|payStartFP
expr_stmt|;
name|state
operator|.
name|singletonDocID
operator|=
name|singletonDocID
expr_stmt|;
name|state
operator|.
name|skipOffset
operator|=
name|skipOffset
expr_stmt|;
name|state
operator|.
name|lastPosBlockOffset
operator|=
name|lastPosBlockOffset
expr_stmt|;
name|docBufferUpto
operator|=
literal|0
expr_stmt|;
name|posBufferUpto
operator|=
literal|0
expr_stmt|;
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|docCount
operator|=
literal|0
expr_stmt|;
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
name|longs
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
name|IntBlockTermState
name|state
init|=
operator|(
name|IntBlockTermState
operator|)
name|_state
decl_stmt|;
if|if
condition|(
name|absolute
condition|)
block|{
name|lastState
operator|=
name|emptyState
expr_stmt|;
block|}
name|longs
index|[
literal|0
index|]
operator|=
name|state
operator|.
name|docStartFP
operator|-
name|lastState
operator|.
name|docStartFP
expr_stmt|;
if|if
condition|(
name|writePositions
condition|)
block|{
name|longs
index|[
literal|1
index|]
operator|=
name|state
operator|.
name|posStartFP
operator|-
name|lastState
operator|.
name|posStartFP
expr_stmt|;
if|if
condition|(
name|writePayloads
operator|||
name|writeOffsets
condition|)
block|{
name|longs
index|[
literal|2
index|]
operator|=
name|state
operator|.
name|payStartFP
operator|-
name|lastState
operator|.
name|payStartFP
expr_stmt|;
block|}
block|}
if|if
condition|(
name|state
operator|.
name|singletonDocID
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|singletonDocID
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|writePositions
condition|)
block|{
if|if
condition|(
name|state
operator|.
name|lastPosBlockOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|lastPosBlockOffset
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|state
operator|.
name|skipOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|state
operator|.
name|skipOffset
argument_list|)
expr_stmt|;
block|}
name|lastState
operator|=
name|state
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
comment|// TODO: add a finish() at least to PushBase? DV too...?
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|docOut
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|docOut
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|posOut
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|posOut
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payOut
operator|!=
literal|null
condition|)
block|{
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|payOut
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
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payOut
argument_list|)
expr_stmt|;
block|}
name|docOut
operator|=
name|posOut
operator|=
name|payOut
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

