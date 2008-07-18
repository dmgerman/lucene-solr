begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/* IndexInput that knows how to read the byte slices written  * by Posting and PostingVector.  We read the bytes in  * each slice until we hit the end of that slice at which  * point we read the forwarding address of the next slice  * and then jump to it.*/
end_comment

begin_class
DECL|class|ByteSliceReader
specifier|final
class|class
name|ByteSliceReader
extends|extends
name|IndexInput
block|{
DECL|field|pool
name|ByteBlockPool
name|pool
decl_stmt|;
DECL|field|bufferUpto
name|int
name|bufferUpto
decl_stmt|;
DECL|field|buffer
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|upto
specifier|public
name|int
name|upto
decl_stmt|;
DECL|field|limit
name|int
name|limit
decl_stmt|;
DECL|field|level
name|int
name|level
decl_stmt|;
DECL|field|bufferOffset
specifier|public
name|int
name|bufferOffset
decl_stmt|;
DECL|field|endIndex
specifier|public
name|int
name|endIndex
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|ByteBlockPool
name|pool
parameter_list|,
name|int
name|startIndex
parameter_list|,
name|int
name|endIndex
parameter_list|)
block|{
assert|assert
name|endIndex
operator|-
name|startIndex
operator|>=
literal|0
assert|;
assert|assert
name|startIndex
operator|>=
literal|0
assert|;
assert|assert
name|endIndex
operator|>=
literal|0
assert|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|endIndex
operator|=
name|endIndex
expr_stmt|;
name|level
operator|=
literal|0
expr_stmt|;
name|bufferUpto
operator|=
name|startIndex
operator|/
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
expr_stmt|;
name|bufferOffset
operator|=
name|bufferUpto
operator|*
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
expr_stmt|;
name|buffer
operator|=
name|pool
operator|.
name|buffers
index|[
name|bufferUpto
index|]
expr_stmt|;
name|upto
operator|=
name|startIndex
operator|&
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_MASK
expr_stmt|;
specifier|final
name|int
name|firstSize
init|=
name|ByteBlockPool
operator|.
name|levelSizeArray
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|startIndex
operator|+
name|firstSize
operator|>=
name|endIndex
condition|)
block|{
comment|// There is only this one slice to read
name|limit
operator|=
name|endIndex
operator|&
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_MASK
expr_stmt|;
block|}
else|else
name|limit
operator|=
name|upto
operator|+
name|firstSize
operator|-
literal|4
expr_stmt|;
block|}
DECL|method|eof
specifier|public
name|boolean
name|eof
parameter_list|()
block|{
assert|assert
name|upto
operator|+
name|bufferOffset
operator|<=
name|endIndex
assert|;
return|return
name|upto
operator|+
name|bufferOffset
operator|==
name|endIndex
return|;
block|}
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
block|{
assert|assert
operator|!
name|eof
argument_list|()
assert|;
assert|assert
name|upto
operator|<=
name|limit
assert|;
if|if
condition|(
name|upto
operator|==
name|limit
condition|)
name|nextSlice
argument_list|()
expr_stmt|;
return|return
name|buffer
index|[
name|upto
operator|++
index|]
return|;
block|}
DECL|method|writeTo
specifier|public
name|long
name|writeTo
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|size
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|limit
operator|+
name|bufferOffset
operator|==
name|endIndex
condition|)
block|{
assert|assert
name|endIndex
operator|-
name|bufferOffset
operator|>=
name|upto
assert|;
name|out
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
name|upto
argument_list|,
name|limit
operator|-
name|upto
argument_list|)
expr_stmt|;
name|size
operator|+=
name|limit
operator|-
name|upto
expr_stmt|;
break|break;
block|}
else|else
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
name|upto
argument_list|,
name|limit
operator|-
name|upto
argument_list|)
expr_stmt|;
name|size
operator|+=
name|limit
operator|-
name|upto
expr_stmt|;
name|nextSlice
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|size
return|;
block|}
DECL|method|nextSlice
specifier|public
name|void
name|nextSlice
parameter_list|()
block|{
comment|// Skip to our next slice
specifier|final
name|int
name|nextIndex
init|=
operator|(
operator|(
name|buffer
index|[
name|limit
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator|+
operator|(
operator|(
name|buffer
index|[
literal|1
operator|+
name|limit
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator|+
operator|(
operator|(
name|buffer
index|[
literal|2
operator|+
name|limit
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|buffer
index|[
literal|3
operator|+
name|limit
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
name|level
operator|=
name|ByteBlockPool
operator|.
name|nextLevelArray
index|[
name|level
index|]
expr_stmt|;
specifier|final
name|int
name|newSize
init|=
name|ByteBlockPool
operator|.
name|levelSizeArray
index|[
name|level
index|]
decl_stmt|;
name|bufferUpto
operator|=
name|nextIndex
operator|/
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
expr_stmt|;
name|bufferOffset
operator|=
name|bufferUpto
operator|*
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_SIZE
expr_stmt|;
name|buffer
operator|=
name|pool
operator|.
name|buffers
index|[
name|bufferUpto
index|]
expr_stmt|;
name|upto
operator|=
name|nextIndex
operator|&
name|DocumentsWriter
operator|.
name|BYTE_BLOCK_MASK
expr_stmt|;
if|if
condition|(
name|nextIndex
operator|+
name|newSize
operator|>=
name|endIndex
condition|)
block|{
comment|// We are advancing to the final slice
assert|assert
name|endIndex
operator|-
name|nextIndex
operator|>
literal|0
assert|;
name|limit
operator|=
name|endIndex
operator|-
name|bufferOffset
expr_stmt|;
block|}
else|else
block|{
comment|// This is not the final slice (subtract 4 for the
comment|// forwarding address at the end of this new slice)
name|limit
operator|=
name|upto
operator|+
name|newSize
operator|-
literal|4
expr_stmt|;
block|}
block|}
DECL|method|readBytes
specifier|public
name|void
name|readBytes
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
block|{
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|numLeft
init|=
name|limit
operator|-
name|upto
decl_stmt|;
if|if
condition|(
name|numLeft
operator|<
name|len
condition|)
block|{
comment|// Read entire slice
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|upto
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|numLeft
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|numLeft
expr_stmt|;
name|len
operator|-=
name|numLeft
expr_stmt|;
name|nextSlice
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// This slice is the last one
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|upto
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|upto
operator|+=
name|len
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

