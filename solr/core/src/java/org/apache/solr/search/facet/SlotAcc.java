begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.facet
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LeafReaderContext
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
name|MultiDocValues
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|LongValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|SimpleOrderedMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SchemaField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|List
import|;
end_import

begin_class
DECL|class|SlotAcc
specifier|public
specifier|abstract
class|class
name|SlotAcc
implements|implements
name|Closeable
block|{
DECL|field|key
name|String
name|key
decl_stmt|;
comment|// todo...
DECL|field|fcontext
specifier|protected
specifier|final
name|FacetContext
name|fcontext
decl_stmt|;
DECL|method|SlotAcc
specifier|public
name|SlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|)
block|{
name|this
operator|.
name|fcontext
operator|=
name|fcontext
expr_stmt|;
block|}
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{   }
DECL|method|collect
specifier|public
specifier|abstract
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|collect
specifier|public
name|int
name|collect
parameter_list|(
name|DocSet
name|docs
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|fcontext
operator|.
name|searcher
decl_stmt|;
specifier|final
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|leaves
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|LeafReaderContext
argument_list|>
name|ctxIt
init|=
name|leaves
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|LeafReaderContext
name|ctx
init|=
literal|null
decl_stmt|;
name|int
name|segBase
init|=
literal|0
decl_stmt|;
name|int
name|segMax
decl_stmt|;
name|int
name|adjustedMax
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DocIterator
name|docsIt
init|=
name|docs
operator|.
name|iterator
argument_list|()
init|;
name|docsIt
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|doc
init|=
name|docsIt
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|>=
name|adjustedMax
condition|)
block|{
do|do
block|{
name|ctx
operator|=
name|ctxIt
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
comment|// should be impossible
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"INTERNAL FACET ERROR"
argument_list|)
throw|;
block|}
name|segBase
operator|=
name|ctx
operator|.
name|docBase
expr_stmt|;
name|segMax
operator|=
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|adjustedMax
operator|=
name|segBase
operator|+
name|segMax
expr_stmt|;
block|}
do|while
condition|(
name|doc
operator|>=
name|adjustedMax
condition|)
do|;
assert|assert
name|doc
operator|>=
name|ctx
operator|.
name|docBase
assert|;
name|setNextReader
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
name|collect
argument_list|(
name|doc
operator|-
name|segBase
argument_list|,
name|slot
argument_list|)
expr_stmt|;
comment|// per-seg collectors
block|}
return|return
name|count
return|;
block|}
DECL|method|compare
specifier|public
specifier|abstract
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
function_decl|;
DECL|method|getValue
specifier|public
specifier|abstract
name|Object
name|getValue
parameter_list|(
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|setValues
specifier|public
name|void
name|setValues
parameter_list|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|bucket
parameter_list|,
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
return|return;
name|bucket
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|getValue
argument_list|(
name|slotNum
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|reset
specifier|public
specifier|abstract
name|void
name|reset
parameter_list|()
function_decl|;
DECL|method|resize
specifier|public
specifier|abstract
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|class|Resizer
specifier|public
specifier|static
specifier|abstract
class|class
name|Resizer
block|{
DECL|method|getNewSize
specifier|public
specifier|abstract
name|int
name|getNewSize
parameter_list|()
function_decl|;
DECL|method|getNewSlot
specifier|public
specifier|abstract
name|int
name|getNewSlot
parameter_list|(
name|int
name|oldSlot
parameter_list|)
function_decl|;
DECL|method|resize
specifier|public
name|double
index|[]
name|resize
parameter_list|(
name|double
index|[]
name|old
parameter_list|,
name|double
name|defaultValue
parameter_list|)
block|{
name|double
index|[]
name|values
init|=
operator|new
name|double
index|[
name|getNewSize
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|defaultValue
operator|!=
literal|0
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|,
name|defaultValue
argument_list|)
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
name|old
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|double
name|val
init|=
name|old
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|!=
name|defaultValue
condition|)
block|{
name|int
name|newSlot
init|=
name|getNewSlot
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSlot
operator|>=
literal|0
condition|)
block|{
name|values
index|[
name|newSlot
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
DECL|method|resize
specifier|public
name|int
index|[]
name|resize
parameter_list|(
name|int
index|[]
name|old
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|getNewSize
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|defaultValue
operator|!=
literal|0
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|,
name|defaultValue
argument_list|)
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
name|old
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|val
init|=
name|old
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|!=
name|defaultValue
condition|)
block|{
name|int
name|newSlot
init|=
name|getNewSlot
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSlot
operator|>=
literal|0
condition|)
block|{
name|values
index|[
name|newSlot
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
DECL|method|resize
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|resize
parameter_list|(
name|T
index|[]
name|old
parameter_list|,
name|T
name|defaultValue
parameter_list|)
block|{
name|T
index|[]
name|values
init|=
operator|(
name|T
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|old
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
argument_list|,
name|getNewSize
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultValue
operator|!=
literal|null
condition|)
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|,
name|defaultValue
argument_list|)
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
name|old
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|T
name|val
init|=
name|old
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|val
operator|!=
name|defaultValue
condition|)
block|{
name|int
name|newSlot
init|=
name|getNewSlot
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|newSlot
operator|>=
literal|0
condition|)
block|{
name|values
index|[
name|newSlot
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
block|}
comment|// end class Resizer
block|}
end_class

begin_comment
comment|// TODO: we should really have a decoupled value provider...
end_comment

begin_comment
comment|// This would enhance reuse and also prevent multiple lookups of same value across diff stats
end_comment

begin_class
DECL|class|FuncSlotAcc
specifier|abstract
class|class
name|FuncSlotAcc
extends|extends
name|SlotAcc
block|{
DECL|field|valueSource
specifier|protected
specifier|final
name|ValueSource
name|valueSource
decl_stmt|;
DECL|field|values
specifier|protected
name|FunctionValues
name|values
decl_stmt|;
DECL|method|FuncSlotAcc
specifier|public
name|FuncSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|this
operator|.
name|valueSource
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|LeafReaderContext
name|readerContext
parameter_list|)
throws|throws
name|IOException
block|{
name|values
operator|=
name|valueSource
operator|.
name|getValues
argument_list|(
name|fcontext
operator|.
name|qcontext
argument_list|,
name|readerContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// have a version that counts the number of times a Slot has been hit?  (for avg... what else?)
end_comment

begin_comment
comment|// TODO: make more sense to have func as the base class rather than double?
end_comment

begin_comment
comment|// double-slot-func -> func-slot -> slot -> acc
end_comment

begin_comment
comment|// double-slot-func -> double-slot -> slot -> acc
end_comment

begin_class
DECL|class|DoubleFuncSlotAcc
specifier|abstract
class|class
name|DoubleFuncSlotAcc
extends|extends
name|FuncSlotAcc
block|{
DECL|field|result
name|double
index|[]
name|result
decl_stmt|;
comment|// TODO: use DoubleArray
DECL|field|initialValue
name|double
name|initialValue
decl_stmt|;
DECL|method|DoubleFuncSlotAcc
specifier|public
name|DoubleFuncSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|this
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|DoubleFuncSlotAcc
specifier|public
name|DoubleFuncSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|double
name|initialValue
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
name|this
operator|.
name|initialValue
operator|=
name|initialValue
expr_stmt|;
name|result
operator|=
operator|new
name|double
index|[
name|numSlots
index|]
expr_stmt|;
if|if
condition|(
name|initialValue
operator|!=
literal|0
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|result
index|[
name|slotA
index|]
argument_list|,
name|result
index|[
name|slotB
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|result
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|initialValue
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|result
operator|=
name|resizer
operator|.
name|resize
argument_list|(
name|result
argument_list|,
name|initialValue
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|IntSlotAcc
specifier|abstract
class|class
name|IntSlotAcc
extends|extends
name|SlotAcc
block|{
DECL|field|result
name|int
index|[]
name|result
decl_stmt|;
comment|// use LongArray32
DECL|field|initialValue
name|int
name|initialValue
decl_stmt|;
DECL|method|IntSlotAcc
specifier|public
name|IntSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|,
name|int
name|initialValue
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|this
operator|.
name|initialValue
operator|=
name|initialValue
expr_stmt|;
name|result
operator|=
operator|new
name|int
index|[
name|numSlots
index|]
expr_stmt|;
if|if
condition|(
name|initialValue
operator|!=
literal|0
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|result
index|[
name|slotA
index|]
argument_list|,
name|result
index|[
name|slotB
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|result
index|[
name|slot
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|initialValue
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|result
operator|=
name|resizer
operator|.
name|resize
argument_list|(
name|result
argument_list|,
name|initialValue
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|SumSlotAcc
class|class
name|SumSlotAcc
extends|extends
name|DoubleFuncSlotAcc
block|{
DECL|method|SumSlotAcc
specifier|public
name|SumSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
block|}
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|// todo: worth trying to share this value across multiple stats that need it?
name|result
index|[
name|slotNum
index|]
operator|+=
name|val
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|SumsqSlotAcc
class|class
name|SumsqSlotAcc
extends|extends
name|DoubleFuncSlotAcc
block|{
DECL|method|SumsqSlotAcc
specifier|public
name|SumsqSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|val
operator|=
name|val
operator|*
name|val
expr_stmt|;
name|result
index|[
name|slotNum
index|]
operator|+=
name|val
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|MinSlotAcc
class|class
name|MinSlotAcc
extends|extends
name|DoubleFuncSlotAcc
block|{
DECL|method|MinSlotAcc
specifier|public
name|MinSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|0
operator|&&
operator|!
name|values
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
return|return;
comment|// depend on fact that non existing values return 0 for func query
name|double
name|currMin
init|=
name|result
index|[
name|slotNum
index|]
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|val
operator|>=
name|currMin
operator|)
condition|)
block|{
comment|// val>=currMin will be false for staring value: val>=NaN
name|result
index|[
name|slotNum
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|MaxSlotAcc
class|class
name|MaxSlotAcc
extends|extends
name|DoubleFuncSlotAcc
block|{
DECL|method|MaxSlotAcc
specifier|public
name|MaxSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|,
name|Double
operator|.
name|NaN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|0
operator|&&
operator|!
name|values
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
return|return;
comment|// depend on fact that non existing values return 0 for func query
name|double
name|currMax
init|=
name|result
index|[
name|slotNum
index|]
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|val
operator|<=
name|currMax
operator|)
condition|)
block|{
comment|// reversed order to handle NaN
name|result
index|[
name|slotNum
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|AvgSlotAcc
class|class
name|AvgSlotAcc
extends|extends
name|DoubleFuncSlotAcc
block|{
DECL|field|counts
name|int
index|[]
name|counts
decl_stmt|;
DECL|method|AvgSlotAcc
specifier|public
name|AvgSlotAcc
parameter_list|(
name|ValueSource
name|values
parameter_list|,
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|values
argument_list|,
name|fcontext
argument_list|,
name|numSlots
argument_list|)
expr_stmt|;
name|counts
operator|=
operator|new
name|int
index|[
name|numSlots
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
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
name|counts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|counts
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
name|double
name|val
init|=
name|values
operator|.
name|doubleVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|0
operator|||
name|values
operator|.
name|exists
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|result
index|[
name|slotNum
index|]
operator|+=
name|val
expr_stmt|;
name|counts
index|[
name|slotNum
index|]
operator|+=
literal|1
expr_stmt|;
block|}
block|}
DECL|method|avg
specifier|private
name|double
name|avg
parameter_list|(
name|double
name|tot
parameter_list|,
name|int
name|count
parameter_list|)
block|{
return|return
name|count
operator|==
literal|0
condition|?
literal|0
else|:
name|tot
operator|/
name|count
return|;
comment|// returns 0 instead of NaN.. todo - make configurable? if NaN, we need to handle comparisons though...
block|}
DECL|method|avg
specifier|private
name|double
name|avg
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|avg
argument_list|(
name|result
index|[
name|slot
index|]
argument_list|,
name|counts
index|[
name|slot
index|]
argument_list|)
return|;
comment|// calc once and cache in result?
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|avg
argument_list|(
name|slotA
argument_list|)
argument_list|,
name|avg
argument_list|(
name|slotB
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
if|if
condition|(
name|fcontext
operator|.
name|isShard
argument_list|()
condition|)
block|{
name|ArrayList
name|lst
init|=
operator|new
name|ArrayList
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|counts
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
name|result
index|[
name|slot
index|]
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
else|else
block|{
return|return
name|avg
argument_list|(
name|slot
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|super
operator|.
name|resize
argument_list|(
name|resizer
argument_list|)
expr_stmt|;
name|counts
operator|=
name|resizer
operator|.
name|resize
argument_list|(
name|counts
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|CountSlotAcc
specifier|abstract
class|class
name|CountSlotAcc
extends|extends
name|SlotAcc
block|{
DECL|method|CountSlotAcc
specifier|public
name|CountSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementCount
specifier|public
specifier|abstract
name|void
name|incrementCount
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
DECL|method|getCount
specifier|public
specifier|abstract
name|int
name|getCount
parameter_list|(
name|int
name|slot
parameter_list|)
function_decl|;
block|}
end_class

begin_class
DECL|class|CountSlotArrAcc
class|class
name|CountSlotArrAcc
extends|extends
name|CountSlotAcc
block|{
DECL|field|result
name|int
index|[]
name|result
decl_stmt|;
DECL|method|CountSlotArrAcc
specifier|public
name|CountSlotArrAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|,
name|int
name|numSlots
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|int
index|[
name|numSlots
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slotNum
parameter_list|)
block|{
comment|// TODO: count arrays can use fewer bytes based on the number of docs in the base set (that's the upper bound for single valued) - look at ttf?
name|result
index|[
name|slotNum
index|]
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|result
index|[
name|slotA
index|]
argument_list|,
name|result
index|[
name|slotB
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slotNum
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|result
index|[
name|slotNum
index|]
return|;
block|}
DECL|method|incrementCount
specifier|public
name|void
name|incrementCount
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|count
parameter_list|)
block|{
name|result
index|[
name|slot
index|]
operator|+=
name|count
expr_stmt|;
block|}
DECL|method|getCount
specifier|public
name|int
name|getCount
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|result
index|[
name|slot
index|]
return|;
block|}
comment|// internal and expert
DECL|method|getCountArray
name|int
index|[]
name|getCountArray
parameter_list|()
block|{
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|Arrays
operator|.
name|fill
argument_list|(
name|result
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
name|resizer
operator|.
name|resize
argument_list|(
name|result
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_class
DECL|class|SortSlotAcc
class|class
name|SortSlotAcc
extends|extends
name|SlotAcc
block|{
DECL|method|SortSlotAcc
specifier|public
name|SortSlotAcc
parameter_list|(
name|FacetContext
name|fcontext
parameter_list|)
block|{
name|super
argument_list|(
name|fcontext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|slot
parameter_list|)
throws|throws
name|IOException
block|{
comment|// no-op
block|}
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slotA
parameter_list|,
name|int
name|slotB
parameter_list|)
block|{
return|return
name|slotA
operator|-
name|slotB
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Object
name|getValue
parameter_list|(
name|int
name|slotNum
parameter_list|)
block|{
return|return
name|slotNum
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|resize
specifier|public
name|void
name|resize
parameter_list|(
name|Resizer
name|resizer
parameter_list|)
block|{
comment|// sort slot only works with direct-mapped accumulators
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

