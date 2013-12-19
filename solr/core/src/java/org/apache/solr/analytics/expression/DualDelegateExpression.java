begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analytics.expression
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|expression
package|;
end_package

begin_comment
comment|/**  * Abstraction of an expression that applies a function to two delegate expressions.  */
end_comment

begin_class
DECL|class|DualDelegateExpression
specifier|public
specifier|abstract
class|class
name|DualDelegateExpression
extends|extends
name|Expression
block|{
DECL|field|a
specifier|protected
name|Expression
name|a
decl_stmt|;
DECL|field|b
specifier|protected
name|Expression
name|b
decl_stmt|;
DECL|method|DualDelegateExpression
specifier|public
name|DualDelegateExpression
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
name|this
operator|.
name|a
operator|=
name|a
expr_stmt|;
name|this
operator|.
name|b
operator|=
name|b
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/**  *<code>DivideExpression</code> returns the quotient of 'a' and 'b'.  */
end_comment

begin_class
DECL|class|DivideExpression
class|class
name|DivideExpression
extends|extends
name|DualDelegateExpression
block|{
comment|/**    * @param a numerator    * @param b divisor    */
DECL|method|DivideExpression
specifier|public
name|DivideExpression
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
name|Comparable
name|aComp
init|=
name|a
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Comparable
name|bComp
init|=
name|b
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|aComp
operator|==
literal|null
operator|||
name|bComp
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|double
name|div
init|=
operator|(
operator|(
name|Number
operator|)
name|aComp
operator|)
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|div
operator|=
name|div
operator|/
operator|(
operator|(
name|Number
operator|)
name|bComp
operator|)
operator|.
name|doubleValue
argument_list|()
expr_stmt|;
return|return
operator|new
name|Double
argument_list|(
name|div
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  *<code>PowerExpression</code> returns 'a' to the power of 'b'.  */
end_comment

begin_class
DECL|class|PowerExpression
class|class
name|PowerExpression
extends|extends
name|DualDelegateExpression
block|{
comment|/**    * @param a base    * @param b exponent    */
DECL|method|PowerExpression
specifier|public
name|PowerExpression
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
name|Comparable
name|aComp
init|=
name|a
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Comparable
name|bComp
init|=
name|b
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|aComp
operator|==
literal|null
operator|||
name|bComp
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|Double
argument_list|(
name|Math
operator|.
name|pow
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|aComp
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|,
operator|(
operator|(
name|Number
operator|)
name|bComp
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  *<code>LogExpression</code> returns the log of the delegate's value given a base number.  */
end_comment

begin_class
DECL|class|LogExpression
class|class
name|LogExpression
extends|extends
name|DualDelegateExpression
block|{
comment|/**    * @param a number    * @param b base    */
DECL|method|LogExpression
specifier|public
name|LogExpression
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
name|super
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Comparable
name|getValue
parameter_list|()
block|{
name|Comparable
name|aComp
init|=
name|a
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Comparable
name|bComp
init|=
name|b
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|aComp
operator|==
literal|null
operator|||
name|bComp
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|Math
operator|.
name|log
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|aComp
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
operator|/
name|Math
operator|.
name|log
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|bComp
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

