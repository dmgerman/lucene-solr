begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrRequestHandler
import|;
end_import

begin_comment
comment|/**An interface for RequestHandlers need to handle all paths under its registered path  */
end_comment

begin_interface
DECL|interface|NestedRequestHandler
specifier|public
interface|interface
name|NestedRequestHandler
block|{
comment|/** Return a RequestHandler to handle a subpath from the path this handler is registered.    */
DECL|method|getSubHandler
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|subPath
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

