begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.processors
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
name|core
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
name|QueryNode
import|;
end_import

begin_comment
comment|/**  *<p>  * This is a default implementation for the {@link QueryNodeProcessor}  * interface, it's an abstract class, so it should be extended by classes that  * want to process a {@link QueryNode} tree.  *</p>  *<p>  * This class process {@link QueryNode}s from left to right in the tree. While  * it's walking down the tree, for every node,  * {@link #preProcessNode(QueryNode)} is invoked. After a node's children are  * processed, {@link #postProcessNode(QueryNode)} is invoked for that node.  * {@link #setChildrenOrder(List)} is invoked before  * {@link #postProcessNode(QueryNode)} only if the node has at least one child,  * in {@link #setChildrenOrder(List)} the implementor might redefine the  * children order or remove any children from the children list.  *</p>  *<p>  * Here is an example about how it process the nodes:  *</p>  *   *<pre>  *      a  *     / \  *    b   e  *   / \  *  c   d  *</pre>  *   * Here is the order the methods would be invoked for the tree described above:  *   *<pre>  *      preProcessNode( a );  *      preProcessNode( b );  *      preProcessNode( c );  *      postProcessNode( c );  *      preProcessNode( d );  *      postProcessNode( d );  *      setChildrenOrder( bChildrenList );  *      postProcessNode( b );  *      preProcessNode( e );  *      postProcessNode( e );  *      setChildrenOrder( aChildrenList );  *      postProcessNode( a )  *</pre>  *   * @see org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor  */
end_comment

begin_class
DECL|class|QueryNodeProcessorImpl
specifier|public
specifier|abstract
class|class
name|QueryNodeProcessorImpl
implements|implements
name|QueryNodeProcessor
block|{
DECL|field|childrenListPool
specifier|private
name|ArrayList
argument_list|<
name|ChildrenList
argument_list|>
name|childrenListPool
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queryConfig
specifier|private
name|QueryConfigHandler
name|queryConfig
decl_stmt|;
DECL|method|QueryNodeProcessorImpl
specifier|public
name|QueryNodeProcessorImpl
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|QueryNodeProcessorImpl
specifier|public
name|QueryNodeProcessorImpl
parameter_list|(
name|QueryConfigHandler
name|queryConfigHandler
parameter_list|)
block|{
name|this
operator|.
name|queryConfig
operator|=
name|queryConfigHandler
expr_stmt|;
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
return|return
name|processIteration
argument_list|(
name|queryTree
argument_list|)
return|;
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
comment|/**    * This method is called every time a child is processed.    *     * @param queryTree    *          the query node child to be processed    * @throws QueryNodeException    *           if something goes wrong during the query node processing    */
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
name|ChildrenList
name|newChildren
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
name|newChildren
operator|=
name|allocateChildrenList
argument_list|()
expr_stmt|;
try|try
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
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|newChildren
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|QueryNode
argument_list|>
name|orderedChildrenList
init|=
name|setChildrenOrder
argument_list|(
name|newChildren
argument_list|)
decl_stmt|;
name|queryTree
operator|.
name|set
argument_list|(
name|orderedChildrenList
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|newChildren
operator|.
name|beingUsed
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
DECL|method|allocateChildrenList
specifier|private
name|ChildrenList
name|allocateChildrenList
parameter_list|()
block|{
name|ChildrenList
name|list
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ChildrenList
name|auxList
range|:
name|this
operator|.
name|childrenListPool
control|)
block|{
if|if
condition|(
operator|!
name|auxList
operator|.
name|beingUsed
condition|)
block|{
name|list
operator|=
name|auxList
expr_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ChildrenList
argument_list|()
expr_stmt|;
name|this
operator|.
name|childrenListPool
operator|.
name|add
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|beingUsed
operator|=
literal|true
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/**    * For reference about this method check:    * {@link QueryNodeProcessor#setQueryConfigHandler(QueryConfigHandler)}.    *     * @param queryConfigHandler    *          the query configuration handler to be set.    *     * @see QueryNodeProcessor#getQueryConfigHandler()    * @see QueryConfigHandler    */
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
name|queryConfig
operator|=
name|queryConfigHandler
expr_stmt|;
block|}
comment|/**    * For reference about this method check:    * {@link QueryNodeProcessor#getQueryConfigHandler()}.    *     * @return QueryConfigHandler the query configuration handler to be set.    *     * @see QueryNodeProcessor#setQueryConfigHandler(QueryConfigHandler)    * @see QueryConfigHandler    */
annotation|@
name|Override
DECL|method|getQueryConfigHandler
specifier|public
name|QueryConfigHandler
name|getQueryConfigHandler
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryConfig
return|;
block|}
comment|/**    * This method is invoked for every node when walking down the tree.    *     * @param node    *          the query node to be pre-processed    *     * @return a query node    *     * @throws QueryNodeException    *           if something goes wrong during the query node processing    */
DECL|method|preProcessNode
specifier|abstract
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
function_decl|;
comment|/**    * This method is invoked for every node when walking up the tree.    *     * @param node    *          node the query node to be post-processed    *     * @return a query node    *     * @throws QueryNodeException    *           if something goes wrong during the query node processing    */
DECL|method|postProcessNode
specifier|abstract
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
function_decl|;
comment|/**    * This method is invoked for every node that has at least on child. It's    * invoked right before {@link #postProcessNode(QueryNode)} is invoked.    *     * @param children    *          the list containing all current node's children    *     * @return a new list containing all children that should be set to the    *         current node    *     * @throws QueryNodeException    *           if something goes wrong during the query node processing    */
DECL|method|setChildrenOrder
specifier|abstract
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
function_decl|;
DECL|class|ChildrenList
specifier|private
specifier|static
class|class
name|ChildrenList
extends|extends
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
block|{
DECL|field|beingUsed
name|boolean
name|beingUsed
decl_stmt|;
block|}
block|}
end_class

end_unit

