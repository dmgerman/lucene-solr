begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|search
operator|.
name|DocIdSetIterator
import|;
end_import

begin_comment
comment|/**  * A bit set that only stores longs that have at least one bit which is set.  * The way it works is that the space of bits is divided into blocks of  * 4096 bits, which is 64 longs. Then for each block, we have:<ul>  *<li>a long[] which stores the non-zero longs for that block</li>  *<li>a long so that bit<tt>i</tt> being set means that the<code>i-th</code>  *     long of the block is non-null, and its offset in the array of longs is  *     the number of one bits on the right of the<code>i-th</code> bit.</li></ul>  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|SparseFixedBitSet
specifier|public
class|class
name|SparseFixedBitSet
extends|extends
name|BitSet
implements|implements
name|Bits
implements|,
name|Accountable
block|{
DECL|field|BASE_RAM_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|BASE_RAM_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|SparseFixedBitSet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SINGLE_ELEMENT_ARRAY_BYTES_USED
specifier|private
specifier|static
specifier|final
name|long
name|SINGLE_ELEMENT_ARRAY_BYTES_USED
init|=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
operator|new
name|long
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
DECL|field|MASK_4096
specifier|private
specifier|static
specifier|final
name|int
name|MASK_4096
init|=
operator|(
literal|1
operator|<<
literal|12
operator|)
operator|-
literal|1
decl_stmt|;
DECL|method|blockCount
specifier|private
specifier|static
name|int
name|blockCount
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|int
name|blockCount
init|=
name|length
operator|>>>
literal|12
decl_stmt|;
if|if
condition|(
operator|(
name|blockCount
operator|<<
literal|12
operator|)
operator|<
name|length
condition|)
block|{
operator|++
name|blockCount
expr_stmt|;
block|}
assert|assert
operator|(
name|blockCount
operator|<<
literal|12
operator|)
operator|>=
name|length
assert|;
return|return
name|blockCount
return|;
block|}
DECL|field|indices
specifier|final
name|long
index|[]
name|indices
decl_stmt|;
DECL|field|bits
specifier|final
name|long
index|[]
index|[]
name|bits
decl_stmt|;
DECL|field|length
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|nonZeroLongCount
name|int
name|nonZeroLongCount
decl_stmt|;
DECL|field|ramBytesUsed
name|long
name|ramBytesUsed
decl_stmt|;
comment|/** Create a {@link SparseFixedBitSet} that can contain bits between    *<code>0</code> included and<code>length</code> excluded. */
DECL|method|SparseFixedBitSet
specifier|public
name|SparseFixedBitSet
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"length needs to be>= 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
specifier|final
name|int
name|blockCount
init|=
name|blockCount
argument_list|(
name|length
argument_list|)
decl_stmt|;
name|indices
operator|=
operator|new
name|long
index|[
name|blockCount
index|]
expr_stmt|;
name|bits
operator|=
operator|new
name|long
index|[
name|blockCount
index|]
index|[]
expr_stmt|;
name|ramBytesUsed
operator|=
name|BASE_RAM_BYTES_USED
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|indices
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
DECL|method|consistent
specifier|private
name|boolean
name|consistent
parameter_list|(
name|int
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
name|length
operator|:
literal|"index="
operator|+
name|index
operator|+
literal|",length="
operator|+
name|length
assert|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|cardinality
specifier|public
name|int
name|cardinality
parameter_list|()
block|{
name|int
name|cardinality
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
index|[]
name|bitArray
range|:
name|bits
control|)
block|{
if|if
condition|(
name|bitArray
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|long
name|bits
range|:
name|bitArray
control|)
block|{
name|cardinality
operator|+=
name|Long
operator|.
name|bitCount
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|cardinality
return|;
block|}
annotation|@
name|Override
DECL|method|approximateCardinality
specifier|public
name|int
name|approximateCardinality
parameter_list|()
block|{
comment|// we are assuming that bits are uniformly set and use the linear counting
comment|// algorithm to estimate the number of bits that are set based on the number
comment|// of longs that are different from zero
specifier|final
name|int
name|totalLongs
init|=
operator|(
name|length
operator|+
literal|63
operator|)
operator|>>>
literal|6
decl_stmt|;
comment|// total number of longs in the space
assert|assert
name|totalLongs
operator|>=
name|nonZeroLongCount
assert|;
specifier|final
name|int
name|zeroLongs
init|=
name|totalLongs
operator|-
name|nonZeroLongCount
decl_stmt|;
comment|// number of longs that are zeros
comment|// No need to guard against division by zero, it will return +Infinity and things will work as expected
specifier|final
name|long
name|estimate
init|=
name|Math
operator|.
name|round
argument_list|(
name|totalLongs
operator|*
name|Math
operator|.
name|log
argument_list|(
operator|(
name|double
operator|)
name|totalLongs
operator|/
name|zeroLongs
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|estimate
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|consistent
argument_list|(
name|i
argument_list|)
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
comment|// first check the index, if the i64-th bit is not set, then i is not set
comment|// note: this relies on the fact that shifts are mod 64 in java
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// if it is set, then we count the number of bits that are set on the right
comment|// of i64, and that gives us the index of the long that stores the bits we
comment|// are interested in
specifier|final
name|long
name|bits
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
index|[
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
index|]
decl_stmt|;
return|return
operator|(
name|bits
operator|&
operator|(
literal|1L
operator|<<
name|i
operator|)
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|oversize
specifier|private
specifier|static
name|int
name|oversize
parameter_list|(
name|int
name|s
parameter_list|)
block|{
name|int
name|newSize
init|=
name|s
operator|+
operator|(
name|s
operator|>>>
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|newSize
operator|>
literal|50
condition|)
block|{
name|newSize
operator|=
literal|64
expr_stmt|;
block|}
return|return
name|newSize
return|;
block|}
comment|/**    * Set the bit at index<tt>i</tt>.    */
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|consistent
argument_list|(
name|i
argument_list|)
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// in that case the sub 64-bits block we are interested in already exists,
comment|// we just need to set a bit in an existing long: the number of ones on
comment|// the right of i64 gives us the index of the long we need to update
name|bits
index|[
name|i4096
index|]
index|[
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
index|]
operator||=
literal|1L
operator|<<
name|i
expr_stmt|;
comment|// shifts are mod 64 in java
block|}
elseif|else
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
comment|// if the index is 0, it means that we just found a block of 4096 bits
comment|// that has no bit that is set yet. So let's initialize a new block:
name|insertBlock
argument_list|(
name|i4096
argument_list|,
name|i64
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// in that case we found a block of 4096 bits that has some values, but
comment|// the sub-block of 64 bits that we are interested in has no value yet,
comment|// so we need to insert a new long
name|insertLong
argument_list|(
name|i4096
argument_list|,
name|i64
argument_list|,
name|i
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|insertBlock
specifier|private
name|void
name|insertBlock
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|i64
parameter_list|,
name|int
name|i
parameter_list|)
block|{
name|indices
index|[
name|i4096
index|]
operator|=
literal|1L
operator|<<
name|i64
expr_stmt|;
comment|// shifts are mod 64 in java
assert|assert
name|bits
index|[
name|i4096
index|]
operator|==
literal|null
assert|;
name|bits
index|[
name|i4096
index|]
operator|=
operator|new
name|long
index|[]
block|{
literal|1L
operator|<<
name|i
block|}
expr_stmt|;
comment|// shifts are mod 64 in java
operator|++
name|nonZeroLongCount
expr_stmt|;
name|ramBytesUsed
operator|+=
name|SINGLE_ELEMENT_ARRAY_BYTES_USED
expr_stmt|;
block|}
DECL|method|insertLong
specifier|private
name|void
name|insertLong
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|i64
parameter_list|,
name|int
name|i
parameter_list|,
name|long
name|index
parameter_list|)
block|{
name|indices
index|[
name|i4096
index|]
operator||=
literal|1L
operator|<<
name|i64
expr_stmt|;
comment|// shifts are mod 64 in java
comment|// we count the number of bits that are set on the right of i64
comment|// this gives us the index at which to perform the insertion
specifier|final
name|int
name|o
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|bitArray
init|=
name|bits
index|[
name|i4096
index|]
decl_stmt|;
if|if
condition|(
name|bitArray
index|[
name|bitArray
operator|.
name|length
operator|-
literal|1
index|]
operator|==
literal|0
condition|)
block|{
comment|// since we only store non-zero longs, if the last value is 0, it means
comment|// that we alreay have extra space, make use of it
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
name|o
argument_list|,
name|bitArray
argument_list|,
name|o
operator|+
literal|1
argument_list|,
name|bitArray
operator|.
name|length
operator|-
name|o
operator|-
literal|1
argument_list|)
expr_stmt|;
name|bitArray
index|[
name|o
index|]
operator|=
literal|1L
operator|<<
name|i
expr_stmt|;
block|}
else|else
block|{
comment|// we don't have extra space so we need to resize to insert the new long
specifier|final
name|int
name|newSize
init|=
name|oversize
argument_list|(
name|bitArray
operator|.
name|length
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|newBitArray
init|=
operator|new
name|long
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
literal|0
argument_list|,
name|newBitArray
argument_list|,
literal|0
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|newBitArray
index|[
name|o
index|]
operator|=
literal|1L
operator|<<
name|i
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
name|o
argument_list|,
name|newBitArray
argument_list|,
name|o
operator|+
literal|1
argument_list|,
name|bitArray
operator|.
name|length
operator|-
name|o
argument_list|)
expr_stmt|;
name|bits
index|[
name|i4096
index|]
operator|=
name|newBitArray
expr_stmt|;
name|ramBytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|newBitArray
argument_list|)
operator|-
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|bitArray
argument_list|)
expr_stmt|;
block|}
operator|++
name|nonZeroLongCount
expr_stmt|;
block|}
comment|/**    * Clear the bit at index<tt>i</tt>.    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|consistent
argument_list|(
name|i
argument_list|)
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
name|and
argument_list|(
name|i4096
argument_list|,
name|i64
argument_list|,
operator|~
operator|(
literal|1L
operator|<<
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|and
specifier|private
name|void
name|and
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|i64
parameter_list|,
name|long
name|mask
parameter_list|)
block|{
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// offset of the long bits we are interested in in the array
specifier|final
name|int
name|o
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
name|long
name|bits
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
index|[
name|o
index|]
operator|&
name|mask
decl_stmt|;
if|if
condition|(
name|bits
operator|==
literal|0
condition|)
block|{
name|removeLong
argument_list|(
name|i4096
argument_list|,
name|i64
argument_list|,
name|index
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|bits
index|[
name|i4096
index|]
index|[
name|o
index|]
operator|=
name|bits
expr_stmt|;
block|}
block|}
block|}
DECL|method|removeLong
specifier|private
name|void
name|removeLong
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|i64
parameter_list|,
name|long
name|index
parameter_list|,
name|int
name|o
parameter_list|)
block|{
name|index
operator|&=
operator|~
operator|(
literal|1L
operator|<<
name|i64
operator|)
expr_stmt|;
name|indices
index|[
name|i4096
index|]
operator|=
name|index
expr_stmt|;
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
comment|// release memory, there is nothing in this block anymore
name|this
operator|.
name|bits
index|[
name|i4096
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|length
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|bitArray
init|=
name|bits
index|[
name|i4096
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bitArray
argument_list|,
name|o
operator|+
literal|1
argument_list|,
name|bitArray
argument_list|,
name|o
argument_list|,
name|length
operator|-
name|o
argument_list|)
expr_stmt|;
name|bitArray
index|[
name|length
index|]
operator|=
literal|0L
expr_stmt|;
block|}
name|nonZeroLongCount
operator|-=
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
assert|assert
name|from
operator|>=
literal|0
assert|;
assert|assert
name|to
operator|<=
name|length
assert|;
if|if
condition|(
name|from
operator|>=
name|to
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|firstBlock
init|=
name|from
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|int
name|lastBlock
init|=
operator|(
name|to
operator|-
literal|1
operator|)
operator|>>>
literal|12
decl_stmt|;
if|if
condition|(
name|firstBlock
operator|==
name|lastBlock
condition|)
block|{
name|clearWithinBlock
argument_list|(
name|firstBlock
argument_list|,
name|from
operator|&
name|MASK_4096
argument_list|,
operator|(
name|to
operator|-
literal|1
operator|)
operator|&
name|MASK_4096
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clearWithinBlock
argument_list|(
name|firstBlock
argument_list|,
name|from
operator|&
name|MASK_4096
argument_list|,
name|MASK_4096
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|firstBlock
operator|+
literal|1
init|;
name|i
operator|<
name|lastBlock
condition|;
operator|++
name|i
control|)
block|{
name|nonZeroLongCount
operator|-=
name|Long
operator|.
name|bitCount
argument_list|(
name|indices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|indices
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
name|bits
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|clearWithinBlock
argument_list|(
name|lastBlock
argument_list|,
literal|0
argument_list|,
operator|(
name|to
operator|-
literal|1
operator|)
operator|&
name|MASK_4096
argument_list|)
expr_stmt|;
block|}
block|}
comment|// create a long that has bits set to one between from and to
DECL|method|mask
specifier|private
specifier|static
name|long
name|mask
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
return|return
operator|(
operator|(
literal|1L
operator|<<
operator|(
name|to
operator|-
name|from
operator|)
operator|<<
literal|1
operator|)
operator|-
literal|1
operator|)
operator|<<
name|from
return|;
block|}
DECL|method|clearWithinBlock
specifier|private
name|void
name|clearWithinBlock
parameter_list|(
name|int
name|i4096
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
name|int
name|firstLong
init|=
name|from
operator|>>>
literal|6
decl_stmt|;
name|int
name|lastLong
init|=
name|to
operator|>>>
literal|6
decl_stmt|;
if|if
condition|(
name|firstLong
operator|==
name|lastLong
condition|)
block|{
name|and
argument_list|(
name|i4096
argument_list|,
name|firstLong
argument_list|,
operator|~
name|mask
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|firstLong
operator|<
name|lastLong
assert|;
name|and
argument_list|(
name|i4096
argument_list|,
name|lastLong
argument_list|,
operator|~
name|mask
argument_list|(
literal|0
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|lastLong
operator|-
literal|1
init|;
name|i
operator|>=
name|firstLong
operator|+
literal|1
condition|;
operator|--
name|i
control|)
block|{
name|and
argument_list|(
name|i4096
argument_list|,
name|i
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
name|and
argument_list|(
name|i4096
argument_list|,
name|firstLong
argument_list|,
operator|~
name|mask
argument_list|(
name|from
argument_list|,
literal|63
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Return the first document that occurs on or after the provided block index. */
DECL|method|firstDoc
specifier|private
name|int
name|firstDoc
parameter_list|(
name|int
name|i4096
parameter_list|)
block|{
name|long
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i4096
operator|<
name|indices
operator|.
name|length
condition|)
block|{
name|index
operator|=
name|indices
index|[
name|i4096
index|]
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|i64
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
operator|(
name|i4096
operator|<<
literal|12
operator|)
operator||
operator|(
name|i64
operator|<<
literal|6
operator|)
operator||
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
index|[
name|i4096
index|]
index|[
literal|0
index|]
argument_list|)
return|;
block|}
name|i4096
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
DECL|method|nextSetBit
specifier|public
name|int
name|nextSetBit
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|i
operator|<
name|length
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|long
index|[]
name|bitArray
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
decl_stmt|;
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
name|int
name|o
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// There is at least one bit that is set in the current long, check if
comment|// one of them is after i
specifier|final
name|long
name|bits
init|=
name|bitArray
index|[
name|o
index|]
operator|>>>
name|i
decl_stmt|;
comment|// shifts are mod 64
if|if
condition|(
name|bits
operator|!=
literal|0
condition|)
block|{
return|return
name|i
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
argument_list|)
return|;
block|}
name|o
operator|+=
literal|1
expr_stmt|;
block|}
specifier|final
name|long
name|indexBits
init|=
name|index
operator|>>>
name|i64
operator|>>>
literal|1
decl_stmt|;
if|if
condition|(
name|indexBits
operator|==
literal|0
condition|)
block|{
comment|// no more bits are set in the current block of 4096 bits, go to the next one
return|return
name|firstDoc
argument_list|(
name|i4096
operator|+
literal|1
argument_list|)
return|;
block|}
comment|// there are still set bits
name|i64
operator|+=
literal|1
operator|+
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|indexBits
argument_list|)
expr_stmt|;
specifier|final
name|long
name|bits
init|=
name|bitArray
index|[
name|o
index|]
decl_stmt|;
return|return
operator|(
name|i64
operator|<<
literal|6
operator|)
operator||
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|bits
argument_list|)
return|;
block|}
comment|/** Return the last document that occurs on or before the provided block index. */
DECL|method|lastDoc
specifier|private
name|int
name|lastDoc
parameter_list|(
name|int
name|i4096
parameter_list|)
block|{
name|long
name|index
decl_stmt|;
while|while
condition|(
name|i4096
operator|>=
literal|0
condition|)
block|{
name|index
operator|=
name|indices
index|[
name|i4096
index|]
expr_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
specifier|final
name|int
name|i64
init|=
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|long
name|bits
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
index|[
name|Long
operator|.
name|bitCount
argument_list|(
name|index
argument_list|)
operator|-
literal|1
index|]
decl_stmt|;
return|return
operator|(
name|i4096
operator|<<
literal|12
operator|)
operator||
operator|(
name|i64
operator|<<
literal|6
operator|)
operator||
operator|(
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|bits
argument_list|)
operator|)
return|;
block|}
name|i4096
operator|-=
literal|1
expr_stmt|;
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|prevSetBit
specifier|public
name|int
name|prevSetBit
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
name|i
operator|>=
literal|0
assert|;
specifier|final
name|int
name|i4096
init|=
name|i
operator|>>>
literal|12
decl_stmt|;
specifier|final
name|long
name|index
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|long
index|[]
name|bitArray
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
decl_stmt|;
name|int
name|i64
init|=
name|i
operator|>>>
literal|6
decl_stmt|;
specifier|final
name|long
name|indexBits
init|=
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
specifier|final
name|int
name|o
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|indexBits
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// There is at least one bit that is set in the same long, check if there
comment|// is one bit that is set that is lower than i
specifier|final
name|long
name|bits
init|=
name|bitArray
index|[
name|o
index|]
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i
operator|<<
literal|1
operator|)
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|bits
operator|!=
literal|0
condition|)
block|{
return|return
operator|(
name|i64
operator|<<
literal|6
operator|)
operator||
operator|(
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|bits
argument_list|)
operator|)
return|;
block|}
block|}
if|if
condition|(
name|indexBits
operator|==
literal|0
condition|)
block|{
comment|// no more bits are set in this block, go find the last bit in the
comment|// previous block
return|return
name|lastDoc
argument_list|(
name|i4096
operator|-
literal|1
argument_list|)
return|;
block|}
comment|// go to the previous long
name|i64
operator|=
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|indexBits
argument_list|)
expr_stmt|;
specifier|final
name|long
name|bits
init|=
name|bitArray
index|[
name|o
operator|-
literal|1
index|]
decl_stmt|;
return|return
operator|(
name|i4096
operator|<<
literal|12
operator|)
operator||
operator|(
name|i64
operator|<<
literal|6
operator|)
operator||
operator|(
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|bits
argument_list|)
operator|)
return|;
block|}
comment|/** Return the long bits at the given<code>i64</code> index. */
DECL|method|longBits
specifier|private
name|long
name|longBits
parameter_list|(
name|long
name|index
parameter_list|,
name|long
index|[]
name|bits
parameter_list|,
name|int
name|i64
parameter_list|)
block|{
if|if
condition|(
operator|(
name|index
operator|&
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
return|return
literal|0L
return|;
block|}
else|else
block|{
return|return
name|bits
index|[
name|Long
operator|.
name|bitCount
argument_list|(
name|index
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|i64
operator|)
operator|-
literal|1
operator|)
argument_list|)
index|]
return|;
block|}
block|}
DECL|method|or
specifier|private
name|void
name|or
parameter_list|(
specifier|final
name|int
name|i4096
parameter_list|,
specifier|final
name|long
name|index
parameter_list|,
name|long
index|[]
name|bits
parameter_list|,
name|int
name|nonZeroLongCount
parameter_list|)
block|{
assert|assert
name|Long
operator|.
name|bitCount
argument_list|(
name|index
argument_list|)
operator|==
name|nonZeroLongCount
assert|;
specifier|final
name|long
name|currentIndex
init|=
name|indices
index|[
name|i4096
index|]
decl_stmt|;
if|if
condition|(
name|currentIndex
operator|==
literal|0
condition|)
block|{
comment|// fast path: if we currently have nothing in the block, just copy the data
comment|// this especially happens all the time if you call OR on an empty set
name|indices
index|[
name|i4096
index|]
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|bits
index|[
name|i4096
index|]
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|bits
argument_list|,
name|nonZeroLongCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|nonZeroLongCount
operator|+=
name|nonZeroLongCount
expr_stmt|;
return|return;
block|}
specifier|final
name|long
index|[]
name|currentBits
init|=
name|this
operator|.
name|bits
index|[
name|i4096
index|]
decl_stmt|;
specifier|final
name|long
index|[]
name|newBits
decl_stmt|;
specifier|final
name|long
name|newIndex
init|=
name|currentIndex
operator||
name|index
decl_stmt|;
specifier|final
name|int
name|requiredCapacity
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|newIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentBits
operator|.
name|length
operator|>=
name|requiredCapacity
condition|)
block|{
name|newBits
operator|=
name|currentBits
expr_stmt|;
block|}
else|else
block|{
name|newBits
operator|=
operator|new
name|long
index|[
name|oversize
argument_list|(
name|requiredCapacity
argument_list|)
index|]
expr_stmt|;
block|}
comment|// we iterate backwards in order to not override data we might need on the next iteration if the
comment|// array is reused
for|for
control|(
name|int
name|i
init|=
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|newIndex
argument_list|)
init|,
name|newO
init|=
name|Long
operator|.
name|bitCount
argument_list|(
name|newIndex
argument_list|)
operator|-
literal|1
init|;
name|i
operator|<
literal|64
condition|;
name|i
operator|+=
literal|1
operator|+
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|newIndex
operator|<<
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
operator|,
name|newO
operator|-=
literal|1
control|)
block|{
comment|// bitIndex is the index of a bit which is set in newIndex and newO is the number of 1 bits on its right
specifier|final
name|int
name|bitIndex
init|=
literal|63
operator|-
name|i
decl_stmt|;
assert|assert
name|newO
operator|==
name|Long
operator|.
name|bitCount
argument_list|(
name|newIndex
operator|&
operator|(
operator|(
literal|1L
operator|<<
name|bitIndex
operator|)
operator|-
literal|1
operator|)
argument_list|)
assert|;
name|newBits
index|[
name|newO
index|]
operator|=
name|longBits
argument_list|(
name|currentIndex
argument_list|,
name|currentBits
argument_list|,
name|bitIndex
argument_list|)
operator||
name|longBits
argument_list|(
name|index
argument_list|,
name|bits
argument_list|,
name|bitIndex
argument_list|)
expr_stmt|;
block|}
name|indices
index|[
name|i4096
index|]
operator|=
name|newIndex
expr_stmt|;
name|this
operator|.
name|bits
index|[
name|i4096
index|]
operator|=
name|newBits
expr_stmt|;
name|this
operator|.
name|nonZeroLongCount
operator|+=
name|nonZeroLongCount
operator|-
name|Long
operator|.
name|bitCount
argument_list|(
name|currentIndex
operator|&
name|index
argument_list|)
expr_stmt|;
block|}
DECL|method|or
specifier|private
name|void
name|or
parameter_list|(
name|SparseFixedBitSet
name|other
parameter_list|)
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
name|other
operator|.
name|indices
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|index
init|=
name|other
operator|.
name|indices
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|index
operator|!=
literal|0
condition|)
block|{
name|or
argument_list|(
name|i
argument_list|,
name|index
argument_list|,
name|other
operator|.
name|bits
index|[
name|i
index|]
argument_list|,
name|Long
operator|.
name|bitCount
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * {@link #or(DocIdSetIterator)} impl that works best when<code>it</code> is dense    */
DECL|method|orDense
specifier|private
name|void
name|orDense
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
name|assertUnpositioned
argument_list|(
name|it
argument_list|)
expr_stmt|;
comment|// The goal here is to try to take advantage of the ordering of documents
comment|// to build the data-structure more efficiently
comment|// NOTE: this heavily relies on the fact that shifts are mod 64
specifier|final
name|int
name|firstDoc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstDoc
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
return|return;
block|}
name|int
name|i4096
init|=
name|firstDoc
operator|>>>
literal|12
decl_stmt|;
name|int
name|i64
init|=
name|firstDoc
operator|>>>
literal|6
decl_stmt|;
name|long
name|index
init|=
literal|1L
operator|<<
name|i64
decl_stmt|;
name|long
name|currentLong
init|=
literal|1L
operator|<<
name|firstDoc
decl_stmt|;
comment|// we store at most 64 longs per block so preallocate in order never to have to resize
name|long
index|[]
name|longs
init|=
operator|new
name|long
index|[
literal|64
index|]
decl_stmt|;
name|int
name|numLongs
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|it
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|it
operator|.
name|nextDoc
argument_list|()
control|)
block|{
specifier|final
name|int
name|doc64
init|=
name|doc
operator|>>>
literal|6
decl_stmt|;
if|if
condition|(
name|doc64
operator|==
name|i64
condition|)
block|{
comment|// still in the same long, just set the bit
name|currentLong
operator||=
literal|1L
operator|<<
name|doc
expr_stmt|;
block|}
else|else
block|{
name|longs
index|[
name|numLongs
operator|++
index|]
operator|=
name|currentLong
expr_stmt|;
specifier|final
name|int
name|doc4096
init|=
name|doc
operator|>>>
literal|12
decl_stmt|;
if|if
condition|(
name|doc4096
operator|==
name|i4096
condition|)
block|{
name|index
operator||=
literal|1L
operator|<<
name|doc64
expr_stmt|;
block|}
else|else
block|{
comment|// we are on a new block, flush what we buffered
name|or
argument_list|(
name|i4096
argument_list|,
name|index
argument_list|,
name|longs
argument_list|,
name|numLongs
argument_list|)
expr_stmt|;
comment|// and reset state for the new block
name|i4096
operator|=
name|doc4096
expr_stmt|;
name|index
operator|=
literal|1L
operator|<<
name|doc64
expr_stmt|;
name|numLongs
operator|=
literal|0
expr_stmt|;
block|}
comment|// we are on a new long, reset state
name|i64
operator|=
name|doc64
expr_stmt|;
name|currentLong
operator|=
literal|1L
operator|<<
name|doc
expr_stmt|;
block|}
block|}
comment|// flush
name|longs
index|[
name|numLongs
operator|++
index|]
operator|=
name|currentLong
expr_stmt|;
name|or
argument_list|(
name|i4096
argument_list|,
name|index
argument_list|,
name|longs
argument_list|,
name|numLongs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|or
specifier|public
name|void
name|or
parameter_list|(
name|DocIdSetIterator
name|it
parameter_list|)
throws|throws
name|IOException
block|{
block|{
comment|// specialize union with another SparseFixedBitSet
specifier|final
name|SparseFixedBitSet
name|other
init|=
name|BitSetIterator
operator|.
name|getSparseFixedBitSetOrNull
argument_list|(
name|it
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
name|assertUnpositioned
argument_list|(
name|it
argument_list|)
expr_stmt|;
name|or
argument_list|(
name|other
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// We do not specialize the union with a FixedBitSet since FixedBitSets are
comment|// supposed to be used for dense data and sparse fixed bit sets for sparse
comment|// data, so a sparse set would likely get upgraded by DocIdSetBuilder before
comment|// being or'ed with a FixedBitSet
if|if
condition|(
name|it
operator|.
name|cost
argument_list|()
operator|<
name|indices
operator|.
name|length
condition|)
block|{
comment|// the default impl is good for sparse iterators
name|super
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|orDense
argument_list|(
name|it
argument_list|)
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
return|;
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
literal|"SparseFixedBitSet(size="
operator|+
name|length
operator|+
literal|",cardinality=~"
operator|+
name|approximateCardinality
argument_list|()
return|;
block|}
block|}
end_class

end_unit

