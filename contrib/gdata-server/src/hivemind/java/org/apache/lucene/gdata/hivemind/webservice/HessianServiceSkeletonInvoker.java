begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.hivemind.webservice
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|hivemind
operator|.
name|webservice
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_comment
comment|/**  * Internal invoker interface to support more than one version of the protocol.<b>Currently only Version 3.0.20 is supported.<b>  *   * @author Simon Willnauer  *   */
end_comment

begin_interface
DECL|interface|HessianServiceSkeletonInvoker
specifier|public
interface|interface
name|HessianServiceSkeletonInvoker
block|{
comment|/**      * @param arg0 - httpServletRequest to access the input stream for processing      * @param arg1 - httpServletResponse to access the output stream for processing      * @throws IOException - if reading or writeing causes an IOException      * @throws Throwable - if the Hessian Impl. causes an error      */
DECL|method|invoke
specifier|public
specifier|abstract
name|void
name|invoke
parameter_list|(
name|HttpServletRequest
name|arg0
parameter_list|,
name|HttpServletResponse
name|arg1
parameter_list|)
throws|throws
name|IOException
throws|,
name|Throwable
function_decl|;
block|}
end_interface

end_unit

