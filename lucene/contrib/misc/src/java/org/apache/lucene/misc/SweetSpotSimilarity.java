begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
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
name|index
operator|.
name|FieldInvertState
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
name|HashMap
import|;
end_import

begin_comment
comment|/**  * A similarity with a lengthNorm that provides for a "plateau" of  * equally good lengths, and tf helper functions.  *  *<p>  * For lengthNorm, A global min/max can be specified to define the  * plateau of lengths that should all have a norm of 1.0.  * Below the min, and above the max the lengthNorm drops off in a  * sqrt function.  *</p>  *<p>  * A per field min/max can be specified if different fields have  * different sweet spots.  *</p>  *  *<p>  * For tf, baselineTf and hyperbolicTf functions are provided, which  * subclasses can choose between.  *</p>  *  */
end_comment

begin_class
DECL|class|SweetSpotSimilarity
specifier|public
class|class
name|SweetSpotSimilarity
extends|extends
name|DefaultSimilarity
block|{
DECL|field|ln_min
specifier|private
name|int
name|ln_min
init|=
literal|1
decl_stmt|;
DECL|field|ln_max
specifier|private
name|int
name|ln_max
init|=
literal|1
decl_stmt|;
DECL|field|ln_steep
specifier|private
name|float
name|ln_steep
init|=
literal|0.5f
decl_stmt|;
DECL|field|ln_maxs
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
name|ln_maxs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
DECL|field|ln_mins
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
name|ln_mins
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Number
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
DECL|field|ln_steeps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|ln_steeps
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
DECL|field|ln_overlaps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|ln_overlaps
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
DECL|field|tf_base
specifier|private
name|float
name|tf_base
init|=
literal|0.0f
decl_stmt|;
DECL|field|tf_min
specifier|private
name|float
name|tf_min
init|=
literal|0.0f
decl_stmt|;
DECL|field|tf_hyper_min
specifier|private
name|float
name|tf_hyper_min
init|=
literal|0.0f
decl_stmt|;
DECL|field|tf_hyper_max
specifier|private
name|float
name|tf_hyper_max
init|=
literal|2.0f
decl_stmt|;
DECL|field|tf_hyper_base
specifier|private
name|double
name|tf_hyper_base
init|=
literal|1.3d
decl_stmt|;
DECL|field|tf_hyper_xoffset
specifier|private
name|float
name|tf_hyper_xoffset
init|=
literal|10.0f
decl_stmt|;
DECL|method|SweetSpotSimilarity
specifier|public
name|SweetSpotSimilarity
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sets the baseline and minimum function variables for baselineTf    *    * @see #baselineTf    */
DECL|method|setBaselineTfFactors
specifier|public
name|void
name|setBaselineTfFactors
parameter_list|(
name|float
name|base
parameter_list|,
name|float
name|min
parameter_list|)
block|{
name|tf_min
operator|=
name|min
expr_stmt|;
name|tf_base
operator|=
name|base
expr_stmt|;
block|}
comment|/**    * Sets the function variables for the hyperbolicTf functions    *    * @param min the minimum tf value to ever be returned (default: 0.0)    * @param max the maximum tf value to ever be returned (default: 2.0)    * @param base the base value to be used in the exponential for the hyperbolic function (default: e)    * @param xoffset the midpoint of the hyperbolic function (default: 10.0)    * @see #hyperbolicTf    */
DECL|method|setHyperbolicTfFactors
specifier|public
name|void
name|setHyperbolicTfFactors
parameter_list|(
name|float
name|min
parameter_list|,
name|float
name|max
parameter_list|,
name|double
name|base
parameter_list|,
name|float
name|xoffset
parameter_list|)
block|{
name|tf_hyper_min
operator|=
name|min
expr_stmt|;
name|tf_hyper_max
operator|=
name|max
expr_stmt|;
name|tf_hyper_base
operator|=
name|base
expr_stmt|;
name|tf_hyper_xoffset
operator|=
name|xoffset
expr_stmt|;
block|}
comment|/**    * Sets the default function variables used by lengthNorm when no field    * specific variables have been set.    *    * @see #lengthNorm    */
DECL|method|setLengthNormFactors
specifier|public
name|void
name|setLengthNormFactors
parameter_list|(
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|float
name|steepness
parameter_list|)
block|{
name|this
operator|.
name|ln_min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|ln_max
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|ln_steep
operator|=
name|steepness
expr_stmt|;
block|}
comment|/**    * Sets the function variables used by lengthNorm for a specific named field.    *     * @param field field name    * @param min minimum value    * @param max maximum value    * @param steepness steepness of the curve    * @param discountOverlaps if true,<code>numOverlapTokens</code> will be    * subtracted from<code>numTokens</code>; if false then    *<code>numOverlapTokens</code> will be assumed to be 0 (see    * {@link DefaultSimilarity#computeNorm(String, FieldInvertState)} for details).    *    * @see #lengthNorm    */
DECL|method|setLengthNormFactors
specifier|public
name|void
name|setLengthNormFactors
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|float
name|steepness
parameter_list|,
name|boolean
name|discountOverlaps
parameter_list|)
block|{
name|ln_mins
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|ln_maxs
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|ln_steeps
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|steepness
argument_list|)
argument_list|)
expr_stmt|;
name|ln_overlaps
operator|.
name|put
argument_list|(
name|field
argument_list|,
operator|new
name|Boolean
argument_list|(
name|discountOverlaps
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Implemented as<code> state.getBoost() *    * lengthNorm(fieldName, numTokens)</code> where    * numTokens does not count overlap tokens if    * discountOverlaps is true by default or true for this    * specific field. */
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|float
name|computeNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|FieldInvertState
name|state
parameter_list|)
block|{
specifier|final
name|int
name|numTokens
decl_stmt|;
name|boolean
name|overlaps
init|=
name|discountOverlaps
decl_stmt|;
if|if
condition|(
name|ln_overlaps
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|overlaps
operator|=
name|ln_overlaps
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|overlaps
condition|)
name|numTokens
operator|=
name|state
operator|.
name|getLength
argument_list|()
operator|-
name|state
operator|.
name|getNumOverlap
argument_list|()
expr_stmt|;
else|else
name|numTokens
operator|=
name|state
operator|.
name|getLength
argument_list|()
expr_stmt|;
return|return
name|state
operator|.
name|getBoost
argument_list|()
operator|*
name|computeLengthNorm
argument_list|(
name|fieldName
argument_list|,
name|numTokens
argument_list|)
return|;
block|}
comment|/**    * Implemented as:    *<code>    * 1/sqrt( steepness * (abs(x-min) + abs(x-max) - (max-min)) + 1 )    *</code>.    *    *<p>    * This degrades to<code>1/sqrt(x)</code> when min and max are both 1 and    * steepness is 0.5    *</p>    *    *<p>    * :TODO: potential optimization is to just flat out return 1.0f if numTerms    * is between min and max.    *</p>    *    * @see #setLengthNormFactors    */
DECL|method|computeLengthNorm
specifier|public
name|float
name|computeLengthNorm
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|numTerms
parameter_list|)
block|{
name|int
name|l
init|=
name|ln_min
decl_stmt|;
name|int
name|h
init|=
name|ln_max
decl_stmt|;
name|float
name|s
init|=
name|ln_steep
decl_stmt|;
if|if
condition|(
name|ln_mins
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|l
operator|=
name|ln_mins
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ln_maxs
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|h
operator|=
name|ln_maxs
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ln_steeps
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|s
operator|=
name|ln_steeps
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
return|return
call|(
name|float
call|)
argument_list|(
literal|1.0f
operator|/
name|Math
operator|.
name|sqrt
argument_list|(
operator|(
name|s
operator|*
call|(
name|float
call|)
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|numTerms
operator|-
name|l
argument_list|)
operator|+
name|Math
operator|.
name|abs
argument_list|(
name|numTerms
operator|-
name|h
argument_list|)
operator|-
operator|(
name|h
operator|-
name|l
operator|)
argument_list|)
operator|)
operator|+
literal|1.0f
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Delegates to baselineTf    *    * @see #baselineTf    */
annotation|@
name|Override
DECL|method|tf
specifier|public
name|float
name|tf
parameter_list|(
name|int
name|freq
parameter_list|)
block|{
return|return
name|baselineTf
argument_list|(
name|freq
argument_list|)
return|;
block|}
comment|/**    * Implemented as:    *<code>    *  (x&lt;= min)&#63; base : sqrt(x+(base**2)-min)    *</code>    * ...but with a special case check for 0.    *<p>    * This degrates to<code>sqrt(x)</code> when min and base are both 0    *</p>    *    * @see #setBaselineTfFactors    */
DECL|method|baselineTf
specifier|public
name|float
name|baselineTf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
if|if
condition|(
literal|0.0f
operator|==
name|freq
condition|)
return|return
literal|0.0f
return|;
return|return
operator|(
name|freq
operator|<=
name|tf_min
operator|)
condition|?
name|tf_base
else|:
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|freq
operator|+
operator|(
name|tf_base
operator|*
name|tf_base
operator|)
operator|-
name|tf_min
argument_list|)
return|;
block|}
comment|/**    * Uses a hyperbolic tangent function that allows for a hard max...    *    *<code>    * tf(x)=min+(max-min)/2*(((base**(x-xoffset)-base**-(x-xoffset))/(base**(x-xoffset)+base**-(x-xoffset)))+1)    *</code>    *    *<p>    * This code is provided as a convenience for subclasses that want    * to use a hyperbolic tf function.    *</p>    *    * @see #setHyperbolicTfFactors    */
DECL|method|hyperbolicTf
specifier|public
name|float
name|hyperbolicTf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
if|if
condition|(
literal|0.0f
operator|==
name|freq
condition|)
return|return
literal|0.0f
return|;
specifier|final
name|float
name|min
init|=
name|tf_hyper_min
decl_stmt|;
specifier|final
name|float
name|max
init|=
name|tf_hyper_max
decl_stmt|;
specifier|final
name|double
name|base
init|=
name|tf_hyper_base
decl_stmt|;
specifier|final
name|float
name|xoffset
init|=
name|tf_hyper_xoffset
decl_stmt|;
specifier|final
name|double
name|x
init|=
call|(
name|double
call|)
argument_list|(
name|freq
operator|-
name|xoffset
argument_list|)
decl_stmt|;
specifier|final
name|float
name|result
init|=
name|min
operator|+
call|(
name|float
call|)
argument_list|(
operator|(
name|max
operator|-
name|min
operator|)
operator|/
literal|2.0f
operator|*
operator|(
operator|(
operator|(
name|Math
operator|.
name|pow
argument_list|(
name|base
argument_list|,
name|x
argument_list|)
operator|-
name|Math
operator|.
name|pow
argument_list|(
name|base
argument_list|,
operator|-
name|x
argument_list|)
operator|)
operator|/
operator|(
name|Math
operator|.
name|pow
argument_list|(
name|base
argument_list|,
name|x
argument_list|)
operator|+
name|Math
operator|.
name|pow
argument_list|(
name|base
argument_list|,
operator|-
name|x
argument_list|)
operator|)
operator|)
operator|+
literal|1.0d
operator|)
argument_list|)
decl_stmt|;
return|return
name|Float
operator|.
name|isNaN
argument_list|(
name|result
argument_list|)
condition|?
name|max
else|:
name|result
return|;
block|}
block|}
end_class

end_unit

