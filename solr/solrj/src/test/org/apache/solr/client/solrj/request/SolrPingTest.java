begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.request
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
name|request
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|SolrJettyTestBase
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
name|SolrPingResponse
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Test SolrPing in Solrj  */
end_comment

begin_class
DECL|class|SolrPingTest
specifier|public
class|class
name|SolrPingTest
extends|extends
name|SolrJettyTestBase
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
name|File
name|testHome
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
name|getFile
argument_list|(
literal|"solrj/solr"
argument_list|)
argument_list|,
name|testHome
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
name|testHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|"collection1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
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
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"id"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|setField
argument_list|(
literal|"terms_s"
argument_list|,
literal|"samsung"
argument_list|)
expr_stmt|;
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnabledSolrPing
specifier|public
name|void
name|testEnabledSolrPing
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrPing
name|ping
init|=
operator|new
name|SolrPing
argument_list|()
decl_stmt|;
name|SolrPingResponse
name|rsp
init|=
literal|null
decl_stmt|;
name|ping
operator|.
name|setActionEnable
argument_list|()
expr_stmt|;
name|ping
operator|.
name|process
argument_list|(
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|ping
operator|.
name|removeAction
argument_list|()
expr_stmt|;
name|rsp
operator|=
name|ping
operator|.
name|process
argument_list|(
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testDisabledSolrPing
specifier|public
name|void
name|testDisabledSolrPing
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrPing
name|ping
init|=
operator|new
name|SolrPing
argument_list|()
decl_stmt|;
name|SolrPingResponse
name|rsp
init|=
literal|null
decl_stmt|;
name|ping
operator|.
name|setActionDisable
argument_list|()
expr_stmt|;
try|try
block|{
name|ping
operator|.
name|process
argument_list|(
name|getSolrClient
argument_list|()
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
name|Exception
argument_list|(
literal|"disable action failed!"
argument_list|)
throw|;
block|}
name|ping
operator|.
name|setActionPing
argument_list|()
expr_stmt|;
name|rsp
operator|=
name|ping
operator|.
name|process
argument_list|(
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
comment|// the above line should fail with a 503 SolrException.
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

