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
name|search
operator|.
name|Query
import|;
end_import

begin_class
DECL|class|SimpleFragListBuilderTest
specifier|public
class|class
name|SimpleFragListBuilderTest
extends|extends
name|AbstractTestCase
block|{
DECL|method|testNullFieldFragList
specifier|public
name|void
name|testNullFieldFragList
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"b c d"
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTooSmallFragSize
specifier|public
name|void
name|testTooSmallFragSize
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"b c d"
argument_list|)
argument_list|,
name|SimpleFragListBuilder
operator|.
name|MIN_FRAG_CHAR_SIZE
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"IllegalArgumentException must be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{     }
block|}
DECL|method|testSmallerFragSizeThanTermQuery
specifier|public
name|void
name|testSmallerFragSizeThanTermQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"abcdefghijklmnopqrs"
argument_list|,
literal|"abcdefghijklmnopqrs"
argument_list|)
argument_list|,
name|SimpleFragListBuilder
operator|.
name|MIN_FRAG_CHAR_SIZE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(abcdefghijklmnopqrs((0,19)))/1.0(0,19)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|testSmallerFragSizeThanPhraseQuery
specifier|public
name|void
name|testSmallerFragSizeThanPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"\"abcdefgh jklmnopqrs\""
argument_list|,
literal|"abcdefgh   jklmnopqrs"
argument_list|)
argument_list|,
name|SimpleFragListBuilder
operator|.
name|MIN_FRAG_CHAR_SIZE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ffl
operator|.
name|fragInfos
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
literal|"subInfos=(abcdefghjklmnopqrs((0,21)))/1.0(0,21)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|test1TermIndex
specifier|public
name|void
name|test1TermIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1)))/1.0(0,100)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|test2TermsIndex1Frag
specifier|public
name|void
name|test2TermsIndex1Frag
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a a"
argument_list|)
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1))a((2,3)))/2.0(0,100)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a b b b b b b b b a"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1))a((18,19)))/2.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"b b b b a b b b b a"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((8,9))a((18,19)))/2.0(2,22)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|test2TermsIndex2Frags
specifier|public
name|void
name|test2TermsIndex2Frags
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a b b b b b b b b b b b b b a"
argument_list|)
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1)))/1.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
literal|"subInfos=(a((28,29)))/1.0(22,42)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a b b b b b b b b b b b b a"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1)))/1.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
literal|"subInfos=(a((26,27)))/1.0(20,40)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a"
argument_list|,
literal|"a b b b b b b b b b a"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1)))/1.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
literal|"subInfos=(a((20,21)))/1.0(20,40)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|test2TermsQuery
specifier|public
name|void
name|test2TermsQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a b"
argument_list|,
literal|"c d e"
argument_list|)
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a b"
argument_list|,
literal|"d b c"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(b((2,3)))/1.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"a b"
argument_list|,
literal|"a b c"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(a((0,1))b((2,3)))/2.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|testPhraseQuery
specifier|public
name|void
name|testPhraseQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"\"a b\""
argument_list|,
literal|"c d e"
argument_list|)
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"\"a b\""
argument_list|,
literal|"a c b"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ffl
operator|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"\"a b\""
argument_list|,
literal|"a b c"
argument_list|)
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(ab((0,3)))/1.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|testPhraseQuerySlop
specifier|public
name|void
name|testPhraseQuerySlop
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|(
literal|"\"a b\"~1"
argument_list|,
literal|"a c b"
argument_list|)
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(ab((0,1)(4,5)))/1.0(0,20)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
DECL|method|fpl
specifier|private
name|FieldPhraseList
name|fpl
parameter_list|(
name|String
name|queryValue
parameter_list|,
name|String
name|indexValue
parameter_list|)
throws|throws
name|Exception
block|{
name|make1d1fIndex
argument_list|(
name|indexValue
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|paW
operator|.
name|parse
argument_list|(
name|queryValue
argument_list|)
decl_stmt|;
name|FieldQuery
name|fq
init|=
operator|new
name|FieldQuery
argument_list|(
name|query
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
return|return
operator|new
name|FieldPhraseList
argument_list|(
name|stack
argument_list|,
name|fq
argument_list|)
return|;
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
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(d((9,10)))/1.0(3,103)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(searchengines((102,116))searchengines((157,171)))/2.0(96,196)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
name|SimpleFragListBuilder
name|sflb
init|=
operator|new
name|SimpleFragListBuilder
argument_list|()
decl_stmt|;
name|FieldFragList
name|ffl
init|=
name|sflb
operator|.
name|createFieldFragList
argument_list|(
name|fpl
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ffl
operator|.
name|fragInfos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subInfos=(sppeeeed((88,93)))/1.0(82,182)"
argument_list|,
name|ffl
operator|.
name|fragInfos
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
block|}
end_class

end_unit

