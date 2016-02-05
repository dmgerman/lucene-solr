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
name|Closeable
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
name|attribute
operator|.
name|FileAttribute
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**   * Base class for tracking file handles.  *<p>  * This class adds tracking to all streams/channels and   * provides two hooks to handle file management:  *<ul>  *<li>{@link #onOpen(Path, Object)}  *<li>{@link #onClose(Path, Object)}  *</ul>  */
end_comment

begin_class
DECL|class|HandleTrackingFS
specifier|public
specifier|abstract
class|class
name|HandleTrackingFS
extends|extends
name|FilterFileSystemProvider
block|{
comment|/**    * Create a new instance, identified by {@code scheme} and passing    * through operations to {@code delegate}.     * @param scheme URI scheme for this provider    * @param delegate delegate filesystem to wrap.    */
DECL|method|HandleTrackingFS
specifier|public
name|HandleTrackingFS
parameter_list|(
name|String
name|scheme
parameter_list|,
name|FileSystem
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|scheme
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called when {@code path} is opened via {@code stream}.     * @param path Path that was opened    * @param stream Stream or Channel opened against the path.    * @throws IOException if an I/O error occurs.    */
DECL|method|onOpen
specifier|protected
specifier|abstract
name|void
name|onOpen
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Called when {@code path} is closed via {@code stream}.     * @param path Path that was closed    * @param stream Stream or Channel closed against the path.    * @throws IOException if an I/O error occurs.    */
DECL|method|onClose
specifier|protected
specifier|abstract
name|void
name|onClose
parameter_list|(
name|Path
name|path
parameter_list|,
name|Object
name|stream
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Helper method, to deal with onOpen() throwing exception    */
DECL|method|callOpenHook
specifier|final
name|void
name|callOpenHook
parameter_list|(
name|Path
name|path
parameter_list|,
name|Closeable
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|onOpen
argument_list|(
name|path
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
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
name|InputStream
name|stream
init|=
operator|new
name|FilterInputStream2
argument_list|(
name|super
operator|.
name|newInputStream
argument_list|(
name|path
argument_list|,
name|options
argument_list|)
argument_list|)
block|{
name|boolean
name|closed
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|onClose
argument_list|(
name|path
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"InputStream("
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
return|;
block|}
block|}
decl_stmt|;
name|callOpenHook
argument_list|(
name|path
argument_list|,
name|stream
argument_list|)
expr_stmt|;
return|return
name|stream
return|;
block|}
annotation|@
name|Override
DECL|method|newOutputStream
specifier|public
name|OutputStream
name|newOutputStream
parameter_list|(
specifier|final
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
name|OutputStream
name|stream
init|=
operator|new
name|FilterOutputStream2
argument_list|(
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
argument_list|)
block|{
name|boolean
name|closed
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|onClose
argument_list|(
name|path
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"OutputStream("
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
return|;
block|}
block|}
decl_stmt|;
name|callOpenHook
argument_list|(
name|path
argument_list|,
name|stream
argument_list|)
expr_stmt|;
return|return
name|stream
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
name|FileChannel
name|channel
init|=
operator|new
name|FilterFileChannel
argument_list|(
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
argument_list|)
block|{
name|boolean
name|closed
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|implCloseChannel
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
try|try
block|{
name|onClose
argument_list|(
name|path
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|implCloseChannel
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"FileChannel("
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
return|;
block|}
block|}
decl_stmt|;
name|callOpenHook
argument_list|(
name|path
argument_list|,
name|channel
argument_list|)
expr_stmt|;
return|return
name|channel
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
name|AsynchronousFileChannel
name|channel
init|=
operator|new
name|FilterAsynchronousFileChannel
argument_list|(
name|super
operator|.
name|newAsynchronousFileChannel
argument_list|(
name|path
argument_list|,
name|options
argument_list|,
name|executor
argument_list|,
name|attrs
argument_list|)
argument_list|)
block|{
name|boolean
name|closed
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|onClose
argument_list|(
name|path
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AsynchronousFileChannel("
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
return|;
block|}
block|}
decl_stmt|;
name|callOpenHook
argument_list|(
name|path
argument_list|,
name|channel
argument_list|)
expr_stmt|;
return|return
name|channel
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
name|SeekableByteChannel
name|channel
init|=
operator|new
name|FilterSeekableByteChannel
argument_list|(
name|super
operator|.
name|newByteChannel
argument_list|(
name|path
argument_list|,
name|options
argument_list|,
name|attrs
argument_list|)
argument_list|)
block|{
name|boolean
name|closed
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|onClose
argument_list|(
name|path
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SeekableByteChannel("
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
return|;
block|}
block|}
decl_stmt|;
name|callOpenHook
argument_list|(
name|path
argument_list|,
name|channel
argument_list|)
expr_stmt|;
return|return
name|channel
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
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|stream
init|=
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
decl_stmt|;
name|stream
operator|=
operator|new
name|FilterDirectoryStream
argument_list|(
name|stream
argument_list|,
name|fileSystem
argument_list|)
block|{
name|boolean
name|closed
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|onClose
argument_list|(
name|dir
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DirectoryStream("
operator|+
name|dir
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
return|;
block|}
block|}
expr_stmt|;
name|callOpenHook
argument_list|(
name|dir
argument_list|,
name|stream
argument_list|)
expr_stmt|;
return|return
name|stream
return|;
block|}
block|}
end_class

end_unit

