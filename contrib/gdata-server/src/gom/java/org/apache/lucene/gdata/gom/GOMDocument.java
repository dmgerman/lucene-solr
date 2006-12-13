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
name|writer
operator|.
name|GOMOutputWriter
import|;
end_import

begin_comment
comment|/**  *<p>  * GOMDocument acts as a container for GOMElements to render the containing  * GOMElement as a valid xml document. This class renderes the  *   *<pre>  *&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;  *</pre>  *   * header to the outputstream before the containing element will be rendered.  *</p>  *   * @author Simon Willnauer  *   * @param<T>  */
end_comment

begin_interface
DECL|interface|GOMDocument
specifier|public
interface|interface
name|GOMDocument
parameter_list|<
name|T
extends|extends
name|GOMElement
parameter_list|>
block|{
comment|/** 	 * setter for the root element of the xml e.g GOMDocument 	 *  	 * @param aRootElement - 	 *            the root element to set 	 */
DECL|method|setRootElement
specifier|public
specifier|abstract
name|void
name|setRootElement
parameter_list|(
name|T
name|aRootElement
parameter_list|)
function_decl|;
comment|/** 	 * Getter for the root element of the xml e.g GOMDocument 	 *  	 * @return - the root elmenent 	 */
DECL|method|getRootElement
specifier|public
specifier|abstract
name|T
name|getRootElement
parameter_list|()
function_decl|;
comment|/** 	 * Sets the xml version 	 *  	 * @param aVersion - 	 *            the version string 	 */
DECL|method|setVersion
specifier|public
specifier|abstract
name|void
name|setVersion
parameter_list|(
name|String
name|aVersion
parameter_list|)
function_decl|;
comment|/** 	 * Gets the xml version 	 *  	 * @return - the xml version string 	 */
DECL|method|getVersion
specifier|public
specifier|abstract
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/** 	 * Gets the xml charset encoding 	 *  	 * @return - the specified char encoding 	 */
DECL|method|getCharacterEncoding
specifier|public
specifier|abstract
name|String
name|getCharacterEncoding
parameter_list|()
function_decl|;
comment|/** 	 * Sets the xml charset encoding 	 *  	 * @param aEncoding - 	 *            the charset encoding to set 	 */
DECL|method|setCharacterEncoding
specifier|public
specifier|abstract
name|void
name|setCharacterEncoding
parameter_list|(
name|String
name|aEncoding
parameter_list|)
function_decl|;
comment|/** 	 * Generates a complete xml document starting with the header followed by 	 * the output of the specified root element in the ATOM 1.0 formate.  	 *  	 * @param aStreamWriter - 	 *            the {@link GOMOutputWriter} implementation to write the output 	 * @throws XMLStreamException - 	 *             if the {@link GOMOutputWriter} throws an exception 	 */
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
comment|/** 	 * 	 * Generates a complete xml document starting with the header followed by 	 * the output of the specified root element in the RSS 2.0 formate.  	 *  	 * @param aStreamWriter - 	 *            the {@link GOMOutputWriter} implementation to write the output 	 * @throws XMLStreamException - 	 *             if the {@link GOMOutputWriter} throws an exception 	 */
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
block|}
end_interface

end_unit

