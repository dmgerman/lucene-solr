begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene41.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|values
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
name|lucene41
operator|.
name|values
operator|.
name|Lucene41DocValuesProducer
operator|.
name|DocValuesFactory
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
name|BinaryDocValues
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
name|PagedBytes
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
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene41
operator|.
name|values
operator|.
name|Lucene41BinaryDocValuesConsumer
operator|.
name|*
import|;
end_import

begin_class
DECL|class|Lucene41BinaryDocValues
specifier|public
specifier|final
class|class
name|Lucene41BinaryDocValues
extends|extends
name|BinaryDocValues
block|{
DECL|field|index
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|index
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|IndexInput
name|data
decl_stmt|;
DECL|field|baseOffset
specifier|private
specifier|final
name|long
name|baseOffset
decl_stmt|;
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|maxLength
specifier|private
name|int
name|maxLength
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|DocValuesFactory
argument_list|<
name|BinaryDocValues
argument_list|>
name|factory
decl_stmt|;
DECL|method|Lucene41BinaryDocValues
specifier|public
name|Lucene41BinaryDocValues
parameter_list|(
name|IndexInput
name|dataIn
parameter_list|,
name|long
name|dataOffset
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|maxLength
parameter_list|,
name|PackedInts
operator|.
name|Reader
name|index
parameter_list|,
name|DocValuesFactory
argument_list|<
name|BinaryDocValues
argument_list|>
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|data
operator|=
name|dataIn
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|maxLength
operator|=
name|maxLength
expr_stmt|;
name|this
operator|.
name|baseOffset
operator|=
name|dataOffset
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docId
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
try|try
block|{
specifier|final
name|long
name|offset
decl_stmt|;
specifier|final
name|int
name|length
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|offset
operator|=
name|size
operator|*
operator|(
operator|(
name|long
operator|)
name|docId
operator|)
expr_stmt|;
name|length
operator|=
name|size
expr_stmt|;
block|}
else|else
block|{
name|offset
operator|=
name|index
operator|.
name|get
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|baseOffset
operator|+
name|offset
argument_list|)
expr_stmt|;
comment|// Safe to do 1+docID because we write sentinel at the end:
specifier|final
name|long
name|nextOffset
init|=
name|index
operator|.
name|get
argument_list|(
literal|1
operator|+
name|docId
argument_list|)
decl_stmt|;
name|length
operator|=
call|(
name|int
call|)
argument_list|(
name|nextOffset
operator|-
name|offset
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|grow
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to get value for docID: "
operator|+
name|docId
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
DECL|method|isFixedLength
specifier|public
name|boolean
name|isFixedLength
parameter_list|()
block|{
return|return
name|index
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|maxLength
specifier|public
name|int
name|maxLength
parameter_list|()
block|{
return|return
name|maxLength
return|;
block|}
annotation|@
name|Override
DECL|method|newRAMInstance
specifier|public
name|BinaryDocValues
name|newRAMInstance
parameter_list|()
block|{
try|try
block|{
return|return
name|factory
operator|==
literal|null
condition|?
name|this
else|:
name|factory
operator|.
name|getInMemory
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|this
return|;
comment|// nocommit ?? now IOException
block|}
block|}
DECL|class|Factory
specifier|public
specifier|static
specifier|final
class|class
name|Factory
extends|extends
name|DocValuesFactory
argument_list|<
name|BinaryDocValues
argument_list|>
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|indexIn
specifier|private
specifier|final
name|IndexInput
name|indexIn
decl_stmt|;
DECL|field|indexHeader
specifier|private
specifier|final
name|PackedInts
operator|.
name|Header
name|indexHeader
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|maxLength
specifier|private
name|int
name|maxLength
decl_stmt|;
DECL|field|baseOffset
specifier|private
name|long
name|baseOffset
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
DECL|method|Factory
specifier|public
name|Factory
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfo
name|field
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|this
operator|.
name|valueCount
operator|=
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
name|IndexInput
name|datIn
init|=
literal|null
decl_stmt|;
name|IndexInput
name|indexIn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|datIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|Lucene41DocValuesConsumer
operator|.
name|getDocValuesFileName
argument_list|(
name|segmentInfo
argument_list|,
name|field
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|datIn
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxLength
operator|=
name|datIn
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|baseOffset
operator|=
name|datIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|==
name|VALUE_SIZE_VAR
condition|)
block|{
name|indexIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|Lucene41DocValuesConsumer
operator|.
name|getDocValuesFileName
argument_list|(
name|segmentInfo
argument_list|,
name|field
argument_list|,
name|Lucene41DocValuesConsumer
operator|.
name|INDEX_EXTENSION
argument_list|)
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|indexIn
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_START
argument_list|)
expr_stmt|;
name|indexHeader
operator|=
name|PackedInts
operator|.
name|readHeader
argument_list|(
name|indexIn
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexIn
operator|=
literal|null
expr_stmt|;
name|indexHeader
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|indexIn
operator|=
name|indexIn
expr_stmt|;
name|this
operator|.
name|datIn
operator|=
name|datIn
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
name|datIn
argument_list|,
name|indexIn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDirect
specifier|public
name|BinaryDocValues
name|getDirect
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene41BinaryDocValues
argument_list|(
name|datIn
operator|.
name|clone
argument_list|()
argument_list|,
name|this
operator|.
name|baseOffset
argument_list|,
name|size
argument_list|,
name|maxLength
argument_list|,
name|indexHeader
operator|==
literal|null
condition|?
literal|null
else|:
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|indexIn
operator|.
name|clone
argument_list|()
argument_list|,
name|indexHeader
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|getInMemory
specifier|public
name|BinaryDocValues
name|getInMemory
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nocommit simple in memory impl
name|PackedInts
operator|.
name|Reader
name|indexReader
init|=
name|indexHeader
operator|==
literal|null
condition|?
literal|null
else|:
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|indexIn
operator|.
name|clone
argument_list|()
argument_list|,
name|indexHeader
argument_list|)
decl_stmt|;
name|PagedBytes
name|bytes
init|=
operator|new
name|PagedBytes
argument_list|(
literal|15
argument_list|)
decl_stmt|;
name|bytes
operator|.
name|copy
argument_list|(
name|datIn
operator|.
name|clone
argument_list|()
argument_list|,
name|indexReader
operator|==
literal|null
condition|?
name|size
operator|*
name|valueCount
else|:
name|indexReader
operator|.
name|get
argument_list|(
name|indexReader
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|freeze
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Lucene41BinaryDocValues
argument_list|(
name|bytes
operator|.
name|getDataInput
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|,
name|maxLength
argument_list|,
name|indexReader
argument_list|,
literal|null
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
name|IOUtils
operator|.
name|close
argument_list|(
name|datIn
argument_list|,
name|indexIn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

