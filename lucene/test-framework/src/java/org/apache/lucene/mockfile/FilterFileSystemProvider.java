begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.mockfile
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|mockfile
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
name|InputStream
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
name|channels
operator|.
name|AsynchronousFileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SeekableByteChannel
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
name|AccessMode
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
name|CopyOption
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
name|DirectoryStream
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
name|DirectoryStream
operator|.
name|Filter
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
name|FileStore
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
name|FileSystem
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
name|LinkOption
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
name|OpenOption
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
name|ProviderMismatchException
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|attribute
operator|.
name|FileAttribute
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
name|FileAttributeView
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
name|spi
operator|.
name|FileSystemProvider
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_comment
comment|/**    * A {@code FilterFileSystemProvider} contains another   * {@code FileSystemProvider}, which it uses as its basic   * source of data, possibly transforming the data along the   * way or providing additional functionality.   */
end_comment

begin_class
DECL|class|FilterFileSystemProvider
specifier|public
specifier|abstract
class|class
name|FilterFileSystemProvider
extends|extends
name|FileSystemProvider
block|{
comment|/**     * The underlying {@code FileSystemProvider}.     */
DECL|field|delegate
specifier|protected
specifier|final
name|FileSystemProvider
name|delegate
decl_stmt|;
comment|/**     * The underlying {@code FileSystem} instance.     */
DECL|field|fileSystem
specifier|protected
name|FileSystem
name|fileSystem
decl_stmt|;
comment|/**     * The URI scheme for this provider.    */
DECL|field|scheme
specifier|protected
specifier|final
name|String
name|scheme
decl_stmt|;
comment|/**    * Construct a {@code FilterFileSystemProvider} indicated by    * the specified {@code scheme} and wrapping functionality of the    * provider of the specified base filesystem.    * @param scheme URI scheme    * @param delegateInstance specified base filesystem.    */
DECL|method|FilterFileSystemProvider
specifier|public
name|FilterFileSystemProvider
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystem
name|delegateInstance
parameter_list|)
block|{
name|this
operator|.
name|scheme
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegateInstance
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegateInstance
operator|.
name|provider
argument_list|()
expr_stmt|;
name|this
operator|.
name|fileSystem
operator|=
operator|new
name|FilterFileSystem
argument_list|(
name|this
argument_list|,
name|delegateInstance
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a {@code FilterFileSystemProvider} indicated by    * the specified {@code scheme} and wrapping functionality of the    * provider. You must set the singleton {@code filesystem} yourself.    * @param scheme URI scheme    * @param delegate specified base provider.    */
DECL|method|FilterFileSystemProvider
specifier|public
name|FilterFileSystemProvider
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystemProvider
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|scheme
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|scheme
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScheme
specifier|public
name|String
name|getScheme
parameter_list|()
block|{
return|return
name|scheme
return|;
block|}
annotation|@
name|Override
DECL|method|newFileSystem
specifier|public
name|FileSystem
name|newFileSystem
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|env
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fileSystem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"subclass did not initialize singleton filesystem"
argument_list|)
throw|;
block|}
return|return
name|fileSystem
return|;
block|}
annotation|@
name|Override
DECL|method|newFileSystem
specifier|public
name|FileSystem
name|newFileSystem
parameter_list|(
name|Path
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|env
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fileSystem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"subclass did not initialize singleton filesystem"
argument_list|)
throw|;
block|}
return|return
name|fileSystem
return|;
block|}
annotation|@
name|Override
DECL|method|getFileSystem
specifier|public
name|FileSystem
name|getFileSystem
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
if|if
condition|(
name|fileSystem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"subclass did not initialize singleton filesystem"
argument_list|)
throw|;
block|}
return|return
name|fileSystem
return|;
block|}
annotation|@
name|Override
DECL|method|getPath
specifier|public
name|Path
name|getPath
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
if|if
condition|(
name|fileSystem
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"subclass did not initialize singleton filesystem"
argument_list|)
throw|;
block|}
name|Path
name|path
init|=
name|delegate
operator|.
name|getPath
argument_list|(
name|uri
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterPath
argument_list|(
name|path
argument_list|,
name|fileSystem
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
name|Path
name|dir
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|createDirectory
argument_list|(
name|toDelegate
argument_list|(
name|dir
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|delete
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copy
specifier|public
name|void
name|copy
parameter_list|(
name|Path
name|source
parameter_list|,
name|Path
name|target
parameter_list|,
name|CopyOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|copy
argument_list|(
name|toDelegate
argument_list|(
name|source
argument_list|)
argument_list|,
name|toDelegate
argument_list|(
name|target
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|Path
name|source
parameter_list|,
name|Path
name|target
parameter_list|,
name|CopyOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|move
argument_list|(
name|toDelegate
argument_list|(
name|source
argument_list|)
argument_list|,
name|toDelegate
argument_list|(
name|target
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isSameFile
specifier|public
name|boolean
name|isSameFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|Path
name|path2
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|isSameFile
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|toDelegate
argument_list|(
name|path2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isHidden
specifier|public
name|boolean
name|isHidden
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|isHidden
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStore
specifier|public
name|FileStore
name|getFileStore
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getFileStore
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkAccess
specifier|public
name|void
name|checkAccess
parameter_list|(
name|Path
name|path
parameter_list|,
name|AccessMode
modifier|...
name|modes
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|checkAccess
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|modes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFileAttributeView
specifier|public
parameter_list|<
name|V
extends|extends
name|FileAttributeView
parameter_list|>
name|V
name|getFileAttributeView
parameter_list|(
name|Path
name|path
parameter_list|,
name|Class
argument_list|<
name|V
argument_list|>
name|type
parameter_list|,
name|LinkOption
modifier|...
name|options
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getFileAttributeView
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|type
argument_list|,
name|options
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readAttributes
specifier|public
parameter_list|<
name|A
extends|extends
name|BasicFileAttributes
parameter_list|>
name|A
name|readAttributes
parameter_list|(
name|Path
name|path
parameter_list|,
name|Class
argument_list|<
name|A
argument_list|>
name|type
parameter_list|,
name|LinkOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|readAttributes
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|type
argument_list|,
name|options
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readAttributes
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|attributes
parameter_list|,
name|LinkOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|readAttributes
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|attributes
argument_list|,
name|options
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setAttribute
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|attribute
parameter_list|,
name|Object
name|value
parameter_list|,
name|LinkOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|setAttribute
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|attribute
argument_list|,
name|value
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newInputStream
specifier|public
name|InputStream
name|newInputStream
parameter_list|(
name|Path
name|path
parameter_list|,
name|OpenOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newInputStream
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|options
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newOutputStream
specifier|public
name|OutputStream
name|newOutputStream
parameter_list|(
name|Path
name|path
parameter_list|,
name|OpenOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newOutputStream
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|options
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newFileChannel
specifier|public
name|FileChannel
name|newFileChannel
parameter_list|(
name|Path
name|path
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newFileChannel
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|options
argument_list|,
name|attrs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newAsynchronousFileChannel
specifier|public
name|AsynchronousFileChannel
name|newAsynchronousFileChannel
parameter_list|(
name|Path
name|path
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|,
name|ExecutorService
name|executor
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newAsynchronousFileChannel
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|options
argument_list|,
name|executor
argument_list|,
name|attrs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newByteChannel
specifier|public
name|SeekableByteChannel
name|newByteChannel
parameter_list|(
name|Path
name|path
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|OpenOption
argument_list|>
name|options
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|newByteChannel
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|,
name|options
argument_list|,
name|attrs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newDirectoryStream
specifier|public
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|newDirectoryStream
parameter_list|(
name|Path
name|dir
parameter_list|,
specifier|final
name|Filter
argument_list|<
name|?
super|super
name|Path
argument_list|>
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|Filter
argument_list|<
name|Path
argument_list|>
name|wrappedFilter
init|=
operator|new
name|Filter
argument_list|<
name|Path
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|entry
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|filter
operator|.
name|accept
argument_list|(
operator|new
name|FilterPath
argument_list|(
name|entry
argument_list|,
name|fileSystem
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|FilterDirectoryStream
argument_list|(
name|delegate
operator|.
name|newDirectoryStream
argument_list|(
name|toDelegate
argument_list|(
name|dir
argument_list|)
argument_list|,
name|wrappedFilter
argument_list|)
argument_list|,
name|fileSystem
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSymbolicLink
specifier|public
name|void
name|createSymbolicLink
parameter_list|(
name|Path
name|link
parameter_list|,
name|Path
name|target
parameter_list|,
name|FileAttribute
argument_list|<
name|?
argument_list|>
modifier|...
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|createSymbolicLink
argument_list|(
name|toDelegate
argument_list|(
name|link
argument_list|)
argument_list|,
name|toDelegate
argument_list|(
name|target
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createLink
specifier|public
name|void
name|createLink
parameter_list|(
name|Path
name|link
parameter_list|,
name|Path
name|existing
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|createLink
argument_list|(
name|toDelegate
argument_list|(
name|link
argument_list|)
argument_list|,
name|toDelegate
argument_list|(
name|existing
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteIfExists
specifier|public
name|boolean
name|deleteIfExists
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|deleteIfExists
argument_list|(
name|toDelegate
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readSymbolicLink
specifier|public
name|Path
name|readSymbolicLink
parameter_list|(
name|Path
name|link
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|readSymbolicLink
argument_list|(
name|toDelegate
argument_list|(
name|link
argument_list|)
argument_list|)
return|;
block|}
DECL|method|toDelegate
specifier|protected
name|Path
name|toDelegate
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|instanceof
name|FilterPath
condition|)
block|{
name|FilterPath
name|fp
init|=
operator|(
name|FilterPath
operator|)
name|path
decl_stmt|;
if|if
condition|(
name|fp
operator|.
name|fileSystem
operator|!=
name|fileSystem
condition|)
block|{
throw|throw
operator|new
name|ProviderMismatchException
argument_list|(
literal|"mismatch, expected: "
operator|+
name|fileSystem
operator|.
name|provider
argument_list|()
operator|.
name|getClass
argument_list|()
operator|+
literal|", got: "
operator|+
name|fp
operator|.
name|fileSystem
operator|.
name|provider
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|fp
operator|.
name|delegate
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ProviderMismatchException
argument_list|(
literal|"mismatch, expected: FilterPath, got: "
operator|+
name|path
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**     * Override to trigger some behavior when the filesystem is closed.    *<p>    * This is always called for each FilterFileSystemProvider in the chain.    */
DECL|method|onClose
specifier|protected
name|void
name|onClose
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|delegate
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

