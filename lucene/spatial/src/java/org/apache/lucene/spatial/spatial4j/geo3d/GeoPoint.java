begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.spatial4j.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|spatial4j
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * This class represents a point on the surface of a unit sphere.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|GeoPoint
specifier|public
class|class
name|GeoPoint
extends|extends
name|Vector
block|{
comment|// By making lazily-evaluated variables be "volatile", we guarantee atomicity when they
comment|// are updated.  This is necessary if we are using these classes in a multi-thread fashion,
comment|// because we don't try to synchronize for the lazy computation.
comment|/** This is the lazily-evaluated magnitude.  Some constructors include it, but others don't, and    * we try not to create extra computation by always computing it.  Does not need to be    * synchronized for thread safety, because depends wholly on immutable variables of this class. */
DECL|field|magnitude
specifier|protected
specifier|volatile
name|double
name|magnitude
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
comment|/** Lazily-evaluated latitude.  Does not need to be    * synchronized for thread safety, because depends wholly on immutable variables of this class.  */
DECL|field|latitude
specifier|protected
specifier|volatile
name|double
name|latitude
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
comment|/** Lazily-evaluated longitude.   Does not need to be    * synchronized for thread safety, because depends wholly on immutable variables of this class.  */
DECL|field|longitude
specifier|protected
specifier|volatile
name|double
name|longitude
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
comment|/** Construct a GeoPoint from the trig functions of a lat and lon pair.    * @param planetModel is the planetModel to put the point on.    * @param sinLat is the sin of the latitude.    * @param sinLon is the sin of the longitude.    * @param cosLat is the cos of the latitude.    * @param cosLon is the cos of the longitude.    * @param lat is the latitude.    * @param lon is the longitude.    */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|sinLat
parameter_list|,
specifier|final
name|double
name|sinLon
parameter_list|,
specifier|final
name|double
name|cosLat
parameter_list|,
specifier|final
name|double
name|cosLon
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|)
block|{
name|this
argument_list|(
name|computeDesiredEllipsoidMagnitude
argument_list|(
name|planetModel
argument_list|,
name|cosLat
operator|*
name|cosLon
argument_list|,
name|cosLat
operator|*
name|sinLon
argument_list|,
name|sinLat
argument_list|)
argument_list|,
name|cosLat
operator|*
name|cosLon
argument_list|,
name|cosLat
operator|*
name|sinLon
argument_list|,
name|sinLat
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a GeoPoint from the trig functions of a lat and lon pair.    * @param planetModel is the planetModel to put the point on.    * @param sinLat is the sin of the latitude.    * @param sinLon is the sin of the longitude.    * @param cosLat is the cos of the latitude.    * @param cosLon is the cos of the longitude.    */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|sinLat
parameter_list|,
specifier|final
name|double
name|sinLon
parameter_list|,
specifier|final
name|double
name|cosLat
parameter_list|,
specifier|final
name|double
name|cosLon
parameter_list|)
block|{
name|this
argument_list|(
name|computeDesiredEllipsoidMagnitude
argument_list|(
name|planetModel
argument_list|,
name|cosLat
operator|*
name|cosLon
argument_list|,
name|cosLat
operator|*
name|sinLon
argument_list|,
name|sinLat
argument_list|)
argument_list|,
name|cosLat
operator|*
name|cosLon
argument_list|,
name|cosLat
operator|*
name|sinLon
argument_list|,
name|sinLat
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a GeoPoint from a latitude/longitude pair.    * @param planetModel is the planetModel to put the point on.    * @param lat is the latitude.    * @param lon is the longitude.    */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|lat
parameter_list|,
specifier|final
name|double
name|lon
parameter_list|)
block|{
name|this
argument_list|(
name|planetModel
argument_list|,
name|Math
operator|.
name|sin
argument_list|(
name|lat
argument_list|)
argument_list|,
name|Math
operator|.
name|sin
argument_list|(
name|lon
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|lat
argument_list|)
argument_list|,
name|Math
operator|.
name|cos
argument_list|(
name|lon
argument_list|)
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a GeoPoint from a unit (x,y,z) vector and a magnitude.    * @param magnitude is the desired magnitude, provided to put the point on the ellipsoid.    * @param x is the unit x value.    * @param y is the unit y value.    * @param z is the unit z value.    * @param lat is the latitude.    * @param lon is the longitude.    */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|double
name|magnitude
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|,
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|)
block|{
name|super
argument_list|(
name|x
operator|*
name|magnitude
argument_list|,
name|y
operator|*
name|magnitude
argument_list|,
name|z
operator|*
name|magnitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|magnitude
operator|=
name|magnitude
expr_stmt|;
if|if
condition|(
name|lat
operator|>
name|Math
operator|.
name|PI
operator|*
literal|0.5
operator|||
name|lat
operator|<
operator|-
name|Math
operator|.
name|PI
operator|*
literal|0.5
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Latitude out of range"
argument_list|)
throw|;
if|if
condition|(
name|lon
argument_list|<
operator|-
name|Math
operator|.
name|PI
operator|||
name|lon
argument_list|>
name|Math
operator|.
name|PI
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Longitude out of range"
argument_list|)
throw|;
name|this
operator|.
name|latitude
operator|=
name|lat
expr_stmt|;
name|this
operator|.
name|longitude
operator|=
name|lon
expr_stmt|;
block|}
comment|/** Construct a GeoPoint from a unit (x,y,z) vector and a magnitude.    * @param magnitude is the desired magnitude, provided to put the point on the ellipsoid.    * @param x is the unit x value.    * @param y is the unit y value.    * @param z is the unit z value.    */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|double
name|magnitude
parameter_list|,
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
name|super
argument_list|(
name|x
operator|*
name|magnitude
argument_list|,
name|y
operator|*
name|magnitude
argument_list|,
name|z
operator|*
name|magnitude
argument_list|)
expr_stmt|;
name|this
operator|.
name|magnitude
operator|=
name|magnitude
expr_stmt|;
block|}
comment|/** Construct a GeoPoint from an (x,y,z) value.    * The (x,y,z) tuple must be on the desired ellipsoid.    * @param x is the ellipsoid point x value.    * @param y is the ellipsoid point y value.    * @param z is the ellipsoid point z value.    */
DECL|method|GeoPoint
specifier|public
name|GeoPoint
parameter_list|(
specifier|final
name|double
name|x
parameter_list|,
specifier|final
name|double
name|y
parameter_list|,
specifier|final
name|double
name|z
parameter_list|)
block|{
name|super
argument_list|(
name|x
argument_list|,
name|y
argument_list|,
name|z
argument_list|)
expr_stmt|;
block|}
comment|/** Compute an arc distance between two points.    * Note: this is an angular distance, and not a surface distance, and is therefore independent of planet model.    * For surface distance, see {@link org.apache.lucene.spatial.spatial4j.geo3d.PlanetModel#surfaceDistance(GeoPoint, GeoPoint)}    * @param v is the second point.    * @return the angle, in radians, between the two points.    */
DECL|method|arcDistance
specifier|public
name|double
name|arcDistance
parameter_list|(
specifier|final
name|GeoPoint
name|v
parameter_list|)
block|{
return|return
name|Tools
operator|.
name|safeAcos
argument_list|(
name|dotProduct
argument_list|(
name|v
argument_list|)
operator|/
operator|(
name|magnitude
argument_list|()
operator|*
name|v
operator|.
name|magnitude
argument_list|()
operator|)
argument_list|)
return|;
block|}
comment|/** Compute the latitude for the point.    * @return the latitude.    */
DECL|method|getLatitude
specifier|public
name|double
name|getLatitude
parameter_list|()
block|{
name|double
name|lat
init|=
name|this
operator|.
name|latitude
decl_stmt|;
comment|//volatile-read once
if|if
condition|(
name|lat
operator|==
name|Double
operator|.
name|NEGATIVE_INFINITY
condition|)
name|this
operator|.
name|latitude
operator|=
name|lat
operator|=
name|Math
operator|.
name|asin
argument_list|(
name|z
operator|/
name|magnitude
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lat
return|;
block|}
comment|/** Compute the longitude for the point.    * @return the longitude value.  Uses 0.0 if there is no computable longitude.    */
DECL|method|getLongitude
specifier|public
name|double
name|getLongitude
parameter_list|()
block|{
name|double
name|lon
init|=
name|this
operator|.
name|longitude
decl_stmt|;
comment|//volatile-read once
if|if
condition|(
name|lon
operator|==
name|Double
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|x
argument_list|)
operator|<
name|MINIMUM_RESOLUTION
operator|&&
name|Math
operator|.
name|abs
argument_list|(
name|y
argument_list|)
operator|<
name|MINIMUM_RESOLUTION
condition|)
name|this
operator|.
name|longitude
operator|=
name|lon
operator|=
literal|0.0
expr_stmt|;
else|else
name|this
operator|.
name|longitude
operator|=
name|lon
operator|=
name|Math
operator|.
name|atan2
argument_list|(
name|y
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
return|return
name|lon
return|;
block|}
comment|/** Compute the linear magnitude of the point.    * @return the magnitude.    */
annotation|@
name|Override
DECL|method|magnitude
specifier|public
name|double
name|magnitude
parameter_list|()
block|{
name|double
name|mag
init|=
name|this
operator|.
name|magnitude
decl_stmt|;
comment|//volatile-read once
if|if
condition|(
name|mag
operator|==
name|Double
operator|.
name|NEGATIVE_INFINITY
condition|)
block|{
name|this
operator|.
name|magnitude
operator|=
name|mag
operator|=
name|super
operator|.
name|magnitude
argument_list|()
expr_stmt|;
block|}
return|return
name|mag
return|;
block|}
block|}
end_class

end_unit

