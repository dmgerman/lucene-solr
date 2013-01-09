begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene41
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
name|SimpleDVProducer
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
name|DocValues
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
name|index
operator|.
name|SortedDocValues
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_class
DECL|class|Lucene41SimpleDocValuesProducer
class|class
name|Lucene41SimpleDocValuesProducer
extends|extends
name|SimpleDVProducer
block|{
DECL|field|numerics
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|numerics
decl_stmt|;
DECL|field|ords
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
name|ords
decl_stmt|;
DECL|field|binaries
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
name|binaries
decl_stmt|;
DECL|field|data
specifier|private
specifier|final
name|IndexInput
name|data
decl_stmt|;
DECL|method|Lucene41SimpleDocValuesProducer
name|Lucene41SimpleDocValuesProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
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
literal|"dvm"
argument_list|)
decl_stmt|;
comment|// read in the entries from the metadata file.
name|IndexInput
name|in
init|=
name|state
operator|.
name|directory
operator|.
name|openInput
argument_list|(
name|metaName
argument_list|,
name|state
operator|.
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|numerics
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
argument_list|()
expr_stmt|;
name|ords
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|NumericEntry
argument_list|>
argument_list|()
expr_stmt|;
name|binaries
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|BinaryEntry
argument_list|>
argument_list|()
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
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|in
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
literal|"dvd"
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
name|DocValues
operator|.
name|Type
name|type
init|=
name|infos
operator|.
name|fieldInfo
argument_list|(
name|fieldNumber
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
if|if
condition|(
name|DocValues
operator|.
name|isNumber
argument_list|(
name|type
argument_list|)
operator|||
name|DocValues
operator|.
name|isFloat
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DocValues
operator|.
name|isBytes
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|BinaryEntry
name|b
init|=
name|readBinaryEntry
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|binaries
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|minLength
operator|!=
name|b
operator|.
name|maxLength
condition|)
block|{
if|if
condition|(
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"binary entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
comment|// variable length byte[]: read addresses as a numeric dv field
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|DocValues
operator|.
name|isSortedBytes
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|BinaryEntry
name|b
init|=
name|readBinaryEntry
argument_list|(
name|meta
argument_list|)
decl_stmt|;
name|binaries
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|minLength
operator|!=
name|b
operator|.
name|maxLength
condition|)
block|{
if|if
condition|(
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sorted entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
comment|// variable length byte[]: read addresses as a numeric dv field
name|numerics
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sorted byte[]: read ords as a numeric dv field
if|if
condition|(
name|meta
operator|.
name|readVInt
argument_list|()
operator|!=
name|fieldNumber
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"sorted entry for field: "
operator|+
name|fieldNumber
operator|+
literal|" is corrupt"
argument_list|)
throw|;
block|}
name|ords
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldNumber
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readNumericEntry
specifier|static
name|NumericEntry
name|readNumericEntry
parameter_list|(
name|IndexInput
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericEntry
name|entry
init|=
operator|new
name|NumericEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|minValue
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|header
operator|=
name|PackedInts
operator|.
name|readHeader
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
DECL|method|readBinaryEntry
specifier|static
name|BinaryEntry
name|readBinaryEntry
parameter_list|(
name|IndexInput
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryEntry
name|entry
init|=
operator|new
name|BinaryEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|minLength
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|maxLength
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|count
operator|=
name|meta
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericEntry
name|entry
init|=
name|numerics
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
return|return
name|getNumeric
argument_list|(
name|field
argument_list|,
name|entry
argument_list|)
return|;
block|}
DECL|method|getNumeric
specifier|private
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|NumericEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|data
init|=
name|this
operator|.
name|data
operator|.
name|clone
argument_list|()
decl_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
argument_list|)
expr_stmt|;
specifier|final
name|PackedInts
operator|.
name|Reader
name|reader
init|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|data
argument_list|,
name|entry
operator|.
name|header
argument_list|)
decl_stmt|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|entry
operator|.
name|minValue
operator|+
name|reader
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryEntry
name|bytes
init|=
name|binaries
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
name|bytes
operator|.
name|minLength
operator|==
name|bytes
operator|.
name|maxLength
condition|)
block|{
return|return
name|getFixedBinary
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getVariableBinary
argument_list|(
name|field
argument_list|,
name|bytes
argument_list|)
return|;
block|}
block|}
DECL|method|getFixedBinary
specifier|private
name|BinaryDocValues
name|getFixedBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|BinaryEntry
name|bytes
parameter_list|)
block|{
specifier|final
name|IndexInput
name|data
init|=
name|this
operator|.
name|data
operator|.
name|clone
argument_list|()
decl_stmt|;
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|long
name|address
init|=
name|bytes
operator|.
name|offset
operator|+
name|docID
operator|*
operator|(
name|long
operator|)
name|bytes
operator|.
name|maxLength
decl_stmt|;
try|try
block|{
name|data
operator|.
name|seek
argument_list|(
name|address
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|length
operator|<
name|bytes
operator|.
name|maxLength
condition|)
block|{
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|bytes
operator|.
name|maxLength
index|]
expr_stmt|;
block|}
name|data
operator|.
name|readBytes
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
name|result
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|maxLength
argument_list|)
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|bytes
operator|.
name|maxLength
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
DECL|method|getVariableBinary
specifier|private
name|BinaryDocValues
name|getVariableBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
specifier|final
name|BinaryEntry
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndexInput
name|data
init|=
name|this
operator|.
name|data
operator|.
name|clone
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValues
name|addresses
init|=
name|getNumeric
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|long
name|startAddress
init|=
name|docID
operator|==
literal|0
condition|?
name|bytes
operator|.
name|offset
else|:
name|bytes
operator|.
name|offset
operator|+
name|addresses
operator|.
name|get
argument_list|(
name|docID
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|endAddress
init|=
name|bytes
operator|.
name|offset
operator|+
name|addresses
operator|.
name|get
argument_list|(
name|docID
argument_list|)
decl_stmt|;
name|int
name|length
init|=
call|(
name|int
call|)
argument_list|(
name|endAddress
operator|-
name|startAddress
argument_list|)
decl_stmt|;
try|try
block|{
name|data
operator|.
name|seek
argument_list|(
name|startAddress
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|length
operator|<
name|length
condition|)
block|{
name|result
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|result
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
block|}
name|data
operator|.
name|readBytes
argument_list|(
name|result
operator|.
name|bytes
argument_list|,
name|result
operator|.
name|offset
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
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nocommit: ugly hack to nuke size()
specifier|final
name|BinaryEntry
name|binaryEntry
init|=
name|binaries
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
specifier|final
name|BinaryDocValues
name|binary
init|=
name|getBinary
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|ordinals
init|=
name|getNumeric
argument_list|(
name|field
argument_list|,
name|ords
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|ordinals
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|binary
operator|.
name|get
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|binaryEntry
operator|.
name|count
return|;
block|}
block|}
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
name|data
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|NumericEntry
specifier|static
class|class
name|NumericEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|minValue
name|long
name|minValue
decl_stmt|;
DECL|field|header
name|PackedInts
operator|.
name|Header
name|header
decl_stmt|;
block|}
DECL|class|BinaryEntry
specifier|static
class|class
name|BinaryEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|minLength
name|int
name|minLength
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
block|}
block|}
end_class

end_unit

