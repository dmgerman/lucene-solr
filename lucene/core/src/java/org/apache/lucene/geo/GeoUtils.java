begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|TO_RADIANS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|cos
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SloppyMath
operator|.
name|haversinMeters
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
name|PointValues
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
name|PointValues
operator|.
name|Relation
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
name|SloppyMath
import|;
end_import

begin_comment
comment|/**  * Basic reusable geo-spatial utility methods  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoUtils
specifier|public
specifier|final
class|class
name|GeoUtils
block|{
comment|/** Minimum longitude value. */
DECL|field|MIN_LON_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LON_INCL
init|=
operator|-
literal|180.0D
decl_stmt|;
comment|/** Maximum longitude value. */
DECL|field|MAX_LON_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LON_INCL
init|=
literal|180.0D
decl_stmt|;
comment|/** Minimum latitude value. */
DECL|field|MIN_LAT_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LAT_INCL
init|=
operator|-
literal|90.0D
decl_stmt|;
comment|/** Maximum latitude value. */
DECL|field|MAX_LAT_INCL
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LAT_INCL
init|=
literal|90.0D
decl_stmt|;
comment|/** min longitude value in radians */
DECL|field|MIN_LON_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LON_RADIANS
init|=
name|TO_RADIANS
operator|*
name|MIN_LON_INCL
decl_stmt|;
comment|/** min latitude value in radians */
DECL|field|MIN_LAT_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MIN_LAT_RADIANS
init|=
name|TO_RADIANS
operator|*
name|MIN_LAT_INCL
decl_stmt|;
comment|/** max longitude value in radians */
DECL|field|MAX_LON_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LON_RADIANS
init|=
name|TO_RADIANS
operator|*
name|MAX_LON_INCL
decl_stmt|;
comment|/** max latitude value in radians */
DECL|field|MAX_LAT_RADIANS
specifier|public
specifier|static
specifier|final
name|double
name|MAX_LAT_RADIANS
init|=
name|TO_RADIANS
operator|*
name|MAX_LAT_INCL
decl_stmt|;
comment|// WGS84 earth-ellipsoid parameters
comment|/** mean earth axis in meters */
comment|// see http://earth-info.nga.mil/GandG/publications/tr8350.2/wgs84fin.pdf
DECL|field|EARTH_MEAN_RADIUS_METERS
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_MEAN_RADIUS_METERS
init|=
literal|6_371_008.7714
decl_stmt|;
comment|// No instance:
DECL|method|GeoUtils
specifier|private
name|GeoUtils
parameter_list|()
block|{   }
comment|/** validates latitude value is within standard +/-90 coordinate bounds */
DECL|method|checkLatitude
specifier|public
specifier|static
name|void
name|checkLatitude
parameter_list|(
name|double
name|latitude
parameter_list|)
block|{
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|latitude
argument_list|)
operator|||
name|latitude
argument_list|<
name|MIN_LAT_INCL
operator|||
name|latitude
argument_list|>
name|MAX_LAT_INCL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid latitude "
operator|+
name|latitude
operator|+
literal|"; must be between "
operator|+
name|MIN_LAT_INCL
operator|+
literal|" and "
operator|+
name|MAX_LAT_INCL
argument_list|)
throw|;
block|}
block|}
comment|/** validates longitude value is within standard +/-180 coordinate bounds */
DECL|method|checkLongitude
specifier|public
specifier|static
name|void
name|checkLongitude
parameter_list|(
name|double
name|longitude
parameter_list|)
block|{
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|longitude
argument_list|)
operator|||
name|longitude
argument_list|<
name|MIN_LON_INCL
operator|||
name|longitude
argument_list|>
name|MAX_LON_INCL
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid longitude "
operator|+
name|longitude
operator|+
literal|"; must be between "
operator|+
name|MIN_LON_INCL
operator|+
literal|" and "
operator|+
name|MAX_LON_INCL
argument_list|)
throw|;
block|}
block|}
comment|// some sloppyish stuff, do we really need this to be done in a sloppy way?
comment|// unless it is performance sensitive, we should try to remove.
DECL|field|PIO2
specifier|private
specifier|static
specifier|final
name|double
name|PIO2
init|=
name|Math
operator|.
name|PI
operator|/
literal|2D
decl_stmt|;
comment|/**    * Returns the trigonometric sine of an angle converted as a cos operation.    *<p>    * Note that this is not quite right... e.g. sin(0) != 0    *<p>    * Special cases:    *<ul>    *<li>If the argument is {@code NaN} or an infinity, then the result is {@code NaN}.    *</ul>    * @param a an angle, in radians.    * @return the sine of the argument.    * @see Math#sin(double)    */
comment|// TODO: deprecate/remove this? at least its no longer public.
DECL|method|sloppySin
specifier|public
specifier|static
name|double
name|sloppySin
parameter_list|(
name|double
name|a
parameter_list|)
block|{
return|return
name|cos
argument_list|(
name|a
operator|-
name|PIO2
argument_list|)
return|;
block|}
comment|/**    * binary search to find the exact sortKey needed to match the specified radius    * any sort key lte this is a query match.    */
DECL|method|distanceQuerySortKey
specifier|public
specifier|static
name|double
name|distanceQuerySortKey
parameter_list|(
name|double
name|radius
parameter_list|)
block|{
comment|// effectively infinite
if|if
condition|(
name|radius
operator|>=
name|haversinMeters
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|)
condition|)
block|{
return|return
name|haversinMeters
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
comment|// this is a search through non-negative long space only
name|long
name|lo
init|=
literal|0
decl_stmt|;
name|long
name|hi
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
while|while
condition|(
name|lo
operator|<=
name|hi
condition|)
block|{
name|long
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|>>>
literal|1
decl_stmt|;
name|double
name|sortKey
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|mid
argument_list|)
decl_stmt|;
name|double
name|midRadius
init|=
name|haversinMeters
argument_list|(
name|sortKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|midRadius
operator|==
name|radius
condition|)
block|{
return|return
name|sortKey
return|;
block|}
elseif|else
if|if
condition|(
name|midRadius
operator|>
name|radius
condition|)
block|{
name|hi
operator|=
name|mid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|lo
operator|=
name|mid
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|// not found: this is because a user can supply an arbitrary radius, one that we will never
comment|// calculate exactly via our haversin method.
name|double
name|ceil
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|lo
argument_list|)
decl_stmt|;
assert|assert
name|haversinMeters
argument_list|(
name|ceil
argument_list|)
operator|>
name|radius
assert|;
return|return
name|ceil
return|;
block|}
comment|/**    * Compute the relation between the provided box and distance query.    * This only works for boxes that do not cross the dateline.    */
DECL|method|relate
specifier|public
specifier|static
name|PointValues
operator|.
name|Relation
name|relate
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|distanceSortKey
parameter_list|,
name|double
name|axisLat
parameter_list|)
block|{
if|if
condition|(
name|minLon
operator|>
name|maxLon
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Box crosses the dateline"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|lon
argument_list|<
name|minLon
operator|||
name|lon
argument_list|>
name|maxLon
operator|)
operator|&&
operator|(
name|axisLat
operator|+
name|Rectangle
operator|.
name|AXISLAT_ERROR
argument_list|<
name|minLat
operator|||
name|axisLat
operator|-
name|Rectangle
operator|.
name|AXISLAT_ERROR
argument_list|>
name|maxLat
operator|)
condition|)
block|{
comment|// circle not fully inside / crossing axis
if|if
condition|(
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|minLat
argument_list|,
name|minLon
argument_list|)
operator|>
name|distanceSortKey
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|)
operator|>
name|distanceSortKey
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|)
operator|>
name|distanceSortKey
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|maxLat
argument_list|,
name|maxLon
argument_list|)
operator|>
name|distanceSortKey
condition|)
block|{
comment|// no points inside
return|return
name|Relation
operator|.
name|CELL_OUTSIDE_QUERY
return|;
block|}
block|}
if|if
condition|(
name|maxLon
operator|-
name|lon
operator|<
literal|90
operator|&&
name|lon
operator|-
name|minLon
operator|<
literal|90
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|minLat
argument_list|,
name|minLon
argument_list|)
operator|<=
name|distanceSortKey
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|minLat
argument_list|,
name|maxLon
argument_list|)
operator|<=
name|distanceSortKey
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|)
operator|<=
name|distanceSortKey
operator|&&
name|SloppyMath
operator|.
name|haversinSortKey
argument_list|(
name|lat
argument_list|,
name|lon
argument_list|,
name|maxLat
argument_list|,
name|maxLon
argument_list|)
operator|<=
name|distanceSortKey
condition|)
block|{
comment|// we are fully enclosed, collect everything within this subtree
return|return
name|Relation
operator|.
name|CELL_INSIDE_QUERY
return|;
block|}
return|return
name|Relation
operator|.
name|CELL_CROSSES_QUERY
return|;
block|}
block|}
end_class

end_unit

