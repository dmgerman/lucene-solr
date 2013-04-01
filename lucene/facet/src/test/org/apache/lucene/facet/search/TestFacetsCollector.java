begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|facet
operator|.
name|FacetTestCase
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
name|facet
operator|.
name|index
operator|.
name|FacetFields
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
name|facet
operator|.
name|params
operator|.
name|CategoryListParams
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
name|facet
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|facet
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|facet
operator|.
name|params
operator|.
name|PerDimensionIndexingParams
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyWriter
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|search
operator|.
name|ConstantScoreQuery
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
name|MultiCollector
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
name|TopScoreDocCollector
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
name|IOUtils
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestFacetsCollector
specifier|public
class|class
name|TestFacetsCollector
extends|extends
name|FacetTestCase
block|{
annotation|@
name|Test
DECL|method|testSumScoreAggregator
specifier|public
name|void
name|testSumScoreAggregator
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|atLeast
argument_list|(
literal|30
argument_list|)
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
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
comment|// don't match all documents
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"v"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
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
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|sParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|SumScoreFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|FacetsAccumulator
name|fa
init|=
operator|new
name|FacetsAccumulator
argument_list|(
name|sParams
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
return|return
operator|new
name|SumScoreFacetsAggregator
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fa
argument_list|)
decl_stmt|;
name|TopScoreDocCollector
name|topDocs
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|ConstantScoreQuery
name|csq
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
decl_stmt|;
name|csq
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
name|csq
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|fc
argument_list|,
name|topDocs
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|float
name|value
init|=
operator|(
name|float
operator|)
name|res
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
decl_stmt|;
name|TopDocs
name|td
init|=
name|topDocs
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|int
name|expected
init|=
call|(
name|int
call|)
argument_list|(
name|td
operator|.
name|getMaxScore
argument_list|()
operator|*
name|td
operator|.
name|totalHits
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
operator|(
name|int
operator|)
name|value
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiCountingLists
specifier|public
name|void
name|testMultiCountingLists
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|fip
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$b"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
argument_list|,
name|fip
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|atLeast
argument_list|(
literal|30
argument_list|)
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
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
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"v"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|cats
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
name|cats
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|cats
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|cats
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|sParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|fip
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|sParams
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResult
name|res
range|:
name|fc
operator|.
name|getFacetResults
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
literal|"unexpected count for "
operator|+
name|res
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|res
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountAndSumScore
specifier|public
name|void
name|testCountAndSumScore
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetIndexingParams
name|fip
init|=
operator|new
name|PerDimensionIndexingParams
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|,
operator|new
name|CategoryListParams
argument_list|(
literal|"$b"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
argument_list|,
name|fip
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|atLeast
argument_list|(
literal|30
argument_list|)
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
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
operator|new
name|StringField
argument_list|(
literal|"f"
argument_list|,
literal|"v"
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|cats
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
name|cats
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|cats
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|cats
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|sParams
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|fip
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|SumScoreFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|CategoryListParams
argument_list|,
name|FacetsAggregator
argument_list|>
name|aggregators
init|=
operator|new
name|HashMap
argument_list|<
name|CategoryListParams
argument_list|,
name|FacetsAggregator
argument_list|>
argument_list|()
decl_stmt|;
name|aggregators
operator|.
name|put
argument_list|(
name|fip
operator|.
name|getCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|FastCountingFacetsAggregator
argument_list|()
argument_list|)
expr_stmt|;
name|aggregators
operator|.
name|put
argument_list|(
name|fip
operator|.
name|getCategoryListParams
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SumScoreFacetsAggregator
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FacetsAggregator
name|aggregator
init|=
operator|new
name|PerCategoryListAggregator
argument_list|(
name|aggregators
argument_list|,
name|fip
argument_list|)
decl_stmt|;
name|FacetsAccumulator
name|fa
init|=
operator|new
name|FacetsAccumulator
argument_list|(
name|sParams
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
return|return
name|aggregator
return|;
block|}
block|}
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fa
argument_list|)
decl_stmt|;
name|TopScoreDocCollector
name|topDocs
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|fc
argument_list|,
name|topDocs
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|FacetResult
name|fresA
init|=
name|facetResults
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected count for "
operator|+
name|fresA
argument_list|,
name|r
operator|.
name|maxDoc
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|fresA
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|)
expr_stmt|;
name|FacetResult
name|fresB
init|=
name|facetResults
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|double
name|expected
init|=
name|topDocs
operator|.
name|topDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
operator|*
name|r
operator|.
name|numDocs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"unexpected value for "
operator|+
name|fresB
argument_list|,
name|expected
argument_list|,
name|fresB
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|value
argument_list|,
literal|1E
operator|-
literal|10
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountRoot
specifier|public
name|void
name|testCountRoot
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-4882: FacetsAccumulator threw NPE if a FacetRequest was defined on CP.EMPTY
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|atLeast
argument_list|(
literal|30
argument_list|)
init|;
name|i
operator|>
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
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
block|}
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
name|CategoryPath
operator|.
name|EMPTY
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|FacetsAccumulator
name|fa
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|FacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
else|:
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fa
argument_list|)
decl_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|FacetResult
name|res
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|node
range|:
name|res
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|subResults
control|)
block|{
name|assertEquals
argument_list|(
name|r
operator|.
name|numDocs
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|node
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFacetResultsTwice
specifier|public
name|void
name|testGetFacetResultsTwice
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-4893: counts were multiplied as many times as getFacetResults was called.
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a/1"
argument_list|,
literal|'/'
argument_list|)
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"b/1"
argument_list|,
literal|'/'
argument_list|)
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
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|FacetsAccumulator
name|fa
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|FacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
else|:
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
specifier|final
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fa
argument_list|)
decl_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res1
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res2
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
literal|"calling getFacetResults twice should return the exact same result"
argument_list|,
name|res1
argument_list|,
name|res2
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReset
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|TaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxonomyWriter
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a/1"
argument_list|,
literal|'/'
argument_list|)
argument_list|,
operator|new
name|CategoryPath
argument_list|(
literal|"b/1"
argument_list|,
literal|'/'
argument_list|)
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
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyReader
name|taxo
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|,
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|FacetsAccumulator
name|fa
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|new
name|FacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
else|:
operator|new
name|StandardFacetsAccumulator
argument_list|(
name|fsp
argument_list|,
name|r
argument_list|,
name|taxo
argument_list|)
decl_stmt|;
specifier|final
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fa
argument_list|)
decl_stmt|;
comment|// this should populate the cached results, but doing search should clear the cache
name|fc
operator|.
name|getFacetResults
argument_list|()
expr_stmt|;
operator|new
name|IndexSearcher
argument_list|(
name|r
argument_list|)
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res1
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
comment|// verify that we didn't get the cached result
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|res1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetResult
name|res
range|:
name|res1
control|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|res
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|subResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|res
operator|.
name|getFacetResultNode
argument_list|()
operator|.
name|subResults
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|fc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res2
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
literal|"reset() should clear the cached results"
argument_list|,
name|res1
argument_list|,
name|res2
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxo
argument_list|,
name|taxoDir
argument_list|,
name|r
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

