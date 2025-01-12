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
name|BaseDistributedSearchTestCase
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
name|response
operator|.
name|PivotField
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
DECL|class|DistributedFacetPivotWhiteBoxTest
specifier|public
class|class
name|DistributedFacetPivotWhiteBoxTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|4
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
comment|// NOTE: we use the literal (4 character) string "null" as a company name
comment|// to help ensure there isn't any bugs where the literal string is treated as if it
comment|// were a true NULL value.
name|index
argument_list|(
name|id
argument_list|,
literal|19
argument_list|,
literal|"place_t"
argument_list|,
literal|"cardiff dublin"
argument_list|,
literal|"company_t"
argument_list|,
literal|"microsoft polecat"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"15"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|20
argument_list|,
literal|"place_t"
argument_list|,
literal|"dublin"
argument_list|,
literal|"company_t"
argument_list|,
literal|"polecat microsoft null"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"19"
argument_list|,
comment|// this is the only doc to have solo_* fields, therefore only 1 shard has them
comment|// TODO: add enum field - blocked by SOLR-6682
literal|"solo_i"
argument_list|,
literal|42
argument_list|,
literal|"solo_s"
argument_list|,
literal|"lonely"
argument_list|,
literal|"solo_dt"
argument_list|,
literal|"1976-03-06T01:23:45Z"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|21
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow london la dublin"
argument_list|,
literal|"company_t"
argument_list|,
literal|"microsoft fujitsu null polecat"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"29"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|22
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow london cardiff"
argument_list|,
literal|"company_t"
argument_list|,
literal|"polecat null bbc"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"39"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|23
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow london"
argument_list|,
literal|"company_t"
argument_list|,
literal|""
argument_list|,
literal|"price_ti"
argument_list|,
literal|"29"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|24
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow la"
argument_list|,
literal|"company_t"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|25
argument_list|,
literal|"company_t"
argument_list|,
literal|"microsoft polecat null fujitsu null bbc"
argument_list|,
literal|"price_ti"
argument_list|,
literal|"59"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|26
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow"
argument_list|,
literal|"company_t"
argument_list|,
literal|"null"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|27
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow cardiff dublin london la"
argument_list|,
literal|"company_t"
argument_list|,
literal|"null microsoft polecat bbc fujitsu"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|28
argument_list|,
literal|"place_t"
argument_list|,
literal|"krakow cork"
argument_list|,
literal|"company_t"
argument_list|,
literal|"fujitsu rte"
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|doShardTestTopStats
argument_list|()
expr_stmt|;
name|doTestRefinementRequest
argument_list|()
expr_stmt|;
block|}
comment|/**     * recreates the initial request to a shard in a distributed query    * confirming that both top level stats, and per-pivot stats are returned.    */
DECL|method|doShardTestTopStats
specifier|private
name|void
name|doShardTestTopStats
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|params
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
comment|// "wt", "javabin",
literal|"facet.pivot"
argument_list|,
literal|"{!stats=s1}place_t,company_t"
argument_list|,
comment|// "version", "2",
literal|"start"
argument_list|,
literal|"0"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"fsv"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
literal|"{!key=avg_price tag=s1}price_ti"
argument_list|,
literal|"f.place_t.facet.limit"
argument_list|,
literal|"160"
argument_list|,
literal|"f.place_t.facet.pivot.mincount"
argument_list|,
literal|"0"
argument_list|,
literal|"f.company_t.facet.limit"
argument_list|,
literal|"160"
argument_list|,
literal|"f.company_t.facet.pivot.mincount"
argument_list|,
literal|"0"
argument_list|,
literal|"isShard"
argument_list|,
literal|"true"
argument_list|,
literal|"distrib"
argument_list|,
literal|"false"
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|queryServer
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"initial shard request should include non-null top level stats"
argument_list|,
name|rsp
operator|.
name|getFieldStatsInfo
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"initial shard request should include top level stats"
argument_list|,
name|rsp
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PivotField
argument_list|>
name|placePivots
init|=
name|rsp
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"place_t,company_t"
argument_list|)
decl_stmt|;
for|for
control|(
name|PivotField
name|pivotField
range|:
name|placePivots
control|)
block|{
name|assertFalse
argument_list|(
literal|"pivot stats should not be empty in initial request"
argument_list|,
name|pivotField
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * recreates a pivot refinement request to a shard in a distributed query    * confirming that the per-pivot stats are returned, but not the top level stats    * because they shouldn't be overcounted.    */
DECL|method|doTestRefinementRequest
specifier|private
name|void
name|doTestRefinementRequest
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrParams
name|params
init|=
name|params
argument_list|(
literal|"facet.missing"
argument_list|,
literal|"true"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"4"
argument_list|,
literal|"distrib"
argument_list|,
literal|"false"
argument_list|,
comment|// "wt", "javabin",
comment|// "version", "2",
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"fpt0"
argument_list|,
literal|"~krakow"
argument_list|,
literal|"facet.pivot.mincount"
argument_list|,
literal|"-1"
argument_list|,
literal|"isShard"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"{!fpt=0 stats=st1}place_t,company_t"
argument_list|,
literal|"stats"
argument_list|,
literal|"false"
argument_list|,
literal|"stats.field"
argument_list|,
literal|"{!key=sk1 tag=st1,st2}price_ti"
argument_list|)
decl_stmt|;
name|QueryResponse
name|rsp
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|query
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"pivot refine request should *NOT* include top level stats"
argument_list|,
name|rsp
operator|.
name|getFieldStatsInfo
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PivotField
argument_list|>
name|placePivots
init|=
name|rsp
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"place_t,company_t"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"asked to refine exactly one place"
argument_list|,
literal|1
argument_list|,
name|placePivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"pivot stats should not be empty in refinement request"
argument_list|,
name|placePivots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFieldStatsInfo
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

