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
comment|/**  * Class representing the "atom:generator" element. The "atom:generator"  * element's content identifies the agent used to generate a feed, for debugging  * and other purposes.  *   *<pre>  *   atomGenerator = element atom:generator {  *   atomCommonAttributes,  *   attribute uri { atomUri }?,  *   attribute version { text }?,  *   text  *   }  *</pre>  *   *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMGenerator
specifier|public
interface|interface
name|GOMGenerator
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
literal|"generator"
decl_stmt|;
comment|/** 	 * Sets the the atom:generator<i>uri</i> attribute value 	 *  	 * @param uri - 	 *            the generator<i>uri</i> attribute value to set 	 */
DECL|method|setUri
specifier|public
name|void
name|setUri
parameter_list|(
name|String
name|uri
parameter_list|)
function_decl|;
comment|/** 	 * Sets the the atom:generator<i>version</i> attribute value 	 *  	 * @param version - 	 *            the version value to set 	 */
DECL|method|setGeneratorVersion
specifier|public
name|void
name|setGeneratorVersion
parameter_list|(
name|String
name|version
parameter_list|)
function_decl|;
comment|/** 	 *  	 * @return - the atom:generator<i>version</i> attribute value 	 */
DECL|method|getGeneratorVersion
specifier|public
name|String
name|getGeneratorVersion
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @return - the atom:generator<i>uri</i> attribute value 	 */
DECL|method|getUri
specifier|public
name|String
name|getUri
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

