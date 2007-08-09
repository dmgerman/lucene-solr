begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage.lucenestorage
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|storage
operator|.
name|lucenestorage
package|;
end_package

begin_comment
comment|/**  *   *  *  */
end_comment

begin_interface
DECL|interface|ConcurrentStorageLock
specifier|public
interface|interface
name|ConcurrentStorageLock
block|{
comment|/**      * @param key      * @return      */
DECL|method|setLock
specifier|public
specifier|abstract
name|boolean
name|setLock
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * @param key      * @return      */
DECL|method|releaseLock
specifier|public
specifier|abstract
name|boolean
name|releaseLock
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      * @return      */
DECL|method|releaseThreadLocks
specifier|public
specifier|abstract
name|boolean
name|releaseThreadLocks
parameter_list|()
function_decl|;
comment|/**      * @param key      * @return      */
DECL|method|isKeyLocked
specifier|public
specifier|abstract
name|boolean
name|isKeyLocked
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
function_decl|;
comment|/**      *       */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

