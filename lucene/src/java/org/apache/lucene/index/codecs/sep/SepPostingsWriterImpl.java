begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
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
operator|.
name|sep
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
name|Set
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

begin_comment
comment|/** Writes frq to .frq, docs to .doc, pos to .pos, payloads  *  to .pyl, skip data to .skp  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|SepPostingsWriterImpl
specifier|public
specifier|final
class|class
name|SepPostingsWriterImpl
extends|extends
name|PostingsWriterBase
block|{
DECL|field|CODEC
specifier|final
specifier|static
name|String
name|CODEC
init|=
literal|"SepDocFreqSkip"
decl_stmt|;
DECL|field|DOC_EXTENSION
specifier|final
specifier|static
name|String
name|DOC_EXTENSION
init|=
literal|"doc"
decl_stmt|;
DECL|field|SKIP_EXTENSION
specifier|final
specifier|static
name|String
name|SKIP_EXTENSION
init|=
literal|"skp"
decl_stmt|;
DECL|field|FREQ_EXTENSION
specifier|final
specifier|static
name|String
name|FREQ_EXTENSION
init|=
literal|"frq"
decl_stmt|;
DECL|field|POS_EXTENSION
specifier|final
specifier|static
name|String
name|POS_EXTENSION
init|=
literal|"pos"
decl_stmt|;
DECL|field|PAYLOAD_EXTENSION
specifier|final
specifier|static
name|String
name|PAYLOAD_EXTENSION
init|=
literal|"pyl"
decl_stmt|;
comment|// Increment version to change it:
DECL|field|VERSION_START
specifier|final
specifier|static
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|final
specifier|static
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|freqOut
specifier|final
name|IntIndexOutput
name|freqOut
decl_stmt|;
DECL|field|freqIndex
specifier|final
name|IntIndexOutput
operator|.
name|Index
name|freqIndex
decl_stmt|;
DECL|field|posOut
specifier|final
name|IntIndexOutput
name|posOut
decl_stmt|;
DECL|field|posIndex
specifier|final
name|IntIndexOutput
operator|.
name|Index
name|posIndex
decl_stmt|;
DECL|field|docOut
specifier|final
name|IntIndexOutput
name|docOut
decl_stmt|;
DECL|field|docIndex
specifier|final
name|IntIndexOutput
operator|.
name|Index
name|docIndex
decl_stmt|;
DECL|field|payloadOut
specifier|final
name|IndexOutput
name|payloadOut
decl_stmt|;
DECL|field|skipOut
specifier|final
name|IndexOutput
name|skipOut
decl_stmt|;
DECL|field|termsOut
name|IndexOutput
name|termsOut
decl_stmt|;
DECL|field|skipListWriter
specifier|final
name|SepSkipListWriter
name|skipListWriter
decl_stmt|;
DECL|field|skipInterval
specifier|final
name|int
name|skipInterval
decl_stmt|;
DECL|field|maxSkipLevels
specifier|final
name|int
name|maxSkipLevels
decl_stmt|;
DECL|field|totalNumDocs
specifier|final
name|int
name|totalNumDocs
decl_stmt|;
DECL|field|storePayloads
name|boolean
name|storePayloads
decl_stmt|;
DECL|field|omitTF
name|boolean
name|omitTF
decl_stmt|;
comment|// Starts a new term
DECL|field|lastSkipStart
name|long
name|lastSkipStart
decl_stmt|;
DECL|field|fieldInfo
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|lastPayloadLength
name|int
name|lastPayloadLength
decl_stmt|;
DECL|field|lastPosition
name|int
name|lastPosition
decl_stmt|;
DECL|field|payloadStart
name|long
name|payloadStart
decl_stmt|;
DECL|field|lastPayloadStart
name|long
name|lastPayloadStart
decl_stmt|;
DECL|field|lastDocID
name|int
name|lastDocID
decl_stmt|;
DECL|field|df
name|int
name|df
decl_stmt|;
DECL|method|SepPostingsWriterImpl
specifier|public
name|SepPostingsWriterImpl
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|IntStreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
specifier|final
name|String
name|docFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|DOC_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|docFileName
argument_list|)
expr_stmt|;
name|docOut
operator|=
name|factory
operator|.
name|createOutput
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|docFileName
argument_list|)
expr_stmt|;
name|docIndex
operator|=
name|docOut
operator|.
name|index
argument_list|()
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
specifier|final
name|String
name|frqFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|FREQ_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|frqFileName
argument_list|)
expr_stmt|;
name|freqOut
operator|=
name|factory
operator|.
name|createOutput
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|frqFileName
argument_list|)
expr_stmt|;
name|freqIndex
operator|=
name|freqOut
operator|.
name|index
argument_list|()
expr_stmt|;
specifier|final
name|String
name|posFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|POS_EXTENSION
argument_list|)
decl_stmt|;
name|posOut
operator|=
name|factory
operator|.
name|createOutput
argument_list|(
name|state
operator|.
name|directory
argument_list|,
name|posFileName
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|posFileName
argument_list|)
expr_stmt|;
name|posIndex
operator|=
name|posOut
operator|.
name|index
argument_list|()
expr_stmt|;
comment|// TODO: -- only if at least one field stores payloads?
specifier|final
name|String
name|payloadFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|PAYLOAD_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|payloadFileName
argument_list|)
expr_stmt|;
name|payloadOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|payloadFileName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|freqOut
operator|=
literal|null
expr_stmt|;
name|freqIndex
operator|=
literal|null
expr_stmt|;
name|posOut
operator|=
literal|null
expr_stmt|;
name|posIndex
operator|=
literal|null
expr_stmt|;
name|payloadOut
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|String
name|skipFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
literal|""
argument_list|,
name|SKIP_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|skipFileName
argument_list|)
expr_stmt|;
name|skipOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|skipFileName
argument_list|)
expr_stmt|;
name|totalNumDocs
operator|=
name|state
operator|.
name|numDocs
expr_stmt|;
comment|// TODO: -- abstraction violation
name|skipListWriter
operator|=
operator|new
name|SepSkipListWriter
argument_list|(
name|state
operator|.
name|skipInterval
argument_list|,
name|state
operator|.
name|maxSkipLevels
argument_list|,
name|state
operator|.
name|numDocs
argument_list|,
name|freqOut
argument_list|,
name|docOut
argument_list|,
name|posOut
argument_list|,
name|payloadOut
argument_list|)
expr_stmt|;
name|skipInterval
operator|=
name|state
operator|.
name|skipInterval
expr_stmt|;
name|maxSkipLevels
operator|=
name|state
operator|.
name|maxSkipLevels
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|termsOut
operator|=
name|termsOut
expr_stmt|;
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
comment|// TODO: -- just ask skipper to "start" here
name|termsOut
operator|.
name|writeInt
argument_list|(
name|skipInterval
argument_list|)
expr_stmt|;
comment|// write skipInterval
name|termsOut
operator|.
name|writeInt
argument_list|(
name|maxSkipLevels
argument_list|)
expr_stmt|;
comment|// write maxSkipLevels
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|void
name|startTerm
parameter_list|()
throws|throws
name|IOException
block|{
name|docIndex
operator|.
name|mark
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|freqIndex
operator|.
name|mark
argument_list|()
expr_stmt|;
name|posIndex
operator|.
name|mark
argument_list|()
expr_stmt|;
name|payloadStart
operator|=
name|payloadOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastPayloadLength
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|skipListWriter
operator|.
name|resetSkip
argument_list|(
name|docIndex
argument_list|,
name|freqIndex
argument_list|,
name|posIndex
argument_list|)
expr_stmt|;
block|}
comment|// TODO: -- should we NOT reuse across fields?  would
comment|// be cleaner
comment|// Currently, this instance is re-used across fields, so
comment|// our parent calls setField whenever the field changes
annotation|@
name|Override
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|omitTF
operator|=
name|fieldInfo
operator|.
name|omitTermFreqAndPositions
expr_stmt|;
name|skipListWriter
operator|.
name|setOmitTF
argument_list|(
name|omitTF
argument_list|)
expr_stmt|;
name|storePayloads
operator|=
operator|!
name|omitTF
operator|&&
name|fieldInfo
operator|.
name|storePayloads
expr_stmt|;
block|}
comment|/** Adds a new doc in this term.  If this returns null    *  then we just skip consuming positions/payloads. */
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
specifier|final
name|int
name|delta
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
name|df
operator|>
literal|0
operator|&&
name|delta
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
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
operator|++
name|df
operator|%
name|skipInterval
operator|)
operator|==
literal|0
condition|)
block|{
comment|// TODO: -- awkward we have to make these two
comment|// separate calls to skipper
name|skipListWriter
operator|.
name|setSkipData
argument_list|(
name|lastDocID
argument_list|,
name|storePayloads
argument_list|,
name|lastPayloadLength
argument_list|)
expr_stmt|;
name|skipListWriter
operator|.
name|bufferSkip
argument_list|(
name|df
argument_list|)
expr_stmt|;
block|}
name|lastDocID
operator|=
name|docID
expr_stmt|;
name|docOut
operator|.
name|write
argument_list|(
name|delta
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|freqOut
operator|.
name|write
argument_list|(
name|termDocFreq
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Add a new position& payload */
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
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
operator|!
name|omitTF
assert|;
specifier|final
name|int
name|delta
init|=
name|position
operator|-
name|lastPosition
decl_stmt|;
name|lastPosition
operator|=
name|position
expr_stmt|;
if|if
condition|(
name|storePayloads
condition|)
block|{
specifier|final
name|int
name|payloadLength
init|=
name|payload
operator|==
literal|null
condition|?
literal|0
else|:
name|payload
operator|.
name|length
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
comment|// TODO: explore whether we get better compression
comment|// by not storing payloadLength into prox stream?
name|posOut
operator|.
name|write
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
name|posOut
operator|.
name|write
argument_list|(
name|payloadLength
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|posOut
operator|.
name|write
argument_list|(
name|delta
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|payloadLength
operator|>
literal|0
condition|)
block|{
name|payloadOut
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
name|payloadLength
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|posOut
operator|.
name|write
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
name|lastPosition
operator|=
name|position
expr_stmt|;
block|}
comment|/** Called when we are done adding positions& payloads */
annotation|@
name|Override
DECL|method|finishDoc
specifier|public
name|void
name|finishDoc
parameter_list|()
block|{
name|lastPosition
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Called when we are done adding docs to this term */
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|int
name|docCount
parameter_list|,
name|boolean
name|isIndexTerm
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|skipPos
init|=
name|skipOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// TODO: -- wasteful we are counting this in two places?
assert|assert
name|docCount
operator|>
literal|0
assert|;
assert|assert
name|docCount
operator|==
name|df
assert|;
comment|// TODO: -- only do this if once (consolidate the
comment|// conditional things that are written)
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|freqIndex
operator|.
name|write
argument_list|(
name|termsOut
argument_list|,
name|isIndexTerm
argument_list|)
expr_stmt|;
block|}
name|docIndex
operator|.
name|write
argument_list|(
name|termsOut
argument_list|,
name|isIndexTerm
argument_list|)
expr_stmt|;
if|if
condition|(
name|df
operator|>=
name|skipInterval
condition|)
block|{
name|skipListWriter
operator|.
name|writeSkip
argument_list|(
name|skipOut
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isIndexTerm
condition|)
block|{
name|termsOut
operator|.
name|writeVLong
argument_list|(
name|skipPos
argument_list|)
expr_stmt|;
name|lastSkipStart
operator|=
name|skipPos
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|df
operator|>=
name|skipInterval
condition|)
block|{
name|termsOut
operator|.
name|writeVLong
argument_list|(
name|skipPos
operator|-
name|lastSkipStart
argument_list|)
expr_stmt|;
name|lastSkipStart
operator|=
name|skipPos
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|omitTF
condition|)
block|{
name|posIndex
operator|.
name|write
argument_list|(
name|termsOut
argument_list|,
name|isIndexTerm
argument_list|)
expr_stmt|;
if|if
condition|(
name|isIndexTerm
condition|)
block|{
comment|// Write absolute at seek points
name|termsOut
operator|.
name|writeVLong
argument_list|(
name|payloadStart
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsOut
operator|.
name|writeVLong
argument_list|(
name|payloadStart
operator|-
name|lastPayloadStart
argument_list|)
expr_stmt|;
block|}
name|lastPayloadStart
operator|=
name|payloadStart
expr_stmt|;
block|}
name|lastDocID
operator|=
literal|0
expr_stmt|;
name|df
operator|=
literal|0
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
try|try
block|{
name|docOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|skipOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|freqOut
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|freqOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|posOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|payloadOut
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|getExtensions
specifier|public
specifier|static
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|DOC_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|FREQ_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|SKIP_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|POS_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|PAYLOAD_EXTENSION
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

