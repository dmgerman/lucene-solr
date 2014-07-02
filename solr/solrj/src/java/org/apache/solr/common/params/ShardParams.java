begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Parameters used for distributed search.  */
end_comment

begin_interface
DECL|interface|ShardParams
specifier|public
interface|interface
name|ShardParams
block|{
comment|/** the shards to use (distributed configuration) */
DECL|field|SHARDS
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS
init|=
literal|"shards"
decl_stmt|;
comment|/** per-shard start and rows */
DECL|field|SHARDS_ROWS
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS_ROWS
init|=
literal|"shards.rows"
decl_stmt|;
DECL|field|SHARDS_START
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS_START
init|=
literal|"shards.start"
decl_stmt|;
comment|/** IDs of the shard documents */
DECL|field|IDS
specifier|public
specifier|static
specifier|final
name|String
name|IDS
init|=
literal|"ids"
decl_stmt|;
comment|/** whether the request goes to a shard */
DECL|field|IS_SHARD
specifier|public
specifier|static
specifier|final
name|String
name|IS_SHARD
init|=
literal|"isShard"
decl_stmt|;
comment|/** The requested URL for this shard */
DECL|field|SHARD_URL
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_URL
init|=
literal|"shard.url"
decl_stmt|;
comment|/** The Request Handler for shard requests */
DECL|field|SHARDS_QT
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS_QT
init|=
literal|"shards.qt"
decl_stmt|;
comment|/** Request detailed match info for each shard (true/false) */
DECL|field|SHARDS_INFO
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS_INFO
init|=
literal|"shards.info"
decl_stmt|;
comment|/** Should things fail if there is an error? (true/false) */
DECL|field|SHARDS_TOLERANT
specifier|public
specifier|static
specifier|final
name|String
name|SHARDS_TOLERANT
init|=
literal|"shards.tolerant"
decl_stmt|;
comment|/** Should things fail if there is an error? (true/false) */
annotation|@
name|Deprecated
DECL|field|SHARD_KEYS
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_KEYS
init|=
literal|"shard.keys"
decl_stmt|;
DECL|field|_ROUTE_
specifier|public
specifier|static
specifier|final
name|String
name|_ROUTE_
init|=
literal|"_route_"
decl_stmt|;
comment|/** Force a single-pass distributed query? (true/false) */
DECL|field|DISTRIB_SINGLE_PASS
specifier|public
specifier|static
specifier|final
name|String
name|DISTRIB_SINGLE_PASS
init|=
literal|"distrib.singlePass"
decl_stmt|;
block|}
end_interface

end_unit

