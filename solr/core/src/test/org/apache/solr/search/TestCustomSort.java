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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Test SortField.CUSTOM sorts  */
end_comment

begin_class
DECL|class|TestCustomSort
specifier|public
class|class
name|TestCustomSort
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
literal|"schema-custom-field.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSortableBinary
specifier|public
name|void
name|testSortableBinary
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x12
block|,
literal|0x62
block|,
literal|0x15
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  2
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x25
block|,
literal|0x21
block|,
literal|0x16
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  5
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x35
block|,
literal|0x32
block|,
literal|0x58
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  8
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x25
block|,
literal|0x21
block|,
literal|0x15
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  4
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x35
block|,
literal|0x35
block|,
literal|0x10
block|,
literal|0x00
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  9
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"text"
argument_list|,
literal|"c"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x1a
block|,
literal|0x2b
block|,
literal|0x3c
block|,
literal|0x00
block|,
literal|0x00
block|,
literal|0x03
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  3
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"text"
argument_list|,
literal|"c"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x00
block|,
literal|0x3c
block|,
literal|0x73
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  1
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"text"
argument_list|,
literal|"c"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x59
block|,
literal|0x2d
block|,
literal|0x4d
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 11
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"text"
argument_list|,
literal|"a"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x39
block|,
literal|0x79
block|,
literal|0x7a
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 10
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"text"
argument_list|,
literal|"b"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x31
block|,
literal|0x39
block|,
literal|0x7c
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  6
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xaf
block|,
operator|(
name|byte
operator|)
literal|0x9c
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 13
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|0x34
block|,
operator|(
name|byte
operator|)
literal|0xdd
block|,
literal|0x4d
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//  7
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"text"
argument_list|,
literal|"d"
argument_list|,
literal|"payload"
argument_list|,
name|ByteBuffer
operator|.
name|wrap
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x80
block|,
literal|0x11
block|,
literal|0x33
block|}
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// 12
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='13']"
comment|//<result name="response" numFound="13" start="0">
argument_list|,
literal|"//result/doc[int='7'  and position()=1]"
comment|//<doc><int name="id">7</int></doc>   00 3c 73
argument_list|,
literal|"//result/doc[int='1'  and position()=2]"
comment|//<doc><int name="id">1</int></doc>   12 62 15
argument_list|,
literal|"//result/doc[int='6'  and position()=3]"
comment|//<doc><int name="id">6</int></doc>   1a 2b 3c 00 00 03
argument_list|,
literal|"//result/doc[int='4'  and position()=4]"
comment|//<doc><int name="id">4</int></doc>   25 21 15
argument_list|,
literal|"//result/doc[int='2'  and position()=5]"
comment|//<doc><int name="id">2</int></doc>   25 21 16
argument_list|,
literal|"//result/doc[int='10' and position()=6]"
comment|//<doc><int name="id">10</int></doc>  31 39 7c
argument_list|,
literal|"//result/doc[int='12' and position()=7]"
comment|//<doc><int name="id">12</int></doc>  34 dd 4d
argument_list|,
literal|"//result/doc[int='3'  and position()=8]"
comment|//<doc><int name="id">3</int></doc>   35 32 58
argument_list|,
literal|"//result/doc[int='5'  and position()=9]"
comment|//<doc><int name="id">5</int></doc>   35 35 10 00
argument_list|,
literal|"//result/doc[int='9'  and position()=10]"
comment|//<doc><int name="id">9</int></doc>   39 79 7a
argument_list|,
literal|"//result/doc[int='8'  and position()=11]"
comment|//<doc><int name="id">8</int></doc>   59 2d 4d
argument_list|,
literal|"//result/doc[int='13' and position()=12]"
comment|//<doc><int name="id">13</int></doc>  80 11 33
argument_list|,
literal|"//result/doc[int='11' and position()=13]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">11</int></doc>  ff af 9c
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='13']"
comment|//<result name="response" numFound="13" start="0">
argument_list|,
literal|"//result/doc[int='11' and position()=1]"
comment|//<doc><int name="id">11</int></doc>  ff af 9c
argument_list|,
literal|"//result/doc[int='13' and position()=2]"
comment|//<doc><int name="id">13</int></doc>  80 11 33
argument_list|,
literal|"//result/doc[int='8'  and position()=3]"
comment|//<doc><int name="id">8</int></doc>   59 2d 4d
argument_list|,
literal|"//result/doc[int='9'  and position()=4]"
comment|//<doc><int name="id">9</int></doc>   39 79 7a
argument_list|,
literal|"//result/doc[int='5'  and position()=5]"
comment|//<doc><int name="id">5</int></doc>   35 35 10 00
argument_list|,
literal|"//result/doc[int='3'  and position()=6]"
comment|//<doc><int name="id">3</int></doc>   35 32 58
argument_list|,
literal|"//result/doc[int='12' and position()=7]"
comment|//<doc><int name="id">12</int></doc>  34 dd 4d
argument_list|,
literal|"//result/doc[int='10' and position()=8]"
comment|//<doc><int name="id">10</int></doc>  31 39 7c
argument_list|,
literal|"//result/doc[int='2'  and position()=9]"
comment|//<doc><int name="id">2</int></doc>   25 21 16
argument_list|,
literal|"//result/doc[int='4'  and position()=10]"
comment|//<doc><int name="id">4</int></doc>   25 21 15
argument_list|,
literal|"//result/doc[int='6'  and position()=11]"
comment|//<doc><int name="id">6</int></doc>   1a 2b 3c 00 00 03
argument_list|,
literal|"//result/doc[int='1'  and position()=12]"
comment|//<doc><int name="id">1</int></doc>   12 62 15
argument_list|,
literal|"//result/doc[int='7'  and position()=13]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">7</int></doc>   00 3c 73
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:a"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='4']"
comment|//<result name="response" numFound="4" start="0">
argument_list|,
literal|"//result/doc[int='1'  and position()=1]"
comment|//<doc><int name="id">1</int></doc>   12 62 15
argument_list|,
literal|"//result/doc[int='3'  and position()=2]"
comment|//<doc><int name="id">3</int></doc>   35 32 58
argument_list|,
literal|"//result/doc[int='5'  and position()=3]"
comment|//<doc><int name="id">5</int></doc>   35 35 10 00
argument_list|,
literal|"//result/doc[int='9'  and position()=4]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">9</int></doc>   39 79 7a
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:a"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='4']"
comment|//<result name="response" numFound="4" start="0">
argument_list|,
literal|"//result/doc[int='9'  and position()=1]"
comment|//<doc><int name="id">9</int></doc>   39 79 7a
argument_list|,
literal|"//result/doc[int='5'  and position()=2]"
comment|//<doc><int name="id">5</int></doc>   35 35 10 00
argument_list|,
literal|"//result/doc[int='3'  and position()=3]"
comment|//<doc><int name="id">3</int></doc>   35 32 58
argument_list|,
literal|"//result/doc[int='1'  and position()=4]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">1</int></doc>   12 62 15
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:b"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|//<result name="response" numFound="3" start="0">
argument_list|,
literal|"//result/doc[int='4'  and position()=1]"
comment|//<doc><int name="id">4</int></doc>   25 21 15
argument_list|,
literal|"//result/doc[int='2'  and position()=2]"
comment|//<doc><int name="id">2</int></doc>   25 21 16
argument_list|,
literal|"//result/doc[int='10' and position()=3]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">10</int></doc>  31 39 7c
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:b"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|//<result name="response" numFound="3" start="0">
argument_list|,
literal|"//result/doc[int='10' and position()=1]"
comment|//<doc><int name="id">10</int></doc>  31 39 7c
argument_list|,
literal|"//result/doc[int='2'  and position()=2]"
comment|//<doc><int name="id">2</int></doc>   25 21 16
argument_list|,
literal|"//result/doc[int='4'  and position()=3]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">4</int></doc>   25 21 15
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:c"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|//<result name="response" numFound="3" start="0">
argument_list|,
literal|"//result/doc[int='7'  and position()=1]"
comment|//<doc><int name="id">7</int></doc>    00 3c 73
argument_list|,
literal|"//result/doc[int='6'  and position()=2]"
comment|//<doc><int name="id">6</int></doc>    1a 2b 3c 00 00 03
argument_list|,
literal|"//result/doc[int='8'  and position()=3]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">8</int></doc>    59 2d 4d
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:c"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|//<result name="response" numFound="3" start="0">
argument_list|,
literal|"//result/doc[int='8'  and position()=1]"
comment|//<doc><int name="id">8</int></doc>    59 2d 4d
argument_list|,
literal|"//result/doc[int='6'  and position()=2]"
comment|//<doc><int name="id">6</int></doc>    1a 2b 3c 00 00 03
argument_list|,
literal|"//result/doc[int='7'  and position()=3]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">7</int></doc>    00 3c 73
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:d"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|//<result name="response" numFound="3" start="0">
argument_list|,
literal|"//result/doc[int='12' and position()=1]"
comment|//<doc><int name="id">12</int></doc>   34 dd 4d
argument_list|,
literal|"//result/doc[int='13' and position()=2]"
comment|//<doc><int name="id">13</int></doc>   80 11 33
argument_list|,
literal|"//result/doc[int='11' and position()=3]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">11</int></doc>   ff af 9c
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"text:d"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"sort"
argument_list|,
literal|"payload desc"
argument_list|,
literal|"rows"
argument_list|,
literal|"20"
argument_list|)
argument_list|,
literal|"//result[@numFound='3']"
comment|//<result name="response" numFound="3" start="0">
argument_list|,
literal|"//result/doc[int='11' and position()=1]"
comment|//<doc><int name="id">11</int></doc>   ff af 9c
argument_list|,
literal|"//result/doc[int='13' and position()=2]"
comment|//<doc><int name="id">13</int></doc>   80 11 33
argument_list|,
literal|"//result/doc[int='12' and position()=3]"
argument_list|)
expr_stmt|;
comment|//<doc><int name="id">12</int></doc>   34 dd 4d
block|}
block|}
end_class

end_unit

