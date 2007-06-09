begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_class
DECL|class|Test03Distance
specifier|public
class|class
name|Test03Distance
extends|extends
name|TestCase
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
operator|new
name|TestSuite
argument_list|(
name|Test03Distance
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|verbose
name|boolean
name|verbose
init|=
literal|false
decl_stmt|;
DECL|field|maxBasicQueries
name|int
name|maxBasicQueries
init|=
literal|16
decl_stmt|;
DECL|field|exceptionQueries
name|String
index|[]
name|exceptionQueries
init|=
block|{
literal|"(aa and bb) w cc"
block|,
literal|"(aa or bb) w (cc and dd)"
block|,
literal|"(aa opt bb) w cc"
block|,
literal|"(aa not bb) w cc"
block|,
literal|"(aa or bb) w (bi:cc)"
block|,
literal|"(aa or bb) w bi:cc"
block|,
literal|"(aa or bi:bb) w cc"
block|,
literal|"(aa or (bi:bb)) w cc"
block|,
literal|"(aa or (bb and dd)) w cc"
block|}
decl_stmt|;
DECL|method|test00Exceptions
specifier|public
name|void
name|test00Exceptions
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|m
init|=
name|ExceptionQueryTst
operator|.
name|getFailQueries
argument_list|(
name|exceptionQueries
argument_list|,
name|verbose
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|fail
argument_list|(
literal|"No ParseException for:\n"
operator|+
name|m
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|fieldName
specifier|final
name|String
name|fieldName
init|=
literal|"bi"
decl_stmt|;
DECL|field|docs1
name|String
index|[]
name|docs1
init|=
block|{
literal|"word1 word2 word3"
block|,
literal|"word4 word5"
block|,
literal|"ord1 ord2 ord3"
block|,
literal|"orda1 orda2 orda3 word2 worda3"
block|,
literal|"a c e a b c"
block|}
decl_stmt|;
DECL|field|db1
name|SingleFieldTestDb
name|db1
init|=
operator|new
name|SingleFieldTestDb
argument_list|(
name|docs1
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
DECL|field|docs2
name|String
index|[]
name|docs2
init|=
block|{
literal|"w1 w2 w3 w4 w5"
block|,
literal|"w1 w3 w2 w3"
block|,
literal|""
block|}
decl_stmt|;
DECL|field|db2
name|SingleFieldTestDb
name|db2
init|=
operator|new
name|SingleFieldTestDb
argument_list|(
name|docs2
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
DECL|method|distanceTest1
specifier|public
name|void
name|distanceTest1
parameter_list|(
name|String
name|query
parameter_list|,
name|int
index|[]
name|expdnrs
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQueryTst
name|bqt
init|=
operator|new
name|BooleanQueryTst
argument_list|(
name|query
argument_list|,
name|expdnrs
argument_list|,
name|db1
argument_list|,
name|fieldName
argument_list|,
name|this
argument_list|,
operator|new
name|BasicQueryFactory
argument_list|(
name|maxBasicQueries
argument_list|)
argument_list|)
decl_stmt|;
name|bqt
operator|.
name|setVerbose
argument_list|(
name|verbose
argument_list|)
expr_stmt|;
name|bqt
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
DECL|method|distanceTest2
specifier|public
name|void
name|distanceTest2
parameter_list|(
name|String
name|query
parameter_list|,
name|int
index|[]
name|expdnrs
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanQueryTst
name|bqt
init|=
operator|new
name|BooleanQueryTst
argument_list|(
name|query
argument_list|,
name|expdnrs
argument_list|,
name|db2
argument_list|,
name|fieldName
argument_list|,
name|this
argument_list|,
operator|new
name|BasicQueryFactory
argument_list|(
name|maxBasicQueries
argument_list|)
argument_list|)
decl_stmt|;
name|bqt
operator|.
name|setVerbose
argument_list|(
name|verbose
argument_list|)
expr_stmt|;
name|bqt
operator|.
name|doTest
argument_list|()
expr_stmt|;
block|}
DECL|method|test0W01
specifier|public
name|void
name|test0W01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1 w word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0N01
specifier|public
name|void
name|test0N01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1 n word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0N01r
specifier|public
name|void
name|test0N01r
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* r reverse */
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 n word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0W02
specifier|public
name|void
name|test0W02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 w word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0W03
specifier|public
name|void
name|test0W03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 2W word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0N03
specifier|public
name|void
name|test0N03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 2N word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0N03r
specifier|public
name|void
name|test0N03r
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1 2N word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0W04
specifier|public
name|void
name|test0W04
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 3w word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0N04
specifier|public
name|void
name|test0N04
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 3n word1"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0N04r
specifier|public
name|void
name|test0N04r
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1 3n word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0W05
specifier|public
name|void
name|test0W05
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"orda1 w orda3"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test0W06
specifier|public
name|void
name|test0W06
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"orda1 2w orda3"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc01
specifier|public
name|void
name|test1Wtrunc01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1* w word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc02
specifier|public
name|void
name|test1Wtrunc02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word* w word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc02r
specifier|public
name|void
name|test1Wtrunc02r
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 w word*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc02
specifier|public
name|void
name|test1Ntrunc02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word* n word2"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc02r
specifier|public
name|void
name|test1Ntrunc02r
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2 n word*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc03
specifier|public
name|void
name|test1Wtrunc03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1* w word2*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc03
specifier|public
name|void
name|test1Ntrunc03
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word1* N word2*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc04
specifier|public
name|void
name|test1Wtrunc04
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"kxork* w kxor*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc04
specifier|public
name|void
name|test1Ntrunc04
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"kxork* 99n kxor*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc05
specifier|public
name|void
name|test1Wtrunc05
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2* 2W word1*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc05
specifier|public
name|void
name|test1Ntrunc05
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word2* 2N word1*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc06
specifier|public
name|void
name|test1Wtrunc06
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"ord* W word*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc06
specifier|public
name|void
name|test1Ntrunc06
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"ord* N word*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc06r
specifier|public
name|void
name|test1Ntrunc06r
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"word* N ord*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc07
specifier|public
name|void
name|test1Wtrunc07
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"(orda2 OR orda3) W word*"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc08
specifier|public
name|void
name|test1Wtrunc08
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"(orda2 OR orda3) W (word2 OR worda3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Wtrunc09
specifier|public
name|void
name|test1Wtrunc09
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"(orda2 OR orda3) 2W (word2 OR worda3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test1Ntrunc09
specifier|public
name|void
name|test1Ntrunc09
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|3
block|}
decl_stmt|;
name|distanceTest1
argument_list|(
literal|"(orda2 OR orda3) 2N (word2 OR worda3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Wprefix01
specifier|public
name|void
name|test2Wprefix01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"W (w1, w2, w3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Nprefix01a
specifier|public
name|void
name|test2Nprefix01a
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"N(w1, w2, w3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Nprefix01b
specifier|public
name|void
name|test2Nprefix01b
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"N(w3, w1, w2)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Wprefix02
specifier|public
name|void
name|test2Wprefix02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"2W(w1,w2,w3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Nprefix02a
specifier|public
name|void
name|test2Nprefix02a
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"2N(w1,w2,w3)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Nprefix02b
specifier|public
name|void
name|test2Nprefix02b
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"2N(w2,w3,w1)"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Wnested01
specifier|public
name|void
name|test2Wnested01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"w1 W w2 W w3"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Nnested01
specifier|public
name|void
name|test2Nnested01
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"w1 N w2 N w3"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Wnested02
specifier|public
name|void
name|test2Wnested02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"w1 2W w2 2W w3"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
DECL|method|test2Nnested02
specifier|public
name|void
name|test2Nnested02
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|expdnrs
init|=
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
name|distanceTest2
argument_list|(
literal|"w1 2N w2 2N w3"
argument_list|,
name|expdnrs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

