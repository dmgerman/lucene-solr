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
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|MapSolrParams
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
name|handler
operator|.
name|loader
operator|.
name|XMLLoader
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
name|response
operator|.
name|QueryResponseWriter
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
name|BufferingRequestProcessor
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
DECL|class|XsltUpdateRequestHandlerTest
specifier|public
class|class
name|XsltUpdateRequestHandlerTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
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
block|}
annotation|@
name|Override
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdate
specifier|public
name|void
name|testUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
literal|"<random>"
operator|+
literal|"<document>"
operator|+
literal|"<node name=\"id\" value=\"12345\"/>"
operator|+
literal|"<node name=\"name\" value=\"kitten\"/>"
operator|+
literal|"<node name=\"text\" enhance=\"3\" value=\"some other day\"/>"
operator|+
literal|"<node name=\"title\" enhance=\"4\" value=\"A story\"/>"
operator|+
literal|"<node name=\"timestamp\" enhance=\"5\" value=\"2011-07-01T10:31:57.140Z\"/>"
operator|+
literal|"</document>"
operator|+
literal|"</random>"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|TR
argument_list|,
literal|"xsl-update-handler-test.xsl"
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
name|LocalSolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
operator|new
name|MapSolrParams
argument_list|(
name|args
argument_list|)
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|xml
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
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|(
literal|32000
argument_list|)
decl_stmt|;
name|QueryResponseWriter
name|responseWriter
init|=
name|core
operator|.
name|getQueryResponseWriter
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|responseWriter
operator|.
name|write
argument_list|(
name|sw
argument_list|,
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
name|String
name|response
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertU
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test document was correctly committed"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//str[@name='id'][.='12345']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntities
specifier|public
name|void
name|testEntities
parameter_list|()
throws|throws
name|Exception
block|{
comment|// use a binary file, so when it's loaded fail with XML eror:
name|String
name|file
init|=
name|getFile
argument_list|(
literal|"mailing_lists.pdf"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
decl_stmt|;
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<!DOCTYPE foo ["
operator|+
comment|// check that external entities are not resolved!
literal|"<!ENTITY bar SYSTEM \""
operator|+
name|file
operator|+
literal|"\">"
operator|+
comment|// but named entities should be
literal|"<!ENTITY wacky \"zzz\">"
operator|+
literal|"]>"
operator|+
literal|"<random>"
operator|+
literal|"&bar;"
operator|+
literal|"<document>"
operator|+
literal|"<node name=\"id\" value=\"12345\"/>"
operator|+
literal|"<node name=\"foo_s\" value=\"&wacky;\"/>"
operator|+
literal|"</document>"
operator|+
literal|"</random>"
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
name|CommonParams
operator|.
name|TR
argument_list|,
literal|"xsl-update-handler-test.xsl"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|BufferingRequestProcessor
name|p
init|=
operator|new
name|BufferingRequestProcessor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|XMLLoader
name|loader
init|=
operator|new
name|XMLLoader
argument_list|()
operator|.
name|init
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|loader
operator|.
name|load
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|xml
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|AddUpdateCommand
name|add
init|=
name|p
operator|.
name|addCommands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"12345"
argument_list|,
name|add
operator|.
name|solrDoc
operator|.
name|getField
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getFirstValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"zzz"
argument_list|,
name|add
operator|.
name|solrDoc
operator|.
name|getField
argument_list|(
literal|"foo_s"
argument_list|)
operator|.
name|getFirstValue
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

