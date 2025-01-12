begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
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
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|util
operator|.
name|LuceneTestCase
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
name|util
operator|.
name|NamedList
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
name|handler
operator|.
name|admin
operator|.
name|CoreAdminHandler
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
name|handler
operator|.
name|component
operator|.
name|HttpShardHandlerFactory
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

begin_class
DECL|class|DirectoryFactoryTest
specifier|public
class|class
name|DirectoryFactoryTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testLockTypesUnchanged
specifier|public
name|void
name|testLockTypesUnchanged
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"simple"
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SIMPLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"native"
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NATIVE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"single"
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_SINGLE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"none"
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_NONE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hdfs"
argument_list|,
name|DirectoryFactory
operator|.
name|LOCK_TYPE_HDFS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Before
DECL|method|clean
specifier|public
name|void
name|clean
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.data.home"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.solr.home"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetDataHome
specifier|public
name|void
name|testGetDataHome
parameter_list|()
throws|throws
name|Exception
block|{
name|MockCoreContainer
name|cc
init|=
operator|new
name|MockCoreContainer
argument_list|(
literal|"/solr/home"
argument_list|)
decl_stmt|;
name|Properties
name|cp
init|=
name|cc
operator|.
name|getContainerProperties
argument_list|()
decl_stmt|;
name|boolean
name|zkAware
init|=
name|cc
operator|.
name|isZooKeeperAware
argument_list|()
decl_stmt|;
name|RAMDirectoryFactory
name|rdf
init|=
operator|new
name|RAMDirectoryFactory
argument_list|()
decl_stmt|;
name|rdf
operator|.
name|initCoreContainer
argument_list|(
name|cc
argument_list|)
expr_stmt|;
name|rdf
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
comment|// No solr.data.home property set. Absolute instanceDir
name|assertEquals
argument_list|(
literal|"/tmp/inst1/data"
argument_list|,
name|rdf
operator|.
name|getDataHome
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
literal|"core_name"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/tmp/inst1"
argument_list|)
argument_list|,
name|cp
argument_list|,
name|zkAware
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Simulate solr.data.home set in solrconfig.xml<directoryFactory> tag
name|NamedList
name|args
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"solr.data.home"
argument_list|,
literal|"/solrdata/"
argument_list|)
expr_stmt|;
name|rdf
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/solrdata/inst_dir/data"
argument_list|,
name|rdf
operator|.
name|getDataHome
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
literal|"core_name"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"inst_dir"
argument_list|)
argument_list|,
name|cp
argument_list|,
name|zkAware
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// solr.data.home set with System property, and relative path
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.data.home"
argument_list|,
literal|"solrdata"
argument_list|)
expr_stmt|;
name|rdf
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/solr/home/solrdata/inst_dir/data"
argument_list|,
name|rdf
operator|.
name|getDataHome
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
literal|"core_name"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"inst_dir"
argument_list|)
argument_list|,
name|cp
argument_list|,
name|zkAware
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test parsing last component of instanceDir, and using custom dataDir
name|assertEquals
argument_list|(
literal|"/solr/home/solrdata/myinst/mydata"
argument_list|,
name|rdf
operator|.
name|getDataHome
argument_list|(
operator|new
name|CoreDescriptor
argument_list|(
literal|"core_name"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/path/to/myinst"
argument_list|)
argument_list|,
name|cp
argument_list|,
name|zkAware
argument_list|,
literal|"dataDir"
argument_list|,
literal|"mydata"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|MockCoreContainer
specifier|private
specifier|static
class|class
name|MockCoreContainer
extends|extends
name|CoreContainer
block|{
DECL|field|mockSolrHome
specifier|private
specifier|final
name|String
name|mockSolrHome
decl_stmt|;
DECL|method|MockCoreContainer
specifier|public
name|MockCoreContainer
parameter_list|(
name|String
name|solrHome
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|mockSolrHome
operator|=
name|solrHome
expr_stmt|;
name|this
operator|.
name|shardHandlerFactory
operator|=
operator|new
name|HttpShardHandlerFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|coreAdminHandler
operator|=
operator|new
name|CoreAdminHandler
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSolrHome
specifier|public
name|String
name|getSolrHome
parameter_list|()
block|{
return|return
name|mockSolrHome
return|;
block|}
block|}
block|}
end_class

end_unit

