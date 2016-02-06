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

begin_class
DECL|class|TestOmitPositions
specifier|public
class|class
name|TestOmitPositions
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
comment|// add some docs
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"nopositionstext"
argument_list|,
literal|"this is a test this is only a test"
argument_list|,
literal|"text"
argument_list|,
literal|"just another test"
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
literal|"nopositionstext"
argument_list|,
literal|"test test test test test test test test test test test test test"
argument_list|,
literal|"text"
argument_list|,
literal|"have a nice day"
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
DECL|method|testFrequencies
specifier|public
name|void
name|testFrequencies
parameter_list|()
block|{
comment|// doc 2 should be ranked above doc 1
name|assertQ
argument_list|(
literal|"term query: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"nopositionstext:test"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.=1]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPositions
specifier|public
name|void
name|testPositions
parameter_list|()
block|{
comment|// no results should be found:
comment|// lucene 3.x: silent failure
comment|// lucene 4.x: illegal state exception, field was indexed without positions
name|ignoreException
argument_list|(
literal|"was indexed without position data"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertQ
argument_list|(
literal|"phrase query: "
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"nopositionstext:\"test test\""
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalStateException
argument_list|)
expr_stmt|;
comment|// in lucene 4.0, queries don't silently fail
block|}
name|resetExceptionIgnores
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

