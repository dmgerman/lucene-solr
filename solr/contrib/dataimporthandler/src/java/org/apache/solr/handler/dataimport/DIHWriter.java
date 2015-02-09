begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SolrInputDocument
import|;
end_import

begin_comment
comment|/**  * @lucene.experimental  *  */
end_comment

begin_interface
DECL|interface|DIHWriter
specifier|public
interface|interface
name|DIHWriter
block|{
comment|/**    *<p>    *  If this writer supports transactions or commit points, then commit any changes,    *  optionally optimizing the data for read/write performance    *</p>    */
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|boolean
name|optimize
parameter_list|)
function_decl|;
comment|/**    *<p>    *  Release resources used by this writer.  After calling close, reads&amp; updates will throw exceptions.    *</p>    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    *<p>    *  If this writer supports transactions or commit points, then roll back any uncommitted changes.    *</p>    */
DECL|method|rollback
specifier|public
name|void
name|rollback
parameter_list|()
function_decl|;
comment|/**    *<p>    *  Delete from the writer's underlying data store based the passed-in writer-specific query. (Optional Operation)    *</p>    */
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|String
name|q
parameter_list|)
function_decl|;
comment|/**    *<p>    *  Delete everything from the writer's underlying data store    *</p>    */
DECL|method|doDeleteAll
specifier|public
name|void
name|doDeleteAll
parameter_list|()
function_decl|;
comment|/**    *<p>    *  Delete from the writer's underlying data store based on the passed-in Primary Key    *</p>    */
DECL|method|deleteDoc
specifier|public
name|void
name|deleteDoc
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**    *<p>    *  Add a document to this writer's underlying data store.    *</p>    * @return true on success, false on failure    */
DECL|method|upload
specifier|public
name|boolean
name|upload
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
function_decl|;
comment|/**    *<p>    *  Provide context information for this writer.  init() should be called before using the writer.    *</p>    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Context
name|context
parameter_list|)
function_decl|;
comment|/**    *<p>    *  Specify the keys to be modified by a delta update (required by writers that can store duplicate keys)    *</p>    */
DECL|method|setDeltaKeys
specifier|public
name|void
name|setDeltaKeys
parameter_list|(
name|Set
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|deltaKeys
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

