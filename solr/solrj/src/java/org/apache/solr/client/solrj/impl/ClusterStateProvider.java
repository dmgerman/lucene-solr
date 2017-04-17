begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.impl
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|cloud
operator|.
name|ClusterState
import|;
end_import

begin_interface
DECL|interface|ClusterStateProvider
specifier|public
interface|interface
name|ClusterStateProvider
extends|extends
name|Closeable
block|{
comment|/**    * Obtain the state of the collection (cluster status).    * @return the collection state, or null is collection doesn't exist    */
DECL|method|getState
name|ClusterState
operator|.
name|CollectionRef
name|getState
parameter_list|(
name|String
name|collection
parameter_list|)
function_decl|;
comment|/**    * Obtain set of live_nodes for the cluster.    */
DECL|method|liveNodes
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|()
function_decl|;
comment|/**    * Given an alias, returns the collection name that this alias points to    */
DECL|method|getAlias
name|String
name|getAlias
parameter_list|(
name|String
name|alias
parameter_list|)
function_decl|;
comment|/**    * Given a name, returns the collection name if an alias by that name exists, or    * returns the name itself, if no alias exists.    */
DECL|method|getCollectionName
name|String
name|getCollectionName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Obtain a cluster property, or null if it doesn't exist.    */
DECL|method|getClusterProperty
name|Object
name|getClusterProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
function_decl|;
comment|/**    * Obtain a cluster property, or the default value if it doesn't exist.    */
DECL|method|getClusterProperty
name|Object
name|getClusterProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|def
parameter_list|)
function_decl|;
DECL|method|connect
name|void
name|connect
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

