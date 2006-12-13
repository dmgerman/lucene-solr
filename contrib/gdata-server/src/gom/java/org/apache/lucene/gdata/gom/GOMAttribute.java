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
comment|/**  * This class is a object representation of a XML Attribute used in the  * GO-Model.  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMAttribute
specifier|public
interface|interface
name|GOMAttribute
extends|extends
name|GOMXmlEntity
block|{
comment|/** 	 *  	 * @return<code>true</code> if and only if this attribute has the default 	 *         namespace, otherwise<code>false</code> 	 */
DECL|method|hasDefaultNamespace
specifier|public
specifier|abstract
name|boolean
name|hasDefaultNamespace
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

