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
name|queryParser
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
name|spans
operator|.
name|SpanNearQuery
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
name|spans
operator|.
name|SpanQuery
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
name|spans
operator|.
name|SpanTermQuery
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

begin_comment
comment|/**  * TestExplanations subclass focusing on basic query types  */
end_comment

begin_class
DECL|class|TestSimpleExplanations
specifier|public
class|class
name|TestSimpleExplanations
extends|extends
name|TestExplanations
block|{
comment|// we focus on queries that don't rewrite to other queries.
comment|// if we get those covered well, then the ones that rewrite should
comment|// also be covered.
comment|/* simple term tests */
DECL|method|testT1
specifier|public
name|void
name|testT1
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"w1"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testT2
specifier|public
name|void
name|testT2
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"w1^1000"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* MatchAllDocs */
DECL|method|testMA1
specifier|public
name|void
name|testMA1
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMA2
specifier|public
name|void
name|testMA2
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|MatchAllDocsQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some simple phrase tests */
DECL|method|testP1
specifier|public
name|void
name|testP1
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w1 w2\""
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testP2
specifier|public
name|void
name|testP2
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w1 w3\""
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testP3
specifier|public
name|void
name|testP3
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w1 w2\"~1"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testP4
specifier|public
name|void
name|testP4
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w2 w3\"~1"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testP5
specifier|public
name|void
name|testP5
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w3 w2\"~1"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testP6
specifier|public
name|void
name|testP6
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w3 w2\"~2"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testP7
specifier|public
name|void
name|testP7
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"\"w3 w2\"~3"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some simple filtered query tests */
DECL|method|testFQ1
specifier|public
name|void
name|testFQ1
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1"
argument_list|)
argument_list|,
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFQ2
specifier|public
name|void
name|testFQ2
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1"
argument_list|)
argument_list|,
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFQ3
specifier|public
name|void
name|testFQ3
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx"
argument_list|)
argument_list|,
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFQ4
specifier|public
name|void
name|testFQ4
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
operator|new
name|FilteredQuery
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx^1000"
argument_list|)
argument_list|,
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testFQ6
specifier|public
name|void
name|testFQ6
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|FilteredQuery
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx"
argument_list|)
argument_list|,
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* ConstantScoreQueries */
DECL|method|testCSQ1
specifier|public
name|void
name|testCSQ1
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCSQ2
specifier|public
name|void
name|testCSQ2
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testCSQ3
specifier|public
name|void
name|testCSQ3
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
operator|new
name|ItemizedFilter
argument_list|(
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* DisjunctionMaxQuery */
DECL|method|testDMQ1
specifier|public
name|void
name|testDMQ1
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.0f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w5"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ2
specifier|public
name|void
name|testDMQ2
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w5"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ3
specifier|public
name|void
name|testDMQ3
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"QQ"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w5"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ4
specifier|public
name|void
name|testDMQ4
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"QQ"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ5
specifier|public
name|void
name|testDMQ5
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"yy -QQ"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ6
specifier|public
name|void
name|testDMQ6
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"-yy w3"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ7
specifier|public
name|void
name|testDMQ7
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"-yy w3"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w2"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ8
specifier|public
name|void
name|testDMQ8
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"yy w5^100"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx^100000"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testDMQ9
specifier|public
name|void
name|testDMQ9
parameter_list|()
throws|throws
name|Exception
block|{
name|DisjunctionMaxQuery
name|q
init|=
operator|new
name|DisjunctionMaxQuery
argument_list|(
literal|0.5f
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"yy w5^100"
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"xx^0"
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* MultiPhraseQuery */
DECL|method|testMPQ1
specifier|public
name|void
name|testMPQ1
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|,
literal|"w3"
block|,
literal|"xx"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMPQ2
specifier|public
name|void
name|testMPQ2
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|,
literal|"w3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMPQ3
specifier|public
name|void
name|testMPQ3
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|,
literal|"xx"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|,
literal|"w3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMPQ4
specifier|public
name|void
name|testMPQ4
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMPQ5
specifier|public
name|void
name|testMPQ5
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMPQ6
specifier|public
name|void
name|testMPQ6
parameter_list|()
throws|throws
name|Exception
block|{
name|MultiPhraseQuery
name|q
init|=
operator|new
name|MultiPhraseQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w1"
block|,
literal|"w3"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|ta
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"w2"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|q
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some simple tests of boolean queries containing term queries */
DECL|method|testBQ1
specifier|public
name|void
name|testBQ1
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"+w1 +w2"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ2
specifier|public
name|void
name|testBQ2
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"+yy +w3"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ3
specifier|public
name|void
name|testBQ3
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"yy +w3"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ4
specifier|public
name|void
name|testBQ4
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"w1 (-xx w2)"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ5
specifier|public
name|void
name|testBQ5
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"w1 (+qq w2)"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ6
specifier|public
name|void
name|testBQ6
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"w1 -(-qq w5)"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ7
specifier|public
name|void
name|testBQ7
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"+w1 +(qq (xx -w2) (+w3 +w4))"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ8
specifier|public
name|void
name|testBQ8
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"+w1 (qq (xx -w2) (+w3 +w4))"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ9
specifier|public
name|void
name|testBQ9
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"+w1 (qq (-xx w2) -(+w3 +w4))"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ10
specifier|public
name|void
name|testBQ10
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"+w1 +(qq (-xx w2) -(+w3 +w4))"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ11
specifier|public
name|void
name|testBQ11
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"w1 w2^1000.0"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ14
specifier|public
name|void
name|testBQ14
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"QQQQQ"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ15
specifier|public
name|void
name|testBQ15
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"QQQQQ"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ16
specifier|public
name|void
name|testBQ16
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"QQQQQ"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1 -xx"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ17
specifier|public
name|void
name|testBQ17
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w2"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w1 -xx"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ19
specifier|public
name|void
name|testBQ19
parameter_list|()
throws|throws
name|Exception
block|{
name|qtest
argument_list|(
literal|"-yy w3"
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testBQ20
specifier|public
name|void
name|testBQ20
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanQuery
name|q
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|q
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"QQQQQ"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"yy"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"zz"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w5"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|q
operator|.
name|add
argument_list|(
name|qp
operator|.
name|parse
argument_list|(
literal|"w4"
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testTermQueryMultiSearcherExplain
specifier|public
name|void
name|testTermQueryMultiSearcherExplain
parameter_list|()
throws|throws
name|Exception
block|{
comment|// creating two directories for indices
name|Directory
name|indexStoreA
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|indexStoreB
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Document
name|lDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1 2"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|lDoc2
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc2
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1 2"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|lDoc3
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|lDoc3
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"handle"
argument_list|,
literal|"1 2"
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
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriter
name|writerA
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreA
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|writerB
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexStoreB
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|addDocument
argument_list|(
name|lDoc2
argument_list|)
expr_stmt|;
name|writerA
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|writerA
operator|.
name|close
argument_list|()
expr_stmt|;
name|writerB
operator|.
name|addDocument
argument_list|(
name|lDoc3
argument_list|)
expr_stmt|;
name|writerB
operator|.
name|close
argument_list|()
expr_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
literal|"fulltext"
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"handle:1"
argument_list|)
decl_stmt|;
name|Searcher
index|[]
name|searchers
init|=
operator|new
name|Searcher
index|[
literal|2
index|]
decl_stmt|;
name|searchers
index|[
literal|0
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreB
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searchers
index|[
literal|1
index|]
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexStoreA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Searcher
name|mSearcher
init|=
operator|new
name|MultiSearcher
argument_list|(
name|searchers
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|hits
init|=
name|mSearcher
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
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|Explanation
name|explain
init|=
name|mSearcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|String
name|exp
init|=
name|explain
operator|.
name|toString
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"maxDocs=3"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"docFreq=3"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"handle:\"1 2\""
argument_list|)
expr_stmt|;
name|hits
operator|=
name|mSearcher
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
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|explain
operator|=
name|mSearcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|exp
operator|=
name|explain
operator|.
name|toString
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"1=3"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"2=3"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"handle"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"handle"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|hits
operator|=
name|mSearcher
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
literal|3
argument_list|,
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
name|explain
operator|=
name|mSearcher
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|hits
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|exp
operator|=
name|explain
operator|.
name|toString
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"1=3"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|exp
argument_list|,
name|exp
operator|.
name|indexOf
argument_list|(
literal|"2=3"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
name|mSearcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStoreA
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexStoreB
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

