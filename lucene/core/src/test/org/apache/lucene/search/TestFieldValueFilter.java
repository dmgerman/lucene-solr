begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SortedDocValuesField
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
name|BytesRef
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
name|LuceneTestCase
operator|.
name|SuppressCodecs
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
DECL|class|TestFieldValueFilter
specifier|public
class|class
name|TestFieldValueFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|testFieldValueFilterNoValue
specifier|public
name|void
name|testFieldValueFilterNoValue
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|docs
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
index|[]
name|docStates
init|=
name|buildIndex
argument_list|(
name|writer
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|int
name|numDocsNoValue
init|=
literal|0
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
name|docStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docStates
index|[
name|i
index|]
operator|==
literal|0
condition|)
block|{
name|numDocsNoValue
operator|++
expr_stmt|;
block|}
block|}
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
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
name|search
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
literal|"all"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|FieldValueFilter
argument_list|(
literal|"some"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|search
operator|.
name|totalHits
argument_list|,
name|numDocsNoValue
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|search
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|scoreDocs
control|)
block|{
name|assertNull
argument_list|(
name|reader
operator|.
name|document
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFieldValueFilter
specifier|public
name|void
name|testFieldValueFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|docs
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
index|[]
name|docStates
init|=
name|buildIndex
argument_list|(
name|writer
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|int
name|numDocsWithValue
init|=
literal|0
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
name|docStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|docStates
index|[
name|i
index|]
operator|==
literal|1
condition|)
block|{
name|numDocsWithValue
operator|++
expr_stmt|;
block|}
block|}
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
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
name|search
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
literal|"all"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|FieldValueFilter
argument_list|(
literal|"some"
argument_list|)
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|search
operator|.
name|totalHits
argument_list|,
name|numDocsWithValue
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|search
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|scoreDocs
control|)
block|{
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|reader
operator|.
name|document
argument_list|(
name|scoreDoc
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|buildIndex
specifier|private
name|int
index|[]
name|buildIndex
parameter_list|(
name|RandomIndexWriter
name|writer
parameter_list|,
name|int
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
name|int
index|[]
name|docStates
init|=
operator|new
name|int
index|[
name|docs
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
name|docs
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|docStates
index|[
name|i
index|]
operator|=
literal|1
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"some"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"some"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"value"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"all"
argument_list|,
literal|"test"
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
name|SortedDocValuesField
argument_list|(
literal|"all"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
literal|"id"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|int
name|numDeletes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|docs
argument_list|)
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
name|numDeletes
condition|;
name|i
operator|++
control|)
block|{
name|int
name|docID
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|writer
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|docID
argument_list|)
argument_list|)
expr_stmt|;
name|docStates
index|[
name|docID
index|]
operator|=
literal|2
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|docStates
return|;
block|}
block|}
end_class

end_unit

