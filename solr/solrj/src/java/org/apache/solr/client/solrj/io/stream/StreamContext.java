begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream
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
operator|.
name|stream
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|io
operator|.
name|SolrClientCache
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import

begin_comment
comment|/**  *  The StreamContext is passed to TupleStreams using the TupleStream.setStreamContext() method.  *  The StreamContext is used to pass shared context to concentrically wrapped TupleStreams.  *  *  Note: The StreamContext contains the SolrClientCache which is used to cache SolrClients for reuse  *  across multiple TupleStreams.  **/
end_comment

begin_class
DECL|class|StreamContext
specifier|public
class|class
name|StreamContext
implements|implements
name|Serializable
block|{
DECL|field|entries
specifier|private
name|Map
name|entries
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|workerID
specifier|public
name|int
name|workerID
decl_stmt|;
DECL|field|numWorkers
specifier|public
name|int
name|numWorkers
decl_stmt|;
DECL|field|clientCache
specifier|private
name|SolrClientCache
name|clientCache
decl_stmt|;
DECL|field|streamFactory
specifier|private
name|StreamFactory
name|streamFactory
decl_stmt|;
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|entries
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|getEntries
specifier|public
name|Map
name|getEntries
parameter_list|()
block|{
return|return
name|this
operator|.
name|entries
return|;
block|}
DECL|method|setSolrClientCache
specifier|public
name|void
name|setSolrClientCache
parameter_list|(
name|SolrClientCache
name|clientCache
parameter_list|)
block|{
name|this
operator|.
name|clientCache
operator|=
name|clientCache
expr_stmt|;
block|}
DECL|method|getSolrClientCache
specifier|public
name|SolrClientCache
name|getSolrClientCache
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientCache
return|;
block|}
DECL|method|setStreamFactory
specifier|public
name|void
name|setStreamFactory
parameter_list|(
name|StreamFactory
name|streamFactory
parameter_list|)
block|{
name|this
operator|.
name|streamFactory
operator|=
name|streamFactory
expr_stmt|;
block|}
DECL|method|getStreamFactory
specifier|public
name|StreamFactory
name|getStreamFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|streamFactory
return|;
block|}
block|}
end_class

end_unit

