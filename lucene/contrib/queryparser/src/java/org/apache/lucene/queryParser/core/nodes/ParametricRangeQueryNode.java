begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|nodes
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
operator|.
name|CompareOperator
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
name|core
operator|.
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link ParametricRangeQueryNode} represents LE, LT, GE, GT, EQ, NE query.  * Example: date>= "2009-10-10" OR price = 200  */
end_comment

begin_class
DECL|class|ParametricRangeQueryNode
specifier|public
class|class
name|ParametricRangeQueryNode
extends|extends
name|QueryNodeImpl
implements|implements
name|FieldableNode
block|{
DECL|method|ParametricRangeQueryNode
specifier|public
name|ParametricRangeQueryNode
parameter_list|(
name|ParametricQueryNode
name|lowerBound
parameter_list|,
name|ParametricQueryNode
name|upperBound
parameter_list|)
block|{
if|if
condition|(
name|upperBound
operator|.
name|getOperator
argument_list|()
operator|!=
name|CompareOperator
operator|.
name|LE
operator|&&
name|upperBound
operator|.
name|getOperator
argument_list|()
operator|!=
name|CompareOperator
operator|.
name|LT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"upper bound should have "
operator|+
name|CompareOperator
operator|.
name|LE
operator|+
literal|" or "
operator|+
name|CompareOperator
operator|.
name|LT
argument_list|)
throw|;
block|}
if|if
condition|(
name|lowerBound
operator|.
name|getOperator
argument_list|()
operator|!=
name|CompareOperator
operator|.
name|GE
operator|&&
name|lowerBound
operator|.
name|getOperator
argument_list|()
operator|!=
name|CompareOperator
operator|.
name|GT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lower bound should have "
operator|+
name|CompareOperator
operator|.
name|GE
operator|+
literal|" or "
operator|+
name|CompareOperator
operator|.
name|GT
argument_list|)
throw|;
block|}
if|if
condition|(
name|upperBound
operator|.
name|getField
argument_list|()
operator|!=
name|lowerBound
operator|.
name|getField
argument_list|()
operator|||
operator|(
name|upperBound
operator|.
name|getField
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|upperBound
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|lowerBound
operator|.
name|getField
argument_list|()
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lower and upper bounds should have the same field name!"
argument_list|)
throw|;
block|}
name|allocate
argument_list|()
expr_stmt|;
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|lowerBound
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|upperBound
argument_list|)
expr_stmt|;
block|}
DECL|method|getUpperBound
specifier|public
name|ParametricQueryNode
name|getUpperBound
parameter_list|()
block|{
return|return
operator|(
name|ParametricQueryNode
operator|)
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
return|;
block|}
DECL|method|getLowerBound
specifier|public
name|ParametricQueryNode
name|getLowerBound
parameter_list|()
block|{
return|return
operator|(
name|ParametricQueryNode
operator|)
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
block|{
return|return
name|getLowerBound
argument_list|()
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
operator|+
literal|" AND "
operator|+
name|getUpperBound
argument_list|()
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
return|;
block|}
DECL|method|getField
specifier|public
name|CharSequence
name|getField
parameter_list|()
block|{
return|return
name|getLowerBound
argument_list|()
operator|.
name|getField
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<parametricRange>\n\t"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getUpperBound
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getLowerBound
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</parametricRange>\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|ParametricRangeQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|ParametricRangeQueryNode
name|clone
init|=
operator|(
name|ParametricRangeQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
comment|// nothing to do here
return|return
name|clone
return|;
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|CharSequence
name|fieldName
parameter_list|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|FieldableNode
condition|)
block|{
operator|(
operator|(
name|FieldableNode
operator|)
name|child
operator|)
operator|.
name|setField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

