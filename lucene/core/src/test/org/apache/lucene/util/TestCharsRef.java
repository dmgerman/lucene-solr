begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
DECL|class|TestCharsRef
specifier|public
class|class
name|TestCharsRef
extends|extends
name|LuceneTestCase
block|{
DECL|method|testUTF16InUTF8Order
specifier|public
name|void
name|testUTF16InUTF8Order
parameter_list|()
block|{
specifier|final
name|int
name|numStrings
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|BytesRef
name|utf8
index|[]
init|=
operator|new
name|BytesRef
index|[
name|numStrings
index|]
decl_stmt|;
name|CharsRef
name|utf16
index|[]
init|=
operator|new
name|CharsRef
index|[
name|numStrings
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
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|utf8
index|[
name|i
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|utf16
index|[
name|i
index|]
operator|=
operator|new
name|CharsRef
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|utf16
argument_list|,
name|CharsRef
operator|.
name|getUTF16SortedAsUTF8Comparator
argument_list|()
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
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|utf8
index|[
name|i
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|utf16
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testAppend
specifier|public
name|void
name|testAppend
parameter_list|()
block|{
name|CharsRefBuilder
name|ref
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|numStrings
init|=
name|atLeast
argument_list|(
literal|10
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
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|char
index|[]
name|charArray
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|charArray
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|charArray
operator|.
name|length
operator|-
name|offset
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|charArray
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|ref
operator|.
name|append
argument_list|(
name|charArray
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
name|ref
operator|.
name|get
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCopy
specifier|public
name|void
name|testCopy
parameter_list|()
block|{
name|int
name|numIters
init|=
name|atLeast
argument_list|(
literal|10
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
name|numIters
condition|;
name|i
operator|++
control|)
block|{
name|CharsRefBuilder
name|ref
init|=
operator|new
name|CharsRefBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|charArray
init|=
name|TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|100
argument_list|)
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|charArray
operator|.
name|length
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|charArray
operator|.
name|length
operator|-
name|offset
decl_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|charArray
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
decl_stmt|;
name|ref
operator|.
name|copyChars
argument_list|(
name|charArray
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|str
argument_list|,
name|ref
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// LUCENE-3590: fix charsequence to fully obey interface
DECL|method|testCharSequenceCharAt
specifier|public
name|void
name|testCharSequenceCharAt
parameter_list|()
block|{
name|CharsRef
name|c
init|=
operator|new
name|CharsRef
argument_list|(
literal|"abc"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|'b'
argument_list|,
name|c
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|c
operator|.
name|charAt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|c
operator|.
name|charAt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3590: fix off-by-one in subsequence, and fully obey interface
comment|// LUCENE-4671: fix subSequence
DECL|method|testCharSequenceSubSequence
specifier|public
name|void
name|testCharSequenceSubSequence
parameter_list|()
block|{
name|CharSequence
name|sequences
index|[]
init|=
block|{
operator|new
name|CharsRef
argument_list|(
literal|"abc"
argument_list|)
block|,
operator|new
name|CharsRef
argument_list|(
literal|"0abc"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|CharsRef
argument_list|(
literal|"abc0"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|CharsRef
argument_list|(
literal|"0abc0"
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|CharSequence
name|c
range|:
name|sequences
control|)
block|{
name|doTestSequence
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doTestSequence
specifier|private
name|void
name|doTestSequence
parameter_list|(
name|CharSequence
name|c
parameter_list|)
block|{
comment|// slice
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|c
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// mid subsequence
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|c
operator|.
name|subSequence
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// end subsequence
name|assertEquals
argument_list|(
literal|"bc"
argument_list|,
name|c
operator|.
name|subSequence
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// empty subsequence
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|c
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|c
operator|.
name|subSequence
argument_list|(
operator|-
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|c
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|c
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IndexOutOfBoundsException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|c
operator|.
name|subSequence
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

