begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|analysis
operator|.
name|Analyzer
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
name|IndexOptions
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
name|IndexableFieldType
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
name|NumericRangeQuery
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
name|NumericUtils
import|;
end_import

begin_comment
comment|/**  * Describes the properties of a field.  */
end_comment

begin_class
DECL|class|FieldType
specifier|public
class|class
name|FieldType
implements|implements
name|IndexableFieldType
block|{
comment|/** Data type of the numeric value    * @since 3.2    */
DECL|enum|NumericType
specifier|public
enum|enum
name|NumericType
block|{
comment|/** 32-bit integer numeric type */
DECL|enum constant|INT
name|INT
block|,
comment|/** 64-bit long numeric type */
DECL|enum constant|LONG
name|LONG
block|,
comment|/** 32-bit float numeric type */
DECL|enum constant|FLOAT
name|FLOAT
block|,
comment|/** 64-bit double numeric type */
DECL|enum constant|DOUBLE
name|DOUBLE
block|}
DECL|field|stored
specifier|private
name|boolean
name|stored
decl_stmt|;
DECL|field|tokenized
specifier|private
name|boolean
name|tokenized
init|=
literal|true
decl_stmt|;
DECL|field|storeTermVectors
specifier|private
name|boolean
name|storeTermVectors
decl_stmt|;
DECL|field|storeTermVectorOffsets
specifier|private
name|boolean
name|storeTermVectorOffsets
decl_stmt|;
DECL|field|storeTermVectorPositions
specifier|private
name|boolean
name|storeTermVectorPositions
decl_stmt|;
DECL|field|storeTermVectorPayloads
specifier|private
name|boolean
name|storeTermVectorPayloads
decl_stmt|;
DECL|field|omitNorms
specifier|private
name|boolean
name|omitNorms
decl_stmt|;
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
init|=
name|IndexOptions
operator|.
name|NONE
decl_stmt|;
DECL|field|numericType
specifier|private
name|NumericType
name|numericType
decl_stmt|;
DECL|field|frozen
specifier|private
name|boolean
name|frozen
decl_stmt|;
DECL|field|numericPrecisionStep
specifier|private
name|int
name|numericPrecisionStep
init|=
name|NumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
decl_stmt|;
DECL|field|docValuesType
specifier|private
name|DocValuesType
name|docValuesType
init|=
name|DocValuesType
operator|.
name|NONE
decl_stmt|;
DECL|field|dimensionCount
specifier|private
name|int
name|dimensionCount
decl_stmt|;
DECL|field|dimensionNumBytes
specifier|private
name|int
name|dimensionNumBytes
decl_stmt|;
comment|/**    * Create a new mutable FieldType with all of the properties from<code>ref</code>    */
DECL|method|FieldType
specifier|public
name|FieldType
parameter_list|(
name|FieldType
name|ref
parameter_list|)
block|{
name|this
operator|.
name|stored
operator|=
name|ref
operator|.
name|stored
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenized
operator|=
name|ref
operator|.
name|tokenized
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectors
operator|=
name|ref
operator|.
name|storeTermVectors
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorOffsets
operator|=
name|ref
operator|.
name|storeTermVectorOffsets
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorPositions
operator|=
name|ref
operator|.
name|storeTermVectorPositions
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorPayloads
operator|=
name|ref
operator|.
name|storeTermVectorPayloads
argument_list|()
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|ref
operator|.
name|omitNorms
argument_list|()
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|ref
operator|.
name|indexOptions
argument_list|()
expr_stmt|;
name|this
operator|.
name|numericType
operator|=
name|ref
operator|.
name|numericType
argument_list|()
expr_stmt|;
name|this
operator|.
name|numericPrecisionStep
operator|=
name|ref
operator|.
name|numericPrecisionStep
argument_list|()
expr_stmt|;
name|this
operator|.
name|docValuesType
operator|=
name|ref
operator|.
name|docValuesType
argument_list|()
expr_stmt|;
name|this
operator|.
name|dimensionCount
operator|=
name|dimensionCount
expr_stmt|;
name|this
operator|.
name|dimensionNumBytes
operator|=
name|dimensionNumBytes
expr_stmt|;
comment|// Do not copy frozen!
block|}
comment|/**    * Create a new FieldType with default properties.    */
DECL|method|FieldType
specifier|public
name|FieldType
parameter_list|()
block|{   }
comment|/**    * Throws an exception if this FieldType is frozen. Subclasses should    * call this within setters for additional state.    */
DECL|method|checkIfFrozen
specifier|protected
name|void
name|checkIfFrozen
parameter_list|()
block|{
if|if
condition|(
name|frozen
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this FieldType is already frozen and cannot be changed"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Prevents future changes. Note, it is recommended that this is called once    * the FieldTypes's properties have been set, to prevent unintentional state    * changes.    */
DECL|method|freeze
specifier|public
name|void
name|freeze
parameter_list|()
block|{
name|this
operator|.
name|frozen
operator|=
literal|true
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>false</code>.    * @see #setStored(boolean)    */
annotation|@
name|Override
DECL|method|stored
specifier|public
name|boolean
name|stored
parameter_list|()
block|{
return|return
name|this
operator|.
name|stored
return|;
block|}
comment|/**    * Set to<code>true</code> to store this field.    * @param value true if this field should be stored.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #stored()    */
DECL|method|setStored
specifier|public
name|void
name|setStored
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|stored
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>true</code>.    * @see #setTokenized(boolean)    */
DECL|method|tokenized
specifier|public
name|boolean
name|tokenized
parameter_list|()
block|{
return|return
name|this
operator|.
name|tokenized
return|;
block|}
comment|/**    * Set to<code>true</code> to tokenize this field's contents via the     * configured {@link Analyzer}.    * @param value true if this field should be tokenized.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #tokenized()    */
DECL|method|setTokenized
specifier|public
name|void
name|setTokenized
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|tokenized
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>false</code>.     * @see #setStoreTermVectors(boolean)    */
annotation|@
name|Override
DECL|method|storeTermVectors
specifier|public
name|boolean
name|storeTermVectors
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectors
return|;
block|}
comment|/**    * Set to<code>true</code> if this field's indexed form should be also stored     * into term vectors.    * @param value true if this field should store term vectors.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #storeTermVectors()    */
DECL|method|setStoreTermVectors
specifier|public
name|void
name|setStoreTermVectors
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectors
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>false</code>.    * @see #setStoreTermVectorOffsets(boolean)    */
annotation|@
name|Override
DECL|method|storeTermVectorOffsets
specifier|public
name|boolean
name|storeTermVectorOffsets
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectorOffsets
return|;
block|}
comment|/**    * Set to<code>true</code> to also store token character offsets into the term    * vector for this field.    * @param value true if this field should store term vector offsets.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #storeTermVectorOffsets()    */
DECL|method|setStoreTermVectorOffsets
specifier|public
name|void
name|setStoreTermVectorOffsets
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorOffsets
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>false</code>.    * @see #setStoreTermVectorPositions(boolean)    */
annotation|@
name|Override
DECL|method|storeTermVectorPositions
specifier|public
name|boolean
name|storeTermVectorPositions
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectorPositions
return|;
block|}
comment|/**    * Set to<code>true</code> to also store token positions into the term    * vector for this field.    * @param value true if this field should store term vector positions.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #storeTermVectorPositions()    */
DECL|method|setStoreTermVectorPositions
specifier|public
name|void
name|setStoreTermVectorPositions
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorPositions
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>false</code>.    * @see #setStoreTermVectorPayloads(boolean)     */
annotation|@
name|Override
DECL|method|storeTermVectorPayloads
specifier|public
name|boolean
name|storeTermVectorPayloads
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeTermVectorPayloads
return|;
block|}
comment|/**    * Set to<code>true</code> to also store token payloads into the term    * vector for this field.    * @param value true if this field should store term vector payloads.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #storeTermVectorPayloads()    */
DECL|method|setStoreTermVectorPayloads
specifier|public
name|void
name|setStoreTermVectorPayloads
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeTermVectorPayloads
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>false</code>.    * @see #setOmitNorms(boolean)    */
annotation|@
name|Override
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
name|this
operator|.
name|omitNorms
return|;
block|}
comment|/**    * Set to<code>true</code> to omit normalization values for the field.    * @param value true if this field should omit norms.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #omitNorms()    */
DECL|method|setOmitNorms
specifier|public
name|void
name|setOmitNorms
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *<p>    * The default is {@link IndexOptions#DOCS_AND_FREQS_AND_POSITIONS}.    * @see #setIndexOptions(IndexOptions)    */
annotation|@
name|Override
DECL|method|indexOptions
specifier|public
name|IndexOptions
name|indexOptions
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexOptions
return|;
block|}
comment|/**    * Sets the indexing options for the field:    * @param value indexing options    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #indexOptions()    */
DECL|method|setIndexOptions
specifier|public
name|void
name|setIndexOptions
parameter_list|(
name|IndexOptions
name|value
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"IndexOptions cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|indexOptions
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Specifies the field's numeric type.    * @param type numeric type, or null if the field has no numeric type.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #numericType()    */
DECL|method|setNumericType
specifier|public
name|void
name|setNumericType
parameter_list|(
name|NumericType
name|type
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
name|numericType
operator|=
name|type
expr_stmt|;
block|}
comment|/**     * NumericType: if non-null then the field's value will be indexed    * numerically so that {@link NumericRangeQuery} can be used at     * search time.     *<p>    * The default is<code>null</code> (no numeric type)     * @see #setNumericType(NumericType)    */
DECL|method|numericType
specifier|public
name|NumericType
name|numericType
parameter_list|()
block|{
return|return
name|numericType
return|;
block|}
comment|/**    * Sets the numeric precision step for the field.    * @param precisionStep numeric precision step for the field    * @throws IllegalArgumentException if precisionStep is less than 1.     * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #numericPrecisionStep()    */
DECL|method|setNumericPrecisionStep
specifier|public
name|void
name|setNumericPrecisionStep
parameter_list|(
name|int
name|precisionStep
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|precisionStep
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"precisionStep must be>= 1 (got "
operator|+
name|precisionStep
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|numericPrecisionStep
operator|=
name|precisionStep
expr_stmt|;
block|}
comment|/**     * Precision step for numeric field.     *<p>    * This has no effect if {@link #numericType()} returns null.    *<p>    * The default is {@link NumericUtils#PRECISION_STEP_DEFAULT}    * @see #setNumericPrecisionStep(int)    */
DECL|method|numericPrecisionStep
specifier|public
name|int
name|numericPrecisionStep
parameter_list|()
block|{
return|return
name|numericPrecisionStep
return|;
block|}
comment|/**    * Enables dimensional indexing.    */
DECL|method|setDimensions
specifier|public
name|void
name|setDimensions
parameter_list|(
name|int
name|dimensionCount
parameter_list|,
name|int
name|dimensionNumBytes
parameter_list|)
block|{
if|if
condition|(
name|dimensionCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dimensionCount must be>= 0; got "
operator|+
name|dimensionCount
argument_list|)
throw|;
block|}
if|if
condition|(
name|dimensionNumBytes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"dimensionNumBytes must be>= 0; got "
operator|+
name|dimensionNumBytes
argument_list|)
throw|;
block|}
if|if
condition|(
name|dimensionCount
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|dimensionNumBytes
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"when dimensionCount is 0 dimensionNumBytes must 0; got "
operator|+
name|dimensionNumBytes
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|dimensionNumBytes
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|dimensionCount
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"when dimensionNumBytes is 0 dimensionCount must 0; got "
operator|+
name|dimensionCount
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|dimensionCount
operator|=
name|dimensionCount
expr_stmt|;
name|this
operator|.
name|dimensionNumBytes
operator|=
name|dimensionNumBytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|dimensionCount
specifier|public
name|int
name|dimensionCount
parameter_list|()
block|{
return|return
name|dimensionCount
return|;
block|}
annotation|@
name|Override
DECL|method|dimensionNumBytes
specifier|public
name|int
name|dimensionNumBytes
parameter_list|()
block|{
return|return
name|dimensionNumBytes
return|;
block|}
comment|/** Prints a Field for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|stored
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"stored"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"indexed"
argument_list|)
expr_stmt|;
if|if
condition|(
name|tokenized
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",tokenized"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectors
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",termVector"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectorOffsets
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",termVectorOffsets"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectorPositions
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",termVectorPosition"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeTermVectorPayloads
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",termVectorPayloads"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omitNorms
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",omitNorms"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",indexOptions="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|indexOptions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numericType
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",numericType="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|numericType
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|",numericPrecisionStep="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|numericPrecisionStep
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dimensionCount
operator|!=
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|",dimensionCount="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|dimensionCount
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|",dimensionNumBytes="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|dimensionNumBytes
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|docValuesType
operator|!=
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"docValuesType="
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|docValuesType
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* from StorableFieldType */
comment|/**    * {@inheritDoc}    *<p>    * The default is<code>null</code> (no docValues)     * @see #setDocValuesType(DocValuesType)    */
annotation|@
name|Override
DECL|method|docValuesType
specifier|public
name|DocValuesType
name|docValuesType
parameter_list|()
block|{
return|return
name|docValuesType
return|;
block|}
comment|/**    * Sets the field's DocValuesType    * @param type DocValues type, or null if no DocValues should be stored.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #docValuesType()    */
DECL|method|setDocValuesType
specifier|public
name|void
name|setDocValuesType
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
name|checkIfFrozen
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"DocValuesType cannot be null"
argument_list|)
throw|;
block|}
name|docValuesType
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|docValuesType
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|docValuesType
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|indexOptions
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|numericPrecisionStep
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|numericType
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|numericType
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|omitNorms
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|storeTermVectorOffsets
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|storeTermVectorPayloads
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|storeTermVectorPositions
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|storeTermVectors
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|stored
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|tokenized
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FieldType
name|other
init|=
operator|(
name|FieldType
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|docValuesType
operator|!=
name|other
operator|.
name|docValuesType
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|indexOptions
operator|!=
name|other
operator|.
name|indexOptions
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|numericPrecisionStep
operator|!=
name|other
operator|.
name|numericPrecisionStep
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|numericType
operator|!=
name|other
operator|.
name|numericType
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|omitNorms
operator|!=
name|other
operator|.
name|omitNorms
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|storeTermVectorOffsets
operator|!=
name|other
operator|.
name|storeTermVectorOffsets
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|storeTermVectorPayloads
operator|!=
name|other
operator|.
name|storeTermVectorPayloads
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|storeTermVectorPositions
operator|!=
name|other
operator|.
name|storeTermVectorPositions
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|storeTermVectors
operator|!=
name|other
operator|.
name|storeTermVectors
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|stored
operator|!=
name|other
operator|.
name|stored
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|tokenized
operator|!=
name|other
operator|.
name|tokenized
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

