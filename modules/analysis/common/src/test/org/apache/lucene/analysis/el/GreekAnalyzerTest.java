begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.el
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|el
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/**  * A unit test class for verifying the correct operation of the GreekAnalyzer.  *  */
end_comment

begin_class
DECL|class|GreekAnalyzerTest
specifier|public
class|class
name|GreekAnalyzerTest
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Test the analysis of various greek strings.    *    * @throws Exception in case an error occurs    */
DECL|method|testAnalyzer
specifier|public
name|void
name|testAnalyzer
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|GreekAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// Verify the correct analysis of capitals and small accented letters, and
comment|// stemming
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÎ¯Î± ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ¬ ÎºÎ±Î»Î® ÎºÎ±Î¹ ÏÎ»Î¿ÏÏÎ¹Î± ÏÎµÎ¹ÏÎ¬ ÏÎ±ÏÎ±ÎºÏÎ®ÏÏÎ½ ÏÎ·Ï ÎÎ»Î»Î·Î½Î¹ÎºÎ®Ï Î³Î»ÏÏÏÎ±Ï"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î¹Î±"
block|,
literal|"ÎµÎ¾Î±Î¹ÏÎµÏ"
block|,
literal|"ÎºÎ±Î»"
block|,
literal|"ÏÎ»Î¿ÏÏ"
block|,
literal|"ÏÎµÎ¹Ï"
block|,
literal|"ÏÎ±ÏÎ±ÎºÏÎ·Ï"
block|,
literal|"ÎµÎ»Î»Î·Î½Î¹Îº"
block|,
literal|"Î³Î»ÏÏÏ"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of small letters with diaeresis and the elimination
comment|// of punctuation marks
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Î ÏÎ¿ÏÏÎ½ÏÎ± (ÎºÎ±Î¹)     [ÏÎ¿Î»Î»Î±ÏÎ»Î­Ï] - ÎÎÎÎÎÎÎ£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÏÏÎ¿Î¹Î¿Î½Ï"
block|,
literal|"ÏÎ¿Î»Î»Î±ÏÎ»"
block|,
literal|"Î±Î½Î±Î³Îº"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of capital accented letters and capital letters with diaeresis,
comment|// as well as the elimination of stop words
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Î Î¡ÎÎ«Î ÎÎÎÎ£ÎÎÎ£  ÎÏÎ¿Î³Î¿Ï, Î¿ Î¼ÎµÏÏÏÏ ÎºÎ±Î¹ Î¿Î¹ Î¬Î»Î»Î¿Î¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÏÏÎ¿ÏÏÎ¿Î¸ÎµÏ"
block|,
literal|"Î±ÏÎ¿Î³"
block|,
literal|"Î¼ÎµÏÏ"
block|,
literal|"Î±Î»Î»"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Test the analysis of various greek strings. 	 * 	 * @throws Exception in case an error occurs 	 * @deprecated Remove this test when support for 3.0 is no longer needed 	 */
annotation|@
name|Deprecated
DECL|method|testAnalyzerBWCompat
specifier|public
name|void
name|testAnalyzerBWCompat
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|GreekAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
comment|// Verify the correct analysis of capitals and small accented letters
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÎ¯Î± ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ¬ ÎºÎ±Î»Î® ÎºÎ±Î¹ ÏÎ»Î¿ÏÏÎ¹Î± ÏÎµÎ¹ÏÎ¬ ÏÎ±ÏÎ±ÎºÏÎ®ÏÏÎ½ ÏÎ·Ï ÎÎ»Î»Î·Î½Î¹ÎºÎ®Ï Î³Î»ÏÏÏÎ±Ï"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î¹Î±"
block|,
literal|"ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ±"
block|,
literal|"ÎºÎ±Î»Î·"
block|,
literal|"ÏÎ»Î¿ÏÏÎ¹Î±"
block|,
literal|"ÏÎµÎ¹ÏÎ±"
block|,
literal|"ÏÎ±ÏÎ±ÎºÏÎ·ÏÏÎ½"
block|,
literal|"ÎµÎ»Î»Î·Î½Î¹ÎºÎ·Ï"
block|,
literal|"Î³Î»ÏÏÏÎ±Ï"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of small letters with diaeresis and the elimination
comment|// of punctuation marks
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Î ÏÎ¿ÏÏÎ½ÏÎ± (ÎºÎ±Î¹)     [ÏÎ¿Î»Î»Î±ÏÎ»Î­Ï] - ÎÎÎÎÎÎÎ£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÏÏÎ¿Î¹Î¿Î½ÏÎ±"
block|,
literal|"ÏÎ¿Î»Î»Î±ÏÎ»ÎµÏ"
block|,
literal|"Î±Î½Î±Î³ÎºÎµÏ"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of capital accented letters and capital letters with diaeresis,
comment|// as well as the elimination of stop words
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Î Î¡ÎÎ«Î ÎÎÎÎ£ÎÎÎ£  ÎÏÎ¿Î³Î¿Ï, Î¿ Î¼ÎµÏÏÏÏ ÎºÎ±Î¹ Î¿Î¹ Î¬Î»Î»Î¿Î¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÏÏÎ¿ÏÏÎ¿Î¸ÎµÏÎµÎ¹Ï"
block|,
literal|"Î±ÏÎ¿Î³Î¿Ï"
block|,
literal|"Î¼ÎµÏÏÎ¿Ï"
block|,
literal|"Î±Î»Î»Î¿Î¹"
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
name|Analyzer
name|a
init|=
operator|new
name|GreekAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|// Verify the correct analysis of capitals and small accented letters, and
comment|// stemming
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"ÎÎ¯Î± ÎµÎ¾Î±Î¹ÏÎµÏÎ¹ÎºÎ¬ ÎºÎ±Î»Î® ÎºÎ±Î¹ ÏÎ»Î¿ÏÏÎ¹Î± ÏÎµÎ¹ÏÎ¬ ÏÎ±ÏÎ±ÎºÏÎ®ÏÏÎ½ ÏÎ·Ï ÎÎ»Î»Î·Î½Î¹ÎºÎ®Ï Î³Î»ÏÏÏÎ±Ï"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î¹Î±"
block|,
literal|"ÎµÎ¾Î±Î¹ÏÎµÏ"
block|,
literal|"ÎºÎ±Î»"
block|,
literal|"ÏÎ»Î¿ÏÏ"
block|,
literal|"ÏÎµÎ¹Ï"
block|,
literal|"ÏÎ±ÏÎ±ÎºÏÎ·Ï"
block|,
literal|"ÎµÎ»Î»Î·Î½Î¹Îº"
block|,
literal|"Î³Î»ÏÏÏ"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of small letters with diaeresis and the elimination
comment|// of punctuation marks
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"Î ÏÎ¿ÏÏÎ½ÏÎ± (ÎºÎ±Î¹)     [ÏÎ¿Î»Î»Î±ÏÎ»Î­Ï] - ÎÎÎÎÎÎÎ£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÏÏÎ¿Î¹Î¿Î½Ï"
block|,
literal|"ÏÎ¿Î»Î»Î±ÏÎ»"
block|,
literal|"Î±Î½Î±Î³Îº"
block|}
argument_list|)
expr_stmt|;
comment|// Verify the correct analysis of capital accented letters and capital letters with diaeresis,
comment|// as well as the elimination of stop words
name|assertAnalyzesToReuse
argument_list|(
name|a
argument_list|,
literal|"Î Î¡ÎÎ«Î ÎÎÎÎ£ÎÎÎ£  ÎÏÎ¿Î³Î¿Ï, Î¿ Î¼ÎµÏÏÏÏ ÎºÎ±Î¹ Î¿Î¹ Î¬Î»Î»Î¿Î¹"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ÏÏÎ¿ÏÏÎ¿Î¸ÎµÏ"
block|,
literal|"Î±ÏÎ¿Î³"
block|,
literal|"Î¼ÎµÏÏ"
block|,
literal|"Î±Î»Î»"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Greek Analyzer didn't call standardFilter, so no normalization of acronyms. 	 * check that this is preserved. 	 * @deprecated remove this test in Lucene 4.0 	 */
annotation|@
name|Deprecated
DECL|method|testAcronymBWCompat
specifier|public
name|void
name|testAcronymBWCompat
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|a
init|=
operator|new
name|GreekAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Î.Î .Î¤."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î±.Ï.Ï."
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

