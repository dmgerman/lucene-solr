begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedCollator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|ULocale
import|;
end_import

begin_comment
comment|/**  * Tests {@link ICUCollationField} with TermQueries, RangeQueries, and sort order.  */
end_comment

begin_class
DECL|class|TestICUCollationField
specifier|public
class|class
name|TestICUCollationField
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
name|String
name|home
init|=
name|setupSolrHome
argument_list|()
decl_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
name|home
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
comment|/**    * Ugly: but what to do? We want to test custom sort, which reads rules in as a resource.    * These are largish files, and jvm-specific (as our documentation says, you should always    * look out for jvm differences with collation).    * So its preferable to create this file on-the-fly.    */
DECL|method|setupSolrHome
specifier|public
specifier|static
name|String
name|setupSolrHome
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|tmpFile
init|=
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
comment|// make data and conf dirs
operator|new
name|File
argument_list|(
name|tmpFile
operator|+
literal|"/collection1"
argument_list|,
literal|"data"
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|confDir
init|=
operator|new
name|File
argument_list|(
name|tmpFile
operator|+
literal|"/collection1"
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|confDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// copy over configuration files
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"analysis-extras/solr/collection1/conf/solrconfig-icucollate.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"solrconfig.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|getFile
argument_list|(
literal|"analysis-extras/solr/collection1/conf/schema-icucollate.xml"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"schema.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// generate custom collation rules (DIN 5007-2), saving to customrules.dat
name|RuleBasedCollator
name|baseCollator
init|=
operator|(
name|RuleBasedCollator
operator|)
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|ULocale
argument_list|(
literal|"de"
argument_list|,
literal|"DE"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|DIN5007_2_tailorings
init|=
literal|"& ae , a\u0308& AE , A\u0308"
operator|+
literal|"& oe , o\u0308& OE , O\u0308"
operator|+
literal|"& ue , u\u0308& UE , u\u0308"
decl_stmt|;
name|RuleBasedCollator
name|tailoredCollator
init|=
operator|new
name|RuleBasedCollator
argument_list|(
name|baseCollator
operator|.
name|getRules
argument_list|()
operator|+
name|DIN5007_2_tailorings
argument_list|)
decl_stmt|;
name|String
name|tailoredRules
init|=
name|tailoredCollator
operator|.
name|getRules
argument_list|()
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|confDir
argument_list|,
literal|"customrules.dat"
argument_list|)
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|write
argument_list|(
name|tailoredRules
argument_list|,
name|os
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|tmpFile
return|;
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
comment|/**     * Test sort with a danish collator. Ã¶ is ordered after z    */
DECL|method|testBasicSort
specifier|public
name|void
name|testBasicSort
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated Sort: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_da:[tz TO tÃ¶z]"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_da asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=11]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=4]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test sort with an arabic collator. U+0633 is ordered after U+0698.    * With a binary collator, the range would also return nothing.    */
DECL|method|testArabicSort
specifier|public
name|void
name|testArabicSort
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"Collated Sort: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"sort_ar:[\u0698 TO \u0633\u0633]"
argument_list|,
literal|"sort"
argument_list|,
literal|"sort_ar asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=12]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=1]"
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
comment|/**    * Test canonical decomposition with turkish primary strength.     * With this sort order, Ä° is the uppercase form of i, and I is the uppercase form of Ä±.    * We index a decomposed form of Ä°.    */
DECL|method|testCanonicalDecomposition
specifier|public
name|void
name|testCanonicalDecomposition
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
literal|"sort_tr_canon:\"I Will Use Turkish CasÄ±ng\""
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.=5]"
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test termquery with custom collator (DIN 5007-2).    * In this case, Ã¶ is equivalent to oe (but not o)     */
DECL|method|testCustomCollation
specifier|public
name|void
name|testCustomCollation
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
literal|"sort_custom:toene"
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
literal|"//result/doc[2]/int[@name='id'][.=10]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

