begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**   * Extension of IndexInput, computing checksum as it goes.   * Callers can retrieve the checksum via {@link #getChecksum()}.  */
end_comment

begin_class
DECL|class|ChecksumIndexInput
specifier|public
specifier|abstract
class|class
name|ChecksumIndexInput
extends|extends
name|IndexInput
block|{
comment|/** resourceDescription should be a non-null, opaque string    *  describing this resource; it's returned from    *  {@link #toString}. */
DECL|method|ChecksumIndexInput
specifier|protected
name|ChecksumIndexInput
parameter_list|(
name|String
name|resourceDescription
parameter_list|)
block|{
name|super
argument_list|(
name|resourceDescription
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the current checksum value */
DECL|method|getChecksum
specifier|public
specifier|abstract
name|long
name|getChecksum
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * {@inheritDoc}    *    * {@link ChecksumIndexInput} can only seek forward and seeks are expensive    * since they imply to read bytes in-between the current position and the    * target position in order to update the checksum.    */
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
specifier|final
name|long
name|curFP
init|=
name|getFilePointer
argument_list|()
decl_stmt|;
specifier|final
name|long
name|skip
init|=
name|pos
operator|-
name|curFP
decl_stmt|;
if|if
condition|(
name|skip
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|getClass
argument_list|()
operator|+
literal|" cannot seek backwards (pos="
operator|+
name|pos
operator|+
literal|" getFilePointer()="
operator|+
name|curFP
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|skipBytes
argument_list|(
name|skip
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

