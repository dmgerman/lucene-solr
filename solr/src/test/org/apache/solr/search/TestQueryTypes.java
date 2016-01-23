begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import

begin_class
DECL|class|TestQueryTypes
specifier|public
class|class
name|TestQueryTypes
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
literal|"schema11.xml"
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
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testQueryTypes
specifier|public
name|void
name|testQueryTypes
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"v_t"
argument_list|,
literal|"Hello Dude"
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
literal|"v_t"
argument_list|,
literal|"Hello Yonik"
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
literal|"v_s"
argument_list|,
literal|"{!literal}"
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
literal|"v_s"
argument_list|,
literal|"other stuff"
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
literal|"v_f"
argument_list|,
literal|"3.14159"
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
literal|"v_f"
argument_list|,
literal|"8983"
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
literal|"v_f"
argument_list|,
literal|"1.5"
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
literal|"v_ti"
argument_list|,
literal|"5"
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
literal|"v_s"
argument_list|,
literal|"internal\"quote"
argument_list|)
argument_list|)
expr_stmt|;
name|Object
index|[]
name|arr
init|=
operator|new
name|Object
index|[]
block|{
literal|"id"
block|,
literal|999.0
block|,
literal|"v_s"
block|,
literal|"wow dude"
block|,
literal|"v_t"
block|,
literal|"wow"
block|,
literal|"v_ti"
block|,
operator|-
literal|1
block|,
literal|"v_tis"
block|,
operator|-
literal|1
block|,
literal|"v_tl"
block|,
operator|-
literal|1234567891234567890L
block|,
literal|"v_tls"
block|,
operator|-
literal|1234567891234567890L
block|,
literal|"v_tf"
block|,
operator|-
literal|2.0f
block|,
literal|"v_tfs"
block|,
operator|-
literal|2.0f
block|,
literal|"v_td"
block|,
operator|-
literal|2.0
block|,
literal|"v_tds"
block|,
operator|-
literal|2.0
block|,
literal|"v_tdt"
block|,
literal|"2000-05-10T01:01:01Z"
block|,
literal|"v_tdts"
block|,
literal|"2002-08-26T01:01:01Z"
block|}
decl_stmt|;
name|String
index|[]
name|sarr
init|=
operator|new
name|String
index|[
name|arr
operator|.
name|length
index|]
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
name|arr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sarr
index|[
name|i
index|]
operator|=
name|arr
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sarr
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
comment|// test field queries
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arr
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|String
name|f
init|=
name|arr
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|v
init|=
name|arr
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// normal lucene fielded query
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|f
operator|+
literal|":\""
operator|+
name|v
operator|+
literal|'"'
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|,
literal|"//*[@name='id'][.='999.0']"
argument_list|,
literal|"//*[@name='"
operator|+
name|f
operator|+
literal|"'][.='"
operator|+
name|v
operator|+
literal|"']"
argument_list|)
expr_stmt|;
comment|// System.out.println("#########################################" + f + "=" + v);
comment|// field qparser
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|f
operator|+
literal|"}"
operator|+
name|v
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// lucene range
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|f
operator|+
literal|":[\""
operator|+
name|v
operator|+
literal|"\" TO \""
operator|+
name|v
operator|+
literal|"\"]"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// frange qparser
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!frange v="
operator|+
name|f
operator|+
literal|" l='"
operator|+
name|v
operator|+
literal|"' u='"
operator|+
name|v
operator|+
literal|"'}"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// function query... just make sure it doesn't throw an exception
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"+id:999 _val_:\""
operator|+
name|f
operator|+
literal|"\""
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
comment|// Some basic tests to ensure that parsing local params is working
name|assertQ
argument_list|(
literal|"test prefix query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix f=v_t}hel"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test raw query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_t}hello"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test raw query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_t}Hello"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test raw query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_f}1.5"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
comment|//
comment|// test escapes in quoted strings
comment|//
comment|// the control... unescaped queries looking for internal"quote
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_s}internal\"quote"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// test that single quoted string needs no escape
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_s v='internal\"quote'}"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// but it's OK if the escape is done
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_s v='internal\\\"quote'}"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// test unicode escape
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_s v=\"internal\\u0022quote\"}"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// inside a quoted string, internal"quote needs to be escaped
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!raw f=v_s v=\"internal\\\"quote\"}"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test custom plugin query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!foo f=v_t}hello"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test single term field query on text type"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f=v_t}HELLO"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test single term field query on type with diff internal rep"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f=v_f}1.5"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f=v_ti}5"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test multi term field query on text type"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f=v_t}Hello  DUDE"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test prefix query with value in local params"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix f=v_t v=hel}"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test optional quotes"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix f='v_t' v=\"hel\"}"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test extra whitespace"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix   f=v_t   v=hel   }"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test literal with {! in it"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix f=v_s}{!lit"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test param subst"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix f=$myf v=$my.v}"
argument_list|,
literal|"myf"
argument_list|,
literal|"v_t"
argument_list|,
literal|"my.v"
argument_list|,
literal|"hel"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test param subst with literal"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!prefix f=$myf v=$my.v}"
argument_list|,
literal|"myf"
argument_list|,
literal|"v_s"
argument_list|,
literal|"my.v"
argument_list|,
literal|"{!lit"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// lucene queries
name|assertQ
argument_list|(
literal|"test lucene query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!lucene}v_t:hel*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
comment|// lucene queries
name|assertQ
argument_list|(
literal|"test lucene default field"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!df=v_t}hel*"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
comment|// lucene operator
name|assertQ
argument_list|(
literal|"test lucene operator"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!q.op=OR df=v_t}Hello Yonik"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test lucene operator"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!q.op=AND df=v_t}Hello Yonik"
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
comment|// test boost queries
name|assertQ
argument_list|(
literal|"test boost"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!boost b=sum(v_f,1)}id:[5 TO 6]"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|,
literal|"//doc[./float[@name='v_f']='3.14159' and ./float[@name='score']='4.14159']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test boost and default type of func"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!boost v=$q1 b=$q2}"
argument_list|,
literal|"q1"
argument_list|,
literal|"{!func}v_f"
argument_list|,
literal|"q2"
argument_list|,
literal|"v_f"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|)
argument_list|,
literal|"//doc[./float[@name='v_f']='1.5' and ./float[@name='score']='2.25']"
argument_list|)
expr_stmt|;
comment|// dismax query from std request handler
name|assertQ
argument_list|(
literal|"test dismax query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!dismax}hello"
argument_list|,
literal|"qf"
argument_list|,
literal|"v_t"
argument_list|,
literal|"bf"
argument_list|,
literal|"sqrt(v_f)^100 log(sum(v_f,1))^50"
argument_list|,
literal|"bq"
argument_list|,
literal|"{!prefix f=v_t}he"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"on"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
comment|// dismax query from std request handler, using local params
name|assertQ
argument_list|(
literal|"test dismax query w/ local params"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!dismax qf=v_t}hello"
argument_list|,
literal|"qf"
argument_list|,
literal|"v_f"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test nested query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_query_:\"{!query v=$q1}\""
argument_list|,
literal|"q1"
argument_list|,
literal|"{!prefix f=v_t}hel"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"test nested nested query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"_query_:\"{!query defType=query v=$q1}\""
argument_list|,
literal|"q1"
argument_list|,
literal|"{!v=$q2}"
argument_list|,
literal|"q2"
argument_list|,
literal|"{!prefix f=v_t v=$qqq}"
argument_list|,
literal|"qqq"
argument_list|,
literal|"hel"
argument_list|)
argument_list|,
literal|"//result[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

