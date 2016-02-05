begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|SolrTestCaseJ4
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
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|SpatialFilterTest
specifier|public
class|class
name|SpatialFilterTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|setupDocs
specifier|private
name|void
name|setupDocs
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldName
argument_list|,
literal|"32.7693246, -79.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|fieldName
argument_list|,
literal|"33.7693246, -80.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|fieldName
argument_list|,
literal|"-32.7693246, 50.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|fieldName
argument_list|,
literal|"-50.7693246, 60.9289094"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|fieldName
argument_list|,
literal|"0,0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|fieldName
argument_list|,
literal|"0.1,0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
name|fieldName
argument_list|,
literal|"-0.1,-0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
name|fieldName
argument_list|,
literal|"0,179.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
name|fieldName
argument_list|,
literal|"0,-179.9"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
name|fieldName
argument_list|,
literal|"89.9,50"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
name|fieldName
argument_list|,
literal|"89.9,-130"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
name|fieldName
argument_list|,
literal|"-89.9,50"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
name|fieldName
argument_list|,
literal|"-89.9,-130"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPoints
specifier|public
name|void
name|testPoints
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"home"
decl_stmt|;
name|setupDocs
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
comment|//Try some edge cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"1,1"
argument_list|,
literal|100
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,179.8"
argument_list|,
literal|200
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|8
argument_list|,
literal|10
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|9
argument_list|)
expr_stmt|;
comment|//try some normal cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"33.0,-80.0"
argument_list|,
literal|300
argument_list|,
literal|12
argument_list|)
expr_stmt|;
comment|//large distance
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"33.0,-80.0"
argument_list|,
literal|5000
argument_list|,
literal|13
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeoHash
specifier|public
name|void
name|testGeoHash
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"home_gh"
decl_stmt|;
name|setupDocs
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
comment|//try some normal cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"33.0,-80.0"
argument_list|,
literal|300
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//large distance
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"33.0,-80.0"
argument_list|,
literal|5000
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//Try some edge cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,179.8"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"1,1"
argument_list|,
literal|180
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"-89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//this goes over the south pole
block|}
annotation|@
name|Test
DECL|method|testLatLonType
specifier|public
name|void
name|testLatLonType
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"home_ll"
decl_stmt|;
name|setupDocs
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
comment|//Try some edge cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"1,1"
argument_list|,
literal|175
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,179.8"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|8
argument_list|,
literal|9
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|10
argument_list|,
literal|11
argument_list|)
expr_stmt|;
comment|//this goes over the north pole
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"-89.8, 50"
argument_list|,
literal|200
argument_list|,
literal|2
argument_list|,
literal|12
argument_list|,
literal|13
argument_list|)
expr_stmt|;
comment|//this goes over the south pole
comment|//try some normal cases
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"33.0,-80.0"
argument_list|,
literal|300
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|//large distance
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"1,1"
argument_list|,
literal|5000
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|,
literal|7
argument_list|)
expr_stmt|;
comment|//Because we are generating a box based on the west/east longitudes and the south/north latitudes, which then
comment|//translates to a range query, which is slightly more inclusive.  Thus, even though 0.0 is 15.725 kms away,
comment|//it will be included, b/c of the box calculation.
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|"0.1,0.1"
argument_list|,
literal|15
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|//try some more
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"14"
argument_list|,
name|fieldName
argument_list|,
literal|"0,5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"15"
argument_list|,
name|fieldName
argument_list|,
literal|"0,15"
argument_list|)
argument_list|)
expr_stmt|;
comment|//3000KM from 0,0, see http://www.movable-type.co.uk/scripts/latlong.html
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"16"
argument_list|,
name|fieldName
argument_list|,
literal|"18.71111,19.79750"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"17"
argument_list|,
name|fieldName
argument_list|,
literal|"44.043900,-95.436643"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|2000
argument_list|,
literal|2
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|"0,0"
argument_list|,
literal|3000
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|3001
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"0,0"
argument_list|,
literal|3000.1
argument_list|,
literal|3
argument_list|,
literal|14
argument_list|,
literal|15
argument_list|,
literal|16
argument_list|)
expr_stmt|;
comment|//really fine grained distance and reflects some of the vagaries of how we are calculating the box
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|"43.517030,-96.789603"
argument_list|,
literal|109
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// falls outside of the real distance, but inside the bounding box
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
literal|"43.517030,-96.789603"
argument_list|,
literal|110
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|false
argument_list|,
literal|"43.517030,-96.789603"
argument_list|,
literal|110
argument_list|,
literal|1
argument_list|,
literal|17
argument_list|)
expr_stmt|;
comment|// Tests SOLR-2829
name|String
name|fieldNameHome
init|=
literal|"home_ll"
decl_stmt|;
name|String
name|fieldNameWork
init|=
literal|"work_ll"
decl_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldNameHome
argument_list|,
literal|"52.67,7.30"
argument_list|,
name|fieldNameWork
argument_list|,
literal|"48.60,11.61"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldNameHome
argument_list|,
literal|"52.67,7.30"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldNameWork
argument_list|,
literal|"48.60,11.61"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldNameWork
argument_list|,
literal|"52.67,7.30"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkHits
argument_list|(
name|fieldNameHome
argument_list|,
literal|"48.60,11.61"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|pt
parameter_list|,
name|double
name|distance
parameter_list|,
name|int
name|count
parameter_list|,
name|int
modifier|...
name|docIds
parameter_list|)
block|{
name|checkHits
argument_list|(
name|fieldName
argument_list|,
literal|true
argument_list|,
name|pt
argument_list|,
name|distance
argument_list|,
name|count
argument_list|,
name|docIds
argument_list|)
expr_stmt|;
block|}
DECL|method|checkHits
specifier|private
name|void
name|checkHits
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|boolean
name|exact
parameter_list|,
name|String
name|pt
parameter_list|,
name|double
name|distance
parameter_list|,
name|int
name|count
parameter_list|,
name|int
modifier|...
name|docIds
parameter_list|)
block|{
name|String
index|[]
name|tests
init|=
operator|new
name|String
index|[
name|docIds
operator|!=
literal|null
operator|&&
name|docIds
operator|.
name|length
operator|>
literal|0
condition|?
name|docIds
operator|.
name|length
operator|+
literal|1
else|:
literal|1
index|]
decl_stmt|;
name|tests
index|[
literal|0
index|]
operator|=
literal|"*[count(//doc)="
operator|+
name|count
operator|+
literal|"]"
expr_stmt|;
if|if
condition|(
name|docIds
operator|!=
literal|null
operator|&&
name|docIds
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|docId
range|:
name|docIds
control|)
block|{
name|tests
index|[
name|i
operator|++
index|]
operator|=
literal|"//result/doc/int[@name='id'][.='"
operator|+
name|docId
operator|+
literal|"']"
expr_stmt|;
block|}
block|}
name|String
name|method
init|=
name|exact
condition|?
literal|"geofilt"
else|:
literal|"bbox"
decl_stmt|;
name|int
name|postFilterCount
init|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
decl_stmt|;
comment|// throw in a random into the main query to prevent most cache hits
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:* OR foo_i:"
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!"
operator|+
name|method
operator|+
literal|" sfield="
operator|+
name|fieldName
operator|+
literal|"}"
argument_list|,
literal|"pt"
argument_list|,
name|pt
argument_list|,
literal|"d"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|distance
argument_list|)
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|postFilterCount
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
argument_list|)
expr_stmt|;
comment|// post filtering shouldn't be used
comment|// try uncached
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:* OR foo_i:"
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!"
operator|+
name|method
operator|+
literal|" sfield="
operator|+
name|fieldName
operator|+
literal|" cache=false"
operator|+
literal|"}"
argument_list|,
literal|"pt"
argument_list|,
name|pt
argument_list|,
literal|"d"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|distance
argument_list|)
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|postFilterCount
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
argument_list|)
expr_stmt|;
comment|// post filtering shouldn't be used
comment|// try post filtered for fields that support it
if|if
condition|(
name|fieldName
operator|.
name|endsWith
argument_list|(
literal|"ll"
argument_list|)
condition|)
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:* OR foo_i:"
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|100
argument_list|,
literal|"rows"
argument_list|,
literal|"1000"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!"
operator|+
name|method
operator|+
literal|" sfield="
operator|+
name|fieldName
operator|+
literal|" cache=false cost=150"
operator|+
literal|"}"
argument_list|,
literal|"pt"
argument_list|,
name|pt
argument_list|,
literal|"d"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|distance
argument_list|)
argument_list|)
argument_list|,
name|tests
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|postFilterCount
operator|+
literal|1
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
argument_list|)
expr_stmt|;
comment|// post filtering *should* have been used
block|}
block|}
block|}
end_class

