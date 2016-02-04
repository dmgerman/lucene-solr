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
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|CommandOperation
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
name|io
operator|.
name|IOException
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
name|List
import|;
end_import

begin_class
DECL|class|TestSchemaManager
specifier|public
class|class
name|TestSchemaManager
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
literal|"schema-tiny.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParsing
specifier|public
name|void
name|testParsing
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|x
init|=
literal|"{\n"
operator|+
literal|" 'add-field' : {\n"
operator|+
literal|"              'name':'a',\n"
operator|+
literal|"              'type': 'string',\n"
operator|+
literal|"              'stored':true,\n"
operator|+
literal|"              'indexed':false\n"
operator|+
literal|"              },\n"
operator|+
literal|" 'add-field' : {\n"
operator|+
literal|"              'name':'b',\n"
operator|+
literal|"              'type': 'string',\n"
operator|+
literal|"              'stored':true,\n"
operator|+
literal|"              'indexed':false\n"
operator|+
literal|"              }\n"
operator|+
literal|"\n"
operator|+
literal|"}"
decl_stmt|;
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|ops
init|=
name|CommandOperation
operator|.
name|parse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|(
name|x
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|ops
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|x
operator|=
literal|" {'add-field' : [{\n"
operator|+
literal|"                       'name':'a1',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':false\n"
operator|+
literal|"                      },\n"
operator|+
literal|"                      {\n"
operator|+
literal|"                       'name':'a2',\n"
operator|+
literal|"                       'type': 'string',\n"
operator|+
literal|"                       'stored':true,\n"
operator|+
literal|"                       'indexed':true\n"
operator|+
literal|"                      }]\n"
operator|+
literal|"      }"
expr_stmt|;
name|ops
operator|=
name|CommandOperation
operator|.
name|parse
argument_list|(
operator|new
name|StringReader
argument_list|(
name|json
argument_list|(
name|x
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ops
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|CommandOperation
operator|.
name|captureErrors
argument_list|(
name|ops
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

