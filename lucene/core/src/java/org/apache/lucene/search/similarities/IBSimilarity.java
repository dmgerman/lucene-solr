begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|java
operator|.
name|util
operator|.
name|List
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
name|Explanation
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
name|Normalization
operator|.
name|NoNormalization
import|;
end_import

begin_comment
comment|/**  * Provides a framework for the family of information-based models, as described  * in St&eacute;phane Clinchant and Eric Gaussier. 2010. Information-based  * models for ad hoc IR. In Proceeding of the 33rd international ACM SIGIR  * conference on Research and development in information retrieval (SIGIR '10).  * ACM, New York, NY, USA, 234-241.  *<p>The retrieval function is of the form<em>RSV(q, d) =&sum;  * -x<sup>q</sup><sub>w</sub> log Prob(X<sub>w</sub>&ge;  * t<sup>d</sup><sub>w</sub> |&lambda;<sub>w</sub>)</em>, where  *<ul>  *<li><em>x<sup>q</sup><sub>w</sub></em> is the query boost;</li>  *<li><em>X<sub>w</sub></em> is a random variable that counts the occurrences  *   of word<em>w</em>;</li>  *<li><em>t<sup>d</sup><sub>w</sub></em> is the normalized term frequency;</li>  *<li><em>&lambda;<sub>w</sub></em> is a parameter.</li>  *</ul>  *<p>The framework described in the paper has many similarities to the DFR  * framework (see {@link DFRSimilarity}). It is possible that the two  * Similarities will be merged at one point.</p>  *<p>To construct an IBSimilarity, you must specify the implementations for   * all three components of the Information-Based model.  *<ol>  *<li>{@link Distribution}: Probabilistic distribution used to  *         model term occurrence  *<ul>  *<li>{@link DistributionLL}: Log-logistic</li>  *<li>{@link DistributionLL}: Smoothed power-law</li>  *</ul>  *</li>  *<li>{@link Lambda}:&lambda;<sub>w</sub> parameter of the  *         probability distribution  *<ul>  *<li>{@link LambdaDF}:<code>N<sub>w</sub>/N</code> or average  *                 number of documents where w occurs</li>  *<li>{@link LambdaTTF}:<code>F<sub>w</sub>/N</code> or  *                 average number of occurrences of w in the collection</li>  *</ul>  *</li>  *<li>{@link Normalization}: Term frequency normalization   *<blockquote>Any supported DFR normalization (listed in  *                      {@link DFRSimilarity})</blockquote>  *</li>  *</ol>  * @see DFRSimilarity  * @lucene.experimental   */
end_comment

begin_class
DECL|class|IBSimilarity
specifier|public
class|class
name|IBSimilarity
extends|extends
name|SimilarityBase
block|{
comment|/** The probabilistic distribution used to model term occurrence. */
DECL|field|distribution
specifier|protected
specifier|final
name|Distribution
name|distribution
decl_stmt|;
comment|/** The<em>lambda (&lambda;<sub>w</sub>)</em> parameter. */
DECL|field|lambda
specifier|protected
specifier|final
name|Lambda
name|lambda
decl_stmt|;
comment|/** The term frequency normalization. */
DECL|field|normalization
specifier|protected
specifier|final
name|Normalization
name|normalization
decl_stmt|;
comment|/**    * Creates IBSimilarity from the three components.    *<p>    * Note that<code>null</code> values are not allowed:    * if you want no normalization, instead pass     * {@link NoNormalization}.    * @param distribution probabilistic distribution modeling term occurrence    * @param lambda distribution's&lambda;<sub>w</sub> parameter    * @param normalization term frequency normalization    */
DECL|method|IBSimilarity
specifier|public
name|IBSimilarity
parameter_list|(
name|Distribution
name|distribution
parameter_list|,
name|Lambda
name|lambda
parameter_list|,
name|Normalization
name|normalization
parameter_list|)
block|{
name|this
operator|.
name|distribution
operator|=
name|distribution
expr_stmt|;
name|this
operator|.
name|lambda
operator|=
name|lambda
expr_stmt|;
name|this
operator|.
name|normalization
operator|=
name|normalization
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|protected
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
return|return
name|stats
operator|.
name|getBoost
argument_list|()
operator|*
name|distribution
operator|.
name|score
argument_list|(
name|stats
argument_list|,
name|normalization
operator|.
name|tfn
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|docLen
argument_list|)
argument_list|,
name|lambda
operator|.
name|lambda
argument_list|(
name|stats
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|explain
specifier|protected
name|void
name|explain
parameter_list|(
name|List
argument_list|<
name|Explanation
argument_list|>
name|subs
parameter_list|,
name|BasicStats
name|stats
parameter_list|,
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|,
name|float
name|docLen
parameter_list|)
block|{
if|if
condition|(
name|stats
operator|.
name|getBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|subs
operator|.
name|add
argument_list|(
name|Explanation
operator|.
name|match
argument_list|(
name|stats
operator|.
name|getBoost
argument_list|()
argument_list|,
literal|"boost"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Explanation
name|normExpl
init|=
name|normalization
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|freq
argument_list|,
name|docLen
argument_list|)
decl_stmt|;
name|Explanation
name|lambdaExpl
init|=
name|lambda
operator|.
name|explain
argument_list|(
name|stats
argument_list|)
decl_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|normExpl
argument_list|)
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|lambdaExpl
argument_list|)
expr_stmt|;
name|subs
operator|.
name|add
argument_list|(
name|distribution
operator|.
name|explain
argument_list|(
name|stats
argument_list|,
name|normExpl
operator|.
name|getValue
argument_list|()
argument_list|,
name|lambdaExpl
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The name of IB methods follow the pattern    * {@code IB<distribution><lambda><normalization>}. The name of the    * distribution is the same as in the original paper; for the names of lambda    * parameters, refer to the javadoc of the {@link Lambda} classes.    */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"IB "
operator|+
name|distribution
operator|.
name|toString
argument_list|()
operator|+
literal|"-"
operator|+
name|lambda
operator|.
name|toString
argument_list|()
operator|+
name|normalization
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns the distribution    */
DECL|method|getDistribution
specifier|public
name|Distribution
name|getDistribution
parameter_list|()
block|{
return|return
name|distribution
return|;
block|}
comment|/**    * Returns the distribution's lambda parameter    */
DECL|method|getLambda
specifier|public
name|Lambda
name|getLambda
parameter_list|()
block|{
return|return
name|lambda
return|;
block|}
comment|/**    * Returns the term frequency normalization    */
DECL|method|getNormalization
specifier|public
name|Normalization
name|getNormalization
parameter_list|()
block|{
return|return
name|normalization
return|;
block|}
block|}
end_class

end_unit

