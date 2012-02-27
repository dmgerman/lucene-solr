begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.pattern
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|pattern
package|;
end_package

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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|analysis
operator|.
name|CharReader
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
name|analysis
operator|.
name|CharStream
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
name|analysis
operator|.
name|MockTokenizer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Tests {@link PatternReplaceCharFilter}  */
end_comment

begin_class
DECL|class|TestPatternReplaceCharFilter
specifier|public
class|class
name|TestPatternReplaceCharFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testFailingDot
specifier|public
name|void
name|testFailingDot
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOutput
argument_list|(
literal|"A. .B."
argument_list|,
literal|"\\.[\\s]*"
argument_list|,
literal|"."
argument_list|,
literal|"A..B."
argument_list|,
literal|"A..B."
argument_list|)
expr_stmt|;
block|}
DECL|method|testLongerReplacement
specifier|public
name|void
name|testLongerReplacement
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOutput
argument_list|(
literal|"XXabcZZabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|"abcde"
argument_list|,
literal|"XXabcdeZZabcdeYY"
argument_list|,
literal|"XXabcccZZabcccYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"XXabcabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|"abcde"
argument_list|,
literal|"XXabcdeabcdeYY"
argument_list|,
literal|"XXabcccabcccYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"abcabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|"abcde"
argument_list|,
literal|"abcdeabcdeYY"
argument_list|,
literal|"abcccabcccYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"YY"
argument_list|,
literal|"^"
argument_list|,
literal|"abcde"
argument_list|,
literal|"abcdeYY"
argument_list|,
comment|// Should be: "-----YY" but we're enforcing non-negative offsets.
literal|"YYYYYYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"YY"
argument_list|,
literal|"$"
argument_list|,
literal|"abcde"
argument_list|,
literal|"YYabcde"
argument_list|,
literal|"YYYYYYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"XYZ"
argument_list|,
literal|"."
argument_list|,
literal|"abc"
argument_list|,
literal|"abcabcabc"
argument_list|,
literal|"XXXYYYZZZ"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"XYZ"
argument_list|,
literal|"."
argument_list|,
literal|"$0abc"
argument_list|,
literal|"XabcYabcZabc"
argument_list|,
literal|"XXXXYYYYZZZZ"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShorterReplacement
specifier|public
name|void
name|testShorterReplacement
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOutput
argument_list|(
literal|"XXabcZZabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|"xy"
argument_list|,
literal|"XXxyZZxyYY"
argument_list|,
literal|"XXabZZabYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"XXabcabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|"xy"
argument_list|,
literal|"XXxyxyYY"
argument_list|,
literal|"XXababYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"abcabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|"xy"
argument_list|,
literal|"xyxyYY"
argument_list|,
literal|"ababYY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"abcabcYY"
argument_list|,
literal|"abc"
argument_list|,
literal|""
argument_list|,
literal|"YY"
argument_list|,
literal|"YY"
argument_list|)
expr_stmt|;
name|checkOutput
argument_list|(
literal|"YYabcabc"
argument_list|,
literal|"abc"
argument_list|,
literal|""
argument_list|,
literal|"YY"
argument_list|,
literal|"YY"
argument_list|)
expr_stmt|;
block|}
DECL|method|checkOutput
specifier|private
name|void
name|checkOutput
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|pattern
parameter_list|,
name|String
name|replacement
parameter_list|,
name|String
name|expectedOutput
parameter_list|,
name|String
name|expectedIndexMatchedOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
name|pattern
argument_list|)
argument_list|,
name|replacement
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuilder
name|output
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|chr
init|=
name|cs
operator|.
name|read
argument_list|()
init|;
name|chr
operator|>
literal|0
condition|;
name|chr
operator|=
name|cs
operator|.
name|read
argument_list|()
control|)
block|{
name|output
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|chr
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|indexMatched
init|=
operator|new
name|StringBuilder
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
name|output
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|indexMatched
operator|.
name|append
argument_list|(
operator|(
name|cs
operator|.
name|correctOffset
argument_list|(
name|i
argument_list|)
operator|<
literal|0
condition|?
literal|"-"
else|:
name|input
operator|.
name|charAt
argument_list|(
name|cs
operator|.
name|correctOffset
argument_list|(
name|i
argument_list|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|outputGood
init|=
name|expectedOutput
operator|.
name|equals
argument_list|(
name|output
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|indexMatchedGood
init|=
name|expectedIndexMatchedOutput
operator|.
name|equals
argument_list|(
name|indexMatched
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|outputGood
operator|||
operator|!
name|indexMatchedGood
operator|||
literal|false
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Pattern : "
operator|+
name|pattern
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Replac. : "
operator|+
name|replacement
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Input   : "
operator|+
name|input
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Output  : "
operator|+
name|output
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Expected: "
operator|+
name|expectedOutput
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Output/i: "
operator|+
name|indexMatched
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Expected: "
operator|+
name|expectedIndexMatchedOutput
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Output doesn't match."
argument_list|,
name|outputGood
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Index-matched output doesn't match."
argument_list|,
name|indexMatchedGood
argument_list|)
expr_stmt|;
block|}
comment|//           1111
comment|// 01234567890123
comment|// this is test.
DECL|method|testNothingChange
specifier|public
name|void
name|testNothingChange
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"this is test."
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)\\s+(cc)"
argument_list|)
argument_list|,
literal|"$1$2$3"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"is"
block|,
literal|"test."
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|5
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|7
block|,
literal|13
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 012345678
comment|// aa bb cc
DECL|method|testReplaceByEmpty
specifier|public
name|void
name|testReplaceByEmpty
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"aa bb cc"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)\\s+(cc)"
argument_list|)
argument_list|,
literal|""
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
comment|// 012345678
comment|// aa bb cc
comment|// aa#bb#cc
DECL|method|test1block1matchSameLength
specifier|public
name|void
name|test1block1matchSameLength
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"aa bb cc"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)\\s+(cc)"
argument_list|)
argument_list|,
literal|"$1#$2#$3"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa#bb#cc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//           11111
comment|// 012345678901234
comment|// aa bb cc dd
comment|// aa##bb###cc dd
DECL|method|test1block1matchLonger
specifier|public
name|void
name|test1block1matchLonger
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"aa bb cc dd"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)\\s+(cc)"
argument_list|)
argument_list|,
literal|"$1##$2###$3"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa##bb###cc"
block|,
literal|"dd"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|9
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|,
literal|11
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// 01234567
comment|//  a  a
comment|//  aa  aa
DECL|method|test1block2matchLonger
specifier|public
name|void
name|test1block2matchLonger
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|" a  a"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"aa"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa"
block|,
literal|"aa"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|5
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//           11111
comment|// 012345678901234
comment|// aa  bb   cc dd
comment|// aa#bb dd
DECL|method|test1block1matchShorter
specifier|public
name|void
name|test1block1matchShorter
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"aa  bb   cc dd"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)\\s+(cc)"
argument_list|)
argument_list|,
literal|"$1#$2"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa#bb"
block|,
literal|"dd"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|12
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|11
block|,
literal|14
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//           111111111122222222223333
comment|// 0123456789012345678901234567890123
comment|//   aa bb cc --- aa bb aa   bb   cc
comment|//   aa  bb  cc --- aa bb aa  bb  cc
DECL|method|test1blockMultiMatches
specifier|public
name|void
name|test1blockMultiMatches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"  aa bb cc --- aa bb aa   bb   cc"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)\\s+(cc)"
argument_list|)
argument_list|,
literal|"$1  $2  $3"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa"
block|,
literal|"bb"
block|,
literal|"cc"
block|,
literal|"---"
block|,
literal|"aa"
block|,
literal|"bb"
block|,
literal|"aa"
block|,
literal|"bb"
block|,
literal|"cc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|6
block|,
literal|9
block|,
literal|11
block|,
literal|15
block|,
literal|18
block|,
literal|21
block|,
literal|25
block|,
literal|29
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|,
literal|8
block|,
literal|10
block|,
literal|14
block|,
literal|17
block|,
literal|20
block|,
literal|23
block|,
literal|27
block|,
literal|33
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//           11111111112222222222333333333
comment|// 012345678901234567890123456789012345678
comment|//   aa bb cc --- aa bb aa. bb aa   bb cc
comment|//   aa##bb cc --- aa##bb aa. bb aa##bb cc
comment|//   aa bb cc --- aa bbbaa. bb aa   b cc
DECL|method|test2blocksMultiMatches
specifier|public
name|void
name|test2blocksMultiMatches
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|"  aa bb cc --- aa bb aa. bb aa   bb cc"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"(aa)\\s+(bb)"
argument_list|)
argument_list|,
literal|"$1##$2"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa##bb"
block|,
literal|"cc"
block|,
literal|"---"
block|,
literal|"aa##bb"
block|,
literal|"aa."
block|,
literal|"bb"
block|,
literal|"aa##bb"
block|,
literal|"cc"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|8
block|,
literal|11
block|,
literal|15
block|,
literal|21
block|,
literal|25
block|,
literal|28
block|,
literal|36
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|10
block|,
literal|14
block|,
literal|20
block|,
literal|24
block|,
literal|27
block|,
literal|35
block|,
literal|38
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//           11111111112222222222333333333
comment|// 012345678901234567890123456789012345678
comment|//  a bb - ccc . --- bb a . ccc ccc bb
comment|//  aa b - c . --- b aa . c c b
DECL|method|testChain
specifier|public
name|void
name|testChain
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|BLOCK
init|=
literal|" a bb - ccc . --- bb a . ccc ccc bb"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"aa"
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|BLOCK
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|cs
operator|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"bb"
argument_list|)
argument_list|,
literal|"b"
argument_list|,
name|cs
argument_list|)
expr_stmt|;
name|cs
operator|=
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|pattern
argument_list|(
literal|"ccc"
argument_list|)
argument_list|,
literal|"c"
argument_list|,
name|cs
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|MockTokenizer
argument_list|(
name|cs
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aa"
block|,
literal|"b"
block|,
literal|"-"
block|,
literal|"c"
block|,
literal|"."
block|,
literal|"---"
block|,
literal|"b"
block|,
literal|"aa"
block|,
literal|"."
block|,
literal|"c"
block|,
literal|"c"
block|,
literal|"b"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|,
literal|6
block|,
literal|8
block|,
literal|12
block|,
literal|14
block|,
literal|18
block|,
literal|21
block|,
literal|23
block|,
literal|25
block|,
literal|29
block|,
literal|33
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|5
block|,
literal|7
block|,
literal|11
block|,
literal|13
block|,
literal|17
block|,
literal|20
block|,
literal|22
block|,
literal|24
block|,
literal|28
block|,
literal|32
block|,
literal|35
block|}
argument_list|,
name|BLOCK
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|pattern
specifier|private
name|Pattern
name|pattern
parameter_list|(
name|String
name|p
parameter_list|)
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|p
argument_list|)
return|;
block|}
comment|/** blast some random strings through the analyzer */
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numPatterns
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
name|numPatterns
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Pattern
name|p
init|=
name|randomPattern
argument_list|()
decl_stmt|;
specifier|final
name|String
name|replacement
init|=
name|_TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|reader
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|tokenizer
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Reader
name|initReader
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|p
argument_list|,
name|replacement
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// only ascii
block|}
block|}
DECL|method|randomPattern
specifier|public
specifier|static
name|Pattern
name|randomPattern
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|Pattern
operator|.
name|compile
argument_list|(
name|_TestUtil
operator|.
name|randomRegexpishString
argument_list|(
name|random
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|ignored
parameter_list|)
block|{
comment|// if at first you don't succeed...
block|}
block|}
block|}
block|}
end_class

end_unit

