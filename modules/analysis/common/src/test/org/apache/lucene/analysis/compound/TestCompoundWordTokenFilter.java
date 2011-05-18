begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.compound
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
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
name|StringReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|compound
operator|.
name|hyphenation
operator|.
name|HyphenationTree
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

begin_class
DECL|class|TestCompoundWordTokenFilter
specifier|public
class|class
name|TestCompoundWordTokenFilter
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|method|testHyphenationCompoundWordsDA
specifier|public
name|void
name|testHyphenationCompoundWordsDA
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"lÃ¦se"
block|,
literal|"hest"
block|}
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"min veninde som er lidt af en lÃ¦sehest"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"min"
block|,
literal|"veninde"
block|,
literal|"som"
block|,
literal|"er"
block|,
literal|"lidt"
block|,
literal|"af"
block|,
literal|"en"
block|,
literal|"lÃ¦sehest"
block|,
literal|"lÃ¦se"
block|,
literal|"hest"
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
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyphenationCompoundWordsDELongestMatch
specifier|public
name|void
name|testHyphenationCompoundWordsDELongestMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"basketball"
block|,
literal|"basket"
block|,
literal|"ball"
block|,
literal|"kurv"
block|}
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|// the word basket will not be added due to the longest match option
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
literal|40
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"basketball"
block|,
literal|"ball"
block|,
literal|"kurv"
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
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * With hyphenation-only, you can get a lot of nonsense tokens.    * This can be controlled with the min/max subword size.    */
DECL|method|testHyphenationOnly
specifier|public
name|void
name|testHyphenationOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"da_UTF8.xml"
argument_list|)
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
name|HyphenationTree
name|hyphenator
init|=
name|HyphenationCompoundWordTokenFilter
operator|.
name|getHyphenationTree
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|HyphenationCompoundWordTokenFilter
name|tf
init|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// min=2, max=4
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"ba"
block|,
literal|"sket"
block|,
literal|"bal"
block|,
literal|"ball"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|// min=4, max=6
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"basket"
block|,
literal|"sket"
block|,
literal|"ball"
block|,
literal|"lkurv"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|HyphenationCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"basketballkurv"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|hyphenator
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
literal|4
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// min=4, max=10
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"basketballkurv"
block|,
literal|"basket"
block|,
literal|"basketbal"
block|,
literal|"basketball"
block|,
literal|"sket"
block|,
literal|"sketbal"
block|,
literal|"sketball"
block|,
literal|"ball"
block|,
literal|"ballkurv"
block|,
literal|"lkurv"
block|,
literal|"kurv"
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDumbCompoundWordsSE
specifier|public
name|void
name|testDumbCompoundWordsSE
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Bil"
block|,
literal|"DÃ¶rr"
block|,
literal|"Motor"
block|,
literal|"Tak"
block|,
literal|"Borr"
block|,
literal|"Slag"
block|,
literal|"Hammar"
block|,
literal|"Pelar"
block|,
literal|"Glas"
block|,
literal|"Ãgon"
block|,
literal|"Fodral"
block|,
literal|"Bas"
block|,
literal|"Fiol"
block|,
literal|"Makare"
block|,
literal|"GesÃ¤ll"
block|,
literal|"Sko"
block|,
literal|"Vind"
block|,
literal|"Rute"
block|,
literal|"Torkare"
block|,
literal|"Blad"
block|}
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BildÃ¶rr Bilmotor Biltak Slagborr Hammarborr Pelarborr GlasÃ¶gonfodral Basfiolsfodral BasfiolsfodralmakaregesÃ¤ll Skomakare Vindrutetorkare Vindrutetorkarblad abba"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|dict
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BildÃ¶rr"
block|,
literal|"Bil"
block|,
literal|"dÃ¶rr"
block|,
literal|"Bilmotor"
block|,
literal|"Bil"
block|,
literal|"motor"
block|,
literal|"Biltak"
block|,
literal|"Bil"
block|,
literal|"tak"
block|,
literal|"Slagborr"
block|,
literal|"Slag"
block|,
literal|"borr"
block|,
literal|"Hammarborr"
block|,
literal|"Hammar"
block|,
literal|"borr"
block|,
literal|"Pelarborr"
block|,
literal|"Pelar"
block|,
literal|"borr"
block|,
literal|"GlasÃ¶gonfodral"
block|,
literal|"Glas"
block|,
literal|"Ã¶gon"
block|,
literal|"fodral"
block|,
literal|"Basfiolsfodral"
block|,
literal|"Bas"
block|,
literal|"fiol"
block|,
literal|"fodral"
block|,
literal|"BasfiolsfodralmakaregesÃ¤ll"
block|,
literal|"Bas"
block|,
literal|"fiol"
block|,
literal|"fodral"
block|,
literal|"makare"
block|,
literal|"gesÃ¤ll"
block|,
literal|"Skomakare"
block|,
literal|"Sko"
block|,
literal|"makare"
block|,
literal|"Vindrutetorkare"
block|,
literal|"Vind"
block|,
literal|"rute"
block|,
literal|"torkare"
block|,
literal|"Vindrutetorkarblad"
block|,
literal|"Vind"
block|,
literal|"rute"
block|,
literal|"blad"
block|,
literal|"abba"
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
literal|3
block|,
literal|8
block|,
literal|8
block|,
literal|11
block|,
literal|17
block|,
literal|17
block|,
literal|20
block|,
literal|24
block|,
literal|24
block|,
literal|28
block|,
literal|33
block|,
literal|33
block|,
literal|39
block|,
literal|44
block|,
literal|44
block|,
literal|49
block|,
literal|54
block|,
literal|54
block|,
literal|58
block|,
literal|62
block|,
literal|69
block|,
literal|69
block|,
literal|72
block|,
literal|77
block|,
literal|84
block|,
literal|84
block|,
literal|87
block|,
literal|92
block|,
literal|98
block|,
literal|104
block|,
literal|111
block|,
literal|111
block|,
literal|114
block|,
literal|121
block|,
literal|121
block|,
literal|125
block|,
literal|129
block|,
literal|137
block|,
literal|137
block|,
literal|141
block|,
literal|151
block|,
literal|156
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|3
block|,
literal|7
block|,
literal|16
block|,
literal|11
block|,
literal|16
block|,
literal|23
block|,
literal|20
block|,
literal|23
block|,
literal|32
block|,
literal|28
block|,
literal|32
block|,
literal|43
block|,
literal|39
block|,
literal|43
block|,
literal|53
block|,
literal|49
block|,
literal|53
block|,
literal|68
block|,
literal|58
block|,
literal|62
block|,
literal|68
block|,
literal|83
block|,
literal|72
block|,
literal|76
block|,
literal|83
block|,
literal|110
block|,
literal|87
block|,
literal|91
block|,
literal|98
block|,
literal|104
block|,
literal|110
block|,
literal|120
block|,
literal|114
block|,
literal|120
block|,
literal|136
block|,
literal|125
block|,
literal|129
block|,
literal|136
block|,
literal|155
block|,
literal|141
block|,
literal|145
block|,
literal|155
block|,
literal|160
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
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
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
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDumbCompoundWordsSELongestMatch
specifier|public
name|void
name|testDumbCompoundWordsSELongestMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Bil"
block|,
literal|"DÃ¶rr"
block|,
literal|"Motor"
block|,
literal|"Tak"
block|,
literal|"Borr"
block|,
literal|"Slag"
block|,
literal|"Hammar"
block|,
literal|"Pelar"
block|,
literal|"Glas"
block|,
literal|"Ãgon"
block|,
literal|"Fodral"
block|,
literal|"Bas"
block|,
literal|"Fiols"
block|,
literal|"Makare"
block|,
literal|"GesÃ¤ll"
block|,
literal|"Sko"
block|,
literal|"Vind"
block|,
literal|"Rute"
block|,
literal|"Torkare"
block|,
literal|"Blad"
block|,
literal|"Fiolsfodral"
block|}
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockTokenizer
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"BasfiolsfodralmakaregesÃ¤ll"
argument_list|)
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|tf
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"BasfiolsfodralmakaregesÃ¤ll"
block|,
literal|"Bas"
block|,
literal|"fiolsfodral"
block|,
literal|"fodral"
block|,
literal|"makare"
block|,
literal|"gesÃ¤ll"
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
literal|3
block|,
literal|8
block|,
literal|14
block|,
literal|20
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|26
block|,
literal|3
block|,
literal|14
block|,
literal|14
block|,
literal|20
block|,
literal|26
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
block|}
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|dict
init|=
block|{
literal|"Rind"
block|,
literal|"Fleisch"
block|,
literal|"Draht"
block|,
literal|"Schere"
block|,
literal|"Gesetz"
block|,
literal|"Aufgabe"
block|,
literal|"Ãberwachung"
block|}
decl_stmt|;
name|Tokenizer
name|wsTokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|)
argument_list|)
decl_stmt|;
name|DictionaryCompoundWordTokenFilter
name|tf
init|=
operator|new
name|DictionaryCompoundWordTokenFilter
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|wsTokenizer
argument_list|,
name|dict
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_WORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MIN_SUBWORD_SIZE
argument_list|,
name|CompoundWordTokenFilterBase
operator|.
name|DEFAULT_MAX_SUBWORD_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|CharTermAttribute
name|termAtt
init|=
name|tf
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Rind"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|wsTokenizer
operator|.
name|reset
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|tf
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"RindfleischÃ¼berwachungsgesetz"
argument_list|,
name|termAtt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

