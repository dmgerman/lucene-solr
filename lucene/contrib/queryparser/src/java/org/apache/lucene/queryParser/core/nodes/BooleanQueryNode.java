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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import

begin_comment
comment|/**  * A {@link BooleanQueryNode} represents a list of elements which do not have an  * explicit boolean operator defined between them. It can be used to express a  * boolean query that intends to use the default boolean operator.  */
end_comment

begin_class
DECL|class|BooleanQueryNode
specifier|public
class|class
name|BooleanQueryNode
extends|extends
name|QueryNodeImpl
block|{
comment|/**    * @param clauses    *          - the query nodes to be and'ed    */
DECL|method|BooleanQueryNode
specifier|public
name|BooleanQueryNode
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|clauses
parameter_list|)
block|{
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|allocate
argument_list|()
expr_stmt|;
name|set
argument_list|(
name|clauses
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|getChildren
argument_list|()
operator|==
literal|null
operator|||
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|"<boolean operation='default'/>"
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<boolean operation='default'>"
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
name|sb
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
name|child
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n</boolean>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
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
if|if
condition|(
name|getChildren
argument_list|()
operator|==
literal|null
operator|||
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|filler
init|=
literal|""
decl_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|filler
argument_list|)
operator|.
name|append
argument_list|(
name|child
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
argument_list|)
expr_stmt|;
name|filler
operator|=
literal|" "
expr_stmt|;
block|}
comment|// in case is root or the parent is a group node avoid parenthesis
if|if
condition|(
operator|(
name|getParent
argument_list|()
operator|!=
literal|null
operator|&&
name|getParent
argument_list|()
operator|instanceof
name|GroupQueryNode
operator|)
operator|||
name|isRoot
argument_list|()
condition|)
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
else|else
return|return
literal|"( "
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|" )"
return|;
block|}
annotation|@
name|Override
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|BooleanQueryNode
name|clone
init|=
operator|(
name|BooleanQueryNode
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
block|}
end_class

end_unit

