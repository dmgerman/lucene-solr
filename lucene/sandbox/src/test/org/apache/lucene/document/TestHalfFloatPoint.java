begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|RandomIndexWriter
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
name|IndexSearcher
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
name|store
operator|.
name|Directory
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
name|StringHelper
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestHalfFloatPoint
specifier|public
class|class
name|TestHalfFloatPoint
extends|extends
name|LuceneTestCase
block|{
DECL|method|testHalfFloat
specifier|private
name|void
name|testHalfFloat
parameter_list|(
name|String
name|sbits
parameter_list|,
name|float
name|value
parameter_list|)
block|{
name|short
name|bits
init|=
operator|(
name|short
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|sbits
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|float
name|converted
init|=
name|HalfFloatPoint
operator|.
name|shortBitsToHalfFloat
argument_list|(
name|bits
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|converted
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|short
name|bits2
init|=
name|HalfFloatPoint
operator|.
name|halfFloatToShortBits
argument_list|(
name|converted
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bits
argument_list|,
name|bits2
argument_list|)
expr_stmt|;
block|}
DECL|method|testHalfFloatConversion
specifier|public
name|void
name|testHalfFloatConversion
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|HalfFloatPoint
operator|.
name|halfFloatToShortBits
argument_list|(
literal|0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
call|(
name|short
call|)
argument_list|(
literal|1
operator|<<
literal|15
argument_list|)
argument_list|,
name|HalfFloatPoint
operator|.
name|halfFloatToShortBits
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|HalfFloatPoint
operator|.
name|halfFloatToShortBits
argument_list|(
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
expr_stmt|;
comment|// rounded to zero
name|testHalfFloat
argument_list|(
literal|"0011110000000000"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"0011110000000001"
argument_list|,
literal|1.0009765625f
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"1100000000000000"
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"0111101111111111"
argument_list|,
literal|65504
argument_list|)
expr_stmt|;
comment|// max value
name|testHalfFloat
argument_list|(
literal|"0000010000000000"
argument_list|,
operator|(
name|float
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
operator|-
literal|14
argument_list|)
argument_list|)
expr_stmt|;
comment|// minimum positive normal
name|testHalfFloat
argument_list|(
literal|"0000001111111111"
argument_list|,
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
operator|-
literal|14
argument_list|)
operator|-
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
operator|-
literal|24
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// maximum subnormal
name|testHalfFloat
argument_list|(
literal|"0000000000000001"
argument_list|,
operator|(
name|float
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
operator|-
literal|24
argument_list|)
argument_list|)
expr_stmt|;
comment|// minimum positive subnormal
name|testHalfFloat
argument_list|(
literal|"0000000000000000"
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"1000000000000000"
argument_list|,
operator|-
literal|0f
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"0111110000000000"
argument_list|,
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"1111110000000000"
argument_list|,
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"0111111000000000"
argument_list|,
name|Float
operator|.
name|NaN
argument_list|)
expr_stmt|;
name|testHalfFloat
argument_list|(
literal|"0011010101010101"
argument_list|,
literal|0.333251953125f
argument_list|)
expr_stmt|;
block|}
DECL|method|testRoundShift
specifier|public
name|void
name|testRoundShift
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// tie so round to 0 since it ends with a 0
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|3
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|4
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|5
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|6
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// tie so round to 2 since it ends with a 0
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|7
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|8
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|9
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|10
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// tie so round to 2 since it ends with a 0
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|11
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|12
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|13
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|14
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// tie so round to 4 since it ends with a 0
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|15
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|HalfFloatPoint
operator|.
name|roundShift
argument_list|(
literal|16
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRounding
specifier|public
name|void
name|testRounding
parameter_list|()
block|{
name|float
index|[]
name|values
init|=
operator|new
name|float
index|[
literal|0
index|]
decl_stmt|;
name|int
name|o
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
init|;
name|i
operator|<=
name|Short
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|float
name|v
init|=
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
operator|(
name|short
operator|)
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|isFinite
argument_list|(
name|v
argument_list|)
condition|)
block|{
if|if
condition|(
name|o
operator|==
name|values
operator|.
name|length
condition|)
block|{
name|values
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
name|values
index|[
name|o
operator|++
index|]
operator|=
name|v
expr_stmt|;
block|}
block|}
name|values
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|values
argument_list|,
name|o
argument_list|)
expr_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
operator|++
name|iter
control|)
block|{
name|float
name|f
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|int
name|floatBits
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|f
operator|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|floatBits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
call|(
name|float
call|)
argument_list|(
operator|(
literal|2
operator|*
name|random
argument_list|()
operator|.
name|nextFloat
argument_list|()
operator|-
literal|1
operator|)
operator|*
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
operator|-
literal|16
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|float
name|rounded
init|=
name|HalfFloatPoint
operator|.
name|shortBitsToHalfFloat
argument_list|(
name|HalfFloatPoint
operator|.
name|halfFloatToShortBits
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|Float
operator|.
name|isFinite
argument_list|(
name|f
argument_list|)
operator|==
literal|false
condition|)
block|{
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|rounded
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Float
operator|.
name|isFinite
argument_list|(
name|rounded
argument_list|)
operator|==
literal|false
condition|)
block|{
name|assertFalse
argument_list|(
name|Float
operator|.
name|isNaN
argument_list|(
name|rounded
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|f
argument_list|)
operator|>=
literal|65520
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|index
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|values
argument_list|,
name|f
argument_list|)
decl_stmt|;
name|float
name|closest
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|closest
operator|=
name|values
index|[
name|index
index|]
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
operator|-
literal|1
operator|-
name|index
expr_stmt|;
name|closest
operator|=
name|Float
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
if|if
condition|(
name|index
operator|<
name|values
operator|.
name|length
condition|)
block|{
name|closest
operator|=
name|values
index|[
name|index
index|]
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|-
literal|1
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|f
operator|-
name|values
index|[
name|index
operator|-
literal|1
index|]
operator|<
name|closest
operator|-
name|f
condition|)
block|{
name|closest
operator|=
name|values
index|[
name|index
operator|-
literal|1
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|f
operator|-
name|values
index|[
name|index
operator|-
literal|1
index|]
operator|==
name|closest
operator|-
name|f
operator|&&
name|Integer
operator|.
name|numberOfTrailingZeros
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|values
index|[
name|index
operator|-
literal|1
index|]
argument_list|)
argument_list|)
operator|>
name|Integer
operator|.
name|numberOfTrailingZeros
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|closest
argument_list|)
argument_list|)
condition|)
block|{
comment|// in case of tie, round to even
name|closest
operator|=
name|values
index|[
name|index
operator|-
literal|1
index|]
expr_stmt|;
block|}
block|}
block|}
name|assertEquals
argument_list|(
name|closest
argument_list|,
name|rounded
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testSortableBits
specifier|public
name|void
name|testSortableBits
parameter_list|()
block|{
name|int
name|low
init|=
name|Short
operator|.
name|MIN_VALUE
decl_stmt|;
name|int
name|high
init|=
name|Short
operator|.
name|MAX_VALUE
decl_stmt|;
while|while
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
operator|(
name|short
operator|)
name|low
argument_list|)
argument_list|)
condition|)
block|{
operator|++
name|low
expr_stmt|;
block|}
while|while
condition|(
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
operator|(
name|short
operator|)
name|low
argument_list|)
operator|==
name|Float
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
operator|++
name|low
expr_stmt|;
block|}
while|while
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
operator|(
name|short
operator|)
name|high
argument_list|)
argument_list|)
condition|)
block|{
operator|--
name|high
expr_stmt|;
block|}
while|while
condition|(
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
operator|(
name|short
operator|)
name|high
argument_list|)
operator|==
name|Float
operator|.
name|POSITIVE_INFINITY
condition|)
block|{
operator|--
name|high
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|low
init|;
name|i
operator|<=
name|high
operator|+
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|float
name|previous
init|=
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
call|(
name|short
call|)
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|float
name|current
init|=
name|HalfFloatPoint
operator|.
name|sortableShortToHalfFloat
argument_list|(
operator|(
name|short
operator|)
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|HalfFloatPoint
operator|.
name|halfFloatToSortableShort
argument_list|(
name|current
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Float
operator|.
name|compare
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSortableBytes
specifier|public
name|void
name|testSortableBytes
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
name|Short
operator|.
name|MIN_VALUE
operator|+
literal|1
init|;
name|i
operator|<=
name|Short
operator|.
name|MAX_VALUE
condition|;
operator|++
name|i
control|)
block|{
name|byte
index|[]
name|previous
init|=
operator|new
name|byte
index|[
name|HalfFloatPoint
operator|.
name|BYTES
index|]
decl_stmt|;
name|HalfFloatPoint
operator|.
name|shortToSortableBytes
argument_list|(
call|(
name|short
call|)
argument_list|(
name|i
operator|-
literal|1
argument_list|)
argument_list|,
name|previous
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|byte
index|[]
name|current
init|=
operator|new
name|byte
index|[
name|HalfFloatPoint
operator|.
name|BYTES
index|]
decl_stmt|;
name|HalfFloatPoint
operator|.
name|shortToSortableBytes
argument_list|(
operator|(
name|short
operator|)
name|i
argument_list|,
name|current
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|StringHelper
operator|.
name|compare
argument_list|(
name|HalfFloatPoint
operator|.
name|BYTES
argument_list|,
name|previous
argument_list|,
literal|0
argument_list|,
name|current
argument_list|,
literal|0
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|i
argument_list|,
name|HalfFloatPoint
operator|.
name|sortableBytesToShort
argument_list|(
name|current
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Add a single value and search for it */
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with an single dimension
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|HalfFloatPoint
argument_list|(
literal|"field"
argument_list|,
literal|1.25f
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
literal|1.25f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
literal|1f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newExactQuery
argument_list|(
literal|"field"
argument_list|,
literal|2f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
literal|1f
argument_list|,
literal|2f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
literal|0f
argument_list|,
literal|1f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
literal|1.5f
argument_list|,
literal|2f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
literal|1.25f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
literal|1f
argument_list|,
literal|1.25f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|,
literal|1f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newSetQuery
argument_list|(
literal|"field"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Add a single multi-dimensional value and search for it */
DECL|method|testBasicsMultiDims
specifier|public
name|void
name|testBasicsMultiDims
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
comment|// add a doc with two dimensions
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|HalfFloatPoint
argument_list|(
literal|"field"
argument_list|,
literal|1.25f
argument_list|,
operator|-
literal|2f
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
comment|// search and verify we found our doc
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0
block|,
operator|-
literal|5
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1.25f
block|,
operator|-
literal|1
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
operator|new
name|float
index|[]
block|{
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|2
block|,
literal|2
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|count
argument_list|(
name|HalfFloatPoint
operator|.
name|newRangeQuery
argument_list|(
literal|"field"
argument_list|,
operator|new
name|float
index|[]
block|{
operator|-
literal|10
block|,
operator|-
literal|10
block|}
argument_list|,
operator|new
name|float
index|[]
block|{
literal|1
block|,
literal|2
block|}
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testNextUp
specifier|public
name|void
name|testNextUp
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|65504
argument_list|,
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HalfFloatPoint
operator|.
name|shortBitsToHalfFloat
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
argument_list|,
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HalfFloatPoint
operator|.
name|shortBitsToHalfFloat
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
argument_list|,
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
literal|0f
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
comment|// values that cannot be exactly represented as a half float
name|assertEquals
argument_list|(
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
literal|0f
argument_list|)
argument_list|,
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
operator|-
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
literal|0f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|HalfFloatPoint
operator|.
name|nextUp
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNextDown
specifier|public
name|void
name|testNextDown
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Float
operator|.
name|NaN
argument_list|,
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|,
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
name|Float
operator|.
name|NEGATIVE_INFINITY
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|65504
argument_list|,
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
name|Float
operator|.
name|POSITIVE_INFINITY
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
literal|0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// values that cannot be exactly represented as a half float
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
literal|0f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|,
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
operator|-
name|Float
operator|.
name|MIN_VALUE
argument_list|)
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|-
literal|0f
argument_list|)
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|HalfFloatPoint
operator|.
name|nextDown
argument_list|(
operator|+
literal|0f
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

