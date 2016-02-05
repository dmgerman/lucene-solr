begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Manage the discovery and persistence of core definitions across Solr restarts  */
end_comment

begin_interface
DECL|interface|CoresLocator
specifier|public
interface|interface
name|CoresLocator
block|{
comment|/**    * Make new cores available for discovery    * @param cc              the CoreContainer    * @param coreDescriptors CoreDescriptors to persist    */
DECL|method|create
specifier|public
name|void
name|create
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
function_decl|;
comment|/**    * Ensure that the core definitions from the passed in CoreDescriptors    * will persist across container restarts.    * @param cc              the CoreContainer    * @param coreDescriptors CoreDescriptors to persist    */
DECL|method|persist
specifier|public
name|void
name|persist
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
function_decl|;
comment|/**    * Ensure that the core definitions from the passed in CoreDescriptors    * are not available for discovery    * @param cc              the CoreContainer    * @param coreDescriptors CoreDescriptors of the cores to remove    */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
modifier|...
name|coreDescriptors
parameter_list|)
function_decl|;
comment|/**    * Persist the new name of a renamed core    * @param cc    the CoreContainer    * @param oldCD the CoreDescriptor of the core before renaming    * @param newCD the CoreDescriptor of the core after renaming    */
DECL|method|rename
specifier|public
name|void
name|rename
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|oldCD
parameter_list|,
name|CoreDescriptor
name|newCD
parameter_list|)
function_decl|;
comment|/**    * Swap two core definitions    * @param cc  the CoreContainer    * @param cd1 the core descriptor of the first core, after swapping    * @param cd2 the core descriptor of the second core, after swapping    */
DECL|method|swap
specifier|public
name|void
name|swap
parameter_list|(
name|CoreContainer
name|cc
parameter_list|,
name|CoreDescriptor
name|cd1
parameter_list|,
name|CoreDescriptor
name|cd2
parameter_list|)
function_decl|;
comment|/**    * Load all the CoreDescriptors from persistence store    * @param cc the CoreContainer    * @return a list of all CoreDescriptors found    */
DECL|method|discover
specifier|public
name|List
argument_list|<
name|CoreDescriptor
argument_list|>
name|discover
parameter_list|(
name|CoreContainer
name|cc
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

