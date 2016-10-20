begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Interface to determine if a collection state matches a required state  *  * @see ZkStateReader#waitForState(String, long, TimeUnit, CollectionStatePredicate)  */
end_comment

begin_interface
DECL|interface|CollectionStatePredicate
specifier|public
interface|interface
name|CollectionStatePredicate
block|{
comment|/**    * Check the collection state matches a required state    *    * Note that both liveNodes and collectionState should be consulted to determine    * the overall state.    *    * @param liveNodes the current set of live nodes    * @param collectionState the latest collection state, or null if the collection    *                        does not exist    */
DECL|method|matches
name|boolean
name|matches
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|liveNodes
parameter_list|,
name|DocCollection
name|collectionState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

