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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|codecs
operator|.
name|FilterCodec
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
name|codecs
operator|.
name|StoredFieldsFormat
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
name|codecs
operator|.
name|TermVectorsFormat
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
name|codecs
operator|.
name|compressing
operator|.
name|dummy
operator|.
name|DummyCompressingCodec
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
name|codecs
operator|.
name|lucene410
operator|.
name|Lucene410Codec
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
import|;
end_import

begin_comment
comment|/**  * A codec that uses {@link CompressingStoredFieldsFormat} for its stored  * fields and delegates to {@link Lucene410Codec} for everything else.  */
end_comment

begin_class
DECL|class|CompressingCodec
specifier|public
specifier|abstract
class|class
name|CompressingCodec
extends|extends
name|FilterCodec
block|{
comment|/**    * Create a random instance.    */
DECL|method|randomInstance
specifier|public
specifier|static
name|CompressingCodec
name|randomInstance
parameter_list|(
name|Random
name|random
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|boolean
name|withSegmentSuffix
parameter_list|)
block|{
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
operator|new
name|FastCompressingCodec
argument_list|(
name|chunkSize
argument_list|,
name|withSegmentSuffix
argument_list|)
return|;
case|case
literal|1
case|:
return|return
operator|new
name|FastDecompressionCompressingCodec
argument_list|(
name|chunkSize
argument_list|,
name|withSegmentSuffix
argument_list|)
return|;
case|case
literal|2
case|:
return|return
operator|new
name|HighCompressionCompressingCodec
argument_list|(
name|chunkSize
argument_list|,
name|withSegmentSuffix
argument_list|)
return|;
case|case
literal|3
case|:
return|return
operator|new
name|DummyCompressingCodec
argument_list|(
name|chunkSize
argument_list|,
name|withSegmentSuffix
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
comment|/**    * Creates a random {@link CompressingCodec} that is using an empty segment     * suffix    */
DECL|method|randomInstance
specifier|public
specifier|static
name|CompressingCodec
name|randomInstance
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
return|return
name|randomInstance
argument_list|(
name|random
argument_list|,
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Creates a random {@link CompressingCodec} that is using a segment suffix    */
DECL|method|randomInstance
specifier|public
specifier|static
name|CompressingCodec
name|randomInstance
parameter_list|(
name|Random
name|random
parameter_list|,
name|boolean
name|withSegmentSuffix
parameter_list|)
block|{
return|return
name|randomInstance
argument_list|(
name|random
argument_list|,
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|)
argument_list|,
name|withSegmentSuffix
argument_list|)
return|;
block|}
DECL|field|storedFieldsFormat
specifier|private
specifier|final
name|CompressingStoredFieldsFormat
name|storedFieldsFormat
decl_stmt|;
DECL|field|termVectorsFormat
specifier|private
specifier|final
name|CompressingTermVectorsFormat
name|termVectorsFormat
decl_stmt|;
comment|/**    * Creates a compressing codec with a given segment suffix    */
DECL|method|CompressingCodec
specifier|public
name|CompressingCodec
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|CompressionMode
name|compressionMode
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
operator|new
name|Lucene410Codec
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|storedFieldsFormat
operator|=
operator|new
name|CompressingStoredFieldsFormat
argument_list|(
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|compressionMode
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|termVectorsFormat
operator|=
operator|new
name|CompressingTermVectorsFormat
argument_list|(
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|compressionMode
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a compressing codec with an empty segment suffix    */
DECL|method|CompressingCodec
specifier|public
name|CompressingCodec
parameter_list|(
name|String
name|name
parameter_list|,
name|CompressionMode
name|compressionMode
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
name|compressionMode
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storedFieldsFormat
specifier|public
name|StoredFieldsFormat
name|storedFieldsFormat
parameter_list|()
block|{
return|return
name|storedFieldsFormat
return|;
block|}
annotation|@
name|Override
DECL|method|termVectorsFormat
specifier|public
name|TermVectorsFormat
name|termVectorsFormat
parameter_list|()
block|{
return|return
name|termVectorsFormat
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
name|getName
argument_list|()
operator|+
literal|"(storedFieldsFormat="
operator|+
name|storedFieldsFormat
operator|+
literal|", termVectorsFormat="
operator|+
name|termVectorsFormat
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

