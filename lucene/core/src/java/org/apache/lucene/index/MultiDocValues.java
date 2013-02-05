begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|java
operator|.
name|util
operator|.
name|List
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
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
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
name|MultiTermsEnum
operator|.
name|TermsEnumWithSlice
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
name|AppendingLongBuffer
import|;
end_import

begin_comment
comment|/**  * A wrapper for CompositeIndexReader providing access to DocValues.  *   *<p><b>NOTE</b>: for multi readers, you'll get better  * performance by gathering the sub readers using  * {@link IndexReader#getContext()} to get the  * atomic leaves and then operate per-AtomicReader,  * instead of using this class.  *   *<p><b>NOTE</b>: This is very costly.  *  * @lucene.experimental  * @lucene.internal  */
end_comment

begin_class
DECL|class|MultiDocValues
specifier|public
class|class
name|MultiDocValues
block|{
comment|/** No instantiation */
DECL|method|MultiDocValues
specifier|private
name|MultiDocValues
parameter_list|()
block|{}
comment|/** Returns a NumericDocValues for a reader's norms (potentially merging on-the-fly).    *<p>    * This is a slow way to access normalization values. Instead, access them per-segment    * with {@link AtomicReader#getNormValues(String)}    *</p>     */
DECL|method|getNormValues
specifier|public
specifier|static
name|NumericDocValues
name|getNormValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|FieldInfo
name|fi
init|=
name|MultiFields
operator|.
name|getMergedFieldInfos
argument_list|(
name|r
argument_list|)
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
operator|||
name|fi
operator|.
name|hasNorms
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|null
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
specifier|final
name|NumericDocValues
index|[]
name|values
init|=
operator|new
name|NumericDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|AtomicReaderContext
name|context
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NumericDocValues
name|v
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNormValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|NumericDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
assert|assert
name|anyReal
assert|;
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|starts
argument_list|)
decl_stmt|;
return|return
name|values
index|[
name|subIndex
index|]
operator|.
name|get
argument_list|(
name|docID
operator|-
name|starts
index|[
name|subIndex
index|]
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Returns a NumericDocValues for a reader's docvalues (potentially merging on-the-fly)     *<p>    * This is a slow way to access numeric values. Instead, access them per-segment    * with {@link AtomicReader#getNumericDocValues(String)}    *</p>     * */
DECL|method|getNumericValues
specifier|public
specifier|static
name|NumericDocValues
name|getNumericValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
specifier|final
name|NumericDocValues
index|[]
name|values
init|=
operator|new
name|NumericDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|AtomicReaderContext
name|context
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NumericDocValues
name|v
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|NumericDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|anyReal
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|starts
argument_list|)
decl_stmt|;
return|return
name|values
index|[
name|subIndex
index|]
operator|.
name|get
argument_list|(
name|docID
operator|-
name|starts
index|[
name|subIndex
index|]
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Returns a BinaryDocValues for a reader's docvalues (potentially merging on-the-fly)    *<p>    * This is a slow way to access binary values. Instead, access them per-segment    * with {@link AtomicReader#getBinaryDocValues(String)}    *</p>      */
DECL|method|getBinaryValues
specifier|public
specifier|static
name|BinaryDocValues
name|getBinaryValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
specifier|final
name|BinaryDocValues
index|[]
name|values
init|=
operator|new
name|BinaryDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|AtomicReaderContext
name|context
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|BinaryDocValues
name|v
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getBinaryDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|BinaryDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|anyReal
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|int
name|docID
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|starts
argument_list|)
decl_stmt|;
name|values
index|[
name|subIndex
index|]
operator|.
name|get
argument_list|(
name|docID
operator|-
name|starts
index|[
name|subIndex
index|]
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
comment|/** Returns a SortedDocValues for a reader's docvalues (potentially doing extremely slow things).    *<p>    * This is an extremely slow way to access sorted values. Instead, access them per-segment    * with {@link AtomicReader#getSortedDocValues(String)}    *</p>      */
DECL|method|getSortedValues
specifier|public
specifier|static
name|SortedDocValues
name|getSortedValues
parameter_list|(
specifier|final
name|IndexReader
name|r
parameter_list|,
specifier|final
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
init|=
name|r
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|==
literal|1
condition|)
block|{
return|return
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
name|boolean
name|anyReal
init|=
literal|false
decl_stmt|;
specifier|final
name|SortedDocValues
index|[]
name|values
init|=
operator|new
name|SortedDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
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
name|AtomicReaderContext
name|context
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SortedDocValues
name|v
init|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|SortedDocValues
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|anyReal
operator|=
literal|true
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|r
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|anyReal
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|OrdinalMap
name|mapping
init|=
operator|new
name|OrdinalMap
argument_list|(
name|values
argument_list|)
decl_stmt|;
return|return
operator|new
name|MultiSortedDocValues
argument_list|(
name|values
argument_list|,
name|starts
argument_list|,
name|mapping
argument_list|)
return|;
block|}
block|}
comment|/** maps per-segment ordinals to/from global ordinal space */
comment|// TODO: use more efficient packed ints structures (these are all positive values!)
DECL|class|OrdinalMap
specifier|static
class|class
name|OrdinalMap
block|{
comment|// globalOrd -> (globalOrd - segmentOrd)
DECL|field|globalOrdDeltas
specifier|final
name|AppendingLongBuffer
name|globalOrdDeltas
decl_stmt|;
comment|// globalOrd -> sub index
DECL|field|subIndexes
specifier|final
name|AppendingLongBuffer
name|subIndexes
decl_stmt|;
comment|// segmentOrd -> (globalOrd - segmentOrd)
DECL|field|ordDeltas
specifier|final
name|AppendingLongBuffer
name|ordDeltas
index|[]
decl_stmt|;
DECL|method|OrdinalMap
name|OrdinalMap
parameter_list|(
name|SortedDocValues
name|subs
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create the ordinal mappings by pulling a termsenum over each sub's
comment|// unique terms, and walking a multitermsenum over those
name|globalOrdDeltas
operator|=
operator|new
name|AppendingLongBuffer
argument_list|()
expr_stmt|;
name|subIndexes
operator|=
operator|new
name|AppendingLongBuffer
argument_list|()
expr_stmt|;
name|ordDeltas
operator|=
operator|new
name|AppendingLongBuffer
index|[
name|subs
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ordDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ordDeltas
index|[
name|i
index|]
operator|=
operator|new
name|AppendingLongBuffer
argument_list|()
expr_stmt|;
block|}
name|int
name|segmentOrds
index|[]
init|=
operator|new
name|int
index|[
name|subs
operator|.
name|length
index|]
decl_stmt|;
name|ReaderSlice
name|slices
index|[]
init|=
operator|new
name|ReaderSlice
index|[
name|subs
operator|.
name|length
index|]
decl_stmt|;
name|TermsEnumIndex
name|indexes
index|[]
init|=
operator|new
name|TermsEnumIndex
index|[
name|slices
operator|.
name|length
index|]
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
name|slices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|slices
index|[
name|i
index|]
operator|=
operator|new
name|ReaderSlice
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|indexes
index|[
name|i
index|]
operator|=
operator|new
name|TermsEnumIndex
argument_list|(
operator|new
name|SortedDocValuesTermsEnum
argument_list|(
name|subs
index|[
name|i
index|]
argument_list|)
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|MultiTermsEnum
name|mte
init|=
operator|new
name|MultiTermsEnum
argument_list|(
name|slices
argument_list|)
decl_stmt|;
name|mte
operator|.
name|reset
argument_list|(
name|indexes
argument_list|)
expr_stmt|;
name|int
name|globalOrd
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|mte
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|TermsEnumWithSlice
name|matches
index|[]
init|=
name|mte
operator|.
name|getMatchArray
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
name|mte
operator|.
name|getMatchCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|subIndex
init|=
name|matches
index|[
name|i
index|]
operator|.
name|index
decl_stmt|;
name|int
name|delta
init|=
name|globalOrd
operator|-
name|segmentOrds
index|[
name|subIndex
index|]
decl_stmt|;
assert|assert
name|delta
operator|>=
literal|0
assert|;
comment|// for each unique term, just mark the first subindex/delta where it occurs
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|subIndexes
operator|.
name|add
argument_list|(
name|subIndex
argument_list|)
expr_stmt|;
name|globalOrdDeltas
operator|.
name|add
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
comment|// for each per-segment ord, map it back to the global term.
name|ordDeltas
index|[
name|subIndex
index|]
operator|.
name|add
argument_list|(
name|delta
argument_list|)
expr_stmt|;
name|segmentOrds
index|[
name|subIndex
index|]
operator|++
expr_stmt|;
block|}
name|globalOrd
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|/** implements SortedDocValues over n subs, using an OrdinalMap */
DECL|class|MultiSortedDocValues
specifier|static
class|class
name|MultiSortedDocValues
extends|extends
name|SortedDocValues
block|{
DECL|field|docStarts
specifier|final
name|int
name|docStarts
index|[]
decl_stmt|;
DECL|field|values
specifier|final
name|SortedDocValues
name|values
index|[]
decl_stmt|;
DECL|field|mapping
specifier|final
name|OrdinalMap
name|mapping
decl_stmt|;
DECL|method|MultiSortedDocValues
name|MultiSortedDocValues
parameter_list|(
name|SortedDocValues
name|values
index|[]
parameter_list|,
name|int
name|docStarts
index|[]
parameter_list|,
name|OrdinalMap
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|docStarts
operator|=
name|docStarts
expr_stmt|;
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|int
name|subIndex
init|=
name|ReaderUtil
operator|.
name|subIndex
argument_list|(
name|docID
argument_list|,
name|docStarts
argument_list|)
decl_stmt|;
name|int
name|segmentOrd
init|=
name|values
index|[
name|subIndex
index|]
operator|.
name|getOrd
argument_list|(
name|docID
operator|-
name|docStarts
index|[
name|subIndex
index|]
argument_list|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|segmentOrd
operator|+
name|mapping
operator|.
name|ordDeltas
index|[
name|subIndex
index|]
operator|.
name|get
argument_list|(
name|segmentOrd
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|int
name|subIndex
init|=
operator|(
name|int
operator|)
name|mapping
operator|.
name|subIndexes
operator|.
name|get
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|int
name|segmentOrd
init|=
call|(
name|int
call|)
argument_list|(
name|ord
operator|-
name|mapping
operator|.
name|globalOrdDeltas
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
decl_stmt|;
assert|assert
name|subIndex
operator|<
name|values
operator|.
name|length
assert|;
name|values
index|[
name|subIndex
index|]
operator|.
name|lookupOrd
argument_list|(
name|segmentOrd
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|mapping
operator|.
name|globalOrdDeltas
operator|.
name|size
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

