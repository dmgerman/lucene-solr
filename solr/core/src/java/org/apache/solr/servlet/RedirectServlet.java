begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.servlet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
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
name|ServletConfig
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
name|HttpServlet
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
comment|/**  * A Simple redirection servlet to help us deprecate old UI elements  */
end_comment

begin_class
DECL|class|RedirectServlet
specifier|public
class|class
name|RedirectServlet
extends|extends
name|HttpServlet
block|{
DECL|field|CONTEXT_KEY
specifier|static
specifier|final
name|String
name|CONTEXT_KEY
init|=
literal|"${context}"
decl_stmt|;
DECL|field|destination
name|String
name|destination
decl_stmt|;
DECL|field|code
name|int
name|code
init|=
name|HttpServletResponse
operator|.
name|SC_MOVED_PERMANENTLY
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|destination
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"destination"
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"RedirectServlet missing destination configuration"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"permanent"
argument_list|)
argument_list|)
condition|)
block|{
name|code
operator|=
name|HttpServletResponse
operator|.
name|SC_MOVED_TEMPORARILY
expr_stmt|;
block|}
comment|// Replace the context key
if|if
condition|(
name|destination
operator|.
name|startsWith
argument_list|(
name|CONTEXT_KEY
argument_list|)
condition|)
block|{
name|destination
operator|=
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getContextPath
argument_list|()
operator|+
name|destination
operator|.
name|substring
argument_list|(
name|CONTEXT_KEY
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|res
operator|.
name|setStatus
argument_list|(
name|code
argument_list|)
expr_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
literal|"Location"
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
DECL|method|doPost
specifier|public
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|doGet
argument_list|(
name|req
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

