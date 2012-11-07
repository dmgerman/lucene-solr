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
name|RamUsageEstimator
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

begin_comment
comment|/**  * Packs integers into 3 bytes (24 bits per value).  * @lucene.internal  */
end_comment

begin_class
DECL|class|Packed8ThreeBlocks
specifier|final
class|class
name|Packed8ThreeBlocks
extends|extends
name|PackedInts
operator|.
name|MutableImpl
block|{
DECL|field|blocks
specifier|final
name|byte
index|[]
name|blocks
decl_stmt|;
DECL|field|MAX_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|MAX_SIZE
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|3
decl_stmt|;
DECL|method|Packed8ThreeBlocks
name|Packed8ThreeBlocks
parameter_list|(
name|int
name|valueCount
parameter_list|)
block|{
name|super
argument_list|(
name|valueCount
argument_list|,
literal|24
argument_list|)
expr_stmt|;
if|if
condition|(
name|valueCount
operator|>
name|MAX_SIZE
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
literal|"MAX_SIZE exceeded"
argument_list|)
throw|;
block|}
name|blocks
operator|=
operator|new
name|byte
index|[
name|valueCount
operator|*
literal|3
index|]
expr_stmt|;
block|}
DECL|method|Packed8ThreeBlocks
name|Packed8ThreeBlocks
parameter_list|(
name|int
name|packedIntsVersion
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|int
name|valueCount
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|valueCount
argument_list|)
expr_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|blocks
argument_list|,
literal|0
argument_list|,
literal|3
operator|*
name|valueCount
argument_list|)
expr_stmt|;
comment|// because packed ints have not always been byte-aligned
specifier|final
name|int
name|remaining
init|=
call|(
name|int
call|)
argument_list|(
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
name|valueCount
argument_list|,
literal|24
argument_list|)
operator|-
literal|3L
operator|*
name|valueCount
operator|*
literal|1
argument_list|)
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
name|remaining
condition|;
operator|++
name|i
control|)
block|{
name|in
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|long
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|index
operator|*
literal|3
decl_stmt|;
return|return
operator|(
name|blocks
index|[
name|o
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|16
operator||
operator|(
name|blocks
index|[
name|o
operator|+
literal|1
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|8
operator||
operator|(
name|blocks
index|[
name|o
operator|+
literal|2
index|]
operator|&
literal|0xFFL
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
operator|:
literal|"len must be> 0 (got "
operator|+
name|len
operator|+
literal|")"
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|gets
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCount
operator|-
name|index
argument_list|,
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|index
operator|*
literal|3
init|,
name|end
init|=
operator|(
name|index
operator|+
name|gets
operator|)
operator|*
literal|3
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|arr
index|[
name|off
operator|++
index|]
operator|=
operator|(
name|blocks
index|[
name|i
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|16
operator||
operator|(
name|blocks
index|[
name|i
operator|+
literal|1
index|]
operator|&
literal|0xFFL
operator|)
operator|<<
literal|8
operator||
operator|(
name|blocks
index|[
name|i
operator|+
literal|2
index|]
operator|&
literal|0xFFL
operator|)
expr_stmt|;
block|}
return|return
name|gets
return|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|void
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
name|value
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|index
operator|*
literal|3
decl_stmt|;
name|blocks
index|[
name|o
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|blocks
index|[
name|o
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|blocks
index|[
name|o
operator|+
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|set
specifier|public
name|int
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|long
index|[]
name|arr
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
assert|assert
name|len
operator|>
literal|0
operator|:
literal|"len must be> 0 (got "
operator|+
name|len
operator|+
literal|")"
assert|;
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|valueCount
assert|;
assert|assert
name|off
operator|+
name|len
operator|<=
name|arr
operator|.
name|length
assert|;
specifier|final
name|int
name|sets
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCount
operator|-
name|index
argument_list|,
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|,
name|o
init|=
name|index
operator|*
literal|3
init|,
name|end
init|=
name|off
operator|+
name|sets
init|;
name|i
operator|<
name|end
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|value
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
name|blocks
index|[
name|o
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|blocks
index|[
name|o
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|value
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|blocks
index|[
name|o
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|value
expr_stmt|;
block|}
return|return
name|sets
return|;
block|}
annotation|@
name|Override
DECL|method|fill
specifier|public
name|void
name|fill
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|long
name|val
parameter_list|)
block|{
specifier|final
name|byte
name|block1
init|=
call|(
name|byte
call|)
argument_list|(
name|val
operator|>>>
literal|16
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|block2
init|=
call|(
name|byte
call|)
argument_list|(
name|val
operator|>>>
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|block3
init|=
operator|(
name|byte
operator|)
name|val
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|fromIndex
operator|*
literal|3
init|,
name|end
init|=
name|toIndex
operator|*
literal|3
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|blocks
index|[
name|i
index|]
operator|=
name|block1
expr_stmt|;
name|blocks
index|[
name|i
operator|+
literal|1
index|]
operator|=
name|block2
expr_stmt|;
name|blocks
index|[
name|i
operator|+
literal|2
index|]
operator|=
name|block3
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|blocks
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|blocks
argument_list|)
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(bitsPerValue="
operator|+
name|bitsPerValue
operator|+
literal|", size="
operator|+
name|size
argument_list|()
operator|+
literal|", elements.length="
operator|+
name|blocks
operator|.
name|length
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

