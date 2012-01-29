begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.memory
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|memory
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|BaseTokenStreamTestCase
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
name|MockTokenFilter
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
name|codecs
operator|.
name|lucene40
operator|.
name|Lucene40PostingsFormat
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
name|index
operator|.
name|AtomicReader
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
name|DocsAndPositionsEnum
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
name|DocsEnum
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
name|TermsEnum
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
name|queryparser
operator|.
name|classic
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
name|_TestUtil
import|;
end_import

begin_comment
comment|/**  * Verifies that Lucene MemoryIndex and RAMDirectory have the same behaviour,  * returning the same results for queries on some randomish indexes.  */
end_comment

begin_class
DECL|class|MemoryIndexTest
specifier|public
class|class
name|MemoryIndexTest
extends|extends
name|BaseTokenStreamTestCase
block|{
DECL|field|queries
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|queries
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|ITERATIONS
specifier|public
specifier|static
specifier|final
name|int
name|ITERATIONS
init|=
literal|100
operator|*
name|RANDOM_MULTIPLIER
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
name|queries
operator|.
name|addAll
argument_list|(
name|readQueries
argument_list|(
literal|"testqueries.txt"
argument_list|)
argument_list|)
expr_stmt|;
name|queries
operator|.
name|addAll
argument_list|(
name|readQueries
argument_list|(
literal|"testqueries2.txt"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * read a set of queries from a resource file    */
DECL|method|readQueries
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|readQueries
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|queries
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|&&
operator|!
name|line
operator|.
name|startsWith
argument_list|(
literal|"//"
argument_list|)
condition|)
block|{
name|queries
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queries
return|;
block|}
comment|/**    * runs random tests, up to ITERATIONS times.    */
DECL|method|testRandomQueries
specifier|public
name|void
name|testRandomQueries
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ITERATIONS
condition|;
name|i
operator|++
control|)
name|assertAgainstRAMDirectory
argument_list|()
expr_stmt|;
block|}
comment|/**    * Build a randomish document for both RAMDirectory and MemoryIndex,    * and run all the queries against it.    */
DECL|method|assertAgainstRAMDirectory
specifier|public
name|void
name|assertAgainstRAMDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|fooField
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|termField
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// add up to 250 terms to field "foo"
specifier|final
name|int
name|numFooTerms
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|250
operator|*
name|RANDOM_MULTIPLIER
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
name|numFooTerms
condition|;
name|i
operator|++
control|)
block|{
name|fooField
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|fooField
operator|.
name|append
argument_list|(
name|randomTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// add up to 250 terms to field "term"
specifier|final
name|int
name|numTermTerms
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|250
operator|*
name|RANDOM_MULTIPLIER
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
name|numTermTerms
condition|;
name|i
operator|++
control|)
block|{
name|termField
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|termField
operator|.
name|append
argument_list|(
name|randomTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Directory
name|ramdir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|randomAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|ramdir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
name|analyzer
argument_list|)
operator|.
name|setCodec
argument_list|(
name|_TestUtil
operator|.
name|alwaysPostingsFormat
argument_list|(
operator|new
name|Lucene40PostingsFormat
argument_list|()
argument_list|)
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
name|Field
name|field1
init|=
name|newField
argument_list|(
literal|"foo"
argument_list|,
name|fooField
operator|.
name|toString
argument_list|()
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|Field
name|field2
init|=
name|newField
argument_list|(
literal|"term"
argument_list|,
name|termField
operator|.
name|toString
argument_list|()
argument_list|,
name|TextField
operator|.
name|TYPE_UNSTORED
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field2
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
name|MemoryIndex
name|memory
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|memory
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
name|fooField
operator|.
name|toString
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|memory
operator|.
name|addField
argument_list|(
literal|"term"
argument_list|,
name|termField
operator|.
name|toString
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|assertAllQueries
argument_list|(
name|memory
argument_list|,
name|ramdir
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|ramdir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Run all queries against both the RAMDirectory and MemoryIndex, ensuring they are the same.    */
DECL|method|assertAllQueries
specifier|public
name|void
name|assertAllQueries
parameter_list|(
name|MemoryIndex
name|memory
parameter_list|,
name|Directory
name|ramdir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|Exception
block|{
name|IndexReader
name|reader
init|=
name|IndexReader
operator|.
name|open
argument_list|(
name|ramdir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|ram
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|IndexSearcher
name|mem
init|=
name|memory
operator|.
name|createSearcher
argument_list|()
decl_stmt|;
name|QueryParser
name|qp
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"foo"
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|query
range|:
name|queries
control|)
block|{
name|TopDocs
name|ramDocs
init|=
name|ram
operator|.
name|search
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|TopDocs
name|memDocs
init|=
name|mem
operator|.
name|search
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
name|query
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|ramDocs
operator|.
name|totalHits
argument_list|,
name|memDocs
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Return a random analyzer (Simple, Stop, Standard) to analyze the terms.    */
DECL|method|randomAnalyzer
specifier|private
name|Analyzer
name|randomAnalyzer
parameter_list|()
block|{
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|SIMPLE
argument_list|,
literal|true
argument_list|,
name|MockTokenFilter
operator|.
name|ENGLISH_STOPSET
argument_list|,
literal|true
argument_list|)
return|;
default|default:
return|return
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
return|;
block|}
block|}
comment|/**    * Some terms to be indexed, in addition to random words.     * These terms are commonly used in the queries.     */
DECL|field|TEST_TERMS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|TEST_TERMS
init|=
block|{
literal|"term"
block|,
literal|"Term"
block|,
literal|"tErm"
block|,
literal|"TERM"
block|,
literal|"telm"
block|,
literal|"stop"
block|,
literal|"drop"
block|,
literal|"roll"
block|,
literal|"phrase"
block|,
literal|"a"
block|,
literal|"c"
block|,
literal|"bar"
block|,
literal|"blar"
block|,
literal|"gack"
block|,
literal|"weltbank"
block|,
literal|"worlbank"
block|,
literal|"hello"
block|,
literal|"on"
block|,
literal|"the"
block|,
literal|"apache"
block|,
literal|"Apache"
block|,
literal|"copyright"
block|,
literal|"Copyright"
block|}
decl_stmt|;
comment|/**    * half of the time, returns a random term from TEST_TERMS.    * the other half of the time, returns a random unicode string.    */
DECL|method|randomTerm
specifier|private
name|String
name|randomTerm
parameter_list|()
block|{
if|if
condition|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// return a random TEST_TERM
return|return
name|TEST_TERMS
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|TEST_TERMS
operator|.
name|length
argument_list|)
index|]
return|;
block|}
else|else
block|{
comment|// return a random unicode term
return|return
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
return|;
block|}
block|}
DECL|method|testDocsEnumStart
specifier|public
name|void
name|testDocsEnumStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|MemoryIndex
name|memory
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|memory
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|AtomicReader
name|reader
init|=
operator|(
name|AtomicReader
operator|)
name|memory
operator|.
name|createSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|DocsEnum
name|disi
init|=
name|_TestUtil
operator|.
name|docs
argument_list|(
name|random
argument_list|,
name|reader
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|docid
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// now reuse and check again
name|TermsEnum
name|te
init|=
name|reader
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|disi
operator|=
name|te
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|disi
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docid
operator|=
name|disi
operator|.
name|docID
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDocsAndPositionsEnumStart
specifier|public
name|void
name|testDocsAndPositionsEnumStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|MemoryIndex
name|memory
init|=
operator|new
name|MemoryIndex
argument_list|()
decl_stmt|;
name|memory
operator|.
name|addField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|AtomicReader
name|reader
init|=
operator|(
name|AtomicReader
operator|)
name|memory
operator|.
name|createSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|DocsAndPositionsEnum
name|disi
init|=
name|reader
operator|.
name|termPositionsEnum
argument_list|(
literal|null
argument_list|,
literal|"foo"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|docid
init|=
name|disi
operator|.
name|docID
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
comment|// now reuse and check again
name|TermsEnum
name|te
init|=
name|reader
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|te
operator|.
name|seekExact
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bar"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|disi
operator|=
name|te
operator|.
name|docsAndPositions
argument_list|(
literal|null
argument_list|,
name|disi
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|docid
operator|=
name|disi
operator|.
name|docID
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|docid
operator|==
operator|-
literal|1
operator|||
name|docid
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|disi
operator|.
name|nextDoc
argument_list|()
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

