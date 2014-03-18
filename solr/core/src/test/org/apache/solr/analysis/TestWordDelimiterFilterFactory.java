begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|analysis
operator|.
name|MockTokenizer
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
name|analysis
operator|.
name|TokenStream
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
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
operator|.
name|WordDelimiterFilterFactory
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
name|analysis
operator|.
name|util
operator|.
name|ResourceLoader
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
name|core
operator|.
name|SolrResourceLoader
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
comment|/**  * New WordDelimiterFilter tests... most of the tests are in ConvertedLegacyTest  */
end_comment

begin_comment
comment|// TODO: add a low-level test for this factory
end_comment

begin_class
DECL|class|TestWordDelimiterFilterFactory
specifier|public
class|class
name|TestWordDelimiterFilterFactory
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
DECL|method|posTst
specifier|public
name|void
name|posTst
parameter_list|(
name|String
name|v1
parameter_list|,
name|String
name|v2
parameter_list|,
name|String
name|s1
parameter_list|,
name|String
name|s2
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"subword"
argument_list|,
name|v1
argument_list|,
literal|"subword"
argument_list|,
name|v2
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// there is a positionIncrementGap of 100 between field values, so
comment|// we test if that was maintained.
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~90"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"position increment lost"
argument_list|,
name|req
argument_list|(
literal|"+id:42 +subword:\""
operator|+
name|s1
operator|+
literal|' '
operator|+
name|s2
operator|+
literal|"\"~110"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRetainPositionIncrement
specifier|public
name|void
name|testRetainPositionIncrement
parameter_list|()
block|{
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"-foo-"
argument_list|,
literal|"-bar-"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"123"
argument_list|,
literal|"456"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/"
argument_list|,
literal|"/456/"
argument_list|,
literal|"123"
argument_list|,
literal|"456"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"/123/abc"
argument_list|,
literal|"qwe/456/"
argument_list|,
literal|"abc"
argument_list|,
literal|"qwe"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo"
argument_list|,
literal|"bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|posTst
argument_list|(
literal|"zoo-foo-123"
argument_list|,
literal|"456-bar-baz"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoGenerationEdgeCase
specifier|public
name|void
name|testNoGenerationEdgeCase
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"222"
argument_list|,
literal|"numberpartfail"
argument_list|,
literal|"123.123.123.123"
argument_list|)
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreCaseChange
specifier|public
name|void
name|testIgnoreCaseChange
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"43"
argument_list|,
literal|"wdf_nocase"
argument_list|,
literal|"HellO WilliAM"
argument_list|,
literal|"subword"
argument_list|,
literal|"GoodBye JonEs"
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
literal|"no case change"
argument_list|,
name|req
argument_list|(
literal|"wdf_nocase:(hell o am)"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"case change"
argument_list|,
name|req
argument_list|(
literal|"subword:(good jon)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPreserveOrignalTrue
specifier|public
name|void
name|testPreserveOrignalTrue
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"144"
argument_list|,
literal|"wdf_preserve"
argument_list|,
literal|"404-123"
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
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:404"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:123"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"preserving original word"
argument_list|,
name|req
argument_list|(
literal|"wdf_preserve:404-123*"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
comment|/***   public void testPerformance() throws IOException {     String s = "now is the time-for all good men to come to-the aid of their country.";     Token tok = new Token();     long start = System.currentTimeMillis();     int ret=0;     for (int i=0; i<1000000; i++) {       StringReader r = new StringReader(s);       TokenStream ts = new WhitespaceTokenizer(r);       ts = new WordDelimiterFilter(ts, 1,1,1,1,0);        while (ts.next(tok) != null) ret++;     }      System.out.println("ret="+ret+" time="+(System.currentTimeMillis()-start));   }   ***/
annotation|@
name|Test
DECL|method|testAlphaNumericWords
specifier|public
name|void
name|testAlphaNumericWords
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"68"
argument_list|,
literal|"numericsubword"
argument_list|,
literal|"Java/J2SE"
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
literal|"j2se found"
argument_list|,
name|req
argument_list|(
literal|"numericsubword:(J2SE)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no j2 or se"
argument_list|,
name|req
argument_list|(
literal|"numericsubword:(J2 OR SE)"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testProtectedWords
specifier|public
name|void
name|testProtectedWords
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"70"
argument_list|,
literal|"protectedsubword"
argument_list|,
literal|"c# c++ .net Java/J2SE"
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
literal|"java found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(java)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|".net found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(.net)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c# found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(c#)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c++ found"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:(c++)"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"c found?"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:c"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"net found?"
argument_list|,
name|req
argument_list|(
literal|"protectedsubword:net"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomTypes
specifier|public
name|void
name|testCustomTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testText
init|=
literal|"I borrowed $5,400.00 at 25% interest-rate"
decl_stmt|;
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|"solr/collection1"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|TEST_VERSION_CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"generateWordParts"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"generateNumberParts"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"catenateWords"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"catenateNumbers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"catenateAll"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"splitOnCaseChange"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|/* default behavior */
name|WordDelimiterFilterFactory
name|factoryDefault
init|=
operator|new
name|WordDelimiterFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factoryDefault
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|TokenStream
name|ts
init|=
name|factoryDefault
operator|.
name|create
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
name|testText
argument_list|)
argument_list|)
decl_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"I"
block|,
literal|"borrowed"
block|,
literal|"5"
block|,
literal|"540000"
block|,
literal|"400"
block|,
literal|"00"
block|,
literal|"at"
block|,
literal|"25"
block|,
literal|"interest"
block|,
literal|"interestrate"
block|,
literal|"rate"
block|}
argument_list|)
expr_stmt|;
name|ts
operator|=
name|factoryDefault
operator|.
name|create
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
literal|"foo\u200Dbar"
argument_list|)
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo"
block|,
literal|"foobar"
block|,
literal|"bar"
block|}
argument_list|)
expr_stmt|;
comment|/* custom behavior */
name|args
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
comment|// use a custom type mapping
name|args
operator|.
name|put
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|TEST_VERSION_CURRENT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"generateWordParts"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"generateNumberParts"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"catenateWords"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"catenateNumbers"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"catenateAll"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"splitOnCaseChange"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"types"
argument_list|,
literal|"wdftypes.txt"
argument_list|)
expr_stmt|;
name|WordDelimiterFilterFactory
name|factoryCustom
init|=
operator|new
name|WordDelimiterFilterFactory
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|factoryCustom
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|ts
operator|=
name|factoryCustom
operator|.
name|create
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
name|testText
argument_list|)
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"I"
block|,
literal|"borrowed"
block|,
literal|"$5,400.00"
block|,
literal|"at"
block|,
literal|"25%"
block|,
literal|"interest"
block|,
literal|"interestrate"
block|,
literal|"rate"
block|}
argument_list|)
expr_stmt|;
comment|/* test custom behavior with a char> 0x7F, because we had to make a larger byte[] */
name|ts
operator|=
name|factoryCustom
operator|.
name|create
argument_list|(
name|whitespaceMockTokenizer
argument_list|(
literal|"foo\u200Dbar"
argument_list|)
argument_list|)
expr_stmt|;
name|BaseTokenStreamTestCase
operator|.
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"foo\u200Dbar"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

