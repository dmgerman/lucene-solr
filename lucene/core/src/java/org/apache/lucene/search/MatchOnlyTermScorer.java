begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
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
name|IOException
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
name|DocsEnum
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

begin_comment
comment|/** Expert: A<code>Scorer</code> for documents matching a  *<code>Term</code>.  It treats all documents as having  * one occurrenc (tf=1) for the term.  */
end_comment

begin_class
DECL|class|MatchOnlyTermScorer
specifier|final
class|class
name|MatchOnlyTermScorer
extends|extends
name|Scorer
block|{
DECL|field|docsEnum
specifier|private
specifier|final
name|DocsEnum
name|docsEnum
decl_stmt|;
DECL|field|docScorer
specifier|private
specifier|final
name|Similarity
operator|.
name|ExactSimScorer
name|docScorer
decl_stmt|;
DECL|field|docFreq
specifier|private
specifier|final
name|int
name|docFreq
decl_stmt|;
comment|/**    * Construct a<code>TermScorer</code>.    *     * @param weight    *          The weight of the<code>Term</code> in the query.    * @param td    *          An iterator over the documents matching the<code>Term</code>.    * @param docScorer    *          The</code>Similarity.ExactSimScorer</code> implementation     *          to be used for score computations.    * @param docFreq    *          per-segment docFreq of this term    */
DECL|method|MatchOnlyTermScorer
name|MatchOnlyTermScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|DocsEnum
name|td
parameter_list|,
name|Similarity
operator|.
name|ExactSimScorer
name|docScorer
parameter_list|,
name|int
name|docFreq
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|docScorer
operator|=
name|docScorer
expr_stmt|;
name|this
operator|.
name|docsEnum
operator|=
name|td
expr_stmt|;
name|this
operator|.
name|docFreq
operator|=
name|docFreq
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|docsEnum
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|float
name|freq
parameter_list|()
block|{
return|return
literal|1.0f
return|;
block|}
comment|/**    * Advances to the next document matching the query.<br>    *     * @return the document matching the query or NO_MORE_DOCS if there are no more documents.    */
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|docsEnum
operator|.
name|nextDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
assert|assert
name|docID
argument_list|()
operator|!=
name|NO_MORE_DOCS
assert|;
return|return
name|docScorer
operator|.
name|score
argument_list|(
name|docsEnum
operator|.
name|docID
argument_list|()
argument_list|,
literal|1
argument_list|)
return|;
block|}
comment|/**    * Advances to the first match beyond the current whose document number is    * greater than or equal to a given target.<br>    * The implementation uses {@link DocsEnum#advance(int)}.    *     * @param target    *          The target document number.    * @return the matching document or NO_MORE_DOCS if none exist.    */
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docsEnum
operator|.
name|advance
argument_list|(
name|target
argument_list|)
return|;
block|}
comment|/** Returns a string representation of this<code>TermScorer</code>. */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"scorer("
operator|+
name|weight
operator|+
literal|")"
return|;
block|}
comment|// TODO: benchmark if the specialized conjunction really benefits
comment|// from these, or if instead its from sorting by docFreq, or both
DECL|method|getDocsEnum
name|DocsEnum
name|getDocsEnum
parameter_list|()
block|{
return|return
name|docsEnum
return|;
block|}
comment|// TODO: generalize something like this for scorers?
comment|// even this is just an estimation...
DECL|method|getDocFreq
name|int
name|getDocFreq
parameter_list|()
block|{
return|return
name|docFreq
return|;
block|}
block|}
end_class

end_unit

