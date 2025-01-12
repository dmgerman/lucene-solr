begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|FilteredTermsEnum
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
name|MultiReader
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
name|index
operator|.
name|Terms
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
name|AttributeSource
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
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_class
DECL|class|TestMultiTermQueryRewrites
specifier|public
class|class
name|TestMultiTermQueryRewrites
extends|extends
name|LuceneTestCase
block|{
DECL|field|dir
DECL|field|sdir1
DECL|field|sdir2
specifier|static
name|Directory
name|dir
decl_stmt|,
name|sdir1
decl_stmt|,
name|sdir2
decl_stmt|;
DECL|field|reader
DECL|field|multiReader
DECL|field|multiReaderDupls
specifier|static
name|IndexReader
name|reader
decl_stmt|,
name|multiReader
decl_stmt|,
name|multiReaderDupls
decl_stmt|;
DECL|field|searcher
DECL|field|multiSearcher
DECL|field|multiSearcherDupls
specifier|static
name|IndexSearcher
name|searcher
decl_stmt|,
name|multiSearcher
decl_stmt|,
name|multiSearcherDupls
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|sdir1
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|sdir2
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|swriter1
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|sdir1
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RandomIndexWriter
name|swriter2
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|sdir2
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
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
literal|10
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
name|doc
operator|.
name|add
argument_list|(
name|newStringField
argument_list|(
literal|"data"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
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
operator|(
operator|(
name|i
operator|%
literal|2
operator|==
literal|0
operator|)
condition|?
name|swriter1
else|:
name|swriter2
operator|)
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|swriter1
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|swriter2
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|swriter1
operator|.
name|close
argument_list|()
expr_stmt|;
name|swriter2
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|multiReader
operator|=
operator|new
name|MultiReader
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|DirectoryReader
operator|.
name|open
argument_list|(
name|sdir1
argument_list|)
block|,
name|DirectoryReader
operator|.
name|open
argument_list|(
name|sdir2
argument_list|)
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|multiSearcher
operator|=
name|newSearcher
argument_list|(
name|multiReader
argument_list|)
expr_stmt|;
name|multiReaderDupls
operator|=
operator|new
name|MultiReader
argument_list|(
operator|new
name|IndexReader
index|[]
block|{
name|DirectoryReader
operator|.
name|open
argument_list|(
name|sdir1
argument_list|)
block|,
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|multiSearcherDupls
operator|=
name|newSearcher
argument_list|(
name|multiReaderDupls
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|multiReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|multiReaderDupls
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|sdir1
operator|.
name|close
argument_list|()
expr_stmt|;
name|sdir2
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|multiReader
operator|=
name|multiReaderDupls
operator|=
literal|null
expr_stmt|;
name|searcher
operator|=
name|multiSearcher
operator|=
name|multiSearcherDupls
operator|=
literal|null
expr_stmt|;
name|dir
operator|=
name|sdir1
operator|=
name|sdir2
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|extractInnerQuery
specifier|private
name|Query
name|extractInnerQuery
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
if|if
condition|(
name|q
operator|instanceof
name|ConstantScoreQuery
condition|)
block|{
comment|// wrapped as ConstantScoreQuery
name|q
operator|=
operator|(
operator|(
name|ConstantScoreQuery
operator|)
name|q
operator|)
operator|.
name|getQuery
argument_list|()
expr_stmt|;
block|}
return|return
name|q
return|;
block|}
DECL|method|extractTerm
specifier|private
name|Term
name|extractTerm
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|q
operator|=
name|extractInnerQuery
argument_list|(
name|q
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|TermQuery
operator|)
name|q
operator|)
operator|.
name|getTerm
argument_list|()
return|;
block|}
DECL|method|checkBooleanQueryOrder
specifier|private
name|void
name|checkBooleanQueryOrder
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
name|q
operator|=
name|extractInnerQuery
argument_list|(
name|q
argument_list|)
expr_stmt|;
specifier|final
name|BooleanQuery
name|bq
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|Term
name|last
init|=
literal|null
decl_stmt|,
name|act
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
operator|.
name|clauses
argument_list|()
control|)
block|{
name|act
operator|=
name|extractTerm
argument_list|(
name|clause
operator|.
name|getQuery
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
literal|"sort order of terms in BQ violated"
argument_list|,
name|last
operator|.
name|compareTo
argument_list|(
name|act
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
block|}
name|last
operator|=
name|act
expr_stmt|;
block|}
block|}
DECL|method|checkDuplicateTerms
specifier|private
name|void
name|checkDuplicateTerms
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|MultiTermQuery
name|mtq
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|,
literal|"7"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mtq
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|q1
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q2
init|=
name|multiSearcher
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q3
init|=
name|multiSearcherDupls
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"single segment: "
operator|+
name|q1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"multi segment: "
operator|+
name|q2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"multi segment with duplicates: "
operator|+
name|q3
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"The multi-segment case must produce same rewritten query"
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The multi-segment case with duplicates must produce same rewritten query"
argument_list|,
name|q1
argument_list|,
name|q3
argument_list|)
expr_stmt|;
name|checkBooleanQueryOrder
argument_list|(
name|q1
argument_list|)
expr_stmt|;
name|checkBooleanQueryOrder
argument_list|(
name|q2
argument_list|)
expr_stmt|;
name|checkBooleanQueryOrder
argument_list|(
name|q3
argument_list|)
expr_stmt|;
block|}
DECL|method|testRewritesWithDuplicateTerms
specifier|public
name|void
name|testRewritesWithDuplicateTerms
parameter_list|()
throws|throws
name|Exception
block|{
name|checkDuplicateTerms
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
name|checkDuplicateTerms
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
comment|// use a large PQ here to only test duplicate terms and dont mix up when all scores are equal
name|checkDuplicateTerms
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsScoringBooleanQueryRewrite
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|checkDuplicateTerms
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsBoostOnlyBooleanQueryRewrite
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkBooleanQueryBoosts
specifier|private
name|void
name|checkBooleanQueryBoosts
parameter_list|(
name|BooleanQuery
name|bq
parameter_list|)
block|{
for|for
control|(
name|BooleanClause
name|clause
range|:
name|bq
operator|.
name|clauses
argument_list|()
control|)
block|{
specifier|final
name|BoostQuery
name|boostQ
init|=
operator|(
name|BoostQuery
operator|)
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
specifier|final
name|TermQuery
name|mtq
init|=
operator|(
name|TermQuery
operator|)
name|boostQ
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Parallel sorting of boosts in rewrite mode broken"
argument_list|,
name|Float
operator|.
name|parseFloat
argument_list|(
name|mtq
operator|.
name|getTerm
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
argument_list|,
name|boostQ
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkBoosts
specifier|private
name|void
name|checkBoosts
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|MultiTermQuery
name|mtq
init|=
operator|new
name|MultiTermQuery
argument_list|(
literal|"data"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|TermsEnum
name|getTermsEnum
parameter_list|(
name|Terms
name|terms
parameter_list|,
name|AttributeSource
name|atts
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FilteredTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
block|{
specifier|final
name|BoostAttribute
name|boostAtt
init|=
name|attributes
argument_list|()
operator|.
name|addAttribute
argument_list|(
name|BoostAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|boostAtt
operator|.
name|setBoost
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|term
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
name|char
name|c
init|=
call|(
name|char
call|)
argument_list|(
name|term
operator|.
name|bytes
index|[
name|term
operator|.
name|offset
index|]
operator|&
literal|0xff
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|'2'
condition|)
block|{
if|if
condition|(
name|c
operator|<=
literal|'7'
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
else|else
block|{
return|return
name|AcceptStatus
operator|.
name|NO
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|"dummy"
return|;
block|}
block|}
decl_stmt|;
name|mtq
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|q1
init|=
name|searcher
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q2
init|=
name|multiSearcher
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
decl_stmt|;
specifier|final
name|Query
name|q3
init|=
name|multiSearcherDupls
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"single segment: "
operator|+
name|q1
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"multi segment: "
operator|+
name|q2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"multi segment with duplicates: "
operator|+
name|q3
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"The multi-segment case must produce same rewritten query"
argument_list|,
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The multi-segment case with duplicates must produce same rewritten query"
argument_list|,
name|q1
argument_list|,
name|q3
argument_list|)
expr_stmt|;
if|if
condition|(
name|q1
operator|instanceof
name|MatchNoDocsQuery
condition|)
block|{
name|assertTrue
argument_list|(
name|q1
operator|instanceof
name|MatchNoDocsQuery
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|q3
operator|instanceof
name|MatchNoDocsQuery
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkBooleanQueryBoosts
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|q1
argument_list|)
expr_stmt|;
name|checkBooleanQueryBoosts
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|q2
argument_list|)
expr_stmt|;
name|checkBooleanQueryBoosts
argument_list|(
operator|(
name|BooleanQuery
operator|)
name|q3
argument_list|)
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
block|}
DECL|method|testBoosts
specifier|public
name|void
name|testBoosts
parameter_list|()
throws|throws
name|Exception
block|{
name|checkBoosts
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
comment|// use a large PQ here to only test boosts and dont mix up when all scores are equal
name|checkBoosts
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsScoringBooleanQueryRewrite
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkMaxClauseLimitation
specifier|private
name|void
name|checkMaxClauseLimitation
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|savedMaxClauseCount
init|=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
specifier|final
name|MultiTermQuery
name|mtq
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|,
literal|"7"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mtq
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
try|try
block|{
name|BooleanQuery
operator|.
name|TooManyClauses
name|expected
init|=
name|expectThrows
argument_list|(
name|BooleanQuery
operator|.
name|TooManyClauses
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|multiSearcherDupls
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
comment|//  Maybe remove this assert in later versions, when internal API changes:
name|assertEquals
argument_list|(
literal|"Should throw BooleanQuery.TooManyClauses with a stacktrace containing checkMaxClauseCount()"
argument_list|,
literal|"checkMaxClauseCount"
argument_list|,
name|expected
operator|.
name|getStackTrace
argument_list|()
index|[
literal|0
index|]
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
name|savedMaxClauseCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkNoMaxClauseLimitation
specifier|private
name|void
name|checkNoMaxClauseLimitation
parameter_list|(
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|savedMaxClauseCount
init|=
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
decl_stmt|;
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
literal|3
argument_list|)
expr_stmt|;
specifier|final
name|MultiTermQuery
name|mtq
init|=
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
literal|"data"
argument_list|,
literal|"2"
argument_list|,
literal|"7"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mtq
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
try|try
block|{
name|multiSearcherDupls
operator|.
name|rewrite
argument_list|(
name|mtq
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|BooleanQuery
operator|.
name|setMaxClauseCount
argument_list|(
name|savedMaxClauseCount
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMaxClauseLimitations
specifier|public
name|void
name|testMaxClauseLimitations
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMaxClauseLimitation
argument_list|(
name|MultiTermQuery
operator|.
name|SCORING_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
name|checkMaxClauseLimitation
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
name|checkNoMaxClauseLimitation
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_REWRITE
argument_list|)
expr_stmt|;
name|checkNoMaxClauseLimitation
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsScoringBooleanQueryRewrite
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|checkNoMaxClauseLimitation
argument_list|(
operator|new
name|MultiTermQuery
operator|.
name|TopTermsBoostOnlyBooleanQueryRewrite
argument_list|(
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

