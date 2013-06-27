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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|conf
operator|.
name|Configuration
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
name|Constants
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
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|HdfsTestUtil
specifier|public
class|class
name|HdfsTestUtil
block|{
DECL|field|savedLocale
specifier|private
specifier|static
name|Locale
name|savedLocale
decl_stmt|;
DECL|method|setupClass
specifier|public
specifier|static
name|MiniDFSCluster
name|setupClass
parameter_list|(
name|String
name|dataDir
parameter_list|)
throws|throws
name|Exception
block|{
name|LuceneTestCase
operator|.
name|assumeFalse
argument_list|(
literal|"HDFS tests on Windows require Cygwin"
argument_list|,
name|Constants
operator|.
name|WINDOWS
argument_list|)
expr_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
decl_stmt|;
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|savedLocale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
comment|// TODO: we HACK around HADOOP-9643
name|Locale
operator|.
name|setDefault
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
name|int
name|dataNodes
init|=
literal|2
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.block.access.token.enable"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.permissions.enabled"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|"simple"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hdfs.minidfs.basedir"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hdfsBaseDir"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dfs.namenode.name.dir"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"nameNodeNameDir"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hdfs"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"build"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"hdfs"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"cache"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.lock.type"
argument_list|,
literal|"hdfs"
argument_list|)
expr_stmt|;
name|MiniDFSCluster
name|dfsCluster
init|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
name|dataNodes
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SolrTestCaseJ4
operator|.
name|useFactory
argument_list|(
literal|"org.apache.solr.core.HdfsDirectoryFactory"
argument_list|)
expr_stmt|;
return|return
name|dfsCluster
return|;
block|}
DECL|method|teardownClass
specifier|public
specifier|static
name|void
name|teardownClass
parameter_list|(
name|MiniDFSCluster
name|dfsCluster
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrTestCaseJ4
operator|.
name|resetFactory
argument_list|()
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"solr.lock.type"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"test.build.data"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"test.cache.data"
argument_list|)
expr_stmt|;
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|// TODO: we HACK around HADOOP-9643
if|if
condition|(
name|savedLocale
operator|!=
literal|null
condition|)
block|{
name|Locale
operator|.
name|setDefault
argument_list|(
name|savedLocale
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDataDir
specifier|public
specifier|static
name|String
name|getDataDir
parameter_list|(
name|MiniDFSCluster
name|dfsCluster
parameter_list|,
name|String
name|dataDir
parameter_list|)
throws|throws
name|IOException
block|{
name|URI
name|uri
init|=
name|dfsCluster
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|String
name|dir
init|=
name|uri
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
operator|new
name|File
argument_list|(
name|dataDir
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
return|return
name|dir
return|;
block|}
block|}
end_class

end_unit

