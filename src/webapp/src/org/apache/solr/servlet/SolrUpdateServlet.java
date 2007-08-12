begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
DECL|package|org.apache.solr.servlet
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|XmlUpdateRequestHandler
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
name|request
operator|.
name|QueryResponseWriter
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
name|request
operator|.
name|XMLResponseWriter
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|SolrUpdateServlet
specifier|public
class|class
name|SolrUpdateServlet
extends|extends
name|HttpServlet
block|{
DECL|field|log
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SolrUpdateServlet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|legacyUpdateHandler
name|XmlUpdateRequestHandler
name|legacyUpdateHandler
decl_stmt|;
DECL|field|xmlResponseWriter
name|XMLResponseWriter
name|xmlResponseWriter
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|legacyUpdateHandler
operator|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
expr_stmt|;
name|legacyUpdateHandler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"SolrUpdateServlet.init() done"
argument_list|)
expr_stmt|;
block|}
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
name|ServletException
throws|,
name|IOException
block|{
name|BufferedReader
name|requestReader
init|=
name|request
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|QueryResponseWriter
operator|.
name|CONTENT_TYPE_XML_UTF8
argument_list|)
expr_stmt|;
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|legacyUpdateHandler
operator|.
name|doLegacyUpdate
argument_list|(
name|requestReader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

