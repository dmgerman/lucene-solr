begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
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
name|tasks
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|feeds
operator|.
name|DocMaker
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
name|benchmark
operator|.
name|byTask
operator|.
name|utils
operator|.
name|StreamUtils
operator|.
name|Type
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
name|Document
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
name|Field
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
name|StringField
import|;
end_import

begin_comment
comment|/** Tests the functionality of {@link WriteLineDocTask}. */
end_comment

begin_class
DECL|class|WriteLineDocTaskTest
specifier|public
class|class
name|WriteLineDocTaskTest
extends|extends
name|BenchmarkTestCase
block|{
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|WriteLineDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|WriteLineDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|BODY_FIELD
argument_list|,
literal|"body"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TITLE_FIELD
argument_list|,
literal|"title"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|NewLinesDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|NewLinesDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|BODY_FIELD
argument_list|,
literal|"body\r\ntext\ttwo"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TITLE_FIELD
argument_list|,
literal|"title\r\ntext"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date\r\ntext"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|NoBodyDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|NoBodyDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TITLE_FIELD
argument_list|,
literal|"title"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|NoTitleDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|NoTitleDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|BODY_FIELD
argument_list|,
literal|"body"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|JustDateDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|JustDateDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
comment|// same as JustDate just that this one is treated as legal
DECL|class|LegalJustDateDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|LegalJustDateDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|EmptyDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|EmptyDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|Document
argument_list|()
return|;
block|}
block|}
comment|// class has to be public so that Class.forName.newInstance() will work
DECL|class|ThreadingDocMaker
specifier|public
specifier|static
specifier|final
class|class
name|ThreadingDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|makeDocument
specifier|public
name|Document
name|makeDocument
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|BODY_FIELD
argument_list|,
literal|"body_"
operator|+
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TITLE_FIELD
argument_list|,
literal|"title_"
operator|+
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|DATE_FIELD
argument_list|,
literal|"date_"
operator|+
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
block|}
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
DECL|method|createPerfRunData
specifier|private
name|PerfRunData
name|createPerfRunData
parameter_list|(
name|Path
name|file
parameter_list|,
name|boolean
name|allowEmptyDocs
parameter_list|,
name|String
name|docMakerName
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
name|props
operator|.
name|setProperty
argument_list|(
literal|"doc.maker"
argument_list|,
name|docMakerName
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"line.file.out"
argument_list|,
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
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
comment|// no accidental FS dir.
if|if
condition|(
name|allowEmptyDocs
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"sufficient.fields"
argument_list|,
literal|","
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docMakerName
operator|.
name|equals
argument_list|(
name|LegalJustDateDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"line.fields"
argument_list|,
name|DocMaker
operator|.
name|DATE_FIELD
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"sufficient.fields"
argument_list|,
name|DocMaker
operator|.
name|DATE_FIELD
argument_list|)
expr_stmt|;
block|}
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
return|return
operator|new
name|PerfRunData
argument_list|(
name|config
argument_list|)
return|;
block|}
DECL|method|doReadTest
specifier|private
name|void
name|doReadTest
parameter_list|(
name|Path
name|file
parameter_list|,
name|Type
name|fileType
parameter_list|,
name|String
name|expTitle
parameter_list|,
name|String
name|expDate
parameter_list|,
name|String
name|expBody
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|in
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|fileType
condition|)
block|{
case|case
name|BZIP2
case|:
name|in
operator|=
name|csFactory
operator|.
name|createCompressorInputStream
argument_list|(
name|CompressorStreamFactory
operator|.
name|BZIP2
argument_list|,
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|GZIP
case|:
name|in
operator|=
name|csFactory
operator|.
name|createCompressorInputStream
argument_list|(
name|CompressorStreamFactory
operator|.
name|GZIP
argument_list|,
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|PLAIN
case|:
break|break;
comment|// nothing to do
default|default:
name|assertFalse
argument_list|(
literal|"Unknown file type!"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//fail, should not happen
block|}
try|try
init|(
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertHeaderLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|String
index|[]
name|parts
init|=
name|line
operator|.
name|split
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|numExpParts
init|=
name|expBody
operator|==
literal|null
condition|?
literal|2
else|:
literal|3
decl_stmt|;
name|assertEquals
argument_list|(
name|numExpParts
argument_list|,
name|parts
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expTitle
argument_list|,
name|parts
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expDate
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|expBody
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expBody
argument_list|,
name|parts
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|br
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertHeaderLine
specifier|static
name|void
name|assertHeaderLine
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"First line should be a header line"
argument_list|,
name|line
operator|.
name|startsWith
argument_list|(
name|WriteLineDocTask
operator|.
name|FIELDS_HEADER_INDICATOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* Tests WriteLineDocTask with a bzip2 format. */
DECL|method|testBZip2
specifier|public
name|void
name|testBZip2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a document in bz2 format.
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line.bz2"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|WriteLineDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
name|Type
operator|.
name|BZIP2
argument_list|,
literal|"title"
argument_list|,
literal|"date"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
block|}
comment|/* Tests WriteLineDocTask with a gzip format. */
DECL|method|testGZip
specifier|public
name|void
name|testGZip
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a document in gz format.
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line.gz"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|WriteLineDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
name|Type
operator|.
name|GZIP
argument_list|,
literal|"title"
argument_list|,
literal|"date"
argument_list|,
literal|"body"
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
comment|// Create a document in regular format.
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|WriteLineDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
name|Type
operator|.
name|PLAIN
argument_list|,
literal|"title"
argument_list|,
literal|"date"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCharsReplace
specifier|public
name|void
name|testCharsReplace
parameter_list|()
throws|throws
name|Exception
block|{
comment|// WriteLineDocTask replaced only \t characters w/ a space, since that's its
comment|// separator char. However, it didn't replace newline characters, which
comment|// resulted in errors in LineDocSource.
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|NewLinesDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
name|Type
operator|.
name|PLAIN
argument_list|,
literal|"title text"
argument_list|,
literal|"date text"
argument_list|,
literal|"body text two"
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyBody
specifier|public
name|void
name|testEmptyBody
parameter_list|()
throws|throws
name|Exception
block|{
comment|// WriteLineDocTask threw away documents w/ no BODY element, even if they
comment|// had a TITLE element (LUCENE-1755). It should throw away documents if they
comment|// don't have BODY nor TITLE
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|NoBodyDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
name|Type
operator|.
name|PLAIN
argument_list|,
literal|"title"
argument_list|,
literal|"date"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyTitle
specifier|public
name|void
name|testEmptyTitle
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|NoTitleDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|doReadTest
argument_list|(
name|file
argument_list|,
name|Type
operator|.
name|PLAIN
argument_list|,
literal|""
argument_list|,
literal|"date"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
block|}
comment|/** Fail by default when there's only date */
DECL|method|testJustDate
specifier|public
name|void
name|testJustDate
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|JustDateDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
init|(
name|BufferedReader
name|br
init|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|file
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertHeaderLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLegalJustDate
specifier|public
name|void
name|testLegalJustDate
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|LegalJustDateDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
init|(
name|BufferedReader
name|br
init|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|file
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertHeaderLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyDoc
specifier|public
name|void
name|testEmptyDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|true
argument_list|,
name|EmptyDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
init|(
name|BufferedReader
name|br
init|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|file
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertHeaderLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMultiThreaded
specifier|public
name|void
name|testMultiThreaded
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|file
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"one-line"
argument_list|)
decl_stmt|;
name|PerfRunData
name|runData
init|=
name|createPerfRunData
argument_list|(
name|file
argument_list|,
literal|false
argument_list|,
name|ThreadingDocMaker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|WriteLineDocTask
name|wldt
init|=
operator|new
name|WriteLineDocTask
argument_list|(
name|runData
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|10
index|]
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
literal|"t"
operator|+
name|i
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|wldt
operator|.
name|doLogic
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|wldt
operator|.
name|close
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|BufferedReader
name|br
init|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|file
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|assertHeaderLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
comment|// header line is written once, no matter how many threads there are
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|String
index|[]
name|parts
init|=
name|line
operator|.
name|split
argument_list|(
name|Character
operator|.
name|toString
argument_list|(
name|WriteLineDocTask
operator|.
name|SEP
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parts
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check that all thread names written are the same in the same line
name|String
name|tname
init|=
name|parts
index|[
literal|0
index|]
operator|.
name|substring
argument_list|(
name|parts
index|[
literal|0
index|]
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
argument_list|)
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|tname
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tname
argument_list|,
name|parts
index|[
literal|1
index|]
operator|.
name|substring
argument_list|(
name|parts
index|[
literal|1
index|]
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tname
argument_list|,
name|parts
index|[
literal|2
index|]
operator|.
name|substring
argument_list|(
name|parts
index|[
literal|2
index|]
operator|.
name|indexOf
argument_list|(
literal|'_'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// only threads.length lines should exist
name|assertNull
argument_list|(
name|br
operator|.
name|readLine
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|threads
operator|.
name|length
argument_list|,
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

