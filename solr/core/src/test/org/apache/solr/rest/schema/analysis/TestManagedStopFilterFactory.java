begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest.schema.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|rest
operator|.
name|schema
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|RestTestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|ServletHolder
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
name|Before
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
name|org
operator|.
name|noggit
operator|.
name|JSONUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import

begin_comment
comment|/**  * Test the REST API for managing stop words, which is pretty basic:  * GET: returns the list of stop words or a single word if it exists  * PUT: add some words to the current list  */
end_comment

begin_class
DECL|class|TestManagedStopFilterFactory
specifier|public
class|class
name|TestManagedStopFilterFactory
extends|extends
name|RestTestBase
block|{
DECL|field|tmpSolrHome
specifier|private
specifier|static
name|File
name|tmpSolrHome
decl_stmt|;
DECL|field|tmpConfDir
specifier|private
specifier|static
name|File
name|tmpConfDir
decl_stmt|;
DECL|field|collection
specifier|private
specifier|static
specifier|final
name|String
name|collection
init|=
literal|"collection1"
decl_stmt|;
DECL|field|confDir
specifier|private
specifier|static
specifier|final
name|String
name|confDir
init|=
name|collection
operator|+
literal|"/conf"
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|createTempDir
argument_list|()
expr_stmt|;
name|tmpSolrHome
operator|=
operator|new
name|File
argument_list|(
name|TEMP_DIR
operator|+
name|File
operator|.
name|separator
operator|+
name|TestManagedStopFilterFactory
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|tmpConfDir
operator|=
operator|new
name|File
argument_list|(
name|tmpSolrHome
argument_list|,
name|confDir
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|copyDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|)
argument_list|,
name|tmpSolrHome
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|SortedMap
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
name|extraServlets
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrSchemaRestApi"
argument_list|,
name|ServerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
name|solrRestApi
operator|.
name|setInitParameter
argument_list|(
literal|"org.restlet.application"
argument_list|,
literal|"org.apache.solr.rest.SolrSchemaRestApi"
argument_list|)
expr_stmt|;
name|extraServlets
operator|.
name|put
argument_list|(
name|solrRestApi
argument_list|,
literal|"/schema/*"
argument_list|)
expr_stmt|;
comment|// '/schema/*' matches '/schema', '/schema/', and '/schema/whatever...'
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|createJettyAndHarness
argument_list|(
name|tmpSolrHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"solrconfig-managed-schema.xml"
argument_list|,
literal|"schema-rest.xml"
argument_list|,
literal|"/solr"
argument_list|,
literal|true
argument_list|,
name|extraServlets
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|after
specifier|private
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|jetty
operator|.
name|stop
argument_list|()
expr_stmt|;
name|jetty
operator|=
literal|null
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tmpSolrHome
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"enable.update.log"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test adding managed stopwords to an endpoint defined in the schema,    * then adding docs containing a stopword before and after removing    * the stopword from the managed stopwords set.    */
annotation|@
name|Test
DECL|method|testManagedStopwords
specifier|public
name|void
name|testManagedStopwords
parameter_list|()
throws|throws
name|Exception
block|{
comment|// invalid endpoint
comment|//// TODO: This returns HTML vs JSON because the exception is thrown
comment|////       from the init method of ManagedEndpoint ... need a better solution
comment|// assertJQ("/schema/analysis/stopwords/bogus", "/error/code==404");
comment|// this endpoint depends on at least one field type containing the following
comment|// declaration in the schema-rest.xml:
comment|//
comment|//<filter class="solr.ManagedStopFilterFactory" managed="english" />
comment|//
name|String
name|endpoint
init|=
literal|"/schema/analysis/stopwords/english"
decl_stmt|;
comment|// test the initial GET request returns the default stopwords settings
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/wordSet/initArgs/ignoreCase==false"
argument_list|,
literal|"/wordSet/managedList==[]"
argument_list|)
expr_stmt|;
comment|// add some stopwords and verify they were added
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|JSONUtil
operator|.
name|toJSON
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"an"
argument_list|,
literal|"the"
argument_list|)
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// test requesting a specific stop word that exists / does not exist
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/the"
argument_list|,
literal|"/the=='the'"
argument_list|)
expr_stmt|;
comment|// not exist - 404
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/foo"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
comment|// wrong case - 404
name|assertJQ
argument_list|(
name|endpoint
operator|+
literal|"/An"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
comment|// update the ignoreCase initArg to true and make sure case is ignored
name|String
name|updateIgnoreCase
init|=
literal|"{ 'initArgs':{ 'ignoreCase':true }, "
operator|+
literal|"'managedList':['A','a','AN','an','THE','the','of','OF'] }"
decl_stmt|;
name|assertJPut
argument_list|(
name|endpoint
argument_list|,
name|json
argument_list|(
name|updateIgnoreCase
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/wordSet/initArgs/ignoreCase==true"
argument_list|,
literal|"/wordSet/managedList==['a','an','of','the']"
argument_list|)
expr_stmt|;
comment|// verify ignoreCase applies when requesting a word
name|assertJQ
argument_list|(
literal|"/schema/analysis/stopwords/english/The"
argument_list|,
literal|"/The=='the'"
argument_list|)
expr_stmt|;
comment|// verify the resource supports XML writer type (wt) as well as JSON
name|assertQ
argument_list|(
name|endpoint
argument_list|,
literal|"count(/response/lst[@name='wordSet']/arr[@name='managedList']/*) = 4"
argument_list|,
literal|"(/response/lst[@name='wordSet']/arr[@name='managedList']/str)[1] = 'a'"
argument_list|,
literal|"(/response/lst[@name='wordSet']/arr[@name='managedList']/str)[2] = 'an'"
argument_list|,
literal|"(/response/lst[@name='wordSet']/arr[@name='managedList']/str)[3] = 'of'"
argument_list|,
literal|"(/response/lst[@name='wordSet']/arr[@name='managedList']/str)[4] = 'the'"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
comment|// make the word set available
name|String
name|newFieldName
init|=
literal|"managed_en_field"
decl_stmt|;
comment|// make sure the new field doesn't already exist
name|assertQ
argument_list|(
literal|"/schema/fields/"
operator|+
name|newFieldName
operator|+
literal|"?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
comment|// add the new field
name|assertJPut
argument_list|(
literal|"/schema/fields/"
operator|+
name|newFieldName
argument_list|,
name|json
argument_list|(
literal|"{'type':'managed_en'}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// make sure the new field exists now
name|assertQ
argument_list|(
literal|"/schema/fields/"
operator|+
name|newFieldName
operator|+
literal|"?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
name|newFieldName
argument_list|,
literal|"This is the one"
argument_list|,
literal|"id"
argument_list|,
literal|"6"
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
literal|"/select?q="
operator|+
name|newFieldName
operator|+
literal|":This"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='6']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/select?q=%7B%21raw%20f="
operator|+
name|newFieldName
operator|+
literal|"%7Dthe"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='0']"
argument_list|)
expr_stmt|;
comment|// verify delete works
name|assertJDelete
argument_list|(
name|endpoint
operator|+
literal|"/the"
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|// verify that removing 'the' is not yet in effect
name|assertU
argument_list|(
name|adoc
argument_list|(
name|newFieldName
argument_list|,
literal|"This is the other one"
argument_list|,
literal|"id"
argument_list|,
literal|"7"
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
literal|"/select?q=%7B%21raw%20f="
operator|+
name|newFieldName
operator|+
literal|"%7Dthe"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='0']"
argument_list|)
expr_stmt|;
name|restTestHarness
operator|.
name|reload
argument_list|()
expr_stmt|;
comment|// verify that after reloading, removing 'the' has taken effect
name|assertU
argument_list|(
name|adoc
argument_list|(
name|newFieldName
argument_list|,
literal|"This is the other other one"
argument_list|,
literal|"id"
argument_list|,
literal|"8"
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
literal|"/select?q=%7B%21raw%20f="
operator|+
name|newFieldName
operator|+
literal|"%7Dthe"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='8']"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|endpoint
argument_list|,
literal|"/wordSet/initArgs/ignoreCase==true"
argument_list|,
literal|"/wordSet/managedList==['a','an','of']"
argument_list|)
expr_stmt|;
comment|// should fail with 404 as foo doesn't exist
name|assertJDelete
argument_list|(
name|endpoint
operator|+
literal|"/foo"
argument_list|,
literal|"/error/code==404"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

