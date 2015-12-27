begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|rest
operator|.
name|schema
operator|.
name|TestBulkSchemaAPI
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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|RestTestHarness
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
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
import|;
end_import

begin_comment
comment|/**  * Tests the useDocValuesAsStored functionality.  */
end_comment

begin_class
DECL|class|TestUseDocValuesAsStored2
specifier|public
class|class
name|TestUseDocValuesAsStored2
extends|extends
name|RestTestBase
block|{
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
name|File
name|tmpSolrHome
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
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
literal|null
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
name|client
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|restTestHarness
operator|!=
literal|null
condition|)
block|{
name|restTestHarness
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|restTestHarness
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testSchemaAPI
specifier|public
name|void
name|testSchemaAPI
parameter_list|()
throws|throws
name|Exception
block|{
name|RestTestHarness
name|harness
init|=
name|restTestHarness
decl_stmt|;
name|String
name|payload
init|=
literal|"{\n"
operator|+
literal|"          'add-field' : {\n"
operator|+
literal|"                       'name':'a1',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':false,\n"
operator|+
literal|"                       'docValues':true,\n"
operator|+
literal|"                       'indexed':false\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-field' : {\n"
operator|+
literal|"                       'name':'a2',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':false,\n"
operator|+
literal|"                       'useDocValuesAsStored':true,\n"
operator|+
literal|"                       'docValues':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                       },\n"
operator|+
literal|"          'add-field' : {\n"
operator|+
literal|"                       'name':'a3',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':false,\n"
operator|+
literal|"                       'useDocValuesAsStored':false,\n"
operator|+
literal|"                       'docValues':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                       }\n"
operator|+
literal|"          }\n"
decl_stmt|;
name|String
name|response
init|=
name|harness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
decl_stmt|;
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|ObjectBuilder
operator|.
name|getVal
argument_list|(
operator|new
name|JSONParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|response
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|response
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"errors"
argument_list|)
argument_list|)
expr_stmt|;
comment|// default value of useDocValuesAsStored
name|m
operator|=
name|TestBulkSchemaAPI
operator|.
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a1"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a1 not created"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|"useDocValuesAsStored"
argument_list|)
argument_list|)
expr_stmt|;
comment|// useDocValuesAsStored=true
name|m
operator|=
name|TestBulkSchemaAPI
operator|.
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a2"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a2 not created"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"useDocValuesAsStored"
argument_list|)
argument_list|)
expr_stmt|;
comment|// useDocValuesAsStored=false
name|m
operator|=
name|TestBulkSchemaAPI
operator|.
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a3"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a3 not created"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"useDocValuesAsStored"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Index documents to check the effect
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid1"
argument_list|,
literal|"a1"
argument_list|,
literal|"1"
argument_list|,
literal|"a2"
argument_list|,
literal|"2"
argument_list|,
literal|"a3"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|RestTestBase
operator|.
name|assertJQ
argument_list|(
literal|"/select?q=id:myid*&fl=*"
argument_list|,
literal|"/response/docs==[{'id':'myid1', 'a1':'1', 'a2':'2'}]"
argument_list|)
expr_stmt|;
name|RestTestBase
operator|.
name|assertJQ
argument_list|(
literal|"/select?q=id:myid*&fl=id,a1,a2,a3"
argument_list|,
literal|"/response/docs==[{'id':'myid1', 'a1':'1', 'a2':'2', 'a3':'3'}]"
argument_list|)
expr_stmt|;
name|RestTestBase
operator|.
name|assertJQ
argument_list|(
literal|"/select?q=id:myid*&fl=a3"
argument_list|,
literal|"/response/docs==[{'a3':'3'}]"
argument_list|)
expr_stmt|;
comment|// this will return a3 because it is explicitly requested even if '*' is specified
name|RestTestBase
operator|.
name|assertJQ
argument_list|(
literal|"/select?q=id:myid*&fl=*,a3"
argument_list|,
literal|"/response/docs==[{'id':'myid1', 'a1':'1', 'a2':'2', 'a3':'3'}]"
argument_list|)
expr_stmt|;
comment|// this will not return a3 because the glob 'a*' will match only stored + useDocValuesAsStored=true fields
name|RestTestBase
operator|.
name|assertJQ
argument_list|(
literal|"/select?q=id:myid*&fl=id,a*"
argument_list|,
literal|"/response/docs==[{'id':'myid1', 'a1':'1', 'a2':'2'}]"
argument_list|)
expr_stmt|;
comment|// Test replace-field
comment|// Explicitly set useDocValuesAsStored to false
name|payload
operator|=
literal|"{\n"
operator|+
literal|"          'replace-field' : {\n"
operator|+
literal|"                       'name':'a1',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':false,\n"
operator|+
literal|"                       'useDocValuesAsStored':false,\n"
operator|+
literal|"                       'docValues':true,\n"
operator|+
literal|"                       'indexed':false\n"
operator|+
literal|"                       }}"
expr_stmt|;
name|response
operator|=
name|harness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|TestBulkSchemaAPI
operator|.
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a1"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a1 doesn't exist any more"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"useDocValuesAsStored"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Explicitly set useDocValuesAsStored to true
name|payload
operator|=
literal|"{\n"
operator|+
literal|"          'replace-field' : {\n"
operator|+
literal|"                       'name':'a1',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':false,\n"
operator|+
literal|"                       'useDocValuesAsStored':true,\n"
operator|+
literal|"                       'docValues':true,\n"
operator|+
literal|"                       'indexed':false\n"
operator|+
literal|"                       }}"
expr_stmt|;
name|response
operator|=
name|harness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|TestBulkSchemaAPI
operator|.
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a1"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a1 doesn't exist any more"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"useDocValuesAsStored"
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a field which is stored as well as docvalues
name|payload
operator|=
literal|"{          'add-field' : {\n"
operator|+
literal|"                       'name':'a4',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'useDocValuesAsStored':true,\n"
operator|+
literal|"                       'docValues':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                       }}"
expr_stmt|;
name|response
operator|=
name|harness
operator|.
name|post
argument_list|(
literal|"/schema?wt=json"
argument_list|,
name|json
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|=
name|TestBulkSchemaAPI
operator|.
name|getObj
argument_list|(
name|harness
argument_list|,
literal|"a4"
argument_list|,
literal|"fields"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"field a4 not found"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|,
name|m
operator|.
name|get
argument_list|(
literal|"useDocValuesAsStored"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"myid1"
argument_list|,
literal|"a1"
argument_list|,
literal|"1"
argument_list|,
literal|"a2"
argument_list|,
literal|"2"
argument_list|,
literal|"a3"
argument_list|,
literal|"3"
argument_list|,
literal|"a4"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|RestTestBase
operator|.
name|assertJQ
argument_list|(
literal|"/select?q=id:myid*&fl=*"
argument_list|,
literal|"/response/docs==[{'id':'myid1', 'a1':'1', 'a2':'2', 'a4':'4'}]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

