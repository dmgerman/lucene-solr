begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
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
name|HashMap
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
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
name|StorableField
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
name|StoredDocument
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
name|BooleanClause
operator|.
name|Occur
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
name|FilteredQuery
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
name|Query
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
name|TermRangeQuery
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
name|TestUtil
import|;
end_import

begin_comment
comment|/**  * Base test class for testing Unicode collation.  */
end_comment

begin_class
DECL|class|CollationTestBase
specifier|public
specifier|abstract
class|class
name|CollationTestBase
extends|extends
name|LuceneTestCase
block|{
DECL|field|firstRangeBeginningOriginal
specifier|protected
name|String
name|firstRangeBeginningOriginal
init|=
literal|"\u062F"
decl_stmt|;
DECL|field|firstRangeEndOriginal
specifier|protected
name|String
name|firstRangeEndOriginal
init|=
literal|"\u0698"
decl_stmt|;
DECL|field|secondRangeBeginningOriginal
specifier|protected
name|String
name|secondRangeBeginningOriginal
init|=
literal|"\u0633"
decl_stmt|;
DECL|field|secondRangeEndOriginal
specifier|protected
name|String
name|secondRangeEndOriginal
init|=
literal|"\u0638"
decl_stmt|;
DECL|method|testFarsiRangeFilterCollating
specifier|public
name|void
name|testFarsiRangeFilterCollating
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BytesRef
name|firstBeg
parameter_list|,
name|BytesRef
name|firstEnd
parameter_list|,
name|BytesRef
name|secondBeg
parameter_list|,
name|BytesRef
name|secondEnd
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
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
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633\u0627\u0628"
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
operator|new
name|StringField
argument_list|(
literal|"body"
argument_list|,
literal|"body"
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
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
literal|"body"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a TermRangeFilter with a Farsi
comment|// Collator (or an Arabic one for the case when Farsi searcher not
comment|// supported).
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|firstBeg
argument_list|,
name|firstEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should not be included."
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|bq
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|secondBeg
argument_list|,
name|secondEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|,
name|Occur
operator|.
name|FILTER
argument_list|)
expr_stmt|;
name|result
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|bq
argument_list|,
literal|1
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should be included."
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
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
DECL|method|testFarsiRangeQueryCollating
specifier|public
name|void
name|testFarsiRangeQueryCollating
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BytesRef
name|firstBeg
parameter_list|,
name|BytesRef
name|firstEnd
parameter_list|,
name|BytesRef
name|secondBeg
parameter_list|,
name|BytesRef
name|secondEnd
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a TermRangeQuery with a Farsi
comment|// Collator (or an Arabic one for the case when Farsi is not supported).
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633\u0627\u0628"
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|firstBeg
argument_list|,
name|firstEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should not be included."
argument_list|,
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|secondBeg
argument_list|,
name|secondEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should be included."
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
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
DECL|method|testFarsiTermRangeQuery
specifier|public
name|void
name|testFarsiTermRangeQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|BytesRef
name|firstBeg
parameter_list|,
name|BytesRef
name|firstEnd
parameter_list|,
name|BytesRef
name|secondBeg
parameter_list|,
name|BytesRef
name|secondEnd
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|farsiIndex
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|farsiIndex
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
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
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633\u0627\u0628"
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
operator|new
name|StringField
argument_list|(
literal|"body"
argument_list|,
literal|"body"
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
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|farsiIndex
argument_list|)
decl_stmt|;
name|IndexSearcher
name|search
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a TermRangeQuery
comment|// with a Farsi Collator (or an Arabic one for the case when Farsi is
comment|// not supported).
name|Query
name|csrq
init|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|firstBeg
argument_list|,
name|firstEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|result
init|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should not be included."
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|csrq
operator|=
operator|new
name|TermRangeQuery
argument_list|(
literal|"content"
argument_list|,
name|secondBeg
argument_list|,
name|secondEnd
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|result
operator|=
name|search
operator|.
name|search
argument_list|(
name|csrq
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The index Term should be included."
argument_list|,
literal|1
argument_list|,
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|farsiIndex
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Make sure the documents returned by the search match the expected list
comment|// Copied from TestSort.java
DECL|method|assertMatches
specifier|private
name|void
name|assertMatches
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|Sort
name|sort
parameter_list|,
name|String
name|expectedResult
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreDoc
index|[]
name|result
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|1000
argument_list|,
name|sort
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|result
operator|.
name|length
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
name|n
condition|;
operator|++
name|i
control|)
block|{
name|StoredDocument
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|result
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|StorableField
index|[]
name|v
init|=
name|doc
operator|.
name|getFields
argument_list|(
literal|"tracer"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|v
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|v
index|[
name|j
index|]
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertThreadSafe
specifier|public
name|void
name|assertThreadSafe
parameter_list|(
specifier|final
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|numTestPoints
init|=
literal|100
decl_stmt|;
name|int
name|numThreads
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// create a map<String,SortKey> up front.
comment|// then with multiple threads, generate sort keys for all the keys in the map
comment|// and ensure they are the same as the ones we produced in serial fashion.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTestPoints
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"fake"
argument_list|,
name|term
argument_list|)
init|)
block|{
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
comment|// ensure we make a copy of the actual bytes too
name|map
operator|.
name|put
argument_list|(
name|term
argument_list|,
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bytes
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
name|Thread
name|threads
index|[]
init|=
operator|new
name|Thread
index|[
name|numThreads
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
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
try|try
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|BytesRef
argument_list|>
name|mapping
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|term
init|=
name|mapping
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|BytesRef
name|expected
init|=
name|mapping
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
init|(
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|"fake"
argument_list|,
name|term
argument_list|)
init|)
block|{
name|TermToBytesRefAttribute
name|termAtt
init|=
name|ts
operator|.
name|addAttribute
argument_list|(
name|TermToBytesRefAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|BytesRef
name|bytes
init|=
name|termAtt
operator|.
name|getBytesRef
argument_list|()
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|fillBytesRef
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ts
operator|.
name|incrementToken
argument_list|()
argument_list|)
expr_stmt|;
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
block|}
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
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
name|numThreads
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

