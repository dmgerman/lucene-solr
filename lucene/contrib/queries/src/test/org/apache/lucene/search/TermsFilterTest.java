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
name|IndexReader
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
name|SlowMultiReaderWrapper
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
name|FixedBitSet
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
name|util
operator|.
name|HashSet
import|;
end_import

begin_class
DECL|class|TermsFilterTest
specifier|public
class|class
name|TermsFilterTest
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
name|TermsFilter
name|a
init|=
operator|new
name|TermsFilter
argument_list|()
decl_stmt|;
name|a
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|a
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
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
name|TermsFilter
name|b
init|=
operator|new
name|TermsFilter
argument_list|()
decl_stmt|;
name|b
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"b"
argument_list|)
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
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//duplicate term
name|assertTrue
argument_list|(
literal|"Must be cached"
argument_list|,
name|cachedFilters
operator|.
name|contains
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"c"
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
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingTerms
specifier|public
name|void
name|testMissingTerms
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
argument_list|,
name|rd
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
literal|100
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
name|int
name|term
init|=
name|i
operator|*
literal|10
decl_stmt|;
comment|//terms are units of 10;
name|doc
operator|.
name|add
argument_list|(
name|newField
argument_list|(
name|fieldName
argument_list|,
literal|""
operator|+
name|term
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
operator|new
name|SlowMultiReaderWrapper
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
name|getTopReaderContext
argument_list|()
operator|.
name|isAtomic
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
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|context
operator|.
name|isAtomic
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|TermsFilter
name|tf
init|=
operator|new
name|TermsFilter
argument_list|()
decl_stmt|;
name|tf
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"19"
argument_list|)
argument_list|)
expr_stmt|;
name|FixedBitSet
name|bits
init|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Must match nothing"
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|tf
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must match 1"
argument_list|,
literal|1
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|tf
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must match 2"
argument_list|,
literal|2
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
name|tf
operator|.
name|addTerm
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"00"
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Must match 2"
argument_list|,
literal|2
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
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
block|}
end_class

end_unit

