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
name|LeafReaderContext
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
name|Weight
operator|.
name|DefaultBulkScorer
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
name|ClassicSimilarity
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
name|Bits
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
DECL|class|TestBooleanScorer
specifier|public
class|class
name|TestBooleanScorer
extends|extends
name|LuceneTestCase
block|{
DECL|field|FIELD
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"category"
decl_stmt|;
DECL|method|testMethod
specifier|public
name|void
name|testMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|}
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
name|values
operator|.
name|length
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
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
name|FIELD
argument_list|,
name|values
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|IndexReader
name|ir
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|booleanQuery1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|booleanQuery1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|booleanQuery1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|booleanQuery1
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"9"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|indexSearcher
operator|.
name|search
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Number of matched documents"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|ir
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
comment|/** Throws UOE if Weight.scorer is called */
DECL|class|CrazyMustUseBulkScorerQuery
specifier|private
specifier|static
class|class
name|CrazyMustUseBulkScorerQuery
extends|extends
name|Query
block|{
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"MustUseBulkScorerQuery"
return|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|needsScores
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Weight
argument_list|(
name|CrazyMustUseBulkScorerQuery
operator|.
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BulkScorer
name|bulkScorer
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|BulkScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|score
parameter_list|(
name|LeafCollector
name|collector
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|min
operator|==
literal|0
assert|;
name|collector
operator|.
name|setScorer
argument_list|(
operator|new
name|FakeScorer
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|collect
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Make sure BooleanScorer can embed another    *  BooleanScorer. */
DECL|method|testEmbeddedBooleanScorer
specifier|public
name|void
name|testEmbeddedBooleanScorer
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
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|"doctors are people who prescribe medicines of which they know little, to cure diseases of which they know less, in human beings of whom they know nothing"
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
name|IndexSearcher
name|s
init|=
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q1
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"little"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q1
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"diseases"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|q2
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|q2
operator|.
name|add
argument_list|(
name|q1
operator|.
name|build
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q2
operator|.
name|add
argument_list|(
operator|new
name|CrazyMustUseBulkScorerQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|search
argument_list|(
name|q2
operator|.
name|build
argument_list|()
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|r
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
DECL|method|testOptimizeTopLevelClauseOrNull
specifier|public
name|void
name|testOptimizeTopLevelClauseOrNull
parameter_list|()
throws|throws
name|IOException
block|{
comment|// When there is a single non-null scorer, this scorer should be used
comment|// directly
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
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
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// so that weights are not wrapped
specifier|final
name|LeafReaderContext
name|ctx
init|=
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
comment|// existing term
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
comment|// missing term
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// no scores -> term scorer
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|BulkScorer
name|scorer
init|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|DefaultBulkScorer
argument_list|)
expr_stmt|;
comment|// term scorer
comment|// disabled coords -> term scorer
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
comment|// existing term
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
comment|// missing term
operator|.
name|setDisableCoord
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|weight
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scorer
operator|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|DefaultBulkScorer
argument_list|)
expr_stmt|;
comment|// term scorer
comment|// enabled coords -> BoostedBulkScorer
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
comment|// existing term
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
comment|// missing term
operator|.
name|build
argument_list|()
expr_stmt|;
name|weight
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scorer
operator|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|BooleanTopLevelScorers
operator|.
name|BoostedBulkScorer
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|testOptimizeProhibitedClauses
specifier|public
name|void
name|testOptimizeProhibitedClauses
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
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
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
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
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
operator|new
name|StringField
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|,
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
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// so that weights are not wrapped
specifier|final
name|LeafReaderContext
name|ctx
init|=
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Weight
name|weight
init|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|BulkScorer
name|scorer
init|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|ReqExclBulkScorer
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|weight
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scorer
operator|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|ReqExclBulkScorer
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|weight
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scorer
operator|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|ReqExclBulkScorer
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|MUST_NOT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|weight
operator|=
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|query
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|scorer
operator|=
operator|(
operator|(
name|BooleanWeight
operator|)
name|weight
operator|)
operator|.
name|booleanScorer
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|scorer
operator|instanceof
name|ReqExclBulkScorer
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
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
DECL|method|testSparseClauseOptimization
specifier|public
name|void
name|testSparseClauseOptimization
parameter_list|()
throws|throws
name|IOException
block|{
comment|// When some windows have only one scorer that can match, the scorer will
comment|// directly call the collector in this window
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
name|emptyDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|d
init|=
literal|0
init|;
name|d
operator|<
name|numDocs
condition|;
operator|++
name|d
control|)
block|{
for|for
control|(
name|int
name|i
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5000
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|emptyDoc
argument_list|)
expr_stmt|;
block|}
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|)
control|)
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"field"
argument_list|,
name|value
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3000
argument_list|,
literal|5000
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|w
operator|.
name|addDocument
argument_list|(
name|emptyDoc
argument_list|)
expr_stmt|;
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
name|w
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
argument_list|,
literal|3
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
argument_list|,
literal|3
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|BoostQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
argument_list|,
literal|3
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
operator|.
name|setDisableCoord
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// duel BS1 vs. BS2
name|QueryUtils
operator|.
name|check
argument_list|(
name|random
argument_list|()
argument_list|,
name|query
argument_list|,
name|searcher
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

