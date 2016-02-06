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
name|DataOutput
import|;
end_import

begin_comment
comment|/**  * A data compressor.  */
end_comment

begin_class
DECL|class|Compressor
specifier|public
specifier|abstract
class|class
name|Compressor
block|{
comment|/** Sole constructor, typically called from sub-classes. */
DECL|method|Compressor
specifier|protected
name|Compressor
parameter_list|()
block|{}
comment|/**    * Compress bytes into<code>out</code>. It it the responsibility of the    * compressor to add all necessary information so that a {@link Decompressor}    * will know when to stop decompressing bytes from the stream.    */
DECL|method|compress
specifier|public
specifier|abstract
name|void
name|compress
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

