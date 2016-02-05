begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.compressing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|compressing
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataInput
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
name|BytesRef
import|;
end_import

begin_comment
comment|/**  * A decompressor.  */
end_comment

begin_class
DECL|class|Decompressor
specifier|public
specifier|abstract
class|class
name|Decompressor
implements|implements
name|Cloneable
block|{
comment|/** Sole constructor, typically called from sub-classes. */
DECL|method|Decompressor
specifier|protected
name|Decompressor
parameter_list|()
block|{}
comment|/**    * Decompress bytes that were stored between offsets<code>offset</code> and    *<code>offset+length</code> in the original stream from the compressed    * stream<code>in</code> to<code>bytes</code>. After returning, the length    * of<code>bytes</code> (<code>bytes.length</code>) must be equal to    *<code>length</code>. Implementations of this method are free to resize    *<code>bytes</code> depending on their needs.    *    * @param in the input that stores the compressed stream    * @param originalLength the length of the original data (before compression)    * @param offset bytes before this offset do not need to be decompressed    * @param length bytes after<code>offset+length</code> do not need to be decompressed    * @param bytes a {@link BytesRef} where to store the decompressed data    */
DECL|method|decompress
specifier|public
specifier|abstract
name|void
name|decompress
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|originalLength
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|clone
specifier|public
specifier|abstract
name|Decompressor
name|clone
parameter_list|()
function_decl|;
block|}
end_class

end_unit

