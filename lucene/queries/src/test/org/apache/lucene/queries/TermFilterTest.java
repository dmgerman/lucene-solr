begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queries
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queries
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AtomicReaderContext
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
name|SlowCompositeReaderWrapper
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
name|DocIdSet
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
name|Filter
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|_TestUtil
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

begin_class
DECL|class|TermFilterTest
specifier|public
class|class
name|TermFilterTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testCachability
specifier|public
name|void
name|testCachability
parameter_list|()
throws|throws
name|Exception
block|{
name|TermFilter
name|a
init|=
name|termFilter
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
decl_stmt|;
name|HashSet
argument_list|<
name|Filter
argument_list|>
name|cachedFilters
init|=
operator|new
name|HashSet
argument_list|<
name|Filter
argument_list|>
argument_list|()
decl_stmt|;
name|cachedFilters
operator|.
name|add
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Must be cached"
argument_list|,
name|cachedFilters
operator|.
name|contains
argument_list|(
name|termFilter
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Must not be cached"
argument_list|,
name|cachedFilters
operator|.
name|contains
argument_list|(
name|termFilter
argument_list|(
literal|"field1"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Must not be cached"
argument_list|,
name|cachedFilters
operator|.
name|contains
argument_list|(
name|termFilter
argument_list|(
literal|"field2"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingTermAndField
specifier|public
name|void
name|testMissingTermAndField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"field1"
decl_stmt|;
name|Directory
name|rd
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|rd
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
name|newStringField
argument_list|(
name|fieldName
argument_list|,
literal|"value1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
operator|new
name|SlowCompositeReaderWrapper
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
operator|instanceof
name|AtomicReaderContext
argument_list|)
expr_stmt|;
name|AtomicReaderContext
name|context
init|=
operator|(
name|AtomicReaderContext
operator|)
name|reader
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|DocIdSet
name|idSet
init|=
name|termFilter
argument_list|(
name|fieldName
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"must not be null"
argument_list|,
name|idSet
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iter
init|=
name|idSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iter
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
argument_list|)
expr_stmt|;
name|idSet
operator|=
name|termFilter
argument_list|(
name|fieldName
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"must be null"
argument_list|,
name|idSet
argument_list|)
expr_stmt|;
name|idSet
operator|=
name|termFilter
argument_list|(
literal|"field2"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"must be null"
argument_list|,
name|idSet
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field
init|=
literal|"field"
operator|+
name|i
decl_stmt|;
name|String
name|string
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|terms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|string
argument_list|)
argument_list|)
expr_stmt|;
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
name|field
argument_list|,
name|string
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
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
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|int
name|numQueries
init|=
name|atLeast
argument_list|(
literal|10
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|term
init|=
name|terms
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|num
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|queryResult
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|term
argument_list|)
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|MatchAllDocsQuery
name|matchAll
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
specifier|final
name|TermFilter
name|filter
init|=
name|termFilter
argument_list|(
name|term
argument_list|)
decl_stmt|;
name|TopDocs
name|filterResult
init|=
name|searcher
operator|.
name|search
argument_list|(
name|matchAll
argument_list|,
name|filter
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|filterResult
operator|.
name|totalHits
argument_list|,
name|queryResult
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|filterResult
operator|.
name|scoreDocs
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
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|,
name|queryResult
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|testHashCodeAndEquals
specifier|public
name|void
name|testHashCodeAndEquals
parameter_list|()
block|{
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|100
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|String
name|field1
init|=
literal|"field"
operator|+
name|i
decl_stmt|;
name|String
name|field2
init|=
literal|"field"
operator|+
name|i
operator|+
name|num
decl_stmt|;
name|String
name|value1
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|value2
init|=
name|_TestUtil
operator|.
name|randomRealisticUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
operator|+
literal|"x"
decl_stmt|;
comment|// this must be not equal to value1
name|TermFilter
name|filter1
init|=
name|termFilter
argument_list|(
name|field1
argument_list|,
name|value1
argument_list|)
decl_stmt|;
name|TermFilter
name|filter2
init|=
name|termFilter
argument_list|(
name|field1
argument_list|,
name|value2
argument_list|)
decl_stmt|;
name|TermFilter
name|filter3
init|=
name|termFilter
argument_list|(
name|field2
argument_list|,
name|value1
argument_list|)
decl_stmt|;
name|TermFilter
name|filter4
init|=
name|termFilter
argument_list|(
name|field2
argument_list|,
name|value2
argument_list|)
decl_stmt|;
name|TermFilter
index|[]
name|filters
init|=
operator|new
name|TermFilter
index|[]
block|{
name|filter1
block|,
name|filter2
block|,
name|filter3
block|,
name|filter4
block|}
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
name|filters
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|TermFilter
name|termFilter
init|=
name|filters
index|[
name|j
index|]
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|filters
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|TermFilter
name|otherTermFilter
init|=
name|filters
index|[
name|k
index|]
decl_stmt|;
if|if
condition|(
name|j
operator|==
name|k
condition|)
block|{
name|assertEquals
argument_list|(
name|termFilter
argument_list|,
name|otherTermFilter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|termFilter
operator|.
name|hashCode
argument_list|()
argument_list|,
name|otherTermFilter
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|termFilter
operator|.
name|equals
argument_list|(
name|otherTermFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|termFilter
operator|.
name|equals
argument_list|(
name|otherTermFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|TermFilter
name|filter5
init|=
name|termFilter
argument_list|(
name|field2
argument_list|,
name|value2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|filter5
argument_list|,
name|filter4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|filter5
operator|.
name|hashCode
argument_list|()
argument_list|,
name|filter4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter5
operator|.
name|equals
argument_list|(
name|filter4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|filter5
argument_list|,
name|filter4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter5
operator|.
name|equals
argument_list|(
name|filter4
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoTerms
specifier|public
name|void
name|testNoTerms
parameter_list|()
block|{
try|try
block|{
operator|new
name|TermFilter
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail - no term!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{}
try|try
block|{
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must fail - no field!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{}
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|TermFilter
name|termsFilter
init|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"field1:a"
argument_list|,
name|termsFilter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|termFilter
specifier|private
name|TermFilter
name|termFilter
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|termFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|termFilter
specifier|private
name|TermFilter
name|termFilter
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|new
name|TermFilter
argument_list|(
name|term
argument_list|)
return|;
block|}
block|}
end_class

end_unit

