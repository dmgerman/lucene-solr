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
name|UnicodeUtil
import|;
end_import

begin_class
DECL|class|TermVectorsTermsWriterPerField
specifier|final
class|class
name|TermVectorsTermsWriterPerField
extends|extends
name|TermsHashConsumerPerField
block|{
DECL|field|perThread
specifier|final
name|TermVectorsTermsWriterPerThread
name|perThread
decl_stmt|;
DECL|field|termsHashPerField
specifier|final
name|TermsHashPerField
name|termsHashPerField
decl_stmt|;
DECL|field|termsWriter
specifier|final
name|TermVectorsTermsWriter
name|termsWriter
decl_stmt|;
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|docState
specifier|final
name|DocumentsWriter
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
DECL|field|maxNumPostings
name|int
name|maxNumPostings
decl_stmt|;
DECL|field|offsetAttribute
name|OffsetAttribute
name|offsetAttribute
init|=
literal|null
decl_stmt|;
DECL|method|TermVectorsTermsWriterPerField
specifier|public
name|TermVectorsTermsWriterPerField
parameter_list|(
name|TermsHashPerField
name|termsHashPerField
parameter_list|,
name|TermVectorsTermsWriterPerThread
name|perThread
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
name|perThread
operator|=
name|perThread
expr_stmt|;
name|this
operator|.
name|termsWriter
operator|=
name|perThread
operator|.
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
DECL|method|getStreamCount
name|int
name|getStreamCount
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
DECL|method|start
name|boolean
name|start
parameter_list|(
name|Fieldable
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
name|Fieldable
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
name|isIndexed
argument_list|()
operator|&&
name|field
operator|.
name|isTermVectorStored
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
name|isStorePositionWithTermVector
argument_list|()
expr_stmt|;
name|doVectorOffsets
operator||=
name|field
operator|.
name|isStoreOffsetWithTermVector
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|doVectors
condition|)
block|{
if|if
condition|(
name|perThread
operator|.
name|doc
operator|==
literal|null
condition|)
block|{
name|perThread
operator|.
name|doc
operator|=
name|termsWriter
operator|.
name|getPerDoc
argument_list|()
expr_stmt|;
name|perThread
operator|.
name|doc
operator|.
name|docID
operator|=
name|docState
operator|.
name|docID
expr_stmt|;
assert|assert
name|perThread
operator|.
name|doc
operator|.
name|numVectorFields
operator|==
literal|0
assert|;
assert|assert
literal|0
operator|==
name|perThread
operator|.
name|doc
operator|.
name|tvf
operator|.
name|length
argument_list|()
assert|;
assert|assert
literal|0
operator|==
name|perThread
operator|.
name|doc
operator|.
name|tvf
operator|.
name|getFilePointer
argument_list|()
assert|;
block|}
else|else
block|{
assert|assert
name|perThread
operator|.
name|doc
operator|.
name|docID
operator|==
name|docState
operator|.
name|docID
assert|;
if|if
condition|(
name|termsHashPerField
operator|.
name|numPostings
operator|!=
literal|0
condition|)
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
comment|/** Called once per field per document if term vectors    *  are enabled, to write the vectors to    *  RAMOutputStream, which is then quickly flushed to    *  * the real term vectors files in the Directory. */
DECL|method|finish
name|void
name|finish
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
name|numPostings
decl_stmt|;
assert|assert
name|numPostings
operator|>=
literal|0
assert|;
if|if
condition|(
operator|!
name|doVectors
operator|||
name|numPostings
operator|==
literal|0
condition|)
return|return;
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
specifier|final
name|IndexOutput
name|tvf
init|=
name|perThread
operator|.
name|doc
operator|.
name|tvf
decl_stmt|;
comment|// This is called once, after inverting all occurences
comment|// of a given field in the doc.  At this point we flush
comment|// our hash into the DocWriter.
assert|assert
name|fieldInfo
operator|.
name|storeTermVector
assert|;
assert|assert
name|perThread
operator|.
name|vectorFieldsInOrder
argument_list|(
name|fieldInfo
argument_list|)
assert|;
name|perThread
operator|.
name|doc
operator|.
name|addField
argument_list|(
name|termsHashPerField
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
specifier|final
name|RawPostingList
index|[]
name|postings
init|=
name|termsHashPerField
operator|.
name|sortPostings
argument_list|()
decl_stmt|;
name|tvf
operator|.
name|writeVInt
argument_list|(
name|numPostings
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|doVectorPositions
condition|)
name|bits
operator||=
name|TermVectorsReader
operator|.
name|STORE_POSITIONS_WITH_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|doVectorOffsets
condition|)
name|bits
operator||=
name|TermVectorsReader
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
name|int
name|encoderUpto
init|=
literal|0
decl_stmt|;
name|int
name|lastTermBytesCount
init|=
literal|0
decl_stmt|;
specifier|final
name|ByteSliceReader
name|reader
init|=
name|perThread
operator|.
name|vectorSliceReader
decl_stmt|;
specifier|final
name|char
index|[]
index|[]
name|charBuffers
init|=
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|charPool
operator|.
name|buffers
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
name|TermVectorsTermsWriter
operator|.
name|PostingList
name|posting
init|=
operator|(
name|TermVectorsTermsWriter
operator|.
name|PostingList
operator|)
name|postings
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|int
name|freq
init|=
name|posting
operator|.
name|freq
decl_stmt|;
specifier|final
name|char
index|[]
name|text2
init|=
name|charBuffers
index|[
name|posting
operator|.
name|textStart
operator|>>
name|DocumentsWriter
operator|.
name|CHAR_BLOCK_SHIFT
index|]
decl_stmt|;
specifier|final
name|int
name|start2
init|=
name|posting
operator|.
name|textStart
operator|&
name|DocumentsWriter
operator|.
name|CHAR_BLOCK_MASK
decl_stmt|;
comment|// We swap between two encoders to save copying
comment|// last Term's byte array
specifier|final
name|UnicodeUtil
operator|.
name|UTF8Result
name|utf8Result
init|=
name|perThread
operator|.
name|utf8Results
index|[
name|encoderUpto
index|]
decl_stmt|;
comment|// TODO: we could do this incrementally
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|text2
argument_list|,
name|start2
argument_list|,
name|utf8Result
argument_list|)
expr_stmt|;
specifier|final
name|int
name|termBytesCount
init|=
name|utf8Result
operator|.
name|length
decl_stmt|;
comment|// TODO: UTF16toUTF8 could tell us this prefix
comment|// Compute common prefix between last term and
comment|// this term
name|int
name|prefix
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|j
operator|>
literal|0
condition|)
block|{
specifier|final
name|byte
index|[]
name|lastTermBytes
init|=
name|perThread
operator|.
name|utf8Results
index|[
literal|1
operator|-
name|encoderUpto
index|]
operator|.
name|result
decl_stmt|;
specifier|final
name|byte
index|[]
name|termBytes
init|=
name|perThread
operator|.
name|utf8Results
index|[
name|encoderUpto
index|]
operator|.
name|result
decl_stmt|;
while|while
condition|(
name|prefix
operator|<
name|lastTermBytesCount
operator|&&
name|prefix
operator|<
name|termBytesCount
condition|)
block|{
if|if
condition|(
name|lastTermBytes
index|[
name|prefix
index|]
operator|!=
name|termBytes
index|[
name|prefix
index|]
condition|)
break|break;
name|prefix
operator|++
expr_stmt|;
block|}
block|}
name|encoderUpto
operator|=
literal|1
operator|-
name|encoderUpto
expr_stmt|;
name|lastTermBytesCount
operator|=
name|termBytesCount
expr_stmt|;
specifier|final
name|int
name|suffix
init|=
name|termBytesCount
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
name|utf8Result
operator|.
name|result
argument_list|,
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
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|reader
argument_list|,
name|posting
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|reader
operator|.
name|writeTo
argument_list|(
name|tvf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doVectorOffsets
condition|)
block|{
name|termsHashPerField
operator|.
name|initReader
argument_list|(
name|reader
argument_list|,
name|posting
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|writeTo
argument_list|(
name|tvf
argument_list|)
expr_stmt|;
block|}
block|}
name|termsHashPerField
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perThread
operator|.
name|termsHashPerThread
operator|.
name|reset
argument_list|(
literal|false
argument_list|)
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
DECL|method|start
name|void
name|start
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
if|if
condition|(
name|doVectorOffsets
operator|&&
name|fieldState
operator|.
name|attributeSource
operator|.
name|hasAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|offsetAttribute
operator|=
operator|(
name|OffsetAttribute
operator|)
name|fieldState
operator|.
name|attributeSource
operator|.
name|getAttribute
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
block|}
DECL|method|newTerm
name|void
name|newTerm
parameter_list|(
name|RawPostingList
name|p0
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
name|TermVectorsTermsWriter
operator|.
name|PostingList
name|p
init|=
operator|(
name|TermVectorsTermsWriter
operator|.
name|PostingList
operator|)
name|p0
decl_stmt|;
name|p
operator|.
name|freq
operator|=
literal|1
expr_stmt|;
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
decl_stmt|;
name|int
name|endOffset
init|=
name|fieldState
operator|.
name|offset
decl_stmt|;
if|if
condition|(
name|offsetAttribute
operator|!=
literal|null
condition|)
block|{
name|startOffset
operator|+=
name|offsetAttribute
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|endOffset
operator|+=
name|offsetAttribute
operator|.
name|endOffset
argument_list|()
expr_stmt|;
block|}
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|1
argument_list|,
name|startOffset
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
name|p
operator|.
name|lastOffset
operator|=
name|endOffset
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|fieldState
operator|.
name|position
argument_list|)
expr_stmt|;
name|p
operator|.
name|lastPosition
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
block|}
DECL|method|addTerm
name|void
name|addTerm
parameter_list|(
name|RawPostingList
name|p0
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
name|TermVectorsTermsWriter
operator|.
name|PostingList
name|p
init|=
operator|(
name|TermVectorsTermsWriter
operator|.
name|PostingList
operator|)
name|p0
decl_stmt|;
name|p
operator|.
name|freq
operator|++
expr_stmt|;
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
empty_stmt|;
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
name|p
operator|.
name|lastOffset
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
name|p
operator|.
name|lastOffset
operator|=
name|endOffset
expr_stmt|;
block|}
if|if
condition|(
name|doVectorPositions
condition|)
block|{
name|termsHashPerField
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|,
name|fieldState
operator|.
name|position
operator|-
name|p
operator|.
name|lastPosition
argument_list|)
expr_stmt|;
name|p
operator|.
name|lastPosition
operator|=
name|fieldState
operator|.
name|position
expr_stmt|;
block|}
block|}
DECL|method|skippingLongTerm
name|void
name|skippingLongTerm
parameter_list|()
block|{}
block|}
end_class

end_unit

