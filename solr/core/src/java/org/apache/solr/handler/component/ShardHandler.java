begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_class
DECL|class|ShardHandler
specifier|public
specifier|abstract
class|class
name|ShardHandler
block|{
DECL|method|prepDistributed
specifier|public
specifier|abstract
name|void
name|prepDistributed
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|)
function_decl|;
DECL|method|submit
specifier|public
specifier|abstract
name|void
name|submit
parameter_list|(
name|ShardRequest
name|sreq
parameter_list|,
name|String
name|shard
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
function_decl|;
DECL|method|takeCompletedIncludingErrors
specifier|public
specifier|abstract
name|ShardResponse
name|takeCompletedIncludingErrors
parameter_list|()
function_decl|;
DECL|method|takeCompletedOrError
specifier|public
specifier|abstract
name|ShardResponse
name|takeCompletedOrError
parameter_list|()
function_decl|;
DECL|method|cancelAll
specifier|public
specifier|abstract
name|void
name|cancelAll
parameter_list|()
function_decl|;
DECL|method|getShardHandlerFactory
specifier|public
specifier|abstract
name|ShardHandlerFactory
name|getShardHandlerFactory
parameter_list|()
function_decl|;
block|}
end_class

end_unit

