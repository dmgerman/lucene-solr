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
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|StreamContext
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
name|StreamExpressionValue
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
DECL|class|ComplexEvaluator
specifier|public
specifier|abstract
class|class
name|ComplexEvaluator
implements|implements
name|StreamEvaluator
block|{
DECL|field|serialVersionUID
specifier|protected
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|streamContext
specifier|protected
name|StreamContext
name|streamContext
decl_stmt|;
DECL|field|nodeId
specifier|protected
name|UUID
name|nodeId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
DECL|field|constructingFactory
specifier|protected
name|StreamFactory
name|constructingFactory
decl_stmt|;
DECL|field|subEvaluators
specifier|protected
name|List
argument_list|<
name|StreamEvaluator
argument_list|>
name|subEvaluators
init|=
operator|new
name|ArrayList
argument_list|<
name|StreamEvaluator
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ComplexEvaluator
specifier|public
name|ComplexEvaluator
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
name|constructingFactory
operator|=
name|factory
expr_stmt|;
comment|// We have to do this because order of the parameters matter
name|List
argument_list|<
name|StreamExpressionParameter
argument_list|>
name|parameters
init|=
name|factory
operator|.
name|getOperandsOfType
argument_list|(
name|expression
argument_list|,
name|StreamExpressionParameter
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|StreamExpressionParameter
name|parameter
range|:
name|parameters
control|)
block|{
if|if
condition|(
name|parameter
operator|instanceof
name|StreamExpression
condition|)
block|{
comment|// possible evaluator
name|StreamExpression
name|streamExpression
init|=
operator|(
name|StreamExpression
operator|)
name|parameter
decl_stmt|;
if|if
condition|(
name|factory
operator|.
name|doesRepresentTypes
argument_list|(
name|streamExpression
argument_list|,
name|ComplexEvaluator
operator|.
name|class
argument_list|)
condition|)
block|{
name|subEvaluators
operator|.
name|add
argument_list|(
name|factory
operator|.
name|constructEvaluator
argument_list|(
name|streamExpression
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|factory
operator|.
name|doesRepresentTypes
argument_list|(
name|streamExpression
argument_list|,
name|SimpleEvaluator
operator|.
name|class
argument_list|)
condition|)
block|{
name|subEvaluators
operator|.
name|add
argument_list|(
name|factory
operator|.
name|constructEvaluator
argument_list|(
name|streamExpression
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Will be treated as a field name
name|subEvaluators
operator|.
name|add
argument_list|(
operator|new
name|FieldEvaluator
argument_list|(
name|streamExpression
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|parameter
operator|instanceof
name|StreamExpressionValue
condition|)
block|{
if|if
condition|(
literal|0
operator|!=
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|parameter
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// special case - if evaluates to a number, boolean, or null then we'll treat it
comment|// as a RawValueEvaluator
name|Object
name|value
init|=
name|factory
operator|.
name|constructPrimitiveObject
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|parameter
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|value
operator|||
name|value
operator|instanceof
name|Boolean
operator|||
name|value
operator|instanceof
name|Number
condition|)
block|{
name|subEvaluators
operator|.
name|add
argument_list|(
operator|new
name|RawValueEvaluator
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|subEvaluators
operator|.
name|add
argument_list|(
operator|new
name|FieldEvaluator
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
operator|!=
name|subEvaluators
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid expression %s - unknown operands found - expecting only StreamEvaluators or field names"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
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
for|for
control|(
name|StreamEvaluator
name|evaluator
range|:
name|subEvaluators
control|)
block|{
name|expression
operator|.
name|addParameter
argument_list|(
name|evaluator
operator|.
name|toExpression
argument_list|(
name|factory
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|streamContext
operator|=
name|context
expr_stmt|;
block|}
block|}
end_class

end_unit

