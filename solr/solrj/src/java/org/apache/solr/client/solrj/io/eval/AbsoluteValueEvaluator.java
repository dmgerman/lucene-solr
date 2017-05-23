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
name|math
operator|.
name|BigDecimal
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
name|StreamFactory
import|;
end_import

begin_class
DECL|class|AbsoluteValueEvaluator
specifier|public
class|class
name|AbsoluteValueEvaluator
extends|extends
name|NumberEvaluator
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
DECL|method|AbsoluteValueEvaluator
specifier|public
name|AbsoluteValueEvaluator
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
if|if
condition|(
literal|1
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
literal|"Invalid expression %s - expecting one value but found %d"
argument_list|,
name|expression
argument_list|,
name|subEvaluators
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|evaluate
specifier|public
name|Number
name|evaluate
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|results
init|=
name|evaluateAll
argument_list|(
name|tuple
argument_list|)
decl_stmt|;
comment|// we're still doing these checks because if we ever add an array-flatten evaluator,
comment|// one found in the constructor could become != 1
if|if
condition|(
literal|1
operator|!=
name|results
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
literal|"%s(...) only works with a 1 value but %d were provided"
argument_list|,
name|constructingFactory
operator|.
name|getFunctionName
argument_list|(
name|getClass
argument_list|()
argument_list|)
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|null
operator|==
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|normalizeType
argument_list|(
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|abs
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

