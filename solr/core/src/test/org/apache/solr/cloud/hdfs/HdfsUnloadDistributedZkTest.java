begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
operator|.
name|hdfs
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
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|MiniDFSCluster
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
operator|.
name|Slow
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
name|cloud
operator|.
name|UnloadDistributedZkTest
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
name|BeforeClass
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
name|annotations
operator|.
name|Nightly
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
name|annotations
operator|.
name|ThreadLeakScope
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
name|annotations
operator|.
name|ThreadLeakScope
operator|.
name|Scope
import|;
end_import

begin_class
annotation|@
name|Slow
annotation|@
name|Nightly
annotation|@
name|ThreadLeakScope
argument_list|(
name|Scope
operator|.
name|NONE
argument_list|)
comment|// hdfs client currently leaks thread(s)
DECL|class|HdfsUnloadDistributedZkTest
specifier|public
class|class
name|HdfsUnloadDistributedZkTest
extends|extends
name|UnloadDistributedZkTest
block|{
DECL|field|dfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|dfsCluster
operator|=
name|HdfsTestUtil
operator|.
name|setupClass
argument_list|(
name|createTempDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsTestUtil
operator|.
name|teardownClass
argument_list|(
name|dfsCluster
argument_list|)
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDataDir
specifier|protected
name|String
name|getDataDir
parameter_list|(
name|String
name|dataDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|HdfsTestUtil
operator|.
name|getDataDir
argument_list|(
name|dfsCluster
argument_list|,
name|dataDir
argument_list|)
return|;
block|}
block|}
end_class

end_unit

