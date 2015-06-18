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
name|queries
operator|.
name|function
operator|.
name|FunctionQuery
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|ConstValueSource
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
name|BaseExplanationTestCase
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
name|TermQuery
import|;
end_import

begin_class
DECL|class|TestCustomScoreExplanations
specifier|public
class|class
name|TestCustomScoreExplanations
extends|extends
name|BaseExplanationTestCase
block|{
DECL|method|testOneTerm
specifier|public
name|void
name|testOneTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|CustomScoreQuery
name|csq
init|=
operator|new
name|CustomScoreQuery
argument_list|(
name|q
argument_list|,
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|csq
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
DECL|method|testBoost
specifier|public
name|void
name|testBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|CustomScoreQuery
name|csq
init|=
operator|new
name|CustomScoreQuery
argument_list|(
name|q
argument_list|,
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|csq
operator|.
name|setBoost
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|csq
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
DECL|method|testTopLevelBoost
specifier|public
name|void
name|testTopLevelBoost
parameter_list|()
throws|throws
name|Exception
block|{
name|Query
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FIELD
argument_list|,
literal|"w1"
argument_list|)
argument_list|)
decl_stmt|;
name|CustomScoreQuery
name|csq
init|=
operator|new
name|CustomScoreQuery
argument_list|(
name|q
argument_list|,
operator|new
name|FunctionQuery
argument_list|(
operator|new
name|ConstValueSource
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bqB
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|bqB
operator|.
name|add
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|bqB
operator|.
name|add
argument_list|(
name|csq
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|BooleanQuery
name|bq
init|=
name|bqB
operator|.
name|build
argument_list|()
decl_stmt|;
name|bq
operator|.
name|setBoost
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|qtest
argument_list|(
name|bq
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
block|}
end_class

end_unit

