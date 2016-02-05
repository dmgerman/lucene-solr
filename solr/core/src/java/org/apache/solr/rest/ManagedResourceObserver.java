begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.rest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_comment
comment|/**  * Allows a Solr component to register as an observer of important  * ManagedResource events, such as when the managed data is loaded.  */
end_comment

begin_interface
DECL|interface|ManagedResourceObserver
specifier|public
interface|interface
name|ManagedResourceObserver
block|{
comment|/**    * Event notification raised once during core initialization to notify    * listeners that a ManagedResource is fully initialized. The most     * common implementation of this method is to pull the managed data from    * the concrete ManagedResource and use it to initialize an analysis component.    * For example, the ManagedStopFilterFactory implements this method to    * receive the list of managed stop words needed to create a CharArraySet     * for the StopFilter.     */
DECL|method|onManagedResourceInitialized
name|void
name|onManagedResourceInitialized
parameter_list|(
name|NamedList
argument_list|<
name|?
argument_list|>
name|args
parameter_list|,
name|ManagedResource
name|res
parameter_list|)
throws|throws
name|SolrException
function_decl|;
block|}
end_interface

end_unit

