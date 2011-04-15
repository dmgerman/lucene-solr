begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.lv
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|lv
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
name|util
operator|.
name|ReusableAnalyzerBase
import|;
end_import

begin_comment
comment|/**  * Basic tests for {@link LatvianStemmer}  */
end_comment

begin_class
DECL|class|TestLatvianStemmer
specifier|public
class|class
name|TestLatvianStemmer
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|a
specifier|private
name|Analyzer
name|a
init|=
operator|new
name|ReusableAnalyzerBase
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
name|LatvianStemFilter
argument_list|(
name|tokenizer
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|method|testNouns1
specifier|public
name|void
name|testNouns1
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decl. I
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvs"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvi"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄva"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvu"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvam"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄviem"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvu"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvus"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvÄ"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvos"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvs"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"tÄvi"
argument_list|,
literal|"tÄv"
argument_list|)
expr_stmt|;
comment|// voc. pl.
block|}
comment|/**    * decl II nouns with (s,t) -> Å¡ and (d,z) -> Å¾    * palatalization will generally conflate to two stems    * due to the ambiguity (plural and singular).    */
DECL|method|testNouns2
specifier|public
name|void
name|testNouns2
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decl. II
comment|// c -> Ä palatalization
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄcis"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄi"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄa"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄu"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄcim"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄiem"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄci"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄus"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄcÄ«"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄos"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄci"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lÄÄi"
argument_list|,
literal|"lÄc"
argument_list|)
expr_stmt|;
comment|// voc. pl.
comment|// n -> Å palatalization
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmens"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeÅi"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmens"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeÅu"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmenim"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeÅiem"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeni"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeÅus"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmenÄ«"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeÅos"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmens"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"akmeÅi"
argument_list|,
literal|"akmen"
argument_list|)
expr_stmt|;
comment|// voc. pl.
comment|// no palatalization
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmis"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmji"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmja"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmju"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmim"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmjiem"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmi"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmjus"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmÄ«"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmjos"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmi"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kurmji"
argument_list|,
literal|"kurm"
argument_list|)
expr_stmt|;
comment|// voc. pl.
block|}
DECL|method|testNouns3
specifier|public
name|void
name|testNouns3
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decl III
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietus"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lieti"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietus"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietu"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietum"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietiem"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietu"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietus"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietÅ«"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietos"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lietus"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lieti"
argument_list|,
literal|"liet"
argument_list|)
expr_stmt|;
comment|// voc. pl.
block|}
DECL|method|testNouns4
specifier|public
name|void
name|testNouns4
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decl IV
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapa"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapas"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapas"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapu"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapai"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapÄm"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapu"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapas"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapÄ"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapÄs"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapa"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lapas"
argument_list|,
literal|"lap"
argument_list|)
expr_stmt|;
comment|// voc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puika"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikas"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikas"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puiku"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikam"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikÄm"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puiku"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikas"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikÄ"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikÄs"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puika"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"puikas"
argument_list|,
literal|"puik"
argument_list|)
expr_stmt|;
comment|// voc. pl.
block|}
comment|/**    * Genitive plural forms with (s,t) -> Å¡ and (d,z) -> Å¾    * will not conflate due to ambiguity.    */
DECL|method|testNouns5
specifier|public
name|void
name|testNouns5
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decl V
comment|// l -> Ä¼ palatalization
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egle"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egles"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egles"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egÄ¼u"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"eglei"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"eglÄm"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egli"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egles"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"eglÄ"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"eglÄs"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egle"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"egles"
argument_list|,
literal|"egl"
argument_list|)
expr_stmt|;
comment|// voc. pl.
block|}
DECL|method|testNouns6
specifier|public
name|void
name|testNouns6
parameter_list|()
throws|throws
name|IOException
block|{
comment|// decl VI
comment|// no palatalization
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govs"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govis"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// nom. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govs"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// gen. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govju"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govij"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// dat. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govÄ«m"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// dat. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govi "
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// acc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govis"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// acc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govi "
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// inst. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govÄ«m"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// inst. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govÄ«"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// loc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govÄ«s"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// loc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govs"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// voc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"govis"
argument_list|,
literal|"gov"
argument_list|)
expr_stmt|;
comment|// voc. pl.
block|}
DECL|method|testAdjectives
specifier|public
name|void
name|testAdjectives
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zils"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. nom. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilais"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. nom. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zili"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. nom. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilie"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. nom. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zila"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. nom. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. nom. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilas"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. nom. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. nom. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zila"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. gen. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. gen. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilu"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. gen. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilo"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. gen. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilas"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. gen. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. gen. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilu"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. gen. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilo"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. gen. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilam"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. dat. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajam"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. dat. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ziliem"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. dat. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajiem"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. dat. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilai"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. dat. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajai"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. dat. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄm"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. dat. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajÄm"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. dat. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilu"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. acc. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilo"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. acc. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilus"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. acc. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilos"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. acc. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilu"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. acc. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilo"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. acc. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. acc. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. acc. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. loc. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. loc. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilos"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. loc. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajos"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. loc. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. loc. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. loc. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// indef. loc. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilajÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// def. loc. fem. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilais"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// voc. masc. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilie"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// voc. masc. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄ"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// voc. fem. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zilÄs"
argument_list|,
literal|"zil"
argument_list|)
expr_stmt|;
comment|// voc. fem. pl.
block|}
comment|/**    * Note: we intentionally don't handle the ambiguous    * (s,t) -> Å¡ and (d,z) -> Å¾    */
DECL|method|testPalatalization
specifier|public
name|void
name|testPalatalization
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"krÄsns"
argument_list|,
literal|"krÄsn"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"krÄÅ¡Åu"
argument_list|,
literal|"krÄsn"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zvaigzne"
argument_list|,
literal|"zvaigzn"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zvaigÅ¾Åu"
argument_list|,
literal|"zvaigzn"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kÄpslis"
argument_list|,
literal|"kÄpsl"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"kÄpÅ¡Ä¼u"
argument_list|,
literal|"kÄpsl"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"zizlis"
argument_list|,
literal|"zizl"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"ziÅ¾Ä¼u"
argument_list|,
literal|"zizl"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"vilnis"
argument_list|,
literal|"viln"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"viÄ¼Åu"
argument_list|,
literal|"viln"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"lelle"
argument_list|,
literal|"lell"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"leÄ¼Ä¼u"
argument_list|,
literal|"lell"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"pinne"
argument_list|,
literal|"pinn"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"piÅÅu"
argument_list|,
literal|"pinn"
argument_list|)
expr_stmt|;
comment|// gen. pl.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"rÄ«kste"
argument_list|,
literal|"rÄ«kst"
argument_list|)
expr_stmt|;
comment|// nom. sing.
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"rÄ«kÅ¡u"
argument_list|,
literal|"rÄ«kst"
argument_list|)
expr_stmt|;
comment|// gen. pl.
block|}
comment|/**    * Test some length restrictions, we require a 3+ char stem,    * with at least one vowel.    */
DECL|method|testLength
specifier|public
name|void
name|testLength
parameter_list|()
throws|throws
name|IOException
block|{
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"usa"
argument_list|,
literal|"usa"
argument_list|)
expr_stmt|;
comment|// length
name|checkOneTerm
argument_list|(
name|a
argument_list|,
literal|"60ms"
argument_list|,
literal|"60ms"
argument_list|)
expr_stmt|;
comment|// vowel count
block|}
block|}
end_class

end_unit

