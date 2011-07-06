begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|values
operator|.
name|IndexDocValues
operator|.
name|Source
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
name|AttributeSource
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
name|FloatsRef
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
comment|/**  * Exposes {@link Writer} and reader ({@link Source}) for 32 bit and 64 bit  * floating point values.  *<p>  * Current implementations store either 4 byte or 8 byte floating points with  * full precision without any compression.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|Floats
specifier|public
class|class
name|Floats
block|{
comment|// TODO - add bulk copy where possible
DECL|field|CODEC_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"SimpleFloats"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|DEFAULTS
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|DEFAULTS
init|=
operator|new
name|byte
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
DECL|method|getWriter
specifier|public
specifier|static
name|Writer
name|getWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|precisionBytes
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|precisionBytes
operator|!=
literal|4
operator|&&
name|precisionBytes
operator|!=
literal|8
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionBytes must be 4 or 8; got "
operator|+
name|precisionBytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|precisionBytes
operator|==
literal|4
condition|)
block|{
return|return
operator|new
name|Float4Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Float8Writer
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|bytesUsed
argument_list|)
return|;
block|}
block|}
DECL|method|getValues
specifier|public
specifier|static
name|IndexDocValues
name|getValues
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FloatsReader
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
DECL|class|FloatsWriter
specifier|abstract
specifier|static
class|class
name|FloatsWriter
extends|extends
name|Writer
block|{
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|floatsRef
specifier|protected
name|FloatsRef
name|floatsRef
decl_stmt|;
DECL|field|lastDocId
specifier|protected
name|int
name|lastDocId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|datOut
specifier|protected
name|IndexOutput
name|datOut
decl_stmt|;
DECL|field|precision
specifier|private
specifier|final
name|byte
name|precision
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|Directory
name|dir
decl_stmt|;
DECL|method|FloatsWriter
specifier|protected
name|FloatsWriter
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|precision
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|bytesUsed
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|precision
operator|=
operator|(
name|byte
operator|)
name|precision
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|initDataOut
specifier|final
name|void
name|initDataOut
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|datOut
operator|==
literal|null
assert|;
name|datOut
operator|=
name|dir
operator|.
name|createOutput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
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
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|datOut
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
assert|assert
name|datOut
operator|.
name|getFilePointer
argument_list|()
operator|==
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
assert|;
name|datOut
operator|.
name|writeByte
argument_list|(
name|this
operator|.
name|precision
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
name|closeSafely
argument_list|(
literal|true
argument_list|,
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|floatsRef
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|PerDocFieldValues
name|docValues
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
name|docID
argument_list|,
name|docValues
operator|.
name|getFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextEnum
specifier|protected
name|void
name|setNextEnum
parameter_list|(
name|ValuesEnum
name|valuesEnum
parameter_list|)
block|{
name|floatsRef
operator|=
name|valuesEnum
operator|.
name|getFloat
argument_list|()
expr_stmt|;
block|}
DECL|method|fillDefault
specifier|protected
specifier|final
name|int
name|fillDefault
parameter_list|(
name|int
name|numValues
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numValues
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeBytes
argument_list|(
name|DEFAULTS
argument_list|,
name|precision
argument_list|)
expr_stmt|;
block|}
return|return
name|numValues
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|protected
name|void
name|merge
parameter_list|(
name|MergeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
name|initDataOut
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|liveDocs
operator|==
literal|null
operator|&&
name|state
operator|.
name|reader
operator|instanceof
name|FloatsReader
condition|)
block|{
comment|// no deletes - bulk copy
specifier|final
name|FloatsReader
name|reader
init|=
operator|(
name|FloatsReader
operator|)
name|state
operator|.
name|reader
decl_stmt|;
assert|assert
name|reader
operator|.
name|precisionBytes
operator|==
operator|(
name|int
operator|)
name|precision
assert|;
if|if
condition|(
name|reader
operator|.
name|maxDoc
operator|==
literal|0
condition|)
return|return;
specifier|final
name|int
name|docBase
init|=
name|state
operator|.
name|docBase
decl_stmt|;
if|if
condition|(
name|docBase
operator|-
name|lastDocId
operator|>
literal|1
condition|)
block|{
comment|// fill with default values
name|lastDocId
operator|+=
name|fillDefault
argument_list|(
name|docBase
operator|-
name|lastDocId
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|lastDocId
operator|+=
name|reader
operator|.
name|transferTo
argument_list|(
name|datOut
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|merge
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|files
operator|.
name|add
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Writes 4 bytes (float) per value
DECL|class|Float4Writer
specifier|static
specifier|final
class|class
name|Float4Writer
extends|extends
name|FloatsWriter
block|{
DECL|field|values
specifier|private
name|int
index|[]
name|values
decl_stmt|;
DECL|method|Float4Writer
specifier|protected
name|Float4Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
literal|4
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
literal|1
index|]
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|int
name|docID
parameter_list|,
specifier|final
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
if|if
condition|(
name|docID
operator|>=
name|values
operator|.
name|length
condition|)
block|{
specifier|final
name|long
name|len
init|=
name|values
operator|.
name|length
decl_stmt|;
name|values
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|values
argument_list|,
literal|1
operator|+
name|docID
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
operator|(
name|values
operator|.
name|length
operator|)
operator|-
name|len
operator|)
argument_list|)
expr_stmt|;
block|}
name|values
index|[
name|docID
index|]
operator|=
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
operator|(
name|float
operator|)
name|v
argument_list|)
expr_stmt|;
name|lastDocId
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|datOut
operator|!=
literal|null
assert|;
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
if|if
condition|(
name|docID
operator|-
name|lastDocId
operator|>
literal|1
condition|)
block|{
comment|// fill with default values
name|fillDefault
argument_list|(
name|docID
operator|-
name|lastDocId
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
assert|assert
name|datOut
operator|!=
literal|null
assert|;
name|datOut
operator|.
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
operator|(
name|float
operator|)
name|floatsRef
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lastDocId
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|int
name|numDefaultsToAppend
init|=
name|docCount
operator|-
operator|(
name|lastDocId
operator|+
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
name|initDataOut
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|lastDocId
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeInt
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|fillDefault
argument_list|(
name|numDefaultsToAppend
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|*
operator|(
operator|(
name|values
operator|.
name|length
operator|)
operator|)
operator|)
argument_list|)
expr_stmt|;
name|values
operator|=
literal|null
expr_stmt|;
name|IOUtils
operator|.
name|closeSafely
argument_list|(
operator|!
name|success
argument_list|,
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Writes 8 bytes (double) per value
DECL|class|Float8Writer
specifier|static
specifier|final
class|class
name|Float8Writer
extends|extends
name|FloatsWriter
block|{
DECL|field|values
specifier|private
name|long
index|[]
name|values
decl_stmt|;
DECL|method|Float8Writer
specifier|protected
name|Float8Writer
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|AtomicLong
name|bytesUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
literal|8
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[
literal|1
index|]
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|docID
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
if|if
condition|(
name|docID
operator|>=
name|values
operator|.
name|length
condition|)
block|{
specifier|final
name|long
name|len
init|=
name|values
operator|.
name|length
decl_stmt|;
name|values
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|values
argument_list|,
literal|1
operator|+
name|docID
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
operator|*
operator|(
operator|(
name|values
operator|.
name|length
operator|)
operator|-
name|len
operator|)
argument_list|)
expr_stmt|;
block|}
name|values
index|[
name|docID
index|]
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|lastDocId
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mergeDoc
specifier|protected
name|void
name|mergeDoc
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|>
name|lastDocId
operator|:
literal|"docID: "
operator|+
name|docID
operator|+
literal|" must be greater than the last added doc id: "
operator|+
name|lastDocId
assert|;
if|if
condition|(
name|docID
operator|-
name|lastDocId
operator|>
literal|1
condition|)
block|{
comment|// fill with default values
name|lastDocId
operator|+=
name|fillDefault
argument_list|(
name|docID
operator|-
name|lastDocId
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
assert|assert
name|datOut
operator|!=
literal|null
assert|;
name|datOut
operator|.
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
operator|(
name|float
operator|)
name|floatsRef
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lastDocId
operator|=
name|docID
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|docCount
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|int
name|numDefaultsToAppend
init|=
name|docCount
operator|-
operator|(
name|lastDocId
operator|+
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|datOut
operator|==
literal|null
condition|)
block|{
name|initDataOut
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|lastDocId
condition|;
name|i
operator|++
control|)
block|{
name|datOut
operator|.
name|writeLong
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|fillDefault
argument_list|(
name|numDefaultsToAppend
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_LONG
operator|*
operator|(
operator|(
name|values
operator|.
name|length
operator|)
operator|)
operator|)
argument_list|)
expr_stmt|;
name|values
operator|=
literal|null
expr_stmt|;
name|IOUtils
operator|.
name|closeSafely
argument_list|(
operator|!
name|success
argument_list|,
name|datOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Opens all necessary files, but does not read any data in until you call    * {@link #load}.    */
DECL|class|FloatsReader
specifier|static
class|class
name|FloatsReader
extends|extends
name|IndexDocValues
block|{
DECL|field|datIn
specifier|private
specifier|final
name|IndexInput
name|datIn
decl_stmt|;
DECL|field|precisionBytes
specifier|private
specifier|final
name|int
name|precisionBytes
decl_stmt|;
comment|// TODO(simonw) is ByteBuffer the way to go here?
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|FloatsReader
specifier|protected
name|FloatsReader
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|datIn
operator|=
name|dir
operator|.
name|openInput
argument_list|(
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|id
argument_list|,
literal|""
argument_list|,
name|Writer
operator|.
name|DATA_EXTENSION
argument_list|)
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
name|precisionBytes
operator|=
name|datIn
operator|.
name|readByte
argument_list|()
expr_stmt|;
assert|assert
name|precisionBytes
operator|==
literal|4
operator|||
name|precisionBytes
operator|==
literal|8
assert|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
DECL|method|transferTo
name|int
name|transferTo
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|indexInput
init|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
decl_stmt|;
try|try
block|{
name|indexInput
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
comment|// skip precision:
name|indexInput
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|out
operator|.
name|copyBytes
argument_list|(
name|indexInput
argument_list|,
name|precisionBytes
operator|*
name|maxDoc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|maxDoc
return|;
block|}
comment|/**      * Loads the actual values. You may call this more than once, eg if you      * already previously loaded but then discarded the Source.      */
annotation|@
name|Override
DECL|method|load
specifier|public
name|Source
name|load
parameter_list|()
throws|throws
name|IOException
block|{
comment|/* we always read BIG_ENDIAN here since the writer uses        * DataOutput#writeInt() / writeLong() we can simply read the ints / longs        * back in using readInt / readLong */
specifier|final
name|IndexInput
name|indexInput
init|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|indexInput
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
comment|// skip precision:
name|indexInput
operator|.
name|readByte
argument_list|()
expr_stmt|;
if|if
condition|(
name|precisionBytes
operator|==
literal|4
condition|)
block|{
specifier|final
name|float
index|[]
name|values
init|=
operator|new
name|float
index|[
operator|(
literal|4
operator|*
name|maxDoc
operator|)
operator|>>
literal|2
index|]
decl_stmt|;
assert|assert
name|values
operator|.
name|length
operator|==
name|maxDoc
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
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
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|indexInput
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Source4
argument_list|(
name|values
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|double
index|[]
name|values
init|=
operator|new
name|double
index|[
operator|(
literal|8
operator|*
name|maxDoc
operator|)
operator|>>
literal|3
index|]
decl_stmt|;
assert|assert
name|values
operator|.
name|length
operator|==
name|maxDoc
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
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
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|indexInput
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Source8
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
DECL|class|Source4
specifier|private
specifier|final
class|class
name|Source4
extends|extends
name|Source
block|{
DECL|field|values
specifier|private
specifier|final
name|float
index|[]
name|values
decl_stmt|;
DECL|method|Source4
name|Source4
parameter_list|(
specifier|final
name|float
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SourceEnum
argument_list|(
name|attrSource
argument_list|,
name|ValueType
operator|.
name|FLOAT_32
argument_list|,
name|this
argument_list|,
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|numDocs
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
name|floatsRef
operator|.
name|floats
index|[
name|floatsRef
operator|.
name|offset
index|]
operator|=
name|source
operator|.
name|getFloat
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|this
operator|.
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|FLOAT_32
return|;
block|}
block|}
DECL|class|Source8
specifier|private
specifier|final
class|class
name|Source8
extends|extends
name|Source
block|{
DECL|field|values
specifier|private
specifier|final
name|double
index|[]
name|values
decl_stmt|;
DECL|method|Source8
name|Source8
parameter_list|(
specifier|final
name|double
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFloat
specifier|public
name|double
name|getFloat
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
index|[
name|docID
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|attrSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SourceEnum
argument_list|(
name|attrSource
argument_list|,
name|type
argument_list|()
argument_list|,
name|this
argument_list|,
name|maxDoc
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|numDocs
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
name|floatsRef
operator|.
name|floats
index|[
name|floatsRef
operator|.
name|offset
index|]
operator|=
name|source
operator|.
name|getFloat
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|ValueType
operator|.
name|FLOAT_64
return|;
block|}
annotation|@
name|Override
DECL|method|getArray
specifier|public
name|Object
name|getArray
parameter_list|()
block|{
return|return
name|this
operator|.
name|values
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
literal|true
return|;
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
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|datIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEnum
specifier|public
name|ValuesEnum
name|getEnum
parameter_list|(
name|AttributeSource
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexInput
name|indexInput
init|=
operator|(
name|IndexInput
operator|)
name|datIn
operator|.
name|clone
argument_list|()
decl_stmt|;
name|indexInput
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
comment|// skip precision:
name|indexInput
operator|.
name|readByte
argument_list|()
expr_stmt|;
return|return
name|precisionBytes
operator|==
literal|4
condition|?
operator|new
name|Floats4Enum
argument_list|(
name|source
argument_list|,
name|indexInput
argument_list|,
name|maxDoc
argument_list|)
else|:
operator|new
name|Floats8EnumImpl
argument_list|(
name|source
argument_list|,
name|indexInput
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|ValueType
name|type
parameter_list|()
block|{
return|return
name|precisionBytes
operator|==
literal|4
condition|?
name|ValueType
operator|.
name|FLOAT_32
else|:
name|ValueType
operator|.
name|FLOAT_64
return|;
block|}
block|}
DECL|class|Floats4Enum
specifier|static
specifier|final
class|class
name|Floats4Enum
extends|extends
name|FloatsEnumImpl
block|{
DECL|method|Floats4Enum
name|Floats4Enum
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|dataIn
argument_list|,
literal|4
argument_list|,
name|maxDoc
argument_list|,
name|ValueType
operator|.
name|FLOAT_32
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
name|dataIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
operator|(
name|target
operator|*
name|precision
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|intBits
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|floatsRef
operator|.
name|floats
index|[
literal|0
index|]
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|intBits
argument_list|)
expr_stmt|;
name|floatsRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
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
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
DECL|class|Floats8EnumImpl
specifier|private
specifier|static
specifier|final
class|class
name|Floats8EnumImpl
extends|extends
name|FloatsEnumImpl
block|{
DECL|method|Floats8EnumImpl
name|Floats8EnumImpl
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|dataIn
argument_list|,
literal|8
argument_list|,
name|maxDoc
argument_list|,
name|ValueType
operator|.
name|FLOAT_64
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
name|dataIn
operator|.
name|seek
argument_list|(
name|fp
operator|+
operator|(
name|target
operator|*
name|precision
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|long
name|value
init|=
name|dataIn
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|floatsRef
operator|.
name|floats
index|[
name|floatsRef
operator|.
name|offset
index|]
operator|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|pos
operator|=
name|target
return|;
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
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|maxDoc
condition|)
block|{
return|return
name|pos
operator|=
name|NO_MORE_DOCS
return|;
block|}
return|return
name|advance
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
DECL|class|FloatsEnumImpl
specifier|static
specifier|abstract
class|class
name|FloatsEnumImpl
extends|extends
name|ValuesEnum
block|{
DECL|field|dataIn
specifier|protected
specifier|final
name|IndexInput
name|dataIn
decl_stmt|;
DECL|field|pos
specifier|protected
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|precision
specifier|protected
specifier|final
name|int
name|precision
decl_stmt|;
DECL|field|maxDoc
specifier|protected
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|field|fp
specifier|protected
specifier|final
name|long
name|fp
decl_stmt|;
DECL|method|FloatsEnumImpl
name|FloatsEnumImpl
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|IndexInput
name|dataIn
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|maxDoc
parameter_list|,
name|ValueType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|source
argument_list|,
name|precision
operator|==
literal|4
condition|?
name|ValueType
operator|.
name|FLOAT_32
else|:
name|ValueType
operator|.
name|FLOAT_64
argument_list|)
expr_stmt|;
name|this
operator|.
name|dataIn
operator|=
name|dataIn
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|fp
operator|=
name|dataIn
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|floatsRef
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
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
name|dataIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

