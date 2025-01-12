begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.stats
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
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
name|response
operator|.
name|QueryResponse
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
name|SolrDocument
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
name|SolrDocumentList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Abstract calls should not executed as test"
argument_list|)
DECL|class|TestBaseStatsCache
specifier|public
specifier|abstract
class|class
name|TestBaseStatsCache
extends|extends
name|TestDefaultStatsCache
block|{
DECL|method|getStatsCacheClassName
specifier|protected
specifier|abstract
name|String
name|getStatsCacheClassName
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|distribSetUp
specifier|public
name|void
name|distribSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|getStatsCacheClassName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|distribTearDown
specifier|public
name|void
name|distribTearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|distribTearDown
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.statsCache"
argument_list|)
expr_stmt|;
block|}
comment|// in this case, as the number of shards increases, per-shard scores should
comment|// remain identical
annotation|@
name|Override
DECL|method|checkResponse
specifier|protected
name|void
name|checkResponse
parameter_list|(
name|QueryResponse
name|controlRsp
parameter_list|,
name|QueryResponse
name|shardRsp
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================= Control Response ======================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|controlRsp
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================= Shard Response ======================="
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|shardRsp
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|shardList
init|=
name|shardRsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|SolrDocumentList
name|controlList
init|=
name|controlRsp
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|controlList
operator|.
name|size
argument_list|()
argument_list|,
name|shardList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|controlList
operator|.
name|getNumFound
argument_list|()
argument_list|,
name|shardList
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|it
init|=
name|controlList
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|SolrDocument
argument_list|>
name|it2
init|=
name|shardList
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SolrDocument
name|controlDoc
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|SolrDocument
name|shardDoc
init|=
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|controlDoc
operator|.
name|getFieldValue
argument_list|(
literal|"score"
argument_list|)
argument_list|,
name|shardDoc
operator|.
name|getFieldValue
argument_list|(
literal|"score"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

