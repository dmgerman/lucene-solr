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
comment|/**   * This class contains utility methods and constants for DocValues  */
end_comment

begin_class
DECL|class|DocValues
specifier|public
specifier|final
class|class
name|DocValues
block|{
comment|/* no instantiation */
DECL|method|DocValues
specifier|private
name|DocValues
parameter_list|()
block|{}
comment|/**     * An empty {@link BinaryDocValues} which returns no documents    */
DECL|method|emptyBinary
specifier|public
specifier|static
specifier|final
name|BinaryDocValues
name|emptyBinary
parameter_list|()
block|{
return|return
operator|new
name|BinaryDocValues
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
expr_stmt|;
return|return
literal|true
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
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
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
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
block|{
assert|assert
literal|false
assert|;
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
comment|/**     * An empty NumericDocValues which returns no documents    */
DECL|method|emptyNumeric
specifier|public
specifier|static
specifier|final
name|NumericDocValues
name|emptyNumeric
parameter_list|()
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
expr_stmt|;
return|return
literal|false
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
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
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
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
block|{
assert|assert
literal|false
assert|;
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
comment|/**     * An empty SortedDocValues which returns {@link BytesRef#EMPTY_BYTES} for every document     */
DECL|method|emptyLegacySorted
specifier|public
specifier|static
specifier|final
name|LegacySortedDocValues
name|emptyLegacySorted
parameter_list|()
block|{
specifier|final
name|BytesRef
name|empty
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
return|return
operator|new
name|LegacySortedDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
operator|-
literal|1
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
block|{
return|return
name|empty
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
literal|0
return|;
block|}
block|}
return|;
block|}
comment|/**     * An empty SortedDocValues which returns {@link BytesRef#EMPTY_BYTES} for every document    */
DECL|method|emptySorted
specifier|public
specifier|static
specifier|final
name|SortedDocValues
name|emptySorted
parameter_list|()
block|{
specifier|final
name|BytesRef
name|empty
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
return|return
operator|new
name|SortedDocValues
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
expr_stmt|;
return|return
literal|false
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
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
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
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|ordValue
parameter_list|()
block|{
assert|assert
literal|false
assert|;
return|return
operator|-
literal|1
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
block|{
return|return
name|empty
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
literal|0
return|;
block|}
block|}
return|;
block|}
comment|/**    * An empty SortedNumericDocValues which returns zero values for every document     */
DECL|method|emptySortedNumeric
specifier|public
specifier|static
specifier|final
name|SortedNumericDocValues
name|emptySortedNumeric
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
return|return
operator|new
name|SortedNumericDocValues
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
expr_stmt|;
return|return
literal|false
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
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
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
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|docValueCount
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
comment|/**     * An empty SortedDocValues which returns {@link BytesRef#EMPTY_BYTES} for every document    */
DECL|method|emptySortedSet
specifier|public
specifier|static
specifier|final
name|SortedSetDocValues
name|emptySortedSet
parameter_list|()
block|{
specifier|final
name|BytesRef
name|empty
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
return|return
operator|new
name|SortedSetDocValues
argument_list|()
block|{
specifier|private
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|advanceExact
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
expr_stmt|;
return|return
literal|false
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
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|nextDoc
parameter_list|()
block|{
return|return
name|doc
operator|=
name|NO_MORE_DOCS
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
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
assert|assert
literal|false
assert|;
return|return
name|NO_MORE_ORDS
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|lookupOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|empty
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getValueCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
return|;
block|}
comment|/**     * Returns a multi-valued view over the provided SortedDocValues    */
DECL|method|singleton
specifier|public
specifier|static
name|SortedSetDocValues
name|singleton
parameter_list|(
name|SortedDocValues
name|dv
parameter_list|)
block|{
return|return
operator|new
name|SingletonSortedSetDocValues
argument_list|(
name|dv
argument_list|)
return|;
block|}
comment|/**     * Returns a single-valued view of the SortedSetDocValues, if it was previously    * wrapped with {@link #singleton(SortedDocValues)}, or null.    */
DECL|method|unwrapSingleton
specifier|public
specifier|static
name|SortedDocValues
name|unwrapSingleton
parameter_list|(
name|SortedSetDocValues
name|dv
parameter_list|)
block|{
if|if
condition|(
name|dv
operator|instanceof
name|SingletonSortedSetDocValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonSortedSetDocValues
operator|)
name|dv
operator|)
operator|.
name|getSortedDocValues
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**     * Returns a single-valued view of the SortedNumericDocValues, if it was previously    * wrapped with {@link #singleton(NumericDocValues)}, or null.    */
DECL|method|unwrapSingleton
specifier|public
specifier|static
name|NumericDocValues
name|unwrapSingleton
parameter_list|(
name|SortedNumericDocValues
name|dv
parameter_list|)
block|{
if|if
condition|(
name|dv
operator|instanceof
name|SingletonSortedNumericDocValues
condition|)
block|{
return|return
operator|(
operator|(
name|SingletonSortedNumericDocValues
operator|)
name|dv
operator|)
operator|.
name|getNumericDocValues
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Returns a multi-valued view over the provided NumericDocValues    */
DECL|method|singleton
specifier|public
specifier|static
name|SortedNumericDocValues
name|singleton
parameter_list|(
name|NumericDocValues
name|dv
parameter_list|)
block|{
return|return
operator|new
name|SingletonSortedNumericDocValues
argument_list|(
name|dv
argument_list|)
return|;
block|}
comment|// some helpers, for transition from fieldcache apis.
comment|// as opposed to the LeafReader apis (which must be strict for consistency), these are lenient
comment|// helper method: to give a nice error when LeafReader.getXXXDocValues returns null.
DECL|method|checkField
specifier|private
specifier|static
name|void
name|checkField
parameter_list|(
name|LeafReader
name|in
parameter_list|,
name|String
name|field
parameter_list|,
name|DocValuesType
modifier|...
name|expected
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|in
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|!=
literal|null
condition|)
block|{
name|DocValuesType
name|actual
init|=
name|fi
operator|.
name|getDocValuesType
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unexpected docvalues type "
operator|+
name|actual
operator|+
literal|" for field '"
operator|+
name|field
operator|+
literal|"' "
operator|+
operator|(
name|expected
operator|.
name|length
operator|==
literal|1
condition|?
literal|"(expected="
operator|+
name|expected
index|[
literal|0
index|]
else|:
literal|"(expected one of "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
operator|)
operator|+
literal|"). "
operator|+
literal|"Re-index with correct docvalues type."
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns NumericDocValues for the field, or {@link #emptyNumeric()} if it has none.    * @return docvalues instance, or an empty instance if {@code field} does not exist in this reader.    * @throws IllegalStateException if {@code field} exists, but was not indexed with docvalues.    * @throws IllegalStateException if {@code field} has docvalues, but the type is not {@link DocValuesType#NUMERIC}.    * @throws IOException if an I/O error occurs.    */
DECL|method|getNumeric
specifier|public
specifier|static
name|NumericDocValues
name|getNumeric
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|NumericDocValues
name|dv
init|=
name|reader
operator|.
name|getNumericDocValues
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
name|checkField
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
return|return
name|emptyNumeric
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|dv
return|;
block|}
block|}
comment|/**    * Returns BinaryDocValues for the field, or {@link #emptyBinary} if it has none.    * @return docvalues instance, or an empty instance if {@code field} does not exist in this reader.    * @throws IllegalStateException if {@code field} exists, but was not indexed with docvalues.    * @throws IllegalStateException if {@code field} has docvalues, but the type is not {@link DocValuesType#BINARY}    *                               or {@link DocValuesType#SORTED}.    * @throws IOException if an I/O error occurs.    */
DECL|method|getBinary
specifier|public
specifier|static
name|BinaryDocValues
name|getBinary
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|BinaryDocValues
name|dv
init|=
name|reader
operator|.
name|getBinaryDocValues
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
name|dv
operator|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|dv
operator|==
literal|null
condition|)
block|{
name|checkField
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|DocValuesType
operator|.
name|BINARY
argument_list|,
name|DocValuesType
operator|.
name|SORTED
argument_list|)
expr_stmt|;
return|return
name|emptyBinary
argument_list|()
return|;
block|}
block|}
return|return
name|dv
return|;
block|}
comment|/**    * Returns SortedDocValues for the field, or {@link #emptySorted} if it has none.    * @return docvalues instance, or an empty instance if {@code field} does not exist in this reader.    * @throws IllegalStateException if {@code field} exists, but was not indexed with docvalues.    * @throws IllegalStateException if {@code field} has docvalues, but the type is not {@link DocValuesType#SORTED}.    * @throws IOException if an I/O error occurs.    */
DECL|method|getSorted
specifier|public
specifier|static
name|SortedDocValues
name|getSorted
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedDocValues
name|dv
init|=
name|reader
operator|.
name|getSortedDocValues
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
name|checkField
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|DocValuesType
operator|.
name|SORTED
argument_list|)
expr_stmt|;
return|return
name|emptySorted
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|dv
return|;
block|}
block|}
comment|/**    * Returns SortedNumericDocValues for the field, or {@link #emptySortedNumeric} if it has none.    * @return docvalues instance, or an empty instance if {@code field} does not exist in this reader.    * @throws IllegalStateException if {@code field} exists, but was not indexed with docvalues.    * @throws IllegalStateException if {@code field} has docvalues, but the type is not {@link DocValuesType#SORTED_NUMERIC}    *                               or {@link DocValuesType#NUMERIC}.    * @throws IOException if an I/O error occurs.    */
DECL|method|getSortedNumeric
specifier|public
specifier|static
name|SortedNumericDocValues
name|getSortedNumeric
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedNumericDocValues
name|dv
init|=
name|reader
operator|.
name|getSortedNumericDocValues
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
name|NumericDocValues
name|single
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|single
operator|==
literal|null
condition|)
block|{
name|checkField
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|DocValuesType
operator|.
name|NUMERIC
argument_list|)
expr_stmt|;
return|return
name|emptySortedNumeric
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
return|return
name|singleton
argument_list|(
name|single
argument_list|)
return|;
block|}
return|return
name|dv
return|;
block|}
comment|/**    * Returns SortedSetDocValues for the field, or {@link #emptySortedSet} if it has none.     * @return docvalues instance, or an empty instance if {@code field} does not exist in this reader.    * @throws IllegalStateException if {@code field} exists, but was not indexed with docvalues.    * @throws IllegalStateException if {@code field} has docvalues, but the type is not {@link DocValuesType#SORTED_SET}    *                               or {@link DocValuesType#SORTED}.    * @throws IOException if an I/O error occurs.    */
DECL|method|getSortedSet
specifier|public
specifier|static
name|SortedSetDocValues
name|getSortedSet
parameter_list|(
name|LeafReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|SortedSetDocValues
name|dv
init|=
name|reader
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
name|SortedDocValues
name|sorted
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sorted
operator|==
literal|null
condition|)
block|{
name|checkField
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|DocValuesType
operator|.
name|SORTED
argument_list|,
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|)
expr_stmt|;
return|return
name|emptySortedSet
argument_list|()
return|;
block|}
name|dv
operator|=
name|singleton
argument_list|(
name|sorted
argument_list|)
expr_stmt|;
block|}
return|return
name|dv
return|;
block|}
block|}
end_class

end_unit

