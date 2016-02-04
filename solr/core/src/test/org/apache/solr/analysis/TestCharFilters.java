begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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

begin_comment
comment|/**  * Tests that charfilters are being applied properly  * (e.g. once and only once) with mockcharfilter.  */
end_comment

begin_class
DECL|class|TestCharFilters
specifier|public
class|class
name|TestCharFilters
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
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-charfilters.xml"
argument_list|)
expr_stmt|;
comment|// add some docs
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"content"
argument_list|,
literal|"aab"
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
literal|"content"
argument_list|,
literal|"aabaa"
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
literal|"content2"
argument_list|,
literal|"ab"
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
literal|"content2"
argument_list|,
literal|"aba"
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
comment|/**    * Test query analysis: at querytime MockCharFilter will    * double the 'a', so ab -&gt; aab, and aba -&gt; aabaa    *     * We run the test twice to make sure reuse is working    */
DECL|method|testQueryAnalysis
specifier|public
name|void
name|testQueryAnalysis
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Query analysis: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"content:ab"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Query analysis: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"content:aba"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=2]"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test index analysis: at indextime MockCharFilter will    * double the 'a', so ab -&gt; aab, and aba -&gt; aabaa    *     * We run the test twice to make sure reuse is working    */
DECL|method|testIndexAnalysis
specifier|public
name|void
name|testIndexAnalysis
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Index analysis: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"content2:aab"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"Index analysis: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"content2:aabaa"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=4]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

