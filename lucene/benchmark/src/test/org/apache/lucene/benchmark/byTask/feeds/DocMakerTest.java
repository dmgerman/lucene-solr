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
name|PrintStream
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|index
operator|.
name|DirectoryReader
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
name|IndexReader
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
name|IOUtils
import|;
end_import

begin_comment
comment|/** Tests the functionality of {@link DocMaker}. */
end_comment

begin_class
DECL|class|DocMakerTest
specifier|public
class|class
name|DocMakerTest
extends|extends
name|BenchmarkTestCase
block|{
DECL|class|OneDocSource
specifier|public
specifier|static
specifier|final
class|class
name|OneDocSource
extends|extends
name|ContentSource
block|{
DECL|field|finish
specifier|private
name|boolean
name|finish
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|getNextDocData
specifier|public
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
block|{
if|if
condition|(
name|finish
condition|)
block|{
throw|throw
operator|new
name|NoMoreDataException
argument_list|()
throw|;
block|}
name|docData
operator|.
name|setBody
argument_list|(
literal|"body"
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setDate
argument_list|(
literal|"date"
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setTitle
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
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
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|docData
operator|.
name|setProps
argument_list|(
name|props
argument_list|)
expr_stmt|;
name|finish
operator|=
literal|true
expr_stmt|;
return|return
name|docData
return|;
block|}
block|}
DECL|method|doTestIndexProperties
specifier|private
name|void
name|doTestIndexProperties
parameter_list|(
name|boolean
name|setIndexProps
parameter_list|,
name|boolean
name|indexPropsVal
parameter_list|,
name|int
name|numExpectedResults
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
comment|// Indexing configuration.
name|props
operator|.
name|setProperty
argument_list|(
literal|"analyzer"
argument_list|,
name|WhitespaceAnalyzer
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
literal|"content.source"
argument_list|,
name|OneDocSource
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
if|if
condition|(
name|setIndexProps
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"doc.index.props"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|indexPropsVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|getTestName
argument_list|()
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|runData
operator|.
name|getDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
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
literal|"key"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numExpectedResults
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createTestNormsDocument
specifier|private
name|Document
name|createTestNormsDocument
parameter_list|(
name|boolean
name|setNormsProp
parameter_list|,
name|boolean
name|normsPropVal
parameter_list|,
name|boolean
name|setBodyNormsProp
parameter_list|,
name|boolean
name|bodyNormsVal
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
comment|// Indexing configuration.
name|props
operator|.
name|setProperty
argument_list|(
literal|"analyzer"
argument_list|,
name|WhitespaceAnalyzer
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
if|if
condition|(
name|setNormsProp
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"doc.tokenized.norms"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|normsPropVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|setBodyNormsProp
condition|)
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"doc.body.tokenized.norms"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|bodyNormsVal
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|DocMaker
name|dm
init|=
operator|new
name|DocMaker
argument_list|()
decl_stmt|;
name|dm
operator|.
name|setConfig
argument_list|(
name|config
argument_list|,
operator|new
name|OneDocSource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dm
operator|.
name|makeDocument
argument_list|()
return|;
block|}
comment|/* Tests doc.index.props property. */
DECL|method|testIndexProperties
specifier|public
name|void
name|testIndexProperties
parameter_list|()
throws|throws
name|Exception
block|{
comment|// default is to not index properties.
name|doTestIndexProperties
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// set doc.index.props to false.
name|doTestIndexProperties
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// set doc.index.props to true.
name|doTestIndexProperties
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/* Tests doc.tokenized.norms and doc.body.tokenized.norms properties. */
DECL|method|testNorms
specifier|public
name|void
name|testNorms
parameter_list|()
throws|throws
name|Exception
block|{
name|Document
name|doc
decl_stmt|;
comment|// Don't set anything, use the defaults
name|doc
operator|=
name|createTestNormsDocument
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set norms to false
name|doc
operator|=
name|createTestNormsDocument
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set norms to true
name|doc
operator|=
name|createTestNormsDocument
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set body norms to false
name|doc
operator|=
name|createTestNormsDocument
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set body norms to true
name|doc
operator|=
name|createTestNormsDocument
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|TITLE_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|DocMaker
operator|.
name|BODY_FIELD
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocMakerLeak
specifier|public
name|void
name|testDocMakerLeak
parameter_list|()
throws|throws
name|Exception
block|{
comment|// DocMaker did not close its ContentSource if resetInputs was called twice,
comment|// leading to a file handle leak.
name|Path
name|f
init|=
name|getWorkDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"docMakerLeak.txt"
argument_list|)
decl_stmt|;
name|PrintStream
name|ps
init|=
operator|new
name|PrintStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|f
argument_list|)
argument_list|,
literal|true
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|ps
operator|.
name|println
argument_list|(
literal|"one title\t"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"\tsome content"
argument_list|)
expr_stmt|;
name|ps
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|"docs.file"
argument_list|,
name|f
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
literal|"content.source.forever"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|Config
name|config
init|=
operator|new
name|Config
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|ContentSource
name|source
init|=
operator|new
name|LineDocSource
argument_list|()
decl_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|DocMaker
name|dm
init|=
operator|new
name|DocMaker
argument_list|()
decl_stmt|;
name|dm
operator|.
name|setConfig
argument_list|(
name|config
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|dm
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|dm
operator|.
name|resetInputs
argument_list|()
expr_stmt|;
name|dm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

