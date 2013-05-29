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
name|Comparator
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
name|FacetTestUtils
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
name|search
operator|.
name|DrillSideways
operator|.
name|DrillSidewaysResult
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
name|TaxonomyReader
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
name|CollectionUtil
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

begin_class
DECL|class|FacetResultTest
specifier|public
class|class
name|FacetResultTest
extends|extends
name|FacetTestCase
block|{
DECL|method|newDocument
specifier|private
name|Document
name|newDocument
parameter_list|(
name|FacetFields
name|facetFields
parameter_list|,
name|String
modifier|...
name|categories
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
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
for|for
control|(
name|String
name|cat
range|:
name|categories
control|)
block|{
name|cats
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
name|cat
argument_list|,
literal|'/'
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
name|cats
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
DECL|method|initIndex
specifier|private
name|void
name|initIndex
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriterConfig
name|conf
init|=
operator|new
name|IndexWriterConfig
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
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|taxoWriter
argument_list|)
decl_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2010/March/12"
argument_list|,
literal|"A/1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2010/March/23"
argument_list|,
literal|"A/2"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2010/April/17"
argument_list|,
literal|"A/3"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2010/May/18"
argument_list|,
literal|"A/1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2011/January/1"
argument_list|,
literal|"A/3"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2011/February/12"
argument_list|,
literal|"A/1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2011/February/18"
argument_list|,
literal|"A/4"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2012/August/15"
argument_list|,
literal|"A/1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2012/July/5"
argument_list|,
literal|"A/2"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2013/September/13"
argument_list|,
literal|"A/1"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|newDocument
argument_list|(
name|facetFields
argument_list|,
literal|"Date/2013/September/25"
argument_list|,
literal|"A/4"
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|)
expr_stmt|;
block|}
DECL|method|searchIndex
specifier|private
name|void
name|searchIndex
parameter_list|(
name|TaxonomyReader
name|taxoReader
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|fillMissingCounts
parameter_list|,
name|String
index|[]
name|exp
parameter_list|,
name|String
index|[]
index|[]
name|drillDowns
parameter_list|,
name|int
index|[]
name|numResults
parameter_list|)
throws|throws
name|IOException
block|{
name|CategoryPath
index|[]
index|[]
name|cps
init|=
operator|new
name|CategoryPath
index|[
name|drillDowns
operator|.
name|length
index|]
index|[]
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
name|cps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cps
index|[
name|i
index|]
operator|=
operator|new
name|CategoryPath
index|[
name|drillDowns
index|[
name|i
index|]
operator|.
name|length
index|]
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
name|cps
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|cps
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|CategoryPath
argument_list|(
name|drillDowns
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
block|}
name|DrillDownQuery
name|ddq
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|FacetIndexingParams
operator|.
name|DEFAULT
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CategoryPath
index|[]
name|cats
range|:
name|cps
control|)
block|{
name|ddq
operator|.
name|add
argument_list|(
name|cats
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryPath
index|[]
name|cats
range|:
name|cps
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|CategoryPath
name|cp
init|=
name|cats
index|[
name|i
index|]
decl_stmt|;
name|int
name|numres
init|=
name|numResults
operator|==
literal|null
condition|?
literal|2
else|:
name|numResults
index|[
name|i
index|]
decl_stmt|;
comment|// for each drill-down, add itself as well as its parent as requests, so
comment|// we get the drill-sideways
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
name|cp
argument_list|,
name|numres
argument_list|)
argument_list|)
expr_stmt|;
name|CountFacetRequest
name|parent
init|=
operator|new
name|CountFacetRequest
argument_list|(
name|cp
operator|.
name|subpath
argument_list|(
name|cp
operator|.
name|length
operator|-
literal|1
argument_list|)
argument_list|,
name|numres
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|facetRequests
operator|.
name|contains
argument_list|(
name|parent
argument_list|)
operator|&&
name|parent
operator|.
name|categoryPath
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|facetRequests
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|facetRequests
argument_list|)
decl_stmt|;
specifier|final
name|DrillSideways
name|ds
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FacetArrays
argument_list|>
name|dimArrays
decl_stmt|;
if|if
condition|(
name|fillMissingCounts
condition|)
block|{
name|dimArrays
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FacetArrays
argument_list|>
argument_list|()
expr_stmt|;
name|ds
operator|=
operator|new
name|DrillSideways
argument_list|(
name|searcher
argument_list|,
name|taxoReader
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|FacetsAccumulator
name|getDrillSidewaysAccumulator
parameter_list|(
name|String
name|dim
parameter_list|,
name|FacetSearchParams
name|fsp
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetsAccumulator
name|fa
init|=
name|super
operator|.
name|getDrillSidewaysAccumulator
argument_list|(
name|dim
argument_list|,
name|fsp
argument_list|)
decl_stmt|;
name|dimArrays
operator|.
name|put
argument_list|(
name|dim
argument_list|,
name|fa
operator|.
name|facetArrays
argument_list|)
expr_stmt|;
return|return
name|fa
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|ds
operator|=
operator|new
name|DrillSideways
argument_list|(
name|searcher
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
name|dimArrays
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|DrillSidewaysResult
name|sidewaysRes
init|=
name|ds
operator|.
name|search
argument_list|(
literal|null
argument_list|,
name|ddq
argument_list|,
literal|5
argument_list|,
name|fsp
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
name|FacetResult
operator|.
name|mergeHierarchies
argument_list|(
name|sidewaysRes
operator|.
name|facetResults
argument_list|,
name|taxoReader
argument_list|,
name|dimArrays
argument_list|)
decl_stmt|;
name|CollectionUtil
operator|.
name|introSort
argument_list|(
name|facetResults
argument_list|,
operator|new
name|Comparator
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|FacetResult
name|o1
parameter_list|,
name|FacetResult
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|categoryPath
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getFacetRequest
argument_list|()
operator|.
name|categoryPath
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exp
operator|.
name|length
argument_list|,
name|facetResults
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// A + single one for date
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|facetResults
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|exp
index|[
name|i
index|]
argument_list|,
name|FacetTestUtils
operator|.
name|toSimpleString
argument_list|(
name|facetResults
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMergeHierarchies
specifier|public
name|void
name|testMergeHierarchies
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|indexDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|,
name|taxoDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|initIndex
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
name|DirectoryReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|String
index|[]
name|exp
init|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n  2011 (3)\n"
block|}
decl_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// two dimensions
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"A (0)\n  1 (5)\n  4 (2)\n"
block|,
literal|"Date (0)\n  2010 (4)\n  2011 (3)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"A"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// both parent and child are OR'd
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (-1)\n  2010 (4)\n    March (2)\n      23 (1)\n      12 (1)\n    May (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010/March"
block|,
literal|"Date/2010/March/23"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// both parent and child are OR'd (fill counts)
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n      23 (1)\n      12 (1)\n    May (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|true
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010/March"
block|,
literal|"Date/2010/March/23"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// same DD twice
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n    May (1)\n  2011 (3)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010"
block|,
literal|"Date/2010"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n    May (1)\n  2011 (3)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n    May (1)\n  2011 (3)\n    February (2)\n    January (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010"
block|,
literal|"Date/2011"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n      23 (1)\n      12 (1)\n    May (1)\n  2011 (3)\n    February (2)\n    January (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010/March"
block|,
literal|"Date/2011"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Date/2010/April not in top-2 of Date/2010
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n      23 (1)\n      12 (1)\n    May (1)\n    April (1)\n      17 (1)\n  2011 (3)\n    February (2)\n    January (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2010/March"
block|,
literal|"Date/2010/April"
block|,
literal|"Date/2011"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// missing ancestors
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (-1)\n  2010 (4)\n    March (2)\n    May (1)\n    April (1)\n      17 (1)\n  2011 (-1)\n    January (1)\n      1 (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|false
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2011/January/1"
block|,
literal|"Date/2010/April"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// missing ancestors (fill counts)
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"Date (0)\n  2010 (4)\n    March (2)\n    May (1)\n    April (1)\n      17 (1)\n  2011 (3)\n    January (1)\n      1 (1)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
literal|true
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"Date/2011/January/1"
block|,
literal|"Date/2010/April"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// non-hierarchical dimension with both parent and child
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"A (0)\n  1 (5)\n  4 (2)\n  3 (2)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
name|INFOSTREAM
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"A/3"
block|}
block|}
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// non-hierarchical dimension with same request but different numResults
name|exp
operator|=
operator|new
name|String
index|[]
block|{
literal|"A (0)\n  1 (5)\n  4 (2)\n  3 (2)\n  2 (2)\n"
block|}
expr_stmt|;
name|searchIndex
argument_list|(
name|taxoReader
argument_list|,
name|searcher
argument_list|,
name|INFOSTREAM
argument_list|,
name|exp
argument_list|,
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"A"
block|}
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|4
block|}
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

