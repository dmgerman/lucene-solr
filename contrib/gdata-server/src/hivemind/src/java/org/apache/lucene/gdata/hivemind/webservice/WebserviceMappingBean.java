begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.hivemind.webservice
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|hivemind
operator|.
name|webservice
package|;
end_package

begin_comment
comment|/**  * This class is a simple configuration bean to expose a certain service via a  * hessian webservice. The configuration requieres the classtype of the  * interface and an instance of a subclass to invoke the interface methodes.  *<p>  * This bean will be created by Hivemind for each configured service and will be  * passed to the  * {@link org.apache.lucene.gdata.hivemind.webservice.HessianSkeletonProvider}  * as a Map.  *</p>  *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|WebserviceMappingBean
specifier|public
class|class
name|WebserviceMappingBean
block|{
DECL|field|serviceInterface
specifier|private
name|Class
name|serviceInterface
decl_stmt|;
DECL|field|serviceImpl
specifier|private
name|Object
name|serviceImpl
decl_stmt|;
comment|/**      * Bean constructor      */
DECL|method|WebserviceMappingBean
specifier|public
name|WebserviceMappingBean
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Returns the serviceImpl.      */
DECL|method|getServiceImpl
specifier|public
name|Object
name|getServiceImpl
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceImpl
return|;
block|}
comment|/**      * @param serviceImpl      *            The serviceImpl to set.      */
DECL|method|setServiceImpl
specifier|public
name|void
name|setServiceImpl
parameter_list|(
name|Object
name|serviceImpl
parameter_list|)
block|{
name|this
operator|.
name|serviceImpl
operator|=
name|serviceImpl
expr_stmt|;
block|}
comment|/**      * @return Returns the serviceInterface.      */
DECL|method|getServiceInterface
specifier|public
name|Class
name|getServiceInterface
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceInterface
return|;
block|}
comment|/**      * @param serviceInterface      *            The serviceInterface to set.      */
DECL|method|setServiceInterface
specifier|public
name|void
name|setServiceInterface
parameter_list|(
name|Class
name|serviceInterface
parameter_list|)
block|{
name|this
operator|.
name|serviceInterface
operator|=
name|serviceInterface
expr_stmt|;
block|}
block|}
end_class

end_unit

