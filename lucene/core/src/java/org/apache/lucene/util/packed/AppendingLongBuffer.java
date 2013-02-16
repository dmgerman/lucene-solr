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

begin_comment
comment|/**  * Utility class to buffer a list of signed longs in memory. This class only  * supports appending and is optimized for the case where values are close to  * each other.  * @lucene.internal  */
end_comment

begin_class
DECL|class|AppendingLongBuffer
specifier|public
specifier|final
class|class
name|AppendingLongBuffer
extends|extends
name|AbstractAppendingLongBuffer
block|{
comment|/** Sole constructor. */
DECL|method|AppendingLongBuffer
specifier|public
name|AppendingLongBuffer
parameter_list|()
block|{
name|super
argument_list|(
literal|16
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
name|long
name|get
parameter_list|(
name|int
name|block
parameter_list|,
name|int
name|element
parameter_list|)
block|{
if|if
condition|(
name|block
operator|==
name|valuesOff
condition|)
block|{
return|return
name|pending
index|[
name|element
index|]
return|;
block|}
elseif|else
if|if
condition|(
name|deltas
index|[
name|block
index|]
operator|==
literal|null
condition|)
block|{
return|return
name|minValues
index|[
name|block
index|]
return|;
block|}
else|else
block|{
return|return
name|minValues
index|[
name|block
index|]
operator|+
name|deltas
index|[
name|block
index|]
operator|.
name|get
argument_list|(
name|element
argument_list|)
return|;
block|}
block|}
DECL|method|packPendingValues
name|void
name|packPendingValues
parameter_list|()
block|{
assert|assert
name|pendingOff
operator|==
name|MAX_PENDING_COUNT
assert|;
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
name|minValues
index|[
name|valuesOff
index|]
operator|=
name|minValue
expr_stmt|;
if|if
condition|(
name|delta
operator|!=
literal|0
condition|)
block|{
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
name|deltas
index|[
name|valuesOff
index|]
operator|=
name|mutable
expr_stmt|;
block|}
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
specifier|final
class|class
name|Iterator
extends|extends
name|AbstractAppendingLongBuffer
operator|.
name|Iterator
block|{
DECL|method|Iterator
specifier|private
name|Iterator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|fillValues
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
elseif|else
if|if
condition|(
name|deltas
index|[
name|vOff
index|]
operator|==
literal|null
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|currentValues
argument_list|,
name|minValues
index|[
name|vOff
index|]
argument_list|)
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
control|)
block|{
name|k
operator|+=
name|deltas
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
block|}
block|}
end_class

end_unit

