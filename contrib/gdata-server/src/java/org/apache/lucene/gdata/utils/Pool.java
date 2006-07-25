begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Basic interface to be implemented by ObjectPool implementations. Pools should  * provide a constructor with a  * {@link org.apache.lucene.gdata.utils.PoolObjectFactory} as a mandatory  * parameter to create and destory the pooled objects.  *   * @see org.apache.lucene.gdata.utils.PoolObjectFactory  *   * @author Simon Willnauer  * @param<Type> -  *            the type of the pooled objects  *   */
end_comment

begin_interface
DECL|interface|Pool
specifier|public
interface|interface
name|Pool
parameter_list|<
name|Type
parameter_list|>
block|{
comment|/**      * Return an object from the pool or create one if the pool is empty.      *       * @return - a pooled object      */
DECL|method|aquire
specifier|public
specifier|abstract
name|Type
name|aquire
parameter_list|()
function_decl|;
comment|/**      * Adds a previously aquired object to the pool. If the pool has already      * been closed or if the pool has already reached his size the released      * object will be destroyed using      * {@link PoolObjectFactory#destroyInstance(Object)} method.      *       * @param type -      *            the previously aquired object      */
DECL|method|release
specifier|public
specifier|abstract
name|void
name|release
parameter_list|(
specifier|final
name|Type
name|type
parameter_list|)
function_decl|;
comment|/**      * @return - the defined size of the pool      */
DECL|method|getSize
specifier|public
specifier|abstract
name|int
name|getSize
parameter_list|()
function_decl|;
comment|/**      * @return - the expire time of the objects in the pool if defined      */
DECL|method|getExpireTime
specifier|public
specifier|abstract
name|long
name|getExpireTime
parameter_list|()
function_decl|;
comment|/**      * @return<code>true</code> if and only if the pool uses an expire      *         mechanismn, otherwith<code>false</code>      */
DECL|method|expires
specifier|public
specifier|abstract
name|boolean
name|expires
parameter_list|()
function_decl|;
comment|/**      * releases all pooled objects using      * {@link PoolObjectFactory#destroyInstance(Object)} method. The pool can not      * be reused after this method has been called      */
DECL|method|destroy
specifier|public
specifier|abstract
name|void
name|destroy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

