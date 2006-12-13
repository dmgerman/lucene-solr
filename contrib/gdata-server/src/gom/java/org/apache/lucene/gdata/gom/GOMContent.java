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
comment|/**  *<p>  * GOMContent represents the atom:content element.  *</p>  * The "atom:content" element either contains or links to the content of the  * entry. The content of atom:content is Language-Sensitive.  *   *<pre>  *   atomInlineTextContent =  *   element atom:content {  *   atomCommonAttributes,  *   attribute type {&quot;text&quot; |&quot;html&quot; }?,  *   (text)*  *   }  *    *   atomInlineXHTMLContent =  *   element atom:content {  *   atomCommonAttributes,  *   attribute type {&quot;xhtml&quot; },  *   xhtmlDiv  *   }  *    *   atomInlineOtherContent =  *   element atom:content {  *   atomCommonAttributes,  *   attribute type { atomMediaType }?,  *   (text|anyElement)*  *   }  *    *    *   atomOutOfLineContent =  *   element atom:content {  *   atomCommonAttributes,  *   attribute type { atomMediaType }?,  *   attribute src { atomUri },  *   empty  *   }  *    *   atomContent = atomInlineTextContent  *   | atomInlineXHTMLContent  *    *   | atomInlineOtherContent  *   | atomOutOfLineContent  *</pre>  *   * @author Simon Willnauer  * @see org.apache.lucene.gdata.gom.GOMTextConstruct  *  *   */
end_comment

begin_interface
DECL|interface|GOMContent
specifier|public
interface|interface
name|GOMContent
extends|extends
name|GOMTextConstruct
block|{
comment|/** 	 * Atom local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"content"
decl_stmt|;
comment|/** 	 * RSS local name for the xml element 	 */
DECL|field|LOCAL_NAME_RSS
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_NAME_RSS
init|=
literal|"description"
decl_stmt|;
comment|/** 	 * The src attribute value 	 * @return - the value of the src attribute 	 */
DECL|method|getSrc
specifier|public
specifier|abstract
name|String
name|getSrc
parameter_list|()
function_decl|;
comment|/** 	 * The src attribute value 	 * @param aSrc - the src attribute value to set 	 */
DECL|method|setSrc
specifier|public
specifier|abstract
name|void
name|setSrc
parameter_list|(
name|String
name|aSrc
parameter_list|)
function_decl|;
comment|/** 	 * The contents abstract media type 	 * @param aMediaType -  	 */
DECL|method|setAtomMediaType
specifier|public
specifier|abstract
name|void
name|setAtomMediaType
parameter_list|(
name|AtomMediaType
name|aMediaType
parameter_list|)
function_decl|;
comment|/** 	 * @return - the atom media type of the content element 	 * @see AtomMediaType 	 */
DECL|method|getAtomMediaType
specifier|public
specifier|abstract
name|AtomMediaType
name|getAtomMediaType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

