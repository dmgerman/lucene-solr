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
comment|/**  *   * GOMCategory type<br>  *<p>  * The<b>"category"</b> element conveys information about a category  * associated with an entry or feed. This specification assigns no meaning to  * the content (if any) of this element.  *</p>  *<p>  * RelaxNG Schema:  *</p>  *   *<pre>  *      atomCategory =  *      element atom:category {  *      	atomCommonAttributes,  *      	attribute term { text },  *      	attribute scheme { atomUri }?,  *      	attribute label { text }?,  *      	undefinedContent  *      }  *</pre>  *   *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMCategory
specifier|public
interface|interface
name|GOMCategory
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
literal|"category"
decl_stmt|;
comment|/** 	 * Attribute name (attribute term { text }) 	 */
DECL|field|TERM_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|TERM_ATTRIBUTE
init|=
literal|"term"
decl_stmt|;
comment|/** 	 * Attribute name (attribute label { text }) 	 */
DECL|field|LABLE_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|LABLE_ATTRIBUTE
init|=
literal|"label"
decl_stmt|;
comment|/** 	 * Attribute name (attribute scheme { atomUri }) 	 */
DECL|field|SCHEME_ATTRIBUTE
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME_ATTRIBUTE
init|=
literal|"scheme"
decl_stmt|;
comment|/** 	 * @param aTerm - 	 *            the attribute term { text } 	 */
DECL|method|setTerm
specifier|public
specifier|abstract
name|void
name|setTerm
parameter_list|(
name|String
name|aTerm
parameter_list|)
function_decl|;
comment|/** 	 * @param aLabel - 	 *            the attribute lable { text } 	 */
DECL|method|setLabel
specifier|public
specifier|abstract
name|void
name|setLabel
parameter_list|(
name|String
name|aLabel
parameter_list|)
function_decl|;
comment|/** 	 * @param aScheme - 	 *            the attribute scheme { atomUri } 	 */
DECL|method|setScheme
specifier|public
specifier|abstract
name|void
name|setScheme
parameter_list|(
name|String
name|aScheme
parameter_list|)
function_decl|;
comment|/** 	 * @return the attribute term { text } 	 */
DECL|method|getTerm
specifier|public
specifier|abstract
name|String
name|getTerm
parameter_list|()
function_decl|;
comment|/** 	 * @return the attribute scheme { atomUri } 	 */
DECL|method|getScheme
specifier|public
specifier|abstract
name|String
name|getScheme
parameter_list|()
function_decl|;
comment|/** 	 * @return the attribute lable { text } 	 */
DECL|method|getLabel
specifier|public
specifier|abstract
name|String
name|getLabel
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

