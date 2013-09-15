begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SpellingParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestAnalyzeInfixSuggestions
specifier|public
class|class
name|TestAnalyzeInfixSuggestions
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|URI_DEFAULT
specifier|static
specifier|final
name|String
name|URI_DEFAULT
init|=
literal|"/infix_suggest_analyzing"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-phrasesuggest.xml"
argument_list|,
literal|"schema-phrasesuggest.xml"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI_DEFAULT
argument_list|,
literal|"q"
argument_list|,
literal|""
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingle
specifier|public
name|void
name|testSingle
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI_DEFAULT
argument_list|,
literal|"q"
argument_list|,
literal|"japan"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/int[@name='numFound'][.='1']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[1][.='<b>Japan</b>ese Autocomplete and<b>Japan</b>ese Highlighter broken']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI_DEFAULT
argument_list|,
literal|"q"
argument_list|,
literal|"high"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='high']/int[@name='numFound'][.='1']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='high']/arr[@name='suggestion']/str[1][.='Japanese Autocomplete and Japanese<b>High</b>lighter broken']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiple
specifier|public
name|void
name|testMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI_DEFAULT
argument_list|,
literal|"q"
argument_list|,
literal|"japan"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[1][.='<b>Japan</b>ese Autocomplete and<b>Japan</b>ese Highlighter broken']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[2][.='Add<b>Japan</b>ese Kanji number normalization to Kuromoji']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI_DEFAULT
argument_list|,
literal|"q"
argument_list|,
literal|"japan"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[1][.='<b>Japan</b>ese Autocomplete and<b>Japan</b>ese Highlighter broken']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[2][.='Add<b>Japan</b>ese Kanji number normalization to Kuromoji']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[3][.='Add decompose compound<b>Japan</b>ese Katakana token capability to Kuromoji']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|URI_DEFAULT
argument_list|,
literal|"q"
argument_list|,
literal|"japan"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"4"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/int[@name='numFound'][.='3']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[1][.='<b>Japan</b>ese Autocomplete and<b>Japan</b>ese Highlighter broken']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[2][.='Add<b>Japan</b>ese Kanji number normalization to Kuromoji']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='japan']/arr[@name='suggestion']/str[3][.='Add decompose compound<b>Japan</b>ese Katakana token capability to Kuromoji']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

