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
comment|/** Provides access to stored term vector of   *  a document field.  */
end_comment

begin_interface
DECL|interface|TermFreqVector
specifier|public
interface|interface
name|TermFreqVector
block|{
comment|/**    *     * @return The field this vector is associated with.    *     */
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
function_decl|;
comment|/**     * @return The number of terms in the term vector.    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**     * @return An Array of term texts in ascending order.    */
DECL|method|getTerms
specifier|public
name|String
index|[]
name|getTerms
parameter_list|()
function_decl|;
comment|/** Array of term frequencies. Locations of the array correspond one to one    *  to the terms in the array obtained from<code>getTerms</code>    *  method. Each location in the array contains the number of times this    *  term occurs in the document or the document field.    */
DECL|method|getTermFrequencies
specifier|public
name|int
index|[]
name|getTermFrequencies
parameter_list|()
function_decl|;
comment|/** Return an index in the term numbers array returned from    *<code>getTerms</code> at which the term with the specified    *<code>term</code> appears. If this term does not appear in the array,    *  return -1.    */
DECL|method|indexOf
specifier|public
name|int
name|indexOf
parameter_list|(
name|String
name|term
parameter_list|)
function_decl|;
comment|/** Just like<code>indexOf(int)</code> but searches for a number of terms    *  at the same time. Returns an array that has the same size as the number    *  of terms searched for, each slot containing the result of searching for    *  that term number.    *    *  @param terms array containing terms to look for    *  @param start index in the array where the list of terms starts    *  @param len the number of terms in the list    */
DECL|method|indexesOf
specifier|public
name|int
index|[]
name|indexesOf
parameter_list|(
name|String
index|[]
name|terms
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

