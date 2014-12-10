begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/** CompressionCodec that uses {@link CompressionMode#HIGH_COMPRESSION} */
end_comment

begin_class
DECL|class|HighCompressionCompressingCodec
specifier|public
class|class
name|HighCompressionCompressingCodec
extends|extends
name|CompressingCodec
block|{
comment|/** Constructor that allows to configure the chunk size. */
DECL|method|HighCompressionCompressingCodec
specifier|public
name|HighCompressionCompressingCodec
parameter_list|(
name|int
name|chunkSize
parameter_list|,
name|int
name|maxDocsPerChunk
parameter_list|,
name|boolean
name|withSegmentSuffix
parameter_list|)
block|{
name|super
argument_list|(
literal|"HighCompressionCompressingStoredFields"
argument_list|,
name|withSegmentSuffix
condition|?
literal|"HighCompressionCompressingStoredFields"
else|:
literal|""
argument_list|,
name|CompressionMode
operator|.
name|HIGH_COMPRESSION
argument_list|,
name|chunkSize
argument_list|,
name|maxDocsPerChunk
argument_list|)
expr_stmt|;
block|}
comment|/** Default constructor. */
DECL|method|HighCompressionCompressingCodec
specifier|public
name|HighCompressionCompressingCodec
parameter_list|()
block|{
comment|// we don't worry about zlib block overhead as its
comment|// not bad and try to save space instead:
name|this
argument_list|(
literal|61440
argument_list|,
literal|512
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

