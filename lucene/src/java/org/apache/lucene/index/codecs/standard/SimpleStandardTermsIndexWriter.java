begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.standard
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
name|standard
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|index
operator|.
name|FieldInfos
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
name|packed
operator|.
name|PackedInts
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|SimpleStandardTermsIndexWriter
specifier|public
class|class
name|SimpleStandardTermsIndexWriter
extends|extends
name|StandardTermsIndexWriter
block|{
DECL|field|out
specifier|protected
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|CODEC_NAME
specifier|final
specifier|static
name|String
name|CODEC_NAME
init|=
literal|"SIMPLE_STANDARD_TERMS_INDEX"
decl_stmt|;
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
DECL|field|termIndexInterval
specifier|final
specifier|private
name|int
name|termIndexInterval
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|SimpleFieldWriter
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|SimpleFieldWriter
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|fieldInfos
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
comment|// unread
DECL|field|termsOut
specifier|private
name|IndexOutput
name|termsOut
decl_stmt|;
DECL|method|SimpleStandardTermsIndexWriter
specifier|public
name|SimpleStandardTermsIndexWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|indexFileName
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
name|StandardCodec
operator|.
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|indexFileName
argument_list|)
expr_stmt|;
name|termIndexInterval
operator|=
name|state
operator|.
name|termIndexInterval
expr_stmt|;
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|indexFileName
argument_list|)
expr_stmt|;
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|writeHeader
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|termIndexInterval
argument_list|)
expr_stmt|;
block|}
DECL|method|writeHeader
specifier|protected
name|void
name|writeHeader
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
comment|// Placeholder for dir offset
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setTermsOutput
specifier|public
name|void
name|setTermsOutput
parameter_list|(
name|IndexOutput
name|termsOut
parameter_list|)
block|{
name|this
operator|.
name|termsOut
operator|=
name|termsOut
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|FieldWriter
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
name|SimpleFieldWriter
name|writer
init|=
operator|new
name|SimpleFieldWriter
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
DECL|class|SimpleFieldWriter
specifier|private
class|class
name|SimpleFieldWriter
extends|extends
name|FieldWriter
block|{
DECL|field|fieldInfo
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numIndexTerms
name|int
name|numIndexTerms
decl_stmt|;
DECL|field|indexStart
specifier|final
name|long
name|indexStart
decl_stmt|;
DECL|field|termsStart
specifier|final
name|long
name|termsStart
decl_stmt|;
DECL|field|packedIndexStart
name|long
name|packedIndexStart
decl_stmt|;
DECL|field|packedOffsetsStart
name|long
name|packedOffsetsStart
decl_stmt|;
DECL|field|numTerms
specifier|private
name|int
name|numTerms
decl_stmt|;
comment|// TODO: we could conceivably make a PackedInts wrapper
comment|// that auto-grows... then we wouldn't force 6 bytes RAM
comment|// per index term:
DECL|field|termLengths
specifier|private
name|short
index|[]
name|termLengths
decl_stmt|;
DECL|field|termsPointerDeltas
specifier|private
name|int
index|[]
name|termsPointerDeltas
decl_stmt|;
DECL|field|lastTermsPointer
specifier|private
name|long
name|lastTermsPointer
decl_stmt|;
DECL|field|totTermLength
specifier|private
name|long
name|totTermLength
decl_stmt|;
DECL|method|SimpleFieldWriter
name|SimpleFieldWriter
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
name|indexStart
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|termsStart
operator|=
name|lastTermsPointer
operator|=
name|termsOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|termLengths
operator|=
operator|new
name|short
index|[
literal|0
index|]
expr_stmt|;
name|termsPointerDeltas
operator|=
operator|new
name|int
index|[
literal|0
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|checkIndexTerm
specifier|public
name|boolean
name|checkIndexTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|int
name|docFreq
parameter_list|)
throws|throws
name|IOException
block|{
comment|// First term is first indexed term:
if|if
condition|(
literal|0
operator|==
operator|(
name|numTerms
operator|++
operator|%
name|termIndexInterval
operator|)
condition|)
block|{
comment|// write full bytes
name|out
operator|.
name|writeBytes
argument_list|(
name|text
operator|.
name|bytes
argument_list|,
name|text
operator|.
name|offset
argument_list|,
name|text
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|termLengths
operator|.
name|length
operator|==
name|numIndexTerms
condition|)
block|{
name|termLengths
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|termLengths
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|termsPointerDeltas
operator|.
name|length
operator|==
name|numIndexTerms
condition|)
block|{
name|termsPointerDeltas
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|termsPointerDeltas
argument_list|)
expr_stmt|;
block|}
comment|// save delta terms pointer
specifier|final
name|long
name|fp
init|=
name|termsOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|termsPointerDeltas
index|[
name|numIndexTerms
index|]
operator|=
call|(
name|int
call|)
argument_list|(
name|fp
operator|-
name|lastTermsPointer
argument_list|)
expr_stmt|;
name|lastTermsPointer
operator|=
name|fp
expr_stmt|;
comment|// save term length (in bytes)
assert|assert
name|text
operator|.
name|length
operator|<=
name|Short
operator|.
name|MAX_VALUE
assert|;
name|termLengths
index|[
name|numIndexTerms
index|]
operator|=
operator|(
name|short
operator|)
name|text
operator|.
name|length
expr_stmt|;
name|totTermLength
operator|+=
name|text
operator|.
name|length
expr_stmt|;
name|numIndexTerms
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
comment|// write primary terms dict offsets
name|packedIndexStart
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
specifier|final
name|long
name|maxValue
init|=
name|termsOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|PackedInts
operator|.
name|Writer
name|w
init|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|out
argument_list|,
name|numIndexTerms
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxValue
argument_list|)
argument_list|)
decl_stmt|;
comment|// relative to our indexStart
name|long
name|upto
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
name|numIndexTerms
condition|;
name|i
operator|++
control|)
block|{
name|upto
operator|+=
name|termsPointerDeltas
index|[
name|i
index|]
expr_stmt|;
name|w
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
name|packedOffsetsStart
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
comment|// write offsets into the byte[] terms
name|w
operator|=
name|PackedInts
operator|.
name|getWriter
argument_list|(
name|out
argument_list|,
literal|1
operator|+
name|numIndexTerms
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|totTermLength
argument_list|)
argument_list|)
expr_stmt|;
name|upto
operator|=
literal|0
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
name|numIndexTerms
condition|;
name|i
operator|++
control|)
block|{
name|w
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|termLengths
index|[
name|i
index|]
expr_stmt|;
block|}
name|w
operator|.
name|add
argument_list|(
name|upto
argument_list|)
expr_stmt|;
name|w
operator|.
name|finish
argument_list|()
expr_stmt|;
comment|// our referrer holds onto us, while other fields are
comment|// being written, so don't tie up this RAM:
name|termLengths
operator|=
literal|null
expr_stmt|;
name|termsPointerDeltas
operator|=
literal|null
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
specifier|final
name|long
name|dirStart
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|fieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|SimpleFieldWriter
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|field
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|field
operator|.
name|numIndexTerms
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|field
operator|.
name|termsStart
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|field
operator|.
name|indexStart
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|field
operator|.
name|packedIndexStart
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|field
operator|.
name|packedOffsetsStart
argument_list|)
expr_stmt|;
block|}
name|writeTrailer
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTrailer
specifier|protected
name|void
name|writeTrailer
parameter_list|(
name|long
name|dirStart
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

