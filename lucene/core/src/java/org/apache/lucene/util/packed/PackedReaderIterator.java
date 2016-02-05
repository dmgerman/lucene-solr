begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|DataInput
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
name|LongsRef
import|;
end_import

begin_class
DECL|class|PackedReaderIterator
specifier|final
class|class
name|PackedReaderIterator
extends|extends
name|PackedInts
operator|.
name|ReaderIteratorImpl
block|{
DECL|field|packedIntsVersion
specifier|final
name|int
name|packedIntsVersion
decl_stmt|;
DECL|field|format
specifier|final
name|PackedInts
operator|.
name|Format
name|format
decl_stmt|;
DECL|field|bulkOperation
specifier|final
name|BulkOperation
name|bulkOperation
decl_stmt|;
DECL|field|nextBlocks
specifier|final
name|byte
index|[]
name|nextBlocks
decl_stmt|;
DECL|field|nextValues
specifier|final
name|LongsRef
name|nextValues
decl_stmt|;
DECL|field|iterations
specifier|final
name|int
name|iterations
decl_stmt|;
DECL|field|position
name|int
name|position
decl_stmt|;
DECL|method|PackedReaderIterator
name|PackedReaderIterator
parameter_list|(
name|PackedInts
operator|.
name|Format
name|format
parameter_list|,
name|int
name|packedIntsVersion
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|int
name|mem
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
name|this
operator|.
name|packedIntsVersion
operator|=
name|packedIntsVersion
expr_stmt|;
name|bulkOperation
operator|=
name|BulkOperation
operator|.
name|of
argument_list|(
name|format
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
name|iterations
operator|=
name|bulkOperation
operator|.
name|computeIterations
argument_list|(
name|valueCount
argument_list|,
name|mem
argument_list|)
expr_stmt|;
assert|assert
name|valueCount
operator|==
literal|0
operator|||
name|iterations
operator|>
literal|0
assert|;
name|nextBlocks
operator|=
operator|new
name|byte
index|[
name|iterations
operator|*
name|bulkOperation
operator|.
name|byteBlockCount
argument_list|()
index|]
expr_stmt|;
name|nextValues
operator|=
operator|new
name|LongsRef
argument_list|(
operator|new
name|long
index|[
name|iterations
operator|*
name|bulkOperation
operator|.
name|byteValueCount
argument_list|()
index|]
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|nextValues
operator|.
name|offset
operator|=
name|nextValues
operator|.
name|longs
operator|.
name|length
expr_stmt|;
name|position
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|LongsRef
name|next
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|nextValues
operator|.
name|length
operator|>=
literal|0
assert|;
assert|assert
name|count
operator|>
literal|0
assert|;
assert|assert
name|nextValues
operator|.
name|offset
operator|+
name|nextValues
operator|.
name|length
operator|<=
name|nextValues
operator|.
name|longs
operator|.
name|length
assert|;
name|nextValues
operator|.
name|offset
operator|+=
name|nextValues
operator|.
name|length
expr_stmt|;
specifier|final
name|int
name|remaining
init|=
name|valueCount
operator|-
name|position
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|remaining
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|count
operator|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextValues
operator|.
name|offset
operator|==
name|nextValues
operator|.
name|longs
operator|.
name|length
condition|)
block|{
specifier|final
name|long
name|remainingBlocks
init|=
name|format
operator|.
name|byteCount
argument_list|(
name|packedIntsVersion
argument_list|,
name|remaining
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
specifier|final
name|int
name|blocksToRead
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|remainingBlocks
argument_list|,
name|nextBlocks
operator|.
name|length
argument_list|)
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|nextBlocks
argument_list|,
literal|0
argument_list|,
name|blocksToRead
argument_list|)
expr_stmt|;
if|if
condition|(
name|blocksToRead
operator|<
name|nextBlocks
operator|.
name|length
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|nextBlocks
argument_list|,
name|blocksToRead
argument_list|,
name|nextBlocks
operator|.
name|length
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
name|bulkOperation
operator|.
name|decode
argument_list|(
name|nextBlocks
argument_list|,
literal|0
argument_list|,
name|nextValues
operator|.
name|longs
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
name|nextValues
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
block|}
name|nextValues
operator|.
name|length
operator|=
name|Math
operator|.
name|min
argument_list|(
name|nextValues
operator|.
name|longs
operator|.
name|length
operator|-
name|nextValues
operator|.
name|offset
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|position
operator|+=
name|nextValues
operator|.
name|length
expr_stmt|;
return|return
name|nextValues
return|;
block|}
annotation|@
name|Override
DECL|method|ord
specifier|public
name|int
name|ord
parameter_list|()
block|{
return|return
name|position
return|;
block|}
block|}
end_class

end_unit

