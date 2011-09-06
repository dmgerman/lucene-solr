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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|Set
import|;
end_import

begin_comment
comment|/**  * A test of basic features using the minial legal solr schema.  */
end_comment

begin_class
DECL|class|MinimalSchemaTest
specifier|public
class|class
name|MinimalSchemaTest
extends|extends
name|SolrTestCaseJ4
block|{
comment|/**    * NOTE: we explicitly use the general 'solrconfig.xml' file here, in     * an attempt to test as many broad features as possible.    *    * Do not change this to point at some other "simpler" solrconfig.xml     * just because you want to add a new test case using solrconfig.xml,     * but your new testcase adds a feature that breaks this test.    */
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
literal|"solr/conf/solrconfig.xml"
argument_list|,
literal|"solr/conf/schema-minimal.xml"
argument_list|)
expr_stmt|;
comment|/* make sure some misguided soul doesn't inadvertently give us         a uniqueKey field and defeat the point of the tests     */
name|assertNull
argument_list|(
literal|"UniqueKey Field isn't null"
argument_list|,
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getUniqueKeyField
argument_list|()
argument_list|)
expr_stmt|;
name|lrf
operator|.
name|args
operator|.
name|put
argument_list|(
name|CommonParams
operator|.
name|VERSION
argument_list|,
literal|"2.2"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Simple assertion that adding a document works"
argument_list|,
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4055"
argument_list|,
literal|"subject"
argument_list|,
literal|"Hoss"
argument_list|,
literal|"project"
argument_list|,
literal|"Solr"
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
literal|"4056"
argument_list|,
literal|"subject"
argument_list|,
literal|"Yonik"
argument_list|,
literal|"project"
argument_list|,
literal|"Solr"
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
name|optimize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleQueries
specifier|public
name|void
name|testSimpleQueries
parameter_list|()
block|{
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
literal|"//str[@name='id'][.='4055']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"couldn't find subject Yonik"
argument_list|,
name|req
argument_list|(
literal|"subject:Yonik"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|,
literal|"//str[@name='id'][.='4056']"
argument_list|)
expr_stmt|;
block|}
comment|/** SOLR-1371 */
annotation|@
name|Test
DECL|method|testLuke
specifier|public
name|void
name|testLuke
parameter_list|()
block|{
name|assertQ
argument_list|(
literal|"basic luke request failed"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|)
argument_list|,
literal|"//int[@name='numDocs'][.='2']"
argument_list|,
literal|"//int[@name='numTerms'][.='5']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"luke show schema failed"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/admin/luke"
argument_list|,
literal|"show"
argument_list|,
literal|"schema"
argument_list|)
argument_list|,
literal|"//int[@name='numDocs'][.='2']"
argument_list|,
literal|"//int[@name='numTerms'][.='5']"
argument_list|,
literal|"//null[@name='uniqueKeyField']"
argument_list|,
literal|"//null[@name='defaultSearchField']"
argument_list|)
expr_stmt|;
block|}
comment|/**     * Iterates over all (non "/update/*") handlers in the core and hits     * them with a request (using some simple params) to verify that they     * don't generate an error against the minimal schema    */
annotation|@
name|Test
DECL|method|testAllConfiguredHandlers
specifier|public
name|void
name|testAllConfiguredHandlers
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|handlerNames
init|=
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandlers
argument_list|()
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|handler
range|:
name|handlerNames
control|)
block|{
try|try
block|{
if|if
condition|(
name|handler
operator|.
name|startsWith
argument_list|(
literal|"/update"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|handler
operator|.
name|startsWith
argument_list|(
literal|"/mlt"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertQ
argument_list|(
literal|"failure w/handler: '"
operator|+
name|handler
operator|+
literal|"'"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
name|handler
argument_list|,
comment|// this should be fairly innocuous for any type of query
literal|"q"
argument_list|,
literal|"foo:bar"
argument_list|,
literal|"omitHeader"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"//lst[@name='responseHeader']"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"exception w/handler: '"
operator|+
name|handler
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

