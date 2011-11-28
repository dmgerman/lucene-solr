begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Tests {@link ICUCollationKeyFilterFactory} with RangeQueries  */
end_comment

begin_class
DECL|class|TestICUCollationKeyRangeQueries
specifier|public
class|class
name|TestICUCollationKeyRangeQueries
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
literal|"solrconfig-icucollate.xml"
argument_list|,
literal|"schema-icucollatefilter.xml"
argument_list|,
literal|"analysis-extras/solr"
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
literal|"text"
argument_list|,
literal|"\u0633\u0627\u0628"
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
literal|"text"
argument_list|,
literal|"I WÄ°LL USE TURKÄ°SH CASING"
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
literal|"text"
argument_list|,
literal|"Ä± will use turkish casÄ±ng"
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
literal|"text"
argument_list|,
literal|"TÃ¶ne"
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
literal|"text"
argument_list|,
literal|"I W\u0049\u0307LL USE TURKÄ°SH CASING"
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
literal|"text"
argument_list|,
literal|"ï¼´ï½ï½ï½ï½ï½ï½"
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
literal|"text"
argument_list|,
literal|"Tone"
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
literal|"text"
argument_list|,
literal|"Testing"
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
literal|"text"
argument_list|,
literal|"testing"
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
literal|"text"
argument_list|,
literal|"toene"
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
literal|"text"
argument_list|,
literal|"Tzne"
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
literal|"text"
argument_list|,
literal|"\u0698\u0698"
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
comment|/**     * Test termquery with german DIN 5007-1 primary strength.    * In this case, Ã¶ is equivalent to o (but not oe)     */
DECL|method|testBasicTermQuery
specifier|public
name|void
name|testBasicTermQuery
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated TQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_de:tone"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=4]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=7]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test rangequery again with the DIN 5007-1 collator.    * We do a range query of tone .. tp, in binary order this    * would retrieve nothing due to case and accent differences.    */
DECL|method|testBasicRangeQuery
specifier|public
name|void
name|testBasicRangeQuery
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated RangeQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_de:[tone TO tp]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=4]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=7]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test rangequery again with an Arabic collator.    * Binary order would normally order U+0633 in this range.    */
DECL|method|testNegativeRangeQuery
specifier|public
name|void
name|testNegativeRangeQuery
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated RangeQ: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_ar:[\u062F TO \u0698]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

