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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|TokenStream
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
name|Tokenizer
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
name|TermAttribute
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_class
DECL|class|TestRangeQuery
specifier|public
class|class
name|TestRangeQuery
extends|extends
name|LuceneTestCase
block|{
DECL|field|docCount
specifier|private
name|int
name|docCount
init|=
literal|0
decl_stmt|;
DECL|field|dir
specifier|private
name|RAMDirectory
name|dir
decl_stmt|;
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
name|dir
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
block|}
DECL|method|testExclusive
specifier|public
name|void
name|testExclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,C,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C added, still only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//TODO: remove in Lucene 3.0
DECL|method|testDeprecatedCstrctors
specifier|public
name|void
name|testDeprecatedCstrctors
parameter_list|()
throws|throws
name|IOException
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|null
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,C,D, only B in range"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,C,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testInclusive
specifier|public
name|void
name|testInclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,C,D - A,B,C in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,D - A and B in range"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C added - A, B, C in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testEqualsHashcode
specifier|public
name|void
name|testEqualsHashcode
parameter_list|()
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|query
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|Query
name|other
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|other
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"query equals itself is true"
argument_list|,
name|query
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries are equal"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode must return same value when equals is true"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|other
operator|.
name|setBoost
argument_list|(
literal|2.0f
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different boost queries are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"notcontent"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different fields are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"X"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different lower terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"Z"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Different upper terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries with null lowerterms are equal()"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode must return same value when equals is true"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent queries with null upperterms are equal()"
argument_list|,
name|query
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hashcode returns same value"
argument_list|,
name|query
operator|.
name|hashCode
argument_list|()
argument_list|,
name|other
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|null
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"queries with different upper and lower terms are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|other
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"queries with different inclusive are not equal"
argument_list|,
name|query
operator|.
name|equals
argument_list|(
name|other
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testExclusiveCollating
specifier|public
name|void
name|testExclusiveCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Collator
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,C,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,D, only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C added, still only B in range"
argument_list|,
literal|1
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testInclusiveCollating
specifier|public
name|void
name|testInclusiveCollating
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"A"
argument_list|,
literal|"C"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|Collator
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,C,D - A,B,C in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|"D"
block|}
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A,B,D - A and B in range"
argument_list|,
literal|2
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
name|assertEquals
argument_list|(
literal|"C added - A, B, C in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testFarsi
specifier|public
name|void
name|testFarsi
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Neither Java 1.4.2 nor 1.5.0 has Farsi Locale collation available in
comment|// RuleBasedCollator.  However, the Arabic Locale seems to order the Farsi
comment|// characters properly.
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"ar"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"\u062F"
argument_list|,
literal|"\u0698"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|collator
argument_list|)
decl_stmt|;
comment|// Unicode order would include U+0633 in [ U+062F - U+0698 ], but Farsi
comment|// orders the U+0698 character before the U+0633 character, so the single
comment|// index Term below should NOT be returned by a RangeQuery with a Farsi
comment|// Collator (or an Arabic one for the case when Farsi is not supported).
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"\u0633\u0627\u0628"
block|}
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
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
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"\u0633"
argument_list|,
literal|"\u0638"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|collator
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
literal|null
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testDanish
specifier|public
name|void
name|testDanish
parameter_list|()
throws|throws
name|Exception
block|{
name|Collator
name|collator
init|=
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"da"
argument_list|,
literal|"dk"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Danish collation orders the words below in the given order (example taken
comment|// from TestSort.testInternationalSort() ).
name|String
index|[]
name|words
init|=
block|{
literal|"H\u00D8T"
block|,
literal|"H\u00C5T"
block|,
literal|"MAND"
block|}
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"H\u00D8T"
argument_list|,
literal|"MAND"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|collator
argument_list|)
decl_stmt|;
comment|// Unicode order would not include "H\u00C5T" in [ "H\u00D8T", "MAND" ],
comment|// but Danish collation does.
name|initializeIndex
argument_list|(
name|words
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
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
name|query
operator|=
operator|new
name|RangeQuery
argument_list|(
literal|"content"
argument_list|,
literal|"H\u00C5T"
argument_list|,
literal|"MAND"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|collator
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
literal|null
argument_list|,
literal|1000
argument_list|)
operator|.
name|scoreDocs
expr_stmt|;
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
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|SingleCharAnalyzer
specifier|private
specifier|static
class|class
name|SingleCharAnalyzer
extends|extends
name|Analyzer
block|{
DECL|class|SingleCharTokenizer
specifier|private
specifier|static
class|class
name|SingleCharTokenizer
extends|extends
name|Tokenizer
block|{
DECL|field|buffer
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1
index|]
decl_stmt|;
DECL|field|done
name|boolean
name|done
decl_stmt|;
DECL|field|termAtt
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|SingleCharTokenizer
specifier|public
name|SingleCharTokenizer
parameter_list|(
name|Reader
name|r
parameter_list|)
block|{
name|super
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|termAtt
operator|=
operator|(
name|TermAttribute
operator|)
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|count
init|=
name|input
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
if|if
condition|(
name|done
condition|)
return|return
literal|false
return|;
else|else
block|{
name|done
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|1
condition|)
block|{
name|termAtt
operator|.
name|termBuffer
argument_list|()
index|[
literal|0
index|]
operator|=
name|buffer
index|[
literal|0
index|]
expr_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
name|termAtt
operator|.
name|setTermLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|reset
specifier|public
specifier|final
name|void
name|reset
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|done
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|reusableTokenStream
specifier|public
name|TokenStream
name|reusableTokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Tokenizer
name|tokenizer
init|=
operator|(
name|Tokenizer
operator|)
name|getPreviousTokenStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenizer
operator|==
literal|null
condition|)
block|{
name|tokenizer
operator|=
operator|new
name|SingleCharTokenizer
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|setPreviousTokenStream
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
block|}
else|else
name|tokenizer
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|tokenizer
return|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|SingleCharTokenizer
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
DECL|method|initializeIndex
specifier|private
name|void
name|initializeIndex
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|initializeIndex
argument_list|(
name|values
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeIndex
specifier|private
name|void
name|initializeIndex
parameter_list|(
name|String
index|[]
name|values
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|insertDoc
argument_list|(
name|writer
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc
specifier|private
name|void
name|addDoc
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
literal|false
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|insertDoc
argument_list|(
name|writer
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|insertDoc
specifier|private
name|void
name|insertDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|content
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
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"id"
operator|+
name|docCount
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
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
name|content
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
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
name|docCount
operator|++
expr_stmt|;
block|}
comment|// LUCENE-38
DECL|method|testExclusiveLowerNull
specifier|public
name|void
name|testExclusiveLowerNull
parameter_list|()
throws|throws
name|Exception
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|SingleCharAnalyzer
argument_list|()
decl_stmt|;
comment|//http://issues.apache.org/jira/browse/LUCENE-38
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|null
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
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
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,C,D => A, B&<empty string> are in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert:
comment|//assertEquals("A,B,<empty string>,C,D => A, B&<empty string> are in range", 2, hits.length());
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,D => A, B&<empty string> are in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert:
comment|//assertEquals("A,B,<empty string>,D => A, B&<empty string> are in range", 2, hits.length());
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"C added, still A, B&<empty string> are in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("C added, still A, B&<empty string> are in range", 2, hits.length());
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// LUCENE-38
DECL|method|testInclusiveLowerNull
specifier|public
name|void
name|testInclusiveLowerNull
parameter_list|()
throws|throws
name|Exception
block|{
comment|//http://issues.apache.org/jira/browse/LUCENE-38
name|Analyzer
name|analyzer
init|=
operator|new
name|SingleCharAnalyzer
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|RangeQuery
argument_list|(
literal|null
argument_list|,
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"C"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"C"
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
decl_stmt|;
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
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,C,D => A,B,<empty string>,C in range"
argument_list|,
literal|4
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("A,B,<empty string>,C,D => A,B,<empty string>,C in range", 3, hits.length());
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|initializeIndex
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"A"
block|,
literal|"B"
block|,
literal|""
block|,
literal|"D"
block|}
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"A,B,<empty string>,D - A, B and<empty string> in range"
argument_list|,
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("A,B,<empty string>,D => A, B and<empty string> in range", 2, hits.length());
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|addDoc
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|)
expr_stmt|;
comment|// When Lucene-38 is fixed, use the assert on the next line:
name|assertEquals
argument_list|(
literal|"C added => A,B,<empty string>,C in range"
argument_list|,
literal|4
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// until Lucene-38 is fixed, use this assert
comment|//assertEquals("C added => A,B,<empty string>,C in range", 3, hits.length());
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

