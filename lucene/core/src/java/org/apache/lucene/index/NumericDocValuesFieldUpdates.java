begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericDocValuesField
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
name|DocIdSetIterator
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
name|InPlaceMergeSorter
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
name|PagedGrowableWriter
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
name|PagedMutable
import|;
end_import

begin_comment
comment|/**  * A {@link DocValuesFieldUpdates} which holds updates of documents, of a single  * {@link NumericDocValuesField}.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|NumericDocValuesFieldUpdates
class|class
name|NumericDocValuesFieldUpdates
extends|extends
name|DocValuesFieldUpdates
block|{
comment|// TODO: can't this just be NumericDocValues now?  avoid boxing the long value...
DECL|class|Iterator
specifier|final
specifier|static
class|class
name|Iterator
extends|extends
name|DocValuesFieldUpdates
operator|.
name|Iterator
block|{
DECL|field|size
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|PagedGrowableWriter
name|values
decl_stmt|;
DECL|field|docs
specifier|private
specifier|final
name|PagedMutable
name|docs
decl_stmt|;
DECL|field|idx
specifier|private
name|long
name|idx
init|=
literal|0
decl_stmt|;
comment|// long so we don't overflow if size == Integer.MAX_VALUE
DECL|field|doc
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|value
specifier|private
name|Long
name|value
init|=
literal|null
decl_stmt|;
DECL|method|Iterator
name|Iterator
parameter_list|(
name|int
name|size
parameter_list|,
name|PagedGrowableWriter
name|values
parameter_list|,
name|PagedMutable
name|docs
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|value
name|Long
name|value
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|nextDoc
name|int
name|nextDoc
parameter_list|()
block|{
if|if
condition|(
name|idx
operator|>=
name|size
condition|)
block|{
name|value
operator|=
literal|null
expr_stmt|;
return|return
name|doc
operator|=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
return|;
block|}
name|doc
operator|=
operator|(
name|int
operator|)
name|docs
operator|.
name|get
argument_list|(
name|idx
argument_list|)
expr_stmt|;
operator|++
name|idx
expr_stmt|;
while|while
condition|(
name|idx
operator|<
name|size
operator|&&
name|docs
operator|.
name|get
argument_list|(
name|idx
argument_list|)
operator|==
name|doc
condition|)
block|{
operator|++
name|idx
expr_stmt|;
block|}
comment|// idx points to the "next" element
name|value
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|idx
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|doc
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|reset
name|void
name|reset
parameter_list|()
block|{
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
name|idx
operator|=
literal|0
expr_stmt|;
block|}
block|}
DECL|field|bitsPerValue
specifier|private
specifier|final
name|int
name|bitsPerValue
decl_stmt|;
DECL|field|docs
specifier|private
name|PagedMutable
name|docs
decl_stmt|;
DECL|field|values
specifier|private
name|PagedGrowableWriter
name|values
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|method|NumericDocValuesFieldUpdates
specifier|public
name|NumericDocValuesFieldUpdates
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
name|bitsPerValue
operator|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxDoc
operator|-
literal|1
argument_list|)
expr_stmt|;
name|docs
operator|=
operator|new
name|PagedMutable
argument_list|(
literal|1
argument_list|,
name|PAGE_SIZE
argument_list|,
name|bitsPerValue
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|PagedGrowableWriter
argument_list|(
literal|1
argument_list|,
name|PAGE_SIZE
argument_list|,
literal|1
argument_list|,
name|PackedInts
operator|.
name|FAST
argument_list|)
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|doc
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// TODO: if the Sorter interface changes to take long indexes, we can remove that limitation
if|if
condition|(
name|size
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot support more than Integer.MAX_VALUE doc/value entries"
argument_list|)
throw|;
block|}
name|Long
name|val
init|=
operator|(
name|Long
operator|)
name|value
decl_stmt|;
comment|// grow the structures to have room for more elements
if|if
condition|(
name|docs
operator|.
name|size
argument_list|()
operator|==
name|size
condition|)
block|{
name|docs
operator|=
name|docs
operator|.
name|grow
argument_list|(
name|size
operator|+
literal|1
argument_list|)
expr_stmt|;
name|values
operator|=
name|values
operator|.
name|grow
argument_list|(
name|size
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|docs
operator|.
name|set
argument_list|(
name|size
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|values
operator|.
name|set
argument_list|(
name|size
argument_list|,
name|val
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
specifier|final
name|PagedMutable
name|docs
init|=
name|this
operator|.
name|docs
decl_stmt|;
specifier|final
name|PagedGrowableWriter
name|values
init|=
name|this
operator|.
name|values
decl_stmt|;
operator|new
name|InPlaceMergeSorter
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|swap
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|long
name|tmpDoc
init|=
name|docs
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|docs
operator|.
name|set
argument_list|(
name|j
argument_list|,
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|tmpDoc
argument_list|)
expr_stmt|;
name|long
name|tmpVal
init|=
name|values
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|values
operator|.
name|set
argument_list|(
name|j
argument_list|,
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|tmpVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|compare
parameter_list|(
name|int
name|i
parameter_list|,
name|int
name|j
parameter_list|)
block|{
name|int
name|x
init|=
operator|(
name|int
operator|)
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|y
init|=
operator|(
name|int
operator|)
name|docs
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
return|return
operator|(
name|x
operator|<
name|y
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|x
operator|==
name|y
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
block|}
operator|.
name|sort
argument_list|(
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
operator|new
name|Iterator
argument_list|(
name|size
argument_list|,
name|values
argument_list|,
name|docs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|DocValuesFieldUpdates
name|other
parameter_list|)
block|{
assert|assert
name|other
operator|instanceof
name|NumericDocValuesFieldUpdates
assert|;
name|NumericDocValuesFieldUpdates
name|otherUpdates
init|=
operator|(
name|NumericDocValuesFieldUpdates
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|otherUpdates
operator|.
name|size
operator|>
name|Integer
operator|.
name|MAX_VALUE
operator|-
name|size
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot support more than Integer.MAX_VALUE doc/value entries; size="
operator|+
name|size
operator|+
literal|" other.size="
operator|+
name|otherUpdates
operator|.
name|size
argument_list|)
throw|;
block|}
name|docs
operator|=
name|docs
operator|.
name|grow
argument_list|(
name|size
operator|+
name|otherUpdates
operator|.
name|size
argument_list|)
expr_stmt|;
name|values
operator|=
name|values
operator|.
name|grow
argument_list|(
name|size
operator|+
name|otherUpdates
operator|.
name|size
argument_list|)
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
name|otherUpdates
operator|.
name|size
condition|;
name|i
operator|++
control|)
block|{
name|int
name|doc
init|=
operator|(
name|int
operator|)
name|otherUpdates
operator|.
name|docs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|docs
operator|.
name|set
argument_list|(
name|size
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|values
operator|.
name|set
argument_list|(
name|size
argument_list|,
name|otherUpdates
operator|.
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
operator|++
name|size
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|any
specifier|public
name|boolean
name|any
parameter_list|()
block|{
return|return
name|size
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesPerDoc
specifier|public
name|long
name|ramBytesPerDoc
parameter_list|()
block|{
name|long
name|bytesPerDoc
init|=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
call|(
name|double
call|)
argument_list|(
name|bitsPerValue
argument_list|)
operator|/
literal|8
argument_list|)
decl_stmt|;
specifier|final
name|int
name|capacity
init|=
name|estimateCapacity
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|bytesPerDoc
operator|+=
operator|(
name|long
operator|)
name|Math
operator|.
name|ceil
argument_list|(
operator|(
name|double
operator|)
name|values
operator|.
name|ramBytesUsed
argument_list|()
operator|/
name|capacity
argument_list|)
expr_stmt|;
comment|// values
return|return
name|bytesPerDoc
return|;
block|}
block|}
end_class

end_unit

