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
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Used by MockDirectoryWrapper to create an input stream that  * keeps track of when it's been closed.  */
end_comment

begin_class
DECL|class|MockIndexInputWrapper
specifier|public
class|class
name|MockIndexInputWrapper
extends|extends
name|IndexInput
block|{
DECL|field|dir
specifier|private
name|MockDirectoryWrapper
name|dir
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|delegate
specifier|private
name|IndexInput
name|delegate
decl_stmt|;
DECL|field|isClone
specifier|private
name|boolean
name|isClone
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
decl_stmt|;
comment|/** Construct an empty output buffer. */
DECL|method|MockIndexInputWrapper
specifier|public
name|MockIndexInputWrapper
parameter_list|(
name|MockDirectoryWrapper
name|dir
parameter_list|,
name|String
name|name
parameter_list|,
name|IndexInput
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
literal|"MockIndexInputWrapper(name="
operator|+
name|name
operator|+
literal|" delegate="
operator|+
name|delegate
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
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
block|{
try|try
block|{
comment|// turn on the following to look for leaks closing inputs,
comment|// after fixing TestTransactions
comment|// dir.maybeThrowDeterministicException();
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|delegate
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Pending resolution on LUCENE-686 we may want to
comment|// remove the conditional check so we also track that
comment|// all clones get closed:
if|if
condition|(
operator|!
name|isClone
condition|)
block|{
name|dir
operator|.
name|removeIndexInput
argument_list|(
name|this
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Abusing closed IndexInput!"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MockIndexInputWrapper
name|clone
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|dir
operator|.
name|inputCloneCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|IndexInput
name|iiclone
init|=
operator|(
name|IndexInput
operator|)
name|delegate
operator|.
name|clone
argument_list|()
decl_stmt|;
name|MockIndexInputWrapper
name|clone
init|=
operator|new
name|MockIndexInputWrapper
argument_list|(
name|dir
argument_list|,
name|name
argument_list|,
name|iiclone
argument_list|)
decl_stmt|;
name|clone
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
comment|// Pending resolution on LUCENE-686 we may want to
comment|// uncomment this code so that we also track that all
comment|// clones get closed:
comment|/*     synchronized(dir.openFiles) {       if (dir.openFiles.containsKey(name)) {         Integer v = (Integer) dir.openFiles.get(name);         v = Integer.valueOf(v.intValue()+1);         dir.openFiles.put(name, v);       } else {         throw new RuntimeException("BUG: cloned file was not open?");       }     }     */
return|return
name|clone
return|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|getFilePointer
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readByte
argument_list|()
return|;
block|}
annotation|@
name|Override
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
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|copyBytes
specifier|public
name|void
name|copyBytes
parameter_list|(
name|IndexOutput
name|out
parameter_list|,
name|long
name|numBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|copyBytes
argument_list|(
name|out
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
parameter_list|,
name|boolean
name|useBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|delegate
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
name|useBuffer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readShort
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readInt
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readString
specifier|public
name|String
name|readString
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readStringStringMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|readStringStringMap
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readStringStringMap
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readVInt
specifier|public
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readVInt
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|readVLong
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MockIndexInputWrapper("
operator|+
name|delegate
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

