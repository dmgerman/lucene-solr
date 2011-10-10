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
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|BM25Similarity
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
name|DefaultSimilarity
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
name|DefaultSimilarityProvider
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
name|Distribution
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
name|DistributionLL
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
name|DistributionSPL
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
name|IBSimilarity
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
name|LMDirichletSimilarity
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
name|LMJelinekMercerSimilarity
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
name|Lambda
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
name|LambdaDF
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
name|LambdaTTF
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

begin_class
DECL|class|RandomSimilarityProvider
specifier|public
class|class
name|RandomSimilarityProvider
extends|extends
name|DefaultSimilarityProvider
block|{
DECL|field|knownSims
specifier|final
name|List
argument_list|<
name|Similarity
argument_list|>
name|knownSims
decl_stmt|;
DECL|field|previousMappings
name|Map
argument_list|<
name|String
argument_list|,
name|Similarity
argument_list|>
name|previousMappings
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Similarity
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|perFieldSeed
specifier|final
name|int
name|perFieldSeed
decl_stmt|;
DECL|field|shouldCoord
specifier|final
name|boolean
name|shouldCoord
decl_stmt|;
DECL|field|shouldQueryNorm
specifier|final
name|boolean
name|shouldQueryNorm
decl_stmt|;
DECL|method|RandomSimilarityProvider
specifier|public
name|RandomSimilarityProvider
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|perFieldSeed
operator|=
name|random
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|shouldCoord
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|shouldQueryNorm
operator|=
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|knownSims
operator|=
operator|new
name|ArrayList
argument_list|<
name|Similarity
argument_list|>
argument_list|(
name|allSims
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|knownSims
argument_list|,
name|random
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|coord
specifier|public
name|float
name|coord
parameter_list|(
name|int
name|overlap
parameter_list|,
name|int
name|maxOverlap
parameter_list|)
block|{
if|if
condition|(
name|shouldCoord
condition|)
block|{
return|return
name|super
operator|.
name|coord
argument_list|(
name|overlap
argument_list|,
name|maxOverlap
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|1.0f
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|queryNorm
specifier|public
name|float
name|queryNorm
parameter_list|(
name|float
name|sumOfSquaredWeights
parameter_list|)
block|{
if|if
condition|(
name|shouldQueryNorm
condition|)
block|{
return|return
name|super
operator|.
name|queryNorm
argument_list|(
name|sumOfSquaredWeights
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|1.0f
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
specifier|synchronized
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
assert|assert
name|field
operator|!=
literal|null
assert|;
name|Similarity
name|sim
init|=
name|previousMappings
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|sim
operator|==
literal|null
condition|)
block|{
name|sim
operator|=
name|knownSims
operator|.
name|get
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|perFieldSeed
operator|^
name|field
operator|.
name|hashCode
argument_list|()
argument_list|)
operator|%
name|knownSims
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|previousMappings
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|sim
argument_list|)
expr_stmt|;
block|}
return|return
name|sim
return|;
block|}
comment|// all the similarities that we rotate through
comment|/** The DFR basic models to test. */
DECL|field|BASIC_MODELS
specifier|static
name|BasicModel
index|[]
name|BASIC_MODELS
init|=
block|{
comment|/* TODO: enable new BasicModelBE(), */
comment|/* TODO: enable new BasicModelD(), */
operator|new
name|BasicModelG
argument_list|()
block|,
operator|new
name|BasicModelIF
argument_list|()
block|,
operator|new
name|BasicModelIn
argument_list|()
block|,
operator|new
name|BasicModelIne
argument_list|()
block|,
comment|/* TODO: enable new BasicModelP() */
block|}
decl_stmt|;
comment|/** The DFR aftereffects to test. */
DECL|field|AFTER_EFFECTS
specifier|static
name|AfterEffect
index|[]
name|AFTER_EFFECTS
init|=
block|{
operator|new
name|AfterEffectB
argument_list|()
block|,
operator|new
name|AfterEffectL
argument_list|()
block|,
operator|new
name|AfterEffect
operator|.
name|NoAfterEffect
argument_list|()
block|}
decl_stmt|;
comment|/** The DFR normalizations to test. */
DECL|field|NORMALIZATIONS
specifier|static
name|Normalization
index|[]
name|NORMALIZATIONS
init|=
block|{
operator|new
name|NormalizationH1
argument_list|()
block|,
operator|new
name|NormalizationH2
argument_list|()
block|,
operator|new
name|NormalizationH3
argument_list|()
block|,
operator|new
name|NormalizationZ
argument_list|()
comment|// TODO: if we enable NoNormalization, we have to deal with
comment|// a couple tests (e.g. TestDocBoost, TestSort) that expect length normalization
comment|// new Normalization.NoNormalization()
block|}
decl_stmt|;
comment|/** The distributions for IB. */
DECL|field|DISTRIBUTIONS
specifier|static
name|Distribution
index|[]
name|DISTRIBUTIONS
init|=
block|{
operator|new
name|DistributionLL
argument_list|()
block|,
operator|new
name|DistributionSPL
argument_list|()
block|}
decl_stmt|;
comment|/** Lambdas for IB. */
DECL|field|LAMBDAS
specifier|static
name|Lambda
index|[]
name|LAMBDAS
init|=
block|{
operator|new
name|LambdaDF
argument_list|()
block|,
operator|new
name|LambdaTTF
argument_list|()
block|}
decl_stmt|;
DECL|field|allSims
specifier|static
name|List
argument_list|<
name|Similarity
argument_list|>
name|allSims
decl_stmt|;
static|static
block|{
name|allSims
operator|=
operator|new
name|ArrayList
argument_list|<
name|Similarity
argument_list|>
argument_list|()
expr_stmt|;
name|allSims
operator|.
name|add
argument_list|(
operator|new
name|DefaultSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|allSims
operator|.
name|add
argument_list|(
operator|new
name|BM25Similarity
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|BasicModel
name|basicModel
range|:
name|BASIC_MODELS
control|)
block|{
for|for
control|(
name|AfterEffect
name|afterEffect
range|:
name|AFTER_EFFECTS
control|)
block|{
for|for
control|(
name|Normalization
name|normalization
range|:
name|NORMALIZATIONS
control|)
block|{
name|allSims
operator|.
name|add
argument_list|(
operator|new
name|DFRSimilarity
argument_list|(
name|basicModel
argument_list|,
name|afterEffect
argument_list|,
name|normalization
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Distribution
name|distribution
range|:
name|DISTRIBUTIONS
control|)
block|{
for|for
control|(
name|Lambda
name|lambda
range|:
name|LAMBDAS
control|)
block|{
for|for
control|(
name|Normalization
name|normalization
range|:
name|NORMALIZATIONS
control|)
block|{
name|allSims
operator|.
name|add
argument_list|(
operator|new
name|IBSimilarity
argument_list|(
name|distribution
argument_list|,
name|lambda
argument_list|,
name|normalization
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* TODO: enable Dirichlet      allSims.add(new LMDirichletSimilarity()); */
name|allSims
operator|.
name|add
argument_list|(
operator|new
name|LMJelinekMercerSimilarity
argument_list|(
literal|0.1f
argument_list|)
argument_list|)
expr_stmt|;
name|allSims
operator|.
name|add
argument_list|(
operator|new
name|LMJelinekMercerSimilarity
argument_list|(
literal|0.7f
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|synchronized
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"RandomSimilarityProvider(queryNorm="
operator|+
name|shouldQueryNorm
operator|+
literal|",coord="
operator|+
name|shouldCoord
operator|+
literal|"): "
operator|+
name|previousMappings
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

