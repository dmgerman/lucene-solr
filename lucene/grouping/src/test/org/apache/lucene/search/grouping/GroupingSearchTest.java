begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|document
operator|.
name|StringField
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|BytesRefFieldSource
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
name|similarities
operator|.
name|BM25Similarity
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
name|mutable
operator|.
name|MutableValueStr
import|;
end_import

begin_class
DECL|class|GroupingSearchTest
specifier|public
class|class
name|GroupingSearchTest
extends|extends
name|LuceneTestCase
block|{
comment|// Tests some very basic usages...
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|groupField
init|=
literal|"author"
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|canUseIDV
init|=
literal|true
decl_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|documents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// 0
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author1"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"random text"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 1
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author1"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"some more random text"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 2
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author1"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"some more random textual data"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|customType
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
literal|"groupend"
argument_list|,
literal|"x"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocuments
argument_list|(
name|documents
argument_list|)
expr_stmt|;
name|documents
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// 3
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author2"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"some random text"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|customType
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
literal|"groupend"
argument_list|,
literal|"x"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 4
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author3"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"some more random text"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 5
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author3"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"random"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|customType
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
literal|"groupend"
argument_list|,
literal|"x"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocuments
argument_list|(
name|documents
argument_list|)
expr_stmt|;
name|documents
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// 6 -- no author field
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
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"random word stuck in alot of other text"
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
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|customType
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
literal|"groupend"
argument_list|,
literal|"x"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|indexSearcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|BM25Similarity
argument_list|()
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|Sort
name|groupSort
init|=
name|Sort
operator|.
name|RELEVANCE
decl_stmt|;
name|GroupingSearch
name|groupingSearch
init|=
name|createRandomGroupingSearch
argument_list|(
name|groupField
argument_list|,
name|groupSort
argument_list|,
literal|5
argument_list|,
name|canUseIDV
argument_list|)
decl_stmt|;
name|TopGroups
argument_list|<
name|?
argument_list|>
name|groups
init|=
name|groupingSearch
operator|.
name|search
argument_list|(
name|indexSearcher
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"random"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|groups
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|groups
operator|.
name|totalGroupedHitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|groups
operator|.
name|groups
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// relevance order: 5, 0, 3, 4, 1, 2, 6
comment|// the later a document is added the higher this docId
comment|// value
name|GroupDocs
argument_list|<
name|?
argument_list|>
name|group
init|=
name|groups
operator|.
name|groups
index|[
literal|0
index|]
decl_stmt|;
name|compareGroupValue
argument_list|(
literal|"author3"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|group
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|group
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
operator|>=
name|group
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
name|group
operator|=
name|groups
operator|.
name|groups
index|[
literal|1
index|]
expr_stmt|;
name|compareGroupValue
argument_list|(
literal|"author1"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|group
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|2
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|group
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
operator|>=
name|group
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|group
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|score
operator|>=
name|group
operator|.
name|scoreDocs
index|[
literal|2
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
name|group
operator|=
name|groups
operator|.
name|groups
index|[
literal|2
index|]
expr_stmt|;
name|compareGroupValue
argument_list|(
literal|"author2"
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|group
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|group
operator|=
name|groups
operator|.
name|groups
index|[
literal|3
index|]
expr_stmt|;
name|compareGroupValue
argument_list|(
literal|null
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|group
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|group
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|Query
name|lastDocInBlock
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"groupend"
argument_list|,
literal|"x"
argument_list|)
argument_list|)
decl_stmt|;
name|groupingSearch
operator|=
operator|new
name|GroupingSearch
argument_list|(
name|lastDocInBlock
argument_list|)
expr_stmt|;
name|groups
operator|=
name|groupingSearch
operator|.
name|search
argument_list|(
name|indexSearcher
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"random"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|groups
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|groups
operator|.
name|totalGroupedHitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|groups
operator|.
name|totalGroupCount
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|groups
operator|.
name|groups
operator|.
name|length
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addGroupField
specifier|private
name|void
name|addGroupField
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|groupField
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|canUseIDV
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|groupField
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|canUseIDV
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|groupField
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|compareGroupValue
specifier|private
name|void
name|compareGroupValue
parameter_list|(
name|String
name|expected
parameter_list|,
name|GroupDocs
argument_list|<
name|?
argument_list|>
name|group
parameter_list|)
block|{
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|group
operator|.
name|groupValue
operator|==
literal|null
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|group
operator|.
name|groupValue
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|MutableValueStr
operator|.
name|class
argument_list|)
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
operator|(
operator|(
name|BytesRef
operator|)
name|group
operator|.
name|groupValue
operator|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|fail
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|group
operator|.
name|groupValue
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|BytesRef
operator|.
name|class
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|expected
argument_list|)
argument_list|,
name|group
operator|.
name|groupValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|group
operator|.
name|groupValue
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|MutableValueStr
operator|.
name|class
argument_list|)
condition|)
block|{
name|MutableValueStr
name|v
init|=
operator|new
name|MutableValueStr
argument_list|()
decl_stmt|;
name|v
operator|.
name|value
operator|.
name|copyChars
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|v
argument_list|,
name|group
operator|.
name|groupValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|createRandomGroupingSearch
specifier|private
name|GroupingSearch
name|createRandomGroupingSearch
parameter_list|(
name|String
name|groupField
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|int
name|docsInGroup
parameter_list|,
name|boolean
name|canUseIDV
parameter_list|)
block|{
name|GroupingSearch
name|groupingSearch
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
name|ValueSource
name|vs
init|=
operator|new
name|BytesRefFieldSource
argument_list|(
name|groupField
argument_list|)
decl_stmt|;
name|groupingSearch
operator|=
operator|new
name|GroupingSearch
argument_list|(
name|vs
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|groupingSearch
operator|=
operator|new
name|GroupingSearch
argument_list|(
name|groupField
argument_list|)
expr_stmt|;
block|}
name|groupingSearch
operator|.
name|setGroupSort
argument_list|(
name|groupSort
argument_list|)
expr_stmt|;
name|groupingSearch
operator|.
name|setGroupDocsLimit
argument_list|(
name|docsInGroup
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|groupingSearch
operator|.
name|setCachingInMB
argument_list|(
literal|4.0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|groupingSearch
return|;
block|}
DECL|method|testSetAllGroups
specifier|public
name|void
name|testSetAllGroups
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
name|newField
argument_list|(
literal|"group"
argument_list|,
literal|"foo"
argument_list|,
name|StringField
operator|.
name|TYPE_NOT_STORED
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
literal|"group"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|GroupingSearch
name|gs
init|=
operator|new
name|GroupingSearch
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
name|gs
operator|.
name|setAllGroups
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TopGroups
argument_list|<
name|?
argument_list|>
name|groups
init|=
name|gs
operator|.
name|search
argument_list|(
name|indexSearcher
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"group"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|groups
operator|.
name|totalHitCount
argument_list|)
expr_stmt|;
comment|//assertEquals(1, groups.totalGroupCount.intValue());
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|groups
operator|.
name|totalGroupedHitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|gs
operator|.
name|getAllMatchingGroups
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

