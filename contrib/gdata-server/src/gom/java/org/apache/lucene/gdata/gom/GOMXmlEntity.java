begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|gom
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  * GOMXmlEntity is a abstract base interface for all Gdata Object Model  * Interfaces to be implemented by any class which is a part of the GOM. This  * interface defines a basic interface for xml attributes and elements  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMXmlEntity
specifier|public
specifier|abstract
interface|interface
name|GOMXmlEntity
block|{
comment|/** 	 * @return - the entities QName 	 * @see QName 	 *  	 */
DECL|method|getQname
specifier|public
specifier|abstract
name|QName
name|getQname
parameter_list|()
function_decl|;
comment|/** 	 * @param aString - the namespace uri to set 	 */
DECL|method|setNamespaceUri
specifier|public
specifier|abstract
name|void
name|setNamespaceUri
parameter_list|(
name|String
name|aString
parameter_list|)
function_decl|;
comment|/** 	 * @param aString - the namespace prefix to set 	 */
DECL|method|setNamespacePrefix
specifier|public
specifier|abstract
name|void
name|setNamespacePrefix
parameter_list|(
name|String
name|aString
parameter_list|)
function_decl|;
comment|/** 	 * @param aLocalName - the localname of the entitiy 	 */
DECL|method|setLocalName
specifier|public
specifier|abstract
name|void
name|setLocalName
parameter_list|(
name|String
name|aLocalName
parameter_list|)
function_decl|;
comment|/** 	 * @return - the local name of the entitiy 	 */
DECL|method|getLocalName
specifier|public
specifier|abstract
name|String
name|getLocalName
parameter_list|()
function_decl|;
comment|/** 	 * @return - the text value of the entity 	 */
DECL|method|getTextValue
specifier|public
specifier|abstract
name|String
name|getTextValue
parameter_list|()
function_decl|;
comment|/** 	 * @param aTextValue - the text value of the entity 	 */
DECL|method|setTextValue
specifier|public
specifier|abstract
name|void
name|setTextValue
parameter_list|(
name|String
name|aTextValue
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

