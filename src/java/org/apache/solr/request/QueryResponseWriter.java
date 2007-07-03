begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|plugin
operator|.
name|NamedListInitializedPlugin
import|;
end_import

begin_comment
comment|/**  * Implementations of<code>QueryResponseWriter</code> are used to format responses to query requests.  *  * Different<code>QueryResponseWriter</code>s are registered with the<code>SolrCore</code>.  * One way to register a QueryResponseWriter with the core is thorugh the<code>solrconfig.xml</code> file.  *<p>  * Example<code>solrconfig.xml</code> entry to register a<code>QueryResponseWRiter</code> implementation to  * handle all queries with a writer type of "simple":  *<p>  *<code>  *&lt;queryResponseWriter name="simple" class="foo.SimpleResponseWriter" /&gt;  *</code>  *<p>  * A single instance of any registered QueryResponseWriter is created  * via the default constructor and is reused for all relevant queries.  *  * @author yonik  * @version $Id$  */
end_comment

begin_interface
DECL|interface|QueryResponseWriter
specifier|public
interface|interface
name|QueryResponseWriter
extends|extends
name|NamedListInitializedPlugin
block|{
DECL|field|CONTENT_TYPE_XML_UTF8
specifier|public
specifier|static
name|String
name|CONTENT_TYPE_XML_UTF8
init|=
literal|"text/xml; charset=UTF-8"
decl_stmt|;
DECL|field|CONTENT_TYPE_TEXT_UTF8
specifier|public
specifier|static
name|String
name|CONTENT_TYPE_TEXT_UTF8
init|=
literal|"text/plain; charset=UTF-8"
decl_stmt|;
DECL|field|CONTENT_TYPE_TEXT_ASCII
specifier|public
specifier|static
name|String
name|CONTENT_TYPE_TEXT_ASCII
init|=
literal|"text/plain; charset=US-ASCII"
decl_stmt|;
comment|/**    * Write a SolrQueryResponse, this method must be thread save.    *    *<p>    * Information about the request (in particular: formating options) may be     * obtained from<code>req</code> but the dominant source of information     * should be<code>rsp</code>.    *<p>    * There are no mandatory actions that write must perform.    * An empty write implementation would fulfill    * all interface obligations.    *</p>     */
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
function_decl|;
comment|/**     * Return the applicable Content Type for a request, this method     * must be thread safe.    *    *<p>    * QueryResponseWriter's must implement this method to return a valid     * HTTP Content-Type header for the request, that will logically     * correspond with the output produced by the write method.    *</p>    * @return a Content-Type string, which may not be null.    */
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
function_decl|;
comment|/**<code>init</code> will be called just once, immediately after creation.    *<p>The args are user-level initialization parameters that    * may be specified when declaring a response writer in    * solrconfig.xml    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

