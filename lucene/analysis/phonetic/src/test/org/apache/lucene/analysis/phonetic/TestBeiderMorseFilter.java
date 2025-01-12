begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.phonetic
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|phonetic
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|NameType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|PhoneticEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|RuleType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|language
operator|.
name|bm
operator|.
name|Languages
operator|.
name|LanguageSet
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
name|core
operator|.
name|KeywordTokenizer
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
name|miscellaneous
operator|.
name|PatternKeywordMarkerFilter
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
name|KeywordAttribute
import|;
end_import

begin_comment
comment|/** Tests {@link BeiderMorseFilter} */
end_comment

begin_class
DECL|class|TestBeiderMorseFilter
specifier|public
class|class
name|TestBeiderMorseFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
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
name|analyzer
operator|=
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
name|MockTokenizer
argument_list|(
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
operator|new
name|BeiderMorseFilter
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|PhoneticEngine
argument_list|(
name|NameType
operator|.
name|GENERIC
argument_list|,
name|RuleType
operator|.
name|EXACT
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/** generic, "exact" configuration */
DECL|method|testBasicUsage
specifier|public
name|void
name|testBasicUsage
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"Angelo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"anZelo"
block|,
literal|"andZelo"
block|,
literal|"angelo"
block|,
literal|"anhelo"
block|,
literal|"anjelo"
block|,
literal|"anxelo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|,
literal|6
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"D'Angelo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"anZelo"
block|,
literal|"andZelo"
block|,
literal|"angelo"
block|,
literal|"anhelo"
block|,
literal|"anjelo"
block|,
literal|"anxelo"
block|,
literal|"danZelo"
block|,
literal|"dandZelo"
block|,
literal|"dangelo"
block|,
literal|"danhelo"
block|,
literal|"danjelo"
block|,
literal|"danxelo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|,
literal|8
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** restrict the output to a set of possible origin languages */
DECL|method|testLanguageSet
specifier|public
name|void
name|testLanguageSet
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|LanguageSet
name|languages
init|=
name|LanguageSet
operator|.
name|from
argument_list|(
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"italian"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"greek"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"spanish"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
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
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
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
operator|new
name|BeiderMorseFilter
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|PhoneticEngine
argument_list|(
name|NameType
operator|.
name|GENERIC
argument_list|,
name|RuleType
operator|.
name|EXACT
argument_list|,
literal|true
argument_list|)
argument_list|,
name|languages
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"Angelo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"andZelo"
block|,
literal|"angelo"
block|,
literal|"anxelo"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|, }
argument_list|,
operator|new
name|int
index|[]
block|{
literal|6
block|,
literal|6
block|,
literal|6
block|, }
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|0
block|,
literal|0
block|, }
argument_list|)
expr_stmt|;
name|analyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** for convenience, if the input yields no output, we pass it thru as-is */
DECL|method|testNumbers
specifier|public
name|void
name|testNumbers
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"1234"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"1234"
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
operator|new
name|int
index|[]
block|{
literal|1
block|}
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
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|analyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
parameter_list|()
throws|throws
name|IOException
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
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|BeiderMorseFilter
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|PhoneticEngine
argument_list|(
name|NameType
operator|.
name|GENERIC
argument_list|,
name|RuleType
operator|.
name|EXACT
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|a
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testCustomAttribute
specifier|public
name|void
name|testCustomAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|TokenStream
name|stream
init|=
operator|new
name|MockTokenizer
argument_list|(
name|MockTokenizer
operator|.
name|KEYWORD
argument_list|,
literal|false
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
literal|"D'Angelo"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|PatternKeywordMarkerFilter
argument_list|(
name|stream
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
argument_list|)
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|BeiderMorseFilter
argument_list|(
name|stream
argument_list|,
operator|new
name|PhoneticEngine
argument_list|(
name|NameType
operator|.
name|GENERIC
argument_list|,
name|RuleType
operator|.
name|EXACT
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|KeywordAttribute
name|keyAtt
init|=
name|stream
operator|.
name|addAttribute
argument_list|(
name|KeywordAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|stream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|keyAtt
operator|.
name|isKeyword
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|stream
operator|.
name|end
argument_list|()
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

