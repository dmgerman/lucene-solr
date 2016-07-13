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
name|nio
operator|.
name|file
operator|.
name|FileVisitResult
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
name|nio
operator|.
name|file
operator|.
name|SimpleFileVisitor
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
name|attribute
operator|.
name|BasicFileAttributes
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
name|FSDirectory
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
name|lucene
operator|.
name|store
operator|.
name|SimpleFSDirectory
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

begin_comment
comment|/**  * A concrete implementation of {@linkplain BackupRepository} interface supporting backup/restore of Solr indexes to a  * local file-system. (Note - This can even be used for a shared file-system if it is exposed via a local file-system  * interface e.g. NFS).  */
end_comment

begin_class
DECL|class|LocalFileSystemRepository
specifier|public
class|class
name|LocalFileSystemRepository
implements|implements
name|BackupRepository
block|{
DECL|field|config
specifier|private
name|NamedList
name|config
init|=
literal|null
decl_stmt|;
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
modifier|...
name|pathComponents
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pathComponents
operator|.
name|length
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Path
name|result
init|=
name|Paths
operator|.
name|get
argument_list|(
name|pathComponents
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|pathComponents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|result
operator|.
name|resolve
argument_list|(
name|pathComponents
index|[
name|i
index|]
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
name|Files
operator|.
name|createDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
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
name|Files
operator|.
name|walkFileTree
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|visitFile
parameter_list|(
name|Path
name|file
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|Files
operator|.
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileVisitResult
name|postVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|IOException
name|exc
parameter_list|)
throws|throws
name|IOException
block|{
name|Files
operator|.
name|delete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
block|}
argument_list|)
expr_stmt|;
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
name|Files
operator|.
name|exists
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
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
try|try
init|(
name|FSDirectory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dirPath
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
init|)
block|{
return|return
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|ctx
argument_list|)
return|;
block|}
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
name|Files
operator|.
name|newOutputStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|)
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
name|dirPath
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|FSDirectory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dirPath
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
argument_list|)
init|)
block|{
return|return
name|dir
operator|.
name|listAll
argument_list|()
return|;
block|}
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
name|Files
operator|.
name|isDirectory
argument_list|(
name|Paths
operator|.
name|get
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
name|FSDirectory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dest
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
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
name|sourceDir
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
name|FSDirectory
name|dir
init|=
operator|new
name|SimpleFSDirectory
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|sourceDir
argument_list|)
argument_list|,
name|NoLockFactory
operator|.
name|INSTANCE
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
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

