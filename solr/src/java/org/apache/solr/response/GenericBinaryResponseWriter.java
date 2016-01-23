begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Writer
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
name|common
operator|.
name|SolrDocumentList
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
name|common
operator|.
name|SolrInputDocument
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
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  *   *   * A generic {@link QueryResponseWriter} implementation that requires a user to  * implement the  * {@link #getSingleResponseWriter(OutputStream, SolrQueryRequest, SolrQueryResponse)}  * that defines a {@link SingleResponseWriter} to handle the binary output.  *   * @since 1.5  * @version $Id$  *   */
end_comment

begin_class
DECL|class|GenericBinaryResponseWriter
specifier|public
specifier|abstract
class|class
name|GenericBinaryResponseWriter
extends|extends
name|BaseResponseWriter
implements|implements
name|BinaryQueryResponseWriter
block|{
comment|/**    *     * Writes the binary output data using the {@link SingleResponseWriter}    * provided by a call to    * {@link #getSingleResponseWriter(OutputStream, SolrQueryRequest, SolrQueryResponse)}    * .    *     * @param out    *          The {@link OutputStream} to write the binary data to.    * @param request    *          The provided {@link SolrQueryRequest}.    * @param response    *          The provided {@link SolrQueryResponse}.    */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|write
argument_list|(
name|getSingleResponseWriter
argument_list|(
name|out
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**    * Users of this class should implement this method to define a    * {@link SingleResponseWriter} responsible for writing the binary output    * given a {@link SolrDocumentList} or doc-by-doc, given a    * {@link SolrInputDocument}.    *     * @param out    *          The {@link OutputStream} to write the binary data response to.    * @param request    *          The provided {@link SolrQueryRequest}.    * @param response    *          The provided {@link SolrQueryResponse}.    * @return A {@link SingleResponseWriter} that will be used to generate the    *         response output from this {@link QueryResponseWriter}.    */
DECL|method|getSingleResponseWriter
specifier|public
specifier|abstract
name|SingleResponseWriter
name|getSingleResponseWriter
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
function_decl|;
comment|/**Just to throw Exception So that the eimplementing classes do not have to do the  same    */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"This is a binary writer , Cannot write to a characterstream"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

