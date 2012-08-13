begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.sandbox.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|sandbox
operator|.
name|queries
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
name|HashSet
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
name|DocIdSetIterator
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
name|ScoreDoc
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
DECL|class|DuplicateFilterTest
specifier|public
class|class
name|DuplicateFilterTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|KEY_FIELD
specifier|private
specifier|static
specifier|final
name|String
name|KEY_FIELD
init|=
literal|"url"
decl_stmt|;
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|tq
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"text"
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
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
name|directory
operator|=
name|newDirectory
argument_list|()
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
name|directory
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
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//Add series of docs with filterable fields : url, text and dates  flags
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"lucene 1.4.3 available"
argument_list|,
literal|"20040101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"New release pending"
argument_list|,
literal|"20040102"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"Lucene 1.9 out now"
argument_list|,
literal|"20050101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://www.bar.com"
argument_list|,
literal|"Local man bites dog"
argument_list|,
literal|"20040101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://www.bar.com"
argument_list|,
literal|"Dog bites local man"
argument_list|,
literal|"20040102"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://www.bar.com"
argument_list|,
literal|"Dog uses Lucene"
argument_list|,
literal|"20050101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"Lucene 2.0 out"
argument_list|,
literal|"20050101"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"http://lucene.apache.org"
argument_list|,
literal|"Oops. Lucene 2.1 out"
argument_list|,
literal|"20050102"
argument_list|)
expr_stmt|;
comment|// Until we fix LUCENE-2348, the index must
comment|// have only 1 segment:
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|RandomIndexWriter
name|writer
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|text
parameter_list|,
name|String
name|date
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
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
name|KEY_FIELD
argument_list|,
name|url
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"text"
argument_list|,
name|text
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
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
DECL|method|testDefaultFilter
specifier|public
name|void
name|testDefaultFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
control|)
block|{
name|StoredDocument
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No duplicate urls should be returned"
argument_list|,
name|results
operator|.
name|contains
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoFilter
specifier|public
name|void
name|testNoFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Default searching should have found some matches"
argument_list|,
name|hits
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|dupsFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
control|)
block|{
name|StoredDocument
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dupsFound
condition|)
name|dupsFound
operator|=
name|results
operator|.
name|contains
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Default searching should have found duplicate urls"
argument_list|,
name|dupsFound
argument_list|)
expr_stmt|;
block|}
DECL|method|testFastFilter
specifier|public
name|void
name|testFastFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|df
operator|.
name|setProcessingMode
argument_list|(
name|DuplicateFilter
operator|.
name|ProcessingMode
operator|.
name|PM_FAST_INVALIDATION
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filtered searching should have found some matches"
argument_list|,
name|hits
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
control|)
block|{
name|StoredDocument
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"No duplicate urls should be returned"
argument_list|,
name|results
operator|.
name|contains
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Two urls found"
argument_list|,
literal|2
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testKeepsLastFilter
specifier|public
name|void
name|testKeepsLastFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|df
operator|.
name|setKeepMode
argument_list|(
name|DuplicateFilter
operator|.
name|KeepMode
operator|.
name|KM_USE_LAST_OCCURRENCE
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filtered searching should have found some matches"
argument_list|,
name|hits
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
control|)
block|{
name|StoredDocument
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|DocsEnum
name|td
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|KEY_FIELD
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|url
argument_list|)
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|td
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
block|{
name|lastDoc
operator|=
name|td
operator|.
name|docID
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Duplicate urls should return last doc"
argument_list|,
name|lastDoc
argument_list|,
name|hit
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testKeepsFirstFilter
specifier|public
name|void
name|testKeepsFirstFilter
parameter_list|()
throws|throws
name|Throwable
block|{
name|DuplicateFilter
name|df
init|=
operator|new
name|DuplicateFilter
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|df
operator|.
name|setKeepMode
argument_list|(
name|DuplicateFilter
operator|.
name|KeepMode
operator|.
name|KM_USE_FIRST_OCCURRENCE
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|tq
argument_list|,
name|df
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Filtered searching should have found some matches"
argument_list|,
name|hits
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|hit
range|:
name|hits
control|)
block|{
name|StoredDocument
name|d
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|hit
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|url
init|=
name|d
operator|.
name|get
argument_list|(
name|KEY_FIELD
argument_list|)
decl_stmt|;
name|DocsEnum
name|td
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|()
argument_list|,
name|reader
argument_list|,
name|KEY_FIELD
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|url
argument_list|)
argument_list|,
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|lastDoc
init|=
literal|0
decl_stmt|;
name|td
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|lastDoc
operator|=
name|td
operator|.
name|docID
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Duplicate urls should return first doc"
argument_list|,
name|lastDoc
argument_list|,
name|hit
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

