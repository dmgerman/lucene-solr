begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|KeywordAnalyzer
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
name|standard
operator|.
name|StandardAnalyzer
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
name|SetBasedFieldSelector
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
name|queryParser
operator|.
name|QueryParser
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
name|store
operator|.
name|MockRAMDirectory
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
name|Collections
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Tests {@link MultiSearcher} class.  *  * @version $Id$  */
end_comment

begin_class
DECL|class|TestMultiSearcher
specifier|public
class|class
name|TestMultiSearcher
extends|extends
name|TestCase
block|{
DECL|method|TestMultiSearcher
specifier|public
name|TestMultiSearcher
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * ReturnS a new instance of the concrete MultiSearcher class 	 * used in this test. 	 */
DECL|method|getMultiSearcherInstance
specifier|protected
name|MultiSearcher
name|getMultiSearcherInstance
parameter_list|(
name|Searcher
index|[]
name|searchers
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|MultiSearcher
argument_list|(
name|searchers
argument_list|)
return|;
block|}
DECL|method|testEmptyIndex
specifier|public
name|void
name|testEmptyIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// creating two directories for indices
name|Directory
name|indexStoreA
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|Directory
name|indexStoreB
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
comment|// creating a document to store
name|Document
name|lDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
literal|"Once upon a time....."
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating a document to store
name|Document
name|lDoc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
literal|"in a galaxy far far away....."
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"doc2"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating a document to store
name|Document
name|lDoc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
literal|"a bizarre bug manifested itself...."
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"doc3"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// creating an index writer for the first index
name|IndexWriter
name|writerA
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreA
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// creating an index writer for the second index, but writing nothing
name|IndexWriter
name|writerB
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//--------------------------------------------------------------------
comment|// scenario 1
comment|//--------------------------------------------------------------------
comment|// writing the documents to the first index
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc2
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc3
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerA
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// closing the second index
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// creating the query
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
literal|"fulltext"
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"handle:1"
argument_list|)
decl_stmt|;
comment|// building the searchables
name|Searcher
index|[]
name|searchers
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
comment|// VITAL STEP:adding the searcher for the empty index first, before the searcher for the populated index
name|searchers
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|)
expr_stmt|;
name|searchers
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|)
expr_stmt|;
comment|// creating the multiSearcher
name|Searcher
name|mSearcher
init|=
name|getMultiSearcherInstance
argument_list|(
name|searchers
argument_list|)
decl_stmt|;
comment|// performing the search
name|Hits
name|hits
init|=
name|mSearcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterating over the hit documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
name|mSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//--------------------------------------------------------------------
comment|// scenario 2
comment|//--------------------------------------------------------------------
comment|// adding one document to the empty index
name|writerB
operator|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|addDocument
argument_list|(
name|lDoc
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// building the searchables
name|Searcher
index|[]
name|searchers2
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
comment|// VITAL STEP:adding the searcher for the empty index first, before the searcher for the populated index
name|searchers2
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|)
expr_stmt|;
name|searchers2
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|)
expr_stmt|;
comment|// creating the mulitSearcher
name|MultiSearcher
name|mSearcher2
init|=
name|getMultiSearcherInstance
argument_list|(
name|searchers2
argument_list|)
decl_stmt|;
comment|// performing the same search
name|Hits
name|hits2
init|=
name|mSearcher2
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|hits2
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterating over the hit documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits2
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// no exception should happen at this point
name|Document
name|d
init|=
name|hits2
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
comment|// test the subSearcher() method:
name|Query
name|subSearcherQuery
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"id:doc1"
argument_list|)
decl_stmt|;
name|hits2
operator|=
name|mSearcher2
operator|.
name|search
argument_list|(
name|subSearcherQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|hits2
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mSearcher2
operator|.
name|subSearcher
argument_list|(
name|hits2
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// hit from searchers2[0]
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mSearcher2
operator|.
name|subSearcher
argument_list|(
name|hits2
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// hit from searchers2[1]
name|subSearcherQuery
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"id:doc2"
argument_list|)
expr_stmt|;
name|hits2
operator|=
name|mSearcher2
operator|.
name|search
argument_list|(
name|subSearcherQuery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|hits2
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mSearcher2
operator|.
name|subSearcher
argument_list|(
name|hits2
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// hit from searchers2[1]
name|mSearcher2
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//--------------------------------------------------------------------
comment|// scenario 3
comment|//--------------------------------------------------------------------
comment|// deleting the document just added, this will cause a different exception to take place
name|Term
name|term
init|=
operator|new
name|Term
argument_list|(
literal|"id"
argument_list|,
literal|"doc1"
argument_list|)
decl_stmt|;
name|IndexReader
name|readerB
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|indexStoreB
argument_list|)
decl_stmt|;
name|readerB
operator|.
name|deleteDocuments
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|readerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// optimizing the index with the writer
name|writerB
operator|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// building the searchables
name|Searcher
index|[]
name|searchers3
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
name|searchers3
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|)
expr_stmt|;
name|searchers3
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|)
expr_stmt|;
comment|// creating the mulitSearcher
name|Searcher
name|mSearcher3
init|=
name|getMultiSearcherInstance
argument_list|(
name|searchers3
argument_list|)
decl_stmt|;
comment|// performing the same search
name|Hits
name|hits3
init|=
name|mSearcher3
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|hits3
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterating over the hit documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits3
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|d
init|=
name|hits3
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
name|mSearcher3
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStoreA
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStoreB
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createDocument
specifier|private
specifier|static
name|Document
name|createDocument
parameter_list|(
name|String
name|contents1
parameter_list|,
name|String
name|contents2
parameter_list|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|contents1
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"other"
argument_list|,
literal|"other contents"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contents2
operator|!=
literal|null
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"contents"
argument_list|,
name|contents2
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|document
return|;
block|}
DECL|method|initIndex
specifier|private
specifier|static
name|void
name|initIndex
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|int
name|nDocs
parameter_list|,
name|boolean
name|create
parameter_list|,
name|String
name|contents2
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|indexWriter
init|=
literal|null
decl_stmt|;
try|try
block|{
name|indexWriter
operator|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|,
name|create
argument_list|)
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
name|nDocs
condition|;
name|i
operator|++
control|)
block|{
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|createDocument
argument_list|(
literal|"doc"
operator|+
name|i
argument_list|,
name|contents2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|indexWriter
operator|!=
literal|null
condition|)
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testFieldSelector
specifier|public
name|void
name|testFieldSelector
parameter_list|()
throws|throws
name|Exception
block|{
name|RAMDirectory
name|ramDirectory1
decl_stmt|,
name|ramDirectory2
decl_stmt|;
name|IndexSearcher
name|indexSearcher1
decl_stmt|,
name|indexSearcher2
decl_stmt|;
name|ramDirectory1
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|ramDirectory2
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"doc0"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Now put the documents in a different index
name|initIndex
argument_list|(
name|ramDirectory1
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// documents with a single token "doc0", "doc1", etc...
name|initIndex
argument_list|(
name|ramDirectory2
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// documents with two tokens "doc0" and "x", "doc1" and x, etc...
name|indexSearcher1
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ramDirectory1
argument_list|)
expr_stmt|;
name|indexSearcher2
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ramDirectory2
argument_list|)
expr_stmt|;
name|MultiSearcher
name|searcher
init|=
name|getMultiSearcherInstance
argument_list|(
operator|new
name|Searcher
index|[]
block|{
name|indexSearcher1
block|,
name|indexSearcher2
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"searcher is null and it shouldn't be"
argument_list|,
name|searcher
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"hits is null and it shouldn't be"
argument_list|,
name|hits
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hits
operator|.
name|length
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|Document
name|document
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|//Should be one document from each directory
comment|//they both have two fields, contents and other
name|Set
name|ftl
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|ftl
operator|.
name|add
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|SetBasedFieldSelector
name|fs
init|=
operator|new
name|SetBasedFieldSelector
argument_list|(
name|ftl
argument_list|,
name|Collections
operator|.
name|EMPTY_SET
argument_list|)
decl_stmt|;
name|document
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|id
argument_list|(
literal|0
argument_list|)
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document is null and it shouldn't be"
argument_list|,
name|document
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"document.getFields() Size: "
operator|+
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|document
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|document
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"value is not null and it should be"
argument_list|,
name|value
operator|==
literal|null
argument_list|)
expr_stmt|;
name|value
operator|=
name|document
operator|.
name|get
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value is null and it shouldn't be"
argument_list|,
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ftl
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ftl
operator|.
name|add
argument_list|(
literal|"contents"
argument_list|)
expr_stmt|;
name|fs
operator|=
operator|new
name|SetBasedFieldSelector
argument_list|(
name|ftl
argument_list|,
name|Collections
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
name|document
operator|=
name|searcher
operator|.
name|doc
argument_list|(
name|hits
operator|.
name|id
argument_list|(
literal|1
argument_list|)
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|value
operator|=
name|document
operator|.
name|get
argument_list|(
literal|"contents"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value is null and it shouldn't be"
argument_list|,
name|value
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|value
operator|=
name|document
operator|.
name|get
argument_list|(
literal|"other"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"value is not null and it should be"
argument_list|,
name|value
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* uncomment this when the highest score is always normalized to 1.0, even when it was< 1.0  public void testNormalization1() throws IOException {      testNormalization(1, "Using 1 document per index:");  }   */
DECL|method|testNormalization10
specifier|public
name|void
name|testNormalization10
parameter_list|()
throws|throws
name|IOException
block|{
name|testNormalization
argument_list|(
literal|10
argument_list|,
literal|"Using 10 documents per index:"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNormalization
specifier|private
name|void
name|testNormalization
parameter_list|(
name|int
name|nDocs
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"contents"
argument_list|,
literal|"doc0"
argument_list|)
argument_list|)
decl_stmt|;
name|RAMDirectory
name|ramDirectory1
decl_stmt|;
name|IndexSearcher
name|indexSearcher1
decl_stmt|;
name|Hits
name|hits
decl_stmt|;
name|ramDirectory1
operator|=
operator|new
name|MockRAMDirectory
argument_list|()
expr_stmt|;
comment|// First put the documents in the same index
name|initIndex
argument_list|(
name|ramDirectory1
argument_list|,
name|nDocs
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// documents with a single token "doc0", "doc1", etc...
name|initIndex
argument_list|(
name|ramDirectory1
argument_list|,
name|nDocs
argument_list|,
literal|false
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// documents with two tokens "doc0" and "x", "doc1" and x, etc...
name|indexSearcher1
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ramDirectory1
argument_list|)
expr_stmt|;
name|hits
operator|=
name|indexSearcher1
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// hits.score(0) is 0.594535 if only a single document is in first index
comment|// Store the scores for use later
name|float
index|[]
name|scores
init|=
block|{
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
block|,
name|hits
operator|.
name|score
argument_list|(
literal|1
argument_list|)
block|}
decl_stmt|;
name|assertTrue
argument_list|(
name|message
argument_list|,
name|scores
index|[
literal|0
index|]
operator|>
name|scores
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|indexSearcher1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDirectory1
operator|.
name|close
argument_list|()
expr_stmt|;
name|hits
operator|=
literal|null
expr_stmt|;
name|RAMDirectory
name|ramDirectory2
decl_stmt|;
name|IndexSearcher
name|indexSearcher2
decl_stmt|;
name|ramDirectory1
operator|=
operator|new
name|MockRAMDirectory
argument_list|()
expr_stmt|;
name|ramDirectory2
operator|=
operator|new
name|MockRAMDirectory
argument_list|()
expr_stmt|;
comment|// Now put the documents in a different index
name|initIndex
argument_list|(
name|ramDirectory1
argument_list|,
name|nDocs
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// documents with a single token "doc0", "doc1", etc...
name|initIndex
argument_list|(
name|ramDirectory2
argument_list|,
name|nDocs
argument_list|,
literal|true
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
comment|// documents with two tokens "doc0" and "x", "doc1" and x, etc...
name|indexSearcher1
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ramDirectory1
argument_list|)
expr_stmt|;
name|indexSearcher2
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|ramDirectory2
argument_list|)
expr_stmt|;
name|Searcher
name|searcher
init|=
name|getMultiSearcherInstance
argument_list|(
operator|new
name|Searcher
index|[]
block|{
name|indexSearcher1
block|,
name|indexSearcher2
block|}
argument_list|)
decl_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// The scores should be the same (within reason)
name|assertEquals
argument_list|(
name|message
argument_list|,
name|scores
index|[
literal|0
index|]
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// This will a document from ramDirectory1
name|assertEquals
argument_list|(
name|message
argument_list|,
name|scores
index|[
literal|1
index|]
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// This will a document from ramDirectory2
comment|// Adding a Sort.RELEVANCE object should not change anything
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Sort
operator|.
name|RELEVANCE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|scores
index|[
literal|0
index|]
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// This will a document from ramDirectory1
name|assertEquals
argument_list|(
name|message
argument_list|,
name|scores
index|[
literal|1
index|]
argument_list|,
name|hits
operator|.
name|score
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|1e-6
argument_list|)
expr_stmt|;
comment|// This will a document from ramDirectory2
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDirectory1
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDirectory2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

