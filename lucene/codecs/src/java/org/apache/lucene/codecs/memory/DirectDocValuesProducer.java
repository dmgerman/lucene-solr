begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|memory
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|DocValuesProducer
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
name|RandomAccessOrds
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
name|index
operator|.
name|SortedSetDocValues
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
name|util
operator|.
name|Bits
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
name|FixedBitSet
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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * Reader for {@link DirectDocValuesFormat}  */
end_comment

begin_class
DECL|class|DirectDocValuesProducer
class|class
name|DirectDocValuesProducer
extends|extends
name|DocValuesProducer
block|{
comment|// metadata maps (just file pointers and minimal stuff)
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
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sorteds
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedEntry
argument_list|>
name|sorteds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sortedSets
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSetEntry
argument_list|>
name|sortedSets
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
comment|// ram instances we have already loaded
DECL|field|numericInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|NumericDocValues
argument_list|>
name|numericInstances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|binaryInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|BinaryDocValues
argument_list|>
name|binaryInstances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sortedInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedDocValues
argument_list|>
name|sortedInstances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|sortedSetInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SortedSetRawValues
argument_list|>
name|sortedSetInstances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|docsWithFieldInstances
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Bits
argument_list|>
name|docsWithFieldInstances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|ramBytesUsed
specifier|private
specifier|final
name|AtomicLong
name|ramBytesUsed
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|NUMBER
specifier|static
specifier|final
name|byte
name|NUMBER
init|=
literal|0
decl_stmt|;
DECL|field|BYTES
specifier|static
specifier|final
name|byte
name|BYTES
init|=
literal|1
decl_stmt|;
DECL|field|SORTED
specifier|static
specifier|final
name|byte
name|SORTED
init|=
literal|2
decl_stmt|;
DECL|field|SORTED_SET
specifier|static
specifier|final
name|byte
name|SORTED_SET
init|=
literal|3
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CHECKSUM
specifier|static
specifier|final
name|int
name|VERSION_CHECKSUM
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_CHECKSUM
decl_stmt|;
DECL|method|DirectDocValuesProducer
name|DirectDocValuesProducer
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
name|getDocCount
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
comment|// read in the entries from the metadata file.
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
decl_stmt|;
name|ramBytesUsed
operator|=
operator|new
name|AtomicLong
argument_list|(
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|version
operator|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|in
argument_list|,
name|metaCodec
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|>=
name|VERSION_CHECKSUM
condition|)
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CodecUtil
operator|.
name|checkEOF
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
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
specifier|final
name|int
name|version2
init|=
name|CodecUtil
operator|.
name|checkHeader
argument_list|(
name|data
argument_list|,
name|dataCodec
argument_list|,
name|VERSION_START
argument_list|,
name|VERSION_CURRENT
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
literal|"Format versions mismatch"
argument_list|)
throw|;
block|}
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
DECL|method|readNumericEntry
specifier|private
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
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|count
operator|=
name|meta
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|missingOffset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|missingOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|entry
operator|.
name|missingBytes
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|missingBytes
operator|=
literal|0
expr_stmt|;
block|}
name|entry
operator|.
name|byteWidth
operator|=
name|meta
operator|.
name|readByte
argument_list|()
expr_stmt|;
return|return
name|entry
return|;
block|}
DECL|method|readBinaryEntry
specifier|private
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
name|offset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|entry
operator|.
name|numBytes
operator|=
name|meta
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|count
operator|=
name|meta
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|entry
operator|.
name|missingOffset
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|missingOffset
operator|!=
operator|-
literal|1
condition|)
block|{
name|entry
operator|.
name|missingBytes
operator|=
name|meta
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|missingBytes
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|entry
return|;
block|}
DECL|method|readSortedEntry
specifier|private
name|SortedEntry
name|readSortedEntry
parameter_list|(
name|IndexInput
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedEntry
name|entry
init|=
operator|new
name|SortedEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|docToOrd
operator|=
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|entry
operator|.
name|values
operator|=
name|readBinaryEntry
argument_list|(
name|meta
argument_list|)
expr_stmt|;
return|return
name|entry
return|;
block|}
DECL|method|readSortedSetEntry
specifier|private
name|SortedSetEntry
name|readSortedSetEntry
parameter_list|(
name|IndexInput
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetEntry
name|entry
init|=
operator|new
name|SortedSetEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|docToOrdAddress
operator|=
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|entry
operator|.
name|ords
operator|=
name|readNumericEntry
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|entry
operator|.
name|values
operator|=
name|readBinaryEntry
argument_list|(
name|meta
argument_list|)
expr_stmt|;
return|return
name|entry
return|;
block|}
DECL|method|readFields
specifier|private
name|void
name|readFields
parameter_list|(
name|IndexInput
name|meta
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
name|int
name|fieldType
init|=
name|meta
operator|.
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldType
operator|==
name|NUMBER
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
name|fieldType
operator|==
name|BYTES
condition|)
block|{
name|binaries
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readBinaryEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldType
operator|==
name|SORTED
condition|)
block|{
name|sorteds
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readSortedEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldType
operator|==
name|SORTED_SET
condition|)
block|{
name|sortedSets
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|readSortedSetEntry
argument_list|(
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid entry type: "
operator|+
name|fieldType
operator|+
literal|", input="
operator|+
name|meta
argument_list|)
throw|;
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
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|ramBytesUsed
operator|.
name|get
argument_list|()
return|;
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
if|if
condition|(
name|version
operator|>=
name|VERSION_CHECKSUM
condition|)
block|{
name|CodecUtil
operator|.
name|checksumEntireFile
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
specifier|synchronized
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|instance
init|=
name|numericInstances
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
name|instance
operator|==
literal|null
condition|)
block|{
comment|// Lazy load
name|instance
operator|=
name|loadNumeric
argument_list|(
name|numerics
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|)
expr_stmt|;
name|numericInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|loadNumeric
specifier|private
name|NumericDocValues
name|loadNumeric
parameter_list|(
name|NumericEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
operator|+
name|entry
operator|.
name|missingBytes
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|entry
operator|.
name|byteWidth
condition|)
block|{
case|case
literal|1
case|:
block|{
specifier|final
name|byte
index|[]
name|values
init|=
operator|new
name|byte
index|[
name|entry
operator|.
name|count
index|]
decl_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|entry
operator|.
name|count
argument_list|)
expr_stmt|;
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
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
name|idx
parameter_list|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|;
block|}
case|case
literal|2
case|:
block|{
specifier|final
name|short
index|[]
name|values
init|=
operator|new
name|short
index|[
name|entry
operator|.
name|count
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
name|entry
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
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
name|idx
parameter_list|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|;
block|}
case|case
literal|4
case|:
block|{
specifier|final
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|entry
operator|.
name|count
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
name|entry
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
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
name|idx
parameter_list|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|;
block|}
case|case
literal|8
case|:
block|{
specifier|final
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[
name|entry
operator|.
name|count
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
name|entry
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
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
name|idx
parameter_list|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|;
block|}
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
specifier|synchronized
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|instance
init|=
name|binaryInstances
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
name|instance
operator|==
literal|null
condition|)
block|{
comment|// Lazy load
name|instance
operator|=
name|loadBinary
argument_list|(
name|binaries
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
argument_list|)
expr_stmt|;
name|binaryInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|loadBinary
specifier|private
name|BinaryDocValues
name|loadBinary
parameter_list|(
name|BinaryEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
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
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|entry
operator|.
name|numBytes
index|]
decl_stmt|;
name|data
operator|.
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|entry
operator|.
name|numBytes
argument_list|)
expr_stmt|;
name|data
operator|.
name|seek
argument_list|(
name|entry
operator|.
name|offset
operator|+
name|entry
operator|.
name|numBytes
operator|+
name|entry
operator|.
name|missingBytes
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|address
init|=
operator|new
name|int
index|[
name|entry
operator|.
name|count
operator|+
literal|1
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
name|entry
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
name|address
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
name|address
index|[
name|entry
operator|.
name|count
index|]
operator|=
name|data
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|ramBytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|bytes
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|address
argument_list|)
argument_list|)
expr_stmt|;
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
name|result
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|result
operator|.
name|offset
operator|=
name|address
index|[
name|docID
index|]
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|address
index|[
name|docID
operator|+
literal|1
index|]
operator|-
name|result
operator|.
name|offset
expr_stmt|;
block|}
empty_stmt|;
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
specifier|synchronized
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|instance
init|=
name|sortedInstances
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
name|instance
operator|==
literal|null
condition|)
block|{
comment|// Lazy load
name|instance
operator|=
name|loadSorted
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|sortedInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
return|return
name|instance
return|;
block|}
DECL|method|loadSorted
specifier|private
name|SortedDocValues
name|loadSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedEntry
name|entry
init|=
name|sorteds
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|docToOrd
init|=
name|loadNumeric
argument_list|(
name|entry
operator|.
name|docToOrd
argument_list|)
decl_stmt|;
specifier|final
name|BinaryDocValues
name|values
init|=
name|loadBinary
argument_list|(
name|entry
operator|.
name|values
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
name|docToOrd
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
name|values
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
name|entry
operator|.
name|values
operator|.
name|count
return|;
block|}
comment|// Leave lookupTerm to super's binary search
comment|// Leave termsEnum to super
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSet
specifier|public
specifier|synchronized
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetRawValues
name|instance
init|=
name|sortedSetInstances
operator|.
name|get
argument_list|(
name|field
operator|.
name|number
argument_list|)
decl_stmt|;
specifier|final
name|SortedSetEntry
name|entry
init|=
name|sortedSets
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
name|instance
operator|==
literal|null
condition|)
block|{
comment|// Lazy load
name|instance
operator|=
name|loadSortedSet
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|sortedSetInstances
operator|.
name|put
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
specifier|final
name|NumericDocValues
name|docToOrdAddress
init|=
name|instance
operator|.
name|docToOrdAddress
decl_stmt|;
specifier|final
name|NumericDocValues
name|ords
init|=
name|instance
operator|.
name|ords
decl_stmt|;
specifier|final
name|BinaryDocValues
name|values
init|=
name|instance
operator|.
name|values
decl_stmt|;
comment|// Must make a new instance since the iterator has state:
return|return
operator|new
name|RandomAccessOrds
argument_list|()
block|{
name|int
name|ordStart
decl_stmt|;
name|int
name|ordUpto
decl_stmt|;
name|int
name|ordLimit
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
if|if
condition|(
name|ordUpto
operator|==
name|ordLimit
condition|)
block|{
return|return
name|NO_MORE_ORDS
return|;
block|}
else|else
block|{
return|return
name|ords
operator|.
name|get
argument_list|(
name|ordUpto
operator|++
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|ordStart
operator|=
name|ordUpto
operator|=
operator|(
name|int
operator|)
name|docToOrdAddress
operator|.
name|get
argument_list|(
name|docID
argument_list|)
expr_stmt|;
name|ordLimit
operator|=
operator|(
name|int
operator|)
name|docToOrdAddress
operator|.
name|get
argument_list|(
name|docID
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|values
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
name|entry
operator|.
name|values
operator|.
name|count
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|ordAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ords
operator|.
name|get
argument_list|(
name|ordStart
operator|+
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
return|return
name|ordLimit
operator|-
name|ordStart
return|;
block|}
comment|// Leave lookupTerm to super's binary search
comment|// Leave termsEnum to super
block|}
return|;
block|}
DECL|method|loadSortedSet
specifier|private
name|SortedSetRawValues
name|loadSortedSet
parameter_list|(
name|SortedSetEntry
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetRawValues
name|instance
init|=
operator|new
name|SortedSetRawValues
argument_list|()
decl_stmt|;
name|instance
operator|.
name|docToOrdAddress
operator|=
name|loadNumeric
argument_list|(
name|entry
operator|.
name|docToOrdAddress
argument_list|)
expr_stmt|;
name|instance
operator|.
name|ords
operator|=
name|loadNumeric
argument_list|(
name|entry
operator|.
name|ords
argument_list|)
expr_stmt|;
name|instance
operator|.
name|values
operator|=
name|loadBinary
argument_list|(
name|entry
operator|.
name|values
argument_list|)
expr_stmt|;
return|return
name|instance
return|;
block|}
DECL|method|getMissingBits
specifier|private
name|Bits
name|getMissingBits
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
specifier|final
name|long
name|offset
parameter_list|,
specifier|final
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|new
name|Bits
operator|.
name|MatchAllBits
argument_list|(
name|maxDoc
argument_list|)
return|;
block|}
else|else
block|{
name|Bits
name|instance
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|instance
operator|=
name|docsWithFieldInstances
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
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
name|offset
argument_list|)
expr_stmt|;
assert|assert
name|length
operator|%
literal|8
operator|==
literal|0
assert|;
name|long
name|bits
index|[]
init|=
operator|new
name|long
index|[
operator|(
name|int
operator|)
name|length
operator|>>
literal|3
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
name|bits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bits
index|[
name|i
index|]
operator|=
name|data
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
name|instance
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|bits
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|docsWithFieldInstances
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|instance
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDocsWithField
specifier|public
name|Bits
name|getDocsWithField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|field
operator|.
name|getDocValuesType
argument_list|()
condition|)
block|{
case|case
name|SORTED_SET
case|:
return|return
name|DocValues
operator|.
name|docsWithValue
argument_list|(
name|getSortedSet
argument_list|(
name|field
argument_list|)
argument_list|,
name|maxDoc
argument_list|)
return|;
case|case
name|SORTED
case|:
return|return
name|DocValues
operator|.
name|docsWithValue
argument_list|(
name|getSorted
argument_list|(
name|field
argument_list|)
argument_list|,
name|maxDoc
argument_list|)
return|;
case|case
name|BINARY
case|:
name|BinaryEntry
name|be
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
return|return
name|getMissingBits
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|be
operator|.
name|missingOffset
argument_list|,
name|be
operator|.
name|missingBytes
argument_list|)
return|;
case|case
name|NUMERIC
case|:
name|NumericEntry
name|ne
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
name|getMissingBits
argument_list|(
name|field
operator|.
name|number
argument_list|,
name|ne
operator|.
name|missingOffset
argument_list|,
name|ne
operator|.
name|missingBytes
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
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
DECL|class|SortedSetRawValues
specifier|static
class|class
name|SortedSetRawValues
block|{
DECL|field|docToOrdAddress
name|NumericDocValues
name|docToOrdAddress
decl_stmt|;
DECL|field|ords
name|NumericDocValues
name|ords
decl_stmt|;
DECL|field|values
name|BinaryDocValues
name|values
decl_stmt|;
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
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|missingOffset
name|long
name|missingOffset
decl_stmt|;
DECL|field|missingBytes
name|long
name|missingBytes
decl_stmt|;
DECL|field|byteWidth
name|byte
name|byteWidth
decl_stmt|;
DECL|field|packedIntsVersion
name|int
name|packedIntsVersion
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
DECL|field|missingOffset
name|long
name|missingOffset
decl_stmt|;
DECL|field|missingBytes
name|long
name|missingBytes
decl_stmt|;
DECL|field|count
name|int
name|count
decl_stmt|;
DECL|field|numBytes
name|int
name|numBytes
decl_stmt|;
DECL|field|minLength
name|int
name|minLength
decl_stmt|;
DECL|field|maxLength
name|int
name|maxLength
decl_stmt|;
DECL|field|packedIntsVersion
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|blockSize
name|int
name|blockSize
decl_stmt|;
block|}
DECL|class|SortedEntry
specifier|static
class|class
name|SortedEntry
block|{
DECL|field|docToOrd
name|NumericEntry
name|docToOrd
decl_stmt|;
DECL|field|values
name|BinaryEntry
name|values
decl_stmt|;
block|}
DECL|class|SortedSetEntry
specifier|static
class|class
name|SortedSetEntry
block|{
DECL|field|docToOrdAddress
name|NumericEntry
name|docToOrdAddress
decl_stmt|;
DECL|field|ords
name|NumericEntry
name|ords
decl_stmt|;
DECL|field|values
name|BinaryEntry
name|values
decl_stmt|;
block|}
DECL|class|FSTEntry
specifier|static
class|class
name|FSTEntry
block|{
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|field|numOrds
name|long
name|numOrds
decl_stmt|;
block|}
block|}
end_class

end_unit

