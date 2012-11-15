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
name|RamUsageEstimator
import|;
end_import

begin_comment
comment|/**  * Utility class to buffer a list of signed longs in memory. This class only  * supports appending.  */
end_comment

begin_class
DECL|class|AppendingLongBuffer
specifier|public
class|class
name|AppendingLongBuffer
block|{
DECL|field|MAX_PENDING_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|MAX_PENDING_COUNT
init|=
literal|1024
decl_stmt|;
DECL|field|minValues
specifier|private
name|long
index|[]
name|minValues
decl_stmt|;
DECL|field|values
specifier|private
name|PackedInts
operator|.
name|Reader
index|[]
name|values
decl_stmt|;
DECL|field|valuesOff
specifier|private
name|int
name|valuesOff
decl_stmt|;
DECL|field|pending
specifier|private
name|long
index|[]
name|pending
decl_stmt|;
DECL|field|pendingOff
specifier|private
name|int
name|pendingOff
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|AppendingLongBuffer
specifier|public
name|AppendingLongBuffer
parameter_list|()
block|{
name|minValues
operator|=
operator|new
name|long
index|[
literal|16
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|PackedInts
operator|.
name|Reader
index|[
literal|16
index|]
expr_stmt|;
name|pending
operator|=
operator|new
name|long
index|[
name|MAX_PENDING_COUNT
index|]
expr_stmt|;
name|valuesOff
operator|=
literal|0
expr_stmt|;
name|pendingOff
operator|=
literal|0
expr_stmt|;
block|}
comment|/** Append a value to this buffer. */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|long
name|l
parameter_list|)
block|{
if|if
condition|(
name|pendingOff
operator|==
name|MAX_PENDING_COUNT
condition|)
block|{
name|packPendingValues
argument_list|()
expr_stmt|;
block|}
name|pending
index|[
name|pendingOff
operator|++
index|]
operator|=
name|l
expr_stmt|;
block|}
DECL|method|packPendingValues
specifier|private
name|void
name|packPendingValues
parameter_list|()
block|{
assert|assert
name|pendingOff
operator|==
name|MAX_PENDING_COUNT
assert|;
comment|// check size
if|if
condition|(
name|values
operator|.
name|length
operator|==
name|valuesOff
condition|)
block|{
specifier|final
name|int
name|newLength
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|valuesOff
operator|+
literal|1
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|minValues
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|minValues
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
name|values
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|values
argument_list|,
name|newLength
argument_list|)
expr_stmt|;
block|}
comment|// compute max delta
name|long
name|minValue
init|=
name|pending
index|[
literal|0
index|]
decl_stmt|;
name|long
name|maxValue
init|=
name|pending
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|pendingOff
condition|;
operator|++
name|i
control|)
block|{
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minValue
argument_list|,
name|pending
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxValue
argument_list|,
name|pending
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|delta
init|=
name|maxValue
operator|-
name|minValue
decl_stmt|;
comment|// build a new packed reader
specifier|final
name|int
name|bitsRequired
init|=
name|delta
operator|<
literal|0
condition|?
literal|64
else|:
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|delta
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
name|pendingOff
condition|;
operator|++
name|i
control|)
block|{
name|pending
index|[
name|i
index|]
operator|-=
name|minValue
expr_stmt|;
block|}
specifier|final
name|PackedInts
operator|.
name|Mutable
name|mutable
init|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
name|pendingOff
argument_list|,
name|bitsRequired
argument_list|,
name|PackedInts
operator|.
name|COMPACT
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
name|pendingOff
condition|;
control|)
block|{
name|i
operator|+=
name|mutable
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|pending
argument_list|,
name|i
argument_list|,
name|pendingOff
operator|-
name|i
argument_list|)
expr_stmt|;
block|}
comment|// store it
name|minValues
index|[
name|valuesOff
index|]
operator|=
name|minValue
expr_stmt|;
name|values
index|[
name|valuesOff
index|]
operator|=
name|mutable
expr_stmt|;
operator|++
name|valuesOff
expr_stmt|;
comment|// reset pending buffer
name|pendingOff
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|valuesOff
operator|*
name|MAX_PENDING_COUNT
operator|+
name|pendingOff
return|;
block|}
comment|/** Return an iterator over the values of this buffer. */
DECL|method|iterator
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|()
return|;
block|}
comment|/** A long iterator. */
DECL|class|Iterator
specifier|public
class|class
name|Iterator
block|{
DECL|field|currentValues
name|long
index|[]
name|currentValues
decl_stmt|;
DECL|field|vOff
DECL|field|pOff
name|int
name|vOff
decl_stmt|,
name|pOff
decl_stmt|;
DECL|method|Iterator
specifier|private
name|Iterator
parameter_list|()
block|{
name|vOff
operator|=
name|pOff
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|valuesOff
operator|==
literal|0
condition|)
block|{
name|currentValues
operator|=
name|pending
expr_stmt|;
block|}
else|else
block|{
name|currentValues
operator|=
operator|new
name|long
index|[
name|MAX_PENDING_COUNT
index|]
expr_stmt|;
name|fillValues
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|fillValues
specifier|private
name|void
name|fillValues
parameter_list|()
block|{
if|if
condition|(
name|vOff
operator|==
name|valuesOff
condition|)
block|{
name|currentValues
operator|=
name|pending
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|MAX_PENDING_COUNT
condition|;
operator|++
name|k
control|)
block|{
name|k
operator|+=
name|values
index|[
name|vOff
index|]
operator|.
name|get
argument_list|(
name|k
argument_list|,
name|currentValues
argument_list|,
name|k
argument_list|,
name|MAX_PENDING_COUNT
operator|-
name|k
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|MAX_PENDING_COUNT
condition|;
operator|++
name|k
control|)
block|{
name|currentValues
index|[
name|k
index|]
operator|+=
name|minValues
index|[
name|vOff
index|]
expr_stmt|;
block|}
block|}
block|}
comment|/** Whether or not there are remaining values. */
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|vOff
operator|<
name|valuesOff
operator|||
name|pOff
operator|<
name|pendingOff
return|;
block|}
comment|/** Return the next long in the buffer. */
DECL|method|next
specifier|public
name|long
name|next
parameter_list|()
block|{
assert|assert
name|hasNext
argument_list|()
assert|;
name|long
name|result
init|=
name|currentValues
index|[
name|pOff
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|pOff
operator|==
name|MAX_PENDING_COUNT
condition|)
block|{
name|vOff
operator|+=
literal|1
expr_stmt|;
name|pOff
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|vOff
operator|<=
name|valuesOff
condition|)
block|{
name|fillValues
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
comment|/**    * Return the number of bytes used by this instance.    */
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
name|long
name|bytesUsed
init|=
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
literal|3
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
comment|// the 3 arrays
operator|+
literal|2
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
comment|// the 2 offsets
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|pending
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|minValues
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
operator|(
name|long
operator|)
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|*
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// values
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|valuesOff
condition|;
operator|++
name|i
control|)
block|{
name|bytesUsed
operator|+=
name|values
index|[
name|i
index|]
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|bytesUsed
return|;
block|}
block|}
end_class

end_unit

