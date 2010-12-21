begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.html
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|html
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|demo
operator|.
name|html
operator|.
name|HTMLParser
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
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestHtmlParser
specifier|public
class|class
name|TestHtmlParser
extends|extends
name|LuceneTestCase
block|{
DECL|method|testUnicode
specifier|public
name|void
name|testUnicode
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><body>æ±è¯­</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"æ±è¯­"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|testEntities
specifier|public
name|void
name|testEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><body>&#x6C49;&#x8BED;&yen;</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"æ±è¯­Â¥"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|testComments
specifier|public
name|void
name|testComments
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><body>foo<!-- bar --><! baz --></body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"foo"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|testScript
specifier|public
name|void
name|testScript
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><body><script type=\"text/javascript\">"
operator|+
literal|"document.write(\"test\")</script>foo</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"foo"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|testStyle
specifier|public
name|void
name|testStyle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><head><style type=\"text/css\">"
operator|+
literal|"body{background-color:blue;}</style>"
operator|+
literal|"</head><body>foo</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"foo"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoctype
specifier|public
name|void
name|testDoctype
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<!DOCTYPE HTML PUBLIC "
operator|+
literal|"\"-//W3C//DTD HTML 4.01 Transitional//EN\""
operator|+
literal|"\"http://www.w3.org/TR/html4/loose.dtd\">"
operator|+
literal|"<html><body>foo</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"foo"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|testMeta
specifier|public
name|void
name|testMeta
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><head>"
operator|+
literal|"<meta name=\"a\" content=\"1\" />"
operator|+
literal|"<meta name=\"b\" content=\"2\" />"
operator|+
literal|"<meta name=\"keywords\" content=\"this is a test\" />"
operator|+
literal|"<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\" />"
operator|+
literal|"</head><body>foobar</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|Properties
name|tags
init|=
name|parser
operator|.
name|getMetaTags
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tags
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|tags
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|tags
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"this is a test"
argument_list|,
name|tags
operator|.
name|get
argument_list|(
literal|"keywords"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html;charset=utf-8"
argument_list|,
name|tags
operator|.
name|get
argument_list|(
literal|"content-type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTitle
specifier|public
name|void
name|testTitle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><head><TITLE>foo</TITLE><head><body>bar</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|parser
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSummary
specifier|public
name|void
name|testSummary
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><head><TITLE>foo</TITLE><head><body>"
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"Summarize me. Summarize me. Summarize me. Summarize me. "
operator|+
literal|"</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|parser
operator|.
name|getSummary
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-590
DECL|method|testSummaryTitle
specifier|public
name|void
name|testSummaryTitle
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><head><title>Summary</title></head><body>Summary of the document</body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Summary of the document"
argument_list|,
name|parser
operator|.
name|getSummary
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-2246
DECL|method|testTurkish
specifier|public
name|void
name|testTurkish
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
literal|"<html><body>"
operator|+
literal|"<IMG SRC=\"../images/head.jpg\" WIDTH=570 HEIGHT=47 BORDER=0 ALT=\"Å\">"
operator|+
literal|"<a title=\"(Ä±Ä±Ä±)\"></body></html>"
decl_stmt|;
name|HTMLParser
name|parser
init|=
operator|new
name|HTMLParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|assertReadsTo
argument_list|(
literal|"[Å]"
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
DECL|method|assertReadsTo
specifier|private
name|void
name|assertReadsTo
parameter_list|(
name|String
name|expected
parameter_list|,
name|HTMLParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|Reader
name|reader
init|=
name|parser
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|ch
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|ch
operator|=
name|reader
operator|.
name|read
argument_list|()
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

