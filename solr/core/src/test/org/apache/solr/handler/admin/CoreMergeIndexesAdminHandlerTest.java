begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.handler.admin
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|LockFactory
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
name|common
operator|.
name|params
operator|.
name|CoreAdminParams
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
name|core
operator|.
name|CoreContainer
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
name|core
operator|.
name|DirectoryFactory
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
name|core
operator|.
name|MockFSDirectoryFactory
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
name|core
operator|.
name|SolrCore
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
name|response
operator|.
name|SolrQueryResponse
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import

begin_class
DECL|class|CoreMergeIndexesAdminHandlerTest
specifier|public
class|class
name|CoreMergeIndexesAdminHandlerTest
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
name|useFactory
argument_list|(
name|FailingDirectoryFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|FAILING_MSG
specifier|private
specifier|static
name|String
name|FAILING_MSG
init|=
literal|"Creating a directory using FailingDirectoryFactoryException always fails"
decl_stmt|;
DECL|class|FailingDirectoryFactory
specifier|public
specifier|static
class|class
name|FailingDirectoryFactory
extends|extends
name|MockFSDirectoryFactory
block|{
DECL|class|FailingDirectoryFactoryException
specifier|public
specifier|static
class|class
name|FailingDirectoryFactoryException
extends|extends
name|RuntimeException
block|{
DECL|method|FailingDirectoryFactoryException
specifier|public
name|FailingDirectoryFactoryException
parameter_list|()
block|{
name|super
argument_list|(
name|FAILING_MSG
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|fail
specifier|public
name|boolean
name|fail
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
DECL|method|create
specifier|public
name|Directory
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|,
name|DirContext
name|dirContext
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fail
condition|)
block|{
throw|throw
operator|new
name|FailingDirectoryFactoryException
argument_list|()
throw|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|,
name|dirContext
argument_list|)
return|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testMergeIndexesCoreAdminHandler
specifier|public
name|void
name|testMergeIndexesCoreAdminHandler
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|File
name|workDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
specifier|final
name|CoreContainer
name|cores
init|=
name|h
operator|.
name|getCoreContainer
argument_list|()
decl_stmt|;
specifier|final
name|CoreAdminHandler
name|admin
init|=
operator|new
name|CoreAdminHandler
argument_list|(
name|cores
argument_list|)
decl_stmt|;
try|try
init|(
name|SolrCore
name|core
init|=
name|cores
operator|.
name|getCore
argument_list|(
literal|"collection1"
argument_list|)
init|)
block|{
name|DirectoryFactory
name|df
init|=
name|core
operator|.
name|getDirectoryFactory
argument_list|()
decl_stmt|;
name|FailingDirectoryFactory
name|dirFactory
init|=
operator|(
name|FailingDirectoryFactory
operator|)
name|df
decl_stmt|;
try|try
block|{
name|dirFactory
operator|.
name|fail
operator|=
literal|true
expr_stmt|;
name|ignoreException
argument_list|(
name|FAILING_MSG
argument_list|)
expr_stmt|;
name|SolrQueryResponse
name|resp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|admin
operator|.
name|handleRequestBody
argument_list|(
name|req
argument_list|(
name|CoreAdminParams
operator|.
name|ACTION
argument_list|,
name|CoreAdminParams
operator|.
name|CoreAdminAction
operator|.
name|MERGEINDEXES
operator|.
name|toString
argument_list|()
argument_list|,
name|CoreAdminParams
operator|.
name|CORE
argument_list|,
literal|"collection1"
argument_list|,
name|CoreAdminParams
operator|.
name|INDEX_DIR
argument_list|,
name|workDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|resp
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FailingDirectoryFactory
operator|.
name|FailingDirectoryFactoryException
name|e
parameter_list|)
block|{
comment|// expected if error handling properly
block|}
finally|finally
block|{
name|unIgnoreException
argument_list|(
name|FAILING_MSG
argument_list|)
expr_stmt|;
block|}
name|dirFactory
operator|.
name|fail
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

