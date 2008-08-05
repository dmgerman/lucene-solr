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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Synonymous with {@link Field}.  *  *<p><bold>WARNING</bold>: This interface may change within minor versions, despite Lucene's backward compatibility requirements.  * This means new methods may be added from version to version.  This change only affects the Fieldable API; other backwards  * compatibility promises remain intact. For example, Lucene can still  * read and write indices created within the same major version.  *</p>  *  **/
end_comment

begin_interface
DECL|interface|Fieldable
specifier|public
interface|interface
name|Fieldable
extends|extends
name|Serializable
block|{
comment|/** Sets the boost factor hits on this field.  This value will be    * multiplied into the score of all hits on this this field of this    * document.    *    *<p>The boost is multiplied by {@link org.apache.lucene.document.Document#getBoost()} of the document    * containing this field.  If a document has multiple fields with the same    * name, all such values are multiplied together.  This product is then    * multipled by the value {@link org.apache.lucene.search.Similarity#lengthNorm(String,int)}, and    * rounded by {@link org.apache.lucene.search.Similarity#encodeNorm(float)} before it is stored in the    * index.  One should attempt to ensure that this product does not overflow    * the range of that encoding.    *    * @see org.apache.lucene.document.Document#setBoost(float)    * @see org.apache.lucene.search.Similarity#lengthNorm(String, int)    * @see org.apache.lucene.search.Similarity#encodeNorm(float)    */
DECL|method|setBoost
name|void
name|setBoost
parameter_list|(
name|float
name|boost
parameter_list|)
function_decl|;
comment|/** Returns the boost factor for hits for this field.    *    *<p>The default value is 1.0.    *    *<p>Note: this value is not stored directly with the document in the index.    * Documents returned from {@link org.apache.lucene.index.IndexReader#document(int)} and    * {@link org.apache.lucene.search.Hits#doc(int)} may thus not have the same value present as when    * this field was indexed.    *    * @see #setBoost(float)    */
DECL|method|getBoost
name|float
name|getBoost
parameter_list|()
function_decl|;
comment|/** Returns the name of the field as an interned string.    * For example "date", "title", "body", ...    */
DECL|method|name
name|String
name|name
parameter_list|()
function_decl|;
comment|/** The value of the field as a String, or null.  If null, the Reader value,    * binary value, or TokenStream value is used.  Exactly one of stringValue(),     * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|stringValue
specifier|public
name|String
name|stringValue
parameter_list|()
function_decl|;
comment|/** The value of the field as a Reader, or null.  If null, the String value,    * binary value, or TokenStream value is used.  Exactly one of stringValue(),     * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|readerValue
specifier|public
name|Reader
name|readerValue
parameter_list|()
function_decl|;
comment|/** The value of the field in Binary, or null.  If null, the Reader value,    * String value, or TokenStream value is used. Exactly one of stringValue(),     * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|binaryValue
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
function_decl|;
comment|/** The value of the field as a TokenStream, or null.  If null, the Reader value,    * String value, or binary value is used. Exactly one of stringValue(),     * readerValue(), binaryValue(), and tokenStreamValue() must be set. */
DECL|method|tokenStreamValue
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
function_decl|;
comment|/** True iff the value of the field is to be stored in the index for return     with search hits.  It is an error for this to be true if a field is     Reader-valued. */
DECL|method|isStored
name|boolean
name|isStored
parameter_list|()
function_decl|;
comment|/** True iff the value of the field is to be indexed, so that it may be     searched on. */
DECL|method|isIndexed
name|boolean
name|isIndexed
parameter_list|()
function_decl|;
comment|/** True iff the value of the field should be tokenized as text prior to     indexing.  Un-tokenized fields are indexed as a single word and may not be     Reader-valued. */
DECL|method|isTokenized
name|boolean
name|isTokenized
parameter_list|()
function_decl|;
comment|/** True if the value of the field is stored and compressed within the index */
DECL|method|isCompressed
name|boolean
name|isCompressed
parameter_list|()
function_decl|;
comment|/** True iff the term or terms used to index this field are stored as a term    *  vector, available from {@link org.apache.lucene.index.IndexReader#getTermFreqVector(int,String)}.    *  These methods do not provide access to the original content of the field,    *  only to terms used to index it. If the original content must be    *  preserved, use the<code>stored</code> attribute instead.    *    * @see org.apache.lucene.index.IndexReader#getTermFreqVector(int, String)    */
DECL|method|isTermVectorStored
name|boolean
name|isTermVectorStored
parameter_list|()
function_decl|;
comment|/**    * True iff terms are stored as term vector together with their offsets     * (start and end positon in source text).    */
DECL|method|isStoreOffsetWithTermVector
name|boolean
name|isStoreOffsetWithTermVector
parameter_list|()
function_decl|;
comment|/**    * True iff terms are stored as term vector together with their token positions.    */
DECL|method|isStorePositionWithTermVector
name|boolean
name|isStorePositionWithTermVector
parameter_list|()
function_decl|;
comment|/** True iff the value of the filed is stored as binary */
DECL|method|isBinary
name|boolean
name|isBinary
parameter_list|()
function_decl|;
comment|/** True if norms are omitted for this indexed field */
DECL|method|getOmitNorms
name|boolean
name|getOmitNorms
parameter_list|()
function_decl|;
comment|/** Expert:    *    * If set, omit normalization factors associated with this indexed field.    * This effectively disables indexing boosts and length normalization for this field.    */
DECL|method|setOmitNorms
name|void
name|setOmitNorms
parameter_list|(
name|boolean
name|omitNorms
parameter_list|)
function_decl|;
comment|/** Expert:    *    * If set, omit term freq, positions and payloads from postings for this field.    */
DECL|method|setOmitTf
name|void
name|setOmitTf
parameter_list|(
name|boolean
name|omitTf
parameter_list|)
function_decl|;
comment|/** True if tf is omitted for this indexed field */
DECL|method|getOmitTf
name|boolean
name|getOmitTf
parameter_list|()
function_decl|;
comment|/**    * Indicates whether a Field is Lazy or not.  The semantics of Lazy loading are such that if a Field is lazily loaded, retrieving    * it's values via {@link #stringValue()} or {@link #binaryValue()} is only valid as long as the {@link org.apache.lucene.index.IndexReader} that    * retrieved the {@link Document} is still open.    *      * @return true if this field can be loaded lazily    */
DECL|method|isLazy
name|boolean
name|isLazy
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

