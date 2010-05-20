begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.bg
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|bg
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|CharArraySet
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
name|KeywordMarkerFilter
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
name|WhitespaceTokenizer
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
name|Version
import|;
end_import

begin_comment
comment|/**  * Test the Bulgarian Stemmer  */
end_comment

begin_class
DECL|class|TestBulgarianStemmer
specifier|public
class|class
name|TestBulgarianStemmer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Test showing how masculine noun forms conflate. An example noun for each    * common (and some rare) plural pattern is listed.    */
DECL|method|testMasculineNouns
specifier|public
name|void
name|testMasculineNouns
parameter_list|()
throws|throws
name|IOException
block|{
name|BulgarianAnalyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// -Ð¸ pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´Ð¾Ð²Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´Ð¾Ð²ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´"
block|}
argument_list|)
expr_stmt|;
comment|// -Ð¾Ð²Ðµ pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð½Ð°ÑÐ¾Ð´"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð½Ð°ÑÐ¾Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð½Ð°ÑÐ¾Ð´Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð½Ð°ÑÐ¾Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð½Ð°ÑÐ¾Ð´ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð½Ð°ÑÐ¾Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð½Ð°ÑÐ¾Ð´Ð¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð½Ð°ÑÐ¾Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð½Ð°ÑÐ¾Ð´Ð¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð½Ð°ÑÐ¾Ð´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð½Ð°ÑÐ¾Ð´Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð½Ð°ÑÐ¾Ð´"
block|}
argument_list|)
expr_stmt|;
comment|// -Ð¸ÑÐ° pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÑÐ¸ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÑÐ¸ÑÐ°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÑ"
block|}
argument_list|)
expr_stmt|;
comment|// -ÑÐµÑÐ° pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´ÐµÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´ÐµÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´ÐµÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´ÐµÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´ÐµÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´ÐµÑ"
block|}
argument_list|)
expr_stmt|;
comment|/* note the below forms conflate with each other, but not the rest */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´Ð¾Ð²ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´Ð¾Ð²Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð³ÑÐ°Ð´Ð¾Ð²ÑÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð³ÑÐ°Ð´Ð¾Ð²Ñ"
block|}
argument_list|)
expr_stmt|;
comment|// -Ð¾Ð²ÑÐ¸ pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÐ´Ð¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÐ´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÐ´Ð¾ÑÐ¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÐ´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÐ´Ð¾Ð²ÑÐ¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÐ´"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÐ´Ð¾Ð²ÑÐ¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÐ´"
block|}
argument_list|)
expr_stmt|;
comment|// -Ðµ pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼ÑÐ¶"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼ÑÐ¶"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼ÑÐ¶Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼ÑÐ¶"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼ÑÐ¶Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼ÑÐ¶"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼ÑÐ¶ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼ÑÐ¶"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼ÑÐ¶Ð¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼ÑÐ¶"
block|}
argument_list|)
expr_stmt|;
comment|/* word is too short, will not remove -ÑÑ */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼ÑÐ¶ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼ÑÐ¶ÑÑ"
block|}
argument_list|)
expr_stmt|;
comment|// -Ð° pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°Ðº"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÐºÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÐºÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÐºÐ°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°Ðº"
block|}
argument_list|)
expr_stmt|;
comment|// Ð±ÑÐ°Ñ
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð±ÑÐ°Ñ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð±ÑÐ°Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð±ÑÐ°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð±ÑÐ°Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð±ÑÐ°ÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð±ÑÐ°Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð±ÑÐ°ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð±ÑÐ°Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð±ÑÐ°ÑÑÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð±ÑÐ°Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð±ÑÐ°ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð±ÑÐ°Ñ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test showing how feminine noun forms conflate    */
DECL|method|testFeminineNouns
specifier|public
name|void
name|testFeminineNouns
parameter_list|()
throws|throws
name|IOException
block|{
name|BulgarianAnalyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð²ÐµÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²ÐµÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð²ÐµÑÑÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²ÐµÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð²ÐµÑÑÐ¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²ÐµÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð²ÐµÑÑÐ¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð²ÐµÑÑ"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test showing how neuter noun forms conflate an example noun for each common    * plural pattern is listed    */
DECL|method|testNeuterNouns
specifier|public
name|void
name|testNeuterNouns
parameter_list|()
throws|throws
name|IOException
block|{
name|BulgarianAnalyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// -Ð° pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÑÐ²Ð¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÑÐ²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÑÐ²Ð¾ÑÐ¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÑÐ²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÑÐ²Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÑÐ²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÑÐ²ÐµÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÑÐ²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÑÐ²Ð°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÑÐ²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð´ÑÑÐ²ÐµÑÐ°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð´ÑÑÐ²"
block|}
argument_list|)
expr_stmt|;
comment|// -ÑÐ° pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼Ð¾ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼Ð¾Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼Ð¾ÑÐµÑÐ¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼Ð¾Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼Ð¾ÑÐµÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼Ð¾Ñ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¼Ð¾ÑÐµÑÐ°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¼Ð¾Ñ"
block|}
argument_list|)
expr_stmt|;
comment|// -Ñ pattern
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸ÐµÑÐ¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸ÑÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸"
block|}
argument_list|)
expr_stmt|;
comment|/* note the below form in this example does not conflate with the rest */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¸Ð·ÐºÐ»ÑÑÐµÐ½Ð¸Ñ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¸Ð·ÐºÐ»ÑÑÐ½"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test showing how adjectival forms conflate    */
DECL|method|testAdjectives
specifier|public
name|void
name|testAdjectives
parameter_list|()
throws|throws
name|IOException
block|{
name|BulgarianAnalyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð¸Ñ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð¸ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð¾ÑÐ¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÑÐ°ÑÐ¸Ð²Ð¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÑÐ°ÑÐ¸Ð²"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test some exceptional rules, implemented as rewrites.    */
DECL|method|testExceptions
specifier|public
name|void
name|testExceptions
parameter_list|()
throws|throws
name|IOException
block|{
name|BulgarianAnalyzer
name|a
init|=
operator|new
name|BulgarianAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// ÑÐ¸ -> Ðº
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÐºÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÐºÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÑÐ¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸ÑÐ¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐ¾Ð±ÑÑÐ²ÐµÐ½Ð¸Ðº"
block|}
argument_list|)
expr_stmt|;
comment|// Ð·Ð¸ -> Ð³
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð·Ð¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð·Ð¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿Ð¾Ð´Ð»Ð¾Ð³"
block|}
argument_list|)
expr_stmt|;
comment|// ÑÐ¸ -> Ñ
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¶ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¶ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¶ÑÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¶ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¶ÑÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¶ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¶ÑÑÐ¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¶ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÐºÐ¾Ð¶ÑÑÐ¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÐºÐ¾Ð¶ÑÑ"
block|}
argument_list|)
expr_stmt|;
comment|// Ñ deletion
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐµÐ½ÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐµÐ½ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐµÐ½ÑÑÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐµÐ½ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐµÐ½ÑÑÑÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐµÐ½ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐµÐ½ÑÑÐ¾Ð²Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐµÐ½ÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÐµÐ½ÑÑÐ¾Ð²ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÐµÐ½ÑÑ"
block|}
argument_list|)
expr_stmt|;
comment|// Ðµ*Ð¸ -> Ñ*
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÐ¾Ð¼ÑÐ½Ð°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÐ¾Ð¼ÑÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÐ¾Ð¼ÑÐ½Ð°ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÐ¾Ð¼ÑÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÐ¾Ð¼ÐµÐ½Ð¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÐ¾Ð¼ÑÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÑÐ¾Ð¼ÐµÐ½Ð¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÑÐ¾Ð¼ÑÐ½"
block|}
argument_list|)
expr_stmt|;
comment|// ÐµÐ½ -> Ð½
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÐµÑÐµÐ½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÐµÑÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÐµÑÐµÐ½ÑÐ°"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÐµÑÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÐµÑÐ½Ð¸"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÐµÑÐ½"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Ð¿ÐµÑÐ½Ð¸ÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ð¿ÐµÑÐ½"
block|}
argument_list|)
expr_stmt|;
comment|// -ÐµÐ²Ðµ -> Ð¹
comment|// note: this is the only word i think this rule works for.
comment|// most -ÐµÐ²Ðµ pluralized nouns are monosyllabic,
comment|// and the stemmer requires length> 6...
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÑÑÐ¾Ð¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑÐ¾Ð¹"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÑÑÐ¾ÐµÐ²Ðµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑÐ¾Ð¹"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÑÑÐ¾ÐµÐ²ÐµÑÐµ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑÐ¾Ð¹"
block|}
argument_list|)
expr_stmt|;
comment|/* note the below forms conflate with each other, but not the rest */
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÑÑÐ¾Ñ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÑÑÑÐ¾ÑÑ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑ"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithKeywordAttribute
specifier|public
name|void
name|testWithKeywordAttribute
parameter_list|()
throws|throws
name|IOException
block|{
name|CharArraySet
name|set
init|=
operator|new
name|CharArraySet
argument_list|(
name|Version
operator|.
name|LUCENE_31
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
literal|"ÑÑÑÐ¾ÐµÐ²Ðµ"
argument_list|)
expr_stmt|;
name|WhitespaceTokenizer
name|tokenStream
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"ÑÑÑÐ¾ÐµÐ²ÐµÑÐµ ÑÑÑÐ¾ÐµÐ²Ðµ"
argument_list|)
argument_list|)
decl_stmt|;
name|BulgarianStemFilter
name|filter
init|=
operator|new
name|BulgarianStemFilter
argument_list|(
operator|new
name|KeywordMarkerFilter
argument_list|(
name|tokenStream
argument_list|,
name|set
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|filter
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÑÑÑÐ¾Ð¹"
block|,
literal|"ÑÑÑÐ¾ÐµÐ²Ðµ"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

