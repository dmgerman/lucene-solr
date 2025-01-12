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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Accountable
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
name|LongValues
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
comment|/**  * Base implementation for {@link PagedMutable} and {@link PagedGrowableWriter}.  * @lucene.internal  */
end_comment

begin_class
DECL|class|AbstractPagedMutable
specifier|abstract
class|class
name|AbstractPagedMutable
parameter_list|<
name|T
extends|extends
name|AbstractPagedMutable
parameter_list|<
name|T
parameter_list|>
parameter_list|>
extends|extends
name|LongValues
implements|implements
name|Accountable
block|{
DECL|field|MIN_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|MIN_BLOCK_SIZE
init|=
literal|1
operator|<<
literal|6
decl_stmt|;
DECL|field|MAX_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|MAX_BLOCK_SIZE
init|=
literal|1
operator|<<
literal|30
decl_stmt|;
DECL|field|size
specifier|final
name|long
name|size
decl_stmt|;
DECL|field|pageShift
specifier|final
name|int
name|pageShift
decl_stmt|;
DECL|field|pageMask
specifier|final
name|int
name|pageMask
decl_stmt|;
DECL|field|subMutables
specifier|final
name|PackedInts
operator|.
name|Mutable
index|[]
name|subMutables
decl_stmt|;
DECL|field|bitsPerValue
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|method|AbstractPagedMutable
name|AbstractPagedMutable
parameter_list|(
name|int
name|bitsPerValue
parameter_list|,
name|long
name|size
parameter_list|,
name|int
name|pageSize
parameter_list|)
block|{
name|this
operator|.
name|bitsPerValue
operator|=
name|bitsPerValue
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|pageShift
operator|=
name|checkBlockSize
argument_list|(
name|pageSize
argument_list|,
name|MIN_BLOCK_SIZE
argument_list|,
name|MAX_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|pageMask
operator|=
name|pageSize
operator|-
literal|1
expr_stmt|;
specifier|final
name|int
name|numPages
init|=
name|numBlocks
argument_list|(
name|size
argument_list|,
name|pageSize
argument_list|)
decl_stmt|;
name|subMutables
operator|=
operator|new
name|PackedInts
operator|.
name|Mutable
index|[
name|numPages
index|]
expr_stmt|;
block|}
DECL|method|fillPages
specifier|protected
specifier|final
name|void
name|fillPages
parameter_list|()
block|{
specifier|final
name|int
name|numPages
init|=
name|numBlocks
argument_list|(
name|size
argument_list|,
name|pageSize
argument_list|()
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
name|numPages
condition|;
operator|++
name|i
control|)
block|{
comment|// do not allocate for more entries than necessary on the last page
specifier|final
name|int
name|valueCount
init|=
name|i
operator|==
name|numPages
operator|-
literal|1
condition|?
name|lastPageSize
argument_list|(
name|size
argument_list|)
else|:
name|pageSize
argument_list|()
decl_stmt|;
name|subMutables
index|[
name|i
index|]
operator|=
name|newMutable
argument_list|(
name|valueCount
argument_list|,
name|bitsPerValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|newMutable
specifier|protected
specifier|abstract
name|PackedInts
operator|.
name|Mutable
name|newMutable
parameter_list|(
name|int
name|valueCount
parameter_list|,
name|int
name|bitsPerValue
parameter_list|)
function_decl|;
DECL|method|lastPageSize
specifier|final
name|int
name|lastPageSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
specifier|final
name|int
name|sz
init|=
name|indexInPage
argument_list|(
name|size
argument_list|)
decl_stmt|;
return|return
name|sz
operator|==
literal|0
condition|?
name|pageSize
argument_list|()
else|:
name|sz
return|;
block|}
DECL|method|pageSize
specifier|final
name|int
name|pageSize
parameter_list|()
block|{
return|return
name|pageMask
operator|+
literal|1
return|;
block|}
comment|/** The number of values. */
DECL|method|size
specifier|public
specifier|final
name|long
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|pageIndex
specifier|final
name|int
name|pageIndex
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|index
operator|>>>
name|pageShift
argument_list|)
return|;
block|}
DECL|method|indexInPage
specifier|final
name|int
name|indexInPage
parameter_list|(
name|long
name|index
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|index
operator|&
name|pageMask
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|final
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
name|size
assert|;
specifier|final
name|int
name|pageIndex
init|=
name|pageIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|indexInPage
init|=
name|indexInPage
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|subMutables
index|[
name|pageIndex
index|]
operator|.
name|get
argument_list|(
name|indexInPage
argument_list|)
return|;
block|}
comment|/** Set value at<code>index</code>. */
DECL|method|set
specifier|public
specifier|final
name|void
name|set
parameter_list|(
name|long
name|index
parameter_list|,
name|long
name|value
parameter_list|)
block|{
assert|assert
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|size
assert|;
specifier|final
name|int
name|pageIndex
init|=
name|pageIndex
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|int
name|indexInPage
init|=
name|indexInPage
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|subMutables
index|[
name|pageIndex
index|]
operator|.
name|set
argument_list|(
name|indexInPage
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|baseRamBytesUsed
specifier|protected
name|long
name|baseRamBytesUsed
parameter_list|()
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_HEADER
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|Long
operator|.
name|BYTES
operator|+
literal|3
operator|*
name|Integer
operator|.
name|BYTES
return|;
block|}
annotation|@
name|Override
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
name|baseRamBytesUsed
argument_list|()
argument_list|)
decl_stmt|;
name|bytesUsed
operator|+=
name|RamUsageEstimator
operator|.
name|alignObjectSize
argument_list|(
name|RamUsageEstimator
operator|.
name|shallowSizeOf
argument_list|(
name|subMutables
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PackedInts
operator|.
name|Mutable
name|gw
range|:
name|subMutables
control|)
block|{
name|bytesUsed
operator|+=
name|gw
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
block|}
return|return
name|bytesUsed
return|;
block|}
DECL|method|newUnfilledCopy
specifier|protected
specifier|abstract
name|T
name|newUnfilledCopy
parameter_list|(
name|long
name|newSize
parameter_list|)
function_decl|;
comment|/** Create a new copy of size<code>newSize</code> based on the content of    *  this buffer. This method is much more efficient than creating a new    *  instance and copying values one by one. */
DECL|method|resize
specifier|public
specifier|final
name|T
name|resize
parameter_list|(
name|long
name|newSize
parameter_list|)
block|{
specifier|final
name|T
name|copy
init|=
name|newUnfilledCopy
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numCommonPages
init|=
name|Math
operator|.
name|min
argument_list|(
name|copy
operator|.
name|subMutables
operator|.
name|length
argument_list|,
name|subMutables
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|long
index|[]
name|copyBuffer
init|=
operator|new
name|long
index|[
literal|1024
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
name|copy
operator|.
name|subMutables
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|valueCount
init|=
name|i
operator|==
name|copy
operator|.
name|subMutables
operator|.
name|length
operator|-
literal|1
condition|?
name|lastPageSize
argument_list|(
name|newSize
argument_list|)
else|:
name|pageSize
argument_list|()
decl_stmt|;
specifier|final
name|int
name|bpv
init|=
name|i
operator|<
name|numCommonPages
condition|?
name|subMutables
index|[
name|i
index|]
operator|.
name|getBitsPerValue
argument_list|()
else|:
name|this
operator|.
name|bitsPerValue
decl_stmt|;
name|copy
operator|.
name|subMutables
index|[
name|i
index|]
operator|=
name|newMutable
argument_list|(
name|valueCount
argument_list|,
name|bpv
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|numCommonPages
condition|)
block|{
specifier|final
name|int
name|copyLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|valueCount
argument_list|,
name|subMutables
index|[
name|i
index|]
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|PackedInts
operator|.
name|copy
argument_list|(
name|subMutables
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|copy
operator|.
name|subMutables
index|[
name|i
index|]
argument_list|,
literal|0
argument_list|,
name|copyLength
argument_list|,
name|copyBuffer
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|copy
return|;
block|}
comment|/** Similar to {@link ArrayUtil#grow(long[], int)}. */
DECL|method|grow
specifier|public
specifier|final
name|T
name|grow
parameter_list|(
name|long
name|minSize
parameter_list|)
block|{
assert|assert
name|minSize
operator|>=
literal|0
assert|;
if|if
condition|(
name|minSize
operator|<=
name|size
argument_list|()
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|T
name|result
init|=
operator|(
name|T
operator|)
name|this
decl_stmt|;
return|return
name|result
return|;
block|}
name|long
name|extra
init|=
name|minSize
operator|>>>
literal|3
decl_stmt|;
if|if
condition|(
name|extra
operator|<
literal|3
condition|)
block|{
name|extra
operator|=
literal|3
expr_stmt|;
block|}
specifier|final
name|long
name|newSize
init|=
name|minSize
operator|+
name|extra
decl_stmt|;
return|return
name|resize
argument_list|(
name|newSize
argument_list|)
return|;
block|}
comment|/** Similar to {@link ArrayUtil#grow(long[])}. */
DECL|method|grow
specifier|public
specifier|final
name|T
name|grow
parameter_list|()
block|{
return|return
name|grow
argument_list|(
name|size
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
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
literal|"(size="
operator|+
name|size
argument_list|()
operator|+
literal|",pageSize="
operator|+
name|pageSize
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

