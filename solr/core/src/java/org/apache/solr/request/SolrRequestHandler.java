begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|core
operator|.
name|SolrInfoMBean
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
name|response
operator|.
name|SolrQueryResponse
import|;
end_import

begin_comment
comment|/**  * Implementations of<code>SolrRequestHandler</code> are called to handle query requests.  *  * Different<code>SolrRequestHandler</code>s are registered with the<code>SolrCore</code>.  * One way to register a SolrRequestHandler with the core is thorugh the<code>solrconfig.xml</code> file.  *<p>  * Example<code>solrconfig.xml</code> entry to register a<code>SolrRequestHandler</code> implementation to  * handle all queries with a Request Handler of "/test":  *<p>  *<code>  *&lt;requestHandler name="/test" class="solr.tst.TestRequestHandler" /&gt;  *</code>  *<p>  * A single instance of any registered SolrRequestHandler is created  * via the default constructor and is reused for all relevant queries.  *  *  */
end_comment

begin_interface
DECL|interface|SolrRequestHandler
specifier|public
interface|interface
name|SolrRequestHandler
extends|extends
name|SolrInfoMBean
block|{
comment|/**<code>init</code> will be called just once, immediately after creation.    *<p>The args are user-level initialization parameters that    * may be specified when declaring a request handler in    * solrconfig.xml    */
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
function_decl|;
comment|/**    * Handles a query request, this method must be thread safe.    *<p>    * Information about the request may be obtained from<code>req</code> and    * response information may be set using<code>rsp</code>.    *<p>    * There are no mandatory actions that handleRequest must perform.    * An empty handleRequest implementation would fulfill    * all interface obligations.    */
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
function_decl|;
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"requestHandler"
decl_stmt|;
block|}
end_interface

end_unit

