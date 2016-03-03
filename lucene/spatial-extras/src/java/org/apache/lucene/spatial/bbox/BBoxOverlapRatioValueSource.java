begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|locationtech
operator|.
name|spatial4j
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_comment
comment|/**  * The algorithm is implemented as envelope on envelope (rect on rect) overlays rather than  * complex polygon on complex polygon overlays.  *<p>  * Spatial relevance scoring algorithm:  *<DL>  *<DT>queryArea</DT><DD>the area of the input query envelope</DD>  *<DT>targetArea</DT><DD>the area of the target envelope (per Lucene document)</DD>  *<DT>intersectionArea</DT><DD>the area of the intersection between the query and target envelopes</DD>  *<DT>queryTargetProportion</DT><DD>A 0-1 factor that divides the score proportion between query and target.  *   0.5 is evenly.</DD>  *  *<DT>queryRatio</DT><DD>intersectionArea / queryArea; (see note)</DD>  *<DT>targetRatio</DT><DD>intersectionArea / targetArea; (see note)</DD>  *<DT>queryFactor</DT><DD>queryRatio * queryTargetProportion;</DD>  *<DT>targetFactor</DT><DD>targetRatio * (1 - queryTargetProportion);</DD>  *<DT>score</DT><DD>queryFactor + targetFactor;</DD>  *</DL>  * Additionally, note that an optional minimum side length {@code minSideLength} may be used whenever an  * area is calculated (queryArea, targetArea, intersectionArea). This allows for points or horizontal/vertical lines  * to be used as the query shape and in such case the descending order should have smallest boxes up front. Without  * this, a point or line query shape typically scores everything with the same value since there is 0 area.  *<p>  * Note: The actual computation of queryRatio and targetRatio is more complicated so that it considers  * points and lines. Lines have the ratio of overlap, and points are either 1.0 or 0.0 depending on whether  * it intersects or not.  *<p>  * Originally based on Geoportal's  *<a href="http://geoportal.svn.sourceforge.net/svnroot/geoportal/Geoportal/trunk/src/com/esri/gpt/catalog/lucene/SpatialRankingValueSource.java">  *   SpatialRankingValueSource</a> but modified quite a bit. GeoPortal's algorithm will yield a score of 0  * if either a line or point is compared, and it doesn't output a 0-1 normalized score (it multiplies the factors),  * and it doesn't support minSideLength, and it had dateline bugs.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BBoxOverlapRatioValueSource
specifier|public
class|class
name|BBoxOverlapRatioValueSource
extends|extends
name|BBoxSimilarityValueSource
block|{
DECL|field|isGeo
specifier|private
specifier|final
name|boolean
name|isGeo
decl_stmt|;
comment|//-180/+180 degrees  (not part of identity; attached to parent strategy/field)
DECL|field|queryExtent
specifier|private
specifier|final
name|Rectangle
name|queryExtent
decl_stmt|;
DECL|field|queryArea
specifier|private
specifier|final
name|double
name|queryArea
decl_stmt|;
comment|//not part of identity
DECL|field|minSideLength
specifier|private
specifier|final
name|double
name|minSideLength
decl_stmt|;
DECL|field|queryTargetProportion
specifier|private
specifier|final
name|double
name|queryTargetProportion
decl_stmt|;
comment|//TODO option to compute geodetic area
comment|/**    *    * @param rectValueSource mandatory; source of rectangles    * @param isGeo True if ctx.isGeo() and thus dateline issues should be attended to    * @param queryExtent mandatory; the query rectangle    * @param queryTargetProportion see class javadocs. Between 0 and 1.    * @param minSideLength see class javadocs. 0.0 will effectively disable.    */
DECL|method|BBoxOverlapRatioValueSource
specifier|public
name|BBoxOverlapRatioValueSource
parameter_list|(
name|ValueSource
name|rectValueSource
parameter_list|,
name|boolean
name|isGeo
parameter_list|,
name|Rectangle
name|queryExtent
parameter_list|,
name|double
name|queryTargetProportion
parameter_list|,
name|double
name|minSideLength
parameter_list|)
block|{
name|super
argument_list|(
name|rectValueSource
argument_list|)
expr_stmt|;
name|this
operator|.
name|isGeo
operator|=
name|isGeo
expr_stmt|;
name|this
operator|.
name|minSideLength
operator|=
name|minSideLength
expr_stmt|;
name|this
operator|.
name|queryExtent
operator|=
name|queryExtent
expr_stmt|;
name|this
operator|.
name|queryArea
operator|=
name|calcArea
argument_list|(
name|queryExtent
operator|.
name|getWidth
argument_list|()
argument_list|,
name|queryExtent
operator|.
name|getHeight
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|queryArea
operator|>=
literal|0
assert|;
name|this
operator|.
name|queryTargetProportion
operator|=
name|queryTargetProportion
expr_stmt|;
if|if
condition|(
name|queryTargetProportion
argument_list|<
literal|0
operator|||
name|queryTargetProportion
argument_list|>
literal|1.0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"queryTargetProportion must be>= 0 and<= 1"
argument_list|)
throw|;
block|}
comment|/** Construct with 75% weighting towards target (roughly GeoPortal's default), geo degrees assumed, no    * minimum side length. */
DECL|method|BBoxOverlapRatioValueSource
specifier|public
name|BBoxOverlapRatioValueSource
parameter_list|(
name|ValueSource
name|rectValueSource
parameter_list|,
name|Rectangle
name|queryExtent
parameter_list|)
block|{
name|this
argument_list|(
name|rectValueSource
argument_list|,
literal|true
argument_list|,
name|queryExtent
argument_list|,
literal|0.25
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|BBoxOverlapRatioValueSource
name|that
init|=
operator|(
name|BBoxOverlapRatioValueSource
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|minSideLength
argument_list|,
name|minSideLength
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|Double
operator|.
name|compare
argument_list|(
name|that
operator|.
name|queryTargetProportion
argument_list|,
name|queryTargetProportion
argument_list|)
operator|!=
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|queryExtent
operator|.
name|equals
argument_list|(
name|that
operator|.
name|queryExtent
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|long
name|temp
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|queryExtent
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|minSideLength
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|queryTargetProportion
argument_list|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|temp
operator|^
operator|(
name|temp
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|similarityDescription
specifier|protected
name|String
name|similarityDescription
parameter_list|()
block|{
return|return
name|queryExtent
operator|.
name|toString
argument_list|()
operator|+
literal|","
operator|+
name|queryTargetProportion
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|protected
name|double
name|score
parameter_list|(
name|Rectangle
name|target
parameter_list|,
name|AtomicReference
argument_list|<
name|Explanation
argument_list|>
name|exp
parameter_list|)
block|{
comment|// calculate "height": the intersection height between two boxes.
name|double
name|top
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxY
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|bottom
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinY
argument_list|()
argument_list|,
name|target
operator|.
name|getMinY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|height
init|=
name|top
operator|-
name|bottom
decl_stmt|;
if|if
condition|(
name|height
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|exp
operator|.
name|set
argument_list|(
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No intersection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
comment|//no intersection
block|}
comment|// calculate "width": the intersection width between two boxes.
name|double
name|width
init|=
literal|0
decl_stmt|;
block|{
name|Rectangle
name|a
init|=
name|queryExtent
decl_stmt|;
name|Rectangle
name|b
init|=
name|target
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|getCrossesDateLine
argument_list|()
operator|==
name|b
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
comment|//both either cross or don't
name|double
name|left
init|=
name|Math
operator|.
name|max
argument_list|(
name|a
operator|.
name|getMinX
argument_list|()
argument_list|,
name|b
operator|.
name|getMinX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|right
init|=
name|Math
operator|.
name|min
argument_list|(
name|a
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|b
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|a
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
comment|//both don't
if|if
condition|(
name|left
operator|<=
name|right
condition|)
block|{
name|width
operator|=
name|right
operator|-
name|left
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isGeo
operator|&&
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|.
name|getMinX
argument_list|()
argument_list|)
operator|==
literal|180
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|a
operator|.
name|getMaxX
argument_list|()
argument_list|)
operator|==
literal|180
operator|)
operator|&&
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|b
operator|.
name|getMinX
argument_list|()
argument_list|)
operator|==
literal|180
operator|||
name|Math
operator|.
name|abs
argument_list|(
name|b
operator|.
name|getMaxX
argument_list|()
argument_list|)
operator|==
literal|180
operator|)
condition|)
block|{
name|width
operator|=
literal|0
expr_stmt|;
comment|//both adjacent to dateline
block|}
else|else
block|{
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|exp
operator|.
name|set
argument_list|(
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No intersection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
comment|//no intersection
block|}
block|}
else|else
block|{
comment|//both cross
name|width
operator|=
name|right
operator|-
name|left
operator|+
literal|360
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|a
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
comment|//then flip
name|a
operator|=
name|target
expr_stmt|;
name|b
operator|=
name|queryExtent
expr_stmt|;
block|}
comment|//a crosses, b doesn't
name|double
name|qryWestLeft
init|=
name|Math
operator|.
name|max
argument_list|(
name|a
operator|.
name|getMinX
argument_list|()
argument_list|,
name|b
operator|.
name|getMinX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|qryWestRight
init|=
name|b
operator|.
name|getMaxX
argument_list|()
decl_stmt|;
if|if
condition|(
name|qryWestLeft
operator|<
name|qryWestRight
condition|)
name|width
operator|+=
name|qryWestRight
operator|-
name|qryWestLeft
expr_stmt|;
name|double
name|qryEastLeft
init|=
name|b
operator|.
name|getMinX
argument_list|()
decl_stmt|;
name|double
name|qryEastRight
init|=
name|Math
operator|.
name|min
argument_list|(
name|a
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|b
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|qryEastLeft
operator|<
name|qryEastRight
condition|)
name|width
operator|+=
name|qryEastRight
operator|-
name|qryEastLeft
expr_stmt|;
if|if
condition|(
name|qryWestLeft
operator|>
name|qryWestRight
operator|&&
name|qryEastLeft
operator|>
name|qryEastRight
condition|)
block|{
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|exp
operator|.
name|set
argument_list|(
name|Explanation
operator|.
name|noMatch
argument_list|(
literal|"No intersection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
comment|//no intersection
block|}
block|}
block|}
comment|// calculate queryRatio and targetRatio
name|double
name|intersectionArea
init|=
name|calcArea
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
decl_stmt|;
name|double
name|queryRatio
decl_stmt|;
if|if
condition|(
name|queryArea
operator|>
literal|0
condition|)
block|{
name|queryRatio
operator|=
name|intersectionArea
operator|/
name|queryArea
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryExtent
operator|.
name|getHeight
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//vert line
name|queryRatio
operator|=
name|height
operator|/
name|queryExtent
operator|.
name|getHeight
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryExtent
operator|.
name|getWidth
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//horiz line
name|queryRatio
operator|=
name|width
operator|/
name|queryExtent
operator|.
name|getWidth
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queryRatio
operator|=
name|queryExtent
operator|.
name|relate
argument_list|(
name|target
argument_list|)
operator|.
name|intersects
argument_list|()
condition|?
literal|1
else|:
literal|0
expr_stmt|;
comment|//could be optimized
block|}
name|double
name|targetArea
init|=
name|calcArea
argument_list|(
name|target
operator|.
name|getWidth
argument_list|()
argument_list|,
name|target
operator|.
name|getHeight
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|targetArea
operator|>=
literal|0
assert|;
name|double
name|targetRatio
decl_stmt|;
if|if
condition|(
name|targetArea
operator|>
literal|0
condition|)
block|{
name|targetRatio
operator|=
name|intersectionArea
operator|/
name|targetArea
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|target
operator|.
name|getHeight
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//vert line
name|targetRatio
operator|=
name|height
operator|/
name|target
operator|.
name|getHeight
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|target
operator|.
name|getWidth
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|//horiz line
name|targetRatio
operator|=
name|width
operator|/
name|target
operator|.
name|getWidth
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|targetRatio
operator|=
name|target
operator|.
name|relate
argument_list|(
name|queryExtent
argument_list|)
operator|.
name|intersects
argument_list|()
condition|?
literal|1
else|:
literal|0
expr_stmt|;
comment|//could be optimized
block|}
assert|assert
name|queryRatio
operator|>=
literal|0
operator|&&
name|queryRatio
operator|<=
literal|1
operator|:
name|queryRatio
assert|;
assert|assert
name|targetRatio
operator|>=
literal|0
operator|&&
name|targetRatio
operator|<=
literal|1
operator|:
name|targetRatio
assert|;
comment|// combine ratios into a score
name|double
name|queryFactor
init|=
name|queryRatio
operator|*
name|queryTargetProportion
decl_stmt|;
name|double
name|targetFactor
init|=
name|targetRatio
operator|*
operator|(
literal|1.0
operator|-
name|queryTargetProportion
operator|)
decl_stmt|;
name|double
name|score
init|=
name|queryFactor
operator|+
name|targetFactor
decl_stmt|;
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|String
name|minSideDesc
init|=
name|minSideLength
operator|>
literal|0.0
condition|?
literal|" (minSide="
operator|+
name|minSideLength
operator|+
literal|")"
else|:
literal|""
decl_stmt|;
name|exp
operator|.
name|set
argument_list|(
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|score
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": queryFactor + targetFactor"
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|intersectionArea
argument_list|,
literal|"IntersectionArea"
operator|+
name|minSideDesc
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|width
argument_list|,
literal|"width"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|height
argument_list|,
literal|"height"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|queryTargetProportion
argument_list|,
literal|"queryTargetProportion"
argument_list|)
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|queryFactor
argument_list|,
literal|"queryFactor"
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|targetRatio
argument_list|,
literal|"ratio"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|queryArea
argument_list|,
literal|"area of "
operator|+
name|queryExtent
operator|+
name|minSideDesc
argument_list|)
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|targetFactor
argument_list|,
literal|"targetFactor"
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|targetRatio
argument_list|,
literal|"ratio"
argument_list|)
argument_list|,
name|Explanation
operator|.
name|match
argument_list|(
operator|(
name|float
operator|)
name|targetArea
argument_list|,
literal|"area of "
operator|+
name|target
operator|+
name|minSideDesc
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
comment|/** Calculates the area while applying the minimum side length. */
DECL|method|calcArea
specifier|private
name|double
name|calcArea
parameter_list|(
name|double
name|width
parameter_list|,
name|double
name|height
parameter_list|)
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|minSideLength
argument_list|,
name|width
argument_list|)
operator|*
name|Math
operator|.
name|max
argument_list|(
name|minSideLength
argument_list|,
name|height
argument_list|)
return|;
block|}
block|}
end_class

end_unit

