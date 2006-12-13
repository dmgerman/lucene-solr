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

begin_comment
comment|/**  *   * GOMPerson type used for feed and entry authors and contributors. It may also  * be used by custom elements.  *   *<pre>  *    *   atomPersonConstruct =  *   atomCommonAttributes,  *   (element atom:name { text }  *&amp; element atom:uri { atomUri }?  *&amp; element atom:email { atomEmailAddress }?  *&amp; extensionElement*)  *   }  *</pre>  *   * @author Simon Willnauer  * @see org.apache.lucene.gdata.gom.GOMAuthor  */
end_comment

begin_interface
DECL|interface|GOMPerson
specifier|public
interface|interface
name|GOMPerson
extends|extends
name|GOMElement
block|{
comment|/** 	 *  Atom local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"person"
decl_stmt|;
comment|/** 	 * @param aName - the person name value 	 */
DECL|method|setName
specifier|public
specifier|abstract
name|void
name|setName
parameter_list|(
name|String
name|aName
parameter_list|)
function_decl|;
comment|/** 	 * @param aEmail - the person email value 	 */
DECL|method|setEmail
specifier|public
specifier|abstract
name|void
name|setEmail
parameter_list|(
name|String
name|aEmail
parameter_list|)
function_decl|;
comment|/** 	 * @param uri - the person uri value 	 */
DECL|method|setUri
specifier|public
specifier|abstract
name|void
name|setUri
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * @return - the person name value 	 */
DECL|method|getName
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/** 	 * @return - the person email value 	 */
DECL|method|getEmail
specifier|public
specifier|abstract
name|String
name|getEmail
parameter_list|()
function_decl|;
comment|/** 	 * @return - the person uri value 	 */
DECL|method|getUri
specifier|public
specifier|abstract
name|String
name|getUri
parameter_list|()
function_decl|;
comment|// TODO needs extension possibility
block|}
end_interface

end_unit

