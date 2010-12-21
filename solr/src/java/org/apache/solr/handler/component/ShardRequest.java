begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_comment
comment|// todo... when finalized make accessors
end_comment

begin_class
DECL|class|ShardRequest
specifier|public
class|class
name|ShardRequest
block|{
DECL|field|ALL_SHARDS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|ALL_SHARDS
init|=
literal|null
decl_stmt|;
DECL|field|PURPOSE_PRIVATE
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_PRIVATE
init|=
literal|0x01
decl_stmt|;
DECL|field|PURPOSE_GET_TERM_DFS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_TERM_DFS
init|=
literal|0x02
decl_stmt|;
DECL|field|PURPOSE_GET_TOP_IDS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_TOP_IDS
init|=
literal|0x04
decl_stmt|;
DECL|field|PURPOSE_REFINE_TOP_IDS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_REFINE_TOP_IDS
init|=
literal|0x08
decl_stmt|;
DECL|field|PURPOSE_GET_FACETS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_FACETS
init|=
literal|0x10
decl_stmt|;
DECL|field|PURPOSE_REFINE_FACETS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_REFINE_FACETS
init|=
literal|0x20
decl_stmt|;
DECL|field|PURPOSE_GET_FIELDS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_FIELDS
init|=
literal|0x40
decl_stmt|;
DECL|field|PURPOSE_GET_HIGHLIGHTS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_HIGHLIGHTS
init|=
literal|0x80
decl_stmt|;
DECL|field|PURPOSE_GET_DEBUG
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_DEBUG
init|=
literal|0x100
decl_stmt|;
DECL|field|PURPOSE_GET_STATS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_STATS
init|=
literal|0x200
decl_stmt|;
DECL|field|PURPOSE_GET_TERMS
specifier|public
specifier|final
specifier|static
name|int
name|PURPOSE_GET_TERMS
init|=
literal|0x400
decl_stmt|;
DECL|field|purpose
specifier|public
name|int
name|purpose
decl_stmt|;
comment|// the purpose of this request
DECL|field|shards
specifier|public
name|String
index|[]
name|shards
decl_stmt|;
comment|// the shards this request should be sent to, null for all
DECL|field|params
specifier|public
name|ModifiableSolrParams
name|params
decl_stmt|;
comment|/** list of responses... filled out by framework */
DECL|field|responses
specifier|public
name|List
argument_list|<
name|ShardResponse
argument_list|>
name|responses
init|=
operator|new
name|ArrayList
argument_list|<
name|ShardResponse
argument_list|>
argument_list|()
decl_stmt|;
comment|/** actual shards to send the request to, filled out by framework */
DECL|field|actualShards
specifier|public
name|String
index|[]
name|actualShards
decl_stmt|;
comment|// TODO: one could store a list of numbers to correlate where returned docs
comment|// go in the top-level response rather than looking up by id...
comment|// this would work well if we ever transitioned to using internal ids and
comment|// didn't require a uniqueId
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ShardRequest:{params="
operator|+
name|params
operator|+
literal|", purpose="
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|purpose
argument_list|)
operator|+
literal|", nResponses ="
operator|+
name|responses
operator|.
name|size
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

