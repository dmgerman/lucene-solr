begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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
name|BufferedReader
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
name|Date
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TrecDocMakerTest
specifier|public
class|class
name|TrecDocMakerTest
extends|extends
name|TestCase
block|{
comment|/** A TrecDocMaker which works on a String and not files. */
DECL|class|StringableTrecDocMaker
specifier|private
specifier|static
class|class
name|StringableTrecDocMaker
extends|extends
name|TrecDocMaker
block|{
DECL|field|docs
specifier|private
name|String
name|docs
init|=
literal|null
decl_stmt|;
DECL|method|StringableTrecDocMaker
specifier|public
name|StringableTrecDocMaker
parameter_list|(
name|String
name|docs
parameter_list|,
name|boolean
name|forever
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|forever
operator|=
name|forever
expr_stmt|;
block|}
DECL|method|openNextFile
specifier|protected
name|void
name|openNextFile
parameter_list|()
throws|throws
name|NoMoreDataException
throws|,
name|Exception
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|forever
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
operator|++
name|iteration
expr_stmt|;
block|}
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertDocData
specifier|private
name|void
name|assertDocData
parameter_list|(
name|DocData
name|dd
parameter_list|,
name|String
name|expName
parameter_list|,
name|String
name|expTitle
parameter_list|,
name|String
name|expBody
parameter_list|,
name|Date
name|expDate
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|dd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expName
argument_list|,
name|dd
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTitle
argument_list|,
name|dd
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|dd
operator|.
name|getBody
argument_list|()
operator|.
name|indexOf
argument_list|(
name|expBody
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expDate
argument_list|,
name|dd
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoMoreDataException
specifier|private
name|void
name|assertNoMoreDataException
parameter_list|(
name|StringableTrecDocMaker
name|stdm
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|thrown
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stdm
operator|.
name|getNextDocData
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
name|thrown
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Expecting NoMoreDataException"
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
DECL|method|testOneDocument
specifier|public
name|void
name|testOneDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-000</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Date: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-000 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-000 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>"
decl_stmt|;
name|StringableTrecDocMaker
name|stdm
init|=
operator|new
name|StringableTrecDocMaker
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stdm
operator|.
name|setHTMLParser
argument_list|(
operator|new
name|DemoHTMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
decl_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000_0"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
name|stdm
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|stdm
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoDocuments
specifier|public
name|void
name|testTwoDocuments
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-000</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Date: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-000 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-000 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>\r\n"
operator|+
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-001</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Date: Sun, 11 Jan 2009 08:01:00 GMT\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2008 08:01:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-001 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-001 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>"
decl_stmt|;
name|StringableTrecDocMaker
name|stdm
init|=
operator|new
name|StringableTrecDocMaker
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stdm
operator|.
name|setHTMLParser
argument_list|(
operator|new
name|DemoHTMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
decl_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000_0"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
name|stdm
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|dd
operator|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
expr_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-001_0"
argument_list|,
literal|"TEST-001 title"
argument_list|,
literal|"TEST-001 text"
argument_list|,
name|stdm
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:01:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|stdm
argument_list|)
expr_stmt|;
block|}
comment|// If a Date: attribute is missing, make sure the document is not skipped, but
comment|// rather that null Data is assigned.
DECL|method|testMissingDate
specifier|public
name|void
name|testMissingDate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-000</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-000 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-000 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>\r\n"
operator|+
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-001</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Date: Sun, 11 Jan 2009 08:01:00 GMT\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2009 08:01:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-001 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-001 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>"
decl_stmt|;
name|StringableTrecDocMaker
name|stdm
init|=
operator|new
name|StringableTrecDocMaker
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stdm
operator|.
name|setHTMLParser
argument_list|(
operator|new
name|DemoHTMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
decl_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000_0"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dd
operator|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
expr_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-001_0"
argument_list|,
literal|"TEST-001 title"
argument_list|,
literal|"TEST-001 text"
argument_list|,
name|stdm
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:01:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|stdm
argument_list|)
expr_stmt|;
block|}
comment|// When a 'bad date' is input (unparsable date), make sure the DocData date is
comment|// assigned null.
DECL|method|testBadDate
specifier|public
name|void
name|testBadDate
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-000</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Date: Bad Date\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-000 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-000 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>"
decl_stmt|;
name|StringableTrecDocMaker
name|stdm
init|=
operator|new
name|StringableTrecDocMaker
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|stdm
operator|.
name|setHTMLParser
argument_list|(
operator|new
name|DemoHTMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
decl_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000_0"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|stdm
argument_list|)
expr_stmt|;
block|}
DECL|method|testForever
specifier|public
name|void
name|testForever
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|docs
init|=
literal|"<DOC>\r\n"
operator|+
literal|"<DOCNO>TEST-000</DOCNO>\r\n"
operator|+
literal|"<DOCHDR>\r\n"
operator|+
literal|"http://lucene.apache.org.trecdocmaker.test\r\n"
operator|+
literal|"HTTP/1.1 200 OK\r\n"
operator|+
literal|"Date: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Server: Apache/1.3.27 (Unix)\r\n"
operator|+
literal|"Last-Modified: Sun, 11 Jan 2009 08:00:00 GMT\r\n"
operator|+
literal|"Content-Length: 614\r\n"
operator|+
literal|"Connection: close\r\n"
operator|+
literal|"Content-Type: text/html\r\n"
operator|+
literal|"</DOCHDR>\r\n"
operator|+
literal|"<html>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<head>\r\n"
operator|+
literal|"<title>\r\n"
operator|+
literal|"TEST-000 title\r\n"
operator|+
literal|"</title>\r\n"
operator|+
literal|"</head>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"<body>\r\n"
operator|+
literal|"TEST-000 text\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</body>\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"</DOC>"
decl_stmt|;
name|StringableTrecDocMaker
name|stdm
init|=
operator|new
name|StringableTrecDocMaker
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|stdm
operator|.
name|setHTMLParser
argument_list|(
operator|new
name|DemoHTMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
decl_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000_0"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
name|stdm
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
comment|// same document, but the second iteration changes the name.
name|dd
operator|=
name|stdm
operator|.
name|getNextDocData
argument_list|()
expr_stmt|;
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000_1"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
name|stdm
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Don't test that NoMoreDataException is thrown, since the forever flag is
comment|// turned on.
block|}
block|}
end_class

end_unit

