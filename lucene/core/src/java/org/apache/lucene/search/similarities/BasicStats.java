begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
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
name|index
operator|.
name|Terms
import|;
end_import

begin_comment
comment|/**  * Stores all statistics commonly used ranking methods.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BasicStats
specifier|public
class|class
name|BasicStats
extends|extends
name|Similarity
operator|.
name|SimWeight
block|{
DECL|field|field
specifier|final
name|String
name|field
decl_stmt|;
comment|/** The number of documents. */
DECL|field|numberOfDocuments
specifier|protected
name|long
name|numberOfDocuments
decl_stmt|;
comment|/** The total number of tokens in the field. */
DECL|field|numberOfFieldTokens
specifier|protected
name|long
name|numberOfFieldTokens
decl_stmt|;
comment|/** The average field length. */
DECL|field|avgFieldLength
specifier|protected
name|float
name|avgFieldLength
decl_stmt|;
comment|/** The document frequency. */
DECL|field|docFreq
specifier|protected
name|long
name|docFreq
decl_stmt|;
comment|/** The total number of occurrences of this term across all documents. */
DECL|field|totalTermFreq
specifier|protected
name|long
name|totalTermFreq
decl_stmt|;
comment|// -------------------------- Boost-related stuff --------------------------
comment|/** For most Similarities, the immediate and the top level query boosts are    * not handled differently. Hence, this field is just the product of the    * other two. */
DECL|field|boost
specifier|protected
name|float
name|boost
decl_stmt|;
comment|/** Constructor. */
DECL|method|BasicStats
specifier|public
name|BasicStats
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|normalize
argument_list|(
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
block|}
comment|// ------------------------- Getter/setter methods -------------------------
comment|/** Returns the number of documents. */
DECL|method|getNumberOfDocuments
specifier|public
name|long
name|getNumberOfDocuments
parameter_list|()
block|{
return|return
name|numberOfDocuments
return|;
block|}
comment|/** Sets the number of documents. */
DECL|method|setNumberOfDocuments
specifier|public
name|void
name|setNumberOfDocuments
parameter_list|(
name|long
name|numberOfDocuments
parameter_list|)
block|{
name|this
operator|.
name|numberOfDocuments
operator|=
name|numberOfDocuments
expr_stmt|;
block|}
comment|/**    * Returns the total number of tokens in the field.    * @see Terms#getSumTotalTermFreq()    */
DECL|method|getNumberOfFieldTokens
specifier|public
name|long
name|getNumberOfFieldTokens
parameter_list|()
block|{
return|return
name|numberOfFieldTokens
return|;
block|}
comment|/**    * Sets the total number of tokens in the field.    * @see Terms#getSumTotalTermFreq()    */
DECL|method|setNumberOfFieldTokens
specifier|public
name|void
name|setNumberOfFieldTokens
parameter_list|(
name|long
name|numberOfFieldTokens
parameter_list|)
block|{
name|this
operator|.
name|numberOfFieldTokens
operator|=
name|numberOfFieldTokens
expr_stmt|;
block|}
comment|/** Returns the average field length. */
DECL|method|getAvgFieldLength
specifier|public
name|float
name|getAvgFieldLength
parameter_list|()
block|{
return|return
name|avgFieldLength
return|;
block|}
comment|/** Sets the average field length. */
DECL|method|setAvgFieldLength
specifier|public
name|void
name|setAvgFieldLength
parameter_list|(
name|float
name|avgFieldLength
parameter_list|)
block|{
name|this
operator|.
name|avgFieldLength
operator|=
name|avgFieldLength
expr_stmt|;
block|}
comment|/** Returns the document frequency. */
DECL|method|getDocFreq
specifier|public
name|long
name|getDocFreq
parameter_list|()
block|{
return|return
name|docFreq
return|;
block|}
comment|/** Sets the document frequency. */
DECL|method|setDocFreq
specifier|public
name|void
name|setDocFreq
parameter_list|(
name|long
name|docFreq
parameter_list|)
block|{
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
block|}
comment|/** Returns the total number of occurrences of this term across all documents. */
DECL|method|getTotalTermFreq
specifier|public
name|long
name|getTotalTermFreq
parameter_list|()
block|{
return|return
name|totalTermFreq
return|;
block|}
comment|/** Sets the total number of occurrences of this term across all documents. */
DECL|method|setTotalTermFreq
specifier|public
name|void
name|setTotalTermFreq
parameter_list|(
name|long
name|totalTermFreq
parameter_list|)
block|{
name|this
operator|.
name|totalTermFreq
operator|=
name|totalTermFreq
expr_stmt|;
block|}
comment|// -------------------------- Boost-related stuff --------------------------
comment|/** The square of the raw normalization value.    * @see #rawNormalizationValue() */
annotation|@
name|Override
DECL|method|getValueForNormalization
specifier|public
name|float
name|getValueForNormalization
parameter_list|()
block|{
name|float
name|rawValue
init|=
name|rawNormalizationValue
argument_list|()
decl_stmt|;
return|return
name|rawValue
operator|*
name|rawValue
return|;
block|}
comment|/** Computes the raw normalization value. This basic implementation returns    * the query boost. Subclasses may override this method to include other    * factors (such as idf), or to save the value for inclusion in    * {@link #normalize(float, float)}, etc.    */
DECL|method|rawNormalizationValue
specifier|protected
name|float
name|rawNormalizationValue
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
comment|/** No normalization is done. {@code boost} is saved in the object, however. */
annotation|@
name|Override
DECL|method|normalize
specifier|public
name|void
name|normalize
parameter_list|(
name|float
name|queryNorm
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
block|}
comment|/** Returns the total boost. */
DECL|method|getBoost
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|boost
return|;
block|}
block|}
end_class

end_unit

