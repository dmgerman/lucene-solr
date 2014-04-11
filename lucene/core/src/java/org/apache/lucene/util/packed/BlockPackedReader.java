begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitUtil
operator|.
name|zigZagDecode
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
name|util
operator|.
name|packed
operator|.
name|AbstractBlockPackedWriter
operator|.
name|BPV_SHIFT
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
name|util
operator|.
name|packed
operator|.
name|AbstractBlockPackedWriter
operator|.
name|MAX_BLOCK_SIZE
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
name|util
operator|.
name|packed
operator|.
name|AbstractBlockPackedWriter
operator|.
name|MIN_BLOCK_SIZE
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
name|util
operator|.
name|packed
operator|.
name|AbstractBlockPackedWriter
operator|.
name|MIN_VALUE_EQUALS_0
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
name|util
operator|.
name|packed
operator|.
name|BlockPackedReaderIterator
operator|.
name|readVLong
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
name|util
operator|.
name|packed
operator|.
name|PackedInts
operator|.
name|checkBlockSize
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
name|util
operator|.
name|packed
operator|.
name|PackedInts
operator|.
name|numBlocks
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
name|LongValues
import|;
end_import

begin_comment
comment|/**  * Provides random access to a stream written with {@link BlockPackedWriter}.  * @lucene.internal  */
end_comment

begin_class
DECL|class|BlockPackedReader
specifier|public
specifier|final
class|class
name|BlockPackedReader
extends|extends
name|LongValues
block|{
DECL|field|blockShift
DECL|field|blockMask
specifier|private
specifier|final
name|int
name|blockShift
decl_stmt|,
name|blockMask
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|long
name|valueCount
decl_stmt|;
DECL|field|minValues
specifier|private
specifier|final
name|long
index|[]
name|minValues
decl_stmt|;
DECL|field|subReaders
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
index|[]
name|subReaders
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|BlockPackedReader
specifier|public
name|BlockPackedReader
parameter_list|(
name|IndexInput
name|in
parameter_list|,
name|int
name|packedIntsVersion
parameter_list|,
name|int
name|blockSize
parameter_list|,
name|long
name|valueCount
parameter_list|,
name|boolean
name|direct
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|valueCount
operator|=
name|valueCount
expr_stmt|;
name|blockShift
operator|=
name|checkBlockSize
argument_list|(
name|blockSize
argument_list|,
name|MIN_BLOCK_SIZE
argument_list|,
name|MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|blockMask
operator|=
name|blockSize
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|numBlocks
init|=
name|numBlocks
argument_list|(
name|valueCount
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|long
index|[]
name|minValues
init|=
literal|null
decl_stmt|;
name|subReaders
operator|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
name|numBlocks
index|]
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
name|numBlocks
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|token
init|=
name|in
operator|.
name|readByte
argument_list|()
operator|&
literal|0xFF
decl_stmt|;
specifier|final
name|int
name|bitsPerValue
init|=
name|token
operator|>>>
name|BPV_SHIFT
decl_stmt|;
if|if
condition|(
name|bitsPerValue
operator|>
literal|64
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupted"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|token
operator|&
name|MIN_VALUE_EQUALS_0
operator|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|minValues
operator|==
literal|null
condition|)
block|{
name|minValues
operator|=
operator|new
name|long
index|[
name|numBlocks
index|]
expr_stmt|;
block|}
name|minValues
index|[
name|i
index|]
operator|=
name|zigZagDecode
argument_list|(
literal|1L
operator|+
name|readVLong
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bitsPerValue
operator|==
literal|0
condition|)
block|{
name|subReaders
index|[
name|i
index|]
operator|=
operator|new
name|PackedInts
operator|.
name|NullReader
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|size
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|blockSize
argument_list|,
name|valueCount
operator|-
operator|(
name|long
operator|)
name|i
operator|*
name|blockSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|direct
condition|)
block|{
specifier|final
name|long
name|pointer
init|=
name|in
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|subReaders
index|[
name|i
index|]
operator|=
name|PackedInts
operator|.
name|getDirectReaderNoHeader
argument_list|(
name|in
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|size
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|pointer
operator|+
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|size
argument_list|,
name|bitsPerValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|subReaders
index|[
name|i
index|]
operator|=
name|PackedInts
operator|.
name|getReaderNoHeader
argument_list|(
name|in
argument_list|,
name|PackedInts
operator|.
name|Format
operator|.
name|PACKED
argument_list|,
name|packedIntsVersion
argument_list|,
name|size
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|minValues
operator|=
name|minValues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|long
name|index
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
specifier|final
name|int
name|block
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|>>>
name|blockShift
argument_list|)
decl_stmt|;
specifier|final
name|int
name|idx
init|=
call|(
name|int
call|)
argument_list|(
name|index
operator|&
name|blockMask
argument_list|)
decl_stmt|;
return|return
operator|(
name|minValues
operator|==
literal|null
condition|?
literal|0
else|:
name|minValues
index|[
name|block
index|]
operator|)
operator|+
name|subReaders
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|idx
argument_list|)
return|;
block|}
comment|/** Returns approximate RAM bytes used */
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Reader
name|reader
range|:
name|subReaders
control|)
block|{
name|size
operator|+=
name|reader
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
block|}
end_class

end_unit

