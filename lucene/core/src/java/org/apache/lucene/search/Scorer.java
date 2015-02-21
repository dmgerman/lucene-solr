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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/**  * Expert: Common scoring functionality for different types of queries.  *  *<p>  * A<code>Scorer</code> iterates over documents matching a  * query in increasing order of doc Id.  *</p>  *<p>  * Document scores are computed using a given<code>Similarity</code>  * implementation.  *</p>  *  *<p><b>NOTE</b>: The values Float.Nan,  * Float.NEGATIVE_INFINITY and Float.POSITIVE_INFINITY are  * not valid scores.  Certain collectors (eg {@link  * TopScoreDocCollector}) will not properly collect hits  * with these scores.  */
end_comment

begin_class
DECL|class|Scorer
specifier|public
specifier|abstract
class|class
name|Scorer
extends|extends
name|DocIdSetIterator
block|{
comment|/** the Scorer's parent Weight. in some cases this may be null */
comment|// TODO can we clean this up?
DECL|field|weight
specifier|protected
specifier|final
name|Weight
name|weight
decl_stmt|;
comment|/**    * Constructs a Scorer    * @param weight The scorers<code>Weight</code>.    */
DECL|method|Scorer
specifier|protected
name|Scorer
parameter_list|(
name|Weight
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|/** Returns the score of the current document matching the query.    * Initially invalid, until {@link #nextDoc()} or {@link #advance(int)}    * is called the first time, or when called from within    * {@link LeafCollector#collect}.    */
DECL|method|score
specifier|public
specifier|abstract
name|float
name|score
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Returns the freq of this Scorer on the current document */
DECL|method|freq
specifier|public
specifier|abstract
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** returns parent Weight    * @lucene.experimental    */
DECL|method|getWeight
specifier|public
name|Weight
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
comment|/** Returns child sub-scorers    * @lucene.experimental */
DECL|method|getChildren
specifier|public
name|Collection
argument_list|<
name|ChildScorer
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/** A child Scorer and its relationship to its parent.    * the meaning of the relationship depends upon the parent query.     * @lucene.experimental */
DECL|class|ChildScorer
specifier|public
specifier|static
class|class
name|ChildScorer
block|{
comment|/**      * Child Scorer. (note this is typically a direct child, and may      * itself also have children).      */
DECL|field|child
specifier|public
specifier|final
name|Scorer
name|child
decl_stmt|;
comment|/**      * An arbitrary string relating this scorer to the parent.      */
DECL|field|relationship
specifier|public
specifier|final
name|String
name|relationship
decl_stmt|;
comment|/**      * Creates a new ChildScorer node with the specified relationship.      *<p>      * The relationship can be any be any string that makes sense to       * the parent Scorer.       */
DECL|method|ChildScorer
specifier|public
name|ChildScorer
parameter_list|(
name|Scorer
name|child
parameter_list|,
name|String
name|relationship
parameter_list|)
block|{
name|this
operator|.
name|child
operator|=
name|child
expr_stmt|;
name|this
operator|.
name|relationship
operator|=
name|relationship
expr_stmt|;
block|}
block|}
comment|/**    * Optional method: Return a {@link TwoPhaseDocIdSetIterator} view of this    * {@link Scorer}. A return value of {@code null} indicates that    * two-phase iteration is not supported.    *    * Note that the returned {@link TwoPhaseDocIdSetIterator}'s    * {@link TwoPhaseDocIdSetIterator#approximation() approximation} must    * advance synchronously with this iterator: advancing the approximation must    * advance this iterator and vice-versa.    *    * Implementing this method is typically useful on {@link Scorer}s    * that have a high per-document overhead in order to confirm matches.    *    * The default implementation returns {@code null}.    */
DECL|method|asTwoPhaseIterator
specifier|public
name|TwoPhaseDocIdSetIterator
name|asTwoPhaseIterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

