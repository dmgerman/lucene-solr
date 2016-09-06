begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.core.backup.repository
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|backup
operator|.
name|repository
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
name|io
operator|.
name|OutputStream
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
name|net
operator|.
name|URISyntaxException
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|IOContext
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
name|IndexInput
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
name|NoLockFactory
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
name|SolrException
operator|.
name|ErrorCode
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
name|HdfsDirectoryFactory
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
name|store
operator|.
name|hdfs
operator|.
name|HdfsDirectory
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
name|store
operator|.
name|hdfs
operator|.
name|HdfsDirectory
operator|.
name|HdfsIndexInput
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
name|Preconditions
import|;
end_import

begin_class
DECL|class|HdfsBackupRepository
specifier|public
class|class
name|HdfsBackupRepository
implements|implements
name|BackupRepository
block|{
DECL|field|factory
specifier|private
name|HdfsDirectoryFactory
name|factory
decl_stmt|;
DECL|field|hdfsConfig
specifier|private
name|Configuration
name|hdfsConfig
init|=
literal|null
decl_stmt|;
DECL|field|fileSystem
specifier|private
name|FileSystem
name|fileSystem
init|=
literal|null
decl_stmt|;
DECL|field|baseHdfsPath
specifier|private
name|Path
name|baseHdfsPath
init|=
literal|null
decl_stmt|;
DECL|field|config
specifier|private
name|NamedList
name|config
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|args
expr_stmt|;
comment|// We don't really need this factory instance. But we want to initialize it here to
comment|// make sure that all HDFS related initialization is at one place (and not duplicated here).
name|factory
operator|=
operator|new
name|HdfsDirectoryFactory
argument_list|()
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|this
operator|.
name|hdfsConfig
operator|=
name|factory
operator|.
name|getConf
argument_list|()
expr_stmt|;
name|String
name|hdfsSolrHome
init|=
operator|(
name|String
operator|)
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|args
operator|.
name|get
argument_list|(
name|HdfsDirectoryFactory
operator|.
name|HDFS_HOME
argument_list|)
argument_list|,
literal|"Please specify "
operator|+
name|HdfsDirectoryFactory
operator|.
name|HDFS_HOME
operator|+
literal|" property."
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|hdfsSolrHome
argument_list|)
decl_stmt|;
while|while
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
comment|// Compute the path of root file-system (without requiring an additional system property).
name|baseHdfsPath
operator|=
name|path
expr_stmt|;
name|path
operator|=
name|path
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|fileSystem
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|this
operator|.
name|baseHdfsPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|this
operator|.
name|hdfsConfig
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|fileSystem
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|fileSystem
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|factory
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|factory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|getConfigProperty
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfigProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|T
operator|)
name|this
operator|.
name|config
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createURI
specifier|public
name|URI
name|createURI
parameter_list|(
name|String
name|location
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|URI
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
operator|new
name|URI
argument_list|(
name|location
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|result
operator|=
name|resolve
argument_list|(
name|this
operator|.
name|baseHdfsPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|result
operator|=
name|resolve
argument_list|(
name|this
operator|.
name|baseHdfsPath
operator|.
name|toUri
argument_list|()
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|resolve
specifier|public
name|URI
name|resolve
parameter_list|(
name|URI
name|baseUri
parameter_list|,
name|String
modifier|...
name|pathComponents
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|baseUri
operator|.
name|isAbsolute
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|result
init|=
operator|new
name|Path
argument_list|(
name|baseUri
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathComponents
control|)
block|{
name|result
operator|=
operator|new
name|Path
argument_list|(
name|result
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toUri
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|exists
specifier|public
name|boolean
name|exists
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|fileSystem
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPathType
specifier|public
name|PathType
name|getPathType
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|fileSystem
operator|.
name|isDirectory
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
condition|?
name|PathType
operator|.
name|DIRECTORY
else|:
name|PathType
operator|.
name|FILE
return|;
block|}
annotation|@
name|Override
DECL|method|listAll
specifier|public
name|String
index|[]
name|listAll
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|status
init|=
name|this
operator|.
name|fileSystem
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|status
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|status
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|status
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|URI
name|dirPath
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|dirPath
argument_list|)
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
return|return
operator|new
name|HdfsIndexInput
argument_list|(
name|fileName
argument_list|,
name|this
operator|.
name|fileSystem
argument_list|,
name|p
argument_list|,
name|HdfsDirectory
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|OutputStream
name|createOutput
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|fileSystem
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createDirectory
specifier|public
name|void
name|createDirectory
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|fileSystem
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to create a directory at following location "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteDirectory
specifier|public
name|void
name|deleteDirectory
parameter_list|(
name|URI
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|fileSystem
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to delete a directory at following location "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|copyFileFrom
specifier|public
name|void
name|copyFileFrom
parameter_list|(
name|Directory
name|sourceDir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|URI
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|HdfsDirectory
name|dir
init|=
operator|new
name|HdfsDirectory
argument_list|(
operator|new
name|Path
argument_list|(
name|dest
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|,
name|hdfsConfig
argument_list|,
name|HdfsDirectory
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
init|)
block|{
name|dir
operator|.
name|copyFrom
argument_list|(
name|sourceDir
argument_list|,
name|fileName
argument_list|,
name|fileName
argument_list|,
name|DirectoryFactory
operator|.
name|IOCONTEXT_NO_CACHE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|copyFileTo
specifier|public
name|void
name|copyFileTo
parameter_list|(
name|URI
name|sourceRepo
parameter_list|,
name|String
name|fileName
parameter_list|,
name|Directory
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|HdfsDirectory
name|dir
init|=
operator|new
name|HdfsDirectory
argument_list|(
operator|new
name|Path
argument_list|(
name|sourceRepo
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|,
name|hdfsConfig
argument_list|,
name|HdfsDirectory
operator|.
name|DEFAULT_BUFFER_SIZE
argument_list|)
init|)
block|{
name|dest
operator|.
name|copyFrom
argument_list|(
name|dir
argument_list|,
name|fileName
argument_list|,
name|fileName
argument_list|,
name|DirectoryFactory
operator|.
name|IOCONTEXT_NO_CACHE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

