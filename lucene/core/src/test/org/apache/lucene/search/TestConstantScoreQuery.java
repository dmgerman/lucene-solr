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
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
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
name|DefaultSimilarity
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

begin_comment
comment|/** This class only tests some basic functionality in CSQ, the main parts are mostly  * tested by MultiTermQuery tests, explanations seems to be tested in TestExplanations! */
end_comment

begin_class
DECL|class|TestConstantScoreQuery
specifier|public
class|class
name|TestConstantScoreQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCSQ
specifier|public
name|void
name|testCSQ
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Query
name|q1
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q2
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q3
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|TermRangeFilter
operator|.
name|newStringRange
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|q1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|q2
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q1
argument_list|,
name|q1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q2
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkEqual
argument_list|(
name|q3
argument_list|,
name|q3
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q2
argument_list|,
name|q3
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
name|q3
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|q1
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|q
parameter_list|,
specifier|final
name|float
name|expectedScore
parameter_list|,
specifier|final
name|String
name|scorerClassName
parameter_list|,
specifier|final
name|String
name|innerScorerClassName
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
index|[]
name|count
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
operator|new
name|SimpleCollector
argument_list|()
block|{
specifier|private
name|Scorer
name|scorer
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Scorer is implemented by wrong class"
argument_list|,
name|scorerClassName
argument_list|,
name|scorer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerScorerClassName
operator|!=
literal|null
operator|&&
name|scorer
operator|instanceof
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
condition|)
block|{
specifier|final
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
name|innerScorer
init|=
operator|(
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
operator|)
name|scorer
decl_stmt|;
name|assertEquals
argument_list|(
literal|"inner Scorer is implemented by wrong class"
argument_list|,
name|innerScorerClassName
argument_list|,
name|innerScorer
operator|.
name|in
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
literal|"Score differs from expected"
argument_list|,
name|expectedScore
argument_list|,
name|this
operator|.
name|scorer
operator|.
name|score
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|count
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid number of results"
argument_list|,
literal|1
argument_list|,
name|count
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|testWrapped2Times
specifier|public
name|void
name|testWrapped2Times
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"term"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// we don't wrap with AssertingIndexSearcher in order to have the original scorer in setScorer.
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// set a similarity that does not normalize our boost away
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
return|return
literal|1.0f
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|csq1
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"term"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|csq1
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|csq2
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|csq1
argument_list|)
decl_stmt|;
name|csq2
operator|.
name|setBoost
argument_list|(
literal|5.0f
argument_list|)
expr_stmt|;
specifier|final
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|csq1
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|csq2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|csqbq
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|bq
argument_list|)
decl_stmt|;
name|csqbq
operator|.
name|setBoost
argument_list|(
literal|17.0f
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|searcher
argument_list|,
name|csq1
argument_list|,
name|csq1
operator|.
name|getBoost
argument_list|()
argument_list|,
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|searcher
argument_list|,
name|csq2
argument_list|,
name|csq2
operator|.
name|getBoost
argument_list|()
argument_list|,
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// for the combined BQ, the scorer should always be BooleanScorer's BucketScorer, because our scorer supports out-of order collection!
specifier|final
name|String
name|bucketScorerClass
init|=
name|FakeScorer
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|checkHits
argument_list|(
name|searcher
argument_list|,
name|bq
argument_list|,
name|csq1
operator|.
name|getBoost
argument_list|()
operator|+
name|csq2
operator|.
name|getBoost
argument_list|()
argument_list|,
name|bucketScorerClass
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|searcher
argument_list|,
name|csqbq
argument_list|,
name|csqbq
operator|.
name|getBoost
argument_list|()
argument_list|,
name|ConstantScoreQuery
operator|.
name|ConstantScoreScorer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|bucketScorerClass
argument_list|)
expr_stmt|;
block|}
finally|finally
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
if|if
condition|(
name|directory
operator|!=
literal|null
condition|)
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testConstantScoreQueryAndFilter
specifier|public
name|void
name|testConstantScoreQueryAndFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|d
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
name|d
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"a"
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"b"
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
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|Filter
name|filterB
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterB
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filterB
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Query for field:b, Filter field:b
name|Filter
name|filterA
init|=
operator|new
name|CachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|query
operator|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filterA
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filterB
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|// Query field:b, Filter field:a
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-5307
comment|// don't reuse the scorer of filters since they have been created with bulkScorer=false
DECL|method|testQueryWrapperFilter
specifier|public
name|void
name|testQueryWrapperFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|d
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
name|d
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
name|newStringField
argument_list|(
literal|"field"
argument_list|,
literal|"a"
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
name|IndexReader
name|r
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|Filter
name|filter
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|AssertingQuery
operator|.
name|wrap
argument_list|(
name|random
argument_list|()
argument_list|,
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|s
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
assert|assert
name|s
operator|instanceof
name|AssertingIndexSearcher
assert|;
comment|// this used to fail
name|s
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
argument_list|,
operator|new
name|TotalHitCountCollector
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the rewrite
name|Query
name|rewritten
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|filter
argument_list|)
operator|.
name|rewrite
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rewritten
operator|instanceof
name|ConstantScoreQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|rewritten
operator|)
operator|.
name|getQuery
argument_list|()
operator|instanceof
name|AssertingQuery
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPropagatesApproximations
specifier|public
name|void
name|testPropagatesApproximations
parameter_list|()
throws|throws
name|IOException
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
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"a b"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
specifier|final
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|PhraseQuery
name|pq
init|=
operator|new
name|PhraseQuery
argument_list|()
decl_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|pq
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|ConstantScoreQuery
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
name|pq
argument_list|)
decl_stmt|;
specifier|final
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|q
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Scorer
name|scorer
init|=
name|weight
operator|.
name|scorer
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|scorer
operator|.
name|asTwoPhaseIterator
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|w
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

