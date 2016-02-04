begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.codecs.lucene54
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|lucene54
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
name|codecs
operator|.
name|DocValuesConsumer
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
name|DocValuesProducer
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
name|index
operator|.
name|SegmentWriteState
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
name|SmallFloat
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
name|DirectWriter
import|;
end_import

begin_comment
comment|/**  * Lucene 5.4 DocValues format.  *<p>  * Encodes the five per-document value types (Numeric,Binary,Sorted,SortedSet,SortedNumeric) with these strategies:  *<p>  * {@link DocValuesType#NUMERIC NUMERIC}:  *<ul>  *<li>Delta-compressed: per-document integers written as deltas from the minimum value,  *        compressed with bitpacking. For more information, see {@link DirectWriter}.  *<li>Table-compressed: when the number of unique values is very small (&lt; 256), and  *        when there are unused "gaps" in the range of values used (such as {@link SmallFloat}),   *        a lookup table is written instead. Each per-document entry is instead the ordinal   *        to this table, and those ordinals are compressed with bitpacking ({@link DirectWriter}).   *<li>GCD-compressed: when all numbers share a common divisor, such as dates, the greatest  *        common denominator (GCD) is computed, and quotients are stored using Delta-compressed Numerics.  *<li>Monotonic-compressed: when all numbers are monotonically increasing offsets, they are written  *        as blocks of bitpacked integers, encoding the deviation from the expected delta.  *<li>Const-compressed: when there is only one possible non-missing value, only the missing  *        bitset is encoded.  *<li>Sparse-compressed: only documents with a value are stored, and lookups are performed  *        using binary search.  *</ul>  *<p>  * {@link DocValuesType#BINARY BINARY}:  *<ul>  *<li>Fixed-width Binary: one large concatenated byte[] is written, along with the fixed length.  *        Each document's value can be addressed directly with multiplication ({@code docID * length}).   *<li>Variable-width Binary: one large concatenated byte[] is written, along with end addresses   *        for each document. The addresses are written as Monotonic-compressed numerics.  *<li>Prefix-compressed Binary: values are written in chunks of 16, with the first value written  *        completely and other values sharing prefixes. chunk addresses are written as Monotonic-compressed  *        numerics. A reverse lookup index is written from a portion of every 1024th term.  *</ul>  *<p>  * {@link DocValuesType#SORTED SORTED}:  *<ul>  *<li>Sorted: a mapping of ordinals to deduplicated terms is written as Binary,   *        along with the per-document ordinals written using one of the numeric strategies above.  *</ul>  *<p>  * {@link DocValuesType#SORTED_SET SORTED_SET}:  *<ul>  *<li>Single: if all documents have 0 or 1 value, then data are written like SORTED.  *<li>SortedSet table: when there are few unique sets of values (&lt; 256) then each set is assigned  *        an id, a lookup table is written and the mapping from document to set id is written using the  *        numeric strategies above.  *<li>SortedSet: a mapping of ordinals to deduplicated terms is written as Binary,   *        an ordinal list and per-document index into this list are written using the numeric strategies   *        above.  *</ul>  *<p>  * {@link DocValuesType#SORTED_NUMERIC SORTED_NUMERIC}:  *<ul>  *<li>Single: if all documents have 0 or 1 value, then data are written like NUMERIC.  *<li>SortedSet table: when there are few unique sets of values (&lt; 256) then each set is assigned  *        an id, a lookup table is written and the mapping from document to set id is written using the  *        numeric strategies above.  *<li>SortedNumeric: a value list and per-document index into this list are written using the numeric  *        strategies above.  *</ul>  *<p>  * Files:  *<ol>  *<li><tt>.dvd</tt>: DocValues data</li>  *<li><tt>.dvm</tt>: DocValues metadata</li>  *</ol>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Lucene54DocValuesFormat
specifier|public
specifier|final
class|class
name|Lucene54DocValuesFormat
extends|extends
name|DocValuesFormat
block|{
comment|/** Sole Constructor */
DECL|method|Lucene54DocValuesFormat
specifier|public
name|Lucene54DocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Lucene54"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|DocValuesConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene54DocValuesConsumer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|META_CODEC
argument_list|,
name|META_EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|DocValuesProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Lucene54DocValuesProducer
argument_list|(
name|state
argument_list|,
name|DATA_CODEC
argument_list|,
name|DATA_EXTENSION
argument_list|,
name|META_CODEC
argument_list|,
name|META_EXTENSION
argument_list|)
return|;
block|}
DECL|field|DATA_CODEC
specifier|static
specifier|final
name|String
name|DATA_CODEC
init|=
literal|"Lucene54DocValuesData"
decl_stmt|;
DECL|field|DATA_EXTENSION
specifier|static
specifier|final
name|String
name|DATA_EXTENSION
init|=
literal|"dvd"
decl_stmt|;
DECL|field|META_CODEC
specifier|static
specifier|final
name|String
name|META_CODEC
init|=
literal|"Lucene54DocValuesMetadata"
decl_stmt|;
DECL|field|META_EXTENSION
specifier|static
specifier|final
name|String
name|META_EXTENSION
init|=
literal|"dvm"
decl_stmt|;
DECL|field|VERSION_START
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
comment|// indicates docvalues type
DECL|field|NUMERIC
specifier|static
specifier|final
name|byte
name|NUMERIC
init|=
literal|0
decl_stmt|;
DECL|field|BINARY
specifier|static
specifier|final
name|byte
name|BINARY
init|=
literal|1
decl_stmt|;
DECL|field|SORTED
specifier|static
specifier|final
name|byte
name|SORTED
init|=
literal|2
decl_stmt|;
DECL|field|SORTED_SET
specifier|static
specifier|final
name|byte
name|SORTED_SET
init|=
literal|3
decl_stmt|;
DECL|field|SORTED_NUMERIC
specifier|static
specifier|final
name|byte
name|SORTED_NUMERIC
init|=
literal|4
decl_stmt|;
comment|// address terms in blocks of 16 terms
DECL|field|INTERVAL_SHIFT
specifier|static
specifier|final
name|int
name|INTERVAL_SHIFT
init|=
literal|4
decl_stmt|;
DECL|field|INTERVAL_COUNT
specifier|static
specifier|final
name|int
name|INTERVAL_COUNT
init|=
literal|1
operator|<<
name|INTERVAL_SHIFT
decl_stmt|;
DECL|field|INTERVAL_MASK
specifier|static
specifier|final
name|int
name|INTERVAL_MASK
init|=
name|INTERVAL_COUNT
operator|-
literal|1
decl_stmt|;
comment|// build reverse index from every 1024th term
DECL|field|REVERSE_INTERVAL_SHIFT
specifier|static
specifier|final
name|int
name|REVERSE_INTERVAL_SHIFT
init|=
literal|10
decl_stmt|;
DECL|field|REVERSE_INTERVAL_COUNT
specifier|static
specifier|final
name|int
name|REVERSE_INTERVAL_COUNT
init|=
literal|1
operator|<<
name|REVERSE_INTERVAL_SHIFT
decl_stmt|;
DECL|field|REVERSE_INTERVAL_MASK
specifier|static
specifier|final
name|int
name|REVERSE_INTERVAL_MASK
init|=
name|REVERSE_INTERVAL_COUNT
operator|-
literal|1
decl_stmt|;
comment|// for conversion from reverse index to block
DECL|field|BLOCK_INTERVAL_SHIFT
specifier|static
specifier|final
name|int
name|BLOCK_INTERVAL_SHIFT
init|=
name|REVERSE_INTERVAL_SHIFT
operator|-
name|INTERVAL_SHIFT
decl_stmt|;
DECL|field|BLOCK_INTERVAL_COUNT
specifier|static
specifier|final
name|int
name|BLOCK_INTERVAL_COUNT
init|=
literal|1
operator|<<
name|BLOCK_INTERVAL_SHIFT
decl_stmt|;
DECL|field|BLOCK_INTERVAL_MASK
specifier|static
specifier|final
name|int
name|BLOCK_INTERVAL_MASK
init|=
name|BLOCK_INTERVAL_COUNT
operator|-
literal|1
decl_stmt|;
comment|/** Compressed using packed blocks of ints. */
DECL|field|DELTA_COMPRESSED
specifier|static
specifier|final
name|int
name|DELTA_COMPRESSED
init|=
literal|0
decl_stmt|;
comment|/** Compressed by computing the GCD. */
DECL|field|GCD_COMPRESSED
specifier|static
specifier|final
name|int
name|GCD_COMPRESSED
init|=
literal|1
decl_stmt|;
comment|/** Compressed by giving IDs to unique values. */
DECL|field|TABLE_COMPRESSED
specifier|static
specifier|final
name|int
name|TABLE_COMPRESSED
init|=
literal|2
decl_stmt|;
comment|/** Compressed with monotonically increasing values */
DECL|field|MONOTONIC_COMPRESSED
specifier|static
specifier|final
name|int
name|MONOTONIC_COMPRESSED
init|=
literal|3
decl_stmt|;
comment|/** Compressed with constant value (uses only missing bitset) */
DECL|field|CONST_COMPRESSED
specifier|static
specifier|final
name|int
name|CONST_COMPRESSED
init|=
literal|4
decl_stmt|;
comment|/** Compressed with sparse arrays. */
DECL|field|SPARSE_COMPRESSED
specifier|static
specifier|final
name|int
name|SPARSE_COMPRESSED
init|=
literal|5
decl_stmt|;
comment|/** Uncompressed binary, written directly (fixed length). */
DECL|field|BINARY_FIXED_UNCOMPRESSED
specifier|static
specifier|final
name|int
name|BINARY_FIXED_UNCOMPRESSED
init|=
literal|0
decl_stmt|;
comment|/** Uncompressed binary, written directly (variable length). */
DECL|field|BINARY_VARIABLE_UNCOMPRESSED
specifier|static
specifier|final
name|int
name|BINARY_VARIABLE_UNCOMPRESSED
init|=
literal|1
decl_stmt|;
comment|/** Compressed binary with shared prefixes */
DECL|field|BINARY_PREFIX_COMPRESSED
specifier|static
specifier|final
name|int
name|BINARY_PREFIX_COMPRESSED
init|=
literal|2
decl_stmt|;
comment|/** Standard storage for sorted set values with 1 level of indirection:    *  {@code docId -> address -> ord}. */
DECL|field|SORTED_WITH_ADDRESSES
specifier|static
specifier|final
name|int
name|SORTED_WITH_ADDRESSES
init|=
literal|0
decl_stmt|;
comment|/** Single-valued sorted set values, encoded as sorted values, so no level    *  of indirection: {@code docId -> ord}. */
DECL|field|SORTED_SINGLE_VALUED
specifier|static
specifier|final
name|int
name|SORTED_SINGLE_VALUED
init|=
literal|1
decl_stmt|;
comment|/** Compressed giving IDs to unique sets of values:    * {@code docId -> setId -> ords} */
DECL|field|SORTED_SET_TABLE
specifier|static
specifier|final
name|int
name|SORTED_SET_TABLE
init|=
literal|2
decl_stmt|;
comment|/** placeholder for missing offset that means there are no missing values */
DECL|field|ALL_LIVE
specifier|static
specifier|final
name|int
name|ALL_LIVE
init|=
operator|-
literal|1
decl_stmt|;
comment|/** placeholder for missing offset that means all values are missing */
DECL|field|ALL_MISSING
specifier|static
specifier|final
name|int
name|ALL_MISSING
init|=
operator|-
literal|2
decl_stmt|;
comment|// addressing uses 16k blocks
DECL|field|MONOTONIC_BLOCK_SIZE
specifier|static
specifier|final
name|int
name|MONOTONIC_BLOCK_SIZE
init|=
literal|16384
decl_stmt|;
DECL|field|DIRECT_MONOTONIC_BLOCK_SHIFT
specifier|static
specifier|final
name|int
name|DIRECT_MONOTONIC_BLOCK_SHIFT
init|=
literal|16
decl_stmt|;
block|}
end_class

end_unit

