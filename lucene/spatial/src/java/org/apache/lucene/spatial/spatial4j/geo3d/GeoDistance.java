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
comment|/** Generic geo-distance-capable shape class description.  An implementer * of this interface is capable of computing the described "distance" values, * which are meant to provide both actual distance values, as well as * distance estimates that can be computed more cheaply. */
end_comment

begin_interface
DECL|interface|GeoDistance
specifier|public
interface|interface
name|GeoDistance
extends|extends
name|Membership
block|{
comment|/** Compute this shape's normal "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param point is the point to compute the distance to.      *@return the normal distance, defined as the perpendicular distance from      * from the point to one of the shape's bounding plane.  Normal      * distances can therefore typically only go up to PI/2, except      * when they represent the sum of a sequence of normal distances.       */
DECL|method|computeNormalDistance
specifier|public
name|double
name|computeNormalDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Compute this shape's normal "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param x is the point's unit x coordinate (using U.S. convention).      *@param y is the point's unit y coordinate (using U.S. convention).      *@param z is the point's unit z coordinate (using U.S. convention).      *@return the normal distance, defined as the perpendicular distance from      * from the point to one of the shape's bounding plane.  Normal      * distances can therefore typically only go up to PI/2, except      * when they represent the sum of a sequence of normal distances.      */
DECL|method|computeNormalDistance
specifier|public
name|double
name|computeNormalDistance
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
function_decl|;
comment|/** Compute the square of this shape's normal "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param point is the point to compute the distance to.      *@return the square of the normal distance, defined as the perpendicular      * distance from      * from the point to one of the shape's bounding plane.  Normal      * distances can therefore typically only go up to PI/2, except      * when they represent the sum of a sequence of normal distances.      */
DECL|method|computeSquaredNormalDistance
specifier|public
name|double
name|computeSquaredNormalDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Compute the square of this shape's normal "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param x is the point's unit x coordinate (using U.S. convention).      *@param y is the point's unit y coordinate (using U.S. convention).      *@param z is the point's unit z coordinate (using U.S. convention).      *@return the square of the  normal distance, defined as the perpendicular      * distance from      * from the point to one of the shape's bounding plane.  Normal      * distances can therefore typically only go up to PI/2, except      * when they represent the sum of a sequence of normal distances.      */
DECL|method|computeSquaredNormalDistance
specifier|public
name|double
name|computeSquaredNormalDistance
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
function_decl|;
comment|/** Compute this shape's linear "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param point is the point to compute the distance to.      *@return the linear (or chord) distance, defined as the distance from      * from the point to the nearest point on the unit sphere and on one of the shape's      * bounding planes.  Linear distances can therefore typically go up to PI,      * except when they represent the sum of a sequence of linear distances.      */
DECL|method|computeLinearDistance
specifier|public
name|double
name|computeLinearDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Compute this shape's linear "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param x is the point's unit x coordinate (using U.S. convention).      *@param y is the point's unit y coordinate (using U.S. convention).      *@param z is the point's unit z coordinate (using U.S. convention).      *@return the linear (or chord) distance, defined as the distance from      * from the point to the nearest point on the unit sphere and on one of the shape's      * bounding planes.  Linear distances can therefore typically go up to PI,      * except when they represent the sum of a sequence of linear distances.      */
DECL|method|computeLinearDistance
specifier|public
name|double
name|computeLinearDistance
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
function_decl|;
comment|/** Compute the square of this shape's linear "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param point is the point to compute the distance to.      *@return the square of the linear (or chord) distance, defined as the      * distance from      * from the point to the nearest point on the unit sphere and on one of the shape's      * bounding planes.  Linear distances can therefore typically go up to PI,      * except when they represent the sum of a sequence of linear distances.      */
DECL|method|computeSquaredLinearDistance
specifier|public
name|double
name|computeSquaredLinearDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
function_decl|;
comment|/** Compute the square of this shape's linear "distance" to the GeoPoint.      * A return value of Double.MAX_VALUE should be returned for      * points outside of the shape.      *@param x is the point's unit x coordinate (using U.S. convention).      *@param y is the point's unit y coordinate (using U.S. convention).      *@param z is the point's unit z coordinate (using U.S. convention).      *@return the square of the linear (or chord) distance, defined as the distance from      * from the point to the nearest point on the unit sphere and on one of the shape's      * bounding planes.  Linear distances can therefore typically go up to PI,      * except when they represent the sum of a sequence of linear distances.      */
DECL|method|computeSquaredLinearDistance
specifier|public
name|double
name|computeSquaredLinearDistance
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|,
name|double
name|z
parameter_list|)
function_decl|;
comment|/** Compute a true, accurate, great-circle distance to a point.      * Double.MAX_VALUE indicates a point is outside of the shape.      *@param point is the point.      *@return the distance.      */
DECL|method|computeArcDistance
specifier|public
name|double
name|computeArcDistance
parameter_list|(
name|GeoPoint
name|point
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

