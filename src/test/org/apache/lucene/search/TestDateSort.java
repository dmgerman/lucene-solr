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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|index
operator|.
name|IndexWriter
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
name|queryParser
operator|.
name|QueryParser
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
name|Query
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
name|Sort
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
name|SortField
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
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_comment
comment|/**  * Test date sorting, i.e. auto-sorting of fields with type "long".  * See http://issues.apache.org/jira/browse/LUCENE-1045   */
end_comment

begin_class
DECL|class|TestDateSort
specifier|public
class|class
name|TestDateSort
extends|extends
name|TestCase
block|{
DECL|field|TEXT_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|TEXT_FIELD
init|=
literal|"text"
decl_stmt|;
DECL|field|DATE_TIME_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|DATE_TIME_FIELD
init|=
literal|"dateTime"
decl_stmt|;
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create an index writer.
name|directory
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
comment|// oldest doc:
comment|// Add the first document.  text = "Document 1"  dateTime = Oct 10 03:25:22 EDT 2007
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"Document 1"
argument_list|,
literal|1192001122000L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the second document.  text = "Document 2"  dateTime = Oct 10 03:25:26 EDT 2007
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"Document 2"
argument_list|,
literal|1192001126000L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the third document.  text = "Document 3"  dateTime = Oct 11 07:12:13 EDT 2007
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"Document 3"
argument_list|,
literal|1192101133000L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add the fourth document.  text = "Document 4"  dateTime = Oct 11 08:02:09 EDT 2007
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"Document 4"
argument_list|,
literal|1192104129000L
argument_list|)
argument_list|)
expr_stmt|;
comment|// latest doc:
comment|// Add the fifth document.  text = "Document 5"  dateTime = Oct 12 13:25:43 EDT 2007
name|writer
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"Document 5"
argument_list|,
literal|1192209943000L
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testReverseDateSort
specifier|public
name|void
name|testReverseDateSort
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|directory
argument_list|)
decl_stmt|;
comment|// Create a Sort object.  reverse is set to true.
comment|// problem occurs only with SortField.AUTO:
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|DATE_TIME_FIELD
argument_list|,
name|SortField
operator|.
name|AUTO
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|QueryParser
name|queryParser
init|=
operator|new
name|QueryParser
argument_list|(
name|TEXT_FIELD
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|queryParser
operator|.
name|parse
argument_list|(
literal|"Document"
argument_list|)
decl_stmt|;
comment|// Execute the search and process the search results.
name|String
index|[]
name|actualOrder
init|=
operator|new
name|String
index|[
literal|5
index|]
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
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
name|hits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|document
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|document
operator|.
name|get
argument_list|(
name|TEXT_FIELD
argument_list|)
decl_stmt|;
name|actualOrder
index|[
name|i
index|]
operator|=
name|text
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Set up the expected order (i.e. Document 5, 4, 3, 2, 1).
name|String
index|[]
name|expectedOrder
init|=
operator|new
name|String
index|[
literal|5
index|]
decl_stmt|;
name|expectedOrder
index|[
literal|0
index|]
operator|=
literal|"Document 5"
expr_stmt|;
name|expectedOrder
index|[
literal|1
index|]
operator|=
literal|"Document 4"
expr_stmt|;
name|expectedOrder
index|[
literal|2
index|]
operator|=
literal|"Document 3"
expr_stmt|;
name|expectedOrder
index|[
literal|3
index|]
operator|=
literal|"Document 2"
expr_stmt|;
name|expectedOrder
index|[
literal|4
index|]
operator|=
literal|"Document 1"
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expectedOrder
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|actualOrder
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createDocument
specifier|private
specifier|static
name|Document
name|createDocument
parameter_list|(
name|String
name|text
parameter_list|,
name|long
name|time
parameter_list|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Add the text field.
name|Field
name|textField
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|textField
argument_list|)
expr_stmt|;
comment|// Add the date/time field.
name|String
name|dateTimeString
init|=
name|DateTools
operator|.
name|timeToString
argument_list|(
name|time
argument_list|,
name|DateTools
operator|.
name|Resolution
operator|.
name|SECOND
argument_list|)
decl_stmt|;
name|Field
name|dateTimeField
init|=
operator|new
name|Field
argument_list|(
name|DATE_TIME_FIELD
argument_list|,
name|dateTimeString
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|dateTimeField
argument_list|)
expr_stmt|;
return|return
name|document
return|;
block|}
block|}
end_class

end_unit

