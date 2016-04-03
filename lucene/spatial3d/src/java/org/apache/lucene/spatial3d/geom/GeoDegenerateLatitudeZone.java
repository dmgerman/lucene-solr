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
comment|/**  * This GeoBBox represents an area rectangle of one specific latitude with  * no longitude bounds.  *  * @lucene.internal  */
end_comment

begin_class
DECL|class|GeoDegenerateLatitudeZone
class|class
name|GeoDegenerateLatitudeZone
extends|extends
name|GeoBaseBBox
block|{
comment|/** The latitude */
DECL|field|latitude
specifier|protected
specifier|final
name|double
name|latitude
decl_stmt|;
comment|/** Sine of the latitude */
DECL|field|sinLatitude
specifier|protected
specifier|final
name|double
name|sinLatitude
decl_stmt|;
comment|/** Plane describing the latitude zone */
DECL|field|plane
specifier|protected
specifier|final
name|Plane
name|plane
decl_stmt|;
comment|/** A point on the world that's also on the zone */
DECL|field|interiorPoint
specifier|protected
specifier|final
name|GeoPoint
name|interiorPoint
decl_stmt|;
comment|/** An array consisting of the interiorPoint */
DECL|field|edgePoints
specifier|protected
specifier|final
name|GeoPoint
index|[]
name|edgePoints
decl_stmt|;
comment|/** No notable points */
DECL|field|planePoints
specifier|protected
specifier|final
specifier|static
name|GeoPoint
index|[]
name|planePoints
init|=
operator|new
name|GeoPoint
index|[
literal|0
index|]
decl_stmt|;
comment|/** Constructor.    *@param planetModel is the planet model to use.    *@param latitude is the latitude of the latitude zone.    */
DECL|method|GeoDegenerateLatitudeZone
specifier|public
name|GeoDegenerateLatitudeZone
parameter_list|(
specifier|final
name|PlanetModel
name|planetModel
parameter_list|,
specifier|final
name|double
name|latitude
parameter_list|)
block|{
name|super
argument_list|(
name|planetModel
argument_list|)
expr_stmt|;
name|this
operator|.
name|latitude
operator|=
name|latitude
expr_stmt|;
name|this
operator|.
name|sinLatitude
operator|=
name|Math
operator|.
name|sin
argument_list|(
name|latitude
argument_list|)
expr_stmt|;
name|double
name|cosLatitude
init|=
name|Math
operator|.
name|cos
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
name|this
operator|.
name|plane
operator|=
operator|new
name|Plane
argument_list|(
name|planetModel
argument_list|,
name|sinLatitude
argument_list|)
expr_stmt|;
comment|// Compute an interior point.
name|interiorPoint
operator|=
operator|new
name|GeoPoint
argument_list|(
name|planetModel
argument_list|,
name|sinLatitude
argument_list|,
literal|0.0
argument_list|,
name|cosLatitude
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|edgePoints
operator|=
operator|new
name|GeoPoint
index|[]
block|{
name|interiorPoint
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expand
specifier|public
name|GeoBBox
name|expand
parameter_list|(
specifier|final
name|double
name|angle
parameter_list|)
block|{
name|double
name|newTopLat
init|=
name|latitude
operator|+
name|angle
decl_stmt|;
name|double
name|newBottomLat
init|=
name|latitude
operator|-
name|angle
decl_stmt|;
return|return
name|GeoBBoxFactory
operator|.
name|makeGeoBBox
argument_list|(
name|planetModel
argument_list|,
name|newTopLat
argument_list|,
name|newBottomLat
argument_list|,
operator|-
name|Math
operator|.
name|PI
argument_list|,
name|Math
operator|.
name|PI
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isWithin
specifier|public
name|boolean
name|isWithin
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
return|return
name|Math
operator|.
name|abs
argument_list|(
name|z
operator|-
name|this
operator|.
name|sinLatitude
argument_list|)
operator|<
literal|1e-10
return|;
block|}
annotation|@
name|Override
DECL|method|getRadius
specifier|public
name|double
name|getRadius
parameter_list|()
block|{
return|return
name|Math
operator|.
name|PI
return|;
block|}
annotation|@
name|Override
DECL|method|getCenter
specifier|public
name|GeoPoint
name|getCenter
parameter_list|()
block|{
comment|// Totally arbitrary
return|return
name|interiorPoint
return|;
block|}
annotation|@
name|Override
DECL|method|getEdgePoints
specifier|public
name|GeoPoint
index|[]
name|getEdgePoints
parameter_list|()
block|{
return|return
name|edgePoints
return|;
block|}
annotation|@
name|Override
DECL|method|intersects
specifier|public
name|boolean
name|intersects
parameter_list|(
specifier|final
name|Plane
name|p
parameter_list|,
specifier|final
name|GeoPoint
index|[]
name|notablePoints
parameter_list|,
specifier|final
name|Membership
modifier|...
name|bounds
parameter_list|)
block|{
return|return
name|p
operator|.
name|intersects
argument_list|(
name|planetModel
argument_list|,
name|plane
argument_list|,
name|notablePoints
argument_list|,
name|planePoints
argument_list|,
name|bounds
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getBounds
specifier|public
name|void
name|getBounds
parameter_list|(
name|Bounds
name|bounds
parameter_list|)
block|{
name|super
operator|.
name|getBounds
argument_list|(
name|bounds
argument_list|)
expr_stmt|;
name|bounds
operator|.
name|noLongitudeBound
argument_list|()
operator|.
name|addHorizontalPlane
argument_list|(
name|planetModel
argument_list|,
name|latitude
argument_list|,
name|plane
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRelationship
specifier|public
name|int
name|getRelationship
parameter_list|(
specifier|final
name|GeoShape
name|path
parameter_list|)
block|{
comment|// Second, the shortcut of seeing whether endpoints are in/out is not going to
comment|// work with no area endpoints.  So we rely entirely on intersections.
comment|//System.out.println("Got here! latitude="+latitude+" path="+path);
if|if
condition|(
name|path
operator|.
name|intersects
argument_list|(
name|plane
argument_list|,
name|planePoints
argument_list|)
condition|)
block|{
return|return
name|OVERLAPS
return|;
block|}
if|if
condition|(
name|path
operator|.
name|isWithin
argument_list|(
name|interiorPoint
argument_list|)
condition|)
block|{
return|return
name|CONTAINS
return|;
block|}
return|return
name|DISJOINT
return|;
block|}
annotation|@
name|Override
DECL|method|outsideDistance
specifier|protected
name|double
name|outsideDistance
parameter_list|(
specifier|final
name|DistanceStyle
name|distanceStyle
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
return|return
name|distanceStyle
operator|.
name|computeDistance
argument_list|(
name|planetModel
argument_list|,
name|plane
argument_list|,
name|x
argument_list|,
name|y
argument_list|,
name|z
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
operator|!
operator|(
name|o
operator|instanceof
name|GeoDegenerateLatitudeZone
operator|)
condition|)
return|return
literal|false
return|;
name|GeoDegenerateLatitudeZone
name|other
init|=
operator|(
name|GeoDegenerateLatitudeZone
operator|)
name|o
decl_stmt|;
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
operator|&&
name|other
operator|.
name|latitude
operator|==
name|latitude
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
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|latitude
argument_list|)
decl_stmt|;
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
parameter_list|()
block|{
return|return
literal|"GeoDegenerateLatitudeZone: {planetmodel="
operator|+
name|planetModel
operator|+
literal|", lat="
operator|+
name|latitude
operator|+
literal|"("
operator|+
name|latitude
operator|*
literal|180.0
operator|/
name|Math
operator|.
name|PI
operator|+
literal|")}"
return|;
block|}
block|}
end_class

end_unit

