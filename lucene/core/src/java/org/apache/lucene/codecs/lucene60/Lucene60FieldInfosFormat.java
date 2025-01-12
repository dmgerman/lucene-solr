begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene60
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene60
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|DocValuesFormat
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
name|FieldInfosFormat
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
name|CorruptIndexException
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
name|DocValuesType
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
name|FieldInfo
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
name|IndexFileNames
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
name|IndexOptions
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
name|ChecksumIndexInput
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
name|store
operator|.
name|IndexInput
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
name|IndexOutput
import|;
end_import

begin_comment
comment|/**  * Lucene 6.0 Field Infos format.  *<p>Field names are stored in the field info file, with suffix<tt>.fnm</tt>.  *<p>FieldInfos (.fnm) --&gt; Header,FieldsCount,&lt;FieldName,FieldNumber,  * FieldBits,DocValuesBits,DocValuesGen,Attributes,DimensionCount,DimensionNumBytes&gt;<sup>FieldsCount</sup>,Footer  *<p>Data types:  *<ul>  *<li>Header --&gt; {@link CodecUtil#checkIndexHeader IndexHeader}</li>  *<li>FieldsCount --&gt; {@link DataOutput#writeVInt VInt}</li>  *<li>FieldName --&gt; {@link DataOutput#writeString String}</li>  *<li>FieldBits, IndexOptions, DocValuesBits --&gt; {@link DataOutput#writeByte Byte}</li>  *<li>FieldNumber, DimensionCount, DimensionNumBytes --&gt; {@link DataOutput#writeInt VInt}</li>  *<li>Attributes --&gt; {@link DataOutput#writeMapOfStrings Map&lt;String,String&gt;}</li>  *<li>DocValuesGen --&gt; {@link DataOutput#writeLong(long) Int64}</li>  *<li>Footer --&gt; {@link CodecUtil#writeFooter CodecFooter}</li>  *</ul>  * Field Descriptions:  *<ul>  *<li>FieldsCount: the number of fields in this file.</li>  *<li>FieldName: name of the field as a UTF-8 String.</li>  *<li>FieldNumber: the field's number. Note that unlike previous versions of  *       Lucene, the fields are not numbered implicitly by their order in the  *       file, instead explicitly.</li>  *<li>FieldBits: a byte containing field options.  *<ul>  *<li>The low order bit (0x1) is one for fields that have term vectors  *           stored, and zero for fields without term vectors.</li>  *<li>If the second lowest order-bit is set (0x2), norms are omitted for the  *           indexed field.</li>  *<li>If the third lowest-order bit is set (0x4), payloads are stored for the  *           indexed field.</li>  *</ul>  *</li>  *<li>IndexOptions: a byte containing index options.  *<ul>  *<li>0: not indexed</li>  *<li>1: indexed as DOCS_ONLY</li>  *<li>2: indexed as DOCS_AND_FREQS</li>  *<li>3: indexed as DOCS_AND_FREQS_AND_POSITIONS</li>  *<li>4: indexed as DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS</li>  *</ul>  *</li>  *<li>DocValuesBits: a byte containing per-document value types. The type  *       recorded as two four-bit integers, with the high-order bits representing  *<code>norms</code> options, and the low-order bits representing   *       {@code DocValues} options. Each four-bit integer can be decoded as such:  *<ul>  *<li>0: no DocValues for this field.</li>  *<li>1: NumericDocValues. ({@link DocValuesType#NUMERIC})</li>  *<li>2: BinaryDocValues. ({@code DocValuesType#BINARY})</li>  *<li>3: SortedDocValues. ({@code DocValuesType#SORTED})</li>  *</ul>  *</li>  *<li>DocValuesGen is the generation count of the field's DocValues. If this is -1,  *       there are no DocValues updates to that field. Anything above zero means there   *       are updates stored by {@link DocValuesFormat}.</li>  *<li>Attributes: a key-value map of codec-private attributes.</li>  *<li>PointDimensionCount, PointNumBytes: these are non-zero only if the field is  *       indexed as points, e.g. using {@link org.apache.lucene.document.LongPoint}</li>  *</ul>  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene60FieldInfosFormat
specifier|public
specifier|final
class|class
name|Lucene60FieldInfosFormat
extends|extends
name|FieldInfosFormat
block|{
comment|/** Sole constructor. */
DECL|method|Lucene60FieldInfosFormat
specifier|public
name|Lucene60FieldInfosFormat
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|read
specifier|public
name|FieldInfos
name|read
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|ChecksumIndexInput
name|input
init|=
name|directory
operator|.
name|openChecksumInput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
init|)
block|{
name|Throwable
name|priorE
init|=
literal|null
decl_stmt|;
name|FieldInfo
name|infos
index|[]
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|input
argument_list|,
name|Lucene60FieldInfosFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene60FieldInfosFormat
operator|.
name|FORMAT_START
argument_list|,
name|Lucene60FieldInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|,
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
comment|//read in the size
name|infos
operator|=
operator|new
name|FieldInfo
index|[
name|size
index|]
expr_stmt|;
comment|// previous field's attribute map, we share when possible:
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|lastAttributes
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|input
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fieldNumber
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldNumber
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid field number for field: "
operator|+
name|name
operator|+
literal|", fieldNumber="
operator|+
name|fieldNumber
argument_list|,
name|input
argument_list|)
throw|;
block|}
name|byte
name|bits
init|=
name|input
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|storeTermVector
init|=
operator|(
name|bits
operator|&
name|STORE_TERMVECTOR
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|omitNorms
init|=
operator|(
name|bits
operator|&
name|OMIT_NORMS
operator|)
operator|!=
literal|0
decl_stmt|;
name|boolean
name|storePayloads
init|=
operator|(
name|bits
operator|&
name|STORE_PAYLOADS
operator|)
operator|!=
literal|0
decl_stmt|;
specifier|final
name|IndexOptions
name|indexOptions
init|=
name|getIndexOptions
argument_list|(
name|input
argument_list|,
name|input
operator|.
name|readByte
argument_list|()
argument_list|)
decl_stmt|;
comment|// DV Types are packed in one byte
specifier|final
name|DocValuesType
name|docValuesType
init|=
name|getDocValuesType
argument_list|(
name|input
argument_list|,
name|input
operator|.
name|readByte
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|long
name|dvGen
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|input
operator|.
name|readMapOfStrings
argument_list|()
decl_stmt|;
comment|// just use the last field's map if its the same
if|if
condition|(
name|attributes
operator|.
name|equals
argument_list|(
name|lastAttributes
argument_list|)
condition|)
block|{
name|attributes
operator|=
name|lastAttributes
expr_stmt|;
block|}
name|lastAttributes
operator|=
name|attributes
expr_stmt|;
name|int
name|pointDimensionCount
init|=
name|input
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|int
name|pointNumBytes
decl_stmt|;
if|if
condition|(
name|pointDimensionCount
operator|!=
literal|0
condition|)
block|{
name|pointNumBytes
operator|=
name|input
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|pointNumBytes
operator|=
literal|0
expr_stmt|;
block|}
try|try
block|{
name|infos
index|[
name|i
index|]
operator|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|,
name|dvGen
argument_list|,
name|attributes
argument_list|,
name|pointDimensionCount
argument_list|,
name|pointNumBytes
argument_list|)
expr_stmt|;
name|infos
index|[
name|i
index|]
operator|.
name|checkConsistency
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid fieldinfo for field: "
operator|+
name|name
operator|+
literal|", fieldNumber="
operator|+
name|fieldNumber
argument_list|,
name|input
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|exception
parameter_list|)
block|{
name|priorE
operator|=
name|exception
expr_stmt|;
block|}
finally|finally
block|{
name|CodecUtil
operator|.
name|checkFooter
argument_list|(
name|input
argument_list|,
name|priorE
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldInfos
argument_list|(
name|infos
argument_list|)
return|;
block|}
block|}
static|static
block|{
comment|// We "mirror" DocValues enum values with the constants below; let's try to ensure if we add a new DocValuesType while this format is
comment|// still used for writing, we remember to fix this encoding:
assert|assert
name|DocValuesType
operator|.
name|values
argument_list|()
operator|.
name|length
operator|==
literal|6
assert|;
block|}
DECL|method|docValuesByte
specifier|private
specifier|static
name|byte
name|docValuesByte
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NONE
case|:
return|return
literal|0
return|;
case|case
name|NUMERIC
case|:
return|return
literal|1
return|;
case|case
name|BINARY
case|:
return|return
literal|2
return|;
case|case
name|SORTED
case|:
return|return
literal|3
return|;
case|case
name|SORTED_SET
case|:
return|return
literal|4
return|;
case|case
name|SORTED_NUMERIC
case|:
return|return
literal|5
return|;
default|default:
comment|// BUG
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unhandled DocValuesType: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
DECL|method|getDocValuesType
specifier|private
specifier|static
name|DocValuesType
name|getDocValuesType
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|b
condition|)
block|{
case|case
literal|0
case|:
return|return
name|DocValuesType
operator|.
name|NONE
return|;
case|case
literal|1
case|:
return|return
name|DocValuesType
operator|.
name|NUMERIC
return|;
case|case
literal|2
case|:
return|return
name|DocValuesType
operator|.
name|BINARY
return|;
case|case
literal|3
case|:
return|return
name|DocValuesType
operator|.
name|SORTED
return|;
case|case
literal|4
case|:
return|return
name|DocValuesType
operator|.
name|SORTED_SET
return|;
case|case
literal|5
case|:
return|return
name|DocValuesType
operator|.
name|SORTED_NUMERIC
return|;
default|default:
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid docvalues byte: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
block|}
static|static
block|{
comment|// We "mirror" IndexOptions enum values with the constants below; let's try to ensure if we add a new IndexOption while this format is
comment|// still used for writing, we remember to fix this encoding:
assert|assert
name|IndexOptions
operator|.
name|values
argument_list|()
operator|.
name|length
operator|==
literal|5
assert|;
block|}
DECL|method|indexOptionsByte
specifier|private
specifier|static
name|byte
name|indexOptionsByte
parameter_list|(
name|IndexOptions
name|indexOptions
parameter_list|)
block|{
switch|switch
condition|(
name|indexOptions
condition|)
block|{
case|case
name|NONE
case|:
return|return
literal|0
return|;
case|case
name|DOCS
case|:
return|return
literal|1
return|;
case|case
name|DOCS_AND_FREQS
case|:
return|return
literal|2
return|;
case|case
name|DOCS_AND_FREQS_AND_POSITIONS
case|:
return|return
literal|3
return|;
case|case
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
case|:
return|return
literal|4
return|;
default|default:
comment|// BUG:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unhandled IndexOptions: "
operator|+
name|indexOptions
argument_list|)
throw|;
block|}
block|}
DECL|method|getIndexOptions
specifier|private
specifier|static
name|IndexOptions
name|getIndexOptions
parameter_list|(
name|IndexInput
name|input
parameter_list|,
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|b
condition|)
block|{
case|case
literal|0
case|:
return|return
name|IndexOptions
operator|.
name|NONE
return|;
case|case
literal|1
case|:
return|return
name|IndexOptions
operator|.
name|DOCS
return|;
case|case
literal|2
case|:
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
return|;
case|case
literal|3
case|:
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
return|;
case|case
literal|4
case|:
return|return
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
return|;
default|default:
comment|// BUG
throw|throw
operator|new
name|CorruptIndexException
argument_list|(
literal|"invalid IndexOptions byte: "
operator|+
name|b
argument_list|,
name|input
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|String
name|segmentSuffix
parameter_list|,
name|FieldInfos
name|infos
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|segmentInfo
operator|.
name|name
argument_list|,
name|segmentSuffix
argument_list|,
name|EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|fileName
argument_list|,
name|context
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeIndexHeader
argument_list|(
name|output
argument_list|,
name|Lucene60FieldInfosFormat
operator|.
name|CODEC_NAME
argument_list|,
name|Lucene60FieldInfosFormat
operator|.
name|FORMAT_CURRENT
argument_list|,
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|segmentSuffix
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|infos
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldInfo
name|fi
range|:
name|infos
control|)
block|{
name|fi
operator|.
name|checkConsistency
argument_list|()
expr_stmt|;
name|output
operator|.
name|writeString
argument_list|(
name|fi
operator|.
name|name
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|fi
operator|.
name|number
argument_list|)
expr_stmt|;
name|byte
name|bits
init|=
literal|0x0
decl_stmt|;
if|if
condition|(
name|fi
operator|.
name|hasVectors
argument_list|()
condition|)
name|bits
operator||=
name|STORE_TERMVECTOR
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|omitsNorms
argument_list|()
condition|)
name|bits
operator||=
name|OMIT_NORMS
expr_stmt|;
if|if
condition|(
name|fi
operator|.
name|hasPayloads
argument_list|()
condition|)
name|bits
operator||=
name|STORE_PAYLOADS
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeByte
argument_list|(
name|indexOptionsByte
argument_list|(
name|fi
operator|.
name|getIndexOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// pack the DV type and hasNorms in one byte
name|output
operator|.
name|writeByte
argument_list|(
name|docValuesByte
argument_list|(
name|fi
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeLong
argument_list|(
name|fi
operator|.
name|getDocValuesGen
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeMapOfStrings
argument_list|(
name|fi
operator|.
name|attributes
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|pointDimensionCount
init|=
name|fi
operator|.
name|getPointDimensionCount
argument_list|()
decl_stmt|;
name|output
operator|.
name|writeVInt
argument_list|(
name|pointDimensionCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|pointDimensionCount
operator|!=
literal|0
condition|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|fi
operator|.
name|getPointNumBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Extension of field infos */
DECL|field|EXTENSION
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|"fnm"
decl_stmt|;
comment|// Codec header
DECL|field|CODEC_NAME
specifier|static
specifier|final
name|String
name|CODEC_NAME
init|=
literal|"Lucene60FieldInfos"
decl_stmt|;
DECL|field|FORMAT_START
specifier|static
specifier|final
name|int
name|FORMAT_START
init|=
literal|0
decl_stmt|;
DECL|field|FORMAT_CURRENT
specifier|static
specifier|final
name|int
name|FORMAT_CURRENT
init|=
name|FORMAT_START
decl_stmt|;
comment|// Field flags
DECL|field|STORE_TERMVECTOR
specifier|static
specifier|final
name|byte
name|STORE_TERMVECTOR
init|=
literal|0x1
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|static
specifier|final
name|byte
name|OMIT_NORMS
init|=
literal|0x2
decl_stmt|;
DECL|field|STORE_PAYLOADS
specifier|static
specifier|final
name|byte
name|STORE_PAYLOADS
init|=
literal|0x4
decl_stmt|;
block|}
end_class

end_unit

