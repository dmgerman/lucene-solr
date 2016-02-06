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
comment|/**   * Random Access Index API.  * Unlike {@link IndexInput}, this has no concept of file position, all reads  * are absolute. However, like IndexInput, it is only intended for use by a single thread.  */
end_comment

begin_interface
DECL|interface|RandomAccessInput
specifier|public
interface|interface
name|RandomAccessInput
block|{
comment|/**     * Reads a byte at the given position in the file    * @see DataInput#readByte    */
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Reads a short at the given position in the file    * @see DataInput#readShort    */
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Reads an integer at the given position in the file    * @see DataInput#readInt    */
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**     * Reads a long at the given position in the file    * @see DataInput#readLong    */
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

