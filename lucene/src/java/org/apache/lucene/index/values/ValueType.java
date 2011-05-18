begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.values
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|values
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|FieldsConsumer
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
name|values
operator|.
name|DocValues
operator|.
name|SortedSource
import|;
end_import

begin_comment
comment|/**  * {@link ValueType} specifies the type of the {@link DocValues} for a certain field.  * A {@link ValueType} only defines the data type for a field while the actual  * implemenation used to encode and decode the values depends on the field's  * {@link Codec}. It is up to the {@link Codec} implementing  * {@link FieldsConsumer#addValuesField(org.apache.lucene.index.FieldInfo)} and  * using a different low-level implemenations to write the stored values for a  * field.  *   * @lucene.experimental  */
end_comment

begin_enum
DECL|enum|ValueType
specifier|public
enum|enum
name|ValueType
block|{
comment|/*    * TODO: Add INT_32 INT_64 INT_16& INT_8?!    */
comment|/**    * Integer values.    */
DECL|enum constant|INTS
name|INTS
block|,
comment|/**    * 32 bit floating point values.    */
DECL|enum constant|FLOAT_32
name|FLOAT_32
block|,
comment|/**    * 64 bit floating point values.    */
DECL|enum constant|FLOAT_64
name|FLOAT_64
block|,
comment|// TODO(simonw): -- shouldn't lucene decide/detect straight vs
comment|// deref, as well fixed vs var?
comment|/**    * Fixed length straight stored byte variant    */
DECL|enum constant|BYTES_FIXED_STRAIGHT
name|BYTES_FIXED_STRAIGHT
block|,
comment|/**    * Fixed length dereferenced (indexed) byte variant    */
DECL|enum constant|BYTES_FIXED_DEREF
name|BYTES_FIXED_DEREF
block|,
comment|/**    * Fixed length pre-sorted byte variant    *     * @see SortedSource    */
DECL|enum constant|BYTES_FIXED_SORTED
name|BYTES_FIXED_SORTED
block|,
comment|/**    * Variable length straight stored byte variant    */
DECL|enum constant|BYTES_VAR_STRAIGHT
name|BYTES_VAR_STRAIGHT
block|,
comment|/**    * Variable length dereferenced (indexed) byte variant    */
DECL|enum constant|BYTES_VAR_DEREF
name|BYTES_VAR_DEREF
block|,
comment|/**    * Variable length pre-sorted byte variant    *     * @see SortedSource    */
DECL|enum constant|BYTES_VAR_SORTED
name|BYTES_VAR_SORTED
block|}
end_enum

end_unit

