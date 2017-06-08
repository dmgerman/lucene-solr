begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

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
name|JSONTestUtil
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
name|SolrTestCaseHS
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
name|SolrTestCaseJ4
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
name|SolrClient
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
name|util
operator|.
name|SimpleOrderedMap
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
name|request
operator|.
name|SolrQueryRequest
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

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_class
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressPointFields
DECL|class|TestJsonFacetRefinement
specifier|public
class|class
name|TestJsonFacetRefinement
extends|extends
name|SolrTestCaseHS
block|{
DECL|field|servers
specifier|private
specifier|static
name|SolrInstances
name|servers
decl_stmt|;
comment|// for distributed testing
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|true
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|initServers
specifier|public
specifier|static
name|void
name|initServers
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|servers
operator|==
literal|null
condition|)
block|{
name|servers
operator|=
operator|new
name|SolrInstances
argument_list|(
literal|3
argument_list|,
literal|"solrconfig-tlog.xml"
argument_list|,
literal|"schema_latest.xml"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|afterTests
specifier|public
specifier|static
name|void
name|afterTests
parameter_list|()
throws|throws
name|Exception
block|{
name|JSONTestUtil
operator|.
name|failRepeatedKeys
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|servers
operator|!=
literal|null
condition|)
block|{
name|servers
operator|.
name|stop
argument_list|()
expr_stmt|;
name|servers
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|// todo - pull up to test base class?
DECL|method|matchJSON
specifier|public
name|void
name|matchJSON
parameter_list|(
name|String
name|json
parameter_list|,
name|double
name|delta
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|test
range|:
name|tests
control|)
block|{
if|if
condition|(
name|test
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|json
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|test
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|String
name|err
init|=
name|JSONTestUtil
operator|.
name|match
argument_list|(
name|json
argument_list|,
name|test
argument_list|,
name|delta
argument_list|)
decl_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"JSON failed validation. error="
operator|+
name|err
operator|+
literal|"\n expected ="
operator|+
name|test
operator|+
literal|"\n got = "
operator|+
name|json
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|match
specifier|public
name|void
name|match
parameter_list|(
name|Object
name|input
parameter_list|,
name|double
name|delta
parameter_list|,
name|String
modifier|...
name|tests
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|test
range|:
name|tests
control|)
block|{
name|String
name|err
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|test
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|input
operator|!=
literal|null
condition|)
block|{
name|err
operator|=
literal|"expected null"
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|err
operator|=
literal|"got null"
expr_stmt|;
block|}
else|else
block|{
name|err
operator|=
name|JSONTestUtil
operator|.
name|matchObj
argument_list|(
name|input
argument_list|,
name|test
argument_list|,
name|delta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"JSON failed validation. error="
operator|+
name|err
operator|+
literal|"\n expected ="
operator|+
name|test
operator|+
literal|"\n got = "
operator|+
name|input
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Use SimpleOrderedMap rather than Map to match responses from shards */
DECL|method|fromJSON
specifier|public
specifier|static
name|Object
name|fromJSON
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|ObjectBuilder
name|ob
init|=
operator|new
name|ObjectBuilder
argument_list|(
name|parser
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Object
name|newObject
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|SimpleOrderedMap
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addKeyVal
parameter_list|(
name|Object
name|map
parameter_list|,
name|Object
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|SimpleOrderedMap
operator|)
name|map
operator|)
operator|.
name|add
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
name|ob
operator|.
name|getObject
argument_list|()
return|;
block|}
DECL|method|doTestRefine
name|void
name|doTestRefine
parameter_list|(
name|String
name|facet
parameter_list|,
name|String
modifier|...
name|responsesAndTests
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
try|try
block|{
name|int
name|nShards
init|=
name|responsesAndTests
operator|.
name|length
operator|/
literal|2
decl_stmt|;
name|Object
name|jsonFacet
init|=
name|ObjectBuilder
operator|.
name|fromJSON
argument_list|(
name|facet
argument_list|)
decl_stmt|;
name|FacetParser
name|parser
init|=
operator|new
name|FacetTopParser
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|FacetRequest
name|facetRequest
init|=
name|parser
operator|.
name|parse
argument_list|(
name|jsonFacet
argument_list|)
decl_stmt|;
name|FacetMerger
name|merger
init|=
literal|null
decl_stmt|;
name|FacetMerger
operator|.
name|Context
name|ctx
init|=
operator|new
name|FacetMerger
operator|.
name|Context
argument_list|(
name|nShards
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nShards
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|response
init|=
name|fromJSON
argument_list|(
name|responsesAndTests
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|merger
operator|=
name|facetRequest
operator|.
name|createFacetMerger
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
name|ctx
operator|.
name|newShard
argument_list|(
literal|"s"
operator|+
name|i
argument_list|)
expr_stmt|;
name|merger
operator|.
name|merge
argument_list|(
name|response
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nShards
condition|;
name|i
operator|++
control|)
block|{
name|ctx
operator|.
name|setShard
argument_list|(
literal|"s"
operator|+
name|i
argument_list|)
expr_stmt|;
name|Object
name|refinement
init|=
name|merger
operator|.
name|getRefinement
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|String
name|tests
init|=
name|responsesAndTests
index|[
name|nShards
operator|+
name|i
index|]
decl_stmt|;
name|match
argument_list|(
name|refinement
argument_list|,
literal|1e-5
argument_list|,
name|tests
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMerge
specifier|public
name|void
name|testMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestRefine
argument_list|(
literal|"{x : {type:terms, field:X, limit:2, refine:true} }"
argument_list|,
comment|// the facet request
literal|"{x: {buckets:[{val:x1, count:5}, {val:x2, count:3}] } }"
argument_list|,
comment|// shard0 response
literal|"{x: {buckets:[{val:x2, count:4}, {val:x3, count:2}] } }"
argument_list|,
comment|// shard1 response
literal|null
argument_list|,
comment|// shard0 expected refinement info
literal|"=={x:{_l:[x1]}}"
comment|// shard1 expected refinement info
argument_list|)
expr_stmt|;
comment|// same test w/o refinement turned on
name|doTestRefine
argument_list|(
literal|"{x : {type:terms, field:X, limit:2} }"
argument_list|,
comment|// the facet request
literal|"{x: {buckets:[{val:x1, count:5}, {val:x2, count:3}] } }"
argument_list|,
comment|// shard0 response
literal|"{x: {buckets:[{val:x2, count:4}, {val:x3, count:2}] } }"
argument_list|,
comment|// shard1 response
literal|null
argument_list|,
comment|// shard0 expected refinement info
literal|null
comment|// shard1 expected refinement info
argument_list|)
expr_stmt|;
comment|// same test, but nested in query facet
name|doTestRefine
argument_list|(
literal|"{top:{type:query, q:'foo_s:myquery', facet:{x : {type:terms, field:X, limit:2, refine:true} } } }"
argument_list|,
comment|// the facet request
literal|"{top: {x: {buckets:[{val:x1, count:5}, {val:x2, count:3}] } } }"
argument_list|,
comment|// shard0 response
literal|"{top: {x: {buckets:[{val:x2, count:4}, {val:x3, count:2}] } } }"
argument_list|,
comment|// shard1 response
literal|null
argument_list|,
comment|// shard0 expected refinement info
literal|"=={top:{x:{_l:[x1]}}}"
comment|// shard1 expected refinement info
argument_list|)
expr_stmt|;
comment|// same test w/o refinement turned on
name|doTestRefine
argument_list|(
literal|"{top:{type:query, q:'foo_s:myquery', facet:{x : {type:terms, field:X, limit:2, refine:false} } } }"
argument_list|,
literal|"{top: {x: {buckets:[{val:x1, count:5}, {val:x2, count:3}] } } }"
argument_list|,
comment|// shard0 response
literal|"{top: {x: {buckets:[{val:x2, count:4}, {val:x3, count:2}] } } }"
argument_list|,
comment|// shard1 response
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// same test, but nested in a terms facet
name|doTestRefine
argument_list|(
literal|"{top:{type:terms, field:Afield, facet:{x : {type:terms, field:X, limit:2, refine:true} } } }"
argument_list|,
literal|"{top: {buckets:[{val:'A', count:2, x:{buckets:[{val:x1, count:5},{val:x2, count:3}]} } ] } }"
argument_list|,
literal|"{top: {buckets:[{val:'A', count:1, x:{buckets:[{val:x2, count:4},{val:x3, count:2}]} } ] } }"
argument_list|,
literal|null
argument_list|,
literal|"=={top: {"
operator|+
literal|"_s:[  ['A' , {x:{_l:[x1]}} ]  ]"
operator|+
literal|"    }  "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// same test, but nested in range facet
name|doTestRefine
argument_list|(
literal|"{top:{type:range, field:R, start:0, end:1, gap:1, facet:{x : {type:terms, field:X, limit:2, refine:true} } } }"
argument_list|,
literal|"{top: {buckets:[{val:0, count:2, x:{buckets:[{val:x1, count:5},{val:x2, count:3}]} } ] } }"
argument_list|,
literal|"{top: {buckets:[{val:0, count:1, x:{buckets:[{val:x2, count:4},{val:x3, count:2}]} } ] } }"
argument_list|,
literal|null
argument_list|,
literal|"=={top: {"
operator|+
literal|"_s:[  [0 , {x:{_l:[x1]}} ]  ]"
operator|+
literal|"    }  "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// for testing partial _p, we need a partial facet within a partial facet
name|doTestRefine
argument_list|(
literal|"{top:{type:terms, field:Afield, refine:true, limit:1, facet:{x : {type:terms, field:X, limit:1, refine:true} } } }"
argument_list|,
literal|"{top: {buckets:[{val:'A', count:2, x:{buckets:[{val:x1, count:5},{val:x2, count:3}]} } ] } }"
argument_list|,
literal|"{top: {buckets:[{val:'B', count:1, x:{buckets:[{val:x2, count:4},{val:x3, count:2}]} } ] } }"
argument_list|,
literal|null
argument_list|,
literal|"=={top: {"
operator|+
literal|"_p:[  ['A' , {x:{_l:[x1]}} ]  ]"
operator|+
literal|"    }  "
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test partial _p under a missing bucket
name|doTestRefine
argument_list|(
literal|"{top:{type:terms, field:Afield, refine:true, limit:1, missing:true, facet:{x : {type:terms, field:X, limit:1, refine:true} } } }"
argument_list|,
literal|"{top: {buckets:[], missing:{count:12, x:{buckets:[{val:x2, count:4},{val:x3, count:2}]} }  } }"
argument_list|,
literal|"{top: {buckets:[], missing:{count:10, x:{buckets:[{val:x1, count:5},{val:x4, count:3}]} }  } }"
argument_list|,
literal|"=={top: {"
operator|+
literal|"missing:{x:{_l:[x1]}}"
operator|+
literal|"    }  "
operator|+
literal|"}"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicRefinement
specifier|public
name|void
name|testBasicRefinement
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|p
init|=
name|params
argument_list|(
literal|"cat_s"
argument_list|,
literal|"cat_s"
argument_list|,
literal|"xy_s"
argument_list|,
literal|"xy_s"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_d"
argument_list|,
literal|"qw_s"
argument_list|,
literal|"qw_s"
argument_list|,
literal|"er_s"
argument_list|,
literal|"er_s"
argument_list|)
decl_stmt|;
name|doBasicRefinement
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|p
operator|.
name|set
argument_list|(
literal|"terms"
argument_list|,
literal|"method:dvhash,"
argument_list|)
expr_stmt|;
name|doBasicRefinement
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// multi-valued strings
name|p
operator|=
name|params
argument_list|(
literal|"cat_s"
argument_list|,
literal|"cat_ss"
argument_list|,
literal|"xy_s"
argument_list|,
literal|"xy_ss"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_d"
argument_list|,
literal|"qw_s"
argument_list|,
literal|"qw_ss"
argument_list|,
literal|"er_s"
argument_list|,
literal|"er_ss"
argument_list|)
expr_stmt|;
name|doBasicRefinement
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|// single valued docvalues
name|p
operator|=
name|params
argument_list|(
literal|"cat_s"
argument_list|,
literal|"cat_sd"
argument_list|,
literal|"xy_s"
argument_list|,
literal|"xy_sd"
argument_list|,
literal|"num_d"
argument_list|,
literal|"num_dd"
argument_list|,
literal|"qw_s"
argument_list|,
literal|"qw_sd"
argument_list|,
literal|"er_s"
argument_list|,
literal|"er_sd"
argument_list|)
expr_stmt|;
name|doBasicRefinement
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|doBasicRefinement
specifier|public
name|void
name|doBasicRefinement
parameter_list|(
name|ModifiableSolrParams
name|p
parameter_list|)
throws|throws
name|Exception
block|{
name|initServers
argument_list|()
expr_stmt|;
name|Client
name|client
init|=
name|servers
operator|.
name|getClient
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|queryDefaults
argument_list|()
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|servers
operator|.
name|getShards
argument_list|()
argument_list|,
literal|"debugQuery"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SolrClient
argument_list|>
name|clients
init|=
name|client
operator|.
name|getClientProvider
argument_list|()
operator|.
name|all
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|clients
operator|.
name|size
argument_list|()
operator|>=
literal|3
argument_list|)
expr_stmt|;
name|client
operator|.
name|deleteByQuery
argument_list|(
literal|"*:*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|cat_s
init|=
name|p
operator|.
name|get
argument_list|(
literal|"cat_s"
argument_list|)
decl_stmt|;
name|String
name|xy_s
init|=
name|p
operator|.
name|get
argument_list|(
literal|"xy_s"
argument_list|)
decl_stmt|;
name|String
name|qw_s
init|=
name|p
operator|.
name|get
argument_list|(
literal|"qw_s"
argument_list|)
decl_stmt|;
name|String
name|er_s
init|=
name|p
operator|.
name|get
argument_list|(
literal|"er_s"
argument_list|)
decl_stmt|;
comment|// this field is designed to test numBuckets refinement... the first phase will only have a single bucket returned for the top count bucket of cat_s
name|String
name|num_d
init|=
name|p
operator|.
name|get
argument_list|(
literal|"num_d"
argument_list|)
decl_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"01"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|,
name|xy_s
argument_list|,
literal|"X"
argument_list|,
name|num_d
argument_list|,
operator|-
literal|1
argument_list|,
name|qw_s
argument_list|,
literal|"Q"
argument_list|,
name|er_s
argument_list|,
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
comment|// A wins count tie
name|clients
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"02"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|xy_s
argument_list|,
literal|"Y"
argument_list|,
name|num_d
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|xy_s
argument_list|,
literal|"X"
argument_list|,
name|num_d
argument_list|,
operator|-
literal|5
argument_list|,
name|er_s
argument_list|,
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
comment|// B highest count
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|xy_s
argument_list|,
literal|"Y"
argument_list|,
name|num_d
argument_list|,
operator|-
literal|11
argument_list|,
name|qw_s
argument_list|,
literal|"W"
argument_list|)
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|,
name|xy_s
argument_list|,
literal|"X"
argument_list|,
name|num_d
argument_list|,
literal|7
argument_list|,
name|er_s
argument_list|,
literal|"R"
argument_list|)
argument_list|)
expr_stmt|;
comment|// "R" will only be picked up via refinement when parent facet is cat_s
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"21"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|,
name|xy_s
argument_list|,
literal|"X"
argument_list|,
name|num_d
argument_list|,
literal|17
argument_list|,
name|qw_s
argument_list|,
literal|"W"
argument_list|,
name|er_s
argument_list|,
literal|"E"
argument_list|)
argument_list|)
expr_stmt|;
comment|// A highest count
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"22"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"A"
argument_list|,
name|xy_s
argument_list|,
literal|"Y"
argument_list|,
name|num_d
argument_list|,
operator|-
literal|19
argument_list|)
argument_list|)
expr_stmt|;
name|clients
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|add
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"23"
argument_list|,
literal|"all_s"
argument_list|,
literal|"all"
argument_list|,
name|cat_s
argument_list|,
literal|"B"
argument_list|,
name|xy_s
argument_list|,
literal|"X"
argument_list|,
name|num_d
argument_list|,
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Shard responses should be A=1, B=2, A=2, merged should be "A=3, B=2"
comment|// One shard will have _facet_={"refine":{"cat0":{"_l":["A"]}}} on the second phase
comment|/****     // fake a refinement request... good for development/debugging     assertJQ(clients.get(1),         params(p, "q", "*:*",     "_facet_","{refine:{cat0:{_l:[A]}}}", "isShard","true", "distrib","false", "shards.purpose","2097216", "ids","11,12,13",             "json.facet", "{" +                 "cat0:{type:terms, field:cat_s, sort:'count desc', limit:1, overrequest:0, refine:true}" +                 "}"         )         , "facets=={foo:555}"     );     ****/
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"cat0:{${terms} type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0, refine:false}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", cat0:{ buckets:[ {val:A,count:3} ] }"
operator|+
comment|// w/o overrequest and refinement, count is lower than it should be (we don't see the A from the middle shard)
literal|"}"
argument_list|)
expr_stmt|;
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"cat0:{${terms} type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0, refine:true}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", cat0:{ buckets:[ {val:A,count:4} ] }"
operator|+
comment|// w/o overrequest, we need refining to get the correct count.
literal|"}"
argument_list|)
expr_stmt|;
comment|// basic refining test through/under a query facet
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"q1 : { type:query, q:'*:*', facet:{"
operator|+
literal|"cat0:{${terms} type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0, refine:true}"
operator|+
literal|"}}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", q1:{ count:8, cat0:{ buckets:[ {val:A,count:4} ] }   }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// basic refining test through/under a range facet
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"r1 : { type:range, field:${num_d} start:-20, end:20, gap:40   , facet:{"
operator|+
literal|"cat0:{${terms} type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0, refine:true}"
operator|+
literal|"}}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", r1:{ buckets:[{val:-20.0,count:8,  cat0:{buckets:[{val:A,count:4}]}  }]   }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test that basic stats work for refinement
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"cat0:{${terms} type:terms, field:${cat_s}, sort:'count desc', limit:1, overrequest:0, refine:true, facet:{ stat1:'sum(${num_d})'}   }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", cat0:{ buckets:[ {val:A,count:4, stat1:4.0} ] }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test sorting buckets by a different stat
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" cat0:{${terms} type:terms, field:${cat_s}, sort:'min1 asc', limit:1, overrequest:0, refine:false, facet:{ min1:'min(${num_d})'}   }"
operator|+
literal|",cat1:{${terms} type:terms, field:${cat_s}, sort:'min1 asc', limit:1, overrequest:0, refine:true,  facet:{ min1:'min(${num_d})'}   }"
operator|+
literal|",qfacet:{type:query, q:'*:*', facet:{  cat2:{${terms} type:terms, field:${cat_s}, sort:'min1 asc', limit:1, overrequest:0, refine:true,  facet:{ min1:'min(${num_d})'}   }  }}"
operator|+
comment|// refinement needed through a query facet
literal|",allf:{${terms} type:terms, field:all_s,  facet:{  cat3:{${terms} type:terms, field:${cat_s}, sort:'min1 asc', limit:1, overrequest:0, refine:true,  facet:{ min1:'min(${num_d})'}   }  }}"
operator|+
comment|// refinement needed through field facet
literal|",sum1:'sum(${num_d})'"
operator|+
comment|// make sure that root bucket stats aren't affected by refinement
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", cat0:{ buckets:[ {val:A,count:3, min1:-19.0} ] }"
operator|+
comment|// B wins in shard2, so we're missing the "A" count for that shard w/o refinement.
literal|", cat1:{ buckets:[ {val:A,count:4, min1:-19.0} ] }"
operator|+
comment|// with refinement, we get the right count
literal|", qfacet:{ count:8,  cat2:{ buckets:[ {val:A,count:4, min1:-19.0} ] }    }"
operator|+
comment|// just like the previous response, just nested under a query facet
literal|", allf:{ buckets:[  {cat3:{ buckets:[ {val:A,count:4, min1:-19.0} ] }  ,count:8,val:all   }]  }"
operator|+
comment|// just like the previous response, just nested under a field facet
literal|", sum1:2.0"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test partial buckets (field facet within field facet)
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"ab:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true,  facet:{  xy:{${terms} type:terms, field:${xy_s}, limit:1, overrequest:0, refine:true   }  }}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", ab:{ buckets:[  {val:A, count:4, xy:{buckets:[ {val:X,count:3}]}  }]  }"
operator|+
comment|// just like the previous response, just nested under a field facet
literal|"}"
argument_list|)
expr_stmt|;
comment|// test that sibling facets and stats are included for _p buckets, but skipped for _s buckets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|" ab :{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true,  facet:{  xy:{${terms} type:terms, field:${xy_s}, limit:1, overrequest:0, refine:true}, qq:{query:'*:*'},ww:'sum(${num_d})'  }}"
operator|+
literal|",ab2:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:false, facet:{  xy:{${terms} type:terms, field:${xy_s}, limit:1, overrequest:0, refine:true}, qq:{query:'*:*'},ww:'sum(${num_d})'  }}"
operator|+
comment|// top level refine=false shouldn't matter
literal|",allf :{${terms} type:terms, field:all_s, limit:1, overrequest:0, refine:true,  facet:{cat:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true}, qq:{query:'*:*'},ww:'sum(${num_d})'  }}"
operator|+
literal|",allf2:{${terms} type:terms, field:all_s, limit:1, overrequest:0, refine:false, facet:{cat:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true}, qq:{query:'*:*'},ww:'sum(${num_d})'  }}"
operator|+
comment|// top level refine=false shouldn't matter
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", ab:{ buckets:[  {val:A, count:4, xy:{buckets:[ {val:X,count:3}]}    ,qq:{count:4}, ww:4.0 }]  }"
operator|+
comment|// make sure qq and ww are included for _p buckets
literal|", allf:{ buckets:[ {count:8, val:all, cat:{buckets:[{val:A,count:4}]} ,qq:{count:8}, ww:2.0 }]  }"
operator|+
comment|// make sure qq and ww are excluded (not calculated again in another phase) for _s buckets
literal|", ab2:{ buckets:[  {val:A, count:4, xy:{buckets:[ {val:X,count:3}]}    ,qq:{count:4}, ww:4.0 }]  }"
operator|+
comment|// make sure qq and ww are included for _p buckets
literal|", allf2:{ buckets:[ {count:8, val:all, cat:{buckets:[{val:A,count:4}]} ,qq:{count:8}, ww:2.0 }]  }"
operator|+
comment|// make sure qq and ww are excluded (not calculated again in another phase) for _s buckets
literal|"}"
argument_list|)
expr_stmt|;
comment|// test refining under the special "missing" bucket of a field facet
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"f:{${terms} type:terms, field:missing_s, limit:1, overrequest:0, missing:true, refine:true,  facet:{  cat:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true   }  }}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", f:{ buckets:[], missing:{count:8, cat:{buckets:[{val:A,count:4}]}  }  }"
operator|+
comment|// just like the previous response, just nested under a field facet
literal|"}"
argument_list|)
expr_stmt|;
comment|// test filling in "missing" bucket for partially refined facets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
comment|// test all values missing in sub-facet
literal|" ab :{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:false,  facet:{  zz:{${terms} type:terms, field:missing_s, limit:1, overrequest:0, refine:false, missing:true}  }}"
operator|+
literal|",ab2:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true ,  facet:{  zz:{${terms} type:terms, field:missing_s, limit:1, overrequest:0, refine:true , missing:true}  }}"
operator|+
comment|// test some values missing in sub-facet (and test that this works with normal partial bucket refinement)
literal|", cd :{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:false,  facet:{  qw:{${terms} type:terms, field:${qw_s}, limit:1, overrequest:0, refine:false, missing:true,   facet:{qq:{query:'*:*'}}   }  }}"
operator|+
literal|", cd2:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true ,  facet:{  qw:{${terms} type:terms, field:${qw_s}, limit:1, overrequest:0, refine:true , missing:true,   facet:{qq:{query:'*:*'}}   }  }}"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", ab:{ buckets:[  {val:A, count:3, zz:{buckets:[], missing:{count:3}}}]  }"
operator|+
literal|",ab2:{ buckets:[  {val:A, count:4, zz:{buckets:[], missing:{count:4}}}]  }"
operator|+
literal|", cd:{ buckets:[  {val:A, count:3,  qw:{buckets:[{val:Q, count:1, qq:{count:1}}], missing:{count:1,qq:{count:1}}}}]  }"
operator|+
literal|",cd2:{ buckets:[  {val:A, count:4,  qw:{buckets:[{val:Q, count:1, qq:{count:1}}], missing:{count:2,qq:{count:2}}}}]  }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test filling in missing "allBuckets"
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"  cat :{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:false, allBuckets:true, facet:{  xy:{${terms} type:terms, field:${xy_s}, limit:1, overrequest:0, allBuckets:true, refine:false}  }  }"
operator|+
literal|", cat2:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true , allBuckets:true, facet:{  xy:{${terms} type:terms, field:${xy_s}, limit:1, overrequest:0, allBuckets:true, refine:true }  }  }"
operator|+
literal|", cat3:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true , allBuckets:true, facet:{  xy:{${terms} type:terms, field:${xy_s}, limit:1, overrequest:0, allBuckets:true, refine:true , facet:{f:'sum(${num_d})'}   }  }  }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", cat:{ allBuckets:{count:8}, buckets:[  {val:A, count:3, xy:{buckets:[{count:2, val:X}], allBuckets:{count:3}}}]  }"
operator|+
literal|",cat2:{ allBuckets:{count:8}, buckets:[  {val:A, count:4, xy:{buckets:[{count:3, val:X}], allBuckets:{count:4}}}]  }"
operator|+
literal|",cat3:{ allBuckets:{count:8}, buckets:[  {val:A, count:4, xy:{buckets:[{count:3, val:X, f:23.0}], allBuckets:{count:4, f:4.0}}}]  }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// test filling in missing numBuckets
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"  cat :{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:false, numBuckets:true, facet:{  er:{${terms} type:terms, field:${er_s}, limit:1, overrequest:0, numBuckets:true, refine:false}  }  }"
operator|+
literal|", cat2:{${terms} type:terms, field:${cat_s}, limit:1, overrequest:0, refine:true , numBuckets:true, facet:{  er:{${terms} type:terms, field:${er_s}, limit:1, overrequest:0, numBuckets:true, refine:true }  }  }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"facets=={ count:8"
operator|+
literal|", cat:{ numBuckets:2, buckets:[  {val:A, count:3, er:{numBuckets:1,buckets:[{count:2, val:E}]  }}]  }"
operator|+
comment|// the "R" bucket will not be seen w/o refinement
literal|",cat2:{ numBuckets:2, buckets:[  {val:A, count:4, er:{numBuckets:2,buckets:[{count:2, val:E}]  }}]  }"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sort_limit_over
init|=
literal|"sort:'count desc', limit:1, overrequest:0, "
decl_stmt|;
comment|// simplistic join domain testing: no refinement == low count
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"${xy_s}:Y"
argument_list|,
comment|// query only matches one doc per shard
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"  cat0:{${terms} type:terms, field:${cat_s}, "
operator|+
name|sort_limit_over
operator|+
literal|" refine:false,"
operator|+
comment|// self join on all_s ensures every doc on every shard included in facets
literal|"        domain: { join: { from:all_s, to:all_s } } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|,
literal|"facets=={ count:3, "
operator|+
comment|// w/o overrequest and refinement, count for 'A' is lower than it should be
comment|// (we don't see the A from the middle shard)
literal|"          cat0:{ buckets:[ {val:A,count:3} ] } }"
argument_list|)
expr_stmt|;
comment|// simplistic join domain testing: refinement == correct count
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"${xy_s}:Y"
argument_list|,
comment|// query only matches one doc per shard
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
literal|"  cat0:{${terms} type:terms, field:${cat_s}, "
operator|+
name|sort_limit_over
operator|+
literal|" refine:true,"
operator|+
comment|// self join on all_s ensures every doc on every shard included in facets
literal|"        domain: { join: { from:all_s, to:all_s } } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|,
literal|"facets=={ count:3,"
operator|+
comment|// w/o overrequest, we need refining to get the correct count for 'A'.
literal|"          cat0:{ buckets:[ {val:A,count:4} ] } }"
argument_list|)
expr_stmt|;
comment|// contrived join domain + refinement (at second level) + testing
name|client
operator|.
name|testJQ
argument_list|(
name|params
argument_list|(
name|p
argument_list|,
literal|"q"
argument_list|,
literal|"${xy_s}:Y"
argument_list|,
comment|// query only matches one doc per shard
literal|"json.facet"
argument_list|,
literal|"{"
operator|+
comment|// top level facet has a single term
literal|"  all:{${terms} type:terms, field:all_s, "
operator|+
name|sort_limit_over
operator|+
literal|" refine:true, "
operator|+
literal|"       facet:{  "
operator|+
comment|// subfacet will facet on cat after joining on all (so all docs should be included in subfacet)
literal|"         cat0:{${terms} type:terms, field:${cat_s}, "
operator|+
name|sort_limit_over
operator|+
literal|" refine:true,"
operator|+
literal|"               domain: { join: { from:all_s, to:all_s } } } } }"
operator|+
literal|"}"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|,
literal|"facets=={ count:3,"
operator|+
comment|// all 3 docs matching base query have same 'all' value in top facet
literal|"          all:{ buckets:[ { val:all, count:3, "
operator|+
comment|// sub facet has refinement, so count for 'A' should be correct
literal|"                            cat0:{ buckets: [{val:A,count:4}] } } ] } }"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

