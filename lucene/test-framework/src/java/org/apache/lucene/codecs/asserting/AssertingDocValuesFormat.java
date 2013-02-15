begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.asserting
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|asserting
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|codecs
operator|.
name|lucene42
operator|.
name|Lucene42DocValuesFormat
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
name|AssertingAtomicReader
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
name|BinaryDocValues
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
name|NumericDocValues
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
name|index
operator|.
name|SortedDocValues
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
name|SortedSetDocValues
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
name|FixedBitSet
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
name|OpenBitSet
import|;
end_import

begin_comment
comment|/**  * Just like {@link Lucene42DocValuesFormat} but with additional asserts.  */
end_comment

begin_class
DECL|class|AssertingDocValuesFormat
specifier|public
class|class
name|AssertingDocValuesFormat
extends|extends
name|DocValuesFormat
block|{
DECL|field|in
specifier|private
specifier|final
name|DocValuesFormat
name|in
init|=
operator|new
name|Lucene42DocValuesFormat
argument_list|()
decl_stmt|;
DECL|method|AssertingDocValuesFormat
specifier|public
name|AssertingDocValuesFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"Asserting"
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
name|DocValuesConsumer
name|consumer
init|=
name|in
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
assert|assert
name|consumer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingDocValuesConsumer
argument_list|(
name|consumer
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
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
assert|assert
name|state
operator|.
name|fieldInfos
operator|.
name|hasDocValues
argument_list|()
assert|;
name|DocValuesProducer
name|producer
init|=
name|in
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
decl_stmt|;
assert|assert
name|producer
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingDocValuesProducer
argument_list|(
name|producer
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getDocCount
argument_list|()
argument_list|)
return|;
block|}
DECL|class|AssertingDocValuesConsumer
specifier|static
class|class
name|AssertingDocValuesConsumer
extends|extends
name|DocValuesConsumer
block|{
DECL|field|in
specifier|private
specifier|final
name|DocValuesConsumer
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AssertingDocValuesConsumer
name|AssertingDocValuesConsumer
parameter_list|(
name|DocValuesConsumer
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addNumericField
specifier|public
name|void
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|v
range|:
name|values
control|)
block|{
assert|assert
name|v
operator|!=
literal|null
assert|;
name|count
operator|++
expr_stmt|;
block|}
assert|assert
name|count
operator|==
name|maxDoc
assert|;
name|checkIterator
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|in
operator|.
name|addNumericField
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addBinaryField
specifier|public
name|void
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|b
range|:
name|values
control|)
block|{
assert|assert
name|b
operator|!=
literal|null
assert|;
assert|assert
name|b
operator|.
name|isValid
argument_list|()
assert|;
name|count
operator|++
expr_stmt|;
block|}
assert|assert
name|count
operator|==
name|maxDoc
assert|;
name|checkIterator
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|in
operator|.
name|addBinaryField
argument_list|(
name|field
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addSortedField
specifier|public
name|void
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrd
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|valueCount
init|=
literal|0
decl_stmt|;
name|BytesRef
name|lastValue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|b
range|:
name|values
control|)
block|{
assert|assert
name|b
operator|!=
literal|null
assert|;
assert|assert
name|b
operator|.
name|isValid
argument_list|()
assert|;
if|if
condition|(
name|valueCount
operator|>
literal|0
condition|)
block|{
assert|assert
name|b
operator|.
name|compareTo
argument_list|(
name|lastValue
argument_list|)
operator|>
literal|0
assert|;
block|}
name|lastValue
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|valueCount
operator|++
expr_stmt|;
block|}
assert|assert
name|valueCount
operator|<=
name|maxDoc
assert|;
name|FixedBitSet
name|seenOrds
init|=
operator|new
name|FixedBitSet
argument_list|(
name|valueCount
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|v
range|:
name|docToOrd
control|)
block|{
assert|assert
name|v
operator|!=
literal|null
assert|;
name|int
name|ord
init|=
name|v
operator|.
name|intValue
argument_list|()
decl_stmt|;
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|valueCount
assert|;
name|seenOrds
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
assert|assert
name|count
operator|==
name|maxDoc
assert|;
assert|assert
name|seenOrds
operator|.
name|cardinality
argument_list|()
operator|==
name|valueCount
assert|;
name|checkIterator
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
argument_list|,
name|valueCount
argument_list|)
expr_stmt|;
name|checkIterator
argument_list|(
name|docToOrd
operator|.
name|iterator
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|in
operator|.
name|addSortedField
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
name|docToOrd
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addSortedSetField
specifier|public
name|void
name|addSortedSetField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|Iterable
argument_list|<
name|BytesRef
argument_list|>
name|values
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|docToOrdCount
parameter_list|,
name|Iterable
argument_list|<
name|Number
argument_list|>
name|ords
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|valueCount
init|=
literal|0
decl_stmt|;
name|BytesRef
name|lastValue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|b
range|:
name|values
control|)
block|{
assert|assert
name|b
operator|!=
literal|null
assert|;
assert|assert
name|b
operator|.
name|isValid
argument_list|()
assert|;
if|if
condition|(
name|valueCount
operator|>
literal|0
condition|)
block|{
assert|assert
name|b
operator|.
name|compareTo
argument_list|(
name|lastValue
argument_list|)
operator|>
literal|0
assert|;
block|}
name|lastValue
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|valueCount
operator|++
expr_stmt|;
block|}
name|int
name|docCount
init|=
literal|0
decl_stmt|;
name|long
name|ordCount
init|=
literal|0
decl_stmt|;
name|OpenBitSet
name|seenOrds
init|=
operator|new
name|OpenBitSet
argument_list|(
name|valueCount
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Number
argument_list|>
name|ordIterator
init|=
name|ords
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Number
name|v
range|:
name|docToOrdCount
control|)
block|{
assert|assert
name|v
operator|!=
literal|null
assert|;
name|int
name|count
init|=
name|v
operator|.
name|intValue
argument_list|()
decl_stmt|;
assert|assert
name|count
operator|>=
literal|0
assert|;
name|docCount
operator|++
expr_stmt|;
name|ordCount
operator|+=
name|count
expr_stmt|;
name|long
name|lastOrd
init|=
operator|-
literal|1
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Number
name|o
init|=
name|ordIterator
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
name|o
operator|!=
literal|null
assert|;
name|long
name|ord
init|=
name|o
operator|.
name|longValue
argument_list|()
decl_stmt|;
assert|assert
name|ord
operator|>=
literal|0
operator|&&
name|ord
operator|<
name|valueCount
assert|;
assert|assert
name|ord
operator|>
name|lastOrd
operator|:
literal|"ord="
operator|+
name|ord
operator|+
literal|",lastOrd="
operator|+
name|lastOrd
assert|;
name|seenOrds
operator|.
name|set
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|lastOrd
operator|=
name|ord
expr_stmt|;
block|}
block|}
assert|assert
name|ordIterator
operator|.
name|hasNext
argument_list|()
operator|==
literal|false
assert|;
assert|assert
name|docCount
operator|==
name|maxDoc
assert|;
assert|assert
name|seenOrds
operator|.
name|cardinality
argument_list|()
operator|==
name|valueCount
assert|;
name|checkIterator
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
argument_list|,
name|valueCount
argument_list|)
expr_stmt|;
name|checkIterator
argument_list|(
name|docToOrdCount
operator|.
name|iterator
argument_list|()
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|checkIterator
argument_list|(
name|ords
operator|.
name|iterator
argument_list|()
argument_list|,
name|ordCount
argument_list|)
expr_stmt|;
name|in
operator|.
name|addSortedSetField
argument_list|(
name|field
argument_list|,
name|values
argument_list|,
name|docToOrdCount
argument_list|,
name|ords
argument_list|)
expr_stmt|;
block|}
DECL|method|checkIterator
specifier|private
parameter_list|<
name|T
parameter_list|>
name|void
name|checkIterator
parameter_list|(
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|,
name|long
name|expectedSize
parameter_list|)
block|{
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedSize
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|hasNext
init|=
name|iterator
operator|.
name|hasNext
argument_list|()
decl_stmt|;
assert|assert
name|hasNext
assert|;
name|T
name|v
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
name|v
operator|!=
literal|null
assert|;
try|try
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"broken iterator (supports remove): "
operator|+
name|iterator
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|expected
parameter_list|)
block|{
comment|// ok
block|}
block|}
assert|assert
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
assert|;
try|try
block|{
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"broken iterator (allows next() when hasNext==false) "
operator|+
name|iterator
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|expected
parameter_list|)
block|{
comment|// ok
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|AssertingDocValuesProducer
specifier|static
class|class
name|AssertingDocValuesProducer
extends|extends
name|DocValuesProducer
block|{
DECL|field|in
specifier|private
specifier|final
name|DocValuesProducer
name|in
decl_stmt|;
DECL|field|maxDoc
specifier|private
specifier|final
name|int
name|maxDoc
decl_stmt|;
DECL|method|AssertingDocValuesProducer
name|AssertingDocValuesProducer
parameter_list|(
name|DocValuesProducer
name|in
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumeric
specifier|public
name|NumericDocValues
name|getNumeric
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|NUMERIC
operator|||
name|field
operator|.
name|getNormType
argument_list|()
operator|==
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|NUMERIC
assert|;
name|NumericDocValues
name|values
init|=
name|in
operator|.
name|getNumeric
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|values
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingNumericDocValues
argument_list|(
name|values
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBinary
specifier|public
name|BinaryDocValues
name|getBinary
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|BINARY
assert|;
name|BinaryDocValues
name|values
init|=
name|in
operator|.
name|getBinary
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|values
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingBinaryDocValues
argument_list|(
name|values
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSorted
specifier|public
name|SortedDocValues
name|getSorted
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|SORTED
assert|;
name|SortedDocValues
name|values
init|=
name|in
operator|.
name|getSorted
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|values
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingSortedDocValues
argument_list|(
name|values
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getSortedSet
specifier|public
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|field
operator|.
name|getDocValuesType
argument_list|()
operator|==
name|FieldInfo
operator|.
name|DocValuesType
operator|.
name|SORTED_SET
assert|;
name|SortedSetDocValues
name|values
init|=
name|in
operator|.
name|getSortedSet
argument_list|(
name|field
argument_list|)
decl_stmt|;
assert|assert
name|values
operator|!=
literal|null
assert|;
return|return
operator|new
name|AssertingAtomicReader
operator|.
name|AssertingSortedSetDocValues
argument_list|(
name|values
argument_list|,
name|maxDoc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

