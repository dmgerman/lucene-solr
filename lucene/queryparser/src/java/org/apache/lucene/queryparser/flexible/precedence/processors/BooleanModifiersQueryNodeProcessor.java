begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.precedence.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|precedence
operator|.
name|processors
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|precedence
operator|.
name|PrecedenceQueryParser
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|StandardQueryConfigHandler
operator|.
name|Operator
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|*
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|ModifierQueryNode
operator|.
name|Modifier
import|;
end_import

begin_comment
comment|/**  *<p>  * This processor is used to apply the correct {@link ModifierQueryNode} to {@link BooleanQueryNode}s children.  *</p>  *<p>  * It walks through the query node tree looking for {@link BooleanQueryNode}s. If an {@link AndQueryNode} is found,  * every child, which is not a {@link ModifierQueryNode} or the {@link ModifierQueryNode}   * is {@link Modifier#MOD_NONE}, becomes a {@link Modifier#MOD_REQ}. For any other  * {@link BooleanQueryNode} which is not an {@link OrQueryNode}, it checks the default operator is {@link Operator#AND},  * if it is, the same operation when an {@link AndQueryNode} is found is applied to it.  *</p>  *   * @see ConfigurationKeys#DEFAULT_OPERATOR  * @see PrecedenceQueryParser#setDefaultOperator  */
end_comment

begin_class
DECL|class|BooleanModifiersQueryNodeProcessor
specifier|public
class|class
name|BooleanModifiersQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|field|childrenBuffer
specifier|private
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
name|childrenBuffer
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|usingAnd
specifier|private
name|Boolean
name|usingAnd
init|=
literal|false
decl_stmt|;
DECL|method|BooleanModifiersQueryNodeProcessor
specifier|public
name|BooleanModifiersQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|QueryNode
name|process
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|Operator
name|op
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|DEFAULT_OPERATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR should be set on the QueryConfigHandler"
argument_list|)
throw|;
block|}
name|this
operator|.
name|usingAnd
operator|=
name|StandardQueryConfigHandler
operator|.
name|Operator
operator|.
name|AND
operator|==
name|op
expr_stmt|;
return|return
name|super
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|AndQueryNode
condition|)
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|add
argument_list|(
name|applyModifier
argument_list|(
name|child
argument_list|,
name|ModifierQueryNode
operator|.
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|set
argument_list|(
name|this
operator|.
name|childrenBuffer
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|usingAnd
operator|&&
name|node
operator|instanceof
name|BooleanQueryNode
operator|&&
operator|!
operator|(
name|node
operator|instanceof
name|OrQueryNode
operator|)
condition|)
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|add
argument_list|(
name|applyModifier
argument_list|(
name|child
argument_list|,
name|ModifierQueryNode
operator|.
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|node
operator|.
name|set
argument_list|(
name|this
operator|.
name|childrenBuffer
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
DECL|method|applyModifier
specifier|private
name|QueryNode
name|applyModifier
parameter_list|(
name|QueryNode
name|node
parameter_list|,
name|ModifierQueryNode
operator|.
name|Modifier
name|mod
parameter_list|)
block|{
comment|// check if modifier is not already defined and is default
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|ModifierQueryNode
operator|)
condition|)
block|{
return|return
operator|new
name|ModifierQueryNode
argument_list|(
name|node
argument_list|,
name|mod
argument_list|)
return|;
block|}
else|else
block|{
name|ModifierQueryNode
name|modNode
init|=
operator|(
name|ModifierQueryNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|modNode
operator|.
name|getModifier
argument_list|()
operator|==
name|ModifierQueryNode
operator|.
name|Modifier
operator|.
name|MOD_NONE
condition|)
block|{
return|return
operator|new
name|ModifierQueryNode
argument_list|(
name|modNode
operator|.
name|getChild
argument_list|()
argument_list|,
name|mod
argument_list|)
return|;
block|}
block|}
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit

