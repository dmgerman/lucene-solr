begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
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
name|standard
operator|.
name|processors
package|;
end_package

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
name|config
operator|.
name|QueryConfigHandler
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
name|AndQueryNode
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
name|BooleanQueryNode
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
name|QueryNode
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
name|QueryNodeProcessor
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
name|processors
operator|.
name|BooleanModifiersQueryNodeProcessor
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
name|standard
operator|.
name|nodes
operator|.
name|BooleanModifierNode
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
name|parser
operator|.
name|StandardSyntaxParser
import|;
end_import

begin_comment
comment|/**  *<p>  * This processor is used to apply the correct {@link ModifierQueryNode} to  * {@link BooleanQueryNode}s children. This is a variant of  * {@link BooleanModifiersQueryNodeProcessor} which ignores precedence.  *</p>  *<p>  * The {@link StandardSyntaxParser} knows the rules of precedence, but lucene  * does not. e.g.<code>(A AND B OR C AND D)</code> ist treated like  *<code>(+A +B +C +D)</code>.  *</p>  *<p>  * This processor walks through the query node tree looking for  * {@link BooleanQueryNode}s. If an {@link AndQueryNode} is found, every child,  * which is not a {@link ModifierQueryNode} or the {@link ModifierQueryNode} is  * {@link Modifier#MOD_NONE}, becomes a {@link Modifier#MOD_REQ}. For default  * {@link BooleanQueryNode}, it checks the default operator is  * {@link Operator#AND}, if it is, the same operation when an  * {@link AndQueryNode} is found is applied to it. Each {@link BooleanQueryNode}  * which direct parent is also a {@link BooleanQueryNode} is removed (to ignore  * the rules of precedence).  *</p>  *   * @see ConfigurationKeys#DEFAULT_OPERATOR  * @see BooleanModifiersQueryNodeProcessor  */
end_comment

begin_class
DECL|class|BooleanQuery2ModifierNodeProcessor
specifier|public
class|class
name|BooleanQuery2ModifierNodeProcessor
implements|implements
name|QueryNodeProcessor
block|{
DECL|field|TAG_REMOVE
specifier|final
specifier|static
name|String
name|TAG_REMOVE
init|=
literal|"remove"
decl_stmt|;
DECL|field|TAG_MODIFIER
specifier|final
specifier|static
name|String
name|TAG_MODIFIER
init|=
literal|"wrapWithModifier"
decl_stmt|;
DECL|field|TAG_BOOLEAN_ROOT
specifier|final
specifier|static
name|String
name|TAG_BOOLEAN_ROOT
init|=
literal|"booleanRoot"
decl_stmt|;
DECL|field|queryConfigHandler
name|QueryConfigHandler
name|queryConfigHandler
decl_stmt|;
DECL|field|childrenBuffer
specifier|private
specifier|final
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
DECL|method|BooleanQuery2ModifierNodeProcessor
specifier|public
name|BooleanQuery2ModifierNodeProcessor
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
name|processIteration
argument_list|(
name|queryTree
argument_list|)
return|;
block|}
DECL|method|processChildren
specifier|protected
name|void
name|processChildren
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|queryTree
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
name|children
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
name|child
operator|=
name|processIteration
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|processIteration
specifier|private
name|QueryNode
name|processIteration
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|queryTree
operator|=
name|preProcessNode
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
name|processChildren
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
name|queryTree
operator|=
name|postProcessNode
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
return|return
name|queryTree
return|;
block|}
DECL|method|fillChildrenBufferAndApplyModifiery
specifier|protected
name|void
name|fillChildrenBufferAndApplyModifiery
parameter_list|(
name|QueryNode
name|parent
parameter_list|)
block|{
for|for
control|(
name|QueryNode
name|node
range|:
name|parent
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|.
name|containsTag
argument_list|(
name|TAG_REMOVE
argument_list|)
condition|)
block|{
name|fillChildrenBufferAndApplyModifiery
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|containsTag
argument_list|(
name|TAG_MODIFIER
argument_list|)
condition|)
block|{
name|childrenBuffer
operator|.
name|add
argument_list|(
name|applyModifier
argument_list|(
name|node
argument_list|,
operator|(
name|Modifier
operator|)
name|node
operator|.
name|getTag
argument_list|(
name|TAG_MODIFIER
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childrenBuffer
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
operator|.
name|containsTag
argument_list|(
name|TAG_BOOLEAN_ROOT
argument_list|)
condition|)
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fillChildrenBufferAndApplyModifiery
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|.
name|set
argument_list|(
name|childrenBuffer
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
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
name|QueryNode
name|parent
init|=
name|node
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|BooleanQueryNode
condition|)
block|{
if|if
condition|(
name|parent
operator|instanceof
name|BooleanQueryNode
condition|)
block|{
name|node
operator|.
name|setTag
argument_list|(
name|TAG_REMOVE
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
comment|// no precedence
block|}
else|else
block|{
name|node
operator|.
name|setTag
argument_list|(
name|TAG_BOOLEAN_ROOT
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|parent
operator|instanceof
name|BooleanQueryNode
condition|)
block|{
if|if
condition|(
operator|(
name|parent
operator|instanceof
name|AndQueryNode
operator|)
operator|||
operator|(
name|usingAnd
operator|&&
name|isDefaultBooleanQueryNode
argument_list|(
name|parent
argument_list|)
operator|)
condition|)
block|{
name|tagModifierButDoNotOverride
argument_list|(
name|node
argument_list|,
name|ModifierQueryNode
operator|.
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|node
return|;
block|}
DECL|method|isDefaultBooleanQueryNode
specifier|protected
name|boolean
name|isDefaultBooleanQueryNode
parameter_list|(
name|QueryNode
name|toTest
parameter_list|)
block|{
return|return
name|toTest
operator|!=
literal|null
operator|&&
name|BooleanQueryNode
operator|.
name|class
operator|.
name|equals
argument_list|(
name|toTest
operator|.
name|getClass
argument_list|()
argument_list|)
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
name|BooleanModifierNode
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
DECL|method|tagModifierButDoNotOverride
specifier|protected
name|void
name|tagModifierButDoNotOverride
parameter_list|(
name|QueryNode
name|node
parameter_list|,
name|Modifier
name|mod
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|ModifierQueryNode
condition|)
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
name|Modifier
operator|.
name|MOD_NONE
condition|)
block|{
name|node
operator|.
name|setTag
argument_list|(
name|TAG_MODIFIER
argument_list|,
name|mod
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|node
operator|.
name|setTag
argument_list|(
name|TAG_MODIFIER
argument_list|,
name|ModifierQueryNode
operator|.
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setQueryConfigHandler
specifier|public
name|void
name|setQueryConfigHandler
parameter_list|(
name|QueryConfigHandler
name|queryConfigHandler
parameter_list|)
block|{
name|this
operator|.
name|queryConfigHandler
operator|=
name|queryConfigHandler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getQueryConfigHandler
specifier|public
name|QueryConfigHandler
name|getQueryConfigHandler
parameter_list|()
block|{
return|return
name|queryConfigHandler
return|;
block|}
block|}
end_class

end_unit

