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

begin_comment
comment|/**   * Describes the properties of a field.  * @lucene.experimental   */
end_comment

begin_interface
DECL|interface|IndexableFieldType
specifier|public
interface|interface
name|IndexableFieldType
block|{
comment|/** True if the field's value should be stored */
DECL|method|stored
specifier|public
name|boolean
name|stored
parameter_list|()
function_decl|;
comment|/**     * True if this field's value should be analyzed by the    * {@link Analyzer}.    *<p>    * This has no effect if {@link #indexOptions()} returns    * IndexOptions.NONE.    */
comment|// TODO: shouldn't we remove this?  Whether/how a field is
comment|// tokenized is an impl detail under Field?
DECL|method|tokenized
specifier|public
name|boolean
name|tokenized
parameter_list|()
function_decl|;
comment|/**     * True if this field's indexed form should be also stored     * into term vectors.    *<p>    * This builds a miniature inverted-index for this field which    * can be accessed in a document-oriented way from     * {@link IndexReader#getTermVector(int,String)}.    *<p>    * This option is illegal if {@link #indexOptions()} returns    * IndexOptions.NONE.    */
DECL|method|storeTermVectors
specifier|public
name|boolean
name|storeTermVectors
parameter_list|()
function_decl|;
comment|/**     * True if this field's token character offsets should also    * be stored into term vectors.    *<p>    * This option is illegal if term vectors are not enabled for the field    * ({@link #storeTermVectors()} is false)    */
DECL|method|storeTermVectorOffsets
specifier|public
name|boolean
name|storeTermVectorOffsets
parameter_list|()
function_decl|;
comment|/**     * True if this field's token positions should also be stored    * into the term vectors.    *<p>    * This option is illegal if term vectors are not enabled for the field    * ({@link #storeTermVectors()} is false).     */
DECL|method|storeTermVectorPositions
specifier|public
name|boolean
name|storeTermVectorPositions
parameter_list|()
function_decl|;
comment|/**     * True if this field's token payloads should also be stored    * into the term vectors.    *<p>    * This option is illegal if term vector positions are not enabled     * for the field ({@link #storeTermVectors()} is false).    */
DECL|method|storeTermVectorPayloads
specifier|public
name|boolean
name|storeTermVectorPayloads
parameter_list|()
function_decl|;
comment|/**    * True if normalization values should be omitted for the field.    *<p>    * This saves memory, but at the expense of scoring quality (length normalization    * will be disabled), and if you omit norms, you cannot use index-time boosts.     */
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
function_decl|;
comment|/** {@link IndexOptions}, describing what should be    *  recorded into the inverted index */
DECL|method|indexOptions
specifier|public
name|IndexOptions
name|indexOptions
parameter_list|()
function_decl|;
comment|/**     * DocValues {@link DocValuesType}: how the field's value will be indexed    * into docValues.    */
DECL|method|docValuesType
specifier|public
name|DocValuesType
name|docValuesType
parameter_list|()
function_decl|;
comment|/**    * If this is positive, the field is indexed as a point.    */
DECL|method|pointDimensionCount
specifier|public
name|int
name|pointDimensionCount
parameter_list|()
function_decl|;
comment|/**    * The number of bytes in each dimension's values.    */
DECL|method|pointNumBytes
specifier|public
name|int
name|pointNumBytes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

