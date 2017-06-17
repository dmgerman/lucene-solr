begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.uhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|uhighlight
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
name|Analyzer
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
name|index
operator|.
name|*
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
name|Sort
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|BitSet
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

begin_comment
comment|/**  * Tests highlighting for matters *expressly* relating to term vectors.  *<p>  * This test DOES NOT represent all testing for highlighting when term vectors are used.  Other tests pick the offset  * source at random (to include term vectors) and in-effect test term vectors generally.  */
end_comment

begin_class
DECL|class|TestUnifiedHighlighterTermVec
specifier|public
class|class
name|TestUnifiedHighlighterTermVec
extends|extends
name|LuceneTestCase
block|{
DECL|field|indexAnalyzer
specifier|private
name|Analyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
annotation|@
name|Before
DECL|method|doBefore
specifier|public
name|void
name|doBefore
parameter_list|()
throws|throws
name|IOException
block|{
name|indexAnalyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//whitespace, punctuation, lowercase
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|doAfter
specifier|public
name|void
name|doAfter
parameter_list|()
throws|throws
name|IOException
block|{
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFetchTermVecsOncePerDoc
specifier|public
name|void
name|testFetchTermVecsOncePerDoc
parameter_list|()
throws|throws
name|IOException
block|{
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
name|indexAnalyzer
argument_list|)
decl_stmt|;
comment|// Declare some number of fields with random field type; but at least one will have term vectors.
specifier|final
name|int
name|numTvFields
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numTvFields
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|FieldType
argument_list|>
name|fieldTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numTvFields
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
name|numTvFields
condition|;
name|i
operator|++
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
literal|"body"
operator|+
name|i
argument_list|)
expr_stmt|;
name|fieldTypes
operator|.
name|add
argument_list|(
name|UHTestHelper
operator|.
name|randomFieldType
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//ensure at least one has TVs by setting one randomly to it:
name|fieldTypes
operator|.
name|set
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|fieldTypes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
name|UHTestHelper
operator|.
name|tvType
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
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
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
literal|"some test text"
argument_list|,
name|UHTestHelper
operator|.
name|tvType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// Wrap the reader to ensure we only fetch TVs once per doc
name|DirectoryReader
name|originalReader
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|IndexReader
name|ir
init|=
operator|new
name|AssertOnceTermVecDirectoryReader
argument_list|(
name|originalReader
argument_list|)
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|UnifiedHighlighter
name|highlighter
init|=
operator|new
name|UnifiedHighlighter
argument_list|(
name|searcher
argument_list|,
name|indexAnalyzer
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
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
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
name|field
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
name|query
init|=
name|queryBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numDocs
argument_list|,
name|topDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|fieldToSnippets
init|=
name|highlighter
operator|.
name|highlightFields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|numTvFields
index|]
argument_list|)
argument_list|,
name|query
argument_list|,
name|topDocs
argument_list|)
decl_stmt|;
name|String
index|[]
name|expectedSnippetsByDoc
init|=
operator|new
name|String
index|[
name|numDocs
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|expectedSnippetsByDoc
argument_list|,
literal|"some<b>test</b> text"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|field
range|:
name|fields
control|)
block|{
name|assertArrayEquals
argument_list|(
name|expectedSnippetsByDoc
argument_list|,
name|fieldToSnippets
operator|.
name|get
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|AssertOnceTermVecDirectoryReader
specifier|private
specifier|static
class|class
name|AssertOnceTermVecDirectoryReader
extends|extends
name|FilterDirectoryReader
block|{
DECL|field|SUB_READER_WRAPPER
specifier|static
specifier|final
name|SubReaderWrapper
name|SUB_READER_WRAPPER
init|=
operator|new
name|SubReaderWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LeafReader
name|wrap
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|FilterLeafReader
argument_list|(
name|reader
argument_list|)
block|{
name|BitSet
name|seenDocIDs
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
comment|// if we're invoked by ParallelLeafReader then we can't do our assertion. TODO see LUCENE-6868
if|if
condition|(
name|calledBy
argument_list|(
name|ParallelLeafReader
operator|.
name|class
argument_list|)
operator|==
literal|false
operator|&&
name|calledBy
argument_list|(
name|CheckIndex
operator|.
name|class
argument_list|)
operator|==
literal|false
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Should not request TVs for doc more than once."
argument_list|,
name|seenDocIDs
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
expr_stmt|;
name|seenDocIDs
operator|.
name|set
argument_list|(
name|docID
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
DECL|method|AssertOnceTermVecDirectoryReader
name|AssertOnceTermVecDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|SUB_READER_WRAPPER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWrapDirectoryReader
specifier|protected
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AssertOnceTermVecDirectoryReader
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|calledBy
specifier|private
specifier|static
name|boolean
name|calledBy
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
for|for
control|(
name|StackTraceElement
name|stackTraceElement
range|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
if|if
condition|(
name|stackTraceElement
operator|.
name|getClassName
argument_list|()
operator|.
name|equals
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testUserFailedToIndexOffsets
specifier|public
name|void
name|testUserFailedToIndexOffsets
parameter_list|()
throws|throws
name|IOException
block|{
name|FieldType
name|fieldType
init|=
operator|new
name|FieldType
argument_list|(
name|UHTestHelper
operator|.
name|tvType
argument_list|)
decl_stmt|;
comment|// note: it's indexed too
name|fieldType
operator|.
name|setStoreTermVectorPositions
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|fieldType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|indexAnalyzer
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
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|"term vectors"
argument_list|,
name|fieldType
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
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|UnifiedHighlighter
name|highlighter
init|=
operator|new
name|UnifiedHighlighter
argument_list|(
name|searcher
argument_list|,
name|indexAnalyzer
argument_list|)
decl_stmt|;
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"vectors"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|)
decl_stmt|;
try|try
block|{
name|highlighter
operator|.
name|highlight
argument_list|(
literal|"body"
argument_list|,
name|query
argument_list|,
name|topDocs
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//should throw
block|}
finally|finally
block|{
name|ir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

