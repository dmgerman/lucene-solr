begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
name|HashMap
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
name|HttpClient
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
name|client
operator|.
name|solrj
operator|.
name|SolrClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|CloudSolrClient
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  The SolrClientCache caches SolrClients so they can be reused by different TupleStreams.  **/
end_comment

begin_class
DECL|class|SolrClientCache
specifier|public
class|class
name|SolrClientCache
implements|implements
name|Serializable
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|solrClients
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrClient
argument_list|>
name|solrClients
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|httpClient
specifier|private
specifier|final
name|HttpClient
name|httpClient
decl_stmt|;
DECL|method|SolrClientCache
specifier|public
name|SolrClientCache
parameter_list|()
block|{
name|httpClient
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|SolrClientCache
specifier|public
name|SolrClientCache
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|)
block|{
name|this
operator|.
name|httpClient
operator|=
name|httpClient
expr_stmt|;
block|}
DECL|method|getCloudSolrClient
specifier|public
specifier|synchronized
name|CloudSolrClient
name|getCloudSolrClient
parameter_list|(
name|String
name|zkHost
parameter_list|)
block|{
name|CloudSolrClient
name|client
decl_stmt|;
if|if
condition|(
name|solrClients
operator|.
name|containsKey
argument_list|(
name|zkHost
argument_list|)
condition|)
block|{
name|client
operator|=
operator|(
name|CloudSolrClient
operator|)
name|solrClients
operator|.
name|get
argument_list|(
name|zkHost
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CloudSolrClient
operator|.
name|Builder
name|builder
init|=
operator|new
name|CloudSolrClient
operator|.
name|Builder
argument_list|()
operator|.
name|withZkHost
argument_list|(
name|zkHost
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpClient
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
name|builder
operator|.
name|withHttpClient
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
block|}
name|client
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|client
operator|.
name|connect
argument_list|()
expr_stmt|;
name|solrClients
operator|.
name|put
argument_list|(
name|zkHost
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
DECL|method|getHttpSolrClient
specifier|public
specifier|synchronized
name|HttpSolrClient
name|getHttpSolrClient
parameter_list|(
name|String
name|host
parameter_list|)
block|{
name|HttpSolrClient
name|client
decl_stmt|;
if|if
condition|(
name|solrClients
operator|.
name|containsKey
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|client
operator|=
operator|(
name|HttpSolrClient
operator|)
name|solrClients
operator|.
name|get
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|HttpSolrClient
operator|.
name|Builder
name|builder
init|=
operator|new
name|HttpSolrClient
operator|.
name|Builder
argument_list|(
name|host
argument_list|)
decl_stmt|;
if|if
condition|(
name|httpClient
operator|!=
literal|null
condition|)
block|{
name|builder
operator|=
name|builder
operator|.
name|withHttpClient
argument_list|(
name|httpClient
argument_list|)
expr_stmt|;
block|}
name|client
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|solrClients
operator|.
name|put
argument_list|(
name|host
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SolrClient
argument_list|>
name|entry
range|:
name|solrClients
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error closing SolrClient for "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|solrClients
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

