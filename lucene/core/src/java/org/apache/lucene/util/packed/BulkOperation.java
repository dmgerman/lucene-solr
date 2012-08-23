begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// This file has been automatically generated, DO NOT EDIT
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Efficient sequential read/write of packed integers.  */
end_comment

begin_class
DECL|class|BulkOperation
specifier|abstract
class|class
name|BulkOperation
implements|implements
name|PackedInts
operator|.
name|Decoder
implements|,
name|PackedInts
operator|.
name|Encoder
block|{
DECL|field|packedBulkOps
specifier|private
specifier|static
specifier|final
name|BulkOperation
index|[]
name|packedBulkOps
init|=
operator|new
name|BulkOperation
index|[]
block|{
operator|new
name|BulkOperationPacked1
argument_list|()
block|,
operator|new
name|BulkOperationPacked2
argument_list|()
block|,
operator|new
name|BulkOperationPacked3
argument_list|()
block|,
operator|new
name|BulkOperationPacked4
argument_list|()
block|,
operator|new
name|BulkOperationPacked5
argument_list|()
block|,
operator|new
name|BulkOperationPacked6
argument_list|()
block|,
operator|new
name|BulkOperationPacked7
argument_list|()
block|,
operator|new
name|BulkOperationPacked8
argument_list|()
block|,
operator|new
name|BulkOperationPacked9
argument_list|()
block|,
operator|new
name|BulkOperationPacked10
argument_list|()
block|,
operator|new
name|BulkOperationPacked11
argument_list|()
block|,
operator|new
name|BulkOperationPacked12
argument_list|()
block|,
operator|new
name|BulkOperationPacked13
argument_list|()
block|,
operator|new
name|BulkOperationPacked14
argument_list|()
block|,
operator|new
name|BulkOperationPacked15
argument_list|()
block|,
operator|new
name|BulkOperationPacked16
argument_list|()
block|,
operator|new
name|BulkOperationPacked17
argument_list|()
block|,
operator|new
name|BulkOperationPacked18
argument_list|()
block|,
operator|new
name|BulkOperationPacked19
argument_list|()
block|,
operator|new
name|BulkOperationPacked20
argument_list|()
block|,
operator|new
name|BulkOperationPacked21
argument_list|()
block|,
operator|new
name|BulkOperationPacked22
argument_list|()
block|,
operator|new
name|BulkOperationPacked23
argument_list|()
block|,
operator|new
name|BulkOperationPacked24
argument_list|()
block|,
operator|new
name|BulkOperationPacked25
argument_list|()
block|,
operator|new
name|BulkOperationPacked26
argument_list|()
block|,
operator|new
name|BulkOperationPacked27
argument_list|()
block|,
operator|new
name|BulkOperationPacked28
argument_list|()
block|,
operator|new
name|BulkOperationPacked29
argument_list|()
block|,
operator|new
name|BulkOperationPacked30
argument_list|()
block|,
operator|new
name|BulkOperationPacked31
argument_list|()
block|,
operator|new
name|BulkOperationPacked32
argument_list|()
block|,
operator|new
name|BulkOperationPacked33
argument_list|()
block|,
operator|new
name|BulkOperationPacked34
argument_list|()
block|,
operator|new
name|BulkOperationPacked35
argument_list|()
block|,
operator|new
name|BulkOperationPacked36
argument_list|()
block|,
operator|new
name|BulkOperationPacked37
argument_list|()
block|,
operator|new
name|BulkOperationPacked38
argument_list|()
block|,
operator|new
name|BulkOperationPacked39
argument_list|()
block|,
operator|new
name|BulkOperationPacked40
argument_list|()
block|,
operator|new
name|BulkOperationPacked41
argument_list|()
block|,
operator|new
name|BulkOperationPacked42
argument_list|()
block|,
operator|new
name|BulkOperationPacked43
argument_list|()
block|,
operator|new
name|BulkOperationPacked44
argument_list|()
block|,
operator|new
name|BulkOperationPacked45
argument_list|()
block|,
operator|new
name|BulkOperationPacked46
argument_list|()
block|,
operator|new
name|BulkOperationPacked47
argument_list|()
block|,
operator|new
name|BulkOperationPacked48
argument_list|()
block|,
operator|new
name|BulkOperationPacked49
argument_list|()
block|,
operator|new
name|BulkOperationPacked50
argument_list|()
block|,
operator|new
name|BulkOperationPacked51
argument_list|()
block|,
operator|new
name|BulkOperationPacked52
argument_list|()
block|,
operator|new
name|BulkOperationPacked53
argument_list|()
block|,
operator|new
name|BulkOperationPacked54
argument_list|()
block|,
operator|new
name|BulkOperationPacked55
argument_list|()
block|,
operator|new
name|BulkOperationPacked56
argument_list|()
block|,
operator|new
name|BulkOperationPacked57
argument_list|()
block|,
operator|new
name|BulkOperationPacked58
argument_list|()
block|,
operator|new
name|BulkOperationPacked59
argument_list|()
block|,
operator|new
name|BulkOperationPacked60
argument_list|()
block|,
operator|new
name|BulkOperationPacked61
argument_list|()
block|,
operator|new
name|BulkOperationPacked62
argument_list|()
block|,
operator|new
name|BulkOperationPacked63
argument_list|()
block|,
operator|new
name|BulkOperationPacked64
argument_list|()
block|,   }
decl_stmt|;
comment|// NOTE: this is sparse (some entries are null):
DECL|field|packedSingleBlockBulkOps
specifier|private
specifier|static
specifier|final
name|BulkOperation
index|[]
name|packedSingleBlockBulkOps
init|=
operator|new
name|BulkOperation
index|[]
block|{
operator|new
name|BulkOperationPackedSingleBlock1
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock2
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock3
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock4
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock5
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock6
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock7
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock8
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock9
argument_list|()
block|,
operator|new
name|BulkOperationPackedSingleBlock10
argument_list|()
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock12
argument_list|()
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock16
argument_list|()
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock21
argument_list|()
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
operator|new
name|BulkOperationPackedSingleBlock32
argument_list|()
block|,   }
decl_stmt|;
DECL|method|of
specifier|public
specifier|static
name|BulkOperation
name|of
parameter_list|(
name|PackedInts
operator|.
name|Format
name|format
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
block|{
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|PACKED
case|:
assert|assert
name|packedBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
operator|!=
literal|null
assert|;
return|return
name|packedBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
return|;
case|case
name|PACKED_SINGLE_BLOCK
case|:
assert|assert
name|packedSingleBlockBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
operator|!=
literal|null
assert|;
return|return
name|packedSingleBlockBulkOps
index|[
name|bitsPerValue
operator|-
literal|1
index|]
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
DECL|method|toLongArray
specifier|private
specifier|static
name|long
index|[]
name|toLongArray
parameter_list|(
name|int
index|[]
name|ints
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|long
index|[]
name|arr
init|=
operator|new
name|long
index|[
name|length
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
name|length
condition|;
operator|++
name|i
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
name|ints
index|[
name|offset
operator|+
name|i
index|]
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|long
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
name|encode
argument_list|(
name|toLongArray
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|iterations
operator|*
name|valueCount
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|,
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|long
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|longBLocks
init|=
operator|new
name|long
index|[
name|blockCount
argument_list|()
operator|*
name|iterations
index|]
decl_stmt|;
name|encode
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|longBLocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
argument_list|)
operator|.
name|asLongBuffer
argument_list|()
operator|.
name|put
argument_list|(
name|longBLocks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|encode
specifier|public
name|void
name|encode
parameter_list|(
name|int
index|[]
name|values
parameter_list|,
name|int
name|valuesOffset
parameter_list|,
name|byte
index|[]
name|blocks
parameter_list|,
name|int
name|blocksOffset
parameter_list|,
name|int
name|iterations
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|longBLocks
init|=
operator|new
name|long
index|[
name|blockCount
argument_list|()
operator|*
name|iterations
index|]
decl_stmt|;
name|encode
argument_list|(
name|values
argument_list|,
name|valuesOffset
argument_list|,
name|longBLocks
argument_list|,
literal|0
argument_list|,
name|iterations
argument_list|)
expr_stmt|;
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|blocks
argument_list|,
name|blocksOffset
argument_list|,
literal|8
operator|*
name|iterations
operator|*
name|blockCount
argument_list|()
argument_list|)
operator|.
name|asLongBuffer
argument_list|()
operator|.
name|put
argument_list|(
name|longBLocks
argument_list|)
expr_stmt|;
block|}
comment|/**    * For every number of bits per value, there is a minimum number of    * blocks (b) / values (v) you need to write in order to reach the next block    * boundary:    *  - 16 bits per value -> b=1, v=4    *  - 24 bits per value -> b=3, v=8    *  - 50 bits per value -> b=25, v=32    *  - 63 bits per value -> b=63, v=64    *  - ...    *    * A bulk read consists in copying<code>iterations*v</code> values that are    * contained in<code>iterations*b</code> blocks into a<code>long[]</code>    * (higher values of<code>iterations</code> are likely to yield a better    * throughput) => this requires n * (b + v) longs in memory.    *    * This method computes<code>iterations</code> as    *<code>ramBudget / (8 * (b + v))</code> (since a long is 8 bytes).    */
DECL|method|computeIterations
specifier|public
specifier|final
name|int
name|computeIterations
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|ramBudget
parameter_list|)
block|{
specifier|final
name|int
name|iterations
init|=
operator|(
name|ramBudget
operator|>>>
literal|3
operator|)
operator|/
operator|(
name|blockCount
argument_list|()
operator|+
name|valueCount
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|iterations
operator|==
literal|0
condition|)
block|{
comment|// at least 1
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
operator|(
name|iterations
operator|-
literal|1
operator|)
operator|*
name|blockCount
argument_list|()
operator|>=
name|valueCount
condition|)
block|{
comment|// don't allocate for more than the size of the reader
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|valueCount
operator|/
name|valueCount
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|iterations
return|;
block|}
block|}
block|}
end_class

end_unit

