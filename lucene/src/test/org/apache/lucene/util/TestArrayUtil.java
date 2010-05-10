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
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|TestArrayUtil
specifier|public
class|class
name|TestArrayUtil
extends|extends
name|LuceneTestCase
block|{
comment|// Ensure ArrayUtil.getNextSize gives linear amortized cost of realloc/copy
DECL|method|testGrowth
specifier|public
name|void
name|testGrowth
parameter_list|()
block|{
name|int
name|currentSize
init|=
literal|0
decl_stmt|;
name|long
name|copyCost
init|=
literal|0
decl_stmt|;
comment|// Make sure ArrayUtil hits Integer.MAX_VALUE, if we insist:
while|while
condition|(
name|currentSize
operator|!=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|int
name|nextSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
literal|1
operator|+
name|currentSize
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nextSize
operator|>
name|currentSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentSize
operator|>
literal|0
condition|)
block|{
name|copyCost
operator|+=
name|currentSize
expr_stmt|;
name|double
name|copyCostPerElement
init|=
operator|(
operator|(
name|double
operator|)
name|copyCost
operator|)
operator|/
name|currentSize
decl_stmt|;
name|assertTrue
argument_list|(
literal|"cost "
operator|+
name|copyCostPerElement
argument_list|,
name|copyCostPerElement
operator|<
literal|10.0
argument_list|)
expr_stmt|;
block|}
name|currentSize
operator|=
name|nextSize
expr_stmt|;
block|}
block|}
DECL|method|testMaxSize
specifier|public
name|void
name|testMaxSize
parameter_list|()
block|{
comment|// intentionally pass invalid elemSizes:
for|for
control|(
name|int
name|elemSize
init|=
literal|0
init|;
name|elemSize
operator|<
literal|10
condition|;
name|elemSize
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|elemSize
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|,
name|elemSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testInvalidElementSizes
specifier|public
name|void
name|testInvalidElementSizes
parameter_list|()
block|{
specifier|final
name|Random
name|r
init|=
name|newRandom
argument_list|()
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
literal|10000
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|minTargetSize
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|int
name|elemSize
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|11
argument_list|)
decl_stmt|;
specifier|final
name|int
name|v
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|minTargetSize
argument_list|,
name|elemSize
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|v
operator|>=
name|minTargetSize
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testParseInt
specifier|public
name|void
name|testParseInt
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|test
decl_stmt|;
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|""
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"foo"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"0.34"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|//expected
block|}
try|try
block|{
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|test
operator|==
literal|1
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"-10000"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
operator|-
literal|10000
argument_list|,
name|test
operator|==
operator|-
literal|10000
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"1923"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1923
argument_list|,
name|test
operator|==
literal|1923
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"-1"
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
operator|-
literal|1
argument_list|,
name|test
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|test
operator|=
name|ArrayUtil
operator|.
name|parseInt
argument_list|(
literal|"foo 1923 bar"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|test
operator|+
literal|" does not equal: "
operator|+
literal|1923
argument_list|,
name|test
operator|==
literal|1923
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSliceEquals
specifier|public
name|void
name|testSliceEquals
parameter_list|()
block|{
name|String
name|left
init|=
literal|"this is equal"
decl_stmt|;
name|String
name|right
init|=
name|left
decl_stmt|;
name|char
index|[]
name|leftChars
init|=
name|left
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|char
index|[]
name|rightChars
init|=
name|right
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|0
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|1
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|1
argument_list|,
name|rightChars
argument_list|,
literal|2
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|25
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|left
operator|+
literal|" does not equal: "
operator|+
name|right
argument_list|,
name|ArrayUtil
operator|.
name|equals
argument_list|(
name|leftChars
argument_list|,
literal|12
argument_list|,
name|rightChars
argument_list|,
literal|0
argument_list|,
name|left
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

