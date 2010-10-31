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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|FieldCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
DECL|class|TestGroupingSearch
specifier|public
class|class
name|TestGroupingSearch
extends|extends
name|SolrTestCaseJ4
block|{
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
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|cleanIndex
specifier|public
name|void
name|cleanIndex
parameter_list|()
block|{
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
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
DECL|method|testGroupingGroupSortingScore_basic
specifier|public
name|void
name|testGroupingGroupSortingScore_basic
parameter_list|()
block|{
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"title"
argument_list|,
literal|"a book title"
argument_list|,
literal|"group_si"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"title"
argument_list|,
literal|"the title"
argument_list|,
literal|"group_si"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"title"
argument_list|,
literal|"a book title"
argument_list|,
literal|"group_si"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"title"
argument_list|,
literal|"title"
argument_list|,
literal|"group_si"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"name"
argument_list|,
literal|"author3"
argument_list|,
literal|"title"
argument_list|,
literal|"the title of a title"
argument_list|,
literal|"group_si"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"title:title"
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|"//lst[@name='grouped']/lst[@name='name']"
argument_list|,
literal|"*[count(//arr[@name='groups']/lst) = 3]"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/str[@name='groupValue'][.='author2']"
comment|//        ,"//arr[@name='groups']/lst[1]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result/doc/*[@name='id'][.='4']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/str[@name='groupValue'][.='author1']"
comment|//       ,"//arr[@name='groups']/lst[2]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result/doc/*[@name='id'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[3]/str[@name='groupValue'][.='author3']"
comment|//        ,"//arr[@name='groups']/lst[3]/int[@name='matches'][.='1']"
argument_list|,
literal|"//arr[@name='groups']/lst[3]/result[@numFound='1']"
argument_list|,
literal|"//arr[@name='groups']/lst[3]/result/doc/*[@name='id'][.='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"title:title"
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
literal|"group_si"
argument_list|)
argument_list|,
literal|"//lst[@name='grouped']/lst[@name='group_si']"
argument_list|,
literal|"*[count(//arr[@name='groups']/lst) = 2]"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/int[@name='groupValue'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result/doc/*[@name='id'][.='4']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/int[@name='groupValue'][.='1']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result[@numFound='3']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result/doc/*[@name='id'][.='5']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGroupingGroupSortingScore_basicWithGroupSortEqualToSort
specifier|public
name|void
name|testGroupingGroupSortingScore_basicWithGroupSortEqualToSort
parameter_list|()
block|{
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"title"
argument_list|,
literal|"a book title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"title"
argument_list|,
literal|"the title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"title"
argument_list|,
literal|"a book title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"title"
argument_list|,
literal|"title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"name"
argument_list|,
literal|"author3"
argument_list|,
literal|"title"
argument_list|,
literal|"the title of a title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"title:title"
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
literal|"name"
argument_list|,
literal|"sort"
argument_list|,
literal|"score desc"
argument_list|,
literal|"group.sort"
argument_list|,
literal|"score desc"
argument_list|)
argument_list|,
literal|"//arr[@name='groups']/lst[1]/str[@name='groupValue'][.='author2']"
comment|//        ,"//arr[@name='groups']/lst[1]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result/doc/*[@name='id'][.='4']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/str[@name='groupValue'][.='author1']"
comment|//        ,"//arr[@name='groups']/lst[2]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result/doc/*[@name='id'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[3]/str[@name='groupValue'][.='author3']"
comment|//        ,"//arr[@name='groups']/lst[3]/int[@name='matches'][.='1']"
argument_list|,
literal|"//arr[@name='groups']/lst[3]/result[@numFound='1']"
argument_list|,
literal|"//arr[@name='groups']/lst[3]/result/doc/*[@name='id'][.='5']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGroupingGroupSortingName
specifier|public
name|void
name|testGroupingGroupSortingName
parameter_list|()
block|{
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"title"
argument_list|,
literal|"a book title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"title"
argument_list|,
literal|"the title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"title"
argument_list|,
literal|"book title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"title"
argument_list|,
literal|"the title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"title:title"
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
literal|"name"
argument_list|,
literal|"group.sort"
argument_list|,
literal|"title asc"
argument_list|)
argument_list|,
literal|"*[count(//arr[@name='groups']/lst) = 2]"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/str[@name='groupValue'][.='author2']"
comment|//       ,"//arr[@name='groups']/lst[1]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result/doc/*[@name='id'][.='3']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/str[@name='groupValue'][.='author1']"
comment|//        ,"//arr[@name='groups']/lst[2]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result/doc/*[@name='id'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGroupingGroupSortingWeight
specifier|public
name|void
name|testGroupingGroupSortingWeight
parameter_list|()
block|{
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"weight"
argument_list|,
literal|"12.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"name"
argument_list|,
literal|"author1"
argument_list|,
literal|"weight"
argument_list|,
literal|"2.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"weight"
argument_list|,
literal|"0.1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"name"
argument_list|,
literal|"author2"
argument_list|,
literal|"weight"
argument_list|,
literal|"0.11"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
literal|"name"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"group.sort"
argument_list|,
literal|"weight desc"
argument_list|)
argument_list|,
literal|"*[count(//arr[@name='groups']/lst) = 2]"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/str[@name='groupValue'][.='author1']"
comment|//        ,"//arr[@name='groups']/lst[1]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[1]/result/doc/*[@name='id'][.='1']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/str[@name='groupValue'][.='author2']"
comment|//        ,"//arr[@name='groups']/lst[2]/int[@name='matches'][.='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result[@numFound='2']"
argument_list|,
literal|"//arr[@name='groups']/lst[2]/result/doc/*[@name='id'][.='4']"
argument_list|)
expr_stmt|;
block|}
DECL|field|f
specifier|static
name|String
name|f
init|=
literal|"foo_i"
decl_stmt|;
DECL|field|f2
specifier|static
name|String
name|f2
init|=
literal|"foo2_i"
decl_stmt|;
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
name|f
argument_list|,
literal|"5"
argument_list|,
name|f2
argument_list|,
literal|"4"
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
name|f
argument_list|,
literal|"4"
argument_list|,
name|f2
argument_list|,
literal|"2"
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
name|f
argument_list|,
literal|"3"
argument_list|,
name|f2
argument_list|,
literal|"7"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
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
name|f
argument_list|,
literal|"2"
argument_list|,
name|f2
argument_list|,
literal|"6"
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
name|f
argument_list|,
literal|"1"
argument_list|,
name|f2
argument_list|,
literal|"2"
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
name|f
argument_list|,
literal|"3"
argument_list|,
name|f2
argument_list|,
literal|"2"
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
name|f
argument_list|,
literal|"2"
argument_list|,
name|f2
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
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
name|f
argument_list|,
literal|"1"
argument_list|,
name|f2
argument_list|,
literal|"10"
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
name|f
argument_list|,
literal|"2"
argument_list|,
name|f2
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
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
name|f
argument_list|,
literal|"1"
argument_list|,
name|f2
argument_list|,
literal|"3"
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
DECL|method|testGroupAPI
specifier|public
name|void
name|testGroupAPI
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|()
expr_stmt|;
name|String
name|filt
init|=
name|f
operator|+
literal|":[* TO *]"
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|)
argument_list|,
literal|"/response/lst[@name='grouped']/lst[@name='"
operator|+
name|f
operator|+
literal|"']/arr[@name='groups']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
comment|// exact match
argument_list|,
literal|"/responseHeader=={'_SKIP_':'QTime', 'status':0}"
comment|// partial match by skipping some elements
argument_list|,
literal|"/responseHeader=={'_MATCH_':'status', 'status':0}"
comment|// partial match by only including some elements
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':[\n"
operator|+
literal|"{'groupValue':1,'doclist':{'numFound':3,'start':0,'docs':[{'id':'8'}]}},"
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':0,'docs':[{'id':'3'}]}},"
operator|+
literal|"{'groupValue':2,'doclist':{'numFound':3,'start':0,'docs':[{'id':'4'}]}},"
operator|+
literal|"{'groupValue':5,'doclist':{'numFound':1,'start':0,'docs':[{'id':'1'}]}},"
operator|+
literal|"{'groupValue':4,'doclist':{'numFound':1,'start':0,'docs':[{'id':'2'}]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test limiting the number of groups returned
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"{'groupValue':1,'doclist':{'numFound':3,'start':0,'docs':[{'id':'8'}]}},"
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':0,'docs':[{'id':'3'}]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test offset into group list
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"start"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':0,'docs':[{'id':'3'}]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test big offset into group list
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"start"
argument_list|,
literal|"100"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test increasing the docs per group returned
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"{'groupValue':1,'doclist':{'numFound':3,'start':0,'docs':[{'id':'8'},{'id':'10'},{'id':'5'}]}},"
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':0,'docs':[{'id':'3'},{'id':'6'}]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test offset into each group
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"3"
argument_list|,
literal|"group.offset"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"{'groupValue':1,'doclist':{'numFound':3,'start':1,'docs':[{'id':'10'},{'id':'5'}]}},"
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':1,'docs':[{'id':'6'}]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test big offset into each group
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"3"
argument_list|,
literal|"group.offset"
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|f
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"{'groupValue':1,'doclist':{'numFound':3,'start':10,'docs':[]}},"
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':10,'docs':[]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test adding in scores
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"2"
argument_list|,
literal|"indent"
argument_list|,
literal|"off"
argument_list|)
argument_list|,
literal|"/grouped/"
operator|+
name|f
operator|+
literal|"/groups=="
operator|+
literal|"["
operator|+
literal|"{'groupValue':1,'doclist':{'numFound':3,'start':0,'maxScore':10.0,'docs':[{'id':'8','score':10.0},{'id':'10','score':3.0}]}},"
operator|+
literal|"{'groupValue':3,'doclist':{'numFound':2,'start':0,'maxScore':7.0,'docs':[{'id':'3','score':7.0},{'id':'6','score':2.0}]}}"
operator|+
literal|"]"
argument_list|)
expr_stmt|;
comment|// test function (functions are currently all float - this may change)
name|String
name|func
init|=
literal|"add("
operator|+
name|f
operator|+
literal|","
operator|+
name|f
operator|+
literal|")"
decl_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.func"
argument_list|,
name|func
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"rows"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"/grouped=={'"
operator|+
name|func
operator|+
literal|"':{'matches':10,'groups':["
operator|+
literal|"{'groupValue':2.0,'doclist':{'numFound':3,'start':0,'docs':[{'id':'8'}]}},"
operator|+
literal|"{'groupValue':6.0,'doclist':{'numFound':2,'start':0,'docs':[{'id':'3'}]}}"
operator|+
literal|"]}}"
argument_list|)
expr_stmt|;
comment|// test that faceting works with grouping
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|f
argument_list|)
argument_list|,
literal|"/grouped/"
operator|+
name|f
operator|+
literal|"/matches==10"
argument_list|,
literal|"/facet_counts/facet_fields/"
operator|+
name|f
operator|+
literal|"==['1',3, '2',3, '3',2, '4',1, '5',1]"
argument_list|)
expr_stmt|;
name|purgeFieldCache
argument_list|(
name|FieldCache
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
comment|// avoid FC insanity
comment|// test that grouping works with highlighting
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"hl"
argument_list|,
literal|"true"
argument_list|,
literal|"hl.fl"
argument_list|,
name|f
argument_list|)
argument_list|,
literal|"/grouped/"
operator|+
name|f
operator|+
literal|"/matches==10"
argument_list|,
literal|"/highlighting=={'_ORDERED_':'', '8':{},'3':{},'4':{},'1':{},'2':{}}"
argument_list|)
expr_stmt|;
comment|// test that grouping works with debugging
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/grouped/"
operator|+
name|f
operator|+
literal|"/matches==10"
argument_list|,
literal|"/debug/explain/8=="
argument_list|,
literal|"/debug/explain/2=="
argument_list|)
expr_stmt|;
comment|///////////////////////// group.query
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.query"
argument_list|,
literal|"id:[2 TO 5]"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"3"
argument_list|)
argument_list|,
literal|"/grouped=={'id:[2 TO 5]':{'matches':10,"
operator|+
literal|"'doclist':{'numFound':4,'start':0,'docs':[{'id':'3'},{'id':'4'},{'id':'2'}]}}}"
argument_list|)
expr_stmt|;
comment|// group.query and offset
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.query"
argument_list|,
literal|"id:[2 TO 5]"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"3"
argument_list|,
literal|"group.offset"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"/grouped=={'id:[2 TO 5]':{'matches':10,"
operator|+
literal|"'doclist':{'numFound':4,'start':2,'docs':[{'id':'2'},{'id':'5'}]}}}"
argument_list|)
expr_stmt|;
comment|// group.query and big offset
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.query"
argument_list|,
literal|"id:[2 TO 5]"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"3"
argument_list|,
literal|"group.offset"
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"/grouped=={'id:[2 TO 5]':{'matches':10,"
operator|+
literal|"'doclist':{'numFound':4,'start':10,'docs':[]}}}"
argument_list|)
expr_stmt|;
comment|// multiple at once
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
name|filt
argument_list|,
literal|"q"
argument_list|,
literal|"{!func}"
operator|+
name|f2
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.query"
argument_list|,
literal|"id:[2 TO 5]"
argument_list|,
literal|"group.query"
argument_list|,
literal|"id:[5 TO 5]"
argument_list|,
literal|"group.field"
argument_list|,
name|f
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"group.limit"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"/grouped/id:[2 TO 5]=={'matches':10,'doclist':{'numFound':4,'start':0,'docs':[{'id':'3'},{'id':'4'}]}}"
argument_list|,
literal|"/grouped/id:[5 TO 5]=={'matches':10,'doclist':{'numFound':1,'start':0,'docs':[{'id':'5'}]}}"
argument_list|,
literal|"/grouped/"
operator|+
name|f
operator|+
literal|"=={'matches':10,'groups':[{'groupValue':1,'doclist':{'numFound':3,'start':0,'docs':[{'id':'8'},{'id':'10'}]}}]}"
argument_list|)
expr_stmt|;
block|}
empty_stmt|;
block|}
end_class

end_unit

