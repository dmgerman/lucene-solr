begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.pfor
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pfor
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IntBuffer
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
name|IndexOutput
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
name|codecs
operator|.
name|sep
operator|.
name|IntStreamFactory
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
name|codecs
operator|.
name|sep
operator|.
name|IntIndexInput
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
name|codecs
operator|.
name|sep
operator|.
name|IntIndexOutput
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
name|codecs
operator|.
name|intblock
operator|.
name|FixedIntBlockIndexInput
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
name|codecs
operator|.
name|intblock
operator|.
name|FixedIntBlockIndexOutput
import|;
end_import

begin_comment
comment|/**   * Stuff to pass to PostingsReader/WriterBase.  * Things really make sense are: flushBlock() and readBlock()  */
end_comment

begin_class
DECL|class|ForFactory
specifier|public
specifier|final
class|class
name|ForFactory
extends|extends
name|IntStreamFactory
block|{
DECL|field|blockSize
specifier|private
specifier|final
name|int
name|blockSize
decl_stmt|;
DECL|method|ForFactory
specifier|public
name|ForFactory
parameter_list|()
block|{
name|this
operator|.
name|blockSize
operator|=
name|ForPostingsFormat
operator|.
name|DEFAULT_BLOCK_SIZE
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|IntIndexOutput
name|createOutput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FixedIntBlockIndexOutput
name|ret
init|=
operator|new
name|ForIndexOutput
argument_list|(
name|out
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
comment|// TODO: why handle exception like this?
comment|// and why not use similar codes for read part?
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|IntIndexInput
name|openInput
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|fileName
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|FixedIntBlockIndexInput
name|ret
init|=
operator|new
name|ForIndexInput
argument_list|(
name|dir
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
comment|// wrap input and output with buffer support
DECL|class|ForIndexInput
specifier|private
class|class
name|ForIndexInput
extends|extends
name|FixedIntBlockIndexInput
block|{
DECL|method|ForIndexInput
name|ForIndexInput
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|class|ForBlockReader
class|class
name|ForBlockReader
implements|implements
name|FixedIntBlockIndexInput
operator|.
name|BlockReader
block|{
DECL|field|encoded
specifier|private
specifier|final
name|byte
index|[]
name|encoded
decl_stmt|;
DECL|field|buffer
specifier|private
specifier|final
name|int
index|[]
name|buffer
decl_stmt|;
DECL|field|in
specifier|private
specifier|final
name|IndexInput
name|in
decl_stmt|;
DECL|field|encodedBuffer
specifier|private
specifier|final
name|IntBuffer
name|encodedBuffer
decl_stmt|;
DECL|method|ForBlockReader
name|ForBlockReader
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|,
specifier|final
name|int
index|[]
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|encoded
operator|=
operator|new
name|byte
index|[
name|blockSize
operator|*
literal|8
operator|+
literal|4
index|]
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|this
operator|.
name|encodedBuffer
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|encoded
argument_list|)
operator|.
name|asIntBuffer
argument_list|()
expr_stmt|;
block|}
comment|// TODO: implement public void skipBlock() {} ?
annotation|@
name|Override
DECL|method|readBlock
specifier|public
name|void
name|readBlock
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBytes
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
assert|assert
name|numBytes
operator|<=
name|blockSize
operator|*
literal|8
operator|+
literal|4
assert|;
name|in
operator|.
name|readBytes
argument_list|(
name|encoded
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|ForUtil
operator|.
name|decompress
argument_list|(
name|encodedBuffer
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBlockReader
specifier|protected
name|BlockReader
name|getBlockReader
parameter_list|(
specifier|final
name|IndexInput
name|in
parameter_list|,
specifier|final
name|int
index|[]
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ForBlockReader
argument_list|(
name|in
argument_list|,
name|buffer
argument_list|)
return|;
block|}
block|}
DECL|class|ForIndexOutput
specifier|private
class|class
name|ForIndexOutput
extends|extends
name|FixedIntBlockIndexOutput
block|{
DECL|field|encoded
specifier|private
name|byte
index|[]
name|encoded
decl_stmt|;
DECL|field|encodedBuffer
specifier|private
name|IntBuffer
name|encodedBuffer
decl_stmt|;
DECL|method|ForIndexOutput
name|ForIndexOutput
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|int
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|encoded
operator|=
operator|new
name|byte
index|[
name|blockSize
operator|*
literal|8
operator|+
literal|4
index|]
expr_stmt|;
name|this
operator|.
name|encodedBuffer
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|encoded
argument_list|)
operator|.
name|asIntBuffer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flushBlock
specifier|protected
name|void
name|flushBlock
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numBytes
init|=
name|ForUtil
operator|.
name|compress
argument_list|(
name|buffer
argument_list|,
name|buffer
operator|.
name|length
argument_list|,
name|encodedBuffer
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|encoded
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

