begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene42
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene42
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|FieldInfosFormat
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
name|UndeadNormsProducer
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
operator|.
name|DocValuesType
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
name|SegmentInfo
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
name|IndexInput
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
comment|/**  * Lucene 4.2 Field Infos format.  * @deprecated Only for reading old 4.2-4.5 segments  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|Lucene42FieldInfosFormat
specifier|public
class|class
name|Lucene42FieldInfosFormat
extends|extends
name|FieldInfosFormat
block|{
comment|/** Sole constructor. */
DECL|method|Lucene42FieldInfosFormat
specifier|public
name|Lucene42FieldInfosFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|read
specifier|public
name|FieldInfos
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|iocontext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
literal|""
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|EXTENSION
argument_list|)
decl_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|iocontext
argument_list|)
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
name|checkHeader
argument_list|(
name|input
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|FORMAT_START
argument_list|,
name|Lucene42FieldInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//read in the size
name|FieldInfo
name|infos
index|[]
init|=
operator|new
name|FieldInfo
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldNumber
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|byte
name|bits
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isIndexed
init|=
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|IS_INDEXED
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storeTermVector
init|=
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|STORE_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|omitNorms
init|=
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|OMIT_NORMS
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storePayloads
init|=
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|STORE_PAYLOADS
operator|)
operator|!=
literal|0
decl_stmt|;
specifier|final
name|IndexOptions
name|indexOptions
decl_stmt|;
if|if
condition|(
operator|!
name|isIndexed
condition|)
block|{
name|indexOptions
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|OMIT_POSITIONS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|bits
operator|&
name|Lucene42FieldInfosFormat
operator|.
name|STORE_OFFSETS_IN_POSTINGS
operator|)
operator|!=
literal|0
condition|)
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
expr_stmt|;
block|}
else|else
block|{
name|indexOptions
operator|=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
expr_stmt|;
block|}
comment|// DV Types are packed in one byte
name|byte
name|val
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
specifier|final
name|DocValuesType
name|docValuesType
init|=
name|getDocValuesType
argument_list|(
name|input
argument_list|,
call|(
name|byte
call|)
argument_list|(
name|val
operator|&
literal|0x0F
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DocValuesType
name|normsType
init|=
name|getDocValuesType
argument_list|(
name|input
argument_list|,
call|(
name|byte
call|)
argument_list|(
operator|(
name|val
operator|>>>
literal|4
operator|)
operator|&
literal|0x0F
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|input
operator|.
name|readStringStringMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|isIndexed
operator|&&
name|omitNorms
operator|==
literal|false
operator|&&
name|normsType
operator|==
literal|null
condition|)
block|{
comment|// Undead norms!  Lucene42NormsProducer will check this and bring norms back from the dead:
name|UndeadNormsProducer
operator|.
name|setUndead
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
name|infos
index|[
name|i
index|]
operator|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|,
operator|-
literal|1
argument_list|,
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|attributes
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|checkEOF
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|(
name|infos
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|fieldInfos
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDocValuesType
specifier|private
specifier|static
name|DocValuesType
name|getDocValuesType
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|1
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|NUMERIC
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|2
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|BINARY
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|3
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|SORTED
return|;
block|}
elseif|else
if|if
condition|(
name|b
operator|==
literal|4
condition|)
block|{
return|return
name|DocValuesType
operator|.
name|SORTED_SET
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docvalues byte: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|FieldInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this codec can only be used for reading"
argument_list|)
throw|;
block|}
comment|/** Extension of field infos */
DECL|field|EXTENSION
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"fnm"
decl_stmt|;
comment|// Codec header
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene42FieldInfos"
decl_stmt|;
DECL|field|FORMAT_START
specifier|static
specifier|final
name|int
name|FORMAT_START
init|=
literal|0
decl_stmt|;
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_START
decl_stmt|;
comment|// Field flags
DECL|field|IS_INDEXED
specifier|static
specifier|final
name|byte
name|IS_INDEXED
init|=
literal|0x1
decl_stmt|;
DECL|field|STORE_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_TERMVECTOR
init|=
literal|0x2
decl_stmt|;
DECL|field|STORE_OFFSETS_IN_POSTINGS
specifier|static
specifier|final
name|byte
name|STORE_OFFSETS_IN_POSTINGS
init|=
literal|0x4
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|static
specifier|final
name|byte
name|OMIT_NORMS
init|=
literal|0x10
decl_stmt|;
DECL|field|STORE_PAYLOADS
specifier|static
specifier|final
name|byte
name|STORE_PAYLOADS
init|=
literal|0x20
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|static
specifier|final
name|byte
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|0x40
decl_stmt|;
DECL|field|OMIT_POSITIONS
specifier|static
specifier|final
name|byte
name|OMIT_POSITIONS
init|=
operator|-
literal|128
decl_stmt|;
block|}
end_class

end_unit

