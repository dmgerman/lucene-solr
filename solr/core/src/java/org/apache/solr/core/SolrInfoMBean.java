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
name|net
operator|.
name|URL
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
comment|/**  * MBean interface for getting various ui friendly strings and URLs  * for use by objects which are 'pluggable' to make server administration  * easier.  *  *  */
end_comment

begin_interface
DECL|interface|SolrInfoMBean
specifier|public
interface|interface
name|SolrInfoMBean
block|{
comment|/**    * Category of Solr component.    */
DECL|enum|Category
DECL|enum constant|CONTAINER
DECL|enum constant|ADMIN
DECL|enum constant|CORE
DECL|enum constant|QUERY
DECL|enum constant|UPDATE
DECL|enum constant|CACHE
DECL|enum constant|HIGHLIGHTER
DECL|enum constant|QUERYPARSER
DECL|enum constant|SPELLCHECKER
enum|enum
name|Category
block|{
name|CONTAINER
block|,
name|ADMIN
block|,
name|CORE
block|,
name|QUERY
block|,
name|UPDATE
block|,
name|CACHE
block|,
name|HIGHLIGHTER
block|,
name|QUERYPARSER
block|,
name|SPELLCHECKER
block|,
DECL|enum constant|SEARCHER
DECL|enum constant|REPLICATION
DECL|enum constant|TLOG
DECL|enum constant|INDEX
DECL|enum constant|DIRECTORY
DECL|enum constant|HTTP
DECL|enum constant|OTHER
name|SEARCHER
block|,
name|REPLICATION
block|,
name|TLOG
block|,
name|INDEX
block|,
name|DIRECTORY
block|,
name|HTTP
block|,
name|OTHER
block|}
comment|/**    * Top-level group of beans or metrics for a subsystem.    */
DECL|enum|Group
DECL|enum constant|jvm
DECL|enum constant|jetty
DECL|enum constant|node
DECL|enum constant|core
DECL|enum constant|collection
DECL|enum constant|shard
DECL|enum constant|cluster
DECL|enum constant|overseer
enum|enum
name|Group
block|{
name|jvm
block|,
name|jetty
block|,
name|node
block|,
name|core
block|,
name|collection
block|,
name|shard
block|,
name|cluster
block|,
name|overseer
block|}
comment|/**    * Simple common usage name, e.g. BasicQueryHandler,    * or fully qualified clas name.    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/** Simple common usage version, e.g. 2.0 */
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/** Simple one or two line description */
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/** Purpose of this Class */
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
function_decl|;
comment|/** CVS Source, SVN Source, etc */
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
function_decl|;
comment|/**    * Documentation URL list.    *    *<p>    * Suggested documentation URLs: Homepage for sponsoring project,    * FAQ on class usage, Design doc for class, Wiki, bug reporting URL, etc...    *</p>    */
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
function_decl|;
comment|/**    * Any statistics this instance would like to be publicly available via    * the Solr Administration interface.    *    *<p>    * Any Object type may be stored in the list, but only the    *<code>toString()</code> representation will be used.    *</p>    */
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

