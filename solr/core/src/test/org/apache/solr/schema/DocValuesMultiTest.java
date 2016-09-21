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
name|IOException
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
name|index
operator|.
name|DocValuesType
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
name|index
operator|.
name|FieldInfos
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
name|index
operator|.
name|LeafReader
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
name|index
operator|.
name|SortedSetDocValues
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
name|core
operator|.
name|SolrCore
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
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
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
DECL|class|DocValuesMultiTest
specifier|public
class|class
name|DocValuesMultiTest
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
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-docValuesMulti.xml"
argument_list|)
expr_stmt|;
comment|// sanity check our schema meets our expectations
specifier|final
name|IndexSchema
name|schema
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getLatestSchema
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
operator|new
name|String
index|[]
block|{
literal|"floatdv"
block|,
literal|"intdv"
block|,
literal|"doubledv"
block|,
literal|"longdv"
block|,
literal|"datedv"
block|,
literal|"stringdv"
block|,
literal|"booldv"
block|}
control|)
block|{
specifier|final
name|SchemaField
name|sf
init|=
name|schema
operator|.
name|getField
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|+
literal|" is not multiValued, test is useless, who changed the schema?"
argument_list|,
name|sf
operator|.
name|multiValued
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|f
operator|+
literal|" is indexed, test is useless, who changed the schema?"
argument_list|,
name|sf
operator|.
name|indexed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|+
literal|" has no docValues, test is useless, who changed the schema?"
argument_list|,
name|sf
operator|.
name|hasDocValues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDocValues
specifier|public
name|void
name|testDocValues
parameter_list|()
throws|throws
name|IOException
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"4.5"
argument_list|,
literal|"intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"3"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"value1"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"value2"
argument_list|,
literal|"booldv"
argument_list|,
literal|"false"
argument_list|,
literal|"booldv"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCoreInc
argument_list|()
init|)
block|{
specifier|final
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcherRef
init|=
name|core
operator|.
name|openNewSearcher
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|SolrIndexSearcher
name|searcher
init|=
name|searcherRef
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|LeafReader
name|reader
init|=
name|searcher
operator|.
name|getLeafReader
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FieldInfos
name|infos
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|infos
operator|.
name|fieldInfo
argument_list|(
literal|"stringdv"
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|infos
operator|.
name|fieldInfo
argument_list|(
literal|"booldv"
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|infos
operator|.
name|fieldInfo
argument_list|(
literal|"floatdv"
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|infos
operator|.
name|fieldInfo
argument_list|(
literal|"intdv"
argument_list|)
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|SortedSetDocValues
name|dv
init|=
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"stringdv"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dv
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|dv
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|dv
operator|=
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
literal|"booldv"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|dv
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|dv
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
argument_list|,
name|dv
operator|.
name|nextOrd
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searcherRef
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** Tests the ability to do basic queries (without scoring, just match-only) on    *  string docvalues fields that are not inverted (indexed "forward" only)    */
annotation|@
name|Test
DECL|method|testStringDocValuesMatch
specifier|public
name|void
name|testStringDocValuesMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"b"
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
literal|"stringdv"
argument_list|,
literal|"a"
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
literal|"stringdv"
argument_list|,
literal|"c"
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
literal|"stringdv"
argument_list|,
literal|"car"
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
literal|"stringdv"
argument_list|,
literal|"dog"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// string: termquery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"stringdv:car"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=4]"
argument_list|)
expr_stmt|;
comment|// string: range query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"stringdv:[b TO d]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=4]"
argument_list|,
literal|"//result/doc[4]/str[@name='id'][.=5]"
argument_list|)
expr_stmt|;
comment|// string: prefix query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"stringdv:c*"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=4]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=5]"
argument_list|)
expr_stmt|;
comment|// string: wildcard query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"stringdv:c?r"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=4]"
argument_list|)
expr_stmt|;
comment|// string: regexp query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"stringdv:/c[a-b]r/"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=4]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the ability to do basic queries (without scoring, just match-only) on    *  boolean docvalues fields that are not inverted (indexed "forward" only)    */
annotation|@
name|Test
DECL|method|testBoolDocValuesMatch
specifier|public
name|void
name|testBoolDocValuesMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"booldv"
argument_list|,
literal|"true"
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
literal|"booldv"
argument_list|,
literal|"false"
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
literal|"booldv"
argument_list|,
literal|"true"
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
literal|"booldv"
argument_list|,
literal|"false"
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
literal|"booldv"
argument_list|,
literal|"true"
argument_list|,
literal|"booldv"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// string: termquery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"booldv:true"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=5]"
argument_list|)
expr_stmt|;
comment|// boolean: range query,
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"booldv:[false TO false]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=4]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=5]"
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
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|,
literal|"rows"
argument_list|,
literal|"10"
argument_list|,
literal|"fl"
argument_list|,
literal|"booldv"
argument_list|)
argument_list|,
literal|"//result/doc[1]/arr[@name='booldv']/bool[1][.='true']"
argument_list|,
literal|"//result/doc[2]/arr[@name='booldv']/bool[1][.='false']"
argument_list|,
literal|"//result/doc[3]/arr[@name='booldv']/bool[1][.='true']"
argument_list|,
literal|"//result/doc[4]/arr[@name='booldv']/bool[1][.='false']"
argument_list|,
literal|"//result/doc[5]/arr[@name='booldv']/bool[1][.='false']"
argument_list|,
literal|"//result/doc[5]/arr[@name='booldv']/bool[2][.='true']"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the ability to do basic queries (without scoring, just match-only) on    *  float docvalues fields that are not inverted (indexed "forward" only)    */
annotation|@
name|Test
DECL|method|testFloatDocValuesMatch
specifier|public
name|void
name|testFloatDocValuesMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"floatdv"
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
literal|"2"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"-5"
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
literal|"floatdv"
argument_list|,
literal|"3.0"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"2.2"
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
literal|"floatdv"
argument_list|,
literal|"3"
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
literal|"floatdv"
argument_list|,
literal|"-0.5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// float: termquery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"floatdv:3"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=4]"
argument_list|)
expr_stmt|;
comment|// float: rangequery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"floatdv:[-1 TO 2.5]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=5]"
argument_list|)
expr_stmt|;
comment|// (neg) float: rangequery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"floatdv:[-6 TO -4]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|)
expr_stmt|;
comment|// (neg) float: termquery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"floatdv:\"-5\""
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the ability to do basic queries (without scoring, just match-only) on    *  double docvalues fields that are not inverted (indexed "forward" only)    */
annotation|@
name|Test
DECL|method|testDoubleDocValuesMatch
specifier|public
name|void
name|testDoubleDocValuesMatch
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"doubledv"
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
literal|"2"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"-5"
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
literal|"doubledv"
argument_list|,
literal|"3.0"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"2.2"
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
literal|"doubledv"
argument_list|,
literal|"3"
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
literal|"doubledv"
argument_list|,
literal|"-0.5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// double: termquery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doubledv:3"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=4]"
argument_list|)
expr_stmt|;
comment|// double: rangequery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doubledv:[-1 TO 2.5]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=3]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=5]"
argument_list|)
expr_stmt|;
comment|// (neg) double: rangequery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doubledv:[-6 TO -4]"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|)
expr_stmt|;
comment|// (neg) double: termquery
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"doubledv:\"-5\""
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDocValuesFacetingSimple
specifier|public
name|void
name|testDocValuesFacetingSimple
parameter_list|()
block|{
comment|// this is the random test verbatim from DocValuesTest, so it populates with the default values defined in its schema.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|50
condition|;
operator|++
name|i
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"floatdv"
argument_list|,
literal|"1"
argument_list|,
literal|"intdv"
argument_list|,
literal|"2"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"3"
argument_list|,
literal|"longdv"
argument_list|,
literal|"4"
argument_list|,
literal|"datedv"
argument_list|,
literal|"1995-12-31T23:59:59.999Z"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"abc"
argument_list|,
literal|"booldv"
argument_list|,
literal|"true"
argument_list|)
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
literal|50
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// to have several segments
block|}
switch|switch
condition|(
name|i
operator|%
literal|3
condition|)
block|{
case|case
literal|0
case|:
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
operator|+
name|i
argument_list|,
literal|"floatdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"intdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"doubledv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"longdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"datedv"
argument_list|,
operator|(
literal|1900
operator|+
name|i
operator|)
operator|+
literal|"-12-31T23:59:59.999Z"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"abc"
operator|+
name|i
argument_list|,
literal|"booldv"
argument_list|,
literal|"true"
argument_list|,
literal|"booldv"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
operator|+
name|i
argument_list|,
literal|"floatdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"intdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"doubledv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"longdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"datedv"
argument_list|,
operator|(
literal|1900
operator|+
name|i
operator|)
operator|+
literal|"-12-31T23:59:59.999Z"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"abc"
operator|+
name|i
argument_list|,
literal|"booldv"
argument_list|,
literal|"false"
argument_list|,
literal|"booldv"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1000"
operator|+
name|i
argument_list|,
literal|"floatdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"intdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"doubledv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"longdv"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"datedv"
argument_list|,
operator|(
literal|1900
operator|+
name|i
operator|)
operator|+
literal|"-12-31T23:59:59.999Z"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"abc"
operator|+
name|i
argument_list|,
literal|"booldv"
argument_list|,
literal|"true"
argument_list|,
literal|"booldv"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"longdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='longdv']/int[@name='4'][.='51']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"longdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='longdv']/int[@name='0'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"longdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"33"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='longdv']/int[@name='33'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='floatdv']/int[@name='1.0'][.='51']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='floatdv']/int[@name='0.0'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"floatdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"33"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='floatdv']/int[@name='33.0'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='doubledv']/int[@name='3.0'][.='51']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='doubledv']/int[@name='0.0'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"doubledv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"33"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='doubledv']/int[@name='33.0'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"intdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='intdv']/int[@name='2'][.='51']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"intdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='intdv']/int[@name='0'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"intdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"33"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='intdv']/int[@name='33'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"datedv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='datedv']/int[@name='1995-12-31T23:59:59.999Z'][.='50']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"datedv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='datedv']/int[@name='1900-12-31T23:59:59.999Z'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"datedv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"33"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='datedv']/int[@name='1933-12-31T23:59:59.999Z'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='stringdv']/int[@name='abc'][.='50']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='stringdv']/int[@name='abc1'][.='1']"
argument_list|,
literal|"//lst[@name='stringdv']/int[@name='abc13'][.='1']"
argument_list|,
literal|"//lst[@name='stringdv']/int[@name='abc19'][.='1']"
argument_list|,
literal|"//lst[@name='stringdv']/int[@name='abc49'][.='1']"
argument_list|)
expr_stmt|;
comment|// Even though offseting by 33, the sort order is abc1 abc11....abc2 so it throws the position in the return list off.
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"index"
argument_list|,
literal|"facet.offset"
argument_list|,
literal|"33"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='stringdv']/int[@name='abc38'][.='1']"
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
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"booldv"
argument_list|,
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|)
argument_list|,
literal|"//lst[@name='booldv']/int[@name='true'][.='83']"
argument_list|,
literal|"//lst[@name='booldv']/int[@name='false'][.='33']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

