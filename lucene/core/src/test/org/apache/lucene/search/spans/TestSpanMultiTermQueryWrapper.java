begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|FuzzyQuery
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
name|PrefixQuery
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
name|RegexpQuery
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
name|WildcardQuery
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

begin_comment
comment|/**  * Tests for {@link SpanMultiTermQueryWrapper}, wrapping a few MultiTermQueries.  */
end_comment

begin_class
DECL|class|TestSpanMultiTermQueryWrapper
specifier|public
class|class
name|TestSpanMultiTermQueryWrapper
extends|extends
name|LuceneTestCase
block|{
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
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
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
name|field
init|=
name|newTextField
argument_list|(
literal|"field"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"quick brown fox"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"jumps over lazy broun dog"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|field
operator|.
name|setStringValue
argument_list|(
literal|"jumps over extremely very lazy broxn dog"
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|reader
operator|=
name|iw
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|iw
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
DECL|method|testWildcard
specifier|public
name|void
name|testWildcard
parameter_list|()
throws|throws
name|Exception
block|{
name|WildcardQuery
name|wq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"bro?n"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|swq
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|wq
argument_list|)
decl_stmt|;
comment|// will only match quick brown fox
name|SpanFirstQuery
name|sfq
init|=
operator|new
name|SpanFirstQuery
argument_list|(
name|swq
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|sfq
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrefix
specifier|public
name|void
name|testPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|WildcardQuery
name|wq
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"extrem*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|swq
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|wq
argument_list|)
decl_stmt|;
comment|// will only match "jumps over extremely very lazy broxn dog"
name|SpanFirstQuery
name|sfq
init|=
operator|new
name|SpanFirstQuery
argument_list|(
name|swq
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|sfq
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testFuzzy
specifier|public
name|void
name|testFuzzy
parameter_list|()
throws|throws
name|Exception
block|{
name|FuzzyQuery
name|fq
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"broan"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|sfq
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|fq
argument_list|)
decl_stmt|;
comment|// will not match quick brown fox
name|SpanPositionRangeQuery
name|sprq
init|=
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|sfq
argument_list|,
literal|3
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|sprq
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testFuzzy2
specifier|public
name|void
name|testFuzzy2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// maximum of 1 term expansion
name|FuzzyQuery
name|fq
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"broan"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
name|sfq
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|fq
argument_list|)
decl_stmt|;
comment|// will only match jumps over lazy broun dog
name|SpanPositionRangeQuery
name|sprq
init|=
operator|new
name|SpanPositionRangeQuery
argument_list|(
name|sfq
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|sprq
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoSuchMultiTermsInNear
specifier|public
name|void
name|testNoSuchMultiTermsInNear
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test to make sure non existent multiterms aren't throwing null pointer exceptions
name|FuzzyQuery
name|fuzzyNoSuch
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|fuzzyNoSuch
argument_list|)
decl_stmt|;
name|SpanQuery
name|term
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|near
init|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanNoSuch
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//flip order
name|near
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanNoSuch
block|,
name|term
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|WildcardQuery
name|wcNoSuch
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanWCNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|wcNoSuch
argument_list|)
decl_stmt|;
name|near
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanWCNoSuch
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|RegexpQuery
name|rgxNoSuch
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanRgxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|rgxNoSuch
argument_list|)
decl_stmt|;
name|near
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanRgxNoSuch
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|PrefixQuery
name|prfxNoSuch
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanPrfxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|prfxNoSuch
argument_list|)
decl_stmt|;
name|near
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanPrfxNoSuch
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//test single noSuch
name|near
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanPrfxNoSuch
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//test double noSuch
name|near
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanPrfxNoSuch
block|,
name|spanPrfxNoSuch
block|}
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoSuchMultiTermsInNotNear
specifier|public
name|void
name|testNoSuchMultiTermsInNotNear
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test to make sure non existent multiterms aren't throwing non-matching field exceptions
name|FuzzyQuery
name|fuzzyNoSuch
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|fuzzyNoSuch
argument_list|)
decl_stmt|;
name|SpanQuery
name|term
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanNotQuery
name|notNear
init|=
operator|new
name|SpanNotQuery
argument_list|(
name|term
argument_list|,
name|spanNoSuch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|notNear
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//flip
name|notNear
operator|=
operator|new
name|SpanNotQuery
argument_list|(
name|spanNoSuch
argument_list|,
name|term
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|notNear
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//both noSuch
name|notNear
operator|=
operator|new
name|SpanNotQuery
argument_list|(
name|spanNoSuch
argument_list|,
name|spanNoSuch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|notNear
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|WildcardQuery
name|wcNoSuch
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanWCNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|wcNoSuch
argument_list|)
decl_stmt|;
name|notNear
operator|=
operator|new
name|SpanNotQuery
argument_list|(
name|term
argument_list|,
name|spanWCNoSuch
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|notNear
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|RegexpQuery
name|rgxNoSuch
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanRgxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|rgxNoSuch
argument_list|)
decl_stmt|;
name|notNear
operator|=
operator|new
name|SpanNotQuery
argument_list|(
name|term
argument_list|,
name|spanRgxNoSuch
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|notNear
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|PrefixQuery
name|prfxNoSuch
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanPrfxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|prfxNoSuch
argument_list|)
decl_stmt|;
name|notNear
operator|=
operator|new
name|SpanNotQuery
argument_list|(
name|term
argument_list|,
name|spanPrfxNoSuch
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|notNear
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoSuchMultiTermsInOr
specifier|public
name|void
name|testNoSuchMultiTermsInOr
parameter_list|()
throws|throws
name|Exception
block|{
comment|//test to make sure non existent multiterms aren't throwing null pointer exceptions
name|FuzzyQuery
name|fuzzyNoSuch
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|fuzzyNoSuch
argument_list|)
decl_stmt|;
name|SpanQuery
name|term
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"brown"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|near
init|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanNoSuch
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
comment|//flip
name|near
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanNoSuch
block|,
name|term
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|WildcardQuery
name|wcNoSuch
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanWCNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|wcNoSuch
argument_list|)
decl_stmt|;
name|near
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanWCNoSuch
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|RegexpQuery
name|rgxNoSuch
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanRgxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|rgxNoSuch
argument_list|)
decl_stmt|;
name|near
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanRgxNoSuch
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|PrefixQuery
name|prfxNoSuch
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanPrfxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|prfxNoSuch
argument_list|)
decl_stmt|;
name|near
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|term
block|,
name|spanPrfxNoSuch
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|near
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanPrfxNoSuch
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|near
operator|=
operator|new
name|SpanOrQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
name|spanPrfxNoSuch
block|,
name|spanPrfxNoSuch
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|near
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoSuchMultiTermsInSpanFirst
specifier|public
name|void
name|testNoSuchMultiTermsInSpanFirst
parameter_list|()
throws|throws
name|Exception
block|{
comment|//this hasn't been a problem
name|FuzzyQuery
name|fuzzyNoSuch
init|=
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|fuzzyNoSuch
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanFirst
init|=
operator|new
name|SpanFirstQuery
argument_list|(
name|spanNoSuch
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|spanFirst
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|WildcardQuery
name|wcNoSuch
init|=
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch*"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanWCNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|wcNoSuch
argument_list|)
decl_stmt|;
name|spanFirst
operator|=
operator|new
name|SpanFirstQuery
argument_list|(
name|spanWCNoSuch
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|spanFirst
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|RegexpQuery
name|rgxNoSuch
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanRgxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|rgxNoSuch
argument_list|)
decl_stmt|;
name|spanFirst
operator|=
operator|new
name|SpanFirstQuery
argument_list|(
name|spanRgxNoSuch
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|spanFirst
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|PrefixQuery
name|prfxNoSuch
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"noSuch"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanQuery
name|spanPrfxNoSuch
init|=
operator|new
name|SpanMultiTermQueryWrapper
argument_list|<>
argument_list|(
name|prfxNoSuch
argument_list|)
decl_stmt|;
name|spanFirst
operator|=
operator|new
name|SpanFirstQuery
argument_list|(
name|spanPrfxNoSuch
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
name|spanFirst
argument_list|,
literal|10
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

