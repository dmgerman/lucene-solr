begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|IndexReader
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
name|GeoDistanceUtils
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
name|GeoRect
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
name|GeoUtils
import|;
end_import

begin_comment
comment|/** Implements a simple point distance query on a GeoPoint field. This is based on  * {@link org.apache.lucene.search.GeoPointInBBoxQuery} and is implemented using a two phase approach. First,  * like {@code GeoPointInBBoxQueryImpl} candidate terms are queried using the numeric ranges based on  * the morton codes of the min and max lat/lon pairs that intersect the boundary of the point-radius  * circle. Terms  * passing this initial filter are then passed to a secondary {@code postFilter} method that verifies whether the  * decoded lat/lon point fall within the specified query distance (see {@link org.apache.lucene.util.SloppyMath#haversin}.  * All morton value comparisons are subject to the same precision tolerance defined in  * {@value org.apache.lucene.util.GeoUtils#TOLERANCE} and distance comparisons are subject to the accuracy of the  * haversine formula (from R.W. Sinnott, "Virtues of the Haversine", Sky and Telescope, vol. 68, no. 2, 1984, p. 159)  *  *<p>Note: This query currently uses haversine which is a sloppy distance calculation (see above reference). For large  * queries one can expect upwards of 400m error. Vincenty shrinks this to ~40m error but pays a penalty for computing  * using the spheroid  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|GeoPointDistanceQuery
specifier|public
class|class
name|GeoPointDistanceQuery
extends|extends
name|GeoPointInBBoxQuery
block|{
DECL|field|centerLon
specifier|protected
specifier|final
name|double
name|centerLon
decl_stmt|;
DECL|field|centerLat
specifier|protected
specifier|final
name|double
name|centerLat
decl_stmt|;
DECL|field|radiusMeters
specifier|protected
specifier|final
name|double
name|radiusMeters
decl_stmt|;
comment|/** NOTE: radius is in meters. */
DECL|method|GeoPointDistanceQuery
specifier|public
name|GeoPointDistanceQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|radiusMeters
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
name|GeoUtils
operator|.
name|circleToBBox
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|radiusMeters
argument_list|)
argument_list|,
name|centerLon
argument_list|,
name|centerLat
argument_list|,
name|radiusMeters
argument_list|)
expr_stmt|;
block|}
DECL|method|GeoPointDistanceQuery
specifier|private
name|GeoPointDistanceQuery
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|GeoRect
name|bbox
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|radiusMeters
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|bbox
operator|.
name|minLon
argument_list|,
name|bbox
operator|.
name|minLat
argument_list|,
name|bbox
operator|.
name|maxLon
argument_list|,
name|bbox
operator|.
name|maxLat
argument_list|)
expr_stmt|;
block|{
comment|// check longitudinal overlap (limits radius)
specifier|final
name|double
name|maxRadius
init|=
name|GeoDistanceUtils
operator|.
name|maxRadialDistanceMeters
argument_list|(
name|centerLon
argument_list|,
name|centerLat
argument_list|)
decl_stmt|;
if|if
condition|(
name|radiusMeters
operator|>
name|maxRadius
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"radiusMeters "
operator|+
name|radiusMeters
operator|+
literal|" exceeds maxRadius ["
operator|+
name|maxRadius
operator|+
literal|"] at location ["
operator|+
name|centerLon
operator|+
literal|" "
operator|+
name|centerLat
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLon
argument_list|(
name|centerLon
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid centerLon "
operator|+
name|centerLon
argument_list|)
throw|;
block|}
if|if
condition|(
name|GeoUtils
operator|.
name|isValidLat
argument_list|(
name|centerLat
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid centerLat "
operator|+
name|centerLat
argument_list|)
throw|;
block|}
if|if
condition|(
name|radiusMeters
operator|<=
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid radiusMeters "
operator|+
name|radiusMeters
argument_list|)
throw|;
block|}
name|this
operator|.
name|centerLon
operator|=
name|centerLon
expr_stmt|;
name|this
operator|.
name|centerLat
operator|=
name|centerLat
expr_stmt|;
name|this
operator|.
name|radiusMeters
operator|=
name|radiusMeters
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
block|{
comment|// query crosses dateline; split into left and right queries
if|if
condition|(
name|maxLon
operator|<
name|minLon
condition|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|bqb
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
comment|// unwrap the longitude iff outside the specified min/max lon range
name|double
name|unwrappedLon
init|=
name|centerLon
decl_stmt|;
if|if
condition|(
name|unwrappedLon
operator|>
name|maxLon
condition|)
block|{
comment|// unwrap left
name|unwrappedLon
operator|+=
operator|-
literal|360.0D
expr_stmt|;
block|}
name|GeoPointDistanceQueryImpl
name|left
init|=
operator|new
name|GeoPointDistanceQueryImpl
argument_list|(
name|field
argument_list|,
name|this
argument_list|,
name|unwrappedLon
argument_list|,
operator|new
name|GeoRect
argument_list|(
name|GeoUtils
operator|.
name|MIN_LON_INCL
argument_list|,
name|maxLon
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
argument_list|)
decl_stmt|;
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|left
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|unwrappedLon
operator|<
name|maxLon
condition|)
block|{
comment|// unwrap right
name|unwrappedLon
operator|+=
literal|360.0D
expr_stmt|;
block|}
name|GeoPointDistanceQueryImpl
name|right
init|=
operator|new
name|GeoPointDistanceQueryImpl
argument_list|(
name|field
argument_list|,
name|this
argument_list|,
name|unwrappedLon
argument_list|,
operator|new
name|GeoRect
argument_list|(
name|minLon
argument_list|,
name|GeoUtils
operator|.
name|MAX_LON_INCL
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|)
argument_list|)
decl_stmt|;
name|bqb
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|right
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bqb
operator|.
name|build
argument_list|()
return|;
block|}
return|return
operator|new
name|GeoPointDistanceQueryImpl
argument_list|(
name|field
argument_list|,
name|this
argument_list|,
name|centerLon
argument_list|,
operator|new
name|GeoRect
argument_list|(
name|this
operator|.
name|minLon
argument_list|,
name|this
operator|.
name|maxLon
argument_list|,
name|this
operator|.
name|minLat
argument_list|,
name|this
operator|.
name|maxLat
argument_list|)
argument_list|)
return|;
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
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|GeoPointDistanceQuery
operator|)
condition|)
return|return
literal|false
return|;
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
name|GeoPointDistanceQuery
name|that
init|=
operator|(
name|GeoPointDistanceQuery
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
name|centerLat
argument_list|,
name|centerLat
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
name|centerLon
argument_list|,
name|centerLon
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
name|radiusMeters
argument_list|,
name|radiusMeters
argument_list|)
operator|!=
literal|0
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
name|temp
operator|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|centerLon
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
name|centerLat
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
name|radiusMeters
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
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" field="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|append
argument_list|(
literal|" Center: ["
argument_list|)
operator|.
name|append
argument_list|(
name|centerLon
argument_list|)
operator|.
name|append
argument_list|(
literal|','
argument_list|)
operator|.
name|append
argument_list|(
name|centerLat
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" Distance: "
argument_list|)
operator|.
name|append
argument_list|(
name|radiusMeters
argument_list|)
operator|.
name|append
argument_list|(
literal|" meters"
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getCenterLon
specifier|public
name|double
name|getCenterLon
parameter_list|()
block|{
return|return
name|this
operator|.
name|centerLon
return|;
block|}
DECL|method|getCenterLat
specifier|public
name|double
name|getCenterLat
parameter_list|()
block|{
return|return
name|this
operator|.
name|centerLat
return|;
block|}
DECL|method|getRadiusMeters
specifier|public
name|double
name|getRadiusMeters
parameter_list|()
block|{
return|return
name|this
operator|.
name|radiusMeters
return|;
block|}
block|}
end_class

end_unit

