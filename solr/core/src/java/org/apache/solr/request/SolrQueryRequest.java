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
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|ContentStream
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|RTimerTree
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
comment|/**  *<p>Container for a request to execute a query.</p>  *<p><code>SolrQueryRequest</code> is not thread safe.</p>  *   *  */
end_comment

begin_interface
DECL|interface|SolrQueryRequest
specifier|public
interface|interface
name|SolrQueryRequest
extends|extends
name|AutoCloseable
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
comment|/** A Collection of ContentStreams passed to the request    */
DECL|method|getContentStreams
specifier|public
name|Iterable
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
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
comment|/** The start time of this request in milliseconds.    * Use this only if you need the absolute system time at the start of the request,    * getRequestTimer() provides a more accurate mechanism for timing purposes.    */
DECL|method|getStartTime
specifier|public
name|long
name|getStartTime
parameter_list|()
function_decl|;
comment|/** The timer for this request, created when the request started being processed */
DECL|method|getRequestTimer
specifier|public
name|RTimerTree
name|getRequestTimer
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
comment|/** The schema snapshot from core.getLatestSchema() at request creation. */
DECL|method|getSchema
specifier|public
name|IndexSchema
name|getSchema
parameter_list|()
function_decl|;
comment|/** Replaces the current schema snapshot with the latest from the core. */
DECL|method|updateSchemaToLatest
specifier|public
name|void
name|updateSchemaToLatest
parameter_list|()
function_decl|;
comment|/**    * Returns a string representing all the important parameters.    * Suitable for logging.    */
DECL|method|getParamString
specifier|public
name|String
name|getParamString
parameter_list|()
function_decl|;
comment|/** Returns any associated JSON (or null if none) in deserialized generic form.    * Java classes used to represent the JSON are as follows: Map, List, String, Long, Double, Boolean    */
DECL|method|getJSON
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getJSON
parameter_list|()
function_decl|;
DECL|method|setJSON
specifier|public
name|void
name|setJSON
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
parameter_list|)
function_decl|;
DECL|method|getUserPrincipal
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

