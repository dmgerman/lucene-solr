begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|request
operator|.
name|CollectionApiMapping
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
name|request
operator|.
name|CollectionApiMapping
operator|.
name|CommandMeta
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
name|request
operator|.
name|CollectionApiMapping
operator|.
name|Meta
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
name|request
operator|.
name|CollectionApiMapping
operator|.
name|V2EndPoint
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
name|handler
operator|.
name|admin
operator|.
name|CollectionsHandler
operator|.
name|CollectionOperation
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

begin_class
DECL|class|CollectionHandlerApi
specifier|public
class|class
name|CollectionHandlerApi
extends|extends
name|BaseHandlerApiSupport
block|{
DECL|field|handler
specifier|final
name|CollectionsHandler
name|handler
decl_stmt|;
DECL|field|apiCommands
specifier|static
name|Collection
argument_list|<
name|ApiCommand
argument_list|>
name|apiCommands
init|=
name|createCollMapping
argument_list|()
decl_stmt|;
DECL|method|createCollMapping
specifier|private
specifier|static
name|Collection
argument_list|<
name|ApiCommand
argument_list|>
name|createCollMapping
parameter_list|()
block|{
name|Map
argument_list|<
name|Meta
argument_list|,
name|ApiCommand
argument_list|>
name|result
init|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|Meta
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|Meta
name|meta
range|:
name|Meta
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|CollectionOperation
name|op
range|:
name|CollectionOperation
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|op
operator|.
name|action
operator|==
name|meta
operator|.
name|action
condition|)
block|{
name|result
operator|.
name|put
argument_list|(
name|meta
argument_list|,
operator|new
name|ApiCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CommandMeta
name|meta
parameter_list|()
block|{
return|return
name|meta
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invoke
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|BaseHandlerApiSupport
name|apiHandler
parameter_list|)
throws|throws
name|Exception
block|{
operator|(
operator|(
name|CollectionHandlerApi
operator|)
name|apiHandler
operator|)
operator|.
name|handler
operator|.
name|invokeAction
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|(
operator|(
name|CollectionHandlerApi
operator|)
name|apiHandler
operator|)
operator|.
name|handler
operator|.
name|coreContainer
argument_list|,
name|op
operator|.
name|action
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|put
argument_list|(
name|Meta
operator|.
name|GET_NODES
argument_list|,
operator|new
name|ApiCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CommandMeta
name|meta
parameter_list|()
block|{
return|return
name|Meta
operator|.
name|GET_NODES
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invoke
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|,
name|BaseHandlerApiSupport
name|apiHandler
parameter_list|)
throws|throws
name|Exception
block|{
name|rsp
operator|.
name|add
argument_list|(
literal|"nodes"
argument_list|,
operator|(
operator|(
name|CollectionHandlerApi
operator|)
name|apiHandler
operator|)
operator|.
name|handler
operator|.
name|coreContainer
operator|.
name|getZkController
argument_list|()
operator|.
name|getClusterState
argument_list|()
operator|.
name|getLiveNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Meta
name|meta
range|:
name|Meta
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|meta
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"No implementation for "
operator|+
name|meta
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|result
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|CollectionHandlerApi
specifier|public
name|CollectionHandlerApi
parameter_list|(
name|CollectionsHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommands
specifier|protected
name|Collection
argument_list|<
name|ApiCommand
argument_list|>
name|getCommands
parameter_list|()
block|{
return|return
name|apiCommands
return|;
block|}
annotation|@
name|Override
DECL|method|getEndPoints
specifier|protected
name|List
argument_list|<
name|V2EndPoint
argument_list|>
name|getEndPoints
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|CollectionApiMapping
operator|.
name|EndPoint
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

