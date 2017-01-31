begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.api
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**The interface that is implemented by a request handler to support the V2 end point  *  */
end_comment

begin_interface
DECL|interface|ApiSupport
specifier|public
interface|interface
name|ApiSupport
block|{
comment|/**It is possible to support multiple v2 apis by a single requesthandler    *    * @return the list of v2 api implementations    */
DECL|method|getApis
name|Collection
argument_list|<
name|Api
argument_list|>
name|getApis
parameter_list|()
function_decl|;
comment|/**Whether this should be made available at the regular legacy path    */
DECL|method|registerV1
specifier|default
name|Boolean
name|registerV1
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
comment|/**Whether this request handler must be made available at the /v2/ path    */
DECL|method|registerV2
specifier|default
name|Boolean
name|registerV2
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
block|}
end_interface

end_unit

