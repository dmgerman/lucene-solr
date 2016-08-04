begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial3d.geom
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial3d
operator|.
name|geom
package|;
end_package

begin_comment
comment|/**  * An object for accumulating XYZ bounds information.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|XYZBounds
specifier|public
class|class
name|XYZBounds
implements|implements
name|Bounds
block|{
comment|/** A 'fudge factor', which is added to maximums and subtracted from minimums,    * in order to compensate for potential error deltas.  This would not be necessary    * except that our 'bounds' is defined as always equaling or exceeding the boundary    * of the shape, and we cannot guarantee that without making MINIMUM_RESOLUTION    * unacceptably large.    * Also, see LUCENE-7290 for a description of how geometry can magnify the bounds delta.    */
DECL|field|FUDGE_FACTOR
specifier|private
specifier|static
specifier|final
name|double
name|FUDGE_FACTOR
init|=
name|Vector
operator|.
name|MINIMUM_RESOLUTION
operator|*
literal|1e3
decl_stmt|;
comment|/** Minimum x */
DECL|field|minX
specifier|private
name|Double
name|minX
init|=
literal|null
decl_stmt|;
comment|/** Maximum x */
DECL|field|maxX
specifier|private
name|Double
name|maxX
init|=
literal|null
decl_stmt|;
comment|/** Minimum y */
DECL|field|minY
specifier|private
name|Double
name|minY
init|=
literal|null
decl_stmt|;
comment|/** Maximum y */
DECL|field|maxY
specifier|private
name|Double
name|maxY
init|=
literal|null
decl_stmt|;
comment|/** Minimum z */
DECL|field|minZ
specifier|private
name|Double
name|minZ
init|=
literal|null
decl_stmt|;
comment|/** Maximum z */
DECL|field|maxZ
specifier|private
name|Double
name|maxZ
init|=
literal|null
decl_stmt|;
comment|/** Set to true if no longitude bounds can be stated */
DECL|field|noLongitudeBound
specifier|private
name|boolean
name|noLongitudeBound
init|=
literal|false
decl_stmt|;
comment|/** Set to true if no top latitude bound can be stated */
DECL|field|noTopLatitudeBound
specifier|private
name|boolean
name|noTopLatitudeBound
init|=
literal|false
decl_stmt|;
comment|/** Set to true if no bottom latitude bound can be stated */
DECL|field|noBottomLatitudeBound
specifier|private
name|boolean
name|noBottomLatitudeBound
init|=
literal|false
decl_stmt|;
comment|/** Construct an empty bounds object */
DECL|method|XYZBounds
specifier|public
name|XYZBounds
parameter_list|()
block|{   }
comment|// Accessor methods
comment|/** Return the minimum X value.    *@return minimum X value.    */
DECL|method|getMinimumX
specifier|public
name|Double
name|getMinimumX
parameter_list|()
block|{
return|return
name|minX
return|;
block|}
comment|/** Return the maximum X value.    *@return maximum X value.    */
DECL|method|getMaximumX
specifier|public
name|Double
name|getMaximumX
parameter_list|()
block|{
return|return
name|maxX
return|;
block|}
comment|/** Return the minimum Y value.    *@return minimum Y value.    */
DECL|method|getMinimumY
specifier|public
name|Double
name|getMinimumY
parameter_list|()
block|{
return|return
name|minY
return|;
block|}
comment|/** Return the maximum Y value.    *@return maximum Y value.    */
DECL|method|getMaximumY
specifier|public
name|Double
name|getMaximumY
parameter_list|()
block|{
return|return
name|maxY
return|;
block|}
comment|/** Return the minimum Z value.    *@return minimum Z value.    */
DECL|method|getMinimumZ
specifier|public
name|Double
name|getMinimumZ
parameter_list|()
block|{
return|return
name|minZ
return|;
block|}
comment|/** Return the maximum Z value.    *@return maximum Z value.    */
DECL|method|getMaximumZ
specifier|public
name|Double
name|getMaximumZ
parameter_list|()
block|{
return|return
name|maxZ
return|;
block|}
comment|/** Return true if minX is as small as the planet model allows.    *@return true if minX has reached its bound.    */
DECL|method|isSmallestMinX
specifier|public
name|boolean
name|isSmallestMinX
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
if|if
condition|(
name|minX
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|minX
operator|-
name|planetModel
operator|.
name|getMinimumXValue
argument_list|()
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Return true if maxX is as large as the planet model allows.    *@return true if maxX has reached its bound.    */
DECL|method|isLargestMaxX
specifier|public
name|boolean
name|isLargestMaxX
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
if|if
condition|(
name|maxX
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|planetModel
operator|.
name|getMaximumXValue
argument_list|()
operator|-
name|maxX
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Return true if minY is as small as the planet model allows.    *@return true if minY has reached its bound.    */
DECL|method|isSmallestMinY
specifier|public
name|boolean
name|isSmallestMinY
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
if|if
condition|(
name|minY
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|minY
operator|-
name|planetModel
operator|.
name|getMinimumYValue
argument_list|()
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Return true if maxY is as large as the planet model allows.    *@return true if maxY has reached its bound.    */
DECL|method|isLargestMaxY
specifier|public
name|boolean
name|isLargestMaxY
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
if|if
condition|(
name|maxY
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|planetModel
operator|.
name|getMaximumYValue
argument_list|()
operator|-
name|maxY
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Return true if minZ is as small as the planet model allows.    *@return true if minZ has reached its bound.    */
DECL|method|isSmallestMinZ
specifier|public
name|boolean
name|isSmallestMinZ
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
if|if
condition|(
name|minZ
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|minZ
operator|-
name|planetModel
operator|.
name|getMinimumZValue
argument_list|()
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|/** Return true if maxZ is as large as the planet model allows.    *@return true if maxZ has reached its bound.    */
DECL|method|isLargestMaxZ
specifier|public
name|boolean
name|isLargestMaxZ
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
if|if
condition|(
name|maxZ
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|planetModel
operator|.
name|getMaximumZValue
argument_list|()
operator|-
name|maxZ
operator|<
name|Vector
operator|.
name|MINIMUM_RESOLUTION
return|;
block|}
comment|// Modification methods
annotation|@
name|Override
DECL|method|addPlane
specifier|public
name|Bounds
name|addPlane
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|Plane
name|plane
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
name|plane
operator|.
name|recordBounds
argument_list|(
name|planetModel
argument_list|,
name|this
argument_list|,
name|bounds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Add a horizontal plane to the bounds description.    * This method should EITHER use the supplied latitude, OR use the supplied    * plane, depending on what is most efficient.    *@param planetModel is the planet model.    *@param latitude is the latitude.    *@param horizontalPlane is the plane.    *@param bounds are the constraints on the plane.    *@return updated Bounds object.    */
DECL|method|addHorizontalPlane
specifier|public
name|Bounds
name|addHorizontalPlane
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|,
specifier|final
name|Plane
name|horizontalPlane
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|addPlane
argument_list|(
name|planetModel
argument_list|,
name|horizontalPlane
argument_list|,
name|bounds
argument_list|)
return|;
block|}
comment|/** Add a vertical plane to the bounds description.    * This method should EITHER use the supplied longitude, OR use the supplied    * plane, depending on what is most efficient.    *@param planetModel is the planet model.    *@param longitude is the longitude.    *@param verticalPlane is the plane.    *@param bounds are the constraints on the plane.    *@return updated Bounds object.    */
DECL|method|addVerticalPlane
specifier|public
name|Bounds
name|addVerticalPlane
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|longitude
parameter_list|,
specifier|final
name|Plane
name|verticalPlane
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|addPlane
argument_list|(
name|planetModel
argument_list|,
name|verticalPlane
argument_list|,
name|bounds
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addXValue
specifier|public
name|Bounds
name|addXValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|addXValue
argument_list|(
name|point
operator|.
name|x
argument_list|)
return|;
block|}
comment|/** Add a specific X value.    * @param x is the value to add.    * @return the bounds object.    */
DECL|method|addXValue
specifier|public
name|Bounds
name|addXValue
parameter_list|(
specifier|final
name|double
name|x
parameter_list|)
block|{
specifier|final
name|double
name|small
init|=
name|x
operator|-
name|FUDGE_FACTOR
decl_stmt|;
if|if
condition|(
name|minX
operator|==
literal|null
operator|||
name|minX
operator|>
name|small
condition|)
block|{
name|minX
operator|=
operator|new
name|Double
argument_list|(
name|small
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|large
init|=
name|x
operator|+
name|FUDGE_FACTOR
decl_stmt|;
if|if
condition|(
name|maxX
operator|==
literal|null
operator|||
name|maxX
operator|<
name|large
condition|)
block|{
name|maxX
operator|=
operator|new
name|Double
argument_list|(
name|large
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|addYValue
specifier|public
name|Bounds
name|addYValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|addYValue
argument_list|(
name|point
operator|.
name|y
argument_list|)
return|;
block|}
comment|/** Add a specific Y value.    * @param y is the value to add.    * @return the bounds object.    */
DECL|method|addYValue
specifier|public
name|Bounds
name|addYValue
parameter_list|(
specifier|final
name|double
name|y
parameter_list|)
block|{
specifier|final
name|double
name|small
init|=
name|y
operator|-
name|FUDGE_FACTOR
decl_stmt|;
if|if
condition|(
name|minY
operator|==
literal|null
operator|||
name|minY
operator|>
name|small
condition|)
block|{
name|minY
operator|=
operator|new
name|Double
argument_list|(
name|small
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|large
init|=
name|y
operator|+
name|FUDGE_FACTOR
decl_stmt|;
if|if
condition|(
name|maxY
operator|==
literal|null
operator|||
name|maxY
operator|<
name|large
condition|)
block|{
name|maxY
operator|=
operator|new
name|Double
argument_list|(
name|large
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|addZValue
specifier|public
name|Bounds
name|addZValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|addZValue
argument_list|(
name|point
operator|.
name|z
argument_list|)
return|;
block|}
comment|/** Add a specific Z value.    * @param z is the value to add.    * @return the bounds object.    */
DECL|method|addZValue
specifier|public
name|Bounds
name|addZValue
parameter_list|(
specifier|final
name|double
name|z
parameter_list|)
block|{
specifier|final
name|double
name|small
init|=
name|z
operator|-
name|FUDGE_FACTOR
decl_stmt|;
if|if
condition|(
name|minZ
operator|==
literal|null
operator|||
name|minZ
operator|>
name|small
condition|)
block|{
name|minZ
operator|=
operator|new
name|Double
argument_list|(
name|small
argument_list|)
expr_stmt|;
block|}
specifier|final
name|double
name|large
init|=
name|z
operator|+
name|FUDGE_FACTOR
decl_stmt|;
if|if
condition|(
name|maxZ
operator|==
literal|null
operator|||
name|maxZ
operator|<
name|large
condition|)
block|{
name|maxZ
operator|=
operator|new
name|Double
argument_list|(
name|large
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|addIntersection
specifier|public
name|Bounds
name|addIntersection
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|Plane
name|plane1
parameter_list|,
specifier|final
name|Plane
name|plane2
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
name|plane1
operator|.
name|recordBounds
argument_list|(
name|planetModel
argument_list|,
name|this
argument_list|,
name|plane2
argument_list|,
name|bounds
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|addPoint
specifier|public
name|Bounds
name|addPoint
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
block|{
return|return
name|addXValue
argument_list|(
name|point
argument_list|)
operator|.
name|addYValue
argument_list|(
name|point
argument_list|)
operator|.
name|addZValue
argument_list|(
name|point
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isWide
specifier|public
name|Bounds
name|isWide
parameter_list|()
block|{
comment|// No specific thing we need to do.
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|noLongitudeBound
specifier|public
name|Bounds
name|noLongitudeBound
parameter_list|()
block|{
comment|// No specific thing we need to do.
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|noTopLatitudeBound
specifier|public
name|Bounds
name|noTopLatitudeBound
parameter_list|()
block|{
comment|// No specific thing we need to do.
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|noBottomLatitudeBound
specifier|public
name|Bounds
name|noBottomLatitudeBound
parameter_list|()
block|{
comment|// No specific thing we need to do.
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|noBound
specifier|public
name|Bounds
name|noBound
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|)
block|{
name|minX
operator|=
name|planetModel
operator|.
name|getMinimumXValue
argument_list|()
expr_stmt|;
name|maxX
operator|=
name|planetModel
operator|.
name|getMaximumXValue
argument_list|()
expr_stmt|;
name|minY
operator|=
name|planetModel
operator|.
name|getMinimumYValue
argument_list|()
expr_stmt|;
name|maxY
operator|=
name|planetModel
operator|.
name|getMaximumYValue
argument_list|()
expr_stmt|;
name|minZ
operator|=
name|planetModel
operator|.
name|getMinimumZValue
argument_list|()
expr_stmt|;
name|maxZ
operator|=
name|planetModel
operator|.
name|getMaximumZValue
argument_list|()
expr_stmt|;
return|return
name|this
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
literal|"XYZBounds: [xmin="
operator|+
name|minX
operator|+
literal|" xmax="
operator|+
name|maxX
operator|+
literal|" ymin="
operator|+
name|minY
operator|+
literal|" ymax="
operator|+
name|maxY
operator|+
literal|" zmin="
operator|+
name|minZ
operator|+
literal|" zmax="
operator|+
name|maxZ
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

