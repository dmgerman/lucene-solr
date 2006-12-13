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
comment|/**  * The "atom:link" element defines a reference from an entry or feed to a Web  * resource. This specification assigns no meaning to the content (if any) of  * this element.  *   *<pre>  *  atomLink =  *  element atom:link {  *  atomCommonAttributes,  *  attribute href { atomUri },  *  attribute rel { atomNCName | atomUri }?,  *  attribute type { atomMediaType }?,  *  attribute hreflang { atomLanguageTag }?,  *  attribute title { text }?,  *  attribute length { text }?,  *  undefinedContent  *  }  *</pre>  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMLink
specifier|public
interface|interface
name|GOMLink
extends|extends
name|GOMElement
block|{
comment|/** 	 * Atom local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"link"
decl_stmt|;
comment|/** 	 * @return - the href attribute value of the element link 	 */
DECL|method|getHref
specifier|public
name|String
name|getHref
parameter_list|()
function_decl|;
comment|/** 	 * @param aHref - 	 *            the href attribute value of the element link to set. 	 */
DECL|method|setHref
specifier|public
name|void
name|setHref
parameter_list|(
name|String
name|aHref
parameter_list|)
function_decl|;
comment|/** 	 * @return the hreflang attribute value of the element link 	 */
DECL|method|getHrefLang
specifier|public
name|String
name|getHrefLang
parameter_list|()
function_decl|;
comment|/** 	 * @param aHrefLang - 	 *            the hreflang attribute value of the element link to set. 	 */
DECL|method|setHrefLang
specifier|public
name|void
name|setHrefLang
parameter_list|(
name|String
name|aHrefLang
parameter_list|)
function_decl|;
comment|/** 	 * @return - the length attribute value of the element link. 	 */
DECL|method|getLength
specifier|public
name|Integer
name|getLength
parameter_list|()
function_decl|;
comment|/** 	 * @param aLength - 	 *            the length attribute value of the element link to set. 	 */
DECL|method|setLength
specifier|public
name|void
name|setLength
parameter_list|(
name|Integer
name|aLength
parameter_list|)
function_decl|;
comment|/** 	 * @return - the rel attribute value of the element link. 	 */
DECL|method|getRel
specifier|public
name|String
name|getRel
parameter_list|()
function_decl|;
comment|/** 	 * @param aRel - 	 *            the rel attribute value of the element link to set 	 */
DECL|method|setRel
specifier|public
name|void
name|setRel
parameter_list|(
name|String
name|aRel
parameter_list|)
function_decl|;
comment|/** 	 * @return - the title attribute value of the element link. 	 */
DECL|method|getTitle
specifier|public
name|String
name|getTitle
parameter_list|()
function_decl|;
comment|/** 	 * @param aTitle - 	 *            the title attribute value of the element link to set 	 */
DECL|method|setTitle
specifier|public
name|void
name|setTitle
parameter_list|(
name|String
name|aTitle
parameter_list|)
function_decl|;
comment|/** 	 * @return - the type attribute value of the element link. 	 */
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
function_decl|;
comment|/** 	 * @param aType - 	 *            the type attribute value of the element link. 	 */
DECL|method|setType
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|aType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

