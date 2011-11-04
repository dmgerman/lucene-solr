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
name|DocValuesFormat
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
name|index
operator|.
name|values
operator|.
name|IndexDocValues
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
comment|/**  *<code>ValueType</code> specifies the {@link IndexDocValues} type for a  * certain field. A<code>ValueType</code> only defines the data type for a field  * while the actual implementation used to encode and decode the values depends  * on the the {@link DocValuesFormat#docsConsumer} and {@link DocValuesFormat#docsProducer} methods.  *   * @lucene.experimental  */
end_comment

begin_enum
DECL|enum|ValueType
specifier|public
enum|enum
name|ValueType
block|{
comment|/**    * A variable bit signed integer value. By default this type uses    * {@link PackedInts} to compress the values, as an offset    * from the minimum value, as long as the value range    * fits into 2<sup>63</sup>-1. Otherwise,    * the default implementation falls back to fixed size 64bit    * integers ({@link #FIXED_INTS_64}).    *<p>    * NOTE: this type uses<tt>0</tt> as the default value without any    * distinction between provided<tt>0</tt> values during indexing. All    * documents without an explicit value will use<tt>0</tt> instead.    * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|VAR_INTS
name|VAR_INTS
block|,
comment|/**    * A 8 bit signed integer value. {@link Source} instances of    * this type return a<tt>byte</tt> array from {@link Source#getArray()}    *<p>    * NOTE: this type uses<tt>0</tt> as the default value without any    * distinction between provided<tt>0</tt> values during indexing. All    * documents without an explicit value will use<tt>0</tt> instead.    * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FIXED_INTS_8
name|FIXED_INTS_8
block|,
comment|/**    * A 16 bit signed integer value. {@link Source} instances of    * this type return a<tt>short</tt> array from {@link Source#getArray()}    *<p>    * NOTE: this type uses<tt>0</tt> as the default value without any    * distinction between provided<tt>0</tt> values during indexing. All    * documents without an explicit value will use<tt>0</tt> instead.    * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FIXED_INTS_16
name|FIXED_INTS_16
block|,
comment|/**    * A 32 bit signed integer value. {@link Source} instances of    * this type return a<tt>int</tt> array from {@link Source#getArray()}    *<p>    * NOTE: this type uses<tt>0</tt> as the default value without any    * distinction between provided<tt>0</tt> values during indexing. All    * documents without an explicit value will use<tt>0</tt> instead.     * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FIXED_INTS_32
name|FIXED_INTS_32
block|,
comment|/**    * A 64 bit signed integer value. {@link Source} instances of    * this type return a<tt>long</tt> array from {@link Source#getArray()}    *<p>    * NOTE: this type uses<tt>0</tt> as the default value without any    * distinction between provided<tt>0</tt> values during indexing. All    * documents without an explicit value will use<tt>0</tt> instead.    * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FIXED_INTS_64
name|FIXED_INTS_64
block|,
comment|/**    * A 32 bit floating point value. By default there is no compression    * applied. To fit custom float values into less than 32bit either a custom    * implementation is needed or values must be encoded into a    * {@link #BYTES_FIXED_STRAIGHT} type. {@link Source} instances of    * this type return a<tt>float</tt> array from {@link Source#getArray()}    *<p>    * NOTE: this type uses<tt>0.0f</tt> as the default value without any    * distinction between provided<tt>0.0f</tt> values during indexing. All    * documents without an explicit value will use<tt>0.0f</tt> instead.    * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FLOAT_32
name|FLOAT_32
block|,
comment|/**    *     * A 64 bit floating point value. By default there is no compression    * applied. To fit custom float values into less than 64bit either a custom    * implementation is needed or values must be encoded into a    * {@link #BYTES_FIXED_STRAIGHT} type. {@link Source} instances of    * this type return a<tt>double</tt> array from {@link Source#getArray()}    *<p>    * NOTE: this type uses<tt>0.0d</tt> as the default value without any    * distinction between provided<tt>0.0d</tt> values during indexing. All    * documents without an explicit value will use<tt>0.0d</tt> instead.    * Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|FLOAT_64
name|FLOAT_64
block|,
comment|// TODO(simonw): -- shouldn't lucene decide/detect straight vs
comment|// deref, as well fixed vs var?
comment|/**    * A fixed length straight byte[]. All values added to    * such a field must be of the same length. All bytes are stored sequentially    * for fast offset access.    *<p>    * NOTE: this type uses<tt>0 byte</tt> filled byte[] based on the length of the first seen    * value as the default value without any distinction between explicitly    * provided values during indexing. All documents without an explicit value    * will use the default instead.Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|BYTES_FIXED_STRAIGHT
name|BYTES_FIXED_STRAIGHT
block|,
comment|/**    * A fixed length dereferenced byte[] variant. Fields with    * this type only store distinct byte values and store an additional offset    * pointer per document to dereference the shared byte[].    * Use this type if your documents may share the same byte[].    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|BYTES_FIXED_DEREF
name|BYTES_FIXED_DEREF
block|,
comment|/**    * Variable length straight stored byte[] variant. All bytes are    * stored sequentially for compactness. Usage of this type via the    * disk-resident API might yield performance degradation since no additional    * index is used to advance by more than one document value at a time.    *<p>    * NOTE: Fields of this type will not store values for documents without an    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * byte[] reference. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|BYTES_VAR_STRAIGHT
name|BYTES_VAR_STRAIGHT
block|,
comment|/**    * A variable length dereferenced byte[]. Just like    * {@link #BYTES_FIXED_DEREF}, but allowing each    * document's value to be a different length.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. Custom default values must be assigned explicitly.    *</p>    */
DECL|enum constant|BYTES_VAR_DEREF
name|BYTES_VAR_DEREF
block|,
comment|/**    * A variable length pre-sorted byte[] variant. Just like    * {@link #BYTES_FIXED_SORTED}, but allowing each    * document's value to be a different length.    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference.Custom default values must be assigned explicitly.    *</p>    *     * @see SortedSource    */
DECL|enum constant|BYTES_VAR_SORTED
name|BYTES_VAR_SORTED
block|,
comment|/**    * A fixed length pre-sorted byte[] variant. Fields with this type only    * store distinct byte values and store an additional offset pointer per    * document to dereference the shared byte[]. The stored    * byte[] is presorted, by default by unsigned byte order,    * and allows access via document id, ordinal and by-value.    * Use this type if your documents may share the same byte[].    *<p>    * NOTE: Fields of this type will not store values for documents without and    * explicitly provided value. If a documents value is accessed while no    * explicit value is stored the returned {@link BytesRef} will be a 0-length    * reference. Custom default values must be assigned    * explicitly.    *</p>    *     * @see SortedSource    */
DECL|enum constant|BYTES_FIXED_SORTED
name|BYTES_FIXED_SORTED
block|}
end_enum

end_unit

