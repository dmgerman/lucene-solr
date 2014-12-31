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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|OutputStream
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
name|CheckedOutputStream
import|;
end_import

begin_comment
comment|/** Implementation class for buffered {@link IndexOutput} that writes to an {@link OutputStream}. */
end_comment

begin_class
DECL|class|OutputStreamIndexOutput
specifier|public
class|class
name|OutputStreamIndexOutput
extends|extends
name|IndexOutput
block|{
DECL|field|crc
specifier|private
specifier|final
name|CRC32
name|crc
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
DECL|field|os
specifier|private
specifier|final
name|BufferedOutputStream
name|os
decl_stmt|;
DECL|field|bytesWritten
specifier|private
name|long
name|bytesWritten
init|=
literal|0L
decl_stmt|;
DECL|field|flushedOnClose
specifier|private
name|boolean
name|flushedOnClose
init|=
literal|false
decl_stmt|;
comment|/**    * Creates a new {@link OutputStreamIndexOutput} with the given buffer size.     * @param bufferSize the buffer size in bytes used to buffer writes internally.    * @throws IllegalArgumentException if the given buffer size is less or equal to<tt>0</tt>    */
DECL|method|OutputStreamIndexOutput
specifier|public
name|OutputStreamIndexOutput
parameter_list|(
name|String
name|resourceDescription
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|resourceDescription
argument_list|)
expr_stmt|;
name|this
operator|.
name|os
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|CheckedOutputStream
argument_list|(
name|out
argument_list|,
name|crc
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeByte
specifier|public
specifier|final
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|bytesWritten
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBytes
specifier|public
specifier|final
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bytesWritten
operator|+=
name|length
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
init|(
specifier|final
name|OutputStream
name|o
init|=
name|os
init|)
block|{
comment|// We want to make sure that os.flush() was running before close:
comment|// BufferedOutputStream may ignore IOExceptions while flushing on close().
comment|// We keep this also in Java 8, although it claims to be fixed there,
comment|// because there are more bugs around this! See:
comment|// # https://bugs.openjdk.java.net/browse/JDK-7015589
comment|// # https://bugs.openjdk.java.net/browse/JDK-8054565
if|if
condition|(
operator|!
name|flushedOnClose
condition|)
block|{
name|flushedOnClose
operator|=
literal|true
expr_stmt|;
comment|// set this BEFORE calling flush!
name|o
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
specifier|final
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|bytesWritten
return|;
block|}
annotation|@
name|Override
DECL|method|getChecksum
specifier|public
specifier|final
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
block|{
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|crc
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

