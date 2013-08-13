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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|util
operator|.
name|ByteBlockPool
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
name|RamUsageEstimator
import|;
end_import

begin_class
DECL|class|TermVectorsConsumerPerField
specifier|final
class|class
name|TermVectorsConsumerPerField
extends|extends
name|TermsHashConsumerPerField
block|{
DECL|field|termsHashPerField
specifier|final
name|TermsHashPerField
name|termsHashPerField
decl_stmt|;
DECL|field|termsWriter
specifier|final
name|TermVectorsConsumer
name|termsWriter
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriterPerThread
operator|.
name|DocState
name|docState
decl_stmt|;
DECL|field|fieldState
specifier|final
name|FieldInvertState
name|fieldState
decl_stmt|;
DECL|field|doVectors
name|boolean
name|doVectors
decl_stmt|;
DECL|field|doVectorPositions
name|boolean
name|doVectorPositions
decl_stmt|;
DECL|field|doVectorOffsets
name|boolean
name|doVectorOffsets
decl_stmt|;
DECL|field|doVectorPayloads
name|boolean
name|doVectorPayloads
decl_stmt|;
DECL|field|maxNumPostings
name|int
name|maxNumPostings
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
decl_stmt|;
DECL|field|payloadAttribute
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
DECL|field|hasPayloads
name|boolean
name|hasPayloads
decl_stmt|;
comment|// if enabled, and we actually saw any for this field
DECL|method|TermVectorsConsumerPerField
specifier|public
name|TermVectorsConsumerPerField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|TermVectorsConsumer
name|termsWriter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|termsHashPerField
operator|=
name|termsHashPerField
expr_stmt|;
name|this
operator|.
name|termsWriter
operator|=
name|termsWriter
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|docState
operator|=
name|termsHashPerField
operator|.
name|docState
expr_stmt|;
name|fieldState
operator|=
name|termsHashPerField
operator|.
name|fieldState
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStreamCount
name|int
name|getStreamCount
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
annotation|@
name|Override
DECL|method|start
name|boolean
name|start
parameter_list|(
name|IndexableField
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|doVectors
operator|=
literal|false
expr_stmt|;
name|doVectorPositions
operator|=
literal|false
expr_stmt|;
name|doVectorOffsets
operator|=
literal|false
expr_stmt|;
name|doVectorPayloads
operator|=
literal|false
expr_stmt|;
name|hasPayloads
operator|=
literal|false
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|IndexableField
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
condition|)
block|{
name|doVectors
operator|=
literal|true
expr_stmt|;
name|doVectorPositions
operator||=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
expr_stmt|;
name|doVectorOffsets
operator||=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
expr_stmt|;
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|doVectorPayloads
operator||=
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
comment|// TODO: move this check somewhere else, and impl the other missing ones
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector payloads without term vector positions (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector offsets when term vectors are not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector positions when term vectors are not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector payloads when term vectors are not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vectors when field is not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector offsets when field is not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector positions when field is not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot index term vector payloads when field is not indexed (field=\""
operator|+
name|field
operator|.
name|name
argument_list|()
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|doVectors
condition|)
block|{
name|termsWriter
operator|.
name|hasVectors
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|termsHashPerField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|// Only necessary if previous doc hit a
comment|// non-aborting exception while writing vectors in
comment|// this field:
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|// TODO: only if needed for performance
comment|//perThread.postingsCount = 0;
return|return
name|doVectors
return|;
block|}
DECL|method|abort
specifier|public
name|void
name|abort
parameter_list|()
block|{}
comment|/** Called once per field per document if term vectors    *  are enabled, to write the vectors to    *  RAMOutputStream, which is then quickly flushed to    *  the real term vectors files in the Directory. */
annotation|@
name|Override
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
operator|!
name|doVectors
operator|||
name|termsHashPerField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|termsWriter
operator|.
name|addFieldToFlush
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|finishDocument
name|void
name|finishDocument
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.finish start"
argument_list|)
assert|;
specifier|final
name|int
name|numPostings
init|=
name|termsHashPerField
operator|.
name|bytesHash
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|BytesRef
name|flushTerm
init|=
name|termsWriter
operator|.
name|flushTerm
decl_stmt|;
assert|assert
name|numPostings
operator|>=
literal|0
assert|;
if|if
condition|(
name|numPostings
operator|>
name|maxNumPostings
condition|)
name|maxNumPostings
operator|=
name|numPostings
expr_stmt|;
comment|// This is called once, after inverting all occurrences
comment|// of a given field in the doc.  At this point we flush
comment|// our hash into the DocWriter.
assert|assert
name|termsWriter
operator|.
name|vectorFieldsInOrder
argument_list|(
name|fieldInfo
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
specifier|final
name|TermVectorsWriter
name|tv
init|=
name|termsWriter
operator|.
name|writer
decl_stmt|;
specifier|final
name|int
index|[]
name|termIDs
init|=
name|termsHashPerField
operator|.
name|sortPostings
argument_list|(
name|tv
operator|.
name|getComparator
argument_list|()
argument_list|)
decl_stmt|;
name|tv
operator|.
name|startField
argument_list|(
name|fieldInfo
argument_list|,
name|numPostings
argument_list|,
name|doVectorPositions
argument_list|,
name|doVectorOffsets
argument_list|,
name|hasPayloads
argument_list|)
expr_stmt|;
specifier|final
name|ByteSliceReader
name|posReader
init|=
name|doVectorPositions
condition|?
name|termsWriter
operator|.
name|vectorSliceReaderPos
else|:
literal|null
decl_stmt|;
specifier|final
name|ByteSliceReader
name|offReader
init|=
name|doVectorOffsets
condition|?
name|termsWriter
operator|.
name|vectorSliceReaderOff
else|:
literal|null
decl_stmt|;
specifier|final
name|ByteBlockPool
name|termBytePool
init|=
name|termsHashPerField
operator|.
name|termBytePool
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numPostings
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|termID
init|=
name|termIDs
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
decl_stmt|;
comment|// Get BytesRef
name|termBytePool
operator|.
name|setBytesRef
argument_list|(
name|flushTerm
argument_list|,
name|postings
operator|.
name|textStarts
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|tv
operator|.
name|startTerm
argument_list|(
name|flushTerm
argument_list|,
name|freq
argument_list|)
expr_stmt|;
if|if
condition|(
name|doVectorPositions
operator|||
name|doVectorOffsets
condition|)
block|{
if|if
condition|(
name|posReader
operator|!=
literal|null
condition|)
block|{
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|posReader
argument_list|,
name|termID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|offReader
operator|!=
literal|null
condition|)
block|{
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|offReader
argument_list|,
name|termID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|tv
operator|.
name|addProx
argument_list|(
name|freq
argument_list|,
name|posReader
argument_list|,
name|offReader
argument_list|)
expr_stmt|;
block|}
name|tv
operator|.
name|finishTerm
argument_list|()
expr_stmt|;
block|}
name|tv
operator|.
name|finishField
argument_list|()
expr_stmt|;
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|fieldInfo
operator|.
name|setStoreTermVectors
argument_list|()
expr_stmt|;
block|}
DECL|method|shrinkHash
name|void
name|shrinkHash
parameter_list|()
block|{
name|termsHashPerField
operator|.
name|shrinkHash
argument_list|(
name|maxNumPostings
argument_list|)
expr_stmt|;
name|maxNumPostings
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start
name|void
name|start
parameter_list|(
name|IndexableField
name|f
parameter_list|)
block|{
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|offsetAttribute
operator|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offsetAttribute
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPayloads
operator|&&
name|fieldState
operator|.
name|attributeSource
operator|.
name|hasAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|payloadAttribute
operator|=
name|fieldState
operator|.
name|attributeSource
operator|.
name|getAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|payloadAttribute
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|writeProx
name|void
name|writeProx
parameter_list|(
name|TermVectorsPostingsArray
name|postings
parameter_list|,
name|int
name|termID
parameter_list|)
block|{
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|int
name|startOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
decl_stmt|;
name|int
name|endOffset
init|=
name|fieldState
operator|.
name|offset
operator|+
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
decl_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|startOffset
operator|-
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|endOffset
operator|-
name|startOffset
argument_list|)
expr_stmt|;
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
name|endOffset
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPositions
condition|)
block|{
specifier|final
name|BytesRef
name|payload
decl_stmt|;
if|if
condition|(
name|payloadAttribute
operator|==
literal|null
condition|)
block|{
name|payload
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|payload
operator|=
name|payloadAttribute
operator|.
name|getPayload
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|pos
init|=
name|fieldState
operator|.
name|position
operator|-
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
operator|&&
name|payload
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
operator|(
name|pos
operator|<<
literal|1
operator|)
operator||
literal|1
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|payload
operator|.
name|length
argument_list|)
expr_stmt|;
name|termsHashPerField
operator|.
name|writeBytes
argument_list|(
literal|0
argument_list|,
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
name|hasPayloads
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|pos
operator|<<
literal|1
argument_list|)
expr_stmt|;
block|}
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newTerm
name|void
name|newTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.newTerm start"
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
operator|=
literal|1
expr_stmt|;
name|postings
operator|.
name|lastOffsets
index|[
name|termID
index|]
operator|=
literal|0
expr_stmt|;
name|postings
operator|.
name|lastPositions
index|[
name|termID
index|]
operator|=
literal|0
expr_stmt|;
name|writeProx
argument_list|(
name|postings
argument_list|,
name|termID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addTerm
name|void
name|addTerm
parameter_list|(
specifier|final
name|int
name|termID
parameter_list|)
block|{
assert|assert
name|docState
operator|.
name|testPoint
argument_list|(
literal|"TermVectorsTermsWriterPerField.addTerm start"
argument_list|)
assert|;
name|TermVectorsPostingsArray
name|postings
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|termsHashPerField
operator|.
name|postingsArray
decl_stmt|;
name|postings
operator|.
name|freqs
index|[
name|termID
index|]
operator|++
expr_stmt|;
name|writeProx
argument_list|(
name|postings
argument_list|,
name|termID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|skippingLongTerm
name|void
name|skippingLongTerm
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|createPostingsArray
name|ParallelPostingsArray
name|createPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|class|TermVectorsPostingsArray
specifier|static
specifier|final
class|class
name|TermVectorsPostingsArray
extends|extends
name|ParallelPostingsArray
block|{
DECL|method|TermVectorsPostingsArray
specifier|public
name|TermVectorsPostingsArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|freqs
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastOffsets
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|lastPositions
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|field|freqs
name|int
index|[]
name|freqs
decl_stmt|;
comment|// How many times this term occurred in the current doc
DECL|field|lastOffsets
name|int
index|[]
name|lastOffsets
decl_stmt|;
comment|// Last offset we saw
DECL|field|lastPositions
name|int
index|[]
name|lastPositions
decl_stmt|;
comment|// Last position where this term occurred
annotation|@
name|Override
DECL|method|newInstance
name|ParallelPostingsArray
name|newInstance
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|TermVectorsPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyTo
name|void
name|copyTo
parameter_list|(
name|ParallelPostingsArray
name|toArray
parameter_list|,
name|int
name|numToCopy
parameter_list|)
block|{
assert|assert
name|toArray
operator|instanceof
name|TermVectorsPostingsArray
assert|;
name|TermVectorsPostingsArray
name|to
init|=
operator|(
name|TermVectorsPostingsArray
operator|)
name|toArray
decl_stmt|;
name|super
operator|.
name|copyTo
argument_list|(
name|toArray
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|freqs
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|freqs
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastOffsets
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|to
operator|.
name|lastPositions
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|super
operator|.
name|bytesPerPosting
argument_list|()
operator|+
literal|3
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
return|;
block|}
block|}
block|}
end_class

end_unit

