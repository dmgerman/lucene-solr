begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.br
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|br
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|tokenattributes
operator|.
name|TermAttribute
import|;
end_import

begin_comment
comment|/**  * Test the Brazilian Stem Filter, which only modifies the term text.  *   * It is very similar to the snowball portuguese algorithm but not exactly the same.  *  */
end_comment

begin_class
DECL|class|TestBrazilianStemmer
specifier|public
class|class
name|TestBrazilianStemmer
extends|extends
name|TestCase
block|{
DECL|method|testWithSnowballExamples
specifier|public
name|void
name|testWithSnowballExamples
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|"boa"
argument_list|,
literal|"boa"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boainain"
argument_list|,
literal|"boainain"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boas"
argument_list|,
literal|"boas"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bÃ´as"
argument_list|,
literal|"boas"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portugese
name|check
argument_list|(
literal|"boassu"
argument_list|,
literal|"boassu"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boataria"
argument_list|,
literal|"boat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boate"
argument_list|,
literal|"boat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boates"
argument_list|,
literal|"boat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boatos"
argument_list|,
literal|"boat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bob"
argument_list|,
literal|"bob"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boba"
argument_list|,
literal|"bob"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobagem"
argument_list|,
literal|"bobag"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobagens"
argument_list|,
literal|"bobagens"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobalhÃµes"
argument_list|,
literal|"bobalho"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portugese
name|check
argument_list|(
literal|"bobear"
argument_list|,
literal|"bob"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobeira"
argument_list|,
literal|"bobeir"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobinho"
argument_list|,
literal|"bobinh"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobinhos"
argument_list|,
literal|"bobinh"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobo"
argument_list|,
literal|"bob"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bobs"
argument_list|,
literal|"bobs"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boca"
argument_list|,
literal|"boc"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bocadas"
argument_list|,
literal|"boc"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bocadinho"
argument_list|,
literal|"bocadinh"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bocado"
argument_list|,
literal|"boc"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bocaiÃºva"
argument_list|,
literal|"bocaiuv"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portuguese
name|check
argument_list|(
literal|"boÃ§al"
argument_list|,
literal|"bocal"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portuguese
name|check
argument_list|(
literal|"bocarra"
argument_list|,
literal|"bocarr"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bocas"
argument_list|,
literal|"boc"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bode"
argument_list|,
literal|"bod"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bodoque"
argument_list|,
literal|"bodoqu"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boeing"
argument_list|,
literal|"boeing"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boem"
argument_list|,
literal|"boem"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boemia"
argument_list|,
literal|"boem"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boÃªmio"
argument_list|,
literal|"boemi"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portuguese
name|check
argument_list|(
literal|"bogotÃ¡"
argument_list|,
literal|"bogot"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"boi"
argument_list|,
literal|"boi"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"bÃ³ia"
argument_list|,
literal|"boi"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portuguese
name|check
argument_list|(
literal|"boiando"
argument_list|,
literal|"boi"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quiabo"
argument_list|,
literal|"quiab"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quicaram"
argument_list|,
literal|"quic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quickly"
argument_list|,
literal|"quickly"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quieto"
argument_list|,
literal|"quiet"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quietos"
argument_list|,
literal|"quiet"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilate"
argument_list|,
literal|"quilat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilates"
argument_list|,
literal|"quilat"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilinhos"
argument_list|,
literal|"quilinh"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilo"
argument_list|,
literal|"quil"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilombo"
argument_list|,
literal|"quilomb"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilomÃ©tricas"
argument_list|,
literal|"quilometr"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portuguese
name|check
argument_list|(
literal|"quilomÃ©tricos"
argument_list|,
literal|"quilometr"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portuguese
name|check
argument_list|(
literal|"quilÃ´metro"
argument_list|,
literal|"quilometr"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portoguese
name|check
argument_list|(
literal|"quilÃ´metros"
argument_list|,
literal|"quilometr"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portoguese
name|check
argument_list|(
literal|"quilos"
argument_list|,
literal|"quil"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimica"
argument_list|,
literal|"quimic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quilos"
argument_list|,
literal|"quil"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimica"
argument_list|,
literal|"quimic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimicas"
argument_list|,
literal|"quimic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimico"
argument_list|,
literal|"quimic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimicos"
argument_list|,
literal|"quimic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimioterapia"
argument_list|,
literal|"quimioterap"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quimioterÃ¡picos"
argument_list|,
literal|"quimioterap"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portoguese
name|check
argument_list|(
literal|"quimono"
argument_list|,
literal|"quimon"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quincas"
argument_list|,
literal|"quinc"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quinhÃ£o"
argument_list|,
literal|"quinha"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portoguese
name|check
argument_list|(
literal|"quinhentos"
argument_list|,
literal|"quinhent"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quinn"
argument_list|,
literal|"quinn"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quino"
argument_list|,
literal|"quin"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quinta"
argument_list|,
literal|"quint"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quintal"
argument_list|,
literal|"quintal"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quintana"
argument_list|,
literal|"quintan"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quintanilha"
argument_list|,
literal|"quintanilh"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quintÃ£o"
argument_list|,
literal|"quinta"
argument_list|)
expr_stmt|;
comment|// removes diacritic: different from snowball portoguese
name|check
argument_list|(
literal|"quintessÃªncia"
argument_list|,
literal|"quintessente"
argument_list|)
expr_stmt|;
comment|// versus snowball portuguese 'quintessent'
name|check
argument_list|(
literal|"quintino"
argument_list|,
literal|"quintin"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quinto"
argument_list|,
literal|"quint"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quintos"
argument_list|,
literal|"quint"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quintuplicou"
argument_list|,
literal|"quintuplic"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quinze"
argument_list|,
literal|"quinz"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quinzena"
argument_list|,
literal|"quinzen"
argument_list|)
expr_stmt|;
name|check
argument_list|(
literal|"quiosque"
argument_list|,
literal|"quiosqu"
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
specifier|final
name|String
name|input
parameter_list|,
specifier|final
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|BrazilianAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"dummy"
argument_list|,
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|TermAttribute
name|text
init|=
operator|(
name|TermAttribute
operator|)
name|stream
operator|.
name|getAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|text
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stream
operator|.
name|incrementToken
argument_list|()
argument_list|)
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

