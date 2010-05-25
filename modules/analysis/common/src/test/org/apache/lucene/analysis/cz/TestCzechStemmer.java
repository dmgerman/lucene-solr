begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.cz
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cz
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
name|util
operator|.
name|CharArraySet
import|;
end_import

begin_comment
comment|/**  * Test the Czech Stemmer.  *   * Note: its algorithmic, so some stems are nonsense  *  */
end_comment

begin_class
DECL|class|TestCzechStemmer
specifier|public
class|class
name|TestCzechStemmer
extends|extends
name|BaseTokenStreamTestCase
block|{
comment|/**    * Test showing how masculine noun forms conflate    */
DECL|method|testMasculineNouns
specifier|public
name|void
name|testMasculineNouns
parameter_list|()
throws|throws
name|IOException
block|{
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|/* animate ending with a hard consonant */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡n"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡ni"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡novÃ©"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡na"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡nÅ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡novi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡nÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡ny"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡ne"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡nech"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ¡nem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ¡n"
block|}
argument_list|)
expr_stmt|;
comment|/* inanimate ending with hard consonant */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hrad"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hradu"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hrade"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hradem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hrady"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hradech"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hradÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hradÅ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hrad"
block|}
argument_list|)
expr_stmt|;
comment|/* animate ending with a soft consonant */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾i"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾e"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾Å¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾Å¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾Ã­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"muÅ¾em"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"muh"
block|}
argument_list|)
expr_stmt|;
comment|/* inanimate ending with a soft consonant */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stroj"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stroje"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"strojÅ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stroji"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"strojÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"strojÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"strojem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stroj"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with a */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedseda"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedovÃ©"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedy"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedÅ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedovi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedu"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedo"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedech"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÅedsedou"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÅedsd"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with e */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"soudce"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"soudk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"soudci"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"soudk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"soudcÅ¯"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"soudk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"soudcÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"soudk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"soudcÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"soudk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"soudcem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"soudk"
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
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|/* ending with hard consonant */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kost"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kost"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kosti"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kost"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kostÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kost"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kostem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kost"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kostech"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kost"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kostmi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kost"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with a soft consonant */
comment|// note: in this example sing nom. and sing acc. don't conflate w/ the rest
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ­seÅ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ­sÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ­snÄ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ­sn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ­sni"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ­sn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ­snÄmi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ­sn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ­snÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ­sn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"pÃ­snÃ­m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"pÃ­sn"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with e */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"rÅ¯Å¾e"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rÅ¯h"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"rÅ¯Å¾Ã­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rÅ¯h"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"rÅ¯Å¾Ã­m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rÅ¯h"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"rÅ¯Å¾Ã­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rÅ¯h"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"rÅ¯Å¾emi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rÅ¯h"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"rÅ¯Å¾i"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"rÅ¯h"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with a */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾ena"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾eny"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾en"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾enÄ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾enÃ¡m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾enu"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾eno"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾enÃ¡ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾enou"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"Å¾enami"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Å¾n"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test showing how neuter noun forms conflate    */
DECL|method|testNeuterNouns
specifier|public
name|void
name|testNeuterNouns
parameter_list|()
throws|throws
name|IOException
block|{
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|/* ending with o */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄsto"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄsta"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄst"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄstu"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄstÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄstÄ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄstech"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄstem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mÄsty"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mÄst"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with e */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"moÅe"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"moÅÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"moÅÃ­m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"moÅi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"moÅÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"moÅem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"moÅ"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with Ä */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅe"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅata"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅete"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅeti"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅatÅ¯m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅatech"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅetem"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kuÅaty"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kuÅ"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with Ã­ */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stavenÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stavn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stavenÃ­m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stavn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stavenÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stavn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"stavenÃ­mi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"stavn"
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
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|/* ending with Ã½/Ã¡/Ã© */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ©ho"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ½ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ©mu"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ½m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ©"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ©m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ½mi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladÃ¡"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mladou"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mlad"
block|}
argument_list|)
expr_stmt|;
comment|/* ending with Ã­ */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jarnÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jarn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jarnÃ­ho"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jarn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jarnÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jarn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jarnÃ­mu"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jarn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jarnÃ­m"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jarn"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jarnÃ­mi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jarn"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test some possessive suffixes    */
DECL|method|testPossessive
specifier|public
name|void
name|testPossessive
parameter_list|()
throws|throws
name|IOException
block|{
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"KarlÅ¯v"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"karl"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"jazykovÃ½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"jazyk"
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
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
comment|/* rewrite of Å¡t -> sk */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"ÄeskÃ½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Äesk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"ÄeÅ¡tÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Äesk"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of Ät -> ck */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"anglickÃ½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"anglick"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"angliÄtÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"anglick"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of z -> h */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kniha"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"knih"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"knize"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"knih"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of Å¾ -> h */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"mazat"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mah"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"maÅ¾u"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mah"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of c -> k */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kluk"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kluk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"kluci"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kluk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"klucÃ­ch"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"kluk"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of Ä -> k */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hezkÃ½"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hezk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hezÄÃ­"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hezk"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of *Å¯* -> *o* */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hÅ¯l"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hol"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"hole"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"hol"
block|}
argument_list|)
expr_stmt|;
comment|/* rewrite of e* -> * */
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"deska"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"desk"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"desek"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"desk"
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that very short words are not stemmed.    */
DECL|method|testDontStem
specifier|public
name|void
name|testDontStem
parameter_list|()
throws|throws
name|IOException
block|{
name|CzechAnalyzer
name|cz
init|=
operator|new
name|CzechAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"e"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"e"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|cz
argument_list|,
literal|"zi"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"zi"
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
name|TEST_VERSION_CURRENT
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
literal|"hole"
argument_list|)
expr_stmt|;
name|CzechStemFilter
name|filter
init|=
operator|new
name|CzechStemFilter
argument_list|(
operator|new
name|KeywordMarkerFilter
argument_list|(
operator|new
name|WhitespaceTokenizer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"hole desek"
argument_list|)
argument_list|)
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
literal|"hole"
block|,
literal|"desk"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

