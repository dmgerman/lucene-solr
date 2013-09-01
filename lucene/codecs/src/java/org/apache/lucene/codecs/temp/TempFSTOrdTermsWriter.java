begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.temp
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|temp
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
name|IntsRef
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
name|fst
operator|.
name|Builder
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
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
name|codecs
operator|.
name|PostingsConsumer
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
name|FieldsConsumer
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
name|TermsConsumer
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
name|TermStats
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

begin_comment
comment|/**   * FST based term dict, the FST maps each term and its ord.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TempFSTOrdTermsWriter
specifier|public
class|class
name|TempFSTOrdTermsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|TERMS_INDEX_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_INDEX_EXTENSION
init|=
literal|"tix"
decl_stmt|;
DECL|field|TERMS_BLOCK_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_BLOCK_EXTENSION
init|=
literal|"tbk"
decl_stmt|;
DECL|field|TERMS_CODEC_NAME
specifier|static
specifier|final
name|String
name|TERMS_CODEC_NAME
init|=
literal|"FST_ORD_TERMS_DICT"
decl_stmt|;
DECL|field|TERMS_VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|TERMS_VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|TERMS_VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|TERMS_VERSION_CURRENT
init|=
name|TERMS_VERSION_START
decl_stmt|;
DECL|field|SKIP_INTERVAL
specifier|public
specifier|static
specifier|final
name|int
name|SKIP_INTERVAL
init|=
literal|8
decl_stmt|;
DECL|field|postingsWriter
specifier|final
name|PostingsWriterBase
name|postingsWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|fields
specifier|final
name|List
argument_list|<
name|FieldMetaData
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldMetaData
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|blockOut
name|IndexOutput
name|blockOut
init|=
literal|null
decl_stmt|;
DECL|field|indexOut
name|IndexOutput
name|indexOut
init|=
literal|null
decl_stmt|;
DECL|method|TempFSTOrdTermsWriter
specifier|public
name|TempFSTOrdTermsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|PostingsWriterBase
name|postingsWriter
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|termsIndexFileName
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
name|TERMS_INDEX_EXTENSION
argument_list|)
decl_stmt|;
specifier|final
name|String
name|termsBlockFileName
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
name|TERMS_BLOCK_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
name|this
operator|.
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|this
operator|.
name|indexOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsIndexFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockOut
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsBlockFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|writeHeader
argument_list|(
name|indexOut
argument_list|)
expr_stmt|;
name|writeHeader
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|.
name|init
argument_list|(
name|blockOut
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|indexOut
argument_list|,
name|blockOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TermsWriter
argument_list|(
name|field
argument_list|)
return|;
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
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|long
name|indexDirStart
init|=
name|indexOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|long
name|blockDirStart
init|=
name|blockOut
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// write field summary
name|blockOut
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
name|blockOut
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|numTerms
argument_list|)
expr_stmt|;
if|if
condition|(
name|field
operator|.
name|fieldInfo
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
condition|)
block|{
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
block|}
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumDocFreq
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|docCount
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|longsSize
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|statsOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|metaLongsOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|blockOut
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
argument_list|)
expr_stmt|;
name|field
operator|.
name|skipOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|statsOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|metaLongsOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|metaBytesOut
operator|.
name|writeTo
argument_list|(
name|blockOut
argument_list|)
expr_stmt|;
name|field
operator|.
name|dict
operator|.
name|save
argument_list|(
name|indexOut
argument_list|)
expr_stmt|;
block|}
name|writeTrailer
argument_list|(
name|indexOut
argument_list|,
name|indexDirStart
argument_list|)
expr_stmt|;
name|writeTrailer
argument_list|(
name|blockOut
argument_list|,
name|blockDirStart
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe2
parameter_list|)
block|{
name|ioe
operator|=
name|ioe2
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|ioe
argument_list|,
name|blockOut
argument_list|,
name|indexOut
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeHeader
specifier|private
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
name|TERMS_CODEC_NAME
argument_list|,
name|TERMS_VERSION_CURRENT
argument_list|)
expr_stmt|;
block|}
DECL|method|writeTrailer
specifier|private
name|void
name|writeTrailer
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
name|dirStart
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
block|}
DECL|class|FieldMetaData
specifier|private
specifier|static
class|class
name|FieldMetaData
block|{
DECL|field|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numTerms
specifier|public
name|long
name|numTerms
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|public
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|public
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|public
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
specifier|public
name|int
name|longsSize
decl_stmt|;
DECL|field|dict
specifier|public
name|FST
argument_list|<
name|Long
argument_list|>
name|dict
decl_stmt|;
comment|// TODO: block encode each part
comment|// vint encode next skip point (fully decoded when reading)
DECL|field|skipOut
specifier|public
name|RAMOutputStream
name|skipOut
decl_stmt|;
comment|// vint encode df, (ttf-df)
DECL|field|statsOut
specifier|public
name|RAMOutputStream
name|statsOut
decl_stmt|;
comment|// vint encode monotonic long[] and length for corresponding byte[]
DECL|field|metaLongsOut
specifier|public
name|RAMOutputStream
name|metaLongsOut
decl_stmt|;
comment|// generic byte[]
DECL|field|metaBytesOut
specifier|public
name|RAMOutputStream
name|metaBytesOut
decl_stmt|;
block|}
DECL|class|TermsWriter
specifier|final
class|class
name|TermsWriter
extends|extends
name|TermsConsumer
block|{
DECL|field|builder
specifier|private
specifier|final
name|Builder
argument_list|<
name|Long
argument_list|>
name|builder
decl_stmt|;
DECL|field|outputs
specifier|private
specifier|final
name|PositiveIntOutputs
name|outputs
decl_stmt|;
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|longsSize
specifier|private
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|numTerms
specifier|private
name|long
name|numTerms
decl_stmt|;
DECL|field|scratchTerm
specifier|private
specifier|final
name|IntsRef
name|scratchTerm
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
DECL|field|statsOut
specifier|private
specifier|final
name|RAMOutputStream
name|statsOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|metaLongsOut
specifier|private
specifier|final
name|RAMOutputStream
name|metaLongsOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|metaBytesOut
specifier|private
specifier|final
name|RAMOutputStream
name|metaBytesOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|skipOut
specifier|private
specifier|final
name|RAMOutputStream
name|skipOut
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|lastBlockStatsFP
specifier|private
name|long
name|lastBlockStatsFP
decl_stmt|;
DECL|field|lastBlockMetaLongsFP
specifier|private
name|long
name|lastBlockMetaLongsFP
decl_stmt|;
DECL|field|lastBlockMetaBytesFP
specifier|private
name|long
name|lastBlockMetaBytesFP
decl_stmt|;
DECL|field|lastBlockLongs
specifier|private
name|long
index|[]
name|lastBlockLongs
decl_stmt|;
DECL|field|lastLongs
specifier|private
name|long
index|[]
name|lastLongs
decl_stmt|;
DECL|field|lastMetaBytesFP
specifier|private
name|long
name|lastMetaBytesFP
decl_stmt|;
DECL|method|TermsWriter
name|TermsWriter
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|)
block|{
name|this
operator|.
name|numTerms
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|postingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|outputs
operator|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
expr_stmt|;
name|this
operator|.
name|builder
operator|=
operator|new
name|Builder
argument_list|<
name|Long
argument_list|>
argument_list|(
name|FST
operator|.
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastBlockStatsFP
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastBlockMetaLongsFP
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastBlockMetaBytesFP
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|lastBlockLongs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|this
operator|.
name|lastLongs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|this
operator|.
name|lastMetaBytesFP
operator|=
literal|0
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
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|postingsWriter
operator|.
name|startTerm
argument_list|()
expr_stmt|;
return|return
name|postingsWriter
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|TermStats
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numTerms
operator|>
literal|0
operator|&&
name|numTerms
operator|%
name|SKIP_INTERVAL
operator|==
literal|0
condition|)
block|{
name|bufferSkip
argument_list|()
expr_stmt|;
block|}
comment|// write term meta data into fst
specifier|final
name|long
name|longs
index|[]
init|=
operator|new
name|long
index|[
name|longsSize
index|]
decl_stmt|;
specifier|final
name|long
name|delta
init|=
name|stats
operator|.
name|totalTermFreq
operator|-
name|stats
operator|.
name|docFreq
decl_stmt|;
if|if
condition|(
name|stats
operator|.
name|totalTermFreq
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|delta
operator|==
literal|0
condition|)
block|{
name|statsOut
operator|.
name|writeVInt
argument_list|(
name|stats
operator|.
name|docFreq
operator|<<
literal|1
operator||
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|statsOut
operator|.
name|writeVInt
argument_list|(
name|stats
operator|.
name|docFreq
operator|<<
literal|1
operator||
literal|0
argument_list|)
expr_stmt|;
name|statsOut
operator|.
name|writeVLong
argument_list|(
name|stats
operator|.
name|totalTermFreq
operator|-
name|stats
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|statsOut
operator|.
name|writeVInt
argument_list|(
name|stats
operator|.
name|docFreq
argument_list|)
expr_stmt|;
block|}
name|BlockTermState
name|state
init|=
name|postingsWriter
operator|.
name|newTermState
argument_list|()
decl_stmt|;
name|state
operator|.
name|docFreq
operator|=
name|stats
operator|.
name|docFreq
expr_stmt|;
name|state
operator|.
name|totalTermFreq
operator|=
name|stats
operator|.
name|totalTermFreq
expr_stmt|;
name|postingsWriter
operator|.
name|finishTerm
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|postingsWriter
operator|.
name|encodeTerm
argument_list|(
name|longs
argument_list|,
name|metaBytesOut
argument_list|,
name|fieldInfo
argument_list|,
name|state
argument_list|,
literal|true
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
name|metaLongsOut
operator|.
name|writeVLong
argument_list|(
name|longs
index|[
name|i
index|]
operator|-
name|lastLongs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|lastLongs
index|[
name|i
index|]
operator|=
name|longs
index|[
name|i
index|]
expr_stmt|;
block|}
name|metaLongsOut
operator|.
name|writeVLong
argument_list|(
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastMetaBytesFP
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|text
argument_list|,
name|scratchTerm
argument_list|)
argument_list|,
name|numTerms
argument_list|)
expr_stmt|;
name|numTerms
operator|++
expr_stmt|;
name|lastMetaBytesFP
operator|=
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|numTerms
operator|>
literal|0
condition|)
block|{
specifier|final
name|FieldMetaData
name|metadata
init|=
operator|new
name|FieldMetaData
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|metadata
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|metadata
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|metadata
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|metadata
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|metadata
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
name|metadata
operator|.
name|skipOut
operator|=
name|skipOut
expr_stmt|;
name|metadata
operator|.
name|statsOut
operator|=
name|statsOut
expr_stmt|;
name|metadata
operator|.
name|metaLongsOut
operator|=
name|metaLongsOut
expr_stmt|;
name|metadata
operator|.
name|metaBytesOut
operator|=
name|metaBytesOut
expr_stmt|;
name|metadata
operator|.
name|dict
operator|=
name|builder
operator|.
name|finish
argument_list|()
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bufferSkip
specifier|private
name|void
name|bufferSkip
parameter_list|()
throws|throws
name|IOException
block|{
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|statsOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastBlockStatsFP
argument_list|)
expr_stmt|;
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|metaLongsOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastBlockMetaLongsFP
argument_list|)
expr_stmt|;
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
operator|-
name|lastBlockMetaBytesFP
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
name|skipOut
operator|.
name|writeVLong
argument_list|(
name|lastLongs
index|[
name|i
index|]
operator|-
name|lastBlockLongs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|lastBlockStatsFP
operator|=
name|statsOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastBlockMetaLongsFP
operator|=
name|metaLongsOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|lastBlockMetaBytesFP
operator|=
name|metaBytesOut
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lastLongs
argument_list|,
literal|0
argument_list|,
name|lastBlockLongs
argument_list|,
literal|0
argument_list|,
name|longsSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

