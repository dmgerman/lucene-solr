begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|max
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|PI
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
name|asin
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
name|TO_DEGREES
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
name|TO_RADIANS
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
comment|/** major (a) axis in meters */
DECL|field|SEMIMAJOR_AXIS
specifier|public
specifier|static
specifier|final
name|double
name|SEMIMAJOR_AXIS
init|=
literal|6_378_137
decl_stmt|;
comment|// [m]
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
comment|/** validates polygon values are within standard +/-180 coordinate bounds, same    *  number of latitude and longitude, and is closed    */
DECL|method|checkPolygon
specifier|public
specifier|static
name|void
name|checkPolygon
parameter_list|(
name|double
index|[]
name|polyLats
parameter_list|,
name|double
index|[]
name|polyLons
parameter_list|)
block|{
if|if
condition|(
name|polyLats
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLons must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|!=
name|polyLons
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats and polyLons must be equal length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|!=
name|polyLons
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"polyLats and polyLons must be equal length"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
operator|.
name|length
operator|<
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"at least 4 polygon points required"
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLats
index|[
literal|0
index|]
operator|!=
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLats[0]="
operator|+
name|polyLats
index|[
literal|0
index|]
operator|+
literal|" polyLats["
operator|+
operator|(
name|polyLats
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLats
index|[
name|polyLats
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|polyLons
index|[
literal|0
index|]
operator|!=
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"first and last points of the polygon must be the same (it must close itself): polyLons[0]="
operator|+
name|polyLons
index|[
literal|0
index|]
operator|+
literal|" polyLons["
operator|+
operator|(
name|polyLons
operator|.
name|length
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|polyLons
index|[
name|polyLons
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|checkLatitude
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|checkLongitude
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Compute Bounding Box for a circle using WGS-84 parameters */
DECL|method|circleToBBox
specifier|public
specifier|static
name|GeoRect
name|circleToBBox
parameter_list|(
specifier|final
name|double
name|centerLat
parameter_list|,
specifier|final
name|double
name|centerLon
parameter_list|,
specifier|final
name|double
name|radiusMeters
parameter_list|)
block|{
specifier|final
name|double
name|radLat
init|=
name|TO_RADIANS
operator|*
name|centerLat
decl_stmt|;
specifier|final
name|double
name|radLon
init|=
name|TO_RADIANS
operator|*
name|centerLon
decl_stmt|;
name|double
name|radDistance
init|=
name|radiusMeters
operator|/
name|SEMIMAJOR_AXIS
decl_stmt|;
name|double
name|minLat
init|=
name|radLat
operator|-
name|radDistance
decl_stmt|;
name|double
name|maxLat
init|=
name|radLat
operator|+
name|radDistance
decl_stmt|;
name|double
name|minLon
decl_stmt|;
name|double
name|maxLon
decl_stmt|;
if|if
condition|(
name|minLat
operator|>
name|MIN_LAT_RADIANS
operator|&&
name|maxLat
operator|<
name|MAX_LAT_RADIANS
condition|)
block|{
name|double
name|deltaLon
init|=
name|asin
argument_list|(
name|sloppySin
argument_list|(
name|radDistance
argument_list|)
operator|/
name|cos
argument_list|(
name|radLat
argument_list|)
argument_list|)
decl_stmt|;
name|minLon
operator|=
name|radLon
operator|-
name|deltaLon
expr_stmt|;
if|if
condition|(
name|minLon
operator|<
name|MIN_LON_RADIANS
condition|)
block|{
name|minLon
operator|+=
literal|2d
operator|*
name|PI
expr_stmt|;
block|}
name|maxLon
operator|=
name|radLon
operator|+
name|deltaLon
expr_stmt|;
if|if
condition|(
name|maxLon
operator|>
name|MAX_LON_RADIANS
condition|)
block|{
name|maxLon
operator|-=
literal|2d
operator|*
name|PI
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// a pole is within the distance
name|minLat
operator|=
name|max
argument_list|(
name|minLat
argument_list|,
name|MIN_LAT_RADIANS
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|min
argument_list|(
name|maxLat
argument_list|,
name|MAX_LAT_RADIANS
argument_list|)
expr_stmt|;
name|minLon
operator|=
name|MIN_LON_RADIANS
expr_stmt|;
name|maxLon
operator|=
name|MAX_LON_RADIANS
expr_stmt|;
block|}
return|return
operator|new
name|GeoRect
argument_list|(
name|TO_DEGREES
operator|*
name|minLat
argument_list|,
name|TO_DEGREES
operator|*
name|maxLat
argument_list|,
name|TO_DEGREES
operator|*
name|minLon
argument_list|,
name|TO_DEGREES
operator|*
name|maxLon
argument_list|)
return|;
block|}
comment|/** Compute Bounding Box for a polygon using WGS-84 parameters */
DECL|method|polyToBBox
specifier|public
specifier|static
name|GeoRect
name|polyToBBox
parameter_list|(
name|double
index|[]
name|polyLats
parameter_list|,
name|double
index|[]
name|polyLons
parameter_list|)
block|{
name|checkPolygon
argument_list|(
name|polyLats
argument_list|,
name|polyLons
argument_list|)
expr_stmt|;
name|double
name|minLon
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLon
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
name|double
name|minLat
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
name|double
name|maxLat
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|polyLats
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|minLat
operator|=
name|min
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|minLat
argument_list|)
expr_stmt|;
name|maxLat
operator|=
name|max
argument_list|(
name|polyLats
index|[
name|i
index|]
argument_list|,
name|maxLat
argument_list|)
expr_stmt|;
name|minLon
operator|=
name|min
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|minLon
argument_list|)
expr_stmt|;
name|maxLon
operator|=
name|max
argument_list|(
name|polyLons
index|[
name|i
index|]
argument_list|,
name|maxLon
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GeoRect
argument_list|(
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
return|;
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
specifier|private
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
block|}
end_class

end_unit

