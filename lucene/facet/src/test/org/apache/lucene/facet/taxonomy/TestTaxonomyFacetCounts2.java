begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|FacetField
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
name|FacetResult
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
name|Facets
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
name|FacetsCollector
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
name|FacetsConfig
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
name|LabelAndValue
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
name|index
operator|.
name|NoMergePolicy
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
DECL|class|TestTaxonomyFacetCounts2
specifier|public
class|class
name|TestTaxonomyFacetCounts2
extends|extends
name|FacetTestCase
block|{
DECL|field|A
specifier|private
specifier|static
specifier|final
name|Term
name|A
init|=
operator|new
name|Term
argument_list|(
literal|"f"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
DECL|field|CP_A
DECL|field|CP_B
specifier|private
specifier|static
specifier|final
name|String
name|CP_A
init|=
literal|"A"
decl_stmt|,
name|CP_B
init|=
literal|"B"
decl_stmt|;
DECL|field|CP_C
DECL|field|CP_D
specifier|private
specifier|static
specifier|final
name|String
name|CP_C
init|=
literal|"C"
decl_stmt|,
name|CP_D
init|=
literal|"D"
decl_stmt|;
comment|// indexed w/ NO_PARENTS
DECL|field|NUM_CHILDREN_CP_A
DECL|field|NUM_CHILDREN_CP_B
specifier|private
specifier|static
specifier|final
name|int
name|NUM_CHILDREN_CP_A
init|=
literal|5
decl_stmt|,
name|NUM_CHILDREN_CP_B
init|=
literal|3
decl_stmt|;
DECL|field|NUM_CHILDREN_CP_C
DECL|field|NUM_CHILDREN_CP_D
specifier|private
specifier|static
specifier|final
name|int
name|NUM_CHILDREN_CP_C
init|=
literal|5
decl_stmt|,
name|NUM_CHILDREN_CP_D
init|=
literal|5
decl_stmt|;
DECL|field|CATEGORIES_A
DECL|field|CATEGORIES_B
specifier|private
specifier|static
specifier|final
name|FacetField
index|[]
name|CATEGORIES_A
decl_stmt|,
name|CATEGORIES_B
decl_stmt|;
DECL|field|CATEGORIES_C
DECL|field|CATEGORIES_D
specifier|private
specifier|static
specifier|final
name|FacetField
index|[]
name|CATEGORIES_C
decl_stmt|,
name|CATEGORIES_D
decl_stmt|;
static|static
block|{
name|CATEGORIES_A
operator|=
operator|new
name|FacetField
index|[
name|NUM_CHILDREN_CP_A
index|]
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
name|NUM_CHILDREN_CP_A
condition|;
name|i
operator|++
control|)
block|{
name|CATEGORIES_A
index|[
name|i
index|]
operator|=
operator|new
name|FacetField
argument_list|(
name|CP_A
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CATEGORIES_B
operator|=
operator|new
name|FacetField
index|[
name|NUM_CHILDREN_CP_B
index|]
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
name|NUM_CHILDREN_CP_B
condition|;
name|i
operator|++
control|)
block|{
name|CATEGORIES_B
index|[
name|i
index|]
operator|=
operator|new
name|FacetField
argument_list|(
name|CP_B
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// NO_PARENTS categories
name|CATEGORIES_C
operator|=
operator|new
name|FacetField
index|[
name|NUM_CHILDREN_CP_C
index|]
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
name|NUM_CHILDREN_CP_C
condition|;
name|i
operator|++
control|)
block|{
name|CATEGORIES_C
index|[
name|i
index|]
operator|=
operator|new
name|FacetField
argument_list|(
name|CP_C
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Multi-level categories
name|CATEGORIES_D
operator|=
operator|new
name|FacetField
index|[
name|NUM_CHILDREN_CP_D
index|]
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
name|NUM_CHILDREN_CP_D
condition|;
name|i
operator|++
control|)
block|{
name|String
name|val
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|CATEGORIES_D
index|[
name|i
index|]
operator|=
operator|new
name|FacetField
argument_list|(
name|CP_D
argument_list|,
name|val
argument_list|,
name|val
operator|+
name|val
argument_list|)
expr_stmt|;
comment|// e.g. D/1/11, D/2/22...
block|}
block|}
DECL|field|indexDir
DECL|field|taxoDir
specifier|private
specifier|static
name|Directory
name|indexDir
decl_stmt|,
name|taxoDir
decl_stmt|;
DECL|field|allExpectedCounts
DECL|field|termExpectedCounts
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|allExpectedCounts
decl_stmt|,
name|termExpectedCounts
decl_stmt|;
annotation|@
name|AfterClass
DECL|method|afterClassCountingFacetsAggregatorTest
specifier|public
specifier|static
name|void
name|afterClassCountingFacetsAggregatorTest
parameter_list|()
throws|throws
name|Exception
block|{
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
DECL|method|randomCategories
specifier|private
specifier|static
name|List
argument_list|<
name|FacetField
argument_list|>
name|randomCategories
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
comment|// add random categories from the two dimensions, ensuring that the same
comment|// category is not added twice.
name|int
name|numFacetsA
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// 1-3
name|int
name|numFacetsB
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// 1-2
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
name|categories_a
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|categories_a
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|CATEGORIES_A
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
name|categories_b
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|categories_b
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|CATEGORIES_B
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|categories_a
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|categories_b
argument_list|,
name|random
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|FacetField
argument_list|>
name|categories
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|categories
operator|.
name|addAll
argument_list|(
name|categories_a
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|numFacetsA
argument_list|)
argument_list|)
expr_stmt|;
name|categories
operator|.
name|addAll
argument_list|(
name|categories_b
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|numFacetsB
argument_list|)
argument_list|)
expr_stmt|;
comment|// add the NO_PARENT categories
name|categories
operator|.
name|add
argument_list|(
name|CATEGORIES_C
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUM_CHILDREN_CP_C
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|categories
operator|.
name|add
argument_list|(
name|CATEGORIES_D
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUM_CHILDREN_CP_D
argument_list|)
index|]
argument_list|)
expr_stmt|;
return|return
name|categories
return|;
block|}
DECL|method|addField
specifier|private
specifier|static
name|void
name|addField
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|A
operator|.
name|field
argument_list|()
argument_list|,
name|A
operator|.
name|text
argument_list|()
argument_list|,
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addFacets
specifier|private
specifier|static
name|void
name|addFacets
parameter_list|(
name|Document
name|doc
parameter_list|,
name|FacetsConfig
name|config
parameter_list|,
name|boolean
name|updateTermExpectedCounts
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FacetField
argument_list|>
name|docCategories
init|=
name|randomCategories
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|FacetField
name|ff
range|:
name|docCategories
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|ff
argument_list|)
expr_stmt|;
name|String
name|cp
init|=
name|ff
operator|.
name|dim
operator|+
literal|"/"
operator|+
name|ff
operator|.
name|path
index|[
literal|0
index|]
decl_stmt|;
name|allExpectedCounts
operator|.
name|put
argument_list|(
name|cp
argument_list|,
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|cp
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateTermExpectedCounts
condition|)
block|{
name|termExpectedCounts
operator|.
name|put
argument_list|(
name|cp
argument_list|,
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|cp
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add 1 to each NO_PARENTS dimension
name|allExpectedCounts
operator|.
name|put
argument_list|(
name|CP_B
argument_list|,
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|allExpectedCounts
operator|.
name|put
argument_list|(
name|CP_C
argument_list|,
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_C
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|allExpectedCounts
operator|.
name|put
argument_list|(
name|CP_D
argument_list|,
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_D
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateTermExpectedCounts
condition|)
block|{
name|termExpectedCounts
operator|.
name|put
argument_list|(
name|CP_B
argument_list|,
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|termExpectedCounts
operator|.
name|put
argument_list|(
name|CP_C
argument_list|,
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|CP_C
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|termExpectedCounts
operator|.
name|put
argument_list|(
name|CP_D
argument_list|,
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|CP_D
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConfig
specifier|private
specifier|static
name|FacetsConfig
name|getConfig
parameter_list|()
block|{
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"A"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"B"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setRequireDimCount
argument_list|(
literal|"B"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setHierarchical
argument_list|(
literal|"D"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
DECL|method|indexDocsNoFacets
specifier|private
specifier|static
name|void
name|indexDocsNoFacets
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|2
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
name|numDocs
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
name|addField
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// flush a segment
block|}
DECL|method|indexDocsWithFacetsNoTerms
specifier|private
specifier|static
name|void
name|indexDocsWithFacetsNoTerms
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|TaxonomyWriter
name|taxoWriter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expectedCounts
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
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
name|numDocs
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
name|addFacets
argument_list|(
name|doc
argument_list|,
name|config
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// flush a segment
block|}
DECL|method|indexDocsWithFacetsAndTerms
specifier|private
specifier|static
name|void
name|indexDocsWithFacetsAndTerms
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|TaxonomyWriter
name|taxoWriter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expectedCounts
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
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
name|numDocs
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
name|addFacets
argument_list|(
name|doc
argument_list|,
name|config
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|addField
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// flush a segment
block|}
DECL|method|indexDocsWithFacetsAndSomeTerms
specifier|private
specifier|static
name|void
name|indexDocsWithFacetsAndSomeTerms
parameter_list|(
name|IndexWriter
name|indexWriter
parameter_list|,
name|TaxonomyWriter
name|taxoWriter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|expectedCounts
parameter_list|)
throws|throws
name|IOException
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
name|random
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
name|getConfig
argument_list|()
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
name|numDocs
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
name|boolean
name|hasContent
init|=
name|random
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasContent
condition|)
block|{
name|addField
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|addFacets
argument_list|(
name|doc
argument_list|,
name|config
argument_list|,
name|hasContent
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// flush a segment
block|}
comment|// initialize expectedCounts w/ 0 for all categories
DECL|method|newCounts
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|newCounts
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|counts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|CP_A
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|CP_B
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|CP_C
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|counts
operator|.
name|put
argument_list|(
name|CP_D
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|FacetField
name|ff
range|:
name|CATEGORIES_A
control|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|ff
operator|.
name|dim
operator|+
literal|"/"
operator|+
name|ff
operator|.
name|path
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FacetField
name|ff
range|:
name|CATEGORIES_B
control|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|ff
operator|.
name|dim
operator|+
literal|"/"
operator|+
name|ff
operator|.
name|path
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FacetField
name|ff
range|:
name|CATEGORIES_C
control|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|ff
operator|.
name|dim
operator|+
literal|"/"
operator|+
name|ff
operator|.
name|path
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|FacetField
name|ff
range|:
name|CATEGORIES_D
control|)
block|{
name|counts
operator|.
name|put
argument_list|(
name|ff
operator|.
name|dim
operator|+
literal|"/"
operator|+
name|ff
operator|.
name|path
index|[
literal|0
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|counts
return|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClassCountingFacetsAggregatorTest
specifier|public
specifier|static
name|void
name|beforeClassCountingFacetsAggregatorTest
parameter_list|()
throws|throws
name|Exception
block|{
name|indexDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|taxoDir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
comment|// create an index which has:
comment|// 1. Segment with no categories, but matching results
comment|// 2. Segment w/ categories, but no results
comment|// 3. Segment w/ categories and results
comment|// 4. Segment w/ categories, but only some results
name|IndexWriterConfig
name|conf
init|=
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
comment|// prevent merges, so we can control the index segments
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
name|TaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|allExpectedCounts
operator|=
name|newCounts
argument_list|()
expr_stmt|;
name|termExpectedCounts
operator|=
name|newCounts
argument_list|()
expr_stmt|;
comment|// segment w/ no categories
name|indexDocsNoFacets
argument_list|(
name|indexWriter
argument_list|)
expr_stmt|;
comment|// segment w/ categories, no content
name|indexDocsWithFacetsNoTerms
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|,
name|allExpectedCounts
argument_list|)
expr_stmt|;
comment|// segment w/ categories and content
name|indexDocsWithFacetsAndTerms
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|,
name|allExpectedCounts
argument_list|)
expr_stmt|;
comment|// segment w/ categories and some content
name|indexDocsWithFacetsAndSomeTerms
argument_list|(
name|indexWriter
argument_list|,
name|taxoWriter
argument_list|,
name|allExpectedCounts
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|taxoWriter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDifferentNumResults
specifier|public
name|void
name|testDifferentNumResults
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test the collector w/ FacetRequests and different numResults
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
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|TermQuery
name|q
init|=
operator|new
name|TermQuery
argument_list|(
name|A
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|sfc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|sfc
argument_list|)
decl_stmt|;
name|FacetResult
name|result
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|NUM_CHILDREN_CP_A
argument_list|,
name|CP_A
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|result
operator|.
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|CP_A
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|NUM_CHILDREN_CP_B
argument_list|,
name|CP_B
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
argument_list|)
argument_list|,
name|result
operator|.
name|value
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|termExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAllCounts
specifier|public
name|void
name|testAllCounts
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|sfc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|sfc
argument_list|)
decl_stmt|;
name|FacetResult
name|result
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|NUM_CHILDREN_CP_A
argument_list|,
name|CP_A
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|result
operator|.
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|prevValue
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_A
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"wrong sort order of sub results: labelValue.value="
operator|+
name|labelValue
operator|.
name|value
operator|+
literal|" prevValue="
operator|+
name|prevValue
argument_list|,
name|labelValue
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|<=
name|prevValue
argument_list|)
expr_stmt|;
name|prevValue
operator|=
name|labelValue
operator|.
name|value
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|result
operator|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|NUM_CHILDREN_CP_B
argument_list|,
name|CP_B
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
argument_list|)
argument_list|,
name|result
operator|.
name|value
argument_list|)
expr_stmt|;
name|prevValue
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"wrong sort order of sub results: labelValue.value="
operator|+
name|labelValue
operator|.
name|value
operator|+
literal|" prevValue="
operator|+
name|prevValue
argument_list|,
name|labelValue
operator|.
name|value
operator|.
name|intValue
argument_list|()
operator|<=
name|prevValue
argument_list|)
expr_stmt|;
name|prevValue
operator|=
name|labelValue
operator|.
name|value
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBigNumResults
specifier|public
name|void
name|testBigNumResults
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|sfc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|sfc
argument_list|)
decl_stmt|;
name|FacetResult
name|result
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|CP_A
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|result
operator|.
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_A
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|CP_B
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
argument_list|)
argument_list|,
name|result
operator|.
name|value
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_B
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoParents
specifier|public
name|void
name|testNoParents
parameter_list|()
throws|throws
name|Exception
block|{
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
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|FacetsCollector
name|sfc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|sfc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
name|getTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|getConfig
argument_list|()
argument_list|,
name|sfc
argument_list|)
decl_stmt|;
name|FacetResult
name|result
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|NUM_CHILDREN_CP_C
argument_list|,
name|CP_C
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_C
argument_list|)
argument_list|,
name|result
operator|.
name|value
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_C
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|NUM_CHILDREN_CP_D
argument_list|,
name|CP_D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_C
argument_list|)
argument_list|,
name|result
operator|.
name|value
argument_list|)
expr_stmt|;
for|for
control|(
name|LabelAndValue
name|labelValue
range|:
name|result
operator|.
name|labelValues
control|)
block|{
name|assertEquals
argument_list|(
name|allExpectedCounts
operator|.
name|get
argument_list|(
name|CP_D
operator|+
literal|"/"
operator|+
name|labelValue
operator|.
name|label
argument_list|)
argument_list|,
name|labelValue
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|taxoReader
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

