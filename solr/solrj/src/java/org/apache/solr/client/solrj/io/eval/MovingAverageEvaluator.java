begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.eval
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
name|ArrayList
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math3
operator|.
name|stat
operator|.
name|descriptive
operator|.
name|DescriptiveStatistics
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
name|stream
operator|.
name|expr
operator|.
name|Explanation
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
name|Explanation
operator|.
name|ExpressionType
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
name|Expressible
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
name|StreamExpression
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
name|StreamExpressionParameter
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

begin_class
DECL|class|MovingAverageEvaluator
specifier|public
class|class
name|MovingAverageEvaluator
extends|extends
name|ComplexEvaluator
implements|implements
name|Expressible
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|method|MovingAverageEvaluator
specifier|public
name|MovingAverageEvaluator
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|expression
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
DECL|method|evaluate
specifier|public
name|List
argument_list|<
name|Number
argument_list|>
name|evaluate
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|subEvaluators
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Moving average evaluator expects 2 parameters found: "
operator|+
name|subEvaluators
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
name|StreamEvaluator
name|colEval
init|=
name|subEvaluators
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StreamEvaluator
name|windowEval
init|=
name|subEvaluators
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|window
init|=
operator|(
operator|(
name|Number
operator|)
name|windowEval
operator|.
name|evaluate
argument_list|(
name|tuple
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Number
argument_list|>
name|numbers
init|=
operator|(
name|List
argument_list|<
name|Number
argument_list|>
operator|)
name|colEval
operator|.
name|evaluate
argument_list|(
name|tuple
argument_list|)
decl_stmt|;
if|if
condition|(
name|window
operator|>
name|numbers
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The window size cannot be larger then the array"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Number
argument_list|>
name|moving
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|DescriptiveStatistics
name|descriptiveStatistics
init|=
operator|new
name|DescriptiveStatistics
argument_list|(
name|window
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
name|numbers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|descriptiveStatistics
operator|.
name|addValue
argument_list|(
name|numbers
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|descriptiveStatistics
operator|.
name|getN
argument_list|()
operator|>=
name|window
condition|)
block|{
name|moving
operator|.
name|add
argument_list|(
name|descriptiveStatistics
operator|.
name|getMean
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|moving
return|;
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamExpression
name|expression
init|=
operator|new
name|StreamExpression
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|expression
return|;
block|}
annotation|@
name|Override
DECL|method|toExplanation
specifier|public
name|Explanation
name|toExplanation
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Explanation
argument_list|(
name|nodeId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withExpressionType
argument_list|(
name|ExpressionType
operator|.
name|EVALUATOR
argument_list|)
operator|.
name|withFunctionName
argument_list|(
name|factory
operator|.
name|getFunctionName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
operator|.
name|withImplementingClass
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|withExpression
argument_list|(
name|toExpression
argument_list|(
name|factory
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

