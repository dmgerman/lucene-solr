begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_interface
DECL|interface|CollectionAdminParams
specifier|public
interface|interface
name|CollectionAdminParams
block|{
comment|/* Param used by DELETESTATUS call to clear all stored responses */
DECL|field|FLUSH
name|String
name|FLUSH
init|=
literal|"flush"
decl_stmt|;
DECL|field|COLLECTION
name|String
name|COLLECTION
init|=
literal|"collection"
decl_stmt|;
DECL|field|COUNT_PROP
name|String
name|COUNT_PROP
init|=
literal|"count"
decl_stmt|;
comment|/**    * A parameter to specify list of Solr nodes to be used (e.g. for collection creation or restore operation).    */
DECL|field|CREATE_NODE_SET_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_NODE_SET_PARAM
init|=
literal|"createNodeSet"
decl_stmt|;
comment|/**    * A parameter which specifies if the provided list of Solr nodes (via {@linkplain #CREATE_NODE_SET_PARAM})    * should be shuffled before being used.    */
DECL|field|CREATE_NODE_SET_SHUFFLE_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_NODE_SET_SHUFFLE_PARAM
init|=
literal|"createNodeSet.shuffle"
decl_stmt|;
comment|/**    * A parameter to specify the name of the index backup strategy to be used.    */
DECL|field|INDEX_BACKUP_STRATEGY
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_BACKUP_STRATEGY
init|=
literal|"indexBackup"
decl_stmt|;
comment|/**    * This constant defines the index backup strategy based on copying index files to desired location.    */
DECL|field|COPY_FILES_STRATEGY
specifier|public
specifier|static
specifier|final
name|String
name|COPY_FILES_STRATEGY
init|=
literal|"copy-files"
decl_stmt|;
comment|/**    * This constant defines the strategy to not copy index files (useful for meta-data only backup).    */
DECL|field|NO_INDEX_BACKUP_STRATEGY
specifier|public
specifier|static
specifier|final
name|String
name|NO_INDEX_BACKUP_STRATEGY
init|=
literal|"none"
decl_stmt|;
comment|/**    * This constant defines a list of valid index backup strategies.    */
DECL|field|INDEX_BACKUP_STRATEGIES
specifier|public
specifier|static
specifier|final
name|Collection
argument_list|<
name|String
argument_list|>
name|INDEX_BACKUP_STRATEGIES
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|COPY_FILES_STRATEGY
argument_list|,
name|NO_INDEX_BACKUP_STRATEGY
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

