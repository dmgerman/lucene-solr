begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Random
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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|index
operator|.
name|AtomicReaderContext
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
name|IndexWriterConfig
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
name|SerialMergeScheduler
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
name|LeafCollector
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
name|MatchAllDocsQuery
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
name|ScoreDoc
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
name|TopFieldCollector
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

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
import|;
end_import

begin_class
DECL|class|TestEarlyTermination
specifier|public
class|class
name|TestEarlyTermination
extends|extends
name|LuceneTestCase
block|{
DECL|field|numDocs
specifier|private
name|int
name|numDocs
decl_stmt|;
DECL|field|terms
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|terms
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|field|iw
specifier|private
name|RandomIndexWriter
name|iw
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
annotation|@
name|Override
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
name|sort
operator|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv1"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|randomDocument
specifier|private
name|Document
name|randomDocument
parameter_list|()
block|{
specifier|final
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
name|NumericDocValuesField
argument_list|(
literal|"ndv1"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"ndv2"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
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
literal|"s"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|createRandomIndex
specifier|private
name|void
name|createRandomIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|numDocs
operator|=
name|atLeast
argument_list|(
literal|150
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numTerms
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|/
literal|5
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|randomTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|randomTerms
operator|.
name|size
argument_list|()
operator|<
name|numTerms
condition|)
block|{
name|randomTerms
operator|.
name|add
argument_list|(
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|terms
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|randomTerms
argument_list|)
expr_stmt|;
specifier|final
name|long
name|seed
init|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergeScheduler
argument_list|(
operator|new
name|SerialMergeScheduler
argument_list|()
argument_list|)
expr_stmt|;
comment|// for reproducible tests
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|TestSortingMergePolicy
operator|.
name|newSortingMergePolicy
argument_list|(
name|sort
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
expr_stmt|;
name|iw
operator|.
name|setDoRandomForceMerge
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't do this, it may happen anyway with MockRandomMP
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Document
name|doc
init|=
name|randomDocument
argument_list|()
decl_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|numDocs
operator|/
literal|2
operator|||
operator|(
name|i
operator|!=
name|numDocs
operator|-
literal|1
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
operator|==
literal|0
operator|)
condition|)
block|{
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|15
argument_list|)
operator|==
literal|0
condition|)
block|{
specifier|final
name|String
name|term
init|=
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|iw
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|iw
operator|.
name|forceMerge
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
DECL|method|closeIndex
specifier|private
name|void
name|closeIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
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
DECL|method|testEarlyTermination
specifier|public
name|void
name|testEarlyTermination
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|8
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
name|createRandomIndex
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|iters
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numHits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv1"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|fillFields
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trackDocScores
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trackMaxScore
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|inOrder
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldCollector
name|collector1
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|fillFields
argument_list|,
name|trackDocScores
argument_list|,
name|trackMaxScore
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
specifier|final
name|TopFieldCollector
name|collector2
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|fillFields
argument_list|,
name|trackDocScores
argument_list|,
name|trackMaxScore
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|query
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
name|query
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector1
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|EarlyTerminatingSortingCollector
argument_list|(
name|collector2
argument_list|,
name|sort
argument_list|,
name|numHits
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|collector1
operator|.
name|getTotalHits
argument_list|()
operator|>=
name|collector2
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
name|assertTopDocsEquals
argument_list|(
name|collector1
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|,
name|collector2
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
name|closeIndex
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testEarlyTerminationDifferentSorter
specifier|public
name|void
name|testEarlyTerminationDifferentSorter
parameter_list|()
throws|throws
name|IOException
block|{
name|createRandomIndex
argument_list|()
expr_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|3
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
name|iters
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// test that the collector works correctly when the index was sorted by a
comment|// different sorter than the one specified in the ctor.
specifier|final
name|int
name|numHits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv2"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|fillFields
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trackDocScores
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|trackMaxScore
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|inOrder
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldCollector
name|collector1
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|fillFields
argument_list|,
name|trackDocScores
argument_list|,
name|trackMaxScore
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
specifier|final
name|TopFieldCollector
name|collector2
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|numHits
argument_list|,
name|fillFields
argument_list|,
name|trackDocScores
argument_list|,
name|trackMaxScore
argument_list|,
name|inOrder
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|query
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
name|query
operator|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"s"
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|()
argument_list|,
name|terms
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
block|}
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector1
argument_list|)
expr_stmt|;
name|Sort
name|different
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"ndv2"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
operator|new
name|EarlyTerminatingSortingCollector
argument_list|(
name|collector2
argument_list|,
name|different
argument_list|,
name|numHits
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|LeafCollector
name|ret
init|=
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"segment should not be recognized as sorted as different sorter was used"
argument_list|,
name|ret
operator|.
name|getClass
argument_list|()
operator|==
name|in
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|collector1
operator|.
name|getTotalHits
argument_list|()
operator|>=
name|collector2
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
name|assertTopDocsEquals
argument_list|(
name|collector1
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|,
name|collector2
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
argument_list|)
expr_stmt|;
block|}
name|closeIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|assertTopDocsEquals
specifier|private
specifier|static
name|void
name|assertTopDocsEquals
parameter_list|(
name|ScoreDoc
index|[]
name|scoreDocs1
parameter_list|,
name|ScoreDoc
index|[]
name|scoreDocs2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|scoreDocs1
operator|.
name|length
argument_list|,
name|scoreDocs2
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scoreDocs1
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|ScoreDoc
name|scoreDoc1
init|=
name|scoreDocs1
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|ScoreDoc
name|scoreDoc2
init|=
name|scoreDocs2
index|[
name|i
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|scoreDoc1
operator|.
name|doc
argument_list|,
name|scoreDoc2
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|scoreDoc1
operator|.
name|score
argument_list|,
name|scoreDoc2
operator|.
name|score
argument_list|,
literal|0.001f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

