begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|BinaryResponseParser
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
operator|.
name|ByteArrayStream
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
operator|.
name|StringStream
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
name|AfterClass
import|;
end_import

begin_comment
comment|/**  * Tests the {@link RawResponseWriter} behavior, in particular when dealing with "base" writer  */
end_comment

begin_class
DECL|class|TestRawResponseWriter
specifier|public
class|class
name|TestRawResponseWriter
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|writerXmlBase
specifier|private
specifier|static
name|RawResponseWriter
name|writerXmlBase
decl_stmt|;
DECL|field|writerJsonBase
specifier|private
specifier|static
name|RawResponseWriter
name|writerJsonBase
decl_stmt|;
DECL|field|writerBinBase
specifier|private
specifier|static
name|RawResponseWriter
name|writerBinBase
decl_stmt|;
DECL|field|writerNoBase
specifier|private
specifier|static
name|RawResponseWriter
name|writerNoBase
decl_stmt|;
DECL|field|allWriters
specifier|private
specifier|static
name|RawResponseWriter
index|[]
name|allWriters
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCoreAndWriters
specifier|public
specifier|static
name|void
name|setupCoreAndWriters
parameter_list|()
throws|throws
name|Exception
block|{
comment|// we don't directly use this core or its config, we use
comment|// QueryResponseWriters' constructed programmatically,
comment|// but we do use this core for managing the life cycle of the requests
comment|// we spin up.
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|writerNoBase
operator|=
name|newRawResponseWriter
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|/* defaults to standard writer as base */
name|writerXmlBase
operator|=
name|newRawResponseWriter
argument_list|(
literal|"xml"
argument_list|)
expr_stmt|;
name|writerJsonBase
operator|=
name|newRawResponseWriter
argument_list|(
literal|"json"
argument_list|)
expr_stmt|;
name|writerBinBase
operator|=
name|newRawResponseWriter
argument_list|(
literal|"javabin"
argument_list|)
expr_stmt|;
name|allWriters
operator|=
operator|new
name|RawResponseWriter
index|[]
block|{
name|writerXmlBase
block|,
name|writerJsonBase
block|,
name|writerBinBase
block|,
name|writerNoBase
block|}
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanupWriters
specifier|public
specifier|static
name|void
name|cleanupWriters
parameter_list|()
throws|throws
name|Exception
block|{
name|writerXmlBase
operator|=
literal|null
expr_stmt|;
name|writerJsonBase
operator|=
literal|null
expr_stmt|;
name|writerBinBase
operator|=
literal|null
expr_stmt|;
name|writerNoBase
operator|=
literal|null
expr_stmt|;
name|allWriters
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Regardless of base writer, the bytes in should be the same as the bytes out     * when response is a raw ContentStream written to an OutputStream    */
DECL|method|testRawBinaryContentStream
specifier|public
name|void
name|testRawBinaryContentStream
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|2048
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|ByteArrayStream
name|stream
init|=
operator|new
name|ByteArrayStream
argument_list|(
name|data
argument_list|,
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setContentType
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|stream
argument_list|)
expr_stmt|;
for|for
control|(
name|RawResponseWriter
name|writer
range|:
name|allWriters
control|)
block|{
name|assertEquals
argument_list|(
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|,
name|writer
operator|.
name|getContentType
argument_list|(
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Regardless of base writer, the String in should be the same as the String out     * when response is a raw ContentStream written to a Writer (or OutputStream)    */
DECL|method|testRawStringContentStream
specifier|public
name|void
name|testRawStringContentStream
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|String
name|data
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|StringStream
name|stream
init|=
operator|new
name|StringStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setContentType
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|stream
argument_list|)
expr_stmt|;
for|for
control|(
name|RawResponseWriter
name|writer
range|:
name|allWriters
control|)
block|{
name|assertEquals
argument_list|(
name|stream
operator|.
name|getContentType
argument_list|()
argument_list|,
name|writer
operator|.
name|getContentType
argument_list|(
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
comment|// we should have the same string if we use a Writer
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|sout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
argument_list|,
name|sout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// we should have UTF-8 Bytes if we use an OutputStream
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|bout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|data
argument_list|,
name|bout
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * When no real ContentStream is specified, each base writer should be used for formatting    */
DECL|method|testStructuredDataViaBaseWriters
specifier|public
name|void
name|testStructuredDataViaBaseWriters
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
comment|// Don't send a ContentStream back, this will fall back to the configured base writer.
comment|// But abuse the CONTENT key to ensure writer is also checking type
name|rsp
operator|.
name|add
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
comment|// check Content-Type against each writer
name|assertEquals
argument_list|(
literal|"application/xml; charset=UTF-8"
argument_list|,
name|writerNoBase
operator|.
name|getContentType
argument_list|(
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/xml; charset=UTF-8"
argument_list|,
name|writerXmlBase
operator|.
name|getContentType
argument_list|(
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/json; charset=UTF-8"
argument_list|,
name|writerJsonBase
operator|.
name|getContentType
argument_list|(
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/octet-stream"
argument_list|,
name|writerBinBase
operator|.
name|getContentType
argument_list|(
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
argument_list|)
expr_stmt|;
comment|// check response against each writer
comment|// xml& none (default behavior same as XML)
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<response>\n<str name=\"content\">test</str><str name=\"foo\">bar</str>\n</response>\n"
decl_stmt|;
name|StringWriter
name|xmlSout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|writerXmlBase
operator|.
name|write
argument_list|(
name|xmlSout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xml
argument_list|,
name|xmlSout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|xmlBout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writerXmlBase
operator|.
name|write
argument_list|(
name|xmlBout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xml
argument_list|,
name|xmlBout
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//
name|StringWriter
name|noneSout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|writerNoBase
operator|.
name|write
argument_list|(
name|noneSout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xml
argument_list|,
name|noneSout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|noneBout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writerNoBase
operator|.
name|write
argument_list|(
name|noneBout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|xml
argument_list|,
name|noneBout
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// json
name|String
name|json
init|=
literal|"{\"content\":\"test\",\"foo\":\"bar\"}\n"
decl_stmt|;
name|StringWriter
name|jsonSout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|writerJsonBase
operator|.
name|write
argument_list|(
name|jsonSout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
name|jsonSout
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|jsonBout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writerJsonBase
operator|.
name|write
argument_list|(
name|jsonBout
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|json
argument_list|,
name|jsonBout
operator|.
name|toString
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// javabin
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|writerBinBase
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
name|req
argument_list|()
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|BinaryResponseParser
name|parser
init|=
operator|new
name|BinaryResponseParser
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|out
init|=
name|parser
operator|.
name|processResponse
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|,
comment|/* encoding irrelevant */
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|RawResponseWriter
operator|.
name|CONTENT
argument_list|,
name|out
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|out
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|out
operator|.
name|getName
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|out
operator|.
name|getVal
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generates a new {@link RawResponseWriter} wrapping the specified baseWriter name     * (which much either be an implicitly defined response writer, or one explicitly     * configured in solrconfig.xml)    *    * @param baseWriter null or the name of a valid base writer    */
DECL|method|newRawResponseWriter
specifier|private
specifier|static
name|RawResponseWriter
name|newRawResponseWriter
parameter_list|(
name|String
name|baseWriter
parameter_list|)
block|{
name|RawResponseWriter
name|writer
init|=
operator|new
name|RawResponseWriter
argument_list|()
decl_stmt|;
name|NamedList
name|initArgs
init|=
operator|new
name|NamedList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|baseWriter
condition|)
block|{
name|initArgs
operator|.
name|add
argument_list|(
literal|"base"
argument_list|,
name|baseWriter
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|init
argument_list|(
name|initArgs
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
block|}
end_class

end_unit

