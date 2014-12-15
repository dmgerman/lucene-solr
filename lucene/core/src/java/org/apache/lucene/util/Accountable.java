begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
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
name|Collections
import|;
end_import

begin_comment
comment|/**  * An object whose RAM usage can be computed.  *  * @lucene.internal  */
end_comment

begin_interface
DECL|interface|Accountable
specifier|public
interface|interface
name|Accountable
block|{
comment|/**    * Return the memory usage of this object in bytes. Negative values are illegal.    */
DECL|method|ramBytesUsed
name|long
name|ramBytesUsed
parameter_list|()
function_decl|;
comment|/**    * Returns nested resources of this class.     * The result should be a point-in-time snapshot (to avoid race conditions).    * @see Accountables    */
DECL|method|getChildResources
specifier|default
name|Iterable
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
end_interface

end_unit

