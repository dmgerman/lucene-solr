begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|InputStream
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|PrintWriter
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringEscapeUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|CoreContainer
import|;
end_import

begin_comment
comment|/**  * A simple servlet to load the Solr Admin UI  *   * @since solr 4.0  */
end_comment

begin_class
DECL|class|LoadAdminUiServlet
specifier|public
specifier|final
class|class
name|LoadAdminUiServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|doGet
specifier|public
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|InputStream
name|in
init|=
name|getServletContext
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/admin.html"
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
comment|// This attribute is set by the SolrDispatchFilter
name|CoreContainer
name|cores
init|=
operator|(
name|CoreContainer
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"org.apache.solr.CoreContainer"
argument_list|)
decl_stmt|;
name|String
name|html
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|in
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|String
index|[]
name|search
init|=
operator|new
name|String
index|[]
block|{
literal|"${contextPath}"
block|,
literal|"${adminPath}"
block|}
decl_stmt|;
name|String
index|[]
name|replace
init|=
operator|new
name|String
index|[]
block|{
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
argument_list|)
block|,
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|cores
operator|.
name|getAdminPath
argument_list|()
argument_list|)
block|}
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|replaceEach
argument_list|(
name|html
argument_list|,
name|search
argument_list|,
name|replace
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|println
argument_list|(
literal|"solr"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doPost
specifier|public
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|doGet
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

