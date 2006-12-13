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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *   *<p>  * The GOMEntry class represents a "atom:entry" element in the GData Object  * Model.  *</p>  *<p>  * The "atom:entry" element represents an individual entry, acting as a  * container for metadata and data associated with the entry. This element can  * appear as a child of the atom:feed element, or it can appear as the document  * (i.e., top-level) element of a stand-alone Atom Entry Document.  *</p>  *<p>  * RelaxNG Schema:  *</p>  *   *<pre>  *     atomEntry =  *     element atom:entry {  *     atomCommonAttributes,  *     (	atomAuthor*  *&amp; atomCategory*  *&amp; atomContent?  *&amp; atomContributor*  *&amp; atomId  *&amp; atomLink*  *&amp; atomPublished?  *&amp; atomRights?  *&amp; atomSource?  *&amp; atomSummary?  *&amp; atomTitle  *&amp; atomUpdated  *&amp; extensionElement*)  *     }  *</pre>  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMEntry
specifier|public
interface|interface
name|GOMEntry
extends|extends
name|GOMXmlEntity
extends|,
name|GOMElement
extends|,
name|GOMExtensible
block|{
comment|/** 	 * Atom 1.0 local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"entry"
decl_stmt|;
comment|/** 	 * RSS 2.0 local name for the xml element 	 */
DECL|field|LOCALNAME_RSS
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME_RSS
init|=
literal|"item"
decl_stmt|;
comment|/** 	 * @param aAuthor - 	 *            a author to add 	 * @see GOMAuthor 	 */
DECL|method|addAuthor
specifier|public
specifier|abstract
name|void
name|addAuthor
parameter_list|(
name|GOMAuthor
name|aAuthor
parameter_list|)
function_decl|;
comment|/** 	 * @param aCategory - 	 *            a category to add 	 * @see GOMCategory 	 */
DECL|method|addCategory
specifier|public
specifier|abstract
name|void
name|addCategory
parameter_list|(
name|GOMCategory
name|aCategory
parameter_list|)
function_decl|;
comment|/** 	 * @param aContributor - 	 *            a contributor to add 	 * @see GOMContributor 	 */
DECL|method|addContributor
specifier|public
specifier|abstract
name|void
name|addContributor
parameter_list|(
name|GOMContributor
name|aContributor
parameter_list|)
function_decl|;
comment|/** 	 * @param aLink - 	 *            a link to add 	 * @see GOMLink 	 */
DECL|method|addLink
specifier|public
specifier|abstract
name|void
name|addLink
parameter_list|(
name|GOMLink
name|aLink
parameter_list|)
function_decl|;
comment|/** 	 * @return - the entry author 	 * @see GOMAuthor 	 */
DECL|method|getAuthors
specifier|public
specifier|abstract
name|List
argument_list|<
name|GOMAuthor
argument_list|>
name|getAuthors
parameter_list|()
function_decl|;
comment|/** 	 *  	 * This method returns all categories and will never return<code>null</code> 	 *  	 * @return - a list of categories 	 * @see GOMCategory 	 */
DECL|method|getCategories
specifier|public
specifier|abstract
name|List
argument_list|<
name|GOMCategory
argument_list|>
name|getCategories
parameter_list|()
function_decl|;
comment|/** 	 *  	 * This method returns all contributors and will never return<code>null</code> 	 *  	 * @return - a list of contributors 	 * @see GOMContributor 	 */
DECL|method|getContributor
specifier|public
specifier|abstract
name|List
argument_list|<
name|GOMContributor
argument_list|>
name|getContributor
parameter_list|()
function_decl|;
comment|/** 	 * @return - the feed id 	 * @see GOMId 	 */
DECL|method|getId
specifier|public
specifier|abstract
name|GOMId
name|getId
parameter_list|()
function_decl|;
comment|/** 	 * @param aId - 	 *            the entry id 	 * @see GOMId 	 */
DECL|method|setId
specifier|public
specifier|abstract
name|void
name|setId
parameter_list|(
name|GOMId
name|aId
parameter_list|)
function_decl|;
comment|/** 	 * @return - the entry rights 	 * @see GOMRights 	 */
DECL|method|getRights
specifier|public
specifier|abstract
name|GOMRights
name|getRights
parameter_list|()
function_decl|;
comment|/** 	 * @param aRights - 	 *            the GOMRights to set 	 * @see GOMRights 	 */
DECL|method|setRights
specifier|public
specifier|abstract
name|void
name|setRights
parameter_list|(
name|GOMRights
name|aRights
parameter_list|)
function_decl|;
comment|/** 	 * @return - the entries title 	 * @see GOMTitle 	 */
DECL|method|getTitle
specifier|public
specifier|abstract
name|GOMTitle
name|getTitle
parameter_list|()
function_decl|;
comment|/** 	 * @param aTitle - 	 *            the title to set 	 * @see GOMTitle 	 */
DECL|method|setTitle
specifier|public
specifier|abstract
name|void
name|setTitle
parameter_list|(
name|GOMTitle
name|aTitle
parameter_list|)
function_decl|;
comment|/** 	 * @return - the last updated element 	 * @see GOMUpdated 	 */
DECL|method|getUpdated
specifier|public
specifier|abstract
name|GOMUpdated
name|getUpdated
parameter_list|()
function_decl|;
comment|/** 	 * @param aUpdated - 	 *            the updated element to set 	 * @see GOMUpdated 	 */
DECL|method|setUpdated
specifier|public
specifier|abstract
name|void
name|setUpdated
parameter_list|(
name|GOMUpdated
name|aUpdated
parameter_list|)
function_decl|;
comment|/** 	 *  	 * This method returns all links and will never return<code>null</code> 	 *  	 * @return - a list of links 	 * @see GOMLink 	 */
DECL|method|getLinks
specifier|public
specifier|abstract
name|List
argument_list|<
name|GOMLink
argument_list|>
name|getLinks
parameter_list|()
function_decl|;
comment|/** 	 * @param aSummary - 	 *            a summary to set 	 * @see GOMSummary 	 */
DECL|method|setSummary
specifier|public
specifier|abstract
name|void
name|setSummary
parameter_list|(
name|GOMSummary
name|aSummary
parameter_list|)
function_decl|;
comment|/** 	 * @return - the summary 	 * @see GOMSummary 	 */
DECL|method|getSummary
specifier|public
specifier|abstract
name|GOMSummary
name|getSummary
parameter_list|()
function_decl|;
comment|/** 	 * @param aSource - 	 *            the source to set 	 * @see GOMSource 	 */
DECL|method|setSource
specifier|public
specifier|abstract
name|void
name|setSource
parameter_list|(
name|GOMSource
name|aSource
parameter_list|)
function_decl|;
comment|/** 	 * @return - the entry source 	 * @see GOMSource 	 */
DECL|method|getSource
specifier|public
specifier|abstract
name|GOMSource
name|getSource
parameter_list|()
function_decl|;
comment|/** 	 * @param aPublished - 	 *            the published element to set 	 * @see GOMPublished 	 */
DECL|method|setPublished
specifier|public
specifier|abstract
name|void
name|setPublished
parameter_list|(
name|GOMPublished
name|aPublished
parameter_list|)
function_decl|;
comment|/** 	 * @return - the published element 	 * @see GOMPublished 	 */
DECL|method|getPublished
specifier|public
specifier|abstract
name|GOMPublished
name|getPublished
parameter_list|()
function_decl|;
comment|/** 	 * @return - the content element 	 * @see GOMContent 	 */
DECL|method|getContent
specifier|public
specifier|abstract
name|GOMContent
name|getContent
parameter_list|()
function_decl|;
comment|/** 	 * @param content - 	 *            the content to set 	 * @see GOMContent 	 */
DECL|method|setContent
specifier|public
specifier|abstract
name|void
name|setContent
parameter_list|(
name|GOMContent
name|content
parameter_list|)
function_decl|;
comment|/** 	 * @param aNamespace - 	 *            a Namespace to add 	 * @see GOMNamespace 	 */
DECL|method|addNamespace
specifier|public
specifier|abstract
name|void
name|addNamespace
parameter_list|(
name|GOMNamespace
name|aNamespace
parameter_list|)
function_decl|;
comment|/** 	 * @return - list of all namespaces - will never be null 	 * @see GOMNamespace 	 */
DECL|method|getNamespaces
specifier|public
specifier|abstract
name|List
argument_list|<
name|GOMNamespace
argument_list|>
name|getNamespaces
parameter_list|()
function_decl|;
comment|/** 	 * @return - the default namespace 	 * @see GOMNamespace 	 */
DECL|method|getDefaultNamespace
specifier|public
specifier|abstract
name|GOMNamespace
name|getDefaultNamespace
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

