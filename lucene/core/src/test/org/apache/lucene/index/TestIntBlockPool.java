begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|IntBlockPool
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
name|LuceneTestCase
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
comment|/**  * tests basic {@link IntBlockPool} functionality  */
end_comment

begin_class
DECL|class|TestIntBlockPool
specifier|public
class|class
name|TestIntBlockPool
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSingleWriterReader
specifier|public
name|void
name|testSingleWriterReader
parameter_list|()
block|{
name|Counter
name|bytesUsed
init|=
name|Counter
operator|.
name|newCounter
argument_list|()
decl_stmt|;
name|IntBlockPool
name|pool
init|=
operator|new
name|IntBlockPool
argument_list|(
operator|new
name|ByteTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|IntBlockPool
operator|.
name|SliceWriter
name|writer
init|=
operator|new
name|IntBlockPool
operator|.
name|SliceWriter
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|writer
operator|.
name|startNewSlice
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|writeInt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|upto
init|=
name|writer
operator|.
name|getCurrentOffset
argument_list|()
decl_stmt|;
name|IntBlockPool
operator|.
name|SliceReader
name|reader
init|=
operator|new
name|IntBlockPool
operator|.
name|SliceReader
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|reader
operator|.
name|reset
argument_list|(
name|start
argument_list|,
name|upto
argument_list|)
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|i
argument_list|,
name|reader
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|reader
operator|.
name|endOfSlice
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|pool
operator|.
name|reset
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pool
operator|.
name|reset
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IntBlockPool
operator|.
name|INT_BLOCK_SIZE
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|,
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMultipleWriterReader
specifier|public
name|void
name|testMultipleWriterReader
parameter_list|()
block|{
name|Counter
name|bytesUsed
init|=
name|Counter
operator|.
name|newCounter
argument_list|()
decl_stmt|;
name|IntBlockPool
name|pool
init|=
operator|new
name|IntBlockPool
argument_list|(
operator|new
name|ByteTrackingAllocator
argument_list|(
name|bytesUsed
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|List
argument_list|<
name|StartEndAndValues
argument_list|>
name|holders
init|=
operator|new
name|ArrayList
argument_list|<
name|TestIntBlockPool
operator|.
name|StartEndAndValues
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|4
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|holders
operator|.
name|add
argument_list|(
operator|new
name|StartEndAndValues
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|IntBlockPool
operator|.
name|SliceWriter
name|writer
init|=
operator|new
name|IntBlockPool
operator|.
name|SliceWriter
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|IntBlockPool
operator|.
name|SliceReader
name|reader
init|=
operator|new
name|IntBlockPool
operator|.
name|SliceReader
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|int
name|numValuesToWrite
init|=
name|atLeast
argument_list|(
literal|10000
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
name|numValuesToWrite
condition|;
name|i
operator|++
control|)
block|{
name|StartEndAndValues
name|values
init|=
name|holders
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|holders
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|valueCount
operator|==
literal|0
condition|)
block|{
name|values
operator|.
name|start
operator|=
name|writer
operator|.
name|startNewSlice
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|reset
argument_list|(
name|values
operator|.
name|end
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|writeInt
argument_list|(
name|values
operator|.
name|nextValue
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|.
name|end
operator|=
name|writer
operator|.
name|getCurrentOffset
argument_list|()
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// pick one and reader the ints
name|assertReader
argument_list|(
name|reader
argument_list|,
name|holders
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|holders
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
operator|!
name|holders
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StartEndAndValues
name|values
init|=
name|holders
operator|.
name|remove
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|holders
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertReader
argument_list|(
name|reader
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|pool
operator|.
name|reset
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pool
operator|.
name|reset
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|IntBlockPool
operator|.
name|INT_BLOCK_SIZE
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|,
name|bytesUsed
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ByteTrackingAllocator
specifier|private
specifier|static
class|class
name|ByteTrackingAllocator
extends|extends
name|IntBlockPool
operator|.
name|Allocator
block|{
DECL|field|bytesUsed
specifier|private
specifier|final
name|Counter
name|bytesUsed
decl_stmt|;
DECL|method|ByteTrackingAllocator
specifier|public
name|ByteTrackingAllocator
parameter_list|(
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|this
argument_list|(
name|IntBlockPool
operator|.
name|INT_BLOCK_SIZE
argument_list|,
name|bytesUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteTrackingAllocator
specifier|public
name|ByteTrackingAllocator
parameter_list|(
name|int
name|blockSize
parameter_list|,
name|Counter
name|bytesUsed
parameter_list|)
block|{
name|super
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|bytesUsed
operator|=
name|bytesUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getIntBlock
specifier|public
name|int
index|[]
name|getIntBlock
parameter_list|()
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
name|blockSize
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
argument_list|)
expr_stmt|;
return|return
operator|new
name|int
index|[
name|blockSize
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|recycleIntBlocks
specifier|public
name|void
name|recycleIntBlocks
parameter_list|(
name|int
index|[]
index|[]
name|blocks
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|bytesUsed
operator|.
name|addAndGet
argument_list|(
operator|-
operator|(
operator|(
name|end
operator|-
name|start
operator|)
operator|*
name|blockSize
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertReader
specifier|private
name|void
name|assertReader
parameter_list|(
name|IntBlockPool
operator|.
name|SliceReader
name|reader
parameter_list|,
name|StartEndAndValues
name|values
parameter_list|)
block|{
name|reader
operator|.
name|reset
argument_list|(
name|values
operator|.
name|start
argument_list|,
name|values
operator|.
name|end
argument_list|)
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
name|values
operator|.
name|valueCount
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|values
operator|.
name|valueOffset
operator|+
name|i
argument_list|,
name|reader
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|reader
operator|.
name|endOfSlice
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|StartEndAndValues
specifier|private
specifier|static
class|class
name|StartEndAndValues
block|{
DECL|field|valueOffset
name|int
name|valueOffset
decl_stmt|;
DECL|field|valueCount
name|int
name|valueCount
decl_stmt|;
DECL|field|start
name|int
name|start
decl_stmt|;
DECL|field|end
name|int
name|end
decl_stmt|;
DECL|method|StartEndAndValues
specifier|public
name|StartEndAndValues
parameter_list|(
name|int
name|valueOffset
parameter_list|)
block|{
name|this
operator|.
name|valueOffset
operator|=
name|valueOffset
expr_stmt|;
block|}
DECL|method|nextValue
specifier|public
name|int
name|nextValue
parameter_list|()
block|{
return|return
name|valueOffset
operator|+
name|valueCount
operator|++
return|;
block|}
block|}
block|}
end_class

end_unit

