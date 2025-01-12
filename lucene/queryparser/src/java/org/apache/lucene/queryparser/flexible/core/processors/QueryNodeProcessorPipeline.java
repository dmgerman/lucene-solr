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
comment|/**  * A {@link QueryNodeProcessorPipeline} class should be used to build a query  * node processor pipeline.  *   * When a query node tree is processed using this class, it passes the query  * node tree to each processor on the pipeline and the result from each  * processor is passed to the next one, always following the order the  * processors were on the pipeline.  *   * When a {@link QueryConfigHandler} object is set on a  * {@link QueryNodeProcessorPipeline}, it also takes care of setting this  * {@link QueryConfigHandler} on all processor on pipeline.  *   */
end_comment

begin_class
DECL|class|QueryNodeProcessorPipeline
specifier|public
class|class
name|QueryNodeProcessorPipeline
implements|implements
name|QueryNodeProcessor
implements|,
name|List
argument_list|<
name|QueryNodeProcessor
argument_list|>
block|{
DECL|field|processors
specifier|private
name|LinkedList
argument_list|<
name|QueryNodeProcessor
argument_list|>
name|processors
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|queryConfig
specifier|private
name|QueryConfigHandler
name|queryConfig
decl_stmt|;
comment|/**    * Constructs an empty query node processor pipeline.    */
DECL|method|QueryNodeProcessorPipeline
specifier|public
name|QueryNodeProcessorPipeline
parameter_list|()
block|{
comment|// empty constructor
block|}
comment|/**    * Constructs with a {@link QueryConfigHandler} object.    */
DECL|method|QueryNodeProcessorPipeline
specifier|public
name|QueryNodeProcessorPipeline
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
comment|/**    * For reference about this method check:    * {@link QueryNodeProcessor#process(QueryNode)}.    *     * @param queryTree the query node tree to be processed    *     * @throws QueryNodeException if something goes wrong during the query node    *         processing    *     * @see QueryNode    */
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
for|for
control|(
name|QueryNodeProcessor
name|processor
range|:
name|this
operator|.
name|processors
control|)
block|{
name|queryTree
operator|=
name|processor
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
block|}
return|return
name|queryTree
return|;
block|}
comment|/**    * For reference about this method check:    * {@link QueryNodeProcessor#setQueryConfigHandler(QueryConfigHandler)}.    *     * @param queryConfigHandler the query configuration handler to be set.    *     * @see QueryNodeProcessor#getQueryConfigHandler()    * @see QueryConfigHandler    */
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
for|for
control|(
name|QueryNodeProcessor
name|processor
range|:
name|this
operator|.
name|processors
control|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|queryConfig
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @see List#add(Object)    */
annotation|@
name|Override
DECL|method|add
specifier|public
name|boolean
name|add
parameter_list|(
name|QueryNodeProcessor
name|processor
parameter_list|)
block|{
name|boolean
name|added
init|=
name|this
operator|.
name|processors
operator|.
name|add
argument_list|(
name|processor
argument_list|)
decl_stmt|;
if|if
condition|(
name|added
condition|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|queryConfig
argument_list|)
expr_stmt|;
block|}
return|return
name|added
return|;
block|}
comment|/**    * @see List#add(int, Object)    */
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|index
parameter_list|,
name|QueryNodeProcessor
name|processor
parameter_list|)
block|{
name|this
operator|.
name|processors
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|processor
argument_list|)
expr_stmt|;
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|queryConfig
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see List#addAll(Collection)    */
annotation|@
name|Override
DECL|method|addAll
specifier|public
name|boolean
name|addAll
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|QueryNodeProcessor
argument_list|>
name|c
parameter_list|)
block|{
name|boolean
name|anyAdded
init|=
name|this
operator|.
name|processors
operator|.
name|addAll
argument_list|(
name|c
argument_list|)
decl_stmt|;
for|for
control|(
name|QueryNodeProcessor
name|processor
range|:
name|c
control|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|queryConfig
argument_list|)
expr_stmt|;
block|}
return|return
name|anyAdded
return|;
block|}
comment|/**    * @see List#addAll(int, Collection)    */
annotation|@
name|Override
DECL|method|addAll
specifier|public
name|boolean
name|addAll
parameter_list|(
name|int
name|index
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|QueryNodeProcessor
argument_list|>
name|c
parameter_list|)
block|{
name|boolean
name|anyAdded
init|=
name|this
operator|.
name|processors
operator|.
name|addAll
argument_list|(
name|index
argument_list|,
name|c
argument_list|)
decl_stmt|;
for|for
control|(
name|QueryNodeProcessor
name|processor
range|:
name|c
control|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|queryConfig
argument_list|)
expr_stmt|;
block|}
return|return
name|anyAdded
return|;
block|}
comment|/**    * @see List#clear()    */
annotation|@
name|Override
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|this
operator|.
name|processors
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * @see List#contains(Object)    */
annotation|@
name|Override
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|contains
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**    * @see List#containsAll(Collection)    */
annotation|@
name|Override
DECL|method|containsAll
specifier|public
name|boolean
name|containsAll
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|containsAll
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|/**    * @see List#get(int)    */
annotation|@
name|Override
DECL|method|get
specifier|public
name|QueryNodeProcessor
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * @see List#indexOf(Object)    */
annotation|@
name|Override
DECL|method|indexOf
specifier|public
name|int
name|indexOf
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|indexOf
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**    * @see List#isEmpty()    */
annotation|@
name|Override
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * @see List#iterator()    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|QueryNodeProcessor
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * @see List#lastIndexOf(Object)    */
annotation|@
name|Override
DECL|method|lastIndexOf
specifier|public
name|int
name|lastIndexOf
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|lastIndexOf
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**    * @see List#listIterator()    */
annotation|@
name|Override
DECL|method|listIterator
specifier|public
name|ListIterator
argument_list|<
name|QueryNodeProcessor
argument_list|>
name|listIterator
parameter_list|()
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|listIterator
argument_list|()
return|;
block|}
comment|/**    * @see List#listIterator(int)    */
annotation|@
name|Override
DECL|method|listIterator
specifier|public
name|ListIterator
argument_list|<
name|QueryNodeProcessor
argument_list|>
name|listIterator
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|listIterator
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * @see List#remove(Object)    */
annotation|@
name|Override
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|remove
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/**    * @see List#remove(int)    */
annotation|@
name|Override
DECL|method|remove
specifier|public
name|QueryNodeProcessor
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|remove
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * @see List#removeAll(Collection)    */
annotation|@
name|Override
DECL|method|removeAll
specifier|public
name|boolean
name|removeAll
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|removeAll
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|/**    * @see List#retainAll(Collection)    */
annotation|@
name|Override
DECL|method|retainAll
specifier|public
name|boolean
name|retainAll
parameter_list|(
name|Collection
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|retainAll
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|/**    * @see List#set(int, Object)    */
annotation|@
name|Override
DECL|method|set
specifier|public
name|QueryNodeProcessor
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|QueryNodeProcessor
name|processor
parameter_list|)
block|{
name|QueryNodeProcessor
name|oldProcessor
init|=
name|this
operator|.
name|processors
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|processor
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldProcessor
operator|!=
name|processor
condition|)
block|{
name|processor
operator|.
name|setQueryConfigHandler
argument_list|(
name|this
operator|.
name|queryConfig
argument_list|)
expr_stmt|;
block|}
return|return
name|oldProcessor
return|;
block|}
comment|/**    * @see List#size()    */
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * @see List#subList(int, int)    */
annotation|@
name|Override
DECL|method|subList
specifier|public
name|List
argument_list|<
name|QueryNodeProcessor
argument_list|>
name|subList
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|subList
argument_list|(
name|fromIndex
argument_list|,
name|toIndex
argument_list|)
return|;
block|}
comment|/**    * @see List#toArray(Object[])    */
annotation|@
name|Override
DECL|method|toArray
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|toArray
parameter_list|(
name|T
index|[]
name|array
parameter_list|)
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|toArray
argument_list|(
name|array
argument_list|)
return|;
block|}
comment|/**    * @see List#toArray()    */
annotation|@
name|Override
DECL|method|toArray
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
return|return
name|this
operator|.
name|processors
operator|.
name|toArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

