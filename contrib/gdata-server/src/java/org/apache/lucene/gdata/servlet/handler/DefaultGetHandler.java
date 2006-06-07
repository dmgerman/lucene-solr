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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|GDataRequestException
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
name|Service
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
name|ServiceException
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
name|GDataRequest
operator|.
name|GDataRequestType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import

begin_comment
comment|/**   * Default Handler implementation. This handler processes the incoming   * {@link org.apache.lucene.gdata.server.GDataRequest} and retrieves the   * requested feed from the underlaying storage.   *<p>   * This hander also processes search queries and retrives the search hits from   * the underlaying search component. The user query will be accessed via the   * {@link org.apache.lucene.gdata.server.GDataRequest} instance passed to the   * {@link Service} class.   *</p>   *    *    * @author Simon Willnauer   *    */
end_comment

begin_class
DECL|class|DefaultGetHandler
specifier|public
class|class
name|DefaultGetHandler
extends|extends
name|AbstractGdataRequestHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DefaultGetHandler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**       * @see org.apache.lucene.gdata.servlet.handler.AbstractGdataRequestHandler#processRequest(javax.servlet.http.HttpServletRequest,       *      javax.servlet.http.HttpServletResponse)       */
annotation|@
name|Override
DECL|method|processRequest
specifier|public
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
name|IOException
throws|,
name|ServletException
block|{
try|try
block|{
name|initializeRequestHandler
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|GDataRequestType
operator|.
name|GET
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GDataRequestException
name|e
parameter_list|)
block|{
name|sendError
argument_list|()
expr_stmt|;
return|return;
block|}
name|Service
name|service
init|=
name|getService
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|info
argument_list|(
literal|"Requested output formate: "
operator|+
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|feedResponse
operator|.
name|setOutputFormat
argument_list|(
name|this
operator|.
name|feedRequest
operator|.
name|getRequestedResponseFormat
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|feedRequest
operator|.
name|isFeedRequested
argument_list|()
condition|)
block|{
name|BaseFeed
name|feed
init|=
name|service
operator|.
name|getFeed
argument_list|(
name|this
operator|.
name|feedRequest
argument_list|,
name|this
operator|.
name|feedResponse
argument_list|)
decl_stmt|;
name|this
operator|.
name|feedResponse
operator|.
name|sendResponse
argument_list|(
name|feed
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BaseEntry
name|entry
init|=
name|service
operator|.
name|getSingleEntry
argument_list|(
name|this
operator|.
name|feedRequest
argument_list|,
name|this
operator|.
name|feedResponse
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|feedResponse
operator|.
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
name|sendError
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|feedResponse
operator|.
name|sendResponse
argument_list|(
name|entry
argument_list|,
name|this
operator|.
name|feedRequest
operator|.
name|getExtensionProfile
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ServiceException
name|e
parameter_list|)
block|{
comment|// TODO handle exceptions to send exact
comment|// response
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not process GetFeed request - "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|feedResponse
operator|.
name|setError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
comment|// TODO
comment|// change
comment|// this
name|sendError
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

