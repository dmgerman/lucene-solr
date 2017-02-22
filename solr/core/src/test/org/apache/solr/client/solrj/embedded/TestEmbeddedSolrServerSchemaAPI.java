begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
package|;
end_package

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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|schema
operator|.
name|SchemaRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|schema
operator|.
name|SchemaResponse
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|schema
operator|.
name|SchemaResponse
operator|.
name|FieldResponse
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
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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

begin_class
DECL|class|TestEmbeddedSolrServerSchemaAPI
specifier|public
class|class
name|TestEmbeddedSolrServerSchemaAPI
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
init|=
literal|"VerificationTest"
decl_stmt|;
DECL|field|server
specifier|private
specifier|static
name|EmbeddedSolrServer
name|server
decl_stmt|;
DECL|field|fieldAttributes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|fieldAttributes
decl_stmt|;
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|field
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|field
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|field
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|field
operator|.
name|put
argument_list|(
literal|"stored"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|field
operator|.
name|put
argument_list|(
literal|"indexed"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|put
argument_list|(
literal|"multiValued"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fieldAttributes
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
DECL|method|initClass
specifier|public
specifier|static
name|void
name|initClass
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNull
argument_list|(
literal|"no system props clash please"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|,
literal|""
operator|+
comment|//true
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|tmpHome
init|=
name|createTempDir
argument_list|(
literal|"tmp-home"
argument_list|)
decl_stmt|;
name|Path
name|coreDir
init|=
name|tmpHome
operator|.
name|resolve
argument_list|(
name|DEFAULT_TEST_CORENAME
argument_list|)
decl_stmt|;
name|copyMinConf
argument_list|(
name|coreDir
operator|.
name|toFile
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|"solrconfig-managed-schema.xml"
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
comment|/*it's renamed to to*/
argument_list|,
literal|"schema.xml"
argument_list|,
name|tmpHome
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|h
operator|.
name|getCoreContainer
argument_list|()
argument_list|,
name|DEFAULT_TEST_CORENAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|destroyClass
specifier|public
specifier|static
name|void
name|destroyClass
parameter_list|()
throws|throws
name|IOException
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// doubtful
name|server
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|thereIsNoFieldYet
specifier|public
name|void
name|thereIsNoFieldYet
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
try|try
block|{
name|FieldResponse
name|process
init|=
operator|new
name|SchemaRequest
operator|.
name|Field
argument_list|(
name|fieldName
argument_list|)
operator|.
name|process
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|""
operator|+
name|process
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"No"
argument_list|)
operator|&&
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"VerificationTest"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSchemaAddFieldAndVerifyExistence
specifier|public
name|void
name|testSchemaAddFieldAndVerifyExistence
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
literal|"it needs to ammend schema"
argument_list|,
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
argument_list|)
expr_stmt|;
name|SchemaResponse
operator|.
name|UpdateResponse
name|addFieldResponse
init|=
operator|new
name|SchemaRequest
operator|.
name|AddField
argument_list|(
name|fieldAttributes
argument_list|)
operator|.
name|process
argument_list|(
name|server
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|addFieldResponse
operator|.
name|toString
argument_list|()
argument_list|,
literal|0
argument_list|,
name|addFieldResponse
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
comment|// This asserts that the field was actually created
comment|// this is due to the fact that the response gave OK but actually never created the field.
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|foundFieldAttributes
init|=
operator|new
name|SchemaRequest
operator|.
name|Field
argument_list|(
name|fieldName
argument_list|)
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getField
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|fieldAttributes
argument_list|,
name|foundFieldAttributes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"removing "
operator|+
name|fieldName
argument_list|,
literal|0
argument_list|,
operator|new
name|SchemaRequest
operator|.
name|DeleteField
argument_list|(
name|fieldName
argument_list|)
operator|.
name|process
argument_list|(
name|server
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSchemaAddFieldAndFailOnImmutable
specifier|public
name|void
name|testSchemaAddFieldAndFailOnImmutable
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"it needs a readonly schema"
argument_list|,
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"managed.schema.mutable"
argument_list|)
argument_list|)
expr_stmt|;
name|SchemaRequest
operator|.
name|AddField
name|addFieldUpdateSchemaRequest
init|=
operator|new
name|SchemaRequest
operator|.
name|AddField
argument_list|(
name|fieldAttributes
argument_list|)
decl_stmt|;
name|SchemaResponse
operator|.
name|UpdateResponse
name|addFieldResponse
init|=
name|addFieldUpdateSchemaRequest
operator|.
name|process
argument_list|(
name|server
argument_list|)
decl_stmt|;
comment|// wt hell???? assertFalse(addFieldResponse.toString(), addFieldResponse.getStatus()==0);
name|assertTrue
argument_list|(
operator|(
literal|""
operator|+
name|addFieldResponse
operator|)
operator|.
name|contains
argument_list|(
literal|"schema is not editable"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

