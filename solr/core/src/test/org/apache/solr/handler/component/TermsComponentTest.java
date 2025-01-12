begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
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
name|params
operator|.
name|ModifiableSolrParams
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
name|TermsParams
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_comment
comment|// TermsComponent not currently supported for PointFields
end_comment

begin_class
annotation|@
name|SolrTestCaseJ4
operator|.
name|SuppressPointFields
DECL|class|TermsComponentTest
specifier|public
class|class
name|TermsComponentTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTest
specifier|public
specifier|static
name|void
name|beforeTest
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
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"a"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"a"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"a"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"aa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"aa"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"aaa"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"aaa"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"abbb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ab"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"abb"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"bb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"abc"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"bbbb"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"b"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"baa"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"cccc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"bbb"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"ccccc"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"10"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"ddddd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"11"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"ddddd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"12"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"ddddd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"13"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"ddddd"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"14"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"15"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"16"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"17"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"snake"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"18"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"spider"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"19"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"shark"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"20"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"snake"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"21"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"snake"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"22"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"shark"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyLower
specifier|public
name|void
name|testEmptyLower
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=6"
argument_list|,
literal|"//int[@name='a'] "
argument_list|,
literal|"//int[@name='aa'] "
argument_list|,
literal|"//int[@name='aaa'] "
argument_list|,
literal|"//int[@name='ab'] "
argument_list|,
literal|"//int[@name='abb'] "
argument_list|,
literal|"//int[@name='abc'] "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleFields
specifier|public
name|void
name|testMultipleFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=6"
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnlimitedRows
specifier|public
name|void
name|testUnlimitedRows
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.rows"
argument_list|,
literal|"-1"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=9"
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=10"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrefix
specifier|public
name|void
name|testPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"aa"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"aa"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|"50"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=1"
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegexp
specifier|public
name|void
name|testRegexp
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"a"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"c"
argument_list|,
literal|"terms.upper.incl"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.regex"
argument_list|,
literal|"b.*"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegexpFlagParsing
specifier|public
name|void
name|testRegexpFlagParsing
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermsParams
operator|.
name|TERMS_REGEXP_FLAG
argument_list|,
literal|"case_insensitive"
argument_list|,
literal|"literal"
argument_list|,
literal|"comments"
argument_list|,
literal|"multiline"
argument_list|,
literal|"unix_lines"
argument_list|,
literal|"unicode_case"
argument_list|,
literal|"dotall"
argument_list|,
literal|"canon_eq"
argument_list|)
expr_stmt|;
name|int
name|flags
init|=
operator|new
name|TermsComponent
argument_list|()
operator|.
name|resolveRegexpFlags
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|int
name|expected
init|=
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|LITERAL
operator||
name|Pattern
operator|.
name|COMMENTS
operator||
name|Pattern
operator|.
name|MULTILINE
operator||
name|Pattern
operator|.
name|UNIX_LINES
operator||
name|Pattern
operator|.
name|UNICODE_CASE
operator||
name|Pattern
operator|.
name|DOTALL
operator||
name|Pattern
operator|.
name|CANON_EQ
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegexpWithFlags
specifier|public
name|void
name|testRegexpWithFlags
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: there are no uppercase or mixed-case terms in the index!
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"a"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"c"
argument_list|,
literal|"terms.upper.incl"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.regex"
argument_list|,
literal|"B.*"
argument_list|,
literal|"terms.regex.flag"
argument_list|,
literal|"case_insensitive"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortCount
specifier|public
name|void
name|testSortCount
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.sort"
argument_list|,
literal|"count"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=3"
argument_list|,
literal|"//lst[@name='standardfilt']/int[1][@name='snake'][.='3']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[2][@name='shark'][.='2']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[3][@name='spider'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTermsList
specifier|public
name|void
name|testTermsList
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Terms list always returns in index order
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.list"
argument_list|,
literal|"spider, snake, shark, ddddd, bad"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=4"
argument_list|,
literal|"//lst[@name='standardfilt']/int[1][@name='ddddd'][.='4']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[2][@name='shark'][.='2']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[3][@name='snake'][.='3']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[4][@name='spider'][.='1']"
argument_list|)
expr_stmt|;
comment|//Test with numeric terms
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"terms.list"
argument_list|,
literal|"2, 1"
argument_list|)
argument_list|,
literal|"count(//lst[@name='foo_i']/*)=2"
argument_list|,
literal|"//lst[@name='foo_i']/int[1][@name='1'][.='2']"
argument_list|,
literal|"//lst[@name='foo_i']/int[2][@name='2'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStats
specifier|public
name|void
name|testStats
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Terms list always returns in index order
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.stats"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.list"
argument_list|,
literal|"spider, snake, shark, ddddd, bad"
argument_list|)
argument_list|,
literal|"//lst[@name='indexstats']/long[1][@name='numDocs'][.='23']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSortIndex
specifier|public
name|void
name|testSortIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.prefix"
argument_list|,
literal|"s"
argument_list|,
literal|"terms.sort"
argument_list|,
literal|"index"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=3"
argument_list|,
literal|"//lst[@name='standardfilt']/int[1][@name='shark'][.='2']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[2][@name='snake'][.='3']"
argument_list|,
literal|"//lst[@name='standardfilt']/int[3][@name='spider'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPastUpper
specifier|public
name|void
name|testPastUpper
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
comment|//no upper bound, lower bound doesn't exist
literal|"terms.lower"
argument_list|,
literal|"d"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLowerExclusive
specifier|public
name|void
name|testLowerExclusive
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"a"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=5"
argument_list|,
literal|"//int[@name='aa'] "
argument_list|,
literal|"//int[@name='aaa'] "
argument_list|,
literal|"//int[@name='ab'] "
argument_list|,
literal|"//int[@name='abb'] "
argument_list|,
literal|"//int[@name='abc'] "
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"cc"
argument_list|,
literal|"terms.lower.incl"
argument_list|,
literal|"false"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"d"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"a"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=6"
argument_list|,
literal|"//int[@name='a'] "
argument_list|,
literal|"//int[@name='aa'] "
argument_list|,
literal|"//int[@name='aaa'] "
argument_list|,
literal|"//int[@name='ab'] "
argument_list|,
literal|"//int[@name='abb'] "
argument_list|,
literal|"//int[@name='abc'] "
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"a"
argument_list|,
literal|"terms.upper"
argument_list|,
literal|"b"
argument_list|,
literal|"terms.raw"
argument_list|,
literal|"true"
argument_list|,
comment|// this should have no effect on a text field
literal|"terms.limit"
argument_list|,
literal|"2"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=2"
argument_list|,
literal|"//int[@name='a']"
argument_list|,
literal|"//int[@name='aa']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"foo_i"
argument_list|)
argument_list|,
literal|"//int[@name='1'][.='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"foo_i"
argument_list|,
literal|"terms.raw"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"not(//int[@name='1'][.='2'])"
argument_list|)
expr_stmt|;
comment|// check something at the end of the index
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"zzz_i"
argument_list|)
argument_list|,
literal|"count(//lst[@name='zzz_i']/*)=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMinMaxFreq
specifier|public
name|void
name|testMinMaxFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"a"
argument_list|,
literal|"terms.mincount"
argument_list|,
literal|"2"
argument_list|,
literal|"terms.maxcount"
argument_list|,
literal|"-1"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|"50"
argument_list|)
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.lower"
argument_list|,
literal|"d"
argument_list|,
literal|"terms.mincount"
argument_list|,
literal|"2"
argument_list|,
literal|"terms.maxcount"
argument_list|,
literal|"3"
argument_list|,
literal|"terms.limit"
argument_list|,
literal|"50"
argument_list|)
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=3"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDocFreqAndTotalTermFreq
specifier|public
name|void
name|testDocFreqAndTotalTermFreq
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.ttf"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.list"
argument_list|,
literal|"snake,spider,shark,ddddd"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=4"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='ddddd']/long[@name='df'][.='4']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='ddddd']/long[@name='ttf'][.='4']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='shark']/long[@name='df'][.='2']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='shark']/long[@name='ttf'][.='2']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='snake']/long[@name='df'][.='3']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='snake']/long[@name='ttf'][.='3']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='spider']/long[@name='df'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='spider']/long[@name='ttf'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDocFreqAndTotalTermFreqForNonExistingTerm
specifier|public
name|void
name|testDocFreqAndTotalTermFreqForNonExistingTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.ttf"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.list"
argument_list|,
literal|"boo,snake"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=1"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='snake']/long[@name='df'][.='3']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='snake']/long[@name='ttf'][.='3']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDocFreqAndTotalTermFreqForMultipleFields
specifier|public
name|void
name|testDocFreqAndTotalTermFreqForMultipleFields
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|(
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"qt"
argument_list|,
literal|"/terms"
argument_list|,
literal|"terms"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"terms.fl"
argument_list|,
literal|"standardfilt"
argument_list|,
literal|"terms.ttf"
argument_list|,
literal|"true"
argument_list|,
literal|"terms.list"
argument_list|,
literal|"a,aa,aaa"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"count(//lst[@name='lowerfilt']/*)=3"
argument_list|,
literal|"count(//lst[@name='standardfilt']/*)=3"
argument_list|,
literal|"//lst[@name='lowerfilt']/lst[@name='a']/long[@name='df'][.='2']"
argument_list|,
literal|"//lst[@name='lowerfilt']/lst[@name='a']/long[@name='ttf'][.='2']"
argument_list|,
literal|"//lst[@name='lowerfilt']/lst[@name='aa']/long[@name='df'][.='1']"
argument_list|,
literal|"//lst[@name='lowerfilt']/lst[@name='aa']/long[@name='ttf'][.='1']"
argument_list|,
literal|"//lst[@name='lowerfilt']/lst[@name='aaa']/long[@name='df'][.='1']"
argument_list|,
literal|"//lst[@name='lowerfilt']/lst[@name='aaa']/long[@name='ttf'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='a']/long[@name='df'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='a']/long[@name='ttf'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='aa']/long[@name='df'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='aa']/long[@name='ttf'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='aaa']/long[@name='df'][.='1']"
argument_list|,
literal|"//lst[@name='standardfilt']/lst[@name='aaa']/long[@name='ttf'][.='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

