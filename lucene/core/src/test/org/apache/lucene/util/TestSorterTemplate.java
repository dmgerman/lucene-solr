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
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestSorterTemplate
specifier|public
class|class
name|TestSorterTemplate
extends|extends
name|LuceneTestCase
block|{
DECL|field|SLOW_SORT_THRESHOLD
specifier|private
specifier|static
specifier|final
name|int
name|SLOW_SORT_THRESHOLD
init|=
literal|1000
decl_stmt|;
comment|// A sorter template that compares only the last 32 bits
DECL|class|Last32BitsSorterTemplate
specifier|static
class|class
name|Last32BitsSorterTemplate
extends|extends
name|SorterTemplate
block|{
DECL|field|arr
specifier|final
name|long
index|[]
name|arr
decl_stmt|;
DECL|field|pivot
name|long
name|pivot
decl_stmt|;
DECL|method|Last32BitsSorterTemplate
name|Last32BitsSorterTemplate
parameter_list|(
name|long
index|[]
name|arr
parameter_list|)
block|{
name|this
operator|.
name|arr
operator|=
name|arr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|swap
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
name|long
name|tmp
init|=
name|arr
index|[
name|i
index|]
decl_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|arr
index|[
name|j
index|]
expr_stmt|;
name|arr
index|[
name|j
index|]
operator|=
name|tmp
expr_stmt|;
block|}
DECL|method|compareValues
specifier|private
name|int
name|compareValues
parameter_list|(
name|long
name|i
parameter_list|,
name|long
name|j
parameter_list|)
block|{
comment|// only compare the last 32 bits
specifier|final
name|long
name|a
init|=
name|i
operator|&
literal|0xFFFFFFFFL
decl_stmt|;
specifier|final
name|long
name|b
init|=
name|j
operator|&
literal|0xFFFFFFFFL
decl_stmt|;
return|return
name|Long
operator|.
name|compare
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compare
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
return|return
name|compareValues
argument_list|(
name|arr
index|[
name|i
index|]
argument_list|,
name|arr
index|[
name|j
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setPivot
specifier|protected
name|void
name|setPivot
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|pivot
operator|=
name|arr
index|[
name|i
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|comparePivot
specifier|protected
name|int
name|comparePivot
parameter_list|(
name|int
name|j
parameter_list|)
block|{
return|return
name|compareValues
argument_list|(
name|pivot
argument_list|,
name|arr
index|[
name|j
index|]
argument_list|)
return|;
block|}
block|}
DECL|method|testSort
name|void
name|testSort
parameter_list|(
name|int
index|[]
name|intArr
parameter_list|)
block|{
comment|// we modify the array as a long[] and store the original ord in the first 32 bits
comment|// to be able to check stability
specifier|final
name|long
index|[]
name|arr
init|=
name|toLongsAndOrds
argument_list|(
name|intArr
argument_list|)
decl_stmt|;
comment|// use MergeSort as a reference
comment|// assertArrayEquals checks for sorting + stability
comment|// assertArrayEquals(toInts) checks for sorting only
specifier|final
name|long
index|[]
name|mergeSorted
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
operator|new
name|Last32BitsSorterTemplate
argument_list|(
name|mergeSorted
argument_list|)
operator|.
name|mergeSort
argument_list|(
literal|0
argument_list|,
name|arr
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|arr
operator|.
name|length
operator|<
name|SLOW_SORT_THRESHOLD
condition|)
block|{
specifier|final
name|long
index|[]
name|insertionSorted
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
operator|new
name|Last32BitsSorterTemplate
argument_list|(
name|insertionSorted
argument_list|)
operator|.
name|insertionSort
argument_list|(
literal|0
argument_list|,
name|arr
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|mergeSorted
argument_list|,
name|insertionSorted
argument_list|)
expr_stmt|;
specifier|final
name|long
index|[]
name|binarySorted
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
operator|new
name|Last32BitsSorterTemplate
argument_list|(
name|binarySorted
argument_list|)
operator|.
name|binarySort
argument_list|(
literal|0
argument_list|,
name|arr
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|mergeSorted
argument_list|,
name|binarySorted
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
index|[]
name|quickSorted
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
operator|new
name|Last32BitsSorterTemplate
argument_list|(
name|quickSorted
argument_list|)
operator|.
name|quickSort
argument_list|(
literal|0
argument_list|,
name|arr
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|toInts
argument_list|(
name|mergeSorted
argument_list|)
argument_list|,
name|toInts
argument_list|(
name|quickSorted
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|long
index|[]
name|timSorted
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|arr
argument_list|,
name|arr
operator|.
name|length
argument_list|)
decl_stmt|;
operator|new
name|Last32BitsSorterTemplate
argument_list|(
name|timSorted
argument_list|)
operator|.
name|timSort
argument_list|(
literal|0
argument_list|,
name|arr
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|mergeSorted
argument_list|,
name|timSorted
argument_list|)
expr_stmt|;
block|}
DECL|method|toInts
specifier|private
name|int
index|[]
name|toInts
parameter_list|(
name|long
index|[]
name|longArr
parameter_list|)
block|{
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|longArr
operator|.
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
name|longArr
operator|.
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
operator|(
name|int
operator|)
name|longArr
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
DECL|method|toLongsAndOrds
specifier|private
name|long
index|[]
name|toLongsAndOrds
parameter_list|(
name|int
index|[]
name|intArr
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|arr
init|=
operator|new
name|long
index|[
name|intArr
operator|.
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
name|intArr
operator|.
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
operator|(
operator|(
operator|(
name|long
operator|)
name|i
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|intArr
index|[
name|i
index|]
operator|&
literal|0xFFFFFFFFL
operator|)
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
DECL|method|randomLength
name|int
name|randomLength
parameter_list|()
block|{
return|return
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|SLOW_SORT_THRESHOLD
else|:
literal|100000
argument_list|)
return|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|testSort
argument_list|(
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testAscending
specifier|public
name|void
name|testAscending
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|randomLength
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|length
index|]
decl_stmt|;
name|arr
index|[
literal|0
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|arr
operator|.
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
name|arr
index|[
name|i
operator|-
literal|1
index|]
operator|+
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|testSort
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|testDescending
specifier|public
name|void
name|testDescending
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|randomLength
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|length
index|]
decl_stmt|;
name|arr
index|[
literal|0
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|arr
operator|.
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
name|arr
index|[
name|i
operator|-
literal|1
index|]
operator|-
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|testSort
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|testStrictlyDescending
specifier|public
name|void
name|testStrictlyDescending
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|randomLength
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|length
index|]
decl_stmt|;
name|arr
index|[
literal|0
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|arr
operator|.
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
name|arr
index|[
name|i
operator|-
literal|1
index|]
operator|-
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
name|testSort
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom1
specifier|public
name|void
name|testRandom1
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|randomLength
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|length
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
name|arr
operator|.
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
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
expr_stmt|;
block|}
name|testSort
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom2
specifier|public
name|void
name|testRandom2
parameter_list|()
block|{
specifier|final
name|int
name|length
init|=
name|randomLength
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|length
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
name|arr
operator|.
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
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|testSort
argument_list|(
name|arr
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

