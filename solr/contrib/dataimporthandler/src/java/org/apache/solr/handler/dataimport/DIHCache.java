begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>  * A cache that allows a DIH entity's data to persist locally prior being joined  * to other data and/or indexed.  *</p>  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|DIHCache
specifier|public
interface|interface
name|DIHCache
extends|extends
name|Iterable
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
block|{
comment|/**    *<p>    * Opens the cache using the specified properties. The {@link Context}    * includes any parameters needed by the cache impl. This must be called    * before any read/write operations are permitted.    */
DECL|method|open
specifier|public
name|void
name|open
parameter_list|(
name|Context
name|context
parameter_list|)
function_decl|;
comment|/**    *<p>    * Releases resources used by this cache, if possible. The cache is flushed    * but not destroyed.    *</p>    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    *<p>    * Persists any pending data to the cache    *</p>    */
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
function_decl|;
comment|/**    *<p>    * Closes the cache, if open. Then removes all data, possibly removing the    * cache entirely from persistent storage.    *</p>    */
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**    *<p>    * Adds a document. If a document already exists with the same key, both    * documents will exist in the cache, as the cache allows duplicate keys. To    * update a key's documents, first call delete(Object key).    *</p>    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|rec
parameter_list|)
function_decl|;
comment|/**    *<p>    * Returns an iterator, allowing callers to iterate through the entire cache    * in key, then insertion, order.    *</p>    */
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/**    *<p>    * Returns an iterator, allowing callers to iterate through all documents that    * match the given key in insertion order.    *</p>    */
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|iterator
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**    *<p>    * Delete all documents associated with the given key    *</p>    */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
comment|/**    *<p>    * Delete all data from the cache,leaving the empty cache intact.    *</p>    */
DECL|method|deleteAll
specifier|public
name|void
name|deleteAll
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

