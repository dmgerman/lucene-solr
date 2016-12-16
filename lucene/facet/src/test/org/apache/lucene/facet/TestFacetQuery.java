begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
package|;
end_package

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
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesFacetField
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
name|IndexableField
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|TestFacetQuery
specifier|public
class|class
name|TestFacetQuery
extends|extends
name|FacetTestCase
block|{
DECL|field|indexDirectory
specifier|private
specifier|static
name|Directory
name|indexDirectory
decl_stmt|;
DECL|field|indexWriter
specifier|private
specifier|static
name|RandomIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|indexReader
specifier|private
specifier|static
name|IndexReader
name|indexReader
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
name|FacetsConfig
name|config
decl_stmt|;
DECL|field|DOC_SINGLEVALUED
specifier|private
specifier|static
specifier|final
name|IndexableField
index|[]
name|DOC_SINGLEVALUED
init|=
operator|new
name|IndexableField
index|[]
block|{
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Mark Twain"
argument_list|)
block|}
decl_stmt|;
DECL|field|DOC_MULTIVALUED
specifier|private
specifier|static
specifier|final
name|IndexableField
index|[]
name|DOC_MULTIVALUED
init|=
operator|new
name|SortedSetDocValuesFacetField
index|[]
block|{
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
literal|"Author"
argument_list|,
literal|"Kurt Vonnegut"
argument_list|)
block|}
decl_stmt|;
DECL|field|DOC_NOFACET
specifier|private
specifier|static
specifier|final
name|IndexableField
index|[]
name|DOC_NOFACET
init|=
operator|new
name|IndexableField
index|[]
block|{
operator|new
name|TextField
argument_list|(
literal|"Hello"
argument_list|,
literal|"World"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
block|}
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createTestIndex
specifier|public
specifier|static
name|void
name|createTestIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|indexDirectory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
comment|// create and open an index writer
name|indexWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|indexDirectory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|=
operator|new
name|FacetsConfig
argument_list|()
expr_stmt|;
name|indexDocuments
argument_list|(
name|DOC_SINGLEVALUED
argument_list|,
name|DOC_MULTIVALUED
argument_list|,
name|DOC_NOFACET
argument_list|)
expr_stmt|;
name|indexReader
operator|=
name|indexWriter
operator|.
name|getReader
argument_list|()
expr_stmt|;
comment|// prepare searcher to search against
name|searcher
operator|=
name|newSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
block|}
DECL|method|indexDocuments
specifier|private
specifier|static
name|void
name|indexDocuments
parameter_list|(
name|IndexableField
index|[]
modifier|...
name|docs
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|IndexableField
index|[]
name|fields
range|:
name|docs
control|)
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|fields
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
name|field
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
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|AfterClass
DECL|method|closeTestIndex
specifier|public
specifier|static
name|void
name|closeTestIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|indexReader
argument_list|,
name|indexWriter
argument_list|,
name|indexDirectory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleValued
specifier|public
name|void
name|testSingleValued
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|FacetQuery
argument_list|(
literal|"Author"
argument_list|,
literal|"Mark Twain"
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultiValued
specifier|public
name|void
name|testMultiValued
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MultiFacetQuery
argument_list|(
literal|"Author"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Mark Twain"
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Kurt Vonnegut"
block|}
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

