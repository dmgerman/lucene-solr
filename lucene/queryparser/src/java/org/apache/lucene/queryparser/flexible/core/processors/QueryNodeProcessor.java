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
comment|/**  * A {@link QueryNodeProcessor} is an interface for classes that process a  * {@link QueryNode} tree.  *<p>  * The implementor of this class should perform some operation on a query node  * tree and return the same or another query node tree.  *<p>  * It also may carry a {@link QueryConfigHandler} object that contains  * configuration about the query represented by the query tree or the  * collection/index where it's intended to be executed.  *<p>  * In case there is any {@link QueryConfigHandler} associated to the query tree  * to be processed, it should be set using  * {@link QueryNodeProcessor#setQueryConfigHandler(QueryConfigHandler)} before  * {@link QueryNodeProcessor#process(QueryNode)} is invoked.  *   * @see QueryNode  * @see QueryNodeProcessor  * @see QueryConfigHandler  */
end_comment

begin_interface
DECL|interface|QueryNodeProcessor
specifier|public
interface|interface
name|QueryNodeProcessor
block|{
comment|/**    * Processes a query node tree. It may return the same or another query tree.    * I should never return<code>null</code>.    *     * @param queryTree    *          tree root node    *     * @return the processed query tree    */
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
function_decl|;
comment|/**    * Sets the {@link QueryConfigHandler} associated to the query tree.    */
DECL|method|setQueryConfigHandler
specifier|public
name|void
name|setQueryConfigHandler
parameter_list|(
name|QueryConfigHandler
name|queryConfigHandler
parameter_list|)
function_decl|;
comment|/**    * Returns the {@link QueryConfigHandler} associated to the query tree if any,    * otherwise it returns<code>null</code>    *     * @return the {@link QueryConfigHandler} associated to the query tree if any,    *         otherwise it returns<code>null</code>    */
DECL|method|getQueryConfigHandler
specifier|public
name|QueryConfigHandler
name|getQueryConfigHandler
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

