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
name|storage
operator|.
name|Storage
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
name|storage
operator|.
name|StorageController
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
name|storage
operator|.
name|StorageException
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
name|STORAGECONTROLLER
argument_list|)
DECL|class|StorageControllerStub
specifier|public
class|class
name|StorageControllerStub
implements|implements
name|StorageController
block|{
comment|/**      *       */
DECL|method|StorageControllerStub
specifier|public
name|StorageControllerStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * @see org.apache.lucene.gdata.storage.StorageController#destroy()      */
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{     }
comment|/**      * @see org.apache.lucene.gdata.storage.StorageController#getStorage()      */
DECL|method|getStorage
specifier|public
name|Storage
name|getStorage
parameter_list|()
throws|throws
name|StorageException
block|{
return|return
operator|new
name|StorageStub
argument_list|()
return|;
block|}
comment|/**      * @see org.apache.lucene.gdata.server.registry.ServerComponent#initialize()      */
DECL|method|initialize
specifier|public
name|void
name|initialize
parameter_list|()
block|{     }
DECL|method|releaseId
specifier|public
name|String
name|releaseId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

