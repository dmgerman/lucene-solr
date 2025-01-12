begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|SortedNumericDocValues
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
name|search
operator|.
name|SortField
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
name|search
operator|.
name|SortedNumericSelector
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
name|search
operator|.
name|SortedSetSelector
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
name|BitSet
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
comment|/** Select a value from a block of documents.  *  @lucene.internal */
end_comment

begin_class
DECL|class|BlockJoinSelector
specifier|public
class|class
name|BlockJoinSelector
block|{
DECL|method|BlockJoinSelector
specifier|private
name|BlockJoinSelector
parameter_list|()
block|{}
comment|/** Type of selection to perform. If none of the documents in the block have    *  a value then no value will be selected. */
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
comment|/** Only consider the minimum value from the block when sorting. */
DECL|enum constant|MIN
name|MIN
block|,
comment|/** Only consider the maximum value from the block when sorting. */
DECL|enum constant|MAX
name|MAX
block|;   }
comment|/** Return a {@link Bits} instance that returns true if, and only if, any of    *  the children of the given parent document has a value. */
DECL|method|wrap
specifier|public
specifier|static
name|Bits
name|wrap
parameter_list|(
specifier|final
name|Bits
name|docsWithValue
parameter_list|,
name|BitSet
name|parents
parameter_list|,
name|BitSet
name|children
parameter_list|)
block|{
return|return
operator|new
name|Bits
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
assert|assert
name|parents
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|:
literal|"this selector may only be used on parent documents"
assert|;
if|if
condition|(
name|docID
operator|==
literal|0
condition|)
block|{
comment|// no children
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|firstChild
init|=
name|parents
operator|.
name|prevSetBit
argument_list|(
name|docID
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|int
name|child
init|=
name|children
operator|.
name|nextSetBit
argument_list|(
name|firstChild
argument_list|)
init|;
name|child
operator|<
name|docID
condition|;
name|child
operator|=
name|children
operator|.
name|nextSetBit
argument_list|(
name|child
operator|+
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
name|docsWithValue
operator|.
name|get
argument_list|(
name|child
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|docsWithValue
operator|.
name|length
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** Wraps the provided {@link SortedSetDocValues} in order to only select    *  one value per parent among its {@code children} using the configured    *  {@code selection} type. */
DECL|method|wrap
specifier|public
specifier|static
name|SortedDocValues
name|wrap
parameter_list|(
name|SortedSetDocValues
name|sortedSet
parameter_list|,
name|Type
name|selection
parameter_list|,
name|BitSet
name|parents
parameter_list|,
name|BitSet
name|children
parameter_list|)
block|{
name|SortedDocValues
name|values
decl_stmt|;
switch|switch
condition|(
name|selection
condition|)
block|{
case|case
name|MIN
case|:
name|values
operator|=
name|SortedSetSelector
operator|.
name|wrap
argument_list|(
name|sortedSet
argument_list|,
name|SortedSetSelector
operator|.
name|Type
operator|.
name|MIN
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|values
operator|=
name|SortedSetSelector
operator|.
name|wrap
argument_list|(
name|sortedSet
argument_list|,
name|SortedSetSelector
operator|.
name|Type
operator|.
name|MAX
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
return|return
name|wrap
argument_list|(
name|values
argument_list|,
name|selection
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
comment|/** Wraps the provided {@link SortedDocValues} in order to only select    *  one value per parent among its {@code children} using the configured    *  {@code selection} type. */
DECL|method|wrap
specifier|public
specifier|static
name|SortedDocValues
name|wrap
parameter_list|(
specifier|final
name|SortedDocValues
name|values
parameter_list|,
name|Type
name|selection
parameter_list|,
name|BitSet
name|parents
parameter_list|,
name|BitSet
name|children
parameter_list|)
block|{
if|if
condition|(
name|values
operator|.
name|docID
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"values iterator was already consumed: values.docID="
operator|+
name|values
operator|.
name|docID
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
specifier|private
name|int
name|ord
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|int
name|docID
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docID
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
assert|assert
name|docID
operator|!=
name|NO_MORE_DOCS
assert|;
if|if
condition|(
name|values
operator|.
name|docID
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|values
operator|.
name|nextDoc
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|docID
return|;
block|}
block|}
if|if
condition|(
name|values
operator|.
name|docID
argument_list|()
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|docID
return|;
block|}
name|int
name|nextParentDocID
init|=
name|parents
operator|.
name|nextSetBit
argument_list|(
name|values
operator|.
name|docID
argument_list|()
argument_list|)
decl_stmt|;
name|ord
operator|=
name|values
operator|.
name|ordValue
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|childDocID
init|=
name|values
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
assert|assert
name|childDocID
operator|!=
name|nextParentDocID
assert|;
if|if
condition|(
name|childDocID
operator|>
name|nextParentDocID
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|childDocID
argument_list|)
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|selection
operator|==
name|Type
operator|.
name|MIN
condition|)
block|{
name|ord
operator|=
name|Math
operator|.
name|min
argument_list|(
name|ord
argument_list|,
name|values
operator|.
name|ordValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|selection
operator|==
name|Type
operator|.
name|MAX
condition|)
block|{
name|ord
operator|=
name|Math
operator|.
name|max
argument_list|(
name|ord
argument_list|,
name|values
operator|.
name|ordValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
name|docID
operator|=
name|nextParentDocID
expr_stmt|;
return|return
name|docID
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|target
operator|>=
name|parents
operator|.
name|length
argument_list|()
condition|)
block|{
name|docID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|docID
return|;
block|}
if|if
condition|(
name|target
operator|==
literal|0
condition|)
block|{
assert|assert
name|docID
argument_list|()
operator|==
operator|-
literal|1
assert|;
return|return
name|nextDoc
argument_list|()
return|;
block|}
name|int
name|prevParentDocID
init|=
name|parents
operator|.
name|prevSetBit
argument_list|(
name|target
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|docID
argument_list|()
operator|<=
name|prevParentDocID
condition|)
block|{
name|values
operator|.
name|advance
argument_list|(
name|prevParentDocID
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|targetParentDocID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|targetParentDocID
operator|<
name|docID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target must be after the current document: current="
operator|+
name|docID
operator|+
literal|" target="
operator|+
name|targetParentDocID
argument_list|)
throw|;
block|}
name|int
name|previousDocId
init|=
name|docID
decl_stmt|;
name|docID
operator|=
name|targetParentDocID
expr_stmt|;
if|if
condition|(
name|targetParentDocID
operator|==
name|previousDocId
condition|)
block|{
return|return
name|ord
operator|!=
operator|-
literal|1
return|;
block|}
name|docID
operator|=
name|targetParentDocID
expr_stmt|;
name|ord
operator|=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|parents
operator|.
name|get
argument_list|(
name|targetParentDocID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|prevParentDocId
init|=
name|docID
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|parents
operator|.
name|prevSetBit
argument_list|(
name|docID
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|childDoc
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|childDoc
operator|<=
name|prevParentDocId
condition|)
block|{
name|childDoc
operator|=
name|values
operator|.
name|advance
argument_list|(
name|prevParentDocId
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|childDoc
operator|>=
name|docID
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|hasValue
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|values
operator|.
name|docID
argument_list|()
init|;
name|doc
operator|<
name|docID
condition|;
name|doc
operator|=
name|values
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|ord
operator|=
name|values
operator|.
name|ordValue
argument_list|()
expr_stmt|;
name|hasValue
operator|=
literal|true
expr_stmt|;
name|values
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|hasValue
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|doc
init|=
name|values
operator|.
name|docID
argument_list|()
init|;
name|doc
operator|<
name|docID
condition|;
name|doc
operator|=
name|values
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|selection
condition|)
block|{
case|case
name|MIN
case|:
name|ord
operator|=
name|Math
operator|.
name|min
argument_list|(
name|ord
argument_list|,
name|values
operator|.
name|ordValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|ord
operator|=
name|Math
operator|.
name|max
argument_list|(
name|ord
argument_list|,
name|values
operator|.
name|ordValue
argument_list|()
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
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|ordValue
parameter_list|()
block|{
return|return
name|ord
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
name|values
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|values
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** Wraps the provided {@link SortedNumericDocValues} in order to only select    *  one value per parent among its {@code children} using the configured    *  {@code selection} type. */
DECL|method|wrap
specifier|public
specifier|static
name|NumericDocValues
name|wrap
parameter_list|(
name|SortedNumericDocValues
name|sortedNumerics
parameter_list|,
name|Type
name|selection
parameter_list|,
name|BitSet
name|parents
parameter_list|,
name|BitSet
name|children
parameter_list|)
block|{
name|NumericDocValues
name|values
decl_stmt|;
switch|switch
condition|(
name|selection
condition|)
block|{
case|case
name|MIN
case|:
name|values
operator|=
name|SortedNumericSelector
operator|.
name|wrap
argument_list|(
name|sortedNumerics
argument_list|,
name|SortedNumericSelector
operator|.
name|Type
operator|.
name|MIN
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|values
operator|=
name|SortedNumericSelector
operator|.
name|wrap
argument_list|(
name|sortedNumerics
argument_list|,
name|SortedNumericSelector
operator|.
name|Type
operator|.
name|MAX
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
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
return|return
name|wrap
argument_list|(
name|values
argument_list|,
name|selection
argument_list|,
name|parents
argument_list|,
name|children
argument_list|)
return|;
block|}
comment|/** Wraps the provided {@link NumericDocValues}, iterating over only    *  child documents, in order to only select one value per parent among    *  its {@code children} using the configured {@code selection} type. */
DECL|method|wrap
specifier|public
specifier|static
name|NumericDocValues
name|wrap
parameter_list|(
specifier|final
name|NumericDocValues
name|values
parameter_list|,
name|Type
name|selection
parameter_list|,
name|BitSet
name|parents
parameter_list|,
name|BitSet
name|children
parameter_list|)
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
specifier|private
name|int
name|parentDocID
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|value
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|parentDocID
operator|==
operator|-
literal|1
condition|)
block|{
name|values
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
comment|// TODO: make this crazy loop more efficient
name|int
name|childDocID
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|childDocID
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|parentDocID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|parentDocID
return|;
block|}
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|childDocID
argument_list|)
operator|==
literal|false
condition|)
block|{
name|values
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
continue|continue;
block|}
assert|assert
name|parents
operator|.
name|get
argument_list|(
name|childDocID
argument_list|)
operator|==
literal|false
assert|;
name|parentDocID
operator|=
name|parents
operator|.
name|nextSetBit
argument_list|(
name|childDocID
argument_list|)
expr_stmt|;
name|value
operator|=
name|values
operator|.
name|longValue
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|childDocID
operator|=
name|values
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
assert|assert
name|childDocID
operator|!=
name|parentDocID
assert|;
if|if
condition|(
name|childDocID
operator|>
name|parentDocID
condition|)
block|{
break|break;
block|}
switch|switch
condition|(
name|selection
condition|)
block|{
case|case
name|MIN
case|:
name|value
operator|=
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
name|values
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|value
operator|=
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
name|values
operator|.
name|longValue
argument_list|()
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
break|break;
block|}
return|return
name|parentDocID
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|targetParentDocID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|targetParentDocID
operator|<=
name|parentDocID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target must be after the current document: current="
operator|+
name|parentDocID
operator|+
literal|" target="
operator|+
name|targetParentDocID
argument_list|)
throw|;
block|}
if|if
condition|(
name|targetParentDocID
operator|==
literal|0
condition|)
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
name|int
name|firstChild
init|=
name|parents
operator|.
name|prevSetBit
argument_list|(
name|targetParentDocID
operator|-
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|advance
argument_list|(
name|firstChild
argument_list|)
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|parentDocID
operator|=
name|NO_MORE_DOCS
expr_stmt|;
return|return
name|parentDocID
return|;
block|}
else|else
block|{
return|return
name|nextDoc
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|targetParentDocID
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|targetParentDocID
operator|<=
name|parentDocID
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"target must be after the current document: current="
operator|+
name|parentDocID
operator|+
literal|" target="
operator|+
name|targetParentDocID
argument_list|)
throw|;
block|}
name|parentDocID
operator|=
name|targetParentDocID
expr_stmt|;
if|if
condition|(
name|parents
operator|.
name|get
argument_list|(
name|targetParentDocID
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|prevParentDocId
init|=
name|parentDocID
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|parents
operator|.
name|prevSetBit
argument_list|(
name|parentDocID
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|childDoc
init|=
name|values
operator|.
name|docID
argument_list|()
decl_stmt|;
if|if
condition|(
name|childDoc
operator|<=
name|prevParentDocId
condition|)
block|{
name|childDoc
operator|=
name|values
operator|.
name|advance
argument_list|(
name|prevParentDocId
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|childDoc
operator|>=
name|parentDocID
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|hasValue
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|values
operator|.
name|docID
argument_list|()
init|;
name|doc
operator|<
name|parentDocID
condition|;
name|doc
operator|=
name|values
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|value
operator|=
name|values
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|hasValue
operator|=
literal|true
expr_stmt|;
name|values
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|hasValue
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|doc
init|=
name|values
operator|.
name|docID
argument_list|()
init|;
name|doc
operator|<
name|parentDocID
condition|;
name|doc
operator|=
name|values
operator|.
name|nextDoc
argument_list|()
control|)
block|{
if|if
condition|(
name|children
operator|.
name|get
argument_list|(
name|doc
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|selection
condition|)
block|{
case|case
name|MIN
case|:
name|value
operator|=
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
name|values
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|value
operator|=
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
name|values
operator|.
name|longValue
argument_list|()
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
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|parentDocID
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|cost
parameter_list|()
block|{
return|return
name|values
operator|.
name|cost
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

