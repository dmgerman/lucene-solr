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
name|stream
operator|.
name|XMLStreamException
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
name|gom
operator|.
name|core
operator|.
name|AtomParser
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
name|gom
operator|.
name|writer
operator|.
name|GOMOutputWriter
import|;
end_import

begin_comment
comment|/**  * Abstract interface which should be assignable from all classes representing  * xml elements within the GData Object Model.  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMElement
specifier|public
specifier|abstract
interface|interface
name|GOMElement
extends|extends
name|GOMXmlEntity
extends|,
name|AtomParser
block|{
comment|/** 	 *<code>xml:lang</code> attribute localpart 	 */
DECL|field|XML_LANG
specifier|public
specifier|static
specifier|final
name|String
name|XML_LANG
init|=
literal|"lang"
decl_stmt|;
comment|/** 	 *<code>xml:base</code> attribute localpart 	 */
DECL|field|XML_BASE
specifier|public
specifier|static
specifier|final
name|String
name|XML_BASE
init|=
literal|"base"
decl_stmt|;
comment|/** 	 *  	 * @return the xml:base attribute value 	 */
DECL|method|getXmlBase
specifier|public
specifier|abstract
name|String
name|getXmlBase
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return the xml:lang attribute value 	 */
DECL|method|getXmlLang
specifier|public
specifier|abstract
name|String
name|getXmlLang
parameter_list|()
function_decl|;
comment|/** 	 * Generates the xml element represented by this class in the ATOM 1.0 	 * formate. 	 *  	 * @param aStreamWriter - 	 *            the {@link GOMOutputWriter} implementation to write the output 	 * @throws XMLStreamException - 	 *             if the {@link GOMOutputWriter} throws an exception 	 */
DECL|method|writeAtomOutput
specifier|public
specifier|abstract
name|void
name|writeAtomOutput
parameter_list|(
specifier|final
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
function_decl|;
comment|/** 	 * Generates the xml element represented by this class in the RSS 2.0 	 * formate. 	 *  	 * @param aStreamWriter - 	 *            the {@link GOMOutputWriter} implementation to write the output 	 * @throws XMLStreamException - 	 *             if the {@link GOMOutputWriter} throws an exception 	 */
DECL|method|writeRssOutput
specifier|public
specifier|abstract
name|void
name|writeRssOutput
parameter_list|(
specifier|final
name|GOMOutputWriter
name|aStreamWriter
parameter_list|)
throws|throws
name|XMLStreamException
function_decl|;
comment|/** 	 * Generates the xml element represented by this class in the RSS 2.0 	 * formate using the parameter rssName as the element local name 	 *  	 * @param rssName - 	 *            the local name to render the element 	 * @param aStreamWriter - 	 *            the {@link GOMOutputWriter} implementation to write the output 	 * @throws XMLStreamException - 	 *             if the {@link GOMOutputWriter} throws an exception 	 */
DECL|method|writeRssOutput
specifier|public
specifier|abstract
name|void
name|writeRssOutput
parameter_list|(
specifier|final
name|GOMOutputWriter
name|aStreamWriter
parameter_list|,
name|String
name|rssName
parameter_list|)
throws|throws
name|XMLStreamException
function_decl|;
block|}
end_interface

end_unit