begin_comment
comment|/*public void testSpatialQParser() throws Exception {     ModifiableSolrParams local = new ModifiableSolrParams();     local.add(CommonParams.FL, "home");     ModifiableSolrParams params = new ModifiableSolrParams();     params.add(SpatialParams.POINT, "5.0,5.0");     params.add(SpatialParams.DISTANCE, "3");     SolrQueryRequest req = new LocalSolrQueryRequest(h.getCore(), "", "", 0, 10, new HashMap());     SpatialFilterQParserPlugin parserPlugin;     Query query;      parserPlugin = new SpatialFilterQParserPlugin();     QParser parser = parserPlugin.createParser("'foo'", local, params, req);     query = parser.parse();     assertNotNull("Query is null", query);     assertTrue("query is not an instanceof "             + BooleanQuery.class,             query instanceof BooleanQuery);     local = new ModifiableSolrParams();     local.add(CommonParams.FL, "x");     params = new ModifiableSolrParams();     params.add(SpatialParams.POINT, "5.0");     params.add(SpatialParams.DISTANCE, "3");     req = new LocalSolrQueryRequest(h.getCore(), "", "", 0, 10, new HashMap());     parser = parserPlugin.createParser("'foo'", local, params, req);     query = parser.parse();     assertNotNull("Query is null", query);     assertTrue(query.getClass() + " is not an instanceof "             + LegacyNumericRangeQuery.class,             query instanceof LegacyNumericRangeQuery);     req.close();   }*/
end_comment

end_unit

