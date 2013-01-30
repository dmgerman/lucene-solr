begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|ByteBlockPool
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
name|BytesRefIterator
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
name|Counter
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SorterTemplate
import|;
end_import

begin_comment
comment|/**  * A simple append only random-access {@link BytesRef} array that stores full  * copies of the appended bytes in a {@link ByteBlockPool}.  *   *   *<b>Note: This class is not Thread-Safe!</b>  *   * @lucene.internal  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BytesRefArray
specifier|final
class|class
name|BytesRefArray
block|{
DECL|field|pool
specifier|private
specifier|final
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|field|offsets
specifier|private
name|int
index|[]
name|offsets
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
DECL|field|lastElement
specifier|private
name|int
name|lastElement
init|=
literal|0
decl_stmt|;
DECL|field|currentOffset
specifier|private
name|int
name|currentOffset
init|=
literal|0
decl_stmt|;
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
comment|/**    * Creates a new {@link BytesRefArray} with a counter to track allocated bytes    */
DECL|method|BytesRefArray
specifier|public
name|BytesRefArray
parameter_list|(
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|ByteBlockPool
operator|.
name|DirectTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|.
name|nextBuffer
argument_list|()
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|RamUsageEstimator
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
comment|/**    * Clears this {@link BytesRefArray}    */
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|lastElement
operator|=
literal|0
expr_stmt|;
name|currentOffset
operator|=
literal|0
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|offsets
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|pool
operator|.
name|reset
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// no need to 0 fill the buffers we control the allocator
block|}
comment|/**    * Appends a copy of the given {@link BytesRef} to this {@link BytesRefArray}.    * @param bytes the bytes to append    * @return the ordinal of the appended bytes    */
DECL|method|append
specifier|public
name|int
name|append
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|lastElement
operator|>=
name|offsets
operator|.
name|length
condition|)
block|{
name|int
name|oldLen
init|=
name|offsets
operator|.
name|length
decl_stmt|;
name|offsets
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|offsets
argument_list|,
name|offsets
operator|.
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|(
name|offsets
operator|.
name|length
operator|-
name|oldLen
operator|)
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|append
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|offsets
index|[
name|lastElement
operator|++
index|]
operator|=
name|currentOffset
expr_stmt|;
name|currentOffset
operator|+=
name|bytes
operator|.
name|length
expr_stmt|;
return|return
name|lastElement
return|;
block|}
comment|/**    * Returns the current size of this {@link BytesRefArray}    * @return the current size of this {@link BytesRefArray}    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|lastElement
return|;
block|}
comment|/**    * Returns the<i>n'th</i> element of this {@link BytesRefArray}    * @param spare a spare {@link BytesRef} instance    * @param ord the elements ordinal to retrieve     * @return the<i>n'th</i> element of this {@link BytesRefArray}    */
DECL|method|get
specifier|public
name|BytesRef
name|get
parameter_list|(
name|BytesRef
name|spare
parameter_list|,
name|int
name|ord
parameter_list|)
block|{
if|if
condition|(
name|lastElement
operator|>
name|ord
condition|)
block|{
name|int
name|offset
init|=
name|offsets
index|[
name|ord
index|]
decl_stmt|;
name|int
name|length
init|=
name|ord
operator|==
name|lastElement
operator|-
literal|1
condition|?
name|currentOffset
operator|-
name|offset
else|:
name|offsets
index|[
name|ord
operator|+
literal|1
index|]
operator|-
name|offset
decl_stmt|;
name|pool
operator|.
name|readBytes
argument_list|(
name|spare
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"index "
operator|+
name|ord
operator|+
literal|" must be less than the size: "
operator|+
name|lastElement
argument_list|)
throw|;
block|}
DECL|method|sort
specifier|private
name|int
index|[]
name|sort
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|orderedEntries
init|=
operator|new
name|int
index|[
name|size
argument_list|()
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
name|orderedEntries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|orderedEntries
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
operator|new
name|SorterTemplate
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|o
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|orderedEntries
index|[
name|i
index|]
operator|=
name|orderedEntries
index|[
name|j
index|]
expr_stmt|;
name|orderedEntries
index|[
name|j
index|]
operator|=
name|o
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ord1
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|,
name|ord2
init|=
name|orderedEntries
index|[
name|j
index|]
decl_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|get
argument_list|(
name|scratch1
argument_list|,
name|ord1
argument_list|)
argument_list|,
name|get
argument_list|(
name|scratch2
argument_list|,
name|ord2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|orderedEntries
index|[
name|i
index|]
decl_stmt|;
name|get
argument_list|(
name|pivot
argument_list|,
name|ord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|orderedEntries
index|[
name|j
index|]
decl_stmt|;
return|return
name|comp
operator|.
name|compare
argument_list|(
name|pivot
argument_list|,
name|get
argument_list|(
name|scratch2
argument_list|,
name|ord
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|BytesRef
name|pivot
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|,
name|scratch1
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|,
name|scratch2
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
block|}
operator|.
name|quickSort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|orderedEntries
return|;
block|}
comment|/**    * sugar for {@link #iterator(Comparator)} with a<code>null</code> comparator    */
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|()
block|{
return|return
name|iterator
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    *<p>    * Returns a {@link BytesRefIterator} with point in time semantics. The    * iterator provides access to all so far appended {@link BytesRef} instances.    *</p>    *<p>    * If a non<code>null</code> {@link Comparator} is provided the iterator will    * iterate the byte values in the order specified by the comparator. Otherwise    * the order is the same as the values were appended.    *</p>    *<p>    * This is a non-destructive operation.    *</p>    */
DECL|method|iterator
specifier|public
name|BytesRefIterator
name|iterator
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|comp
parameter_list|)
block|{
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|ords
init|=
name|comp
operator|==
literal|null
condition|?
literal|null
else|:
name|sort
argument_list|(
name|comp
argument_list|)
decl_stmt|;
return|return
operator|new
name|BytesRefIterator
argument_list|()
block|{
name|int
name|pos
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BytesRef
name|next
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|<
name|size
condition|)
block|{
return|return
name|get
argument_list|(
name|spare
argument_list|,
name|ords
operator|==
literal|null
condition|?
name|pos
operator|++
else|:
name|ords
index|[
name|pos
operator|++
index|]
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|comp
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

