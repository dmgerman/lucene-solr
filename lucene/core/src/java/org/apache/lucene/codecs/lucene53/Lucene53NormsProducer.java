begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene53
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene53
package|;
end_package

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
name|HashMap
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
name|NormsProducer
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
name|NumericDocValues
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
name|SegmentReadState
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
name|ChecksumIndexInput
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
name|store
operator|.
name|RandomAccessInput
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene53
operator|.
name|Lucene53NormsFormat
operator|.
name|VERSION_CURRENT
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
name|lucene53
operator|.
name|Lucene53NormsFormat
operator|.
name|VERSION_START
import|;
end_import

begin_comment
comment|/**  * Reader for {@link Lucene53NormsFormat}  */
end_comment

begin_class
DECL|class|Lucene53NormsProducer
class|class
name|Lucene53NormsProducer
extends|extends
name|NormsProducer
block|{
comment|// metadata maps (just file pointers and minimal stuff)
DECL|field|norms
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NormsEntry
argument_list|>
name|norms
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|IndexInput
name|data
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|Lucene53NormsProducer
name|Lucene53NormsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|,
name|String
name|dataCodec
parameter_list|,
name|String
name|dataExtension
parameter_list|,
name|String
name|metaCodec
parameter_list|,
name|String
name|metaExtension
parameter_list|)
throws|throws
name|IOException
block|{
name|maxDoc
operator|=
name|state
operator|.
name|segmentInfo
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|String
name|metaName
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
name|metaExtension
argument_list|)
decl_stmt|;
name|int
name|version
init|=
operator|-
literal|1
decl_stmt|;
comment|// read in the entries from the metadata file.
try|try
init|(
name|ChecksumIndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|in
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_START
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
name|readFields
argument_list|(
name|in
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|in
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|dataName
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
name|dataExtension
argument_list|)
decl_stmt|;
name|data
operator|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|dataName
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
specifier|final
name|int
name|version2
init|=
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_START
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
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|version2
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Format versions mismatch: meta="
operator|+
name|version
operator|+
literal|",data="
operator|+
name|version2
argument_list|,
name|data
argument_list|)
throw|;
block|}
comment|// NOTE: data file is too costly to verify checksum against all the bytes on open,
comment|// but for now we at least verify proper structure of the checksum footer: which looks
comment|// for FOOTER_MAGIC + algorithmID. This is cheap and can detect some forms of corruption
comment|// such as file truncation.
name|CodecUtil
operator|.
name|retrieveChecksum
argument_list|(
name|data
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
name|this
operator|.
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readFields
specifier|private
name|void
name|readFields
parameter_list|(
name|IndexInput
name|meta
parameter_list|,
name|FieldInfos
name|infos
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|fieldNumber
init|=
name|meta
operator|.
name|readVInt
argument_list|()
decl_stmt|;
while|while
condition|(
name|fieldNumber
operator|!=
operator|-
literal|1
condition|)
block|{
name|FieldInfo
name|info
init|=
name|infos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid field number: "
operator|+
name|fieldNumber
argument_list|,
name|meta
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|info
operator|.
name|hasNorms
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid field: "
operator|+
name|info
operator|.
name|name
argument_list|,
name|meta
argument_list|)
throw|;
block|}
name|NormsEntry
name|entry
init|=
operator|new
name|NormsEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|bytesPerValue
operator|=
name|meta
operator|.
name|readByte
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|entry
operator|.
name|bytesPerValue
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|4
case|:
case|case
literal|8
case|:
break|break;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"Invalid bytesPerValue: "
operator|+
name|entry
operator|.
name|bytesPerValue
operator|+
literal|", field: "
operator|+
name|info
operator|.
name|name
argument_list|,
name|meta
argument_list|)
throw|;
block|}
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|norms
operator|.
name|put
argument_list|(
name|info
operator|.
name|number
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNorms
specifier|public
name|NumericDocValues
name|getNorms
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|NormsEntry
name|entry
init|=
name|norms
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|bytesPerValue
operator|==
literal|0
condition|)
block|{
specifier|final
name|long
name|value
init|=
name|entry
operator|.
name|offset
decl_stmt|;
return|return
operator|new
name|NormsIterator
argument_list|(
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
return|;
block|}
else|else
block|{
name|RandomAccessInput
name|slice
decl_stmt|;
synchronized|synchronized
init|(
name|data
init|)
block|{
switch|switch
condition|(
name|entry
operator|.
name|bytesPerValue
condition|)
block|{
case|case
literal|1
case|:
name|slice
operator|=
name|data
operator|.
name|randomAccessSlice
argument_list|(
name|entry
operator|.
name|offset
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
return|return
operator|new
name|NormsIterator
argument_list|(
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|slice
operator|.
name|readByte
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
return|;
case|case
literal|2
case|:
name|slice
operator|=
name|data
operator|.
name|randomAccessSlice
argument_list|(
name|entry
operator|.
name|offset
argument_list|,
name|maxDoc
operator|*
literal|2L
argument_list|)
expr_stmt|;
return|return
operator|new
name|NormsIterator
argument_list|(
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|slice
operator|.
name|readShort
argument_list|(
operator|(
operator|(
name|long
operator|)
name|docID
operator|)
operator|<<
literal|1L
argument_list|)
return|;
block|}
block|}
return|;
case|case
literal|4
case|:
name|slice
operator|=
name|data
operator|.
name|randomAccessSlice
argument_list|(
name|entry
operator|.
name|offset
argument_list|,
name|maxDoc
operator|*
literal|4L
argument_list|)
expr_stmt|;
return|return
operator|new
name|NormsIterator
argument_list|(
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|slice
operator|.
name|readInt
argument_list|(
operator|(
operator|(
name|long
operator|)
name|docID
operator|)
operator|<<
literal|2L
argument_list|)
return|;
block|}
block|}
return|;
case|case
literal|8
case|:
name|slice
operator|=
name|data
operator|.
name|randomAccessSlice
argument_list|(
name|entry
operator|.
name|offset
argument_list|,
name|maxDoc
operator|*
literal|8L
argument_list|)
expr_stmt|;
return|return
operator|new
name|NormsIterator
argument_list|(
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|slice
operator|.
name|readLong
argument_list|(
operator|(
operator|(
name|long
operator|)
name|docID
operator|)
operator|<<
literal|3L
argument_list|)
return|;
block|}
block|}
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
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
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|64L
operator|*
name|norms
operator|.
name|size
argument_list|()
return|;
comment|// good enough
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
DECL|class|NormsEntry
specifier|static
class|class
name|NormsEntry
block|{
DECL|field|bytesPerValue
name|byte
name|bytesPerValue
decl_stmt|;
DECL|field|offset
name|long
name|offset
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(fields="
operator|+
name|norms
operator|.
name|size
argument_list|()
operator|+
literal|")"
return|;
block|}
DECL|class|NormsIterator
specifier|private
specifier|static
specifier|abstract
class|class
name|NormsIterator
extends|extends
name|NumericDocValues
block|{
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|docID
specifier|protected
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|NormsIterator
specifier|public
name|NormsIterator
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
name|docID
operator|++
expr_stmt|;
if|if
condition|(
name|docID
operator|==
name|maxDoc
condition|)
block|{
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
name|docID
operator|=
name|target
expr_stmt|;
if|if
condition|(
name|docID
operator|>=
name|maxDoc
condition|)
block|{
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
return|return
name|docID
return|;
block|}
annotation|@
name|Override
DECL|method|cost
specifier|public
name|long
name|cost
parameter_list|()
block|{
comment|// TODO
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

