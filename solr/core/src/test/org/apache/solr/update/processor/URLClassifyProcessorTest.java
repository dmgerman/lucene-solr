begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|update
operator|.
name|AddUpdateCommand
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
name|processor
operator|.
name|URLClassifyProcessor
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
name|processor
operator|.
name|URLClassifyProcessorFactory
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
DECL|class|URLClassifyProcessorTest
specifier|public
class|class
name|URLClassifyProcessorTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|classifyProcessor
specifier|private
specifier|static
name|URLClassifyProcessor
name|classifyProcessor
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|initTest
specifier|public
specifier|static
name|void
name|initTest
parameter_list|()
block|{
name|classifyProcessor
operator|=
operator|(
name|URLClassifyProcessor
operator|)
operator|new
name|URLClassifyProcessorFactory
argument_list|()
operator|.
name|getInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProcessor
specifier|public
name|void
name|testProcessor
parameter_list|()
throws|throws
name|IOException
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
name|SolrInputDocument
name|document
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|document
operator|.
name|addField
argument_list|(
literal|"url"
argument_list|,
literal|"http://www.example.com"
argument_list|)
expr_stmt|;
name|addCommand
operator|.
name|solrDoc
operator|=
name|document
expr_stmt|;
name|classifyProcessor
operator|.
name|processAdd
argument_list|(
name|addCommand
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNormalizations
specifier|public
name|void
name|testNormalizations
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|String
name|url1
init|=
literal|"http://www.example.com/research/"
decl_stmt|;
name|String
name|url2
init|=
literal|"http://www.example.com/research/../research/"
decl_stmt|;
name|assertEquals
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
name|url1
argument_list|)
argument_list|,
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
name|url2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLength
specifier|public
name|void
name|testLength
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|assertEquals
argument_list|(
literal|22
argument_list|,
name|classifyProcessor
operator|.
name|length
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLevels
specifier|public
name|void
name|testLevels
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/research/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/research/index.html"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/research/../research/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.htm"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"https://www.example.com"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|classifyProcessor
operator|.
name|levels
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com////"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLandingPage
specifier|public
name|void
name|testLandingPage
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.html"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.htm"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/welcome.html"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/welcome.htm"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.php"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.asp"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/research/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"https://www.example.com/research/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|classifyProcessor
operator|.
name|isLandingPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/intro.htm"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTopLevelPage
specifier|public
name|void
name|testTopLevelPage
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isTopLevelPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isTopLevelPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isTopLevelPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://subdomain.example.com:1234/#anchor"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classifyProcessor
operator|.
name|isTopLevelPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.html"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|classifyProcessor
operator|.
name|isTopLevelPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|classifyProcessor
operator|.
name|isTopLevelPage
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://subdomain.example.com/?sorting=lastModified%253Adesc&tag=myTag&view=feed"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCanonicalUrl
specifier|public
name|void
name|testCanonicalUrl
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|assertEquals
argument_list|(
literal|"http://www.example.com/"
argument_list|,
name|classifyProcessor
operator|.
name|getCanonicalUrl
argument_list|(
name|classifyProcessor
operator|.
name|getNormalizedURL
argument_list|(
literal|"http://www.example.com/index.html"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

