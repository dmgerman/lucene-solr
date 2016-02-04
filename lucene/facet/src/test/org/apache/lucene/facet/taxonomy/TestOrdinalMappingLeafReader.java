begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BinaryDocValuesField
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
name|facet
operator|.
name|taxonomy
operator|.
name|directory
operator|.
name|DirectoryTaxonomyWriter
operator|.
name|MemoryOrdinalMap
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
name|BinaryDocValues
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
name|MultiDocValues
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
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|TestOrdinalMappingLeafReader
specifier|public
class|class
name|TestOrdinalMappingLeafReader
extends|extends
name|FacetTestCase
block|{
DECL|field|NUM_DOCS
specifier|private
specifier|static
specifier|final
name|int
name|NUM_DOCS
init|=
literal|100
decl_stmt|;
DECL|field|facetConfig
specifier|private
specifier|final
name|FacetsConfig
name|facetConfig
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
annotation|@
name|Before
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
name|facetConfig
operator|.
name|setMultiValued
argument_list|(
literal|"tag"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|facetConfig
operator|.
name|setIndexFieldName
argument_list|(
literal|"tag"
argument_list|,
literal|"$tags"
argument_list|)
expr_stmt|;
comment|// add custom index field name
block|}
annotation|@
name|Test
DECL|method|testTaxonomyMergeUtils
specifier|public
name|void
name|testTaxonomyMergeUtils
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|srcIndexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|srcTaxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|buildIndexWithFacets
argument_list|(
name|srcIndexDir
argument_list|,
name|srcTaxoDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|targetIndexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|targetTaxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|buildIndexWithFacets
argument_list|(
name|targetIndexDir
argument_list|,
name|targetTaxoDir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexWriter
name|destIndexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|targetIndexDir
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|destTaxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|targetTaxoDir
argument_list|)
decl_stmt|;
try|try
block|{
name|TaxonomyMergeUtils
operator|.
name|merge
argument_list|(
name|srcIndexDir
argument_list|,
name|srcTaxoDir
argument_list|,
operator|new
name|MemoryOrdinalMap
argument_list|()
argument_list|,
name|destIndexWriter
argument_list|,
name|destTaxoWriter
argument_list|,
name|facetConfig
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|destIndexWriter
argument_list|,
name|destTaxoWriter
argument_list|)
expr_stmt|;
block|}
name|verifyResults
argument_list|(
name|targetIndexDir
argument_list|,
name|targetTaxoDir
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|targetIndexDir
argument_list|,
name|targetTaxoDir
argument_list|,
name|srcIndexDir
argument_list|,
name|srcTaxoDir
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyResults
specifier|private
name|void
name|verifyResults
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
name|DirectoryTaxonomyReader
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
name|collector
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
name|FacetsCollector
operator|.
name|search
argument_list|(
name|searcher
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|,
name|collector
argument_list|)
expr_stmt|;
comment|// tag facets
name|Facets
name|tagFacets
init|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
literal|"$tags"
argument_list|,
name|taxoReader
argument_list|,
name|facetConfig
argument_list|,
name|collector
argument_list|)
decl_stmt|;
name|FacetResult
name|result
init|=
name|tagFacets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"tag"
argument_list|)
decl_stmt|;
for|for
control|(
name|LabelAndValue
name|lv
range|:
name|result
operator|.
name|labelValues
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|lv
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|NUM_DOCS
argument_list|,
name|lv
operator|.
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// id facets
name|Facets
name|idFacets
init|=
operator|new
name|FastTaxonomyFacetCounts
argument_list|(
name|taxoReader
argument_list|,
name|facetConfig
argument_list|,
name|collector
argument_list|)
decl_stmt|;
name|FacetResult
name|idResult
init|=
name|idFacets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
argument_list|,
name|idResult
operator|.
name|childCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NUM_DOCS
operator|*
literal|2
argument_list|,
name|idResult
operator|.
name|value
argument_list|)
expr_stmt|;
comment|// each "id" appears twice
name|BinaryDocValues
name|bdv
init|=
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|indexReader
argument_list|,
literal|"bdv"
argument_list|)
decl_stmt|;
name|BinaryDocValues
name|cbdv
init|=
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|indexReader
argument_list|,
literal|"cbdv"
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
name|indexReader
operator|.
name|maxDoc
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|cbdv
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|bdv
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|*
literal|2
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
DECL|method|buildIndexWithFacets
specifier|private
name|void
name|buildIndexWithFacets
parameter_list|(
name|Directory
name|indexDir
parameter_list|,
name|Directory
name|taxoDir
parameter_list|,
name|boolean
name|asc
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriterConfig
name|config
init|=
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
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
name|indexDir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|DirectoryTaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|NUM_DOCS
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
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<=
name|NUM_DOCS
condition|;
name|j
operator|++
control|)
block|{
name|int
name|facetValue
init|=
name|asc
condition|?
name|j
else|:
name|NUM_DOCS
operator|-
name|j
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"tag"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|facetValue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// add a facet under default dim config
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FacetField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// make sure OrdinalMappingLeafReader ignores non-facet BinaryDocValues fields
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"bdv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|BinaryDocValuesField
argument_list|(
literal|"cbdv"
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|facetConfig
operator|.
name|build
argument_list|(
name|taxonomyWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|taxonomyWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|taxonomyWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

