begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.geo3d
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|geo3d
package|;
end_package

begin_comment
comment|/**  * An interface for accumulating bounds information.  * The bounds object is initially empty.  Bounding points  * are then applied by supplying (x,y,z) tuples.  It is also  * possible to indicate the following edge cases:  * (1) No longitude bound possible  * (2) No upper latitude bound possible  * (3) No lower latitude bound possible  * When any of these have been applied, further application of  * points cannot override that decision.  *  * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|Bounds
specifier|public
interface|interface
name|Bounds
block|{
comment|/** Add a general plane to the bounds description.    *@param planetModel is the planet model.    *@param plane is the plane.    *@param bounds are the membership bounds for points along the arc.    */
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
function_decl|;
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
function_decl|;
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
function_decl|;
comment|/** Add a single point.    *@param point is the point.    *@return the updated Bounds object.    */
DECL|method|addPoint
specifier|public
name|Bounds
name|addPoint
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Add an X value.    *@param point is the point to take the x value from.    *@return the updated object.    */
DECL|method|addXValue
specifier|public
name|Bounds
name|addXValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Add a Y value.    *@param point is the point to take the y value from.    *@return the updated object.    */
DECL|method|addYValue
specifier|public
name|Bounds
name|addYValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Add a Z value.    *@param point is the point to take the z value from.    *@return the updated object.    */
DECL|method|addZValue
specifier|public
name|Bounds
name|addZValue
parameter_list|(
specifier|final
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Signal that the shape exceeds Math.PI in longitude.    *@return the updated Bounds object.    */
DECL|method|isWide
specifier|public
name|Bounds
name|isWide
parameter_list|()
function_decl|;
comment|/** Signal that there is no longitude bound.    *@return the updated Bounds object.    */
DECL|method|noLongitudeBound
specifier|public
name|Bounds
name|noLongitudeBound
parameter_list|()
function_decl|;
comment|/** Signal that there is no top latitude bound.    *@return the updated Bounds object.    */
DECL|method|noTopLatitudeBound
specifier|public
name|Bounds
name|noTopLatitudeBound
parameter_list|()
function_decl|;
comment|/** Signal that there is no bottom latitude bound.    *@return the updated Bounds object.    */
DECL|method|noBottomLatitudeBound
specifier|public
name|Bounds
name|noBottomLatitudeBound
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

