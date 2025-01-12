begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
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
name|IndexOptions
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
name|BooleanQuery
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
name|Explanation
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
name|search
operator|.
name|spans
operator|.
name|SpanOrQuery
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
name|spans
operator|.
name|SpanTermQuery
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

begin_comment
comment|/**  * Tests against all the similarities we have  */
end_comment

begin_class
DECL|class|TestSimilarity2
specifier|public
class|class
name|TestSimilarity2
extends|extends
name|LuceneTestCase
block|{
DECL|field|sims
name|List
argument_list|<
name|Similarity
argument_list|>
name|sims
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
name|sims
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|sims
operator|.
name|add
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|sims
operator|.
name|add
argument_list|(
operator|new
name|BM25Similarity
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO: not great that we dup this all with TestSimilarityBase
for|for
control|(
name|BasicModel
name|basicModel
range|:
name|TestSimilarityBase
operator|.
name|BASIC_MODELS
control|)
block|{
for|for
control|(
name|AfterEffect
name|afterEffect
range|:
name|TestSimilarityBase
operator|.
name|AFTER_EFFECTS
control|)
block|{
for|for
control|(
name|Normalization
name|normalization
range|:
name|TestSimilarityBase
operator|.
name|NORMALIZATIONS
control|)
block|{
name|sims
operator|.
name|add
argument_list|(
operator|new
name|DFRSimilarity
argument_list|(
name|basicModel
argument_list|,
name|afterEffect
argument_list|,
name|normalization
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Distribution
name|distribution
range|:
name|TestSimilarityBase
operator|.
name|DISTRIBUTIONS
control|)
block|{
for|for
control|(
name|Lambda
name|lambda
range|:
name|TestSimilarityBase
operator|.
name|LAMBDAS
control|)
block|{
for|for
control|(
name|Normalization
name|normalization
range|:
name|TestSimilarityBase
operator|.
name|NORMALIZATIONS
control|)
block|{
name|sims
operator|.
name|add
argument_list|(
operator|new
name|IBSimilarity
argument_list|(
name|distribution
argument_list|,
name|lambda
argument_list|,
name|normalization
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|sims
operator|.
name|add
argument_list|(
operator|new
name|LMDirichletSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|sims
operator|.
name|add
argument_list|(
operator|new
name|LMJelinekMercerSimilarity
argument_list|(
literal|0.1f
argument_list|)
argument_list|)
expr_stmt|;
name|sims
operator|.
name|add
argument_list|(
operator|new
name|LMJelinekMercerSimilarity
argument_list|(
literal|0.7f
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Independence
name|independence
range|:
name|TestSimilarityBase
operator|.
name|INDEPENDENCE_MEASURES
control|)
block|{
name|sims
operator|.
name|add
argument_list|(
operator|new
name|DFISimilarity
argument_list|(
name|independence
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** because of stupid things like querynorm, it's possible we computeStats on a field that doesnt exist at all    *  test this against a totally empty index, to make sure sims handle it    */
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
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
name|iw
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
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|is
operator|.
name|search
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
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
name|ir
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
comment|/** similar to the above, but ORs the query with a real field */
DECL|method|testEmptyField
specifier|public
name|void
name|testEmptyField
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
name|iw
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
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
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
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
literal|"bar"
argument_list|,
literal|"baz"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|is
operator|.
name|search
argument_list|(
name|query
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
block|}
name|ir
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
comment|/** similar to the above, however the field exists, but we query with a term that doesnt exist too */
DECL|method|testEmptyTerm
specifier|public
name|void
name|testEmptyTerm
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
name|iw
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
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
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
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
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
literal|"foo"
argument_list|,
literal|"baz"
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
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|is
operator|.
name|search
argument_list|(
name|query
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
block|}
name|ir
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
comment|/** make sure we can retrieve when norms are disabled */
DECL|method|testNoNorms
specifier|public
name|void
name|testNoNorms
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
name|iw
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
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
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
name|is
operator|.
name|search
argument_list|(
name|query
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
block|}
name|ir
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
comment|/** make sure scores are not skewed by docs not containing the field */
DECL|method|testNoFieldSkew
specifier|public
name|void
name|testNoFieldSkew
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
comment|// an evil merge policy could reorder our docs for no reason
name|IndexWriterConfig
name|iwConfig
init|=
name|newIndexWriterConfig
argument_list|()
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwConfig
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
literal|"foo"
argument_list|,
literal|"bar baz somethingelse"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|queryBuilder
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|queryBuilder
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
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|queryBuilder
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
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|queryBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// collect scores
name|List
argument_list|<
name|Explanation
argument_list|>
name|scores
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
name|scores
operator|.
name|add
argument_list|(
name|is
operator|.
name|explain
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// add some additional docs without the field
name|int
name|numExtraDocs
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
literal|1000
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
name|numExtraDocs
condition|;
name|i
operator|++
control|)
block|{
name|iw
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check scores are the same
name|ir
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|is
operator|=
name|newSearcher
argument_list|(
name|ir
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
name|sims
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sims
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|Explanation
name|expected
init|=
name|scores
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Explanation
name|actual
init|=
name|is
operator|.
name|explain
argument_list|(
name|query
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sims
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|": actual="
operator|+
name|actual
operator|+
literal|",expected="
operator|+
name|expected
argument_list|,
name|expected
operator|.
name|getValue
argument_list|()
argument_list|,
name|actual
operator|.
name|getValue
argument_list|()
argument_list|,
literal|0F
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ir
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
comment|/** make sure all sims work if TF is omitted */
DECL|method|testOmitTF
specifier|public
name|void
name|testOmitTF
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
name|iw
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
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|ft
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
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
name|is
operator|.
name|search
argument_list|(
name|query
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
block|}
name|ir
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
comment|/** make sure all sims work if TF and norms is omitted */
DECL|method|testOmitTFAndNorms
specifier|public
name|void
name|testOmitTFAndNorms
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
name|iw
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
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|Field
name|f
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|ft
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
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
name|is
operator|.
name|search
argument_list|(
name|query
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
block|}
name|ir
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
comment|/** make sure all sims work with spanOR(termX, termY) where termY does not exist */
DECL|method|testCrazySpans
specifier|public
name|void
name|testCrazySpans
parameter_list|()
throws|throws
name|Exception
block|{
comment|// The problem: "normal" lucene queries create scorers, returning null if terms dont exist
comment|// This means they never score a term that does not exist.
comment|// however with spans, there is only one scorer for the whole hierarchy:
comment|// inner queries are not real queries, their boosts are ignored, etc.
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|iw
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
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|is
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
for|for
control|(
name|Similarity
name|sim
range|:
name|sims
control|)
block|{
name|is
operator|.
name|setSimilarity
argument_list|(
name|sim
argument_list|)
expr_stmt|;
name|SpanTermQuery
name|s1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|s2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
decl_stmt|;
name|TopDocs
name|td
init|=
name|is
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|float
name|score
init|=
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
decl_stmt|;
name|assertFalse
argument_list|(
literal|"negative score for "
operator|+
name|sim
argument_list|,
name|score
operator|<
literal|0.0f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"inf score for "
operator|+
name|sim
argument_list|,
name|Float
operator|.
name|isInfinite
argument_list|(
name|score
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"nan score for "
operator|+
name|sim
argument_list|,
name|Float
operator|.
name|isNaN
argument_list|(
name|score
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
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

