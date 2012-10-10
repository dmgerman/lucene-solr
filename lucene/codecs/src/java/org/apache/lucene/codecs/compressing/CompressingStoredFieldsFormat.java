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
name|StoredFieldsReader
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
name|StoredFieldsWriter
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
name|lucene40
operator|.
name|Lucene40StoredFieldsFormat
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
name|MergePolicy
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
comment|/**  * A {@link StoredFieldsFormat} that is very similar to  * {@link Lucene40StoredFieldsFormat} but compresses documents in chunks in  * order to improve compression ratio.  *<p>  * For optimal performance, you should use a {@link MergePolicy} that returns  * segments that have the biggest byte size first.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|CompressingStoredFieldsFormat
specifier|public
class|class
name|CompressingStoredFieldsFormat
extends|extends
name|StoredFieldsFormat
block|{
DECL|field|storedFieldsIndex
specifier|private
specifier|final
name|CompressingStoredFieldsIndex
name|storedFieldsIndex
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
comment|/**    * Create a new {@link CompressingStoredFieldsFormat}.    *<p>    * The<code>compressionMode</code> parameter allows you to choose between    * compression algorithms that have various compression and uncompression    * speeds so that you can pick the one that best fits your indexing and    * searching throughput.    *<p>    *<code>chunkSize</code> is the minimum byte size of a chunk of documents.    * A value of<code>1</code> can make sense if there is redundancy across    * fields. In that case, both performance and compression ratio should be    * better than with {@link Lucene40StoredFieldsFormat} with compressed    * fields.    *<p>    * Higher values of<code>chunkSize</code> should improve the compression    * atio but will require more memory at indexing time and might make document    * loading a little slower (depending on the size of your OS cache compared    * to the size of your index).    *<p>    * The<code>storedFieldsIndex</code> parameter allows you to choose between    * several fields index implementations that offer various trade-offs between    * memory usage and speed.    *    * @param compressionMode the {@link CompressionMode} to use    * @param chunkSize the minimum number of bytes of a single chunk of stored documents    * @param storedFieldsIndex the fields index impl to use    * @see CompressionMode    * @see CompressingStoredFieldsIndex    */
DECL|method|CompressingStoredFieldsFormat
specifier|public
name|CompressingStoredFieldsFormat
parameter_list|(
name|CompressionMode
name|compressionMode
parameter_list|,
name|int
name|chunkSize
parameter_list|,
name|CompressingStoredFieldsIndex
name|storedFieldsIndex
parameter_list|)
block|{
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
name|this
operator|.
name|storedFieldsIndex
operator|=
name|storedFieldsIndex
expr_stmt|;
block|}
comment|/**    * Create a new {@link CompressingStoredFieldsFormat} with an in-memory    * {@link CompressingStoredFieldsIndex}.    *    * @see CompressingStoredFieldsFormat#CompressingStoredFieldsFormat(CompressionMode, int, CompressingStoredFieldsIndex)    */
DECL|method|CompressingStoredFieldsFormat
specifier|public
name|CompressingStoredFieldsFormat
parameter_list|(
name|CompressionMode
name|compressionMode
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
name|this
argument_list|(
name|compressionMode
argument_list|,
name|chunkSize
argument_list|,
name|chunkSize
operator|==
literal|1
condition|?
name|CompressingStoredFieldsIndex
operator|.
name|MEMORY_DOC
else|:
name|CompressingStoredFieldsIndex
operator|.
name|MEMORY_CHUNK
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new {@link CompressingStoredFieldsFormat} with    * {@link CompressionMode#FAST} compression and chunks of<tt>16 KB</tt>.    *    * @see CompressingStoredFieldsFormat#CompressingStoredFieldsFormat(CompressionMode, int)    */
DECL|method|CompressingStoredFieldsFormat
specifier|public
name|CompressingStoredFieldsFormat
parameter_list|()
block|{
name|this
argument_list|(
name|CompressionMode
operator|.
name|FAST
argument_list|,
literal|1
operator|<<
literal|14
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsReader
specifier|public
name|StoredFieldsReader
name|fieldsReader
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|FieldInfos
name|fn
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CompressingStoredFieldsReader
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|fn
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsWriter
specifier|public
name|StoredFieldsWriter
name|fieldsWriter
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|si
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CompressingStoredFieldsWriter
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|context
argument_list|,
name|compressionMode
argument_list|,
name|chunkSize
argument_list|,
name|storedFieldsIndex
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
literal|", storedFieldsIndex="
operator|+
name|storedFieldsIndex
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

