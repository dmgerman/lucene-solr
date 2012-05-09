begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.lucene40
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene40
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|PerDocConsumer
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
name|PerDocProducer
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
name|DocValues
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|DocValues
operator|.
name|Type
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|PerDocWriteState
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
name|SegmentReadState
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
name|CompoundFileDirectory
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
comment|// javadocs
end_comment

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
name|CodecUtil
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
comment|// javadocs
end_comment

begin_comment
comment|/**  * Lucene 4.0 DocValues format.  *<p>  * Files:  *<ul>  *<li><tt>.dv.cfs</tt>: {@link CompoundFileDirectory compound container}</li>  *<li><tt>.dv.cfe</tt>: {@link CompoundFileDirectory compound entries}</li>  *</ul>  * Entries within the compound file:  *<ul>  *<li><tt>&lt;segment&gt;_&lt;fieldNumber&gt;.dat</tt>: data values</li>  *<li><tt>&lt;segment&gt;_&lt;fieldNumber&gt;.idx</tt>: index into the .dat for DEREF types</li>  *</ul>  *<p>  * There are several many types of {@link DocValues} with different encodings.  * From the perspective of filenames, all types store their values in<tt>.dat</tt>  * entries within the compound file. In the case of dereferenced/sorted types, the<tt>.dat</tt>  * actually contains only the unique values, and an additional<tt>.idx</tt> file contains  * pointers to these unique values.  *</p>  * Formats:  *<ul>  *<li>{@link Type#VAR_INTS VAR_INTS} .dat --&gt; Header, PackedType, MinValue,   *        DefaultValue, PackedStream</li>  *<li>{@link Type#FIXED_INTS_8 FIXED_INTS_8} .dat --&gt; Header, ValueSize,   *        {@link DataOutput#writeByte Byte}<sup>maxdoc</sup></li>  *<li>{@link Type#FIXED_INTS_16 FIXED_INTS_16} .dat --&gt; Header, ValueSize,  *        {@link DataOutput#writeShort Short}<sup>maxdoc</sup></li>  *<li>{@link Type#FIXED_INTS_32 FIXED_INTS_32} .dat --&gt; Header, ValueSize,  *        {@link DataOutput#writeInt Int32}<sup>maxdoc</sup></li>  *<li>{@link Type#FIXED_INTS_64 FIXED_INTS_64} .dat --&gt; Header, ValueSize,  *        {@link DataOutput#writeLong Int64}<sup>maxdoc</sup></li>  *<li>{@link Type#FLOAT_32 FLOAT_32} .dat --&gt; Header, ValueSize,  *        Float32<sup>maxdoc</sup></li>  *<li>{@link Type#FLOAT_64 FLOAT_64} .dat --&gt; Header, ValueSize,  *        Float64<sup>maxdoc</sup></li>  *<li>{@link Type#BYTES_FIXED_STRAIGHT BYTES_FIXED_STRAIGHT} .dat --&gt; Header, ValueSize,  *        ({@link DataOutput#writeByte Byte} * ValueSize)<sup>maxdoc</sup></li>  *<li>{@link Type#BYTES_VAR_STRAIGHT BYTES_VAR_STRAIGHT} .idx --&gt; Header, MaxAddress,  *        Addresses</li>  *<li>{@link Type#BYTES_VAR_STRAIGHT BYTES_VAR_STRAIGHT} .dat --&gt; Header, TotalBytes,  *        Addresses, ({@link DataOutput#writeByte Byte} *  *<i>variable ValueSize</i>)<sup>maxdoc</sup></li>  *<li>{@link Type#BYTES_FIXED_DEREF BYTES_FIXED_DEREF} .idx --&gt; Header, NumValues,  *        Addresses</li>  *<li>{@link Type#BYTES_FIXED_DEREF BYTES_FIXED_DEREF} .dat --&gt; Header, ValueSize,  *        ({@link DataOutput#writeByte Byte} * ValueSize)<sup>NumValues</sup></li>  *<li>{@link Type#BYTES_VAR_DEREF BYTES_VAR_DEREF} .idx --&gt; Header, TotalVarBytes,  *        Addresses</li>  *<li>{@link Type#BYTES_VAR_DEREF BYTES_VAR_DEREF} .dat --&gt; Header,  *        (LengthPrefix + {@link DataOutput#writeByte Byte} *<i>variable ValueSize</i>)<sup>NumValues</sup></li>  *<li>{@link Type#BYTES_FIXED_SORTED BYTES_FIXED_SORTED} .idx --&gt; Header, NumValues,  *        Ordinals</li>  *<li>{@link Type#BYTES_FIXED_SORTED BYTES_FIXED_SORTED} .dat --&gt; Header, ValueSize,  *        ({@link DataOutput#writeByte Byte} * ValueSize)<sup>NumValues</sup></li>  *<li>{@link Type#BYTES_VAR_SORTED BYTES_VAR_SORTED} .idx --&gt; Header, TotalVarBytes,  *        Addresses, Ordinals</li>  *<li>{@link Type#BYTES_VAR_SORTED BYTES_VAR_SORTED} .dat --&gt; Header,  *        ({@link DataOutput#writeByte Byte} *<i>variable ValueSize</i>)<sup>NumValues</sup></li>  *</ul>  * Data Types:  *<ul>  *<li>Header --&gt; {@link CodecUtil#writeHeader CodecHeader}</li>  *<li>PackedType --&gt; {@link DataOutput#writeByte Byte}</li>  *<li>MaxAddress, MinValue, DefaultValue --&gt; {@link DataOutput#writeLong Int64}</li>  *<li>PackedStream, Addresses, Ordinals --&gt; {@link PackedInts}</li>  *<li>ValueSize, NumValues --&gt; {@link DataOutput#writeInt Int32}</li>  *<li>Float32 --&gt; 32-bit float encoded with {@link Float#floatToRawIntBits(float)}  *                       then written as {@link DataOutput#writeInt Int32}</li>  *<li>Float64 --&gt; 64-bit float encoded with {@link Double#doubleToRawLongBits(double)}  *                       then written as {@link DataOutput#writeLong Int64}</li>  *<li>TotalBytes --&gt; {@link DataOutput#writeVLong VLong}</li>  *<li>TotalVarBytes --&gt; {@link DataOutput#writeLong Int64}</li>  *<li>LengthPrefix --&gt; Length of the data value as {@link DataOutput#writeVInt VInt} (maximum  *                       of 2 bytes)</li>  *</ul>  * Notes:  *<ul>  *<li>PackedType is a 0 when compressed, 1 when the stream is written as 64-bit integers.</li>  *<li>Addresses stores pointers to the actual byte location (indexed by docid). In the VAR_STRAIGHT  *        case, each entry can have a different length, so to determine the length, docid+1 is   *        retrieved. A sentinel address is written at the end for the VAR_STRAIGHT case, so the Addresses   *        stream contains maxdoc+1 indices. For the deduplicated VAR_DEREF case, each length  *        is encoded as a prefix to the data itself as a {@link DataOutput#writeVInt VInt}   *        (maximum of 2 bytes).</li>  *<li>Ordinals stores the term ID in sorted order (indexed by docid). In the FIXED_SORTED case,  *        the address into the .dat can be computed from the ordinal as   *<code>Header+ValueSize+(ordinal*ValueSize)</code> because the byte length is fixed.  *        In the VAR_SORTED case, there is double indirection (docid -> ordinal -> address), but  *        an additional sentinel ordinal+address is always written (so there are NumValues+1 ordinals). To  *        determine the length, ord+1's address is looked up as well.</li>  *<li>{@link Type#BYTES_VAR_STRAIGHT BYTES_VAR_STRAIGHT} in contrast to other straight   *        variants uses a<tt>.idx</tt> file to improve lookup perfromance. In contrast to   *        {@link Type#BYTES_VAR_DEREF BYTES_VAR_DEREF} it doesn't apply deduplication of the document values.  *</li>  *</ul>  */
end_comment

begin_class
DECL|class|Lucene40DocValuesFormat
specifier|public
class|class
name|Lucene40DocValuesFormat
extends|extends
name|DocValuesFormat
block|{
annotation|@
name|Override
DECL|method|docsConsumer
specifier|public
name|PerDocConsumer
name|docsConsumer
parameter_list|(
name|PerDocWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40DocValuesConsumer
argument_list|(
name|state
argument_list|,
name|Lucene40DocValuesConsumer
operator|.
name|DOC_VALUES_SEGMENT_SUFFIX
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|docsProducer
specifier|public
name|PerDocProducer
name|docsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene40DocValuesProducer
argument_list|(
name|state
argument_list|,
name|Lucene40DocValuesConsumer
operator|.
name|DOC_VALUES_SEGMENT_SUFFIX
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|SegmentInfo
name|info
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
throws|throws
name|IOException
block|{
name|Lucene40DocValuesConsumer
operator|.
name|files
argument_list|(
name|info
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

