begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|servlet
operator|.
name|SolrRequestParsers
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
name|update
operator|.
name|AddUpdateCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|RegexBoostProcessorTest
specifier|public
class|class
name|RegexBoostProcessorTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|reProcessor
specifier|private
specifier|static
name|RegexpBoostProcessor
name|reProcessor
decl_stmt|;
DECL|field|_parser
specifier|protected
specifier|static
name|SolrRequestParsers
name|_parser
decl_stmt|;
DECL|field|parameters
specifier|protected
specifier|static
name|ModifiableSolrParams
name|parameters
decl_stmt|;
DECL|field|factory
specifier|private
specifier|static
name|RegexpBoostProcessorFactory
name|factory
decl_stmt|;
DECL|field|document
specifier|private
name|SolrInputDocument
name|document
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUpBeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|_parser
operator|=
operator|new
name|SolrRequestParsers
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|resp
init|=
literal|null
decl_stmt|;
name|parameters
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|parameters
operator|.
name|set
argument_list|(
name|RegexpBoostProcessor
operator|.
name|BOOST_FILENAME_PARAM
argument_list|,
literal|"regex-boost-processor-test.txt"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|set
argument_list|(
name|RegexpBoostProcessor
operator|.
name|INPUT_FIELD_PARAM
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|set
argument_list|(
name|RegexpBoostProcessor
operator|.
name|BOOST_FIELD_PARAM
argument_list|,
literal|"urlboost"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|_parser
operator|.
name|buildRequestFrom
argument_list|(
name|core
argument_list|,
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|factory
operator|=
operator|new
name|RegexpBoostProcessorFactory
argument_list|()
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|parameters
operator|.
name|toNamedList
argument_list|()
argument_list|)
expr_stmt|;
name|reProcessor
operator|=
operator|(
name|RegexpBoostProcessor
operator|)
name|factory
operator|.
name|getInstance
argument_list|(
name|req
argument_list|,
name|resp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownAfterClass
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
comment|// null static members for gc
name|reProcessor
operator|=
literal|null
expr_stmt|;
name|_parser
operator|=
literal|null
expr_stmt|;
name|parameters
operator|=
literal|null
expr_stmt|;
name|factory
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|document
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoBoost
specifier|public
name|void
name|testNoBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"url"
argument_list|,
literal|"http://www.nomatch.no"
argument_list|)
expr_stmt|;
name|processAdd
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0d
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"urlboost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDeboostOld
specifier|public
name|void
name|testDeboostOld
parameter_list|()
throws|throws
name|Exception
block|{
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"url"
argument_list|,
literal|"http://www.somedomain.no/old/test.html"
argument_list|)
expr_stmt|;
name|processAdd
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.1d
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"urlboost"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test the other deboost rule
name|document
operator|=
operator|new
name|SolrInputDocument
argument_list|()
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"url"
argument_list|,
literal|"http://www.somedomain.no/foo/index(1).html"
argument_list|)
expr_stmt|;
name|processAdd
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.5d
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"urlboost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBoostGood
specifier|public
name|void
name|testBoostGood
parameter_list|()
throws|throws
name|Exception
block|{
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"url"
argument_list|,
literal|"http://www.mydomain.no/fifty-percent-boost"
argument_list|)
expr_stmt|;
name|processAdd
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.5d
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"urlboost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoRules
specifier|public
name|void
name|testTwoRules
parameter_list|()
throws|throws
name|Exception
block|{
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"url"
argument_list|,
literal|"http://www.mydomain.no/old/test.html"
argument_list|)
expr_stmt|;
name|processAdd
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.15d
argument_list|,
name|document
operator|.
name|getFieldValue
argument_list|(
literal|"urlboost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|processAdd
specifier|private
name|void
name|processAdd
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|Exception
block|{
name|AddUpdateCommand
name|addCommand
init|=
operator|new
name|AddUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|addCommand
operator|.
name|solrDoc
operator|=
name|doc
expr_stmt|;
name|reProcessor
operator|.
name|processAdd
argument_list|(
name|addCommand
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

