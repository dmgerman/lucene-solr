begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.gom.core.extension
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
operator|.
name|core
operator|.
name|extension
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
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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
name|GOMExtension
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
name|GOMNamespace
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|GOMExtensionFactory
specifier|public
interface|interface
name|GOMExtensionFactory
block|{
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
DECL|method|canHandleExtensionElement
specifier|public
specifier|abstract
name|GOMExtension
name|canHandleExtensionElement
parameter_list|(
name|QName
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

