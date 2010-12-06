begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|request
operator|.
name|SolrQueryRequest
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
name|response
operator|.
name|SolrQueryResponse
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
name|spelling
operator|.
name|AbstractLuceneSpellChecker
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SpellCheckComponentTest
specifier|public
class|class
name|SpellCheckComponentTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|rh
specifier|static
name|String
name|rh
init|=
literal|"spellCheckCompRH"
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
literal|"solrconfig-spellcheckcomponent.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"This is a title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"This is a document"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"another document"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
comment|//bunch of docs that are variants on blue
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blud"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"boue"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blee"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"pixmaa"
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
operator|(
name|commit
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExtendedResultsCount
specifier|public
name|void
name|testExtendedResultsCount
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"bluo"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"5"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/[0]=='bluo'"
argument_list|,
literal|"/spellcheck/suggestions/[1]/numFound==5"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"bluo"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"3"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/[1]/suggestion==[{'word':'blud','freq':1}, {'word':'blue','freq':1}, {'word':'blee','freq':1}]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"documemt"
argument_list|)
argument_list|,
literal|"/spellcheck=={'suggestions':['documemt',{'numFound':1,'startOffset':0,'endOffset':8,'suggestion':['document']}]}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPerDictionary
specifier|public
name|void
name|testPerDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"documemt"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_DICT
argument_list|,
literal|"perDict"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_PREFIX
operator|+
literal|".perDict.foo"
argument_list|,
literal|"bar"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_PREFIX
operator|+
literal|".perDict.bar"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/bar=={'numFound':1, 'startOffset':0, 'endOffset':1, 'suggestion':['foo']}"
argument_list|,
literal|"/spellcheck/suggestions/foo=={'numFound':1, 'startOffset':2, 'endOffset':3, 'suggestion':['bar']}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollate
specifier|public
name|void
name|testCollate
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"documemt"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/collation=='document'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"documemt lowerfilt:broen^4"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/collation=='document lowerfilt:brown^4'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"documemtsss broens"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/collation=='document brown'"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"pixma-a-b-c-d-e-f-g"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/collation=='pixmaa'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorrectSpelling
specifier|public
name|void
name|testCorrectSpelling
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure correct spellings are signaled in the response
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"lowerfilt:lazy lowerfilt:brown"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions=={'correctlySpelled':true}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|rh
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"lakkle"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/spellcheck/suggestions/correctlySpelled==false"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testRelativeIndexDirLocation
specifier|public
name|void
name|testRelativeIndexDirLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"spellchecker1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|indexDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|indexDir
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"spellchecker2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|indexDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|indexDir
operator|=
operator|new
name|File
argument_list|(
name|core
operator|.
name|getDataDir
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"spellchecker3"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|indexDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReloadOnStart
specifier|public
name|void
name|testReloadOnStart
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"This is a title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|request
init|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"ttle"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"spellcheck.dictionary"
argument_list|,
literal|"default"
argument_list|,
literal|"spellcheck.build"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
name|request
argument_list|,
literal|"//arr[@name='suggestion'][.='title']"
argument_list|)
expr_stmt|;
name|NamedList
name|args
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|DICTIONARY_NAME
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
literal|"spellchecker1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"spellchecker"
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
comment|// TODO: this is really fragile and error prone - find a higher level way to test this.
name|SpellCheckComponent
name|checker
init|=
operator|new
name|SpellCheckComponent
argument_list|()
decl_stmt|;
name|checker
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|checker
operator|.
name|inform
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|=
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"spellcheck.q"
argument_list|,
literal|"ttle"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|,
literal|"spellcheck.dictionary"
argument_list|,
literal|"default"
argument_list|,
literal|"spellcheck.reload"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|ResponseBuilder
name|rb
init|=
operator|new
name|ResponseBuilder
argument_list|()
decl_stmt|;
name|rb
operator|.
name|req
operator|=
name|request
expr_stmt|;
name|rb
operator|.
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rb
operator|.
name|components
operator|=
operator|new
name|ArrayList
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSearchComponents
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|checker
operator|.
name|prepare
argument_list|(
name|rb
argument_list|)
expr_stmt|;
try|try
block|{
name|checker
operator|.
name|process
argument_list|(
name|rb
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"NullPointerException due to reload not initializing analyzers"
argument_list|)
expr_stmt|;
block|}
name|rb
operator|.
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
DECL|method|testRebuildOnCommit
specifier|public
name|void
name|testRebuildOnCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:lucenejavt"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|"spellcheck"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No suggestions should be returned"
argument_list|,
name|response
operator|.
name|contains
argument_list|(
literal|"lucenejava"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11231"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"lucenejava"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//arr[@name='suggestion'][.='lucenejava']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

