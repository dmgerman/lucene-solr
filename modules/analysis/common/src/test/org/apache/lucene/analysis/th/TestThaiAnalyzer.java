begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.th
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|th
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * Test case for ThaiAnalyzer, modified from TestFrenchAnalyzer  *  * @version   0.1  */
end_comment

begin_class
DECL|class|TestThaiAnalyzer
specifier|public
class|class
name|TestThaiAnalyzer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/*  	 * testcase for offsets 	 */
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|17
block|,
literal|20
block|,
literal|23
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|17
block|,
literal|20
block|,
literal|23
block|,
literal|25
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTokenType
specifier|public
name|void
name|testTokenType
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ à¹à¹à¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|,
literal|"à¹à¹à¹"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<SOUTHEAST_ASIAN>"
block|,
literal|"<NUM>"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Thai numeric tokens were typed as<ALPHANUM> instead of<NUM>. 	 * @deprecated (3.1) testing backwards behavior  	 */
annotation|@
name|Deprecated
DECL|method|testBuggyTokenType30
specifier|public
name|void
name|testBuggyTokenType30
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ à¹à¹à¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|,
literal|"à¹à¹à¹"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|,
literal|"<ALPHANUM>"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated (3.1) testing backwards behavior */
annotation|@
name|Deprecated
DECL|method|testAnalyzer30
specifier|public
name|void
name|testAnalyzer30
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸´à¸©à¸±à¸"
block|,
literal|"à¸à¸·à¹à¸­"
block|,
literal|"xy&z"
block|,
literal|"à¸à¸¸à¸¢"
block|,
literal|"à¸à¸±à¸"
block|,
literal|"xyz@demo.com"
block|}
argument_list|)
expr_stmt|;
comment|// English stop words
name|assertAnalyzesTo
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸°à¹à¸¢à¸à¸§à¹à¸² The quick brown fox jumped over the lazy dogs"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸°à¹à¸¢à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"quick"
block|,
literal|"brown"
block|,
literal|"fox"
block|,
literal|"jumped"
block|,
literal|"over"
block|,
literal|"lazy"
block|,
literal|"dogs"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* 	 * Test that position increments are adjusted correctly for stopwords. 	 */
DECL|method|testPositionIncrements
specifier|public
name|void
name|testPositionIncrements
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸ the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|18
block|,
literal|22
block|,
literal|25
block|,
literal|28
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|22
block|,
literal|25
block|,
literal|28
block|,
literal|30
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
comment|// case that a stopword is adjacent to thai text, with no whitespace
name|assertAnalyzesTo
argument_list|(
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸the à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|17
block|,
literal|21
block|,
literal|24
block|,
literal|27
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|,
literal|6
block|,
literal|9
block|,
literal|13
block|,
literal|21
block|,
literal|24
block|,
literal|27
block|,
literal|29
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|2
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testReusableTokenStream
specifier|public
name|void
name|testReusableTokenStream
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸´à¸©à¸±à¸"
block|,
literal|"à¸à¸·à¹à¸­"
block|,
literal|"xy"
block|,
literal|"z"
block|,
literal|"à¸à¸¸à¸¢"
block|,
literal|"à¸à¸±à¸"
block|,
literal|"xyz"
block|,
literal|"demo.com"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** @deprecated (3.1) for version back compat */
annotation|@
name|Deprecated
DECL|method|testReusableTokenStream30
specifier|public
name|void
name|testReusableTokenStream30
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"JRE does not support Thai dictionary-based BreakIterator"
argument_list|,
name|ThaiWordFilter
operator|.
name|DBBI_AVAILABLE
argument_list|)
expr_stmt|;
name|ThaiAnalyzer
name|analyzer
init|=
operator|new
name|ThaiAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|""
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸²à¸£à¸à¸µà¹à¹à¸à¹à¸à¹à¸­à¸à¹à¸ªà¸à¸à¸§à¹à¸²à¸à¸²à¸à¸à¸µ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸²à¸£"
block|,
literal|"à¸à¸µà¹"
block|,
literal|"à¹à¸à¹"
block|,
literal|"à¸à¹à¸­à¸"
block|,
literal|"à¹à¸ªà¸à¸"
block|,
literal|"à¸§à¹à¸²"
block|,
literal|"à¸à¸²à¸"
block|,
literal|"à¸à¸µ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesToReuse
argument_list|(
name|analyzer
argument_list|,
literal|"à¸à¸£à¸´à¸©à¸±à¸à¸à¸·à¹à¸­ XY&Z - à¸à¸¸à¸¢à¸à¸±à¸ xyz@demo.com"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¸à¸£à¸´à¸©à¸±à¸"
block|,
literal|"à¸à¸·à¹à¸­"
block|,
literal|"xy&z"
block|,
literal|"à¸à¸¸à¸¢"
block|,
literal|"à¸à¸±à¸"
block|,
literal|"xyz@demo.com"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

