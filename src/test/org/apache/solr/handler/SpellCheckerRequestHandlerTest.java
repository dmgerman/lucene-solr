begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/**  * This is a test case to test the SpellCheckerRequestHandler class.  * It tests:   *<ul>  *<li>The generation of the spell checkers list with a 10 words</li>  *<li>The identification of the word that was being spell checked</li>  *<li>The confirmation if the word exists or not in the index</li>  *<li>The suggested list of a correctly and incorrectly spelled words</li>  *<li>The suggestions for both correct and incorrect words</li>  *<li>The limitation on the number of suggestions with the   *       suggestionCount parameter</li>  *<li>The usage of the parameter multiWords</li>  *</ul>  *   * Notes/Concerns about this Test Case:  *<ul>  *<li>This is my first test case for a Solr Handler.  As such I am not  *       familiar with the AbstractSolrTestCase and as such I am not  *       100% these test cases will work under the same for each person  *       who runs the test cases (see next note).</li>  *<li>The order of the arrays (arr) may not be consistant on other   *       systems or different runs, as such these test cases may fail?</li>  *<li>Note: I changed //arr/str[1][.='cart'] to //arr/str[.='cart'] and it   *       appears to work.</li>  *<li>The two notations appear to successfully test for the same thing:   *       "//lst[@name='result']/lst[1][@name='word']/str[@name='words'][.='cat']"   *       and "//str[@name='words'][.='cat']" which I would think // would indicate   *       a root node.</li>  *</ul>  */
end_comment

