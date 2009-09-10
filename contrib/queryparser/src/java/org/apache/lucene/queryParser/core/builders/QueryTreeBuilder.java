begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.builders
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
name|builders
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
name|HashMap
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
name|messages
operator|.
name|MessageImpl
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
name|queryParser
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|FieldableNode
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
name|queryParser
operator|.
name|standard
operator|.
name|parser
operator|.
name|EscapeQuerySyntaxImpl
import|;
end_import

begin_comment
comment|/**  * This class should be used when there is a builder for each type of node.  *   * The type of node may be defined in 2 different ways: - by the field name,  * when the node implements the {@link FieldableNode} interface - by its class,  * it keeps checking the class and all the interfaces and classes this class  * implements/extends until it finds a builder for that class/interface  *   * This class always check if there is a builder for the field name before it  * checks for the node class. So, field name builders have precedence over class  * builders.  *   * When a builder is found for a node, it's called and the node is passed to the  * builder. If the returned built object is not<code>null</code>, it's tagged  * on the node using the tag {@link QueryTreeBuilder#QUERY_TREE_BUILDER_TAGID}.  *   * The children are usually built before the parent node. However, if a builder  * associated to a node is an instance of {@link QueryTreeBuilder}, the node is  * delegated to this builder and it's responsible to build the node and its  * children.  *   * @see QueryBuilder  */
end_comment

begin_class
DECL|class|QueryTreeBuilder
specifier|public
class|class
name|QueryTreeBuilder
implements|implements
name|QueryBuilder
block|{
comment|/**    * This tag is used to tag the nodes in a query tree with the built objects    * produced from their own associated builder.    */
DECL|field|QUERY_TREE_BUILDER_TAGID
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_TREE_BUILDER_TAGID
init|=
name|QueryTreeBuilder
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|queryNodeBuilders
specifier|private
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|QueryNode
argument_list|>
argument_list|,
name|QueryBuilder
argument_list|>
name|queryNodeBuilders
decl_stmt|;
DECL|field|fieldNameBuilders
specifier|private
name|HashMap
argument_list|<
name|CharSequence
argument_list|,
name|QueryBuilder
argument_list|>
name|fieldNameBuilders
decl_stmt|;
comment|/**    * {@link QueryTreeBuilder} constructor.    */
DECL|method|QueryTreeBuilder
specifier|public
name|QueryTreeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
comment|/**    * Associates a field name with a builder.    *     * @param fieldName    *          the field name    * @param builder    *          the builder to be associated    */
DECL|method|setBuilder
specifier|public
name|void
name|setBuilder
parameter_list|(
name|CharSequence
name|fieldName
parameter_list|,
name|QueryBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|fieldNameBuilders
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|fieldNameBuilders
operator|=
operator|new
name|HashMap
argument_list|<
name|CharSequence
argument_list|,
name|QueryBuilder
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|fieldNameBuilders
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
comment|/**    * Associates a class with a builder    *     * @param queryNodeClass    *          the class    * @param builder    *          the builder to be associated    */
DECL|method|setBuilder
specifier|public
name|void
name|setBuilder
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|QueryNode
argument_list|>
name|queryNodeClass
parameter_list|,
name|QueryBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|queryNodeBuilders
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|queryNodeBuilders
operator|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|QueryNode
argument_list|>
argument_list|,
name|QueryBuilder
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|queryNodeBuilders
operator|.
name|put
argument_list|(
name|queryNodeClass
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|process
specifier|private
name|void
name|process
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
operator|!=
literal|null
condition|)
block|{
name|QueryBuilder
name|builder
init|=
name|getBuilder
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|builder
operator|instanceof
name|QueryTreeBuilder
operator|)
condition|)
block|{
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
name|children
control|)
block|{
name|process
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|processNode
argument_list|(
name|node
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getBuilder
specifier|private
name|QueryBuilder
name|getBuilder
parameter_list|(
name|QueryNode
name|node
parameter_list|)
block|{
name|QueryBuilder
name|builder
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fieldNameBuilders
operator|!=
literal|null
operator|&&
name|node
operator|instanceof
name|FieldableNode
condition|)
block|{
name|builder
operator|=
name|this
operator|.
name|fieldNameBuilders
operator|.
name|get
argument_list|(
operator|(
operator|(
name|FieldableNode
operator|)
name|node
operator|)
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|builder
operator|==
literal|null
operator|&&
name|this
operator|.
name|queryNodeBuilders
operator|!=
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|node
operator|.
name|getClass
argument_list|()
decl_stmt|;
do|do
block|{
name|builder
operator|=
name|getQueryBuilder
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|classes
init|=
name|node
operator|.
name|getClass
argument_list|()
operator|.
name|getInterfaces
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|actualClass
range|:
name|classes
control|)
block|{
name|builder
operator|=
name|getQueryBuilder
argument_list|(
name|actualClass
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
do|while
condition|(
name|builder
operator|==
literal|null
operator|&&
operator|(
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
operator|)
operator|!=
literal|null
condition|)
do|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|processNode
specifier|private
name|void
name|processNode
parameter_list|(
name|QueryNode
name|node
parameter_list|,
name|QueryBuilder
name|builder
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|LUCENE_QUERY_CONVERSION_ERROR
argument_list|,
operator|new
name|Object
index|[]
block|{
name|node
operator|.
name|toQueryString
argument_list|(
operator|new
name|EscapeQuerySyntaxImpl
argument_list|()
argument_list|)
block|,
name|node
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
block|}
argument_list|)
argument_list|)
throw|;
block|}
name|Object
name|obj
init|=
name|builder
operator|.
name|build
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|setTag
argument_list|(
name|QUERY_TREE_BUILDER_TAGID
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getQueryBuilder
specifier|private
name|QueryBuilder
name|getQueryBuilder
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|QueryNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|queryNodeBuilders
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Builds some kind of object from a query tree. Each node in the query tree    * is built using an specific builder associated to it.    *     * @param queryNode    *          the query tree root node    *     * @return the built object    *     * @throws QueryNodeException    *           if some node builder throws a {@link QueryNodeException} or if    *           there is a node which had no builder associated to it    */
DECL|method|build
specifier|public
name|Object
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|process
argument_list|(
name|queryNode
argument_list|)
expr_stmt|;
return|return
name|queryNode
operator|.
name|getTag
argument_list|(
name|QUERY_TREE_BUILDER_TAGID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

