begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
package|;
end_package

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
name|Analyzer
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
name|MockAnalyzer
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
name|FieldType
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
name|TextField
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
name|LeafReader
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
name|RandomIndexWriter
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
name|store
operator|.
name|BaseDirectoryWrapper
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
name|store
operator|.
name|Directory
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
name|junit
operator|.
name|After
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
name|Test
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
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link org.apache.lucene.classification.utils.DatasetSplitter}  */
end_comment

begin_class
DECL|class|DataSplitterTest
specifier|public
class|class
name|DataSplitterTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|originalIndex
specifier|private
name|LeafReader
name|originalIndex
decl_stmt|;
DECL|field|indexWriter
specifier|private
name|RandomIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|textFieldName
specifier|private
name|String
name|textFieldName
init|=
literal|"text"
decl_stmt|;
DECL|field|classFieldName
specifier|private
name|String
name|classFieldName
init|=
literal|"class"
decl_stmt|;
DECL|field|idFieldName
specifier|private
name|String
name|idFieldName
init|=
literal|"id"
decl_stmt|;
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
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Document
name|doc
decl_stmt|;
name|Random
name|rnd
init|=
name|random
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|idFieldName
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|rnd
argument_list|,
literal|1024
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|classFieldName
argument_list|,
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|rnd
argument_list|,
literal|10
argument_list|)
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|originalIndex
operator|=
name|getOnlyLeafReader
argument_list|(
name|indexWriter
operator|.
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|originalIndex
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplitOnAllFields
specifier|public
name|void
name|testSplitOnAllFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSplit
argument_list|(
name|originalIndex
argument_list|,
literal|0.1
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSplitOnSomeFields
specifier|public
name|void
name|testSplitOnSomeFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSplit
argument_list|(
name|originalIndex
argument_list|,
literal|0.2
argument_list|,
literal|0.35
argument_list|,
name|idFieldName
argument_list|,
name|textFieldName
argument_list|)
expr_stmt|;
block|}
DECL|method|assertSplit
specifier|public
specifier|static
name|void
name|assertSplit
parameter_list|(
name|LeafReader
name|originalIndex
parameter_list|,
name|double
name|testRatio
parameter_list|,
name|double
name|crossValidationRatio
parameter_list|,
name|String
modifier|...
name|fieldNames
parameter_list|)
throws|throws
name|Exception
block|{
name|BaseDirectoryWrapper
name|trainingIndex
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|BaseDirectoryWrapper
name|testIndex
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|BaseDirectoryWrapper
name|crossValidationIndex
init|=
name|newDirectory
argument_list|()
decl_stmt|;
try|try
block|{
name|DatasetSplitter
name|datasetSplitter
init|=
operator|new
name|DatasetSplitter
argument_list|(
name|testRatio
argument_list|,
name|crossValidationRatio
argument_list|)
decl_stmt|;
name|datasetSplitter
operator|.
name|split
argument_list|(
name|originalIndex
argument_list|,
name|trainingIndex
argument_list|,
name|testIndex
argument_list|,
name|crossValidationIndex
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|fieldNames
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|trainingIndex
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testIndex
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|crossValidationIndex
argument_list|)
expr_stmt|;
name|DirectoryReader
name|trainingReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|trainingIndex
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
call|(
name|int
call|)
argument_list|(
name|originalIndex
operator|.
name|maxDoc
argument_list|()
operator|*
operator|(
literal|1d
operator|-
name|testRatio
operator|-
name|crossValidationRatio
operator|)
argument_list|)
operator|==
name|trainingReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|testReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|testIndex
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
call|(
name|int
call|)
argument_list|(
name|originalIndex
operator|.
name|maxDoc
argument_list|()
operator|*
name|testRatio
argument_list|)
operator|==
name|testReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|DirectoryReader
name|cvReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|crossValidationIndex
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
call|(
name|int
call|)
argument_list|(
name|originalIndex
operator|.
name|maxDoc
argument_list|()
operator|*
name|crossValidationRatio
argument_list|)
operator|==
name|cvReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|trainingReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|testReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|cvReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeQuietly
argument_list|(
name|trainingReader
argument_list|)
expr_stmt|;
name|closeQuietly
argument_list|(
name|testReader
argument_list|)
expr_stmt|;
name|closeQuietly
argument_list|(
name|cvReader
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|trainingIndex
operator|!=
literal|null
condition|)
block|{
name|trainingIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|testIndex
operator|!=
literal|null
condition|)
block|{
name|testIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|crossValidationIndex
operator|!=
literal|null
condition|)
block|{
name|crossValidationIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|closeQuietly
specifier|private
specifier|static
name|void
name|closeQuietly
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
end_class

end_unit

