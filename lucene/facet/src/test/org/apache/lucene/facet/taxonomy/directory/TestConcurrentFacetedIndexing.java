begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
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
operator|.
name|directory
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
name|Random
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
name|ConcurrentHashMap
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
name|AtomicInteger
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
name|taxonomy
operator|.
name|FacetLabel
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
name|writercache
operator|.
name|TaxonomyWriterCache
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
name|writercache
operator|.
name|Cl2oTaxonomyWriterCache
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
name|writercache
operator|.
name|LruTaxonomyWriterCache
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

begin_comment
comment|/** Tests concurrent indexing with facets. */
end_comment

begin_class
DECL|class|TestConcurrentFacetedIndexing
specifier|public
class|class
name|TestConcurrentFacetedIndexing
extends|extends
name|FacetTestCase
block|{
comment|// A No-Op TaxonomyWriterCache which always discards all given categories, and
comment|// always returns true in put(), to indicate some cache entries were cleared.
DECL|field|NO_OP_CACHE
specifier|private
specifier|static
name|TaxonomyWriterCache
name|NO_OP_CACHE
init|=
operator|new
name|TaxonomyWriterCache
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{}
annotation|@
name|Override
specifier|public
name|int
name|get
parameter_list|(
name|FacetLabel
name|categoryPath
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|put
parameter_list|(
name|FacetLabel
name|categoryPath
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{}
block|}
decl_stmt|;
DECL|method|newCategory
specifier|static
name|FacetField
name|newCategory
parameter_list|()
block|{
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
name|String
name|l1
init|=
literal|"l1."
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
comment|// l1.0-l1.9 (10 categories)
name|String
name|l2
init|=
literal|"l2."
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|30
argument_list|)
decl_stmt|;
comment|// l2.0-l2.29 (30 categories)
name|String
name|l3
init|=
literal|"l3."
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
comment|// l3.0-l3.99 (100 categories)
return|return
operator|new
name|FacetField
argument_list|(
name|l1
argument_list|,
name|l2
argument_list|,
name|l3
argument_list|)
return|;
block|}
DECL|method|newTaxoWriterCache
specifier|static
name|TaxonomyWriterCache
name|newTaxoWriterCache
parameter_list|(
name|int
name|ndocs
parameter_list|)
block|{
specifier|final
name|double
name|d
init|=
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0.7
condition|)
block|{
comment|// this is the fastest, yet most memory consuming
return|return
operator|new
name|Cl2oTaxonomyWriterCache
argument_list|(
literal|1024
argument_list|,
literal|0.15f
argument_list|,
literal|3
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|TEST_NIGHTLY
operator|&&
name|d
operator|>
literal|0.98
condition|)
block|{
comment|// this is the slowest, but tests the writer concurrency when no caching is done.
comment|// only pick it during NIGHTLY tests, and even then, with very low chances.
return|return
name|NO_OP_CACHE
return|;
block|}
else|else
block|{
comment|// this is slower than CL2O, but less memory consuming, and exercises finding categories on disk too.
return|return
operator|new
name|LruTaxonomyWriterCache
argument_list|(
name|ndocs
operator|/
literal|10
argument_list|)
return|;
block|}
block|}
DECL|method|testConcurrency
specifier|public
name|void
name|testConcurrency
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|numDocs
init|=
operator|new
name|AtomicInteger
argument_list|(
name|atLeast
argument_list|(
literal|10000
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Directory
name|indexDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|Directory
name|taxoDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|values
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
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
literal|null
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
argument_list|,
name|OpenMode
operator|.
name|CREATE
argument_list|,
name|newTaxoWriterCache
argument_list|(
name|numDocs
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Thread
index|[]
name|indexThreads
init|=
operator|new
name|Thread
index|[
name|atLeast
argument_list|(
literal|4
argument_list|)
index|]
decl_stmt|;
specifier|final
name|FacetsConfig
name|config
init|=
operator|new
name|FacetsConfig
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|config
operator|.
name|setHierarchical
argument_list|(
literal|"l1."
operator|+
name|i
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"l1."
operator|+
name|i
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexThreads
index|[
name|i
index|]
operator|=
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
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
while|while
condition|(
name|numDocs
operator|.
name|decrementAndGet
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|numCats
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
while|while
condition|(
name|numCats
operator|--
operator|>
literal|0
condition|)
block|{
name|FacetField
name|ff
init|=
name|newCategory
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|ff
argument_list|)
expr_stmt|;
name|FacetLabel
name|label
init|=
operator|new
name|FacetLabel
argument_list|(
name|ff
operator|.
name|dim
argument_list|,
name|ff
operator|.
name|path
argument_list|)
decl_stmt|;
comment|// add all prefixes to values
name|int
name|level
init|=
name|label
operator|.
name|length
decl_stmt|;
while|while
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|String
name|s
init|=
name|FacetsConfig
operator|.
name|pathToString
argument_list|(
name|label
operator|.
name|components
argument_list|,
name|level
argument_list|)
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
name|s
argument_list|,
name|s
argument_list|)
expr_stmt|;
operator|--
name|level
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|tw
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|indexThreads
control|)
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|indexThreads
control|)
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyReader
name|tr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|tw
argument_list|)
decl_stmt|;
comment|// +1 for root category
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|!=
name|tr
operator|.
name|getSize
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|value
range|:
name|values
operator|.
name|keySet
argument_list|()
control|)
block|{
name|FacetLabel
name|label
init|=
operator|new
name|FacetLabel
argument_list|(
name|FacetsConfig
operator|.
name|stringToPath
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|tr
operator|.
name|getOrdinal
argument_list|(
name|label
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FAIL: path="
operator|+
name|label
operator|+
literal|" not recognized"
argument_list|)
expr_stmt|;
block|}
block|}
name|fail
argument_list|(
literal|"mismatch number of categories"
argument_list|)
expr_stmt|;
block|}
name|int
index|[]
name|parents
init|=
name|tr
operator|.
name|getParallelTaxonomyArrays
argument_list|()
operator|.
name|parents
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|cat
range|:
name|values
operator|.
name|keySet
argument_list|()
control|)
block|{
name|FacetLabel
name|cp
init|=
operator|new
name|FacetLabel
argument_list|(
name|FacetsConfig
operator|.
name|stringToPath
argument_list|(
name|cat
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"category not found "
operator|+
name|cp
argument_list|,
name|tr
operator|.
name|getOrdinal
argument_list|(
name|cp
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|int
name|level
init|=
name|cp
operator|.
name|length
decl_stmt|;
name|int
name|parentOrd
init|=
literal|0
decl_stmt|;
comment|// for root, parent is always virtual ROOT (ord=0)
name|FacetLabel
name|path
init|=
literal|null
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
name|level
condition|;
name|i
operator|++
control|)
block|{
name|path
operator|=
name|cp
operator|.
name|subpath
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|ord
init|=
name|tr
operator|.
name|getOrdinal
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"invalid parent for cp="
operator|+
name|path
argument_list|,
name|parentOrd
argument_list|,
name|parents
index|[
name|ord
index|]
argument_list|)
expr_stmt|;
name|parentOrd
operator|=
name|ord
expr_stmt|;
comment|// next level should have this parent
block|}
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|tw
argument_list|,
name|tr
argument_list|,
name|taxoDir
argument_list|,
name|indexDir
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

