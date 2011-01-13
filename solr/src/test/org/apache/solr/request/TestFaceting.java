begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
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
name|index
operator|.
name|Term
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
name|BytesRef
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
name|After
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|TestFaceting
specifier|public
class|class
name|TestFaceting
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
literal|"schema11.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|t
name|String
name|t
parameter_list|(
name|int
name|tnum
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|US
argument_list|,
literal|"%08d"
argument_list|,
name|tnum
argument_list|)
return|;
block|}
DECL|method|createIndex
name|void
name|createIndex
parameter_list|(
name|int
name|nTerms
parameter_list|)
block|{
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|Float
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|proto
operator|.
name|field
argument_list|()
argument_list|,
name|t
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
comment|// squeeze out any possible deleted docs
block|}
DECL|field|proto
name|Term
name|proto
init|=
operator|new
name|Term
argument_list|(
literal|"field_s"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
DECL|field|req
name|SolrQueryRequest
name|req
decl_stmt|;
comment|// used to get a searcher
DECL|method|close
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|req
operator|!=
literal|null
condition|)
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
name|req
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|doTermEnum
name|void
name|doTermEnum
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|close
argument_list|()
expr_stmt|;
name|createIndex
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|req
operator|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
name|TermIndex
name|ti
init|=
operator|new
name|TermIndex
argument_list|(
name|proto
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|NumberedTermsEnum
name|te
init|=
name|ti
operator|.
name|getEnumerator
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
comment|// iterate through first
while|while
condition|(
name|te
operator|.
name|term
argument_list|()
operator|!=
literal|null
condition|)
name|te
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|te
operator|.
name|getTermNumber
argument_list|()
argument_list|)
expr_stmt|;
name|te
operator|.
name|close
argument_list|()
expr_stmt|;
name|te
operator|=
name|ti
operator|.
name|getEnumerator
argument_list|(
name|req
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
name|size
argument_list|)
decl_stmt|;
comment|// test seeking by term string
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
operator|*
literal|2
operator|+
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|int
name|rnum
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|size
operator|+
literal|2
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|t
argument_list|(
name|rnum
argument_list|)
decl_stmt|;
name|BytesRef
name|br
init|=
name|te
operator|.
name|skipTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|br
operator|!=
literal|null
argument_list|,
name|rnum
operator|<
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|rnum
operator|<
name|size
condition|)
block|{
name|assertEquals
argument_list|(
name|rnum
argument_list|,
name|te
operator|.
name|pos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|size
argument_list|,
name|te
operator|.
name|getTermNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// test seeking before term
name|assertEquals
argument_list|(
name|size
operator|>
literal|0
argument_list|,
name|te
operator|.
name|skipTo
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"000"
argument_list|)
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|te
operator|.
name|getTermNumber
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
name|t
argument_list|(
literal|0
argument_list|)
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
comment|// test seeking by term number
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
operator|*
literal|2
operator|+
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|int
name|rnum
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|t
argument_list|(
name|rnum
argument_list|)
decl_stmt|;
name|BytesRef
name|br
init|=
name|te
operator|.
name|skipTo
argument_list|(
name|rnum
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|br
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rnum
argument_list|,
name|te
operator|.
name|pos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testTermEnum
specifier|public
name|void
name|testTermEnum
parameter_list|()
throws|throws
name|Exception
block|{
name|doTermEnum
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|doTermEnum
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|doTermEnum
argument_list|(
name|TermIndex
operator|.
name|interval
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// test boundaries around the block size
name|doTermEnum
argument_list|(
name|TermIndex
operator|.
name|interval
argument_list|)
expr_stmt|;
name|doTermEnum
argument_list|(
name|TermIndex
operator|.
name|interval
operator|+
literal|1
argument_list|)
expr_stmt|;
name|doTermEnum
argument_list|(
name|TermIndex
operator|.
name|interval
operator|*
literal|2
operator|+
literal|2
argument_list|)
expr_stmt|;
comment|// doTermEnum(TermIndex.interval * 3 + 3);
block|}
annotation|@
name|Test
DECL|method|testFacets
specifier|public
name|void
name|testFacets
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// go over 4096 to test some of the buffer resizing
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"many_ws"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
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
literal|"check many tokens"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.method"
argument_list|,
literal|"fc"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"many_ws"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|)
argument_list|,
literal|"*[count(//lst[@name='many_ws']/int)=5000]"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|0
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|1
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|2
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|3
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|5
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4092
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4093
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4094
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4095
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4096
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4097
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4098
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4090
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4999
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|)
expr_stmt|;
comment|// test gaps that take more than one byte
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|150
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|301
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|453
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|606
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|2010
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|3050
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
literal|4999
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
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
literal|"many_ws"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"check many tokens"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:1"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.method"
argument_list|,
literal|"fc"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"many_ws"
argument_list|,
literal|"facet.limit"
argument_list|,
literal|"-1"
argument_list|)
argument_list|,
literal|"*[count(//lst[@name='many_ws']/int)=5000]"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|0
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|150
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|301
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|453
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|606
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|1000
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|2010
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|3050
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
literal|4999
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegularBig
specifier|public
name|void
name|testRegularBig
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// go over 4096 to test some of the buffer resizing
name|int
name|nTerms
init|=
literal|7
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nTerms
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|t
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|int
name|i1
init|=
literal|1000000
decl_stmt|;
comment|// int iter=65536+10;
name|int
name|iter
init|=
literal|1000
decl_stmt|;
name|int
name|commitInterval
init|=
name|iter
operator|/
literal|9
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
comment|// assertU(adoc("id", t(i), "many_ws", many_ws + t(i1+i) + " " + t(i1*2+i)));
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|t
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"many_ws"
argument_list|,
name|t
argument_list|(
name|i1
operator|+
name|i
argument_list|)
operator|+
literal|" "
operator|+
name|t
argument_list|(
name|i1
operator|*
literal|2
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|iter
operator|%
name|commitInterval
operator|==
literal|0
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|+=
name|iter
operator|/
literal|10
control|)
block|{
name|assertQ
argument_list|(
literal|"check many tokens"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|t
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.method"
argument_list|,
literal|"fc"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"many_ws"
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
literal|"*[count(//lst[@name='many_ws']/int)="
operator|+
literal|2
operator|+
literal|"]"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
name|i1
operator|+
name|i
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
name|i1
operator|*
literal|2
operator|+
name|i
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|)
expr_stmt|;
block|}
name|int
name|i
init|=
name|iter
operator|-
literal|1
decl_stmt|;
name|assertQ
argument_list|(
literal|"check many tokens"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"id:"
operator|+
name|t
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.method"
argument_list|,
literal|"fc"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"many_ws"
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
literal|"*[count(//lst[@name='many_ws']/int)="
operator|+
literal|2
operator|+
literal|"]"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
name|i1
operator|+
name|i
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|,
literal|"//lst[@name='many_ws']/int[@name='"
operator|+
name|t
argument_list|(
name|i1
operator|*
literal|2
operator|+
name|i
argument_list|)
operator|+
literal|"'][.='1']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

