begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

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
name|EOFException
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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|ClosedChannelException
import|;
end_import

begin_comment
comment|// javadoc @link
end_comment

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
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_comment
comment|// javadoc
end_comment

begin_comment
comment|/**  * An {@link FSDirectory} implementation that uses java.nio's FileChannel's  * positional read, which allows multiple threads to read from the same file  * without synchronizing.  *<p>  * This class only uses FileChannel when reading; writing is achieved with  * {@link FSDirectory.FSIndexOutput}.  *<p>  *<b>NOTE</b>: NIOFSDirectory is not recommended on Windows because of a bug in  * how FileChannel.read is implemented in Sun's JRE. Inside of the  * implementation the position is apparently synchronized. See<a  * href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6265734">here</a>  * for details.  *</p>  *<p>  *<font color="red"><b>NOTE:</b> Accessing this class either directly or  * indirectly from a thread while it's interrupted can close the  * underlying file descriptor immediately if at the same time the thread is  * blocked on IO. The file descriptor will remain closed and subsequent access  * to {@link NIOFSDirectory} will throw a {@link ClosedChannelException}. If  * your application uses either {@link Thread#interrupt()} or  * {@link Future#cancel(boolean)} you should use {@link SimpleFSDirectory} in  * favor of {@link NIOFSDirectory}.</font>  *</p>  */
end_comment

