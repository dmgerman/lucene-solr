begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.legacy
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|legacy
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
name|FieldType
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

begin_comment
comment|/**  * FieldType extension with support for legacy numerics  * @deprecated Please switch to {@link org.apache.lucene.index.PointValues} instead  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyFieldType
specifier|public
specifier|final
class|class
name|LegacyFieldType
extends|extends
name|FieldType
block|{
DECL|field|numericType
specifier|private
name|LegacyNumericType
name|numericType
decl_stmt|;
DECL|field|numericPrecisionStep
specifier|private
name|int
name|numericPrecisionStep
init|=
name|LegacyNumericUtils
operator|.
name|PRECISION_STEP_DEFAULT
decl_stmt|;
comment|/**    * Create a new mutable LegacyFieldType with all of the properties from<code>ref</code>    */
DECL|method|LegacyFieldType
specifier|public
name|LegacyFieldType
parameter_list|(
name|LegacyFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|this
operator|.
name|numericType
operator|=
name|ref
operator|.
name|numericType
expr_stmt|;
name|this
operator|.
name|numericPrecisionStep
operator|=
name|ref
operator|.
name|numericPrecisionStep
expr_stmt|;
block|}
comment|/**    * Create a new FieldType with default properties.    */
DECL|method|LegacyFieldType
specifier|public
name|LegacyFieldType
parameter_list|()
block|{   }
comment|/**    * Specifies the field's numeric type.    * @param type numeric type, or null if the field has no numeric type.    * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #numericType()    *    * @deprecated Please switch to {@link org.apache.lucene.index.PointValues} instead    */
annotation|@
name|Deprecated
DECL|method|setNumericType
specifier|public
name|void
name|setNumericType
parameter_list|(
name|LegacyNumericType
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
comment|/**     * LegacyNumericType: if non-null then the field's value will be indexed    * numerically so that {@link org.apache.solr.legacy.LegacyNumericRangeQuery} can be used at    * search time.     *<p>    * The default is<code>null</code> (no numeric type)     * @see #setNumericType(LegacyNumericType)    *    * @deprecated Please switch to {@link org.apache.lucene.index.PointValues} instead    */
annotation|@
name|Deprecated
DECL|method|numericType
specifier|public
name|LegacyNumericType
name|numericType
parameter_list|()
block|{
return|return
name|numericType
return|;
block|}
comment|/**    * Sets the numeric precision step for the field.    * @param precisionStep numeric precision step for the field    * @throws IllegalArgumentException if precisionStep is less than 1.     * @throws IllegalStateException if this FieldType is frozen against    *         future modifications.    * @see #numericPrecisionStep()    *    * @deprecated Please switch to {@link org.apache.lucene.index.PointValues} instead    */
annotation|@
name|Deprecated
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
comment|/**     * Precision step for numeric field.     *<p>    * This has no effect if {@link #numericType()} returns null.    *<p>    * The default is {@link org.apache.solr.legacy.LegacyNumericUtils#PRECISION_STEP_DEFAULT}    * @see #setNumericPrecisionStep(int)    *    * @deprecated Please switch to {@link org.apache.lucene.index.PointValues} instead    */
annotation|@
name|Deprecated
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
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
operator|!
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
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
name|LegacyFieldType
name|other
init|=
operator|(
name|LegacyFieldType
operator|)
name|obj
decl_stmt|;
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
return|return
literal|true
return|;
block|}
comment|/** Prints a Field for human consumption. */
annotation|@
name|Override
DECL|method|toString
specifier|public
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
name|result
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexOptions
argument_list|()
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
block|{
name|result
operator|.
name|append
argument_list|(
literal|","
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
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

