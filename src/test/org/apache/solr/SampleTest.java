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
comment|/**  * This is an example of how to write a JUnit tests for Solr using the  * AbstractSolrTestCase  */
end_comment

begin_class
DECL|class|SampleTest
specifier|public
class|class
name|SampleTest
extends|extends
name|AbstractSolrTestCase
block|{
comment|/**    * All subclasses of AbstractSolrTestCase must define this method.    *    *<p>    * Note that different tests can use different schemas by refering    * to any crazy path they want (as long as it works).    *</p>    */
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"solr/crazy-path-to-schema.xml"
return|;
block|}
comment|/**    * All subclasses of AbstractSolrTestCase must define this method    *    *<p>    * Note that different tests can use different configs by refering    * to any crazy path they want (as long as it works).    *</p>    */
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solr/crazy-path-to-config.xml"
return|;
block|}
comment|/**    * Demonstration of some of the simple ways to use the base class    */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
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
name|assertU
argument_list|(
literal|"Simple assertion that adding a document works"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4055"
argument_list|,
literal|"subject"
argument_list|,
literal|"Hoss the Hoss man Hostetter"
argument_list|)
argument_list|)
expr_stmt|;
comment|/* alternate syntax, no label */
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4056"
argument_list|,
literal|"subject"
argument_list|,
literal|"Some Other Guy"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"couldn't find subject hoss"
argument_list|,
name|req
argument_list|(
literal|"subject:Hoss"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|,
literal|"//int[@name='id'][.='4055']"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Demonstration of some of the more complex ways to use the base class    */
DECL|method|testAdvanced
specifier|public
name|void
name|testAdvanced
parameter_list|()
throws|throws
name|Exception
block|{
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
name|assertU
argument_list|(
literal|"less common case, a complex addition with options"
argument_list|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4059"
argument_list|,
literal|"subject"
argument_list|,
literal|"Who Me?"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"or just make the raw XML yourself"
argument_list|,
literal|"<add allowDups=\"true\">"
operator|+
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"4059"
argument_list|,
literal|"subject"
argument_list|,
literal|"Who Me Again?"
argument_list|)
operator|+
literal|"</add>"
argument_list|)
expr_stmt|;
comment|/* or really make the xml yourself */
name|assertU
argument_list|(
literal|"<add><doc><field name=\"id\">4055</field>"
operator|+
literal|"<field name=\"subject\">Hoss the Hoss man Hostetter</field>"
operator|+
literal|"</doc></add>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<optimize/>"
argument_list|)
expr_stmt|;
comment|/* access the default LocalRequestFactory directly to make a request */
name|SolrQueryRequest
name|req
init|=
name|lrf
operator|.
name|makeRequest
argument_list|(
literal|"subject:Hoss"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"couldn't find subject hoss"
argument_list|,
name|req
argument_list|,
literal|"//result[@numFound=1]"
argument_list|,
literal|"//int[@name='id'][.='4055']"
argument_list|)
expr_stmt|;
comment|/* make your own LocalRequestFactory to build a request      *      * Note: the qt proves we are using our custom config...      */
name|TestHarness
operator|.
name|LocalRequestFactory
name|l
init|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"crazy_custom_qt"
argument_list|,
literal|100
argument_list|,
literal|200
argument_list|,
literal|"version"
argument_list|,
literal|"2.1"
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
literal|"how did i find Mack Daddy? "
argument_list|,
name|l
operator|.
name|makeRequest
argument_list|(
literal|"Mack Daddy"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|/* you can access the harness directly as well*/
name|assertNull
argument_list|(
literal|"how did i find Mack Daddy? "
argument_list|,
name|h
operator|.
name|validateQuery
argument_list|(
name|l
operator|.
name|makeRequest
argument_list|(
literal|"Mack Daddy"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

