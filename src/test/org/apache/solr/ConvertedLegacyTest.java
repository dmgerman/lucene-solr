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
name|solr
operator|.
name|request
operator|.
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This tests was converted from a legacy testing system.  *  * it does not represent the best practices that should be used when  * writing Solr JUnit tests  */
end_comment

begin_class
DECL|class|ConvertedLegacyTest
specifier|public
class|class
name|ConvertedLegacyTest
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
DECL|method|testABunchOfConvertedStuff
specifier|public
name|void
name|testABunchOfConvertedStuff
parameter_list|()
block|{
comment|// these may be reused by things that need a special query
name|SolrQueryRequest
name|req
init|=
literal|null
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
comment|// compact the index, keep things from getting out of hand
name|assertU
argument_list|(
literal|"<optimize/>"
argument_list|)
expr_stmt|;
comment|// test query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test escaping of ";"
name|assertU
argument_list|(
literal|"<delete><id>42</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"val_s\">aa;bb</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND val_s:aa\\;bb"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND val_s:\"aa;bb\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND val_s:\"aa\""
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test allowDups default of false
name|assertU
argument_list|(
literal|"<delete><id>42</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"val_s\">AAA</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"val_s\">BBB</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"//str[.='BBB']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"val_s\">CCC</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"val_s\">DDD</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"//str[.='DDD']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>42</id></delete>"
argument_list|)
expr_stmt|;
comment|// test deletes
name|assertU
argument_list|(
literal|"<delete><query>id:[100 TO 110]</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"false\"><doc><field name=\"id\">101</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"false\"><doc><field name=\"id\">101</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">105</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"false\"><doc><field name=\"id\">102</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">103</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"false\"><doc><field name=\"id\">101</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>102</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><query>id:105</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><query>id:[100 TO 110]</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test range
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"val_s\">apple</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"val_s\">banana</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"val_s\">pear</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=3] "
argument_list|,
literal|"//*[@start='0']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=1] "
argument_list|,
literal|"*//doc[1]/str[.='pear'] "
argument_list|,
literal|"//*[@start='2']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|25
argument_list|,
literal|5
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=1] "
argument_list|,
literal|"*//doc[1]/str[.='apple']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=2] "
argument_list|,
literal|"*//doc[2]/str[.='banana']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=1] "
argument_list|,
literal|"*//doc[1]/str[.='banana']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z]"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z];val_s asc"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"val_s:[a TO z];val_s desc"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//*[@numFound='3'] "
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO b]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO cat]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[* TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[apple TO pear]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[bear TO boar]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO a]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[apple TO apple]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:{apple TO pear}"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:{a TO z}"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:{* TO *}"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
comment|// test rangequery within a boolean query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND val_s:[a TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 OR val_s:[a TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO b] OR val_s:[b TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+val_s:[a TO b] -val_s:[b TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"-val_s:[a TO b] +val_s:[b TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO c] AND val_s:[apple TO z]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[a TO c] AND val_s:[a TO apple]"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND (val_s:[a TO c] AND val_s:[a TO apple])"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"(val_s:[apple TO apple] OR val_s:[a TO c]) AND (val_s:[b TO c] OR val_s:[b TO b])"
argument_list|)
argument_list|,
literal|"//*[@numFound='1'] "
argument_list|,
literal|"//str[.='banana']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"(val_s:[apple TO apple] AND val_s:[a TO c]) OR (val_s:[p TO z] AND val_s:[a TO z])"
argument_list|)
argument_list|,
literal|"//*[@numFound='2'] "
argument_list|,
literal|"//str[.='apple'] "
argument_list|,
literal|"//str[.='pear']"
argument_list|)
expr_stmt|;
comment|// check for docs that appear more than once in a range
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"val_s\">apple</field><field name=\"val_s\">banana</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[* TO *] OR  val_s:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[* TO *] AND  val_s:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
comment|//<delete><id>44</id></delete>
name|assertU
argument_list|(
literal|"<add overwritePending=\"true\" overwriteCommitted=\"true\"><doc><field name=\"id\">44</field><field name=\"text\">red riding hood</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND red"
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND ride"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND blue"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
comment|// allow duplicates
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\" overwriteCommitted=\"false\" overwritePending=\"false\"><doc><field name=\"id\">44</field><field name=\"text\">red riding hood</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\" overwriteCommitted=\"false\" overwritePending=\"false\"><doc><field name=\"id\">44</field><field name=\"text\">big bad wolf</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//@numFound[.='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND red"
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44 AND wolf"
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 red wolf"
argument_list|)
argument_list|,
literal|"//@numFound[.='2']"
argument_list|)
expr_stmt|;
comment|// test removal of multiples w/o adding anything else
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
comment|// untokenized string type
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"ssto\">and a 10.4 ?</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//str[.='and a 10.4 ?']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"sind\">abc123</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
comment|// TODO: how to search for something with spaces....
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"sind:abc123"
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"*[count(//@name[.='sind'])=0] "
argument_list|,
literal|"*[count(//@name[.='id'])=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"sindsto\">abc123</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
comment|// TODO: how to search for something with spaces....
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"sindsto:abc123"
argument_list|)
argument_list|,
literal|"//str[.='abc123']"
argument_list|)
expr_stmt|;
comment|// test output of multivalued fields
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"title\">yonik3</field><field name=\"title\" boost=\"2\">yonik4</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit></commit>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//arr[@name='title'][./str='yonik3' and ./str='yonik4'] "
argument_list|,
literal|"*[count(//@name[.='title'])=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"title:yonik3"
argument_list|)
argument_list|,
literal|"//@numFound[.>'0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"title:yonik4"
argument_list|)
argument_list|,
literal|"//@numFound[.>'0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"title:yonik5"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><query>title:yonik4</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
comment|// not visible until commit
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
comment|// test configurable stop words
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"teststop\">world stopworda view</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +teststop:world"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"teststop:stopworda"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
comment|// test ignoreCase stop words
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"stopfilt\">world AnD view</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +stopfilt:world"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"stopfilt:\"and\""
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"stopfilt:\"AND\""
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"stopfilt:\"AnD\""
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
comment|// test dynamic field types
name|assertU
argument_list|(
literal|"<delete fromPending=\"true\" fromCommitted=\"true\"><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"gack_i\">51778</field><field name=\"t_name\">cats</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
comment|// test if the dyn fields got added
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"*[count(//doc/*)>=3]  "
argument_list|,
literal|"//int[@name='gack_i'][.='51778']  "
argument_list|,
literal|"//str[@name='t_name'][.='cats']"
argument_list|)
expr_stmt|;
comment|// now test if we can query by a dynamic field (requires analyzer support)
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"t_name:cat"
argument_list|)
argument_list|,
literal|"//str[@name='t_name' and .='cats']"
argument_list|)
expr_stmt|;
comment|// check that deleteByQuery works for dynamic fields
name|assertU
argument_list|(
literal|"<delete><query>t_name:cat</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"t_name:cat"
argument_list|)
argument_list|,
literal|"//@numFound[.='0']"
argument_list|)
expr_stmt|;
comment|// test that longest dynamic field match happens first
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"xaa\">mystr</field><field name=\"xaaa\">12321</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//str[@name='xaa'][.='mystr']  "
argument_list|,
literal|"//int[@name='xaaa'][.='12321']"
argument_list|)
expr_stmt|;
comment|// test integer ranges and sorting
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">1234567890</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">10</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">2</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">15</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">-1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">-987654321</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">2147483647</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">-2147483648</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_i\">0</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"*[count(//doc)=10]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_i:2147483647"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//int[.='2147483647']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_i:\"-2147483648\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"//int[.='-2147483648']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_i asc;"
argument_list|)
argument_list|,
literal|"//doc[1]/int[.='-2147483648'] "
argument_list|,
literal|"//doc[last()]/int[.='2147483647']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_i desc;"
argument_list|)
argument_list|,
literal|"//doc[1]/int[.='2147483647'] "
argument_list|,
literal|"//doc[last()]/int[.='-2147483648']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_i:[0 TO 9]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_i:[-2147483648 TO 2147483647]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=10]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_i:[-10 TO -1]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
comment|// test long ranges and sorting
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">1234567890</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">10</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">2</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">15</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">-1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">-987654321</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">9223372036854775807</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">-9223372036854775808</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_l\">0</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"*[count(//doc)=10]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_l:9223372036854775807"
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"//long[.='9223372036854775807']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_l:\"-9223372036854775808\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1'] "
argument_list|,
literal|"//long[.='-9223372036854775808']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_l asc;"
argument_list|)
argument_list|,
literal|"//doc[1]/long[.='-9223372036854775808'] "
argument_list|,
literal|"//doc[last()]/long[.='9223372036854775807']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_l desc;"
argument_list|)
argument_list|,
literal|"//doc[1]/long[.='9223372036854775807'] "
argument_list|,
literal|"//doc[last()]/long[.='-9223372036854775808']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_l:[-1 TO 9]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_l:[-9223372036854775808 TO 9223372036854775807]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=10]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_l:[-10 TO -1]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
comment|// test binary float ranges and sorting
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">1.4142135</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">Infinity</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">-Infinity</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">NaN</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">2</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">-1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">-987654321</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">-999999.99</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">-1e20</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_f\">0</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"*[count(//doc)=10]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_f:Infinity"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//float[.='Infinity']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_f:\"-Infinity\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//float[.='-Infinity']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_f:\"NaN\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//float[.='NaN']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_f:\"-1e20\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_f asc;"
argument_list|)
argument_list|,
literal|"//doc[1]/float[.='-Infinity'] "
argument_list|,
literal|"//doc[last()]/float[.='NaN']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_f desc;"
argument_list|)
argument_list|,
literal|"//doc[1]/float[.='NaN'] "
argument_list|,
literal|"//doc[last()]/float[.='-Infinity']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_f:[-1 TO 2]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=4]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_f:[-Infinity TO Infinity]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=9]"
argument_list|)
expr_stmt|;
comment|// test binary double ranges and sorting
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">1.4142135</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">Infinity</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">-Infinity</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">NaN</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">2</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">-1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">1e-100</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">-999999.99</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">-1e100</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"num_d\">0</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"*[count(//doc)=10]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:Infinity"
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//double[.='Infinity']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:\"-Infinity\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//double[.='-Infinity']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:\"NaN\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']  "
argument_list|,
literal|"//double[.='NaN']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:\"-1e100\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:\"1e-100\""
argument_list|)
argument_list|,
literal|"//@numFound[.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_d asc;"
argument_list|)
argument_list|,
literal|"//doc[1]/double[.='-Infinity'] "
argument_list|,
literal|"//doc[last()]/double[.='NaN']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;num_d desc;"
argument_list|)
argument_list|,
literal|"//doc[1]/double[.='NaN'] "
argument_list|,
literal|"//doc[last()]/double[.='-Infinity']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:[-1 TO 2]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=5]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"num_d:[-Infinity TO Infinity]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=9]"
argument_list|)
expr_stmt|;
comment|// test sorting on multiple fields
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"a_i\">10</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"a_i\">1</field><field name=\"b_i\">100</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"a_i\">-1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"a_i\">15</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"a_i\">1</field><field name=\"b_i\">50</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id\">44</field><field name=\"a_i\">0</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44; a_i asc,b_i desc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6] "
argument_list|,
literal|"//doc[3]/int[.='100'] "
argument_list|,
literal|"//doc[4]/int[.='50']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;a_i asc  , b_i asc;"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6] "
argument_list|,
literal|"//doc[3]/int[.='50'] "
argument_list|,
literal|"//doc[4]/int[.='100']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;a_i asc;"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6] "
argument_list|,
literal|"//doc[1]/int[.='-1'] "
argument_list|,
literal|"//doc[last()]/int[.='15']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44;a_i asc , score top;"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6] "
argument_list|,
literal|"//doc[1]/int[.='-1'] "
argument_list|,
literal|"//doc[last()]/int[.='15']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44; score top , a_i top, b_i bottom ;"
argument_list|)
argument_list|,
literal|"*[count(//doc)=6] "
argument_list|,
literal|"//doc[last()]/int[.='-1'] "
argument_list|,
literal|"//doc[1]/int[.='15'] "
argument_list|,
literal|"//doc[3]/int[.='50'] "
argument_list|,
literal|"//doc[4]/int[.='100']"
argument_list|)
expr_stmt|;
comment|// test sorting  with some docs missing the sort field
name|assertU
argument_list|(
literal|"<delete><query>id_i:[1000 TO 1010]</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1000</field><field name=\"a_i\">1</field><field name=\"nullfirst\">Z</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1001</field><field name=\"a_i\">10</field><field name=\"nullfirst\">A</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1002</field><field name=\"a_i\">1</field><field name=\"b_i\">100</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1003</field><field name=\"a_i\">-1</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1004</field><field name=\"a_i\">15</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1005</field><field name=\"a_i\">1</field><field name=\"b_i\">50</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add allowDups=\"true\"><doc><field name=\"id_i\">1006</field><field name=\"a_i\">0</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1010]"
argument_list|)
argument_list|,
literal|"*[count(//doc)=7]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1010]; b_i asc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=7] "
argument_list|,
literal|"//doc[1]/int[.='50'] "
argument_list|,
literal|"//doc[2]/int[.='100']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1010]; b_i desc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=7] "
argument_list|,
literal|"//doc[1]/int[.='100'] "
argument_list|,
literal|"//doc[2]/int[.='50']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1010]; a_i asc,b_i desc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=7] "
argument_list|,
literal|"//doc[3]/int[.='100'] "
argument_list|,
literal|"//doc[4]/int[.='50']  "
argument_list|,
literal|"//doc[5]/int[.='1000']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1010]; a_i asc,b_i asc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=7] "
argument_list|,
literal|"//doc[3]/int[.='50'] "
argument_list|,
literal|"//doc[4]/int[.='100']  "
argument_list|,
literal|"//doc[5]/int[.='1000']"
argument_list|)
expr_stmt|;
comment|// nullfirst tests
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1002]; nullfirst asc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3] "
argument_list|,
literal|"//doc[1]/int[.='1002']"
argument_list|,
literal|"//doc[2]/int[.='1001']  "
argument_list|,
literal|"//doc[3]/int[.='1000']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id_i:[1000 TO 1002]; nullfirst desc"
argument_list|)
argument_list|,
literal|"*[count(//doc)=3] "
argument_list|,
literal|"//doc[1]/int[.='1002']"
argument_list|,
literal|"//doc[2]/int[.='1000']  "
argument_list|,
literal|"//doc[3]/int[.='1001']"
argument_list|)
expr_stmt|;
comment|// Sort parsing exception tests.  (SOLR-6, SOLR-99)
name|assertQEx
argument_list|(
literal|"can not sort unindexed fields"
argument_list|,
name|req
argument_list|(
literal|"id_i:1000; shouldbeunindexed asc"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|"invalid query format"
argument_list|,
name|req
argument_list|(
literal|"id_i:1000; nullfirst"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|"unknown sort field"
argument_list|,
name|req
argument_list|(
literal|"id_i:1000; abcde12345 asc"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
name|assertQEx
argument_list|(
literal|"unknown sort order"
argument_list|,
name|req
argument_list|(
literal|"id_i:1000; nullfirst aaa"
argument_list|)
argument_list|,
literal|400
argument_list|)
expr_stmt|;
comment|// test prefix query
name|assertU
argument_list|(
literal|"<delete><query>val_s:[* TO *]</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">100</field><field name=\"val_s\">apple</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">101</field><field name=\"val_s\">banana</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">102</field><field name=\"val_s\">apple</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">103</field><field name=\"val_s\">pearing</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">104</field><field name=\"val_s\">pear</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">105</field><field name=\"val_s\">appalling</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">106</field><field name=\"val_s\">pearson</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">107</field><field name=\"val_s\">port</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:a*"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"val_s:p*"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
comment|// val_s:* %//*[@numFound="8"]
name|assertU
argument_list|(
literal|"<delete><query>id:[100 TO 110]</query></delete>"
argument_list|)
expr_stmt|;
comment|// test copyField functionality
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"title\">How Now4 brown Cows</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND title:Now"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND title_lettertok:Now"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND title:cow"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND title_stemmed:cow"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:cow"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
comment|// test copyField functionality with a pattern.
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"copy_t\">Copy me to the text field pretty please.</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:pretty"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND copy_t:pretty"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
comment|// test slop
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"text\">foo bar</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:\"foo bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:\"foo\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:\"bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:\"bar foo\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND text:\"bar foo\"~2"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
comment|// intra-word delimiter testing (WordDelimiterFilter)
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">foo bar</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foo bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foo\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"bar foo\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"bar foo\"~2"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foo/bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foobar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">foo-bar</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foo bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foo\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"bar foo\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"bar foo\"~2"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"foo/bar\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:foobar"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">Canon PowerShot SD500 7MP</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"power-shot\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"power shot sd 500\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"powershot\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"SD-500\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"SD500\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"SD500-7MP\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"PowerShotSD500-7MP\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">Wi-Fi</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:wifi"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:wi+=fi"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:wi+=fi"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:WiFi"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"wi fi\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">'I.B.M' A's,B's,C's</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"'I.B.M.'\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:I.B.M"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:IBM"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:I--B--M"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"I B M\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:IBM's"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:IBM'sx"
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
comment|// this one fails since IBM and ABC are separated by two tokens
comment|// id:42 AND subword:IBM's-ABC's  %*[count(//doc)=1]
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"IBM's-ABC's\"~2"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"A's B's-C's\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">Sony KDF-E50A10</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
comment|// check for exact match:
comment|//  Sony KDF E/KDFE 50 A 10  (this is how it's indexed)
comment|//  Sony KDF E      50 A 10  (and how it's queried)
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"Sony KDF-E50A10\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:10"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:Sony"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
comment|// this one fails without slop since Sony and KDFE have a token inbetween
comment|// id:42 AND subword:SonyKDFE50A10  %*[count(//doc)=1]
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"SonyKDFE50A10\"~10"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"Sony KDF E-50-A-10\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">http://www.yahoo.com</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:yahoo"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:www.yahoo.com"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:http\\://www.yahoo.com"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">--Q 1-- W2 E-3 Ok xY 4R 5-T *6-Y- 7-8-- 10A-B</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:Q"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:1"
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"w 2\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"e 3\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"o k\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=0]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"ok\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"x y\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"xy\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"4 r\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"5 t\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"5 t\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"6 y\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"7 8\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"78\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:42 AND subword:\"10 A+B\""
argument_list|)
argument_list|,
literal|"*[count(//doc)=1]"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">FooBarBaz</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">FooBar10</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">10FooBar</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">BAZ</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">10</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">42</field><field name=\"subword\">Mark, I found what's the problem! It turns to be from the latest schema. I found tons of exceptions in the resin.stdout that prevented the builder from performing. It's all coming from the WordDelimiterFilter which was just added to the latest schema: [2005-08-29 15:11:38.375] java.lang.IndexOutOfBoundsException: Index: 3, Size: 3 673804 [2005-08-29 15:11:38.375]  at java.util.ArrayList.RangeCheck(ArrayList.java:547) 673805 [2005-08-29 15:11:38.375]  at java.util.ArrayList.get(ArrayList.java:322) 673806 [2005-08-29 15:11:38.375]  at solr.analysis.WordDelimiterFilter.addCombos(WordDelimiterFilter.java:349) 673807 [2005-08-29 15:11:38.375]  at solr.analysis.WordDelimiterFilter.next(WordDelimiterFilter.java:325) 673808 [2005-08-29 15:11:38.375]  at org.apache.lucene.analysis.LowerCaseFilter.next(LowerCaseFilter.java:32) 673809 [2005-08-29 15:11:38.375]  at org.apache.lucene.analysis.StopFilter.next(StopFilter.java:98) 673810 [2005-08-29 15:11:38.375]  at solr.EnglishPorterFilter.next(TokenizerFactory.java:163) 673811 [2005-08-29 15:11:38.375]  at org.apache.lucene.index.DocumentWriter.invertDocument(DocumentWriter.java:143) 673812 [2005-08-29 15:11:38.375]  at org.apache.lucene.index.DocumentWriter.addDocument(DocumentWriter.java:81) 673813 [2005-08-29 15:11:38.375]  at org.apache.lucene.index.IndexWriter.addDocument(IndexWriter.java:307) 673814 [2005-08-29 15:11:38.375]  at org.apache.lucene.index.IndexWriter.addDocument(IndexWriter.java:294) 673815 [2005-08-29 15:11:38.375]  at solr.DirectUpdateHandler2.doAdd(DirectUpdateHandler2.java:170) 673816 [2005-08-29 15:11:38.375]  at solr.DirectUpdateHandler2.overwriteBoth(DirectUpdateHandler2.java:317) 673817 [2005-08-29 15:11:38.375]  at solr.DirectUpdateHandler2.addDoc(DirectUpdateHandler2.java:191) 673818 [2005-08-29 15:11:38.375]  at solr.SolrCore.update(SolrCore.java:795) 673819 [2005-08-29 15:11:38.375]  at solrserver.SolrServlet.doPost(SolrServlet.java:71) 673820 [2005-08-29 15:11:38.375]  at javax.servlet.http.HttpServlet.service(HttpServlet.java:154) 673821 [2005-08-29 15:11:38.375]  at javax.servlet.http.HttpServlet.service(HttpServlet.java:92) 673822 [2005-08-29 15:11:38.375]  at com.caucho.server.dispatch.ServletFilterChain.doFilter(ServletFilterChain.java:99) 673823 [2005-08-29 15:11:38.375]  at com.caucho.server.cache.CacheFilterChain.doFilter(CacheFilterChain.java:188) 673824 [2005-08-29 15:11:38.375]  at com.caucho.server.webapp.WebAppFilterChain.doFilter(WebAppFilterChain.java:163) 673825 [2005-08-29 15:11:38.375]  at com.caucho.server.dispatch.ServletInvocation.service(ServletInvocation.java:208) 673826 [2005-08-29 15:11:38.375]  at com.caucho.server.http.HttpRequest.handleRequest(HttpRequest.java:259) 673827 [2005-08-29 15:11:38.375]  at com.caucho.server.port.TcpConnection.run(TcpConnection.java:363) 673828 [2005-08-29 15:11:38.375]  at com.caucho.util.ThreadPool.runTasks(ThreadPool.java:490) 673829 [2005-08-29 15:11:38.375]  at com.caucho.util.ThreadPool.run(ThreadPool.java:423) 673830 [2005-08-29 15:11:38.375]  at java.lang.Thread.run(Thread.java:595) With the previous schema I'm able to perform a successful full build: http://c12-ssa-dev40-so-mas1.cnet.com:5078/select/?stylesheet=q=docTypeversion=2.0start=0rows=10indent=on Do you want to rollback to the previous schema version</field></doc></add>"
argument_list|)
expr_stmt|;
comment|//
name|assertU
argument_list|(
literal|"<delete fromPending=\"true\" fromCommitted=\"true\"><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"fname_s\">Yonik</field><field name=\"here_b\">true</field><field name=\"iq_l\">10000000000</field><field name=\"description_t\">software engineer</field><field name=\"ego_d\">1e100</field><field name=\"pi_f\">3.1415962</field><field name=\"when_dt\">2005-03-18T01:14:34Z</field><field name=\"arr_f\">1.414213562</field><field name=\"arr_f\">.999</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"fname_s,arr_f  "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//str[.='Yonik']  "
argument_list|,
literal|"//float[.='1.4142135']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"  "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//str[.='Yonik']  "
argument_list|,
literal|"//float[.='1.4142135']"
argument_list|)
expr_stmt|;
comment|// test addition of score field
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"score "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//str[.='Yonik']  "
argument_list|,
literal|"//float[.='1.4142135'] "
argument_list|,
literal|"//float[@name='score'] "
argument_list|,
literal|"*[count(//doc/*)=13]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//str[.='Yonik']  "
argument_list|,
literal|"//float[.='1.4142135'] "
argument_list|,
literal|"//float[@name='score'] "
argument_list|,
literal|"*[count(//doc/*)=13]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"* "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//str[.='Yonik']  "
argument_list|,
literal|"//float[.='1.4142135'] "
argument_list|,
literal|"*[count(//doc/*)>=12]"
argument_list|)
expr_stmt|;
comment|// test maxScore
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"score "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//result[@maxScore>0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"score "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44;id desc;"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//result[@maxScore>0]"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"score "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44;"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//@maxScore = //doc/float[@name='score']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"score "
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44;id desc;"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//@maxScore = //doc/float[@name='score']"
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"fl"
argument_list|,
literal|"score"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"id:44;id desc;"
argument_list|,
literal|"standard"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|,
literal|"//result[@maxScore>0]"
argument_list|)
expr_stmt|;
comment|//  test schema field attribute inheritance and overriding
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"shouldbestored\">hi</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//*[@name='shouldbestored']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +shouldbestored:hi"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"shouldbeunstored\">hi</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"not(//*[@name='shouldbeunstored'])"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +shouldbeunstored:hi"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"shouldbeunindexed\">hi</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:44"
argument_list|)
argument_list|,
literal|"//*[@name='shouldbeunindexed']"
argument_list|)
expr_stmt|;
comment|//  this should result in an error... how to check for that?
comment|// +id:44 +shouldbeunindexed:hi %//*[@numFound="0"]
comment|// test spaces between XML elements because that can introduce extra XML events that
comment|// can mess up parsing (and it has in the past)
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"shouldbestored\">hi</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit />"
argument_list|)
expr_stmt|;
comment|// test adding multiple docs per add command
comment|// assertU("<delete><query>id:[0 TO 99]</query></delete>");
comment|// assertU("<add><doc><field name=\"id\">1</field></doc><doc><field name=\"id\">2</field></doc></add>");
comment|// assertU("<commit/>");
comment|// assertQ(req("id:[0 TO 99]")
comment|// ,"//*[@numFound='2']"
comment|// );
comment|// test synonym filter
name|assertU
argument_list|(
literal|"<delete><query>id:[10 TO 100]</query></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">10</field><field name=\"syn\">a</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">11</field><field name=\"syn\">b</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">12</field><field name=\"syn\">c</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">13</field><field name=\"syn\">foo</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:10 AND syn:a"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:10 AND syn:aa"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:11 AND syn:b"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:11 AND syn:b1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:11 AND syn:b2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:12 AND syn:c"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:12 AND syn:c1"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:12 AND syn:c2"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:13 AND syn:foo"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:13 AND syn:bar"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:13 AND syn:baz"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// test position increment gaps between field values
name|assertU
argument_list|(
literal|"<delete><id>44</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<delete><id>45</id></delete>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">44</field><field name=\"textgap\">aa bb cc</field><field name=\"textgap\">dd ee ff</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">45</field><field name=\"text\">aa bb cc</field><field name=\"text\">dd ee ff</field></doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +textgap:\"aa bb cc\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +textgap:\"dd ee ff\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +textgap:\"cc dd\""
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +textgap:\"cc dd\"~100"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +textgap:\"bb cc dd ee\"~90"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:44 +textgap:\"bb cc dd ee\"~100"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"+id:45 +text:\"cc dd\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// trigger output of custom value test
name|args
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"version"
argument_list|,
literal|"2.0"
argument_list|)
expr_stmt|;
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"values"
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
name|args
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

