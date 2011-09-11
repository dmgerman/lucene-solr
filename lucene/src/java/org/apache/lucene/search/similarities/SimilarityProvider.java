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
name|BooleanQuery
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Expert: Scoring API.  *   * Provides top-level scoring functions that aren't specific to a field,  * and work across multi-field queries (such as {@link BooleanQuery}).  *   * Field-specific scoring is accomplished through {@link Similarity}.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|SimilarityProvider
specifier|public
interface|interface
name|SimilarityProvider
block|{
comment|/** Computes a score factor based on the fraction of all query terms that a    * document contains.  This value is multiplied into scores.    *    *<p>The presence of a large portion of the query terms indicates a better    * match with the query, so implementations of this method usually return    * larger values when the ratio between these parameters is large and smaller    * values when the ratio between them is small.    *    * @param overlap the number of query terms matched in the document    * @param maxOverlap the total number of terms in the query    * @return a score factor based on term overlap with the query    */
DECL|method|coord
specifier|public
specifier|abstract
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
function_decl|;
comment|/** Computes the normalization value for a query given the sum of the squared    * weights of each of the query terms.  This value is multiplied into the    * weight of each query term. While the classic query normalization factor is    * computed as 1/sqrt(sumOfSquaredWeights), other implementations might    * completely ignore sumOfSquaredWeights (ie return 1).    *    *<p>This does not affect ranking, but the default implementation does make scores    * from different queries more comparable than they would be by eliminating the    * magnitude of the Query vector as a factor in the score.    *    * @param sumOfSquaredWeights the sum of the squares of query term weights    * @return a normalization factor for query weights    */
DECL|method|queryNorm
specifier|public
specifier|abstract
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
function_decl|;
comment|/** Returns a {@link Similarity} for scoring a field    * @param field field name.    * @return a field-specific Similarity.    */
DECL|method|get
specifier|public
specifier|abstract
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

