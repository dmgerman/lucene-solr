begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|client
operator|.
name|solrj
operator|.
name|SolrJettyTestBase
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
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|QueryRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SpellCheckResponse
operator|.
name|Collation
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SpellCheckResponse
operator|.
name|Correction
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
name|SolrInputDocument
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
name|CommonParams
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
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|handler
operator|.
name|component
operator|.
name|SearchComponent
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
name|handler
operator|.
name|component
operator|.
name|SpellCheckComponent
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
name|LocalSolrQueryRequest
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
name|SolrRequestHandler
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
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|fail
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
name|assertEquals
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
name|assertNull
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test for SpellCheckComponent's response in Solrj  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestSpellCheckResponse
specifier|public
class|class
name|TestSpellCheckResponse
extends|extends
name|SolrJettyTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// createJetty(EXAMPLE_HOME, null, null);
name|initCore
argument_list|(
name|EXAMPLE_CONFIG
argument_list|,
name|EXAMPLE_SCHEMA
argument_list|,
name|EXAMPLE_HOME
argument_list|)
expr_stmt|;
comment|// initCore("solrconfig.xml", "schema.xml", null);
block|}
DECL|field|field
specifier|static
name|String
name|field
init|=
literal|"name"
decl_stmt|;
annotation|@
name|Test
DECL|method|testSpellCheckResponse
specifier|public
name|void
name|testSpellCheckResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|getSolrServer
argument_list|()
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"111"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|field
argument_list|,
literal|"Samsung"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/spell"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_Q
argument_list|,
literal|"samsang"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SpellCheckResponse
name|response
init|=
name|request
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getSpellCheckResponse
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"samsung"
argument_list|,
name|response
operator|.
name|getFirstSuggestion
argument_list|(
literal|"samsang"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpellCheckResponse_Extended
specifier|public
name|void
name|testSpellCheckResponse_Extended
parameter_list|()
throws|throws
name|Exception
block|{
name|getSolrServer
argument_list|()
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"111"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
name|field
argument_list|,
literal|"Samsung"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"*:*"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/spell"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_Q
argument_list|,
literal|"samsang"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SpellCheckResponse
name|response
init|=
name|request
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getSpellCheckResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"samsung"
argument_list|,
name|response
operator|.
name|getFirstSuggestion
argument_list|(
literal|"samsang"
argument_list|)
argument_list|)
expr_stmt|;
name|SpellCheckResponse
operator|.
name|Suggestion
name|sug
init|=
name|response
operator|.
name|getSuggestion
argument_list|(
literal|"samsang"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SpellCheckResponse
operator|.
name|Suggestion
argument_list|>
name|sugs
init|=
name|response
operator|.
name|getSuggestions
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|sug
operator|.
name|getAlternatives
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|sug
operator|.
name|getAlternativeFrequencies
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sugs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAlternatives
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|sugs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAlternativeFrequencies
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"samsung"
argument_list|,
name|sug
operator|.
name|getAlternatives
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"samsung"
argument_list|,
name|sugs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAlternatives
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// basic test if fields were filled in
name|assertTrue
argument_list|(
name|sug
operator|.
name|getEndOffset
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sug
operator|.
name|getToken
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sug
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// assertTrue(sug.getOriginalFrequency()> 0);
comment|// Hmmm... the API for SpellCheckResponse could be nicer:
name|response
operator|.
name|getSuggestions
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAlternatives
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSpellCheckCollationResponse
specifier|public
name|void
name|testSpellCheckCollationResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|getSolrServer
argument_list|()
expr_stmt|;
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"faith hope and love"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"faith hope and loaves"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"fat hops and loaves"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"faith of homer"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"name"
argument_list|,
literal|"fat of homer"
argument_list|)
expr_stmt|;
name|server
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|server
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Test Backwards Compatibility
name|SolrQuery
name|query
init|=
operator|new
name|SolrQuery
argument_list|(
literal|"name:(+fauth +home +loane)"
argument_list|)
decl_stmt|;
name|query
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/spell"
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|QueryRequest
name|request
init|=
operator|new
name|QueryRequest
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|SpellCheckResponse
name|response
init|=
name|request
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getSpellCheckResponse
argument_list|()
decl_stmt|;
name|response
operator|=
name|request
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getSpellCheckResponse
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"name:(+faith +homer +loaves)"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getCollatedResult
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test Expanded Collation Results
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|query
operator|.
name|set
argument_list|(
name|SpellingParams
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|QueryRequest
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|response
operator|=
name|request
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getSpellCheckResponse
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"name:(+faith +hope +love)"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getCollatedResult
argument_list|()
argument_list|)
operator|||
literal|"name:(+faith +hope +loaves)"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getCollatedResult
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Collation
argument_list|>
name|collations
init|=
name|response
operator|.
name|getCollatedResults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|collations
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|Collation
name|collation
range|:
name|collations
control|)
block|{
name|assertTrue
argument_list|(
literal|"name:(+faith +hope +love)"
operator|.
name|equals
argument_list|(
name|collation
operator|.
name|getCollationQueryString
argument_list|()
argument_list|)
operator|||
literal|"name:(+faith +hope +loaves)"
operator|.
name|equals
argument_list|(
name|collation
operator|.
name|getCollationQueryString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|collation
operator|.
name|getNumberOfHits
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Correction
argument_list|>
name|misspellingsAndCorrections
init|=
name|collation
operator|.
name|getMisspellingsAndCorrections
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|misspellingsAndCorrections
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
for|for
control|(
name|Correction
name|correction
range|:
name|misspellingsAndCorrections
control|)
block|{
if|if
condition|(
literal|"fauth"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getOriginal
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"faith"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getCorrection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"home"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getOriginal
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"hope"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getCorrection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"loane"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getOriginal
argument_list|()
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
literal|"love"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getCorrection
argument_list|()
argument_list|)
operator|||
literal|"loaves"
operator|.
name|equals
argument_list|(
name|correction
operator|.
name|getCorrection
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Original Word Should have been either fauth, home or loane."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

