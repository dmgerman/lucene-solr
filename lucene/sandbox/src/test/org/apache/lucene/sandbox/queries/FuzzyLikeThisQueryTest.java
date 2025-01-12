begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|LuceneTestCase
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
name|HashSet
import|;
end_import

begin_class
DECL|class|FuzzyLikeThisQueryTest
specifier|public
class|class
name|FuzzyLikeThisQueryTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
name|Directory
name|directory
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|analyzer
specifier|private
name|Analyzer
name|analyzer
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
name|analyzer
operator|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
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
name|analyzer
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//Add series of docs with misspelt names
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"jonathon smythe"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"jonathan smith"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"johnathon smyth"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"johnny smith"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"jonny smith"
argument_list|,
literal|"5"
argument_list|)
expr_stmt|;
name|addDoc
argument_list|(
name|writer
argument_list|,
literal|"johnathon smythe"
argument_list|,
literal|"6"
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
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|directory
argument_list|,
name|analyzer
argument_list|)
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
name|name
parameter_list|,
name|String
name|id
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
name|newTextField
argument_list|(
literal|"name"
argument_list|,
name|name
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
literal|"id"
argument_list|,
name|id
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
comment|//Tests that idf ranking is not favouring rare mis-spellings over a strong edit-distance match
DECL|method|testClosestEditDistanceMatchComesFirst
specifier|public
name|void
name|testClosestEditDistanceMatchComesFirst
parameter_list|()
throws|throws
name|Throwable
block|{
name|FuzzyLikeThisQuery
name|flt
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
literal|10
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|flt
operator|.
name|addTerms
argument_list|(
literal|"smith"
argument_list|,
literal|"name"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|flt
operator|.
name|rewrite
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|queryTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|createWeight
argument_list|(
name|q
argument_list|,
literal|true
argument_list|,
literal|1f
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|queryTerms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant smythe"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"smythe"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant smith"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant smyth"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"smyth"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|flt
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"score docs must match 1 doc"
argument_list|,
operator|(
name|sd
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|sd
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should match most similar not most rare variant"
argument_list|,
literal|"2"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Test multiple input words are having variants produced
DECL|method|testMultiWord
specifier|public
name|void
name|testMultiWord
parameter_list|()
throws|throws
name|Throwable
block|{
name|FuzzyLikeThisQuery
name|flt
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
literal|10
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|flt
operator|.
name|addTerms
argument_list|(
literal|"jonathin smoth"
argument_list|,
literal|"name"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|flt
operator|.
name|rewrite
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|queryTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|createWeight
argument_list|(
name|q
argument_list|,
literal|true
argument_list|,
literal|1f
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|queryTerms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant jonathan"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"jonathan"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant smith"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|flt
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"score docs must match 1 doc"
argument_list|,
operator|(
name|sd
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|sd
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should match most similar when using 2 words"
argument_list|,
literal|"2"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-4809
DECL|method|testNonExistingField
specifier|public
name|void
name|testNonExistingField
parameter_list|()
throws|throws
name|Throwable
block|{
name|FuzzyLikeThisQuery
name|flt
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
literal|10
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|flt
operator|.
name|addTerms
argument_list|(
literal|"jonathin smoth"
argument_list|,
literal|"name"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|flt
operator|.
name|addTerms
argument_list|(
literal|"jonathin smoth"
argument_list|,
literal|"this field does not exist"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// don't fail here just because the field doesn't exits
name|Query
name|q
init|=
name|flt
operator|.
name|rewrite
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|queryTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|createWeight
argument_list|(
name|q
argument_list|,
literal|true
argument_list|,
literal|1f
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|queryTerms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant jonathan"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"jonathan"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant smith"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|flt
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"score docs must match 1 doc"
argument_list|,
operator|(
name|sd
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|sd
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should match most similar when using 2 words"
argument_list|,
literal|"2"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Test bug found when first query word does not match anything
DECL|method|testNoMatchFirstWordBug
specifier|public
name|void
name|testNoMatchFirstWordBug
parameter_list|()
throws|throws
name|Throwable
block|{
name|FuzzyLikeThisQuery
name|flt
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
literal|10
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|flt
operator|.
name|addTerms
argument_list|(
literal|"fernando smith"
argument_list|,
literal|"name"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Query
name|q
init|=
name|flt
operator|.
name|rewrite
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Term
argument_list|>
name|queryTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|createWeight
argument_list|(
name|q
argument_list|,
literal|true
argument_list|,
literal|1f
argument_list|)
operator|.
name|extractTerms
argument_list|(
name|queryTerms
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have variant smith"
argument_list|,
name|queryTerms
operator|.
name|contains
argument_list|(
operator|new
name|Term
argument_list|(
literal|"name"
argument_list|,
literal|"smith"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|flt
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
name|assertTrue
argument_list|(
literal|"score docs must match 1 doc"
argument_list|,
operator|(
name|sd
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|sd
operator|.
name|length
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|searcher
operator|.
name|doc
argument_list|(
name|sd
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should match most similar when using 2 words"
argument_list|,
literal|"2"
argument_list|,
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFuzzyLikeThisQueryEquals
specifier|public
name|void
name|testFuzzyLikeThisQueryEquals
parameter_list|()
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|FuzzyLikeThisQuery
name|fltq1
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
literal|10
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|fltq1
operator|.
name|addTerms
argument_list|(
literal|"javi"
argument_list|,
literal|"subject"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FuzzyLikeThisQuery
name|fltq2
init|=
operator|new
name|FuzzyLikeThisQuery
argument_list|(
literal|10
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
name|fltq2
operator|.
name|addTerms
argument_list|(
literal|"javi"
argument_list|,
literal|"subject"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"FuzzyLikeThisQuery with same attributes is not equal"
argument_list|,
name|fltq1
argument_list|,
name|fltq2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

