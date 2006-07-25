begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.storage
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
name|ServerComponent
import|;
end_import

begin_comment
comment|/**  * An interface to define a central storage controller acting as a  *<tt>Stroage</tt> Factory. The<tt>StroageController</tt> manages the  * storage logic. Subclasses of {@link StorageController} can be registered as  * {@link org.apache.lucene.gdata.server.registry.Component} in the  * {@link org.apache.lucene.gdata.server.registry.GDataServerRegistry}. A  * single instance of the contorller will be loaded and passed to clients via  * the lookup service.  *<p>  * This instances, registered in the registry must be thread save as they are  * shared between several clients  *</p>  *<p>  * Each StroageController implementation must provide a super user  * {@link org.apache.lucene.gdata.data.GDataAccount} with all  * {@link org.apache.lucene.gdata.data.GDataAccount.AccountRole} set. This  * account must have the defined name<i>administrator</i> and a default  * password<i>password</i>. The password has to be updated by the server  * administrator before production use.  * To get the predefinded GDataAccount use {@link org.apache.lucene.gdata.data.GDataAccount#createAdminAccount()}  *</p>  *  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|StorageController
specifier|public
interface|interface
name|StorageController
extends|extends
name|ServerComponent
block|{
comment|/**      * Destroys the controller - this method is called by the registry when the      * context will be destroyed      */
DECL|method|destroy
specifier|public
specifier|abstract
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**      * Creates Storage instances to access the underlaying storage component      *       * @return a storage instance      * @throws StorageException -      *             if the storage instance can not be created      */
DECL|method|getStorage
specifier|public
specifier|abstract
name|Storage
name|getStorage
parameter_list|()
throws|throws
name|StorageException
function_decl|;
comment|/**      * Releases a new unique ID      * @return - unique ID      */
DECL|method|releaseId
specifier|public
specifier|abstract
name|String
name|releaseId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

