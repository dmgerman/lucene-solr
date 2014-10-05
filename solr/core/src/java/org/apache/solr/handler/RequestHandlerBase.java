begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
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
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
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
name|request
operator|.
name|SolrRequestHandler
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
name|search
operator|.
name|SyntaxError
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
name|SolrPluginUtils
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
name|stats
operator|.
name|Snapshot
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
name|stats
operator|.
name|Timer
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
name|stats
operator|.
name|TimerContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RequestHandlerBase
specifier|public
specifier|abstract
class|class
name|RequestHandlerBase
implements|implements
name|SolrRequestHandler
implements|,
name|SolrInfoMBean
implements|,
name|NestedRequestHandler
block|{
DECL|field|initArgs
specifier|protected
name|NamedList
name|initArgs
init|=
literal|null
decl_stmt|;
DECL|field|defaults
specifier|protected
name|SolrParams
name|defaults
decl_stmt|;
DECL|field|appends
specifier|protected
name|SolrParams
name|appends
decl_stmt|;
DECL|field|invariants
specifier|protected
name|SolrParams
name|invariants
decl_stmt|;
DECL|field|httpCaching
specifier|protected
name|boolean
name|httpCaching
init|=
literal|true
decl_stmt|;
comment|// Statistics
DECL|field|numRequests
specifier|private
specifier|final
name|AtomicLong
name|numRequests
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numErrors
specifier|private
specifier|final
name|AtomicLong
name|numErrors
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numTimeouts
specifier|private
specifier|final
name|AtomicLong
name|numTimeouts
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|requestTimes
specifier|private
specifier|final
name|Timer
name|requestTimes
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
DECL|field|handlerStart
specifier|private
specifier|final
name|long
name|handlerStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|/**    * Initializes the {@link org.apache.solr.request.SolrRequestHandler} by creating three {@link org.apache.solr.common.params.SolrParams} named.    *<table border="1">    *<tr><th>Name</th><th>Description</th></tr>    *<tr><td>defaults</td><td>Contains all of the named arguments contained within the list element named "defaults".</td></tr>    *<tr><td>appends</td><td>Contains all of the named arguments contained within the list element named "appends".</td></tr>    *<tr><td>invariants</td><td>Contains all of the named arguments contained within the list element named "invariants".</td></tr>    *</table>    *    * Example:    *<pre>    *&lt;lst name="defaults"&gt;    *&lt;str name="echoParams"&gt;explicit&lt;/str&gt;    *&lt;str name="qf"&gt;text^0.5 features^1.0 name^1.2 sku^1.5 id^10.0&lt;/str&gt;    *&lt;str name="mm"&gt;2&lt;-1 5&lt;-2 6&lt;90%&lt;/str&gt;    *&lt;str name="bq"&gt;incubationdate_dt:[* TO NOW/DAY-1MONTH]^2.2&lt;/str&gt;    *&lt;/lst&gt;    *&lt;lst name="appends"&gt;    *&lt;str name="fq"&gt;inStock:true&lt;/str&gt;    *&lt;/lst&gt;    *    *&lt;lst name="invariants"&gt;    *&lt;str name="facet.field"&gt;cat&lt;/str&gt;    *&lt;str name="facet.field"&gt;manu_exact&lt;/str&gt;    *&lt;str name="facet.query"&gt;price:[* TO 500]&lt;/str&gt;    *&lt;str name="facet.query"&gt;price:[500 TO *]&lt;/str&gt;    *&lt;/lst&gt;    *</pre>    *    *    * @param args The {@link org.apache.solr.common.util.NamedList} to initialize from    *    * @see #handleRequest(org.apache.solr.request.SolrQueryRequest, org.apache.solr.response.SolrQueryResponse)    * @see #handleRequestBody(org.apache.solr.request.SolrQueryRequest, org.apache.solr.response.SolrQueryResponse)    * @see org.apache.solr.util.SolrPluginUtils#setDefaults(org.apache.solr.request.SolrQueryRequest, org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams, org.apache.solr.common.params.SolrParams)    * @see SolrParams#toSolrParams(org.apache.solr.common.util.NamedList)    *    * See also the example solrconfig.xml located in the Solr codebase (example/solr/conf).    */
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
name|initArgs
operator|=
name|args
expr_stmt|;
comment|// Copied from StandardRequestHandler
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|args
operator|.
name|get
argument_list|(
literal|"defaults"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|defaults
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"appends"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|appends
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"invariants"
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
operator|&&
name|o
operator|instanceof
name|NamedList
condition|)
block|{
name|invariants
operator|=
name|SolrParams
operator|.
name|toSolrParams
argument_list|(
operator|(
name|NamedList
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|initArgs
operator|!=
literal|null
condition|)
block|{
name|Object
name|caching
init|=
name|initArgs
operator|.
name|get
argument_list|(
literal|"httpCaching"
argument_list|)
decl_stmt|;
name|httpCaching
operator|=
name|caching
operator|!=
literal|null
condition|?
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|caching
operator|.
name|toString
argument_list|()
argument_list|)
else|:
literal|true
expr_stmt|;
block|}
block|}
DECL|method|getInitArgs
specifier|public
name|NamedList
name|getInitArgs
parameter_list|()
block|{
return|return
name|initArgs
return|;
block|}
DECL|method|handleRequestBody
specifier|public
specifier|abstract
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
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
block|{
name|numRequests
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|TimerContext
name|timer
init|=
name|requestTimes
operator|.
name|time
argument_list|()
decl_stmt|;
try|try
block|{
name|SolrPluginUtils
operator|.
name|setDefaults
argument_list|(
name|req
argument_list|,
name|defaults
argument_list|,
name|appends
argument_list|,
name|invariants
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
name|httpCaching
argument_list|)
expr_stmt|;
name|handleRequestBody
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// count timeouts
name|NamedList
name|header
init|=
name|rsp
operator|.
name|getResponseHeader
argument_list|()
decl_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
block|{
name|Object
name|partialResults
init|=
name|header
operator|.
name|get
argument_list|(
literal|"partialResults"
argument_list|)
decl_stmt|;
name|boolean
name|timedOut
init|=
name|partialResults
operator|==
literal|null
condition|?
literal|false
else|:
operator|(
name|Boolean
operator|)
name|partialResults
decl_stmt|;
if|if
condition|(
name|timedOut
condition|)
block|{
name|numTimeouts
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|setHttpCaching
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
name|SolrException
name|se
init|=
operator|(
name|SolrException
operator|)
name|e
decl_stmt|;
if|if
condition|(
name|se
operator|.
name|code
argument_list|()
operator|==
name|SolrException
operator|.
name|ErrorCode
operator|.
name|CONFLICT
operator|.
name|code
condition|)
block|{
comment|// TODO: should we allow this to be counted as an error (numErrors++)?
block|}
else|else
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SyntaxError
condition|)
block|{
name|e
operator|=
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|setException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|timer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|//////////////////////// SolrInfoMBeans methods //////////////////////
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDescription
specifier|public
specifier|abstract
name|String
name|getDescription
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getPackage
argument_list|()
operator|.
name|getSpecificationVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|QUERYHANDLER
return|;
block|}
annotation|@
name|Override
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// this can be overridden, but not required
block|}
annotation|@
name|Override
DECL|method|getSubHandler
specifier|public
name|SolrRequestHandler
name|getSubHandler
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Get the request handler registered to a given name.    *    * This function is thread safe.    */
DECL|method|getRequestHandler
specifier|public
specifier|static
name|SolrRequestHandler
name|getRequestHandler
parameter_list|(
name|String
name|handlerName
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SolrRequestHandler
argument_list|>
name|reqHandlers
parameter_list|)
block|{
if|if
condition|(
name|handlerName
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|SolrRequestHandler
name|handler
init|=
name|reqHandlers
operator|.
name|get
argument_list|(
name|handlerName
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|idx
operator|=
name|handlerName
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|String
name|firstPart
init|=
name|handlerName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|handler
operator|=
name|reqHandlers
operator|.
name|get
argument_list|(
name|firstPart
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
continue|continue;
if|if
condition|(
name|handler
operator|instanceof
name|NestedRequestHandler
condition|)
block|{
return|return
operator|(
operator|(
name|NestedRequestHandler
operator|)
name|handler
operator|)
operator|.
name|getSubHandler
argument_list|(
name|handlerName
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
argument_list|)
return|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
return|return
name|handler
return|;
block|}
annotation|@
name|Override
DECL|method|getStatistics
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|getStatistics
parameter_list|()
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|lst
init|=
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Snapshot
name|snapshot
init|=
name|requestTimes
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"handlerStart"
argument_list|,
name|handlerStart
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"requests"
argument_list|,
name|numRequests
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"timeouts"
argument_list|,
name|numTimeouts
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"totalTime"
argument_list|,
name|requestTimes
operator|.
name|getSum
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"avgRequestsPerSecond"
argument_list|,
name|requestTimes
operator|.
name|getMeanRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"5minRateReqsPerSecond"
argument_list|,
name|requestTimes
operator|.
name|getFiveMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"15minRateReqsPerSecond"
argument_list|,
name|requestTimes
operator|.
name|getFifteenMinuteRate
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"avgTimePerRequest"
argument_list|,
name|requestTimes
operator|.
name|getMean
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"medianRequestTime"
argument_list|,
name|snapshot
operator|.
name|getMedian
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"75thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get75thPercentile
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"95thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get95thPercentile
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"99thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get99thPercentile
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"999thPcRequestTime"
argument_list|,
name|snapshot
operator|.
name|get999thPercentile
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
block|}
end_class

end_unit

