begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|AfterEffect
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
name|AfterEffect
operator|.
name|NoAfterEffect
import|;
end_import

begin_comment
comment|// javadoc
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
name|AfterEffectB
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
name|AfterEffectL
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
name|BasicModel
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
name|BasicModelBE
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
name|BasicModelD
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
name|BasicModelG
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
name|BasicModelIF
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
name|BasicModelIn
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
name|BasicModelIne
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
name|BasicModelP
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
name|DFRSimilarity
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
comment|// javadoc
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
name|NormalizationH1
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
name|NormalizationH2
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
name|NormalizationH3
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
name|NormalizationZ
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
operator|.
name|SimilarityFactory
import|;
end_import

begin_comment
comment|/**  * Factory for {@link DFRSimilarity}  *<p>  * You must specify the implementations for all three components of  * DFR (strings). In general the models are parameter-free, but two of the  * normalizations take floating point parameters (see below):  *<ol>  *<li>{@link BasicModel basicModel}: Basic model of information content:  *<ul>  *<li>{@link BasicModelBE Be}: Limiting form of Bose-Einstein  *<li>{@link BasicModelG G}: Geometric approximation of Bose-Einstein  *<li>{@link BasicModelP P}: Poisson approximation of the Binomial  *<li>{@link BasicModelD D}: Divergence approximation of the Binomial   *<li>{@link BasicModelIn I(n)}: Inverse document frequency  *<li>{@link BasicModelIne I(ne)}: Inverse expected document  *               frequency [mixture of Poisson and IDF]  *<li>{@link BasicModelIF I(F)}: Inverse term frequency  *               [approximation of I(ne)]  *</ul>  *<li>{@link AfterEffect afterEffect}: First normalization of information  *        gain:  *<ul>  *<li>{@link AfterEffectL L}: Laplace's law of succession  *<li>{@link AfterEffectB B}: Ratio of two Bernoulli processes  *<li>{@link NoAfterEffect none}: no first normalization  *</ul>  *<li>{@link Normalization normalization}: Second (length) normalization:  *<ul>  *<li>{@link NormalizationH1 H1}: Uniform distribution of term  *               frequency  *<li>{@link NormalizationH2 H2}: term frequency density inversely  *               related to length  *<li>{@link NormalizationH3 H3}: term frequency normalization  *               provided by Dirichlet prior  *<ul>  *<li>parameter mu (float): smoothing parameter&mu;. The  *                      default is<code>800</code>  *</ul>  *<li>{@link NormalizationZ Z}: term frequency normalization provided  *                by a Zipfian relation  *<ul>  *<li>parameter z (float): represents<code>A/(A+1)</code>  *                      where A measures the specificity of the language.  *                      The default is<code>0.3</code>  *</ul>  *<li>{@link NoNormalization none}: no second normalization  *</ul>  *</ol>  *<p>  *<p>  * Optional settings:  *<ul>  *<li>discountOverlaps (bool): Sets  *       {@link DFRSimilarity#setDiscountOverlaps(boolean)}</li>  *</ul>  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DFRSimilarityFactory
specifier|public
class|class
name|DFRSimilarityFactory
extends|extends
name|SimilarityFactory
block|{
DECL|field|discountOverlaps
specifier|private
name|boolean
name|discountOverlaps
decl_stmt|;
DECL|field|basicModel
specifier|private
name|BasicModel
name|basicModel
decl_stmt|;
DECL|field|afterEffect
specifier|private
name|AfterEffect
name|afterEffect
decl_stmt|;
DECL|field|normalization
specifier|private
name|Normalization
name|normalization
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|SolrParams
name|params
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|discountOverlaps
operator|=
name|params
operator|.
name|getBool
argument_list|(
literal|"discountOverlaps"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|basicModel
operator|=
name|parseBasicModel
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"basicModel"
argument_list|)
argument_list|)
expr_stmt|;
name|afterEffect
operator|=
name|parseAfterEffect
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"afterEffect"
argument_list|)
argument_list|)
expr_stmt|;
name|normalization
operator|=
name|parseNormalization
argument_list|(
name|params
operator|.
name|get
argument_list|(
literal|"normalization"
argument_list|)
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"mu"
argument_list|)
argument_list|,
name|params
operator|.
name|get
argument_list|(
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseBasicModel
specifier|private
name|BasicModel
name|parseBasicModel
parameter_list|(
name|String
name|expr
parameter_list|)
block|{
if|if
condition|(
literal|"Be"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelBE
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"D"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelD
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"G"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelG
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"I(F)"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelIF
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"I(n)"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelIn
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"I(ne)"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelIne
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"P"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|BasicModelP
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid basicModel: "
operator|+
name|expr
argument_list|)
throw|;
block|}
block|}
DECL|method|parseAfterEffect
specifier|private
name|AfterEffect
name|parseAfterEffect
parameter_list|(
name|String
name|expr
parameter_list|)
block|{
if|if
condition|(
literal|"B"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|AfterEffectB
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"L"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|AfterEffectL
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|AfterEffect
operator|.
name|NoAfterEffect
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid afterEffect: "
operator|+
name|expr
argument_list|)
throw|;
block|}
block|}
comment|// also used by IBSimilarityFactory
DECL|method|parseNormalization
specifier|static
name|Normalization
name|parseNormalization
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
name|mu
parameter_list|,
name|String
name|z
parameter_list|)
block|{
if|if
condition|(
name|mu
operator|!=
literal|null
operator|&&
name|z
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"specifying mu and z make no sense for: "
operator|+
name|expr
argument_list|)
throw|;
block|}
if|if
condition|(
name|mu
operator|!=
literal|null
operator|&&
operator|!
literal|"H3"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"parameter mu only makes sense for normalization H3"
argument_list|)
throw|;
block|}
if|if
condition|(
name|z
operator|!=
literal|null
operator|&&
operator|!
literal|"Z"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"parameter z only makes sense for normalization Z"
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"H1"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|NormalizationH1
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"H2"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|NormalizationH2
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"H3"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|(
name|mu
operator|!=
literal|null
operator|)
condition|?
operator|new
name|NormalizationH3
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|mu
argument_list|)
argument_list|)
else|:
operator|new
name|NormalizationH3
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"Z"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|(
name|z
operator|!=
literal|null
operator|)
condition|?
operator|new
name|NormalizationZ
argument_list|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|z
argument_list|)
argument_list|)
else|:
operator|new
name|NormalizationZ
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|expr
argument_list|)
condition|)
block|{
return|return
operator|new
name|Normalization
operator|.
name|NoNormalization
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid normalization: "
operator|+
name|expr
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getSimilarity
specifier|public
name|Similarity
name|getSimilarity
parameter_list|()
block|{
name|DFRSimilarity
name|sim
init|=
operator|new
name|DFRSimilarity
argument_list|(
name|basicModel
argument_list|,
name|afterEffect
argument_list|,
name|normalization
argument_list|)
decl_stmt|;
name|sim
operator|.
name|setDiscountOverlaps
argument_list|(
name|discountOverlaps
argument_list|)
expr_stmt|;
return|return
name|sim
return|;
block|}
block|}
end_class

end_unit

