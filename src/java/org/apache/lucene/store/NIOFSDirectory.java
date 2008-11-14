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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|IOException
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
name|FileChannel
import|;
end_import

begin_comment
comment|/**  * NIO version of FSDirectory.  Uses FileChannel.read(ByteBuffer dst, long position) method  * which allows multiple threads to read from the file without synchronizing.  FSDirectory  * synchronizes in the FSIndexInput.readInternal method which can cause pileups when there  * are many threads accessing the Directory concurrently.    *  * This class only uses FileChannel when reading; writing  * with an IndexOutput is inherited from FSDirectory.  *   * Note: NIOFSDirectory is not recommended on Windows because of a bug  * in how FileChannel.read is implemented in Sun's JRE.  * Inside of the implementation the position is apparently  * synchronized.  See here for details:   * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6265734   *   * @see FSDirectory  */
end_comment

begin_class
DECL|class|NIOFSDirectory
specifier|public
class|class
name|NIOFSDirectory
extends|extends
name|FSDirectory
block|{
comment|/** Create a new NIOFSDirectory for the named location.    *     * @param path the path of the directory    * @param lockFactory the lock factory to use, or null for the default.    * @throws IOException    */
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
comment|// back compatibility so FSDirectory can instantiate via reflection
DECL|method|NIOFSDirectory
specifier|protected
name|NIOFSDirectory
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|// Inherit javadoc
DECL|method|openInput
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|bufferSize
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
name|getFile
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
DECL|class|NIOFSIndexInput
specifier|private
specifier|static
class|class
name|NIOFSIndexInput
extends|extends
name|FSDirectory
operator|.
name|FSIndexInput
block|{
DECL|field|byteBuf
specifier|private
name|ByteBuffer
name|byteBuf
decl_stmt|;
comment|// wraps the buffer for NIO
DECL|field|otherBuffer
specifier|private
name|byte
index|[]
name|otherBuffer
decl_stmt|;
DECL|field|otherByteBuf
specifier|private
name|ByteBuffer
name|otherByteBuf
decl_stmt|;
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
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|path
argument_list|,
name|bufferSize
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
operator|!
name|isClone
operator|&&
name|file
operator|.
name|isOpen
condition|)
block|{
comment|// Close the channel& file
try|try
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|otherBuffer
operator|!=
name|b
condition|)
block|{
comment|// Now wrap this other buffer; with compound
comment|// file, we are repeatedly called with its
comment|// buffer, so we wrap it once and then re-use it
comment|// on subsequent calls
name|otherBuffer
operator|=
name|b
expr_stmt|;
name|otherByteBuf
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
else|else
name|otherByteBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|otherByteBuf
operator|.
name|limit
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|bb
operator|=
name|otherByteBuf
expr_stmt|;
block|}
else|else
comment|// Always wrap when offset != 0
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
name|long
name|pos
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
while|while
condition|(
name|bb
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
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
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"read past EOF"
argument_list|)
throw|;
name|pos
operator|+=
name|i
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

