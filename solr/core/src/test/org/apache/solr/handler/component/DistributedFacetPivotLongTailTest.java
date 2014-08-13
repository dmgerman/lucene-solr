begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|SolrServer
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
name|SolrServerException
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
name|SolrInputDocument
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
name|FacetParams
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
comment|/**  * test demonstrating how overrequesting helps finds top-terms in the "long tail"   * of shards that don't have even distributions of terms (something that can be common  * in cases of custom sharding -- even if you don't know that there is a corrolation   * between the property you are sharding on and the property you are faceting on).  *  * NOTE: This test ignores the control collection (in single node mode, there is no   * need for the overrequesting, all the data is local -- so comparisons with it wouldn't   * be valid in the cases we are testing here)  */
end_comment

begin_class
DECL|class|DistributedFacetPivotLongTailTest
specifier|public
class|class
name|DistributedFacetPivotLongTailTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedFacetPivotLongTailTest
specifier|public
name|DistributedFacetPivotLongTailTest
parameter_list|()
block|{
name|this
operator|.
name|fixShardCount
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|shardCount
operator|=
literal|3
expr_stmt|;
block|}
DECL|field|docNumber
specifier|private
name|int
name|docNumber
init|=
literal|0
decl_stmt|;
DECL|method|getDocNum
specifier|public
name|int
name|getDocNum
parameter_list|()
block|{
name|docNumber
operator|++
expr_stmt|;
return|return
name|docNumber
return|;
block|}
annotation|@
name|Override
DECL|method|doTest
specifier|public
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SolrServer
name|shard0
init|=
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SolrServer
name|shard1
init|=
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|SolrServer
name|shard2
init|=
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// the 5 top foo_s terms have 100 docs each on every shard
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|shard2
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 20 foo_s terms that come in "second" with 50 docs each
comment|// on both shard0& shard1 ("bbb_")
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|20
condition|;
name|j
operator|++
control|)
block|{
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb"
operator|+
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb"
operator|+
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// distracting term appears on only on shard2 50 times
name|shard2
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"junkA"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// put "bbb0" on shard2 exactly once to sanity check refinement
name|shard2
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"bbb0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// long 'tail' foo_s term appears in 45 docs on every shard
comment|// foo_s:tail is the only term with bar_s sub-pivot terms
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|45
condition|;
name|i
operator|++
control|)
block|{
comment|// for sub-pivot, shard0& shard1 have 6 docs each for "tailB"
comment|// but the top 5 terms are ccc(0-4) -- 7 on each shard
comment|// (4 docs each have junk terms)
name|String
name|sub_term
init|=
operator|(
name|i
operator|<
literal|35
operator|)
condition|?
literal|"ccc"
operator|+
operator|(
name|i
operator|%
literal|5
operator|)
else|:
operator|(
operator|(
name|i
operator|<
literal|41
operator|)
condition|?
literal|"tailB"
else|:
literal|"junkA"
operator|)
decl_stmt|;
name|shard0
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"tail"
argument_list|,
literal|"bar_s"
argument_list|,
name|sub_term
argument_list|)
argument_list|)
expr_stmt|;
name|shard1
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"tail"
argument_list|,
literal|"bar_s"
argument_list|,
name|sub_term
argument_list|)
argument_list|)
expr_stmt|;
comment|// shard2's top 5 sub-pivot terms are junk only it has with 8 docs each
comment|// and 5 docs that use "tailB"
name|sub_term
operator|=
operator|(
name|i
operator|<
literal|40
operator|)
condition|?
literal|"junkB"
operator|+
operator|(
name|i
operator|%
literal|5
operator|)
else|:
literal|"tailB"
expr_stmt|;
name|shard2
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"tail"
argument_list|,
literal|"bar_s"
argument_list|,
name|sub_term
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// really long tail uncommon foo_s terms on shard2
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|shard2
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|getDocNum
argument_list|()
argument_list|,
literal|"foo_s"
argument_list|,
literal|"zzz"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|SolrParams
name|req
init|=
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"distrib"
argument_list|,
literal|"false"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"10"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"foo_s,bar_s"
argument_list|)
decl_stmt|;
comment|// sanity check that our expectations about each shard (non-distrib) are correct
name|PivotField
name|pivot
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|PivotField
argument_list|>
name|pivots
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|PivotField
argument_list|>
index|[]
name|shardPivots
init|=
operator|new
name|List
index|[
literal|3
index|]
decl_stmt|;
name|shardPivots
index|[
literal|0
index|]
operator|=
name|shard0
operator|.
name|query
argument_list|(
name|req
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
name|shardPivots
index|[
literal|1
index|]
operator|=
name|shard1
operator|.
name|query
argument_list|(
name|req
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
name|shardPivots
index|[
literal|2
index|]
operator|=
name|shard2
operator|.
name|query
argument_list|(
name|req
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
comment|// top 5 same on all shards
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|shardPivots
index|[
name|i
index|]
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|pivot
operator|=
name|shardPivots
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|"aaa"
operator|+
name|j
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|100
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// top 6-10 same on shard0& shard11
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|5
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|pivot
operator|=
name|shardPivots
index|[
name|i
index|]
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"bbb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|50
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// 6-10 on shard2
name|assertEquals
argument_list|(
literal|"junkA"
argument_list|,
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tail"
argument_list|,
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|45
argument_list|,
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bbb0"
argument_list|,
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|7
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|8
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|pivot
operator|=
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"zzz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check sub-shardPivots on "tail" from shard2
name|pivots
operator|=
name|shardPivots
index|[
literal|2
index|]
operator|.
name|get
argument_list|(
literal|6
argument_list|)
operator|.
name|getPivot
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
name|j
operator|++
control|)
block|{
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|j
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"junkB"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|8
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tailB"
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// if we disable overrequesting, we don't find the long tail
name|pivots
operator|=
name|queryServer
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|,
name|FacetParams
operator|.
name|FACET_OVERREQUEST_COUNT
argument_list|,
literal|"0"
argument_list|,
name|FacetParams
operator|.
name|FACET_OVERREQUEST_RATIO
argument_list|,
literal|"0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"6"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"foo_s,bar_s"
argument_list|)
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|300
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// even w/o the long tail, we should have still asked shard2 to refine bbb0
name|assertTrue
argument_list|(
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"bbb0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|101
argument_list|,
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// with default overrequesting, we should find the correct top 6 including
comment|// long tail and top sub-pivots
comment|// (even if we disable overrequesting on the sub-pivot)
for|for
control|(
name|ModifiableSolrParams
name|q
range|:
operator|new
name|ModifiableSolrParams
index|[]
block|{
name|params
argument_list|()
block|,
name|params
argument_list|(
literal|"f.bar_s.facet.overrequest.ratio"
argument_list|,
literal|"0"
argument_list|,
literal|"f.bar_s.facet.overrequest.count"
argument_list|,
literal|"0"
argument_list|)
block|}
control|)
block|{
name|q
operator|.
name|add
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"6"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"foo_s,bar_s"
argument_list|)
argument_list|)
expr_stmt|;
name|pivots
operator|=
name|queryServer
argument_list|(
name|q
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|300
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|"tail"
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|135
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the sub pivots
name|pivots
operator|=
name|pivot
operator|.
name|getPivot
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|"tailB"
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|17
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|6
condition|;
name|i
operator|++
control|)
block|{
comment|// ccc(0-4)
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"ccc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|14
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if we lower the facet.limit on the sub-pivot, overrequesting should still ensure
comment|// that we get the correct top5 including "tailB"
name|pivots
operator|=
name|queryServer
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"6"
argument_list|,
literal|"f.bar_s.facet.limit"
argument_list|,
literal|"5"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"foo_s,bar_s"
argument_list|)
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|300
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|"tail"
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|135
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the sub pivots
name|pivots
operator|=
name|pivot
operator|.
name|getPivot
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|"tailB"
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|17
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// ccc(0-3)
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"ccc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|14
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// however with a lower limit and overrequesting disabled,
comment|// we're going to miss out on tailB
name|pivots
operator|=
name|queryServer
argument_list|(
name|params
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"shards"
argument_list|,
name|getShardsString
argument_list|()
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"6"
argument_list|,
literal|"f.bar_s.facet.overrequest.ratio"
argument_list|,
literal|"0"
argument_list|,
literal|"f.bar_s.facet.overrequest.count"
argument_list|,
literal|"0"
argument_list|,
literal|"f.bar_s.facet.limit"
argument_list|,
literal|"5"
argument_list|,
literal|"facet.pivot"
argument_list|,
literal|"foo_s,bar_s"
argument_list|)
argument_list|)
operator|.
name|getFacetPivot
argument_list|()
operator|.
name|get
argument_list|(
literal|"foo_s,bar_s"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|300
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|"tail"
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|135
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// check the sub pivots
name|pivots
operator|=
name|pivot
operator|.
name|getPivot
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|pivots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// ccc(0-4)
name|pivot
operator|=
name|pivots
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
name|pivot
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"ccc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pivot
operator|.
name|toString
argument_list|()
argument_list|,
literal|14
argument_list|,
name|pivot
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

