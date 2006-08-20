begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server.registry
package|package
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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Templates
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
name|search
operator|.
name|config
operator|.
name|IndexSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import

begin_comment
comment|/**  * This interface describes a service provided by the GData-Server.  * @see org.apache.lucene.gdata.server.registry.GDataServerRegistry   * @author Simon Willnauer  *  */
end_comment

begin_interface
DECL|interface|ProvidedService
specifier|public
interface|interface
name|ProvidedService
block|{
comment|/**       * @return Returns the feedType.       */
DECL|method|getFeedType
specifier|public
specifier|abstract
name|Class
name|getFeedType
parameter_list|()
function_decl|;
comment|/**       * @return - the extension profile for this feed       */
DECL|method|getExtensionProfile
specifier|public
specifier|abstract
name|ExtensionProfile
name|getExtensionProfile
parameter_list|()
function_decl|;
comment|/**      * @return the entry Type configured for this Service      */
DECL|method|getEntryType
specifier|public
specifier|abstract
name|Class
name|getEntryType
parameter_list|()
function_decl|;
comment|/**      * @return - the service name      */
DECL|method|getName
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * releases all dependencies and resources      */
DECL|method|destroy
specifier|public
specifier|abstract
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**      * @return the index schema configuration for this service      */
DECL|method|getIndexSchema
specifier|public
specifier|abstract
name|IndexSchema
name|getIndexSchema
parameter_list|()
function_decl|;
comment|/**      * @return the compiled xslt stylesheet to transform the feed / entry for preview      */
DECL|method|getTransformTemplate
specifier|public
specifier|abstract
name|Templates
name|getTransformTemplate
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

