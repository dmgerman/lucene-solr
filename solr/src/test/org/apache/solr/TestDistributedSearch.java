begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|common
operator|.
name|params
operator|.
name|CommonParams
import|;
end_import

begin_comment
comment|/**  * TODO? perhaps use:  *  http://docs.codehaus.org/display/JETTY/ServletTester  * rather then open a real connection?  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestDistributedSearch
specifier|public
class|class
name|TestDistributedSearch
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|field|t1
name|String
name|t1
init|=
literal|"a_t"
decl_stmt|;
DECL|field|i1
name|String
name|i1
init|=
literal|"a_si"
decl_stmt|;
DECL|field|nint
name|String
name|nint
init|=
literal|"n_i"
decl_stmt|;
DECL|field|tint
name|String
name|tint
init|=
literal|"n_ti"
decl_stmt|;
DECL|field|nfloat
name|String
name|nfloat
init|=
literal|"n_f"
decl_stmt|;
DECL|field|tfloat
name|String
name|tfloat
init|=
literal|"n_tf"
decl_stmt|;
DECL|field|ndouble
name|String
name|ndouble
init|=
literal|"n_d"
decl_stmt|;
DECL|field|tdouble
name|String
name|tdouble
init|=
literal|"n_td"
decl_stmt|;
DECL|field|nlong
name|String
name|nlong
init|=
literal|"n_l"
decl_stmt|;
DECL|field|tlong
name|String
name|tlong
init|=
literal|"n_tl"
decl_stmt|;
DECL|field|ndate
name|String
name|ndate
init|=
literal|"n_dt"
decl_stmt|;
DECL|field|tdate
name|String
name|tdate
init|=
literal|"n_tdt"
decl_stmt|;
DECL|field|oddField
name|String
name|oddField
init|=
literal|"oddField_s"
decl_stmt|;
DECL|field|missingField
name|String
name|missingField
init|=
literal|"ignore_exception__missing_but_valid_field_t"
decl_stmt|;
DECL|field|invalidField
name|String
name|invalidField
init|=
literal|"ignore_exception__invalid_field_not_in_schema"
decl_stmt|;
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
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|1
argument_list|,
name|i1
argument_list|,
literal|100
argument_list|,
name|tlong
argument_list|,
literal|100
argument_list|,
name|t1
argument_list|,
literal|"now is the time for all good men"
argument_list|,
literal|"foo_f"
argument_list|,
literal|1.414f
argument_list|,
literal|"foo_b"
argument_list|,
literal|"true"
argument_list|,
literal|"foo_d"
argument_list|,
literal|1.414d
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|2
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|tlong
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"to come to the aid of their country."
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|3
argument_list|,
name|i1
argument_list|,
literal|2
argument_list|,
name|tlong
argument_list|,
literal|2
argument_list|,
name|t1
argument_list|,
literal|"how now brown cow"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|4
argument_list|,
name|i1
argument_list|,
operator|-
literal|100
argument_list|,
name|tlong
argument_list|,
literal|101
argument_list|,
name|t1
argument_list|,
literal|"the quick fox jumped over the lazy dog"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|5
argument_list|,
name|i1
argument_list|,
literal|500
argument_list|,
name|tlong
argument_list|,
literal|500
argument_list|,
name|t1
argument_list|,
literal|"the quick fox jumped way over the lazy dog"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|6
argument_list|,
name|i1
argument_list|,
operator|-
literal|600
argument_list|,
name|tlong
argument_list|,
literal|600
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy sat on a wall"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|7
argument_list|,
name|i1
argument_list|,
literal|123
argument_list|,
name|tlong
argument_list|,
literal|123
argument_list|,
name|t1
argument_list|,
literal|"humpty dumpy had a great fall"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|8
argument_list|,
name|i1
argument_list|,
literal|876
argument_list|,
name|tlong
argument_list|,
literal|876
argument_list|,
name|t1
argument_list|,
literal|"all the kings horses and all the kings men"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|9
argument_list|,
name|i1
argument_list|,
literal|7
argument_list|,
name|tlong
argument_list|,
literal|7
argument_list|,
name|t1
argument_list|,
literal|"couldn't put humpty together again"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|10
argument_list|,
name|i1
argument_list|,
literal|4321
argument_list|,
name|tlong
argument_list|,
literal|4321
argument_list|,
name|t1
argument_list|,
literal|"this too shall pass"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|11
argument_list|,
name|i1
argument_list|,
operator|-
literal|987
argument_list|,
name|tlong
argument_list|,
literal|987
argument_list|,
name|t1
argument_list|,
literal|"An eye for eye only ends up making the whole world blind."
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|12
argument_list|,
name|i1
argument_list|,
literal|379
argument_list|,
name|tlong
argument_list|,
literal|379
argument_list|,
name|t1
argument_list|,
literal|"Great works are performed, not by strength, but by perseverance."
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|13
argument_list|,
name|i1
argument_list|,
literal|232
argument_list|,
name|tlong
argument_list|,
literal|232
argument_list|,
name|t1
argument_list|,
literal|"no eggs on wall, lesson learned"
argument_list|,
name|oddField
argument_list|,
literal|"odd man out"
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|14
argument_list|,
literal|"SubjectTerms_mfacet"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"mathematical models"
block|,
literal|"mathematical analysis"
block|}
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|15
argument_list|,
literal|"SubjectTerms_mfacet"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test 1"
block|,
literal|"test 2"
block|,
literal|"test3"
block|}
argument_list|)
expr_stmt|;
name|indexr
argument_list|(
name|id
argument_list|,
literal|16
argument_list|,
literal|"SubjectTerms_mfacet"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"test 1"
block|,
literal|"test 2"
block|,
literal|"test3"
block|}
argument_list|)
expr_stmt|;
name|String
index|[]
name|vals
init|=
operator|new
name|String
index|[
literal|100
index|]
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|vals
index|[
name|i
index|]
operator|=
literal|"test "
operator|+
name|i
expr_stmt|;
block|}
name|indexr
argument_list|(
name|id
argument_list|,
literal|17
argument_list|,
literal|"SubjectTerms_mfacet"
argument_list|,
name|vals
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|100
init|;
name|i
operator|<
literal|150
condition|;
name|i
operator|++
control|)
block|{
name|indexr
argument_list|(
name|id
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
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
comment|// random value sort
for|for
control|(
name|String
name|f
range|:
name|fieldNames
control|)
block|{
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|f
operator|+
literal|" desc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|f
operator|+
literal|" asc"
argument_list|)
expr_stmt|;
block|}
comment|// these queries should be exactly ordered and scores should exactly match
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|i1
operator|+
literal|" desc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|i1
operator|+
literal|" asc"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|i1
operator|+
literal|" desc"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|tlong
operator|+
literal|" asc"
argument_list|,
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
expr_stmt|;
comment|// test legacy behavior - "score"=="*,score"
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|tlong
operator|+
literal|" desc"
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
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|i1
argument_list|)
expr_stmt|;
comment|// does not expect maxScore. So if it comes ,ignore it. JavaBinCodec.writeSolrDocumentList()
comment|//is agnostic of request params.
name|handle
operator|.
name|remove
argument_list|(
literal|"maxScore"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|i1
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
comment|// even scores should match exactly here
name|handle
operator|.
name|put
argument_list|(
literal|"highlighting"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|UNORDERED
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
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"quick"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"all"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"start"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"all"
argument_list|,
literal|"fl"
argument_list|,
literal|"foofoofoo"
argument_list|,
literal|"start"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|// no fields in returned docs
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"all"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"start"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"score"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"quick"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"all"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"start"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"all"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"start"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"now their fox sat had put"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
name|t1
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"now their fox sat had put"
argument_list|,
literal|"fl"
argument_list|,
literal|"foofoofoo"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
name|t1
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"debug"
argument_list|,
name|UNORDERED
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"time"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"now their fox sat had put"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|// TODO: This test currently fails because debug info is obtained only
comment|// on shards with matches.
comment|/***     query("q","matchesnothing","fl","*,score",             "debugQuery", "true");         ***/
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"matchesnothing"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.limit"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.limit"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.limit"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.limit"
argument_list|,
operator|-
literal|1
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.limit"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"quick"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"all"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.offset"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// test faceting multiple things at once
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"quick"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"all"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"*:*"
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|)
expr_stmt|;
comment|// test filter tagging, facet exclusion, and naming (multi-select facet support)
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"{!key=myquick}quick"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"{!key=myall ex=a}all"
argument_list|,
literal|"facet.query"
argument_list|,
literal|"*:*"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=mykey ex=a}"
operator|+
name|t1
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=other ex=b}"
operator|+
name|t1
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!key=again ex=a,b}"
operator|+
name|t1
argument_list|,
literal|"facet.field"
argument_list|,
name|t1
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=a}id:[1 TO 7]"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=b}id:[3 TO 9]"
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"{!ex=t1}SubjectTerms_mfacet"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!tag=t1}SubjectTerms_mfacet:(test 1)"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"10"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// test field that is valid in schema but missing in all shards
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|missingField
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// test field that is valid in schema and missing in some shards
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|oddField
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
name|i1
operator|+
literal|" desc"
argument_list|,
literal|"stats"
argument_list|,
literal|"true"
argument_list|,
literal|"stats.field"
argument_list|,
name|i1
argument_list|)
expr_stmt|;
try|try
block|{
comment|// test error produced for field that is invalid for schema
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|invalidField
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|TestCase
operator|.
name|fail
argument_list|(
literal|"SolrServerException expected for invalid field that is not in schema"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
comment|// index the same document to two servers and make sure things
comment|// don't blow up.
if|if
condition|(
name|clients
operator|.
name|size
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|index
argument_list|(
name|id
argument_list|,
literal|100
argument_list|,
name|i1
argument_list|,
literal|107
argument_list|,
name|t1
argument_list|,
literal|"oh no, a duplicate!"
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
name|clients
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|index_specific
argument_list|(
name|i
argument_list|,
name|id
argument_list|,
literal|100
argument_list|,
name|i1
argument_list|,
literal|107
argument_list|,
name|t1
argument_list|,
literal|"oh no, a duplicate!"
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|()
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"duplicate"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
name|t1
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"fox duplicate horses"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
name|t1
argument_list|)
expr_stmt|;
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// Thread.sleep(10000000000L);
block|}
block|}
end_class

end_unit

