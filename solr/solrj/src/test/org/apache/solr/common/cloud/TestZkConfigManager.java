begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
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
name|cloud
operator|.
name|ZkTestServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|ZooDefs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Id
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|server
operator|.
name|auth
operator|.
name|DigestAuthenticationProvider
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
DECL|class|TestZkConfigManager
specifier|public
class|class
name|TestZkConfigManager
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|zkServer
specifier|private
specifier|static
name|ZkTestServer
name|zkServer
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startZkServer
specifier|public
specifier|static
name|void
name|startZkServer
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|zkServer
operator|=
operator|new
name|ZkTestServer
argument_list|(
name|createTempDir
argument_list|(
literal|"zkData"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdownZkServer
specifier|public
specifier|static
name|void
name|shutdownZkServer
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|zkServer
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUploadConfig
specifier|public
name|void
name|testUploadConfig
parameter_list|()
throws|throws
name|IOException
block|{
name|zkServer
operator|.
name|ensurePathExists
argument_list|(
literal|"/solr"
argument_list|)
expr_stmt|;
try|try
init|(
name|SolrZkClient
name|zkClient
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|(
literal|"/solr"
argument_list|)
argument_list|,
literal|10000
argument_list|)
init|)
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|zkClient
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|configManager
operator|.
name|listConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|testdata
init|=
literal|"test data"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Path
name|tempConfig
init|=
name|createTempDir
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|,
name|testdata
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|"file2"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|"subdir"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|"subdir"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"file3"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|".ignored"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|".ignoreddir"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|".ignoreddir"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"ignored"
argument_list|)
argument_list|)
expr_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|tempConfig
argument_list|,
literal|"testconfig"
argument_list|)
expr_stmt|;
comment|// uploading a directory creates a new config
name|List
argument_list|<
name|String
argument_list|>
name|configs
init|=
name|configManager
operator|.
name|listConfigs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testconfig"
argument_list|,
name|configs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// check downloading
name|Path
name|downloadPath
init|=
name|createTempDir
argument_list|(
literal|"download"
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|downloadConfigDir
argument_list|(
literal|"testconfig"
argument_list|,
name|downloadPath
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|"file2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|"subdir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|"subdir/file3"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// dotfiles should be ignored
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|".ignored"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|".ignoreddir/ignored"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|checkdata
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|downloadPath
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|testdata
argument_list|,
name|checkdata
argument_list|)
expr_stmt|;
comment|// uploading to the same config overwrites
name|byte
index|[]
name|overwritten
init|=
literal|"new test data"
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Files
operator|.
name|write
argument_list|(
name|tempConfig
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|,
name|overwritten
argument_list|)
expr_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|tempConfig
argument_list|,
literal|"testconfig"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configManager
operator|.
name|listConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|download2
init|=
name|createTempDir
argument_list|(
literal|"download2"
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|downloadConfigDir
argument_list|(
literal|"testconfig"
argument_list|,
name|download2
argument_list|)
expr_stmt|;
name|byte
index|[]
name|checkdata2
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|download2
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|overwritten
argument_list|,
name|checkdata2
argument_list|)
expr_stmt|;
comment|// uploading same files to a new name creates a new config
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|tempConfig
argument_list|,
literal|"config2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|configManager
operator|.
name|listConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUploadWithACL
specifier|public
name|void
name|testUploadWithACL
parameter_list|()
throws|throws
name|IOException
block|{
name|zkServer
operator|.
name|ensurePathExists
argument_list|(
literal|"/acl"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|readOnlyUsername
init|=
literal|"readonly"
decl_stmt|;
specifier|final
name|String
name|readOnlyPassword
init|=
literal|"readonly"
decl_stmt|;
specifier|final
name|String
name|writeableUsername
init|=
literal|"writeable"
decl_stmt|;
specifier|final
name|String
name|writeablePassword
init|=
literal|"writeable"
decl_stmt|;
name|ZkACLProvider
name|aclProvider
init|=
operator|new
name|DefaultZkACLProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|ACL
argument_list|>
name|createGlobalACLsToAdd
parameter_list|()
block|{
try|try
block|{
name|List
argument_list|<
name|ACL
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|ACL
argument_list|(
name|ZooDefs
operator|.
name|Perms
operator|.
name|ALL
argument_list|,
operator|new
name|Id
argument_list|(
literal|"digest"
argument_list|,
name|DigestAuthenticationProvider
operator|.
name|generateDigest
argument_list|(
name|writeableUsername
operator|+
literal|":"
operator|+
name|writeablePassword
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|ACL
argument_list|(
name|ZooDefs
operator|.
name|Perms
operator|.
name|READ
argument_list|,
operator|new
name|Id
argument_list|(
literal|"digest"
argument_list|,
name|DigestAuthenticationProvider
operator|.
name|generateDigest
argument_list|(
name|readOnlyUsername
operator|+
literal|":"
operator|+
name|readOnlyPassword
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|ZkCredentialsProvider
name|readonly
init|=
operator|new
name|DefaultZkCredentialsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Collection
argument_list|<
name|ZkCredentials
argument_list|>
name|createCredentials
parameter_list|()
block|{
name|List
argument_list|<
name|ZkCredentials
argument_list|>
name|credentials
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|add
argument_list|(
operator|new
name|ZkCredentials
argument_list|(
literal|"digest"
argument_list|,
operator|(
name|readOnlyUsername
operator|+
literal|":"
operator|+
name|readOnlyPassword
operator|)
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|credentials
return|;
block|}
block|}
decl_stmt|;
name|ZkCredentialsProvider
name|writeable
init|=
operator|new
name|DefaultZkCredentialsProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Collection
argument_list|<
name|ZkCredentials
argument_list|>
name|createCredentials
parameter_list|()
block|{
name|List
argument_list|<
name|ZkCredentials
argument_list|>
name|credentials
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|add
argument_list|(
operator|new
name|ZkCredentials
argument_list|(
literal|"digest"
argument_list|,
operator|(
name|writeableUsername
operator|+
literal|":"
operator|+
name|writeablePassword
operator|)
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|credentials
return|;
block|}
block|}
decl_stmt|;
name|Path
name|configPath
init|=
name|createTempDir
argument_list|(
literal|"acl-config"
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createFile
argument_list|(
name|configPath
operator|.
name|resolve
argument_list|(
literal|"file1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Start with all-access client
try|try
init|(
name|SolrZkClient
name|client
init|=
name|buildZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|(
literal|"/acl"
argument_list|)
argument_list|,
name|aclProvider
argument_list|,
name|writeable
argument_list|)
init|)
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|configPath
argument_list|,
literal|"acltest"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configManager
operator|.
name|listConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Readonly access client can get the list of configs, but can't upload
try|try
init|(
name|SolrZkClient
name|client
init|=
name|buildZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|(
literal|"/acl"
argument_list|)
argument_list|,
name|aclProvider
argument_list|,
name|readonly
argument_list|)
init|)
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|configManager
operator|.
name|listConfigs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|configManager
operator|.
name|uploadConfigDir
argument_list|(
name|configPath
argument_list|,
literal|"acltest2"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an ACL exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|KeeperException
operator|.
name|NoAuthException
operator|.
name|class
argument_list|,
name|Throwables
operator|.
name|getRootCause
argument_list|(
name|e
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Client with no auth whatsoever can't even get the list of configs
try|try
init|(
name|SolrZkClient
name|client
init|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServer
operator|.
name|getZkAddress
argument_list|(
literal|"/acl"
argument_list|)
argument_list|,
literal|10000
argument_list|)
init|)
block|{
name|ZkConfigManager
name|configManager
init|=
operator|new
name|ZkConfigManager
argument_list|(
name|client
argument_list|)
decl_stmt|;
name|configManager
operator|.
name|listConfigs
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an ACL exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|KeeperException
operator|.
name|NoAuthException
operator|.
name|class
argument_list|,
name|Throwables
operator|.
name|getRootCause
argument_list|(
name|e
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildZkClient
specifier|static
name|SolrZkClient
name|buildZkClient
parameter_list|(
name|String
name|zkAddress
parameter_list|,
specifier|final
name|ZkACLProvider
name|aclProvider
parameter_list|,
specifier|final
name|ZkCredentialsProvider
name|credentialsProvider
parameter_list|)
block|{
return|return
operator|new
name|SolrZkClient
argument_list|(
name|zkAddress
argument_list|,
literal|10000
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ZkCredentialsProvider
name|createZkCredentialsToAddAutomatically
parameter_list|()
block|{
return|return
name|credentialsProvider
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ZkACLProvider
name|createZkACLProvider
parameter_list|()
block|{
return|return
name|aclProvider
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

