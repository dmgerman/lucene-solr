begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.ckb
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ckb
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|VocabularyAssert
operator|.
name|assertVocabulary
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
name|core
operator|.
name|KeywordTokenizer
import|;
end_import

begin_comment
comment|/**  * Test the Sorani Stemmer.  */
end_comment

begin_class
DECL|class|TestSoraniStemFilter
specifier|public
class|class
name|TestSoraniStemFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
name|Analyzer
name|a
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
name|a
operator|=
operator|new
name|SoraniAnalyzer
argument_list|()
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
name|a
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
DECL|method|testIndefiniteSingular
specifier|public
name|void
name|testIndefiniteSingular
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙÛÚ©"
argument_list|,
literal|"Ù¾ÛØ§Ù"
argument_list|)
expr_stmt|;
comment|// -ek
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§ÛÛÚ©"
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§"
argument_list|)
expr_stmt|;
comment|// -yek
block|}
DECL|method|testDefiniteSingular
specifier|public
name|void
name|testDefiniteSingular
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙÛÙÛ"
argument_list|,
literal|"Ù¾ÛØ§Ù"
argument_list|)
expr_stmt|;
comment|// -aka
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§ÙÛ"
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§"
argument_list|)
expr_stmt|;
comment|// -ka
block|}
DECL|method|testDemonstrativeSingular
specifier|public
name|void
name|testDemonstrativeSingular
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ú©ØªØ§ÙÛÛ"
argument_list|,
literal|"Ú©ØªØ§ÙÛ"
argument_list|)
expr_stmt|;
comment|// -a
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§ÛÛ"
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§"
argument_list|)
expr_stmt|;
comment|// -ya
block|}
DECL|method|testIndefinitePlural
specifier|public
name|void
name|testIndefinitePlural
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙØ§Ù"
argument_list|,
literal|"Ù¾ÛØ§Ù"
argument_list|)
expr_stmt|;
comment|// -An
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§ÛØ§Ù"
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§"
argument_list|)
expr_stmt|;
comment|// -yAn
block|}
DECL|method|testDefinitePlural
specifier|public
name|void
name|testDefinitePlural
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙÛÚ©Ø§Ù"
argument_list|,
literal|"Ù¾ÛØ§Ù"
argument_list|)
expr_stmt|;
comment|// -akAn
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§Ú©Ø§Ù"
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§"
argument_list|)
expr_stmt|;
comment|// -kAn
block|}
DECL|method|testDemonstrativePlural
specifier|public
name|void
name|testDemonstrativePlural
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾ÛØ§ÙØ§ÙÛ"
argument_list|,
literal|"Ù¾ÛØ§Ù"
argument_list|)
expr_stmt|;
comment|// -Ana
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§ÛØ§ÙÛ"
argument_list|,
literal|"Ø¯ÛØ±Ú¯Ø§"
argument_list|)
expr_stmt|;
comment|// -yAna
block|}
DECL|method|testEzafe
specifier|public
name|void
name|testEzafe
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ÙÛØªÛÙÛ"
argument_list|,
literal|"ÙÛØªÛÙ"
argument_list|)
expr_stmt|;
comment|// singular
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ÙÛØªÛÙÛÚ©Û"
argument_list|,
literal|"ÙÛØªÛÙ"
argument_list|)
expr_stmt|;
comment|// indefinite
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ÙÛØªÛÙØ§ÙÛ"
argument_list|,
literal|"ÙÛØªÛÙ"
argument_list|)
expr_stmt|;
comment|// plural
block|}
DECL|method|testPostpositions
specifier|public
name|void
name|testPostpositions
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø¯ÙÙØ±ÛÙÛ"
argument_list|,
literal|"Ø¯ÙÙØ±"
argument_list|)
expr_stmt|;
comment|// -awa
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ÙÛÙÛØ´ÛÙØ¯Ø§"
argument_list|,
literal|"ÙÛÙÛØ´ÛÙ"
argument_list|)
expr_stmt|;
comment|// -dA
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ø³ÛØ±Ø§ÙØ§"
argument_list|,
literal|"Ø³ÛØ±Ø§Ù"
argument_list|)
expr_stmt|;
comment|// -A
block|}
DECL|method|testPossessives
specifier|public
name|void
name|testPossessives
parameter_list|()
throws|throws
name|Exception
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾Ø§Ø±ÛÙØ§Ù"
argument_list|,
literal|"Ù¾Ø§Ø±Û"
argument_list|)
expr_stmt|;
comment|// -mAn
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾Ø§Ø±ÛØªØ§Ù"
argument_list|,
literal|"Ù¾Ø§Ø±Û"
argument_list|)
expr_stmt|;
comment|// -tAn
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"Ù¾Ø§Ø±ÛÛØ§Ù"
argument_list|,
literal|"Ù¾Ø§Ø±Û"
argument_list|)
expr_stmt|;
comment|// -yAn
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
name|SoraniStemFilter
argument_list|(
name|tokenizer
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
comment|/** test against a basic vocabulary file */
DECL|method|testVocabulary
specifier|public
name|void
name|testVocabulary
parameter_list|()
throws|throws
name|Exception
block|{
comment|// top 8k words or so: freq> 1000
name|assertVocabulary
argument_list|(
name|a
argument_list|,
name|getDataPath
argument_list|(
literal|"ckbtestdata.zip"
argument_list|)
argument_list|,
literal|"testdata.txt"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

