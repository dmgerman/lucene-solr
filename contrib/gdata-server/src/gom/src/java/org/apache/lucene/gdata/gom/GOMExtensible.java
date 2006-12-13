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
name|extension
operator|.
name|GOMExtensionFactory
import|;
end_import

begin_comment
comment|/**  *<p>  * The Gdata Object Model describes an abstract object model for the gdata  * protocol. GData is supposed to be very flexible and extensible. Users should  * be able to extend {@link org.apache.lucene.gdata.gom.GOMFeed} and  * {@link org.apache.lucene.gdata.gom.GOMEntry} elements to create extensions  * and custom classes for their own model.  *</p>  *   *<p>  * This interface describes the extensible GOM entities.  *</p>  *   * @author Simon Willnauer  * @see org.apache.lucene.gdata.gom.GOMFeed  * @see org.apache.lucene.gdata.gom.GOMEntry  *   */
end_comment

begin_interface
DECL|interface|GOMExtensible
specifier|public
interface|interface
name|GOMExtensible
block|{
comment|//TODO add setter!
comment|//TODO add how to
comment|/** 	 * @return - a list of all extensions specified to the extended element 	 */
DECL|method|getExtensions
specifier|public
name|List
argument_list|<
name|GOMExtension
argument_list|>
name|getExtensions
parameter_list|()
function_decl|;
comment|/** 	 *  	 * @param factory - the extension factory to set 	 */
DECL|method|setExtensionFactory
specifier|public
name|void
name|setExtensionFactory
parameter_list|(
name|GOMExtensionFactory
name|factory
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

