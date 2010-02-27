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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_comment
comment|/**  * Returns primitive memory sizes for estimating RAM usage.  *   */
end_comment

begin_class
DECL|class|MemoryModel
specifier|public
specifier|abstract
class|class
name|MemoryModel
block|{
comment|/**    * @return size of array beyond contents    */
DECL|method|getArraySize
specifier|public
specifier|abstract
name|int
name|getArraySize
parameter_list|()
function_decl|;
comment|/**    * @return Class size overhead    */
DECL|method|getClassSize
specifier|public
specifier|abstract
name|int
name|getClassSize
parameter_list|()
function_decl|;
comment|/**    * @param clazz a primitive Class - bool, byte, char, short, long, float,    *        short, double, int    * @return the size in bytes of given primitive Class    */
DECL|method|getPrimitiveSize
specifier|public
specifier|abstract
name|int
name|getPrimitiveSize
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
function_decl|;
comment|/**    * @return size of reference    */
DECL|method|getReferenceSize
specifier|public
specifier|abstract
name|int
name|getReferenceSize
parameter_list|()
function_decl|;
block|}
end_class

end_unit

