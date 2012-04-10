begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.charfilter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|charfilter
package|;
end_package

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

begin_class
DECL|class|TestMappingCharFilter
specifier|public
class|class
name|TestMappingCharFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|normMap
name|NormalizeCharMap
name|normMap
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
name|normMap
operator|=
operator|new
name|NormalizeCharMap
argument_list|()
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"aa"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"bbb"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"cccc"
argument_list|,
literal|"cc"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"h"
argument_list|,
literal|"i"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"j"
argument_list|,
literal|"jj"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"k"
argument_list|,
literal|"kkk"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"ll"
argument_list|,
literal|"llll"
argument_list|)
expr_stmt|;
name|normMap
operator|.
name|add
argument_list|(
literal|"empty"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|testReaderReset
specifier|public
name|void
name|testReaderReset
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"x"
argument_list|)
argument_list|)
decl_stmt|;
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
name|int
name|len
init|=
name|cs
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|buf
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|len
operator|=
name|cs
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|// rewind
name|cs
operator|.
name|reset
argument_list|()
expr_stmt|;
name|len
operator|=
name|cs
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|'x'
argument_list|,
name|buf
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testNothingChange
specifier|public
name|void
name|testNothingChange
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"x"
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
literal|"x"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test1to1
specifier|public
name|void
name|test1to1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"h"
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
literal|"i"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test1to2
specifier|public
name|void
name|test1to2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"j"
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
literal|"jj"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test1to3
specifier|public
name|void
name|test1to3
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"k"
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
literal|"kkk"
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
literal|1
block|}
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|test2to4
specifier|public
name|void
name|test2to4
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"ll"
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
literal|"llll"
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
literal|2
block|}
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|test2to1
specifier|public
name|void
name|test2to1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"aa"
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
literal|"a"
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
literal|2
block|}
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|test3to1
specifier|public
name|void
name|test3to1
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"bbb"
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
literal|"b"
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
literal|3
block|}
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|test4to2
specifier|public
name|void
name|test4to2
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"cccc"
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
literal|"cc"
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
literal|4
block|}
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
DECL|method|test5to0
specifier|public
name|void
name|test5to0
parameter_list|()
throws|throws
name|Exception
block|{
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"empty"
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
index|[
literal|0
index|]
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
comment|//
comment|//                1111111111222
comment|//      01234567890123456789012
comment|//(in)  h i j k ll cccc bbb aa
comment|//
comment|//                1111111111222
comment|//      01234567890123456789012
comment|//(out) i i jj kkk llll cc b a
comment|//
comment|//    h, 0, 1 =>    i, 0, 1
comment|//    i, 2, 3 =>    i, 2, 3
comment|//    j, 4, 5 =>   jj, 4, 5
comment|//    k, 6, 7 =>  kkk, 6, 7
comment|//   ll, 8,10 => llll, 8,10
comment|// cccc,11,15 =>   cc,11,15
comment|//  bbb,16,19 =>    b,16,19
comment|//   aa,20,22 =>    a,20,22
comment|//
DECL|method|testTokenStream
specifier|public
name|void
name|testTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testString
init|=
literal|"h i j k ll cccc bbb aa"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testString
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
literal|"i"
block|,
literal|"i"
block|,
literal|"jj"
block|,
literal|"kkk"
block|,
literal|"llll"
block|,
literal|"cc"
block|,
literal|"b"
block|,
literal|"a"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|8
block|,
literal|11
block|,
literal|16
block|,
literal|20
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
literal|5
block|,
literal|7
block|,
literal|10
block|,
literal|15
block|,
literal|19
block|,
literal|22
block|}
argument_list|,
name|testString
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//
comment|//
comment|//        0123456789
comment|//(in)    aaaa ll h
comment|//(out-1) aa llll i
comment|//(out-2) a llllllll i
comment|//
comment|// aaaa,0,4 => a,0,4
comment|//   ll,5,7 => llllllll,5,7
comment|//    h,8,9 => i,8,9
DECL|method|testChained
specifier|public
name|void
name|testChained
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testString
init|=
literal|"aaaa ll h"
decl_stmt|;
name|CharStream
name|cs
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
name|CharReader
operator|.
name|get
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testString
argument_list|)
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
literal|"a"
block|,
literal|"llllllll"
block|,
literal|"i"
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
literal|9
block|}
argument_list|,
name|testString
operator|.
name|length
argument_list|()
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
name|Analyzer
name|analyzer
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
name|MappingCharFilter
argument_list|(
name|normMap
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
name|int
name|numRounds
init|=
name|RANDOM_MULTIPLIER
operator|*
literal|10000
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|analyzer
argument_list|,
name|numRounds
argument_list|)
expr_stmt|;
block|}
comment|// nocommit: wrong final offset, fix this!
DECL|method|testFinalOffsetSpecialCase
specifier|public
name|void
name|testFinalOffsetSpecialCase
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|NormalizeCharMap
name|map
init|=
operator|new
name|NormalizeCharMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
literal|"t"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// even though this below rule has no effect, the test passes if you remove it!!
name|map
operator|.
name|add
argument_list|(
literal|"tmakdbl"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|Analyzer
name|analyzer
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
name|MappingCharFilter
argument_list|(
name|map
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
name|String
name|text
init|=
literal|"gzw f quaxot"
decl_stmt|;
name|checkAnalysisConsistency
argument_list|(
name|random
argument_list|,
name|analyzer
argument_list|,
literal|false
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

