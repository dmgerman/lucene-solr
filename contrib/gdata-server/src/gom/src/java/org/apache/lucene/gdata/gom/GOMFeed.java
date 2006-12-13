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
comment|/**  * Class representing the "atom:feed" element. The "atom:feed" element is the  * document (i.e., top-level) element of an Atom Feed Document, acting as a  * container for metadata and data associated with the feed. Its element  * children consist of metadata elements followed by zero or more atom:entry  * child elements.  *   *<pre>  *        atom:feed {  *        	atomCommonAttributes,   *         	(atomAuthor*&amp; atomCategory*&amp;  *        	atomContributor*&amp;  *         	atomGenerator?&amp; atomIcon?&amp;  *         	atomId&amp;   *         	atomLink*&amp;  *        	atomLogo?&amp;  *        	atomRights?&amp;  *        	atomSubtitle?&amp;  *        	atomTitle&amp;   *        	atomUpdated&amp;  *        	extensionElement*),  *        	 atomEntry* }  *</pre>  *   *   * @author Simon Willnauer  * @see org.apache.lucene.gdata.gom.GOMExtensible  * @see org.apache.lucene.gdata.gom.GOMExtension  * @see org.apache.lucene.gdata.gom.GOMDocument  */
end_comment

begin_interface
DECL|interface|GOMFeed
specifier|public
interface|interface
name|GOMFeed
extends|extends
name|GOMSource
extends|,
name|GOMExtensible
block|{
comment|/** 	 * Atom local name for the xml element 	 */
DECL|field|LOCALNAME
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME
init|=
literal|"feed"
decl_stmt|;
comment|/** 	 * RSS local name for the xml element 	 */
DECL|field|LOCALNAME_RSS
specifier|public
specifier|static
specifier|final
name|String
name|LOCALNAME_RSS
init|=
literal|"rss"
decl_stmt|;
comment|/** 	 * RSS channel localname as Rss starts with 	 *  	 *<pre> 	 *&lt;rss&gt;&lt;channel&gt; 	 *</pre> 	 */
DECL|field|RSS_CHANNEL_ELEMENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|RSS_CHANNEL_ELEMENT_NAME
init|=
literal|"channel"
decl_stmt|;
comment|/** 	 * this class can contain namespaces which will be rendered into the start 	 * element. 	 *  	 *<pre> 	 *&lt;feed xmlns:myNs=&quot;someNamespace&quot;&gt;&lt;/feed&gt; 	 *</pre> 	 *  	 * @param aNamespace - 	 *            a namespace to add 	 */
DECL|method|addNamespace
specifier|public
name|void
name|addNamespace
parameter_list|(
name|GOMNamespace
name|aNamespace
parameter_list|)
function_decl|;
comment|/** 	 * @return - all declared namespaces, excluding the default namespace, this 	 *         method will never return<code>null</code>. 	 * @see GOMFeed#getDefaultNamespace() 	 */
DECL|method|getNamespaces
specifier|public
name|List
argument_list|<
name|GOMNamespace
argument_list|>
name|getNamespaces
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return - a list of added entries, this method will never return 	 *<code>null</code>. 	 */
DECL|method|getEntries
specifier|public
name|List
argument_list|<
name|GOMEntry
argument_list|>
name|getEntries
parameter_list|()
function_decl|;
comment|/** 	 * @return - the OpenSearch namespace element<i>itemsPerPage</i> text 	 *         value. 	 */
DECL|method|getItemsPerPage
specifier|public
name|int
name|getItemsPerPage
parameter_list|()
function_decl|;
comment|/** 	 * @return - the OpenSearch namespace element<i>startIndex</i> text value. 	 */
DECL|method|getStartIndex
specifier|public
name|int
name|getStartIndex
parameter_list|()
function_decl|;
comment|/** 	 * @param aIndex - 	 *            the OpenSearch namespace element<i>startIndex</i> text value 	 *            as an integer. 	 */
DECL|method|setStartIndex
specifier|public
name|void
name|setStartIndex
parameter_list|(
name|int
name|aIndex
parameter_list|)
function_decl|;
comment|/** 	 * @param aInt - 	 *            the OpenSearch namespace element<i>itemsPerPage</i> text 	 *            value as an integer. 	 */
DECL|method|setItemsPerPage
specifier|public
name|void
name|setItemsPerPage
parameter_list|(
name|int
name|aInt
parameter_list|)
function_decl|;
comment|/** 	 *  	 * @return the default namespace - this will always be 	 *         {@link GOMNamespace#ATOM_NAMESPACE} 	 */
DECL|method|getDefaultNamespace
specifier|public
name|GOMNamespace
name|getDefaultNamespace
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

