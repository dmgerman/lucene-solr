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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|ServiceFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|administration
operator|.
name|AdminService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|registry
operator|.
name|ComponentType
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|componentType
operator|=
name|ComponentType
operator|.
name|SERVICEFACTORY
argument_list|)
DECL|class|ServiceFactoryStub
specifier|public
class|class
name|ServiceFactoryStub
extends|extends
name|ServiceFactory
block|{
DECL|field|service
specifier|public
name|Service
name|service
decl_stmt|;
DECL|field|adminService
specifier|public
name|AdminService
name|adminService
decl_stmt|;
comment|/**      * @see org.apache.lucene.gdata.server.ServiceFactory#getAdminService()      */
annotation|@
name|Override
DECL|method|getAdminService
specifier|public
name|AdminService
name|getAdminService
parameter_list|()
block|{
return|return
name|adminService
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.ServiceFactory#getService()      */
annotation|@
name|Override
DECL|method|getService
specifier|public
name|Service
name|getService
parameter_list|()
block|{
return|return
name|service
return|;
block|}
DECL|method|setAdminService
specifier|public
name|void
name|setAdminService
parameter_list|(
name|AdminService
name|service
parameter_list|)
block|{
name|this
operator|.
name|adminService
operator|=
name|service
expr_stmt|;
block|}
DECL|method|setService
specifier|public
name|void
name|setService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
block|}
block|}
end_class

end_unit