begin_class
DECL|class|SpellCheckerRequestHandlerTest
specifier|public
class|class
name|SpellCheckerRequestHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"solr/conf/schema-spellchecker.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solr/conf/solrconfig-spellchecker.xml"
return|;
block|}
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
block|}
DECL|method|buildSpellCheckIndex
specifier|private
name|void
name|buildSpellCheckIndex
parameter_list|()
block|{
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"spellchecker"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"Add some words to the Spell Check Index:"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|,
literal|"spell"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"102"
argument_list|,
literal|"spell"
argument_list|,
literal|"cart"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"103"
argument_list|,
literal|"spell"
argument_list|,
literal|"carp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"104"
argument_list|,
literal|"spell"
argument_list|,
literal|"cant"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"105"
argument_list|,
literal|"spell"
argument_list|,
literal|"catnip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"106"
argument_list|,
literal|"spell"
argument_list|,
literal|"cattails"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"107"
argument_list|,
literal|"spell"
argument_list|,
literal|"cod"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"108"
argument_list|,
literal|"spell"
argument_list|,
literal|"corn"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"109"
argument_list|,
literal|"spell"
argument_list|,
literal|"cot"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"cmd"
argument_list|,
literal|"rebuild"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Need to first build the index:"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='cmdExecuted'][.='rebuild']"
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
comment|//        ,"//arr[@name='suggestions'][.='']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test for correct spelling of a single word at various accuracy levels    * to see how the suggestions vary.    */
DECL|method|testSpellCheck_01_correctWords
specifier|public
name|void
name|testSpellCheck_01_correctWords
parameter_list|()
block|{
name|buildSpellCheckIndex
argument_list|()
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"spellchecker"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".4"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|,
literal|"//arr/str[.='cot']"
argument_list|,
literal|"//arr/str[.='cart']"
comment|//            ,"//arr/str[1][.='cot']"
comment|//            ,"//arr/str[2][.='cart']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|,
literal|"//arr/str[.='cart']"
argument_list|,
literal|"//arr/str[.='cot']"
argument_list|,
literal|"//arr/str[.='carp']"
argument_list|,
literal|"//arr/str[.='cod']"
argument_list|,
literal|"//arr/str[.='corn']"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for correct spelling of a single word at various accuracy levels    * to see how the suggestions vary.    */
DECL|method|testSpellCheck_02_incorrectWords
specifier|public
name|void
name|testSpellCheck_02_incorrectWords
parameter_list|()
block|{
name|buildSpellCheckIndex
argument_list|()
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"spellchecker"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm the index is still valid"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"coat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='coat']"
argument_list|,
literal|"//str[@name='exist'][.='false']"
argument_list|,
literal|"//arr[@name='suggestions'][.='']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"coat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='coat']"
argument_list|,
literal|"//str[@name='exist'][.='false']"
argument_list|,
literal|"//arr/str[.='cot']"
argument_list|,
literal|"//arr/str[.='cat']"
argument_list|,
literal|"//arr/str[.='corn']"
argument_list|,
literal|"//arr/str[.='cart']"
argument_list|,
literal|"//arr/str[.='cod']"
argument_list|,
literal|"//arr/str[.='solr']"
argument_list|,
literal|"//arr/str[.='carp']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.suggestionCount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"coat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='coat']"
argument_list|,
literal|"//str[@name='exist'][.='false']"
argument_list|,
literal|"//arr/str[.='cot']"
argument_list|,
literal|"//arr/str[.='cat']"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for correct spelling of a single word at various accuracy levels    * to see how the suggestions vary.    */
DECL|method|testSpellCheck_03_multiWords_correctWords
specifier|public
name|void
name|testSpellCheck_03_multiWords_correctWords
parameter_list|()
block|{
name|buildSpellCheckIndex
argument_list|()
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"spellchecker"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm the index is still valid"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
comment|// Enable multiWords formatting:
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.extendedResults"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//int[@name='numDocs'][.=10]"
argument_list|,
literal|"//lst[@name='cat']"
argument_list|,
literal|"//lst[@name='cat']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions' and count(lst)=0]"
argument_list|)
expr_stmt|;
comment|// Please note that the following produces the following XML structure.
comment|//<response>
comment|//<responseHeader>
comment|//<status>0</status><QTime>0</QTime>
comment|//</responseHeader>
comment|//<lst name="result">
comment|//<lst name="cat">
comment|//<int name="frequency">1</int>
comment|//<lst name="suggestions">
comment|//<lst name="cart"><int name="frequency">1</int></lst>
comment|//<lst name="cot"><int name="frequency">1</int></lst>
comment|//<lst name="cod"><int name="frequency">1</int></lst>
comment|//<lst name="carp"><int name="frequency">1</int></lst>
comment|//</lst>
comment|//</lst>
comment|//</lst>
comment|//</response>
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//int[@name='numDocs'][.=10]"
argument_list|,
literal|"//lst[@name='cat']"
argument_list|,
literal|"//lst[@name='cat']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cart']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cot']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cod']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='carp']/int[@name='frequency'][.>0]"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.suggestionCount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//lst[@name='cat']"
argument_list|,
literal|"//lst[@name='cat']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cart']"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cot']"
argument_list|)
expr_stmt|;
comment|/* The following is the generated XML response for the next query with three words:<response><responseHeader><status>0</status><QTime>0</QTime></responseHeader><int name="numDocs">10</int><lst name="result"><lst name="cat"><int name="frequency">1</int><lst name="suggestions"><lst name="cart"><int name="frequency">1</int></lst><lst name="cot"><int name="frequency">1</int></lst></lst></lst><lst name="card"><int name="frequency">1</int><lst name="suggestions"><lst name="carp"><int name="frequency">1</int></lst><lst name="cat"><int name="frequency">1</int></lst></lst></lst><lst name="carp"><int name="frequency">1</int><lst name="suggestions"><lst name="cart"><int name="frequency">1</int></lst><lst name="corn"><int name="frequency">1</int></lst></lst></lst></lst></response>     */
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.suggestionCount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cat cart carp"
argument_list|)
argument_list|,
literal|"//lst[@name='cat']"
argument_list|,
literal|"//lst[@name='cat']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cart']"
argument_list|,
literal|"//lst[@name='cat']/lst[@name='suggestions']/lst[@name='cot']"
argument_list|,
literal|"//lst[@name='cart']"
argument_list|,
literal|"//lst[@name='cart']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='cart']/lst/lst[1]"
argument_list|,
literal|"//lst[@name='cart']/lst/lst[2]"
argument_list|,
literal|"//lst[@name='carp']"
argument_list|,
literal|"//lst[@name='carp']/int[@name='frequency'][.>0]"
argument_list|,
literal|"//lst[@name='carp']/lst[@name='suggestions']/lst[@name='cart']"
argument_list|,
literal|"//lst[@name='carp']/lst[@name='suggestions']/lst[@name='corn']"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for correct spelling of a single word at various accuracy levels    * to see how the suggestions vary.    */
DECL|method|testSpellCheck_04_multiWords_incorrectWords
specifier|public
name|void
name|testSpellCheck_04_multiWords_incorrectWords
parameter_list|()
block|{
name|buildSpellCheckIndex
argument_list|()
expr_stmt|;
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"spellchecker"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm the index is still valid"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
comment|// Enable multiWords formatting:
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.extendedResults"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"coat"
argument_list|)
argument_list|,
literal|"//int[@name='numDocs'][.=10]"
argument_list|,
literal|"//lst[@name='coat']"
argument_list|,
literal|"//lst[@name='coat']/int[@name='frequency'][.=0]"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions' and count(lst)=0]"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"coat"
argument_list|)
argument_list|,
literal|"//lst[@name='coat']"
argument_list|,
literal|"//lst[@name='coat']/int[@name='frequency'][.=0]"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions']/lst[@name='cot']"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions']/lst[@name='cat']"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions']/lst[@name='corn']"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions']/lst[@name='cart']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.suggestionCount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"coat"
argument_list|)
argument_list|,
literal|"//lst[@name='coat']"
argument_list|,
literal|"//lst[@name='coat']/int[@name='frequency'][.=0]"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions']/lst[@name='cot']"
argument_list|,
literal|"//lst[@name='coat']/lst[@name='suggestions']/lst[@name='cat']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.suggestionCount"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".2"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Failed to spell check"
argument_list|,
name|req
argument_list|(
literal|"cet cert corp"
argument_list|)
argument_list|,
literal|"//int[@name='numDocs'][.=10]"
argument_list|,
literal|"//lst[@name='cet']"
argument_list|,
literal|"//lst[@name='cet']/int[@name='frequency'][.=0]"
argument_list|,
literal|"//lst[@name='cet']/lst[@name='suggestions']/lst[1]"
argument_list|,
literal|"//lst[@name='cet']/lst[@name='suggestions']/lst[2]"
argument_list|,
literal|"//lst[@name='cert']"
argument_list|,
literal|"//lst[@name='cert']/int[@name='frequency'][.=0]"
argument_list|,
literal|"//lst[@name='cert']/lst[@name='suggestions']/lst[1]"
argument_list|,
literal|"//lst[@name='cert']/lst[@name='suggestions']/lst[2]"
argument_list|,
literal|"//lst[@name='corp']"
argument_list|,
literal|"//lst[@name='corp']/int[@name='frequency'][.=0]"
argument_list|,
literal|"//lst[@name='corp']/lst[@name='suggestions']/lst[1]"
argument_list|,
literal|"//lst[@name='corp']/lst[@name='suggestions']/lst[2]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpellCheck_05_buildDictionary
specifier|public
name|void
name|testSpellCheck_05_buildDictionary
parameter_list|()
block|{
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"spellchecker"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"Add some words to the Spell Check Index:"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"100"
argument_list|,
literal|"spell"
argument_list|,
literal|"solr cat cart"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat cart"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"102"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat cart"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"103"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat cart carp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"104"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat car cant"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"105"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat catnip"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"106"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat cattails"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"107"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat cod"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"108"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat corn"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"109"
argument_list|,
literal|"spell"
argument_list|,
literal|"cat cot"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.dictionary.threshold"
argument_list|,
literal|"0.20"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"cmd"
argument_list|,
literal|"rebuild"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Need to first build the index:"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='cmdExecuted'][.='rebuild']"
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|clear
argument_list|()
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"sp.query.accuracy"
argument_list|,
literal|".9"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm index contains only words above threshold"
argument_list|,
name|req
argument_list|(
literal|"cat"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cat']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm index contains only words above threshold"
argument_list|,
name|req
argument_list|(
literal|"cart"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cart']"
argument_list|,
literal|"//str[@name='exist'][.='true']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm index contains only words above threshold"
argument_list|,
name|req
argument_list|(
literal|"cod"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='cod']"
argument_list|,
literal|"//str[@name='exist'][.='false']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Confirm index contains only words above threshold"
argument_list|,
name|req
argument_list|(
literal|"corn"
argument_list|)
argument_list|,
literal|"//str[@name='words'][.='corn']"
argument_list|,
literal|"//str[@name='exist'][.='false']"
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

