begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
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
name|core
operator|.
name|DirectoryFactory
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
name|core
operator|.
name|SolrCore
import|;
end_import

begin_comment
comment|/**  * The state in this class can be easily shared between SolrCores across  * SolrCore reloads.  *   */
end_comment

begin_class
DECL|class|SolrCoreState
specifier|public
specifier|abstract
class|class
name|SolrCoreState
block|{
comment|/**    * Force the creation of a new IndexWriter using the settings from the given    * SolrCore.    *     * @param core    * @throws IOException    */
DECL|method|newIndexWriter
specifier|public
specifier|abstract
name|void
name|newIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the current IndexWriter. If a new IndexWriter must be created, use the    * settings from the given {@link SolrCore}.    *     * @param core    * @return    * @throws IOException    */
DECL|method|getIndexWriter
specifier|public
specifier|abstract
name|IndexWriter
name|getIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Decrement the number of references to this state. When then number of    * references hits 0, the state will close.    *     * @throws IOException    */
DECL|method|decref
specifier|public
specifier|abstract
name|void
name|decref
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Increment the number of references to this state.    */
DECL|method|incref
specifier|public
specifier|abstract
name|void
name|incref
parameter_list|()
function_decl|;
comment|/**    * Rollback the current IndexWriter. When creating the new IndexWriter use the    * settings from the given {@link SolrCore}.    *     * @param core    * @throws IOException    */
DECL|method|rollbackIndexWriter
specifier|public
specifier|abstract
name|void
name|rollbackIndexWriter
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the {@link DirectoryFactory} that should be used.    */
DECL|method|getDirectoryFactory
specifier|public
specifier|abstract
name|DirectoryFactory
name|getDirectoryFactory
parameter_list|()
function_decl|;
block|}
end_class

end_unit

