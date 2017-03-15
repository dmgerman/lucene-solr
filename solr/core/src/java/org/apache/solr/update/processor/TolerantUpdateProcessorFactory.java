begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
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
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
name|request
operator|.
name|SolrQueryRequest
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributedUpdateProcessor
operator|.
name|DistribPhase
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
name|SolrCoreAware
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
operator|.
name|DistributingUpdateProcessorFactory
operator|.
name|DISTRIB_UPDATE_PARAM
import|;
end_import

begin_comment
comment|/**  *<p>   * Suppresses errors for individual add/delete commands within a single request.  * Instead of failing on the first error, at most<code>maxErrors</code> errors (or unlimited   * if<code>-1==maxErrors</code>) are logged and recorded the batch continues.   * The client will receive a<code>status==200</code> response, which includes a list of errors   * that were tolerated.  *</p>  *<p>  * If more then<code>maxErrors</code> occur, the first exception recorded will be re-thrown,   * Solr will respond with<code>status==5xx</code> or<code>status==4xx</code>   * (depending on the underlying exceptions) and it won't finish processing any more updates in the request.   * (ie: subsequent update commands in the request will not be processed even if they are valid).  *</p>  *   *<p>  *<code>maxErrors</code> is an int value that can be specified in the configuration and/or overridden   * per request. If unset, it will default to {@link Integer#MAX_VALUE}.  Specifying an explicit value   * of<code>-1</code> is supported as shorthand for {@link Integer#MAX_VALUE}, all other negative   * integer values are not supported.  *</p>  *<p>  * An example configuration would be:  *</p>  *<pre class="prettyprint">  *&lt;updateRequestProcessorChain name="tolerant-chain"&gt;  *&lt;processor class="solr.TolerantUpdateProcessorFactory"&gt;  *&lt;int name="maxErrors"&gt;10&lt;/int&gt;  *&lt;/processor&gt;  *&lt;processor class="solr.RunUpdateProcessorFactory" /&gt;  *&lt;/updateRequestProcessorChain&gt;  *   *</pre>  *   *<p>  * The<code>maxErrors</code> parameter in the above chain could be overwritten per request, for example:  *</p>  *<pre class="prettyprint">  * curl http://localhost:8983/update?update.chain=tolerant-chain&amp;maxErrors=100 -H "Content-Type: text/xml" -d @myfile.xml  *</pre>  *   *<p>  *<b>NOTE:</b> The behavior of this UpdateProcessofFactory in conjunction with indexing operations   * while a Shard Split is actively in progress is not well defined (or sufficiently tested).  Users   * of this update processor are encouraged to either disable it, or pause updates, while any shard   * splitting is in progress (see<a href="https://issues.apache.org/jira/browse/SOLR-8881">SOLR-8881</a>   * for more details.)  *</p>  */
end_comment

begin_class
DECL|class|TolerantUpdateProcessorFactory
specifier|public
class|class
name|TolerantUpdateProcessorFactory
extends|extends
name|UpdateRequestProcessorFactory
implements|implements
name|SolrCoreAware
implements|,
name|UpdateRequestProcessorFactory
operator|.
name|RunAlways
block|{
comment|/**    * Parameter that defines how many errors the UpdateRequestProcessor will tolerate    */
DECL|field|MAX_ERRORS_PARAM
specifier|private
specifier|final
specifier|static
name|String
name|MAX_ERRORS_PARAM
init|=
literal|"maxErrors"
decl_stmt|;
comment|/**    * Default maxErrors value that will be use if the value is not set in configuration    * or in the request    */
DECL|field|defaultMaxErrors
specifier|private
name|int
name|defaultMaxErrors
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|informed
specifier|private
name|boolean
name|informed
init|=
literal|false
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|Object
name|maxErrorsObj
init|=
name|args
operator|.
name|get
argument_list|(
name|MAX_ERRORS_PARAM
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxErrorsObj
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|defaultMaxErrors
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|maxErrorsObj
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Unnable to parse maxErrors parameter: "
operator|+
name|maxErrorsObj
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|defaultMaxErrors
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Config option '"
operator|+
name|MAX_ERRORS_PARAM
operator|+
literal|"' must either be non-negative, or -1 to indicate 'unlimiited': "
operator|+
name|maxErrorsObj
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|informed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|core
operator|.
name|getLatestSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" requires a schema that includes a uniqueKey field."
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInstance
specifier|public
name|UpdateRequestProcessor
name|getInstance
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|UpdateRequestProcessor
name|next
parameter_list|)
block|{
assert|assert
name|informed
operator|:
literal|"inform(SolrCore) never called?"
assert|;
comment|// short circut if we're a replica processing commands from our leader
name|DistribPhase
name|distribPhase
init|=
name|DistribPhase
operator|.
name|parseParam
argument_list|(
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
name|DISTRIB_UPDATE_PARAM
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|DistribPhase
operator|.
name|FROMLEADER
operator|.
name|equals
argument_list|(
name|distribPhase
argument_list|)
condition|)
block|{
return|return
name|next
return|;
block|}
name|DistributedUpdateProcessorFactory
operator|.
name|addParamToDistributedRequestWhitelist
argument_list|(
name|req
argument_list|,
name|MAX_ERRORS_PARAM
argument_list|)
expr_stmt|;
name|int
name|maxErrors
init|=
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getInt
argument_list|(
name|MAX_ERRORS_PARAM
argument_list|,
name|defaultMaxErrors
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxErrors
operator|<
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"'"
operator|+
name|MAX_ERRORS_PARAM
operator|+
literal|"' must either be non-negative, or -1 to indicate 'unlimiited': "
operator|+
name|maxErrors
argument_list|)
throw|;
block|}
comment|// NOTE: even if 0==maxErrors, we still inject processor into chain so respones has expected header info
return|return
operator|new
name|TolerantUpdateProcessor
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|next
argument_list|,
name|maxErrors
argument_list|,
name|distribPhase
argument_list|)
return|;
block|}
block|}
end_class

end_unit

