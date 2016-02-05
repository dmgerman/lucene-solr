begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|facet
operator|.
name|FacetsConfig
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
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesReaderState
operator|.
name|OrdRange
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
name|LeafReader
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
name|IndexReader
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
name|SlowCompositeReaderWrapper
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

begin_comment
comment|/**  * Default implementation of {@link SortedSetDocValuesFacetCounts}  */
end_comment

begin_class
DECL|class|DefaultSortedSetDocValuesReaderState
specifier|public
class|class
name|DefaultSortedSetDocValuesReaderState
extends|extends
name|SortedSetDocValuesReaderState
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|topReader
specifier|private
specifier|final
name|LeafReader
name|topReader
decl_stmt|;
DECL|field|valueCount
specifier|private
specifier|final
name|int
name|valueCount
decl_stmt|;
comment|/** {@link IndexReader} passed to the constructor. */
DECL|field|origReader
specifier|public
specifier|final
name|IndexReader
name|origReader
decl_stmt|;
DECL|field|prefixToOrdRange
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
name|prefixToOrdRange
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Creates this, pulling doc values from the default {@link    *  FacetsConfig#DEFAULT_INDEX_FIELD_NAME}. */
DECL|method|DefaultSortedSetDocValuesReaderState
specifier|public
name|DefaultSortedSetDocValuesReaderState
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|FacetsConfig
operator|.
name|DEFAULT_INDEX_FIELD_NAME
argument_list|)
expr_stmt|;
block|}
comment|/** Creates this, pulling doc values from the specified    *  field. */
DECL|method|DefaultSortedSetDocValuesReaderState
specifier|public
name|DefaultSortedSetDocValuesReaderState
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|origReader
operator|=
name|reader
expr_stmt|;
comment|// We need this to create thread-safe MultiSortedSetDV
comment|// per collector:
name|topReader
operator|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|SortedSetDocValues
name|dv
init|=
name|topReader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field \""
operator|+
name|field
operator|+
literal|"\" was not indexed with SortedSetDocValues"
argument_list|)
throw|;
block|}
if|if
condition|(
name|dv
operator|.
name|getValueCount
argument_list|()
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can only handle valueCount< Integer.MAX_VALUE; got "
operator|+
name|dv
operator|.
name|getValueCount
argument_list|()
argument_list|)
throw|;
block|}
name|valueCount
operator|=
operator|(
name|int
operator|)
name|dv
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
comment|// TODO: we can make this more efficient if eg we can be
comment|// "involved" when OrdinalMap is being created?  Ie see
comment|// each term/ord it's assigning as it goes...
name|String
name|lastDim
init|=
literal|null
decl_stmt|;
name|int
name|startOrd
init|=
operator|-
literal|1
decl_stmt|;
comment|// TODO: this approach can work for full hierarchy?;
comment|// TaxoReader can't do this since ords are not in
comment|// "sorted order" ... but we should generalize this to
comment|// support arbitrary hierarchy:
for|for
control|(
name|int
name|ord
init|=
literal|0
init|;
name|ord
operator|<
name|valueCount
condition|;
name|ord
operator|++
control|)
block|{
specifier|final
name|BytesRef
name|term
init|=
name|dv
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|String
index|[]
name|components
init|=
name|FacetsConfig
operator|.
name|stringToPath
argument_list|(
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|components
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"this class can only handle 2 level hierarchy (dim/value); got: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|components
argument_list|)
operator|+
literal|" "
operator|+
name|term
operator|.
name|utf8ToString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|components
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|lastDim
argument_list|)
condition|)
block|{
if|if
condition|(
name|lastDim
operator|!=
literal|null
condition|)
block|{
name|prefixToOrdRange
operator|.
name|put
argument_list|(
name|lastDim
argument_list|,
operator|new
name|OrdRange
argument_list|(
name|startOrd
argument_list|,
name|ord
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startOrd
operator|=
name|ord
expr_stmt|;
name|lastDim
operator|=
name|components
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastDim
operator|!=
literal|null
condition|)
block|{
name|prefixToOrdRange
operator|.
name|put
argument_list|(
name|lastDim
argument_list|,
operator|new
name|OrdRange
argument_list|(
name|startOrd
argument_list|,
name|valueCount
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Return top-level doc values. */
annotation|@
name|Override
DECL|method|getDocValues
specifier|public
name|SortedSetDocValues
name|getDocValues
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|topReader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Returns mapping from prefix to {@link OrdRange}. */
annotation|@
name|Override
DECL|method|getPrefixToOrdRange
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
name|getPrefixToOrdRange
parameter_list|()
block|{
return|return
name|prefixToOrdRange
return|;
block|}
comment|/** Returns the {@link OrdRange} for this dimension. */
annotation|@
name|Override
DECL|method|getOrdRange
specifier|public
name|OrdRange
name|getOrdRange
parameter_list|(
name|String
name|dim
parameter_list|)
block|{
return|return
name|prefixToOrdRange
operator|.
name|get
argument_list|(
name|dim
argument_list|)
return|;
block|}
comment|/** Indexed field we are reading. */
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|getOrigReader
specifier|public
name|IndexReader
name|getOrigReader
parameter_list|()
block|{
return|return
name|origReader
return|;
block|}
comment|/** Number of unique labels. */
annotation|@
name|Override
DECL|method|getSize
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|valueCount
return|;
block|}
block|}
end_class

end_unit

