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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AtomicReader
import|;
end_import

begin_comment
comment|// javadocs
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
name|AtomicReaderContext
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
name|IndexReaderContext
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|SimilarityProvider
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
name|Bits
import|;
end_import

begin_comment
comment|/**  * Expert: Calculate query weights and build query scorers.  *<p>  * The purpose of {@link Weight} is to ensure searching does not modify a  * {@link Query}, so that a {@link Query} instance can be reused.<br>  * {@link IndexSearcher} dependent state of the query should reside in the  * {@link Weight}.<br>  * {@link AtomicReader} dependent state should reside in the {@link Scorer}.  *<p>  * Since {@link Weight} creates {@link Scorer} instances for a given  * {@link AtomicReaderContext} ({@link #scorer(AtomicReaderContext,   * boolean, boolean, Bits)})  * callers must maintain the relationship between the searcher's top-level  * {@link IndexReaderContext} and the context used to create a {@link Scorer}.   *<p>  * A<code>Weight</code> is used in the following way:  *<ol>  *<li>A<code>Weight</code> is constructed by a top-level query, given a  *<code>IndexSearcher</code> ({@link Query#createWeight(IndexSearcher)}).  *<li>The {@link #getValueForNormalization()} method is called on the  *<code>Weight</code> to compute the query normalization factor  * {@link SimilarityProvider#queryNorm(float)} of the query clauses contained in the  * query.  *<li>The query normalization factor is passed to {@link #normalize(float, float)}. At  * this point the weighting is complete.  *<li>A<code>Scorer</code> is constructed by  * {@link #scorer(AtomicReaderContext, boolean, boolean, Bits)}.  *</ol>  *   * @since 2.9  */
end_comment

begin_class
DECL|class|Weight
specifier|public
specifier|abstract
class|class
name|Weight
block|{
comment|/**    * An explanation of the score computation for the named document.    *     * @param context the readers context to create the {@link Explanation} for.    * @param doc the document's id relative to the given context's reader    * @return an Explanation for the score    * @throws IOException if an {@link IOException} occurs    */
DECL|method|explain
specifier|public
specifier|abstract
name|Explanation
name|explain
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** The query that this concerns. */
DECL|method|getQuery
specifier|public
specifier|abstract
name|Query
name|getQuery
parameter_list|()
function_decl|;
comment|/** The value for normalization of contained query clauses (e.g. sum of squared weights). */
DECL|method|getValueForNormalization
specifier|public
specifier|abstract
name|float
name|getValueForNormalization
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Assigns the query normalization factor and boost from parent queries to this. */
DECL|method|normalize
specifier|public
specifier|abstract
name|void
name|normalize
parameter_list|(
name|float
name|norm
parameter_list|,
name|float
name|topLevelBoost
parameter_list|)
function_decl|;
comment|/**    * Returns a {@link Scorer} which scores documents in/out-of order according    * to<code>scoreDocsInOrder</code>.    *<p>    *<b>NOTE:</b> even if<code>scoreDocsInOrder</code> is false, it is    * recommended to check whether the returned<code>Scorer</code> indeed scores    * documents out of order (i.e., call {@link #scoresDocsOutOfOrder()}), as    * some<code>Scorer</code> implementations will always return documents    * in-order.<br>    *<b>NOTE:</b> null can be returned if no documents will be scored by this    * query.    *     * @param context    *          the {@link AtomicReaderContext} for which to return the {@link Scorer}.    * @param scoreDocsInOrder    *          specifies whether in-order scoring of documents is required. Note    *          that if set to false (i.e., out-of-order scoring is required),    *          this method can return whatever scoring mode it supports, as every    *          in-order scorer is also an out-of-order one. However, an    *          out-of-order scorer may not support {@link Scorer#nextDoc()}    *          and/or {@link Scorer#advance(int)}, therefore it is recommended to    *          request an in-order scorer if use of these methods is required.    * @param topScorer    *          if true, {@link Scorer#score(Collector)} will be called; if false,    *          {@link Scorer#nextDoc()} and/or {@link Scorer#advance(int)} will    *          be called.    * @param acceptDocs    *          Bits that represent the allowable docs to match (typically deleted docs    *          but possibly filtering other documents)    *              * @return a {@link Scorer} which scores documents in/out-of order.    * @throws IOException    */
DECL|method|scorer
specifier|public
specifier|abstract
name|Scorer
name|scorer
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|,
name|Bits
name|acceptDocs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns true iff this implementation scores docs only out of order. This    * method is used in conjunction with {@link Collector}'s    * {@link Collector#acceptsDocsOutOfOrder() acceptsDocsOutOfOrder} and    * {@link #scorer(AtomicReaderContext, boolean, boolean, Bits)} to    * create a matching {@link Scorer} instance for a given {@link Collector}, or    * vice versa.    *<p>    *<b>NOTE:</b> the default implementation returns<code>false</code>, i.e.    * the<code>Scorer</code> scores documents in-order.    */
DECL|method|scoresDocsOutOfOrder
specifier|public
name|boolean
name|scoresDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

