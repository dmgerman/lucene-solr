begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.rest.schema
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
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|restlet
operator|.
name|ext
operator|.
name|servlet
operator|.
name|ServerServlet
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
DECL|class|TestManagedSchemaFieldResource
specifier|public
class|class
name|TestManagedSchemaFieldResource
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
name|TestManagedSchemaFieldResource
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
argument_list|<
name|ServletHolder
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ServletHolder
name|solrRestApi
init|=
operator|new
name|ServletHolder
argument_list|(
literal|"SolrRestApi"
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
literal|"org.apache.solr.rest.SolrRestApi"
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
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|jetty
operator|!=
literal|null
condition|)
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
block|}
name|server
operator|=
literal|null
expr_stmt|;
name|restTestHarness
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddFieldBadFieldType
specifier|public
name|void
name|testAddFieldBadFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield"
argument_list|,
name|json
argument_list|(
literal|"{'type':'not_in_there_at_all','stored':false}"
argument_list|)
argument_list|,
literal|"/error/msg==\"Field \\'newfield\\': Field type \\'not_in_there_at_all\\' not found.\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddFieldMismatchedName
specifier|public
name|void
name|testAddFieldMismatchedName
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield"
argument_list|,
name|json
argument_list|(
literal|"{'name':'something_else','type':'text','stored':false}"
argument_list|)
argument_list|,
literal|"/error/msg=='///regex:newfield///'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddFieldBadProperty
specifier|public
name|void
name|testAddFieldBadProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','no_property_with_this_name':false}"
argument_list|)
argument_list|,
literal|"/error/msg==\"java.lang.IllegalArgumentException: Invalid field property: no_property_with_this_name\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddField
specifier|public
name|void
name|testAddField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/newfield?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','stored':false}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/newfield?indent=on&wt=xml"
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
literal|"newfield"
argument_list|,
literal|"value1 value2"
argument_list|,
literal|"id"
argument_list|,
literal|"123"
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
literal|"/select?q=newfield:value1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"count(/response/result[@name='response']/doc/*) = 1"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='123']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddFieldWithMulipleOptions
specifier|public
name|void
name|testAddFieldWithMulipleOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/newfield?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text_en','stored':true,'indexed':false}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|File
name|managedSchemaFile
init|=
operator|new
name|File
argument_list|(
name|tmpConfDir
argument_list|,
literal|"managed-schema"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|managedSchemaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|managedSchemaContents
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|managedSchemaFile
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Pattern
name|newfieldStoredTrueIndexedFalsePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<field name=\"newfield\" type=\"text_en\" "
operator|+
literal|"(?=.*stored=\"true\")(?=.*indexed=\"false\").*/>"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newfieldStoredTrueIndexedFalsePattern
operator|.
name|matcher
argument_list|(
name|managedSchemaContents
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/newfield?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='name'] = 'newfield'"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='type'] = 'text_en'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='indexed'] = 'false'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='stored'] = 'true'"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"newfield"
argument_list|,
literal|"value1 value2"
argument_list|,
literal|"id"
argument_list|,
literal|"1234"
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
literal|"/schema/fields/newfield2?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
literal|"/schema/fields/newfield2"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text_en','stored':true,'indexed':true,'multiValued':true}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|managedSchemaContents
operator|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|managedSchemaFile
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|Pattern
name|newfield2StoredTrueIndexedTrueMultiValuedTruePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<field name=\"newfield2\" type=\"text_en\" "
operator|+
literal|"(?=.*stored=\"true\")(?=.*indexed=\"true\")(?=.*multiValued=\"true\").*/>"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newfield2StoredTrueIndexedTrueMultiValuedTruePattern
operator|.
name|matcher
argument_list|(
name|managedSchemaContents
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/newfield2?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='name'] = 'newfield2'"
argument_list|,
literal|"/response/lst[@name='field']/str[@name='type'] = 'text_en'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='indexed'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='stored'] = 'true'"
argument_list|,
literal|"/response/lst[@name='field']/bool[@name='multiValued'] = 'true'"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"newfield2"
argument_list|,
literal|"value1 value2"
argument_list|,
literal|"newfield2"
argument_list|,
literal|"value3 value4"
argument_list|,
literal|"id"
argument_list|,
literal|"5678"
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
literal|"/select?q=newfield2:value3"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"count(/response/result[@name='response']/doc) = 1"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='5678']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddFieldCollectionWithMultipleOptions
specifier|public
name|void
name|testAddFieldCollectionWithMultipleOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml"
argument_list|,
literal|"count(/response/arr[@name='fields']/lst/str[@name])> 0"
argument_list|,
comment|// there are fields
literal|"count(/response/arr[@name='fields']/lst/str[starts-with(@name,'newfield')]) = 0"
argument_list|)
expr_stmt|;
comment|// but none named newfield*
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'newfield','type':'text_en','stored':true,'indexed':false}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|File
name|managedSchemaFile
init|=
operator|new
name|File
argument_list|(
name|tmpConfDir
argument_list|,
literal|"managed-schema"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|managedSchemaFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|managedSchemaContents
init|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|managedSchemaFile
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Pattern
name|newfieldStoredTrueIndexedFalsePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<field name=\"newfield\" type=\"text_en\" "
operator|+
literal|"(?=.*stored=\"true\")(?=.*indexed=\"false\").*/>"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newfieldStoredTrueIndexedFalsePattern
operator|.
name|matcher
argument_list|(
name|managedSchemaContents
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml"
argument_list|,
literal|"/response/arr[@name='fields']/lst"
operator|+
literal|"[str[@name='name']='newfield' and str[@name='type']='text_en'"
operator|+
literal|" and bool[@name='stored']='true' and bool[@name='indexed']='false']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"newfield"
argument_list|,
literal|"value1 value2"
argument_list|,
literal|"id"
argument_list|,
literal|"789"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'newfield2','type':'text_en','stored':true,'indexed':true,'multiValued':true}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|managedSchemaContents
operator|=
name|FileUtils
operator|.
name|readFileToString
argument_list|(
name|managedSchemaFile
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|Pattern
name|newfield2StoredTrueIndexedTrueMultiValuedTruePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"<field name=\"newfield2\" type=\"text_en\" "
operator|+
literal|"(?=.*stored=\"true\")(?=.*indexed=\"true\")(?=.*multiValued=\"true\").*/>"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|newfield2StoredTrueIndexedTrueMultiValuedTruePattern
operator|.
name|matcher
argument_list|(
name|managedSchemaContents
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields?indent=on&wt=xml"
argument_list|,
literal|"/response/arr[@name='fields']/lst"
operator|+
literal|"[str[@name='name']='newfield2' and str[@name='type']='text_en'"
operator|+
literal|" and bool[@name='stored']='true' and bool[@name='indexed']='true' and bool[@name='multiValued']='true']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"newfield2"
argument_list|,
literal|"value1 value2"
argument_list|,
literal|"newfield2"
argument_list|,
literal|"value3 value4"
argument_list|,
literal|"id"
argument_list|,
literal|"790"
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
literal|"/select?q=newfield2:value3"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"count(/response/result[@name='response']/doc) = 1"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='790']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAddCopyField
specifier|public
name|void
name|testAddCopyField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/newfield2?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
literal|"/schema/fields/fieldA"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','stored':false}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
literal|"/schema/fields/fieldB"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','stored':false, 'copyFields':['fieldA']}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJPut
argument_list|(
literal|"/schema/fields/fieldC"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','stored':false, 'copyFields':'fieldA'}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/fieldB?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=fieldB"
argument_list|,
literal|"count(/response/arr[@name='copyFields']/lst) = 1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=fieldC"
argument_list|,
literal|"count(/response/arr[@name='copyFields']/lst) = 1"
argument_list|)
expr_stmt|;
comment|//fine to pass in empty list, just won't do anything
name|assertJPut
argument_list|(
literal|"/schema/fields/fieldD"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','stored':false, 'copyFields':[]}"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|//some bad usages
name|assertJPut
argument_list|(
literal|"/schema/fields/fieldF"
argument_list|,
name|json
argument_list|(
literal|"{'type':'text','stored':false, 'copyFields':['some_nonexistent_field_ignore_exception']}"
argument_list|)
argument_list|,
literal|"/error/msg==\"copyField dest :\\'some_nonexistent_field_ignore_exception\\' is not an explicit field and doesn\\'t match a dynamicField.\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPostMultipleFields
specifier|public
name|void
name|testPostMultipleFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"/schema/fields/newfield1?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/newfield2?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 0"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '404'"
argument_list|,
literal|"/response/lst[@name='error']/int[@name='code'] = '404'"
argument_list|)
expr_stmt|;
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'newfield1','type':'text','stored':false},"
operator|+
literal|" {'name':'newfield2','type':'text','stored':false}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/newfield1?indent=on&wt=xml"
argument_list|,
literal|"count(/response/lst[@name='field']) = 1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/fields/newfield2?indent=on&wt=xml"
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
literal|"newfield1"
argument_list|,
literal|"value1 value2"
argument_list|,
literal|"id"
argument_list|,
literal|"123"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"newfield2"
argument_list|,
literal|"value3 value4"
argument_list|,
literal|"id"
argument_list|,
literal|"456"
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
literal|"/select?q=newfield1:value1"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"count(/response/result[@name='response']/doc/*) = 1"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='123']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/select?q=newfield2:value3"
argument_list|,
literal|"/response/lst[@name='responseHeader']/int[@name='status'] = '0'"
argument_list|,
literal|"/response/result[@name='response'][@numFound='1']"
argument_list|,
literal|"count(/response/result[@name='response']/doc/*) = 1"
argument_list|,
literal|"/response/result[@name='response']/doc/str[@name='id'][.='456']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPostCopy
specifier|public
name|void
name|testPostCopy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'fieldA','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldB','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldC','type':'text','stored':false, 'copyFields':['fieldB']}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=fieldC"
argument_list|,
literal|"count(/response/arr[@name='copyFields']/lst) = 1"
argument_list|)
expr_stmt|;
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'fieldD','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldE','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldF','type':'text','stored':false, 'copyFields':['fieldD','fieldE']},"
operator|+
literal|" {'name':'fieldG','type':'text','stored':false, 'copyFields':'fieldD'}]"
argument_list|)
argument_list|,
comment|//single
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=fieldF"
argument_list|,
literal|"count(/response/arr[@name='copyFields']/lst) = 2"
argument_list|)
expr_stmt|;
comment|//passing in an empty list is perfectly acceptable, it just won't do anything
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'fieldX','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldY','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldZ','type':'text','stored':false, 'copyFields':[]}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
comment|//some bad usages
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'fieldH','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldI','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldJ','type':'text','stored':false, 'copyFields':['some_nonexistent_field_ignore_exception']}]"
argument_list|)
argument_list|,
literal|"/error/msg=='copyField dest :\\'some_nonexistent_field_ignore_exception\\' is not an explicit field and doesn\\'t match a dynamicField.'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPostCopyFields
specifier|public
name|void
name|testPostCopyFields
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJPost
argument_list|(
literal|"/schema/fields"
argument_list|,
name|json
argument_list|(
literal|"[{'name':'fieldA','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldB','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldC','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldD','type':'text','stored':false},"
operator|+
literal|" {'name':'fieldE','type':'text','stored':false}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertJPost
argument_list|(
literal|"/schema/copyfields"
argument_list|,
name|json
argument_list|(
literal|"[{'source':'fieldA', 'dest':'fieldB'},"
operator|+
literal|" {'source':'fieldD', 'dest':['fieldC', 'fieldE']}]"
argument_list|)
argument_list|,
literal|"/responseHeader/status==0"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=fieldA"
argument_list|,
literal|"count(/response/arr[@name='copyFields']/lst) = 1"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"/schema/copyfields/?indent=on&wt=xml&source.fl=fieldD"
argument_list|,
literal|"count(/response/arr[@name='copyFields']/lst) = 2"
argument_list|)
expr_stmt|;
name|assertJPost
argument_list|(
literal|"/schema/copyfields"
argument_list|,
name|json
argument_list|(
literal|"[{'source':'some_nonexistent_field_ignore_exception', 'dest':['fieldA']}]"
argument_list|)
argument_list|,
literal|"/error/msg=='copyField source :\\'some_nonexistent_field_ignore_exception\\' is not a glob and doesn\\'t match any explicit field or dynamicField.'"
argument_list|)
expr_stmt|;
name|assertJPost
argument_list|(
literal|"/schema/copyfields"
argument_list|,
name|json
argument_list|(
literal|"[{'source':'fieldD', 'dest':['some_nonexistent_field_ignore_exception']}]"
argument_list|)
argument_list|,
literal|"/error/msg=='copyField dest :\\'some_nonexistent_field_ignore_exception\\' is not an explicit field and doesn\\'t match a dynamicField.'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

