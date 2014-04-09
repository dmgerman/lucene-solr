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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

