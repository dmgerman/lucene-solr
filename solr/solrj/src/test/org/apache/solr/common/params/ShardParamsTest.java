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

begin_comment
comment|/**  * This class tests backwards compatibility of {@link ShardParams} parameter constants.  * If someone accidentally changes those constants then this test will flag that up.   */
end_comment

begin_class
DECL|class|ShardParamsTest
specifier|public
class|class
name|ShardParamsTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testShards
specifier|public
name|void
name|testShards
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS
argument_list|,
literal|"shards"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardsRows
specifier|public
name|void
name|testShardsRows
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS_ROWS
argument_list|,
literal|"shards.rows"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardsStart
specifier|public
name|void
name|testShardsStart
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS_START
argument_list|,
literal|"shards.start"
argument_list|)
expr_stmt|;
block|}
DECL|method|testIds
specifier|public
name|void
name|testIds
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|IDS
argument_list|,
literal|"ids"
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsShard
specifier|public
name|void
name|testIsShard
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|IS_SHARD
argument_list|,
literal|"isShard"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardUrl
specifier|public
name|void
name|testShardUrl
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARD_URL
argument_list|,
literal|"shard.url"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardsQt
specifier|public
name|void
name|testShardsQt
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS_QT
argument_list|,
literal|"shards.qt"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardsInfo
specifier|public
name|void
name|testShardsInfo
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS_INFO
argument_list|,
literal|"shards.info"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardsTolerant
specifier|public
name|void
name|testShardsTolerant
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS_TOLERANT
argument_list|,
literal|"shards.tolerant"
argument_list|)
expr_stmt|;
block|}
DECL|method|testShardsPurpose
specifier|public
name|void
name|testShardsPurpose
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|SHARDS_PURPOSE
argument_list|,
literal|"shards.purpose"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRoute
specifier|public
name|void
name|testRoute
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|_ROUTE_
argument_list|,
literal|"_route_"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDistribSinglePass
specifier|public
name|void
name|testDistribSinglePass
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ShardParams
operator|.
name|DISTRIB_SINGLE_PASS
argument_list|,
literal|"distrib.singlePass"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

