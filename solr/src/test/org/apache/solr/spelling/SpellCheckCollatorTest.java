begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_class
DECL|class|SpellCheckCollatorTest
specifier|public
class|class
name|SpellCheckCollatorTest
extends|extends
name|SolrTestCaseJ4
block|{
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"faith hope and love"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"faith hope and loaves"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"fat hops and loaves"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"faith of homer"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"fat of homer"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"peace"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollateWithFilter
specifier|public
name|void
name|testCollateWithFilter
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
name|SearchComponent
name|speller
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"speller is null and it shouldn't be"
argument_list|,
name|speller
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"lowerfilt:(+fauth +home +loane)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|FQ
argument_list|,
literal|"NOT(id:1)"
argument_list|)
expr_stmt|;
comment|//Because a FilterQuery is applied which removes doc id#1 from possible hits, we would
comment|//not want the collations to return us "lowerfilt:(+faith +hope +loaves)" as this only matches doc id#1.
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|spellCheck
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
name|NamedList
name|suggestions
init|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|collations
init|=
name|suggestions
operator|.
name|getAll
argument_list|(
literal|"collation"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|collations
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|collation
range|:
name|collations
control|)
block|{
name|assertTrue
argument_list|(
operator|!
name|collation
operator|.
name|equals
argument_list|(
literal|"lowerfilt:(+faith +hope +loaves)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCollateWithMultipleRequestHandlers
specifier|public
name|void
name|testCollateWithMultipleRequestHandlers
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
name|SearchComponent
name|speller
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"speller is null and it shouldn't be"
argument_list|,
name|speller
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_DICT
argument_list|,
literal|"multipleFields"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"peac"
argument_list|)
expr_stmt|;
comment|//SpellCheckCompRH has no "qf" defined.  It will not find "peace" from "peac" despite it being in the dictionary
comment|//because requrying against this Request Handler results in 0 hits.
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|spellCheck
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
name|NamedList
name|suggestions
init|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
decl_stmt|;
name|String
name|singleCollation
init|=
operator|(
name|String
operator|)
name|suggestions
operator|.
name|get
argument_list|(
literal|"collation"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|singleCollation
argument_list|)
expr_stmt|;
comment|//SpellCheckCompRH1 has "lowerfilt1" defined in the "qf" param.  It will find "peace" from "peac" because
comment|//requrying field "lowerfilt1" returns the hit.
name|params
operator|.
name|remove
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|)
expr_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH1"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|spellCheck
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
expr_stmt|;
name|suggestions
operator|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
expr_stmt|;
name|singleCollation
operator|=
operator|(
name|String
operator|)
name|suggestions
operator|.
name|get
argument_list|(
literal|"collation"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|singleCollation
argument_list|,
literal|"peace"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testExtendedCollate
specifier|public
name|void
name|testExtendedCollate
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
name|SearchComponent
name|speller
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"speller is null and it shouldn't be"
argument_list|,
name|speller
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"lowerfilt:(+fauth +home +loane)"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// Testing backwards-compatible behavior.
comment|// Returns 1 collation as a single string.
comment|// All words are "correct" per the dictionary, but this collation would
comment|// return no results if tried.
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|spellCheck
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
name|NamedList
name|suggestions
init|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
decl_stmt|;
name|String
name|singleCollation
init|=
operator|(
name|String
operator|)
name|suggestions
operator|.
name|get
argument_list|(
literal|"collation"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"lowerfilt:(+faith +homer +loaves)"
argument_list|,
name|singleCollation
argument_list|)
expr_stmt|;
comment|// Testing backwards-compatible response format but will only return a
comment|// collation that would return results.
name|params
operator|.
name|remove
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|spellCheck
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
expr_stmt|;
name|suggestions
operator|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
expr_stmt|;
name|singleCollation
operator|=
operator|(
name|String
operator|)
name|suggestions
operator|.
name|get
argument_list|(
literal|"collation"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"lowerfilt:(+faith +hope +loaves)"
argument_list|,
name|singleCollation
argument_list|)
expr_stmt|;
comment|// Testing returning multiple collations if more than one valid
comment|// combination exists.
name|params
operator|.
name|remove
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|)
expr_stmt|;
name|params
operator|.
name|remove
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|spellCheck
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
expr_stmt|;
name|suggestions
operator|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|collations
init|=
name|suggestions
operator|.
name|getAll
argument_list|(
literal|"collation"
argument_list|)
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
name|String
name|multipleCollation
range|:
name|collations
control|)
block|{
name|assertTrue
argument_list|(
name|multipleCollation
operator|.
name|equals
argument_list|(
literal|"lowerfilt:(+faith +hope +love)"
argument_list|)
operator|||
name|multipleCollation
operator|.
name|equals
argument_list|(
literal|"lowerfilt:(+faith +hope +loaves)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Testing return multiple collations with expanded collation response
comment|// format.
name|params
operator|.
name|add
argument_list|(
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|handler
operator|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"spellCheckCompRH"
argument_list|)
expr_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|values
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|spellCheck
operator|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
expr_stmt|;
name|suggestions
operator|=
operator|(
name|NamedList
operator|)
name|spellCheck
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NamedList
argument_list|>
name|expandedCollationList
init|=
name|suggestions
operator|.
name|getAll
argument_list|(
literal|"collation"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|usedcollations
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|expandedCollationList
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|NamedList
name|expandedCollation
range|:
name|expandedCollationList
control|)
block|{
name|String
name|multipleCollation
init|=
operator|(
name|String
operator|)
name|expandedCollation
operator|.
name|get
argument_list|(
literal|"collationQuery"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|multipleCollation
operator|.
name|equals
argument_list|(
literal|"lowerfilt:(+faith +hope +love)"
argument_list|)
operator|||
name|multipleCollation
operator|.
name|equals
argument_list|(
literal|"lowerfilt:(+faith +hope +loaves)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|usedcollations
operator|.
name|contains
argument_list|(
name|multipleCollation
argument_list|)
argument_list|)
expr_stmt|;
name|usedcollations
operator|.
name|add
argument_list|(
name|multipleCollation
argument_list|)
expr_stmt|;
name|int
name|hits
init|=
operator|(
name|Integer
operator|)
name|expandedCollation
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|==
literal|1
argument_list|)
expr_stmt|;
name|NamedList
name|misspellingsAndCorrections
init|=
operator|(
name|NamedList
operator|)
name|expandedCollation
operator|.
name|get
argument_list|(
literal|"misspellingsAndCorrections"
argument_list|)
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
name|String
name|correctionForFauth
init|=
operator|(
name|String
operator|)
name|misspellingsAndCorrections
operator|.
name|get
argument_list|(
literal|"fauth"
argument_list|)
decl_stmt|;
name|String
name|correctionForHome
init|=
operator|(
name|String
operator|)
name|misspellingsAndCorrections
operator|.
name|get
argument_list|(
literal|"home"
argument_list|)
decl_stmt|;
name|String
name|correctionForLoane
init|=
operator|(
name|String
operator|)
name|misspellingsAndCorrections
operator|.
name|get
argument_list|(
literal|"loane"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|correctionForFauth
operator|.
name|equals
argument_list|(
literal|"faith"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|correctionForHome
operator|.
name|equals
argument_list|(
literal|"hope"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|correctionForLoane
operator|.
name|equals
argument_list|(
literal|"love"
argument_list|)
operator|||
name|correctionForLoane
operator|.
name|equals
argument_list|(
literal|"loaves"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

