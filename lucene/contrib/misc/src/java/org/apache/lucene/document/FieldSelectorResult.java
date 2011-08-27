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
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  Provides information about what should be done with this Field   *  **/
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
name|IndexableField
import|;
end_import

begin_comment
comment|// for javadocs
end_comment

begin_enum
DECL|enum|FieldSelectorResult
specifier|public
enum|enum
name|FieldSelectorResult
block|{
comment|/**      * Load this {@link Field} every time the {@link Document} is loaded, reading in the data as it is encountered.      *  {@link Document#getField(String)} should not return null.      *<p/>      * {@link Document#add(IndexableField)} should be called by the Reader.      */
DECL|enum constant|LOAD
name|LOAD
block|,
comment|/**      * Lazily load this {@link Field}.  This means the {@link Field} is valid, but it may not actually contain its data until      * invoked.  {@link Document#getField(String)} is safe to use and should      * return a valid instance of a {@link IndexableField}.      *<p/>      * {@link Document#add(IndexableField)} should be called by the Reader.      */
DECL|enum constant|LAZY_LOAD
name|LAZY_LOAD
block|,
comment|/**      * Do not load the {@link Field}.  {@link Document#getField(String)} should return null.      * {@link Document#add(IndexableField)} is not called.      *<p/>      * {@link Document#add(IndexableField)} should not be called by the Reader.      */
DECL|enum constant|NO_LOAD
name|NO_LOAD
block|,
comment|/**      * Load this field as in the {@link #LOAD} case, but immediately return from {@link Field} loading for the {@link Document}.  Thus, the      * Document may not have its complete set of Fields.  {@link Document#getField(String)} should      * both be valid for this {@link Field}      *<p/>      * {@link Document#add(IndexableField)} should be called by the Reader.      */
DECL|enum constant|LOAD_AND_BREAK
name|LOAD_AND_BREAK
block|,
comment|/** Expert:  Load the size of this {@link Field} rather than its value.      * Size is measured as number of bytes required to store the field == bytes for a binary or any compressed value, and 2*chars for a String value.      * The size is stored as a binary value, represented as an int in a byte[], with the higher order byte first in [0]      */
DECL|enum constant|SIZE
name|SIZE
block|,
comment|/** Expert: Like {@link #SIZE} but immediately break from the field loading loop, i.e., stop loading further fields, after the size is loaded */
DECL|enum constant|SIZE_AND_BREAK
name|SIZE_AND_BREAK
block|,
comment|/**      * Lazily load this {@link Field}, but do not cache the result.  This means the {@link Field} is valid, but it may not actually contain its data until      * invoked.  {@link Document#getField(String)} is safe to use and should      * return a valid instance of a {@link IndexableField}.      *<p/>      * {@link Document#add(IndexableField)} should be called by the Reader.      */
DECL|enum constant|LATENT
name|LATENT
block|}
end_enum

end_unit

