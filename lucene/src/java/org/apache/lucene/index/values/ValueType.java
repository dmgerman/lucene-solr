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
name|PerDocConsumer
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
name|IndexDocValues
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
name|packed
operator|.
name|PackedInts
import|;
end_import

begin_comment
comment|/**  * {@link ValueType} specifies the type of the {@link IndexDocValues} for a  * certain field. A {@link ValueType} only defines the data type for a field  * while the actual Implementation used to encode and decode the values depends  * on the field's {@link Codec}. It is up to the {@link Codec} implementing  * {@link PerDocConsumer#addValuesField(org.apache.lucene.index.FieldInfo)} and  * using a different low-level implementations to write the stored values for a  * field.  *   * @lucene.experimental  */
end_comment

begin_enum
DECL|enum|ValueType
specifier|public
enum|enum
name|ValueType
block|{
comment|/*    * TODO: Add INT_32 INT_64 INT_16& INT_8?!    */
comment|/**    * Defines an 64 bit integer value. By default this type uses a simple    * compression technique based on {@link PackedInts}. Internally only the used    * value range is encoded if it fits into 2<sup>63</sup>-1. If that range is    * exceeded the default implementation falls back to fixed size 64bit    * integers.    *<p>    * NOTE: this type uses<tt>0</tt> as the default value without any    * distinction between provided<tt>0</tt> values during indexing. All    * documents without an explicit value will use<tt>0</tt> instead. In turn,    * {@link ValuesEnum} instances will not skip documents without an explicit    * value assigned. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|INTS
name|INTS
block|,
comment|/**    * Defines a 32 bit floating point values. By default there is no compression    * applied. To fit custom float values into less than 32bit either a custom    * implementation is needed or values must be encoded into a    * {@link #BYTES_FIXED_STRAIGHT} type.    *<p>    * NOTE: this type uses<tt>0.0f</tt> as the default value without any    * distinction between provided<tt>0.0f</tt> values during indexing. All    * documents without an explicit value will use<tt>0.0f</tt> instead. In    * turn, {@link ValuesEnum} instances will not skip documents without an    * explicit value assigned. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FLOAT_32
name|FLOAT_32
block|,
comment|/**    * Defines a 64 bit floating point values. By default there is no compression    * applied. To fit custom float values into less than 64bit either a custom    * implementation is needed or values must be encoded into a    * {@link #BYTES_FIXED_STRAIGHT} type.    *<p>    * NOTE: this type uses<tt>0.0d</tt> as the default value without any    * distinction between provided<tt>0.0d</tt> values during indexing. All    * documents without an explicit value will use<tt>0.0d</tt> instead. In    * turn, {@link ValuesEnum} instances will not skip documents without an    * explicit value assigned. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FLOAT_64
name|FLOAT_64
block|,
comment|// TODO(simonw): -- shouldn't lucene decide/detect straight vs
comment|// deref, as well fixed vs var?
comment|/**    * Defines a fixed length straight stored byte variant. All values added to    * such a field must be of the same length. All bytes are stored sequentially    * for fast offset access.    *<p>    * NOTE: this type uses<tt>0-bytes</tt> based on the length of the first seen    * values as the default value without any distinction between explicitly    * provided values during indexing. All documents without an explicit value    * will use the default instead. In turn, {@link ValuesEnum} instances will    * not skip documents without an explicit value assigned. Custom default    * values must be assigned explicitly.    *</p>    */
DECL|enum constant|BYTES_FIXED_STRAIGHT
name|BYTES_FIXED_STRAIGHT
block|,
comment|/**    * Defines a fixed length dereferenced (indexed) byte variant. Fields with    * this type only store distinct byte values and store an additional offset    * pointer per document to dereference the payload.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. In turn, {@link ValuesEnum} instances will skip over documents    * without an explicit value assigned. Custom default values must be assigned    * explicitly.    *</p>    */
DECL|enum constant|BYTES_FIXED_DEREF
name|BYTES_FIXED_DEREF
block|,
comment|/**    * Defines a fixed length pre-sorted byte variant. Fields with this type only    * store distinct byte values and store an additional offset pointer per    * document to dereference the payload. The stored byte payload is presorted    * and allows access via document id, ordinal and by-value.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. In turn, {@link ValuesEnum} instances will skip over documents    * without an explicit value assigned. Custom default values must be assigned    * explicitly.    *</p>    *     * @see SortedSource    */
DECL|enum constant|BYTES_FIXED_SORTED
name|BYTES_FIXED_SORTED
block|,
comment|/**    * Defines a variable length straight stored byte variant. All bytes are    * stored sequentially for compactness. Usage of this type via the    * disk-resident API might yield performance degradation since no additional    * index is used to advance by more than one documents value at a time.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. Yet, in contrast to dereferences variants {@link ValuesEnum}    * instances will<b>not</b> skip over documents without an explicit value    * assigned. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|BYTES_VAR_STRAIGHT
name|BYTES_VAR_STRAIGHT
block|,
comment|/**    * Defines a variable length dereferenced (indexed) byte variant. Just as    * {@link #BYTES_FIXED_DEREF} yet supporting variable length values.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. In turn, {@link ValuesEnum} instances will skip over documents    * without an explicit value assigned. Custom default values must be assigned    * explicitly.    *</p>    */
DECL|enum constant|BYTES_VAR_DEREF
name|BYTES_VAR_DEREF
block|,
comment|/**    * Defines a variable length pre-sorted byte variant. Just as    * {@link #BYTES_FIXED_SORTED} yet supporting variable length values.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. In turn, {@link ValuesEnum} instances will skip over documents    * without an explicit value assigned. Custom default values must be assigned    * explicitly.    *</p>    *     * @see SortedSource    */
DECL|enum constant|BYTES_VAR_SORTED
name|BYTES_VAR_SORTED
block|}
end_enum

end_unit

