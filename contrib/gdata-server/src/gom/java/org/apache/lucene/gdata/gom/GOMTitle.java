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
comment|/**  *   * The "atom:title" element is a Text construct that conveys a human-readable  * title for an entry or feed.  *   *<pre>  *  atomTitle = element atom:title { atomTextConstruct }  *</pre>  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMTitle
specifier|public
interface|interface
name|GOMTitle
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
literal|"title"
decl_stmt|;
comment|/** 	 * @return - the content type attribute value as a {@link ContentType} 	 * @see ContentType 	 */
DECL|method|getContentType
specifier|public
specifier|abstract
name|ContentType
name|getContentType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

