begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|stats
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
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
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
name|http
operator|.
name|HttpClientConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|RequestLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpRequestWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|utils
operator|.
name|URIBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HttpContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HttpRequestExecutor
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
name|metrics
operator|.
name|SolrMetricManager
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
name|metrics
operator|.
name|SolrMetricProducer
import|;
end_import

begin_comment
comment|/**  * Sub-class of HttpRequestExecutor which tracks metrics interesting to solr  * Inspired and partially copied from dropwizard httpclient library  */
end_comment

begin_class
DECL|class|InstrumentedHttpRequestExecutor
specifier|public
class|class
name|InstrumentedHttpRequestExecutor
extends|extends
name|HttpRequestExecutor
implements|implements
name|SolrMetricProducer
block|{
DECL|field|metricsRegistry
specifier|protected
name|MetricRegistry
name|metricsRegistry
decl_stmt|;
DECL|field|scope
specifier|protected
name|String
name|scope
decl_stmt|;
DECL|method|methodNameString
specifier|private
specifier|static
name|String
name|methodNameString
parameter_list|(
name|HttpRequest
name|request
parameter_list|)
block|{
return|return
name|request
operator|.
name|getRequestLine
argument_list|()
operator|.
name|getMethod
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|".requests"
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|HttpResponse
name|execute
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpClientConnection
name|conn
parameter_list|,
name|HttpContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|HttpException
block|{
name|Timer
operator|.
name|Context
name|timerContext
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|metricsRegistry
operator|!=
literal|null
condition|)
block|{
name|timerContext
operator|=
name|timer
argument_list|(
name|request
argument_list|)
operator|.
name|time
argument_list|()
expr_stmt|;
block|}
try|try
block|{
return|return
name|super
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|conn
argument_list|,
name|context
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|timerContext
operator|!=
literal|null
condition|)
block|{
name|timerContext
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|timer
specifier|private
name|Timer
name|timer
parameter_list|(
name|HttpRequest
name|request
parameter_list|)
block|{
return|return
name|metricsRegistry
operator|.
name|timer
argument_list|(
name|getNameFor
argument_list|(
name|request
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initializeMetrics
specifier|public
name|void
name|initializeMetrics
parameter_list|(
name|SolrMetricManager
name|manager
parameter_list|,
name|String
name|registry
parameter_list|,
name|String
name|scope
parameter_list|)
block|{
name|this
operator|.
name|metricsRegistry
operator|=
name|manager
operator|.
name|registry
argument_list|(
name|registry
argument_list|)
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
DECL|method|getNameFor
specifier|private
name|String
name|getNameFor
parameter_list|(
name|HttpRequest
name|request
parameter_list|)
block|{
try|try
block|{
specifier|final
name|RequestLine
name|requestLine
init|=
name|request
operator|.
name|getRequestLine
argument_list|()
decl_stmt|;
name|String
name|schemeHostPort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|instanceof
name|HttpRequestWrapper
condition|)
block|{
name|HttpRequestWrapper
name|wrapper
init|=
operator|(
name|HttpRequestWrapper
operator|)
name|request
decl_stmt|;
name|schemeHostPort
operator|=
name|wrapper
operator|.
name|getTarget
argument_list|()
operator|.
name|getSchemeName
argument_list|()
operator|+
literal|"://"
operator|+
name|wrapper
operator|.
name|getTarget
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|wrapper
operator|.
name|getTarget
argument_list|()
operator|.
name|getPort
argument_list|()
expr_stmt|;
block|}
specifier|final
name|URIBuilder
name|url
init|=
operator|new
name|URIBuilder
argument_list|(
name|requestLine
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|SolrMetricManager
operator|.
name|mkName
argument_list|(
operator|(
name|schemeHostPort
operator|!=
literal|null
condition|?
name|schemeHostPort
else|:
literal|""
operator|)
operator|+
name|url
operator|.
name|removeQuery
argument_list|()
operator|.
name|build
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"."
operator|+
name|methodNameString
argument_list|(
name|request
argument_list|)
argument_list|,
name|scope
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

