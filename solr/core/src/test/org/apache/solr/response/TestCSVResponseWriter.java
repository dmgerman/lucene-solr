begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|DateUtil
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
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrReturnFields
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_class
DECL|class|TestCSVResponseWriter
specifier|public
class|class
name|TestCSVResponseWriter
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
comment|// schema12 doesn't support _version_
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"-1"
argument_list|,
literal|"foo_s"
argument_list|,
literal|"hi"
argument_list|,
literal|"foo_l"
argument_list|,
literal|"12345678987654321"
argument_list|,
literal|"foo_b"
argument_list|,
literal|"false"
argument_list|,
literal|"foo_f"
argument_list|,
literal|"1.414"
argument_list|,
literal|"foo_d"
argument_list|,
literal|"-1.0E300"
argument_list|,
literal|"foo_dt"
argument_list|,
literal|"2000-01-02T03:04:05Z"
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
literal|"v_ss"
argument_list|,
literal|"hi"
argument_list|,
literal|"v_ss"
argument_list|,
literal|"there"
argument_list|,
literal|"v2_ss"
argument_list|,
literal|"nice"
argument_list|,
literal|"v2_ss"
argument_list|,
literal|"output"
argument_list|,
literal|"shouldbeunstored"
argument_list|,
literal|"foo"
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
literal|"shouldbeunstored"
argument_list|,
literal|"foo"
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
literal|"amount_c"
argument_list|,
literal|"1.50,EUR"
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
literal|"store"
argument_list|,
literal|"12.434,-134.1"
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
DECL|method|testCSVOutput
specifier|public
name|void
name|testCSVOutput
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test our basic types,and that fields come back in the requested order
name|assertEquals
argument_list|(
literal|"id,foo_s,foo_i,foo_l,foo_b,foo_f,foo_d,foo_dt\n1,hi,-1,12345678987654321,false,1.414,-1.0E300,2000-01-02T03:04:05Z\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,foo_s,foo_i,foo_l,foo_b,foo_f,foo_d,foo_dt"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test retrieving score, csv.header
name|assertEquals
argument_list|(
literal|"1,0.0,hi\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1^0"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score,foo_s"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test multivalued
name|assertEquals
argument_list|(
literal|"2,\"hi,there\"\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test separator change
name|assertEquals
argument_list|(
literal|"2|\"hi|there\"\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.separator"
argument_list|,
literal|"|"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test mv separator change
name|assertEquals
argument_list|(
literal|"2,hi|there\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.mv.separator"
argument_list|,
literal|"|"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test mv separator change for a single field
name|assertEquals
argument_list|(
literal|"2,hi|there,nice:output\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.mv.separator"
argument_list|,
literal|"|"
argument_list|,
literal|"f.v2_ss.csv.separator"
argument_list|,
literal|":"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss,v2_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test csv field for polyfield (currency) SOLR-3959
name|assertEquals
argument_list|(
literal|"4,\"1.50\\,EUR\"\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:4"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,amount_c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test csv field for polyfield (latlon) SOLR-3959
name|assertEquals
argument_list|(
literal|"5,\"12.434\\,-134.1\"\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:5"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,store"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test retrieving fields from index
name|String
name|result
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
literal|"id,foo_s,foo_i,foo_l,foo_b,foo_f,foo_d,foo_dt,v_ss,v2_ss,score"
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|result
operator|.
name|indexOf
argument_list|(
name|field
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// test null values
name|assertEquals
argument_list|(
literal|"2,,hi|there\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.mv.separator"
argument_list|,
literal|"|"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,foo_s,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test alternate null value
name|assertEquals
argument_list|(
literal|"2,NULL,hi|there\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.mv.separator"
argument_list|,
literal|"|"
argument_list|,
literal|"csv.null"
argument_list|,
literal|"NULL"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,foo_s,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test alternate newline
name|assertEquals
argument_list|(
literal|"2,\"hi,there\"\r\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.newline"
argument_list|,
literal|"\r\n"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test alternate encapsulator
name|assertEquals
argument_list|(
literal|"2,'hi,there'\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.encapsulator"
argument_list|,
literal|"'"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test using escape instead of encapsulator
name|assertEquals
argument_list|(
literal|"2,hi\\,there\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:2"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"csv.escape"
argument_list|,
literal|"\\"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test multiple lines
name|assertEquals
argument_list|(
literal|"1,,hi\n2,\"hi,there\",\n"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:[1 TO 2]"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"id,v_ss,foo_s"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// test SOLR-2970 not returning non-stored fields by default. Compare sorted list
name|assertEquals
argument_list|(
name|sortHeader
argument_list|(
literal|"amount_c,store,v_ss,foo_b,v2_ss,foo_f,foo_i,foo_d,foo_s,foo_dt,id,foo_l\n"
argument_list|)
argument_list|,
name|sortHeader
argument_list|(
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:3"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// now test SolrDocumentList
name|SolrDocument
name|d
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
name|SolrDocument
name|d1
init|=
name|d
decl_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_i"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_s"
argument_list|,
literal|"hi"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_l"
argument_list|,
literal|"12345678987654321L"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_b"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_f"
argument_list|,
literal|1.414f
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_d"
argument_list|,
operator|-
literal|1.0E300
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"foo_dt"
argument_list|,
name|DateUtil
operator|.
name|parseDate
argument_list|(
literal|"2000-01-02T03:04:05Z"
argument_list|)
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"score"
argument_list|,
literal|"2.718"
argument_list|)
expr_stmt|;
name|d
operator|=
operator|new
name|SolrDocument
argument_list|()
expr_stmt|;
name|SolrDocument
name|d2
init|=
name|d
decl_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"v_ss"
argument_list|,
literal|"hi"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"v_ss"
argument_list|,
literal|"there"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"v2_ss"
argument_list|,
literal|"nice"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"v2_ss"
argument_list|,
literal|"output"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"score"
argument_list|,
literal|"89.83"
argument_list|)
expr_stmt|;
name|d
operator|.
name|addField
argument_list|(
literal|"shouldbeunstored"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|sdl
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|sdl
operator|.
name|add
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|sdl
operator|.
name|add
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|addResponse
argument_list|(
name|sdl
argument_list|)
expr_stmt|;
name|QueryResponseWriter
name|w
init|=
operator|new
name|CSVResponseWriter
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"id,foo_s"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|StringWriter
name|buf
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id,foo_s\n1,hi\n2,\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// try scores
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"id,score,foo_s"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id,score,foo_s\n1,2.718,hi\n2,89.83,\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// get field values from docs... should be ordered and not include score unless requested
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"*"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id,foo_i,foo_s,foo_l,foo_b,foo_f,foo_d,foo_dt,v_ss,v2_ss\n"
operator|+
literal|"1,-1,hi,12345678987654321L,false,1.414,-1.0E300,2000-01-02T03:04:05Z,,\n"
operator|+
literal|"2,,,,,,,,\"hi,there\",\"nice,output\"\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// get field values and scores - just check that the scores are there... we don't guarantee where
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"*,score"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"score"
argument_list|)
operator|>=
literal|0
operator|&&
name|s
operator|.
name|indexOf
argument_list|(
literal|"2.718"
argument_list|)
operator|>
literal|0
operator|&&
name|s
operator|.
name|indexOf
argument_list|(
literal|"89.83"
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// Test field globs
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"id,foo*"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id,foo_i,foo_s,foo_l,foo_b,foo_f,foo_d,foo_dt\n"
operator|+
literal|"1,-1,hi,12345678987654321L,false,1.414,-1.0E300,2000-01-02T03:04:05Z\n"
operator|+
literal|"2,,,,,,,\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"id,*_d*"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id,foo_d,foo_dt\n"
operator|+
literal|"1,-1.0E300,2000-01-02T03:04:05Z\n"
operator|+
literal|"2,,\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test function queries
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"sum(1,1),id,exists(foo_i),div(9,1),foo_f"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"sum(1,1)\",id,exists(foo_i),\"div(9,1)\",foo_f\n"
operator|+
literal|"\"\",1,,,1.414\n"
operator|+
literal|"\"\",2,,,\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test transformers
name|rsp
operator|.
name|setReturnFields
argument_list|(
operator|new
name|SolrReturnFields
argument_list|(
literal|"mydocid:[docid],[explain]"
argument_list|,
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|buf
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mydocid,[explain]\n"
operator|+
literal|"\"\",\n"
operator|+
literal|"\"\",\n"
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPseudoFields
specifier|public
name|void
name|testPseudoFields
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Use Pseudo Field
name|assertEquals
argument_list|(
literal|"1,hi"
argument_list|,
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"false"
argument_list|,
literal|"fl"
argument_list|,
literal|"XXX:id,foo_s"
argument_list|)
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|txt
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"XXX:id,YYY:[docid],FOO:foo_s"
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|lines
init|=
name|txt
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|lines
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XXX,YYY,FOO"
argument_list|,
name|lines
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1,0,hi"
argument_list|,
name|lines
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|//assertions specific to multiple pseudofields functions like abs, div, exists, etc.. (SOLR-5423)
name|String
name|funcText
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"XXX:id,YYY:exists(foo_i),exists(shouldbeunstored)"
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|funcLines
init|=
name|funcText
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|funcLines
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XXX,YYY,exists(shouldbeunstored)"
argument_list|,
name|funcLines
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1,true,false"
argument_list|,
name|funcLines
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3,false,true"
argument_list|,
name|funcLines
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
comment|//assertions specific to single function without alias (SOLR-5423)
name|String
name|singleFuncText
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*"
argument_list|,
literal|"wt"
argument_list|,
literal|"csv"
argument_list|,
literal|"csv.header"
argument_list|,
literal|"true"
argument_list|,
literal|"fl"
argument_list|,
literal|"exists(shouldbeunstored),XXX:id"
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|singleFuncLines
init|=
name|singleFuncText
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|singleFuncLines
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"exists(shouldbeunstored),XXX"
argument_list|,
name|singleFuncLines
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false,1"
argument_list|,
name|singleFuncLines
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true,3"
argument_list|,
name|singleFuncLines
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
comment|/*    * Utility method to sort a comma separated list of strings, for easier comparison regardless of platform    */
DECL|method|sortHeader
specifier|private
name|String
name|sortHeader
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|String
index|[]
name|output
init|=
name|input
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|output
argument_list|)
expr_stmt|;
return|return
name|Arrays
operator|.
name|toString
argument_list|(
name|output
argument_list|)
return|;
block|}
block|}
end_class

end_unit

