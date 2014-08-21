begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.store.blockcache
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|store
operator|.
name|blockcache
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Cache
specifier|public
interface|interface
name|Cache
block|{
comment|/**    * Remove a file from the cache.    *     * @param name    *          cache file name    */
DECL|method|delete
name|void
name|delete
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Update the content of the specified cache file. Creates cache entry if    * necessary.    *     */
DECL|method|update
name|void
name|update
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|blockId
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
function_decl|;
comment|/**    * Fetch the specified cache file content.    *     * @return true if cached content found, otherwise return false    */
DECL|method|fetch
name|boolean
name|fetch
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|blockId
parameter_list|,
name|int
name|blockOffset
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|lengthToReadInBlock
parameter_list|)
function_decl|;
comment|/**    * Number of entries in the cache.    */
DECL|method|size
name|long
name|size
parameter_list|()
function_decl|;
comment|/**    * Expert: Rename the specified file in the cache. Allows a file to be moved    * without invalidating the cache.    *     * @param source    *          original name    * @param dest    *          final name    */
DECL|method|renameCacheFile
name|void
name|renameCacheFile
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
function_decl|;
comment|/**    * Release any resources associated with the cache.    */
DECL|method|releaseResources
name|void
name|releaseResources
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

