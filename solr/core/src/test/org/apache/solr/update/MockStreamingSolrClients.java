begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
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
name|SolrRequest
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
name|SolrServerException
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
name|ConnectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_class
DECL|class|MockStreamingSolrClients
specifier|public
class|class
name|MockStreamingSolrClients
extends|extends
name|StreamingSolrClients
block|{
DECL|enum|Exp
DECL|enum constant|CONNECT_EXCEPTION
DECL|enum constant|SOCKET_EXCEPTION
specifier|public
enum|enum
name|Exp
block|{
name|CONNECT_EXCEPTION
block|,
name|SOCKET_EXCEPTION
block|}
empty_stmt|;
DECL|field|exp
specifier|private
specifier|volatile
name|Exp
name|exp
init|=
literal|null
decl_stmt|;
DECL|method|MockStreamingSolrClients
specifier|public
name|MockStreamingSolrClients
parameter_list|(
name|UpdateShardHandler
name|updateShardHandler
parameter_list|)
block|{
name|super
argument_list|(
name|updateShardHandler
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSolrClient
specifier|public
specifier|synchronized
name|SolrClient
name|getSolrClient
parameter_list|(
specifier|final
name|SolrCmdDistributor
operator|.
name|Req
name|req
parameter_list|)
block|{
name|SolrClient
name|client
init|=
name|super
operator|.
name|getSolrClient
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
operator|new
name|MockSolrClient
argument_list|(
name|client
argument_list|)
return|;
block|}
DECL|method|setExp
specifier|public
name|void
name|setExp
parameter_list|(
name|Exp
name|exp
parameter_list|)
block|{
name|this
operator|.
name|exp
operator|=
name|exp
expr_stmt|;
block|}
DECL|method|exception
specifier|private
name|IOException
name|exception
parameter_list|()
block|{
switch|switch
condition|(
name|exp
condition|)
block|{
case|case
name|CONNECT_EXCEPTION
case|:
return|return
operator|new
name|ConnectException
argument_list|()
return|;
case|case
name|SOCKET_EXCEPTION
case|:
return|return
operator|new
name|SocketException
argument_list|()
return|;
default|default:
break|break;
block|}
return|return
literal|null
return|;
block|}
DECL|class|MockSolrClient
class|class
name|MockSolrClient
extends|extends
name|SolrClient
block|{
DECL|field|solrClient
specifier|private
name|SolrClient
name|solrClient
decl_stmt|;
DECL|method|MockSolrClient
specifier|public
name|MockSolrClient
parameter_list|(
name|SolrClient
name|solrClient
parameter_list|)
block|{
name|this
operator|.
name|solrClient
operator|=
name|solrClient
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|request
specifier|public
name|NamedList
argument_list|<
name|Object
argument_list|>
name|request
parameter_list|(
name|SolrRequest
name|request
parameter_list|,
name|String
name|collection
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|LuceneTestCase
operator|.
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
throw|throw
name|exception
argument_list|()
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrServerException
argument_list|(
name|exception
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|solrClient
operator|.
name|request
argument_list|(
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{}
block|}
block|}
end_class

end_unit

