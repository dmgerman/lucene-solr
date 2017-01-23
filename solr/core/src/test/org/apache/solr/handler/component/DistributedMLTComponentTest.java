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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
operator|.
name|Slow
import|;
end_import

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
name|TestUtil
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
name|SolrDocumentList
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
name|MoreLikeThisParams
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|stats
operator|.
name|ExactStatsCache
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
name|search
operator|.
name|stats
operator|.
name|LRUStatsCache
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
name|search
operator|.
name|stats
operator|.
name|LocalStatsCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/**  * Test for distributed MoreLikeThisComponent's   *  * @since solr 4.1  *  * @see org.apache.solr.handler.component.MoreLikeThisComponent  */
end_comment

begin_class
annotation|@
name|Slow
DECL|class|DistributedMLTComponentTest
specifier|public
class|class
name|DistributedMLTComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|requestHandlerName
specifier|private
name|String
name|requestHandlerName
decl_stmt|;
DECL|method|DistributedMLTComponentTest
specifier|public
name|DistributedMLTComponentTest
parameter_list|()
block|{
name|stress
operator|=
literal|0
expr_stmt|;
block|}
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
name|requestHandlerName
operator|=
literal|"mltrh"
expr_stmt|;
name|super
operator|.
name|distribSetUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|int
name|statsType
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|statsType
operator|==
literal|1
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|ExactStatsCache
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|statsType
operator|==
literal|2
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|LRUStatsCache
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.statsCache"
argument_list|,
name|LocalStatsCache
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.statsCache"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|3
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
name|index
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"toyota"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"chevrolet"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"suzuki"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ford"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ferrari"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"jaguar"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"mclaren moon or the moon and moon moon shine and the moon but moon was good foxes too"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"sonata"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick red fox jumped over the lazy big and large brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The fat red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The slim red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped moon over the lazy brown dogs moon. Of course moon. Foxes and moon come back to the foxes and moon"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The hose red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The court red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"22"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"23"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"24"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The file red fox jumped over the lazy brown dogs."
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"25"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"rod fix"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"y"
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
comment|// we care only about the mlt results
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
comment|// currently distrib mlt is sorting by score (even though it's not really comparable across shards)
comment|// so it may not match the sort of single shard mlt
name|handle
operator|.
name|put
argument_list|(
literal|"17"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"match_none"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:sonata"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"24"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"23"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"22"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"21"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"20"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"19"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"18"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"17"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"16"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"15"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"14"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"13"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"7"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
comment|// keep in mind that MLT params influence stats that are calulated
comment|// per shard - because of this, depending on params, distrib and single
comment|// shard queries will not match.
comment|// because distrib and single node do not currently sort exactly the same,
comment|// we ask for an mlt.count of 20 to ensure both include all results
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:moon"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|2
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|1
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"mlt.count"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:fox"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|1
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|1
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"mlt.count"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:the red fox"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|1
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|1
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"mlt.count"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:blue moon"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|1
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|1
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"mlt.count"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
comment|// let's query by specifying multiple mlt.fl as comma-separated values
name|QueryResponse
name|response
init|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:moon"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|2
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|1
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt1,lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"mlt.count"
argument_list|,
literal|"20"
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|moreLikeThis
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"moreLikeThis"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|idVsMLTCount
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|moreLikeThis
control|)
block|{
name|SolrDocumentList
name|docList
init|=
operator|(
name|SolrDocumentList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|idVsMLTCount
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|docList
operator|.
name|getNumFound
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// let's query by specifying multiple mlt.fl as multiple request parameters
name|response
operator|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:moon"
argument_list|,
literal|"fl"
argument_list|,
name|id
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_TERM_FREQ
argument_list|,
literal|2
argument_list|,
name|MoreLikeThisParams
operator|.
name|MIN_DOC_FREQ
argument_list|,
literal|1
argument_list|,
literal|"sort"
argument_list|,
literal|"id desc"
argument_list|,
literal|"mlt"
argument_list|,
literal|"true"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt1"
argument_list|,
literal|"mlt.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"shards.qt"
argument_list|,
name|requestHandlerName
argument_list|,
literal|"mlt.count"
argument_list|,
literal|"20"
argument_list|)
expr_stmt|;
name|moreLikeThis
operator|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|get
argument_list|(
literal|"moreLikeThis"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|moreLikeThis
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Long
name|expected
init|=
name|idVsMLTCount
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Long
name|actual
init|=
operator|(
operator|(
name|SolrDocumentList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|getNumFound
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"MLT mismatch for id="
operator|+
name|key
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

