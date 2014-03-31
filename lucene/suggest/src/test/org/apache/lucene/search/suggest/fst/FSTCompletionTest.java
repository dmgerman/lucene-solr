begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
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
operator|.
name|fst
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
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
name|suggest
operator|.
name|*
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
name|suggest
operator|.
name|fst
operator|.
name|FSTCompletion
operator|.
name|Completion
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
name|*
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link FSTCompletion}.  */
end_comment

begin_class
DECL|class|FSTCompletionTest
specifier|public
class|class
name|FSTCompletionTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|tf
specifier|public
specifier|static
name|Input
name|tf
parameter_list|(
name|String
name|t
parameter_list|,
name|int
name|v
parameter_list|)
block|{
return|return
operator|new
name|Input
argument_list|(
name|t
argument_list|,
name|v
argument_list|)
return|;
block|}
DECL|field|completion
specifier|private
name|FSTCompletion
name|completion
decl_stmt|;
DECL|field|completionAlphabetical
specifier|private
name|FSTCompletion
name|completionAlphabetical
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|FSTCompletionBuilder
name|builder
init|=
operator|new
name|FSTCompletionBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Input
name|tf
range|:
name|evalKeys
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|tf
operator|.
name|term
argument_list|,
operator|(
name|int
operator|)
name|tf
operator|.
name|v
argument_list|)
expr_stmt|;
block|}
name|completion
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|completionAlphabetical
operator|=
operator|new
name|FSTCompletion
argument_list|(
name|completion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|evalKeys
specifier|private
name|Input
index|[]
name|evalKeys
parameter_list|()
block|{
specifier|final
name|Input
index|[]
name|keys
init|=
operator|new
name|Input
index|[]
block|{
name|tf
argument_list|(
literal|"one"
argument_list|,
literal|0
argument_list|)
block|,
name|tf
argument_list|(
literal|"oneness"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"onerous"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"onesimus"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"two"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"twofold"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"twonk"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"thrive"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"through"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"threat"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"three"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"foundation"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourblah"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourteen"
argument_list|,
literal|1
argument_list|)
block|,
name|tf
argument_list|(
literal|"four"
argument_list|,
literal|0
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourier"
argument_list|,
literal|0
argument_list|)
block|,
name|tf
argument_list|(
literal|"fourty"
argument_list|,
literal|0
argument_list|)
block|,
name|tf
argument_list|(
literal|"xo"
argument_list|,
literal|1
argument_list|)
block|,       }
decl_stmt|;
return|return
name|keys
return|;
block|}
DECL|method|testExactMatchHighPriority
specifier|public
name|void
name|testExactMatchHighPriority
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"two"
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"two/1.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExactMatchLowPriority
specifier|public
name|void
name|testExactMatchLowPriority
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"one"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExactMatchReordering
specifier|public
name|void
name|testExactMatchReordering
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Check reordering of exact matches.
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"four"
argument_list|)
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|"four/0.0"
argument_list|,
literal|"fourblah/1.0"
argument_list|,
literal|"fourteen/1.0"
argument_list|,
literal|"fourier/0.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRequestedCount
specifier|public
name|void
name|testRequestedCount
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 'one' is promoted after collecting two higher ranking results.
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"one"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
comment|// 'four' is collected in a bucket and then again as an exact match.
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"four"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"four/0.0"
argument_list|,
literal|"fourblah/1.0"
argument_list|)
expr_stmt|;
comment|// Check reordering of exact matches.
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"four"
argument_list|)
argument_list|,
literal|4
argument_list|)
argument_list|,
literal|"four/0.0"
argument_list|,
literal|"fourblah/1.0"
argument_list|,
literal|"fourteen/1.0"
argument_list|,
literal|"fourier/0.0"
argument_list|)
expr_stmt|;
comment|// 'one' is at the top after collecting all alphabetical results.
name|assertMatchEquals
argument_list|(
name|completionAlphabetical
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"one"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
comment|// 'one' is not promoted after collecting two higher ranking results.
name|FSTCompletion
name|noPromotion
init|=
operator|new
name|FSTCompletion
argument_list|(
name|completion
operator|.
name|getFST
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertMatchEquals
argument_list|(
name|noPromotion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"one"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"oneness/1.0"
argument_list|,
literal|"onerous/1.0"
argument_list|)
expr_stmt|;
comment|// 'one' is at the top after collecting all alphabetical results.
name|assertMatchEquals
argument_list|(
name|completionAlphabetical
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"one"
argument_list|)
argument_list|,
literal|2
argument_list|)
argument_list|,
literal|"one/0.0"
argument_list|,
literal|"oneness/1.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMiss
specifier|public
name|void
name|testMiss
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"xyz"
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAlphabeticWithWeights
specifier|public
name|void
name|testAlphabeticWithWeights
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|completionAlphabetical
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"xyz"
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullMatchList
specifier|public
name|void
name|testFullMatchList
parameter_list|()
throws|throws
name|Exception
block|{
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"one"
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|,
literal|"oneness/1.0"
argument_list|,
literal|"onerous/1.0"
argument_list|,
literal|"onesimus/1.0"
argument_list|,
literal|"one/0.0"
argument_list|)
expr_stmt|;
block|}
DECL|method|testThreeByte
specifier|public
name|void
name|testThreeByte
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|key
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xF0
block|,
operator|(
name|byte
operator|)
literal|0xA4
block|,
operator|(
name|byte
operator|)
literal|0xAD
block|,
operator|(
name|byte
operator|)
literal|0xA2
block|}
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|FSTCompletionBuilder
name|builder
init|=
operator|new
name|FSTCompletionBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|key
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|FSTCompletion
name|lookup
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Completion
argument_list|>
name|result
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
name|key
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testLargeInputConstantWeights
specifier|public
name|void
name|testLargeInputConstantWeights
parameter_list|()
throws|throws
name|Exception
block|{
name|FSTCompletionLookup
name|lookup
init|=
operator|new
name|FSTCompletionLookup
argument_list|(
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Input
argument_list|>
name|keys
init|=
operator|new
name|ArrayList
argument_list|<>
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
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|keys
operator|.
name|add
argument_list|(
operator|new
name|Input
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|r
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|keys
argument_list|)
argument_list|)
expr_stmt|;
comment|// All the weights were constant, so all returned buckets must be constant, whatever they
comment|// are.
name|Long
name|previous
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Input
name|tf
range|:
name|keys
control|)
block|{
name|Long
name|current
init|=
operator|(
operator|(
name|Number
operator|)
name|lookup
operator|.
name|get
argument_list|(
name|TestUtil
operator|.
name|bytesToCharSequence
argument_list|(
name|tf
operator|.
name|term
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|)
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|previous
argument_list|,
name|current
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|current
expr_stmt|;
block|}
block|}
DECL|method|testMultilingualInput
specifier|public
name|void
name|testMultilingualInput
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Input
argument_list|>
name|input
init|=
name|LookupBenchmarkTest
operator|.
name|readTop50KWiki
argument_list|()
decl_stmt|;
name|FSTCompletionLookup
name|lookup
init|=
operator|new
name|FSTCompletionLookup
argument_list|()
decl_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|input
operator|.
name|size
argument_list|()
argument_list|,
name|lookup
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Input
name|tf
range|:
name|input
control|)
block|{
name|assertNotNull
argument_list|(
literal|"Not found: "
operator|+
name|tf
operator|.
name|term
operator|.
name|toString
argument_list|()
argument_list|,
name|lookup
operator|.
name|get
argument_list|(
name|TestUtil
operator|.
name|bytesToCharSequence
argument_list|(
name|tf
operator|.
name|term
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tf
operator|.
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|lookup
operator|.
name|lookup
argument_list|(
name|TestUtil
operator|.
name|bytesToCharSequence
argument_list|(
name|tf
operator|.
name|term
argument_list|,
name|random
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|LookupResult
argument_list|>
name|result
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|"wit"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"wit"
argument_list|)
argument_list|)
expr_stmt|;
comment|// exact match.
name|assertTrue
argument_list|(
name|result
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"with"
argument_list|)
argument_list|)
expr_stmt|;
comment|// highest count.
block|}
DECL|method|testEmptyInput
specifier|public
name|void
name|testEmptyInput
parameter_list|()
throws|throws
name|Exception
block|{
name|completion
operator|=
operator|new
name|FSTCompletionBuilder
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertMatchEquals
argument_list|(
name|completion
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
literal|""
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Input
argument_list|>
name|freqs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Random
name|rnd
init|=
name|random
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
literal|2500
operator|+
name|rnd
operator|.
name|nextInt
argument_list|(
literal|2500
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|int
name|weight
init|=
name|rnd
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|freqs
operator|.
name|add
argument_list|(
operator|new
name|Input
argument_list|(
literal|""
operator|+
name|rnd
operator|.
name|nextLong
argument_list|()
argument_list|,
name|weight
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FSTCompletionLookup
name|lookup
init|=
operator|new
name|FSTCompletionLookup
argument_list|()
decl_stmt|;
name|lookup
operator|.
name|build
argument_list|(
operator|new
name|InputArrayIterator
argument_list|(
name|freqs
operator|.
name|toArray
argument_list|(
operator|new
name|Input
index|[
name|freqs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Input
name|tf
range|:
name|freqs
control|)
block|{
specifier|final
name|String
name|term
init|=
name|tf
operator|.
name|term
operator|.
name|utf8ToString
argument_list|()
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
name|term
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|prefix
init|=
name|term
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|LookupResult
name|lr
range|:
name|lookup
operator|.
name|lookup
argument_list|(
name|stringToCharSequence
argument_list|(
name|prefix
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|10
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|lr
operator|.
name|key
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|stringToCharSequence
specifier|private
name|CharSequence
name|stringToCharSequence
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|TestUtil
operator|.
name|stringToCharSequence
argument_list|(
name|prefix
argument_list|,
name|random
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertMatchEquals
specifier|private
name|void
name|assertMatchEquals
parameter_list|(
name|List
argument_list|<
name|Completion
argument_list|>
name|res
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|res
operator|.
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
name|res
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|res
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|stripScore
argument_list|(
name|expected
argument_list|)
argument_list|,
name|stripScore
argument_list|(
name|result
argument_list|)
argument_list|)
condition|)
block|{
name|int
name|colLen
init|=
name|Math
operator|.
name|max
argument_list|(
name|maxLen
argument_list|(
name|expected
argument_list|)
argument_list|,
name|maxLen
argument_list|(
name|result
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|format
init|=
literal|"%"
operator|+
name|colLen
operator|+
literal|"s  "
operator|+
literal|"%"
operator|+
name|colLen
operator|+
literal|"s\n"
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|format
argument_list|,
literal|"Expected"
argument_list|,
literal|"Result"
argument_list|)
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
name|Math
operator|.
name|max
argument_list|(
name|result
operator|.
name|length
argument_list|,
name|expected
operator|.
name|length
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|format
argument_list|,
name|i
operator|<
name|expected
operator|.
name|length
condition|?
name|expected
index|[
name|i
index|]
else|:
literal|"--"
argument_list|,
name|i
operator|<
name|result
operator|.
name|length
condition|?
name|result
index|[
name|i
index|]
else|:
literal|"--"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected different output:\n"
operator|+
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|stripScore
specifier|private
name|String
index|[]
name|stripScore
parameter_list|(
name|String
index|[]
name|expected
parameter_list|)
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|expected
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
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|expected
index|[
name|i
index|]
operator|.
name|replaceAll
argument_list|(
literal|"\\/[0-9\\.]+"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|maxLen
specifier|private
name|int
name|maxLen
parameter_list|(
name|String
index|[]
name|result
parameter_list|)
block|{
name|int
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|result
control|)
name|len
operator|=
name|Math
operator|.
name|max
argument_list|(
name|len
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

