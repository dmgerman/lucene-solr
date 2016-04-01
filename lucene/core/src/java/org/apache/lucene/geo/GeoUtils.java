begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
block|}
end_class

end_unit

