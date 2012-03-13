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
name|util
operator|.
name|zip
operator|.
name|CRC32
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
import|;
end_import

begin_comment
comment|/** Writes bytes through to a primary IndexOutput, computing  *  checksum as it goes. Note that you cannot use seek().  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|ChecksumIndexInput
specifier|public
class|class
name|ChecksumIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|main
name|IndexInput
name|main
decl_stmt|;
DECL|field|digest
name|Checksum
name|digest
decl_stmt|;
DECL|method|ChecksumIndexInput
specifier|public
name|ChecksumIndexInput
parameter_list|(
name|IndexInput
name|main
parameter_list|)
block|{
name|super
argument_list|(
literal|"ChecksumIndexInput("
operator|+
name|main
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|main
operator|=
name|main
expr_stmt|;
name|digest
operator|=
operator|new
name|CRC32
argument_list|()
expr_stmt|;
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
specifier|final
name|byte
name|b
init|=
name|main
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
return|return
name|b
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
name|main
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
name|digest
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|getChecksum
specifier|public
name|long
name|getChecksum
parameter_list|()
block|{
return|return
name|digest
operator|.
name|getValue
argument_list|()
return|;
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
name|main
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|main
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|main
operator|.
name|length
argument_list|()
return|;
block|}
block|}
end_class

end_unit

