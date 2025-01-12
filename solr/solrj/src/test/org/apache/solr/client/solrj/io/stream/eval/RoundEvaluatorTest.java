begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.eval
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|eval
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|RoundEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|StreamEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|RoundEvaluatorTest
specifier|public
class|class
name|RoundEvaluatorTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|factory
name|StreamFactory
name|factory
decl_stmt|;
DECL|field|values
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
decl_stmt|;
DECL|method|RoundEvaluatorTest
specifier|public
name|RoundEvaluatorTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withFunctionName
argument_list|(
literal|"round"
argument_list|,
name|RoundEvaluator
operator|.
name|class
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|test
specifier|private
name|void
name|test
parameter_list|(
name|Double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"round(a)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Object
name|result
init|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|value
condition|)
block|{
name|Assert
operator|.
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Long
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|oneField
specifier|public
name|void
name|oneField
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|90D
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|45.555555D
argument_list|)
expr_stmt|;
name|test
argument_list|(
literal|12.4D
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|-
literal|.4D
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|-
literal|0D
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|-
literal|0.0235D
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|-
literal|12.44444446D
argument_list|)
expr_stmt|;
name|test
argument_list|(
operator|-
literal|45.23D
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|noField
specifier|public
name|void
name|noField
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"round()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|twoFields
specifier|public
name|void
name|twoFields
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"round(a,b)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noValue
specifier|public
name|void
name|noValue
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"round(a)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Object
name|result
init|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nullValue
specifier|public
name|void
name|nullValue
parameter_list|()
throws|throws
name|Exception
block|{
name|test
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

