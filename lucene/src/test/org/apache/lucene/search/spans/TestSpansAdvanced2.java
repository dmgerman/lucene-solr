begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|analysis
operator|.
name|MockTokenFilter
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
name|MockTokenizer
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
name|index
operator|.
name|IndexWriterConfig
operator|.
name|OpenMode
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
name|*
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
name|DefaultSimilarityProvider
import|;
end_import

begin_comment
comment|/*******************************************************************************  * Some expanded tests to make sure my patch doesn't break other SpanTermQuery  * functionality.  *   */
end_comment

begin_class
DECL|class|TestSpansAdvanced2
specifier|public
class|class
name|TestSpansAdvanced2
extends|extends
name|TestSpansAdvanced
block|{
DECL|field|searcher2
name|IndexSearcher
name|searcher2
decl_stmt|;
DECL|field|reader2
name|IndexReader
name|reader2
decl_stmt|;
comment|/**    * Initializes the tests by adding documents to the index.    */
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
comment|// create test index
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|mDirectory
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|OpenMode
operator|.
name|APPEND
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|DefaultSimilarityProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"A"
argument_list|,
literal|"Should we, could we, would we?"
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"B"
argument_list|,
literal|"It should.  Should it?"
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"C"
argument_list|,
literal|"It shouldn't."
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|writer
argument_list|,
literal|"D"
argument_list|,
literal|"Should we, should we, should we."
argument_list|)
expr_stmt|;
name|reader2
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
comment|// re-open the searcher since we added more docs
name|searcher2
operator|=
name|newSearcher
argument_list|(
name|reader2
argument_list|)
expr_stmt|;
name|searcher2
operator|.
name|setSimilarityProvider
argument_list|(
operator|new
name|DefaultSimilarityProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher2
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader2
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
comment|/**    * Verifies that the index has the correct number of documents.    *     * @throws Exception    */
DECL|method|testVerifyIndex
specifier|public
name|void
name|testVerifyIndex
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|mDirectory
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests a single span query that matches multiple documents.    *     * @throws IOException    */
DECL|method|testSingleSpanQuery
specifier|public
name|void
name|testSingleSpanQuery
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|spanQuery
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_TEXT
argument_list|,
literal|"should"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|expectedIds
init|=
operator|new
name|String
index|[]
block|{
literal|"B"
block|,
literal|"D"
block|,
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"A"
block|}
decl_stmt|;
specifier|final
name|float
index|[]
name|expectedScores
init|=
operator|new
name|float
index|[]
block|{
literal|0.625f
block|,
literal|0.45927936f
block|,
literal|0.35355338f
block|,
literal|0.35355338f
block|,
literal|0.35355338f
block|,
literal|0.35355338f
block|,
literal|0.26516503f
block|,}
decl_stmt|;
name|assertHits
argument_list|(
name|searcher2
argument_list|,
name|spanQuery
argument_list|,
literal|"single span query"
argument_list|,
name|expectedIds
argument_list|,
name|expectedScores
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests a single span query that matches multiple documents.    *     * @throws IOException    */
DECL|method|testMultipleDifferentSpanQueries
specifier|public
name|void
name|testMultipleDifferentSpanQueries
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Query
name|spanQuery1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_TEXT
argument_list|,
literal|"should"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|spanQuery2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD_TEXT
argument_list|,
literal|"we"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|spanQuery1
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
name|spanQuery2
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|expectedIds
init|=
operator|new
name|String
index|[]
block|{
literal|"D"
block|,
literal|"A"
block|}
decl_stmt|;
comment|// these values were pre LUCENE-413
comment|// final float[] expectedScores = new float[] { 0.93163157f, 0.20698164f };
specifier|final
name|float
index|[]
name|expectedScores
init|=
operator|new
name|float
index|[]
block|{
literal|1.0191123f
block|,
literal|0.93163157f
block|}
decl_stmt|;
name|assertHits
argument_list|(
name|searcher2
argument_list|,
name|query
argument_list|,
literal|"multiple different span queries"
argument_list|,
name|expectedIds
argument_list|,
name|expectedScores
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests two span queries.    *     * @throws IOException    */
annotation|@
name|Override
DECL|method|testBooleanQueryWithSpanQueries
specifier|public
name|void
name|testBooleanQueryWithSpanQueries
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestBooleanQueryWithSpanQueries
argument_list|(
name|searcher2
argument_list|,
literal|0.73500174f
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