begin_class
DECL|class|NIOFSDirectory
specifier|public
class|class
name|NIOFSDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new NIOFSDirectory for the named location.    *     * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default    * ({@link NativeFSLockFactory});    * @throws IOException if there is a low-level I/O error    */
DECL|method|NIOFSDirectory
specifier|public
name|NIOFSDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|lockFactory
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new NIOFSDirectory for the named location and {@link NativeFSLockFactory}.    *    * @param path the path of the directory    * @throws IOException if there is a low-level I/O error    */
DECL|method|NIOFSDirectory
specifier|public
name|NIOFSDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an IndexInput for the file with the given name. */
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
operator|new
name|NIOFSIndexInput
argument_list|(
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|,
name|context
argument_list|,
name|getReadChunkSize
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createSlicer
specifier|public
name|IndexInputSlicer
name|createSlicer
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|getDirectory
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|RandomAccessFile
name|descriptor
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|path
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
return|return
operator|new
name|Directory
operator|.
name|IndexInputSlicer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|descriptor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openSlice
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
block|{
return|return
operator|new
name|NIOFSIndexInput
argument_list|(
name|sliceDescription
argument_list|,
name|path
argument_list|,
name|descriptor
argument_list|,
name|descriptor
operator|.
name|getChannel
argument_list|()
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|BufferedIndexInput
operator|.
name|bufferSize
argument_list|(
name|context
argument_list|)
argument_list|,
name|getReadChunkSize
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Reads bytes with {@link FileChannel#read(ByteBuffer, long)}    */
DECL|class|NIOFSIndexInput
specifier|protected
specifier|static
class|class
name|NIOFSIndexInput
extends|extends
name|FSIndexInput
block|{
DECL|field|byteBuf
specifier|private
name|ByteBuffer
name|byteBuf
decl_stmt|;
comment|// wraps the buffer for NIO
DECL|field|channel
specifier|final
name|FileChannel
name|channel
decl_stmt|;
DECL|method|NIOFSIndexInput
specifier|public
name|NIOFSIndexInput
parameter_list|(
name|File
name|path
parameter_list|,
name|IOContext
name|context
parameter_list|,
name|int
name|chunkSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"NIOFSIndexInput(path=\""
operator|+
name|path
operator|+
literal|"\")"
argument_list|,
name|path
argument_list|,
name|context
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
name|channel
operator|=
name|file
operator|.
name|getChannel
argument_list|()
expr_stmt|;
block|}
DECL|method|NIOFSIndexInput
specifier|public
name|NIOFSIndexInput
parameter_list|(
name|String
name|sliceDescription
parameter_list|,
name|File
name|path
parameter_list|,
name|RandomAccessFile
name|file
parameter_list|,
name|FileChannel
name|fc
parameter_list|,
name|long
name|off
parameter_list|,
name|long
name|length
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
name|super
argument_list|(
literal|"NIOFSIndexInput("
operator|+
name|sliceDescription
operator|+
literal|" in path=\""
operator|+
name|path
operator|+
literal|"\" slice="
operator|+
name|off
operator|+
literal|":"
operator|+
operator|(
name|off
operator|+
name|length
operator|)
operator|+
literal|")"
argument_list|,
name|file
argument_list|,
name|off
argument_list|,
name|length
argument_list|,
name|bufferSize
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
name|channel
operator|=
name|fc
expr_stmt|;
name|isClone
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newBuffer
specifier|protected
name|void
name|newBuffer
parameter_list|(
name|byte
index|[]
name|newBuffer
parameter_list|)
block|{
name|super
operator|.
name|newBuffer
argument_list|(
name|newBuffer
argument_list|)
expr_stmt|;
name|byteBuf
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|newBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readInternal
specifier|protected
name|void
name|readInternal
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ByteBuffer
name|bb
decl_stmt|;
comment|// Determine the ByteBuffer we should use
if|if
condition|(
name|b
operator|==
name|buffer
operator|&&
literal|0
operator|==
name|offset
condition|)
block|{
comment|// Use our own pre-wrapped byteBuf:
assert|assert
name|byteBuf
operator|!=
literal|null
assert|;
name|byteBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|byteBuf
operator|.
name|limit
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|bb
operator|=
name|byteBuf
expr_stmt|;
block|}
else|else
block|{
name|bb
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|int
name|readOffset
init|=
name|bb
operator|.
name|position
argument_list|()
decl_stmt|;
name|int
name|readLength
init|=
name|bb
operator|.
name|limit
argument_list|()
operator|-
name|readOffset
decl_stmt|;
assert|assert
name|readLength
operator|==
name|len
assert|;
name|long
name|pos
init|=
name|getFilePointer
argument_list|()
operator|+
name|off
decl_stmt|;
if|if
condition|(
name|pos
operator|+
name|len
operator|>
name|end
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"read past EOF: "
operator|+
name|this
argument_list|)
throw|;
block|}
try|try
block|{
while|while
condition|(
name|readLength
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|limit
decl_stmt|;
if|if
condition|(
name|readLength
operator|>
name|chunkSize
condition|)
block|{
comment|// LUCENE-1566 - work around JVM Bug by breaking
comment|// very large reads into chunks
name|limit
operator|=
name|readOffset
operator|+
name|chunkSize
expr_stmt|;
block|}
else|else
block|{
name|limit
operator|=
name|readOffset
operator|+
name|readLength
expr_stmt|;
block|}
name|bb
operator|.
name|limit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
name|int
name|i
init|=
name|channel
operator|.
name|read
argument_list|(
name|bb
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|i
expr_stmt|;
name|readOffset
operator|+=
name|i
expr_stmt|;
name|readLength
operator|-=
name|i
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OutOfMemoryError
name|e
parameter_list|)
block|{
comment|// propagate OOM up and add a hint for 32bit VM Users hitting the bug
comment|// with a large chunk size in the fast path.
specifier|final
name|OutOfMemoryError
name|outOfMemoryError
init|=
operator|new
name|OutOfMemoryError
argument_list|(
literal|"OutOfMemoryError likely caused by the Sun VM Bug described in "
operator|+
literal|"https://issues.apache.org/jira/browse/LUCENE-1566; try calling FSDirectory.setReadChunkSize "
operator|+
literal|"with a value smaller than the current chunk size ("
operator|+
name|chunkSize
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|outOfMemoryError
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|outOfMemoryError
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
operator|+
literal|": "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|seekInternal
specifier|protected
name|void
name|seekInternal
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{}
block|}
block|}
end_class

end_unit

