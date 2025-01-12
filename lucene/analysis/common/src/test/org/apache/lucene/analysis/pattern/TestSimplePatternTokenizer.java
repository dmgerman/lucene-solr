begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|StringReader
import|;
end_import

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
name|CharFilter
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
name|analysis
operator|.
name|charfilter
operator|.
name|MappingCharFilter
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
name|charfilter
operator|.
name|NormalizeCharMap
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|automaton
operator|.
name|Automaton
import|;
end_import

begin_class
DECL|class|TestSimplePatternTokenizer
specifier|public
class|class
name|TestSimplePatternTokenizer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testGreedy
specifier|public
name|void
name|testGreedy
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"(foo)+"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"bar foofoo baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foofoo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|4
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|10
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBigLookahead
specifier|public
name|void
name|testBigLookahead
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|b
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|'b'
argument_list|)
expr_stmt|;
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|b
operator|=
operator|new
name|StringBuilder
argument_list|()
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
block|}
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneToken
specifier|public
name|void
name|testOneToken
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|".*"
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|t
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|s
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|s
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
break|break;
block|}
block|}
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyStringPatternNoMatch
specifier|public
name|void
name|testEmptyStringPatternNoMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"a*"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyStringPatternOneMatch
specifier|public
name|void
name|testEmptyStringPatternOneMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"a*"
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|t
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"bbab"
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEndOffset
specifier|public
name|void
name|testEndOffset
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"a+"
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|t
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|OffsetAttribute
name|offsetAtt
init|=
name|t
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"aaabbb"
argument_list|)
argument_list|)
expr_stmt|;
name|t
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"aaa"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|t
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|end
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|offsetAtt
operator|.
name|endOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFixedToken
specifier|public
name|void
name|testFixedToken
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"aaaa"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"aaaaaaaaaaaaaaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"aaaa"
block|,
literal|"aaaa"
block|,
literal|"aaaa"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|4
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
literal|8
block|,
literal|12
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|qpattern
init|=
literal|"\\'([^\\']+)\\'"
decl_stmt|;
comment|// get stuff between "'"
name|String
index|[]
index|[]
name|tests
init|=
block|{
comment|// pattern        input                    output
block|{
literal|":"
block|,
literal|"boo:and:foo"
block|,
literal|": :"
block|}
block|,
block|{
name|qpattern
block|,
literal|"aaa 'bbb' 'ccc'"
block|,
literal|"'bbb' 'ccc'"
block|}
block|,     }
decl_stmt|;
for|for
control|(
name|String
index|[]
name|test
range|:
name|tests
control|)
block|{
name|TokenStream
name|stream
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
name|test
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|stream
operator|)
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|test
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|out
init|=
name|tsToString
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"pattern: "
operator|+
name|test
index|[
literal|0
index|]
operator|+
literal|" with input: "
operator|+
name|test
index|[
literal|1
index|]
argument_list|,
name|test
index|[
literal|2
index|]
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNotDeterminized
specifier|public
name|void
name|testNotDeterminized
parameter_list|()
throws|throws
name|Exception
block|{
name|Automaton
name|a
init|=
operator|new
name|Automaton
argument_list|()
decl_stmt|;
name|int
name|start
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|mid1
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|mid2
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|a
operator|.
name|createState
argument_list|()
decl_stmt|;
name|a
operator|.
name|setAccept
argument_list|(
name|end
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|mid1
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|start
argument_list|,
name|mid2
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|mid1
argument_list|,
name|end
argument_list|,
literal|'b'
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTransition
argument_list|(
name|mid2
argument_list|,
name|end
argument_list|,
literal|'b'
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|SimplePatternTokenizer
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsetCorrection
specifier|public
name|void
name|testOffsetCorrection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|INPUT
init|=
literal|"G&uuml;nther G&uuml;nther is here"
decl_stmt|;
comment|// create MappingCharFilter
name|List
argument_list|<
name|String
argument_list|>
name|mappingRules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|mappingRules
operator|.
name|add
argument_list|(
literal|"\"&uuml;\" => \"Ã¼\""
argument_list|)
expr_stmt|;
name|NormalizeCharMap
operator|.
name|Builder
name|builder
init|=
operator|new
name|NormalizeCharMap
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
literal|"&uuml;"
argument_list|,
literal|"Ã¼"
argument_list|)
expr_stmt|;
name|NormalizeCharMap
name|normMap
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|CharFilter
name|charStream
init|=
operator|new
name|MappingCharFilter
argument_list|(
name|normMap
argument_list|,
operator|new
name|StringReader
argument_list|(
name|INPUT
argument_list|)
argument_list|)
decl_stmt|;
comment|// create SimplePatternTokenizer
name|Tokenizer
name|stream
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"GÃ¼nther"
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setReader
argument_list|(
name|charStream
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"GÃ¼nther"
block|,
literal|"GÃ¼nther"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|13
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|12
block|,
literal|25
block|}
argument_list|,
name|INPUT
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**     * TODO: rewrite tests not to use string comparison.    */
DECL|method|tsToString
specifier|private
specifier|static
name|String
name|tsToString
parameter_list|(
name|TokenStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|in
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// extra safety to enforce, that the state is not preserved and also
comment|// assign bogus values
name|in
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"bogusTerm"
argument_list|)
expr_stmt|;
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
if|if
condition|(
name|out
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|append
argument_list|(
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|clearAttributes
argument_list|()
expr_stmt|;
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
literal|"bogusTerm"
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
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
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|a
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
name|Analyzer
name|b
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
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|b
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
name|b
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEndLookahead
specifier|public
name|void
name|testEndLookahead
parameter_list|()
throws|throws
name|Exception
block|{
name|Tokenizer
name|t
init|=
operator|new
name|SimplePatternTokenizer
argument_list|(
literal|"(ab)+"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setReader
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"aba"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|t
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ab"
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
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

