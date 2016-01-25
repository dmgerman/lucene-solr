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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Analyzer
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
name|search
operator|.
name|similarities
operator|.
name|ClassicSimilarity
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
name|similarities
operator|.
name|Similarity
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
comment|/** Represents a single field for indexing.  IndexWriter  *  consumes Iterable&lt;IndexableField&gt; as a document.  *  *  @lucene.experimental */
end_comment

begin_interface
DECL|interface|IndexableField
specifier|public
interface|interface
name|IndexableField
block|{
comment|/** Field name */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
function_decl|;
comment|/** {@link IndexableFieldType} describing the properties    * of this field. */
DECL|method|fieldType
specifier|public
name|IndexableFieldType
name|fieldType
parameter_list|()
function_decl|;
comment|/**    * Creates the TokenStream used for indexing this field.  If appropriate,    * implementations should use the given Analyzer to create the TokenStreams.    *    * @param analyzer Analyzer that should be used to create the TokenStreams from    * @param reuse TokenStream for a previous instance of this field<b>name</b>. This allows    *              custom field types (like StringField and NumericField) that do not use    *              the analyzer to still have good performance. Note: the passed-in type    *              may be inappropriate, for example if you mix up different types of Fields    *              for the same field name. So it's the responsibility of the implementation to    *              check.    * @return TokenStream value for indexing the document.  Should always return    *         a non-null value if the field is to be indexed    */
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
function_decl|;
comment|/**     * Returns the field's index-time boost.    *<p>    * Only fields can have an index-time boost, if you want to simulate    * a "document boost", then you must pre-multiply it across all the    * relevant fields yourself.     *<p>The boost is used to compute the norm factor for the field.  By    * default, in the {@link Similarity#computeNorm(FieldInvertState)} method,     * the boost value is multiplied by the length normalization factor and then    * rounded by {@link ClassicSimilarity#encodeNormValue(float)} before it is stored in the    * index.  One should attempt to ensure that this product does not overflow    * the range of that encoding.    *<p>    * It is illegal to return a boost other than 1.0f for a field that is not    * indexed ({@link IndexableFieldType#indexOptions()} is IndexOptions.NONE) or    * omits normalization values ({@link IndexableFieldType#omitNorms()} returns true).    *    * @see Similarity#computeNorm(FieldInvertState)    * @see ClassicSimilarity#encodeNormValue(float)    */
DECL|method|boost
specifier|public
name|float
name|boost
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a binary value */
DECL|method|binaryValue
specifier|public
name|BytesRef
name|binaryValue
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a string value */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a Reader value */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
function_decl|;
comment|/** Non-null if this field has a numeric value */
DECL|method|numericValue
specifier|public
name|Number
name|numericValue
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

