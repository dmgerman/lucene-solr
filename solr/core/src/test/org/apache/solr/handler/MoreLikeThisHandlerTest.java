begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|common
operator|.
name|util
operator|.
name|ContentStream
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
name|ContentStreamBase
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
name|SolrQueryRequestBase
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

begin_comment
comment|/**  * TODO -- this needs to actually test the results/query etc  */
end_comment

begin_class
DECL|class|MoreLikeThisHandlerTest
specifier|public
class|class
name|MoreLikeThisHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|moreLikeThisBeforeClass
specifier|public
specifier|static
name|void
name|moreLikeThisBeforeClass
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
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInterface
specifier|public
name|void
name|testInterface
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
name|MoreLikeThisHandler
name|mlt
init|=
operator|new
name|MoreLikeThisHandler
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
comment|// requires 'q' or single content stream
try|try
block|{
name|mlt
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// expected
comment|// requires 'q' or single content stream
try|try
block|{
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
literal|"hello"
argument_list|)
argument_list|)
expr_stmt|;
name|streams
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
literal|"there"
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|streams
argument_list|)
expr_stmt|;
name|mlt
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
operator|new
name|SolrQueryResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
comment|// expected
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"name"
argument_list|,
literal|"Tom Cruise"
argument_list|,
literal|"subword"
argument_list|,
literal|"Top Gun"
argument_list|,
literal|"subword"
argument_list|,
literal|"Risky Business"
argument_list|,
literal|"subword"
argument_list|,
literal|"The Color of Money"
argument_list|,
literal|"subword"
argument_list|,
literal|"Minority Report"
argument_list|,
literal|"subword"
argument_list|,
literal|"Days of Thunder"
argument_list|,
literal|"subword"
argument_list|,
literal|"Eyes Wide Shut"
argument_list|,
literal|"subword"
argument_list|,
literal|"Far and Away"
argument_list|,
literal|"foo_ti"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"name"
argument_list|,
literal|"Tom Hanks"
argument_list|,
literal|"subword"
argument_list|,
literal|"The Green Mile"
argument_list|,
literal|"subword"
argument_list|,
literal|"Forest Gump"
argument_list|,
literal|"subword"
argument_list|,
literal|"Philadelphia Story"
argument_list|,
literal|"subword"
argument_list|,
literal|"Big"
argument_list|,
literal|"subword"
argument_list|,
literal|"Cast Away"
argument_list|,
literal|"foo_ti"
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"44"
argument_list|,
literal|"name"
argument_list|,
literal|"Harrison Ford"
argument_list|,
literal|"subword"
argument_list|,
literal|"Star Wars"
argument_list|,
literal|"subword"
argument_list|,
literal|"Indiana Jones"
argument_list|,
literal|"subword"
argument_list|,
literal|"Patriot Games"
argument_list|,
literal|"subword"
argument_list|,
literal|"Regarding Henry"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"45"
argument_list|,
literal|"name"
argument_list|,
literal|"George Harrison"
argument_list|,
literal|"subword"
argument_list|,
literal|"Yellow Submarine"
argument_list|,
literal|"subword"
argument_list|,
literal|"Help"
argument_list|,
literal|"subword"
argument_list|,
literal|"Magical Mystery Tour"
argument_list|,
literal|"subword"
argument_list|,
literal|"Sgt. Peppers Lonley Hearts Club Band"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"46"
argument_list|,
literal|"name"
argument_list|,
literal|"Nicole Kidman"
argument_list|,
literal|"subword"
argument_list|,
literal|"Batman"
argument_list|,
literal|"subword"
argument_list|,
literal|"Days of Thunder"
argument_list|,
literal|"subword"
argument_list|,
literal|"Eyes Wide Shut"
argument_list|,
literal|"subword"
argument_list|,
literal|"Far and Away"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:42"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|MLT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|SIMILARITY_FIELDS
argument_list|,
literal|"name,subword"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|INTERESTING_TERMS
argument_list|,
literal|"details"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|mltreq
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"morelikethis - tom cruise"
argument_list|,
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='46']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='43']"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|BOOST
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"morelikethis - tom cruise"
argument_list|,
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='46']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='43']"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:44"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"morelike this - harrison ford"
argument_list|,
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='45']"
argument_list|)
expr_stmt|;
comment|// test MoreLikeThis debug
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"morelike this - harrison ford"
argument_list|,
name|mltreq
argument_list|,
literal|"//lst[@name='debug']/lst[@name='moreLikeThis']/lst[@name='44']/str[@name='rawMLTQuery']"
argument_list|,
literal|"//lst[@name='debug']/lst[@name='moreLikeThis']/lst[@name='44']/str[@name='boostedMLTQuery']"
argument_list|,
literal|"//lst[@name='debug']/lst[@name='moreLikeThis']/lst[@name='44']/str[@name='realMLTQuery']"
argument_list|,
literal|"//lst[@name='debug']/lst[@name='moreLikeThis']/lst[@name='44']/lst[@name='explain']/str[@name='45']"
argument_list|)
expr_stmt|;
comment|// test that qparser plugins work
name|params
operator|.
name|remove
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!field f=id}44"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='45']"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:42"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|MoreLikeThisParams
operator|.
name|QF
argument_list|,
literal|"name^5.0 subword^0.1"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"morelikethis with weights"
argument_list|,
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='43']"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.='46']"
argument_list|)
expr_stmt|;
comment|// test that qparser plugins work w/ the MoreLikeThisHandler
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/mlt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!field f=id}44"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='45']"
argument_list|)
expr_stmt|;
comment|// test that debugging works (test for MoreLikeThis*Handler*)
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"/mlt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
name|mltreq
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|mltreq
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.='45']"
argument_list|,
literal|"//lst[@name='debug']/lst[@name='explain']"
argument_list|)
expr_stmt|;
comment|// params.put(MoreLikeThisParams.QF,new String[]{"foo_ti"});
comment|// String response = h.query(mltreq);
comment|// System.out.println(response);
name|mltreq
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

