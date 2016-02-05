begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.grouping.distributed.shardresultserializer
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
operator|.
name|shardresultserializer
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
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

begin_comment
comment|/**  * A<code>ShardResultTransformer</code> is responsible for transforming a grouped shard result into group related  * structures (such as {@link org.apache.lucene.search.grouping.TopGroups} and {@link org.apache.lucene.search.grouping.SearchGroup})  * and visa versa.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|ShardResultTransformer
specifier|public
interface|interface
name|ShardResultTransformer
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
block|{
comment|/**    * Transforms data to a {@link NamedList} structure for serialization purposes.    *    * @param data The data to be transformed    * @return {@link NamedList} structure    * @throws IOException If I/O related errors occur during transforming    */
DECL|method|transform
name|NamedList
name|transform
parameter_list|(
name|T
name|data
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Transforms the specified shard response into native structures.    *    * @param shardResponse The shard response containing data in a {@link NamedList} structure    * @param groupSort The group sort    * @param sortWithinGroup The sort inside a group    * @param shard The shard address where the response originated from    * @return native structure of the data    */
DECL|method|transformToNative
name|R
name|transformToNative
parameter_list|(
name|NamedList
argument_list|<
name|NamedList
argument_list|>
name|shardResponse
parameter_list|,
name|Sort
name|groupSort
parameter_list|,
name|Sort
name|sortWithinGroup
parameter_list|,
name|String
name|shard
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

