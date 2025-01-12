begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
package|;
end_package

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
name|util
operator|.
name|IOUtils
import|;
end_import

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

begin_class
DECL|class|TestJapaneseIterationMarkCharFilter
specifier|public
class|class
name|TestJapaneseIterationMarkCharFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|keywordAnalyzer
DECL|field|japaneseAnalyzer
specifier|private
name|Analyzer
name|keywordAnalyzer
decl_stmt|,
name|japaneseAnalyzer
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
name|keywordAnalyzer
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
name|KEYWORD
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
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|JapaneseIterationMarkCharFilter
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|japaneseAnalyzer
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
name|JapaneseTokenizer
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|JapaneseTokenizer
operator|.
name|Mode
operator|.
name|SEARCH
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
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|JapaneseIterationMarkCharFilter
argument_list|(
name|reader
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
name|IOUtils
operator|.
name|close
argument_list|(
name|keywordAnalyzer
argument_list|,
name|japaneseAnalyzer
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testKanji
specifier|public
name|void
name|testKanji
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test single repetition
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"æã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ææ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"æã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ææ"
block|}
argument_list|)
expr_stmt|;
comment|// Test multiple repetitions
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"é¦¬é¹¿ãããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"é¦¬é¹¿é¦¬é¹¿ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"é¦¬é¹¿ãããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"é¦¬é¹¿é¦¬é¹¿ãã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKatakana
specifier|public
name|void
name|testKatakana
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test single repetition
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãã¹ã¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã¹ãº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"ãã¹ã¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã"
block|,
literal|"ã¹ãº"
block|}
argument_list|)
expr_stmt|;
comment|// Side effect
block|}
DECL|method|testHiragana
specifier|public
name|void
name|testHiragana
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test single unvoiced iteration
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ããã®"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ããã®"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"ããã®"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã"
block|,
literal|"ãã®"
block|}
argument_list|)
expr_stmt|;
comment|// Side effect
comment|// Test single voiced iteration
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã¿ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã¿ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"ã¿ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã¿ãã"
block|}
argument_list|)
expr_stmt|;
comment|// Test single voiced iteration
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
comment|// Test single unvoiced iteration with voiced iteration
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
comment|// Test multiple repetitions with voiced iteration
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã¨ããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã¨ããã©ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|"ã¨ããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã¨ããã©ãã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMalformed
specifier|public
name|void
name|testMalformed
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We can't iterate c here, so emit as it is
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"abcã¨ãããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abcã¨ããcã¨ãã"
block|}
argument_list|)
expr_stmt|;
comment|// We can't iterate c (with dakuten change) here, so emit it as-is
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"abcã¨ãããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abcã¨ããcã¨ãã"
block|}
argument_list|)
expr_stmt|;
comment|// We can't iterate before beginning of stream, so emit characters as-is
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã¨ãããããããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã¨ããã©ããããã"
block|}
argument_list|)
expr_stmt|;
comment|// We can't iterate an iteration mark only, so emit as-is
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
comment|// We can't iterate a full stop punctuation mark (because we use it as a flush marker)
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãããã"
block|}
argument_list|)
expr_stmt|;
comment|// We can iterate other punctuation marks
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ï¼ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ï¼ï¼"
block|}
argument_list|)
expr_stmt|;
comment|// We can not get a dakuten variant of ã½ -- this is also a corner case test for inside()
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã­ãã½ãã¤ãã´"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã­ãã½ã½ã¤ãã´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã­ãã½ãã¤ãã´"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã­ãã½ã½ã¤ãã´"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Empty input stays empty
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|japaneseAnalyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullStop
specifier|public
name|void
name|testFullStop
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test full stops
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ãã"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"ããã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ããã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKanjiOnly
specifier|public
name|void
name|testKanjiOnly
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test kanji only repetition marks
name|CharFilter
name|filter
init|=
operator|new
name|JapaneseIterationMarkCharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"
argument_list|)
argument_list|,
literal|true
argument_list|,
comment|// kanji
literal|false
comment|// no kana
argument_list|)
decl_stmt|;
name|assertCharFilterEquals
argument_list|(
name|filter
argument_list|,
literal|"ææãããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"
argument_list|)
expr_stmt|;
block|}
DECL|method|testKanaOnly
specifier|public
name|void
name|testKanaOnly
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test kana only repetition marks
name|CharFilter
name|filter
init|=
operator|new
name|JapaneseIterationMarkCharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"
argument_list|)
argument_list|,
literal|false
argument_list|,
comment|// no kanji
literal|true
comment|// kana
argument_list|)
decl_stmt|;
name|assertCharFilterEquals
argument_list|(
name|filter
argument_list|,
literal|"æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ããã©ããã"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNone
specifier|public
name|void
name|testNone
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test no repetition marks
name|CharFilter
name|filter
init|=
operator|new
name|JapaneseIterationMarkCharFilter
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"
argument_list|)
argument_list|,
literal|false
argument_list|,
comment|// no kanji
literal|false
comment|// no kana
argument_list|)
decl_stmt|;
name|assertCharFilterEquals
argument_list|(
name|filter
argument_list|,
literal|"æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ããã§ããabcã¨ãããããã"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCombinations
specifier|public
name|void
name|testCombinations
parameter_list|()
throws|throws
name|IOException
block|{
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
literal|"æããããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ã«è¡ãã¾ãã"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ææãããã®ããã¨ä¸ç·ã«ãå¯¿å¸ãé£ã¹ã«è¡ãã¾ãã"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHiraganaCoverage
specifier|public
name|void
name|testHiraganaCoverage
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test all hiragana iteration variants
name|String
name|source
init|=
literal|"ããããããããããããããããããããããããããããããããããããããããããã ãã¡ãã¢ãã¤ãã¥ãã¦ãã§ãã¨ãã©ãã¯ãã°ãã²ãã³ããµãã¶ãã¸ãã¹ãã»ãã¼ã"
decl_stmt|;
name|String
name|target
init|=
literal|"ããããããããããããããããããããããããããããããããããããããããããã ãã¡ã¡ã¢ã¡ã¤ã¤ã¥ã¤ã¦ã¦ã§ã¦ã¨ã¨ã©ã¨ã¯ã¯ã°ã¯ã²ã²ã³ã²ãµãµã¶ãµã¸ã¸ã¹ã¸ã»ã»ã¼ã»"
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
name|target
block|}
argument_list|)
expr_stmt|;
comment|// Test all hiragana iteration variants with dakuten
name|source
operator|=
literal|"ããããããããããããããããããããããããããããããããããããããããããã ãã¡ãã¢ãã¤ãã¥ãã¦ãã§ãã¨ãã©ãã¯ãã°ãã²ãã³ããµãã¶ãã¸ãã¹ãã»ãã¼ã"
expr_stmt|;
name|target
operator|=
literal|"ãããããããããããããããããããããããããããããããããããããããããã ã ã ã¡ã¢ã¢ã¢ã¤ã¥ã¥ã¥ã¦ã§ã§ã§ã¨ã©ã©ã©ã¯ã°ã°ã°ã²ã³ã³ã³ãµã¶ã¶ã¶ã¸ã¹ã¹ã¹ã»ã¼ã¼ã¼"
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
name|target
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKatakanaCoverage
specifier|public
name|void
name|testKatakanaCoverage
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Test all katakana iteration variants
name|String
name|source
init|=
literal|"ã«ã½ã¬ã½ã­ã½ã®ã½ã¯ã½ã°ã½ã±ã½ã²ã½ã³ã½ã´ã½ãµã½ã¶ã½ã·ã½ã¸ã½ã¹ã½ãºã½ã»ã½ã¼ã½ã½ã½ã¾ã½ã¿ã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½ãã½"
decl_stmt|;
name|String
name|target
init|=
literal|"ã«ã«ã¬ã«ã­ã­ã®ã­ã¯ã¯ã°ã¯ã±ã±ã²ã±ã³ã³ã´ã³ãµãµã¶ãµã·ã·ã¸ã·ã¹ã¹ãºã¹ã»ã»ã¼ã»ã½ã½ã¾ã½ã¿ã¿ãã¿ãããããããããããããããããããããããããããããããããããã"
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
name|target
block|}
argument_list|)
expr_stmt|;
comment|// Test all katakana iteration variants with dakuten
name|source
operator|=
literal|"ã«ã¾ã¬ã¾ã­ã¾ã®ã¾ã¯ã¾ã°ã¾ã±ã¾ã²ã¾ã³ã¾ã´ã¾ãµã¾ã¶ã¾ã·ã¾ã¸ã¾ã¹ã¾ãºã¾ã»ã¾ã¼ã¾ã½ã¾ã¾ã¾ã¿ã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾ãã¾"
expr_stmt|;
name|target
operator|=
literal|"ã«ã¬ã¬ã¬ã­ã®ã®ã®ã¯ã°ã°ã°ã±ã²ã²ã²ã³ã´ã´ã´ãµã¶ã¶ã¶ã·ã¸ã¸ã¸ã¹ãºãºãºã»ã¼ã¼ã¼ã½ã¾ã¾ã¾ã¿ããããããããããããããããããããããããããããããããããããããã"
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|keywordAnalyzer
argument_list|,
name|source
argument_list|,
operator|new
name|String
index|[]
block|{
name|target
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomStrings
specifier|public
name|void
name|testRandomStrings
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Blast some random strings through
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|keywordAnalyzer
argument_list|,
literal|1000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandomHugeStrings
specifier|public
name|void
name|testRandomHugeStrings
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Blast some random strings through
name|checkRandomData
argument_list|(
name|random
argument_list|()
argument_list|,
name|keywordAnalyzer
argument_list|,
literal|100
operator|*
name|RANDOM_MULTIPLIER
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCharFilterEquals
specifier|private
name|void
name|assertCharFilterEquals
parameter_list|(
name|CharFilter
name|filter
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|actual
init|=
name|readFully
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
DECL|method|readFully
specifier|private
name|String
name|readFully
parameter_list|(
name|Reader
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|stream
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

