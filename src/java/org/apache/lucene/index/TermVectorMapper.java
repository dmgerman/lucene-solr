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
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * The TermVectorMapper can be used to map Term Vectors into your own  * structure instead of the parallel array structure used by  * {@link org.apache.lucene.index.IndexReader#getTermFreqVector(int,String)}.  *<p/>  * It is up to the implementation to make sure it is thread-safe.  *  *  **/
end_comment

begin_class
DECL|class|TermVectorMapper
specifier|public
specifier|abstract
class|class
name|TermVectorMapper
block|{
DECL|field|ignoringPositions
specifier|private
name|boolean
name|ignoringPositions
decl_stmt|;
DECL|field|ignoringOffsets
specifier|private
name|boolean
name|ignoringOffsets
decl_stmt|;
DECL|method|TermVectorMapper
specifier|protected
name|TermVectorMapper
parameter_list|()
block|{   }
comment|/**    *    * @param ignoringPositions true if this mapper should tell Lucene to ignore positions even if they are stored    * @param ignoringOffsets similar to ignoringPositions    */
DECL|method|TermVectorMapper
specifier|protected
name|TermVectorMapper
parameter_list|(
name|boolean
name|ignoringPositions
parameter_list|,
name|boolean
name|ignoringOffsets
parameter_list|)
block|{
name|this
operator|.
name|ignoringPositions
operator|=
name|ignoringPositions
expr_stmt|;
name|this
operator|.
name|ignoringOffsets
operator|=
name|ignoringOffsets
expr_stmt|;
block|}
comment|/**    * Tell the mapper what to expect in regards to field, number of terms, offset and position storage.    * This method will be called once before retrieving the vector for a field.    *    * This method will be called before {@link #map(String,int,TermVectorOffsetInfo[],int[])}.    * @param field The field the vector is for    * @param numTerms The number of terms that need to be mapped    * @param storeOffsets true if the mapper should expect offset information    * @param storePositions true if the mapper should expect positions info    */
DECL|method|setExpectations
specifier|public
specifier|abstract
name|void
name|setExpectations
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|storeOffsets
parameter_list|,
name|boolean
name|storePositions
parameter_list|)
function_decl|;
comment|/**    * Map the Term Vector information into your own structure    * @param term The term to add to the vector    * @param frequency The frequency of the term in the document    * @param offsets null if the offset is not specified, otherwise the offset into the field of the term    * @param positions null if the position is not specified, otherwise the position in the field of the term    */
DECL|method|map
specifier|public
specifier|abstract
name|void
name|map
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|frequency
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|,
name|int
index|[]
name|positions
parameter_list|)
function_decl|;
comment|/**    * Indicate to Lucene that even if there are positions stored, this mapper is not interested in them and they    * can be skipped over.  Derived classes should set this to true if they want to ignore positions.  The default    * is false, meaning positions will be loaded if they are stored.    * @return false    */
DECL|method|isIgnoringPositions
specifier|public
name|boolean
name|isIgnoringPositions
parameter_list|()
block|{
return|return
name|ignoringPositions
return|;
block|}
comment|/**    *    * @see #isIgnoringPositions() Same principal as {@link #isIgnoringPositions()}, but applied to offsets.  false by default.    * @return false    */
DECL|method|isIgnoringOffsets
specifier|public
name|boolean
name|isIgnoringOffsets
parameter_list|()
block|{
return|return
name|ignoringOffsets
return|;
block|}
block|}
end_class

end_unit

