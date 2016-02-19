begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
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
name|facet
operator|.
name|DrillDownQuery
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
name|SlowCompositeReaderWrapper
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
DECL|class|TestSortedSetDocValuesFacets
specifier|public
class|class
name|TestSortedSetDocValuesFacets
extends|extends
name|FacetTestCase
block|{
comment|// NOTE: TestDrillSideways.testRandom also sometimes
comment|// randomly uses SortedSetDV
DECL|method|testBasic
specifier|public
name|void
name|testBasic
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
literal|"a"
argument_list|,
literal|true
argument_list|)
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"zoo"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"b"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|FacetsCollector
name|c
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
name|c
argument_list|)
expr_stmt|;
name|SortedSetDocValuesFacetCounts
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"dim=a path=[] value=4 childCount=3\n  foo (2)\n  bar (1)\n  zoo (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"a"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=b path=[] value=1 childCount=1\n  baz (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"b"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// DrillDown:
name|DrillDownQuery
name|q
init|=
operator|new
name|DrillDownQuery
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
literal|"b"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|TopDocs
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-5090
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|testStaleState
specifier|public
name|void
name|testStaleState
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
name|writer
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
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|r
argument_list|)
decl_stmt|;
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|FacetsCollector
name|c
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
name|c
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
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
comment|// LUCENE-5333
DECL|method|testSparseFacets
specifier|public
name|void
name|testSparseFacets
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
name|writer
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
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"b"
argument_list|,
literal|"bar1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo3"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"b"
argument_list|,
literal|"bar2"
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"c"
argument_list|,
literal|"baz1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|FacetsCollector
name|c
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
name|c
argument_list|)
expr_stmt|;
name|SortedSetDocValuesFacetCounts
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Ask for top 10 labels for any dims that have counts:
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
name|facets
operator|.
name|getAllDims
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=a path=[] value=3 childCount=3\n  foo1 (1)\n  foo2 (1)\n  foo3 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=b path=[] value=2 childCount=2\n  bar1 (1)\n  bar2 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"dim=c path=[] value=1 childCount=1\n  baz1 (1)\n"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
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
DECL|method|testSomeSegmentsMissing
specifier|public
name|void
name|testSomeSegmentsMissing
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
name|writer
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
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|FacetsCollector
name|c
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
name|c
argument_list|)
expr_stmt|;
name|SortedSetDocValuesFacetCounts
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Ask for top 10 labels for any dims that have counts:
name|assertEquals
argument_list|(
literal|"dim=a path=[] value=2 childCount=2\n  foo1 (1)\n  foo2 (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"a"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|getIndexReader
argument_list|()
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
DECL|method|testSlowCompositeReaderWrapper
specifier|public
name|void
name|testSlowCompositeReaderWrapper
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
name|writer
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
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo1"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
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
name|SortedSetDocValuesFacetField
argument_list|(
literal|"a"
argument_list|,
literal|"foo2"
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|writer
operator|.
name|getReader
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|FacetsCollector
name|c
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
name|c
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|c
argument_list|)
decl_stmt|;
comment|// Ask for top 10 labels for any dims that have counts:
name|assertEquals
argument_list|(
literal|"dim=a path=[] value=2 childCount=2\n  foo1 (1)\n  foo2 (1)\n"
argument_list|,
name|facets
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"a"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|tokens
init|=
name|getRandomTokens
argument_list|(
literal|10
argument_list|)
decl_stmt|;
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
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDir
argument_list|)
decl_stmt|;
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
argument_list|()
decl_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|int
name|numDims
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
literal|7
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TestDoc
argument_list|>
name|testDocs
init|=
name|getRandomDocs
argument_list|(
name|tokens
argument_list|,
name|numDocs
argument_list|,
name|numDims
argument_list|)
decl_stmt|;
for|for
control|(
name|TestDoc
name|testDoc
range|:
name|testDocs
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
literal|"content"
argument_list|,
name|testDoc
operator|.
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
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
name|numDims
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|testDoc
operator|.
name|dims
index|[
name|j
index|]
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"dim"
operator|+
name|j
argument_list|,
name|testDoc
operator|.
name|dims
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|w
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// NRT open
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
comment|// Per-top-reader state:
name|SortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
name|String
name|searchToken
init|=
name|tokens
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|tokens
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
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
literal|"\nTEST: iter content="
operator|+
name|searchToken
argument_list|)
expr_stmt|;
block|}
name|FacetsCollector
name|fc
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
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
name|searchToken
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|facets
init|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|fc
argument_list|)
decl_stmt|;
comment|// Slow, yet hopefully bug-free, faceting:
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
index|[]
name|expectedCounts
init|=
operator|new
name|HashMap
index|[
name|numDims
index|]
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
name|numDims
condition|;
name|i
operator|++
control|)
block|{
name|expectedCounts
index|[
name|i
index|]
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|TestDoc
name|doc
range|:
name|testDocs
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|content
operator|.
name|equals
argument_list|(
name|searchToken
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDims
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|doc
operator|.
name|dims
index|[
name|j
index|]
operator|!=
literal|null
condition|)
block|{
name|Integer
name|v
init|=
name|expectedCounts
index|[
name|j
index|]
operator|.
name|get
argument_list|(
name|doc
operator|.
name|dims
index|[
name|j
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|expectedCounts
index|[
name|j
index|]
operator|.
name|put
argument_list|(
name|doc
operator|.
name|dims
index|[
name|j
index|]
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectedCounts
index|[
name|j
index|]
operator|.
name|put
argument_list|(
name|doc
operator|.
name|dims
index|[
name|j
index|]
argument_list|,
name|v
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
name|List
argument_list|<
name|FacetResult
argument_list|>
name|expected
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numDims
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|LabelAndValue
argument_list|>
name|labelValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|totCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ent
range|:
name|expectedCounts
index|[
name|i
index|]
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|labelValues
operator|.
name|add
argument_list|(
operator|new
name|LabelAndValue
argument_list|(
name|ent
operator|.
name|getKey
argument_list|()
argument_list|,
name|ent
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|totCount
operator|+=
name|ent
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|sortLabelValues
argument_list|(
name|labelValues
argument_list|)
expr_stmt|;
if|if
condition|(
name|totCount
operator|>
literal|0
condition|)
block|{
name|expected
operator|.
name|add
argument_list|(
operator|new
name|FacetResult
argument_list|(
literal|"dim"
operator|+
name|i
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|,
name|totCount
argument_list|,
name|labelValues
operator|.
name|toArray
argument_list|(
operator|new
name|LabelAndValue
index|[
name|labelValues
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|labelValues
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Sort by highest value, tie break by value:
name|sortFacetResults
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|actual
init|=
name|facets
operator|.
name|getAllDims
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// Messy: fixup ties
comment|//sortTies(actual);
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|indexDir
argument_list|,
name|taxoDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

