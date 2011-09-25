begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
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
name|Reader
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
name|*
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
name|WhitespaceTokenizer
import|;
end_import

begin_comment
comment|/**  * Tests ICUFoldingFilter  */
end_comment

begin_class
DECL|class|TestICUFoldingFilter
specifier|public
class|class
name|TestICUFoldingFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
name|Analyzer
name|a
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
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
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
operator|new
name|ICUFoldingFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|IOException
block|{
comment|// case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"This is a test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"this"
block|,
literal|"is"
block|,
literal|"a"
block|,
literal|"test"
block|}
argument_list|)
expr_stmt|;
comment|// case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"RuÃ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"russ"
block|}
argument_list|)
expr_stmt|;
comment|// case folding with accent removal
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÎÎªÎÎ£"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î±Î¹Î¿Ï"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ÎÎ¬ÏÎ¿Ï"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Î¼Î±Î¹Î¿Ï"
block|}
argument_list|)
expr_stmt|;
comment|// supplementary case folding
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ð"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"ð¾"
block|}
argument_list|)
expr_stmt|;
comment|// normalization
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ï´³ï´ºï°§"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Ø·ÙØ·ÙØ·Ù"
block|}
argument_list|)
expr_stmt|;
comment|// removal of default ignorables
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"à¤à¥âà¤·"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"à¤à¤·"
block|}
argument_list|)
expr_stmt|;
comment|// removal of latin accents (composed)
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"rÃ©sumÃ©"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"resume"
block|}
argument_list|)
expr_stmt|;
comment|// removal of latin accents (decomposed)
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"re\u0301sume\u0301"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"resume"
block|}
argument_list|)
expr_stmt|;
comment|// fold native digits
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"à§­à§¦à§¬"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"706"
block|}
argument_list|)
expr_stmt|;
comment|// ascii-folding-filter type stuff
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"Äis is crÃ¦zy"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dis"
block|,
literal|"is"
block|,
literal|"craezy"
block|}
argument_list|)
expr_stmt|;
comment|// proper downcasing of Turkish dotted-capital I
comment|// (according to default case folding rules)
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"ELÄ°F"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"elif"
block|}
argument_list|)
expr_stmt|;
comment|// handling of decomposed combining-dot-above
name|assertAnalyzesTo
argument_list|(
name|a
argument_list|,
literal|"eli\u0307f"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"elif"
block|}
argument_list|)
expr_stmt|;
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
name|checkRandomData
argument_list|(
name|random
argument_list|,
name|a
argument_list|,
literal|10000
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

