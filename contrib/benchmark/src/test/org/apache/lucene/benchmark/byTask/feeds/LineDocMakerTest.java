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
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|commons
operator|.
name|compress
operator|.
name|compressors
operator|.
name|CompressorStreamFactory
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
name|analysis
operator|.
name|SimpleAnalyzer
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
name|BenchmarkTestCase
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
name|PerfRunData
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
name|tasks
operator|.
name|AddDocTask
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
name|tasks
operator|.
name|CloseIndexTask
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
name|tasks
operator|.
name|CreateIndexTask
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
name|tasks
operator|.
name|TaskSequence
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
name|tasks
operator|.
name|WriteLineDocTask
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
name|index
operator|.
name|Term
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|TermQuery
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
name|search
operator|.
name|TopDocs
import|;
end_import

begin_comment
comment|/** Tests the functionality of {@link LineDocMaker}. */
end_comment

begin_class
DECL|class|LineDocMakerTest
specifier|public
class|class
name|LineDocMakerTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|field|csFactory
specifier|private
specifier|static
specifier|final
name|CompressorStreamFactory
name|csFactory
init|=
operator|new
name|CompressorStreamFactory
argument_list|()
decl_stmt|;
DECL|method|createBZ2LineFile
specifier|private
name|void
name|createBZ2LineFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|out
operator|=
name|csFactory
operator|.
name|createCompressorOutputStream
argument_list|(
literal|"bzip2"
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"utf-8"
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuffer
name|doc
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|doc
operator|.
name|append
argument_list|(
literal|"title"
argument_list|)
operator|.
name|append
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
operator|.
name|append
argument_list|(
literal|"date"
argument_list|)
operator|.
name|append
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
operator|.
name|append
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|doc
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createRegularLineFile
specifier|private
name|void
name|createRegularLineFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
literal|"utf-8"
argument_list|)
argument_list|)
decl_stmt|;
name|StringBuffer
name|doc
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|doc
operator|.
name|append
argument_list|(
literal|"title"
argument_list|)
operator|.
name|append
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
operator|.
name|append
argument_list|(
literal|"date"
argument_list|)
operator|.
name|append
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
operator|.
name|append
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|doc
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doIndexAndSearchTest
specifier|private
name|void
name|doIndexAndSearchTest
parameter_list|(
name|File
name|file
parameter_list|,
name|boolean
name|setBZCompress
parameter_list|,
name|String
name|bz2CompressVal
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|// LineDocMaker specific settings.
name|props
operator|.
name|setProperty
argument_list|(
literal|"docs.file"
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|setBZCompress
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"bzip.compression"
argument_list|,
name|bz2CompressVal
argument_list|)
expr_stmt|;
block|}
comment|// Indexing configuration.
name|props
operator|.
name|setProperty
argument_list|(
literal|"analyzer"
argument_list|,
name|SimpleAnalyzer
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
literal|"doc.maker"
argument_list|,
name|LineDocMaker
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
literal|"directory"
argument_list|,
literal|"RAMDirectory"
argument_list|)
expr_stmt|;
comment|// Create PerfRunData
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
operator|new
name|PerfRunData
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|TaskSequence
name|tasks
init|=
operator|new
name|TaskSequence
argument_list|(
name|runData
argument_list|,
literal|"testBzip2"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|addTask
argument_list|(
operator|new
name|CreateIndexTask
argument_list|(
name|runData
argument_list|)
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|addTask
argument_list|(
operator|new
name|AddDocTask
argument_list|(
name|runData
argument_list|)
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|addTask
argument_list|(
operator|new
name|CloseIndexTask
argument_list|(
name|runData
argument_list|)
argument_list|)
expr_stmt|;
name|tasks
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|runData
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"body"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/* Tests LineDocMaker with a bzip2 input stream. */
DECL|method|testBZip2
specifier|public
name|void
name|testBZip2
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"one-line.bz2"
argument_list|)
decl_stmt|;
name|createBZ2LineFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|doIndexAndSearchTest
argument_list|(
name|file
argument_list|,
literal|true
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBZip2AutoDetect
specifier|public
name|void
name|testBZip2AutoDetect
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"one-line.bz2"
argument_list|)
decl_stmt|;
name|createBZ2LineFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|doIndexAndSearchTest
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegularFile
specifier|public
name|void
name|testRegularFile
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"one-line"
argument_list|)
decl_stmt|;
name|createRegularLineFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|doIndexAndSearchTest
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidFormat
specifier|public
name|void
name|testInvalidFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|testCases
init|=
operator|new
name|String
index|[]
block|{
literal|""
block|,
comment|// empty line
literal|"title"
block|,
comment|// just title
literal|"title"
operator|+
name|WriteLineDocTask
operator|.
name|SEP
block|,
comment|// title + SEP
literal|"title"
operator|+
name|WriteLineDocTask
operator|.
name|SEP
operator|+
literal|"body"
block|,
comment|// title + SEP + body
comment|// note that title + SEP + body + SEP is a valid line, which results in an
comment|// empty body
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testCases
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|getWorkDir
argument_list|()
argument_list|,
literal|"one-line"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
argument_list|,
literal|"utf-8"
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|testCases
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|doIndexAndSearchTest
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Some exception should have been thrown for: ["
operator|+
name|testCases
index|[
name|i
index|]
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected.
block|}
block|}
block|}
block|}
end_class

end_unit

