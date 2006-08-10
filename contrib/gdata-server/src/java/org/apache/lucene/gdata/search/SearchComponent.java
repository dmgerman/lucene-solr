begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
package|;
end_package

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
name|server
operator|.
name|registry
operator|.
name|ProvidedService
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
name|server
operator|.
name|registry
operator|.
name|ServerComponent
import|;
end_import

begin_comment
comment|/**  * TODO document this when Search comes into play  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|SearchComponent
specifier|public
interface|interface
name|SearchComponent
extends|extends
name|ServerComponent
block|{
comment|/**      * TODO document this when Search comes into play      *       * @param service      *       * @return a GDataSearcher      */
DECL|method|getServiceSearcher
specifier|public
specifier|abstract
name|GDataSearcher
argument_list|<
name|String
argument_list|>
name|getServiceSearcher
parameter_list|(
name|ProvidedService
name|service
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

