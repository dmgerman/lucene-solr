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
name|BitSet
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
name|DocIdSet
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
comment|/**  * Stores and iterate on sorted integers in compressed form in RAM.<br>  * The code for compressing the differences between ascending integers was  * borrowed from {@link org.apache.lucene.store.IndexInput} and  * {@link org.apache.lucene.store.IndexOutput}.  *<p>  *<b>NOTE:</b> this class assumes the stored integers are doc Ids (hence why it  * extends {@link DocIdSet}). Therefore its {@link #iterator()} assumes {@link  * DocIdSetIterator#NO_MORE_DOCS} can be used as sentinel. If you intent to use  * this value, then make sure it's not used during search flow.  */
end_comment

begin_class
DECL|class|SortedVIntList
specifier|public
class|class
name|SortedVIntList
extends|extends
name|DocIdSet
block|{
comment|/** When a BitSet has fewer than 1 in BITS2VINTLIST_SIZE bits set,    * a SortedVIntList representing the index numbers of the set bits    * will be smaller than that BitSet.    */
DECL|field|BITS2VINTLIST_SIZE
specifier|final
specifier|static
name|int
name|BITS2VINTLIST_SIZE
init|=
literal|8
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
DECL|field|lastBytePos
specifier|private
name|int
name|lastBytePos
decl_stmt|;
comment|/**    *  Create a SortedVIntList from all elements of an array of integers.    *    * @param  sortedInts  A sorted array of non negative integers.    */
DECL|method|SortedVIntList
specifier|public
name|SortedVIntList
parameter_list|(
name|int
modifier|...
name|sortedInts
parameter_list|)
block|{
name|this
argument_list|(
name|sortedInts
argument_list|,
name|sortedInts
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a SortedVIntList from an array of integers.    * @param  sortedInts  An array of sorted non negative integers.    * @param  inputSize   The number of integers to be used from the array.    */
DECL|method|SortedVIntList
specifier|public
name|SortedVIntList
parameter_list|(
name|int
index|[]
name|sortedInts
parameter_list|,
name|int
name|inputSize
parameter_list|)
block|{
name|SortedVIntListBuilder
name|builder
init|=
operator|new
name|SortedVIntListBuilder
argument_list|()
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
name|inputSize
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addInt
argument_list|(
name|sortedInts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a SortedVIntList from a BitSet.    * @param  bits  A bit set representing a set of integers.    */
DECL|method|SortedVIntList
specifier|public
name|SortedVIntList
parameter_list|(
name|BitSet
name|bits
parameter_list|)
block|{
name|SortedVIntListBuilder
name|builder
init|=
operator|new
name|SortedVIntListBuilder
argument_list|()
decl_stmt|;
name|int
name|nextInt
init|=
name|bits
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
while|while
condition|(
name|nextInt
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|addInt
argument_list|(
name|nextInt
argument_list|)
expr_stmt|;
name|nextInt
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|nextInt
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a SortedVIntList from an OpenBitSet.    * @param  bits  A bit set representing a set of integers.    */
DECL|method|SortedVIntList
specifier|public
name|SortedVIntList
parameter_list|(
name|OpenBitSet
name|bits
parameter_list|)
block|{
name|SortedVIntListBuilder
name|builder
init|=
operator|new
name|SortedVIntListBuilder
argument_list|()
decl_stmt|;
name|int
name|nextInt
init|=
name|bits
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
while|while
condition|(
name|nextInt
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|addInt
argument_list|(
name|nextInt
argument_list|)
expr_stmt|;
name|nextInt
operator|=
name|bits
operator|.
name|nextSetBit
argument_list|(
name|nextInt
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a SortedVIntList.    * @param  docIdSetIterator  An iterator providing document numbers as a set of integers.    *                  This DocIdSetIterator is iterated completely when this constructor    *                  is called and it must provide the integers in non    *                  decreasing order.    */
DECL|method|SortedVIntList
specifier|public
name|SortedVIntList
parameter_list|(
name|DocIdSetIterator
name|docIdSetIterator
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedVIntListBuilder
name|builder
init|=
operator|new
name|SortedVIntListBuilder
argument_list|()
decl_stmt|;
name|int
name|doc
decl_stmt|;
while|while
condition|(
operator|(
name|doc
operator|=
name|docIdSetIterator
operator|.
name|nextDoc
argument_list|()
operator|)
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|builder
operator|.
name|addInt
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
DECL|class|SortedVIntListBuilder
specifier|private
class|class
name|SortedVIntListBuilder
block|{
DECL|field|lastInt
specifier|private
name|int
name|lastInt
init|=
literal|0
decl_stmt|;
DECL|method|SortedVIntListBuilder
name|SortedVIntListBuilder
parameter_list|()
block|{
name|initBytes
argument_list|()
expr_stmt|;
name|lastInt
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|addInt
name|void
name|addInt
parameter_list|(
name|int
name|nextInt
parameter_list|)
block|{
name|int
name|diff
init|=
name|nextInt
operator|-
name|lastInt
decl_stmt|;
if|if
condition|(
name|diff
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Input not sorted or first element negative."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|lastBytePos
operator|+
name|MAX_BYTES_PER_INT
operator|)
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
comment|// Biggest possible int does not fit.
name|resizeBytes
argument_list|(
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|lastBytePos
operator|+
name|MAX_BYTES_PER_INT
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// See org.apache.lucene.store.IndexOutput.writeVInt()
while|while
condition|(
operator|(
name|diff
operator|&
operator|~
name|VB1
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// The high bit of the next byte needs to be set.
name|bytes
index|[
name|lastBytePos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|diff
operator|&
name|VB1
operator|)
operator||
operator|~
name|VB1
argument_list|)
expr_stmt|;
name|diff
operator|>>>=
name|BIT_SHIFT
expr_stmt|;
block|}
name|bytes
index|[
name|lastBytePos
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|diff
expr_stmt|;
comment|// Last byte, high bit not set.
name|size
operator|++
expr_stmt|;
name|lastInt
operator|=
name|nextInt
expr_stmt|;
block|}
DECL|method|done
name|void
name|done
parameter_list|()
block|{
name|resizeBytes
argument_list|(
name|lastBytePos
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initBytes
specifier|private
name|void
name|initBytes
parameter_list|()
block|{
name|size
operator|=
literal|0
expr_stmt|;
name|bytes
operator|=
operator|new
name|byte
index|[
literal|128
index|]
expr_stmt|;
comment|// initial byte size
name|lastBytePos
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|resizeBytes
specifier|private
name|void
name|resizeBytes
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
if|if
condition|(
name|newSize
operator|!=
name|bytes
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|newBytes
init|=
operator|new
name|byte
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|newBytes
argument_list|,
literal|0
argument_list|,
name|lastBytePos
argument_list|)
expr_stmt|;
name|bytes
operator|=
name|newBytes
expr_stmt|;
block|}
block|}
DECL|field|VB1
specifier|private
specifier|static
specifier|final
name|int
name|VB1
init|=
literal|0x7F
decl_stmt|;
DECL|field|BIT_SHIFT
specifier|private
specifier|static
specifier|final
name|int
name|BIT_SHIFT
init|=
literal|7
decl_stmt|;
DECL|field|MAX_BYTES_PER_INT
specifier|private
specifier|final
name|int
name|MAX_BYTES_PER_INT
init|=
operator|(
literal|31
operator|/
name|BIT_SHIFT
operator|)
operator|+
literal|1
decl_stmt|;
comment|/**    * @return    The total number of sorted integers.    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * @return The size of the byte array storing the compressed sorted integers.    */
DECL|method|getByteSize
specifier|public
name|int
name|getByteSize
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|length
return|;
block|}
comment|/** This DocIdSet implementation is cacheable. */
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * @return    An iterator over the sorted integers.    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|DocIdSetIterator
argument_list|()
block|{
name|int
name|bytePos
init|=
literal|0
decl_stmt|;
name|int
name|lastInt
init|=
literal|0
decl_stmt|;
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|void
name|advance
parameter_list|()
block|{
comment|// See org.apache.lucene.store.IndexInput.readVInt()
name|byte
name|b
init|=
name|bytes
index|[
name|bytePos
operator|++
index|]
decl_stmt|;
name|lastInt
operator|+=
name|b
operator|&
name|VB1
expr_stmt|;
for|for
control|(
name|int
name|s
init|=
name|BIT_SHIFT
init|;
operator|(
name|b
operator|&
operator|~
name|VB1
operator|)
operator|!=
literal|0
condition|;
name|s
operator|+=
name|BIT_SHIFT
control|)
block|{
name|b
operator|=
name|bytes
index|[
name|bytePos
operator|++
index|]
expr_stmt|;
name|lastInt
operator|+=
operator|(
name|b
operator|&
name|VB1
operator|)
operator|<<
name|s
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
if|if
condition|(
name|bytePos
operator|>=
name|lastBytePos
condition|)
block|{
name|doc
operator|=
name|NO_MORE_DOCS
expr_stmt|;
block|}
else|else
block|{
name|advance
argument_list|()
expr_stmt|;
name|doc
operator|=
name|lastInt
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
while|while
condition|(
name|bytePos
operator|<
name|lastBytePos
condition|)
block|{
name|advance
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastInt
operator|>=
name|target
condition|)
block|{
return|return
name|doc
operator|=
name|lastInt
return|;
block|}
block|}
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

