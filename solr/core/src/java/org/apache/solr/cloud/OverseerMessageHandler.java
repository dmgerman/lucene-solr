begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|client
operator|.
name|solrj
operator|.
name|SolrResponse
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
name|cloud
operator|.
name|ZkNodeProps
import|;
end_import

begin_comment
comment|/**  * Interface for processing messages received by an {@link OverseerTaskProcessor}  */
end_comment

begin_interface
DECL|interface|OverseerMessageHandler
specifier|public
interface|interface
name|OverseerMessageHandler
block|{
comment|/**    * @param message the message to process    * @param operation the operation to process    *    * @return response    */
DECL|method|processMessage
name|SolrResponse
name|processMessage
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|,
name|String
name|operation
parameter_list|)
function_decl|;
comment|/**    * @return the name of the OverseerMessageHandler    */
DECL|method|getName
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * @param operation the operation to be timed    *    * @return the name of the timer to use for the operation    */
DECL|method|getTimerName
name|String
name|getTimerName
parameter_list|(
name|String
name|operation
parameter_list|)
function_decl|;
DECL|interface|Lock
interface|interface
name|Lock
block|{
DECL|method|unlock
name|void
name|unlock
parameter_list|()
function_decl|;
block|}
comment|/**Try to provide an exclusive lock for this particular task    * return null if locking is not possible. If locking is not necessary    */
DECL|method|lockTask
name|Lock
name|lockTask
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|,
name|OverseerTaskProcessor
operator|.
name|TaskBatch
name|taskBatch
parameter_list|)
function_decl|;
comment|/**    * @param message the message being processed    *    * @return the taskKey for the message for handling task exclusivity    */
DECL|method|getTaskKey
name|String
name|getTaskKey
parameter_list|(
name|ZkNodeProps
name|message
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

