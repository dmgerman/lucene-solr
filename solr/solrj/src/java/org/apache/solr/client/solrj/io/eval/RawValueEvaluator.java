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
DECL|class|RawValueEvaluator
specifier|public
class|class
name|RawValueEvaluator
extends|extends
name|SimpleEvaluator
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|value
specifier|private
name|Object
name|value
decl_stmt|;
DECL|method|RawValueEvaluator
specifier|public
name|RawValueEvaluator
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|init
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|RawValueEvaluator
specifier|public
name|RawValueEvaluator
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
name|StreamExpressionValue
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|parameters
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
literal|"Invalid expression %s - unknown operands found - expecting only raw values"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|!=
name|parameters
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
literal|"Invalid expression %s - only 1 value can exist in a %s(...) evaluator"
argument_list|,
name|expression
argument_list|,
name|factory
operator|.
name|getFunctionName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
name|init
argument_list|(
name|factory
operator|.
name|constructPrimitiveObject
argument_list|(
operator|(
operator|(
name|StreamExpressionValue
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
name|this
operator|.
name|value
operator|=
operator|(
name|Long
operator|)
name|value
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Float
condition|)
block|{
name|this
operator|.
name|value
operator|=
operator|(
operator|(
name|Float
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|evaluate
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
block|{
return|return
name|value
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
name|expression
operator|.
name|addParameter
argument_list|(
operator|new
name|StreamExpressionValue
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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

