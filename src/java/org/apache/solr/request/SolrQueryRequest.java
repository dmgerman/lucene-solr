begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|search
operator|.
name|SolrIndexSearcher
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
name|schema
operator|.
name|IndexSchema
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
name|SolrCore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>Container for a request to execute a query.</p>  *<p><code>SolrQueryRequest</code> is not thread safe.</p>  *   * @author yonik  * @version $Id$  */
end_comment

begin_interface
DECL|interface|SolrQueryRequest
specifier|public
interface|interface
name|SolrQueryRequest
block|{
comment|/** returns the current request parameters */
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
function_decl|;
comment|/** Change the parameters for this request.  This does not affect    *  the original parameters returned by getOriginalParams()    */
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|SolrParams
name|params
parameter_list|)
function_decl|;
comment|/** Returns the original request parameters.  As this    * does not normally include configured defaults    * it's more suitable for logging.    */
DECL|method|getOriginalParams
specifier|public
name|SolrParams
name|getOriginalParams
parameter_list|()
function_decl|;
comment|/**    * Generic information associated with this request that may be both read and updated.    */
DECL|method|getContext
specifier|public
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|getContext
parameter_list|()
function_decl|;
comment|/**    * This method should be called when all uses of this request are    * finished, so that resources can be freed.    */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Returns the input parameter value for the specified name    * @return the value, or the first value if the parameter was    * specified more then once; may be null.    */
annotation|@
name|Deprecated
DECL|method|getParam
specifier|public
name|String
name|getParam
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the input parameter values for the specified name    * @return the values; may be null or empty depending on implementation    */
annotation|@
name|Deprecated
DECL|method|getParams
specifier|public
name|String
index|[]
name|getParams
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the primary query string parameter of the request    */
annotation|@
name|Deprecated
DECL|method|getQueryString
specifier|public
name|String
name|getQueryString
parameter_list|()
function_decl|;
comment|/**    * Signifies the syntax and the handler that should be used    * to execute this query.    */
annotation|@
name|Deprecated
DECL|method|getQueryType
specifier|public
name|String
name|getQueryType
parameter_list|()
function_decl|;
comment|/** starting position in matches to return to client */
annotation|@
name|Deprecated
DECL|method|getStart
specifier|public
name|int
name|getStart
parameter_list|()
function_decl|;
comment|/** number of matching documents to return */
annotation|@
name|Deprecated
DECL|method|getLimit
specifier|public
name|int
name|getLimit
parameter_list|()
function_decl|;
comment|/** The start time of this request in milliseconds */
DECL|method|getStartTime
specifier|public
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/** The index searcher associated with this request */
DECL|method|getSearcher
specifier|public
name|SolrIndexSearcher
name|getSearcher
parameter_list|()
function_decl|;
comment|/** The solr core (coordinator, etc) associated with this request */
DECL|method|getCore
specifier|public
name|SolrCore
name|getCore
parameter_list|()
function_decl|;
comment|/** The index schema associated with this request */
DECL|method|getSchema
specifier|public
name|IndexSchema
name|getSchema
parameter_list|()
function_decl|;
comment|/**    * Returns a string representing all the important parameters.    * Suitable for logging.    */
DECL|method|getParamString
specifier|public
name|String
name|getParamString
parameter_list|()
function_decl|;
comment|/******   // Get the current elapsed time in milliseconds   public long getElapsedTime();   ******/
block|}
end_interface

end_unit

