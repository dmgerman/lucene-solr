begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
package|;
end_package

begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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

begin_class
DECL|class|TestTrieUtils
specifier|public
class|class
name|TestTrieUtils
extends|extends
name|LuceneTestCase
block|{
DECL|method|testSpecialValues
specifier|public
name|void
name|testSpecialValues
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Variant 8bit values
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|TRIE_CODED_NUMERIC_MIN
argument_list|,
literal|"\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|TRIE_CODED_NUMERIC_MAX
argument_list|,
literal|"\u01ff\u01ff\u01ff\u01ff\u01ff\u01ff\u01ff\u01ff"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|longToTrieCoded
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
literal|"\u017f\u01ff\u01ff\u01ff\u01ff\u01ff\u01ff\u01ff"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|longToTrieCoded
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"\u0180\u0100\u0100\u0100\u0100\u0100\u0100\u0100"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
operator|.
name|longToTrieCoded
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"\u0180\u0100\u0100\u0100\u0100\u0100\u0100\u0101"
argument_list|)
expr_stmt|;
comment|// Variant 4bit values
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|TRIE_CODED_NUMERIC_MIN
argument_list|,
literal|"\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|TRIE_CODED_NUMERIC_MAX
argument_list|,
literal|"\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|longToTrieCoded
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|,
literal|"\u0107\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f\u010f"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|longToTrieCoded
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"\u0108\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
operator|.
name|longToTrieCoded
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"\u0108\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0100\u0101"
argument_list|)
expr_stmt|;
comment|// TODO: 2bit tests
block|}
DECL|method|testBinaryOrderingAndIncrement
specifier|private
name|void
name|testBinaryOrderingAndIncrement
parameter_list|(
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
comment|// generate a series of encoded longs, each numerical one bigger than the one before
name|String
name|last
init|=
literal|null
decl_stmt|;
for|for
control|(
name|long
name|l
init|=
operator|-
literal|100000L
init|;
name|l
operator|<
literal|100000L
condition|;
name|l
operator|++
control|)
block|{
name|String
name|act
init|=
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|l
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
comment|// test if smaller
name|assertTrue
argument_list|(
name|last
operator|.
name|compareTo
argument_list|(
name|act
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// test the increment method (the last incremented by one should be the actual)
name|assertEquals
argument_list|(
name|variant
operator|.
name|incrementTrieCoded
argument_list|(
name|last
argument_list|)
argument_list|,
name|act
argument_list|)
expr_stmt|;
comment|// test the decrement method (the actual decremented by one should be the last)
name|assertEquals
argument_list|(
name|last
argument_list|,
name|variant
operator|.
name|decrementTrieCoded
argument_list|(
name|act
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// next step
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
DECL|method|testBinaryOrderingAndIncrement_8bit
specifier|public
name|void
name|testBinaryOrderingAndIncrement_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testBinaryOrderingAndIncrement
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testBinaryOrderingAndIncrement_4bit
specifier|public
name|void
name|testBinaryOrderingAndIncrement_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testBinaryOrderingAndIncrement
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testBinaryOrderingAndIncrement_2bit
specifier|public
name|void
name|testBinaryOrderingAndIncrement_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testBinaryOrderingAndIncrement
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongs
specifier|private
name|void
name|testLongs
parameter_list|(
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|long
index|[]
name|vals
init|=
operator|new
name|long
index|[]
block|{
name|Long
operator|.
name|MIN_VALUE
block|,
operator|-
literal|5000L
block|,
operator|-
literal|4000L
block|,
operator|-
literal|3000L
block|,
operator|-
literal|2000L
block|,
operator|-
literal|1000L
block|,
literal|0L
block|,
literal|1L
block|,
literal|10L
block|,
literal|300L
block|,
literal|5000L
block|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|2
block|,
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|1
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
name|String
index|[]
name|trieVals
init|=
operator|new
name|String
index|[
name|vals
operator|.
name|length
index|]
decl_stmt|;
comment|// check back and forth conversion
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|trieVals
index|[
name|i
index|]
operator|=
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Back and forth conversion should return same value"
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|,
name|variant
operator|.
name|trieCodedToLong
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Automatic back conversion with encoding detection should return same value"
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|,
name|TrieUtils
operator|.
name|trieCodedToLongAuto
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check sort order (trieVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|trieVals
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|compareTo
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLongs_8bit
specifier|public
name|void
name|testLongs_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testLongs
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongs_4bit
specifier|public
name|void
name|testLongs_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testLongs
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongs_2bit
specifier|public
name|void
name|testLongs_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testLongs
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubles
specifier|private
name|void
name|testDoubles
parameter_list|(
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|double
index|[]
name|vals
init|=
operator|new
name|double
index|[]
block|{
name|Double
operator|.
name|NEGATIVE_INFINITY
block|,
operator|-
literal|2.3E25
block|,
operator|-
literal|1.0E15
block|,
operator|-
literal|1.0
block|,
operator|-
literal|1.0E
operator|-
literal|1
block|,
operator|-
literal|1.0E
operator|-
literal|2
block|,
operator|-
literal|0.0
block|,
operator|+
literal|0.0
block|,
literal|1.0E
operator|-
literal|2
block|,
literal|1.0E
operator|-
literal|1
block|,
literal|1.0
block|,
literal|1.0E15
block|,
literal|2.3E25
block|,
name|Double
operator|.
name|POSITIVE_INFINITY
block|}
decl_stmt|;
name|String
index|[]
name|trieVals
init|=
operator|new
name|String
index|[
name|vals
operator|.
name|length
index|]
decl_stmt|;
comment|// check back and forth conversion
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|trieVals
index|[
name|i
index|]
operator|=
name|variant
operator|.
name|doubleToTrieCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Back and forth conversion should return same value"
argument_list|,
name|vals
index|[
name|i
index|]
operator|==
name|variant
operator|.
name|trieCodedToDouble
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Automatic back conversion with encoding detection should return same value"
argument_list|,
name|vals
index|[
name|i
index|]
operator|==
name|TrieUtils
operator|.
name|trieCodedToDoubleAuto
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check sort order (trieVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|trieVals
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|compareTo
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDoubles_8bit
specifier|public
name|void
name|testDoubles_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testDoubles
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubles_4bit
specifier|public
name|void
name|testDoubles_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testDoubles
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoubles_2bit
specifier|public
name|void
name|testDoubles_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testDoubles
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testDates
specifier|private
name|void
name|testDates
parameter_list|(
name|TrieUtils
name|variant
parameter_list|)
throws|throws
name|Exception
block|{
name|Date
index|[]
name|vals
init|=
operator|new
name|Date
index|[]
block|{
operator|new
name|GregorianCalendar
argument_list|(
literal|1000
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|getTime
argument_list|()
block|,
operator|new
name|GregorianCalendar
argument_list|(
literal|1999
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|getTime
argument_list|()
block|,
operator|new
name|GregorianCalendar
argument_list|(
literal|2000
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|getTime
argument_list|()
block|,
operator|new
name|GregorianCalendar
argument_list|(
literal|2001
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
operator|.
name|getTime
argument_list|()
block|}
decl_stmt|;
name|String
index|[]
name|trieVals
init|=
operator|new
name|String
index|[
name|vals
operator|.
name|length
index|]
decl_stmt|;
comment|// check back and forth conversion
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|trieVals
index|[
name|i
index|]
operator|=
name|variant
operator|.
name|dateToTrieCoded
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Back and forth conversion should return same value"
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|,
name|variant
operator|.
name|trieCodedToDate
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Automatic back conversion with encoding detection should return same value"
argument_list|,
name|vals
index|[
name|i
index|]
argument_list|,
name|TrieUtils
operator|.
name|trieCodedToDateAuto
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check sort order (trieVals should be ascending)
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|vals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|trieVals
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|compareTo
argument_list|(
name|trieVals
index|[
name|i
index|]
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDates_8bit
specifier|public
name|void
name|testDates_8bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testDates
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_8BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testDates_4bit
specifier|public
name|void
name|testDates_4bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testDates
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_4BIT
argument_list|)
expr_stmt|;
block|}
DECL|method|testDates_2bit
specifier|public
name|void
name|testDates_2bit
parameter_list|()
throws|throws
name|Exception
block|{
name|testDates
argument_list|(
name|TrieUtils
operator|.
name|VARIANT_2BIT
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

