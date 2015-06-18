begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|vectorhighlight
operator|.
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
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
name|vectorhighlight
operator|.
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
operator|.
name|Toffs
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
name|vectorhighlight
operator|.
name|FieldTermStack
operator|.
name|TermInfo
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

begin_class
DECL|class|FieldPhraseListTest
specifier|public
class|class
name|FieldPhraseListTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|test1TermIndex
specifier|public
name|void
name|test1TermIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(1.0)((0,1))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fq
operator|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|stack
operator|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
expr_stmt|;
name|fpl
operator|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test2TermsIndex
specifier|public
name|void
name|test2TermsIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"a a"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(1.0)((0,1))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(1.0)((2,3))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseIndex
specifier|public
name|void
name|test1PhraseIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"a b"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(1.0)((0,3))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|fq
operator|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|stack
operator|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
expr_stmt|;
name|fpl
operator|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b(1.0)((2,3))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseIndexB
specifier|public
name|void
name|test1PhraseIndexB
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 01 12 23 34 45 56 67 78 (offsets)
comment|// bb|bb|ba|ac|cb|ba|ab|bc
comment|//  0  1  2  3  4  5  6  7 (positions)
name|make1d1fIndexB
argument_list|(
literal|"bbbacbabc"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"ba"
argument_list|,
literal|"ac"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"baac(1.0)((2,5))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test2ConcatTermsIndexB
specifier|public
name|void
name|test2ConcatTermsIndexB
parameter_list|()
throws|throws
name|Exception
block|{
comment|// 01 12 23 (offsets)
comment|// ab|ba|ab
comment|//  0  1  2 (positions)
name|make1d1fIndexB
argument_list|(
literal|"abab"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(1.0)((0,2))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(1.0)((2,4))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Terms1PhraseIndex
specifier|public
name|void
name|test2Terms1PhraseIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"c a a b"
argument_list|)
expr_stmt|;
comment|// phraseHighlight = true
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(1.0)((4,7))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// phraseHighlight = false
name|fq
operator|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|stack
operator|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
expr_stmt|;
name|fpl
operator|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a(1.0)((2,3))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(1.0)((4,7))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPhraseSlop
specifier|public
name|void
name|testPhraseSlop
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"c a a b c"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|2F
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ac(2.0)((4,5)(8,9))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStartOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEndOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test2PhrasesOverlap
specifier|public
name|void
name|test2PhrasesOverlap
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"d a b c d"
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc(1.0)((2,7))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test3TermsPhrase
specifier|public
name|void
name|test3TermsPhrase
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"d a b a b c d"
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc(1.0)((6,11))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSearchLongestPhrase
specifier|public
name|void
name|testSearchLongestPhrase
parameter_list|()
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
literal|"d a b d c a b c"
argument_list|)
expr_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|query
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|pqF
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ab(1.0)((2,5))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc(1.0)((10,15))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseShortMV
specifier|public
name|void
name|test1PhraseShortMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexShortMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|tq
argument_list|(
literal|"d"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"d(1.0)((9,10))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseLongMV
specifier|public
name|void
name|test1PhraseLongMV
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMV
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"search"
argument_list|,
literal|"engines"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"searchengines(1.0)((102,116))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"searchengines(1.0)((157,171))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|test1PhraseLongMVB
specifier|public
name|void
name|test1PhraseLongMVB
parameter_list|()
throws|throws
name|Exception
block|{
name|makeIndexLongMVB
argument_list|()
expr_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|pqF
argument_list|(
literal|"sp"
argument_list|,
literal|"pe"
argument_list|,
literal|"ee"
argument_list|,
literal|"ed"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// "speed" -(2gram)-> "sp","pe","ee","ed"
name|FieldTermStack
name|stack
init|=
operator|new
name|FieldTermStack
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
name|F
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|FieldPhraseList
name|fpl
init|=
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"sppeeeed(1.0)((88,93))"
argument_list|,
name|fpl
operator|.
name|phraseList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* This test shows a big speedup from limiting the number of analyzed phrases in     * this bad case for FieldPhraseList */
comment|/* But it is not reliable as a unit test since it is timing-dependent   public void testManyRepeatedTerms() throws Exception {       long t = System.currentTimeMillis();       testManyTermsWithLimit (-1);       long t1 = System.currentTimeMillis();       testManyTermsWithLimit (1);       long t2 = System.currentTimeMillis();       assertTrue (t2-t1 * 1000< t1-t);   }   private void testManyTermsWithLimit (int limit) throws Exception {       StringBuilder buf = new StringBuilder ();       for (int i = 0; i< 16000; i++) {           buf.append("a b c ");       }       make1d1fIndex( buf.toString());        Query query = tq("a");       FieldQuery fq = new FieldQuery( query, true, true );       FieldTermStack stack = new FieldTermStack( reader, 0, F, fq );       FieldPhraseList fpl = new FieldPhraseList( stack, fq, limit);       if (limit< 0 || limit> 16000)           assertEquals( 16000, fpl.phraseList.size() );       else           assertEquals( limit, fpl.phraseList.size() );       assertEquals( "a(1.0)((0,1))", fpl.phraseList.get( 0 ).toString() );         }   */
DECL|method|testWeightedPhraseInfoComparisonConsistency
specifier|public
name|void
name|testWeightedPhraseInfoComparisonConsistency
parameter_list|()
block|{
name|WeightedPhraseInfo
name|a
init|=
name|newInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|WeightedPhraseInfo
name|b
init|=
name|newInfo
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|WeightedPhraseInfo
name|c
init|=
name|newInfo
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|WeightedPhraseInfo
name|d
init|=
name|newInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|WeightedPhraseInfo
name|e
init|=
name|newInfo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertConsistentEquals
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|b
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|d
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|a
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|b
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|e
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|e
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|testToffsComparisonConsistency
specifier|public
name|void
name|testToffsComparisonConsistency
parameter_list|()
block|{
name|Toffs
name|a
init|=
operator|new
name|Toffs
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Toffs
name|b
init|=
operator|new
name|Toffs
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|Toffs
name|c
init|=
operator|new
name|Toffs
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|Toffs
name|d
init|=
operator|new
name|Toffs
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertConsistentEquals
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|b
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|c
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|d
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertConsistentEquals
argument_list|(
name|a
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|b
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|a
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertConsistentLessThan
argument_list|(
name|d
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|newInfo
specifier|private
name|WeightedPhraseInfo
name|newInfo
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|TermInfo
argument_list|>
name|infos
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|infos
operator|.
name|add
argument_list|(
operator|new
name|TermInfo
argument_list|(
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
name|startOffset
argument_list|,
name|endOffset
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|WeightedPhraseInfo
argument_list|(
name|infos
argument_list|,
name|boost
argument_list|)
return|;
block|}
DECL|method|assertConsistentEquals
specifier|private
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|void
name|assertConsistentEquals
parameter_list|(
name|T
name|a
parameter_list|,
name|T
name|b
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|b
argument_list|,
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a
operator|.
name|hashCode
argument_list|()
argument_list|,
name|b
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b
operator|.
name|compareTo
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertConsistentLessThan
specifier|private
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|void
name|assertConsistentLessThan
parameter_list|(
name|T
name|a
parameter_list|,
name|T
name|b
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|equals
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|a
operator|.
name|hashCode
argument_list|()
operator|==
name|b
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|b
operator|.
name|compareTo
argument_list|(
name|a
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

