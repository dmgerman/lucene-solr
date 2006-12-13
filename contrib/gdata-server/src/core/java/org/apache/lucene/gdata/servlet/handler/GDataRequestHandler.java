begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.servlet.handler
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|servlet
operator|.
name|handler
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
name|ServletException
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
comment|/**   *    * Based on the Command pattern [GoF], the Command and Controller Strategy   * suggests providing a generic interface to the handler components to which the   * controller may delegate responsibility, minimizing the coupling among these   * components.   *    * Adding to or changing the work that needs to be completed by these handlers   * does not require any changes to the interface between the controller and the   * handlers, but rather to the type and/or content of the commands. This provides   * a flexible and easily extensible mechanism for developers to add request   * handling behaviors.   *    * The controller invokes the processRequest method from the corresponding servlet<i>doXXX</i>   * method to delegate the request to the handler.   *     *    * @author Simon Willnauer   *    */
end_comment

begin_interface
DECL|interface|GDataRequestHandler
specifier|public
interface|interface
name|GDataRequestHandler
block|{
comment|/**       * Processes the GDATA Client request       *        * @param request - the client request to be processed       * @param response - the response to the client request       * @throws ServletException - if a servlet exception is thrown by the request or response         * @throws IOException -  if an input/output error occurs due to accessing an IO steam       */
DECL|method|processRequest
specifier|public
specifier|abstract
name|void
name|processRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

