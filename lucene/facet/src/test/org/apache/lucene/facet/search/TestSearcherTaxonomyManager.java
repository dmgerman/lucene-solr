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
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|SearcherTaxonomyManager
operator|.
name|SearcherAndTaxonomy
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
name|_TestUtil
import|;
end_import

begin_class
DECL|class|TestSearcherTaxonomyManager
specifier|public
class|class
name|TestSearcherTaxonomyManager
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
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
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
specifier|final
name|DirectoryTaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
specifier|final
name|FacetFields
name|facetFields
init|=
operator|new
name|FacetFields
argument_list|(
name|tw
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
comment|// How many unique facets to index before stopping:
specifier|final
name|int
name|ordLimit
init|=
name|TEST_NIGHTLY
condition|?
literal|100000
else|:
literal|6000
decl_stmt|;
name|Thread
name|indexer
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|seen
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
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
name|docPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|CategoryPath
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|numPaths
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|5
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
name|numPaths
condition|;
name|i
operator|++
control|)
block|{
name|String
name|path
decl_stmt|;
if|if
condition|(
operator|!
name|paths
operator|.
name|isEmpty
argument_list|()
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|!=
literal|4
condition|)
block|{
comment|// Use previous path
name|path
operator|=
name|paths
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Create new path
name|path
operator|=
literal|null
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|path
operator|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
name|seen
operator|.
name|contains
argument_list|(
name|path
argument_list|)
operator|&&
name|path
operator|.
name|indexOf
argument_list|(
name|FacetIndexingParams
operator|.
name|DEFAULT_FACET_DELIM_CHAR
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|seen
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|docPaths
operator|.
name|add
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"field"
argument_list|,
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|facetFields
operator|.
name|addFields
argument_list|(
name|doc
argument_list|,
name|docPaths
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
if|if
condition|(
name|tw
operator|.
name|getSize
argument_list|()
operator|>=
name|ordLimit
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|SearcherTaxonomyManager
name|mgr
init|=
operator|new
name|SearcherTaxonomyManager
argument_list|(
name|w
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|tw
argument_list|)
decl_stmt|;
name|Thread
name|reopener
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
comment|// Sleep for up to 20 msec:
name|Thread
operator|.
name|sleep
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"TEST: reopen"
argument_list|)
expr_stmt|;
block|}
name|mgr
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
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
literal|"TEST: reopen done"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|reopener
operator|.
name|start
argument_list|()
expr_stmt|;
name|indexer
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
while|while
condition|(
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
name|SearcherAndTaxonomy
name|pair
init|=
name|mgr
operator|.
name|acquire
argument_list|()
decl_stmt|;
try|try
block|{
comment|//System.out.println("search maxOrd=" + pair.taxonomyReader.getSize());
name|int
name|topN
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|CountFacetRequest
name|cfr
init|=
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|topN
argument_list|)
decl_stmt|;
name|FacetSearchParams
name|fsp
init|=
operator|new
name|FacetSearchParams
argument_list|(
name|cfr
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
name|FacetsCollector
operator|.
name|create
argument_list|(
name|fsp
argument_list|,
name|pair
operator|.
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|pair
operator|.
name|taxonomyReader
argument_list|)
decl_stmt|;
name|pair
operator|.
name|searcher
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
name|results
init|=
name|fc
operator|.
name|getFacetResults
argument_list|()
decl_stmt|;
name|FacetResult
name|fr
init|=
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|FacetResultNode
name|root
init|=
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFacetResultNode
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|ordinal
operator|!=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|pair
operator|.
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//System.out.println(pair.taxonomyReader.getSize());
name|assertTrue
argument_list|(
name|fr
operator|.
name|getNumValidDescendants
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|subResults
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//if (VERBOSE) {
comment|//System.out.println("TEST: facets=" + FacetTestUtils.toSimpleString(results.get(0)));
comment|//}
block|}
finally|finally
block|{
name|mgr
operator|.
name|release
argument_list|(
name|pair
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|indexer
operator|.
name|join
argument_list|()
expr_stmt|;
name|reopener
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
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
literal|"TEST: now stop"
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|mgr
argument_list|,
name|tw
argument_list|,
name|w
argument_list|,
name|taxoDir
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|testReplaceTaxonomy
specifier|public
name|void
name|testReplaceTaxonomy
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
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
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
name|DirectoryTaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|Directory
name|taxoDir2
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|tw2
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir2
argument_list|)
decl_stmt|;
name|tw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|SearcherTaxonomyManager
name|mgr
init|=
operator|new
name|SearcherTaxonomyManager
argument_list|(
name|w
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|tw
argument_list|)
decl_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
operator|new
name|Document
argument_list|()
argument_list|)
expr_stmt|;
name|tw
operator|.
name|replaceTaxonomy
argument_list|(
name|taxoDir2
argument_list|)
expr_stmt|;
name|taxoDir2
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|mgr
operator|.
name|maybeRefresh
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"should have hit exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ise
parameter_list|)
block|{
comment|// expected
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|mgr
argument_list|,
name|tw
argument_list|,
name|w
argument_list|,
name|taxoDir
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

