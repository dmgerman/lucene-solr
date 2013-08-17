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
name|TempPostingsWriterBase
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
comment|/** FST based term dict, all the metadata held  *  as output of FST */
end_comment

begin_class
DECL|class|TempFSTTermsWriter
specifier|public
class|class
name|TempFSTTermsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|TERMS_EXTENSION
specifier|static
specifier|final
name|String
name|TERMS_EXTENSION
init|=
literal|"tmp"
decl_stmt|;
DECL|field|TERMS_CODEC_NAME
specifier|static
specifier|final
name|String
name|TERMS_CODEC_NAME
init|=
literal|"FST_TERMS_DICT"
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
DECL|field|postingsWriter
specifier|final
name|TempPostingsWriterBase
name|postingsWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|out
specifier|final
name|IndexOutput
name|out
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
DECL|method|TempFSTTermsWriter
specifier|public
name|TempFSTTermsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|,
name|TempPostingsWriterBase
name|postingsWriter
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|termsFileName
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
name|TERMS_EXTENSION
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
name|this
operator|.
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsFileName
argument_list|,
name|state
operator|.
name|context
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|writeHeader
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|.
name|init
argument_list|(
name|out
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
name|out
argument_list|)
expr_stmt|;
block|}
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
comment|// write field summary
specifier|final
name|long
name|dirStart
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
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
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|out
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
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVLong
argument_list|(
name|field
operator|.
name|sumDocFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|field
operator|.
name|docCount
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
name|field
operator|.
name|dict
operator|.
name|save
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|writeTrailer
argument_list|(
name|out
argument_list|,
name|dirStart
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
name|out
argument_list|,
name|postingsWriter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FieldMetaData
specifier|private
specifier|static
class|class
name|FieldMetaData
block|{
DECL|field|fieldInfo
specifier|public
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|numTerms
specifier|public
specifier|final
name|long
name|numTerms
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|public
specifier|final
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|sumDocFreq
specifier|public
specifier|final
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|docCount
specifier|public
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|longsSize
specifier|public
specifier|final
name|int
name|longsSize
decl_stmt|;
DECL|field|dict
specifier|public
specifier|final
name|FST
argument_list|<
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|>
name|dict
decl_stmt|;
DECL|method|FieldMetaData
specifier|public
name|FieldMetaData
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|long
name|numTerms
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|int
name|docCount
parameter_list|,
name|int
name|longsSize
parameter_list|,
name|FST
argument_list|<
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|>
name|fst
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|numTerms
operator|=
name|numTerms
expr_stmt|;
name|this
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
name|this
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|longsSize
operator|=
name|longsSize
expr_stmt|;
name|this
operator|.
name|dict
operator|=
name|fst
expr_stmt|;
block|}
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
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|>
name|builder
decl_stmt|;
DECL|field|outputs
specifier|private
specifier|final
name|TempTermOutputs
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
DECL|field|statsWriter
specifier|private
specifier|final
name|RAMOutputStream
name|statsWriter
init|=
operator|new
name|RAMOutputStream
argument_list|()
decl_stmt|;
DECL|field|metaWriter
specifier|private
specifier|final
name|RAMOutputStream
name|metaWriter
init|=
operator|new
name|RAMOutputStream
argument_list|()
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
operator|new
name|TempTermOutputs
argument_list|(
name|fieldInfo
argument_list|,
name|longsSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|builder
operator|=
operator|new
name|Builder
argument_list|<
name|TempTermOutputs
operator|.
name|TempMetaData
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
comment|// write term meta data into fst
specifier|final
name|BlockTermState
name|state
init|=
name|postingsWriter
operator|.
name|newTermState
argument_list|()
decl_stmt|;
specifier|final
name|TempTermOutputs
operator|.
name|TempMetaData
name|meta
init|=
operator|new
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|()
decl_stmt|;
name|meta
operator|.
name|longs
operator|=
operator|new
name|long
index|[
name|longsSize
index|]
expr_stmt|;
name|meta
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
name|meta
operator|.
name|docFreq
operator|=
name|state
operator|.
name|docFreq
operator|=
name|stats
operator|.
name|docFreq
expr_stmt|;
name|meta
operator|.
name|totalTermFreq
operator|=
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
name|meta
operator|.
name|longs
argument_list|,
name|metaWriter
argument_list|,
name|fieldInfo
argument_list|,
name|state
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bytesSize
init|=
operator|(
name|int
operator|)
name|metaWriter
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytesSize
operator|>
literal|0
condition|)
block|{
name|meta
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytesSize
index|]
expr_stmt|;
name|metaWriter
operator|.
name|writeTo
argument_list|(
name|meta
operator|.
name|bytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|metaWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
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
name|meta
argument_list|)
expr_stmt|;
name|numTerms
operator|++
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
comment|// save FST dict
if|if
condition|(
name|numTerms
operator|>
literal|0
condition|)
block|{
specifier|final
name|FST
argument_list|<
name|TempTermOutputs
operator|.
name|TempMetaData
argument_list|>
name|fst
init|=
name|builder
operator|.
name|finish
argument_list|()
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
operator|new
name|FieldMetaData
argument_list|(
name|fieldInfo
argument_list|,
name|numTerms
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|sumDocFreq
argument_list|,
name|docCount
argument_list|,
name|longsSize
argument_list|,
name|fst
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

