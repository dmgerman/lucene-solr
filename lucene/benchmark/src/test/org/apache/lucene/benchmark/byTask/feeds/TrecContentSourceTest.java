begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IOException
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
operator|.
name|TrecDocParser
operator|.
name|ParsePathType
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|Config
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
name|document
operator|.
name|DateTools
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

begin_class
DECL|class|TrecContentSourceTest
specifier|public
class|class
name|TrecContentSourceTest
extends|extends
name|LuceneTestCase
block|{
comment|/** A TrecDocMaker which works on a String and not files. */
DECL|class|StringableTrecSource
specifier|private
specifier|static
class|class
name|StringableTrecSource
extends|extends
name|TrecContentSource
block|{
DECL|field|docs
specifier|private
name|String
name|docs
init|=
literal|null
decl_stmt|;
DECL|method|StringableTrecSource
specifier|public
name|StringableTrecSource
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
annotation|@
name|Override
DECL|method|openNextFile
name|void
name|openNextFile
parameter_list|()
throws|throws
name|NoMoreDataException
throws|,
name|IOException
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
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|htmlParser
operator|=
operator|new
name|DemoHTMLParser
argument_list|()
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
throws|throws
name|ParseException
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
name|Date
name|date
init|=
name|dd
operator|.
name|getDate
argument_list|()
operator|!=
literal|null
condition|?
name|DateTools
operator|.
name|stringToDate
argument_list|(
name|dd
operator|.
name|getDate
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|assertEquals
argument_list|(
name|expDate
argument_list|,
name|date
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNoMoreDataException
specifier|private
name|void
name|assertNoMoreDataException
parameter_list|(
name|StringableTrecSource
name|stdm
parameter_list|)
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|NoMoreDataException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|stdm
operator|.
name|getNextDocData
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|StringableTrecSource
name|source
init|=
operator|new
name|StringableTrecSource
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
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
name|source
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|source
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
literal|"<meta name=\"date\" content=\"Tue&#44; 09 Dec 2003 22&#58;39&#58;08 GMT\">"
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
name|StringableTrecSource
name|source
init|=
operator|new
name|StringableTrecSource
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
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
name|source
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|dd
operator|=
name|source
operator|.
name|getNextDocData
argument_list|(
name|dd
argument_list|)
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
name|source
operator|.
name|parseDate
argument_list|(
literal|"Tue, 09 Dec 2003 22:39:08 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|source
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
name|StringableTrecSource
name|source
init|=
operator|new
name|StringableTrecSource
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
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
name|source
operator|.
name|getNextDocData
argument_list|(
name|dd
argument_list|)
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
name|source
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:01:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMoreDataException
argument_list|(
name|source
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
name|StringableTrecSource
name|source
init|=
operator|new
name|StringableTrecSource
argument_list|(
name|docs
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
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
name|source
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
name|StringableTrecSource
name|source
init|=
operator|new
name|StringableTrecSource
argument_list|(
name|docs
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|DocData
name|dd
init|=
name|source
operator|.
name|getNextDocData
argument_list|(
operator|new
name|DocData
argument_list|()
argument_list|)
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
name|source
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
name|source
operator|.
name|getNextDocData
argument_list|(
name|dd
argument_list|)
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
name|source
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Don't test that NoMoreDataException is thrown, since the forever flag is
comment|// turned on.
block|}
comment|/**     * Open a trec content source over a directory with files of all trec path types and all    * supported formats - bzip, gzip, txt.     */
DECL|method|testTrecFeedDirAllTypes
specifier|public
name|void
name|testTrecFeedDirAllTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|dataDir
init|=
name|createTempDir
argument_list|(
literal|"trecFeedAllTypes"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|unzip
argument_list|(
name|getDataInputStream
argument_list|(
literal|"trecdocs.zip"
argument_list|)
argument_list|,
name|dataDir
argument_list|)
expr_stmt|;
name|TrecContentSource
name|tcs
init|=
operator|new
name|TrecContentSource
argument_list|()
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"print.props"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"content.source.verbose"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"content.source.excludeIteration"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"docs.dir"
argument_list|,
name|dataDir
operator|.
name|toRealPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|'\\'
argument_list|,
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"trec.doc.parser"
argument_list|,
name|TrecParserByPath
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"content.source.forever"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|tcs
operator|.
name|setConfig
argument_list|(
operator|new
name|Config
argument_list|(
name|props
argument_list|)
argument_list|)
expr_stmt|;
name|tcs
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|DocData
name|dd
init|=
operator|new
name|DocData
argument_list|()
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
name|boolean
name|gotExpectedException
init|=
literal|false
decl_stmt|;
name|HashSet
argument_list|<
name|ParsePathType
argument_list|>
name|unseenTypes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|ParsePathType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|n
operator|<
literal|100
condition|)
block|{
comment|// arbiterary limit to prevent looping forever in case of test failure
name|dd
operator|=
name|tcs
operator|.
name|getNextDocData
argument_list|(
name|dd
argument_list|)
expr_stmt|;
operator|++
name|n
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"doc data "
operator|+
name|n
operator|+
literal|" should not be null!"
argument_list|,
name|dd
argument_list|)
expr_stmt|;
name|unseenTypes
operator|.
name|remove
argument_list|(
name|tcs
operator|.
name|currPathType
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|tcs
operator|.
name|currPathType
condition|)
block|{
case|case
name|GOV2
case|:
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-000"
argument_list|,
literal|"TEST-000 title"
argument_list|,
literal|"TEST-000 text"
argument_list|,
name|tcs
operator|.
name|parseDate
argument_list|(
literal|"Sun, 11 Jan 2009 08:00:00 GMT"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FBIS
case|:
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-001"
argument_list|,
literal|"TEST-001 Title"
argument_list|,
literal|"TEST-001 text"
argument_list|,
name|tcs
operator|.
name|parseDate
argument_list|(
literal|"1 January 1991"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FR94
case|:
comment|// no title extraction in this source for now
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-002"
argument_list|,
literal|null
argument_list|,
literal|"DEPARTMENT OF SOMETHING"
argument_list|,
name|tcs
operator|.
name|parseDate
argument_list|(
literal|"February 3, 1994"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|FT
case|:
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-003"
argument_list|,
literal|"Test-003 title"
argument_list|,
literal|"Some pub text"
argument_list|,
name|tcs
operator|.
name|parseDate
argument_list|(
literal|"980424"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|LATIMES
case|:
name|assertDocData
argument_list|(
name|dd
argument_list|,
literal|"TEST-004"
argument_list|,
literal|"Test-004 Title"
argument_list|,
literal|"Some paragraph"
argument_list|,
name|tcs
operator|.
name|parseDate
argument_list|(
literal|"January 17, 1997, Sunday"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|assertTrue
argument_list|(
literal|"Should never get here!"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoMoreDataException
name|e
parameter_list|)
block|{
name|gotExpectedException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Should have gotten NoMoreDataException!"
argument_list|,
name|gotExpectedException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of documents created by source!"
argument_list|,
literal|5
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Did not see all types!"
argument_list|,
name|unseenTypes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

