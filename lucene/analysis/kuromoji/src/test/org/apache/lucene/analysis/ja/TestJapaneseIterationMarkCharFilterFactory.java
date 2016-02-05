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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Simple tests for {@link JapaneseIterationMarkCharFilterFactory}  */
end_comment

begin_class
DECL|class|TestJapaneseIterationMarkCharFilterFactory
specifier|public
class|class
name|TestJapaneseIterationMarkCharFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testIterationMarksWithKeywordTokenizer
specifier|public
name|void
name|testIterationMarksWithKeywordTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|text
init|=
literal|"æãé¦¬é¹¿ããããã¨ããããããã¹ã¾"
decl_stmt|;
name|JapaneseIterationMarkCharFilterFactory
name|filterFactory
init|=
operator|new
name|JapaneseIterationMarkCharFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|CharFilter
name|filter
init|=
name|filterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tokenStream
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
name|tokenStream
operator|)
operator|.
name|setReader
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ææé¦¬é¹¿é¦¬é¹¿ããã¨ããã©ãããã¹ãº"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testIterationMarksWithJapaneseTokenizer
specifier|public
name|void
name|testIterationMarksWithJapaneseTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizerFactory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|JapaneseIterationMarkCharFilterFactory
name|filterFactory
init|=
operator|new
name|JapaneseIterationMarkCharFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|CharFilter
name|filter
init|=
name|filterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æãé¦¬é¹¿ããããã¨ããããããã¹ã¾"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|tokenStream
operator|)
operator|.
name|setReader
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ææ"
block|,
literal|"é¦¬é¹¿é¦¬é¹¿ãã"
block|,
literal|"ã¨ããã©ãã"
block|,
literal|"ã"
block|,
literal|"ã¹ãº"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKanjiOnlyIterationMarksWithJapaneseTokenizer
specifier|public
name|void
name|testKanjiOnlyIterationMarksWithJapaneseTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizerFactory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filterArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|filterArgs
operator|.
name|put
argument_list|(
literal|"normalizeKanji"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|filterArgs
operator|.
name|put
argument_list|(
literal|"normalizeKana"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|JapaneseIterationMarkCharFilterFactory
name|filterFactory
init|=
operator|new
name|JapaneseIterationMarkCharFilterFactory
argument_list|(
name|filterArgs
argument_list|)
decl_stmt|;
name|CharFilter
name|filter
init|=
name|filterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æãé¦¬é¹¿ããããã¨ããããããã¹ã¾"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|tokenStream
operator|)
operator|.
name|setReader
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ææ"
block|,
literal|"é¦¬é¹¿é¦¬é¹¿ãã"
block|,
literal|"ã¨ãã"
block|,
literal|"ã"
block|,
literal|"ã"
block|,
literal|"ã"
block|,
literal|"ãã¹"
block|,
literal|"ã¾"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testKanaOnlyIterationMarksWithJapaneseTokenizer
specifier|public
name|void
name|testKanaOnlyIterationMarksWithJapaneseTokenizer
parameter_list|()
throws|throws
name|IOException
block|{
name|JapaneseTokenizerFactory
name|tokenizerFactory
init|=
operator|new
name|JapaneseTokenizerFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
name|tokenizerFactory
operator|.
name|inform
argument_list|(
operator|new
name|StringMockResourceLoader
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|filterArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|filterArgs
operator|.
name|put
argument_list|(
literal|"normalizeKanji"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|filterArgs
operator|.
name|put
argument_list|(
literal|"normalizeKana"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|JapaneseIterationMarkCharFilterFactory
name|filterFactory
init|=
operator|new
name|JapaneseIterationMarkCharFilterFactory
argument_list|(
name|filterArgs
argument_list|)
decl_stmt|;
name|CharFilter
name|filter
init|=
name|filterFactory
operator|.
name|create
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"æãé¦¬é¹¿ããããã¨ããããããã¹ã¾"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|tokenStream
init|=
name|tokenizerFactory
operator|.
name|create
argument_list|(
name|newAttributeFactory
argument_list|()
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Tokenizer
operator|)
name|tokenStream
operator|)
operator|.
name|setReader
argument_list|(
name|filter
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tokenStream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"æã"
block|,
literal|"é¦¬é¹¿"
block|,
literal|"ã"
block|,
literal|"ã"
block|,
literal|"ãã"
block|,
literal|"ã¨ããã©ãã"
block|,
literal|"ã"
block|,
literal|"ã¹ãº"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Test that bogus arguments result in exception */
DECL|method|testBogusArguments
specifier|public
name|void
name|testBogusArguments
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
operator|new
name|JapaneseIterationMarkCharFilterFactory
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"bogusArg"
argument_list|,
literal|"bogusValue"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unknown parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

