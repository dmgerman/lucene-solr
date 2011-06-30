begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
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
name|junit
operator|.
name|Test
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
name|facet
operator|.
name|example
operator|.
name|merge
operator|.
name|TaxonomyMergeUtils
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
name|search
operator|.
name|params
operator|.
name|CountFacetRequest
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
name|results
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
name|search
operator|.
name|results
operator|.
name|FacetResultNode
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
name|lucene
operator|.
name|LuceneTaxonomyReader
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
name|lucene
operator|.
name|LuceneTaxonomyWriter
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|FacetsPayloadProcessorProviderTest
specifier|public
class|class
name|FacetsPayloadProcessorProviderTest
extends|extends
name|LuceneTestCase
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
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|buildIndexWithFacets
argument_list|(
name|dir
argument_list|,
name|taxDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|dir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|taxDir1
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|buildIndexWithFacets
argument_list|(
name|dir1
argument_list|,
name|taxDir1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|TaxonomyMergeUtils
operator|.
name|merge
argument_list|(
name|dir
argument_list|,
name|taxDir
argument_list|,
name|dir1
argument_list|,
name|taxDir1
argument_list|)
expr_stmt|;
name|verifyResults
argument_list|(
name|dir1
argument_list|,
name|taxDir1
argument_list|)
expr_stmt|;
name|dir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxDir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyResults
specifier|private
name|void
name|verifyResults
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Directory
name|taxDir
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexReader
name|reader1
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|LuceneTaxonomyReader
name|taxReader
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|taxDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader1
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|()
decl_stmt|;
name|fsp
operator|.
name|addFacetRequest
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"tag"
argument_list|)
argument_list|,
name|NUM_DOCS
argument_list|)
argument_list|)
expr_stmt|;
name|FacetsCollector
name|collector
init|=
operator|new
name|FacetsCollector
argument_list|(
name|fsp
argument_list|,
name|reader1
argument_list|,
name|taxReader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|FacetResult
name|result
init|=
name|collector
operator|.
name|getFacetResults
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FacetResultNode
name|node
init|=
name|result
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetResultNode
name|facet
range|:
name|node
operator|.
name|getSubResults
argument_list|()
control|)
block|{
name|int
name|weight
init|=
operator|(
name|int
operator|)
name|facet
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|label
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|facet
operator|.
name|getLabel
argument_list|()
operator|.
name|getComponent
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println(label + ": " + weight);
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
name|label
operator|+
literal|": "
operator|+
name|weight
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|NUM_DOCS
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
name|reader1
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|buildIndexWithFacets
specifier|private
name|void
name|buildIndexWithFacets
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Directory
name|taxDir
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
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|LuceneTaxonomyWriter
name|taxonomyWriter
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|taxDir
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
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|categoryPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|(
name|i
operator|+
literal|1
argument_list|)
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
name|categoryPaths
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
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
name|CategoryDocumentBuilder
name|catBuilder
init|=
operator|new
name|CategoryDocumentBuilder
argument_list|(
name|taxonomyWriter
argument_list|)
decl_stmt|;
name|catBuilder
operator|.
name|setCategoryPaths
argument_list|(
name|categoryPaths
argument_list|)
expr_stmt|;
name|catBuilder
operator|.
name|build
argument_list|(
name|doc
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
name|taxonomyWriter
operator|.
name|close
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

