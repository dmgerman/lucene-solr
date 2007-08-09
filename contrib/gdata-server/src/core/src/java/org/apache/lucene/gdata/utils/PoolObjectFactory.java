begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
package|;
end_package

begin_comment
comment|/**  *<p>  * This interface enables {@link org.apache.lucene.gdata.utils.Pool} users to  * build a custom creation and destroy mechanismn for pooled objects.  * Implementations can use standart creation to prevent the pool from using  * reflection to create objects of the specific type. This implementation  * seperates the Pool implementation from the creation or the destruction of a  * pooled type.  *</p>  *<p>  * The destroy method can be used to close datasource connections or release  * resources if the object will be removed from the pool  *</p>  *   *   * @see org.apache.lucene.gdata.utils.Pool  *  * @param<Type> -  *            the type to be created  *   */
end_comment

begin_interface
DECL|interface|PoolObjectFactory
specifier|public
interface|interface
name|PoolObjectFactory
parameter_list|<
name|Type
parameter_list|>
block|{
comment|/**      * @return an instance of the specified Type      */
DECL|method|getInstance
specifier|public
specifier|abstract
name|Type
name|getInstance
parameter_list|()
function_decl|;
comment|/**      * destroys the given instance      * @param type - the object to destroy / release all resources      */
DECL|method|destroyInstance
specifier|public
specifier|abstract
name|void
name|destroyInstance
parameter_list|(
name|Type
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

