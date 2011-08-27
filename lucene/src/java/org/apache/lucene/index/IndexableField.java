begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|analysis
operator|.
name|TokenStream
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
name|document
operator|.
name|NumericField
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
name|FieldInfo
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
name|values
operator|.
name|PerDocFieldValues
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
name|ValueType
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
comment|// TODO: how to handle versioning here...?
end_comment

begin_comment
comment|// TODO: we need to break out separate StoredField...
end_comment

begin_comment
comment|/** Represents a single field for indexing.  IndexWriter  *  consumes Iterable<IndexableField> as a document.  *  *  @lucene.experimental */
end_comment

begin_interface
DECL|interface|IndexableField
specifier|public
interface|interface
name|IndexableField
block|{
comment|// TODO: add attrs to this API?
comment|/* Field name */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
function_decl|;
comment|// NOTE: if doc/field impl has the notion of "doc level boost"
comment|// it must be multiplied in w/ this field's boost
comment|/** Field boost (you must pre-multiply in any doc boost). */
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
function_decl|;
comment|/* True if the field's value should be stored */
DECL|method|stored
specifier|public
name|boolean
name|stored
parameter_list|()
function_decl|;
comment|/* Non-null if this field has a binary value */
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
function_decl|;
comment|/* Non-null if this field has a string value */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
function_decl|;
comment|/* Non-null if this field has a Reader value */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
function_decl|;
comment|/* Non-null if this field has a pre-tokenized ({@link TokenStream}) value */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
function_decl|;
comment|// Numeric field:
comment|/* True if this field is numeric */
DECL|method|numeric
specifier|public
name|boolean
name|numeric
parameter_list|()
function_decl|;
comment|/* Numeric {@link NumericField.DataType}; only used if    * the field is numeric */
DECL|method|numericDataType
specifier|public
name|NumericField
operator|.
name|DataType
name|numericDataType
parameter_list|()
function_decl|;
comment|/* Numeric value; only used if the field is numeric */
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
function_decl|;
comment|/* True if this field should be indexed (inverted) */
DECL|method|indexed
specifier|public
name|boolean
name|indexed
parameter_list|()
function_decl|;
comment|/* True if this field's value should be analyzed */
DECL|method|tokenized
specifier|public
name|boolean
name|tokenized
parameter_list|()
function_decl|;
comment|/* True if norms should not be indexed */
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
function_decl|;
comment|/* {@link IndexOptions}, describing what should be    * recorded into the inverted index */
DECL|method|indexOptions
specifier|public
name|IndexOptions
name|indexOptions
parameter_list|()
function_decl|;
comment|/* True if term vectors should be indexed */
DECL|method|storeTermVectors
specifier|public
name|boolean
name|storeTermVectors
parameter_list|()
function_decl|;
comment|/* True if term vector offsets should be indexed */
DECL|method|storeTermVectorOffsets
specifier|public
name|boolean
name|storeTermVectorOffsets
parameter_list|()
function_decl|;
comment|/* True if term vector positions should be indexed */
DECL|method|storeTermVectorPositions
specifier|public
name|boolean
name|storeTermVectorPositions
parameter_list|()
function_decl|;
comment|/* Non-null if doc values should be indexed */
DECL|method|docValues
specifier|public
name|PerDocFieldValues
name|docValues
parameter_list|()
function_decl|;
comment|/* DocValues type; only used if docValues is non-null */
DECL|method|docValuesType
specifier|public
name|ValueType
name|docValuesType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

