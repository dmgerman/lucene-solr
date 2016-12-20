begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene50
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene50
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
name|java
operator|.
name|util
operator|.
name|Objects
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
name|compressing
operator|.
name|CompressingStoredFieldsFormat
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
name|CompressingStoredFieldsIndexWriter
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
name|CompressionMode
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
name|index
operator|.
name|StoredFieldVisitor
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * Lucene 5.0 stored fields format.  *  *<p><b>Principle</b>  *<p>This {@link StoredFieldsFormat} compresses blocks of documents in  * order to improve the compression ratio compared to document-level  * compression. It uses the<a href="http://code.google.com/p/lz4/">LZ4</a>  * compression algorithm by default in 16KB blocks, which is fast to compress   * and very fast to decompress data. Although the default compression method   * that is used ({@link Mode#BEST_SPEED BEST_SPEED}) focuses more on speed than on   * compression ratio, it should provide interesting compression ratios  * for redundant inputs (such as log files, HTML or plain text). For higher  * compression, you can choose ({@link Mode#BEST_COMPRESSION BEST_COMPRESSION}), which uses   * the<a href="http://en.wikipedia.org/wiki/DEFLATE">DEFLATE</a> algorithm with 60KB blocks   * for a better ratio at the expense of slower performance.   * These two options can be configured like this:  *<pre class="prettyprint">  *   // the default: for high performance  *   indexWriterConfig.setCodec(new Lucene54Codec(Mode.BEST_SPEED));  *   // instead for higher performance (but slower):  *   // indexWriterConfig.setCodec(new Lucene54Codec(Mode.BEST_COMPRESSION));  *</pre>  *<p><b>File formats</b>  *<p>Stored fields are represented by two files:  *<ol>  *<li><a name="field_data"></a>  *<p>A fields data file (extension<tt>.fdt</tt>). This file stores a compact  * representation of documents in compressed blocks of 16KB or more. When  * writing a segment, documents are appended to an in-memory<tt>byte[]</tt>  * buffer. When its size reaches 16KB or more, some metadata about the documents  * is flushed to disk, immediately followed by a compressed representation of  * the buffer using the  *<a href="http://code.google.com/p/lz4/">LZ4</a>  *<a href="http://fastcompression.blogspot.fr/2011/05/lz4-explained.html">compression format</a>.</p>  *<p>Here is a more detailed description of the field data file format:</p>  *<ul>  *<li>FieldData (.fdt) --&gt;&lt;Header&gt;, PackedIntsVersion,&lt;Chunk&gt;<sup>ChunkCount</sup>, ChunkCount, DirtyChunkCount, Footer</li>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>PackedIntsVersion --&gt; {@link PackedInts#VERSION_CURRENT} as a {@link DataOutput#writeVInt VInt}</li>  *<li>ChunkCount is not known in advance and is the number of chunks necessary to store all document of the segment</li>  *<li>Chunk --&gt; DocBase, ChunkDocs, DocFieldCounts, DocLengths,&lt;CompressedDocs&gt;</li>  *<li>DocBase --&gt; the ID of the first document of the chunk as a {@link DataOutput#writeVInt VInt}</li>  *<li>ChunkDocs --&gt; the number of documents in the chunk as a {@link DataOutput#writeVInt VInt}</li>  *<li>DocFieldCounts --&gt; the number of stored fields of every document in the chunk, encoded as followed:<ul>  *<li>if chunkDocs=1, the unique value is encoded as a {@link DataOutput#writeVInt VInt}</li>  *<li>else read a {@link DataOutput#writeVInt VInt} (let's call it<tt>bitsRequired</tt>)<ul>  *<li>if<tt>bitsRequired</tt> is<tt>0</tt> then all values are equal, and the common value is the following {@link DataOutput#writeVInt VInt}</li>  *<li>else<tt>bitsRequired</tt> is the number of bits required to store any value, and values are stored in a {@link PackedInts packed} array where every value is stored on exactly<tt>bitsRequired</tt> bits</li>  *</ul></li>  *</ul></li>  *<li>DocLengths --&gt; the lengths of all documents in the chunk, encoded with the same method as DocFieldCounts</li>  *<li>CompressedDocs --&gt; a compressed representation of&lt;Docs&gt; using the LZ4 compression format</li>  *<li>Docs --&gt;&lt;Doc&gt;<sup>ChunkDocs</sup></li>  *<li>Doc --&gt;&lt;FieldNumAndType, Value&gt;<sup>DocFieldCount</sup></li>  *<li>FieldNumAndType --&gt; a {@link DataOutput#writeVLong VLong}, whose 3 last bits are Type and other bits are FieldNum</li>  *<li>Type --&gt;<ul>  *<li>0: Value is String</li>  *<li>1: Value is BinaryValue</li>  *<li>2: Value is Int</li>  *<li>3: Value is Float</li>  *<li>4: Value is Long</li>  *<li>5: Value is Double</li>  *<li>6, 7: unused</li>  *</ul></li>  *<li>FieldNum --&gt; an ID of the field</li>  *<li>Value --&gt; {@link DataOutput#writeString(String) String} | BinaryValue | Int | Float | Long | Double depending on Type</li>  *<li>BinaryValue --&gt; ValueLength&lt;Byte&gt;<sup>ValueLength</sup></li>  *<li>ChunkCount --&gt; the number of chunks in this file</li>  *<li>DirtyChunkCount --&gt; the number of prematurely flushed chunks in this file</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *<p>Notes  *<ul>  *<li>If documents are larger than 16KB then chunks will likely contain only  * one document. However, documents can never spread across several chunks (all  * fields of a single document are in the same chunk).</li>  *<li>When at least one document in a chunk is large enough so that the chunk  * is larger than 32KB, the chunk will actually be compressed in several LZ4  * blocks of 16KB. This allows {@link StoredFieldVisitor}s which are only  * interested in the first fields of a document to not have to decompress 10MB  * of data if the document is 10MB, but only 16KB.</li>  *<li>Given that the original lengths are written in the metadata of the chunk,  * the decompressor can leverage this information to stop decoding as soon as  * enough data has been decompressed.</li>  *<li>In case documents are incompressible, CompressedDocs will be less than  * 0.5% larger than Docs.</li>  *</ul>  *</li>  *<li><a name="field_index"></a>  *<p>A fields index file (extension<tt>.fdx</tt>).</p>  *<ul>  *<li>FieldsIndex (.fdx) --&gt;&lt;Header&gt;,&lt;ChunkIndex&gt;, Footer</li>  *<li>Header --&gt; {@link CodecUtil#writeIndexHeader IndexHeader}</li>  *<li>ChunkIndex: See {@link CompressingStoredFieldsIndexWriter}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  *</li>  *</ol>  *<p><b>Known limitations</b>  *<p>This {@link StoredFieldsFormat} does not support individual documents  * larger than (<tt>2<sup>31</sup> - 2<sup>14</sup></tt>) bytes.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene50StoredFieldsFormat
specifier|public
specifier|final
class|class
name|Lucene50StoredFieldsFormat
extends|extends
name|StoredFieldsFormat
block|{
comment|/** Configuration option for stored fields. */
DECL|enum|Mode
specifier|public
specifier|static
enum|enum
name|Mode
block|{
comment|/** Trade compression ratio for retrieval speed. */
DECL|enum constant|BEST_SPEED
name|BEST_SPEED
block|,
comment|/** Trade retrieval speed for compression ratio. */
DECL|enum constant|BEST_COMPRESSION
name|BEST_COMPRESSION
block|}
comment|/** Attribute key for compression mode. */
DECL|field|MODE_KEY
specifier|public
specifier|static
specifier|final
name|String
name|MODE_KEY
init|=
name|Lucene50StoredFieldsFormat
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".mode"
decl_stmt|;
DECL|field|mode
specifier|final
name|Mode
name|mode
decl_stmt|;
comment|/** Stored fields format with default options */
DECL|method|Lucene50StoredFieldsFormat
specifier|public
name|Lucene50StoredFieldsFormat
parameter_list|()
block|{
name|this
argument_list|(
name|Mode
operator|.
name|BEST_SPEED
argument_list|)
expr_stmt|;
block|}
comment|/** Stored fields format with specified mode */
DECL|method|Lucene50StoredFieldsFormat
specifier|public
name|Lucene50StoredFieldsFormat
parameter_list|(
name|Mode
name|mode
parameter_list|)
block|{
name|this
operator|.
name|mode
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mode
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
name|String
name|value
init|=
name|si
operator|.
name|getAttribute
argument_list|(
name|MODE_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"missing value for "
operator|+
name|MODE_KEY
operator|+
literal|" for segment: "
operator|+
name|si
operator|.
name|name
argument_list|)
throw|;
block|}
name|Mode
name|mode
init|=
name|Mode
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
name|impl
argument_list|(
name|mode
argument_list|)
operator|.
name|fieldsReader
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
name|String
name|previous
init|=
name|si
operator|.
name|putAttribute
argument_list|(
name|MODE_KEY
argument_list|,
name|mode
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
operator|&&
name|previous
operator|.
name|equals
argument_list|(
name|mode
operator|.
name|name
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"found existing value for "
operator|+
name|MODE_KEY
operator|+
literal|" for segment: "
operator|+
name|si
operator|.
name|name
operator|+
literal|"old="
operator|+
name|previous
operator|+
literal|", new="
operator|+
name|mode
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|impl
argument_list|(
name|mode
argument_list|)
operator|.
name|fieldsWriter
argument_list|(
name|directory
argument_list|,
name|si
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|impl
name|StoredFieldsFormat
name|impl
parameter_list|(
name|Mode
name|mode
parameter_list|)
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|BEST_SPEED
case|:
return|return
operator|new
name|CompressingStoredFieldsFormat
argument_list|(
literal|"Lucene50StoredFieldsFast"
argument_list|,
name|CompressionMode
operator|.
name|FAST
argument_list|,
literal|1
operator|<<
literal|14
argument_list|,
literal|128
argument_list|,
literal|1024
argument_list|)
return|;
case|case
name|BEST_COMPRESSION
case|:
return|return
operator|new
name|CompressingStoredFieldsFormat
argument_list|(
literal|"Lucene50StoredFieldsHigh"
argument_list|,
name|CompressionMode
operator|.
name|HIGH_COMPRESSION
argument_list|,
literal|61440
argument_list|,
literal|512
argument_list|,
literal|1024
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
block|}
end_class

end_unit

