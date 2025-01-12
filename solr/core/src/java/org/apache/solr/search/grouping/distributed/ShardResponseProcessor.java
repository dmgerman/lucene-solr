begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.grouping.distributed
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|grouping
operator|.
name|distributed
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
name|handler
operator|.
name|component
operator|.
name|ResponseBuilder
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
name|component
operator|.
name|ShardRequest
import|;
end_import

begin_comment
comment|/**  * Responsible for processing shard responses.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|ShardResponseProcessor
specifier|public
interface|interface
name|ShardResponseProcessor
block|{
comment|/**    * Processes the responses from the specified shardRequest. The result is put into specific    * fields in the specified rb.    *    * @param rb The ResponseBuilder to put the merge result into    * @param shardRequest The shard request containing the responses from all shards.    */
DECL|method|process
name|void
name|process
parameter_list|(
name|ResponseBuilder
name|rb
parameter_list|,
name|ShardRequest
name|shardRequest
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

