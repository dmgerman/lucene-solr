begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
name|Closeable
import|;
end_import

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
name|index
operator|.
name|AtomicReader
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
operator|.
name|SortedSource
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
operator|.
name|Source
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
name|MergeState
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
name|Bits
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

begin_comment
comment|// prototype streaming DV api
end_comment

begin_class
DECL|class|SimpleDVConsumer
specifier|public
specifier|abstract
class|class
name|SimpleDVConsumer
implements|implements
name|Closeable
block|{
comment|// TODO: are any of these params too "infringing" on codec?
comment|// we want codec to get necessary stuff from IW, but trading off against merge complexity.
comment|// nocommit should we pass SegmentWriteState...?
DECL|method|addNumericField
specifier|public
specifier|abstract
name|NumericDocValuesConsumer
name|addNumericField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|long
name|minValue
parameter_list|,
name|long
name|maxValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|addBinaryField
specifier|public
specifier|abstract
name|BinaryDocValuesConsumer
name|addBinaryField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|boolean
name|fixedLength
parameter_list|,
name|int
name|maxLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// nocommit: figure out whats fair here.
DECL|method|addSortedField
specifier|public
specifier|abstract
name|SortedDocValuesConsumer
name|addSortedField
parameter_list|(
name|FieldInfo
name|field
parameter_list|,
name|int
name|valueCount
parameter_list|,
name|boolean
name|fixedLength
parameter_list|,
name|int
name|maxLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|FieldInfo
name|field
range|:
name|mergeState
operator|.
name|fieldInfos
control|)
block|{
if|if
condition|(
name|field
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
name|mergeState
operator|.
name|fieldInfo
operator|=
name|field
expr_stmt|;
comment|// nocommit: switch on 3 types: NUMBER, BYTES, SORTED
name|DocValues
operator|.
name|Type
name|type
init|=
name|field
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|VAR_INTS
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FLOAT_64
case|:
case|case
name|FLOAT_32
case|:
name|mergeNumericField
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_SORTED
case|:
case|case
name|BYTES_FIXED_SORTED
case|:
case|case
name|BYTES_VAR_DEREF
case|:
case|case
name|BYTES_FIXED_DEREF
case|:
name|mergeSortedField
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
break|break;
case|case
name|BYTES_VAR_STRAIGHT
case|:
case|case
name|BYTES_FIXED_STRAIGHT
case|:
name|mergeBinaryField
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
block|}
comment|// dead simple impl: codec can optimize
DECL|method|mergeNumericField
specifier|protected
name|void
name|mergeNumericField
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// first compute min and max value of live ones to be merged.
name|long
name|minValue
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|long
name|maxValue
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|DocValues
name|docValues
init|=
name|reader
operator|.
name|docValues
argument_list|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|source
decl_stmt|;
if|if
condition|(
name|docValues
operator|==
literal|null
condition|)
block|{
name|source
operator|=
name|DocValues
operator|.
name|getDefaultSource
argument_list|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
name|docValues
operator|.
name|getDirectSource
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|long
name|val
init|=
name|source
operator|.
name|getInt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|val
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|maxValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|val
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now we can merge
name|NumericDocValuesConsumer
name|field
init|=
name|addNumericField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|,
name|minValue
argument_list|,
name|maxValue
argument_list|)
decl_stmt|;
name|field
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
block|}
comment|// dead simple impl: codec can optimize
DECL|method|mergeBinaryField
specifier|protected
name|void
name|mergeBinaryField
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// first compute fixedLength and maxLength of live ones to be merged.
name|boolean
name|fixedLength
init|=
literal|true
decl_stmt|;
name|int
name|maxLength
init|=
operator|-
literal|1
decl_stmt|;
name|BytesRef
name|bytes
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
for|for
control|(
name|AtomicReader
name|reader
range|:
name|mergeState
operator|.
name|readers
control|)
block|{
specifier|final
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
specifier|final
name|Bits
name|liveDocs
init|=
name|reader
operator|.
name|getLiveDocs
argument_list|()
decl_stmt|;
name|DocValues
name|docValues
init|=
name|reader
operator|.
name|docValues
argument_list|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|name
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|source
decl_stmt|;
if|if
condition|(
name|docValues
operator|==
literal|null
condition|)
block|{
name|source
operator|=
name|DocValues
operator|.
name|getDefaultSource
argument_list|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|source
operator|=
name|docValues
operator|.
name|getDirectSource
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|liveDocs
operator|==
literal|null
operator|||
name|liveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|source
operator|.
name|getBytes
argument_list|(
name|i
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxLength
operator|==
operator|-
literal|1
condition|)
block|{
name|maxLength
operator|=
name|bytes
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|fixedLength
operator|&=
name|bytes
operator|.
name|length
operator|==
name|maxLength
expr_stmt|;
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
block|}
name|mergeState
operator|.
name|checkAbort
operator|.
name|work
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now we can merge
assert|assert
name|maxLength
operator|>=
literal|0
assert|;
comment|// could this happen (nothing to do?)
name|BinaryDocValuesConsumer
name|field
init|=
name|addBinaryField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|,
name|fixedLength
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
name|field
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
block|}
DECL|method|mergeSortedField
specifier|protected
name|void
name|mergeSortedField
parameter_list|(
name|MergeState
name|mergeState
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValuesConsumer
operator|.
name|Merger
name|merger
init|=
operator|new
name|SortedDocValuesConsumer
operator|.
name|Merger
argument_list|()
decl_stmt|;
name|merger
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|)
expr_stmt|;
name|SortedDocValuesConsumer
name|consumer
init|=
name|addSortedField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|,
name|merger
operator|.
name|numMergedTerms
argument_list|,
name|merger
operator|.
name|fixedLength
operator|>=
literal|0
argument_list|,
name|merger
operator|.
name|maxLength
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|merger
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

