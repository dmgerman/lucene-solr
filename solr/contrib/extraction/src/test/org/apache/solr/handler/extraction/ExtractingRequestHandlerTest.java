begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
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
name|ArrayList
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
name|SolrException
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
name|handler
operator|.
name|extraction
operator|.
name|ExtractingDocumentLoader
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
name|extraction
operator|.
name|ExtractingParams
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
name|extraction
operator|.
name|ExtractingRequestHandler
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

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|ExtractingRequestHandlerTest
specifier|public
class|class
name|ExtractingRequestHandlerTest
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
argument_list|,
name|getFile
argument_list|(
literal|"extraction/solr"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
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
DECL|method|testExtraction
specifier|public
name|void
name|testExtraction
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"extraction/solr-word.pdf"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Creation-Date"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.AAPL:Keywords"
argument_list|,
literal|"ignored_a"
argument_list|,
literal|"fmap.xmpTPg:NPages"
argument_list|,
literal|"ignored_a"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"title:solr-word"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
name|req
argument_list|(
literal|"title:solr-word"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"two"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"title:Welcome"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
name|req
argument_list|(
literal|"title:Welcome"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"simple2"
argument_list|,
literal|"uprefix"
argument_list|,
literal|"t_"
argument_list|,
literal|"lowernames"
argument_list|,
literal|"true"
argument_list|,
literal|"captureAttr"
argument_list|,
literal|"true"
argument_list|,
literal|"fmap.a"
argument_list|,
literal|"t_href"
argument_list|,
literal|"fmap.content_type"
argument_list|,
literal|"abcxyz"
argument_list|,
comment|// test that lowernames is applied before mapping, and uprefix is applied after mapping
literal|"commit"
argument_list|,
literal|"true"
comment|// test immediate commit
argument_list|)
expr_stmt|;
comment|// test that purposely causes a failure to print out the doc for test debugging
comment|// assertQ(req("q","id:simple2","indent","true"), "//*[@numFound='0']");
comment|// test both lowernames and unknown field mapping
comment|//assertQ(req("+id:simple2 +t_content_type:[* TO *]"), "//*[@numFound='1']");
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:simple2 +t_href:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:simple2 +t_abcxyz:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// load again in the exact same way, but boost one field
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"simple3"
argument_list|,
literal|"uprefix"
argument_list|,
literal|"t_"
argument_list|,
literal|"lowernames"
argument_list|,
literal|"true"
argument_list|,
literal|"captureAttr"
argument_list|,
literal|"true"
argument_list|,
literal|"fmap.a"
argument_list|,
literal|"t_href"
argument_list|,
literal|"commit"
argument_list|,
literal|"true"
argument_list|,
literal|"boost.t_href"
argument_list|,
literal|"100.0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"t_href:http"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"t_href:http"
argument_list|)
argument_list|,
literal|"//doc[1]/str[.='simple3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:simple3 +t_content_type:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|//test lowercase and then uprefix
comment|// test capture
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"simple4"
argument_list|,
literal|"uprefix"
argument_list|,
literal|"t_"
argument_list|,
literal|"capture"
argument_list|,
literal|"p"
argument_list|,
comment|// capture only what is in the title element
literal|"commit"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:simple4 +t_content:Solr"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:simple4 +t_p:\"here is some text\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"extraction/version_control.xml"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"three"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"stream_name:version_control.xml"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
name|req
argument_list|(
literal|"stream_name:version_control.xml"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultField
specifier|public
name|void
name|testDefaultField
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|ignoreException
argument_list|(
literal|"unknown field 'a'"
argument_list|)
expr_stmt|;
name|ignoreException
argument_list|(
literal|"unknown field 'meta'"
argument_list|)
expr_stmt|;
comment|// TODO: should this exception be happening?
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"simple2"
argument_list|,
literal|"lowernames"
argument_list|,
literal|"true"
argument_list|,
literal|"captureAttr"
argument_list|,
literal|"true"
argument_list|,
comment|//"fmap.content_type", "abcxyz",
literal|"commit"
argument_list|,
literal|"true"
comment|// test immediate commit
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|//do nothing
block|}
finally|finally
block|{
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"simple2"
argument_list|,
name|ExtractingParams
operator|.
name|DEFAULT_FIELD
argument_list|,
literal|"defaultExtr"
argument_list|,
comment|//test that unmapped fields go to the text field when no uprefix is specified
literal|"lowernames"
argument_list|,
literal|"true"
argument_list|,
literal|"captureAttr"
argument_list|,
literal|"true"
argument_list|,
comment|//"fmap.content_type", "abcxyz",
literal|"commit"
argument_list|,
literal|"true"
comment|// test immediate commit
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:simple2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"defaultExtr:http\\://www.apache.org"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|//Test when both uprefix and default are specified.
name|loadLocal
argument_list|(
literal|"extraction/simple.html"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"simple2"
argument_list|,
name|ExtractingParams
operator|.
name|DEFAULT_FIELD
argument_list|,
literal|"defaultExtr"
argument_list|,
comment|//test that unmapped fields go to the text field when no uprefix is specified
name|ExtractingParams
operator|.
name|UNKNOWN_FIELD_PREFIX
argument_list|,
literal|"t_"
argument_list|,
literal|"lowernames"
argument_list|,
literal|"true"
argument_list|,
literal|"captureAttr"
argument_list|,
literal|"true"
argument_list|,
literal|"fmap.a"
argument_list|,
literal|"t_href"
argument_list|,
comment|//"fmap.content_type", "abcxyz",
literal|"commit"
argument_list|,
literal|"true"
comment|// test immediate commit
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:simple2 +t_href:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLiterals
specifier|public
name|void
name|testLiterals
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//test literal
name|loadLocal
argument_list|(
literal|"extraction/version_control.xml"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"literal.extractionLiteralMV"
argument_list|,
literal|"one"
argument_list|,
literal|"literal.extractionLiteralMV"
argument_list|,
literal|"two"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"stream_name:version_control.xml"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
name|req
argument_list|(
literal|"stream_name:version_control.xml"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"extractionLiteralMV:one"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"extractionLiteralMV:two"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
try|try
block|{
name|loadLocal
argument_list|(
literal|"extraction/version_control.xml"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"two"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"literal.extractionLiteral"
argument_list|,
literal|"one"
argument_list|,
literal|"literal.extractionLiteral"
argument_list|,
literal|"two"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
comment|// TODO: original author did not specify why an exception should be thrown... how to fix?
comment|// assertTrue("Exception should have been thrown", false);
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
comment|//nothing to see here, move along
block|}
name|loadLocal
argument_list|(
literal|"extraction/version_control.xml"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"three"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"literal.extractionLiteral"
argument_list|,
literal|"one"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
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
name|req
argument_list|(
literal|"extractionLiteral:one"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPlainTextSpecifyingMimeType
specifier|public
name|void
name|testPlainTextSpecifyingMimeType
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// Load plain text specifying MIME type:
name|loadLocal
argument_list|(
literal|"extraction/version_control.txt"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
name|ExtractingParams
operator|.
name|STREAM_TYPE
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"extractedContent:Apache"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
name|req
argument_list|(
literal|"extractedContent:Apache"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPlainTextSpecifyingResourceName
specifier|public
name|void
name|testPlainTextSpecifyingResourceName
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|// Load plain text specifying filename
name|loadLocal
argument_list|(
literal|"extraction/version_control.txt"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
literal|"fmap.language"
argument_list|,
literal|"extractedLanguage"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"extractedContent"
argument_list|,
name|ExtractingParams
operator|.
name|RESOURCE_NAME
argument_list|,
literal|"extraction/version_control.txt"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"extractedContent:Apache"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
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
name|req
argument_list|(
literal|"extractedContent:Apache"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCommitWithin
specifier|public
name|void
name|testCommitWithin
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
name|ExtractingParams
operator|.
name|RESOURCE_NAME
argument_list|,
literal|"extraction/version_control.txt"
argument_list|,
literal|"commitWithin"
argument_list|,
literal|"200"
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
name|ExtractingDocumentLoader
name|loader
init|=
operator|(
name|ExtractingDocumentLoader
operator|)
name|handler
operator|.
name|newLoader
argument_list|(
name|req
argument_list|,
name|p
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
name|FileStream
argument_list|(
name|getFile
argument_list|(
literal|"extraction/version_control.txt"
argument_list|)
argument_list|)
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
literal|200
argument_list|,
name|add
operator|.
name|commitWithin
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Note: If you load a plain text file specifying neither MIME type nor filename, extraction will silently fail. This is because Tika's
comment|// automatic MIME type detection will fail, and it will default to using an empty-string-returning default parser
annotation|@
name|Test
DECL|method|testExtractOnly
specifier|public
name|void
name|testExtractOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
name|loadLocal
argument_list|(
literal|"extraction/solr-word.pdf"
argument_list|,
name|ExtractingParams
operator|.
name|EXTRACT_ONLY
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"rsp is null and it shouldn't be"
argument_list|,
name|rsp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|NamedList
name|list
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|String
name|extraction
init|=
operator|(
name|String
operator|)
name|list
operator|.
name|get
argument_list|(
literal|"solr-word.pdf"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"extraction is null and it shouldn't be"
argument_list|,
name|extraction
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extraction
operator|+
literal|" does not contain "
operator|+
literal|"solr-word"
argument_list|,
name|extraction
operator|.
name|indexOf
argument_list|(
literal|"solr-word"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|list
operator|.
name|get
argument_list|(
literal|"solr-word.pdf_metadata"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"nl is null and it shouldn't be"
argument_list|,
name|nl
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Object
name|title
init|=
name|nl
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"title is null and it shouldn't be"
argument_list|,
name|title
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extraction
operator|.
name|indexOf
argument_list|(
literal|"<?xml"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|rsp
operator|=
name|loadLocal
argument_list|(
literal|"extraction/solr-word.pdf"
argument_list|,
name|ExtractingParams
operator|.
name|EXTRACT_ONLY
argument_list|,
literal|"true"
argument_list|,
name|ExtractingParams
operator|.
name|EXTRACT_FORMAT
argument_list|,
name|ExtractingDocumentLoader
operator|.
name|TEXT_FORMAT
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"rsp is null and it shouldn't be"
argument_list|,
name|rsp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|list
operator|=
name|rsp
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|extraction
operator|=
operator|(
name|String
operator|)
name|list
operator|.
name|get
argument_list|(
literal|"solr-word.pdf"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"extraction is null and it shouldn't be"
argument_list|,
name|extraction
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extraction
operator|+
literal|" does not contain "
operator|+
literal|"solr-word"
argument_list|,
name|extraction
operator|.
name|indexOf
argument_list|(
literal|"solr-word"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extraction
operator|.
name|indexOf
argument_list|(
literal|"<?xml"
argument_list|)
operator|==
operator|-
literal|1
argument_list|)
expr_stmt|;
name|nl
operator|=
operator|(
name|NamedList
operator|)
name|list
operator|.
name|get
argument_list|(
literal|"solr-word.pdf_metadata"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"nl is null and it shouldn't be"
argument_list|,
name|nl
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|title
operator|=
name|nl
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"title is null and it shouldn't be"
argument_list|,
name|title
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testXPath
specifier|public
name|void
name|testXPath
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|rsp
init|=
name|loadLocal
argument_list|(
literal|"extraction/example.html"
argument_list|,
name|ExtractingParams
operator|.
name|XPATH_EXPRESSION
argument_list|,
literal|"/xhtml:html/xhtml:body/xhtml:a/descendant:node()"
argument_list|,
name|ExtractingParams
operator|.
name|EXTRACT_ONLY
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"rsp is null and it shouldn't be"
argument_list|,
name|rsp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|NamedList
name|list
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|String
name|val
init|=
operator|(
name|String
operator|)
name|list
operator|.
name|get
argument_list|(
literal|"example.html"
argument_list|)
decl_stmt|;
name|val
operator|=
name|val
operator|.
name|trim
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|val
operator|+
literal|" is not equal to "
operator|+
literal|"linkNews"
argument_list|,
name|val
operator|.
name|equals
argument_list|(
literal|"linkNews"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
comment|//there are two<a> tags, and they get collapesd
block|}
comment|/** test arabic PDF extraction is functional */
annotation|@
name|Test
DECL|method|testArabicPDF
specifier|public
name|void
name|testArabicPDF
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|loadLocal
argument_list|(
literal|"extraction/arabic.pdf"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Creation-Date"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.AAPL:Keywords"
argument_list|,
literal|"ignored_a"
argument_list|,
literal|"fmap.xmpTPg:NPages"
argument_list|,
literal|"ignored_a"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"wdf_nocase"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"wdf_nocase:Ø§ÙØ³ÙÙ"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
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
name|req
argument_list|(
literal|"wdf_nocase:Ø§ÙØ³ÙÙ"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTikaExceptionHandling
specifier|public
name|void
name|testTikaExceptionHandling
parameter_list|()
throws|throws
name|Exception
block|{
name|ExtractingRequestHandler
name|handler
init|=
operator|(
name|ExtractingRequestHandler
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"/update/extract"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"handler is null and it shouldn't be"
argument_list|,
name|handler
operator|!=
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|loadLocal
argument_list|(
literal|"extraction/password-is-solrcell.docx"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"TikaException is expected because of trying to extract text from password protected word file."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
try|try
block|{
name|loadLocal
argument_list|(
literal|"extraction/password-is-solrcell.docx"
argument_list|,
literal|"fmap.created"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.producer"
argument_list|,
literal|"extractedProducer"
argument_list|,
literal|"fmap.creator"
argument_list|,
literal|"extractedCreator"
argument_list|,
literal|"fmap.Keywords"
argument_list|,
literal|"extractedKeywords"
argument_list|,
literal|"fmap.Creation-Date"
argument_list|,
literal|"extractedDate"
argument_list|,
literal|"fmap.AAPL:Keywords"
argument_list|,
literal|"ignored_a"
argument_list|,
literal|"fmap.xmpTPg:NPages"
argument_list|,
literal|"ignored_a"
argument_list|,
literal|"fmap.Author"
argument_list|,
literal|"extractedAuthor"
argument_list|,
literal|"fmap.content"
argument_list|,
literal|"wdf_nocase"
argument_list|,
literal|"literal.id"
argument_list|,
literal|"one"
argument_list|,
literal|"ignoreTikaException"
argument_list|,
literal|"true"
argument_list|,
comment|// set ignore flag
literal|"fmap.Last-Modified"
argument_list|,
literal|"extractedDate"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"TikaException should be ignored."
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
block|}
DECL|method|loadLocal
name|SolrQueryResponse
name|loadLocal
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|LocalSolrQueryRequest
name|req
init|=
operator|(
name|LocalSolrQueryRequest
operator|)
name|req
argument_list|(
name|args
argument_list|)
decl_stmt|;
try|try
block|{
comment|// TODO: stop using locally defined streams once stream.file and
comment|// stream.body work everywhere
name|List
argument_list|<
name|ContentStream
argument_list|>
name|cs
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|cs
operator|.
name|add
argument_list|(
operator|new
name|ContentStreamBase
operator|.
name|FileStream
argument_list|(
name|getFile
argument_list|(
name|filename
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|cs
argument_list|)
expr_stmt|;
return|return
name|h
operator|.
name|queryAndResponse
argument_list|(
literal|"/update/extract"
argument_list|,
name|req
argument_list|)
return|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

