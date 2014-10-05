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
name|codecs
operator|.
name|CodecUtil
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
name|TermVectorsReader
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
name|TermVectorsWriter
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
name|index
operator|.
name|FieldInfos
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
name|index
operator|.
name|SegmentInfo
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
name|Directory
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
name|IOContext
import|;
end_import

begin_comment
comment|/**  * A {@link TermVectorsFormat} that compresses chunks of documents together in  * order to improve the compression ratio.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompressingTermVectorsFormat
specifier|public
class|class
name|CompressingTermVectorsFormat
extends|extends
name|TermVectorsFormat
block|{
DECL|field|formatName
specifier|private
specifier|final
name|String
name|formatName
decl_stmt|;
DECL|field|segmentSuffix
specifier|private
specifier|final
name|String
name|segmentSuffix
decl_stmt|;
DECL|field|compressionMode
specifier|private
specifier|final
name|CompressionMode
name|compressionMode
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|int
name|chunkSize
decl_stmt|;
comment|/**    * Create a new {@link CompressingTermVectorsFormat}.    *<p>    *<code>formatName</code> is the name of the format. This name will be used    * in the file formats to perform    * {@link CodecUtil#checkSegmentHeader codec header checks}.    *<p>    * The<code>compressionMode</code> parameter allows you to choose between    * compression algorithms that have various compression and decompression    * speeds so that you can pick the one that best fits your indexing and    * searching throughput. You should never instantiate two    * {@link CompressingTermVectorsFormat}s that have the same name but    * different {@link CompressionMode}s.    *<p>    *<code>chunkSize</code> is the minimum byte size of a chunk of documents.    * Higher values of<code>chunkSize</code> should improve the compression    * ratio but will require more memory at indexing time and might make document    * loading a little slower (depending on the size of your OS cache compared    * to the size of your index).    *    * @param formatName the name of the {@link StoredFieldsFormat}    * @param segmentSuffix a suffix to append to files created by this format    * @param compressionMode the {@link CompressionMode} to use    * @param chunkSize the minimum number of bytes of a single chunk of stored documents    * @see CompressionMode    */
DECL|method|CompressingTermVectorsFormat
specifier|public
name|CompressingTermVectorsFormat
parameter_list|(
name|String
name|formatName
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
name|this
operator|.
name|formatName
operator|=
name|formatName
expr_stmt|;
name|this
operator|.
name|segmentSuffix
operator|=
name|segmentSuffix
expr_stmt|;
name|this
operator|.
name|compressionMode
operator|=
name|compressionMode
expr_stmt|;
if|if
condition|(
name|chunkSize
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"chunkSize must be>= 1"
argument_list|)
throw|;
block|}
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|vectorsReader
specifier|public
specifier|final
name|TermVectorsReader
name|vectorsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CompressingTermVectorsReader
argument_list|(
name|directory
argument_list|,
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|fieldInfos
argument_list|,
name|context
argument_list|,
name|formatName
argument_list|,
name|compressionMode
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|vectorsWriter
specifier|public
specifier|final
name|TermVectorsWriter
name|vectorsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CompressingTermVectorsWriter
argument_list|(
name|directory
argument_list|,
name|segmentInfo
argument_list|,
name|segmentSuffix
argument_list|,
name|context
argument_list|,
name|formatName
argument_list|,
name|compressionMode
argument_list|,
name|chunkSize
argument_list|)
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(compressionMode="
operator|+
name|compressionMode
operator|+
literal|", chunkSize="
operator|+
name|chunkSize
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

