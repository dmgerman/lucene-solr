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

begin_comment
comment|/**  * The smoothed power-law (SPL) distribution for the information-based framework  * that is described in the original paper.  *<p>Unlike for DFR, the natural logarithm is used, as  * it is faster to compute and the original paper does not express any  * preference to a specific base.</p>  * WARNING: this model currently returns infinite scores for very small  * tf values and negative scores for very large tf values  * @lucene.experimental  */
end_comment

begin_class
DECL|class|DistributionSPL
specifier|public
class|class
name|DistributionSPL
extends|extends
name|Distribution
block|{
comment|/** Sole constructor: parameter-free */
DECL|method|DistributionSPL
specifier|public
name|DistributionSPL
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|score
specifier|public
specifier|final
name|float
name|score
parameter_list|(
name|BasicStats
name|stats
parameter_list|,
name|float
name|tfn
parameter_list|,
name|float
name|lambda
parameter_list|)
block|{
if|if
condition|(
name|lambda
operator|==
literal|1f
condition|)
block|{
name|lambda
operator|=
literal|0.99f
expr_stmt|;
block|}
return|return
operator|(
name|float
operator|)
operator|-
name|Math
operator|.
name|log
argument_list|(
operator|(
name|Math
operator|.
name|pow
argument_list|(
name|lambda
argument_list|,
operator|(
name|tfn
operator|/
operator|(
name|tfn
operator|+
literal|1
operator|)
operator|)
argument_list|)
operator|-
name|lambda
operator|)
operator|/
operator|(
literal|1
operator|-
name|lambda
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SPL"
return|;
block|}
block|}
end_class

end_unit

