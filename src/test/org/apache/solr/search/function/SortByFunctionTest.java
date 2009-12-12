begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AbstractSolrTestCase
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|SortByFunctionTest
specifier|public
class|class
name|SortByFunctionTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
DECL|method|test
specifier|public
name|void
name|test
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
literal|"x_td"
argument_list|,
literal|"0"
argument_list|,
literal|"y_td"
argument_list|,
literal|"2"
argument_list|,
literal|"w_td"
argument_list|,
literal|"25"
argument_list|,
literal|"z_td"
argument_list|,
literal|"5"
argument_list|,
literal|"f_t"
argument_list|,
literal|"ipod"
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
literal|"x_td"
argument_list|,
literal|"2"
argument_list|,
literal|"y_td"
argument_list|,
literal|"2"
argument_list|,
literal|"w_td"
argument_list|,
literal|"15"
argument_list|,
literal|"z_td"
argument_list|,
literal|"5"
argument_list|,
literal|"f_t"
argument_list|,
literal|"ipod ipod ipod ipod ipod"
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
literal|"x_td"
argument_list|,
literal|"3"
argument_list|,
literal|"y_td"
argument_list|,
literal|"2"
argument_list|,
literal|"w_td"
argument_list|,
literal|"55"
argument_list|,
literal|"z_td"
argument_list|,
literal|"5"
argument_list|,
literal|"f_t"
argument_list|,
literal|"ipod ipod ipod ipod ipod ipod ipod ipod ipod"
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
literal|"x_td"
argument_list|,
literal|"4"
argument_list|,
literal|"y_td"
argument_list|,
literal|"2"
argument_list|,
literal|"w_td"
argument_list|,
literal|"45"
argument_list|,
literal|"z_td"
argument_list|,
literal|"5"
argument_list|,
literal|"f_t"
argument_list|,
literal|"ipod ipod ipod ipod ipod ipod ipod"
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
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='1']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='2']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='3']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"score desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='1']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='2']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='3']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"q"
argument_list|,
literal|"f_t:ipod"
argument_list|,
literal|"sort"
argument_list|,
literal|"score desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='1']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='4']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='2']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"sum(x_td, y_td) desc"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='4']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='3']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='2']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"sort"
argument_list|,
literal|"sum(x_td, y_td) asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//float[@name='score']='1.0'"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='1']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='2']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='3']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='4']"
argument_list|)
expr_stmt|;
comment|//the function is equal, w_td separates
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
literal|"sum(z_td, y_td) asc, w_td asc"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='2']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='1']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='4']"
argument_list|,
literal|"//result/doc[4]/int[@name='id'][.='3']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/*<lst name="responseHeader"><int name="status">0</int><int name="QTime">93</int></lst><result name="response" numFound="4" start="0" maxScore="1.0"><doc><float name="score">1.0</float><int name="id">4</int><int name="intDefault">42</int><arr name="multiDefault"><str>muLti-Default</str></arr><date name="timestamp">2009-12-12T12:59:46.412Z</date><arr name="x_td"><double>4.0</double></arr><arr name="y_td"><double>2.0</double></arr></doc><doc><float name="score">1.0</float><int name="id">3</int><int name="intDefault">42</int><arr name="multiDefault"><str>muLti-Default</str></arr><date name="timestamp">2009-12-12T12:59:46.409Z</date><arr name="x_td"><double>3.0</double></arr><arr name="y_td"><double>2.0</double></arr></doc><doc><float name="score">1.0</float><int name="id">2</int><int name="intDefault">42</int><arr name="multiDefault"><str>muLti-Default</str></arr><date name="timestamp">2009-12-12T12:59:46.406Z</date><arr name="x_td"><double>2.0</double></arr><arr name="y_td"><double>2.0</double></arr></doc><doc><float name="score">1.0</float><int name="id">1</int><int name="intDefault">42</int><arr name="multiDefault"><str>muLti-Default</str></arr><date name="timestamp">2009-12-12T12:59:46.361Z</date><arr name="x_td"><double>0.0</double></arr><arr name="y_td"><double>2.0</double></arr></doc></result> */
end_comment

end_unit

